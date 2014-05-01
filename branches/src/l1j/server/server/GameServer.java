
package l1j.server.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.logging.Logger;
import java.util.Date;
import java.io.File;
import java.net.URL;

import l1j.server.server.datatables.*;
import l1j.server.server.model.L1BossCycle;
import java.text.SimpleDateFormat;

import l1j.server.Config;
import l1j.server.L1Message;
import l1j.server.console.ConsoleProcess;
import l1j.server.server.controllers.CrackOfTimeController;
import l1j.server.server.model.Dungeon;
import l1j.server.server.model.ElementalStoneGenerator;
import l1j.server.server.model.Getback;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.L1CastleLocation;
import l1j.server.server.model.L1DeleteItemOnGround;
import l1j.server.server.model.L1NpcRegenerationTimer;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.game.L1BugBearRace;
import l1j.server.server.model.gametime.L1GameTimeClock;
import l1j.server.server.model.item.L1TreasureBox;
import l1j.server.server.model.map.L1Map;
import l1j.server.server.model.map.L1WorldMap;
import l1j.server.server.model.npc.action.L1NpcDefaultAction;
import l1j.server.server.model.trap.L1WorldTraps;
import l1j.server.server.storage.mysql.MysqlAutoBackup;
import l1j.server.server.utils.KGRing;
import l1j.server.server.utils.MysqlAutoBackupTimer;
import l1j.server.server.utils.PerformanceTimer;
import l1j.server.server.utils.SystemUtil;

// Referenced classes of package l1j.server.server:
// ClientThread, Logins, RateTable, IdFactory,
// LoginController, GameTimeController, Announcements,
// MobTable, SpawnTable, SkillsTable, PolyTable,
// TeleportLocations, ShopTable, NPCTalkDataTable, NpcSpawnTable,
// IpTable, Shutdown, NpcTable, MobGroupTable, NpcShoutTable

public class GameServer extends Thread {
    private ServerSocket _serverSocket;
    private static Logger _log = Logger.getLogger(GameServer.class.getName());
    private static int YesNoCount = 0;
    private int _port;
    // private Logins _logins;
    private LoginController _loginController;
    private int chatlvl;

    @Override
    public void run() {
        System.out.println("[Statistics]");
        System.out.println("       " + L1Message.setporton + _port);
        System.out.println("       " + L1Message.memoryUse + SystemUtil.getUsedMemoryMB()+ "mb");
        System.out.println("_________________________________________________________________");
        System.out.println("");
        System.out.println("");
        while (true) {
            try {
                Socket socket = _serverSocket.accept();
                //System.out.println(L1Message.from + socket.getInetAddress()+ L1Message.attempt);
                String host = socket.getInetAddress().getHostAddress();
                if (IpTable.getInstance().isBannedIp(host)) {
                    _log.info("banned IP(" + host + ")");
                } else {
                    ClientThread client = new ClientThread(socket);
                    GeneralThreadPool.getInstance().execute(client);
                }
            } catch (IOException ioexception) {
            }
        }
    }

    private static GameServer _instance;

    private GameServer() {
        super("GameServer");
    }

    public static GameServer getInstance() {
        if (_instance == null) {
            _instance = new GameServer();
        }
        return _instance;
    }

    public void initialize() throws Exception {
        String s = Config.GAME_SERVER_HOST_NAME;
        double rateXp = Config.RATE_XP;
        double LA = Config.RATE_LA;
        double rateKarma = Config.RATE_KARMA;
        double rateDropItems = Config.RATE_DROP_ITEMS;
        double rateDropAdena = Config.RATE_DROP_ADENA;

        L1Message.getInstance();

        chatlvl = Config.GLOBAL_CHAT_LEVEL;
        _port = Config.GAME_SERVER_PORT;
        if (!"*".equals(s)) {
            InetAddress inetaddress = InetAddress.getByName(s);
            inetaddress.getHostAddress();
            _serverSocket = new ServerSocket(_port, 50, inetaddress);
        } else {
            _serverSocket = new ServerSocket(_port);
        }
        //Be sure to set the path of the jar file if you want the last build date to work - [Legends]
        Date buildDate = new Date(new File("F:\\TestServer\\L1J.jar").lastModified());
        String cleanDate = new SimpleDateFormat("MM/dd/yyyy").format(buildDate);
        int maxOnlineUsers = Config.MAX_ONLINE_USERS;

        System.out.println("");
        System.out.println("                 " + L1Message.servername);
        System.out.println("                     Built on " + cleanDate);
        System.out.println("_________________________________________________________________");
        System.out.println("");
        System.out.println("[Rates]");
        System.out.println("       " + L1Message.exp + Math.round(rateXp) +"x");
        System.out.println("       " + L1Message.justice + Math.round(LA) +"x");
        System.out.println("       " + L1Message.karma + Math.round(rateKarma) +"x");
        System.out.println("       " + L1Message.dropitems + Math.round(rateDropItems) +"x");
        System.out.println("       " + L1Message.dropadena + Math.round(rateDropAdena) +"x");
        System.out.println("       " + L1Message.enchantweapon + (Config.ENCHANT_CHANCE_WEAPON) + "%");
        System.out.println("       " + L1Message.enchantarmor + (Config.ENCHANT_CHANCE_ARMOR)+ "%");
        System.out.println("       " + L1Message.chatlevel + (chatlvl));
        System.out.println("       " + L1Message.maxplayer + (maxOnlineUsers));
        System.out.println("[Resources]");


        //Timer to calculate how long each resource load takes. - [Legends]
        PerformanceTimer timer = new PerformanceTimer();

        IdFactory.getInstance();
        timer.reset();
        L1WorldMap.getInstance();
        System.out.println("       Maps: " + timer.get() + "ms");
        _loginController = LoginController.getInstance();
        _loginController.setMaxAllowedOnlinePlayers(maxOnlineUsers);

        timer.reset();
        CharacterTable.getInstance().loadAllCharName();
        System.out.println("       Characters: " + timer.get() + "ms");

        CharacterTable.clearOnlineStatus();


        L1GameTimeClock.init();

        //Disabling Ultimate Battle as it is not functional yet - [Legends]
        //UbTimeController ubTimeContoroller = UbTimeController.getInstance();
        //GeneralThreadPool.getInstance().execute(ubTimeContoroller);

        WarTimeController warTimeController = WarTimeController.getInstance();
        GeneralThreadPool.getInstance().execute(warTimeController);

        if (Config.ELEMENTAL_STONE_AMOUNT > 0) {
            ElementalStoneGenerator elementalStoneGenerator = ElementalStoneGenerator.getInstance();
            GeneralThreadPool.getInstance().execute(elementalStoneGenerator);
        }


        HomeTownTimeController.getInstance();
        AuctionTimeController auctionTimeController = AuctionTimeController.getInstance();
        GeneralThreadPool.getInstance().execute(auctionTimeController);

        //TODO: Disable taxes and add a menu to NPC to sell house back to aden. People losing houses to non-tax payment is silly - [Legends]
        HouseTaxTimeController houseTaxTimeController = HouseTaxTimeController.getInstance();
        GeneralThreadPool.getInstance().execute(houseTaxTimeController);

        FishingTimeController fishingTimeController = FishingTimeController.getInstance();
        GeneralThreadPool.getInstance().execute(fishingTimeController);


        NpcChatTimeController npcChatTimeController = NpcChatTimeController.getInstance();
        GeneralThreadPool.getInstance().execute(npcChatTimeController);


        LightTimeController lightTimeController = LightTimeController.getInstance();
        GeneralThreadPool.getInstance().execute(lightTimeController);

        //TODO: Remove old anouncment system and put it into database. Currently reads from text file. - [Legends]
        Announcements.getInstance();
        AnnouncementsCycle.getInstance();

        //This currently does not work - [Legends]
        //MysqlAutoBackup.getInstance();
        //MysqlAutoBackupTimer.TimerStart();


        Account.InitialOnlineStatus();
        CrackOfTimeController.getStart();
        NpcTable.getInstance();
        L1DeleteItemOnGround deleteitem = new L1DeleteItemOnGround();
        deleteitem.initialize();

        if (!NpcTable.getInstance().isInitialized()) {
            throw new Exception("Could not initialize the npc table.");
        }

        //KG Ring -------------------------------------------------------------------------------------------
        KGRing.initialize();

        //NPC Actions ---------------------------------------------------------------------------------------
        timer.reset();
        L1NpcDefaultAction.getInstance();
        System.out.println("       NPC Actions: " + timer.get() + "ms");

        //Doors & Objects -----------------------------------------------------------------------------------
        DoorTable.initialize();

        //Spawn Table ---------------------------------------------------------------------------------------
        timer.reset();
        SpawnTable.getInstance();
        System.out.println("       Mobs: " + timer.get() + "ms");

        //Mob Groups ----------------------------------------------------------------------------------------
        MobGroupTable.getInstance();

        //Skills --------------------------------------------------------------------------------------------
        timer.reset();
        SkillsTable.getInstance();
        System.out.println("       Skills: " + timer.get() + "ms");

        //Poly Table ----------------------------------------------------------------------------------------
        PolyTable.getInstance();

        //Items ---------------------------------------------------------------------------------------------
        timer.reset();
        ItemTable.getInstance();
        System.out.println("       Items: " + timer.get() + "ms");

        //Drop Table ----------------------------------------------------------------------------------------
        timer.reset();
        DropTable.getInstance();

        //Drop Item Table------------------------------------------------------------------------------------
        DropItemTable.getInstance();
        System.out.println("       Drops: " + timer.get() + "ms");

        //Shops ---------------------------------------------------------------------------------------------
        timer.reset();
        ShopTable.getInstance();
        System.out.println("       Shops: " + timer.get() + "ms");

        //NPC Instances & Bosses
        NpcSpawnTable.getInstance();
        L1BossCycle.load();

        //NPC Talk ------------------------------------------------------------------------------------------
        NPCTalkDataTable.getInstance();

        //L1World Instance ----------------------------------------------------------------------------------
        L1World.getInstance();

        //Traps ---------------------------------------------------------------------------------------------
        L1WorldTraps.getInstance();

        //Dungeons ------------------------------------------------------------------------------------------
        timer.reset();
        Dungeon.getInstance();
        System.out.println("       Dungeons: " + timer.get() + "ms");

        //IPTable -------------------------------------------------------------------------------------------
        IpTable.getInstance();

        //Maps ----------------------------------------------------------------------------------------------
        MapsTable.getInstance();

        //Ultimate Battle -----------------------------------------------------------------------------------
        UBSpawnTable.getInstance();

        //Pets ----------------------------------------------------------------------------------------------
        timer.reset();
        PetTable.getInstance();
        System.out.println("       Pets: " + timer.get() + "ms");

        //Pledges -------------------------------------------------------------------------------------------
        timer.reset();
        ClanTable.getInstance();
        System.out.println("       Pledges: " + timer.get() + "ms");

        //Castles -------------------------------------------------------------------------------------------
        timer.reset();
        CastleTable.getInstance();

        //Castle Borders ------------------------------------------------------------------------------------
        L1CastleLocation.setCastleTaxRate();
        System.out.println("       Castle Data: " + timer.get() + "ms");

        //Buy Back XP ---------------------------------------------------------------------------------------
        GetBackRestartTable.getInstance();

        //Threading -----------------------------------------------------------------------------------------
        GeneralThreadPool.getInstance();

        //NPC Regen -----------------------------------------------------------------------------------------
        L1NpcRegenerationTimer.getInstance();

        //Chat Log Table ------------------------------------------------------------------------------------
        ChatLogTable.getInstance();

        //Weapon Skills -------------------------------------------------------------------------------------
        timer.reset();
        WeaponSkillTable.getInstance();
        System.out.println("       Weapon Skills: " + timer.get() + "ms");

        //NPC Actions ---------------------------------------------------------------------------------------
        NpcActionTable.load();

        //GM Commands ---------------------------------------------------------------------------------------
        GMCommandsConfig.load();

        //Get Back Table ------------------------------------------------------------------------------------
        Getback.loadGetBack();

        //Pet Types -----------------------------------------------------------------------------------------
        PetTypeTable.load();

        //Treasure Boxes ------------------------------------------------------------------------------------
        timer.reset();
        L1TreasureBox.load();
        System.out.println("       Containers: " + timer.get() + "ms");

        //Sprite Table --------------------------------------------------------------------------------------
        SprTable.getInstance();

        //Resolvent Table -----------------------------------------------------------------------------------
        ResolventTable.getInstance();

        //Furniture Data ------------------------------------------------------------------------------------
        FurnitureSpawnTable.getInstance();

        //NPC Chat Table ------------------------------------------------------------------------------------
        timer.reset();
        NpcChatTable.getInstance();

        //Mail Data -----------------------------------------------------------------------------------------
        MailTable.getInstance();

        //Race Tickets --------------------------------------------------------------------------------------
        RaceTicketTable.getInstance();

        //Bug Bear Race -------------------------------------------------------------------------------------
        //L1BugBearRace.getInstance();

        //Inn Data ------------------------------------------------------------------------------------------
        InnTable.getInstance();

        //Magic Doll Data -----------------------------------------------------------------------------------
        MagicDollTable.getInstance();

        //Furniture Items -----------------------------------------------------------------------------------
        FurnitureItemTable.getInstance();

        Runtime.getRuntime().addShutdownHook(Shutdown.getInstance());


        Thread cp = new ConsoleProcess();
        cp.start();

        this.start();
    }


    public void disconnectAllCharacters() {
        Collection<L1PcInstance> players = L1World.getInstance()
                .getAllPlayers();
        for (L1PcInstance pc : players) {
            pc.getNetConnection().setActiveChar(null);
            pc.getNetConnection().kick();
        }
        for (L1PcInstance pc : players) {
            ClientThread.quitGame(pc);
            L1World.getInstance().removeObject(pc);
            Account account = Account.load(pc.getAccountName());
            Account.online(account, false);
        }
    }

    private class ServerShutdownThread extends Thread {
        private final int _secondsCount;

        public ServerShutdownThread(int secondsCount) {
            _secondsCount = secondsCount;
        }

        @Override
        public void run() {
            L1World world = L1World.getInstance();
            try {
                int secondsCount = _secondsCount;
                world.broadcastServerMessage("Servers Will Be Shutting Down");
                world.broadcastServerMessage("Please Log Off In A Save Spot");
                while (secondsCount > 0) {
                    if (secondsCount <= 30 && secondsCount % 5 == 0) {
                        world.broadcastServerMessage("Server will be shut down in " + secondsCount + " seconds, please move to safe area and log out.");
                    }
                    else if (secondsCount <= 9) {
                        world.broadcastServerMessage("Server will be shut down in " + secondsCount + " seconds, please move to safe area and log out.");
                    }
                    else if (secondsCount % 60 == 0) {
                        world.broadcastServerMessage("Server will be shut down in " + secondsCount / 60 + " minutes.");
                    }

                    Thread.sleep(1000L);
                    secondsCount--;
                }
                shutdown();
            } catch (InterruptedException e) {
                world.broadcastServerMessage("Server shutdown has been canceled by Game Master. Please resume game play.");
                return;
            }
        }
    }

    private ServerShutdownThread _shutdownThread = null;

    public synchronized void shutdownWithCountdown(int secondsCount) {
        if (_shutdownThread != null) {
            return;
        }
        _shutdownThread = new ServerShutdownThread(secondsCount);
        GeneralThreadPool.getInstance().execute(_shutdownThread);
    }

    public void shutdown() {
        disconnectAllCharacters();
        System.exit(0);
    }

    public synchronized void abortShutdown() {
        if (_shutdownThread == null) {
            return;
        }

        _shutdownThread.interrupt();
        _shutdownThread = null;
    }

    public static int getYesNoCount() {
        YesNoCount += 1;
        return YesNoCount;
    }
}
