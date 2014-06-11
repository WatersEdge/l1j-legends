package l1j.server.server.model;

import l1j.server.Config;
import l1j.server.server.ActionCodes;
import l1j.server.server.WarTimeController;
import l1j.server.server.command.executor.L1Summon;
import l1j.server.server.datatables.SkillsTable;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.model.Instance.L1DoorInstance;
import l1j.server.server.model.Instance.L1SummonInstance;
import l1j.server.server.model.gametime.L1GameTimeClock;
import l1j.server.server.model.npc.action.L1NpcDefaultAction;
import l1j.server.server.model.poison.L1DamagePoison;
import l1j.server.server.model.poison.L1ParalysisPoison;
import l1j.server.server.model.poison.L1SilencePoison;
import l1j.server.server.serverpackets.S_AttackMissPacket;
import l1j.server.server.serverpackets.S_AttackPacket;
import l1j.server.server.serverpackets.S_DoActionGFX;
import l1j.server.server.serverpackets.S_EffectLocation;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SkillIconGFX;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.serverpackets.S_UseArrowSkill;
import l1j.server.server.serverpackets.S_UseAttackSkill;
import l1j.server.server.templates.L1MagicDoll;
import l1j.server.server.templates.L1Skills;
import l1j.server.server.types.Point;
import l1j.server.server.utils.Random;
import java.util.Arrays;

import static l1j.server.server.model.skill.L1SkillId.*;

public class L1Attack {
    private L1PcInstance _pc = null;

    private L1Character _target = null;

    private L1PcInstance _targetPc = null;

    private L1NpcInstance _npc = null;

    private L1NpcInstance _targetNpc = null;

    private final int _targetId;

    private int _targetX;

    private int _targetY;

    private int _statusDamage = 0;

    private int _hitRate = 0;

    private int _calcType;

    private static final int PC_PC = 1;

    private static final int PC_NPC = 2;

    private static final int NPC_PC = 3;

    private static final int NPC_NPC = 4;

    private boolean _isHit = false;

    private int _damage = 0;

    private int _drainMana = 0;

    private int _drainHp = 0;

    private byte _effectId = 0;

    private int _attckGrfxId = 0;

    private int _attckActId = 0;

    private L1ItemInstance weapon = null;

    private int _weaponId = 0;

    private int _weaponType = 0;

    private int _weaponType2 = 0;

    private int _weaponAddHit = 0;

    private int _weaponAddDmg = 0;

    private int _weaponSmall = 0;

    private int _weaponLarge = 0;

    private int _weaponRange = 1;

    private int _weaponBless = 1;

    private int _weaponEnchant = 0;

    private int _weaponMaterial = 0;

    private int _weaponDoubleDmgChance = 0;

    private int _weaponAttrEnchantKind = 0;

    private int _weaponAttrEnchantLevel = 0;

    private L1ItemInstance _arrow = null;

    private L1ItemInstance _sting = null;

    private int _leverage = 10;

    private int _skillId;

    @SuppressWarnings("unused")
    private double _skillDamage = 0;

    public void setLeverage(int i) {
        _leverage = i;
    }

    private int getLeverage() {
        return _leverage;
    }

    private static final int[] PREVENT_DAMAGE = { ABSOLUTE_BARRIER, ICE_LANCE,
            FREEZING_BLIZZARD, FREEZING_BREATH, EARTH_BIND };






        /*
         * private static final int[] strHit = { -2, -2, -2, -2, -2, -2, -2,
         * 0ï½ž7ã�¾ã�§ -1, -1, 0, 0, 1, 1, 2, 2, 3, 3, 4, 4, 4, 5, 5, 5, 6, 6, 6,
         * 8ï½ž26ã�¾ã�§ 7, 7, 7, 8, 8, 8, 9, 9, 9, 10, 10, 10, 11, 11, 11, 12, 12, 12,
         * 27ï½ž44ã�¾ã�§ 13, 13, 13, 14, 14, 14, 15, 15, 15, 16, 16, 16, 17, 17, 17};
         * 45ï½ž59ã�¾ã�§
         *
         * private static final int[] dexHit = { -2, -2, -2, -2, -2, -2, -1, -1, 0,
         * 0,
         * 15, 16,
         * 30, 31,
         * 45, 46 };
         *
         * private static final int[] strDmg = new int[128];
         *
         * static {
         *
         * str = 23; str <= 28; str++) {
         * strDmg[str] = dmg; } for (int str = 29; str <= 32; str++) {
         * ï¼’ï¼™ï½žï¼“ï¼’ã�¯ï¼’æ¯Žã�«ï¼‹ï¼‘ if (str % 2 == 1) { dmg++; } strDmg[str] = dmg; } for (int
         * str = 33; str <= 39; str++) {
         * for (int str = 40; str <= 46; str++) {
         * strDmg[str] = dmg; } for (int str = 47; str <= 127; str++) {
         * ï¼”ï¼-ï½žï¼‘ï¼’ï¼-ã�¯ï¼‘æ¯Žã�«ï¼‹ï¼‘ dmg++; strDmg[str] = dmg; } }
         *
         * private static final int[] dexDmg = new int[128];
         *
         * static {
         * dexDmg[dex] = 0; } dexDmg[15] = 1; dexDmg[16] = 2; dexDmg[17] = 3;
         * dexDmg[18] = 4; dexDmg[19] = 4; dexDmg[20] = 4; dexDmg[21] = 5;
         * dexDmg[22] = 5; dexDmg[23] = 5; int dmg = 5; for (int dex = 24; dex <=
         * 127; dex++) {
         */

    private static final int[] strHit = { -2, -2, -2, -2, -2, -2, -2,
            -2, -1, -1, 0, 0, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 5, 6, 6, 6,
            7, 7, 7, 8, 8, 8, 9, 9, 9, 10, 10, 10, 11, 11, 11, 12, 12, 12,
            13, 13, 13, 14, 14, 14, 15, 15, 15, 16, 16, 16, 17, 17, 17 };

    private static final int[] dexHit = { -2, -2, -2, -2, -2, -2, -1, -1, 0, 0,
            1, 1, 2, 2, 3, 3, 4, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16,
            17, 18, 19, 19, 19, 20, 20, 20, 21, 21, 21, 22, 22, 22, 23,
            23, 23, 24, 24, 24, 25, 25, 25, 26, 26, 26, 27, 27, 27, 28 };

    private static final int[] strDmg = new int[128];

    static {
        int dmg = -6;
        for (int str = 0; str <= 22; str++) {
            if (str % 2 == 1) {
                dmg++;
            }
            strDmg[str] = dmg;
        }
        for (int str = 23; str <= 28; str++) {
            if (str % 3 == 2) {
                dmg++;
            }
            strDmg[str] = dmg;
        }
        for (int str = 29; str <= 32; str++) {
            if (str % 2 == 1) {
                dmg++;
            }
            strDmg[str] = dmg;
        }
        for (int str = 33; str <= 35; str++) {
            dmg++;
            strDmg[str] = dmg;
        }
        for (int str = 35; str <= 127; str++) {
            if (str % 4 == 1) {
                dmg++;
            }
            strDmg[str] = dmg;
        }
        Arrays.sort(PREVENT_DAMAGE);
    }

    private static final int[] dexDmg = new int[128];

    static {
        for (int dex = 0; dex <= 14; dex++) {
            dexDmg[dex] = 0;
        }
        dexDmg[15] = 1;
        dexDmg[16] = 2;
        dexDmg[17] = 3;
        dexDmg[18] = 4;
        dexDmg[19] = 4;
        dexDmg[20] = 4;
        dexDmg[21] = 5;
        dexDmg[22] = 5;
        dexDmg[23] = 5;
        int dmg = 5;
        for (int dex = 24; dex <= 35; dex++) {
            if (dex % 3 == 1) {
                dmg++;
            }
            dexDmg[dex] = dmg;
        }
        for (int dex = 36; dex <= 127; dex++) {
            if (dex % 4 == 1) {
                dmg++;
            }
            dexDmg[dex] = dmg;
        }
    }

    public void setActId(int actId) {
        _attckActId = actId;
    }

    public void setGfxId(int gfxId) {
        _attckGrfxId = gfxId;
    }

    public int getActId() {
        return _attckActId;
    }

    public int getGfxId() {
        return _attckGrfxId;
    }

    public L1Attack(L1Character attacker, L1Character target) {
        this(attacker, target, 0);
    }

    public L1Attack(L1Character attacker, L1Character target, int skillId) {
        _skillId = skillId;
        if (_skillId != 0) {
            L1Skills skills = SkillsTable.getInstance().getTemplate(_skillId);
            _skillDamage = skills.getDamageValue();
        }
        if (attacker instanceof L1PcInstance) {
            _pc = (L1PcInstance) attacker;
            if (target instanceof L1PcInstance) {
                _targetPc = (L1PcInstance) target;
                _calcType = PC_PC;
            } else if (target instanceof L1NpcInstance) {
                _targetNpc = (L1NpcInstance) target;
                _calcType = PC_NPC;
            }
            weapon = _pc.getWeapon();
            if (weapon != null) {
                _weaponId = weapon.getItem().getItemId();
                _weaponType = weapon.getItem().getType1();
                _weaponType2 = weapon.getItem().getType();
                _weaponAddHit = weapon.getItem().getHitModifier()
                        + weapon.getHitByMagic();
                _weaponAddDmg = weapon.getItem().getDmgModifier()
                        + weapon.getDmgByMagic();
                _weaponSmall = weapon.getItem().getDmgSmall();
                _weaponLarge = weapon.getItem().getDmgLarge();
                _weaponRange = weapon.getItem().getRange();
                _weaponBless = weapon.getItem().getBless();
                _weaponEnchant = weapon.getEnchantLevel();
                _weaponMaterial = weapon.getItem().getMaterial();
                _statusDamage = dexDmg[_pc.getDex()];

                if (_weaponType == 20) {
                    _arrow = _pc.getInventory().getArrow();
                    if (_arrow != null) {
                        _weaponBless = _arrow.getItem().getBless();
                        _weaponMaterial = _arrow.getItem().getMaterial();
                    }
                } else if (_weaponType == 62) {
                    _sting = _pc.getInventory().getSting();
                    if (_sting != null) {
                        _weaponBless = _sting.getItem().getBless();
                        _weaponMaterial = _sting.getItem().getMaterial();
                    }
                } else {
                    _weaponEnchant = weapon.getEnchantLevel()
                            - weapon.get_durability();
                    _statusDamage = strDmg[_pc.getStr()];
                }
                _weaponDoubleDmgChance = weapon.getItem().getDoubleDmgChance();
                _weaponAttrEnchantKind = weapon.getAttrEnchantKind();
                _weaponAttrEnchantLevel = weapon.getAttrEnchantLevel();
            }
        } else if (attacker instanceof L1NpcInstance) {
            _npc = (L1NpcInstance) attacker;
            if (target instanceof L1PcInstance) {
                _targetPc = (L1PcInstance) target;
                _calcType = NPC_PC;
            } else if (target instanceof L1NpcInstance) {
                _targetNpc = (L1NpcInstance) target;
                _calcType = NPC_NPC;
            }
        }
        _target = target;
        _targetId = target.getId();
        _targetX = target.getX();
        _targetY = target.getY();
    }

        /* â- â- â- â- â- â- â- â- â- â- â- â- â- â- â- â-  å‘½ä¸­åˆ¤å®š â- â- â- â- â- â- â- â- â- â- â- â- â- â- â- â-  */

    private static final int[] INVINCIBLE = { ABSOLUTE_BARRIER, ICE_LANCE,
            FREEZING_BLIZZARD, FREEZING_BREATH, EARTH_BIND,
            ICE_LANCE_COCKATRICE, ICE_LANCE_BASILISK };

    public boolean calcHit() {
        for (int skillId : INVINCIBLE) {
            if (_target.hasSkillEffect(skillId)) {
                _isHit = false;
                return _isHit;
            }
        }

        if ((_calcType == PC_PC) || (_calcType == PC_NPC)) {
            if (_weaponRange != -1) {
/*                              if (_pc.getMapId() >= 181 && _pc.getMapId() <= 190 && _weaponRange == 1 && !_pc.glanceCheck(_targetX, _targetY)) {
                                        _isHit = false;
                                        return _isHit;
                                } */
                if (_pc.getLocation()
                        .getTileLineDistance(_target.getLocation()) > _weaponRange + 1) {
                    _isHit = false;
                    return _isHit;
                }
            } else {
                if (!_pc.getLocation().isInScreen(_target.getLocation())) {
                    _isHit = false;
                    return _isHit;
                }
            }
            if ((_weaponType == 20) && (_weaponId != 190) && (_arrow == null)) {
                _isHit = false;
            } else if ((_weaponType == 62) && (_sting == null)) {
                _isHit = false;
            } else if (!_pc.glanceCheck(_targetX, _targetY) && !(_target instanceof L1DoorInstance)) {
                _isHit = false;
            } else if ((_weaponId == 247) || (_weaponId == 248)
                    || (_weaponId == 249)) {
                _isHit = false;
            } else if (_calcType == PC_PC) {
                _isHit = calcPcPcHit();
            } else if (_calcType == PC_NPC) {
                _isHit = calcPcNpcHit();
            }
        } else if (_calcType == NPC_PC) {
            _isHit = calcNpcPcHit();
        } else if (_calcType == NPC_NPC) {
            _isHit = calcNpcNpcHit();
        }
        return _isHit;
    }

    private int calShortRageHit(int hitRate) {
        int shortHit = hitRate + _pc.getHitup() + _pc.getOriginalHitup();
        shortHit += _pc.getHitModifierByArmor();

        if (_pc.hasSkillEffect(COOKING_2_0_N)
                || _pc.hasSkillEffect(COOKING_2_0_S))
            shortHit += 1;
        if (_pc.hasSkillEffect(COOKING_3_2_N)
                || _pc.hasSkillEffect(COOKING_3_2_S))
            shortHit += 2;
        return shortHit;
    }

    private int calLongRageHit(int hitRate) {
        int longHit = hitRate + _pc.getBowHitup() + _pc.getOriginalBowHitup();
        longHit += _pc.getBowHitModifierByArmor();

        if (_pc.hasSkillEffect(COOKING_2_3_N)
                || _pc.hasSkillEffect(COOKING_2_3_S)
                || _pc.hasSkillEffect(COOKING_3_0_N)
                || _pc.hasSkillEffect(COOKING_3_0_S))
            longHit += 1;
        return longHit;
    }

    /*
     * ï¼°ï¼£ã�¸ã�®å‘½ä¸­çŽ‡ ï¼�ï¼ˆPCã�®Lvï¼‹ã‚¯ãƒ©ã‚¹è£œæ­£ï¼‹STRè£œæ­£ï¼‹DEXè£œæ­£ï¼‹æ­¦å™¨è£œæ­£ï¼‹DAIã�®æžšæ•°/2ï¼‹é­”æ³•è£œæ­£ï¼‰Ã-0.68ï¼�10
     * ã�“ã‚Œã�§ç®-å‡ºã�•ã‚Œã�Ÿæ•°å€¤ã�¯è‡ªåˆ†ã�Œæœ€å¤§å‘½ä¸­(95%)ã‚’ä¸Žã�ˆã‚‹äº‹ã�®ã�§ã��ã‚‹ç›¸æ‰‹å�´PCã�®AC ã��ã�“ã�‹ã‚‰ç›¸æ‰‹å�´PCã�®ACã�Œ1è‰¯ã��ã�ªã‚‹æ¯Žã�«è‡ªå‘½ä¸­çŽ‡ã�‹ã‚‰1å¼•ã�„ã�¦ã�„ã��
     * æœ€å°�å‘½ä¸­çŽ‡5% æœ€å¤§å‘½ä¸­çŽ‡95%
     */
    private boolean calcPcPcHit() {
        _hitRate = _pc.getLevel();

        if (_pc.getStr() > 59) {
            _hitRate += strHit[58];
        } else {
            _hitRate += strHit[_pc.getStr() - 1];
        }

        if (_pc.getDex() > 60) {
            _hitRate += dexHit[59];
        } else {
            _hitRate += dexHit[_pc.getDex() - 1];
        }

        _hitRate += _weaponAddHit + (_weaponEnchant / 2);
        if (_weaponType == 20 || _weaponType == 62)
            _hitRate = calLongRageHit(_hitRate);
        else
            _hitRate = calShortRageHit(_hitRate);

        if ((80 < _pc.getInventory().getWeight242()
        )
                && (121 >= _pc.getInventory().getWeight242())) {
            _hitRate -= 1;
        } else if ((122 <= _pc.getInventory().getWeight242())
                && (160 >= _pc.getInventory().getWeight242())) {
            _hitRate -= 3;
        } else if ((161 <= _pc.getInventory().getWeight242())
                && (200 >= _pc.getInventory().getWeight242())) {
            _hitRate -= 5;
        }

        int attackerDice = Random.nextInt(20) + 1 + _hitRate - 10;

        attackerDice -= _targetPc.getDodge();
        attackerDice += _targetPc.getNdodge();

        int defenderDice = 0;

        int defenderValue = (int) (_targetPc.getAc() * 1.5) * -1;

        if (_targetPc.getAc() >= 0) {
            defenderDice = 10 - _targetPc.getAc();
        } else if (_targetPc.getAc() < 0) {
            defenderDice = 10 + Random.nextInt(defenderValue) + 1;
        }

        int fumble = _hitRate - 9;
        int critical = _hitRate + 10;

        if (attackerDice <= fumble) {
            _hitRate = 0;
        } else if (attackerDice >= critical) {
            _hitRate = 100;
        } else {
            if (attackerDice > defenderDice) {
                _hitRate = 100;
            } else if (attackerDice <= defenderDice) {
                _hitRate = 0;
            }
        }

        if (_weaponType2 == 17 || _weaponType2 == 19) {
            _hitRate = 100;
        }

        else if (L1MagicDoll.getDamageEvasionByDoll(_targetPc) > 0) {
            _hitRate = 0;
        }

        int rnd = Random.nextInt(100) + 1;
        if ((_weaponType == 20) && (_hitRate > rnd)) {
            return calcErEvasion();
        }

        return _hitRate >= rnd;

                /*
                 * final int MIN_HITRATE = 5;
                 *
                 * _hitRate = _pc.getLevel();
                 *
                 * if (_pc.getStr() > 39) { _hitRate += strHit[39]; } else { _hitRate +=
                 * strHit[_pc.getStr()]; }
                 *
                 * if (_pc.getDex() > 39) { _hitRate += dexHit[39]; } else { _hitRate +=
                 * dexHit[_pc.getDex()]; }
                 *
                 * if (_weaponType != 20 && _weaponType != 62) { _hitRate +=
                 * _weaponAddHit + _pc.getHitup() + _pc.getOriginalHitup() +
                 * (_weaponEnchant / 2); } else { _hitRate += _weaponAddHit +
                 * _pc.getBowHitup() + _pc .getOriginalBowHitup() + (_weaponEnchant /
                 * 2); }
                 *
                 * if (_weaponType != 20 && _weaponType != 62) {
                 * += _pc.getHitModifierByArmor(); } else { _hitRate +=
                 * _pc.getBowHitModifierByArmor(); }
                 *
                 * int hitAc = (int) (_hitRate * 0.68 - 10) * -1;
                 *
                 * if (hitAc <= _targetPc.getAc()) { _hitRate = 95; } else { _hitRate =
                 * 95 - (hitAc - _targetPc.getAc()); }
                 *
                 * if (_targetPc.hasSkillEffect(UNCANNY_DODGE)) { _hitRate -= 20; }
                 *
                 * if (_targetPc.hasSkillEffect(MIRROR_IMAGE)) { _hitRate -= 20; }
                 *
                 * if (_pc.hasSkillEffect(COOKING_2_0_N)
                 * _pc.hasSkillEffect(COOKING_2_0_S)) { if (_weaponType != 20 &&
                 * _weaponType != 62) { _hitRate += 1; } } if
                 * (_pc.hasSkillEffect(COOKING_3_2_N)
                 * _pc.hasSkillEffect(COOKING_3_2_S)) { if (_weaponType != 20 &&
                 * _weaponType != 62) { _hitRate += 2; } } if
                 * (_pc.hasSkillEffect(COOKING_2_3_N)
                 * _pc.hasSkillEffect(COOKING_2_3_S) ||
                 * _pc.hasSkillEffect(COOKING_3_0_N) ||
                 * _pc.hasSkillEffect(COOKING_3_0_S)) { if (_weaponType == 20 ||
                 * _weaponType == 62) { _hitRate += 1; } }
                 *
                 * if (_hitRate < MIN_HITRATE) { _hitRate = MIN_HITRATE; }
                 *
                 * if (_weaponType2 == 17) { _hitRate = 100;
                 *
                 * if (_targetPc.hasSkillEffect(ABSOLUTE_BARRIER)) { _hitRate = 0; } if
                 * (_targetPc.hasSkillEffect(ICE_LANCE)) { _hitRate = 0; } if
                 * (_targetPc.hasSkillEffect(FREEZING_BLIZZARD)) { _hitRate = 0; } if
                 * (_targetPc.hasSkillEffect(FREEZING_BREATH)) { _hitRate = 0; } if
                 * (_targetPc.hasSkillEffect(EARTH_BIND)) { _hitRate = 0; } int rnd =
                 * Random.nextInt(100) + 1; if (_weaponType == 20 && _hitRate > rnd) {
                 *
                 *
                 * return _hitRate >= rnd;
                 */
    }

    private boolean calcPcNpcHit() {
        _hitRate = _pc.getLevel();

        if (_pc.getStr() > 59) {
            _hitRate += strHit[58];
        } else {
            _hitRate += strHit[_pc.getStr() - 1];
        }

        if (_pc.getDex() > 60) {
            _hitRate += dexHit[59];
        } else {
            _hitRate += dexHit[_pc.getDex() - 1];
        }

        _hitRate += _weaponAddHit + (_weaponEnchant / 2);
        if (_weaponType == 20 || _weaponType == 62)
            _hitRate = calLongRageHit(_hitRate);
        else
            _hitRate = calShortRageHit(_hitRate);

        if ((80 < _pc.getInventory().getWeight242()
        )
                && (121 >= _pc.getInventory().getWeight242())) {
            _hitRate -= 1;
        } else if ((122 <= _pc.getInventory().getWeight242())
                && (160 >= _pc.getInventory().getWeight242())) {
            _hitRate -= 3;
        } else if ((161 <= _pc.getInventory().getWeight242())
                && (200 >= _pc.getInventory().getWeight242())) {
            _hitRate -= 5;
        }

        int attackerDice = Random.nextInt(20) + 1 + _hitRate - 10;

        attackerDice -= _targetNpc.getDodge();
        attackerDice += _targetNpc.getNdodge();

        int defenderDice = 10 - _targetNpc.getAc();

        int fumble = _hitRate - 9;
        int critical = _hitRate + 10;

        if (attackerDice <= fumble) {
            _hitRate = 0;
        } else if (attackerDice >= critical) {
            _hitRate = 100;
        } else {
            if (attackerDice > defenderDice) {
                _hitRate = 100;
            } else if (attackerDice <= defenderDice) {
                _hitRate = 0;
            }
        }

        if (_weaponType2 == 17 || _weaponType2 == 19) {
            _hitRate = 100;
        }

        if (_pc.isAttackMiss(_pc, _targetNpc.getNpcTemplate().get_npcId())) {
            _hitRate = 0;
        }

        int rnd = Random.nextInt(100) + 1;

        return _hitRate >= rnd;
    }

    private boolean calcNpcPcHit() {

        _hitRate += _npc.getLevel();

        if (_npc instanceof L1PetInstance) {
            _hitRate += ((L1PetInstance) _npc).getHitByWeapon();
        }

        _hitRate += _npc.getHitup();

        int attackerDice = Random.nextInt(20) + 1 + _hitRate - 1;

        attackerDice -= _targetPc.getDodge();
        attackerDice += _targetPc.getNdodge();

        int defenderDice = 0;

        int defenderValue = (_targetPc.getAc()) * -1;

        if (_targetPc.getAc() >= 0) {
            defenderDice = 10 - _targetPc.getAc();
        } else if (_targetPc.getAc() < 0) {
            defenderDice = 10 + Random.nextInt(defenderValue) + 1;
        }

        int fumble = _hitRate;
        int critical = _hitRate + 19;

        if (attackerDice <= fumble) {
            _hitRate = 0;
        } else if (attackerDice >= critical) {
            _hitRate = 100;
        } else {
            if (attackerDice > defenderDice) {
                _hitRate = 100;
            } else if (attackerDice <= defenderDice) {
                _hitRate = 0;
            }
        }

        if ((_npc instanceof L1PetInstance)
                || (_npc instanceof L1SummonInstance)) {
            if ((_targetPc.getZoneType() == 1) || (_npc.getZoneType() == 1)
                    || (_targetPc.checkNonPvP(_targetPc, _npc))) {
                _hitRate = 0;
            }
        }
        else if (L1MagicDoll.getDamageEvasionByDoll(_targetPc) > 0) {
            _hitRate = 0;
        }

        int rnd = Random.nextInt(100) + 1;

        if ((_npc.getAtkRanged() >= 10)
                && (_hitRate > rnd)
                && (_npc.getLocation().getTileLineDistance(
                new Point(_targetX, _targetY)) >= 2)) {
            return calcErEvasion();
        }
        return _hitRate >= rnd;
    }

    private boolean calcNpcNpcHit() {

        _hitRate += _npc.getLevel();

        if (_npc instanceof L1PetInstance) {
            _hitRate += ((L1PetInstance) _npc).getHitByWeapon();
        }

        _hitRate += _npc.getHitup();

        int attackerDice = Random.nextInt(20) + 1 + _hitRate - 1;

        attackerDice -= _targetNpc.getDodge();
        attackerDice += _targetNpc.getNdodge();

        int defenderDice = 0;

        int defenderValue = (_targetNpc.getAc()) * -1;

        if (_targetNpc.getAc() >= 0) {
            defenderDice = 10 - _targetNpc.getAc();
        } else if (_targetNpc.getAc() < 0) {
            defenderDice = 10 + Random.nextInt(defenderValue) + 1;
        }

        int fumble = _hitRate;
        int critical = _hitRate + 19;

        if (attackerDice <= fumble) {
            _hitRate = 0;
        } else if (attackerDice >= critical) {
            _hitRate = 100;
        } else {
            if (attackerDice > defenderDice) {
                _hitRate = 100;
            } else if (attackerDice <= defenderDice) {
                _hitRate = 0;
            }
        }
        if (((_npc instanceof L1PetInstance) || (_npc instanceof L1SummonInstance))
                && ((_targetNpc instanceof L1PetInstance) || (_targetNpc instanceof L1SummonInstance))) {
            if ((_targetNpc.getZoneType() == 1) || (_npc.getZoneType() == 1)) {
                _hitRate = 0;
            }
        }

        int rnd = Random.nextInt(100) + 1;
        return _hitRate >= rnd;
    }

    private boolean calcErEvasion() {
        int er = _targetPc.getEr();

        int rnd = Random.nextInt(100) + 1;
        return er < rnd;
    }

        /* â- â- â- â- â- â- â- â- â- â- â- â- â- â- â-  ãƒ€ãƒ¡ãƒ¼ã‚¸ç®-å‡º â- â- â- â- â- â- â- â- â- â- â- â- â- â- â-  */

    public int calcDamage() {
        if (_calcType == PC_PC) {
            _damage = calcPcPcDamage();
        } else if (_calcType == PC_NPC) {
            _damage = calcPcNpcDamage();
            if (_pc.getDmgMessages()) {
                _pc.sendPackets(new S_SystemMessage("Damage Dealt:" + String.valueOf(_damage)));
            }
        } else if (_calcType == NPC_PC) {
            _damage = calcNpcPcDamage();
        } else if (_calcType == NPC_NPC) {
            _damage = calcNpcNpcDamage();
        }
        return _damage;
    }

    private int calcWeponDamage(int weaponMaxDamage) {
        int weaponDamage = Random.nextInt(weaponMaxDamage) + 1;
        if (_pc.hasSkillEffect(SOUL_OF_FLAME) && _weaponType != 20)
        {
            weaponDamage = weaponMaxDamage + Math.round((_pc.getLevel()-50)/2);
        }
        boolean darkElfWeapon = false ;
        if (_pc.isDarkelf() && (_weaponType == 58)) {
            darkElfWeapon = true ;
            if ((Random.nextInt(100) + 1) <= _weaponDoubleDmgChance) {
                weaponDamage = weaponMaxDamage;
            }
            if (weaponDamage == weaponMaxDamage) {
                _effectId = 2;
            }
        } else if (_weaponType == 20 || _weaponType == 62) {
            weaponDamage = 0;
        }

        weaponDamage +=  _weaponAddDmg + _weaponEnchant ;

        if (_calcType == PC_NPC)
            weaponDamage += calcMaterialBlessDmg();
        if (_weaponType == 54) {
            darkElfWeapon = true ;
            if ((Random.nextInt(100) + 1) <= _weaponDoubleDmgChance) {
                weaponDamage *= 2;
                _effectId = 4;
            }
        }
        weaponDamage += calcAttrEnchantDmg();

        if (darkElfWeapon && _pc.hasSkillEffect(DOUBLE_BRAKE))
            if ((Random.nextInt(100) + 1) <= 33)
                weaponDamage *= 2;

        return weaponDamage;
    }

    private double calLongRageDamage(double dmg) {
        double longdmg = dmg + _pc.getBowDmgup() + _pc.getOriginalBowDmgup();

        int add_dmg = 1;
        if (_weaponType == 20) {
            if (_arrow != null) {
                add_dmg = _arrow.getItem().getDmgSmall();
                if (_calcType == PC_NPC) {
                    if (_targetNpc.getNpcTemplate().get_size()
                            .equalsIgnoreCase("large"))
                        add_dmg = _arrow.getItem().getDmgLarge();
                    if (_targetNpc.getNpcTemplate().is_hard())
                        add_dmg /= 2;
                }
            } else if (_weaponId == 190)
                add_dmg = 15;
        } else if (_weaponType == 62) {
            add_dmg = _sting.getItem().getDmgSmall();
            if (_calcType == PC_NPC)
                if (_targetNpc.getNpcTemplate().get_size()
                        .equalsIgnoreCase("large"))
                    add_dmg = _sting.getItem().getDmgLarge();
        }

        if ( add_dmg > 0)
            longdmg += Random.nextInt(add_dmg) + 1;

        longdmg += _pc.getBowDmgModifierByArmor();

        if (_pc.hasSkillEffect(COOKING_2_3_N)
                || _pc.hasSkillEffect(COOKING_2_3_S)
                || _pc.hasSkillEffect(COOKING_3_0_N)
                || _pc.hasSkillEffect(COOKING_3_0_S))
            longdmg += 1;

        return longdmg;
    }

    private double calShortRageDamage(double dmg) {
        double shortdmg = dmg + _pc.getDmgup() + _pc.getOriginalDmgup();
        WeaknessExposure();
        shortdmg = calcBuffDamage(shortdmg);
        shortdmg += _pc.getDmgModifierByArmor();

        if (_weaponType == 0)
            shortdmg = (Random.nextInt(5) + 4) / 4;
        else if (_weaponType2 == 17 || _weaponType2 == 19)
            shortdmg = L1WeaponSkill.getKiringkuDamage(_pc, _target) + calcAttrEnchantDmg();

        if (_pc.hasSkillEffect(COOKING_2_0_N)
                || _pc.hasSkillEffect(COOKING_2_0_S)
                || _pc.hasSkillEffect(COOKING_3_2_N)
                || _pc.hasSkillEffect(COOKING_3_2_S))
            shortdmg += 1;

        return shortdmg;
    }

    public int calcPcPcDamage() {
        int weaponTotalDamage = calcWeponDamage(_weaponSmall);

        if (((_weaponId == 12)||(_weaponId == 262)) && (Random.nextInt(100) + 1 <= 50)) {
            weaponTotalDamage += calcDestruction(weaponTotalDamage);
        }

        double dmg = weaponTotalDamage + _statusDamage;
        if (_weaponType == 20 || _weaponType == 62)
            dmg = calLongRageDamage(dmg);
        else
            dmg = calShortRageDamage(dmg);

        if ((_weaponId == 2 || _weaponId == 200002)) {
            dmg = L1WeaponSkill.getDiceDaggerDamage(_pc, _target, weapon);
        } else {
            dmg += L1WeaponSkill.getWeaponSkillDamage(
                    _pc, _target, _weaponId);
        }

        if (_pc.hasSkillEffect(ARM_BREAKER)) {
            dmg -= 5;
        }

        dmg -= _targetPc.getDamageReductionByArmor();

        dmg -= L1MagicDoll.getDamageReductionByDoll(_targetPc);

        if (_targetPc.hasSkillEffect(COOKING_1_0_S)
                || _targetPc.hasSkillEffect(COOKING_1_1_S)
                || _targetPc.hasSkillEffect(COOKING_1_2_S)
                || _targetPc.hasSkillEffect(COOKING_1_3_S)
                || _targetPc.hasSkillEffect(COOKING_1_4_S)
                || _targetPc.hasSkillEffect(COOKING_1_5_S)
                || _targetPc.hasSkillEffect(COOKING_1_6_S)
                || _targetPc.hasSkillEffect(COOKING_2_0_S)
                || _targetPc.hasSkillEffect(COOKING_2_1_S)
                || _targetPc.hasSkillEffect(COOKING_2_2_S)
                || _targetPc.hasSkillEffect(COOKING_2_3_S)
                || _targetPc.hasSkillEffect(COOKING_2_4_S)
                || _targetPc.hasSkillEffect(COOKING_2_5_S)
                || _targetPc.hasSkillEffect(COOKING_2_6_S)
                || _targetPc.hasSkillEffect(COOKING_3_0_S)
                || _targetPc.hasSkillEffect(COOKING_3_1_S)
                || _targetPc.hasSkillEffect(COOKING_3_2_S)
                || _targetPc.hasSkillEffect(COOKING_3_3_S)
                || _targetPc.hasSkillEffect(COOKING_3_4_S)
                || _targetPc.hasSkillEffect(COOKING_3_5_S)
                || _targetPc.hasSkillEffect(COOKING_3_6_S)) {
            dmg -= 5;
        }
        if (_targetPc.hasSkillEffect(COOKING_1_7_S)
                || _targetPc.hasSkillEffect(COOKING_2_7_S)
                || _targetPc.hasSkillEffect(COOKING_3_7_S)) {
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

        if (_targetPc.hasSkillEffect(IMMUNE_TO_HARM)) {
            dmg /= 2;
        }
        if (_targetPc.hasSkillEffect(ILLUSION_AVATAR)) {
            dmg *= 1.05;
        }

        if (_targetPc.hasSkillEffect(ARMOR_BREAK)){
            dmg *= 1.6;
        }
        if (_skillId == SMASH) {
            dmg += 15;
            if (_weaponType2 == 17 || _weaponType2 == 19) {
                dmg = 15;
            }
        }

        else if (_skillId == BONE_BREAK) {
            dmg += 10;
            if (_weaponType2 == 17 || _weaponType2 == 19) {
                dmg = 10;
            }
        }
        if (dmg <= 0) {
            _isHit = false;
            _drainHp = 0;
        }

        return (int) dmg;
    }

    private int calcPcNpcDamage() {
        int weaponMaxDamage = 0;
        if (_targetNpc.getNpcTemplate().get_size().equalsIgnoreCase("small")
                && (_weaponSmall > 0)) {
            weaponMaxDamage = _weaponSmall;
        } else if (_targetNpc.getNpcTemplate().get_size()
                .equalsIgnoreCase("large")
                && (_weaponLarge > 0)) {
            weaponMaxDamage = _weaponLarge;
        }

        int weaponTotalDamage = calcWeponDamage(weaponMaxDamage) ;

        if (((_weaponId == 12)||(_weaponId == 262))&& (Random.nextInt(100) + 1 <= 50)) {
            weaponTotalDamage += calcDestruction(weaponTotalDamage);
        }

        double dmg = weaponTotalDamage + _statusDamage;
        if (_weaponType == 20 || _weaponType == 62)
            dmg = calLongRageDamage(dmg);
        else
            dmg = calShortRageDamage(dmg);


        dmg += L1WeaponSkill.getWeaponSkillDamage(_pc, _target, _weaponId);


        if (_pc.hasSkillEffect(ARM_BREAKER)) {
            dmg -= 5;
        }

        dmg -= calcNpcDamageReduction();

        if (_skillId == SMASH) {
            dmg += 15;
            if (_weaponType2 == 17 || _weaponType2 == 19) {
                dmg = 15;
            }
        }
        else if (_skillId == BONE_BREAK) {
            dmg += 10;
            if (_weaponType2 == 17 || _weaponType2 == 19) {
                dmg = 10;
            }
        }

        boolean isNowWar = false;
        int castleId = L1CastleLocation.getCastleIdByArea(_targetNpc);
        if (castleId > 0) {
            isNowWar = WarTimeController.getInstance().isNowWar(castleId);
        }


        int chaDmgModifier = 8;
        if (_npc instanceof L1PetInstance || _npc instanceof L1SummonInstance) {
            L1PcInstance _pc = (L1PcInstance) _npc.getMaster();
            if(_pc.getCha() < 35) {
                chaDmgModifier = 2;
            }
            if(_pc.getCha() >= 35) {
                chaDmgModifier = 4;
            }
        }

        if (!isNowWar) {
            if (_targetNpc instanceof L1PetInstance)
                dmg /= chaDmgModifier;
            else if (_targetNpc instanceof L1SummonInstance) {
                L1SummonInstance summon = (L1SummonInstance) _targetNpc;
                if (summon.isExsistMaster())
                    dmg /= chaDmgModifier;
            }
        }
        if (dmg <= 0) {
            _isHit = false;
            _drainHp = 0;
        }

        return (int) dmg;
    }

    private int calcNpcPcDamage() {
        int lvl = _npc.getLevel();
        double dmg = 0D;
        if (lvl < 10) {
            dmg = Random.nextInt(lvl) + 10D + _npc.getStr() / 2 + 1;
        } else {
            dmg = Random.nextInt(lvl) + _npc.getStr() / 2 + 1;
        }

        if (_npc instanceof L1PetInstance) {
            dmg += (lvl / 16);
            dmg += ((L1PetInstance) _npc).getDamageByWeapon();
        }

        dmg += _npc.getDmgup();

        if (isUndeadDamage()) {
            dmg *= 1.1;
        }

        dmg = dmg * getLeverage() / 10;

        dmg -= calcPcDefense();

        if (_npc.isWeaponBreaked()) {
            dmg /= 2;
        }

        if (_npc.hasSkillEffect(ARM_BREAKER)) {
            dmg -= 5;
        }

        dmg -= _targetPc.getDamageReductionByArmor();

        dmg -= L1MagicDoll.getDamageReductionByDoll(_targetPc);

        if (_targetPc.hasSkillEffect(COOKING_1_0_S)
                || _targetPc.hasSkillEffect(COOKING_1_1_S)
                || _targetPc.hasSkillEffect(COOKING_1_2_S)
                || _targetPc.hasSkillEffect(COOKING_1_3_S)
                || _targetPc.hasSkillEffect(COOKING_1_4_S)
                || _targetPc.hasSkillEffect(COOKING_1_5_S)
                || _targetPc.hasSkillEffect(COOKING_1_6_S)
                || _targetPc.hasSkillEffect(COOKING_2_0_S)
                || _targetPc.hasSkillEffect(COOKING_2_1_S)
                || _targetPc.hasSkillEffect(COOKING_2_2_S)
                || _targetPc.hasSkillEffect(COOKING_2_3_S)
                || _targetPc.hasSkillEffect(COOKING_2_4_S)
                || _targetPc.hasSkillEffect(COOKING_2_5_S)
                || _targetPc.hasSkillEffect(COOKING_2_6_S)
                || _targetPc.hasSkillEffect(COOKING_3_0_S)
                || _targetPc.hasSkillEffect(COOKING_3_1_S)
                || _targetPc.hasSkillEffect(COOKING_3_2_S)
                || _targetPc.hasSkillEffect(COOKING_3_3_S)
                || _targetPc.hasSkillEffect(COOKING_3_4_S)
                || _targetPc.hasSkillEffect(COOKING_3_5_S)
                || _targetPc.hasSkillEffect(COOKING_3_6_S)) {
            dmg -= 5;
        }
        if (_targetPc.hasSkillEffect(COOKING_1_7_S)
                || _targetPc.hasSkillEffect(COOKING_2_7_S)
                || _targetPc.hasSkillEffect(COOKING_3_7_S)) {
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
        if (_targetPc.hasSkillEffect(IMMUNE_TO_HARM)) {
            dmg /= 2;
        }
        boolean isNowWar = false;
        int castleId = L1CastleLocation.getCastleIdByArea(_targetPc);
        if (castleId > 0) {
            isNowWar = WarTimeController.getInstance().isNowWar(castleId);
        }


        int chaDmgModifier = 8;
        if (_npc instanceof L1PetInstance || _npc instanceof L1SummonInstance) {
            L1PcInstance _pc = (L1PcInstance) _npc.getMaster();
            if(_pc.getCha() < 10) {
                chaDmgModifier = 8;
            }
            if(_pc.getCha() >= 10) {
                chaDmgModifier = 7;
            }
            if(_pc.getCha() >= 15) {
                chaDmgModifier = 6;
            }
            if(_pc.getCha() >= 20) {
                chaDmgModifier = 5;
            }
            if(_pc.getCha() >= 25) {
                chaDmgModifier = 4;
            }
            if(_pc.getCha() >= 30) {
                chaDmgModifier = 3;
            }
            if(_pc.getCha() >= 35) {
                chaDmgModifier = 2;
            }
        }
        if (!isNowWar) {
            if (_npc instanceof L1PetInstance) {
                dmg /= chaDmgModifier;
            } else if (_npc instanceof L1SummonInstance) {
                L1SummonInstance summon = (L1SummonInstance) _npc;
                if (summon.isExsistMaster()) {
                    dmg /= chaDmgModifier;
                }
            }
        }

        if (dmg <= 0) {
            _isHit = false;
        }

        addNpcPoisonAttack(_npc, _targetPc);
        if(_targetPc.isGm())
        {
            _targetPc.sendPackets(new S_SystemMessage("Dmg Recieved: " + dmg));
        }
        return (int) dmg;
    }

    private int calcNpcNpcDamage() {
        int lvl = _npc.getLevel();
        double dmg = 0;

        if (_npc instanceof L1PetInstance) {
            dmg = Random.nextInt(_npc.getNpcTemplate().get_level())
                    + _npc.getStr() / 2 + 1;
            dmg += (lvl / 16);
            dmg += ((L1PetInstance) _npc).getDamageByWeapon();
        } else {
            dmg = Random.nextInt(lvl) + _npc.getStr() / 2 + 1;
        }

        if (isUndeadDamage()) {
            dmg *= 1.1;
        }

        dmg = dmg * getLeverage() / 10;

        if (_npc.hasSkillEffect(ARM_BREAKER)) {
            dmg -= 5;
        }

        dmg -= calcNpcDamageReduction();

        if (_npc.isWeaponBreaked()) {
            dmg /= 2;
        }

        addNpcPoisonAttack(_npc, _targetNpc);

        if (dmg <= 0) {
            _isHit = false;
        }

        return (int) dmg;
    }


    private double calcBuffDamage(double dmg) {
        if (_pc.hasSkillEffect(BURNING_SPIRIT)
                || _pc.hasSkillEffect(ELEMENTAL_FIRE)) {
            if ((Random.nextInt(100) + 1) <= 33) {
                double tempDmg = dmg;
                if (_pc.hasSkillEffect(FIRE_WEAPON)) {
                    tempDmg -= 4;
                } else if (_pc.hasSkillEffect(FIRE_BLESS)) {
                    tempDmg -= 5;
                } else if (_pc.hasSkillEffect(BURNING_WEAPON)) {
                    tempDmg -= 6;
                }
                if (_pc.hasSkillEffect(BERSERKERS)) {
                    tempDmg -= 5;
                }
                double diffDmg = dmg - tempDmg;
                dmg = tempDmg * 1.5 + diffDmg;
            }
        }


        if (_weaponType2 == 18) {

            if (_pc.hasSkillEffect(SPECIAL_EFFECT_WEAKNESS_LV3)) {
                dmg += 6;
            } else if (_pc.hasSkillEffect(SPECIAL_EFFECT_WEAKNESS_LV2)) {
                dmg += 4;
            } else if (_pc.hasSkillEffect(SPECIAL_EFFECT_WEAKNESS_LV1)) {
                dmg += 2;
            }
        }


        if (_pc.isFoeSlayer()) {

            int level = _pc.getLevel();
            int tempDmg = (int)Math.floor((level/10));

            if (_pc.hasSkillEffect(SPECIAL_EFFECT_WEAKNESS_LV3)) {
                dmg += (tempDmg*3);
            } else if (_pc.hasSkillEffect(SPECIAL_EFFECT_WEAKNESS_LV2)) {
                dmg += (tempDmg*2);
            } else if (_pc.hasSkillEffect(SPECIAL_EFFECT_WEAKNESS_LV1)) {
                dmg += (tempDmg*1);
            }
        }

        if (_pc.hasSkillEffect(BURNING_SLASH)) {
            dmg += 10;
            _pc.sendPackets(new S_EffectLocation(_targetX, _targetY, 6591));
            _pc.broadcastPacket(new S_EffectLocation(_targetX, _targetY, 6591));
            _pc.killSkillEffectTimer(BURNING_SLASH);
        }

        return dmg;
    }

    private int calcPcDefense() {
        int ac = Math.max(0, 10 - _targetPc.getAc());
        int acDefMax = _targetPc.getClassFeature().getAcDefenseMax(ac);
        return Random.nextInt(acDefMax + 1);
    }

    private int calcNpcDamageReduction() {
        return _targetNpc.getNpcTemplate().get_damagereduction();
    }

    private int calcMaterialBlessDmg() {
        int damage = 0;
        int undead = _targetNpc.getNpcTemplate().get_undead();
        if (((_weaponMaterial == 14) || (_weaponMaterial == 17) || (_weaponMaterial == 22))
                && ((undead == 1) || (undead == 3) || (undead == 5))) {
            damage += Random.nextInt(20) + 1;
        } else if (((_weaponMaterial == 17) || (_weaponMaterial == 22))
                && (undead == 2)) {
            damage += Random.nextInt(3) + 1;
        }
        if ((_weaponBless == 0)
                && ((undead == 1) || (undead == 2) || (undead == 3))) {
            damage += Random.nextInt(4) + 1;
        }
        if ((_pc.getWeapon() != null) && (_weaponType != 20)
                && (_weaponType != 62) && (weapon.getHolyDmgByMagic() != 0)
                && ((undead == 1) || (undead == 3))) {
            damage += weapon.getHolyDmgByMagic();
        }
        return damage;
    }

    private int calcAttrEnchantDmg() {
        int damage = 0;
        if (_weaponAttrEnchantLevel == 1) {
            damage = 1;
        } else if (_weaponAttrEnchantLevel == 2) {
            damage = 3;
        } else if (_weaponAttrEnchantLevel == 3) {
            damage = 5;
        }

        int resist = 0;
        if (_calcType == PC_PC) {
            if (_weaponAttrEnchantKind == 1) {
                resist = _targetPc.getEarth();
            } else if (_weaponAttrEnchantKind == 2) {
                resist = _targetPc.getFire();
            } else if (_weaponAttrEnchantKind == 4) {
                resist = _targetPc.getWater();
            } else if (_weaponAttrEnchantKind == 8) {
                resist = _targetPc.getWind();
            }
        } else if (_calcType == PC_NPC) {
            int weakAttr = _targetNpc.getNpcTemplate().get_weakAttr();
            if (((_weaponAttrEnchantKind == 1) && (weakAttr == 1))
                    || ((_weaponAttrEnchantKind == 2) && (weakAttr == 2))
                    || ((_weaponAttrEnchantKind == 4) && (weakAttr == 4))
                    || ((_weaponAttrEnchantKind == 8) && (weakAttr == 8))) {
                resist = -50;
            }
        }

        int resistFloor = (int) (0.32 * Math.abs(resist));
        if (resist >= 0) {
            resistFloor *= 1;
        } else {
            resistFloor *= -1;
        }

        double attrDeffence = resistFloor / 32.0;
        double attrCoefficient = 1 - attrDeffence;

        damage *= attrCoefficient;

        return damage;
    }


    private boolean isUndeadDamage() {
        boolean flag = false;
        int undead = _npc.getNpcTemplate().get_undead();
        boolean isNight = L1GameTimeClock.getInstance().currentTime().isNight();
        if (isNight && ((undead == 1) || (undead == 3) || (undead == 4))) {
            flag = true;
        }
        return flag;
    }


    private void addNpcPoisonAttack(L1Character attacker, L1Character target) {
        if (_npc.getNpcTemplate().get_poisonatk() != 0) {
            if (15 >= Random.nextInt(100) + 1) {
                if (_npc.getNpcTemplate().get_poisonatk() == 1) {
                    L1DamagePoison.doInfection(attacker, target, 3000, 5);
                } else if (_npc.getNpcTemplate().get_poisonatk() == 2) {
                    L1SilencePoison.doInfection(target);
                } else if (_npc.getNpcTemplate().get_poisonatk() == 4) {

                    L1ParalysisPoison.doInfection(target, 20000, 45000);
                }
            }
        } else if (_npc.getNpcTemplate().get_paralysisatk() != 0) {
        }
    }


    public void calcStaffOfMana() {
        if ((_weaponId == 126) || (_weaponId == 127 || _weaponId == 500026 )) {
            int som_lvl = _weaponEnchant + 3;
            if (som_lvl < 0) {
                som_lvl = 0;
            }
            _drainMana = Random.nextInt(som_lvl) + 1;
            if (_drainMana > Config.MANA_DRAIN_LIMIT_PER_SOM_ATTACK) {
                _drainMana = Config.MANA_DRAIN_LIMIT_PER_SOM_ATTACK;
            }
        } else if (_weaponId == 259) {
            if (_calcType == PC_PC) {
                if (_targetPc.getMr() <= Random.nextInt(100) + 1) {
                    _drainMana = 1;
                }
            } else if (_calcType == PC_NPC) {
                if (_targetNpc.getMr() <= Random.nextInt(100) + 1) {
                    _drainMana = 1;
                }
            }
        }
        else if (_weaponId == 500026) {
            if (_calcType == PC_PC) {
                if (_targetPc.getMr() <= Random.nextInt(100) + 1) {
                    _drainMana = 3;
                }
            } else if (_calcType == PC_NPC) {
                if (_targetNpc.getMr() <= Random.nextInt(100) + 1) {
                    _drainMana = 3;
                }
            }
        }
    }

    private int calcDestruction(int dmg) {
        _drainHp = (dmg / 8) + 1;
        return _drainHp > 0 ? _drainHp : 1;
    }


    public void addPcPoisonAttack(L1Character attacker, L1Character target) {
        int chance = Random.nextInt(100) + 1;
        if (((_weaponId == 13) || (_weaponId == 44) || ((_weaponId != 0) && _pc.hasSkillEffect(ENCHANT_VENOM))) && (chance <= 10)) {

            if(_pc.hasSkillEffect(ENCHANT_VENOM) && _weaponId !=0) {

                if(target instanceof L1PcInstance)
                {
                    int poisonType = Random.nextInt(4) + 1;
                    if(poisonType == 1) {
                        L1DamagePoison.doInfection(attacker, target, 3000, 5);
                    } else if(poisonType == 2) {
                        L1DamagePoison.doInfection(attacker, target, 3000, _pc.getLevel());
                    }else if(poisonType == 3) {
                        L1SilencePoison.doInfection(target);
                    }else if(poisonType == 4) {
                        L1ParalysisPoison.doInfection(target, 6000, 10000);
                    }
                }
                else
                {
                    L1DamagePoison.doInfection(attacker, target, 3000, _pc.getLevel()/2);
                }
            }
            else
            {
                L1DamagePoison.doInfection(attacker, target, 3000, 5);
            }

        } else {
            if (L1MagicDoll.getEffectByDoll(attacker, (byte) 1) == 1) {
                L1DamagePoison.doInfection(attacker, target, 3000, 5);
            }
        }
    }


    public void addChaserAttack() {
        /*
        if (5 > Random.nextInt(100) + 1) {
            if (_weaponId == 265 || _weaponId == 266 || _weaponId == 267
                    || _weaponId == 268 || _weaponId == 280 || _weaponId == 281) {
                L1Chaser chaser = new L1Chaser(_pc, _target,
                        L1Skills.ATTR_EARTH, 7025);
                chaser.begin();
            } else if (_weaponId == 276 || _weaponId == 277) {
                L1Chaser chaser = new L1Chaser(_pc, _target,
                        L1Skills.ATTR_WATER, 7179);
                chaser.begin();
            } else if (_weaponId == 304 || _weaponId == 307 || _weaponId == 308) {
                L1Chaser chaser = new L1Chaser(_pc, _target,
                        L1Skills.ATTR_WATER, 8150);
                chaser.begin();
            } else if (_weaponId == 305 || _weaponId == 306 || _weaponId == 309) {
                L1Chaser chaser = new L1Chaser(_pc, _target,
                        L1Skills.ATTR_WATER, 8152);
                chaser.begin();
            }
        }*/
    }

    /* â- â- â- â- â- â- â- â- â- â- â- â- â- â-  æ”»æ’ƒãƒ¢ãƒ¼ã‚·ãƒ§ãƒ³é€�ä¿¡ â- â- â- â- â- â- â- â- â- â- â- â- â- â-  */
    public void action() {
        if (_calcType == PC_PC || _calcType == PC_NPC) {
            actionPc();
        } else if (_calcType == NPC_PC || _calcType == NPC_NPC) {
            actionNpc();
        }
    }

    public void actionPc() {
        _attckActId = 1;
        boolean isFly = false;
        _pc.setHeading(_pc.targetDirection(_targetX, _targetY));

        if (_weaponType == 20 && (_arrow != null || _weaponId == 190)) {
            if (_arrow != null) {
                _pc.getInventory().removeItem(_arrow, 1);
                _attckGrfxId = 66;
            } else if (_weaponId == 190)
                _attckGrfxId = 2349;

            if (_pc.getTempCharGfx() == 8719)
                _attckGrfxId = 8721;

            if (_pc.getTempCharGfx() == 8900)
                _attckGrfxId = 8904;

            if (_pc.getTempCharGfx() == 8913)
                _attckGrfxId = 8916;

            isFly = true;
        } else if ((_weaponType == 62) && (_sting != null)) {
            _pc.getInventory().removeItem(_sting, 1);
            _attckGrfxId = 2989;
            isFly = true;
        }

        if (!_isHit) {
            _damage = 0;
        }

        int[] data = null;

        if (isFly) {
            data = new int[] { _attckActId, _damage, _attckGrfxId };
            _pc.sendPackets(new S_UseArrowSkill(_pc, _targetId, _targetX,
                    _targetY, data));
            _pc.broadcastPacket(new S_UseArrowSkill(_pc, _targetId, _targetX,
                    _targetY, data));
        } else {
            data = new int[] { _attckActId, _damage, _effectId };
            _pc.sendPackets(new S_AttackPacket(_pc, _targetId, data));
            _pc.broadcastPacket(new S_AttackPacket(_pc, _targetId, data));
        }

        if (_isHit) {
            _target.broadcastPacketExceptTargetSight(new S_DoActionGFX(
                    _targetId, ActionCodes.ACTION_Damage), _pc);
        }
    }

    private void actionNpc() {
        int bowActId = 0;
        int npcGfxid = _npc.getTempCharGfx();
        int actId = L1NpcDefaultAction.getInstance().getSpecialAttack(npcGfxid);
        double dmg = _damage;
        int[] data = null;

        _npc.setHeading(_npc.targetDirection(_targetX, _targetY));

        boolean isLongRange = false;
        if (npcGfxid == 4521 || npcGfxid == 4550 || npcGfxid == 5062 || npcGfxid == 5317
                || npcGfxid == 5324 || npcGfxid == 5331 || npcGfxid == 5338 || npcGfxid == 5412) {
            isLongRange = (_npc.getLocation().getTileLineDistance(
                    new Point(_targetX, _targetY)) > 2);
        } else {
            isLongRange = (_npc.getLocation().getTileLineDistance(
                    new Point(_targetX, _targetY)) > 1);
        }
        bowActId = _npc.getPolyArrowGfx();
        if (bowActId == 0) {
            bowActId = _npc.getNpcTemplate().getBowActId();
        }
        if (getActId() == 0) {
            if ((actId != 0) && ((Random.nextInt(100) + 1) <= 40)) {
                dmg *= 1.2;
            } else {
                if (!isLongRange || bowActId == 0) {
                    actId = L1NpcDefaultAction.getInstance().getDefaultAttack(npcGfxid);
                    if (bowActId > 0) {
                        dmg *= 1.2;
                    }
                } else {
                    actId = L1NpcDefaultAction.getInstance().getRangedAttack(npcGfxid);
                }
            }
        } else {
            actId = getActId();
        }
        _damage = (int) dmg;

        if (!_isHit) {
            _damage = 0;
        }

        if (isLongRange && (bowActId > 0)) {
            data = new int[] { actId, _damage, bowActId };
            _npc.broadcastPacket(new S_UseArrowSkill(_npc, _targetId, _targetX,
                    _targetY, data));
        } else {
            if (getGfxId() > 0) {
                data = new int[] { actId, _damage, getGfxId(), 6 };
                _npc.broadcastPacket(new S_UseAttackSkill(_npc, _targetId,
                        _targetX, _targetY, data));
            } else {
                data = new int[] { actId, _damage, 0 };
                _npc.broadcastPacket(new S_AttackPacket(_npc, _targetId, data));
            }
        }
        if (_isHit) {
            _target.broadcastPacketExceptTargetSight(new S_DoActionGFX(
                    _targetId, ActionCodes.ACTION_Damage), _npc);
        }
    }

        /*
         *
         * head)
         * ï¼¸æ-¹å�‘ã�®ã‚¿ãƒ¼ã‚²ãƒƒãƒˆã�¾ã�§ã�®è·�é›¢ float dis_y = Math.abs(cy - _targetY);
         * float dis = Math.max(dis_x, dis_y);
         * avg_y = 0; if (dis == 0) {
         * = 1; avg_y = -1; } else if (head == 2) { avg_x = 1; avg_y = 0; } else if
         * (head == 3) { avg_x = 1; avg_y = 1; } else if (head == 4) { avg_x = 0;
         * avg_y = 1; } else if (head == 5) { avg_x = -1; avg_y = 1; } else if (head
         * == 6) { avg_x = -1; avg_y = 0; } else if (head == 7) { avg_x = -1; avg_y
         * = -1; } else if (head == 0) { avg_x = 0; avg_y = -1; } } else { avg_x =
         * dis_x / dis; avg_y = dis_y / dis; }
         *
         * int add_x = (int) Math.floor((avg_x * 15) + 0.59f);
         * add_y = (int) Math.floor((avg_y * 15) + 0.59f);
         *
         * if (cx > _targetX) { add_x *= -1; } if (cy > _targetY) { add_y *= -1; }
         *
         * _targetX = _targetX + add_x; _targetY = _targetY + add_y; }
         */

        /* â- â- â- â- â- â- â- â- â- â- â- â- â- â- â-  è¨ˆç®-çµ�æžœå��æ˜  â- â- â- â- â- â- â- â- â- â- â- â- â- â- â-  */

    public void commit() {
        if (_isHit) {
            if ((_calcType == PC_PC) || (_calcType == NPC_PC)) {
                commitPc();
            } else if ((_calcType == PC_NPC) || (_calcType == NPC_NPC)) {
                commitNpc();
            }
        }

        if (!Config.ALT_ATKMSG) {
            return;
        }
        if (Config.ALT_ATKMSG) {
            if (((_calcType == PC_PC) || (_calcType == PC_NPC)) && !_pc.isGm()) {
                return;
            }
            if (((_calcType == PC_PC) || (_calcType == NPC_PC))
                    && !_targetPc.isGm()) {
                return;
            }
        }
        String msg0 = "";
        String msg1 = " é€ æˆ� ";
        String msg2 = "";
        String msg3 = "";
        String msg4 = "";
        if ((_calcType == PC_PC) || (_calcType == PC_NPC)) {
            msg0 = "ç‰©æ”» å°�";
        } else if (_calcType == NPC_PC) {
            msg0 = _npc.getNameId() + "(ç‰©æ”»)ï¼š";
        }

        if ((_calcType == NPC_PC) || (_calcType == PC_PC)) {
            msg4 = _targetPc.getName();
            msg2 = "ï¼Œå‰©é¤˜ " + _targetPc.getCurrentHp() + "ï¼Œå‘½ä¸­     " + _hitRate + "%";
        } else if (_calcType == PC_NPC) {
            msg4 = _targetNpc.getNameId();
            msg2 = "ï¼Œå‰©é¤˜ " + _targetNpc.getCurrentHp() + "ï¼Œå‘½ä¸­ " + _hitRate + "%";
        }
        msg3 = _isHit ? _damage + " å‚·å®³" : "  0 å‚·å®³";

        if ((_calcType == PC_PC) || (_calcType == PC_NPC)) {
            _pc.sendPackets(new S_ServerMessage(166, msg0, msg1, msg2, msg3,
                    msg4));
        }
        else if ((_calcType == NPC_PC)) {
            _targetPc.sendPackets(new S_ServerMessage(166, msg0, null, msg2,
                    msg3, null));
        }
    }

    private void commitPc() {
        if (_calcType == PC_PC) {
            if ((_drainMana > 0) && (_targetPc.getCurrentMp() > 0)) {
                if (_drainMana > _targetPc.getCurrentMp()) {
                    _drainMana = _targetPc.getCurrentMp();
                }
                short newMp = (short) (_targetPc.getCurrentMp() - _drainMana);
                _targetPc.setCurrentMp(newMp);
                newMp = (short) (_pc.getCurrentMp() + _drainMana);
                _pc.setCurrentMp(newMp);
            }
            if (_drainHp > 0) {
                short newHp = (short) (_pc.getCurrentHp() + _drainHp);
                _pc.setCurrentHp(newHp);
            }
            damagePcWeaponDurability();
            _targetPc.receiveDamage(_pc, _damage, false);
        } else if (_calcType == NPC_PC) {
            _targetPc.receiveDamage(_npc, _damage, false);
        }
    }

    private void commitNpc() {
        if (_calcType == PC_NPC) {
            if (_drainMana > 0) {
                int drainValue = _targetNpc.drainMana(_drainMana);
                int newMp = _pc.getCurrentMp() + drainValue;
                _pc.setCurrentMp(newMp);
                if (drainValue > 0) {
                    int newMp2 = _targetNpc.getCurrentMp() - drainValue;
                    _targetNpc.setCurrentMpDirect(newMp2);
                }
            }
            if (_drainHp > 0) {
                short newHp = (short) (_pc.getCurrentHp() + _drainHp);
                _pc.setCurrentHp(newHp);
            }
            damageNpcWeaponDurability();
            _targetNpc.receiveDamage(_pc, _damage);
        } else if (_calcType == NPC_NPC) {
            _targetNpc.receiveDamage(_npc, _damage);
        }
    }

        /* â- â- â- â- â- â- â- â- â- â- â- â- â- â- â-  ã‚«ã‚¦ãƒ³ã‚¿ãƒ¼ãƒ�ãƒªã‚¢ â- â- â- â- â- â- â- â- â- â- â- â- â- â- â-  */

    public void actionCounterBarrier() {
        if (_calcType == PC_PC) {
            _pc.setHeading(_pc.targetDirection(_targetX, _targetY));
            _pc.sendPackets(new S_AttackMissPacket(_pc, _targetId));
            _pc.broadcastPacket(new S_AttackMissPacket(_pc, _targetId));
            _pc.sendPackets(new S_DoActionGFX(_pc.getId(),
                    ActionCodes.ACTION_Damage));
            _pc.broadcastPacket(new S_DoActionGFX(_pc.getId(),
                    ActionCodes.ACTION_Damage));
        } else if (_calcType == NPC_PC) {
            int actId = 0;
            _npc.setHeading(_npc.targetDirection(_targetX, _targetY));
            if (getActId() > 0) {
                actId = getActId();
            } else {
                actId = ActionCodes.ACTION_Attack;
            }
            if (getGfxId() > 0) {
                int[] data = { actId, 0, getGfxId(), 6 };
                _npc.broadcastPacket(new S_UseAttackSkill(_target,
                        _npc.getId(), _targetX, _targetY, data));
            } else {
                _npc.broadcastPacket(new S_AttackMissPacket(_npc, _targetId,
                        actId));
            }
            _npc.broadcastPacket(new S_DoActionGFX(_npc.getId(),
                    ActionCodes.ACTION_Damage));
        }
    }

    public boolean isShortDistance() {
        boolean isShortDistance = true;
        if (_calcType == PC_PC) {
            if ((_weaponType == 20) || (_weaponType == 62) || _weaponType2 == 17 || _weaponType2 == 19) {
                isShortDistance = false;
            }
        } else if (_calcType == NPC_PC) {
            boolean isLongRange = (_npc.getLocation().getTileLineDistance(
                    new Point(_targetX, _targetY)) > 1);
            int bowActId = _npc.getPolyArrowGfx();
            if (bowActId == 0) {
                bowActId = _npc.getNpcTemplate().getBowActId();
            }
            if (isLongRange && (bowActId > 0)) {
                isShortDistance = false;
            }
        }
        return isShortDistance;
    }

    public void commitCounterBarrier() {
        int damage = calcCounterBarrierDamage();
        if (damage == 0) {
            return;
        }
        if (_calcType == PC_PC) {
            _pc.receiveDamage(_targetPc, damage, false);
        } else if (_calcType == NPC_PC) {
            _npc.receiveDamage(_targetPc, damage);
        }
    }

    private int calcCounterBarrierDamage() {
        int damage = 0;
        L1ItemInstance weapon = null;
        weapon = _targetPc.getWeapon();
        if (weapon != null) {
            if (weapon.getItem().getType() == 3) {
                damage = (weapon.getItem().getDmgLarge()
                        + weapon.getEnchantLevel() + weapon.getItem()
                        .getDmgModifier()) * 2;
            }
        }
        return damage;
    }

    public static boolean isImmune(L1Character character) {
        for (int skillId : PREVENT_DAMAGE)
            if (character.hasSkillEffect(skillId))
                return true;
        return false;
    }

    /*
     * æ­¦å™¨ã‚’æ��å‚·ã�•ã�›ã‚‹ã€‚ å¯¾NPCã�®å ´å�ˆã€�æ��å‚·ç¢ºçŽ‡ã�¯10%ã�¨ã�™ã‚‹ã€‚ç¥�ç¦�æ­¦å™¨ã�¯3%ã�¨ã�™ã‚‹ã€‚
     */
    private void damageNpcWeaponDurability() {
        int chance = 10;
        int bchance = 3;

                /*
                 * æ��å‚·ã�-ã�ªã�„NPCã€�ç´ æ‰‹ã€�æ��å‚·ã�-ã�ªã�„æ­¦å™¨ä½¿ç”¨ã€�SOFä¸­ã�®å ´å�ˆä½•ã‚‚ã�-ã�ªã�„ã€‚
                 */
        if ((_calcType != PC_NPC)
                || (_targetNpc.getNpcTemplate().is_hard() == false)
                || (_weaponType == 0) || (weapon.getItem().get_canbedmg() == 0)
                || _pc.hasSkillEffect(SOUL_OF_FLAME)) {
            return;
        }
        if (((_weaponBless == 1) || (_weaponBless == 2))
                && ((Random.nextInt(100) + 1) < chance)) {
            _pc.sendPackets(new S_ServerMessage(268, weapon.getLogName()));
            _pc.getInventory().receiveDamage(weapon);
        }
        if ((_weaponBless == 0) && ((Random.nextInt(100) + 1) < bchance)) {
            _pc.sendPackets(new S_ServerMessage(268, weapon.getLogName()));
            _pc.getInventory().receiveDamage(weapon);
        }
    }

    /*
     * ãƒ�ã‚¦ãƒ³ã‚¹ã‚¢ã‚¿ãƒƒã‚¯ã�«ã‚ˆã‚Šæ­¦å™¨ã‚’æ��å‚·ã�•ã�›ã‚‹ã€‚ ãƒ�ã‚¦ãƒ³ã‚¹ã‚¢ã‚¿ãƒƒã‚¯ã�®æ��å‚·ç¢ºçŽ‡ã�¯10%
     */
    private void damagePcWeaponDurability() {
        if ((_calcType != PC_PC) || (_weaponType == 0) || (_weaponType == 20)
                || (_weaponType == 62)
                || (_targetPc.hasSkillEffect(BOUNCE_ATTACK) == false)
                || _pc.hasSkillEffect(SOUL_OF_FLAME)) {
            return;
        }

        if (Random.nextInt(100) + 1 <= 10) {
            _pc.sendPackets(new S_ServerMessage(268, weapon.getLogName()));
            _pc.getInventory().receiveDamage(weapon);
        }
    }

    private static final S_SkillIconGFX Weakness1 = new S_SkillIconGFX(75, 1);
    private static final S_SkillIconGFX Weakness2 = new S_SkillIconGFX(75, 2);
    private static final S_SkillIconGFX Weakness3 = new S_SkillIconGFX(75, 3);

    /** å¼±é»žæ›�å…‰ */
    private void WeaknessExposure() {
        if (weapon != null) {
            int random = Random.nextInt(100) + 1;
            if (_weaponType2 == 18) {
                if (_pc.isFoeSlayer()) {
                    return;
                }
                int weaponWeaknessExposureChance = 30;
                if (random <= weaponWeaknessExposureChance) {
                    if (_pc.hasSkillEffect(SPECIAL_EFFECT_WEAKNESS_LV3)) {
                    } else if (_pc.hasSkillEffect(SPECIAL_EFFECT_WEAKNESS_LV2)) {
                        _pc.killSkillEffectTimer(SPECIAL_EFFECT_WEAKNESS_LV2);
                        _pc.setSkillEffect(SPECIAL_EFFECT_WEAKNESS_LV3, 16000);
                        _pc.sendPackets(Weakness3);
                    } else if (_pc.hasSkillEffect(SPECIAL_EFFECT_WEAKNESS_LV1)) {
                        _pc.killSkillEffectTimer(SPECIAL_EFFECT_WEAKNESS_LV1);
                        _pc.setSkillEffect(SPECIAL_EFFECT_WEAKNESS_LV2, 16000);
                        _pc.sendPackets(Weakness2);
                    } else {
                        _pc.setSkillEffect(SPECIAL_EFFECT_WEAKNESS_LV1, 16000);
                        _pc.sendPackets(Weakness1);
                    }
                }
            }
        }
    }
}


