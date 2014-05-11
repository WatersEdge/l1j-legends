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
package l1j.server.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.utils.SQLUtil;
import l1j.server.server.utils.collections.Maps;

public final class MapsTable {
	private class MapData {
		public int startX = 0;

		public int endX = 0;

		public int startY = 0;

		public int endY = 0;

		public double monster_amount = 1;

		public double dropRate = 1;

		public boolean isUnderwater = false;

		public boolean markable = false;

		public boolean teleportable = false;

		public boolean escapable = false;

		public boolean isUseResurrection = false;

		public boolean isUsePainwand = false;

		public boolean isEnabledDeathPenalty = false;

		public boolean isTakePets = false;

		public boolean isRecallPets = false;

		public boolean isUsableItem = false;

		public boolean isUsableSkill = false;
	}

	private static Logger _log = Logger.getLogger(MapsTable.class.getName());

	private static MapsTable _instance;

	/**
	 * Keyã�«ãƒžãƒƒãƒ—IDã€�Valueã�«ãƒ†ãƒ¬ãƒ�ãƒ¼ãƒˆå�¯å�¦ãƒ•ãƒ©ã‚°ã�Œæ ¼ç´�ã�•ã‚Œã‚‹HashMap
	 */
	private final Map<Integer, MapData> _maps = Maps.newMap();

	/**
	 * æ–°ã�—ã��MapsTableã‚ªãƒ–ã‚¸ã‚§ã‚¯ãƒˆã‚’ç”Ÿæˆ�ã�—ã€�ãƒžãƒƒãƒ—ã�®ãƒ†ãƒ¬ãƒ�ãƒ¼ãƒˆå�¯å�¦ãƒ•ãƒ©ã‚°ã‚’èª­ã�¿è¾¼ã‚€ã€‚
	 */
	private MapsTable() {
		loadMapsFromDatabase();
	}

	/**
	 * ãƒžãƒƒãƒ—ã�®ãƒ†ãƒ¬ãƒ�ãƒ¼ãƒˆå�¯å�¦ãƒ•ãƒ©ã‚°ã‚’ãƒ‡ãƒ¼ã‚¿ãƒ™ãƒ¼ã‚¹ã�‹ã‚‰èª­ã�¿è¾¼ã�¿ã€�HashMap _mapsã�«æ ¼ç´�ã�™ã‚‹ã€‚
	 */
	private void loadMapsFromDatabase() {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM mapids");

			for (rs = pstm.executeQuery(); rs.next();) {
				MapData data = new MapData();
				int mapId = rs.getInt("mapid");
				// rs.getString("locationname");
				data.startX = rs.getInt("startX");
				data.endX = rs.getInt("endX");
				data.startY = rs.getInt("startY");
				data.endY = rs.getInt("endY");
				data.monster_amount = rs.getDouble("monster_amount");
				data.dropRate = rs.getDouble("drop_rate");
				data.isUnderwater = rs.getBoolean("underwater");
				data.markable = rs.getBoolean("markable");
				data.teleportable = rs.getBoolean("teleportable");
				data.escapable = rs.getBoolean("escapable");
				data.isUseResurrection = rs.getBoolean("resurrection");
				data.isUsePainwand = rs.getBoolean("painwand");
				data.isEnabledDeathPenalty = rs.getBoolean("penalty");
				data.isTakePets = rs.getBoolean("take_pets");
				data.isRecallPets = rs.getBoolean("recall_pets");
				data.isUsableItem = rs.getBoolean("usable_item");
				data.isUsableSkill = rs.getBoolean("usable_skill");

				_maps.put(new Integer(mapId), data);
			}

			_log.config("Maps " + _maps.size());
		}
		catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		}
		finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	/**
	 * MapsTableã�®ã‚¤ãƒ³ã‚¹ã‚¿ãƒ³ã‚¹ã‚’è¿”ã�™ã€‚
	 * 
	 * @return MapsTableã�®ã‚¤ãƒ³ã‚¹ã‚¿ãƒ³ã‚¹
	 */
	public static MapsTable getInstance() {
		if (_instance == null) {
			_instance = new MapsTable();
		}
		return _instance;
	}

	/**
	 * ãƒžãƒƒãƒ—ã�Œã�®Xé–‹å§‹åº§æ¨™ã‚’è¿”ã�™ã€‚
	 * 
	 * @param mapId
	 *            èª¿ã�¹ã‚‹ãƒžãƒƒãƒ—ã�®ãƒžãƒƒãƒ—ID
	 * @return Xé–‹å§‹åº§æ¨™
	 */
	public int getStartX(int mapId) {
		MapData map = _maps.get(mapId);
		if (map == null) {
			return 0;
		}
		return _maps.get(mapId).startX;
	}

	/**
	 * ãƒžãƒƒãƒ—ã�Œã�®Xçµ‚äº†åº§æ¨™ã‚’è¿”ã�™ã€‚
	 * 
	 * @param mapId
	 *            èª¿ã�¹ã‚‹ãƒžãƒƒãƒ—ã�®ãƒžãƒƒãƒ—ID
	 * @return Xçµ‚äº†åº§æ¨™
	 */
	public int getEndX(int mapId) {
		MapData map = _maps.get(mapId);
		if (map == null) {
			return 0;
		}
		return _maps.get(mapId).endX;
	}

	/**
	 * ãƒžãƒƒãƒ—ã�Œã�®Yé–‹å§‹åº§æ¨™ã‚’è¿”ã�™ã€‚
	 * 
	 * @param mapId
	 *            èª¿ã�¹ã‚‹ãƒžãƒƒãƒ—ã�®ãƒžãƒƒãƒ—ID
	 * @return Yé–‹å§‹åº§æ¨™
	 */
	public int getStartY(int mapId) {
		MapData map = _maps.get(mapId);
		if (map == null) {
			return 0;
		}
		return _maps.get(mapId).startY;
	}

	/**
	 * ãƒžãƒƒãƒ—ã�Œã�®Yçµ‚äº†åº§æ¨™ã‚’è¿”ã�™ã€‚
	 * 
	 * @param mapId
	 *            èª¿ã�¹ã‚‹ãƒžãƒƒãƒ—ã�®ãƒžãƒƒãƒ—ID
	 * @return Yçµ‚äº†åº§æ¨™
	 */
	public int getEndY(int mapId) {
		MapData map = _maps.get(mapId);
		if (map == null) {
			return 0;
		}
		return _maps.get(mapId).endY;
	}

	/**
	 * ãƒžãƒƒãƒ—ã�®ãƒ¢ãƒ³ã‚¹ã‚¿ãƒ¼é‡�å€�çŽ‡ã‚’è¿”ã�™
	 * 
	 * @param mapId
	 *            èª¿ã�¹ã‚‹ãƒžãƒƒãƒ—ã�®ãƒžãƒƒãƒ—ID
	 * @return ãƒ¢ãƒ³ã‚¹ã‚¿ãƒ¼é‡�ã�®å€�çŽ‡
	 */
	public double getMonsterAmount(int mapId) {
		MapData map = _maps.get(mapId);
		if (map == null) {
			return 0;
		}
		return map.monster_amount;
	}

	/**
	 * ãƒžãƒƒãƒ—ã�®ãƒ‰ãƒ­ãƒƒãƒ—å€�çŽ‡ã‚’è¿”ã�™
	 * 
	 * @param mapId
	 *            èª¿ã�¹ã‚‹ãƒžãƒƒãƒ—ã�®ãƒžãƒƒãƒ—ID
	 * @return ãƒ‰ãƒ­ãƒƒãƒ—å€�çŽ‡
	 */
	public double getDropRate(int mapId) {
		MapData map = _maps.get(mapId);
		if (map == null) {
			return 0;
		}
		return map.dropRate;
	}

	/**
	 * ãƒžãƒƒãƒ—ã�Œã€�æ°´ä¸­ã�§ã�‚ã‚‹ã�‹ã‚’è¿”ã�™ã€‚
	 * 
	 * @param mapId
	 *            èª¿ã�¹ã‚‹ãƒžãƒƒãƒ—ã�®ãƒžãƒƒãƒ—ID
	 * 
	 * @return æ°´ä¸­ã�§ã�‚ã‚Œã�°true
	 */
	public boolean isUnderwater(int mapId) {
		MapData map = _maps.get(mapId);
		if (map == null) {
			return false;
		}
		//return _maps.get(mapId).isUnderwater;
        return false; //[Legends] Disabling underwater
	}

	/**
	 * ãƒžãƒƒãƒ—ã�Œã€�ãƒ–ãƒƒã‚¯ãƒžãƒ¼ã‚¯å�¯èƒ½ã�§ã�‚ã‚‹ã�‹ã‚’è¿”ã�™ã€‚
	 * 
	 * @param mapId
	 *            èª¿ã�¹ã‚‹ãƒžãƒƒãƒ—ã�®ãƒžãƒƒãƒ—ID
	 * @return ãƒ–ãƒƒã‚¯ãƒžãƒ¼ã‚¯å�¯èƒ½ã�§ã�‚ã‚Œã�°true
	 */
	public boolean isMarkable(int mapId) {
		MapData map = _maps.get(mapId);
		if (map == null) {
			return false;
		}
		return _maps.get(mapId).markable;
	}

	/**
	 * ãƒžãƒƒãƒ—ã�Œã€�ãƒ©ãƒ³ãƒ€ãƒ ãƒ†ãƒ¬ãƒ�ãƒ¼ãƒˆå�¯èƒ½ã�§ã�‚ã‚‹ã�‹ã‚’è¿”ã�™ã€‚
	 * 
	 * @param mapId
	 *            èª¿ã�¹ã‚‹ãƒžãƒƒãƒ—ã�®ãƒžãƒƒãƒ—ID
	 * @return å�¯èƒ½ã�§ã�‚ã‚Œã�°true
	 */
	public boolean isTeleportable(int mapId) {
		MapData map = _maps.get(mapId);
		if (map == null) {
			return false;
		}
		return _maps.get(mapId).teleportable;
	}

	/**
	 * ãƒžãƒƒãƒ—ã�Œã€�MAPã‚’è¶…ã�ˆã�Ÿãƒ†ãƒ¬ãƒ�ãƒ¼ãƒˆå�¯èƒ½ã�§ã�‚ã‚‹ã�‹ã‚’è¿”ã�™ã€‚
	 * 
	 * @param mapId
	 *            èª¿ã�¹ã‚‹ãƒžãƒƒãƒ—ã�®ãƒžãƒƒãƒ—ID
	 * @return å�¯èƒ½ã�§ã�‚ã‚Œã�°true
	 */
	public boolean isEscapable(int mapId) {
		MapData map = _maps.get(mapId);
		if (map == null) {
			return false;
		}
		return _maps.get(mapId).escapable;
	}

	/**
	 * ãƒžãƒƒãƒ—ã�Œã€�å¾©æ´»å�¯èƒ½ã�§ã�‚ã‚‹ã�‹ã‚’è¿”ã�™ã€‚
	 * 
	 * @param mapId
	 *            èª¿ã�¹ã‚‹ãƒžãƒƒãƒ—ã�®ãƒžãƒƒãƒ—ID
	 * 
	 * @return å¾©æ´»å�¯èƒ½ã�§ã�‚ã‚Œã�°true
	 */
	public boolean isUseResurrection(int mapId) {
		MapData map = _maps.get(mapId);
		if (map == null) {
			return false;
		}
		return _maps.get(mapId).isUseResurrection;
	}

	/**
	 * ãƒžãƒƒãƒ—ã�Œã€�ãƒ‘ã‚¤ãƒ³ãƒ¯ãƒ³ãƒ‰ä½¿ç”¨å�¯èƒ½ã�§ã�‚ã‚‹ã�‹ã‚’è¿”ã�™ã€‚
	 * 
	 * @param mapId
	 *            èª¿ã�¹ã‚‹ãƒžãƒƒãƒ—ã�®ãƒžãƒƒãƒ—ID
	 * 
	 * @return ãƒ‘ã‚¤ãƒ³ãƒ¯ãƒ³ãƒ‰ä½¿ç”¨å�¯èƒ½ã�§ã�‚ã‚Œã�°true
	 */
	public boolean isUsePainwand(int mapId) {
		MapData map = _maps.get(mapId);
		if (map == null) {
			return false;
		}
		return _maps.get(mapId).isUsePainwand;
	}

	/**
	 * ãƒžãƒƒãƒ—ã�Œã€�ãƒ‡ã‚¹ãƒšãƒŠãƒ«ãƒ†ã‚£ã�Œã�‚ã‚‹ã�‹ã‚’è¿”ã�™ã€‚
	 * 
	 * @param mapId
	 *            èª¿ã�¹ã‚‹ãƒžãƒƒãƒ—ã�®ãƒžãƒƒãƒ—ID
	 * 
	 * @return ãƒ‡ã‚¹ãƒšãƒŠãƒ«ãƒ†ã‚£ã�§ã�‚ã‚Œã�°true
	 */
	public boolean isEnabledDeathPenalty(int mapId) {
		MapData map = _maps.get(mapId);
		if (map == null) {
			return false;
		}
		return _maps.get(mapId).isEnabledDeathPenalty;
	}

	/**
	 * ãƒžãƒƒãƒ—ã�Œã€�ãƒšãƒƒãƒˆãƒ»ã‚µãƒ¢ãƒ³ã‚’é€£ã‚Œã�¦è¡Œã�‘ã‚‹ã�‹ã‚’è¿”ã�™ã€‚
	 * 
	 * @param mapId
	 *            èª¿ã�¹ã‚‹ãƒžãƒƒãƒ—ã�®ãƒžãƒƒãƒ—ID
	 * 
	 * @return ãƒšãƒƒãƒˆãƒ»ã‚µãƒ¢ãƒ³ã‚’é€£ã‚Œã�¦è¡Œã�‘ã‚‹ã�ªã‚‰ã�°true
	 */
	public boolean isTakePets(int mapId) {
		MapData map = _maps.get(mapId);
		if (map == null) {
			return false;
		}
		return _maps.get(mapId).isTakePets;
	}

	/**
	 * ãƒžãƒƒãƒ—ã�Œã€�ãƒšãƒƒãƒˆãƒ»ã‚µãƒ¢ãƒ³ã‚’å‘¼ã�³å‡ºã�›ã‚‹ã�‹ã‚’è¿”ã�™ã€‚
	 * 
	 * @param mapId
	 *            èª¿ã�¹ã‚‹ãƒžãƒƒãƒ—ã�®ãƒžãƒƒãƒ—ID
	 * 
	 * @return ãƒšãƒƒãƒˆãƒ»ã‚µãƒ¢ãƒ³ã‚’å‘¼ã�³å‡ºã�›ã‚‹ã�ªã‚‰ã�°true
	 */
	public boolean isRecallPets(int mapId) {
		MapData map = _maps.get(mapId);
		if (map == null) {
			return false;
		}
		return _maps.get(mapId).isRecallPets;
	}

	/**
	 * ãƒžãƒƒãƒ—ã�Œã€�ã‚¢ã‚¤ãƒ†ãƒ ã‚’ä½¿ç”¨ã�§ã��ã‚‹ã�‹ã‚’è¿”ã�™ã€‚
	 * 
	 * @param mapId
	 *            èª¿ã�¹ã‚‹ãƒžãƒƒãƒ—ã�®ãƒžãƒƒãƒ—ID
	 * 
	 * @return ã‚¢ã‚¤ãƒ†ãƒ ã‚’ä½¿ç”¨ã�§ã��ã‚‹ã�ªã‚‰ã�°true
	 */
	public boolean isUsableItem(int mapId) {
		MapData map = _maps.get(mapId);
		if (map == null) {
			return false;
		}
		return _maps.get(mapId).isUsableItem;
	}

	/**
	 * ãƒžãƒƒãƒ—ã�Œã€�ã‚¹ã‚­ãƒ«ã‚’ä½¿ç”¨ã�§ã��ã‚‹ã�‹ã‚’è¿”ã�™ã€‚
	 * 
	 * @param mapId
	 *            èª¿ã�¹ã‚‹ãƒžãƒƒãƒ—ã�®ãƒžãƒƒãƒ—ID
	 * 
	 * @return ã‚¹ã‚­ãƒ«ã‚’ä½¿ç”¨ã�§ã��ã‚‹ã�ªã‚‰ã�°true
	 */
	public boolean isUsableSkill(int mapId) {
		MapData map = _maps.get(mapId);
		if (map == null) {
			return false;
		}
		return _maps.get(mapId).isUsableSkill;
	}
	
	   public static void reloadTable(){
		   MapsTable oldInstance = _instance;
			_instance = new MapsTable() ;
			oldInstance._maps.clear();
		}


}
