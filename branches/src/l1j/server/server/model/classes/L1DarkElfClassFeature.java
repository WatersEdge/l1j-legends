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
package l1j.server.server.model.classes;
import l1j.server.server.model.L1Attribute;
import java.util.HashMap;
import java.util.Map;

class L1DarkElfClassFeature extends L1ClassFeature {
	@Override
	public int getAcDefenseMax(int ac) {
		return ac / 4;
	}

	@Override
	public int getMagicLevel(int playerLevel) {
		return Math.min(2, playerLevel / 12);
	}
	
	@Override
	public Map<L1Attribute, Integer> getFixedStats() {
		Map<L1Attribute, Integer> fixedStats = 
			new HashMap<L1Attribute, Integer>();
		fixedStats.put(L1Attribute.Str, 12);
		fixedStats.put(L1Attribute.Dex, 15);
		fixedStats.put(L1Attribute.Con, 8);
		fixedStats.put(L1Attribute.Wis, 10);
		fixedStats.put(L1Attribute.Int, 11);
		fixedStats.put(L1Attribute.Cha, 9);
		return fixedStats;
	}
}
