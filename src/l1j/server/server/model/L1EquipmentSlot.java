package l1j.server.server.model;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import l1j.server.server.datatables.SkillsTable;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.*;
import l1j.server.server.templates.L1Item;
import l1j.server.server.utils.collections.Lists;

public class L1EquipmentSlot
{
    private L1PcInstance _owner;
    private static Logger _log = Logger.getLogger(L1EquipmentSlot.class
            .getName());
    private List<L1ArmorSet> _currentArmorSet;
    private L1ItemInstance _weapon;
    private List<L1ItemInstance> _armors;

    public L1EquipmentSlot(L1PcInstance owner)
    {
        this._owner = owner;

        this._armors = Lists.newList();
        this._currentArmorSet = Lists.newList();
    }

    private void setWeapon(L1ItemInstance weapon) {
        this._owner.setWeapon(weapon);
        this._owner.setCurrentWeapon(weapon.getItem().getType1());
        weapon.startEquipmentTimer(this._owner);
        this._weapon = weapon;
    }

    public L1ItemInstance getWeapon() {
        return this._weapon;
    }

    private void setArmor(L1ItemInstance armor) {
        L1Item item = armor.getItem();

        int type = item.getType();
        int ringCount = 0;
        L1Item equippedItem;
        for (L1ItemInstance equipped : this._armors) {
            equippedItem = equipped.getItem();
            if (equippedItem.getType() == 9) {
                ringCount++;
                if ((ringCount >= 2) && (type == 9)) {
                    _log.log(Level.WARNING,
                            "Tried to equip too many rings.");
                }
            }
            else if (equippedItem.getType() == type) {
                _log.log(Level.WARNING, "Tried to equip multiple items in same slot.");
                return;
            }
        }

        int itemId = armor.getItem().getItemId();

        if ((armor.getItem().getType2() == 2) && (armor.getItem().getType() >= 8) &&
                (armor.getItem().getType() <= 12))
            this._owner.addAc(item.get_ac() - armor.getAcByMagic());
        else {
            this._owner.addAc(item.get_ac() - armor.getEnchantLevel() -
                    armor.getAcByMagic());
        }
        this._owner.addDamageReductionByArmor(item.getDamageReduction());
        this._owner.addWeightReduction(item.getWeightReduction());
        this._owner.addHitModifierByArmor(item.getHitModifierByArmor());
        this._owner.addDmgModifierByArmor(item.getDmgModifierByArmor());
        this._owner.addBowHitModifierByArmor(item.getBowHitModifierByArmor());
        this._owner.addBowDmgModifierByArmor(item.getBowDmgModifierByArmor());
        this._owner.addRegistHold(item.get_regist_stun());

        this._owner.addEarth(item.get_defense_earth() + armor.getEarthMr());
        this._owner.addWind(item.get_defense_wind() + armor.getWindMr());
        this._owner.addWater(item.get_defense_water() + armor.getWaterMr());
        this._owner.addFire(item.get_defense_fire() + armor.getFireMr());

        this._armors.add(armor);

        for (L1ArmorSet armorSet : L1ArmorSet.getAllSet()) {
            if ((armorSet.isPartOfSet(itemId)) && (armorSet.isValid(this._owner))) {
                if ((armor.getItem().getType2() == 2) &&
                        (armor.getItem().getType() == 9)) {
                    if (!armorSet.isEquippedRingOfArmorSet(this._owner)) {
                        armorSet.giveEffect(this._owner);
                        this._currentArmorSet.add(armorSet);
                    }
                } else {
                    armorSet.giveEffect(this._owner);
                    this._currentArmorSet.add(armorSet);
                }
            }
        }

        if (((itemId == 20077) || (itemId == 20062) || (itemId == 120077)) &&
                (!this._owner.hasSkillEffect(60))) {
            this._owner.killSkillEffectTimer(97);
            this._owner.setSkillEffect(60, 0);
            this._owner.sendPackets(new S_Invis(this._owner.getId(), 1));
            this._owner.broadcastPacketForFindInvis(new S_RemoveObject(this._owner),
                    false);
        }

        if (itemId == 20288) {
            this._owner.sendPackets(new S_Ability(1, true));
        }
        if ((itemId == 20383) &&
                (armor.getChargeCount() != 0)) {
            armor.setChargeCount(armor.getChargeCount() - 1);
            this._owner.getInventory().updateItem(armor,
                    128);
        }

        armor.startEquipmentTimer(this._owner);
    }

    public List<L1ItemInstance> getArmors() {
        return this._armors;
    }

    private void removeWeapon(L1ItemInstance weapon) {
        this._owner.setWeapon(null);
        this._owner.setCurrentWeapon(0);
        weapon.stopEquipmentTimer(this._owner);
        this._weapon = null;
        if (this._owner.hasSkillEffect(91))
            this._owner.removeSkillEffect(91);
    }

    private void removeArmor(L1ItemInstance armor)
    {
        L1Item item = armor.getItem();
        int itemId = armor.getItem().getItemId();

        if ((armor.getItem().getType2() == 2) && (armor.getItem().getType() >= 8) &&
                (armor.getItem().getType() <= 12))
            this._owner.addAc(-(item.get_ac() - armor.getAcByMagic()));
        else {
            this._owner.addAc(
                    -(item.get_ac() - armor.getEnchantLevel() - armor
                            .getAcByMagic()));
        }
        this._owner.addDamageReductionByArmor(-item.getDamageReduction());
        this._owner.addWeightReduction(-item.getWeightReduction());
        this._owner.addHitModifierByArmor(-item.getHitModifierByArmor());
        this._owner.addDmgModifierByArmor(-item.getDmgModifierByArmor());
        this._owner.addBowHitModifierByArmor(-item.getBowHitModifierByArmor());
        this._owner.addBowDmgModifierByArmor(-item.getBowDmgModifierByArmor());
        this._owner.addRegistHold(-item.get_regist_stun());

        this._owner.addEarth(-item.get_defense_earth() - armor.getEarthMr());
        this._owner.addWind(-item.get_defense_wind() - armor.getWindMr());
        this._owner.addWater(-item.get_defense_water() - armor.getWaterMr());
        this._owner.addFire(-item.get_defense_fire() - armor.getFireMr());

        for (L1ArmorSet armorSet : L1ArmorSet.getAllSet()) {
            if ((armorSet.isPartOfSet(itemId)) &&
                    (this._currentArmorSet.contains(armorSet)) &&
                    (!armorSet.isValid(this._owner))) {
                armorSet.cancelEffect(this._owner);
                this._currentArmorSet.remove(armorSet);
            }
        }

        if ((itemId == 20077) || (itemId == 20062) || (itemId == 120077)) {
            this._owner.delInvis();
        }
        if (itemId == 20288) {
            this._owner.sendPackets(new S_Ability(1, false));
        }
        armor.stopEquipmentTimer(this._owner);

        this._armors.remove(armor);
    }

    public void set(L1ItemInstance equipment) {
        L1Item item = equipment.getItem();
        if (item.getType2() == 0) {
            return;
        }

        if (item.get_addhp() != 0) {
            this._owner.addMaxHp(item.get_addhp());
        }
        if (item.get_addmp() != 0) {
            this._owner.addMaxMp(item.get_addmp());
        }
        if (equipment.getaddHp() != 0) {
            this._owner.addMaxHp(equipment.getaddHp());
        }
        if (equipment.getaddMp() != 0) {
            this._owner.addMaxMp(equipment.getaddMp());
        }
        this._owner.addStr(item.get_addstr());
        this._owner.addCon(item.get_addcon());
        this._owner.addDex(item.get_adddex());
        this._owner.addInt(item.get_addint());
        this._owner.addWis(item.get_addwis());

        // [Legends] - Calculate Primary'
        if(this._owner.getPrimaryStat()== "Str")
        {
            this._owner.addStr(item.get_addprimary());
        }
        else if(this._owner.getPrimaryStat() == "Dex")
        {
            this._owner.addDex(item.get_addprimary());
        }
        else if(this._owner.getPrimaryStat() == "Int")
        {
            this._owner.addInt(item.get_addprimary());
        }


        if (item.get_addwis() != 0) {
            this._owner.resetBaseMr();
        }
        this._owner.addCha(item.get_addcha());

        int addMr = 0;
        addMr += equipment.getMr();
        if ((item.getItemId() == 20236) && (this._owner.isElf())) {
            addMr += 5;
        }
        if (addMr != 0) {
            this._owner.addMr(addMr);
            this._owner.sendPackets(new S_SPMR(this._owner));
        }
        if ((item.get_addsp() != 0) || (equipment.getaddSp() != 0)) {
            this._owner.addSp(item.get_addsp() + equipment.getaddSp());
            this._owner.sendPackets(new S_SPMR(this._owner));
        }
        if (item.isHasteItem()) {
            this._owner.addHasteItemEquipped(1);
            this._owner.removeHasteSkillEffect();
            if (this._owner.getMoveSpeed() != 1) {
                this._owner.setMoveSpeed(1);
                this._owner.sendPackets(new S_SkillHaste(this._owner.getId(), 1, -1));
                this._owner.broadcastPacket(new S_SkillHaste(this._owner.getId(), 1, 0));
            }
        }

        if ((item.getItemId() == 20383) &&
                (this._owner.hasSkillEffect(1000))) {
            this._owner.killSkillEffectTimer(1000);
            this._owner.sendPackets(new S_SkillBrave(this._owner.getId(), 0, 0));
            this._owner.broadcastPacket(new S_SkillBrave(this._owner.getId(), 0, 0));
            this._owner.setBraveSpeed(0);
        }

        this._owner.getEquipSlot().setMagicHelm(equipment);

        if (item.getType2() == 1) {
            setWeapon(equipment);
        } else if (item.getType2() == 2) {
            setArmor(equipment);
            this._owner.sendPackets(new S_SPMR(this._owner));
        }
    }

    public void remove(L1ItemInstance equipment) {
        L1Item item = equipment.getItem();
        if (item.getType2() == 0) {
            return;
        }

        if (item.get_addhp() != 0) {
            this._owner.addMaxHp(-item.get_addhp());
        }
        if (item.get_addmp() != 0) {
            this._owner.addMaxMp(-item.get_addmp());
        }
        if (equipment.getaddHp() != 0) {
            this._owner.addMaxHp(-equipment.getaddHp());
        }
        if (equipment.getaddMp() != 0) {
            this._owner.addMaxMp(-equipment.getaddMp());
        }
        this._owner.addStr((byte)-item.get_addstr());
        this._owner.addCon((byte)-item.get_addcon());
        this._owner.addDex((byte)-item.get_adddex());
        this._owner.addInt((byte)-item.get_addint());
        this._owner.addWis((byte)-item.get_addwis());

        if(this._owner.getPrimaryStat()== "Str")
        {
            this._owner.addStr((byte)-item.get_addprimary());
        }
        else if(this._owner.getPrimaryStat() == "Dex")
        {
            this._owner.addDex((byte)-item.get_addprimary());
        }
        else if(this._owner.getPrimaryStat() == "Int")
        {
            this._owner.addInt((byte)-item.get_addprimary());
        }


        if (item.get_addwis() != 0) {
            this._owner.resetBaseMr();
        }
        this._owner.addCha((byte)-item.get_addcha());

        int addMr = 0;
        addMr -= equipment.getMr();
        if ((item.getItemId() == 20236) && (this._owner.isElf())) {
            addMr -= 5;
        }
        if (addMr != 0) {
            this._owner.addMr(addMr);
            this._owner.sendPackets(new S_SPMR(this._owner));
        }
        if ((item.get_addsp() != 0) || (equipment.getaddSp() != 0)) {
            this._owner.addSp(-(item.get_addsp() + equipment.getaddSp()));
            this._owner.sendPackets(new S_SPMR(this._owner));
        }
        if (item.isHasteItem()) {
            this._owner.addHasteItemEquipped(-1);
            if (this._owner.getHasteItemEquipped() == 0) {
                this._owner.setMoveSpeed(0);
                this._owner.sendPackets(new S_SkillHaste(this._owner.getId(), 0, 0));
                this._owner.broadcastPacket(new S_SkillHaste(this._owner.getId(), 0, 0));
            }
        }
        this._owner.getEquipSlot().removeMagicHelm(this._owner.getId(), equipment);

        if (item.getType2() == 1)
            removeWeapon(equipment);
        else if (item.getType2() == 2)
            removeArmor(equipment);
    }

    public void setMagicHelm(L1ItemInstance item)
    {
        switch (item.getItemId()) {
            case 20013:
                this._owner.setSkillMastery(26);
                this._owner.setSkillMastery(43);
                this._owner.sendPackets(new S_AddSkill(0, 0, 0, 2, 0, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
                break;
            case 20014:
                this._owner.setSkillMastery(1);
                this._owner.setSkillMastery(19);
                this._owner.sendPackets(new S_AddSkill(1, 0, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
                break;
            case 20015:
                this._owner.setSkillMastery(12);
                this._owner.setSkillMastery(13);
                this._owner.setSkillMastery(42);
                this._owner.sendPackets(new S_AddSkill(0, 24, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
                break;
            case 20008:
                this._owner.setSkillMastery(43);
                this._owner.sendPackets(new S_AddSkill(0, 0, 0, 0, 0, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
                break;
            case 20023:
                this._owner.setSkillMastery(43);
                this._owner.setSkillMastery(54);
                this._owner.sendPackets(new S_AddSkill(0, 0, 0, 0, 0, 4, 32, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
        }
    }

    public void removeMagicHelm(int objectId, L1ItemInstance item)
    {
        switch (item.getItemId()) {
            case 20013:
                if (!SkillsTable.getInstance().spellCheck(objectId,
                        26)) {
                    this._owner.removeSkillMastery(26);
                    this._owner.sendPackets(new S_DelSkill(0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
                }
                if (!SkillsTable.getInstance().spellCheck(objectId, 43)) {
                    this._owner.removeSkillMastery(43);
                    this._owner.sendPackets(new S_DelSkill(0, 0, 0, 0, 0, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
                }
                break;
            case 20014:
                if (!SkillsTable.getInstance().spellCheck(objectId, 1)) {
                    this._owner.removeSkillMastery(1);
                    this._owner.sendPackets(new S_DelSkill(1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
                }
                if (!SkillsTable.getInstance().spellCheck(objectId, 19)) {
                    this._owner.removeSkillMastery(19);
                    this._owner.sendPackets(new S_DelSkill(0, 0, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
                }
                break;
            case 20015:
                if (!SkillsTable.getInstance().spellCheck(objectId, 12)) {
                    this._owner.removeSkillMastery(12);
                    this._owner.sendPackets(new S_DelSkill(0, 8, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
                }
                if (!SkillsTable.getInstance().spellCheck(objectId, 13)) {
                    this._owner.removeSkillMastery(13);
                    this._owner.sendPackets(new S_DelSkill(0, 16, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
                }
                if (!SkillsTable.getInstance().spellCheck(objectId, 42)) {
                    this._owner.removeSkillMastery(42);
                    this._owner.sendPackets(new S_DelSkill(0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
                }
                break;
            case 20008:
                if (!SkillsTable.getInstance().spellCheck(objectId, 43)) {
                    this._owner.removeSkillMastery(43);
                    this._owner.sendPackets(new S_DelSkill(0, 0, 0, 0, 0, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
                }
                break;
            case 20023:
                if (!SkillsTable.getInstance().spellCheck(objectId, 43)) {
                    this._owner.removeSkillMastery(43);
                    this._owner.sendPackets(new S_DelSkill(0, 0, 0, 0, 0, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
                }
                if (!SkillsTable.getInstance().spellCheck(objectId, 54)) {
                    this._owner.removeSkillMastery(54);
                    this._owner.sendPackets(new S_DelSkill(0, 0, 0, 0, 0, 0, 32, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
                }
                break;
        }
    }
}