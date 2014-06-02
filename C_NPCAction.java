/**
 * License THE WORK (AS DEFINED BELOW) IS PROVIDED UNDER THE TERMS OF THIS
 * CREATIVE COMMONS PUBLIC LICENSE ("CCPL" OR "LICENSE"). THE WORK IS PROTECTED
 * BY COPYRIGHT AND/OR OTHER APPLICABLE LAW. ANY USE OF THE WORK OTHER THAN AS
 * AUTHORIZED UNDER THIS LICENSE OR COPYRIGHT LAW IS PROHIBITED.
 * 
 * BY EXERCISING ANY RIGHTS TO THE WORK PROVIDED HERE, YOU ACCEPT AND AGREE TO
 * BE BOUND BY THE TERMS OF THIS LICENSE. TO THE EXTENT THIS LICENSE MAY BE
 * CONSIDERED TO BE A CONTRACT, THE LICENSOR GRANTS YOU THE RIGHTS CONTAINED
 * HERE IN CONSIDERATION OF YOUR ACCEPTANCE OF SUCH TERMS AND CONDITIONS.
 * 
 */

package l1j.server.server.clientpackets;

import static l1j.server.server.model.skill.L1SkillId.AWAKEN_ANTHARAS;
import static l1j.server.server.model.skill.L1SkillId.AWAKEN_FAFURION;
import static l1j.server.server.model.skill.L1SkillId.AWAKEN_VALAKAS;
import static l1j.server.server.model.skill.L1SkillId.BLESSED_ARMOR;
import static l1j.server.server.model.skill.L1SkillId.CANCELLATION;
import static l1j.server.server.model.skill.L1SkillId.EFFECT_BLESS_OF_CRAY;
import static l1j.server.server.model.skill.L1SkillId.EFFECT_BLESS_OF_SAELL;
import static l1j.server.server.model.skill.L1SkillId.ELEMENTAL_PROTECTION;
import static l1j.server.server.model.skill.L1SkillId.ENCHANT_WEAPON;
import static l1j.server.server.model.skill.L1SkillId.SHAPE_CHANGE;
import static l1j.server.server.model.skill.L1SkillId.STATUS_CURSE_BARLOG;
import static l1j.server.server.model.skill.L1SkillId.STATUS_CURSE_YAHEE;
import static l1j.server.server.model.skill.L1SkillId.STATUS_HASTE;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.Config;
import l1j.server.server.ClientThread;
import l1j.server.server.HomeTownTimeController;
import l1j.server.server.WarTimeController;
import l1j.server.server.controllers.CrackOfTimeController;
import l1j.server.server.datatables.CastleTable;
import l1j.server.server.datatables.DoorTable;
import l1j.server.server.datatables.ExpTable;
import l1j.server.server.datatables.HouseTable;
import l1j.server.server.datatables.InnKeyTable;
import l1j.server.server.datatables.InnTable;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.datatables.NpcActionTable;
import l1j.server.server.datatables.NpcTable;
import l1j.server.server.datatables.PetTable;
import l1j.server.server.datatables.PolyTable;
import l1j.server.server.datatables.SkillsTable;
import l1j.server.server.datatables.TownTable;
import l1j.server.server.datatables.UBTable;
import l1j.server.server.model.L1CastleLocation;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1HauntedHouse;
import l1j.server.server.model.L1HouseLocation;
import l1j.server.server.model.L1Location;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1PcInventory;
import l1j.server.server.model.L1PetMatch;
import l1j.server.server.model.L1PolyMorph;
import l1j.server.server.model.L1Quest;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1TownLocation;
import l1j.server.server.model.L1UltimateBattle;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1DoorInstance;
import l1j.server.server.model.Instance.L1HousekeeperInstance;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1MerchantInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.model.Instance.L1SummonInstance;
import l1j.server.server.model.game.L1PolyRace;
import l1j.server.server.model.identity.L1ItemId;
import l1j.server.server.model.identity.L1MiscId;
import l1j.server.server.model.npc.L1NpcHtml;
import l1j.server.server.model.npc.action.L1NpcAction;
import l1j.server.server.model.skill.L1BuffUtil;
import l1j.server.server.model.skill.L1SkillUse;
import l1j.server.server.serverpackets.S_ApplyAuction;
import l1j.server.server.serverpackets.S_AuctionBoardRead;
import l1j.server.server.serverpackets.S_CharReset;
import l1j.server.server.serverpackets.S_CloseList;
import l1j.server.server.serverpackets.S_DelSkill;
import l1j.server.server.serverpackets.S_Deposit;
import l1j.server.server.serverpackets.S_Drawal;
import l1j.server.server.serverpackets.S_HPUpdate;
import l1j.server.server.serverpackets.S_HouseMap;
import l1j.server.server.serverpackets.S_HowManyKey;
import l1j.server.server.serverpackets.S_ItemName;
import l1j.server.server.serverpackets.S_MPUpdate;
import l1j.server.server.serverpackets.S_Message_YN;
import l1j.server.server.serverpackets.S_NPCTalkReturn;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_PetCtrlMenu;
import l1j.server.server.serverpackets.S_PetList;
import l1j.server.server.serverpackets.S_RetrieveElfList;
import l1j.server.server.serverpackets.S_RetrieveList;
import l1j.server.server.serverpackets.S_RetrievePledgeList;
import l1j.server.server.serverpackets.S_SelectTarget;
import l1j.server.server.serverpackets.S_SellHouse;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_ShopBuyList;
import l1j.server.server.serverpackets.S_ShopSellList;
import l1j.server.server.serverpackets.S_SkillHaste;
import l1j.server.server.serverpackets.S_SkillIconAura;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.serverpackets.S_TaxRate;
import l1j.server.server.templates.L1Castle;
import l1j.server.server.templates.L1House;
import l1j.server.server.templates.L1Inn;
import l1j.server.server.templates.L1Item;
import l1j.server.server.templates.L1Npc;
import l1j.server.server.templates.L1Skills;
import l1j.server.server.templates.L1Town;
import l1j.server.server.utils.Random;
import static l1j.server.server.model.skill.L1SkillId.*;

/**
 * TODO: ç¿»è­¯ï¼Œå¥½å¤š è™•ç�†æ”¶åˆ°ç”±å®¢æˆ¶ç«¯å‚³ä¾†NPCå‹•ä½œçš„å°�åŒ…
 */
public class C_NPCAction extends ClientBasePacket {

	private static final String C_NPC_ACTION = "[C] C_NPCAction";

	private static Logger _log = Logger.getLogger(C_NPCAction.class.getName());

	public C_NPCAction(byte abyte0[], ClientThread client) throws Exception {
		super(abyte0);
		
		L1PcInstance pc = client.getActiveChar();
		if (pc == null) {
			return;
		}
		
		int objid = readD();
		String s = readS();

		String s2 = null;
		if (s.equalsIgnoreCase("select") // æ‹�è³£å…¬å‘Šæ�¿çš„é�¸æ“‡
				|| s.equalsIgnoreCase("map") // åœ°åœ–ä½�ç½®çš„ç¢ºèª�
				|| s.equalsIgnoreCase("apply")) { // å�ƒåŠ æ‹�è³£
			s2 = readS();
		} else if (s.equalsIgnoreCase("ent")) {
			L1Object obj = L1World.getInstance().findObject(objid);
			if ((obj != null) && (obj instanceof L1NpcInstance)) {
				if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80088) {
//					s2 = readS();
				}
			}
		}

		int[] materials = null;
		int[] counts = null;
		int[] createitem = null;
		int[] createcount = null;

		String htmlid = null;
		String success_htmlid = null;
		String failure_htmlid = null;
		String[] htmldata = null;

		int questid = 0;
		int questvalue = 0;
		int contribution = 0;
		
		L1PcInstance target;
		L1Object obj = L1World.getInstance().findObject(objid);
		if (obj != null) {
			if (obj instanceof L1NpcInstance) {
				L1NpcInstance npc = (L1NpcInstance) obj;
				int difflocx = Math.abs(pc.getX() - npc.getX());
				int difflocy = Math.abs(pc.getY() - npc.getY());
				if (!(obj instanceof L1PetInstance)
						&& !(obj instanceof L1SummonInstance)) {
					if ((difflocx > 3) || (difflocy > 3)) { // 3æ ¼ä»¥ä¸Šçš„è·�é›¢å°�è©±ç„¡æ•ˆ
						return;
					}
				}
				npc.onFinalAction(pc, s);
			} else if (obj instanceof L1PcInstance) {
				target = (L1PcInstance) obj;
				if (s.matches("[0-9]+")) {
					if (target.isSummonMonster()) {
						summonMonster(target, s);
						target.setSummonMonster(false);
					}
				} else {

					if (target.isShapeChange()) {
						L1PolyMorph.handleCommands(target, s);
						target.setShapeChange(false);
					} else {
						L1PolyMorph poly = PolyTable.getInstance().getTemplate(
								s);
						if ((poly != null) || s.equals("none")) {
							if (target.getInventory().checkItem(40088)
									&& usePolyScroll(target, 40088, s)) {
							}
							if (target.getInventory().checkItem(40096)
									&& usePolyScroll(target, 40096, s)) {
							}
							if (target.getInventory().checkItem(140088)
									&& usePolyScroll(target, 140088, s)) {
							}
						}
					}
				}
				return;
			}
		} else {
			// _log.warning("object not found, oid " + i);
		}

		// XMLåŒ–ã�•ã‚Œã�Ÿã‚¢ã‚¯ã‚·ãƒ§ãƒ³
		L1NpcAction action = NpcActionTable.getInstance().get(s, pc, obj);
		if (action != null) {
			L1NpcHtml result = action.execute(s, pc, obj, readByte());
			if (result != null) {
				pc.sendPackets(new S_NPCTalkReturn(obj.getId(), result));
			}
			return;
		}

		/*
		 * å€‹åˆ¥è™•ç�†è¡Œå‹•
		 */
		if (s.equalsIgnoreCase("buy")) {
			L1NpcInstance npc = (L1NpcInstance) obj;
			// sell æ‡‰è©²æŒ‡çµ¦ NPC æª¢æŸ¥
			if (isNpcSellOnly(npc)) {
				return;
			}

			// è²©è³£æ¸…å–®
			pc.sendPackets(new S_ShopSellList(objid, pc));
		} else if (s.equalsIgnoreCase("sell")) {
			int npcid = ((L1NpcInstance) obj).getNpcTemplate().get_npcId();
			if ((npcid == 70523) || (npcid == 70805)) { // ãƒ©ãƒ€ãƒ¼ or ã‚¸ãƒ¥ãƒªãƒ¼
				htmlid = "ladar2";
			} else if ((npcid == 70537) || (npcid == 70807)) { // ãƒ•ã‚¡ãƒ¼ãƒªãƒ³ or ãƒ•ã‚£ãƒ³
				htmlid = "farlin2";
			} else if ((npcid == 70525) || (npcid == 70804)) { // ãƒ©ã‚¤ã‚¢ãƒ³ or ã‚¸ãƒ§ã‚¨ãƒ«
				htmlid = "lien2";
			} else if ((npcid == 50527) || (npcid == 50505) || (npcid == 50519)
					|| (npcid == 50545) || (npcid == 50531) || (npcid == 50529)
					|| (npcid == 50516) || (npcid == 50538) || (npcid == 50518)
					|| (npcid == 50509) || (npcid == 50536) || (npcid == 50520)
					|| (npcid == 50543) || (npcid == 50526) || (npcid == 50512)
					|| (npcid == 50510) || (npcid == 50504) || (npcid == 50525)
					|| (npcid == 50534) || (npcid == 50540) || (npcid == 50515)
					|| (npcid == 50513) || (npcid == 50528) || (npcid == 50533)
					|| (npcid == 50542) || (npcid == 50511) || (npcid == 50501)
					|| (npcid == 50503) || (npcid == 50508) || (npcid == 50514)
					|| (npcid == 50532) || (npcid == 50544) || (npcid == 50524)
					|| (npcid == 50535) || (npcid == 50521) || (npcid == 50517)
					|| (npcid == 50537) || (npcid == 50539) || (npcid == 50507)
					|| (npcid == 50530) || (npcid == 50502) || (npcid == 50506)
					|| (npcid == 50522) || (npcid == 50541) || (npcid == 50523)
					|| (npcid == 50620) || (npcid == 50623) || (npcid == 50619)
					|| (npcid == 50621) || (npcid == 50622) || (npcid == 50624)
					|| (npcid == 50617) || (npcid == 50614) || (npcid == 50618)
					|| (npcid == 50616) || (npcid == 50615) || (npcid == 50626)
					|| (npcid == 50627) || (npcid == 50628) || (npcid == 50629)
					|| (npcid == 50630) || (npcid == 50631)) { // ã‚¢ã‚¸ãƒˆã�®NPC
				String sellHouseMessage = sellHouse(pc, objid, npcid);
				if (sellHouseMessage != null) {
					htmlid = sellHouseMessage;
				}
			} else { // ä¸€èˆ¬å•†äºº

				// å�¯ä»¥è²·çš„ç‰©å“�æ¸…å–®
				pc.sendPackets(new S_ShopBuyList(objid, pc));
			}
		} else if ((((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 91002 // å¯µç‰©ç«¶é€ŸNPCçš„ç·¨è™Ÿ
				)
				&& s.equalsIgnoreCase("ent")) {
			L1PolyRace.getInstance().enterGame(pc);
		} else if (s.equalsIgnoreCase("retrieve")) { // ã€Œå€‹äººå€‰åº«ï¼šé ˜å�–ç‰©å“�ã€�
			if (pc.getLevel() >= 5) {
				if (client.getAccount().getWarePassword() > 0) {
					pc.sendPackets(new S_ServerMessage(834));
				} else {
					pc.sendPackets(new S_RetrieveList(objid, pc));
				}
			}
		} else if (s.equalsIgnoreCase("retrieve-elven")) { // ã€Œå¦–ç²¾å€‰åº«ï¼šé ˜å�–ç‰©å“�ã€�
			if ((pc.getLevel() >= 5) && pc.isElf()) {
				if (pc.isElf() && (pc.getLevel() > 4)) {
					if (client.getAccount().getWarePassword() > 0) {
						pc.sendPackets(new S_ServerMessage(834));
					} else {
						pc.sendPackets(new S_RetrieveElfList(objid, pc));
					}
				}
			}
		} else if (s.equalsIgnoreCase("retrieve-pledge")) { // ã€Œè¡€ç›Ÿå€‰åº«ï¼šé ˜å�–ç‰©å“�ã€�
			if (pc.getLevel() >= 5) {
				if (pc.getClanid() == 0) {
					// \f1è¡€ç›Ÿå€‰åº«ã‚’ä½¿ç”¨ã�™ã‚‹ã�«ã�¯è¡€ç›Ÿã�«åŠ å…¥ã�—ã�¦ã�„ã�ªã��ã�¦ã�¯ã�ªã‚Šã�¾ã�›ã‚“ã€‚
					pc.sendPackets(new S_ServerMessage(208));
					return;
				}
				int rank = pc.getClanRank();
				if ((rank != L1Clan.CLAN_RANK_PUBLIC)
						&& (rank != L1Clan.CLAN_RANK_GUARDIAN)
						&& (rank != L1Clan.CLAN_RANK_PRINCE)) {
					// ã‚¿ã‚¤ãƒˆãƒ«ã�®ã�ªã�„è¡€ç›Ÿå“¡ã‚‚ã�—ã��ã�¯ã€�è¦‹ç¿’ã�„è¡€ç›Ÿå“¡ã�®å ´å�ˆã�¯ã€�è¡€ç›Ÿå€‰åº«ã‚’åˆ©ç”¨ã�™ã‚‹ã�“ã�¨ã�¯ã�§ã��ã�¾ã�›ã‚“ã€‚
					pc.sendPackets(new S_ServerMessage(728));
					return;
				}
				if ((rank != L1Clan.CLAN_RANK_PRINCE)
						&& pc.getTitle().equalsIgnoreCase("")) {
					// ã‚¿ã‚¤ãƒˆãƒ«ã�®ã�ªã�„è¡€ç›Ÿå“¡ã‚‚ã�—ã��ã�¯ã€�è¦‹ç¿’ã�„è¡€ç›Ÿå“¡ã�®å ´å�ˆã�¯ã€�è¡€ç›Ÿå€‰åº«ã‚’åˆ©ç”¨ã�™ã‚‹ã�“ã�¨ã�¯ã�§ã��ã�¾ã�›ã‚“ã€‚
					pc.sendPackets(new S_ServerMessage(728));
					return;
				}
				if (client.getAccount().getWarePassword() > 0) {
					pc.sendPackets(new S_ServerMessage(834));
				} else {
					pc.sendPackets(new S_RetrievePledgeList(objid, pc));
				}
			}
		} else if (s.equalsIgnoreCase("get")) {
			L1NpcInstance npc = (L1NpcInstance) obj;
			int npcId = npc.getNpcTemplate().get_npcId();
			// ã‚¯ãƒ¼ãƒ‘ãƒ¼ or ãƒ€ãƒ³ãƒ�ãƒ 
			if ((npcId == 70099) || (npcId == 70796)) {
				L1ItemInstance item = pc.getInventory().storeItem(20081, 1); // ã‚ªã‚¤ãƒ«ã‚¹ã‚­ãƒ³ãƒžãƒ³ãƒˆ
				String npcName = npc.getNpcTemplate().get_name();
				String itemName = item.getItem().getName();
				pc.sendPackets(new S_ServerMessage(143, npcName, itemName)); // \f1%0ã�Œ%1ã‚’ã��ã‚Œã�¾ã�—ã�Ÿã€‚
				pc.getQuest().set_end(L1Quest.QUEST_OILSKINMANT);
				htmlid = ""; // ã‚¦ã‚£ãƒ³ãƒ‰ã‚¦ã‚’æ¶ˆã�™
			}
			// HomeTown æ�‘èŽŠç®¡ç�†äºº æ”¯ä»˜ç¦�åˆ©é‡‘
			else if ((npcId == 70528) || (npcId == 70546) || (npcId == 70567)
					|| (npcId == 70594) || (npcId == 70654) || (npcId == 70748)
					|| (npcId == 70774) || (npcId == 70799) || (npcId == 70815)
					|| (npcId == 70860)) {

				int townId = pc.getHomeTownId();
				int pay = pc.getPay();
				int cb = pc.getContribution(); // è²¢ç�»åº¦
				htmlid = "";
				if (pay < 1) {
					pc.sendPackets(new S_ServerMessage(767));// æ²’æœ‰æ�‘èŽŠæ”¯æ�´è²»ï¼Œè«‹åœ¨ä¸‹å€‹æœˆå†�ä¾†ã€‚
				} else if ((pay > 0) && (cb < 500)) {
					pc.sendPackets(new S_ServerMessage(766));// è²¢ç�»åº¦ä¸�è¶³è€Œç„¡æ³•å¾—åˆ°è£œå„Ÿé‡‘
				} else if (townId > 0) {
					double payBonus = 1.0; // cb > 499 && cb < 1000
					boolean isLeader = TownTable.getInstance().isLeader(pc,
							townId); // æ�‘é•·
					L1ItemInstance item = pc.getInventory().findItemId(
							L1ItemId.ADENA);
					if ((cb > 999) && (cb < 1500)) {
						payBonus = 1.5;
					} else if ((cb > 1499) && (cb < 2000)) {
						payBonus = 2.0;
					} else if ((cb > 1999) && (cb < 2500)) {
						payBonus = 2.5;
					} else if ((cb > 2499) && (cb < 3000)) {
						payBonus = 3.0;
					} else if (cb > 2999) {
						payBonus = 4.0;
					}
					if (isLeader) {
						payBonus++;
					}
					if ((item != null)
							&& (item.getCount() + pay * payBonus > 2000000000)) {
						pc.sendPackets(new S_ServerMessage(166,"æ‰€æŒ�æœ‰çš„é‡‘å¹£è¶…é�Ž2,000,000,000ã€‚"));
						htmlid = "";
					} else if ((item != null)
							&& (item.getCount() + pay * payBonus < 2000000001)) {
						pay = (int) (HomeTownTimeController.getPay(pc.getId()) * payBonus);
						pc.getInventory().storeItem(L1ItemId.ADENA, pay);
						pc.sendPackets(new S_ServerMessage(761, "" + pay));
						pc.setPay(0);
					}
				}
			}
		} else if (s.equalsIgnoreCase("townscore")) {// ç¢ºèª�ç›®å‰�è²¢ç�»åº¦
			L1NpcInstance npc = (L1NpcInstance) obj;
			int npcId = npc.getNpcTemplate().get_npcId();
			if ((npcId == 70528) || (npcId == 70546) || (npcId == 70567)
					|| (npcId == 70594) || (npcId == 70654) || (npcId == 70748)
					|| (npcId == 70774) || (npcId == 70799) || (npcId == 70815)
					|| (npcId == 70860)) {
				if (pc.getHomeTownId() > 0) {
					pc.sendPackets(new S_ServerMessage(1569, String.valueOf(pc
							.getContribution())));
				}
			}
		} else if (s.equalsIgnoreCase("fix")) { // æ­¦å™¨çš„ä¿®ç�†

		} else if (s.equalsIgnoreCase("room")) { // ç§Ÿæˆ¿é–“
			L1NpcInstance npc = (L1NpcInstance) obj;
			int npcId = npc.getNpcTemplate().get_npcId();
			boolean canRent = false;
			boolean findRoom = false;
			boolean isRent = false;
			boolean isHall = false;
			int roomNumber = 0;
			byte roomCount = 0;
			for (int i = 0; i < 16; i++) {
				L1Inn inn = InnTable.getInstance().getTemplate(npcId, i);
				if (inn != null) { // æ­¤æ—…é¤¨NPCè³‡è¨Šä¸�ç‚ºç©ºå€¼
					Timestamp dueTime = inn.getDueTime();
					Calendar cal = Calendar.getInstance();
					long checkDueTime = (cal.getTimeInMillis() - dueTime
							.getTime()) / 1000;
					if (inn.getLodgerId() == pc.getId() && checkDueTime < 0) { // å‡ºç§Ÿæ™‚é–“æœªåˆ°çš„æˆ¿é–“ç§Ÿç”¨äººåˆ¤æ–·
						if (inn.isHall()) { // ç§Ÿç”¨çš„æ˜¯æœƒè­°å®¤
							isHall = true;
						}
						isRent = true; // å·²ç§Ÿç”¨
						break;
					} else if (!findRoom && !isRent) { // æœªç§Ÿç”¨ä¸”å°šæœªæ‰¾åˆ°å�¯ç§Ÿç”¨çš„æˆ¿é–“
						if (checkDueTime >= 0) { // ç§Ÿç”¨æ™‚é–“å·²åˆ°
							canRent = true;
							findRoom = true;
							roomNumber = inn.getRoomNumber();
						} else { // è¨ˆç®—å‡ºç§Ÿæ™‚é–“æœªåˆ°çš„æ•¸é‡�
							if (!inn.isHall()) { // ä¸€èˆ¬æˆ¿é–“
								roomCount++;
							}
						}
					}
				}
			}

			if (isRent) {
				if (isHall) {
					htmlid = "inn15"; // çœŸæ˜¯æŠ±æ­‰ï¼Œä½ å·²ç¶“ç§Ÿå€Ÿé�Žæœƒè­°å»³äº†ã€‚
				} else {
					htmlid = "inn5"; // å°�ä¸�èµ·ï¼Œä½ å·²ç¶“æœ‰ç§Ÿæˆ¿é–“äº†ã€‚
				}
			} else if (roomCount >= 12) {
				htmlid = "inn6"; // çœŸä¸�å¥½æ„�æ€�ï¼Œç�¾åœ¨æ²’æœ‰æˆ¿é–“äº†ã€‚
			} else if (canRent) {
				pc.setInnRoomNumber(roomNumber); // æˆ¿é–“ç·¨è™Ÿ
				pc.setHall(false); // ä¸€èˆ¬æˆ¿é–“
				pc.sendPackets(new S_HowManyKey(npc, 300, 1, 8, "inn2"));
			}
		} else if (s.equalsIgnoreCase("hall")
				&& (obj instanceof L1MerchantInstance)) { // ç§Ÿæœƒè­°å»³
			if (pc.isCrown()) {
				L1NpcInstance npc = (L1NpcInstance) obj;
				int npcId = npc.getNpcTemplate().get_npcId();
				boolean canRent = false;
				boolean findRoom = false;
				boolean isRent = false;
				boolean isHall = false;
				int roomNumber = 0;
				byte roomCount = 0;
				for (int i = 0; i < 16; i++) {
					L1Inn inn = InnTable.getInstance().getTemplate(npcId, i);
					if (inn != null) { // æ­¤æ—…é¤¨NPCè³‡è¨Šä¸�ç‚ºç©ºå€¼
						Timestamp dueTime = inn.getDueTime();
						Calendar cal = Calendar.getInstance();
						long checkDueTime = (cal.getTimeInMillis() - dueTime
								.getTime()) / 1000;
						if (inn.getLodgerId() == pc.getId() && checkDueTime < 0) { // å‡ºç§Ÿæ™‚é–“æœªåˆ°çš„æˆ¿é–“ç§Ÿç”¨äººåˆ¤æ–·
							if (inn.isHall()) { // ç§Ÿç”¨çš„æ˜¯æœƒè­°å®¤
								isHall = true;
							}
							isRent = true; // å·²ç§Ÿç”¨
							break;
						} else if (!findRoom && !isRent) { // æœªç§Ÿç”¨ä¸”å°šæœªæ‰¾åˆ°å�¯ç§Ÿç”¨çš„æˆ¿é–“
							if (checkDueTime >= 0) { // ç§Ÿç”¨æ™‚é–“å·²åˆ°
								canRent = true;
								findRoom = true;
								roomNumber = inn.getRoomNumber();
							} else { // è¨ˆç®—å‡ºç§Ÿæ™‚é–“æœªåˆ°çš„æ•¸é‡�
								if (inn.isHall()) { // æœƒè­°å®¤
									roomCount++;
								}
							}
						}
					}
				}

				if (isRent) {
					if (isHall) {
						htmlid = "inn15"; // çœŸæ˜¯æŠ±æ­‰ï¼Œä½ å·²ç¶“ç§Ÿå€Ÿé�Žæœƒè­°å»³äº†ã€‚
					} else {
						htmlid = "inn5"; // å°�ä¸�èµ·ï¼Œä½ å·²ç¶“æœ‰ç§Ÿæˆ¿é–“äº†ã€‚
					}
				} else if (roomCount >= 4) {
					htmlid = "inn16"; // ä¸�å¥½æ„�æ€�ï¼Œç›®å‰�æ­£å¥½æ²’æœ‰ç©ºçš„æœƒè­°å»³ã€‚
				} else if (canRent) {
					pc.setInnRoomNumber(roomNumber); // æˆ¿é–“ç·¨è™Ÿ
					pc.setHall(true); // æœƒè­°å®¤
					pc.sendPackets(new S_HowManyKey(npc, 300, 1, 8, "inn12"));
				}
			} else {
				// çŽ‹å­�å’Œå…¬ä¸»æ‰�èƒ½ç§Ÿç”¨æœƒè­°å»³ã€‚
				htmlid = "inn10";
			}
		} else if (s.equalsIgnoreCase("return")) { // é€€ç§Ÿ
			L1NpcInstance npc = (L1NpcInstance) obj;
			int npcId = npc.getNpcTemplate().get_npcId();
			int price = 0;
			boolean isBreak = false;
			// é€€ç§Ÿåˆ¤æ–·
			for (int i = 0; i < 16; i++) {
				L1Inn inn = InnTable.getInstance().getTemplate(npcId, i);
				if (inn != null) { // æ­¤æ—…é¤¨NPCæˆ¿é–“è³‡è¨Šä¸�ç‚ºç©ºå€¼
					if (inn.getLodgerId() == pc.getId()) { // æ¬²é€€ç§Ÿçš„ç§Ÿç”¨äºº
						Timestamp dueTime = inn.getDueTime();
						if (dueTime != null) { // æ™‚é–“ä¸�ç‚ºç©ºå€¼
							Calendar cal = Calendar.getInstance();
							if (((cal.getTimeInMillis() - dueTime.getTime()) / 1000) < 0) { // ç§Ÿç”¨æ™‚é–“æœªåˆ°
								isBreak = true;
								price += 60; // é€€ 20%ç§Ÿé‡‘
							}
						}
						Timestamp ts = new Timestamp(System.currentTimeMillis()); // ç›®å‰�æ™‚é–“
						inn.setDueTime(ts); // é€€ç§Ÿæ™‚é–“
						inn.setLodgerId(0); // ç§Ÿç”¨äºº
						inn.setKeyId(0); // æ—…é¤¨é‘°åŒ™
						inn.setHall(false);
						// DBæ›´æ–°
						InnTable.getInstance().updateInn(inn);
						break;
					}
				}
			}
			// åˆªé™¤é‘°åŒ™åˆ¤æ–·
			for (L1ItemInstance item : pc.getInventory().getItems()) {
				if (item.getInnNpcId() == npcId) { // é‘°åŒ™èˆ‡é€€ç§Ÿçš„NPCç›¸ç¬¦
					price += 20 * item.getCount(); // é‘°åŒ™çš„åƒ¹éŒ¢ 20 * é‘°åŒ™æ•¸é‡�
					InnKeyTable.DeleteKey(item); // åˆªé™¤é‘°åŒ™ç´€éŒ„
					pc.getInventory().removeItem(item); // åˆªé™¤é‘°åŒ™
					isBreak = true;
				}
			}

			if (isBreak) {
				htmldata = new String[] { npc.getName(), String.valueOf(price) };
				htmlid = "inn20";
				pc.getInventory().storeItem(L1ItemId.ADENA, price); // å�–å¾—é‡‘å¹£
			} else {
				htmlid = "";
			}
		} else if (s.equalsIgnoreCase("enter")) { // é€²å…¥æˆ¿é–“æˆ–æœƒè­°å»³
			L1NpcInstance npc = (L1NpcInstance) obj;
			int npcId = npc.getNpcTemplate().get_npcId();

			for (L1ItemInstance item : pc.getInventory().getItems()) {
				if (item.getInnNpcId() == npcId) { // é‘°åŒ™èˆ‡NPCç›¸ç¬¦
					for (int i = 0; i < 16; i++) {
						L1Inn inn = InnTable.getInstance()
								.getTemplate(npcId, i);
						if (inn.getKeyId() == item.getKeyId()) {
							Timestamp dueTime = item.getDueTime();
							if (dueTime != null) { // æ™‚é–“ä¸�ç‚ºç©ºå€¼
								Calendar cal = Calendar.getInstance();
								if (((cal.getTimeInMillis() - dueTime.getTime()) / 1000) < 0) { // é‘°åŒ™ç§Ÿç”¨æ™‚é–“æœªåˆ°
									int[] data = null;
									switch (npcId) {
									case 70012: // èªªè©±ä¹‹å³¶ - ç‘Ÿç�³å¨œ
										data = new int[] { 32745, 32803, 16384,
												32743, 32808, 16896 };
										break;
									case 70019: // å�¤é­¯ä¸� - ç¾…åˆ©é›…
										data = new int[] { 32743, 32803, 17408,
												32744, 32807, 17920 };
										break;
									case 70031: // å¥‡å²© - ç‘ªç�†
										data = new int[] { 32744, 32803, 18432,
												32744, 32807, 18944 };
										break;
									case 70065: // æ­�ç‘ž - å°�å®‰å®‰
										data = new int[] { 32744, 32803, 19456,
												32744, 32807, 19968 };
										break;
									case 70070: // é¢¨æœ¨ - ç¶­è�ŠèŽŽ
										data = new int[] { 32744, 32803, 20480,
												32744, 32807, 20992 };
										break;
									case 70075: // éŠ€é¨Žå£« - ç±³è˜­å¾·
										data = new int[] { 32744, 32803, 21504,
												32744, 32807, 22016 };
										break;
									case 70084: // æµ·éŸ³ - ä¼ŠèŽ‰
										data = new int[] { 32744, 32803, 22528,
												32744, 32807, 23040 };
										break;
									default:
										break;
									}

									pc.setInnKeyId(item.getKeyId()); // ç™»å…¥é‘°åŒ™ç·¨è™Ÿ

									if (!item.checkRoomOrHall()) { // æˆ¿é–“
										L1Teleport.teleport(pc, data[0],
												data[1], (short) data[2], 6,
												false);
									} else { // æœƒè­°å®¤
										L1Teleport.teleport(pc, data[3],
												data[4], (short) data[5], 6,
												false);
										break;
									}
								}
							}
						}
					}
				}
			}
		} else if (s.equalsIgnoreCase("openigate")) { // ã‚²ãƒ¼ãƒˆã‚­ãƒ¼ãƒ‘ãƒ¼ / åŸŽé–€ã‚’é–‹ã�‘ã‚‹
			L1NpcInstance npc = (L1NpcInstance) obj;
			openCloseGate(pc, npc.getNpcTemplate().get_npcId(), true);
			htmlid = ""; // ã‚¦ã‚£ãƒ³ãƒ‰ã‚¦ã‚’æ¶ˆã�™
		} else if (s.equalsIgnoreCase("closeigate")) { // ã‚²ãƒ¼ãƒˆã‚­ãƒ¼ãƒ‘ãƒ¼ / åŸŽé–€ã‚’é–‰ã‚�ã‚‹
			L1NpcInstance npc = (L1NpcInstance) obj;
			openCloseGate(pc, npc.getNpcTemplate().get_npcId(), false);
			htmlid = ""; // ã‚¦ã‚£ãƒ³ãƒ‰ã‚¦ã‚’æ¶ˆã�™
		} else if (s.equalsIgnoreCase("askwartime")) { // è¿‘è¡›å…µ / æ¬¡ã�®æ”»åŸŽæˆ¦ã�„ã�®æ™‚é–“ã‚’ã�Ÿã�šã�­ã‚‹
			L1NpcInstance npc = (L1NpcInstance) obj;
			if (npc.getNpcTemplate().get_npcId() == 60514) { // ã‚±ãƒ³ãƒˆåŸŽè¿‘è¡›å…µ
				htmldata = makeWarTimeStrings(L1CastleLocation.KENT_CASTLE_ID);
				htmlid = "ktguard7";
			} else if (npc.getNpcTemplate().get_npcId() == 60560) { // ã‚ªãƒ¼ã‚¯è¿‘è¡›å…µ
				htmldata = makeWarTimeStrings(L1CastleLocation.OT_CASTLE_ID);
				htmlid = "orcguard7";
			} else if (npc.getNpcTemplate().get_npcId() == 60552) { // ã‚¦ã‚£ãƒ³ãƒ€ã‚¦ãƒƒãƒ‰åŸŽè¿‘è¡›å…µ
				htmldata = makeWarTimeStrings(L1CastleLocation.WW_CASTLE_ID);
				htmlid = "wdguard7";
			} else if ((npc.getNpcTemplate().get_npcId() == 60524) || // ã‚®ãƒ©ãƒ³è¡—å…¥ã‚Šå�£è¿‘è¡›å…µ(å¼“)
					(npc.getNpcTemplate().get_npcId() == 60525) || // ã‚®ãƒ©ãƒ³è¡—å…¥ã‚Šå�£è¿‘è¡›å…µ
					(npc.getNpcTemplate().get_npcId() == 60529)) { // ã‚®ãƒ©ãƒ³åŸŽè¿‘è¡›å…µ
				htmldata = makeWarTimeStrings(L1CastleLocation.GIRAN_CASTLE_ID);
				htmlid = "grguard7";
			} else if (npc.getNpcTemplate().get_npcId() == 70857) { // ãƒ�ã‚¤ãƒ�åŸŽãƒ�ã‚¤ãƒ�ã‚¬ãƒ¼ãƒ‰
				htmldata = makeWarTimeStrings(L1CastleLocation.HEINE_CASTLE_ID);
				htmlid = "heguard7";
			} else if ((npc.getNpcTemplate().get_npcId() == 60530) || // ãƒ‰ãƒ¯ãƒ¼ãƒ•åŸŽãƒ‰ãƒ¯ãƒ¼ãƒ•ã‚¬ãƒ¼ãƒ‰
					(npc.getNpcTemplate().get_npcId() == 60531)) {
				htmldata = makeWarTimeStrings(L1CastleLocation.DOWA_CASTLE_ID);
				htmlid = "dcguard7";
			} else if ((npc.getNpcTemplate().get_npcId() == 60533) || // ã‚¢ãƒ‡ãƒ³åŸŽ
																		// ã‚¬ãƒ¼ãƒ‰
					(npc.getNpcTemplate().get_npcId() == 60534)) {
				htmldata = makeWarTimeStrings(L1CastleLocation.ADEN_CASTLE_ID);
				htmlid = "adguard7";
			} else if (npc.getNpcTemplate().get_npcId() == 81156) { // ã‚¢ãƒ‡ãƒ³å�µå¯Ÿå…µï¼ˆãƒ‡ã‚£ã‚¢ãƒ‰è¦�å¡žï¼‰
				htmldata = makeWarTimeStrings(L1CastleLocation.DIAD_CASTLE_ID);
				htmlid = "dfguard3";
			}
		} else if (s.equalsIgnoreCase("inex")) { // å�Žå…¥/æ”¯å‡ºã�®å ±å‘Šã‚’å�—ã�‘ã‚‹
			// æš«å®šçš„ã�«å…¬é‡‘ã‚’ãƒ�ãƒ£ãƒƒãƒˆã‚¦ã‚£ãƒ³ãƒ‰ã‚¦ã�«è¡¨ç¤ºã�•ã�›ã‚‹ã€‚
			// ãƒ¡ãƒƒã‚»ãƒ¼ã‚¸ã�¯é�©å½“ã€‚
			L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
			if (clan != null) {
				int castle_id = clan.getCastleId();
				if (castle_id != 0) { // åŸŽä¸»ã‚¯ãƒ©ãƒ³
					L1Castle l1castle = CastleTable.getInstance()
							.getCastleTable(castle_id);
					pc.sendPackets(new S_ServerMessage(309, // %0ã�®ç²¾ç®—ç·�é¡�ã�¯%1ã‚¢ãƒ‡ãƒŠã�§ã�™ã€‚
							l1castle.getName(), String.valueOf(l1castle.getPublicMoney())));
					htmlid = ""; // ã‚¦ã‚£ãƒ³ãƒ‰ã‚¦ã‚’æ¶ˆã�™
				}
			}
		} else if (s.equalsIgnoreCase("tax")) { // ç¨ŽçŽ‡ã‚’èª¿ç¯€ã�™ã‚‹
			pc.sendPackets(new S_TaxRate(pc.getId()));
		} else if (s.equalsIgnoreCase("withdrawal")) { // è³‡é‡‘ã‚’å¼•ã��å‡ºã�™
			L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
			if (clan != null) {
				int castle_id = clan.getCastleId();
				if (castle_id != 0) { // åŸŽä¸»ã‚¯ãƒ©ãƒ³
					L1Castle l1castle = CastleTable.getInstance().getCastleTable(castle_id);
					pc.sendPackets(new S_Drawal(pc.getId(), l1castle.getPublicMoney()));
				}
			}
		} else if (s.equalsIgnoreCase("cdeposit")) { // è³‡é‡‘ã‚’å…¥é‡‘ã�™ã‚‹
			pc.sendPackets(new S_Deposit(pc.getId()));
		} else if (s.equalsIgnoreCase("employ")) { // å‚­å…µã�®é›‡ç”¨

		} else if (s.equalsIgnoreCase("arrange")) { // é›‡ç”¨ã�—ã�Ÿå‚­å…µã�®é…�ç½®

		} else if (s.equalsIgnoreCase("castlegate")) { // åŸŽé–€ã‚’ç®¡ç�†ã�™ã‚‹
			repairGate(pc);
			htmlid = ""; // ã‚¦ã‚£ãƒ³ãƒ‰ã‚¦ã‚’æ¶ˆã�™
		} else if (s.equalsIgnoreCase("encw")) { // æ­¦å™¨å°‚é–€å®¶ / æ­¦å™¨ã�®å¼·åŒ–é­”æ³•ã‚’å�—ã�‘ã‚‹
			if (pc.getWeapon() == null) {
				pc.sendPackets(new S_ServerMessage(79));
			} else {
				for (L1ItemInstance item : pc.getInventory().getItems()) {
					if (pc.getWeapon().equals(item)) {
						L1SkillUse l1skilluse = new L1SkillUse();
						l1skilluse.handleCommands(pc, ENCHANT_WEAPON,
								item.getId(), 0, 0, null, 0,
								L1SkillUse.TYPE_SPELLSC);
						break;
					}
				}
			}
			htmlid = ""; // ã‚¦ã‚£ãƒ³ãƒ‰ã‚¦ã‚’æ¶ˆã�™
			
			
			// Adding buff NPC in Caslte - [Hank]
		}else if(s.equalsIgnoreCase("buff")){
			int[] allBuffSkill =
				{LIGHT, DECREASE_WEIGHT, PHYSICAL_ENCHANT_DEX, PHYSICAL_ENCHANT_STR, BLESS_WEAPON, ADVANCE_SPIRIT, FIRE_WEAPON, WIND_SHOT, EARTH_SKIN};
			for(int i = 0; i < allBuffSkill.length;i++)
			{
				L1SkillUse l1skilluse = new L1SkillUse();
				l1skilluse.handleCommands(pc, allBuffSkill[i], pc.getId(), pc.getX(),
						pc.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
			}
			htmlid = "";
			
			
		} else if (s.equalsIgnoreCase("enca")) { // é˜²å…·å°‚é–€å®¶ / é˜²å…·ã�®å¼·åŒ–é­”æ³•ã‚’å�—ã�‘ã‚‹
			L1ItemInstance item = pc.getInventory().getItemEquipped(2, 2);
			if (item != null) {
				L1SkillUse l1skilluse = new L1SkillUse();
				l1skilluse.handleCommands(pc, BLESSED_ARMOR, item.getId(), 0,
						0, null, 0, L1SkillUse.TYPE_SPELLSC);
			} else {
				pc.sendPackets(new S_ServerMessage(79));
			}
			htmlid = ""; // ã‚¦ã‚£ãƒ³ãƒ‰ã‚¦ã‚’æ¶ˆã�™
		} else if (s.equalsIgnoreCase("depositnpc")) { // å¯„è¨—å¯µç‰©
			for (L1NpcInstance petNpc : pc.getPetList().values()) {
				if (petNpc instanceof L1PetInstance) { // ãƒšãƒƒãƒˆ
					L1PetInstance pet = (L1PetInstance) petNpc;
					pc.sendPackets(new S_PetCtrlMenu(pc, petNpc, false));// é—œé–‰å¯µç‰©æŽ§åˆ¶åœ–å½¢ä»‹é�¢
					// å�œæ­¢é£½é£Ÿåº¦è¨ˆæ™‚
					pet.stopFoodTimer(pet);
					pet.collect(true);
					pc.getPetList().remove(pet.getId());
					pet.deleteMe();
				}
			}
			/*if (pc.getPetList().isEmpty()) {
				pc.sendPackets(new S_PetCtrlMenu(pc, null, false));// é—œé–‰å¯µç‰©æŽ§åˆ¶åœ–å½¢ä»‹é�¢
			} else {
				// æ›´æ–°å¯µç‰©æŽ§åˆ¶ä»‹é�¢
				for (L1NpcInstance petNpc : pc.getPetList().values()) {
					if (petNpc instanceof L1SummonInstance) {
						L1SummonInstance summon = (L1SummonInstance) petNpc;
						pc.sendPackets(new S_SummonPack(summon, pc));
						pc.sendPackets(new S_ServerMessage(79));
						break;
					}
				}
			}*/
			htmlid = ""; // ã‚¦ã‚£ãƒ³ãƒ‰ã‚¦ã‚’æ¶ˆã�™
		} else if (s.equalsIgnoreCase("withdrawnpc")) { // é ˜å�–å¯µç‰©
			pc.sendPackets(new S_PetList(objid, pc));
		} else if (s.equalsIgnoreCase("aggressive")) { // æ”»æ’ƒæ…‹å‹¢
			if (obj instanceof L1PetInstance) {
				L1PetInstance l1pet = (L1PetInstance) obj;
				l1pet.setCurrentPetStatus(1);
			}
		} else if (s.equalsIgnoreCase("defensive")) { // é˜²ç¦¦åž‹æ…‹
			if (obj instanceof L1PetInstance) {
				L1PetInstance l1pet = (L1PetInstance) obj;
				l1pet.setCurrentPetStatus(2);
			}
		} else if (s.equalsIgnoreCase("stay")) { // ä¼‘æ†©
			if (obj instanceof L1PetInstance) {
				L1PetInstance l1pet = (L1PetInstance) obj;
				l1pet.setCurrentPetStatus(3);
			}
		} else if (s.equalsIgnoreCase("extend")) { // é…�å‚™
			if (obj instanceof L1PetInstance) {
				L1PetInstance l1pet = (L1PetInstance) obj;
				l1pet.setCurrentPetStatus(4);
			}
		} else if (s.equalsIgnoreCase("alert")) { // è­¦æˆ’
			if (obj instanceof L1PetInstance) {
				L1PetInstance l1pet = (L1PetInstance) obj;
				l1pet.setCurrentPetStatus(5);
			}
		} else if (s.equalsIgnoreCase("dismiss")) { // è§£æ•£
			if (obj instanceof L1PetInstance) {
				L1PetInstance l1pet = (L1PetInstance) obj;
				l1pet.setCurrentPetStatus(6);
			}
		} else if (s.equalsIgnoreCase("changename")) { // ã€Œå��å‰�ã‚’æ±ºã‚�ã‚‹ã€�
			pc.setTempID(objid); // ãƒšãƒƒãƒˆã�®ã‚ªãƒ–ã‚¸ã‚§ã‚¯ãƒˆIDã‚’ä¿�å­˜ã�—ã�¦ã�Šã��
			pc.sendPackets(new S_Message_YN(325, "")); // å‹•ç‰©ã�®å��å‰�ã‚’æ±ºã‚�ã�¦ã��ã� ã�•ã�„ï¼š
		} else if (s.equalsIgnoreCase("attackchr")) {
			if (obj instanceof L1Character) {
				L1Character cha = (L1Character) obj;
				pc.sendPackets(new S_SelectTarget(cha.getId()));
			}
		} else if (s.equalsIgnoreCase("select")) { // ç«¶å£²æŽ²ç¤ºæ�¿ã�®ãƒªã‚¹ãƒˆã‚’ã‚¯ãƒªãƒƒã‚¯
			pc.sendPackets(new S_AuctionBoardRead(objid, s2));
		} else if (s.equalsIgnoreCase("map")) { // ã‚¢ã‚¸ãƒˆã�®ä½�ç½®ã‚’ç¢ºã�‹ã‚�ã‚‹
			pc.sendPackets(new S_HouseMap(objid, s2));
		} else if (s.equalsIgnoreCase("apply")) { // ç«¶å£²ã�«å�‚åŠ ã�™ã‚‹
			L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
			if (clan != null) {
				if (pc.isCrown() && (pc.getId() == clan.getLeaderId())) { // å�›ä¸»ã€�ã�‹ã�¤ã€�è¡€ç›Ÿä¸»
					if (pc.getLevel() >= 15) {
						if (clan.getHouseId() == 0) {
							pc.sendPackets(new S_ApplyAuction(objid, s2));
						} else {
							pc.sendPackets(new S_ServerMessage(521)); // ã�™ã�§ã�«å®¶ã‚’æ‰€æœ‰ã�—ã�¦ã�„ã�¾ã�™ã€‚
							htmlid = ""; // ã‚¦ã‚£ãƒ³ãƒ‰ã‚¦ã‚’æ¶ˆã�™
						}
					} else {
						pc.sendPackets(new S_ServerMessage(519)); // ãƒ¬ãƒ™ãƒ«15æœªæº€ã�®å�›ä¸»ã�¯ç«¶å£²ã�«å�‚åŠ ã�§ã��ã�¾ã�›ã‚“ã€‚
						htmlid = ""; // ã‚¦ã‚£ãƒ³ãƒ‰ã‚¦ã‚’æ¶ˆã�™
					}
				} else {
					pc.sendPackets(new S_ServerMessage(518)); // ã�“ã�®å‘½ä»¤ã�¯è¡€ç›Ÿã�®å�›ä¸»ã�®ã�¿ã�Œåˆ©ç”¨ã�§ã��ã�¾ã�™ã€‚
					htmlid = ""; // ã‚¦ã‚£ãƒ³ãƒ‰ã‚¦ã‚’æ¶ˆã�™
				}
			} else {
				pc.sendPackets(new S_ServerMessage(518)); // ã�“ã�®å‘½ä»¤ã�¯è¡€ç›Ÿã�®å�›ä¸»ã�®ã�¿ã�Œåˆ©ç”¨ã�§ã��ã�¾ã�™ã€‚
				htmlid = ""; // ã‚¦ã‚£ãƒ³ãƒ‰ã‚¦ã‚’æ¶ˆã�™
			}
		} else if (s.equalsIgnoreCase("open") // ãƒ‰ã‚¢ã‚’é–‹ã�‘ã‚‹
				|| s.equalsIgnoreCase("close")) { // ãƒ‰ã‚¢ã‚’é–‰ã‚�ã‚‹
			L1NpcInstance npc = (L1NpcInstance) obj;
			openCloseDoor(pc, npc, s);
			htmlid = ""; // ã‚¦ã‚£ãƒ³ãƒ‰ã‚¦ã‚’æ¶ˆã�™
		} else if (s.equalsIgnoreCase("expel")) { // å¤–éƒ¨ã�®äººé–“ã‚’è¿½ã�„å‡ºã�™
			L1NpcInstance npc = (L1NpcInstance) obj;
			expelOtherClan(pc, npc.getNpcTemplate().get_npcId());
			htmlid = ""; // ã‚¦ã‚£ãƒ³ãƒ‰ã‚¦ã‚’æ¶ˆã�™
		} else if (s.equalsIgnoreCase("pay")) { // ç¨Žé‡‘ã‚’ç´�ã‚�ã‚‹
			L1NpcInstance npc = (L1NpcInstance) obj;
			htmldata = makeHouseTaxStrings(pc, npc);
			htmlid = "agpay";
		} else if (s.equalsIgnoreCase("payfee")) { // ç¨Žé‡‘ã‚’ç´�ã‚�ã‚‹
			L1NpcInstance npc = (L1NpcInstance) obj;
			htmldata = new String[] { npc.getNpcTemplate().get_name(), "2000" };
			htmlid = "";
			if (payFee(pc, npc))
				htmlid = "agpayfee";
		} else if (s.equalsIgnoreCase("name")) { // å®¶ã�®å��å‰�ã‚’æ±ºã‚�ã‚‹
			L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
			if (clan != null) {
				int houseId = clan.getHouseId();
				if (houseId != 0) {
					L1House house = HouseTable.getInstance().getHouseTable(
							houseId);
					int keeperId = house.getKeeperId();
					L1NpcInstance npc = (L1NpcInstance) obj;
					if (npc.getNpcTemplate().get_npcId() == keeperId) {
						pc.setTempID(houseId); // ã‚¢ã‚¸ãƒˆIDã‚’ä¿�å­˜ã�—ã�¦ã�Šã��
						pc.sendPackets(new S_Message_YN(512, "")); // å®¶ã�®å��å‰�ã�¯ï¼Ÿ
					}
				}
			}
			htmlid = ""; // ã‚¦ã‚£ãƒ³ãƒ‰ã‚¦ã‚’æ¶ˆã�™
		} else if (s.equalsIgnoreCase("rem")) { // å®¶ã�®ä¸­ã�®å®¶å…·ã‚’ã�™ã�¹ã�¦å�–ã‚Šé™¤ã��
		} else if (s.equalsIgnoreCase("tel0") // ãƒ†ãƒ¬ãƒ�ãƒ¼ãƒˆã�™ã‚‹(å€‰åº«)
				|| s.equalsIgnoreCase("tel1") // ãƒ†ãƒ¬ãƒ�ãƒ¼ãƒˆã�™ã‚‹(ãƒšãƒƒãƒˆä¿�ç®¡æ‰€)
				|| s.equalsIgnoreCase("tel2") // ãƒ†ãƒ¬ãƒ�ãƒ¼ãƒˆã�™ã‚‹(è´–ç½ªã�®ä½¿è€…)
				|| s.equalsIgnoreCase("tel3")) { // ãƒ†ãƒ¬ãƒ�ãƒ¼ãƒˆã�™ã‚‹(ã‚®ãƒ©ãƒ³å¸‚å ´)
			L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
			if (clan != null) {
				int houseId = clan.getHouseId();
				if (houseId != 0) {
					L1House house = HouseTable.getInstance().getHouseTable(
							houseId);
					int keeperId = house.getKeeperId();
					L1NpcInstance npc = (L1NpcInstance) obj;
					if (npc.getNpcTemplate().get_npcId() == keeperId) {
						int[] loc = new int[3];
						if (s.equalsIgnoreCase("tel0")) {
							loc = L1HouseLocation.getHouseTeleportLoc(houseId,
									0);
						} else if (s.equalsIgnoreCase("tel1")) {
							loc = L1HouseLocation.getHouseTeleportLoc(houseId,
									1);
						} else if (s.equalsIgnoreCase("tel2")) {
							loc = L1HouseLocation.getHouseTeleportLoc(houseId,
									2);
						} else if (s.equalsIgnoreCase("tel3")) {
							loc = L1HouseLocation.getHouseTeleportLoc(houseId,
									3);
						}
						L1Teleport.teleport(pc, loc[0], loc[1], (short) loc[2],
								5, true);
					}
				}
			}
			htmlid = ""; // ã‚¦ã‚£ãƒ³ãƒ‰ã‚¦ã‚’æ¶ˆã�™
		} else if (s.equalsIgnoreCase("upgrade")) { // åœ°ä¸‹ã‚¢ã‚¸ãƒˆã‚’ä½œã‚‹
			L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
			if (clan != null) {
				int houseId = clan.getHouseId();
				if (houseId != 0) {
					L1House house = HouseTable.getInstance().getHouseTable(
							houseId);
					int keeperId = house.getKeeperId();
					L1NpcInstance npc = (L1NpcInstance) obj;
					if (npc.getNpcTemplate().get_npcId() == keeperId) {
						if (pc.isCrown() && (pc.getId() == clan.getLeaderId())) { // å�›ä¸»ã€�ã�‹ã�¤ã€�è¡€ç›Ÿä¸»
							if (house.isPurchaseBasement()) {
								// æ—¢ã�«åœ°ä¸‹ã‚¢ã‚¸ãƒˆã‚’æ‰€æœ‰ã�—ã�¦ã�„ã�¾ã�™ã€‚
								pc.sendPackets(new S_ServerMessage(1135));
							} else {
								if (pc.getInventory().consumeItem(
										L1ItemId.ADENA, 5000000)) {
									house.setPurchaseBasement(true);
									HouseTable.getInstance().updateHouse(house); // DBã�«æ›¸ã��è¾¼ã�¿
									// åœ°ä¸‹ã‚¢ã‚¸ãƒˆã�Œç”Ÿæˆ�ã�•ã‚Œã�¾ã�—ã�Ÿã€‚
									pc.sendPackets(new S_ServerMessage(1099));
								} else {
									// \f1ã‚¢ãƒ‡ãƒŠã�Œä¸�è¶³ã�—ã�¦ã�„ã�¾ã�™ã€‚
									pc.sendPackets(new S_ServerMessage(189));
								}
							}
						} else {
							// ã�“ã�®å‘½ä»¤ã�¯è¡€ç›Ÿã�®å�›ä¸»ã�®ã�¿ã�Œåˆ©ç”¨ã�§ã��ã�¾ã�™ã€‚
							pc.sendPackets(new S_ServerMessage(518));
						}
					}
				}
			}
			htmlid = ""; // ã‚¦ã‚£ãƒ³ãƒ‰ã‚¦ã‚’æ¶ˆã�™
		} else if (s.equalsIgnoreCase("hall")
				&& (obj instanceof L1HousekeeperInstance)) { // åœ°ä¸‹ã‚¢ã‚¸ãƒˆã�«ãƒ†ãƒ¬ãƒ�ãƒ¼ãƒˆã�™ã‚‹
			L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
			if (clan != null) {
				int houseId = clan.getHouseId();
				if (houseId != 0) {
					L1House house = HouseTable.getInstance().getHouseTable(
							houseId);
					int keeperId = house.getKeeperId();
					L1NpcInstance npc = (L1NpcInstance) obj;
					if (npc.getNpcTemplate().get_npcId() == keeperId) {
						if (house.isPurchaseBasement()) {
							int[] loc = new int[3];
							loc = L1HouseLocation.getBasementLoc(houseId);
							L1Teleport.teleport(pc, loc[0], loc[1],
									(short) (loc[2]), 5, true);
						} else {
							// åœ°ä¸‹ã‚¢ã‚¸ãƒˆã�Œã�ªã�„ã�Ÿã‚�ã€�ãƒ†ãƒ¬ãƒ�ãƒ¼ãƒˆã�§ã��ã�¾ã�›ã‚“ã€‚
							pc.sendPackets(new S_ServerMessage(1098));
						}
					}
				}
			}
			htmlid = ""; // ã‚¦ã‚£ãƒ³ãƒ‰ã‚¦ã‚’æ¶ˆã�™
		}

		// ElfAttr:0.ç„¡å±žæ€§,1.åœ°å±žæ€§,2.ç�«å±žæ€§,4.æ°´å±žæ€§,8.é¢¨å±žæ€§
		else if (s.equalsIgnoreCase("fire")) // ã‚¨ãƒ«ãƒ•ã�®å±žæ€§å¤‰æ›´ã€Œç�«ã�®ç³»åˆ—ã‚’ç¿’ã�†ã€�
		{
			if (pc.isElf()) {
				if (pc.getElfAttr() != 0) {
					return;
				}
				pc.setElfAttr(2);
				pc.save(); // DBã�«ã‚­ãƒ£ãƒ©ã‚¯ã‚¿ãƒ¼æƒ…å ±ã‚’æ›¸ã��è¾¼ã‚€
				pc.sendPackets(new S_PacketBox(S_PacketBox.MSG_ELF, 1)); // å¿½ç„¶å…¨èº«å……æ»¿äº†ç�«çš„é�ˆåŠ›ã€‚
				htmlid = ""; // ã‚¦ã‚£ãƒ³ãƒ‰ã‚¦ã‚’æ¶ˆã�™
			}
		} else if (s.equalsIgnoreCase("water")) { // ã‚¨ãƒ«ãƒ•ã�®å±žæ€§å¤‰æ›´ã€Œæ°´ã�®ç³»åˆ—ã‚’ç¿’ã�†ã€�
			if (pc.isElf()) {
				if (pc.getElfAttr() != 0) {
					return;
				}
				pc.setElfAttr(4);
				pc.save(); // DBã�«ã‚­ãƒ£ãƒ©ã‚¯ã‚¿ãƒ¼æƒ…å ±ã‚’æ›¸ã��è¾¼ã‚€
				pc.sendPackets(new S_PacketBox(S_PacketBox.MSG_ELF, 2)); // å¿½ç„¶å…¨èº«å……æ»¿äº†æ°´çš„é�ˆåŠ›ã€‚
				htmlid = ""; // ã‚¦ã‚£ãƒ³ãƒ‰ã‚¦ã‚’æ¶ˆã�™
			}
		} else if (s.equalsIgnoreCase("air")) { // ã‚¨ãƒ«ãƒ•ã�®å±žæ€§å¤‰æ›´ã€Œé¢¨ã�®ç³»åˆ—ã‚’ç¿’ã�†ã€�
			if (pc.isElf()) {
				if (pc.getElfAttr() != 0) {
					return;
				}
				pc.setElfAttr(8);
				pc.save(); // DBã�«ã‚­ãƒ£ãƒ©ã‚¯ã‚¿ãƒ¼æƒ…å ±ã‚’æ›¸ã��è¾¼ã‚€
				pc.sendPackets(new S_PacketBox(S_PacketBox.MSG_ELF, 3)); // å¿½ç„¶å…¨èº«å……æ»¿äº†é¢¨çš„é�ˆåŠ›ã€‚
				htmlid = ""; // ã‚¦ã‚£ãƒ³ãƒ‰ã‚¦ã‚’æ¶ˆã�™
			}
		} else if (s.equalsIgnoreCase("earth")) { // ã‚¨ãƒ«ãƒ•ã�®å±žæ€§å¤‰æ›´ã€Œåœ°ã�®ç³»åˆ—ã‚’ç¿’ã�†ã€�
			if (pc.isElf()) {
				if (pc.getElfAttr() != 0) {
					return;
				}
				pc.setElfAttr(1);
				pc.save(); // DBã�«ã‚­ãƒ£ãƒ©ã‚¯ã‚¿ãƒ¼æƒ…å ±ã‚’æ›¸ã��è¾¼ã‚€
				pc.sendPackets(new S_PacketBox(S_PacketBox.MSG_ELF, 4)); // å¿½ç„¶å…¨èº«å……æ»¿äº†åœ°çš„é�ˆåŠ›ã€‚
				htmlid = ""; // ã‚¦ã‚£ãƒ³ãƒ‰ã‚¦ã‚’æ¶ˆã�™
			}
		} else if (s.equalsIgnoreCase("init")) { // ã‚¨ãƒ«ãƒ•ã�®å±žæ€§å¤‰æ›´ã€Œç²¾éœŠåŠ›ã‚’é™¤åŽ»ã�™ã‚‹ã€�
			if (pc.isElf()) {
				if (pc.getElfAttr() == 0) {
					return;
				}
				for (int cnt = 129; cnt <= 176; cnt++) // å…¨ã‚¨ãƒ«ãƒ•é­”æ³•ã‚’ãƒ�ã‚§ãƒƒã‚¯
				{
					L1Skills l1skills1 = SkillsTable.getInstance().getTemplate(
							cnt);
					int skill_attr = l1skills1.getAttr();
					if (skill_attr != 0) // ç„¡å±žæ€§é­”æ³•ä»¥å¤–ã�®ã‚¨ãƒ«ãƒ•é­”æ³•ã‚’DBã�‹ã‚‰å‰Šé™¤ã�™ã‚‹
					{
						SkillsTable.getInstance().spellLost(pc.getId(),
								l1skills1.getSkillId());
					}
				}
				// ã‚¨ãƒ¬ãƒ¡ãƒ³ã‚¿ãƒ«ãƒ—ãƒ­ãƒ†ã‚¯ã‚·ãƒ§ãƒ³ã�«ã‚ˆã�£ã�¦ä¸Šæ˜‡ã�—ã�¦ã�„ã‚‹å±žæ€§é˜²å¾¡ã‚’ãƒªã‚»ãƒƒãƒˆ
				if (pc.hasSkillEffect(ELEMENTAL_PROTECTION)) {
					pc.removeSkillEffect(ELEMENTAL_PROTECTION);
				}
				pc.sendPackets(new S_DelSkill(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 248, 252, 252, 255, 0, 0, 0, 0, 0,
						0)); // ç„¡å±žæ€§é­”æ³•ä»¥å¤–ã�®ã‚¨ãƒ«ãƒ•é­”æ³•ã‚’é­”æ³•ã‚¦ã‚£ãƒ³ãƒ‰ã‚¦ã�‹ã‚‰å‰Šé™¤ã�™ã‚‹
				pc.setElfAttr(0);
				pc.save(); // DBã�«ã‚­ãƒ£ãƒ©ã‚¯ã‚¿ãƒ¼æƒ…å ±ã‚’æ›¸ã��è¾¼ã‚€
				pc.sendPackets(new S_ServerMessage(678));
				htmlid = ""; // ã‚¦ã‚£ãƒ³ãƒ‰ã‚¦ã‚’æ¶ˆã�™
			}
		} else if (s.equalsIgnoreCase("exp")) { // ã€ŒçµŒé¨“å€¤ã‚’å›žå¾©ã�™ã‚‹ã€�
			if (pc.getExpRes() == 1) {
				int cost = 0;
				int level = pc.getLevel();
				int lawful = pc.getLawful();
				if (level < 45) {
					cost = level * level * 100;
				} else {
					cost = level * level * 200;
				}
				if (lawful >= 0) {
					cost = (cost / 2);
				}
				pc.sendPackets(new S_Message_YN(738, String.valueOf(cost))); // çµŒé¨“å€¤ã‚’å›žå¾©ã�™ã‚‹ã�«ã�¯%0ã�®ã‚¢ãƒ‡ãƒŠã�Œå¿…è¦�ã�§ã�™ã€‚çµŒé¨“å€¤ã‚’å›žå¾©ã�—ã�¾ã�™ã�‹ï¼Ÿ
			} else {
				pc.sendPackets(new S_ServerMessage(739)); // ä»Šã�¯çµŒé¨“å€¤ã‚’å›žå¾©ã�™ã‚‹ã�“ã�¨ã�Œã�§ã��ã�¾ã�›ã‚“ã€‚
				htmlid = ""; // ã‚¦ã‚£ãƒ³ãƒ‰ã‚¦ã‚’æ¶ˆã�™
			}
		} else if (s.equalsIgnoreCase("pk")) { // ã€Œè´–ç½ªã�™ã‚‹ã€�
			if (pc.getLawful() < 30000) {
				pc.sendPackets(new S_ServerMessage(559)); // \f1ã�¾ã� ç½ªæ™´ã‚‰ã�—ã�«å��åˆ†ã�ªå–„è¡Œã‚’è¡Œã�£ã�¦ã�„ã�¾ã�›ã‚“ã€‚
			} else if (pc.get_PKcount() < 5) {
				pc.sendPackets(new S_ServerMessage(560)); // \f1ã�¾ã� ç½ªæ™´ã‚‰ã�—ã‚’ã�™ã‚‹å¿…è¦�ã�¯ã�‚ã‚Šã�¾ã�›ã‚“ã€‚
			} else {
				if (pc.getInventory().consumeItem(L1ItemId.ADENA, 700000)) {
					pc.set_PKcount(pc.get_PKcount() - 5);
					pc.sendPackets(new S_ServerMessage(561, String.valueOf(pc
							.get_PKcount()))); // PKå›žæ•°ã�Œ%0ã�«ã�ªã‚Šã�¾ã�—ã�Ÿã€‚
				} else {
					pc.sendPackets(new S_ServerMessage(189)); // \f1ã‚¢ãƒ‡ãƒŠã�Œä¸�è¶³ã�—ã�¦ã�„ã�¾ã�™ã€‚
				}
			}
			// ã‚¦ã‚£ãƒ³ãƒ‰ã‚¦ã‚’æ¶ˆã�™
			htmlid = "";
		} else if (s.equalsIgnoreCase("ent")) {
			// ã€Œã�ŠåŒ–ã�‘å±‹æ•·ã�«å…¥ã‚‹ã€�
			// ã€Œã‚¢ãƒ«ãƒ†ã‚£ãƒ¡ãƒƒãƒˆ ãƒ�ãƒˆãƒ«ã�«å�‚åŠ ã�™ã‚‹ã€�ã�¾ã�Ÿã�¯
			// ã€Œè¦³è¦§ãƒ¢ãƒ¼ãƒ‰ã�§é—˜æŠ€å ´ã�«å…¥ã‚‹ã€�
			// ã€Œã‚¹ãƒ†ãƒ¼ã‚¿ã‚¹å†�åˆ†é…�ã€�
			int npcId = ((L1NpcInstance) obj).getNpcId();
			if (npcId == 80085) {
				htmlid = enterHauntedHouse(pc);
			} else if (npcId == 80088) {
				// hack to get petmatch working
				int petid = 0;
				if (pc.getInventory().checkItem(40314)) {
					L1ItemInstance item = pc.getInventory().findItemId(40314);
					petid = item.getId();
				}
				else if (pc.getInventory().checkItem(40316)) {
					L1ItemInstance item = pc.getInventory().findItemId(40316);
					petid = item.getId();
				}
				htmlid = enterPetMatch(pc, petid);
			} else if ((npcId == 50038) || (npcId == 50042) || (npcId == 50029)
					|| (npcId == 50019) || (npcId == 50062)) { // å‰¯ç®¡ç�†äººã�®å ´å�ˆã�¯è¦³æˆ¦
				htmlid = watchUb(pc, npcId);
			} else if (npcId == 71251) { // ãƒ­ãƒ­
				if (!pc.getInventory().checkItem(49142)) { // å¸Œæœ›ã�®ãƒ­ã‚¦ã‚½ã‚¯
					pc.sendPackets(new S_ServerMessage(1290)); // ã‚¹ãƒ†ãƒ¼ã‚¿ã‚¹åˆ�æœŸåŒ–ã�«å¿…è¦�ã�ªã‚¢ã‚¤ãƒ†ãƒ ã�Œã�‚ã‚Šã�¾ã�›ã‚“ã€‚
					return;
				}
				L1SkillUse l1skilluse = new L1SkillUse();
				l1skilluse.handleCommands(pc, CANCELLATION, pc.getId(),
						pc.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_LOGIN);
				pc.getInventory().takeoffEquip(945); // ç‰›ã�®polyIdã�§è£…å‚™ã‚’å…¨éƒ¨å¤–ã�™ã€‚
				L1Teleport.teleport(pc, 32737, 32789, (short) 997, 4, false);
				int initStatusPoint = 75 + pc.getElixirStats();
				int pcStatusPoint = pc.getBaseStr() + pc.getBaseInt()
						+ pc.getBaseWis() + pc.getBaseDex() + pc.getBaseCon()
						+ pc.getBaseCha();
				if (pc.getLevel() > 50) {
					pcStatusPoint += (pc.getLevel() - 50 - pc.getBonusStats());
				}
				int diff = pcStatusPoint - initStatusPoint;
				/**
				 * [50ç´šä»¥ä¸Š]
				 * 
				 * ç›®å‰�é»žæ•¸ - åˆ�å§‹é»žæ•¸ = äººç‰©æ‡‰æœ‰ç­‰ç´š - 50 -> äººç‰©æ‡‰æœ‰ç­‰ç´š = 50 + (ç›®å‰�é»žæ•¸ - åˆ�å§‹é»žæ•¸)
				 */
				int maxLevel = 1;

				if (diff > 0) {
					// æœ€é«˜åˆ°99ç´š:ä¹Ÿå°±æ˜¯?ä¸�æ”¯æ�´è½‰ç”Ÿ
					maxLevel = Math.min(50 + diff, 99);
				} else {
					maxLevel = pc.getLevel();
				}

				pc.setTempMaxLevel(maxLevel);
				pc.setTempLevel(1);
				pc.setInCharReset(true);
				pc.sendPackets(new S_CharReset(pc));
			} else {
				htmlid = enterUb(pc, npcId);
			}
		} else if (s.equalsIgnoreCase("par")) { // UBé–¢é€£ã€Œã‚¢ãƒ«ãƒ†ã‚£ãƒ¡ãƒƒãƒˆ ãƒ�ãƒˆãƒ«ã�«å�‚åŠ ã�™ã‚‹ã€� å‰¯ç®¡ç�†äººçµŒç”±
			htmlid = enterUb(pc, ((L1NpcInstance) obj).getNpcId());
		} else if (s.equalsIgnoreCase("info")) { // ã€Œæƒ…å ±ã‚’ç¢ºèª�ã�™ã‚‹ã€�ã€Œç«¶æŠ€æƒ…å ±ã‚’ç¢ºèª�ã�™ã‚‹ã€�
			htmlid = "colos2";
		} else if (s.equalsIgnoreCase("sco")) { // UBé–¢é€£ã€Œé«˜å¾—ç‚¹è€…ä¸€è¦§ã‚’ç¢ºèª�ã�™ã‚‹ã€�
			htmldata = new String[10];
			htmlid = "colos3";
		}

		else if (s.equalsIgnoreCase("haste")) { // ãƒ˜ã‚¤ã‚¹ãƒˆå¸«
			L1NpcInstance l1npcinstance = (L1NpcInstance) obj;
			int npcid = l1npcinstance.getNpcTemplate().get_npcId();
			if (npcid == 70514) {
				pc.sendPackets(new S_ServerMessage(183));
				pc.sendPackets(new S_SkillHaste(pc.getId(), 1, 1600));
				pc.broadcastPacket(new S_SkillHaste(pc.getId(), 1, 0));
				pc.sendPackets(new S_SkillSound(pc.getId(), 755));
				pc.broadcastPacket(new S_SkillSound(pc.getId(), 755));
				pc.setMoveSpeed(1);
				pc.setSkillEffect(STATUS_HASTE, 1600 * 1000);
				htmlid = ""; // ã‚¦ã‚£ãƒ³ãƒ‰ã‚¦ã‚’æ¶ˆã�™
			}
		}
		// å¤‰èº«å°‚é–€å®¶
		else if (s.equalsIgnoreCase("skeleton nbmorph")) {
			poly(client, 2374);
			htmlid = ""; // ã‚¦ã‚£ãƒ³ãƒ‰ã‚¦ã‚’æ¶ˆã�™
		} else if (s.equalsIgnoreCase("lycanthrope nbmorph")) {
			poly(client, 3874);
			htmlid = ""; // ã‚¦ã‚£ãƒ³ãƒ‰ã‚¦ã‚’æ¶ˆã�™
		} else if (s.equalsIgnoreCase("shelob nbmorph")) {
			poly(client, 95);
			htmlid = ""; // ã‚¦ã‚£ãƒ³ãƒ‰ã‚¦ã‚’æ¶ˆã�™
		} else if (s.equalsIgnoreCase("ghoul nbmorph")) {
			poly(client, 3873);
			htmlid = ""; // ã‚¦ã‚£ãƒ³ãƒ‰ã‚¦ã‚’æ¶ˆã�™
		} else if (s.equalsIgnoreCase("ghast nbmorph")) {
			poly(client, 3875);
			htmlid = ""; // ã‚¦ã‚£ãƒ³ãƒ‰ã‚¦ã‚’æ¶ˆã�™
		} else if (s.equalsIgnoreCase("atuba orc nbmorph")) {
			poly(client, 3868);
			htmlid = ""; // ã‚¦ã‚£ãƒ³ãƒ‰ã‚¦ã‚’æ¶ˆã�™
		} else if (s.equalsIgnoreCase("skeleton axeman nbmorph")) {
			poly(client, 2376);
			htmlid = ""; // ã‚¦ã‚£ãƒ³ãƒ‰ã‚¦ã‚’æ¶ˆã�™
		} else if (s.equalsIgnoreCase("troll nbmorph")) {
			poly(client, 3878);
			htmlid = ""; // ã‚¦ã‚£ãƒ³ãƒ‰ã‚¦ã‚’æ¶ˆã�™
		}

		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71095) { // å¢®è�½çš„é�ˆé­‚
			if (s.equalsIgnoreCase("teleport evil-dungeon")) { // å¾€é‚ªå¿µåœ°ç›£
				boolean find = false;
				for (Object objs : L1World.getInstance().getVisibleObjects(306).values()) {
					if (objs instanceof L1PcInstance) {
						L1PcInstance _pc = (L1PcInstance) objs;
						if (_pc != null) {
							find = true;
							htmlid = "csoulqn"; // ä½ çš„é‚ªå¿µé‚„ä¸�å¤ ï¼�
							break;
						}
					}
				}
				if (!find) {
					L1Quest quest = pc.getQuest();
					int lv50_step = quest.get_step(L1Quest.QUEST_LEVEL50);
					if (lv50_step == L1Quest.QUEST_END) {
						htmlid = "csoulq3";
					} else if (lv50_step >= 3) {
						L1Teleport.teleport(pc, 32747, 32799, (short) 306, 6, true);
					} else {
						htmlid = "csoulq2";
					}
				}
			}
		}
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 81279) { // å�¡ç‘ž
																					// -
																					// å�¡ç‘žçš„ç¥�ç¦�
			if (s.equalsIgnoreCase("a")) {
				// å�¡ç‘žçš„ç¥�ç¦�å·²ç¶“ç’°ç¹žæ•´å€‹èº«è»€
				L1BuffUtil.effectBlessOfDragonSlayer(pc, EFFECT_BLESS_OF_CRAY,
						2400, 7681);
				htmlid = "grayknight2";
			}
		} else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 81292) { // å�—å’€å’’çš„å·«å¥³èŽŽçˆ¾
																					// -
																					// èŽŽçˆ¾çš„ç¥�ç¦�
			if (s.equalsIgnoreCase("a")) {
				// å·«å¥³èŽŽçˆ¾çš„ç¥�ç¦�çº�ç¹žè‘—æ•´å€‹èº«é«”ã€‚
				L1BuffUtil.effectBlessOfDragonSlayer(pc, EFFECT_BLESS_OF_SAELL,
						2400, 7680);
				htmlid = "";
			}
		}
		// é•·è€� ãƒŽãƒŠãƒ¡
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71038) {
			// ã€Œæ‰‹ç´™ã‚’å�—ã�‘å�–ã‚‹ã€�
			if (s.equalsIgnoreCase("A")) {
				L1NpcInstance npc = (L1NpcInstance) obj;
				L1ItemInstance item = pc.getInventory().storeItem(41060, 1); // ãƒŽãƒŠãƒ¡ã�®æŽ¨è–¦æ›¸
				String npcName = npc.getNpcTemplate().get_name();
				String itemName = item.getItem().getName();
				pc.sendPackets(new S_ServerMessage(143, npcName, itemName)); // \f1%0ã�Œ%1ã‚’ã��ã‚Œã�¾ã�—ã�Ÿã€‚
				htmlid = "orcfnoname9";
			}
			// ã€Œèª¿æŸ»ã‚’ã‚„ã‚�ã�¾ã�™ã€�
			else if (s.equalsIgnoreCase("Z")) {
				if (pc.getInventory().consumeItem(41060, 1)) {
					htmlid = "orcfnoname11";
				}
			}
		}
		// ãƒ‰ã‚¥ãƒ€-ãƒžãƒ© ãƒ–ã‚¦
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71039) {
			// ã€Œã‚�ã�‹ã‚Šã�¾ã�—ã�Ÿã€�ã��ã�®å ´æ‰€ã�«é€�ã�£ã�¦ã��ã� ã�•ã�„ã€�
			if (s.equalsIgnoreCase("teleportURL")) {
				htmlid = "orcfbuwoo2";
			}
		}
		// èª¿æŸ»å›£é•· ã‚¢ãƒˆã‚¥ãƒ� ãƒŽã‚¢
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71040) {
			// ã€Œã‚„ã�£ã�¦ã�¿ã�¾ã�™ã€�
			if (s.equalsIgnoreCase("A")) {
				L1NpcInstance npc = (L1NpcInstance) obj;
				L1ItemInstance item = pc.getInventory().storeItem(41065, 1); // èª¿æŸ»å›£ã�®è¨¼æ›¸
				String npcName = npc.getNpcTemplate().get_name();
				String itemName = item.getItem().getName();
				pc.sendPackets(new S_ServerMessage(143, npcName, itemName)); // \f1%0ã�Œ%1ã‚’ã��ã‚Œã�¾ã�—ã�Ÿã€‚
				htmlid = "orcfnoa4";
			}
			// ã€Œèª¿æŸ»ã‚’ã‚„ã‚�ã�¾ã�™ã€�
			else if (s.equalsIgnoreCase("Z")) {
				if (pc.getInventory().consumeItem(41065, 1)) {
					htmlid = "orcfnoa7";
				}
			}
		}
		// ãƒ�ãƒ«ã‚¬ ãƒ•ã‚¦ãƒ¢
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71041) {
			// ã€Œèª¿æŸ»ã‚’ã�—ã�¾ã�™ã€�
			if (s.equalsIgnoreCase("A")) {
				L1NpcInstance npc = (L1NpcInstance) obj;
				L1ItemInstance item = pc.getInventory().storeItem(41064, 1); // èª¿æŸ»å›£ã�®è¨¼æ›¸
				String npcName = npc.getNpcTemplate().get_name();
				String itemName = item.getItem().getName();
				pc.sendPackets(new S_ServerMessage(143, npcName, itemName)); // \f1%0ã�Œ%1ã‚’ã��ã‚Œã�¾ã�—ã�Ÿã€‚
				htmlid = "orcfhuwoomo4";
			}
			// ã€Œèª¿æŸ»ã‚’ã‚„ã‚�ã�¾ã�™ã€�
			else if (s.equalsIgnoreCase("Z")) {
				if (pc.getInventory().consumeItem(41064, 1)) {
					htmlid = "orcfhuwoomo6";
				}
			}
		}
		// ãƒ�ãƒ«ã‚¬ ãƒ�ã‚¯ãƒ¢
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71042) {
			// ã€Œèª¿æŸ»ã‚’ã�—ã�¾ã�™ã€�
			if (s.equalsIgnoreCase("A")) {
				L1NpcInstance npc = (L1NpcInstance) obj;
				L1ItemInstance item = pc.getInventory().storeItem(41062, 1); // èª¿æŸ»å›£ã�®è¨¼æ›¸
				String npcName = npc.getNpcTemplate().get_name();
				String itemName = item.getItem().getName();
				pc.sendPackets(new S_ServerMessage(143, npcName, itemName)); // \f1%0ã�Œ%1ã‚’ã��ã‚Œã�¾ã�—ã�Ÿã€‚
				htmlid = "orcfbakumo4";
			}
			// ã€Œèª¿æŸ»ã‚’ã‚„ã‚�ã�¾ã�™ã€�
			else if (s.equalsIgnoreCase("Z")) {
				if (pc.getInventory().consumeItem(41062, 1)) {
					htmlid = "orcfbakumo6";
				}
			}
		}
		// ãƒ‰ã‚¥ãƒ€-ãƒžãƒ© ãƒ–ã‚«
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71043) {
			// ã€Œèª¿æŸ»ã‚’ã�—ã�¾ã�™ã€�
			if (s.equalsIgnoreCase("A")) {
				L1NpcInstance npc = (L1NpcInstance) obj;
				L1ItemInstance item = pc.getInventory().storeItem(41063, 1); // èª¿æŸ»å›£ã�®è¨¼æ›¸
				String npcName = npc.getNpcTemplate().get_name();
				String itemName = item.getItem().getName();
				pc.sendPackets(new S_ServerMessage(143, npcName, itemName)); // \f1%0ã�Œ%1ã‚’ã��ã‚Œã�¾ã�—ã�Ÿã€‚
				htmlid = "orcfbuka4";
			}
			// ã€Œèª¿æŸ»ã‚’ã‚„ã‚�ã�¾ã�™ã€�
			else if (s.equalsIgnoreCase("Z")) {
				if (pc.getInventory().consumeItem(41063, 1)) {
					htmlid = "orcfbuka6";
				}
			}
		}
		// ãƒ‰ã‚¥ãƒ€-ãƒžãƒ© ã‚«ãƒ¡
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71044) {
			// ã€Œèª¿æŸ»ã‚’ã�—ã�¾ã�™ã€�
			if (s.equalsIgnoreCase("A")) {
				L1NpcInstance npc = (L1NpcInstance) obj;
				L1ItemInstance item = pc.getInventory().storeItem(41061, 1); // èª¿æŸ»å›£ã�®è¨¼æ›¸
				String npcName = npc.getNpcTemplate().get_name();
				String itemName = item.getItem().getName();
				pc.sendPackets(new S_ServerMessage(143, npcName, itemName)); // \f1%0ã�Œ%1ã‚’ã��ã‚Œã�¾ã�—ã�Ÿã€‚
				htmlid = "orcfkame4";
			}
			// ã€Œèª¿æŸ»ã‚’ã‚„ã‚�ã�¾ã�™ã€�
			else if (s.equalsIgnoreCase("Z")) {
				if (pc.getInventory().consumeItem(41061, 1)) {
					htmlid = "orcfkame6";
				}
			}
		}



        // ãƒ�ãƒ¯ãƒ¼ãƒ«
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71078) {
			// ã€Œå…¥ã�£ã�¦ã�¿ã‚‹ã€�
			if (s.equalsIgnoreCase("teleportURL")) {
				htmlid = "usender2";
			}
		}
		// æ²»å®‰å›£é•·ã‚¢ãƒŸã‚¹
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71080) {
			// ã€Œç§�ã�Œã�Šæ‰‹ä¼�ã�„ã�—ã�¾ã�—ã‚‡ã�†ã€�
			if (s.equalsIgnoreCase("teleportURL")) {
				htmlid = "amisoo2";
			}
		}
		// ç©ºé–“ã�®æ­ªã�¿
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80048) {
			// ã€Œã‚„ã‚�ã‚‹ã€�
			if (s.equalsIgnoreCase("2")) {
				htmlid = ""; // ã‚¦ã‚£ãƒ³ãƒ‰ã‚¦ã‚’æ¶ˆã�™
			}
		}
		// æ�ºã‚‰ã��è€…
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80049) {
			// ã€Œãƒ�ãƒ«ãƒ­ã‚°ã�®æ„�å¿—ã‚’è¿Žã�ˆå…¥ã‚Œã‚‹ã€�
			if (s.equalsIgnoreCase("1")) {
				if (pc.getKarma() <= -10000000) {
					pc.setKarma(1000000);
					// ãƒ�ãƒ«ãƒ­ã‚°ã�®ç¬‘ã�„å£°ã�Œè„³è£�ã‚’å¼·æ‰“ã�—ã�¾ã�™ã€‚
					pc.sendPackets(new S_ServerMessage(1078));
					htmlid = "betray13";
				}
			}
		}
		// ãƒ¤ãƒ’ã�®åŸ·æ”¿å®˜
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80050) {
			// ã€Œç§�ã�®éœŠé­‚ã�¯ãƒ¤ãƒ’æ§˜ã�¸â€¦ã€�
			if (s.equalsIgnoreCase("1")) {
				htmlid = "meet105";
			}
			// ã€Œç§�ã�®éœŠé­‚ã‚’ã�‹ã�‘ã�¦ãƒ¤ãƒ’æ§˜ã�«å¿ èª ã‚’èª“ã�„ã�¾ã�™â€¦ã€�
			else if (s.equalsIgnoreCase("2")) {
				if (pc.getInventory().checkItem(40718)) { // ãƒ–ãƒ©ãƒƒãƒ‰ã‚¯ãƒªã‚¹ã‚¿ãƒ«ã�®æ¬ ç‰‡
					htmlid = "meet106";
				} else {
					htmlid = "meet110";
				}
			}
			// ã€Œãƒ–ãƒ©ãƒƒãƒ‰ã‚¯ãƒªã‚¹ã‚¿ãƒ«ã�®æ¬ ç‰‡ã‚’1å€‹æ�§ã�’ã�¾ã�™ã€�
			else if (s.equalsIgnoreCase("a")) {
				if (pc.getInventory().consumeItem(40718, 1)) {
					pc.addKarma((int) (-100 * Config.RATE_KARMA));
					// ãƒ¤ãƒ’ã�®å§¿ã�Œã� ã‚“ã� ã‚“è¿‘ã��ã�«æ„Ÿã�˜ã‚‰ã‚Œã�¾ã�™ã€‚
					pc.sendPackets(new S_ServerMessage(1079));
					htmlid = "meet107";
				} else {
					htmlid = "meet104";
				}
			}
			// ã€Œãƒ–ãƒ©ãƒƒãƒ‰ã‚¯ãƒªã‚¹ã‚¿ãƒ«ã�®æ¬ ç‰‡ã‚’10å€‹æ�§ã�’ã�¾ã�™ã€�
			else if (s.equalsIgnoreCase("b")) {
				if (pc.getInventory().consumeItem(40718, 10)) {
					pc.addKarma((int) (-1000 * Config.RATE_KARMA));
					// ãƒ¤ãƒ’ã�®å§¿ã�Œã� ã‚“ã� ã‚“è¿‘ã��ã�«æ„Ÿã�˜ã‚‰ã‚Œã�¾ã�™ã€‚
					pc.sendPackets(new S_ServerMessage(1079));
					htmlid = "meet108";
				} else {
					htmlid = "meet104";
				}
			}
			// ã€Œãƒ–ãƒ©ãƒƒãƒ‰ã‚¯ãƒªã‚¹ã‚¿ãƒ«ã�®æ¬ ç‰‡ã‚’100å€‹æ�§ã�’ã�¾ã�™ã€�
			else if (s.equalsIgnoreCase("c")) {
				if (pc.getInventory().consumeItem(40718, 100)) {
					pc.addKarma((int) (-10000 * Config.RATE_KARMA));
					// ãƒ¤ãƒ’ã�®å§¿ã�Œã� ã‚“ã� ã‚“è¿‘ã��ã�«æ„Ÿã�˜ã‚‰ã‚Œã�¾ã�™ã€‚
					pc.sendPackets(new S_ServerMessage(1079));
					htmlid = "meet109";
				} else {
					htmlid = "meet104";
				}
			}
			// ã€Œãƒ¤ãƒ’æ§˜ã�«ä¼šã‚�ã�›ã�¦ã��ã� ã�•ã�„ã€�
			else if (s.equalsIgnoreCase("d")) {
				if (pc.getInventory().checkItem(40615) // å½±ã�®ç¥žæ®¿2éšŽã�®é�µ
						|| pc.getInventory().checkItem(40616)) { // å½±ã�®ç¥žæ®¿3éšŽã�®é�µ
					htmlid = "";
				} else {
					L1Teleport.teleport(pc, 32683, 32895, (short) 608, 5, true);
				}
			}
		} else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80052) { // ç�«ç„°ä¹‹å½±çš„è»�å¸«
			if (s.equalsIgnoreCase("a")) { // è«‹è³œçµ¦æˆ‘åŠ›é‡�
				if (pc.hasSkillEffect(STATUS_CURSE_BARLOG)) { // ç�«ç„°ä¹‹å½±çš„çƒ™å�°
					pc.killSkillEffectTimer(STATUS_CURSE_BARLOG);
				}
				pc.sendPackets(new S_SkillSound(pc.getId(), 750));
				pc.broadcastPacket(new S_SkillSound(pc.getId(), 750));
				pc.sendPackets(new S_SkillIconAura(221, 1020, 2)); // ç�«ç„°ä¹‹å½±çš„çƒ™å�°
				pc.setSkillEffect(STATUS_CURSE_BARLOG, 1020 * 1000);
				pc.sendPackets(new S_ServerMessage(1127));
				htmlid = "";
			}
		}
		// ãƒ¤ãƒ’ã�®é�›å†¶å±‹
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80053) {
			// ã€Œæ��æ–™ã�™ã�¹ã�¦ã‚’ç”¨æ„�ã�§ã��ã�¾ã�—ã�Ÿã€�
			if (s.equalsIgnoreCase("a")) {
				// ãƒ�ãƒ«ãƒ­ã‚°ã�®ãƒ„ãƒ¼ãƒ�ãƒ³ãƒ‰ ã‚½ãƒ¼ãƒ‰ / ãƒ¤ãƒ’ã�®é�›å†¶å±‹
				int aliceMaterialId = 0;
				int karmaLevel = 0;
				int[] material = null;
				int[] count = null;
				int createItem = 0;
				String successHtmlId = null;
				String htmlId = null;

				int[] aliceMaterialIdList = { 40991, 196, 197, 198, 199, 200,
						201, 202 };
				int[] karmaLevelList = { -1, -2, -3, -4, -5, -6, -7, -8 };
				int[][] materialsList = { { 40995, 40718, 40991 },
						{ 40997, 40718, 196 }, { 40990, 40718, 197 },
						{ 40994, 40718, 198 }, { 40993, 40718, 199 },
						{ 40998, 40718, 200 }, { 40996, 40718, 201 },
						{ 40992, 40718, 202 } };
				int[][] countList = { { 100, 100, 1 }, { 100, 100, 1 },
						{ 100, 100, 1 }, { 50, 100, 1 }, { 50, 100, 1 },
						{ 50, 100, 1 }, { 10, 100, 1 }, { 10, 100, 1 } };
				int[] createItemList = { 196, 197, 198, 199, 200, 201, 202, 203 };
				String[] successHtmlIdList = { "alice_1", "alice_2", "alice_3",
						"alice_4", "alice_5", "alice_6", "alice_7", "alice_8" };
				String[] htmlIdList = { "aliceyet", "alice_1", "alice_2",
						"alice_3", "alice_4", "alice_5", "alice_5", "alice_7" };

				for (int i = 0; i < aliceMaterialIdList.length; i++) {
					if (pc.getInventory().checkItem(aliceMaterialIdList[i])) {
						aliceMaterialId = aliceMaterialIdList[i];
						karmaLevel = karmaLevelList[i];
						material = materialsList[i];
						count = countList[i];
						createItem = createItemList[i];
						successHtmlId = successHtmlIdList[i];
						htmlId = htmlIdList[i];
						break;
					}
				}

				if (aliceMaterialId == 0) {
					htmlid = "alice_no";
				} else if (aliceMaterialId == 203) {
					htmlid = "alice_8";
				} else {
					if (pc.getKarmaLevel() <= karmaLevel) {
						materials = material;
						counts = count;
						createitem = new int[] { createItem };
						createcount = new int[] { 1 };
						success_htmlid = successHtmlId;
						failure_htmlid = "alice_no";
					} else {
						htmlid = htmlId;
					}
				}
			}
		}
		// ãƒ¤ãƒ’ã�®è£œä½�å®˜
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80055) {
			L1NpcInstance npc = (L1NpcInstance) obj;
			htmlid = getYaheeAmulet(pc, npc, s);
		}
		// æ¥­ã�®ç®¡ç�†è€…
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80056) {
			L1NpcInstance npc = (L1NpcInstance) obj;
			if (pc.getKarma() <= -10000000) {
				getBloodCrystalByKarma(pc, npc, s);
			}
			htmlid = "";
		}
		// æ¬¡å…ƒã�®æ‰‰(ãƒ�ãƒ«ãƒ­ã‚°ã�®éƒ¨å±‹)
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80063) {
			// ã€Œä¸­ã�«å…¥ã‚‹ã€�
			if (s.equalsIgnoreCase("a")) {
				if (pc.getInventory().checkItem(40921)) { // å…ƒç´ ã�®æ”¯é…�è€…
					L1Teleport.teleport(pc, 32674, 32832, (short) 603, 2, true);
				} else {
					htmlid = "gpass02";
				}
			}
		}
		// ãƒ�ãƒ«ãƒ­ã‚°ã�®åŸ·æ”¿å®˜
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80064) {
			// ã€Œç§�ã�®æ°¸é� ã�®ä¸»ã�¯ãƒ�ãƒ«ãƒ­ã‚°æ§˜ã� ã�‘ã�§ã�™â€¦ã€�
			if (s.equalsIgnoreCase("1")) {
				htmlid = "meet005";
			}
			// ã€Œç§�ã�®éœŠé­‚ã‚’ã�‹ã�‘ã�¦ãƒ�ãƒ«ãƒ­ã‚°æ§˜ã�«å¿ èª ã‚’èª“ã�„ã�¾ã�™â€¦ã€�
			else if (s.equalsIgnoreCase("2")) {
				if (pc.getInventory().checkItem(40678)) { // ã‚½ã‚¦ãƒ«ã‚¯ãƒªã‚¹ã‚¿ãƒ«ã�®æ¬ ç‰‡
					htmlid = "meet006";
				} else {
					htmlid = "meet010";
				}
			}
			// ã€Œã‚½ã‚¦ãƒ«ã‚¯ãƒªã‚¹ã‚¿ãƒ«ã�®æ¬ ç‰‡ã‚’1å€‹æ�§ã�’ã�¾ã�™ã€�
			else if (s.equalsIgnoreCase("a")) {
				if (pc.getInventory().consumeItem(40678, 1)) {
					pc.addKarma((int) (100 * Config.RATE_KARMA));
					// ãƒ�ãƒ«ãƒ­ã‚°ã�®ç¬‘ã�„å£°ã�Œè„³è£�ã‚’å¼·æ‰“ã�—ã�¾ã�™ã€‚
					pc.sendPackets(new S_ServerMessage(1078));
					htmlid = "meet007";
				} else {
					htmlid = "meet004";
				}
			}
			// ã€Œã‚½ã‚¦ãƒ«ã‚¯ãƒªã‚¹ã‚¿ãƒ«ã�®æ¬ ç‰‡ã‚’10å€‹æ�§ã�’ã�¾ã�™ã€�
			else if (s.equalsIgnoreCase("b")) {
				if (pc.getInventory().consumeItem(40678, 10)) {
					pc.addKarma((int) (1000 * Config.RATE_KARMA));
					// ãƒ�ãƒ«ãƒ­ã‚°ã�®ç¬‘ã�„å£°ã�Œè„³è£�ã‚’å¼·æ‰“ã�—ã�¾ã�™ã€‚
					pc.sendPackets(new S_ServerMessage(1078));
					htmlid = "meet008";
				} else {
					htmlid = "meet004";
				}
			}
			// ã€Œã‚½ã‚¦ãƒ«ã‚¯ãƒªã‚¹ã‚¿ãƒ«ã�®æ¬ ç‰‡ã‚’100å€‹æ�§ã�’ã�¾ã�™ã€�
			else if (s.equalsIgnoreCase("c")) {
				if (pc.getInventory().consumeItem(40678, 100)) {
					pc.addKarma((int) (10000 * Config.RATE_KARMA));
					// ãƒ�ãƒ«ãƒ­ã‚°ã�®ç¬‘ã�„å£°ã�Œè„³è£�ã‚’å¼·æ‰“ã�—ã�¾ã�™ã€‚
					pc.sendPackets(new S_ServerMessage(1078));
					htmlid = "meet009";
				} else {
					htmlid = "meet004";
				}
			}
			// ã€Œãƒ�ãƒ«ãƒ­ã‚°æ§˜ã�«ä¼šã‚�ã�›ã�¦ã��ã� ã�•ã�„ã€�
			else if (s.equalsIgnoreCase("d")) {
				if (pc.getInventory().checkItem(40909) // åœ°ã�®é€šè¡Œè¨¼
						|| pc.getInventory().checkItem(40910) // æ°´ã�®é€šè¡Œè¨¼
						|| pc.getInventory().checkItem(40911) // ç�«ã�®é€šè¡Œè¨¼
						|| pc.getInventory().checkItem(40912) // é¢¨ã�®é€šè¡Œè¨¼
						|| pc.getInventory().checkItem(40913) // åœ°ã�®å�°ç« 
						|| pc.getInventory().checkItem(40914) // æ°´ã�®å�°ç« 
						|| pc.getInventory().checkItem(40915) // ç�«ã�®å�°ç« 
						|| pc.getInventory().checkItem(40916) // é¢¨ã�®å�°ç« 
						|| pc.getInventory().checkItem(40917) // åœ°ã�®æ”¯é…�è€…
						|| pc.getInventory().checkItem(40918) // æ°´ã�®æ”¯é…�è€…
						|| pc.getInventory().checkItem(40919) // ç�«ã�®æ”¯é…�è€…
						|| pc.getInventory().checkItem(40920) // é¢¨ã�®æ”¯é…�è€…
						|| pc.getInventory().checkItem(40921)) { // å…ƒç´ ã�®æ”¯é…�è€…
					htmlid = "";
				} else {
					L1Teleport.teleport(pc, 32674, 32832, (short) 602, 2, true);
				}
			}
		}
		// æ�ºã‚‰ã‚�ã��è€…
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80066) {
			// ã€Œã‚«ãƒ˜ãƒ«ã�®æ„�å¿—ã‚’å�—ã�‘å…¥ã‚Œã‚‹ã€�
			if (s.equalsIgnoreCase("1")) {
				if (pc.getKarma() >= 10000000) {
					pc.setKarma(-1000000);
					// ãƒ¤ãƒ’ã�®å§¿ã�Œã� ã‚“ã� ã‚“è¿‘ã��ã�«æ„Ÿã�˜ã‚‰ã‚Œã�¾ã�™ã€‚
					pc.sendPackets(new S_ServerMessage(1079));
					htmlid = "betray03";
				}
			}
		}
		// ãƒ�ãƒ«ãƒ­ã‚°ã�®è£œä½�å®˜
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80071) {
			L1NpcInstance npc = (L1NpcInstance) obj;
			htmlid = getBarlogEarring(pc, npc, s);
		} else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80073) { // ç‚Žé­”çš„è»�å¸«
			if (s.equalsIgnoreCase("a")) { // è«‹çµ¦æˆ‘åŠ›é‡�
				if (pc.hasSkillEffect(STATUS_CURSE_YAHEE)) { // ç‚Žé­”çš„çƒ™å�°
					pc.killSkillEffectTimer(STATUS_CURSE_YAHEE);
				}
				pc.sendPackets(new S_SkillSound(pc.getId(), 750));
				pc.broadcastPacket(new S_SkillSound(pc.getId(), 750));
				pc.sendPackets(new S_SkillIconAura(221, 1020, 1)); // ç‚Žé­”çš„çƒ™å�°
				pc.setSkillEffect(STATUS_CURSE_YAHEE, 1020 * 1000);
				pc.sendPackets(new S_ServerMessage(1127));
				htmlid = "";
			}
		}
		// ãƒ�ãƒ«ãƒ­ã‚°ã�®é�›å†¶å±‹
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80072) {
			String sEquals = null;
			int karmaLevel = 0;
			int[] material = null;
			int[] count = null;
			int createItem = 0;
			String failureHtmlId = null;
			String htmlId = null;

			String[] sEqualsList = { "0", "1", "2", "3", "4", "5", "6", "7",
					"8", "a", "b", "c", "d", "e", "f", "g", "h" };
			String[] htmlIdList = { "lsmitha", "lsmithb", "lsmithc", "lsmithd",
					"lsmithe", "", "lsmithf", "lsmithg", "lsmithh" };
			int[] karmaLevelList = { 1, 2, 3, 4, 5, 6, 7, 8 };
			int[][] materialsList = { { 20158, 40669, 40678 },
					{ 20144, 40672, 40678 }, { 20075, 40671, 40678 },
					{ 20183, 40674, 40678 }, { 20190, 40674, 40678 },
					{ 20078, 40674, 40678 }, { 20078, 40670, 40678 },
					{ 40719, 40673, 40678 } };
			int[][] countList = { { 1, 50, 100 }, { 1, 50, 100 },
					{ 1, 50, 100 }, { 1, 20, 100 }, { 1, 40, 100 },
					{ 1, 5, 100 }, { 1, 1, 100 }, { 1, 1, 100 } };
			int[] createItemList = { 20083, 20131, 20069, 20179, 20209, 20290,
					20261, 20031 };
			String[] failureHtmlIdList = { "lsmithaa", "lsmithbb", "lsmithcc",
					"lsmithdd", "lsmithee", "lsmithff", "lsmithgg", "lsmithhh" };

			for (int i = 0; i < sEqualsList.length; i++) {
				if (s.equalsIgnoreCase(sEqualsList[i])) {
					sEquals = sEqualsList[i];
					if (i <= 8) {
						htmlId = htmlIdList[i];
					} else if (i > 8) {
						karmaLevel = karmaLevelList[i - 9];
						material = materialsList[i - 9];
						count = countList[i - 9];
						createItem = createItemList[i - 9];
						failureHtmlId = failureHtmlIdList[i - 9];
					}
					break;
				}
			}
			if (s.equalsIgnoreCase(sEquals)) {
				if ((karmaLevel != 0) && (pc.getKarmaLevel() >= karmaLevel)) {
					materials = material;
					counts = count;
					createitem = new int[] { createItem };
					createcount = new int[] { 1 };
					success_htmlid = "";
					failure_htmlid = failureHtmlId;
				} else {
					htmlid = htmlId;
				}
			}
		}
		// æ¥­ã�®ç®¡ç�†è€…
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80074) {
			L1NpcInstance npc = (L1NpcInstance) obj;
			if (pc.getKarma() >= 10000000) {
				getSoulCrystalByKarma(pc, npc, s);
			}
			htmlid = "";
		}
		// ã‚¢ãƒ«ãƒ•ã‚©ãƒ³ã‚¹
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80057) {
			htmlid = karmaLevelToHtmlId(pc.getKarmaLevel());
			htmldata = new String[] { String.valueOf(pc.getKarmaPercent()) };
		}
		// æ¬¡å…ƒã�®æ‰‰(åœŸé¢¨æ°´ç�«)
		else if ((((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80059)
				|| (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80060)
				|| (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80061)
				|| (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80062)) {
			htmlid = talkToDimensionDoor(pc, (L1NpcInstance) obj, s);
		}
		// ã‚¸ãƒ£ãƒƒã‚¯ ã‚ª ãƒ©ãƒ³ã‚¿ãƒ³
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 81124) {
			if (s.equalsIgnoreCase("1")) {
				poly(client, 4002);
				htmlid = ""; // ã‚¦ã‚£ãƒ³ãƒ‰ã‚¦ã‚’æ¶ˆã�™
			} else if (s.equalsIgnoreCase("2")) {
				poly(client, 4004);
				htmlid = ""; // ã‚¦ã‚£ãƒ³ãƒ‰ã‚¦ã‚’æ¶ˆã�™
			} else if (s.equalsIgnoreCase("3")) {
				poly(client, 4950);
				htmlid = ""; // ã‚¦ã‚£ãƒ³ãƒ‰ã‚¦ã‚’æ¶ˆã�™
			}
		}

		// ã‚¯ã‚¨ã‚¹ãƒˆé–¢é€£
		// ä¸€èˆ¬ã‚¯ã‚¨ã‚¹ãƒˆ / ãƒ©ã‚¤ãƒ©
		else if (s.equalsIgnoreCase("contract1")) {
			pc.getQuest().set_step(L1Quest.QUEST_LYRA, 1);
			htmlid = "lyraev2";
		} else if (s.equalsIgnoreCase("contract1yes") || // ãƒ©ã‚¤ãƒ© Yes
				s.equalsIgnoreCase("contract1no")) { // ãƒ©ã‚¤ãƒ© No

			if (s.equalsIgnoreCase("contract1yes")) {
				htmlid = "lyraev5";
			} else if (s.equalsIgnoreCase("contract1no")) {
				pc.getQuest().set_step(L1Quest.QUEST_LYRA, 0);
				htmlid = "lyraev4";
			}
			int totem = 0;
			if (pc.getInventory().checkItem(40131)) {
				totem++;
			}
			if (pc.getInventory().checkItem(40132)) {
				totem++;
			}
			if (pc.getInventory().checkItem(40133)) {
				totem++;
			}
			if (pc.getInventory().checkItem(40134)) {
				totem++;
			}
			if (pc.getInventory().checkItem(40135)) {
				totem++;
			}
			if (totem != 0) {
				materials = new int[totem];
				counts = new int[totem];
				createitem = new int[totem];
				createcount = new int[totem];

				totem = 0;
				if (pc.getInventory().checkItem(40131)) {
					L1ItemInstance l1iteminstance = pc.getInventory()
							.findItemId(40131);
					int i1 = l1iteminstance.getCount();
					materials[totem] = 40131;
					counts[totem] = i1;
					createitem[totem] = L1ItemId.ADENA;
					createcount[totem] = i1 * 50;
					totem++;
				}
				if (pc.getInventory().checkItem(40132)) {
					L1ItemInstance l1iteminstance = pc.getInventory()
							.findItemId(40132);
					int i1 = l1iteminstance.getCount();
					materials[totem] = 40132;
					counts[totem] = i1;
					createitem[totem] = L1ItemId.ADENA;
					createcount[totem] = i1 * 100;
					totem++;
				}
				if (pc.getInventory().checkItem(40133)) {
					L1ItemInstance l1iteminstance = pc.getInventory()
							.findItemId(40133);
					int i1 = l1iteminstance.getCount();
					materials[totem] = 40133;
					counts[totem] = i1;
					createitem[totem] = L1ItemId.ADENA;
					createcount[totem] = i1 * 50;
					totem++;
				}
				if (pc.getInventory().checkItem(40134)) {
					L1ItemInstance l1iteminstance = pc.getInventory()
							.findItemId(40134);
					int i1 = l1iteminstance.getCount();
					materials[totem] = 40134;
					counts[totem] = i1;
					createitem[totem] = L1ItemId.ADENA;
					createcount[totem] = i1 * 30;
					totem++;
				}
				if (pc.getInventory().checkItem(40135)) {
					L1ItemInstance l1iteminstance = pc.getInventory()
							.findItemId(40135);
					int i1 = l1iteminstance.getCount();
					materials[totem] = 40135;
					counts[totem] = i1;
					createitem[totem] = L1ItemId.ADENA;
					createcount[totem] = i1 * 200;
					totem++;
				}
			}
		}
		// æœ€è¿‘ç‰©åƒ¹å€�çŽ‡
		else if (s.equalsIgnoreCase("pandora6")     // æ½˜æœµæ‹‰(èªªè©±ä¹‹å³¶ é›œè²¨å•†)
				|| s.equalsIgnoreCase("cold6")      // åº«å¾·(èªªè©±ä¹‹å³¶ ç…™ç�«å•†)
				|| s.equalsIgnoreCase("balsim3")    // å·´è¾›(èªªè©±ä¹‹å³¶ å¦–é­”å•†)
				|| s.equalsIgnoreCase("arieh6")     // 70015: è‰¾èŽ‰é›…(è‚¯ç‰¹ ç…™ç�«å•†)
				|| s.equalsIgnoreCase("andyn3")     // 70016: å®‰è¿ª(è‚¯ç‰¹ æ­¦å™¨å•†)
				|| s.equalsIgnoreCase("ysorya3")    // 70018: ç´¢æ‹‰é›…(è‚¯ç‰¹ é›œè²¨å•†)
				|| s.equalsIgnoreCase("luth3")      // 70021: éœ²è¥¿(å�¤é­¯ä¸� é›œè²¨å•†)
				|| s.equalsIgnoreCase("catty3")     // 70024: å‡±è’‚(å�¤é­¯ä¸� æ­¦å™¨å•†)
				|| s.equalsIgnoreCase("mayer3")     // 70030: é‚�çˆ¾(å¥‡å²© é›œè²¨å•†)
				|| s.equalsIgnoreCase("vergil3")    // 70032: èŒƒå�‰çˆ¾(å¥‡å²© é˜²å…·å•†)
				|| s.equalsIgnoreCase("stella6")    // 70036: å�²å ¤æ‹‰(å¥‡å²© ç…™ç�«å•†)
				|| s.equalsIgnoreCase("ralf6")      // 70044: ç‘žç¦�(å¨�é “ æ­¦å™¨å•†)
				|| s.equalsIgnoreCase("berry6")     // 70045: è““èŽ‰(å¨�é “ é›œè²¨å•†)
				|| s.equalsIgnoreCase("jin6")       // 70046: ç��(å¨�é “ ç…™ç�«å•†)
				|| s.equalsIgnoreCase("defman3")    // 70047: æˆ´å¤«æ›¼(äºžä¸� æ­¦å™¨å•†)
				|| s.equalsIgnoreCase("mellisa3")   // 70052: é¦¬å¤�(äºžä¸� é›œè²¨å•†)
				|| s.equalsIgnoreCase("mandra3")    // 70061: æ›¼å¾·æ‹‰(æ­�ç‘ž æ­¦å™¨å•†)
				|| s.equalsIgnoreCase("bius3")      // 70063: ç•¢ä¼�æ–¯(æ­�ç‘ž é›œè²¨å•†)
				|| s.equalsIgnoreCase("momo6")      // 70069: æ‘©æ‘©(é¢¨æœ¨ ç…™ç�«å•†)
				|| s.equalsIgnoreCase("ashurEv7")   // 70071: äºžä¿®(ç¶ æ´² é›œè²¨å•†)
				|| s.equalsIgnoreCase("elmina3")    // 70072: è‰¾ç±³å¨œ(é¢¨æœ¨ é›œè²¨å•†)
				|| s.equalsIgnoreCase("glen3")      // 70073: æ ¼æž—(éŠ€é¨Žå£« æ­¦å™¨å•†)
				|| s.equalsIgnoreCase("mellin3")    // 70074: æ¢…æž—(éŠ€é¨Žå£« é›œè²¨å•†)
				|| s.equalsIgnoreCase("orcm6")      // 70078: æ­�è‚¯(ç‡ƒæŸ³ ç…™ç�«å•†)
				|| s.equalsIgnoreCase("jackson3")   // 70079: å‚‘å…‹æ£®(ç‡ƒæŸ³ é›œè²¨å•†)
				|| s.equalsIgnoreCase("britt3")     // 70082: æ¯”ç‰¹(æµ·éŸ³ é›œè²¨å•†)
				|| s.equalsIgnoreCase("old6")       // 70085: æ­�å¾—(æµ·éŸ³ ç…™ç�«å•†)
				|| s.equalsIgnoreCase("shivan3")) { // 70083: é ˆå‡¡(æµ·éŸ³ æ­¦å™¨å•†)
			htmlid = s;
			int npcid = ((L1NpcInstance) obj).getNpcTemplate().get_npcId();
			int taxRatesCastle = L1CastleLocation
					.getCastleTaxRateByNpcId(npcid);
			htmldata = new String[] { String.valueOf(taxRatesCastle) };
		}
		// ã‚¿ã‚¦ãƒ³ãƒžã‚¹ã‚¿ãƒ¼ï¼ˆã�“ã�®æ�‘ã�®ä½�æ°‘ã�«ç™»éŒ²ã�™ã‚‹ï¼‰
		else if (s.equalsIgnoreCase("set")) {
			if (obj instanceof L1NpcInstance) {
				int npcid = ((L1NpcInstance) obj).getNpcTemplate().get_npcId();
				int town_id = L1TownLocation.getTownIdByNpcid(npcid);

				if ((town_id >= 1) && (town_id <= 10)) {
					if (pc.getHomeTownId() == -1) {
						// \f1æ–°ã�—ã��ä½�æ°‘ç™»éŒ²ã‚’è¡Œã�ªã�†ã�«ã�¯æ™‚é–“ã�Œã�‹ã�‹ã‚Šã�¾ã�™ã€‚æ™‚é–“ã‚’ç½®ã�„ã�¦ã�‹ã‚‰ã�¾ã�Ÿç™»éŒ²ã�—ã�¦ã��ã� ã�•ã�„ã€‚
						pc.sendPackets(new S_ServerMessage(759));
						htmlid = "";
					} else if (pc.getHomeTownId() > 0) {
						// æ—¢ã�«ç™»éŒ²ã�—ã�¦ã‚‹
						if (pc.getHomeTownId() != town_id) {
							L1Town town = TownTable.getInstance().getTownTable(
									pc.getHomeTownId());
							if (town != null) {
								// ç�¾åœ¨ã€�ã�‚ã�ªã�Ÿã�Œä½�æ°‘ç™»éŒ²ã�—ã�¦ã�„ã‚‹å ´æ‰€ã�¯%0ã�§ã�™ã€‚
								pc.sendPackets(new S_ServerMessage(758, town
										.get_name()));
							}
							htmlid = "";
						} else {
							// ã�‚ã‚Šã�ˆã�ªã�„ï¼Ÿ
							htmlid = "";
						}
					} else if (pc.getHomeTownId() == 0) {
						// ç™»éŒ²
						if (pc.getLevel() < 10) {
							// \f1ä½�æ°‘ç™»éŒ²ã�Œã�§ã��ã‚‹ã�®ã�¯ãƒ¬ãƒ™ãƒ«10ä»¥ä¸Šã�®ã‚­ãƒ£ãƒ©ã‚¯ã‚¿ãƒ¼ã�§ã�™ã€‚
							pc.sendPackets(new S_ServerMessage(757));
							htmlid = "";
						} else {
							int level = pc.getLevel();
							int cost = level * level * 10;
							if (pc.getInventory().consumeItem(L1ItemId.ADENA,
									cost)) {
								pc.setHomeTownId(town_id);
								pc.setContribution(0); // å¿µã�®ã�Ÿã‚�
								pc.save();
							} else {
								// ã‚¢ãƒ‡ãƒŠã�Œä¸�è¶³ã�—ã�¦ã�„ã�¾ã�™ã€‚
								pc.sendPackets(new S_ServerMessage(337, "$4"));
							}
							htmlid = "";
						}
					}
				}
			}
		}
		// ã‚¿ã‚¦ãƒ³ãƒžã‚¹ã‚¿ãƒ¼ï¼ˆä½�æ°‘ç™»éŒ²ã‚’å�–ã‚Šæ¶ˆã�™ï¼‰
		else if (s.equalsIgnoreCase("clear")) {
			if (obj instanceof L1NpcInstance) {
				int npcid = ((L1NpcInstance) obj).getNpcTemplate().get_npcId();
				int town_id = L1TownLocation.getTownIdByNpcid(npcid);
				if (town_id > 0) {
					if (pc.getHomeTownId() > 0) {
						if (pc.getHomeTownId() == town_id) {
							pc.setHomeTownId(-1);
							pc.setContribution(0); // è²¢çŒ®åº¦ã‚¯ãƒªã‚¢
							pc.save();
						} else {
							// \f1ã�‚ã�ªã�Ÿã�¯ä»–ã�®æ�‘ã�®ä½�æ°‘ã�§ã�™ã€‚
							pc.sendPackets(new S_ServerMessage(756));
						}
					}
					htmlid = "";
				}
			}
		}
		// ã‚¿ã‚¦ãƒ³ãƒžã‚¹ã‚¿ãƒ¼ï¼ˆæ�‘ã�®æ�‘é•·ã�Œèª°ã�‹ã‚’è�žã��ï¼‰
		else if (s.equalsIgnoreCase("ask")) {
			if (obj instanceof L1NpcInstance) {
				int npcid = ((L1NpcInstance) obj).getNpcTemplate().get_npcId();
				int town_id = L1TownLocation.getTownIdByNpcid(npcid);

				if ((town_id >= 1) && (town_id <= 10)) {
					L1Town town = TownTable.getInstance().getTownTable(town_id);
					String leader = town.get_leader_name();
					if ((leader != null) && (leader.length() != 0)) {
						htmlid = "owner";
						htmldata = new String[] { leader };
					} else {
						htmlid = "noowner";
					}
				}
			}
		}
		// HomeTown å�„æ�‘èŽŠ å‰¯æ�‘é•· (å�–æ¶ˆå‰¯æ�‘é•· for 3.3C)
		else if ((((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70534)
				|| (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70556)
				|| (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70572)
				|| (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70631)
				|| (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70663)
				|| (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70761)
				|| (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70788)
				|| (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70806)
				|| (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70830)
				|| (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70876)) {
			// ã‚¿ã‚¦ãƒ³ã‚¢ãƒ‰ãƒ�ã‚¤ã‚¶ãƒ¼ï¼ˆå�Žå…¥ã�«é–¢ã�™ã‚‹å ±å‘Šï¼‰
			if (s.equalsIgnoreCase("r")) {
			}
			// ã‚¿ã‚¦ãƒ³ã‚¢ãƒ‰ãƒ�ã‚¤ã‚¶ãƒ¼ï¼ˆç¨ŽçŽ‡å¤‰æ›´ï¼‰
			else if (s.equalsIgnoreCase("t")) {

			}
			// ã‚¿ã‚¦ãƒ³ã‚¢ãƒ‰ãƒ�ã‚¤ã‚¶ãƒ¼ï¼ˆå ±é…¬ã‚’ã‚‚ã‚‰ã�†ï¼‰
			else if (s.equalsIgnoreCase("c")) {

			}
		}
		// ãƒ‰ãƒ­ãƒ¢ãƒ³ãƒ‰
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70997) {
			// ã�‚ã‚Šã�Œã�¨ã�†ã€�æ—…ç«‹ã�¡ã�¾ã�™
			if (s.equalsIgnoreCase("0")) {
				final int[] item_ids = { 41146, 4, 20322, 173, 40743, };
				final int[] item_amounts = { 1, 1, 1, 1, 500, };
				for (int i = 0; i < item_ids.length; i++) {
					L1ItemInstance item = pc.getInventory().storeItem(
							item_ids[i], item_amounts[i]);
					pc.sendPackets(new S_ServerMessage(143,
							((L1NpcInstance) obj).getNpcTemplate().get_name(),
							item.getLogName()));
				}
				pc.getQuest().set_step(L1Quest.QUEST_DOROMOND, 1);
				htmlid = "jpe0015";
			}
		}
		// ã‚¢ãƒ¬ãƒƒã‚¯ã‚¹(æ­Œã�†å³¶)
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70999) {
			// ãƒ‰ãƒ­ãƒ¢ãƒ³ãƒ‰ã�®ç´¹ä»‹çŠ¶ã‚’æ¸¡ã�™
			if (s.equalsIgnoreCase("1")) {
				if (pc.getInventory().consumeItem(41146, 1)) {
					final int[] item_ids = { 23, 20219, 20193, };
					final int[] item_amounts = { 1, 1, 1, };
					for (int i = 0; i < item_ids.length; i++) {
						L1ItemInstance item = pc.getInventory().storeItem(
								item_ids[i], item_amounts[i]);
						pc.sendPackets(new S_ServerMessage(143,
								((L1NpcInstance) obj).getNpcTemplate()
										.get_name(), item.getLogName()));
					}
					pc.getQuest().set_step(L1Quest.QUEST_DOROMOND, 2);
					htmlid = "";
				}
			} else if (s.equalsIgnoreCase("2")) {
				L1ItemInstance item = pc.getInventory().storeItem(41227, 1); // ã‚¢ãƒ¬ãƒƒã‚¯ã‚¹ã�®ç´¹ä»‹çŠ¶
				pc.sendPackets(new S_ServerMessage(143, ((L1NpcInstance) obj)
						.getNpcTemplate().get_name(), item.getLogName()));
				pc.getQuest().set_step(L1Quest.QUEST_AREX, L1Quest.QUEST_END);
				htmlid = "";
			}
		}
		// ãƒ�ãƒ”ãƒ¬ã‚¢(æ­Œã�†å³¶)
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71005) {
			// ã‚¢ã‚¤ãƒ†ãƒ ã‚’å�—ã�‘å�–ã‚‹
			if (s.equalsIgnoreCase("0")) {
				if (!pc.getInventory().checkItem(41209)) {
					L1ItemInstance item = pc.getInventory().storeItem(41209, 1);
					pc.sendPackets(new S_ServerMessage(143,
                            ((L1NpcInstance) obj).getNpcTemplate().get_name(),
                            item.getItem().getName()));
					htmlid = ""; // ã‚¦ã‚£ãƒ³ãƒ‰ã‚¦ã‚’æ¶ˆã�™
				}
			}
			// ã‚¢ã‚¤ãƒ†ãƒ ã‚’å�—ã�‘å�–ã‚‹
			else if (s.equalsIgnoreCase("1")) {
				if (pc.getInventory().consumeItem(41213, 1)) {
					L1ItemInstance item = pc.getInventory()
							.storeItem(40029, 20);
					pc.sendPackets(new S_ServerMessage(143,
							((L1NpcInstance) obj).getNpcTemplate().get_name(),
							item.getItem().getName() + " (" + 20 + ")"));
					htmlid = ""; // ã‚¦ã‚£ãƒ³ãƒ‰ã‚¦ã‚’æ¶ˆã�™
				}
			}
		}
		// ãƒ†ã‚£ãƒŸãƒ¼(æ­Œã�†å³¶)
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71006) {
			if (s.equalsIgnoreCase("0")) {
				if (pc.getLevel() > 25) {
					htmlid = "jpe0057";
				} else if (pc.getInventory().checkItem(41213)) { // ãƒ†ã‚£ãƒŸãƒ¼ã�®ãƒ�ã‚¹ã‚±ãƒƒãƒˆ
					htmlid = "jpe0056";
				} else if (pc.getInventory().checkItem(41210)
						|| pc.getInventory().checkItem(41211)) { // ç ”ç£¨æ��ã€�ãƒ�ãƒ¼ãƒ–
					htmlid = "jpe0055";
				} else if (pc.getInventory().checkItem(41209)) { // ãƒ�ãƒ”ãƒªã‚¢ã�®ä¾�é ¼æ›¸
					htmlid = "jpe0054";
				} else if (pc.getInventory().checkItem(41212)) { // ç‰¹è£½ã‚­ãƒ£ãƒ³ãƒ‡ã‚£ãƒ¼
					htmlid = "jpe0056";
					materials = new int[] { 41212 }; // ç‰¹è£½ã‚­ãƒ£ãƒ³ãƒ‡ã‚£ãƒ¼
					counts = new int[] { 1 };
					createitem = new int[] { 41213 }; // ãƒ†ã‚£ãƒŸãƒ¼ã�®ãƒ�ã‚¹ã‚±ãƒƒãƒˆ
					createcount = new int[] { 1 };
				} else {
					htmlid = "jpe0057";
				}
			}
		}
		// æ²»ç™‚å¸«ï¼ˆæ­Œã�†å³¶ã�®ä¸­ï¼šï¼¨ï¼°ã�®ã�¿å›žå¾©ï¼‰
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70512) {
			// æ²»ç™‚ã‚’å�—ã�‘ã‚‹("fullheal"ã�§ãƒªã‚¯ã‚¨ã‚¹ãƒˆã�Œæ�¥ã‚‹ã�“ã�¨ã�¯ã�‚ã‚‹ã�®ã�‹ï¼Ÿ)
			if (s.equalsIgnoreCase("0") || s.equalsIgnoreCase("fullheal")) {
				int hp = Random.nextInt(21) + 70;
				pc.setCurrentHp(pc.getCurrentHp() + hp);
				pc.sendPackets(new S_ServerMessage(77));
				pc.sendPackets(new S_SkillSound(pc.getId(), 830));
				pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
				htmlid = ""; // ã‚¦ã‚£ãƒ³ãƒ‰ã‚¦ã‚’æ¶ˆã�™
			}
		}
		// æ²»ç™‚å¸«ï¼ˆè¨“ç·´å ´ï¼šHPMPå›žå¾©ï¼‰
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71037) {
			if (s.equalsIgnoreCase("0")) {
				pc.setCurrentHp(pc.getMaxHp());
				pc.setCurrentMp(pc.getMaxMp());
				pc.sendPackets(new S_ServerMessage(77));
				pc.sendPackets(new S_SkillSound(pc.getId(), 830));
				pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
				pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
			}
		}
		// æ²»ç™‚å¸«ï¼ˆè¥¿éƒ¨ï¼‰
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71030) {
			if (s.equalsIgnoreCase("fullheal")) {
				if (pc.getInventory().checkItem(L1ItemId.ADENA, 5)) { // check
					pc.getInventory().consumeItem(L1ItemId.ADENA, 5); // del
					pc.setCurrentHp(pc.getMaxHp());
					pc.setCurrentMp(pc.getMaxMp());
					pc.sendPackets(new S_ServerMessage(77));
					pc.sendPackets(new S_SkillSound(pc.getId(), 830));
					pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc
							.getMaxHp()));
					pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc
							.getMaxMp()));
					if (pc.isInParty()) { // ãƒ‘ãƒ¼ãƒ†ã‚£ãƒ¼ä¸­
						pc.getParty().updateMiniHP(pc);
					}
				} else {
					pc.sendPackets(new S_ServerMessage(337, "$4")); // ã‚¢ãƒ‡ãƒŠã�Œä¸�è¶³ã�—ã�¦ã�„ã�¾ã�™ã€‚
				}
			}
		}
		// ã‚­ãƒ£ãƒ³ã‚»ãƒ¬ãƒ¼ã‚·ãƒ§ãƒ³å¸«
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71002) {
			// ã‚­ãƒ£ãƒ³ã‚»ãƒ¬ãƒ¼ã‚·ãƒ§ãƒ³é­”æ³•ã‚’ã�‹ã�‘ã�¦ã‚‚ã‚‰ã�†
			if (s.equalsIgnoreCase("0")) {
				if (pc.getLevel() <= 13) {
					L1SkillUse skillUse = new L1SkillUse();
					skillUse.handleCommands(pc, CANCELLATION, pc.getId(),
							pc.getX(), pc.getY(), null, 0,
							L1SkillUse.TYPE_NPCBUFF, (L1NpcInstance) obj);
					htmlid = ""; // ã‚¦ã‚£ãƒ³ãƒ‰ã‚¦ã‚’æ¶ˆã�™
				}
			}
		}
		// ã‚±ã‚¹ã‚­ãƒ³(æ­Œã�†å³¶)
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71025) {
			if (s.equalsIgnoreCase("0")) {
				L1ItemInstance item = pc.getInventory().storeItem(41225, 1); // ã‚±ã‚¹ã‚­ãƒ³ã�®ç™ºæ³¨æ›¸
				pc.sendPackets(new S_ServerMessage(143, ((L1NpcInstance) obj)
                        .getNpcTemplate().get_name(), item.getItem().getName()));
				htmlid = "jpe0083";
			}
		}
		// ãƒ«ã‚±ã‚¤ãƒ³(æµ·è³Šå³¶)
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71055) {
			// ã‚¢ã‚¤ãƒ†ãƒ ã‚’å�—ã�‘å�–ã‚‹
			if (s.equalsIgnoreCase("0")) {
				L1ItemInstance item = pc.getInventory().storeItem(40701, 1); // å°�ã�•ã�ªå®�ã�®åœ°å›³
				pc.sendPackets(new S_ServerMessage(143, ((L1NpcInstance) obj)
						.getNpcTemplate().get_name(), item.getItem().getName()));
				pc.getQuest().set_step(L1Quest.QUEST_LUKEIN1, 1);
				htmlid = "lukein8";
			} else if (s.equalsIgnoreCase("2")) {
				htmlid = "lukein12";
				pc.getQuest().set_step(L1Quest.QUEST_RESTA, 3);
			}
		}
		// å°�ã�•ã�ªç®±-1ç•ªç›®
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71063) {
			if (s.equalsIgnoreCase("0")) {
				materials = new int[] { 40701 }; // å°�ã�•ã�ªå®�ã�®åœ°å›³
				counts = new int[] { 1 };
				createitem = new int[] { 40702 }; // å°�ã�•ã�ªè¢‹
				createcount = new int[] { 1 };
				htmlid = "maptbox1";
				pc.getQuest().set_end(L1Quest.QUEST_TBOX1);
				int[] nextbox = { 1, 2, 3 };
				int pid = Random.nextInt(nextbox.length);
				int nb = nextbox[pid];
				if (nb == 1) { // båœ°ç‚¹
					pc.getQuest().set_step(L1Quest.QUEST_LUKEIN1, 2);
				} else if (nb == 2) { // cåœ°ç‚¹
					pc.getQuest().set_step(L1Quest.QUEST_LUKEIN1, 3);
				} else if (nb == 3) { // dåœ°ç‚¹
					pc.getQuest().set_step(L1Quest.QUEST_LUKEIN1, 4);
				}
			}
		}
		// å°�ã�•ã�ªç®±-2ç•ªç›®
		else if ((((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71064)
				|| (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71065)
				|| (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71066)) {
			if (s.equalsIgnoreCase("0")) {
				materials = new int[] { 40701 }; // å°�ã�•ã�ªå®�ã�®åœ°å›³
				counts = new int[] { 1 };
				createitem = new int[] { 40702 }; // å°�ã�•ã�ªè¢‹
				createcount = new int[] { 1 };
				htmlid = "maptbox1";
				pc.getQuest().set_end(L1Quest.QUEST_TBOX2);
				int[] nextbox2 = { 1, 2, 3, 4, 5, 6 };
				int pid = Random.nextInt(nextbox2.length);
				int nb2 = nextbox2[pid];
				if (nb2 == 1) { // eåœ°ç‚¹
					pc.getQuest().set_step(L1Quest.QUEST_LUKEIN1, 5);
				} else if (nb2 == 2) { // fåœ°ç‚¹
					pc.getQuest().set_step(L1Quest.QUEST_LUKEIN1, 6);
				} else if (nb2 == 3) { // gåœ°ç‚¹
					pc.getQuest().set_step(L1Quest.QUEST_LUKEIN1, 7);
				} else if (nb2 == 4) { // håœ°ç‚¹
					pc.getQuest().set_step(L1Quest.QUEST_LUKEIN1, 8);
				} else if (nb2 == 5) { // iåœ°ç‚¹
					pc.getQuest().set_step(L1Quest.QUEST_LUKEIN1, 9);
				} else if (nb2 == 6) { // jåœ°ç‚¹
					pc.getQuest().set_step(L1Quest.QUEST_LUKEIN1, 10);
				}
			}
		}
		// ã‚·ãƒŸã‚º(æµ·è³Šå³¶)
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71056) {
			// æ�¯å­�ã‚’æ�œã�™
			if (s.equalsIgnoreCase("a")) {
				pc.getQuest().set_step(L1Quest.QUEST_SIMIZZ, 1);
				htmlid = "SIMIZZ7";
			} else if (s.equalsIgnoreCase("b")) {
				if (pc.getInventory().checkItem(40661)
						&& pc.getInventory().checkItem(40662)
						&& pc.getInventory().checkItem(40663)) {
					htmlid = "SIMIZZ8";
					pc.getQuest().set_step(L1Quest.QUEST_SIMIZZ, 2);
					materials = new int[] { 40661, 40662, 40663 };
					counts = new int[] { 1, 1, 1 };
					createitem = new int[] { 20044 };
					createcount = new int[] { 1 };
				} else {
					htmlid = "SIMIZZ9";
				}
			} else if (s.equalsIgnoreCase("d")) {
				htmlid = "SIMIZZ12";
				pc.getQuest().set_step(L1Quest.QUEST_SIMIZZ, L1Quest.QUEST_END);
			}
		}
		// ãƒ‰ã‚¤ãƒ«(æµ·è³Šå³¶)
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71057) {
			// ãƒ©ãƒƒã‚·ãƒ¥ã�«ã�¤ã�„ã�¦è�žã��
			if (s.equalsIgnoreCase("3")) {
				htmlid = "doil4";
			} else if (s.equalsIgnoreCase("6")) {
				htmlid = "doil6";
			} else if (s.equalsIgnoreCase("1")) {
				if (pc.getInventory().checkItem(40714)) {
					htmlid = "doil8";
					materials = new int[] { 40714 };
					counts = new int[] { 1 };
					createitem = new int[] { 40647 };
					createcount = new int[] { 1 };
					pc.getQuest().set_step(L1Quest.QUEST_DOIL,
							L1Quest.QUEST_END);
				} else {
					htmlid = "doil7";
				}
			}
		}
		// ãƒ«ãƒ‡ã‚£ã‚¢ãƒ³(æµ·è³Šå³¶)
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71059) {
			// ãƒ«ãƒ‡ã‚£ã‚¢ãƒ³ã�®é ¼ã�¿ã‚’å�—ã�‘å…¥ã‚Œã‚‹
			if (s.equalsIgnoreCase("A")) {
				htmlid = "rudian6";
				L1ItemInstance item = pc.getInventory().storeItem(40700, 1);
				pc.sendPackets(new S_ServerMessage(143, ((L1NpcInstance) obj)
						.getNpcTemplate().get_name(), item.getItem().getName()));
				pc.getQuest().set_step(L1Quest.QUEST_RUDIAN, 1);
			} else if (s.equalsIgnoreCase("B")) {
				if (pc.getInventory().checkItem(40710)) {
					htmlid = "rudian8";
					materials = new int[] { 40700, 40710 };
					counts = new int[] { 1, 1 };
					createitem = new int[] { 40647 };
					createcount = new int[] { 1 };
					pc.getQuest().set_step(L1Quest.QUEST_RUDIAN,
							L1Quest.QUEST_END);
				} else {
					htmlid = "rudian9";
				}
			}
		}
		// ãƒ¬ã‚¹ã‚¿(æµ·è³Šå³¶)
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71060) {
			// ä»²é–“ã�Ÿã�¡ã�«ã�¤ã�„ã�¦
			if (s.equalsIgnoreCase("A")) {
				if (pc.getQuest().get_step(L1Quest.QUEST_RUDIAN) == L1Quest.QUEST_END) {
					htmlid = "resta6";
				} else {
					htmlid = "resta4";
				}
			} else if (s.equalsIgnoreCase("B")) {
				htmlid = "resta10";
				pc.getQuest().set_step(L1Quest.QUEST_RESTA, 2);
			}
		}
		// ã‚«ãƒ‰ãƒ ã‚¹(æµ·è³Šå³¶)
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71061) {
			// åœ°å›³ã‚’çµ„ã�¿å�ˆã‚�ã�›ã�¦ã��ã� ã�•ã�„
			if (s.equalsIgnoreCase("A")) {
				if (pc.getInventory().checkItem(40647, 3)) {
					htmlid = "cadmus6";
					pc.getInventory().consumeItem(40647, 3);
					pc.getQuest().set_step(L1Quest.QUEST_CADMUS, 2);
				} else {
					htmlid = "cadmus5";
					pc.getQuest().set_step(L1Quest.QUEST_CADMUS, 1);
				}
			}
		}
		// ã‚«ãƒŸãƒ¼ãƒ©(æµ·è³Šå³¶)
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71036) {
			if (s.equalsIgnoreCase("a")) {
				htmlid = "kamyla7";
				pc.getQuest().set_step(L1Quest.QUEST_KAMYLA, 1);
			} else if (s.equalsIgnoreCase("c")) {
				htmlid = "kamyla10";
				pc.getInventory().consumeItem(40644, 1);
				pc.getQuest().set_step(L1Quest.QUEST_KAMYLA, 3);
			} else if (s.equalsIgnoreCase("e")) {
				htmlid = "kamyla13";
				pc.getInventory().consumeItem(40630, 1);
				pc.getQuest().set_step(L1Quest.QUEST_KAMYLA, 4);
			} else if (s.equalsIgnoreCase("i")) {
				htmlid = "kamyla25";
			} else if (s.equalsIgnoreCase("b")) { // ã‚«ãƒ¼ãƒŸãƒ©ï¼ˆãƒ•ãƒ©ãƒ³ã‚³ã�®è¿·å®®ï¼‰
				if (pc.getQuest().get_step(L1Quest.QUEST_KAMYLA) == 1) {
					L1Teleport.teleport(pc, 32679, 32742, (short) 482, 5, true);
				}
			} else if (s.equalsIgnoreCase("d")) { // ã‚«ãƒ¼ãƒŸãƒ©ï¼ˆãƒ‡ã‚£ã‚¨ã‚´ã�®é–‰ã�–ã�•ã‚Œã�Ÿç‰¢ï¼‰
				if (pc.getQuest().get_step(L1Quest.QUEST_KAMYLA) == 3) {
					L1Teleport.teleport(pc, 32736, 32800, (short) 483, 5, true);
				}
			} else if (s.equalsIgnoreCase("f")) { // ã‚«ãƒ¼ãƒŸãƒ©ï¼ˆãƒ›ã‚»åœ°ä¸‹ç‰¢ï¼‰
				if (pc.getQuest().get_step(L1Quest.QUEST_KAMYLA) == 4) {
					L1Teleport.teleport(pc, 32746, 32807, (short) 484, 5, true);
				}
			}
		}
		// ãƒ•ãƒ©ãƒ³ã‚³(æµ·è³Šå³¶)
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71089) {
			// ã‚«ãƒŸãƒ¼ãƒ©ã�«ã�‚ã�ªã�Ÿã�®æ½”ç™½ã‚’è¨¼æ˜Žã�—ã�¾ã�—ã‚‡ã�†
			if (s.equalsIgnoreCase("a")) {
				htmlid = "francu10";
				L1ItemInstance item = pc.getInventory().storeItem(40644, 1);
				pc.sendPackets(new S_ServerMessage(143, ((L1NpcInstance) obj)
						.getNpcTemplate().get_name(), item.getItem().getName()));
				pc.getQuest().set_step(L1Quest.QUEST_KAMYLA, 2);
			}
		}
		// è©¦ç·´ã�®ã‚¯ãƒªã‚¹ã‚¿ãƒ«2(æµ·è³Šå³¶)
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71090) {
			// ã�¯ã�„ã€�æ­¦å™¨ã�¨ã‚¹ã‚¯ãƒ­ãƒ¼ãƒ«ã‚’ã��ã� ã�•ã�„
			if (s.equalsIgnoreCase("a")) {
				htmlid = "";
				final int[] item_ids = { 246, 247, 248, 249, 40660 };
				final int[] item_amounts = { 1, 1, 1, 1, 5 };
				for (int i = 0; i < item_ids.length; i++) {
					L1ItemInstance item = pc.getInventory().storeItem(
							item_ids[i], item_amounts[i]);
					pc.sendPackets(new S_ServerMessage(143,
							((L1NpcInstance) obj).getNpcTemplate().get_name(),
							item.getItem().getName()));
					pc.getQuest().set_step(L1Quest.QUEST_CRYSTAL, 1);
				}
			} else if (s.equalsIgnoreCase("b")) {
				if (pc.getInventory().checkEquipped(246)
						|| pc.getInventory().checkEquipped(247)
						|| pc.getInventory().checkEquipped(248)
						|| pc.getInventory().checkEquipped(249)) {
					htmlid = "jcrystal5";
				} else if (pc.getInventory().checkItem(40660)) {
					htmlid = "jcrystal4";
				} else {
					pc.getInventory().consumeItem(246, 1);
					pc.getInventory().consumeItem(247, 1);
					pc.getInventory().consumeItem(248, 1);
					pc.getInventory().consumeItem(249, 1);
					pc.getInventory().consumeItem(40620, 1);
					pc.getQuest().set_step(L1Quest.QUEST_CRYSTAL, 2);
					L1Teleport.teleport(pc, 32801, 32895, (short) 483, 4, true);
				}
			} else if (s.equalsIgnoreCase("c")) {
				if (pc.getInventory().checkEquipped(246)
						|| pc.getInventory().checkEquipped(247)
						|| pc.getInventory().checkEquipped(248)
						|| pc.getInventory().checkEquipped(249)) {
					htmlid = "jcrystal5";
				} else {
					pc.getInventory().checkItem(40660);
					L1ItemInstance l1iteminstance = pc.getInventory()
							.findItemId(40660);
					int sc = l1iteminstance.getCount();
					if (sc > 0) {
						pc.getInventory().consumeItem(40660, sc);
					} else {
					}
					pc.getInventory().consumeItem(246, 1);
					pc.getInventory().consumeItem(247, 1);
					pc.getInventory().consumeItem(248, 1);
					pc.getInventory().consumeItem(249, 1);
					pc.getInventory().consumeItem(40620, 1);
					pc.getQuest().set_step(L1Quest.QUEST_CRYSTAL, 0);
					L1Teleport.teleport(pc, 32736, 32800, (short) 483, 4, true);
				}
			}
		}
		// è©¦ç·´ã�®ã‚¯ãƒªã‚¹ã‚¿ãƒ«2(æµ·è³Šå³¶)
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71091) {
			// ã�•ã‚‰ã�°ï¼�ï¼�
			if (s.equalsIgnoreCase("a")) {
				htmlid = "";
				pc.getInventory().consumeItem(40654, 1);
				pc.getQuest()
						.set_step(L1Quest.QUEST_CRYSTAL, L1Quest.QUEST_END);
				L1Teleport.teleport(pc, 32744, 32927, (short) 483, 4, true);
			}
		}
		// ãƒªã‚¶ãƒ¼ãƒ‰ãƒžãƒ³ã�®é•·è€�(æµ·è³Šå³¶)
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71074) {
			// ã��ã�®æˆ¦å£«ã�¯ä»Šã�©ã�“ã‚‰ã�¸ã‚“ã�«ã�„ã‚‹ã‚“ã�§ã�™ã�‹ï¼Ÿ
			if (s.equalsIgnoreCase("A")) {
				htmlid = "lelder5";
				pc.getQuest().set_step(L1Quest.QUEST_LIZARD, 1);
				// å®�ã‚’å�–ã‚Šæˆ»ã�—ã�¦ã��ã�¾ã�™
			} else if (s.equalsIgnoreCase("B")) {
				htmlid = "lelder10";
				pc.getInventory().consumeItem(40633, 1);
				pc.getQuest().set_step(L1Quest.QUEST_LIZARD, 3);
			} else if (s.equalsIgnoreCase("C")) {
				htmlid = "lelder13";
				if (pc.getQuest().get_step(L1Quest.QUEST_LIZARD) == L1Quest.QUEST_END) {
				}
				materials = new int[] { 40634 };
				counts = new int[] { 1 };
				createitem = new int[] { 20167 }; // ãƒªã‚¶ãƒ¼ãƒ‰ãƒžãƒ³ã‚°ãƒ­ãƒ¼ãƒ–
				createcount = new int[] { 1 };
				pc.getQuest().set_step(L1Quest.QUEST_LIZARD, L1Quest.QUEST_END);
			}
		}
		// å‚­å…µå›£é•· ãƒ†ã‚£ã‚ªãƒ³
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71198) {
			if (s.equalsIgnoreCase("A")) {
				if ((pc.getQuest().get_step(71198) != 0)
						|| pc.getInventory().checkItem(21059, 1)) {
					return;
				}
				if (pc.getInventory().consumeItem(41339, 5)) { // äº¡è€…ã�®ãƒ¡ãƒ¢
					L1ItemInstance item = ItemTable.getInstance().createItem(
							41340); // å‚­å…µå›£é•·
									// ãƒ†ã‚£ã‚ªãƒ³ã�®ç´¹ä»‹çŠ¶
					if (item != null) {
						if (pc.getInventory().checkAddItem(item, 1) == 0) {
							pc.getInventory().storeItem(item);
							pc.sendPackets(new S_ServerMessage(143,
									((L1NpcInstance) obj).getNpcTemplate()
											.get_name(), item.getItem()
											.getName())); // \f1%0ã�Œ%1ã‚’ã��ã‚Œã�¾ã�—ã�Ÿã€‚
						}
					}
					pc.getQuest().set_step(71198, 1);
					htmlid = "tion4";
				} else {
					htmlid = "tion9";
				}
			} else if (s.equalsIgnoreCase("B")) {
				if ((pc.getQuest().get_step(71198) != 1)
						|| pc.getInventory().checkItem(21059, 1)) {
					return;
				}
				if (pc.getInventory().consumeItem(41341, 1)) { // ã‚¸ã‚§ãƒ­ãƒ³ã�®æ•™æœ¬
					pc.getQuest().set_step(71198, 2);
					htmlid = "tion5";
				} else {
					htmlid = "tion10";
				}
			} else if (s.equalsIgnoreCase("C")) {
				if ((pc.getQuest().get_step(71198) != 2)
						|| pc.getInventory().checkItem(21059, 1)) {
					return;
				}
				if (pc.getInventory().consumeItem(41343, 1)) { // ãƒ‘ãƒ—ãƒªã‚ªãƒ³ã�®è¡€ç—•
					L1ItemInstance item = ItemTable.getInstance().createItem(
							21057); // è¨“ç·´é¨Žå£«ã�®ãƒžãƒ³ãƒˆ1
					if (item != null) {
						if (pc.getInventory().checkAddItem(item, 1) == 0) {
							pc.getInventory().storeItem(item);
							pc.sendPackets(new S_ServerMessage(143,
									((L1NpcInstance) obj).getNpcTemplate()
											.get_name(), item.getItem()
											.getName())); // \f1%0ã�Œ%1ã‚’ã��ã‚Œã�¾ã�—ã�Ÿã€‚
						}
					}
					pc.getQuest().set_step(71198, 3);
					htmlid = "tion6";
				} else {
					htmlid = "tion12";
				}
			} else if (s.equalsIgnoreCase("D")) {
				if ((pc.getQuest().get_step(71198) != 3)
						|| pc.getInventory().checkItem(21059, 1)) {
					return;
				}
				if (pc.getInventory().consumeItem(41344, 1)) { // æ°´ã�®ç²¾ç²‹
					L1ItemInstance item = ItemTable.getInstance().createItem(
							21058); // è¨“ç·´é¨Žå£«ã�®ãƒžãƒ³ãƒˆ2
					if (item != null) {
						pc.getInventory().consumeItem(21057, 1); // è¨“ç·´é¨Žå£«ã�®ãƒžãƒ³ãƒˆ1
						if (pc.getInventory().checkAddItem(item, 1) == 0) {
							pc.getInventory().storeItem(item);
							pc.sendPackets(new S_ServerMessage(143,
									((L1NpcInstance) obj).getNpcTemplate()
											.get_name(), item.getItem()
											.getName())); // \f1%0ã�Œ%1ã‚’ã��ã‚Œã�¾ã�—ã�Ÿã€‚
						}
					}
					pc.getQuest().set_step(71198, 4);
					htmlid = "tion7";
				} else {
					htmlid = "tion13";
				}
			} else if (s.equalsIgnoreCase("E")) {
				if ((pc.getQuest().get_step(71198) != 4)
						|| pc.getInventory().checkItem(21059, 1)) {
					return;
				}
				if (pc.getInventory().consumeItem(41345, 1)) { // é…¸æ€§ã�®ä¹³æ¶²
					L1ItemInstance item = ItemTable.getInstance().createItem(
							21059); // ãƒ�ã‚¤ã‚ºãƒ³
									// ã‚µãƒ¼ãƒšãƒ³ãƒˆ
									// ã‚¯ãƒ­ãƒ¼ã‚¯
					if (item != null) {
						pc.getInventory().consumeItem(21058, 1); // è¨“ç·´é¨Žå£«ã�®ãƒžãƒ³ãƒˆ2
						if (pc.getInventory().checkAddItem(item, 1) == 0) {
							pc.getInventory().storeItem(item);
							pc.sendPackets(new S_ServerMessage(143,
									((L1NpcInstance) obj).getNpcTemplate()
											.get_name(), item.getItem()
											.getName())); // \f1%0ã�Œ%1ã‚’ã��ã‚Œã�¾ã�—ã�Ÿã€‚
						}
					}
					pc.getQuest().set_step(71198, 0);
					pc.getQuest().set_step(71199, 0);
					htmlid = "tion8";
				} else {
					htmlid = "tion15";
				}
			}
		}
		// ã‚¸ã‚§ãƒ­ãƒ³
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71199) {
			if (s.equalsIgnoreCase("A")) {
				if ((pc.getQuest().get_step(71199) != 0)
						|| pc.getInventory().checkItem(21059, 1)) {
					return;
				}
				if (pc.getInventory().checkItem(41340, 1)) { // å‚­å…µå›£é•· ãƒ†ã‚£ã‚ªãƒ³ã�®ç´¹ä»‹çŠ¶
					pc.getQuest().set_step(71199, 1);
					htmlid = "jeron2";
				} else {
					htmlid = "jeron10";
				}
			} else if (s.equalsIgnoreCase("B")) {
				if ((pc.getQuest().get_step(71199) != 1)
						|| pc.getInventory().checkItem(21059, 1)) {
					return;
				}
				if (pc.getInventory().consumeItem(L1ItemId.ADENA, 1000000)) {
					L1ItemInstance item = ItemTable.getInstance().createItem(
							41341); // ã‚¸ã‚§ãƒ­ãƒ³ã�®æ•™æœ¬
					if (item != null) {
						if (pc.getInventory().checkAddItem(item, 1) == 0) {
							pc.getInventory().storeItem(item);
							pc.sendPackets(new S_ServerMessage(143,
									((L1NpcInstance) obj).getNpcTemplate()
											.get_name(), item.getItem()
											.getName())); // \f1%0ã�Œ%1ã‚’ã��ã‚Œã�¾ã�—ã�Ÿã€‚
						}
					}
					pc.getInventory().consumeItem(41340, 1);
					pc.getQuest().set_step(71199, 255);
					htmlid = "jeron6";
				} else {
					htmlid = "jeron8";
				}
			} else if (s.equalsIgnoreCase("C")) {
				if ((pc.getQuest().get_step(71199) != 1)
						|| pc.getInventory().checkItem(21059, 1)) {
					return;
				}
				if (pc.getInventory().consumeItem(41342, 1)) { // ãƒ¡ãƒ‡ãƒ¥ãƒ¼ã‚µã�®è¡€
					L1ItemInstance item = ItemTable.getInstance().createItem(
							41341); // ã‚¸ã‚§ãƒ­ãƒ³ã�®æ•™æœ¬
					if (item != null) {
						if (pc.getInventory().checkAddItem(item, 1) == 0) {
							pc.getInventory().storeItem(item);
							pc.sendPackets(new S_ServerMessage(143,
									((L1NpcInstance) obj).getNpcTemplate()
											.get_name(), item.getItem()
											.getName())); // \f1%0ã�Œ%1ã‚’ã��ã‚Œã�¾ã�—ã�Ÿã€‚
						}
					}
					pc.getInventory().consumeItem(41340, 1);
					pc.getQuest().set_step(71199, 255);
					htmlid = "jeron5";
				} else {
					htmlid = "jeron9";
				}
			}
		}
		// å� æ˜Ÿè¡“å¸«ã‚±ãƒ—ãƒªã‚·ãƒ£
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80079) {
			// ã‚±ãƒ—ãƒªã‚·ãƒ£ã�¨é­‚ã�®å¥‘ç´„ã‚’çµ�ã�¶
			if (s.equalsIgnoreCase("0")) {
				if (!pc.getInventory().checkItem(41312)) { // å� æ˜Ÿè¡“å¸«ã�®å£º
					L1ItemInstance item = pc.getInventory().storeItem(41312, 1);
					if (item != null) {
						pc.sendPackets(new S_ServerMessage(143,
								((L1NpcInstance) obj).getNpcTemplate()
										.get_name(), item.getItem().getName())); // \f1%0ã�Œ%1ã‚’ã��ã‚Œã�¾ã�—ã�Ÿã€‚
						pc.getQuest().set_step(L1Quest.QUEST_KEPLISHA,
								L1Quest.QUEST_END);
					}
					htmlid = "keplisha7";
				}
			}
			// æ�´åŠ©é‡‘ã‚’å‡ºã�—ã�¦é�‹å‹¢ã‚’è¦‹ã‚‹
			else if (s.equalsIgnoreCase("1")) {
				if (!pc.getInventory().checkItem(41314)) { // å� æ˜Ÿè¡“å¸«ã�®ã�Šå®ˆã‚Š
					if (pc.getInventory().checkItem(L1ItemId.ADENA, 1000)) {
						materials = new int[] { L1ItemId.ADENA, 41313 }; // ã‚¢ãƒ‡ãƒŠã€�å� æ˜Ÿè¡“å¸«ã�®çŽ‰
						counts = new int[] { 1000, 1 };
						createitem = new int[] { 41314 }; // å� æ˜Ÿè¡“å¸«ã�®ã�Šå®ˆã‚Š
						createcount = new int[] { 1 };
						int htmlA = Random.nextInt(3) + 1;
						int htmlB = Random.nextInt(100) + 1;
						switch (htmlA) {
						case 1:
							htmlid = "horosa" + htmlB; // horosa1 ~
														// horosa100
							break;
						case 2:
							htmlid = "horosb" + htmlB; // horosb1 ~
														// horosb100
							break;
						case 3:
							htmlid = "horosc" + htmlB; // horosc1 ~
														// horosc100
							break;
						default:
							break;
						}
					} else {
						htmlid = "keplisha8";
					}
				}
			}
			// ã‚±ãƒ—ãƒªã‚·ãƒ£ã�‹ã‚‰ç¥�ç¦�ã‚’å�—ã�‘ã‚‹
			else if (s.equalsIgnoreCase("2")) {
				if (pc.getTempCharGfx() != pc.getClassId()) {
					htmlid = "keplisha9";
				} else {
					if (pc.getInventory().checkItem(41314)) { // å� æ˜Ÿè¡“å¸«ã�®ã�Šå®ˆã‚Š
						pc.getInventory().consumeItem(41314, 1); // å� æ˜Ÿè¡“å¸«ã�®ã�Šå®ˆã‚Š
						int html = Random.nextInt(9) + 1;
						int PolyId = 6180 + Random.nextInt(64);
						polyByKeplisha(client, PolyId);
						switch (html) {
						case 1:
							htmlid = "horomon11";
							break;
						case 2:
							htmlid = "horomon12";
							break;
						case 3:
							htmlid = "horomon13";
							break;
						case 4:
							htmlid = "horomon21";
							break;
						case 5:
							htmlid = "horomon22";
							break;
						case 6:
							htmlid = "horomon23";
							break;
						case 7:
							htmlid = "horomon31";
							break;
						case 8:
							htmlid = "horomon32";
							break;
						case 9:
							htmlid = "horomon33";
							break;
						default:
							break;
						}
					}
				}
			}
			// å£ºã‚’å‰²ã�£ã�¦å¥‘ç´„ã‚’ç ´æ£„ã�™ã‚‹
			else if (s.equalsIgnoreCase("3")) {
				if (pc.getInventory().checkItem(41312)) { // å� æ˜Ÿè¡“å¸«ã�®å£º
					pc.getInventory().consumeItem(41312, 1);
					htmlid = "";
				}
				if (pc.getInventory().checkItem(41313)) { // å� æ˜Ÿè¡“å¸«ã�®çŽ‰
					pc.getInventory().consumeItem(41313, 1);
					htmlid = "";
				}
				if (pc.getInventory().checkItem(41314)) { // å� æ˜Ÿè¡“å¸«ã�®ã�Šå®ˆã‚Š
					pc.getInventory().consumeItem(41314, 1);
					htmlid = "";
				}
			}
		}
		// é‡£é­šå°�ç«¥ æ³¢çˆ¾ (é€²å…¥é‡£é­šæ± )
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80082) {
			if (s.equalsIgnoreCase("a")) {
				if (pc.getLevel() < 15) {
					htmlid = "fk_in_lv"; // é­”æ³•é‡£é­šæ± å�ªå°�15ç­‰ç´šä»¥ä¸Šçš„å†’éšªå®¶é–‹æ”¾ã€‚
				} else if (pc.getInventory().consumeItem(L1ItemId.ADENA, 1000)) {
					L1PolyMorph.undoPoly(pc);
					L1Teleport
							.teleport(pc, 32742, 32799, (short) 5300, 4, true);
				} else {
					htmlid = "fk_in_0";
				}
			}
		}
		// æ€ªã�—ã�„ã‚ªãƒ¼ã‚¯å•†äºº ãƒ‘ãƒ«ãƒ¼ãƒ 
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80084) {
			// ã€Œè³‡æº�ãƒªã‚¹ãƒˆã‚’ã‚‚ã‚‰ã�†ã€�
			if (s.equalsIgnoreCase("q")) {
				if (pc.getInventory().checkItem(41356, 1)) {
					htmlid = "rparum4";
				} else {
					L1ItemInstance item = pc.getInventory().storeItem(41356, 1);
					if (item != null) {
						pc.sendPackets(new S_ServerMessage(143,
								((L1NpcInstance) obj).getNpcTemplate()
										.get_name(), item.getItem().getName())); // \f1%0ã�Œ%1ã‚’ã��ã‚Œã�¾ã�—ã�Ÿã€‚
					}
					htmlid = "rparum3";
				}
			}
		}
		// ã‚¢ãƒ‡ãƒ³é¨Žé¦¬å›£å“¡
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80105) {
			// ã€Œæ–°ã�Ÿã�ªåŠ›ã‚’ã��ã� ã�•ã�„ã‚‹ã€�
			if (s.equalsIgnoreCase("c")) {
				if (pc.isCrown()) {
					if (pc.getInventory().checkItem(20383, 1)) {
						if (pc.getInventory().checkItem(L1ItemId.ADENA, 100000)) {
							L1ItemInstance item = pc.getInventory().findItemId(
									20383);
							if ((item != null) && (item.getChargeCount() != 50)) {
								item.setChargeCount(50);
								pc.getInventory().updateItem(item,
										L1PcInventory.COL_CHARGE_COUNT);
								pc.getInventory().consumeItem(L1ItemId.ADENA,
										100000);
								htmlid = "";
							}
						} else {
							pc.sendPackets(new S_ServerMessage(337, "$4")); // ã‚¢ãƒ‡ãƒŠã�Œä¸�è¶³ã�—ã�¦ã�„ã�¾ã�™ã€‚
						}
					}
				}
			}
		}
		// è£œä½�å®˜ã‚¤ãƒªã‚¹
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71126) {
			// ã€Œã�¯ã�„ã€‚ç§�ã�Œã�”å�”åŠ›ã�—ã�¾ã�—ã‚‡ã�†ã€�
			if (s.equalsIgnoreCase("B")) {
				if (pc.getInventory().checkItem(41007, 1)) { // ã‚¤ãƒªã‚¹ã�®å‘½ä»¤æ›¸ï¼šéœŠé­‚ã�®å®‰æ�¯
					htmlid = "eris10";
				} else {
					L1NpcInstance npc = (L1NpcInstance) obj;
					L1ItemInstance item = pc.getInventory().storeItem(41007, 1);
					String npcName = npc.getNpcTemplate().get_name();
					String itemName = item.getItem().getName();
					pc.sendPackets(new S_ServerMessage(143, npcName, itemName));
					htmlid = "eris6";
				}
			} else if (s.equalsIgnoreCase("C")) {
				if (pc.getInventory().checkItem(41009, 1)) { // ã‚¤ãƒªã‚¹ã�®å‘½ä»¤æ›¸ï¼šå�Œç›Ÿã�®æ„�æ€�
					htmlid = "eris10";
				} else {
					L1NpcInstance npc = (L1NpcInstance) obj;
					L1ItemInstance item = pc.getInventory().storeItem(41009, 1);
					String npcName = npc.getNpcTemplate().get_name();
					String itemName = item.getItem().getName();
					pc.sendPackets(new S_ServerMessage(143, npcName, itemName));
					htmlid = "eris8";
				}
			} else if (s.equalsIgnoreCase("A")) {
				if (pc.getInventory().checkItem(41007, 1)) { // ã‚¤ãƒªã‚¹ã�®å‘½ä»¤æ›¸ï¼šéœŠé­‚ã�®å®‰æ�¯
					if (pc.getInventory().checkItem(40969, 20)) { // ãƒ€ãƒ¼ã‚¯ã‚¨ãƒ«ãƒ•é­‚ã�®çµ�æ™¶ä½“
						htmlid = "eris18";
						materials = new int[] { 40969, 41007 };
						counts = new int[] { 20, 1 };
						createitem = new int[] { 41008 }; // ã‚¤ãƒªã‚¹ã�®ãƒ�ãƒƒã‚¯
						createcount = new int[] { 1 };
					} else {
						htmlid = "eris5";
					}
				} else {
					htmlid = "eris2";
				}
			} else if (s.equalsIgnoreCase("E")) {
				if (pc.getInventory().checkItem(41010, 1)) { // ã‚¤ãƒªã‚¹ã�®æŽ¨è–¦æ›¸
					htmlid = "eris19";
				} else {
					htmlid = "eris7";
				}
			} else if (s.equalsIgnoreCase("D")) {
				if (pc.getInventory().checkItem(41010, 1)) { // ã‚¤ãƒªã‚¹ã�®æŽ¨è–¦æ›¸
					htmlid = "eris19";
				} else {
					if (pc.getInventory().checkItem(41009, 1)) { // ã‚¤ãƒªã‚¹ã�®å‘½ä»¤æ›¸ï¼šå�Œç›Ÿã�®æ„�æ€�
						if (pc.getInventory().checkItem(40959, 1)) { // å†¥æ³•è»�çŽ‹ã�®å�°ç« 
							htmlid = "eris17";
							materials = new int[] { 40959, 41009 }; // å†¥æ³•è»�çŽ‹ã�®å�°ç« 
							counts = new int[] { 1, 1 };
							createitem = new int[] { 41010 }; // ã‚¤ãƒªã‚¹ã�®æŽ¨è–¦æ›¸
							createcount = new int[] { 1 };
						} else if (pc.getInventory().checkItem(40960, 1)) { // é­”éœŠè»�çŽ‹ã�®å�°ç« 
							htmlid = "eris16";
							materials = new int[] { 40960, 41009 }; // é­”éœŠè»�çŽ‹ã�®å�°ç« 
							counts = new int[] { 1, 1 };
							createitem = new int[] { 41010 }; // ã‚¤ãƒªã‚¹ã�®æŽ¨è–¦æ›¸
							createcount = new int[] { 1 };
						} else if (pc.getInventory().checkItem(40961, 1)) { // é­”ç�£éœŠè»�çŽ‹ã�®å�°ç« 
							htmlid = "eris15";
							materials = new int[] { 40961, 41009 }; // é­”ç�£è»�çŽ‹ã�®å�°ç« 
							counts = new int[] { 1, 1 };
							createitem = new int[] { 41010 }; // ã‚¤ãƒªã‚¹ã�®æŽ¨è–¦æ›¸
							createcount = new int[] { 1 };
						} else if (pc.getInventory().checkItem(40962, 1)) { // æš—æ®ºè»�çŽ‹ã�®å�°ç« 
							htmlid = "eris14";
							materials = new int[] { 40962, 41009 }; // æš—æ®ºè»�çŽ‹ã�®å�°ç« 
							counts = new int[] { 1, 1 };
							createitem = new int[] { 41010 }; // ã‚¤ãƒªã‚¹ã�®æŽ¨è–¦æ›¸
							createcount = new int[] { 1 };
						} else if (pc.getInventory().checkItem(40635, 10)) { // é­”éœŠè»�ã�®ãƒ�ãƒƒã‚¸
							htmlid = "eris12";
							materials = new int[] { 40635, 41009 }; // é­”éœŠè»�ã�®ãƒ�ãƒƒã‚¸
							counts = new int[] { 10, 1 };
							createitem = new int[] { 41010 }; // ã‚¤ãƒªã‚¹ã�®æŽ¨è–¦æ›¸
							createcount = new int[] { 1 };
						} else if (pc.getInventory().checkItem(40638, 10)) { // é­”ç�£è»�ã�®ãƒ�ãƒƒã‚¸
							htmlid = "eris11";
							materials = new int[] { 40638, 41009 }; // é­”éœŠè»�ã�®ãƒ�ãƒƒã‚¸
							counts = new int[] { 10, 1 };
							createitem = new int[] { 41010 }; // ã‚¤ãƒªã‚¹ã�®æŽ¨è–¦æ›¸
							createcount = new int[] { 1 };
						} else if (pc.getInventory().checkItem(40642, 10)) { // å†¥æ³•è»�ã�®ãƒ�ãƒƒã‚¸
							htmlid = "eris13";
							materials = new int[] { 40642, 41009 }; // å†¥æ³•è»�ã�®ãƒ�ãƒƒã‚¸
							counts = new int[] { 10, 1 };
							createitem = new int[] { 41010 }; // ã‚¤ãƒªã‚¹ã�®æŽ¨è–¦æ›¸
							createcount = new int[] { 1 };
						} else if (pc.getInventory().checkItem(40667, 10)) { // æš—æ®ºè»�ã�®ãƒ�ãƒƒã‚¸
							htmlid = "eris13";
							materials = new int[] { 40667, 41009 }; // æš—æ®ºè»�ã�®ãƒ�ãƒƒã‚¸
							counts = new int[] { 10, 1 };
							createitem = new int[] { 41010 }; // ã‚¤ãƒªã‚¹ã�®æŽ¨è–¦æ›¸
							createcount = new int[] { 1 };
						} else {
							htmlid = "eris8";
						}
					} else {
						htmlid = "eris7";
					}
				}
			}
		}
		// å€’ã‚Œã�Ÿèˆªæµ·å£«
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80076) {
			if (s.equalsIgnoreCase("A")) {
				int[] diaryno = { 49082, 49083 };
				int pid = Random.nextInt(diaryno.length);
				int di = diaryno[pid];
				if (di == 49082) { // å¥‡æ•°ãƒšãƒ¼ã‚¸æŠœã�‘
					htmlid = "voyager6a";
					L1NpcInstance npc = (L1NpcInstance) obj;
					L1ItemInstance item = pc.getInventory().storeItem(di, 1);
					String npcName = npc.getNpcTemplate().get_name();
					String itemName = item.getItem().getName();
					pc.sendPackets(new S_ServerMessage(143, npcName, itemName));
				} else if (di == 49083) { // å�¶æ•°ãƒšãƒ¼ã‚¸æŠœã�‘
					htmlid = "voyager6b";
					L1NpcInstance npc = (L1NpcInstance) obj;
					L1ItemInstance item = pc.getInventory().storeItem(di, 1);
					String npcName = npc.getNpcTemplate().get_name();
					String itemName = item.getItem().getName();
					pc.sendPackets(new S_ServerMessage(143, npcName, itemName));
				}
			}
		}
		// éŒ¬é‡‘è¡“å¸« ãƒšãƒªã‚¿ãƒ¼
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71128) {
			if (s.equals("A")) {
				if (pc.getInventory().checkItem(41010, 1)) { // ã‚¤ãƒªã‚¹ã�®æŽ¨è–¦æ›¸
					htmlid = "perita2";
				} else {
					htmlid = "perita3";
				}
			} else if (s.equals("p")) {
				// å‘ªã‚�ã‚Œã�Ÿãƒ–ãƒ©ãƒƒã‚¯ã‚¤ã‚¢ãƒªãƒ³ã‚°åˆ¤åˆ¥
				if (pc.getInventory().checkItem(40987, 1) // ã‚¦ã‚£ã‚¶ãƒ¼ãƒ‰ã‚¯ãƒ©ã‚¹
						&& pc.getInventory().checkItem(40988, 1) // ãƒŠã‚¤ãƒˆã‚¯ãƒ©ã‚¹
						&& pc.getInventory().checkItem(40989, 1)) { // ã‚¦ã‚©ãƒ¼ãƒªã‚¢ã‚¯ãƒ©ã‚¹
					htmlid = "perita43";
				} else if (pc.getInventory().checkItem(40987, 1) // ã‚¦ã‚£ã‚¶ãƒ¼ãƒ‰ã‚¯ãƒ©ã‚¹
						&& pc.getInventory().checkItem(40989, 1)) { // ã‚¦ã‚©ãƒ¼ãƒªã‚¢ã‚¯ãƒ©ã‚¹
					htmlid = "perita44";
				} else if (pc.getInventory().checkItem(40987, 1) // ã‚¦ã‚£ã‚¶ãƒ¼ãƒ‰ã‚¯ãƒ©ã‚¹
						&& pc.getInventory().checkItem(40988, 1)) { // ãƒŠã‚¤ãƒˆã‚¯ãƒ©ã‚¹
					htmlid = "perita45";
				} else if (pc.getInventory().checkItem(40988, 1) // ãƒŠã‚¤ãƒˆã‚¯ãƒ©ã‚¹
						&& pc.getInventory().checkItem(40989, 1)) { // ã‚¦ã‚©ãƒ¼ãƒªã‚¢ã‚¯ãƒ©ã‚¹
					htmlid = "perita47";
				} else if (pc.getInventory().checkItem(40987, 1)) { // ã‚¦ã‚£ã‚¶ãƒ¼ãƒ‰ã‚¯ãƒ©ã‚¹
					htmlid = "perita46";
				} else if (pc.getInventory().checkItem(40988, 1)) { // ãƒŠã‚¤ãƒˆã‚¯ãƒ©ã‚¹
					htmlid = "perita49";
				} else if (pc.getInventory().checkItem(40987, 1)) { // ã‚¦ã‚©ãƒ¼ãƒªã‚¢ã‚¯ãƒ©ã‚¹
					htmlid = "perita48";
				} else {
					htmlid = "perita50";
				}
			} else if (s.equals("q")) {
				// ãƒ–ãƒ©ãƒƒã‚¯ã‚¤ã‚¢ãƒªãƒ³ã‚°åˆ¤åˆ¥
				if (pc.getInventory().checkItem(41173, 1) // ã‚¦ã‚£ã‚¶ãƒ¼ãƒ‰ã‚¯ãƒ©ã‚¹
						&& pc.getInventory().checkItem(41174, 1) // ãƒŠã‚¤ãƒˆã‚¯ãƒ©ã‚¹
						&& pc.getInventory().checkItem(41175, 1)) { // ã‚¦ã‚©ãƒ¼ãƒªã‚¢ã‚¯ãƒ©ã‚¹
					htmlid = "perita54";
				} else if (pc.getInventory().checkItem(41173, 1) // ã‚¦ã‚£ã‚¶ãƒ¼ãƒ‰ã‚¯ãƒ©ã‚¹
						&& pc.getInventory().checkItem(41175, 1)) { // ã‚¦ã‚©ãƒ¼ãƒªã‚¢ã‚¯ãƒ©ã‚¹
					htmlid = "perita55";
				} else if (pc.getInventory().checkItem(41173, 1) // ã‚¦ã‚£ã‚¶ãƒ¼ãƒ‰ã‚¯ãƒ©ã‚¹
						&& pc.getInventory().checkItem(41174, 1)) { // ãƒŠã‚¤ãƒˆã‚¯ãƒ©ã‚¹
					htmlid = "perita56";
				} else if (pc.getInventory().checkItem(41174, 1) // ãƒŠã‚¤ãƒˆã‚¯ãƒ©ã‚¹
						&& pc.getInventory().checkItem(41175, 1)) { // ã‚¦ã‚©ãƒ¼ãƒªã‚¢ã‚¯ãƒ©ã‚¹
					htmlid = "perita58";
				} else if (pc.getInventory().checkItem(41174, 1)) { // ã‚¦ã‚£ã‚¶ãƒ¼ãƒ‰ã‚¯ãƒ©ã‚¹
					htmlid = "perita57";
				} else if (pc.getInventory().checkItem(41175, 1)) { // ãƒŠã‚¤ãƒˆã‚¯ãƒ©ã‚¹
					htmlid = "perita60";
				} else if (pc.getInventory().checkItem(41176, 1)) { // ã‚¦ã‚©ãƒ¼ãƒªã‚¢ã‚¯ãƒ©ã‚¹
					htmlid = "perita59";
				} else {
					htmlid = "perita61";
				}
			} else if (s.equals("s")) {
				// ãƒŸã‚¹ãƒ†ãƒªã‚¢ã‚¹ ãƒ–ãƒ©ãƒƒã‚¯ã‚¤ã‚¢ãƒªãƒ³ã‚°åˆ¤åˆ¥
				if (pc.getInventory().checkItem(41161, 1) // ã‚¦ã‚£ã‚¶ãƒ¼ãƒ‰ã‚¯ãƒ©ã‚¹
						&& pc.getInventory().checkItem(41162, 1) // ãƒŠã‚¤ãƒˆã‚¯ãƒ©ã‚¹
						&& pc.getInventory().checkItem(41163, 1)) { // ã‚¦ã‚©ãƒ¼ãƒªã‚¢ã‚¯ãƒ©ã‚¹
					htmlid = "perita62";
				} else if (pc.getInventory().checkItem(41161, 1) // ã‚¦ã‚£ã‚¶ãƒ¼ãƒ‰ã‚¯ãƒ©ã‚¹
						&& pc.getInventory().checkItem(41163, 1)) { // ã‚¦ã‚©ãƒ¼ãƒªã‚¢ã‚¯ãƒ©ã‚¹
					htmlid = "perita63";
				} else if (pc.getInventory().checkItem(41161, 1) // ã‚¦ã‚£ã‚¶ãƒ¼ãƒ‰ã‚¯ãƒ©ã‚¹
						&& pc.getInventory().checkItem(41162, 1)) { // ãƒŠã‚¤ãƒˆã‚¯ãƒ©ã‚¹
					htmlid = "perita64";
				} else if (pc.getInventory().checkItem(41162, 1) // ãƒŠã‚¤ãƒˆã‚¯ãƒ©ã‚¹
						&& pc.getInventory().checkItem(41163, 1)) { // ã‚¦ã‚©ãƒ¼ãƒªã‚¢ã‚¯ãƒ©ã‚¹
					htmlid = "perita66";
				} else if (pc.getInventory().checkItem(41161, 1)) { // ã‚¦ã‚£ã‚¶ãƒ¼ãƒ‰ã‚¯ãƒ©ã‚¹
					htmlid = "perita65";
				} else if (pc.getInventory().checkItem(41162, 1)) { // ãƒŠã‚¤ãƒˆã‚¯ãƒ©ã‚¹
					htmlid = "perita68";
				} else if (pc.getInventory().checkItem(41163, 1)) { // ã‚¦ã‚©ãƒ¼ãƒªã‚¢ã‚¯ãƒ©ã‚¹
					htmlid = "perita67";
				} else {
					htmlid = "perita69";
				}
			} else if (s.equals("B")) {
				// æµ„åŒ–ã�®ãƒ�ãƒ¼ã‚·ãƒ§ãƒ³
				if (pc.getInventory().checkItem(40651, 10) // ç�«ã�®æ�¯å�¹
						&& pc.getInventory().checkItem(40643, 10) // æ°´ã�®æ�¯å�¹
						&& pc.getInventory().checkItem(40618, 10) // å¤§åœ°ã�®æ�¯å�¹
						&& pc.getInventory().checkItem(40645, 10) // é¢¨ã�®æ�¯å�¹
						&& pc.getInventory().checkItem(40676, 10) // é—‡ã�®æ�¯å�¹
						&& pc.getInventory().checkItem(40442, 5) // ãƒ—ãƒ­ãƒƒãƒ–ã�®èƒƒæ¶²
						&& pc.getInventory().checkItem(40051, 1)) { // é«˜ç´šã‚¨ãƒ¡ãƒ©ãƒ«ãƒ‰
					htmlid = "perita7";
					materials = new int[] { 40651, 40643, 40618, 40645, 40676,
							40442, 40051 };
					counts = new int[] { 10, 10, 10, 10, 20, 5, 1 };
					createitem = new int[] { 40925 }; // æµ„åŒ–ã�®ãƒ�ãƒ¼ã‚·ãƒ§ãƒ³
					createcount = new int[] { 1 };
				} else {
					htmlid = "perita8";
				}
			} else if (s.equals("G") || s.equals("h") || s.equals("i")) {
				// ãƒŸã‚¹ãƒ†ãƒªã‚¢ã‚¹ ãƒ�ãƒ¼ã‚·ãƒ§ãƒ³ï¼šï¼‘æ®µéšŽ
				if (pc.getInventory().checkItem(40651, 5) // ç�«ã�®æ�¯å�¹
						&& pc.getInventory().checkItem(40643, 5) // æ°´ã�®æ�¯å�¹
						&& pc.getInventory().checkItem(40618, 5) // å¤§åœ°ã�®æ�¯å�¹
						&& pc.getInventory().checkItem(40645, 5) // é¢¨ã�®æ�¯å�¹
						&& pc.getInventory().checkItem(40676, 5) // é—‡ã�®æ�¯å�¹
						&& pc.getInventory().checkItem(40675, 5) // é—‡ã�®é‰±çŸ³
						&& pc.getInventory().checkItem(40049, 3) // é«˜ç´šãƒ«ãƒ“ãƒ¼
						&& pc.getInventory().checkItem(40051, 1)) { // é«˜ç´šã‚¨ãƒ¡ãƒ©ãƒ«ãƒ‰
					htmlid = "perita27";
					materials = new int[] { 40651, 40643, 40618, 40645, 40676,
							40675, 40049, 40051 };
					counts = new int[] { 5, 5, 5, 5, 10, 10, 3, 1 };
					createitem = new int[] { 40926 }; // ãƒŸã‚¹ãƒ†ãƒªã‚¢ã‚¹ãƒ�ãƒ¼ã‚·ãƒ§ãƒ³ï¼šï¼‘æ®µéšŽ
					createcount = new int[] { 1 };
				} else {
					htmlid = "perita28";
				}
			} else if (s.equals("H") || s.equals("j") || s.equals("k")) {
				// ãƒŸã‚¹ãƒ†ãƒªã‚¢ã‚¹ ãƒ�ãƒ¼ã‚·ãƒ§ãƒ³ï¼šï¼’æ®µéšŽ
				if (pc.getInventory().checkItem(40651, 10) // ç�«ã�®æ�¯å�¹
						&& pc.getInventory().checkItem(40643, 10) // æ°´ã�®æ�¯å�¹
						&& pc.getInventory().checkItem(40618, 10) // å¤§åœ°ã�®æ�¯å�¹
						&& pc.getInventory().checkItem(40645, 10) // é¢¨ã�®æ�¯å�¹
						&& pc.getInventory().checkItem(40676, 20) // é—‡ã�®æ�¯å�¹
						&& pc.getInventory().checkItem(40675, 10) // é—‡ã�®é‰±çŸ³
						&& pc.getInventory().checkItem(40048, 3) // é«˜ç´šãƒ€ã‚¤ã‚¢ãƒ¢ãƒ³ãƒ‰
						&& pc.getInventory().checkItem(40051, 1)) { // é«˜ç´šã‚¨ãƒ¡ãƒ©ãƒ«ãƒ‰
					htmlid = "perita29";
					materials = new int[] { 40651, 40643, 40618, 40645, 40676,
							40675, 40048, 40051 };
					counts = new int[] { 10, 10, 10, 10, 20, 10, 3, 1 };
					createitem = new int[] { 40927 }; // ãƒŸã‚¹ãƒ†ãƒªã‚¢ã‚¹ãƒ�ãƒ¼ã‚·ãƒ§ãƒ³ï¼šï¼’æ®µéšŽ
					createcount = new int[] { 1 };
				} else {
					htmlid = "perita30";
				}
			} else if (s.equals("I") || s.equals("l") || s.equals("m")) {
				// ãƒŸã‚¹ãƒ†ãƒªã‚¢ã‚¹ ãƒ�ãƒ¼ã‚·ãƒ§ãƒ³ï¼šï¼“æ®µéšŽ
				if (pc.getInventory().checkItem(40651, 20) // ç�«ã�®æ�¯å�¹
						&& pc.getInventory().checkItem(40643, 20) // æ°´ã�®æ�¯å�¹
						&& pc.getInventory().checkItem(40618, 20) // å¤§åœ°ã�®æ�¯å�¹
						&& pc.getInventory().checkItem(40645, 20) // é¢¨ã�®æ�¯å�¹
						&& pc.getInventory().checkItem(40676, 30) // é—‡ã�®æ�¯å�¹
						&& pc.getInventory().checkItem(40675, 10) // é—‡ã�®é‰±çŸ³
						&& pc.getInventory().checkItem(40050, 3) // é«˜ç´šã‚µãƒ•ã‚¡ã‚¤ã‚¢
						&& pc.getInventory().checkItem(40051, 1)) { // é«˜ç´šã‚¨ãƒ¡ãƒ©ãƒ«ãƒ‰
					htmlid = "perita31";
					materials = new int[] { 40651, 40643, 40618, 40645, 40676,
							40675, 40050, 40051 };
					counts = new int[] { 20, 20, 20, 20, 30, 10, 3, 1 };
					createitem = new int[] { 40928 }; // ãƒŸã‚¹ãƒ†ãƒªã‚¢ã‚¹ãƒ�ãƒ¼ã‚·ãƒ§ãƒ³ï¼šï¼“æ®µéšŽ
					createcount = new int[] { 1 };
				} else {
					htmlid = "perita32";
				}
			} else if (s.equals("J") || s.equals("n") || s.equals("o")) {
				// ãƒŸã‚¹ãƒ†ãƒªã‚¢ã‚¹ ãƒ�ãƒ¼ã‚·ãƒ§ãƒ³ï¼šï¼”æ®µéšŽ
				if (pc.getInventory().checkItem(40651, 30) // ç�«ã�®æ�¯å�¹
						&& pc.getInventory().checkItem(40643, 30) // æ°´ã�®æ�¯å�¹
						&& pc.getInventory().checkItem(40618, 30) // å¤§åœ°ã�®æ�¯å�¹
						&& pc.getInventory().checkItem(40645, 30) // é¢¨ã�®æ�¯å�¹
						&& pc.getInventory().checkItem(40676, 30) // é—‡ã�®æ�¯å�¹
						&& pc.getInventory().checkItem(40675, 20) // é—‡ã�®é‰±çŸ³
						&& pc.getInventory().checkItem(40052, 1) // æœ€é«˜ç´šãƒ€ã‚¤ã‚¢ãƒ¢ãƒ³ãƒ‰
						&& pc.getInventory().checkItem(40051, 1)) { // é«˜ç´šã‚¨ãƒ¡ãƒ©ãƒ«ãƒ‰
					htmlid = "perita33";
					materials = new int[] { 40651, 40643, 40618, 40645, 40676,
							40675, 40052, 40051 };
					counts = new int[] { 30, 30, 30, 30, 30, 20, 1, 1 };
					createitem = new int[] { 40928 }; // ãƒŸã‚¹ãƒ†ãƒªã‚¢ã‚¹ãƒ�ãƒ¼ã‚·ãƒ§ãƒ³ï¼šï¼”æ®µéšŽ
					createcount = new int[] { 1 };
				} else {
					htmlid = "perita34";
				}
			} else if (s.equals("K")) { // ï¼‘æ®µéšŽã‚¤ã‚¢ãƒªãƒ³ã‚°(éœŠé­‚ã�®ã‚¤ã‚¢ãƒªãƒ³ã‚°)
				int earinga = 0;
				int earingb = 0;
				if (pc.getInventory().checkEquipped(21014)
						|| pc.getInventory().checkEquipped(21006)
						|| pc.getInventory().checkEquipped(21007)) {
					htmlid = "perita36";
				} else if (pc.getInventory().checkItem(21014, 1)) { // ã‚¦ã‚£ã‚¶ãƒ¼ãƒ‰ã‚¯ãƒ©ã‚¹
					earinga = 21014;
					earingb = 41176;
				} else if (pc.getInventory().checkItem(21006, 1)) { // ãƒŠã‚¤ãƒˆã‚¯ãƒ©ã‚¹
					earinga = 21006;
					earingb = 41177;
				} else if (pc.getInventory().checkItem(21007, 1)) { // ã‚¦ã‚©ãƒ¼ãƒªã‚¢ã‚¯ãƒ©ã‚¹
					earinga = 21007;
					earingb = 41178;
				} else {
					htmlid = "perita36";
				}
				if (earinga > 0) {
					materials = new int[] { earinga };
					counts = new int[] { 1 };
					createitem = new int[] { earingb };
					createcount = new int[] { 1 };
				}
			} else if (s.equals("L")) { // ï¼’æ®µéšŽã‚¤ã‚¢ãƒªãƒ³ã‚°(çŸ¥æ�µã�®ã‚¤ã‚¢ãƒªãƒ³ã‚°)
				if (pc.getInventory().checkEquipped(21015)) {
					htmlid = "perita22";
				} else if (pc.getInventory().checkItem(21015, 1)) {
					materials = new int[] { 21015 };
					counts = new int[] { 1 };
					createitem = new int[] { 41179 };
					createcount = new int[] { 1 };
				} else {
					htmlid = "perita22";
				}
			} else if (s.equals("M")) { // ï¼“æ®µéšŽã‚¤ã‚¢ãƒªãƒ³ã‚°(çœŸå®Ÿã�®ã‚¤ã‚¢ãƒªãƒ³ã‚°)
				if (pc.getInventory().checkEquipped(21016)) {
					htmlid = "perita26";
				} else if (pc.getInventory().checkItem(21016, 1)) {
					materials = new int[] { 21016 };
					counts = new int[] { 1 };
					createitem = new int[] { 41182 };
					createcount = new int[] { 1 };
				} else {
					htmlid = "perita26";
				}
			} else if (s.equals("b")) { // ï¼’æ®µéšŽã‚¤ã‚¢ãƒªãƒ³ã‚°(æƒ…ç†±ã�®ã‚¤ã‚¢ãƒªãƒ³ã‚°)
				if (pc.getInventory().checkEquipped(21009)) {
					htmlid = "perita39";
				} else if (pc.getInventory().checkItem(21009, 1)) {
					materials = new int[] { 21009 };
					counts = new int[] { 1 };
					createitem = new int[] { 41180 };
					createcount = new int[] { 1 };
				} else {
					htmlid = "perita39";
				}
			} else if (s.equals("d")) { // ï¼“æ®µéšŽã‚¤ã‚¢ãƒªãƒ³ã‚°(å��èª‰ã�®ã‚¤ã‚¢ãƒªãƒ³ã‚°)
				if (pc.getInventory().checkEquipped(21012)) {
					htmlid = "perita41";
				} else if (pc.getInventory().checkItem(21012, 1)) {
					materials = new int[] { 21012 };
					counts = new int[] { 1 };
					createitem = new int[] { 41183 };
					createcount = new int[] { 1 };
				} else {
					htmlid = "perita41";
				}
			} else if (s.equals("a")) { // ï¼’æ®µéšŽã‚¤ã‚¢ãƒªãƒ³ã‚°(æ†¤æ€’ã�®ã‚¤ã‚¢ãƒªãƒ³ã‚°)
				if (pc.getInventory().checkEquipped(21008)) {
					htmlid = "perita38";
				} else if (pc.getInventory().checkItem(21008, 1)) {
					materials = new int[] { 21008 };
					counts = new int[] { 1 };
					createitem = new int[] { 41181 };
					createcount = new int[] { 1 };
				} else {
					htmlid = "perita38";
				}
			} else if (s.equals("c")) { // ï¼“æ®µéšŽã‚¤ã‚¢ãƒªãƒ³ã‚°(å‹‡çŒ›ã�®ã‚¤ã‚¢ãƒªãƒ³ã‚°)
				if (pc.getInventory().checkEquipped(21010)) {
					htmlid = "perita40";
				} else if (pc.getInventory().checkItem(21010, 1)) {
					materials = new int[] { 21010 };
					counts = new int[] { 1 };
					createitem = new int[] { 41184 };
					createcount = new int[] { 1 };
				} else {
					htmlid = "perita40";
				}
			}
		}
		// å®�çŸ³ç´°å·¥å¸« ãƒ«ãƒ¼ãƒ ã‚£ã‚¹
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71129) {
			if (s.equals("Z")) {
				htmlid = "rumtis2";
			} else if (s.equals("Y")) {
				if (pc.getInventory().checkItem(41010, 1)) { // ã‚¤ãƒªã‚¹ã�®æŽ¨è–¦æ›¸
					htmlid = "rumtis3";
				} else {
					htmlid = "rumtis4";
				}
			} else if (s.equals("q")) {
				htmlid = "rumtis92";
			} else if (s.equals("A")) {
				if (pc.getInventory().checkItem(41161, 1)) {
					// ãƒŸã‚¹ãƒ†ãƒªã‚¢ã‚¹ãƒ–ãƒ©ãƒƒã‚¯ã‚¤ã‚¢ãƒªãƒ³ã‚°
					htmlid = "rumtis6";
				} else {
					htmlid = "rumtis101";
				}
			} else if (s.equals("B")) {
				if (pc.getInventory().checkItem(41164, 1)) {
					// ãƒŸã‚¹ãƒ†ãƒªã‚¢ã‚¹ã‚¦ã‚£ã‚¶ãƒ¼ãƒ‰ã‚¤ã‚¢ãƒªãƒ³ã‚°
					htmlid = "rumtis7";
				} else {
					htmlid = "rumtis101";
				}
			} else if (s.equals("C")) {
				if (pc.getInventory().checkItem(41167, 1)) {
					// ãƒŸã‚¹ãƒ†ãƒªã‚¢ã‚¹ã‚°ãƒ¬ãƒ¼ã‚¦ã‚£ã‚¶ãƒ¼ãƒ‰ã‚¤ã‚¢ãƒªãƒ³ã‚°
					htmlid = "rumtis8";
				} else {
					htmlid = "rumtis101";
				}
			} else if (s.equals("T")) {
				if (pc.getInventory().checkItem(41167, 1)) {
					// ãƒŸã‚¹ãƒ†ãƒªã‚¢ã‚¹ãƒ›ãƒ¯ã‚¤ãƒˆã‚¦ã‚£ã‚¶ãƒ¼ãƒ‰ã‚¤ã‚¢ãƒªãƒ³ã‚°
					htmlid = "rumtis9";
				} else {
					htmlid = "rumtis101";
				}
			} else if (s.equals("w")) {
				if (pc.getInventory().checkItem(41162, 1)) {
					// ãƒŸã‚¹ãƒ†ãƒªã‚¢ã‚¹ãƒ–ãƒ©ãƒƒã‚¯ã‚¤ã‚¢ãƒªãƒ³ã‚°
					htmlid = "rumtis14";
				} else {
					htmlid = "rumtis101";
				}
			} else if (s.equals("x")) {
				if (pc.getInventory().checkItem(41165, 1)) {
					// ãƒŸã‚¹ãƒ†ãƒªã‚¢ã‚¹ãƒŠã‚¤ãƒˆã‚¤ã‚¢ãƒªãƒ³ã‚°
					htmlid = "rumtis15";
				} else {
					htmlid = "rumtis101";
				}
			} else if (s.equals("y")) {
				if (pc.getInventory().checkItem(41168, 1)) {
					// ãƒŸã‚¹ãƒ†ãƒªã‚¢ã‚¹ã‚°ãƒ¬ãƒ¼ãƒŠã‚¤ãƒˆã‚¤ã‚¢ãƒªãƒ³ã‚°
					htmlid = "rumtis16";
				} else {
					htmlid = "rumtis101";
				}
			} else if (s.equals("z")) {
				if (pc.getInventory().checkItem(41171, 1)) {
					// ãƒŸã‚¹ãƒ†ãƒªã‚¢ã‚¹ãƒ›ãƒ¯ã‚¤ãƒˆãƒŠã‚¤ãƒˆã‚¤ã‚¢ãƒªãƒ³ã‚°
					htmlid = "rumtis17";
				} else {
					htmlid = "rumtis101";
				}
			} else if (s.equals("U")) {
				if (pc.getInventory().checkItem(41163, 1)) {
					// ãƒŸã‚¹ãƒ†ãƒªã‚¢ã‚¹ãƒ–ãƒ©ãƒƒã‚¯ã‚¤ã‚¢ãƒªãƒ³ã‚°
					htmlid = "rumtis10";
				} else {
					htmlid = "rumtis101";
				}
			} else if (s.equals("V")) {
				if (pc.getInventory().checkItem(41166, 1)) {
					// ãƒŸã‚¹ãƒ†ãƒªã‚¢ã‚¹ã‚¦ã‚©ãƒ¼ãƒªã‚¢ã‚¤ã‚¢ãƒªãƒ³ã‚°
					htmlid = "rumtis11";
				} else {
					htmlid = "rumtis101";
				}
			} else if (s.equals("W")) {
				if (pc.getInventory().checkItem(41169, 1)) {
					// ãƒŸã‚¹ãƒ†ãƒªã‚¢ã‚¹ã‚°ãƒ¬ãƒ¼ã‚¦ã‚©ãƒ¼ãƒªã‚¢ã‚¤ã‚¢ãƒªãƒ³ã‚°
					htmlid = "rumtis12";
				} else {
					htmlid = "rumtis101";
				}
			} else if (s.equals("X")) {
				if (pc.getInventory().checkItem(41172, 1)) {
					// ãƒŸã‚¹ãƒ†ãƒªã‚¢ã‚¹ãƒ›ãƒ¯ã‚¤ã‚¦ã‚©ãƒ¼ãƒªã‚¢ã‚¤ã‚¢ãƒªãƒ³ã‚°
					htmlid = "rumtis13";
				} else {
					htmlid = "rumtis101";
				}
			} else if (s.equals("D") || s.equals("E") || s.equals("F")
					|| s.equals("G")) {
				int insn = 0;
				int bacn = 0;
				int me = 0;
				int mr = 0;
				int mj = 0;
				int an = 0;
				int men = 0;
				int mrn = 0;
				int mjn = 0;
				int ann = 0;
				if (pc.getInventory().checkItem(40959, 1) // å†¥æ³•è»�çŽ‹ã�®å�°ç« 
						&& pc.getInventory().checkItem(40960, 1) // é­”éœŠè»�çŽ‹ã�®å�°ç« 
						&& pc.getInventory().checkItem(40961, 1) // é­”ç�£è»�çŽ‹ã�®å�°ç« 
						&& pc.getInventory().checkItem(40962, 1)) { // æš—æ®ºè»�çŽ‹ã�®å�°ç« 
					insn = 1;
					me = 40959;
					mr = 40960;
					mj = 40961;
					an = 40962;
					men = 1;
					mrn = 1;
					mjn = 1;
					ann = 1;
				} else if (pc.getInventory().checkItem(40642, 10) // å†¥æ³•è»�ã�®ãƒ�ãƒƒã‚¸
						&& pc.getInventory().checkItem(40635, 10) // é­”éœŠè»�ã�®ãƒ�ãƒƒã‚¸
						&& pc.getInventory().checkItem(40638, 10) // é­”ç�£è»�ã�®ãƒ�ãƒƒã‚¸
						&& pc.getInventory().checkItem(40667, 10)) { // æš—æ®ºè»�ã�®ãƒ�ãƒƒã‚¸
					bacn = 1;
					me = 40642;
					mr = 40635;
					mj = 40638;
					an = 40667;
					men = 10;
					mrn = 10;
					mjn = 10;
					ann = 10;
				}
				if (pc.getInventory().checkItem(40046, 1) // ã‚µãƒ•ã‚¡ã‚¤ã‚¢
						&& pc.getInventory().checkItem(40618, 5) // å¤§åœ°ã�®æ�¯å�¹
						&& pc.getInventory().checkItem(40643, 5) // æ°´ã�®æ�¯å�¹
						&& pc.getInventory().checkItem(40645, 5) // é¢¨ã�®æ�¯å�¹
						&& pc.getInventory().checkItem(40651, 5) // ç�«ã�®æ�¯å�¹
						&& pc.getInventory().checkItem(40676, 5)) { // é—‡ã�®æ�¯å�¹
					if ((insn == 1) || (bacn == 1)) {
						htmlid = "rumtis60";
						materials = new int[] { me, mr, mj, an, 40046, 40618,
								40643, 40651, 40676 };
						counts = new int[] { men, mrn, mjn, ann, 1, 5, 5, 5, 5,
								5 };
						createitem = new int[] { 40926 }; // åŠ å·¥ã�•ã‚Œã�Ÿã‚µãƒ•ã‚¡ã‚¤ã‚¢ï¼šï¼‘æ®µéšŽ
						createcount = new int[] { 1 };
					} else {
						htmlid = "rumtis18";
					}
				}
			}
		}
		// ã‚¢ã‚¿ãƒ­ã‚¼
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71119) {
			// ã€Œãƒ©ã‚¹ã‚¿ãƒ�ãƒ‰ã�®æ­´å�²æ›¸1ç« ã�‹ã‚‰8ç« ã�¾ã�§å…¨éƒ¨æ¸¡ã�™ã€�
			if (s.equalsIgnoreCase("request las history book")) {
				materials = new int[] { 41019, 41020, 41021, 41022, 41023,
						41024, 41025, 41026 };
				counts = new int[] { 1, 1, 1, 1, 1, 1, 1, 1 };
				createitem = new int[] { 41027 };
				createcount = new int[] { 1 };
				htmlid = "";
			}
		}
		// é•·è€�éš�è¡Œå“¡ã‚¯ãƒ­ãƒ¬ãƒ³ã‚¹
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71170) {
			// ã€Œãƒ©ã‚¹ã‚¿ãƒ�ãƒ‰ã�®æ­´å�²æ›¸ã‚’æ¸¡ã�™ã€�
			if (s.equalsIgnoreCase("request las weapon manual")) {
				materials = new int[] { 41027 };
				counts = new int[] { 1 };
				createitem = new int[] { 40965 };
				createcount = new int[] { 1 };
				htmlid = "";
			}
		}
		// çœŸå†¥çŽ‹ ãƒ€ãƒ³ãƒ†ã‚¹
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71168) {
			// ã€Œç•°ç•Œã�®é­”ç‰©ã�Œã�„ã‚‹å ´æ‰€ã�¸é€�ã�£ã�¦ã��ã� ã�•ã�„ã€�
			if (s.equalsIgnoreCase("a")) {
				if (pc.getInventory().checkItem(41028, 1)) {
					L1Teleport.teleport(pc, 32648, 32921, (short) 535, 6, true);
					pc.getInventory().consumeItem(41028, 1);
				}
			}
		}
		// è«œå ±å“¡(æ¬²æœ›ã�®æ´žçªŸå�´)
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80067) {
			// ã€Œå‹•æ�ºã�—ã�¤ã�¤ã‚‚æ‰¿è«¾ã�™ã‚‹ã€�
			if (s.equalsIgnoreCase("n")) {
				htmlid = "";
				poly(client, 6034);
				final int[] item_ids = { 41132, 41133, 41134 };
				final int[] item_amounts = { 1, 1, 1 };
				for (int i = 0; i < item_ids.length; i++) {
					L1ItemInstance item = pc.getInventory().storeItem(
							item_ids[i], item_amounts[i]);
					pc.sendPackets(new S_ServerMessage(143,
							((L1NpcInstance) obj).getNpcTemplate().get_name(),
							item.getItem().getName()));
					pc.getQuest().set_step(L1Quest.QUEST_DESIRE, 1);
				}
				// ã€Œã��ã‚“ã�ªä»»å‹™ã�¯ã‚„ã‚�ã‚‹ã€�
			} else if (s.equalsIgnoreCase("d")) {
				htmlid = "minicod09";
				pc.getInventory().consumeItem(41130, 1);
				pc.getInventory().consumeItem(41131, 1);
				// ã€Œåˆ�æœŸåŒ–ã�™ã‚‹ã€�
			} else if (s.equalsIgnoreCase("k")) {
				htmlid = "";
				pc.getInventory().consumeItem(41132, 1); // è¡€ç—•ã�®å •è�½ã�—ã�Ÿç²‰
				pc.getInventory().consumeItem(41133, 1); // è¡€ç—•ã�®ç„¡åŠ›ã�—ã�Ÿç²‰
				pc.getInventory().consumeItem(41134, 1); // è¡€ç—•ã�®æˆ‘åŸ·ã�—ã�Ÿç²‰
				pc.getInventory().consumeItem(41135, 1); // ã‚«ãƒ˜ãƒ«ã�®å •è�½ã�—ã�Ÿç²¾é«„
				pc.getInventory().consumeItem(41136, 1); // ã‚«ãƒ˜ãƒ«ã�®ç„¡åŠ›ã�—ã�Ÿç²¾é«„
				pc.getInventory().consumeItem(41137, 1); // ã‚«ãƒ˜ãƒ«ã�®æˆ‘åŸ·ã�—ã�Ÿç²¾é«„
				pc.getInventory().consumeItem(41138, 1); // ã‚«ãƒ˜ãƒ«ã�®ç²¾é«„
				pc.getQuest().set_step(L1Quest.QUEST_DESIRE, 0);
				// ç²¾é«„ã‚’æ¸¡ã�™
			} else if (s.equalsIgnoreCase("e")) {
				if ((pc.getQuest().get_step(L1Quest.QUEST_DESIRE) == L1Quest.QUEST_END)
						|| (pc.getKarmaLevel() >= 1)) {
					htmlid = "";
				} else {
					if (pc.getInventory().checkItem(41138)) {
						htmlid = "";
						pc.addKarma((int) (1600 * Config.RATE_KARMA));
						pc.getInventory().consumeItem(41130, 1); // è¡€ç—•ã�®å¥‘ç´„æ›¸
						pc.getInventory().consumeItem(41131, 1); // è¡€ç—•ã�®æŒ‡ä»¤æ›¸
						pc.getInventory().consumeItem(41138, 1); // ã‚«ãƒ˜ãƒ«ã�®ç²¾é«„
						pc.getQuest().set_step(L1Quest.QUEST_DESIRE,
								L1Quest.QUEST_END);
					} else {
						htmlid = "minicod04";
					}
				}
				// ãƒ—ãƒ¬ã‚¼ãƒ³ãƒˆã‚’ã‚‚ã‚‰ã�†
			} else if (s.equalsIgnoreCase("g")) {
				htmlid = "";
				L1ItemInstance item = pc.getInventory().storeItem(41130, 1); // è¡€ç—•ã�®å¥‘ç´„æ›¸
				pc.sendPackets(new S_ServerMessage(143, ((L1NpcInstance) obj)
						.getNpcTemplate().get_name(), item.getItem().getName()));
			}
		}
		// è«œå ±å“¡(å½±ã�®ç¥žæ®¿å�´)
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 81202) {
			// ã€Œé ­ã�«ã��ã‚‹ã�Œæ‰¿è«¾ã�™ã‚‹ã€�
			if (s.equalsIgnoreCase("n")) {
				htmlid = "";
				poly(client, 6035);
				final int[] item_ids = { 41123, 41124, 41125 };
				final int[] item_amounts = { 1, 1, 1 };
				for (int i = 0; i < item_ids.length; i++) {
					L1ItemInstance item = pc.getInventory().storeItem(
							item_ids[i], item_amounts[i]);
					pc.sendPackets(new S_ServerMessage(143,
							((L1NpcInstance) obj).getNpcTemplate().get_name(),
							item.getItem().getName()));
					pc.getQuest().set_step(L1Quest.QUEST_SHADOWS, 1);
				}
				// ã€Œã��ã‚“ã�ªä»»å‹™ã�¯ã‚„ã‚�ã‚‹ã€�
			} else if (s.equalsIgnoreCase("d")) {
				htmlid = "minitos09";
				pc.getInventory().consumeItem(41121, 1);
				pc.getInventory().consumeItem(41122, 1);
				// ã€Œåˆ�æœŸåŒ–ã�™ã‚‹ã€�
			} else if (s.equalsIgnoreCase("k")) {
				htmlid = "";
				pc.getInventory().consumeItem(41123, 1); // ã‚«ãƒ˜ãƒ«ã�®å •è�½ã�—ã�Ÿç²‰
				pc.getInventory().consumeItem(41124, 1); // ã‚«ãƒ˜ãƒ«ã�®ç„¡åŠ›ã�—ã�Ÿç²‰
				pc.getInventory().consumeItem(41125, 1); // ã‚«ãƒ˜ãƒ«ã�®æˆ‘åŸ·ã�—ã�Ÿç²‰
				pc.getInventory().consumeItem(41126, 1); // è¡€ç—•ã�®å •è�½ã�—ã�Ÿç²¾é«„
				pc.getInventory().consumeItem(41127, 1); // è¡€ç—•ã�®ç„¡åŠ›ã�—ã�Ÿç²¾é«„
				pc.getInventory().consumeItem(41128, 1); // è¡€ç—•ã�®æˆ‘åŸ·ã�—ã�Ÿç²¾é«„
				pc.getInventory().consumeItem(41129, 1); // è¡€ç—•ã�®ç²¾é«„
				pc.getQuest().set_step(L1Quest.QUEST_SHADOWS, 0);
				// ç²¾é«„ã‚’æ¸¡ã�™
			} else if (s.equalsIgnoreCase("e")) {
				if ((pc.getQuest().get_step(L1Quest.QUEST_SHADOWS) == L1Quest.QUEST_END)
						|| (pc.getKarmaLevel() >= 1)) {
					htmlid = "";
				} else {
					if (pc.getInventory().checkItem(41129)) {
						htmlid = "";
						pc.addKarma((int) (-1600 * Config.RATE_KARMA));
						pc.getInventory().consumeItem(41121, 1); // ã‚«ãƒ˜ãƒ«ã�®å¥‘ç´„æ›¸
						pc.getInventory().consumeItem(41122, 1); // ã‚«ãƒ˜ãƒ«ã�®æŒ‡ä»¤æ›¸
						pc.getInventory().consumeItem(41129, 1); // è¡€ç—•ã�®ç²¾é«„
						pc.getQuest().set_step(L1Quest.QUEST_SHADOWS,
								L1Quest.QUEST_END);
					} else {
						htmlid = "minitos04";
					}
				}
				// ç´ æ—©ã��å�—å�–ã‚‹
			} else if (s.equalsIgnoreCase("g")) {
				htmlid = "";
				L1ItemInstance item = pc.getInventory().storeItem(41121, 1); // ã‚«ãƒ˜ãƒ«ã�®å¥‘ç´„æ›¸
				pc.sendPackets(new S_ServerMessage(143, ((L1NpcInstance) obj)
						.getNpcTemplate().get_name(), item.getItem().getName()));
			}
		}
		// [Legends] - Joe Golem Magic Weapons
		//else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71252) {
        else if (1==2) {
			int weapon1 = 0;
			int weapon2 = 0;
			int newWeapon = 0;
			if (s.equalsIgnoreCase("A")) {
				weapon1 = 5; // +7ã‚¨ãƒ«ãƒ´ãƒ³ãƒ€ã‚¬ãƒ¼
				weapon2 = 6; // +7ãƒ©ã‚¹ã‚¿ãƒ�ãƒ‰ãƒ€ã‚¬ãƒ¼
				newWeapon = 259; // ãƒžãƒŠãƒ�ãƒ¼ãƒ©ãƒ¼ãƒ‰
				htmlid = "joegolem9";
			} else if (s.equalsIgnoreCase("B")) {
				weapon1 = 145; // +7ãƒ�ãƒ¼ã‚µãƒ¼ã‚«ãƒ¼ã‚¢ãƒƒã‚¯ã‚¹
				weapon2 = 148; // +7ã‚°ãƒ¬ãƒ¼ãƒˆã‚¢ãƒƒã‚¯ã‚¹
				newWeapon = 260; // ãƒ¬ã‚¤ã‚¸ãƒ³ã‚°ã‚¦ã‚£ãƒ³ãƒ‰
				htmlid = "joegolem10";
			} else if (s.equalsIgnoreCase("C")) {
				weapon1 = 52; // +7ãƒ„ãƒ¼ãƒ�ãƒ³ãƒ‰ã‚½ãƒ¼ãƒ‰
				weapon2 = 64; // +7ã‚°ãƒ¬ãƒ¼ãƒˆã‚½ãƒ¼ãƒ‰
				newWeapon = 262; // ãƒ‡ã‚£ã‚¹ãƒˆãƒ©ã‚¯ã‚·ãƒ§ãƒ³
				htmlid = "joegolem11";
			} else if (s.equalsIgnoreCase("D")) {
				weapon1 = 125; // +7ã‚½ãƒ¼ã‚µãƒªãƒ¼ã‚¹ã‚¿ãƒƒãƒ•
				weapon2 = 129; // +7ãƒ¡ã‚¤ã‚¸ã‚¹ã‚¿ãƒƒãƒ•
				newWeapon = 261; // ã‚¢ãƒ¼ã‚¯ãƒ¡ã‚¤ã‚¸ã‚¹ã‚¿ãƒƒãƒ•
				htmlid = "joegolem12";
			} else if (s.equalsIgnoreCase("E")) {
				weapon1 = 99; // +7ã‚¨ãƒ«ãƒ–ãƒ³ã‚¹ãƒ”ã‚¢ãƒ¼
				weapon2 = 104; // +7ãƒ•ã‚©ãƒ�ãƒ£ãƒ¼ãƒ‰
				newWeapon = 263; // ãƒ•ãƒªãƒ¼ã‚¸ãƒ³ã‚°ãƒ©ãƒ³ã‚µãƒ¼
				htmlid = "joegolem13";
			} else if (s.equalsIgnoreCase("F")) {
				weapon1 = 32; // +7ã‚°ãƒ©ãƒ‡ã‚£ã‚¦ã‚¹
				weapon2 = 42; // +7ãƒ¬ã‚¤ãƒ”ã‚¢
				newWeapon = 264; // ãƒ©ã‚¤ãƒˆãƒ‹ãƒ³ã‚°ã‚¨ãƒƒã‚¸
				htmlid = "joegolem14";
			}
			if (pc.getInventory().checkEnchantItem(weapon1, 7, 1)
					&& pc.getInventory().checkEnchantItem(weapon2, 7, 1)
					&& pc.getInventory().checkItem(41246, 1000) // çµ�æ™¶ä½“
					&& pc.getInventory().checkItem(49143, 10)) { // å‹‡æ°—ã�®çµ�æ™¶
				pc.getInventory().consumeEnchantItem(weapon1, 7, 1);
				pc.getInventory().consumeEnchantItem(weapon2, 7, 1);
				pc.getInventory().consumeItem(41246, 1000);
				pc.getInventory().consumeItem(49143, 10);
				L1ItemInstance item = pc.getInventory().storeItem(newWeapon, 1);
				pc.sendPackets(new S_ServerMessage(143, ((L1NpcInstance) obj)
						.getNpcTemplate().get_name(), item.getItem().getName()));
			} else {
				htmlid = "joegolem15";
				if (!pc.getInventory().checkEnchantItem(weapon1, 7, 1)) {
					pc.sendPackets(new S_ServerMessage(337, "+7 "
							+ ItemTable.getInstance().getTemplate(weapon1)
									.getName())); // \f1%0ã�Œä¸�è¶³ã�—ã�¦ã�„ã�¾ã�™ã€‚
				}
				if (!pc.getInventory().checkEnchantItem(weapon2, 7, 1)) {
					pc.sendPackets(new S_ServerMessage(337, "+7 "
							+ ItemTable.getInstance().getTemplate(weapon2)
									.getName())); // \f1%0ã�Œä¸�è¶³ã�—ã�¦ã�„ã�¾ã�™ã€‚
				}
				if (!pc.getInventory().checkItem(41246, 1000)) {
					int itemCount = 0;
					itemCount = 1000 - pc.getInventory().countItems(41246);
					pc.sendPackets(new S_ServerMessage(337, ItemTable
							.getInstance().getTemplate(41246).getName()
							+ "(" + itemCount + ")")); // \f1%0ã�Œä¸�è¶³ã�—ã�¦ã�„ã�¾ã�™ã€‚
				}
				if (!pc.getInventory().checkItem(49143, 10)) {
					int itemCount = 0;
					itemCount = 10 - pc.getInventory().countItems(49143);
					pc.sendPackets(new S_ServerMessage(337, ItemTable
							.getInstance().getTemplate(49143).getName()
							+ "(" + itemCount + ")")); // \f1%0ã�Œä¸�è¶³ã�—ã�¦ã�„ã�¾ã�™ã€‚
				}
			}
		}
		// ã‚¾ã‚¦ã�®ã‚¹ãƒˆãƒ¼ãƒ³ã‚´ãƒ¼ãƒ¬ãƒ  ãƒ†ãƒ¼ãƒ™ç ‚æ¼ 
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71253) {
			// ã€Œæ­ªã�¿ã�®ã‚³ã‚¢ã‚’ä½œã‚‹ã€�
			if (s.equalsIgnoreCase("A")) {
				if (pc.getInventory().checkItem(49101, 100)) {
					materials = new int[] { 49101 };
					counts = new int[] { 100 };
					createitem = new int[] { 49092 };
					createcount = new int[] { 1 };
					htmlid = "joegolem18";
				} else {
					htmlid = "joegolem19";
				}
			} else if (s.equalsIgnoreCase("B")) {
				if (pc.getInventory().checkItem(49101, 1)) {
					pc.getInventory().consumeItem(49101, 1);
					L1Teleport.teleport(pc, 33966, 33253, (short) 4, 5, true);
					htmlid = "";
				} else {
					htmlid = "joegolem20";
				}
			}
		}
		// ãƒ†ãƒ¼ãƒ™ ã‚ªã‚·ãƒªã‚¹ç¥­å£‡ã�®ã‚­ãƒ¼ãƒ‘ãƒ¼
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71255) {
			// ã€Œãƒ†ãƒ¼ãƒ™ã‚ªã‚·ãƒªã‚¹ç¥­å£‡ã�®é�µã‚’æŒ�ã�£ã�¦ã�„ã‚‹ã�ªã‚‰ã€�ã‚ªã‚·ãƒªã‚¹ã�®ç¥­å£‡ã�«ã�Šé€�ã‚Šã�—ã�¾ã�—ã‚‡ã�†ã€‚ã€�
			if (s.equalsIgnoreCase("e")) {
				if (pc.getInventory().checkItem(49242, 1)) { // é�µã�®ãƒ�ã‚§ãƒƒã‚¯(20äººé™�å®š/æ™‚ã�®æ­ªã�¿ã�Œç�¾ã‚Œã�¦ã�‹ã‚‰2h30ã�¯æœªå®Ÿè£…)
					pc.getInventory().consumeItem(49242, 1);
					L1Teleport.teleport(pc, 32735, 32831, (short) 782, 2, true);
					htmlid = "";
				} else {
					htmlid = "tebegate3";
					// ã€Œä¸Šé™�äººæ•°ã�«é�”ã�—ã�¦ã�„ã‚‹å ´å�ˆã�¯ã€�
					// htmlid = "tebegate4";
				}
			}
		}
		// ãƒ­ãƒ“ãƒ³ãƒ•ãƒƒãƒ‰
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71256) {
			if (s.equalsIgnoreCase("E")) {
				if ((pc.getQuest().get_step(L1Quest.QUEST_MOONOFLONGBOW) == 8)
						&& pc.getInventory().checkItem(40491, 30)
						&& pc.getInventory().checkItem(40495, 40)
						&& pc.getInventory().checkItem(100, 1)
						&& pc.getInventory().checkItem(40509, 12)
						&& pc.getInventory().checkItem(40052, 1)
						&& pc.getInventory().checkItem(40053, 1)
						&& pc.getInventory().checkItem(40054, 1)
						&& pc.getInventory().checkItem(40055, 1)
						&& pc.getInventory().checkItem(41347, 1)
						&& pc.getInventory().checkItem(41350, 1)) {
					pc.getInventory().consumeItem(40491, 30);
					pc.getInventory().consumeItem(40495, 40);
					pc.getInventory().consumeItem(100, 1);
					pc.getInventory().consumeItem(40509, 12);
					pc.getInventory().consumeItem(40052, 1);
					pc.getInventory().consumeItem(40053, 1);
					pc.getInventory().consumeItem(40054, 1);
					pc.getInventory().consumeItem(40055, 1);
					pc.getInventory().consumeItem(41347, 1);
					pc.getInventory().consumeItem(41350, 1);
					htmlid = "robinhood12";
					pc.getInventory().storeItem(205, 1);
//					pc.getQuest().set_step(L1Quest.QUEST_MOONOFLONGBOW,
//							L1Quest.QUEST_END);
					pc.getQuest().set_step(L1Quest.QUEST_MOONOFLONGBOW, 0); // makes the quest repeatable
				}
			} else if (s.equalsIgnoreCase("C")) {
				if (pc.getQuest().get_step(L1Quest.QUEST_MOONOFLONGBOW) == 7) {
					if (pc.getInventory().checkItem(41352, 4)
							&& pc.getInventory().checkItem(40618, 30)
							&& pc.getInventory().checkItem(40643, 30)
							&& pc.getInventory().checkItem(40645, 30)
							&& pc.getInventory().checkItem(40651, 30)
							&& pc.getInventory().checkItem(40676, 30)
							&& pc.getInventory().checkItem(40514, 20)
							&& pc.getInventory().checkItem(41351, 1)
							&& pc.getInventory().checkItem(41346, 1)) {
						pc.getInventory().consumeItem(41352, 4);
						pc.getInventory().consumeItem(40618, 30);
						pc.getInventory().consumeItem(40643, 30);
						pc.getInventory().consumeItem(40645, 30);
						pc.getInventory().consumeItem(40651, 30);
						pc.getInventory().consumeItem(40676, 30);
						pc.getInventory().consumeItem(40514, 20);
						pc.getInventory().consumeItem(41351, 1);
						pc.getInventory().consumeItem(41346, 1);
						pc.getInventory().storeItem(41347, 1);
						pc.getInventory().storeItem(41350, 1);
						htmlid = "robinhood10";
						pc.getQuest().set_step(L1Quest.QUEST_MOONOFLONGBOW, 8);
					}
				}
			} else if (s.equalsIgnoreCase("B")) {
				if (pc.getInventory().checkItem(41348)
						&& pc.getInventory().checkItem(41346)) {
					htmlid = "robinhood13";
				} else {
					pc.getInventory().storeItem(41348, 1);
					pc.getInventory().storeItem(41346, 1);
					htmlid = "robinhood13";
					pc.getQuest().set_step(L1Quest.QUEST_MOONOFLONGBOW, 2);
				}
			} else if (s.equalsIgnoreCase("A")) {
				if (pc.getInventory().checkItem(40028)) {
					pc.getInventory().consumeItem(40028, 1);
					htmlid = "robinhood4";
					pc.getQuest().set_step(L1Quest.QUEST_MOONOFLONGBOW, 1);
				} else {
					htmlid = "robinhood19";
				}
			}
		}
		// ã‚¸ãƒ–ãƒªãƒ«
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71257) {
			if (s.equalsIgnoreCase("D")) {
				if (pc.getInventory().checkItem(41349)) {
					htmlid = "zybril10";
					pc.getInventory().storeItem(41351, 1);
					pc.getInventory().consumeItem(41349, 1);
					pc.getQuest().set_step(L1Quest.QUEST_MOONOFLONGBOW, 7);
				} else {
					htmlid = "zybril14";
				}
			} else if (s.equalsIgnoreCase("C")) {
				if (pc.getInventory().checkItem(40514, 10)
						&& pc.getInventory().checkItem(41353)) {
					pc.getInventory().consumeItem(40514, 10);
					pc.getInventory().consumeItem(41353, 1);
					pc.getInventory().storeItem(41354, 1);
					htmlid = "zybril9";
					pc.getQuest().set_step(L1Quest.QUEST_MOONOFLONGBOW, 6);
				}
			} else if (pc.getInventory().checkItem(41353)
					&& pc.getInventory().checkItem(40514, 10)) {
				htmlid = "zybril8";
			} else if (s.equalsIgnoreCase("B")) {
				if (pc.getInventory().checkItem(40048, 10)
						&& pc.getInventory().checkItem(40049, 10)
						&& pc.getInventory().checkItem(40050, 10)
						&& pc.getInventory().checkItem(40051, 10)) {
					pc.getInventory().consumeItem(40048, 10);
					pc.getInventory().consumeItem(40049, 10);
					pc.getInventory().consumeItem(40050, 10);
					pc.getInventory().consumeItem(40051, 10);
					pc.getInventory().storeItem(41353, 1);
					htmlid = "zybril15";
					pc.getQuest().set_step(L1Quest.QUEST_MOONOFLONGBOW, 5);
				} else {
					htmlid = "zybril12";
					pc.getQuest().set_step(L1Quest.QUEST_MOONOFLONGBOW, 4);
				}
			} else if (s.equalsIgnoreCase("A")) {
				if (pc.getInventory().checkItem(41348)
						&& pc.getInventory().checkItem(41346)) {
					htmlid = "zybril3";
					pc.getQuest().set_step(L1Quest.QUEST_MOONOFLONGBOW, 3);
				} else {
					htmlid = "zybril11";
				}
			}
		}
		// ãƒžãƒ«ãƒ�
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71258) {
			if (pc.getInventory().checkItem(40665)) {
				htmlid = "marba17";
				if (s.equalsIgnoreCase("B")) {
					htmlid = "marba7";
					if (pc.getInventory().checkItem(214)
							&& pc.getInventory().checkItem(20389)
							&& pc.getInventory().checkItem(20393)
							&& pc.getInventory().checkItem(20401)
							&& pc.getInventory().checkItem(20406)
							&& pc.getInventory().checkItem(20409)) {
						htmlid = "marba15";
					}
				}
			} else if (s.equalsIgnoreCase("A")) {
				if (pc.getInventory().checkItem(40637)) {
					htmlid = "marba20";
				} else {
					L1NpcInstance npc = (L1NpcInstance) obj;
					L1ItemInstance item = pc.getInventory().storeItem(40637, 1);
					String npcName = npc.getNpcTemplate().get_name();
					String itemName = item.getItem().getName();
					pc.sendPackets(new S_ServerMessage(143, npcName, itemName));
					htmlid = "marba6";
				}
			}
		}
		// ã‚¢ãƒ©ã‚¹
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71259) {
			if (pc.getInventory().checkItem(40665)) {
				htmlid = "aras8";
			} else if (pc.getInventory().checkItem(40637)) {
				htmlid = "aras1";
				if (s.equalsIgnoreCase("A")) {
					if (pc.getInventory().checkItem(40664)) {
						htmlid = "aras6";
						if (pc.getInventory().checkItem(40679)
								|| pc.getInventory().checkItem(40680)
								|| pc.getInventory().checkItem(40681)
								|| pc.getInventory().checkItem(40682)
								|| pc.getInventory().checkItem(40683)
								|| pc.getInventory().checkItem(40684)
								|| pc.getInventory().checkItem(40693)
								|| pc.getInventory().checkItem(40694)
								|| pc.getInventory().checkItem(40695)
								|| pc.getInventory().checkItem(40697)
								|| pc.getInventory().checkItem(40698)
								|| pc.getInventory().checkItem(40699)) {
							htmlid = "aras3";
						} else {
							htmlid = "aras6";
						}
					} else {
						L1NpcInstance npc = (L1NpcInstance) obj;
						L1ItemInstance item = pc.getInventory().storeItem(
								40664, 1);
						String npcName = npc.getNpcTemplate().get_name();
						String itemName = item.getItem().getName();
						pc.sendPackets(new S_ServerMessage(143, npcName,
								itemName));
						htmlid = "aras6";
					}
				} else if (s.equalsIgnoreCase("B")) {
					if (pc.getInventory().checkItem(40664)) {
						pc.getInventory().consumeItem(40664, 1);
						L1NpcInstance npc = (L1NpcInstance) obj;
						L1ItemInstance item = pc.getInventory().storeItem(
								40665, 1);
						String npcName = npc.getNpcTemplate().get_name();
						String itemName = item.getItem().getName();
						pc.sendPackets(new S_ServerMessage(143, npcName,
								itemName));
						htmlid = "aras13";
					} else {
						htmlid = "aras14";
						L1NpcInstance npc = (L1NpcInstance) obj;
						L1ItemInstance item = pc.getInventory().storeItem(
								40665, 1);
						String npcName = npc.getNpcTemplate().get_name();
						String itemName = item.getItem().getName();
						pc.sendPackets(new S_ServerMessage(143, npcName,
								itemName));
					}
				} else {
					if (s.equalsIgnoreCase("7")) {
						if (pc.getInventory().checkItem(40693)
								&& pc.getInventory().checkItem(40694)
								&& pc.getInventory().checkItem(40695)
								&& pc.getInventory().checkItem(40697)
								&& pc.getInventory().checkItem(40698)
								&& pc.getInventory().checkItem(40699)) {
							htmlid = "aras10";
						} else {
							htmlid = "aras9";
						}
					}
				}
			} else {
				htmlid = "aras7";
			}
		}
		// æ²»å®‰å›£é•·ãƒ©ãƒ«ã‚½ãƒ³
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80099) {
			if (s.equalsIgnoreCase("A")) {
				if (pc.getInventory().checkItem(L1ItemId.ADENA, 300)) {
					pc.getInventory().consumeItem(L1ItemId.ADENA, 300);
					pc.getInventory().storeItem(41315, 1);
					pc.getQuest().set_step(
							L1Quest.QUEST_GENERALHAMELOFRESENTMENT, 1);
					htmlid = "rarson16";
				} else if (!pc.getInventory().checkItem(L1ItemId.ADENA, 300)) {
					htmlid = "rarson7";
				}
			} else if (s.equalsIgnoreCase("B")) {
				if ((pc.getQuest().get_step(
						L1Quest.QUEST_GENERALHAMELOFRESENTMENT) == 1)
						&& (pc.getInventory().checkItem(41325, 1))) {
					pc.getInventory().consumeItem(41325, 1);
					pc.getInventory().storeItem(L1ItemId.ADENA, 2000);
					pc.getInventory().storeItem(41317, 1);
					pc.getQuest().set_step(
							L1Quest.QUEST_GENERALHAMELOFRESENTMENT, 2);
					htmlid = "rarson9";
				} else {
					htmlid = "rarson10";
				}
			} else if (s.equalsIgnoreCase("C")) {
				if ((pc.getQuest().get_step(
						L1Quest.QUEST_GENERALHAMELOFRESENTMENT) == 4)
						&& (pc.getInventory().checkItem(41326, 1))) {
					pc.getInventory().storeItem(L1ItemId.ADENA, 30000);
					pc.getInventory().consumeItem(41326, 1);
					htmlid = "rarson12";
					pc.getQuest().set_step(
							L1Quest.QUEST_GENERALHAMELOFRESENTMENT, 5);
				} else {
					htmlid = "rarson17";
				}
			} else if (s.equalsIgnoreCase("D")) {
				if ((pc.getQuest().get_step(
						L1Quest.QUEST_GENERALHAMELOFRESENTMENT) <= 1)
						|| (pc.getQuest().get_step(
								L1Quest.QUEST_GENERALHAMELOFRESENTMENT) == 5)) {
					if (pc.getInventory().checkItem(L1ItemId.ADENA, 300)) {
						pc.getInventory().consumeItem(L1ItemId.ADENA, 300);
						pc.getInventory().storeItem(41315, 1);
						pc.getQuest().set_step(
								L1Quest.QUEST_GENERALHAMELOFRESENTMENT, 1);
						htmlid = "rarson16";
					} else if (!pc.getInventory().checkItem(L1ItemId.ADENA, 300)) {
						htmlid = "rarson7";
					}
				} else if ((pc.getQuest().get_step(
						L1Quest.QUEST_GENERALHAMELOFRESENTMENT) >= 2)
						&& (pc.getQuest().get_step(
								L1Quest.QUEST_GENERALHAMELOFRESENTMENT) <= 4)) {
					if (pc.getInventory().checkItem(L1ItemId.ADENA, 300)) {
						pc.getInventory().consumeItem(L1ItemId.ADENA, 300);
						pc.getInventory().storeItem(41315, 1);
						htmlid = "rarson16";
					} else if (!pc.getInventory().checkItem(L1ItemId.ADENA, 300)) {
						htmlid = "rarson7";
					}
				}
			}
		}
		// ã‚¯ã‚¨ãƒ³
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80101) {
			if (s.equalsIgnoreCase("request letter of kuen")) {
				if ((pc.getQuest().get_step(
						L1Quest.QUEST_GENERALHAMELOFRESENTMENT) == 2)
						&& (pc.getInventory().checkItem(41317, 1))) {
					pc.getInventory().consumeItem(41317, 1);
					pc.getInventory().storeItem(41318, 1);
					pc.getQuest().set_step(
							L1Quest.QUEST_GENERALHAMELOFRESENTMENT, 3);
					htmlid = "";
				} else {
					htmlid = "";
				}
			} else if (s.equalsIgnoreCase("request holy mithril dust")) {
				if ((pc.getQuest().get_step(
						L1Quest.QUEST_GENERALHAMELOFRESENTMENT) == 3)
						&& (pc.getInventory().checkItem(41315, 1))
						&& pc.getInventory().checkItem(40494, 30)
						&& pc.getInventory().checkItem(41318, 1)) {
					pc.getInventory().consumeItem(41315, 1);
					pc.getInventory().consumeItem(41318, 1);
					pc.getInventory().consumeItem(40494, 30);
					pc.getInventory().storeItem(41316, 1);
					pc.getQuest().set_step(
							L1Quest.QUEST_GENERALHAMELOFRESENTMENT, 4);
					htmlid = "";
				} else {
					htmlid = "";
				}
			}
		}

		// é•·è€� æ™®æ´›å‡±çˆ¾
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80136) {
			int lv15_step = pc.getQuest().get_step(L1Quest.QUEST_LEVEL15);
			int lv30_step = pc.getQuest().get_step(L1Quest.QUEST_LEVEL30);
			int lv45_step = pc.getQuest().get_step(L1Quest.QUEST_LEVEL45);
			int lv50_step = pc.getQuest().get_step(L1Quest.QUEST_LEVEL50);
			if (pc.isDragonKnight()) {
				// åŸ·è¡Œæ™®æ´›å‡±çˆ¾çš„èª²é¡Œ
				if (s.equalsIgnoreCase("a") && (lv15_step == 0)) {
					L1NpcInstance npc = (L1NpcInstance) obj;
					L1ItemInstance item = pc.getInventory().storeItem(49210, 1); // æ™®æ´›å‡±çˆ¾çš„ç¬¬ä¸€æ¬¡æŒ‡ä»¤æ›¸
					String npcName = npc.getNpcTemplate().get_name();
					String itemName = item.getItem().getName();
					pc.sendPackets(new S_ServerMessage(143, npcName, itemName)); // \f1%0ã�Œ%1ã‚’ã��ã‚Œã�¾ã�—ã�Ÿã€‚
					pc.getQuest().set_step(L1Quest.QUEST_LEVEL15, 1);
					htmlid = "prokel3";
				// åŸ·è¡Œæ™®æ´›å‡±çˆ¾çš„ç¬¬äºŒæ¬¡èª²é¡Œ
				} else if (s.equalsIgnoreCase("c") && (lv30_step == 0)) {
					final int[] item_ids = { 49211, 49215, }; // æ™®æ´›å‡±çˆ¾çš„ç¬¬äºŒæ¬¡æŒ‡ä»¤æ›¸ã€�æ™®æ´›å‡±çˆ¾çš„ç¤¦ç‰©è¢‹
					final int[] item_amounts = { 1, 1, };
					for (int i = 0; i < item_ids.length; i++) {
						L1ItemInstance item = pc.getInventory().storeItem(
								item_ids[i], item_amounts[i]);
						pc.sendPackets(new S_ServerMessage(143,
								((L1NpcInstance) obj).getNpcTemplate()
										.get_name(), item.getItem().getName()));
					}
					pc.getQuest().set_step(L1Quest.QUEST_LEVEL30, 1);
					htmlid = "prokel9";
				// éœ€è¦�æ™®æ´›å‡±çˆ¾çš„ç¤¦ç‰©è¢‹
				} else if (s.equalsIgnoreCase("e")) {
					if (pc.getInventory().checkItem(49215, 1)) {
						htmlid = "prokel35";
					} else {
						L1NpcInstance npc = (L1NpcInstance) obj;
						L1ItemInstance item = pc.getInventory().storeItem(
								49215, 1); // æ™®æ´›å‡±çˆ¾çš„ç¤¦ç‰©è¢‹
						String npcName = npc.getNpcTemplate().get_name();
						String itemName = item.getItem().getName();
						pc.sendPackets(new S_ServerMessage(143, npcName,
								itemName)); // \f1%0ã�Œ%1ã‚’ã��ã‚Œã�¾ã�—ã�Ÿã€‚
						htmlid = "prokel13";
					}
				// åŸ·è¡Œæ™®æ´›å‡±çˆ¾çš„ç¬¬ä¸‰æ¬¡èª²é¡Œ
				} else if (s.equalsIgnoreCase("f") && (lv45_step == 0)) {
					final int[] item_ids = { 49209, 49212, 49226, }; // é•·è€�æ™®æ´›å‡±çˆ¾çš„ä¿¡ä»¶ã€�æ™®æ´›å‡±çˆ¾çš„ç¬¬ä¸‰æ¬¡æŒ‡ä»¤æ›¸ã€�çµ�ç›Ÿçž¬é–“ç§»å‹•å�·è»¸
					final int[] item_amounts = { 1, 1, 1, };
					for (int i = 0; i < item_ids.length; i++) {
						L1ItemInstance item = pc.getInventory().storeItem(
								item_ids[i], item_amounts[i]);
						pc.sendPackets(new S_ServerMessage(143,
								((L1NpcInstance) obj).getNpcTemplate()
										.get_name(), item.getItem().getName()));
					}
					pc.getQuest().set_step(L1Quest.QUEST_LEVEL45, 1);
					htmlid = "prokel16";
				// åŸ·è¡Œæ™®æ´›å‡±çˆ¾çš„ç¬¬å››æ¬¡èª²é¡Œ
				} else if (s.equalsIgnoreCase("h") && (lv50_step == 0)) {
					final int[] item_ids = { 49287, }; // æ™®æ´›å‡±çˆ¾çš„ç¬¬å››æ¬¡æŒ‡ä»¤æ›¸
					final int[] item_amounts = { 1, };
					for (int i = 0; i < item_ids.length; i++) {
						L1ItemInstance item = pc.getInventory().storeItem(
								item_ids[i], item_amounts[i]);
						pc.sendPackets(new S_ServerMessage(143,
								((L1NpcInstance) obj).getNpcTemplate()
										.get_name(), item.getItem().getName()));
					}
					pc.getQuest().set_step(L1Quest.QUEST_LEVEL50, 1);
					htmlid = "prokel22";
				// é‡�æ–°æŽ¥æ”¶æ™‚ç©ºè£‚ç—•é‚ªå¿µç¢Žç‰‡ã€�æ™®æ´›å‡±çˆ¾çš„è­·èº«ç¬¦
				} else if (s.equalsIgnoreCase("k") && (lv50_step >= 2)) {
					if (pc.getInventory().checkItem(49202, 1)
							|| pc.getInventory().checkItem(49216, 1)) {
						htmlid = "prokel29";
					} else {
						final int[] item_ids = { 49202, 49216, };
						final int[] item_amounts = { 1, 1, };
						for (int i = 0; i < item_ids.length; i++) {
							L1ItemInstance item = pc.getInventory().storeItem(
									item_ids[i], item_amounts[i]);
							pc.sendPackets(new S_ServerMessage(143,
									((L1NpcInstance) obj).getNpcTemplate()
									.get_name(), item.getItem().getName()));
						}
						htmlid = "prokel28";
					}
				}
			}
		}

		/*
		 * // é•·è€� ã‚·ãƒ«ãƒ¬ã‚¤ãƒ³ else if (((L1NpcInstance)
		 * obj).getNpcTemplate().get_npcId() == 80145) {// ä½µåˆ° å¹»è¡“å£« è©¦ç…‰ if
		 * (pc.isDragonKnight()) { int lv45_step =
		 * pc.getQuest().get_step(L1Quest.QUEST_LEVEL45); // ã€Œãƒ—ãƒ­ã‚±ãƒ«ã�®æ‰‹ç´™ã‚’æ¸¡ã�™ã€� if
		 * (s.equalsIgnoreCase("l") && (lv45_step == 1)) { if
		 * (pc.getInventory().checkItem(49209, 1)) { // check
		 * pc.getInventory().consumeItem(49209, 1); // del
		 * pc.getQuest().set_step(L1Quest.QUEST_LEVEL45, 2); htmlid =
		 * "silrein38"; } } else if (s.equalsIgnoreCase("m") && (lv45_step ==
		 * 2)) { pc.getQuest().set_step(L1Quest.QUEST_LEVEL45, 3); htmlid =
		 * "silrein39"; } } }
		 */

		// ã‚¨ãƒ«ãƒ©ã‚¹
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80135) {
			if (pc.isDragonKnight()) {
				// ã€Œã‚ªãƒ¼ã‚¯å¯†ä½¿å¤‰èº«ã‚¹ã‚¯ãƒ­ãƒ¼ãƒ«ã‚’å�—ã�‘å�–ã‚‹ã€�
				if (s.equalsIgnoreCase("a")) {
					if (pc.getInventory().checkItem(49220, 1)) {
						htmlid = "elas5";
					} else {
						L1NpcInstance npc = (L1NpcInstance) obj;
						L1ItemInstance item = pc.getInventory().storeItem(
								49220, 1); // ã‚ªãƒ¼ã‚¯å¯†ä½¿å¤‰èº«ã‚¹ã‚¯ãƒ­ãƒ¼ãƒ«
						String npcName = npc.getNpcTemplate().get_name();
						String itemName = item.getItem().getName();
						pc.sendPackets(new S_ServerMessage(143, npcName,
								itemName)); // \f1%0ã�Œ%1ã‚’ã��ã‚Œã�¾ã�—ã�Ÿã€‚
						htmlid = "elas4";
					}
				}
			}
		}

		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 81245) { // ã‚ªãƒ¼ã‚¯å¯†ä½¿(HC3)
			if (pc.isDragonKnight()) {
				if (s.equalsIgnoreCase("request flute of spy")) {
					if (pc.getInventory().checkItem(49223, 1)) { // check
						pc.getInventory().consumeItem(49223, 1); // del
						L1NpcInstance npc = (L1NpcInstance) obj;
						L1ItemInstance item = pc.getInventory().storeItem(
								49222, 1); // ã‚ªãƒ¼ã‚¯å¯†ä½¿ã�®ç¬›
						String npcName = npc.getNpcTemplate().get_name();
						String itemName = item.getItem().getName();
						pc.sendPackets(new S_ServerMessage(143, npcName,
								itemName)); // \f1%0ã�Œ%1ã‚’ã��ã‚Œã�¾ã�—ã�Ÿã€‚
						htmlid = "";
					} else {
						htmlid = "";
					}
				}
			}
		}

		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 81246) { // ã‚·ãƒ£ãƒ«ãƒŠ
			if (s.equalsIgnoreCase("0")) {
				materials = new int[] { L1ItemId.ADENA };
				counts = new int[] { 2500 };
				if (pc.getLevel() < 30) {
					htmlid = "sharna4";
				} else if ((pc.getLevel() >= 30) && (pc.getLevel() <= 39)) {
					createitem = new int[] { 49149 }; // ã‚·ãƒ£ãƒ«ãƒŠã�®å¤‰èº«ã‚¹ã‚¯ãƒ­ãƒ¼ãƒ«ï¼ˆãƒ¬ãƒ™ãƒ«30ï¼‰
					createcount = new int[] { 1 };
				} else if ((pc.getLevel() >= 40) && (pc.getLevel() <= 51)) {
					createitem = new int[] { 49150 }; // ã‚·ãƒ£ãƒ«ãƒŠã�®å¤‰èº«ã‚¹ã‚¯ãƒ­ãƒ¼ãƒ«ï¼ˆãƒ¬ãƒ™ãƒ«40ï¼‰
					createcount = new int[] { 1 };
				} else if ((pc.getLevel() >= 52) && (pc.getLevel() <= 54)) {
					createitem = new int[] { 49151 }; // ã‚·ãƒ£ãƒ«ãƒŠã�®å¤‰èº«ã‚¹ã‚¯ãƒ­ãƒ¼ãƒ«ï¼ˆãƒ¬ãƒ™ãƒ«52ï¼‰
					createcount = new int[] { 1 };
				} else if ((pc.getLevel() >= 55) && (pc.getLevel() <= 59)) {
					createitem = new int[] { 49152 }; // ã‚·ãƒ£ãƒ«ãƒŠã�®å¤‰èº«ã‚¹ã‚¯ãƒ­ãƒ¼ãƒ«ï¼ˆãƒ¬ãƒ™ãƒ«55ï¼‰
					createcount = new int[] { 1 };
				} else if ((pc.getLevel() >= 60) && (pc.getLevel() <= 64)) {
					createitem = new int[] { 49153 }; // ã‚·ãƒ£ãƒ«ãƒŠã�®å¤‰èº«ã‚¹ã‚¯ãƒ­ãƒ¼ãƒ«ï¼ˆãƒ¬ãƒ™ãƒ«60ï¼‰
					createcount = new int[] { 1 };
				} else if ((pc.getLevel() >= 65) && (pc.getLevel() <= 69)) {
					createitem = new int[] { 49154 }; // ã‚·ãƒ£ãƒ«ãƒŠã�®å¤‰èº«ã‚¹ã‚¯ãƒ­ãƒ¼ãƒ«ï¼ˆãƒ¬ãƒ™ãƒ«65ï¼‰
					createcount = new int[] { 1 };
				} else if (pc.getLevel() >= 70) {
					createitem = new int[] { 49155 }; // ã‚·ãƒ£ãƒ«ãƒŠã�®å¤‰èº«ã‚¹ã‚¯ãƒ­ãƒ¼ãƒ«ï¼ˆãƒ¬ãƒ™ãƒ«70ï¼‰
					createcount = new int[] { 1 };
				}
				success_htmlid = "sharna3";
				failure_htmlid = "sharna5";
			}
		} else if ((((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70035)
				|| (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70041)
				|| (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70042)) { // ã‚®ãƒ©ãƒ³ãƒ¬ãƒ¼ã‚¹ç®¡ç�†äººã€€ã‚»ã‚·ãƒ«ã€€ãƒ�ãƒ¼ãƒªãƒ¼ã€€ãƒ‘ãƒ¼ã‚­ãƒ³
			if (s.equalsIgnoreCase("status")) {// status
				htmldata = new String[15];
				for (int i = 0; i < 5; i++) {
					htmldata[i * 3] = (NpcTable.getInstance().getTemplate(
							l1j.server.server.model.game.L1BugBearRace.getInstance()
									.getRunner(i).getNpcId()).get_nameid());
					String condition;// 610 æ™®é€š
					if (l1j.server.server.model.game.L1BugBearRace.getInstance()
							.getCondition(i) == 0) {
						condition = "$610";
					} else {
						if (l1j.server.server.model.game.L1BugBearRace.getInstance()
								.getCondition(i) > 0) {// 368
														// è‰¯ã�„
							condition = "$368";
						} else {// 370 æ‚ªã�„
							condition = "$370";
						}
					}
					htmldata[i * 3 + 1] = condition;
					htmldata[i * 3 + 2] = String
							.valueOf(l1j.server.server.model.game.L1BugBearRace
									.getInstance().getWinningAverage(i));
				}
				htmlid = "maeno4";
			}
		}
		// [Legends] Beast Trainer
        else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70077 || ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 81290) {
            int consumeItem = 0;
            int consumeItemCount = 0;
            int petNpcId = 0;
            int petItemId = 0;
            int upLv = 0;
            int lvExp = 0;
            String msg = "";

            consumeItem = L1ItemId.ADENA;
            consumeItemCount = 50000;
            petItemId = 40314; //Pet Colar

            if(pc.getLevel() > 30)
            {
                upLv = 30;
            }
            else
            {
                upLv = pc.getLevel();
            }

            lvExp = ExpTable.getExpByLevel(upLv);
            msg = "Adena";

            if (s.equalsIgnoreCase("buy 1"))
            {
                petNpcId = L1MiscId.NPC_Beagle;
            }
            else if(s.equalsIgnoreCase("buy 2"))
            {
                petNpcId = L1MiscId.NPC_Bear;
            }
            else if(s.equalsIgnoreCase("buy 3"))
            {
                petNpcId = L1MiscId.NPC_Cat;
            }
            else if(s.equalsIgnoreCase("buy 4"))
            {
                petNpcId = L1MiscId.NPC_Collie;
            }
            else if(s.equalsIgnoreCase("buy 5"))
            {
                petNpcId = L1MiscId.NPC_Doberman;
            }
            else if(s.equalsIgnoreCase("buy 6"))
            {
                petNpcId = L1MiscId.NPC_Fox;
            }
            else if(s.equalsIgnoreCase("buy 7"))
            {
                petNpcId = L1MiscId.NPC_Husky;
            }
            else if(s.equalsIgnoreCase("buy 8"))
            {
                petNpcId = L1MiscId.NPC_Jindo_Puppy;
            }
            else if(s.equalsIgnoreCase("buy 9"))
            {
                petNpcId = L1MiscId.NPC_Killer_Rabbit;
            }
            else if(s.equalsIgnoreCase("buy 10"))
            {
                petNpcId = L1MiscId.NPC_Raccoon;
            }
            else if(s.equalsIgnoreCase("buy 11"))
            {
                petNpcId = L1MiscId.NPC_Saint_Bernard;
            }
            else if(s.equalsIgnoreCase("buy 12"))
            {
                petNpcId = L1MiscId.NPC_Shepherd;
            }
            else if(s.equalsIgnoreCase("buy 13"))
            {
                petNpcId = L1MiscId.NPC_Tiger;
            }
            else if(s.equalsIgnoreCase("buy 14"))
            {
                petNpcId = L1MiscId.NPC_Wolf;
            }
            else if(s.equalsIgnoreCase("buy 15"))
            {
                petNpcId = L1MiscId.NPC_Dire_Cub;
            }



            if (petNpcId > 0) {
                if (!pc.getInventory().checkItem(consumeItem, consumeItemCount)) {
                    pc.sendPackets(new S_ServerMessage(337, msg));
                } else if (pc.getInventory().getSize() > 180) {
                    pc.sendPackets(new S_ServerMessage(337, "You cannot hold more than 180 items."));
                } else if (pc.getInventory().checkItem(consumeItem,consumeItemCount)) {
                    pc.getInventory().consumeItem(consumeItem, consumeItemCount);
                    L1PcInventory inv = pc.getInventory();
                    L1ItemInstance petamu = inv.storeItem(petItemId, 1);
                    pc.sendPackets(new S_SystemMessage("You adopted a young new pet, take care of it"));
                    if (petamu != null) {
                        PetTable.getInstance().buyNewPet(petNpcId, petamu.getId() + 1,petamu.getId(), upLv, lvExp);
                        pc.sendPackets(new S_ItemName(petamu));
                        pc.sendPackets(new S_ServerMessage(403, petamu.getName()));
                    }
                }
            } else {
                pc.sendPackets(new S_SystemMessage("Client Patch Issue - Please Contact Support"));
            }
            htmlid = "";
        }

		// å¹»è¡“å£« è©¦ç·´ä»»å‹™
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80145) {// é•·è€� å¸Œè“®æ�©
			int lv15_step = pc.getQuest().get_step(L1Quest.QUEST_LEVEL15);
			int lv30_step = pc.getQuest().get_step(L1Quest.QUEST_LEVEL30);
			int lv45_step = pc.getQuest().get_step(L1Quest.QUEST_LEVEL45);
			int lv50_step = pc.getQuest().get_step(L1Quest.QUEST_LEVEL50);
			if (pc.isDragonKnight()) {
				if (s.equalsIgnoreCase("l") && (lv45_step == 1)) {
					if (pc.getInventory().checkItem(49209, 1)) { // check
						pc.getInventory().consumeItem(49209, 1); // del
						pc.getQuest().set_step(L1Quest.QUEST_LEVEL45, 2);
						htmlid = "silrein38";
					}
				} else if (s.equalsIgnoreCase("m") && (lv45_step == 2)) {
					pc.getQuest().set_step(L1Quest.QUEST_LEVEL45, 3);
					htmlid = "silrein39";
				}
			}
			if (pc.isIllusionist()) {
				// å¸Œè“®æ�©çš„ç¬¬ä¸€æ¬¡èª²é¡Œî¿œî»®
				if (s.equalsIgnoreCase("a") && (lv15_step == 0)) {
					final int[] item_ids = { 49172, 49182, }; // å¸Œè“®æ�©çš„ç¬¬ä¸€æ¬¡ä¿¡ä»¶ã€�å¦–ç²¾æ£®æž—çž¬é–“ç§»å‹•å�·è»¸
					final int[] item_amounts = { 1, 1, };
					for (int i = 0; i < item_ids.length; i++) {
						L1ItemInstance item = pc.getInventory().storeItem(
								item_ids[i], item_amounts[i]);
						pc.sendPackets(new S_ServerMessage(143,
								((L1NpcInstance) obj).getNpcTemplate()
										.get_name(), item.getItem().getName()));
					}
					pc.getQuest().set_step(L1Quest.QUEST_LEVEL15, 1);
					htmlid = "silrein3";
				// åŸ·è¡Œå¸Œè“®æ�©çš„ç¬¬äºŒèª²é¡Œ
				} else if (s.equalsIgnoreCase("c") && (lv30_step == 0)) {
					final int[] item_ids = { 49173, 49179, }; // å¸Œè“®æ�©çš„ç¬¬äºŒæ¬¡ä¿¡ä»¶ã€�å¸Œè“®æ�©ä¹‹è¢‹
																// ç�²å¾—ã€�æ­�ç‘žæ�‘èŽŠçž¬é–“ç§»å‹•å�·è»¸ã€�ç”Ÿé�½çš„ç¬›å­�ã€‘
					final int[] item_amounts = { 1, 1, };
					for (int i = 0; i < item_ids.length; i++) {
						L1ItemInstance item = pc.getInventory().storeItem(
								item_ids[i], item_amounts[i]);
						pc.sendPackets(new S_ServerMessage(143,
								((L1NpcInstance) obj).getNpcTemplate()
										.get_name(), item.getItem().getName()));
					}
					pc.getQuest().set_step(L1Quest.QUEST_LEVEL30, 1);
					htmlid = "silrein12";
				// é‡�æ–°æŽ¥æ”¶ç”Ÿé�½çš„ç¬›å­�
				} else if (s.equalsIgnoreCase("o") && (lv30_step == 1)) {
					if (pc.getInventory().checkItem(49186, 1)
							|| pc.getInventory().checkItem(49179, 1)) {
						htmlid = "silrein17";// å·²ç¶“æœ‰ å¸Œè“®æ�©ä¹‹è¢‹ã€�ç”Ÿé�½çš„ç¬›å­� ä¸�å�¯å†�å�–å¾—
					} else {
						L1ItemInstance item = pc.getInventory().storeItem(
								49186, 1); // ç”Ÿé�½çš„ç¬›å­�
						pc.sendPackets(new S_ServerMessage(143, item.getItem()
								.getName()));
						htmlid = "silrein16";
					}
				// åŸ·è¡Œå¸Œè“®æ�©çš„ç¬¬ä¸‰èª²é¡Œ
				} else if (s.equalsIgnoreCase("e") && (lv45_step == 0)) {
					final int[] item_ids = { 49174, 49180, }; // å¸Œè“®æ�©çš„ç¬¬ä¸‰æ¬¡ä¿¡ä»¶ã€�å¸Œè“®æ�©ä¹‹è¢‹
																// ç�²å¾—ã€�é¢¨æœ¨æ�‘èŽŠçž¬é–“ç§»å‹•å�·è»¸ã€�æ™‚ç©ºè£‚ç—•æ°´æ™¶(ç¶ è‰²
																// 3å€‹)ã€‘
					final int[] item_amounts = { 1, 1, };
					for (int i = 0; i < item_ids.length; i++) {
						L1ItemInstance item = pc.getInventory().storeItem(
								item_ids[i], item_amounts[i]);
						pc.sendPackets(new S_ServerMessage(143,
								((L1NpcInstance) obj).getNpcTemplate()
										.get_name(), item.getItem().getName()));
					}
					pc.getQuest().set_step(L1Quest.QUEST_LEVEL45, 1);
					htmlid = "silrein19";
				// åŸ·è¡Œå¸Œè“®æ�©çš„ç¬¬å››èª²é¡Œ
				} else if (s.equalsIgnoreCase("h") && (lv50_step == 0)) {
					final int[] item_ids = { 49176, }; // å¸Œè“®æ�©çš„ç¬¬äº”æ¬¡ä¿¡ä»¶
					final int[] item_amounts = { 1, };
					for (int i = 0; i < item_ids.length; i++) {
						L1ItemInstance item = pc.getInventory().storeItem(
								item_ids[i], item_amounts[i]);
						pc.sendPackets(new S_ServerMessage(143,
								((L1NpcInstance) obj).getNpcTemplate()
										.get_name(), item.getItem().getName()));
					}
					pc.getQuest().set_step(L1Quest.QUEST_LEVEL50, 1);
					htmlid = "silrein28";
				// é‡�æ–°æŽ¥æ”¶æ™‚ç©ºè£‚ç—•é‚ªå¿µç¢Žç‰‡ã€�å¸Œè“®æ�©çš„è­·èº«ç¬¦
				} else if (s.equalsIgnoreCase("k") && (lv50_step >= 2)) {
					if (pc.getInventory().checkItem(49202, 1)
							|| pc.getInventory().checkItem(49178, 1)) {
						htmlid = "silrein32";
					} else {
						final int[] item_ids = { 49202, 49178, };
						final int[] item_amounts = { 1, 1, };
						for (int i = 0; i < item_ids.length; i++) {
							L1ItemInstance item = pc.getInventory().storeItem(
									item_ids[i], item_amounts[i]);
							pc.sendPackets(new S_ServerMessage(143,
									((L1NpcInstance) obj).getNpcTemplate()
									.get_name(), item.getItem().getName()));
						}
						htmlid = "silrein32";
					}
				}
			}
		} else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70739) { // è¿ªå˜‰å‹’å»·
			if (pc.isCrown()) {
				if (s.equalsIgnoreCase("e")) {
					if (pc.getInventory().checkItem(49159, 1)) {
						htmlid = "dicardingp5";
						pc.getInventory().consumeItem(49159, 1);
						pc.getQuest().set_step(L1Quest.QUEST_LEVEL50, 2);
					} else {
						htmlid = "dicardingp4a";
					}
				} else if (s.equalsIgnoreCase("d")) {
					htmlid = "dicardingp7";
					L1PolyMorph.doPoly(pc, 6035, 900, 1, true);
					pc.getQuest().set_step(L1Quest.QUEST_LEVEL50, 3);
				} else if (s.equalsIgnoreCase("c")) {
					htmlid = "dicardingp9";
					L1PolyMorph.undoPoly(pc);
					L1PolyMorph.doPoly(pc, 6035, 900, 1, true);
				} else if (s.equalsIgnoreCase("b")) {
					htmlid = "dicardingp12";
					pc.getQuest().set_step(L1Quest.QUEST_LEVEL50, 4);
					if (pc.getInventory().checkItem(49165)) {
						pc.getInventory().consumeItem(49165, pc.getInventory().countItems(49165));
					} 
					if (pc.getInventory().checkItem(49166)) {
						pc.getInventory().consumeItem(49166, pc.getInventory().countItems(49166));
					} 
					if (pc.getInventory().checkItem(49167)) {
						pc.getInventory().consumeItem(49167, pc.getInventory().countItems(49167));
					} 
					if (pc.getInventory().checkItem(49168)) {
						pc.getInventory().consumeItem(49168, pc.getInventory().countItems(49168));
					} 
					if (pc.getInventory().checkItem(49239)) {
						pc.getInventory().consumeItem(49239, pc.getInventory().countItems(49239));
					}
				}
			}
			if (pc.isKnight()) {
				if (s.equalsIgnoreCase("h")) {
					if (pc.getInventory().checkItem(49160, 1)) {
						htmlid = "dicardingk5";
						pc.getInventory().consumeItem(49160, 1);
						pc.getQuest().set_step(L1Quest.QUEST_LEVEL50, 2);
					}
				} else if (s.equalsIgnoreCase("j")) {
					htmlid = "dicardingk10";
					pc.getInventory().consumeItem(49161, 10);
					pc.getQuest().set_step(L1Quest.QUEST_LEVEL50, 4);
				} else if (s.equalsIgnoreCase("k")) {
					htmlid = "dicardingk13";
					pc.getQuest().set_step(L1Quest.QUEST_LEVEL50, 4);
					if (pc.getInventory().checkItem(49165)) {
						pc.getInventory().consumeItem(49165, pc.getInventory().countItems(49165));
					} 
					if (pc.getInventory().checkItem(49166)) {
						pc.getInventory().consumeItem(49166, pc.getInventory().countItems(49166));
					} 
					if (pc.getInventory().checkItem(49167)) {
						pc.getInventory().consumeItem(49167, pc.getInventory().countItems(49167));
					} 
					if (pc.getInventory().checkItem(49168)) {
						pc.getInventory().consumeItem(49168, pc.getInventory().countItems(49168));
					} 
					if (pc.getInventory().checkItem(49239)) {
						pc.getInventory().consumeItem(49239, pc.getInventory().countItems(49239));
					}
				}
			}
			if (pc.isElf()) {
				if (s.equalsIgnoreCase("n")) {
					if (pc.getInventory().checkItem(49162, 1)) {
						htmlid = "dicardinge5";
						pc.getInventory().consumeItem(49162, 1);
						pc.getQuest().set_step(L1Quest.QUEST_LEVEL50, 2);
					}
				} else if (s.equalsIgnoreCase("p")) {
					htmlid = "dicardinge10";
					pc.getInventory().consumeItem(49163, 1);
					pc.getQuest().set_step(L1Quest.QUEST_LEVEL50, 5);
				} else if (s.equalsIgnoreCase("q")) {
					htmlid = "dicardinge14";
					pc.getQuest().set_step(L1Quest.QUEST_LEVEL50, 5);
					if (pc.getInventory().checkItem(49165)) {
						pc.getInventory().consumeItem(49165, pc.getInventory().countItems(49165));
					} 
					if (pc.getInventory().checkItem(49166)) {
						pc.getInventory().consumeItem(49166, pc.getInventory().countItems(49166));
					} 
					if (pc.getInventory().checkItem(49167)) {
						pc.getInventory().consumeItem(49167, pc.getInventory().countItems(49167));
					} 
					if (pc.getInventory().checkItem(49168)) {
						pc.getInventory().consumeItem(49168, pc.getInventory().countItems(49168));
					} 
					if (pc.getInventory().checkItem(49239)) {
						pc.getInventory().consumeItem(49239, pc.getInventory().countItems(49239));
					}
				}
			}
			if (pc.isWizard()) {
				if (s.equalsIgnoreCase("u")) {
					if (pc.getInventory().checkItem(49164, 1)) {
						htmlid = "dicardingw6";
						pc.getInventory().consumeItem(49164, 1);
						pc.getQuest().set_step(L1Quest.QUEST_LEVEL50, 3);
					}
				} else if (s.equalsIgnoreCase("w")) {
					htmlid = "dicardingw12";
					pc.getQuest().set_step(L1Quest.QUEST_LEVEL50, 4);
					if (pc.getInventory().checkItem(49165)) {
						pc.getInventory().consumeItem(49165, pc.getInventory().countItems(49165));
					} 
					if (pc.getInventory().checkItem(49166)) {
						pc.getInventory().consumeItem(49166, pc.getInventory().countItems(49166));
					} 
					if (pc.getInventory().checkItem(49167)) {
						pc.getInventory().consumeItem(49167, pc.getInventory().countItems(49167));
					} 
					if (pc.getInventory().checkItem(49168)) {
						pc.getInventory().consumeItem(49168, pc.getInventory().countItems(49168));
					} 
					if (pc.getInventory().checkItem(49239)) {
						pc.getInventory().consumeItem(49239, pc.getInventory().countItems(49239));
					}
				}
			}
		} else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 81334) { // è¢«é�ºæ£„çš„è‚‰èº«
			if (s.equalsIgnoreCase("a")) {
				if (pc.getInventory().checkItem(49239, 1)) {
					htmlid = "rtf06";
				} else {
					final int[] item_ids = { 49239, };
					final int[] item_amounts = { 1, };
					for (int i = 0; i < item_ids.length; i++) {
						L1ItemInstance item = pc.getInventory().storeItem(
								item_ids[i], item_amounts[i]);
						pc.sendPackets(new S_ServerMessage(143,
								((L1NpcInstance) obj).getNpcTemplate()
										.get_name(), item.getItem().getName()));
					}
				}
			}
		} else if ((((L1NpcInstance) obj).getNpcTemplate().get_npcId() >= 81353)
				&& (((L1NpcInstance) obj).getNpcTemplate().get_npcId() <= 81363)) { // é­”æ³•å•†äºº- ä»¿æ­£è¨­å®š   
			int[] skills = new int[10];
			char s1 = s.charAt(0);
			switch(s1){
			case 'b':
				skills = new int[] {43, 79, 151, 158, 160, 206, 211, 216, 115, 149};                     
				break;
			case 'a':
				skills = new int[] {43, 79, 151, 158, 160, 206, 211, 216, 115, 148};
				break;
			}
			if (s.equalsIgnoreCase("a") || s.equalsIgnoreCase("b")){
				if(pc.getInventory().consumeItem(L1ItemId.ADENA,3000)){
					L1SkillUse l1skilluse = new L1SkillUse();
					for (int i = 0; i < skills.length; i++) {
						l1skilluse.handleCommands(pc, 
								skills[i], pc.getId(), pc.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
					}
					htmlid = "bs_done";           
				} else {
					htmlid = "bs_adena";
				}
			}
			if (s.equalsIgnoreCase("0")) {
				htmlid = "bs_01";                 
			}
		} else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 50016) {// å‚‘è«¾
			if (s.equalsIgnoreCase("0")) {
				if (pc.getLevel() < 13) {// lv < 13 å‚³é€�éš±è—�ä¹‹è°·
					L1Teleport
							.teleport(pc, 32682, 32874, (short) 2005, 2, true);
				} else {
					htmlid = "zeno1";
				}
			}
		} else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 50065) {// é­¯æ¯”æ�©
			if (s.equalsIgnoreCase("teleport valley-in")) {
				if (pc.getLevel() < 13) {// lv < 13 å‚³é€�éš±è—�ä¹‹è°·
					L1Teleport
							.teleport(pc, 32682, 32874, (short) 2005, 2, true);
				} else {
					htmlid = "";
				}
			}
		} else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 50055) {// å¾·ç‘žæ–¯ç‰¹
			if (s.equalsIgnoreCase("teleport hidden-valley")) {
				if (pc.getLevel() < 13) {// lv < 13 å‚³é€�éš±è—�ä¹‹è°·
					L1Teleport
							.teleport(pc, 32682, 32874, (short) 2005, 2, true);
				} else {
					htmlid = "drist1";
				}
			}
		} else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 81255) {// æ–°æ‰‹å°Žå¸«
			@SuppressWarnings("unused")
			int quest_step = pc.getQuest().get_step(L1Quest.QUEST_TUTOR);// ä»»å‹™ç·¨è™ŸéšŽæ®µ
			int level = pc.getLevel();// è§’è‰²ç­‰ç´š
			char s1 = s.charAt(0);
			if (level < 13) {
				switch (s1) {
				case 'A':
				case 'a':// isCrown
					if ((level > 1) && (level < 5)) {// lv2 ~ lv4
						htmlid = "tutorp1";// æŒ‡å¼•
					} else if ((level > 4) && (level < 8)) {// lv5 ~ lv7
						htmlid = "tutorp2";// å‚³é€�æœ�å‹™
					} else if ((level > 7) && (level < 10)) {// lv8 ~ lv9
						htmlid = "tutorp3";// å‚³é€�æœ�å‹™
					} else if ((level > 9) && (level < 12)) {// lv10 ~ lv11
						htmlid = "tutorp4";// å‚³é€�æœ�å‹™
					} else if ((level > 11) && (level < 13)) {// lv12
						htmlid = "tutorp5";// å‚³é€�æœ�å‹™
					} else if (level > 12) {// lv13
						htmlid = "tutorp6";// é›¢é–‹éš±è—�ä¹‹è°·
					} else {
						htmlid = "tutorend";
					}
					break;
				case 'B':
				case 'b':// isKnight
					if ((level > 1) && (level < 5)) {// lv2 ~ lv4
						htmlid = "tutork1";// æŽ¥å�—å¹«åŠ©
					} else if ((level > 4) && (level < 8)) {// lv5 ~ lv7
						htmlid = "tutork2";// å‚³é€�æœ�å‹™
					} else if ((level > 7) && (level < 10)) {// lv8 ~ lv9
						htmlid = "tutork3";// å‚³é€�æœ�å‹™
					} else if ((level > 9) && (level < 13)) {// lv10 ~ lv12
						htmlid = "tutork4";// å‚³é€�æœ�å‹™
					} else if (level > 12) {// lv13
						htmlid = "tutork5";// é›¢é–‹éš±è—�ä¹‹è°·
					} else {
						htmlid = "tutorend";
					}
					break;
				case 'C':
				case 'c':// isElf
					if ((level > 1) && (level < 5)) {// lv2 ~ lv4
						htmlid = "tutore1";// æŽ¥å�—å¹«åŠ©
					} else if ((level > 4) && (level < 8)) {// lv5 ~ lv7
						htmlid = "tutore2";// å‚³é€�æœ�å‹™
					} else if ((level > 7) && (level < 10)) {// lv8 ~ lv9
						htmlid = "tutore3";// å‚³é€�æœ�å‹™
					} else if ((level > 9) && (level < 12)) {// lv10 ~ lv11
						htmlid = "tutore4";// å‚³é€�æœ�å‹™
					} else if ((level > 11) && (level < 13)) {// lv12
						htmlid = "tutore5";// å‚³é€�æœ�å‹™
					} else if (level > 12) {// lv13
						htmlid = "tutore6";// é›¢é–‹éš±è—�ä¹‹è°·
					} else {
						htmlid = "tutorend";
					}
					break;
				case 'D':
				case 'd':// isWizard
					if ((level > 1) && (level < 5)) {// lv2 ~ lv4
						htmlid = "tutorm1";// æŽ¥å�—å¹«åŠ©
					} else if ((level > 4) && (level < 8)) {// lv5 ~ lv7
						htmlid = "tutorm2";// å‚³é€�æœ�å‹™
					} else if ((level > 7) && (level < 10)) {// lv8 ~ lv9
						htmlid = "tutorm3";// å‚³é€�æœ�å‹™
					} else if ((level > 9) && (level < 12)) {// lv10 ~ lv11
						htmlid = "tutorm4";// å‚³é€�æœ�å‹™
					} else if ((level > 11) && (level < 13)) {// lv12
						htmlid = "tutorm5";// å‚³é€�æœ�å‹™
					} else if (level > 12) {// lv13
						htmlid = "tutorm6";// é›¢é–‹éš±è—�ä¹‹è°·
					} else {
						htmlid = "tutorend";
					}
					break;
				case 'E':
				case 'e':// isDarkelf
					if ((level > 1) && (level < 5)) {// lv2 ~ lv4
						htmlid = "tutord1";// æŽ¥å�—å¹«åŠ©
					} else if ((level > 4) && (level < 8)) {// lv5 ~ lv7
						htmlid = "tutord2";// å‚³é€�æœ�å‹™
					} else if ((level > 7) && (level < 10)) {// lv8 ~ lv9
						htmlid = "tutord3";// å‚³é€�æœ�å‹™
					} else if ((level > 9) && (level < 12)) {// lv10 ~ lv11
						htmlid = "tutord4";// å‚³é€�æœ�å‹™
					} else if ((level > 11) && (level < 13)) {// lv12
						htmlid = "tutord5";// å‚³é€�æœ�å‹™
					} else if (level > 12) {// lv13
						htmlid = "tutord6";// é›¢é–‹éš±è—�ä¹‹è°·
					} else {
						htmlid = "tutorend";
					}
					break;
				case 'F':
				case 'f':// isDragonKnight
					if ((level > 1) && (level < 5)) {// lv2 ~ lv4
						htmlid = "tutordk1";// æŽ¥å�—å¹«åŠ©
					} else if ((level > 4) && (level < 8)) {// lv5 ~ lv7
						htmlid = "tutordk2";// å‚³é€�æœ�å‹™
					} else if ((level > 7) && (level < 10)) {// lv8 ~ lv9
						htmlid = "tutordk3";// å‚³é€�æœ�å‹™
					} else if ((level > 9) && (level < 13)) {// lv10 ~ lv12
						htmlid = "tutordk4";// å‚³é€�æœ�å‹™
					} else if (level > 12) {// lv13
						htmlid = "tutordk5";// é›¢é–‹éš±è—�ä¹‹è°·
					} else {
						htmlid = "tutorend";
					}
					break;
				case 'G':
				case 'g':// isIllusionist
					if ((level > 1) && (level < 5)) {// lv2 ~ lv4
						htmlid = "tutori1";// æŽ¥å�—å¹«åŠ©
					} else if ((level > 4) && (level < 8)) {// lv5 ~ lv7
						htmlid = "tutori2";// å‚³é€�æœ�å‹™
					} else if ((level > 7) && (level < 10)) {// lv8 ~ lv9
						htmlid = "tutori3";// å‚³é€�æœ�å‹™
					} else if ((level > 9) && (level < 13)) {// lv10 ~ lv12
						htmlid = "tutori4";// å‚³é€�æœ�å‹™
					} else if (level > 12) {// lv13
						htmlid = "tutori5";// é›¢é–‹éš±è—�ä¹‹è°·
					} else {
						htmlid = "tutorend";
					}
					break;
				case 'H':
				case 'h':
					L1Teleport.teleport(pc, 32575, 32945, (short) 0, 5, true); // èªªè©±ä¹‹å³¶å€‰åº«ç®¡ç�†å“¡
					htmlid = "";
					break;
				case 'I':
				case 'i':
					L1Teleport.teleport(pc, 32579, 32923, (short) 0, 5, true); // è¡€ç›ŸåŸ·è¡Œäºº
					htmlid = "";
					break;
				case 'J':
				case 'j':
					createitem = new int[] { 40101 };
					createcount = new int[] { 1 };
					L1Teleport
							.teleport(pc, 32676, 32813, (short) 2005, 5, true); // éš±è—�ä¹‹è°·åœ°ä¸‹æ´žç©´
					htmlid = "";
					break;
				case 'K':
				case 'k':
					L1Teleport.teleport(pc, 32562, 33082, (short) 0, 5, true); // é­”æ³•å¸«å�‰å€«å°�å±‹
					htmlid = "";
					break;
				case 'L':
				case 'l':
					L1Teleport.teleport(pc, 32792, 32820, (short) 75, 5, true); // è±¡ç‰™å¡”
					htmlid = "";
					break;
				case 'M':
				case 'm':
					L1Teleport.teleport(pc, 32877, 32904, (short) 304, 5, true); // é»‘æš—é­”æ³•å¸«è³½å¸�äºž
					htmlid = "";
					break;
				case 'N':
				case 'n':
					L1Teleport
							.teleport(pc, 32759, 32884, (short) 1000, 5, true); // å¹»è¡“å£«å�²è�²çˆ¾
					htmlid = "";
					break;
				case 'O':
				case 'o':
					L1Teleport
							.teleport(pc, 32605, 32837, (short) 2005, 5, true); // æ�‘èŽŠè¥¿éƒŠ
					htmlid = "";
					break;
				case 'P':
				case 'p':
					L1Teleport
							.teleport(pc, 32733, 32902, (short) 2005, 5, true); // æ�‘èŽŠæ�±éƒŠ
					htmlid = "";
					break;
				case 'Q':
				case 'q':
					L1Teleport
							.teleport(pc, 32559, 32843, (short) 2005, 5, true); // æ�‘èŽŠå�—éƒ¨ç‹©ç�µå ´
					htmlid = "";
					break;
				case 'R':
				case 'r':
					L1Teleport
							.teleport(pc, 32677, 32982, (short) 2005, 5, true); // æ�‘èŽŠæ�±å�—éƒ¨ç‹©ç�µå ´
					htmlid = "";
					break;
				case 'S':
				case 's':
					L1Teleport
							.teleport(pc, 32781, 32854, (short) 2005, 5, true); // æ�‘èŽŠæ�±åŒ—éƒ¨ç‹©ç�µå ´
					htmlid = "";
					break;
				case 'T':
				case 't':
					L1Teleport
							.teleport(pc, 32674, 32739, (short) 2005, 5, true); // æ�‘èŽŠè¥¿åŒ—éƒ¨ç‹©ç�µå ´
					htmlid = "";
					break;
				case 'U':
				case 'u':
					L1Teleport
							.teleport(pc, 32578, 32737, (short) 2005, 5, true); // æ�‘èŽŠè¥¿éƒ¨ç‹©ç�µå ´
					htmlid = "";
					break;
				case 'V':
				case 'v':
					L1Teleport
							.teleport(pc, 32542, 32996, (short) 2005, 5, true); // æ�‘èŽŠå�—éƒ¨ç‹©ç�µå ´
					htmlid = "";
					break;
				case 'W':
				case 'w':
					L1Teleport
							.teleport(pc, 32794, 32973, (short) 2005, 5, true); // æ�‘èŽŠæ�±éƒ¨ç‹©ç�µå ´
					htmlid = "";
					break;
				case 'X':
				case 'x':
					L1Teleport
							.teleport(pc, 32803, 32789, (short) 2005, 5, true); // æ�‘èŽŠåŒ—éƒ¨ç‹©ç�µå ´
					htmlid = "";
					break;
				default:
					break;
				}
			}
		} else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 81256) {// ä¿®ç·´å ´ç®¡ç�†å“¡
			int quest_step = pc.getQuest().get_step(L1Quest.QUEST_TUTOR2);// ä»»å‹™ç·¨è™ŸéšŽæ®µ
			int level = pc.getLevel();// è§’è‰²ç­‰ç´š
			@SuppressWarnings("unused")
			boolean isOK = false;
			if (s.equalsIgnoreCase("A")) {
				if ((level > 4) && (quest_step == 2)) {
					createitem = new int[] { 20028, 20126, 20173, 20206, 20232,
							40029, 40030, 40098, 40099, 40101 }; // ç�²å¾—è£�å‚™
					createcount = new int[] { 1, 1, 1, 1, 1, 50, 5, 20, 30, 5 };
					questid = L1Quest.QUEST_TUTOR2;
					questvalue = 3;
				}
			}
			htmlid = "";
		} else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 81257) {// æ—…äººè«®è©¢å“¡
			int level = pc.getLevel();// è§’è‰²ç­‰ç´š
			char s1 = s.charAt(0);
			if (level < 46) {
				switch (s1) {
				case 'A':
				case 'a':
					L1Teleport.teleport(pc, 32562, 33082, (short) 0, 5, true); // é­”æ³•å¸«å�‰å€«å°�å±‹
					htmlid = "";
					break;
				case 'B':
				case 'b':
					L1Teleport.teleport(pc, 33119, 32933, (short) 4, 5, true); // æ­£ç¾©ç¥žæ®¿
					htmlid = "";
					break;
				case 'C':
				case 'c':
					L1Teleport.teleport(pc, 32887, 32652, (short) 4, 5, true); // é‚ªæƒ¡ç¥žæ®¿
					htmlid = "";
					break;
				case 'D':
				case 'd':
					L1Teleport.teleport(pc, 32792, 32820, (short) 75, 5, true); // è²©å”®å¦–ç²¾ç²¾é�ˆé­”æ³•çš„ç�³é�”
					htmlid = "";
					break;
				case 'E':
				case 'e':
					L1Teleport.teleport(pc, 32789, 32851, (short) 76, 5, true); // è±¡ç‰™å¡”çš„ç²¾é�ˆé­”æ³•ä¿®ç…‰å®¤
					htmlid = "";
					break;
				case 'F':
				case 'f':
					L1Teleport.teleport(pc, 32750, 32847, (short) 76, 5, true); // è±¡ç‰™å¡”çš„è‰¾åˆ©æº«
					htmlid = "";
					break;
				case 'G':
				case 'g':
					if (pc.isDarkelf()) {
						L1Teleport.teleport(pc, 32877, 32904, (short) 304, 5,
								true); // é»‘æš—é­”æ³•å¸«è³½å¸�äºž
						htmlid = "";
					} else {
						htmlid = "lowlv40";
					}
					break;
				case 'H':
				case 'h':
					if (pc.isDragonKnight()) {
						L1Teleport.teleport(pc, 32811, 32873, (short) 1001, 5,
								true); // è²©å”®é¾�é¨Žå£«æŠ€èƒ½çš„æ£®å¸•çˆ¾è™•
						htmlid = "";
					} else {
						htmlid = "lowlv41";
					}
					break;
				case 'I':
				case 'i':
					if (pc.isIllusionist()) {
						L1Teleport.teleport(pc, 32759, 32884, (short) 1000, 5,
								true); // è²©å”®å¹»è¡“å£«é­”æ³•çš„å�²è�²çˆ¾è™•
						htmlid = "";
					} else {
						htmlid = "lowlv42";
					}
					break;
				case 'J':
				case 'j':
					L1Teleport.teleport(pc, 32509, 32867, (short) 0, 5, true); // èªªè©±ä¹‹å³¶çš„ç”˜ç‰¹è™•
					htmlid = "";
					break;
				case 'K':
				case 'k':
					if ((level > 34)) {
						createitem = new int[] { 20282, 21139 }; // è£œå……è±¡ç‰™å¡”é£¾å“�
						createcount = new int[] { 0, 0 };
						boolean isOK = false;
						for (int i = 0; i < createitem.length; i++) {
							if (!pc.getInventory().checkItem(createitem[i], 1)) { // check
								createcount[i] = 1;
								isOK = true;
							}
						}
						if (isOK) {
							success_htmlid = "lowlv43";
						} else {
							htmlid = "lowlv45";
						}
					} else {
						htmlid = "lowlv44";
					}
					break;
				case '0':
					if (level < 13) {
						htmlid = "lowlvS1";
					} else if ((level > 12) && (level < 46)) {
						htmlid = "lowlvS2";
					} else {
						htmlid = "lowlvno";
					}
					break;
				case '1':
					if (level < 13) {
						htmlid = "lowlv14";
					} else if ((level > 12) && (level < 46)) {
						htmlid = "lowlv15";
					} else {
						htmlid = "lowlvno";
					}
					break;
				case '2':
					createitem = new int[] { 20028, 20126, 20173, 20206, 20232,
							21138, 49310 }; // è£œå……è±¡ç‰™å¡”è£�å‚™
					createcount = new int[] { 0, 0, 0, 0, 0, 0, 0 };
					boolean isOK = false;
					for (int i = 0; i < createitem.length; i++) {
						if (createitem[i] == 49310) {
							L1ItemInstance item = pc.getInventory().findItemId(
									createitem[i]);
							if (item != null) {
								if (item.getCount() < 1000) {
									createcount[i] = 1000 - item.getCount();
									isOK = true;
								}
							} else {
								createcount[i] = 1000;
								isOK = true;
							}
						} else if (!pc.getInventory().checkItem(createitem[i],
								1)) { // check
							createcount[i] = 1;
							isOK = true;
						}
					}
					if (isOK) {
						success_htmlid = "lowlv16";
					} else {
						htmlid = "lowlv17";
					}
					break;
				case '6':
					if (!pc.getInventory().checkItem(49313, 1)
							&& !pc.getInventory().checkItem(49314, 1)) {
						createitem = new int[] { 49313 }; // è±¡ç‰™å¡”é­”æ³•è¢‹
						createcount = new int[] { 2 };
						materials = new int[] { L1ItemId.ADENA };
						counts = new int[] { 2000 };
						success_htmlid = "lowlv22";
						failure_htmlid = "lowlv20";
					} else if (pc.getInventory().checkItem(49313, 1)
							|| pc.getInventory().checkItem(49314, 1)) {
						htmlid = "lowlv23";
					} else {
						htmlid = "lowlvno";
					}
					break;
				default:
					break;
				}
			}
		} else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 81260) {// æ�‘èŽŠç¦�åˆ©å“¡
			int townid = pc.getHomeTownId();// è§’è‰²æ‰€å±¬æ�‘èŽŠ
			char s1 = s.charAt(0);
			if ((pc.getLevel() > 9) && (townid > 0) && (townid < 11)) {
				switch (s1) {
				case '0':
					createitem = new int[] { 49305 }; // è£½ä½œ ç¦�åˆ©å‹‡æ•¢è—¥æ°´
														// addContribution + 2
					createcount = new int[] { 1 };
					materials = new int[] { L1ItemId.ADENA, 40014 };
					counts = new int[] { 1000, 3 };
					contribution = 2;
					htmlid = "";
					break;
				case '1':
					createitem = new int[] { 49304 }; // è£½ä½œ ç¦�åˆ©æ£®æž—è—¥æ°´
														// addContribution + 4
					createcount = new int[] { 1 };
					materials = new int[] { L1ItemId.ADENA, 40068 };
					counts = new int[] { 1000, 3 };
					contribution = 4;
					htmlid = "";
					break;
				case '2':
					createitem = new int[] { 49307 }; // è£½ä½œ ç¦�åˆ©æ…Žé‡�è—¥æ°´
														// addContribution + 2
					createcount = new int[] { 1 };
					materials = new int[] { L1ItemId.ADENA, 40016 };
					counts = new int[] { 500, 3 };
					contribution = 2;
					htmlid = "";
					break;
				case '3':
					createitem = new int[] { 49306 }; // è£½ä½œ ç¦�åˆ©è—�è‰²è—¥æ°´
														// addContribution + 2
					createcount = new int[] { 1 };
					materials = new int[] { L1ItemId.ADENA, 40015 };
					counts = new int[] { 1000, 3 };
					contribution = 2;
					htmlid = "";
					break;
				case '4':
					createitem = new int[] { 49302 }; // è£½ä½œ ç¦�åˆ©åŠ é€Ÿè—¥æ°´
														// addContribution + 1
					createcount = new int[] { 1 };
					materials = new int[] { L1ItemId.ADENA, 40013 };
					counts = new int[] { 500, 3 };
					contribution = 1;
					htmlid = "";
					break;
				case '5':
					createitem = new int[] { 49303 }; // è£½ä½œ ç¦�åˆ©å‘¼å�¸è—¥æ°´
														// addContribution + 1
					createcount = new int[] { 1 };
					materials = new int[] { L1ItemId.ADENA, 40032 };
					counts = new int[] { 500, 3 };
					contribution = 1;
					htmlid = "";
					break;
				case '6':
					createitem = new int[] { 49308 }; // è£½ä½œ ç¦�åˆ©è®Šå½¢è—¥æ°´
														// addContribution + 3
					createcount = new int[] { 1 };
					materials = new int[] { L1ItemId.ADENA, 40088 };
					counts = new int[] { 1000, 3 };
					contribution = 3;
					htmlid = "";
					break;
				case 'A':
				case 'a':
					switch (townid) {
					case 1:
						createitem = new int[] { 49292 }; // è³¼è²· ç¦�åˆ©å‚³é€�å�·è»¸ï¼šèªªè©±ä¹‹å³¶
						createcount = new int[] { 1 };
						materials = new int[] { L1ItemId.ADENA };
						counts = new int[] { 400 };
						htmlid = "";
						break;
					case 2:
						createitem = new int[] { 49297 }; // è³¼è²· ç¦�åˆ©å‚³é€�å�·è»¸ï¼šéŠ€é¨Žå£«
						createcount = new int[] { 1 };
						materials = new int[] { L1ItemId.ADENA };
						counts = new int[] { 400 };
						htmlid = "";
						break;
					case 3:
						createitem = new int[] { 49293 }; // è³¼è²· ç¦�åˆ©å‚³é€�å�·è»¸ï¼šå�¤é­¯ä¸�
						createcount = new int[] { 1 };
						materials = new int[] { L1ItemId.ADENA };
						counts = new int[] { 400 };
						htmlid = "";
						break;
					case 4:
						createitem = new int[] { 49296 }; // è³¼è²· ç¦�åˆ©å‚³é€�å�·è»¸ï¼šç‡ƒæŸ³
						createcount = new int[] { 1 };
						materials = new int[] { L1ItemId.ADENA };
						counts = new int[] { 400 };
						htmlid = "";
						break;
					case 5:
						createitem = new int[] { 49295 }; // è³¼è²· ç¦�åˆ©å‚³é€�å�·è»¸ï¼šé¢¨æœ¨
						createcount = new int[] { 1 };
						materials = new int[] { L1ItemId.ADENA };
						counts = new int[] { 400 };
						htmlid = "";
						break;
					case 6:
						createitem = new int[] { 49294 }; // è³¼è²· ç¦�åˆ©å‚³é€�å�·è»¸ï¼šè‚¯ç‰¹
						createcount = new int[] { 1 };
						materials = new int[] { L1ItemId.ADENA };
						counts = new int[] { 400 };
						htmlid = "";
						break;
					case 7:
						createitem = new int[] { 49298 }; // è³¼è²· ç¦�åˆ©å‚³é€�å�·è»¸ï¼šå¥‡å²©
						createcount = new int[] { 1 };
						materials = new int[] { L1ItemId.ADENA };
						counts = new int[] { 400 };
						htmlid = "";
						break;
					case 8:
						createitem = new int[] { 49299 }; // è³¼è²· ç¦�åˆ©å‚³é€�å�·è»¸ï¼šæµ·éŸ³
						createcount = new int[] { 1 };
						materials = new int[] { L1ItemId.ADENA };
						counts = new int[] { 400 };
						htmlid = "";
						break;
					case 9:
						createitem = new int[] { 49301 }; // è³¼è²· ç¦�åˆ©å‚³é€�å�·è»¸ï¼šå¨�é “
						createcount = new int[] { 1 };
						materials = new int[] { L1ItemId.ADENA };
						counts = new int[] { 400 };
						htmlid = "";
						break;
					case 10:
						createitem = new int[] { 49300 }; // è³¼è²· ç¦�åˆ©å‚³é€�å�·è»¸ï¼šæ­�ç‘ž
						createcount = new int[] { 1 };
						materials = new int[] { L1ItemId.ADENA };
						counts = new int[] { 400 };
						htmlid = "";
						break;
					default:
						break;
					}
					break;
				default:
					break;
				}
			}
		}
		// å¤šé­¯å˜‰è²�çˆ¾
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 81278) { // å¤šé­¯å˜‰ä¹‹è¢‹
			if (s.equalsIgnoreCase("0")) {
				if (pc.getInventory().checkItem(46000, 1)) { // æª¢æŸ¥èº«ä¸Šæ˜¯å�¦æœ‰å¤šé­¯å˜‰ä¹‹è¢‹
					htmlid = "veil3"; // å·²ç¶“æœ‰è¢‹å­�äº†
				} else if (pc.getInventory().checkItem(L1ItemId.ADENA, 1000000)) { // æª¢æŸ¥èº«ä¸Šé‡‘å¹£æ˜¯å�¦è¶³å¤ 
					pc.getInventory().consumeItem(L1ItemId.ADENA, 1000000);
					pc.getInventory().storeItem(46000, 1);
					htmlid = "veil7"; // è³¼è²·æˆ�åŠŸé¡¯ç¤º
				} else if (!pc.getInventory().checkItem(L1ItemId.ADENA, 1000000)) { // æª¢æŸ¥èº«ä¸Šé‡‘å¹£æ˜¯å�¦è¶³å¤ 
					htmlid = "veil4"; // éŒ¢ä¸�å¤ é¡¯ç¤º æˆ‘å€‘é‚„æ˜¯ä¸�è¦�ç´„å®šäº†
				}
			} else if (s.equalsIgnoreCase("1")) {
				htmlid = "veil9"; // è�½å�–å»ºè­°
			}
		} else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 95020) {
            int pccount = 0;
            for (L1PcInstance map784pc : L1World.getInstance().getAllPlayers()) {
                    if (map784pc.getMapId() == 784) {
                            pccount++;
                    }
            }
            if (pccount >= 20) {
                    htmlid = "tikalgate4";
            }
            if (s.equalsIgnoreCase("e") && pccount <= 19) {
                    if (CrackOfTimeController.getStart().map784gateopen() == false) {
                            htmlid = "tikalgate2";
                    } else {
                            if (pc.getInventory().checkItem(49324, 1)) {
                                    pc.getInventory().consumeItem(49324, 1);
                                    L1Teleport.teleport(pc, 32730, 32866, (short) 784, 2, true);
                                    htmlid = "";
                            } else {
                                    htmlid = "tikalgate3";
                            }
                    }
            }
		}  else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 81277) { // éš±åŒ¿çš„å·¨é¾�è°·å…¥å�£
			int level = pc.getLevel();// è§’è‰²ç­‰ç´š
			char s1 = s.charAt(0);
			if (s.equalsIgnoreCase("0")) {
				if (level >= 30 && level <= 51) {
					L1Teleport
							.teleport(pc, 32820, 32904, (short) 1002, 5, true); // å‰�å¾€ä¾�å„’éƒ¨è�½
					htmlid = "";
				} else {
					htmlid = "dsecret3";
				}
			} else if (level >= 52) {
				switch (s1) {
				case '1':
					L1Teleport
							.teleport(pc, 32904, 32627, (short) 1002, 5, true); // å‰�å¾€é€ åŒ–ä¹‹åœ°(åœ°)
					break;
				case '2':
					L1Teleport
							.teleport(pc, 32793, 32593, (short) 1002, 5, true); // å‰�å¾€é€ åŒ–ä¹‹åœ°(ç�«)
					break;
				case '3':
					L1Teleport
							.teleport(pc, 32874, 32785, (short) 1002, 5, true); // å‰�å¾€é€ åŒ–ä¹‹åœ°(æ°´)
					break;
				case '4':
					L1Teleport
							.teleport(pc, 32993, 32716, (short) 1002, 4, true); // å‰�å¾€é€ åŒ–ä¹‹åœ°(é¢¨)
					break;
				case '5':
					L1Teleport
							.teleport(pc, 32698, 32664, (short) 1002, 6, true); // å‰�å¾€é¾�ä¹‹å¢“(åŒ—é‚Š)
					break;
				case '6':
					L1Teleport
							.teleport(pc, 32710, 32759, (short) 1002, 6, true); // å‰�å¾€é¾�ä¹‹å¢“(å�—é‚Š)
					break;
				case '7':
					L1Teleport
							.teleport(pc, 32986, 32630, (short) 1002, 4, true); // å‰�å¾€è’¼ç©ºä¹‹è°·
					break;
				}
				htmlid = "";
			} else {
				htmlid = "dsecret3";
			}
		}

		// else System.out.println("C_NpcAction: " + s);
		if ((htmlid != null) && htmlid.equalsIgnoreCase("colos2")) {
			htmldata = makeUbInfoStrings(((L1NpcInstance) obj).getNpcTemplate()
					.get_npcId());
		}
		if (createitem != null) { // ã‚¢ã‚¤ãƒ†ãƒ ç²¾è£½
			boolean isCreate = true;
			if (materials != null) {
				for (int j = 0; j < materials.length; j++) {
					if (!pc.getInventory().checkItemNotEquipped(materials[j],
							counts[j])) {
						L1Item temp = ItemTable.getInstance().getTemplate(
								materials[j]);
						pc.sendPackets(new S_ServerMessage(337, temp.getName())); // \f1%0ã�Œä¸�è¶³ã�—ã�¦ã�„ã�¾ã�™ã€‚
						isCreate = false;
					}
				}
			}

			if (isCreate) {
				// å®¹é‡�ã�¨é‡�é‡�ã�®è¨ˆç®—
				int create_count = 0; // ã‚¢ã‚¤ãƒ†ãƒ ã�®å€‹æ•°ï¼ˆçº�ã�¾ã‚‹ç‰©ã�¯1å€‹ï¼‰
				int create_weight = 0;
				for (int k = 0; k < createitem.length; k++) {
					if ((createitem[k] > 0) && (createcount[k] > 0)) {
						L1Item temp = ItemTable.getInstance().getTemplate(
								createitem[k]);
						if (temp != null) {
							if (temp.isStackable()) {
								if (!pc.getInventory().checkItem(createitem[k])) {
									create_count += 1;
								}
							} else {
								create_count += createcount[k];
							}
							create_weight += temp.getWeight() * createcount[k]
									/ 1000;
						}
					}
				}
				// å®¹é‡�ç¢ºèª�
				if (pc.getInventory().getSize() + create_count > 180) {
					pc.sendPackets(new S_ServerMessage(263)); // \f1ä¸€äººã�®ã‚­ãƒ£ãƒ©ã‚¯ã‚¿ãƒ¼ã�ŒæŒ�ã�£ã�¦æ­©ã�‘ã‚‹ã‚¢ã‚¤ãƒ†ãƒ ã�¯æœ€å¤§180å€‹ã�¾ã�§ã�§ã�™ã€‚
					return;
				}
				// é‡�é‡�ç¢ºèª�
				if (pc.getMaxWeight() < pc.getInventory().getWeight()
						+ create_weight) {
					pc.sendPackets(new S_ServerMessage(82)); // ã‚¢ã‚¤ãƒ†ãƒ ã�Œé‡�ã�™ã�Žã�¦ã€�ã�“ã‚Œä»¥ä¸ŠæŒ�ã�¦ã�¾ã�›ã‚“ã€‚
					return;
				}

				if (materials != null) {
					for (int j = 0; j < materials.length; j++) {
						// æ��æ–™æ¶ˆè²»
						pc.getInventory().consumeItem(materials[j], counts[j]);
					}
				}
				for (int k = 0; k < createitem.length; k++) {
					if ((createitem[k] > 0) && (createcount[k] > 0)) {
						L1ItemInstance item = pc.getInventory().storeItem(
								createitem[k], createcount[k]);
						if (item != null) {
							String itemName = ItemTable.getInstance()
									.getTemplate(createitem[k]).getName();
							String createrName = "";
							if (obj instanceof L1NpcInstance) {
								createrName = ((L1NpcInstance) obj)
										.getNpcTemplate().get_name();
							}
							if (createcount[k] > 1) {
								pc.sendPackets(new S_ServerMessage(143,
										createrName, itemName + " ("
												+ createcount[k] + ")")); // \f1%0ã�Œ%1ã‚’ã��ã‚Œã�¾ã�—ã�Ÿã€‚
							} else {
								pc.sendPackets(new S_ServerMessage(143,
										createrName, itemName)); // \f1%0ã�Œ%1ã‚’ã��ã‚Œã�¾ã�—ã�Ÿã€‚
							}
						}
					}
				}
				if (success_htmlid != null) { // htmlæŒ‡å®šã�Œã�‚ã‚‹å ´å�ˆã�¯è¡¨ç¤º
					pc.sendPackets(new S_NPCTalkReturn(objid, success_htmlid,
							htmldata));
				}
				if (questid > 0) {
					pc.getQuest().set_step(questid, questvalue);
				}
				if (contribution > 0) {
					pc.addContribution(contribution);
				}
			}

            // [Legends] Add Magic Doll Token Trade
            else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 97076) {
               //Uses SingleItemMaking.xml Entry
            }
            // [Legends] Add Joe Golem Token Trade
            else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71252) {
                //Uses SingleItemMaking.xml Entry
            }
            else { // ç²¾è£½å¤±æ•—
				if (failure_htmlid != null) { // htmlæŒ‡å®šã�Œã�‚ã‚‹å ´å�ˆã�¯è¡¨ç¤º
					pc.sendPackets(new S_NPCTalkReturn(objid, failure_htmlid,
							htmldata));
				}
			}
		}
        if (htmlid != null) { // htmlæŒ‡å®šã�Œã�‚ã‚‹å ´å�ˆã�¯è¡¨ç¤º
			pc.sendPackets(new S_NPCTalkReturn(objid, htmlid, htmldata));
		}
	}

	private String karmaLevelToHtmlId(int level) {
		if ((level == 0) || (level < -7) || (7 < level)) {
			return "";
		}
		String htmlid = "";
		if (0 < level) {
			htmlid = "vbk" + level;
		} else if (level < 0) {
			htmlid = "vyk" + Math.abs(level);
		}
		return htmlid;
	}

	private String watchUb(L1PcInstance pc, int npcId) {
		L1UltimateBattle ub = UBTable.getInstance().getUbForNpcId(npcId);
		L1Location loc = ub.getLocation();
		if (pc.getInventory().consumeItem(L1ItemId.ADENA, 100)) {
			try {
				pc.save();
				pc.beginGhost(loc.getX(), loc.getY(), (short) loc.getMapId(),
						true);
			} catch (Exception e) {
				_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
			}
		} else {
			pc.sendPackets(new S_ServerMessage(189)); // \f1ã‚¢ãƒ‡ãƒŠã�Œä¸�è¶³ã�—ã�¦ã�„ã�¾ã�™ã€‚
		}
		return "";
	}

	private String enterUb(L1PcInstance pc, int npcId) {
		L1UltimateBattle ub = UBTable.getInstance().getUbForNpcId(npcId);
		if (!ub.isActive() || !ub.canPcEnter(pc)) { // æ™‚é–“å¤–
			return "colos2";
		}
		if (ub.isNowUb()) { // ç«¶æŠ€ä¸­
			return "colos1";
		}
		if (ub.getMembersCount() >= ub.getMaxPlayer()) { // å®šå“¡ã‚ªãƒ¼ãƒ�ãƒ¼
			return "colos4";
		}

		ub.addMember(pc); // ãƒ¡ãƒ³ãƒ�ãƒ¼ã�«è¿½åŠ 
		L1Location loc = ub.getLocation().randomLocation(10, false);
		L1Teleport.teleport(pc, loc.getX(), loc.getY(), ub.getMapId(), 5, true);
		return "";
	}
/*
	private String enterHauntedHouse(L1PcInstance pc) {
		if (L1HauntedHouse.getInstance().getHauntedHouseStatus() == L1HauntedHouse.STATUS_PLAYING) { // ç«¶æŠ€ä¸­
			pc.sendPackets(new S_ServerMessage(1182)); // ã‚‚ã�†ã‚²ãƒ¼ãƒ ã�¯å§‹ã�¾ã�£ã�¦ã‚‹ã‚ˆã€‚
			return "";
		}
		if (L1HauntedHouse.getInstance().getMembersCount() >= 10) { // å®šå“¡ã‚ªãƒ¼ãƒ�ãƒ¼
			pc.sendPackets(new S_ServerMessage(1184)); // ã�ŠåŒ–ã�‘å±‹æ•·ã�¯äººã�§ã�„ã�£ã�±ã�„ã� ã‚ˆã€‚
			return "";
		}

		L1HauntedHouse.getInstance().addMember(pc); // ãƒ¡ãƒ³ãƒ�ãƒ¼ã�«è¿½åŠ 
		L1Teleport.teleport(pc, 32722, 32830, (short) 5140, 2, true);
		return "";
	}

	private String enterPetMatch(L1PcInstance pc, int objid2) {
		if (pc.getPetList().values().size() > 0) {
			pc.sendPackets(new S_ServerMessage(1187)); // ãƒšãƒƒãƒˆã�®ã‚¢ãƒŸãƒ¥ãƒ¬ãƒƒãƒˆã�Œä½¿ç”¨ä¸­ã�§ã�™ã€‚
			return "";
		}
		if (!L1PetMatch.getInstance().enterPetMatch(pc, objid2)) {
			pc.sendPackets(new S_ServerMessage(1182)); // ã‚‚ã�†ã‚²ãƒ¼ãƒ ã�¯å§‹ã�¾ã�£ã�¦ã‚‹ã‚ˆã€‚
		}
		return "";
	}
*/

    private String enterHauntedHouse(L1PcInstance pc) {
        pc.sendPackets(new S_SystemMessage("Haunted House Is Disabled On Legends"));
        return "";

    }

    private String enterPetMatch(L1PcInstance pc, int objid2) {
        pc.sendPackets(new S_SystemMessage("Pet Match Is Disabled On Legends"));
        return "";
    }

	private void summonMonster(L1PcInstance pc, String s) {
		String[] summonstr_list;
		int[] summonid_list;
		int[] summonlvl_list;
		int[] summoncha_list;
		int summonid = 0;
		int levelrange = 0;
		int summoncost = 0;
		/*
		 * summonstr_list = new String[] { "7", "263", "8", "264", "9", "265",
		 * "10", "266", "11", "267", "12", "268", "13", "269", "14", "270",
		 * "526", "15", "271", "527", "17", "18" }; summonid_list = new int[] {
		 * 81083, 81090, 81084, 81091, 81085, 81092, 81086, 81093, 81087, 81094,
		 * 81088, 81095, 81089, 81096, 81097, 81098, 81099, 81100, 81101, 81102,
		 * 81103, 81104 }; summonlvl_list = new int[] { 28, 28, 32, 32, 36, 36,
		 * 40, 40, 44, 44, 48, 48, 52, 52, 56, 56, 56, 60, 60, 60, 68, 72 }; //
		 * ãƒ‰ãƒƒãƒšãƒ«ã‚²ãƒ³ã‚¬ãƒ¼ãƒœã‚¹ã€�ã‚¯ãƒ¼ã‚¬ãƒ¼ã�«ã�¯ãƒšãƒƒãƒˆãƒœãƒ¼ãƒŠã‚¹ã�Œä»˜ã�‹ã�ªã�„ã�®ã�§+6ã�—ã�¦ã�Šã�� summoncha_list = new int[] { 6,
		 * 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 8, 8, 8, 8, 10, 10, 10, 36, 40 };
		 */
		summonstr_list = new String[] { "7", "263", "519", "8", "264", "520",
				"9", "265", "521", "10", "266", "522", "11", "267", "523",
				"12", "268", "524", "13", "269", "525", "14", "270", "526",
				"15", "271", "527", "16", "17", "18", "274" };
		summonid_list = new int[] { 81210, 81211, 81212, 81213, 81214, 81215,
				81216, 81217, 81218, 81219, 81220, 81221, 81222, 81223, 81224,
				81225, 81226, 81227, 81228, 81229, 81230, 81231, 81232, 81233,
				81234, 81235, 81236, 81237, 81238, 81239, 81240 };
		summonlvl_list = new int[] { 28, 28, 28, 32, 32, 32, 36, 36, 36, 40,
				40, 40, 44, 44, 44, 48, 48, 48, 52, 52, 52, 56, 56, 56, 60, 60,
				60, 64, 68, 72, 72 };
		// ãƒ‰ãƒƒãƒšãƒ«ã‚²ãƒ³ã‚¬ãƒ¼ãƒœã‚¹ã€�ã‚¯ãƒ¼ã‚¬ãƒ¼ã�«ã�¯ãƒšãƒƒãƒˆãƒœãƒ¼ãƒŠã‚¹ã�Œä»˜ã�‹ã�ªã�„ã�®ã�§+6ã�—ã�¦ã�Šã��
		// summoncha_list = new int[] { 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8,
		// 8,
		// 8, 8, 8, 8, 8, 8, 8, 10, 10, 10, 12, 12, 12, 20, 42, 42, 50 };
		summoncha_list = new int[] { 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8,
				8, // 28 ~
					// 44
				8, 8, 8, 8, 8, 8, 10, 10, 10, 12, 12, 12, // 48 ~ 60
				20, 36, 36, 44 }; // 64,68,72,72
		// ã‚µãƒ¢ãƒ³ã�®ç¨®é¡žã€�å¿…è¦�Lvã€�ãƒšãƒƒãƒˆã‚³ã‚¹ãƒˆã‚’å¾—ã‚‹
		for (int loop = 0; loop < summonstr_list.length; loop++) {
			if (s.equalsIgnoreCase(summonstr_list[loop])) {
				summonid = summonid_list[loop];
				levelrange = summonlvl_list[loop];
				summoncost = summoncha_list[loop];
				break;
			}
		}
		// Lvä¸�è¶³
		if (pc.getLevel() < levelrange) {
			// ãƒ¬ãƒ™ãƒ«ã�Œä½Žã��ã�¦è©²å½“ã�®ãƒ¢ãƒ³ã‚¹ã‚¿ãƒ¼ã‚’å�¬é‚„ã�™ã‚‹ã�“ã�¨ã�Œã�§ã��ã�¾ã�›ã‚“ã€‚
			pc.sendPackets(new S_ServerMessage(743));
			return;
		}

		int petcost = 0;
		for (L1NpcInstance petNpc : pc.getPetList().values()) {
			// ç�¾åœ¨ã�®ãƒšãƒƒãƒˆã‚³ã‚¹ãƒˆ
			petcost += petNpc.getPetcost();
		}

		/*
		 * // æ—¢ã�«ãƒšãƒƒãƒˆã�Œã�„ã‚‹å ´å�ˆã�¯ã€�ãƒ‰ãƒƒãƒšãƒ«ã‚²ãƒ³ã‚¬ãƒ¼ãƒœã‚¹ã€�ã‚¯ãƒ¼ã‚¬ãƒ¼ã�¯å‘¼ã�³å‡ºã�›ã�ªã�„ if ((summonid == 81103 ||
		 * summonid == 81104) && petcost != 0) { pc.sendPackets(new
		 * S_CloseList(pc.getId())); return; } int charisma = pc.getCha() + 6 -
		 * petcost; int summoncount = charisma / summoncost;
		 */
		int pcCha = pc.getCha();
		int charisma = 0;
		int summoncount = 0;
		if ((levelrange <= 56 // max count = 5
				)
				|| (levelrange == 64)) { // max count = 2
			if (pcCha > 34) {
				pcCha = 34;
			}
		} else if (levelrange == 60) {
			if (pcCha > 30) { // max count = 3
				pcCha = 30;
			}
		} else if (levelrange > 64) {
			if (pcCha > 44) { // max count = 1
				pcCha = 44;
			}
		}
		charisma = pcCha + 6 - petcost;
		summoncount = charisma / summoncost;

		L1Npc npcTemp = NpcTable.getInstance().getTemplate(summonid);
		for (int cnt = 0; cnt < summoncount; cnt++) {
			L1SummonInstance summon = new L1SummonInstance(npcTemp, pc);
			// if (summonid == 81103 || summonid == 81104) {
			// summon.setPetcost(pc.getCha() + 7);
			// } else {
			summon.setPetcost(summoncost);
			// }
		}
		pc.sendPackets(new S_CloseList(pc.getId()));
	}

	private void poly(ClientThread clientthread, int polyId) {
		L1PcInstance pc = clientthread.getActiveChar();
        //[Legends] - Disable Preventing them from polymorph with buffs on.
        /*
		int awakeSkillId = pc.getAwakeSkillId();
		if ((awakeSkillId == AWAKEN_ANTHARAS)
				|| (awakeSkillId == AWAKEN_FAFURION)
				|| (awakeSkillId == AWAKEN_VALAKAS)) {
			pc.sendPackets(new S_ServerMessage(1384)); // ç�¾åœ¨ã�®çŠ¶æ…‹ã�§ã�¯å¤‰èº«ã�§ã��ã�¾ã�›ã‚“ã€‚
			return;
		}
        */
		if (pc.getInventory().checkItem(L1ItemId.ADENA, 100)) { // check
			pc.getInventory().consumeItem(L1ItemId.ADENA, 100); // del

			L1PolyMorph.doPoly(pc, polyId, 1800, L1PolyMorph.MORPH_BY_NPC);
		} else {
			pc.sendPackets(new S_ServerMessage(337, "$4")); // ã‚¢ãƒ‡ãƒŠã�Œä¸�è¶³ã�—ã�¦ã�„ã�¾ã�™ã€‚
		}
	}

	private void polyByKeplisha(ClientThread clientthread, int polyId) {
		L1PcInstance pc = clientthread.getActiveChar();
        //[Legends] - Disable Preventing them from polymorph with buffs on.
        /*
		int awakeSkillId = pc.getAwakeSkillId();
		if ((awakeSkillId == AWAKEN_ANTHARAS)
				|| (awakeSkillId == AWAKEN_FAFURION)
				|| (awakeSkillId == AWAKEN_VALAKAS)) {
			pc.sendPackets(new S_ServerMessage(1384)); // ç�¾åœ¨ã�®çŠ¶æ…‹ã�§ã�¯å¤‰èº«ã�§ã��ã�¾ã�›ã‚“ã€‚
			return;
		}
        */
		if (pc.getInventory().checkItem(L1ItemId.ADENA, 100)) { // check
			pc.getInventory().consumeItem(L1ItemId.ADENA, 100); // del

			L1PolyMorph.doPoly(pc, polyId, 1800, L1PolyMorph.MORPH_BY_KEPLISHA);
		} else {
			pc.sendPackets(new S_ServerMessage(337, "$4")); // ã‚¢ãƒ‡ãƒŠã�Œä¸�è¶³ã�—ã�¦ã�„ã�¾ã�™ã€‚
		}
	}

	private String sellHouse(L1PcInstance pc, int objectId, int npcId) {
		L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
		if (clan == null) {
			return ""; // ã‚¦ã‚£ãƒ³ãƒ‰ã‚¦ã‚’æ¶ˆã�™
		}
		int houseId = clan.getHouseId();
		if (houseId == 0) {
			return ""; // ã‚¦ã‚£ãƒ³ãƒ‰ã‚¦ã‚’æ¶ˆã�™
		}
		L1House house = HouseTable.getInstance().getHouseTable(houseId);
		int keeperId = house.getKeeperId();
		if (npcId != keeperId) {
			return ""; // ã‚¦ã‚£ãƒ³ãƒ‰ã‚¦ã‚’æ¶ˆã�™
		}
		if (!pc.isCrown()) {
			pc.sendPackets(new S_ServerMessage(518)); // ã�“ã�®å‘½ä»¤ã�¯è¡€ç›Ÿã�®å�›ä¸»ã�®ã�¿ã�Œåˆ©ç”¨ã�§ã��ã�¾ã�™ã€‚
			return ""; // ã‚¦ã‚£ãƒ³ãƒ‰ã‚¦ã‚’æ¶ˆã�™
		}
		if (pc.getId() != clan.getLeaderId()) {
			pc.sendPackets(new S_ServerMessage(518)); // ã�“ã�®å‘½ä»¤ã�¯è¡€ç›Ÿã�®å�›ä¸»ã�®ã�¿ã�Œåˆ©ç”¨ã�§ã��ã�¾ã�™ã€‚
			return ""; // ã‚¦ã‚£ãƒ³ãƒ‰ã‚¦ã‚’æ¶ˆã�™
		}
		if (house.isOnSale()) {
			return "agonsale";
		}

		pc.sendPackets(new S_SellHouse(objectId, String.valueOf(houseId)));
		return null;
	}

	private void openCloseDoor(L1PcInstance pc, L1NpcInstance npc, String s) {
		L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
		if (clan != null) {
			int houseId = clan.getHouseId();
			if (houseId != 0) {
				L1House house = HouseTable.getInstance().getHouseTable(houseId);
				int keeperId = house.getKeeperId();
				if (npc.getNpcTemplate().get_npcId() == keeperId) {
					L1DoorInstance door1 = null;
					L1DoorInstance door2 = null;
					L1DoorInstance door3 = null;
					L1DoorInstance door4 = null;
					for (L1DoorInstance door : DoorTable.getInstance()
							.getDoorList()) {
						if (door.getKeeperId() == keeperId) {
							if (door1 == null) {
								door1 = door;
								continue;
							}
							if (door2 == null) {
								door2 = door;
								continue;
							}
							if (door3 == null) {
								door3 = door;
								continue;
							}
							if (door4 == null) {
								door4 = door;
								break;
							}
						}
					}
					if (door1 != null) {
						if (s.equalsIgnoreCase("open")) {
							door1.open();
						} else if (s.equalsIgnoreCase("close")) {
							door1.close();
						}
					}
					if (door2 != null) {
						if (s.equalsIgnoreCase("open")) {
							door2.open();
						} else if (s.equalsIgnoreCase("close")) {
							door2.close();
						}
					}
					if (door3 != null) {
						if (s.equalsIgnoreCase("open")) {
							door3.open();
						} else if (s.equalsIgnoreCase("close")) {
							door3.close();
						}
					}
					if (door4 != null) {
						if (s.equalsIgnoreCase("open")) {
							door4.open();
						} else if (s.equalsIgnoreCase("close")) {
							door4.close();
						}
					}
				}
			}
		}
	}

	private void openCloseGate(L1PcInstance pc, int keeperId, boolean isOpen) {
		boolean isNowWar = false;
		int pcCastleId = 0;
		if (pc.getClanid() != 0) {
			L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
			if (clan != null) {
				pcCastleId = clan.getCastleId();
			}
		}
		if ((keeperId == 70656) || (keeperId == 70549) || (keeperId == 70985)) { // ã‚±ãƒ³ãƒˆåŸŽ
			if (isExistDefenseClan(L1CastleLocation.KENT_CASTLE_ID)) {
				if (pcCastleId != L1CastleLocation.KENT_CASTLE_ID) {
					return;
				}
			}
			isNowWar = WarTimeController.getInstance().isNowWar(
					L1CastleLocation.KENT_CASTLE_ID);
		} else if (keeperId == 70600) { // OT
			if (isExistDefenseClan(L1CastleLocation.OT_CASTLE_ID)) {
				if (pcCastleId != L1CastleLocation.OT_CASTLE_ID) {
					return;
				}
			}
			isNowWar = WarTimeController.getInstance().isNowWar(
					L1CastleLocation.OT_CASTLE_ID);
		} else if ((keeperId == 70778) || (keeperId == 70987)
				|| (keeperId == 70687)) { // WWåŸŽ
			if (isExistDefenseClan(L1CastleLocation.WW_CASTLE_ID)) {
				if (pcCastleId != L1CastleLocation.WW_CASTLE_ID) {
					return;
				}
			}
			isNowWar = WarTimeController.getInstance().isNowWar(
					L1CastleLocation.WW_CASTLE_ID);
		} else if ((keeperId == 70817) || (keeperId == 70800)
				|| (keeperId == 70988) || (keeperId == 70990)
				|| (keeperId == 70989) || (keeperId == 70991)) { // ã‚®ãƒ©ãƒ³åŸŽ
			if (isExistDefenseClan(L1CastleLocation.GIRAN_CASTLE_ID)) {
				if (pcCastleId != L1CastleLocation.GIRAN_CASTLE_ID) {
					return;
				}
			}
			isNowWar = WarTimeController.getInstance().isNowWar(
					L1CastleLocation.GIRAN_CASTLE_ID);
		} else if ((keeperId == 70863) || (keeperId == 70992)
				|| (keeperId == 70862)) { // ãƒ�ã‚¤ãƒ�åŸŽ
			if (isExistDefenseClan(L1CastleLocation.HEINE_CASTLE_ID)) {
				if (pcCastleId != L1CastleLocation.HEINE_CASTLE_ID) {
					return;
				}
			}
			isNowWar = WarTimeController.getInstance().isNowWar(
					L1CastleLocation.HEINE_CASTLE_ID);
		} else if ((keeperId == 70995) || (keeperId == 70994)
				|| (keeperId == 70993)) { // ãƒ‰ãƒ¯ãƒ¼ãƒ•åŸŽ
			if (isExistDefenseClan(L1CastleLocation.DOWA_CASTLE_ID)) {
				if (pcCastleId != L1CastleLocation.DOWA_CASTLE_ID) {
					return;
				}
			}
			isNowWar = WarTimeController.getInstance().isNowWar(
					L1CastleLocation.DOWA_CASTLE_ID);
		} else if (keeperId == 70996) { // ã‚¢ãƒ‡ãƒ³åŸŽ
			if (isExistDefenseClan(L1CastleLocation.ADEN_CASTLE_ID)) {
				if (pcCastleId != L1CastleLocation.ADEN_CASTLE_ID) {
					return;
				}
			}
			isNowWar = WarTimeController.getInstance().isNowWar(
					L1CastleLocation.ADEN_CASTLE_ID);
		}

		for (L1DoorInstance door : DoorTable.getInstance().getDoorList()) {
			if (door.getKeeperId() == keeperId) {
				if (isNowWar && (door.getMaxHp() > 1)) { // æˆ¦äº‰ä¸­ã�¯åŸŽé–€é–‹é–‰ä¸�å�¯
				} else {
					if (isOpen) { // é–‹
						door.open();
					} else { // é–‰
						door.close();
					}
				}
			}
		}
	}

	private boolean isExistDefenseClan(int castleId) {
		boolean isExistDefenseClan = false;
		for (L1Clan clan : L1World.getInstance().getAllClans()) {
			if (castleId == clan.getCastleId()) {
				isExistDefenseClan = true;
				break;
			}
		}
		return isExistDefenseClan;
	}

	private void expelOtherClan(L1PcInstance clanPc, int keeperId) {
		int houseId = 0;
		for (L1House house : HouseTable.getInstance().getHouseTableList()) {
			if (house.getKeeperId() == keeperId) {
				houseId = house.getHouseId();
			}
		}
		if (houseId == 0) {
			return;
		}

		int[] loc = new int[3];
		for (L1Object object : L1World.getInstance().getObject()) {
			if (object instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) object;
				if (L1HouseLocation.isInHouseLoc(houseId, pc.getX(), pc.getY(),
						pc.getMapId())
						&& (clanPc.getClanid() != pc.getClanid())) {
					loc = L1HouseLocation.getHouseTeleportLoc(houseId, 0);
					if (pc != null) {
						L1Teleport.teleport(pc, loc[0], loc[1], (short) loc[2],
								5, true);
					}
				}
			}
		}
	}

	private void repairGate(L1PcInstance pc) {
		L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
		if (clan != null) {
			int castleId = clan.getCastleId();
			if (castleId != 0) { // åŸŽä¸»ã‚¯ãƒ©ãƒ³
				if (!WarTimeController.getInstance().isNowWar(castleId)) {
					// åŸŽé–€ã‚’å…ƒã�«æˆ»ã�™
					for (L1DoorInstance door : DoorTable.getInstance()
							.getDoorList()) {
						if (L1CastleLocation.checkInWarArea(castleId, door)) {
							door.repairGate();
						}
					}
					pc.sendPackets(new S_ServerMessage(990)); // åŸŽé–€è‡ªå‹•ä¿®ç�†ã‚’å‘½ä»¤ã�—ã�¾ã�—ã�Ÿã€‚
				} else {
					pc.sendPackets(new S_ServerMessage(991)); // åŸŽé–€è‡ªå‹•ä¿®ç�†å‘½ä»¤ã‚’å�–ã‚Šæ¶ˆã�—ã�¾ã�—ã�Ÿã€‚
				}
			}
		}
	}

	private boolean payFee(L1PcInstance pc, L1NpcInstance npc) {
		L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
		if (clan != null) {
			int houseId = clan.getHouseId();
			if (houseId != 0) {
				L1House house = HouseTable.getInstance().getHouseTable(houseId);
				int keeperId = house.getKeeperId();
				if (npc.getNpcTemplate().get_npcId() == keeperId) {
					TimeZone tz = TimeZone.getTimeZone(Config.TIME_ZONE);
					Calendar cal = Calendar.getInstance(tz); // ç›®å‰�æ™‚é–“
					Calendar deadlineCal = house.getTaxDeadline(); // ç›Ÿå±‹åˆ°æœŸæ™‚é–“

//					int remainingTime = (int) ((deadlineCal.getTimeInMillis() - cal.getTimeInMillis()) / (1000 * 60 * 60 * 24));
					// ç§ŸæœŸå‰©é¤˜æ™‚é–“å¤§æ–¼ä¸€å�Š ä¸�ç”¨ç¹³æˆ¿ç§Ÿ
//					if (remainingTime >= Config.HOUSE_TAX_INTERVAL / 2)
//						return true;
					if (pc.getInventory().checkItem(L1ItemId.ADENA, 2000)) {
						pc.getInventory().consumeItem(L1ItemId.ADENA, 2000);
						// æ”¯ä»˜å¾Œ deadlineå»¶æœŸ
						deadlineCal.add(Calendar.DATE,Config.HOUSE_TAX_INTERVAL);
						deadlineCal.set(Calendar.MINUTE, 0); // åˆ†ã€�ç§’ã�¯åˆ‡ã‚Šæ�¨ã�¦
						deadlineCal.set(Calendar.SECOND, 0);
						house.setTaxDeadline(deadlineCal);
						HouseTable.getInstance().updateHouse(house); // DBã�«æ›¸ã��è¾¼ã�¿
						return true;
					} else {
						pc.sendPackets(new S_ServerMessage(189)); // \f1ã‚¢ãƒ‡ãƒŠã�Œä¸�è¶³ã�—ã�¦ã�„ã�¾ã�™ã€‚
					}
				}
			}
		}
		return false;
	}

	private String[] makeHouseTaxStrings(L1PcInstance pc, L1NpcInstance npc) {
		String name = npc.getNpcTemplate().get_name();
		String[] result;
		result = new String[] { name, "2000", "1", "1", "00" };
		L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
		if (clan != null) {
			int houseId = clan.getHouseId();
			if (houseId != 0) {
				L1House house = HouseTable.getInstance().getHouseTable(houseId);
				int keeperId = house.getKeeperId();
				if (npc.getNpcTemplate().get_npcId() == keeperId) {
					Calendar cal = house.getTaxDeadline();
					int month = cal.get(Calendar.MONTH) + 1;
					int day = cal.get(Calendar.DATE);
					int hour = cal.get(Calendar.HOUR_OF_DAY);
					result = new String[] { name, "2000",
							String.valueOf(month), String.valueOf(day),
							String.valueOf(hour) };
				}
			}
		}
		return result;
	}

	private String[] makeWarTimeStrings(int castleId) {
		L1Castle castle = CastleTable.getInstance().getCastleTable(castleId);
		if (castle == null) {
			return null;
		}
		Calendar warTime = castle.getWarTime();
		int year = warTime.get(Calendar.YEAR);
		int month = warTime.get(Calendar.MONTH) + 1;
		int day = warTime.get(Calendar.DATE);
		int hour = warTime.get(Calendar.HOUR_OF_DAY);
		int minute = warTime.get(Calendar.MINUTE);
		String[] result;
		if (castleId == L1CastleLocation.OT_CASTLE_ID) {
			result = new String[] { String.valueOf(year),
					String.valueOf(month), String.valueOf(day),
					String.valueOf(hour), String.valueOf(minute) };
		} else {
			result = new String[] { "", String.valueOf(year),
					String.valueOf(month), String.valueOf(day),
					String.valueOf(hour), String.valueOf(minute) };
		}
		return result;
	}

	private String getYaheeAmulet(L1PcInstance pc, L1NpcInstance npc, String s) {
		int[] amuletIdList = { 20358, 20359, 20360, 20361, 20362, 20363, 20364,
				20365 };
		int amuletId = 0;
		L1ItemInstance item = null;
		String htmlid = null;
		if (s.equalsIgnoreCase("1")) {
			amuletId = amuletIdList[0];
		} else if (s.equalsIgnoreCase("2")) {
			amuletId = amuletIdList[1];
		} else if (s.equalsIgnoreCase("3")) {
			amuletId = amuletIdList[2];
		} else if (s.equalsIgnoreCase("4")) {
			amuletId = amuletIdList[3];
		} else if (s.equalsIgnoreCase("5")) {
			amuletId = amuletIdList[4];
		} else if (s.equalsIgnoreCase("6")) {
			amuletId = amuletIdList[5];
		} else if (s.equalsIgnoreCase("7")) {
			amuletId = amuletIdList[6];
		} else if (s.equalsIgnoreCase("8")) {
			amuletId = amuletIdList[7];
		}
		if (amuletId != 0) {
			item = pc.getInventory().storeItem(amuletId, 1);
			if (item != null) {
				pc.sendPackets(new S_ServerMessage(143, npc.getNpcTemplate()
						.get_name(), item.getLogName())); // \f1%0ã�Œ%1ã‚’ã��ã‚Œã�¾ã�—ã�Ÿã€‚
			}
			for (int id : amuletIdList) {
				if (id == amuletId) {
					break;
				}
				if (pc.getInventory().checkItem(id)) {
					pc.getInventory().consumeItem(id, 1);
				}
			}
			htmlid = "";
		}
		return htmlid;
	}

	private String getBarlogEarring(L1PcInstance pc, L1NpcInstance npc, String s) {
		int[] earringIdList = { 21020, 21021, 21022, 21023, 21024, 21025,
				21026, 21027 };
		int earringId = 0;
		L1ItemInstance item = null;
		String htmlid = null;
		if (s.equalsIgnoreCase("1")) {
			earringId = earringIdList[0];
		} else if (s.equalsIgnoreCase("2")) {
			earringId = earringIdList[1];
		} else if (s.equalsIgnoreCase("3")) {
			earringId = earringIdList[2];
		} else if (s.equalsIgnoreCase("4")) {
			earringId = earringIdList[3];
		} else if (s.equalsIgnoreCase("5")) {
			earringId = earringIdList[4];
		} else if (s.equalsIgnoreCase("6")) {
			earringId = earringIdList[5];
		} else if (s.equalsIgnoreCase("7")) {
			earringId = earringIdList[6];
		} else if (s.equalsIgnoreCase("8")) {
			earringId = earringIdList[7];
		}
		if (earringId != 0) {
			item = pc.getInventory().storeItem(earringId, 1);
			if (item != null) {
				pc.sendPackets(new S_ServerMessage(143, npc.getNpcTemplate()
						.get_name(), item.getLogName())); // \f1%0ã�Œ%1ã‚’ã��ã‚Œã�¾ã�—ã�Ÿã€‚
			}
			for (int id : earringIdList) {
				if (id == earringId) {
					break;
				}
				if (pc.getInventory().checkItem(id)) {
					pc.getInventory().consumeItem(id, 1);
				}
			}
			htmlid = "";
		}
		return htmlid;
	}

	private String[] makeUbInfoStrings(int npcId) {
		L1UltimateBattle ub = UBTable.getInstance().getUbForNpcId(npcId);
		return ub.makeUbInfoStrings();
	}

	private String talkToDimensionDoor(L1PcInstance pc, L1NpcInstance npc,
			String s) {
		String htmlid = "";
		int protectionId = 0;
		int sealId = 0;
		int locX = 0;
		int locY = 0;
		short mapId = 0;
		if (npc.getNpcTemplate().get_npcId() == 80059) { // æ¬¡å…ƒã�®æ‰‰(åœŸ)
			protectionId = 40909;
			sealId = 40913;
			locX = 32773;
			locY = 32835;
			mapId = 607;
		} else if (npc.getNpcTemplate().get_npcId() == 80060) { // æ¬¡å…ƒã�®æ‰‰(é¢¨)
			protectionId = 40912;
			sealId = 40916;
			locX = 32757;
			locY = 32842;
			mapId = 606;
		} else if (npc.getNpcTemplate().get_npcId() == 80061) { // æ¬¡å…ƒã�®æ‰‰(æ°´)
			protectionId = 40910;
			sealId = 40914;
			locX = 32830;
			locY = 32822;
			mapId = 604;
		} else if (npc.getNpcTemplate().get_npcId() == 80062) { // æ¬¡å…ƒã�®æ‰‰(ç�«)
			protectionId = 40911;
			sealId = 40915;
			locX = 32835;
			locY = 32822;
			mapId = 605;
		}

		// ã€Œä¸­ã�«å…¥ã�£ã�¦ã�¿ã‚‹ã€�ã€Œå…ƒç´ ã�®æ”¯é…�è€…ã‚’è¿‘ã�¥ã�‘ã�¦ã�¿ã‚‹ã€�ã€Œé€šè¡Œè¨¼ã‚’ä½¿ã�†ã€�ã€Œé€šé�Žã�™ã‚‹ã€�
		if (s.equalsIgnoreCase("a")) {
			L1Teleport.teleport(pc, locX, locY, mapId, 5, true);
			htmlid = "";
		}
		// ã€Œçµµã�‹ã‚‰çª�å‡ºéƒ¨åˆ†ã‚’å�–ã‚Šé™¤ã��ã€�
		else if (s.equalsIgnoreCase("b")) {
			L1ItemInstance item = pc.getInventory().storeItem(protectionId, 1);
			if (item != null) {
				pc.sendPackets(new S_ServerMessage(143, npc.getNpcTemplate()
						.get_name(), item.getLogName())); // \f1%0ã�Œ%1ã‚’ã��ã‚Œã�¾ã�—ã�Ÿã€‚
			}
			htmlid = "";
		}
		// ã€Œé€šè¡Œè¨¼ã‚’æ�¨ã�¦ã�¦ã€�ã�“ã�®åœ°ã‚’ã�‚ã��ã‚‰ã‚�ã‚‹ã€�
		else if (s.equalsIgnoreCase("c")) {
			htmlid = "wpass07";
		}
		// ã€Œç¶šã�‘ã‚‹ã€�
		else if (s.equalsIgnoreCase("d")) {
			if (pc.getInventory().checkItem(sealId)) { // åœ°ã�®å�°ç« 
				L1ItemInstance item = pc.getInventory().findItemId(sealId);
				pc.getInventory().consumeItem(sealId, item.getCount());
			}
		}
		// ã€Œã��ã�®ã�¾ã�¾ã�«ã�™ã‚‹ã€�ã€Œæ…Œã�¦ã�¦æ‹¾ã�†ã€�
		else if (s.equalsIgnoreCase("e")) {
			htmlid = "";
		}
		// ã€Œæ¶ˆã�ˆã‚‹ã‚ˆã�†ã�«ã�™ã‚‹ã€�
		else if (s.equalsIgnoreCase("f")) {
			if (pc.getInventory().checkItem(protectionId)) { // åœ°ã�®é€šè¡Œè¨¼
				pc.getInventory().consumeItem(protectionId, 1);
			}
			if (pc.getInventory().checkItem(sealId)) { // åœ°ã�®å�°ç« 
				L1ItemInstance item = pc.getInventory().findItemId(sealId);
				pc.getInventory().consumeItem(sealId, item.getCount());
			}
			htmlid = "";
		}
		return htmlid;
	}

	private boolean isNpcSellOnly(L1NpcInstance npc) {
		int npcId = npc.getNpcTemplate().get_npcId();
		String npcName = npc.getNpcTemplate().get_name();
		if ((npcId == 70027 // ãƒ‡ã‚£ã‚ª
				)
				|| "äºžä¸�å•†åœ˜".equals(npcName)) {
			return true;
		}
		return false;
	}

	private void getBloodCrystalByKarma(L1PcInstance pc, L1NpcInstance npc,
			String s) {
		L1ItemInstance item = null;

		// ã€Œãƒ–ãƒ©ãƒƒãƒ‰ã‚¯ãƒªã‚¹ã‚¿ãƒ«ã�®æ¬ ç‰‡ã‚’1å€‹ã��ã� ã�•ã�„ã€�
		if (s.equalsIgnoreCase("1")) {
			pc.addKarma((int) (500 * Config.RATE_KARMA));
			item = pc.getInventory().storeItem(40718, 1);
			if (item != null) {
				pc.sendPackets(new S_ServerMessage(143, npc.getNpcTemplate()
						.get_name(), item.getLogName())); // \f1%0ã�Œ%1ã‚’ã��ã‚Œã�¾ã�—ã�Ÿã€‚
			}
			// ãƒ¤ãƒ’ã�®å§¿ã‚’è¨˜æ†¶ã�™ã‚‹ã�®ã�Œé›£ã�—ã��ã�ªã‚Šã�¾ã�™ã€‚
			pc.sendPackets(new S_ServerMessage(1081));
		}
		// ã€Œãƒ–ãƒ©ãƒƒãƒ‰ã‚¯ãƒªã‚¹ã‚¿ãƒ«ã�®æ¬ ç‰‡ã‚’10å€‹ã��ã� ã�•ã�„ã€�
		else if (s.equalsIgnoreCase("2")) {
			pc.addKarma((int) (5000 * Config.RATE_KARMA));
			item = pc.getInventory().storeItem(40718, 10);
			if (item != null) {
				pc.sendPackets(new S_ServerMessage(143, npc.getNpcTemplate()
						.get_name(), item.getLogName())); // \f1%0ã�Œ%1ã‚’ã��ã‚Œã�¾ã�—ã�Ÿã€‚
			}
			// ãƒ¤ãƒ’ã�®å§¿ã‚’è¨˜æ†¶ã�™ã‚‹ã�®ã�Œé›£ã�—ã��ã�ªã‚Šã�¾ã�™ã€‚
			pc.sendPackets(new S_ServerMessage(1081));
		}
		// ã€Œãƒ–ãƒ©ãƒƒãƒ‰ã‚¯ãƒªã‚¹ã‚¿ãƒ«ã�®æ¬ ç‰‡ã‚’100å€‹ã��ã� ã�•ã�„ã€�
		else if (s.equalsIgnoreCase("3")) {
			pc.addKarma((int) (50000 * Config.RATE_KARMA));
			item = pc.getInventory().storeItem(40718, 100);
			if (item != null) {
				pc.sendPackets(new S_ServerMessage(143, npc.getNpcTemplate()
						.get_name(), item.getLogName())); // \f1%0ã�Œ%1ã‚’ã��ã‚Œã�¾ã�—ã�Ÿã€‚
			}
			// ãƒ¤ãƒ’ã�®å§¿ã‚’è¨˜æ†¶ã�™ã‚‹ã�®ã�Œé›£ã�—ã��ã�ªã‚Šã�¾ã�™ã€‚
			pc.sendPackets(new S_ServerMessage(1081));
		}
	}

	private void getSoulCrystalByKarma(L1PcInstance pc, L1NpcInstance npc,
			String s) {
		L1ItemInstance item = null;

		// ã€Œã‚½ã‚¦ãƒ«ã‚¯ãƒªã‚¹ã‚¿ãƒ«ã�®æ¬ ç‰‡ã‚’1å€‹ã��ã� ã�•ã�„ã€�
		if (s.equalsIgnoreCase("1")) {
			pc.addKarma((int) (-500 * Config.RATE_KARMA));
			item = pc.getInventory().storeItem(40678, 1);
			if (item != null) {
				pc.sendPackets(new S_ServerMessage(143, npc.getNpcTemplate()
						.get_name(), item.getLogName())); // \f1%0ã�Œ%1ã‚’ã��ã‚Œã�¾ã�—ã�Ÿã€‚
			}
			// ãƒ�ãƒ«ãƒ­ã‚°ã�®å†·ç¬‘ã‚’æ„Ÿã�˜æ‚ªå¯’ã�Œèµ°ã‚Šã�¾ã�™ã€‚
			pc.sendPackets(new S_ServerMessage(1080));
		}
		// ã€Œã‚½ã‚¦ãƒ«ã‚¯ãƒªã‚¹ã‚¿ãƒ«ã�®æ¬ ç‰‡ã‚’10å€‹ã��ã� ã�•ã�„ã€�
		else if (s.equalsIgnoreCase("2")) {
			pc.addKarma((int) (-5000 * Config.RATE_KARMA));
			item = pc.getInventory().storeItem(40678, 10);
			if (item != null) {
				pc.sendPackets(new S_ServerMessage(143, npc.getNpcTemplate()
						.get_name(), item.getLogName())); // \f1%0ã�Œ%1ã‚’ã��ã‚Œã�¾ã�—ã�Ÿã€‚
			}
			// ãƒ�ãƒ«ãƒ­ã‚°ã�®å†·ç¬‘ã‚’æ„Ÿã�˜æ‚ªå¯’ã�Œèµ°ã‚Šã�¾ã�™ã€‚
			pc.sendPackets(new S_ServerMessage(1080));
		}
		// ã€Œã‚½ã‚¦ãƒ«ã‚¯ãƒªã‚¹ã‚¿ãƒ«ã�®æ¬ ç‰‡ã‚’100å€‹ã��ã� ã�•ã�„ã€�
		else if (s.equalsIgnoreCase("3")) {
			pc.addKarma((int) (-50000 * Config.RATE_KARMA));
			item = pc.getInventory().storeItem(40678, 100);
			if (item != null) {
				pc.sendPackets(new S_ServerMessage(143, npc.getNpcTemplate()
						.get_name(), item.getLogName())); // \f1%0ã�Œ%1ã‚’ã��ã‚Œã�¾ã�—ã�Ÿã€‚
			}
			// ãƒ�ãƒ«ãƒ­ã‚°ã�®å†·ç¬‘ã‚’æ„Ÿã�˜æ‚ªå¯’ã�Œèµ°ã‚Šã�¾ã�™ã€‚
			pc.sendPackets(new S_ServerMessage(1080));
		}
	}
	
	private boolean usePolyScroll(L1PcInstance pc, int itemId, String s) {
		int time = 0;
		if ((itemId == 40088) || (itemId == 40096)) { // å¤‰èº«ã‚¹ã‚¯ãƒ­ãƒ¼ãƒ«ã€�è±¡ç‰™ã�®å¡”ã�®å¤‰èº«ã‚¹ã‚¯ãƒ­ãƒ¼ãƒ«
			time = 1800;
		} else if (itemId == 140088) { // ç¥�ç¦�ã�•ã‚Œã�Ÿå¤‰èº«ã‚¹ã‚¯ãƒ­ãƒ¼ãƒ«
			time = 2100;
		}

		L1PolyMorph poly = PolyTable.getInstance().getTemplate(s);
		L1ItemInstance item = pc.getInventory().findItemId(itemId);
		boolean isUseItem = false;
		if ((poly != null) || s.equals("none")) {
			if (s.equals("none")) {
				if ((pc.getTempCharGfx() == 6034)
						|| (pc.getTempCharGfx() == 6035)) {
					isUseItem = true;
				} else {
					pc.removeSkillEffect(SHAPE_CHANGE);
					isUseItem = true;
				}
			} else if ((poly.getMinLevel() <= pc.getLevel()) || pc.isGm()) {
				L1PolyMorph.doPoly(pc, poly.getPolyId(), time,
						L1PolyMorph.MORPH_BY_ITEMMAGIC);
				isUseItem = true;
			}
		}
		if (isUseItem) {
			pc.getInventory().removeItem(item, 1);
		} else {
			pc.sendPackets(new S_ServerMessage(181)); // \f1ã��ã�®ã‚ˆã�†ã�ªãƒ¢ãƒ³ã‚¹ã‚¿ãƒ¼ã�«ã�¯å¤‰èº«ã�§ã��ã�¾ã�›ã‚“ã€‚
		}
		return isUseItem;
	}

	@Override
	public String getType() {
		return C_NPC_ACTION;
	}

}
