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

import static l1j.server.server.model.skill.L1SkillId.AWAKEN_ANTHARAS;
import static l1j.server.server.model.skill.L1SkillId.AWAKEN_FAFURION;
import static l1j.server.server.model.skill.L1SkillId.AWAKEN_VALAKAS;
import static l1j.server.server.model.skill.L1SkillId.SHAPE_CHANGE;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.*;

// Referenced classes of package l1j.server.server.model:
// L1Cooking

public class L1Awake {
	private L1Awake() {
	}

	public static void start(L1PcInstance pc, int skillId) {
        if (skillId == pc.getAwakeSkillId()) {
            stop(pc);
        }
        else if (pc.getAwakeSkillId() != 0) {
            return;
        }
        else
        {
            if (skillId == AWAKEN_ANTHARAS) {
                pc.addMaxHp(100);
                pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
                if (pc.isInParty()) { // çµ„éšŠä¸­
                    pc.getParty().updateMiniHP(pc);
                }
                pc.addAc(-8);
                pc.sendPackets(new S_OwnCharStatus2(pc));
                pc.setAwakeSkillId(skillId);
            }
            else if (skillId == AWAKEN_FAFURION) {
                pc.addMr(15);
                pc.sendPackets(new S_SPMR(pc));
                pc.addWind(15);
                pc.addWater(15);
                pc.addFire(15);
                pc.addEarth(15);
                pc.sendPackets(new S_OwnCharAttrDef(pc));
                pc.setAwakeSkillId(skillId);
            }
            else if (skillId == AWAKEN_VALAKAS) {
                pc.addStr(3);
                pc.addCon(3);
                pc.addDex(3);
                pc.addCha(3);
                pc.addInt(3);
                pc.addWis(3);
                pc.sendPackets(new S_OwnCharStatus2(pc));
                pc.setAwakeSkillId(skillId);
            }
            pc.startMpReductionByAwake();
        }

	}

	public static void stop(L1PcInstance pc) {
		int skillId = pc.getAwakeSkillId();
		if (skillId == AWAKEN_ANTHARAS) { // è¦ºé†’ï¼šå®‰å¡”ç‘žæ–¯
			pc.addMaxHp(-100);
			pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
			if (pc.isInParty()) { // ãƒ‘ãƒ¼ãƒ†ã‚£ãƒ¼ä¸­
				pc.getParty().updateMiniHP(pc);
			}
			pc.addAc(8);
			pc.sendPackets(new S_OwnCharAttrDef(pc));
		}
		else if (skillId == AWAKEN_FAFURION) { // è¦ºé†’ï¼šæ³•åŠ›æ˜‚
			pc.addMr(-15);
			pc.addWind(-15);
			pc.addWater(-15);
			pc.addFire(-15);
			pc.addEarth(-15);
			pc.sendPackets(new S_SPMR(pc));
			pc.sendPackets(new S_OwnCharAttrDef(pc));
		}
		else if (skillId == AWAKEN_VALAKAS) { // è¦ºé†’ï¼šå·´æ‹‰å�¡æ–¯
			pc.addStr(-3);
			pc.addCon(-3);
			pc.addDex(-3);
			pc.addCha(-3);
			pc.addInt(-3);
			pc.addWis(-3);
			pc.sendPackets(new S_OwnCharStatus2(pc));
		}
		pc.setAwakeSkillId(0);
		//undoPoly(pc);
		pc.stopMpReductionByAwake();

	}

	// è®Šèº«
	public static void doPoly(L1PcInstance pc) {
		int polyId = 6894;
		if (pc.hasSkillEffect(SHAPE_CHANGE)) {
			pc.killSkillEffectTimer(SHAPE_CHANGE);
		}
		L1ItemInstance weapon = pc.getWeapon();
		boolean weaponTakeoff = (weapon != null && !L1PolyMorph.isEquipableWeapon(polyId, weapon.getItem().getType()));
		if (weaponTakeoff) { // è§£é™¤æ­¦å™¨æ™‚
			pc.setCurrentWeapon(0);
		}
		pc.setTempCharGfx(polyId);
		pc.sendPackets(new S_ChangeShape(pc.getId(), polyId, pc.getCurrentWeapon()));
		if (pc.isGmInvis()) { // GMéš±èº«
		} else if (pc.isInvisble()) { // ä¸€èˆ¬éš±èº«
			pc.broadcastPacketForFindInvis(new S_ChangeShape(pc.getId(), polyId, pc.getCurrentWeapon()), true);
		} else {
			pc.broadcastPacket(new S_ChangeShape(pc.getId(), polyId, pc.getCurrentWeapon()));
		}
		pc.getInventory().takeoffEquip(polyId); // æ˜¯å�¦å°‡è£�å‚™çš„æ­¦å™¨å¼·åˆ¶è§£é™¤ã€‚
	}

	// è§£é™¤è®Šèº«
	public static void undoPoly(L1PcInstance pc) {
		int classId = pc.getClassId();
		pc.setTempCharGfx(classId);
		if (!pc.isDead()) {
			pc.sendPackets(new S_ChangeShape(pc.getId(), classId, pc.getCurrentWeapon()));
			pc.broadcastPacket(new S_ChangeShape(pc.getId(), classId, pc.getCurrentWeapon()));
		}
	}

}
