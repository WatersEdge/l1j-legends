package l1j.server.server.model.skill;

import static l1j.server.server.model.skill.L1SkillName.*;
import static l1j.server.server.model.skill.L1SkillId.*;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


import l1j.server.server.ActionCodes;
import l1j.server.server.command.executor.L1Summon;
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
import l1j.server.server.templates.L1Npc;
import l1j.server.server.templates.L1Skills;
import l1j.server.server.utils.Random;
import l1j.server.server.utils.collections.IntArrays;
import l1j.server.server.utils.collections.Lists;
import static l1j.server.server.model.item.L1ItemId.*;
import l1j.server.server.random.RandomGenerator;
import l1j.server.server.random.RandomGeneratorFactory;


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
    private int _holdDuration;
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
    private boolean _checkedUseSkill = false;
    private int _leverage = 10;
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
/*
    private static final int[] CAST_WITH_INVIS ={
        Skill_WaterLife,Skill_ElementalFire,Skill_ExoticVitalize,Skill_IronSkin,Skill_StormShot,Skill_NaturesMiracle,Skill_NaturesBlessing,Skill_LesserHeal,
        Skill_BurningWeapon,Skill_BlessOfEarth,Skill_NaturesTouch,Skill_EyeofStorm,Skill_BlessOfFire,Skill_EarthSkin,Skill_WindWalk,
        Skill_WindShot,Skill_FireWeapon,Skill_ProtectionFromElemental,Skill_BloodtoSoul,Skill_ResistElemental,Skill_ClearMind,
        Skill_CounterMirror,Skill_ElementalFallDown,Skill_TeleportToMotherTree,Skill_BodytoMind,Skill_ResistMagic,Skill_Teleport_to_Pledge_Member,
        Skill_Brave_Aura,Skill_CallPledgeMember,Skill_Shining_Aura,Skill_Glowing_Aura,Skill_TrueTarget,Skill_DressEvasion,Skill_DressDex,
        Skill_DressMighty,Skill_ShadowFang,Skill_UncannyDodge,Skill_DoubleBreak,Skill_VenomResist,Skill_BurningSpirit,Skill_MovingAcceleration,
        Skill_PurifyStone,Skill_ShadowArmor,Skill_EnchantVenom,Skill_BlindHiding,Skill_AdvanceSpirit,Skill_AbsoluteBarrier,Skill_GreaterResurrection,
        Skill_CreateMagicalWeapon,Skill_CounterDetection,Skill_MassTeleport, Skill_Soul_of_Flame, Skill_Additional_Fire, Skill_ReductionArmor,
        Skill_BounceAttack, Skill_SolidCarriage, Skill_CounterBarrier, Skill_DragonSkin, Skill_AwakenAntharas, Skill_AwakenFafurion, Skill_AwakenValakas,
        Skill_MirrorImage, Skill_IllusionOgre, Skill_IllusionLich, Skill_Patience, Skill_IllusionDiaGolem, Skill_Insight, Skill_IllusionAvatar };


    private static final int[] EXCEPT_Skill_CounterMagic = {
        Skill_WaterLife,Skill_ElementalFire,Skill_ExoticVitalize,Skill_IronSkin,Skill_StormShot,Skill_NaturesMiracle,Skill_NaturesBlessing,Skill_BurningWeapon,Skill_LesserHeal,
        Skill_AreaOfSilence,Skill_BlessOfEarth,Skill_NaturesTouch,Skill_EyeofStorm,Skill_BlessOfFire,Skill_EarthSkin,Skill_WindWalk,Skill_WindShot,Skill_FireWeapon,
        Skill_ProtectionFromElemental,Skill_BloodtoSoul,Skill_ResistElemental,Skill_ClearMind,Skill_CounterMirror,Skill_TripleShot,Skill_TeleportToMotherTree,
        Skill_BodytoMind,Skill_ResistMagic,Skill_Teleport_to_Pledge_Member,Skill_Brave_Aura,Skill_CallPledgeMember,Skill_Shining_Aura,Skill_Glowing_Aura,
        Skill_TrueTarget,Skill_DressEvasion,Skill_DressDex,Skill_DressMighty,Skill_ShadowFang,Skill_UncannyDodge,Skill_DoubleBreak,Skill_VenomResist,Skill_BurningSpirit,
        Skill_MovingAcceleration,Skill_PurifyStone,Skill_ShadowArmor,Skill_EnchantVenom,Skill_BlindHiding,Skill_AdvanceSpirit,Skill_AbsoluteBarrier,Skill_GreaterResurrection,
        Skill_CreateMagicalWeapon,Skill_CounterDetection,Skill_MassTeleport,Skill_ImmuneToHarm,Skill_Pollute_Water,Skill_ShockStun, Skill_ReductionArmor,
        Skill_BounceAttack, Skill_SolidCarriage, Skill_CounterBarrier,Skill_Soul_of_Flame, Skill_Additional_Fire, Skill_DragonSkin, Skill_AwakenAntharas, Skill_AwakenFafurion,
        Skill_AwakenValakas,Skill_MirrorImage, Skill_IllusionOgre, Skill_IllusionLich, Skill_Patience, Skill_IllusionDiaGolem, Skill_Insight, Skill_IllusionAvatar,Skill_FoeSlayer};

    private static final int [] CAST_WITH_Skill_Silence = {
        Skill_ShockStun, Skill_ReductionArmor, Skill_BounceAttack, Skill_SolidCarriage, Skill_CounterBarrier };
*/

    private static final int[] CAST_WITH_INVIS = { 1, 2, 3, 5, 8, 9, 12, 13,
            14, 19, 21, 26, 31, 32, 35, 37, 42, 43, 44, 48, 49, 52, 54, 55, 57,
            60, 61, 63, 67, 68, 69, 72, 73, 75, 78, 79, REDUCTION_ARMOR,
            BOUNCE_ATTACK, SOLID_CARRIAGE, COUNTER_BARRIER, 97, 98, 99, 100,
            101, 102, 104, 105, 106, 107, 109, 110, 111, 113, 114, 115, 116,
            117, 118, 129, 130, 131, 133, 134, 137, 138, 146, 147, 148, 149,
            150, 151, 155, 156, 158, 159, 163, 164, 165, 166, 168, 169, 170,
            171, SOUL_OF_FLAME, ADDITIONAL_FIRE, DRAGON_SKIN, AWAKEN_ANTHARAS,
            AWAKEN_FAFURION, AWAKEN_VALAKAS, MIRROR_IMAGE, ILLUSION_OGRE,
            ILLUSION_LICH, PATIENCE, ILLUSION_DIA_GOLEM, INSIGHT,
            ILLUSION_AVATAR };

    private static final int[] EXCEPT_COUNTER_MAGIC = { 1, 2, 3, 5, 8, 9, 12,
            13, 14, 19, 21, 26, 31, 32, 35, 37, 42, 43, 44, 48, 49, 52, 54, 55,
            57, 60, 61, 63, 67, 68, 69, 72, 73, 75, 78, 79, SHOCK_STUN,
            REDUCTION_ARMOR, BOUNCE_ATTACK, SOLID_CARRIAGE, COUNTER_BARRIER,
            97, 98, 99, 100, 101, 102, 104, 105, 106, 107, 109, 110, 111, 113,
            114, 115, 116, 117, 118, 129, 130, 131, 132, 134, 137, 138, 146,
            147, 148, 149, 150, 151, 155, 156, 158, 159, 161, 163, 164, 165,
            166, 168, 169, 170, 171, SOUL_OF_FLAME, ADDITIONAL_FIRE,
            DRAGON_SKIN, FOE_SLAYER,
            AWAKEN_ANTHARAS, AWAKEN_FAFURION, AWAKEN_VALAKAS,
            MIRROR_IMAGE, ILLUSION_OGRE, ILLUSION_LICH, PATIENCE, 10026, 10027,
            ILLUSION_DIA_GOLEM, INSIGHT, ILLUSION_AVATAR, 10028, 10029 };

    private static final int [] CAST_WITH_Skill_Silence =
            {
                    Skill_ShockStun, Skill_ReductionArmor, Skill_BounceAttack, Skill_SolidCarriage, Skill_CounterBarrier
            };

    static {
        Arrays.sort(CAST_WITH_Skill_Silence);
    }

    static {
        Arrays.sort(CAST_WITH_INVIS);
    }

    public L1SkillUse() {
    }

    private static class TargetStatus {
        private L1Character _target = null;

        private boolean _isCalc = true;

        public TargetStatus(L1Character _cha) {
            _target = _cha;
        }

        public TargetStatus(L1Character _cha, boolean _flg) {
            _target = _cha;
            _isCalc = _flg;
        }

        public L1Character getTarget() {
            return _target;
        }

        public boolean isCalc() {
            return _isCalc;
        }
    }


    public void setSkillRanged(int i) {
        _skillRanged = i;
    }

    public int getSkillRanged() {
        if (_skillRanged == 0) {
            return _skill.getRanged();
        }
        return _skillRanged;
    }

    public int getSkillArea() {
        if (_skillArea == 0) {
            return _skill.getArea();
        }
        return _skillArea;
    }


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

    public boolean checkUseSkill(L1PcInstance player, int skillid, int target_id, int x, int y, String message, int time, int type,L1Character attacker) {
        return checkUseSkill(player, skillid, target_id, x, y, message, time, type, attacker, 0, 0, 0);
    }

    public boolean checkUseSkill(L1PcInstance player, int skillid, int target_id, int x, int y, String message, int time, int type,L1Character attacker, int actid, int gfxid, int mpConsume) {
        setCheckedUseSkill(true);
        _targetList = Lists.newList();

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
            _player = player;
            _user = _player;
        }
        else {
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

        if (type == TYPE_NORMAL) {
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


        if ((_skillId == Skill_Firewall) || (_skillId == Skill_LifeStream) || (_skillId == Skill_TrueTarget)) {
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

        if ((_skillId == Skill_Teleport) || (_skillId == Skill_MassTeleport)) {
            _bookmarkId = target_id;
        }
        if ((_skillId == Skill_CreateMagicalWeapon) || (_skillId == Skill_PurifyStone) || (_skillId == Skill_EnchantArmor) || (_skillId == Skill_EnchantWeapon) || (_skillId == Skill_ShadowFang)) {
            _itemobjid = target_id;
        }
        _target = (L1Character) l1object;

        if (!(_target instanceof L1MonsterInstance) && _skill.getTarget().equals("attack") && (_user.getId() != target_id)) {
            _isPK = true;
        }


        if (!(l1object instanceof L1Character)) {
            checkedResult = false;
        }
        makeTargetList();
        if (_targetList.isEmpty() && (_user instanceof L1NpcInstance)) {
            checkedResult = false;
        }
        return checkedResult;
    }


    private boolean isNormalSkillUsable() {
        if (_user instanceof L1PcInstance) {
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
            if (pc.getInventory().getWeight242() >= 197) {
                pc.sendPackets(new S_ServerMessage(316));
                return false;
            }
            int polyId = pc.getTempCharGfx();
            L1PolyMorph poly = PolyTable.getInstance().getTemplate(polyId);
            if ((poly != null) && !poly.canUseSkill()) {
                pc.sendPackets(new S_ServerMessage(285));
                return false;
            }

            if (!isAttrAgrees()) {
                return false;
            }

            if ((_skillId == Skill_ProtectionFromElemental) && (pc.getElfAttr() == 0)) {
                pc.sendPackets(new S_ServerMessage(280));
                return false;
            }

            if (pc.isSkillDelay()) {
                return false;
            }

            // removing cast with invis check - [Hank]
            if ((pc.hasSkillEffect(Skill_Silence) ||
                    pc.hasSkillEffect(Skill_AreaOfSilence) ||
                    pc.hasSkillEffect(L1SkillId.STATUS_POISON_SILENCE)||
                    pc.hasSkillEffect(L1SkillId.CONFUSION_ING))) {
                pc.sendPackets(new S_ServerMessage(285));
                return false;
            }


            if ((_skillId == Skill_Destroy) && (pc.getLawful() < 500)) {
                pc.sendPackets(new S_ServerMessage(352, "$967"));
                return false;
            }

            if ((_skillId == Skill_CubeIgnition) || (_skillId == Skill_CubeQuake) || (_skillId == Skill_CubeShock) || (_skillId == Skill_CubeBalance)) {
                boolean isNearSameCube = false;
                int gfxId = 0;
                for (L1Object obj : L1World.getInstance().getVisibleObjects(pc, 3)) {
                    if (obj instanceof L1EffectInstance) {
                        L1EffectInstance effect = (L1EffectInstance) obj;
                        gfxId = effect.getGfxId();
                        if (((_skillId == Skill_CubeIgnition) && (gfxId == 6706)) || ((_skillId == Skill_CubeQuake) && (gfxId == 6712))
                                || ((_skillId == Skill_CubeShock) && (gfxId == 6718)) || ((_skillId == Skill_CubeBalance) && (gfxId == 6724))) {
                            isNearSameCube = true;
                            break;
                        }
                    }
                }
                if (isNearSameCube) {
                    pc.sendPackets(new S_ServerMessage(1412));
                    return false;
                }
            }

            if (_skillId == Skill_SolidCarriage) {
                L1PcInventory Inventory = pc.getInventory();
                if (Inventory.getItemEquipped(2, 7) == null) {
                    pc.sendPackets(new S_SystemMessage(
                            "Solid Carriage requires a Shield on to use."));
                    return false;
                }

            }

            if (_skillId == Skill_BoneBreak && pc.getWeapon() == null) {
                pc.sendPackets(new S_SystemMessage(
                        "Bonebreak requires a Weapon on to use."));
                return false;
            }

            if (_skillId == Skill_ThunderGrab && pc.getWeapon() == null) {
                pc.sendPackets(new S_SystemMessage(
                        "ThunderGrab requires a Weapon on to use."));
                return false;
            }

            if (_skillId == Skill_Confusion && pc.getWeapon() == null) {
                pc.sendPackets(new S_SystemMessage(
                        "Confusion requires a Weapon on to use."));
                return false;
            }

            if (_skillId == Skill_Smash && pc.getWeapon() == null) {
                pc.sendPackets(new S_SystemMessage(
                        "Smash requires a Weapon on to use."));
                return false;
            }

            if (_skillId == Skill_ArmBreaker && pc.getWeapon() == null) {
                pc.sendPackets(new S_SystemMessage(
                        "Arm Breaker requires a Weapon on to use."));
                return false;
            }

            if ((isItemConsume() == false) && !_player.isGm()) {
                _player.sendPackets(new S_ServerMessage(299));
                return false;
            }
        }
        else if (_user instanceof L1NpcInstance) {

            if (_user.hasSkillEffect(Skill_Silence)) {
                _user.removeSkillEffect(Skill_Silence);
                return false;
            }
        }

        if (!isHPMPConsume()) {
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

            if (type == TYPE_NORMAL) {
                if (!_isGlanceCheckFail || (getSkillArea() > 0) || _skill.getTarget().equals("none")) {
                    if ( _skill.getSkillId()== Skill_HolyWalk && _target.hasSkillEffect(Skill_HolyWalk)) {
                        _skillTime = 300 +
                                _target.getSkillEffectTimeSec(Skill_HolyWalk);
                        _skillTime = Math.min(_skillTime, 1800);
                    }

                    runSkill();
                    useConsume();
                    sendGrfx(true);
                    sendFailMessageHandle();
                    setDelay();
                }
            }
            else if (type == TYPE_LOGIN) {
                runSkill();
            }
            else if (type == TYPE_SPELLSC) {
                runSkill();
                sendGrfx(true);
            }
            else if (type == TYPE_GMBUFF) {
                runSkill();
                sendGrfx(false);
            }
            else if (type == TYPE_NPCBUFF) {
                runSkill();
                sendGrfx(true);
            }
            setCheckedUseSkill(false);
        }
        catch (Exception e) {
            _log.log(Level.SEVERE, "", e.getMessage());
        }
    }


    private void failSkill() {
        setCheckedUseSkill(false);
        if ((_skillId == Skill_Teleport) || (_skillId == Skill_MassTeleport) || (_skillId == Skill_TeleportToMotherTree)) {
            _player.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
        }
    }

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

        if (cha instanceof L1DoorInstance) {
            if ((cha.getMaxHp() == 0) || (cha.getMaxHp() == 1)) {
                return false;
            }
        }

        if ((cha instanceof L1DollInstance) && (_skillId != Skill_Haste)) {
            return false;
        }

        if ((_calcType == PC_NPC) && (_target instanceof L1NpcInstance) && !(_target instanceof L1PetInstance)
                && !(_target instanceof L1SummonInstance)
                && ((cha instanceof L1PetInstance) || (cha instanceof L1SummonInstance) || (cha instanceof L1PcInstance))) {
            return false;
        }

        if ((_calcType == PC_NPC) && (_target instanceof L1NpcInstance) && !(_target instanceof L1GuardInstance) && (cha instanceof L1GuardInstance)) {
            return false;
        }

        if ((_skill.getTarget().equals("attack") || (_skill.getType() == L1Skills.TYPE_ATTACK)) && (_calcType == NPC_PC)
                && !(cha instanceof L1PetInstance) && !(cha instanceof L1SummonInstance) && !(cha instanceof L1PcInstance)) {
            return false;
        }

        if ((_skill.getTarget().equals("attack") || (_skill.getType() == L1Skills.TYPE_ATTACK)) && (_calcType == NPC_NPC)
                && (_user instanceof L1MonsterInstance) && (cha instanceof L1MonsterInstance)) {
            return false;
        }

        if (_skill.getTarget().equals("none")
                && (_skill.getType() == L1Skills.TYPE_ATTACK)
                && ((cha instanceof L1AuctionBoardInstance) || (cha instanceof L1BoardInstance) || (cha instanceof L1CrownInstance)
                || (cha instanceof L1DwarfInstance) || (cha instanceof L1EffectInstance) || (cha instanceof L1FieldObjectInstance)
                || (cha instanceof L1FurnitureInstance) || (cha instanceof L1HousekeeperInstance) || (cha instanceof L1MerchantInstance) || (cha instanceof L1TeleporterInstance))) {
            return false;
        }

        if ((_skill.getType() == L1Skills.TYPE_ATTACK) && (cha.getId() == _user.getId())) {
            return false;
        }

        if ((cha.getId() == _user.getId()) && (_skillId == Skill_HealPledge)) {
            return false;
        }

        if ((((_skill.getTargetTo() & L1Skills.TARGET_TO_PC) == L1Skills.TARGET_TO_PC)
                || ((_skill.getTargetTo() & L1Skills.TARGET_TO_CLAN) == L1Skills.TARGET_TO_CLAN) || ((_skill.getTargetTo() & L1Skills.TARGET_TO_PARTY) == L1Skills.TARGET_TO_PARTY))
                && (cha.getId() == _user.getId()) && (_skillId != Skill_HealPledge)) {
            return true;
        }

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
            if ((_skillId == Skill_CounterDetection) && (enemy.getZoneType() != 1)
                    && (cha.hasSkillEffect(Skill_Invisibility) || cha.hasSkillEffect(Skill_BlindHiding))) {
                return true;
            }
            if ((_skillId == Skill_CounterDetection) && (enemy.getZoneType() != 1)
                    && !(cha.hasSkillEffect(Skill_Invisibility) || cha.hasSkillEffect(Skill_BlindHiding))) {
                return false;
            }
            if ((_player.getClanid() != 0) && (enemy.getClanid() != 0)) {
                for (L1War war : L1World.getInstance().getWarList()) {
                    if (war.CheckClanInWar(_player.getClanname())) {
                        if (war.CheckClanInSameWar(
                                _player.getClanname(), enemy.getClanname())) {
                            if (L1CastleLocation.checkInAllWarArea(enemy.getX(), enemy.getY(), enemy.getMapId())) {
                                return true;
                            }
                        }
                    }
                }
            }
            return false;
        }

        if ((_user.glanceCheck(cha.getX(), cha.getY()) == false) && (_skill.isThrough() == false)) {
            if (!((_skill.getType() == L1Skills.TYPE_CHANGE) || (_skill.getType() == L1Skills.TYPE_RESTORE))) {
                _isGlanceCheckFail = true;
                return false;
            }
        }

        if (cha.hasSkillEffect(Skill_IceLance) || cha.hasSkillEffect(Skill_FreezingBlizzard) || cha.hasSkillEffect(Skill_FreezingBreath)
                || cha.hasSkillEffect(L1SkillId.ICE_LANCE_COCKATRICE) || cha.hasSkillEffect(L1SkillId.ICE_LANCE_BASILISK)) {
            if (_skillId == Skill_IceLance || _skillId == Skill_FreezingBlizzard
                    || _skillId == Skill_FreezingBreath || _skillId == L1SkillId.ICE_LANCE_BASILISK || _skillId == L1SkillId.ICE_LANCE_COCKATRICE) {
                return false;
            }
        }
        if (cha.hasSkillEffect(Skill_EarthBind) && (_skillId == Skill_EarthBind)) {
            return false;
        }

        if (!(cha instanceof L1MonsterInstance) && ((_skillId == Skill_TameMonster) || (_skillId == Skill_CreateZombie))) {
            return false;
        }
        if (cha.isDead()
                && ((_skillId != Skill_CreateZombie) && (_skillId != Skill_Resurrection) && (_skillId != Skill_GreaterResurrection) && (_skillId != Skill_ReturnToNature))) {
            return false;
        }

        if ((cha.isDead() == false)
                && ((_skillId == Skill_CreateZombie) || (_skillId == Skill_Resurrection) || (_skillId == Skill_GreaterResurrection) || (_skillId == Skill_ReturnToNature))) {
            return false;
        }

        if (((cha instanceof L1TowerInstance) || (cha instanceof L1DoorInstance))
                && ((_skillId == Skill_CreateZombie) || (_skillId == Skill_Resurrection) || (_skillId == Skill_GreaterResurrection) || (_skillId == Skill_ReturnToNature))) {
            return false;
        }

        if (cha instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) cha;
            if (pc.hasSkillEffect(Skill_AbsoluteBarrier)) {
                if ((_skillId == Skill_Blind) || (_skillId == Skill_WeaponBreak) || (_skillId == Skill_Darkness) || (_skillId == Skill_Weakness)
                        || (_skillId == Skill_Disease) || (_skillId == Skill_FogOfSleeping) || (_skillId == Skill_MassSlow) || (_skillId == Skill_Slow)
                        || (_skillId == Skill_Cancel) || (_skillId == Skill_Silence) || (_skillId == Skill_DecayPotion) || (_skillId == Skill_MassTeleport)
                        || (_skillId == Skill_Detection) || (_skillId == Skill_CounterDetection) || (_skillId == Skill_EraseMagic) || (_skillId == Skill_Entangle)
                        || (_skillId == Skill_EnchantStr) || (_skillId == Skill_EnchantDex) || (_skillId == Skill_BlessWeapon)
                        || (_skillId == Skill_EarthSkin) || (_skillId == Skill_ImmuneToHarm) || (_skillId == Skill_RemoveCurse)) {
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
                if ((_skillId == Skill_Detection) || (_skillId == Skill_CounterDetection)) {
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

        if (((_skill.getTargetTo() & L1Skills.TARGET_TO_PC) == L1Skills.TARGET_TO_PC
        )
                && (cha instanceof L1PcInstance)) {
            _flg = true;
        }
        else if (((_skill.getTargetTo() & L1Skills.TARGET_TO_NPC) == L1Skills.TARGET_TO_NPC
        )
                && ((cha instanceof L1MonsterInstance) || (cha instanceof L1NpcInstance) || (cha instanceof L1SummonInstance) || (cha instanceof L1PetInstance))) {
            _flg = true;
        }
        else if (((_skill.getTargetTo() & L1Skills.TARGET_TO_PET) == L1Skills.TARGET_TO_PET) && (_user instanceof L1PcInstance)) {
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
            if (((_skill.getTargetTo() & L1Skills.TARGET_TO_CLAN) == L1Skills.TARGET_TO_CLAN) && (((_player.getClanid() != 0
            ) && (_player.getClanid() == ((L1PcInstance) cha).getClanid())) || _player.isGm())) {
                return true;
            }
            if (((_skill.getTargetTo() & L1Skills.TARGET_TO_PARTY) == L1Skills.TARGET_TO_PARTY) && (_player.getParty()
                    .isMember((L1PcInstance) cha) || _player.isGm())) {
                return true;
            }
        }

        return _flg;
    }

    private void makeTargetList() {
        try {
            if (_type == TYPE_LOGIN) {
                _targetList.add(new TargetStatus(_user));
                return;
            }
            if ((_skill.getTargetTo() == L1Skills.TARGET_TO_ME) && ((_skill.getType() & L1Skills.TYPE_ATTACK) != L1Skills.TYPE_ATTACK)) {
                _targetList.add(new TargetStatus(_user));
                return;
            }

            if (getSkillRanged() != -1) {
                if (_user.getLocation().getTileLineDistance(_target.getLocation()) > getSkillRanged()) {
                    return;
                }
            }
            else {
                if (!_user.getLocation().isInScreen(_target.getLocation())) {
                    return;
                }
            }

            if ((isTarget(_target) == false) && !(_skill.getTarget().equals("none"))) {
                return;
            }

            if ((_skillId == Skill_Lightning) || (_skillId == Skill_FreezingBreath)) {
                List<L1Object> al1object = L1World.getInstance().getVisibleLineObjects(_user, _target);

                for (L1Object tgobj : al1object) {
                    if (tgobj == null) {
                        continue;
                    }
                    if (!(tgobj instanceof L1Character)) {
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

            if (getSkillArea() == 0) {
                if (!_user.glanceCheck(_target.getX(), _target.getY())) {
                    if (((_skill.getType() & L1Skills.TYPE_ATTACK) == L1Skills.TYPE_ATTACK) && (_skillId != 10026) && (_skillId != 10027)
                            && (_skillId != 10028) && (_skillId != 10029)) {
                        _targetList.add(new TargetStatus(_target, false));
                        return;
                    }
                }
                _targetList.add(new TargetStatus(_target));
            }
            else {
                if (!_skill.getTarget().equals("none")) {
                    _targetList.add(new TargetStatus(_target));
                }

                if ((_skillId != 49) && !(_skill.getTarget().equals("attack") || (_skill.getType() == L1Skills.TYPE_ATTACK))) {
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
                    if (!(tgobj instanceof L1Character)) {
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

    private void sendHappenMessage(L1PcInstance pc) {
        int msgID = _skill.getSysmsgIdHappen();
        if (msgID > 0) {
            if (_skillId == Skill_AreaOfSilence && _user.getId() == pc.getId()) {
                return;
            }
            pc.sendPackets(new S_ServerMessage(msgID));
        }
    }

    private void sendFailMessageHandle() {
        if ((_skill.getType() != L1Skills.TYPE_ATTACK) && !_skill.getTarget().equals("none") && _targetList.isEmpty()) {
            sendFailMessage();
        }
    }

    private void sendFailMessage() {
        int msgID = _skill.getSysmsgIdFail();
        if ((msgID > 0) && (_user instanceof L1PcInstance)) {
            _player.sendPackets(new S_ServerMessage(msgID));
        }
    }

    private boolean isAttrAgrees() {
        int magicattr = _skill.getAttr();
        if (_user instanceof L1NpcInstance) {
            return true;
        }

        if ((_skill.getSkillLevel() >= 17) && (_skill.getSkillLevel() <= 22) && (magicattr != 0
        ) && (magicattr != _player.getElfAttr()
        ) && !_player.isGm()) {
            return false;
        }
        return true;
    }

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

            if ((_player.getInt() > 12) && (_skillId > Skill_HolyWeapon) && (_skillId <= Skill_FreezingBlizzard)) {
                _mpConsume--;
            }
            if ((_player.getInt() > 13) && (_skillId > Skill_Stalac) && (_skillId <= Skill_FreezingBlizzard)) {
                _mpConsume--;
            }
            if ((_player.getInt() > 14) && (_skillId > Skill_WeakElemental) && (_skillId <= Skill_FreezingBlizzard)) {
                _mpConsume--;
            }
            if ((_player.getInt() > 15) && (_skillId > Skill_Meditate) && (_skillId <= Skill_FreezingBlizzard)) {
                _mpConsume--;
            }
            if ((_player.getInt() > 16) && (_skillId > Skill_Darkness) && (_skillId <= Skill_FreezingBlizzard)) {
                _mpConsume--;
            }
            if ((_player.getInt() > 17) && (_skillId > Skill_BlessWeapon) && (_skillId <= Skill_FreezingBlizzard)) {
                _mpConsume--;
            }
            if ((_player.getInt() > 18) && (_skillId > Skill_Disease) && (_skillId <= Skill_FreezingBlizzard)) {
                _mpConsume--;
            }

            if ((_player.getInt() > 12) && (_skillId >= Skill_ShockStun) && (_skillId <= Skill_CounterBarrier)) {
                if ( _player.getInt() <= 17 )
                    _mpConsume -= (_player.getInt() - 12);
                else {
                    _mpConsume -= 5 ;
                    if ( _mpConsume > 1 ) {
                        byte extraInt = (byte) (_player.getInt() - 17) ;
                        for ( int first= 1 ,range = 2 ; first <= extraInt; first += range, range ++  )
                            _mpConsume -- ;
                    }
                }

            }

            if ((_skillId == Skill_EnchantDex) && _player.getInventory().checkEquipped(20013)) {
                _mpConsume /= 2;
            }
            else if ((_skillId == Skill_Haste) && _player.getInventory().checkEquipped(20013)) {
                _mpConsume /= 2;
            }
            else if ((_skillId == Skill_Heal) && _player.getInventory().checkEquipped(20014)) {
                _mpConsume /= 2;
            }
            else if ((_skillId == Skill_GreaterHeal) && _player.getInventory().checkEquipped(20014)) {
                _mpConsume /= 2;
            }
            else if ((_skillId == Skill_EnchantWeapon) && _player.getInventory().checkEquipped(20015)) {
                _mpConsume /= 2;
            }
            else if ((_skillId == Skill_Detection) && _player.getInventory().checkEquipped(20015)) {
                _mpConsume /= 2;
            }
            else if ((_skillId == Skill_EnchantStr) && _player.getInventory().checkEquipped(20015)) {
                _mpConsume /= 2;
            }
            else if ((_skillId == Skill_Haste) && _player.getInventory().checkEquipped(20008)) {
                _mpConsume /= 2;
            }
            else if ((_skillId == Skill_Haste) && _player.getInventory().checkEquipped(20023)) {
                _mpConsume = 25;
            }
            else if ((_skillId == Skill_GreaterHaste) && _player.getInventory().checkEquipped(20023)) {
                _mpConsume /= 2;
            }

            if (_player.getOriginalMagicConsumeReduction() > 0) {
                _mpConsume -= _player.getOriginalMagicConsumeReduction();
            }

            if (0 < _skill.getMpConsume()) {
                _mpConsume = Math.max(_mpConsume, 1);
            }
        }

        if (currentHp < _hpConsume + 1) {
            if (_user instanceof L1PcInstance) {
                _player.sendPackets(new S_ServerMessage(279));
            }
            return false;
        }
        else if (currentMp < _mpConsume) {
            if (_user instanceof L1PcInstance) {
                _player.sendPackets(new S_ServerMessage(278));
            }
            return false;
        }

        return true;
    }

    private boolean isItemConsume() {

        int itemConsume = _skill.getItemConsumeId();
        int itemConsumeCount = _skill.getItemConsumeCount();

        if (itemConsume == 0) {
            return true;
        }

        if (!_player.getInventory().checkItem(itemConsume, itemConsumeCount)) {
            return false;
        }

        return true;
    }

    private void useConsume() {
        if (_user instanceof L1NpcInstance) {
            int current_hp = _npc.getCurrentHp() - _hpConsume;
            _npc.setCurrentHp(current_hp);

            int current_mp = _npc.getCurrentMp() - _mpConsume;
            _npc.setCurrentMp(current_mp);
            return;
        }


        int current_hp = _player.getCurrentHp() - _hpConsume;
        _player.setCurrentHp(current_hp);

        int current_mp = _player.getCurrentMp() - _mpConsume;
        _player.setCurrentMp(current_mp);



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
            return;
        }

        _player.getInventory().consumeItem(itemConsume, itemConsumeCount);
    }

    private void addMagicList(L1Character cha, boolean repetition) {
        try
        {
            int _getBuffDuration = 0;

            if(_skillId != Skill_ShockStun && _skillId != Skill_BoneBreak && _skillId != Skill_EarthBind)
            {
                if (_skillTime == 0) {
                    _getBuffDuration = _skill.getBuffDuration() * 1000;
                    if (_skill.getBuffDuration() == 0) {
                        if (_skillId == Skill_Invisibility) {
                            cha.setSkillEffect(Skill_Invisibility, 0);
                        }
                        return;
                    }
                }
                else {
                    _getBuffDuration = _skillTime * 1000;
                }
            }
            //Prevent Hold Abilities from being recast
            else
            {
                if(cha instanceof L1PcInstance)
                {
                    L1PcInstance pc =(L1PcInstance) cha;
                    if(pc.getBuffs().containsKey(_skillId))
                    {
                        return;
                    }
                    else
                    {
                        pc.sendPackets(new S_SystemMessage("Hold Duration: " + _holdDuration));
                    }
                }
                else if (cha instanceof L1NpcInstance)
                {
                    L1NpcInstance npc = (L1NpcInstance) cha;
                    if(npc.getBuffs().containsKey(_skillId))
                    {
                        return;
                    }
                }
            }

            if (_skillId == Skill_Poison) {
                return;
            }
            if ((_skillId == L1SkillId.CURSE_PARALYZE) || (_skillId == L1SkillId.CURSE_PARALYZE2)) {
                return;
            }
            if (_skillId == L1SkillId.SHAPE_CHANGE) {
                return;
            }
            if ((_skillId == Skill_EnchantArmor) || (_skillId == Skill_HolyWeapon
            ) || (_skillId == Skill_EnchantWeapon) || (_skillId == Skill_BlessWeapon) || (_skillId == Skill_ShadowFang)) {
                return;
            }
            if (((_skillId == Skill_IceLance) || (_skillId == Skill_FreezingBlizzard) || (_skillId == Skill_FreezingBreath)
                    || (_skillId == L1SkillId.ICE_LANCE_COCKATRICE) || (_skillId == L1SkillId.ICE_LANCE_BASILISK)) && !_isFreeze) {
                return;
            }

            if (_skillId == Skill_Confusion) {
                return;
            }

            if (_skillId == Skill_ElementalFallDown && repetition) {
                if (_skillTime == 0) {
                    _getBuffIconDuration = _skill.getBuffDuration();
                } else {
                    _getBuffIconDuration = _skillTime;
                }
                _target.removeSkillEffect(Skill_ElementalFallDown);
                runSkill();
                return;
            }

            cha.setSkillEffect(_skillId, _getBuffDuration);

            if ((cha instanceof L1PcInstance) && repetition) {
                L1PcInstance pc = (L1PcInstance) cha;
                sendIcon(pc);
            }
        }
        catch (Exception e)
        {
            _log.log(Level.SEVERE,e.getMessage(),e);
        }

    }

    public static int randInt(int min, int max) {

        Random rand = new Random();

        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }
    private void sendIcon(L1PcInstance pc) {
        if (_skillTime == 0) {
            _getBuffIconDuration = _skill.getBuffDuration();
        }
        else {
            _getBuffIconDuration = _skillTime;
        }

        if (_skillId == Skill_Shield) {
            pc.sendPackets(new S_SkillIconShield(5, _getBuffIconDuration));
        }
        else if (_skillId == Skill_ShadowArmor) {
            pc.sendPackets(new S_SkillIconShield(3, _getBuffIconDuration));
        }
        else if (_skillId == Skill_DressDex) {
            pc.sendPackets(new S_Dexup(pc, 2, _getBuffIconDuration));
        }
        else if (_skillId == Skill_DressMighty) {
            pc.sendPackets(new S_Strup(pc, 2, _getBuffIconDuration));
        }
        else if (_skillId == Skill_Glowing_Aura) {
            pc.sendPackets(new S_SkillIconAura(113, _getBuffIconDuration));
        }
        else if (_skillId == Skill_Shining_Aura) {
            pc.sendPackets(new S_SkillIconAura(114, _getBuffIconDuration));
        }
        else if (_skillId == Skill_Brave_Aura) {
            pc.sendPackets(new S_SkillIconAura(116, _getBuffIconDuration));
        }
        else if (_skillId == Skill_FireWeapon) {
            pc.sendPackets(new S_SkillIconAura(147, _getBuffIconDuration));
        }
        else if (_skillId == Skill_WindShot) {
            pc.sendPackets(new S_SkillIconAura(148, _getBuffIconDuration));
        }
        else if (_skillId == Skill_BlessOfFire) {
            pc.sendPackets(new S_SkillIconAura(154, _getBuffIconDuration));
        }
        else if (_skillId == Skill_EyeofStorm) {
            pc.sendPackets(new S_SkillIconAura(155, _getBuffIconDuration));
        }
        else if (_skillId == Skill_BlessOfEarth) {
            pc.sendPackets(new S_SkillIconShield(7, _getBuffIconDuration));
        }
        else if (_skillId == Skill_BurningWeapon) {
            pc.sendPackets(new S_SkillIconAura(162, _getBuffIconDuration));
        }
        else if (_skillId == Skill_StormShot) {
            pc.sendPackets(new S_SkillIconAura(165, _getBuffIconDuration));
        }
        else if (_skillId == Skill_IronSkin) {
            pc.sendPackets(new S_SkillIconShield(10, _getBuffIconDuration));
        }
        else if (_skillId == Skill_EarthSkin) {
            pc.sendPackets(new S_SkillIconShield(6, _getBuffIconDuration));
        }
        else if (_skillId == Skill_EnchantStr) {
            pc.sendPackets(new S_Strup(pc, 5, _getBuffIconDuration));
        }
        else if (_skillId == Skill_EnchantDex)
        {
            pc.sendPackets(new S_Dexup(pc, 5, _getBuffIconDuration));
        }
        else if ((_skillId == Skill_Haste) || (_skillId == Skill_GreaterHaste)) {
            pc.sendPackets(new S_SkillHaste(pc.getId(), 1, _getBuffIconDuration));
            pc.broadcastPacket(new S_SkillHaste(pc.getId(), 1, 0));
        }
        else if ((_skillId == Skill_HolyWalk) || (_skillId == Skill_MovingAcceleration) || (_skillId == Skill_WindWalk)) {
            pc.sendPackets(new S_SkillBrave(pc.getId(), 4, _getBuffIconDuration));
            pc.broadcastPacket(new S_SkillBrave(pc.getId(), 4, 0));
        }
        else if (_skillId == Skill_BloodLust) {
            pc.sendPackets(new S_SkillBrave(pc.getId(), 6, _getBuffIconDuration));
            pc.broadcastPacket(new S_SkillBrave(pc.getId(), 6, 0));
        }
        else if ((_skillId == Skill_Slow) || (_skillId == Skill_MassSlow) || (_skillId == Skill_Entangle)) {
            pc.sendPackets(new S_SkillHaste(pc.getId(), 2, _getBuffIconDuration));
            pc.broadcastPacket(new S_SkillHaste(pc.getId(), 2, 0));
        }
        else if (_skillId == Skill_ImmuneToHarm) {
            pc.sendPackets(new S_SkillIconGFX(40, _getBuffIconDuration));
        }
        else if (_skillId == Skill_WindShackle) {
            pc.sendPackets(new S_SkillIconWindShackle(pc.getId(), _getBuffIconDuration));
            pc.broadcastPacket(new S_SkillIconWindShackle(pc.getId(), _getBuffIconDuration));
        }
        pc.sendPackets(new S_OwnCharStatus(pc));
    }


    public void sendIcon(L1PcInstance pc, int skillId, int buffIconDuration) {
        if (skillId == Skill_Shield) {
            pc.sendPackets(new S_SkillIconShield(5, buffIconDuration));
        }
        else if (skillId == Skill_ShadowArmor) {
            pc.sendPackets(new S_SkillIconShield(3, buffIconDuration));
        }
        else if (skillId == Skill_DressDex) {
            pc.sendPackets(new S_Dexup(pc, 2, buffIconDuration));
        }
        else if (skillId == Skill_DressMighty) {
            pc.sendPackets(new S_Strup(pc, 2, buffIconDuration));
        }
        else if (skillId == Skill_Glowing_Aura) {
            pc.sendPackets(new S_SkillIconAura(113, buffIconDuration));
        }
        else if (skillId == Skill_Shining_Aura) {
            pc.sendPackets(new S_SkillIconAura(114, buffIconDuration));
        }
        else if (skillId == Skill_Brave_Aura) {
            pc.sendPackets(new S_SkillIconAura(116, buffIconDuration));
        }
        else if (skillId == Skill_FireWeapon) {
            pc.sendPackets(new S_SkillIconAura(147, buffIconDuration));
        }
        else if (skillId == Skill_WindShot) {
            pc.sendPackets(new S_SkillIconAura(148, buffIconDuration));
        }
        else if (skillId == Skill_BlessOfFire) {
            pc.sendPackets(new S_SkillIconAura(154, buffIconDuration));
        }
        else if (skillId == Skill_EyeofStorm) {
            pc.sendPackets(new S_SkillIconAura(155, buffIconDuration));
        }
        else if (skillId == Skill_BlessOfEarth) {
            pc.sendPackets(new S_SkillIconShield(7, buffIconDuration));
        }
        else if (skillId == Skill_BurningWeapon) {
            pc.sendPackets(new S_SkillIconAura(162, buffIconDuration));
        }
        else if (skillId == Skill_StormShot) {
            pc.sendPackets(new S_SkillIconAura(165, buffIconDuration));
        }
        else if (skillId == Skill_IronSkin) {
            pc.sendPackets(new S_SkillIconShield(10, buffIconDuration));
        }
        else if (skillId == Skill_EarthSkin) {
            pc.sendPackets(new S_SkillIconShield(6, buffIconDuration));
        }
        else if (skillId == Skill_EnchantStr) {
            pc.sendPackets(new S_Strup(pc, 5, buffIconDuration));
        }
        else if (skillId == Skill_EnchantDex) {
            pc.sendPackets(new S_Dexup(pc, 5, buffIconDuration));
        }
        else if ((skillId == Skill_Haste) || (skillId == Skill_GreaterHaste)) {
            pc.sendPackets(new S_SkillHaste(pc.getId(), 1, buffIconDuration));
            pc.broadcastPacket(new S_SkillHaste(pc.getId(), 1, 0));
        }
        else if ((skillId == Skill_HolyWalk) || (skillId == Skill_MovingAcceleration) || (skillId == Skill_WindWalk)) {
            pc.sendPackets(new S_SkillBrave(pc.getId(), 4, buffIconDuration));
            pc.broadcastPacket(new S_SkillBrave(pc.getId(), 4, 0));
        }
        else if (skillId == Skill_BloodLust) {
            pc.sendPackets(new S_SkillBrave(pc.getId(), 6, buffIconDuration));
            pc.broadcastPacket(new S_SkillBrave(pc.getId(), 6, 0));
        }
        else if ((skillId == Skill_Slow) || (skillId == Skill_MassSlow) || (skillId == Skill_Entangle)) {
            pc.sendPackets(new S_SkillHaste(pc.getId(), 2, buffIconDuration));
            pc.broadcastPacket(new S_SkillHaste(pc.getId(), 2, 0));
        }
        else if (skillId == Skill_ImmuneToHarm) {
            pc.sendPackets(new S_SkillIconGFX(40, buffIconDuration));
        }
        else if (skillId == Skill_WindShackle) {
            pc.sendPackets(new S_SkillIconWindShackle(pc.getId(), buffIconDuration));
            pc.broadcastPacket(new S_SkillIconWindShackle(pc.getId(), buffIconDuration));
        }
        else if (skillId == Skill_BurningSpirit)
        {
            pc.sendPackets(new S_SkillIconAura(162, buffIconDuration));
        }
        pc.sendPackets(new S_OwnCharStatus(pc));
    }
    private void sendGrfx(boolean isSkillAction) {
        if (_actid == 0) {
            _actid = _skill.getActionId();
        }
        if (_gfxid == 0) {
            _gfxid = _skill.getCastGfx();
        }
        if (_gfxid == 0) {
            return;
        }
        int[] data = null;

        if (_user instanceof L1PcInstance) {

            int targetid = 0;
            if (_skillId != Skill_Firewall) {
                targetid = _target.getId();
            }
            L1PcInstance pc = (L1PcInstance) _user;

            switch(_skillId) {
                case Skill_Firewall:
                case Skill_LifeStream:
                case Skill_ElementalFallDown:
                    if (_skillId == Skill_Firewall) {
                        pc.setHeading(pc.targetDirection(_targetX, _targetY));
                        pc.sendPackets(new S_ChangeHeading(pc));
                        pc.broadcastPacket(new S_ChangeHeading(pc));
                    }
                    S_DoActionGFX gfx = new S_DoActionGFX(pc.getId(), _actid);
                    pc.sendPackets(gfx);
                    pc.broadcastPacket(gfx);
                    return;
                case Skill_ShockStun:
                    if (_targetList.isEmpty()) {
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
                case Skill_Light:
                    pc.sendPackets(new S_Sound(145));
                    break;
                case Skill_MindBreak:
                case Skill_JoyofPain:
                    data = new int[] {_actid, _dmg, 0};
                    pc.sendPackets(new S_AttackPacket(pc, targetid, data));
                    pc.broadcastPacket(new S_AttackPacket(pc, targetid, data));
                    pc.sendPackets(new S_SkillSound(targetid, _gfxid));
                    pc.broadcastPacket(new S_SkillSound(targetid, _gfxid));
                    return;
                case Skill_Confusion:
                    data = new int[] {_actid, _dmg, 0};
                    pc.sendPackets(new S_AttackPacket(pc, targetid, data));
                    pc.broadcastPacket(new S_AttackPacket(pc, targetid, data));
                    return;
                case Skill_Smash:
                    pc.sendPackets(new S_SkillSound(targetid, _gfxid));
                    pc.broadcastPacket(new S_SkillSound(targetid, _gfxid));
                    return;
                case Skill_TameMonster:
                    pc.sendPackets(new S_EffectLocation(_targetX, _targetY, _gfxid));
                    pc.broadcastPacket(new S_EffectLocation(_targetX, _targetY, _gfxid));
                    return;
                default:
                    break;
            }

            if (_skillId == Skill_ArmBreaker) return;

            if (_targetList.isEmpty() && !(_skill.getTarget().equals("none"))) {
                int tempchargfx = _player.getTempCharGfx();
                if ((tempchargfx == 5727) || (tempchargfx == 5730)) {
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
                if (isPcSummonPet(_target)) {
                    if ((_player.getZoneType() == 1) || (_target.getZoneType() == 1)
                            || _player.checkNonPvP(_player, _target)) {
                        data = new int[] {_actid, 0, _gfxid, 6};
                        _player.sendPackets(new S_UseAttackSkill(_player, _target.getId(), _targetX, _targetY, data));
                        _player.broadcastPacket(new S_UseAttackSkill(_player, _target.getId(), _targetX, _targetY, data));
                        return;
                    }
                }

                if (getSkillArea() == 0) {
                    data = new int[] {_actid, _dmg, _gfxid, 6};
                    _player.sendPackets(new S_UseAttackSkill(_player, targetid, _targetX, _targetY, data));
                    _player.broadcastPacket(new S_UseAttackSkill(_player, targetid, _targetX, _targetY, data));
                    _target.broadcastPacketExceptTargetSight(new S_DoActionGFX(targetid, ActionCodes.ACTION_Damage), _player);
                }
                else {
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
            else if (_skill.getTarget().equals("none") && (_skill.getType() == L1Skills.TYPE_ATTACK)) {
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
            else {
                if ((_skillId != Skill_Teleport) && (_skillId != Skill_MassTeleport) && (_skillId != Skill_TeleportToMotherTree)) {
                    if (isSkillAction) {
                        S_DoActionGFX gfx = new S_DoActionGFX(_player.getId(), _skill.getActionId());
                        _player.sendPackets(gfx);
                        _player.broadcastPacket(gfx);
                    }
                    if ((_skillId == Skill_CounterMagic) || (_skillId == Skill_CounterBarrier) || (_skillId == Skill_CounterMirror)) {
                        _player.sendPackets(new S_SkillSound(targetid, _gfxid));
                    }
                    else if ((_skillId == Skill_AwakenAntharas) || (_skillId == Skill_AwakenFafurion) || (_skillId == Skill_AwakenValakas)) {
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

                for (TargetStatus ts : _targetList) {
                    L1Character cha = ts.getTarget();
                    if (cha instanceof L1PcInstance) {
                        L1PcInstance chaPc = (L1PcInstance) cha;
                        chaPc.sendPackets(new S_OwnCharStatus(chaPc));
                    }
                }
            }
        }
        else if (_user instanceof L1NpcInstance) {
            int targetid = _target.getId();

            if (_user instanceof L1MerchantInstance) {
                _user.broadcastPacket(new S_SkillSound(targetid, _gfxid));
                return;
            }

            if (_skillId == L1SkillId.CURSE_PARALYZE || _skillId == Skill_Weakness || _skillId == Skill_Disease) {
                _user.setHeading(_user.targetDirection(_targetX, _targetY));
                _user.broadcastPacket(new S_ChangeHeading(_user));
            }

            if (_targetList.isEmpty() && !(_skill.getTarget().equals("none"))) {
                S_DoActionGFX gfx = new S_DoActionGFX(_user.getId(), _actid);
                _user.broadcastPacket(gfx);
                return;
            }

            if (_skill.getTarget().equals("attack") && (_skillId != 18)) {
                if (getSkillArea() == 0) {
                    data = new int[] {_actid, _dmg, _gfxid, 6};
                    _user.broadcastPacket(new S_UseAttackSkill(_user, targetid, _targetX, _targetY, data));
                    _target.broadcastPacketExceptTargetSight(new S_DoActionGFX(targetid, ActionCodes.ACTION_Damage), _user);
                }
                else {
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
            else if (_skill.getTarget().equals("none") && (_skill.getType() == L1Skills.TYPE_ATTACK)) {
                L1Character[] cha = new L1Character[_targetList.size()];
                int i = 0;
                for (TargetStatus ts : _targetList) {
                    cha[i] = ts.getTarget();
                    i++;
                }
                _user.broadcastPacket(new S_RangeSkill(_user, cha, _gfxid, _actid, S_RangeSkill.TYPE_NODIR));
            }
            else {
                if ((_skillId != 5) && (_skillId != 69) && (_skillId != 131)) {
                    S_DoActionGFX gfx = new S_DoActionGFX(_user.getId(), _actid);
                    _user.broadcastPacket(gfx);
                    _user.broadcastPacket(new S_SkillSound(targetid, _gfxid));
                }
            }
        }
    }


    private void deleteRepeatedSkills(L1Character cha) {
        final int[][] repeatedSkills =
                {

                        { Skill_FireWeapon, Skill_WindShot, Skill_BlessOfFire, Skill_EyeofStorm, Skill_BurningWeapon, Skill_StormShot, L1SkillId.EFFECT_BLESS_OF_MAZU },
                        { Skill_Shield, Skill_ShadowArmor, Skill_EarthSkin, Skill_BlessOfEarth, Skill_IronSkin },
                        { L1SkillId.STATUS_BRAVE, L1SkillId.STATUS_ELFBRAVE, Skill_HolyWalk, Skill_MovingAcceleration, Skill_WindWalk, L1SkillId.STATUS_BRAVE2, Skill_BloodLust },
                        { Skill_Haste, Skill_GreaterHaste, L1SkillId.STATUS_HASTE },
                        { Skill_Slow , Skill_MassSlow , Skill_Entangle },
                        { Skill_EnchantDex, Skill_DressDex },
                        { Skill_EnchantStr, Skill_DressMighty },
                        { Skill_Glowing_Aura, Skill_Shining_Aura },
                        { Skill_MirrorImage, Skill_UncannyDodge} };


        for (int[] skills : repeatedSkills) {
            for (int id : skills) {
                if (id == _skillId) {
                    stopSkillList(cha, skills);
                }
            }
        }
    }

    private void stopSkillList(L1Character cha, int[] repeat_skill) {
        for (int skillId : repeat_skill) {
            if (skillId != _skillId) {
                cha.removeSkillEffect(skillId);
            }
        }
    }

    private void setDelay() {
        if (_skill.getReuseDelay() > 0) {
            L1SkillDelay.onSkillUse(_user, _skill.getReuseDelay());
        }
    }

    private void runSkill() {

        switch(_skillId) {
            case Skill_LifeStream:
                L1EffectSpawn.getInstance().spawnEffect(81169, _skill.getBuffDuration() * 1000, _targetX, _targetY, _user.getMapId());
                return;
            case Skill_CubeIgnition:
                L1EffectSpawn.getInstance().spawnEffect(80149, _skill.getBuffDuration() * 1000, _targetX, _targetY, _user.getMapId(),
                        (L1PcInstance) _user, _skillId);
                return;
            case Skill_CubeQuake:
                L1EffectSpawn.getInstance().spawnEffect(80150, _skill.getBuffDuration() * 1000, _targetX, _targetY, _user.getMapId(),
                        (L1PcInstance) _user, _skillId);
                return;
            case Skill_CubeShock:
                L1EffectSpawn.getInstance().spawnEffect(80151, _skill.getBuffDuration() * 1000, _targetX, _targetY, _user.getMapId(),
                        (L1PcInstance) _user, _skillId);
                return;
            case Skill_CubeBalance:
                L1EffectSpawn.getInstance().spawnEffect(80152, _skill.getBuffDuration() * 1000, _targetX, _targetY, _user.getMapId(),
                        (L1PcInstance) _user, _skillId);
                return;
            case Skill_Firewall:
                L1EffectSpawn.getInstance().doSpawnFireWall(_user, _targetX, _targetY);
                return;
            case Skill_TrueTarget:
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

        for (int skillId : EXCEPT_COUNTER_MAGIC) {
            if (_skillId == skillId) {
                _isCounterMagic = false;
                break;
            }
        }

        if ((_skillId == Skill_ShockStun || _skillId == Skill_BoneBreak) && (_user instanceof L1PcInstance)) {
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
                dmg = 0;
                heal = 0;
                undeadType = 0;
                ts = iter.next();
                cha = ts.getTarget();

                if (!ts.isCalc() || !isTargetCalc(cha)) {
                    continue;
                }

                L1Magic _magic = new L1Magic(_user, cha);
                _magic.setLeverage(getLeverage());

                if (cha instanceof L1MonsterInstance) {
                    undeadType = ((L1MonsterInstance) cha).getNpcTemplate().get_undead();
                }

                if (((_skill.getType() == L1Skills.TYPE_CURSE) || (_skill.getType() == L1Skills.TYPE_PROBABILITY)) && isTargetFailure(cha)) {
                    iter.remove();
                    continue;
                }

                if (cha instanceof L1PcInstance) {
                    if (_skillTime == 0) {
                        _getBuffIconDuration = _skill.getBuffDuration();
                    }
                    else {
                        _getBuffIconDuration = _skillTime;
                    }
                }

                deleteRepeatedSkills(cha);

                if ((_skill.getType() == L1Skills.TYPE_ATTACK) && (_user.getId() != cha.getId())) {
                    if (isUseCounterMagic(cha)) {
                        iter.remove();
                        continue;
                    }
                    dmg = _magic.calcMagicDamage(_skillId);
                    _dmg = dmg;


                    if((_skillId != Skill_TripleShot) && (_skillId != Skill_FoeSlayer))
                    {
                        cha.removeSkillEffect(Skill_EraseMagic);
                    }
                }
                else if ((_skill.getType() == L1Skills.TYPE_CURSE) || (_skill.getType() == L1Skills.TYPE_PROBABILITY)) {
                    isSuccess = _magic.calcProbabilityMagic(_skillId);
                    if (_skillId != Skill_EraseMagic) {
                        cha.removeSkillEffect(Skill_EraseMagic);
                    }
                    if (_skillId != Skill_FogOfSleeping) {
                        cha.removeSkillEffect(Skill_FogOfSleeping);
                    }
                    if (isSuccess) {
                        if (isUseCounterMagic(cha)) {
                            iter.remove();
                            continue;
                        }
                    }
                    else {

                        if (((_skillId == Skill_Phantasm ) ||(_skillId == Skill_FogOfSleeping)) && (cha instanceof L1PcInstance)) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.sendPackets(new S_ServerMessage(297));
                        }
                        iter.remove();
                        continue;
                    }
                }
                else if (_skill.getType() == L1Skills.TYPE_HEAL) {
                    dmg = -1 * _magic.calcHealing(_skillId);
                    if (cha.hasSkillEffect(Skill_WaterLife)) {
                        dmg *= 2;
                        cha.killSkillEffectTimer(Skill_WaterLife);
                        if (cha instanceof L1PcInstance) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.sendPackets(new S_SkillIconWaterLife());
                        }
                    }
                    if (cha.hasSkillEffect(Skill_Pollute_Water)) {
                        dmg /= 2;
                    }
                }
                else if ((_skillId == Skill_BlessOfFire || _skillId == Skill_EyeofStorm || _skillId == Skill_BlessOfEarth || _skillId == Skill_Glowing_Aura || _skillId == Skill_Shining_Aura || _skillId == Skill_Brave_Aura) && _user.getId() != cha.getId()) {
                    if (cha instanceof L1PcInstance) {
                        L1PcInstance _targetPc = (L1PcInstance) cha;
                        _targetPc.sendPackets(new S_SkillSound(_targetPc.getId(), _skill.getCastGfx()));
                        _targetPc.broadcastPacket(new S_SkillSound(_targetPc.getId(), _skill.getCastGfx()));
                    }
                }

                if (cha.hasSkillEffect(_skillId) && (_skillId != Skill_ShockStun && _skillId != Skill_BoneBreak && _skillId != Skill_Confusion && _skillId != Skill_ThunderGrab)) {
                        addMagicList(cha, true);

                        if (_skillId != L1SkillId.SHAPE_CHANGE)
                            {
                                continue;
                            }
                    }

                switch(_skillId) {
                    case Skill_Haste:
                        if (cha.getMoveSpeed() != 2) {
                            if (cha instanceof L1PcInstance) {
                                L1PcInstance pc = (L1PcInstance) cha;
                                if (pc.getHasteItemEquipped() > 0) {
                                    continue;
                                }
                                pc.setDrink(false);
                                pc.sendPackets(new S_SkillHaste(pc.getId(), 1, _getBuffIconDuration));
                            }
                            if (cha instanceof L1PetInstance) {
                                L1PetInstance pet = (L1PetInstance) cha;
                                if (pet.hasSkillEffect(L1SkillId.STATUS_HASTE))
                                    pet.setMoveSpeed(0);
                                pet.setParalyzed(true);
                                pet.setParalyzed(false);
                            }
                            cha.broadcastPacket(new S_SkillHaste(cha.getId(), 1, 0));
                            cha.setMoveSpeed(1);
                        }
                        else {
                            int skillNum = 0;
                            if (cha.hasSkillEffect(Skill_Slow)) {
                                skillNum = Skill_Slow;
                            }
                            else if (cha.hasSkillEffect(Skill_MassSlow)) {
                                skillNum = Skill_MassSlow;
                            }
                            else if (cha.hasSkillEffect(Skill_Entangle)) {
                                skillNum = Skill_Entangle;
                            }
                            if (skillNum != 0) {
                                cha.removeSkillEffect(skillNum);
                                cha.removeSkillEffect(Skill_Haste);
                                cha.setMoveSpeed(0);
                                continue;
                            }
                        }
                        break;
                    case Skill_GreaterHaste:
                        if (cha instanceof L1PcInstance) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            if (pc.getHasteItemEquipped() > 0) {
                                continue;
                            }
                            if (pc.getMoveSpeed() != 2) {
                                pc.setDrink(false);
                                pc.setMoveSpeed(1);
                                pc.sendPackets(new S_SkillHaste(pc.getId(), 1, _getBuffIconDuration));
                                pc.broadcastPacket(new S_SkillHaste(pc.getId(), 1, 0));
                            }
                            else {
                                int skillNum = 0;
                                if (pc.hasSkillEffect(Skill_Slow)) {
                                    skillNum = Skill_Slow;
                                }
                                else if (pc.hasSkillEffect(Skill_MassSlow)) {
                                    skillNum = Skill_MassSlow;
                                }
                                else if (pc.hasSkillEffect(Skill_Entangle)) {
                                    skillNum = Skill_Entangle;
                                }
                                if (skillNum != 0) {
                                    pc.removeSkillEffect(skillNum);
                                    pc.removeSkillEffect(Skill_GreaterHaste);
                                    pc.setMoveSpeed(0);
                                    continue;
                                }
                            }
                        }
                        break;
                    case Skill_Slow:
                    case Skill_MassSlow:
                    case Skill_Entangle:
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
                            if (cha.hasSkillEffect(Skill_Haste)) {
                                skillNum = Skill_Haste;
                            }
                            else if (cha.hasSkillEffect(Skill_GreaterHaste)) {
                                skillNum = Skill_GreaterHaste;
                            }
                            else if (cha.hasSkillEffect(L1SkillId.STATUS_HASTE)) {
                                skillNum = L1SkillId.STATUS_HASTE;
                            }
                            if (skillNum != 0) {
                                cha.removeSkillEffect(skillNum);
                                cha.removeSkillEffect(_skillId);
                                cha.setMoveSpeed(0);
                                continue;
                            }
                        }
                        break;
                    case Skill_ChillTouch:
                    case Skill_VampiricTouch:
                        heal = dmg;
                        break;
                    case L1SkillId.ICE_LANCE_COCKATRICE:
                    case L1SkillId.ICE_LANCE_BASILISK:
                    case Skill_IceLance:
                    case Skill_FreezingBlizzard:
                    case Skill_FreezingBreath:
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
                    case 20011:
                        _user.setHeading(_user.targetDirection(_targetX, _targetY));
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
                    case Skill_EarthBind:
                    case Skill_BoneBreak:
                    case Skill_ShockStun:
                        _holdDuration = L1Hold.Hold(_user, cha, _skillId);
                        break;
                    case Skill_ThunderGrab:
                        isSuccess = _magic.calcProbabilityMagic(_skillId);
                        if (isSuccess) {
                            if (!cha.hasSkillEffect(L1SkillId.THUNDER_GRAB_START) && !cha.hasSkillEffect(L1SkillId.STATUS_FREEZE) ) {
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
                                cha.setSkillEffect(L1SkillId.THUNDER_GRAB_START, 500);
                            }
                        }
                        break;
                    case Skill_ArmBreaker:
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
                            npc.broadcastPacket(new S_SkillSound(npc.getId(),6551));
                        }
                        RandomGenerator random2 = RandomGeneratorFactory.getSharedRandom();
                        int armchance = (random2.nextInt(100) + 1);
                        int time = 10;
                        if (armchance <= 50) {
                            if (cha instanceof L1PcInstance) {
                                if (cha instanceof L1PcInstance) {
                                    L1PcInstance pc = (L1PcInstance) cha;
                                    pc.setSkillEffect(Skill_ArmBreaker, time);
                                    pc.sendPackets(new S_SkillIconGFX(74,
                                            (time / 3)));
                                }
                            } else if (cha instanceof L1MonsterInstance
                                    || cha instanceof L1SummonInstance
                                    || cha instanceof L1PetInstance) {
                                L1NpcInstance npc = (L1NpcInstance) cha;
                                npc.setSkillEffect(Skill_ArmBreaker, time);
                            }
                        }
                        break;
                    case Skill_TurnUndead:
                        if (undeadType == 1 || undeadType == 3){
                            dmg = cha.getCurrentHp();
                        }
                        break;

                    case Skill_ArmorBreak:
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
                                    if (!cha.hasSkillEffect(Skill_ArmorBreak)) {
                                        L1PcInstance pc = (L1PcInstance) cha;
                                        pc.sendPackets(new S_SkillIconGFX(74, 3));
                                        cha.setSkillEffect(Skill_ArmorBreak, 8 * 1000);
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
                            System.out.println(e);
                        }
                    case Skill_ManaDrain:
                        int manachance = Random.nextInt(10) + 5;
                        drainMana = manachance + (_user.getInt() / 2);
                        if (cha.getCurrentMp() < drainMana) {
                            drainMana = cha.getCurrentMp();
                        }
                        break;
                    case Skill_Teleport:
                    case Skill_MassTeleport:
                        if (cha instanceof L1PcInstance) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            L1BookMark bookm = pc.getBookMark(_bookmarkId);
                            if (bookm != null) {
                                if (pc.getMap().isEscapable() || pc.isGm()) {
                                    int newX = bookm.getLocX();
                                    int newY = bookm.getLocY();
                                    short mapId = bookm.getMapId();

                                    if (_skillId == Skill_MassTeleport) {
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
                            else {
                                if (pc.getMap().isTeleportable() || pc.isGm()) {
                                    L1Location newLocation = pc.getLocation().randomLocation(200, true);
                                    int newX = newLocation.getX();
                                    int newY = newLocation.getY();
                                    short mapId = (short) newLocation.getMapId();

                                    if (_skillId == Skill_MassTeleport) {
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
                                    pc.sendPackets(new S_ServerMessage(276));
                                    pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, true));
                                    L1Teleport.teleport(pc, pc.getX(), pc.getY(), pc.getMapId(), 5, false);
                                }
                            }
                        }
                        break;
                    case Skill_CallPledgeMember:
                        if (cha instanceof L1PcInstance) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            L1PcInstance clanPc = (L1PcInstance) L1World.getInstance().findObject(_targetID);
                            if (clanPc != null) {
                                clanPc.setTempID(pc.getId());
                                clanPc.sendPackets(new S_Message_YN(729, ""));
                            }
                        }
                        break;
                    case Skill_Teleport_to_Pledge_Member:
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
                                    pc.sendPackets(new S_ServerMessage(647));
                                    pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, true));
                                }
                            }
                        }
                        break;
                    case Skill_CounterDetection:
                        if (cha instanceof L1PcInstance) {
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
                    case Skill_CreateMagicalWeapon:
                        if (cha instanceof L1PcInstance) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            L1ItemInstance item = pc.getInventory().getItem(_itemobjid);
                            if ((item != null) && (item.getItem().getType2() == 1)) {
                                int item_type = item.getItem().getType2();
                                int safe_enchant = item.getItem().get_safeenchant();
                                int enchant_level = item.getEnchantLevel();
                                String item_name = item.getName();
                                if (safe_enchant < 0) {
                                    pc.sendPackets(
                                            new S_ServerMessage(79));
                                }
                                else if (safe_enchant == 0) {
                                    pc.sendPackets(
                                            new S_ServerMessage(79));
                                }
                                else if ((item_type == 1) && (enchant_level == 0)) {
                                    if (!item.isIdentified()) {
                                        pc.sendPackets(
                                                new S_ServerMessage(161, item_name, "$245", "$247"));
                                    }
                                    else {
                                        item_name = "+0 " + item_name;
                                        pc.sendPackets(
                                                new S_ServerMessage(161, "+0 " + item_name, "$245", "$247"));
                                    }
                                    item.setEnchantLevel(1);
                                    pc.getInventory().updateItem(item, L1PcInventory.COL_ENCHANTLVL);
                                }
                                else {
                                    pc.sendPackets(
                                            new S_ServerMessage(79));
                                }
                            }
                            else {
                                pc.sendPackets(
                                        new S_ServerMessage(79));
                            }
                        }
                        break;
                    case Skill_PurifyStone:
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
                                        pc.sendPackets(new S_ServerMessage(403, "$2475"));
                                    } else {
                                        pc.sendPackets(new S_ServerMessage(280));
                                    }
                                } else if (item.getItem().getItemId() == 40321) {
                                    pc.getInventory().removeItem(item, 1);
                                    if (brave >= run) {
                                        pc.getInventory().storeItem(40322, 1);
                                        pc.sendPackets(new S_ServerMessage(403, "$2476"));
                                    } else {
                                        pc.sendPackets(new S_ServerMessage(280));
                                    }
                                } else if (item.getItem().getItemId() == 40322) {
                                    pc.getInventory().removeItem(item, 1);
                                    if (wise >= run) {
                                        pc.getInventory().storeItem(40323, 1);
                                        pc.sendPackets(new S_ServerMessage(403, "$2477"));
                                    } else {
                                        pc.sendPackets(new S_ServerMessage(280));
                                    }
                                } else if (item.getItem().getItemId() == 40323) {
                                    pc.getInventory().removeItem(item, 1);
                                    if (kayser >= run) {
                                        pc.getInventory().storeItem(40324, 1);
                                        pc.sendPackets(new S_ServerMessage(403, "$2478"));
                                    } else {
                                        pc.sendPackets(new S_ServerMessage(280));
                                    }
                                }
                            }
                        }
                        break;
                    case Skill_Light:

                        if (cha instanceof L1PcInstance) {

                        }
                        break;
                    case Skill_ShadowFang:
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
                    case Skill_EnchantWeapon:
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
                    case Skill_HolyWeapon:
                    case Skill_BlessWeapon:
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
                    case Skill_EnchantArmor:
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


                if ((_skill.getType() == L1Skills.TYPE_HEAL) && (_calcType == PC_NPC) && (undeadType == 1)) {
                    dmg *= -1;
                }
                if ((_skill.getType() == L1Skills.TYPE_HEAL) && (_calcType == PC_NPC) && (undeadType == 3)) {
                    dmg = 0;
                }
                if (((cha instanceof L1TowerInstance) || (cha instanceof L1DoorInstance)) && (dmg < 0)) {
                    dmg = 0;
                }
                if ((dmg > 0) || (drainMana != 0)) {
                    _magic.commit(dmg, drainMana);
                }
                if ((_skill.getType() == L1Skills.TYPE_HEAL) && (dmg < 0)) {
                    cha.setCurrentHp((dmg * -1) + cha.getCurrentHp());
                }
                if (heal > 0) {
                    _user.setCurrentHp(heal + _user.getCurrentHp());
                }

                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    pc.turnOnOffLight();
                    pc.sendPackets(new S_OwnCharAttrDef(pc));
                    pc.sendPackets(new S_OwnCharStatus(pc));
                    sendHappenMessage(pc);
                }

                addMagicList(cha, false);

                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    pc.turnOnOffLight();
                }
            }

            if ((_skillId == Skill_Detection) || (_skillId == Skill_CounterDetection)) {
                Skill_Detection(_player);
            }

        }
        catch (Exception e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        }
    }

    private void Skill_Detection(L1PcInstance pc) {
        if (!pc.isGmInvis() && pc.isInvisble()) {
            pc.delInvis();
            pc.beginInvisTimer();
        }

        for (L1PcInstance tgt : L1World.getInstance().getVisiblePlayer(pc)) {
            if (!tgt.isGmInvis() && tgt.isInvisble()) {
                tgt.delInvis();
            }
        }
        L1WorldTraps.getInstance().onDetection(pc);
    }

    private boolean isTargetCalc(L1Character cha) {
        if ((_user instanceof L1PcInstance)
                && (_skillId == Skill_TripleShot || _skillId == Skill_FoeSlayer
                || _skillId == Skill_Smash || _skillId == Skill_BoneBreak)) {
            return true;
        }
        if (_skill.getTarget().equals("attack") && (_skillId != 18)) {
            if (isPcSummonPet(cha)) {
                if ((_player.getZoneType() == 1) || (cha.getZoneType() == 1
                ) || _player.checkNonPvP(_player, cha)) {
                    return false;
                }
            }
        }

        if ((_skillId == Skill_FogOfSleeping) && (_user.getId() == cha.getId())) {
            return false;
        }

        if (_skillId == Skill_MassSlow) {
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

        if (_skillId == Skill_MassTeleport) {
            if (_user.getId() != cha.getId()) {
                return false;
            }
        }

        return true;
    }

    private boolean isPcSummonPet(L1Character cha) {
        if (_calcType == PC_PC) {
            return true;
        }

        if (_calcType == PC_NPC) {
            if (cha instanceof L1SummonInstance) {
                L1SummonInstance summon = (L1SummonInstance) cha;
                if (summon.isExsistMaster()) {
                    return true;
                }
            }
            if (cha instanceof L1PetInstance) {
                return true;
            }
        }
        return false;
    }

    private boolean isTargetFailure(L1Character cha) {
        boolean isTU = false;
        boolean isErase = false;
        boolean isManaDrain = false;
        int undeadType = 0;

        if ((cha instanceof L1TowerInstance) || (cha instanceof L1DoorInstance)) {
            return true;
        }

        if (cha instanceof L1PcInstance) {
            if ((_calcType == PC_PC) && _player.checkNonPvP(_player, cha)) {
                L1PcInstance pc = (L1PcInstance) cha;
                if ((_player.getId() == pc.getId()) || ((pc.getClanid() != 0) && (_player.getClanid() == pc.getClanid()))) {
                    return false;
                }
                return true;
            }
            return false;
        }

        if (cha instanceof L1MonsterInstance) {
            isTU = ((L1MonsterInstance) cha).getNpcTemplate().get_IsTU();
        }

        if (cha instanceof L1MonsterInstance) {
            isErase = ((L1MonsterInstance) cha).getNpcTemplate().get_IsErase();
        }

        if (cha instanceof L1MonsterInstance) {
            undeadType = ((L1MonsterInstance) cha).getNpcTemplate().get_undead();
        }

        if (cha instanceof L1MonsterInstance) {
            isManaDrain = true;
        }

        if (((_skillId == Skill_TurnUndead) && ((undeadType == 0) || (undeadType == 2)))
                || ((_skillId == Skill_TurnUndead) && (isTU == false))
                || (((_skillId == Skill_EraseMagic) || (_skillId == Skill_Slow) || (_skillId == Skill_ManaDrain) || (_skillId == Skill_MassSlow) || (_skillId == Skill_Entangle) || (_skillId == Skill_WindShackle)) && (isErase == false))
                || ((_skillId == Skill_ManaDrain) && (isManaDrain == false))) {
            return true;
        }
        return false;
    }

    private boolean isUseCounterMagic(L1Character cha) {
        if (_isCounterMagic && cha.hasSkillEffect(Skill_CounterMagic)) {
            cha.removeSkillEffect(Skill_CounterMagic);
            int castgfx = SkillsTable.getInstance().getTemplate(Skill_CounterMagic).getCastGfx();
            cha.broadcastPacket(new S_SkillSound(cha.getId(), castgfx));
            if (cha instanceof L1PcInstance) {
                L1PcInstance pc = (L1PcInstance) cha;
                pc.sendPackets(new S_SkillSound(pc.getId(), castgfx));
            }
            return true;
        }
        return false;
    }

    public static void turnStone(final L1PcInstance player,final L1ItemInstance item, double penalty, int count, boolean report) {
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

    private static void turnStone(final L1PcInstance player,final L1ItemInstance item, int chance, int nextStone, String name,int count, boolean report) {
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