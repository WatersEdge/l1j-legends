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

import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import l1j.server.server.utils.Random;

import l1j.server.server.model.Instance.L1EffectInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.types.Point;
import static l1j.server.server.model.skill.L1SkillId.*;

public class HpRegeneration extends TimerTask {

	private static final Logger _log = Logger.getLogger(HpRegeneration.class
			.getName());

	private final L1PcInstance _pc;

	private int _regenMax = 0;

	private int _regenPoint = 0;

	private int _curPoint = 4;

	public HpRegeneration(L1PcInstance pc) {
		_pc = pc;

		updateLevel();
	}

	public void setState(int state) {
		if (_curPoint < state) {
			return;
		}

		_curPoint = state;
	}

	@Override
	public void run() {
		try {
			if (_pc.isDead()) {
				return;
			}

			_regenPoint += _curPoint;
			_curPoint = 4;

			synchronized (this) {
				if (_regenMax <= _regenPoint) {
					_regenPoint = 0;
					regenHp();
				}
			}
		} catch (Throwable e) {
			_log.log(Level.WARNING, e.getLocalizedMessage(), e);
		}
	}

	public void updateLevel() {
		final int lvlTable[] = new int[] { 30, 25, 20, 16, 14, 12, 11, 10, 9,
				3, 2 };

		int regenLvl = Math.min(10, _pc.getLevel());
		if (30 <= _pc.getLevel() && _pc.isKnight()) {
			regenLvl = 11;
		}

		synchronized (this) {
			_regenMax = lvlTable[regenLvl - 1] * 4;
		}
	}

	public void regenHp() {
		if (_pc.isDead()) {
			return;
		}

		int maxBonus = 1;

		// CONãƒœãƒ¼ãƒŠã‚¹
		if (11 < _pc.getLevel() && 14 <= _pc.getCon()) {
			maxBonus = _pc.getCon() - 12;
			if (25 < _pc.getCon()) {
				maxBonus = 14;
			}
		}

		int equipHpr = _pc.getInventory().hpRegenPerTick();
		equipHpr += _pc.getHpr();
		int bonus = Random.nextInt(maxBonus) + 1;

		if (_pc.hasSkillEffect(NATURES_TOUCH)) {
			bonus += 15;
		}
		if (L1HouseLocation.isInHouse(_pc.getX(), _pc.getY(), _pc.getMapId())) {
			bonus += 5;
		}
		if (_pc.getMapId() == 16384 || _pc.getMapId() == 16896
				|| _pc.getMapId() == 17408 || _pc.getMapId() == 17920
				|| _pc.getMapId() == 18432 || _pc.getMapId() == 18944
				|| _pc.getMapId() == 19968 || _pc.getMapId() == 19456
				|| _pc.getMapId() == 20480 || _pc.getMapId() == 20992
				|| _pc.getMapId() == 21504 || _pc.getMapId() == 22016
				|| _pc.getMapId() == 22528 || _pc.getMapId() == 23040
				|| _pc.getMapId() == 23552 || _pc.getMapId() == 24064
				|| _pc.getMapId() == 24576 || _pc.getMapId() == 25088) { // å®¿å±‹
			bonus += 5;
		}
		if ((_pc.getLocation().isInScreen(new Point(33055,32336))
				&& _pc.getMapId() == 4 && _pc.isElf())) {
			bonus += 5;
		}
 		if (_pc.hasSkillEffect(COOKING_1_5_N)
				|| _pc.hasSkillEffect(COOKING_1_5_S)) {
			bonus += 3;
		}
 		if (_pc.hasSkillEffect(COOKING_2_4_N)
				|| _pc.hasSkillEffect(COOKING_2_4_S)
				|| _pc.hasSkillEffect(COOKING_3_6_N)
				|| _pc.hasSkillEffect(COOKING_3_6_S)) {
			bonus += 2;
		}
 		if (_pc.getOriginalHpr() > 0) { // ã‚ªãƒªã‚¸ãƒŠãƒ«CON HPRè£œæ­£
 			bonus += _pc.getOriginalHpr();
 		}

		boolean inLifeStream = false;
		if (isPlayerInLifeStream(_pc)) {
			inLifeStream = true;
			// å�¤ä»£ã�®ç©ºé–“ã€�é­”æ—�ã�®ç¥žæ®¿ã�§ã�¯HPR+3ã�¯ã�ªã��ã�ªã‚‹ï¼Ÿ
			bonus += 3;
		}

		// ç©ºè…¹ã�¨é‡�é‡�ã�®ãƒ�ã‚§ãƒƒã‚¯
		if (_pc.get_food() < 3 || isOverWeight(_pc)
				|| _pc.hasSkillEffect(BERSERKERS)) {
			bonus = 0;
			// è£…å‚™ã�«ã‚ˆã‚‹ï¼¨ï¼°ï¼²å¢—åŠ ã�¯æº€è…¹åº¦ã€�é‡�é‡�ã�«ã‚ˆã�£ã�¦ã�ªã��ã�ªã‚‹ã�Œã€� æ¸›å°‘ã�§ã�‚ã‚‹å ´å�ˆã�¯æº€è…¹åº¦ã€�é‡�é‡�ã�«é–¢ä¿‚ã�ªã��åŠ¹æžœã�Œæ®‹ã‚‹
			if (equipHpr > 0) {
				equipHpr = 0;
			}
		}
		
		// fixes the low con DE negative regen.
		int conHp = _pc.getCon() - 10;
		
		if (conHp < 0) 
		{
			conHp = 1;
		}

		int newHp = _pc.getCurrentHp();
		
		// adding hpr variable, if player is overeighted, hpr = 0 - [Hank]
		int hpr = bonus + equipHpr + conHp;
		if(isOverWeight(_pc))
		{
			hpr = 0;
		}
		newHp += hpr;

		if (newHp < 1) {
			newHp = 1; // ï¼¨ï¼°ï¼²æ¸›å°‘è£…å‚™ã�«ã‚ˆã�£ã�¦æ­»äº¡ã�¯ã�—ã�ªã�„
		}
		// æ°´ä¸­ã�§ã�®æ¸›å°‘å‡¦ç�†
		// ãƒ©ã‚¤ãƒ•ã‚¹ãƒˆãƒªãƒ¼ãƒ ã�§æ¸›å°‘ã‚’ã�ªã��ã�›ã‚‹ã�‹ä¸�æ˜Ž
		if (isUnderwater(_pc)) {
			newHp -= 20;
			if (newHp < 1) {
				if (_pc.isGm()) {
					newHp = 1;
				} else {
					_pc.death(null); // çª’æ�¯ã�«ã‚ˆã�£ã�¦ï¼¨ï¼°ã�Œï¼�ã�«ã�ªã�£ã�Ÿå ´å�ˆã�¯æ­»äº¡ã�™ã‚‹ã€‚
				}
			}
		}
		// Lv50ã‚¯ã‚¨ã‚¹ãƒˆã�®å�¤ä»£ã�®ç©ºé–“1F2Fã�§ã�®æ¸›å°‘å‡¦ç�†
		if (isLv50Quest(_pc) && !inLifeStream) {
			newHp -= 10;
			if (newHp < 1) {
				if (_pc.isGm()) {
					newHp = 1;
				} else {
					_pc.death(null); // ï¼¨ï¼°ã�Œï¼�ã�«ã�ªã�£ã�Ÿå ´å�ˆã�¯æ­»äº¡ã�™ã‚‹ã€‚
				}
			}
		}
		// é­”æ—�ã�®ç¥žæ®¿ã�§ã�®æ¸›å°‘å‡¦ç�†
		if (_pc.getMapId() == 410 && !inLifeStream) {
			newHp -= 10;
			if (newHp < 1) {
				if (_pc.isGm()) {
					newHp = 1;
				} else {
					_pc.death(null); // ï¼¨ï¼°ã�Œï¼�ã�«ã�ªã�£ã�Ÿå ´å�ˆã�¯æ­»äº¡ã�™ã‚‹ã€‚
				}
			}
		}

		if (!_pc.isDead()) {
			_pc.setCurrentHp(Math.min(newHp, _pc.getMaxHp()));
		}
	}

	private boolean isUnderwater(L1PcInstance pc) {
		// ã‚¦ã‚©ãƒ¼ã‚¿ãƒ¼ãƒ–ãƒ¼ãƒ„è£…å‚™æ™‚ã�‹ã€� ã‚¨ãƒ´ã‚¡ã�®ç¥�ç¦�çŠ¶æ…‹ã€�ä¿®ç�†ã�•ã‚Œã�Ÿè£…å‚™ã‚»ãƒƒãƒˆã�§ã�‚ã‚Œã�°æ°´ä¸­ã�§ã�¯ç„¡ã�„ã�¨ã�¿ã�ªã�™ã€‚
		// added fafurion elite armor series
		if (pc.getInventory().checkEquipped(20207)||
			pc.getInventory().checkEquipped(21119)|| 
			pc.getInventory().checkEquipped(21120)|| 
			pc.getInventory().checkEquipped(21121)|| 
			pc.getInventory().checkEquipped(21122)) {
			return false;
		}
		if (pc.hasSkillEffect(STATUS_UNDERWATER_BREATH)) {
			return false;
		}
		if (pc.getInventory().checkEquipped(21048)
				&& pc.getInventory().checkEquipped(21049)
				&& pc.getInventory().checkEquipped(21050)) {
			return false;
		}
        return false;
		//return pc.getMap().isUnderwater(); [Legends] disabling underwater
	}

	private boolean isOverWeight(L1PcInstance pc) {
		// ã‚¨ã‚­ã‚¾ãƒ�ãƒƒã‚¯ãƒ�ã‚¤ã‚¿ãƒ©ã‚¤ã‚ºçŠ¶æ…‹ã€�ã‚¢ãƒ‡ã‚£ã‚·ãƒ§ãƒŠãƒ«ãƒ•ã‚¡ã‚¤ã‚¢ãƒ¼çŠ¶æ…‹ã�‹
		// ã‚´ãƒ¼ãƒ«ãƒ‡ãƒ³ã‚¦ã‚£ãƒ³ã‚°è£…å‚™æ™‚ã�§ã�‚ã‚Œã�°ã€�é‡�é‡�ã‚ªãƒ¼ãƒ�ãƒ¼ã�§ã�¯ç„¡ã�„ã�¨ã�¿ã�ªã�™ã€‚
		if (pc.hasSkillEffect(EXOTIC_VITALIZE)
				|| pc.hasSkillEffect(ADDITIONAL_FIRE)) {
			return false;
		}
		if (pc.getInventory().checkEquipped(20049)) {
			return false;
		}

		return (121 <= pc.getInventory().getWeight242()) ? true : false;
	}

	private boolean isLv50Quest(L1PcInstance pc) {
		int mapId = pc.getMapId();
		return (mapId == 2000 || mapId == 2001) ? true : false;
	}

	/**
	 * æŒ‡å®šã�—ã�ŸPCã�Œãƒ©ã‚¤ãƒ•ã‚¹ãƒˆãƒªãƒ¼ãƒ ã�®ç¯„å›²å†…ã�«ã�„ã‚‹ã�‹ãƒ�ã‚§ãƒƒã‚¯ã�™ã‚‹
	 * 
	 * @param pc
	 *            PC
	 * @return true PCã�Œãƒ©ã‚¤ãƒ•ã‚¹ãƒˆãƒªãƒ¼ãƒ ã�®ç¯„å›²å†…ã�«ã�„ã‚‹å ´å�ˆ
	 */
	private static boolean isPlayerInLifeStream(L1PcInstance pc) {
		for (L1Object object : pc.getKnownObjects()) {
			if (object instanceof L1EffectInstance == false) {
				continue;
			}
			L1EffectInstance effect = (L1EffectInstance) object;
			if (effect.getNpcId() == 81169 && effect.getLocation()
					.getTileLineDistance(pc.getLocation()) < 4) {
				return true;
			}
		}
		return false;
	}
}
