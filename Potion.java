package l1j.server.server.model.item.action;

import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.L1PcInventory;
import l1j.server.server.serverpackets.S_CurseBlind;
import l1j.server.server.serverpackets.S_Liquor;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SkillBrave;
import l1j.server.server.serverpackets.S_SkillHaste;
import l1j.server.server.serverpackets.S_SkillIconBlessOfEva;
import l1j.server.server.serverpackets.S_SkillIconGFX;
import l1j.server.server.serverpackets.S_SkillIconWisdomPotion;
import l1j.server.server.serverpackets.S_SkillSound;
import static l1j.server.server.model.skill.L1SkillId.*;

public class Potion
{
	
    public static void Brave(L1PcInstance pc, L1ItemInstance item, int item_id)
    {
    	// Declare player's weapon instance - [Hank]
    	L1ItemInstance weapon = null;
    	int weaponType = 0;
        if (pc.hasSkillEffect(71)) {
            pc.sendPackets(new S_ServerMessage(698));
            return;
        }

        int time = 0;

        if (pc.hasSkillEffect(1000))
            time = pc.getSkillEffectTimeSec(1000);
        else if (pc.hasSkillEffect(1016))
            time = pc.getSkillEffectTimeSec(1016);
        else if (pc.hasSkillEffect(1017)) {
            time = pc.getSkillEffectTimeSec(1017);
        }

        int addtime = 0;

        if ((item_id == 40014) || (item_id == 140014) || (item_id == 41415) || (item_id == 49305) || (item_id == 40031) || (item_id == 40733) || (item_id == 999999))
        {
            if (item_id == 40014)
            {
                addtime = 300;
            }
            else if (item_id == 140014)
            {
                addtime = 350;
            }
            else if (item_id == 41415)
            {
                addtime = 1800;
            }
            else if (item_id == 40031)
            {
                addtime = 600;
            }
            else if (item_id == 40733)
            {
                addtime = 600;
            }
            else if (item_id == 49305)
            {
                addtime = 1200;
            }
            else if (item_id == 999999)
            {
                addtime = 1800;
            }


            time = Math.min(time + addtime, 1800);


            if(item_id != 999999)
            {
                buff_brave(pc, 1000, (byte)1, time);
                pc.getInventory().removeItem(item, 1);
            }
            else
            {
                buff_brave(pc, 186, (byte)1, time);
            }
        }
        else if ((item_id == 40068) || (item_id == 140068) || (item_id == 49304)) {
            if (item_id == 40068)
            {
                addtime = 480;
            }
            else if (item_id == 140068)
            {
                addtime = 700;
            }
            else if (item_id == 49304)
            {
                addtime = 1920;
            }

            time = Math.min(time + addtime, 1800);
            
            
            // gives fire elf brave effect when he/she is using melee weapon - [Hank]
            weapon = pc.getWeapon();
            weaponType = weapon.getItem().getType1();
           //LEGENDS - Wafer For Fire Elf
            // weapon check - [Hank]
            if(pc.getElfAttr() == 2 && (weaponType == 4 || weaponType == 46 || weaponType == 24 || weaponType == 11))
            {
                buff_brave(pc, 1000, (byte)1, time);
            }
            else
            {
                buff_brave(pc, 1016, (byte)3, time);
            }

            pc.getInventory().removeItem(item, 1);

        }
        //JohnJohn Illusionist get wafer movement speed
        else if (item_id == 49158)
        {
            time = 700;
            pc.setSkillEffect(1017, time * 1000);
            pc.sendPackets(new S_SkillSound(pc.getId(), 7110));
            pc.broadcastPacket(new S_SkillSound(pc.getId(), 7110));
            pc.getInventory().removeItem(item, 1);
        }
    }

    public static void buff_brave(L1PcInstance pc, int skillId, byte type, int timeMillis)
    {
        if (pc.hasSkillEffect(1000)) {
            pc.killSkillEffectTimer(1000);
        }
        if (pc.hasSkillEffect(1016)) {
            pc.killSkillEffectTimer(1016);
        }
        if (pc.hasSkillEffect(52)) {
            pc.killSkillEffectTimer(52);
        }
        if (pc.hasSkillEffect(186)) {
            pc.killSkillEffectTimer(186);
        }
        if (pc.hasSkillEffect(101)) {
            pc.killSkillEffectTimer(101);
        }
        if (pc.hasSkillEffect(150)) {
            pc.killSkillEffectTimer(150);
        }
        if (pc.hasSkillEffect(1026)) {
            pc.killSkillEffectTimer(1026);
        }

        pc.setSkillEffect(skillId, timeMillis * 1000);
        pc.sendPackets(new S_SkillSound(pc.getId(), 751));
        pc.broadcastPacket(new S_SkillSound(pc.getId(), 751));
        pc.sendPackets(new S_SkillBrave(pc.getId(), type, timeMillis));
        pc.broadcastPacket(new S_SkillBrave(pc.getId(), type, 0));
        pc.setBraveSpeed(type);
    }

    public static void ThirdSpeed(L1PcInstance pc, L1ItemInstance item, int time)
    {
        if (pc.hasSkillEffect(71)) {
            pc.sendPackets(new S_ServerMessage(698));
            return;
        }

        if (pc.hasSkillEffect(1027)) {
            pc.killSkillEffectTimer(1027);
        }

        pc.setSkillEffect(1027, time * 1000);

        pc.sendPackets(new S_SkillSound(pc.getId(), 8031));
        pc.broadcastPacket(new S_SkillSound(pc.getId(), 8031));
        pc.sendPackets(new S_Liquor(pc.getId(), 8));
        pc.broadcastPacket(new S_Liquor(pc.getId(), 8));
        pc.sendPackets(new S_ServerMessage(1065));
        pc.getInventory().removeItem(item, 1);
    }

    public static void UseHeallingPotion(L1PcInstance pc, L1ItemInstance item, int healHp, int gfxid) {
        if (pc.hasSkillEffect(71)) {
            pc.sendPackets(new S_ServerMessage(698));
            return;
        }

        pc.sendPackets(new S_SkillSound(pc.getId(), gfxid));
        pc.broadcastPacket(new S_SkillSound(pc.getId(), gfxid));

        if (pc.getPotionMessages()) {
            pc.sendPackets(new S_ServerMessage(77));
        }

        healHp = (int)(healHp * (new java.util.Random().nextGaussian() / 5.0D + 1.0D));
        if (pc.hasSkillEffect(173)) {
            healHp /= 2;
        }
        pc.setCurrentHp(pc.getCurrentHp() + healHp);
        pc.getInventory().removeItem(item, 1);
    }

    public static void UseMpPotion(L1PcInstance pc, L1ItemInstance item, int mp, int i) {
        if (pc.hasSkillEffect(71)) {
            pc.sendPackets(new S_ServerMessage(698));
            return;
        }

        pc.sendPackets(new S_SkillSound(pc.getId(), 190));
        pc.broadcastPacket(new S_SkillSound(pc.getId(), 190));
        pc.sendPackets(new S_ServerMessage(338, "$1084"));
        int newMp = 0;
        if (i > 0)
            newMp = l1j.server.server.utils.Random.nextInt(i, mp);
        else {
            newMp = mp;
        }
        pc.setCurrentMp(pc.getCurrentMp() + newMp);
        pc.getInventory().removeItem(item, 1);
    }

    public static void useGreenPotion(L1PcInstance pc, L1ItemInstance item, int itemId) {
        if (pc.hasSkillEffect(71)) {
            pc.sendPackets(new S_ServerMessage(698));
            return;
        }

        int time = pc.getSkillEffectTimeSec(1001);
        time = time < 0 ? 0 : time;

        int addtime = 0;

        if ((itemId == 40013) || (itemId == 40030))
            addtime = 300;
        else if (itemId == 140013)
            addtime = 350;
        else if ((itemId == 40018) || (itemId == 41338) ||
                (itemId == 41342) || (itemId == 49140))
            addtime = 1800;
        else if (itemId == 140018)
            addtime = 2100;
        else if (itemId == 40039)
            addtime = 600;
        else if (itemId == 40040)
            addtime = 900;
        else if (itemId == 49302)
            addtime = 1200;
        else if ((itemId == 41261) || (itemId == 41262) || (itemId == 41268) ||
                (itemId == 41269) || (itemId == 41271) || (itemId == 41272) ||
                (itemId == 41273)) {
            addtime = 30;
        }

        time = Math.min(time + addtime, 2100);

        pc.sendPackets(new S_SkillSound(pc.getId(), 191));
        pc.broadcastPacket(new S_SkillSound(pc.getId(), 191));

        if (pc.getHasteItemEquipped() > 0)
        {
            pc.removeHasteSkillEffect();
            if (pc.getMoveSpeed() != 1) {
                pc.setMoveSpeed(1);
                pc.sendPackets(new S_SkillHaste(pc.getId(), 1, -1));
                pc.broadcastPacket(new S_SkillHaste(pc.getId(), 1, 0));
            }
            return;
        }

        pc.setDrink(false);

        if (pc.hasSkillEffect(43)) {
            pc.killSkillEffectTimer(43);
            pc.sendPackets(new S_SkillHaste(pc.getId(), 0, 0));
            pc.broadcastPacket(new S_SkillHaste(pc.getId(), 0, 0));
            pc.setMoveSpeed(0);
        }
        else if (pc.hasSkillEffect(54)) {
            pc.killSkillEffectTimer(54);
            pc.sendPackets(new S_SkillHaste(pc.getId(), 0, 0));
            pc.broadcastPacket(new S_SkillHaste(pc.getId(), 0, 0));
            pc.setMoveSpeed(0);
        }
        else if (pc.hasSkillEffect(1001)) {
            pc.killSkillEffectTimer(1001);
            pc.sendPackets(new S_SkillHaste(pc.getId(), 0, 0));
            pc.broadcastPacket(new S_SkillHaste(pc.getId(), 0, 0));
            pc.setMoveSpeed(0);
        }

        if (pc.hasSkillEffect(29)) {
            pc.killSkillEffectTimer(29);
            pc.sendPackets(new S_SkillHaste(pc.getId(), 0, 0));
            pc.broadcastPacket(new S_SkillHaste(pc.getId(), 0, 0));
        }
        else if (pc.hasSkillEffect(76)) {
            pc.killSkillEffectTimer(76);
            pc.sendPackets(new S_SkillHaste(pc.getId(), 0, 0));
            pc.broadcastPacket(new S_SkillHaste(pc.getId(), 0, 0));
        }
        else if (pc.hasSkillEffect(152)) {
            pc.killSkillEffectTimer(152);
            pc.sendPackets(new S_SkillHaste(pc.getId(), 0, 0));
            pc.broadcastPacket(new S_SkillHaste(pc.getId(), 0, 0));
        }
        else {
            pc.sendPackets(new S_SkillHaste(pc.getId(), 1, time));
            pc.broadcastPacket(new S_SkillHaste(pc.getId(), 1, 0));
            pc.setMoveSpeed(1);
            pc.setSkillEffect(1001, time * 1000);
        }
        pc.getInventory().removeItem(item, 1);
    }

    public static void useBluePotion(L1PcInstance pc, L1ItemInstance item, int item_id) {
        if (pc.hasSkillEffect(71)) {
            pc.sendPackets(new S_ServerMessage(698));
            return;
        }

        int time = 0;

        if ((item_id == 40015) || (item_id == 40736))
        {
            time = 600;
        }
        else if (item_id == 90008) //Greater Mana Potion, Currently only gives longer duration need to make it give more MPRegen - [Legends]
        {
            time = 900;
        }
        else if (item_id == 140015)
        {
            time = 1200;
        }

        if (pc.hasSkillEffect(1002)) {
            pc.killSkillEffectTimer(1002);
        }
        pc.sendPackets(new S_SkillIconGFX(34, time));
        pc.sendPackets(new S_SkillSound(pc.getId(), 190));
        pc.broadcastPacket(new S_SkillSound(pc.getId(), 190));
        pc.sendPackets(new S_ServerMessage(1007));

        pc.setSkillEffect(1002, time * 1000);
        pc.getInventory().removeItem(item, 1);
    }

    public static void useWisdomPotion(L1PcInstance pc, L1ItemInstance item, int item_id) {
        if (pc.hasSkillEffect(71)) {
            pc.sendPackets(new S_ServerMessage(698));
            return;
        }

        int time = 0;

        if (pc.hasSkillEffect(1004)) {
            time = pc.getSkillEffectTimeSec(1004);
        }
        int addtime = 0;

        if (item_id == 40016)
            addtime = 300;
        else if (item_id == 140016)
            addtime = 360;
        else if (item_id == 49307) {
            addtime = 1000;
        }

        time = Math.min(time + addtime, 1000);

        if (!pc.hasSkillEffect(1004))
            pc.addSp(2);
        else {
            pc.killSkillEffectTimer(1004);
        }

        pc.sendPackets(new S_SkillIconWisdomPotion(time / 4));
        pc.sendPackets(new S_SkillSound(pc.getId(), 750));
        pc.broadcastPacket(new S_SkillSound(pc.getId(), 750));

        pc.setSkillEffect(1004, time * 1000);
        pc.getInventory().removeItem(item, 1);
    }

    public static void useBlessOfEva(L1PcInstance pc, L1ItemInstance item, int item_id) {
        if (pc.hasSkillEffect(71)) {
            pc.sendPackets(new S_ServerMessage(698));
            return;
        }

        int time = 0;
        if (item_id == 40032)
            time = 1800;
        else if (item_id == 40041)
            time = 300;
        else if (item_id == 41344)
            time = 2100;
        else if (item_id == 49303) {
            time = 7200;
        }

        if (pc.hasSkillEffect(1003)) {
            int timeSec = pc.getSkillEffectTimeSec(1003);
            time += timeSec;
            if (time > 7200) {
                time = 7200;
            }
            pc.killSkillEffectTimer(1003);
        }
        pc.sendPackets(new S_SkillIconBlessOfEva(pc.getId(), time));
        pc.sendPackets(new S_SkillSound(pc.getId(), 190));
        pc.broadcastPacket(new S_SkillSound(pc.getId(), 190));
        pc.setSkillEffect(1003, time * 1000);
        pc.getInventory().removeItem(item, 1);
    }

    public static void useBlindPotion(L1PcInstance pc, L1ItemInstance item) {
        if (pc.hasSkillEffect(71)) {
            pc.sendPackets(new S_ServerMessage(698));
            return;
        }

        int time = 16;
        if (pc.hasSkillEffect(20))
            pc.killSkillEffectTimer(20);
        else if (pc.hasSkillEffect(40)) {
            pc.killSkillEffectTimer(40);
        }

        if (pc.hasSkillEffect(1012))
            pc.sendPackets(new S_CurseBlind(2));
        else {
            pc.sendPackets(new S_CurseBlind(1));
        }

        pc.setSkillEffect(20, time * 1000);
        pc.getInventory().removeItem(item, 1);
    }
}