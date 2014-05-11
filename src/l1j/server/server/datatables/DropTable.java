/**
 *                            License
 * THE WORK (AS DEFINED BELOW) IS PROVIDED UNDER THE TERMS OF THIS  
 * CREATIVE COMMONS PUBLIC LICENSE ("CCPL" OR "LICENSE"). 
 * THE WORK IS PROTECTED BY COPYRIGHT AND/OR OTHER APPLICABLE LAW.  
 * ANY USE OF THE WORK OTHER THAN AS AUTHORIZED UNDER THIS LICENSE OR  
 * COPYRIGHT LAW IS PROHIBITED.
 * 
 * BY EXERCISING ANY RIGHTS TO THE WORK PROVIDED HERE, YOU ACCEPT AND  
 * AGREE TO BE BOUND BY THE TERMS OF THIS LICENSE. TO THE EXTENT THIS LICENSE  
 * MAY BE CONSIDERED TO BE A CONTRACT, THE LICENSOR GRANTS YOU THE RIGHTS CONTAINED 
 * HERE IN CONSIDERATION OF YOUR ACCEPTANCE OF SUCH TERMS AND CONDITIONS.
 * 
 */
package l1j.server.server.datatables;

import java.util.HashMap;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ArrayList;

import l1j.server.Config;
import l1j.server.L1DatabaseFactory;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1Inventory;
import l1j.server.server.model.L1Quest;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.model.Instance.L1SummonInstance;
import l1j.server.server.model.identity.L1ItemId;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.templates.L1Drop;
import l1j.server.server.utils.Random;
import l1j.server.server.utils.SQLUtil;
import l1j.server.server.utils.collections.Lists;
import l1j.server.server.utils.collections.Maps;

// Referenced classes of package l1j.server.server.templates:
// L1Npc, L1Item, ItemTable

public class DropTable {

	private static final Logger _log = Logger.getLogger(DropTable.class.getName());

	private static DropTable _instance;

	private final Map<Integer, List<L1Drop>> _droplists; // ãƒ¢ãƒ³ã‚¹ã‚¿ãƒ¼æ¯Žã�®ãƒ‰ãƒ­ãƒƒãƒ—ãƒªã‚¹ãƒˆ

	public static DropTable getInstance() {
		if (_instance == null) {
			_instance = new DropTable();
		}
		return _instance;
	}

    public static void reloadTable(){
    	DropTable oldInstance = _instance;
		_instance = new DropTable();
		oldInstance._questDrops.clear();
		oldInstance._droplists.clear();
	}


    private static Map<Integer, String> _questDrops;
    public static final int CLASSID_KNIGHT_MALE = 61;
    public static final int CLASSID_KNIGHT_FEMALE = 48;
    public static final int CLASSID_ELF_MALE = 138;
    public static final int CLASSID_ELF_FEMALE = 37;
    public static final int CLASSID_WIZARD_MALE = 734;
    public static final int CLASSID_WIZARD_FEMALE = 1186;
    public static final int CLASSID_DARK_ELF_MALE = 2786;
    public static final int CLASSID_DARK_ELF_FEMALE = 2796;
    public static final int CLASSID_PRINCE = 0;
    public static final int CLASSID_PRINCESS = 1;

    private DropTable() {
        _droplists = allDropList();
        _questDrops = questDrops();
    }

    private Map<Integer, String> questDrops() {
        Map<Integer, String> questDropsMap = new HashMap<Integer, String>();
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con.prepareStatement("select * from quest_drops");
            rs = pstm.executeQuery();
            while (rs.next()) {
                questDropsMap.put(rs.getInt("item_id"), rs.getString("class"));
            }
        } catch (SQLException e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
        return questDropsMap;
    }

	private Map<Integer, List<L1Drop>> allDropList() {
		Map<Integer, List<L1Drop>> droplistMap = Maps.newMap();

		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("select * from droplist");
			rs = pstm.executeQuery();
			while (rs.next()) {
				int mobId = rs.getInt("mobId");
				int itemId = rs.getInt("itemId");
				int min = rs.getInt("min");
				int max = rs.getInt("max");
				int chance = rs.getInt("chance");

				L1Drop drop = new L1Drop(mobId, itemId, min, max, chance);

				List<L1Drop> dropList = droplistMap.get(drop.getMobid());
				if (dropList == null) {
					dropList = Lists.newList();
					droplistMap.put(new Integer(drop.getMobid()), dropList);
				}
				dropList.add(drop);
			}
		}
		catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		}
		finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		return droplistMap;
	}

	// ã‚¤ãƒ³ãƒ™ãƒ³ãƒˆãƒªã�«ãƒ‰ãƒ­ãƒƒãƒ—ã‚’è¨­å®š
	public void setDrop(L1NpcInstance npc, L1Inventory inventory) {
		try {
		// ãƒ‰ãƒ­ãƒƒãƒ—ãƒªã‚¹ãƒˆã�®å�–å¾—
		int mobId = npc.getNpcTemplate().get_npcId();
		List<L1Drop> dropList = _droplists.get(mobId);
		if (dropList == null) {
			return;
		}

		// ãƒ¬ãƒ¼ãƒˆå�–å¾—
		double droprate = Config.RATE_DROP_ITEMS;
		if (droprate <= 0) {
			droprate = 0;
		}
		double adenarate = Config.RATE_DROP_ADENA;
		if (adenarate <= 0) {
			adenarate = 0;
		}
		if ((droprate <= 0) && (adenarate <= 0)) {
			return;
		}

		int itemId;
		int itemCount;
		int addCount;
		int randomChance;
		L1ItemInstance item;

		for (L1Drop drop : dropList) {
			
				// ãƒ‰ãƒ­ãƒƒãƒ—ã‚¢ã‚¤ãƒ†ãƒ ã�®å�–å¾—
				itemId = drop.getItemid();
				if ((adenarate == 0) && (itemId == L1ItemId.ADENA)) {
					continue; // ã‚¢ãƒ‡ãƒŠãƒ¬ãƒ¼ãƒˆï¼�ã�§ãƒ‰ãƒ­ãƒƒãƒ—ã�Œã‚¢ãƒ‡ãƒŠã�®å ´å�ˆã�¯ã‚¹ãƒ«ãƒ¼
				}
	
				// ãƒ‰ãƒ­ãƒƒãƒ—ãƒ�ãƒ£ãƒ³ã‚¹åˆ¤å®š
				randomChance = Random.nextInt(0xf4240) + 1;
				double rateOfMapId = MapsTable.getInstance().getDropRate(npc.getMapId());
				double rateOfItem = DropItemTable.getInstance().getDropRate(itemId);
				if ((droprate == 0) || (drop.getChance() * droprate * rateOfMapId * rateOfItem < randomChance)) {
					continue;
				}
	
				// ãƒ‰ãƒ­ãƒƒãƒ—å€‹æ•°ã‚’è¨­å®š
				double amount = DropItemTable.getInstance().getDropAmount(itemId);
				int min = (int) (drop.getMin() * amount);
				int max = (int) (drop.getMax() * amount);
	
				itemCount = min;
				addCount = max - min + 1;
				if (addCount > 1) {
					itemCount += Random.nextInt(addCount);
				}
				if (itemId == L1ItemId.ADENA) { // ãƒ‰ãƒ­ãƒƒãƒ—ã�Œã‚¢ãƒ‡ãƒŠã�®å ´å�ˆã�¯ã‚¢ãƒ‡ãƒŠãƒ¬ãƒ¼ãƒˆã‚’æŽ›ã�‘ã‚‹
					itemCount *= adenarate;
				}
				if (itemCount < 0) {
					itemCount = 0;
				}
				if (itemCount > 2000000000) {
					itemCount = 2000000000;
				}
	
				// ã‚¢ã‚¤ãƒ†ãƒ ã�®ç”Ÿæˆ�
				item = ItemTable.getInstance().createItem(itemId);
				item.setCount(itemCount);
	
				// ã‚¢ã‚¤ãƒ†ãƒ æ ¼ç´�
				inventory.storeItem(item);
		}
	}
		catch (Exception e) {
			_log.log(Level.SEVERE, "Error Loading Item From Drop Table", e);
		}
	}

	// ãƒ‰ãƒ­ãƒƒãƒ—ã‚’åˆ†é…�
    public void dropShare(L1NpcInstance npc, ArrayList acquisitorList, ArrayList hateList) {
        L1Inventory inventory = npc.getInventory();
        if (inventory.getSize() == 0) {
            return;
        }
        if (acquisitorList.size() != hateList.size()) {
            return;
        }
        int totalHate = 0;
        L1Character acquisitor;
        for (int i = hateList.size() - 1; i >= 0; i--) {
            acquisitor = (L1Character) acquisitorList.get(i);
            if ((Config.AUTO_LOOT == 2)
                    && (acquisitor instanceof L1SummonInstance || acquisitor instanceof L1PetInstance)) {
                acquisitorList.remove(i);
                hateList.remove(i);
            } else if (acquisitor != null
                    && !acquisitor.isDead() // added
                    && acquisitor.getMapId() == npc.getMapId()
                    && acquisitor.getLocation().getTileLineDistance(
                    npc.getLocation()) <= Config.LOOTING_RANGE) {
                totalHate += (Integer) hateList.get(i);
            } else {
                acquisitorList.remove(i);
                hateList.remove(i);
            }
        }
        L1ItemInstance item;
        L1Inventory targetInventory = null;
        L1PcInstance player;
        L1PcInstance[] partyMember;
        Random random = new Random();
        int randomInt;
        int chanceHate;
        int itemId;
        for (int i = inventory.getSize(); i > 0; i--) {
            item = inventory.getItems().get(0);
            itemId = item.getItemId();
            boolean isGround = false;
            if (item.getItem().getType2() == 0 && item.getItem().getType() == 2) {
                item.setNowLighting(false);
            }
            item.setIdentified(false); // changed
            if (((Config.AUTO_LOOT != 0) || itemId == L1ItemId.ADENA) && totalHate > 0) {
                randomInt = random.nextInt(totalHate);
                chanceHate = 0;
                for (int j = hateList.size() - 1; j >= 0; j--) {
                    chanceHate += (Integer) hateList.get(j);
                    if (chanceHate > randomInt) {
                        acquisitor = (L1Character) acquisitorList.get(j);
                        if (itemId >= 40131 && itemId <= 40135) {
                            if (!(acquisitor instanceof L1PcInstance) || hateList.size() > 1) {
                                targetInventory = null;
                                break;
                            }
                            player = (L1PcInstance) acquisitor;
                            if (player.getQuest().get_step(L1Quest.QUEST_LYRA) != 1) {
                                targetInventory = null;
                                break;
                            }
                        }
                        if (acquisitor.getInventory().checkAddItem(item, item.getCount()) == L1Inventory.OK) {
                            targetInventory = acquisitor.getInventory();
                            if (acquisitor instanceof L1PcInstance) {
                                player = (L1PcInstance) acquisitor;
                                // added to exclude quest drops from invalid classes
                                /*
                                if(_questDrops.containsKey(item.getItemId())) {
                                    if(!classCode(player).equals(_questDrops.get(item.getItemId()))) {
                                        inventory.deleteItem(item);
                                        break;
                                    }
                                }*/
                                L1ItemInstance l1iteminstance = player
                                        .getInventory().findItemId(
                                                L1ItemId.ADENA);
                                if (l1iteminstance != null
                                        && l1iteminstance.getCount() > 2000000000) {
                                    targetInventory = L1World.getInstance()
                                            .getInventory(acquisitor.getX(),
                                                    acquisitor.getY(),
                                                    acquisitor.getMapId());
                                    isGround = true;
                                    player.sendPackets(new S_ServerMessage(166,
                                            "The limit of the itemcount is 2000000000"));
                                } else {
                                    if (player.isInParty()) {
                                        partyMember = player.getParty().getMembers();
                                        for (int p = 0; p < partyMember.length; p++) {
                                            L1PcInstance pc = partyMember[p];
                                            if(pc.getPartyDropMessages())
                                            {
                                                partyMember[p].sendPackets(new S_ServerMessage(813, npc.getName(),item.getLogName(),player.getName()));
                                            }
                                        }
                                    } else {
                                        if (player.getDropMessages())
                                        {
                                            player.sendPackets(new S_ServerMessage(143, npc.getName(), item.getLogName()));}
                                    }
                                }
                            }
                        } else {
                            targetInventory = L1World.getInstance()
                                    .getInventory(acquisitor.getX(),
                                            acquisitor.getY(),
                                            acquisitor.getMapId());
                            isGround = true;
                        }
                        break;
                    }
                }
            } else {
                List<Integer> dirList = new ArrayList<Integer>();
                for (int j = 0; j < 8; j++) {
                    dirList.add(j);
                }
                int x = 0;
                int y = 0;
                int dir = 0;
                do {
                    if (dirList.size() == 0) {
                        x = 0;
                        y = 0;
                        break;
                    }
                    randomInt = random.nextInt(dirList.size());
                    dir = dirList.get(randomInt);
                    dirList.remove(randomInt);
                    switch (dir) {
                        case 0:
                            x = 0;
                            y = -1;
                            break;
                        case 1:
                            x = 1;
                            y = -1;
                            break;
                        case 2:
                            x = 1;
                            y = 0;
                            break;
                        case 3:
                            x = 1;
                            y = 1;
                            break;
                        case 4:
                            x = 0;
                            y = 1;
                            break;
                        case 5:
                            x = -1;
                            y = 1;
                            break;
                        case 6:
                            x = -1;
                            y = 0;
                            break;
                        case 7:
                            x = -1;
                            y = -1;
                            break;
                    }
                } while (!npc.getMap().isPassable(npc.getX(), npc.getY(), dir));
                targetInventory = L1World.getInstance().getInventory(
                        npc.getX() + x, npc.getY() + y, npc.getMapId());
                isGround = true;
            }
            if (itemId >= 40131 && itemId <= 40135) {
                if (isGround || targetInventory == null) {
                    inventory.removeItem(item, item.getCount());
                    continue;
                }
            }
            if(item != null) {
                inventory.tradeItem(item, item.getCount(), targetInventory);
            }
        }
        npc.turnOnOffLight();
    }


    public List<L1Drop> getDrops(int mobID) {//New for GMCommands
		return _droplists.get(mobID);
	}

    private String classCode(L1PcInstance pc) {
        int i = pc.getClassId();
        if(i == CLASSID_KNIGHT_MALE || i == CLASSID_KNIGHT_FEMALE) {
            return "K";
        } else if(i == CLASSID_ELF_MALE || i == CLASSID_ELF_FEMALE) {
            return "E";
        } else if(i == CLASSID_WIZARD_MALE || i == CLASSID_WIZARD_FEMALE) {
            return "W";
        } else if(i == CLASSID_DARK_ELF_MALE || i == CLASSID_DARK_ELF_FEMALE) {
            return "D";
        } else if(i == CLASSID_PRINCE || i == CLASSID_PRINCESS) {
            return "P";
        } else {
            return null;
        }
    }

}