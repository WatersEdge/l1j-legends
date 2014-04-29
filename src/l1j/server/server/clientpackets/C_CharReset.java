package l1j.server.server.clientpackets;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.server.ClientThread;
import l1j.server.server.datatables.CharacterTable;
import l1j.server.server.datatables.ExpTable;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.L1Attribute;
import l1j.server.server.model.L1PcInventory;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.classes.L1ClassFeature;
import l1j.server.server.serverpackets.S_CharReset;
import l1j.server.server.serverpackets.S_OwnCharStatus;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.utils.CalcInitHpMp;
import l1j.server.server.utils.CalcStat;

public class C_CharReset extends ClientBasePacket
{
    private static final String C_CHAR_RESET = "[C] C_CharReset";
    private static Logger _log = Logger.getLogger(C_CharReset.class.getName());

    private void checkProvidedStats(L1PcInstance pc, int str, int intel, int wis, int dex, int con, int cha)
    {
        Map fixedStats = pc.getClassFeature().getFixedStats();
        if ((str < ((Integer)fixedStats.get(L1Attribute.Str)).intValue()) ||
                (intel < ((Integer)fixedStats.get(L1Attribute.Int)).intValue()) ||
                (wis < ((Integer)fixedStats.get(L1Attribute.Wis)).intValue()) ||
                (dex < ((Integer)fixedStats.get(L1Attribute.Dex)).intValue()) ||
                (con < ((Integer)fixedStats.get(L1Attribute.Con)).intValue()) ||
                (cha < ((Integer)fixedStats.get(L1Attribute.Cha)).intValue()))
        {
            _log.log(Level.SEVERE,  String.format("Candle: %s had less than starting stats!", new Object[] { pc.getName() }));
        }

        //pc.sendPackets(new S_SystemMessage("str: " + str));
        //pc.sendPackets(new S_SystemMessage("intel: " + intel));
        //pc.sendPackets(new S_SystemMessage("wis: " + wis));
        //pc.sendPackets(new S_SystemMessage("dex: " + dex));
        //pc.sendPackets(new S_SystemMessage("con: " + con));
        //pc.sendPackets(new S_SystemMessage("cha: " + cha));

        int bonusStats = pc.getLevel() > 50 ? pc.getLevel() - 50 : 0;

        if (str + intel + wis + dex + con + cha > 75 + bonusStats + pc.getElixirStats())
            emergencyCleanup(pc, "Candle Issue: " + pc.getName() + " has too many stats!", "Candle: issue with stats, contact a GM for help.");
    }

    public C_CharReset(byte[] abyte0, ClientThread clientthread)
    {
        super(abyte0);
        L1PcInstance pc = clientthread.getActiveChar();

        int stage = readC();

        if (stage == 1)
        {
            int str = readC();
            int intel = readC();
            int wis = readC();
            int dex = readC();
            int con = readC();
            int cha = readC();

            checkProvidedStats(pc, str, intel, wis, dex, con, cha);

            int hp = CalcInitHpMp.calcInitHp(pc);
            int mp = CalcInitHpMp.calcInitMp(pc);

            //pc.sendPackets(new S_SystemMessage("inital hp: " + hp));


            pc.sendPackets(new S_CharReset(pc, 1, hp, mp, 10, str, intel, wis, dex, con, cha));

            initCharStatus(pc, hp, mp, str, intel, wis, dex, con, cha);

            CharacterTable.getInstance();
            CharacterTable.saveCharStatus(pc);

            pc.setOriginalStr(str);
            pc.setOriginalCon(con);
            pc.setOriginalDex(dex);
            pc.setOriginalCha(cha);
            pc.setOriginalInt(intel);
            pc.setOriginalWis(wis);
            pc.refresh();
        }
        else if (stage == 2) {
            int type2 = readC();
            if (type2 == 0) {
                setLevelUp(pc, 1);
            } else if (type2 == 7) {
                if (pc.getTempMaxLevel() - pc.getTempLevel() < 10) {
                    return;
                }
                setLevelUp(pc, 10);
            } else if (type2 == 1) {
                pc.addBaseStr((byte)1);
                setLevelUp(pc, 1);
            } else if (type2 == 2) {
                pc.addBaseInt((byte)1);
                setLevelUp(pc, 1);
            } else if (type2 == 3) {
                pc.addBaseWis((byte)1);
                setLevelUp(pc, 1);
            } else if (type2 == 4) {
                pc.addBaseDex((byte)1);
                setLevelUp(pc, 1);
            } else if (type2 == 5) {
                pc.addBaseCon((byte)1);
                setLevelUp(pc, 1);
            } else if (type2 == 6) {
                pc.addBaseCha((byte)1);
                setLevelUp(pc, 1);
            } else if (type2 == 8) {
                switch (readC()) {
                    case 1:
                        pc.addBaseStr((byte)1);
                        break;
                    case 2:
                        pc.addBaseInt((byte)1);
                        break;
                    case 3:
                        pc.addBaseWis((byte)1);
                        break;
                    case 4:
                        pc.addBaseDex((byte)1);
                        break;
                    case 5:
                        pc.addBaseCon((byte)1);
                        break;
                    case 6:
                        pc.addBaseCha((byte)1);
                }

                if (pc.getElixirStats() > 0) {
                    pc.sendPackets(new S_CharReset(pc.getElixirStats()));
                    return;
                }
                saveNewCharStatus(pc);
            }
        } else if (stage == 3)
        {
            int str = readC();
            int intel = readC();
            int wis = readC();
            int dex = readC();
            int con = readC();
            int cha = readC();

            checkProvidedStats(pc, str, intel, wis, dex, con, cha);

            pc.addBaseStr((byte)(str - pc.getBaseStr()));
            pc.addBaseInt((byte)(intel - pc.getBaseInt()));
            pc.addBaseWis((byte)(wis - pc.getBaseWis()));
            pc.addBaseDex((byte)(dex - pc.getBaseDex()));
            pc.addBaseCon((byte)(con - pc.getBaseCon()));
            pc.addBaseCha((byte)(cha - pc.getBaseCha()));
            saveNewCharStatus(pc);
        }
    }

    private void emergencyCleanup(L1PcInstance pc, String logEntry, String message)
    {
        _log.log(Level.SEVERE, logEntry);
        pc.sendPackets(new S_SystemMessage(message));
        L1Teleport.teleport(pc, 32628, 32772, (short)4, 4, false);
        throw new IllegalStateException();
    }

    private synchronized void saveNewCharStatus(L1PcInstance pc) {
        if (pc.getTempMaxLevel() != pc.getLevel()) {
            //System.out.println("saveNewCharStatus - pc.getTempMaLevel: " + pc.getTempMaxLevel() + "  !=  " + pc.getLevel());
            emergencyCleanup(pc, "Candle: " + pc.getName() + "'s level doesn't match!", "Candle: issue with level, contact a GM! - SaveNewCharStatus");
        }

        pc.setInCharReset(false);
        if (pc.getOriginalAc() > 0) {
            pc.addAc(pc.getOriginalAc());
        }
        if (pc.getOriginalMr() > 0) {
            pc.addMr(0 - pc.getOriginalMr());
        }
        pc.refresh();
        pc.setCurrentHp(pc.getMaxHp());
        pc.setCurrentMp(pc.getMaxMp());
        if (pc.getTempMaxLevel() != pc.getLevel()) {
            pc.setLevel(pc.getTempMaxLevel());
            pc.setExp(ExpTable.getExpByLevel(pc.getTempMaxLevel()));
        }
        if (pc.getLevel() > 50)
            pc.setBonusStats(pc.getLevel() - 50);
        else {
            pc.setBonusStats(0);
        }
        pc.sendPackets(new S_OwnCharStatus(pc));
        L1ItemInstance item = pc.getInventory().findItemId(49142);
        if (item != null) {
            try {
                pc.getInventory().removeItem(item, 1);
                pc.save();
            } catch (Exception e) {
                _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
            }
        }
        L1Teleport.teleport(pc, 32628, 32772, (short)4, 4, false);
    }

    private void initCharStatus(L1PcInstance pc, int hp, int mp, int str, int intel, int wis, int dex, int con, int cha) {
        //System.out.println("hp :"  + hp);
        //System.out.println("pc.getBaseMaxHp(): " + pc.getBaseMaxHp());
        pc.addBaseMaxHp((short)(hp - pc.getBaseMaxHp()));
        pc.addBaseMaxMp((short)(mp - pc.getBaseMaxMp()));
        pc.addBaseStr((byte)(str - pc.getBaseStr()));
        pc.addBaseInt((byte)(intel - pc.getBaseInt()));
        pc.addBaseWis((byte)(wis - pc.getBaseWis()));
        pc.addBaseDex((byte)(dex - pc.getBaseDex()));
        pc.addBaseCon((byte)(con - pc.getBaseCon()));
        pc.addBaseCha((byte)(cha - pc.getBaseCha()));
        pc.addMr(0 - pc.getMr());
        pc.addDmgup(0 - pc.getDmgup());
        pc.addHitup(0 - pc.getHitup());
    }

    private synchronized void setLevelUp(L1PcInstance pc, int addLv) {
        pc.setTempLevel(pc.getTempLevel() + addLv);
        for (int i = 0; i < addLv; i++) {
            short randomHp = CalcStat.calcStatHp(pc.getType(), pc.getBaseMaxHp(), pc.getBaseCon(), pc.getOriginalHpup(),pc.getOriginalCon());
            short randomMp = CalcStat.calcStatMp(pc.getType(), pc.getBaseWis(), pc.getOriginalWis());
            pc.addBaseMaxHp(randomHp);
            pc.addBaseMaxMp(randomMp);
        }
        int newAc = CalcStat.calcAc(pc.getTempLevel(), pc.getBaseDex());

        pc.sendPackets(new S_CharReset(pc, pc.getTempLevel(),
                pc.getBaseMaxHp(), pc.getBaseMaxMp(), newAc,
                pc.getBaseStr(), pc.getBaseInt(), pc.getBaseWis(),
                pc.getBaseDex(), pc.getBaseCon(), pc.getBaseCha()));
    }

    public String getType()
    {
        return "[C] C_CharReset";
    }
}