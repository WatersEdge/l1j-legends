/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package l1j.server.server.model;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Timer;
import java.util.TimerTask;

import l1j.server.server.model.L1Object;
import l1j.server.server.model.Instance.L1DoorInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.skill.L1SkillUse;
import l1j.server.server.serverpackets.S_Paralysis;
import l1j.server.server.serverpackets.S_Poison;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;
import static l1j.server.server.model.skill.L1SkillId.CANCELLATION;

public class L1Arena4v4 {
	private static final Logger _log = 
		Logger.getLogger(L1Arena4v4.class.getName());
	public static final int STATUS_NONE = 0;
	public static final int STATUS_READY = 1;
	public static final int STATUS_PLAYING = 2;


	private final List<L1PcInstance> _members =
			new ArrayList<L1PcInstance>();
	private int _arenaStatus = STATUS_NONE;
	private L1ArenaTimer _hhTimer;

	private static L1Arena4v4 _instance;

	public static L1Arena4v4 getInstance() {
		if (_instance == null) {
			_instance = new L1Arena4v4();
		}
		
		return _instance;
	}

	private void readyArena()
	{
		L1ArenaReadyTimer hhrTimer = new L1ArenaReadyTimer();
		hhrTimer.begin();
	}

	
	
	private void setArenaStatus(int i) {
		_arenaStatus = i;
	}
	
	public int getArenaStatus() {
		return _arenaStatus;
	}

	
	private void startArena()
	{
		
		setArenaStatus(2);
		for (L1PcInstance pc : _members) {
			// remove user's buff when entering arena
			new L1SkillUse().handleCommands(pc, 37, pc.getId(),
					pc.getX(), pc.getY(), null, 30000,
					L1SkillUse.TYPE_GMBUFF);
			new L1SkillUse().handleCommands(pc, 44, pc.getId(),
					pc.getX(), pc.getY(), null, 30000,
					L1SkillUse.TYPE_GMBUFF);

			
		}
	}


	private synchronized void endArena()
	{
		_hhTimer.cancel();
		for (L1PcInstance pc : _members) {
			L1Teleport.teleport(pc, 33437, 32802, (short) 4, 5, true);
		}
		_members.clear();
		setArenaStatus(1);
	}


	
	public void addMember(L1PcInstance pc) {
		L1PcInstance[] players = pc.getParty().getMembers();
		for (L1PcInstance pc2 : players) {
			try 
			{				
				// bind the target for 30s to prevent them from heaing other team's loc
	            pc2.sendPackets(new S_Paralysis(S_Paralysis.TYPE_PARALYSIS, true));
	            pc2.setSkillEffect(33,30000);
				_members.add(pc2);
			} catch (Exception e) {
				_log.log(Level.SEVERE, "", e);
			}
		}

		// start the match if enough people join the game
		if (getMembersCount() ==  4) {
			readyArena();
		}
	}

	
	public synchronized void removeMember(L1PcInstance pc) {
		_members.remove(pc);
	}

	public int getMembersCount() {
		return _members.size();
	}


	public class L1ArenaReadyTimer extends TimerTask
	{

		public L1ArenaReadyTimer() {}
		
		
		@Override
		public void run() {
			startArena();
			if (_hhTimer != null)
				_hhTimer.cancel();
			_hhTimer = new L1ArenaTimer();
			_hhTimer.begin();
			
		}
		
		public void begin() {
			Timer timer = new Timer();
			timer.schedule(this, 30000); 
		}
		
	}


	public boolean isInMap(L1PcInstance pc)
	{
		boolean isInMap = false;
		if(pc.getMapId() == (short)34 || pc.getMapId() == (short)38 || pc.getMapId() == (short)39 || pc.getMapId() == (short)40  ||
				pc.getMapId() == (short)41)
		{
			isInMap = true;
		}
		
		return isInMap;
	}
	
	public class L1ArenaTimer extends TimerTask
	{
		public L1ArenaTimer() {}
		private int _counter = 0;
		private List<L1PcInstance> dup = new ArrayList<L1PcInstance>(_members);
		public void begin() {
			Timer timer = new Timer();
			timer.schedule(this, 0); 
		}
		
		
		@Override
		public void run() {
			try {
				for (;;) {
					Thread.sleep(10000);
					_counter++;
//					_log.info("counter: " + _counter);
//					_log.info("Arena: " + L1Arena.getInstance().getMembersCount());
					for(L1PcInstance p : dup)
					{
						
						if(p.isDead() || (!isInMap(p)))
						{
			                
//		                	_log.info("player's map: " + p.getMapId());
//		                	_log.info("Map Capacity before: " + L1Arena.getInstance().getMembersCount());

							L1Arena4v4.getInstance().removeMember(p);
//		                	_log.info("Map Capacity after: " + L1Arena.getInstance().getMembersCount());
						}
					}

					if (_counter == 5 || L1Arena4v4.getInstance().getMembersCount() == 1 ) { 
						
						// adding W/L later
						this.cancel();
						L1Arena4v4.getInstance().endArena();
						return;
					}
				}
			} catch (Throwable e) {
				_log.log(Level.WARNING, e.getLocalizedMessage(), e);
			}
			
		}
		
	}

}
