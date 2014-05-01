/**
 *      ****                      License
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
package l1j.server;

import java.util.MissingResourceException;
import java.util.ResourceBundle;


public class L1Message {

    public static String memoryUse;
    public static String onGroundItem;
    public static String secondsDelete;
    public static String deleted;
    public static String ver;
    public static String settingslist;
    public static String exp;
    public static String level;
    public static String justice;
    public static String karma;
    public static String dropitems;
    public static String dropadena;
    public static String enchantweapon;
    public static String enchantarmor;
    public static String chatlevel;
    public static String nonpvpNo;
    public static String nonpvpYes;
    public static String memory;
    public static String maxplayer;
    public static String player;
    public static String waitingforuser;
    public static String from;
    public static String attempt;
    public static String setporton;
    public static String initialfinished;
    public static String servername;
    private static L1Message _instance;

    private L1Message() {
        try {
            initLocaleMessage();
        } catch (MissingResourceException mre) {
            mre.printStackTrace();
        }
    }

    public static L1Message getInstance() {
        if (_instance == null) {
            _instance = new L1Message();
        }
        return _instance;
    }

    public void initLocaleMessage() {
        memoryUse = "Memory: ";
        memory = "MB";
        onGroundItem = "items on the ground";
        secondsDelete = "will be delete after 10 seconds";
        deleted = "was deleted";
        ver = "3.52e";
        settingslist = "Server Configuration";
        exp = "XP: ";
        level = "Level: ";
        justice = "Justice: ";
        karma = "Karma: ";
        dropitems = "Drop Items: ";
        dropadena = "Drop Adena: ";
        enchantweapon = "Enchant Weapon: ";
        enchantarmor = "Enchant Armor: ";
        chatlevel = "Global Level: ";
        nonpvpNo = "PvP: Disabled";
        nonpvpYes =  "PvE: Enabled";
        maxplayer =  "Max Players: ";
        player = " players";
        waitingforuser =  "Waiting for user's connection.";
        from = "from ";
        attempt = " attempt to connect.";
        setporton = "Port: ";
        initialfinished = "Initialize finished.";
        servername = "Legends Of Aden 3.52 Custom";
    }



}
