package l1j.server.server;

import static l1j.server.server.model.skill.L1SkillId.ADDITIONAL_FIRE;
import static l1j.server.server.model.skill.L1SkillId.BERSERKERS;
import static l1j.server.server.model.skill.L1SkillId.COOKING_1_2_N;
import static l1j.server.server.model.skill.L1SkillId.COOKING_1_2_S;
import static l1j.server.server.model.skill.L1SkillId.COOKING_1_5_N;
import static l1j.server.server.model.skill.L1SkillId.COOKING_1_5_S;
import static l1j.server.server.model.skill.L1SkillId.COOKING_2_4_N;
import static l1j.server.server.model.skill.L1SkillId.COOKING_2_4_S;
import static l1j.server.server.model.skill.L1SkillId.COOKING_3_5_N;
import static l1j.server.server.model.skill.L1SkillId.COOKING_3_5_S;
import static l1j.server.server.model.skill.L1SkillId.COOKING_3_6_N;
import static l1j.server.server.model.skill.L1SkillId.COOKING_3_6_S;
import static l1j.server.server.model.skill.L1SkillId.EXOTIC_VITALIZE;
import static l1j.server.server.model.skill.L1SkillId.NATURES_TOUCH;
import static l1j.server.server.model.skill.L1SkillId.STATUS_BLUE_POTION;
import static l1j.server.server.model.skill.L1SkillId.STATUS_UNDERWATER_BREATH;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.Config;
import l1j.server.L1DatabaseFactory;
import l1j.server.server.datatables.SkillsTable;
import l1j.server.server.model.Instance.L1EffectInstance;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.L1CastleLocation;
import l1j.server.server.model.L1HouseLocation;
import l1j.server.server.model.L1Location;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1Party;
import l1j.server.server.model.L1PcInventory;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.item.L1ItemId;
import l1j.server.server.model.map.L1Map;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.model.skill.L1SkillUse;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.serverpackets.ServerBasePacket;
import l1j.server.server.templates.L1Skills;
import l1j.server.server.types.Point;
import l1j.server.server.utils.Random;
import l1j.server.server.utils.SQLUtil;

public class PCommands
{
    private static Logger _log = Logger.getLogger(PCommands.class.getName());
    private static PCommands _instance;
    private static int[] PowerBuffSkills = { 2, 14,
            26, 42, 48, 54,
            55, 68, 78, 79,
            166, 151, 158 };

    private static int[] BuffSkills = { 2, 3, 8,
            14, 26, 42,
            48, 43 };

    private static final S_SystemMessage DropHelp = new S_SystemMessage("-drop [all|mine|party] [on|off] toggles drop messages.");
    private static final S_SystemMessage CommandsHelp = new S_SystemMessage("-warp 1-11, -karma, -buff, -bug, -drop, -help, -dkbuff, -dmg, -potions, -pvpchat");
    private static final S_SystemMessage CommandsHelpNoBuff = new S_SystemMessage("-warp 1-11, -karma, -bug, -drop, -help");
    private static final S_SystemMessage NoBuff = new S_SystemMessage("The -buff command is disabled.");
    private static final S_SystemMessage BuffLevel = new S_SystemMessage("You must be level 45 to use -buff.");
    private static final S_SystemMessage NoWarpArea = new S_SystemMessage("You cannot -warp in this area.");
    private static final S_SystemMessage NoWarpState = new S_SystemMessage("You cannot -warp in your current state.");
    private static final S_SystemMessage NoWarp = new S_SystemMessage("The -warp command is disabled.");
    private static final S_SystemMessage NoPowerBuff = new S_SystemMessage("The -pbuff command is disabled.");
    private static final S_SystemMessage WarpLimit = new S_SystemMessage("-warp 1-12 only.");
    private static final S_SystemMessage WarpHelp = new S_SystemMessage("1-Pandora, 2-SKT, 3-Giran, 4-Werld, 5-Oren, 6-Orc Town, 7-Silent Cave, 8-Trade, 9-WW, 10-Behimous, 11-Silveria, 12-Elven Mother");
    private static final S_SystemMessage BugHelp = new S_SystemMessage("-bug bugReport");
    private static final S_SystemMessage BugThanks = new S_SystemMessage("Bug reported. Thank you for your help!");
    private static final S_SystemMessage NotDK = new S_SystemMessage("Only Dragon Knights can use -dkbuff.");
    private static final S_SystemMessage DKHelp = new S_SystemMessage("You have to equip Helm of Magic to use -dkbuff.");
    private static final S_SystemMessage NoMp = new S_SystemMessage("You don't have enough mana to use -dkbuff.");
    private static final S_SystemMessage NoDkBuff = new S_SystemMessage("The -dkbuff command is disabled.");
    private static final S_SystemMessage DmgHelp = new S_SystemMessage("dmg [on|off] toggles damage messages.");
    private static final S_SystemMessage PotionHelp = new S_SystemMessage("potion [on|off] toggles healing potion messages.");
    private static final S_SystemMessage NoAutoTurning = new S_SystemMessage("The -turn command is disabled.");
    private static final S_SystemMessage OnlyDarkElvesTurn = new S_SystemMessage("Only Dark Elves can use -turn.");
    private static final S_SystemMessage RollHelp = new S_SystemMessage("-roll integer[1 to 1000]");
    private static final ServerBasePacket DmgRHelp = new S_SystemMessage("dmgr [on|off] toggles damage recieved messages.");;

    public static PCommands getInstance()
    {
        if (_instance == null) {
            _instance = new PCommands();
        }
        return _instance;
    }

    public void handleCommands(L1PcInstance player, String cmd2) {
        try {
            if (cmd2.equalsIgnoreCase("help"))
                showPHelp(player);
            else if (cmd2.startsWith("buff"))
                buff(player);
            else if (cmd2.startsWith("stat"))
                showStat(player);
            else if (cmd2.startsWith("dkbuff"))
                dkbuff(player);
            else if (cmd2.startsWith("warp"))
                warp(player, cmd2);
            else if (cmd2.startsWith("pbuff"))
                powerBuff(player);
            else if (cmd2.startsWith("bug"))
                reportBug(player, cmd2);
            else if (cmd2.startsWith("karma"))
                checkKarma(player);
            else if (cmd2.startsWith("drop"))
                setDropOptions(player, cmd2);
            else if (cmd2.startsWith("dmgr"))
                setDmgROptions(player, cmd2);
            else if (cmd2.startsWith("dmg"))
                setDmgOptions(player, cmd2);
            else if (cmd2.startsWith("potions"))
                setPotionOptions(player, cmd2);
            else if (cmd2.startsWith("turn")) {
                turnAllStones(player);
            }
            else if (cmd2.startsWith("roll")) {
                roll(player, cmd2);
            }
            _log.log(Level.FINE, player.getName() + " used " + cmd2);
        } catch (Exception e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        }
    }

    public void showPHelp(L1PcInstance player) {
        player.sendPackets((Config.PLAYER_BUFF) && (Config.PLAYER_COMMANDS) ?
                CommandsHelp : CommandsHelpNoBuff);
    }


    // Adding true MPR - [Hank]
    public int mpRegen(L1PcInstance player)
    {
        int baseMpr = 1;
        int wis = player.getWis();
        if (player.hasSkillEffect(STATUS_BLUE_POTION)) {
            if (wis < 11) {
                wis = 11;
            }
            baseMpr += wis - 10 + 5;
        }

        if (L1HouseLocation.isInHouse(player.getX(), player.getY(), player.getMapId())) {
            baseMpr += 8;
        }
        //Castles - If they are inside the castle and they are in the pledge that owns the castle.
        if(L1CastleLocation.getCastleId(player.getX(), player.getY(), player.getMapId()) >0 && L1CastleLocation.getCastleId(player.getX(), player.getY(), player.getMapId()) == player.getClan().getCastleId())
        {
            baseMpr +=10;
        }

        //Hotels/Inns
        if ((player.getMapId() == 16384) || (player.getMapId() == 16896) || (player.getMapId() == 17408) || (player.getMapId() == 17920)
                || (player.getMapId() == 18432) || (player.getMapId() == 18944) || (player.getMapId() == 19968) || (player.getMapId() == 19456)
                || (player.getMapId() == 20480) || (player.getMapId() == 20992) || (player.getMapId() == 21504) || (player.getMapId() == 22016)
                || (player.getMapId() == 22528) || (player.getMapId() == 23040) || (player.getMapId() == 23552) || (player.getMapId() == 24064)
                || (player.getMapId() == 24576) || (player.getMapId() == 25088)) {
            baseMpr += 5;
        }
        //Mother Tree and Elf
        if ((player.getLocation().isInScreen(new Point(33055, 32336)) && (player.getMapId() == 4) && player.isElf())) {
            baseMpr += 3;
        }

        if (player.hasSkillEffect(COOKING_1_2_N) || player.hasSkillEffect(COOKING_1_2_S)) {
            baseMpr += 3;
        }
        if (player.hasSkillEffect(COOKING_2_4_N) || player.hasSkillEffect(COOKING_2_4_S) || player.hasSkillEffect(COOKING_3_5_N)
                || player.hasSkillEffect(COOKING_3_5_S)) {
            baseMpr += 2;
        }
        if (player.getOriginalMpr() > 0) {
            baseMpr += player.getOriginalMpr();
        }
        int itemMpr = player.getInventory().mpRegenPerTick();
        int mpPluss = player.getWis() - 12;
        if (mpPluss <= 12){
            mpPluss = 12;
        }
        int finalMpr = baseMpr + itemMpr + mpPluss;
        if(isOverWeight(player))
        {
            finalMpr = 0;
        }
        return finalMpr;

    }

    // Adding true HPR - [Hank]
    public int hpRegen(L1PcInstance player)
    {
        int maxBonus = 1;

        if (11 < player.getLevel() && 14 <= player.getCon()) {
            maxBonus = player.getCon() - 12;
            if (25 < player.getCon()) {
                maxBonus = 14;
            }
        }

        int equipHpr = player.getInventory().hpRegenPerTick();
        equipHpr += player.getHpr();
        int bonus = Random.nextInt(maxBonus) + 1;

        if (player.hasSkillEffect(NATURES_TOUCH)) {
            bonus += 15;
        }
        if (L1HouseLocation.isInHouse(player.getX(), player.getY(), player.getMapId())) {
            bonus += 5;
        }
        if (player.getMapId() == 16384 || player.getMapId() == 16896
                || player.getMapId() == 17408 || player.getMapId() == 17920
                || player.getMapId() == 18432 || player.getMapId() == 18944
                || player.getMapId() == 19968 || player.getMapId() == 19456
                || player.getMapId() == 20480 || player.getMapId() == 20992
                || player.getMapId() == 21504 || player.getMapId() == 22016
                || player.getMapId() == 22528 || player.getMapId() == 23040
                || player.getMapId() == 23552 || player.getMapId() == 24064
                || player.getMapId() == 24576 || player.getMapId() == 25088) {
            bonus += 5;
        }
        if ((player.getLocation().isInScreen(new Point(33055,32336))
                && player.getMapId() == 4 && player.isElf())) {
            bonus += 5;
        }
        if (player.hasSkillEffect(COOKING_1_5_N)
                || player.hasSkillEffect(COOKING_1_5_S)) {
            bonus += 3;
        }
        if (player.hasSkillEffect(COOKING_2_4_N)
                || player.hasSkillEffect(COOKING_2_4_S)
                || player.hasSkillEffect(COOKING_3_6_N)
                || player.hasSkillEffect(COOKING_3_6_S)) {
            bonus += 2;
        }
        if (player.getOriginalHpr() > 0) {
            bonus += player.getOriginalHpr();
        }

        if (player.get_food() < 3 || isOverWeight(player)
                || player.hasSkillEffect(BERSERKERS)) {
            bonus = 0;
            if (equipHpr > 0) {
                equipHpr = 0;
            }
        }

        // fixes the low con DE negative regen.
        int conHp = player.getCon() - 10;

        if (conHp < 0)
        {
            conHp = 1;
        }

        // adding hpr variable, if player is overeighted, hpr = 0 - [Hank]
        int hpr = bonus + equipHpr + conHp;
        if(isOverWeight(player))
        {
            hpr = 0;
        }
        return hpr;
    }

    // private overweight function - [Hank]
    private boolean isOverWeight(L1PcInstance pc) {
        if (pc.hasSkillEffect(EXOTIC_VITALIZE)
                || pc.hasSkillEffect(ADDITIONAL_FIRE)) {
            return false;
        }
        if (pc.getInventory().checkEquipped(20049)) {
            return false;
        }

        return (121 <= pc.getInventory().getWeight242()) ? true : false;
    }

    //[Legends] - New -stat Command
    public void showStat(L1PcInstance player) {
        String result = "Error Getting Stats";
        // true MPR/HPR - [Hank]
        int mpr = mpRegen(player);
        int hpr = hpRegen(player);
        try
        {
            result = "";
            result += "DR: " + player.getDamageReductionByArmor() + " | ";
            result += "HPR: " + hpr + " | ";
            result += "MPR: " + mpr + " | ";
            result += "+Hit: " + player.getHitModifierByArmor() + " | ";
            result += "+Dmg: " + player.getDmgModifierByArmor() + " | ";
            result += "Fire MR: " + player.getFire() + " | ";
            result += "Water MR: " + player.getWater() + " | ";
            result += "Wind MR: " + player.getWind() + " | ";
            result += "Earth MR: " + player.getEarth();
        }
        catch(Exception e)
        {

        }

        player.sendPackets(new S_SystemMessage(result));
    }

    public void buff(L1PcInstance player) {
        if ((!Config.PLAYER_BUFF) || (!Config.PLAYER_COMMANDS)) {
            player.sendPackets(NoBuff);
            return;
        }

        try
        {
            //[Legends] - Doppelganger Amulet
            if(player.getInventory().getItemEquipped(2,8).getItem().getItemId() == 20250)
            {

                if(player.getCurrentMp() >= 50)
                {
                    player.sendPackets(new S_SystemMessage("You harness the power of the doppelganger and begin to transform in the moonlight."));
                    new L1SkillUse().handleCommands(player, 67, player.getId(),player.getX(), player.getY(), null, 3600, L1SkillUse.TYPE_GMBUFF);
                    player.setCurrentMp(player.getCurrentMp()-50);
                }
                else
                {
                    player.sendPackets(new S_SystemMessage("The doppelgangers amulet requires 50 MP to cast."));
                }
            }
        }
        catch (Exception e) {
            //Do Nothing Just Skip It
        }
        //[Legends] - Shield Only
        L1SkillUse skillUse = new L1SkillUse();
        skillUse.handleCommands(player, L1SkillId.SHIELD, player.getId(),player.getX(), player.getY(), null, 0,2);
    }

    public void dkbuff(L1PcInstance player) {
        if (!Config.PLAYER_COMMANDS) {
            player.sendPackets(NoBuff);
            return;
        }

        if (!Config.DK_BUFF) {
            player.sendPackets(NoDkBuff);
            return;
        }

        if (!player.isDragonKnight()) {
            player.sendPackets(NotDK);
            return;
        }

        if (player.getCurrentMp() < 25) {
            player.sendPackets(NoMp);
            return;
        }

        L1SkillUse skillUse = new L1SkillUse();
        if (player.getInventory().checkEquipped(20013))
            skillUse.handleCommands(player, 26,
                    player.getId(), player.getX(), player.getY(), null, 0,
                    0);
        else if (player.getInventory().checkEquipped(20015))
            skillUse.handleCommands(player, 42,
                    player.getId(), player.getX(), player.getY(), null, 0,
                    0);
        else
            player.sendPackets(DKHelp);
    }

    public void powerBuff(L1PcInstance player) {
        if ((Config.POWER_BUFF) && (Config.PLAYER_COMMANDS)) {
            L1SkillUse skillUse = new L1SkillUse();
            for (int i = 0; i < PowerBuffSkills.length; i++)
                skillUse.handleCommands(player, PowerBuffSkills[i],
                        player.getId(), player.getX(), player.getY(), null, 0,
                        2);
        } else if ((Config.PLAYER_COMMANDS) && (!Config.POWER_BUFF)) {
            player.sendPackets(NoPowerBuff);
        }
    }

    public void warp(L1PcInstance player, String cmd2) { if (!Config.WARP) {
        player.sendPackets(NoWarp);
        return;
    }

        if (!player.getLocation().getMap().isEscapable()) {
            player.sendPackets(NoWarpArea);
            return;
        }

        if ((player.isPrivateShop()) || (player.hasSkillEffect(157)) ||
                (player.isParalyzed()) || (player.isPinkName()) ||
                (player.isSleeped()) || (player.isDead()) ||
                (player.getMapId() == 99) || player.hasSkillEffect(1008)) {
            player.sendPackets(NoWarpState);
            return;
        }
        try
        {
            int i = Integer.parseInt(cmd2.substring(5));
            Thread.sleep(3000L);
            switch (i) {
                case 1:
                    L1Teleport.teleport(player, 32644, 32955, (short)0, 5, true);
                    break;
                case 2:
                    L1Teleport.teleport(player, 33080, 33392, (short)4, 5, true);
                    break;
                case 3:
                    L1Teleport.teleport(player, 33442, 32797, (short)4, 5, true);
                    break;
                case 4:
                    L1Teleport.teleport(player, 33705, 32504, (short)4, 5, true);
                    break;
                case 5:
                    L1Teleport.teleport(player, 34061, 32276, (short)4, 5, true);
                    break;
                case 6:
                    L1Teleport.teleport(player, 32715, 32448, (short)4, 5, true);
                    break;
                case 7:
                    L1Teleport.teleport(player, 32857, 32898, (short)304, 5, true);
                    break;
                case 8:
                    L1Teleport.teleport(player, 32688, 32838, (short)350, 5, true);
                    break;
                case 9:
                    L1Teleport.teleport(player, 32628, 33204, (short)4, 5, true);
                    break;
                case 10:
                    L1Teleport.teleport(player, 32804, 32847, (short)1001, 5, true);
                    break;
                case 11:
                    L1Teleport.teleport(player, 32804, 32818, (short)1000, 5, true);
                    break;
                case 12:
                    L1Teleport.teleport(player, 33054, 32323, (short)4, 5, true);
                    break;
                default:
                    player.sendPackets(WarpLimit);
            }
        } catch (Exception exception) {
            player.sendPackets(WarpHelp);
        } }

    private void reportBug(L1PcInstance pc, String bug)
    {
        Connection con = null;
        PreparedStatement pstm = null;
        try
        {
            bug = bug.substring(3).trim();
            if (bug.equals("")) {
                pc.sendPackets(BugHelp);
                return;
            }
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con.prepareStatement("INSERT INTO bugs (bugtext, charname, mapID, mapX, mapY, resolved) VALUES (?, ?, ?, ?, ?, 0);");
            pstm.setString(1, bug);
            pstm.setString(2, pc.getName());
            pstm.setInt(3, pc.getMapId());
            pstm.setInt(4, pc.getX());
            pstm.setInt(5, pc.getY());
            pstm.execute();
            pc.sendPackets(BugThanks);
        } catch (Exception e) {
            pc.sendPackets(new S_SystemMessage("Could not report bug: ('" + bug + "', '" + pc.getName() + "', " + pc.getMapId() + ", " + pc.getX() + ", " + pc.getY() + ");"));
            //pc.sendPackets(new S_SystemMessage(e.getMessage().toString()));
            e.printStackTrace();
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    private void checkKarma(L1PcInstance pc) {
        pc.sendPackets(new S_SystemMessage("Your karma is currently: " +
                pc.getKarma() + "."));
    }

    private void setDropOptions(L1PcInstance pc, String options) {
        List pieces = Arrays.asList(options.split("\\s"));
        if (pieces.size() < 3) {
            pc.sendPackets(DropHelp);
            return;
        }
        boolean on = ((String)pieces.get(2)).equals("on");
        if (((String)pieces.get(1)).equals("all")) {
            pc.setDropMessages(on);
            pc.setPartyDropMessages(on);
        } else if (((String)pieces.get(1)).equals("party")) {
            pc.setPartyDropMessages(on);
        } else if (((String)pieces.get(1)).equals("mine")) {
            pc.setDropMessages(on);
        } else {
            pc.sendPackets(DropHelp);
        }
    }

    private void setDmgOptions(L1PcInstance pc, String options) {
        List pieces = Arrays.asList(options.split("\\s"));
        if (pieces.size() < 2) {
            pc.sendPackets(DmgHelp);
            return;
        }
        if (((String)pieces.get(1)).equals("on"))
            pc.setDmgMessages(true);
        else if (((String)pieces.get(1)).equals("off"))
            pc.setDmgMessages(false);
        else
            pc.sendPackets(DmgHelp);
    }

    private void setDmgROptions(L1PcInstance pc, String options) {
        List pieces = Arrays.asList(options.split("\\s"));
        if (pieces.size() < 2) {
            pc.sendPackets(DmgRHelp);
            return;
        }
        if (((String)pieces.get(1)).equals("on"))
            pc.setDmgRMessages(true);
        else if (((String)pieces.get(1)).equals("off"))
            pc.setDmgRMessages(false);
        else
            pc.sendPackets(DmgRHelp);
    }

    private void setPotionOptions(L1PcInstance pc, String options)
    {
        List pieces = Arrays.asList(options.split("\\s"));
        if (pieces.size() < 2) {
            pc.sendPackets(PotionHelp);
            return;
        }
        if (((String)pieces.get(1)).equals("on"))
            pc.setPotionMessages(true);
        else if (((String)pieces.get(1)).equals("off"))
            pc.setPotionMessages(false);
        else
            pc.sendPackets(PotionHelp);
    }

    private void turnAllStones(L1PcInstance player)
    {
        if (!Config.AUTO_STONE) {
            player.sendPackets(NoAutoTurning);
            return;
        }

        if ((!player.isDarkelf()) || (!player.isSkillMastery(100))) {
            player.sendPackets(OnlyDarkElvesTurn);
            return;
        }

        L1Skills skill = SkillsTable.getInstance().findBySkillId(100);
        int currentMana = player.getCurrentMp();
        int castingCost = skill.getMpConsume();
        for (int stone : L1ItemId.StoneList) {
            L1ItemInstance item = player.getInventory().findItemId(stone);
            if (item != null)
            {
                L1SkillUse.turnStone(player, item, 0.9D, Math.min(item.getCount(), currentMana / castingCost), false);
                player.setCurrentMp(player.getCurrentMp() % castingCost);
                break;
            }
        }
    }

    private void roll(L1PcInstance pc, String options) { List pieces = Arrays.asList(options.split("\\s"));
        if (pieces.size() < 2) {
            pc.sendPackets(RollHelp);
            return;
        }
        if (!isInteger((String)pieces.get(1))) {
            pc.sendPackets(RollHelp);
            return;
        }
        int rolln = Integer.parseInt((String)pieces.get(1));
        if ((rolln <= 1000) && (rolln >= 1)) {
            String result = Integer.toString(1 + Random.nextInt(rolln));
            if (pc.isInParty()) {
                L1PcInstance[] partyMember = pc.getParty().getMembers();
                for (L1PcInstance member : partyMember)
                    member.sendPackets(new S_SystemMessage(pc.getName() + " rolled " + result + " out of " + rolln + "."));
            }
            else
            {
                pc.sendPackets(new S_SystemMessage("The roll result is " + result + " out of " + rolln + "."));
            }
        } else {
            pc.sendPackets(RollHelp);
        } }

    private static boolean isInteger(String s)
    {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return false;
        }

        return true;
    }
}