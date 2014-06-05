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
import l1j.server.server.model.skill.L1Stun;
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

    private boolean _checkedUseSkill = false; // ÃƒÂ¤Ã‚ÂºÃ¢â‚¬Â¹ÃƒÂ¥Ã¢â‚¬Â°Ã¯Â¿Â½ÃƒÂ£Ã†â€™Ã¯Â¿Â½ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â§ÃƒÂ£Ã†â€™Ã†â€™ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¯ÃƒÂ¦Ã‚Â¸Ã‹â€ ÃƒÂ£Ã¯Â¿Â½Ã‚Â¿ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬Â¹

    private int _leverage = 10; // 1/10ÃƒÂ¥Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ£Ã¯Â¿Â½Ã‚ÂªÃƒÂ£Ã¯Â¿Â½Ã‚Â®ÃƒÂ£Ã¯Â¿Â½Ã‚Â§10ÃƒÂ£Ã¯Â¿Â½Ã‚Â§1ÃƒÂ¥Ã¢â€šÂ¬Ã¯Â¿Â½

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

    // Adding Foe Slayer to CM exception list - [Hank]
    private static final int[] EXCEPT_COUNTER_MAGIC =
            { 1, 2, 3, 5, 8, 9, 12, 13, 14, 19, 21, 26, 31, 32, 35, 37, 42, 43, 44, 48, 49, 52, 54, 55, 57, 60, 61, 63, 67, 68, 69, 72, 73, 75, 78, 79,
                    SHOCK_STUN, REDUCTION_ARMOR, BOUNCE_ATTACK, SOLID_CARRIAGE, COUNTER_BARRIER, 97, 98, 99, 100, 101, 102, 104, 105, 106, 107, 109, 110,
                    111, 113, 114, 115, 116, 117, 118, 129, 130, 131, 132, 134, 137, 138, 146, 147, 148, 149, 150, 151, 155, 156, 158, 159, 161, 163, 164,
                    165, 166, 168, 169, 170, 171, SOUL_OF_FLAME, ADDITIONAL_FIRE, DRAGON_SKIN, AWAKEN_ANTHARAS, AWAKEN_FAFURION, AWAKEN_VALAKAS,
                    MIRROR_IMAGE, ILLUSION_OGRE, ILLUSION_LICH, PATIENCE, 10026, 10027, ILLUSION_DIA_GOLEM, INSIGHT, ILLUSION_AVATAR, 10028, 10029, FOE_SLAYER };

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

        private boolean _isCalc = true; // ÃƒÂ£Ã†â€™Ã¢â€šÂ¬ÃƒÂ£Ã†â€™Ã‚Â¡ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¸ÃƒÂ£Ã¢â‚¬Å¡Ã¢â‚¬Å¾ÃƒÂ§Ã‚Â¢Ã‚ÂºÃƒÂ§Ã…Â½Ã¢â‚¬Â¡ÃƒÂ©Ã‚Â­Ã¢â‚¬ï¿½ÃƒÂ¦Ã‚Â³Ã¢â‚¬Â¢ÃƒÂ£Ã¯Â¿Â½Ã‚Â®ÃƒÂ¨Ã‚Â¨Ã‹â€ ÃƒÂ§Ã‚Â®Ã¢â‚¬â€�ÃƒÂ£Ã¢â‚¬Å¡Ã¢â‚¬â„¢ÃƒÂ£Ã¯Â¿Â½Ã¢â€žÂ¢ÃƒÂ£Ã¢â‚¬Å¡Ã¢â‚¬Â¹ÃƒÂ¥Ã‚Â¿Ã¢â‚¬Â¦ÃƒÂ¨Ã‚Â¦Ã¯Â¿Â½ÃƒÂ£Ã¯Â¿Â½Ã…â€™ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬Å¡ÃƒÂ£Ã¢â‚¬Å¡Ã¢â‚¬Â¹ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬Â¹ÃƒÂ¯Ã‚Â¼Ã…Â¸

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
     * ÃƒÂ¦Ã¢â‚¬ï¿½Ã‚Â»ÃƒÂ¦Ã¢â‚¬Å“Ã… ÃƒÂ¨Ã‚Â·Ã¯Â¿Â½ÃƒÂ©Ã¢â‚¬ÂºÃ‚Â¢ÃƒÂ¨Ã‚Â®Ã… ÃƒÂ¦Ã¢â‚¬ÂºÃ‚Â´ÃƒÂ£Ã¢â€šÂ¬Ã¢â‚¬Å¡
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
     * ÃƒÂ¦Ã¢â‚¬ï¿½Ã‚Â»ÃƒÂ¦Ã¢â‚¬Å“Ã… ÃƒÂ§Ã‚Â¯Ã¢â‚¬Å¾ÃƒÂ¥Ã…â€œÃ¯Â¿Â½ÃƒÂ¨Ã‚Â®Ã… ÃƒÂ¦Ã¢â‚¬ÂºÃ‚Â´ÃƒÂ£Ã¢â€šÂ¬Ã¢â‚¬Å¡
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
     * 1/10ÃƒÂ¥Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ£Ã¯Â¿Â½Ã‚Â§ÃƒÂ¨Ã‚Â¡Ã‚Â¨ÃƒÂ§Ã¯Â¿Â½Ã‚Â¾ÃƒÂ£Ã¯Â¿Â½Ã¢â€žÂ¢ÃƒÂ£Ã¢â‚¬Å¡Ã¢â‚¬Â¹ÃƒÂ£Ã¢â€šÂ¬Ã¢â‚¬Å¡
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
        // ÃƒÂ¥Ã‹â€ Ã¯Â¿Â½ÃƒÂ¦Ã…â€œÃ…Â¸ÃƒÂ¨Ã‚Â¨Ã‚Â­ÃƒÂ¥Ã‚Â®Ã…Â¡ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬Å“ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬Å“ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬Â¹ÃƒÂ£Ã¢â‚¬Å¡Ã¢â‚¬Â°
        setCheckedUseSkill(true);
        _targetList = Lists.newList(); // ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¿ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â²ÃƒÂ£Ã†â€™Ã†â€™ÃƒÂ£Ã†â€™Ã‹â€ ÃƒÂ£Ã†â€™Ã‚ÂªÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¹ÃƒÂ£Ã†â€™Ã‹â€ ÃƒÂ£Ã¯Â¿Â½Ã‚Â®ÃƒÂ¥Ã‹â€ Ã¯Â¿Â½ÃƒÂ¦Ã…â€œÃ…Â¸ÃƒÂ¥Ã…â€™Ã¢â‚¬â€œ

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

        if (type == TYPE_NORMAL) { // ÃƒÂ©Ã¢â€šÂ¬Ã…Â¡ÃƒÂ¥Ã‚Â¸Ã‚Â¸ÃƒÂ£Ã¯Â¿Â½Ã‚Â®ÃƒÂ©Ã‚Â­Ã¢â‚¬ï¿½ÃƒÂ¦Ã‚Â³Ã¢â‚¬Â¢ÃƒÂ¤Ã‚Â½Ã‚Â¿ÃƒÂ§Ã¢â‚¬ï¿½Ã‚Â¨ÃƒÂ¦Ã¢â€žÂ¢Ã¢â‚¬Å¡
            checkedResult = isNormalSkillUsable();
        }
        else if (type == TYPE_SPELLSC) {
            checkedResult = isSpellScrollUsable();
        }
        else if (type == TYPE_NPCBUFF) {
            checkedResult = true;
        }
        if (!checkedResult) {
            return false;
        }


        if ((_skillId == FIRE_WALL) || (_skillId == LIFE_STREAM) || (_skillId == TRUE_TARGET)) {
            return true;
        }

        L1Object l1object = L1World.getInstance().findObject(_targetID);
        if (l1object instanceof L1ItemInstance) {
            _log.fine("skill target item name: " + ((L1ItemInstance) l1object).getViewName());

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

        // ÃƒÂ£Ã†â€™Ã¢â‚¬ ÃƒÂ£Ã†â€™Ã‚Â¬ÃƒÂ£Ã†â€™Ã¯Â¿Â½ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã†â€™Ã‹â€ ÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ£Ã†â€™Ã…Â¾ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¹ÃƒÂ£Ã†â€™Ã¢â‚¬ ÃƒÂ£Ã†â€™Ã‚Â¬ÃƒÂ£Ã†â€™Ã¯Â¿Â½ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã†â€™Ã‹â€ ÃƒÂ£Ã¯Â¿Â½Ã‚Â¯ÃƒÂ¥Ã‚Â¯Ã‚Â¾ÃƒÂ¨Ã‚Â±Ã‚Â¡ÃƒÂ£Ã¯Â¿Â½Ã…â€™ÃƒÂ£Ã†â€™Ã¢â‚¬â€œÃƒÂ£Ã†â€™Ã†â€™ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¯ÃƒÂ£Ã†â€™Ã…Â¾ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¯ID
        if ((_skillId == TELEPORT) || (_skillId == MASS_TELEPORT)) {
            _bookmarkId = target_id;
        }
        // ÃƒÂ¥Ã‚Â¯Ã‚Â¾ÃƒÂ¨Ã‚Â±Ã‚Â¡ÃƒÂ£Ã¯Â¿Â½Ã…â€™ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¢ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¤ÃƒÂ£Ã†â€™Ã¢â‚¬ ÃƒÂ£Ã†â€™Ã‚ ÃƒÂ£Ã¯Â¿Â½Ã‚Â®ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¹ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â­ÃƒÂ£Ã†â€™Ã‚Â«
        if ((_skillId == CREATE_MAGICAL_WEAPON) || (_skillId == BRING_STONE) || (_skillId == BLESSED_ARMOR) || (_skillId == ENCHANT_WEAPON)
                || (_skillId == SHADOW_FANG)) {
            _itemobjid = target_id;
        }
        _target = (L1Character) l1object;

        if (!(_target instanceof L1MonsterInstance) && _skill.getTarget().equals("attack") && (_user.getId() != target_id)) {
            _isPK = true; // ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¿ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â²ÃƒÂ£Ã†â€™Ã†â€™ÃƒÂ£Ã†â€™Ã‹â€ ÃƒÂ£Ã¯Â¿Â½Ã…â€™ÃƒÂ£Ã†â€™Ã‚Â¢ÃƒÂ£Ã†â€™Ã‚Â³ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¹ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¿ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ¤Ã‚Â»Ã‚Â¥ÃƒÂ¥Ã‚Â¤Ã¢â‚¬â€œÃƒÂ£Ã¯Â¿Â½Ã‚Â§ÃƒÂ¦Ã¢â‚¬ï¿½Ã‚Â»ÃƒÂ¦Ã¢â‚¬â„¢Ã†â€™ÃƒÂ§Ã‚Â³Ã‚Â»ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¹ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â­ÃƒÂ£Ã†â€™Ã‚Â«ÃƒÂ£Ã¯Â¿Â½Ã‚Â§ÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ¨Ã¢â‚¬Â¡Ã‚ÂªÃƒÂ¥Ã‹â€ Ã¢â‚¬ ÃƒÂ¤Ã‚Â»Ã‚Â¥ÃƒÂ¥Ã‚Â¤Ã¢â‚¬â€œÃƒÂ£Ã¯Â¿Â½Ã‚Â®ÃƒÂ¥Ã‚ Ã‚Â´ÃƒÂ¥Ã¯Â¿Â½Ã‹â€ PKÃƒÂ£Ã†â€™Ã‚Â¢ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã†â€™Ã¢â‚¬Â°ÃƒÂ£Ã¯Â¿Â½Ã‚Â¨ÃƒÂ£Ã¯Â¿Â½Ã¢â€žÂ¢ÃƒÂ£Ã¢â‚¬Å¡Ã¢â‚¬Â¹ÃƒÂ£Ã¢â€šÂ¬Ã¢â‚¬Å¡
        }

        // ÃƒÂ¥Ã‹â€ Ã¯Â¿Â½ÃƒÂ¦Ã…â€œÃ…Â¸ÃƒÂ¨Ã‚Â¨Ã‚Â­ÃƒÂ¥Ã‚Â®Ã…Â¡ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬Å“ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬Å“ÃƒÂ£Ã¯Â¿Â½Ã‚Â¾ÃƒÂ£Ã¯Â¿Â½Ã‚Â§

        // ÃƒÂ¤Ã‚ÂºÃ¢â‚¬Â¹ÃƒÂ¥Ã¢â‚¬Â°Ã¯Â¿Â½ÃƒÂ£Ã†â€™Ã¯Â¿Â½ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â§ÃƒÂ£Ã†â€™Ã†â€™ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¯
        if (!(l1object instanceof L1Character)) { // ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¿ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â²ÃƒÂ£Ã†â€™Ã†â€™ÃƒÂ£Ã†â€™Ã‹â€ ÃƒÂ£Ã¯Â¿Â½Ã…â€™ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â­ÃƒÂ£Ã†â€™Ã‚Â£ÃƒÂ£Ã†â€™Ã‚Â©ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¯ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¿ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ¤Ã‚Â»Ã‚Â¥ÃƒÂ¥Ã‚Â¤Ã¢â‚¬â€œÃƒÂ£Ã¯Â¿Â½Ã‚Â®ÃƒÂ¥Ã‚ Ã‚Â´ÃƒÂ¥Ã¯Â¿Â½Ã‹â€ ÃƒÂ¤Ã‚Â½Ã¢â‚¬Â¢ÃƒÂ£Ã¢â‚¬Å¡Ã¢â‚¬Å¡ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬â€�ÃƒÂ£Ã¯Â¿Â½Ã‚ÂªÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬Å¾ÃƒÂ£Ã¢â€šÂ¬Ã¢â‚¬Å¡
            checkedResult = false;
        }
        makeTargetList(); // ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¿ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â²ÃƒÂ£Ã†â€™Ã†â€™ÃƒÂ£Ã†â€™Ã‹â€ ÃƒÂ£Ã¯Â¿Â½Ã‚Â®ÃƒÂ¤Ã‚Â¸Ã¢â€šÂ¬ÃƒÂ¨Ã‚Â¦Ã‚Â§ÃƒÂ£Ã¢â‚¬Å¡Ã¢â‚¬â„¢ÃƒÂ¤Ã‚Â½Ã…â€œÃƒÂ¦Ã‹â€ Ã¯Â¿Â½
        if (_targetList.isEmpty() && (_user instanceof L1NpcInstance)) {
            checkedResult = false;
        }
        // ÃƒÂ¤Ã‚ÂºÃ¢â‚¬Â¹ÃƒÂ¥Ã¢â‚¬Â°Ã¯Â¿Â½ÃƒÂ£Ã†â€™Ã¯Â¿Â½ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â§ÃƒÂ£Ã†â€™Ã†â€™ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¯ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬Å“ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬Å“ÃƒÂ£Ã¯Â¿Â½Ã‚Â¾ÃƒÂ£Ã¯Â¿Â½Ã‚Â§
        return checkedResult;
    }

    /**
     * ÃƒÂ©Ã¢â€šÂ¬Ã…Â¡ÃƒÂ¥Ã‚Â¸Ã‚Â¸ÃƒÂ£Ã¯Â¿Â½Ã‚Â®ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¹ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â­ÃƒÂ£Ã†â€™Ã‚Â«ÃƒÂ¤Ã‚Â½Ã‚Â¿ÃƒÂ§Ã¢â‚¬ï¿½Ã‚Â¨ÃƒÂ¦Ã¢â€žÂ¢Ã¢â‚¬Å¡ÃƒÂ£Ã¯Â¿Â½Ã‚Â«ÃƒÂ¤Ã‚Â½Ã‚Â¿ÃƒÂ§Ã¢â‚¬ï¿½Ã‚Â¨ÃƒÂ¨Ã¢â€šÂ¬Ã¢â‚¬Â¦ÃƒÂ£Ã¯Â¿Â½Ã‚Â®ÃƒÂ§Ã… Ã‚Â¶ÃƒÂ¦Ã¢â‚¬Â¦Ã¢â‚¬Â¹ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬Â¹ÃƒÂ£Ã¢â‚¬Å¡Ã¢â‚¬Â°ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¹ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â­ÃƒÂ£Ã†â€™Ã‚Â«ÃƒÂ£Ã¯Â¿Â½Ã…â€™ÃƒÂ¤Ã‚Â½Ã‚Â¿ÃƒÂ§Ã¢â‚¬ï¿½Ã‚Â¨ÃƒÂ¥Ã¯Â¿Â½Ã‚Â¯ÃƒÂ¨Ã†â€™Ã‚Â½ÃƒÂ£Ã¯Â¿Â½Ã‚Â§ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬Å¡ÃƒÂ£Ã¢â‚¬Å¡Ã¢â‚¬Â¹ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬Â¹ÃƒÂ¥Ã‹â€ Ã‚Â¤ÃƒÂ¦Ã¢â‚¬â€œÃ‚Â­ÃƒÂ£Ã¯Â¿Â½Ã¢â€žÂ¢ÃƒÂ£Ã¢â‚¬Å¡Ã¢â‚¬Â¹
     *
     * @return false ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¹ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â­ÃƒÂ£Ã†â€™Ã‚Â«ÃƒÂ£Ã¯Â¿Â½Ã…â€™ÃƒÂ¤Ã‚Â½Ã‚Â¿ÃƒÂ§Ã¢â‚¬ï¿½Ã‚Â¨ÃƒÂ¤Ã‚Â¸Ã¯Â¿Â½ÃƒÂ¥Ã¯Â¿Â½Ã‚Â¯ÃƒÂ¨Ã†â€™Ã‚Â½ÃƒÂ£Ã¯Â¿Â½Ã‚ÂªÃƒÂ§Ã… Ã‚Â¶ÃƒÂ¦Ã¢â‚¬Â¦Ã¢â‚¬Â¹ÃƒÂ£Ã¯Â¿Â½Ã‚Â§ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬Å¡ÃƒÂ£Ã¢â‚¬Å¡Ã¢â‚¬Â¹ÃƒÂ¥Ã‚ Ã‚Â´ÃƒÂ¥Ã¯Â¿Â½Ã‹â€
     */
    private boolean isNormalSkillUsable() {
        // ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¹ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â­ÃƒÂ£Ã†â€™Ã‚Â«ÃƒÂ¤Ã‚Â½Ã‚Â¿ÃƒÂ§Ã¢â‚¬ï¿½Ã‚Â¨ÃƒÂ¨Ã¢â€šÂ¬Ã¢â‚¬Â¦ÃƒÂ£Ã¯Â¿Â½Ã…â€™PCÃƒÂ£Ã¯Â¿Â½Ã‚Â®ÃƒÂ¥Ã‚ Ã‚Â´ÃƒÂ¥Ã¯Â¿Â½Ã‹â€ ÃƒÂ£Ã¯Â¿Â½Ã‚Â®ÃƒÂ£Ã†â€™Ã¯Â¿Â½ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â§ÃƒÂ£Ã†â€™Ã†â€™ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¯
        if (_user instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) _user;

            if (pc.isTeleport()) { // ÃƒÂ¥Ã¢â‚¬Å¡Ã‚Â³ÃƒÂ©Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ¤Ã‚Â¸Ã‚Â­
                return false;
            }
            if (pc.isParalyzed()) { // ÃƒÂ©Ã‚ÂºÃ‚Â»ÃƒÂ§Ã¢â‚¬â€�Ã‚ÂºÃƒÂ£Ã†â€™Ã‚Â»ÃƒÂ¥Ã¢â‚¬Â¡Ã¯Â¿Â½ÃƒÂ§Ã‚ÂµÃ¯Â¿Â½ÃƒÂ§Ã… Ã‚Â¶ÃƒÂ¦Ã¢â‚¬Â¦Ã¢â‚¬Â¹ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬Â¹
                return false;
            }
            if ((pc.isInvisble() || pc.isInvisDelay()) && !isInvisUsableSkill()) { // ÃƒÂ©Ã…Â¡Ã‚Â±ÃƒÂ¨Ã‚ÂºÃ‚Â«ÃƒÂ¤Ã‚Â¸Ã‚Â­ÃƒÂ§Ã¢â‚¬Å¾Ã‚Â¡ÃƒÂ¦Ã‚Â³Ã¢â‚¬Â¢ÃƒÂ¤Ã‚Â½Ã‚Â¿ÃƒÂ§Ã¢â‚¬ï¿½Ã‚Â¨ÃƒÂ¦Ã… Ã¢â€šÂ¬ÃƒÂ¨Ã†â€™Ã‚Â½
                return false;
            }
            if (pc.getInventory().getWeight242() >= 197) { // \f1ÃƒÂ¤Ã‚Â½Ã‚ ÃƒÂ¦Ã¢â‚¬ï¿½Ã…â€œÃƒÂ¥Ã‚Â¸Ã‚Â¶ÃƒÂ¥Ã‚Â¤Ã‚ÂªÃƒÂ¥Ã‚Â¤Ã…Â¡ÃƒÂ§Ã¢â‚¬Â°Ã‚Â©ÃƒÂ¥Ã¢â‚¬Å“Ã¯Â¿Â½ÃƒÂ¯Ã‚Â¼Ã…â€™ÃƒÂ¥Ã¢â‚¬ÂºÃ‚ ÃƒÂ¦Ã‚Â­Ã‚Â¤ÃƒÂ§Ã¢â‚¬Å¾Ã‚Â¡ÃƒÂ¦Ã‚Â³Ã¢â‚¬Â¢ÃƒÂ¤Ã‚Â½Ã‚Â¿ÃƒÂ§Ã¢â‚¬ï¿½Ã‚Â¨ÃƒÂ¦Ã‚Â³Ã¢â‚¬Â¢ÃƒÂ¨Ã‚Â¡Ã¢â‚¬Å“ÃƒÂ£Ã¢â€šÂ¬Ã¢â‚¬Å¡
                pc.sendPackets(new S_ServerMessage(316));
                return false;
            }
            int polyId = pc.getTempCharGfx();
            L1PolyMorph poly = PolyTable.getInstance().getTemplate(polyId);
            // ÃƒÂ©Ã‚Â­Ã¢â‚¬ï¿½ÃƒÂ¦Ã‚Â³Ã¢â‚¬Â¢ÃƒÂ£Ã¯Â¿Â½Ã…â€™ÃƒÂ¤Ã‚Â½Ã‚Â¿ÃƒÂ£Ã¯Â¿Â½Ã‹â€ ÃƒÂ£Ã¯Â¿Â½Ã‚ÂªÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬Å¾ÃƒÂ¥Ã‚Â¤Ã¢â‚¬Â°ÃƒÂ¨Ã‚ÂºÃ‚Â«
            if ((poly != null) && !poly.canUseSkill()) {
                pc.sendPackets(new S_ServerMessage(285)); // \f1ÃƒÂ¥Ã…â€œÃ‚Â¨ÃƒÂ¦Ã‚Â­Ã‚Â¤ÃƒÂ§Ã¢â‚¬Â¹Ã¢â€šÂ¬ÃƒÂ¦Ã¢â‚¬Â¦Ã¢â‚¬Â¹ÃƒÂ¤Ã‚Â¸Ã¢â‚¬Â¹ÃƒÂ§Ã¢â‚¬Å¾Ã‚Â¡ÃƒÂ¦Ã‚Â³Ã¢â‚¬Â¢ÃƒÂ¤Ã‚Â½Ã‚Â¿ÃƒÂ§Ã¢â‚¬ï¿½Ã‚Â¨ÃƒÂ©Ã‚Â­Ã¢â‚¬ï¿½ÃƒÂ¦Ã‚Â³Ã¢â‚¬Â¢ÃƒÂ£Ã¢â€šÂ¬Ã¢â‚¬Å¡
                return false;
            }

            if (!isAttrAgrees()) { // ÃƒÂ§Ã‚Â²Ã‚Â¾ÃƒÂ©Ã…â€œÃ… ÃƒÂ©Ã‚Â­Ã¢â‚¬ï¿½ÃƒÂ¦Ã‚Â³Ã¢â‚¬Â¢ÃƒÂ£Ã¯Â¿Â½Ã‚Â§ÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ¥Ã‚Â±Ã…Â¾ÃƒÂ¦Ã¢â€šÂ¬Ã‚Â§ÃƒÂ£Ã¯Â¿Â½Ã…â€™ÃƒÂ¤Ã‚Â¸Ã¢â€šÂ¬ÃƒÂ¨Ã¢â‚¬Â¡Ã‚Â´ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬â€�ÃƒÂ£Ã¯Â¿Â½Ã‚ÂªÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬ËœÃƒÂ£Ã¢â‚¬Å¡Ã…â€™ÃƒÂ£Ã¯Â¿Â½Ã‚Â°ÃƒÂ¤Ã‚Â½Ã¢â‚¬Â¢ÃƒÂ£Ã¢â‚¬Å¡Ã¢â‚¬Å¡ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬â€�ÃƒÂ£Ã¯Â¿Â½Ã‚ÂªÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬Å¾ÃƒÂ£Ã¢â€šÂ¬Ã¢â‚¬Å¡
                return false;
            }

            if ((_skillId == ELEMENTAL_PROTECTION) && (pc.getElfAttr() == 0)) {
                pc.sendPackets(new S_ServerMessage(280)); // \f1ÃƒÂ¦Ã¢â‚¬â€œÃ‚Â½ÃƒÂ¥Ã¢â‚¬â„¢Ã¢â‚¬â„¢ÃƒÂ¥Ã‚Â¤Ã‚Â±ÃƒÂ¦Ã¢â‚¬Â¢Ã¢â‚¬â€�ÃƒÂ£Ã¢â€šÂ¬Ã¢â‚¬Å¡
                return false;
            }

            //DIsable Not casting underwater
            /*
            if (pc.getMap().isUnderwater() && _skill.getAttr() == 2) {
                pc.sendPackets(new S_ServerMessage(280)); // \f1ÃƒÂ¦Ã¢â‚¬â€œÃ‚Â½ÃƒÂ¥Ã¢â‚¬â„¢Ã¢â‚¬â„¢ÃƒÂ¥Ã‚Â¤Ã‚Â±ÃƒÂ¦Ã¢â‚¬Â¢Ã¢â‚¬â€�ÃƒÂ£Ã¢â€šÂ¬Ã¢â‚¬Å¡
                return false;
            }
            */

            // ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¹ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â­ÃƒÂ£Ã†â€™Ã‚Â«ÃƒÂ£Ã†â€™Ã¢â‚¬Â¡ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â£ÃƒÂ£Ã†â€™Ã‚Â¬ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¤ÃƒÂ¤Ã‚Â¸Ã‚Â­ÃƒÂ¤Ã‚Â½Ã‚Â¿ÃƒÂ§Ã¢â‚¬ï¿½Ã‚Â¨ÃƒÂ¤Ã‚Â¸Ã¯Â¿Â½ÃƒÂ¥Ã¯Â¿Â½Ã‚Â¯
            if (pc.isSkillDelay()) {
                return false;
            }

            // ÃƒÂ©Ã‚Â­Ã¢â‚¬ï¿½ÃƒÂ¦Ã‚Â³Ã¢â‚¬Â¢ÃƒÂ¥Ã‚Â°Ã¯Â¿Â½ÃƒÂ¥Ã¯Â¿Â½Ã‚Â°ÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ¥Ã‚Â°Ã¯Â¿Â½ÃƒÂ¥Ã¯Â¿Â½Ã‚Â°ÃƒÂ§Ã‚Â¦Ã¯Â¿Â½ÃƒÂ¥Ã…â€œÃ‚Â°ÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ¥Ã¯Â¿Â½Ã‚Â¡ÃƒÂ¦Ã‚Â¯Ã¢â‚¬â„¢ÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ¥Ã‚Â¹Ã‚Â»ÃƒÂ¦Ã†â€™Ã‚Â³
            if ((pc.hasSkillEffect(SILENCE) ||
                    pc.hasSkillEffect(AREA_OF_SILENCE) ||
                    pc.hasSkillEffect(STATUS_POISON_SILENCE)||
                    pc.hasSkillEffect(CONFUSION_ING)) &&
                    !IntArrays.sContains(CAST_WITH_SILENCE, _skillId)) {
                pc.sendPackets(new S_ServerMessage(285));
                return false;
            }


            if ((_skillId == DISINTEGRATE) && (pc.getLawful() < 500)) {
                // ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬Å“ÃƒÂ£Ã¯Â¿Â½Ã‚Â®ÃƒÂ£Ã†â€™Ã‚Â¡ÃƒÂ£Ã†â€™Ã†â€™ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â»ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¸ÃƒÂ£Ã¯Â¿Â½Ã‚Â§ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬Å¡ÃƒÂ£Ã¯Â¿Â½Ã‚Â£ÃƒÂ£Ã¯Â¿Â½Ã‚Â¦ÃƒÂ£Ã¢â‚¬Å¡Ã¢â‚¬Â¹ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬Â¹ÃƒÂ¦Ã…â€œÃ‚ÂªÃƒÂ§Ã‚Â¢Ã‚ÂºÃƒÂ¨Ã‚ÂªÃ¯Â¿Â½
                pc.sendPackets(new S_ServerMessage(352, "$967")); // ÃƒÂ¨Ã¢â‚¬Â¹Ã‚Â¥ÃƒÂ¨Ã‚Â¦Ã¯Â¿Â½ÃƒÂ¤Ã‚Â½Ã‚Â¿ÃƒÂ§Ã¢â‚¬ï¿½Ã‚Â¨ÃƒÂ©Ã¢â€šÂ¬Ã¢â€žÂ¢ÃƒÂ¥Ã¢â€šÂ¬Ã¢â‚¬Â¹ÃƒÂ¦Ã‚Â³Ã¢â‚¬Â¢ÃƒÂ¨Ã‚Â¡Ã¢â‚¬Å“ÃƒÂ¯Ã‚Â¼Ã…â€™ÃƒÂ¥Ã‚Â±Ã‚Â¬ÃƒÂ¦Ã¢â€šÂ¬Ã‚Â§ÃƒÂ¥Ã‚Â¿Ã¢â‚¬Â¦ÃƒÂ©Ã‚ Ã‹â€ ÃƒÂ¦Ã‹â€ Ã¯Â¿Â½ÃƒÂ§Ã¢â‚¬Å¡Ã‚Âº (ÃƒÂ¦Ã‚Â­Ã‚Â£ÃƒÂ§Ã‚Â¾Ã‚Â©)ÃƒÂ£Ã¢â€šÂ¬Ã¢â‚¬Å¡
                return false;
            }

            // ÃƒÂ¥Ã¯Â¿Â½Ã…â€™ÃƒÂ£Ã¯Â¿Â½Ã‹Å“ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â­ÃƒÂ£Ã†â€™Ã‚Â¥ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã†â€™Ã¢â‚¬â€œÃƒÂ£Ã¯Â¿Â½Ã‚Â¯ÃƒÂ¥Ã… Ã‚Â¹ÃƒÂ¦Ã…Â¾Ã…â€œÃƒÂ§Ã‚Â¯Ã¢â‚¬Å¾ÃƒÂ¥Ã¢â‚¬ÂºÃ‚Â²ÃƒÂ¥Ã‚Â¤Ã¢â‚¬â€œÃƒÂ£Ã¯Â¿Â½Ã‚Â§ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬Å¡ÃƒÂ£Ã¢â‚¬Å¡Ã…â€™ÃƒÂ£Ã¯Â¿Â½Ã‚Â°ÃƒÂ©Ã¢â‚¬Â¦Ã¯Â¿Â½ÃƒÂ§Ã‚Â½Ã‚Â®ÃƒÂ¥Ã¯Â¿Â½Ã‚Â¯ÃƒÂ¨Ã†â€™Ã‚Â½
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
                    pc.sendPackets(new S_ServerMessage(1412)); // ÃƒÂ¥Ã‚Â·Ã‚Â²ÃƒÂ¥Ã…â€œÃ‚Â¨ÃƒÂ¥Ã…â€œÃ‚Â°ÃƒÂ¦Ã¯Â¿Â½Ã‚Â¿ÃƒÂ¤Ã‚Â¸Ã… ÃƒÂ¥Ã¯Â¿Â½Ã‚Â¬ÃƒÂ¥Ã¢â‚¬â€œÃ…Â¡ÃƒÂ¤Ã‚ÂºÃ¢â‚¬ ÃƒÂ©Ã‚Â­Ã¢â‚¬ï¿½ÃƒÂ¦Ã‚Â³Ã¢â‚¬Â¢ÃƒÂ§Ã‚Â«Ã¢â‚¬Â¹ÃƒÂ¦Ã¢â‚¬â€œÃ‚Â¹ÃƒÂ¥Ã‚Â¡Ã… ÃƒÂ£Ã¢â€šÂ¬Ã¢â‚¬Å¡
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

            // ÃƒÂ¨Ã‚Â¦Ã‚ÂºÃƒÂ©Ã¢â‚¬ Ã¢â‚¬â„¢ÃƒÂ§Ã¢â‚¬Â¹Ã¢â€šÂ¬ÃƒÂ¦Ã¢â‚¬Â¦Ã¢â‚¬Â¹ - ÃƒÂ©Ã¯Â¿Â½Ã…Â¾ÃƒÂ¨Ã‚Â¦Ã‚ÂºÃƒÂ©Ã¢â‚¬ Ã¢â‚¬â„¢ÃƒÂ¦Ã… Ã¢â€šÂ¬ÃƒÂ¨Ã†â€™Ã‚Â½ÃƒÂ§Ã¢â‚¬Å¾Ã‚Â¡ÃƒÂ¦Ã‚Â³Ã¢â‚¬Â¢ÃƒÂ¤Ã‚Â½Ã‚Â¿ÃƒÂ§Ã¢â‚¬ï¿½Ã‚Â¨
            //[Legends] - Disable Preventing them from casting with buffs on.
            /*
            if (((pc.getAwakeSkillId() == AWAKEN_ANTHARAS) && (_skillId != AWAKEN_ANTHARAS))
                    || ((pc.getAwakeSkillId() == AWAKEN_FAFURION) && (_skillId != AWAKEN_FAFURION))
                    || ((pc.getAwakeSkillId() == AWAKEN_VALAKAS) && (_skillId != AWAKEN_VALAKAS))
                    && (_skillId != MAGMA_BREATH) && (_skillId != SHOCK_SKIN) && (_skillId != FREEZING_BREATH)) {
                pc.sendPackets(new S_ServerMessage(1385)); // ÃƒÂ§Ã¢â‚¬ÂºÃ‚Â®ÃƒÂ¥Ã¢â‚¬Â°Ã¯Â¿Â½ÃƒÂ§Ã¢â‚¬Â¹Ã¢â€šÂ¬ÃƒÂ¦Ã¢â‚¬Â¦Ã¢â‚¬Â¹ÃƒÂ¤Ã‚Â¸Ã‚Â­ÃƒÂ§Ã¢â‚¬Å¾Ã‚Â¡ÃƒÂ¦Ã‚Â³Ã¢â‚¬Â¢ÃƒÂ¤Ã‚Â½Ã‚Â¿ÃƒÂ§Ã¢â‚¬ï¿½Ã‚Â¨ÃƒÂ¨Ã‚Â¦Ã‚ÂºÃƒÂ©Ã¢â‚¬ Ã¢â‚¬â„¢ÃƒÂ©Ã‚Â­Ã¢â‚¬ï¿½ÃƒÂ¦Ã‚Â³Ã¢â‚¬Â¢ÃƒÂ£Ã¢â€šÂ¬Ã¢â‚¬Å¡
                return false;
            }*/

            // [Mike] Fix ItemConsume when casting spells..
            if ((isItemConsume() == false) && !_player.isGm()) { // ÃƒÂ¦Ã‚Â³Ã¢â‚¬Â¢ÃƒÂ¨Ã‚Â¡Ã¢â‚¬Å“ÃƒÂ¦Ã‚Â¶Ã‹â€ ÃƒÂ¨Ã¢â€šÂ¬Ã¢â‚¬â€�ÃƒÂ©Ã¯Â¿Â½Ã¢â‚¬Å“ÃƒÂ¥Ã¢â‚¬Â¦Ã‚Â·ÃƒÂ¥Ã‹â€ Ã‚Â¤ÃƒÂ¦Ã¢â‚¬â€œÃ‚Â·ÃƒÂ£Ã¢â€šÂ¬Ã¢â‚¬Å¡
                _player.sendPackets(new S_ServerMessage(299)); // \f1ÃƒÂ¦Ã¢â‚¬â€œÃ‚Â½ÃƒÂ¦Ã¢â‚¬ï¿½Ã‚Â¾ÃƒÂ©Ã‚Â­Ã¢â‚¬ï¿½ÃƒÂ¦Ã‚Â³Ã¢â‚¬Â¢ÃƒÂ¦Ã¢â‚¬Â°Ã¢â€šÂ¬ÃƒÂ©Ã…â€œÃ¢â€šÂ¬ÃƒÂ¦Ã¯Â¿Â½Ã¯Â¿Â½ÃƒÂ¦Ã¢â‚¬â€œÃ¢â€žÂ¢ÃƒÂ¤Ã‚Â¸Ã¯Â¿Â½ÃƒÂ¨Ã‚Â¶Ã‚Â³ÃƒÂ£Ã¢â€šÂ¬Ã¢â‚¬Å¡
                return false;
            }
        }
        // ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¹ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â­ÃƒÂ£Ã†â€™Ã‚Â«ÃƒÂ¤Ã‚Â½Ã‚Â¿ÃƒÂ§Ã¢â‚¬ï¿½Ã‚Â¨ÃƒÂ¨Ã¢â€šÂ¬Ã¢â‚¬Â¦ÃƒÂ£Ã¯Â¿Â½Ã…â€™NPCÃƒÂ£Ã¯Â¿Â½Ã‚Â®ÃƒÂ¥Ã‚ Ã‚Â´ÃƒÂ¥Ã¯Â¿Â½Ã‹â€ ÃƒÂ£Ã¯Â¿Â½Ã‚Â®ÃƒÂ£Ã†â€™Ã¯Â¿Â½ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â§ÃƒÂ£Ã†â€™Ã†â€™ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¯
        else if (_user instanceof L1NpcInstance) {

            // ÃƒÂ£Ã¢â‚¬Å¡Ã‚ÂµÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¤ÃƒÂ£Ã†â€™Ã‚Â¬ÃƒÂ£Ã†â€™Ã‚Â³ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¹ÃƒÂ§Ã… Ã‚Â¶ÃƒÂ¦Ã¢â‚¬Â¦Ã¢â‚¬Â¹ÃƒÂ£Ã¯Â¿Â½Ã‚Â§ÃƒÂ£Ã¯Â¿Â½Ã‚Â¯ÃƒÂ¤Ã‚Â½Ã‚Â¿ÃƒÂ§Ã¢â‚¬ï¿½Ã‚Â¨ÃƒÂ¤Ã‚Â¸Ã¯Â¿Â½ÃƒÂ¥Ã¯Â¿Â½Ã‚Â¯
            if (_user.hasSkillEffect(SILENCE)) {
                // NPCÃƒÂ£Ã¯Â¿Â½Ã‚Â«ÃƒÂ£Ã¢â‚¬Å¡Ã‚ÂµÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¤ÃƒÂ£Ã†â€™Ã‚Â¬ÃƒÂ£Ã†â€™Ã‚Â³ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¹ÃƒÂ£Ã¯Â¿Â½Ã…â€™ÃƒÂ¦Ã…Â½Ã¢â‚¬ÂºÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬Â¹ÃƒÂ£Ã¯Â¿Â½Ã‚Â£ÃƒÂ£Ã¯Â¿Â½Ã‚Â¦ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬Å¾ÃƒÂ£Ã¢â‚¬Å¡Ã¢â‚¬Â¹ÃƒÂ¥Ã‚ Ã‚Â´ÃƒÂ¥Ã¯Â¿Â½Ã‹â€ ÃƒÂ£Ã¯Â¿Â½Ã‚Â¯1ÃƒÂ¥Ã¢â‚¬ÂºÃ…Â¾ÃƒÂ£Ã¯Â¿Â½Ã‚ ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬ËœÃƒÂ¤Ã‚Â½Ã‚Â¿ÃƒÂ§Ã¢â‚¬ï¿½Ã‚Â¨ÃƒÂ£Ã¢â‚¬Å¡Ã¢â‚¬â„¢ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â­ÃƒÂ£Ã†â€™Ã‚Â£ÃƒÂ£Ã†â€™Ã‚Â³ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â»ÃƒÂ£Ã†â€™Ã‚Â«ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬Â¢ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬ÂºÃƒÂ£Ã¢â‚¬Å¡Ã¢â‚¬Â¹ÃƒÂ¥Ã… Ã‚Â¹ÃƒÂ¦Ã…Â¾Ã…â€œÃƒÂ£Ã¢â€šÂ¬Ã¢â‚¬Å¡
                _user.removeSkillEffect(SILENCE);
                return false;
            }
        }

        // PCÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½NPCÃƒÂ¥Ã¢â‚¬Â¦Ã‚Â±ÃƒÂ©Ã¢â€šÂ¬Ã…Â¡ÃƒÂ¦Ã‚ÂªÃ‚Â¢ÃƒÂ¦Ã…Â¸Ã‚Â¥HPÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½MPÃƒÂ¦Ã‹Å“Ã‚Â¯ÃƒÂ¥Ã¯Â¿Â½Ã‚Â¦ÃƒÂ¨Ã‚Â¶Ã‚Â³ÃƒÂ¥Ã‚Â¤Ã‚
        if (!isHPMPConsume()) { // ÃƒÂ¨Ã… Ã‚Â±ÃƒÂ¨Ã‚Â²Ã‚Â»ÃƒÂ§Ã…Â¡Ã¢â‚¬Å¾HPÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½MPÃƒÂ¨Ã‚Â¨Ã‹â€ ÃƒÂ§Ã‚Â®Ã¢â‚¬â€�
            return false;
        }
        return true;
    }


    private boolean isSpellScrollUsable() {

        L1PcInstance pc = (L1PcInstance) _user;

        if (pc.isTeleport()) {
            return false;
        }

        if (pc.isParalyzed()) {
            return false;
        }


        if ((pc.isInvisble() || pc.isInvisDelay()) && !isInvisUsableSkill()) {
            return false;
        }

        return true;
    }


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

            if (!isCheckedUseSkill()) {
                boolean isUseSkill = checkUseSkill(player, skillId, targetId, x, y, message, timeSecs, type, attacker);

                if (!isUseSkill) {
                    failSkill();
                    return;
                }
            }

            if (type == TYPE_NORMAL) { // ÃƒÂ©Ã‚Â­Ã¢â‚¬ï¿½ÃƒÂ¦Ã‚Â³Ã¢â‚¬Â¢ÃƒÂ¨Ã‚Â©Ã‚ ÃƒÂ¥Ã¢â‚¬ï¿½Ã‚Â±ÃƒÂ¦Ã¢â€žÂ¢Ã¢â‚¬Å¡
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
            else if (type == TYPE_LOGIN) { // ÃƒÂ£Ã†â€™Ã‚Â­ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â°ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¤ÃƒÂ£Ã†â€™Ã‚Â³ÃƒÂ¦Ã¢â€žÂ¢Ã¢â‚¬Å¡ÃƒÂ¯Ã‚Â¼Ã‹â€ HPMPÃƒÂ¦Ã¯Â¿Â½Ã¯Â¿Â½ÃƒÂ¦Ã¢â‚¬â€œÃ¢â€žÂ¢ÃƒÂ¦Ã‚Â¶Ã‹â€ ÃƒÂ¨Ã‚Â²Ã‚Â»ÃƒÂ£Ã¯Â¿Â½Ã‚ÂªÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬â€�ÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â°ÃƒÂ£Ã†â€™Ã‚Â©ÃƒÂ£Ã†â€™Ã¢â‚¬Â¢ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â£ÃƒÂ£Ã†â€™Ã†â€™ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¯ÃƒÂ£Ã¯Â¿Â½Ã‚ÂªÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬â€�ÃƒÂ¯Ã‚Â¼Ã¢â‚¬Â°
                runSkill();
            }
            else if (type == TYPE_SPELLSC) { // ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¹ÃƒÂ£Ã†â€™Ã…Â¡ÃƒÂ£Ã†â€™Ã‚Â«ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¹ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¯ÃƒÂ£Ã†â€™Ã‚Â­ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã†â€™Ã‚Â«ÃƒÂ¤Ã‚Â½Ã‚Â¿ÃƒÂ§Ã¢â‚¬ï¿½Ã‚Â¨ÃƒÂ¦Ã¢â€žÂ¢Ã¢â‚¬Å¡ÃƒÂ¯Ã‚Â¼Ã‹â€ HPMPÃƒÂ¦Ã¯Â¿Â½Ã¯Â¿Â½ÃƒÂ¦Ã¢â‚¬â€œÃ¢â€žÂ¢ÃƒÂ¦Ã‚Â¶Ã‹â€ ÃƒÂ¨Ã‚Â²Ã‚Â»ÃƒÂ£Ã¯Â¿Â½Ã‚ÂªÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬â€�ÃƒÂ¯Ã‚Â¼Ã¢â‚¬Â°
                runSkill();
                sendGrfx(true);
            }
            else if (type == TYPE_GMBUFF) { // GMBUFFÃƒÂ¤Ã‚Â½Ã‚Â¿ÃƒÂ§Ã¢â‚¬ï¿½Ã‚Â¨ÃƒÂ¦Ã¢â€žÂ¢Ã¢â‚¬Å¡ÃƒÂ¯Ã‚Â¼Ã‹â€ HPMPÃƒÂ¦Ã¯Â¿Â½Ã¯Â¿Â½ÃƒÂ¦Ã¢â‚¬â€œÃ¢â€žÂ¢ÃƒÂ¦Ã‚Â¶Ã‹â€ ÃƒÂ¨Ã‚Â²Ã‚Â»ÃƒÂ£Ã¯Â¿Â½Ã‚ÂªÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬â€�ÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ©Ã‚Â­Ã¢â‚¬ï¿½ÃƒÂ¦Ã‚Â³Ã¢â‚¬Â¢ÃƒÂ£Ã†â€™Ã‚Â¢ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â·ÃƒÂ£Ã†â€™Ã‚Â§ÃƒÂ£Ã†â€™Ã‚Â³ÃƒÂ£Ã¯Â¿Â½Ã‚ÂªÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬â€�ÃƒÂ¯Ã‚Â¼Ã¢â‚¬Â°
                runSkill();
                sendGrfx(false);
            }
            else if (type == TYPE_NPCBUFF) { // NPCBUFFÃƒÂ¤Ã‚Â½Ã‚Â¿ÃƒÂ§Ã¢â‚¬ï¿½Ã‚Â¨ÃƒÂ¦Ã¢â€žÂ¢Ã¢â‚¬Å¡ÃƒÂ¯Ã‚Â¼Ã‹â€ HPMPÃƒÂ¦Ã¯Â¿Â½Ã¯Â¿Â½ÃƒÂ¦Ã¢â‚¬â€œÃ¢â€žÂ¢ÃƒÂ¦Ã‚Â¶Ã‹â€ ÃƒÂ¨Ã‚Â²Ã‚Â»ÃƒÂ£Ã¯Â¿Â½Ã‚ÂªÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬â€�ÃƒÂ¯Ã‚Â¼Ã¢â‚¬Â°
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
     * ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¹ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â­ÃƒÂ£Ã†â€™Ã‚Â«ÃƒÂ£Ã¯Â¿Â½Ã‚Â®ÃƒÂ¥Ã‚Â¤Ã‚Â±ÃƒÂ¦Ã¢â‚¬Â¢Ã¢â‚¬â€�ÃƒÂ¥Ã¢â‚¬Â¡Ã‚Â¦ÃƒÂ§Ã¯Â¿Â½Ã¢â‚¬ (PCÃƒÂ£Ã¯Â¿Â½Ã‚Â®ÃƒÂ£Ã¯Â¿Â½Ã‚Â¿ÃƒÂ¯Ã‚Â¼Ã¢â‚¬Â°
     */
    private void failSkill() {
        // HPÃƒÂ£Ã¯Â¿Â½Ã…â€™ÃƒÂ¨Ã‚Â¶Ã‚Â³ÃƒÂ£Ã¢â‚¬Å¡Ã… ÃƒÂ£Ã¯Â¿Â½Ã‚ÂªÃƒÂ£Ã¯Â¿Â½Ã¯Â¿Â½ÃƒÂ£Ã¯Â¿Â½Ã‚Â¦ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¹ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â­ÃƒÂ£Ã†â€™Ã‚Â«ÃƒÂ£Ã¯Â¿Â½Ã…â€™ÃƒÂ¤Ã‚Â½Ã‚Â¿ÃƒÂ§Ã¢â‚¬ï¿½Ã‚Â¨ÃƒÂ£Ã¯Â¿Â½Ã‚Â§ÃƒÂ£Ã¯Â¿Â½Ã¯Â¿Â½ÃƒÂ£Ã¯Â¿Â½Ã‚ÂªÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬Å¾ÃƒÂ¥Ã‚ Ã‚Â´ÃƒÂ¥Ã¯Â¿Â½Ã‹â€ ÃƒÂ£Ã¯Â¿Â½Ã‚Â®ÃƒÂ£Ã¯Â¿Â½Ã‚Â¿ÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½MPÃƒÂ£Ã¯Â¿Â½Ã‚Â®ÃƒÂ£Ã¯Â¿Â½Ã‚Â¿ÃƒÂ¦Ã‚Â¶Ã‹â€ ÃƒÂ¨Ã‚Â²Ã‚Â»ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬â€�ÃƒÂ£Ã¯Â¿Â½Ã…Â¸ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬Å¾ÃƒÂ£Ã¯Â¿Â½Ã…â€™ÃƒÂ¦Ã…â€œÃ‚ÂªÃƒÂ¥Ã‚Â®Ã…Â¸ÃƒÂ¨Ã‚Â£Ã¢â‚¬Â¦ÃƒÂ¯Ã‚Â¼Ã‹â€ ÃƒÂ¥Ã‚Â¿Ã¢â‚¬Â¦ÃƒÂ¨Ã‚Â¦Ã¯Â¿Â½ÃƒÂ£Ã¯Â¿Â½Ã‚ÂªÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬Å¾ÃƒÂ¯Ã‚Â¼Ã…Â¸ÃƒÂ¯Ã‚Â¼Ã¢â‚¬Â°
        // ÃƒÂ£Ã¯Â¿Â½Ã¯Â¿Â½ÃƒÂ£Ã¯Â¿Â½Ã‚Â®ÃƒÂ¤Ã‚Â»Ã¢â‚¬â€œÃƒÂ£Ã¯Â¿Â½Ã‚Â®ÃƒÂ¥Ã‚ Ã‚Â´ÃƒÂ¥Ã¯Â¿Â½Ã‹â€ ÃƒÂ£Ã¯Â¿Â½Ã‚Â¯ÃƒÂ¤Ã‚Â½Ã¢â‚¬Â¢ÃƒÂ£Ã¢â‚¬Å¡Ã¢â‚¬Å¡ÃƒÂ¦Ã‚Â¶Ã‹â€ ÃƒÂ¨Ã‚Â²Ã‚Â»ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬Â¢ÃƒÂ£Ã¢â‚¬Å¡Ã…â€™ÃƒÂ£Ã¯Â¿Â½Ã‚ÂªÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬Å¾ÃƒÂ£Ã¢â€šÂ¬Ã¢â‚¬Å¡
        // useConsume(); // HPÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½MPÃƒÂ£Ã¯Â¿Â½Ã‚Â¯ÃƒÂ¦Ã‚Â¸Ã¢â‚¬ÂºÃƒÂ£Ã¢â‚¬Å¡Ã¢â‚¬Â°ÃƒÂ£Ã¯Â¿Â½Ã¢â€žÂ¢
        setCheckedUseSkill(false);
        // ÃƒÂ£Ã†â€™Ã¢â‚¬ ÃƒÂ£Ã†â€™Ã‚Â¬ÃƒÂ£Ã†â€™Ã¯Â¿Â½ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã†â€™Ã‹â€ ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¹ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â­ÃƒÂ£Ã†â€™Ã‚Â«
        if ((_skillId == TELEPORT) || (_skillId == MASS_TELEPORT) || (_skillId == TELEPORT_TO_MATHER)) {
            // ÃƒÂ£Ã†â€™Ã¢â‚¬ ÃƒÂ£Ã†â€™Ã‚Â¬ÃƒÂ£Ã†â€™Ã¯Â¿Â½ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã†â€™Ã‹â€ ÃƒÂ£Ã¯Â¿Â½Ã‚Â§ÃƒÂ£Ã¯Â¿Â½Ã¯Â¿Â½ÃƒÂ£Ã¯Â¿Â½Ã‚ÂªÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬Å¾ÃƒÂ¥Ã‚ Ã‚Â´ÃƒÂ¥Ã¯Â¿Â½Ã‹â€ ÃƒÂ£Ã¯Â¿Â½Ã‚Â§ÃƒÂ£Ã¢â‚¬Å¡Ã¢â‚¬Å¡ÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¯ÃƒÂ£Ã†â€™Ã‚Â©ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¤ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¢ÃƒÂ£Ã†â€™Ã‚Â³ÃƒÂ£Ã†â€™Ã‹â€ ÃƒÂ¥Ã¯Â¿Â½Ã‚Â´ÃƒÂ£Ã¯Â¿Â½Ã‚Â¯ÃƒÂ¥Ã‚Â¿Ã…â€œÃƒÂ§Ã‚Â­Ã¢â‚¬ï¿½ÃƒÂ£Ã¢â‚¬Å¡Ã¢â‚¬â„¢ÃƒÂ¥Ã‚Â¾Ã¢â‚¬Â¦ÃƒÂ£Ã¯Â¿Â½Ã‚Â£ÃƒÂ£Ã¯Â¿Â½Ã‚Â¦ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬Å¾ÃƒÂ£Ã¢â‚¬Å¡Ã¢â‚¬Â¹
            // ÃƒÂ£Ã†â€™Ã¢â‚¬ ÃƒÂ£Ã†â€™Ã‚Â¬ÃƒÂ£Ã†â€™Ã¯Â¿Â½ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã†â€™Ã‹â€ ÃƒÂ¥Ã‚Â¾Ã¢â‚¬Â¦ÃƒÂ£Ã¯Â¿Â½Ã‚Â¡ÃƒÂ§Ã… Ã‚Â¶ÃƒÂ¦Ã¢â‚¬Â¦Ã¢â‚¬Â¹ÃƒÂ£Ã¯Â¿Â½Ã‚Â®ÃƒÂ¨Ã‚Â§Ã‚Â£ÃƒÂ©Ã¢â€žÂ¢Ã‚Â¤ÃƒÂ¯Ã‚Â¼Ã‹â€ ÃƒÂ§Ã‚Â¬Ã‚Â¬2ÃƒÂ¥Ã‚Â¼Ã¢â‚¬Â¢ÃƒÂ¦Ã¢â‚¬Â¢Ã‚Â°ÃƒÂ£Ã¯Â¿Â½Ã‚Â«ÃƒÂ¦Ã¢â‚¬Å¾Ã¯Â¿Â½ÃƒÂ¥Ã¢â‚¬ËœÃ‚Â³ÃƒÂ£Ã¯Â¿Â½Ã‚Â¯ÃƒÂ£Ã¯Â¿Â½Ã‚ÂªÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬Å¾ÃƒÂ¯Ã‚Â¼Ã¢â‚¬Â°
            _player.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
        }
    }

    // ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¿ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â²ÃƒÂ£Ã†â€™Ã†â€™ÃƒÂ£Ã†â€™Ã‹â€ ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬Â¹ÃƒÂ¯Ã‚Â¼Ã…Â¸
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

        // ÃƒÂ§Ã‚ Ã‚Â´ÃƒÂ¥Ã‚Â£Ã… ÃƒÂ¤Ã‚Â¸Ã¯Â¿Â½ÃƒÂ¥Ã¯Â¿Â½Ã‚Â¯ÃƒÂ¨Ã†â€™Ã‚Â½ÃƒÂ£Ã¯Â¿Â½Ã‚ÂªÃƒÂ£Ã†â€™Ã¢â‚¬Â°ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¢ÃƒÂ£Ã¯Â¿Â½Ã‚Â¯ÃƒÂ¥Ã‚Â¯Ã‚Â¾ÃƒÂ¨Ã‚Â±Ã‚Â¡ÃƒÂ¥Ã‚Â¤Ã¢â‚¬â€œ
        if (cha instanceof L1DoorInstance) {
            if ((cha.getMaxHp() == 0) || (cha.getMaxHp() == 1)) {
                return false;
            }
        }

        // ÃƒÂ£Ã†â€™Ã…Â¾ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¸ÃƒÂ£Ã†â€™Ã†â€™ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¯ÃƒÂ£Ã†â€™Ã¢â‚¬Â°ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã†â€™Ã‚Â«ÃƒÂ£Ã¯Â¿Â½Ã‚Â¯ÃƒÂ¥Ã‚Â¯Ã‚Â¾ÃƒÂ¨Ã‚Â±Ã‚Â¡ÃƒÂ¥Ã‚Â¤Ã¢â‚¬â€œ
        if ((cha instanceof L1DollInstance) && (_skillId != HASTE)) {
            return false;
        }

        // ÃƒÂ¥Ã¢â‚¬Â¦Ã†â€™ÃƒÂ£Ã¯Â¿Â½Ã‚Â®ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¿ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â²ÃƒÂ£Ã†â€™Ã†â€™ÃƒÂ£Ã†â€™Ã‹â€ ÃƒÂ£Ã¯Â¿Â½Ã…â€™PetÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½SummonÃƒÂ¤Ã‚Â»Ã‚Â¥ÃƒÂ¥Ã‚Â¤Ã¢â‚¬â€œÃƒÂ£Ã¯Â¿Â½Ã‚Â®NPCÃƒÂ£Ã¯Â¿Â½Ã‚Â®ÃƒÂ¥Ã‚ Ã‚Â´ÃƒÂ¥Ã¯Â¿Â½Ã‹â€ ÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½PCÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½PetÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½SummonÃƒÂ£Ã¯Â¿Â½Ã‚Â¯ÃƒÂ¥Ã‚Â¯Ã‚Â¾ÃƒÂ¨Ã‚Â±Ã‚Â¡ÃƒÂ¥Ã‚Â¤Ã¢â‚¬â€œ
        if ((_calcType == PC_NPC) && (_target instanceof L1NpcInstance) && !(_target instanceof L1PetInstance)
                && !(_target instanceof L1SummonInstance)
                && ((cha instanceof L1PetInstance) || (cha instanceof L1SummonInstance) || (cha instanceof L1PcInstance))) {
            return false;
        }

        // ÃƒÂ¥Ã¢â‚¬Â¦Ã†â€™ÃƒÂ£Ã¯Â¿Â½Ã‚Â®ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¿ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â²ÃƒÂ£Ã†â€™Ã†â€™ÃƒÂ£Ã†â€™Ã‹â€ ÃƒÂ£Ã¯Â¿Â½Ã…â€™ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¬ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã†â€™Ã¢â‚¬Â°ÃƒÂ¤Ã‚Â»Ã‚Â¥ÃƒÂ¥Ã‚Â¤Ã¢â‚¬â€œÃƒÂ£Ã¯Â¿Â½Ã‚Â®NPCÃƒÂ£Ã¯Â¿Â½Ã‚Â®ÃƒÂ¥Ã‚ Ã‚Â´ÃƒÂ¥Ã¯Â¿Â½Ã‹â€ ÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¬ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã†â€™Ã¢â‚¬Â°ÃƒÂ£Ã¯Â¿Â½Ã‚Â¯ÃƒÂ¥Ã‚Â¯Ã‚Â¾ÃƒÂ¨Ã‚Â±Ã‚Â¡ÃƒÂ¥Ã‚Â¤Ã¢â‚¬â€œ
        if ((_calcType == PC_NPC) && (_target instanceof L1NpcInstance) && !(_target instanceof L1GuardInstance) && (cha instanceof L1GuardInstance)) {
            return false;
        }

        // NPCÃƒÂ¥Ã‚Â¯Ã‚Â¾PCÃƒÂ£Ã¯Â¿Â½Ã‚Â§ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¿ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â²ÃƒÂ£Ã†â€™Ã†â€™ÃƒÂ£Ã†â€™Ã‹â€ ÃƒÂ£Ã¯Â¿Â½Ã…â€™ÃƒÂ£Ã†â€™Ã‚Â¢ÃƒÂ£Ã†â€™Ã‚Â³ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¹ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¿ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã¯Â¿Â½Ã‚Â®ÃƒÂ¥Ã‚ Ã‚Â´ÃƒÂ¥Ã¯Â¿Â½Ã‹â€ ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¿ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â²ÃƒÂ£Ã†â€™Ã†â€™ÃƒÂ£Ã†â€™Ã‹â€ ÃƒÂ£Ã¯Â¿Â½Ã‚Â§ÃƒÂ£Ã¯Â¿Â½Ã‚Â¯ÃƒÂ£Ã¯Â¿Â½Ã‚ÂªÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬Å¾ÃƒÂ£Ã¢â€šÂ¬Ã¢â‚¬Å¡
        if ((_skill.getTarget().equals("attack") || (_skill.getType() == L1Skills.TYPE_ATTACK)) && (_calcType == NPC_PC)
                && !(cha instanceof L1PetInstance) && !(cha instanceof L1SummonInstance) && !(cha instanceof L1PcInstance)) {
            return false;
        }

        // NPCÃƒÂ¥Ã‚Â¯Ã‚Â¾NPCÃƒÂ£Ã¯Â¿Â½Ã‚Â§ÃƒÂ¤Ã‚Â½Ã‚Â¿ÃƒÂ§Ã¢â‚¬ï¿½Ã‚Â¨ÃƒÂ¨Ã¢â€šÂ¬Ã¢â‚¬Â¦ÃƒÂ£Ã¯Â¿Â½Ã…â€™MOBÃƒÂ£Ã¯Â¿Â½Ã‚Â§ÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¿ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â²ÃƒÂ£Ã†â€™Ã†â€™ÃƒÂ£Ã†â€™Ã‹â€ ÃƒÂ£Ã¯Â¿Â½Ã…â€™MOBÃƒÂ£Ã¯Â¿Â½Ã‚Â®ÃƒÂ¥Ã‚ Ã‚Â´ÃƒÂ¥Ã¯Â¿Â½Ã‹â€ ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¿ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â²ÃƒÂ£Ã†â€™Ã†â€™ÃƒÂ£Ã†â€™Ã‹â€ ÃƒÂ£Ã¯Â¿Â½Ã‚Â§ÃƒÂ£Ã¯Â¿Â½Ã‚Â¯ÃƒÂ£Ã¯Â¿Â½Ã‚ÂªÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬Å¾ÃƒÂ£Ã¢â€šÂ¬Ã¢â‚¬Å¡
        if ((_skill.getTarget().equals("attack") || (_skill.getType() == L1Skills.TYPE_ATTACK)) && (_calcType == NPC_NPC)
                && (_user instanceof L1MonsterInstance) && (cha instanceof L1MonsterInstance)) {
            return false;
        }

        // ÃƒÂ§Ã¢â‚¬Å¾Ã‚Â¡ÃƒÂ¦Ã¢â‚¬â€œÃ‚Â¹ÃƒÂ¥Ã¯Â¿Â½Ã¢â‚¬ËœÃƒÂ§Ã‚Â¯Ã¢â‚¬Å¾ÃƒÂ¥Ã¢â‚¬ÂºÃ‚Â²ÃƒÂ¦Ã¢â‚¬ï¿½Ã‚Â»ÃƒÂ¦Ã¢â‚¬â„¢Ã†â€™ÃƒÂ©Ã‚Â­Ã¢â‚¬ï¿½ÃƒÂ¦Ã‚Â³Ã¢â‚¬Â¢ÃƒÂ£Ã¯Â¿Â½Ã‚Â§ÃƒÂ¦Ã¢â‚¬ï¿½Ã‚Â»ÃƒÂ¦Ã¢â‚¬â„¢Ã†â€™ÃƒÂ£Ã¯Â¿Â½Ã‚Â§ÃƒÂ£Ã¯Â¿Â½Ã¯Â¿Â½ÃƒÂ£Ã¯Â¿Â½Ã‚ÂªÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬Å¾NPCÃƒÂ£Ã¯Â¿Â½Ã‚Â¯ÃƒÂ¥Ã‚Â¯Ã‚Â¾ÃƒÂ¨Ã‚Â±Ã‚Â¡ÃƒÂ¥Ã‚Â¤Ã¢â‚¬â€œ
        if (_skill.getTarget().equals("none")
                && (_skill.getType() == L1Skills.TYPE_ATTACK)
                && ((cha instanceof L1AuctionBoardInstance) || (cha instanceof L1BoardInstance) || (cha instanceof L1CrownInstance)
                || (cha instanceof L1DwarfInstance) || (cha instanceof L1EffectInstance) || (cha instanceof L1FieldObjectInstance)
                || (cha instanceof L1FurnitureInstance) || (cha instanceof L1HousekeeperInstance) || (cha instanceof L1MerchantInstance) || (cha instanceof L1TeleporterInstance))) {
            return false;
        }

        // ÃƒÂ¦Ã¢â‚¬ï¿½Ã‚Â»ÃƒÂ¦Ã¢â‚¬Å“Ã… ÃƒÂ¥Ã…Â¾Ã¢â‚¬Â¹ÃƒÂ©Ã‚Â­Ã¢â‚¬ï¿½ÃƒÂ¦Ã‚Â³Ã¢â‚¬Â¢ÃƒÂ§Ã¢â‚¬Å¾Ã‚Â¡ÃƒÂ¦Ã‚Â³Ã¢â‚¬Â¢ÃƒÂ¦Ã¢â‚¬ï¿½Ã‚Â»ÃƒÂ¦Ã¢â‚¬Å“Ã… ÃƒÂ¨Ã¢â‚¬Â¡Ã‚ÂªÃƒÂ¥Ã‚Â·Ã‚Â±
        if ((_skill.getType() == L1Skills.TYPE_ATTACK) && (cha.getId() == _user.getId())) {
            return false;
        }

        // ÃƒÂ©Ã‚Â«Ã¢â‚¬ï¿½ÃƒÂ¥Ã… Ã¢â‚¬ÂºÃƒÂ¥Ã¢â‚¬ÂºÃ…Â¾ÃƒÂ¥Ã‚Â¾Ã‚Â©ÃƒÂ¨Ã‚Â¡Ã¢â‚¬Å“ÃƒÂ¥Ã‹â€ Ã‚Â¤ÃƒÂ¦Ã¢â‚¬â€œÃ‚Â·ÃƒÂ¦Ã¢â‚¬â€œÃ‚Â½ÃƒÂ¦Ã‚Â³Ã¢â‚¬Â¢ÃƒÂ¨Ã¢â€šÂ¬Ã¢â‚¬Â¦ÃƒÂ¤Ã‚Â¸Ã¯Â¿Â½ÃƒÂ¨Ã‚Â£Ã…â€œÃƒÂ¨Ã‚Â¡Ã¢â€šÂ¬
        if ((cha.getId() == _user.getId()) && (_skillId == HEAL_ALL)) {
            return false;
        }

        if ((((_skill.getTargetTo() & L1Skills.TARGET_TO_PC) == L1Skills.TARGET_TO_PC)
                || ((_skill.getTargetTo() & L1Skills.TARGET_TO_CLAN) == L1Skills.TARGET_TO_CLAN) || ((_skill.getTargetTo() & L1Skills.TARGET_TO_PARTY) == L1Skills.TARGET_TO_PARTY))
                && (cha.getId() == _user.getId()) && (_skillId != HEAL_ALL)) {
            return true; // ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¿ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â²ÃƒÂ£Ã†â€™Ã†â€™ÃƒÂ£Ã†â€™Ã‹â€ ÃƒÂ£Ã¯Â¿Â½Ã…â€™ÃƒÂ£Ã†â€™Ã¢â‚¬ËœÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã†â€™Ã¢â‚¬ ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â£ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬Â¹ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¯ÃƒÂ£Ã†â€™Ã‚Â©ÃƒÂ£Ã†â€™Ã‚Â³ÃƒÂ¥Ã¢â‚¬Å“Ã‚Â¡ÃƒÂ£Ã¯Â¿Â½Ã‚Â®ÃƒÂ£Ã¢â‚¬Å¡Ã¢â‚¬Å¡ÃƒÂ£Ã¯Â¿Â½Ã‚Â®ÃƒÂ£Ã¯Â¿Â½Ã‚Â¯ÃƒÂ¨Ã¢â‚¬Â¡Ã‚ÂªÃƒÂ¥Ã‹â€ Ã¢â‚¬ ÃƒÂ£Ã¯Â¿Â½Ã‚Â«ÃƒÂ¥Ã… Ã‚Â¹ÃƒÂ¦Ã…Â¾Ã…â€œÃƒÂ£Ã¯Â¿Â½Ã…â€™ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬Å¡ÃƒÂ£Ã¢â‚¬Å¡Ã¢â‚¬Â¹ÃƒÂ£Ã¢â€šÂ¬Ã¢â‚¬Å¡ÃƒÂ¯Ã‚Â¼Ã‹â€ ÃƒÂ£Ã¯Â¿Â½Ã…Â¸ÃƒÂ£Ã¯Â¿Â½Ã‚ ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬â€�ÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ£Ã†â€™Ã¢â‚¬â„¢ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã†â€™Ã‚Â«ÃƒÂ£Ã¢â‚¬Å¡Ã‚ÂªÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã†â€™Ã‚Â«ÃƒÂ£Ã¯Â¿Â½Ã‚Â¯ÃƒÂ©Ã¢â€žÂ¢Ã‚Â¤ÃƒÂ¥Ã‚Â¤Ã¢â‚¬â€œÃƒÂ¯Ã‚Â¼Ã¢â‚¬Â°
        }

        // ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¹ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â­ÃƒÂ£Ã†â€™Ã‚Â«ÃƒÂ¤Ã‚Â½Ã‚Â¿ÃƒÂ§Ã¢â‚¬ï¿½Ã‚Â¨ÃƒÂ¨Ã¢â€šÂ¬Ã¢â‚¬Â¦ÃƒÂ£Ã¯Â¿Â½Ã…â€™PCÃƒÂ£Ã¯Â¿Â½Ã‚Â§ÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½PKÃƒÂ£Ã†â€™Ã‚Â¢ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã†â€™Ã¢â‚¬Â°ÃƒÂ£Ã¯Â¿Â½Ã‚Â§ÃƒÂ£Ã¯Â¿Â½Ã‚Â¯ÃƒÂ£Ã¯Â¿Â½Ã‚ÂªÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬Å¾ÃƒÂ¥Ã‚ Ã‚Â´ÃƒÂ¥Ã¯Â¿Â½Ã‹â€ ÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ¨Ã¢â‚¬Â¡Ã‚ÂªÃƒÂ¥Ã‹â€ Ã¢â‚¬ ÃƒÂ£Ã¯Â¿Â½Ã‚Â®ÃƒÂ£Ã¢â‚¬Å¡Ã‚ÂµÃƒÂ£Ã†â€™Ã‚Â¢ÃƒÂ£Ã†â€™Ã‚Â³ÃƒÂ£Ã†â€™Ã‚Â»ÃƒÂ£Ã†â€™Ã…Â¡ÃƒÂ£Ã†â€™Ã†â€™ÃƒÂ£Ã†â€™Ã‹â€ ÃƒÂ£Ã¯Â¿Â½Ã‚Â¯ÃƒÂ¥Ã‚Â¯Ã‚Â¾ÃƒÂ¨Ã‚Â±Ã‚Â¡ÃƒÂ¥Ã‚Â¤Ã¢â‚¬â€œ
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
            // ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â«ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¦ÃƒÂ£Ã†â€™Ã‚Â³ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¿ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã†â€™Ã¢â‚¬Â¡ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â£ÃƒÂ£Ã†â€™Ã¢â‚¬ ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¯ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â·ÃƒÂ£Ã†â€™Ã‚Â§ÃƒÂ£Ã†â€™Ã‚Â³
            if ((_skillId == COUNTER_DETECTION) && (enemy.getZoneType() != 1)
                    && (cha.hasSkillEffect(INVISIBILITY) || cha.hasSkillEffect(BLIND_HIDING))) {
                return true; // ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¤ÃƒÂ£Ã†â€™Ã‚Â³ÃƒÂ£Ã†â€™Ã¢â‚¬Å“ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¸ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬Â¹ÃƒÂ£Ã†â€™Ã¢â‚¬â€œÃƒÂ£Ã†â€™Ã‚Â©ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¤ÃƒÂ£Ã†â€™Ã‚Â³ÃƒÂ£Ã†â€™Ã¢â‚¬Â°ÃƒÂ£Ã†â€™Ã¯Â¿Â½ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¤ÃƒÂ£Ã†â€™Ã¢â‚¬Â¡ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â£ÃƒÂ£Ã†â€™Ã‚Â³ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â°ÃƒÂ¤Ã‚Â¸Ã‚Â­
            }
            if ((_skillId == COUNTER_DETECTION) && (enemy.getZoneType() != 1)
                    && !(cha.hasSkillEffect(INVISIBILITY) || cha.hasSkillEffect(BLIND_HIDING))) {
                return false; // added to try to fix CD
            }
            if ((_player.getClanid() != 0) && (enemy.getClanid() != 0)) { // ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¯ÃƒÂ£Ã†â€™Ã‚Â©ÃƒÂ£Ã†â€™Ã‚Â³ÃƒÂ¦Ã¢â‚¬Â°Ã¢â€šÂ¬ÃƒÂ¥Ã‚Â±Ã…Â¾ÃƒÂ¤Ã‚Â¸Ã‚Â­
                // ÃƒÂ¥Ã¢â‚¬Â¦Ã‚Â¨ÃƒÂ¦Ã‹â€ Ã‚Â¦ÃƒÂ¤Ã‚ÂºÃ¢â‚¬Â°ÃƒÂ£Ã†â€™Ã‚ÂªÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¹ÃƒÂ£Ã†â€™Ã‹â€ ÃƒÂ£Ã¢â‚¬Å¡Ã¢â‚¬â„¢ÃƒÂ¥Ã¯Â¿Â½Ã¢â‚¬â€œÃƒÂ¥Ã‚Â¾Ã¢â‚¬â€�
                for (L1War war : L1World.getInstance().getWarList()) {
                    if (war.CheckClanInWar(_player.getClanname())) { // ÃƒÂ¨Ã¢â‚¬Â¡Ã‚ÂªÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¯ÃƒÂ£Ã†â€™Ã‚Â©ÃƒÂ£Ã†â€™Ã‚Â³ÃƒÂ£Ã¯Â¿Â½Ã…â€™ÃƒÂ¦Ã‹â€ Ã‚Â¦ÃƒÂ¤Ã‚ÂºÃ¢â‚¬Â°ÃƒÂ£Ã¯Â¿Â½Ã‚Â«ÃƒÂ¥Ã¯Â¿Â½Ã¢â‚¬Å¡ÃƒÂ¥Ã… Ã‚ ÃƒÂ¤Ã‚Â¸Ã‚Â­
                        if (war.CheckClanInSameWar( // ÃƒÂ¥Ã¯Â¿Â½Ã…â€™ÃƒÂ£Ã¯Â¿Â½Ã‹Å“ÃƒÂ¦Ã‹â€ Ã‚Â¦ÃƒÂ¤Ã‚ÂºÃ¢â‚¬Â°ÃƒÂ£Ã¯Â¿Â½Ã‚Â«ÃƒÂ¥Ã¯Â¿Â½Ã¢â‚¬Å¡ÃƒÂ¥Ã… Ã‚ ÃƒÂ¤Ã‚Â¸Ã‚Â­
                                _player.getClanname(), enemy.getClanname())) {
                            if (L1CastleLocation.checkInAllWarArea(enemy.getX(), enemy.getY(), enemy.getMapId())) {
                                return true;
                            }
                        }
                    }
                }
            }
            return false; // ÃƒÂ¦Ã¢â‚¬ï¿½Ã‚Â»ÃƒÂ¦Ã¢â‚¬â„¢Ã†â€™ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¹ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â­ÃƒÂ£Ã†â€™Ã‚Â«ÃƒÂ£Ã¯Â¿Â½Ã‚Â§PKÃƒÂ£Ã†â€™Ã‚Â¢ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã†â€™Ã¢â‚¬Â°ÃƒÂ£Ã¯Â¿Â½Ã‹Å“ÃƒÂ£Ã¢â‚¬Å¡Ã†â€™ÃƒÂ£Ã¯Â¿Â½Ã‚ÂªÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬Å¾ÃƒÂ¥Ã‚ Ã‚Â´ÃƒÂ¥Ã¯Â¿Â½Ã‹â€
        }

        if ((_user.glanceCheck(cha.getX(), cha.getY()) == false) && (_skill.isThrough() == false)) {
            // ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¨ÃƒÂ£Ã†â€™Ã‚Â³ÃƒÂ£Ã†â€™Ã¯Â¿Â½ÃƒÂ£Ã†â€™Ã‚Â£ÃƒÂ£Ã†â€™Ã‚Â³ÃƒÂ£Ã†â€™Ã‹â€ ÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ¥Ã‚Â¾Ã‚Â©ÃƒÂ¦Ã‚Â´Ã‚Â»ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¹ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â­ÃƒÂ£Ã†â€™Ã‚Â«ÃƒÂ£Ã¯Â¿Â½Ã‚Â¯ÃƒÂ©Ã…Â¡Ã…â€œÃƒÂ¥Ã‚Â®Ã‚Â³ÃƒÂ§Ã¢â‚¬Â°Ã‚Â©ÃƒÂ£Ã¯Â¿Â½Ã‚Â®ÃƒÂ¥Ã‹â€ Ã‚Â¤ÃƒÂ¥Ã‚Â®Ã…Â¡ÃƒÂ£Ã¢â‚¬Å¡Ã¢â‚¬â„¢ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬â€�ÃƒÂ£Ã¯Â¿Â½Ã‚ÂªÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬Å¾
            if (!((_skill.getType() == L1Skills.TYPE_CHANGE) || (_skill.getType() == L1Skills.TYPE_RESTORE))) {
                _isGlanceCheckFail = true;
                return false; // ÃƒÂ§Ã¢â‚¬ÂºÃ‚Â´ÃƒÂ§Ã‚Â·Ã…Â¡ÃƒÂ¤Ã‚Â¸Ã… ÃƒÂ£Ã¯Â¿Â½Ã‚Â«ÃƒÂ©Ã…Â¡Ã…â€œÃƒÂ¥Ã‚Â®Ã‚Â³ÃƒÂ§Ã¢â‚¬Â°Ã‚Â©ÃƒÂ£Ã¯Â¿Â½Ã…â€™ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬Å¡ÃƒÂ£Ã¢â‚¬Å¡Ã¢â‚¬Â¹
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
                        return false; // ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¢ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¤ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¹ÃƒÂ£Ã†â€™Ã‚Â©ÃƒÂ£Ã†â€™Ã‚Â³ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¹ÃƒÂ¤Ã‚Â¸Ã‚Â­ÃƒÂ£Ã¯Â¿Â½Ã‚Â«ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¢ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¤ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¹ÃƒÂ£Ã†â€™Ã‚Â©ÃƒÂ£Ã†â€™Ã‚Â³ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¹ÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ£Ã†â€™Ã¢â‚¬Â¢ÃƒÂ£Ã†â€™Ã‚ÂªÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¸ÃƒÂ£Ã†â€™Ã‚Â³ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â°ÃƒÂ£Ã†â€™Ã¢â‚¬â€œÃƒÂ£Ã†â€™Ã‚ÂªÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¶ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã†â€™Ã¢â‚¬Â°ÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ£Ã†â€™Ã¢â‚¬Â¢ÃƒÂ£Ã†â€™Ã‚ÂªÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¸ÃƒÂ£Ã†â€™Ã‚Â³ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â°ÃƒÂ£Ã†â€™Ã¢â‚¬â€œÃƒÂ£Ã†â€™Ã‚Â¬ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¹
                }

                if (cha.hasSkillEffect(FREEZING_BLIZZARD) && ((_skillId == ICE_LANCE) || (_skillId == FREEZING_BLIZZARD) || (_skillId == FREEZING_BREATH))) {
                        return false; // ÃƒÂ£Ã†â€™Ã¢â‚¬Â¢ÃƒÂ£Ã†â€™Ã‚ÂªÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¸ÃƒÂ£Ã†â€™Ã‚Â³ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â°ÃƒÂ£Ã†â€™Ã¢â‚¬â€œÃƒÂ£Ã†â€™Ã‚ÂªÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¶ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã†â€™Ã¢â‚¬Â°ÃƒÂ¤Ã‚Â¸Ã‚Â­ÃƒÂ£Ã¯Â¿Â½Ã‚Â«ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¢ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¤ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¹ÃƒÂ£Ã†â€™Ã‚Â©ÃƒÂ£Ã†â€™Ã‚Â³ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¹ÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ£Ã†â€™Ã¢â‚¬Â¢ÃƒÂ£Ã†â€™Ã‚ÂªÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¸ÃƒÂ£Ã†â€™Ã‚Â³ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â°ÃƒÂ£Ã†â€™Ã¢â‚¬â€œÃƒÂ£Ã†â€™Ã‚ÂªÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¶ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã†â€™Ã¢â‚¬Â°ÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ£Ã†â€™Ã¢â‚¬Â¢ÃƒÂ£Ã†â€™Ã‚ÂªÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¸ÃƒÂ£Ã†â€™Ã‚Â³ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â°ÃƒÂ£Ã†â€™Ã¢â‚¬â€œÃƒÂ£Ã†â€™Ã‚Â¬ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¹
                }

                if (cha.hasSkillEffect(FREEZING_BREATH) && ((_skillId == ICE_LANCE) || (_skillId == FREEZING_BLIZZARD) || (_skillId == FREEZING_BREATH))) {
                        return false; // ÃƒÂ£Ã†â€™Ã¢â‚¬Â¢ÃƒÂ£Ã†â€™Ã‚ÂªÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¸ÃƒÂ£Ã†â€™Ã‚Â³ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â°ÃƒÂ£Ã†â€™Ã¢â‚¬â€œÃƒÂ£Ã†â€™Ã‚Â¬ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¹ÃƒÂ¤Ã‚Â¸Ã‚Â­ÃƒÂ£Ã¯Â¿Â½Ã‚Â«ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¢ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¤ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¹ÃƒÂ£Ã†â€™Ã‚Â©ÃƒÂ£Ã†â€™Ã‚Â³ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¹ÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ£Ã†â€™Ã¢â‚¬Â¢ÃƒÂ£Ã†â€™Ã‚ÂªÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¸ÃƒÂ£Ã†â€™Ã‚Â³ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â°ÃƒÂ£Ã†â€™Ã¢â‚¬â€œÃƒÂ£Ã†â€™Ã‚ÂªÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¶ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã†â€™Ã¢â‚¬Â°ÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ£Ã†â€™Ã¢â‚¬Â¢ÃƒÂ£Ã†â€™Ã‚ÂªÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¸ÃƒÂ£Ã†â€™Ã‚Â³ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â°ÃƒÂ£Ã†â€™Ã¢â‚¬â€œÃƒÂ£Ã†â€™Ã‚Â¬ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¹
                }
*/
        if (cha.hasSkillEffect(EARTH_BIND) && (_skillId == EARTH_BIND)) {
            return false; // ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¢ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¹ ÃƒÂ£Ã†â€™Ã¯Â¿Â½ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¤ÃƒÂ£Ã†â€™Ã‚Â³ÃƒÂ£Ã†â€™Ã¢â‚¬Â°ÃƒÂ¤Ã‚Â¸Ã‚Â­ÃƒÂ£Ã¯Â¿Â½Ã‚Â«ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¢ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¹ ÃƒÂ£Ã†â€™Ã¯Â¿Â½ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¤ÃƒÂ£Ã†â€™Ã‚Â³ÃƒÂ£Ã†â€™Ã¢â‚¬Â°
        }

        if (!(cha instanceof L1MonsterInstance) && ((_skillId == TAMING_MONSTER) || (_skillId == CREATE_ZOMBIE))) {
            return false; // ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¿ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â²ÃƒÂ£Ã†â€™Ã†â€™ÃƒÂ£Ã†â€™Ã‹â€ ÃƒÂ£Ã¯Â¿Â½Ã…â€™ÃƒÂ£Ã†â€™Ã‚Â¢ÃƒÂ£Ã†â€™Ã‚Â³ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¹ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¿ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã¯Â¿Â½Ã‹Å“ÃƒÂ£Ã¢â‚¬Å¡Ã†â€™ÃƒÂ£Ã¯Â¿Â½Ã‚ÂªÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬Å¾ÃƒÂ¯Ã‚Â¼Ã‹â€ ÃƒÂ£Ã†â€™Ã¢â‚¬ ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¤ÃƒÂ£Ã†â€™Ã…Â¸ÃƒÂ£Ã†â€™Ã‚Â³ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â°ÃƒÂ£Ã†â€™Ã‚Â¢ÃƒÂ£Ã†â€™Ã‚Â³ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¹ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¿ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ¯Ã‚Â¼Ã¢â‚¬Â°
        }
        if (cha.isDead()
                && ((_skillId != CREATE_ZOMBIE) && (_skillId != RESURRECTION) && (_skillId != GREATER_RESURRECTION) && (_skillId != CALL_OF_NATURE))) {
            return false; // ÃƒÂ§Ã¢â‚¬ÂºÃ‚Â®ÃƒÂ¦Ã‚Â¨Ã¢â€žÂ¢ÃƒÂ¥Ã‚Â·Ã‚Â²ÃƒÂ¦Ã‚Â­Ã‚Â»ÃƒÂ¤Ã‚ÂºÃ‚Â¡ ÃƒÂ¦Ã‚Â³Ã¢â‚¬Â¢ÃƒÂ¨Ã‚Â¡Ã¢â‚¬Å“ÃƒÂ©Ã¯Â¿Â½Ã…Â¾ÃƒÂ¥Ã‚Â¾Ã‚Â©ÃƒÂ¦Ã‚Â´Ã‚Â»ÃƒÂ©Ã‚Â¡Ã…Â¾
        }

        if ((cha.isDead() == false)
                && ((_skillId == CREATE_ZOMBIE) || (_skillId == RESURRECTION) || (_skillId == GREATER_RESURRECTION) || (_skillId == CALL_OF_NATURE))) {
            return false; // ÃƒÂ§Ã¢â‚¬ÂºÃ‚Â®ÃƒÂ¦Ã‚Â¨Ã¢â€žÂ¢ÃƒÂ¦Ã…â€œÃ‚ÂªÃƒÂ¦Ã‚Â­Ã‚Â»ÃƒÂ¤Ã‚ÂºÃ‚Â¡ ÃƒÂ¦Ã‚Â³Ã¢â‚¬Â¢ÃƒÂ¨Ã‚Â¡Ã¢â‚¬Å“ÃƒÂ¥Ã‚Â¾Ã‚Â©ÃƒÂ¦Ã‚Â´Ã‚Â»ÃƒÂ©Ã‚Â¡Ã…Â¾
        }

        if (((cha instanceof L1TowerInstance) || (cha instanceof L1DoorInstance))
                && ((_skillId == CREATE_ZOMBIE) || (_skillId == RESURRECTION) || (_skillId == GREATER_RESURRECTION) || (_skillId == CALL_OF_NATURE))) {
            return false; // ÃƒÂ¥Ã‚Â¡Ã¢â‚¬ï¿½ÃƒÂ¨Ã‚Â·Ã…Â¸ÃƒÂ©Ã¢â‚¬â€œÃ¢â€šÂ¬ÃƒÂ¤Ã‚Â¸Ã¯Â¿Â½ÃƒÂ¥Ã¯Â¿Â½Ã‚Â¯ÃƒÂ¦Ã¢â‚¬ï¿½Ã‚Â¾ÃƒÂ¥Ã‚Â¾Ã‚Â©ÃƒÂ¦Ã‚Â´Ã‚Â»ÃƒÂ¦Ã‚Â³Ã¢â‚¬Â¢ÃƒÂ¨Ã‚Â¡Ã¢â‚¬Å“
        }

        if (cha instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) cha;
            if (pc.hasSkillEffect(ABSOLUTE_BARRIER)) { // ÃƒÂ§Ã‚ÂµÃ¢â‚¬Â¢ÃƒÂ¥Ã‚Â°Ã¯Â¿Â½ÃƒÂ¥Ã‚Â±Ã¯Â¿Â½ÃƒÂ©Ã…Â¡Ã…â€œÃƒÂ§Ã¢â‚¬Â¹Ã¢â€šÂ¬ÃƒÂ¦Ã¢â‚¬Â¦Ã¢â‚¬Â¹ÃƒÂ¤Ã‚Â¸Ã‚Â­
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
                if ((_skillId == DETECTION) || (_skillId == COUNTER_DETECTION)) { // ÃƒÂ£Ã†â€™Ã¢â‚¬Â¡ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â£ÃƒÂ£Ã†â€™Ã¢â‚¬ ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¯ÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½CÃƒÂ£Ã†â€™Ã¢â‚¬Â¡ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â£ÃƒÂ£Ã†â€™Ã¢â‚¬ ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¯
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

        if (((_skill.getTargetTo() & L1Skills.TARGET_TO_PC) == L1Skills.TARGET_TO_PC // ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¿ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â²ÃƒÂ£Ã†â€™Ã†â€™ÃƒÂ£Ã†â€™Ã‹â€ ÃƒÂ£Ã¯Â¿Â½Ã…â€™PC
        )
                && (cha instanceof L1PcInstance)) {
            _flg = true;
        }
        else if (((_skill.getTargetTo() & L1Skills.TARGET_TO_NPC) == L1Skills.TARGET_TO_NPC // ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¿ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â²ÃƒÂ£Ã†â€™Ã†â€™ÃƒÂ£Ã†â€™Ã‹â€ ÃƒÂ£Ã¯Â¿Â½Ã…â€™NPC
        )
                && ((cha instanceof L1MonsterInstance) || (cha instanceof L1NpcInstance) || (cha instanceof L1SummonInstance) || (cha instanceof L1PetInstance))) {
            _flg = true;
        }
        else if (((_skill.getTargetTo() & L1Skills.TARGET_TO_PET) == L1Skills.TARGET_TO_PET) && (_user instanceof L1PcInstance)) { // ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¿ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â²ÃƒÂ£Ã†â€™Ã†â€™ÃƒÂ£Ã†â€™Ã‹â€ ÃƒÂ£Ã¯Â¿Â½Ã…â€™Summon,Pet
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
            if (((_skill.getTargetTo() & L1Skills.TARGET_TO_CLAN) == L1Skills.TARGET_TO_CLAN) && (((_player.getClanid() != 0 // ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¿ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â²ÃƒÂ£Ã†â€™Ã†â€™ÃƒÂ£Ã†â€™Ã‹â€ ÃƒÂ£Ã¯Â¿Â½Ã…â€™ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¯ÃƒÂ£Ã†â€™Ã‚Â©ÃƒÂ£Ã†â€™Ã‚Â³ÃƒÂ¥Ã¢â‚¬Å“Ã‚Â¡
            ) && (_player.getClanid() == ((L1PcInstance) cha).getClanid())) || _player.isGm())) {
                return true;
            }
            if (((_skill.getTargetTo() & L1Skills.TARGET_TO_PARTY) == L1Skills.TARGET_TO_PARTY) && (_player.getParty() // ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¿ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â²ÃƒÂ£Ã†â€™Ã†â€™ÃƒÂ£Ã†â€™Ã‹â€ ÃƒÂ£Ã¯Â¿Â½Ã…â€™ÃƒÂ£Ã†â€™Ã¢â‚¬ËœÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã†â€™Ã¢â‚¬ ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â£ÃƒÂ£Ã†â€™Ã‚Â¼
                    .isMember((L1PcInstance) cha) || _player.isGm())) {
                return true;
            }
        }

        return _flg;
    }

    // ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¿ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â²ÃƒÂ£Ã†â€™Ã†â€™ÃƒÂ£Ã†â€™Ã‹â€ ÃƒÂ£Ã¯Â¿Â½Ã‚Â®ÃƒÂ¤Ã‚Â¸Ã¢â€šÂ¬ÃƒÂ¨Ã‚Â¦Ã‚Â§ÃƒÂ£Ã¢â‚¬Å¡Ã¢â‚¬â„¢ÃƒÂ¤Ã‚Â½Ã…â€œÃƒÂ¦Ã‹â€ Ã¯Â¿Â½
    private void makeTargetList() {
        try {
            if (_type == TYPE_LOGIN) { // ÃƒÂ£Ã†â€™Ã‚Â­ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â°ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¤ÃƒÂ£Ã†â€™Ã‚Â³ÃƒÂ¦Ã¢â€žÂ¢Ã¢â‚¬Å¡(ÃƒÂ¦Ã‚Â­Ã‚Â»ÃƒÂ¤Ã‚ÂºÃ‚Â¡ÃƒÂ¦Ã¢â€žÂ¢Ã¢â‚¬Å¡ÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ£Ã¯Â¿Â½Ã… ÃƒÂ¥Ã…â€™Ã¢â‚¬â€œÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬ËœÃƒÂ¥Ã‚Â±Ã¢â‚¬Â¹ÃƒÂ¦Ã¢â‚¬Â¢Ã‚Â·ÃƒÂ£Ã¯Â¿Â½Ã‚Â®ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â­ÃƒÂ£Ã†â€™Ã‚Â£ÃƒÂ£Ã†â€™Ã‚Â³ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â»ÃƒÂ£Ã†â€™Ã‚Â¬ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â·ÃƒÂ£Ã†â€™Ã‚Â§ÃƒÂ£Ã†â€™Ã‚Â³ÃƒÂ¥Ã¯Â¿Â½Ã‚Â«ÃƒÂ£Ã¢â‚¬Å¡Ã¢â€šÂ¬)ÃƒÂ£Ã¯Â¿Â½Ã‚Â¯ÃƒÂ¤Ã‚Â½Ã‚Â¿ÃƒÂ§Ã¢â‚¬ï¿½Ã‚Â¨ÃƒÂ¨Ã¢â€šÂ¬Ã¢â‚¬Â¦ÃƒÂ£Ã¯Â¿Â½Ã‚Â®ÃƒÂ£Ã¯Â¿Â½Ã‚Â¿
                _targetList.add(new TargetStatus(_user));
                return;
            }
            if ((_skill.getTargetTo() == L1Skills.TARGET_TO_ME) && ((_skill.getType() & L1Skills.TYPE_ATTACK) != L1Skills.TYPE_ATTACK)) {
                _targetList.add(new TargetStatus(_user)); // ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¿ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â²ÃƒÂ£Ã†â€™Ã†â€™ÃƒÂ£Ã†â€™Ã‹â€ ÃƒÂ£Ã¯Â¿Â½Ã‚Â¯ÃƒÂ¤Ã‚Â½Ã‚Â¿ÃƒÂ§Ã¢â‚¬ï¿½Ã‚Â¨ÃƒÂ¨Ã¢â€šÂ¬Ã¢â‚¬Â¦ÃƒÂ£Ã¯Â¿Â½Ã‚Â®ÃƒÂ£Ã¯Â¿Â½Ã‚Â¿
                return;
            }

            // ÃƒÂ¥Ã‚Â°Ã¢â‚¬Å¾ÃƒÂ§Ã‚Â¨Ã¢â‚¬Â¹ÃƒÂ¨Ã‚Â·Ã¯Â¿Â½ÃƒÂ©Ã¢â‚¬ÂºÃ‚Â¢-1ÃƒÂ£Ã¯Â¿Â½Ã‚Â®ÃƒÂ¥Ã‚ Ã‚Â´ÃƒÂ¥Ã¯Â¿Â½Ã‹â€ ÃƒÂ£Ã¯Â¿Â½Ã‚Â¯ÃƒÂ§Ã¢â‚¬ï¿½Ã‚Â»ÃƒÂ©Ã¯Â¿Â½Ã‚Â¢ÃƒÂ¥Ã¢â‚¬ Ã¢â‚¬Â¦ÃƒÂ£Ã¯Â¿Â½Ã‚Â®ÃƒÂ£Ã¢â‚¬Å¡Ã‚ÂªÃƒÂ£Ã†â€™Ã¢â‚¬â€œÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¸ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â§ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¯ÃƒÂ£Ã†â€™Ã‹â€ ÃƒÂ£Ã¯Â¿Â½Ã…â€™ÃƒÂ¥Ã‚Â¯Ã‚Â¾ÃƒÂ¨Ã‚Â±Ã‚Â¡
            if (getSkillRanged() != -1) {
                if (_user.getLocation().getTileLineDistance(_target.getLocation()) > getSkillRanged()) {
                    return; // ÃƒÂ¥Ã‚Â°Ã¢â‚¬Å¾ÃƒÂ§Ã‚Â¨Ã¢â‚¬Â¹ÃƒÂ§Ã‚Â¯Ã¢â‚¬Å¾ÃƒÂ¥Ã¢â‚¬ÂºÃ‚Â²ÃƒÂ¥Ã‚Â¤Ã¢â‚¬â€œ
                }
            }
            else {
                if (!_user.getLocation().isInScreen(_target.getLocation())) {
                    return; // ÃƒÂ¥Ã‚Â°Ã¢â‚¬Å¾ÃƒÂ§Ã‚Â¨Ã¢â‚¬Â¹ÃƒÂ§Ã‚Â¯Ã¢â‚¬Å¾ÃƒÂ¥Ã¢â‚¬ÂºÃ‚Â²ÃƒÂ¥Ã‚Â¤Ã¢â‚¬â€œ
                }
            }

            if ((isTarget(_target) == false) && !(_skill.getTarget().equals("none"))) {
                // ÃƒÂ¥Ã‚Â¯Ã‚Â¾ÃƒÂ¨Ã‚Â±Ã‚Â¡ÃƒÂ£Ã¯Â¿Â½Ã…â€™ÃƒÂ©Ã¯Â¿Â½Ã¢â‚¬Â¢ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬ ÃƒÂ£Ã¯Â¿Â½Ã‚Â®ÃƒÂ£Ã¯Â¿Â½Ã‚Â§ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¹ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â­ÃƒÂ£Ã†â€™Ã‚Â«ÃƒÂ£Ã¯Â¿Â½Ã…â€™ÃƒÂ§Ã¢â€žÂ¢Ã‚ÂºÃƒÂ¥Ã¢â‚¬Â¹Ã¢â‚¬Â¢ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬â€�ÃƒÂ£Ã¯Â¿Â½Ã‚ÂªÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬Å¾ÃƒÂ£Ã¢â€šÂ¬Ã¢â‚¬Å¡
                return;
            }

            if ((_skillId == LIGHTNING) || (_skillId == FREEZING_BREATH)) { // ÃƒÂ£Ã†â€™Ã‚Â©ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¤ÃƒÂ£Ã†â€™Ã‹â€ ÃƒÂ£Ã†â€™Ã¢â‚¬Â¹ÃƒÂ£Ã†â€™Ã‚Â³ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â°ÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ£Ã†â€™Ã¢â‚¬Â¢ÃƒÂ£Ã†â€™Ã‚ÂªÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¸ÃƒÂ£Ã†â€™Ã‚Â³ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â°ÃƒÂ£Ã†â€™Ã¢â‚¬â€œÃƒÂ£Ã†â€™Ã‚Â¬ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¹ÃƒÂ§Ã¢â‚¬ÂºÃ‚Â´ÃƒÂ§Ã‚Â·Ã…Â¡ÃƒÂ§Ã…Â¡Ã¢â‚¬Å¾ÃƒÂ£Ã¯Â¿Â½Ã‚Â«ÃƒÂ§Ã‚Â¯Ã¢â‚¬Å¾ÃƒÂ¥Ã¢â‚¬ÂºÃ‚Â²ÃƒÂ£Ã¢â‚¬Å¡Ã¢â‚¬â„¢ÃƒÂ¦Ã‚Â±Ã‚ÂºÃƒÂ£Ã¢â‚¬Å¡Ã¯Â¿Â½ÃƒÂ£Ã¢â‚¬Å¡Ã¢â‚¬Â¹
                List<L1Object> al1object = L1World.getInstance().getVisibleLineObjects(_user, _target);

                for (L1Object tgobj : al1object) {
                    if (tgobj == null) {
                        continue;
                    }
                    if (!(tgobj instanceof L1Character)) { // ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¿ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â²ÃƒÂ£Ã†â€™Ã†â€™ÃƒÂ£Ã†â€™Ã‹â€ ÃƒÂ£Ã¯Â¿Â½Ã…â€™ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â­ÃƒÂ£Ã†â€™Ã‚Â£ÃƒÂ£Ã†â€™Ã‚Â©ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¯ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¿ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ¤Ã‚Â»Ã‚Â¥ÃƒÂ¥Ã‚Â¤Ã¢â‚¬â€œÃƒÂ£Ã¯Â¿Â½Ã‚Â®ÃƒÂ¥Ã‚ Ã‚Â´ÃƒÂ¥Ã¯Â¿Â½Ã‹â€ ÃƒÂ¤Ã‚Â½Ã¢â‚¬Â¢ÃƒÂ£Ã¢â‚¬Å¡Ã¢â‚¬Å¡ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬â€�ÃƒÂ£Ã¯Â¿Â½Ã‚ÂªÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬Å¾ÃƒÂ£Ã¢â€šÂ¬Ã¢â‚¬Å¡
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

            if (getSkillArea() == 0) { // ÃƒÂ¥Ã¯Â¿Â½Ã‹Å“ÃƒÂ¤Ã‚Â½Ã¢â‚¬Å“ÃƒÂ£Ã¯Â¿Â½Ã‚Â®ÃƒÂ¥Ã‚ Ã‚Â´ÃƒÂ¥Ã¯Â¿Â½Ã‹â€
                if (!_user.glanceCheck(_target.getX(), _target.getY())) { // ÃƒÂ§Ã¢â‚¬ÂºÃ‚Â´ÃƒÂ§Ã‚Â·Ã…Â¡ÃƒÂ¤Ã‚Â¸Ã… ÃƒÂ£Ã¯Â¿Â½Ã‚Â«ÃƒÂ©Ã…Â¡Ã…â€œÃƒÂ¥Ã‚Â®Ã‚Â³ÃƒÂ§Ã¢â‚¬Â°Ã‚Â©ÃƒÂ£Ã¯Â¿Â½Ã…â€™ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬Å¡ÃƒÂ£Ã¢â‚¬Å¡Ã¢â‚¬Â¹ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬Â¹
                    if (((_skill.getType() & L1Skills.TYPE_ATTACK) == L1Skills.TYPE_ATTACK) && (_skillId != 10026) && (_skillId != 10027)
                            && (_skillId != 10028) && (_skillId != 10029)) { // ÃƒÂ¥Ã‚Â®Ã¢â‚¬Â°ÃƒÂ¦Ã¯Â¿Â½Ã‚Â¯ÃƒÂ¦Ã¢â‚¬ï¿½Ã‚Â»ÃƒÂ¦Ã¢â‚¬â„¢Ã†â€™ÃƒÂ¤Ã‚Â»Ã‚Â¥ÃƒÂ¥Ã‚Â¤Ã¢â‚¬â€œÃƒÂ£Ã¯Â¿Â½Ã‚Â®ÃƒÂ¦Ã¢â‚¬ï¿½Ã‚Â»ÃƒÂ¦Ã¢â‚¬â„¢Ã†â€™ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¹ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â­ÃƒÂ£Ã†â€™Ã‚Â«
                        _targetList.add(new TargetStatus(_target, false)); // ÃƒÂ£Ã†â€™Ã¢â€šÂ¬ÃƒÂ£Ã†â€™Ã‚Â¡ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¸ÃƒÂ£Ã¢â‚¬Å¡Ã¢â‚¬Å¡ÃƒÂ§Ã¢â€žÂ¢Ã‚ÂºÃƒÂ§Ã¢â‚¬ï¿½Ã…Â¸ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬â€�ÃƒÂ£Ã¯Â¿Â½Ã‚ÂªÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬Å¾ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬â€�ÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ£Ã†â€™Ã¢â€šÂ¬ÃƒÂ£Ã†â€™Ã‚Â¡ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¸ÃƒÂ£Ã†â€™Ã‚Â¢ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â·ÃƒÂ£Ã†â€™Ã‚Â§ÃƒÂ£Ã†â€™Ã‚Â³ÃƒÂ£Ã¢â‚¬Å¡Ã¢â‚¬Å¡ÃƒÂ§Ã¢â€žÂ¢Ã‚ÂºÃƒÂ§Ã¢â‚¬ï¿½Ã…Â¸ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬â€�ÃƒÂ£Ã¯Â¿Â½Ã‚ÂªÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬Å¾ÃƒÂ£Ã¯Â¿Â½Ã…â€™ÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¹ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â­ÃƒÂ£Ã†â€™Ã‚Â«ÃƒÂ£Ã¯Â¿Â½Ã‚Â¯ÃƒÂ§Ã¢â€žÂ¢Ã‚ÂºÃƒÂ¥Ã¢â‚¬Â¹Ã¢â‚¬Â¢
                        return;
                    }
                }
                _targetList.add(new TargetStatus(_target));
            }
            else { // ÃƒÂ§Ã‚Â¯Ã¢â‚¬Å¾ÃƒÂ¥Ã¢â‚¬ÂºÃ‚Â²ÃƒÂ£Ã¯Â¿Â½Ã‚Â®ÃƒÂ¥Ã‚ Ã‚Â´ÃƒÂ¥Ã¯Â¿Â½Ã‹â€
                if (!_skill.getTarget().equals("none")) {
                    _targetList.add(new TargetStatus(_target));
                }

                if ((_skillId != 49) && !(_skill.getTarget().equals("attack") || (_skill.getType() == L1Skills.TYPE_ATTACK))) {
                    // ÃƒÂ¦Ã¢â‚¬ï¿½Ã‚Â»ÃƒÂ¦Ã¢â‚¬â„¢Ã†â€™ÃƒÂ§Ã‚Â³Ã‚Â»ÃƒÂ¤Ã‚Â»Ã‚Â¥ÃƒÂ¥Ã‚Â¤Ã¢â‚¬â€œÃƒÂ£Ã¯Â¿Â½Ã‚Â®ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¹ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â­ÃƒÂ£Ã†â€™Ã‚Â«ÃƒÂ£Ã¯Â¿Â½Ã‚Â¨H-AÃƒÂ¤Ã‚Â»Ã‚Â¥ÃƒÂ¥Ã‚Â¤Ã¢â‚¬â€œÃƒÂ£Ã¯Â¿Â½Ã‚Â¯ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¿ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â²ÃƒÂ£Ã†â€™Ã†â€™ÃƒÂ£Ã†â€™Ã‹â€ ÃƒÂ¨Ã¢â‚¬Â¡Ã‚ÂªÃƒÂ¨Ã‚ÂºÃ‚Â«ÃƒÂ£Ã¢â‚¬Å¡Ã¢â‚¬â„¢ÃƒÂ¥Ã¯Â¿Â½Ã‚Â«ÃƒÂ£Ã¢â‚¬Å¡Ã¯Â¿Â½ÃƒÂ£Ã¢â‚¬Å¡Ã¢â‚¬Â¹
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
                    if (!(tgobj instanceof L1Character)) { // ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¿ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â²ÃƒÂ£Ã†â€™Ã†â€™ÃƒÂ£Ã†â€™Ã‹â€ ÃƒÂ£Ã¯Â¿Â½Ã…â€™ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â­ÃƒÂ£Ã†â€™Ã‚Â£ÃƒÂ£Ã†â€™Ã‚Â©ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¯ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¿ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ¤Ã‚Â»Ã‚Â¥ÃƒÂ¥Ã‚Â¤Ã¢â‚¬â€œÃƒÂ£Ã¯Â¿Â½Ã‚Â®ÃƒÂ¥Ã‚ Ã‚Â´ÃƒÂ¥Ã¯Â¿Â½Ã‹â€ ÃƒÂ¤Ã‚Â½Ã¢â‚¬Â¢ÃƒÂ£Ã¢â‚¬Å¡Ã¢â‚¬Å¡ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬â€�ÃƒÂ£Ã¯Â¿Â½Ã‚ÂªÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬Å¾ÃƒÂ£Ã¢â€šÂ¬Ã¢â‚¬Å¡
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

    // ÃƒÂ£Ã†â€™Ã‚Â¡ÃƒÂ£Ã†â€™Ã†â€™ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â»ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¸ÃƒÂ£Ã¯Â¿Â½Ã‚Â®ÃƒÂ¨Ã‚Â¡Ã‚Â¨ÃƒÂ§Ã‚Â¤Ã‚ÂºÃƒÂ¯Ã‚Â¼Ã‹â€ ÃƒÂ¤Ã‚Â½Ã¢â‚¬Â¢ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬Â¹ÃƒÂ¨Ã‚ÂµÃ‚Â·ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬Å“ÃƒÂ£Ã¯Â¿Â½Ã‚Â£ÃƒÂ£Ã¯Â¿Â½Ã…Â¸ÃƒÂ£Ã¯Â¿Â½Ã‚Â¨ÃƒÂ£Ã¯Â¿Â½Ã¯Â¿Â½ÃƒÂ¯Ã‚Â¼Ã¢â‚¬Â°
    private void sendHappenMessage(L1PcInstance pc) {
        int msgID = _skill.getSysmsgIdHappen();
        if (msgID > 0) {
            // ÃƒÂ¦Ã¢â‚¬Â¢Ã‹â€ ÃƒÂ¦Ã…Â¾Ã…â€œÃƒÂ¨Ã‚Â¨Ã… ÃƒÂ¦Ã¯Â¿Â½Ã‚Â¯ÃƒÂ¦Ã…Â½Ã¢â‚¬â„¢ÃƒÂ©Ã¢â€žÂ¢Ã‚Â¤ÃƒÂ¦Ã¢â‚¬â€œÃ‚Â½ÃƒÂ¦Ã‚Â³Ã¢â‚¬Â¢ÃƒÂ¨Ã¢â€šÂ¬Ã¢â‚¬Â¦ÃƒÂ¦Ã…â€œÃ‚Â¬ÃƒÂ¨Ã‚ÂºÃ‚Â«ÃƒÂ£Ã¢â€šÂ¬Ã¢â‚¬Å¡
            if (_skillId == AREA_OF_SILENCE && _user.getId() == pc.getId()) {// ÃƒÂ¥Ã‚Â°Ã¯Â¿Â½ÃƒÂ¥Ã¯Â¿Â½Ã‚Â°ÃƒÂ§Ã‚Â¦Ã¯Â¿Â½ÃƒÂ¥Ã…â€œÃ‚Â°
                return;
            }
            pc.sendPackets(new S_ServerMessage(msgID));
        }
    }

    // ÃƒÂ¥Ã‚Â¤Ã‚Â±ÃƒÂ¦Ã¢â‚¬Â¢Ã¢â‚¬â€�ÃƒÂ£Ã†â€™Ã‚Â¡ÃƒÂ£Ã†â€™Ã†â€™ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â»ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¸ÃƒÂ¨Ã‚Â¡Ã‚Â¨ÃƒÂ§Ã‚Â¤Ã‚ÂºÃƒÂ£Ã¯Â¿Â½Ã‚Â®ÃƒÂ£Ã†â€™Ã¯Â¿Â½ÃƒÂ£Ã†â€™Ã‚Â³ÃƒÂ£Ã†â€™Ã¢â‚¬Â°ÃƒÂ£Ã†â€™Ã‚Â«
    private void sendFailMessageHandle() {
        // ÃƒÂ¦Ã¢â‚¬ï¿½Ã‚Â»ÃƒÂ¦Ã¢â‚¬â„¢Ã†â€™ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¹ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â­ÃƒÂ£Ã†â€™Ã‚Â«ÃƒÂ¤Ã‚Â»Ã‚Â¥ÃƒÂ¥Ã‚Â¤Ã¢â‚¬â€œÃƒÂ£Ã¯Â¿Â½Ã‚Â§ÃƒÂ¥Ã‚Â¯Ã‚Â¾ÃƒÂ¨Ã‚Â±Ã‚Â¡ÃƒÂ£Ã¢â‚¬Å¡Ã¢â‚¬â„¢ÃƒÂ¦Ã…â€™Ã¢â‚¬Â¡ÃƒÂ¥Ã‚Â®Ã…Â¡ÃƒÂ£Ã¯Â¿Â½Ã¢â€žÂ¢ÃƒÂ£Ã¢â‚¬Å¡Ã¢â‚¬Â¹ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¹ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â­ÃƒÂ£Ã†â€™Ã‚Â«ÃƒÂ£Ã¯Â¿Â½Ã…â€™ÃƒÂ¥Ã‚Â¤Ã‚Â±ÃƒÂ¦Ã¢â‚¬Â¢Ã¢â‚¬â€�ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬â€�ÃƒÂ£Ã¯Â¿Â½Ã…Â¸ÃƒÂ¥Ã‚ Ã‚Â´ÃƒÂ¥Ã¯Â¿Â½Ã‹â€ ÃƒÂ£Ã¯Â¿Â½Ã‚Â¯ÃƒÂ¥Ã‚Â¤Ã‚Â±ÃƒÂ¦Ã¢â‚¬Â¢Ã¢â‚¬â€�ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬â€�ÃƒÂ£Ã¯Â¿Â½Ã…Â¸ÃƒÂ£Ã†â€™Ã‚Â¡ÃƒÂ£Ã†â€™Ã†â€™ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â»ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¸ÃƒÂ£Ã¢â‚¬Å¡Ã¢â‚¬â„¢ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¯ÃƒÂ£Ã†â€™Ã‚Â©ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¤ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¢ÃƒÂ£Ã†â€™Ã‚Â³ÃƒÂ£Ã†â€™Ã‹â€ ÃƒÂ£Ã¯Â¿Â½Ã‚Â«ÃƒÂ©Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ¤Ã‚Â¿Ã‚Â¡
        // ÃƒÂ¢Ã¢â€šÂ¬Ã‚Â»ÃƒÂ¦Ã¢â‚¬ï¿½Ã‚Â»ÃƒÂ¦Ã¢â‚¬â„¢Ã†â€™ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¹ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â­ÃƒÂ£Ã†â€™Ã‚Â«ÃƒÂ£Ã¯Â¿Â½Ã‚Â¯ÃƒÂ©Ã…Â¡Ã…â€œÃƒÂ¥Ã‚Â®Ã‚Â³ÃƒÂ§Ã¢â‚¬Â°Ã‚Â©ÃƒÂ£Ã¯Â¿Â½Ã…â€™ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬Å¡ÃƒÂ£Ã¯Â¿Â½Ã‚Â£ÃƒÂ£Ã¯Â¿Â½Ã‚Â¦ÃƒÂ£Ã¢â‚¬Å¡Ã¢â‚¬Å¡ÃƒÂ¦Ã‹â€ Ã¯Â¿Â½ÃƒÂ¥Ã… Ã…Â¸ÃƒÂ¦Ã¢â€žÂ¢Ã¢â‚¬Å¡ÃƒÂ£Ã¯Â¿Â½Ã‚Â¨ÃƒÂ¥Ã¯Â¿Â½Ã…â€™ÃƒÂ£Ã¯Â¿Â½Ã‹Å“ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¢ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¯ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â·ÃƒÂ£Ã†â€™Ã‚Â§ÃƒÂ£Ã†â€™Ã‚Â³ÃƒÂ£Ã¯Â¿Â½Ã‚Â§ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬Å¡ÃƒÂ£Ã¢â‚¬Å¡Ã¢â‚¬Â¹ÃƒÂ£Ã¯Â¿Â½Ã‚Â¹ÃƒÂ£Ã¯Â¿Â½Ã¯Â¿Â½ÃƒÂ£Ã¢â€šÂ¬Ã¢â‚¬Å¡
        if ((_skill.getType() != L1Skills.TYPE_ATTACK) && !_skill.getTarget().equals("none") && _targetList.isEmpty()) {
            sendFailMessage();
        }
    }

    // ÃƒÂ£Ã†â€™Ã‚Â¡ÃƒÂ£Ã†â€™Ã†â€™ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â»ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¸ÃƒÂ£Ã¯Â¿Â½Ã‚Â®ÃƒÂ¨Ã‚Â¡Ã‚Â¨ÃƒÂ§Ã‚Â¤Ã‚ÂºÃƒÂ¯Ã‚Â¼Ã‹â€ ÃƒÂ¥Ã‚Â¤Ã‚Â±ÃƒÂ¦Ã¢â‚¬Â¢Ã¢â‚¬â€�ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬â€�ÃƒÂ£Ã¯Â¿Â½Ã…Â¸ÃƒÂ£Ã¯Â¿Â½Ã‚Â¨ÃƒÂ£Ã¯Â¿Â½Ã¯Â¿Â½ÃƒÂ¯Ã‚Â¼Ã¢â‚¬Â°
    private void sendFailMessage() {
        int msgID = _skill.getSysmsgIdFail();
        if ((msgID > 0) && (_user instanceof L1PcInstance)) {
            _player.sendPackets(new S_ServerMessage(msgID));
        }
    }

    // ÃƒÂ§Ã‚Â²Ã‚Â¾ÃƒÂ©Ã…â€œÃ… ÃƒÂ©Ã‚Â­Ã¢â‚¬ï¿½ÃƒÂ¦Ã‚Â³Ã¢â‚¬Â¢ÃƒÂ£Ã¯Â¿Â½Ã‚Â®ÃƒÂ¥Ã‚Â±Ã…Â¾ÃƒÂ¦Ã¢â€šÂ¬Ã‚Â§ÃƒÂ£Ã¯Â¿Â½Ã‚Â¨ÃƒÂ¤Ã‚Â½Ã‚Â¿ÃƒÂ§Ã¢â‚¬ï¿½Ã‚Â¨ÃƒÂ¨Ã¢â€šÂ¬Ã¢â‚¬Â¦ÃƒÂ£Ã¯Â¿Â½Ã‚Â®ÃƒÂ¥Ã‚Â±Ã…Â¾ÃƒÂ¦Ã¢â€šÂ¬Ã‚Â§ÃƒÂ£Ã¯Â¿Â½Ã‚Â¯ÃƒÂ¤Ã‚Â¸Ã¢â€šÂ¬ÃƒÂ¨Ã¢â‚¬Â¡Ã‚Â´ÃƒÂ£Ã¯Â¿Â½Ã¢â€žÂ¢ÃƒÂ£Ã¢â‚¬Å¡Ã¢â‚¬Â¹ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬Â¹ÃƒÂ¯Ã‚Â¼Ã…Â¸ÃƒÂ¯Ã‚Â¼Ã‹â€ ÃƒÂ£Ã¯Â¿Â½Ã‚Â¨ÃƒÂ£Ã¢â‚¬Å¡Ã… ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬Å¡ÃƒÂ£Ã¯Â¿Â½Ã‹â€ ÃƒÂ£Ã¯Â¿Â½Ã…Â¡ÃƒÂ£Ã¯Â¿Â½Ã‚Â®ÃƒÂ¥Ã‚Â¯Ã‚Â¾ÃƒÂ¥Ã¢â‚¬Â¡Ã‚Â¦ÃƒÂ£Ã¯Â¿Â½Ã‚ÂªÃƒÂ£Ã¯Â¿Â½Ã‚Â®ÃƒÂ£Ã¯Â¿Â½Ã‚Â§ÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ¥Ã‚Â¯Ã‚Â¾ÃƒÂ¥Ã‚Â¿Ã…â€œÃƒÂ£Ã¯Â¿Â½Ã‚Â§ÃƒÂ£Ã¯Â¿Â½Ã¯Â¿Â½ÃƒÂ£Ã¯Â¿Â½Ã…Â¸ÃƒÂ£Ã¢â‚¬Å¡Ã¢â‚¬Â°ÃƒÂ¦Ã‚Â¶Ã‹â€ ÃƒÂ¥Ã…Â½Ã‚Â»ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬â€�ÃƒÂ£Ã¯Â¿Â½Ã‚Â¦ÃƒÂ¤Ã‚Â¸Ã¢â‚¬Â¹ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬Â¢ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬Å¾)
    private boolean isAttrAgrees() {
        int magicattr = _skill.getAttr();
        if (_user instanceof L1NpcInstance) { // NPCÃƒÂ£Ã¯Â¿Â½Ã…â€™ÃƒÂ¤Ã‚Â½Ã‚Â¿ÃƒÂ£Ã¯Â¿Â½Ã‚Â£ÃƒÂ£Ã¯Â¿Â½Ã…Â¸ÃƒÂ¥Ã‚ Ã‚Â´ÃƒÂ¥Ã¯Â¿Â½Ã‹â€ ÃƒÂ£Ã¯Â¿Â½Ã‚ÂªÃƒÂ£Ã¢â‚¬Å¡Ã¢â‚¬Å“ÃƒÂ£Ã¯Â¿Â½Ã‚Â§ÃƒÂ£Ã¢â‚¬Å¡Ã¢â‚¬Å¡OK
            return true;
        }

        if ((_skill.getSkillLevel() >= 17) && (_skill.getSkillLevel() <= 22) && (magicattr != 0 // ÃƒÂ§Ã‚Â²Ã‚Â¾ÃƒÂ©Ã…â€œÃ… ÃƒÂ©Ã‚Â­Ã¢â‚¬ï¿½ÃƒÂ¦Ã‚Â³Ã¢â‚¬Â¢ÃƒÂ£Ã¯Â¿Â½Ã‚Â§ÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ§Ã¢â‚¬Å¾Ã‚Â¡ÃƒÂ¥Ã‚Â±Ã…Â¾ÃƒÂ¦Ã¢â€šÂ¬Ã‚Â§ÃƒÂ©Ã‚Â­Ã¢â‚¬ï¿½ÃƒÂ¦Ã‚Â³Ã¢â‚¬Â¢ÃƒÂ£Ã¯Â¿Â½Ã‚Â§ÃƒÂ£Ã¯Â¿Â½Ã‚Â¯ÃƒÂ£Ã¯Â¿Â½Ã‚ÂªÃƒÂ£Ã¯Â¿Â½Ã¯Â¿Â½ÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½
        ) && (magicattr != _player.getElfAttr() // ÃƒÂ¤Ã‚Â½Ã‚Â¿ÃƒÂ§Ã¢â‚¬ï¿½Ã‚Â¨ÃƒÂ¨Ã¢â€šÂ¬Ã¢â‚¬Â¦ÃƒÂ£Ã¯Â¿Â½Ã‚Â¨ÃƒÂ©Ã‚Â­Ã¢â‚¬ï¿½ÃƒÂ¦Ã‚Â³Ã¢â‚¬Â¢ÃƒÂ£Ã¯Â¿Â½Ã‚Â®ÃƒÂ¥Ã‚Â±Ã…Â¾ÃƒÂ¦Ã¢â€šÂ¬Ã‚Â§ÃƒÂ£Ã¯Â¿Â½Ã…â€™ÃƒÂ¤Ã‚Â¸Ã¢â€šÂ¬ÃƒÂ¨Ã¢â‚¬Â¡Ã‚Â´ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬â€�ÃƒÂ£Ã¯Â¿Â½Ã‚ÂªÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬Å¾ÃƒÂ£Ã¢â€šÂ¬Ã¢â‚¬Å¡
        ) && !_player.isGm()) { // ÃƒÂ£Ã¯Â¿Â½Ã…Â¸ÃƒÂ£Ã¯Â¿Â½Ã‚ ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬â€�GMÃƒÂ£Ã¯Â¿Â½Ã‚Â¯ÃƒÂ¤Ã‚Â¾Ã¢â‚¬Â¹ÃƒÂ¥Ã‚Â¤Ã¢â‚¬â€œ
            return false;
        }
        return true;
    }

    // ÃƒÂ¥Ã‚Â¿Ã¢â‚¬Â¦ÃƒÂ¨Ã‚Â¦Ã¯Â¿Â½ÃƒÂ¯Ã‚Â¼Ã‚Â¨ÃƒÂ¯Ã‚Â¼Ã‚Â°ÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ¯Ã‚Â¼Ã‚Â­ÃƒÂ¯Ã‚Â¼Ã‚Â°ÃƒÂ£Ã¯Â¿Â½Ã…â€™ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬Å¡ÃƒÂ£Ã¢â‚¬Å¡Ã¢â‚¬Â¹ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬Â¹ÃƒÂ¯Ã‚Â¼Ã…Â¸
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

            // MPÃƒÂ£Ã¯Â¿Â½Ã‚Â®INTÃƒÂ¨Ã‚Â»Ã‚Â½ÃƒÂ¦Ã‚Â¸Ã¢â‚¬Âº
            if ((_player.getInt() > 12) && (_skillId > HOLY_WEAPON) && (_skillId <= FREEZING_BLIZZARD)) { // LV2ÃƒÂ¤Ã‚Â»Ã‚Â¥ÃƒÂ¤Ã‚Â¸Ã…
                _mpConsume--;
            }
            if ((_player.getInt() > 13) && (_skillId > STALAC) && (_skillId <= FREEZING_BLIZZARD)) { // LV3ÃƒÂ¤Ã‚Â»Ã‚Â¥ÃƒÂ¤Ã‚Â¸Ã…
                _mpConsume--;
            }
            if ((_player.getInt() > 14) && (_skillId > WEAK_ELEMENTAL) && (_skillId <= FREEZING_BLIZZARD)) { // LV4ÃƒÂ¤Ã‚Â»Ã‚Â¥ÃƒÂ¤Ã‚Â¸Ã…
                _mpConsume--;
            }
            if ((_player.getInt() > 15) && (_skillId > MEDITATION) && (_skillId <= FREEZING_BLIZZARD)) { // LV5ÃƒÂ¤Ã‚Â»Ã‚Â¥ÃƒÂ¤Ã‚Â¸Ã…
                _mpConsume--;
            }
            if ((_player.getInt() > 16) && (_skillId > DARKNESS) && (_skillId <= FREEZING_BLIZZARD)) { // LV6ÃƒÂ¤Ã‚Â»Ã‚Â¥ÃƒÂ¤Ã‚Â¸Ã…
                _mpConsume--;
            }
            if ((_player.getInt() > 17) && (_skillId > BLESS_WEAPON) && (_skillId <= FREEZING_BLIZZARD)) { // LV7ÃƒÂ¤Ã‚Â»Ã‚Â¥ÃƒÂ¤Ã‚Â¸Ã…
                _mpConsume--;
            }
            if ((_player.getInt() > 18) && (_skillId > DISEASE) && (_skillId <= FREEZING_BLIZZARD)) { // LV8ÃƒÂ¤Ã‚Â»Ã‚Â¥ÃƒÂ¤Ã‚Â¸Ã…
                _mpConsume--;
            }

            // ÃƒÂ©Ã‚Â¨Ã…Â½ÃƒÂ¥Ã‚Â£Ã‚Â«ÃƒÂ¦Ã¢â€žÂ¢Ã‚ÂºÃƒÂ¥Ã… Ã¢â‚¬ÂºÃƒÂ¦Ã‚Â¸Ã¢â‚¬ÂºÃƒÂ¥Ã¢â‚¬Â¦Ã¯Â¿Â½
            if ((_player.getInt() > 12) && (_skillId >= SHOCK_STUN) && (_skillId <= COUNTER_BARRIER)) {
                if ( _player.getInt() <= 17 )
                    _mpConsume -= (_player.getInt() - 12);
                else {
                    _mpConsume -= 5 ; // int > 18
                    if ( _mpConsume > 1 ) { // ÃƒÂ¦Ã‚Â³Ã¢â‚¬Â¢ÃƒÂ¨Ã‚Â¡Ã¢â‚¬Å“ÃƒÂ©Ã¢â‚¬Å¡Ã¢â‚¬Å¾ÃƒÂ¥Ã¯Â¿Â½Ã‚Â¯ÃƒÂ¤Ã‚Â»Ã‚Â¥ÃƒÂ¦Ã‚Â¸Ã¢â‚¬ÂºÃƒÂ¥Ã¢â‚¬Â¦Ã¯Â¿Â½
                        byte extraInt = (byte) (_player.getInt() - 17) ;
                        // ÃƒÂ¦Ã‚Â¸Ã¢â‚¬ÂºÃƒÂ¥Ã¢â‚¬Â¦Ã¯Â¿Â½ÃƒÂ¥Ã¢â‚¬Â¦Ã‚Â¬ÃƒÂ¥Ã‚Â¼Ã¯Â¿Â½
                        for ( int first= 1 ,range = 2 ; first <= extraInt; first += range, range ++  )
                            _mpConsume -- ;
                    }
                }

            }

            // ÃƒÂ¨Ã‚Â£Ã¯Â¿Â½ÃƒÂ¥Ã¢â‚¬Å¡Ã¢â€žÂ¢MPÃƒÂ¦Ã‚Â¸Ã¢â‚¬ÂºÃƒÂ¥Ã¢â‚¬Â¦Ã¯Â¿Â½ ÃƒÂ¤Ã‚Â¸Ã¢â€šÂ¬ÃƒÂ¦Ã‚Â¬Ã‚Â¡ÃƒÂ¥Ã¯Â¿Â½Ã‚ÂªÃƒÂ©Ã…â€œÃ¢â€šÂ¬ÃƒÂ¥Ã‹â€ Ã‚Â¤ÃƒÂ¦Ã¢â‚¬â€œÃ‚Â·ÃƒÂ¤Ã‚Â¸Ã¢â€šÂ¬ÃƒÂ¥Ã¢â€šÂ¬Ã¢â‚¬Â¹
            if ((_skillId == PHYSICAL_ENCHANT_DEX) && _player.getInventory().checkEquipped(20013)) { // ÃƒÂ¦Ã¢â‚¬Â¢Ã¯Â¿Â½ÃƒÂ¦Ã¯Â¿Â½Ã‚Â·ÃƒÂ©Ã‚Â­Ã¢â‚¬ï¿½ÃƒÂ¦Ã‚Â³Ã¢â‚¬Â¢ÃƒÂ©Ã‚ Ã‚Â­ÃƒÂ§Ã¢â‚¬ÂºÃ¢â‚¬ï¿½ÃƒÂ¤Ã‚Â½Ã‚Â¿ÃƒÂ§Ã¢â‚¬ï¿½Ã‚Â¨ÃƒÂ©Ã¢â€šÂ¬Ã…Â¡ÃƒÂ¦Ã…Â¡Ã‚Â¢ÃƒÂ¦Ã‚Â°Ã‚Â£ÃƒÂ¨Ã¢â‚¬Å¾Ã‹â€ ÃƒÂ¨Ã‚Â¡Ã¢â‚¬Å“
                _mpConsume /= 2;
            }
            else if ((_skillId == HASTE) && _player.getInventory().checkEquipped(20013)) { // ÃƒÂ¦Ã¢â‚¬Â¢Ã¯Â¿Â½ÃƒÂ¦Ã¯Â¿Â½Ã‚Â·ÃƒÂ©Ã‚Â­Ã¢â‚¬ï¿½ÃƒÂ¦Ã‚Â³Ã¢â‚¬Â¢ÃƒÂ©Ã‚ Ã‚Â­ÃƒÂ§Ã¢â‚¬ÂºÃ¢â‚¬ï¿½ÃƒÂ¤Ã‚Â½Ã‚Â¿ÃƒÂ§Ã¢â‚¬ï¿½Ã‚Â¨ÃƒÂ¥Ã… Ã‚ ÃƒÂ©Ã¢â€šÂ¬Ã…Â¸ÃƒÂ¨Ã‚Â¡Ã¢â‚¬Å“
                _mpConsume /= 2;
            }
            else if ((_skillId == HEAL) && _player.getInventory().checkEquipped(20014)) { // ÃƒÂ¦Ã‚Â²Ã‚Â»ÃƒÂ§Ã¢â€žÂ¢Ã¢â‚¬â„¢ÃƒÂ©Ã‚Â­Ã¢â‚¬ï¿½ÃƒÂ¦Ã‚Â³Ã¢â‚¬Â¢ÃƒÂ©Ã‚ Ã‚Â­ÃƒÂ§Ã¢â‚¬ÂºÃ¢â‚¬ï¿½ÃƒÂ¤Ã‚Â½Ã‚Â¿ÃƒÂ§Ã¢â‚¬ï¿½Ã‚Â¨ÃƒÂ¥Ã‹â€ Ã¯Â¿Â½ÃƒÂ§Ã‚Â´Ã…Â¡ÃƒÂ¦Ã‚Â²Ã‚Â»ÃƒÂ§Ã¢â€žÂ¢Ã¢â‚¬â„¢ÃƒÂ¨Ã‚Â¡Ã¢â‚¬Å“
                _mpConsume /= 2;
            }
            else if ((_skillId == EXTRA_HEAL) && _player.getInventory().checkEquipped(20014)) { // ÃƒÂ¦Ã‚Â²Ã‚Â»ÃƒÂ§Ã¢â€žÂ¢Ã¢â‚¬â„¢ÃƒÂ©Ã‚Â­Ã¢â‚¬ï¿½ÃƒÂ¦Ã‚Â³Ã¢â‚¬Â¢ÃƒÂ©Ã‚ Ã‚Â­ÃƒÂ§Ã¢â‚¬ÂºÃ¢â‚¬ï¿½ÃƒÂ¤Ã‚Â½Ã‚Â¿ÃƒÂ§Ã¢â‚¬ï¿½Ã‚Â¨ÃƒÂ¤Ã‚Â¸Ã‚Â­ÃƒÂ§Ã‚Â´Ã…Â¡ÃƒÂ¦Ã‚Â²Ã‚Â»ÃƒÂ§Ã¢â€žÂ¢Ã¢â‚¬â„¢ÃƒÂ¨Ã‚Â¡Ã¢â‚¬Å“
                _mpConsume /= 2;
            }
            else if ((_skillId == ENCHANT_WEAPON) && _player.getInventory().checkEquipped(20015)) { // ÃƒÂ¥Ã… Ã¢â‚¬ÂºÃƒÂ©Ã¢â‚¬Â¡Ã¯Â¿Â½ÃƒÂ©Ã‚Â­Ã¢â‚¬ï¿½ÃƒÂ¦Ã‚Â³Ã¢â‚¬Â¢ÃƒÂ©Ã‚ Ã‚Â­ÃƒÂ§Ã¢â‚¬ÂºÃ¢â‚¬ï¿½ÃƒÂ¤Ã‚Â½Ã‚Â¿ÃƒÂ§Ã¢â‚¬ï¿½Ã‚Â¨ÃƒÂ¦Ã¢â‚¬Å“Ã‚Â¬ÃƒÂ¤Ã‚Â¼Ã‚Â¼ÃƒÂ©Ã‚Â­Ã¢â‚¬ï¿½ÃƒÂ¦Ã‚Â³Ã¢â‚¬Â¢ÃƒÂ¦Ã‚Â­Ã‚Â¦ÃƒÂ¥Ã¢â€žÂ¢Ã‚Â¨
                _mpConsume /= 2;
            }
            else if ((_skillId == DETECTION) && _player.getInventory().checkEquipped(20015)) { // ÃƒÂ¥Ã… Ã¢â‚¬ÂºÃƒÂ©Ã¢â‚¬Â¡Ã¯Â¿Â½ÃƒÂ©Ã‚Â­Ã¢â‚¬ï¿½ÃƒÂ¦Ã‚Â³Ã¢â‚¬Â¢ÃƒÂ©Ã‚ Ã‚Â­ÃƒÂ§Ã¢â‚¬ÂºÃ¢â‚¬ï¿½ÃƒÂ¤Ã‚Â½Ã‚Â¿ÃƒÂ§Ã¢â‚¬ï¿½Ã‚Â¨ÃƒÂ§Ã¢â‚¬Å¾Ã‚Â¡ÃƒÂ¦Ã¢â‚¬Â°Ã¢â€šÂ¬ÃƒÂ©Ã¯Â¿Â½Ã¯Â¿Â½ÃƒÂ¥Ã‚Â½Ã‚Â¢ÃƒÂ¨Ã‚Â¡Ã¢â‚¬Å“
                _mpConsume /= 2;
            }
            else if ((_skillId == PHYSICAL_ENCHANT_STR) && _player.getInventory().checkEquipped(20015)) { // ÃƒÂ¥Ã… Ã¢â‚¬ÂºÃƒÂ©Ã¢â‚¬Â¡Ã¯Â¿Â½ÃƒÂ©Ã‚Â­Ã¢â‚¬ï¿½ÃƒÂ¦Ã‚Â³Ã¢â‚¬Â¢ÃƒÂ©Ã‚ Ã‚Â­ÃƒÂ§Ã¢â‚¬ÂºÃ¢â‚¬ï¿½ÃƒÂ¤Ã‚Â½Ã‚Â¿ÃƒÂ§Ã¢â‚¬ï¿½Ã‚Â¨ÃƒÂ©Ã‚Â«Ã¢â‚¬ï¿½ÃƒÂ©Ã‚Â­Ã¢â‚¬Å¾ÃƒÂ¥Ã‚Â¼Ã‚Â·ÃƒÂ¥Ã¯Â¿Â½Ã‚Â¥ÃƒÂ¨Ã‚Â¡Ã¢â‚¬Å“
                _mpConsume /= 2;
            }
            else if ((_skillId == HASTE) && _player.getInventory().checkEquipped(20008)) { // ÃƒÂ¥Ã‚Â°Ã¯Â¿Â½ÃƒÂ¥Ã…Â¾Ã¢â‚¬Â¹ÃƒÂ©Ã‚Â¢Ã‚Â¨ÃƒÂ¤Ã‚Â¹Ã¢â‚¬Â¹ÃƒÂ©Ã‚ Ã‚Â­ÃƒÂ§Ã¢â‚¬ÂºÃ¢â‚¬ï¿½ÃƒÂ¤Ã‚Â½Ã‚Â¿ÃƒÂ§Ã¢â‚¬ï¿½Ã‚Â¨ÃƒÂ¥Ã… Ã‚ ÃƒÂ©Ã¢â€šÂ¬Ã…Â¸ÃƒÂ¨Ã‚Â¡Ã¢â‚¬Å“
                _mpConsume /= 2;
            }
            else if ((_skillId == HASTE) && _player.getInventory().checkEquipped(20023)) { // ÃƒÂ©Ã‚Â¢Ã‚Â¨ÃƒÂ¤Ã‚Â¹Ã¢â‚¬Â¹ÃƒÂ©Ã‚ Ã‚Â­ÃƒÂ§Ã¢â‚¬ÂºÃ¢â‚¬ï¿½ÃƒÂ¤Ã‚Â½Ã‚Â¿ÃƒÂ§Ã¢â‚¬ï¿½Ã‚Â¨ÃƒÂ¥Ã… Ã‚ ÃƒÂ©Ã¢â€šÂ¬Ã…Â¸ÃƒÂ¨Ã‚Â¡Ã¢â‚¬Å“
                _mpConsume = 25;
            }
            else if ((_skillId == GREATER_HASTE) && _player.getInventory().checkEquipped(20023)) { // ÃƒÂ©Ã‚Â¢Ã‚Â¨ÃƒÂ¤Ã‚Â¹Ã¢â‚¬Â¹ÃƒÂ©Ã‚ Ã‚Â­ÃƒÂ§Ã¢â‚¬ÂºÃ¢â‚¬ï¿½ÃƒÂ¤Ã‚Â½Ã‚Â¿ÃƒÂ§Ã¢â‚¬ï¿½Ã‚Â¨ÃƒÂ¥Ã‚Â¼Ã‚Â·ÃƒÂ¥Ã… Ã¢â‚¬ÂºÃƒÂ¥Ã… Ã‚ ÃƒÂ©Ã¢â€šÂ¬Ã…Â¸ÃƒÂ¨Ã‚Â¡Ã¢â‚¬Å“
                _mpConsume /= 2;
            }

            // ÃƒÂ¥Ã‹â€ Ã¯Â¿Â½ÃƒÂ¥Ã‚Â§Ã¢â‚¬Â¹ÃƒÂ¨Ã†â€™Ã‚Â½ÃƒÂ¥Ã… Ã¢â‚¬ÂºÃƒÂ¦Ã‚Â¸Ã¢â‚¬ÂºÃƒÂ¥Ã¢â‚¬Â¦Ã¯Â¿Â½
            if (_player.getOriginalMagicConsumeReduction() > 0) {
                _mpConsume -= _player.getOriginalMagicConsumeReduction();
            }

            if (0 < _skill.getMpConsume()) {
                _mpConsume = Math.max(_mpConsume, 1); // ÃƒÂ¦Ã…â€œÃ¢â€šÂ¬ÃƒÂ¥Ã‚Â°Ã¯Â¿Â½ÃƒÂ¥Ã¢â€šÂ¬Ã‚Â¼ 1
            }
        }

        if (currentHp < _hpConsume + 1) {
            if (_user instanceof L1PcInstance) {
                _player.sendPackets(new S_ServerMessage(279)); // \f1ÃƒÂ¥Ã¢â‚¬ÂºÃ‚ ÃƒÂ©Ã‚Â«Ã¢â‚¬ï¿½ÃƒÂ¥Ã… Ã¢â‚¬ÂºÃƒÂ¤Ã‚Â¸Ã¯Â¿Â½ÃƒÂ¨Ã‚Â¶Ã‚Â³ÃƒÂ¨Ã¢â€šÂ¬Ã…â€™ÃƒÂ§Ã¢â‚¬Å¾Ã‚Â¡ÃƒÂ¦Ã‚Â³Ã¢â‚¬Â¢ÃƒÂ¤Ã‚Â½Ã‚Â¿ÃƒÂ§Ã¢â‚¬ï¿½Ã‚Â¨ÃƒÂ©Ã‚Â­Ã¢â‚¬ï¿½ÃƒÂ¦Ã‚Â³Ã¢â‚¬Â¢ÃƒÂ£Ã¢â€šÂ¬Ã¢â‚¬Å¡
            }
            return false;
        }
        else if (currentMp < _mpConsume) {
            if (_user instanceof L1PcInstance) {
                _player.sendPackets(new S_ServerMessage(278)); // \f1ÃƒÂ¥Ã¢â‚¬ÂºÃ‚ ÃƒÂ©Ã‚Â­Ã¢â‚¬ï¿½ÃƒÂ¥Ã… Ã¢â‚¬ÂºÃƒÂ¤Ã‚Â¸Ã¯Â¿Â½ÃƒÂ¨Ã‚Â¶Ã‚Â³ÃƒÂ¨Ã¢â€šÂ¬Ã…â€™ÃƒÂ§Ã¢â‚¬Å¾Ã‚Â¡ÃƒÂ¦Ã‚Â³Ã¢â‚¬Â¢ÃƒÂ¤Ã‚Â½Ã‚Â¿ÃƒÂ§Ã¢â‚¬ï¿½Ã‚Â¨ÃƒÂ©Ã‚Â­Ã¢â‚¬ï¿½ÃƒÂ¦Ã‚Â³Ã¢â‚¬Â¢ÃƒÂ£Ã¢â€šÂ¬Ã¢â‚¬Å¡
            }
            return false;
        }

        return true;
    }

    // ÃƒÂ¥Ã‚Â¿Ã¢â‚¬Â¦ÃƒÂ¨Ã‚Â¦Ã¯Â¿Â½ÃƒÂ¦Ã¯Â¿Â½Ã¯Â¿Â½ÃƒÂ¦Ã¢â‚¬â€œÃ¢â€žÂ¢ÃƒÂ£Ã¯Â¿Â½Ã…â€™ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬Å¡ÃƒÂ£Ã¢â‚¬Å¡Ã¢â‚¬Â¹ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬Â¹ÃƒÂ¯Ã‚Â¼Ã…Â¸
    private boolean isItemConsume() {

        int itemConsume = _skill.getItemConsumeId();
        int itemConsumeCount = _skill.getItemConsumeCount();

        if (itemConsume == 0) {
            return true; // ÃƒÂ¦Ã¯Â¿Â½Ã¯Â¿Â½ÃƒÂ¦Ã¢â‚¬â€œÃ¢â€žÂ¢ÃƒÂ£Ã¢â‚¬Å¡Ã¢â‚¬â„¢ÃƒÂ¥Ã‚Â¿Ã¢â‚¬Â¦ÃƒÂ¨Ã‚Â¦Ã¯Â¿Â½ÃƒÂ£Ã¯Â¿Â½Ã‚Â¨ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬â€�ÃƒÂ£Ã¯Â¿Â½Ã‚ÂªÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬Å¾ÃƒÂ©Ã‚Â­Ã¢â‚¬ï¿½ÃƒÂ¦Ã‚Â³Ã¢â‚¬Â¢
        }

        if (!_player.getInventory().checkItem(itemConsume, itemConsumeCount)) {
            return false; // ÃƒÂ¥Ã‚Â¿Ã¢â‚¬Â¦ÃƒÂ¨Ã‚Â¦Ã¯Â¿Â½ÃƒÂ¦Ã¯Â¿Â½Ã¯Â¿Â½ÃƒÂ¦Ã¢â‚¬â€œÃ¢â€žÂ¢ÃƒÂ£Ã¯Â¿Â½Ã…â€™ÃƒÂ¨Ã‚Â¶Ã‚Â³ÃƒÂ£Ã¢â‚¬Å¡Ã… ÃƒÂ£Ã¯Â¿Â½Ã‚ÂªÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬Â¹ÃƒÂ£Ã¯Â¿Â½Ã‚Â£ÃƒÂ£Ã¯Â¿Â½Ã…Â¸ÃƒÂ£Ã¢â€šÂ¬Ã¢â‚¬Å¡
        }

        return true;
    }

    // ÃƒÂ¤Ã‚Â½Ã‚Â¿ÃƒÂ§Ã¢â‚¬ï¿½Ã‚Â¨ÃƒÂ¦Ã¯Â¿Â½Ã¯Â¿Â½ÃƒÂ¦Ã¢â‚¬â€œÃ¢â€žÂ¢ÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½HPÃƒÂ£Ã†â€™Ã‚Â»MPÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½LawfulÃƒÂ£Ã¢â‚¬Å¡Ã¢â‚¬â„¢ÃƒÂ£Ã†â€™Ã…Â¾ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¤ÃƒÂ£Ã†â€™Ã… ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¹ÃƒÂ£Ã¯Â¿Â½Ã¢â€žÂ¢ÃƒÂ£Ã¢â‚¬Å¡Ã¢â‚¬Â¹ÃƒÂ£Ã¢â€šÂ¬Ã¢â‚¬Å¡
    private void useConsume() {
        if (_user instanceof L1NpcInstance) {
            // NPCÃƒÂ£Ã¯Â¿Â½Ã‚Â®ÃƒÂ¥Ã‚ Ã‚Â´ÃƒÂ¥Ã¯Â¿Â½Ã‹â€ ÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½HPÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½MPÃƒÂ£Ã¯Â¿Â½Ã‚Â®ÃƒÂ£Ã¯Â¿Â½Ã‚Â¿ÃƒÂ£Ã†â€™Ã…Â¾ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¤ÃƒÂ£Ã†â€™Ã… ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¹
            int current_hp = _npc.getCurrentHp() - _hpConsume;
            _npc.setCurrentHp(current_hp);

            int current_mp = _npc.getCurrentMp() - _mpConsume;
            _npc.setCurrentMp(current_mp);
            return;
        }

        // [Legends] - Took out check for Final Burn
        int current_hp = _player.getCurrentHp() - _hpConsume;
        _player.setCurrentHp(current_hp);

        int current_mp = _player.getCurrentMp() - _mpConsume;
        _player.setCurrentMp(current_mp);



        // LawfulÃƒÂ£Ã¢â‚¬Å¡Ã¢â‚¬â„¢ÃƒÂ£Ã†â€™Ã…Â¾ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¤ÃƒÂ£Ã†â€™Ã… ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¹
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
            return; // ÃƒÂ¦Ã¯Â¿Â½Ã¯Â¿Â½ÃƒÂ¦Ã¢â‚¬â€œÃ¢â€žÂ¢ÃƒÂ£Ã¢â‚¬Å¡Ã¢â‚¬â„¢ÃƒÂ¥Ã‚Â¿Ã¢â‚¬Â¦ÃƒÂ¨Ã‚Â¦Ã¯Â¿Â½ÃƒÂ£Ã¯Â¿Â½Ã‚Â¨ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬â€�ÃƒÂ£Ã¯Â¿Â½Ã‚ÂªÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬Å¾ÃƒÂ©Ã‚Â­Ã¢â‚¬ï¿½ÃƒÂ¦Ã‚Â³Ã¢â‚¬Â¢
        }

        // ÃƒÂ¤Ã‚Â½Ã‚Â¿ÃƒÂ§Ã¢â‚¬ï¿½Ã‚Â¨ÃƒÂ¦Ã¯Â¿Â½Ã¯Â¿Â½ÃƒÂ¦Ã¢â‚¬â€œÃ¢â€žÂ¢ÃƒÂ£Ã¢â‚¬Å¡Ã¢â‚¬â„¢ÃƒÂ£Ã†â€™Ã…Â¾ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¤ÃƒÂ£Ã†â€™Ã… ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¹
        _player.getInventory().consumeItem(itemConsume, itemConsumeCount);
    }

    // ÃƒÂ£Ã†â€™Ã…Â¾ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¸ÃƒÂ£Ã†â€™Ã†â€™ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¯ÃƒÂ£Ã†â€™Ã‚ÂªÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¹ÃƒÂ£Ã†â€™Ã‹â€ ÃƒÂ£Ã¯Â¿Â½Ã‚Â«ÃƒÂ¨Ã‚Â¿Ã‚Â½ÃƒÂ¥Ã… Ã‚ ÃƒÂ£Ã¯Â¿Â½Ã¢â€žÂ¢ÃƒÂ£Ã¢â‚¬Å¡Ã¢â‚¬Â¹ÃƒÂ£Ã¢â€šÂ¬Ã¢â‚¬Å¡
    private void addMagicList(L1Character cha, boolean repetition) {
        if (_skillTime == 0) {
            _getBuffDuration = _skill.getBuffDuration() * 1000; // ÃƒÂ¥Ã… Ã‚Â¹ÃƒÂ¦Ã…Â¾Ã…â€œÃƒÂ¦Ã¢â€žÂ¢Ã¢â‚¬Å¡ÃƒÂ©Ã¢â‚¬â€œÃ¢â‚¬Å“
            if (_skill.getBuffDuration() == 0) {
                if (_skillId == INVISIBILITY) { // ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¤ÃƒÂ£Ã†â€™Ã‚Â³ÃƒÂ£Ã†â€™Ã¢â‚¬Å“ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¸ÃƒÂ£Ã†â€™Ã¢â‚¬Å“ÃƒÂ£Ã†â€™Ã‚ÂªÃƒÂ£Ã†â€™Ã¢â‚¬ ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â£
                    cha.setSkillEffect(INVISIBILITY, 0);
                }
                return;
            }
        }
        else {
            _getBuffDuration = _skillTime * 1000; // ÃƒÂ£Ã†â€™Ã¢â‚¬ËœÃƒÂ£Ã†â€™Ã‚Â©ÃƒÂ£Ã†â€™Ã‚Â¡ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¿ÃƒÂ£Ã¯Â¿Â½Ã‚Â®timeÃƒÂ£Ã¯Â¿Â½Ã…â€™0ÃƒÂ¤Ã‚Â»Ã‚Â¥ÃƒÂ¥Ã‚Â¤Ã¢â‚¬â€œÃƒÂ£Ã¯Â¿Â½Ã‚ÂªÃƒÂ£Ã¢â‚¬Å¡Ã¢â‚¬Â°ÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ¥Ã… Ã‚Â¹ÃƒÂ¦Ã…Â¾Ã…â€œÃƒÂ¦Ã¢â€žÂ¢Ã¢â‚¬Å¡ÃƒÂ©Ã¢â‚¬â€œÃ¢â‚¬Å“ÃƒÂ£Ã¯Â¿Â½Ã‚Â¨ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬â€�ÃƒÂ£Ã¯Â¿Â½Ã‚Â¦ÃƒÂ¨Ã‚Â¨Ã‚Â­ÃƒÂ¥Ã‚Â®Ã…Â¡ÃƒÂ£Ã¯Â¿Â½Ã¢â€žÂ¢ÃƒÂ£Ã¢â‚¬Å¡Ã¢â‚¬Â¹
        }

        if (_skillId == SHOCK_STUN) {
            _getBuffDuration = _shockStunDuration;
        }

        if (_skillId == BONE_BREAK) {
            _getBuffDuration = _boneBreakDuration;
        }

        if (_skillId == CURSE_POISON) {  // ÃƒÂ¦Ã‚Â¯Ã¢â‚¬â„¢ÃƒÂ¥Ã¢â‚¬â„¢Ã¢â‚¬â„¢ÃƒÂ¦Ã…â€™Ã¯Â¿Â½ÃƒÂ§Ã‚ÂºÃ…â€™ÃƒÂ¦Ã¢â€žÂ¢Ã¢â‚¬Å¡ÃƒÂ©Ã¢â‚¬â€œÃ¢â‚¬Å“ÃƒÂ§Ã‚Â§Ã‚Â»ÃƒÂ¨Ã¢â‚¬Â¡Ã‚Â³ L1Poison ÃƒÂ¨Ã¢â€žÂ¢Ã¢â‚¬Â¢ÃƒÂ§Ã¯Â¿Â½Ã¢â‚¬ ÃƒÂ£Ã¢â€šÂ¬Ã¢â‚¬Å¡
            return;
        }
        if ((_skillId == CURSE_PARALYZE) || (_skillId == CURSE_PARALYZE2)) { // ÃƒÂ¦Ã…â€œÃ‚Â¨ÃƒÂ¤Ã‚Â¹Ã†â€™ÃƒÂ¤Ã‚Â¼Ã… ÃƒÂ§Ã…Â¡Ã¢â‚¬Å¾ÃƒÂ¥Ã¢â‚¬â„¢Ã¢â€šÂ¬ÃƒÂ¥Ã¢â‚¬â„¢Ã¢â‚¬â„¢ÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ§Ã…Â¸Ã‚Â³ÃƒÂ¥Ã…â€™Ã¢â‚¬â€œÃƒÂ¦Ã…â€™Ã¯Â¿Â½ÃƒÂ§Ã‚ÂºÃ…â€™ÃƒÂ¦Ã¢â€žÂ¢Ã¢â‚¬Å¡ÃƒÂ©Ã¢â‚¬â€œÃ¢â‚¬Å“ÃƒÂ§Ã‚Â§Ã‚Â»ÃƒÂ¨Ã¢â‚¬Â¡Ã‚Â³ L1CurseParalysis ÃƒÂ¨Ã¢â€žÂ¢Ã¢â‚¬Â¢ÃƒÂ§Ã¯Â¿Â½Ã¢â‚¬ ÃƒÂ£Ã¢â€šÂ¬Ã¢â‚¬Å¡
            return;
        }
        if (_skillId == SHAPE_CHANGE) { // ÃƒÂ¨Ã‚Â®Ã… ÃƒÂ¥Ã‚Â½Ã‚Â¢ÃƒÂ¨Ã‚Â¡Ã¢â‚¬Å“ÃƒÂ¦Ã…â€™Ã¯Â¿Â½ÃƒÂ§Ã‚ÂºÃ…â€™ÃƒÂ¦Ã¢â€žÂ¢Ã¢â‚¬Å¡ÃƒÂ©Ã¢â‚¬â€œÃ¢â‚¬Å“ÃƒÂ§Ã‚Â§Ã‚Â»ÃƒÂ¨Ã¢â‚¬Â¡Ã‚Â³ L1PolyMorph ÃƒÂ¨Ã¢â€žÂ¢Ã¢â‚¬Â¢ÃƒÂ§Ã¯Â¿Â½Ã¢â‚¬ ÃƒÂ£Ã¢â€šÂ¬Ã¢â‚¬Å¡
            return;
        }
        if ((_skillId == BLESSED_ARMOR) || (_skillId == HOLY_WEAPON // ÃƒÂ¦Ã‚Â­Ã‚Â¦ÃƒÂ¥Ã¢â€žÂ¢Ã‚Â¨ÃƒÂ£Ã†â€™Ã‚Â»ÃƒÂ©Ã‹Å“Ã‚Â²ÃƒÂ¥Ã¢â‚¬Â¦Ã‚Â·ÃƒÂ£Ã¯Â¿Â½Ã‚Â«ÃƒÂ¥Ã… Ã‚Â¹ÃƒÂ¦Ã…Â¾Ã…â€œÃƒÂ£Ã¯Â¿Â½Ã…â€™ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬Å¡ÃƒÂ£Ã¢â‚¬Å¡Ã¢â‚¬Â¹ÃƒÂ¥Ã¢â‚¬Â¡Ã‚Â¦ÃƒÂ§Ã¯Â¿Â½Ã¢â‚¬ ÃƒÂ£Ã¯Â¿Â½Ã‚Â¯L1ItemInstanceÃƒÂ£Ã¯Â¿Â½Ã‚Â«ÃƒÂ§Ã‚Â§Ã‚Â»ÃƒÂ¨Ã‚Â­Ã‚Â²ÃƒÂ£Ã¢â€šÂ¬Ã¢â‚¬Å¡
        ) || (_skillId == ENCHANT_WEAPON) || (_skillId == BLESS_WEAPON) || (_skillId == SHADOW_FANG)) {
            return;
        }
        if (((_skillId == ICE_LANCE) || (_skillId == FREEZING_BLIZZARD) || (_skillId == FREEZING_BREATH)
                || (_skillId == ICE_LANCE_COCKATRICE) || (_skillId == ICE_LANCE_BASILISK)) && !_isFreeze) { // ÃƒÂ¥Ã¢â‚¬Â¡Ã¯Â¿Â½ÃƒÂ§Ã‚ÂµÃ¯Â¿Â½ÃƒÂ¥Ã‚Â¤Ã‚Â±ÃƒÂ¦Ã¢â‚¬Â¢Ã¢â‚¬â€�
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
                        _getBuffDuration =      pc.getBuffs().get(157).getRemainingTime();
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
                        _getBuffDuration =      cha.getBuffs().get(157).getRemainingTime();
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
        if ((_skillId == AWAKEN_ANTHARAS) || (_skillId == AWAKEN_FAFURION) || (_skillId == AWAKEN_VALAKAS)) { // ÃƒÂ¨Ã‚Â¦Ã…Â¡ÃƒÂ©Ã¢â‚¬ Ã¢â‚¬â„¢ÃƒÂ£Ã¯Â¿Â½Ã‚Â®ÃƒÂ¥Ã… Ã‚Â¹ÃƒÂ¦Ã…Â¾Ã…â€œÃƒÂ¥Ã¢â‚¬Â¡Ã‚Â¦ÃƒÂ§Ã¯Â¿Â½Ã¢â‚¬ ÃƒÂ£Ã¯Â¿Â½Ã‚Â¯L1AwakeÃƒÂ£Ã¯Â¿Â½Ã‚Â«ÃƒÂ§Ã‚Â§Ã‚Â»ÃƒÂ¨Ã‚Â­Ã‚Â²ÃƒÂ£Ã¢â€šÂ¬Ã¢â‚¬Å¡
            return;
        }*/
        // ÃƒÂ©Ã‚ÂªÃ‚Â·ÃƒÂ©Ã‚Â«Ã¯Â¿Â½ÃƒÂ¦Ã‚Â¯Ã¢â€šÂ¬ÃƒÂ¥Ã‚Â£Ã…Â¾ÃƒÂ¦Ã…â€™Ã¯Â¿Â½ÃƒÂ§Ã‚ÂºÃ…â€™ÃƒÂ¦Ã¢â€žÂ¢Ã¢â‚¬Å¡ÃƒÂ©Ã¢â‚¬â€œÃ¢â‚¬Å“ÃƒÂ¥Ã¯Â¿Â½Ã‚Â¦ÃƒÂ¥Ã‚Â¤Ã¢â‚¬â€œÃƒÂ¨Ã¢â€žÂ¢Ã¢â‚¬Â¢ÃƒÂ§Ã¯Â¿Â½Ã¢â‚¬  removed BONE_BREAK HERE
        if (_skillId == CONFUSION) {
            return;
        }
        cha.setSkillEffect(_skillId, _getBuffDuration);

        if (_skillId == ELEMENTAL_FALL_DOWN && repetition) { // ÃƒÂ¥Ã‚Â¼Ã‚Â±ÃƒÂ¥Ã…â€™Ã¢â‚¬â€œÃƒÂ¥Ã‚Â±Ã‚Â¬ÃƒÂ¦Ã¢â€šÂ¬Ã‚Â§ÃƒÂ©Ã¢â‚¬Â¡Ã¯Â¿Â½ÃƒÂ¨Ã‚Â¤Ã¢â‚¬Â¡ÃƒÂ¦Ã¢â‚¬â€œÃ‚Â½ÃƒÂ¦Ã¢â‚¬ï¿½Ã‚Â¾
            if (_skillTime == 0) {
                _getBuffIconDuration = _skill.getBuffDuration(); // ÃƒÂ¥Ã… Ã‚Â¹ÃƒÂ¦Ã…Â¾Ã…â€œÃƒÂ¦Ã¢â€žÂ¢Ã¢â‚¬Å¡ÃƒÂ©Ã¢â‚¬â€œÃ¢â‚¬Å“
            } else {
                _getBuffIconDuration = _skillTime;
            }
            _target.removeSkillEffect(ELEMENTAL_FALL_DOWN);
            runSkill();
            return;
        }
        if ((cha instanceof L1PcInstance) && repetition) { // ÃƒÂ¥Ã‚Â¯Ã‚Â¾ÃƒÂ¨Ã‚Â±Ã‚Â¡ÃƒÂ£Ã¯Â¿Â½Ã…â€™PCÃƒÂ£Ã¯Â¿Â½Ã‚Â§ÃƒÂ¦Ã¢â‚¬â€�Ã‚Â¢ÃƒÂ£Ã¯Â¿Â½Ã‚Â«ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¹ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â­ÃƒÂ£Ã†â€™Ã‚Â«ÃƒÂ£Ã¯Â¿Â½Ã…â€™ÃƒÂ©Ã¢â‚¬Â¡Ã¯Â¿Â½ÃƒÂ¨Ã‚Â¤Ã¢â‚¬Â¡ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬â€�ÃƒÂ£Ã¯Â¿Â½Ã‚Â¦ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬Å¾ÃƒÂ£Ã¢â‚¬Å¡Ã¢â‚¬Â¹ÃƒÂ¥Ã‚ Ã‚Â´ÃƒÂ¥Ã¯Â¿Â½Ã‹â€
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
    // ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¢ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¤ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â³ÃƒÂ£Ã†â€™Ã‚Â³ÃƒÂ£Ã¯Â¿Â½Ã‚Â®ÃƒÂ©Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ¤Ã‚Â¿Ã‚Â¡
    private void sendIcon(L1PcInstance pc) {
        if (_skillTime == 0) {
            _getBuffIconDuration = _skill.getBuffDuration(); // ÃƒÂ¥Ã… Ã‚Â¹ÃƒÂ¦Ã…Â¾Ã…â€œÃƒÂ¦Ã¢â€žÂ¢Ã¢â‚¬Å¡ÃƒÂ©Ã¢â‚¬â€œÃ¢â‚¬Å“
        }
        else {
            _getBuffIconDuration = _skillTime; // ÃƒÂ£Ã†â€™Ã¢â‚¬ËœÃƒÂ£Ã†â€™Ã‚Â©ÃƒÂ£Ã†â€™Ã‚Â¡ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¿ÃƒÂ£Ã¯Â¿Â½Ã‚Â®timeÃƒÂ£Ã¯Â¿Â½Ã…â€™0ÃƒÂ¤Ã‚Â»Ã‚Â¥ÃƒÂ¥Ã‚Â¤Ã¢â‚¬â€œÃƒÂ£Ã¯Â¿Â½Ã‚ÂªÃƒÂ£Ã¢â‚¬Å¡Ã¢â‚¬Â°ÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ¥Ã… Ã‚Â¹ÃƒÂ¦Ã…Â¾Ã…â€œÃƒÂ¦Ã¢â€žÂ¢Ã¢â‚¬Å¡ÃƒÂ©Ã¢â‚¬â€œÃ¢â‚¬Å“ÃƒÂ£Ã¯Â¿Â½Ã‚Â¨ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬â€�ÃƒÂ£Ã¯Â¿Â½Ã‚Â¦ÃƒÂ¨Ã‚Â¨Ã‚Â­ÃƒÂ¥Ã‚Â®Ã…Â¡ÃƒÂ£Ã¯Â¿Â½Ã¢â€žÂ¢ÃƒÂ£Ã¢â‚¬Å¡Ã¢â‚¬Â¹
        }

        if (_skillId == SHIELD) { // ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â·ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã†â€™Ã‚Â«ÃƒÂ£Ã†â€™Ã¢â‚¬Â°
            pc.sendPackets(new S_SkillIconShield(5, _getBuffIconDuration));
        }
        else if (_skillId == SHADOW_ARMOR) { // ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â·ÃƒÂ£Ã†â€™Ã‚Â£ÃƒÂ£Ã†â€™Ã¢â‚¬Â°ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¦ ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¢ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã†â€™Ã…Â¾ÃƒÂ£Ã†â€™Ã‚Â¼
            pc.sendPackets(new S_SkillIconShield(3, _getBuffIconDuration));
        }
        else if (_skillId == DRESS_DEXTERITY) { // ÃƒÂ£Ã†â€™Ã¢â‚¬Â°ÃƒÂ£Ã†â€™Ã‚Â¬ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¹ ÃƒÂ£Ã†â€™Ã¢â‚¬Â¡ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¯ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¹ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¿ÃƒÂ£Ã†â€™Ã‚ÂªÃƒÂ£Ã†â€™Ã¢â‚¬ ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â£ÃƒÂ£Ã†â€™Ã‚Â¼
            pc.sendPackets(new S_Dexup(pc, 2, _getBuffIconDuration));
        }
        else if (_skillId == DRESS_MIGHTY) { // ÃƒÂ£Ã†â€™Ã¢â‚¬Â°ÃƒÂ£Ã†â€™Ã‚Â¬ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¹ ÃƒÂ£Ã†â€™Ã…Â¾ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¤ÃƒÂ£Ã†â€™Ã¢â‚¬ ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â£ÃƒÂ£Ã†â€™Ã‚Â¼
            pc.sendPackets(new S_Strup(pc, 2, _getBuffIconDuration));
        }
        else if (_skillId == GLOWING_AURA) { // ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â°ÃƒÂ£Ã†â€™Ã‚Â­ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¦ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â£ÃƒÂ£Ã†â€™Ã‚Â³ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â° ÃƒÂ£Ã¢â‚¬Å¡Ã‚ÂªÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã†â€™Ã‚Â©
            pc.sendPackets(new S_SkillIconAura(113, _getBuffIconDuration));
        }
        else if (_skillId == SHINING_AURA) { // ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â·ÃƒÂ£Ã†â€™Ã‚Â£ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¤ÃƒÂ£Ã†â€™Ã¢â‚¬Â¹ÃƒÂ£Ã†â€™Ã‚Â³ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â° ÃƒÂ£Ã¢â‚¬Å¡Ã‚ÂªÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã†â€™Ã‚Â©
            pc.sendPackets(new S_SkillIconAura(114, _getBuffIconDuration));
        }
        else if (_skillId == BRAVE_AURA) { // ÃƒÂ£Ã†â€™Ã¢â‚¬â€œÃƒÂ£Ã†â€™Ã‚Â¬ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¤ÃƒÂ£Ã†â€™Ã¢â‚¬â€œ ÃƒÂ£Ã¢â‚¬Å¡Ã‚ÂªÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã†â€™Ã‚Â©
            pc.sendPackets(new S_SkillIconAura(116, _getBuffIconDuration));
        }
        else if (_skillId == FIRE_WEAPON) { // ÃƒÂ£Ã†â€™Ã¢â‚¬Â¢ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¡ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¤ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¢ÃƒÂ£Ã†â€™Ã‚Â¼ ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¦ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â§ÃƒÂ£Ã†â€™Ã¯Â¿Â½ÃƒÂ£Ã†â€™Ã‚Â³
            pc.sendPackets(new S_SkillIconAura(147, _getBuffIconDuration));
        }
        else if (_skillId == WIND_SHOT) { // ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¦ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â£ÃƒÂ£Ã†â€™Ã‚Â³ÃƒÂ£Ã†â€™Ã¢â‚¬Â° ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â·ÃƒÂ£Ã†â€™Ã‚Â§ÃƒÂ£Ã†â€™Ã†â€™ÃƒÂ£Ã†â€™Ã‹â€
            pc.sendPackets(new S_SkillIconAura(148, _getBuffIconDuration));
        }
        else if (_skillId == FIRE_BLESS) { // ÃƒÂ£Ã†â€™Ã¢â‚¬Â¢ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¡ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¤ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¢ÃƒÂ£Ã†â€™Ã‚Â¼ ÃƒÂ£Ã†â€™Ã¢â‚¬â€œÃƒÂ£Ã†â€™Ã‚Â¬ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¹
            pc.sendPackets(new S_SkillIconAura(154, _getBuffIconDuration));
        }
        else if (_skillId == STORM_EYE) { // ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¹ÃƒÂ£Ã†â€™Ã‹â€ ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã†â€™Ã‚  ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¢ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¤
            pc.sendPackets(new S_SkillIconAura(155, _getBuffIconDuration));
        }
        else if (_skillId == EARTH_BLESS) { // ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¢ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¹ ÃƒÂ£Ã†â€™Ã¢â‚¬â€œÃƒÂ£Ã†â€™Ã‚Â¬ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¹
            pc.sendPackets(new S_SkillIconShield(7, _getBuffIconDuration));
        }
        else if (_skillId == BURNING_WEAPON) { // ÃƒÂ£Ã†â€™Ã¯Â¿Â½ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã†â€™Ã¢â‚¬Â¹ÃƒÂ£Ã†â€™Ã‚Â³ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â° ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¦ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â§ÃƒÂ£Ã†â€™Ã¯Â¿Â½ÃƒÂ£Ã†â€™Ã‚Â³
            pc.sendPackets(new S_SkillIconAura(162, _getBuffIconDuration));
        }
        else if (_skillId == STORM_SHOT) { // ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¹ÃƒÂ£Ã†â€™Ã‹â€ ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã†â€™Ã‚  ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â·ÃƒÂ£Ã†â€™Ã‚Â§ÃƒÂ£Ã†â€™Ã†â€™ÃƒÂ£Ã†â€™Ã‹â€
            pc.sendPackets(new S_SkillIconAura(165, _getBuffIconDuration));
        }
        else if (_skillId == IRON_SKIN) { // ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¢ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¤ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¢ÃƒÂ£Ã†â€™Ã‚Â³ ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¹ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â­ÃƒÂ£Ã†â€™Ã‚Â³
            pc.sendPackets(new S_SkillIconShield(10, _getBuffIconDuration));
        }
        else if (_skillId == EARTH_SKIN) { // ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¢ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¹ ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¹ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â­ÃƒÂ£Ã†â€™Ã‚Â³
            pc.sendPackets(new S_SkillIconShield(6, _getBuffIconDuration));
        }
        else if (_skillId == PHYSICAL_ENCHANT_STR) { // ÃƒÂ£Ã†â€™Ã¢â‚¬Â¢ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â£ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¸ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â«ÃƒÂ£Ã†â€™Ã‚Â« ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¨ÃƒÂ£Ã†â€™Ã‚Â³ÃƒÂ£Ã†â€™Ã¯Â¿Â½ÃƒÂ£Ã†â€™Ã‚Â£ÃƒÂ£Ã†â€™Ã‚Â³ÃƒÂ£Ã†â€™Ã‹â€ ÃƒÂ¯Ã‚Â¼Ã…Â¡STR
            pc.sendPackets(new S_Strup(pc, 5, _getBuffIconDuration));
        }
        else if (_skillId == PHYSICAL_ENCHANT_DEX) { // ÃƒÂ£Ã†â€™Ã¢â‚¬Â¢ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â£ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¸ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â«ÃƒÂ£Ã†â€™Ã‚Â« ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¨ÃƒÂ£Ã†â€™Ã‚Â³ÃƒÂ£Ã†â€™Ã¯Â¿Â½ÃƒÂ£Ã†â€™Ã‚Â£ÃƒÂ£Ã†â€™Ã‚Â³ÃƒÂ£Ã†â€™Ã‹â€ ÃƒÂ¯Ã‚Â¼Ã…Â¡DEX
            pc.sendPackets(new S_Dexup(pc, 5, _getBuffIconDuration));
        }
        else if ((_skillId == HASTE) || (_skillId == GREATER_HASTE)) { // ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â°ÃƒÂ£Ã†â€™Ã‚Â¬ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¿ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã†â€™Ã‹Å“ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¤ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¹ÃƒÂ£Ã†â€™Ã‹â€
            pc.sendPackets(new S_SkillHaste(pc.getId(), 1, _getBuffIconDuration));
            pc.broadcastPacket(new S_SkillHaste(pc.getId(), 1, 0));
        }
        else if ((_skillId == HOLY_WALK) || (_skillId == MOVING_ACCELERATION) || (_skillId == WIND_WALK)) { // ÃƒÂ£Ã†â€™Ã¢â‚¬ÂºÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã†â€™Ã‚ÂªÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¦ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â©ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¯ÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ£Ã†â€™Ã‚ ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã†â€™Ã¢â‚¬Å“ÃƒÂ£Ã†â€™Ã‚Â³ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â°ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¢ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¯ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â»ÃƒÂ£Ã†â€™Ã‚Â¬ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â·ÃƒÂ£Ã†â€™Ã‚Â§ÃƒÂ£Ã†â€™Ã‚Â³ÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¦ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â£ÃƒÂ£Ã†â€™Ã‚Â³ÃƒÂ£Ã†â€™Ã¢â‚¬Â°ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¦ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â©ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¯
            pc.sendPackets(new S_SkillBrave(pc.getId(), 4, _getBuffIconDuration));
            pc.broadcastPacket(new S_SkillBrave(pc.getId(), 4, 0));
        }
        else if (_skillId == BLOODLUST) { // ÃƒÂ£Ã†â€™Ã¢â‚¬â€œÃƒÂ£Ã†â€™Ã‚Â©ÃƒÂ£Ã†â€™Ã†â€™ÃƒÂ£Ã†â€™Ã¢â‚¬Â°ÃƒÂ£Ã†â€™Ã‚Â©ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¹ÃƒÂ£Ã†â€™Ã‹â€
            pc.sendPackets(new S_SkillBrave(pc.getId(), 6, _getBuffIconDuration));
            pc.broadcastPacket(new S_SkillBrave(pc.getId(), 6, 0));
        }
        else if ((_skillId == SLOW) || (_skillId == MASS_SLOW) || (_skillId == ENTANGLE)) { // ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¹ÃƒÂ£Ã†â€™Ã‚Â­ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¨ÃƒÂ£Ã†â€™Ã‚Â³ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¿ÃƒÂ£Ã†â€™Ã‚Â³ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â°ÃƒÂ£Ã†â€™Ã‚Â«ÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ£Ã†â€™Ã…Â¾ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¹ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¹ÃƒÂ£Ã†â€™Ã‚Â­ÃƒÂ£Ã†â€™Ã‚Â¼
            pc.sendPackets(new S_SkillHaste(pc.getId(), 2, _getBuffIconDuration));
            pc.broadcastPacket(new S_SkillHaste(pc.getId(), 2, 0));
        }
        else if (_skillId == IMMUNE_TO_HARM) {
            pc.sendPackets(new S_SkillIconGFX(40, _getBuffIconDuration));
        }
        else if (_skillId == WIND_SHACKLE) { // ÃƒÂ©Ã‚Â¢Ã‚Â¨ÃƒÂ¤Ã‚Â¹Ã¢â‚¬Â¹ÃƒÂ¦Ã…Â¾Ã‚Â·ÃƒÂ©Ã…Â½Ã¢â‚¬â€œ
            pc.sendPackets(new S_SkillIconWindShackle(pc.getId(), _getBuffIconDuration));
            pc.broadcastPacket(new S_SkillIconWindShackle(pc.getId(), _getBuffIconDuration));
        }
        pc.sendPackets(new S_OwnCharStatus(pc));
    }


    public void sendIcon(L1PcInstance pc, int skillId, int buffIconDuration) {
        if (skillId == SHIELD) { // ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â·ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã†â€™Ã‚Â«ÃƒÂ£Ã†â€™Ã¢â‚¬Â°
            pc.sendPackets(new S_SkillIconShield(5, buffIconDuration));
        }
        else if (skillId == SHADOW_ARMOR) { // ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â·ÃƒÂ£Ã†â€™Ã‚Â£ÃƒÂ£Ã†â€™Ã¢â‚¬Â°ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¦ ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¢ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã†â€™Ã…Â¾ÃƒÂ£Ã†â€™Ã‚Â¼
            pc.sendPackets(new S_SkillIconShield(3, buffIconDuration));
        }
        else if (skillId == DRESS_DEXTERITY) { // ÃƒÂ£Ã†â€™Ã¢â‚¬Â°ÃƒÂ£Ã†â€™Ã‚Â¬ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¹ ÃƒÂ£Ã†â€™Ã¢â‚¬Â¡ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¯ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¹ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¿ÃƒÂ£Ã†â€™Ã‚ÂªÃƒÂ£Ã†â€™Ã¢â‚¬ ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â£ÃƒÂ£Ã†â€™Ã‚Â¼
            pc.sendPackets(new S_Dexup(pc, 2, buffIconDuration));
        }
        else if (skillId == DRESS_MIGHTY) { // ÃƒÂ£Ã†â€™Ã¢â‚¬Â°ÃƒÂ£Ã†â€™Ã‚Â¬ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¹ ÃƒÂ£Ã†â€™Ã…Â¾ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¤ÃƒÂ£Ã†â€™Ã¢â‚¬ ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â£ÃƒÂ£Ã†â€™Ã‚Â¼
            pc.sendPackets(new S_Strup(pc, 2, buffIconDuration));
        }
        else if (skillId == GLOWING_AURA) { // ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â°ÃƒÂ£Ã†â€™Ã‚Â­ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¦ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â£ÃƒÂ£Ã†â€™Ã‚Â³ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â° ÃƒÂ£Ã¢â‚¬Å¡Ã‚ÂªÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã†â€™Ã‚Â©
            pc.sendPackets(new S_SkillIconAura(113, buffIconDuration));
        }
        else if (skillId == SHINING_AURA) { // ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â·ÃƒÂ£Ã†â€™Ã‚Â£ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¤ÃƒÂ£Ã†â€™Ã¢â‚¬Â¹ÃƒÂ£Ã†â€™Ã‚Â³ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â° ÃƒÂ£Ã¢â‚¬Å¡Ã‚ÂªÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã†â€™Ã‚Â©
            pc.sendPackets(new S_SkillIconAura(114, buffIconDuration));
        }
        else if (skillId == BRAVE_AURA) { // ÃƒÂ£Ã†â€™Ã¢â‚¬â€œÃƒÂ£Ã†â€™Ã‚Â¬ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¤ÃƒÂ£Ã†â€™Ã¢â‚¬â€œ ÃƒÂ£Ã¢â‚¬Å¡Ã‚ÂªÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã†â€™Ã‚Â©
            pc.sendPackets(new S_SkillIconAura(116, buffIconDuration));
        }
        else if (skillId == FIRE_WEAPON) { // ÃƒÂ£Ã†â€™Ã¢â‚¬Â¢ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¡ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¤ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¢ÃƒÂ£Ã†â€™Ã‚Â¼ ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¦ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â§ÃƒÂ£Ã†â€™Ã¯Â¿Â½ÃƒÂ£Ã†â€™Ã‚Â³
            pc.sendPackets(new S_SkillIconAura(147, buffIconDuration));
        }
        else if (skillId == WIND_SHOT) { // ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¦ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â£ÃƒÂ£Ã†â€™Ã‚Â³ÃƒÂ£Ã†â€™Ã¢â‚¬Â° ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â·ÃƒÂ£Ã†â€™Ã‚Â§ÃƒÂ£Ã†â€™Ã†â€™ÃƒÂ£Ã†â€™Ã‹â€
            pc.sendPackets(new S_SkillIconAura(148, buffIconDuration));
        }
        else if (skillId == FIRE_BLESS) { // ÃƒÂ£Ã†â€™Ã¢â‚¬Â¢ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¡ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¤ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¢ÃƒÂ£Ã†â€™Ã‚Â¼ ÃƒÂ£Ã†â€™Ã¢â‚¬â€œÃƒÂ£Ã†â€™Ã‚Â¬ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¹
            pc.sendPackets(new S_SkillIconAura(154, buffIconDuration));
        }
        else if (skillId == STORM_EYE) { // ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¹ÃƒÂ£Ã†â€™Ã‹â€ ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã†â€™Ã‚  ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¢ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¤
            pc.sendPackets(new S_SkillIconAura(155, buffIconDuration));
        }
        else if (skillId == EARTH_BLESS) { // ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¢ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¹ ÃƒÂ£Ã†â€™Ã¢â‚¬â€œÃƒÂ£Ã†â€™Ã‚Â¬ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¹
            pc.sendPackets(new S_SkillIconShield(7, buffIconDuration));
        }
        else if (skillId == BURNING_WEAPON) { // ÃƒÂ£Ã†â€™Ã¯Â¿Â½ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã†â€™Ã¢â‚¬Â¹ÃƒÂ£Ã†â€™Ã‚Â³ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â° ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¦ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â§ÃƒÂ£Ã†â€™Ã¯Â¿Â½ÃƒÂ£Ã†â€™Ã‚Â³
            pc.sendPackets(new S_SkillIconAura(162, buffIconDuration));
        }
        else if (skillId == STORM_SHOT) { // ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¹ÃƒÂ£Ã†â€™Ã‹â€ ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã†â€™Ã‚  ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â·ÃƒÂ£Ã†â€™Ã‚Â§ÃƒÂ£Ã†â€™Ã†â€™ÃƒÂ£Ã†â€™Ã‹â€
            pc.sendPackets(new S_SkillIconAura(165, buffIconDuration));
        }
        else if (skillId == IRON_SKIN) { // ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¢ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¤ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¢ÃƒÂ£Ã†â€™Ã‚Â³ ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¹ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â­ÃƒÂ£Ã†â€™Ã‚Â³
            pc.sendPackets(new S_SkillIconShield(10, buffIconDuration));
        }
        else if (skillId == EARTH_SKIN) { // ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¢ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¹ ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¹ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â­ÃƒÂ£Ã†â€™Ã‚Â³
            pc.sendPackets(new S_SkillIconShield(6, buffIconDuration));
        }
        else if (skillId == PHYSICAL_ENCHANT_STR) { // ÃƒÂ£Ã†â€™Ã¢â‚¬Â¢ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â£ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¸ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â«ÃƒÂ£Ã†â€™Ã‚Â« ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¨ÃƒÂ£Ã†â€™Ã‚Â³ÃƒÂ£Ã†â€™Ã¯Â¿Â½ÃƒÂ£Ã†â€™Ã‚Â£ÃƒÂ£Ã†â€™Ã‚Â³ÃƒÂ£Ã†â€™Ã‹â€ ÃƒÂ¯Ã‚Â¼Ã…Â¡STR
            pc.sendPackets(new S_Strup(pc, 5, buffIconDuration));
        }
        else if (skillId == PHYSICAL_ENCHANT_DEX) { // ÃƒÂ£Ã†â€™Ã¢â‚¬Â¢ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â£ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¸ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â«ÃƒÂ£Ã†â€™Ã‚Â« ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¨ÃƒÂ£Ã†â€™Ã‚Â³ÃƒÂ£Ã†â€™Ã¯Â¿Â½ÃƒÂ£Ã†â€™Ã‚Â£ÃƒÂ£Ã†â€™Ã‚Â³ÃƒÂ£Ã†â€™Ã‹â€ ÃƒÂ¯Ã‚Â¼Ã…Â¡DEX
            pc.sendPackets(new S_Dexup(pc, 5, buffIconDuration));
        }
        else if ((skillId == HASTE) || (skillId == GREATER_HASTE)) { // ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â°ÃƒÂ£Ã†â€™Ã‚Â¬ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¿ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã†â€™Ã‹Å“ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¤ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¹ÃƒÂ£Ã†â€™Ã‹â€
            pc.sendPackets(new S_SkillHaste(pc.getId(), 1, buffIconDuration));
            pc.broadcastPacket(new S_SkillHaste(pc.getId(), 1, 0));
        }
        else if ((skillId == HOLY_WALK) || (skillId == MOVING_ACCELERATION) || (skillId == WIND_WALK)) { // ÃƒÂ£Ã†â€™Ã¢â‚¬ÂºÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã†â€™Ã‚ÂªÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¦ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â©ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¯ÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ£Ã†â€™Ã‚ ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã†â€™Ã¢â‚¬Å“ÃƒÂ£Ã†â€™Ã‚Â³ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â°ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¢ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¯ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â»ÃƒÂ£Ã†â€™Ã‚Â¬ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â·ÃƒÂ£Ã†â€™Ã‚Â§ÃƒÂ£Ã†â€™Ã‚Â³ÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¦ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â£ÃƒÂ£Ã†â€™Ã‚Â³ÃƒÂ£Ã†â€™Ã¢â‚¬Â°ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¦ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â©ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¯
            pc.sendPackets(new S_SkillBrave(pc.getId(), 4, buffIconDuration));
            pc.broadcastPacket(new S_SkillBrave(pc.getId(), 4, 0));
        }
        else if (skillId == BLOODLUST) {
            pc.sendPackets(new S_SkillBrave(pc.getId(), 6, buffIconDuration));
            pc.broadcastPacket(new S_SkillBrave(pc.getId(), 6, 0));
        }
        else if ((skillId == SLOW) || (skillId == MASS_SLOW) || (skillId == ENTANGLE)) { // ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¹ÃƒÂ£Ã†â€™Ã‚Â­ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¨ÃƒÂ£Ã†â€™Ã‚Â³ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¿ÃƒÂ£Ã†â€™Ã‚Â³ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â°ÃƒÂ£Ã†â€™Ã‚Â«ÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ£Ã†â€™Ã…Â¾ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¹ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¹ÃƒÂ£Ã†â€™Ã‚Â­ÃƒÂ£Ã†â€™Ã‚Â¼
            pc.sendPackets(new S_SkillHaste(pc.getId(), 2, buffIconDuration));
            pc.broadcastPacket(new S_SkillHaste(pc.getId(), 2, 0));
        }
        else if (skillId == IMMUNE_TO_HARM) {
            pc.sendPackets(new S_SkillIconGFX(40, buffIconDuration));
        }
        else if (skillId == WIND_SHACKLE) { // ÃƒÂ©Ã‚Â¢Ã‚Â¨ÃƒÂ¤Ã‚Â¹Ã¢â‚¬Â¹ÃƒÂ¦Ã…Â¾Ã‚Â·ÃƒÂ©Ã…Â½Ã¢â‚¬â€œ
            pc.sendPackets(new S_SkillIconWindShackle(pc.getId(), buffIconDuration));
            pc.broadcastPacket(new S_SkillIconWindShackle(pc.getId(), buffIconDuration));
        }
        else if (skillId == BURNING_SPIRIT)
        {
            pc.sendPackets(new S_SkillIconAura(162, buffIconDuration));
        }
        pc.sendPackets(new S_OwnCharStatus(pc));
    }
    // ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â°ÃƒÂ£Ã†â€™Ã‚Â©ÃƒÂ£Ã†â€™Ã¢â‚¬Â¢ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â£ÃƒÂ£Ã†â€™Ã†â€™ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¯ÃƒÂ£Ã¯Â¿Â½Ã‚Â®ÃƒÂ©Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ¤Ã‚Â¿Ã‚Â¡
    private void sendGrfx(boolean isSkillAction) {
        if (_actid == 0) {
            _actid = _skill.getActionId();
        }
        if (_gfxid == 0) {
            _gfxid = _skill.getCastGfx();
        }
        if (_gfxid == 0) {
            return; // ÃƒÂ¨Ã‚Â¡Ã‚Â¨ÃƒÂ§Ã‚Â¤Ã‚ÂºÃƒÂ£Ã¯Â¿Â½Ã¢â€žÂ¢ÃƒÂ£Ã¢â‚¬Å¡Ã¢â‚¬Â¹ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â°ÃƒÂ£Ã†â€™Ã‚Â©ÃƒÂ£Ã†â€™Ã¢â‚¬Â¢ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â£ÃƒÂ£Ã†â€™Ã†â€™ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¯ÃƒÂ£Ã¯Â¿Â½Ã…â€™ÃƒÂ§Ã¢â‚¬Å¾Ã‚Â¡ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬Å¾
        }
        int[] data = null;

        if (_user instanceof L1PcInstance) {

            int targetid = 0;
            if (_skillId != FIRE_WALL) {
                targetid = _target.getId();
            }
            L1PcInstance pc = (L1PcInstance) _user;

            switch(_skillId) {
                case FIRE_WALL: // ÃƒÂ§Ã¯Â¿Â½Ã‚Â«ÃƒÂ§Ã¢â‚¬Â°Ã‚Â¢
                case LIFE_STREAM: // ÃƒÂ¦Ã‚Â²Ã‚Â»ÃƒÂ§Ã¢â€žÂ¢Ã¢â‚¬â„¢ÃƒÂ¨Ã†â€™Ã‚Â½ÃƒÂ©Ã¢â‚¬Â¡Ã¯Â¿Â½ÃƒÂ©Ã‚Â¢Ã‚Â¨ÃƒÂ¦Ã…Â¡Ã‚Â´
                case ELEMENTAL_FALL_DOWN: // ÃƒÂ¥Ã‚Â¼Ã‚Â±ÃƒÂ¥Ã…â€™Ã¢â‚¬â€œÃƒÂ¥Ã‚Â±Ã‚Â¬ÃƒÂ¦Ã¢â€šÂ¬Ã‚Â§
                    if (_skillId == FIRE_WALL) {
                        pc.setHeading(pc.targetDirection(_targetX, _targetY));
                        pc.sendPackets(new S_ChangeHeading(pc));
                        pc.broadcastPacket(new S_ChangeHeading(pc));
                    }
                    S_DoActionGFX gfx = new S_DoActionGFX(pc.getId(), _actid);
                    pc.sendPackets(gfx);
                    pc.broadcastPacket(gfx);
                    return;
                case SHOCK_STUN: // ÃƒÂ¨Ã‚Â¡Ã¯Â¿Â½ÃƒÂ¦Ã¢â‚¬Å“Ã… ÃƒÂ¤Ã‚Â¹Ã¢â‚¬Â¹ÃƒÂ¦Ã…Â¡Ã‹â€
                    if (_targetList.isEmpty()) { // ÃƒÂ¥Ã‚Â¤Ã‚Â±ÃƒÂ¦Ã¢â‚¬Â¢Ã¢â‚¬â€�
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
                case LIGHT: // ÃƒÂ¦Ã¢â‚¬â€�Ã‚Â¥ÃƒÂ¥Ã¢â‚¬Â¦Ã¢â‚¬Â°ÃƒÂ¨Ã‚Â¡Ã¢â‚¬Å“
                    pc.sendPackets(new S_Sound(145));
                    break;
                case MIND_BREAK: // ÃƒÂ¥Ã‚Â¿Ã†â€™ÃƒÂ©Ã¯Â¿Â½Ã‹â€ ÃƒÂ§Ã‚ Ã‚Â´ÃƒÂ¥Ã‚Â£Ã…Â¾
                case JOY_OF_PAIN: // ÃƒÂ§Ã¢â‚¬â€œÃ‚Â¼ÃƒÂ§Ã¢â‚¬â€�Ã¢â‚¬ÂºÃƒÂ§Ã…Â¡Ã¢â‚¬Å¾ÃƒÂ¦Ã‚Â­Ã‚Â¡ÃƒÂ¦Ã¢â‚¬Å¾Ã¢â‚¬Â°
                    data = new int[] {_actid, _dmg, 0}; // data = {actid, dmg, effect}
                    pc.sendPackets(new S_AttackPacket(pc, targetid, data));
                    pc.broadcastPacket(new S_AttackPacket(pc, targetid, data));
                    pc.sendPackets(new S_SkillSound(targetid, _gfxid));
                    pc.broadcastPacket(new S_SkillSound(targetid, _gfxid));
                    return;
                case CONFUSION: // ÃƒÂ¦Ã‚Â·Ã‚Â·ÃƒÂ¤Ã‚ÂºÃ¢â‚¬Å¡
                    data = new int[] {_actid, _dmg, 0}; // data = {actid, dmg, effect}
                    pc.sendPackets(new S_AttackPacket(pc, targetid, data));
                    pc.broadcastPacket(new S_AttackPacket(pc, targetid, data));
                    return;
                case SMASH: // ÃƒÂ¦Ã…Â¡Ã‚Â´ÃƒÂ¦Ã¢â‚¬Å“Ã…
                    pc.sendPackets(new S_SkillSound(targetid, _gfxid));
                    pc.broadcastPacket(new S_SkillSound(targetid, _gfxid));
                    return;
                case TAMING_MONSTER: // ÃƒÂ¨Ã‚Â¿Ã‚Â·ÃƒÂ©Ã‚Â­Ã¢â‚¬Â¦
                    pc.sendPackets(new S_EffectLocation(_targetX, _targetY, _gfxid));
                    pc.broadcastPacket(new S_EffectLocation(_targetX, _targetY, _gfxid));
                    return;
                default:
                    break;
            }

/*                      if (_skillId == BONE_BREAK || _skillId == ARM_BREAKER) {
                                return;
                        }*/

            if (_skillId == ARM_BREAKER) return;

            if (_targetList.isEmpty() && !(_skill.getTarget().equals("none"))) {
                // ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¿ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â²ÃƒÂ£Ã†â€™Ã†â€™ÃƒÂ£Ã†â€™Ã‹â€ ÃƒÂ¦Ã¢â‚¬Â¢Ã‚Â°ÃƒÂ£Ã¯Â¿Â½Ã…â€™ÃƒÂ¯Ã‚Â¼Ã¯Â¿Â½ÃƒÂ£Ã¯Â¿Â½Ã‚Â§ÃƒÂ¥Ã‚Â¯Ã‚Â¾ÃƒÂ¨Ã‚Â±Ã‚Â¡ÃƒÂ£Ã¢â‚¬Å¡Ã¢â‚¬â„¢ÃƒÂ¦Ã…â€™Ã¢â‚¬Â¡ÃƒÂ¥Ã‚Â®Ã…Â¡ÃƒÂ£Ã¯Â¿Â½Ã¢â€žÂ¢ÃƒÂ£Ã¢â‚¬Å¡Ã¢â‚¬Â¹ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¹ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â­ÃƒÂ£Ã†â€™Ã‚Â«ÃƒÂ£Ã¯Â¿Â½Ã‚Â®ÃƒÂ¥Ã‚ Ã‚Â´ÃƒÂ¥Ã¯Â¿Â½Ã‹â€ ÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ©Ã‚Â­Ã¢â‚¬ï¿½ÃƒÂ¦Ã‚Â³Ã¢â‚¬Â¢ÃƒÂ¤Ã‚Â½Ã‚Â¿ÃƒÂ§Ã¢â‚¬ï¿½Ã‚Â¨ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¨ÃƒÂ£Ã†â€™Ã¢â‚¬Â¢ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â§ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¯ÃƒÂ£Ã†â€™Ã‹â€ ÃƒÂ£Ã¯Â¿Â½Ã‚ ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬ËœÃƒÂ¨Ã‚Â¡Ã‚Â¨ÃƒÂ§Ã‚Â¤Ã‚ÂºÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬â€�ÃƒÂ£Ã¯Â¿Â½Ã‚Â¦ÃƒÂ§Ã‚ÂµÃ¢â‚¬Å¡ÃƒÂ¤Ã‚ÂºÃ¢â‚¬
                int tempchargfx = _player.getTempCharGfx();
                if ((tempchargfx == 5727) || (tempchargfx == 5730)) { // ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â·ÃƒÂ£Ã†â€™Ã‚Â£ÃƒÂ£Ã†â€™Ã¢â‚¬Â°ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¦ÃƒÂ§Ã‚Â³Ã‚Â»ÃƒÂ¥Ã‚Â¤Ã¢â‚¬Â°ÃƒÂ¨Ã‚ÂºÃ‚Â«ÃƒÂ£Ã¯Â¿Â½Ã‚Â®ÃƒÂ£Ã†â€™Ã‚Â¢ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â·ÃƒÂ£Ã†â€™Ã‚Â§ÃƒÂ£Ã†â€™Ã‚Â³ÃƒÂ¥Ã‚Â¯Ã‚Â¾ÃƒÂ¥Ã‚Â¿Ã…â€œ
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
                if (isPcSummonPet(_target)) { // ÃƒÂ§Ã¢â‚¬ÂºÃ‚Â®ÃƒÂ¦Ã‚Â¨Ã¢â€žÂ¢ÃƒÂ§Ã…Â½Ã‚Â©ÃƒÂ¥Ã‚Â®Ã‚Â¶ÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ¥Ã‚Â¯Ã‚ÂµÃƒÂ§Ã¢â‚¬Â°Ã‚Â©ÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ¥Ã¯Â¿Â½Ã‚Â¬ÃƒÂ¥Ã¢â‚¬â€œÃ…Â¡ÃƒÂ§Ã¯Â¿Â½Ã‚Â¸
                    if ((_player.getZoneType() == 1) || (_target.getZoneType() == 1)
                            || _player.checkNonPvP(_player, _target)) { // Non-PvPÃƒÂ¨Ã‚Â¨Ã‚Â­ÃƒÂ¥Ã‚Â®Ã…Â¡
                        data = new int[] {_actid, 0, _gfxid, 6};
                        _player.sendPackets(new S_UseAttackSkill(_player, _target.getId(), _targetX, _targetY, data));
                        _player.broadcastPacket(new S_UseAttackSkill(_player, _target.getId(), _targetX, _targetY, data));
                        return;
                    }
                }

                if (getSkillArea() == 0) { // ÃƒÂ¥Ã¢â‚¬â€œÃ‚Â®ÃƒÂ©Ã‚Â«Ã¢â‚¬ï¿½ÃƒÂ¦Ã¢â‚¬ï¿½Ã‚Â»ÃƒÂ¦Ã¢â‚¬Å“Ã… ÃƒÂ©Ã‚Â­Ã¢â‚¬ï¿½ÃƒÂ¦Ã‚Â³Ã¢â‚¬Â¢
                    data = new int[] {_actid, _dmg, _gfxid, 6};
                    _player.sendPackets(new S_UseAttackSkill(_player, targetid, _targetX, _targetY, data));
                    _player.broadcastPacket(new S_UseAttackSkill(_player, targetid, _targetX, _targetY, data));
                    _target.broadcastPacketExceptTargetSight(new S_DoActionGFX(targetid, ActionCodes.ACTION_Damage), _player);
                }
                else { // ÃƒÂ¦Ã…â€œÃ¢â‚¬Â°ÃƒÂ¦Ã¢â‚¬â€œÃ‚Â¹ÃƒÂ¥Ã¯Â¿Â½Ã¢â‚¬ËœÃƒÂ§Ã‚Â¯Ã¢â‚¬Å¾ÃƒÂ¥Ã¢â‚¬ÂºÃ‚Â²ÃƒÂ¦Ã¢â‚¬ï¿½Ã‚Â»ÃƒÂ¦Ã¢â‚¬â„¢Ã†â€™ÃƒÂ©Ã‚Â­Ã¢â‚¬ï¿½ÃƒÂ¦Ã‚Â³Ã¢â‚¬Â¢
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
            else if (_skill.getTarget().equals("none") && (_skill.getType() == L1Skills.TYPE_ATTACK)) { // ÃƒÂ§Ã¢â‚¬Å¾Ã‚Â¡ÃƒÂ¦Ã¢â‚¬â€œÃ‚Â¹ÃƒÂ¥Ã¯Â¿Â½Ã¢â‚¬ËœÃƒÂ§Ã‚Â¯Ã¢â‚¬Å¾ÃƒÂ¥Ã¢â‚¬ÂºÃ‚Â²ÃƒÂ¦Ã¢â‚¬ï¿½Ã‚Â»ÃƒÂ¦Ã¢â‚¬â„¢Ã†â€™ÃƒÂ©Ã‚Â­Ã¢â‚¬ï¿½ÃƒÂ¦Ã‚Â³Ã¢â‚¬Â¢
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
            else { // ÃƒÂ¨Ã‚Â£Ã…â€œÃƒÂ¥Ã… Ã‚Â©ÃƒÂ©Ã‚Â­Ã¢â‚¬ï¿½ÃƒÂ¦Ã‚Â³Ã¢â‚¬Â¢
                // ÃƒÂ¦Ã…â€™Ã¢â‚¬Â¡ÃƒÂ¥Ã‚Â®Ã…Â¡ÃƒÂ¥Ã¢â‚¬Å¡Ã‚Â³ÃƒÂ©Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ©Ã¢â‚¬ÂºÃ¢â‚¬ ÃƒÂ©Ã‚Â«Ã¢â‚¬ï¿½ÃƒÂ¥Ã¢â‚¬Å¡Ã‚Â³ÃƒÂ©Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ¨Ã‚Â¡Ã¢â‚¬Å“ÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ¤Ã‚Â¸Ã¢â‚¬â€œÃƒÂ§Ã¢â‚¬Â¢Ã…â€™ÃƒÂ¦Ã‚Â¨Ã‚Â¹ÃƒÂ§Ã…Â¡Ã¢â‚¬Å¾ÃƒÂ¥Ã¢â‚¬ËœÃ‚Â¼ÃƒÂ¥Ã¢â‚¬â€œÃ…Â¡ÃƒÂ¤Ã‚Â»Ã‚Â¥ÃƒÂ¥Ã‚Â¤Ã¢â‚¬â€œ
                if ((_skillId != TELEPORT) && (_skillId != MASS_TELEPORT) && (_skillId != TELEPORT_TO_MATHER)) {
                    // ÃƒÂ¦Ã¢â‚¬â€œÃ‚Â½ÃƒÂ¦Ã‚Â³Ã¢â‚¬Â¢ÃƒÂ¥Ã¢â‚¬Â¹Ã¢â‚¬Â¢ÃƒÂ¤Ã‚Â½Ã…â€œ
                    if (isSkillAction) {
                        S_DoActionGFX gfx = new S_DoActionGFX(_player.getId(), _skill.getActionId());
                        _player.sendPackets(gfx);
                        _player.broadcastPacket(gfx);
                    }
                    // ÃƒÂ©Ã‚Â­Ã¢â‚¬ï¿½ÃƒÂ¦Ã‚Â³Ã¢â‚¬Â¢ÃƒÂ¥Ã‚Â±Ã¯Â¿Â½ÃƒÂ©Ã…Â¡Ã…â€œÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ¥Ã¯Â¿Â½Ã¯Â¿Â½ÃƒÂ¦Ã¢â‚¬Å“Ã… ÃƒÂ¥Ã‚Â±Ã¯Â¿Â½ÃƒÂ©Ã…Â¡Ã…â€œÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ©Ã¯Â¿Â½Ã‚Â¡ÃƒÂ¥Ã¯Â¿Â½Ã¯Â¿Â½ÃƒÂ¥Ã‚Â°Ã¢â‚¬Å¾ ÃƒÂ©Ã‚Â­Ã¢â‚¬ï¿½ÃƒÂ¦Ã‚Â³Ã¢â‚¬Â¢ÃƒÂ¦Ã¢â‚¬Â¢Ã‹â€ ÃƒÂ¦Ã…Â¾Ã…â€œÃƒÂ¥Ã¯Â¿Â½Ã‚ÂªÃƒÂ¦Ã…â€œÃ¢â‚¬Â°ÃƒÂ¨Ã¢â‚¬Â¡Ã‚ÂªÃƒÂ¨Ã‚ÂºÃ‚Â«ÃƒÂ©Ã‚Â¡Ã‚Â¯ÃƒÂ§Ã‚Â¤Ã‚Âº
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

                // ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¹ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â­ÃƒÂ£Ã†â€™Ã‚Â«ÃƒÂ£Ã¯Â¿Â½Ã‚Â®ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¨ÃƒÂ£Ã†â€™Ã¢â‚¬Â¢ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â§ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¯ÃƒÂ£Ã†â€™Ã‹â€ ÃƒÂ¨Ã‚Â¡Ã‚Â¨ÃƒÂ§Ã‚Â¤Ã‚ÂºÃƒÂ£Ã¯Â¿Â½Ã‚Â¯ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¿ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â²ÃƒÂ£Ã†â€™Ã†â€™ÃƒÂ£Ã†â€™Ã‹â€ ÃƒÂ¥Ã¢â‚¬Â¦Ã‚Â¨ÃƒÂ¥Ã¢â‚¬Å“Ã‚Â¡ÃƒÂ£Ã¯Â¿Â½Ã‚ ÃƒÂ£Ã¯Â¿Â½Ã…â€™ÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬Å¡ÃƒÂ£Ã¯Â¿Â½Ã‚Â¾ÃƒÂ£Ã¢â‚¬Å¡Ã… ÃƒÂ¥Ã‚Â¿Ã¢â‚¬Â¦ÃƒÂ¨Ã‚Â¦Ã¯Â¿Â½ÃƒÂ¦Ã¢â€šÂ¬Ã‚Â§ÃƒÂ£Ã¯Â¿Â½Ã…â€™ÃƒÂ£Ã¯Â¿Â½Ã‚ÂªÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬Å¾ÃƒÂ£Ã¯Â¿Â½Ã‚Â®ÃƒÂ£Ã¯Â¿Â½Ã‚Â§ÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¹ÃƒÂ£Ã†â€™Ã¢â‚¬ ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¿ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¹ÃƒÂ£Ã¯Â¿Â½Ã‚Â®ÃƒÂ£Ã¯Â¿Â½Ã‚Â¿ÃƒÂ©Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ¤Ã‚Â¿Ã‚Â¡
                for (TargetStatus ts : _targetList) {
                    L1Character cha = ts.getTarget();
                    if (cha instanceof L1PcInstance) {
                        L1PcInstance chaPc = (L1PcInstance) cha;
                        chaPc.sendPackets(new S_OwnCharStatus(chaPc));
                    }
                }
            }
        }
        else if (_user instanceof L1NpcInstance) { // NPCÃƒÂ£Ã¯Â¿Â½Ã…â€™ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¹ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â­ÃƒÂ£Ã†â€™Ã‚Â«ÃƒÂ£Ã¢â‚¬Å¡Ã¢â‚¬â„¢ÃƒÂ¤Ã‚Â½Ã‚Â¿ÃƒÂ£Ã¯Â¿Â½Ã‚Â£ÃƒÂ£Ã¯Â¿Â½Ã…Â¸ÃƒÂ¥Ã‚ Ã‚Â´ÃƒÂ¥Ã¯Â¿Â½Ã‹â€
            int targetid = _target.getId();

            if (_user instanceof L1MerchantInstance) {
                _user.broadcastPacket(new S_SkillSound(targetid, _gfxid));
                return;
            }

            if (_skillId == CURSE_PARALYZE || _skillId == WEAKNESS || _skillId == DISEASE) { // ÃƒÂ¦Ã…â€œÃ‚Â¨ÃƒÂ¤Ã‚Â¹Ã†â€™ÃƒÂ¤Ã‚Â¼Ã… ÃƒÂ§Ã…Â¡Ã¢â‚¬Å¾ÃƒÂ¨Ã‚Â©Ã¢â‚¬ÂºÃƒÂ¥Ã¢â‚¬â„¢Ã¢â‚¬â„¢ÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ¥Ã‚Â¼Ã‚Â±ÃƒÂ¥Ã…â€™Ã¢â‚¬â€œÃƒÂ¨Ã‚Â¡Ã¢â‚¬Å“ÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ§Ã¢â‚¬â€œÃ‚Â¾ÃƒÂ§Ã¢â‚¬â€�Ã¢â‚¬Â¦ÃƒÂ¨Ã‚Â¡Ã¢â‚¬Å“
                _user.setHeading(_user.targetDirection(_targetX, _targetY)); // ÃƒÂ¦Ã¢â‚¬ï¿½Ã‚Â¹ÃƒÂ¨Ã‚Â®Ã… ÃƒÂ©Ã¯Â¿Â½Ã‚Â¢ÃƒÂ¥Ã¯Â¿Â½Ã¢â‚¬Ëœ
                _user.broadcastPacket(new S_ChangeHeading(_user));
            }

            if (_targetList.isEmpty() && !(_skill.getTarget().equals("none"))) {
                // ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¿ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â²ÃƒÂ£Ã†â€™Ã†â€™ÃƒÂ£Ã†â€™Ã‹â€ ÃƒÂ¦Ã¢â‚¬Â¢Ã‚Â°ÃƒÂ£Ã¯Â¿Â½Ã…â€™ÃƒÂ¯Ã‚Â¼Ã¯Â¿Â½ÃƒÂ£Ã¯Â¿Â½Ã‚Â§ÃƒÂ¥Ã‚Â¯Ã‚Â¾ÃƒÂ¨Ã‚Â±Ã‚Â¡ÃƒÂ£Ã¢â‚¬Å¡Ã¢â‚¬â„¢ÃƒÂ¦Ã…â€™Ã¢â‚¬Â¡ÃƒÂ¥Ã‚Â®Ã…Â¡ÃƒÂ£Ã¯Â¿Â½Ã¢â€žÂ¢ÃƒÂ£Ã¢â‚¬Å¡Ã¢â‚¬Â¹ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¹ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â­ÃƒÂ£Ã†â€™Ã‚Â«ÃƒÂ£Ã¯Â¿Â½Ã‚Â®ÃƒÂ¥Ã‚ Ã‚Â´ÃƒÂ¥Ã¯Â¿Â½Ã‹â€ ÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ©Ã‚Â­Ã¢â‚¬ï¿½ÃƒÂ¦Ã‚Â³Ã¢â‚¬Â¢ÃƒÂ¤Ã‚Â½Ã‚Â¿ÃƒÂ§Ã¢â‚¬ï¿½Ã‚Â¨ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¨ÃƒÂ£Ã†â€™Ã¢â‚¬Â¢ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â§ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¯ÃƒÂ£Ã†â€™Ã‹â€ ÃƒÂ£Ã¯Â¿Â½Ã‚ ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬ËœÃƒÂ¨Ã‚Â¡Ã‚Â¨ÃƒÂ§Ã‚Â¤Ã‚ÂºÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬â€�ÃƒÂ£Ã¯Â¿Â½Ã‚Â¦ÃƒÂ§Ã‚ÂµÃ¢â‚¬Å¡ÃƒÂ¤Ã‚ÂºÃ¢â‚¬
                S_DoActionGFX gfx = new S_DoActionGFX(_user.getId(), _actid);
                _user.broadcastPacket(gfx);
                return;
            }

            if (_skill.getTarget().equals("attack") && (_skillId != 18)) {
                if (getSkillArea() == 0) { // ÃƒÂ¥Ã¢â‚¬â€œÃ‚Â®ÃƒÂ©Ã‚Â«Ã¢â‚¬ï¿½ÃƒÂ¦Ã¢â‚¬ï¿½Ã‚Â»ÃƒÂ¦Ã¢â‚¬Å“Ã… ÃƒÂ©Ã‚Â­Ã¢â‚¬ï¿½ÃƒÂ¦Ã‚Â³Ã¢â‚¬Â¢
                    data = new int[] {_actid, _dmg, _gfxid, 6};
                    _user.broadcastPacket(new S_UseAttackSkill(_user, targetid, _targetX, _targetY, data));
                    _target.broadcastPacketExceptTargetSight(new S_DoActionGFX(targetid, ActionCodes.ACTION_Damage), _user);
                }
                else { // ÃƒÂ¦Ã…â€œÃ¢â‚¬Â°ÃƒÂ¦Ã¢â‚¬â€œÃ‚Â¹ÃƒÂ¥Ã¯Â¿Â½Ã¢â‚¬ËœÃƒÂ§Ã‚Â¯Ã¢â‚¬Å¾ÃƒÂ¥Ã¢â‚¬ÂºÃ‚Â²ÃƒÂ¦Ã¢â‚¬ï¿½Ã‚Â»ÃƒÂ¦Ã¢â‚¬â„¢Ã†â€™ÃƒÂ©Ã‚Â­Ã¢â‚¬ï¿½ÃƒÂ¦Ã‚Â³Ã¢â‚¬Â¢
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
            else if (_skill.getTarget().equals("none") && (_skill.getType() == L1Skills.TYPE_ATTACK)) { // ÃƒÂ§Ã¢â‚¬Å¾Ã‚Â¡ÃƒÂ¦Ã¢â‚¬â€œÃ‚Â¹ÃƒÂ¥Ã¯Â¿Â½Ã¢â‚¬ËœÃƒÂ§Ã‚Â¯Ã¢â‚¬Å¾ÃƒÂ¥Ã¢â‚¬ÂºÃ‚Â²ÃƒÂ¦Ã¢â‚¬ï¿½Ã‚Â»ÃƒÂ¦Ã¢â‚¬â„¢Ã†â€™ÃƒÂ©Ã‚Â­Ã¢â‚¬ï¿½ÃƒÂ¦Ã‚Â³Ã¢â‚¬Â¢
                L1Character[] cha = new L1Character[_targetList.size()];
                int i = 0;
                for (TargetStatus ts : _targetList) {
                    cha[i] = ts.getTarget();
                    i++;
                }
                _user.broadcastPacket(new S_RangeSkill(_user, cha, _gfxid, _actid, S_RangeSkill.TYPE_NODIR));
            }
            else { // ÃƒÂ¨Ã‚Â£Ã…â€œÃƒÂ¥Ã… Ã‚Â©ÃƒÂ©Ã‚Â­Ã¢â‚¬ï¿½ÃƒÂ¦Ã‚Â³Ã¢â‚¬Â¢
                // ÃƒÂ£Ã†â€™Ã¢â‚¬ ÃƒÂ£Ã†â€™Ã‚Â¬ÃƒÂ£Ã†â€™Ã¯Â¿Â½ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã†â€™Ã‹â€ ÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ£Ã†â€™Ã…Â¾ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¹ÃƒÂ£Ã†â€™Ã¢â‚¬ ÃƒÂ£Ã†â€™Ã‚Â¬ÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ£Ã†â€™Ã¢â‚¬ ÃƒÂ£Ã†â€™Ã‚Â¬ÃƒÂ£Ã†â€™Ã¯Â¿Â½ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã†â€™Ã‹â€ ÃƒÂ£Ã†â€™Ã‹â€ ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¥ÃƒÂ£Ã†â€™Ã…Â¾ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¶ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ¤Ã‚Â»Ã‚Â¥ÃƒÂ¥Ã‚Â¤Ã¢â‚¬â€œ
                if ((_skillId != 5) && (_skillId != 69) && (_skillId != 131)) {
                    // ÃƒÂ©Ã‚Â­Ã¢â‚¬ï¿½ÃƒÂ¦Ã‚Â³Ã¢â‚¬Â¢ÃƒÂ£Ã¢â‚¬Å¡Ã¢â‚¬â„¢ÃƒÂ¤Ã‚Â½Ã‚Â¿ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬ ÃƒÂ¥Ã¢â‚¬Â¹Ã¢â‚¬Â¢ÃƒÂ¤Ã‚Â½Ã…â€œÃƒÂ£Ã¯Â¿Â½Ã‚Â®ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¨ÃƒÂ£Ã†â€™Ã¢â‚¬Â¢ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â§ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¯ÃƒÂ£Ã†â€™Ã‹â€ ÃƒÂ£Ã¯Â¿Â½Ã‚Â¯ÃƒÂ¤Ã‚Â½Ã‚Â¿ÃƒÂ§Ã¢â‚¬ï¿½Ã‚Â¨ÃƒÂ¨Ã¢â€šÂ¬Ã¢â‚¬Â¦ÃƒÂ£Ã¯Â¿Â½Ã‚ ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬Ëœ
                    S_DoActionGFX gfx = new S_DoActionGFX(_user.getId(), _actid);
                    _user.broadcastPacket(gfx);
                    _user.broadcastPacket(new S_SkillSound(targetid, _gfxid));
                }
            }
        }
    }

    /** ÃƒÂ¥Ã‹â€ Ã‚ÂªÃƒÂ©Ã¢â€žÂ¢Ã‚Â¤ÃƒÂ©Ã¢â‚¬Â¡Ã¯Â¿Â½ÃƒÂ¨Ã‚Â¤Ã¢â‚¬Â¡ÃƒÂ§Ã…Â¡Ã¢â‚¬Å¾ÃƒÂ©Ã‚Â­Ã¢â‚¬ï¿½ÃƒÂ¦Ã‚Â³Ã¢â‚¬Â¢ÃƒÂ§Ã¢â‚¬Â¹Ã¢â€šÂ¬ÃƒÂ¦Ã¢â‚¬Â¦Ã¢â‚¬Â¹ */
    private void deleteRepeatedSkills(L1Character cha) {
        final int[][] repeatedSkills =
                {

                        // ÃƒÂ§Ã¯Â¿Â½Ã‚Â«ÃƒÂ§Ã¢â‚¬Å¾Ã‚Â°ÃƒÂ¦Ã‚Â­Ã‚Â¦ÃƒÂ¥Ã¢â€žÂ¢Ã‚Â¨ÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ©Ã‚Â¢Ã‚Â¨ÃƒÂ¤Ã‚Â¹Ã¢â‚¬Â¹ÃƒÂ§Ã‚Â¥Ã…Â¾ÃƒÂ¥Ã‚Â°Ã¢â‚¬Å¾ÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ§Ã†â€™Ã‹â€ ÃƒÂ§Ã¢â‚¬Å¡Ã…Â½ÃƒÂ¦Ã‚Â°Ã‚Â£ÃƒÂ¦Ã¯Â¿Â½Ã‚Â¯ÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ¦Ã…Â¡Ã‚Â´ÃƒÂ©Ã‚Â¢Ã‚Â¨ÃƒÂ¤Ã‚Â¹Ã¢â‚¬Â¹ÃƒÂ§Ã…â€œÃ‚Â¼ÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ§Ã†â€™Ã‹â€ ÃƒÂ§Ã¢â‚¬Å¡Ã…Â½ÃƒÂ¦Ã‚Â­Ã‚Â¦ÃƒÂ¥Ã¢â€žÂ¢Ã‚Â¨ÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ¦Ã…Â¡Ã‚Â´ÃƒÂ©Ã‚Â¢Ã‚Â¨ÃƒÂ§Ã‚Â¥Ã…Â¾ÃƒÂ¥Ã‚Â°Ã¢â‚¬Å¾ÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ¥Ã‚ÂªÃ‚Â½ÃƒÂ§Ã‚Â¥Ã¢â‚¬â€œÃƒÂ§Ã…Â¡Ã¢â‚¬Å¾ÃƒÂ§Ã‚Â¥Ã¯Â¿Â½ÃƒÂ§Ã‚Â¦Ã¯Â¿Â½
                        { FIRE_WEAPON, WIND_SHOT, FIRE_BLESS, STORM_EYE, BURNING_WEAPON, STORM_SHOT, EFFECT_BLESS_OF_MAZU },
                        // ÃƒÂ©Ã‹Å“Ã‚Â²ÃƒÂ¨Ã‚Â­Ã‚Â·ÃƒÂ§Ã‚Â½Ã‚Â©ÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ¥Ã‚Â½Ã‚Â±ÃƒÂ¤Ã‚Â¹Ã¢â‚¬Â¹ÃƒÂ©Ã‹Å“Ã‚Â²ÃƒÂ¨Ã‚Â­Ã‚Â·ÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ¥Ã‚Â¤Ã‚Â§ÃƒÂ¥Ã…â€œÃ‚Â°ÃƒÂ©Ã‹Å“Ã‚Â²ÃƒÂ¨Ã‚Â­Ã‚Â·ÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ¥Ã‚Â¤Ã‚Â§ÃƒÂ¥Ã…â€œÃ‚Â°ÃƒÂ§Ã…Â¡Ã¢â‚¬Å¾ÃƒÂ§Ã‚Â¥Ã¯Â¿Â½ÃƒÂ§Ã‚Â¦Ã¯Â¿Â½ÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ©Ã¢â‚¬Â¹Ã‚Â¼ÃƒÂ©Ã¯Â¿Â½Ã‚ÂµÃƒÂ©Ã‹Å“Ã‚Â²ÃƒÂ¨Ã‚Â­Ã‚Â·
                        { SHIELD, SHADOW_ARMOR, EARTH_SKIN, EARTH_BLESS, IRON_SKIN },
                        // ÃƒÂ¥Ã¢â‚¬Â¹Ã¢â‚¬Â¡ÃƒÂ¦Ã¢â‚¬Â¢Ã‚Â¢ÃƒÂ¨Ã¢â‚¬â€�Ã‚Â¥ÃƒÂ¦Ã‚Â°Ã‚Â´ÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ§Ã‚Â²Ã‚Â¾ÃƒÂ©Ã¯Â¿Â½Ã‹â€ ÃƒÂ©Ã‚Â¤Ã¢â‚¬Â¦ÃƒÂ¤Ã‚Â¹Ã‚Â¾ÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½(ÃƒÂ§Ã‚Â¥Ã…Â¾ÃƒÂ¨Ã¯Â¿Â½Ã¢â‚¬â€œÃƒÂ§Ã¢â‚¬â€œÃ‚Â¾ÃƒÂ¨Ã‚ÂµÃ‚Â°ÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ¨Ã‚Â¡Ã…â€™ÃƒÂ¨Ã‚ÂµÃ‚Â°ÃƒÂ¥Ã… Ã‚ ÃƒÂ©Ã¢â€šÂ¬Ã…Â¸ÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ©Ã‚Â¢Ã‚Â¨ÃƒÂ¤Ã‚Â¹Ã¢â‚¬Â¹ÃƒÂ§Ã¢â‚¬â€œÃ‚Â¾ÃƒÂ¨Ã‚ÂµÃ‚Â°)ÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ¨Ã‚Â¶Ã¢â‚¬Â¦ÃƒÂ§Ã‚Â´Ã…Â¡ÃƒÂ¥Ã… Ã‚ ÃƒÂ©Ã¢â€šÂ¬Ã…Â¸ÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ¨Ã‚Â¡Ã¢â€šÂ¬ÃƒÂ¤Ã‚Â¹Ã¢â‚¬Â¹ÃƒÂ¦Ã‚Â¸Ã‚Â´ÃƒÂ¦Ã…â€œÃ¢â‚¬Âº
                        { STATUS_BRAVE, STATUS_ELFBRAVE, HOLY_WALK, MOVING_ACCELERATION, WIND_WALK, STATUS_BRAVE2, BLOODLUST },
                        // ÃƒÂ¥Ã… Ã‚ ÃƒÂ©Ã¢â€šÂ¬Ã…Â¸ÃƒÂ¨Ã‚Â¡Ã¢â‚¬Å“ÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ¥Ã‚Â¼Ã‚Â·ÃƒÂ¥Ã… Ã¢â‚¬ÂºÃƒÂ¥Ã… Ã‚ ÃƒÂ©Ã¢â€šÂ¬Ã…Â¸ÃƒÂ¨Ã‚Â¡Ã¢â‚¬Å“ÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ¨Ã¢â‚¬Â¡Ã‚ÂªÃƒÂ¦Ã‹â€ Ã¢â‚¬ËœÃƒÂ¥Ã… Ã‚ ÃƒÂ©Ã¢â€šÂ¬Ã…Â¸ÃƒÂ¨Ã¢â‚¬â€�Ã‚Â¥ÃƒÂ¦Ã‚Â°Ã‚Â´
                        { HASTE, GREATER_HASTE, STATUS_HASTE },
                        // ÃƒÂ§Ã‚Â·Ã‚Â©ÃƒÂ©Ã¢â€šÂ¬Ã…Â¸ÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ©Ã¢â‚¬ÂºÃ¢â‚¬ ÃƒÂ©Ã‚Â«Ã¢â‚¬ï¿½ÃƒÂ§Ã‚Â·Ã‚Â©ÃƒÂ¨Ã‚Â¡Ã¢â‚¬Å“ÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ¥Ã…â€œÃ‚Â°ÃƒÂ©Ã¯Â¿Â½Ã‚Â¢ÃƒÂ©Ã…Â¡Ã…â€œÃƒÂ§Ã‚Â¤Ã¢â€žÂ¢
                        { SLOW , MASS_SLOW , ENTANGLE },
                        // ÃƒÂ©Ã¢â€šÂ¬Ã…Â¡ÃƒÂ¦Ã…Â¡Ã‚Â¢ÃƒÂ¦Ã‚Â°Ã‚Â£ÃƒÂ¨Ã¢â‚¬Å¾Ã‹â€ ÃƒÂ¨Ã‚Â¡Ã¢â‚¬Å“ÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ¦Ã¢â‚¬Â¢Ã¯Â¿Â½ÃƒÂ¦Ã¯Â¿Â½Ã‚Â·ÃƒÂ¦Ã¯Â¿Â½Ã¯Â¿Â½ÃƒÂ¥Ã¯Â¿Â½Ã¢â‚¬Â¡
                        { PHYSICAL_ENCHANT_DEX, DRESS_DEXTERITY },
                        // ÃƒÂ©Ã‚Â«Ã¢â‚¬ï¿½ÃƒÂ©Ã‚Â­Ã¢â‚¬Å¾ÃƒÂ¥Ã‚Â¼Ã‚Â·ÃƒÂ¥Ã¯Â¿Â½Ã‚Â¥ÃƒÂ¨Ã‚Â¡Ã¢â‚¬Å“ÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ¥Ã… Ã¢â‚¬ÂºÃƒÂ©Ã¢â‚¬Â¡Ã¯Â¿Â½ÃƒÂ¦Ã¯Â¿Â½Ã¯Â¿Â½ÃƒÂ¥Ã¯Â¿Â½Ã¢â‚¬Â¡
                        { PHYSICAL_ENCHANT_STR, DRESS_MIGHTY },
                        // ÃƒÂ¦Ã‚Â¿Ã¢â€šÂ¬ÃƒÂ¥Ã¢â‚¬Â¹Ã‚ÂµÃƒÂ¥Ã‚Â£Ã‚Â«ÃƒÂ¦Ã‚Â°Ã‚Â£ÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ©Ã¢â‚¬Â¹Ã‚Â¼ÃƒÂ©Ã¯Â¿Â½Ã‚ÂµÃƒÂ¥Ã‚Â£Ã‚Â«ÃƒÂ¦Ã‚Â°Ã‚Â£
                        { GLOWING_AURA, SHINING_AURA },
                        // ÃƒÂ©Ã¯Â¿Â½Ã‚Â¡ÃƒÂ¥Ã†â€™Ã¯Â¿Â½ÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ¦Ã…Â¡Ã¢â‚¬â€�ÃƒÂ¥Ã‚Â½Ã‚Â±ÃƒÂ©Ã¢â‚¬â€œÃ†â€™ÃƒÂ©Ã¯Â¿Â½Ã‚Â¿
                        { MIRROR_IMAGE, UNCANNY_DODGE } };


        for (int[] skills : repeatedSkills) {
            for (int id : skills) {
                if (id == _skillId) {
                    stopSkillList(cha, skills);
                }
            }
        }
    }

    // ÃƒÂ©Ã¢â‚¬Â¡Ã¯Â¿Â½ÃƒÂ¨Ã‚Â¤Ã¢â‚¬Â¡ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬â€�ÃƒÂ£Ã¯Â¿Â½Ã‚Â¦ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬Å¾ÃƒÂ£Ã¢â‚¬Å¡Ã¢â‚¬Â¹ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¹ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â­ÃƒÂ£Ã†â€™Ã‚Â«ÃƒÂ£Ã¢â‚¬Å¡Ã¢â‚¬â„¢ÃƒÂ¤Ã‚Â¸Ã¢â€šÂ¬ÃƒÂ¦Ã¢â‚¬â€�Ã‚Â¦ÃƒÂ£Ã¯Â¿Â½Ã¢â€žÂ¢ÃƒÂ£Ã¯Â¿Â½Ã‚Â¹ÃƒÂ£Ã¯Â¿Â½Ã‚Â¦ÃƒÂ¥Ã¢â‚¬Â°Ã… ÃƒÂ©Ã¢â€žÂ¢Ã‚Â¤
    private void stopSkillList(L1Character cha, int[] repeat_skill) {
        for (int skillId : repeat_skill) {
            if (skillId != _skillId) {
                cha.removeSkillEffect(skillId);
            }
        }
    }

    // ÃƒÂ£Ã†â€™Ã¢â‚¬Â¡ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â£ÃƒÂ£Ã†â€™Ã‚Â¬ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¤ÃƒÂ£Ã¯Â¿Â½Ã‚Â®ÃƒÂ¨Ã‚Â¨Ã‚Â­ÃƒÂ¥Ã‚Â®Ã…Â¡
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
            case FIRE_WALL: // ÃƒÂ§Ã¯Â¿Â½Ã‚Â«ÃƒÂ§Ã¢â‚¬Â°Ã‚Â¢
                L1EffectSpawn.getInstance().doSpawnFireWall(_user, _targetX, _targetY);
                return;
            case TRUE_TARGET: // ÃƒÂ§Ã‚Â²Ã‚Â¾ÃƒÂ¦Ã‚ÂºÃ¢â‚¬â€œÃƒÂ§Ã¢â‚¬ÂºÃ‚Â®ÃƒÂ¦Ã‚Â¨Ã¢â€žÂ¢
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

        // ÃƒÂ©Ã‚Â­Ã¢â‚¬ï¿½ÃƒÂ¦Ã‚Â³Ã¢â‚¬Â¢ÃƒÂ¥Ã‚Â±Ã¯Â¿Â½ÃƒÂ©Ã…Â¡Ã…â€œÃƒÂ¤Ã‚Â¸Ã¯Â¿Â½ÃƒÂ¥Ã¯Â¿Â½Ã‚Â¯ÃƒÂ¦Ã… Ã‚ÂµÃƒÂ¦Ã¢â‚¬Å“Ã¢â‚¬Â¹ÃƒÂ§Ã…Â¡Ã¢â‚¬Å¾ÃƒÂ©Ã‚Â­Ã¢â‚¬ï¿½ÃƒÂ¦Ã‚Â³Ã¢â‚¬Â¢
        for (int skillId : EXCEPT_COUNTER_MAGIC) {
            if (_skillId == skillId) {
                _isCounterMagic = false;
                break;
            }
        }

        // NPCÃƒÂ£Ã¯Â¿Â½Ã‚Â«ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â·ÃƒÂ£Ã†â€™Ã‚Â§ÃƒÂ£Ã†â€™Ã†â€™ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¯ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¹ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¿ÃƒÂ£Ã†â€™Ã‚Â³ÃƒÂ£Ã¢â‚¬Å¡Ã¢â‚¬â„¢ÃƒÂ¤Ã‚Â½Ã‚Â¿ÃƒÂ§Ã¢â‚¬ï¿½Ã‚Â¨ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬Â¢ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬ÂºÃƒÂ£Ã¢â‚¬Å¡Ã¢â‚¬Â¹ÃƒÂ£Ã¯Â¿Â½Ã‚Â¨onActionÃƒÂ£Ã¯Â¿Â½Ã‚Â§NullPointerExceptionÃƒÂ£Ã¯Â¿Â½Ã…â€™ÃƒÂ§Ã¢â€žÂ¢Ã‚ÂºÃƒÂ§Ã¢â‚¬ï¿½Ã…Â¸ÃƒÂ£Ã¯Â¿Â½Ã¢â€žÂ¢ÃƒÂ£Ã¢â‚¬Å¡Ã¢â‚¬Â¹ÃƒÂ£Ã¯Â¿Â½Ã…Â¸ÃƒÂ£Ã¢â‚¬Å¡Ã¯Â¿Â½
        // ÃƒÂ£Ã¯Â¿Â½Ã‚Â¨ÃƒÂ£Ã¢â‚¬Å¡Ã… ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬Å¡ÃƒÂ£Ã¯Â¿Â½Ã‹â€ ÃƒÂ£Ã¯Â¿Â½Ã…Â¡PCÃƒÂ£Ã¯Â¿Â½Ã…â€™ÃƒÂ¤Ã‚Â½Ã‚Â¿ÃƒÂ§Ã¢â‚¬ï¿½Ã‚Â¨ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬â€�ÃƒÂ£Ã¯Â¿Â½Ã…Â¸ÃƒÂ¦Ã¢â€žÂ¢Ã¢â‚¬Å¡ÃƒÂ£Ã¯Â¿Â½Ã‚Â®ÃƒÂ£Ã¯Â¿Â½Ã‚Â¿
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
                    continue; // ÃƒÂ¨Ã‚Â¨Ã‹â€ ÃƒÂ§Ã‚Â®Ã¢â‚¬â€�ÃƒÂ£Ã¯Â¿Â½Ã¢â€žÂ¢ÃƒÂ£Ã¢â‚¬Å¡Ã¢â‚¬Â¹ÃƒÂ¥Ã‚Â¿Ã¢â‚¬Â¦ÃƒÂ¨Ã‚Â¦Ã¯Â¿Â½ÃƒÂ£Ã¯Â¿Â½Ã…â€™ÃƒÂ£Ã¯Â¿Â½Ã‚ÂªÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬Å¾ÃƒÂ£Ã¢â€šÂ¬Ã¢â‚¬Å¡
                }

                L1Magic _magic = new L1Magic(_user, cha);
                _magic.setLeverage(getLeverage());

                if (cha instanceof L1MonsterInstance) { // ÃƒÂ¤Ã‚Â¸Ã¯Â¿Â½ÃƒÂ¦Ã‚Â­Ã‚Â»ÃƒÂ¤Ã‚Â¿Ã¢â‚¬Å¡ÃƒÂ¥Ã‹â€ Ã‚Â¤ÃƒÂ¦Ã¢â‚¬â€œÃ‚Â·
                    undeadType = ((L1MonsterInstance) cha).getNpcTemplate().get_undead();
                }

                // ÃƒÂ§Ã‚Â¢Ã‚ÂºÃƒÂ§Ã…Â½Ã¢â‚¬Â¡ÃƒÂ§Ã‚Â³Ã‚Â»ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¹ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â­ÃƒÂ£Ã†â€™Ã‚Â«ÃƒÂ£Ã¯Â¿Â½Ã‚Â§ÃƒÂ¥Ã‚Â¤Ã‚Â±ÃƒÂ¦Ã¢â‚¬Â¢Ã¢â‚¬â€�ÃƒÂ£Ã¯Â¿Â½Ã…â€™ÃƒÂ§Ã‚Â¢Ã‚ÂºÃƒÂ¥Ã‚Â®Ã…Â¡ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬â€�ÃƒÂ£Ã¯Â¿Â½Ã‚Â¦ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬Å¾ÃƒÂ£Ã¢â‚¬Å¡Ã¢â‚¬Â¹ÃƒÂ¥Ã‚ Ã‚Â´ÃƒÂ¥Ã¯Â¿Â½Ã‹â€
                if (((_skill.getType() == L1Skills.TYPE_CURSE) || (_skill.getType() == L1Skills.TYPE_PROBABILITY)) && isTargetFailure(cha)) {
                    iter.remove();
                    continue;
                }

                if (cha instanceof L1PcInstance) { // ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¿ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â²ÃƒÂ£Ã†â€™Ã†â€™ÃƒÂ£Ã†â€™Ã‹â€ ÃƒÂ£Ã¯Â¿Â½Ã…â€™PCÃƒÂ£Ã¯Â¿Â½Ã‚Â®ÃƒÂ¥Ã‚ Ã‚Â´ÃƒÂ¥Ã¯Â¿Â½Ã‹â€ ÃƒÂ£Ã¯Â¿Â½Ã‚Â®ÃƒÂ£Ã¯Â¿Â½Ã‚Â¿ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¢ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¤ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â³ÃƒÂ£Ã†â€™Ã‚Â³ÃƒÂ£Ã¯Â¿Â½Ã‚Â¯ÃƒÂ©Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ¤Ã‚Â¿Ã‚Â¡ÃƒÂ£Ã¯Â¿Â½Ã¢â€žÂ¢ÃƒÂ£Ã¢â‚¬Å¡Ã¢â‚¬Â¹ÃƒÂ£Ã¢â€šÂ¬Ã¢â‚¬Å¡
                    if (_skillTime == 0) {
                        _getBuffIconDuration = _skill.getBuffDuration(); // ÃƒÂ¥Ã… Ã‚Â¹ÃƒÂ¦Ã…Â¾Ã…â€œÃƒÂ¦Ã¢â€žÂ¢Ã¢â‚¬Å¡ÃƒÂ©Ã¢â‚¬â€œÃ¢â‚¬Å“
                    }
                    else {
                        _getBuffIconDuration = _skillTime; // ÃƒÂ£Ã†â€™Ã¢â‚¬ËœÃƒÂ£Ã†â€™Ã‚Â©ÃƒÂ£Ã†â€™Ã‚Â¡ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¿ÃƒÂ£Ã¯Â¿Â½Ã‚Â®timeÃƒÂ£Ã¯Â¿Â½Ã…â€™0ÃƒÂ¤Ã‚Â»Ã‚Â¥ÃƒÂ¥Ã‚Â¤Ã¢â‚¬â€œÃƒÂ£Ã¯Â¿Â½Ã‚ÂªÃƒÂ£Ã¢â‚¬Å¡Ã¢â‚¬Â°ÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ¥Ã… Ã‚Â¹ÃƒÂ¦Ã…Â¾Ã…â€œÃƒÂ¦Ã¢â€žÂ¢Ã¢â‚¬Å¡ÃƒÂ©Ã¢â‚¬â€œÃ¢â‚¬Å“ÃƒÂ£Ã¯Â¿Â½Ã‚Â¨ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬â€�ÃƒÂ£Ã¯Â¿Â½Ã‚Â¦ÃƒÂ¨Ã‚Â¨Ã‚Â­ÃƒÂ¥Ã‚Â®Ã…Â¡ÃƒÂ£Ã¯Â¿Â½Ã¢â€žÂ¢ÃƒÂ£Ã¢â‚¬Å¡Ã¢â‚¬Â¹
                    }
                }

                deleteRepeatedSkills(cha); // ÃƒÂ¥Ã‹â€ Ã‚ÂªÃƒÂ©Ã¢â€žÂ¢Ã‚Â¤ÃƒÂ§Ã¢â‚¬Å¾Ã‚Â¡ÃƒÂ¦Ã‚Â³Ã¢â‚¬Â¢ÃƒÂ¥Ã¢â‚¬Â¦Ã‚Â±ÃƒÂ¥Ã¯Â¿Â½Ã…â€™ÃƒÂ¥Ã‚Â­Ã‹Å“ÃƒÂ¥Ã…â€œÃ‚Â¨ÃƒÂ§Ã…Â¡Ã¢â‚¬Å¾ÃƒÂ©Ã‚Â­Ã¢â‚¬ï¿½ÃƒÂ¦Ã‚Â³Ã¢â‚¬Â¢ÃƒÂ§Ã¢â‚¬Â¹Ã¢â€šÂ¬ÃƒÂ¦Ã¢â‚¬Â¦Ã¢â‚¬Â¹

                if ((_skill.getType() == L1Skills.TYPE_ATTACK) && (_user.getId() != cha.getId())) { // ÃƒÂ¦Ã¢â‚¬ï¿½Ã‚Â»ÃƒÂ¦Ã¢â‚¬â„¢Ã†â€™ÃƒÂ§Ã‚Â³Ã‚Â»ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¹ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â­ÃƒÂ£Ã†â€™Ã‚Â«ÃƒÂ¯Ã‚Â¼Ã¢â‚¬ ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¿ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â²ÃƒÂ£Ã†â€™Ã†â€™ÃƒÂ£Ã†â€™Ã‹â€ ÃƒÂ£Ã¯Â¿Â½Ã…â€™ÃƒÂ¤Ã‚Â½Ã‚Â¿ÃƒÂ§Ã¢â‚¬ï¿½Ã‚Â¨ÃƒÂ¨Ã¢â€šÂ¬Ã¢â‚¬Â¦ÃƒÂ¤Ã‚Â»Ã‚Â¥ÃƒÂ¥Ã‚Â¤Ã¢â‚¬â€œÃƒÂ£Ã¯Â¿Â½Ã‚Â§ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬Å¡ÃƒÂ£Ã¢â‚¬Å¡Ã¢â‚¬Â¹ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬Å“ÃƒÂ£Ã¯Â¿Â½Ã‚Â¨ÃƒÂ£Ã¢â€šÂ¬Ã¢â‚¬Å¡
                    if (isUseCounterMagic(cha)) { // ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â«ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¦ÃƒÂ£Ã†â€™Ã‚Â³ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¿ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã†â€™Ã…Â¾ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¸ÃƒÂ£Ã†â€™Ã†â€™ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¯ÃƒÂ£Ã¯Â¿Â½Ã…â€™ÃƒÂ§Ã¢â€žÂ¢Ã‚ÂºÃƒÂ¥Ã¢â‚¬Â¹Ã¢â‚¬Â¢ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬â€�ÃƒÂ£Ã¯Â¿Â½Ã…Â¸ÃƒÂ¥Ã‚ Ã‚Â´ÃƒÂ¥Ã¯Â¿Â½Ã‹â€ ÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ£Ã†â€™Ã‚ÂªÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¹ÃƒÂ£Ã†â€™Ã‹â€ ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬Â¹ÃƒÂ£Ã¢â‚¬Å¡Ã¢â‚¬Â°ÃƒÂ¥Ã¢â‚¬Â°Ã… ÃƒÂ©Ã¢â€žÂ¢Ã‚Â¤
                        iter.remove();
                        continue;
                    }
                    dmg = _magic.calcMagicDamage(_skillId);
                    _dmg = dmg;

                    // Triple Arrow and FOE Slayer should not cancel erase - [Hank]
                    if((_skillId != TRIPLE_ARROW) && (_skillId != FOE_SLAYER))
                    {
                        cha.removeSkillEffect(ERASE_MAGIC); // ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¤ÃƒÂ£Ã†â€™Ã‚Â¬ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¹ÃƒÂ£Ã†â€™Ã…Â¾ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¸ÃƒÂ£Ã†â€™Ã†â€™ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¯ÃƒÂ¤Ã‚Â¸Ã‚Â­ÃƒÂ£Ã¯Â¿Â½Ã‚ÂªÃƒÂ£Ã¢â‚¬Å¡Ã¢â‚¬Â°ÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ¦Ã¢â‚¬ï¿½Ã‚Â»ÃƒÂ¦Ã¢â‚¬â„¢Ã†â€™ÃƒÂ©Ã‚Â­Ã¢â‚¬ï¿½ÃƒÂ¦Ã‚Â³Ã¢â‚¬Â¢ÃƒÂ£Ã¯Â¿Â½Ã‚Â§ÃƒÂ¨Ã‚Â§Ã‚Â£ÃƒÂ©Ã¢â€žÂ¢Ã‚Â¤

                    }
                }
                else if ((_skill.getType() == L1Skills.TYPE_CURSE) || (_skill.getType() == L1Skills.TYPE_PROBABILITY)) { // ÃƒÂ§Ã‚Â¢Ã‚ÂºÃƒÂ§Ã…Â½Ã¢â‚¬Â¡ÃƒÂ§Ã‚Â³Ã‚Â»ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¹ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â­ÃƒÂ£Ã†â€™Ã‚Â«
                    isSuccess = _magic.calcProbabilityMagic(_skillId);
                    if (_skillId != ERASE_MAGIC) {
                        cha.removeSkillEffect(ERASE_MAGIC); // ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¤ÃƒÂ£Ã†â€™Ã‚Â¬ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¹ÃƒÂ£Ã†â€™Ã…Â¾ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¸ÃƒÂ£Ã†â€™Ã†â€™ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¯ÃƒÂ¤Ã‚Â¸Ã‚Â­ÃƒÂ£Ã¯Â¿Â½Ã‚ÂªÃƒÂ£Ã¢â‚¬Å¡Ã¢â‚¬Â°ÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ§Ã‚Â¢Ã‚ÂºÃƒÂ§Ã…Â½Ã¢â‚¬Â¡ÃƒÂ©Ã‚Â­Ã¢â‚¬ï¿½ÃƒÂ¦Ã‚Â³Ã¢â‚¬Â¢ÃƒÂ£Ã¯Â¿Â½Ã‚Â§ÃƒÂ¨Ã‚Â§Ã‚Â£ÃƒÂ©Ã¢â€žÂ¢Ã‚Â¤
                    }
                    if (_skillId != FOG_OF_SLEEPING) {
                        cha.removeSkillEffect(FOG_OF_SLEEPING); // ÃƒÂ£Ã†â€™Ã¢â‚¬Â¢ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â©ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â°ÃƒÂ£Ã¢â‚¬Å¡Ã‚ÂªÃƒÂ£Ã†â€™Ã¢â‚¬â€œÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¹ÃƒÂ£Ã†â€™Ã‚ÂªÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã†â€™Ã¢â‚¬ï¿½ÃƒÂ£Ã†â€™Ã‚Â³ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â°ÃƒÂ¤Ã‚Â¸Ã‚Â­ÃƒÂ£Ã¯Â¿Â½Ã‚ÂªÃƒÂ£Ã¢â‚¬Å¡Ã¢â‚¬Â°ÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ§Ã‚Â¢Ã‚ÂºÃƒÂ§Ã…Â½Ã¢â‚¬Â¡ÃƒÂ©Ã‚Â­Ã¢â‚¬ï¿½ÃƒÂ¦Ã‚Â³Ã¢â‚¬Â¢ÃƒÂ£Ã¯Â¿Â½Ã‚Â§ÃƒÂ¨Ã‚Â§Ã‚Â£ÃƒÂ©Ã¢â€žÂ¢Ã‚Â¤
                    }
                    if (isSuccess) { // ÃƒÂ¦Ã‹â€ Ã¯Â¿Â½ÃƒÂ¥Ã… Ã…Â¸ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬â€�ÃƒÂ£Ã¯Â¿Â½Ã…Â¸ÃƒÂ£Ã¯Â¿Â½Ã…â€™ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â«ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¦ÃƒÂ£Ã†â€™Ã‚Â³ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¿ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã†â€™Ã…Â¾ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¸ÃƒÂ£Ã†â€™Ã†â€™ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¯ÃƒÂ£Ã¯Â¿Â½Ã…â€™ÃƒÂ§Ã¢â€žÂ¢Ã‚ÂºÃƒÂ¥Ã¢â‚¬Â¹Ã¢â‚¬Â¢ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬â€�ÃƒÂ£Ã¯Â¿Â½Ã…Â¸ÃƒÂ¥Ã‚ Ã‚Â´ÃƒÂ¥Ã¯Â¿Â½Ã‹â€ ÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ£Ã†â€™Ã‚ÂªÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¹ÃƒÂ£Ã†â€™Ã‹â€ ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬Â¹ÃƒÂ£Ã¢â‚¬Å¡Ã¢â‚¬Â°ÃƒÂ¥Ã¢â‚¬Â°Ã… ÃƒÂ©Ã¢â€žÂ¢Ã‚Â¤
                        if (isUseCounterMagic(cha)) { // ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â«ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¦ÃƒÂ£Ã†â€™Ã‚Â³ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¿ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã†â€™Ã…Â¾ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¸ÃƒÂ£Ã†â€™Ã†â€™ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¯ÃƒÂ£Ã¯Â¿Â½Ã…â€™ÃƒÂ§Ã¢â€žÂ¢Ã‚ÂºÃƒÂ¥Ã¢â‚¬Â¹Ã¢â‚¬Â¢ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬â€�ÃƒÂ£Ã¯Â¿Â½Ã…Â¸ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬Â¹
                            iter.remove();
                            continue;
                        }
                    }
                    else {
                        // adding Phantasm effect - [Hank]
                        if (((_skillId == PHANTASM ) ||(_skillId == FOG_OF_SLEEPING)) && (cha instanceof L1PcInstance)) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.sendPackets(new S_ServerMessage(297)); // Ã©â€ºÂ¿Ã®ÂºÅ¸Ã¯Â¿Â½Ã®Â¸â€žÃ¦Â­Â»Ã©Ë†Â­Ã®Â®ï¿½Ã¥â€¡ï¿½Ã¯Â¿Â½Ã®Â¯ÂµÃ¯Â¿Â½Ã¯Â¿Â½Ã¯Å½Ë†Ã®Â¯Â®Ã¯Â¿Â½Ã¯Â¿Â½
                        }
                        iter.remove();
                        continue;
                    }
                }
                // ÃƒÂ¦Ã‚Â²Ã‚Â»ÃƒÂ§Ã¢â€žÂ¢Ã¢â‚¬â„¢ÃƒÂ¦Ã¢â€šÂ¬Ã‚Â§ÃƒÂ©Ã‚Â­Ã¢â‚¬ï¿½ÃƒÂ¦Ã‚Â³Ã¢â‚¬Â¢
                else if (_skill.getType() == L1Skills.TYPE_HEAL) {
                    // ÃƒÂ¥Ã¢â‚¬ÂºÃ…Â¾ÃƒÂ¥Ã‚Â¾Ã‚Â©ÃƒÂ©Ã¢â‚¬Â¡Ã¯Â¿Â½
                    dmg = -1 * _magic.calcHealing(_skillId);
                    if (cha.hasSkillEffect(WATER_LIFE)) { // ÃƒÂ¦Ã‚Â°Ã‚Â´ÃƒÂ¤Ã‚Â¹Ã¢â‚¬Â¹ÃƒÂ¥Ã¢â‚¬Â¦Ã†â€™ÃƒÂ¦Ã‚Â°Ã‚Â£-ÃƒÂ¦Ã¢â‚¬Â¢Ã‹â€ ÃƒÂ¦Ã…Â¾Ã…â€œ 2ÃƒÂ¥Ã¢â€šÂ¬Ã¯Â¿Â½
                        dmg *= 2;
                        cha.killSkillEffectTimer(WATER_LIFE); // ÃƒÂ¦Ã¢â‚¬Â¢Ã‹â€ ÃƒÂ¦Ã…Â¾Ã…â€œÃƒÂ¥Ã¯Â¿Â½Ã‚ÂªÃƒÂ¦Ã…â€œÃ¢â‚¬Â°ÃƒÂ¤Ã‚Â¸Ã¢â€šÂ¬ÃƒÂ¦Ã‚Â¬Ã‚Â¡
                        if (cha instanceof L1PcInstance) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.sendPackets(new S_SkillIconWaterLife());
                        }
                    }
                    if (cha.hasSkillEffect(POLLUTE_WATER)) { // ÃƒÂ¦Ã‚Â±Ã¢â€žÂ¢ÃƒÂ¦Ã‚Â¿Ã¯Â¿Â½ÃƒÂ¤Ã‚Â¹Ã¢â‚¬Â¹ÃƒÂ¦Ã‚Â°Ã‚Â´-ÃƒÂ¦Ã¢â‚¬Â¢Ã‹â€ ÃƒÂ¦Ã…Â¾Ã…â€œÃƒÂ¦Ã‚Â¸Ã¢â‚¬ÂºÃƒÂ¥Ã¯Â¿Â½Ã…
                        dmg /= 2;
                    }
                }
                // ÃƒÂ©Ã‚Â¡Ã‚Â¯ÃƒÂ§Ã‚Â¤Ã‚ÂºÃƒÂ¥Ã…â€œÃ‹Å“ÃƒÂ©Ã‚Â«Ã¢â‚¬ï¿½ÃƒÂ©Ã‚Â­Ã¢â‚¬ï¿½ÃƒÂ¦Ã‚Â³Ã¢â‚¬Â¢ÃƒÂ¦Ã¢â‚¬Â¢Ã‹â€ ÃƒÂ¦Ã…Â¾Ã…â€œÃƒÂ¥Ã…â€œÃ‚Â¨ÃƒÂ©Ã…Â¡Ã… ÃƒÂ¥Ã¯Â¿Â½Ã¢â‚¬Â¹ÃƒÂ¦Ã‹â€ Ã¢â‚¬â€œÃƒÂ§Ã¢â‚¬ÂºÃ…Â¸ÃƒÂ¥Ã¯Â¿Â½Ã¢â‚¬Â¹
                else if ((_skillId == FIRE_BLESS || _skillId == STORM_EYE // ÃƒÂ§Ã†â€™Ã‹â€ ÃƒÂ§Ã¢â‚¬Å¡Ã…Â½ÃƒÂ¦Ã‚Â°Ã‚Â£ÃƒÂ¦Ã¯Â¿Â½Ã‚Â¯ÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ¦Ã…Â¡Ã‚Â´ÃƒÂ©Ã‚Â¢Ã‚Â¨ÃƒÂ¤Ã‚Â¹Ã¢â‚¬Â¹ÃƒÂ§Ã…â€œÃ‚Â¼
                        || _skillId == EARTH_BLESS // ÃƒÂ¥Ã‚Â¤Ã‚Â§ÃƒÂ¥Ã…â€œÃ‚Â°ÃƒÂ§Ã…Â¡Ã¢â‚¬Å¾ÃƒÂ§Ã‚Â¥Ã¯Â¿Â½ÃƒÂ§Ã‚Â¦Ã¯Â¿Â½
                        || _skillId == GLOWING_AURA // ÃƒÂ¦Ã‚Â¿Ã¢â€šÂ¬ÃƒÂ¥Ã¢â‚¬Â¹Ã‚ÂµÃƒÂ¥Ã‚Â£Ã‚Â«ÃƒÂ¦Ã‚Â°Ã‚Â£
                        || _skillId == SHINING_AURA || _skillId == BRAVE_AURA) // ÃƒÂ©Ã¢â‚¬Â¹Ã‚Â¼ÃƒÂ©Ã¯Â¿Â½Ã‚ÂµÃƒÂ¥Ã‚Â£Ã‚Â«ÃƒÂ¦Ã‚Â°Ã‚Â£ÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ¨Ã‚Â¡Ã¯Â¿Â½ÃƒÂ¦Ã¢â‚¬Å“Ã… ÃƒÂ¥Ã‚Â£Ã‚Â«ÃƒÂ¦Ã‚Â°Ã‚Â£
                        && _user.getId() != cha.getId()) {
                    if (cha instanceof L1PcInstance) {
                        L1PcInstance _targetPc = (L1PcInstance) cha;
                        _targetPc.sendPackets(new S_SkillSound(_targetPc.getId(), _skill.getCastGfx()));
                        _targetPc.broadcastPacket(new S_SkillSound(_targetPc.getId(), _skill.getCastGfx()));
                    }
                }

                // ÃƒÂ¢Ã¢â‚¬â€œÃ‚ ÃƒÂ¢Ã¢â‚¬â€œÃ‚ ÃƒÂ¢Ã¢â‚¬â€œÃ‚ ÃƒÂ¢Ã¢â‚¬â€œÃ‚  ÃƒÂ¥Ã¢â€šÂ¬Ã¢â‚¬Â¹ÃƒÂ¥Ã‹â€ Ã‚Â¥ÃƒÂ¥Ã¢â‚¬Â¡Ã‚Â¦ÃƒÂ§Ã¯Â¿Â½Ã¢â‚¬ ÃƒÂ£Ã¯Â¿Â½Ã‚Â®ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬Å¡ÃƒÂ£Ã¢â‚¬Å¡Ã¢â‚¬Â¹ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¹ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â­ÃƒÂ£Ã†â€™Ã‚Â«ÃƒÂ£Ã¯Â¿Â½Ã‚Â®ÃƒÂ£Ã¯Â¿Â½Ã‚Â¿ÃƒÂ¦Ã¢â‚¬ÂºÃ‚Â¸ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬Å¾ÃƒÂ£Ã¯Â¿Â½Ã‚Â¦ÃƒÂ£Ã¯Â¿Â½Ã¯Â¿Â½ÃƒÂ£Ã¯Â¿Â½Ã‚ ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬Â¢ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬Å¾ÃƒÂ£Ã¢â€šÂ¬Ã¢â‚¬Å¡ ÃƒÂ¢Ã¢â‚¬â€œÃ‚ ÃƒÂ¢Ã¢â‚¬â€œÃ‚ ÃƒÂ¢Ã¢â‚¬â€œÃ‚ ÃƒÂ¢Ã¢â‚¬â€œÃ‚

                // ÃƒÂ©Ã¢â€žÂ¢Ã‚Â¤ÃƒÂ¤Ã‚ÂºÃ¢â‚¬ ÃƒÂ¨Ã‚Â¡Ã¯Â¿Â½ÃƒÂ¦Ã…Â¡Ã‹â€ ÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ©Ã‚ÂªÃ‚Â·ÃƒÂ©Ã‚Â«Ã¯Â¿Â½ÃƒÂ¦Ã‚Â¯Ã¢â€šÂ¬ÃƒÂ¥Ã‚Â£Ã…Â¾ÃƒÂ¤Ã‚Â¹Ã¢â‚¬Â¹ÃƒÂ¥Ã‚Â¤Ã¢â‚¬â€œÃƒÂ©Ã‚Â­Ã¢â‚¬ï¿½ÃƒÂ¦Ã‚Â³Ã¢â‚¬Â¢ÃƒÂ¦Ã¢â‚¬Â¢Ã‹â€ ÃƒÂ¦Ã…Â¾Ã…â€œÃƒÂ¥Ã‚Â­Ã‹Å“ÃƒÂ¥Ã…â€œÃ‚Â¨ÃƒÂ¦Ã¢â€žÂ¢Ã¢â‚¬Å¡ÃƒÂ¯Ã‚Â¼Ã…â€™ÃƒÂ¥Ã¯Â¿Â½Ã‚ÂªÃƒÂ¦Ã¢â‚¬ÂºÃ‚Â´ÃƒÂ¦Ã¢â‚¬â€œÃ‚Â°ÃƒÂ¦Ã¢â‚¬Â¢Ã‹â€ ÃƒÂ¦Ã…Â¾Ã…â€œÃƒÂ¦Ã¢â€žÂ¢Ã¢â‚¬Å¡ÃƒÂ©Ã¢â‚¬â€œÃ¢â‚¬Å“ÃƒÂ¨Ã‚Â·Ã…Â¸ÃƒÂ¥Ã…â€œÃ¢â‚¬â€œÃƒÂ§Ã‚Â¤Ã‚ÂºÃƒÂ£Ã¢â€šÂ¬Ã¢â‚¬Å¡
                if (cha.hasSkillEffect(_skillId) && (_skillId != SHOCK_STUN && _skillId != BONE_BREAK && _skillId != CONFUSION && _skillId != THUNDER_GRAB)) {
                    addMagicList(cha, true); // ÃƒÂ©Ã‚Â­Ã¢â‚¬ï¿½ÃƒÂ¦Ã‚Â³Ã¢â‚¬Â¢ÃƒÂ¦Ã¢â‚¬Â¢Ã‹â€ ÃƒÂ¦Ã…Â¾Ã…â€œÃƒÂ¥Ã‚Â·Ã‚Â²ÃƒÂ¥Ã‚Â­Ã‹Å“ÃƒÂ¥Ã…â€œÃ‚Â¨ÃƒÂ¦Ã¢â€žÂ¢Ã¢â‚¬Å¡
                    if (_skillId != SHAPE_CHANGE) { // ÃƒÂ©Ã¢â€žÂ¢Ã‚Â¤ÃƒÂ¤Ã‚ÂºÃ¢â‚¬ ÃƒÂ¨Ã‚Â®Ã… ÃƒÂ¥Ã‚Â½Ã‚Â¢ÃƒÂ¨Ã‚Â¡Ã¢â‚¬Å“ÃƒÂ¤Ã‚Â¹Ã¢â‚¬Â¹ÃƒÂ¥Ã‚Â¤Ã¢â‚¬â€œ
                        continue;
                    }
                }

                switch(_skillId) {
                    // ÃƒÂ¥Ã… Ã‚ ÃƒÂ©Ã¢â€šÂ¬Ã…Â¸ÃƒÂ¨Ã‚Â¡Ã¢â‚¬Å“
                    case HASTE:
                        if (cha.getMoveSpeed() != 2) { // ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¹ÃƒÂ£Ã†â€™Ã‚Â­ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ¤Ã‚Â¸Ã‚Â­ÃƒÂ¤Ã‚Â»Ã‚Â¥ÃƒÂ¥Ã‚Â¤Ã¢â‚¬â€œ
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
                        else { // ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¹ÃƒÂ£Ã†â€™Ã‚Â­ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ¤Ã‚Â¸Ã‚Â­
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
                    // ÃƒÂ¥Ã‚Â¼Ã‚Â·ÃƒÂ¥Ã… Ã¢â‚¬ÂºÃƒÂ¥Ã… Ã‚ ÃƒÂ©Ã¢â€šÂ¬Ã…Â¸ÃƒÂ¨Ã‚Â¡Ã¢â‚¬Å“
                    case GREATER_HASTE:
                        if (cha instanceof L1PcInstance) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            if (pc.getHasteItemEquipped() > 0) {
                                continue;
                            }
                            if (pc.getMoveSpeed() != 2) { // ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¹ÃƒÂ£Ã†â€™Ã‚Â­ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ¤Ã‚Â¸Ã‚Â­ÃƒÂ¤Ã‚Â»Ã‚Â¥ÃƒÂ¥Ã‚Â¤Ã¢â‚¬â€œ
                                pc.setDrink(false);
                                pc.setMoveSpeed(1);
                                pc.sendPackets(new S_SkillHaste(pc.getId(), 1, _getBuffIconDuration));
                                pc.broadcastPacket(new S_SkillHaste(pc.getId(), 1, 0));
                            }
                            else { // ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¹ÃƒÂ£Ã†â€™Ã‚Â­ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ¤Ã‚Â¸Ã‚Â­
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
                    // ÃƒÂ§Ã‚Â·Ã‚Â©ÃƒÂ©Ã¢â€šÂ¬Ã…Â¸ÃƒÂ¨Ã‚Â¡Ã¢â‚¬Å“ÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ©Ã¢â‚¬ÂºÃ¢â‚¬ ÃƒÂ©Ã‚Â«Ã¢â‚¬ï¿½ÃƒÂ§Ã‚Â·Ã‚Â©ÃƒÂ©Ã¢â€šÂ¬Ã…Â¸ÃƒÂ¨Ã‚Â¡Ã¢â‚¬Å“ÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ¥Ã…â€œÃ‚Â°ÃƒÂ©Ã¯Â¿Â½Ã‚Â¢ÃƒÂ©Ã…Â¡Ã…â€œÃƒÂ§Ã‚Â¤Ã¢â€žÂ¢
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
                    case CHILL_TOUCH:
                    case VAMPIRIC_TOUCH:
                        heal = dmg;
                        break;
                    // ÃƒÂ¤Ã‚ÂºÃ…Â¾ÃƒÂ¥Ã… Ã¢â‚¬ÂºÃƒÂ¥Ã‚Â®Ã¢â‚¬Â°ÃƒÂ¥Ã¢â‚¬ Ã‚Â°ÃƒÂ§Ã…Â¸Ã¢â‚¬ÂºÃƒÂ¥Ã…â€œÃ¯Â¿Â½ÃƒÂ§Ã‚Â±Ã‚Â¬
                    case ICE_LANCE_COCKATRICE:
                        // ÃƒÂ©Ã¢â‚¬Å¡Ã‚ÂªÃƒÂ¦Ã†â€™Ã‚Â¡ÃƒÂ¨Ã…â€œÃ‚Â¥ÃƒÂ¨Ã…â€œÃ‚Â´ÃƒÂ¥Ã¢â‚¬ Ã‚Â°ÃƒÂ§Ã…Â¸Ã¢â‚¬ÂºÃƒÂ¥Ã…â€œÃ¯Â¿Â½ÃƒÂ§Ã‚Â±Ã‚Â¬
                    case ICE_LANCE_BASILISK:
                        // ÃƒÂ¥Ã¢â‚¬ Ã‚Â°ÃƒÂ¦Ã‚Â¯Ã¢â‚¬ÂºÃƒÂ¥Ã…â€œÃ¯Â¿Â½ÃƒÂ§Ã‚Â±Ã‚Â¬ÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ¥Ã¢â‚¬ Ã‚Â°ÃƒÂ©Ã¢â‚¬ÂºÃ‚ÂªÃƒÂ©Ã‚Â¢Ã‚Â¶ÃƒÂ©Ã‚Â¢Ã‚Â¨ÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ¥Ã‚Â¯Ã¢â‚¬â„¢ÃƒÂ¥Ã¢â‚¬ Ã‚Â°ÃƒÂ¥Ã¢â€žÂ¢Ã‚Â´ÃƒÂ¥Ã¯Â¿Â½Ã¯Â¿Â½
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
                    // ÃƒÂ¥Ã‚Â¤Ã‚Â§ÃƒÂ¥Ã…â€œÃ‚Â°ÃƒÂ¥Ã‚Â±Ã¯Â¿Â½ÃƒÂ©Ã…Â¡Ã…â€œ
                    case EARTH_BIND:

                        // Updated earth bind duration so higher level gets some benefit. - [Hank]
                        int tarLevel = 0;
                        int levelDiff = 0;

                        // Checking the target's type - [Hank]
                        if (cha instanceof L1PcInstance) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            tarLevel = pc.getLevel();
                        } else if (cha instanceof L1MonsterInstance
                                || cha instanceof L1SummonInstance
                                || cha instanceof L1PetInstance) {
                            L1NpcInstance npc = (L1NpcInstance) cha;
                            tarLevel = npc.getLevel();
                        }
                        levelDiff = _user.getLevel() - tarLevel;
                        // If the level difference is > 5, set to 5 - [Hank]
                        if (levelDiff > 5)
                        {
                            levelDiff = 5;
                        }
                        // if level difference is negative, set to 0 - [Hank]
                        else if (levelDiff < 0)
                        {
                            levelDiff = 0;
                        }
                        Random rn = new Random();
                        //Ebind duration is base on: Random(0 - 4s) + 5(base) + level difference - [Hank]
                        _earthBindDuration = (rn.nextInt(5) + 5 + levelDiff) * 1000;


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
                    case 20011: // ÃƒÂ¦Ã‚Â¯Ã¢â‚¬â„¢ÃƒÂ©Ã…â€œÃ‚Â§-ÃƒÂ¥Ã¢â‚¬Â°Ã¯Â¿Â½ÃƒÂ¦Ã¢â‚¬â€œÃ‚Â¹ 3X3
                        _user.setHeading(_user.targetDirection(_targetX, _targetY)); // ÃƒÂ¦Ã¢â‚¬ï¿½Ã‚Â¹ÃƒÂ¨Ã‚Â®Ã… ÃƒÂ©Ã¯Â¿Â½Ã‚Â¢ÃƒÂ¥Ã¯Â¿Â½Ã¢â‚¬Ëœ
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
                    // ÃƒÂ¨Ã‚Â¡Ã¯Â¿Â½ÃƒÂ¦Ã¢â‚¬Å“Ã… ÃƒÂ¤Ã‚Â¹Ã¢â‚¬Â¹ÃƒÂ¦Ã…Â¡Ã‹â€
                    case SHOCK_STUN:
                        L1Stun.Stun(_user,_target,_skillId);
                        break;
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
                    // [Legends] BONE_BREAK
                    case BONE_BREAK:
                        L1Stun.Stun(_user,_target,_skillId);
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
                    // ÃƒÂ¨Ã‚ÂµÃ‚Â·ÃƒÂ¦Ã‚Â­Ã‚Â»ÃƒÂ¥Ã¢â‚¬ÂºÃ…Â¾ÃƒÂ§Ã¢â‚¬ï¿½Ã…Â¸ÃƒÂ¨Ã‚Â¡Ã¢â‚¬Å“
                    case TURN_UNDEAD:
                        if (undeadType == 1 || undeadType == 3){
                            dmg = cha.getCurrentHp();
                        }
                        break;
                    // [Legends] - Added for new DE skill
                    case ARMOR_BREAK:
                        try
                        {
                            if(!_user.getInventory().checkItem(DarkStone))
                            {
                                L1PcInstance pc = (L1PcInstance) _user;
                                pc.sendPackets(new S_SystemMessage("You do not have enough dark stones to cast Armor Break"));
                                return;
                            }

                            isSuccess = _magic.calcProbabilityMagic(_skillId);
                            if(isSuccess){
                                useConsume();
                                if (cha instanceof L1PcInstance) {
                                    if (!cha.hasSkillEffect(ARMOR_BREAK)) {
                                        L1PcInstance pc = (L1PcInstance) cha;
                                        pc.sendPackets(new S_SkillIconGFX(74, 3));
                                        cha.setSkillEffect(ARMOR_BREAK, 8 * 1000);
                                    }
                                }
                                else{
                                    L1PcInstance pc = (L1PcInstance) _user;
                                    pc.sendPackets(new S_ServerMessage(280));
                                }

                            }
                        }
                        catch(Exception e)
                        {
                            //Do nothing
                        }
                    case MANA_DRAIN:
                        int manachance = Random.nextInt(10) + 5;
                        drainMana = manachance + (_user.getInt() / 2);
                        if (cha.getCurrentMp() < drainMana) {
                            drainMana = cha.getCurrentMp();
                        }
                        break;
                    // ÃƒÂ¦Ã…â€™Ã¢â‚¬Â¡ÃƒÂ¥Ã‚Â®Ã…Â¡ÃƒÂ¥Ã¢â‚¬Å¡Ã‚Â³ÃƒÂ©Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ©Ã¢â‚¬ÂºÃ¢â‚¬ ÃƒÂ©Ã‚Â«Ã¢â‚¬ï¿½ÃƒÂ¥Ã¢â‚¬Å¡Ã‚Â³ÃƒÂ©Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ¨Ã‚Â¡Ã¢â‚¬Å“
                    case TELEPORT:
                    case MASS_TELEPORT:
                        if (cha instanceof L1PcInstance) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            L1BookMark bookm = pc.getBookMark(_bookmarkId);
                            if (bookm != null) { // ÃƒÂ£Ã†â€™Ã¢â‚¬â€œÃƒÂ£Ã†â€™Ã†â€™ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¯ÃƒÂ£Ã†â€™Ã…Â¾ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¯ÃƒÂ£Ã¢â‚¬Å¡Ã¢â‚¬â„¢ÃƒÂ¥Ã¯Â¿Â½Ã¢â‚¬â€œÃƒÂ¥Ã‚Â¾Ã¢â‚¬â€�ÃƒÂ¥Ã¢â‚¬Â¡Ã‚ÂºÃƒÂ¦Ã¯Â¿Â½Ã‚Â¥ÃƒÂ£Ã¯Â¿Â½Ã…Â¸ÃƒÂ£Ã¢â‚¬Å¡Ã¢â‚¬Â°ÃƒÂ£Ã†â€™Ã¢â‚¬ ÃƒÂ£Ã†â€™Ã‚Â¬ÃƒÂ£Ã†â€™Ã¯Â¿Â½ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã†â€™Ã‹â€
                                if (pc.getMap().isEscapable() || pc.isGm()) {
                                    int newX = bookm.getLocX();
                                    int newY = bookm.getLocY();
                                    short mapId = bookm.getMapId();

                                    if (_skillId == MASS_TELEPORT) { // ÃƒÂ£Ã†â€™Ã…Â¾ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¹ÃƒÂ£Ã†â€™Ã¢â‚¬ ÃƒÂ£Ã†â€™Ã‚Â¬ÃƒÂ£Ã†â€™Ã¯Â¿Â½ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã†â€™Ã‹â€
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
                            else { // ÃƒÂ£Ã†â€™Ã¢â‚¬â€œÃƒÂ£Ã†â€™Ã†â€™ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¯ÃƒÂ£Ã†â€™Ã…Â¾ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¯ÃƒÂ£Ã¯Â¿Â½Ã…â€™ÃƒÂ¥Ã¯Â¿Â½Ã¢â‚¬â€œÃƒÂ¥Ã‚Â¾Ã¢â‚¬â€�ÃƒÂ¥Ã¢â‚¬Â¡Ã‚ÂºÃƒÂ¦Ã¯Â¿Â½Ã‚Â¥ÃƒÂ£Ã¯Â¿Â½Ã‚ÂªÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬Â¹ÃƒÂ£Ã¯Â¿Â½Ã‚Â£ÃƒÂ£Ã¯Â¿Â½Ã…Â¸ÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬Å¡ÃƒÂ£Ã¢â‚¬Å¡Ã¢â‚¬Â¹ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬Å¾ÃƒÂ£Ã¯Â¿Â½Ã‚Â¯ÃƒÂ£Ã¢â€šÂ¬Ã…â€™ÃƒÂ¤Ã‚Â»Ã‚Â»ÃƒÂ¦Ã¢â‚¬Å¾Ã¯Â¿Â½ÃƒÂ£Ã¯Â¿Â½Ã‚Â®ÃƒÂ¥Ã‚ Ã‚Â´ÃƒÂ¦Ã¢â‚¬Â°Ã¢â€šÂ¬ÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ£Ã¢â‚¬Å¡Ã¢â‚¬â„¢ÃƒÂ©Ã¯Â¿Â½Ã‚Â¸ÃƒÂ¦Ã… Ã…Â¾ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬â€�ÃƒÂ£Ã¯Â¿Â½Ã…Â¸ÃƒÂ¥Ã‚ Ã‚Â´ÃƒÂ¥Ã¯Â¿Â½Ã‹â€ ÃƒÂ£Ã¯Â¿Â½Ã‚Â®ÃƒÂ¥Ã¢â‚¬Â¡Ã‚Â¦ÃƒÂ§Ã¯Â¿Â½Ã¢â‚¬
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
                                    pc.sendPackets(new S_ServerMessage(276)); // \f1ÃƒÂ¥Ã…â€œÃ‚Â¨ÃƒÂ¦Ã‚Â­Ã‚Â¤ÃƒÂ§Ã¢â‚¬Å¾Ã‚Â¡ÃƒÂ¦Ã‚Â³Ã¢â‚¬Â¢ÃƒÂ¤Ã‚Â½Ã‚Â¿ÃƒÂ§Ã¢â‚¬ï¿½Ã‚Â¨ÃƒÂ¥Ã¢â‚¬Å¡Ã‚Â³ÃƒÂ©Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ£Ã¢â€šÂ¬Ã¢â‚¬Å¡
                                    pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, true));
                                    // using tele spell in non-teleportable map will give effect of .reload
                                    L1Teleport.teleport(pc, pc.getX(), pc.getY(), pc.getMapId(), 5, false);
                                }
                            }
                        }
                        break;
                    // ÃƒÂ¥Ã¢â‚¬ËœÃ‚Â¼ÃƒÂ¥Ã¢â‚¬â€œÃ…Â¡ÃƒÂ§Ã¢â‚¬ÂºÃ…Â¸ÃƒÂ¥Ã¯Â¿Â½Ã¢â‚¬Â¹
                    case CALL_CLAN:
                        if (cha instanceof L1PcInstance) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            L1PcInstance clanPc = (L1PcInstance) L1World.getInstance().findObject(_targetID);
                            if (clanPc != null) {
                                clanPc.setTempID(pc.getId());
                                clanPc.sendPackets(new S_Message_YN(729, "")); // ÃƒÂ§Ã¢â‚¬ÂºÃ…Â¸ÃƒÂ¤Ã‚Â¸Ã‚Â»ÃƒÂ¦Ã‚Â­Ã‚Â£ÃƒÂ¥Ã…â€œÃ‚Â¨ÃƒÂ¥Ã¢â‚¬ËœÃ‚Â¼ÃƒÂ¥Ã¢â‚¬â€œÃ…Â¡ÃƒÂ¤Ã‚Â½Ã‚ ÃƒÂ¯Ã‚Â¼Ã…â€™ÃƒÂ¤Ã‚Â½Ã‚ ÃƒÂ¨Ã‚Â¦Ã¯Â¿Â½ÃƒÂ¦Ã…Â½Ã‚Â¥ÃƒÂ¥Ã¯Â¿Â½Ã¢â‚¬â€�ÃƒÂ¤Ã‚Â»Ã¢â‚¬â€œÃƒÂ§Ã…Â¡Ã¢â‚¬Å¾ÃƒÂ¥Ã¢â‚¬ËœÃ‚Â¼ÃƒÂ¥Ã¢â‚¬â€œÃ…Â¡ÃƒÂ¥Ã¢â‚¬â€�Ã…Â½ÃƒÂ¯Ã‚Â¼Ã…Â¸(Y/N)
                            }
                        }
                        break;
                    // ÃƒÂ¦Ã¯Â¿Â½Ã‚Â´ÃƒÂ¨Ã‚Â­Ã‚Â·ÃƒÂ§Ã¢â‚¬ÂºÃ…Â¸ÃƒÂ¥Ã¯Â¿Â½Ã¢â‚¬Â¹
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
                                    // ÃƒÂ©Ã¢â€šÂ¬Ã¢â€žÂ¢ÃƒÂ©Ã¢â€žÂ¢Ã¢â‚¬Å¾ÃƒÂ¨Ã‚Â¿Ã¢â‚¬ËœÃƒÂ§Ã…Â¡Ã¢â‚¬Å¾ÃƒÂ¨Ã†â€™Ã‚Â½ÃƒÂ©Ã¢â‚¬Â¡Ã¯Â¿Â½ÃƒÂ¥Ã‚Â½Ã‚Â±ÃƒÂ©Ã…Â¸Ã‚Â¿ÃƒÂ¥Ã‹â€ Ã‚Â°ÃƒÂ§Ã…Â¾Ã‚Â¬ÃƒÂ©Ã¢â‚¬â€œÃ¢â‚¬Å“ÃƒÂ§Ã‚Â§Ã‚Â»ÃƒÂ¥Ã¢â‚¬Â¹Ã¢â‚¬Â¢ÃƒÂ£Ã¢â€šÂ¬Ã¢â‚¬Å¡ÃƒÂ¥Ã…â€œÃ‚Â¨ÃƒÂ¦Ã‚Â­Ã‚Â¤ÃƒÂ¥Ã…â€œÃ‚Â°ÃƒÂ§Ã¢â‚¬Å¾Ã‚Â¡ÃƒÂ¦Ã‚Â³Ã¢â‚¬Â¢ÃƒÂ¤Ã‚Â½Ã‚Â¿ÃƒÂ§Ã¢â‚¬ï¿½Ã‚Â¨ÃƒÂ§Ã…Â¾Ã‚Â¬ÃƒÂ©Ã¢â‚¬â€œÃ¢â‚¬Å“ÃƒÂ§Ã‚Â§Ã‚Â»ÃƒÂ¥Ã¢â‚¬Â¹Ã¢â‚¬Â¢ÃƒÂ£Ã¢â€šÂ¬Ã¢â‚¬Å¡
                                    pc.sendPackets(new S_ServerMessage(647));
                                    pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, true));
                                }
                            }
                        }
                        break;
                    // ÃƒÂ¥Ã‚Â¼Ã‚Â·ÃƒÂ¥Ã… Ã¢â‚¬ÂºÃƒÂ§Ã¢â‚¬Å¾Ã‚Â¡ÃƒÂ¦Ã¢â‚¬Â°Ã¢â€šÂ¬ÃƒÂ©Ã¯Â¿Â½Ã¯Â¿Â½ÃƒÂ¥Ã‚Â½Ã‚Â¢
                    case COUNTER_DETECTION:
                        if (cha instanceof L1PcInstance) {
//                                                      if (cha.isInvisble())
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
                    // ÃƒÂ¥Ã¢â‚¬Â°Ã‚ÂµÃƒÂ©Ã¢â€šÂ¬Ã‚ ÃƒÂ©Ã‚Â­Ã¢â‚¬ï¿½ÃƒÂ¦Ã‚Â³Ã¢â‚¬Â¢ÃƒÂ¦Ã‚Â­Ã‚Â¦ÃƒÂ¥Ã¢â€žÂ¢Ã‚Â¨
                    case CREATE_MAGICAL_WEAPON:
                        if (cha instanceof L1PcInstance) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            L1ItemInstance item = pc.getInventory().getItem(_itemobjid);
                            if ((item != null) && (item.getItem().getType2() == 1)) {
                                int item_type = item.getItem().getType2();
                                int safe_enchant = item.getItem().get_safeenchant();
                                int enchant_level = item.getEnchantLevel();
                                String item_name = item.getName();
                                if (safe_enchant < 0) { // ÃƒÂ¥Ã‚Â¼Ã‚Â·ÃƒÂ¥Ã…â€™Ã¢â‚¬â€œÃƒÂ¤Ã‚Â¸Ã¯Â¿Â½ÃƒÂ¥Ã¯Â¿Â½Ã‚Â¯
                                    pc.sendPackets( // \f1ÃƒÂ¤Ã‚Â½Ã¢â‚¬Â¢ÃƒÂ£Ã¢â‚¬Å¡Ã¢â‚¬Å¡ÃƒÂ¨Ã‚ÂµÃ‚Â·ÃƒÂ£Ã¯Â¿Â½Ã¯Â¿Â½ÃƒÂ£Ã¯Â¿Â½Ã‚Â¾ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬ÂºÃƒÂ£Ã¢â‚¬Å¡Ã¢â‚¬Å“ÃƒÂ£Ã¯Â¿Â½Ã‚Â§ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬â€�ÃƒÂ£Ã¯Â¿Â½Ã…Â¸ÃƒÂ£Ã¢â€šÂ¬Ã¢â‚¬Å¡
                                            new S_ServerMessage(79));
                                }
                                else if (safe_enchant == 0) { // ÃƒÂ¥Ã‚Â®Ã¢â‚¬Â°ÃƒÂ¥Ã¢â‚¬Â¦Ã‚Â¨ÃƒÂ¥Ã…â€œÃ¯Â¿Â½+0
                                    pc.sendPackets( // \f1ÃƒÂ¤Ã‚Â½Ã¢â‚¬Â¢ÃƒÂ£Ã¢â‚¬Å¡Ã¢â‚¬Å¡ÃƒÂ¨Ã‚ÂµÃ‚Â·ÃƒÂ£Ã¯Â¿Â½Ã¯Â¿Â½ÃƒÂ£Ã¯Â¿Â½Ã‚Â¾ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬ÂºÃƒÂ£Ã¢â‚¬Å¡Ã¢â‚¬Å“ÃƒÂ£Ã¯Â¿Â½Ã‚Â§ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬â€�ÃƒÂ£Ã¯Â¿Â½Ã…Â¸ÃƒÂ£Ã¢â€šÂ¬Ã¢â‚¬Å¡
                                            new S_ServerMessage(79));
                                }
                                else if ((item_type == 1) && (enchant_level == 0)) {
                                    if (!item.isIdentified()) {// ÃƒÂ¦Ã…â€œÃ‚ÂªÃƒÂ©Ã¢â‚¬ËœÃ¢â‚¬ËœÃƒÂ¥Ã‚Â®Ã…Â¡
                                        pc.sendPackets( // \f1%0ÃƒÂ£Ã¯Â¿Â½Ã…â€™%2%1ÃƒÂ¥Ã¢â‚¬Â¦Ã¢â‚¬Â°ÃƒÂ£Ã¢â‚¬Å¡Ã… ÃƒÂ£Ã¯Â¿Â½Ã‚Â¾ÃƒÂ£Ã¯Â¿Â½Ã¢â€žÂ¢ÃƒÂ£Ã¢â€šÂ¬Ã¢â‚¬Å¡
                                                new S_ServerMessage(161, item_name, "$245", "$247"));
                                    }
                                    else {
                                        item_name = "+0 " + item_name;
                                        pc.sendPackets( // \f1%0ÃƒÂ£Ã¯Â¿Â½Ã…â€™%2%1ÃƒÂ¥Ã¢â‚¬Â¦Ã¢â‚¬Â°ÃƒÂ£Ã¢â‚¬Å¡Ã… ÃƒÂ£Ã¯Â¿Â½Ã‚Â¾ÃƒÂ£Ã¯Â¿Â½Ã¢â€žÂ¢ÃƒÂ£Ã¢â€šÂ¬Ã¢â‚¬Å¡
                                                new S_ServerMessage(161, "+0 " + item_name, "$245", "$247"));
                                    }
                                    item.setEnchantLevel(1);
                                    pc.getInventory().updateItem(item, L1PcInventory.COL_ENCHANTLVL);
                                }
                                else {
                                    pc.sendPackets( // \f1ÃƒÂ¤Ã‚Â½Ã¢â‚¬Â¢ÃƒÂ£Ã¢â‚¬Å¡Ã¢â‚¬Å¡ÃƒÂ¨Ã‚ÂµÃ‚Â·ÃƒÂ£Ã¯Â¿Â½Ã¯Â¿Â½ÃƒÂ£Ã¯Â¿Â½Ã‚Â¾ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬ÂºÃƒÂ£Ã¢â‚¬Å¡Ã¢â‚¬Å“ÃƒÂ£Ã¯Â¿Â½Ã‚Â§ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬â€�ÃƒÂ£Ã¯Â¿Â½Ã…Â¸ÃƒÂ£Ã¢â€šÂ¬Ã¢â‚¬Å¡
                                            new S_ServerMessage(79));
                                }
                            }
                            else {
                                pc.sendPackets( // \f1ÃƒÂ¤Ã‚Â½Ã¢â‚¬Â¢ÃƒÂ£Ã¢â‚¬Å¡Ã¢â‚¬Å¡ÃƒÂ¨Ã‚ÂµÃ‚Â·ÃƒÂ£Ã¯Â¿Â½Ã¯Â¿Â½ÃƒÂ£Ã¯Â¿Â½Ã‚Â¾ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬ÂºÃƒÂ£Ã¢â‚¬Å¡Ã¢â‚¬Å“ÃƒÂ£Ã¯Â¿Â½Ã‚Â§ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬â€�ÃƒÂ£Ã¯Â¿Â½Ã…Â¸ÃƒÂ£Ã¢â€šÂ¬Ã¢â‚¬Å¡
                                        new S_ServerMessage(79));
                            }
                        }
                        break;
                    // ÃƒÂ¦Ã¯Â¿Â½Ã¯Â¿Â½ÃƒÂ§Ã¢â‚¬Â¦Ã¢â‚¬Â°ÃƒÂ©Ã‚Â­Ã¢â‚¬ï¿½ÃƒÂ§Ã…Â¸Ã‚Â³
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
                                        pc.sendPackets(new S_ServerMessage(403, "$2475")); // ÃƒÂ§Ã¯Â¿Â½Ã‚Â²ÃƒÂ¥Ã‚Â¾Ã¢â‚¬â€�%0%o ÃƒÂ£Ã¢â€šÂ¬Ã¢â‚¬Å¡
                                    } else {
                                        pc.sendPackets(new S_ServerMessage(280)); // \f1ÃƒÂ¦Ã¢â‚¬â€œÃ‚Â½ÃƒÂ¥Ã¢â‚¬â„¢Ã¢â‚¬â„¢ÃƒÂ¥Ã‚Â¤Ã‚Â±ÃƒÂ¦Ã¢â‚¬Â¢Ã¢â‚¬â€�ÃƒÂ£Ã¢â€šÂ¬Ã¢â‚¬Å¡
                                    }
                                } else if (item.getItem().getItemId() == 40321) {
                                    pc.getInventory().removeItem(item, 1);
                                    if (brave >= run) {
                                        pc.getInventory().storeItem(40322, 1);
                                        pc.sendPackets(new S_ServerMessage(403, "$2476")); // ÃƒÂ§Ã¯Â¿Â½Ã‚Â²ÃƒÂ¥Ã‚Â¾Ã¢â‚¬â€�%0%o ÃƒÂ£Ã¢â€šÂ¬Ã¢â‚¬Å¡
                                    } else {
                                        pc.sendPackets(new S_ServerMessage(280));// \f1ÃƒÂ¦Ã¢â‚¬â€œÃ‚Â½ÃƒÂ¥Ã¢â‚¬â„¢Ã¢â‚¬â„¢ÃƒÂ¥Ã‚Â¤Ã‚Â±ÃƒÂ¦Ã¢â‚¬Â¢Ã¢â‚¬â€�ÃƒÂ£Ã¢â€šÂ¬Ã¢â‚¬Å¡
                                    }
                                } else if (item.getItem().getItemId() == 40322) {
                                    pc.getInventory().removeItem(item, 1);
                                    if (wise >= run) {
                                        pc.getInventory().storeItem(40323, 1);
                                        pc.sendPackets(new S_ServerMessage(403, "$2477")); // ÃƒÂ§Ã¯Â¿Â½Ã‚Â²ÃƒÂ¥Ã‚Â¾Ã¢â‚¬â€�%0%o ÃƒÂ£Ã¢â€šÂ¬Ã¢â‚¬Å¡
                                    } else {
                                        pc.sendPackets(new S_ServerMessage(280));// \f1ÃƒÂ¦Ã¢â‚¬â€œÃ‚Â½ÃƒÂ¥Ã¢â‚¬â„¢Ã¢â‚¬â„¢ÃƒÂ¥Ã‚Â¤Ã‚Â±ÃƒÂ¦Ã¢â‚¬Â¢Ã¢â‚¬â€�ÃƒÂ£Ã¢â€šÂ¬Ã¢â‚¬Å¡
                                    }
                                } else if (item.getItem().getItemId() == 40323) {
                                    pc.getInventory().removeItem(item, 1);
                                    if (kayser >= run) {
                                        pc.getInventory().storeItem(40324, 1);
                                        pc.sendPackets(new S_ServerMessage(403, "$2478")); // ÃƒÂ§Ã¯Â¿Â½Ã‚Â²ÃƒÂ¥Ã‚Â¾Ã¢â‚¬â€�%0%o ÃƒÂ£Ã¢â€šÂ¬Ã¢â‚¬Å¡
                                    } else {
                                        pc.sendPackets(new S_ServerMessage(280));// \f1ÃƒÂ¦Ã¢â‚¬â€œÃ‚Â½ÃƒÂ¥Ã¢â‚¬â„¢Ã¢â‚¬â„¢ÃƒÂ¥Ã‚Â¤Ã‚Â±ÃƒÂ¦Ã¢â‚¬Â¢Ã¢â‚¬â€�ÃƒÂ£Ã¢â€šÂ¬Ã¢â‚¬Å¡
                                    }
                                }
                            }
                        }
                        break;
                    // ÃƒÂ¦Ã¢â‚¬â€�Ã‚Â¥ÃƒÂ¥Ã¢â‚¬Â¦Ã¢â‚¬Â°ÃƒÂ¨Ã‚Â¡Ã¢â‚¬Å“
                    case LIGHT:

                        if (cha instanceof L1PcInstance) {

                        }
                        break;
                    // ÃƒÂ¦Ã…Â¡Ã¢â‚¬â€�ÃƒÂ¥Ã‚Â½Ã‚Â±ÃƒÂ¤Ã‚Â¹Ã¢â‚¬Â¹ÃƒÂ§Ã¢â‚¬Â°Ã¢â€žÂ¢
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
                    // ÃƒÂ¦Ã¢â‚¬Å“Ã‚Â¬ÃƒÂ¤Ã‚Â¼Ã‚Â¼ÃƒÂ©Ã‚Â­Ã¢â‚¬ï¿½ÃƒÂ¦Ã‚Â³Ã¢â‚¬Â¢ÃƒÂ¦Ã‚Â­Ã‚Â¦ÃƒÂ¥Ã¢â€žÂ¢Ã‚Â¨
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
                    // ÃƒÂ§Ã‚Â¥Ã…Â¾ÃƒÂ¨Ã¯Â¿Â½Ã¢â‚¬â€œÃƒÂ¦Ã‚Â­Ã‚Â¦ÃƒÂ¥Ã¢â€žÂ¢Ã‚Â¨ÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ§Ã‚Â¥Ã¯Â¿Â½ÃƒÂ§Ã‚Â¦Ã¯Â¿Â½ÃƒÂ©Ã‚Â­Ã¢â‚¬ï¿½ÃƒÂ¦Ã‚Â³Ã¢â‚¬Â¢ÃƒÂ¦Ã‚Â­Ã‚Â¦ÃƒÂ¥Ã¢â€žÂ¢Ã‚Â¨
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
                    // ÃƒÂ©Ã…Â½Ã‚Â§ÃƒÂ§Ã¢â‚¬ï¿½Ã‚Â²ÃƒÂ¨Ã‚Â­Ã‚Â·ÃƒÂ¦Ã…â€™Ã¯Â¿Â½
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

                // ÃƒÂ¢Ã¢â‚¬â€œÃ‚ ÃƒÂ¢Ã¢â‚¬â€œÃ‚ ÃƒÂ¢Ã¢â‚¬â€œÃ‚ ÃƒÂ¢Ã¢â‚¬â€œÃ‚  ÃƒÂ¥Ã¢â€šÂ¬Ã¢â‚¬Â¹ÃƒÂ¥Ã‹â€ Ã‚Â¥ÃƒÂ¥Ã¢â‚¬Â¡Ã‚Â¦ÃƒÂ§Ã¯Â¿Â½Ã¢â‚¬ ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬Å“ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬Å“ÃƒÂ£Ã¯Â¿Â½Ã‚Â¾ÃƒÂ£Ã¯Â¿Â½Ã‚Â§ ÃƒÂ¢Ã¢â‚¬â€œÃ‚ ÃƒÂ¢Ã¢â‚¬â€œÃ‚ ÃƒÂ¢Ã¢â‚¬â€œÃ‚ ÃƒÂ¢Ã¢â‚¬â€œÃ‚

                // ÃƒÂ¦Ã‚Â²Ã‚Â»ÃƒÂ§Ã¢â€žÂ¢Ã¢â‚¬â„¢ÃƒÂ¦Ã¢â€šÂ¬Ã‚Â§ÃƒÂ©Ã‚Â­Ã¢â‚¬ï¿½ÃƒÂ¦Ã‚Â³Ã¢â‚¬Â¢ÃƒÂ¦Ã¢â‚¬ï¿½Ã‚Â»ÃƒÂ¦Ã¢â‚¬Å“Ã… ÃƒÂ¤Ã‚Â¸Ã¯Â¿Â½ÃƒÂ¦Ã‚Â­Ã‚Â»ÃƒÂ¤Ã‚Â¿Ã¢â‚¬Å¡ÃƒÂ§Ã…Â¡Ã¢â‚¬Å¾ÃƒÂ¦Ã¢â€šÂ¬Ã‚ÂªÃƒÂ§Ã¢â‚¬Â°Ã‚Â©ÃƒÂ£Ã¢â€šÂ¬Ã¢â‚¬Å¡
                if ((_skill.getType() == L1Skills.TYPE_HEAL) && (_calcType == PC_NPC) && (undeadType == 1)) {
                    dmg *= -1;
                }
                // ÃƒÂ¦Ã‚Â²Ã‚Â»ÃƒÂ§Ã¢â€žÂ¢Ã¢â‚¬â„¢ÃƒÂ¦Ã¢â€šÂ¬Ã‚Â§ÃƒÂ©Ã‚Â­Ã¢â‚¬ï¿½ÃƒÂ¦Ã‚Â³Ã¢â‚¬Â¢ÃƒÂ§Ã¢â‚¬Å¾Ã‚Â¡ÃƒÂ¦Ã‚Â³Ã¢â‚¬Â¢ÃƒÂ¥Ã‚Â°Ã¯Â¿Â½ÃƒÂ¦Ã‚Â­Ã‚Â¤ÃƒÂ¤Ã‚Â¸Ã¯Â¿Â½ÃƒÂ¦Ã‚Â­Ã‚Â»ÃƒÂ¤Ã‚Â¿Ã¢â‚¬Å¡ÃƒÂ¨Ã‚ÂµÃ‚Â·ÃƒÂ¤Ã‚Â½Ã…â€œÃƒÂ§Ã¢â‚¬ï¿½Ã‚Â¨
                if ((_skill.getType() == L1Skills.TYPE_HEAL) && (_calcType == PC_NPC) && (undeadType == 3)) {
                    dmg = 0;
                }
                // ÃƒÂ§Ã¢â‚¬Å¾Ã‚Â¡ÃƒÂ¦Ã‚Â³Ã¢â‚¬Â¢ÃƒÂ¥Ã‚Â°Ã¯Â¿Â½ÃƒÂ¥Ã…Â¸Ã…Â½ÃƒÂ©Ã¢â‚¬â€œÃ¢â€šÂ¬ÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ¥Ã‚Â®Ã‹â€ ÃƒÂ¨Ã‚Â­Ã‚Â·ÃƒÂ¥Ã‚Â¡Ã¢â‚¬ï¿½ÃƒÂ¨Ã‚Â£Ã…â€œÃƒÂ¨Ã‚Â¡Ã¢â€šÂ¬
                if (((cha instanceof L1TowerInstance) || (cha instanceof L1DoorInstance)) && (dmg < 0)) {
                    dmg = 0;
                }
                // ÃƒÂ¥Ã¯Â¿Â½Ã‚Â¸ÃƒÂ¥Ã¯Â¿Â½Ã¢â‚¬â€œÃƒÂ©Ã‚Â­Ã¢â‚¬ï¿½ÃƒÂ¥Ã… Ã¢â‚¬ÂºÃƒÂ£Ã¢â€šÂ¬Ã¢â‚¬Å¡
                if ((dmg > 0) || (drainMana != 0)) {
                    _magic.commit(dmg, drainMana);
                }
                // ÃƒÂ¨Ã‚Â£Ã…â€œÃƒÂ¨Ã‚Â¡Ã¢â€šÂ¬ÃƒÂ¥Ã‹â€ Ã‚Â¤ÃƒÂ¦Ã¢â‚¬â€œÃ‚Â·
                if ((_skill.getType() == L1Skills.TYPE_HEAL) && (dmg < 0)) {
                    cha.setCurrentHp((dmg * -1) + cha.getCurrentHp());
                }
                // ÃƒÂ©Ã¯Â¿Â½Ã…Â¾ÃƒÂ¦Ã‚Â²Ã‚Â»ÃƒÂ§Ã¢â€žÂ¢Ã¢â‚¬â„¢ÃƒÂ¦Ã¢â€šÂ¬Ã‚Â§ÃƒÂ©Ã‚Â­Ã¢â‚¬ï¿½ÃƒÂ¦Ã‚Â³Ã¢â‚¬Â¢ÃƒÂ¨Ã‚Â£Ã…â€œÃƒÂ¨Ã‚Â¡Ã¢â€šÂ¬ÃƒÂ¥Ã‹â€ Ã‚Â¤ÃƒÂ¦Ã¢â‚¬â€œÃ‚Â·(ÃƒÂ¥Ã‚Â¯Ã¢â‚¬â„¢ÃƒÂ¦Ã‹â€ Ã‚Â°ÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ¥Ã¯Â¿Â½Ã‚Â¸ÃƒÂ¥Ã¯Â¿Â½Ã‚Â»ÃƒÂ§Ã‚Â­Ã¢â‚¬Â°)
                if (heal > 0) {
                    _user.setCurrentHp(heal + _user.getCurrentHp());
                }

                if (cha instanceof L1PcInstance) { // ÃƒÂ¦Ã¢â‚¬ÂºÃ‚Â´ÃƒÂ¦Ã¢â‚¬â€œÃ‚Â°ÃƒÂ¨Ã¢â‚¬Â¡Ã‚ÂªÃƒÂ¨Ã‚ÂºÃ‚Â«ÃƒÂ§Ã¢â‚¬Â¹Ã¢â€šÂ¬ÃƒÂ¦Ã¢â‚¬Â¦Ã¢â‚¬Â¹
                    L1PcInstance pc = (L1PcInstance) cha;
                    pc.turnOnOffLight();
                    pc.sendPackets(new S_OwnCharAttrDef(pc));
                    pc.sendPackets(new S_OwnCharStatus(pc));
                    sendHappenMessage(pc); // ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¿ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â²ÃƒÂ£Ã†â€™Ã†â€™ÃƒÂ£Ã†â€™Ã‹â€ ÃƒÂ£Ã¯Â¿Â½Ã‚Â«ÃƒÂ£Ã†â€™Ã‚Â¡ÃƒÂ£Ã†â€™Ã†â€™ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â»ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¸ÃƒÂ£Ã¢â‚¬Å¡Ã¢â‚¬â„¢ÃƒÂ©Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ¤Ã‚Â¿Ã‚Â¡
                }

                addMagicList(cha, false); // ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¿ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â²ÃƒÂ£Ã†â€™Ã†â€™ÃƒÂ£Ã†â€™Ã‹â€ ÃƒÂ£Ã¯Â¿Â½Ã‚Â«ÃƒÂ©Ã‚Â­Ã¢â‚¬ï¿½ÃƒÂ¦Ã‚Â³Ã¢â‚¬Â¢ÃƒÂ£Ã¯Â¿Â½Ã‚Â®ÃƒÂ¥Ã… Ã‚Â¹ÃƒÂ¦Ã…Â¾Ã…â€œÃƒÂ¦Ã¢â€žÂ¢Ã¢â‚¬Å¡ÃƒÂ©Ã¢â‚¬â€œÃ¢â‚¬Å“ÃƒÂ£Ã¢â‚¬Å¡Ã¢â‚¬â„¢ÃƒÂ¨Ã‚Â¨Ã‚Â­ÃƒÂ¥Ã‚Â®Ã…Â¡

                if (cha instanceof L1PcInstance) { // ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¿ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â²ÃƒÂ£Ã†â€™Ã†â€™ÃƒÂ£Ã†â€™Ã‹â€ ÃƒÂ£Ã¯Â¿Â½Ã…â€™PCÃƒÂ£Ã¯Â¿Â½Ã‚ÂªÃƒÂ£Ã¢â‚¬Å¡Ã¢â‚¬Â°ÃƒÂ£Ã¯Â¿Â½Ã‚Â°ÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ£Ã†â€™Ã‚Â©ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¤ÃƒÂ£Ã†â€™Ã‹â€ ÃƒÂ§Ã… Ã‚Â¶ÃƒÂ¦Ã¢â‚¬Â¦Ã¢â‚¬Â¹ÃƒÂ£Ã¢â‚¬Å¡Ã¢â‚¬â„¢ÃƒÂ¦Ã¢â‚¬ÂºÃ‚Â´ÃƒÂ¦Ã¢â‚¬â€œÃ‚Â°
                    L1PcInstance pc = (L1PcInstance) cha;
                    pc.turnOnOffLight();
                }
            }

            // ÃƒÂ¨Ã‚Â§Ã‚Â£ÃƒÂ©Ã¢â€žÂ¢Ã‚Â¤ÃƒÂ©Ã…Â¡Ã‚Â±ÃƒÂ¨Ã‚ÂºÃ‚Â«
            if ((_skillId == DETECTION) || (_skillId == COUNTER_DETECTION)) { // ÃƒÂ§Ã¢â‚¬Å¾Ã‚Â¡ÃƒÂ¦Ã¢â‚¬Â°Ã¢â€šÂ¬ÃƒÂ©Ã¯Â¿Â½Ã¯Â¿Â½ÃƒÂ¥Ã‚Â½Ã‚Â¢ÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ¥Ã‚Â¼Ã‚Â·ÃƒÂ¥Ã… Ã¢â‚¬ÂºÃƒÂ§Ã¢â‚¬Å¾Ã‚Â¡ÃƒÂ¦Ã¢â‚¬Â°Ã¢â€šÂ¬ÃƒÂ©Ã¯Â¿Â½Ã¯Â¿Â½ÃƒÂ¥Ã‚Â½Ã‚Â¢
                detection(_player);
            }

        }
        catch (Exception e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        }
    }

    private void detection(L1PcInstance pc) {
        if (!pc.isGmInvis() && pc.isInvisble()) { // ÃƒÂ¨Ã¢â‚¬Â¡Ã‚ÂªÃƒÂ¥Ã‚Â·Ã‚Â±ÃƒÂ©Ã…Â¡Ã‚Â±ÃƒÂ¨Ã‚ÂºÃ‚Â«ÃƒÂ¤Ã‚Â¸Ã‚Â­
            pc.delInvis();
            pc.beginInvisTimer();
        }

        for (L1PcInstance tgt : L1World.getInstance().getVisiblePlayer(pc)) { // ÃƒÂ§Ã¢â‚¬Â¢Ã‚Â«ÃƒÂ©Ã¯Â¿Â½Ã‚Â¢ÃƒÂ¥Ã¢â‚¬Â¦Ã‚Â§ÃƒÂ¥Ã¢â‚¬Â¦Ã‚Â¶ÃƒÂ¤Ã‚Â»Ã¢â‚¬â€œÃƒÂ©Ã…Â¡Ã‚Â±ÃƒÂ¨Ã‚ÂºÃ‚Â«ÃƒÂ¨Ã¢â€šÂ¬Ã¢â‚¬Â¦
            if (!tgt.isGmInvis() && tgt.isInvisble()) {
                tgt.delInvis();
            }
        }
        L1WorldTraps.getInstance().onDetection(pc);
    }

    // ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¿ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â²ÃƒÂ£Ã†â€™Ã†â€™ÃƒÂ£Ã†â€™Ã‹â€ ÃƒÂ£Ã¯Â¿Â½Ã‚Â«ÃƒÂ£Ã¯Â¿Â½Ã‚Â¤ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬Å¾ÃƒÂ£Ã¯Â¿Â½Ã‚Â¦ÃƒÂ¨Ã‚Â¨Ã‹â€ ÃƒÂ§Ã‚Â®Ã¢â‚¬â€�ÃƒÂ£Ã¯Â¿Â½Ã¢â€žÂ¢ÃƒÂ£Ã¢â‚¬Å¡Ã¢â‚¬Â¹ÃƒÂ¥Ã‚Â¿Ã¢â‚¬Â¦ÃƒÂ¨Ã‚Â¦Ã¯Â¿Â½ÃƒÂ£Ã¯Â¿Â½Ã…â€™ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬Å¡ÃƒÂ£Ã¢â‚¬Å¡Ã¢â‚¬Â¹ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬Â¹ÃƒÂ¨Ã‚Â¿Ã¢â‚¬ï¿½ÃƒÂ£Ã¯Â¿Â½Ã¢â€žÂ¢
    private boolean isTargetCalc(L1Character cha) {
        // ÃƒÂ¤Ã‚Â¸Ã¢â‚¬Â°ÃƒÂ©Ã¢â‚¬Â¡Ã¯Â¿Â½ÃƒÂ§Ã…Â¸Ã‚Â¢ÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ¥Ã‚Â±Ã‚ ÃƒÂ¥Ã‚Â®Ã‚Â°ÃƒÂ¨Ã¢â€šÂ¬Ã¢â‚¬Â¦ÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ¦Ã…Â¡Ã‚Â´ÃƒÂ¦Ã¢â‚¬Å“Ã… ÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ©Ã‚ÂªÃ‚Â·ÃƒÂ©Ã‚Â«Ã¯Â¿Â½ÃƒÂ¦Ã‚Â¯Ã¢â€šÂ¬ÃƒÂ¥Ã‚Â£Ã…Â¾
        if ((_user instanceof L1PcInstance)
                && (_skillId == TRIPLE_ARROW || _skillId == FOE_SLAYER
                || _skillId == SMASH || _skillId == BONE_BREAK)) {
            return true;
        }
        // ÃƒÂ¦Ã¢â‚¬ï¿½Ã‚Â»ÃƒÂ¦Ã¢â‚¬â„¢Ã†â€™ÃƒÂ©Ã‚Â­Ã¢â‚¬ï¿½ÃƒÂ¦Ã‚Â³Ã¢â‚¬Â¢ÃƒÂ£Ã¯Â¿Â½Ã‚Â®NonÃƒÂ¯Ã‚Â¼Ã¯Â¿Â½PvPÃƒÂ¥Ã‹â€ Ã‚Â¤ÃƒÂ¥Ã‚Â®Ã…Â¡
        if (_skill.getTarget().equals("attack") && (_skillId != 18)) { // ÃƒÂ¦Ã¢â‚¬ï¿½Ã‚Â»ÃƒÂ¦Ã¢â‚¬â„¢Ã†â€™ÃƒÂ©Ã‚Â­Ã¢â‚¬ï¿½ÃƒÂ¦Ã‚Â³Ã¢â‚¬Â¢
            if (isPcSummonPet(cha)) { // ÃƒÂ¥Ã‚Â¯Ã‚Â¾ÃƒÂ¨Ã‚Â±Ã‚Â¡ÃƒÂ£Ã¯Â¿Â½Ã…â€™PCÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ£Ã¢â‚¬Å¡Ã‚ÂµÃƒÂ£Ã†â€™Ã‚Â¢ÃƒÂ£Ã†â€™Ã‚Â³ÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ£Ã†â€™Ã…Â¡ÃƒÂ£Ã†â€™Ã†â€™ÃƒÂ£Ã†â€™Ã‹â€
                if ((_player.getZoneType() == 1) || (cha.getZoneType() == 1 // ÃƒÂ¦Ã¢â‚¬ï¿½Ã‚Â»ÃƒÂ¦Ã¢â‚¬â„¢Ã†â€™ÃƒÂ£Ã¯Â¿Â½Ã¢â€žÂ¢ÃƒÂ£Ã¢â‚¬Å¡Ã¢â‚¬Â¹ÃƒÂ¥Ã¯Â¿Â½Ã‚Â´ÃƒÂ£Ã¯Â¿Â½Ã‚Â¾ÃƒÂ£Ã¯Â¿Â½Ã…Â¸ÃƒÂ£Ã¯Â¿Â½Ã‚Â¯ÃƒÂ¦Ã¢â‚¬ï¿½Ã‚Â»ÃƒÂ¦Ã¢â‚¬â„¢Ã†â€™ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬Â¢ÃƒÂ£Ã¢â‚¬Å¡Ã…â€™ÃƒÂ£Ã¢â‚¬Å¡Ã¢â‚¬Â¹ÃƒÂ¥Ã¯Â¿Â½Ã‚Â´ÃƒÂ£Ã¯Â¿Â½Ã…â€™ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â»ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã†â€™Ã¢â‚¬Â¢ÃƒÂ£Ã†â€™Ã¢â‚¬ ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â£ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¾ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã†â€™Ã‚Â³
                ) || _player.checkNonPvP(_player, cha)) { // Non-PvPÃƒÂ¨Ã‚Â¨Ã‚Â­ÃƒÂ¥Ã‚Â®Ã…Â¡
                    return false;
                }
            }
        }

        // ÃƒÂ£Ã†â€™Ã¢â‚¬Â¢ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â©ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â°ÃƒÂ£Ã¢â‚¬Å¡Ã‚ÂªÃƒÂ£Ã†â€™Ã¢â‚¬â€œÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¹ÃƒÂ£Ã†â€™Ã‚ÂªÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã†â€™Ã¢â‚¬ï¿½ÃƒÂ£Ã†â€™Ã‚Â³ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â°ÃƒÂ£Ã¯Â¿Â½Ã‚Â¯ÃƒÂ¨Ã¢â‚¬Â¡Ã‚ÂªÃƒÂ¥Ã‹â€ Ã¢â‚¬ ÃƒÂ¨Ã¢â‚¬Â¡Ã‚ÂªÃƒÂ¨Ã‚ÂºÃ‚Â«ÃƒÂ£Ã¯Â¿Â½Ã‚Â¯ÃƒÂ¥Ã‚Â¯Ã‚Â¾ÃƒÂ¨Ã‚Â±Ã‚Â¡ÃƒÂ¥Ã‚Â¤Ã¢â‚¬â€œ
        if ((_skillId == FOG_OF_SLEEPING) && (_user.getId() == cha.getId())) {
            return false;
        }

        // ÃƒÂ£Ã†â€™Ã…Â¾ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¹ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¹ÃƒÂ£Ã†â€™Ã‚Â­ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã¯Â¿Â½Ã‚Â¯ÃƒÂ¨Ã¢â‚¬Â¡Ã‚ÂªÃƒÂ¥Ã‹â€ Ã¢â‚¬ ÃƒÂ¨Ã¢â‚¬Â¡Ã‚ÂªÃƒÂ¨Ã‚ÂºÃ‚Â«ÃƒÂ£Ã¯Â¿Â½Ã‚Â¨ÃƒÂ¨Ã¢â‚¬Â¡Ã‚ÂªÃƒÂ¥Ã‹â€ Ã¢â‚¬ ÃƒÂ£Ã¯Â¿Â½Ã‚Â®ÃƒÂ£Ã†â€™Ã…Â¡ÃƒÂ£Ã†â€™Ã†â€™ÃƒÂ£Ã†â€™Ã‹â€ ÃƒÂ£Ã¯Â¿Â½Ã‚Â¯ÃƒÂ¥Ã‚Â¯Ã‚Â¾ÃƒÂ¨Ã‚Â±Ã‚Â¡ÃƒÂ¥Ã‚Â¤Ã¢â‚¬â€œ
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

        // ÃƒÂ£Ã†â€™Ã…Â¾ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¹ÃƒÂ£Ã†â€™Ã¢â‚¬ ÃƒÂ£Ã†â€™Ã‚Â¬ÃƒÂ£Ã†â€™Ã¯Â¿Â½ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã†â€™Ã‹â€ ÃƒÂ£Ã¯Â¿Â½Ã‚Â¯ÃƒÂ¨Ã¢â‚¬Â¡Ã‚ÂªÃƒÂ¥Ã‹â€ Ã¢â‚¬ ÃƒÂ¨Ã¢â‚¬Â¡Ã‚ÂªÃƒÂ¨Ã‚ÂºÃ‚Â«ÃƒÂ£Ã¯Â¿Â½Ã‚Â®ÃƒÂ£Ã¯Â¿Â½Ã‚Â¿ÃƒÂ¥Ã‚Â¯Ã‚Â¾ÃƒÂ¨Ã‚Â±Ã‚Â¡ÃƒÂ¯Ã‚Â¼Ã‹â€ ÃƒÂ¥Ã¯Â¿Â½Ã…â€™ÃƒÂ¦Ã¢â€žÂ¢Ã¢â‚¬Å¡ÃƒÂ£Ã¯Â¿Â½Ã‚Â«ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¯ÃƒÂ£Ã†â€™Ã‚Â©ÃƒÂ£Ã†â€™Ã‚Â³ÃƒÂ¥Ã¢â‚¬Å“Ã‚Â¡ÃƒÂ£Ã¢â‚¬Å¡Ã¢â‚¬Å¡ÃƒÂ£Ã†â€™Ã¢â‚¬ ÃƒÂ£Ã†â€™Ã‚Â¬ÃƒÂ£Ã†â€™Ã¯Â¿Â½ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã†â€™Ã‹â€ ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬Â¢ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬ÂºÃƒÂ£Ã¢â‚¬Å¡Ã¢â‚¬Â¹ÃƒÂ¯Ã‚Â¼Ã¢â‚¬Â°
        if (_skillId == MASS_TELEPORT) {
            if (_user.getId() != cha.getId()) {
                return false;
            }
        }

        return true;
    }

    // ÃƒÂ¥Ã‚Â¯Ã‚Â¾ÃƒÂ¨Ã‚Â±Ã‚Â¡ÃƒÂ£Ã¯Â¿Â½Ã…â€™PCÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ£Ã¢â‚¬Å¡Ã‚ÂµÃƒÂ£Ã†â€™Ã‚Â¢ÃƒÂ£Ã†â€™Ã‚Â³ÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ£Ã†â€™Ã…Â¡ÃƒÂ£Ã†â€™Ã†â€™ÃƒÂ£Ã†â€™Ã‹â€ ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬Â¹ÃƒÂ£Ã¢â‚¬Å¡Ã¢â‚¬â„¢ÃƒÂ¨Ã‚Â¿Ã¢â‚¬ï¿½ÃƒÂ£Ã¯Â¿Â½Ã¢â€žÂ¢
    private boolean isPcSummonPet(L1Character cha) {
        if (_calcType == PC_PC) { // ÃƒÂ¥Ã‚Â¯Ã‚Â¾ÃƒÂ¨Ã‚Â±Ã‚Â¡ÃƒÂ£Ã¯Â¿Â½Ã…â€™PC
            return true;
        }

        if (_calcType == PC_NPC) {
            if (cha instanceof L1SummonInstance) { // ÃƒÂ¥Ã‚Â¯Ã‚Â¾ÃƒÂ¨Ã‚Â±Ã‚Â¡ÃƒÂ£Ã¯Â¿Â½Ã…â€™ÃƒÂ£Ã¢â‚¬Å¡Ã‚ÂµÃƒÂ£Ã†â€™Ã‚Â¢ÃƒÂ£Ã†â€™Ã‚Â³
                L1SummonInstance summon = (L1SummonInstance) cha;
                if (summon.isExsistMaster()) { // ÃƒÂ£Ã†â€™Ã…Â¾ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¹ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¿ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã¯Â¿Â½Ã…â€™ÃƒÂ¥Ã‚Â±Ã¢â‚¬Â¦ÃƒÂ£Ã¢â‚¬Å¡Ã¢â‚¬Â¹
                    return true;
                }
            }
            if (cha instanceof L1PetInstance) { // ÃƒÂ¥Ã‚Â¯Ã‚Â¾ÃƒÂ¨Ã‚Â±Ã‚Â¡ÃƒÂ£Ã¯Â¿Â½Ã…â€™ÃƒÂ£Ã†â€™Ã…Â¡ÃƒÂ£Ã†â€™Ã†â€™ÃƒÂ£Ã†â€™Ã‹â€
                return true;
            }
        }
        return false;
    }

    // ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¿ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â²ÃƒÂ£Ã†â€™Ã†â€™ÃƒÂ£Ã†â€™Ã‹â€ ÃƒÂ£Ã¯Â¿Â½Ã‚Â«ÃƒÂ¥Ã‚Â¯Ã‚Â¾ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬â€�ÃƒÂ£Ã¯Â¿Â½Ã‚Â¦ÃƒÂ¥Ã‚Â¿Ã¢â‚¬Â¦ÃƒÂ£Ã¯Â¿Â½Ã…Â¡ÃƒÂ¥Ã‚Â¤Ã‚Â±ÃƒÂ¦Ã¢â‚¬Â¢Ã¢â‚¬â€�ÃƒÂ£Ã¯Â¿Â½Ã‚Â«ÃƒÂ£Ã¯Â¿Â½Ã‚ÂªÃƒÂ£Ã¢â‚¬Å¡Ã¢â‚¬Â¹ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬Â¹ÃƒÂ¨Ã‚Â¿Ã¢â‚¬ï¿½ÃƒÂ£Ã¯Â¿Â½Ã¢â€žÂ¢
    private boolean isTargetFailure(L1Character cha) {
        boolean isTU = false;
        boolean isErase = false;
        boolean isManaDrain = false;
        int undeadType = 0;

        if ((cha instanceof L1TowerInstance) || (cha instanceof L1DoorInstance)) { // ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¬ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã†â€™Ã¢â‚¬Â¡ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â£ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¢ÃƒÂ£Ã†â€™Ã‚Â³ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¿ÃƒÂ£Ã†â€™Ã‚Â¯ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ£Ã†â€™Ã¢â‚¬Â°ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¢ÃƒÂ£Ã¯Â¿Â½Ã‚Â«ÃƒÂ£Ã¯Â¿Â½Ã‚Â¯ÃƒÂ§Ã‚Â¢Ã‚ÂºÃƒÂ§Ã…Â½Ã¢â‚¬Â¡ÃƒÂ§Ã‚Â³Ã‚Â»ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¹ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â­ÃƒÂ£Ã†â€™Ã‚Â«ÃƒÂ§Ã¢â‚¬Å¾Ã‚Â¡ÃƒÂ¥Ã… Ã‚Â¹
            return true;
        }

        if (cha instanceof L1PcInstance) { // ÃƒÂ¥Ã‚Â¯Ã‚Â¾PCÃƒÂ£Ã¯Â¿Â½Ã‚Â®ÃƒÂ¥Ã‚ Ã‚Â´ÃƒÂ¥Ã¯Â¿Â½Ã‹â€
            if ((_calcType == PC_PC) && _player.checkNonPvP(_player, cha)) { // Non-PvPÃƒÂ¨Ã‚Â¨Ã‚Â­ÃƒÂ¥Ã‚Â®Ã…Â¡
                L1PcInstance pc = (L1PcInstance) cha;
                if ((_player.getId() == pc.getId()) || ((pc.getClanid() != 0) && (_player.getClanid() == pc.getClanid()))) {
                    return false;
                }
                return true;
            }
            return false;
        }

        if (cha instanceof L1MonsterInstance) { // ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¿ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã†â€™Ã‚Â³ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¢ÃƒÂ£Ã†â€™Ã‚Â³ÃƒÂ£Ã†â€™Ã¢â‚¬Â¡ÃƒÂ£Ã†â€™Ã†â€™ÃƒÂ£Ã†â€™Ã‹â€ ÃƒÂ¥Ã¯Â¿Â½Ã‚Â¯ÃƒÂ¨Ã†â€™Ã‚Â½ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬Â¹ÃƒÂ¥Ã‹â€ Ã‚Â¤ÃƒÂ¥Ã‚Â®Ã…Â¡
            isTU = ((L1MonsterInstance) cha).getNpcTemplate().get_IsTU();
        }

        if (cha instanceof L1MonsterInstance) { // ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¤ÃƒÂ£Ã†â€™Ã‚Â¬ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¹ÃƒÂ£Ã†â€™Ã…Â¾ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¸ÃƒÂ£Ã†â€™Ã†â€™ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¯ÃƒÂ¥Ã¯Â¿Â½Ã‚Â¯ÃƒÂ¨Ã†â€™Ã‚Â½ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬Â¹ÃƒÂ¥Ã‹â€ Ã‚Â¤ÃƒÂ¥Ã‚Â®Ã…Â¡
            isErase = ((L1MonsterInstance) cha).getNpcTemplate().get_IsErase();
        }

        if (cha instanceof L1MonsterInstance) { // ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¢ÃƒÂ£Ã†â€™Ã‚Â³ÃƒÂ£Ã†â€™Ã¢â‚¬Â¡ÃƒÂ£Ã†â€™Ã†â€™ÃƒÂ£Ã†â€™Ã‹â€ ÃƒÂ£Ã¯Â¿Â½Ã‚Â®ÃƒÂ¥Ã‹â€ Ã‚Â¤ÃƒÂ¥Ã‚Â®Ã…Â¡
            undeadType = ((L1MonsterInstance) cha).getNpcTemplate().get_undead();
        }

        // ÃƒÂ£Ã†â€™Ã…Â¾ÃƒÂ£Ã†â€™Ã… ÃƒÂ£Ã†â€™Ã¢â‚¬Â°ÃƒÂ£Ã†â€™Ã‚Â¬ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¤ÃƒÂ£Ã†â€™Ã‚Â³ÃƒÂ£Ã¯Â¿Â½Ã…â€™ÃƒÂ¥Ã¯Â¿Â½Ã‚Â¯ÃƒÂ¨Ã†â€™Ã‚Â½ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬Â¹ÃƒÂ¯Ã‚Â¼Ã…Â¸
        if (cha instanceof L1MonsterInstance) {
            isManaDrain = true;
        }
                /*
                 * ÃƒÂ¦Ã‹â€ Ã¯Â¿Â½ÃƒÂ¥Ã… Ã…Â¸ÃƒÂ©Ã¢â€žÂ¢Ã‚Â¤ÃƒÂ¥Ã‚Â¤Ã¢â‚¬â€œÃƒÂ¦Ã¯Â¿Â½Ã‚Â¡ÃƒÂ¤Ã‚Â»Ã‚Â¶ÃƒÂ¯Ã‚Â¼Ã¢â‚¬ËœÃƒÂ¯Ã‚Â¼Ã…Â¡T-UÃƒÂ£Ã¯Â¿Â½Ã…â€™ÃƒÂ¦Ã‹â€ Ã¯Â¿Â½ÃƒÂ¥Ã… Ã…Â¸ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬â€�ÃƒÂ£Ã¯Â¿Â½Ã…Â¸ÃƒÂ£Ã¯Â¿Â½Ã…â€™ÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ¥Ã‚Â¯Ã‚Â¾ÃƒÂ¨Ã‚Â±Ã‚Â¡ÃƒÂ£Ã¯Â¿Â½Ã…â€™ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¢ÃƒÂ£Ã†â€™Ã‚Â³ÃƒÂ£Ã†â€™Ã¢â‚¬Â¡ÃƒÂ£Ã†â€™Ã†â€™ÃƒÂ£Ã†â€™Ã‹â€ ÃƒÂ£Ã¯Â¿Â½Ã‚Â§ÃƒÂ£Ã¯Â¿Â½Ã‚Â¯ÃƒÂ£Ã¯Â¿Â½Ã‚ÂªÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬Å¾ÃƒÂ£Ã¢â€šÂ¬Ã¢â‚¬Å¡ ÃƒÂ¦Ã‹â€ Ã¯Â¿Â½ÃƒÂ¥Ã… Ã…Â¸ÃƒÂ©Ã¢â€žÂ¢Ã‚Â¤ÃƒÂ¥Ã‚Â¤Ã¢â‚¬â€œÃƒÂ¦Ã¯Â¿Â½Ã‚Â¡ÃƒÂ¤Ã‚Â»Ã‚Â¶ÃƒÂ¯Ã‚Â¼Ã¢â‚¬â„¢ÃƒÂ¯Ã‚Â¼Ã…Â¡T-UÃƒÂ£Ã¯Â¿Â½Ã…â€™ÃƒÂ¦Ã‹â€ Ã¯Â¿Â½ÃƒÂ¥Ã… Ã…Â¸ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬â€�ÃƒÂ£Ã¯Â¿Â½Ã…Â¸ÃƒÂ£Ã¯Â¿Â½Ã…â€™ÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ¥Ã‚Â¯Ã‚Â¾ÃƒÂ¨Ã‚Â±Ã‚Â¡ÃƒÂ£Ã¯Â¿Â½Ã‚Â«ÃƒÂ£Ã¯Â¿Â½Ã‚Â¯ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¿ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã†â€™Ã‚Â³ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¢ÃƒÂ£Ã†â€™Ã‚Â³ÃƒÂ£Ã†â€™Ã¢â‚¬Â¡ÃƒÂ£Ã†â€™Ã†â€™ÃƒÂ£Ã†â€™Ã‹â€ ÃƒÂ§Ã¢â‚¬Å¾Ã‚Â¡ÃƒÂ¥Ã… Ã‚Â¹ÃƒÂ£Ã¢â€šÂ¬Ã¢â‚¬Å¡
                 * ÃƒÂ¦Ã‹â€ Ã¯Â¿Â½ÃƒÂ¥Ã… Ã…Â¸ÃƒÂ©Ã¢â€žÂ¢Ã‚Â¤ÃƒÂ¥Ã‚Â¤Ã¢â‚¬â€œÃƒÂ¦Ã¯Â¿Â½Ã‚Â¡ÃƒÂ¤Ã‚Â»Ã‚Â¶ÃƒÂ¯Ã‚Â¼Ã¢â‚¬Å“ÃƒÂ¯Ã‚Â¼Ã…Â¡ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¹ÃƒÂ£Ã†â€™Ã‚Â­ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ£Ã†â€™Ã…Â¾ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¹ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¹ÃƒÂ£Ã†â€™Ã‚Â­ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ£Ã†â€™Ã…Â¾ÃƒÂ£Ã†â€™Ã… ÃƒÂ£Ã†â€™Ã¢â‚¬Â°ÃƒÂ£Ã†â€™Ã‚Â¬ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¤ÃƒÂ£Ã†â€™Ã‚Â³ÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¨ÃƒÂ£Ã†â€™Ã‚Â³ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¿ÃƒÂ£Ã†â€™Ã‚Â³ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â°ÃƒÂ£Ã†â€™Ã‚Â«ÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¤ÃƒÂ£Ã†â€™Ã‚Â¬ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¹ÃƒÂ£Ã†â€™Ã…Â¾ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¸ÃƒÂ£Ã†â€™Ã†â€™ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¯ÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¦ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â£ÃƒÂ£Ã†â€™Ã‚Â³ÃƒÂ£Ã†â€™Ã¢â‚¬Â°ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â·ÃƒÂ£Ã†â€™Ã‚Â£ÃƒÂ£Ã†â€™Ã†â€™ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¯ÃƒÂ£Ã†â€™Ã‚Â«ÃƒÂ§Ã¢â‚¬Å¾Ã‚Â¡ÃƒÂ¥Ã… Ã‚Â¹
                 * ÃƒÂ¦Ã‹â€ Ã¯Â¿Â½ÃƒÂ¥Ã… Ã…Â¸ÃƒÂ©Ã¢â€žÂ¢Ã‚Â¤ÃƒÂ¥Ã‚Â¤Ã¢â‚¬â€œÃƒÂ¦Ã¯Â¿Â½Ã‚Â¡ÃƒÂ¤Ã‚Â»Ã‚Â¶ÃƒÂ¯Ã‚Â¼Ã¢â‚¬ï¿½ÃƒÂ¯Ã‚Â¼Ã…Â¡ÃƒÂ£Ã†â€™Ã…Â¾ÃƒÂ£Ã†â€™Ã… ÃƒÂ£Ã†â€™Ã¢â‚¬Â°ÃƒÂ£Ã†â€™Ã‚Â¬ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¤ÃƒÂ£Ã†â€™Ã‚Â³ÃƒÂ£Ã¯Â¿Â½Ã…â€™ÃƒÂ¦Ã‹â€ Ã¯Â¿Â½ÃƒÂ¥Ã… Ã…Â¸ÃƒÂ£Ã¯Â¿Â½Ã¢â‚¬â€�ÃƒÂ£Ã¯Â¿Â½Ã…Â¸ÃƒÂ£Ã¯Â¿Â½Ã…â€™ÃƒÂ£Ã¢â€šÂ¬Ã¯Â¿Â½ÃƒÂ£Ã†â€™Ã‚Â¢ÃƒÂ£Ã†â€™Ã‚Â³ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¹ÃƒÂ£Ã¢â‚¬Å¡Ã‚Â¿ÃƒÂ£Ã†â€™Ã‚Â¼ÃƒÂ¤Ã‚Â»Ã‚Â¥ÃƒÂ¥Ã‚Â¤Ã¢â‚¬â€œÃƒÂ£Ã¯Â¿Â½Ã‚Â®ÃƒÂ¥Ã‚ Ã‚Â´ÃƒÂ¥Ã¯Â¿Â½Ã‹â€
                 */
        if (((_skillId == TURN_UNDEAD) && ((undeadType == 0) || (undeadType == 2)))
                || ((_skillId == TURN_UNDEAD) && (isTU == false))
                || (((_skillId == ERASE_MAGIC) || (_skillId == SLOW) || (_skillId == MANA_DRAIN) || (_skillId == MASS_SLOW) || (_skillId == ENTANGLE) || (_skillId == WIND_SHACKLE)) && (isErase == false))
                || ((_skillId == MANA_DRAIN) && (isManaDrain == false))) {
            return true;
        }
        return false;
    }

    // ÃƒÂ©Ã‚Â­Ã¢â‚¬ï¿½ÃƒÂ¦Ã‚Â³Ã¢â‚¬Â¢ÃƒÂ¥Ã‚Â±Ã¯Â¿Â½ÃƒÂ©Ã…Â¡Ã…â€œÃƒÂ§Ã¢â€žÂ¢Ã‚Â¼ÃƒÂ¥Ã¢â‚¬Â¹Ã¢â‚¬Â¢ÃƒÂ¥Ã‹â€ Ã‚Â¤ÃƒÂ¦Ã¢â‚¬â€œÃ‚Â·
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