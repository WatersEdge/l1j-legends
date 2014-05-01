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
package l1j.server.server.utils;

import l1j.server.Config;

public class CalcStat {

    private CalcStat() {

    }

    /**
     * ACボーナスを返す
     *
     * @param level
     * @param dex
     * @return acBonus
     *
     */
    public static int calcAc(int level, int dex) {
        int acBonus = 10;
        if (dex <= 9) {
            acBonus -= level / 8;
        } else if (dex >= 10 && dex <= 12) {
            acBonus -= level / 7;
        } else if (dex >= 13 && dex <= 15) {
            acBonus -= level / 6;
        } else if (dex >= 16 && dex <= 17) {
            acBonus -= level / 5;
        } else if (dex >= 18) {
            acBonus -= level / 4;
        }
        return acBonus;
    }

    /**
     * <b> 傳回精 wis 對應的抗魔值 </b>
     *
     * @param wis 精神點數
     *
     * @return mrBonus 抗魔值
     */
    public static int calcStatMr(int wis) {
        int mrBonus = 0;
        if (wis <= 14) {
            mrBonus = 0;
        } else if (wis >= 15 && wis <= 16) {
            mrBonus = 3;
        } else if (wis == 17) {
            mrBonus = 6;
        } else if (wis == 18) {
            mrBonus = 10;
        } else if (wis == 19) {
            mrBonus = 15;
        } else if (wis == 20) {
            mrBonus = 21;
        } else if (wis == 21) {
            mrBonus = 28;
        } else if (wis == 22) {
            mrBonus = 37;
        } else if (wis == 23) {
            mrBonus = 47;
        } else if (wis >= 24 && wis <= 29) {
            mrBonus = 50;
        } else if (wis >= 30 && wis <= 34) {
            mrBonus = 52;
        } else if (wis >= 35 && wis <= 39) {
            mrBonus = 55;
        } else if (wis >= 40 && wis <= 43) {
            mrBonus = 59;
        } else if (wis >= 44 && wis <= 46) {
            mrBonus = 62;
        } else if (wis >= 47 && wis <= 49) {
            mrBonus = 64;
        } else if (wis == 50) {
            mrBonus = 65;
        } else {
            mrBonus = 65;
        }
        return mrBonus;
    }

    public static int calcDiffMr(int wis, int diff) {
        return calcStatMr(wis + diff) - calcStatMr(wis);
    }

    /**
     * 各クラスのLVUP時のHP上昇値を返す
     *
     * @param charType
     * @param baseMaxHp
     * @param baseCon
     * @param originalHpup
     * @return HP上昇値
     */
    public static short calcStatHp(int charType, int baseMaxHp, byte baseCon, int originalHpup, int originalCon) {
        short randomhp = 0;
        if (baseCon > 15) {
            randomhp = (short) (baseCon - 15);
        }

        randomhp += getBonusHp(charType,originalCon);

        if (charType == 0) { // プリンス
            randomhp += (short) (11 + Random.nextInt(2)); // 初期値分追加

            if (baseMaxHp + randomhp > Config.PRINCE_MAX_HP) {
                randomhp = (short) (Config.PRINCE_MAX_HP - baseMaxHp);
            }
        } else if (charType == 1) { // ナイト
            randomhp += (short) (17 + Random.nextInt(2)); // 初期値分追加

            if (baseMaxHp + randomhp > Config.KNIGHT_MAX_HP) {
                randomhp = (short) (Config.KNIGHT_MAX_HP - baseMaxHp);
            }
        } else if (charType == 2) { // エルフ
            randomhp += (short) (10 + Random.nextInt(2)); // 初期値分追加

            if (baseMaxHp + randomhp > Config.ELF_MAX_HP) {
                randomhp = (short) (Config.ELF_MAX_HP - baseMaxHp);
            }
        } else if (charType == 3) { // ウィザード
            randomhp += (short) (7 + Random.nextInt(2)); // 初期値分追加

            if (baseMaxHp + randomhp > Config.WIZARD_MAX_HP) {
                randomhp = (short) (Config.WIZARD_MAX_HP - baseMaxHp);
            }
        } else if (charType == 4) { // ダークエルフ
            randomhp += (short) (13 + Random.nextInt(2)); // 初期値分追加

            if (baseMaxHp + randomhp > Config.DARKELF_MAX_HP) {
                randomhp = (short) (Config.DARKELF_MAX_HP - baseMaxHp);
            }
        } else if (charType == 5) { // ドラゴンナイト
            randomhp += (short) (13 + Random.nextInt(2)); // 初期値分追加

            if (baseMaxHp + randomhp > Config.DRAGONKNIGHT_MAX_HP) {
                randomhp = (short) (Config.DRAGONKNIGHT_MAX_HP - baseMaxHp);
            }
        } else if (charType == 6) { // イリュージョニスト
            randomhp += (short) (9 + Random.nextInt(2)); // 初期値分追加

            if (baseMaxHp + randomhp > Config.ILLUSIONIST_MAX_HP) {
                randomhp = (short) (Config.ILLUSIONIST_MAX_HP - baseMaxHp);
            }
        }

        //randomhp += originalHpup;

        if (randomhp < 0) {
            randomhp = 0;
        }
        return randomhp;
    }


    public static short calcStatMp(int charType, byte baseWis, int originalWis)
    {
        short mpGain = 0;
        mpGain = (short) (getRandomMP(charType,baseWis) + getBonusMp(charType,originalWis));
        //System.out.println("Original Wisdom:" + originalWis);
        return mpGain;
    }

    public static short getBonusHp(int charType, int origCon)
    {
        short bonusHP = 0;
        switch(charType)
        {
            //Royal
            case 0:
                if(origCon >= 12 && origCon <= 13)
                {
                    bonusHP = 1;
                }
                else if(origCon >= 14 && origCon <= 15)
                {
                    bonusHP = 2;
                }
                else if(origCon >= 16)
                {
                    bonusHP = 3;
                }
                break;
            //Knight
            case 1:
                if(origCon >= 15 && origCon <= 16)
                {
                    bonusHP = 1;
                }
                else if(origCon >= 17)
                {
                    bonusHP = 3;
                }
                break;
            //Elf
            case 2:
                if(origCon >= 13 && origCon <= 14)
                {
                    bonusHP = 1;
                }
                else if(origCon >= 18)
                {
                    bonusHP = 3;
                }
                break;
            //wisard
            case 3:
                if(origCon >= 14 && origCon <= 15)
                {
                    bonusHP = 1;
                }
                else if(origCon >= 16)
                {
                    bonusHP = 2;
                }
                break;
            //Dark Elf
            case 4:
                if(origCon >= 10 && origCon <= 11)
                {
                    bonusHP = 2;
                }
                else if(origCon >= 12)
                {
                    bonusHP = 3;
                }
                break;
            //Dragon KNight
            case 5:
                if(origCon >= 15 && origCon <= 16)
                {
                    bonusHP = 1;
                }
                else if(origCon >= 17)
                {
                    bonusHP = 3;
                }
                break;
            //Illusionist
            case 6:
                if(origCon >= 13 && origCon <= 14)
                {
                    bonusHP = 1;
                }
                else if(origCon >= 15)
                {
                    bonusHP = 2;
                }
                break;
            default:
                //System.out.println("Error Getting The getBonusHp");
                break;
        }
        return bonusHP;
    }


    public static short getBonusMp(int charType, int origWis)
    {
        short bonusMP = 0;
        switch(charType)
        {
            //Royal
            case 0:
                if(origWis >= 16)
                {
                    bonusMP = 1;
                }
                break;
            //Knight
            case 1:
                break;
            //Elf
            case 2:
                if(origWis >= 14)
                {
                    bonusMP = 1;
                }
                else if(origWis >= 17)
                {
                    bonusMP = 2;
                }
                break;
            //wisard
            case 3:
                if(origWis >= 13 && origWis <= 16)
                {
                    bonusMP = 1;
                }
                else if(origWis >= 17)
                {
                    bonusMP = 2;
                }
                break;
            //Dark Elf
            case 4:
                if(origWis >= 12)
                {
                    bonusMP = 1;
                }
                break;
            //Dragon KNight
            case 5:
                if(origWis >= 13)
                {
                    bonusMP = 1;
                }
                break;
            //Illusionist
            case 6:
                if(origWis >= 13 && origWis <= 15)
                {
                    bonusMP = 1;
                }
                else if(origWis >= 16)
                {
                    bonusMP = 2;
                }
                break;
            default:

                break;
        }
        return bonusMP;
    }
    public static short getRandomMP(int charType, byte baseWis)
    {
        short wisBonusMp = (short) getwisBonusMp(baseWis);
        int randomMP = 0;
        switch(charType)
        {
            //Royal
            case 0:
                randomMP = Random.nextInt(3) + 1 + wisBonusMp;
                break;
            //Knight
            case 1:
                randomMP = Random.nextInt(3) + wisBonusMp;
                break;
            //Elf
            case 2:
                randomMP = Random.nextInt(3) + 2 + wisBonusMp;
                break;
            //Wizard
            case 3:
                randomMP = Random.nextInt(5) + 3 + wisBonusMp;
                break;
            //Dark Elf
            case 4:
                randomMP = Random.nextInt(3) + 2 + wisBonusMp;
                break;
            //Dragon KNight
            case 5:
                randomMP = Random.nextInt(3) + wisBonusMp;
                break;
            //Illusionist
            case 6:
                randomMP = Random.nextInt(3) + 2 + wisBonusMp;
                break;
            default:

                break;
        }
        return (short) Math.max(randomMP,1);
    }

    public static short getwisBonusMp(byte baseWis)
    {
        if(baseWis >=12 && baseWis <=13)
        {
            return 1;
        }
        if(baseWis >=14 && baseWis <=16)
        {
            return 2;
        }
        else if(baseWis == 17)
        {
            return 3;
        }
        else if(baseWis >= 18)
        {
            short returnInt = 3;
            for(int i=0;i<baseWis-17;i++)
            {
                returnInt++;
            }
            return returnInt;
        }
        else
        {
            return 0;
        }
    }



}
