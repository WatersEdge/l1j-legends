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
package l1j.server.server.model.skill;

import static l1j.server.server.model.skill.L1SkillId.*;

import java.util.concurrent.ScheduledFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.Config_Einhasad;
import l1j.server.server.GeneralThreadPool;
import l1j.server.server.datatables.SkillsTable;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1EffectSpawn;
import l1j.server.server.model.L1PolyMorph;
import l1j.server.server.model.Instance.L1MonsterInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.model.Instance.L1SummonInstance;
import l1j.server.server.serverpackets.S_CurseBlind;
import l1j.server.server.serverpackets.S_Dexup;
import l1j.server.server.serverpackets.S_HPUpdate;
import l1j.server.server.serverpackets.S_Liquor;
import l1j.server.server.serverpackets.S_MPUpdate;
import l1j.server.server.serverpackets.S_OwnCharAttrDef;
import l1j.server.server.serverpackets.S_OwnCharStatus;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_Paralysis;
import l1j.server.server.serverpackets.S_Poison;
import l1j.server.server.serverpackets.S_SPMR;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SkillBrave;
import l1j.server.server.serverpackets.S_SkillHaste;
import l1j.server.server.serverpackets.S_SkillIconAura;
import l1j.server.server.serverpackets.S_SkillIconBlessOfEva;
import l1j.server.server.serverpackets.S_SkillIconBloodstain;
import l1j.server.server.serverpackets.S_SkillIconShield;
import l1j.server.server.serverpackets.S_SkillIconWindShackle;
import l1j.server.server.serverpackets.S_SkillIconWisdomPotion;
import l1j.server.server.serverpackets.S_Strup;
import l1j.server.server.templates.L1Skills;

public interface L1SkillTimer {
	public int getRemainingTime();

	public void begin();

	public void end();

	public void kill();
}

/*
 * XXX 2008/02/13 vala ï¿½î¯±ï¿½î²‡ï¿½î¼¹ï¿½îš£î¼„ï¿½î¾¢ï¿½ï€¹ï¿½î¾¬ï¿½î¼…ï¿½ï¿½î¿–ï¿½ï•›î¼�ï¿½ï¿½ïš•î»½ï¿½î¼…ï¿½î¼€ï¿½ï¿½ï„�ï¿½ï—»îª¶æ‘°î«°ïŠªè�µæŸ´ï¿½ï¿½
 */
class L1SkillStop {
	public static void stopSkill(L1Character cha, int skillId) {
		if (skillId == LIGHT) { // ï¿½ï€¹ï¿½î¾—ï¿½ï¿½ï¿½
			if (cha instanceof L1PcInstance) {
				if (!cha.isInvisble()) {
					L1PcInstance pc = (L1PcInstance) cha;
					pc.turnOnOffLight();
				}
			}
		}
		else if (skillId == GLOWING_AURA) { // ï¿½î¾£ï¿½ï€½ï¿½ï�Œï¿½î¾™ï¿½î¾–ï¿½ï�ƒï¿½î¾£ ï¿½î¾�ï¿½ï�Œï¿½ï€¹
			cha.addHitup(-5);
			cha.addBowHitup(-5);
			cha.addMr(-20);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SPMR(pc));
				pc.sendPackets(new S_SkillIconAura(113, 0));
			}
		}
		else if (skillId == SHINING_AURA) { // ï¿½î¾ªï¿½ï€³ï¿½î¾—ï¿½ï¿½ï•›ï�ƒï¿½î¾£ ï¿½î¾�ï¿½ï�Œï¿½ï€¹
			cha.addAc(8);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillIconAura(114, 0));
			}
		}
		else if (skillId == BRAVE_AURA) { // ï¿½ï¿½î¡ºï€¼ï¿½î¾—ï¿½ï¿½ï¿½ ï¿½î¾�ï¿½ï�Œï¿½ï€¹
			cha.addDmgup(-5);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillIconAura(116, 0));
			}
		}
		else if (skillId == SHIELD) { // ï¿½î¾ªï¿½ï�Œï¿½ï€»ï¿½ï¿½ï¿½
			cha.addAc(2);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillIconShield(5, 0));
			}
		}
		else if (skillId == BLIND_HIDING) { // ï¿½ï¿½î¡ºï€¹ï¿½î¾—ï¿½ï�ƒï¿½ï¿½ï�¡ï¿½î�¯î¾—ï¿½ï¿½ï‹§î¾–ï¿½ï�ƒï¿½î¾£
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.delBlindHiding();
			}
		}
		else if (skillId == SHADOW_ARMOR) { // ï¿½î¾ªï¿½ï€³ï¿½ï¿½ï�¡î¾™ ï¿½î¾•ï¿½ï�Œï¿½ï¿½îµ¢ï�Œ
			cha.addAc(3);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillIconShield(3, 0));
			}
		}
		else if (skillId == DRESS_DEXTERITY) { // ï¿½ï¿½ï�¡ï€¼ï¿½î¾¬ ï¿½ï¿½ï‹§î¾¢ï¿½î¾¬ï¿½î¾²ï¿½ï€ºï¿½ï¿½ï‰Šî¾–ï¿½ï�Œ
			cha.addDex((byte) -2);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_Dexup(pc, 2, 0));
			}
		}
		else if (skillId == DRESS_MIGHTY) { // ï¿½ï¿½ï�¡ï€¼ï¿½î¾¬ ï¿½ï¿½îµ¢î¾—ï¿½ï¿½ï‰Šî¾–ï¿½ï�Œ
			cha.addStr((byte) -2);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_Strup(pc, 2, 0));
			}
		}
		else if (skillId == SHADOW_FANG) { // ï¿½î¾ªï¿½ï€³ï¿½ï¿½ï�¡î¾™ ï¿½ï¿½îŸ�î¾”ï¿½ï�ƒï¿½î¾£
			cha.addDmgup(-5);
		}
		else if (skillId == ENCHANT_WEAPON) { // ï¿½î¾›ï¿½ï�ƒï¿½ï¿½î¼¹ï€³ï¿½ï�ƒï¿½ï¿½ï¿½ ï¿½î¾™ï¿½î¾šï¿½ï¿½î³…ï�ƒ
			cha.addDmgup(-2);
		}
		else if (skillId == BLESSED_ARMOR) { // ï¿½ï¿½î¡ºï€¼ï¿½î¾¬ï¿½ï¿½ï¿½ ï¿½î¾•ï¿½ï�Œï¿½ï¿½îµ¢ï�Œ
			cha.addAc(3);
		}
		else if (skillId == EARTH_BLESS) { // ï¿½î¾•ï¿½ï�Œï¿½î¾¬ ï¿½ï¿½î¡ºï€¼ï¿½î¾¬
			cha.addAc(7);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillIconShield(7, 0));
			}
		}
        // [Legends] - Add in new armor break skill check
        else if(skillId == ARMOR_BREAK)
        {
            cha.removeSkillEffect(ARM_BREAKER);
        }
		else if (skillId == RESIST_MAGIC) { // ï¿½ï€¼ï¿½î¾«ï¿½î¾¬ï¿½ï¿½ï¿½ ï¿½ï¿½îµ¢î¾«ï¿½ï¿½ï�³î¾¢
			cha.addMr(-10);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SPMR(pc));
			}
		}
		else if (skillId == CLEAR_MIND) { // ï¿½î¾¢ï¿½ï€ºï¿½î¾•ï¿½ï�Œ ï¿½ï¿½îµ¢î¾—ï¿½ï�ƒï¿½ï¿½ï¿½
			cha.addWis((byte) -3);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.resetBaseMr();
			}
		}
		else if (skillId == RESIST_ELEMENTAL) { // ï¿½ï€¼ï¿½î¾«ï¿½î¾¬ï¿½ï¿½ï¿½ ï¿½î¾›ï¿½ï€¼ï¿½ï€±ï¿½ï�ƒï¿½ï¿½ï¿½
			cha.addWind(-10);
			cha.addWater(-10);
			cha.addFire(-10);
			cha.addEarth(-10);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_OwnCharAttrDef(pc));
			}
		}
		else if (skillId == ELEMENTAL_PROTECTION) { // ï¿½î¾›ï¿½ï€¼ï¿½ï€±ï¿½ï�ƒï¿½î¾²ï¿½ï€»ï¿½ï¿½î¤—ï€½ï¿½ï¿½ï‰Šî¾¢ï¿½î¾ªï¿½ï€·ï¿½ï�ƒ
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				int attr = pc.getElfAttr();
				if (attr == 1) {
					cha.addEarth(-50);
				} else if (attr == 2) {
					cha.addFire(-50);
				} else if (attr == 4) {
					cha.addWater(-50);
				} else if (attr == 8) {
					cha.addWind(-50);
				}
				pc.sendPackets(new S_OwnCharAttrDef(pc));
			}
		}
		else if (skillId == ELEMENTAL_FALL_DOWN) { // æ’˜å‹—ï¿½î¡¼æƒ‡ï¿½ï¿½
			int attr = cha.getAddAttrKind();
			int i = 50;
			switch (attr) {
				case 1:
					cha.addEarth(i);
					break;
				case 2:
					cha.addFire(i);
					break;
				case 4:
					cha.addWater(i);
					break;
				case 8:
					cha.addWind(i);
					break;
				default:
					break;
			}
			cha.setAddAttrKind(0);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_OwnCharAttrDef(pc));
			}
		}
		else if (skillId == IRON_SKIN) { // ï¿½î¾•ï¿½î¾—ï¿½î¾•ï¿½ï�ƒ ï¿½î¾¬ï¿½î¾ ï¿½ï�ƒ
			cha.addAc(10);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillIconShield(10, 0));
			}
		}
		else if (skillId == EARTH_SKIN) { // ï¿½î¾•ï¿½ï�Œï¿½î¾¬ ï¿½î¾¬ï¿½î¾ ï¿½ï�ƒ
			cha.addAc(6);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillIconShield(6, 0));
			}
		}
		else if (skillId == PHYSICAL_ENCHANT_STR) { // ï¿½ï¿½îŸ�î¾–ï¿½î¾«ï¿½î¾žï¿½ï€» ï¿½î¾›ï¿½ï�ƒï¿½ï¿½î¼¹ï€³ï¿½ï�ƒï¿½ï¿½ïŽ�ï¿½îª€TR
			cha.addStr((byte) -5);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_Strup(pc, 5, 0));
			}
		}
		else if (skillId == PHYSICAL_ENCHANT_DEX) { // ï¿½ï¿½îŸ�î¾–ï¿½î¾«ï¿½î¾žï¿½ï€» ï¿½î¾›ï¿½ï�ƒï¿½ï¿½î¼¹ï€³ï¿½ï�ƒï¿½ï¿½ïŽ�ï¿½î©±EX
			cha.addDex((byte) -5);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_Dexup(pc, 5, 0));
			}
		}
		else if (skillId == FIRE_WEAPON) { // ï¿½ï¿½îŸ�î¾”ï¿½î¾—ï¿½î¾•ï¿½ï�Œ ï¿½î¾™ï¿½î¾šï¿½ï¿½î³…ï�ƒ
			cha.addDmgup(-4);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillIconAura(147, 0));
			}
		}
		else if (skillId == FIRE_BLESS) { // ï¿½ï¿½îŸ�î¾”ï¿½î¾—ï¿½î¾•ï¿½ï�Œ ï¿½ï¿½î¡ºï€¼ï¿½î¾¬
			cha.addDmgup(-4);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillIconAura(154, 0));
			}
		}
		else if (skillId == BURNING_WEAPON) { // ï¿½ï¿½î“Œï�Œï¿½ï¿½ï•›ï�ƒï¿½î¾£ ï¿½î¾™ï¿½î¾šï¿½ï¿½î³…ï�ƒ
			cha.addDmgup(-6);
			cha.addHitup(-3);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillIconAura(162, 0));
			}
		}
		else if (skillId == BLESS_WEAPON) { // ï¿½ï¿½î¡ºï€¼ï¿½î¾¬ ï¿½î¾™ï¿½î¾šï¿½ï¿½î³…ï�ƒ
			cha.addDmgup(-2);
			cha.addHitup(-2);
			cha.addBowHitup(-2);
		}
		else if (skillId == WIND_SHOT) { // ï¿½î¾™ï¿½î¾–ï¿½ï�ƒï¿½ï¿½ï¿½ ï¿½î¾ªï¿½ï€·ï¿½ï¿½ï�³ï¿½ï¿½
			cha.addBowHitup(-6);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillIconAura(148, 0));
			}
		}
		else if (skillId == STORM_EYE) { // ï¿½î¾¬ï¿½ï¿½ïŽ„ï�Œï¿½ï¿½ï¿½ ï¿½î¾•ï¿½î¾—
			cha.addBowHitup(-2);
			cha.addBowDmgup(-3);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillIconAura(155, 0));
			}
		}
		else if (skillId == STORM_SHOT) { // ï¿½î¾¬ï¿½ï¿½ïŽ„ï�Œï¿½ï¿½ï¿½ ï¿½î¾ªï¿½ï€·ï¿½ï¿½ï�³ï¿½ï¿½
			cha.addBowDmgup(-5);
			cha.addBowHitup(1);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillIconAura(165, 0));
			}
		}
		else if (skillId == BERSERKERS) { // ï¿½ï¿½î“Œï�Œï¿½î¾¨ï¿½ï�Œï¿½î¾žï¿½ï�Œ
			cha.addAc(-10);
			cha.addDmgup(-5);
			cha.addHitup(-2);
		}
		else if (skillId == SHAPE_CHANGE) { // ï¿½î¾ªï¿½î¾šï¿½î¾—ï¿½ï¿½ï¿½ ï¿½ï¿½î¼¹î¾šï¿½ï�ƒï¿½î¾«
			L1PolyMorph.undoPoly(cha);
		}
		else if (skillId == ADVANCE_SPIRIT) { // ï¿½î¾•ï¿½ï¿½ï�¡ï¿½î“Œï�ƒï¿½î¾¬ï¿½ï¿½ï¿½ ï¿½î¾¬ï¿½ï¿½î�€ï€ºï¿½ï¿½ï�³ï¿½ï¿½
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMaxHp(-pc.getAdvenHp());
				pc.addMaxMp(-pc.getAdvenMp());
				pc.setAdvenHp(0);
				pc.setAdvenMp(0);
				pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
				if (pc.isInParty()) { // ï¿½ï¿½î•©ï�Œï¿½ï¿½ï‰Šî¾–ï¿½ï�ŒéŠ�ï¿½
					pc.getParty().updateMiniHP(pc);
				}
				pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
			}
		}
		else if ((skillId == HASTE) || (skillId == GREATER_HASTE)) { // ï¿½ï¿½î¦´î¾—ï¿½î¾¬ï¿½ï¿½ïŽ„ï¿½î¼¹î¾£ï¿½ï€¼ï¿½ï�Œï¿½î¾²ï¿½ï�Œï¿½ï¿½î¦´î¾—ï¿½î¾¬ï¿½ï¿½ï¿½
			cha.setMoveSpeed(0);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillHaste(pc.getId(), 0, 0));
				pc.broadcastPacket(new S_SkillHaste(pc.getId(), 0, 0));
			}
		}
		else if ((skillId == HOLY_WALK) || (skillId == MOVING_ACCELERATION) || (skillId == WIND_WALK)) { //|| (skillId == BLOODLUST) [Legends] Removed check for bloodlust as it is now a brave.
			cha.setBraveSpeed(0);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillBrave(pc.getId(), 0, 0));
				pc.broadcastPacket(new S_SkillBrave(pc.getId(), 0, 0));
			}
		}
		else if (skillId == ILLUSION_OGRE) { // æ’Ÿé¤‰æ­»åš—î«±ï¿½î“Žï¿½ï¿½
			cha.addDmgup(-4);
			cha.addHitup(-4);
			cha.addBowDmgup(-4);
			cha.addBowHitup(-4);
		}
		else if (skillId == ILLUSION_LICH) { // ï¿½î¾—ï¿½ï€ºï¿½ï€µï¿½ï�Œï¿½î¾«ï¿½ï€·ï¿½ï�ƒåš—î«®ï€ºï¿½ï¿½ï�³ï¿½ï¿½
			cha.addSp(-2);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SPMR(pc));
			}
		}
		else if (skillId == ILLUSION_DIA_GOLEM) { // ï¿½î¾—ï¿½ï€ºï¿½ï€µï¿½ï�Œï¿½î¾«ï¿½ï€·ï¿½ï�ƒåš—î«®ï¿½ï¿½î¾—ï¿½î¾•ï¿½ï€²ï¿½ï�ƒï¿½ï¿½ï�¡î¾§ï¿½ï�Œï¿½ï€¼ï¿½ï¿½ï¿½
			cha.addAc(20);
		}
		else if (skillId == ILLUSION_AVATAR) { // ï¿½î¾—ï¿½ï€ºï¿½ï€µï¿½ï�Œï¿½î¾«ï¿½ï€·ï¿½ï�ƒåš—î«®î¾•ï¿½ï¿½î“Œî¾²ï¿½ï�Œ
			cha.addDmgup(-10);
			cha.addBowDmgup(-10);
		}
		else if (skillId == INSIGHT) { // ç˜£îµ¤ï¿½ï¿½
			cha.addStr((byte) -1);
			cha.addCon((byte) -1);
			cha.addDex((byte) -1);
			cha.addWis((byte) -1);
			cha.addInt((byte) -1);
		}
		else if (skillId == PANIC) { // ï¿½ï¿½î“�ï¿½ï¿½
			cha.addStr((byte) 1);
			cha.addCon((byte) 1);
			cha.addDex((byte) 1);
			cha.addWis((byte) 1);
			cha.addInt((byte) 1);
		}

		// ****** ï¿½ï’‘ï¿½ï¿½ï•�ï¿½ï�£ï¿½î¡ºï¿½ï—½åœ¾ï¿½ï¿½î•©ï¿½î¸�î¹­ï¿½ï¿½ï¿½
		else if ((skillId == CURSE_BLIND) || (skillId == DARKNESS)) {
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_CurseBlind(0));
			}
		}
		else if (skillId == CURSE_PARALYZE) { // ï¿½î¾žï¿½ï�Œï¿½î¾­ ï¿½ï¿½î•©ï€¹ï¿½ï€¹ï¿½î¾—ï¿½î¾­
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_Poison(pc.getId(), 0));
				pc.broadcastPacket(new S_Poison(pc.getId(), 0));
				pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_PARALYSIS, false));
			}
		}
		else if (skillId == WEAKNESS) { // æ’˜å‹—ï¿½î¡¿ï¿½ï¿½
			cha.addDmgup(5);
			cha.addHitup(1);
		}
		else if (skillId == DISEASE) { // ï¿½î¡•ï¿½ï¿½ï†²ï¿½ï¿½
			cha.addDmgup(6);
			cha.addAc(-12);
		}
		else if ((skillId == ICE_LANCE // ï¿½î¾•ï¿½î¾—ï¿½î¾¬ï¿½ï€¹ï¿½ï�ƒï¿½î¾¬
				)
				|| (skillId == FREEZING_BLIZZARD // ï¿½ï¿½îŸ�ï€ºï¿½ï�Œï¿½î¾«ï¿½ï�ƒï¿½î¾£ï¿½ï¿½î¡ºï€ºï¿½î¾©ï¿½ï�Œï¿½ï¿½ï¿½
				) || (skillId == FREEZING_BREATH) // ï¿½ï¿½îŸ�ï€ºï¿½ï�Œï¿½î¾«ï¿½ï�ƒï¿½î¾£ï¿½ï¿½î¡ºï€¼ï¿½î¾¬
				|| (skillId == ICE_LANCE_COCKATRICE) // éˆ­îµ¤ï¿½î®�ï¿½ï�£ïˆ—ï¿½ï¿½î®�ï¿½ïš™æƒ‡
				|| (skillId == ICE_LANCE_BASILISK)) { // ï¿½î¾�ï¿½ï€±ï¿½î¯ªï¿½î¯¹ï¿½ïˆ—ï¿½ï¿½î®�ï¿½ïš™æƒ‡
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_Poison(pc.getId(), 0));
				pc.broadcastPacket(new S_Poison(pc.getId(), 0));
				pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_FREEZE, false));
			}
			else if ((cha instanceof L1MonsterInstance) || (cha instanceof L1SummonInstance) || (cha instanceof L1PetInstance)) {
				L1NpcInstance npc = (L1NpcInstance) cha;
				npc.broadcastPacket(new S_Poison(npc.getId(), 0));
				npc.setParalyzed(false);
			}
		}
		else if (skillId == EARTH_BIND) { // ï¿½î¾•ï¿½ï�Œï¿½î¾¬ï¿½ï¿½î“Œî¾—ï¿½ï�ƒï¿½ï¿½ï¿½
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_Poison(pc.getId(), 0));
				pc.broadcastPacket(new S_Poison(pc.getId(), 0));
				pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_FREEZE, false));
			}
			else if ((cha instanceof L1MonsterInstance) || (cha instanceof L1SummonInstance) || (cha instanceof L1PetInstance)) {
				L1NpcInstance npc = (L1NpcInstance) cha;
				npc.broadcastPacket(new S_Poison(npc.getId(), 0));
				npc.setParalyzed(false);
			}
		}
		else if (skillId == SHOCK_STUN || skillId == BONE_BREAK) { // éŠµî³ˆï¿½ï’¿ï¿½ï•žï¿½ï¿½
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_STUN, false));
			} else if ((cha instanceof L1MonsterInstance) || (cha instanceof L1SummonInstance) || (cha instanceof L1PetInstance)) {
				L1NpcInstance npc = (L1NpcInstance) cha;
				npc.setParalyzed(false);
			}
		}
/*		else if (skillId == BONE_BREAK_START) { // æ’‰ç�¿ï¿½î�²ï¿½æ†¯ï¿½ (ï¿½î¨ªï¿½ï¿½ï¿½)
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_STUN, true));
				pc.setSkillEffect(BONE_BREAK_END, 2 * 1000); // 2 second stun
			} else if ((cha instanceof L1MonsterInstance) || (cha instanceof L1SummonInstance) || (cha instanceof L1PetInstance)) {
				L1NpcInstance npc = (L1NpcInstance) cha;
				npc.setParalyzed(true);
				npc.setSkillEffect(BONE_BREAK_END, 2 * 1000);
			}
		}
		else if (skillId == BONE_BREAK_END) { // æ’‰ç�¿ï¿½î�²ï¿½æ†¯ï¿½ (è�¯î“�ï¿½ï¿½)
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_STUN, false));
			} else if ((cha instanceof L1MonsterInstance) || (cha instanceof L1SummonInstance) || (cha instanceof L1PetInstance)) {
				L1NpcInstance npc = (L1NpcInstance) cha;
				npc.setParalyzed(false);
			}
		}*/
		// Make sure phatasm effect get removed when time out - [Hank]
		else if (skillId == FOG_OF_SLEEPING || skillId == PHANTASM) {
			cha.setSleeped(false);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_SLEEP, false));
				pc.sendPackets(new S_OwnCharStatus(pc));
			}
		}
		else if (skillId == ABSOLUTE_BARRIER) { // è�¯îŸŸï¿½ïš—ï¿½î�µï¿½ï¿½
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.startHpRegeneration();
				pc.startMpRegeneration();
				pc.startHpRegenerationByDoll();
				pc.startMpRegenerationByDoll();
			}
		}
		else if (skillId == MEDITATION) { // ï¿½ïˆŒï¿½ï�ƒéŠµï¿½
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMpr(-5);
			}
		}
		else if (skillId == CONCENTRATION) { // æ’ ïŽ‡é‡£
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMpr(-4);
			}
		}
		else if (skillId == WIND_SHACKLE) { // æ†¸å…¶ï¿½ï•žî´¶ï¿½ï¿½ï¿½
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillIconWindShackle(pc.getId(), 0));
				pc.broadcastPacket(new S_SkillIconWindShackle(pc.getId(), 0));
			}
		}
		else if ((skillId == SLOW) || (skillId == ENTANGLE) || (skillId == MASS_SLOW)) { // ï¿½î¾¬ï¿½ï€½ï¿½ï�Œï¿½î¼¹î¾›ï¿½ï�ƒï¿½î¾²ï¿½ï�ƒï¿½î¾£ï¿½ï€»ï¿½î¼¹ï¿½îµ¢î¾¬ï¿½î¾¬ï¿½ï€½ï¿½ï�Œ
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillHaste(pc.getId(), 0, 0));
				pc.broadcastPacket(new S_SkillHaste(pc.getId(), 0, 0));
			}
			cha.setMoveSpeed(0);
		}
		else if (skillId == STATUS_FREEZE) { // ï¿½ï¿½î¸ƒï¿½ï¿½
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_BIND, false));
			}
			else if ((cha instanceof L1MonsterInstance) || (cha instanceof L1SummonInstance) || (cha instanceof L1PetInstance)) {
				L1NpcInstance npc = (L1NpcInstance) cha;
				npc.setParalyzed(false);
			}
		}
		else if (skillId == THUNDER_GRAB_START) {
			L1Skills _skill = SkillsTable.getInstance().getTemplate(THUNDER_GRAB); // æ†ŸèŠ¸î•ƒéŠ‹ï•¡î­Ÿ
			int _fetterDuration = _skill.getBuffDuration() * 1000;
			cha.setSkillEffect(STATUS_FREEZE, _fetterDuration);
			L1EffectSpawn.getInstance().spawnEffect(81182, _fetterDuration, cha.getX(), cha.getY(), cha.getMapId());
		}
		else if (skillId == GUARD_BRAKE) { // éœ…ç‘�ï¿½î®Žï¿½çš›ï¿½
			cha.addAc(-15);
		}
		else if (skillId == HORROR_OF_DEATH) { // æ’½î«±ï¿½î«±é¦™èŸ¡ï¿½
			cha.addStr(3);
			cha.addInt(3);
		}
		else if (skillId == STATUS_CUBE_IGNITION_TO_ALLY) { // ï¿½î¾ ï¿½ï€µï¿½ï�Œï¿½ï¿½î ”ï¿½î¾—ï¿½î¾£ï¿½ï¿½ï•›î¾ªï¿½ï€·ï¿½ï�ƒ]åš—î«°î”¹ï¿½î¡�
			cha.addFire(-30);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_OwnCharAttrDef(pc));
			}
		}
		else if (skillId == STATUS_CUBE_QUAKE_TO_ALLY) { // ï¿½î¾ ï¿½ï€µï¿½ï�Œï¿½ï¿½î ”ï¿½î¾¢ï¿½î¾›ï¿½î¾—ï¿½î¾¢]åš—î«°î”¹ï¿½î¡�
			cha.addEarth(-30);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_OwnCharAttrDef(pc));
			}
		}
		else if (skillId == STATUS_CUBE_SHOCK_TO_ALLY) { // ï¿½î¾ ï¿½ï€µï¿½ï�Œï¿½ï¿½î ”ï¿½î¾ªï¿½ï€·ï¿½ï¿½ï�³î¾¢]åš—î«°î”¹ï¿½î¡�
			cha.addWind(-30);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_OwnCharAttrDef(pc));
			}
		}
		else if (skillId == STATUS_CUBE_IGNITION_TO_ENEMY) { // ï¿½î¾ ï¿½ï€µï¿½ï�Œï¿½ï¿½î ”ï¿½î¾—ï¿½î¾£ï¿½ï¿½ï•›î¾ªï¿½ï€·ï¿½ï�ƒ]åš—î«±îž¯
		}
		else if (skillId == STATUS_CUBE_QUAKE_TO_ENEMY) { // ï¿½î¾ ï¿½ï€µï¿½ï�Œï¿½ï¿½î ”ï¿½î¾¢ï¿½î¾›ï¿½î¾—ï¿½î¾¢]åš—î«±îž¯
		}
		else if (skillId == STATUS_CUBE_SHOCK_TO_ENEMY) { // ï¿½î¾ ï¿½ï€µï¿½ï�Œï¿½ï¿½î ”ï¿½î¾ªï¿½ï€·ï¿½ï¿½ï�³î¾¢]åš—î«±îž¯
		}
		else if (skillId == STATUS_MR_REDUCTION_BY_CUBE_SHOCK) { // ï¿½î¾ ï¿½ï€µï¿½ï�Œï¿½ï¿½î ”ï¿½î¾ªï¿½ï€·ï¿½ï¿½ï�³î¾¢]ï¿½î¼�ï¿½ï¿½ïŽ„ï¿½ï“§Rçšœî®�ï¿½ï¿½
			// cha.addMr(10);
			// if (cha instanceof L1PcInstance) {
			// L1PcInstance pc = (L1PcInstance) cha;
			// pc.sendPackets(new S_SPMR(pc));
			// }
		}
		else if (skillId == STATUS_CUBE_BALANCE) { // ï¿½î¾ ï¿½ï€µï¿½ï�Œï¿½ï¿½î ”ï¿½ï¿½î“Œï€¹ï¿½ï�ƒï¿½î¾¬]
		}

		// ****** ï¿½î¾•ï¿½î¾—ï¿½ï¿½ï‰Šï¿½îº¢î ¹é�½ï¿½
		else if ((skillId == STATUS_BRAVE)
				|| (skillId == STATUS_ELFBRAVE)
				|| (skillId == STATUS_BRAVE2)) { // éˆ­ï—»æŒ¾ï¿½ï¿½îº¢ï¿½ï¿½
			cha.setBraveSpeed(0);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillBrave(pc.getId(), 0, 0));
				pc.broadcastPacket(new S_SkillBrave(pc.getId(), 0, 0));
			}
		}
		else if (skillId == STATUS_THIRD_SPEED) { // éŠ�ï�¤æŒ¾ï¿½ï¿½îº¢ï¿½ï¿½
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_Liquor(pc.getId(), 0)); // éˆ­ç®‡ï�§ * 1.15
				pc.broadcastPacket(new S_Liquor(pc.getId(), 0)); // éˆ­ç®‡ï�§ * 1.15
			}
		}
		/** ï¿½ï¿½î¸�î•ƒéŠ‹ï•žé‚¦ï¿½ï¿½î°ªç¥• */
		/*else if (skillId == STATUS_RIBRAVE) { // ï¿½ï€¶ï¿½î¾£ï¿½ï¿½ï�¡ï€¹ï¿½î¼„æ‘°ï¿½
			// XXX ï¿½ï€¶ï¿½î¾£ï¿½ï¿½ï�¡ï€¹ï¿½î¼„æ‘°î·¿î¼„ï¿½î¾•ï¿½î¾—ï¿½î¾¦ï¿½ï�ƒï¿½ï¿½î˜‰ï¿½ïŽ„ï¿½î©”î¡�ç˜œîŸ�ï¿½ï—¹ï¿½ïš˜ï¿½ï¿½
			cha.setBraveSpeed(0);
		}*/
		else if (skillId == STATUS_HASTE) { // ï¿½î¾£ï¿½ï€ºï¿½ï�Œï¿½ï�ƒ ï¿½ï¿½î³…ï�Œï¿½î¾ªï¿½ï€·ï¿½ï�ƒ
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillHaste(pc.getId(), 0, 0));
				pc.broadcastPacket(new S_SkillHaste(pc.getId(), 0, 0));
			}
			cha.setMoveSpeed(0);
		}
		else if (skillId == STATUS_BLUE_POTION) { // ï¿½ï¿½î¡ºï€»ï¿½ï�Œ ï¿½ï¿½î³…ï�Œï¿½î¾ªï¿½ï€·ï¿½ï�ƒ
		}
		else if (skillId == STATUS_UNDERWATER_BREATH) { // ï¿½î¾›ï¿½ï�„ï¿½î¾”ï¿½î¼„èŸ¡î³‰ï¿½î�»ï¿½ï‰Šï¿½îµ¢ï�Œï¿½ï€±ï¿½î¾—ï¿½ï¿½ï�¡î¼„æ•¼ï¿½
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillIconBlessOfEva(pc.getId(), 0));
			}
		}
		else if (skillId == STATUS_WISDOM_POTION) { // ï¿½î¾™ï¿½î¾–ï¿½î¾­ï¿½ï¿½ï¿½ï¿½ï¿½ ï¿½ï¿½î³…ï�Œï¿½î¾ªï¿½ï€·ï¿½ï�ƒ
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				cha.addSp(-2);
				pc.sendPackets(new S_SkillIconWisdomPotion(0));
			}
		}
		else if (skillId == STATUS_CHAT_PROHIBITED) { // ï¿½ï¿½î¼¹ï€³ï¿½ï¿½ï�³ï¿½ïŽˆï¿½î¼¼è¿«
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_ServerMessage(288)); // ï¿½ï¿½î¼¹ï€³ï¿½ï¿½ï�³ï¿½ïŽ„ï¿½ï—¸î»½ï¿½ï¿½ïš•ï¿½ï•›ï¿½ïŽ„ï¿½ï‰Šî¼�ï¿½î¼€ï¿½ï¿½ï’¾î¼”ï¿½ï¿½î¤—ï¿½î·¿ï¿½ï¿½
			}
		}

		// ****** ç˜¥î˜Œî ¹é�½ï¿½
		else if (skillId == STATUS_POISON) { // ï¿½ï¿½ï¿½ï€±ï¿½ï�Œï¿½î¾«ç˜¥ï¿½
			cha.curePoison();
		}

		// ****** ï¿½ï¿½î©•ï¿½ï‰�î ¹é�½ï¿½
		else if ((skillId == COOKING_1_0_N) || (skillId == COOKING_1_0_S)) { // ï¿½ï¿½îŸ�ï€½ï¿½ï�Œï¿½ï¿½ï‰Šî¾–ï¿½ï�ƒï¿½î¾£ï¿½î¾•ï¿½î¾—ï¿½î¾¬ï¿½ï¿½ï‰Šï�Œï¿½î¾ 
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addWind(-10);
				pc.addWater(-10);
				pc.addFire(-10);
				pc.addEarth(-10);
				pc.sendPackets(new S_OwnCharAttrDef(pc));
				pc.sendPackets(new S_PacketBox(53, 0, 0));
				pc.setCookingId(0);
			}
		}
		else if ((skillId == COOKING_1_1_N) || (skillId == COOKING_1_1_S)) { // ï¿½ï¿½î©‘î¾•ï¿½ï�Œï¿½î¾¬ï¿½ï¿½ï‰Šï�Œï¿½î¾ 
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMaxHp(-30);
				pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
				if (pc.isInParty()) { // ï¿½ï¿½î•©ï�Œï¿½ï¿½ï‰Šî¾–ï¿½ï�ŒéŠ�ï¿½
					pc.getParty().updateMiniHP(pc);
				}
				pc.sendPackets(new S_PacketBox(53, 1, 0));
				pc.setCookingId(0);
			}
		}
		else if ((skillId == COOKING_1_2_N) || (skillId == COOKING_1_2_S)) { // ï¿½ï¿½ï’¾ï¿½ï�³ï¿½ï„–ï¿½ï¿½
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_PacketBox(53, 2, 0));
				pc.setCookingId(0);
			}
		}
		else if ((skillId == COOKING_1_3_N) || (skillId == COOKING_1_3_S)) { // ï¿½î·—ï¿½ï¿½î«®î¼„ï¿½ï¿½î¼¹ï�Œï¿½î¾­ï¿½ïƒ©ï¿½ï¿½ï¿½
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addAc(1);
				pc.sendPackets(new S_PacketBox(53, 3, 0));
				pc.setCookingId(0);
			}
		}
		else if ((skillId == COOKING_1_4_N) || (skillId == COOKING_1_4_S)) { // ï¿½ï¿½îŸ�ï€»ï¿½ï�Œï¿½ï¿½ï„�î¾¨ï¿½ï€¹ï¿½ï¿½
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMaxMp(-20);
				pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
				pc.sendPackets(new S_PacketBox(53, 4, 0));
				pc.setCookingId(0);
			}
		}
		else if ((skillId == COOKING_1_5_N) || (skillId == COOKING_1_5_S)) { // ï¿½ï¿½îŸ�ï€»ï¿½ï�Œï¿½ï¿½ï„”ï¿½î¦ºï…¬ï¿½ï¿½î¿–ï¿½îš£ï¿½ï•›ï¿½ï¿½
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_PacketBox(53, 5, 0));
				pc.setCookingId(0);
			}
		}
		else if ((skillId == COOKING_1_6_N) || (skillId == COOKING_1_6_S)) { // ï¿½ï–¿ï¿½ï¿½ï�¡î¼„éŠ�è„©ïƒ©ï¿½ï¿½ï¿½
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMr(-5);
				pc.sendPackets(new S_SPMR(pc));
				pc.sendPackets(new S_PacketBox(53, 6, 0));
				pc.setCookingId(0);
			}
		}
		else if ((skillId == COOKING_1_7_N) || (skillId == COOKING_1_7_S)) { // ï¿½î¾ ï¿½ï¿½îŽ’î¾¦ï¿½î¾¬ï¿½ï�Œï¿½ï¿½ï¿½
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_PacketBox(53, 7, 0));
				pc.setDessertId(0);
			}
		}
		else if ((skillId == COOKING_2_0_N) || (skillId == COOKING_2_0_S)) { // ï¿½î¾ ï¿½ï€³ï¿½ï¿½îš£î¾•ï¿½î¾žï¿½ï¿½ï’¾ï¿½ï�³ï¿½ï¿½
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_PacketBox(53, 8, 0));
				pc.setCookingId(0);
			}
		}
		else if ((skillId == COOKING_2_1_N) || (skillId == COOKING_2_1_S)) { // ï¿½î¾•ï¿½ï€ºï¿½î¾¥ï¿½ï�Œï¿½î¾²ï¿½ï�Œï¿½î¾¬ï¿½ï¿½ï‰Šï�Œï¿½î¾ 
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMaxHp(-30);
				pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
				if (pc.isInParty()) { // ï¿½ï¿½î•©ï�Œï¿½ï¿½ï‰Šî¾–ï¿½ï�ŒéŠ�ï¿½
					pc.getParty().updateMiniHP(pc);
				}
				pc.addMaxMp(-30);
				pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
				pc.sendPackets(new S_PacketBox(53, 9, 0));
				pc.setCookingId(0);
			}
		}
		else if ((skillId == COOKING_2_2_N) || (skillId == COOKING_2_2_S)) { // ï¿½î¾²ï¿½ï�Œï¿½ï¿½ïŽ„ï€»ï¿½ï¿½ï�¡ï€¹ï¿½î¾§ï¿½ï�ƒï¿½î¼„ï¿½ï¿½îš¥ï¿½ï¿½
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addAc(2);
				pc.sendPackets(new S_PacketBox(53, 10, 0));
				pc.setCookingId(0);
			}
		}
		else if ((skillId == COOKING_2_3_N) || (skillId == COOKING_2_3_S)) { // ï¿½î¾ ï¿½î¾™ï¿½î¾–ï¿½ï¿½î•©ï€½ï¿½ï¿½ï�³ï¿½ïŽˆïƒ©ï¿½ï¿½ï¿½
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_PacketBox(53, 11, 0));
				pc.setCookingId(0);
			}
		}
		else if ((skillId == COOKING_2_4_N) || (skillId == COOKING_2_4_S)) { // ï¿½î¾¬ï¿½î¾¦ï¿½ï�Œï¿½ï¿½î�€î¾�ï¿½ï�ƒï¿½ïƒ©ï¿½ï¿½ï¿½
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_PacketBox(53, 12, 0));
				pc.setCookingId(0);
			}
		}
		else if ((skillId == COOKING_2_5_N) || (skillId == COOKING_2_5_S)) { // ï¿½î¾—ï¿½ï€¼ï¿½ï¿½ï�³î¾žï¿½ï¿½ï�¡ï¿½îºœî¾ªï¿½ï¿½î¼¹ï€µï¿½ï�Œ
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMr(-10);
				pc.sendPackets(new S_SPMR(pc));
				pc.sendPackets(new S_PacketBox(53, 13, 0));
				pc.setCookingId(0);
			}
		}
		else if ((skillId == COOKING_2_6_N) || (skillId == COOKING_2_6_S)) { // ï¿½î¾¢ï¿½ï€²ï¿½ï¿½î«®î¼„éŠ�è„©ïƒ©ï¿½ï¿½ï¿½
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addSp(-1);
				pc.sendPackets(new S_SPMR(pc));
				pc.sendPackets(new S_PacketBox(53, 14, 0));
				pc.setCookingId(0);
			}
		}
		else if ((skillId == COOKING_2_7_N) || (skillId == COOKING_2_7_S)) { // ï¿½î¾¢ï¿½ï€¹ï¿½ï¿½î¡ºî¾¬ï¿½ï�Œï¿½ï¿½ï¿½
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_PacketBox(53, 15, 0));
				pc.setDessertId(0);
			}
		}
		else if ((skillId == COOKING_3_0_N) || (skillId == COOKING_3_0_S)) { // ï¿½î¾¢ï¿½ï€¹ï¿½î¾¬ï¿½î¾²ï¿½î¾ªï¿½î¾•ï¿½ï�ƒï¿½î¼„ï¿½ï¿½î�¯î¾¨ï¿½ï¿½î¸ƒïƒ©ï¿½ï¿½ï¿½
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_PacketBox(53, 16, 0));
				pc.setCookingId(0);
			}
		}
		else if ((skillId == COOKING_3_1_N) || (skillId == COOKING_3_1_S)) { // ï¿½î¾£ï¿½ï€ºï¿½ï¿½îŸ�î¾œï¿½ï�ƒï¿½ïƒ©ï¿½ï¿½ï¿½
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMaxHp(-50);
				pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
				if (pc.isInParty()) { // ï¿½ï¿½î•©ï�Œï¿½ï¿½ï‰Šî¾–ï¿½ï�ŒéŠ�ï¿½
					pc.getParty().updateMiniHP(pc);
				}
				pc.addMaxMp(-50);
				pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
				pc.sendPackets(new S_PacketBox(53, 17, 0));
				pc.setCookingId(0);
			}
		}
		else if ((skillId == COOKING_3_2_N) || (skillId == COOKING_3_2_S)) { // ï¿½î¾¦ï¿½î¾žï¿½ï¿½ïŽ„ï€ºï¿½î¾¬ï¿½î¾¬ï¿½ï¿½ï‰Šï�Œï¿½î¾ 
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_PacketBox(53, 18, 0));
				pc.setCookingId(0);
			}
		}
		else if ((skillId == COOKING_3_3_N) || (skillId == COOKING_3_3_S)) { // ï¿½î¾²ï¿½ï�Œï¿½ï¿½ïŽ„ï€»ï¿½ï¿½ï�¡ï€¹ï¿½î¾§ï¿½ï�ƒï¿½ïƒ©ï¿½ï¿½ï¿½
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addAc(3);
				pc.sendPackets(new S_PacketBox(53, 19, 0));
				pc.setCookingId(0);
			}
		}
		else if ((skillId == COOKING_3_4_N) || (skillId == COOKING_3_4_S)) { // ï¿½ï€¼ï¿½ï¿½ï�³î¾¨ï¿½ï�Œï¿½ï¿½ï�¡ï€¹ï¿½î¾§ï¿½ï�ƒï¿½î¼„ï¿½ï¿½ï•Ÿå™¬ï¿½ï¿½ï¿½
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMr(-15);
				pc.sendPackets(new S_SPMR(pc));
				pc.addWind(-10);
				pc.addWater(-10);
				pc.addFire(-10);
				pc.addEarth(-10);
				pc.sendPackets(new S_OwnCharAttrDef(pc));
				pc.sendPackets(new S_PacketBox(53, 20, 0));
				pc.setCookingId(0);
			}
		}
		else if ((skillId == COOKING_3_5_N) || (skillId == COOKING_3_5_S)) { // ï¿½ï¿½ï�¡ï€¼ï¿½î¾—ï¿½î¾¢ï¿½ïƒ©ï¿½ï¿½ï¿½
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addSp(-2);
				pc.sendPackets(new S_SPMR(pc));
				pc.sendPackets(new S_PacketBox(53, 21, 0));
				pc.setCookingId(0);
			}
		}
		else if ((skillId == COOKING_3_6_N) || (skillId == COOKING_3_6_S)) { // ç˜›æœ›çµ²æ“³î«®î¼„ï¿½î¾ªï¿½ï¿½î¼¹ï€µï¿½ï�Œ
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMaxHp(-30);
				pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
				if (pc.isInParty()) { // ï¿½ï¿½î•©ï�Œï¿½ï¿½ï‰Šî¾–ï¿½ï�ŒéŠ�ï¿½
					pc.getParty().updateMiniHP(pc);
				}
				pc.sendPackets(new S_PacketBox(53, 22, 0));
				pc.setCookingId(0);
			}
		}
		else if ((skillId == COOKING_3_7_N) || (skillId == COOKING_3_7_S)) { // ï¿½ï¿½î“Œî¾ªï¿½ï€ºï¿½î¾¬ï¿½î¾¢ï¿½î¼„ï¿½ï™§ï¿½î¾¬ï¿½ï�Œï¿½ï¿½ï¿½
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_PacketBox(53, 23, 0));
				pc.setDessertId(0);
			}
		}
		else if (skillId == COOKING_WONDER_DRUG) { // éžŠâˆ ï¿½î©“ï¿½î�‚ï¿½î©–î£™
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addHpr(-10);
				pc.addMpr(-2);
			}
		}
		// ****** 
		else if (skillId == EFFECT_BLESS_OF_MAZU) { // æ…¦è³œï¿½î¡¾ï¿½ï„”ï¿½î³‰ï¿½ï¿½
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addHitup(-3);
				pc.addDmgup(-3);
				pc.addMpr(-2);
			}
		}
		else if (skillId == EFFECT_STRENGTHENING_HP) { // æ“ƒî�‚ï¿½î®�ï¿½îµ¤æ’¥ï¿½ï™©é  ï¿½
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMaxHp(-50);
				pc.addHpr(-4);
				pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
				if (pc.isInParty()) { // è�¯ï„–ï¿½ï’¿è‘‰
					pc.getParty().updateMiniHP(pc);
				}
			}
		}
		else if (skillId == EFFECT_STRENGTHENING_MP) { // æ“³î�‚ï¿½î®�ï¿½îµ¤æ’¥ï¿½ï™©é  ï¿½
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMaxMp(-40);
				pc.addMpr(-4);
				pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
			}
		}
		else if (skillId == EFFECT_ENCHANTING_BATTLE) { // æ’˜ç‘•ï¿½î¡½ï�‘æ“›äº™ï™©é  ï¿½
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addHitup(-3);
				pc.addDmgup(-3);
				pc.addBowHitup(-3);
				pc.addBowDmgup(-3);
				pc.addSp(-3);
				pc.sendPackets(new S_SPMR(pc));
			}
		}
		else if (skillId == MIRROR_IMAGE || skillId == UNCANNY_DODGE) { // ï¿½î�­ï¿½ï¿½î�¯ï¿½î¼¼ï¿½î¤™è”£ï¿½ï¿½ï�¹î¼•
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addDodge((byte) -5); // ï¿½ï¿½ï�¹î¼•ï¿½ï¿½ï¿½ - 50%
				// ï¿½î­œï¿½î¡‡ï¿½ï¿½ï�¹î¼•ï¿½ï¿½ï‹­ï¼Šè�·ï¿½
				pc.sendPackets(new S_PacketBox(88, pc.getDodge()));
			}
		}
		else if (skillId == RESIST_FEAR) { // ï¿½ï¿½î“�ï‹€ï¿½ïƒŽï¿½ï’„
			cha.addNdodge((byte) -5); // ï¿½ï¿½ï�¹î¼•ï¿½ï¿½ï¿½ + 50%
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				// ï¿½î­œï¿½î¡‡ï¿½ï¿½ï�¹î¼•ï¿½ï¿½ï‹­ï¼Šè�·ï¿½
				pc.sendPackets(new S_PacketBox(101, pc.getNdodge()));
			}
		}
		else if (skillId == EFFECT_BLOODSTAIN_OF_ANTHARAS) { // æ‘°ï�£ï¿½î�„ï¿½îµ¥î¡†ï¿½ï¿½ï„•ï¿½ï¿½ï¿½ï¿½
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addAc(2);
				pc.addWater(-50);
				pc.sendPackets(new S_SkillIconBloodstain(82, 0));
			}
		}
		else if (skillId == EFFECT_BLOODSTAIN_OF_FAFURION) { // ç˜œîŸŸï�Šï¿½ï¿½î¿šï¿½ï„•ï¿½ï¿½ï¿½ï¿½
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addWind(-50);
				pc.sendPackets(new S_SkillIconBloodstain(85, 0));
			}
		}
		else if (skillId == LOGIN_EIN_TIME) {
			if (Config_Einhasad.EINHASAD_IS_ACTIVE) {
				if (cha instanceof L1PcInstance) {
					final L1PcInstance pc = (L1PcInstance) cha;
					if (pc.isMatchEinResult()) {
						pc.addEinPoint(1); // æšºîµ¥îž² +1
					}
					pc.setSkillEffect(L1SkillId.LOGIN_EIN_TIME,
							Config_Einhasad.EIN_TIME * 60000); // Nï¿½ï¿½ï‰�ï¿½ï¿½
				}
			}			
		}
		else if (skillId == EFFECT_MAGIC_STONE_A_1) {
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMaxHp(-10);
				pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
				if (pc.isInParty()) { // è�¯ï„–ï¿½ï’¿è‘‰
					pc.getParty().updateMiniHP(pc);
				}
				pc.setMagicStoneLevel((byte) 0);
			}
		}
		else if (skillId == EFFECT_MAGIC_STONE_A_2) {
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMaxHp(-20);
				pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
				if (pc.isInParty()) { // è�¯ï„–ï¿½ï’¿è‘‰
					pc.getParty().updateMiniHP(pc);
				}
				pc.setMagicStoneLevel((byte) 0);
			}
		}
		else if (skillId == EFFECT_MAGIC_STONE_A_3) {
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMaxHp(-30);
				pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
				if (pc.isInParty()) { // è�¯ï„–ï¿½ï’¿è‘‰
					pc.getParty().updateMiniHP(pc);
				}
				pc.setMagicStoneLevel((byte) 0);
			}
		}
		else if (skillId == EFFECT_MAGIC_STONE_A_4) {
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMaxHp(-40);
				pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
				if (pc.isInParty()) { // è�¯ï„–ï¿½ï’¿è‘‰
					pc.getParty().updateMiniHP(pc);
				}
				pc.setMagicStoneLevel((byte) 0);
			}
		}
		else if (skillId == EFFECT_MAGIC_STONE_A_5) {
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMaxHp(-50);
				pc.addHpr(-1);
				pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
				if (pc.isInParty()) { // è�¯ï„–ï¿½ï’¿è‘‰
					pc.getParty().updateMiniHP(pc);
				}
				pc.setMagicStoneLevel((byte) 0);
			}
		}
		else if (skillId == EFFECT_MAGIC_STONE_A_6) {
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMaxHp(-60);
				pc.addHpr(-2);
				pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
				if (pc.isInParty()) { // è�¯ï„–ï¿½ï’¿è‘‰
					pc.getParty().updateMiniHP(pc);
				}
				pc.setMagicStoneLevel((byte) 0);
			}
		}
		else if (skillId == EFFECT_MAGIC_STONE_A_7) {
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMaxHp(-70);
				pc.addHpr(-3);
				pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
				if (pc.isInParty()) { // è�¯ï„–ï¿½ï’¿è‘‰
					pc.getParty().updateMiniHP(pc);
				}
				pc.setMagicStoneLevel((byte) 0);
			}
		}
		else if (skillId == EFFECT_MAGIC_STONE_A_8) {
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMaxHp(-80);
				pc.addHpr(-4);
				pc.addHitup(-1);
				pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
				if (pc.isInParty()) { // è�¯ï„–ï¿½ï’¿è‘‰
					pc.getParty().updateMiniHP(pc);
				}
				pc.setMagicStoneLevel((byte) 0);
			}
		}
		else if (skillId == EFFECT_MAGIC_STONE_A_9) {
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMaxHp(-100);
				pc.addHpr(-5);
				pc.addHitup(-2);
				pc.addDmgup(-2);
				pc.addStr((byte) -1);
				pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
				if (pc.isInParty()) { // è�¯ï„–ï¿½ï’¿è‘‰
					pc.getParty().updateMiniHP(pc);
				}
				pc.setMagicStoneLevel((byte) 0);
			}
		}
		else if (skillId == EFFECT_MAGIC_STONE_B_1) {
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMaxHp(-5);
				pc.addMaxMp(-3);
				pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
				pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
				if (pc.isInParty()) { // è�¯ï„–ï¿½ï’¿è‘‰
					pc.getParty().updateMiniHP(pc);
				}
				pc.setMagicStoneLevel((byte) 0);
			}
		}
		else if (skillId == EFFECT_MAGIC_STONE_B_2) {
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMaxHp(-10);
				pc.addMaxMp(-6);
				pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
				pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
				if (pc.isInParty()) { // è�¯ï„–ï¿½ï’¿è‘‰
					pc.getParty().updateMiniHP(pc);
				}
				pc.setMagicStoneLevel((byte) 0);
			}
		}
		else if (skillId == EFFECT_MAGIC_STONE_B_3) {
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMaxHp(-15);
				pc.addMaxMp(-10);
				pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
				pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
				if (pc.isInParty()) { // è�¯ï„–ï¿½ï’¿è‘‰
					pc.getParty().updateMiniHP(pc);
				}
				pc.setMagicStoneLevel((byte) 0);
			}
		}
		else if (skillId == EFFECT_MAGIC_STONE_B_4) {
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMaxHp(-20);
				pc.addMaxMp(-15);
				pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
				pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
				if (pc.isInParty()) { // è�¯ï„–ï¿½ï’¿è‘‰
					pc.getParty().updateMiniHP(pc);
				}
				pc.setMagicStoneLevel((byte) 0);
			}
		}
		else if (skillId == EFFECT_MAGIC_STONE_B_5) {
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMaxHp(-25);
				pc.addMaxMp(-20);
				pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
				pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
				if (pc.isInParty()) { // è�¯ï„–ï¿½ï’¿è‘‰
					pc.getParty().updateMiniHP(pc);
				}
				pc.setMagicStoneLevel((byte) 0);
			}
		}
		else if (skillId == EFFECT_MAGIC_STONE_B_6) {
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMaxHp(-30);
				pc.addMaxMp(-20);
				pc.addHpr(-1);
				pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
				pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
				if (pc.isInParty()) { // è�¯ï„–ï¿½ï’¿è‘‰
					pc.getParty().updateMiniHP(pc);
				}
				pc.setMagicStoneLevel((byte) 0);
			}
		}
		else if (skillId == EFFECT_MAGIC_STONE_B_7) {
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMaxHp(-35);
				pc.addMaxMp(-20);
				pc.addHpr(-1);
				pc.addMpr(-1);
				pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
				pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
				if (pc.isInParty()) { // è�¯ï„–ï¿½ï’¿è‘‰
					pc.getParty().updateMiniHP(pc);
				}
				pc.setMagicStoneLevel((byte) 0);
			}
		}
		else if (skillId == EFFECT_MAGIC_STONE_B_8) {
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMaxHp(-40);
				pc.addMaxMp(-25);
				pc.addHpr(-2);
				pc.addMpr(-1);
				pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
				pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
				if (pc.isInParty()) { // è�¯ï„–ï¿½ï’¿è‘‰
					pc.getParty().updateMiniHP(pc);
				}
				pc.setMagicStoneLevel((byte) 0);
			}
		}
		else if (skillId == EFFECT_MAGIC_STONE_B_9) {
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMaxHp(-50);
				pc.addMaxMp(-30);
				pc.addHpr(-2);
				pc.addMpr(-2);
				pc.addBowDmgup(-2);
				pc.addBowHitup(-2);
				pc.addDex((byte) -1);
				pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
				pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
				if (pc.isInParty()) { // è�¯ï„–ï¿½ï’¿è‘‰
					pc.getParty().updateMiniHP(pc);
				}
				pc.setMagicStoneLevel((byte) 0);
			}
		}
		else if (skillId == EFFECT_MAGIC_STONE_C_1) {
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMaxMp(-5);
				pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
				pc.setMagicStoneLevel((byte) 0);
			}
		}
		else if (skillId == EFFECT_MAGIC_STONE_C_2) {
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMaxMp(-10);
				pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
				pc.setMagicStoneLevel((byte) 0);
			}
		}
		else if (skillId == EFFECT_MAGIC_STONE_C_3) {
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMaxMp(-15);
				pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
				pc.setMagicStoneLevel((byte) 0);
			}
		}
		else if (skillId == EFFECT_MAGIC_STONE_C_4) {
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMaxMp(-20);
				pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
				pc.setMagicStoneLevel((byte) 0);
			}
		}
		else if (skillId == EFFECT_MAGIC_STONE_C_5) {
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMaxMp(-25);
				pc.addMpr(-1);
				pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
				pc.setMagicStoneLevel((byte) 0);
			}
		}
		else if (skillId == EFFECT_MAGIC_STONE_C_6) {
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMaxMp(-30);
				pc.addMpr(-2);
				pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
				pc.setMagicStoneLevel((byte) 0);
			}
		}
		else if (skillId == EFFECT_MAGIC_STONE_C_7) {
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMaxMp(-35);
				pc.addMpr(-3);
				pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
				pc.setMagicStoneLevel((byte) 0);
			}
		}
		else if (skillId == EFFECT_MAGIC_STONE_C_8) {
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMaxMp(-40);
				pc.addMpr(-4);
				pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
				pc.setMagicStoneLevel((byte) 0);
			}
		}
		else if (skillId == EFFECT_MAGIC_STONE_C_9) {
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMaxMp(-50);
				pc.addMpr(-5);
				pc.addInt((byte)-1);
				pc.addSp(-1);
				pc.sendPackets(new S_SPMR(pc));
				pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
				pc.setMagicStoneLevel((byte) 0);
			}
		}
		else if (skillId == EFFECT_MAGIC_STONE_D_1) {
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMr(-2);
				pc.sendPackets(new S_SPMR(pc));
				pc.setMagicStoneLevel((byte) 0);
			}
		}
		else if (skillId == EFFECT_MAGIC_STONE_D_2) {
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMr(-4);
				pc.sendPackets(new S_SPMR(pc));
				pc.setMagicStoneLevel((byte) 0);
			}
		}
		else if (skillId == EFFECT_MAGIC_STONE_D_3) {
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMr(-6);
				pc.sendPackets(new S_SPMR(pc));
				pc.setMagicStoneLevel((byte) 0);
			}
		}
		else if (skillId == EFFECT_MAGIC_STONE_D_4) {
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMr(-8);
				pc.sendPackets(new S_SPMR(pc));
				pc.setMagicStoneLevel((byte) 0);
			}
		}
		else if (skillId == EFFECT_MAGIC_STONE_D_5) {
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMr(-10);
				pc.addAc(1);
				pc.sendPackets(new S_SPMR(pc));
				pc.setMagicStoneLevel((byte) 0);
			}
		}
		else if (skillId == EFFECT_MAGIC_STONE_D_6) {
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMr(-10);
				pc.addAc(2);
				pc.sendPackets(new S_SPMR(pc));
				pc.setMagicStoneLevel((byte) 0);
			}
		}
		else if (skillId == EFFECT_MAGIC_STONE_D_7) {
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMr(-10);
				pc.addAc(3);
				pc.sendPackets(new S_SPMR(pc));
				pc.setMagicStoneLevel((byte) 0);
			}
		}
		else if (skillId == EFFECT_MAGIC_STONE_D_8) {
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMr(-15);
				pc.addAc(4);
				pc.addDamageReductionByArmor(-1);
				pc.sendPackets(new S_SPMR(pc));
				pc.setMagicStoneLevel((byte) 0);
			}
		}
		else if (skillId == EFFECT_MAGIC_STONE_D_9) {
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMr(-20);
				pc.addAc(5);
				pc.addCon((byte) -1);
				pc.addDamageReductionByArmor(-3);
				pc.sendPackets(new S_SPMR(pc));
				pc.setMagicStoneLevel((byte) 0);
			}
		}
		else if (skillId == EFFECT_MAGIC_EYE_OF_AHTHARTS) {
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addRegistStone(-3); // ï¿½î·�ï¿½ï¿½î¡¿ï¿½î“�ï¿½ï¿½

				pc.addDodge((byte) -1); // ï¿½ï¿½ï�¹î¼•ï¿½ï¿½ï¿½ - 10%
				// ï¿½î­œï¿½î¡‡ï¿½ï¿½ï�¹î¼•ï¿½ï¿½ï‹­ï¼Šè�·ï¿½
				pc.sendPackets(new S_PacketBox(88, pc.getDodge()));
			}
		}
		else if (skillId == EFFECT_MAGIC_EYE_OF_FAFURION) {
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.add_regist_freeze(-3); // æ’–î˜ˆïˆ—ï¿½î“�ï¿½ï¿½
				// æ“³î�ƒï¿½îŸŸî¾ªæ‘°å–®ï¿½î®�ï¿½ï¿½
			}
		}
		else if (skillId == EFFECT_MAGIC_EYE_OF_LINDVIOR) {
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addRegistSleep(-3); // ï¿½î²ƒï¿½ï¿½îº¡ï¿½î“�ï¿½ï¿½
				// æ“³î�ƒï¿½îŸ îª¿ï¿½ï¿½ï“‚ï¿½ï¿½
			}
		}
		else if (skillId == EFFECT_MAGIC_EYE_OF_VALAKAS) {
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addRegistStun(-3); // ï¿½ï¿½î�´è•™ï¿½î“�ï¿½ï¿½
				pc.addDmgup(-2); // æ†¿ïš—ï¿½î¡½îœ˜ï¿½ï¿½ï“„ï¿½îµ¥îž²
			}
		}
		else if (skillId == EFFECT_MAGIC_EYE_OF_BIRTH) {
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addRegistBlind(-3); // ï¿½ï¿½ï‹­ï¿½î•®ï¿½î“�ï¿½ï¿½
				// æ“³î�ƒï¿½îŸŸî¾ªæ‘°å–®ï¿½î®�ï¿½ï¿½

				pc.addDodge((byte) -1); // ï¿½ï¿½ï�¹î¼•ï¿½ï¿½ï¿½ - 10%
				// ï¿½î­œï¿½î¡‡ï¿½ï¿½ï�¹î¼•ï¿½ï¿½ï‹­ï¼Šè�·ï¿½
				pc.sendPackets(new S_PacketBox(88, pc.getDodge()));
			}
		}
		else if (skillId == EFFECT_MAGIC_EYE_OF_FIGURE) {
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addRegistSustain(-3); // ï¿½îœŒï¿½ï¿½î“‘ï¿½î“�ï¿½ï¿½
				// æ“³î�ƒï¿½îŸŸî¾ªæ‘°å–®ï¿½î®�ï¿½ï¿½
				// æ“³î�ƒï¿½îŸ îª¿ï¿½ï¿½ï“‚ï¿½ï¿½

				pc.addDodge((byte) -1); // ï¿½ï¿½ï�¹î¼•ï¿½ï¿½ï¿½ - 10%
				// ï¿½î­œï¿½î¡‡ï¿½ï¿½ï�¹î¼•ï¿½ï¿½ï‹­ï¼Šè�·ï¿½
				pc.sendPackets(new S_PacketBox(88, pc.getDodge()));
			}
		}
		else if (skillId == EFFECT_MAGIC_EYE_OF_LIFE) {
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addDmgup(2); // æ†¿ïš—ï¿½î¡½îœ˜ï¿½ï¿½ï“„ï¿½îµ¥îž²
				// æ“³î�ƒï¿½îŸŸî¾ªæ‘°å–®ï¿½î®�ï¿½ï¿½
				// æ“³î�ƒï¿½îŸ îª¿ï¿½ï¿½ï“‚ï¿½ï¿½
				// ï¿½î¦ƒéœ…ç‘šè‘‰ç˜¥î˜Šï¿½ï¿½ï¿½ï¿½

				pc.addDodge((byte) -1); // ï¿½ï¿½ï�¹î¼•ï¿½ï¿½ï¿½ - 10%
				// ï¿½î­œï¿½î¡‡ï¿½ï¿½ï�¹î¼•ï¿½ï¿½ï‹­ï¼Šè�·ï¿½
				pc.sendPackets(new S_PacketBox(88, pc.getDodge()));
			}
		}
		else if (skillId == EFFECT_BLESS_OF_CRAY) { // ï¿½ï™“ï¿½ï¿½îµ¦ï¿½ï„”ï¿½î³‰ï¿½ï¿½
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMaxHp(-100);
				pc.addMaxMp(-50);
				pc.addHpr(-3);
				pc.addMpr(-3);
				pc.addEarth(-30);
				pc.addDmgup(-1);
				pc.addHitup(-5);
				pc.addWeightReduction(-40);
				pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
				if (pc.isInParty()) {
					pc.getParty().updateMiniHP(pc);
				}
				pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
			}
		}
		else if (skillId == EFFECT_BLESS_OF_SAELL) { // ï¿½ï¿½îŽ–ï�Ÿï¿½ï¿½ï„”ï¿½î³‰ï¿½ï¿½
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMaxHp(-80);
				pc.addMaxMp(-10);
				pc.addWater(-30);
				pc.addAc(8);
				pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
				if (pc.isInParty()) {
					pc.getParty().updateMiniHP(pc);
				}
				pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
			}
		}
		else if (skillId == ERASE_MAGIC) { // æ“³î�ƒï¿½îŸ ï¿½ïŽŠî¨’
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillIconAura(152, 0));
			}
		}
		else if (skillId == STATUS_CURSE_YAHEE) { // ï¿½ï¿½îŽ˜ï¿½î�„ï¿½ï„”ï¿½î©“ï™¢
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillIconAura(221, 0, 1));
			}
		}
		else if (skillId == STATUS_CURSE_BARLOG) { // ï¿½î¼�ï¿½ïƒ�éŠ‹ï•�è”£ï¿½ï¿½ï„”ï¿½î©“ï™¢
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillIconAura(221, 0, 2));
			}
		}

		if (cha instanceof L1PcInstance) {
			L1PcInstance pc = (L1PcInstance) cha;
			sendStopMessage(pc, skillId);
			pc.sendPackets(new S_OwnCharStatus(pc));
		}
	}

	// ï¿½ï€±ï¿½ï¿½ï�³î¾®ï¿½ï�Œï¿½î¾«ï¿½î¼„éŠµå‡½å…§åš—ïŽˆï¿½î¿—ï¿½ï‰Šï¿½î©‘ï¿½ï•›î»¾ï¿½ï¿½ïš¡ï¿½ï¿½
	private static void sendStopMessage(L1PcInstance charaPc, int skillid) {
		L1Skills l1skills = SkillsTable.getInstance().getTemplate(skillid);
		if ((l1skills == null) || (charaPc == null)) {
			return;
		}

		int msgID = l1skills.getSysmsgIdStop();
		if (msgID > 0) {
			charaPc.sendPackets(new S_ServerMessage(msgID));
		}
	}
}

class L1SkillTimerThreadImpl extends Thread implements L1SkillTimer {
	public L1SkillTimerThreadImpl(L1Character cha, int skillId, int timeMillis) {
		_cha = cha;
		_skillId = skillId;
		_timeMillis = timeMillis;
	}

	
	public void run() {
		for (int timeCount = _timeMillis / 1000; timeCount > 0; timeCount--) {
			try {
				Thread.sleep(1000);
				_remainingTime = timeCount;
			}
			catch (InterruptedException e) {
				return;
			}
		}
		_cha.removeSkillEffect(_skillId);
	}

	
	public int getRemainingTime() {
		return _remainingTime;
	}

	
	public void begin() {
		GeneralThreadPool.getInstance().execute(this);
	}

	
	public void end() {
		super.interrupt();
		L1SkillStop.stopSkill(_cha, _skillId);
	}

	
	public void kill() {
		if (Thread.currentThread().getId() == super.getId()) {
			return; // ï¿½î•‚ï¿½î¼‰ï¿½ïŠ¾ï¿½ï¿½î¤™ï¿½ï�³î¾¬ï¿½ï€¼ï¿½ï¿½ï�³ï¿½ï�¡ï¿½ï—½ïŠ®ï¿½ï¿½ï‰Šî»½ï¿½ï¿½î¿–ï¿½ï—¸î¼†ç”‡ï¼µï¿½î¼¹î¼€ï¿½ï¿½ï¿½
		}
		super.interrupt();
	}

	private final L1Character _cha;

	private final int _timeMillis;

	private final int _skillId;

	private int _remainingTime;
}

class L1SkillTimerTimerImpl implements L1SkillTimer, Runnable {
	private static Logger _log = Logger.getLogger(L1SkillTimerTimerImpl.class.getName());

	private ScheduledFuture<?> _future = null;

	public L1SkillTimerTimerImpl(L1Character cha, int skillId, int timeMillis) {
		_cha = cha;
		_skillId = skillId;
		_timeMillis = timeMillis;

		_remainingTime = _timeMillis / 1000;
	}

	
	public void run() {
		_remainingTime--;
		if (_remainingTime <= 0) {
			_cha.removeSkillEffect(_skillId);
		}
	}

	
	public void begin() {
		_future = GeneralThreadPool.getInstance().scheduleAtFixedRate(this, 1000, 1000);
	}

	
	public void end() {
		kill();
		try {
			L1SkillStop.stopSkill(_cha, _skillId);
		}
		catch (Throwable e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		}
	}

	
	public void kill() {
		if (_future != null) {
			_future.cancel(false);
		}
	}

	
	public int getRemainingTime() {
		return _remainingTime;
	}

	private final L1Character _cha;

	private final int _timeMillis;

	private final int _skillId;

	private int _remainingTime;
}