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

import l1j.server.Config;
import l1j.server.server.ActionCodes;
import l1j.server.server.WarTimeController;
import l1j.server.server.datatables.SkillsTable;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.model.Instance.L1SummonInstance;
import l1j.server.server.model.skill.L1NamedSkill;
import l1j.server.server.serverpackets.S_DoActionGFX;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1MagicDoll;
import l1j.server.server.templates.L1Skills;
import l1j.server.server.utils.Random;

import static l1j.server.server.model.skill.L1SkillId.*;

public class L1Magic {
    private int _calcType;

    private final int PC_PC = 1;

    private final int PC_NPC = 2;

    private final int NPC_PC = 3;

    private final int NPC_NPC = 4;

    private L1Character _target = null;

    private L1PcInstance _pc = null;

    private L1PcInstance _targetPc = null;

    private L1NpcInstance _npc = null;

    private L1NpcInstance _targetNpc = null;

    private int _leverage = 10; // 1/10å€�ã�§è¡¨ç�¾ã�™ã‚‹ã€‚

    public void setLeverage(int i) {
        _leverage = i;
    }

    private int getLeverage() {
        return _leverage;
    }

    public L1Magic(L1Character attacker, L1Character target) {
        _target = target;

        if (attacker instanceof L1PcInstance) {
            if (target instanceof L1PcInstance) {
                _calcType = PC_PC;
                _pc = (L1PcInstance) attacker;
                _targetPc = (L1PcInstance) target;
            }
            else {
                _calcType = PC_NPC;
                _pc = (L1PcInstance) attacker;
                _targetNpc = (L1NpcInstance) target;
            }
        }
        else {
            if (target instanceof L1PcInstance) {
                _calcType = NPC_PC;
                _npc = (L1NpcInstance) attacker;
                _targetPc = (L1PcInstance) target;
            }
            else {
                _calcType = NPC_NPC;
                _npc = (L1NpcInstance) attacker;
                _targetNpc = (L1NpcInstance) target;
            }
        }
    }

    private int getMagicLevel() {
        int magicLevel = 0;
        if ((_calcType == PC_PC) || (_calcType == PC_NPC)) {
            magicLevel = _pc.getMagicLevel();
        }
        else if ((_calcType == NPC_PC) || (_calcType == NPC_NPC)) {
            magicLevel = _npc.getMagicLevel();
        }
        return magicLevel;
    }

    private int getMagicBonus() {
        int magicBonus = 0;
        if ((_calcType == PC_PC) || (_calcType == PC_NPC)) {
            magicBonus = _pc.getMagicBonus();
        }
        else if ((_calcType == NPC_PC) || (_calcType == NPC_NPC)) {
            magicBonus = _npc.getMagicBonus();
        }
        return magicBonus;
    }

    private int getLawful() {
        int lawful = 0;
        if ((_calcType == PC_PC) || (_calcType == PC_NPC)) {
            lawful = _pc.getLawful();
        }
        else if ((_calcType == NPC_PC) || (_calcType == NPC_NPC)) {
            lawful = _npc.getLawful();
        }
        return lawful;
    }

    private int getTargetMr() {
        int mr = 0;
        if ((_calcType == PC_PC) || (_calcType == NPC_PC)) {
            mr = _targetPc.getMr();
        }
        else {
            mr = _targetNpc.getMr();
        }
        return mr;
    }

    /* â– â– â– â– â– â– â– â– â– â– â– â– â– â–  æˆ�åŠŸåˆ¤å®š â– â– â– â– â– â– â– â– â– â– â– â– â–  */
    // â—�â—�â—�â—� ç¢ºçŽ‡ç³»é­”æ³•ã�®æˆ�åŠŸåˆ¤å®š â—�â—�â—�â—�
    // è¨ˆç®—æ–¹æ³•
    // æ”»æ’ƒå�´ãƒ�ã‚¤ãƒ³ãƒˆï¼šLV + ((MagicBonus * 3) * é­”æ³•å›ºæœ‰ä¿‚æ•°)
    // é˜²å¾¡å�´ãƒ�ã‚¤ãƒ³ãƒˆï¼š((LV / 2) + (MR * 3)) / 2
    // æ”»æ’ƒæˆ�åŠŸçŽ‡ï¼šæ”»æ’ƒå�´ãƒ�ã‚¤ãƒ³ãƒˆ - é˜²å¾¡å�´ãƒ�ã‚¤ãƒ³ãƒˆ
    public boolean calcProbabilityMagic(int skillId) {
        int probability = 0;
        boolean isSuccess = false;

        // æ”»æ’ƒè€…ã�ŒGMæ¨©é™�ã�®å ´å�ˆ100%æˆ�åŠŸ
        if ((_pc != null) && _pc.isGm()) {
            return true;
        }

        // åˆ¤æ–·ç‰¹å®šç‹€æ…‹ä¸‹æ‰�å�¯æ”»æ“Š NPC
        if ((_calcType == PC_NPC) && (_targetNpc != null)) {
            if (_pc.isAttackMiss(_pc, _targetNpc.getNpcTemplate().get_npcId())) {
                return false;
            }
        }

        if (!checkZone(skillId)) {
            return false;
        }
        if (skillId == CANCELLATION) {
            if ((_calcType == PC_PC) && (_pc != null) && (_targetPc != null)) {
                // è‡ªåˆ†è‡ªèº«ã�®å ´å�ˆã�¯100%æˆ�åŠŸ
                if (_pc.getId() == _targetPc.getId()) {
                    return true;
                }
                // å�Œã�˜ã‚¯ãƒ©ãƒ³ã�®å ´å�ˆã�¯100%æˆ�åŠŸ
                if ((_pc.getClanid() > 0) && (_pc.getClanid() == _targetPc.getClanid())) {
                    return true;
                }
                // å�Œã�˜ãƒ‘ãƒ¼ãƒ†ã‚£ã�®å ´å�ˆã�¯100%æˆ�åŠŸ
                if (_pc.isInParty()) {
                    if (_pc.getParty().isMember(_targetPc)) {
                        return true;
                    }
                }
                // ã��ã‚Œä»¥å¤–ã�®å ´å�ˆã€�ã‚»ãƒ¼ãƒ•ãƒ†ã‚£ã‚¾ãƒ¼ãƒ³å†…ã�§ã�¯ç„¡åŠ¹
                if ((_pc.getZoneType() == 1) || (_targetPc.getZoneType() == 1)) {
                    return false;
                }
            }
            // å¯¾è±¡ã�ŒNPCã€�ä½¿ç”¨è€…ã�ŒNPCã�®å ´å�ˆã�¯100%æˆ�åŠŸ
            if ((_calcType == PC_NPC) || (_calcType == NPC_PC) || (_calcType == NPC_NPC)) {
                return true;
            }
        }

        // ã‚¢ãƒ¼ã‚¹ãƒ�ã‚¤ãƒ³ãƒ‰ä¸­ã�¯WBã€�ã‚­ãƒ£ãƒ³ã‚»ãƒ¬ãƒ¼ã‚·ãƒ§ãƒ³ä»¥å¤–ç„¡åŠ¹
        if ((_calcType == PC_PC) || (_calcType == NPC_PC)) {
            if (_targetPc.hasSkillEffect(EARTH_BIND)) {
                if ((skillId != WEAPON_BREAK) && (skillId != CANCELLATION)) {
                    return false;
                }
            }
        }
        else {
            if (_targetNpc.hasSkillEffect(EARTH_BIND)) {
                if ((skillId != WEAPON_BREAK) && (skillId != CANCELLATION)) {
                    return false;
                }
            }
        }

        probability = calcProbability(skillId);

        int rnd = Random.nextInt(100) + 1;
        if (probability > 90) {
            probability = 90; // æœ€é«˜æˆ�åŠŸçŽ‡ã‚’90%ã�¨ã�™ã‚‹ã€‚
        }

        if (probability >= rnd) {
            isSuccess = true;
        }
        else {
            isSuccess = false;
        }

        // ç¢ºçŽ‡ç³»é­”æ³•ãƒ¡ãƒƒã‚»ãƒ¼ã‚¸
        if (!Config.ALT_ATKMSG) {
            return isSuccess;
        }
        if (Config.ALT_ATKMSG) {
            if (((_calcType == PC_PC) || (_calcType == PC_NPC)) && !_pc.isGm()) {
                return isSuccess;
            }
            if (((_calcType == PC_PC) || (_calcType == NPC_PC)) && !_targetPc.isGm()) {
                return isSuccess;
            }
        }

        String msg0 = "";
        String msg1 = " æ–½æ”¾é­”æ³• ";
        String msg2 = "";
        String msg3 = "";
        String msg4 = "";

        if ((_calcType == PC_PC) || (_calcType == PC_NPC)) { // ã‚¢ã‚¿ãƒƒã‚«ãƒ¼ã�Œï¼°ï¼£ã�®å ´å�ˆ
            msg0 = _pc.getName() + " å°�";
        }
        else if (_calcType == NPC_PC) { // ã‚¢ã‚¿ãƒƒã‚«ãƒ¼ã�Œï¼®ï¼°ï¼£ã�®å ´å�ˆ
            msg0 = _npc.getName();
        }

        msg2 = "ï¼Œæ©ŸçŽ‡ï¼š" + probability + "%";
        if ((_calcType == NPC_PC) || (_calcType == PC_PC)) { // ã‚¿ãƒ¼ã‚²ãƒƒãƒˆã�Œï¼°ï¼£ã�®å ´å�ˆ
            msg4 = _targetPc.getName();
        }
        else if (_calcType == PC_NPC) { // ã‚¿ãƒ¼ã‚²ãƒƒãƒˆã�Œï¼®ï¼°ï¼£ã�®å ´å�ˆ
            msg4 = _targetNpc.getName();
        }
        if (isSuccess == true) {
            msg3 = "æˆ�åŠŸ";
        }
        else {
            msg3 = "å¤±æ•—";
        }

        // 0 4 1 3 2 æ”»æ“Šè€… å°� ç›®æ¨™ æ–½æ”¾é­”æ³• æˆ�åŠŸ/å¤±æ•—ï¼Œæ©ŸçŽ‡ï¼šX%ã€‚
        if ((_calcType == PC_PC) || (_calcType == PC_NPC)) {
            _pc.sendPackets(new S_ServerMessage(166, msg0, msg1, msg2, msg3, msg4));
        }
        // æ”»æ“Šè€… æ–½æ”¾é­”æ³• æˆ�åŠŸ/å¤±æ•—ï¼Œæ©ŸçŽ‡ï¼šX%ã€‚
        else if ((_calcType == NPC_PC)) {
            _targetPc.sendPackets(new S_ServerMessage(166, msg0, msg1, msg2, msg3, null));
        }

        return isSuccess;
    }

    private boolean checkZone(int skillId) {
        if ((_pc != null) && (_targetPc != null)) {
            if ((_pc.getZoneType() == 1) || (_targetPc.getZoneType() == 1)) { // ã‚»ãƒ¼ãƒ•ãƒ†ã‚£ãƒ¼ã‚¾ãƒ¼ãƒ³
                if ((skillId == WEAPON_BREAK) || (skillId == SLOW) || (skillId == CURSE_PARALYZE) || (skillId == MANA_DRAIN) || (skillId == DARKNESS)
                        || (skillId == WEAKNESS) || (skillId == DISEASE) || (skillId == DECAY_POTION) || (skillId == MASS_SLOW)
                        || (skillId == ENTANGLE) || (skillId == ERASE_MAGIC) || (skillId == EARTH_BIND) || (skillId == AREA_OF_SILENCE)
                        || (skillId == WIND_SHACKLE) || (skillId == STRIKER_GALE) || (skillId == SHOCK_STUN) || (skillId == FOG_OF_SLEEPING)
                        || (skillId == ICE_LANCE) || (skillId == FREEZING_BLIZZARD) || (skillId == FREEZING_BREATH) || (skillId == POLLUTE_WATER)
                        || (skillId == ELEMENTAL_FALL_DOWN) || (skillId == RETURN_TO_NATURE)
                        || (skillId == ICE_LANCE_COCKATRICE) || (skillId == ICE_LANCE_BASILISK)) {
                    return false;
                }
            }
        }
        return true;
    }

    private int calcProbability(int skillId) {
        L1Skills l1skills = SkillsTable.getInstance().getTemplate(skillId);
        int attackLevel = 0;
        int defenseLevel = 0;
        int probability = 0;

        if ((_calcType == PC_PC) || (_calcType == PC_NPC)) {
            attackLevel = _pc.getLevel();
        }
        else {
            attackLevel = _npc.getLevel();
        }

        if ((_calcType == PC_PC) || (_calcType == NPC_PC)) {
            defenseLevel = _targetPc.getLevel();
        }
        else {
            defenseLevel = _targetNpc.getLevel();
            if (skillId == RETURN_TO_NATURE) {
                if (_targetNpc instanceof L1SummonInstance) {
                    L1SummonInstance summon = (L1SummonInstance) _targetNpc;
                    defenseLevel = summon.getMaster().getLevel();
                }
            }
        }

        if ((skillId == ELEMENTAL_FALL_DOWN) || (skillId == RETURN_TO_NATURE) || (skillId == ENTANGLE) || (skillId == ERASE_MAGIC)
                || (skillId == AREA_OF_SILENCE) || (skillId == WIND_SHACKLE) || (skillId == STRIKER_GALE) || (skillId == POLLUTE_WATER)
                || (skillId == EARTH_BIND)) {
            // æˆ�åŠŸç¢ºçŽ‡ã�¯ é­”æ³•å›ºæœ‰ä¿‚æ•° Ã— LVå·® + åŸºæœ¬ç¢ºçŽ‡
            probability = (int) (((l1skills.getProbabilityDice()) / 10D) * (attackLevel - defenseLevel)) + l1skills.getProbabilityValue();

            // ã‚ªãƒªã‚¸ãƒŠãƒ«INTã�«ã‚ˆã‚‹é­”æ³•å‘½ä¸­
            if ((_calcType == PC_PC) || (_calcType == PC_NPC)) {
                probability += 2 * _pc.getOriginalMagicHit();
            }
        }
        else if (skillId == SHOCK_STUN) {
            probability = 100 + (attackLevel - defenseLevel) * 2;
        }
        else if (skillId == COUNTER_BARRIER) {
            int bonus = Math.max(0, (attackLevel - 60) / 4);

            probability = l1skills.getProbabilityValue() + bonus;

            if (_calcType == PC_PC || _calcType == PC_NPC) {
                probability += 2 * _pc.getOriginalMagicHit();
            }
        }
        else if (skillId == PHANTASM) {
            // Make sure PHANTASM is level based - [Hank]
            probability = l1skills.getProbabilityValue() + (attackLevel - defenseLevel) * 2;
        }
        else if (skillId == BONE_BREAK) {
            probability = 10 + (attackLevel - defenseLevel)*2;
        }

        else if (skillId == GUARD_BRAKE || skillId == RESIST_FEAR
                || skillId == HORROR_OF_DEATH) {
            // probability is based on http://forum.gamer.com.tw/Co.php?bsn=00842&sn=5283670
            probability = 50 + (attackLevel - defenseLevel) * 3;
            if (skillId == GUARD_BRAKE) probability -= 3;
            if (skillId == RESIST_FEAR) probability -= 5;

            if ((_calcType == PC_PC) || (_calcType == PC_NPC)) {
                probability += 2 * _pc.getOriginalMagicHit();
            }
        }
        // [Legends] - Adding in new armor break skill check for dark elf
        else if(skillId == ARMOR_BREAK)
        {
            probability = 30 + (attackLevel - defenseLevel) * 2;
        }
        else if (skillId == THUNDER_GRAB) {
            // success rate is probability_value(50%) * (attackerlvl/ defenselvl) + random(0ã€œ-20)
            probability = 50
                    * (attackLevel / Math.max(1, defenseLevel))
                    - Random.nextInt(21);

            if (_calcType == PC_PC || _calcType == PC_NPC) {
                probability += 2 * _pc.getOriginalMagicHit();
            }
        }
        else {
            int dice = l1skills.getProbabilityDice();
            int diceCount = 0;
            if ((_calcType == PC_PC) || (_calcType == PC_NPC)) {
                if (_pc.isWizard()) {
                    diceCount = getMagicBonus() + getMagicLevel() + 1;
                }
                else if (_pc.isElf()) {
                    diceCount = getMagicBonus() + getMagicLevel() - 1;
                }
                else {
                    diceCount = getMagicBonus() + getMagicLevel() - 1;
                }
            }
            else {
                diceCount = getMagicBonus() + getMagicLevel();
            }
            if (diceCount < 1) {
                diceCount = 1;
            }

            for (int i = 0; i < diceCount; i++) {
                probability += (Random.nextInt(dice) + 1);
            }
            probability = probability * getLeverage() / 10;

            // ã‚ªãƒªã‚¸ãƒŠãƒ«INTã�«ã‚ˆã‚‹é­”æ³•å‘½ä¸­
            if ((_calcType == PC_PC) || (_calcType == PC_NPC)) {
                probability += 2 * _pc.getOriginalMagicHit();
            }

            probability -= getTargetMr();

            if (skillId == TAMING_MONSTER) {
                double probabilityRevision = 1;
                if ((_targetNpc.getMaxHp() * 1 / 4) > _targetNpc.getCurrentHp()) {
                    probabilityRevision = 1.3;
                }
                else if ((_targetNpc.getMaxHp() * 2 / 4) > _targetNpc.getCurrentHp()) {
                    probabilityRevision = 1.2;
                }
                else if ((_targetNpc.getMaxHp() * 3 / 4) > _targetNpc.getCurrentHp()) {
                    probabilityRevision = 1.1;
                }
                probability *= probabilityRevision;
            }
        }

        // çŠ¶æ…‹ç•°å¸¸ã�«å¯¾ã�™ã‚‹è€�æ€§
        if (skillId == EARTH_BIND) {
            if ((_calcType == PC_PC) || (_calcType == NPC_PC)) {
                probability -= _targetPc.getRegistSustain();
            }
        }
        else if (skillId == SHOCK_STUN) {
            if ((_calcType == PC_PC) || (_calcType == NPC_PC)) {
                probability -= 2 * _targetPc.getRegistStun();
            }
        }
        else if (skillId == CURSE_PARALYZE) {
            if ((_calcType == PC_PC) || (_calcType == NPC_PC)) {
                probability -= _targetPc.getRegistStone();
            }
        }
        else if (skillId == FOG_OF_SLEEPING || skillId == PHANTASM) {
            if ((_calcType == PC_PC) || (_calcType == NPC_PC)) {
                probability -= _targetPc.getRegistSleep();
            }
        }
        else if ((skillId == ICE_LANCE) || (skillId == FREEZING_BLIZZARD) || (skillId == FREEZING_BREATH)
                || (skillId == ICE_LANCE_COCKATRICE) || (skillId == ICE_LANCE_BASILISK)) {
            if ((_calcType == PC_PC) || (_calcType == NPC_PC)) {
                probability -= _targetPc.getRegistFreeze();
                // æª¢æŸ¥ç„¡æ•µç‹€æ…‹
                for (int skillid : INVINCIBLE) {
                    if (_targetPc.hasSkillEffect(skillid)) {
                        probability = 0;
                        break;
                    }
                }
            }
        }
        else if ((skillId == CURSE_BLIND) || (skillId == DARKNESS) || (skillId == DARK_BLIND)) {
            if ((_calcType == PC_PC) || (_calcType == NPC_PC)) {
                probability -= _targetPc.getRegistBlind();
            }
        }

        return probability;
    }

    // æ“�æœ‰é€™äº›ç‹€æ…‹çš„, ä¸�æœƒå�—åˆ°å‚·å®³(ç„¡æ•µ)
    private static final int[] INVINCIBLE = {
            ABSOLUTE_BARRIER, ICE_LANCE, FREEZING_BLIZZARD, FREEZING_BREATH, EARTH_BIND, ICE_LANCE_COCKATRICE, ICE_LANCE_BASILISK
    };

        /* â– â– â– â– â– â– â– â– â– â– â– â– â– â–  é­”æ³•ãƒ€ãƒ¡ãƒ¼ã‚¸ç®—å‡º â– â– â– â– â– â– â– â– â– â– â– â– â– â–  */

    public int calcMagicDamage(int skillId) {
        int damage = 0;

        // æª¢æŸ¥ç„¡æ•µç‹€æ…‹
        for (int skillid : INVINCIBLE) {
            if (_target.hasSkillEffect(skillid)) {
                return damage;
            }
        }

        if ((_calcType == PC_PC) || (_calcType == NPC_PC)) {
            damage = calcPcMagicDamage(skillId);
        }
        else if ((_calcType == PC_NPC) || (_calcType == NPC_NPC)) {
            damage = calcNpcMagicDamage(skillId);
        }

        if (skillId != JOY_OF_PAIN) { // ç–¼ç—›çš„æ­¡æ„‰ç„¡è¦–é­”å…�
            damage = calcMrDefense(damage);
        }

        if (_calcType == PC_NPC && _pc.getDmgMessages()) {
            _pc.sendPackets(new S_SystemMessage(L1NamedSkill.getName(skillId) +
                    " Dealt:" + String.valueOf(damage)));
        }

        return damage;
    }

    // â—�â—�â—�â—� ãƒ—ãƒ¬ã‚¤ãƒ¤ãƒ¼ ã�¸ã�®ãƒ•ã‚¡ã‚¤ã‚¢ãƒ¼ã‚¦ã‚©ãƒ¼ãƒ«ã�®é­”æ³•ãƒ€ãƒ¡ãƒ¼ã‚¸ç®—å‡º â—�â—�â—�â—�
    public int calcPcFireWallDamage() {
        int dmg = 0;
        double attrDeffence = calcAttrResistance(L1Skills.ATTR_FIRE);
        L1Skills l1skills = SkillsTable.getInstance().getTemplate(FIRE_WALL);
        dmg = (int) ((1.0 - attrDeffence) * l1skills.getDamageValue());

        if (_targetPc.hasSkillEffect(ABSOLUTE_BARRIER)) {
            dmg = 0;
        }
        if (_targetPc.hasSkillEffect(ICE_LANCE)) {
            dmg = 0;
        }
        if (_targetPc.hasSkillEffect(FREEZING_BLIZZARD)) {
            dmg = 0;
        }
        if (_targetPc.hasSkillEffect(FREEZING_BREATH)) {
            dmg = 0;
        }
        if (_targetPc.hasSkillEffect(EARTH_BIND)) {
            dmg = 0;
        }
        if (_targetPc.hasSkillEffect(ICE_LANCE_COCKATRICE)) {
            dmg = 0;
        }
        if (_targetPc.hasSkillEffect(ICE_LANCE_BASILISK)) {
            dmg = 0;
        }

        if (dmg < 0) {
            dmg = 0;
        }

        return dmg;
    }

    // â—�â—�â—�â—� ï¼®ï¼°ï¼£ ã�¸ã�®ãƒ•ã‚¡ã‚¤ã‚¢ãƒ¼ã‚¦ã‚©ãƒ¼ãƒ«ã�®é­”æ³•ãƒ€ãƒ¡ãƒ¼ã‚¸ç®—å‡º â—�â—�â—�â—�
    public int calcNpcFireWallDamage() {
        int dmg = 0;
        double attrDeffence = calcAttrResistance(L1Skills.ATTR_FIRE);
        L1Skills l1skills = SkillsTable.getInstance().getTemplate(FIRE_WALL);
        dmg = (int) ((1.0 - attrDeffence) * l1skills.getDamageValue());

        if (_targetNpc.hasSkillEffect(ICE_LANCE)) {
            dmg = 0;
        }
        if (_targetNpc.hasSkillEffect(FREEZING_BLIZZARD)) {
            dmg = 0;
        }
        if (_targetNpc.hasSkillEffect(FREEZING_BREATH)) {
            dmg = 0;
        }
        if (_targetNpc.hasSkillEffect(EARTH_BIND)) {
            dmg = 0;
        }
        if (_targetNpc.hasSkillEffect(ICE_LANCE_COCKATRICE)) {
            dmg = 0;
        }
        if (_targetNpc.hasSkillEffect(ICE_LANCE_BASILISK)) {
            dmg = 0;
        }

        if (dmg < 0) {
            dmg = 0;
        }

        return dmg;
    }

    // â—�â—�â—�â—� ãƒ—ãƒ¬ã‚¤ãƒ¤ãƒ¼ãƒ»ï¼®ï¼°ï¼£ ã�‹ã‚‰ ãƒ—ãƒ¬ã‚¤ãƒ¤ãƒ¼ ã�¸ã�®é­”æ³•ãƒ€ãƒ¡ãƒ¼ã‚¸ç®—å‡º â—�â—�â—�â—�
    private int calcPcMagicDamage(int skillId) {
        int dmg = 0;
        // [Legends] - Removed final burn check as we are replacing it with Armor Break
        dmg = calcMagicDiceDamage(skillId);
        dmg = (dmg * getLeverage()) / 10;


        // å¿ƒé�ˆç ´å£žæ¶ˆè€—ç›®æ¨™5é»žMPé€ æˆ�5å€�ç²¾ç¥žå‚·å®³
        if (skillId == MIND_BREAK) {
            if (_targetPc.getCurrentMp() >= 10) {
                _targetPc.setCurrentMp(_targetPc.getCurrentMp() - 10);
                if (_calcType == PC_PC) {
                    dmg += _pc.getWis() * 5;
                } else if (_calcType == NPC_PC) {
                    dmg += _npc.getWis() * 5;
                }
            }
        }

        dmg -= _targetPc.getDamageReductionByArmor(); // é˜²å…·ã�«ã‚ˆã‚‹ãƒ€ãƒ¡ãƒ¼ã‚¸è»½æ¸›

        // é­”æ³•å¨ƒå¨ƒæ•ˆæžœ - å‚·å®³æ¸›å…�
        dmg -= L1MagicDoll.getDamageReductionByDoll(_targetPc);

        if (_targetPc.hasSkillEffect(COOKING_1_0_S) // æ–™ç�†ã�«ã‚ˆã‚‹ãƒ€ãƒ¡ãƒ¼ã‚¸è»½æ¸›
                || _targetPc.hasSkillEffect(COOKING_1_1_S) || _targetPc.hasSkillEffect(COOKING_1_2_S)
                || _targetPc.hasSkillEffect(COOKING_1_3_S)
                || _targetPc.hasSkillEffect(COOKING_1_4_S) || _targetPc.hasSkillEffect(COOKING_1_5_S)
                || _targetPc.hasSkillEffect(COOKING_1_6_S)
                || _targetPc.hasSkillEffect(COOKING_2_0_S) || _targetPc.hasSkillEffect(COOKING_2_1_S)
                || _targetPc.hasSkillEffect(COOKING_2_2_S)
                || _targetPc.hasSkillEffect(COOKING_2_3_S) || _targetPc.hasSkillEffect(COOKING_2_4_S)
                || _targetPc.hasSkillEffect(COOKING_2_5_S)
                || _targetPc.hasSkillEffect(COOKING_2_6_S) || _targetPc.hasSkillEffect(COOKING_3_0_S)
                || _targetPc.hasSkillEffect(COOKING_3_1_S)
                || _targetPc.hasSkillEffect(COOKING_3_2_S) || _targetPc.hasSkillEffect(COOKING_3_3_S)
                || _targetPc.hasSkillEffect(COOKING_3_4_S)
                || _targetPc.hasSkillEffect(COOKING_3_5_S) || _targetPc.hasSkillEffect(COOKING_3_6_S)) {
            dmg -= 5;
        }
        if (_targetPc.hasSkillEffect(COOKING_1_7_S) // ãƒ‡ã‚¶ãƒ¼ãƒˆã�«ã‚ˆã‚‹ãƒ€ãƒ¡ãƒ¼ã‚¸è»½æ¸›
                || _targetPc.hasSkillEffect(COOKING_2_7_S) || _targetPc.hasSkillEffect(COOKING_3_7_S)) {
            dmg -= 5;
        }

        if (_targetPc.hasSkillEffect(REDUCTION_ARMOR)) {
            int targetPcLvl = _targetPc.getLevel();
            if (targetPcLvl < 50) {
                targetPcLvl = 50;
            }
            dmg -= (targetPcLvl - 50) / 5 + 1;
        }
        if (_targetPc.hasSkillEffect(DRAGON_SKIN)) {
            dmg -= 5;
        }

        if (_targetPc.hasSkillEffect(PATIENCE)) {
            dmg -= 2;
        }

        if (_calcType == NPC_PC) { // ãƒšãƒƒãƒˆã€�ã‚µãƒ¢ãƒ³ã�‹ã‚‰ãƒ—ãƒ¬ã‚¤ãƒ¤ãƒ¼ã�«æ”»æ’ƒ
            boolean isNowWar = false;
            int castleId = L1CastleLocation.getCastleIdByArea(_targetPc);
            if (castleId > 0) {
                isNowWar = WarTimeController.getInstance().isNowWar(castleId);
            }
            if (!isNowWar) {
                if (_npc instanceof L1PetInstance) {
                    dmg /= 16;
                }
                if (_npc instanceof L1SummonInstance) {
                    L1SummonInstance summon = (L1SummonInstance) _npc;
                    if (summon.isExsistMaster()) {
                        dmg /= 16;
                    }
                }
            }
        }

        if (_targetPc.hasSkillEffect(IMMUNE_TO_HARM)) {
            dmg /= 2;
        }
        // ç–¼ç—›çš„æ­¡æ„‰å‚·å®³ï¼š(æœ€å¤§è¡€é‡� - ç›®å‰�è¡€é‡� /5)
        if (skillId == JOY_OF_PAIN) {
            int nowDamage = 0;
            if (_calcType == PC_PC) {
                nowDamage = _pc.getMaxHp() - _pc.getCurrentHp();
                if (nowDamage > 0) {
                    dmg = nowDamage / 5;
                }
            } else if (_calcType == NPC_PC) {
                nowDamage = _npc.getMaxHp() - _npc.getCurrentHp();
                if (nowDamage > 0) {
                    dmg = nowDamage / 5;
                }
            }
        }

        if (_targetPc.hasSkillEffect(ABSOLUTE_BARRIER)) {
            dmg = 0;
        } else if (_targetPc.hasSkillEffect(ICE_LANCE)) {
            dmg = 0;
        } else if (_targetPc.hasSkillEffect(FREEZING_BLIZZARD)) {
            dmg = 0;
        } else if (_targetPc.hasSkillEffect(FREEZING_BREATH)) {
            dmg = 0;
        } else if (_targetPc.hasSkillEffect(EARTH_BIND)) {
            dmg = 0;
        }

        // magic doll damage evasion
        if (L1MagicDoll.getDamageEvasionByDoll(_targetPc) > 0) {
            dmg = 0;
        }

        if (_calcType == NPC_PC) {
            if ((_npc instanceof L1PetInstance) || (_npc instanceof L1SummonInstance)) {
                // ç›®æ¨™åœ¨å®‰å�€ã€�æ”»æ“Šè€…åœ¨å®‰å�€ã€�NOPVP
                if ((_targetPc.getZoneType() == 1) || (_npc.getZoneType() == 1)
                        || (_targetPc.checkNonPvP(_targetPc, _npc))) {
                    dmg = 0;
                }
            }
        }

        if (_targetPc.hasSkillEffect(COUNTER_MIRROR)) {
            if (_calcType == PC_PC) {
                if (_targetPc.getWis() >= Random.nextInt(100)) {
                    _pc.sendPackets(new S_DoActionGFX(_pc.getId(), ActionCodes.ACTION_Damage));
                    _pc.broadcastPacket(new S_DoActionGFX(_pc.getId(), ActionCodes.ACTION_Damage));
                    _targetPc.sendPackets(new S_SkillSound(_targetPc.getId(), 4395));
                    _targetPc.broadcastPacket(new S_SkillSound(_targetPc.getId(), 4395));
                    _pc.receiveDamage(_targetPc, dmg, false);
                    dmg = 0;
                    _targetPc.killSkillEffectTimer(COUNTER_MIRROR);
                }
            }
            else if (_calcType == NPC_PC) {
                int npcId = _npc.getNpcTemplate().get_npcId();
                if ((npcId == 45681) || (npcId == 45682) || (npcId == 45683) || (npcId == 45684)) {}
                else if (!_npc.getNpcTemplate().get_IsErase()) {}
                else {
                    if (_targetPc.getWis() >= Random.nextInt(100)) {
                        _npc.broadcastPacket(new S_DoActionGFX(_npc.getId(), ActionCodes.ACTION_Damage));
                        _targetPc.sendPackets(new S_SkillSound(_targetPc.getId(), 4395));
                        _targetPc.broadcastPacket(new S_SkillSound(_targetPc.getId(), 4395));
                        _npc.receiveDamage(_targetPc, dmg);
                        dmg = 0;
                        _targetPc.killSkillEffectTimer(COUNTER_MIRROR);
                    }
                }
            }
        }

        if (dmg < 0) {
            dmg = 0;
        }
        if(_targetPc instanceof  L1PcInstance)
        {
            if(_targetPc.isGm())
            {
                _targetPc.sendPackets(new S_SystemMessage("Magic Dmg Recieved: " + dmg));
            }
        }
        return dmg;
    }

    // â—�â—�â—�â—� ãƒ—ãƒ¬ã‚¤ãƒ¤ãƒ¼ãƒ»ï¼®ï¼°ï¼£ ã�‹ã‚‰ ï¼®ï¼°ï¼£ ã�¸ã�®ãƒ€ãƒ¡ãƒ¼ã‚¸ç®—å‡º â—�â—�â—�â—�
    private int calcNpcMagicDamage(int skillId) {
        int dmg = 0;

        // [Legends] - Removed final burn check as we are replacing it with Armor Break
        dmg = calcMagicDiceDamage(skillId);
        dmg = (dmg * getLeverage()) / 10;


        // å¿ƒé�ˆç ´å£žæ¶ˆè€—ç›®æ¨™5é»žMPé€ æˆ�5å€�ç²¾ç¥žå‚·å®³
        if (skillId == MIND_BREAK) {
            if (_targetNpc.getCurrentMp() >= 10) {
                _targetNpc.setCurrentMp(_targetNpc.getCurrentMp() - 10);
                if (_calcType == PC_NPC) {
                    dmg += _pc.getWis() * 5;
                } else if (_calcType == NPC_NPC) {
                    dmg += _npc.getWis() * 5;
                }
            }
        }

        // ç–¼ç—›çš„æ­¡æ„‰å‚·å®³ï¼š(æœ€å¤§è¡€é‡� - ç›®å‰�è¡€é‡� /5)
        if (skillId == JOY_OF_PAIN) {
            int nowDamage = 0;
            if (_calcType == PC_NPC) {
                nowDamage = _pc.getMaxHp() - _pc.getCurrentHp();
                if (nowDamage > 0) {
                    dmg = nowDamage / 5;
                }
            } else if (_calcType == NPC_NPC) {
                nowDamage = _npc.getMaxHp() - _npc.getCurrentHp();
                if (nowDamage > 0) {
                    dmg = nowDamage / 5;
                }
            }
        }

        if (_calcType == PC_NPC) { // ãƒ—ãƒ¬ã‚¤ãƒ¤ãƒ¼ã�‹ã‚‰ãƒšãƒƒãƒˆã€�ã‚µãƒ¢ãƒ³ã�«æ”»æ’ƒ
            boolean isNowWar = false;
            int castleId = L1CastleLocation.getCastleIdByArea(_targetNpc);
            if (castleId > 0) {
                isNowWar = WarTimeController.getInstance().isNowWar(castleId);
            }
            if (!isNowWar) {
                if (_targetNpc instanceof L1PetInstance) {
                    dmg /= 8;
                }
                if (_targetNpc instanceof L1SummonInstance) {
                    L1SummonInstance summon = (L1SummonInstance) _targetNpc;
                    if (summon.isExsistMaster()) {
                        dmg /= 8;
                    }
                }
            }
        }

        if (_targetNpc.hasSkillEffect(ICE_LANCE)) {
            dmg = 0;
        } else if (_targetNpc.hasSkillEffect(FREEZING_BLIZZARD)) {
            dmg = 0;
        } else if (_targetNpc.hasSkillEffect(FREEZING_BREATH)) {
            dmg = 0;
        } else if (_targetNpc.hasSkillEffect(EARTH_BIND)) {
            dmg = 0;
        }

        // åˆ¤æ–·ç‰¹å®šç‹€æ…‹ä¸‹æ‰�å�¯æ”»æ“Š NPC
        if ((_calcType == PC_NPC) && (_targetNpc != null)) {
            if (_pc.isAttackMiss(_pc, _targetNpc.getNpcTemplate().get_npcId())) {
                dmg = 0;
            }
        }
        if (_calcType == NPC_NPC) {
            if (((_npc instanceof L1PetInstance) || (_npc instanceof L1SummonInstance))
                    && ((_targetNpc instanceof L1PetInstance) || (_targetNpc instanceof L1SummonInstance))) {
                // ç›®æ¨™åœ¨å®‰å�€ã€�æ”»æ“Šè€…åœ¨å®‰å�€
                if ((_targetNpc.getZoneType() == 1) || (_npc.getZoneType() == 1)) {
                    dmg = 0;
                }
                if (_targetNpc.getMaster() == _npc.getMaster()) {
                    dmg = 0;
                }
            }
        }

        if(_targetPc instanceof  L1PcInstance)
        {
            if(_targetPc.isGm())
            {
                _targetPc.sendPackets(new S_SystemMessage("Magic Dmg Recieved: " + dmg));
            }
        }
        return dmg;
    }

    // â—�â—�â—�â—� damage_diceã€�damage_dice_countã€�damage_valueã€�SPã�‹ã‚‰é­”æ³•ãƒ€ãƒ¡ãƒ¼ã‚¸ã‚’ç®—å‡º â—�â—�â—�â—�
    private int calcMagicDiceDamage(int skillId) {
        L1Skills l1skills = SkillsTable.getInstance().getTemplate(skillId);
        int dice = l1skills.getDamageDice();
        int diceCount = l1skills.getDamageDiceCount();
        int value = l1skills.getDamageValue();
        int magicDamage = 0;
        int charaIntelligence = 0;

        for (int i = 0; i < diceCount; i++) {
            magicDamage += (Random.nextInt(dice) + 1);
        }
        magicDamage += value;

        if ((_calcType == PC_PC) || (_calcType == PC_NPC)) {
            int weaponAddDmg = 0; // æ­¦å™¨ã�«ã‚ˆã‚‹è¿½åŠ ãƒ€ãƒ¡ãƒ¼ã‚¸
            L1ItemInstance weapon = _pc.getWeapon();
            if (weapon != null) {
                weaponAddDmg = weapon.getItem().getMagicDmgModifier();
            }
            magicDamage += weaponAddDmg;
        }

        if ((_calcType == PC_PC) || (_calcType == PC_NPC)) {
            int spByItem = _pc.getSp() - _pc.getTrueSp(); // ã‚¢ã‚¤ãƒ†ãƒ ã�«ã‚ˆã‚‹SPå¤‰å‹•
            charaIntelligence = _pc.getInt() + spByItem - 12;
        }
        else if ((_calcType == NPC_PC) || (_calcType == NPC_NPC)) {
            int spByItem = _npc.getSp() - _npc.getTrueSp(); // ã‚¢ã‚¤ãƒ†ãƒ ã�«ã‚ˆã‚‹SPå¤‰å‹•
            charaIntelligence = _npc.getInt() + spByItem - 12;
        }
        if (charaIntelligence < 1) {
            charaIntelligence = 1;
        }

        double attrDeffence = calcAttrResistance(l1skills.getAttr());

        double coefficient = (1.0 - attrDeffence + charaIntelligence * 3.0 / 32.0);
        if (coefficient < 0) {
            coefficient = 0;
        }

        magicDamage *= coefficient;

        double criticalCoefficient = 1.5; // é­”æ³•ã‚¯ãƒªãƒ†ã‚£ã‚«ãƒ«
        int rnd = Random.nextInt(100) + 1;
        if ((_calcType == PC_PC) || (_calcType == PC_NPC)) {
            if (l1skills.getSkillLevel() <= 6) {
                if (rnd <= (10 + _pc.getOriginalMagicCritical())) {
                    magicDamage *= criticalCoefficient;
                }
            }
        }

        if ((_calcType == PC_PC) || (_calcType == PC_NPC)) { // ã‚ªãƒªã‚¸ãƒŠãƒ«INTã�«ã‚ˆã‚‹é­”æ³•ãƒ€ãƒ¡ãƒ¼ã‚¸
            magicDamage += _pc.getOriginalMagicDamage();
        }
        if ((_calcType == PC_PC) || (_calcType == PC_NPC)) { // ã‚¢ãƒ�ã‚¿ãƒ¼ã�«ã‚ˆã‚‹è¿½åŠ ãƒ€ãƒ¡ãƒ¼ã‚¸
            if (_pc.hasSkillEffect(ILLUSION_AVATAR)) {
                magicDamage += 10;
            }
        }

        return magicDamage;
    }

    // â—�â—�â—�â—� ãƒ’ãƒ¼ãƒ«å›žå¾©é‡�ï¼ˆå¯¾ã‚¢ãƒ³ãƒ‡ãƒƒãƒ‰ã�«ã�¯ãƒ€ãƒ¡ãƒ¼ã‚¸ï¼‰ã‚’ç®—å‡º â—�â—�â—�â—�
    public int calcHealing(int skillId) {
        L1Skills l1skills = SkillsTable.getInstance().getTemplate(skillId);
        int dice = l1skills.getDamageDice();
        int value = l1skills.getDamageValue();
        int magicDamage = 0;

        int magicBonus = getMagicBonus();
        if (magicBonus > 10) {
            magicBonus = 10;
        }

        // If water elfs int is > 25, give 1 more die count for every int added, max is 13 - [Hank]
        if((skillId == NATURES_BLESSING) && (_pc.getInt() > 25))
        {
            value += _pc.getInt() - 25;
            if(value > 13)
            {
                value = 13;
            }
        }

        int diceCount = value + magicBonus;
        for (int i = 0; i < diceCount; i++) {
            magicDamage += (Random.nextInt(dice) + 1);
        }

        double alignmentRevision = 1.0;
        if (getLawful() > 0) {
            alignmentRevision += (getLawful() / 32768.0);
        }

        magicDamage *= alignmentRevision;

        magicDamage = (magicDamage * getLeverage()) / 10;

        return magicDamage;
    }

    // â—�â—�â—�â—� ï¼­ï¼²ã�«ã‚ˆã‚‹ãƒ€ãƒ¡ãƒ¼ã‚¸è»½æ¸› â—�â—�â—�â—�
    private int calcMrDefense(int dmg) {
        int mr = getTargetMr();

        double mrFloor = 0;
        if ((_calcType == PC_PC) || (_calcType == PC_NPC)) {
            if (mr <= 100) {
                mrFloor = Math.floor((mr - _pc.getOriginalMagicHit()) / 2);
            }
            else if (mr >= 100) {
                mrFloor = Math.floor((mr - _pc.getOriginalMagicHit()) / 10);
            }
            double mrCoefficient = 0;
            if (mr <= 100) {
                mrCoefficient = 1 - 0.01 * mrFloor;
            }
            else if (mr >= 100) {
                mrCoefficient = 0.6 - 0.01 * mrFloor;
            }
            dmg *= mrCoefficient;
        }
        else if ((_calcType == NPC_PC) || (_calcType == NPC_NPC)) {
            int rnd = Random.nextInt(100) + 1;
            if (mr >= rnd) {
                dmg /= 2;
            }
        }

        return dmg;
    }

    // â—�â—�â—�â—� å±žæ€§ã�«ã‚ˆã‚‹ãƒ€ãƒ¡ãƒ¼ã‚¸è»½æ¸› â—�â—�â—�â—�
    // attr:0.ç„¡å±žæ€§é­”æ³•,1.åœ°é­”æ³•,2.ç�«é­”æ³•,4.æ°´é­”æ³•,8.é¢¨é­”æ³•(,16.å…‰é­”æ³•)
    private double calcAttrResistance(int attr) {
        int resist = 0;
        if ((_calcType == PC_PC) || (_calcType == NPC_PC)) {
            if (attr == L1Skills.ATTR_EARTH) {
                resist = _targetPc.getEarth();
            }
            else if (attr == L1Skills.ATTR_FIRE) {
                resist = _targetPc.getFire();
            }
            else if (attr == L1Skills.ATTR_WATER) {
                resist = _targetPc.getWater();
            }
            else if (attr == L1Skills.ATTR_WIND) {
                resist = _targetPc.getWind();
            }
        }
        else if ((_calcType == PC_NPC) || (_calcType == NPC_NPC)) {}

        int resistFloor = (int) (0.32 * Math.abs(resist));
        if (resist >= 0) {
            resistFloor *= 1;
        }
        else {
            resistFloor *= -1;
        }

        double attrDeffence = resistFloor / 32.0;

        return attrDeffence;
    }

        /* â– â– â– â– â– â– â– â– â– â– â– â– â– â– â–  è¨ˆç®—çµ�æžœå��æ˜  â– â– â– â– â– â– â– â– â– â– â– â– â– â– â–  */

    public void commit(int damage, int drainMana) {
        if ((_calcType == PC_PC) || (_calcType == NPC_PC)) {
            commitPc(damage, drainMana);
        }
        else if ((_calcType == PC_NPC) || (_calcType == NPC_NPC)) {
            commitNpc(damage, drainMana);
        }

        // ãƒ€ãƒ¡ãƒ¼ã‚¸å€¤å�Šã�³å‘½ä¸­çŽ‡ç¢ºèª�ç”¨ãƒ¡ãƒƒã‚»ãƒ¼ã‚¸
        if (!Config.ALT_ATKMSG) {
            return;
        }
        if (Config.ALT_ATKMSG) {
            if (((_calcType == PC_PC) || (_calcType == PC_NPC)) && !_pc.isGm()) {
                return;
            }
            if ((_calcType == NPC_PC) && !_targetPc.isGm()) {
                return;
            }
        }

        String msg0 = "";
        String msg1 = " é€ æˆ� ";
        String msg2 = "";
        String msg3 = "";
        String msg4 = "";

        if ((_calcType == PC_PC) || (_calcType == PC_NPC)) {// ã‚¢ã‚¿ãƒƒã‚«ãƒ¼ã�Œï¼°ï¼£ã�®å ´å�ˆ
            msg0 = "é­”æ”» å°�";
        }
        else if (_calcType == NPC_PC) { // ã‚¢ã‚¿ãƒƒã‚«ãƒ¼ã�Œï¼®ï¼°ï¼£ã�®å ´å�ˆ
            msg0 = _npc.getName() + "(é­”æ”»)ï¼š";
        }

        if ((_calcType == NPC_PC) || (_calcType == PC_PC)) { // ã‚¿ãƒ¼ã‚²ãƒƒãƒˆã�Œï¼°ï¼£ã�®å ´å�ˆ
            msg4 = _targetPc.getName();
            msg2 = "ï¼Œå‰©é¤˜ " + _targetPc.getCurrentHp();
        }
        else if (_calcType == PC_NPC) { // ã‚¿ãƒ¼ã‚²ãƒƒãƒˆã�Œï¼®ï¼°ï¼£ã�®å ´å�ˆ
            msg4 = _targetNpc.getName();
            msg2 = "ï¼Œå‰©é¤˜ " + _targetNpc.getCurrentHp();
        }

        msg3 = damage  + " å‚·å®³";

        // é­”æ”» å°� ç›®æ¨™ é€ æˆ� X å‚·å®³ï¼Œå‰©é¤˜ Yã€‚
        if ((_calcType == PC_PC) || (_calcType == PC_NPC)) { // ã‚¢ã‚¿ãƒƒã‚«ãƒ¼ã�Œï¼°ï¼£ã�®å ´å�ˆ
            _pc.sendPackets(new S_ServerMessage(166, msg0, msg1, msg2, msg3, msg4)); // \f1%0ã�Œ%4%1%3
            // %2
        }
        // æ”»æ“Šè€…(é­”æ”»)ï¼š Xå‚·å®³ï¼Œå‰©é¤˜ Yã€‚
        else if ((_calcType == NPC_PC)) { // ã‚¿ãƒ¼ã‚²ãƒƒãƒˆã�Œï¼°ï¼£ã�®å ´å�ˆ
            _targetPc.sendPackets(new S_ServerMessage(166, msg0, null, msg2, msg3, null)); // \f1%0ã�Œ%4%1%3
            // %2
        }
    }

    // â—�â—�â—�â—� ãƒ—ãƒ¬ã‚¤ãƒ¤ãƒ¼ã�«è¨ˆç®—çµ�æžœã‚’å��æ˜  â—�â—�â—�â—�
    private void commitPc(int damage, int drainMana) {
        if (_calcType == PC_PC) {
            if ((drainMana > 0) && (_targetPc.getCurrentMp() > 0)) {
                if (drainMana > _targetPc.getCurrentMp()) {
                    drainMana = _targetPc.getCurrentMp();
                }
                int newMp = _pc.getCurrentMp() + drainMana;
                _pc.setCurrentMp(newMp);
            }
            _targetPc.receiveManaDamage(_pc, drainMana);
            _targetPc.receiveDamage(_pc, damage, true);
        }
        else if (_calcType == NPC_PC) {
            _targetPc.receiveDamage(_npc, damage, true);
        }
    }

    // â—�â—�â—�â—� ï¼®ï¼°ï¼£ã�«è¨ˆç®—çµ�æžœã‚’å��æ˜  â—�â—�â—�â—�
    private void commitNpc(int damage, int drainMana) {
        if (_calcType == PC_NPC) {
            if (drainMana > 0) {
                int drainValue = _targetNpc.drainMana(drainMana);
                int newMp = _pc.getCurrentMp() + drainValue;
                _pc.setCurrentMp(newMp);
            }
            _targetNpc.ReceiveManaDamage(_pc, drainMana);
            _targetNpc.receiveDamage(_pc, damage);
        }
        else if (_calcType == NPC_NPC) {
            _targetNpc.receiveDamage(_npc, damage);
        }
    }
}