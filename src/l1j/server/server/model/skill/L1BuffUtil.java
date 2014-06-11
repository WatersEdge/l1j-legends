package l1j.server.server.model.skill;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import l1j.server.Config;
import l1j.server.server.ActionCodes;
import l1j.server.server.datatables.NpcTable;
import l1j.server.server.model.L1Awake;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1CurseParalysis;
import l1j.server.server.model.L1PinkName;
import l1j.server.server.model.L1PolyMorph;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1MonsterInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.model.Instance.L1SummonInstance;
import l1j.server.server.model.Instance.L1TowerInstance;
import l1j.server.server.model.item.action.Potion;
import l1j.server.server.model.poison.L1DamagePoison;
import l1j.server.server.serverpackets.S_ChangeName;
import l1j.server.server.serverpackets.S_CharVisualUpdate;
import l1j.server.server.serverpackets.S_ChatPacket;
import l1j.server.server.serverpackets.S_CurseBlind;
import l1j.server.server.serverpackets.S_Dexup;
import l1j.server.server.serverpackets.S_DoActionShop;
import l1j.server.server.serverpackets.S_EffectLocation;
import l1j.server.server.serverpackets.S_HPUpdate;
import l1j.server.server.serverpackets.S_Invis;
import l1j.server.server.serverpackets.S_Liquor;
import l1j.server.server.serverpackets.S_MPUpdate;
import l1j.server.server.serverpackets.S_Message_YN;
import l1j.server.server.serverpackets.S_NpcChangeShape;
import l1j.server.server.serverpackets.S_NpcChatPacket;
import l1j.server.server.serverpackets.S_OwnCharAttrDef;
import l1j.server.server.serverpackets.S_OwnCharStatus2;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_Paralysis;
import l1j.server.server.serverpackets.S_RemoveObject;
import l1j.server.server.serverpackets.S_SPMR;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_ShowPolyList;
import l1j.server.server.serverpackets.S_ShowSummonList;
import l1j.server.server.serverpackets.S_SkillBrave;
import l1j.server.server.serverpackets.S_SkillHaste;
import l1j.server.server.serverpackets.S_SkillIconAura;
import l1j.server.server.serverpackets.S_SkillIconBloodstain;
import l1j.server.server.serverpackets.S_SkillIconGFX;
import l1j.server.server.serverpackets.S_SkillIconShield;
import l1j.server.server.serverpackets.S_SkillIconWindShackle;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.serverpackets.S_Strup;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1Npc;
import l1j.server.server.utils.Random;
import static l1j.server.server.model.skill.L1SkillId.*;

public class L1BuffUtil {

    public static void haste(L1PcInstance pc, int timeMillis) {

        int objId = pc.getId();

                /* ÃƒÂ¥Ã‚Â·Ã‚Â²ÃƒÂ¥Ã‚Â­Ã‹Å“ÃƒÂ¥Ã…â€œÃ‚Â¨ÃƒÂ¥Ã…  ÃƒÂ©Ã¢â€šÂ¬Ã…Â¸ÃƒÂ§Ã¢â‚¬Â¹Ã¢â€šÂ¬ÃƒÂ¦Ã¢â‚¬Â¦Ã¢â‚¬Â¹ÃƒÂ¦Ã‚Â¶Ã‹â€ ÃƒÂ©Ã¢â€žÂ¢Ã‚Â¤ */
        if (pc.hasSkillEffect(HASTE) || pc.hasSkillEffect(GREATER_HASTE)
                || pc.hasSkillEffect(STATUS_HASTE)) {
            if (pc.hasSkillEffect(HASTE)) {
                pc.killSkillEffectTimer(HASTE);
            } else if (pc.hasSkillEffect(GREATER_HASTE)) {
                pc.killSkillEffectTimer(GREATER_HASTE);
            } else if (pc.hasSkillEffect(STATUS_HASTE)) {
                pc.killSkillEffectTimer(STATUS_HASTE);
            }
        }
                /* ÃƒÂ¦Ã… Ã‚ÂµÃƒÂ¦Ã‚Â¶Ã‹â€ ÃƒÂ§Ã‚Â·Ã‚Â©ÃƒÂ©Ã¢â€šÂ¬Ã…Â¸ÃƒÂ©Ã‚Â­Ã¢â‚¬ï¿½ÃƒÂ¦Ã‚Â³Ã¢â‚¬Â¢ÃƒÂ¦Ã¢â‚¬Â¢Ã‹â€ ÃƒÂ¦Ã…Â¾Ã…â€œ ÃƒÂ§Ã‚Â·Ã‚Â©ÃƒÂ©Ã¢â€šÂ¬Ã…Â¸ÃƒÂ¨Ã‚Â¡Ã¢â‚¬Å“ ÃƒÂ©Ã¢â‚¬ÂºÃ¢â‚¬ ÃƒÂ©Ã‚Â«Ã¢â‚¬ï¿½ÃƒÂ§Ã‚Â·Ã‚Â©ÃƒÂ©Ã¢â€šÂ¬Ã…Â¸ÃƒÂ¨Ã‚Â¡Ã¢â‚¬Å“ ÃƒÂ¥Ã…â€œÃ‚Â°ÃƒÂ©Ã¯Â¿Â½Ã‚Â¢ÃƒÂ©Ã…Â¡Ã…â€œÃƒÂ§Ã‚Â¤Ã¢â€žÂ¢ */
        if (pc.hasSkillEffect(SLOW) || pc.hasSkillEffect(MASS_SLOW)
                || pc.hasSkillEffect(ENTANGLE)) {
            if (pc.hasSkillEffect(SLOW)) {
                pc.killSkillEffectTimer(SLOW);
            } else if (pc.hasSkillEffect(MASS_SLOW)) {
                pc.killSkillEffectTimer(MASS_SLOW);
            } else if (pc.hasSkillEffect(ENTANGLE)) {
                pc.killSkillEffectTimer(ENTANGLE);
            }
            pc.sendPackets(new S_SkillHaste(objId, 0, 0));
            pc.broadcastPacket(new S_SkillHaste(objId, 0, 0));
        }

        pc.setSkillEffect(STATUS_HASTE, timeMillis);

        pc.sendPackets(new S_SkillSound(objId, 191));
        pc.broadcastPacket(new S_SkillSound(objId, 191));
        pc.sendPackets(new S_SkillHaste(objId, 1, timeMillis / 1000));
        pc.broadcastPacket(new S_SkillHaste(objId, 1, 0));
        pc.sendPackets(new S_ServerMessage(184));
        pc.setMoveSpeed(1);
    }

    public static void brave(L1PcInstance pc, int timeMillis) {
        if (pc.hasSkillEffect(STATUS_BRAVE)) {
            pc.killSkillEffectTimer(STATUS_BRAVE);
        }
        if (pc.hasSkillEffect(STATUS_ELFBRAVE)) {
            pc.killSkillEffectTimer(STATUS_ELFBRAVE);
        }
        if (pc.hasSkillEffect(HOLY_WALK)) {
            pc.killSkillEffectTimer(HOLY_WALK);
        }
        if (pc.hasSkillEffect(MOVING_ACCELERATION)) {
            pc.killSkillEffectTimer(MOVING_ACCELERATION);
        }
        if (pc.hasSkillEffect(WIND_WALK)) {
            pc.killSkillEffectTimer(WIND_WALK);
        }
        if (pc.hasSkillEffect(STATUS_BRAVE2)) {
            pc.killSkillEffectTimer(STATUS_BRAVE2);
        }

        pc.setSkillEffect(STATUS_BRAVE, timeMillis);

        int objId = pc.getId();
        pc.sendPackets(new S_SkillSound(objId, 751));
        pc.broadcastPacket(new S_SkillSound(objId, 751));
        pc.sendPackets(new S_SkillBrave(objId, 1, timeMillis / 1000));
        pc.broadcastPacket(new S_SkillBrave(objId, 1, 0));
        pc.setBraveSpeed(1);
    }

    public static void thirdSpeed(L1PcInstance pc) {
        if (pc.hasSkillEffect(STATUS_THIRD_SPEED)) {
            pc.killSkillEffectTimer(STATUS_THIRD_SPEED);
        }

        pc.setSkillEffect(STATUS_THIRD_SPEED, 600 * 1000);

        pc.sendPackets(new S_SkillSound(pc.getId(), 8031));
        pc.broadcastPacket(new S_SkillSound(pc.getId(), 8031));
        pc.sendPackets(new S_Liquor(pc.getId(), 8));
        pc.broadcastPacket(new S_Liquor(pc.getId(), 8));
        pc.sendPackets(new S_ServerMessage(1065));
    }

    public static void bloodstain(L1PcInstance pc, byte type, int time, boolean showGfx) {
        if (showGfx) {
            pc.sendPackets(new S_SkillSound(pc.getId(), 7783));
            pc.broadcastPacket(new S_SkillSound(pc.getId(), 7783));
        }

        int skillId = EFFECT_BLOODSTAIN_OF_ANTHARAS;
        int iconType = 0;
        if (type == 0) {
            if (!pc.hasSkillEffect(skillId)) {
                pc.addAc(-2);
                pc.addWater(50);
            }
            iconType = 82;
        } else if (type == 1) {
            skillId = EFFECT_BLOODSTAIN_OF_FAFURION;
            if (!pc.hasSkillEffect(skillId)) {
                pc.addWind(50);
            }
            iconType = 85;
        }
        pc.sendPackets(new S_OwnCharAttrDef(pc));
        pc.sendPackets(new S_SkillIconBloodstain(iconType, time));
        pc.setSkillEffect(skillId, (time * 60 * 1000));
    }

    public static void effectBlessOfDragonSlayer(L1PcInstance pc, int skillId, int time, int showGfx) {
        if (showGfx != 0) {
            pc.sendPackets(new S_SkillSound(pc.getId(), showGfx));
            pc.broadcastPacket(new S_SkillSound(pc.getId(), showGfx));
        }

        if (!pc.hasSkillEffect(skillId)) {
            switch (skillId) {
                case EFFECT_BLESS_OF_CRAY:
                    if (pc.hasSkillEffect(EFFECT_BLESS_OF_SAELL)) {
                        pc.removeSkillEffect(EFFECT_BLESS_OF_SAELL);
                    }
                    pc.addMaxHp(100);
                    pc.addMaxMp(50);
                    pc.addHpr(3);
                    pc.addMpr(3);
                    pc.addEarth(30);
                    pc.addDmgup(1);
                    pc.addHitup(5);
                    pc.addWeightReduction(40);
                    break;
                case EFFECT_BLESS_OF_SAELL:
                    if (pc.hasSkillEffect(EFFECT_BLESS_OF_CRAY)) {
                        pc.removeSkillEffect(EFFECT_BLESS_OF_CRAY);
                    }
                    pc.addMaxHp(80);
                    pc.addMaxMp(10);
                    pc.addWater(30);
                    pc.addAc(-8);
                    break;
            }
            pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
            if (pc.isInParty()) {
                pc.getParty().updateMiniHP(pc);
            }
            pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
            pc.sendPackets(new S_OwnCharStatus2(pc));
            pc.sendPackets(new S_OwnCharAttrDef(pc));
        }
        pc.setSkillEffect(skillId, (time * 1000));
    }

    public static int skillEffect(L1Character _user, L1Character cha, L1Character _target, int skillId, int _getBuffIconDuration, int dmg)     {
        L1PcInstance _player = null;
        if (_user instanceof L1PcInstance) {
            L1PcInstance _pc = (L1PcInstance) _user;
            _player = _pc;
        }

        switch (skillId) {
            case CURE_POISON:
                cha.curePoison();
                break;
            case REMOVE_CURSE:
                cha.curePoison();
                if (cha.hasSkillEffect(STATUS_CURSE_PARALYZING)
                        || cha.hasSkillEffect(STATUS_CURSE_PARALYZED)) {
                    cha.cureParalaysis();
                }
                break;
            case RESURRECTION:
            case GREATER_RESURRECTION:
                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    if (_player.getId() != pc.getId()) {
                        if (L1World.getInstance().getVisiblePlayer(pc, 0).size() > 0) {
                            for (L1PcInstance visiblePc : L1World.getInstance().getVisiblePlayer(pc, 0)) {
                                if (!visiblePc.isDead()) {
                                    _player.sendPackets(new S_ServerMessage(592));
                                    return 0;
                                }
                            }
                        }
                        if ((pc.getCurrentHp() == 0) && pc.isDead()) {
                            if (pc.getMap().isUseResurrection()) {
                                if (skillId == RESURRECTION) {
                                    pc.setGres(false);
                                } else if (skillId == GREATER_RESURRECTION) {
                                    pc.setGres(true);
                                }
                                pc.setTempID(_player.getId());
                                pc.sendPackets(new S_Message_YN(322, ""));
                            }
                        }
                    }
                }
                else if (cha instanceof L1NpcInstance)
                {
                    if (!(cha instanceof L1TowerInstance)) {
                        L1NpcInstance npc = (L1NpcInstance) cha;
                        if (npc.getNpcTemplate().isCantResurrect() && !(npc instanceof L1PetInstance)) {
                            return 0;
                        }
                        if ((npc instanceof L1PetInstance) && (L1World.getInstance().getVisiblePlayer(npc, 0).size() > 0)) {
                            for (L1PcInstance visiblePc : L1World.getInstance().getVisiblePlayer(npc, 0)) {
                                if (!visiblePc.isDead()) {
                                    _player.sendPackets(new S_ServerMessage(592));
                                    return 0;
                                }
                            }
                        }
                        if ((npc.getCurrentHp() == 0) && npc.isDead()) {
                            if ((npc instanceof L1PetInstance)) {
                                L1PetInstance pet = (L1PetInstance) npc;
                                npc.resurrect(npc.getMaxHp() / 4);
                                npc.setResurrect(true);
                                pet.startFoodTimer(pet);
                                pet.startHpRegeneration();
                                pet.startMpRegeneration();
                            }
                        }
                    }
                }
                break;
            case CALL_OF_NATURE:
                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    if (_player.getId() != pc.getId()) {
                        if (L1World.getInstance().getVisiblePlayer(pc, 0).size() > 0) {
                            for (L1PcInstance visiblePc : L1World.getInstance()
                                    .getVisiblePlayer(pc, 0)) {
                                if (!visiblePc.isDead()) {
                                    _player.sendPackets(new S_ServerMessage(592));
                                    return 0;
                                }
                            }
                        }
                        if ((pc.getCurrentHp() == 0) && pc.isDead()) {
                            pc.setTempID(_player.getId());
                            pc.sendPackets(new S_Message_YN(322, ""));
                        }
                    }
                } else if (cha instanceof L1NpcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    if (!(cha instanceof L1TowerInstance)) {
                        L1NpcInstance npc = (L1NpcInstance) cha;
                        if (npc.getNpcTemplate().isCantResurrect() && !(npc instanceof L1PetInstance)) {
                            return 0;
                        }
                        if ((npc instanceof L1PetInstance) && (L1World.getInstance().getVisiblePlayer(npc, 0).size() > 0)) {
                            for (L1PcInstance visiblePc : L1World.getInstance().getVisiblePlayer(npc, 0)) {
                                if (!visiblePc.isDead()) {
                                    _player.sendPackets(new S_ServerMessage(592));
                                    return 0;
                                }
                            }
                        }
                        if ((npc.getCurrentHp() == 0) && npc.isDead()) {
                            if ((npc instanceof L1PetInstance)) {
                                L1PetInstance pet = (L1PetInstance) npc;
                                npc.resurrect(cha.getMaxHp());
                                npc.resurrect(cha.getMaxMp() / 100);
                                npc.setResurrect(true);
                                pet.startFoodTimer(pet);
                                pet.startHpRegeneration();
                                pet.startMpRegeneration();
                            }
                        }
                    }
                }
                else
                {
                    L1PcInstance pc = (L1PcInstance) cha;
                }
                break;
            case DETECTION:
                if (cha instanceof L1NpcInstance) {
                    L1NpcInstance npc = (L1NpcInstance) cha;
                    int hiddenStatus = npc.getHiddenStatus();
                    if (hiddenStatus == L1NpcInstance.HIDDEN_STATUS_SINK) {
                        npc.appearOnGround(_player);
                    }
                }
                break;
            case ELEMENTAL_FALL_DOWN:
                if (_user instanceof L1PcInstance) {
                    int playerAttr = _player.getElfAttr();
                    int i = -50;
                    if (playerAttr != 0) {
                        _player.sendPackets(new S_SkillSound(cha.getId(), 4396));
                        _player.broadcastPacket(new S_SkillSound(cha.getId(), 4396));
                    }
                    switch (playerAttr) {
                        case 0:
                            _player.sendPackets(new S_ServerMessage(79));
                            break;
                        case 1:
                            cha.addEarth(i);
                            cha.setAddAttrKind(1);
                            break;
                        case 2:
                            cha.addFire(i);
                            cha.setAddAttrKind(2);
                            break;
                        case 4:
                            cha.addWater(i);
                            cha.setAddAttrKind(4);
                            break;
                        case 8:
                            cha.addWind(i);
                            cha.setAddAttrKind(8);
                            break;
                        default:
                            break;
                    }
                }
                break;

            case TRIPLE_ARROW:
                boolean gfxcheck = false;
                int[] BowGFX = { 138, 37, 3860, 3126, 3420, 2284, 3105, 3145, 3148,
                        3151, 3871, 4125, 2323, 3892, 3895, 3898, 3901, 4917, 4918,
                        4919, 4950, 6087, 6140, 6145, 6150, 6155, 6160, 6269, 6272,
                        6275, 6278, 6826, 6827, 6836, 6837, 6846, 6847, 6856, 6857,
                        6866, 6867, 6876, 6877, 6886, 6887, 8719, 8786, 8792, 8798,
                        8804, 8808, 8860, 8900, 8913, 9225, 9226 };
                int playerGFX = _player.getTempCharGfx();
                for (int gfx : BowGFX) {
                    if (playerGFX == gfx) {
                        gfxcheck = true;
                        break;
                    }
                }
                if (!gfxcheck) {
                    return 0;
                }

                for (int i = 3; i > 0; i--) {
                    _target.onAction(_player);
                }
                _player.sendPackets(new S_SkillSound(_player.getId(), 4394));
                _player.broadcastPacket(new S_SkillSound(_player.getId(), 4394));
                break;
            case FOE_SLAYER:
                        /*
                        _player.setFoeSlayer(true);
                        for (int i = 3; i > 0; i--)
                        {
                                _target.onAction(_player);
                        }
                        _player.setFoeSlayer(false);

                        _player.sendPackets(new S_EffectLocation(_target.getX(), _target.getY(), 6509));
                        _player.broadcastPacket(new S_EffectLocation(_target.getX(),_target.getY(), 6509));
                        _player.sendPackets(new S_SkillSound(_player.getId(), 7020));
                        _player.broadcastPacket(new S_SkillSound(_player.getId(), 7020));

                        if (_player.hasSkillEffect(SPECIAL_EFFECT_WEAKNESS_LV1))
                        {
                                _player.killSkillEffectTimer(SPECIAL_EFFECT_WEAKNESS_LV1);
                                _player.sendPackets(new S_SkillIconGFX(75, 0));
                        }
                        else if (_player.hasSkillEffect(SPECIAL_EFFECT_WEAKNESS_LV2))
                        {
                                _player.killSkillEffectTimer(SPECIAL_EFFECT_WEAKNESS_LV2);
                                _player.sendPackets(new S_SkillIconGFX(75, 0));
                        }
                        else if (_player.hasSkillEffect(SPECIAL_EFFECT_WEAKNESS_LV3))
                        {
                                _player.killSkillEffectTimer(SPECIAL_EFFECT_WEAKNESS_LV3);
                                _player.sendPackets(new S_SkillIconGFX(75, 0));
                        }
                        break;
                */
                _player.setFoeSlayer(true);
                for (int i = 3; i > 0; i--)
                {
                    _target.onAction(_player);
                }
                _player.setFoeSlayer(false);
                _player.sendPackets(new S_EffectLocation(_target.getX(), _target.getY(), 6509));
                _player.broadcastPacket(new S_EffectLocation(_target.getX(), _target.getY(), 6509));
                _player.sendPackets(new S_SkillSound(_player.getId(), 7020));
                _player.broadcastPacket(new S_SkillSound(_player.getId(), 7020));

                if (_player.hasSkillEffect(5001))
                {
                    _player.killSkillEffectTimer(5001);
                    _player.sendPackets(new S_SkillIconGFX(75, 0));
                }
                else if (_player.hasSkillEffect(5002))
                {
                    _player.killSkillEffectTimer(5002);
                    _player.sendPackets(new S_SkillIconGFX(75, 0));
                }
                else if (_player.hasSkillEffect(5003))
                {
                    _player.killSkillEffectTimer(5003);
                    _player.sendPackets(new S_SkillIconGFX(75, 0));
                }
                break;
            case SMASH:
                _target.onAction(_player, SMASH);
                break;


            case CONFUSION:
                if (_user instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) _user;
                    if (!cha.hasSkillEffect(CONFUSION)) {
                        int change = Random.nextInt(100) + 1;
                        if (change < (30 + Random.nextInt(11))) {
                            pc.sendPackets(new S_SkillSound(cha.getId(), 6525));
                            pc.broadcastPacket(new S_SkillSound(cha.getId(), 6525));
                            cha.setSkillEffect(CONFUSION, 2 * 1000);
                            cha.setSkillEffect(CONFUSION_ING, 8 * 1000);
                            if (cha instanceof L1PcInstance) {
                                L1PcInstance targetPc = (L1PcInstance) cha;
                                targetPc.sendPackets(new S_ServerMessage(1339));
                            }
                        }
                    }
                }
                break;
            case CURSE_BLIND:
            case DARKNESS:
                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    if (pc.hasSkillEffect(STATUS_FLOATING_EYE)) {
                        pc.sendPackets(new S_CurseBlind(2));
                    } else {
                        pc.sendPackets(new S_CurseBlind(1));
                    }
                }
                break;
            case CURSE_POISON:
                L1DamagePoison.doInfection(_user, cha, 3000, 5);
                break;
            case CURSE_PARALYZE:
            case CURSE_PARALYZE2:
                if (!cha.hasSkillEffect(EARTH_BIND) && !cha.hasSkillEffect(ICE_LANCE) && !cha.hasSkillEffect(FREEZING_BLIZZARD) && !cha.hasSkillEffect(FREEZING_BREATH)) {
                    int holdDuration = L1Hold.Hold(_user,cha,skillId);
                    if (cha instanceof L1PcInstance) {
                        L1CurseParalysis.curse(cha, 8000, 8000+holdDuration);
                    } else if (cha instanceof L1MonsterInstance) {
                        L1CurseParalysis.curse(cha, 8000, 8000+holdDuration);
                    }
                }
                break;
            case WEAKNESS:
                cha.addDmgup(-5);
                cha.addHitup(-1);
                break;
            case DISEASE:
                cha.addDmgup(-6);
                cha.addAc(12);
                break;
            case WIND_SHACKLE:
                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    pc.sendPackets(new S_SkillIconWindShackle(pc.getId(),
                            _getBuffIconDuration));
                    pc.broadcastPacket(new S_SkillIconWindShackle(pc.getId(),
                            _getBuffIconDuration));
                }
                break;
            case CANCELLATION:
                if(cha.isDead())
                {
                    break;
                }
                if (cha instanceof L1NpcInstance) {
                    L1NpcInstance npc = (L1NpcInstance) cha;
                    int npcId = npc.getNpcTemplate().get_npcId();
                    if (npcId == 71092) {
                        if (npc.getGfxId() == npc.getTempCharGfx()) {
                            npc.setTempCharGfx(1314);
                            npc.broadcastPacket(new S_NpcChangeShape(npc.getId(),
                                    1314, npc.getLawful(), npc.getStatus()));
                            return 0;
                        } else {
                            return 0;
                        }
                    }
                    if (npcId == 45640) {
                        if (npc.getGfxId() == npc.getTempCharGfx()) {
                            npc.setCurrentHp(npc.getMaxHp());
                            npc.setTempCharGfx(2332);
                            npc.broadcastPacket(new S_NpcChangeShape(npc.getId(),
                                    2332, npc.getLawful(), npc.getStatus()));
                            npc.setName("$2103");
                            npc.setNameId("$2103");
                            npc.broadcastPacket(new S_ChangeName(npc.getId(),
                                    "$2103"));
                        } else if (npc.getTempCharGfx() == 2332) {
                            npc.setCurrentHp(npc.getMaxHp());
                            npc.setTempCharGfx(2755);
                            npc.broadcastPacket(new S_NpcChangeShape(npc.getId(),
                                    2755, npc.getLawful(), npc.getStatus()));
                            npc.setName("$2488");
                            npc.setNameId("$2488");
                            npc.broadcastPacket(new S_ChangeName(npc.getId(),
                                    "$2488"));
                        }
                    }
                    if (npcId == 81209) {
                        if (npc.getGfxId() == npc.getTempCharGfx()) {
                            npc.setTempCharGfx(4310);
                            npc.broadcastPacket(new S_NpcChangeShape(npc.getId(),
                                    4310, npc.getLawful(), npc.getStatus()));
                            return 0;
                        } else {
                            return 0;
                        }
                    }
                    if (npcId == 81352) {
                        if (npc.getGfxId() == npc.getTempCharGfx()) {
                            npc.setTempCharGfx(148);
                            npc.broadcastPacket(new S_NpcChangeShape(npc.getId(),
                                    148, npc.getLawful(), npc.getStatus()));
                            npc.setName("$6068");
                            npc.setNameId("$6068");
                            npc.broadcastPacket(new S_ChangeName(npc.getId(),
                                    "$6068"));
                        }
                    }
                }

                if ((_player != null) && _player.isInvisble()) {
                    _player.delInvis();
                }

                if (!(cha instanceof L1PcInstance)) {
                    L1NpcInstance npc = (L1NpcInstance) cha;
                    npc.setMoveSpeed(0);
                    npc.setBraveSpeed(0);
                    npc.broadcastPacket(new S_SkillHaste(cha.getId(), 0, 0));
                    npc.broadcastPacket(new S_SkillBrave(cha.getId(), 0, 0));
                    npc.setWeaponBreaked(false);
                    npc.setParalyzed(false);
                    npc.setParalysisTime(0);
                }

                cha.curePoison();
                cha.cureParalaysis();
                cha.removeSkillEffect(STATUS_FREEZE);

                if(cha instanceof L1PcInstance)
                {
                    L1PcInstance pc = (L1PcInstance) cha;
                    L1SkillUse _su = new L1SkillUse();

                    Map<Integer, L1SkillTimer> map = new HashMap<Integer, L1SkillTimer>();
                    map = pc.getBuffs();
                    for (Entry<Integer, L1SkillTimer> entry : map.entrySet())
                    {
                        int skillID = entry.getKey();
                        L1SkillTimer skillDuration = entry.getValue();
                        if (isNotCancelable(skillID))
                        {
                            pc.removeSkillEffect(skillID);
                        }
                        else
                        {
                            pc.setSkillEffect(skillID, skillDuration.getRemainingTime());
                            _su.sendIcon(pc, skillID,skillDuration.getRemainingTime());
                        }
                    }

                    L1PolyMorph.undoPoly(pc);
                    pc.sendPackets(new S_CharVisualUpdate(pc));
                    pc.broadcastPacket(new S_CharVisualUpdate(pc));

                    if (pc.getHasteItemEquipped() > 0) {
                        pc.setMoveSpeed(0);
                        pc.sendPackets(new S_SkillHaste(pc.getId(), 0, 0));
                        pc.broadcastPacket(new S_SkillHaste(pc.getId(), 0, 0));
                    }

                    if (pc.isPrivateShop()) {
                        pc.sendPackets(new S_DoActionShop(pc.getId(),ActionCodes.ACTION_Shop, pc.getShopChat()));
                        pc.broadcastPacket(new S_DoActionShop(pc.getId(),ActionCodes.ACTION_Shop, pc.getShopChat()));
                    }
                    if (_user instanceof L1PcInstance) {
                        L1PinkName.onAction(pc, _user);
                    }
                }
                break;
            case FOG_OF_SLEEPING:
                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_SLEEP, true));
                }
                cha.setSleeped(true);
                break;

            case PHANTASM:
                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_SLEEP, true));
                }
                cha.setSleeped(true);
                break;
            case GUARD_BRAKE:
                cha.addAc(15);
                break;
            case HORROR_OF_DEATH:

                cha.addStr(-3);
                cha.addInt(-3);
                break;
            case PANIC:
                cha.addStr((byte) -1);
                cha.addCon((byte) -1);
                cha.addDex((byte) -1);
                cha.addWis((byte) -1);
                cha.addInt((byte) -1);
                break;
            case RESIST_FEAR:
                cha.addNdodge((byte) 5);
                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    pc.sendPackets(new S_PacketBox(101, pc.getNdodge()));
                }
                break;
            case RETURN_TO_NATURE:
                if (Config.RETURN_TO_NATURE && (cha instanceof L1SummonInstance)) {
                    L1SummonInstance summon = (L1SummonInstance) cha;
                    summon.broadcastPacket(new S_SkillSound(summon.getId(), 2245));
                    summon.returnToNature();
                } else {
                    if (_user instanceof L1PcInstance) {
                        _player.sendPackets(new S_ServerMessage(79));
                    }
                }
                break;
            case WEAPON_BREAK:
                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    L1ItemInstance weapon = pc.getWeapon();
                    if (weapon != null) {
                        int weaponDamage = Random.nextInt(_user.getInt() / 3) + 1;
                        pc.sendPackets(new S_ServerMessage(268, weapon.getLogName()));
                        pc.getInventory().receiveDamage(weapon, weaponDamage);
                    }
                } else {
                    ((L1NpcInstance) cha).setWeaponBreaked(true);
                }
                break;

            case MIRROR_IMAGE:
            case UNCANNY_DODGE:
                if (_user instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) _user;
                    pc.addDodge((byte) 5);
                    pc.sendPackets(new S_PacketBox(88, pc.getDodge()));
                }
                break;
            case GLOWING_AURA:
                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    pc.addHitup(5);
                    pc.addBowHitup(5);
                    pc.addMr(20);
                    pc.sendPackets(new S_SPMR(pc));
                    pc.sendPackets(new S_SkillIconAura(113, _getBuffIconDuration));
                }
                break;
            case SHINING_AURA:
                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    pc.addAc(-8);
                    pc.sendPackets(new S_SkillIconAura(114, _getBuffIconDuration));
                }
                break;
            case BRAVE_AURA:
                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    pc.addDmgup(5);
                    pc.sendPackets(new S_SkillIconAura(116, _getBuffIconDuration));
                }
                break;
            case SHIELD:
                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    pc.addAc(-2);
                    pc.sendPackets(new S_SkillIconShield(5, _getBuffIconDuration));
                }
                break;
            case SHADOW_ARMOR:
                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    pc.addAc(-3);
                    pc.sendPackets(new S_SkillIconShield(3, _getBuffIconDuration));
                }
                break;
            case EARTH_SKIN:
                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    pc.addAc(-6);
                    pc.sendPackets(new S_SkillIconShield(6, _getBuffIconDuration));
                }
                break;
            case EARTH_BLESS:
                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    pc.addAc(-7);
                    pc.sendPackets(new S_SkillIconShield(7, _getBuffIconDuration));
                }
                break;
            case IRON_SKIN:
                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    pc.addAc(-10);
                    pc.sendPackets(new S_SkillIconShield(10, _getBuffIconDuration));
                }
                break;
            case PHYSICAL_ENCHANT_STR:
                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    pc.addStr((byte) 5);
                    pc.sendPackets(new S_Strup(pc, 5, _getBuffIconDuration));
                }
                break;
            case PHYSICAL_ENCHANT_DEX:
                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    pc.addDex((byte) 5);
                    pc.sendPackets(new S_Dexup(pc, 5, _getBuffIconDuration));
                }
                break;
            case DRESS_MIGHTY:
                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    pc.addStr((byte) 2);
                    pc.sendPackets(new S_Strup(pc, 2, _getBuffIconDuration));
                }
                break;
            case DRESS_DEXTERITY:
                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    pc.addDex((byte) 2);
                    pc.sendPackets(new S_Dexup(pc, 2, _getBuffIconDuration));
                }
                break;
            case RESIST_MAGIC:
                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    pc.addMr(10);
                    pc.sendPackets(new S_SPMR(pc));
                }
                break;
            case CLEAR_MIND:
                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    pc.addWis((byte) 3);
                    pc.resetBaseMr();
                }
                break;
            case RESIST_ELEMENTAL:
                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    pc.addWind(10);
                    pc.addWater(10);
                    pc.addFire(10);
                    pc.addEarth(10);
                    pc.sendPackets(new S_OwnCharAttrDef(pc));
                }
                break;
            case ELEMENTAL_PROTECTION:
                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    int attr = pc.getElfAttr();
                    if (attr == 1) {
                        pc.addEarth(50);
                    } else if (attr == 2) {
                        pc.addFire(50);
                    } else if (attr == 4) {
                        pc.addWater(50);
                    } else if (attr == 8) {
                        pc.addWind(50);
                    }
                }
                break;
            case BODY_TO_MIND:
                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    pc.setCurrentMp(pc.getCurrentMp() + 2);
                }
                break;
            case BLOODY_SOUL:
                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    pc.setCurrentMp(pc.getCurrentMp() + 18);
                }
                break;
            case INVISIBILITY:
            case BLIND_HIDING:
                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    pc.sendPackets(new S_Invis(pc.getId(), 1));
                    pc.broadcastPacketForFindInvis(new S_RemoveObject(pc), false);
                }
                break;
            case FIRE_WEAPON:
                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    pc.addDmgup(4);
                    pc.sendPackets(new S_SkillIconAura(147, _getBuffIconDuration));
                }
                break;
            case FIRE_BLESS:
                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    pc.addDmgup(4);
                    pc.sendPackets(new S_SkillIconAura(154, _getBuffIconDuration));
                }
                break;
            case BURNING_WEAPON:
                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    pc.addDmgup(6);
                    pc.addHitup(3);
                    pc.sendPackets(new S_SkillIconAura(162, _getBuffIconDuration));
                }
                break;
            case WIND_SHOT:
                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    pc.addBowHitup(6);
                    pc.sendPackets(new S_SkillIconAura(148, _getBuffIconDuration));
                }
                break;
            case STORM_EYE:
                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    pc.addBowHitup(2);
                    pc.addBowDmgup(3);
                    pc.sendPackets(new S_SkillIconAura(155, _getBuffIconDuration));
                }
                break;
            case STORM_SHOT:
                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    pc.addBowDmgup(6);
                    pc.addBowHitup(2);
                    pc.sendPackets(new S_SkillIconAura(165, _getBuffIconDuration));
                }
                break;
            case BERSERKERS:
                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    pc.addAc(10);
                    pc.addDmgup(5);
                    pc.addHitup(2);
                }
                break;
            case SHAPE_CHANGE:
                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    pc.sendPackets(new S_ShowPolyList(pc.getId()));
                    if (!pc.isShapeChange()) {
                        pc.setShapeChange(true);
                    }
                }
                break;
            case ADVANCE_SPIRIT:
                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    pc.setAdvenHp(pc.getBaseMaxHp() / 5);
                    pc.setAdvenMp(pc.getBaseMaxMp() / 5);
                    pc.addMaxHp(pc.getAdvenHp());
                    pc.addMaxMp(pc.getAdvenMp());
                    pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
                    if (pc.isInParty()) {
                        pc.getParty().updateMiniHP(pc);
                    }
                    pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
                }
                break;
            case HOLY_WALK:
            case MOVING_ACCELERATION:
            case WIND_WALK:
                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    pc.setBraveSpeed(4);
                    pc.sendPackets(new S_SkillBrave(pc.getId(), 4,_getBuffIconDuration));
                    pc.broadcastPacket(new S_SkillBrave(pc.getId(), 4, 0));
                }
                break;
            case BLOODLUST:
                if ((cha instanceof L1PcInstance)) {
                    L1PcInstance pc = (L1PcInstance)cha;
                    L1ItemInstance item = new L1ItemInstance();
                    Potion.Brave(pc, item , 999999);
                }
                break;
            case AWAKEN_ANTHARAS:
            case AWAKEN_FAFURION:
            case AWAKEN_VALAKAS:
                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    L1Awake.start(pc, skillId);
                }
                break;
            case ILLUSION_OGRE:
                cha.addDmgup(4);
                cha.addHitup(4);
                cha.addBowDmgup(4);
                cha.addBowHitup(4);
                break;
            case ILLUSION_LICH:
                cha.addSp(2);
                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    pc.sendPackets(new S_SPMR(pc));
                }
                break;
            case ILLUSION_DIA_GOLEM:
                cha.addAc(-20);
                break;
            case ILLUSION_AVATAR:
                cha.addDmgup(10);
                cha.addBowDmgup(10);
                break;
            case INSIGHT:
                cha.addStr((byte) 1);
                cha.addCon((byte) 1);
                cha.addDex((byte) 1);
                cha.addWis((byte) 1);
                cha.addInt((byte) 1);
                break;
            case ABSOLUTE_BARRIER:
                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    pc.stopHpRegeneration();
                    pc.stopMpRegeneration();
                    pc.stopHpRegenerationByDoll();
                    pc.stopMpRegenerationByDoll();
                }
                break;
            case MEDITATION:
                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    pc.addMpr(5);
                }
                break;
            case CONCENTRATION:
                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    pc.addMpr(2);
                }
                break;

            case WEAK_ELEMENTAL:
                if (cha instanceof L1MonsterInstance) {
                    L1Npc npcTemp = ((L1MonsterInstance) cha).getNpcTemplate();
                    int weakAttr = npcTemp.get_weakAttr();
                    if ((weakAttr & 1) == 1) {
                        cha.broadcastPacket(new S_SkillSound(cha.getId(), 2169));
                    } else if ((weakAttr & 2) == 2) {
                        cha.broadcastPacket(new S_SkillSound(cha.getId(), 2166));
                    } else if ((weakAttr & 4) == 4) {
                        cha.broadcastPacket(new S_SkillSound(cha.getId(), 2167));
                    } else if ((weakAttr & 8) == 8) {
                        cha.broadcastPacket(new S_SkillSound(cha.getId(), 2168));
                    } else {
                        if (_user instanceof L1PcInstance) {
                            _player.sendPackets(new S_ServerMessage(79));
                        }
                    }
                } else {
                    if (_user instanceof L1PcInstance) {
                        _player.sendPackets(new S_ServerMessage(79));
                    }
                }
                break;

            case TELEPORT_TO_MATHER:
                if (_user instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    if (pc.getMap().isEscapable() || pc.isGm()) {
                        L1Teleport.teleport(pc, 33051, 32337, (short) 4, 5, true);
                    } else {
                        pc.sendPackets(new S_ServerMessage(276));
                        pc.sendPackets(new S_Paralysis(
                                S_Paralysis.TYPE_TELEPORT_UNLOCK, true));
                    }
                }
                break;

            case SUMMON_MONSTER:
                if (_user instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    int level = pc.getLevel();
                    int[] summons;
                    if (pc.getMap().isRecallPets()) {
                        if (pc.getInventory().checkEquipped(20284)) {
                            pc.sendPackets(new S_ShowSummonList(pc.getId()));
                            if (!pc.isSummonMonster()) {
                                pc.setSummonMonster(true);
                            }
                        } else {
                                                /*
                                                 * summons = new int[] { 81083, 81084, 81085, 81086,
                                                 * 81087, 81088, 81089 };
                                                 */
                            summons = new int[] { 81210, 81213, 81216, 81219,
                                    81222, 81225, 81228 };
                            int summonid = 0;
                            int summoncost = 8;
                            int levelRange = 32;
                            for (int i = 0; i < summons.length; i++) {
                                if ((level < levelRange)
                                        || (i == summons.length - 1)) {
                                    summonid = summons[i];
                                    break;
                                }
                                levelRange += 4;
                            }

                            int petcost = 0;
                            Object[] petlist = pc.getPetList().values().toArray();
                            for (Object pet : petlist) {
                                petcost += ((L1NpcInstance) pet).getPetcost();
                            }
                            int pcCha = pc.getCha();
                            if (pcCha > 34) {
                                pcCha = 34;
                            }
                            int charisma = pcCha + 6 - petcost;
                            int summoncount = charisma / summoncost;
                            L1Npc npcTemp = NpcTable.getInstance().getTemplate(
                                    summonid);
                            for (int i = 0; i < summoncount; i++) {
                                L1SummonInstance summon = new L1SummonInstance(
                                        npcTemp, pc);
                                summon.setPetcost(summoncost);
                            }
                        }
                    } else {
                        pc.sendPackets(new S_ServerMessage(79));
                    }
                }
                break;
            case LESSER_ELEMENTAL:
            case GREATER_ELEMENTAL:
                if (_user instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    int attr = pc.getElfAttr();
                    if (attr != 0) {
                        if (pc.getMap().isRecallPets()) {
                            int petcost = 0;
                            for (L1NpcInstance petNpc : pc.getPetList().values()) {
                                petcost += petNpc.getPetcost();
                            }

                            if (petcost == 0) {
                                int summonid = 0;
                                int summons[];
                                if (skillId == LESSER_ELEMENTAL) {
                                    summons = new int[] { 45306, 45303, 45304,
                                            45305 };
                                } else {

                                    summons = new int[] { 81053, 81050, 81051,
                                            81052 };
                                }
                                int npcattr = 1;
                                for (int i = 0; i < summons.length; i++) {
                                    if (npcattr == attr) {
                                        summonid = summons[i];
                                        i = summons.length;
                                    }
                                    npcattr *= 2;
                                }
                                if (summonid == 0) {

                                    int k3 = Random.nextInt(4);
                                    summonid = summons[k3];
                                }

                                L1Npc npcTemp = NpcTable.getInstance().getTemplate(
                                        summonid);
                                L1SummonInstance summon = new L1SummonInstance(
                                        npcTemp, pc);
                                summon.setPetcost(pc.getCha() + 7);
                            }
                        } else {
                            pc.sendPackets(new S_ServerMessage(79));
                        }
                    } else {
                        pc.sendPackets(new S_ServerMessage(79));
                    }
                }
                break;
            case TAMING_MONSTER:
                if (cha instanceof L1MonsterInstance) {
                    L1MonsterInstance npc = (L1MonsterInstance) cha;
                    if (npc.getNpcTemplate().isTamable()) {
                        int petcost = 0;
                        Object[] petlist = _user.getPetList().values().toArray();
                        for (Object pet : petlist) {
                            petcost += ((L1NpcInstance) pet).getPetcost();
                        }
                        int charisma = _user.getCha();
                        if (_player.isElf()) {
                            if (charisma > 30) {
                                charisma = 30;
                            }
                            charisma += 12;
                        } else if (_player.isWizard()) {
                            if (charisma > 36) {
                                charisma = 36;
                            }
                            charisma += 6;
                        }
                        charisma -= petcost;
                        if (charisma >= 6) {
                            L1SummonInstance summon = new L1SummonInstance(npc,
                                    _user, false);
                            _target = summon;
                        } else {
                            _player.sendPackets(new S_ServerMessage(319));
                        }
                    }
                }
                break;
            case CREATE_ZOMBIE:
                if (cha instanceof L1MonsterInstance) {
                    L1MonsterInstance npc = (L1MonsterInstance) cha;
                    int petcost = 0;
                    Object[] petlist = _user.getPetList().values().toArray();
                    for (Object pet : petlist) {
                        petcost += ((L1NpcInstance) pet).getPetcost();
                    }
                    int charisma = _user.getCha();
                    if (_player.isElf()) {
                        if (charisma > 30) {
                            charisma = 30;
                        }
                        charisma += 12;
                    } else if (_player.isWizard()) {
                        if (charisma > 36) {
                            charisma = 36;
                        }
                        charisma += 6;
                    }
                    charisma -= petcost;
                    if (charisma >= 6) {
                        L1SummonInstance summon = new L1SummonInstance(npc, _user,
                                true);
                        _target = summon;
                    } else {
                        _player.sendPackets(new S_ServerMessage(319));
                    }
                }
                break;

            case 10026:
            case 10027:
            case 10028:
            case 10029:
                if (_user instanceof L1NpcInstance) {
                    L1NpcInstance npc = (L1NpcInstance) _user;
                    _user.broadcastPacket(new S_NpcChatPacket(npc, "$3717", 0));
                } else {
                    _player.broadcastPacket(new S_ChatPacket(_player, "$3717", 0, 0));
                }
                break;
            case 10057:
                L1Teleport.teleportToTargetFront(cha, _user, 1);
                break;
            case STATUS_FREEZE:
                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_BIND, true));
                }
                break;
            default:
                break;
        }

        return dmg;
    }

    private static boolean isNotCancelable(int skillNum)
    {
        HashMap<Integer,Boolean> map = new HashMap<Integer ,Boolean>();
        map.put(ENCHANT_WEAPON, false);
        map.put(BLESSED_ARMOR, false);
        map.put(ABSOLUTE_BARRIER, false);
        map.put(ADVANCE_SPIRIT, false);
        map.put(SHOCK_STUN, false);
        map.put(REDUCTION_ARMOR, false);
        map.put(SOLID_CARRIAGE, false);
        map.put(COUNTER_BARRIER, false);
        map.put(AWAKEN_ANTHARAS, false);
        map.put(AWAKEN_FAFURION, false);
        map.put(AWAKEN_VALAKAS, false);
        map.put(COOKING_WONDER_DRUG, false);
        map.put(1015, false);
        map.put(1014, false);
        map.put(ENCHANT_VENOM, false);
        map.put(UNCANNY_DODGE, false);
        map.put(DRESS_EVASION, false);
        map.put(VENOM_RESIST, false);

        Set set = map.entrySet();
        Iterator i = set.iterator();

        while(i.hasNext()) {
            Map.Entry me = (Map.Entry)i.next();
            if(Integer.parseInt(me.getKey().toString()) ==  skillNum)
            {
                return (Boolean) me.getValue();
            }
        }
        return true;
    }
}


