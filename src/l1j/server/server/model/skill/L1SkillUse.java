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

    private boolean _checkedUseSkill = false; // Ã¤Âºâ€¹Ã¥â€°ï¿½Ã£Æ’ï¿½Ã£â€šÂ§Ã£Æ’Æ’Ã£â€šÂ¯Ã¦Â¸Ë†Ã£ï¿½Â¿Ã£ï¿½â€¹

    private int _leverage = 10; // 1/10Ã¥â‚¬ï¿½Ã£ï¿½ÂªÃ£ï¿½Â®Ã£ï¿½Â§10Ã£ï¿½Â§1Ã¥â‚¬ï¿½

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

        private boolean _isCalc = true; // Ã£Æ’â‚¬Ã£Æ’Â¡Ã£Æ’Â¼Ã£â€šÂ¸Ã£â€šâ€žÃ§Â¢ÂºÃ§Å½â€¡Ã©Â­â€�Ã¦Â³â€¢Ã£ï¿½Â®Ã¨Â¨Ë†Ã§Â®â€”Ã£â€šâ€™Ã£ï¿½â„¢Ã£â€šâ€¹Ã¥Â¿â€¦Ã¨Â¦ï¿½Ã£ï¿½Å’Ã£ï¿½â€šÃ£â€šâ€¹Ã£ï¿½â€¹Ã¯Â¼Å¸

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
     * Ã¦â€�Â»Ã¦â€œÅ Ã¨Â·ï¿½Ã©â€ºÂ¢Ã¨Â®Å Ã¦â€ºÂ´Ã£â‚¬â€š
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
     * Ã¦â€�Â»Ã¦â€œÅ Ã§Â¯â€žÃ¥Å“ï¿½Ã¨Â®Å Ã¦â€ºÂ´Ã£â‚¬â€š
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
     * 1/10Ã¥â‚¬ï¿½Ã£ï¿½Â§Ã¨Â¡Â¨Ã§ï¿½Â¾Ã£ï¿½â„¢Ã£â€šâ€¹Ã£â‚¬â€š
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
        // Ã¥Ë†ï¿½Ã¦Å“Å¸Ã¨Â¨Â­Ã¥Â®Å¡Ã£ï¿½â€œÃ£ï¿½â€œÃ£ï¿½â€¹Ã£â€šâ€°
        setCheckedUseSkill(true);
        _targetList = Lists.newList(); // Ã£â€šÂ¿Ã£Æ’Â¼Ã£â€šÂ²Ã£Æ’Æ’Ã£Æ’Ë†Ã£Æ’ÂªÃ£â€šÂ¹Ã£Æ’Ë†Ã£ï¿½Â®Ã¥Ë†ï¿½Ã¦Å“Å¸Ã¥Å’â€“

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

        if (type == TYPE_NORMAL) { // Ã©â‚¬Å¡Ã¥Â¸Â¸Ã£ï¿½Â®Ã©Â­â€�Ã¦Â³â€¢Ã¤Â½Â¿Ã§â€�Â¨Ã¦â„¢â€š
            checkedResult = isNormalSkillUsable();
        }
        else if (type == TYPE_SPELLSC) { // Ã£â€šÂ¹Ã£Æ’Å¡Ã£Æ’Â«Ã£â€šÂ¹Ã£â€šÂ¯Ã£Æ’Â­Ã£Æ’Â¼Ã£Æ’Â«Ã¤Â½Â¿Ã§â€�Â¨Ã¦â„¢â€š
            checkedResult = isSpellScrollUsable();
        }
        else if (type == TYPE_NPCBUFF) {
            checkedResult = true;
        }
        if (!checkedResult) {
            return false;
        }

        // Ã£Æ’â€¢Ã£â€šÂ¡Ã£â€šÂ¤Ã£â€šÂ¢Ã£Æ’Â¼Ã£â€šÂ¦Ã£â€šÂ©Ã£Æ’Â¼Ã£Æ’Â«Ã£â‚¬ï¿½Ã£Æ’Â©Ã£â€šÂ¤Ã£Æ’â€¢Ã£â€šÂ¹Ã£Æ’Ë†Ã£Æ’ÂªÃ£Æ’Â¼Ã£Æ’Â Ã£ï¿½Â¯Ã¨Â©Â Ã¥â€�Â±Ã¥Â¯Â¾Ã¨Â±Â¡Ã£ï¿½Å’Ã¥ÂºÂ§Ã¦Â¨â„¢
        // Ã£â€šÂ­Ã£Æ’Â¥Ã£Æ’Â¼Ã£Æ’â€“Ã£ï¿½Â¯Ã¨Â©Â Ã¥â€�Â±Ã¨â‚¬â€¦Ã£ï¿½Â®Ã¥ÂºÂ§Ã¦Â¨â„¢Ã£ï¿½Â«Ã©â€¦ï¿½Ã§Â½Â®Ã£ï¿½â€¢Ã£â€šÅ’Ã£â€šâ€¹Ã£ï¿½Å¸Ã£â€šï¿½Ã¤Â¾â€¹Ã¥Â¤â€“
        if ((_skillId == FIRE_WALL) || (_skillId == LIFE_STREAM) || (_skillId == TRUE_TARGET)) {
            return true;
        }

        L1Object l1object = L1World.getInstance().findObject(_targetID);
        if (l1object instanceof L1ItemInstance) {
            _log.fine("skill target item name: " + ((L1ItemInstance) l1object).getViewName());
            // Ã£â€šÂ¹Ã£â€šÂ­Ã£Æ’Â«Ã£â€šÂ¿Ã£Æ’Â¼Ã£â€šÂ²Ã£Æ’Æ’Ã£Æ’Ë†Ã£ï¿½Å’Ã§Â²Â¾Ã©Å“Å Ã£ï¿½Â®Ã§Å¸Â³Ã£ï¿½Â«Ã£ï¿½ÂªÃ£â€šâ€¹Ã£ï¿½â€œÃ£ï¿½Â¨Ã£ï¿½Å’Ã£ï¿½â€šÃ£â€šâ€¹Ã£â‚¬â€š
            // LinuxÃ§â€™Â°Ã¥Â¢Æ’Ã£ï¿½Â§Ã§Â¢ÂºÃ¨Âªï¿½Ã¯Â¼Ë†WindowsÃ£ï¿½Â§Ã£ï¿½Â¯Ã¦Å“ÂªÃ§Â¢ÂºÃ¨Âªï¿½Ã¯Â¼â€°
            // 2008.5.4Ã¨Â¿Â½Ã¨Â¨ËœÃ¯Â¼Å¡Ã¥Å“Â°Ã©ï¿½Â¢Ã£ï¿½Â®Ã£â€šÂ¢Ã£â€šÂ¤Ã£Æ’â€ Ã£Æ’Â Ã£ï¿½Â«Ã©Â­â€�Ã¦Â³â€¢Ã£â€šâ€™Ã¤Â½Â¿Ã£ï¿½â€ Ã£ï¿½Â¨Ã£ï¿½ÂªÃ£â€šâ€¹Ã£â‚¬â€šÃ§Â¶â„¢Ã§Â¶Å¡Ã£ï¿½â€”Ã£ï¿½Â¦Ã£â€šâ€šÃ£â€šÂ¨Ã£Æ’Â©Ã£Æ’Â¼Ã£ï¿½Â«Ã£ï¿½ÂªÃ£â€šâ€¹Ã£ï¿½Â Ã£ï¿½â€˜Ã£ï¿½ÂªÃ£ï¿½Â®Ã£ï¿½Â§return
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

        // Ã£Æ’â€ Ã£Æ’Â¬Ã£Æ’ï¿½Ã£Æ’Â¼Ã£Æ’Ë†Ã£â‚¬ï¿½Ã£Æ’Å¾Ã£â€šÂ¹Ã£Æ’â€ Ã£Æ’Â¬Ã£Æ’ï¿½Ã£Æ’Â¼Ã£Æ’Ë†Ã£ï¿½Â¯Ã¥Â¯Â¾Ã¨Â±Â¡Ã£ï¿½Å’Ã£Æ’â€“Ã£Æ’Æ’Ã£â€šÂ¯Ã£Æ’Å¾Ã£Æ’Â¼Ã£â€šÂ¯ID
        if ((_skillId == TELEPORT) || (_skillId == MASS_TELEPORT)) {
            _bookmarkId = target_id;
        }
        // Ã¥Â¯Â¾Ã¨Â±Â¡Ã£ï¿½Å’Ã£â€šÂ¢Ã£â€šÂ¤Ã£Æ’â€ Ã£Æ’Â Ã£ï¿½Â®Ã£â€šÂ¹Ã£â€šÂ­Ã£Æ’Â«
        if ((_skillId == CREATE_MAGICAL_WEAPON) || (_skillId == BRING_STONE) || (_skillId == BLESSED_ARMOR) || (_skillId == ENCHANT_WEAPON)
                || (_skillId == SHADOW_FANG)) {
            _itemobjid = target_id;
        }
        _target = (L1Character) l1object;

        if (!(_target instanceof L1MonsterInstance) && _skill.getTarget().equals("attack") && (_user.getId() != target_id)) {
            _isPK = true; // Ã£â€šÂ¿Ã£Æ’Â¼Ã£â€šÂ²Ã£Æ’Æ’Ã£Æ’Ë†Ã£ï¿½Å’Ã£Æ’Â¢Ã£Æ’Â³Ã£â€šÂ¹Ã£â€šÂ¿Ã£Æ’Â¼Ã¤Â»Â¥Ã¥Â¤â€“Ã£ï¿½Â§Ã¦â€�Â»Ã¦â€™Æ’Ã§Â³Â»Ã£â€šÂ¹Ã£â€šÂ­Ã£Æ’Â«Ã£ï¿½Â§Ã£â‚¬ï¿½Ã¨â€¡ÂªÃ¥Ë†â€ Ã¤Â»Â¥Ã¥Â¤â€“Ã£ï¿½Â®Ã¥Â Â´Ã¥ï¿½Ë†PKÃ£Æ’Â¢Ã£Æ’Â¼Ã£Æ’â€°Ã£ï¿½Â¨Ã£ï¿½â„¢Ã£â€šâ€¹Ã£â‚¬â€š
        }

        // Ã¥Ë†ï¿½Ã¦Å“Å¸Ã¨Â¨Â­Ã¥Â®Å¡Ã£ï¿½â€œÃ£ï¿½â€œÃ£ï¿½Â¾Ã£ï¿½Â§

        // Ã¤Âºâ€¹Ã¥â€°ï¿½Ã£Æ’ï¿½Ã£â€šÂ§Ã£Æ’Æ’Ã£â€šÂ¯
        if (!(l1object instanceof L1Character)) { // Ã£â€šÂ¿Ã£Æ’Â¼Ã£â€šÂ²Ã£Æ’Æ’Ã£Æ’Ë†Ã£ï¿½Å’Ã£â€šÂ­Ã£Æ’Â£Ã£Æ’Â©Ã£â€šÂ¯Ã£â€šÂ¿Ã£Æ’Â¼Ã¤Â»Â¥Ã¥Â¤â€“Ã£ï¿½Â®Ã¥Â Â´Ã¥ï¿½Ë†Ã¤Â½â€¢Ã£â€šâ€šÃ£ï¿½â€”Ã£ï¿½ÂªÃ£ï¿½â€žÃ£â‚¬â€š
            checkedResult = false;
        }
        makeTargetList(); // Ã£â€šÂ¿Ã£Æ’Â¼Ã£â€šÂ²Ã£Æ’Æ’Ã£Æ’Ë†Ã£ï¿½Â®Ã¤Â¸â‚¬Ã¨Â¦Â§Ã£â€šâ€™Ã¤Â½Å“Ã¦Ë†ï¿½
        if (_targetList.isEmpty() && (_user instanceof L1NpcInstance)) {
            checkedResult = false;
        }
        // Ã¤Âºâ€¹Ã¥â€°ï¿½Ã£Æ’ï¿½Ã£â€šÂ§Ã£Æ’Æ’Ã£â€šÂ¯Ã£ï¿½â€œÃ£ï¿½â€œÃ£ï¿½Â¾Ã£ï¿½Â§
        return checkedResult;
    }

    /**
     * Ã©â‚¬Å¡Ã¥Â¸Â¸Ã£ï¿½Â®Ã£â€šÂ¹Ã£â€šÂ­Ã£Æ’Â«Ã¤Â½Â¿Ã§â€�Â¨Ã¦â„¢â€šÃ£ï¿½Â«Ã¤Â½Â¿Ã§â€�Â¨Ã¨â‚¬â€¦Ã£ï¿½Â®Ã§Å Â¶Ã¦â€¦â€¹Ã£ï¿½â€¹Ã£â€šâ€°Ã£â€šÂ¹Ã£â€šÂ­Ã£Æ’Â«Ã£ï¿½Å’Ã¤Â½Â¿Ã§â€�Â¨Ã¥ï¿½Â¯Ã¨Æ’Â½Ã£ï¿½Â§Ã£ï¿½â€šÃ£â€šâ€¹Ã£ï¿½â€¹Ã¥Ë†Â¤Ã¦â€“Â­Ã£ï¿½â„¢Ã£â€šâ€¹
     *
     * @return false Ã£â€šÂ¹Ã£â€šÂ­Ã£Æ’Â«Ã£ï¿½Å’Ã¤Â½Â¿Ã§â€�Â¨Ã¤Â¸ï¿½Ã¥ï¿½Â¯Ã¨Æ’Â½Ã£ï¿½ÂªÃ§Å Â¶Ã¦â€¦â€¹Ã£ï¿½Â§Ã£ï¿½â€šÃ£â€šâ€¹Ã¥Â Â´Ã¥ï¿½Ë†
     */
    private boolean isNormalSkillUsable() {
        // Ã£â€šÂ¹Ã£â€šÂ­Ã£Æ’Â«Ã¤Â½Â¿Ã§â€�Â¨Ã¨â‚¬â€¦Ã£ï¿½Å’PCÃ£ï¿½Â®Ã¥Â Â´Ã¥ï¿½Ë†Ã£ï¿½Â®Ã£Æ’ï¿½Ã£â€šÂ§Ã£Æ’Æ’Ã£â€šÂ¯
        if (_user instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) _user;

            if (pc.isTeleport()) { // Ã¥â€šÂ³Ã©â‚¬ï¿½Ã¤Â¸Â­
                return false;
            }
            if (pc.isParalyzed()) { // Ã©ÂºÂ»Ã§â€”ÂºÃ£Æ’Â»Ã¥â€¡ï¿½Ã§Âµï¿½Ã§Å Â¶Ã¦â€¦â€¹Ã£ï¿½â€¹
                return false;
            }
            if ((pc.isInvisble() || pc.isInvisDelay()) && !isInvisUsableSkill()) { // Ã©Å¡Â±Ã¨ÂºÂ«Ã¤Â¸Â­Ã§â€žÂ¡Ã¦Â³â€¢Ã¤Â½Â¿Ã§â€�Â¨Ã¦Å â‚¬Ã¨Æ’Â½
                return false;
            }
            if (pc.getInventory().getWeight242() >= 197) { // \f1Ã¤Â½Â Ã¦â€�Å“Ã¥Â¸Â¶Ã¥Â¤ÂªÃ¥Â¤Å¡Ã§â€°Â©Ã¥â€œï¿½Ã¯Â¼Å’Ã¥â€ºÂ Ã¦Â­Â¤Ã§â€žÂ¡Ã¦Â³â€¢Ã¤Â½Â¿Ã§â€�Â¨Ã¦Â³â€¢Ã¨Â¡â€œÃ£â‚¬â€š
                pc.sendPackets(new S_ServerMessage(316));
                return false;
            }
            int polyId = pc.getTempCharGfx();
            L1PolyMorph poly = PolyTable.getInstance().getTemplate(polyId);
            // Ã©Â­â€�Ã¦Â³â€¢Ã£ï¿½Å’Ã¤Â½Â¿Ã£ï¿½Ë†Ã£ï¿½ÂªÃ£ï¿½â€žÃ¥Â¤â€°Ã¨ÂºÂ«
            if ((poly != null) && !poly.canUseSkill()) {
                pc.sendPackets(new S_ServerMessage(285)); // \f1Ã¥Å“Â¨Ã¦Â­Â¤Ã§â€¹â‚¬Ã¦â€¦â€¹Ã¤Â¸â€¹Ã§â€žÂ¡Ã¦Â³â€¢Ã¤Â½Â¿Ã§â€�Â¨Ã©Â­â€�Ã¦Â³â€¢Ã£â‚¬â€š
                return false;
            }

            if (!isAttrAgrees()) { // Ã§Â²Â¾Ã©Å“Å Ã©Â­â€�Ã¦Â³â€¢Ã£ï¿½Â§Ã£â‚¬ï¿½Ã¥Â±Å¾Ã¦â‚¬Â§Ã£ï¿½Å’Ã¤Â¸â‚¬Ã¨â€¡Â´Ã£ï¿½â€”Ã£ï¿½ÂªÃ£ï¿½â€˜Ã£â€šÅ’Ã£ï¿½Â°Ã¤Â½â€¢Ã£â€šâ€šÃ£ï¿½â€”Ã£ï¿½ÂªÃ£ï¿½â€žÃ£â‚¬â€š
                return false;
            }

            if ((_skillId == ELEMENTAL_PROTECTION) && (pc.getElfAttr() == 0)) {
                pc.sendPackets(new S_ServerMessage(280)); // \f1Ã¦â€“Â½Ã¥â€™â€™Ã¥Â¤Â±Ã¦â€¢â€”Ã£â‚¬â€š
                return false;
            }

            //DIsable Not casting underwater
            /*
            if (pc.getMap().isUnderwater() && _skill.getAttr() == 2) {
                pc.sendPackets(new S_ServerMessage(280)); // \f1Ã¦â€“Â½Ã¥â€™â€™Ã¥Â¤Â±Ã¦â€¢â€”Ã£â‚¬â€š
                return false;
            }
            */

            // Ã£â€šÂ¹Ã£â€šÂ­Ã£Æ’Â«Ã£Æ’â€¡Ã£â€šÂ£Ã£Æ’Â¬Ã£â€šÂ¤Ã¤Â¸Â­Ã¤Â½Â¿Ã§â€�Â¨Ã¤Â¸ï¿½Ã¥ï¿½Â¯
            if (pc.isSkillDelay()) {
                return false;
            }

            // Ã©Â­â€�Ã¦Â³â€¢Ã¥Â°ï¿½Ã¥ï¿½Â°Ã£â‚¬ï¿½Ã¥Â°ï¿½Ã¥ï¿½Â°Ã§Â¦ï¿½Ã¥Å“Â°Ã£â‚¬ï¿½Ã¥ï¿½Â¡Ã¦Â¯â€™Ã£â‚¬ï¿½Ã¥Â¹Â»Ã¦Æ’Â³
            if ((pc.hasSkillEffect(SILENCE) ||
                    pc.hasSkillEffect(AREA_OF_SILENCE) ||
                    pc.hasSkillEffect(STATUS_POISON_SILENCE)||
                    pc.hasSkillEffect(CONFUSION_ING)) &&
                    !IntArrays.sContains(CAST_WITH_SILENCE, _skillId)) {
                pc.sendPackets(new S_ServerMessage(285));
                return false;
            }

            // DIGÃ£ï¿½Â¯Ã£Æ’Â­Ã£â€šÂ¦Ã£Æ’â€¢Ã£Æ’Â«Ã£ï¿½Â§Ã£ï¿½Â®Ã£ï¿½Â¿Ã¤Â½Â¿Ã§â€�Â¨Ã¥ï¿½Â¯
            if ((_skillId == DISINTEGRATE) && (pc.getLawful() < 500)) {
                // Ã£ï¿½â€œÃ£ï¿½Â®Ã£Æ’Â¡Ã£Æ’Æ’Ã£â€šÂ»Ã£Æ’Â¼Ã£â€šÂ¸Ã£ï¿½Â§Ã£ï¿½â€šÃ£ï¿½Â£Ã£ï¿½Â¦Ã£â€šâ€¹Ã£ï¿½â€¹Ã¦Å“ÂªÃ§Â¢ÂºÃ¨Âªï¿½
                pc.sendPackets(new S_ServerMessage(352, "$967")); // Ã¨â€¹Â¥Ã¨Â¦ï¿½Ã¤Â½Â¿Ã§â€�Â¨Ã©â‚¬â„¢Ã¥â‚¬â€¹Ã¦Â³â€¢Ã¨Â¡â€œÃ¯Â¼Å’Ã¥Â±Â¬Ã¦â‚¬Â§Ã¥Â¿â€¦Ã©Â Ë†Ã¦Ë†ï¿½Ã§â€šÂº (Ã¦Â­Â£Ã§Â¾Â©)Ã£â‚¬â€š
                return false;
            }

            // Ã¥ï¿½Å’Ã£ï¿½ËœÃ£â€šÂ­Ã£Æ’Â¥Ã£Æ’Â¼Ã£Æ’â€“Ã£ï¿½Â¯Ã¥Å Â¹Ã¦Å¾Å“Ã§Â¯â€žÃ¥â€ºÂ²Ã¥Â¤â€“Ã£ï¿½Â§Ã£ï¿½â€šÃ£â€šÅ’Ã£ï¿½Â°Ã©â€¦ï¿½Ã§Â½Â®Ã¥ï¿½Â¯Ã¨Æ’Â½
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
                    pc.sendPackets(new S_ServerMessage(1412)); // Ã¥Â·Â²Ã¥Å“Â¨Ã¥Å“Â°Ã¦ï¿½Â¿Ã¤Â¸Å Ã¥ï¿½Â¬Ã¥â€“Å¡Ã¤Âºâ€ Ã©Â­â€�Ã¦Â³â€¢Ã§Â«â€¹Ã¦â€“Â¹Ã¥Â¡Å Ã£â‚¬â€š
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

            // Ã¨Â¦ÂºÃ©â€ â€™Ã§â€¹â‚¬Ã¦â€¦â€¹ - Ã©ï¿½Å¾Ã¨Â¦ÂºÃ©â€ â€™Ã¦Å â‚¬Ã¨Æ’Â½Ã§â€žÂ¡Ã¦Â³â€¢Ã¤Â½Â¿Ã§â€�Â¨
            //[Legends] - Disable Preventing them from casting with buffs on.
            /*
            if (((pc.getAwakeSkillId() == AWAKEN_ANTHARAS) && (_skillId != AWAKEN_ANTHARAS))
                    || ((pc.getAwakeSkillId() == AWAKEN_FAFURION) && (_skillId != AWAKEN_FAFURION))
                    || ((pc.getAwakeSkillId() == AWAKEN_VALAKAS) && (_skillId != AWAKEN_VALAKAS))
                    && (_skillId != MAGMA_BREATH) && (_skillId != SHOCK_SKIN) && (_skillId != FREEZING_BREATH)) {
                pc.sendPackets(new S_ServerMessage(1385)); // Ã§â€ºÂ®Ã¥â€°ï¿½Ã§â€¹â‚¬Ã¦â€¦â€¹Ã¤Â¸Â­Ã§â€žÂ¡Ã¦Â³â€¢Ã¤Â½Â¿Ã§â€�Â¨Ã¨Â¦ÂºÃ©â€ â€™Ã©Â­â€�Ã¦Â³â€¢Ã£â‚¬â€š
                return false;
            }*/

            // [Mike] Fix ItemConsume when casting spells..
            if ((isItemConsume() == false) && !_player.isGm()) { // Ã¦Â³â€¢Ã¨Â¡â€œÃ¦Â¶Ë†Ã¨â‚¬â€”Ã©ï¿½â€œÃ¥â€¦Â·Ã¥Ë†Â¤Ã¦â€“Â·Ã£â‚¬â€š
                _player.sendPackets(new S_ServerMessage(299)); // \f1Ã¦â€“Â½Ã¦â€�Â¾Ã©Â­â€�Ã¦Â³â€¢Ã¦â€°â‚¬Ã©Å“â‚¬Ã¦ï¿½ï¿½Ã¦â€“â„¢Ã¤Â¸ï¿½Ã¨Â¶Â³Ã£â‚¬â€š
                return false;
            }
        }
        // Ã£â€šÂ¹Ã£â€šÂ­Ã£Æ’Â«Ã¤Â½Â¿Ã§â€�Â¨Ã¨â‚¬â€¦Ã£ï¿½Å’NPCÃ£ï¿½Â®Ã¥Â Â´Ã¥ï¿½Ë†Ã£ï¿½Â®Ã£Æ’ï¿½Ã£â€šÂ§Ã£Æ’Æ’Ã£â€šÂ¯
        else if (_user instanceof L1NpcInstance) {

            // Ã£â€šÂµÃ£â€šÂ¤Ã£Æ’Â¬Ã£Æ’Â³Ã£â€šÂ¹Ã§Å Â¶Ã¦â€¦â€¹Ã£ï¿½Â§Ã£ï¿½Â¯Ã¤Â½Â¿Ã§â€�Â¨Ã¤Â¸ï¿½Ã¥ï¿½Â¯
            if (_user.hasSkillEffect(SILENCE)) {
                // NPCÃ£ï¿½Â«Ã£â€šÂµÃ£â€šÂ¤Ã£Æ’Â¬Ã£Æ’Â³Ã£â€šÂ¹Ã£ï¿½Å’Ã¦Å½â€ºÃ£ï¿½â€¹Ã£ï¿½Â£Ã£ï¿½Â¦Ã£ï¿½â€žÃ£â€šâ€¹Ã¥Â Â´Ã¥ï¿½Ë†Ã£ï¿½Â¯1Ã¥â€ºÅ¾Ã£ï¿½Â Ã£ï¿½â€˜Ã¤Â½Â¿Ã§â€�Â¨Ã£â€šâ€™Ã£â€šÂ­Ã£Æ’Â£Ã£Æ’Â³Ã£â€šÂ»Ã£Æ’Â«Ã£ï¿½â€¢Ã£ï¿½â€ºÃ£â€šâ€¹Ã¥Å Â¹Ã¦Å¾Å“Ã£â‚¬â€š
                _user.removeSkillEffect(SILENCE);
                return false;
            }
        }

        // PCÃ£â‚¬ï¿½NPCÃ¥â€¦Â±Ã©â‚¬Å¡Ã¦ÂªÂ¢Ã¦Å¸Â¥HPÃ£â‚¬ï¿½MPÃ¦ËœÂ¯Ã¥ï¿½Â¦Ã¨Â¶Â³Ã¥Â¤Â 
        if (!isHPMPConsume()) { // Ã¨Å Â±Ã¨Â²Â»Ã§Å¡â€žHPÃ£â‚¬ï¿½MPÃ¨Â¨Ë†Ã§Â®â€”
            return false;
        }
        return true;
    }

    /**
     * Ã£â€šÂ¹Ã£Æ’Å¡Ã£Æ’Â«Ã£â€šÂ¹Ã£â€šÂ¯Ã£Æ’Â­Ã£Æ’Â¼Ã£Æ’Â«Ã¤Â½Â¿Ã§â€�Â¨Ã¦â„¢â€šÃ£ï¿½Â«Ã¤Â½Â¿Ã§â€�Â¨Ã¨â‚¬â€¦Ã£ï¿½Â®Ã§Å Â¶Ã¦â€¦â€¹Ã£ï¿½â€¹Ã£â€šâ€°Ã£â€šÂ¹Ã£â€šÂ­Ã£Æ’Â«Ã£ï¿½Å’Ã¤Â½Â¿Ã§â€�Â¨Ã¥ï¿½Â¯Ã¨Æ’Â½Ã£ï¿½Â§Ã£ï¿½â€šÃ£â€šâ€¹Ã£ï¿½â€¹Ã¥Ë†Â¤Ã¦â€“Â­Ã£ï¿½â„¢Ã£â€šâ€¹
     *
     * @return false Ã£â€šÂ¹Ã£â€šÂ­Ã£Æ’Â«Ã£ï¿½Å’Ã¤Â½Â¿Ã§â€�Â¨Ã¤Â¸ï¿½Ã¥ï¿½Â¯Ã¨Æ’Â½Ã£ï¿½ÂªÃ§Å Â¶Ã¦â€¦â€¹Ã£ï¿½Â§Ã£ï¿½â€šÃ£â€šâ€¹Ã¥Â Â´Ã¥ï¿½Ë†
     */
    private boolean isSpellScrollUsable() {
        // Ã£â€šÂ¹Ã£Æ’Å¡Ã£Æ’Â«Ã£â€šÂ¹Ã£â€šÂ¯Ã£Æ’Â­Ã£Æ’Â¼Ã£Æ’Â«Ã£â€šâ€™Ã¤Â½Â¿Ã§â€�Â¨Ã£ï¿½â„¢Ã£â€šâ€¹Ã£ï¿½Â®Ã£ï¿½Â¯PCÃ£ï¿½Â®Ã£ï¿½Â¿
        L1PcInstance pc = (L1PcInstance) _user;

        if (pc.isTeleport()) { // Ã¥â€šÂ³Ã©â‚¬ï¿½Ã¤Â¸Â­
            return false;
        }

        if (pc.isParalyzed()) { // Ã©ÂºÂ»Ã§â€”ÂºÃ£Æ’Â»Ã¥â€¡ï¿½Ã§Âµï¿½Ã§Å Â¶Ã¦â€¦â€¹Ã£ï¿½â€¹
            return false;
        }

        // Ã£â€šÂ¤Ã£Æ’Â³Ã£Æ’â€œÃ£â€šÂ¸Ã¤Â¸Â­Ã£ï¿½Â«Ã¤Â½Â¿Ã§â€�Â¨Ã¤Â¸ï¿½Ã¥ï¿½Â¯Ã£ï¿½Â®Ã£â€šÂ¹Ã£â€šÂ­Ã£Æ’Â«
        if ((pc.isInvisble() || pc.isInvisDelay()) && !isInvisUsableSkill()) {
            return false;
        }

        return true;
    }

    // Ã£â€šÂ¤Ã£Æ’Â³Ã£Æ’â€œÃ£â€šÂ¸Ã¤Â¸Â­Ã£ï¿½Â«Ã¤Â½Â¿Ã§â€�Â¨Ã¥ï¿½Â¯Ã¨Æ’Â½Ã£ï¿½ÂªÃ£â€šÂ¹Ã£â€šÂ­Ã£Æ’Â«Ã£ï¿½â€¹Ã£â€šâ€™Ã¨Â¿â€�Ã£ï¿½â„¢
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
            // Ã¤Âºâ€¹Ã¥â€°ï¿½Ã£Æ’ï¿½Ã£â€šÂ§Ã£Æ’Æ’Ã£â€šÂ¯Ã£â€šâ€™Ã£ï¿½â€”Ã£ï¿½Â¦Ã£ï¿½â€žÃ£â€šâ€¹Ã£ï¿½â€¹Ã¯Â¼Å¸
            if (!isCheckedUseSkill()) {
                boolean isUseSkill = checkUseSkill(player, skillId, targetId, x, y, message, timeSecs, type, attacker);

                if (!isUseSkill) {
                    failSkill();
                    return;
                }
            }

            if (type == TYPE_NORMAL) { // Ã©Â­â€�Ã¦Â³â€¢Ã¨Â©Â Ã¥â€�Â±Ã¦â„¢â€š
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
            else if (type == TYPE_LOGIN) { // Ã£Æ’Â­Ã£â€šÂ°Ã£â€šÂ¤Ã£Æ’Â³Ã¦â„¢â€šÃ¯Â¼Ë†HPMPÃ¦ï¿½ï¿½Ã¦â€“â„¢Ã¦Â¶Ë†Ã¨Â²Â»Ã£ï¿½ÂªÃ£ï¿½â€”Ã£â‚¬ï¿½Ã£â€šÂ°Ã£Æ’Â©Ã£Æ’â€¢Ã£â€šÂ£Ã£Æ’Æ’Ã£â€šÂ¯Ã£ï¿½ÂªÃ£ï¿½â€”Ã¯Â¼â€°
                runSkill();
            }
            else if (type == TYPE_SPELLSC) { // Ã£â€šÂ¹Ã£Æ’Å¡Ã£Æ’Â«Ã£â€šÂ¹Ã£â€šÂ¯Ã£Æ’Â­Ã£Æ’Â¼Ã£Æ’Â«Ã¤Â½Â¿Ã§â€�Â¨Ã¦â„¢â€šÃ¯Â¼Ë†HPMPÃ¦ï¿½ï¿½Ã¦â€“â„¢Ã¦Â¶Ë†Ã¨Â²Â»Ã£ï¿½ÂªÃ£ï¿½â€”Ã¯Â¼â€°
                runSkill();
                sendGrfx(true);
            }
            else if (type == TYPE_GMBUFF) { // GMBUFFÃ¤Â½Â¿Ã§â€�Â¨Ã¦â„¢â€šÃ¯Â¼Ë†HPMPÃ¦ï¿½ï¿½Ã¦â€“â„¢Ã¦Â¶Ë†Ã¨Â²Â»Ã£ï¿½ÂªÃ£ï¿½â€”Ã£â‚¬ï¿½Ã©Â­â€�Ã¦Â³â€¢Ã£Æ’Â¢Ã£Æ’Â¼Ã£â€šÂ·Ã£Æ’Â§Ã£Æ’Â³Ã£ï¿½ÂªÃ£ï¿½â€”Ã¯Â¼â€°
                runSkill();
                sendGrfx(false);
            }
            else if (type == TYPE_NPCBUFF) { // NPCBUFFÃ¤Â½Â¿Ã§â€�Â¨Ã¦â„¢â€šÃ¯Â¼Ë†HPMPÃ¦ï¿½ï¿½Ã¦â€“â„¢Ã¦Â¶Ë†Ã¨Â²Â»Ã£ï¿½ÂªÃ£ï¿½â€”Ã¯Â¼â€°
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
     * Ã£â€šÂ¹Ã£â€šÂ­Ã£Æ’Â«Ã£ï¿½Â®Ã¥Â¤Â±Ã¦â€¢â€”Ã¥â€¡Â¦Ã§ï¿½â€ (PCÃ£ï¿½Â®Ã£ï¿½Â¿Ã¯Â¼â€°
     */
    private void failSkill() {
        // HPÃ£ï¿½Å’Ã¨Â¶Â³Ã£â€šÅ Ã£ï¿½ÂªÃ£ï¿½ï¿½Ã£ï¿½Â¦Ã£â€šÂ¹Ã£â€šÂ­Ã£Æ’Â«Ã£ï¿½Å’Ã¤Â½Â¿Ã§â€�Â¨Ã£ï¿½Â§Ã£ï¿½ï¿½Ã£ï¿½ÂªÃ£ï¿½â€žÃ¥Â Â´Ã¥ï¿½Ë†Ã£ï¿½Â®Ã£ï¿½Â¿Ã£â‚¬ï¿½MPÃ£ï¿½Â®Ã£ï¿½Â¿Ã¦Â¶Ë†Ã¨Â²Â»Ã£ï¿½â€”Ã£ï¿½Å¸Ã£ï¿½â€žÃ£ï¿½Å’Ã¦Å“ÂªÃ¥Â®Å¸Ã¨Â£â€¦Ã¯Â¼Ë†Ã¥Â¿â€¦Ã¨Â¦ï¿½Ã£ï¿½ÂªÃ£ï¿½â€žÃ¯Â¼Å¸Ã¯Â¼â€°
        // Ã£ï¿½ï¿½Ã£ï¿½Â®Ã¤Â»â€“Ã£ï¿½Â®Ã¥Â Â´Ã¥ï¿½Ë†Ã£ï¿½Â¯Ã¤Â½â€¢Ã£â€šâ€šÃ¦Â¶Ë†Ã¨Â²Â»Ã£ï¿½â€¢Ã£â€šÅ’Ã£ï¿½ÂªÃ£ï¿½â€žÃ£â‚¬â€š
        // useConsume(); // HPÃ£â‚¬ï¿½MPÃ£ï¿½Â¯Ã¦Â¸â€ºÃ£â€šâ€°Ã£ï¿½â„¢
        setCheckedUseSkill(false);
        // Ã£Æ’â€ Ã£Æ’Â¬Ã£Æ’ï¿½Ã£Æ’Â¼Ã£Æ’Ë†Ã£â€šÂ¹Ã£â€šÂ­Ã£Æ’Â«
        if ((_skillId == TELEPORT) || (_skillId == MASS_TELEPORT) || (_skillId == TELEPORT_TO_MATHER)) {
            // Ã£Æ’â€ Ã£Æ’Â¬Ã£Æ’ï¿½Ã£Æ’Â¼Ã£Æ’Ë†Ã£ï¿½Â§Ã£ï¿½ï¿½Ã£ï¿½ÂªÃ£ï¿½â€žÃ¥Â Â´Ã¥ï¿½Ë†Ã£ï¿½Â§Ã£â€šâ€šÃ£â‚¬ï¿½Ã£â€šÂ¯Ã£Æ’Â©Ã£â€šÂ¤Ã£â€šÂ¢Ã£Æ’Â³Ã£Æ’Ë†Ã¥ï¿½Â´Ã£ï¿½Â¯Ã¥Â¿Å“Ã§Â­â€�Ã£â€šâ€™Ã¥Â¾â€¦Ã£ï¿½Â£Ã£ï¿½Â¦Ã£ï¿½â€žÃ£â€šâ€¹
            // Ã£Æ’â€ Ã£Æ’Â¬Ã£Æ’ï¿½Ã£Æ’Â¼Ã£Æ’Ë†Ã¥Â¾â€¦Ã£ï¿½Â¡Ã§Å Â¶Ã¦â€¦â€¹Ã£ï¿½Â®Ã¨Â§Â£Ã©â„¢Â¤Ã¯Â¼Ë†Ã§Â¬Â¬2Ã¥Â¼â€¢Ã¦â€¢Â°Ã£ï¿½Â«Ã¦â€žï¿½Ã¥â€˜Â³Ã£ï¿½Â¯Ã£ï¿½ÂªÃ£ï¿½â€žÃ¯Â¼â€°
            _player.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
        }
    }

    // Ã£â€šÂ¿Ã£Æ’Â¼Ã£â€šÂ²Ã£Æ’Æ’Ã£Æ’Ë†Ã£ï¿½â€¹Ã¯Â¼Å¸
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

        // Ã§Â Â´Ã¥Â£Å Ã¤Â¸ï¿½Ã¥ï¿½Â¯Ã¨Æ’Â½Ã£ï¿½ÂªÃ£Æ’â€°Ã£â€šÂ¢Ã£ï¿½Â¯Ã¥Â¯Â¾Ã¨Â±Â¡Ã¥Â¤â€“
        if (cha instanceof L1DoorInstance) {
            if ((cha.getMaxHp() == 0) || (cha.getMaxHp() == 1)) {
                return false;
            }
        }

        // Ã£Æ’Å¾Ã£â€šÂ¸Ã£Æ’Æ’Ã£â€šÂ¯Ã£Æ’â€°Ã£Æ’Â¼Ã£Æ’Â«Ã£ï¿½Â¯Ã¥Â¯Â¾Ã¨Â±Â¡Ã¥Â¤â€“
        if ((cha instanceof L1DollInstance) && (_skillId != HASTE)) {
            return false;
        }

        // Ã¥â€¦Æ’Ã£ï¿½Â®Ã£â€šÂ¿Ã£Æ’Â¼Ã£â€šÂ²Ã£Æ’Æ’Ã£Æ’Ë†Ã£ï¿½Å’PetÃ£â‚¬ï¿½SummonÃ¤Â»Â¥Ã¥Â¤â€“Ã£ï¿½Â®NPCÃ£ï¿½Â®Ã¥Â Â´Ã¥ï¿½Ë†Ã£â‚¬ï¿½PCÃ£â‚¬ï¿½PetÃ£â‚¬ï¿½SummonÃ£ï¿½Â¯Ã¥Â¯Â¾Ã¨Â±Â¡Ã¥Â¤â€“
        if ((_calcType == PC_NPC) && (_target instanceof L1NpcInstance) && !(_target instanceof L1PetInstance)
                && !(_target instanceof L1SummonInstance)
                && ((cha instanceof L1PetInstance) || (cha instanceof L1SummonInstance) || (cha instanceof L1PcInstance))) {
            return false;
        }

        // Ã¥â€¦Æ’Ã£ï¿½Â®Ã£â€šÂ¿Ã£Æ’Â¼Ã£â€šÂ²Ã£Æ’Æ’Ã£Æ’Ë†Ã£ï¿½Å’Ã£â€šÂ¬Ã£Æ’Â¼Ã£Æ’â€°Ã¤Â»Â¥Ã¥Â¤â€“Ã£ï¿½Â®NPCÃ£ï¿½Â®Ã¥Â Â´Ã¥ï¿½Ë†Ã£â‚¬ï¿½Ã£â€šÂ¬Ã£Æ’Â¼Ã£Æ’â€°Ã£ï¿½Â¯Ã¥Â¯Â¾Ã¨Â±Â¡Ã¥Â¤â€“
        if ((_calcType == PC_NPC) && (_target instanceof L1NpcInstance) && !(_target instanceof L1GuardInstance) && (cha instanceof L1GuardInstance)) {
            return false;
        }

        // NPCÃ¥Â¯Â¾PCÃ£ï¿½Â§Ã£â€šÂ¿Ã£Æ’Â¼Ã£â€šÂ²Ã£Æ’Æ’Ã£Æ’Ë†Ã£ï¿½Å’Ã£Æ’Â¢Ã£Æ’Â³Ã£â€šÂ¹Ã£â€šÂ¿Ã£Æ’Â¼Ã£ï¿½Â®Ã¥Â Â´Ã¥ï¿½Ë†Ã£â€šÂ¿Ã£Æ’Â¼Ã£â€šÂ²Ã£Æ’Æ’Ã£Æ’Ë†Ã£ï¿½Â§Ã£ï¿½Â¯Ã£ï¿½ÂªÃ£ï¿½â€žÃ£â‚¬â€š
        if ((_skill.getTarget().equals("attack") || (_skill.getType() == L1Skills.TYPE_ATTACK)) && (_calcType == NPC_PC)
                && !(cha instanceof L1PetInstance) && !(cha instanceof L1SummonInstance) && !(cha instanceof L1PcInstance)) {
            return false;
        }

        // NPCÃ¥Â¯Â¾NPCÃ£ï¿½Â§Ã¤Â½Â¿Ã§â€�Â¨Ã¨â‚¬â€¦Ã£ï¿½Å’MOBÃ£ï¿½Â§Ã£â‚¬ï¿½Ã£â€šÂ¿Ã£Æ’Â¼Ã£â€šÂ²Ã£Æ’Æ’Ã£Æ’Ë†Ã£ï¿½Å’MOBÃ£ï¿½Â®Ã¥Â Â´Ã¥ï¿½Ë†Ã£â€šÂ¿Ã£Æ’Â¼Ã£â€šÂ²Ã£Æ’Æ’Ã£Æ’Ë†Ã£ï¿½Â§Ã£ï¿½Â¯Ã£ï¿½ÂªÃ£ï¿½â€žÃ£â‚¬â€š
        if ((_skill.getTarget().equals("attack") || (_skill.getType() == L1Skills.TYPE_ATTACK)) && (_calcType == NPC_NPC)
                && (_user instanceof L1MonsterInstance) && (cha instanceof L1MonsterInstance)) {
            return false;
        }

        // Ã§â€žÂ¡Ã¦â€“Â¹Ã¥ï¿½â€˜Ã§Â¯â€žÃ¥â€ºÂ²Ã¦â€�Â»Ã¦â€™Æ’Ã©Â­â€�Ã¦Â³â€¢Ã£ï¿½Â§Ã¦â€�Â»Ã¦â€™Æ’Ã£ï¿½Â§Ã£ï¿½ï¿½Ã£ï¿½ÂªÃ£ï¿½â€žNPCÃ£ï¿½Â¯Ã¥Â¯Â¾Ã¨Â±Â¡Ã¥Â¤â€“
        if (_skill.getTarget().equals("none")
                && (_skill.getType() == L1Skills.TYPE_ATTACK)
                && ((cha instanceof L1AuctionBoardInstance) || (cha instanceof L1BoardInstance) || (cha instanceof L1CrownInstance)
                || (cha instanceof L1DwarfInstance) || (cha instanceof L1EffectInstance) || (cha instanceof L1FieldObjectInstance)
                || (cha instanceof L1FurnitureInstance) || (cha instanceof L1HousekeeperInstance) || (cha instanceof L1MerchantInstance) || (cha instanceof L1TeleporterInstance))) {
            return false;
        }

        // Ã¦â€�Â»Ã¦â€œÅ Ã¥Å¾â€¹Ã©Â­â€�Ã¦Â³â€¢Ã§â€žÂ¡Ã¦Â³â€¢Ã¦â€�Â»Ã¦â€œÅ Ã¨â€¡ÂªÃ¥Â·Â±
        if ((_skill.getType() == L1Skills.TYPE_ATTACK) && (cha.getId() == _user.getId())) {
            return false;
        }

        // Ã©Â«â€�Ã¥Å â€ºÃ¥â€ºÅ¾Ã¥Â¾Â©Ã¨Â¡â€œÃ¥Ë†Â¤Ã¦â€“Â·Ã¦â€“Â½Ã¦Â³â€¢Ã¨â‚¬â€¦Ã¤Â¸ï¿½Ã¨Â£Å“Ã¨Â¡â‚¬
        if ((cha.getId() == _user.getId()) && (_skillId == HEAL_ALL)) {
            return false;
        }

        if ((((_skill.getTargetTo() & L1Skills.TARGET_TO_PC) == L1Skills.TARGET_TO_PC)
                || ((_skill.getTargetTo() & L1Skills.TARGET_TO_CLAN) == L1Skills.TARGET_TO_CLAN) || ((_skill.getTargetTo() & L1Skills.TARGET_TO_PARTY) == L1Skills.TARGET_TO_PARTY))
                && (cha.getId() == _user.getId()) && (_skillId != HEAL_ALL)) {
            return true; // Ã£â€šÂ¿Ã£Æ’Â¼Ã£â€šÂ²Ã£Æ’Æ’Ã£Æ’Ë†Ã£ï¿½Å’Ã£Æ’â€˜Ã£Æ’Â¼Ã£Æ’â€ Ã£â€šÂ£Ã£Æ’Â¼Ã£ï¿½â€¹Ã£â€šÂ¯Ã£Æ’Â©Ã£Æ’Â³Ã¥â€œÂ¡Ã£ï¿½Â®Ã£â€šâ€šÃ£ï¿½Â®Ã£ï¿½Â¯Ã¨â€¡ÂªÃ¥Ë†â€ Ã£ï¿½Â«Ã¥Å Â¹Ã¦Å¾Å“Ã£ï¿½Å’Ã£ï¿½â€šÃ£â€šâ€¹Ã£â‚¬â€šÃ¯Â¼Ë†Ã£ï¿½Å¸Ã£ï¿½Â Ã£ï¿½â€”Ã£â‚¬ï¿½Ã£Æ’â€™Ã£Æ’Â¼Ã£Æ’Â«Ã£â€šÂªÃ£Æ’Â¼Ã£Æ’Â«Ã£ï¿½Â¯Ã©â„¢Â¤Ã¥Â¤â€“Ã¯Â¼â€°
        }

        // Ã£â€šÂ¹Ã£â€šÂ­Ã£Æ’Â«Ã¤Â½Â¿Ã§â€�Â¨Ã¨â‚¬â€¦Ã£ï¿½Å’PCÃ£ï¿½Â§Ã£â‚¬ï¿½PKÃ£Æ’Â¢Ã£Æ’Â¼Ã£Æ’â€°Ã£ï¿½Â§Ã£ï¿½Â¯Ã£ï¿½ÂªÃ£ï¿½â€žÃ¥Â Â´Ã¥ï¿½Ë†Ã£â‚¬ï¿½Ã¨â€¡ÂªÃ¥Ë†â€ Ã£ï¿½Â®Ã£â€šÂµÃ£Æ’Â¢Ã£Æ’Â³Ã£Æ’Â»Ã£Æ’Å¡Ã£Æ’Æ’Ã£Æ’Ë†Ã£ï¿½Â¯Ã¥Â¯Â¾Ã¨Â±Â¡Ã¥Â¤â€“
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
            // Ã£â€šÂ«Ã£â€šÂ¦Ã£Æ’Â³Ã£â€šÂ¿Ã£Æ’Â¼Ã£Æ’â€¡Ã£â€šÂ£Ã£Æ’â€ Ã£â€šÂ¯Ã£â€šÂ·Ã£Æ’Â§Ã£Æ’Â³
            if ((_skillId == COUNTER_DETECTION) && (enemy.getZoneType() != 1)
                    && (cha.hasSkillEffect(INVISIBILITY) || cha.hasSkillEffect(BLIND_HIDING))) {
                return true; // Ã£â€šÂ¤Ã£Æ’Â³Ã£Æ’â€œÃ£â€šÂ¸Ã£ï¿½â€¹Ã£Æ’â€“Ã£Æ’Â©Ã£â€šÂ¤Ã£Æ’Â³Ã£Æ’â€°Ã£Æ’ï¿½Ã£â€šÂ¤Ã£Æ’â€¡Ã£â€šÂ£Ã£Æ’Â³Ã£â€šÂ°Ã¤Â¸Â­
            }
            if ((_skillId == COUNTER_DETECTION) && (enemy.getZoneType() != 1)
                    && !(cha.hasSkillEffect(INVISIBILITY) || cha.hasSkillEffect(BLIND_HIDING))) {
                return false; // added to try to fix CD
            }
            if ((_player.getClanid() != 0) && (enemy.getClanid() != 0)) { // Ã£â€šÂ¯Ã£Æ’Â©Ã£Æ’Â³Ã¦â€°â‚¬Ã¥Â±Å¾Ã¤Â¸Â­
                // Ã¥â€¦Â¨Ã¦Ë†Â¦Ã¤Âºâ€°Ã£Æ’ÂªÃ£â€šÂ¹Ã£Æ’Ë†Ã£â€šâ€™Ã¥ï¿½â€“Ã¥Â¾â€”
                for (L1War war : L1World.getInstance().getWarList()) {
                    if (war.CheckClanInWar(_player.getClanname())) { // Ã¨â€¡ÂªÃ£â€šÂ¯Ã£Æ’Â©Ã£Æ’Â³Ã£ï¿½Å’Ã¦Ë†Â¦Ã¤Âºâ€°Ã£ï¿½Â«Ã¥ï¿½â€šÃ¥Å Â Ã¤Â¸Â­
                        if (war.CheckClanInSameWar( // Ã¥ï¿½Å’Ã£ï¿½ËœÃ¦Ë†Â¦Ã¤Âºâ€°Ã£ï¿½Â«Ã¥ï¿½â€šÃ¥Å Â Ã¤Â¸Â­
                                _player.getClanname(), enemy.getClanname())) {
                            if (L1CastleLocation.checkInAllWarArea(enemy.getX(), enemy.getY(), enemy.getMapId())) {
                                return true;
                            }
                        }
                    }
                }
            }
            return false; // Ã¦â€�Â»Ã¦â€™Æ’Ã£â€šÂ¹Ã£â€šÂ­Ã£Æ’Â«Ã£ï¿½Â§PKÃ£Æ’Â¢Ã£Æ’Â¼Ã£Æ’â€°Ã£ï¿½ËœÃ£â€šÆ’Ã£ï¿½ÂªÃ£ï¿½â€žÃ¥Â Â´Ã¥ï¿½Ë†
        }

        if ((_user.glanceCheck(cha.getX(), cha.getY()) == false) && (_skill.isThrough() == false)) {
            // Ã£â€šÂ¨Ã£Æ’Â³Ã£Æ’ï¿½Ã£Æ’Â£Ã£Æ’Â³Ã£Æ’Ë†Ã£â‚¬ï¿½Ã¥Â¾Â©Ã¦Â´Â»Ã£â€šÂ¹Ã£â€šÂ­Ã£Æ’Â«Ã£ï¿½Â¯Ã©Å¡Å“Ã¥Â®Â³Ã§â€°Â©Ã£ï¿½Â®Ã¥Ë†Â¤Ã¥Â®Å¡Ã£â€šâ€™Ã£ï¿½â€”Ã£ï¿½ÂªÃ£ï¿½â€ž
            if (!((_skill.getType() == L1Skills.TYPE_CHANGE) || (_skill.getType() == L1Skills.TYPE_RESTORE))) {
                _isGlanceCheckFail = true;
                return false; // Ã§â€ºÂ´Ã§Â·Å¡Ã¤Â¸Å Ã£ï¿½Â«Ã©Å¡Å“Ã¥Â®Â³Ã§â€°Â©Ã£ï¿½Å’Ã£ï¿½â€šÃ£â€šâ€¹
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
                        return false; // Ã£â€šÂ¢Ã£â€šÂ¤Ã£â€šÂ¹Ã£Æ’Â©Ã£Æ’Â³Ã£â€šÂ¹Ã¤Â¸Â­Ã£ï¿½Â«Ã£â€šÂ¢Ã£â€šÂ¤Ã£â€šÂ¹Ã£Æ’Â©Ã£Æ’Â³Ã£â€šÂ¹Ã£â‚¬ï¿½Ã£Æ’â€¢Ã£Æ’ÂªÃ£Æ’Â¼Ã£â€šÂ¸Ã£Æ’Â³Ã£â€šÂ°Ã£Æ’â€“Ã£Æ’ÂªÃ£â€šÂ¶Ã£Æ’Â¼Ã£Æ’â€°Ã£â‚¬ï¿½Ã£Æ’â€¢Ã£Æ’ÂªÃ£Æ’Â¼Ã£â€šÂ¸Ã£Æ’Â³Ã£â€šÂ°Ã£Æ’â€“Ã£Æ’Â¬Ã£â€šÂ¹
                }

                if (cha.hasSkillEffect(FREEZING_BLIZZARD) && ((_skillId == ICE_LANCE) || (_skillId == FREEZING_BLIZZARD) || (_skillId == FREEZING_BREATH))) {
                        return false; // Ã£Æ’â€¢Ã£Æ’ÂªÃ£Æ’Â¼Ã£â€šÂ¸Ã£Æ’Â³Ã£â€šÂ°Ã£Æ’â€“Ã£Æ’ÂªÃ£â€šÂ¶Ã£Æ’Â¼Ã£Æ’â€°Ã¤Â¸Â­Ã£ï¿½Â«Ã£â€šÂ¢Ã£â€šÂ¤Ã£â€šÂ¹Ã£Æ’Â©Ã£Æ’Â³Ã£â€šÂ¹Ã£â‚¬ï¿½Ã£Æ’â€¢Ã£Æ’ÂªÃ£Æ’Â¼Ã£â€šÂ¸Ã£Æ’Â³Ã£â€šÂ°Ã£Æ’â€“Ã£Æ’ÂªÃ£â€šÂ¶Ã£Æ’Â¼Ã£Æ’â€°Ã£â‚¬ï¿½Ã£Æ’â€¢Ã£Æ’ÂªÃ£Æ’Â¼Ã£â€šÂ¸Ã£Æ’Â³Ã£â€šÂ°Ã£Æ’â€“Ã£Æ’Â¬Ã£â€šÂ¹
                }

                if (cha.hasSkillEffect(FREEZING_BREATH) && ((_skillId == ICE_LANCE) || (_skillId == FREEZING_BLIZZARD) || (_skillId == FREEZING_BREATH))) {
                        return false; // Ã£Æ’â€¢Ã£Æ’ÂªÃ£Æ’Â¼Ã£â€šÂ¸Ã£Æ’Â³Ã£â€šÂ°Ã£Æ’â€“Ã£Æ’Â¬Ã£â€šÂ¹Ã¤Â¸Â­Ã£ï¿½Â«Ã£â€šÂ¢Ã£â€šÂ¤Ã£â€šÂ¹Ã£Æ’Â©Ã£Æ’Â³Ã£â€šÂ¹Ã£â‚¬ï¿½Ã£Æ’â€¢Ã£Æ’ÂªÃ£Æ’Â¼Ã£â€šÂ¸Ã£Æ’Â³Ã£â€šÂ°Ã£Æ’â€“Ã£Æ’ÂªÃ£â€šÂ¶Ã£Æ’Â¼Ã£Æ’â€°Ã£â‚¬ï¿½Ã£Æ’â€¢Ã£Æ’ÂªÃ£Æ’Â¼Ã£â€šÂ¸Ã£Æ’Â³Ã£â€šÂ°Ã£Æ’â€“Ã£Æ’Â¬Ã£â€šÂ¹
                }
*/
        if (cha.hasSkillEffect(EARTH_BIND) && (_skillId == EARTH_BIND)) {
            return false; // Ã£â€šÂ¢Ã£Æ’Â¼Ã£â€šÂ¹ Ã£Æ’ï¿½Ã£â€šÂ¤Ã£Æ’Â³Ã£Æ’â€°Ã¤Â¸Â­Ã£ï¿½Â«Ã£â€šÂ¢Ã£Æ’Â¼Ã£â€šÂ¹ Ã£Æ’ï¿½Ã£â€šÂ¤Ã£Æ’Â³Ã£Æ’â€°
        }

        if (!(cha instanceof L1MonsterInstance) && ((_skillId == TAMING_MONSTER) || (_skillId == CREATE_ZOMBIE))) {
            return false; // Ã£â€šÂ¿Ã£Æ’Â¼Ã£â€šÂ²Ã£Æ’Æ’Ã£Æ’Ë†Ã£ï¿½Å’Ã£Æ’Â¢Ã£Æ’Â³Ã£â€šÂ¹Ã£â€šÂ¿Ã£Æ’Â¼Ã£ï¿½ËœÃ£â€šÆ’Ã£ï¿½ÂªÃ£ï¿½â€žÃ¯Â¼Ë†Ã£Æ’â€ Ã£â€šÂ¤Ã£Æ’Å¸Ã£Æ’Â³Ã£â€šÂ°Ã£Æ’Â¢Ã£Æ’Â³Ã£â€šÂ¹Ã£â€šÂ¿Ã£Æ’Â¼Ã¯Â¼â€°
        }
        if (cha.isDead()
                && ((_skillId != CREATE_ZOMBIE) && (_skillId != RESURRECTION) && (_skillId != GREATER_RESURRECTION) && (_skillId != CALL_OF_NATURE))) {
            return false; // Ã§â€ºÂ®Ã¦Â¨â„¢Ã¥Â·Â²Ã¦Â­Â»Ã¤ÂºÂ¡ Ã¦Â³â€¢Ã¨Â¡â€œÃ©ï¿½Å¾Ã¥Â¾Â©Ã¦Â´Â»Ã©Â¡Å¾
        }

        if ((cha.isDead() == false)
                && ((_skillId == CREATE_ZOMBIE) || (_skillId == RESURRECTION) || (_skillId == GREATER_RESURRECTION) || (_skillId == CALL_OF_NATURE))) {
            return false; // Ã§â€ºÂ®Ã¦Â¨â„¢Ã¦Å“ÂªÃ¦Â­Â»Ã¤ÂºÂ¡ Ã¦Â³â€¢Ã¨Â¡â€œÃ¥Â¾Â©Ã¦Â´Â»Ã©Â¡Å¾
        }

        if (((cha instanceof L1TowerInstance) || (cha instanceof L1DoorInstance))
                && ((_skillId == CREATE_ZOMBIE) || (_skillId == RESURRECTION) || (_skillId == GREATER_RESURRECTION) || (_skillId == CALL_OF_NATURE))) {
            return false; // Ã¥Â¡â€�Ã¨Â·Å¸Ã©â€“â‚¬Ã¤Â¸ï¿½Ã¥ï¿½Â¯Ã¦â€�Â¾Ã¥Â¾Â©Ã¦Â´Â»Ã¦Â³â€¢Ã¨Â¡â€œ
        }

        if (cha instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) cha;
            if (pc.hasSkillEffect(ABSOLUTE_BARRIER)) { // Ã§Âµâ€¢Ã¥Â°ï¿½Ã¥Â±ï¿½Ã©Å¡Å“Ã§â€¹â‚¬Ã¦â€¦â€¹Ã¤Â¸Â­
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
                if ((_skillId == DETECTION) || (_skillId == COUNTER_DETECTION)) { // Ã£Æ’â€¡Ã£â€šÂ£Ã£Æ’â€ Ã£â€šÂ¯Ã£â‚¬ï¿½CÃ£Æ’â€¡Ã£â€šÂ£Ã£Æ’â€ Ã£â€šÂ¯
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

        if (((_skill.getTargetTo() & L1Skills.TARGET_TO_PC) == L1Skills.TARGET_TO_PC // Ã£â€šÂ¿Ã£Æ’Â¼Ã£â€šÂ²Ã£Æ’Æ’Ã£Æ’Ë†Ã£ï¿½Å’PC
        )
                && (cha instanceof L1PcInstance)) {
            _flg = true;
        }
        else if (((_skill.getTargetTo() & L1Skills.TARGET_TO_NPC) == L1Skills.TARGET_TO_NPC // Ã£â€šÂ¿Ã£Æ’Â¼Ã£â€šÂ²Ã£Æ’Æ’Ã£Æ’Ë†Ã£ï¿½Å’NPC
        )
                && ((cha instanceof L1MonsterInstance) || (cha instanceof L1NpcInstance) || (cha instanceof L1SummonInstance) || (cha instanceof L1PetInstance))) {
            _flg = true;
        }
        else if (((_skill.getTargetTo() & L1Skills.TARGET_TO_PET) == L1Skills.TARGET_TO_PET) && (_user instanceof L1PcInstance)) { // Ã£â€šÂ¿Ã£Æ’Â¼Ã£â€šÂ²Ã£Æ’Æ’Ã£Æ’Ë†Ã£ï¿½Å’Summon,Pet
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
            if (((_skill.getTargetTo() & L1Skills.TARGET_TO_CLAN) == L1Skills.TARGET_TO_CLAN) && (((_player.getClanid() != 0 // Ã£â€šÂ¿Ã£Æ’Â¼Ã£â€šÂ²Ã£Æ’Æ’Ã£Æ’Ë†Ã£ï¿½Å’Ã£â€šÂ¯Ã£Æ’Â©Ã£Æ’Â³Ã¥â€œÂ¡
            ) && (_player.getClanid() == ((L1PcInstance) cha).getClanid())) || _player.isGm())) {
                return true;
            }
            if (((_skill.getTargetTo() & L1Skills.TARGET_TO_PARTY) == L1Skills.TARGET_TO_PARTY) && (_player.getParty() // Ã£â€šÂ¿Ã£Æ’Â¼Ã£â€šÂ²Ã£Æ’Æ’Ã£Æ’Ë†Ã£ï¿½Å’Ã£Æ’â€˜Ã£Æ’Â¼Ã£Æ’â€ Ã£â€šÂ£Ã£Æ’Â¼
                    .isMember((L1PcInstance) cha) || _player.isGm())) {
                return true;
            }
        }

        return _flg;
    }

    // Ã£â€šÂ¿Ã£Æ’Â¼Ã£â€šÂ²Ã£Æ’Æ’Ã£Æ’Ë†Ã£ï¿½Â®Ã¤Â¸â‚¬Ã¨Â¦Â§Ã£â€šâ€™Ã¤Â½Å“Ã¦Ë†ï¿½
    private void makeTargetList() {
        try {
            if (_type == TYPE_LOGIN) { // Ã£Æ’Â­Ã£â€šÂ°Ã£â€šÂ¤Ã£Æ’Â³Ã¦â„¢â€š(Ã¦Â­Â»Ã¤ÂºÂ¡Ã¦â„¢â€šÃ£â‚¬ï¿½Ã£ï¿½Å Ã¥Å’â€“Ã£ï¿½â€˜Ã¥Â±â€¹Ã¦â€¢Â·Ã£ï¿½Â®Ã£â€šÂ­Ã£Æ’Â£Ã£Æ’Â³Ã£â€šÂ»Ã£Æ’Â¬Ã£Æ’Â¼Ã£â€šÂ·Ã£Æ’Â§Ã£Æ’Â³Ã¥ï¿½Â«Ã£â€šâ‚¬)Ã£ï¿½Â¯Ã¤Â½Â¿Ã§â€�Â¨Ã¨â‚¬â€¦Ã£ï¿½Â®Ã£ï¿½Â¿
                _targetList.add(new TargetStatus(_user));
                return;
            }
            if ((_skill.getTargetTo() == L1Skills.TARGET_TO_ME) && ((_skill.getType() & L1Skills.TYPE_ATTACK) != L1Skills.TYPE_ATTACK)) {
                _targetList.add(new TargetStatus(_user)); // Ã£â€šÂ¿Ã£Æ’Â¼Ã£â€šÂ²Ã£Æ’Æ’Ã£Æ’Ë†Ã£ï¿½Â¯Ã¤Â½Â¿Ã§â€�Â¨Ã¨â‚¬â€¦Ã£ï¿½Â®Ã£ï¿½Â¿
                return;
            }

            // Ã¥Â°â€žÃ§Â¨â€¹Ã¨Â·ï¿½Ã©â€ºÂ¢-1Ã£ï¿½Â®Ã¥Â Â´Ã¥ï¿½Ë†Ã£ï¿½Â¯Ã§â€�Â»Ã©ï¿½Â¢Ã¥â€ â€¦Ã£ï¿½Â®Ã£â€šÂªÃ£Æ’â€“Ã£â€šÂ¸Ã£â€šÂ§Ã£â€šÂ¯Ã£Æ’Ë†Ã£ï¿½Å’Ã¥Â¯Â¾Ã¨Â±Â¡
            if (getSkillRanged() != -1) {
                if (_user.getLocation().getTileLineDistance(_target.getLocation()) > getSkillRanged()) {
                    return; // Ã¥Â°â€žÃ§Â¨â€¹Ã§Â¯â€žÃ¥â€ºÂ²Ã¥Â¤â€“
                }
            }
            else {
                if (!_user.getLocation().isInScreen(_target.getLocation())) {
                    return; // Ã¥Â°â€žÃ§Â¨â€¹Ã§Â¯â€žÃ¥â€ºÂ²Ã¥Â¤â€“
                }
            }

            if ((isTarget(_target) == false) && !(_skill.getTarget().equals("none"))) {
                // Ã¥Â¯Â¾Ã¨Â±Â¡Ã£ï¿½Å’Ã©ï¿½â€¢Ã£ï¿½â€ Ã£ï¿½Â®Ã£ï¿½Â§Ã£â€šÂ¹Ã£â€šÂ­Ã£Æ’Â«Ã£ï¿½Å’Ã§â„¢ÂºÃ¥â€¹â€¢Ã£ï¿½â€”Ã£ï¿½ÂªÃ£ï¿½â€žÃ£â‚¬â€š
                return;
            }

            if ((_skillId == LIGHTNING) || (_skillId == FREEZING_BREATH)) { // Ã£Æ’Â©Ã£â€šÂ¤Ã£Æ’Ë†Ã£Æ’â€¹Ã£Æ’Â³Ã£â€šÂ°Ã£â‚¬ï¿½Ã£Æ’â€¢Ã£Æ’ÂªÃ£Æ’Â¼Ã£â€šÂ¸Ã£Æ’Â³Ã£â€šÂ°Ã£Æ’â€“Ã£Æ’Â¬Ã£â€šÂ¹Ã§â€ºÂ´Ã§Â·Å¡Ã§Å¡â€žÃ£ï¿½Â«Ã§Â¯â€žÃ¥â€ºÂ²Ã£â€šâ€™Ã¦Â±ÂºÃ£â€šï¿½Ã£â€šâ€¹
                List<L1Object> al1object = L1World.getInstance().getVisibleLineObjects(_user, _target);

                for (L1Object tgobj : al1object) {
                    if (tgobj == null) {
                        continue;
                    }
                    if (!(tgobj instanceof L1Character)) { // Ã£â€šÂ¿Ã£Æ’Â¼Ã£â€šÂ²Ã£Æ’Æ’Ã£Æ’Ë†Ã£ï¿½Å’Ã£â€šÂ­Ã£Æ’Â£Ã£Æ’Â©Ã£â€šÂ¯Ã£â€šÂ¿Ã£Æ’Â¼Ã¤Â»Â¥Ã¥Â¤â€“Ã£ï¿½Â®Ã¥Â Â´Ã¥ï¿½Ë†Ã¤Â½â€¢Ã£â€šâ€šÃ£ï¿½â€”Ã£ï¿½ÂªÃ£ï¿½â€žÃ£â‚¬â€š
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

            if (getSkillArea() == 0) { // Ã¥ï¿½ËœÃ¤Â½â€œÃ£ï¿½Â®Ã¥Â Â´Ã¥ï¿½Ë†
                if (!_user.glanceCheck(_target.getX(), _target.getY())) { // Ã§â€ºÂ´Ã§Â·Å¡Ã¤Â¸Å Ã£ï¿½Â«Ã©Å¡Å“Ã¥Â®Â³Ã§â€°Â©Ã£ï¿½Å’Ã£ï¿½â€šÃ£â€šâ€¹Ã£ï¿½â€¹
                    if (((_skill.getType() & L1Skills.TYPE_ATTACK) == L1Skills.TYPE_ATTACK) && (_skillId != 10026) && (_skillId != 10027)
                            && (_skillId != 10028) && (_skillId != 10029)) { // Ã¥Â®â€°Ã¦ï¿½Â¯Ã¦â€�Â»Ã¦â€™Æ’Ã¤Â»Â¥Ã¥Â¤â€“Ã£ï¿½Â®Ã¦â€�Â»Ã¦â€™Æ’Ã£â€šÂ¹Ã£â€šÂ­Ã£Æ’Â«
                        _targetList.add(new TargetStatus(_target, false)); // Ã£Æ’â‚¬Ã£Æ’Â¡Ã£Æ’Â¼Ã£â€šÂ¸Ã£â€šâ€šÃ§â„¢ÂºÃ§â€�Å¸Ã£ï¿½â€”Ã£ï¿½ÂªÃ£ï¿½â€žÃ£ï¿½â€”Ã£â‚¬ï¿½Ã£Æ’â‚¬Ã£Æ’Â¡Ã£Æ’Â¼Ã£â€šÂ¸Ã£Æ’Â¢Ã£Æ’Â¼Ã£â€šÂ·Ã£Æ’Â§Ã£Æ’Â³Ã£â€šâ€šÃ§â„¢ÂºÃ§â€�Å¸Ã£ï¿½â€”Ã£ï¿½ÂªÃ£ï¿½â€žÃ£ï¿½Å’Ã£â‚¬ï¿½Ã£â€šÂ¹Ã£â€šÂ­Ã£Æ’Â«Ã£ï¿½Â¯Ã§â„¢ÂºÃ¥â€¹â€¢
                        return;
                    }
                }
                _targetList.add(new TargetStatus(_target));
            }
            else { // Ã§Â¯â€žÃ¥â€ºÂ²Ã£ï¿½Â®Ã¥Â Â´Ã¥ï¿½Ë†
                if (!_skill.getTarget().equals("none")) {
                    _targetList.add(new TargetStatus(_target));
                }

                if ((_skillId != 49) && !(_skill.getTarget().equals("attack") || (_skill.getType() == L1Skills.TYPE_ATTACK))) {
                    // Ã¦â€�Â»Ã¦â€™Æ’Ã§Â³Â»Ã¤Â»Â¥Ã¥Â¤â€“Ã£ï¿½Â®Ã£â€šÂ¹Ã£â€šÂ­Ã£Æ’Â«Ã£ï¿½Â¨H-AÃ¤Â»Â¥Ã¥Â¤â€“Ã£ï¿½Â¯Ã£â€šÂ¿Ã£Æ’Â¼Ã£â€šÂ²Ã£Æ’Æ’Ã£Æ’Ë†Ã¨â€¡ÂªÃ¨ÂºÂ«Ã£â€šâ€™Ã¥ï¿½Â«Ã£â€šï¿½Ã£â€šâ€¹
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
                    if (!(tgobj instanceof L1Character)) { // Ã£â€šÂ¿Ã£Æ’Â¼Ã£â€šÂ²Ã£Æ’Æ’Ã£Æ’Ë†Ã£ï¿½Å’Ã£â€šÂ­Ã£Æ’Â£Ã£Æ’Â©Ã£â€šÂ¯Ã£â€šÂ¿Ã£Æ’Â¼Ã¤Â»Â¥Ã¥Â¤â€“Ã£ï¿½Â®Ã¥Â Â´Ã¥ï¿½Ë†Ã¤Â½â€¢Ã£â€šâ€šÃ£ï¿½â€”Ã£ï¿½ÂªÃ£ï¿½â€žÃ£â‚¬â€š
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

    // Ã£Æ’Â¡Ã£Æ’Æ’Ã£â€šÂ»Ã£Æ’Â¼Ã£â€šÂ¸Ã£ï¿½Â®Ã¨Â¡Â¨Ã§Â¤ÂºÃ¯Â¼Ë†Ã¤Â½â€¢Ã£ï¿½â€¹Ã¨ÂµÂ·Ã£ï¿½â€œÃ£ï¿½Â£Ã£ï¿½Å¸Ã£ï¿½Â¨Ã£ï¿½ï¿½Ã¯Â¼â€°
    private void sendHappenMessage(L1PcInstance pc) {
        int msgID = _skill.getSysmsgIdHappen();
        if (msgID > 0) {
            // Ã¦â€¢Ë†Ã¦Å¾Å“Ã¨Â¨Å Ã¦ï¿½Â¯Ã¦Å½â€™Ã©â„¢Â¤Ã¦â€“Â½Ã¦Â³â€¢Ã¨â‚¬â€¦Ã¦Å“Â¬Ã¨ÂºÂ«Ã£â‚¬â€š
            if (_skillId == AREA_OF_SILENCE && _user.getId() == pc.getId()) {// Ã¥Â°ï¿½Ã¥ï¿½Â°Ã§Â¦ï¿½Ã¥Å“Â°
                return;
            }
            pc.sendPackets(new S_ServerMessage(msgID));
        }
    }

    // Ã¥Â¤Â±Ã¦â€¢â€”Ã£Æ’Â¡Ã£Æ’Æ’Ã£â€šÂ»Ã£Æ’Â¼Ã£â€šÂ¸Ã¨Â¡Â¨Ã§Â¤ÂºÃ£ï¿½Â®Ã£Æ’ï¿½Ã£Æ’Â³Ã£Æ’â€°Ã£Æ’Â«
    private void sendFailMessageHandle() {
        // Ã¦â€�Â»Ã¦â€™Æ’Ã£â€šÂ¹Ã£â€šÂ­Ã£Æ’Â«Ã¤Â»Â¥Ã¥Â¤â€“Ã£ï¿½Â§Ã¥Â¯Â¾Ã¨Â±Â¡Ã£â€šâ€™Ã¦Å’â€¡Ã¥Â®Å¡Ã£ï¿½â„¢Ã£â€šâ€¹Ã£â€šÂ¹Ã£â€šÂ­Ã£Æ’Â«Ã£ï¿½Å’Ã¥Â¤Â±Ã¦â€¢â€”Ã£ï¿½â€”Ã£ï¿½Å¸Ã¥Â Â´Ã¥ï¿½Ë†Ã£ï¿½Â¯Ã¥Â¤Â±Ã¦â€¢â€”Ã£ï¿½â€”Ã£ï¿½Å¸Ã£Æ’Â¡Ã£Æ’Æ’Ã£â€šÂ»Ã£Æ’Â¼Ã£â€šÂ¸Ã£â€šâ€™Ã£â€šÂ¯Ã£Æ’Â©Ã£â€šÂ¤Ã£â€šÂ¢Ã£Æ’Â³Ã£Æ’Ë†Ã£ï¿½Â«Ã©â‚¬ï¿½Ã¤Â¿Â¡
        // Ã¢â‚¬Â»Ã¦â€�Â»Ã¦â€™Æ’Ã£â€šÂ¹Ã£â€šÂ­Ã£Æ’Â«Ã£ï¿½Â¯Ã©Å¡Å“Ã¥Â®Â³Ã§â€°Â©Ã£ï¿½Å’Ã£ï¿½â€šÃ£ï¿½Â£Ã£ï¿½Â¦Ã£â€šâ€šÃ¦Ë†ï¿½Ã¥Å Å¸Ã¦â„¢â€šÃ£ï¿½Â¨Ã¥ï¿½Å’Ã£ï¿½ËœÃ£â€šÂ¢Ã£â€šÂ¯Ã£â€šÂ·Ã£Æ’Â§Ã£Æ’Â³Ã£ï¿½Â§Ã£ï¿½â€šÃ£â€šâ€¹Ã£ï¿½Â¹Ã£ï¿½ï¿½Ã£â‚¬â€š
        if ((_skill.getType() != L1Skills.TYPE_ATTACK) && !_skill.getTarget().equals("none") && _targetList.isEmpty()) {
            sendFailMessage();
        }
    }

    // Ã£Æ’Â¡Ã£Æ’Æ’Ã£â€šÂ»Ã£Æ’Â¼Ã£â€šÂ¸Ã£ï¿½Â®Ã¨Â¡Â¨Ã§Â¤ÂºÃ¯Â¼Ë†Ã¥Â¤Â±Ã¦â€¢â€”Ã£ï¿½â€”Ã£ï¿½Å¸Ã£ï¿½Â¨Ã£ï¿½ï¿½Ã¯Â¼â€°
    private void sendFailMessage() {
        int msgID = _skill.getSysmsgIdFail();
        if ((msgID > 0) && (_user instanceof L1PcInstance)) {
            _player.sendPackets(new S_ServerMessage(msgID));
        }
    }

    // Ã§Â²Â¾Ã©Å“Å Ã©Â­â€�Ã¦Â³â€¢Ã£ï¿½Â®Ã¥Â±Å¾Ã¦â‚¬Â§Ã£ï¿½Â¨Ã¤Â½Â¿Ã§â€�Â¨Ã¨â‚¬â€¦Ã£ï¿½Â®Ã¥Â±Å¾Ã¦â‚¬Â§Ã£ï¿½Â¯Ã¤Â¸â‚¬Ã¨â€¡Â´Ã£ï¿½â„¢Ã£â€šâ€¹Ã£ï¿½â€¹Ã¯Â¼Å¸Ã¯Â¼Ë†Ã£ï¿½Â¨Ã£â€šÅ Ã£ï¿½â€šÃ£ï¿½Ë†Ã£ï¿½Å¡Ã£ï¿½Â®Ã¥Â¯Â¾Ã¥â€¡Â¦Ã£ï¿½ÂªÃ£ï¿½Â®Ã£ï¿½Â§Ã£â‚¬ï¿½Ã¥Â¯Â¾Ã¥Â¿Å“Ã£ï¿½Â§Ã£ï¿½ï¿½Ã£ï¿½Å¸Ã£â€šâ€°Ã¦Â¶Ë†Ã¥Å½Â»Ã£ï¿½â€”Ã£ï¿½Â¦Ã¤Â¸â€¹Ã£ï¿½â€¢Ã£ï¿½â€ž)
    private boolean isAttrAgrees() {
        int magicattr = _skill.getAttr();
        if (_user instanceof L1NpcInstance) { // NPCÃ£ï¿½Å’Ã¤Â½Â¿Ã£ï¿½Â£Ã£ï¿½Å¸Ã¥Â Â´Ã¥ï¿½Ë†Ã£ï¿½ÂªÃ£â€šâ€œÃ£ï¿½Â§Ã£â€šâ€šOK
            return true;
        }

        if ((_skill.getSkillLevel() >= 17) && (_skill.getSkillLevel() <= 22) && (magicattr != 0 // Ã§Â²Â¾Ã©Å“Å Ã©Â­â€�Ã¦Â³â€¢Ã£ï¿½Â§Ã£â‚¬ï¿½Ã§â€žÂ¡Ã¥Â±Å¾Ã¦â‚¬Â§Ã©Â­â€�Ã¦Â³â€¢Ã£ï¿½Â§Ã£ï¿½Â¯Ã£ï¿½ÂªÃ£ï¿½ï¿½Ã£â‚¬ï¿½
        ) && (magicattr != _player.getElfAttr() // Ã¤Â½Â¿Ã§â€�Â¨Ã¨â‚¬â€¦Ã£ï¿½Â¨Ã©Â­â€�Ã¦Â³â€¢Ã£ï¿½Â®Ã¥Â±Å¾Ã¦â‚¬Â§Ã£ï¿½Å’Ã¤Â¸â‚¬Ã¨â€¡Â´Ã£ï¿½â€”Ã£ï¿½ÂªÃ£ï¿½â€žÃ£â‚¬â€š
        ) && !_player.isGm()) { // Ã£ï¿½Å¸Ã£ï¿½Â Ã£ï¿½â€”GMÃ£ï¿½Â¯Ã¤Â¾â€¹Ã¥Â¤â€“
            return false;
        }
        return true;
    }

    // Ã¥Â¿â€¦Ã¨Â¦ï¿½Ã¯Â¼Â¨Ã¯Â¼Â°Ã£â‚¬ï¿½Ã¯Â¼Â­Ã¯Â¼Â°Ã£ï¿½Å’Ã£ï¿½â€šÃ£â€šâ€¹Ã£ï¿½â€¹Ã¯Â¼Å¸
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

            // MPÃ£ï¿½Â®INTÃ¨Â»Â½Ã¦Â¸â€º
            if ((_player.getInt() > 12) && (_skillId > HOLY_WEAPON) && (_skillId <= FREEZING_BLIZZARD)) { // LV2Ã¤Â»Â¥Ã¤Â¸Å 
                _mpConsume--;
            }
            if ((_player.getInt() > 13) && (_skillId > STALAC) && (_skillId <= FREEZING_BLIZZARD)) { // LV3Ã¤Â»Â¥Ã¤Â¸Å 
                _mpConsume--;
            }
            if ((_player.getInt() > 14) && (_skillId > WEAK_ELEMENTAL) && (_skillId <= FREEZING_BLIZZARD)) { // LV4Ã¤Â»Â¥Ã¤Â¸Å 
                _mpConsume--;
            }
            if ((_player.getInt() > 15) && (_skillId > MEDITATION) && (_skillId <= FREEZING_BLIZZARD)) { // LV5Ã¤Â»Â¥Ã¤Â¸Å 
                _mpConsume--;
            }
            if ((_player.getInt() > 16) && (_skillId > DARKNESS) && (_skillId <= FREEZING_BLIZZARD)) { // LV6Ã¤Â»Â¥Ã¤Â¸Å 
                _mpConsume--;
            }
            if ((_player.getInt() > 17) && (_skillId > BLESS_WEAPON) && (_skillId <= FREEZING_BLIZZARD)) { // LV7Ã¤Â»Â¥Ã¤Â¸Å 
                _mpConsume--;
            }
            if ((_player.getInt() > 18) && (_skillId > DISEASE) && (_skillId <= FREEZING_BLIZZARD)) { // LV8Ã¤Â»Â¥Ã¤Â¸Å 
                _mpConsume--;
            }

            // Ã©Â¨Å½Ã¥Â£Â«Ã¦â„¢ÂºÃ¥Å â€ºÃ¦Â¸â€ºÃ¥â€¦ï¿½
            if ((_player.getInt() > 12) && (_skillId >= SHOCK_STUN) && (_skillId <= COUNTER_BARRIER)) {
                if ( _player.getInt() <= 17 )
                    _mpConsume -= (_player.getInt() - 12);
                else {
                    _mpConsume -= 5 ; // int > 18
                    if ( _mpConsume > 1 ) { // Ã¦Â³â€¢Ã¨Â¡â€œÃ©â€šâ€žÃ¥ï¿½Â¯Ã¤Â»Â¥Ã¦Â¸â€ºÃ¥â€¦ï¿½
                        byte extraInt = (byte) (_player.getInt() - 17) ;
                        // Ã¦Â¸â€ºÃ¥â€¦ï¿½Ã¥â€¦Â¬Ã¥Â¼ï¿½
                        for ( int first= 1 ,range = 2 ; first <= extraInt; first += range, range ++  )
                            _mpConsume -- ;
                    }
                }

            }

            // Ã¨Â£ï¿½Ã¥â€šâ„¢MPÃ¦Â¸â€ºÃ¥â€¦ï¿½ Ã¤Â¸â‚¬Ã¦Â¬Â¡Ã¥ï¿½ÂªÃ©Å“â‚¬Ã¥Ë†Â¤Ã¦â€“Â·Ã¤Â¸â‚¬Ã¥â‚¬â€¹
            if ((_skillId == PHYSICAL_ENCHANT_DEX) && _player.getInventory().checkEquipped(20013)) { // Ã¦â€¢ï¿½Ã¦ï¿½Â·Ã©Â­â€�Ã¦Â³â€¢Ã©Â Â­Ã§â€ºâ€�Ã¤Â½Â¿Ã§â€�Â¨Ã©â‚¬Å¡Ã¦Å¡Â¢Ã¦Â°Â£Ã¨â€žË†Ã¨Â¡â€œ
                _mpConsume /= 2;
            }
            else if ((_skillId == HASTE) && _player.getInventory().checkEquipped(20013)) { // Ã¦â€¢ï¿½Ã¦ï¿½Â·Ã©Â­â€�Ã¦Â³â€¢Ã©Â Â­Ã§â€ºâ€�Ã¤Â½Â¿Ã§â€�Â¨Ã¥Å Â Ã©â‚¬Å¸Ã¨Â¡â€œ
                _mpConsume /= 2;
            }
            else if ((_skillId == HEAL) && _player.getInventory().checkEquipped(20014)) { // Ã¦Â²Â»Ã§â„¢â€™Ã©Â­â€�Ã¦Â³â€¢Ã©Â Â­Ã§â€ºâ€�Ã¤Â½Â¿Ã§â€�Â¨Ã¥Ë†ï¿½Ã§Â´Å¡Ã¦Â²Â»Ã§â„¢â€™Ã¨Â¡â€œ
                _mpConsume /= 2;
            }
            else if ((_skillId == EXTRA_HEAL) && _player.getInventory().checkEquipped(20014)) { // Ã¦Â²Â»Ã§â„¢â€™Ã©Â­â€�Ã¦Â³â€¢Ã©Â Â­Ã§â€ºâ€�Ã¤Â½Â¿Ã§â€�Â¨Ã¤Â¸Â­Ã§Â´Å¡Ã¦Â²Â»Ã§â„¢â€™Ã¨Â¡â€œ
                _mpConsume /= 2;
            }
            else if ((_skillId == ENCHANT_WEAPON) && _player.getInventory().checkEquipped(20015)) { // Ã¥Å â€ºÃ©â€¡ï¿½Ã©Â­â€�Ã¦Â³â€¢Ã©Â Â­Ã§â€ºâ€�Ã¤Â½Â¿Ã§â€�Â¨Ã¦â€œÂ¬Ã¤Â¼Â¼Ã©Â­â€�Ã¦Â³â€¢Ã¦Â­Â¦Ã¥â„¢Â¨
                _mpConsume /= 2;
            }
            else if ((_skillId == DETECTION) && _player.getInventory().checkEquipped(20015)) { // Ã¥Å â€ºÃ©â€¡ï¿½Ã©Â­â€�Ã¦Â³â€¢Ã©Â Â­Ã§â€ºâ€�Ã¤Â½Â¿Ã§â€�Â¨Ã§â€žÂ¡Ã¦â€°â‚¬Ã©ï¿½ï¿½Ã¥Â½Â¢Ã¨Â¡â€œ
                _mpConsume /= 2;
            }
            else if ((_skillId == PHYSICAL_ENCHANT_STR) && _player.getInventory().checkEquipped(20015)) { // Ã¥Å â€ºÃ©â€¡ï¿½Ã©Â­â€�Ã¦Â³â€¢Ã©Â Â­Ã§â€ºâ€�Ã¤Â½Â¿Ã§â€�Â¨Ã©Â«â€�Ã©Â­â€žÃ¥Â¼Â·Ã¥ï¿½Â¥Ã¨Â¡â€œ
                _mpConsume /= 2;
            }
            else if ((_skillId == HASTE) && _player.getInventory().checkEquipped(20008)) { // Ã¥Â°ï¿½Ã¥Å¾â€¹Ã©Â¢Â¨Ã¤Â¹â€¹Ã©Â Â­Ã§â€ºâ€�Ã¤Â½Â¿Ã§â€�Â¨Ã¥Å Â Ã©â‚¬Å¸Ã¨Â¡â€œ
                _mpConsume /= 2;
            }
            else if ((_skillId == HASTE) && _player.getInventory().checkEquipped(20023)) { // Ã©Â¢Â¨Ã¤Â¹â€¹Ã©Â Â­Ã§â€ºâ€�Ã¤Â½Â¿Ã§â€�Â¨Ã¥Å Â Ã©â‚¬Å¸Ã¨Â¡â€œ
                _mpConsume = 25;
            }
            else if ((_skillId == GREATER_HASTE) && _player.getInventory().checkEquipped(20023)) { // Ã©Â¢Â¨Ã¤Â¹â€¹Ã©Â Â­Ã§â€ºâ€�Ã¤Â½Â¿Ã§â€�Â¨Ã¥Â¼Â·Ã¥Å â€ºÃ¥Å Â Ã©â‚¬Å¸Ã¨Â¡â€œ
                _mpConsume /= 2;
            }

            // Ã¥Ë†ï¿½Ã¥Â§â€¹Ã¨Æ’Â½Ã¥Å â€ºÃ¦Â¸â€ºÃ¥â€¦ï¿½
            if (_player.getOriginalMagicConsumeReduction() > 0) {
                _mpConsume -= _player.getOriginalMagicConsumeReduction();
            }

            if (0 < _skill.getMpConsume()) {
                _mpConsume = Math.max(_mpConsume, 1); // Ã¦Å“â‚¬Ã¥Â°ï¿½Ã¥â‚¬Â¼ 1
            }
        }

        if (currentHp < _hpConsume + 1) {
            if (_user instanceof L1PcInstance) {
                _player.sendPackets(new S_ServerMessage(279)); // \f1Ã¥â€ºÂ Ã©Â«â€�Ã¥Å â€ºÃ¤Â¸ï¿½Ã¨Â¶Â³Ã¨â‚¬Å’Ã§â€žÂ¡Ã¦Â³â€¢Ã¤Â½Â¿Ã§â€�Â¨Ã©Â­â€�Ã¦Â³â€¢Ã£â‚¬â€š
            }
            return false;
        }
        else if (currentMp < _mpConsume) {
            if (_user instanceof L1PcInstance) {
                _player.sendPackets(new S_ServerMessage(278)); // \f1Ã¥â€ºÂ Ã©Â­â€�Ã¥Å â€ºÃ¤Â¸ï¿½Ã¨Â¶Â³Ã¨â‚¬Å’Ã§â€žÂ¡Ã¦Â³â€¢Ã¤Â½Â¿Ã§â€�Â¨Ã©Â­â€�Ã¦Â³â€¢Ã£â‚¬â€š
            }
            return false;
        }

        return true;
    }

    // Ã¥Â¿â€¦Ã¨Â¦ï¿½Ã¦ï¿½ï¿½Ã¦â€“â„¢Ã£ï¿½Å’Ã£ï¿½â€šÃ£â€šâ€¹Ã£ï¿½â€¹Ã¯Â¼Å¸
    private boolean isItemConsume() {

        int itemConsume = _skill.getItemConsumeId();
        int itemConsumeCount = _skill.getItemConsumeCount();

        if (itemConsume == 0) {
            return true; // Ã¦ï¿½ï¿½Ã¦â€“â„¢Ã£â€šâ€™Ã¥Â¿â€¦Ã¨Â¦ï¿½Ã£ï¿½Â¨Ã£ï¿½â€”Ã£ï¿½ÂªÃ£ï¿½â€žÃ©Â­â€�Ã¦Â³â€¢
        }

        if (!_player.getInventory().checkItem(itemConsume, itemConsumeCount)) {
            return false; // Ã¥Â¿â€¦Ã¨Â¦ï¿½Ã¦ï¿½ï¿½Ã¦â€“â„¢Ã£ï¿½Å’Ã¨Â¶Â³Ã£â€šÅ Ã£ï¿½ÂªÃ£ï¿½â€¹Ã£ï¿½Â£Ã£ï¿½Å¸Ã£â‚¬â€š
        }

        return true;
    }

    // Ã¤Â½Â¿Ã§â€�Â¨Ã¦ï¿½ï¿½Ã¦â€“â„¢Ã£â‚¬ï¿½HPÃ£Æ’Â»MPÃ£â‚¬ï¿½LawfulÃ£â€šâ€™Ã£Æ’Å¾Ã£â€šÂ¤Ã£Æ’Å Ã£â€šÂ¹Ã£ï¿½â„¢Ã£â€šâ€¹Ã£â‚¬â€š
    private void useConsume() {
        if (_user instanceof L1NpcInstance) {
            // NPCÃ£ï¿½Â®Ã¥Â Â´Ã¥ï¿½Ë†Ã£â‚¬ï¿½HPÃ£â‚¬ï¿½MPÃ£ï¿½Â®Ã£ï¿½Â¿Ã£Æ’Å¾Ã£â€šÂ¤Ã£Æ’Å Ã£â€šÂ¹
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



        // LawfulÃ£â€šâ€™Ã£Æ’Å¾Ã£â€šÂ¤Ã£Æ’Å Ã£â€šÂ¹
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
            return; // Ã¦ï¿½ï¿½Ã¦â€“â„¢Ã£â€šâ€™Ã¥Â¿â€¦Ã¨Â¦ï¿½Ã£ï¿½Â¨Ã£ï¿½â€”Ã£ï¿½ÂªÃ£ï¿½â€žÃ©Â­â€�Ã¦Â³â€¢
        }

        // Ã¤Â½Â¿Ã§â€�Â¨Ã¦ï¿½ï¿½Ã¦â€“â„¢Ã£â€šâ€™Ã£Æ’Å¾Ã£â€šÂ¤Ã£Æ’Å Ã£â€šÂ¹
        _player.getInventory().consumeItem(itemConsume, itemConsumeCount);
    }

    // Ã£Æ’Å¾Ã£â€šÂ¸Ã£Æ’Æ’Ã£â€šÂ¯Ã£Æ’ÂªÃ£â€šÂ¹Ã£Æ’Ë†Ã£ï¿½Â«Ã¨Â¿Â½Ã¥Å Â Ã£ï¿½â„¢Ã£â€šâ€¹Ã£â‚¬â€š
    private void addMagicList(L1Character cha, boolean repetition) {
        if (_skillTime == 0) {
            _getBuffDuration = _skill.getBuffDuration() * 1000; // Ã¥Å Â¹Ã¦Å¾Å“Ã¦â„¢â€šÃ©â€“â€œ
            if (_skill.getBuffDuration() == 0) {
                if (_skillId == INVISIBILITY) { // Ã£â€šÂ¤Ã£Æ’Â³Ã£Æ’â€œÃ£â€šÂ¸Ã£Æ’â€œÃ£Æ’ÂªÃ£Æ’â€ Ã£â€šÂ£
                    cha.setSkillEffect(INVISIBILITY, 0);
                }
                return;
            }
        }
        else {
            _getBuffDuration = _skillTime * 1000; // Ã£Æ’â€˜Ã£Æ’Â©Ã£Æ’Â¡Ã£Æ’Â¼Ã£â€šÂ¿Ã£ï¿½Â®timeÃ£ï¿½Å’0Ã¤Â»Â¥Ã¥Â¤â€“Ã£ï¿½ÂªÃ£â€šâ€°Ã£â‚¬ï¿½Ã¥Å Â¹Ã¦Å¾Å“Ã¦â„¢â€šÃ©â€“â€œÃ£ï¿½Â¨Ã£ï¿½â€”Ã£ï¿½Â¦Ã¨Â¨Â­Ã¥Â®Å¡Ã£ï¿½â„¢Ã£â€šâ€¹
        }

        if (_skillId == SHOCK_STUN) {
            _getBuffDuration = _shockStunDuration;
        }

        if (_skillId == BONE_BREAK) {
            _getBuffDuration = _boneBreakDuration;
        }

        if (_skillId == CURSE_POISON) {  // Ã¦Â¯â€™Ã¥â€™â€™Ã¦Å’ï¿½Ã§ÂºÅ’Ã¦â„¢â€šÃ©â€“â€œÃ§Â§Â»Ã¨â€¡Â³ L1Poison Ã¨â„¢â€¢Ã§ï¿½â€ Ã£â‚¬â€š
            return;
        }
        if ((_skillId == CURSE_PARALYZE) || (_skillId == CURSE_PARALYZE2)) { // Ã¦Å“Â¨Ã¤Â¹Æ’Ã¤Â¼Å Ã§Å¡â€žÃ¥â€™â‚¬Ã¥â€™â€™Ã£â‚¬ï¿½Ã§Å¸Â³Ã¥Å’â€“Ã¦Å’ï¿½Ã§ÂºÅ’Ã¦â„¢â€šÃ©â€“â€œÃ§Â§Â»Ã¨â€¡Â³ L1CurseParalysis Ã¨â„¢â€¢Ã§ï¿½â€ Ã£â‚¬â€š
            return;
        }
        if (_skillId == SHAPE_CHANGE) { // Ã¨Â®Å Ã¥Â½Â¢Ã¨Â¡â€œÃ¦Å’ï¿½Ã§ÂºÅ’Ã¦â„¢â€šÃ©â€“â€œÃ§Â§Â»Ã¨â€¡Â³ L1PolyMorph Ã¨â„¢â€¢Ã§ï¿½â€ Ã£â‚¬â€š
            return;
        }
        if ((_skillId == BLESSED_ARMOR) || (_skillId == HOLY_WEAPON // Ã¦Â­Â¦Ã¥â„¢Â¨Ã£Æ’Â»Ã©ËœÂ²Ã¥â€¦Â·Ã£ï¿½Â«Ã¥Å Â¹Ã¦Å¾Å“Ã£ï¿½Å’Ã£ï¿½â€šÃ£â€šâ€¹Ã¥â€¡Â¦Ã§ï¿½â€ Ã£ï¿½Â¯L1ItemInstanceÃ£ï¿½Â«Ã§Â§Â»Ã¨Â­Â²Ã£â‚¬â€š
        ) || (_skillId == ENCHANT_WEAPON) || (_skillId == BLESS_WEAPON) || (_skillId == SHADOW_FANG)) {
            return;
        }
        if (((_skillId == ICE_LANCE) || (_skillId == FREEZING_BLIZZARD) || (_skillId == FREEZING_BREATH)
                || (_skillId == ICE_LANCE_COCKATRICE) || (_skillId == ICE_LANCE_BASILISK)) && !_isFreeze) { // Ã¥â€¡ï¿½Ã§Âµï¿½Ã¥Â¤Â±Ã¦â€¢â€”
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
        if ((_skillId == AWAKEN_ANTHARAS) || (_skillId == AWAKEN_FAFURION) || (_skillId == AWAKEN_VALAKAS)) { // Ã¨Â¦Å¡Ã©â€ â€™Ã£ï¿½Â®Ã¥Å Â¹Ã¦Å¾Å“Ã¥â€¡Â¦Ã§ï¿½â€ Ã£ï¿½Â¯L1AwakeÃ£ï¿½Â«Ã§Â§Â»Ã¨Â­Â²Ã£â‚¬â€š
            return;
        }*/
        // Ã©ÂªÂ·Ã©Â«ï¿½Ã¦Â¯â‚¬Ã¥Â£Å¾Ã¦Å’ï¿½Ã§ÂºÅ’Ã¦â„¢â€šÃ©â€“â€œÃ¥ï¿½Â¦Ã¥Â¤â€“Ã¨â„¢â€¢Ã§ï¿½â€  removed BONE_BREAK HERE
        if (_skillId == CONFUSION) {
            return;
        }
        cha.setSkillEffect(_skillId, _getBuffDuration);

        if (_skillId == ELEMENTAL_FALL_DOWN && repetition) { // Ã¥Â¼Â±Ã¥Å’â€“Ã¥Â±Â¬Ã¦â‚¬Â§Ã©â€¡ï¿½Ã¨Â¤â€¡Ã¦â€“Â½Ã¦â€�Â¾
            if (_skillTime == 0) {
                _getBuffIconDuration = _skill.getBuffDuration(); // Ã¥Å Â¹Ã¦Å¾Å“Ã¦â„¢â€šÃ©â€“â€œ
            } else {
                _getBuffIconDuration = _skillTime;
            }
            _target.removeSkillEffect(ELEMENTAL_FALL_DOWN);
            runSkill();
            return;
        }
        if ((cha instanceof L1PcInstance) && repetition) { // Ã¥Â¯Â¾Ã¨Â±Â¡Ã£ï¿½Å’PCÃ£ï¿½Â§Ã¦â€”Â¢Ã£ï¿½Â«Ã£â€šÂ¹Ã£â€šÂ­Ã£Æ’Â«Ã£ï¿½Å’Ã©â€¡ï¿½Ã¨Â¤â€¡Ã£ï¿½â€”Ã£ï¿½Â¦Ã£ï¿½â€žÃ£â€šâ€¹Ã¥Â Â´Ã¥ï¿½Ë†
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
    // Ã£â€šÂ¢Ã£â€šÂ¤Ã£â€šÂ³Ã£Æ’Â³Ã£ï¿½Â®Ã©â‚¬ï¿½Ã¤Â¿Â¡
    private void sendIcon(L1PcInstance pc) {
        if (_skillTime == 0) {
            _getBuffIconDuration = _skill.getBuffDuration(); // Ã¥Å Â¹Ã¦Å¾Å“Ã¦â„¢â€šÃ©â€“â€œ
        }
        else {
            _getBuffIconDuration = _skillTime; // Ã£Æ’â€˜Ã£Æ’Â©Ã£Æ’Â¡Ã£Æ’Â¼Ã£â€šÂ¿Ã£ï¿½Â®timeÃ£ï¿½Å’0Ã¤Â»Â¥Ã¥Â¤â€“Ã£ï¿½ÂªÃ£â€šâ€°Ã£â‚¬ï¿½Ã¥Å Â¹Ã¦Å¾Å“Ã¦â„¢â€šÃ©â€“â€œÃ£ï¿½Â¨Ã£ï¿½â€”Ã£ï¿½Â¦Ã¨Â¨Â­Ã¥Â®Å¡Ã£ï¿½â„¢Ã£â€šâ€¹
        }

        if (_skillId == SHIELD) { // Ã£â€šÂ·Ã£Æ’Â¼Ã£Æ’Â«Ã£Æ’â€°
            pc.sendPackets(new S_SkillIconShield(5, _getBuffIconDuration));
        }
        else if (_skillId == SHADOW_ARMOR) { // Ã£â€šÂ·Ã£Æ’Â£Ã£Æ’â€°Ã£â€šÂ¦ Ã£â€šÂ¢Ã£Æ’Â¼Ã£Æ’Å¾Ã£Æ’Â¼
            pc.sendPackets(new S_SkillIconShield(3, _getBuffIconDuration));
        }
        else if (_skillId == DRESS_DEXTERITY) { // Ã£Æ’â€°Ã£Æ’Â¬Ã£â€šÂ¹ Ã£Æ’â€¡Ã£â€šÂ¯Ã£â€šÂ¹Ã£â€šÂ¿Ã£Æ’ÂªÃ£Æ’â€ Ã£â€šÂ£Ã£Æ’Â¼
            pc.sendPackets(new S_Dexup(pc, 2, _getBuffIconDuration));
        }
        else if (_skillId == DRESS_MIGHTY) { // Ã£Æ’â€°Ã£Æ’Â¬Ã£â€šÂ¹ Ã£Æ’Å¾Ã£â€šÂ¤Ã£Æ’â€ Ã£â€šÂ£Ã£Æ’Â¼
            pc.sendPackets(new S_Strup(pc, 2, _getBuffIconDuration));
        }
        else if (_skillId == GLOWING_AURA) { // Ã£â€šÂ°Ã£Æ’Â­Ã£Æ’Â¼Ã£â€šÂ¦Ã£â€šÂ£Ã£Æ’Â³Ã£â€šÂ° Ã£â€šÂªÃ£Æ’Â¼Ã£Æ’Â©
            pc.sendPackets(new S_SkillIconAura(113, _getBuffIconDuration));
        }
        else if (_skillId == SHINING_AURA) { // Ã£â€šÂ·Ã£Æ’Â£Ã£â€šÂ¤Ã£Æ’â€¹Ã£Æ’Â³Ã£â€šÂ° Ã£â€šÂªÃ£Æ’Â¼Ã£Æ’Â©
            pc.sendPackets(new S_SkillIconAura(114, _getBuffIconDuration));
        }
        else if (_skillId == BRAVE_AURA) { // Ã£Æ’â€“Ã£Æ’Â¬Ã£â€šÂ¤Ã£Æ’â€“ Ã£â€šÂªÃ£Æ’Â¼Ã£Æ’Â©
            pc.sendPackets(new S_SkillIconAura(116, _getBuffIconDuration));
        }
        else if (_skillId == FIRE_WEAPON) { // Ã£Æ’â€¢Ã£â€šÂ¡Ã£â€šÂ¤Ã£â€šÂ¢Ã£Æ’Â¼ Ã£â€šÂ¦Ã£â€šÂ§Ã£Æ’ï¿½Ã£Æ’Â³
            pc.sendPackets(new S_SkillIconAura(147, _getBuffIconDuration));
        }
        else if (_skillId == WIND_SHOT) { // Ã£â€šÂ¦Ã£â€šÂ£Ã£Æ’Â³Ã£Æ’â€° Ã£â€šÂ·Ã£Æ’Â§Ã£Æ’Æ’Ã£Æ’Ë†
            pc.sendPackets(new S_SkillIconAura(148, _getBuffIconDuration));
        }
        else if (_skillId == FIRE_BLESS) { // Ã£Æ’â€¢Ã£â€šÂ¡Ã£â€šÂ¤Ã£â€šÂ¢Ã£Æ’Â¼ Ã£Æ’â€“Ã£Æ’Â¬Ã£â€šÂ¹
            pc.sendPackets(new S_SkillIconAura(154, _getBuffIconDuration));
        }
        else if (_skillId == STORM_EYE) { // Ã£â€šÂ¹Ã£Æ’Ë†Ã£Æ’Â¼Ã£Æ’Â  Ã£â€šÂ¢Ã£â€šÂ¤
            pc.sendPackets(new S_SkillIconAura(155, _getBuffIconDuration));
        }
        else if (_skillId == EARTH_BLESS) { // Ã£â€šÂ¢Ã£Æ’Â¼Ã£â€šÂ¹ Ã£Æ’â€“Ã£Æ’Â¬Ã£â€šÂ¹
            pc.sendPackets(new S_SkillIconShield(7, _getBuffIconDuration));
        }
        else if (_skillId == BURNING_WEAPON) { // Ã£Æ’ï¿½Ã£Æ’Â¼Ã£Æ’â€¹Ã£Æ’Â³Ã£â€šÂ° Ã£â€šÂ¦Ã£â€šÂ§Ã£Æ’ï¿½Ã£Æ’Â³
            pc.sendPackets(new S_SkillIconAura(162, _getBuffIconDuration));
        }
        else if (_skillId == STORM_SHOT) { // Ã£â€šÂ¹Ã£Æ’Ë†Ã£Æ’Â¼Ã£Æ’Â  Ã£â€šÂ·Ã£Æ’Â§Ã£Æ’Æ’Ã£Æ’Ë†
            pc.sendPackets(new S_SkillIconAura(165, _getBuffIconDuration));
        }
        else if (_skillId == IRON_SKIN) { // Ã£â€šÂ¢Ã£â€šÂ¤Ã£â€šÂ¢Ã£Æ’Â³ Ã£â€šÂ¹Ã£â€šÂ­Ã£Æ’Â³
            pc.sendPackets(new S_SkillIconShield(10, _getBuffIconDuration));
        }
        else if (_skillId == EARTH_SKIN) { // Ã£â€šÂ¢Ã£Æ’Â¼Ã£â€šÂ¹ Ã£â€šÂ¹Ã£â€šÂ­Ã£Æ’Â³
            pc.sendPackets(new S_SkillIconShield(6, _getBuffIconDuration));
        }
        else if (_skillId == PHYSICAL_ENCHANT_STR) { // Ã£Æ’â€¢Ã£â€šÂ£Ã£â€šÂ¸Ã£â€šÂ«Ã£Æ’Â« Ã£â€šÂ¨Ã£Æ’Â³Ã£Æ’ï¿½Ã£Æ’Â£Ã£Æ’Â³Ã£Æ’Ë†Ã¯Â¼Å¡STR
            pc.sendPackets(new S_Strup(pc, 5, _getBuffIconDuration));
        }
        else if (_skillId == PHYSICAL_ENCHANT_DEX) { // Ã£Æ’â€¢Ã£â€šÂ£Ã£â€šÂ¸Ã£â€šÂ«Ã£Æ’Â« Ã£â€šÂ¨Ã£Æ’Â³Ã£Æ’ï¿½Ã£Æ’Â£Ã£Æ’Â³Ã£Æ’Ë†Ã¯Â¼Å¡DEX
            pc.sendPackets(new S_Dexup(pc, 5, _getBuffIconDuration));
        }
        else if ((_skillId == HASTE) || (_skillId == GREATER_HASTE)) { // Ã£â€šÂ°Ã£Æ’Â¬Ã£Æ’Â¼Ã£â€šÂ¿Ã£Æ’Â¼Ã£Æ’ËœÃ£â€šÂ¤Ã£â€šÂ¹Ã£Æ’Ë†
            pc.sendPackets(new S_SkillHaste(pc.getId(), 1, _getBuffIconDuration));
            pc.broadcastPacket(new S_SkillHaste(pc.getId(), 1, 0));
        }
        else if ((_skillId == HOLY_WALK) || (_skillId == MOVING_ACCELERATION) || (_skillId == WIND_WALK)) { // Ã£Æ’â€ºÃ£Æ’Â¼Ã£Æ’ÂªÃ£Æ’Â¼Ã£â€šÂ¦Ã£â€šÂ©Ã£Æ’Â¼Ã£â€šÂ¯Ã£â‚¬ï¿½Ã£Æ’Â Ã£Æ’Â¼Ã£Æ’â€œÃ£Æ’Â³Ã£â€šÂ°Ã£â€šÂ¢Ã£â€šÂ¯Ã£â€šÂ»Ã£Æ’Â¬Ã£Æ’Â¼Ã£â€šÂ·Ã£Æ’Â§Ã£Æ’Â³Ã£â‚¬ï¿½Ã£â€šÂ¦Ã£â€šÂ£Ã£Æ’Â³Ã£Æ’â€°Ã£â€šÂ¦Ã£â€šÂ©Ã£Æ’Â¼Ã£â€šÂ¯
            pc.sendPackets(new S_SkillBrave(pc.getId(), 4, _getBuffIconDuration));
            pc.broadcastPacket(new S_SkillBrave(pc.getId(), 4, 0));
        }
        else if (_skillId == BLOODLUST) { // Ã£Æ’â€“Ã£Æ’Â©Ã£Æ’Æ’Ã£Æ’â€°Ã£Æ’Â©Ã£â€šÂ¹Ã£Æ’Ë†
            pc.sendPackets(new S_SkillBrave(pc.getId(), 6, _getBuffIconDuration));
            pc.broadcastPacket(new S_SkillBrave(pc.getId(), 6, 0));
        }
        else if ((_skillId == SLOW) || (_skillId == MASS_SLOW) || (_skillId == ENTANGLE)) { // Ã£â€šÂ¹Ã£Æ’Â­Ã£Æ’Â¼Ã£â‚¬ï¿½Ã£â€šÂ¨Ã£Æ’Â³Ã£â€šÂ¿Ã£Æ’Â³Ã£â€šÂ°Ã£Æ’Â«Ã£â‚¬ï¿½Ã£Æ’Å¾Ã£â€šÂ¹Ã£â€šÂ¹Ã£Æ’Â­Ã£Æ’Â¼
            pc.sendPackets(new S_SkillHaste(pc.getId(), 2, _getBuffIconDuration));
            pc.broadcastPacket(new S_SkillHaste(pc.getId(), 2, 0));
        }
        else if (_skillId == IMMUNE_TO_HARM) {
            pc.sendPackets(new S_SkillIconGFX(40, _getBuffIconDuration));
        }
        else if (_skillId == WIND_SHACKLE) { // Ã©Â¢Â¨Ã¤Â¹â€¹Ã¦Å¾Â·Ã©Å½â€“
            pc.sendPackets(new S_SkillIconWindShackle(pc.getId(), _getBuffIconDuration));
            pc.broadcastPacket(new S_SkillIconWindShackle(pc.getId(), _getBuffIconDuration));
        }
        pc.sendPackets(new S_OwnCharStatus(pc));
    }


    public void sendIcon(L1PcInstance pc, int skillId, int buffIconDuration) {
        if (skillId == SHIELD) { // Ã£â€šÂ·Ã£Æ’Â¼Ã£Æ’Â«Ã£Æ’â€°
            pc.sendPackets(new S_SkillIconShield(5, buffIconDuration));
        }
        else if (skillId == SHADOW_ARMOR) { // Ã£â€šÂ·Ã£Æ’Â£Ã£Æ’â€°Ã£â€šÂ¦ Ã£â€šÂ¢Ã£Æ’Â¼Ã£Æ’Å¾Ã£Æ’Â¼
            pc.sendPackets(new S_SkillIconShield(3, buffIconDuration));
        }
        else if (skillId == DRESS_DEXTERITY) { // Ã£Æ’â€°Ã£Æ’Â¬Ã£â€šÂ¹ Ã£Æ’â€¡Ã£â€šÂ¯Ã£â€šÂ¹Ã£â€šÂ¿Ã£Æ’ÂªÃ£Æ’â€ Ã£â€šÂ£Ã£Æ’Â¼
            pc.sendPackets(new S_Dexup(pc, 2, buffIconDuration));
        }
        else if (skillId == DRESS_MIGHTY) { // Ã£Æ’â€°Ã£Æ’Â¬Ã£â€šÂ¹ Ã£Æ’Å¾Ã£â€šÂ¤Ã£Æ’â€ Ã£â€šÂ£Ã£Æ’Â¼
            pc.sendPackets(new S_Strup(pc, 2, buffIconDuration));
        }
        else if (skillId == GLOWING_AURA) { // Ã£â€šÂ°Ã£Æ’Â­Ã£Æ’Â¼Ã£â€šÂ¦Ã£â€šÂ£Ã£Æ’Â³Ã£â€šÂ° Ã£â€šÂªÃ£Æ’Â¼Ã£Æ’Â©
            pc.sendPackets(new S_SkillIconAura(113, buffIconDuration));
        }
        else if (skillId == SHINING_AURA) { // Ã£â€šÂ·Ã£Æ’Â£Ã£â€šÂ¤Ã£Æ’â€¹Ã£Æ’Â³Ã£â€šÂ° Ã£â€šÂªÃ£Æ’Â¼Ã£Æ’Â©
            pc.sendPackets(new S_SkillIconAura(114, buffIconDuration));
        }
        else if (skillId == BRAVE_AURA) { // Ã£Æ’â€“Ã£Æ’Â¬Ã£â€šÂ¤Ã£Æ’â€“ Ã£â€šÂªÃ£Æ’Â¼Ã£Æ’Â©
            pc.sendPackets(new S_SkillIconAura(116, buffIconDuration));
        }
        else if (skillId == FIRE_WEAPON) { // Ã£Æ’â€¢Ã£â€šÂ¡Ã£â€šÂ¤Ã£â€šÂ¢Ã£Æ’Â¼ Ã£â€šÂ¦Ã£â€šÂ§Ã£Æ’ï¿½Ã£Æ’Â³
            pc.sendPackets(new S_SkillIconAura(147, buffIconDuration));
        }
        else if (skillId == WIND_SHOT) { // Ã£â€šÂ¦Ã£â€šÂ£Ã£Æ’Â³Ã£Æ’â€° Ã£â€šÂ·Ã£Æ’Â§Ã£Æ’Æ’Ã£Æ’Ë†
            pc.sendPackets(new S_SkillIconAura(148, buffIconDuration));
        }
        else if (skillId == FIRE_BLESS) { // Ã£Æ’â€¢Ã£â€šÂ¡Ã£â€šÂ¤Ã£â€šÂ¢Ã£Æ’Â¼ Ã£Æ’â€“Ã£Æ’Â¬Ã£â€šÂ¹
            pc.sendPackets(new S_SkillIconAura(154, buffIconDuration));
        }
        else if (skillId == STORM_EYE) { // Ã£â€šÂ¹Ã£Æ’Ë†Ã£Æ’Â¼Ã£Æ’Â  Ã£â€šÂ¢Ã£â€šÂ¤
            pc.sendPackets(new S_SkillIconAura(155, buffIconDuration));
        }
        else if (skillId == EARTH_BLESS) { // Ã£â€šÂ¢Ã£Æ’Â¼Ã£â€šÂ¹ Ã£Æ’â€“Ã£Æ’Â¬Ã£â€šÂ¹
            pc.sendPackets(new S_SkillIconShield(7, buffIconDuration));
        }
        else if (skillId == BURNING_WEAPON) { // Ã£Æ’ï¿½Ã£Æ’Â¼Ã£Æ’â€¹Ã£Æ’Â³Ã£â€šÂ° Ã£â€šÂ¦Ã£â€šÂ§Ã£Æ’ï¿½Ã£Æ’Â³
            pc.sendPackets(new S_SkillIconAura(162, buffIconDuration));
        }
        else if (skillId == STORM_SHOT) { // Ã£â€šÂ¹Ã£Æ’Ë†Ã£Æ’Â¼Ã£Æ’Â  Ã£â€šÂ·Ã£Æ’Â§Ã£Æ’Æ’Ã£Æ’Ë†
            pc.sendPackets(new S_SkillIconAura(165, buffIconDuration));
        }
        else if (skillId == IRON_SKIN) { // Ã£â€šÂ¢Ã£â€šÂ¤Ã£â€šÂ¢Ã£Æ’Â³ Ã£â€šÂ¹Ã£â€šÂ­Ã£Æ’Â³
            pc.sendPackets(new S_SkillIconShield(10, buffIconDuration));
        }
        else if (skillId == EARTH_SKIN) { // Ã£â€šÂ¢Ã£Æ’Â¼Ã£â€šÂ¹ Ã£â€šÂ¹Ã£â€šÂ­Ã£Æ’Â³
            pc.sendPackets(new S_SkillIconShield(6, buffIconDuration));
        }
        else if (skillId == PHYSICAL_ENCHANT_STR) { // Ã£Æ’â€¢Ã£â€šÂ£Ã£â€šÂ¸Ã£â€šÂ«Ã£Æ’Â« Ã£â€šÂ¨Ã£Æ’Â³Ã£Æ’ï¿½Ã£Æ’Â£Ã£Æ’Â³Ã£Æ’Ë†Ã¯Â¼Å¡STR
            pc.sendPackets(new S_Strup(pc, 5, buffIconDuration));
        }
        else if (skillId == PHYSICAL_ENCHANT_DEX) { // Ã£Æ’â€¢Ã£â€šÂ£Ã£â€šÂ¸Ã£â€šÂ«Ã£Æ’Â« Ã£â€šÂ¨Ã£Æ’Â³Ã£Æ’ï¿½Ã£Æ’Â£Ã£Æ’Â³Ã£Æ’Ë†Ã¯Â¼Å¡DEX
            pc.sendPackets(new S_Dexup(pc, 5, buffIconDuration));
        }
        else if ((skillId == HASTE) || (skillId == GREATER_HASTE)) { // Ã£â€šÂ°Ã£Æ’Â¬Ã£Æ’Â¼Ã£â€šÂ¿Ã£Æ’Â¼Ã£Æ’ËœÃ£â€šÂ¤Ã£â€šÂ¹Ã£Æ’Ë†
            pc.sendPackets(new S_SkillHaste(pc.getId(), 1, buffIconDuration));
            pc.broadcastPacket(new S_SkillHaste(pc.getId(), 1, 0));
        }
        else if ((skillId == HOLY_WALK) || (skillId == MOVING_ACCELERATION) || (skillId == WIND_WALK)) { // Ã£Æ’â€ºÃ£Æ’Â¼Ã£Æ’ÂªÃ£Æ’Â¼Ã£â€šÂ¦Ã£â€šÂ©Ã£Æ’Â¼Ã£â€šÂ¯Ã£â‚¬ï¿½Ã£Æ’Â Ã£Æ’Â¼Ã£Æ’â€œÃ£Æ’Â³Ã£â€šÂ°Ã£â€šÂ¢Ã£â€šÂ¯Ã£â€šÂ»Ã£Æ’Â¬Ã£Æ’Â¼Ã£â€šÂ·Ã£Æ’Â§Ã£Æ’Â³Ã£â‚¬ï¿½Ã£â€šÂ¦Ã£â€šÂ£Ã£Æ’Â³Ã£Æ’â€°Ã£â€šÂ¦Ã£â€šÂ©Ã£Æ’Â¼Ã£â€šÂ¯
            pc.sendPackets(new S_SkillBrave(pc.getId(), 4, buffIconDuration));
            pc.broadcastPacket(new S_SkillBrave(pc.getId(), 4, 0));
        }
        else if (skillId == BLOODLUST) {
            pc.sendPackets(new S_SkillBrave(pc.getId(), 6, buffIconDuration));
            pc.broadcastPacket(new S_SkillBrave(pc.getId(), 6, 0));
        }
        else if ((skillId == SLOW) || (skillId == MASS_SLOW) || (skillId == ENTANGLE)) { // Ã£â€šÂ¹Ã£Æ’Â­Ã£Æ’Â¼Ã£â‚¬ï¿½Ã£â€šÂ¨Ã£Æ’Â³Ã£â€šÂ¿Ã£Æ’Â³Ã£â€šÂ°Ã£Æ’Â«Ã£â‚¬ï¿½Ã£Æ’Å¾Ã£â€šÂ¹Ã£â€šÂ¹Ã£Æ’Â­Ã£Æ’Â¼
            pc.sendPackets(new S_SkillHaste(pc.getId(), 2, buffIconDuration));
            pc.broadcastPacket(new S_SkillHaste(pc.getId(), 2, 0));
        }
        else if (skillId == IMMUNE_TO_HARM) {
            pc.sendPackets(new S_SkillIconGFX(40, buffIconDuration));
        }
        else if (skillId == WIND_SHACKLE) { // Ã©Â¢Â¨Ã¤Â¹â€¹Ã¦Å¾Â·Ã©Å½â€“
            pc.sendPackets(new S_SkillIconWindShackle(pc.getId(), buffIconDuration));
            pc.broadcastPacket(new S_SkillIconWindShackle(pc.getId(), buffIconDuration));
        }
        else if (skillId == BURNING_SPIRIT)
        {
            pc.sendPackets(new S_SkillIconAura(162, buffIconDuration));
        }
        pc.sendPackets(new S_OwnCharStatus(pc));
    }
    // Ã£â€šÂ°Ã£Æ’Â©Ã£Æ’â€¢Ã£â€šÂ£Ã£Æ’Æ’Ã£â€šÂ¯Ã£ï¿½Â®Ã©â‚¬ï¿½Ã¤Â¿Â¡
    private void sendGrfx(boolean isSkillAction) {
        if (_actid == 0) {
            _actid = _skill.getActionId();
        }
        if (_gfxid == 0) {
            _gfxid = _skill.getCastGfx();
        }
        if (_gfxid == 0) {
            return; // Ã¨Â¡Â¨Ã§Â¤ÂºÃ£ï¿½â„¢Ã£â€šâ€¹Ã£â€šÂ°Ã£Æ’Â©Ã£Æ’â€¢Ã£â€šÂ£Ã£Æ’Æ’Ã£â€šÂ¯Ã£ï¿½Å’Ã§â€žÂ¡Ã£ï¿½â€ž
        }
        int[] data = null;

        if (_user instanceof L1PcInstance) {

            int targetid = 0;
            if (_skillId != FIRE_WALL) {
                targetid = _target.getId();
            }
            L1PcInstance pc = (L1PcInstance) _user;

            switch(_skillId) {
                case FIRE_WALL: // Ã§ï¿½Â«Ã§â€°Â¢
                case LIFE_STREAM: // Ã¦Â²Â»Ã§â„¢â€™Ã¨Æ’Â½Ã©â€¡ï¿½Ã©Â¢Â¨Ã¦Å¡Â´
                case ELEMENTAL_FALL_DOWN: // Ã¥Â¼Â±Ã¥Å’â€“Ã¥Â±Â¬Ã¦â‚¬Â§
                    if (_skillId == FIRE_WALL) {
                        pc.setHeading(pc.targetDirection(_targetX, _targetY));
                        pc.sendPackets(new S_ChangeHeading(pc));
                        pc.broadcastPacket(new S_ChangeHeading(pc));
                    }
                    S_DoActionGFX gfx = new S_DoActionGFX(pc.getId(), _actid);
                    pc.sendPackets(gfx);
                    pc.broadcastPacket(gfx);
                    return;
                case SHOCK_STUN: // Ã¨Â¡ï¿½Ã¦â€œÅ Ã¤Â¹â€¹Ã¦Å¡Ë†
                    if (_targetList.isEmpty()) { // Ã¥Â¤Â±Ã¦â€¢â€”
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
                case LIGHT: // Ã¦â€”Â¥Ã¥â€¦â€°Ã¨Â¡â€œ
                    pc.sendPackets(new S_Sound(145));
                    break;
                case MIND_BREAK: // Ã¥Â¿Æ’Ã©ï¿½Ë†Ã§Â Â´Ã¥Â£Å¾
                case JOY_OF_PAIN: // Ã§â€“Â¼Ã§â€”â€ºÃ§Å¡â€žÃ¦Â­Â¡Ã¦â€žâ€°
                    data = new int[] {_actid, _dmg, 0}; // data = {actid, dmg, effect}
                    pc.sendPackets(new S_AttackPacket(pc, targetid, data));
                    pc.broadcastPacket(new S_AttackPacket(pc, targetid, data));
                    pc.sendPackets(new S_SkillSound(targetid, _gfxid));
                    pc.broadcastPacket(new S_SkillSound(targetid, _gfxid));
                    return;
                case CONFUSION: // Ã¦Â·Â·Ã¤Âºâ€š
                    data = new int[] {_actid, _dmg, 0}; // data = {actid, dmg, effect}
                    pc.sendPackets(new S_AttackPacket(pc, targetid, data));
                    pc.broadcastPacket(new S_AttackPacket(pc, targetid, data));
                    return;
                case SMASH: // Ã¦Å¡Â´Ã¦â€œÅ 
                    pc.sendPackets(new S_SkillSound(targetid, _gfxid));
                    pc.broadcastPacket(new S_SkillSound(targetid, _gfxid));
                    return;
                case TAMING_MONSTER: // Ã¨Â¿Â·Ã©Â­â€¦
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
                // Ã£â€šÂ¿Ã£Æ’Â¼Ã£â€šÂ²Ã£Æ’Æ’Ã£Æ’Ë†Ã¦â€¢Â°Ã£ï¿½Å’Ã¯Â¼ï¿½Ã£ï¿½Â§Ã¥Â¯Â¾Ã¨Â±Â¡Ã£â€šâ€™Ã¦Å’â€¡Ã¥Â®Å¡Ã£ï¿½â„¢Ã£â€šâ€¹Ã£â€šÂ¹Ã£â€šÂ­Ã£Æ’Â«Ã£ï¿½Â®Ã¥Â Â´Ã¥ï¿½Ë†Ã£â‚¬ï¿½Ã©Â­â€�Ã¦Â³â€¢Ã¤Â½Â¿Ã§â€�Â¨Ã£â€šÂ¨Ã£Æ’â€¢Ã£â€šÂ§Ã£â€šÂ¯Ã£Æ’Ë†Ã£ï¿½Â Ã£ï¿½â€˜Ã¨Â¡Â¨Ã§Â¤ÂºÃ£ï¿½â€”Ã£ï¿½Â¦Ã§Âµâ€šÃ¤Âºâ€ 
                int tempchargfx = _player.getTempCharGfx();
                if ((tempchargfx == 5727) || (tempchargfx == 5730)) { // Ã£â€šÂ·Ã£Æ’Â£Ã£Æ’â€°Ã£â€šÂ¦Ã§Â³Â»Ã¥Â¤â€°Ã¨ÂºÂ«Ã£ï¿½Â®Ã£Æ’Â¢Ã£Æ’Â¼Ã£â€šÂ·Ã£Æ’Â§Ã£Æ’Â³Ã¥Â¯Â¾Ã¥Â¿Å“
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
                if (isPcSummonPet(_target)) { // Ã§â€ºÂ®Ã¦Â¨â„¢Ã§Å½Â©Ã¥Â®Â¶Ã£â‚¬ï¿½Ã¥Â¯ÂµÃ§â€°Â©Ã£â‚¬ï¿½Ã¥ï¿½Â¬Ã¥â€“Å¡Ã§ï¿½Â¸
                    if ((_player.getZoneType() == 1) || (_target.getZoneType() == 1)
                            || _player.checkNonPvP(_player, _target)) { // Non-PvPÃ¨Â¨Â­Ã¥Â®Å¡
                        data = new int[] {_actid, 0, _gfxid, 6};
                        _player.sendPackets(new S_UseAttackSkill(_player, _target.getId(), _targetX, _targetY, data));
                        _player.broadcastPacket(new S_UseAttackSkill(_player, _target.getId(), _targetX, _targetY, data));
                        return;
                    }
                }

                if (getSkillArea() == 0) { // Ã¥â€“Â®Ã©Â«â€�Ã¦â€�Â»Ã¦â€œÅ Ã©Â­â€�Ã¦Â³â€¢
                    data = new int[] {_actid, _dmg, _gfxid, 6};
                    _player.sendPackets(new S_UseAttackSkill(_player, targetid, _targetX, _targetY, data));
                    _player.broadcastPacket(new S_UseAttackSkill(_player, targetid, _targetX, _targetY, data));
                    _target.broadcastPacketExceptTargetSight(new S_DoActionGFX(targetid, ActionCodes.ACTION_Damage), _player);
                }
                else { // Ã¦Å“â€°Ã¦â€“Â¹Ã¥ï¿½â€˜Ã§Â¯â€žÃ¥â€ºÂ²Ã¦â€�Â»Ã¦â€™Æ’Ã©Â­â€�Ã¦Â³â€¢
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
            else if (_skill.getTarget().equals("none") && (_skill.getType() == L1Skills.TYPE_ATTACK)) { // Ã§â€žÂ¡Ã¦â€“Â¹Ã¥ï¿½â€˜Ã§Â¯â€žÃ¥â€ºÂ²Ã¦â€�Â»Ã¦â€™Æ’Ã©Â­â€�Ã¦Â³â€¢
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
            else { // Ã¨Â£Å“Ã¥Å Â©Ã©Â­â€�Ã¦Â³â€¢
                // Ã¦Å’â€¡Ã¥Â®Å¡Ã¥â€šÂ³Ã©â‚¬ï¿½Ã£â‚¬ï¿½Ã©â€ºâ€ Ã©Â«â€�Ã¥â€šÂ³Ã©â‚¬ï¿½Ã¨Â¡â€œÃ£â‚¬ï¿½Ã¤Â¸â€“Ã§â€¢Å’Ã¦Â¨Â¹Ã§Å¡â€žÃ¥â€˜Â¼Ã¥â€“Å¡Ã¤Â»Â¥Ã¥Â¤â€“
                if ((_skillId != TELEPORT) && (_skillId != MASS_TELEPORT) && (_skillId != TELEPORT_TO_MATHER)) {
                    // Ã¦â€“Â½Ã¦Â³â€¢Ã¥â€¹â€¢Ã¤Â½Å“
                    if (isSkillAction) {
                        S_DoActionGFX gfx = new S_DoActionGFX(_player.getId(), _skill.getActionId());
                        _player.sendPackets(gfx);
                        _player.broadcastPacket(gfx);
                    }
                    // Ã©Â­â€�Ã¦Â³â€¢Ã¥Â±ï¿½Ã©Å¡Å“Ã£â‚¬ï¿½Ã¥ï¿½ï¿½Ã¦â€œÅ Ã¥Â±ï¿½Ã©Å¡Å“Ã£â‚¬ï¿½Ã©ï¿½Â¡Ã¥ï¿½ï¿½Ã¥Â°â€ž Ã©Â­â€�Ã¦Â³â€¢Ã¦â€¢Ë†Ã¦Å¾Å“Ã¥ï¿½ÂªÃ¦Å“â€°Ã¨â€¡ÂªÃ¨ÂºÂ«Ã©Â¡Â¯Ã§Â¤Âº
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

                // Ã£â€šÂ¹Ã£â€šÂ­Ã£Æ’Â«Ã£ï¿½Â®Ã£â€šÂ¨Ã£Æ’â€¢Ã£â€šÂ§Ã£â€šÂ¯Ã£Æ’Ë†Ã¨Â¡Â¨Ã§Â¤ÂºÃ£ï¿½Â¯Ã£â€šÂ¿Ã£Æ’Â¼Ã£â€šÂ²Ã£Æ’Æ’Ã£Æ’Ë†Ã¥â€¦Â¨Ã¥â€œÂ¡Ã£ï¿½Â Ã£ï¿½Å’Ã£â‚¬ï¿½Ã£ï¿½â€šÃ£ï¿½Â¾Ã£â€šÅ Ã¥Â¿â€¦Ã¨Â¦ï¿½Ã¦â‚¬Â§Ã£ï¿½Å’Ã£ï¿½ÂªÃ£ï¿½â€žÃ£ï¿½Â®Ã£ï¿½Â§Ã£â‚¬ï¿½Ã£â€šÂ¹Ã£Æ’â€ Ã£Æ’Â¼Ã£â€šÂ¿Ã£â€šÂ¹Ã£ï¿½Â®Ã£ï¿½Â¿Ã©â‚¬ï¿½Ã¤Â¿Â¡
                for (TargetStatus ts : _targetList) {
                    L1Character cha = ts.getTarget();
                    if (cha instanceof L1PcInstance) {
                        L1PcInstance chaPc = (L1PcInstance) cha;
                        chaPc.sendPackets(new S_OwnCharStatus(chaPc));
                    }
                }
            }
        }
        else if (_user instanceof L1NpcInstance) { // NPCÃ£ï¿½Å’Ã£â€šÂ¹Ã£â€šÂ­Ã£Æ’Â«Ã£â€šâ€™Ã¤Â½Â¿Ã£ï¿½Â£Ã£ï¿½Å¸Ã¥Â Â´Ã¥ï¿½Ë†
            int targetid = _target.getId();

            if (_user instanceof L1MerchantInstance) {
                _user.broadcastPacket(new S_SkillSound(targetid, _gfxid));
                return;
            }

            if (_skillId == CURSE_PARALYZE || _skillId == WEAKNESS || _skillId == DISEASE) { // Ã¦Å“Â¨Ã¤Â¹Æ’Ã¤Â¼Å Ã§Å¡â€žÃ¨Â©â€ºÃ¥â€™â€™Ã£â‚¬ï¿½Ã¥Â¼Â±Ã¥Å’â€“Ã¨Â¡â€œÃ£â‚¬ï¿½Ã§â€“Â¾Ã§â€”â€¦Ã¨Â¡â€œ
                _user.setHeading(_user.targetDirection(_targetX, _targetY)); // Ã¦â€�Â¹Ã¨Â®Å Ã©ï¿½Â¢Ã¥ï¿½â€˜
                _user.broadcastPacket(new S_ChangeHeading(_user));
            }

            if (_targetList.isEmpty() && !(_skill.getTarget().equals("none"))) {
                // Ã£â€šÂ¿Ã£Æ’Â¼Ã£â€šÂ²Ã£Æ’Æ’Ã£Æ’Ë†Ã¦â€¢Â°Ã£ï¿½Å’Ã¯Â¼ï¿½Ã£ï¿½Â§Ã¥Â¯Â¾Ã¨Â±Â¡Ã£â€šâ€™Ã¦Å’â€¡Ã¥Â®Å¡Ã£ï¿½â„¢Ã£â€šâ€¹Ã£â€šÂ¹Ã£â€šÂ­Ã£Æ’Â«Ã£ï¿½Â®Ã¥Â Â´Ã¥ï¿½Ë†Ã£â‚¬ï¿½Ã©Â­â€�Ã¦Â³â€¢Ã¤Â½Â¿Ã§â€�Â¨Ã£â€šÂ¨Ã£Æ’â€¢Ã£â€šÂ§Ã£â€šÂ¯Ã£Æ’Ë†Ã£ï¿½Â Ã£ï¿½â€˜Ã¨Â¡Â¨Ã§Â¤ÂºÃ£ï¿½â€”Ã£ï¿½Â¦Ã§Âµâ€šÃ¤Âºâ€ 
                S_DoActionGFX gfx = new S_DoActionGFX(_user.getId(), _actid);
                _user.broadcastPacket(gfx);
                return;
            }

            if (_skill.getTarget().equals("attack") && (_skillId != 18)) {
                if (getSkillArea() == 0) { // Ã¥â€“Â®Ã©Â«â€�Ã¦â€�Â»Ã¦â€œÅ Ã©Â­â€�Ã¦Â³â€¢
                    data = new int[] {_actid, _dmg, _gfxid, 6};
                    _user.broadcastPacket(new S_UseAttackSkill(_user, targetid, _targetX, _targetY, data));
                    _target.broadcastPacketExceptTargetSight(new S_DoActionGFX(targetid, ActionCodes.ACTION_Damage), _user);
                }
                else { // Ã¦Å“â€°Ã¦â€“Â¹Ã¥ï¿½â€˜Ã§Â¯â€žÃ¥â€ºÂ²Ã¦â€�Â»Ã¦â€™Æ’Ã©Â­â€�Ã¦Â³â€¢
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
            else if (_skill.getTarget().equals("none") && (_skill.getType() == L1Skills.TYPE_ATTACK)) { // Ã§â€žÂ¡Ã¦â€“Â¹Ã¥ï¿½â€˜Ã§Â¯â€žÃ¥â€ºÂ²Ã¦â€�Â»Ã¦â€™Æ’Ã©Â­â€�Ã¦Â³â€¢
                L1Character[] cha = new L1Character[_targetList.size()];
                int i = 0;
                for (TargetStatus ts : _targetList) {
                    cha[i] = ts.getTarget();
                    i++;
                }
                _user.broadcastPacket(new S_RangeSkill(_user, cha, _gfxid, _actid, S_RangeSkill.TYPE_NODIR));
            }
            else { // Ã¨Â£Å“Ã¥Å Â©Ã©Â­â€�Ã¦Â³â€¢
                // Ã£Æ’â€ Ã£Æ’Â¬Ã£Æ’ï¿½Ã£Æ’Â¼Ã£Æ’Ë†Ã£â‚¬ï¿½Ã£Æ’Å¾Ã£â€šÂ¹Ã£Æ’â€ Ã£Æ’Â¬Ã£â‚¬ï¿½Ã£Æ’â€ Ã£Æ’Â¬Ã£Æ’ï¿½Ã£Æ’Â¼Ã£Æ’Ë†Ã£Æ’Ë†Ã£â€šÂ¥Ã£Æ’Å¾Ã£â€šÂ¶Ã£Æ’Â¼Ã¤Â»Â¥Ã¥Â¤â€“
                if ((_skillId != 5) && (_skillId != 69) && (_skillId != 131)) {
                    // Ã©Â­â€�Ã¦Â³â€¢Ã£â€šâ€™Ã¤Â½Â¿Ã£ï¿½â€ Ã¥â€¹â€¢Ã¤Â½Å“Ã£ï¿½Â®Ã£â€šÂ¨Ã£Æ’â€¢Ã£â€šÂ§Ã£â€šÂ¯Ã£Æ’Ë†Ã£ï¿½Â¯Ã¤Â½Â¿Ã§â€�Â¨Ã¨â‚¬â€¦Ã£ï¿½Â Ã£ï¿½â€˜
                    S_DoActionGFX gfx = new S_DoActionGFX(_user.getId(), _actid);
                    _user.broadcastPacket(gfx);
                    _user.broadcastPacket(new S_SkillSound(targetid, _gfxid));
                }
            }
        }
    }

    /** Ã¥Ë†ÂªÃ©â„¢Â¤Ã©â€¡ï¿½Ã¨Â¤â€¡Ã§Å¡â€žÃ©Â­â€�Ã¦Â³â€¢Ã§â€¹â‚¬Ã¦â€¦â€¹ */
    private void deleteRepeatedSkills(L1Character cha) {
        final int[][] repeatedSkills =
                {

                        // Ã§ï¿½Â«Ã§â€žÂ°Ã¦Â­Â¦Ã¥â„¢Â¨Ã£â‚¬ï¿½Ã©Â¢Â¨Ã¤Â¹â€¹Ã§Â¥Å¾Ã¥Â°â€žÃ£â‚¬ï¿½Ã§Æ’Ë†Ã§â€šÅ½Ã¦Â°Â£Ã¦ï¿½Â¯Ã£â‚¬ï¿½Ã¦Å¡Â´Ã©Â¢Â¨Ã¤Â¹â€¹Ã§Å“Â¼Ã£â‚¬ï¿½Ã§Æ’Ë†Ã§â€šÅ½Ã¦Â­Â¦Ã¥â„¢Â¨Ã£â‚¬ï¿½Ã¦Å¡Â´Ã©Â¢Â¨Ã§Â¥Å¾Ã¥Â°â€žÃ£â‚¬ï¿½Ã¥ÂªÂ½Ã§Â¥â€“Ã§Å¡â€žÃ§Â¥ï¿½Ã§Â¦ï¿½
                        { FIRE_WEAPON, WIND_SHOT, FIRE_BLESS, STORM_EYE, BURNING_WEAPON, STORM_SHOT, EFFECT_BLESS_OF_MAZU },
                        // Ã©ËœÂ²Ã¨Â­Â·Ã§Â½Â©Ã£â‚¬ï¿½Ã¥Â½Â±Ã¤Â¹â€¹Ã©ËœÂ²Ã¨Â­Â·Ã£â‚¬ï¿½Ã¥Â¤Â§Ã¥Å“Â°Ã©ËœÂ²Ã¨Â­Â·Ã£â‚¬ï¿½Ã¥Â¤Â§Ã¥Å“Â°Ã§Å¡â€žÃ§Â¥ï¿½Ã§Â¦ï¿½Ã£â‚¬ï¿½Ã©â€¹Â¼Ã©ï¿½ÂµÃ©ËœÂ²Ã¨Â­Â·
                        { SHIELD, SHADOW_ARMOR, EARTH_SKIN, EARTH_BLESS, IRON_SKIN },
                        // Ã¥â€¹â€¡Ã¦â€¢Â¢Ã¨â€”Â¥Ã¦Â°Â´Ã£â‚¬ï¿½Ã§Â²Â¾Ã©ï¿½Ë†Ã©Â¤â€¦Ã¤Â¹Â¾Ã£â‚¬ï¿½(Ã§Â¥Å¾Ã¨ï¿½â€“Ã§â€“Â¾Ã¨ÂµÂ°Ã£â‚¬ï¿½Ã¨Â¡Å’Ã¨ÂµÂ°Ã¥Å Â Ã©â‚¬Å¸Ã£â‚¬ï¿½Ã©Â¢Â¨Ã¤Â¹â€¹Ã§â€“Â¾Ã¨ÂµÂ°)Ã£â‚¬ï¿½Ã¨Â¶â€¦Ã§Â´Å¡Ã¥Å Â Ã©â‚¬Å¸Ã£â‚¬ï¿½Ã¨Â¡â‚¬Ã¤Â¹â€¹Ã¦Â¸Â´Ã¦Å“â€º
                        { STATUS_BRAVE, STATUS_ELFBRAVE, HOLY_WALK, MOVING_ACCELERATION, WIND_WALK, STATUS_BRAVE2, BLOODLUST },
                        // Ã¥Å Â Ã©â‚¬Å¸Ã¨Â¡â€œÃ£â‚¬ï¿½Ã¥Â¼Â·Ã¥Å â€ºÃ¥Å Â Ã©â‚¬Å¸Ã¨Â¡â€œÃ£â‚¬ï¿½Ã¨â€¡ÂªÃ¦Ë†â€˜Ã¥Å Â Ã©â‚¬Å¸Ã¨â€”Â¥Ã¦Â°Â´
                        { HASTE, GREATER_HASTE, STATUS_HASTE },
                        // Ã§Â·Â©Ã©â‚¬Å¸Ã£â‚¬ï¿½Ã©â€ºâ€ Ã©Â«â€�Ã§Â·Â©Ã¨Â¡â€œÃ£â‚¬ï¿½Ã¥Å“Â°Ã©ï¿½Â¢Ã©Å¡Å“Ã§Â¤â„¢
                        { SLOW , MASS_SLOW , ENTANGLE },
                        // Ã©â‚¬Å¡Ã¦Å¡Â¢Ã¦Â°Â£Ã¨â€žË†Ã¨Â¡â€œÃ£â‚¬ï¿½Ã¦â€¢ï¿½Ã¦ï¿½Â·Ã¦ï¿½ï¿½Ã¥ï¿½â€¡
                        { PHYSICAL_ENCHANT_DEX, DRESS_DEXTERITY },
                        // Ã©Â«â€�Ã©Â­â€žÃ¥Â¼Â·Ã¥ï¿½Â¥Ã¨Â¡â€œÃ£â‚¬ï¿½Ã¥Å â€ºÃ©â€¡ï¿½Ã¦ï¿½ï¿½Ã¥ï¿½â€¡
                        { PHYSICAL_ENCHANT_STR, DRESS_MIGHTY },
                        // Ã¦Â¿â‚¬Ã¥â€¹ÂµÃ¥Â£Â«Ã¦Â°Â£Ã£â‚¬ï¿½Ã©â€¹Â¼Ã©ï¿½ÂµÃ¥Â£Â«Ã¦Â°Â£
                        { GLOWING_AURA, SHINING_AURA },
                        // Ã©ï¿½Â¡Ã¥Æ’ï¿½Ã£â‚¬ï¿½Ã¦Å¡â€”Ã¥Â½Â±Ã©â€“Æ’Ã©ï¿½Â¿
                        { MIRROR_IMAGE, UNCANNY_DODGE } };


        for (int[] skills : repeatedSkills) {
            for (int id : skills) {
                if (id == _skillId) {
                    stopSkillList(cha, skills);
                }
            }
        }
    }

    // Ã©â€¡ï¿½Ã¨Â¤â€¡Ã£ï¿½â€”Ã£ï¿½Â¦Ã£ï¿½â€žÃ£â€šâ€¹Ã£â€šÂ¹Ã£â€šÂ­Ã£Æ’Â«Ã£â€šâ€™Ã¤Â¸â‚¬Ã¦â€”Â¦Ã£ï¿½â„¢Ã£ï¿½Â¹Ã£ï¿½Â¦Ã¥â€°Å Ã©â„¢Â¤
    private void stopSkillList(L1Character cha, int[] repeat_skill) {
        for (int skillId : repeat_skill) {
            if (skillId != _skillId) {
                cha.removeSkillEffect(skillId);
            }
        }
    }

    // Ã£Æ’â€¡Ã£â€šÂ£Ã£Æ’Â¬Ã£â€šÂ¤Ã£ï¿½Â®Ã¨Â¨Â­Ã¥Â®Å¡
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
            case FIRE_WALL: // Ã§ï¿½Â«Ã§â€°Â¢
                L1EffectSpawn.getInstance().doSpawnFireWall(_user, _targetX, _targetY);
                return;
            case TRUE_TARGET: // Ã§Â²Â¾Ã¦Âºâ€“Ã§â€ºÂ®Ã¦Â¨â„¢
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

        // Ã©Â­â€�Ã¦Â³â€¢Ã¥Â±ï¿½Ã©Å¡Å“Ã¤Â¸ï¿½Ã¥ï¿½Â¯Ã¦Å ÂµÃ¦â€œâ€¹Ã§Å¡â€žÃ©Â­â€�Ã¦Â³â€¢
        for (int skillId : EXCEPT_COUNTER_MAGIC) {
            if (_skillId == skillId) {
                _isCounterMagic = false;
                break;
            }
        }

        // NPCÃ£ï¿½Â«Ã£â€šÂ·Ã£Æ’Â§Ã£Æ’Æ’Ã£â€šÂ¯Ã£â€šÂ¹Ã£â€šÂ¿Ã£Æ’Â³Ã£â€šâ€™Ã¤Â½Â¿Ã§â€�Â¨Ã£ï¿½â€¢Ã£ï¿½â€ºÃ£â€šâ€¹Ã£ï¿½Â¨onActionÃ£ï¿½Â§NullPointerExceptionÃ£ï¿½Å’Ã§â„¢ÂºÃ§â€�Å¸Ã£ï¿½â„¢Ã£â€šâ€¹Ã£ï¿½Å¸Ã£â€šï¿½
        // Ã£ï¿½Â¨Ã£â€šÅ Ã£ï¿½â€šÃ£ï¿½Ë†Ã£ï¿½Å¡PCÃ£ï¿½Å’Ã¤Â½Â¿Ã§â€�Â¨Ã£ï¿½â€”Ã£ï¿½Å¸Ã¦â„¢â€šÃ£ï¿½Â®Ã£ï¿½Â¿
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
                    continue; // Ã¨Â¨Ë†Ã§Â®â€”Ã£ï¿½â„¢Ã£â€šâ€¹Ã¥Â¿â€¦Ã¨Â¦ï¿½Ã£ï¿½Å’Ã£ï¿½ÂªÃ£ï¿½â€žÃ£â‚¬â€š
                }

                L1Magic _magic = new L1Magic(_user, cha);
                _magic.setLeverage(getLeverage());

                if (cha instanceof L1MonsterInstance) { // Ã¤Â¸ï¿½Ã¦Â­Â»Ã¤Â¿â€šÃ¥Ë†Â¤Ã¦â€“Â·
                    undeadType = ((L1MonsterInstance) cha).getNpcTemplate().get_undead();
                }

                // Ã§Â¢ÂºÃ§Å½â€¡Ã§Â³Â»Ã£â€šÂ¹Ã£â€šÂ­Ã£Æ’Â«Ã£ï¿½Â§Ã¥Â¤Â±Ã¦â€¢â€”Ã£ï¿½Å’Ã§Â¢ÂºÃ¥Â®Å¡Ã£ï¿½â€”Ã£ï¿½Â¦Ã£ï¿½â€žÃ£â€šâ€¹Ã¥Â Â´Ã¥ï¿½Ë†
                if (((_skill.getType() == L1Skills.TYPE_CURSE) || (_skill.getType() == L1Skills.TYPE_PROBABILITY)) && isTargetFailure(cha)) {
                    iter.remove();
                    continue;
                }

                if (cha instanceof L1PcInstance) { // Ã£â€šÂ¿Ã£Æ’Â¼Ã£â€šÂ²Ã£Æ’Æ’Ã£Æ’Ë†Ã£ï¿½Å’PCÃ£ï¿½Â®Ã¥Â Â´Ã¥ï¿½Ë†Ã£ï¿½Â®Ã£ï¿½Â¿Ã£â€šÂ¢Ã£â€šÂ¤Ã£â€šÂ³Ã£Æ’Â³Ã£ï¿½Â¯Ã©â‚¬ï¿½Ã¤Â¿Â¡Ã£ï¿½â„¢Ã£â€šâ€¹Ã£â‚¬â€š
                    if (_skillTime == 0) {
                        _getBuffIconDuration = _skill.getBuffDuration(); // Ã¥Å Â¹Ã¦Å¾Å“Ã¦â„¢â€šÃ©â€“â€œ
                    }
                    else {
                        _getBuffIconDuration = _skillTime; // Ã£Æ’â€˜Ã£Æ’Â©Ã£Æ’Â¡Ã£Æ’Â¼Ã£â€šÂ¿Ã£ï¿½Â®timeÃ£ï¿½Å’0Ã¤Â»Â¥Ã¥Â¤â€“Ã£ï¿½ÂªÃ£â€šâ€°Ã£â‚¬ï¿½Ã¥Å Â¹Ã¦Å¾Å“Ã¦â„¢â€šÃ©â€“â€œÃ£ï¿½Â¨Ã£ï¿½â€”Ã£ï¿½Â¦Ã¨Â¨Â­Ã¥Â®Å¡Ã£ï¿½â„¢Ã£â€šâ€¹
                    }
                }

                deleteRepeatedSkills(cha); // Ã¥Ë†ÂªÃ©â„¢Â¤Ã§â€žÂ¡Ã¦Â³â€¢Ã¥â€¦Â±Ã¥ï¿½Å’Ã¥Â­ËœÃ¥Å“Â¨Ã§Å¡â€žÃ©Â­â€�Ã¦Â³â€¢Ã§â€¹â‚¬Ã¦â€¦â€¹

                if ((_skill.getType() == L1Skills.TYPE_ATTACK) && (_user.getId() != cha.getId())) { // Ã¦â€�Â»Ã¦â€™Æ’Ã§Â³Â»Ã£â€šÂ¹Ã£â€šÂ­Ã£Æ’Â«Ã¯Â¼â€ Ã£â€šÂ¿Ã£Æ’Â¼Ã£â€šÂ²Ã£Æ’Æ’Ã£Æ’Ë†Ã£ï¿½Å’Ã¤Â½Â¿Ã§â€�Â¨Ã¨â‚¬â€¦Ã¤Â»Â¥Ã¥Â¤â€“Ã£ï¿½Â§Ã£ï¿½â€šÃ£â€šâ€¹Ã£ï¿½â€œÃ£ï¿½Â¨Ã£â‚¬â€š
                    if (isUseCounterMagic(cha)) { // Ã£â€šÂ«Ã£â€šÂ¦Ã£Æ’Â³Ã£â€šÂ¿Ã£Æ’Â¼Ã£Æ’Å¾Ã£â€šÂ¸Ã£Æ’Æ’Ã£â€šÂ¯Ã£ï¿½Å’Ã§â„¢ÂºÃ¥â€¹â€¢Ã£ï¿½â€”Ã£ï¿½Å¸Ã¥Â Â´Ã¥ï¿½Ë†Ã£â‚¬ï¿½Ã£Æ’ÂªÃ£â€šÂ¹Ã£Æ’Ë†Ã£ï¿½â€¹Ã£â€šâ€°Ã¥â€°Å Ã©â„¢Â¤
                        iter.remove();
                        continue;
                    }
                    dmg = _magic.calcMagicDamage(_skillId);
                    _dmg = dmg;

                    // Triple Arrow and FOE Slayer should not cancel erase - [Hank]
                    if((_skillId != TRIPLE_ARROW) && (_skillId != FOE_SLAYER))
                    {
                        cha.removeSkillEffect(ERASE_MAGIC); // Ã£â€šÂ¤Ã£Æ’Â¬Ã£Æ’Â¼Ã£â€šÂ¹Ã£Æ’Å¾Ã£â€šÂ¸Ã£Æ’Æ’Ã£â€šÂ¯Ã¤Â¸Â­Ã£ï¿½ÂªÃ£â€šâ€°Ã£â‚¬ï¿½Ã¦â€�Â»Ã¦â€™Æ’Ã©Â­â€�Ã¦Â³â€¢Ã£ï¿½Â§Ã¨Â§Â£Ã©â„¢Â¤

                    }
                }
                else if ((_skill.getType() == L1Skills.TYPE_CURSE) || (_skill.getType() == L1Skills.TYPE_PROBABILITY)) { // Ã§Â¢ÂºÃ§Å½â€¡Ã§Â³Â»Ã£â€šÂ¹Ã£â€šÂ­Ã£Æ’Â«
                    isSuccess = _magic.calcProbabilityMagic(_skillId);
                    if (_skillId != ERASE_MAGIC) {
                        cha.removeSkillEffect(ERASE_MAGIC); // Ã£â€šÂ¤Ã£Æ’Â¬Ã£Æ’Â¼Ã£â€šÂ¹Ã£Æ’Å¾Ã£â€šÂ¸Ã£Æ’Æ’Ã£â€šÂ¯Ã¤Â¸Â­Ã£ï¿½ÂªÃ£â€šâ€°Ã£â‚¬ï¿½Ã§Â¢ÂºÃ§Å½â€¡Ã©Â­â€�Ã¦Â³â€¢Ã£ï¿½Â§Ã¨Â§Â£Ã©â„¢Â¤
                    }
                    if (_skillId != FOG_OF_SLEEPING) {
                        cha.removeSkillEffect(FOG_OF_SLEEPING); // Ã£Æ’â€¢Ã£â€šÂ©Ã£â€šÂ°Ã£â€šÂªÃ£Æ’â€“Ã£â€šÂ¹Ã£Æ’ÂªÃ£Æ’Â¼Ã£Æ’â€�Ã£Æ’Â³Ã£â€šÂ°Ã¤Â¸Â­Ã£ï¿½ÂªÃ£â€šâ€°Ã£â‚¬ï¿½Ã§Â¢ÂºÃ§Å½â€¡Ã©Â­â€�Ã¦Â³â€¢Ã£ï¿½Â§Ã¨Â§Â£Ã©â„¢Â¤
                    }
                    if (isSuccess) { // Ã¦Ë†ï¿½Ã¥Å Å¸Ã£ï¿½â€”Ã£ï¿½Å¸Ã£ï¿½Å’Ã£â€šÂ«Ã£â€šÂ¦Ã£Æ’Â³Ã£â€šÂ¿Ã£Æ’Â¼Ã£Æ’Å¾Ã£â€šÂ¸Ã£Æ’Æ’Ã£â€šÂ¯Ã£ï¿½Å’Ã§â„¢ÂºÃ¥â€¹â€¢Ã£ï¿½â€”Ã£ï¿½Å¸Ã¥Â Â´Ã¥ï¿½Ë†Ã£â‚¬ï¿½Ã£Æ’ÂªÃ£â€šÂ¹Ã£Æ’Ë†Ã£ï¿½â€¹Ã£â€šâ€°Ã¥â€°Å Ã©â„¢Â¤
                        if (isUseCounterMagic(cha)) { // Ã£â€šÂ«Ã£â€šÂ¦Ã£Æ’Â³Ã£â€šÂ¿Ã£Æ’Â¼Ã£Æ’Å¾Ã£â€šÂ¸Ã£Æ’Æ’Ã£â€šÂ¯Ã£ï¿½Å’Ã§â„¢ÂºÃ¥â€¹â€¢Ã£ï¿½â€”Ã£ï¿½Å¸Ã£ï¿½â€¹
                            iter.remove();
                            continue;
                        }
                    }
                    else {
                        // adding Phantasm effect - [Hank]
                        if (((_skillId == PHANTASM ) ||(_skillId == FOG_OF_SLEEPING)) && (cha instanceof L1PcInstance)) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.sendPackets(new S_ServerMessage(297)); // é›¿îºŸï¿½î¸„æ­»éˆ­î®�å‡�ï¿½î¯µï¿½ï¿½ïŽˆî¯®ï¿½ï¿½
                        }
                        iter.remove();
                        continue;
                    }
                }
                // Ã¦Â²Â»Ã§â„¢â€™Ã¦â‚¬Â§Ã©Â­â€�Ã¦Â³â€¢
                else if (_skill.getType() == L1Skills.TYPE_HEAL) {
                    // Ã¥â€ºÅ¾Ã¥Â¾Â©Ã©â€¡ï¿½
                    dmg = -1 * _magic.calcHealing(_skillId);
                    if (cha.hasSkillEffect(WATER_LIFE)) { // Ã¦Â°Â´Ã¤Â¹â€¹Ã¥â€¦Æ’Ã¦Â°Â£-Ã¦â€¢Ë†Ã¦Å¾Å“ 2Ã¥â‚¬ï¿½
                        dmg *= 2;
                        cha.killSkillEffectTimer(WATER_LIFE); // Ã¦â€¢Ë†Ã¦Å¾Å“Ã¥ï¿½ÂªÃ¦Å“â€°Ã¤Â¸â‚¬Ã¦Â¬Â¡
                        if (cha instanceof L1PcInstance) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.sendPackets(new S_SkillIconWaterLife());
                        }
                    }
                    if (cha.hasSkillEffect(POLLUTE_WATER)) { // Ã¦Â±â„¢Ã¦Â¿ï¿½Ã¤Â¹â€¹Ã¦Â°Â´-Ã¦â€¢Ë†Ã¦Å¾Å“Ã¦Â¸â€ºÃ¥ï¿½Å 
                        dmg /= 2;
                    }
                }
                // Ã©Â¡Â¯Ã§Â¤ÂºÃ¥Å“ËœÃ©Â«â€�Ã©Â­â€�Ã¦Â³â€¢Ã¦â€¢Ë†Ã¦Å¾Å“Ã¥Å“Â¨Ã©Å¡Å Ã¥ï¿½â€¹Ã¦Ë†â€“Ã§â€ºÅ¸Ã¥ï¿½â€¹
                else if ((_skillId == FIRE_BLESS || _skillId == STORM_EYE // Ã§Æ’Ë†Ã§â€šÅ½Ã¦Â°Â£Ã¦ï¿½Â¯Ã£â‚¬ï¿½Ã¦Å¡Â´Ã©Â¢Â¨Ã¤Â¹â€¹Ã§Å“Â¼
                        || _skillId == EARTH_BLESS // Ã¥Â¤Â§Ã¥Å“Â°Ã§Å¡â€žÃ§Â¥ï¿½Ã§Â¦ï¿½
                        || _skillId == GLOWING_AURA // Ã¦Â¿â‚¬Ã¥â€¹ÂµÃ¥Â£Â«Ã¦Â°Â£
                        || _skillId == SHINING_AURA || _skillId == BRAVE_AURA) // Ã©â€¹Â¼Ã©ï¿½ÂµÃ¥Â£Â«Ã¦Â°Â£Ã£â‚¬ï¿½Ã¨Â¡ï¿½Ã¦â€œÅ Ã¥Â£Â«Ã¦Â°Â£
                        && _user.getId() != cha.getId()) {
                    if (cha instanceof L1PcInstance) {
                        L1PcInstance _targetPc = (L1PcInstance) cha;
                        _targetPc.sendPackets(new S_SkillSound(_targetPc.getId(), _skill.getCastGfx()));
                        _targetPc.broadcastPacket(new S_SkillSound(_targetPc.getId(), _skill.getCastGfx()));
                    }
                }

                // Ã¢â€“Â Ã¢â€“Â Ã¢â€“Â Ã¢â€“Â  Ã¥â‚¬â€¹Ã¥Ë†Â¥Ã¥â€¡Â¦Ã§ï¿½â€ Ã£ï¿½Â®Ã£ï¿½â€šÃ£â€šâ€¹Ã£â€šÂ¹Ã£â€šÂ­Ã£Æ’Â«Ã£ï¿½Â®Ã£ï¿½Â¿Ã¦â€ºÂ¸Ã£ï¿½â€žÃ£ï¿½Â¦Ã£ï¿½ï¿½Ã£ï¿½Â Ã£ï¿½â€¢Ã£ï¿½â€žÃ£â‚¬â€š Ã¢â€“Â Ã¢â€“Â Ã¢â€“Â Ã¢â€“Â 

                // Ã©â„¢Â¤Ã¤Âºâ€ Ã¨Â¡ï¿½Ã¦Å¡Ë†Ã£â‚¬ï¿½Ã©ÂªÂ·Ã©Â«ï¿½Ã¦Â¯â‚¬Ã¥Â£Å¾Ã¤Â¹â€¹Ã¥Â¤â€“Ã©Â­â€�Ã¦Â³â€¢Ã¦â€¢Ë†Ã¦Å¾Å“Ã¥Â­ËœÃ¥Å“Â¨Ã¦â„¢â€šÃ¯Â¼Å’Ã¥ï¿½ÂªÃ¦â€ºÂ´Ã¦â€“Â°Ã¦â€¢Ë†Ã¦Å¾Å“Ã¦â„¢â€šÃ©â€“â€œÃ¨Â·Å¸Ã¥Å“â€“Ã§Â¤ÂºÃ£â‚¬â€š
                if (cha.hasSkillEffect(_skillId) && (_skillId != SHOCK_STUN && _skillId != BONE_BREAK && _skillId != CONFUSION && _skillId != THUNDER_GRAB)) {
                    addMagicList(cha, true); // Ã©Â­â€�Ã¦Â³â€¢Ã¦â€¢Ë†Ã¦Å¾Å“Ã¥Â·Â²Ã¥Â­ËœÃ¥Å“Â¨Ã¦â„¢â€š
                    if (_skillId != SHAPE_CHANGE) { // Ã©â„¢Â¤Ã¤Âºâ€ Ã¨Â®Å Ã¥Â½Â¢Ã¨Â¡â€œÃ¤Â¹â€¹Ã¥Â¤â€“
                        continue;
                    }
                }

                switch(_skillId) {
                    // Ã¥Å Â Ã©â‚¬Å¸Ã¨Â¡â€œ
                    case HASTE:
                        if (cha.getMoveSpeed() != 2) { // Ã£â€šÂ¹Ã£Æ’Â­Ã£Æ’Â¼Ã¤Â¸Â­Ã¤Â»Â¥Ã¥Â¤â€“
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
                        else { // Ã£â€šÂ¹Ã£Æ’Â­Ã£Æ’Â¼Ã¤Â¸Â­
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
                    // Ã¥Â¼Â·Ã¥Å â€ºÃ¥Å Â Ã©â‚¬Å¸Ã¨Â¡â€œ
                    case GREATER_HASTE:
                        if (cha instanceof L1PcInstance) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            if (pc.getHasteItemEquipped() > 0) {
                                continue;
                            }
                            if (pc.getMoveSpeed() != 2) { // Ã£â€šÂ¹Ã£Æ’Â­Ã£Æ’Â¼Ã¤Â¸Â­Ã¤Â»Â¥Ã¥Â¤â€“
                                pc.setDrink(false);
                                pc.setMoveSpeed(1);
                                pc.sendPackets(new S_SkillHaste(pc.getId(), 1, _getBuffIconDuration));
                                pc.broadcastPacket(new S_SkillHaste(pc.getId(), 1, 0));
                            }
                            else { // Ã£â€šÂ¹Ã£Æ’Â­Ã£Æ’Â¼Ã¤Â¸Â­
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
                    // Ã§Â·Â©Ã©â‚¬Å¸Ã¨Â¡â€œÃ£â‚¬ï¿½Ã©â€ºâ€ Ã©Â«â€�Ã§Â·Â©Ã©â‚¬Å¸Ã¨Â¡â€œÃ£â‚¬ï¿½Ã¥Å“Â°Ã©ï¿½Â¢Ã©Å¡Å“Ã§Â¤â„¢
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
                    // Ã¤ÂºÅ¾Ã¥Å â€ºÃ¥Â®â€°Ã¥â€ Â°Ã§Å¸â€ºÃ¥Å“ï¿½Ã§Â±Â¬
                    case ICE_LANCE_COCKATRICE:
                        // Ã©â€šÂªÃ¦Æ’Â¡Ã¨Å“Â¥Ã¨Å“Â´Ã¥â€ Â°Ã§Å¸â€ºÃ¥Å“ï¿½Ã§Â±Â¬
                    case ICE_LANCE_BASILISK:
                        // Ã¥â€ Â°Ã¦Â¯â€ºÃ¥Å“ï¿½Ã§Â±Â¬Ã£â‚¬ï¿½Ã¥â€ Â°Ã©â€ºÂªÃ©Â¢Â¶Ã©Â¢Â¨Ã£â‚¬ï¿½Ã¥Â¯â€™Ã¥â€ Â°Ã¥â„¢Â´Ã¥ï¿½ï¿½
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
                    // Ã¥Â¤Â§Ã¥Å“Â°Ã¥Â±ï¿½Ã©Å¡Å“
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
                    case 20011: // Ã¦Â¯â€™Ã©Å“Â§-Ã¥â€°ï¿½Ã¦â€“Â¹ 3X3
                        _user.setHeading(_user.targetDirection(_targetX, _targetY)); // Ã¦â€�Â¹Ã¨Â®Å Ã©ï¿½Â¢Ã¥ï¿½â€˜
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
                    // Ã¨Â¡ï¿½Ã¦â€œÅ Ã¤Â¹â€¹Ã¦Å¡Ë†
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
                    // Ã¥Â¥ÂªÃ¥â€˜Â½Ã¤Â¹â€¹Ã©â€ºÂ·
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
                    // Ã¨ÂµÂ·Ã¦Â­Â»Ã¥â€ºÅ¾Ã§â€�Å¸Ã¨Â¡â€œ
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
                    // Ã¦Å’â€¡Ã¥Â®Å¡Ã¥â€šÂ³Ã©â‚¬ï¿½Ã£â‚¬ï¿½Ã©â€ºâ€ Ã©Â«â€�Ã¥â€šÂ³Ã©â‚¬ï¿½Ã¨Â¡â€œ
                    case TELEPORT:
                    case MASS_TELEPORT:
                        if (cha instanceof L1PcInstance) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            L1BookMark bookm = pc.getBookMark(_bookmarkId);
                            if (bookm != null) { // Ã£Æ’â€“Ã£Æ’Æ’Ã£â€šÂ¯Ã£Æ’Å¾Ã£Æ’Â¼Ã£â€šÂ¯Ã£â€šâ€™Ã¥ï¿½â€“Ã¥Â¾â€”Ã¥â€¡ÂºÃ¦ï¿½Â¥Ã£ï¿½Å¸Ã£â€šâ€°Ã£Æ’â€ Ã£Æ’Â¬Ã£Æ’ï¿½Ã£Æ’Â¼Ã£Æ’Ë†
                                if (pc.getMap().isEscapable() || pc.isGm()) {
                                    int newX = bookm.getLocX();
                                    int newY = bookm.getLocY();
                                    short mapId = bookm.getMapId();

                                    if (_skillId == MASS_TELEPORT) { // Ã£Æ’Å¾Ã£â€šÂ¹Ã£Æ’â€ Ã£Æ’Â¬Ã£Æ’ï¿½Ã£Æ’Â¼Ã£Æ’Ë†
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
                            else { // Ã£Æ’â€“Ã£Æ’Æ’Ã£â€šÂ¯Ã£Æ’Å¾Ã£Æ’Â¼Ã£â€šÂ¯Ã£ï¿½Å’Ã¥ï¿½â€“Ã¥Â¾â€”Ã¥â€¡ÂºÃ¦ï¿½Â¥Ã£ï¿½ÂªÃ£ï¿½â€¹Ã£ï¿½Â£Ã£ï¿½Å¸Ã£â‚¬ï¿½Ã£ï¿½â€šÃ£â€šâ€¹Ã£ï¿½â€žÃ£ï¿½Â¯Ã£â‚¬Å’Ã¤Â»Â»Ã¦â€žï¿½Ã£ï¿½Â®Ã¥Â Â´Ã¦â€°â‚¬Ã£â‚¬ï¿½Ã£â€šâ€™Ã©ï¿½Â¸Ã¦Å Å¾Ã£ï¿½â€”Ã£ï¿½Å¸Ã¥Â Â´Ã¥ï¿½Ë†Ã£ï¿½Â®Ã¥â€¡Â¦Ã§ï¿½â€ 
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
                                    pc.sendPackets(new S_ServerMessage(276)); // \f1Ã¥Å“Â¨Ã¦Â­Â¤Ã§â€žÂ¡Ã¦Â³â€¢Ã¤Â½Â¿Ã§â€�Â¨Ã¥â€šÂ³Ã©â‚¬ï¿½Ã£â‚¬â€š
                                    pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, true));
                                    // using tele spell in non-teleportable map will give effect of .reload
                                    L1Teleport.teleport(pc, pc.getX(), pc.getY(), pc.getMapId(), 5, false);
                                }
                            }
                        }
                        break;
                    // Ã¥â€˜Â¼Ã¥â€“Å¡Ã§â€ºÅ¸Ã¥ï¿½â€¹
                    case CALL_CLAN:
                        if (cha instanceof L1PcInstance) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            L1PcInstance clanPc = (L1PcInstance) L1World.getInstance().findObject(_targetID);
                            if (clanPc != null) {
                                clanPc.setTempID(pc.getId());
                                clanPc.sendPackets(new S_Message_YN(729, "")); // Ã§â€ºÅ¸Ã¤Â¸Â»Ã¦Â­Â£Ã¥Å“Â¨Ã¥â€˜Â¼Ã¥â€“Å¡Ã¤Â½Â Ã¯Â¼Å’Ã¤Â½Â Ã¨Â¦ï¿½Ã¦Å½Â¥Ã¥ï¿½â€”Ã¤Â»â€“Ã§Å¡â€žÃ¥â€˜Â¼Ã¥â€“Å¡Ã¥â€”Å½Ã¯Â¼Å¸(Y/N)
                            }
                        }
                        break;
                    // Ã¦ï¿½Â´Ã¨Â­Â·Ã§â€ºÅ¸Ã¥ï¿½â€¹
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
                                    // Ã©â‚¬â„¢Ã©â„¢â€žÃ¨Â¿â€˜Ã§Å¡â€žÃ¨Æ’Â½Ã©â€¡ï¿½Ã¥Â½Â±Ã©Å¸Â¿Ã¥Ë†Â°Ã§Å¾Â¬Ã©â€“â€œÃ§Â§Â»Ã¥â€¹â€¢Ã£â‚¬â€šÃ¥Å“Â¨Ã¦Â­Â¤Ã¥Å“Â°Ã§â€žÂ¡Ã¦Â³â€¢Ã¤Â½Â¿Ã§â€�Â¨Ã§Å¾Â¬Ã©â€“â€œÃ§Â§Â»Ã¥â€¹â€¢Ã£â‚¬â€š
                                    pc.sendPackets(new S_ServerMessage(647));
                                    pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, true));
                                }
                            }
                        }
                        break;
                    // Ã¥Â¼Â·Ã¥Å â€ºÃ§â€žÂ¡Ã¦â€°â‚¬Ã©ï¿½ï¿½Ã¥Â½Â¢
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
                    // Ã¥â€°ÂµÃ©â‚¬Â Ã©Â­â€�Ã¦Â³â€¢Ã¦Â­Â¦Ã¥â„¢Â¨
                    case CREATE_MAGICAL_WEAPON:
                        if (cha instanceof L1PcInstance) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            L1ItemInstance item = pc.getInventory().getItem(_itemobjid);
                            if ((item != null) && (item.getItem().getType2() == 1)) {
                                int item_type = item.getItem().getType2();
                                int safe_enchant = item.getItem().get_safeenchant();
                                int enchant_level = item.getEnchantLevel();
                                String item_name = item.getName();
                                if (safe_enchant < 0) { // Ã¥Â¼Â·Ã¥Å’â€“Ã¤Â¸ï¿½Ã¥ï¿½Â¯
                                    pc.sendPackets( // \f1Ã¤Â½â€¢Ã£â€šâ€šÃ¨ÂµÂ·Ã£ï¿½ï¿½Ã£ï¿½Â¾Ã£ï¿½â€ºÃ£â€šâ€œÃ£ï¿½Â§Ã£ï¿½â€”Ã£ï¿½Å¸Ã£â‚¬â€š
                                            new S_ServerMessage(79));
                                }
                                else if (safe_enchant == 0) { // Ã¥Â®â€°Ã¥â€¦Â¨Ã¥Å“ï¿½+0
                                    pc.sendPackets( // \f1Ã¤Â½â€¢Ã£â€šâ€šÃ¨ÂµÂ·Ã£ï¿½ï¿½Ã£ï¿½Â¾Ã£ï¿½â€ºÃ£â€šâ€œÃ£ï¿½Â§Ã£ï¿½â€”Ã£ï¿½Å¸Ã£â‚¬â€š
                                            new S_ServerMessage(79));
                                }
                                else if ((item_type == 1) && (enchant_level == 0)) {
                                    if (!item.isIdentified()) {// Ã¦Å“ÂªÃ©â€˜â€˜Ã¥Â®Å¡
                                        pc.sendPackets( // \f1%0Ã£ï¿½Å’%2%1Ã¥â€¦â€°Ã£â€šÅ Ã£ï¿½Â¾Ã£ï¿½â„¢Ã£â‚¬â€š
                                                new S_ServerMessage(161, item_name, "$245", "$247"));
                                    }
                                    else {
                                        item_name = "+0 " + item_name;
                                        pc.sendPackets( // \f1%0Ã£ï¿½Å’%2%1Ã¥â€¦â€°Ã£â€šÅ Ã£ï¿½Â¾Ã£ï¿½â„¢Ã£â‚¬â€š
                                                new S_ServerMessage(161, "+0 " + item_name, "$245", "$247"));
                                    }
                                    item.setEnchantLevel(1);
                                    pc.getInventory().updateItem(item, L1PcInventory.COL_ENCHANTLVL);
                                }
                                else {
                                    pc.sendPackets( // \f1Ã¤Â½â€¢Ã£â€šâ€šÃ¨ÂµÂ·Ã£ï¿½ï¿½Ã£ï¿½Â¾Ã£ï¿½â€ºÃ£â€šâ€œÃ£ï¿½Â§Ã£ï¿½â€”Ã£ï¿½Å¸Ã£â‚¬â€š
                                            new S_ServerMessage(79));
                                }
                            }
                            else {
                                pc.sendPackets( // \f1Ã¤Â½â€¢Ã£â€šâ€šÃ¨ÂµÂ·Ã£ï¿½ï¿½Ã£ï¿½Â¾Ã£ï¿½â€ºÃ£â€šâ€œÃ£ï¿½Â§Ã£ï¿½â€”Ã£ï¿½Å¸Ã£â‚¬â€š
                                        new S_ServerMessage(79));
                            }
                        }
                        break;
                    // Ã¦ï¿½ï¿½Ã§â€¦â€°Ã©Â­â€�Ã§Å¸Â³
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
                                        pc.sendPackets(new S_ServerMessage(403, "$2475")); // Ã§ï¿½Â²Ã¥Â¾â€”%0%o Ã£â‚¬â€š
                                    } else {
                                        pc.sendPackets(new S_ServerMessage(280)); // \f1Ã¦â€“Â½Ã¥â€™â€™Ã¥Â¤Â±Ã¦â€¢â€”Ã£â‚¬â€š
                                    }
                                } else if (item.getItem().getItemId() == 40321) {
                                    pc.getInventory().removeItem(item, 1);
                                    if (brave >= run) {
                                        pc.getInventory().storeItem(40322, 1);
                                        pc.sendPackets(new S_ServerMessage(403, "$2476")); // Ã§ï¿½Â²Ã¥Â¾â€”%0%o Ã£â‚¬â€š
                                    } else {
                                        pc.sendPackets(new S_ServerMessage(280));// \f1Ã¦â€“Â½Ã¥â€™â€™Ã¥Â¤Â±Ã¦â€¢â€”Ã£â‚¬â€š
                                    }
                                } else if (item.getItem().getItemId() == 40322) {
                                    pc.getInventory().removeItem(item, 1);
                                    if (wise >= run) {
                                        pc.getInventory().storeItem(40323, 1);
                                        pc.sendPackets(new S_ServerMessage(403, "$2477")); // Ã§ï¿½Â²Ã¥Â¾â€”%0%o Ã£â‚¬â€š
                                    } else {
                                        pc.sendPackets(new S_ServerMessage(280));// \f1Ã¦â€“Â½Ã¥â€™â€™Ã¥Â¤Â±Ã¦â€¢â€”Ã£â‚¬â€š
                                    }
                                } else if (item.getItem().getItemId() == 40323) {
                                    pc.getInventory().removeItem(item, 1);
                                    if (kayser >= run) {
                                        pc.getInventory().storeItem(40324, 1);
                                        pc.sendPackets(new S_ServerMessage(403, "$2478")); // Ã§ï¿½Â²Ã¥Â¾â€”%0%o Ã£â‚¬â€š
                                    } else {
                                        pc.sendPackets(new S_ServerMessage(280));// \f1Ã¦â€“Â½Ã¥â€™â€™Ã¥Â¤Â±Ã¦â€¢â€”Ã£â‚¬â€š
                                    }
                                }
                            }
                        }
                        break;
                    // Ã¦â€”Â¥Ã¥â€¦â€°Ã¨Â¡â€œ
                    case LIGHT:

                        if (cha instanceof L1PcInstance) {

                        }
                        break;
                    // Ã¦Å¡â€”Ã¥Â½Â±Ã¤Â¹â€¹Ã§â€°â„¢
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
                    // Ã¦â€œÂ¬Ã¤Â¼Â¼Ã©Â­â€�Ã¦Â³â€¢Ã¦Â­Â¦Ã¥â„¢Â¨
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
                    // Ã§Â¥Å¾Ã¨ï¿½â€“Ã¦Â­Â¦Ã¥â„¢Â¨Ã£â‚¬ï¿½Ã§Â¥ï¿½Ã§Â¦ï¿½Ã©Â­â€�Ã¦Â³â€¢Ã¦Â­Â¦Ã¥â„¢Â¨
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
                    // Ã©Å½Â§Ã§â€�Â²Ã¨Â­Â·Ã¦Å’ï¿½
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

                // Ã¢â€“Â Ã¢â€“Â Ã¢â€“Â Ã¢â€“Â  Ã¥â‚¬â€¹Ã¥Ë†Â¥Ã¥â€¡Â¦Ã§ï¿½â€ Ã£ï¿½â€œÃ£ï¿½â€œÃ£ï¿½Â¾Ã£ï¿½Â§ Ã¢â€“Â Ã¢â€“Â Ã¢â€“Â Ã¢â€“Â 

                // Ã¦Â²Â»Ã§â„¢â€™Ã¦â‚¬Â§Ã©Â­â€�Ã¦Â³â€¢Ã¦â€�Â»Ã¦â€œÅ Ã¤Â¸ï¿½Ã¦Â­Â»Ã¤Â¿â€šÃ§Å¡â€žÃ¦â‚¬ÂªÃ§â€°Â©Ã£â‚¬â€š
                if ((_skill.getType() == L1Skills.TYPE_HEAL) && (_calcType == PC_NPC) && (undeadType == 1)) {
                    dmg *= -1;
                }
                // Ã¦Â²Â»Ã§â„¢â€™Ã¦â‚¬Â§Ã©Â­â€�Ã¦Â³â€¢Ã§â€žÂ¡Ã¦Â³â€¢Ã¥Â°ï¿½Ã¦Â­Â¤Ã¤Â¸ï¿½Ã¦Â­Â»Ã¤Â¿â€šÃ¨ÂµÂ·Ã¤Â½Å“Ã§â€�Â¨
                if ((_skill.getType() == L1Skills.TYPE_HEAL) && (_calcType == PC_NPC) && (undeadType == 3)) {
                    dmg = 0;
                }
                // Ã§â€žÂ¡Ã¦Â³â€¢Ã¥Â°ï¿½Ã¥Å¸Å½Ã©â€“â‚¬Ã£â‚¬ï¿½Ã¥Â®Ë†Ã¨Â­Â·Ã¥Â¡â€�Ã¨Â£Å“Ã¨Â¡â‚¬
                if (((cha instanceof L1TowerInstance) || (cha instanceof L1DoorInstance)) && (dmg < 0)) {
                    dmg = 0;
                }
                // Ã¥ï¿½Â¸Ã¥ï¿½â€“Ã©Â­â€�Ã¥Å â€ºÃ£â‚¬â€š
                if ((dmg > 0) || (drainMana != 0)) {
                    _magic.commit(dmg, drainMana);
                }
                // Ã¨Â£Å“Ã¨Â¡â‚¬Ã¥Ë†Â¤Ã¦â€“Â·
                if ((_skill.getType() == L1Skills.TYPE_HEAL) && (dmg < 0)) {
                    cha.setCurrentHp((dmg * -1) + cha.getCurrentHp());
                }
                // Ã©ï¿½Å¾Ã¦Â²Â»Ã§â„¢â€™Ã¦â‚¬Â§Ã©Â­â€�Ã¦Â³â€¢Ã¨Â£Å“Ã¨Â¡â‚¬Ã¥Ë†Â¤Ã¦â€“Â·(Ã¥Â¯â€™Ã¦Ë†Â°Ã£â‚¬ï¿½Ã¥ï¿½Â¸Ã¥ï¿½Â»Ã§Â­â€°)
                if (heal > 0) {
                    _user.setCurrentHp(heal + _user.getCurrentHp());
                }

                if (cha instanceof L1PcInstance) { // Ã¦â€ºÂ´Ã¦â€“Â°Ã¨â€¡ÂªÃ¨ÂºÂ«Ã§â€¹â‚¬Ã¦â€¦â€¹
                    L1PcInstance pc = (L1PcInstance) cha;
                    pc.turnOnOffLight();
                    pc.sendPackets(new S_OwnCharAttrDef(pc));
                    pc.sendPackets(new S_OwnCharStatus(pc));
                    sendHappenMessage(pc); // Ã£â€šÂ¿Ã£Æ’Â¼Ã£â€šÂ²Ã£Æ’Æ’Ã£Æ’Ë†Ã£ï¿½Â«Ã£Æ’Â¡Ã£Æ’Æ’Ã£â€šÂ»Ã£Æ’Â¼Ã£â€šÂ¸Ã£â€šâ€™Ã©â‚¬ï¿½Ã¤Â¿Â¡
                }

                addMagicList(cha, false); // Ã£â€šÂ¿Ã£Æ’Â¼Ã£â€šÂ²Ã£Æ’Æ’Ã£Æ’Ë†Ã£ï¿½Â«Ã©Â­â€�Ã¦Â³â€¢Ã£ï¿½Â®Ã¥Å Â¹Ã¦Å¾Å“Ã¦â„¢â€šÃ©â€“â€œÃ£â€šâ€™Ã¨Â¨Â­Ã¥Â®Å¡

                if (cha instanceof L1PcInstance) { // Ã£â€šÂ¿Ã£Æ’Â¼Ã£â€šÂ²Ã£Æ’Æ’Ã£Æ’Ë†Ã£ï¿½Å’PCÃ£ï¿½ÂªÃ£â€šâ€°Ã£ï¿½Â°Ã£â‚¬ï¿½Ã£Æ’Â©Ã£â€šÂ¤Ã£Æ’Ë†Ã§Å Â¶Ã¦â€¦â€¹Ã£â€šâ€™Ã¦â€ºÂ´Ã¦â€“Â°
                    L1PcInstance pc = (L1PcInstance) cha;
                    pc.turnOnOffLight();
                }
            }

            // Ã¨Â§Â£Ã©â„¢Â¤Ã©Å¡Â±Ã¨ÂºÂ«
            if ((_skillId == DETECTION) || (_skillId == COUNTER_DETECTION)) { // Ã§â€žÂ¡Ã¦â€°â‚¬Ã©ï¿½ï¿½Ã¥Â½Â¢Ã£â‚¬ï¿½Ã¥Â¼Â·Ã¥Å â€ºÃ§â€žÂ¡Ã¦â€°â‚¬Ã©ï¿½ï¿½Ã¥Â½Â¢
                detection(_player);
            }

        }
        catch (Exception e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        }
    }

    private void detection(L1PcInstance pc) {
        if (!pc.isGmInvis() && pc.isInvisble()) { // Ã¨â€¡ÂªÃ¥Â·Â±Ã©Å¡Â±Ã¨ÂºÂ«Ã¤Â¸Â­
            pc.delInvis();
            pc.beginInvisTimer();
        }

        for (L1PcInstance tgt : L1World.getInstance().getVisiblePlayer(pc)) { // Ã§â€¢Â«Ã©ï¿½Â¢Ã¥â€¦Â§Ã¥â€¦Â¶Ã¤Â»â€“Ã©Å¡Â±Ã¨ÂºÂ«Ã¨â‚¬â€¦
            if (!tgt.isGmInvis() && tgt.isInvisble()) {
                tgt.delInvis();
            }
        }
        L1WorldTraps.getInstance().onDetection(pc);
    }

    // Ã£â€šÂ¿Ã£Æ’Â¼Ã£â€šÂ²Ã£Æ’Æ’Ã£Æ’Ë†Ã£ï¿½Â«Ã£ï¿½Â¤Ã£ï¿½â€žÃ£ï¿½Â¦Ã¨Â¨Ë†Ã§Â®â€”Ã£ï¿½â„¢Ã£â€šâ€¹Ã¥Â¿â€¦Ã¨Â¦ï¿½Ã£ï¿½Å’Ã£ï¿½â€šÃ£â€šâ€¹Ã£ï¿½â€¹Ã¨Â¿â€�Ã£ï¿½â„¢
    private boolean isTargetCalc(L1Character cha) {
        // Ã¤Â¸â€°Ã©â€¡ï¿½Ã§Å¸Â¢Ã£â‚¬ï¿½Ã¥Â±Â Ã¥Â®Â°Ã¨â‚¬â€¦Ã£â‚¬ï¿½Ã¦Å¡Â´Ã¦â€œÅ Ã£â‚¬ï¿½Ã©ÂªÂ·Ã©Â«ï¿½Ã¦Â¯â‚¬Ã¥Â£Å¾
        if ((_user instanceof L1PcInstance)
                && (_skillId == TRIPLE_ARROW || _skillId == FOE_SLAYER
                || _skillId == SMASH || _skillId == BONE_BREAK)) {
            return true;
        }
        // Ã¦â€�Â»Ã¦â€™Æ’Ã©Â­â€�Ã¦Â³â€¢Ã£ï¿½Â®NonÃ¯Â¼ï¿½PvPÃ¥Ë†Â¤Ã¥Â®Å¡
        if (_skill.getTarget().equals("attack") && (_skillId != 18)) { // Ã¦â€�Â»Ã¦â€™Æ’Ã©Â­â€�Ã¦Â³â€¢
            if (isPcSummonPet(cha)) { // Ã¥Â¯Â¾Ã¨Â±Â¡Ã£ï¿½Å’PCÃ£â‚¬ï¿½Ã£â€šÂµÃ£Æ’Â¢Ã£Æ’Â³Ã£â‚¬ï¿½Ã£Æ’Å¡Ã£Æ’Æ’Ã£Æ’Ë†
                if ((_player.getZoneType() == 1) || (cha.getZoneType() == 1 // Ã¦â€�Â»Ã¦â€™Æ’Ã£ï¿½â„¢Ã£â€šâ€¹Ã¥ï¿½Â´Ã£ï¿½Â¾Ã£ï¿½Å¸Ã£ï¿½Â¯Ã¦â€�Â»Ã¦â€™Æ’Ã£ï¿½â€¢Ã£â€šÅ’Ã£â€šâ€¹Ã¥ï¿½Â´Ã£ï¿½Å’Ã£â€šÂ»Ã£Æ’Â¼Ã£Æ’â€¢Ã£Æ’â€ Ã£â€šÂ£Ã£Æ’Â¼Ã£â€šÂ¾Ã£Æ’Â¼Ã£Æ’Â³
                ) || _player.checkNonPvP(_player, cha)) { // Non-PvPÃ¨Â¨Â­Ã¥Â®Å¡
                    return false;
                }
            }
        }

        // Ã£Æ’â€¢Ã£â€šÂ©Ã£â€šÂ°Ã£â€šÂªÃ£Æ’â€“Ã£â€šÂ¹Ã£Æ’ÂªÃ£Æ’Â¼Ã£Æ’â€�Ã£Æ’Â³Ã£â€šÂ°Ã£ï¿½Â¯Ã¨â€¡ÂªÃ¥Ë†â€ Ã¨â€¡ÂªÃ¨ÂºÂ«Ã£ï¿½Â¯Ã¥Â¯Â¾Ã¨Â±Â¡Ã¥Â¤â€“
        if ((_skillId == FOG_OF_SLEEPING) && (_user.getId() == cha.getId())) {
            return false;
        }

        // Ã£Æ’Å¾Ã£â€šÂ¹Ã£â€šÂ¹Ã£Æ’Â­Ã£Æ’Â¼Ã£ï¿½Â¯Ã¨â€¡ÂªÃ¥Ë†â€ Ã¨â€¡ÂªÃ¨ÂºÂ«Ã£ï¿½Â¨Ã¨â€¡ÂªÃ¥Ë†â€ Ã£ï¿½Â®Ã£Æ’Å¡Ã£Æ’Æ’Ã£Æ’Ë†Ã£ï¿½Â¯Ã¥Â¯Â¾Ã¨Â±Â¡Ã¥Â¤â€“
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

        // Ã£Æ’Å¾Ã£â€šÂ¹Ã£Æ’â€ Ã£Æ’Â¬Ã£Æ’ï¿½Ã£Æ’Â¼Ã£Æ’Ë†Ã£ï¿½Â¯Ã¨â€¡ÂªÃ¥Ë†â€ Ã¨â€¡ÂªÃ¨ÂºÂ«Ã£ï¿½Â®Ã£ï¿½Â¿Ã¥Â¯Â¾Ã¨Â±Â¡Ã¯Â¼Ë†Ã¥ï¿½Å’Ã¦â„¢â€šÃ£ï¿½Â«Ã£â€šÂ¯Ã£Æ’Â©Ã£Æ’Â³Ã¥â€œÂ¡Ã£â€šâ€šÃ£Æ’â€ Ã£Æ’Â¬Ã£Æ’ï¿½Ã£Æ’Â¼Ã£Æ’Ë†Ã£ï¿½â€¢Ã£ï¿½â€ºÃ£â€šâ€¹Ã¯Â¼â€°
        if (_skillId == MASS_TELEPORT) {
            if (_user.getId() != cha.getId()) {
                return false;
            }
        }

        return true;
    }

    // Ã¥Â¯Â¾Ã¨Â±Â¡Ã£ï¿½Å’PCÃ£â‚¬ï¿½Ã£â€šÂµÃ£Æ’Â¢Ã£Æ’Â³Ã£â‚¬ï¿½Ã£Æ’Å¡Ã£Æ’Æ’Ã£Æ’Ë†Ã£ï¿½â€¹Ã£â€šâ€™Ã¨Â¿â€�Ã£ï¿½â„¢
    private boolean isPcSummonPet(L1Character cha) {
        if (_calcType == PC_PC) { // Ã¥Â¯Â¾Ã¨Â±Â¡Ã£ï¿½Å’PC
            return true;
        }

        if (_calcType == PC_NPC) {
            if (cha instanceof L1SummonInstance) { // Ã¥Â¯Â¾Ã¨Â±Â¡Ã£ï¿½Å’Ã£â€šÂµÃ£Æ’Â¢Ã£Æ’Â³
                L1SummonInstance summon = (L1SummonInstance) cha;
                if (summon.isExsistMaster()) { // Ã£Æ’Å¾Ã£â€šÂ¹Ã£â€šÂ¿Ã£Æ’Â¼Ã£ï¿½Å’Ã¥Â±â€¦Ã£â€šâ€¹
                    return true;
                }
            }
            if (cha instanceof L1PetInstance) { // Ã¥Â¯Â¾Ã¨Â±Â¡Ã£ï¿½Å’Ã£Æ’Å¡Ã£Æ’Æ’Ã£Æ’Ë†
                return true;
            }
        }
        return false;
    }

    // Ã£â€šÂ¿Ã£Æ’Â¼Ã£â€šÂ²Ã£Æ’Æ’Ã£Æ’Ë†Ã£ï¿½Â«Ã¥Â¯Â¾Ã£ï¿½â€”Ã£ï¿½Â¦Ã¥Â¿â€¦Ã£ï¿½Å¡Ã¥Â¤Â±Ã¦â€¢â€”Ã£ï¿½Â«Ã£ï¿½ÂªÃ£â€šâ€¹Ã£ï¿½â€¹Ã¨Â¿â€�Ã£ï¿½â„¢
    private boolean isTargetFailure(L1Character cha) {
        boolean isTU = false;
        boolean isErase = false;
        boolean isManaDrain = false;
        int undeadType = 0;

        if ((cha instanceof L1TowerInstance) || (cha instanceof L1DoorInstance)) { // Ã£â€šÂ¬Ã£Æ’Â¼Ã£Æ’â€¡Ã£â€šÂ£Ã£â€šÂ¢Ã£Æ’Â³Ã£â€šÂ¿Ã£Æ’Â¯Ã£Æ’Â¼Ã£â‚¬ï¿½Ã£Æ’â€°Ã£â€šÂ¢Ã£ï¿½Â«Ã£ï¿½Â¯Ã§Â¢ÂºÃ§Å½â€¡Ã§Â³Â»Ã£â€šÂ¹Ã£â€šÂ­Ã£Æ’Â«Ã§â€žÂ¡Ã¥Å Â¹
            return true;
        }

        if (cha instanceof L1PcInstance) { // Ã¥Â¯Â¾PCÃ£ï¿½Â®Ã¥Â Â´Ã¥ï¿½Ë†
            if ((_calcType == PC_PC) && _player.checkNonPvP(_player, cha)) { // Non-PvPÃ¨Â¨Â­Ã¥Â®Å¡
                L1PcInstance pc = (L1PcInstance) cha;
                if ((_player.getId() == pc.getId()) || ((pc.getClanid() != 0) && (_player.getClanid() == pc.getClanid()))) {
                    return false;
                }
                return true;
            }
            return false;
        }

        if (cha instanceof L1MonsterInstance) { // Ã£â€šÂ¿Ã£Æ’Â¼Ã£Æ’Â³Ã£â€šÂ¢Ã£Æ’Â³Ã£Æ’â€¡Ã£Æ’Æ’Ã£Æ’Ë†Ã¥ï¿½Â¯Ã¨Æ’Â½Ã£ï¿½â€¹Ã¥Ë†Â¤Ã¥Â®Å¡
            isTU = ((L1MonsterInstance) cha).getNpcTemplate().get_IsTU();
        }

        if (cha instanceof L1MonsterInstance) { // Ã£â€šÂ¤Ã£Æ’Â¬Ã£Æ’Â¼Ã£â€šÂ¹Ã£Æ’Å¾Ã£â€šÂ¸Ã£Æ’Æ’Ã£â€šÂ¯Ã¥ï¿½Â¯Ã¨Æ’Â½Ã£ï¿½â€¹Ã¥Ë†Â¤Ã¥Â®Å¡
            isErase = ((L1MonsterInstance) cha).getNpcTemplate().get_IsErase();
        }

        if (cha instanceof L1MonsterInstance) { // Ã£â€šÂ¢Ã£Æ’Â³Ã£Æ’â€¡Ã£Æ’Æ’Ã£Æ’Ë†Ã£ï¿½Â®Ã¥Ë†Â¤Ã¥Â®Å¡
            undeadType = ((L1MonsterInstance) cha).getNpcTemplate().get_undead();
        }

        // Ã£Æ’Å¾Ã£Æ’Å Ã£Æ’â€°Ã£Æ’Â¬Ã£â€šÂ¤Ã£Æ’Â³Ã£ï¿½Å’Ã¥ï¿½Â¯Ã¨Æ’Â½Ã£ï¿½â€¹Ã¯Â¼Å¸
        if (cha instanceof L1MonsterInstance) {
            isManaDrain = true;
        }
                /*
                 * Ã¦Ë†ï¿½Ã¥Å Å¸Ã©â„¢Â¤Ã¥Â¤â€“Ã¦ï¿½Â¡Ã¤Â»Â¶Ã¯Â¼â€˜Ã¯Â¼Å¡T-UÃ£ï¿½Å’Ã¦Ë†ï¿½Ã¥Å Å¸Ã£ï¿½â€”Ã£ï¿½Å¸Ã£ï¿½Å’Ã£â‚¬ï¿½Ã¥Â¯Â¾Ã¨Â±Â¡Ã£ï¿½Å’Ã£â€šÂ¢Ã£Æ’Â³Ã£Æ’â€¡Ã£Æ’Æ’Ã£Æ’Ë†Ã£ï¿½Â§Ã£ï¿½Â¯Ã£ï¿½ÂªÃ£ï¿½â€žÃ£â‚¬â€š Ã¦Ë†ï¿½Ã¥Å Å¸Ã©â„¢Â¤Ã¥Â¤â€“Ã¦ï¿½Â¡Ã¤Â»Â¶Ã¯Â¼â€™Ã¯Â¼Å¡T-UÃ£ï¿½Å’Ã¦Ë†ï¿½Ã¥Å Å¸Ã£ï¿½â€”Ã£ï¿½Å¸Ã£ï¿½Å’Ã£â‚¬ï¿½Ã¥Â¯Â¾Ã¨Â±Â¡Ã£ï¿½Â«Ã£ï¿½Â¯Ã£â€šÂ¿Ã£Æ’Â¼Ã£Æ’Â³Ã£â€šÂ¢Ã£Æ’Â³Ã£Æ’â€¡Ã£Æ’Æ’Ã£Æ’Ë†Ã§â€žÂ¡Ã¥Å Â¹Ã£â‚¬â€š
                 * Ã¦Ë†ï¿½Ã¥Å Å¸Ã©â„¢Â¤Ã¥Â¤â€“Ã¦ï¿½Â¡Ã¤Â»Â¶Ã¯Â¼â€œÃ¯Â¼Å¡Ã£â€šÂ¹Ã£Æ’Â­Ã£Æ’Â¼Ã£â‚¬ï¿½Ã£Æ’Å¾Ã£â€šÂ¹Ã£â€šÂ¹Ã£Æ’Â­Ã£Æ’Â¼Ã£â‚¬ï¿½Ã£Æ’Å¾Ã£Æ’Å Ã£Æ’â€°Ã£Æ’Â¬Ã£â€šÂ¤Ã£Æ’Â³Ã£â‚¬ï¿½Ã£â€šÂ¨Ã£Æ’Â³Ã£â€šÂ¿Ã£Æ’Â³Ã£â€šÂ°Ã£Æ’Â«Ã£â‚¬ï¿½Ã£â€šÂ¤Ã£Æ’Â¬Ã£Æ’Â¼Ã£â€šÂ¹Ã£Æ’Å¾Ã£â€šÂ¸Ã£Æ’Æ’Ã£â€šÂ¯Ã£â‚¬ï¿½Ã£â€šÂ¦Ã£â€šÂ£Ã£Æ’Â³Ã£Æ’â€°Ã£â€šÂ·Ã£Æ’Â£Ã£Æ’Æ’Ã£â€šÂ¯Ã£Æ’Â«Ã§â€žÂ¡Ã¥Å Â¹
                 * Ã¦Ë†ï¿½Ã¥Å Å¸Ã©â„¢Â¤Ã¥Â¤â€“Ã¦ï¿½Â¡Ã¤Â»Â¶Ã¯Â¼â€�Ã¯Â¼Å¡Ã£Æ’Å¾Ã£Æ’Å Ã£Æ’â€°Ã£Æ’Â¬Ã£â€šÂ¤Ã£Æ’Â³Ã£ï¿½Å’Ã¦Ë†ï¿½Ã¥Å Å¸Ã£ï¿½â€”Ã£ï¿½Å¸Ã£ï¿½Å’Ã£â‚¬ï¿½Ã£Æ’Â¢Ã£Æ’Â³Ã£â€šÂ¹Ã£â€šÂ¿Ã£Æ’Â¼Ã¤Â»Â¥Ã¥Â¤â€“Ã£ï¿½Â®Ã¥Â Â´Ã¥ï¿½Ë†
                 */
        if (((_skillId == TURN_UNDEAD) && ((undeadType == 0) || (undeadType == 2)))
                || ((_skillId == TURN_UNDEAD) && (isTU == false))
                || (((_skillId == ERASE_MAGIC) || (_skillId == SLOW) || (_skillId == MANA_DRAIN) || (_skillId == MASS_SLOW) || (_skillId == ENTANGLE) || (_skillId == WIND_SHACKLE)) && (isErase == false))
                || ((_skillId == MANA_DRAIN) && (isManaDrain == false))) {
            return true;
        }
        return false;
    }

    // Ã©Â­â€�Ã¦Â³â€¢Ã¥Â±ï¿½Ã©Å¡Å“Ã§â„¢Â¼Ã¥â€¹â€¢Ã¥Ë†Â¤Ã¦â€“Â·
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