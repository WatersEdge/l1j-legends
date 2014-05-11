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
 package l1j.server.server.command.executor;

import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.server.datatables.ArmorSetTable;
import l1j.server.server.datatables.BuddyTable;
import l1j.server.server.datatables.CastleTable;
import l1j.server.server.datatables.ClanTable;
import l1j.server.server.datatables.DoorTable;
import l1j.server.server.datatables.DropItemTable;
import l1j.server.server.datatables.DropTable;
import l1j.server.server.datatables.FurnitureItemTable;
import l1j.server.server.datatables.GetBackRestartTable;
import l1j.server.server.datatables.HouseTable;
import l1j.server.server.datatables.IpTable;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.datatables.MagicDollTable;
import l1j.server.server.datatables.MailTable;
import l1j.server.server.datatables.MapsTable;
import l1j.server.server.datatables.MobGroupTable;
import l1j.server.server.datatables.MobSkillTable;
import l1j.server.server.datatables.NPCTalkDataTable;
import l1j.server.server.datatables.NpcActionTable;
import l1j.server.server.datatables.NpcChatTable;
import l1j.server.server.datatables.NpcSpawnTable;
import l1j.server.server.datatables.NpcTable;
import l1j.server.server.datatables.PetItemTable;
import l1j.server.server.datatables.PetTypeTable;
import l1j.server.server.datatables.PolyTable;
import l1j.server.server.datatables.ResolventTable;
import l1j.server.server.datatables.ShopTable;
import l1j.server.server.datatables.SkillsTable;
import l1j.server.server.datatables.SpawnTimeTable;
import l1j.server.server.datatables.TownTable;
import l1j.server.server.datatables.TrapTable;
import l1j.server.server.datatables.UBSpawnTable;
import l1j.server.server.datatables.UBTable;
import l1j.server.server.datatables.WeaponSkillTable;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_SystemMessage;

public class L1ReloadTables  implements L1CommandExecutor {
	private static Logger _log = Logger.getLogger(L1ReloadTables.class.getName());

	private L1ReloadTables () {
	}

	public static L1CommandExecutor getInstance() {
		return new L1ReloadTables ();
	}

	@Override
	public void execute(L1PcInstance pc, String cmdName, String arg) {
		try {
			// New Reload Table System Added by Mike Sullivan
			ArmorSetTable.reloadTable();
			pc.sendPackets(new S_SystemMessage("ArmorSetTable Reload Complete..."));
			BuddyTable.reloadTable();
			pc.sendPackets(new S_SystemMessage("BuddyTable Reload Complete..."));
			CastleTable.reloadTable();
			pc.sendPackets(new S_SystemMessage("CastleTable Reload Complete..."));
			ClanTable.reloadTable();
			pc.sendPackets(new S_SystemMessage("ClanTable Reload Complete..."));
			DoorTable.reloadTable();
			pc.sendPackets(new S_SystemMessage("DoorTable Reload Complete..."));
			DropItemTable.reloadTable();
			pc.sendPackets(new S_SystemMessage("DropItemTable Reload Complete..."));
			DropTable.reloadTable();
			pc.sendPackets(new S_SystemMessage("DropTable Reload Complete..."));
			FurnitureItemTable.reloadTable();
			pc.sendPackets(new S_SystemMessage("FurnitureItemTable Reload Complete..."));
			GetBackRestartTable.reloadTable();
			pc.sendPackets(new S_SystemMessage("GetBackRestartTable Reload Complete..."));
			HouseTable.reloadTable();
			pc.sendPackets(new S_SystemMessage("HouseTable Reload Complete..."));
			IpTable.reloadTable();
			pc.sendPackets(new S_SystemMessage("IpTable Reload Complete..."));
			ItemTable.reloadTable();
			pc.sendPackets(new S_SystemMessage("ItemTable Reload Complete..."));
			MagicDollTable.reloadTable();
			pc.sendPackets(new S_SystemMessage("MagicDollTable Reload Complete..."));
			MailTable.reloadTable();
			pc.sendPackets(new S_SystemMessage("MailTable Reload Complete..."));
			MapsTable.reloadTable();
			pc.sendPackets(new S_SystemMessage("MapsTable Reload Complete..."));
			MobGroupTable.reloadTable();
			pc.sendPackets(new S_SystemMessage("MobGroupTable Reload Complete..."));
			MobSkillTable.reloadTable();
			pc.sendPackets(new S_SystemMessage("MobSkillTable Reload Complete..."));
			NpcActionTable.reloadTable();
			pc.sendPackets(new S_SystemMessage("NpcActionTable Reload Complete..."));
			NpcChatTable.reloadTable();
			pc.sendPackets(new S_SystemMessage("NpcChatTable Reload Complete..."));
			NpcSpawnTable.reloadTable();
			pc.sendPackets(new S_SystemMessage("NpcSpawnTable Reload Complete..."));
			NpcTable.reloadTable();
			pc.sendPackets(new S_SystemMessage("NpcTable Reload Complete..."));
			NPCTalkDataTable.reloadTable();
			pc.sendPackets(new S_SystemMessage("NpcTalkDataTable Reload Complete..."));
			PetItemTable.reloadTable();
			pc.sendPackets(new S_SystemMessage("PetItemTable Reload Complete..."));
			PetTypeTable.reloadTable();
			pc.sendPackets(new S_SystemMessage("PetTypeTable Reload Complete..."));
			PolyTable.reloadTable();
			pc.sendPackets(new S_SystemMessage("PolyTable Reload Complete..."));
			ResolventTable.reloadTable();
			pc.sendPackets(new S_SystemMessage("ResolventTable Reload Complete..."));
			ShopTable.reloadTable();
			pc.sendPackets(new S_SystemMessage("ShopTable Reload Complete..."));
			SkillsTable.reloadTable();
			pc.sendPackets(new S_SystemMessage("SkillsTable Reload Complete..."));
			SpawnTimeTable.reloadTable();
			pc.sendPackets(new S_SystemMessage("SpawnTimeTable Reload Complete..."));
			TownTable.reloadTable();
			pc.sendPackets(new S_SystemMessage("TownTable Reload Complete..."));
			TrapTable.reloadTable();
			pc.sendPackets(new S_SystemMessage("TrapTable Reload Complete..."));
			UBSpawnTable.reloadTable();
			pc.sendPackets(new S_SystemMessage("UBSpawnTable Reload Complete..."));
			UBTable.reloadTable();
			pc.sendPackets(new S_SystemMessage("UBTable Reload Complete..."));
			WeaponSkillTable.reloadTable();
			pc.sendPackets(new S_SystemMessage("WeaponSkillTable Reload Complete..."));
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		}
	}
}
