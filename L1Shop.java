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
package l1j.server.server.model.shop;

import java.util.List;

import l1j.server.Config;
import l1j.server.server.datatables.CastleTable;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.datatables.TownTable;
import l1j.server.server.model.L1CastleLocation;
import l1j.server.server.model.L1PcInventory;
import l1j.server.server.model.L1TaxCalculator;
import l1j.server.server.model.L1TownLocation;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.game.L1BugBearRace;
import l1j.server.server.model.identity.L1ItemId;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.templates.L1Castle;
import l1j.server.server.templates.L1Item;
import l1j.server.server.templates.L1ShopItem;
import l1j.server.server.utils.IntRange;
import l1j.server.server.utils.Random;
import l1j.server.server.utils.collections.Lists;

import l1j.server.server.log.LogShopBuy;
import l1j.server.server.log.LogShopSell;

public class L1Shop {
	private final int _npcId;

	private final List<L1ShopItem> _sellingItems;

	private final List<L1ShopItem> _purchasingItems;

	public L1Shop(int npcId, List<L1ShopItem> sellingItems,
			List<L1ShopItem> purchasingItems) {
		if ((sellingItems == null) || (purchasingItems == null)) {
			throw new NullPointerException();
		}

		_npcId = npcId;
		_sellingItems = sellingItems;
		_purchasingItems = purchasingItems;
	}

	public int getNpcId() {
		return _npcId;
	}

	public List<L1ShopItem> getSellingItems() {
		return _sellingItems;
	}

	/**
	 * ã�“ã�®å•†åº—ã�§ã€�æŒ‡å®šã�•ã‚Œã�Ÿã‚¢ã‚¤ãƒ†ãƒ ã�Œè²·å�–å�¯èƒ½ã�ªçŠ¶æ…‹ã�§ã�‚ã‚‹ã�‹ã‚’è¿”ã�™ã€‚
	 * 
	 * @param item
	 * @return ã‚¢ã‚¤ãƒ†ãƒ ã�Œè²·å�–å�¯èƒ½ã�§ã�‚ã‚Œã�°true
	 */
	private boolean isPurchaseableItem(L1ItemInstance item) {
		if (item == null) {
			return false;
		}
		if (item.isEquipped()) { // è£…å‚™ä¸­ã�§ã�‚ã‚Œã�°ä¸�å�¯
			return false;
		}
		if (item.getEnchantLevel() != 0) { // å¼·åŒ–(orå¼±åŒ–)ã�•ã‚Œã�¦ã�„ã‚Œã�°ä¸�å�¯
			return false;
		}
		if (item.getBless() >= 128) { // å°�å�°ã�•ã‚Œã�Ÿè£…å‚™
			return false;
		}

		return true;
	}

	private L1ShopItem getPurchasingItem(int itemId) {
		for (L1ShopItem shopItem : _purchasingItems) {
			if (shopItem.getItemId() == itemId) {
				return shopItem;
			}
		}
		return null;
	}

	public L1AssessedItem assessItem(L1ItemInstance item) {
		L1ShopItem shopItem = getPurchasingItem(item.getItemId());
		if (shopItem == null) {
			return null;
		}
		return new L1AssessedItem(item.getId(), getAssessedPrice(shopItem));
	}

	private int getAssessedPrice(L1ShopItem item) {
		return (int) (item.getPrice() * Config.RATE_SHOP_PURCHASING_PRICE / item
				.getPackCount());
	}

	/**
	 * ã‚¤ãƒ³ãƒ™ãƒ³ãƒˆãƒªå†…ã�®è²·å�–å�¯èƒ½ã‚¢ã‚¤ãƒ†ãƒ ã‚’æŸ»å®šã�™ã‚‹ã€‚
	 * 
	 * @param inv
	 *            æŸ»å®šå¯¾è±¡ã�®ã‚¤ãƒ³ãƒ™ãƒ³ãƒˆãƒª
	 * @return æŸ»å®šã�•ã‚Œã�Ÿè²·å�–å�¯èƒ½ã‚¢ã‚¤ãƒ†ãƒ ã�®ãƒªã‚¹ãƒˆ
	 */
	public List<L1AssessedItem> assessItems(L1PcInventory inv) {
		List<L1AssessedItem> result = Lists.newList();
		for (L1ShopItem item : _purchasingItems) {
			for (L1ItemInstance targetItem : inv.findItemsId(item.getItemId())) {
				if (!isPurchaseableItem(targetItem)) {
					continue;
				}

				result.add(new L1AssessedItem(targetItem.getId(),
						getAssessedPrice(item)));
			}
		}
		return result;
	}

	/**
	 * ãƒ—ãƒ¬ã‚¤ãƒ¤ãƒ¼ã�¸ã‚¢ã‚¤ãƒ†ãƒ ã‚’è²©å£²ã�§ã��ã‚‹ã�“ã�¨ã‚’ä¿�è¨¼ã�™ã‚‹ã€‚
	 * 
	 * @return ä½•ã‚‰ã�‹ã�®ç�†ç”±ã�§ã‚¢ã‚¤ãƒ†ãƒ ã‚’è²©å£²ã�§ã��ã�ªã�„å ´å�ˆã€�false
	 */
	private boolean ensureSell(L1PcInstance pc, L1ShopBuyOrderList orderList) {
		int price = orderList.getTotalPriceTaxIncluded();
		// ã‚ªãƒ¼ãƒ�ãƒ¼ãƒ•ãƒ­ãƒ¼ãƒ�ã‚§ãƒƒã‚¯
		if (!IntRange.includes(price, 0, 2000000000)) {
			// ç·�è²©å£²ä¾¡æ ¼ã�¯%dã‚¢ãƒ‡ãƒŠã‚’è¶…é�Žã�§ã��ã�¾ã�›ã‚“ã€‚
			pc.sendPackets(new S_ServerMessage(904, "2000000000"));
			return false;
		}
		// è³¼å…¥ã�§ã��ã‚‹ã�‹ãƒ�ã‚§ãƒƒã‚¯
		if (!pc.getInventory().checkItem(L1ItemId.ADENA, price)) {
			System.out.println(price);
			// \f1ã‚¢ãƒ‡ãƒŠã�Œä¸�è¶³ã�—ã�¦ã�„ã�¾ã�™ã€‚
			pc.sendPackets(new S_ServerMessage(189));
			return false;
		}
		// é‡�é‡�ãƒ�ã‚§ãƒƒã‚¯
		int currentWeight = pc.getInventory().getWeight() * 1000;
		if (currentWeight + orderList.getTotalWeight() > pc.getMaxWeight() * 1000) {
			// ã‚¢ã‚¤ãƒ†ãƒ ã�Œé‡�ã�™ã�Žã�¦ã€�ã�“ã‚Œä»¥ä¸ŠæŒ�ã�¦ã�¾ã�›ã‚“ã€‚
			pc.sendPackets(new S_ServerMessage(82));
			return false;
		}
		// å€‹æ•°ãƒ�ã‚§ãƒƒã‚¯
		int totalCount = pc.getInventory().getSize();
		for (L1ShopBuyOrder order : orderList.getList()) {
			L1Item temp = order.getItem().getItem();
			if (temp.isStackable()) {
				if (!pc.getInventory().checkItem(temp.getItemId())) {
					totalCount += 1;
				}
			} else {
				totalCount += 1;
			}
		}
		if (totalCount > 180) {
			// \f1ä¸€äººã�®ã‚­ãƒ£ãƒ©ã‚¯ã‚¿ãƒ¼ã�ŒæŒ�ã�£ã�¦æ­©ã�‘ã‚‹ã‚¢ã‚¤ãƒ†ãƒ ã�¯æœ€å¤§180å€‹ã�¾ã�§ã�§ã�™ã€‚
			pc.sendPackets(new S_ServerMessage(263));
			return false;
		}
		return true;
	}
	
	
	private boolean ensureSellToken(L1PcInstance pc, L1ShopBuyOrderList orderList) {
		int price = orderList.getTotalPriceTaxIncluded();
		
		if (!IntRange.includes(price, 0, 2000000000)) {
			pc.sendPackets(new S_ServerMessage(904, "2000000000"));
			return false;
		}
		if (!pc.getInventory().checkItem(L1ItemId.ARENA_TOKEN, price)) {
			System.out.println(price);
			pc.sendPackets(new S_ServerMessage(189));
			return false;
		}
		
		int currentWeight = pc.getInventory().getWeight() * 1000;
		if (currentWeight + orderList.getTotalWeight() > pc.getMaxWeight() * 1000) {
			pc.sendPackets(new S_ServerMessage(82));
			return false;
		}
		int totalCount = pc.getInventory().getSize();
		for (L1ShopBuyOrder order : orderList.getList()) {
			L1Item temp = order.getItem().getItem();
			if (temp.isStackable()) {
				if (!pc.getInventory().checkItem(temp.getItemId())) {
					totalCount += 1;
				}
			} else {
				totalCount += 1;
			}
		}
		if (totalCount > 180) {
			pc.sendPackets(new S_ServerMessage(263));
			return false;
		}
		return true;
	}
	
	
	/**
	 * åœ°åŸŸç¨Žç´�ç¨Žå‡¦ç�† ã‚¢ãƒ‡ãƒ³åŸŽãƒ»ãƒ‡ã‚£ã‚¢ãƒ‰è¦�å¡žã‚’é™¤ã��åŸŽã�¯ã‚¢ãƒ‡ãƒ³åŸŽã�¸å›½ç¨Žã�¨ã�—ã�¦10%ç´�ç¨Žã�™ã‚‹
	 * 
	 * @param orderList
	 */
	private void payCastleTax(L1ShopBuyOrderList orderList) {
		L1TaxCalculator calc = orderList.getTaxCalculator();

		int price = orderList.getTotalPrice();

		int castleId = L1CastleLocation.getCastleIdByNpcid(_npcId);
		int castleTax = calc.calcCastleTaxPrice(price);
		int nationalTax = calc.calcNationalTaxPrice(price);
		// ã‚¢ãƒ‡ãƒ³åŸŽãƒ»ãƒ‡ã‚£ã‚¢ãƒ‰åŸŽã�®å ´å�ˆã�¯å›½ç¨Žã�ªã�—
		if ((castleId == L1CastleLocation.ADEN_CASTLE_ID)
				|| (castleId == L1CastleLocation.DIAD_CASTLE_ID)) {
			castleTax += nationalTax;
			nationalTax = 0;
		}

		if ((castleId != 0) && (castleTax > 0)) {
			L1Castle castle = CastleTable.getInstance()
					.getCastleTable(castleId);

			synchronized (castle) {
				int money = castle.getPublicMoney();
				if (2000000000 > money) {
					money = money + castleTax;
					castle.setPublicMoney(money);
					CastleTable.getInstance().updateCastle(castle);
				}
			}

			if (nationalTax > 0) {
				L1Castle aden = CastleTable.getInstance().getCastleTable(
						L1CastleLocation.ADEN_CASTLE_ID);
				synchronized (aden) {
					int money = aden.getPublicMoney();
					if (2000000000 > money) {
						money = money + nationalTax;
						aden.setPublicMoney(money);
						CastleTable.getInstance().updateCastle(aden);
					}
				}
			}
		}
	}

	/**
	 * ãƒ‡ã‚£ã‚¢ãƒ‰ç¨Žç´�ç¨Žå‡¦ç�† æˆ¦äº‰ç¨Žã�®10%ã�Œãƒ‡ã‚£ã‚¢ãƒ‰è¦�å¡žã�®å…¬é‡‘ã�¨ã�ªã‚‹ã€‚
	 * 
	 * @param orderList
	 */
	private void payDiadTax(L1ShopBuyOrderList orderList) {
		L1TaxCalculator calc = orderList.getTaxCalculator();

		int price = orderList.getTotalPrice();

		// ãƒ‡ã‚£ã‚¢ãƒ‰ç¨Ž
		int diadTax = calc.calcDiadTaxPrice(price);
		if (diadTax <= 0) {
			return;
		}

		L1Castle castle = CastleTable.getInstance().getCastleTable(
				L1CastleLocation.DIAD_CASTLE_ID);
		synchronized (castle) {
			int money = castle.getPublicMoney();
			if (2000000000 > money) {
				money = money + diadTax;
				castle.setPublicMoney(money);
				CastleTable.getInstance().updateCastle(castle);
			}
		}
	}

	/**
	 * ç”ºç¨Žç´�ç¨Žå‡¦ç�†
	 * 
	 * @param orderList
	 */
	private void payTownTax(L1ShopBuyOrderList orderList) {
		int price = orderList.getTotalPrice();

		// ç”ºã�®å£²ä¸Š
		if (!L1World.getInstance().isProcessingContributionTotal()) {
			int town_id = L1TownLocation.getTownIdByNpcid(_npcId);
			if ((town_id >= 1) && (town_id <= 10)) {
				TownTable.getInstance().addSalesMoney(town_id, price);
			}
		}
	}

	// XXX ç´�ç¨Žå‡¦ç�†ã�¯ã�“ã�®ã‚¯ãƒ©ã‚¹ã�®è²¬å‹™ã�§ã�¯ç„¡ã�„æ°—ã�Œã�™ã‚‹ã�Œã€�ã�¨ã‚Šã�‚ã�ˆã�š
	private void payTax(L1ShopBuyOrderList orderList) {
		payCastleTax(orderList);
		payTownTax(orderList);
		payDiadTax(orderList);
	}
	
	// Buying using Arena Token - [Hank]
	private void sellItemsToken(L1PcInventory inv, L1ShopBuyOrderList orderList, L1PcInstance pc)
	{
		L1ItemInstance pcitem = pc.getInventory().findItemId(260000);
		if (!inv.consumeItem(L1ItemId.ARENA_TOKEN,
				orderList.getTotalPriceTaxIncluded())) {
			throw new IllegalStateException("Arena Token Buying Error");
		}
		
		for (L1ShopBuyOrder order : orderList.getList()) {
			int itemId = order.getItem().getItemId();
			int amount = order.getCount();
			L1ItemInstance item = ItemTable.getInstance().createItem(itemId);
			item.setCount(amount);
			item.setIdentified(true);
			inv.storeItem(item);
		}
	}

	/**
	 * è²©å£²å�–å¼•
	 */
	private void sellItems(L1PcInventory inv, L1ShopBuyOrderList orderList, L1PcInstance pc) {
		int adenabefore = 0;
		int adenaafter = 0;
		
		L1ItemInstance pcitem = pc.getInventory().findItemId(40308);
		if (pcitem != null) {
			adenabefore = pcitem.getCount();
		}
		
		if (!inv.consumeItem(L1ItemId.ADENA,
				orderList.getTotalPriceTaxIncluded())) {
			throw new IllegalStateException("è³¼å…¥ã�«å¿…è¦�ã�ªã‚¢ãƒ‡ãƒŠã‚’æ¶ˆè²»ã�§ã��ã�¾ã�›ã‚“ã�§ã�—ã�Ÿã€‚");
		}
		if (pcitem != null) {
			adenaafter = pcitem.getCount();
		}
		for (L1ShopBuyOrder order : orderList.getList()) {
			int itemId = order.getItem().getItemId();
			int amount = order.getCount();
			L1ItemInstance item = ItemTable.getInstance().createItem(itemId);
			if (item.getItemId() == 40309) {// Race Tickets
				item.setItem(order.getItem().getItem());
				L1BugBearRace.getInstance().setAllBet(
						L1BugBearRace.getInstance().getAllBet()
								+ (amount * order.getItem().getPrice()));
				String[] runNum = item.getItem().getIdentifiedNameId()
						.split("-");
				int trueNum = 0;
				for (int i = 0; i < 5; i++) {
					if (L1BugBearRace.getInstance().getRunner(i).getNpcId() - 91350 == (Integer
							.parseInt(runNum[runNum.length - 1]) - 1)) {
						trueNum = i;
						break;
					}
				}
				L1BugBearRace.getInstance().setBetCount(
						trueNum,
						L1BugBearRace.getInstance().getBetCount(trueNum)
								+ amount);
			}
			item.setCount(amount);
			item.setIdentified(true);
			inv.storeItem(item);
			if ((_npcId == 70068) || (_npcId == 70020)) {
				item.setIdentified(false);
				int chance = Random.nextInt(100) + 1;
				if (chance <= 15) {
					item.setEnchantLevel(-2);
				} else if ((chance >= 16) && (chance <= 30)) {
					item.setEnchantLevel(-1);
				} else if ((chance >= 31) && (chance <= 70)) {
					item.setEnchantLevel(0);
				} else if ((chance >= 71) && (chance <= 87)) {
					item.setEnchantLevel(Random.nextInt(2) + 1);
				} else if ((chance >= 88) && (chance <= 97)) {
					item.setEnchantLevel(Random.nextInt(3) + 3);
				} else if ((chance >= 98) && (chance <= 99)) {
					item.setEnchantLevel(6);
				} else if (chance == 100) {
					item.setEnchantLevel(7);
				}
			}
			LogShopBuy lsb = new LogShopBuy();
			try {
			lsb.storeLogShopBuy(pc, item, amount, adenabefore, adenaafter,orderList.getTotalPriceTaxIncluded() );
			} catch (Exception e) {
				System.out.println("Problem with storeLogShopBuy");
				System.out.println(e);
			}
		}
	}

	/**
	 * ãƒ—ãƒ¬ã‚¤ãƒ¤ãƒ¼ã�«ã€�L1ShopBuyOrderListã�«è¨˜è¼‰ã�•ã‚Œã�Ÿã‚¢ã‚¤ãƒ†ãƒ ã‚’è²©å£²ã�™ã‚‹ã€‚
	 * 
	 * @param pc
	 *            è²©å£²ã�™ã‚‹ãƒ—ãƒ¬ã‚¤ãƒ¤ãƒ¼
	 * @param orderList
	 *            è²©å£²ã�™ã�¹ã��ã‚¢ã‚¤ãƒ†ãƒ ã�Œè¨˜è¼‰ã�•ã‚Œã�ŸL1ShopBuyOrderList
	 */
	public void sellItems(L1PcInstance pc, L1ShopBuyOrderList orderList) {
		if(_npcId == 100053)
		{
			if(!ensureSellToken(pc, orderList))
			{
				return;
			}
			sellItemsToken(pc.getInventory(), orderList, pc);
		}
		else{
			if (!ensureSell(pc, orderList)) {
				return;
			}	
			else{
				sellItems(pc.getInventory(), orderList, pc);
				payTax(orderList);
			}
		}


	}

	/**
	 * L1ShopSellOrderListã�«è¨˜è¼‰ã�•ã‚Œã�Ÿã‚¢ã‚¤ãƒ†ãƒ ã‚’è²·ã�„å�–ã‚‹ã€‚
	 * 
	 * @param orderList
	 *            è²·ã�„å�–ã‚‹ã�¹ã��ã‚¢ã‚¤ãƒ†ãƒ ã�¨ä¾¡æ ¼ã�Œè¨˜è¼‰ã�•ã‚Œã�ŸL1ShopSellOrderList
	 */
	public void buyItems(L1ShopSellOrderList orderList) {
		LogShopSell lsb = new LogShopSell();
		L1PcInstance pc = orderList.getPc();
		
		L1PcInventory inv = orderList.getPc().getInventory();
		int adenabefore = pc.getInventory().findItemId(40308).getCount();
		int adenaafter = 0;
		int totalPrice = 0;
		for (L1ShopSellOrder order : orderList.getList()) {
			L1ItemInstance sellme = inv.getItem(order.getItem().getTargetId());
			int count = inv.removeItem(order.getItem().getTargetId(),
					order.getCount());
			totalPrice += order.getItem().getAssessedPrice() * count;
			adenaafter = adenabefore + (order.getItem().getAssessedPrice()*count);
			try {
			lsb.storeLogShopSell(pc, sellme, adenabefore, adenaafter,order.getItem().getAssessedPrice()*count);
			} catch (Exception e) {
				System.out.println("Problem with storeLogShopSell");
				System.out.println(e);
			}
			adenabefore = adenaafter;
		}

		totalPrice = IntRange.ensure(totalPrice, 0, 2000000000);
		if (0 < totalPrice) {
			inv.storeItem(L1ItemId.ADENA, totalPrice);
		}
	}

	public L1ShopBuyOrderList newBuyOrderList() {
		return new L1ShopBuyOrderList(this);
	}

	public L1ShopSellOrderList newSellOrderList(L1PcInstance pc) {
		return new L1ShopSellOrderList(this, pc);
	}
}
