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

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.server.ActionCodes;
import l1j.server.server.datatables.PolyTable;
import l1j.server.server.datatables.SkillsTable;
import l1j.server.server.model.*;
import l1j.server.server.model.Instance.L1AuctionBoardInstance;
import l1j.server.server.model.Instance.L1BoardInstance;
import l1j.server.server.model.Instance.L1CrownInstance;
import l1j.server.server.model.Instance.L1DollInstance;
import l1j.server.server.model.Instance.L1DoorInstance;
import l1j.server.server.model.Instance.L1DwarfInstance;
import l1j.server.server.model.Instance.L1EffectInstance;
import l1j.server.server.model.Instance.L1FieldObjectInstance;
import l1j.server.server.model.Instance.L1FurnitureInstance;
import l1j.server.server.model.Instance.L1GuardInstance;
import l1j.server.server.model.Instance.L1HousekeeperInstance;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1MerchantInstance;
import l1j.server.server.model.Instance.L1MonsterInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.model.Instance.L1SummonInstance;
import l1j.server.server.model.Instance.L1TeleporterInstance;
import l1j.server.server.model.Instance.L1TowerInstance;
import l1j.server.server.model.trap.L1WorldTraps;
import l1j.server.server.serverpackets.*;
import l1j.server.server.templates.L1BookMark;
import l1j.server.server.templates.L1Skills;
import l1j.server.server.utils.Random;
import l1j.server.server.utils.collections.IntArrays;
import l1j.server.server.utils.collections.Lists;
import static l1j.server.server.model.skill.L1SkillId.*;
import static l1j.server.server.model.item.L1ItemId.*;
import l1j.server.server.random.RandomGenerator;
import l1j.server.server.random.RandomGeneratorFactory;
import sun.print.resources.serviceui_sv;

public class L1SkillUse {
    public static final int TYPE_NORMAL = 0;

    public static final int TYPE_LOGIN = 1;

    public static final int TYPE_SPELLSC = 2;

    public static final int TYPE_NPCBUFF = 3;

    public static final int TYPE_GMBUFF = 4;

    private L1Skills _skill;

    private int _skillId;

    private int _earthBindDuration;

    private int _dmg;

    private int _getBuffDuration;

    private int _shockStunDuration;

    private int _boneBreakDuration;

    private int _getBuffIconDuration;

    private int _targetID;

    private int _mpConsume = 0;

    private int _hpConsume = 0;

    private int _targetX = 0;

    private int _targetY = 0;

    private String _message = null;

    private int _skillTime = 0;

    private int _type = 0;

    private boolean _isPK = false;

    private int _bookmarkId = 0;

    private int _itemobjid = 0;

    private boolean _checkedUseSkill = false; // äº‹å‰�ãƒ�ã‚§ãƒƒã‚¯æ¸ˆã�¿ã�‹

    private int _leverage = 10; // 1/10å€�ã�ªã�®ã�§10ã�§1å€�

    private int _skillRanged = 0;

    private int _skillArea = 0;

    private boolean _isFreeze = false;

    private boolean _isCounterMagic = true;

    private boolean _isGlanceCheckFail = false;

    private L1Character _user = null;

    private L1Character _target = null;

    private L1PcInstance _player = null;

    private L1NpcInstance _npc = null;

    private int _calcType;

    private static final int PC_PC = 1;

    private static final int PC_NPC = 2;

    private static final int NPC_PC = 3;

    private static final int NPC_NPC = 4;

    private List<TargetStatus> _targetList;

    private int _actid = 0;

    private int _gfxid = 0;

    private static Logger _log = Logger.getLogger(L1SkillUse.class.getName());

    private static final S_ServerMessage SkillFailed = new S_ServerMessage(280);

    private static final int[] CAST_WITH_INVIS =
            { 1, 2, 3, 5, 8, 9, 12, 13, 14, 19, 21, 26, 31, 32, 35, 37, 42, 43, 44, 48, 49, 52, 54, 55, 57, 60, 61, 63, 67, 68, 69, 72, 73, 75, 78, 79,
                    REDUCTION_ARMOR, BOUNCE_ATTACK, SOLID_CARRIAGE, COUNTER_BARRIER, 97, 98, 99, 100, 101, 102, 104, 105, 106, 107, 109, 110, 111, 113, 114,
                    115, 116, 117, 118, 129, 130, 131, 133, 134, 137, 138, 146, 147, 148, 149, 150, 151, 155, 156, 158, 159, 163, 164, 165, 166, 168, 169,
                    170, 171, SOUL_OF_FLAME, ADDITIONAL_FIRE, DRAGON_SKIN, AWAKEN_ANTHARAS, AWAKEN_FAFURION, AWAKEN_VALAKAS, MIRROR_IMAGE, ILLUSION_OGRE,
                    ILLUSION_LICH, PATIENCE, ILLUSION_DIA_GOLEM, INSIGHT, ILLUSION_AVATAR };

    // è¨­å®šé­”æ³•å±�éšœä¸�å�¯æŠµæ“‹çš„é­”æ³•
    private static final int[] EXCEPT_COUNTER_MAGIC =
            { 1, 2, 3, 5, 8, 9, 12, 13, 14, 19, 21, 26, 31, 32, 35, 37, 42, 43, 44, 48, 49, 52, 54, 55, 57, 60, 61, 63, 67, 68, 69, 72, 73, 75, 78, 79,
                    SHOCK_STUN, REDUCTION_ARMOR, BOUNCE_ATTACK, SOLID_CARRIAGE, COUNTER_BARRIER, 97, 98, 99, 100, 101, 102, 104, 105, 106, 107, 109, 110,
                    111, 113, 114, 115, 116, 117, 118, 129, 130, 131, 132, 134, 137, 138, 146, 147, 148, 149, 150, 151, 155, 156, 158, 159, 161, 163, 164,
                    165, 166, 168, 169, 170, 171, SOUL_OF_FLAME, ADDITIONAL_FIRE, DRAGON_SKIN, AWAKEN_ANTHARAS, AWAKEN_FAFURION, AWAKEN_VALAKAS,
                    MIRROR_IMAGE, ILLUSION_OGRE, ILLUSION_LICH, PATIENCE, 10026, 10027, ILLUSION_DIA_GOLEM, INSIGHT, ILLUSION_AVATAR, 10028, 10029 };

    private static final int [] CAST_WITH_SILENCE =
            {
                    SHOCK_STUN, REDUCTION_ARMOR, BOUNCE_ATTACK, SOLID_CARRIAGE,
                    COUNTER_BARRIER
            };

    static {
        Arrays.sort(CAST_WITH_SILENCE);
    }

    public L1SkillUse() {
    }

    private static class TargetStatus {
        private L1Character _target = null;

        private boolean _isCalc = true; // ãƒ€ãƒ¡ãƒ¼ã‚¸ã‚„ç¢ºçŽ‡é­”æ³•ã�®è¨ˆç®—ã‚’ã�™ã‚‹å¿…è¦�ã�Œã�‚ã‚‹ã�‹ï¼Ÿ

        public TargetStatus(L1Character _cha) {
            _target = _cha;
        }

        public TargetStatus(L1Character _cha, boolean _flg) {
            _isCalc = _flg;
        }

        public L1Character getTarget() {
            return _target;
        }

        public boolean isCalc() {
            return _isCalc;
        }
    }

    /*
     * æ”»æ“Šè·�é›¢è®Šæ›´ã€‚
     */
    public void setSkillRanged(int i) {
        _skillRanged = i;
    }

    public int getSkillRanged() {
        if (_skillRanged == 0) {
            return _skill.getRanged();
        }
        return _skillRanged;
    }

    /*
     * æ”»æ“Šç¯„åœ�è®Šæ›´ã€‚
     */
    public void setSkillArea(int i) {
        _skillArea = i;
    }

    public int getSkillArea() {
        if (_skillArea == 0) {
            return _skill.getArea();
        }
        return _skillArea;
    }

    /*
     * 1/10å€�ã�§è¡¨ç�¾ã�™ã‚‹ã€‚
     */
    public void setLeverage(int i) {
        _leverage = i;
    }

    public int getLeverage() {
        return _leverage;
    }

    private boolean isCheckedUseSkill() {
        return _checkedUseSkill;
    }

    private void setCheckedUseSkill(boolean flg) {
        _checkedUseSkill = flg;
    }

    public boolean checkUseSkill(L1PcInstance player, int skillid, int target_id, int x, int y, String message, int time, int type,
                                 L1Character attacker) {
        return checkUseSkill(player, skillid, target_id, x, y, message, time, type, attacker, 0, 0, 0);
    }

    public boolean checkUseSkill(L1PcInstance player, int skillid, int target_id, int x, int y, String message, int time, int type,
                                 L1Character attacker, int actid, int gfxid, int mpConsume) {
        // åˆ�æœŸè¨­å®šã�“ã�“ã�‹ã‚‰
        setCheckedUseSkill(true);
        _targetList = Lists.newList(); // ã‚¿ãƒ¼ã‚²ãƒƒãƒˆãƒªã‚¹ãƒˆã�®åˆ�æœŸåŒ–

        _skill = SkillsTable.getInstance().getTemplate(skillid);
        _skillId = skillid;
        _targetX = x;
        _targetY = y;
        _message = message;
        _skillTime = time;
        _type = type;
        _actid = actid;
        _gfxid = gfxid;
        _mpConsume = mpConsume;
        boolean checkedResult = true;

        if (attacker == null) {
            // pc
            _player = player;
            _user = _player;
        }
        else {
            // npc
            _npc = (L1NpcInstance) attacker;
            _user = _npc;
        }

        if (_skill.getTarget().equals("none")) {
            _targetID = _user.getId();
            _targetX = _user.getX();
            _targetY = _user.getY();
        }
        else {
            _targetID = target_id;
        }

        if (type == TYPE_NORMAL) { // é€šå¸¸ã�®é­”æ³•ä½¿ç”¨æ™‚
            checkedResult = isNormalSkillUsable();
        }
        else if (type == TYPE_SPELLSC) { // ã‚¹ãƒšãƒ«ã‚¹ã‚¯ãƒ­ãƒ¼ãƒ«ä½¿ç”¨æ™‚
            checkedResult = isSpellScrollUsable();
        }
        else if (type == TYPE_NPCBUFF) {
            checkedResult = true;
        }
        if (!checkedResult) {
            return false;
        }

        // ãƒ•ã‚¡ã‚¤ã‚¢ãƒ¼ã‚¦ã‚©ãƒ¼ãƒ«ã€�ãƒ©ã‚¤ãƒ•ã‚¹ãƒˆãƒªãƒ¼ãƒ ã�¯è© å”±å¯¾è±¡ã�Œåº§æ¨™
        // ã‚­ãƒ¥ãƒ¼ãƒ–ã�¯è© å”±è€…ã�®åº§æ¨™ã�«é…�ç½®ã�•ã‚Œã‚‹ã�Ÿã‚�ä¾‹å¤–
        if ((_skillId == FIRE_WALL) || (_skillId == LIFE_STREAM) || (_skillId == TRUE_TARGET)) {
            return true;
        }

        L1Object l1object = L1World.getInstance().findObject(_targetID);
        if (l1object instanceof L1ItemInstance) {
            _log.fine("skill target item name: " + ((L1ItemInstance) l1object).getViewName());
            // ã‚¹ã‚­ãƒ«ã‚¿ãƒ¼ã‚²ãƒƒãƒˆã�Œç²¾éœŠã�®çŸ³ã�«ã�ªã‚‹ã�“ã�¨ã�Œã�‚ã‚‹ã€‚
            // Linuxç’°å¢ƒã�§ç¢ºèª�ï¼ˆWindowsã�§ã�¯æœªç¢ºèª�ï¼‰
            // 2008.5.4è¿½è¨˜ï¼šåœ°é�¢ã�®ã‚¢ã‚¤ãƒ†ãƒ ã�«é­”æ³•ã‚’ä½¿ã�†ã�¨ã�ªã‚‹ã€‚ç¶™ç¶šã�—ã�¦ã‚‚ã‚¨ãƒ©ãƒ¼ã�«ã�ªã‚‹ã� ã�‘ã�ªã�®ã�§return
            return false;
        }
        if (_user instanceof L1PcInstance) {
            if (l1object instanceof L1PcInstance) {
                _calcType = PC_PC;
            }
            else {
                _calcType = PC_NPC;
            }
        }
        else if (_user instanceof L1NpcInstance) {
            if (l1object instanceof L1PcInstance) {
                _calcType = NPC_PC;
            }
            else if (_skill.getTarget().equals("none")) {
                _calcType = NPC_PC;
            }
            else {
                _calcType = NPC_NPC;
            }
        }

        // ãƒ†ãƒ¬ãƒ�ãƒ¼ãƒˆã€�ãƒžã‚¹ãƒ†ãƒ¬ãƒ�ãƒ¼ãƒˆã�¯å¯¾è±¡ã�Œãƒ–ãƒƒã‚¯ãƒžãƒ¼ã‚¯ID
        if ((_skillId == TELEPORT) || (_skillId == MASS_TELEPORT)) {
            _bookmarkId = target_id;
        }
        // å¯¾è±¡ã�Œã‚¢ã‚¤ãƒ†ãƒ ã�®ã‚¹ã‚­ãƒ«
        if ((_skillId == CREATE_MAGICAL_WEAPON) || (_skillId == BRING_STONE) || (_skillId == BLESSED_ARMOR) || (_skillId == ENCHANT_WEAPON)
                || (_skillId == SHADOW_FANG)) {
            _itemobjid = target_id;
        }
        _target = (L1Character) l1object;

        if (!(_target instanceof L1MonsterInstance) && _skill.getTarget().equals("attack") && (_user.getId() != target_id)) {
            _isPK = true; // ã‚¿ãƒ¼ã‚²ãƒƒãƒˆã�Œãƒ¢ãƒ³ã‚¹ã‚¿ãƒ¼ä»¥å¤–ã�§æ”»æ’ƒç³»ã‚¹ã‚­ãƒ«ã�§ã€�è‡ªåˆ†ä»¥å¤–ã�®å ´å�ˆPKãƒ¢ãƒ¼ãƒ‰ã�¨ã�™ã‚‹ã€‚
        }

        // åˆ�æœŸè¨­å®šã�“ã�“ã�¾ã�§

        // äº‹å‰�ãƒ�ã‚§ãƒƒã‚¯
        if (!(l1object instanceof L1Character)) { // ã‚¿ãƒ¼ã‚²ãƒƒãƒˆã�Œã‚­ãƒ£ãƒ©ã‚¯ã‚¿ãƒ¼ä»¥å¤–ã�®å ´å�ˆä½•ã‚‚ã�—ã�ªã�„ã€‚
            checkedResult = false;
        }
        makeTargetList(); // ã‚¿ãƒ¼ã‚²ãƒƒãƒˆã�®ä¸€è¦§ã‚’ä½œæˆ�
        if (_targetList.isEmpty() && (_user instanceof L1NpcInstance)) {
            checkedResult = false;
        }
        // äº‹å‰�ãƒ�ã‚§ãƒƒã‚¯ã�“ã�“ã�¾ã�§
        return checkedResult;
    }

    /**
     * é€šå¸¸ã�®ã‚¹ã‚­ãƒ«ä½¿ç”¨æ™‚ã�«ä½¿ç”¨è€…ã�®çŠ¶æ…‹ã�‹ã‚‰ã‚¹ã‚­ãƒ«ã�Œä½¿ç”¨å�¯èƒ½ã�§ã�‚ã‚‹ã�‹åˆ¤æ–­ã�™ã‚‹
     *
     * @return false ã‚¹ã‚­ãƒ«ã�Œä½¿ç”¨ä¸�å�¯èƒ½ã�ªçŠ¶æ…‹ã�§ã�‚ã‚‹å ´å�ˆ
     */
    private boolean isNormalSkillUsable() {
        // ã‚¹ã‚­ãƒ«ä½¿ç”¨è€…ã�ŒPCã�®å ´å�ˆã�®ãƒ�ã‚§ãƒƒã‚¯
        if (_user instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) _user;

            if (pc.isTeleport()) { // å‚³é€�ä¸­
                return false;
            }
            if (pc.isParalyzed()) { // éº»ç—ºãƒ»å‡�çµ�çŠ¶æ…‹ã�‹
                return false;
            }
            if ((pc.isInvisble() || pc.isInvisDelay()) && !isInvisUsableSkill()) { // éš±èº«ä¸­ç„¡æ³•ä½¿ç”¨æŠ€èƒ½
                return false;
            }
            if (pc.getInventory().getWeight242() >= 197) { // \f1ä½ æ”œå¸¶å¤ªå¤šç‰©å“�ï¼Œå› æ­¤ç„¡æ³•ä½¿ç”¨æ³•è¡“ã€‚
                pc.sendPackets(new S_ServerMessage(316));
                return false;
            }
            int polyId = pc.getTempCharGfx();
            L1PolyMorph poly = PolyTable.getInstance().getTemplate(polyId);
            // é­”æ³•ã�Œä½¿ã�ˆã�ªã�„å¤‰èº«
            if ((poly != null) && !poly.canUseSkill()) {
                pc.sendPackets(new S_ServerMessage(285)); // \f1åœ¨æ­¤ç‹€æ…‹ä¸‹ç„¡æ³•ä½¿ç”¨é­”æ³•ã€‚
                return false;
            }

            if (!isAttrAgrees()) { // ç²¾éœŠé­”æ³•ã�§ã€�å±žæ€§ã�Œä¸€è‡´ã�—ã�ªã�‘ã‚Œã�°ä½•ã‚‚ã�—ã�ªã�„ã€‚
                return false;
            }

            if ((_skillId == ELEMENTAL_PROTECTION) && (pc.getElfAttr() == 0)) {
                pc.sendPackets(new S_ServerMessage(280)); // \f1æ–½å’’å¤±æ•—ã€‚
                return false;
            }

			/* æ°´ä¸­ç„¡æ³•ä½¿ç”¨ç�«å±¬æ€§é­”æ³• */
            if (pc.getMap().isUnderwater() && _skill.getAttr() == 2) {
                pc.sendPackets(new S_ServerMessage(280)); // \f1æ–½å’’å¤±æ•—ã€‚
                return false;
            }

            // ã‚¹ã‚­ãƒ«ãƒ‡ã‚£ãƒ¬ã‚¤ä¸­ä½¿ç”¨ä¸�å�¯
            if (pc.isSkillDelay()) {
                return false;
            }

            // é­”æ³•å°�å�°ã€�å°�å�°ç¦�åœ°ã€�å�¡æ¯’ã€�å¹»æƒ³
            if ((pc.hasSkillEffect(SILENCE) ||
                    pc.hasSkillEffect(AREA_OF_SILENCE) ||
                    pc.hasSkillEffect(STATUS_POISON_SILENCE)||
                    pc.hasSkillEffect(CONFUSION_ING)) &&
                    !IntArrays.sContains(CAST_WITH_SILENCE, _skillId)) {
                pc.sendPackets(new S_ServerMessage(285));
                return false;
            }

            // DIGã�¯ãƒ­ã‚¦ãƒ•ãƒ«ã�§ã�®ã�¿ä½¿ç”¨å�¯
            if ((_skillId == DISINTEGRATE) && (pc.getLawful() < 500)) {
                // ã�“ã�®ãƒ¡ãƒƒã‚»ãƒ¼ã‚¸ã�§ã�‚ã�£ã�¦ã‚‹ã�‹æœªç¢ºèª�
                pc.sendPackets(new S_ServerMessage(352, "$967")); // è‹¥è¦�ä½¿ç”¨é€™å€‹æ³•è¡“ï¼Œå±¬æ€§å¿…é ˆæˆ�ç‚º (æ­£ç¾©)ã€‚
                return false;
            }

            // å�Œã�˜ã‚­ãƒ¥ãƒ¼ãƒ–ã�¯åŠ¹æžœç¯„å›²å¤–ã�§ã�‚ã‚Œã�°é…�ç½®å�¯èƒ½
            if ((_skillId == CUBE_IGNITION) || (_skillId == CUBE_QUAKE) || (_skillId == CUBE_SHOCK) || (_skillId == CUBE_BALANCE)) {
                boolean isNearSameCube = false;
                int gfxId = 0;
                for (L1Object obj : L1World.getInstance().getVisibleObjects(pc, 3)) {
                    if (obj instanceof L1EffectInstance) {
                        L1EffectInstance effect = (L1EffectInstance) obj;
                        gfxId = effect.getGfxId();
                        if (((_skillId == CUBE_IGNITION) && (gfxId == 6706)) || ((_skillId == CUBE_QUAKE) && (gfxId == 6712))
                                || ((_skillId == CUBE_SHOCK) && (gfxId == 6718)) || ((_skillId == CUBE_BALANCE) && (gfxId == 6724))) {
                            isNearSameCube = true;
                            break;
                        }
                    }
                }
                if (isNearSameCube) {
                    pc.sendPackets(new S_ServerMessage(1412)); // å·²åœ¨åœ°æ�¿ä¸Šå�¬å–šäº†é­”æ³•ç«‹æ–¹å¡Šã€‚
                    return false;
                }
            }
            // [Mike] Fixes for various spells to ensure that weapons, shields are equipped to use.
            if (_skillId == SOLID_CARRIAGE) {
                L1PcInventory Inventory = pc.getInventory();
                if (Inventory.getItemEquipped(2, 7) == null) {
                    pc.sendPackets(new S_SystemMessage(
                            "Solid Carriage requires a Shield on to use."));
                    return false;
                }

            }

            if (_skillId == BONE_BREAK && pc.getWeapon() == null) {
                pc.sendPackets(new S_SystemMessage(
                        "Bonebreak requires a Weapon on to use."));
                return false;
            }

            if (_skillId == THUNDER_GRAB && pc.getWeapon() == null) {
                pc.sendPackets(new S_SystemMessage(
                        "ThunderGrab requires a Weapon on to use."));
                return false;
            }

            if (_skillId == CONFUSION && pc.getWeapon() == null) {
                pc.sendPackets(new S_SystemMessage(
                        "Confusion requires a Weapon on to use."));
                return false;
            }

            if (_skillId == SMASH && pc.getWeapon() == null) {
                pc.sendPackets(new S_SystemMessage(
                        "Smash requires a Weapon on to use."));
                return false;
            }

            if (_skillId == ARM_BREAKER && pc.getWeapon() == null) {
                pc.sendPackets(new S_SystemMessage(
                        "Arm Breaker requires a Weapon on to use."));
                return false;
            }

            // è¦ºé†’ç‹€æ…‹ - é�žè¦ºé†’æŠ€èƒ½ç„¡æ³•ä½¿ç”¨
            //[Legends] - Disable Preventing them from casting with buffs on.
            /*
            if (((pc.getAwakeSkillId() == AWAKEN_ANTHARAS) && (_skillId != AWAKEN_ANTHARAS))
                    || ((pc.getAwakeSkillId() == AWAKEN_FAFURION) && (_skillId != AWAKEN_FAFURION))
                    || ((pc.getAwakeSkillId() == AWAKEN_VALAKAS) && (_skillId != AWAKEN_VALAKAS))
                    && (_skillId != MAGMA_BREATH) && (_skillId != SHOCK_SKIN) && (_skillId != FREEZING_BREATH)) {
                pc.sendPackets(new S_ServerMessage(1385)); // ç›®å‰�ç‹€æ…‹ä¸­ç„¡æ³•ä½¿ç”¨è¦ºé†’é­”æ³•ã€‚
                return false;
            }*/

            // [Mike] Fix ItemConsume when casting spells..
            if ((isItemConsume() == false) && !_player.isGm()) { // æ³•è¡“æ¶ˆè€—é�“å…·åˆ¤æ–·ã€‚
                _player.sendPackets(new S_ServerMessage(299)); // \f1æ–½æ”¾é­”æ³•æ‰€éœ€æ��æ–™ä¸�è¶³ã€‚
                return false;
            }
        }
        // ã‚¹ã‚­ãƒ«ä½¿ç”¨è€…ã�ŒNPCã�®å ´å�ˆã�®ãƒ�ã‚§ãƒƒã‚¯
        else if (_user instanceof L1NpcInstance) {

            // ã‚µã‚¤ãƒ¬ãƒ³ã‚¹çŠ¶æ…‹ã�§ã�¯ä½¿ç”¨ä¸�å�¯
            if (_user.hasSkillEffect(SILENCE)) {
                // NPCã�«ã‚µã‚¤ãƒ¬ãƒ³ã‚¹ã�ŒæŽ›ã�‹ã�£ã�¦ã�„ã‚‹å ´å�ˆã�¯1å›žã� ã�‘ä½¿ç”¨ã‚’ã‚­ãƒ£ãƒ³ã‚»ãƒ«ã�•ã�›ã‚‹åŠ¹æžœã€‚
                _user.removeSkillEffect(SILENCE);
                return false;
            }
        }

        // PCã€�NPCå…±é€šæª¢æŸ¥HPã€�MPæ˜¯å�¦è¶³å¤ 
        if (!isHPMPConsume()) { // èŠ±è²»çš„HPã€�MPè¨ˆç®—
            return false;
        }
        return true;
    }

    /**
     * ã‚¹ãƒšãƒ«ã‚¹ã‚¯ãƒ­ãƒ¼ãƒ«ä½¿ç”¨æ™‚ã�«ä½¿ç”¨è€…ã�®çŠ¶æ…‹ã�‹ã‚‰ã‚¹ã‚­ãƒ«ã�Œä½¿ç”¨å�¯èƒ½ã�§ã�‚ã‚‹ã�‹åˆ¤æ–­ã�™ã‚‹
     *
     * @return false ã‚¹ã‚­ãƒ«ã�Œä½¿ç”¨ä¸�å�¯èƒ½ã�ªçŠ¶æ…‹ã�§ã�‚ã‚‹å ´å�ˆ
     */
    private boolean isSpellScrollUsable() {
        // ã‚¹ãƒšãƒ«ã‚¹ã‚¯ãƒ­ãƒ¼ãƒ«ã‚’ä½¿ç”¨ã�™ã‚‹ã�®ã�¯PCã�®ã�¿
        L1PcInstance pc = (L1PcInstance) _user;

        if (pc.isTeleport()) { // å‚³é€�ä¸­
            return false;
        }

        if (pc.isParalyzed()) { // éº»ç—ºãƒ»å‡�çµ�çŠ¶æ…‹ã�‹
            return false;
        }

        // ã‚¤ãƒ³ãƒ“ã‚¸ä¸­ã�«ä½¿ç”¨ä¸�å�¯ã�®ã‚¹ã‚­ãƒ«
        if ((pc.isInvisble() || pc.isInvisDelay()) && !isInvisUsableSkill()) {
            return false;
        }

        return true;
    }

    // ã‚¤ãƒ³ãƒ“ã‚¸ä¸­ã�«ä½¿ç”¨å�¯èƒ½ã�ªã‚¹ã‚­ãƒ«ã�‹ã‚’è¿”ã�™
    private boolean isInvisUsableSkill() {
        for (int skillId : CAST_WITH_INVIS) {
            if (skillId == _skillId) {
                return true;
            }
        }
        return false;
    }

    public void handleCommands(L1PcInstance player, int skillId, int targetId, int x, int y, String message, int timeSecs, int type) {
        L1Character attacker = null;
        handleCommands(player, skillId, targetId, x, y, message, timeSecs, type, attacker);
    }

    public void handleCommands(L1PcInstance player, int skillId, int targetId, int x, int y, String message, int timeSecs, int type,
                               L1Character attacker) {

        try {
            // äº‹å‰�ãƒ�ã‚§ãƒƒã‚¯ã‚’ã�—ã�¦ã�„ã‚‹ã�‹ï¼Ÿ
            if (!isCheckedUseSkill()) {
                boolean isUseSkill = checkUseSkill(player, skillId, targetId, x, y, message, timeSecs, type, attacker);

                if (!isUseSkill) {
                    failSkill();
                    return;
                }
            }

            if (type == TYPE_NORMAL) { // é­”æ³•è© å”±æ™‚
                if (!_isGlanceCheckFail || (getSkillArea() > 0) || _skill.getTarget().equals("none")) {
                    if ( _skill.getSkillId()== HOLY_WALK && _target.hasSkillEffect(HOLY_WALK)) {
                        _skillTime = 300 +
                                _target.getSkillEffectTimeSec(HOLY_WALK);
                        _skillTime = Math.min(_skillTime, 1800);
                    }

                    runSkill();
                    useConsume();
                    sendGrfx(true);
                    sendFailMessageHandle();
                    setDelay();
                }
            }
            else if (type == TYPE_LOGIN) { // ãƒ­ã‚°ã‚¤ãƒ³æ™‚ï¼ˆHPMPæ��æ–™æ¶ˆè²»ã�ªã�—ã€�ã‚°ãƒ©ãƒ•ã‚£ãƒƒã‚¯ã�ªã�—ï¼‰
                runSkill();
            }
            else if (type == TYPE_SPELLSC) { // ã‚¹ãƒšãƒ«ã‚¹ã‚¯ãƒ­ãƒ¼ãƒ«ä½¿ç”¨æ™‚ï¼ˆHPMPæ��æ–™æ¶ˆè²»ã�ªã�—ï¼‰
                runSkill();
                sendGrfx(true);
            }
            else if (type == TYPE_GMBUFF) { // GMBUFFä½¿ç”¨æ™‚ï¼ˆHPMPæ��æ–™æ¶ˆè²»ã�ªã�—ã€�é­”æ³•ãƒ¢ãƒ¼ã‚·ãƒ§ãƒ³ã�ªã�—ï¼‰
                runSkill();
                sendGrfx(false);
            }
            else if (type == TYPE_NPCBUFF) { // NPCBUFFä½¿ç”¨æ™‚ï¼ˆHPMPæ��æ–™æ¶ˆè²»ã�ªã�—ï¼‰
                runSkill();
                sendGrfx(true);
            }
            setCheckedUseSkill(false);
        }
        catch (Exception e) {
            _log.log(Level.SEVERE, "", e);
        }
    }

    /**
     * ã‚¹ã‚­ãƒ«ã�®å¤±æ•—å‡¦ç�†(PCã�®ã�¿ï¼‰
     */
    private void failSkill() {
        // HPã�Œè¶³ã‚Šã�ªã��ã�¦ã‚¹ã‚­ãƒ«ã�Œä½¿ç”¨ã�§ã��ã�ªã�„å ´å�ˆã�®ã�¿ã€�MPã�®ã�¿æ¶ˆè²»ã�—ã�Ÿã�„ã�Œæœªå®Ÿè£…ï¼ˆå¿…è¦�ã�ªã�„ï¼Ÿï¼‰
        // ã��ã�®ä»–ã�®å ´å�ˆã�¯ä½•ã‚‚æ¶ˆè²»ã�•ã‚Œã�ªã�„ã€‚
        // useConsume(); // HPã€�MPã�¯æ¸›ã‚‰ã�™
        setCheckedUseSkill(false);
        // ãƒ†ãƒ¬ãƒ�ãƒ¼ãƒˆã‚¹ã‚­ãƒ«
        if ((_skillId == TELEPORT) || (_skillId == MASS_TELEPORT) || (_skillId == TELEPORT_TO_MATHER)) {
            // ãƒ†ãƒ¬ãƒ�ãƒ¼ãƒˆã�§ã��ã�ªã�„å ´å�ˆã�§ã‚‚ã€�ã‚¯ãƒ©ã‚¤ã‚¢ãƒ³ãƒˆå�´ã�¯å¿œç­”ã‚’å¾…ã�£ã�¦ã�„ã‚‹
            // ãƒ†ãƒ¬ãƒ�ãƒ¼ãƒˆå¾…ã�¡çŠ¶æ…‹ã�®è§£é™¤ï¼ˆç¬¬2å¼•æ•°ã�«æ„�å‘³ã�¯ã�ªã�„ï¼‰
            _player.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
        }
    }

    // ã‚¿ãƒ¼ã‚²ãƒƒãƒˆã�‹ï¼Ÿ
    private boolean isTarget(L1Character cha) throws Exception {
        boolean _flg = false;

        if (cha instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) cha;
            if (pc.isGhost() || pc.isGmInvis()) {
                return false;
            }
        }
        if ((_calcType == NPC_PC) && ((cha instanceof L1PcInstance) || (cha instanceof L1PetInstance) || (cha instanceof L1SummonInstance))) {
            _flg = true;
        }

        // ç ´å£Šä¸�å�¯èƒ½ã�ªãƒ‰ã‚¢ã�¯å¯¾è±¡å¤–
        if (cha instanceof L1DoorInstance) {
            if ((cha.getMaxHp() == 0) || (cha.getMaxHp() == 1)) {
                return false;
            }
        }

        // ãƒžã‚¸ãƒƒã‚¯ãƒ‰ãƒ¼ãƒ«ã�¯å¯¾è±¡å¤–
        if ((cha instanceof L1DollInstance) && (_skillId != HASTE)) {
            return false;
        }

        // å…ƒã�®ã‚¿ãƒ¼ã‚²ãƒƒãƒˆã�ŒPetã€�Summonä»¥å¤–ã�®NPCã�®å ´å�ˆã€�PCã€�Petã€�Summonã�¯å¯¾è±¡å¤–
        if ((_calcType == PC_NPC) && (_target instanceof L1NpcInstance) && !(_target instanceof L1PetInstance)
                && !(_target instanceof L1SummonInstance)
                && ((cha instanceof L1PetInstance) || (cha instanceof L1SummonInstance) || (cha instanceof L1PcInstance))) {
            return false;
        }

        // å…ƒã�®ã‚¿ãƒ¼ã‚²ãƒƒãƒˆã�Œã‚¬ãƒ¼ãƒ‰ä»¥å¤–ã�®NPCã�®å ´å�ˆã€�ã‚¬ãƒ¼ãƒ‰ã�¯å¯¾è±¡å¤–
        if ((_calcType == PC_NPC) && (_target instanceof L1NpcInstance) && !(_target instanceof L1GuardInstance) && (cha instanceof L1GuardInstance)) {
            return false;
        }

        // NPCå¯¾PCã�§ã‚¿ãƒ¼ã‚²ãƒƒãƒˆã�Œãƒ¢ãƒ³ã‚¹ã‚¿ãƒ¼ã�®å ´å�ˆã‚¿ãƒ¼ã‚²ãƒƒãƒˆã�§ã�¯ã�ªã�„ã€‚
        if ((_skill.getTarget().equals("attack") || (_skill.getType() == L1Skills.TYPE_ATTACK)) && (_calcType == NPC_PC)
                && !(cha instanceof L1PetInstance) && !(cha instanceof L1SummonInstance) && !(cha instanceof L1PcInstance)) {
            return false;
        }

        // NPCå¯¾NPCã�§ä½¿ç”¨è€…ã�ŒMOBã�§ã€�ã‚¿ãƒ¼ã‚²ãƒƒãƒˆã�ŒMOBã�®å ´å�ˆã‚¿ãƒ¼ã‚²ãƒƒãƒˆã�§ã�¯ã�ªã�„ã€‚
        if ((_skill.getTarget().equals("attack") || (_skill.getType() == L1Skills.TYPE_ATTACK)) && (_calcType == NPC_NPC)
                && (_user instanceof L1MonsterInstance) && (cha instanceof L1MonsterInstance)) {
            return false;
        }

        // ç„¡æ–¹å�‘ç¯„å›²æ”»æ’ƒé­”æ³•ã�§æ”»æ’ƒã�§ã��ã�ªã�„NPCã�¯å¯¾è±¡å¤–
        if (_skill.getTarget().equals("none")
                && (_skill.getType() == L1Skills.TYPE_ATTACK)
                && ((cha instanceof L1AuctionBoardInstance) || (cha instanceof L1BoardInstance) || (cha instanceof L1CrownInstance)
                || (cha instanceof L1DwarfInstance) || (cha instanceof L1EffectInstance) || (cha instanceof L1FieldObjectInstance)
                || (cha instanceof L1FurnitureInstance) || (cha instanceof L1HousekeeperInstance) || (cha instanceof L1MerchantInstance) || (cha instanceof L1TeleporterInstance))) {
            return false;
        }

        // æ”»æ“Šåž‹é­”æ³•ç„¡æ³•æ”»æ“Šè‡ªå·±
        if ((_skill.getType() == L1Skills.TYPE_ATTACK) && (cha.getId() == _user.getId())) {
            return false;
        }

        // é«”åŠ›å›žå¾©è¡“åˆ¤æ–·æ–½æ³•è€…ä¸�è£œè¡€
        if ((cha.getId() == _user.getId()) && (_skillId == HEAL_ALL)) {
            return false;
        }

        if ((((_skill.getTargetTo() & L1Skills.TARGET_TO_PC) == L1Skills.TARGET_TO_PC)
                || ((_skill.getTargetTo() & L1Skills.TARGET_TO_CLAN) == L1Skills.TARGET_TO_CLAN) || ((_skill.getTargetTo() & L1Skills.TARGET_TO_PARTY) == L1Skills.TARGET_TO_PARTY))
                && (cha.getId() == _user.getId()) && (_skillId != HEAL_ALL)) {
            return true; // ã‚¿ãƒ¼ã‚²ãƒƒãƒˆã�Œãƒ‘ãƒ¼ãƒ†ã‚£ãƒ¼ã�‹ã‚¯ãƒ©ãƒ³å“¡ã�®ã‚‚ã�®ã�¯è‡ªåˆ†ã�«åŠ¹æžœã�Œã�‚ã‚‹ã€‚ï¼ˆã�Ÿã� ã�—ã€�ãƒ’ãƒ¼ãƒ«ã‚ªãƒ¼ãƒ«ã�¯é™¤å¤–ï¼‰
        }

        // ã‚¹ã‚­ãƒ«ä½¿ç”¨è€…ã�ŒPCã�§ã€�PKãƒ¢ãƒ¼ãƒ‰ã�§ã�¯ã�ªã�„å ´å�ˆã€�è‡ªåˆ†ã�®ã‚µãƒ¢ãƒ³ãƒ»ãƒšãƒƒãƒˆã�¯å¯¾è±¡å¤–
        if ((_user instanceof L1PcInstance) && (_skill.getTarget().equals("attack") || (_skill.getType() == L1Skills.TYPE_ATTACK))
                && (_isPK == false)) {
            if (cha instanceof L1SummonInstance) {
                L1SummonInstance summon = (L1SummonInstance) cha;
                if (_player.getId() == summon.getMaster().getId()) {
                    return false;
                }
            }
            else if (cha instanceof L1PetInstance) {
                L1PetInstance pet = (L1PetInstance) cha;
                if (_player.getId() == pet.getMaster().getId()) {
                    return false;
                }
            }
        }

        if ((_skill.getTarget().equals("attack") || (_skill.getType() == L1Skills.TYPE_ATTACK)) && !(cha instanceof L1MonsterInstance)
                && (_isPK == false) && (_target instanceof L1PcInstance)) {
            L1PcInstance enemy = (L1PcInstance) cha;
            // ã‚«ã‚¦ãƒ³ã‚¿ãƒ¼ãƒ‡ã‚£ãƒ†ã‚¯ã‚·ãƒ§ãƒ³
            if ((_skillId == COUNTER_DETECTION) && (enemy.getZoneType() != 1)
                    && (cha.hasSkillEffect(INVISIBILITY) || cha.hasSkillEffect(BLIND_HIDING))) {
                return true; // ã‚¤ãƒ³ãƒ“ã‚¸ã�‹ãƒ–ãƒ©ã‚¤ãƒ³ãƒ‰ãƒ�ã‚¤ãƒ‡ã‚£ãƒ³ã‚°ä¸­
            }
            if ((_skillId == COUNTER_DETECTION) && (enemy.getZoneType() != 1)
                    && !(cha.hasSkillEffect(INVISIBILITY) || cha.hasSkillEffect(BLIND_HIDING))) {
                return false; // added to try to fix CD
            }
            if ((_player.getClanid() != 0) && (enemy.getClanid() != 0)) { // ã‚¯ãƒ©ãƒ³æ‰€å±žä¸­
                // å…¨æˆ¦äº‰ãƒªã‚¹ãƒˆã‚’å�–å¾—
                for (L1War war : L1World.getInstance().getWarList()) {
                    if (war.CheckClanInWar(_player.getClanname())) { // è‡ªã‚¯ãƒ©ãƒ³ã�Œæˆ¦äº‰ã�«å�‚åŠ ä¸­
                        if (war.CheckClanInSameWar( // å�Œã�˜æˆ¦äº‰ã�«å�‚åŠ ä¸­
                                _player.getClanname(), enemy.getClanname())) {
                            if (L1CastleLocation.checkInAllWarArea(enemy.getX(), enemy.getY(), enemy.getMapId())) {
                                return true;
                            }
                        }
                    }
                }
            }
            return false; // æ”»æ’ƒã‚¹ã‚­ãƒ«ã�§PKãƒ¢ãƒ¼ãƒ‰ã�˜ã‚ƒã�ªã�„å ´å�ˆ
        }

        if ((_user.glanceCheck(cha.getX(), cha.getY()) == false) && (_skill.isThrough() == false)) {
            // ã‚¨ãƒ³ãƒ�ãƒ£ãƒ³ãƒˆã€�å¾©æ´»ã‚¹ã‚­ãƒ«ã�¯éšœå®³ç‰©ã�®åˆ¤å®šã‚’ã�—ã�ªã�„
            if (!((_skill.getType() == L1Skills.TYPE_CHANGE) || (_skill.getType() == L1Skills.TYPE_RESTORE))) {
                _isGlanceCheckFail = true;
                return false; // ç›´ç·šä¸Šã�«éšœå®³ç‰©ã�Œã�‚ã‚‹
            }
        }

        if (cha.hasSkillEffect(ICE_LANCE) || cha.hasSkillEffect(FREEZING_BLIZZARD) || cha.hasSkillEffect(FREEZING_BREATH)
                || cha.hasSkillEffect(ICE_LANCE_COCKATRICE) || cha.hasSkillEffect(ICE_LANCE_BASILISK)) {
            if (_skillId == ICE_LANCE || _skillId == FREEZING_BLIZZARD
                    || _skillId == FREEZING_BREATH || _skillId == ICE_LANCE_COCKATRICE || _skillId == ICE_LANCE_BASILISK) {
                return false;
            }
        }
/*
		if (cha.hasSkillEffect(ICE_LANCE) && ((_skillId == ICE_LANCE) || (_skillId == FREEZING_BLIZZARD) || (_skillId == FREEZING_BREATH))) {
			return false; // ã‚¢ã‚¤ã‚¹ãƒ©ãƒ³ã‚¹ä¸­ã�«ã‚¢ã‚¤ã‚¹ãƒ©ãƒ³ã‚¹ã€�ãƒ•ãƒªãƒ¼ã‚¸ãƒ³ã‚°ãƒ–ãƒªã‚¶ãƒ¼ãƒ‰ã€�ãƒ•ãƒªãƒ¼ã‚¸ãƒ³ã‚°ãƒ–ãƒ¬ã‚¹
		}

		if (cha.hasSkillEffect(FREEZING_BLIZZARD) && ((_skillId == ICE_LANCE) || (_skillId == FREEZING_BLIZZARD) || (_skillId == FREEZING_BREATH))) {
			return false; // ãƒ•ãƒªãƒ¼ã‚¸ãƒ³ã‚°ãƒ–ãƒªã‚¶ãƒ¼ãƒ‰ä¸­ã�«ã‚¢ã‚¤ã‚¹ãƒ©ãƒ³ã‚¹ã€�ãƒ•ãƒªãƒ¼ã‚¸ãƒ³ã‚°ãƒ–ãƒªã‚¶ãƒ¼ãƒ‰ã€�ãƒ•ãƒªãƒ¼ã‚¸ãƒ³ã‚°ãƒ–ãƒ¬ã‚¹
		}

		if (cha.hasSkillEffect(FREEZING_BREATH) && ((_skillId == ICE_LANCE) || (_skillId == FREEZING_BLIZZARD) || (_skillId == FREEZING_BREATH))) {
			return false; // ãƒ•ãƒªãƒ¼ã‚¸ãƒ³ã‚°ãƒ–ãƒ¬ã‚¹ä¸­ã�«ã‚¢ã‚¤ã‚¹ãƒ©ãƒ³ã‚¹ã€�ãƒ•ãƒªãƒ¼ã‚¸ãƒ³ã‚°ãƒ–ãƒªã‚¶ãƒ¼ãƒ‰ã€�ãƒ•ãƒªãƒ¼ã‚¸ãƒ³ã‚°ãƒ–ãƒ¬ã‚¹
		}
*/
        if (cha.hasSkillEffect(EARTH_BIND) && (_skillId == EARTH_BIND)) {
            return false; // ã‚¢ãƒ¼ã‚¹ ãƒ�ã‚¤ãƒ³ãƒ‰ä¸­ã�«ã‚¢ãƒ¼ã‚¹ ãƒ�ã‚¤ãƒ³ãƒ‰
        }

        if (!(cha instanceof L1MonsterInstance) && ((_skillId == TAMING_MONSTER) || (_skillId == CREATE_ZOMBIE))) {
            return false; // ã‚¿ãƒ¼ã‚²ãƒƒãƒˆã�Œãƒ¢ãƒ³ã‚¹ã‚¿ãƒ¼ã�˜ã‚ƒã�ªã�„ï¼ˆãƒ†ã‚¤ãƒŸãƒ³ã‚°ãƒ¢ãƒ³ã‚¹ã‚¿ãƒ¼ï¼‰
        }
        if (cha.isDead()
                && ((_skillId != CREATE_ZOMBIE) && (_skillId != RESURRECTION) && (_skillId != GREATER_RESURRECTION) && (_skillId != CALL_OF_NATURE))) {
            return false; // ç›®æ¨™å·²æ­»äº¡ æ³•è¡“é�žå¾©æ´»é¡ž
        }

        if ((cha.isDead() == false)
                && ((_skillId == CREATE_ZOMBIE) || (_skillId == RESURRECTION) || (_skillId == GREATER_RESURRECTION) || (_skillId == CALL_OF_NATURE))) {
            return false; // ç›®æ¨™æœªæ­»äº¡ æ³•è¡“å¾©æ´»é¡ž
        }

        if (((cha instanceof L1TowerInstance) || (cha instanceof L1DoorInstance))
                && ((_skillId == CREATE_ZOMBIE) || (_skillId == RESURRECTION) || (_skillId == GREATER_RESURRECTION) || (_skillId == CALL_OF_NATURE))) {
            return false; // å¡”è·Ÿé–€ä¸�å�¯æ”¾å¾©æ´»æ³•è¡“
        }

        if (cha instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) cha;
            if (pc.hasSkillEffect(ABSOLUTE_BARRIER)) { // çµ•å°�å±�éšœç‹€æ…‹ä¸­
                if ((_skillId == CURSE_BLIND) || (_skillId == WEAPON_BREAK) || (_skillId == DARKNESS) || (_skillId == WEAKNESS)
                        || (_skillId == DISEASE) || (_skillId == FOG_OF_SLEEPING) || (_skillId == MASS_SLOW) || (_skillId == SLOW)
                        || (_skillId == CANCELLATION) || (_skillId == SILENCE) || (_skillId == DECAY_POTION) || (_skillId == MASS_TELEPORT)
                        || (_skillId == DETECTION) || (_skillId == COUNTER_DETECTION) || (_skillId == ERASE_MAGIC) || (_skillId == ENTANGLE)
                        || (_skillId == PHYSICAL_ENCHANT_DEX) || (_skillId == PHYSICAL_ENCHANT_STR) || (_skillId == BLESS_WEAPON)
                        || (_skillId == EARTH_SKIN) || (_skillId == IMMUNE_TO_HARM) || (_skillId == REMOVE_CURSE)) {
                    return true;
                }
                else {
                    return false;
                }
            }
        }

        if (cha instanceof L1NpcInstance) {
            int hiddenStatus = ((L1NpcInstance) cha).getHiddenStatus();
            if (hiddenStatus == L1NpcInstance.HIDDEN_STATUS_SINK) {
                if ((_skillId == DETECTION) || (_skillId == COUNTER_DETECTION)) { // ãƒ‡ã‚£ãƒ†ã‚¯ã€�Cãƒ‡ã‚£ãƒ†ã‚¯
                    return true;
                }
                else {
                    return false;
                }
            }
            else if (hiddenStatus == L1NpcInstance.HIDDEN_STATUS_FLY) {
                return false;
            }
        }

        if (((_skill.getTargetTo() & L1Skills.TARGET_TO_PC) == L1Skills.TARGET_TO_PC // ã‚¿ãƒ¼ã‚²ãƒƒãƒˆã�ŒPC
        )
                && (cha instanceof L1PcInstance)) {
            _flg = true;
        }
        else if (((_skill.getTargetTo() & L1Skills.TARGET_TO_NPC) == L1Skills.TARGET_TO_NPC // ã‚¿ãƒ¼ã‚²ãƒƒãƒˆã�ŒNPC
        )
                && ((cha instanceof L1MonsterInstance) || (cha instanceof L1NpcInstance) || (cha instanceof L1SummonInstance) || (cha instanceof L1PetInstance))) {
            _flg = true;
        }
        else if (((_skill.getTargetTo() & L1Skills.TARGET_TO_PET) == L1Skills.TARGET_TO_PET) && (_user instanceof L1PcInstance)) { // ã‚¿ãƒ¼ã‚²ãƒƒãƒˆã�ŒSummon,Pet
            if (cha instanceof L1SummonInstance) {
                L1SummonInstance summon = (L1SummonInstance) cha;
                if (summon.getMaster() != null) {
                    if (_player.getId() == summon.getMaster().getId()) {
                        _flg = true;
                    }
                }
            }
            else if (cha instanceof L1PetInstance) {
                L1PetInstance pet = (L1PetInstance) cha;
                if (pet.getMaster() != null) {
                    if (_player.getId() == pet.getMaster().getId()) {
                        _flg = true;
                    }
                }
            }
        }

        if ((_calcType == PC_PC) && (cha instanceof L1PcInstance)) {
            if (((_skill.getTargetTo() & L1Skills.TARGET_TO_CLAN) == L1Skills.TARGET_TO_CLAN) && (((_player.getClanid() != 0 // ã‚¿ãƒ¼ã‚²ãƒƒãƒˆã�Œã‚¯ãƒ©ãƒ³å“¡
            ) && (_player.getClanid() == ((L1PcInstance) cha).getClanid())) || _player.isGm())) {
                return true;
            }
            if (((_skill.getTargetTo() & L1Skills.TARGET_TO_PARTY) == L1Skills.TARGET_TO_PARTY) && (_player.getParty() // ã‚¿ãƒ¼ã‚²ãƒƒãƒˆã�Œãƒ‘ãƒ¼ãƒ†ã‚£ãƒ¼
                    .isMember((L1PcInstance) cha) || _player.isGm())) {
                return true;
            }
        }

        return _flg;
    }

    // ã‚¿ãƒ¼ã‚²ãƒƒãƒˆã�®ä¸€è¦§ã‚’ä½œæˆ�
    private void makeTargetList() {
        try {
            if (_type == TYPE_LOGIN) { // ãƒ­ã‚°ã‚¤ãƒ³æ™‚(æ­»äº¡æ™‚ã€�ã�ŠåŒ–ã�‘å±‹æ•·ã�®ã‚­ãƒ£ãƒ³ã‚»ãƒ¬ãƒ¼ã‚·ãƒ§ãƒ³å�«ã‚€)ã�¯ä½¿ç”¨è€…ã�®ã�¿
                _targetList.add(new TargetStatus(_user));
                return;
            }
            if ((_skill.getTargetTo() == L1Skills.TARGET_TO_ME) && ((_skill.getType() & L1Skills.TYPE_ATTACK) != L1Skills.TYPE_ATTACK)) {
                _targetList.add(new TargetStatus(_user)); // ã‚¿ãƒ¼ã‚²ãƒƒãƒˆã�¯ä½¿ç”¨è€…ã�®ã�¿
                return;
            }

            // å°„ç¨‹è·�é›¢-1ã�®å ´å�ˆã�¯ç”»é�¢å†…ã�®ã‚ªãƒ–ã‚¸ã‚§ã‚¯ãƒˆã�Œå¯¾è±¡
            if (getSkillRanged() != -1) {
                if (_user.getLocation().getTileLineDistance(_target.getLocation()) > getSkillRanged()) {
                    return; // å°„ç¨‹ç¯„å›²å¤–
                }
            }
            else {
                if (!_user.getLocation().isInScreen(_target.getLocation())) {
                    return; // å°„ç¨‹ç¯„å›²å¤–
                }
            }

            if ((isTarget(_target) == false) && !(_skill.getTarget().equals("none"))) {
                // å¯¾è±¡ã�Œé�•ã�†ã�®ã�§ã‚¹ã‚­ãƒ«ã�Œç™ºå‹•ã�—ã�ªã�„ã€‚
                return;
            }

            if ((_skillId == LIGHTNING) || (_skillId == FREEZING_BREATH)) { // ãƒ©ã‚¤ãƒˆãƒ‹ãƒ³ã‚°ã€�ãƒ•ãƒªãƒ¼ã‚¸ãƒ³ã‚°ãƒ–ãƒ¬ã‚¹ç›´ç·šçš„ã�«ç¯„å›²ã‚’æ±ºã‚�ã‚‹
                List<L1Object> al1object = L1World.getInstance().getVisibleLineObjects(_user, _target);

                for (L1Object tgobj : al1object) {
                    if (tgobj == null) {
                        continue;
                    }
                    if (!(tgobj instanceof L1Character)) { // ã‚¿ãƒ¼ã‚²ãƒƒãƒˆã�Œã‚­ãƒ£ãƒ©ã‚¯ã‚¿ãƒ¼ä»¥å¤–ã�®å ´å�ˆä½•ã‚‚ã�—ã�ªã�„ã€‚
                        continue;
                    }
                    L1Character cha = (L1Character) tgobj;
                    if (isTarget(cha) == false) {
                        continue;
                    }
                    _targetList.add(new TargetStatus(cha));
                }
                return;
            }

            if (getSkillArea() == 0) { // å�˜ä½“ã�®å ´å�ˆ
                if (!_user.glanceCheck(_target.getX(), _target.getY())) { // ç›´ç·šä¸Šã�«éšœå®³ç‰©ã�Œã�‚ã‚‹ã�‹
                    if (((_skill.getType() & L1Skills.TYPE_ATTACK) == L1Skills.TYPE_ATTACK) && (_skillId != 10026) && (_skillId != 10027)
                            && (_skillId != 10028) && (_skillId != 10029)) { // å®‰æ�¯æ”»æ’ƒä»¥å¤–ã�®æ”»æ’ƒã‚¹ã‚­ãƒ«
                        _targetList.add(new TargetStatus(_target, false)); // ãƒ€ãƒ¡ãƒ¼ã‚¸ã‚‚ç™ºç”Ÿã�—ã�ªã�„ã�—ã€�ãƒ€ãƒ¡ãƒ¼ã‚¸ãƒ¢ãƒ¼ã‚·ãƒ§ãƒ³ã‚‚ç™ºç”Ÿã�—ã�ªã�„ã�Œã€�ã‚¹ã‚­ãƒ«ã�¯ç™ºå‹•
                        return;
                    }
                }
                _targetList.add(new TargetStatus(_target));
            }
            else { // ç¯„å›²ã�®å ´å�ˆ
                if (!_skill.getTarget().equals("none")) {
                    _targetList.add(new TargetStatus(_target));
                }

                if ((_skillId != 49) && !(_skill.getTarget().equals("attack") || (_skill.getType() == L1Skills.TYPE_ATTACK))) {
                    // æ”»æ’ƒç³»ä»¥å¤–ã�®ã‚¹ã‚­ãƒ«ã�¨H-Aä»¥å¤–ã�¯ã‚¿ãƒ¼ã‚²ãƒƒãƒˆè‡ªèº«ã‚’å�«ã‚�ã‚‹
                    _targetList.add(new TargetStatus(_user));
                }

                List<L1Object> objects;
                if (getSkillArea() == -1) {
                    objects = L1World.getInstance().getVisibleObjects(_user);
                }
                else {
                    objects = L1World.getInstance().getVisibleObjects(_target, getSkillArea());
                }
                for (L1Object tgobj : objects) {
                    if (tgobj == null) {
                        continue;
                    }
                    if (!(tgobj instanceof L1Character)) { // ã‚¿ãƒ¼ã‚²ãƒƒãƒˆã�Œã‚­ãƒ£ãƒ©ã‚¯ã‚¿ãƒ¼ä»¥å¤–ã�®å ´å�ˆä½•ã‚‚ã�—ã�ªã�„ã€‚
                        continue;
                    }
                    L1Character cha = (L1Character) tgobj;
                    if (!isTarget(cha)) {
                        continue;
                    }

                    _targetList.add(new TargetStatus(cha));
                }
                return;
            }

        }
        catch (Exception e) {
            _log.log(Level.FINEST, "exception in L1Skilluse makeTargetList{0}", e);
        }
    }

    // ãƒ¡ãƒƒã‚»ãƒ¼ã‚¸ã�®è¡¨ç¤ºï¼ˆä½•ã�‹èµ·ã�“ã�£ã�Ÿã�¨ã��ï¼‰
    private void sendHappenMessage(L1PcInstance pc) {
        int msgID = _skill.getSysmsgIdHappen();
        if (msgID > 0) {
            // æ•ˆæžœè¨Šæ�¯æŽ’é™¤æ–½æ³•è€…æœ¬èº«ã€‚
            if (_skillId == AREA_OF_SILENCE && _user.getId() == pc.getId()) {// å°�å�°ç¦�åœ°
                return;
            }
            pc.sendPackets(new S_ServerMessage(msgID));
        }
    }

    // å¤±æ•—ãƒ¡ãƒƒã‚»ãƒ¼ã‚¸è¡¨ç¤ºã�®ãƒ�ãƒ³ãƒ‰ãƒ«
    private void sendFailMessageHandle() {
        // æ”»æ’ƒã‚¹ã‚­ãƒ«ä»¥å¤–ã�§å¯¾è±¡ã‚’æŒ‡å®šã�™ã‚‹ã‚¹ã‚­ãƒ«ã�Œå¤±æ•—ã�—ã�Ÿå ´å�ˆã�¯å¤±æ•—ã�—ã�Ÿãƒ¡ãƒƒã‚»ãƒ¼ã‚¸ã‚’ã‚¯ãƒ©ã‚¤ã‚¢ãƒ³ãƒˆã�«é€�ä¿¡
        // â€»æ”»æ’ƒã‚¹ã‚­ãƒ«ã�¯éšœå®³ç‰©ã�Œã�‚ã�£ã�¦ã‚‚æˆ�åŠŸæ™‚ã�¨å�Œã�˜ã‚¢ã‚¯ã‚·ãƒ§ãƒ³ã�§ã�‚ã‚‹ã�¹ã��ã€‚
        if ((_skill.getType() != L1Skills.TYPE_ATTACK) && !_skill.getTarget().equals("none") && _targetList.isEmpty()) {
            sendFailMessage();
        }
    }

    // ãƒ¡ãƒƒã‚»ãƒ¼ã‚¸ã�®è¡¨ç¤ºï¼ˆå¤±æ•—ã�—ã�Ÿã�¨ã��ï¼‰
    private void sendFailMessage() {
        int msgID = _skill.getSysmsgIdFail();
        if ((msgID > 0) && (_user instanceof L1PcInstance)) {
            _player.sendPackets(new S_ServerMessage(msgID));
        }
    }

    // ç²¾éœŠé­”æ³•ã�®å±žæ€§ã�¨ä½¿ç”¨è€…ã�®å±žæ€§ã�¯ä¸€è‡´ã�™ã‚‹ã�‹ï¼Ÿï¼ˆã�¨ã‚Šã�‚ã�ˆã�šã�®å¯¾å‡¦ã�ªã�®ã�§ã€�å¯¾å¿œã�§ã��ã�Ÿã‚‰æ¶ˆåŽ»ã�—ã�¦ä¸‹ã�•ã�„)
    private boolean isAttrAgrees() {
        int magicattr = _skill.getAttr();
        if (_user instanceof L1NpcInstance) { // NPCã�Œä½¿ã�£ã�Ÿå ´å�ˆã�ªã‚“ã�§ã‚‚OK
            return true;
        }

        if ((_skill.getSkillLevel() >= 17) && (_skill.getSkillLevel() <= 22) && (magicattr != 0 // ç²¾éœŠé­”æ³•ã�§ã€�ç„¡å±žæ€§é­”æ³•ã�§ã�¯ã�ªã��ã€�
        ) && (magicattr != _player.getElfAttr() // ä½¿ç”¨è€…ã�¨é­”æ³•ã�®å±žæ€§ã�Œä¸€è‡´ã�—ã�ªã�„ã€‚
        ) && !_player.isGm()) { // ã�Ÿã� ã�—GMã�¯ä¾‹å¤–
            return false;
        }
        return true;
    }

    // å¿…è¦�ï¼¨ï¼°ã€�ï¼­ï¼°ã�Œã�‚ã‚‹ã�‹ï¼Ÿ
    private boolean isHPMPConsume() {
        if (_mpConsume == 0) {
            _mpConsume = _skill.getMpConsume();
        }
        _hpConsume = _skill.getHpConsume();
        int currentMp = 0;
        int currentHp = 0;

        if (_user instanceof L1NpcInstance) {
            currentMp = _npc.getCurrentMp();
            currentHp = _npc.getCurrentHp();
        }
        else {
            currentMp = _player.getCurrentMp();
            currentHp = _player.getCurrentHp();

            // MPã�®INTè»½æ¸›
            if ((_player.getInt() > 12) && (_skillId > HOLY_WEAPON) && (_skillId <= FREEZING_BLIZZARD)) { // LV2ä»¥ä¸Š
                _mpConsume--;
            }
            if ((_player.getInt() > 13) && (_skillId > STALAC) && (_skillId <= FREEZING_BLIZZARD)) { // LV3ä»¥ä¸Š
                _mpConsume--;
            }
            if ((_player.getInt() > 14) && (_skillId > WEAK_ELEMENTAL) && (_skillId <= FREEZING_BLIZZARD)) { // LV4ä»¥ä¸Š
                _mpConsume--;
            }
            if ((_player.getInt() > 15) && (_skillId > MEDITATION) && (_skillId <= FREEZING_BLIZZARD)) { // LV5ä»¥ä¸Š
                _mpConsume--;
            }
            if ((_player.getInt() > 16) && (_skillId > DARKNESS) && (_skillId <= FREEZING_BLIZZARD)) { // LV6ä»¥ä¸Š
                _mpConsume--;
            }
            if ((_player.getInt() > 17) && (_skillId > BLESS_WEAPON) && (_skillId <= FREEZING_BLIZZARD)) { // LV7ä»¥ä¸Š
                _mpConsume--;
            }
            if ((_player.getInt() > 18) && (_skillId > DISEASE) && (_skillId <= FREEZING_BLIZZARD)) { // LV8ä»¥ä¸Š
                _mpConsume--;
            }

            // é¨Žå£«æ™ºåŠ›æ¸›å…�
            if ((_player.getInt() > 12) && (_skillId >= SHOCK_STUN) && (_skillId <= COUNTER_BARRIER)) {
                if ( _player.getInt() <= 17 )
                    _mpConsume -= (_player.getInt() - 12);
                else {
                    _mpConsume -= 5 ; // int > 18
                    if ( _mpConsume > 1 ) { // æ³•è¡“é‚„å�¯ä»¥æ¸›å…�
                        byte extraInt = (byte) (_player.getInt() - 17) ;
                        // æ¸›å…�å…¬å¼�
                        for ( int first= 1 ,range = 2 ; first <= extraInt; first += range, range ++  )
                            _mpConsume -- ;
                    }
                }

            }

            // è£�å‚™MPæ¸›å…� ä¸€æ¬¡å�ªéœ€åˆ¤æ–·ä¸€å€‹
            if ((_skillId == PHYSICAL_ENCHANT_DEX) && _player.getInventory().checkEquipped(20013)) { // æ•�æ�·é­”æ³•é ­ç›”ä½¿ç”¨é€šæš¢æ°£è„ˆè¡“
                _mpConsume /= 2;
            }
            else if ((_skillId == HASTE) && _player.getInventory().checkEquipped(20013)) { // æ•�æ�·é­”æ³•é ­ç›”ä½¿ç”¨åŠ é€Ÿè¡“
                _mpConsume /= 2;
            }
            else if ((_skillId == HEAL) && _player.getInventory().checkEquipped(20014)) { // æ²»ç™’é­”æ³•é ­ç›”ä½¿ç”¨åˆ�ç´šæ²»ç™’è¡“
                _mpConsume /= 2;
            }
            else if ((_skillId == EXTRA_HEAL) && _player.getInventory().checkEquipped(20014)) { // æ²»ç™’é­”æ³•é ­ç›”ä½¿ç”¨ä¸­ç´šæ²»ç™’è¡“
                _mpConsume /= 2;
            }
            else if ((_skillId == ENCHANT_WEAPON) && _player.getInventory().checkEquipped(20015)) { // åŠ›é‡�é­”æ³•é ­ç›”ä½¿ç”¨æ“¬ä¼¼é­”æ³•æ­¦å™¨
                _mpConsume /= 2;
            }
            else if ((_skillId == DETECTION) && _player.getInventory().checkEquipped(20015)) { // åŠ›é‡�é­”æ³•é ­ç›”ä½¿ç”¨ç„¡æ‰€é��å½¢è¡“
                _mpConsume /= 2;
            }
            else if ((_skillId == PHYSICAL_ENCHANT_STR) && _player.getInventory().checkEquipped(20015)) { // åŠ›é‡�é­”æ³•é ­ç›”ä½¿ç”¨é«”é­„å¼·å�¥è¡“
                _mpConsume /= 2;
            }
            else if ((_skillId == HASTE) && _player.getInventory().checkEquipped(20008)) { // å°�åž‹é¢¨ä¹‹é ­ç›”ä½¿ç”¨åŠ é€Ÿè¡“
                _mpConsume /= 2;
            }
            else if ((_skillId == HASTE) && _player.getInventory().checkEquipped(20023)) { // é¢¨ä¹‹é ­ç›”ä½¿ç”¨åŠ é€Ÿè¡“
                _mpConsume = 25;
            }
            else if ((_skillId == GREATER_HASTE) && _player.getInventory().checkEquipped(20023)) { // é¢¨ä¹‹é ­ç›”ä½¿ç”¨å¼·åŠ›åŠ é€Ÿè¡“
                _mpConsume /= 2;
            }

            // åˆ�å§‹èƒ½åŠ›æ¸›å…�
            if (_player.getOriginalMagicConsumeReduction() > 0) {
                _mpConsume -= _player.getOriginalMagicConsumeReduction();
            }

            if (0 < _skill.getMpConsume()) {
                _mpConsume = Math.max(_mpConsume, 1); // æœ€å°�å€¼ 1
            }
        }

        if (currentHp < _hpConsume + 1) {
            if (_user instanceof L1PcInstance) {
                _player.sendPackets(new S_ServerMessage(279)); // \f1å› é«”åŠ›ä¸�è¶³è€Œç„¡æ³•ä½¿ç”¨é­”æ³•ã€‚
            }
            return false;
        }
        else if (currentMp < _mpConsume) {
            if (_user instanceof L1PcInstance) {
                _player.sendPackets(new S_ServerMessage(278)); // \f1å› é­”åŠ›ä¸�è¶³è€Œç„¡æ³•ä½¿ç”¨é­”æ³•ã€‚
            }
            return false;
        }

        return true;
    }

    // å¿…è¦�æ��æ–™ã�Œã�‚ã‚‹ã�‹ï¼Ÿ
    private boolean isItemConsume() {

        int itemConsume = _skill.getItemConsumeId();
        int itemConsumeCount = _skill.getItemConsumeCount();

        if (itemConsume == 0) {
            return true; // æ��æ–™ã‚’å¿…è¦�ã�¨ã�—ã�ªã�„é­”æ³•
        }

        if (!_player.getInventory().checkItem(itemConsume, itemConsumeCount)) {
            return false; // å¿…è¦�æ��æ–™ã�Œè¶³ã‚Šã�ªã�‹ã�£ã�Ÿã€‚
        }

        return true;
    }

    // ä½¿ç”¨æ��æ–™ã€�HPãƒ»MPã€�Lawfulã‚’ãƒžã‚¤ãƒŠã‚¹ã�™ã‚‹ã€‚
    private void useConsume() {
        if (_user instanceof L1NpcInstance) {
            // NPCã�®å ´å�ˆã€�HPã€�MPã�®ã�¿ãƒžã‚¤ãƒŠã‚¹
            int current_hp = _npc.getCurrentHp() - _hpConsume;
            _npc.setCurrentHp(current_hp);

            int current_mp = _npc.getCurrentMp() - _mpConsume;
            _npc.setCurrentMp(current_mp);
            return;
        }

        // HPãƒ»MPèŠ±è²» å·²ç¶“è¨ˆç®—ä½¿ç”¨é‡�
        if (_skillId == FINAL_BURN) { // æœƒå¿ƒä¸€æ“Š
            _player.setCurrentHp(1);
            _player.setCurrentMp(0);
        }
        else {
            int current_hp = _player.getCurrentHp() - _hpConsume;
            _player.setCurrentHp(current_hp);

            int current_mp = _player.getCurrentMp() - _mpConsume;
            _player.setCurrentMp(current_mp);
        }

        // Lawfulã‚’ãƒžã‚¤ãƒŠã‚¹
        int lawful = _player.getLawful() + _skill.getLawful();
        if (lawful > 32767) {
            lawful = 32767;
        }
        else if (lawful < -32767) {
            lawful = -32767;
        }
        _player.setLawful(lawful);

        int itemConsume = _skill.getItemConsumeId();
        int itemConsumeCount = _skill.getItemConsumeCount();

        if (itemConsume == 0) {
            return; // æ��æ–™ã‚’å¿…è¦�ã�¨ã�—ã�ªã�„é­”æ³•
        }

        // ä½¿ç”¨æ��æ–™ã‚’ãƒžã‚¤ãƒŠã‚¹
        _player.getInventory().consumeItem(itemConsume, itemConsumeCount);
    }

    // ãƒžã‚¸ãƒƒã‚¯ãƒªã‚¹ãƒˆã�«è¿½åŠ ã�™ã‚‹ã€‚
    private void addMagicList(L1Character cha, boolean repetition) {
        if (_skillTime == 0) {
            _getBuffDuration = _skill.getBuffDuration() * 1000; // åŠ¹æžœæ™‚é–“
            if (_skill.getBuffDuration() == 0) {
                if (_skillId == INVISIBILITY) { // ã‚¤ãƒ³ãƒ“ã‚¸ãƒ“ãƒªãƒ†ã‚£
                    cha.setSkillEffect(INVISIBILITY, 0);
                }
                return;
            }
        }
        else {
            _getBuffDuration = _skillTime * 1000; // ãƒ‘ãƒ©ãƒ¡ãƒ¼ã‚¿ã�®timeã�Œ0ä»¥å¤–ã�ªã‚‰ã€�åŠ¹æžœæ™‚é–“ã�¨ã�—ã�¦è¨­å®šã�™ã‚‹
        }

        if (_skillId == SHOCK_STUN) {
            _getBuffDuration = _shockStunDuration;
        }

        if (_skillId == BONE_BREAK) {
            _getBuffDuration = _boneBreakDuration;
        }

        if (_skillId == CURSE_POISON) {  // æ¯’å’’æŒ�çºŒæ™‚é–“ç§»è‡³ L1Poison è™•ç�†ã€‚
            return;
        }
        if ((_skillId == CURSE_PARALYZE) || (_skillId == CURSE_PARALYZE2)) { // æœ¨ä¹ƒä¼Šçš„å’€å’’ã€�çŸ³åŒ–æŒ�çºŒæ™‚é–“ç§»è‡³ L1CurseParalysis è™•ç�†ã€‚
            return;
        }
        if (_skillId == SHAPE_CHANGE) { // è®Šå½¢è¡“æŒ�çºŒæ™‚é–“ç§»è‡³ L1PolyMorph è™•ç�†ã€‚
            return;
        }
        if ((_skillId == BLESSED_ARMOR) || (_skillId == HOLY_WEAPON // æ­¦å™¨ãƒ»é˜²å…·ã�«åŠ¹æžœã�Œã�‚ã‚‹å‡¦ç�†ã�¯L1ItemInstanceã�«ç§»è­²ã€‚
        ) || (_skillId == ENCHANT_WEAPON) || (_skillId == BLESS_WEAPON) || (_skillId == SHADOW_FANG)) {
            return;
        }
        if (((_skillId == ICE_LANCE) || (_skillId == FREEZING_BLIZZARD) || (_skillId == FREEZING_BREATH)
                || (_skillId == ICE_LANCE_COCKATRICE) || (_skillId == ICE_LANCE_BASILISK)) && !_isFreeze) { // å‡�çµ�å¤±æ•—
            return;
        }
        else if (_skillId == EARTH_BIND) {
            try{


                if(cha instanceof L1PcInstance)
                {
                    L1PcInstance pc = new L1PcInstance();
                    pc = (L1PcInstance) cha;

                    if(pc.getBuffs().containsKey(157))
                    {
                        _getBuffDuration = 	pc.getBuffs().get(157).getRemainingTime();
                        pc.sendPackets(new S_SystemMessage("Cannot restun with stun time remaining: " + _getBuffDuration));
                        return;
                    }
                    else
                    {
                        _getBuffDuration = _earthBindDuration;
                        pc.sendPackets(new S_SystemMessage("earth bind Duration: " + _getBuffDuration));
                        L1EffectSpawn.getInstance().spawnEffect(97076,_earthBindDuration, cha.getX(), cha.getY(),cha.getMapId());
                    }
                }
                else
                {
                    if(cha.getBuffs().containsKey(157))
                    {
                        _getBuffDuration = 	cha.getBuffs().get(157).getRemainingTime();
                        return;
                    }
                    else
                    {
                        _getBuffDuration = _earthBindDuration;
                        L1EffectSpawn.getInstance().spawnEffect(97077,_earthBindDuration, cha.getX(), cha.getY(),cha.getMapId());
                    }
                }
            }
            catch (Exception e)
            {

            }
        }
        /*
        if ((_skillId == AWAKEN_ANTHARAS) || (_skillId == AWAKEN_FAFURION) || (_skillId == AWAKEN_VALAKAS)) { // è¦šé†’ã�®åŠ¹æžœå‡¦ç�†ã�¯L1Awakeã�«ç§»è­²ã€‚
            return;
        }*/
        // éª·é«�æ¯€å£žæŒ�çºŒæ™‚é–“å�¦å¤–è™•ç�† removed BONE_BREAK HERE
        if (_skillId == CONFUSION) {
            return;
        }
        cha.setSkillEffect(_skillId, _getBuffDuration);

        if (_skillId == ELEMENTAL_FALL_DOWN && repetition) { // å¼±åŒ–å±¬æ€§é‡�è¤‡æ–½æ”¾
            if (_skillTime == 0) {
                _getBuffIconDuration = _skill.getBuffDuration(); // åŠ¹æžœæ™‚é–“
            } else {
                _getBuffIconDuration = _skillTime;
            }
            _target.removeSkillEffect(ELEMENTAL_FALL_DOWN);
            runSkill();
            return;
        }
        if ((cha instanceof L1PcInstance) && repetition) { // å¯¾è±¡ã�ŒPCã�§æ—¢ã�«ã‚¹ã‚­ãƒ«ã�Œé‡�è¤‡ã�—ã�¦ã�„ã‚‹å ´å�ˆ
            L1PcInstance pc = (L1PcInstance) cha;
            sendIcon(pc);
        }
    }

    public static int randInt(int min, int max) {

        // Usually this can be a field rather than a method variable
        Random rand = new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }
    // ã‚¢ã‚¤ã‚³ãƒ³ã�®é€�ä¿¡
    private void sendIcon(L1PcInstance pc) {
        if (_skillTime == 0) {
            _getBuffIconDuration = _skill.getBuffDuration(); // åŠ¹æžœæ™‚é–“
        }
        else {
            _getBuffIconDuration = _skillTime; // ãƒ‘ãƒ©ãƒ¡ãƒ¼ã‚¿ã�®timeã�Œ0ä»¥å¤–ã�ªã‚‰ã€�åŠ¹æžœæ™‚é–“ã�¨ã�—ã�¦è¨­å®šã�™ã‚‹
        }

        if (_skillId == SHIELD) { // ã‚·ãƒ¼ãƒ«ãƒ‰
            pc.sendPackets(new S_SkillIconShield(5, _getBuffIconDuration));
        }
        else if (_skillId == SHADOW_ARMOR) { // ã‚·ãƒ£ãƒ‰ã‚¦ ã‚¢ãƒ¼ãƒžãƒ¼
            pc.sendPackets(new S_SkillIconShield(3, _getBuffIconDuration));
        }
        else if (_skillId == DRESS_DEXTERITY) { // ãƒ‰ãƒ¬ã‚¹ ãƒ‡ã‚¯ã‚¹ã‚¿ãƒªãƒ†ã‚£ãƒ¼
            pc.sendPackets(new S_Dexup(pc, 2, _getBuffIconDuration));
        }
        else if (_skillId == DRESS_MIGHTY) { // ãƒ‰ãƒ¬ã‚¹ ãƒžã‚¤ãƒ†ã‚£ãƒ¼
            pc.sendPackets(new S_Strup(pc, 2, _getBuffIconDuration));
        }
        else if (_skillId == GLOWING_AURA) { // ã‚°ãƒ­ãƒ¼ã‚¦ã‚£ãƒ³ã‚° ã‚ªãƒ¼ãƒ©
            pc.sendPackets(new S_SkillIconAura(113, _getBuffIconDuration));
        }
        else if (_skillId == SHINING_AURA) { // ã‚·ãƒ£ã‚¤ãƒ‹ãƒ³ã‚° ã‚ªãƒ¼ãƒ©
            pc.sendPackets(new S_SkillIconAura(114, _getBuffIconDuration));
        }
        else if (_skillId == BRAVE_AURA) { // ãƒ–ãƒ¬ã‚¤ãƒ– ã‚ªãƒ¼ãƒ©
            pc.sendPackets(new S_SkillIconAura(116, _getBuffIconDuration));
        }
        else if (_skillId == FIRE_WEAPON) { // ãƒ•ã‚¡ã‚¤ã‚¢ãƒ¼ ã‚¦ã‚§ãƒ�ãƒ³
            pc.sendPackets(new S_SkillIconAura(147, _getBuffIconDuration));
        }
        else if (_skillId == WIND_SHOT) { // ã‚¦ã‚£ãƒ³ãƒ‰ ã‚·ãƒ§ãƒƒãƒˆ
            pc.sendPackets(new S_SkillIconAura(148, _getBuffIconDuration));
        }
        else if (_skillId == FIRE_BLESS) { // ãƒ•ã‚¡ã‚¤ã‚¢ãƒ¼ ãƒ–ãƒ¬ã‚¹
            pc.sendPackets(new S_SkillIconAura(154, _getBuffIconDuration));
        }
        else if (_skillId == STORM_EYE) { // ã‚¹ãƒˆãƒ¼ãƒ  ã‚¢ã‚¤
            pc.sendPackets(new S_SkillIconAura(155, _getBuffIconDuration));
        }
        else if (_skillId == EARTH_BLESS) { // ã‚¢ãƒ¼ã‚¹ ãƒ–ãƒ¬ã‚¹
            pc.sendPackets(new S_SkillIconShield(7, _getBuffIconDuration));
        }
        else if (_skillId == BURNING_WEAPON) { // ãƒ�ãƒ¼ãƒ‹ãƒ³ã‚° ã‚¦ã‚§ãƒ�ãƒ³
            pc.sendPackets(new S_SkillIconAura(162, _getBuffIconDuration));
        }
        else if (_skillId == STORM_SHOT) { // ã‚¹ãƒˆãƒ¼ãƒ  ã‚·ãƒ§ãƒƒãƒˆ
            pc.sendPackets(new S_SkillIconAura(165, _getBuffIconDuration));
        }
        else if (_skillId == IRON_SKIN) { // ã‚¢ã‚¤ã‚¢ãƒ³ ã‚¹ã‚­ãƒ³
            pc.sendPackets(new S_SkillIconShield(10, _getBuffIconDuration));
        }
        else if (_skillId == EARTH_SKIN) { // ã‚¢ãƒ¼ã‚¹ ã‚¹ã‚­ãƒ³
            pc.sendPackets(new S_SkillIconShield(6, _getBuffIconDuration));
        }
        else if (_skillId == PHYSICAL_ENCHANT_STR) { // ãƒ•ã‚£ã‚¸ã‚«ãƒ« ã‚¨ãƒ³ãƒ�ãƒ£ãƒ³ãƒˆï¼šSTR
            pc.sendPackets(new S_Strup(pc, 5, _getBuffIconDuration));
        }
        else if (_skillId == PHYSICAL_ENCHANT_DEX) { // ãƒ•ã‚£ã‚¸ã‚«ãƒ« ã‚¨ãƒ³ãƒ�ãƒ£ãƒ³ãƒˆï¼šDEX
            pc.sendPackets(new S_Dexup(pc, 5, _getBuffIconDuration));
        }
        else if ((_skillId == HASTE) || (_skillId == GREATER_HASTE)) { // ã‚°ãƒ¬ãƒ¼ã‚¿ãƒ¼ãƒ˜ã‚¤ã‚¹ãƒˆ
            pc.sendPackets(new S_SkillHaste(pc.getId(), 1, _getBuffIconDuration));
            pc.broadcastPacket(new S_SkillHaste(pc.getId(), 1, 0));
        }
        else if ((_skillId == HOLY_WALK) || (_skillId == MOVING_ACCELERATION) || (_skillId == WIND_WALK)) { // ãƒ›ãƒ¼ãƒªãƒ¼ã‚¦ã‚©ãƒ¼ã‚¯ã€�ãƒ ãƒ¼ãƒ“ãƒ³ã‚°ã‚¢ã‚¯ã‚»ãƒ¬ãƒ¼ã‚·ãƒ§ãƒ³ã€�ã‚¦ã‚£ãƒ³ãƒ‰ã‚¦ã‚©ãƒ¼ã‚¯
            pc.sendPackets(new S_SkillBrave(pc.getId(), 4, _getBuffIconDuration));
            pc.broadcastPacket(new S_SkillBrave(pc.getId(), 4, 0));
        }
        else if (_skillId == BLOODLUST) { // ãƒ–ãƒ©ãƒƒãƒ‰ãƒ©ã‚¹ãƒˆ
            pc.sendPackets(new S_SkillBrave(pc.getId(), 6, _getBuffIconDuration));
            pc.broadcastPacket(new S_SkillBrave(pc.getId(), 6, 0));
        }
        else if ((_skillId == SLOW) || (_skillId == MASS_SLOW) || (_skillId == ENTANGLE)) { // ã‚¹ãƒ­ãƒ¼ã€�ã‚¨ãƒ³ã‚¿ãƒ³ã‚°ãƒ«ã€�ãƒžã‚¹ã‚¹ãƒ­ãƒ¼
            pc.sendPackets(new S_SkillHaste(pc.getId(), 2, _getBuffIconDuration));
            pc.broadcastPacket(new S_SkillHaste(pc.getId(), 2, 0));
        }
        else if (_skillId == IMMUNE_TO_HARM) {
            pc.sendPackets(new S_SkillIconGFX(40, _getBuffIconDuration));
        }
        else if (_skillId == WIND_SHACKLE) { // é¢¨ä¹‹æž·éŽ–
            pc.sendPackets(new S_SkillIconWindShackle(pc.getId(), _getBuffIconDuration));
            pc.broadcastPacket(new S_SkillIconWindShackle(pc.getId(), _getBuffIconDuration));
        }
        pc.sendPackets(new S_OwnCharStatus(pc));
    }


    public void sendIcon(L1PcInstance pc, int skillId, int buffIconDuration) {
        if (skillId == SHIELD) { // ã‚·ãƒ¼ãƒ«ãƒ‰
            pc.sendPackets(new S_SkillIconShield(5, buffIconDuration));
        }
        else if (skillId == SHADOW_ARMOR) { // ã‚·ãƒ£ãƒ‰ã‚¦ ã‚¢ãƒ¼ãƒžãƒ¼
            pc.sendPackets(new S_SkillIconShield(3, buffIconDuration));
        }
        else if (skillId == DRESS_DEXTERITY) { // ãƒ‰ãƒ¬ã‚¹ ãƒ‡ã‚¯ã‚¹ã‚¿ãƒªãƒ†ã‚£ãƒ¼
            pc.sendPackets(new S_Dexup(pc, 2, buffIconDuration));
        }
        else if (skillId == DRESS_MIGHTY) { // ãƒ‰ãƒ¬ã‚¹ ãƒžã‚¤ãƒ†ã‚£ãƒ¼
            pc.sendPackets(new S_Strup(pc, 2, buffIconDuration));
        }
        else if (skillId == GLOWING_AURA) { // ã‚°ãƒ­ãƒ¼ã‚¦ã‚£ãƒ³ã‚° ã‚ªãƒ¼ãƒ©
            pc.sendPackets(new S_SkillIconAura(113, buffIconDuration));
        }
        else if (skillId == SHINING_AURA) { // ã‚·ãƒ£ã‚¤ãƒ‹ãƒ³ã‚° ã‚ªãƒ¼ãƒ©
            pc.sendPackets(new S_SkillIconAura(114, buffIconDuration));
        }
        else if (skillId == BRAVE_AURA) { // ãƒ–ãƒ¬ã‚¤ãƒ– ã‚ªãƒ¼ãƒ©
            pc.sendPackets(new S_SkillIconAura(116, buffIconDuration));
        }
        else if (skillId == FIRE_WEAPON) { // ãƒ•ã‚¡ã‚¤ã‚¢ãƒ¼ ã‚¦ã‚§ãƒ�ãƒ³
            pc.sendPackets(new S_SkillIconAura(147, buffIconDuration));
        }
        else if (skillId == WIND_SHOT) { // ã‚¦ã‚£ãƒ³ãƒ‰ ã‚·ãƒ§ãƒƒãƒˆ
            pc.sendPackets(new S_SkillIconAura(148, buffIconDuration));
        }
        else if (skillId == FIRE_BLESS) { // ãƒ•ã‚¡ã‚¤ã‚¢ãƒ¼ ãƒ–ãƒ¬ã‚¹
            pc.sendPackets(new S_SkillIconAura(154, buffIconDuration));
        }
        else if (skillId == STORM_EYE) { // ã‚¹ãƒˆãƒ¼ãƒ  ã‚¢ã‚¤
            pc.sendPackets(new S_SkillIconAura(155, buffIconDuration));
        }
        else if (skillId == EARTH_BLESS) { // ã‚¢ãƒ¼ã‚¹ ãƒ–ãƒ¬ã‚¹
            pc.sendPackets(new S_SkillIconShield(7, buffIconDuration));
        }
        else if (skillId == BURNING_WEAPON) { // ãƒ�ãƒ¼ãƒ‹ãƒ³ã‚° ã‚¦ã‚§ãƒ�ãƒ³
            pc.sendPackets(new S_SkillIconAura(162, buffIconDuration));
        }
        else if (skillId == STORM_SHOT) { // ã‚¹ãƒˆãƒ¼ãƒ  ã‚·ãƒ§ãƒƒãƒˆ
            pc.sendPackets(new S_SkillIconAura(165, buffIconDuration));
        }
        else if (skillId == IRON_SKIN) { // ã‚¢ã‚¤ã‚¢ãƒ³ ã‚¹ã‚­ãƒ³
            pc.sendPackets(new S_SkillIconShield(10, buffIconDuration));
        }
        else if (skillId == EARTH_SKIN) { // ã‚¢ãƒ¼ã‚¹ ã‚¹ã‚­ãƒ³
            pc.sendPackets(new S_SkillIconShield(6, buffIconDuration));
        }
        else if (skillId == PHYSICAL_ENCHANT_STR) { // ãƒ•ã‚£ã‚¸ã‚«ãƒ« ã‚¨ãƒ³ãƒ�ãƒ£ãƒ³ãƒˆï¼šSTR
            pc.sendPackets(new S_Strup(pc, 5, buffIconDuration));
        }
        else if (skillId == PHYSICAL_ENCHANT_DEX) { // ãƒ•ã‚£ã‚¸ã‚«ãƒ« ã‚¨ãƒ³ãƒ�ãƒ£ãƒ³ãƒˆï¼šDEX
            pc.sendPackets(new S_Dexup(pc, 5, buffIconDuration));
        }
        else if ((skillId == HASTE) || (skillId == GREATER_HASTE)) { // ã‚°ãƒ¬ãƒ¼ã‚¿ãƒ¼ãƒ˜ã‚¤ã‚¹ãƒˆ
            pc.sendPackets(new S_SkillHaste(pc.getId(), 1, buffIconDuration));
            pc.broadcastPacket(new S_SkillHaste(pc.getId(), 1, 0));
        }
        else if ((skillId == HOLY_WALK) || (skillId == MOVING_ACCELERATION) || (skillId == WIND_WALK)) { // ãƒ›ãƒ¼ãƒªãƒ¼ã‚¦ã‚©ãƒ¼ã‚¯ã€�ãƒ ãƒ¼ãƒ“ãƒ³ã‚°ã‚¢ã‚¯ã‚»ãƒ¬ãƒ¼ã‚·ãƒ§ãƒ³ã€�ã‚¦ã‚£ãƒ³ãƒ‰ã‚¦ã‚©ãƒ¼ã‚¯
            pc.sendPackets(new S_SkillBrave(pc.getId(), 4, buffIconDuration));
            pc.broadcastPacket(new S_SkillBrave(pc.getId(), 4, 0));
        }
        else if (skillId == BLOODLUST) {
            pc.sendPackets(new S_SkillBrave(pc.getId(), 6, buffIconDuration));
            pc.broadcastPacket(new S_SkillBrave(pc.getId(), 6, 0));
        }
        else if ((skillId == SLOW) || (skillId == MASS_SLOW) || (skillId == ENTANGLE)) { // ã‚¹ãƒ­ãƒ¼ã€�ã‚¨ãƒ³ã‚¿ãƒ³ã‚°ãƒ«ã€�ãƒžã‚¹ã‚¹ãƒ­ãƒ¼
            pc.sendPackets(new S_SkillHaste(pc.getId(), 2, buffIconDuration));
            pc.broadcastPacket(new S_SkillHaste(pc.getId(), 2, 0));
        }
        else if (skillId == IMMUNE_TO_HARM) {
            pc.sendPackets(new S_SkillIconGFX(40, buffIconDuration));
        }
        else if (skillId == WIND_SHACKLE) { // é¢¨ä¹‹æž·éŽ–
            pc.sendPackets(new S_SkillIconWindShackle(pc.getId(), buffIconDuration));
            pc.broadcastPacket(new S_SkillIconWindShackle(pc.getId(), buffIconDuration));
        }
        else if (skillId == BURNING_SPIRIT)
        {
            pc.sendPackets(new S_SkillIconAura(162, buffIconDuration));
        }
        pc.sendPackets(new S_OwnCharStatus(pc));
    }
    // ã‚°ãƒ©ãƒ•ã‚£ãƒƒã‚¯ã�®é€�ä¿¡
    private void sendGrfx(boolean isSkillAction) {
        if (_actid == 0) {
            _actid = _skill.getActionId();
        }
        if (_gfxid == 0) {
            _gfxid = _skill.getCastGfx();
        }
        if (_gfxid == 0) {
            return; // è¡¨ç¤ºã�™ã‚‹ã‚°ãƒ©ãƒ•ã‚£ãƒƒã‚¯ã�Œç„¡ã�„
        }
        int[] data = null;

        if (_user instanceof L1PcInstance) {

            int targetid = 0;
            if (_skillId != FIRE_WALL) {
                targetid = _target.getId();
            }
            L1PcInstance pc = (L1PcInstance) _user;

            switch(_skillId) {
                case FIRE_WALL: // ç�«ç‰¢
                case LIFE_STREAM: // æ²»ç™’èƒ½é‡�é¢¨æš´
                case ELEMENTAL_FALL_DOWN: // å¼±åŒ–å±¬æ€§
                    if (_skillId == FIRE_WALL) {
                        pc.setHeading(pc.targetDirection(_targetX, _targetY));
                        pc.sendPackets(new S_ChangeHeading(pc));
                        pc.broadcastPacket(new S_ChangeHeading(pc));
                    }
                    S_DoActionGFX gfx = new S_DoActionGFX(pc.getId(), _actid);
                    pc.sendPackets(gfx);
                    pc.broadcastPacket(gfx);
                    return;
                case SHOCK_STUN: // è¡�æ“Šä¹‹æšˆ
                    if (_targetList.isEmpty()) { // å¤±æ•—
                        return;
                    } else {
                        if (_target instanceof L1PcInstance) {
                            L1PcInstance targetPc = (L1PcInstance) _target;
                            targetPc.sendPackets(new S_SkillSound(targetid, 4434));
                            targetPc.broadcastPacket(new S_SkillSound(targetid, 4434));
                        } else if (_target instanceof L1NpcInstance) {
                            _target.broadcastPacket(new S_SkillSound(targetid, 4434));
                        }
                        return;
                    }
                case LIGHT: // æ—¥å…‰è¡“
                    pc.sendPackets(new S_Sound(145));
                    break;
                case MIND_BREAK: // å¿ƒé�ˆç ´å£ž
                case JOY_OF_PAIN: // ç–¼ç—›çš„æ­¡æ„‰
                    data = new int[] {_actid, _dmg, 0}; // data = {actid, dmg, effect}
                    pc.sendPackets(new S_AttackPacket(pc, targetid, data));
                    pc.broadcastPacket(new S_AttackPacket(pc, targetid, data));
                    pc.sendPackets(new S_SkillSound(targetid, _gfxid));
                    pc.broadcastPacket(new S_SkillSound(targetid, _gfxid));
                    return;
                case CONFUSION: // æ··äº‚
                    data = new int[] {_actid, _dmg, 0}; // data = {actid, dmg, effect}
                    pc.sendPackets(new S_AttackPacket(pc, targetid, data));
                    pc.broadcastPacket(new S_AttackPacket(pc, targetid, data));
                    return;
                case SMASH: // æš´æ“Š
                    pc.sendPackets(new S_SkillSound(targetid, _gfxid));
                    pc.broadcastPacket(new S_SkillSound(targetid, _gfxid));
                    return;
                case TAMING_MONSTER: // è¿·é­…
                    pc.sendPackets(new S_EffectLocation(_targetX, _targetY, _gfxid));
                    pc.broadcastPacket(new S_EffectLocation(_targetX, _targetY, _gfxid));
                    return;
                default:
                    break;
            }

/*			if (_skillId == BONE_BREAK || _skillId == ARM_BREAKER) {
				return;
			}*/

            if (_skillId == ARM_BREAKER) return;

            if (_targetList.isEmpty() && !(_skill.getTarget().equals("none"))) {
                // ã‚¿ãƒ¼ã‚²ãƒƒãƒˆæ•°ã�Œï¼�ã�§å¯¾è±¡ã‚’æŒ‡å®šã�™ã‚‹ã‚¹ã‚­ãƒ«ã�®å ´å�ˆã€�é­”æ³•ä½¿ç”¨ã‚¨ãƒ•ã‚§ã‚¯ãƒˆã� ã�‘è¡¨ç¤ºã�—ã�¦çµ‚äº†
                int tempchargfx = _player.getTempCharGfx();
                if ((tempchargfx == 5727) || (tempchargfx == 5730)) { // ã‚·ãƒ£ãƒ‰ã‚¦ç³»å¤‰èº«ã�®ãƒ¢ãƒ¼ã‚·ãƒ§ãƒ³å¯¾å¿œ
                    _actid = ActionCodes.ACTION_SkillBuff;
                }
                else if ((tempchargfx == 5733) || (tempchargfx == 5736)) {
                    _actid = ActionCodes.ACTION_Attack;
                }
                if (isSkillAction) {
                    S_DoActionGFX gfx = new S_DoActionGFX(_player.getId(), _actid);
                    _player.sendPackets(gfx);
                    _player.broadcastPacket(gfx);
                }
                return;
            }

            if (_skill.getTarget().equals("attack") && (_skillId != 18)) {
                if (isPcSummonPet(_target)) { // ç›®æ¨™çŽ©å®¶ã€�å¯µç‰©ã€�å�¬å–šç�¸
                    if ((_player.getZoneType() == 1) || (_target.getZoneType() == 1)
                            || _player.checkNonPvP(_player, _target)) { // Non-PvPè¨­å®š
                        data = new int[] {_actid, 0, _gfxid, 6};
                        _player.sendPackets(new S_UseAttackSkill(_player, _target.getId(), _targetX, _targetY, data));
                        _player.broadcastPacket(new S_UseAttackSkill(_player, _target.getId(), _targetX, _targetY, data));
                        return;
                    }
                }

                if (getSkillArea() == 0) { // å–®é«”æ”»æ“Šé­”æ³•
                    data = new int[] {_actid, _dmg, _gfxid, 6};
                    _player.sendPackets(new S_UseAttackSkill(_player, targetid, _targetX, _targetY, data));
                    _player.broadcastPacket(new S_UseAttackSkill(_player, targetid, _targetX, _targetY, data));
                    _target.broadcastPacketExceptTargetSight(new S_DoActionGFX(targetid, ActionCodes.ACTION_Damage), _player);
                }
                else { // æœ‰æ–¹å�‘ç¯„å›²æ”»æ’ƒé­”æ³•
                    L1Character[] cha = new L1Character[_targetList.size()];
                    int i = 0;
                    for (TargetStatus ts : _targetList) {
                        cha[i] = ts.getTarget();
                        i++;
                    }
                    _player.sendPackets(new S_RangeSkill(_player, cha, _gfxid, _actid, S_RangeSkill.TYPE_DIR));
                    _player.broadcastPacket(new S_RangeSkill(_player, cha, _gfxid, _actid, S_RangeSkill.TYPE_DIR));
                }
            }
            else if (_skill.getTarget().equals("none") && (_skill.getType() == L1Skills.TYPE_ATTACK)) { // ç„¡æ–¹å�‘ç¯„å›²æ”»æ’ƒé­”æ³•
                L1Character[] cha = new L1Character[_targetList.size()];
                int i = 0;
                for (TargetStatus ts : _targetList) {
                    cha[i] = ts.getTarget();
                    cha[i].broadcastPacketExceptTargetSight(new S_DoActionGFX(cha[i].getId(), ActionCodes.ACTION_Damage), _player);
                    i++;
                }
                _player.sendPackets(new S_RangeSkill(_player, cha, _gfxid, _actid, S_RangeSkill.TYPE_NODIR));
                _player.broadcastPacket(new S_RangeSkill(_player, cha, _gfxid, _actid, S_RangeSkill.TYPE_NODIR));
            }
            else { // è£œåŠ©é­”æ³•
                // æŒ‡å®šå‚³é€�ã€�é›†é«”å‚³é€�è¡“ã€�ä¸–ç•Œæ¨¹çš„å‘¼å–šä»¥å¤–
                if ((_skillId != TELEPORT) && (_skillId != MASS_TELEPORT) && (_skillId != TELEPORT_TO_MATHER)) {
                    // æ–½æ³•å‹•ä½œ
                    if (isSkillAction) {
                        S_DoActionGFX gfx = new S_DoActionGFX(_player.getId(), _skill.getActionId());
                        _player.sendPackets(gfx);
                        _player.broadcastPacket(gfx);
                    }
                    // é­”æ³•å±�éšœã€�å��æ“Šå±�éšœã€�é�¡å��å°„ é­”æ³•æ•ˆæžœå�ªæœ‰è‡ªèº«é¡¯ç¤º
                    if ((_skillId == COUNTER_MAGIC) || (_skillId == COUNTER_BARRIER) || (_skillId == COUNTER_MIRROR)) {
                        _player.sendPackets(new S_SkillSound(targetid, _gfxid));
                    }
                    else if ((_skillId == AWAKEN_ANTHARAS) || (_skillId == AWAKEN_FAFURION) || (_skillId == AWAKEN_VALAKAS)) {
                        if (_skillId == _player.getAwakeSkillId()) {
                            _player.sendPackets(new S_SkillSound(targetid, _gfxid));
                            _player.broadcastPacket(new S_SkillSound(targetid, _gfxid));
                        }
                        else {
                            return;
                        }
                    }
                    else {
                        _player.sendPackets(new S_SkillSound(targetid, _gfxid));
                        _player.broadcastPacket(new S_SkillSound(targetid, _gfxid));
                    }
                }

                // ã‚¹ã‚­ãƒ«ã�®ã‚¨ãƒ•ã‚§ã‚¯ãƒˆè¡¨ç¤ºã�¯ã‚¿ãƒ¼ã‚²ãƒƒãƒˆå…¨å“¡ã� ã�Œã€�ã�‚ã�¾ã‚Šå¿…è¦�æ€§ã�Œã�ªã�„ã�®ã�§ã€�ã‚¹ãƒ†ãƒ¼ã‚¿ã‚¹ã�®ã�¿é€�ä¿¡
                for (TargetStatus ts : _targetList) {
                    L1Character cha = ts.getTarget();
                    if (cha instanceof L1PcInstance) {
                        L1PcInstance chaPc = (L1PcInstance) cha;
                        chaPc.sendPackets(new S_OwnCharStatus(chaPc));
                    }
                }
            }
        }
        else if (_user instanceof L1NpcInstance) { // NPCã�Œã‚¹ã‚­ãƒ«ã‚’ä½¿ã�£ã�Ÿå ´å�ˆ
            int targetid = _target.getId();

            if (_user instanceof L1MerchantInstance) {
                _user.broadcastPacket(new S_SkillSound(targetid, _gfxid));
                return;
            }

            if (_skillId == CURSE_PARALYZE || _skillId == WEAKNESS || _skillId == DISEASE) { // æœ¨ä¹ƒä¼Šçš„è©›å’’ã€�å¼±åŒ–è¡“ã€�ç–¾ç—…è¡“
                _user.setHeading(_user.targetDirection(_targetX, _targetY)); // æ”¹è®Šé�¢å�‘
                _user.broadcastPacket(new S_ChangeHeading(_user));
            }

            if (_targetList.isEmpty() && !(_skill.getTarget().equals("none"))) {
                // ã‚¿ãƒ¼ã‚²ãƒƒãƒˆæ•°ã�Œï¼�ã�§å¯¾è±¡ã‚’æŒ‡å®šã�™ã‚‹ã‚¹ã‚­ãƒ«ã�®å ´å�ˆã€�é­”æ³•ä½¿ç”¨ã‚¨ãƒ•ã‚§ã‚¯ãƒˆã� ã�‘è¡¨ç¤ºã�—ã�¦çµ‚äº†
                S_DoActionGFX gfx = new S_DoActionGFX(_user.getId(), _actid);
                _user.broadcastPacket(gfx);
                return;
            }

            if (_skill.getTarget().equals("attack") && (_skillId != 18)) {
                if (getSkillArea() == 0) { // å–®é«”æ”»æ“Šé­”æ³•
                    data = new int[] {_actid, _dmg, _gfxid, 6};
                    _user.broadcastPacket(new S_UseAttackSkill(_user, targetid, _targetX, _targetY, data));
                    _target.broadcastPacketExceptTargetSight(new S_DoActionGFX(targetid, ActionCodes.ACTION_Damage), _user);
                }
                else { // æœ‰æ–¹å�‘ç¯„å›²æ”»æ’ƒé­”æ³•
                    L1Character[] cha = new L1Character[_targetList.size()];
                    int i = 0;
                    for (TargetStatus ts : _targetList) {
                        cha[i] = ts.getTarget();
                        cha[i].broadcastPacketExceptTargetSight(new S_DoActionGFX(cha[i].getId(), ActionCodes.ACTION_Damage), _user);
                        i++;
                    }
                    _user.broadcastPacket(new S_RangeSkill(_user, cha, _gfxid, _actid, S_RangeSkill.TYPE_DIR));
                }
            }
            else if (_skill.getTarget().equals("none") && (_skill.getType() == L1Skills.TYPE_ATTACK)) { // ç„¡æ–¹å�‘ç¯„å›²æ”»æ’ƒé­”æ³•
                L1Character[] cha = new L1Character[_targetList.size()];
                int i = 0;
                for (TargetStatus ts : _targetList) {
                    cha[i] = ts.getTarget();
                    i++;
                }
                _user.broadcastPacket(new S_RangeSkill(_user, cha, _gfxid, _actid, S_RangeSkill.TYPE_NODIR));
            }
            else { // è£œåŠ©é­”æ³•
                // ãƒ†ãƒ¬ãƒ�ãƒ¼ãƒˆã€�ãƒžã‚¹ãƒ†ãƒ¬ã€�ãƒ†ãƒ¬ãƒ�ãƒ¼ãƒˆãƒˆã‚¥ãƒžã‚¶ãƒ¼ä»¥å¤–
                if ((_skillId != 5) && (_skillId != 69) && (_skillId != 131)) {
                    // é­”æ³•ã‚’ä½¿ã�†å‹•ä½œã�®ã‚¨ãƒ•ã‚§ã‚¯ãƒˆã�¯ä½¿ç”¨è€…ã� ã�‘
                    S_DoActionGFX gfx = new S_DoActionGFX(_user.getId(), _actid);
                    _user.broadcastPacket(gfx);
                    _user.broadcastPacket(new S_SkillSound(targetid, _gfxid));
                }
            }
        }
    }

    /** åˆªé™¤é‡�è¤‡çš„é­”æ³•ç‹€æ…‹ */
    private void deleteRepeatedSkills(L1Character cha) {
        final int[][] repeatedSkills =
                {

                        // ç�«ç„°æ­¦å™¨ã€�é¢¨ä¹‹ç¥žå°„ã€�çƒˆç‚Žæ°£æ�¯ã€�æš´é¢¨ä¹‹çœ¼ã€�çƒˆç‚Žæ­¦å™¨ã€�æš´é¢¨ç¥žå°„ã€�åª½ç¥–çš„ç¥�ç¦�
                        { FIRE_WEAPON, WIND_SHOT, FIRE_BLESS, STORM_EYE, BURNING_WEAPON, STORM_SHOT, EFFECT_BLESS_OF_MAZU },
                        // é˜²è­·ç½©ã€�å½±ä¹‹é˜²è­·ã€�å¤§åœ°é˜²è­·ã€�å¤§åœ°çš„ç¥�ç¦�ã€�é‹¼é�µé˜²è­·
                        { SHIELD, SHADOW_ARMOR, EARTH_SKIN, EARTH_BLESS, IRON_SKIN },
                        // å‹‡æ•¢è—¥æ°´ã€�ç²¾é�ˆé¤…ä¹¾ã€�(ç¥žè�–ç–¾èµ°ã€�è¡Œèµ°åŠ é€Ÿã€�é¢¨ä¹‹ç–¾èµ°)ã€�è¶…ç´šåŠ é€Ÿã€�è¡€ä¹‹æ¸´æœ›
                        { STATUS_BRAVE, STATUS_ELFBRAVE, HOLY_WALK, MOVING_ACCELERATION, WIND_WALK, STATUS_BRAVE2, BLOODLUST },
                        // åŠ é€Ÿè¡“ã€�å¼·åŠ›åŠ é€Ÿè¡“ã€�è‡ªæˆ‘åŠ é€Ÿè—¥æ°´
                        { HASTE, GREATER_HASTE, STATUS_HASTE },
                        // ç·©é€Ÿã€�é›†é«”ç·©è¡“ã€�åœ°é�¢éšœç¤™
                        { SLOW , MASS_SLOW , ENTANGLE },
                        // é€šæš¢æ°£è„ˆè¡“ã€�æ•�æ�·æ��å�‡
                        { PHYSICAL_ENCHANT_DEX, DRESS_DEXTERITY },
                        // é«”é­„å¼·å�¥è¡“ã€�åŠ›é‡�æ��å�‡
                        { PHYSICAL_ENCHANT_STR, DRESS_MIGHTY },
                        // æ¿€å‹µå£«æ°£ã€�é‹¼é�µå£«æ°£
                        { GLOWING_AURA, SHINING_AURA },
                        // é�¡åƒ�ã€�æš—å½±é–ƒé�¿
                        { MIRROR_IMAGE, UNCANNY_DODGE } };


        for (int[] skills : repeatedSkills) {
            for (int id : skills) {
                if (id == _skillId) {
                    stopSkillList(cha, skills);
                }
            }
        }
    }

    // é‡�è¤‡ã�—ã�¦ã�„ã‚‹ã‚¹ã‚­ãƒ«ã‚’ä¸€æ—¦ã�™ã�¹ã�¦å‰Šé™¤
    private void stopSkillList(L1Character cha, int[] repeat_skill) {
        for (int skillId : repeat_skill) {
            if (skillId != _skillId) {
                cha.removeSkillEffect(skillId);
            }
        }
    }

    // ãƒ‡ã‚£ãƒ¬ã‚¤ã�®è¨­å®š
    private void setDelay() {
        if (_skill.getReuseDelay() > 0) {
            L1SkillDelay.onSkillUse(_user, _skill.getReuseDelay());
        }
    }

    private void runSkill() {

        switch(_skillId) {
            case LIFE_STREAM:
                L1EffectSpawn.getInstance().spawnEffect(81169, _skill.getBuffDuration() * 1000, _targetX, _targetY, _user.getMapId());
                return;
            case CUBE_IGNITION:
                L1EffectSpawn.getInstance().spawnEffect(80149, _skill.getBuffDuration() * 1000, _targetX, _targetY, _user.getMapId(),
                        (L1PcInstance) _user, _skillId);
                return;
            case CUBE_QUAKE:
                L1EffectSpawn.getInstance().spawnEffect(80150, _skill.getBuffDuration() * 1000, _targetX, _targetY, _user.getMapId(),
                        (L1PcInstance) _user, _skillId);
                return;
            case CUBE_SHOCK:
                L1EffectSpawn.getInstance().spawnEffect(80151, _skill.getBuffDuration() * 1000, _targetX, _targetY, _user.getMapId(),
                        (L1PcInstance) _user, _skillId);
                return;
            case CUBE_BALANCE:
                L1EffectSpawn.getInstance().spawnEffect(80152, _skill.getBuffDuration() * 1000, _targetX, _targetY, _user.getMapId(),
                        (L1PcInstance) _user, _skillId);
                return;
            case FIRE_WALL: // ç�«ç‰¢
                L1EffectSpawn.getInstance().doSpawnFireWall(_user, _targetX, _targetY);
                return;
            case TRUE_TARGET: // ç²¾æº–ç›®æ¨™
                if (_user instanceof L1PcInstance) {
                    L1PcInstance pri = (L1PcInstance) _user;
                    L1EffectInstance effect = L1EffectSpawn.getInstance().spawnEffect(80153, 5 * 1000, _targetX + 2, _targetY - 1, _user.getMapId());
                    if (_targetID != 0) {
                        pri.sendPackets(new S_TrueTarget(_targetID, pri.getId(), _message));
                        if (pri.getClanid() != 0) {
                            L1PcInstance players[] = L1World.getInstance().getClan(pri.getClanname()).getOnlineClanMember();
                            for (L1PcInstance pc : players) {
                                pc.sendPackets(new S_TrueTarget(_targetID, pc.getId(), _message));
                            }
                        }
                    } else if (effect != null) {
                        pri.sendPackets(new S_TrueTarget(effect.getId(), pri.getId(), _message));
                        if (pri.getClanid() != 0) {
                            L1PcInstance players[] = L1World.getInstance().getClan(pri.getClanname()).getOnlineClanMember();
                            for (L1PcInstance pc : players) {
                                pc.sendPackets(new S_TrueTarget(effect.getId(), pc.getId(), _message));
                            }
                        }
                    }
                }
                return;
            default:
                break;
        }

        // é­”æ³•å±�éšœä¸�å�¯æŠµæ“‹çš„é­”æ³•
        for (int skillId : EXCEPT_COUNTER_MAGIC) {
            if (_skillId == skillId) {
                _isCounterMagic = false;
                break;
            }
        }

        // NPCã�«ã‚·ãƒ§ãƒƒã‚¯ã‚¹ã‚¿ãƒ³ã‚’ä½¿ç”¨ã�•ã�›ã‚‹ã�¨onActionã�§NullPointerExceptionã�Œç™ºç”Ÿã�™ã‚‹ã�Ÿã‚�
        // ã�¨ã‚Šã�‚ã�ˆã�šPCã�Œä½¿ç”¨ã�—ã�Ÿæ™‚ã�®ã�¿
        if ((_skillId == SHOCK_STUN) && (_user instanceof L1PcInstance)) {
            _target.onAction(_player);
        }

        if (!isTargetCalc(_target)) {
            return;
        }

        try {
            TargetStatus ts = null;
            L1Character cha = null;
            int dmg = 0;
            int drainMana = 0;
            int heal = 0;
            boolean isSuccess = false;
            int undeadType = 0;

            for (Iterator<TargetStatus> iter = _targetList.iterator(); iter.hasNext();) {
                ts = null;
                cha = null;
                dmg = 0;
                heal = 0;
                isSuccess = false;
                undeadType = 0;

                ts = iter.next();
                cha = ts.getTarget();

                if (!ts.isCalc() || !isTargetCalc(cha)) {
                    continue; // è¨ˆç®—ã�™ã‚‹å¿…è¦�ã�Œã�ªã�„ã€‚
                }

                L1Magic _magic = new L1Magic(_user, cha);
                _magic.setLeverage(getLeverage());

                if (cha instanceof L1MonsterInstance) { // ä¸�æ­»ä¿‚åˆ¤æ–·
                    undeadType = ((L1MonsterInstance) cha).getNpcTemplate().get_undead();
                }

                // ç¢ºçŽ‡ç³»ã‚¹ã‚­ãƒ«ã�§å¤±æ•—ã�Œç¢ºå®šã�—ã�¦ã�„ã‚‹å ´å�ˆ
                if (((_skill.getType() == L1Skills.TYPE_CURSE) || (_skill.getType() == L1Skills.TYPE_PROBABILITY)) && isTargetFailure(cha)) {
                    iter.remove();
                    continue;
                }

                if (cha instanceof L1PcInstance) { // ã‚¿ãƒ¼ã‚²ãƒƒãƒˆã�ŒPCã�®å ´å�ˆã�®ã�¿ã‚¢ã‚¤ã‚³ãƒ³ã�¯é€�ä¿¡ã�™ã‚‹ã€‚
                    if (_skillTime == 0) {
                        _getBuffIconDuration = _skill.getBuffDuration(); // åŠ¹æžœæ™‚é–“
                    }
                    else {
                        _getBuffIconDuration = _skillTime; // ãƒ‘ãƒ©ãƒ¡ãƒ¼ã‚¿ã�®timeã�Œ0ä»¥å¤–ã�ªã‚‰ã€�åŠ¹æžœæ™‚é–“ã�¨ã�—ã�¦è¨­å®šã�™ã‚‹
                    }
                }

                deleteRepeatedSkills(cha); // åˆªé™¤ç„¡æ³•å…±å�Œå­˜åœ¨çš„é­”æ³•ç‹€æ…‹

                if ((_skill.getType() == L1Skills.TYPE_ATTACK) && (_user.getId() != cha.getId())) { // æ”»æ’ƒç³»ã‚¹ã‚­ãƒ«ï¼†ã‚¿ãƒ¼ã‚²ãƒƒãƒˆã�Œä½¿ç”¨è€…ä»¥å¤–ã�§ã�‚ã‚‹ã�“ã�¨ã€‚
                    if (isUseCounterMagic(cha)) { // ã‚«ã‚¦ãƒ³ã‚¿ãƒ¼ãƒžã‚¸ãƒƒã‚¯ã�Œç™ºå‹•ã�—ã�Ÿå ´å�ˆã€�ãƒªã‚¹ãƒˆã�‹ã‚‰å‰Šé™¤
                        iter.remove();
                        continue;
                    }
                    dmg = _magic.calcMagicDamage(_skillId);
                    _dmg = dmg;
                    cha.removeSkillEffect(ERASE_MAGIC); // ã‚¤ãƒ¬ãƒ¼ã‚¹ãƒžã‚¸ãƒƒã‚¯ä¸­ã�ªã‚‰ã€�æ”»æ’ƒé­”æ³•ã�§è§£é™¤
                }
                else if ((_skill.getType() == L1Skills.TYPE_CURSE) || (_skill.getType() == L1Skills.TYPE_PROBABILITY)) { // ç¢ºçŽ‡ç³»ã‚¹ã‚­ãƒ«
                    isSuccess = _magic.calcProbabilityMagic(_skillId);
                    if (_skillId != ERASE_MAGIC) {
                        cha.removeSkillEffect(ERASE_MAGIC); // ã‚¤ãƒ¬ãƒ¼ã‚¹ãƒžã‚¸ãƒƒã‚¯ä¸­ã�ªã‚‰ã€�ç¢ºçŽ‡é­”æ³•ã�§è§£é™¤
                    }
                    if (_skillId != FOG_OF_SLEEPING) {
                        cha.removeSkillEffect(FOG_OF_SLEEPING); // ãƒ•ã‚©ã‚°ã‚ªãƒ–ã‚¹ãƒªãƒ¼ãƒ”ãƒ³ã‚°ä¸­ã�ªã‚‰ã€�ç¢ºçŽ‡é­”æ³•ã�§è§£é™¤
                    }
                    if (isSuccess) { // æˆ�åŠŸã�—ã�Ÿã�Œã‚«ã‚¦ãƒ³ã‚¿ãƒ¼ãƒžã‚¸ãƒƒã‚¯ã�Œç™ºå‹•ã�—ã�Ÿå ´å�ˆã€�ãƒªã‚¹ãƒˆã�‹ã‚‰å‰Šé™¤
                        if (isUseCounterMagic(cha)) { // ã‚«ã‚¦ãƒ³ã‚¿ãƒ¼ãƒžã‚¸ãƒƒã‚¯ã�Œç™ºå‹•ã�—ã�Ÿã�‹
                            iter.remove();
                            continue;
                        }
                    }
                    else { // å¤±æ•—ã�—ã�Ÿå ´å�ˆã€�ãƒªã‚¹ãƒˆã�‹ã‚‰å‰Šé™¤
                        if ((_skillId == FOG_OF_SLEEPING) && (cha instanceof L1PcInstance)) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.sendPackets(new S_ServerMessage(297)); // ä½ æ„Ÿè¦ºäº›å¾®åœ°æšˆçœ©ã€‚
                        }
                        iter.remove();
                        continue;
                    }
                }
                // æ²»ç™’æ€§é­”æ³•
                else if (_skill.getType() == L1Skills.TYPE_HEAL) {
                    // å›žå¾©é‡�
                    dmg = -1 * _magic.calcHealing(_skillId);
                    if (cha.hasSkillEffect(WATER_LIFE)) { // æ°´ä¹‹å…ƒæ°£-æ•ˆæžœ 2å€�
                        dmg *= 2;
                        cha.killSkillEffectTimer(WATER_LIFE); // æ•ˆæžœå�ªæœ‰ä¸€æ¬¡
                        if (cha instanceof L1PcInstance) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.sendPackets(new S_SkillIconWaterLife());
                        }
                    }
                    if (cha.hasSkillEffect(POLLUTE_WATER)) { // æ±™æ¿�ä¹‹æ°´-æ•ˆæžœæ¸›å�Š
                        dmg /= 2;
                    }
                }
                // é¡¯ç¤ºåœ˜é«”é­”æ³•æ•ˆæžœåœ¨éšŠå�‹æˆ–ç›Ÿå�‹
                else if ((_skillId == FIRE_BLESS || _skillId == STORM_EYE // çƒˆç‚Žæ°£æ�¯ã€�æš´é¢¨ä¹‹çœ¼
                        || _skillId == EARTH_BLESS // å¤§åœ°çš„ç¥�ç¦�
                        || _skillId == GLOWING_AURA // æ¿€å‹µå£«æ°£
                        || _skillId == SHINING_AURA || _skillId == BRAVE_AURA) // é‹¼é�µå£«æ°£ã€�è¡�æ“Šå£«æ°£
                        && _user.getId() != cha.getId()) {
                    if (cha instanceof L1PcInstance) {
                        L1PcInstance _targetPc = (L1PcInstance) cha;
                        _targetPc.sendPackets(new S_SkillSound(_targetPc.getId(), _skill.getCastGfx()));
                        _targetPc.broadcastPacket(new S_SkillSound(_targetPc.getId(), _skill.getCastGfx()));
                    }
                }

                // â– â– â– â–  å€‹åˆ¥å‡¦ç�†ã�®ã�‚ã‚‹ã‚¹ã‚­ãƒ«ã�®ã�¿æ›¸ã�„ã�¦ã��ã� ã�•ã�„ã€‚ â– â– â– â– 

                // é™¤äº†è¡�æšˆã€�éª·é«�æ¯€å£žä¹‹å¤–é­”æ³•æ•ˆæžœå­˜åœ¨æ™‚ï¼Œå�ªæ›´æ–°æ•ˆæžœæ™‚é–“è·Ÿåœ–ç¤ºã€‚
                if (cha.hasSkillEffect(_skillId) && (_skillId != SHOCK_STUN && _skillId != BONE_BREAK && _skillId != CONFUSION && _skillId != THUNDER_GRAB)) {
                    addMagicList(cha, true); // é­”æ³•æ•ˆæžœå·²å­˜åœ¨æ™‚
                    if (_skillId != SHAPE_CHANGE) { // é™¤äº†è®Šå½¢è¡“ä¹‹å¤–
                        continue;
                    }
                }

                switch(_skillId) {
                    // åŠ é€Ÿè¡“
                    case HASTE:
                        if (cha.getMoveSpeed() != 2) { // ã‚¹ãƒ­ãƒ¼ä¸­ä»¥å¤–
                            if (cha instanceof L1PcInstance) {
                                L1PcInstance pc = (L1PcInstance) cha;
                                if (pc.getHasteItemEquipped() > 0) {
                                    continue;
                                }
                                pc.setDrink(false);
                                pc.sendPackets(new S_SkillHaste(pc.getId(), 1, _getBuffIconDuration));
                            } // added
                            if (cha instanceof L1PetInstance) {
                                L1PetInstance pet = (L1PetInstance) cha;
                                if (pet.hasSkillEffect(STATUS_HASTE))
                                    pet.setMoveSpeed(0);
                                pet.setParalyzed(true);
                                pet.setParalyzed(false);
                            }
                            cha.broadcastPacket(new S_SkillHaste(cha.getId(), 1, 0));
                            cha.setMoveSpeed(1);
                        }
                        else { // ã‚¹ãƒ­ãƒ¼ä¸­
                            int skillNum = 0;
                            if (cha.hasSkillEffect(SLOW)) {
                                skillNum = SLOW;
                            }
                            else if (cha.hasSkillEffect(MASS_SLOW)) {
                                skillNum = MASS_SLOW;
                            }
                            else if (cha.hasSkillEffect(ENTANGLE)) {
                                skillNum = ENTANGLE;
                            }
                            if (skillNum != 0) {
                                cha.removeSkillEffect(skillNum);
                                cha.removeSkillEffect(HASTE);
                                cha.setMoveSpeed(0);
                                continue;
                            }
                        }
                        break;
                    // å¼·åŠ›åŠ é€Ÿè¡“
                    case GREATER_HASTE:
                        if (cha instanceof L1PcInstance) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            if (pc.getHasteItemEquipped() > 0) {
                                continue;
                            }
                            if (pc.getMoveSpeed() != 2) { // ã‚¹ãƒ­ãƒ¼ä¸­ä»¥å¤–
                                pc.setDrink(false);
                                pc.setMoveSpeed(1);
                                pc.sendPackets(new S_SkillHaste(pc.getId(), 1, _getBuffIconDuration));
                                pc.broadcastPacket(new S_SkillHaste(pc.getId(), 1, 0));
                            }
                            else { // ã‚¹ãƒ­ãƒ¼ä¸­
                                int skillNum = 0;
                                if (pc.hasSkillEffect(SLOW)) {
                                    skillNum = SLOW;
                                }
                                else if (pc.hasSkillEffect(MASS_SLOW)) {
                                    skillNum = MASS_SLOW;
                                }
                                else if (pc.hasSkillEffect(ENTANGLE)) {
                                    skillNum = ENTANGLE;
                                }
                                if (skillNum != 0) {
                                    pc.removeSkillEffect(skillNum);
                                    pc.removeSkillEffect(GREATER_HASTE);
                                    pc.setMoveSpeed(0);
                                    continue;
                                }
                            }
                        }
                        break;
                    // ç·©é€Ÿè¡“ã€�é›†é«”ç·©é€Ÿè¡“ã€�åœ°é�¢éšœç¤™
                    case SLOW:
                    case MASS_SLOW:
                    case ENTANGLE:
                        if (cha instanceof L1PcInstance) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            if (pc.getHasteItemEquipped() > 0) {
                                continue;
                            }
                        }
                        if (cha.getMoveSpeed() == 0) {
                            if (cha instanceof L1PcInstance) {
                                L1PcInstance pc = (L1PcInstance) cha;
                                pc.sendPackets(new S_SkillHaste(pc.getId(), 2, _getBuffIconDuration));
                            }
                            cha.broadcastPacket(new S_SkillHaste(cha.getId(), 2, _getBuffIconDuration));
                            cha.setMoveSpeed(2);
                        }
                        else if (cha.getMoveSpeed() == 1) {
                            int skillNum = 0;
                            if (cha.hasSkillEffect(HASTE)) {
                                skillNum = HASTE;
                            }
                            else if (cha.hasSkillEffect(GREATER_HASTE)) {
                                skillNum = GREATER_HASTE;
                            }
                            else if (cha.hasSkillEffect(STATUS_HASTE)) {
                                skillNum = STATUS_HASTE;
                            }
                            if (skillNum != 0) {
                                cha.removeSkillEffect(skillNum);
                                cha.removeSkillEffect(_skillId);
                                cha.setMoveSpeed(0);
                                continue;
                            }
                        }
                        break;
                    // [Legends] Phantasm
                    case PHANTASM:
                        if(cha instanceof L1PcInstance){
                            ((L1PcInstance) cha).sendPackets(new S_SystemMessage("Fantasm has put you to sleep"));
                        }
                        else if(cha instanceof  L1NpcInstance) {
                            System.out.println("Putting " + cha.getName() + " to sleep");
                        }
                        cha.setSleeped(true);
                        break;
                    case CHILL_TOUCH:
                    case VAMPIRIC_TOUCH:
                        heal = dmg;
                        break;
                    // äºžåŠ›å®‰å†°çŸ›åœ�ç±¬
                    case ICE_LANCE_COCKATRICE:
                        // é‚ªæƒ¡èœ¥èœ´å†°çŸ›åœ�ç±¬
                    case ICE_LANCE_BASILISK:
                        // å†°æ¯›åœ�ç±¬ã€�å†°é›ªé¢¶é¢¨ã€�å¯’å†°å™´å��
                    case ICE_LANCE:
                    case FREEZING_BLIZZARD:
                    case FREEZING_BREATH:
                        _isFreeze = _magic.calcProbabilityMagic(_skillId);
                        if (_isFreeze) {
                            int time = _skill.getBuffDuration() * 1000;
                            L1EffectSpawn.getInstance().spawnEffect(81168, time, cha.getX(), cha.getY(), cha.getMapId());
                            if (cha instanceof L1PcInstance) {
                                L1PcInstance pc = (L1PcInstance) cha;
                                pc.sendPackets(new S_Poison(pc.getId(), 2));
                                pc.broadcastPacket(new S_Poison(pc.getId(), 2));
                                pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_FREEZE, true));
                            }
                            else if ((cha instanceof L1MonsterInstance) || (cha instanceof L1SummonInstance) || (cha instanceof L1PetInstance)) {
                                L1NpcInstance npc = (L1NpcInstance) cha;
                                npc.broadcastPacket(new S_Poison(npc.getId(), 2));
                                npc.setParalyzed(true);
                                npc.setParalysisTime(time);
                            }
                        }
                        break;
                    // å¤§åœ°å±�éšœ
                    case EARTH_BIND:

                        Random rn = new Random();
                        _earthBindDuration = (rn.nextInt(10) + 5) * 1000;


                        if (cha instanceof L1PcInstance) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.sendPackets(new S_Poison(pc.getId(), 2));
                            pc.broadcastPacket(new S_Poison(pc.getId(), 2));
                            pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_FREEZE, true));
                        }
                        else if ((cha instanceof L1MonsterInstance) || (cha instanceof L1SummonInstance) || (cha instanceof L1PetInstance)) {
                            L1NpcInstance npc = (L1NpcInstance) cha;
                            npc.broadcastPacket(new S_Poison(npc.getId(), 2));
                            npc.setParalyzed(true);
                            npc.setParalysisTime(_earthBindDuration);
                        }
                        break;
                    case 20011: // æ¯’éœ§-å‰�æ–¹ 3X3
                        _user.setHeading(_user.targetDirection(_targetX, _targetY)); // æ”¹è®Šé�¢å�‘
                        int locX = 0;
                        int locY = 0;
                        for (int i = 0; i < 3; i++) {
                            for (int j = 0; j < 3; j++) {
                                switch (_user.getHeading()) {
                                    case 0:
                                        locX = (-1 + j);
                                        locY = -1 * (-3 + i);
                                        break;
                                    case 1:
                                        locX = -1 * (2 + j - i);
                                        locY = -1 * (-4 + j + i);
                                        break;
                                    case 2:
                                        locX = -1 * (3 - i);
                                        locY = (-1 + j);
                                        break;
                                    case 3:
                                        locX = -1 * (4 - j - i);
                                        locY = -1 * (2 + j - i);
                                        break;
                                    case 4:
                                        locX = (1 - j);
                                        locY = -1 * (3 - i);
                                        break;
                                    case 5:
                                        locX = -1 * (-2 - j + i);
                                        locY = -1 * (4 - j - i);
                                        break;
                                    case 6:
                                        locX = -1 * (-3 + i);
                                        locY = (1 - j);
                                        break;
                                    case 7:
                                        locX = -1 * (-4 + j + i);
                                        locY = -1 * (-2 - j + i);
                                        break;
                                }
                                L1EffectSpawn.getInstance().spawnEffect(93002, 10000, _user.getX() - locX, _user.getY() - locY, _user.getMapId());
                            }
                        }
                        break;
                    // è¡�æ“Šä¹‹æšˆ
                    case SHOCK_STUN:
                        int targetLevel = 0;
                        int diffLevel = 0;
                        int stunTime = 0;
                        if (cha instanceof L1PcInstance) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            targetLevel = pc.getLevel();
                        } else if (cha instanceof L1MonsterInstance
                                || cha instanceof L1SummonInstance
                                || cha instanceof L1PetInstance) {
                            L1NpcInstance npc = (L1NpcInstance) cha;
                            targetLevel = npc.getLevel();
                        }
                        diffLevel = _user.getLevel() - targetLevel;
                        RandomGenerator random = RandomGeneratorFactory.getSharedRandom();

                        int basechance = random.nextInt(99) + 1;

                        int chance = basechance+(diffLevel*5);

                        if (chance>90) {
                            stunTime = 6000;
                        } else if (chance > 85) {
                            stunTime = 5500;
                        } else if (chance > 80) {
                            stunTime = 5000;
                        } else if (chance > 75) {
                            stunTime = 4500;
                        } else if (chance > 70) {
                            stunTime = 4000;
                        } else if (chance > 65) {
                            stunTime = 3500;
                        } else if (chance > 60) {
                            stunTime = 3000;
                        } else if (chance > 55) {
                            stunTime = 2500;
                        } else if (chance > 50) {
                            stunTime = 2000;
                        } else if (chance > 45) {
                            stunTime = 1500;
                        } else {
                            stunTime = 1000;
                        }

                        if (_calcType == PC_PC) {
                            stunTime = Math.max(500, stunTime - 1500);
                        }

                        _shockStunDuration = stunTime;
                        L1EffectSpawn.getInstance().spawnEffect(81162,
                                _shockStunDuration, cha.getX(), cha.getY(),
                                cha.getMapId());
                        if (cha instanceof L1PcInstance) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_STUN,
                                    true));
                        } else if (cha instanceof L1MonsterInstance
                                || cha instanceof L1SummonInstance
                                || cha instanceof L1PetInstance) {
                            L1NpcInstance npc = (L1NpcInstance) cha;
                            npc.setParalyzed(true);
                            npc.setParalysisTime(_shockStunDuration);
                        }
                        break;
                    // å¥ªå‘½ä¹‹é›·
                    case THUNDER_GRAB:
                        isSuccess = _magic.calcProbabilityMagic(_skillId);
                        if (isSuccess) {
                            if (!cha.hasSkillEffect(THUNDER_GRAB_START) && !cha.hasSkillEffect(STATUS_FREEZE) ) {
                                if (cha instanceof L1PcInstance) {
                                    L1PcInstance pc = (L1PcInstance) cha;
                                    pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_BIND, true));
                                    pc.sendPackets(new S_SkillSound(pc.getId(), 4184));
                                    pc.broadcastPacket(new S_SkillSound(pc.getId(), 4184));
                                } else if (cha instanceof L1NpcInstance) {
                                    L1NpcInstance npc = (L1NpcInstance) cha;
                                    npc.setParalyzed(true);
                                    npc.broadcastPacket(new S_SkillSound(npc.getId(), 4184));
                                }
                                cha.setSkillEffect(THUNDER_GRAB_START, 500);
                            }
                        }
                        break;
                    // BONE_BREAK
                    case BONE_BREAK:
                        RandomGenerator random1 = RandomGeneratorFactory.getSharedRandom();
                        int boneTime = (random1.nextInt(5)) * 500;
                        _boneBreakDuration = boneTime;
                        int intbonus = _user.getInt();
                        if (intbonus > 0) {
                            if(intbonus >= 35)
                            {
                                intbonus = 35;
                            }
                            _boneBreakDuration = _boneBreakDuration + (intbonus * 70);
                        }
                        if (_boneBreakDuration < 1500) {
                            _boneBreakDuration = 1500;
                        }
                        if (_boneBreakDuration > 4500) {
                            _boneBreakDuration = 4500;
                        }
                        int bonechance = Random.nextInt(100) + 1;
                        if (bonechance <= (40-(2*cha.getRegistStun()))) {
                            L1EffectSpawn.getInstance().spawnEffect(93001,
                                    _boneBreakDuration, cha.getX(), cha.getY(),
                                    cha.getMapId());
                            if (cha instanceof L1PcInstance) {
                                L1PcInstance pc = (L1PcInstance) cha;

                                pc.sendPackets(new S_Paralysis(
                                        S_Paralysis.TYPE_STUN, true));
                            } else if (cha instanceof L1MonsterInstance ||
                                    cha instanceof L1SummonInstance ||
                                    cha instanceof L1PetInstance) {
                                L1NpcInstance npc = (L1NpcInstance) cha;

                                npc.setParalyzed(true);
                                npc.setParalysisTime(_boneBreakDuration);
                            }
                        }
                        break;
                    case ARM_BREAKER:
                        if (cha instanceof L1PcInstance) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.sendPackets(new S_SkillSound(pc.getId(), 6551));
                            pc.broadcastPacket(new S_SkillSound(pc.getId(), 6551));
                        } else if (cha instanceof L1MonsterInstance
                                || cha instanceof L1SummonInstance
                                || cha instanceof L1PetInstance
                                || cha instanceof L1DwarfInstance
                                || cha instanceof L1GuardInstance
                                || cha instanceof L1MerchantInstance
                                || cha instanceof L1TeleporterInstance
                                || cha instanceof L1HousekeeperInstance) {
                            L1NpcInstance npc = (L1NpcInstance) cha;
                            npc
                                    .broadcastPacket(new S_SkillSound(npc.getId(),
                                            6551));
                        }
                        RandomGenerator random2 = RandomGeneratorFactory
                                .getSharedRandom();
                        int armchance = (random2.nextInt(100) + 1);
                        int time = 10;
                        if (armchance <= 50) {
                            if (cha instanceof L1PcInstance) {
                                if (cha instanceof L1PcInstance) {
                                    L1PcInstance pc = (L1PcInstance) cha;
                                    pc.setSkillEffect(ARM_BREAKER, time);
                                    pc.sendPackets(new S_SkillIconGFX(74,
                                            (time / 3)));
                                }
                            } else if (cha instanceof L1MonsterInstance
                                    || cha instanceof L1SummonInstance
                                    || cha instanceof L1PetInstance) {
                                L1NpcInstance npc = (L1NpcInstance) cha;
                                npc.setSkillEffect(ARM_BREAKER, time);
                            }
                        }
                        break;
                    // èµ·æ­»å›žç”Ÿè¡“
                    case TURN_UNDEAD:
                        if (undeadType == 1 || undeadType == 3){
                            dmg = cha.getCurrentHp();
                        }
                        break;
                    // é­”åŠ›å¥ªå�–
                    case MANA_DRAIN:
                        int manachance = Random.nextInt(10) + 5;
                        drainMana = manachance + (_user.getInt() / 2);
                        if (cha.getCurrentMp() < drainMana) {
                            drainMana = cha.getCurrentMp();
                        }
                        break;
                    // æŒ‡å®šå‚³é€�ã€�é›†é«”å‚³é€�è¡“
                    case TELEPORT:
                    case MASS_TELEPORT:
                        if (cha instanceof L1PcInstance) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            L1BookMark bookm = pc.getBookMark(_bookmarkId);
                            if (bookm != null) { // ãƒ–ãƒƒã‚¯ãƒžãƒ¼ã‚¯ã‚’å�–å¾—å‡ºæ�¥ã�Ÿã‚‰ãƒ†ãƒ¬ãƒ�ãƒ¼ãƒˆ
                                if (pc.getMap().isEscapable() || pc.isGm()) {
                                    int newX = bookm.getLocX();
                                    int newY = bookm.getLocY();
                                    short mapId = bookm.getMapId();

                                    if (_skillId == MASS_TELEPORT) { // ãƒžã‚¹ãƒ†ãƒ¬ãƒ�ãƒ¼ãƒˆ
                                        List<L1PcInstance> clanMember = L1World.getInstance().getVisiblePlayer(pc);
                                        for (L1PcInstance member : clanMember) {
                                            if ((pc.getLocation().getTileLineDistance(member.getLocation()) <= 3)
                                                    && (member.getClanid() == pc.getClanid()) && (pc.getClanid() != 0) && (member.getId() != pc.getId())) {
                                                L1Teleport.teleport(member, newX, newY, mapId, 5, true);
                                            }
                                        }
                                    }
                                    L1Teleport.teleport(pc, newX, newY, mapId, 5, true);
                                }
                                else {
                                    pc.sendPackets(new S_ServerMessage(79));
                                    pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, true));
                                }
                            }
                            else { // ãƒ–ãƒƒã‚¯ãƒžãƒ¼ã‚¯ã�Œå�–å¾—å‡ºæ�¥ã�ªã�‹ã�£ã�Ÿã€�ã�‚ã‚‹ã�„ã�¯ã€Œä»»æ„�ã�®å ´æ‰€ã€�ã‚’é�¸æŠžã�—ã�Ÿå ´å�ˆã�®å‡¦ç�†
                                if (pc.getMap().isTeleportable() || pc.isGm()) {
                                    L1Location newLocation = pc.getLocation().randomLocation(200, true);
                                    int newX = newLocation.getX();
                                    int newY = newLocation.getY();
                                    short mapId = (short) newLocation.getMapId();

                                    if (_skillId == MASS_TELEPORT) {
                                        List<L1PcInstance> clanMember = L1World.getInstance().getVisiblePlayer(pc);
                                        for (L1PcInstance member : clanMember) {
                                            if ((pc.getLocation().getTileLineDistance(member.getLocation()) <= 3)
                                                    && (member.getClanid() == pc.getClanid()) && (pc.getClanid() != 0) && (member.getId() != pc.getId())) {
                                                L1Teleport.teleport(member, newX, newY, mapId, 5, true);
                                            }
                                        }
                                    }
                                    L1Teleport.teleport(pc, newX, newY, mapId, 5, true);
                                }
                                else {
                                    pc.sendPackets(new S_ServerMessage(276)); // \f1åœ¨æ­¤ç„¡æ³•ä½¿ç”¨å‚³é€�ã€‚
                                    pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, true));
                                    // using tele spell in non-teleportable map will give effect of .reload
                                    L1Teleport.teleport(pc, pc.getX(), pc.getY(), pc.getMapId(), 5, false);
                                }
                            }
                        }
                        break;
                    // å‘¼å–šç›Ÿå�‹
                    case CALL_CLAN:
                        if (cha instanceof L1PcInstance) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            L1PcInstance clanPc = (L1PcInstance) L1World.getInstance().findObject(_targetID);
                            if (clanPc != null) {
                                clanPc.setTempID(pc.getId());
                                clanPc.sendPackets(new S_Message_YN(729, "")); // ç›Ÿä¸»æ­£åœ¨å‘¼å–šä½ ï¼Œä½ è¦�æŽ¥å�—ä»–çš„å‘¼å–šå—Žï¼Ÿ(Y/N)
                            }
                        }
                        break;
                    // æ�´è­·ç›Ÿå�‹
                    case RUN_CLAN:
                        if (cha instanceof L1PcInstance) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            L1PcInstance clanPc = (L1PcInstance) L1World.getInstance().findObject(_targetID);
                            if (clanPc != null) {
                                if (pc.getMap().isEscapable() || pc.isGm()) {
                                    boolean castle_area = L1CastleLocation.checkInAllWarArea(
                                            clanPc.getX(), clanPc.getY(), clanPc.getMapId());
                                    if (((clanPc.getMapId() == 0) || (clanPc.getMapId() == 4) || (clanPc.getMapId() == 304)) && (castle_area == false)) {
                                        L1Teleport.teleport(pc, clanPc.getX(), clanPc.getY(), clanPc.getMapId(), 5, true);
                                    }
                                    else {
                                        pc.sendPackets(new S_ServerMessage(79));
                                    }
                                }
                                else {
                                    // é€™é™„è¿‘çš„èƒ½é‡�å½±éŸ¿åˆ°çž¬é–“ç§»å‹•ã€‚åœ¨æ­¤åœ°ç„¡æ³•ä½¿ç”¨çž¬é–“ç§»å‹•ã€‚
                                    pc.sendPackets(new S_ServerMessage(647));
                                    pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, true));
                                }
                            }
                        }
                        break;
                    // å¼·åŠ›ç„¡æ‰€é��å½¢
                    case COUNTER_DETECTION:
                        if (cha instanceof L1PcInstance) {
//							if (cha.isInvisble())
                            dmg = _magic.calcMagicDamage(_skillId);
                        }
                        else if (cha instanceof L1NpcInstance) {
                            L1NpcInstance npc = (L1NpcInstance) cha;
                            int hiddenStatus = npc.getHiddenStatus();
                            if (hiddenStatus == L1NpcInstance.HIDDEN_STATUS_SINK) {
                                npc.appearOnGround(_player);
                            } else {
                                dmg = 0;
                            }
                        } else {
                            dmg = 0;
                        }
                        break;
                    // å‰µé€ é­”æ³•æ­¦å™¨
                    case CREATE_MAGICAL_WEAPON:
                        if (cha instanceof L1PcInstance) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            L1ItemInstance item = pc.getInventory().getItem(_itemobjid);
                            if ((item != null) && (item.getItem().getType2() == 1)) {
                                int item_type = item.getItem().getType2();
                                int safe_enchant = item.getItem().get_safeenchant();
                                int enchant_level = item.getEnchantLevel();
                                String item_name = item.getName();
                                if (safe_enchant < 0) { // å¼·åŒ–ä¸�å�¯
                                    pc.sendPackets( // \f1ä½•ã‚‚èµ·ã��ã�¾ã�›ã‚“ã�§ã�—ã�Ÿã€‚
                                            new S_ServerMessage(79));
                                }
                                else if (safe_enchant == 0) { // å®‰å…¨åœ�+0
                                    pc.sendPackets( // \f1ä½•ã‚‚èµ·ã��ã�¾ã�›ã‚“ã�§ã�—ã�Ÿã€‚
                                            new S_ServerMessage(79));
                                }
                                else if ((item_type == 1) && (enchant_level == 0)) {
                                    if (!item.isIdentified()) {// æœªé‘‘å®š
                                        pc.sendPackets( // \f1%0ã�Œ%2%1å…‰ã‚Šã�¾ã�™ã€‚
                                                new S_ServerMessage(161, item_name, "$245", "$247"));
                                    }
                                    else {
                                        item_name = "+0 " + item_name;
                                        pc.sendPackets( // \f1%0ã�Œ%2%1å…‰ã‚Šã�¾ã�™ã€‚
                                                new S_ServerMessage(161, "+0 " + item_name, "$245", "$247"));
                                    }
                                    item.setEnchantLevel(1);
                                    pc.getInventory().updateItem(item, L1PcInventory.COL_ENCHANTLVL);
                                }
                                else {
                                    pc.sendPackets( // \f1ä½•ã‚‚èµ·ã��ã�¾ã�›ã‚“ã�§ã�—ã�Ÿã€‚
                                            new S_ServerMessage(79));
                                }
                            }
                            else {
                                pc.sendPackets( // \f1ä½•ã‚‚èµ·ã��ã�¾ã�›ã‚“ã�§ã�—ã�Ÿã€‚
                                        new S_ServerMessage(79));
                            }
                        }
                        break;
                    // æ��ç…‰é­”çŸ³
                    case BRING_STONE:
                        if (cha instanceof L1PcInstance) {
                            L1PcInstance pc = (L1PcInstance) cha;

                            L1ItemInstance item = pc.getInventory().getItem(_itemobjid);
                            if (item != null) {
                                int dark = (int) (10 + (pc.getLevel() * 0.8) + (pc.getWis() - 6) * 1.2);
                                int brave = (int) (dark / 2.1);
                                int wise = (int) (brave / 2.0);
                                int kayser = (int) (wise / 1.9);
                                int run = Random.nextInt(100) + 1;
                                if (item.getItem().getItemId() == 40320) {
                                    pc.getInventory().removeItem(item, 1);
                                    if (dark >= run) {
                                        pc.getInventory().storeItem(40321, 1);
                                        pc.sendPackets(new S_ServerMessage(403, "$2475")); // ç�²å¾—%0%o ã€‚
                                    } else {
                                        pc.sendPackets(new S_ServerMessage(280)); // \f1æ–½å’’å¤±æ•—ã€‚
                                    }
                                } else if (item.getItem().getItemId() == 40321) {
                                    pc.getInventory().removeItem(item, 1);
                                    if (brave >= run) {
                                        pc.getInventory().storeItem(40322, 1);
                                        pc.sendPackets(new S_ServerMessage(403, "$2476")); // ç�²å¾—%0%o ã€‚
                                    } else {
                                        pc.sendPackets(new S_ServerMessage(280));// \f1æ–½å’’å¤±æ•—ã€‚
                                    }
                                } else if (item.getItem().getItemId() == 40322) {
                                    pc.getInventory().removeItem(item, 1);
                                    if (wise >= run) {
                                        pc.getInventory().storeItem(40323, 1);
                                        pc.sendPackets(new S_ServerMessage(403, "$2477")); // ç�²å¾—%0%o ã€‚
                                    } else {
                                        pc.sendPackets(new S_ServerMessage(280));// \f1æ–½å’’å¤±æ•—ã€‚
                                    }
                                } else if (item.getItem().getItemId() == 40323) {
                                    pc.getInventory().removeItem(item, 1);
                                    if (kayser >= run) {
                                        pc.getInventory().storeItem(40324, 1);
                                        pc.sendPackets(new S_ServerMessage(403, "$2478")); // ç�²å¾—%0%o ã€‚
                                    } else {
                                        pc.sendPackets(new S_ServerMessage(280));// \f1æ–½å’’å¤±æ•—ã€‚
                                    }
                                }
                            }
                        }
                        break;
                    // æ—¥å…‰è¡“
                    case LIGHT:
                        if (cha instanceof L1PcInstance) {
                        }
                        break;
                    // æš—å½±ä¹‹ç‰™
                    case SHADOW_FANG:
                        if (cha instanceof L1PcInstance) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            L1ItemInstance item = pc.getInventory().getItem(_itemobjid);
                            if ((item != null) && (item.getItem().getType2() == 1)) {
                                item.setSkillWeaponEnchant(pc, _skillId, _skill.getBuffDuration() * 1000);
                            }
                            else {
                                pc.sendPackets(new S_ServerMessage(79));
                            }
                        }
                        break;
                    // æ“¬ä¼¼é­”æ³•æ­¦å™¨
                    case ENCHANT_WEAPON:
                        if (cha instanceof L1PcInstance) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            L1ItemInstance item = pc.getInventory().getItem(_itemobjid);
                            if ((item != null) && (item.getItem().getType2() == 1)) {
                                pc.sendPackets(new S_ServerMessage(161, item.getLogName(), "$245", "$247"));
                                item.setSkillWeaponEnchant(pc, _skillId, _skill.getBuffDuration() * 1000);
                            }
                            else {
                                pc.sendPackets(new S_ServerMessage(79));
                            }
                        }
                        break;
                    // ç¥žè�–æ­¦å™¨ã€�ç¥�ç¦�é­”æ³•æ­¦å™¨
                    case HOLY_WEAPON:
                    case BLESS_WEAPON:
                        if (cha instanceof L1PcInstance) {
                            if (!(cha instanceof L1PcInstance)) {
                                return;
                            }
                            L1PcInstance pc = (L1PcInstance) cha;
                            if (pc.getWeapon() == null) {
                                pc.sendPackets(new S_ServerMessage(79));
                                return;
                            }
                            for (L1ItemInstance item : pc.getInventory().getItems()) {
                                if (pc.getWeapon().equals(item)) {
                                    pc.sendPackets(new S_ServerMessage(161, item.getLogName(), "$245", "$247"));
                                    item.setSkillWeaponEnchant(pc, _skillId, _skill.getBuffDuration() * 1000);
                                    return;
                                }
                            }
                        }
                        break;
                    // éŽ§ç”²è­·æŒ�
                    case BLESSED_ARMOR:
                        if (cha instanceof L1PcInstance) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            L1ItemInstance item = pc.getInventory().getItem(_itemobjid);
                            if ((item != null) && (item.getItem().getType2() == 2) && (item.getItem().getType() == 2)) {
                                pc.sendPackets(new S_ServerMessage(161, item.getLogName(), "$245", "$247"));
                                item.setSkillArmorEnchant(pc, _skillId, _skill.getBuffDuration() * 1000);
                            }
                            else {
                                pc.sendPackets(new S_ServerMessage(79));
                            }
                        }
                        break;
                    default:
                        L1BuffUtil.skillEffect(_user, cha, _target, _skillId, _getBuffIconDuration, dmg);
                        break;
                }

                // â– â– â– â–  å€‹åˆ¥å‡¦ç�†ã�“ã�“ã�¾ã�§ â– â– â– â– 

                // æ²»ç™’æ€§é­”æ³•æ”»æ“Šä¸�æ­»ä¿‚çš„æ€ªç‰©ã€‚
                if ((_skill.getType() == L1Skills.TYPE_HEAL) && (_calcType == PC_NPC) && (undeadType == 1)) {
                    dmg *= -1;
                }
                // æ²»ç™’æ€§é­”æ³•ç„¡æ³•å°�æ­¤ä¸�æ­»ä¿‚èµ·ä½œç”¨
                if ((_skill.getType() == L1Skills.TYPE_HEAL) && (_calcType == PC_NPC) && (undeadType == 3)) {
                    dmg = 0;
                }
                // ç„¡æ³•å°�åŸŽé–€ã€�å®ˆè­·å¡”è£œè¡€
                if (((cha instanceof L1TowerInstance) || (cha instanceof L1DoorInstance)) && (dmg < 0)) {
                    dmg = 0;
                }
                // å�¸å�–é­”åŠ›ã€‚
                if ((dmg > 0) || (drainMana != 0)) {
                    _magic.commit(dmg, drainMana);
                }
                // è£œè¡€åˆ¤æ–·
                if ((_skill.getType() == L1Skills.TYPE_HEAL) && (dmg < 0)) {
                    cha.setCurrentHp((dmg * -1) + cha.getCurrentHp());
                }
                // é�žæ²»ç™’æ€§é­”æ³•è£œè¡€åˆ¤æ–·(å¯’æˆ°ã€�å�¸å�»ç­‰)
                if (heal > 0) {
                    _user.setCurrentHp(heal + _user.getCurrentHp());
                }

                if (cha instanceof L1PcInstance) { // æ›´æ–°è‡ªèº«ç‹€æ…‹
                    L1PcInstance pc = (L1PcInstance) cha;
                    pc.turnOnOffLight();
                    pc.sendPackets(new S_OwnCharAttrDef(pc));
                    pc.sendPackets(new S_OwnCharStatus(pc));
                    sendHappenMessage(pc); // ã‚¿ãƒ¼ã‚²ãƒƒãƒˆã�«ãƒ¡ãƒƒã‚»ãƒ¼ã‚¸ã‚’é€�ä¿¡
                }

                addMagicList(cha, false); // ã‚¿ãƒ¼ã‚²ãƒƒãƒˆã�«é­”æ³•ã�®åŠ¹æžœæ™‚é–“ã‚’è¨­å®š

                if (cha instanceof L1PcInstance) { // ã‚¿ãƒ¼ã‚²ãƒƒãƒˆã�ŒPCã�ªã‚‰ã�°ã€�ãƒ©ã‚¤ãƒˆçŠ¶æ…‹ã‚’æ›´æ–°
                    L1PcInstance pc = (L1PcInstance) cha;
                    pc.turnOnOffLight();
                }
            }

            // è§£é™¤éš±èº«
            if ((_skillId == DETECTION) || (_skillId == COUNTER_DETECTION)) { // ç„¡æ‰€é��å½¢ã€�å¼·åŠ›ç„¡æ‰€é��å½¢
                detection(_player);
            }

        }
        catch (Exception e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        }
    }

    private void detection(L1PcInstance pc) {
        if (!pc.isGmInvis() && pc.isInvisble()) { // è‡ªå·±éš±èº«ä¸­
            pc.delInvis();
            pc.beginInvisTimer();
        }

        for (L1PcInstance tgt : L1World.getInstance().getVisiblePlayer(pc)) { // ç•«é�¢å…§å…¶ä»–éš±èº«è€…
            if (!tgt.isGmInvis() && tgt.isInvisble()) {
                tgt.delInvis();
            }
        }
        L1WorldTraps.getInstance().onDetection(pc);
    }

    // ã‚¿ãƒ¼ã‚²ãƒƒãƒˆã�«ã�¤ã�„ã�¦è¨ˆç®—ã�™ã‚‹å¿…è¦�ã�Œã�‚ã‚‹ã�‹è¿”ã�™
    private boolean isTargetCalc(L1Character cha) {
        // ä¸‰é‡�çŸ¢ã€�å± å®°è€…ã€�æš´æ“Šã€�éª·é«�æ¯€å£ž
        if ((_user instanceof L1PcInstance)
                && (_skillId == TRIPLE_ARROW || _skillId == FOE_SLAYER
                || _skillId == SMASH || _skillId == BONE_BREAK)) {
            return true;
        }
        // æ”»æ’ƒé­”æ³•ã�®Nonï¼�PvPåˆ¤å®š
        if (_skill.getTarget().equals("attack") && (_skillId != 18)) { // æ”»æ’ƒé­”æ³•
            if (isPcSummonPet(cha)) { // å¯¾è±¡ã�ŒPCã€�ã‚µãƒ¢ãƒ³ã€�ãƒšãƒƒãƒˆ
                if ((_player.getZoneType() == 1) || (cha.getZoneType() == 1 // æ”»æ’ƒã�™ã‚‹å�´ã�¾ã�Ÿã�¯æ”»æ’ƒã�•ã‚Œã‚‹å�´ã�Œã‚»ãƒ¼ãƒ•ãƒ†ã‚£ãƒ¼ã‚¾ãƒ¼ãƒ³
                ) || _player.checkNonPvP(_player, cha)) { // Non-PvPè¨­å®š
                    return false;
                }
            }
        }

        // ãƒ•ã‚©ã‚°ã‚ªãƒ–ã‚¹ãƒªãƒ¼ãƒ”ãƒ³ã‚°ã�¯è‡ªåˆ†è‡ªèº«ã�¯å¯¾è±¡å¤–
        if ((_skillId == FOG_OF_SLEEPING) && (_user.getId() == cha.getId())) {
            return false;
        }

        // ãƒžã‚¹ã‚¹ãƒ­ãƒ¼ã�¯è‡ªåˆ†è‡ªèº«ã�¨è‡ªåˆ†ã�®ãƒšãƒƒãƒˆã�¯å¯¾è±¡å¤–
        if (_skillId == MASS_SLOW) {
            if (_user.getId() == cha.getId()) {
                return false;
            }
            if (cha instanceof L1SummonInstance) {
                L1SummonInstance summon = (L1SummonInstance) cha;
                if (_user.getId() == summon.getMaster().getId()) {
                    return false;
                }
            }
            else if (cha instanceof L1PetInstance) {
                L1PetInstance pet = (L1PetInstance) cha;
                if (_user.getId() == pet.getMaster().getId()) {
                    return false;
                }
            }
        }

        // ãƒžã‚¹ãƒ†ãƒ¬ãƒ�ãƒ¼ãƒˆã�¯è‡ªåˆ†è‡ªèº«ã�®ã�¿å¯¾è±¡ï¼ˆå�Œæ™‚ã�«ã‚¯ãƒ©ãƒ³å“¡ã‚‚ãƒ†ãƒ¬ãƒ�ãƒ¼ãƒˆã�•ã�›ã‚‹ï¼‰
        if (_skillId == MASS_TELEPORT) {
            if (_user.getId() != cha.getId()) {
                return false;
            }
        }

        return true;
    }

    // å¯¾è±¡ã�ŒPCã€�ã‚µãƒ¢ãƒ³ã€�ãƒšãƒƒãƒˆã�‹ã‚’è¿”ã�™
    private boolean isPcSummonPet(L1Character cha) {
        if (_calcType == PC_PC) { // å¯¾è±¡ã�ŒPC
            return true;
        }

        if (_calcType == PC_NPC) {
            if (cha instanceof L1SummonInstance) { // å¯¾è±¡ã�Œã‚µãƒ¢ãƒ³
                L1SummonInstance summon = (L1SummonInstance) cha;
                if (summon.isExsistMaster()) { // ãƒžã‚¹ã‚¿ãƒ¼ã�Œå±…ã‚‹
                    return true;
                }
            }
            if (cha instanceof L1PetInstance) { // å¯¾è±¡ã�Œãƒšãƒƒãƒˆ
                return true;
            }
        }
        return false;
    }

    // ã‚¿ãƒ¼ã‚²ãƒƒãƒˆã�«å¯¾ã�—ã�¦å¿…ã�šå¤±æ•—ã�«ã�ªã‚‹ã�‹è¿”ã�™
    private boolean isTargetFailure(L1Character cha) {
        boolean isTU = false;
        boolean isErase = false;
        boolean isManaDrain = false;
        int undeadType = 0;

        if ((cha instanceof L1TowerInstance) || (cha instanceof L1DoorInstance)) { // ã‚¬ãƒ¼ãƒ‡ã‚£ã‚¢ãƒ³ã‚¿ãƒ¯ãƒ¼ã€�ãƒ‰ã‚¢ã�«ã�¯ç¢ºçŽ‡ç³»ã‚¹ã‚­ãƒ«ç„¡åŠ¹
            return true;
        }

        if (cha instanceof L1PcInstance) { // å¯¾PCã�®å ´å�ˆ
            if ((_calcType == PC_PC) && _player.checkNonPvP(_player, cha)) { // Non-PvPè¨­å®š
                L1PcInstance pc = (L1PcInstance) cha;
                if ((_player.getId() == pc.getId()) || ((pc.getClanid() != 0) && (_player.getClanid() == pc.getClanid()))) {
                    return false;
                }
                return true;
            }
            return false;
        }

        if (cha instanceof L1MonsterInstance) { // ã‚¿ãƒ¼ãƒ³ã‚¢ãƒ³ãƒ‡ãƒƒãƒˆå�¯èƒ½ã�‹åˆ¤å®š
            isTU = ((L1MonsterInstance) cha).getNpcTemplate().get_IsTU();
        }

        if (cha instanceof L1MonsterInstance) { // ã‚¤ãƒ¬ãƒ¼ã‚¹ãƒžã‚¸ãƒƒã‚¯å�¯èƒ½ã�‹åˆ¤å®š
            isErase = ((L1MonsterInstance) cha).getNpcTemplate().get_IsErase();
        }

        if (cha instanceof L1MonsterInstance) { // ã‚¢ãƒ³ãƒ‡ãƒƒãƒˆã�®åˆ¤å®š
            undeadType = ((L1MonsterInstance) cha).getNpcTemplate().get_undead();
        }

        // ãƒžãƒŠãƒ‰ãƒ¬ã‚¤ãƒ³ã�Œå�¯èƒ½ã�‹ï¼Ÿ
        if (cha instanceof L1MonsterInstance) {
            isManaDrain = true;
        }
		/*
		 * æˆ�åŠŸé™¤å¤–æ�¡ä»¶ï¼‘ï¼šT-Uã�Œæˆ�åŠŸã�—ã�Ÿã�Œã€�å¯¾è±¡ã�Œã‚¢ãƒ³ãƒ‡ãƒƒãƒˆã�§ã�¯ã�ªã�„ã€‚ æˆ�åŠŸé™¤å¤–æ�¡ä»¶ï¼’ï¼šT-Uã�Œæˆ�åŠŸã�—ã�Ÿã�Œã€�å¯¾è±¡ã�«ã�¯ã‚¿ãƒ¼ãƒ³ã‚¢ãƒ³ãƒ‡ãƒƒãƒˆç„¡åŠ¹ã€‚
		 * æˆ�åŠŸé™¤å¤–æ�¡ä»¶ï¼“ï¼šã‚¹ãƒ­ãƒ¼ã€�ãƒžã‚¹ã‚¹ãƒ­ãƒ¼ã€�ãƒžãƒŠãƒ‰ãƒ¬ã‚¤ãƒ³ã€�ã‚¨ãƒ³ã‚¿ãƒ³ã‚°ãƒ«ã€�ã‚¤ãƒ¬ãƒ¼ã‚¹ãƒžã‚¸ãƒƒã‚¯ã€�ã‚¦ã‚£ãƒ³ãƒ‰ã‚·ãƒ£ãƒƒã‚¯ãƒ«ç„¡åŠ¹
		 * æˆ�åŠŸé™¤å¤–æ�¡ä»¶ï¼”ï¼šãƒžãƒŠãƒ‰ãƒ¬ã‚¤ãƒ³ã�Œæˆ�åŠŸã�—ã�Ÿã�Œã€�ãƒ¢ãƒ³ã‚¹ã‚¿ãƒ¼ä»¥å¤–ã�®å ´å�ˆ
		 */
        if (((_skillId == TURN_UNDEAD) && ((undeadType == 0) || (undeadType == 2)))
                || ((_skillId == TURN_UNDEAD) && (isTU == false))
                || (((_skillId == ERASE_MAGIC) || (_skillId == SLOW) || (_skillId == MANA_DRAIN) || (_skillId == MASS_SLOW) || (_skillId == ENTANGLE) || (_skillId == WIND_SHACKLE)) && (isErase == false))
                || ((_skillId == MANA_DRAIN) && (isManaDrain == false))) {
            return true;
        }
        return false;
    }

    // é­”æ³•å±�éšœç™¼å‹•åˆ¤æ–·
    private boolean isUseCounterMagic(L1Character cha) {
        if (_isCounterMagic && cha.hasSkillEffect(COUNTER_MAGIC)) {
            cha.removeSkillEffect(COUNTER_MAGIC);
            int castgfx = SkillsTable.getInstance().getTemplate(COUNTER_MAGIC).getCastGfx();
            cha.broadcastPacket(new S_SkillSound(cha.getId(), castgfx));
            if (cha instanceof L1PcInstance) {
                L1PcInstance pc = (L1PcInstance) cha;
                pc.sendPackets(new S_SkillSound(pc.getId(), castgfx));
            }
            return true;
        }
        return false;
    }

    public static void turnStone(final L1PcInstance player,
                                 final L1ItemInstance item, double penalty, int count,
                                 boolean report) {
        if (item == null)
            return;

        int dark = (int) (penalty * (10 + (player.getLevel() * 0.8) +
                (player.getWis() - 6) * 1.2));
        int brave = (int) (dark / 2.1);
        int wise = (int) (brave / 2.0);
        int kaiser = (int) (wise / 1.9);

        switch (item.getItem().getItemId()) {
            case BringStone:
                turnStone(player, item, dark, DarkStone, "$2475", count, report);
                break;
            case DarkStone:
                turnStone(player, item, brave, BraveStone, "$2475", count, report);
                break;
            case BraveStone:
                turnStone(player, item, wise, WiseStone, "$2475", count, report);
                break;
            case WiseStone:
                turnStone(player, item, kaiser, KaiserStone, "$2475", count, report);
                break;
        }
    }

    private static void turnStone(final L1PcInstance player,
                                  final L1ItemInstance item, int chance, int nextStone, String name,
                                  int count, boolean report) {
        // This should never actually happen...
        if (count > item.getCount()) {
            _log.log(Level.WARNING, "turnStone count did not match.");
            return;
        }

        RandomGenerator random = RandomGeneratorFactory.getSharedRandom();
        L1PcInventory inventory = player.getInventory();

        for (int i = 0; i < count; i++) {
            inventory.removeItem(item, 1);
            if (chance > random.nextInt(100) + 1) {
                inventory.storeItem(nextStone, 1);
                if (report)
                    player.sendPackets(new S_ServerMessage(403, name));
            } else if (report)
                player.sendPackets(SkillFailed);
        }
    }
}