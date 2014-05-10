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

import l1j.server.L1Message;
import l1j.server.server.ActionCodes;
import l1j.server.server.datatables.NpcTable;
import l1j.server.server.datatables.PolyTable;
import l1j.server.server.datatables.SkillsTable;
import l1j.server.server.model.L1CastleLocation;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1EffectSpawn;
import l1j.server.server.model.L1Location;
import l1j.server.server.model.L1Magic;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1PcInventory;
import l1j.server.server.model.L1PolyMorph;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1War;
import l1j.server.server.model.L1World;
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
import l1j.server.server.serverpackets.S_AttackPacket;
import l1j.server.server.serverpackets.S_ChangeHeading;
import l1j.server.server.serverpackets.S_Dexup;
import l1j.server.server.serverpackets.S_DoActionGFX;
import l1j.server.server.serverpackets.S_EffectLocation;
import l1j.server.server.serverpackets.S_IdentifyDesc;
import l1j.server.server.serverpackets.S_Message_YN;
import l1j.server.server.serverpackets.S_OwnCharAttrDef;
import l1j.server.server.serverpackets.S_OwnCharStatus;
import l1j.server.server.serverpackets.S_Paralysis;
import l1j.server.server.serverpackets.S_Poison;
import l1j.server.server.serverpackets.S_RangeSkill;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SkillBrave;
import l1j.server.server.serverpackets.S_SkillHaste;
import l1j.server.server.serverpackets.S_SkillIconAura;
import l1j.server.server.serverpackets.S_SkillIconGFX;
import l1j.server.server.serverpackets.S_SkillIconShield;
import l1j.server.server.serverpackets.S_SkillIconWaterLife;
import l1j.server.server.serverpackets.S_SkillIconWindShackle;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.serverpackets.S_Sound;
import l1j.server.server.serverpackets.S_Strup;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.serverpackets.S_TrueTarget;
import l1j.server.server.serverpackets.S_UseAttackSkill;
import l1j.server.server.templates.L1BookMark;
import l1j.server.server.templates.L1Skills;
import l1j.server.server.utils.Random;
import l1j.server.server.utils.collections.IntArrays;
import l1j.server.server.utils.collections.Lists;
import static l1j.server.server.model.skill.L1SkillId.*;
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

    private int _dmg;

    private int _getBuffDuration;

    private int _shockStunDuration;
    
    private int _earthBindDuration;

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

    private boolean _checkedUseSkill = false; // 鈭����皜���

    private int _leverage = 10; // 1/10���10�1��

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

    // 閮剖�������������
    private static final int[] EXCEPT_COUNTER_MAGIC =
            { 1, 2, 3, 5, 8, 9, 12, 13, 14, 19, 21, 26, 31, 32, 35, 37, 42, 43, 44, 48, 49, 52, 54, 55, 57, 60, 61, 63, 67, 68, 69, 72, 73, 75, 78, 79,
                    SHOCK_STUN, REDUCTION_ARMOR, BOUNCE_ATTACK, SOLID_CARRIAGE, COUNTER_BARRIER, 97, 98, 99, 100, 101, 102, 104, 105, 106, 107, 109, 110,
                    111, 113, 114, 115, 116, 117, 118, 129, 130, 131, 132, 134, 137, 138, 146, 147, 148, 149, 150, 151, 155, 156, 158, 159, 161, 163, 164,
                    165, 166, 168, 169, 170, 171, SOUL_OF_FLAME, ADDITIONAL_FIRE, DRAGON_SKIN, AWAKEN_ANTHARAS, AWAKEN_FAFURION, AWAKEN_VALAKAS,
                    MIRROR_IMAGE, ILLUSION_OGRE, ILLUSION_LICH, PATIENCE, 10026, 10027, ILLUSION_DIA_GOLEM, INSIGHT, ILLUSION_AVATAR, 10028, 10029 };

    private static final int [] CAST_WITH_SILENCE =
            {
                    SHOCK_STUN, REDUCTION_ARMOR, BOUNCE_ATTACK, SOLID_CARRIAGE, COUNTER_BARRIER, ENCHANT_VENOM,SHADOW_ARMOR,BRING_STONE,
                    MOVING_ACCELERATION,BURNING_SPIRIT,VENOM_RESIST,DOUBLE_BRAKE,UNCANNY_DODGE,SHADOW_FANG,DRESS_MIGHTY,
                    DRESS_DEXTERITY,DRESS_EVASION
            };

    static {
        Arrays.sort(CAST_WITH_SILENCE);
    }

    public L1SkillUse() {
    }

    private static class TargetStatus {
        private L1Character _target = null;

        private boolean _isCalc = true; // �������Ⅱ����閮������������

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

    public void setSkillRanged(int i) {
        _skillRanged = i;
    }

    public int getSkillRanged() {
        if (_skillRanged == 0) {
            return _skill.getRanged();
        }
        return _skillRanged;
    }

    public void setSkillArea(int i) {
        _skillArea = i;
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

    public boolean checkUseSkill(L1PcInstance player, int skillid, int target_id, int x, int y, String message, int time, int type,
                                 L1Character attacker) {
        return checkUseSkill(player, skillid, target_id, x, y, message, time, type, attacker, 0, 0, 0);
    }

    public boolean checkUseSkill(L1PcInstance player, int skillid, int target_id, int x, int y, String message, int time, int type,
                                 L1Character attacker, int actid, int gfxid, int mpConsume) {
        // ���身摰�����
        setCheckedUseSkill(true);
        _targetList = Lists.newList(); // ��������������

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

        if (type == TYPE_NORMAL) { // �虜�擳�蝙����
            checkedResult = isNormalSkillUsable();
        }
        else if (type == TYPE_SPELLSC) { // ��������雿輻���
            checkedResult = isSpellScrollUsable();
        }
        else if (type == TYPE_NPCBUFF) {
            checkedResult = true;
        }
        if (!checkedResult) {
            return false;
        }

        // ������������������閰撖曇情��漣璅�
        // �����閰�摨扳���蔭���������
        if ((_skillId == FIRE_WALL) || (_skillId == LIFE_STREAM) || (_skillId == TRUE_TARGET)) {
            return true;
        }

        L1Object l1object = L1World.getInstance().findObject(_targetID);
        if (l1object instanceof L1ItemInstance) {
            _log.fine("skill target item name: " + ((L1ItemInstance) l1object).getViewName());
            // ����������移��������������
            // Linux�憓蝣箄��indows���蝣箄���
            // 2008.5.4餈質���������擳��蝙���������������������return
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

        // ��������������撖曇情�������ID
        if ((_skillId == TELEPORT) || (_skillId == MASS_TELEPORT)) {
            _bookmarkId = target_id;
        }
        // 撖曇情���������
        if ((_skillId == CREATE_MAGICAL_WEAPON) || (_skillId == BRING_STONE) || (_skillId == BLESSED_ARMOR) || (_skillId == ENCHANT_WEAPON)
                || (_skillId == SHADOW_FANG))
        {
            _itemobjid = target_id;
        }
        _target = (L1Character) l1object;

        if (!(_target instanceof L1MonsterInstance) && _skill.getTarget().equals("attack") && (_user.getId() != target_id)) {
            _isPK = true; // �����������隞亙����頂�������誑憭���K���������
        }

        // ���身摰���

        // 鈭����
        if (!(l1object instanceof L1Character)) { // ������������隞亙�����������
            checkedResult = false;
        }
        makeTargetList(); // ������銝�閬扼����
        if (_targetList.isEmpty() && (_user instanceof L1NpcInstance)) {
            //System.out.println("No Target");
            checkedResult = false;
        }
        // 鈭��������
        return checkedResult;
    }

    private boolean isNormalSkillUsable() {
        // ���雿輻��C��������
        if (_user instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) _user;

            if (pc.isTeleport()) { // ��葉
                return false;
            }
            if (pc.isParalyzed()) { // 暻餌��������
                return false;
            }
            if ((pc.isInvisble() || pc.isInvisDelay()) && !isInvisUsableSkill()) { // �頨思葉�瘜蝙����
                return false;
            }
            if (pc.getInventory().getWeight242() >= 197) { // \f1雿�葆憭芸�����迨�瘜蝙�瘜���
                pc.sendPackets(new S_ServerMessage(316));
                return false;
            }
            int polyId = pc.getTempCharGfx();
            L1PolyMorph poly = PolyTable.getInstance().getTemplate(polyId);
            // 擳��蝙�����澈
            if ((poly != null) && !poly.canUseSkill()) {
                pc.sendPackets(new S_ServerMessage(285)); // \f1�甇斤����瘜蝙�擳���
                return false;
            }

            if (!isAttrAgrees()) { // 蝎暸������扼��������雿������
                return false;
            }

            if ((_skillId == ELEMENTAL_PROTECTION) && (pc.getElfAttr() == 0)) {
                pc.sendPackets(new S_ServerMessage(280)); // \f1���仃����
                return false;
            }

			/* 瘞港葉�瘜蝙��撅祆�折��� */
            if (pc.getMap().isUnderwater() && _skill.getAttr() == 2) {
                pc.sendPackets(new S_ServerMessage(280)); // \f1���仃����
                return false;
            }

            // �������銝凋蝙�銝
            if (pc.isSkillDelay()) {
                return false;
            }

            // 擳����蝳�瘥�劂�
            if ((pc.hasSkillEffect(SILENCE) ||
                    pc.hasSkillEffect(AREA_OF_SILENCE) ||
                    pc.hasSkillEffect(STATUS_POISON_SILENCE)||
                    pc.hasSkillEffect(CONFUSION_ING)) &&
                    !IntArrays.sContains(CAST_WITH_SILENCE, _skillId)) {
                pc.sendPackets(new S_ServerMessage(285));
                return false;
            }

            // DIG��������雿輻�
            if ((_skillId == DISINTEGRATE) && (pc.getLawful() < 500)) {
                // ��������������蝣箄��
                pc.sendPackets(new S_ServerMessage(352, "$967")); // �閬蝙������惇�批��� (甇�蝢�)��
                return false;
            }

            // �����������憭�����蔭��
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
                    pc.sendPackets(new S_ServerMessage(1412)); // 撌脣��銝������憛��
                    return false;
                }
            }

            // 閬粹����� - ��死�����瘜蝙�
            if (((pc.getAwakeSkillId() == AWAKEN_ANTHARAS) && (_skillId != AWAKEN_ANTHARAS))
                    || ((pc.getAwakeSkillId() == AWAKEN_FAFURION) && (_skillId != AWAKEN_FAFURION))
                    || ((pc.getAwakeSkillId() == AWAKEN_VALAKAS) && (_skillId != AWAKEN_VALAKAS))
                    && (_skillId != MAGMA_BREATH) && (_skillId != SHOCK_SKIN) && (_skillId != FREEZING_BREATH)) {
                pc.sendPackets(new S_ServerMessage(1385)); // ������葉�瘜蝙�閬粹�����
                return false;
            }

            if ((isItemConsume() == false) && !_player.isGm()) { // 瘜��������
                _player.sendPackets(new S_ServerMessage(299)); // \f1��擳��������雲��
                return false;
            }
        }
        // ���雿輻��PC��������
        else if (_user instanceof L1NpcInstance) {

            // ���������雿輻銝
            if (_user.hasSkillEffect(SILENCE)) {
                // NPC����������������1����蝙���������������
                _user.removeSkillEffect(SILENCE);
                return false;
            }
        }

        // PC�PC��炎�HP�P��頞喳��
        if (!isHPMPConsume()) { // �鞎餌�P�P閮��
            return false;
        }
        return true;
    }

    private boolean isSpellScrollUsable() {
        // ����������蝙�����PC��
        L1PcInstance pc = (L1PcInstance) _user;

        if (pc.isTeleport()) { // ��葉
            return false;
        }

        if (pc.isParalyzed()) { // 暻餌��������
            return false;
        }

        // ����銝准雿輻銝����
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

    public void handleCommands(L1PcInstance player, int skillId, int targetId, int x, int y, String message, int timeSecs, int type, L1Character attacker)
    {
        try
        {
            if (!isCheckedUseSkill()) {
                boolean isUseSkill = checkUseSkill(player, skillId, targetId, x, y, message, timeSecs, type, attacker);

                if (!isUseSkill) {
                    failSkill();
                    return;
                }
            }

            if (type == 0) {
                if ((!this._isGlanceCheckFail) || (getSkillArea() > 0) || (this._skill.getTarget().equals("none"))) {
                    if ((this._skill.getSkillId() == 186) || (this._skill.getSkillId() == 52)) {
                        if ((this._target.hasSkillEffect(52)) && (this._skill.getSkillId() == 52)) {
                            this._skillTime = (300 + this._target.getSkillEffectTimeSec(52));
                            this._skillTime = Math.min(this._skillTime, 1800);
                        }
                        if ((this._target.hasSkillEffect(186)) && (this._skill.getSkillId() == 186)) {
                            this._skillTime = 300;
                        }
                    }
                    runSkill();
                    useConsume();
                    sendGrfx(true);
                    sendFailMessageHandle();
                    setDelay();
                }
            }
            else if (type == 1) {
                runSkill();
            }
            else if (type == 2) {
                runSkill();
                sendGrfx(true);
            }
            else if (type == 4) {
                runSkill();
                sendGrfx(false);
            }
            else if (type == 3) {
                runSkill();
                sendGrfx(true);
            }
            setCheckedUseSkill(false);
        }
        catch (Exception e) {
            _log.log(Level.SEVERE, "", e);
        }
    }

    private void failSkill() {
        // HP��雲���������蝙����������P��瘨祥�����摰���������
        // ��隞���雿��祥�������
        // useConsume(); // HP�P�皜���
        setCheckedUseSkill(false);
        // ��������
        if ((_skillId == TELEPORT) || (_skillId == MASS_TELEPORT) || (_skillId == TELEPORT_TO_MATHER)) {
            // ����������������������敹��������
            // ����������閫��嚗洵2撘���������
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

        // �憯������撖曇情憭�
        if (cha instanceof L1DoorInstance) {
            if ((cha.getMaxHp() == 0) || (cha.getMaxHp() == 1)) {
                return false;
            }
        }

        // ��������撖曇情憭�
        if ((cha instanceof L1DollInstance) && (_skillId != HASTE)) {
            return false;
        }





        // ���������et�ummon隞亙�NPC�����C�et�ummon�撖曇情憭�
        if ((_calcType == PC_NPC) && (_target instanceof L1NpcInstance) && !(_target instanceof L1PetInstance)
                && !(_target instanceof L1SummonInstance)
                && ((cha instanceof L1PetInstance) || (cha instanceof L1SummonInstance) || (cha instanceof L1PcInstance))) {
            return false;
        }

        // ������������誑憭NPC��������撖曇情憭�
        if ((_calcType == PC_NPC) && (_target instanceof L1NpcInstance) && !(_target instanceof L1GuardInstance) && (cha instanceof L1GuardInstance)) {
            return false;
        }

        // NPC撖銷C���������������������������
        if ((_skill.getTarget().equals("attack") || (_skill.getType() == L1Skills.TYPE_ATTACK)) && (_calcType == NPC_PC)
                && !(cha instanceof L1PetInstance) && !(cha instanceof L1SummonInstance) && !(cha instanceof L1PcInstance)) {
            return false;
        }

        // NPC撖鋅PC�雿輻��OB��������OB���������������
        if ((_skill.getTarget().equals("attack") || (_skill.getType() == L1Skills.TYPE_ATTACK)) && (_calcType == NPC_NPC)
                && (_user instanceof L1MonsterInstance) && (cha instanceof L1MonsterInstance)) {
            return false;
        }

        // �����������������PC�撖曇情憭�
        if (_skill.getTarget().equals("none")
                && (_skill.getType() == L1Skills.TYPE_ATTACK)
                && ((cha instanceof L1AuctionBoardInstance) || (cha instanceof L1BoardInstance) || (cha instanceof L1CrownInstance)
                || (cha instanceof L1DwarfInstance) || (cha instanceof L1EffectInstance) || (cha instanceof L1FieldObjectInstance)
                || (cha instanceof L1FurnitureInstance) || (cha instanceof L1HousekeeperInstance) || (cha instanceof L1MerchantInstance) || (cha instanceof L1TeleporterInstance))) {
            return false;
        }

        // ������瘜��撌�
        if ((_skill.getType() == L1Skills.TYPE_ATTACK) && (cha.getId() == _user.getId())) {
            return false;
        }

        // 擃��儔銵��瘜����
        if ((cha.getId() == _user.getId()) && (_skillId == HEAL_ALL)) {
            return false;
        }

        if ((((_skill.getTargetTo() & L1Skills.TARGET_TO_PC) == L1Skills.TARGET_TO_PC)
                || ((_skill.getTargetTo() & L1Skills.TARGET_TO_CLAN) == L1Skills.TARGET_TO_CLAN) || ((_skill.getTargetTo() & L1Skills.TARGET_TO_PARTY) == L1Skills.TARGET_TO_PARTY))
                && (cha.getId() == _user.getId()) && (_skillId != HEAL_ALL)) {
            return true; // ������������������������������������������憭��
        }

        // ���雿輻��C��K���������������������撖曇情憭�
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
            // ������������
            if ((_skillId == COUNTER_DETECTION) && (enemy.getZoneType() != 1)
                    && (cha.hasSkillEffect(INVISIBILITY) || cha.hasSkillEffect(BLIND_HIDING))) {
                return true; // ����������������銝�
            }
            if ((_player.getClanid() != 0) && (enemy.getClanid() != 0)) { // �����撅葉
                // ��鈭�������
                for (L1War war : L1World.getInstance().getWarList()) {
                    if (war.CheckClanInWar(_player.getClanname())) { // ������鈭���葉
                        if (war.CheckClanInSameWar( // ���鈭���葉
                                _player.getClanname(), enemy.getClanname())) {
                            if (L1CastleLocation.checkInAllWarArea(enemy.getX(), enemy.getY(), enemy.getMapId())) {
                                return true;
                            }
                        }
                    }
                }
            }
            return false; // ������PK�����������
        }

        if ((_user.glanceCheck(cha.getX(), cha.getY()) == false) && (_skill.isThrough() == false)) {
            // ��������儔瘣颯�����拿���摰�����
            if (!((_skill.getType() == L1Skills.TYPE_CHANGE) || (_skill.getType() == L1Skills.TYPE_RESTORE))) {
                _isGlanceCheckFail = true;
                return false; // �蝺���拿������
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
			return false; // ������銝准���������������������������
		}

		if (cha.hasSkillEffect(FREEZING_BLIZZARD) && ((_skillId == ICE_LANCE) || (_skillId == FREEZING_BLIZZARD) || (_skillId == FREEZING_BREATH))) {
			return false; // ������������葉����������������������������
		}

		if (cha.hasSkillEffect(FREEZING_BREATH) && ((_skillId == ICE_LANCE) || (_skillId == FREEZING_BLIZZARD) || (_skillId == FREEZING_BREATH))) {
			return false; // ���������銝准���������������������������
		}
*/
        if (cha.hasSkillEffect(EARTH_BIND) && (_skillId == EARTH_BIND)) {
            return false; // ��� �����葉���� ������
        }

        if (!(cha instanceof L1MonsterInstance) && ((_skillId == TAMING_MONSTER) || (_skillId == CREATE_ZOMBIE))) {
            return false; // ��������������������������嚗�
        }




        if (cha.isDead()
                && ((_skillId != CREATE_ZOMBIE) && (_skillId != RESURRECTION) && (_skillId != GREATER_RESURRECTION) && (_skillId != CALL_OF_NATURE))) {
            if (cha instanceof L1PcInstance) {
                L1PcInstance pc = (L1PcInstance) cha;
                //pc.sendPackets(new S_SystemMessage("Is a character and is a res spell and is dead"));
            }
            else if (cha instanceof L1NpcInstance) {
                //System.out.println("Is a npc and is a res spell and is dead");

            }

            return false; // �璅歇甇颱滿 瘜��儔瘣駁��
        }

        if ((cha.isDead() == false) && ((_skillId == CREATE_ZOMBIE) || (_skillId == RESURRECTION) || (_skillId == GREATER_RESURRECTION) || (_skillId == CALL_OF_NATURE))) {

            if (cha instanceof L1PcInstance) {
                L1PcInstance pc = (L1PcInstance) cha;
                //pc.sendPackets(new S_SystemMessage("Is a character and is a res spell and is not dead"));
            }
            else if (cha instanceof L1NpcInstance) {
                //System.out.println("Is a npc and is a res spell and is not dead");

            }

            return false; // �璅甇颱滿 瘜�儔瘣駁��
        }

        if (((cha instanceof L1TowerInstance) || (cha instanceof L1DoorInstance))
                && ((_skillId == CREATE_ZOMBIE) || (_skillId == RESURRECTION) || (_skillId == GREATER_RESURRECTION) || (_skillId == CALL_OF_NATURE))) {
            return false; // 憛��銝�敺拇暑瘜��
        }

        if (cha instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) cha;
            if (pc.hasSkillEffect(ABSOLUTE_BARRIER)) { // 蝯������葉
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
                if ((_skillId == DETECTION) || (_skillId == COUNTER_DETECTION)) { // ���������
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

        if (((_skill.getTargetTo() & L1Skills.TARGET_TO_PC) == L1Skills.TARGET_TO_PC // �������C
        )
                && (cha instanceof L1PcInstance)) {
            _flg = true;
        }
        else if (((_skill.getTargetTo() & L1Skills.TARGET_TO_NPC) == L1Skills.TARGET_TO_NPC // �������PC
        )
                && ((cha instanceof L1MonsterInstance) || (cha instanceof L1NpcInstance) || (cha instanceof L1SummonInstance) || (cha instanceof L1PetInstance))) {
            _flg = true;
        }
        else if (((_skill.getTargetTo() & L1Skills.TARGET_TO_PET) == L1Skills.TARGET_TO_PET) && (_user instanceof L1PcInstance)) { // �������ummon,Pet
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
            if (((_skill.getTargetTo() & L1Skills.TARGET_TO_CLAN) == L1Skills.TARGET_TO_CLAN) && (((_player.getClanid() != 0 // ����������
            ) && (_player.getClanid() == ((L1PcInstance) cha).getClanid())) || _player.isGm())) {
                return true;
            }
            if (((_skill.getTargetTo() & L1Skills.TARGET_TO_PARTY) == L1Skills.TARGET_TO_PARTY) && (_player.getParty() // �����������
                    .isMember((L1PcInstance) cha) || _player.isGm())) {
                return true;
            }
        }

        return _flg;
    }

    private void makeTargetList() {
        try {
            if(_target instanceof L1PcInstance)
            {
                //System.out.println("the _target is a PC");
            }
            else if(_target instanceof L1NpcInstance)
            {
                //System.out.println("the _target is a npc");
            }
            else
            {
                //System.out.println("the _target is something else");
            }

            if (_type == TYPE_LOGIN) { // �������(甇颱滿��������������������)�雿輻��
                _targetList.add(new TargetStatus(_user));
                return;
            }
            if ((_skill.getTargetTo() == L1Skills.TARGET_TO_ME) && ((_skill.getType() & L1Skills.TYPE_ATTACK) != L1Skills.TYPE_ATTACK)) {
                _targetList.add(new TargetStatus(_user)); // ������雿輻��
                return;
            }

            // 撠��-1����������������紋鞊�
            if (getSkillRanged() != -1) {
                if (_user.getLocation().getTileLineDistance(_target.getLocation()) > getSkillRanged()) {
                    return; // 撠��憭�
                }
            }
            else {
                if (!_user.getLocation().isInScreen(_target.getLocation())) {
                    return; // 撠��憭�
                }
            }

            if ((isTarget(_target) == false) && !(_skill.getTarget().equals("none"))) {
                // 撖曇情�����������������
                return;
            }

            if ((_skillId == LIGHTNING) || (_skillId == FREEZING_BREATH)) { // ����������������蝺�蝭��捱����
                List<L1Object> al1object = L1World.getInstance().getVisibleLineObjects(_user, _target);

                for (L1Object tgobj : al1object) {
                    if (tgobj == null) {
                        continue;
                    }
                    if (!(tgobj instanceof L1Character)) { // ������������隞亙�����������
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

            if (getSkillArea() == 0) { // �������
                if (!_user.glanceCheck(_target.getX(), _target.getY())) { // �蝺���拿�������
                    if (((_skill.getType() & L1Skills.TYPE_ATTACK) == L1Skills.TYPE_ATTACK) && (_skillId != 10026) && (_skillId != 10027)
                            && (_skillId != 10028) && (_skillId != 10029)) { // 摰���誑憭�����
                        _targetList.add(new TargetStatus(_target, false)); // ���������������������������������������
                        return;
                    }
                }
                _targetList.add(new TargetStatus(_target));
            }
            else { // 蝭�����
                if (!_skill.getTarget().equals("none")) {
                    _targetList.add(new TargetStatus(_target));
                }

                if ((_skillId != 49) && !(_skill.getTarget().equals("attack") || (_skill.getType() == L1Skills.TYPE_ATTACK))) {
                    // ���頂隞亙�����H-A隞亙�������頨怒�����
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
                    if (!(tgobj instanceof L1Character)) { // ������������隞亙�����������
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
            // �������瘜�頨怒��
            if (_skillId == AREA_OF_SILENCE && _user.getId() == pc.getId()) {// 撠蝳
                return;
            }
            pc.sendPackets(new S_ServerMessage(msgID));
        }
    }

    private void sendFailMessageHandle() {
        // �����隞亙�撖曇情����������仃������憭望����������������縑
        // �餅�������拿�������������������������
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
        if (_user instanceof L1NpcInstance) { // NPC��蝙���������K
            return true;
        }

        if ((_skill.getSkillLevel() >= 17) && (_skill.getSkillLevel() <= 22) && (magicattr != 0 // 蝎暸����撅�折��������
        ) && (magicattr != _player.getElfAttr() // 雿輻�擳�撅�扼���������
        ) && !_player.isGm()) { // ����M�靘��
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

            // MP�INT頠賣��
            if ((_player.getInt() > 12) && (_skillId > HOLY_WEAPON) && (_skillId <= FREEZING_BLIZZARD)) { // LV2隞乩��
                _mpConsume--;
            }
            if ((_player.getInt() > 13) && (_skillId > STALAC) && (_skillId <= FREEZING_BLIZZARD)) { // LV3隞乩��
                _mpConsume--;
            }
            if ((_player.getInt() > 14) && (_skillId > WEAK_ELEMENTAL) && (_skillId <= FREEZING_BLIZZARD)) { // LV4隞乩��
                _mpConsume--;
            }
            if ((_player.getInt() > 15) && (_skillId > MEDITATION) && (_skillId <= FREEZING_BLIZZARD)) { // LV5隞乩��
                _mpConsume--;
            }
            if ((_player.getInt() > 16) && (_skillId > DARKNESS) && (_skillId <= FREEZING_BLIZZARD)) { // LV6隞乩��
                _mpConsume--;
            }
            if ((_player.getInt() > 17) && (_skillId > BLESS_WEAPON) && (_skillId <= FREEZING_BLIZZARD)) { // LV7隞乩��
                _mpConsume--;
            }
            if ((_player.getInt() > 18) && (_skillId > DISEASE) && (_skillId <= FREEZING_BLIZZARD)) { // LV8隞乩��
                _mpConsume--;
            }

            // 擉ㄚ������
            if ((_player.getInt() > 12) && (_skillId >= SHOCK_STUN) && (_skillId <= COUNTER_BARRIER)) {
                if ( _player.getInt() <= 17 )
                    _mpConsume -= (_player.getInt() - 12);
                else {
                    _mpConsume -= 5 ; // int > 18
                    if ( _mpConsume > 1 ) { // 瘜��隞交���
                        byte extraInt = (byte) (_player.getInt() - 17) ;
                        // 皜�撘�
                        for ( int first= 1 ,range = 2 ; first <= extraInt; first += range, range ++  )
                            _mpConsume -- ;
                    }
                }

            }

            // 鋆�P皜�� 銝�甈∪����銝���
            if ((_skillId == PHYSICAL_ENCHANT_DEX) && _player.getInventory().checkEquipped(20013)) { // ��擳���蝙��瘞�����
                _mpConsume /= 2;
            }
            else if ((_skillId == HASTE) && _player.getInventory().checkEquipped(20013)) { // ��擳���蝙������
                _mpConsume /= 2;
            }
            else if ((_skillId == HEAL) && _player.getInventory().checkEquipped(20014)) { // 瘝餌�����蝙����祥����
                _mpConsume /= 2;
            }
            else if ((_skillId == EXTRA_HEAL) && _player.getInventory().checkEquipped(20014)) { // 瘝餌�����蝙�銝剔�祥����
                _mpConsume /= 2;
            }
            else if ((_skillId == ENCHANT_WEAPON) && _player.getInventory().checkEquipped(20015)) { // �������蝙��隡潮��郎�
                _mpConsume /= 2;
            }
            else if ((_skillId == DETECTION) && _player.getInventory().checkEquipped(20015)) { // �������蝙������耦銵�
                _mpConsume /= 2;
            }
            else if ((_skillId == PHYSICAL_ENCHANT_STR) && _player.getInventory().checkEquipped(20015)) { // �������蝙�擃�撥�銵�
                _mpConsume /= 2;
            }
            else if ((_skillId == HASTE) && _player.getInventory().checkEquipped(20008)) { // 撠�◢銋��蝙������
                _mpConsume /= 2;
            }
            else if ((_skillId == HASTE) && _player.getInventory().checkEquipped(20023)) { // 憸其���蝙������
                _mpConsume = 25;
            }
            else if ((_skillId == GREATER_HASTE) && _player.getInventory().checkEquipped(20023)) { // 憸其���蝙�撘瑕�����
                _mpConsume /= 2;
            }

            // ��������
            if (_player.getOriginalMagicConsumeReduction() > 0) {
                _mpConsume -= _player.getOriginalMagicConsumeReduction();
            }

            if (0 < _skill.getMpConsume()) {
                _mpConsume = Math.max(_mpConsume, 1); // ��撠�� 1
            }
        }

        if (currentHp < _hpConsume + 1) {
            if (_user instanceof L1PcInstance) {
                _player.sendPackets(new S_ServerMessage(279)); // \f1�����雲�瘜蝙�擳���
            }
            return false;
        }
        else if (currentMp < _mpConsume) {
            if (_user instanceof L1PcInstance) {
                _player.sendPackets(new S_ServerMessage(278)); // \f1�����雲�瘜蝙�擳���
            }
            return false;
        }

        return true;
    }

    private boolean isItemConsume() {

        int itemConsume = _skill.getItemConsumeId();
        int itemConsumeCount = _skill.getItemConsumeCount();

        if (itemConsume == 0) {
            return true; // �������������
        }

        if (!_player.getInventory().checkItem(itemConsume, itemConsumeCount)) {
            return false; // 敹����雲��������
        }

        return true;
    }

    private void useConsume() {
        if (_user instanceof L1NpcInstance) {
            // NPC�����P�P������
            int current_hp = _npc.getCurrentHp() - _hpConsume;
            _npc.setCurrentHp(current_hp);

            int current_mp = _npc.getCurrentMp() - _mpConsume;
            _npc.setCurrentMp(current_mp);
            return;
        }

        // HP�MP�鞎� 撌脩���蝙����
        if (_skillId == FINAL_BURN) { // �������
            _player.setCurrentHp(_player.getCurrentHp() - 1);
            _player.setCurrentMp(_player.getCurrentMp() - 1);
        }
        else {
            int current_hp = _player.getCurrentHp() - _hpConsume;
            _player.setCurrentHp(current_hp);

            int current_mp = _player.getCurrentMp() - _mpConsume;
            _player.setCurrentMp(current_mp);
        }

        // Lawful�����
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
            return; // �������������
        }

        // 雿輻�������
        _player.getInventory().consumeItem(itemConsume, itemConsumeCount);
    }

    private void addMagicList(L1Character cha, boolean repetition) {
        if(_skillId != SHOCK_STUN && _skillId != EARTH_BIND)
        {
            if (_skillTime == 0) {
                _getBuffDuration = _skill.getBuffDuration() * 1000; // ������
                if (_skill.getBuffDuration() == 0) {
                    if (_skillId == INVISIBILITY) { // ��������
                        cha.setSkillEffect(INVISIBILITY, 0);
                    }
                    return;
                }
            }
            else {
                _getBuffDuration = _skillTime * 1000;
            }
        }
        else if (_skillId == SHOCK_STUN) {

            if(cha instanceof L1PcInstance)
            {
                L1PcInstance pc = new L1PcInstance();
                pc = (L1PcInstance) cha;

                if(pc.getBuffs().containsKey(87))
                {
                    _getBuffDuration = 	pc.getBuffs().get(87).getRemainingTime();
                    pc.sendPackets(new S_SystemMessage("Cannot restun with stun time remaining: " + _getBuffDuration));
                    return;
                }
                else
                {
                    _getBuffDuration = _shockStunDuration;
                    pc.sendPackets(new S_SystemMessage("Shock Stun Duration: " + _getBuffDuration));
                    L1EffectSpawn.getInstance().spawnEffect(81162,_shockStunDuration, cha.getX(), cha.getY(),cha.getMapId());
                }
            }
            else
            {
                if(cha.getBuffs().containsKey(87))
                {
                    _getBuffDuration = 	cha.getBuffs().get(87).getRemainingTime();
                    return;
                }
                else
                {
                    _getBuffDuration = _shockStunDuration;
                    L1EffectSpawn.getInstance().spawnEffect(81162,_shockStunDuration, cha.getX(), cha.getY(),cha.getMapId());
                }
            }
        }
        
        else if (_skillId == EARTH_BIND) {

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
                    L1EffectSpawn.getInstance().spawnEffect(97076,_earthBindDuration, cha.getX(), cha.getY(),cha.getMapId());
                }
            }
        }

        if (_skillId == CURSE_POISON) {  // 瘥�����宏� L1Poison �����
            return;
        }
        if ((_skillId == CURSE_PARALYZE) || (_skillId == CURSE_PARALYZE2)) { // �銋������������宏� L1CurseParalysis �����
            return;
        }
        if (_skillId == SHAPE_CHANGE) { // 霈耦銵����宏� L1PolyMorph �����
            return;
        }
        if ((_skillId == BLESSED_ARMOR) || (_skillId == HOLY_WEAPON // 甇血������������L1ItemInstance�蝘餉革��
        ) || (_skillId == ENCHANT_WEAPON) || (_skillId == BLESS_WEAPON) || (_skillId == SHADOW_FANG)) {
            return;
        }
        if (((_skillId == ICE_LANCE) || (_skillId == FREEZING_BLIZZARD) || (_skillId == FREEZING_BREATH)
                || (_skillId == ICE_LANCE_COCKATRICE) || (_skillId == ICE_LANCE_BASILISK)) && !_isFreeze) { // ���仃���
            return;
        }
        if ((_skillId == AWAKEN_ANTHARAS) || (_skillId == AWAKEN_FAFURION) || (_skillId == AWAKEN_VALAKAS)) { // 閬������L1Awake�蝘餉革��
            return;
        }
        // 撉琿��憯����憭���
        if (_skillId == BONE_BREAK || _skillId == CONFUSION) {
            return;
        }
        cha.setSkillEffect(_skillId, _getBuffDuration);

        if (_skillId == ELEMENTAL_FALL_DOWN && repetition) { // 撘勗�惇�折���
            if (_skillTime == 0) {
                _getBuffIconDuration = _skill.getBuffDuration(); // ������
            } else {
                _getBuffIconDuration = _skillTime;
            }
            _target.removeSkillEffect(ELEMENTAL_FALL_DOWN);
            runSkill();
            return;
        }
        if ((cha instanceof L1PcInstance) && repetition) { // 撖曇情��C�����������������
            L1PcInstance pc = (L1PcInstance) cha;
            sendIcon(pc);
        }
    }

    private void sendIcon(L1PcInstance pc) {
        if (_skillTime == 0) {
            _getBuffIconDuration = _skill.getBuffDuration(); // ������
        }
        else {
            _getBuffIconDuration = _skillTime; // ������time���0隞亙����������閮剖����
        }

        if (_skillId == SHIELD) { // ������
            pc.sendPackets(new S_SkillIconShield(5, _getBuffIconDuration));
        }
        else if (_skillId == SHADOW_ARMOR) { // ���� ����
            pc.sendPackets(new S_SkillIconShield(3, _getBuffIconDuration));
        }
        else if (_skillId == DRESS_DEXTERITY) { // ��� ��������
            pc.sendPackets(new S_Dexup(pc, 2, _getBuffIconDuration));
        }
        else if (_skillId == DRESS_MIGHTY) { // ��� �����
            pc.sendPackets(new S_Strup(pc, 2, _getBuffIconDuration));
        }
        else if (_skillId == GLOWING_AURA) { // ������� ���
            pc.sendPackets(new S_SkillIconAura(113, _getBuffIconDuration));
        }
        else if (_skillId == SHINING_AURA) { // ������ ���
            pc.sendPackets(new S_SkillIconAura(114, _getBuffIconDuration));
        }
        else if (_skillId == BRAVE_AURA) { // ������ ���
            pc.sendPackets(new S_SkillIconAura(116, _getBuffIconDuration));
        }
        else if (_skillId == FIRE_WEAPON) { // ����� ����
            pc.sendPackets(new S_SkillIconAura(147, _getBuffIconDuration));
        }
        else if (_skillId == WIND_SHOT) { // ������ ������
            pc.sendPackets(new S_SkillIconAura(148, _getBuffIconDuration));
        }
        else if (_skillId == FIRE_BLESS) { // ����� ���
            pc.sendPackets(new S_SkillIconAura(154, _getBuffIconDuration));
        }
        else if (_skillId == STORM_EYE) { // ������ ��
            pc.sendPackets(new S_SkillIconAura(155, _getBuffIconDuration));
        }
        else if (_skillId == EARTH_BLESS) { // ��� ���
            pc.sendPackets(new S_SkillIconShield(7, _getBuffIconDuration));
        }
        else if (_skillId == BURNING_WEAPON) { // ����� ����
            pc.sendPackets(new S_SkillIconAura(162, _getBuffIconDuration));
        }
        else if (_skillId == SHADOW_FANG) { // ����� ����
            pc.sendPackets(new S_SkillIconAura(162, _getBuffIconDuration));
        }
        else if (_skillId == STORM_SHOT) { // ������ ������
            pc.sendPackets(new S_SkillIconAura(165, _getBuffIconDuration));
        }
        else if (_skillId == IRON_SKIN) { // ���� ���
            pc.sendPackets(new S_SkillIconShield(10, _getBuffIconDuration));
        }
        else if (_skillId == EARTH_SKIN) { // ��� ���
            pc.sendPackets(new S_SkillIconShield(6, _getBuffIconDuration));
        }
        else if (_skillId == PHYSICAL_ENCHANT_STR) { // ����� ��������TR
            pc.sendPackets(new S_Strup(pc, 5, _getBuffIconDuration));
        }
        else if (_skillId == PHYSICAL_ENCHANT_DEX) { // ����� ��������EX
            pc.sendPackets(new S_Dexup(pc, 5, _getBuffIconDuration));
        }
        else if ((_skillId == HASTE) || (_skillId == GREATER_HASTE)) { // �����������
            pc.sendPackets(new S_SkillHaste(pc.getId(), 1, _getBuffIconDuration));
            pc.broadcastPacket(new S_SkillHaste(pc.getId(), 1, 0));
        }
        else if ((_skillId == HOLY_WALK) || (_skillId == MOVING_ACCELERATION) || (_skillId == WIND_WALK)) { // �����������������������������
            pc.sendPackets(new S_SkillBrave(pc.getId(), 4, _getBuffIconDuration));
            pc.broadcastPacket(new S_SkillBrave(pc.getId(), 4, 0));
        }
        else if ((_skillId == SLOW) || (_skillId == MASS_SLOW) || (_skillId == ENTANGLE)) { // ��������������
            pc.sendPackets(new S_SkillHaste(pc.getId(), 2, _getBuffIconDuration));
            pc.broadcastPacket(new S_SkillHaste(pc.getId(), 2, 0));
        }
        else if (_skillId == IMMUNE_TO_HARM) {
            pc.sendPackets(new S_SkillIconGFX(40, _getBuffIconDuration));
        }
        else if (_skillId == WIND_SHACKLE) { // 憸其����
            pc.sendPackets(new S_SkillIconWindShackle(pc.getId(), _getBuffIconDuration));
            pc.broadcastPacket(new S_SkillIconWindShackle(pc.getId(), _getBuffIconDuration));
        }
        pc.sendPackets(new S_OwnCharStatus(pc));
    }

    public void sendIcon(L1PcInstance pc, int skillId, int buffIconDuration) {
        if (skillId == SHIELD) { // ������
            pc.sendPackets(new S_SkillIconShield(5, buffIconDuration));
        }
        else if (skillId == SHADOW_ARMOR) { // ���� ����
            pc.sendPackets(new S_SkillIconShield(3, buffIconDuration));
        }
        else if (skillId == DRESS_DEXTERITY) { // ��� ��������
            pc.sendPackets(new S_Dexup(pc, 2, buffIconDuration));
        }
        else if (skillId == DRESS_MIGHTY) { // ��� �����
            pc.sendPackets(new S_Strup(pc, 2, buffIconDuration));
        }
        else if (skillId == GLOWING_AURA) { // ������� ���
            pc.sendPackets(new S_SkillIconAura(113, buffIconDuration));
        }
        else if (skillId == SHINING_AURA) { // ������ ���
            pc.sendPackets(new S_SkillIconAura(114, buffIconDuration));
        }
        else if (skillId == BRAVE_AURA) { // ������ ���
            pc.sendPackets(new S_SkillIconAura(116, buffIconDuration));
        }
        else if (skillId == FIRE_WEAPON) { // ����� ����
            pc.sendPackets(new S_SkillIconAura(147, buffIconDuration));
        }
        else if (skillId == WIND_SHOT) { // ������ ������
            pc.sendPackets(new S_SkillIconAura(148, buffIconDuration));
        }
        else if (skillId == FIRE_BLESS) { // ����� ���
            pc.sendPackets(new S_SkillIconAura(154, buffIconDuration));
        }
        else if (skillId == STORM_EYE) { // ������ ��
            pc.sendPackets(new S_SkillIconAura(155, buffIconDuration));
        }
        else if (skillId == EARTH_BLESS) { // ��� ���
            pc.sendPackets(new S_SkillIconShield(7, buffIconDuration));
        }
        else if (skillId == BURNING_WEAPON) { // ����� ����
            pc.sendPackets(new S_SkillIconAura(162, buffIconDuration));
        }
        else if (skillId == STORM_SHOT) { // ������ ������
            pc.sendPackets(new S_SkillIconAura(165, buffIconDuration));
        }
        else if (skillId == IRON_SKIN) { // ���� ���
            pc.sendPackets(new S_SkillIconShield(10, buffIconDuration));
        }
        else if (skillId == EARTH_SKIN) { // ��� ���
            pc.sendPackets(new S_SkillIconShield(6, buffIconDuration));
        }
        else if (skillId == PHYSICAL_ENCHANT_STR) { // ����� ��������TR
            pc.sendPackets(new S_Strup(pc, 5, buffIconDuration));
        }
        else if (skillId == PHYSICAL_ENCHANT_DEX) { // ����� ��������EX
            pc.sendPackets(new S_Dexup(pc, 5, buffIconDuration));
        }
        else if ((skillId == HASTE) || (skillId == GREATER_HASTE)) { // �����������
            pc.sendPackets(new S_SkillHaste(pc.getId(), 1, buffIconDuration));
            pc.broadcastPacket(new S_SkillHaste(pc.getId(), 1, 0));
        }
        else if ((skillId == HOLY_WALK) || (skillId == MOVING_ACCELERATION) || (skillId == WIND_WALK)) { // �����������������������������
            pc.sendPackets(new S_SkillBrave(pc.getId(), 4, buffIconDuration));
            pc.broadcastPacket(new S_SkillBrave(pc.getId(), 4, 0));
        }
        else if (skillId == BLOODLUST) {
            pc.sendPackets(new S_SkillBrave(pc.getId(), 6, buffIconDuration));
            pc.broadcastPacket(new S_SkillBrave(pc.getId(), 6, 0));
        }
        else if ((skillId == SLOW) || (skillId == MASS_SLOW) || (skillId == ENTANGLE)) { // ��������������
            pc.sendPackets(new S_SkillHaste(pc.getId(), 2, buffIconDuration));
            pc.broadcastPacket(new S_SkillHaste(pc.getId(), 2, 0));
        }
        else if (skillId == IMMUNE_TO_HARM) {
            pc.sendPackets(new S_SkillIconGFX(40, buffIconDuration));
        }
        else if (skillId == WIND_SHACKLE) { // 憸其����
            pc.sendPackets(new S_SkillIconWindShackle(pc.getId(), buffIconDuration));
            pc.broadcastPacket(new S_SkillIconWindShackle(pc.getId(), buffIconDuration));
        }
        else if (skillId == BURNING_SPIRIT)
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
            return; // 銵函內�������������
        }
        int[] data = null;

        if (_user instanceof L1PcInstance) {

            int targetid = 0;
            if (_skillId != FIRE_WALL) {
                targetid = _target.getId();
            }
            L1PcInstance pc = (L1PcInstance) _user;

            switch(_skillId) {
                case FIRE_WALL: // ��
                case LIFE_STREAM: // 瘝餌���◢�
                case ELEMENTAL_FALL_DOWN: // 撘勗�惇��
                    if (_skillId == FIRE_WALL) {
                        pc.setHeading(pc.targetDirection(_targetX, _targetY));
                        pc.sendPackets(new S_ChangeHeading(pc));
                        pc.broadcastPacket(new S_ChangeHeading(pc));
                    }
                    S_DoActionGFX gfx = new S_DoActionGFX(pc.getId(), _actid);
                    pc.sendPackets(gfx);
                    pc.broadcastPacket(gfx);
                    return;
                case SHOCK_STUN: // 銵����
                    if (_targetList.isEmpty()) { // 憭望��
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
                case LIGHT: // �����
                    pc.sendPackets(new S_Sound(145));
                    break;
                case MIND_BREAK: // 敹�憯�
                case JOY_OF_PAIN: // ����迭���
                    data = new int[] {_actid, _dmg, 0}; // data = {actid, dmg, effect}
                    pc.sendPackets(new S_AttackPacket(pc, targetid, data));
                    pc.broadcastPacket(new S_AttackPacket(pc, targetid, data));
                    pc.sendPackets(new S_SkillSound(targetid, _gfxid));
                    pc.broadcastPacket(new S_SkillSound(targetid, _gfxid));
                    return;
                case CONFUSION: // 瘛瑚��
                    data = new int[] {_actid, _dmg, 0}; // data = {actid, dmg, effect}
                    pc.sendPackets(new S_AttackPacket(pc, targetid, data));
                    pc.broadcastPacket(new S_AttackPacket(pc, targetid, data));
                    return;
                case SMASH: // ����
                    pc.sendPackets(new S_SkillSound(targetid, _gfxid));
                    pc.broadcastPacket(new S_SkillSound(targetid, _gfxid));
                    return;
                case TAMING_MONSTER: // 餈琿��
                    pc.sendPackets(new S_EffectLocation(_targetX, _targetY, _gfxid));
                    pc.broadcastPacket(new S_EffectLocation(_targetX, _targetY, _gfxid));
                    return;
                default:
                    break;
            }

            if (_targetList.isEmpty() && !(_skill.getTarget().equals("none"))) {
                // ���������撖曇情���������������蝙���������”蝷箝�蝯��
                int tempchargfx = _player.getTempCharGfx();
                if ((tempchargfx == 5727) || (tempchargfx == 5730)) { // ����蝟餃�澈������撖曉��
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
                if (isPcSummonPet(_target)) { // �璅摰嗚�秘����
                    if ((_player.getZoneType() == 1) || (_target.getZoneType() == 1)
                            || _player.checkNonPvP(_player, _target)) { // Non-PvP閮剖��
                        data = new int[] {_actid, 0, _gfxid, 6};
                        _player.sendPackets(new S_UseAttackSkill(_player, _target.getId(), _targetX, _targetY, data));
                        _player.broadcastPacket(new S_UseAttackSkill(_player, _target.getId(), _targetX, _targetY, data));
                        return;
                    }
                }

                if (getSkillArea() == 0) { // �擃�����
                    data = new int[] {_actid, _dmg, _gfxid, 6};
                    _player.sendPackets(new S_UseAttackSkill(_player, targetid, _targetX, _targetY, data));
                    _player.broadcastPacket(new S_UseAttackSkill(_player, targetid, _targetX, _targetY, data));
                    _target.broadcastPacketExceptTargetSight(new S_DoActionGFX(targetid, ActionCodes.ACTION_Damage), _player);
                }
                else { // �����������
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
            else if (_skill.getTarget().equals("none") && (_skill.getType() == L1Skills.TYPE_ATTACK)) { // �����������
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
            else { // 鋆擳��
                // ������������邦����誑憭�
                if ((_skillId != TELEPORT) && (_skillId != MASS_TELEPORT) && (_skillId != TELEPORT_TO_MATHER)) {
                    // �瘜���
                    if (isSkillAction) {
                        S_DoActionGFX gfx = new S_DoActionGFX(_player.getId(), _skill.getActionId());
                        _player.sendPackets(gfx);
                        _player.broadcastPacket(gfx);
                    }
                    // 擳������������� 擳�����頨恍＊蝷�
                    if ((_skillId == COUNTER_MAGIC) || (_skillId == COUNTER_BARRIER) || (_skillId == COUNTER_MIRROR)) {
                        S_DoActionGFX gfx = new S_DoActionGFX(_player.getId(), _skill.getActionId());
                        _player.sendPackets(gfx);
                        _player.sendPackets(new S_SkillSound(targetid, _gfxid));
                    }
                    else if ((_skillId == AWAKEN_ANTHARAS // 閬������
                    )
                            || (_skillId == AWAKEN_FAFURION // 閬������
                    ) || (_skillId == AWAKEN_VALAKAS)) { // 閬������
                        if (_skillId == _player.getAwakeSkillId()) { // ������圾�����������
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

                // ����������”蝷箝�����������������扼������������縑
                for (TargetStatus ts : _targetList) {
                    L1Character cha = ts.getTarget();
                    if (cha instanceof L1PcInstance) {
                        L1PcInstance chaPc = (L1PcInstance) cha;
                        chaPc.sendPackets(new S_OwnCharStatus(chaPc));
                    }
                }
            }
        }
        else if (_user instanceof L1NpcInstance) { // NPC������蝙������
            int targetid = _target.getId();

            if (_user instanceof L1MerchantInstance) {
                _user.broadcastPacket(new S_SkillSound(targetid, _gfxid));
                return;
            }

            if (_skillId == CURSE_PARALYZE || _skillId == WEAKNESS || _skillId == DISEASE) { // �銋�����摹��������
                _user.setHeading(_user.targetDirection(_targetX, _targetY)); // �霈���
                _user.broadcastPacket(new S_ChangeHeading(_user));
            }

            if (_targetList.isEmpty() && !(_skill.getTarget().equals("none"))) {
                // ���������撖曇情���������������蝙���������”蝷箝�蝯��
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
            else if (_skill.getTarget().equals("none") && (_skill.getType() == L1Skills.TYPE_ATTACK)) { // �����������
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

                        { FIRE_WEAPON, WIND_SHOT, FIRE_BLESS, STORM_EYE, BURNING_WEAPON, STORM_SHOT, EFFECT_BLESS_OF_MAZU },
                        { SHIELD, SHADOW_ARMOR, EARTH_SKIN, EARTH_BLESS, IRON_SKIN },
                        { STATUS_BRAVE, STATUS_ELFBRAVE, HOLY_WALK, MOVING_ACCELERATION, WIND_WALK, STATUS_BRAVE2 },
                        { HASTE, GREATER_HASTE, STATUS_HASTE },
                        { SLOW , MASS_SLOW , ENTANGLE },
                        { PHYSICAL_ENCHANT_DEX, DRESS_DEXTERITY },
                        { PHYSICAL_ENCHANT_STR, DRESS_MIGHTY },
                        { GLOWING_AURA, SHINING_AURA },
                        { MIRROR_IMAGE, UNCANNY_DODGE } };

        for (int[] skills : repeatedSkills)
        {
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
        if (_skill.getReuseDelay() > 0)
        {
            L1SkillDelay.onSkillUse(_user, _skill.getReuseDelay());
        }
    }

    private void runSkill() {
        //initialize the buff duration -[John]
        _getBuffIconDuration = _skill.getBuffDuration() * 1000;
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
            case FIRE_WALL: // ��
                L1EffectSpawn.getInstance().doSpawnFireWall(_user, _targetX, _targetY);
                return;
            case TRUE_TARGET: // 蝎暹�璅�
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

        if ((_skillId == SHOCK_STUN) && (_user instanceof L1PcInstance)) {
            _target.onAction(_player);
        }
        
        
        if ((_skillId == EARTH_BIND) && (_user instanceof L1PcInstance)) {
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
                    continue; // 閮����������
                }

                L1Magic _magic = new L1Magic(_user, cha);
                _magic.setLeverage(getLeverage());

                if (cha instanceof L1MonsterInstance) { // 銝香靽�
                    undeadType = ((L1MonsterInstance) cha).getNpcTemplate().get_undead();
                }

                // 蝣箇�頂����憭望��Ⅱ摰�������
                if (((_skill.getType() == L1Skills.TYPE_CURSE) || (_skill.getType() == L1Skills.TYPE_PROBABILITY)) && isTargetFailure(cha)) {
                    iter.remove();
                    continue;
                }

                if (cha instanceof L1PcInstance) { // �������C�����������縑�����
                    if (_skillTime == 0) {
                        _getBuffIconDuration = _skill.getBuffDuration();
                    }
                    else {
                        _getBuffIconDuration = _skillTime; // ������time���0隞亙����������閮剖����
                    }
                }

                deleteRepeatedSkills(cha); // ���瘜�����������

                if ((_skill.getType() == L1Skills.TYPE_ATTACK) && (_user.getId() != cha.getId())) { // ���頂���嚗������蝙��誑憭������
                    if (isUseCounterMagic(cha)) { // ������������������������
                        iter.remove();
                        continue;
                    }
                    dmg = _magic.calcMagicDamage(_skillId);
                    _dmg = dmg;
                    cha.removeSkillEffect(ERASE_MAGIC); // ��������銝准�������閫��
                }
                else if ((_skill.getType() == L1Skills.TYPE_CURSE) || (_skill.getType() == L1Skills.TYPE_PROBABILITY)) { // 蝣箇�頂���
                    isSuccess = _magic.calcProbabilityMagic(_skillId);
                    if (_skillId != ERASE_MAGIC) {
                        cha.removeSkillEffect(ERASE_MAGIC); // ��������銝准���Ⅱ����閫��
                    }
                    if (_skillId != FOG_OF_SLEEPING) {
                        cha.removeSkillEffect(FOG_OF_SLEEPING); // �����������銝准���Ⅱ����閫��
                    }
                    if (isSuccess) { // �����������������������������
                        if (isUseCounterMagic(cha)) { // �����������������
                            iter.remove();
                            continue;
                        }
                    }
                    else { // 憭望������������
                        if ((_skillId == FOG_OF_SLEEPING) && (cha instanceof L1PcInstance)) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.sendPackets(new S_ServerMessage(297)); // 雿�死鈭凝�����
                        }
                        iter.remove();
                        continue;
                    }
                }
                // 瘝餌��折���
                else if (_skill.getType() == L1Skills.TYPE_HEAL) {
                    // ��儔���
                    dmg = -1 * _magic.calcHealing(_skillId);
                    if (cha.hasSkillEffect(WATER_LIFE)) { // 瘞港��除-���� 2��
                        dmg *= 2;
                        cha.killSkillEffectTimer(WATER_LIFE); // ������甈�
                        if (cha instanceof L1PcInstance) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.sendPackets(new S_SkillIconWaterLife());
                        }
                    }
                    if (cha.hasSkillEffect(POLLUTE_WATER)) { // 瘙��偌-������
                        dmg /= 2;
                    }
                }
                // 憿舐內��������������
                else if ((_skillId == FIRE_BLESS || _skillId == STORM_EYE // ���除��憸其�
                        || _skillId == EARTH_BLESS // 憭批�����
                        || _skillId == GLOWING_AURA // 瞈��憯急除
                        || _skillId == SHINING_AURA || _skillId == BRAVE_AURA) // ��憯急除���ㄚ瘞�
                        && _user.getId() != cha.getId()) {
                    if (cha instanceof L1PcInstance) {
                        L1PcInstance _targetPc = (L1PcInstance) cha;
                        _targetPc.sendPackets(new S_SkillSound(_targetPc.getId(), _skill.getCastGfx()));
                        _targetPc.broadcastPacket(new S_SkillSound(_targetPc.getId(), _skill.getCastGfx()));
                    }
                }

                // ������ ��������������������� ������

                // �鈭���疝擃�憯�������������������內��
                if (cha.hasSkillEffect(_skillId) && (_skillId != SHOCK_STUN && _skillId != EARTH_BIND && _skillId != BONE_BREAK && _skillId != CONFUSION && _skillId != THUNDER_GRAB)) {
                    addMagicList(cha, true); // 擳���歇摮���
                    if (_skillId != SHAPE_CHANGE) { // �鈭�耦銵���
                        continue;
                    }
                }

                switch(_skillId) {
                    // �����
                    case HASTE:
                        if (cha.getMoveSpeed() != 2) { // ���銝凋誑憭�
                            if (cha instanceof L1PcInstance) {
                                L1PcInstance pc = (L1PcInstance) cha;
                                if (pc.getHasteItemEquipped() > 0) {
                                    continue;
                                }
                                pc.setDrink(false);
                                pc.sendPackets(new S_SkillHaste(pc.getId(), 1, _getBuffIconDuration));
                            }
                            cha.broadcastPacket(new S_SkillHaste(cha.getId(), 1, 0));
                            cha.setMoveSpeed(1);
                        }
                        else { // ���銝�
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
                    // 撘瑕�����
                    case GREATER_HASTE:
                        if (cha instanceof L1PcInstance) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            if (pc.getHasteItemEquipped() > 0) {
                                continue;
                            }
                            if (pc.getMoveSpeed() != 2) { // ���銝凋誑憭�
                                pc.setDrink(false);
                                pc.setMoveSpeed(1);
                                pc.sendPackets(new S_SkillHaste(pc.getId(), 1, _getBuffIconDuration));
                                pc.broadcastPacket(new S_SkillHaste(pc.getId(), 1, 0));
                            }
                            else { // ���銝�
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
                    // 蝺拚�����楨��������
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
                    // 撖����銵�擛潔�
                    case CHILL_TOUCH:
                    case VAMPIRIC_TOUCH:
                        heal = dmg;
                        break;
                    // 鈭�����惇
                    case ICE_LANCE_COCKATRICE:
                        // ��������惇
                    case ICE_LANCE_BASILISK:
                        // �瘥�惇��憸園◢������
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
                    // 憭批撅��
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
                    case 20011: // 瘥-�� 3X3
                        _user.setHeading(_user.targetDirection(_targetX, _targetY)); // �霈���
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
                    // 銵����
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
                            stunTime = randInt(4000,6000);
                        } else if (chance > 85) {
                            stunTime = randInt(3500,5500);
                        } else if (chance > 80) {
                            stunTime = randInt(3000,5000);
                        } else if (chance > 75) {
                            stunTime = randInt(2500,4500);
                        } else if (chance > 70) {
                            stunTime = randInt(2000,4000);
                        } else if (chance > 65) {
                            stunTime = randInt(1500,3500);
                        } else if (chance > 60) {
                            stunTime = randInt(1000,3000);
                        } else if (chance > 55) {
                            stunTime = randInt(500,2500);
                        } else if (chance > 50) {
                            stunTime = randInt(500,2000);
                        } else if (chance > 45) {
                            stunTime = randInt(500,1500);
                        } else {
                            stunTime = randInt(0,500);
                        }


                        float rounding;
                        rounding = ((float)stunTime/1000);
                        rounding = (float)(Math.ceil(rounding * 2) / 2);
                        stunTime = (int)(rounding * 1000);


                        _shockStunDuration = stunTime;
                        if (cha instanceof L1PcInstance) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_STUN, true));
                        }
                        else if (cha instanceof L1MonsterInstance || cha instanceof L1SummonInstance || cha instanceof L1PetInstance)
                        {
                            L1NpcInstance npc = (L1NpcInstance) cha;
                            npc.setParalyzed(true);
                            npc.setParalysisTime(_shockStunDuration);
                        }
                        break;
                    // 憟芸銋
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
                    // 韏瑟香�����
                    case TURN_UNDEAD:
                        if (undeadType == 1 || undeadType == 3){
                            dmg = cha.getCurrentHp();
                        }
                        break;
                    // Armor Break should only have effect on players - [Hank]
                    case FINAL_BURN:
                    	isSuccess = _magic.calcProbabilityMagic(_skillId);
                    	if(isSuccess){
                            if (cha instanceof L1PcInstance) {
                                if (!cha.hasSkillEffect(FINAL_BURN)) {
                                	L1PcInstance pc = (L1PcInstance) cha;
                            		pc.sendPackets(new S_SkillIconGFX(74, 3));
                                	cha.setSkillEffect(FINAL_BURN, 8 * 1000);
                                }
                                // no effect on NPC
                                else{
                                }
                            }
                            else{
                            	L1PcInstance pc = (L1PcInstance) cha;
                            	_player.sendPackets(new S_ServerMessage(280));
                            }
                    	}
                    case MANA_DRAIN:
                        int manachance = Random.nextInt(10) + 5;
                        drainMana = manachance + (_user.getInt() / 2);
                        if (cha.getCurrentMp() < drainMana) {
                            drainMana = cha.getCurrentMp();
                        }
                        break;
                    // ����������
                    case TELEPORT:
                    case MASS_TELEPORT:
                        if (cha instanceof L1PcInstance) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            L1BookMark bookm = pc.getBookMark(_bookmarkId);
                            if (bookm != null) { // ��������������������
                                if (pc.getMap().isEscapable() || pc.isGm()) {
                                    int newX = bookm.getLocX();
                                    int newY = bookm.getLocY();
                                    short mapId = bookm.getMapId();

                                    if (_skillId == MASS_TELEPORT) { // ���������
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
                            else { // ���������������������遙�����������������
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
                                    pc.sendPackets(new S_ServerMessage(276)); // \f1�甇斤瘜蝙�����
                                    pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, true));
                                }
                            }
                        }
                        break;
                    // ������
                    case CALL_CLAN:
                        if (cha instanceof L1PcInstance) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            L1PcInstance clanPc = (L1PcInstance) L1World.getInstance().findObject(_targetID);
                            if (clanPc != null) {
                                clanPc.setTempID(pc.getId());
                                clanPc.sendPackets(new S_Message_YN(729, "")); // ��蜓甇������������������(Y/N)
                            }
                        }
                        break;
                    // �霅瑞���
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
                                    // ������蔣�����宏���甇文�瘜蝙����宏����
                                    pc.sendPackets(new S_ServerMessage(647));
                                    pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, true));
                                }
                            }
                        }
                        break;
                    // 撘瑕�����耦
                    case COUNTER_DETECTION:
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
                    // ����郎�
                    case CREATE_MAGICAL_WEAPON:
                        if (cha instanceof L1PcInstance) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            L1ItemInstance item = pc.getInventory().getItem(_itemobjid);
                            if ((item != null) && (item.getItem().getType2() == 1)) {
                                int item_type = item.getItem().getType2();
                                int safe_enchant = item.getItem().get_safeenchant();
                                int enchant_level = item.getEnchantLevel();
                                String item_name = item.getName();
                                if (safe_enchant < 0) { // 撘瑕��
                                    pc.sendPackets( // \f1雿�絲����������
                                            new S_ServerMessage(79));
                                }
                                else if (safe_enchant == 0) { // 摰���+0
                                    pc.sendPackets( // \f1雿�絲����������
                                            new S_ServerMessage(79));
                                }
                                else if ((item_type == 1) && (enchant_level == 0)) {
                                    if (!item.isIdentified()) {// �����
                                        pc.sendPackets( // \f1%0���%2%1�������
                                                new S_ServerMessage(161, item_name, "$245", "$247"));
                                    }
                                    else {
                                        item_name = "+0 " + item_name;
                                        pc.sendPackets( // \f1%0���%2%1�������
                                                new S_ServerMessage(161, "+0 " + item_name, "$245", "$247"));
                                    }
                                    item.setEnchantLevel(1);
                                    pc.getInventory().updateItem(item, L1PcInventory.COL_ENCHANTLVL);
                                }
                                else {
                                    pc.sendPackets( // \f1雿�絲����������
                                            new S_ServerMessage(79));
                                }
                            }
                            else {
                                pc.sendPackets( // \f1雿�絲����������
                                        new S_ServerMessage(79));
                            }
                        }
                        break;
                    // ����
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
                                        pc.sendPackets(new S_ServerMessage(403, "$2475")); // �敺�%0%o ��
                                    } else {
                                        pc.sendPackets(new S_ServerMessage(280)); // \f1���仃����
                                    }
                                } else if (item.getItem().getItemId() == 40321) {
                                    pc.getInventory().removeItem(item, 1);
                                    if (brave >= run) {
                                        pc.getInventory().storeItem(40322, 1);
                                        pc.sendPackets(new S_ServerMessage(403, "$2476")); // �敺�%0%o ��
                                    } else {
                                        pc.sendPackets(new S_ServerMessage(280));// \f1���仃����
                                    }
                                } else if (item.getItem().getItemId() == 40322) {
                                    pc.getInventory().removeItem(item, 1);
                                    if (wise >= run) {
                                        pc.getInventory().storeItem(40323, 1);
                                        pc.sendPackets(new S_ServerMessage(403, "$2477")); // �敺�%0%o ��
                                    } else {
                                        pc.sendPackets(new S_ServerMessage(280));// \f1���仃����
                                    }
                                } else if (item.getItem().getItemId() == 40323) {
                                    pc.getInventory().removeItem(item, 1);
                                    if (kayser >= run) {
                                        pc.getInventory().storeItem(40324, 1);
                                        pc.sendPackets(new S_ServerMessage(403, "$2478")); // �敺�%0%o ��
                                    } else {
                                        pc.sendPackets(new S_ServerMessage(280));// \f1���仃����
                                    }
                                }
                            }
                        }
                        break;
                    // �����
                    case LIGHT:
                        break;
                    // ��蔣銋��
                    case SHADOW_FANG:
                        if (cha instanceof L1PcInstance) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            L1ItemInstance item = pc.getInventory().getItem(_itemobjid);
                            if ((item != null) && (item.getItem().getType2() == 1)) {
                                item.setSkillWeaponEnchant(pc, _skillId, _skill.getBuffDuration() * 1000);
                                pc.sendPackets(new S_SkillIconAura(162, _skill.getBuffDuration()));
                            }
                            else {
                                pc.sendPackets(new S_ServerMessage(79));
                            }
                        }
                        break;
                    // �隡潮��郎�
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
                    // 蟡�郎������郎�
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
                    // ��霅瑟��
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

                if (cha instanceof L1PcInstance) { // ���頨怎����
                    L1PcInstance pc = (L1PcInstance) cha;
                    pc.turnOnOffLight();
                    pc.sendPackets(new S_OwnCharAttrDef(pc));
                    pc.sendPackets(new S_OwnCharStatus(pc));
                    sendHappenMessage(pc); // ��������������縑
                }

                addMagicList(cha, false); // ������擳�������身摰�

                if (cha instanceof L1PcInstance) { // �������C�����������
                    L1PcInstance pc = (L1PcInstance) cha;
                    pc.turnOnOffLight();
                }
            }

            // 閫���頨�
            if ((_skillId == DETECTION) || (_skillId == COUNTER_DETECTION)) { // �����耦�撥������耦
                detection(_player);
            }

        }
        catch (Exception e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        }
    }

    private void detection(L1PcInstance pc) {
        if (!pc.isGmInvis() && pc.isInvisble()) { // �撌梢頨思葉
            pc.delInvis();
            pc.beginInvisTimer();
        }

        for (L1PcInstance tgt : L1World.getInstance().getVisiblePlayer(pc)) { // ����隞頨怨��
            if (!tgt.isGmInvis() && tgt.isInvisble()) {
                tgt.delInvis();
            }
        }
        L1WorldTraps.getInstance().onDetection(pc);
    }

    public static int randInt(int min, int max) {

        // Usually this can be a field rather than a method variable
        Random rand = new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }

    private boolean isTargetCalc(L1Character cha) {
        // 銝���扇�����疝擃�憯�
        if ((_user instanceof L1PcInstance)
                && (_skillId == TRIPLE_ARROW || _skillId == FOE_SLAYER
                || _skillId == SMASH || _skillId == BONE_BREAK)) {
            return true;
        }
        // �����Non嚗vP�摰�
        if (_skill.getTarget().equals("attack") && (_skillId != 18)) { // ������
            if (isPcSummonPet(cha)) { // 撖曇情��C��������
                if ((_player.getZoneType() == 1) || (cha.getZoneType() == 1 // ������������������������
                ) || _player.checkNonPvP(_player, cha)) { // Non-PvP閮剖��
                    return false;
                }
            }
        }

        // ���������������頨怒撖曇情憭�
        if ((_skillId == FOG_OF_SLEEPING) && (_user.getId() == cha.getId())) {
            return false;
        }

        // ���������頨怒�������撖曇情憭�
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

        // �����������頨怒�撖曇情嚗������������������
        if (_skillId == MASS_TELEPORT) {
            if (_user.getId() != cha.getId()) {
                return false;
            }
        }

        return true;
    }

    private boolean isPcSummonPet(L1Character cha) {
        if (_calcType == PC_PC) { // 撖曇情��C
            return true;
        }

        if (_calcType == PC_NPC) {
            if (cha instanceof L1SummonInstance) { // 撖曇情����
                L1SummonInstance summon = (L1SummonInstance) cha;
                if (summon.isExsistMaster()) { // ���������
                    return true;
                }
            }
            if (cha instanceof L1PetInstance) { // 撖曇情������
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

        if ((cha instanceof L1TowerInstance) || (cha instanceof L1DoorInstance)) { // �������������蝣箇�頂�����
            return true;
        }

        if (cha instanceof L1PcInstance) { // 撖銷C�����
            if ((_calcType == PC_PC) && _player.checkNonPvP(_player, cha)) { // Non-PvP閮剖��
                L1PcInstance pc = (L1PcInstance) cha;
                if ((_player.getId() == pc.getId()) || ((pc.getClanid() != 0) && (_player.getClanid() == pc.getClanid()))) {
                    return false;
                }
                return true;
            }
            return false;
        }

        if (cha instanceof L1MonsterInstance) { // ������������摰�
            isTU = ((L1MonsterInstance) cha).getNpcTemplate().get_IsTU();
        }

        if (cha instanceof L1MonsterInstance) { // ������������摰�
            isErase = ((L1MonsterInstance) cha).getNpcTemplate().get_IsErase();
        }

        if (cha instanceof L1MonsterInstance) { // �������摰�
            undeadType = ((L1MonsterInstance) cha).getNpcTemplate().get_undead();
        }

        // �������������
        if (cha instanceof L1MonsterInstance) {
            isManaDrain = true;
        }
		/*
		 * ���憭隞塚��-U��������紋鞊～������������ ���憭隞塚��-U��������紋鞊～�������������
		 * ���憭隞塚����������������������������������������
		 * ���憭隞塚�������������������隞亙�����
		 */
        if (((_skillId == TURN_UNDEAD) && ((undeadType == 0) || (undeadType == 2)))
                || ((_skillId == TURN_UNDEAD) && (isTU == false))
                || (((_skillId == ERASE_MAGIC) || (_skillId == SLOW) || (_skillId == MANA_DRAIN) || (_skillId == MASS_SLOW) || (_skillId == ENTANGLE) || (_skillId == WIND_SHACKLE)) && (isErase == false))
                || ((_skillId == MANA_DRAIN) && (isManaDrain == false))) {
            return true;
        }
        return false;
    }

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
