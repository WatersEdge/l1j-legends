package l1j.server.server.utils;


import l1j.server.L1DatabaseFactory;
import java.util.Date;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class KGRing {
    private static KGRing _instance;

    public static void initialize() {
        _instance = new KGRing();
    }

    private KGRing() {
        checkDate();
    }

    private boolean checkDate() {
        Date _DaysFromLM = new Date( this.getLastUpdated().getTime() + 3* (1000 * 60 * 60 * 24));
        if(new Date().after(_DaysFromLM))
        {
            updateKGRing();
        }
        return true;
    }


    public static void updateKGRing()
    {
        getStats();
    }

    public static void  getStats()
    {
        //How many stats the ring will get 1-6
        double howMany = Math.floor((Math.random()*6)+1);
        String[] myStats = new String[25];
        myStats[0] = "ac";
        myStats[1] = "add_str";
        myStats[2] = "add_con";
        myStats[3] = "add_dex";
        myStats[4] = "add_int";
        myStats[5] = "add_wis";
        myStats[6] = "add_cha";
        myStats[7] = "add_hp";
        myStats[8] = "add_mp";
        myStats[9] = "add_hpr";
        myStats[10] = "add_mpr";
        myStats[11] = "add_sp";
        myStats[12] = "add_primary";
        myStats[13] = "m_def";
        myStats[14] = "damage_reduction";
        myStats[15] = "weight_reduction";
        myStats[16] = "hit_modifier";
        myStats[17] = "dmg_modifier";
        myStats[18] = "bow_hit_modifier";
        myStats[19] = "bow_dmg_modifier";
        myStats[20] = "defense_water";
        myStats[21] = "defense_wind";
        myStats[22] = "defense_fire";
        myStats[23] = "defense_earth";
        myStats[24] = "hold_resist";


        String hasAlready;
        hasAlready = "";
        boolean hasBonusXP = false;

        //Determine if the ring gets bonus XP;
        /*
        double bonusXPChance = Math.floor((Math.random()*100)+1);
        if(bonusXPChance > 90)
        {
            hasBonusXP = true;
            howMany = howMany-1;
        }
        */
        String sqlStatment = "update armor set ";
        for(int i=0;i<howMany;i++)
        {
            double modifier = Math.floor((Math.random()*7)+1) - 3;

            while(modifier == 0)
            {
                modifier = Math.floor((Math.random()*6)+1) - 2;
            }

            int wStatID = (int) Math.floor((Math.random()*24));

            while(hasAlready.indexOf("[" + wStatID + "]") != -1)
            {
                wStatID = (int) Math.floor((Math.random()*24));
            }

            String wStat = myStats[wStatID];
            hasAlready = hasAlready + "[" + wStatID + "]";

            if(wStatID == 0)
            {
                modifier = modifier *-1;
            }
            if(wStatID >= 20 && wStatID <= 23)
            {
                modifier = modifier * 5;
            }
            if(wStatID == 24)
            {
                modifier = modifier * 1;
            }
            if(wStatID == 7 || wStatID == 8)
            {
                modifier = modifier * 25;
            }
            if(wStatID == 9 || wStatID == 10)
            {
                modifier = modifier * 2;
            }

            if(wStatID == 16)
            {
                modifier = modifier * 3;
            }
            if(wStatID == 13)
            {
                modifier = modifier * 10;
            }
            sqlStatment +=  wStat + " = " + Math.round(modifier) + ",";
        }

        /*
        if(hasBonusXP == true)
        {
            int bonusXPValue = (int) Math.floor((Math.random()*5)+1);
        }
        */
        sqlStatment += "lastModified = CURRENT_TIMESTAMP where item_id = 30009;";

        resetKGRing();

        //System.out.println(sqlStatment);

        Connection con = null;
        PreparedStatement pstm = null;

        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            String sqlstr = sqlStatment;
            pstm = con.prepareStatement(sqlstr);
            pstm.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.toString());
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }

    }

    private static void resetKGRing()
    {
        String sqlReset = "UPDATE armor SET ac = 0,add_str = 0,add_con = 0,add_dex = 0,add_int = 0,add_wis = 0,add_cha = 0,add_hp = 0,add_mp =0,add_hpr =0,add_mpr = 0,add_sp =0,";
        sqlReset += "min_lvl = 0,max_lvl =0,m_def =0,haste_item = 0,damage_reduction = 0,weight_reduction = 0,hit_modifier = 0,dmg_modifier = 0,bow_hit_modifier = 0,bow_dmg_modifier = 0,";
        sqlReset += "defense_water = 0,defense_wind = 0,defense_fire = 0,defense_earth =0,hold_resist = 0";
        sqlReset += "add_primary = 0 WHERE item_id = 30009; ";

        Connection con = null;
        PreparedStatement pstm = null;

        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            String sqlstr = sqlReset;
            pstm = con.prepareStatement(sqlstr);
            pstm.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.toString());
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    private Date getLastUpdated() {
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        Date lastModified = new Date();
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            String sqlstr = "SELECT * FROM armor WHERE item_id = 30009";
            pstm = con.prepareStatement(sqlstr);
            rs = pstm.executeQuery();
            if (!rs.next()) {
                return null;
            }
           lastModified = rs.getDate("lastModified");
        } catch (SQLException e) {

        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
        return lastModified;
    }


}
