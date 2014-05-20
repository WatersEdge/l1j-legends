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
package l1j.server.server.model;

import java.text.DecimalFormat;
import l1j.server.server.utils.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.Config;
import l1j.server.server.datatables.RaceTicketTable;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.model.identity.L1ItemId;
import l1j.server.server.serverpackets.S_AddItem;
import l1j.server.server.serverpackets.S_CharVisualUpdate;
import l1j.server.server.serverpackets.S_DeleteInventoryItem;
import l1j.server.server.serverpackets.S_ItemColor;
import l1j.server.server.serverpackets.S_ItemStatus;
import l1j.server.server.serverpackets.S_OwnCharStatus;
import l1j.server.server.serverpackets.S_ItemName;
import l1j.server.server.serverpackets.S_ItemAmount;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.storage.CharactersItemStorage;
import l1j.server.server.templates.L1Item;
import l1j.server.server.templates.L1RaceTicket;

public class L1PcInventory extends L1Inventory {

	private static final long serialVersionUID = 1L;

	private static Logger _log = Logger
			.getLogger(L1PcInventory.class.getName());

	private static final int MAX_SIZE = 180;

	private final L1PcInstance _owner; // æ‰€æœ‰è€…ãƒ—ãƒ¬ã‚¤ãƒ¤ãƒ¼

	private int _arrowId; // å„ªå…ˆã�—ã�¦ä½¿ç”¨ã�•ã‚Œã‚‹ã‚¢ãƒ­ãƒ¼ã�®ItemID

	private int _stingId; // å„ªå…ˆã�—ã�¦ä½¿ç”¨ã�•ã‚Œã‚‹ã‚¹ãƒ†ã‚£ãƒ³ã‚°ã�®ItemID

	public L1PcInventory(L1PcInstance owner) {
		_owner = owner;
		_arrowId = 0;
		_stingId = 0;
	}

	public L1PcInstance getOwner() {
		return _owner;
	}

	// åˆ†ç‚º242æ®µçš„é‡�é‡�æ•¸å€¼
	public int getWeight242() {
		return calcWeight242(getWeight());
	}

	// 242éšŽæ®µçš„é‡�é‡�æ•¸å€¼è¨ˆç®—
	public int calcWeight242(int weight) {
		int weight242 = 0;
		if (Config.RATE_WEIGHT_LIMIT != 0) {
			double maxWeight = _owner.getMaxWeight();
			if (weight > maxWeight) {
				weight242 = 242;
			} else {
				double wpTemp = (weight * 100 / maxWeight) * 242.00 / 100.00;
				DecimalFormat df = new DecimalFormat("00.##");
				df.format(wpTemp);
				wpTemp = Math.round(wpTemp);
				weight242 = (int) (wpTemp);
			}
		} else { // ã‚¦ã‚§ã‚¤ãƒˆãƒ¬ãƒ¼ãƒˆã�Œï¼�ã�ªã‚‰é‡�é‡�å¸¸ã�«ï¼�
			weight242 = 0;
		}
		return weight242;
	}

	@Override
	public int checkAddItem(L1ItemInstance item, int count) {
		return checkAddItem(item, count, true);
	}

	public int checkAddItem(L1ItemInstance item, int count, boolean message) {
		if (item == null) {
			return -1;
		}
		if (getSize() > MAX_SIZE
				|| (getSize() == MAX_SIZE && (!item.isStackable() || !checkItem(item
						.getItem().getItemId())))) { // å®¹é‡�ç¢ºèª�
			if (message) {
				sendOverMessage(263); // \f1ä¸€äººã�®ã‚­ãƒ£ãƒ©ã‚¯ã‚¿ãƒ¼ã�ŒæŒ�ã�£ã�¦æ­©ã�‘ã‚‹ã‚¢ã‚¤ãƒ†ãƒ ã�¯æœ€å¤§180å€‹ã�¾ã�§ã�§ã�™ã€‚
			}
			return SIZE_OVER;
		}

		int weight = getWeight() + item.getItem().getWeight() * count / 1000
				+ 1;
		if (weight < 0 || (item.getItem().getWeight() * count / 1000) < 0) {
			if (message) {
				sendOverMessage(82); // æ­¤ç‰©å“�å¤ªé‡�äº†ï¼Œæ‰€ä»¥ä½ ç„¡æ³•æ”œå¸¶ã€‚
			}
			return WEIGHT_OVER;
		}
		if (calcWeight242(weight) >= 242) {
			if (message) {
				sendOverMessage(82); // æ­¤ç‰©å“�å¤ªé‡�äº†ï¼Œæ‰€ä»¥ä½ ç„¡æ³•æ”œå¸¶ã€‚
			}
			return WEIGHT_OVER;
		}

		L1ItemInstance itemExist = findItemId(item.getItemId());
		if (itemExist != null && (itemExist.getCount() + count) > MAX_AMOUNT) {
			if (message) {
				getOwner().sendPackets(
						new S_ServerMessage(166, "æ‰€æŒ�æœ‰çš„é‡‘å¹£",
								"è¶…é�Žäº†2,000,000,000ä¸Šé™�ã€‚")); // \f1%0ã�Œ%4%1%3%2
			}
			return AMOUNT_OVER;
		}

		return OK;
	}

	public void sendOverMessage(int message_id) {
		// é‡£é­šä¸­è² é‡�è¨Šæ�¯è®Šæ›´
		if (_owner.isFishing() && message_id == 82) {
			message_id = 1518; // è² é‡�å¤ªé«˜çš„ç‹€æ…‹ä¸‹ç„¡æ³•é€²è¡Œé‡£é­šã€‚
		}
		_owner.sendPackets(new S_ServerMessage(message_id));
	}

	// è®€å�–è³‡æ–™åº«ä¸­çš„character_itemsè³‡æ–™è¡¨
	@Override
	public void loadItems() {
		try {
			CharactersItemStorage storage = CharactersItemStorage.create();
			
			boolean weaponEquipped = false;

			for (L1ItemInstance item : storage.loadItems(_owner.getId())) {
				_items.add(item);

				if (item.isEquipped()) {
					
					// Quick patch to deal with equipping multiple weapons. Not
					// the right thing to do, but should deal with the problem
					// in the short term.
					if (item.getItem().getType2() == 1) {
						if (weaponEquipped) {
							_log.log(Level.WARNING,"Trying to equip extra weapon during load.");
							item.setEquipped(false);
							continue;
						} else {
							weaponEquipped = true;
						}
					}
					
					item.setEquipped(false);
					setEquipped(item, true, true, false);
				}
				if (item.getItem().getType2() == 0
						&& item.getItem().getType() == 2) { // lightç³»ã‚¢ã‚¤ãƒ†ãƒ 
					item.setRemainingTime(item.getItem().getLightFuel());
				}
				/**
				 * çŽ©å®¶èº«ä¸Šçš„é£Ÿäººå¦–ç²¾RaceTicket é¡¯ç¤ºå ´æ¬¡ã€�å�Šé�¸æ‰‹ç·¨è™Ÿ
				 */
				if (item.getItemId() == 40309) {
					L1RaceTicket ticket = RaceTicketTable.getInstance()
							.getTemplate(item.getId());
					if (ticket != null) {
						L1Item temp = (L1Item) item.getItem().clone();
						String buf = temp.getIdentifiedNameId() + " "
								+ ticket.get_round() + "-"
								+ ticket.get_runner_num();
						temp.setName(buf);
						temp.setUnidentifiedNameId(buf);
						temp.setIdentifiedNameId(buf);
						item.setItem(temp);
					}
				}
				L1World.getInstance().storeObject(item);
			}
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		}
	}

	// å°�è³‡æ–™åº«ä¸­çš„character_itemsè³‡æ–™è¡¨å¯«å…¥
	@Override
	public void insertItem(L1ItemInstance item) {
		_owner.sendPackets(new S_AddItem(item));
		if (item.getItem().getWeight() != 0) {
			_owner.sendPackets(new S_PacketBox(S_PacketBox.WEIGHT,
					getWeight242()));
		}
		try {
			CharactersItemStorage storage = CharactersItemStorage.create();
			storage.storeItem(_owner.getId(), item);
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		}
	}

	public static final int COL_ALL = 0;

	public static final int COL_DURABILITY = 1;

	public static final int COL_IS_ID = 2;

	public static final int COL_ENCHANTLVL = 4;

	public static final int COL_EQUIPPED = 8;

	public static final int COL_COUNT = 16;

	public static final int COL_DELAY_EFFECT = 32;

	public static final int COL_ITEMID = 64;

	public static final int COL_CHARGE_COUNT = 128;

	public static final int COL_REMAINING_TIME = 256;

	public static final int COL_BLESS = 512;

	public static final int COL_ATTR_ENCHANT_KIND = 1024;

	public static final int COL_ATTR_ENCHANT_LEVEL = 2048;

	public static final int COL_ADDHP = 1;

	public static final int COL_ADDMP = 2;

	public static final int COL_HPR = 4;

	public static final int COL_MPR = 8;

	public static final int COL_ADDSP = 16;
	
	public static final int COL_M_DEF = 32;

	public static final int COL_EARTHMR = 64;

	public static final int COL_FIREMR = 128;

	public static final int COL_WATERMR = 256;

	public static final int COL_WINDMR = 512;

	@Override
	public void updateItem(L1ItemInstance item) {
		updateItem(item, COL_COUNT);
		if (item.getItem().isToBeSavedAtOnce()) {
			saveItem(item, COL_COUNT);
		}
	}

	/**
	 * ã‚¤ãƒ³ãƒ™ãƒ³ãƒˆãƒªå†…ã�®ã‚¢ã‚¤ãƒ†ãƒ ã�®çŠ¶æ…‹ã‚’æ›´æ–°ã�™ã‚‹ã€‚
	 * 
	 * @param item
	 *            - æ›´æ–°å¯¾è±¡ã�®ã‚¢ã‚¤ãƒ†ãƒ 
	 * @param column
	 *            - æ›´æ–°ã�™ã‚‹ã‚¹ãƒ†ãƒ¼ã‚¿ã‚¹ã�®ç¨®é¡ž
	 */
	@Override
	public void updateItem(L1ItemInstance item, int column) {
		if (column >= COL_ATTR_ENCHANT_LEVEL) { // å±žæ€§å¼·åŒ–æ•°
			_owner.sendPackets(new S_ItemStatus(item));
			column -= COL_ATTR_ENCHANT_LEVEL;
		}
		if (column >= COL_ATTR_ENCHANT_KIND) { // å±žæ€§å¼·åŒ–ã�®ç¨®é¡ž
			_owner.sendPackets(new S_ItemStatus(item));
			column -= COL_ATTR_ENCHANT_KIND;
		}
		if (column >= COL_BLESS) { // ç¥�ç¦�ãƒ»å°�å�°
			_owner.sendPackets(new S_ItemColor(item));
			column -= COL_BLESS;
		}
		if (column >= COL_REMAINING_TIME) { // ä½¿ç”¨å�¯èƒ½ã�ªæ®‹ã‚Šæ™‚é–“
			_owner.sendPackets(new S_ItemName(item));
			column -= COL_REMAINING_TIME;
		}
		if (column >= COL_CHARGE_COUNT) { // ãƒ�ãƒ£ãƒ¼ã‚¸æ•°
			_owner.sendPackets(new S_ItemName(item));
			column -= COL_CHARGE_COUNT;
		}
		if (column >= COL_ITEMID) { // åˆ¥ã�®ã‚¢ã‚¤ãƒ†ãƒ ã�«ã�ªã‚‹å ´å�ˆ(ä¾¿ç®‹ã‚’é–‹å°�ã�—ã�Ÿã�¨ã��ã�ªã�©)
			_owner.sendPackets(new S_ItemStatus(item));
			_owner.sendPackets(new S_ItemColor(item));
			_owner.sendPackets(new S_PacketBox(S_PacketBox.WEIGHT,
					getWeight242()));
			column -= COL_ITEMID;
		}
		if (column >= COL_DELAY_EFFECT) { // åŠ¹æžœãƒ‡ã‚£ãƒ¬ã‚¤
			column -= COL_DELAY_EFFECT;
		}
		if (column >= COL_COUNT) { // ã‚«ã‚¦ãƒ³ãƒˆ
			_owner.sendPackets(new S_ItemAmount(item));

			int weight = item.getWeight();
			if (weight != item.getLastWeight()) {
				item.setLastWeight(weight);
				_owner.sendPackets(new S_ItemStatus(item));
			} else {
				_owner.sendPackets(new S_ItemName(item));
			}
			if (item.getItem().getWeight() != 0) {
				// XXX 242æ®µéšŽã�®ã‚¦ã‚§ã‚¤ãƒˆã�Œå¤‰åŒ–ã�—ã�ªã�„å ´å�ˆã�¯é€�ã‚‰ã�ªã��ã�¦ã‚ˆã�„
				_owner.sendPackets(new S_PacketBox(S_PacketBox.WEIGHT,
						getWeight242()));
			}
			column -= COL_COUNT;
		}
		if (column >= COL_EQUIPPED) { // è£…å‚™çŠ¶æ…‹
			_owner.sendPackets(new S_ItemName(item));
			column -= COL_EQUIPPED;
		}
		if (column >= COL_ENCHANTLVL) { // ã‚¨ãƒ³ãƒ�ãƒ£ãƒ³ãƒˆ
			_owner.sendPackets(new S_ItemStatus(item));
			column -= COL_ENCHANTLVL;
		}
		if (column >= COL_IS_ID) { // ç¢ºèª�çŠ¶æ…‹
			_owner.sendPackets(new S_ItemStatus(item));
			_owner.sendPackets(new S_ItemColor(item));
			column -= COL_IS_ID;
		}
		if (column >= COL_DURABILITY) { // è€�ä¹…æ€§
			_owner.sendPackets(new S_ItemStatus(item));
			column -= COL_DURABILITY;
		}
	}

	/**
	 * ã‚¤ãƒ³ãƒ™ãƒ³ãƒˆãƒªå†…ã�®ã‚¢ã‚¤ãƒ†ãƒ ã�®çŠ¶æ…‹ã‚’DBã�«ä¿�å­˜ã�™ã‚‹ã€‚
	 * 
	 * @param item
	 *            - æ›´æ–°å¯¾è±¡ã�®ã‚¢ã‚¤ãƒ†ãƒ 
	 * @param column
	 *            - æ›´æ–°ã�™ã‚‹ã‚¹ãƒ†ãƒ¼ã‚¿ã‚¹ã�®ç¨®é¡ž
	 */
	public void saveItem(L1ItemInstance item, int column) {
		if (column == 0) {
			return;
		}

		try {
			CharactersItemStorage storage = CharactersItemStorage.create();
			if (column >= COL_ATTR_ENCHANT_LEVEL) { // å±žæ€§å¼·åŒ–æ•°
				storage.updateItemAttrEnchantLevel(item);
				column -= COL_ATTR_ENCHANT_LEVEL;
			}
			if (column >= COL_ATTR_ENCHANT_KIND) { // å±žæ€§å¼·åŒ–ã�®ç¨®é¡ž
				storage.updateItemAttrEnchantKind(item);
				column -= COL_ATTR_ENCHANT_KIND;
			}
			if (column >= COL_BLESS) { // ç¥�ç¦�ãƒ»å°�å�°
				storage.updateItemBless(item);
				column -= COL_BLESS;
			}
			if (column >= COL_REMAINING_TIME) { // ä½¿ç”¨å�¯èƒ½ã�ªæ®‹ã‚Šæ™‚é–“
				storage.updateItemRemainingTime(item);
				column -= COL_REMAINING_TIME;
			}
			if (column >= COL_CHARGE_COUNT) { // ãƒ�ãƒ£ãƒ¼ã‚¸æ•°
				storage.updateItemChargeCount(item);
				column -= COL_CHARGE_COUNT;
			}
			if (column >= COL_ITEMID) { // åˆ¥ã�®ã‚¢ã‚¤ãƒ†ãƒ ã�«ã�ªã‚‹å ´å�ˆ(ä¾¿ç®‹ã‚’é–‹å°�ã�—ã�Ÿã�¨ã��ã�ªã�©)
				storage.updateItemId(item);
				column -= COL_ITEMID;
			}
			if (column >= COL_DELAY_EFFECT) { // åŠ¹æžœãƒ‡ã‚£ãƒ¬ã‚¤
				storage.updateItemDelayEffect(item);
				column -= COL_DELAY_EFFECT;
			}
			if (column >= COL_COUNT) { // ã‚«ã‚¦ãƒ³ãƒˆ
				storage.updateItemCount(item);
				column -= COL_COUNT;
			}
			if (column >= COL_EQUIPPED) { // è£…å‚™çŠ¶æ…‹
				storage.updateItemEquipped(item);
				column -= COL_EQUIPPED;
			}
			if (column >= COL_ENCHANTLVL) { // ã‚¨ãƒ³ãƒ�ãƒ£ãƒ³ãƒˆ
				storage.updateItemEnchantLevel(item);
				column -= COL_ENCHANTLVL;
			}
			if (column >= COL_IS_ID) { // ç¢ºèª�çŠ¶æ…‹
				storage.updateItemIdentified(item);
				column -= COL_IS_ID;
			}
			if (column >= COL_DURABILITY) { // è€�ä¹…æ€§
				storage.updateItemDurability(item);
				column -= COL_DURABILITY;
			}
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		}
	}

	public void saveEnchantAccessory(L1ItemInstance item, int column) { // é£¾å“�å¼·åŒ–
		if (column == 0) {
			return;
		}

		try {
			CharactersItemStorage storage = CharactersItemStorage.create();
			if (column >= COL_WINDMR) {
				storage.updateWindMr(item);
				column -= COL_WINDMR;
			}
			if (column >= COL_WATERMR) {
				storage.updateWaterMr(item);
				column -= COL_WATERMR;
			}
			if (column >= COL_FIREMR) {
				storage.updateFireMr(item);
				column -= COL_FIREMR;
			}
			if (column >= COL_EARTHMR) {
				storage.updateEarthMr(item);
				column -= COL_EARTHMR;
			}
			if (column >= COL_M_DEF) {
				storage.updateM_Def(item);
				column -= COL_M_DEF;
			}
			if (column >= COL_ADDSP) {
				storage.updateaddSp(item);
				column -= COL_ADDSP;
			}
			if (column >= COL_MPR) {
				storage.updateMpr(item);
				column -= COL_MPR;
			}
			if (column >= COL_HPR) {
				storage.updateHpr(item);
				column -= COL_HPR;
			}
			if (column >= COL_ADDMP) {
				storage.updateaddMp(item);
				column -= COL_ADDMP;
			}
			if (column >= COL_ADDHP) {
				storage.updateaddHp(item);
				column -= COL_ADDHP;
			}
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		}
	}

	// ï¼¤ï¼¢ã�®character_itemsã�‹ã‚‰å‰Šé™¤
	@Override
	public void deleteItem(L1ItemInstance item) {
		try {
			CharactersItemStorage storage = CharactersItemStorage.create();

			storage.deleteItem(item);
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		}
		if (item.isEquipped()) {
			setEquipped(item, false);
		}
		_owner.sendPackets(new S_DeleteInventoryItem(item));
		_items.remove(item);
		if (item.getItem().getWeight() != 0) {
			_owner.sendPackets(new S_PacketBox(S_PacketBox.WEIGHT,
					getWeight242()));
		}
	}

	// ã‚¢ã‚¤ãƒ†ãƒ ã‚’è£…ç�€è„±ç�€ã�•ã�›ã‚‹ï¼ˆL1ItemInstanceã�®å¤‰æ›´ã€�è£œæ­£å€¤ã�®è¨­å®šã€�character_itemsã�®æ›´æ–°ã€�ãƒ‘ã‚±ãƒƒãƒˆé€�ä¿¡ã�¾ã�§ç®¡ç�†ï¼‰
	public void setEquipped(L1ItemInstance item, boolean equipped) {
		setEquipped(item, equipped, false, false);
	}

	public void setEquipped(L1ItemInstance item, boolean equipped,
			boolean loaded, boolean changeWeapon) {
		if (item.isEquipped() != equipped) { // è¨­å®šå€¤ã�¨é�•ã�†å ´å�ˆã� ã�‘å‡¦ç�†
			L1Item temp = item.getItem();
			if (equipped) { // è£…ç�€
				item.setEquipped(true);
				_owner.getEquipSlot().set(item);
				// if fire fire elf switch from melee weapon to bow, remove its brave effect - [Hank]
				if(_owner.getElfAttr() == 2)
				{
					int weaponType = _owner.getWeapon().getItem().getType1();
					if(weaponType == 20)
					{
						this._owner.removeSkillEffect(1000);
					}
							
				}
			} else { // è„±ç�€
				if (!loaded) {
					// ã‚¤ãƒ³ãƒ“ã‚¸ãƒ“ãƒªãƒ†ã‚£ã‚¯ãƒ­ãƒ¼ã‚¯ ãƒ�ãƒ«ãƒ­ã‚°ãƒ–ãƒ©ãƒƒãƒ‡ã‚£ã‚¯ãƒ­ãƒ¼ã‚¯è£…å‚™ä¸­ã�§ã‚¤ãƒ³ãƒ“ã‚¸çŠ¶æ…‹ã�®å ´å�ˆã�¯ã‚¤ãƒ³ãƒ“ã‚¸çŠ¶æ…‹ã�®è§£é™¤
					if (temp.getItemId() == 20077 || temp.getItemId() == 20062
							|| temp.getItemId() == 120077) {
						if (_owner.isInvisble()) {
							_owner.delInvis();
							return;
						}
					}
				}
				item.setEquipped(false);
				_owner.getEquipSlot().remove(item);
			}
			if (!loaded) { // æœ€åˆ�ã�®èª­è¾¼æ™‚ã�¯ï¼¤ï¼¢ãƒ‘ã‚±ãƒƒãƒˆé–¢é€£ã�®å‡¦ç�†ã�¯ã�—ã�ªã�„
				// XXX:æ„�å‘³ã�®ã�ªã�„ã‚»ãƒƒã‚¿ãƒ¼
				_owner.setCurrentHp(_owner.getCurrentHp());
				_owner.setCurrentMp(_owner.getCurrentMp());
				updateItem(item, COL_EQUIPPED);
				_owner.sendPackets(new S_OwnCharStatus(_owner));
				if (temp.getType2() == 1 && changeWeapon == false) { // æ­¦å™¨ã�®å ´å�ˆã�¯ãƒ“ã‚¸ãƒ¥ã‚¢ãƒ«æ›´æ–°ã€‚ã�Ÿã� ã�—ã€�æ­¦å™¨ã�®æŒ�ã�¡æ›¿ã�ˆã�§æ­¦å™¨ã‚’è„±ç�€ã�™ã‚‹æ™‚ã�¯æ›´æ–°ã�—ã�ªã�„
					_owner.sendPackets(new S_CharVisualUpdate(_owner));
					_owner.broadcastPacket(new S_CharVisualUpdate(_owner));
				}
				// _owner.getNetConnection().saveCharToDisk(_owner); //
				// DBã�«ã‚­ãƒ£ãƒ©ã‚¯ã‚¿ãƒ¼æƒ…å ±ã‚’æ›¸ã��è¾¼ã‚€
			}
		}
	}

	// ç‰¹å®šã�®ã‚¢ã‚¤ãƒ†ãƒ ã‚’è£…å‚™ã�—ã�¦ã�„ã‚‹ã�‹ç¢ºèª�
	public boolean checkEquipped(int id) {
		for (Object itemObject : _items) {
			L1ItemInstance item = (L1ItemInstance) itemObject;
			if (item.getItem().getItemId() == id && item.isEquipped()) {
				return true;
			}
		}
		return false;
	}

	// ç‰¹å®šã�®ã‚¢ã‚¤ãƒ†ãƒ ã‚’å…¨ã�¦è£…å‚™ã�—ã�¦ã�„ã‚‹ã�‹ç¢ºèª�ï¼ˆã‚»ãƒƒãƒˆãƒœãƒ¼ãƒŠã‚¹ã�Œã�‚ã‚‹ã‚„ã�¤ã�®ç¢ºèª�ç”¨ï¼‰
	public boolean checkEquipped(int[] ids) {
		for (int id : ids) {
			if (!checkEquipped(id)) {
				return false;
			}
		}
		return true;
	}

	// ç‰¹å®šã�®ã‚¿ã‚¤ãƒ—ã�®ã‚¢ã‚¤ãƒ†ãƒ ã‚’è£…å‚™ã�—ã�¦ã�„ã‚‹æ•°
	public int getTypeEquipped(int type2, int type) {
		int equipeCount = 0;
		for (Object itemObject : _items) {
			L1ItemInstance item = (L1ItemInstance) itemObject;
			if (item.getItem().getType2() == type2
					&& item.getItem().getType() == type && item.isEquipped()) {
				equipeCount++;
			}
		}
		return equipeCount;
	}

	// è£…å‚™ã�—ã�¦ã�„ã‚‹ç‰¹å®šã�®ã‚¿ã‚¤ãƒ—ã�®ã‚¢ã‚¤ãƒ†ãƒ 
	public L1ItemInstance getItemEquipped(int type2, int type) {
		L1ItemInstance equipeitem = null;
		for (Object itemObject : _items) {
			L1ItemInstance item = (L1ItemInstance) itemObject;
			if (item.getItem().getType2() == type2
					&& item.getItem().getType() == type && item.isEquipped()) {
				equipeitem = item;
				break;
			}
		}
		return equipeitem;
	}

	// è£…å‚™ã�—ã�¦ã�„ã‚‹ãƒªãƒ³ã‚°
	public L1ItemInstance[] getRingEquipped() {
		L1ItemInstance equipeItem[] = new L1ItemInstance[2];
		int equipeCount = 0;
		for (Object itemObject : _items) {
			L1ItemInstance item = (L1ItemInstance) itemObject;
			if (item.getItem().getType2() == 2 && item.getItem().getType() == 9
					&& item.isEquipped()) {
				equipeItem[equipeCount] = item;
				equipeCount++;
				if (equipeCount == 2) {
					break;
				}
			}
		}
		return equipeItem;
	}

	// å¤‰èº«æ™‚ã�«è£…å‚™ã�§ã��ã�ªã�„è£…å‚™ã‚’å¤–ã�™
	public void takeoffEquip(int polyid) {
		takeoffWeapon(polyid);
		takeoffArmor(polyid);
	}

	// å¤‰èº«æ™‚ã�«è£…å‚™ã�§ã��ã�ªã�„æ­¦å™¨ã‚’å¤–ã�™
	private void takeoffWeapon(int polyid) {
		if (_owner.getWeapon() == null) { // ç´ æ‰‹
			return;
		}

		boolean takeoff = false;
		int weapon_type = _owner.getWeapon().getItem().getType();
		// è£…å‚™å‡ºæ�¥ã�ªã�„æ­¦å™¨ã‚’è£…å‚™ã�—ã�¦ã‚‹ã�‹ï¼Ÿ
		takeoff = !L1PolyMorph.isEquipableWeapon(polyid, weapon_type);

		if (takeoff) {
			setEquipped(_owner.getWeapon(), false, false, false);
		}
	}

	// å¤‰èº«æ™‚ã�«è£…å‚™ã�§ã��ã�ªã�„é˜²å…·ã‚’å¤–ã�™
	private void takeoffArmor(int polyid) {
		L1ItemInstance armor = null;

		// ãƒ˜ãƒ«ãƒ ã�‹ã‚‰ã‚¬ãƒ¼ãƒ€ãƒ¼ã�¾ã�§ãƒ�ã‚§ãƒƒã‚¯ã�™ã‚‹
		for (int type = 0; type <= 13; type++) {
			// è£…å‚™ã�—ã�¦ã�„ã�¦ã€�è£…å‚™ä¸�å�¯ã�®å ´å�ˆã�¯å¤–ã�™
			if (getTypeEquipped(2, type) != 0
					&& !L1PolyMorph.isEquipableArmor(polyid, type)) {
				if (type == 9) { // ãƒªãƒ³ã‚°ã�®å ´å�ˆã�¯ã€�ä¸¡æ‰‹åˆ†å¤–ã�™
					armor = getItemEquipped(2, type);
					if (armor != null) {
						setEquipped(armor, false, false, false);
					}
					armor = getItemEquipped(2, type);
					if (armor != null) {
						setEquipped(armor, false, false, false);
					}
				} else {
					armor = getItemEquipped(2, type);
					if (armor != null) {
						setEquipped(armor, false, false, false);
					}
				}
			}
		}
	}

	// ä½¿ç”¨ã�™ã‚‹ã‚¢ãƒ­ãƒ¼ã�®å�–å¾—
	public L1ItemInstance getArrow() {
		return getBullet(0);
	}

	// ä½¿ç”¨ã�™ã‚‹ã‚¹ãƒ†ã‚£ãƒ³ã‚°ã�®å�–å¾—
	public L1ItemInstance getSting() {
		return getBullet(15);
	}

	private L1ItemInstance getBullet(int type) {
		L1ItemInstance bullet;
		int priorityId = 0;
		if (type == 0) {
			priorityId = _arrowId; // ã‚¢ãƒ­ãƒ¼
		}
		if (type == 15) {
			priorityId = _stingId; // ã‚¹ãƒ†ã‚£ãƒ³ã‚°
		}
		if (priorityId > 0) // å„ªå…ˆã�™ã‚‹å¼¾ã�Œã�‚ã‚‹ã�‹
		{
			bullet = findItemId(priorityId);
			if (bullet != null) {
				return bullet;
			} else // ã�ªã��ã�ªã�£ã�¦ã�„ã�Ÿå ´å�ˆã�¯å„ªå…ˆã‚’æ¶ˆã�™
			{
				if (type == 0) {
					_arrowId = 0;
				}
				if (type == 15) {
					_stingId = 0;
				}
			}
		}

		for (Object itemObject : _items) // å¼¾ã‚’æŽ¢ã�™
		{
			bullet = (L1ItemInstance) itemObject;
			if (bullet.getItem().getType() == type
					&& bullet.getItem().getType2() == 0) {
				if (type == 0) {
					_arrowId = bullet.getItem().getItemId(); // å„ªå…ˆã�«ã�—ã�¦ã�Šã��
				}
				if (type == 15) {
					_stingId = bullet.getItem().getItemId(); // å„ªå…ˆã�«ã�—ã�¦ã�Šã��
				}
				return bullet;
			}
		}
		return null;
	}

	// å„ªå…ˆã�™ã‚‹ã‚¢ãƒ­ãƒ¼ã�®è¨­å®š
	public void setArrow(int id) {
		_arrowId = id;
	}

	// å„ªå…ˆã�™ã‚‹ã‚¹ãƒ†ã‚£ãƒ³ã‚°ã�®è¨­å®š
	public void setSting(int id) {
		_stingId = id;
	}

	// è£…å‚™ã�«ã‚ˆã‚‹ï¼¨ï¼°è‡ªç„¶å›žå¾©è£œæ­£
	public int hpRegenPerTick() {
		int hpr = 0;
		for (Object itemObject : _items) {
			L1ItemInstance item = (L1ItemInstance) itemObject;
			if (item.isEquipped()) {
				hpr += item.getItem().get_addhpr() + item.getHpr();
			}
		}
		return hpr;
	}

	// è£…å‚™ã�«ã‚ˆã‚‹ï¼­ï¼°è‡ªç„¶å›žå¾©è£œæ­£
	public int mpRegenPerTick() {
		int mpr = 0;
		for (Object itemObject : _items) {
			L1ItemInstance item = (L1ItemInstance) itemObject;
			if (item.isEquipped()) {
				mpr += item.getItem().get_addmpr() + item.getMpr();
			}
		}
		return mpr;
	}

	public L1ItemInstance CaoPenalty() {
		int rnd = Random.nextInt(_items.size());
		L1ItemInstance penaltyItem = _items.get(rnd);
		if (penaltyItem.getItem().getItemId() == L1ItemId.ADENA // ã‚¢ãƒ‡ãƒŠã€�ãƒˆãƒ¬ãƒ¼ãƒ‰ä¸�å�¯ã�®ã‚¢ã‚¤ãƒ†ãƒ ã�¯è�½ã�¨ã�•ã�ªã�„
				|| !penaltyItem.getItem().isTradable()) {
			return null;
		}
		Object[] petlist = _owner.getPetList().values().toArray();
		for (Object petObject : petlist) {
			if (petObject instanceof L1PetInstance) {
				L1PetInstance pet = (L1PetInstance) petObject;
				if (penaltyItem.getId() == pet.getItemObjId()) {
					return null;
				}
			}
		}
		setEquipped(penaltyItem, false);
		return penaltyItem;
	}
}
