package l1j.server.server.model.Instance;

import java.io.PrintStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.Config;
import l1j.server.Config_Einhasad;
import l1j.server.server.ClientThread;
import l1j.server.server.GeneralThreadPool;
import l1j.server.server.PacketOutput;
import l1j.server.server.WarTimeController;
import l1j.server.server.command.executor.L1HpBar;
import l1j.server.server.datatables.CharacterTable;
import l1j.server.server.datatables.ExpTable;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.model.AcceleratorChecker;
import l1j.server.server.model.HpRegeneration;
import l1j.server.server.model.HpRegenerationByDoll;
import l1j.server.server.model.ItemMakeByDoll;
import l1j.server.server.model.L1Attack;
import l1j.server.server.model.L1CastleLocation;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1ChatParty;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1DwarfForElfInventory;
import l1j.server.server.model.L1DwarfInventory;
import l1j.server.server.model.L1EquipmentSlot;
import l1j.server.server.model.L1ExcludingList;
import l1j.server.server.model.L1Inventory;
import l1j.server.server.model.L1Karma;
import l1j.server.server.model.L1Location;
import l1j.server.server.model.L1Magic;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1Paralysis;
import l1j.server.server.model.L1Party;
import l1j.server.server.model.L1PartyRefresh;
import l1j.server.server.model.L1PcDeleteTimer;
import l1j.server.server.model.L1PcInventory;
import l1j.server.server.model.L1PinkName;
import l1j.server.server.model.L1Quest;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1TownLocation;
import l1j.server.server.model.L1Trade;
import l1j.server.server.model.L1War;
import l1j.server.server.model.L1World;
import l1j.server.server.model.MpReductionByAwake;
import l1j.server.server.model.MpRegeneration;
import l1j.server.server.model.MpRegenerationByDoll;
import l1j.server.server.model.TheCryOfSurvival;
import l1j.server.server.model.classes.L1ClassFeature;
import l1j.server.server.model.gametime.L1GameTimeCarrier;
import l1j.server.server.model.map.L1Map;
import l1j.server.server.model.monitor.L1PcAutoUpdate;
import l1j.server.server.model.monitor.L1PcExpMonitor;
import l1j.server.server.model.monitor.L1PcGhostMonitor;
import l1j.server.server.model.monitor.L1PcHellMonitor;
import l1j.server.server.model.monitor.L1PcInvisDelay;
import l1j.server.server.model.poison.L1Poison;
import l1j.server.server.model.skill.L1SkillUse;
import l1j.server.server.serverpackets.S_BlueMessage;
import l1j.server.server.serverpackets.S_CastleMaster;
import l1j.server.server.serverpackets.S_Disconnect;
import l1j.server.server.serverpackets.S_DoActionGFX;
import l1j.server.server.serverpackets.S_DoActionShop;
import l1j.server.server.serverpackets.S_Exp;
import l1j.server.server.serverpackets.S_Fight;
import l1j.server.server.serverpackets.S_Fishing;
import l1j.server.server.serverpackets.S_HPMeter;
import l1j.server.server.serverpackets.S_HPUpdate;
import l1j.server.server.serverpackets.S_Invis;
import l1j.server.server.serverpackets.S_Lawful;
import l1j.server.server.serverpackets.S_Liquor;
import l1j.server.server.serverpackets.S_MPUpdate;
import l1j.server.server.serverpackets.S_OtherCharPacks;
import l1j.server.server.serverpackets.S_OwnCharStatus;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_Poison;
import l1j.server.server.serverpackets.S_RemoveObject;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SkillIconEinhasad;
import l1j.server.server.serverpackets.S_SkillIconGFX;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.serverpackets.S_bonusstats;
import l1j.server.server.serverpackets.ServerBasePacket;
import l1j.server.server.templates.L1BookMark;
import l1j.server.server.templates.L1Item;
import l1j.server.server.templates.L1MagicDoll;
import l1j.server.server.templates.L1PrivateShopBuyList;
import l1j.server.server.templates.L1PrivateShopSellList;
import l1j.server.server.utils.CalcStat;
import l1j.server.server.utils.Random;
import l1j.server.server.utils.collections.Lists;

public class L1PcInstance extends L1Character
{
    private static final long serialVersionUID = 1L;
    public static final int CLASSID_KNIGHT_MALE = 61;
    public static final int CLASSID_KNIGHT_FEMALE = 48;
    public static final int CLASSID_ELF_MALE = 138;
    public static final int CLASSID_ELF_FEMALE = 37;
    public static final int CLASSID_WIZARD_MALE = 734;
    public static final int CLASSID_WIZARD_FEMALE = 1186;
    public static final int CLASSID_DARK_ELF_MALE = 2786;
    public static final int CLASSID_DARK_ELF_FEMALE = 2796;
    public static final int CLASSID_PRINCE = 0;
    public static final int CLASSID_PRINCESS = 1;
    public static final int CLASSID_DRAGON_KNIGHT_MALE = 6658;
    public static final int CLASSID_DRAGON_KNIGHT_FEMALE = 6661;
    public static final int CLASSID_ILLUSIONIST_MALE = 6671;
    public static final int CLASSID_ILLUSIONIST_FEMALE = 6650;
    private short _hpr = 0;

    private short _trueHpr = 0;

    boolean _rpActive = false;
    private L1PartyRefresh _rp;
    private int _partyType;
    private short _mpr = 0;

    private short _trueMpr = 0;

    public short _originalHpr = 0;

    public short _originalMpr = 0;
    private static final long INTERVAL_AUTO_UPDATE = 300L;
    private ScheduledFuture<?> _autoUpdateFuture;
    private static final long INTERVAL_EXP_MONITOR = 500L;
    private ScheduledFuture<?> _expMonitorFuture;
    private List<Integer> skillList = Lists.newList();

    private int _lap = 1;

    private int _lapCheck = 0;

    private boolean _order_list = false;

    private L1ClassFeature _classFeature = null;
    private int _PKcount;
    private int _PkCountForElf;
    private int _clanid;
    private String clanname;
    private int _clanRank;
    private Timestamp _birthday;
    private byte _sex;
    private List<L1PrivateShopSellList> _sellList = Lists.newList();

    private List<L1PrivateShopBuyList> _buyList = Lists.newList();
    private byte[] _shopChat;
    private boolean _isPrivateShop = false;

    private boolean _isTradingInPrivateShop = false;

    private int _partnersPrivateShopItemCount = 0;
    private PacketOutput _out;
    public double _oldTime = 0.0D;

    private int _originalEr = 0;

    private static Logger _log = Logger.getLogger(L1PcInstance.class.getName());
    private ClientThread _netConnection;
    private int _classId;
    private int _type;
    private int _exp;
    private final L1Karma _karma = new L1Karma();
    private boolean _gm;
    private boolean _monitor;
    private boolean _gmInvis;
    private short _accessLevel;
    private int _currentWeapon;
    private final L1PcInventory _inventory;
    private final L1DwarfInventory _dwarf;
    private final L1DwarfForElfInventory _dwarfForElf;
    private final L1Inventory _tradewindow;
    private L1ItemInstance _weapon;
    private L1Party _party;
    private L1ChatParty _chatParty;
    private int _partyID;
    private int _tradeID;
    private boolean _tradeOk;
    private int _tempID;
    private boolean _isTeleport = false;
    private boolean _isDrink = false;
    private boolean _isGres = false;
    private boolean _isPinkName = false;
    private final List<L1BookMark> _bookmarks;
    private L1Quest _quest;
    private MpRegeneration _mpRegen;
    private MpRegenerationByDoll _mpRegenByDoll;
    private MpReductionByAwake _mpReductionByAwake;
    private HpRegeneration _hpRegen;
    private HpRegenerationByDoll _hpRegenByDoll;
    private ItemMakeByDoll _itemMakeByDoll;
    private static Timer _regenTimer = new Timer(true);
    private boolean _mpRegenActive;
    private boolean _mpRegenActiveByDoll;
    private boolean _mpReductionActiveByAwake;
    private boolean _hpRegenActive;
    private boolean _hpRegenActiveByDoll;
    private boolean _ItemMakeActiveByDoll;
    private L1EquipmentSlot _equipSlot;
    private L1PcDeleteTimer _pcDeleteTimer;
    private String _accountName;
    private short _baseMaxHp = 0;

    private short _baseMaxMp = 0;

    private int _baseAc = 0;

    private int _originalAc = 0;

    private byte _baseStr = 0;

    private byte _baseCon = 0;

    private byte _baseDex = 0;

    private byte _baseCha = 0;

    private byte _baseInt = 0;

    private byte _baseWis = 0;

    private int _originalStr = 0;

    private int _originalCon = 0;

    private int _originalDex = 0;

    private int _originalCha = 0;

    private int _originalInt = 0;

    private int _originalWis = 0;

    private int _originalDmgup = 0;

    private int _originalBowDmgup = 0;

    private int _originalHitup = 0;

    private int _originalBowHitup = 0;

    private int _originalMr = 0;

    private int _originalMagicHit = 0;

    private int _originalMagicCritical = 0;

    private int _originalMagicConsumeReduction = 0;

    private int _originalMagicDamage = 0;

    private int _originalHpup = 0;

    private int _originalMpup = 0;

    private int _baseDmgup = 0;

    private int _baseBowDmgup = 0;

    private int _baseHitup = 0;

    private int _baseBowHitup = 0;

    private int _baseMr = 0;
    private int _advenHp;
    private int _advenMp;
    private int _highLevel;
    private int _bonusStats;
    private int _elixirStats;
    private int _elfAttr;
    private int _expRes;
    private int _partnerId;
    private int _onlineStatus;
    private int _homeTownId;
    private int _contribution;
    private int _pay;
    private int _hellTime;
    private boolean _banned;
    public static final int REGENSTATE_NONE = 4;
    public static final int REGENSTATE_MOVE = 2;
    public static final int REGENSTATE_ATTACK = 1;
    private int invisDelayCounter = 0;

    private Object _invisTimerMonitor = new Object();
    private static final long DELAY_INVIS = 3000L;
    private boolean _ghost = false;

    private boolean _ghostCanTalk = true;

    private boolean _isReserveGhost = false;
    private ScheduledFuture<?> _ghostFuture;
    private int _ghostSaveLocX = 0;

    private int _ghostSaveLocY = 0;

    private short _ghostSaveMapId = 0;

    private int _ghostSaveHeading = 0;
    private ScheduledFuture<?> _hellFuture;
    private Timestamp _lastPk;
    private Timestamp _lastPkForElf;
    private Timestamp _deleteTime;
    private int _weightReduction = 0;

    private int _originalStrWeightReduction = 0;

    private int _originalConWeightReduction = 0;

    private int _hasteItemEquipped = 0;

    private int _damageReductionByArmor = 0;

    private int _hitModifierByArmor = 0;

    private int _dmgModifierByArmor = 0;

    private int _bowHitModifierByArmor = 0;

    private int _bowDmgModifierByArmor = 0;
    private boolean _gresValid;
    private long _fishingTime = 0L;

    private boolean _isFishing = false;

    private boolean _isFishingReady = false;

    private int _cookingId = 0;

    private int _dessertId = 0;

    private final L1ExcludingList _excludingList = new L1ExcludingList();

    private final AcceleratorChecker _acceleratorChecker = new AcceleratorChecker(this);

    private boolean _FoeSlayer = false;

    private int _teleportX = 0;

    private int _teleportY = 0;

    private short _teleportMapId = 0;

    private int _teleportHeading = 0;
    private int _tempCharGfxAtDead;
    private boolean _isCanWhisper = true;

    private boolean _showTradeChat = true;

    private boolean _showWorldChat = true;

    private boolean _isShowTradeChat = true;

    private boolean _isShowClanChat = true;

    private boolean _isShowPartyChat = true;

    private boolean _isShowWorldChat = true;
    private int _fightId;
    private int _fishX;
    private int _fishY;
    private byte _chatCount = 0;

    private long _oldChatTimeInMillis = 0L;
    private int _callClanId;
    private int _callClanHeading;
    private boolean _isInCharReset = false;

    private int _tempLevel = 1;

    private int _tempMaxLevel = 1;

    private int _awakeSkillId = 0;

    private boolean _isSummonMonster = false;

    private boolean _isShapeChange = false;

    private boolean _dropMessages = true;

    private boolean _partyDropMessages = true;

    private boolean _dmgMessages = false;
    private boolean _dmgRMessages = false;

    private boolean _potionMessages = true;
    private TheCryOfSurvival _CryOfSurvival;
    private boolean _CryOfSurvivalActive;
    private int _CryTime = 0;
    private Timestamp _lastActive;
    private boolean _isEinLevel;
    private int _einPoint;
    private int _ein_getExp;
    private static final int einMaxPercent = Config_Einhasad.EIN_MAX_PERCENT;

    private int _kill = 0;
    private int _death = 0;


    public short getHpr()
    {
        return this._hpr;
    }

    public void addHpr(int i) {
        this._trueHpr = ((short)(this._trueHpr + i));
        this._hpr = ((short)Math.max(0, this._trueHpr));
    }

    public short getMpr()
    {
        return this._mpr;
    }

    public void addMpr(int i) {
        this._trueMpr = ((short)(this._trueMpr + i));
        this._mpr = ((short)Math.max(0, this._trueMpr));
    }

    public short getOriginalHpr()
    {
        return this._originalHpr;
    }

    public short getOriginalMpr()
    {
        return this._originalMpr;
    }

    public void startHpRegeneration() {
        int INTERVAL = 1000;

        if (!this._hpRegenActive) {
            this._hpRegen = new HpRegeneration(this);
            _regenTimer.scheduleAtFixedRate(this._hpRegen, 1000L, 1000L);
            this._hpRegenActive = true;
        }
    }

    public void stopHpRegeneration() {
        if (this._hpRegenActive) {
            this._hpRegen.cancel();
            this._hpRegen = null;
            this._hpRegenActive = false;
        }
    }

    public void startMpRegeneration() {
        int INTERVAL = 1000;

        if (!this._mpRegenActive) {
            this._mpRegen = new MpRegeneration(this);
            _regenTimer.scheduleAtFixedRate(this._mpRegen, 1000L, 1000L);
            this._mpRegenActive = true;
        }
    }

    public void stopMpRegeneration() {
        if (this._mpRegenActive) {
            this._mpRegen.cancel();
            this._mpRegen = null;
            this._mpRegenActive = false;
        }
    }

    public void startItemMakeByDoll()
    {
        int INTERVAL_BY_DOLL = 240000;
        boolean isExistItemMakeDoll = false;
        if (L1MagicDoll.isItemMake(this)) {
            isExistItemMakeDoll = true;
        }
        if ((!this._ItemMakeActiveByDoll) && (isExistItemMakeDoll)) {
            this._itemMakeByDoll = new ItemMakeByDoll(this);
            _regenTimer.scheduleAtFixedRate(this._itemMakeByDoll, 240000L,
                    240000L);
            this._ItemMakeActiveByDoll = true;
        }
    }

    public void stopItemMakeByDoll()
    {
        if (this._ItemMakeActiveByDoll) {
            this._itemMakeByDoll.cancel();
            this._itemMakeByDoll = null;
            this._ItemMakeActiveByDoll = false;
        }
    }

    public void startHpRegenerationByDoll()
    {
        int INTERVAL_BY_DOLL = 64000;
        boolean isExistHprDoll = false;
        if (L1MagicDoll.isHpRegeneration(this)) {
            isExistHprDoll = true;
        }
        if ((!this._hpRegenActiveByDoll) && (isExistHprDoll)) {
            this._hpRegenByDoll = new HpRegenerationByDoll(this);
            _regenTimer.scheduleAtFixedRate(this._hpRegenByDoll, 64000L,
                    64000L);
            this._hpRegenActiveByDoll = true;
        }
    }

    public void stopHpRegenerationByDoll()
    {
        if (this._hpRegenActiveByDoll) {
            this._hpRegenByDoll.cancel();
            this._hpRegenByDoll = null;
            this._hpRegenActiveByDoll = false;
        }
    }

    public void startMpRegenerationByDoll()
    {
        int INTERVAL_BY_DOLL = 64000;
        boolean isExistMprDoll = false;
        if (L1MagicDoll.isMpRegeneration(this)) {
            isExistMprDoll = true;
        }
        if ((!this._mpRegenActiveByDoll) && (isExistMprDoll)) {
            this._mpRegenByDoll = new MpRegenerationByDoll(this);
            _regenTimer.scheduleAtFixedRate(this._mpRegenByDoll, 64000L, 64000L);
            this._mpRegenActiveByDoll = true;
        }
    }

    public void stopMpRegenerationByDoll()
    {
        if (this._mpRegenActiveByDoll) {
            this._mpRegenByDoll.cancel();
            this._mpRegenByDoll = null;
            this._mpRegenActiveByDoll = false;
        }
    }

    public void startMpReductionByAwake() {
        int INTERVAL_BY_AWAKE = 4000;
        if (!this._mpReductionActiveByAwake) {
            this._mpReductionByAwake = new MpReductionByAwake(this);
            _regenTimer.scheduleAtFixedRate(this._mpReductionByAwake, 4000L, 4000L);
            this._mpReductionActiveByAwake = true;
        }
    }

    public void stopMpReductionByAwake() {
        if (this._mpReductionActiveByAwake) {
            this._mpReductionByAwake.cancel();
            this._mpReductionByAwake = null;
            this._mpReductionActiveByAwake = false;
        }
    }

    public void startObjectAutoUpdate() {
        removeAllKnownObjects();
        this._autoUpdateFuture = GeneralThreadPool.getInstance().pcScheduleAtFixedRate(new L1PcAutoUpdate(getId()), 0L, 300L);
    }

    public void stopEtcMonitor()
    {
        if (this._autoUpdateFuture != null) {
            this._autoUpdateFuture.cancel(true);
            this._autoUpdateFuture = null;
        }
        if (this._expMonitorFuture != null) {
            this._expMonitorFuture.cancel(true);
            this._expMonitorFuture = null;
        }
        if (this._ghostFuture != null) {
            this._ghostFuture.cancel(true);
            this._ghostFuture = null;
        }

        if (this._hellFuture != null) {
            this._hellFuture.cancel(true);
            this._hellFuture = null;
        }
    }

    public void onChangeExp()
    {
        int level = ExpTable.getLevelByExp(getExp());
        int char_level = getLevel();
        int gap = level - char_level;
        if (gap == 0)
        {
            sendPackets(new S_Exp(this));
            return;
        }

        if (gap > 0) {
            levelUp(gap);
        }
        else if (gap < 0)
            levelDown(gap);
    }

    public void onPerceive(L1PcInstance perceivedFrom)
    {
        if ((perceivedFrom.getMapId() >= 16384) && (perceivedFrom.getMapId() <= 25088) &&
                (perceivedFrom.getInnKeyId() != getInnKeyId())) {
            return;
        }
        if ((isGmInvis()) || (isGhost())) {
            return;
        }
        if ((isInvisble()) && (!perceivedFrom.hasSkillEffect(2003))) {
            return;
        }

        perceivedFrom.addKnownObject(this);
        perceivedFrom.sendPackets(new S_OtherCharPacks(this, perceivedFrom.hasSkillEffect(2003)));
        if ((isInParty()) && (getParty().isMember(perceivedFrom))) {
            perceivedFrom.sendPackets(new S_HPMeter(this));
        }

        if (isPrivateShop())
            perceivedFrom.sendPackets(new S_DoActionShop(getId(), 70, getShopChat()));
        else if (isFishing()) {
            perceivedFrom.sendPackets(new S_Fishing(getId(), 71, getFishX(), getFishY()));
        }

        if (isCrown()) {
            L1Clan clan = L1World.getInstance().getClan(getClanname());
            if ((clan != null) &&
                    (getId() == clan.getLeaderId()))
            {
                if (clan.getCastleId() != 0)
                    perceivedFrom.sendPackets(new S_CastleMaster(clan.getCastleId(), getId()));
            }
        }
    }

    private void removeOutOfRangeObjects()
    {
        for (L1Object known : getKnownObjects())
            if (known != null)
            {
                if (Config.PC_RECOGNIZE_RANGE == -1) {
                    if (!getLocation().isInScreen(known.getLocation())) {
                        removeKnownObject(known);
                        sendPackets(new S_RemoveObject(known));
                    }

                }
                else if (getLocation().getTileLineDistance(known.getLocation()) > Config.PC_RECOGNIZE_RANGE) {
                    removeKnownObject(known);
                    sendPackets(new S_RemoveObject(known));
                }
            }
    }

    public void updateObject()
    {
        removeOutOfRangeObjects();

        if (getMapId() <= 10000) {
            for (L1Object visible : L1World.getInstance().getVisibleObjects(this, Config.PC_RECOGNIZE_RANGE)) {
                if (!knownsObject(visible)) {
                    visible.onPerceive(this);
                }
                else if ((visible instanceof L1NpcInstance)) {
                    L1NpcInstance npc = (L1NpcInstance)visible;
                    if ((getLocation().isInScreen(npc.getLocation())) && (npc.getHiddenStatus() != 0)) {
                        npc.approachPlayer(this);
                    }
                }

                if ((hasSkillEffect(2001)) && (L1HpBar.isHpBarTarget(visible)))
                    sendPackets(new S_HPMeter((L1Character)visible));
            }
        }
        else
            for (L1Object visible : L1World.getInstance().getVisiblePlayer(this)) {
                if (!knownsObject(visible)) {
                    visible.onPerceive(this);
                }
                if ((hasSkillEffect(2001)) && (L1HpBar.isHpBarTarget(visible)) &&
                        (getInnKeyId() == ((L1Character)visible).getInnKeyId()))
                    sendPackets(new S_HPMeter((L1Character)visible));
            }
    }

    private void sendVisualEffect()
    {
        int poisonId = 0;
        if (getPoison() != null) {
            poisonId = getPoison().getEffectId();
        }
        if (getParalysis() != null)
        {
            poisonId = getParalysis().getEffectId();
        }
        if (poisonId != 0) {
            sendPackets(new S_Poison(getId(), poisonId));
            broadcastPacket(new S_Poison(getId(), poisonId));
        }
    }

    public void sendVisualEffectAtLogin()
    {
        if (getClanid() != 0) {
            L1Clan clan = L1World.getInstance().getClan(getClanname());
            if ((clan != null) &&
                    (isCrown()) && (getId() == clan.getLeaderId()) &&
                    (clan.getCastleId() != 0)) {
                sendPackets(new S_CastleMaster(clan.getCastleId(), getId()));
            }

        }

        sendVisualEffect();
    }

    public void sendVisualEffectAtTeleport() {
        if (isDrink()) {
            sendPackets(new S_Liquor(getId(), 1));
        }

        sendVisualEffect();
    }

    public void setSkillMastery(int skillid)
    {
        if (!this.skillList.contains(Integer.valueOf(skillid)))
            this.skillList.add(Integer.valueOf(skillid));
    }

    public void removeSkillMastery(int skillid)
    {
        if (this.skillList.contains(Integer.valueOf(skillid)))
            this.skillList.remove(Integer.valueOf(skillid));
    }

    public boolean isSkillMastery(int skillid)
    {
        return this.skillList.contains(Integer.valueOf(skillid));
    }

    public void clearSkillMastery() {
        this.skillList.clear();
    }

    public void setLap(int i)
    {
        this._lap = i;
    }

    public int getLap() {
        return this._lap;
    }

    public void setLapCheck(int i)
    {
        this._lapCheck = i;
    }

    public int getLapCheck() {
        return this._lapCheck;
    }

    public int getLapScore()
    {
        return this._lap * 29 + this._lapCheck;
    }

    public boolean isInOrderList()
    {
        return this._order_list;
    }

    public void setInOrderList(boolean bool) {
        this._order_list = bool;
    }

    public L1PcInstance() {
        this._accessLevel = 0;
        this._currentWeapon = 0;
        this._inventory = new L1PcInventory(this);
        this._dwarf = new L1DwarfInventory(this);
        this._dwarfForElf = new L1DwarfForElfInventory(this);
        this._tradewindow = new L1Inventory();
        this._bookmarks = Lists.newList();
        this._quest = new L1Quest(this);
        this._equipSlot = new L1EquipmentSlot(this);
    }

    public void setCurrentHp(int i)
    {
        if (getCurrentHp() == i) {
            return;
        }
        int currentHp = i;
        if (currentHp >= getMaxHp()) {
            currentHp = getMaxHp();
        }
        setCurrentHpDirect(currentHp);
        sendPackets(new S_HPUpdate(currentHp, getMaxHp()));
        if (isInParty())
            getParty().updateMiniHP(this);
    }

    public void setCurrentMp(int i)
    {
        if (getCurrentMp() == i) {
            return;
        }
        int currentMp = i;
        if ((currentMp >= getMaxMp()) || (isGm())) {
            currentMp = getMaxMp();
        }
        setCurrentMpDirect(currentMp);
        sendPackets(new S_MPUpdate(currentMp, getMaxMp()));
    }

    public L1PcInventory getInventory()
    {
        return this._inventory;
    }

    public L1DwarfInventory getDwarfInventory() {
        return this._dwarf;
    }

    public L1DwarfForElfInventory getDwarfForElfInventory() {
        return this._dwarfForElf;
    }

    public L1Inventory getTradeWindowInventory() {
        return this._tradewindow;
    }

    public boolean isGmInvis() {
        return this._gmInvis;
    }

    public void setGmInvis(boolean flag) {
        this._gmInvis = flag;
    }

    public int getCurrentWeapon() {
        return this._currentWeapon;
    }

    public void setCurrentWeapon(int i) {
        this._currentWeapon = i;
    }

    public int getType() {
        return this._type;
    }

    public void setType(int i) {
        this._type = i;
    }

    public short getAccessLevel() {
        return this._accessLevel;
    }

    public void setAccessLevel(short i) {
        this._accessLevel = i;
    }

    public int getClassId() {
        return this._classId;
    }

    public void setClassId(int i) {
        this._classId = i;
        this._classFeature = L1ClassFeature.newClassFeature(i);
    }

    public L1ClassFeature getClassFeature()
    {
        return this._classFeature;
    }

    public synchronized int getExp()
    {
        return this._exp;
    }

    public synchronized void setExp(int i)
    {
        this._exp = i;
    }

    public int get_PKcount()
    {
        return this._PKcount;
    }

    public void set_PKcount(int i) {
        this._PKcount = i;
    }

    public int getPkCountForElf()
    {
        return this._PkCountForElf;
    }

    public void setPkCountForElf(int i) {
        this._PkCountForElf = i;
    }

    public int getClanid()
    {
        return this._clanid;
    }

    public void setClanid(int i) {
        this._clanid = i;
    }

    public String getClanname()
    {
        return this.clanname;
    }

    public void setClanname(String s) {
        this.clanname = s;
    }

    public L1Clan getClan()
    {
        return L1World.getInstance().getClan(getClanname());
    }

    public int getClanRank()
    {
        return this._clanRank;
    }

    public void setClanRank(int i) {
        this._clanRank = i;
    }

    public Timestamp getBirthday()
    {
        return this._birthday;
    }

    public int getSimpleBirthday() {
        if (this._birthday != null) {
            SimpleDateFormat SimpleDate = new SimpleDateFormat("yyyyMMdd");
            int BornTime = Integer.parseInt(SimpleDate.format(Long.valueOf(this._birthday.getTime())));
            return BornTime;
        }

        return 0;
    }

    public void setBirthday(Timestamp time)
    {
        this._birthday = time;
    }

    public void setBirthday() {
        this._birthday = new Timestamp(System.currentTimeMillis());
    }

    public byte get_sex()
    {
        return this._sex;
    }

    public void set_sex(int i) {
        this._sex = ((byte)i);
    }

    public boolean isGm() {
        return this._gm;
    }

    public void setGm(boolean flag) {
        this._gm = flag;
    }

    public boolean isMonitor() {
        return this._monitor;
    }

    public void setMonitor(boolean flag) {
        this._monitor = flag;
    }

    private L1PcInstance getStat() {
        return null;
    }

    public void reduceCurrentHp(double d, L1Character l1character) {
        getStat().reduceCurrentHp(d, l1character);
    }

    private void notifyPlayersLogout(List<L1PcInstance> playersArray)
    {
        for (L1PcInstance player : playersArray)
            if (player.knownsObject(this)) {
                player.removeKnownObject(this);
                player.sendPackets(new S_RemoveObject(this));
            }
    }

    public void logout()
    {
        L1World world = L1World.getInstance();
        if (getClanid() != 0)
        {
            L1Clan clan = world.getClan(getClanname());
            if ((clan != null) &&
                    (clan.getWarehouseUsingChar() == getId()))
            {
                clan.setWarehouseUsingChar(0);
            }
        }

        notifyPlayersLogout(getKnownPlayers());
        world.removeVisibleObject(this);
        world.removeObject(this);
        notifyPlayersLogout(world.getRecognizePlayer(this));
        this._inventory.clearItems();
        this._dwarf.clearItems();
        removeAllKnownObjects();
        stopHpRegeneration();
        stopMpRegeneration();
        stopCryOfSurvival();
        setCryTime(0);
        setDead(true);
        setNetConnection(null);
        setPacketOutput(null);
    }

    public ClientThread getNetConnection() {
        return this._netConnection;
    }

    public void setNetConnection(ClientThread clientthread) {
        this._netConnection = clientthread;
    }

    public boolean isInParty() {
        return getParty() != null;
    }

    public L1Party getParty() {
        return this._party;
    }

    public void setParty(L1Party p) {
        this._party = p;
    }

    public boolean isInChatParty() {
        return getChatParty() != null;
    }

    public L1ChatParty getChatParty() {
        return this._chatParty;
    }

    public void setChatParty(L1ChatParty cp) {
        this._chatParty = cp;
    }

    public int getPartyID() {
        return this._partyID;
    }

    public void setPartyID(int partyID) {
        this._partyID = partyID;
    }

    public int getTradeID() {
        return this._tradeID;
    }

    public void setTradeID(int tradeID) {
        this._tradeID = tradeID;
    }

    public void setTradeOk(boolean tradeOk) {
        this._tradeOk = tradeOk;
    }

    public boolean getTradeOk() {
        return this._tradeOk;
    }

    public int getTempID() {
        return this._tempID;
    }

    public void setTempID(int tempID) {
        this._tempID = tempID;
    }

    public boolean isTeleport() {
        return this._isTeleport;
    }

    public void setTeleport(boolean flag) {
        this._isTeleport = flag;
    }

    public boolean isDrink() {
        return this._isDrink;
    }

    public void setDrink(boolean flag) {
        this._isDrink = flag;
    }

    public boolean isGres() {
        return this._isGres;
    }

    public void setGres(boolean flag) {
        this._isGres = flag;
    }

    public boolean isPinkName() {
        return this._isPinkName;
    }

    public void setPinkName(boolean flag) {
        this._isPinkName = flag;
    }

    public List<L1PrivateShopSellList> getSellList()
    {
        return this._sellList;
    }

    public List<L1PrivateShopBuyList> getBuyList()
    {
        return this._buyList;
    }

    public void setShopChat(byte[] chat)
    {
        this._shopChat = chat;
    }

    public byte[] getShopChat() {
        return this._shopChat;
    }

    public boolean isPrivateShop()
    {
        return this._isPrivateShop;
    }

    public void setPrivateShop(boolean flag) {
        this._isPrivateShop = flag;
    }

    public boolean isTradingInPrivateShop()
    {
        return this._isTradingInPrivateShop;
    }

    public void setTradingInPrivateShop(boolean flag) {
        this._isTradingInPrivateShop = flag;
    }

    public int getPartnersPrivateShopItemCount()
    {
        return this._partnersPrivateShopItemCount;
    }

    public void setPartnersPrivateShopItemCount(int i) {
        this._partnersPrivateShopItemCount = i;
    }

    public void setPacketOutput(PacketOutput out)
    {
        this._out = out;
    }

    public void sendPackets(ServerBasePacket serverbasepacket) {
        if (this._out == null) {
            return;
        }
        try
        {
            this._out.sendPacket(serverbasepacket);
        }
        catch (Exception localException) {
        }
    }

    public void onAction(L1PcInstance attacker) {
        onAction(attacker, 0);
    }

    public void onAction(L1PcInstance attacker, int skillId)
    {
        if (attacker == null) {
            return;
        }

        if (isTeleport()) {
            return;
        }

        if ((getZoneType() == 1) || (attacker.getZoneType() == 1))
        {
            L1Attack attack_mortion = new L1Attack(attacker, this, skillId);
            attack_mortion.action();
            return;
        }

        if (checkNonPvP(this, attacker))
        {
            L1Attack attack_mortion = new L1Attack(attacker, this, skillId);
            attack_mortion.action();
            return;
        }

        if ((getCurrentHp() > 0) && (!isDead())) {
            attacker.delInvis();

            L1Attack attack = new L1Attack(attacker, this);

            if (hasSkillEffect(91)) {
                L1Magic magic = new L1Magic(this, attacker);
                if ((magic.calcProbabilityMagic(91)) &&
                        (attack.isShortDistance()) && (!attacker.isFoeSlayer())) {
                    attack.actionCounterBarrier();
                    attack.commitCounterBarrier();
                    return;
                }
            }
            if (attack.calcHit()) {
                attacker.setPetTarget(this);
                attack.calcDamage();
                attack.calcStaffOfMana();
                attack.addPcPoisonAttack(attacker, this);
                attack.addChaserAttack();
            }
            attack.action();
            attack.commit();
        }
    }

    public boolean checkNonPvP(L1PcInstance pc, L1Character target) {
        L1PcInstance targetpc = null;
        if ((target instanceof L1PcInstance)) {
            targetpc = (L1PcInstance)target;
        }
        else if ((target instanceof L1PetInstance)) {
            targetpc = (L1PcInstance)((L1PetInstance)target).getMaster();
        }
        else if ((target instanceof L1SummonInstance)) {
            targetpc = (L1PcInstance)((L1SummonInstance)target).getMaster();
        }
        if (targetpc == null) {
            return false;
        }
        if (!Config.ALT_NONPVP) {
            if (getMap().isCombatZone(getLocation())) {
                return false;
            }

            for (L1War war : L1World.getInstance().getWarList()) {
                if ((pc.getClanid() != 0) && (targetpc.getClanid() != 0)) {
                    boolean same_war = war.CheckClanInSameWar(pc.getClanname(), targetpc.getClanname());
                    if (same_war) {
                        return false;
                    }
                }
            }

            if ((target instanceof L1PcInstance)) {
                L1PcInstance targetPc = (L1PcInstance)target;
                if (isInWarAreaAndWarTime(pc, targetPc)) {
                    return false;
                }
            }
            return true;
        }

        return false;
    }

    private boolean isInWarAreaAndWarTime(L1PcInstance pc, L1PcInstance target)
    {
        int castleId = L1CastleLocation.getCastleIdByArea(pc);
        int targetCastleId = L1CastleLocation.getCastleIdByArea(target);
        if ((castleId != 0) && (targetCastleId != 0) && (castleId == targetCastleId) &&
                (WarTimeController.getInstance().isNowWar(castleId))) {
            return true;
        }

        return false;
    }

    public void setPetTarget(L1Character target) {
        Object[] petList = getPetList().values().toArray();
        for (Object pet : petList)
            if ((pet instanceof L1PetInstance)) {
                L1PetInstance pets = (L1PetInstance)pet;
                pets.setMasterTarget(target);
            }
            else if ((pet instanceof L1SummonInstance)) {
                L1SummonInstance summon = (L1SummonInstance)pet;
                summon.setMasterTarget(target);
            }
    }

    public void delInvis()
    {
        if (hasSkillEffect(60)) {
            killSkillEffectTimer(60);
            sendPackets(new S_Invis(getId(), 0));
            broadcastPacket(new S_OtherCharPacks(this));
        }
        if (hasSkillEffect(97)) {
            killSkillEffectTimer(97);
            sendPackets(new S_Invis(getId(), 0));
            broadcastPacket(new S_OtherCharPacks(this));
        }
    }

    public void delBlindHiding()
    {
        killSkillEffectTimer(97);
        sendPackets(new S_Invis(getId(), 0));
        broadcastPacket(new S_OtherCharPacks(this));
    }

    public void receiveDamage(L1Character attacker, int damage, int attr)
    {
        int player_mr = getMr();
        int rnd = Random.nextInt(100) + 1;
        if (player_mr >= rnd) {
            damage /= 2;
        }
        receiveDamage(attacker, damage, false);
    }

    public void receiveManaDamage(L1Character attacker, int mpDamage) {
        if ((mpDamage > 0) && (!isDead())) {
            delInvis();
            if ((attacker instanceof L1PcInstance)) {
                L1PinkName.onAction(this, attacker);
            }
            if (((attacker instanceof L1PcInstance)) && (((L1PcInstance)attacker).isPinkName()))
            {
                for (L1Object object : L1World.getInstance().getVisibleObjects(attacker)) {
                    if ((object instanceof L1GuardInstance)) {
                        L1GuardInstance guard = (L1GuardInstance)object;
                        guard.setTarget((L1PcInstance)attacker);
                    }
                }
            }

            int newMp = getCurrentMp() - mpDamage;
            if (newMp > getMaxMp()) {
                newMp = getMaxMp();
            }

            if (newMp <= 0) {
                newMp = 0;
            }
            setCurrentMp(newMp);
        }
    }

    public void receiveDamage(L1Character attacker, double damage, boolean isMagicDamage)
    {
        if ((getCurrentHp() > 0) && (!isDead())) {
            if ((attacker != this) &&
                    (!(attacker instanceof L1EffectInstance)) && (!knownsObject(attacker)) && (attacker.getMapId() == getMapId())) {
                attacker.onPerceive(this);
            }

            if (isMagicDamage) {
                double nowTime = System.currentTimeMillis();
                double interval = (20.0D - (nowTime - this._oldTime) / 100.0D) % 20.0D;

                if (damage > 0.0D) {
                    if (interval > 0.0D) {
                        damage *= (1.0D - interval / 30.0D);
                    }
                    if (damage < 1.0D) {
                        damage = 0.0D;
                    }

                    this._oldTime = nowTime;
                }
            }
            if (damage > 0.0D) {
                delInvis();
                if ((attacker instanceof L1PcInstance)) {
                    L1PinkName.onAction(this, attacker);
                }
                if (((attacker instanceof L1PcInstance)) && (((L1PcInstance)attacker).isPinkName()))
                {
                    for (L1Object object : L1World.getInstance().getVisibleObjects(attacker)) {
                        if ((object instanceof L1GuardInstance)) {
                            L1GuardInstance guard = (L1GuardInstance)object;
                            guard.setTarget((L1PcInstance)attacker);
                        }
                    }
                }
                removeSkillEffect(66);
                removeSkillEffect(212);
            }

            if ((hasSkillEffect(191)) && (getId() != attacker.getId())) {
                int rnd = Random.nextInt(100) + 1;
                if ((damage > 0.0D) && (rnd <= 10)) {
                    if ((attacker instanceof L1PcInstance)) {
                        L1PcInstance attackPc = (L1PcInstance)attacker;
                        attackPc.sendPackets(new S_DoActionGFX(attackPc.getId(), 2));
                        attackPc.broadcastPacket(new S_DoActionGFX(attackPc.getId(), 2));
                        attackPc.receiveDamage(this, 30.0D, false);
                    }
                    else if ((attacker instanceof L1NpcInstance)) {
                        L1NpcInstance attackNpc = (L1NpcInstance)attacker;
                        attackNpc.broadcastPacket(new S_DoActionGFX(attackNpc.getId(), 2));
                        attackNpc.receiveDamage(this, 30);
                    }
                }
            }
            if ((getInventory().checkEquipped(145)) ||
                    (getInventory().checkEquipped(149))) {
                damage *= 1.5D;
            }
            if (hasSkillEffect(219)) {
                damage *= 1.2D;
            }
            if ((attacker instanceof L1PetInstance)) {
                L1PetInstance pet = (L1PetInstance)attacker;

                if ((getZoneType() == 1) || (pet.getZoneType() == 1) || (checkNonPvP(this, pet)))
                    damage = 0.0D;
            }
            else if ((attacker instanceof L1SummonInstance)) {
                L1SummonInstance summon = (L1SummonInstance)attacker;

                if ((getZoneType() == 1) || (summon.getZoneType() == 1) || (checkNonPvP(this, summon))) {
                    damage = 0.0D;
                }
            }
            int newHp = getCurrentHp() - (int)damage;
            if (newHp > getMaxHp()) {
                newHp = getMaxHp();
            }
            if (newHp <= 0) {
                if (isGm()) {
                    setCurrentHp(getMaxHp());
                }
                else {
                    death(attacker);
                }
            }
            if (newHp > 0) {
                setCurrentHp(newHp);
            }
            int healHp = 0;
            if ((getInventory().checkEquipped(21119)) ||
                    (getInventory().checkEquipped(21120)) ||
                    (getInventory().checkEquipped(21121)) ||
                    (getInventory().checkEquipped(21122))) {
                int rnd = Random.nextInt(100) + 1;
                if ((damage > 0.0D) && (rnd <= 5)) {
                    sendPackets(new S_SkillSound(getId(), 2187));
                    broadcastPacket(new S_SkillSound(getId(), 2187));
                    healHp = Random.nextInt(60) + 60;
                    newHp += healHp;
                    if (newHp > getMaxHp()) {
                        newHp = getMaxHp();
                    }
                    setCurrentHp(newHp);
                }
            }
        }
        else if (!isDead()) {
            death(attacker);
        }
    }

    public void death(L1Character lastAttacker) {
        synchronized (this) {
            if (isDead()) {
                return;
            }
            setDead(true);
            setStatus(8);
        }

        if (getTradeID() != 0) {
            L1Trade trade = new L1Trade();
            trade.TradeCancel(this);
        }

        GeneralThreadPool.getInstance().execute(new Death(lastAttacker));
    }

    public void stopPcDeleteTimer()
    {
        if (this._pcDeleteTimer != null) {
            this._pcDeleteTimer.cancel();
            this._pcDeleteTimer = null;
        }
    }

    private void caoPenaltyResult(int count) {
        for (int i = 0; i < count; i++) {
            L1ItemInstance item = getInventory().CaoPenalty();

            if (item != null) {
                getInventory().tradeItem(item, item.isStackable() ? item.getCount() : 1,
                        L1World.getInstance().getInventory(getX(), getY(), getMapId()));
                sendPackets(new S_ServerMessage(638, item.getLogName()));
            }
        }
    }

    public boolean castleWarResult()
    {
        if ((getClanid() != 0) && (isCrown())) {
            L1Clan clan = L1World.getInstance().getClan(getClanname());

            for (L1War war : L1World.getInstance().getWarList()) {
                int warType = war.GetWarType();
                boolean isInWar = war.CheckClanInWar(getClanname());
                boolean isAttackClan = war.CheckAttackClan(getClanname());
                if ((getId() == clan.getLeaderId()) &&
                        (warType == 1) && (isInWar) && (isAttackClan)) {
                    String enemyClanName = war.GetEnemyClanName(getClanname());
                    if (enemyClanName == null) break;
                    war.CeaseWar(getClanname(), enemyClanName);

                    break;
                }
            }
        }

        int castleId = 0;
        boolean isNowWar = false;
        castleId = L1CastleLocation.getCastleIdByArea(this);
        if (castleId != 0) {
            isNowWar = WarTimeController.getInstance().isNowWar(castleId);
        }
        return isNowWar;
    }

    public boolean simWarResult(L1Character lastAttacker) {
        if (getClanid() == 0) {
            return false;
        }
        if (Config.SIM_WAR_PENALTY) {
            return false;
        }
        L1PcInstance attacker = null;
        String enemyClanName = null;
        boolean sameWar = false;

        if ((lastAttacker instanceof L1PcInstance)) {
            attacker = (L1PcInstance)lastAttacker;
        }
        else if ((lastAttacker instanceof L1PetInstance)) {
            attacker = (L1PcInstance)((L1PetInstance)lastAttacker).getMaster();
        }
        else if ((lastAttacker instanceof L1SummonInstance)) {
            attacker = (L1PcInstance)((L1SummonInstance)lastAttacker).getMaster();
        }
        else {
            return false;
        }

        for (L1War war : L1World.getInstance().getWarList()) {
            L1Clan clan = L1World.getInstance().getClan(getClanname());

            int warType = war.GetWarType();
            boolean isInWar = war.CheckClanInWar(getClanname());
            if ((attacker != null) && (attacker.getClanid() != 0)) {
                sameWar = war.CheckClanInSameWar(getClanname(), attacker.getClanname());
            }

            if ((getId() == clan.getLeaderId()) &&
                    (warType == 2) && (isInWar)) {
                enemyClanName = war.GetEnemyClanName(getClanname());
                if (enemyClanName != null) {
                    war.CeaseWar(getClanname(), enemyClanName);
                }
            }

            if ((warType == 2) && (sameWar)) {
                return true;
            }
        }
        return false;
    }

    public void resExp() {
        int oldLevel = getLevel();
        int needExp = ExpTable.getNeedExpNextLevel(oldLevel);
        int exp = 0;
        if (oldLevel < 45) {
            exp = (int)(needExp * 0.05D);
        }
        else if (oldLevel == 45) {
            exp = (int)(needExp * 0.045D);
        }
        else if (oldLevel == 46) {
            exp = (int)(needExp * 0.04D);
        }
        else if (oldLevel == 47) {
            exp = (int)(needExp * 0.035D);
        }
        else if (oldLevel == 48) {
            exp = (int)(needExp * 0.03D);
        }
        else if ((oldLevel >= 49) && (oldLevel < 65))
            exp = (int)(needExp * 0.025D);
        else if ((oldLevel >= 65) && (oldLevel < 70))
            exp = (int)(needExp * 0.0125D);
        else if ((oldLevel >= 65) && (oldLevel < 75))
            exp = (int)(needExp * 0.00625D);
        else if ((oldLevel >= 75) && (oldLevel < 79))
            exp = (int)(needExp * 0.003125D);
        else if ((oldLevel >= 79) && (oldLevel < 80))
            exp = (int)(needExp * 0.0015625D);
        else if (oldLevel >= 80) {
            exp = (int)(needExp * 0.00078125D);
        }

        if (exp == 0) {
            return;
        }
        addExp(exp);
    }

    public void deathPenalty() {
        int oldLevel = getLevel();
        int needExp = ExpTable.getNeedExpNextLevel(oldLevel);
        int exp = 0;
        if ((oldLevel >= 1) && (oldLevel < 11)) {
            exp = 0;
        }
        else if ((oldLevel >= 11) && (oldLevel < 45)) {
            exp = (int)(needExp * 0.1D);
        }
        else if (oldLevel == 45) {
            exp = (int)(needExp * 0.09D);
        }
        else if (oldLevel == 46) {
            exp = (int)(needExp * 0.08D);
        }
        else if (oldLevel == 47) {
            exp = (int)(needExp * 0.07000000000000001D);
        }
        else if (oldLevel == 48) {
            exp = (int)(needExp * 0.06D);
        }
        else if ((oldLevel >= 49) && (oldLevel < 65))
            exp = (int)(needExp * 0.05D);
        else if ((oldLevel >= 65) && (oldLevel < 70))
            exp = (int)(needExp * 0.025D);
        else if ((oldLevel >= 65) && (oldLevel < 75))
            exp = (int)(needExp * 0.0125D);
        else if ((oldLevel >= 75) && (oldLevel < 79))
            exp = (int)(needExp * 0.00625D);
        else if ((oldLevel >= 79) && (oldLevel < 80))
            exp = (int)(needExp * 0.003125D);
        else if (oldLevel >= 80) {
            exp = (int)(needExp * 0.0015625D);
        }

        if (exp == 0) {
            return;
        }

        if (getExpRes() != 1) setExpRes(1);

        addExp(-exp);
    }

    public int getOriginalEr()
    {
        return this._originalEr;
    }

    public int getEr() {
        if (hasSkillEffect(174)) {
            return 0;
        }

        int er = 0;
        if (isKnight()) {
            er = getLevel() / 4;
        }
        else if ((isCrown()) || (isElf())) {
            er = getLevel() / 8;
        }
        else if (isDarkelf()) {
            er = getLevel() / 6;
        }
        else if (isWizard()) {
            er = getLevel() / 10;
        }
        else if (isDragonKnight()) {
            er = getLevel() / 7;
        }
        else if (isIllusionist()) {
            er = getLevel() / 9;
        }

        er += (getDex() - 8) / 2;

        er += getOriginalEr();

        if (hasSkillEffect(111)) {
            er += 12;
        }
        if (hasSkillEffect(90)) {
            er += 15;
        }
        return er;
    }

    public L1BookMark getBookMark(String name) {
        for (int i = 0; i < this._bookmarks.size(); i++) {
            L1BookMark element = (L1BookMark)this._bookmarks.get(i);
            if (element.getName().equalsIgnoreCase(name)) {
                return element;
            }
        }

        return null;
    }

    public L1BookMark getBookMark(int id) {
        for (int i = 0; i < this._bookmarks.size(); i++) {
            L1BookMark element = (L1BookMark)this._bookmarks.get(i);
            if (element.getId() == id) {
                return element;
            }
        }

        return null;
    }

    public int getBookMarkSize() {
        return this._bookmarks.size();
    }

    public void addBookMark(L1BookMark book) {
        this._bookmarks.add(book);
    }

    public void removeBookMark(L1BookMark book) {
        this._bookmarks.remove(book);
    }

    public L1ItemInstance getWeapon() {
        return this._weapon;
    }

    public void setWeapon(L1ItemInstance weapon) {
        this._weapon = weapon;
    }

    public L1Quest getQuest() {
        return this._quest;
    }

    public boolean isCrown() {
        return (getClassId() == 0) || (getClassId() == 1);
    }

    public boolean isKnight() {
        return (getClassId() == 61) || (getClassId() == 48);
    }

    public boolean isElf() {
        return (getClassId() == 138) || (getClassId() == 37);
    }

    public boolean isWizard() {
        return (getClassId() == 734) || (getClassId() == 1186);
    }

    public boolean isDarkelf() {
        return (getClassId() == 2786) || (getClassId() == 2796);
    }

    public boolean isDragonKnight() {
        return (getClassId() == 6658) || (getClassId() == 6661);
    }

    public boolean isIllusionist() {
        return (getClassId() == 6671) || (getClassId() == 6650);
    }

    public int getClassType()
    {
        switch(getClassId())
        {
            case 0:
            case 1:
                return 1; //Royal
            case 48:
            case 61:
                return 2; //Knight
            case 37:
            case 138:
                return 3; //Elf
            case 734:
            case 1186:
                return 4; //Wizard
            case 2786:
            case 2796:
                return 5; //Darkelf
            case 6658:
            case 6661:
                return 6; //Dragonknight
            case 6650:
            case 6671:
                return 7; //Illusionist
            default:
                return 0;
        }
    }

    public int getClassRole()
    {
        switch(getClassId())
        {
            case 0:
            case 1:
            case 48:
            case 61:
            case 2786:
            case 2796:
            case 6658:
            case 6661:
            case 6650:
            case 6671:
                return 1; //Melee
            case 37:
            case 138:
            case 734:
            case 1186:
                return 2; //Caster
            default:
                return 0;
        }
    }

    public String getAccountName()
    {
        return this._accountName;
    }

    public void setAccountName(String s) {
        this._accountName = s;
    }

    public short getBaseMaxHp()
    {
        return this._baseMaxHp;
    }

    public void addBaseMaxHp(short i) {
        i = (short)(i + this._baseMaxHp);
        if (i >= 32767) {
            i = 32767;
        }
        else if (i < 1) {
            i = 1;
        }
        addMaxHp(i - this._baseMaxHp);
        this._baseMaxHp = i;
    }

    public short getBaseMaxMp()
    {
        return this._baseMaxMp;
    }

    public void addBaseMaxMp(short i) {
        i = (short)(i + this._baseMaxMp);
        if (i >= 32767) {
            i = 32767;
        }
        else if (i < 0) {
            i = 0;
        }
        addMaxMp(i - this._baseMaxMp);
        this._baseMaxMp = i;
    }

    public int getBaseAc()
    {
        return this._baseAc;
    }

    public int getOriginalAc()
    {
        return this._originalAc;
    }

    public byte getBaseStr()
    {
        return this._baseStr;
    }

    public void addBaseStr(byte i) {
        i = (byte)(i + this._baseStr);
        if (i >= 127) {
            i = 127;
        }
        else if (i < 1) {
            i = 1;
        }
        addStr((byte)(i - this._baseStr));
        this._baseStr = i;
    }

    public byte getBaseCon()
    {
        return this._baseCon;
    }

    public void addBaseCon(byte i) {
        i = (byte)(i + this._baseCon);
        if (i >= 127) {
            i = 127;
        }
        else if (i < 1) {
            i = 1;
        }
        addCon((byte)(i - this._baseCon));
        this._baseCon = i;
    }

    public byte getBaseDex()
    {
        return this._baseDex;
    }

    public void addBaseDex(byte i) {
        i = (byte)(i + this._baseDex);
        if (i >= 127) {
            i = 127;
        }
        else if (i < 1) {
            i = 1;
        }
        addDex((byte)(i - this._baseDex));
        this._baseDex = i;
    }

    public byte getBaseCha()
    {
        return this._baseCha;
    }

    public void addBaseCha(byte i) {
        i = (byte)(i + this._baseCha);
        if (i >= 127) {
            i = 127;
        }
        else if (i < 1) {
            i = 1;
        }
        addCha((byte)(i - this._baseCha));
        this._baseCha = i;
    }

    public byte getBaseInt()
    {
        return this._baseInt;
    }

    public void addBaseInt(byte i) {
        i = (byte)(i + this._baseInt);
        if (i >= 127) {
            i = 127;
        }
        else if (i < 1) {
            i = 1;
        }
        addInt((byte)(i - this._baseInt));
        this._baseInt = i;
    }

    public byte getBaseWis()
    {
        return this._baseWis;
    }

    public void addBaseWis(byte i) {
        i = (byte)(i + this._baseWis);
        if (i >= 127) {
            i = 127;
        }
        else if (i < 1) {
            i = 1;
        }
        addWis((byte)(i - this._baseWis));
        this._baseWis = i;
    }

    public int getOriginalStr()
    {
        return this._originalStr;
    }

    public void setOriginalStr(int i) {
        this._originalStr = i;
    }

    public int getOriginalCon()
    {
        return this._originalCon;
    }

    public void setOriginalCon(int i) {
        this._originalCon = i;
    }

    public int getOriginalDex()
    {
        return this._originalDex;
    }

    public void setOriginalDex(int i) {
        this._originalDex = i;
    }

    public int getOriginalCha()
    {
        return this._originalCha;
    }

    public void setOriginalCha(int i) {
        this._originalCha = i;
    }

    public int getOriginalInt()
    {
        return this._originalInt;
    }

    public void setOriginalInt(int i) {
        this._originalInt = i;
    }

    public int getOriginalWis()
    {
        return this._originalWis;
    }

    public void setOriginalWis(int i) {
        this._originalWis = i;
    }

    public int getOriginalDmgup()
    {
        return this._originalDmgup;
    }

    public int getOriginalBowDmgup()
    {
        return this._originalBowDmgup;
    }

    public int getOriginalHitup()
    {
        return this._originalHitup;
    }

    public int getOriginalBowHitup()
    {
        return this._originalBowHitup;
    }

    public int getOriginalMr()
    {
        return this._originalMr;
    }

    public int getOriginalMagicHit()
    {
        return this._originalMagicHit;
    }

    public int getOriginalMagicCritical()
    {
        return this._originalMagicCritical;
    }

    public int getOriginalMagicConsumeReduction()
    {
        return this._originalMagicConsumeReduction;
    }

    public int getOriginalMagicDamage()
    {
        return this._originalMagicDamage;
    }

    public int getOriginalHpup()
    {
        return this._originalHpup;
    }

    public int getOriginalMpup()
    {
        return this._originalMpup;
    }

    public int getBaseDmgup()
    {
        return this._baseDmgup;
    }

    public int getBaseBowDmgup()
    {
        return this._baseBowDmgup;
    }

    public int getBaseHitup()
    {
        return this._baseHitup;
    }

    public int getBaseBowHitup()
    {
        return this._baseBowHitup;
    }

    public int getBaseMr()
    {
        return this._baseMr;
    }

    public int getAdvenHp()
    {
        return this._advenHp;
    }

    public void setAdvenHp(int i) {
        this._advenHp = i;
    }

    public int getAdvenMp()
    {
        return this._advenMp;
    }

    public void setAdvenMp(int i) {
        this._advenMp = i;
    }

    public int getHighLevel()
    {
        return this._highLevel;
    }

    public void setHighLevel(int i) {
        this._highLevel = i;
    }

    public int getBonusStats()
    {
        return this._bonusStats;
    }

    public void setBonusStats(int i) {
        this._bonusStats = i;
    }

    public int getElixirStats()
    {
        return this._elixirStats;
    }

    public void setElixirStats(int i) {
        this._elixirStats = i;
    }

    public int getElfAttr()
    {
        return this._elfAttr;
    }
    /*
    fire 2
    water 4
    air 8
    earth 1
     */
    public void setElfAttr(int i) {
        this._elfAttr = i;
    }

    public int getExpRes()
    {
        return this._expRes;
    }

    public void setExpRes(int i) {
        this._expRes = i;
    }

    public int getPartnerId()
    {
        return this._partnerId;
    }

    public void setPartnerId(int i) {
        this._partnerId = i;
    }

    public int getOnlineStatus()
    {
        return this._onlineStatus;
    }

    public void setOnlineStatus(int i) {
        this._onlineStatus = i;
    }

    public int getHomeTownId()
    {
        return this._homeTownId;
    }

    public void setHomeTownId(int i) {
        this._homeTownId = i;
    }

    public int getContribution()
    {
        return this._contribution;
    }

    public void setContribution(int i) {
        this._contribution = i;
    }

    public int getPay()
    {
        return this._pay;
    }

    public void setPay(int i) {
        this._pay = i;
    }

    public int getHellTime()
    {
        return this._hellTime;
    }

    public void setHellTime(int i) {
        this._hellTime = i;
    }

    public boolean isBanned()
    {
        return this._banned;
    }

    public void setBanned(boolean flag) {
        this._banned = flag;
    }

    public L1EquipmentSlot getEquipSlot() {
        return this._equipSlot;
    }

    public static L1PcInstance load(String charName) {
        L1PcInstance result = null;
        try {
            result = CharacterTable.getInstance().loadCharacter(charName);
        }
        catch (Exception e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        }
        return result;
    }

    public void save()
            throws Exception
    {
        if (isGhost()) {
            return;
        }
        if (isInCharReset()) {
            return;
        }

        CharacterTable.getInstance().storeCharacter(this);
    }

    public void saveInventory()
    {
        for (L1ItemInstance item : getInventory().getItems()) {
            getInventory().saveItem(item, item.getRecordingColumns());
            getInventory().saveEnchantAccessory(item, item.getRecordingColumnsEnchantAccessory());
        }
    }

    public void setRegenState(int state)
    {
        this._mpRegen.setState(state);
        this._hpRegen.setState(state);
    }

    public double getMaxWeight() {
        int str = getStr();
        int con = getCon();
        double maxWeight = 150.0D * Math.floor(0.6D * str + 0.4D * con + 1.0D);

        double weightReductionByArmor = getWeightReduction();
        weightReductionByArmor /= 100.0D;

        double weightReductionByDoll = 0.0D;
        weightReductionByDoll += L1MagicDoll.getWeightReductionByDoll(this);
        weightReductionByDoll /= 100.0D;

        int weightReductionByMagic = 0;
        if (hasSkillEffect(14)) {
            weightReductionByMagic = 180;
        }

        double originalWeightReduction = 0.0D;
        originalWeightReduction += 0.04D * (getOriginalStrWeightReduction() + getOriginalConWeightReduction());

        double weightReduction = 1.0D + weightReductionByArmor +
                weightReductionByDoll + originalWeightReduction;

        maxWeight *= weightReduction;

        maxWeight += weightReductionByMagic;

        maxWeight *= Config.RATE_WEIGHT_LIMIT;

        return maxWeight;
    }

    public boolean isRibrave() {
        return hasSkillEffect(1017);
    }

    public boolean isThirdSpeed() {
        return hasSkillEffect(1027);
    }

    public boolean isWindShackle() {
        return hasSkillEffect(167);
    }

    public boolean isInvisDelay()
    {
        return this.invisDelayCounter > 0;
    }

    public void addInvisDelayCounter(int counter)
    {
        synchronized (this._invisTimerMonitor) {
            this.invisDelayCounter += counter;
        }
    }

    public void beginInvisTimer()
    {
        addInvisDelayCounter(1);
        GeneralThreadPool.getInstance().pcSchedule(new L1PcInvisDelay(getId()), 3000L);
    }

    public synchronized void addExp(int exp) {
        this._exp += exp;
        if (this._exp > 1859065562)
            this._exp = 1859065562;
    }

    public synchronized void addContribution(int contribution)
    {
        this._contribution += contribution;
    }

    public void beginExpMonitor() {
        this._expMonitorFuture = GeneralThreadPool.getInstance().pcScheduleAtFixedRate(new L1PcExpMonitor(getId()), 0L, 500L);
    }

    private void levelUp(int gap) {
        resetLevel();

        if ((getLevel() == 99) && (Config.ALT_REVIVAL_POTION)) {
            try {
                L1Item l1item = ItemTable.getInstance().getTemplate(43000);
                if (l1item != null) {
                    getInventory().storeItem(43000, 1);
                    sendPackets(new S_ServerMessage(403, l1item.getName()));
                }
                else {
                    sendPackets(new S_SystemMessage("返生藥水取得失敗。"));
                }
            }
            catch (Exception e) {
                _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
                sendPackets(new S_SystemMessage("返生藥水取得失敗。"));
            }
        }

        for (int i = 0; i < gap; i++) {
            short randomHp = CalcStat.calcStatHp(getType(), getBaseMaxHp(), getBaseCon(), getOriginalHpup(),getOriginalCon());
            short randomMp = CalcStat.calcStatMp(getType(), getBaseWis(), getOriginalWis());
            //System.out.println("Adding HP: " + randomHp);
            addBaseMaxHp(randomHp);
            addBaseMaxMp(randomMp);
        }
        resetBaseHitup();
        resetBaseDmgup();
        resetBaseAc();
        resetBaseMr();
        if (getLevel() > getHighLevel()) {
            setHighLevel(getLevel());
        }

        try
        {
            save();
        }
        catch (Exception e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        }

        if ((getLevel() >= 51) && (getLevel() - 50 > getBonusStats()) &&
                (getBaseStr() + getBaseDex() + getBaseCon() + getBaseInt() + getBaseWis() + getBaseCha() < 210)) {
            sendPackets(new S_bonusstats(getId(), 1));
        }

        setCurrentHp(getMaxHp());
        setCurrentMp(getMaxMp());

        sendPackets(new S_OwnCharStatus(this));

        if ((getMapId() == 2005) || (getMapId() == 86)) {
            if (getLevel() >= 13) {
                if (getQuest().get_step(300) != 255) {
                    getQuest().set_step(300, 255);
                }
                L1Teleport.teleport(this, 33084, 33391, (short)4, 5, true);
            }
        } else if (getLevel() >= 52) {
            if (getMapId() == 777)
                L1Teleport.teleport(this, 34043, 32184, (short)4, 5, true);
            else if ((getMapId() == 778) || (getMapId() == 779)) {
                L1Teleport.teleport(this, 32608, 33178, (short)4, 5, true);
            }

        }

        checkNoviceType();
    }

    //Function to determine which is the largest stat - [Legends]
    public String getPrimaryStat() {
        Integer _str = this.getOriginalStr();
        Integer _dex = this.getOriginalDex();
        Integer _int = this.getOriginalInt();

        if(_str > _dex && _str > _int)
        {
            return "Str";
        }

        if(_dex > _str && _dex > _int)
        {
            return "Dex";
        }

        if(_int > _dex && _int > _str)
        {
            return "Int";
        }

        if(_str == _dex && _str > _int)
        {
            return "Str";
        }
        if(_str == _int && _str > _dex)
        {
            return "Str";
        }

        if(_int == _dex && _int > _str)
        {
            return "Int";
        }

        if(_str == _int && _str == _dex)
        {
            return "Str";
        }
        return "Unknown";

    }


    private void levelDown(int gap) {
        resetLevel();

        for (int i = 0; i > gap; i--)
        {
            short randomHp = CalcStat.calcStatHp(getType(), 0, getBaseCon(), getOriginalHpup(),getOriginalCon());
            short randomMp = CalcStat.calcStatMp(getType(), getBaseWis(), getOriginalWis());
            addBaseMaxHp((short)-randomHp);
            addBaseMaxMp((short)-randomMp);
        }
        resetBaseHitup();
        resetBaseDmgup();
        resetBaseAc();
        resetBaseMr();
        if ((Config.LEVEL_DOWN_RANGE != 0) &&
                (getHighLevel() - getLevel() >= Config.LEVEL_DOWN_RANGE)) {
            sendPackets(new S_ServerMessage(64));
            sendPackets(new S_Disconnect());

        }

        try
        {
            save();
        }
        catch (Exception e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        }
        sendPackets(new S_OwnCharStatus(this));

        checkNoviceType();
    }

    public void beginGameTimeCarrier() {
        new L1GameTimeCarrier(this).start();
    }

    public boolean isGhost()
    {
        return this._ghost;
    }

    private void setGhost(boolean flag) {
        this._ghost = flag;
    }

    public boolean isGhostCanTalk()
    {
        return this._ghostCanTalk;
    }

    private void setGhostCanTalk(boolean flag) {
        this._ghostCanTalk = flag;
    }

    public boolean isReserveGhost()
    {
        return this._isReserveGhost;
    }

    private void setReserveGhost(boolean flag) {
        this._isReserveGhost = flag;
    }

    public void beginGhost(int locx, int locy, short mapid, boolean canTalk) {
        beginGhost(locx, locy, mapid, canTalk, 0);
    }

    public void beginGhost(int locx, int locy, short mapid, boolean canTalk, int sec) {
        if (isGhost()) {
            return;
        }
        setGhost(true);
        this._ghostSaveLocX = getX();
        this._ghostSaveLocY = getY();
        this._ghostSaveMapId = getMapId();
        this._ghostSaveHeading = getHeading();
        setGhostCanTalk(canTalk);
        L1Teleport.teleport(this, locx, locy, mapid, 5, true);
        if (sec > 0)
            this._ghostFuture = GeneralThreadPool.getInstance().pcSchedule(new L1PcGhostMonitor(getId()), sec * 1000);
    }

    public void makeReadyEndGhost()
    {
        setReserveGhost(true);
        L1Teleport.teleport(this, this._ghostSaveLocX, this._ghostSaveLocY, this._ghostSaveMapId, this._ghostSaveHeading, true);
    }

    public void endGhost() {
        setGhost(false);
        setGhostCanTalk(true);
        setReserveGhost(false);
    }

    public void beginHell(boolean isFirst)
    {
        if (getMapId() != 666) {
            int locx = 32701;
            int locy = 32777;
            short mapid = 666;
            L1Teleport.teleport(this, locx, locy, mapid, 5, false);
        }

        if (isFirst) {
            if (get_PKcount() <= 10) {
                setHellTime(300);
            }
            else {
                setHellTime(300 * (get_PKcount() - 10) + 300);
            }

            sendPackets(new S_BlueMessage(552, String.valueOf(get_PKcount()), String.valueOf(getHellTime() / 60)));
        }
        else
        {
            sendPackets(new S_BlueMessage(637, String.valueOf(getHellTime())));
        }
        if (this._hellFuture == null)
            this._hellFuture = GeneralThreadPool.getInstance().pcScheduleAtFixedRate(new L1PcHellMonitor(getId()), 0L, 1000L);
    }

    public void endHell()
    {
        if (this._hellFuture != null) {
            this._hellFuture.cancel(false);
            this._hellFuture = null;
        }

        int[] loc = L1TownLocation.getGetBackLoc(4);
        L1Teleport.teleport(this, loc[0], loc[1], (short)loc[2], 5, true);
        try {
            save();
        }
        catch (Exception localException)
        {
        }
    }

    public void setPoisonEffect(int effectId)
    {
        sendPackets(new S_Poison(getId(), effectId));

        if ((!isGmInvis()) && (!isGhost()) && (!isInvisble())) {
            broadcastPacket(new S_Poison(getId(), effectId));
        }
        if ((!isGmInvis()) && (!isGhost()))
            if (isInvisble()) {
                broadcastPacketForFindInvis(new S_Poison(getId(), effectId), true);
            }
            else
                broadcastPacket(new S_Poison(getId(), effectId));
    }

    public void healHp(int pt)
    {
        super.healHp(pt);

        sendPackets(new S_HPUpdate(this));
    }

    public int getKarma()
    {
        return this._karma.get();
    }

    public void setKarma(int i)
    {
        this._karma.set(i);
    }

    public void addKarma(int i) {
        synchronized (this._karma) {
            this._karma.add(i);
        }
    }

    public int getKarmaLevel() {
        return this._karma.getLevel();
    }

    public int getKarmaPercent() {
        return this._karma.getPercent();
    }

    public Timestamp getLastPk()
    {
        return this._lastPk;
    }

    public void setLastPk(Timestamp time)
    {
        this._lastPk = time;
    }

    public void setLastPk()
    {
        this._lastPk = new Timestamp(System.currentTimeMillis());
    }

    public boolean isWanted()
    {
        if (this._lastPk == null) {
            return false;
        }
        if (System.currentTimeMillis() - this._lastPk.getTime() > 86400000L) {
            setLastPk(null);
            return false;
        }
        return true;
    }

    public Timestamp getLastPkForElf()
    {
        return this._lastPkForElf;
    }

    public void setLastPkForElf(Timestamp time) {
        this._lastPkForElf = time;
    }

    public void setLastPkForElf() {
        this._lastPkForElf = new Timestamp(System.currentTimeMillis());
    }

    public boolean isWantedForElf() {
        if (this._lastPkForElf == null) {
            return false;
        }
        if (System.currentTimeMillis() - this._lastPkForElf.getTime() > 86400000L) {
            setLastPkForElf(null);
            return false;
        }
        return true;
    }

    public Timestamp getDeleteTime()
    {
        return this._deleteTime;
    }

    public void setDeleteTime(Timestamp time) {
        this._deleteTime = time;
    }

    public int getMagicLevel()
    {
        return getClassFeature().getMagicLevel(getLevel());
    }

    public int getWeightReduction()
    {
        return this._weightReduction;
    }

    public void addWeightReduction(int i) {
        this._weightReduction += i;
    }

    public int getOriginalStrWeightReduction()
    {
        return this._originalStrWeightReduction;
    }

    public int getOriginalConWeightReduction()
    {
        return this._originalConWeightReduction;
    }

    public int getHasteItemEquipped()
    {
        return this._hasteItemEquipped;
    }

    public void addHasteItemEquipped(int i) {
        this._hasteItemEquipped += i;
    }

    public void removeHasteSkillEffect() {
        if (hasSkillEffect(29)) {
            removeSkillEffect(29);
        }
        if (hasSkillEffect(76)) {
            removeSkillEffect(76);
        }
        if (hasSkillEffect(152)) {
            removeSkillEffect(152);
        }
        if (hasSkillEffect(43)) {
            removeSkillEffect(43);
        }
        if (hasSkillEffect(54)) {
            removeSkillEffect(54);
        }
        if (hasSkillEffect(1001))
            removeSkillEffect(1001);
    }

    public int getDamageReductionByArmor()
    {
        return this._damageReductionByArmor;
    }

    public void addDamageReductionByArmor(int i) {
        this._damageReductionByArmor += i;
    }

    public int getHitModifierByArmor()
    {
        return this._hitModifierByArmor;
    }

    public void addHitModifierByArmor(int i) {
        this._hitModifierByArmor += i;
    }

    public int getDmgModifierByArmor()
    {
        return this._dmgModifierByArmor;
    }

    public void addDmgModifierByArmor(int i) {
        this._dmgModifierByArmor += i;
    }

    public int getBowHitModifierByArmor()
    {
        return this._bowHitModifierByArmor;
    }

    public void addBowHitModifierByArmor(int i) {
        this._bowHitModifierByArmor += i;
    }

    public int getBowDmgModifierByArmor()
    {
        return this._bowDmgModifierByArmor;
    }

    public void addBowDmgModifierByArmor(int i) {
        this._bowDmgModifierByArmor += i;
    }

    private void setGresValid(boolean valid)
    {
        this._gresValid = valid;
    }

    public boolean isGresValid() {
        return this._gresValid;
    }

    public long getFishingTime()
    {
        return this._fishingTime;
    }

    public void setFishingTime(long i) {
        this._fishingTime = i;
    }

    public boolean isFishing()
    {
        return this._isFishing;
    }

    public void setFishing(boolean flag) {
        this._isFishing = flag;
    }

    public boolean isFishingReady()
    {
        return this._isFishingReady;
    }

    public void setFishingReady(boolean flag) {
        this._isFishingReady = flag;
    }

    public int getCookingId()
    {
        return this._cookingId;
    }

    public void setCookingId(int i) {
        this._cookingId = i;
    }

    public int getDessertId()
    {
        return this._dessertId;
    }

    public void setDessertId(int i) {
        this._dessertId = i;
    }

    public void resetBaseDmgup()
    {
        int newBaseDmgup = 0;
        int newBaseBowDmgup = 0;
        if (isKnight()) {
            newBaseDmgup = getLevel() / 10;
            newBaseBowDmgup = 0;
        }
        else if (isCrown())
        {
            newBaseDmgup = getLevel() / 15;
            newBaseBowDmgup = 0;
        }
        else if ((isDarkelf()) || (isDragonKnight()))
        {
            newBaseDmgup = getLevel() / 8;
            newBaseDmgup += 2;
            newBaseBowDmgup = 0;
        }
        else if (isElf()) {
            newBaseDmgup = 0;
            newBaseBowDmgup = getLevel() / 10;
        }
        addDmgup(newBaseDmgup - this._baseDmgup);
        addBowDmgup(newBaseBowDmgup - this._baseBowDmgup);
        this._baseDmgup = newBaseDmgup;
        this._baseBowDmgup = newBaseBowDmgup;
    }

    public void resetBaseHitup()
    {
        int newBaseHitup = 0;
        int newBaseBowHitup = 0;
        if (isCrown()) {
            newBaseHitup = getLevel() / 5;
            newBaseBowHitup = getLevel() / 5;
        }
        else if (isKnight()) {
            newBaseHitup = getLevel() / 3;
            newBaseBowHitup = getLevel() / 3;
        }
        else if (isElf()) {
            newBaseHitup = getLevel() / 5;
            newBaseBowHitup = getLevel() / 5;
        }
        else if (isDarkelf()) {
            newBaseHitup = getLevel() / 2;
            newBaseBowHitup = getLevel() / 2;
        }
        else if (isDragonKnight()) {
            newBaseHitup = getLevel() / 3;
            newBaseBowHitup = getLevel() / 3;
        }
        else if (isIllusionist()) {
            newBaseHitup = getLevel() / 5;
            newBaseBowHitup = getLevel() / 5;
        }
        addHitup(newBaseHitup - this._baseHitup);
        addBowHitup(newBaseBowHitup - this._baseBowHitup);
        this._baseHitup = newBaseHitup;
        this._baseBowHitup = newBaseBowHitup;
    }

    public void resetBaseAc()
    {
        int newAc = CalcStat.calcAc(getLevel(), getBaseDex());
        addAc(newAc - this._baseAc);
        this._baseAc = newAc;
    }

    public void resetBaseMr()
    {
        int newMr = 0;
        if (isCrown()) {
            newMr = 10;
        }
        else if (isElf()) {
            newMr = 25;
        }
        else if (isWizard()) {
            newMr = 15;
        }
        else if (isDarkelf()) {
            newMr = 10;
        }
        else if (isDragonKnight()) {
            newMr = 18;
        }
        else if (isIllusionist()) {
            newMr = 20;
        }
        newMr += CalcStat.calcStatMr(getWis());
        newMr += getLevel() / 2;
        addMr(newMr - this._baseMr);
        this._baseMr = newMr;
    }

    public void resetLevel()
    {
        setLevel(ExpTable.getLevelByExp(this._exp));

        if (this._hpRegen != null)
            this._hpRegen.updateLevel();
    }

    public void resetOriginalHpup()
    {
        int originalCon = getOriginalCon();
        if (isCrown()) {
            if ((originalCon == 12) || (originalCon == 13)) {
                this._originalHpup = 1;
            }
            else if ((originalCon == 14) || (originalCon == 15)) {
                this._originalHpup = 2;
            }
            else if (originalCon >= 16) {
                this._originalHpup = 3;
            }
            else {
                this._originalHpup = 0;
            }
        }
        else if (isKnight()) {
            if ((originalCon == 15) || (originalCon == 16)) {
                this._originalHpup = 1;
            }
            else if (originalCon >= 17) {
                this._originalHpup = 3;
            }
            else {
                this._originalHpup = 0;
            }
        }
        else if (isElf()) {
            if ((originalCon >= 13) && (originalCon <= 17)) {
                this._originalHpup = 1;
            }
            else if (originalCon == 18) {
                this._originalHpup = 2;
            }
            else {
                this._originalHpup = 0;
            }
        }
        else if (isDarkelf()) {
            if ((originalCon == 10) || (originalCon == 11)) {
                this._originalHpup = 1;
            }
            else if (originalCon >= 12) {
                this._originalHpup = 2;
            }
            else {
                this._originalHpup = 0;
            }
        }
        else if (isWizard()) {
            if ((originalCon == 14) || (originalCon == 15)) {
                this._originalHpup = 1;
            }
            else if (originalCon >= 16) {
                this._originalHpup = 2;
            }
            else {
                this._originalHpup = 0;
            }
        }
        else if (isDragonKnight()) {
            if ((originalCon == 15) || (originalCon == 16)) {
                this._originalHpup = 1;
            }
            else if (originalCon >= 17) {
                this._originalHpup = 3;
            }
            else {
                this._originalHpup = 0;
            }
        }
        else if (isIllusionist())
            if ((originalCon == 13) || (originalCon == 14)) {
                this._originalHpup = 1;
            }
            else if (originalCon >= 15) {
                this._originalHpup = 2;
            }
            else
                this._originalHpup = 0;
    }

    public void resetOriginalMpup()
    {
        int originalWis = getOriginalWis();

        if (isCrown()) {
            if (originalWis >= 16) {
                this._originalMpup = 1;
            }
            else {
                this._originalMpup = 0;
            }
        }
        else if (isKnight()) {
            this._originalMpup = 0;
        }
        else if (isElf()) {
            if ((originalWis >= 14) && (originalWis <= 16)) {
                this._originalMpup = 1;
            }
            else if (originalWis >= 17) {
                this._originalMpup = 2;
            }
            else {
                this._originalMpup = 0;
            }
        }
        else if (isDarkelf()) {
            if (originalWis >= 12) {
                this._originalMpup = 1;
            }
            else {
                this._originalMpup = 0;
            }
        }
        else if (isWizard()) {
            if ((originalWis >= 13) && (originalWis <= 16)) {
                this._originalMpup = 1;
            }
            else if (originalWis >= 17) {
                this._originalMpup = 2;
            }
            else {
                this._originalMpup = 0;
            }
        }
        else if (isDragonKnight()) {
            if ((originalWis >= 13) && (originalWis <= 15)) {
                this._originalMpup = 1;
            }
            else if (originalWis >= 16) {
                this._originalMpup = 2;
            }
            else {
                this._originalMpup = 0;
            }
        }
        else if (isIllusionist())
            if ((originalWis >= 13) && (originalWis <= 15)) {
                this._originalMpup = 1;
            }
            else if (originalWis >= 16) {
                this._originalMpup = 2;
            }
            else
                this._originalMpup = 0;
    }

    public void resetOriginalStrWeightReduction()
    {
        int originalStr = getOriginalStr();
        if (isCrown()) {
            if ((originalStr >= 14) && (originalStr <= 16)) {
                this._originalStrWeightReduction = 1;
            }
            else if ((originalStr >= 17) && (originalStr <= 19)) {
                this._originalStrWeightReduction = 2;
            }
            else if (originalStr == 20) {
                this._originalStrWeightReduction = 3;
            }
            else {
                this._originalStrWeightReduction = 0;
            }
        }
        else if (isKnight()) {
            this._originalStrWeightReduction = 0;
        }
        else if (isElf()) {
            if (originalStr >= 16) {
                this._originalStrWeightReduction = 2;
            }
            else {
                this._originalStrWeightReduction = 0;
            }
        }
        else if (isDarkelf()) {
            if ((originalStr >= 13) && (originalStr <= 15)) {
                this._originalStrWeightReduction = 2;
            }
            else if (originalStr >= 16) {
                this._originalStrWeightReduction = 3;
            }
            else {
                this._originalStrWeightReduction = 0;
            }
        }
        else if (isWizard()) {
            if (originalStr >= 9) {
                this._originalStrWeightReduction = 1;
            }
            else {
                this._originalStrWeightReduction = 0;
            }
        }
        else if (isDragonKnight()) {
            if (originalStr >= 16) {
                this._originalStrWeightReduction = 1;
            }
            else {
                this._originalStrWeightReduction = 0;
            }
        }
        else if (isIllusionist())
            if (originalStr == 18) {
                this._originalStrWeightReduction = 1;
            }
            else
                this._originalStrWeightReduction = 0;
    }

    public void resetOriginalDmgup()
    {
        int originalStr = getOriginalStr();
        if (isCrown()) {
            if ((originalStr >= 15) && (originalStr <= 17)) {
                this._originalDmgup = 1;
            }
            else if (originalStr >= 18) {
                this._originalDmgup = 2;
            }
            else {
                this._originalDmgup = 0;
            }
        }
        else if (isKnight()) {
            if ((originalStr == 18) || (originalStr == 19)) {
                this._originalDmgup = 2;
            }
            else if (originalStr == 20) {
                this._originalDmgup = 4;
            }
            else {
                this._originalDmgup = 0;
            }
        }
        else if (isElf()) {
            if ((originalStr == 12) || (originalStr == 13)) {
                this._originalDmgup = 1;
            }
            else if (originalStr >= 14) {
                this._originalDmgup = 2;
            }
            else {
                this._originalDmgup = 0;
            }
        }
        else if (isDarkelf()) {
            if ((originalStr >= 14) && (originalStr <= 17)) {
                this._originalDmgup = 1;
            }
            else if (originalStr == 18) {
                this._originalDmgup = 2;
            }
            else {
                this._originalDmgup = 0;
            }
        }
        else if (isWizard()) {
            if ((originalStr == 10) || (originalStr == 11)) {
                this._originalDmgup = 1;
            }
            else if (originalStr >= 12) {
                this._originalDmgup = 2;
            }
            else {
                this._originalDmgup = 0;
            }
        }
        else if (isDragonKnight()) {
            if ((originalStr >= 15) && (originalStr <= 17)) {
                this._originalDmgup = 1;
            }
            else if (originalStr >= 18) {
                this._originalDmgup = 3;
            }
            else {
                this._originalDmgup = 0;
            }
        }
        else if (isIllusionist())
            if ((originalStr == 13) || (originalStr == 14)) {
                this._originalDmgup = 1;
            }
            else if (originalStr >= 15) {
                this._originalDmgup = 2;
            }
            else
                this._originalDmgup = 0;
    }

    public void resetOriginalConWeightReduction()
    {
        int originalCon = getOriginalCon();
        if (isCrown()) {
            if (originalCon >= 11) {
                this._originalConWeightReduction = 1;
            }
            else {
                this._originalConWeightReduction = 0;
            }
        }
        else if (isKnight()) {
            if (originalCon >= 15) {
                this._originalConWeightReduction = 1;
            }
            else {
                this._originalConWeightReduction = 0;
            }
        }
        else if (isElf()) {
            if (originalCon >= 15) {
                this._originalConWeightReduction = 2;
            }
            else {
                this._originalConWeightReduction = 0;
            }
        }
        else if (isDarkelf()) {
            if (originalCon >= 9) {
                this._originalConWeightReduction = 1;
            }
            else {
                this._originalConWeightReduction = 0;
            }
        }
        else if (isWizard()) {
            if ((originalCon == 13) || (originalCon == 14)) {
                this._originalConWeightReduction = 1;
            }
            else if (originalCon >= 15) {
                this._originalConWeightReduction = 2;
            }
            else {
                this._originalConWeightReduction = 0;
            }
        }
        else if (isDragonKnight()) {
            this._originalConWeightReduction = 0;
        }
        else if (isIllusionist())
            if (originalCon == 17) {
                this._originalConWeightReduction = 1;
            }
            else if (originalCon == 18) {
                this._originalConWeightReduction = 2;
            }
            else
                this._originalConWeightReduction = 0;
    }

    public void resetOriginalBowDmgup()
    {
        int originalDex = getOriginalDex();
        if (isCrown()) {
            if (originalDex >= 13) {
                this._originalBowDmgup = 1;
            }
            else {
                this._originalBowDmgup = 0;
            }
        }
        else if (isKnight()) {
            this._originalBowDmgup = 0;
        }
        else if (isElf()) {
            if ((originalDex >= 14) && (originalDex <= 16)) {
                this._originalBowDmgup = 2;
            }
            else if (originalDex >= 17) {
                this._originalBowDmgup = 3;
            }
            else {
                this._originalBowDmgup = 0;
            }
        }
        else if (isDarkelf()) {
            if (originalDex == 18) {
                this._originalBowDmgup = 2;
            }
            else {
                this._originalBowDmgup = 0;
            }
        }
        else if (isWizard()) {
            this._originalBowDmgup = 0;
        }
        else if (isDragonKnight()) {
            this._originalBowDmgup = 0;
        }
        else if (isIllusionist())
            this._originalBowDmgup = 0;
    }

    public void resetOriginalHitup()
    {
        int originalStr = getOriginalStr();
        if (isCrown()) {
            if ((originalStr >= 16) && (originalStr <= 18)) {
                this._originalHitup = 1;
            }
            else if (originalStr >= 19) {
                this._originalHitup = 2;
            }
            else {
                this._originalHitup = 0;
            }
        }
        else if (isKnight()) {
            if ((originalStr == 17) || (originalStr == 18)) {
                this._originalHitup = 2;
            }
            else if (originalStr >= 19) {
                this._originalHitup = 4;
            }
            else {
                this._originalHitup = 0;
            }
        }
        else if (isElf()) {
            if ((originalStr == 13) || (originalStr == 14)) {
                this._originalHitup = 1;
            }
            else if (originalStr >= 15) {
                this._originalHitup = 2;
            }
            else {
                this._originalHitup = 0;
            }
        }
        else if (isDarkelf()) {
            if ((originalStr >= 15) && (originalStr <= 17)) {
                this._originalHitup = 1;
            }
            else if (originalStr == 18) {
                this._originalHitup = 2;
            }
            else {
                this._originalHitup = 0;
            }
        }
        else if (isWizard()) {
            if ((originalStr == 11) || (originalStr == 12)) {
                this._originalHitup = 1;
            }
            else if (originalStr >= 13) {
                this._originalHitup = 2;
            }
            else {
                this._originalHitup = 0;
            }
        }
        else if (isDragonKnight()) {
            if ((originalStr >= 14) && (originalStr <= 16)) {
                this._originalHitup = 1;
            }
            else if (originalStr >= 17) {
                this._originalHitup = 3;
            }
            else {
                this._originalHitup = 0;
            }
        }
        else if (isIllusionist())
            if ((originalStr == 12) || (originalStr == 13)) {
                this._originalHitup = 1;
            }
            else if ((originalStr == 14) || (originalStr == 15)) {
                this._originalHitup = 2;
            }
            else if (originalStr == 16) {
                this._originalHitup = 3;
            }
            else if (originalStr >= 17) {
                this._originalHitup = 4;
            }
            else
                this._originalHitup = 0;
    }

    public void resetOriginalBowHitup()
    {
        int originalDex = getOriginalDex();
        if (isCrown()) {
            this._originalBowHitup = 0;
        }
        else if (isKnight()) {
            this._originalBowHitup = 0;
        }
        else if (isElf()) {
            if ((originalDex >= 13) && (originalDex <= 15)) {
                this._originalBowHitup = 2;
            }
            else if (originalDex >= 16) {
                this._originalBowHitup = 3;
            }
            else {
                this._originalBowHitup = 0;
            }
        }
        else if (isDarkelf()) {
            if (originalDex == 17) {
                this._originalBowHitup = 1;
            }
            else if (originalDex == 18) {
                this._originalBowHitup = 2;
            }
            else {
                this._originalBowHitup = 0;
            }
        }
        else if (isWizard()) {
            this._originalBowHitup = 0;
        }
        else if (isDragonKnight()) {
            this._originalBowHitup = 0;
        }
        else if (isIllusionist())
            this._originalBowHitup = 0;
    }

    public void resetOriginalMr()
    {
        int originalWis = getOriginalWis();
        if (isCrown()) {
            if ((originalWis == 12) || (originalWis == 13)) {
                this._originalMr = 1;
            }
            else if (originalWis >= 14) {
                this._originalMr = 2;
            }
            else {
                this._originalMr = 0;
            }
        }
        else if (isKnight()) {
            if ((originalWis == 10) || (originalWis == 11)) {
                this._originalMr = 1;
            }
            else if (originalWis >= 12) {
                this._originalMr = 2;
            }
            else {
                this._originalMr = 0;
            }
        }
        else if (isElf()) {
            if ((originalWis >= 13) && (originalWis <= 15)) {
                this._originalMr = 1;
            }
            else if (originalWis >= 16) {
                this._originalMr = 2;
            }
            else {
                this._originalMr = 0;
            }
        }
        else if (isDarkelf()) {
            if ((originalWis >= 11) && (originalWis <= 13)) {
                this._originalMr = 1;
            }
            else if (originalWis == 14) {
                this._originalMr = 2;
            }
            else if (originalWis == 15) {
                this._originalMr = 3;
            }
            else if (originalWis >= 16) {
                this._originalMr = 4;
            }
            else {
                this._originalMr = 0;
            }
        }
        else if (isWizard()) {
            if (originalWis >= 15) {
                this._originalMr = 1;
            }
            else {
                this._originalMr = 0;
            }
        }
        else if (isDragonKnight()) {
            if (originalWis >= 14) {
                this._originalMr = 2;
            }
            else {
                this._originalMr = 0;
            }
        }
        else if (isIllusionist()) {
            if ((originalWis >= 15) && (originalWis <= 17)) {
                this._originalMr = 2;
            }
            else if (originalWis == 18) {
                this._originalMr = 4;
            }
            else {
                this._originalMr = 0;
            }
        }

        addMr(this._originalMr);
    }

    public void resetOriginalMagicHit() {
        int originalInt = getOriginalInt();
        if (isCrown()) {
            if ((originalInt == 12) || (originalInt == 13)) {
                this._originalMagicHit = 1;
            }
            else if (originalInt >= 14) {
                this._originalMagicHit = 2;
            }
            else {
                this._originalMagicHit = 0;
            }
        }
        else if (isKnight()) {
            if ((originalInt == 10) || (originalInt == 11)) {
                this._originalMagicHit = 1;
            }
            else if (originalInt == 12) {
                this._originalMagicHit = 2;
            }
            else {
                this._originalMagicHit = 0;
            }
        }
        else if (isElf()) {
            if ((originalInt == 13) || (originalInt == 14)) {
                this._originalMagicHit = 1;
            }
            else if (originalInt >= 15) {
                this._originalMagicHit = 2;
            }
            else {
                this._originalMagicHit = 0;
            }
        }
        else if (isDarkelf()) {
            if ((originalInt == 12) || (originalInt == 13)) {
                this._originalMagicHit = 1;
            }
            else if (originalInt >= 14) {
                this._originalMagicHit = 2;
            }
            else {
                this._originalMagicHit = 0;
            }
        }
        else if (isWizard()) {
            if (originalInt >= 14) {
                this._originalMagicHit = 1;
            }
            else {
                this._originalMagicHit = 0;
            }
        }
        else if (isDragonKnight()) {
            if ((originalInt == 12) || (originalInt == 13)) {
                this._originalMagicHit = 2;
            }
            else if ((originalInt == 14) || (originalInt == 15)) {
                this._originalMagicHit = 3;
            }
            else if (originalInt >= 16) {
                this._originalMagicHit = 4;
            }
            else {
                this._originalMagicHit = 0;
            }
        }
        else if (isIllusionist())
            if (originalInt >= 13) {
                this._originalMagicHit = 1;
            }
            else
                this._originalMagicHit = 0;
    }

    public void resetOriginalMagicCritical()
    {
        int originalInt = getOriginalInt();
        if (isCrown()) {
            this._originalMagicCritical = 0;
        }
        else if (isKnight()) {
            this._originalMagicCritical = 0;
        }
        else if (isElf()) {
            if ((originalInt == 14) || (originalInt == 15)) {
                this._originalMagicCritical = 2;
            }
            else if (originalInt >= 16) {
                this._originalMagicCritical = 4;
            }
            else {
                this._originalMagicCritical = 0;
            }
        }
        else if (isDarkelf()) {
            this._originalMagicCritical = 0;
        }
        else if (isWizard()) {
            if (originalInt == 15) {
                this._originalMagicCritical = 2;
            }
            else if (originalInt == 16) {
                this._originalMagicCritical = 4;
            }
            else if (originalInt == 17) {
                this._originalMagicCritical = 6;
            }
            else if (originalInt == 18) {
                this._originalMagicCritical = 8;
            }
            else {
                this._originalMagicCritical = 0;
            }
        }
        else if (isDragonKnight()) {
            this._originalMagicCritical = 0;
        }
        else if (isIllusionist())
            this._originalMagicCritical = 0;
    }

    public void resetOriginalMagicConsumeReduction()
    {
        int originalInt = getOriginalInt();
        if (isCrown()) {
            if ((originalInt == 11) || (originalInt == 12)) {
                this._originalMagicConsumeReduction = 1;
            }
            else if (originalInt >= 13) {
                this._originalMagicConsumeReduction = 2;
            }
            else {
                this._originalMagicConsumeReduction = 0;
            }
        }
        else if (isKnight()) {
            if ((originalInt == 9) || (originalInt == 10)) {
                this._originalMagicConsumeReduction = 1;
            }
            else if (originalInt >= 11) {
                this._originalMagicConsumeReduction = 2;
            }
            else {
                this._originalMagicConsumeReduction = 0;
            }
        }
        else if (isElf()) {
            this._originalMagicConsumeReduction = 0;
        }
        else if (isDarkelf()) {
            if ((originalInt == 13) || (originalInt == 14)) {
                this._originalMagicConsumeReduction = 1;
            }
            else if (originalInt >= 15) {
                this._originalMagicConsumeReduction = 2;
            }
            else {
                this._originalMagicConsumeReduction = 0;
            }
        }
        else if (isWizard()) {
            this._originalMagicConsumeReduction = 0;
        }
        else if (isDragonKnight()) {
            this._originalMagicConsumeReduction = 0;
        }
        else if (isIllusionist())
            if (originalInt == 14) {
                this._originalMagicConsumeReduction = 1;
            }
            else if (originalInt >= 15) {
                this._originalMagicConsumeReduction = 2;
            }
            else
                this._originalMagicConsumeReduction = 0;
    }

    public void resetOriginalMagicDamage()
    {
        int originalInt = getOriginalInt();
        if (isCrown()) {
            this._originalMagicDamage = 0;
        }
        else if (isKnight()) {
            this._originalMagicDamage = 0;
        }
        else if (isElf()) {
            this._originalMagicDamage = 0;
        }
        else if (isDarkelf()) {
            this._originalMagicDamage = 0;
        }
        else if (isWizard()) {
            if (originalInt >= 13) {
                this._originalMagicDamage = 1;
            }
            else {
                this._originalMagicDamage = 0;
            }
        }
        else if (isDragonKnight()) {
            if ((originalInt == 13) || (originalInt == 14)) {
                this._originalMagicDamage = 1;
            }
            else if ((originalInt == 15) || (originalInt == 16)) {
                this._originalMagicDamage = 2;
            }
            else if (originalInt == 17) {
                this._originalMagicDamage = 3;
            }
            else {
                this._originalMagicDamage = 0;
            }
        }
        else if (isIllusionist())
            if (originalInt == 16) {
                this._originalMagicDamage = 1;
            }
            else if (originalInt == 17) {
                this._originalMagicDamage = 2;
            }
            else
                this._originalMagicDamage = 0;
    }

    public void resetOriginalAc()
    {
        int originalDex = getOriginalDex();
        if (isCrown()) {
            if ((originalDex >= 12) && (originalDex <= 14)) {
                this._originalAc = 1;
            }
            else if ((originalDex == 15) || (originalDex == 16)) {
                this._originalAc = 2;
            }
            else if (originalDex >= 17) {
                this._originalAc = 3;
            }
            else {
                this._originalAc = 0;
            }
        }
        else if (isKnight()) {
            if ((originalDex == 13) || (originalDex == 14)) {
                this._originalAc = 1;
            }
            else if (originalDex >= 15) {
                this._originalAc = 3;
            }
            else {
                this._originalAc = 0;
            }
        }
        else if (isElf()) {
            if ((originalDex >= 15) && (originalDex <= 17)) {
                this._originalAc = 1;
            }
            else if (originalDex == 18) {
                this._originalAc = 2;
            }
            else {
                this._originalAc = 0;
            }
        }
        else if (isDarkelf()) {
            if (originalDex >= 17) {
                this._originalAc = 1;
            }
            else {
                this._originalAc = 0;
            }
        }
        else if (isWizard()) {
            if ((originalDex == 8) || (originalDex == 9)) {
                this._originalAc = 1;
            }
            else if (originalDex >= 10) {
                this._originalAc = 2;
            }
            else {
                this._originalAc = 0;
            }
        }
        else if (isDragonKnight()) {
            if ((originalDex == 12) || (originalDex == 13)) {
                this._originalAc = 1;
            }
            else if (originalDex >= 14) {
                this._originalAc = 2;
            }
            else {
                this._originalAc = 0;
            }
        }
        else if (isIllusionist()) {
            if ((originalDex == 11) || (originalDex == 12)) {
                this._originalAc = 1;
            }
            else if (originalDex >= 13) {
                this._originalAc = 2;
            }
            else {
                this._originalAc = 0;
            }
        }

        addAc(0 - this._originalAc);
    }

    public void resetOriginalEr() {
        int originalDex = getOriginalDex();
        if (isCrown()) {
            if ((originalDex == 14) || (originalDex == 15)) {
                this._originalEr = 1;
            }
            else if ((originalDex == 16) || (originalDex == 17)) {
                this._originalEr = 2;
            }
            else if (originalDex == 18) {
                this._originalEr = 3;
            }
            else {
                this._originalEr = 0;
            }
        }
        else if (isKnight()) {
            if ((originalDex == 14) || (originalDex == 15)) {
                this._originalEr = 1;
            }
            else if (originalDex == 16) {
                this._originalEr = 3;
            }
            else {
                this._originalEr = 0;
            }
        }
        else if (isElf()) {
            this._originalEr = 0;
        }
        else if (isDarkelf()) {
            if (originalDex >= 16) {
                this._originalEr = 2;
            }
            else {
                this._originalEr = 0;
            }
        }
        else if (isWizard()) {
            if ((originalDex == 9) || (originalDex == 10)) {
                this._originalEr = 1;
            }
            else if (originalDex == 11) {
                this._originalEr = 2;
            }
            else {
                this._originalEr = 0;
            }
        }
        else if (isDragonKnight()) {
            if ((originalDex == 13) || (originalDex == 14)) {
                this._originalEr = 1;
            }
            else if (originalDex >= 15) {
                this._originalEr = 2;
            }
            else {
                this._originalEr = 0;
            }
        }
        else if (isIllusionist())
            if ((originalDex == 12) || (originalDex == 13)) {
                this._originalEr = 1;
            }
            else if (originalDex >= 14) {
                this._originalEr = 2;
            }
            else
                this._originalEr = 0;
    }

    public void resetOriginalHpr()
    {
        int originalCon = getOriginalCon();
        if (isCrown()) {
            if ((originalCon == 13) || (originalCon == 14)) {
                this._originalHpr = 1;
            }
            else if ((originalCon == 15) || (originalCon == 16)) {
                this._originalHpr = 2;
            }
            else if (originalCon == 17) {
                this._originalHpr = 3;
            }
            else if (originalCon == 18) {
                this._originalHpr = 4;
            }
            else {
                this._originalHpr = 0;
            }
        }
        else if (isKnight()) {
            if ((originalCon == 16) || (originalCon == 17)) {
                this._originalHpr = 2;
            }
            else if (originalCon == 18) {
                this._originalHpr = 4;
            }
            else {
                this._originalHpr = 0;
            }
        }
        else if (isElf()) {
            if ((originalCon == 14) || (originalCon == 15)) {
                this._originalHpr = 1;
            }
            else if (originalCon == 16) {
                this._originalHpr = 2;
            }
            else if (originalCon >= 17) {
                this._originalHpr = 3;
            }
            else {
                this._originalHpr = 0;
            }
        }
        else if (isDarkelf()) {
            if ((originalCon == 11) || (originalCon == 12)) {
                this._originalHpr = 1;
            }
            else if (originalCon >= 13) {
                this._originalHpr = 2;
            }
            else {
                this._originalHpr = 0;
            }
        }
        else if (isWizard()) {
            if (originalCon == 17) {
                this._originalHpr = 1;
            }
            else if (originalCon == 18) {
                this._originalHpr = 2;
            }
            else {
                this._originalHpr = 0;
            }
        }
        else if (isDragonKnight()) {
            if ((originalCon == 16) || (originalCon == 17)) {
                this._originalHpr = 1;
            }
            else if (originalCon == 18) {
                this._originalHpr = 3;
            }
            else {
                this._originalHpr = 0;
            }
        }
        else if (isIllusionist())
            if ((originalCon == 14) || (originalCon == 15)) {
                this._originalHpr = 1;
            }
            else if (originalCon >= 16) {
                this._originalHpr = 2;
            }
            else
                this._originalHpr = 0;
    }

    public void resetOriginalMpr()
    {
        int originalWis = getOriginalWis();
        if (isCrown()) {
            if ((originalWis == 13) || (originalWis == 14)) {
                this._originalMpr = 1;
            }
            else if (originalWis >= 15) {
                this._originalMpr = 2;
            }
            else {
                this._originalMpr = 0;
            }
        }
        else if (isKnight()) {
            if ((originalWis == 11) || (originalWis == 12)) {
                this._originalMpr = 1;
            }
            else if (originalWis == 13) {
                this._originalMpr = 2;
            }
            else {
                this._originalMpr = 0;
            }
        }
        else if (isElf()) {
            if ((originalWis >= 15) && (originalWis <= 17)) {
                this._originalMpr = 1;
            }
            else if (originalWis == 18) {
                this._originalMpr = 2;
            }
            else {
                this._originalMpr = 0;
            }
        }
        else if (isDarkelf()) {
            if (originalWis >= 13) {
                this._originalMpr = 1;
            }
            else {
                this._originalMpr = 0;
            }
        }
        else if (isWizard()) {
            if ((originalWis == 14) || (originalWis == 15)) {
                this._originalMpr = 1;
            }
            else if ((originalWis == 16) || (originalWis == 17)) {
                this._originalMpr = 2;
            }
            else if (originalWis == 18) {
                this._originalMpr = 3;
            }
            else {
                this._originalMpr = 0;
            }
        }
        else if (isDragonKnight()) {
            if ((originalWis == 15) || (originalWis == 16)) {
                this._originalMpr = 1;
            }
            else if (originalWis >= 17) {
                this._originalMpr = 2;
            }
            else {
                this._originalMpr = 0;
            }
        }
        else if (isIllusionist())
            if ((originalWis >= 14) && (originalWis <= 16)) {
                this._originalMpr = 1;
            }
            else if (originalWis >= 17) {
                this._originalMpr = 2;
            }
            else
                this._originalMpr = 0;
    }

    public void refresh()
    {
        resetLevel();
        resetBaseHitup();
        resetBaseDmgup();
        resetBaseMr();
        resetBaseAc();
        resetOriginalHpup();
        resetOriginalMpup();
        resetOriginalDmgup();
        resetOriginalBowDmgup();
        resetOriginalHitup();
        resetOriginalBowHitup();
        resetOriginalMr();
        resetOriginalMagicHit();
        resetOriginalMagicCritical();
        resetOriginalMagicConsumeReduction();
        resetOriginalMagicDamage();
        resetOriginalAc();
        resetOriginalEr();
        resetOriginalHpr();
        resetOriginalMpr();
        resetOriginalStrWeightReduction();
        resetOriginalConWeightReduction();
    }

    public void startRefreshParty()
    {
        int INTERVAL = 25000;

        if (!this._rpActive)
        {
            this._rp = new L1PartyRefresh(this);

            _regenTimer.scheduleAtFixedRate(this._rp, 25000L, 25000L);

            this._rpActive = true;
        }
    }

    public void stopRefreshParty()
    {
        if (this._rpActive)
        {
            this._rp.cancel();

            this._rp = null;

            this._rpActive = false;
        }
    }

    public L1ExcludingList getExcludingList()
    {
        return this._excludingList;
    }

    public AcceleratorChecker getAcceleratorChecker()
    {
        return this._acceleratorChecker;
    }

    public void setFoeSlayer(boolean FoeSlayer)
    {
        this._FoeSlayer = FoeSlayer;
    }

    public boolean isFoeSlayer() {
        return this._FoeSlayer;
    }

    public int getTeleportX()
    {
        return this._teleportX;
    }

    public void setTeleportX(int i) {
        this._teleportX = i;
    }

    public int getTeleportY()
    {
        return this._teleportY;
    }

    public void setTeleportY(int i) {
        this._teleportY = i;
    }

    public short getTeleportMapId()
    {
        return this._teleportMapId;
    }

    public void setTeleportMapId(short i) {
        this._teleportMapId = i;
    }

    public int getTeleportHeading()
    {
        return this._teleportHeading;
    }

    public void setTeleportHeading(int i) {
        this._teleportHeading = i;
    }

    public int getTempCharGfxAtDead()
    {
        return this._tempCharGfxAtDead;
    }

    public void setTempCharGfxAtDead(int i) {
        this._tempCharGfxAtDead = i;
    }

    public boolean isCanWhisper()
    {
        return this._isCanWhisper;
    }

    public void setCanWhisper(boolean flag) {
        this._isCanWhisper = flag;
    }

    public boolean showTradeChat()
    {
        return this._showTradeChat;
    }
    public boolean showWorldChat() {
        return this._showWorldChat;
    }

    public boolean isShowTradeChat()
    {
        return this._isShowTradeChat;
    }

    public void setShowTradeChat(boolean flag) {
        this._isShowTradeChat = flag;
    }

    public boolean isShowClanChat()
    {
        return this._isShowClanChat;
    }

    public void setShowClanChat(boolean flag) {
        this._isShowClanChat = flag;
    }

    public boolean isShowPartyChat()
    {
        return this._isShowPartyChat;
    }

    public void setShowPartyChat(boolean flag) {
        this._isShowPartyChat = flag;
    }

    public boolean isShowWorldChat()
    {
        return this._isShowWorldChat;
    }

    public void setShowWorldChat(boolean flag) {
        this._isShowWorldChat = flag;
    }

    public int getFightId()
    {
        return this._fightId;
    }

    public void setFightId(int i) {
        this._fightId = i;
    }

    public int getFishX()
    {
        return this._fishX;
    }

    public void setFishX(int i) {
        this._fishX = i;
    }

    public int getFishY()
    {
        return this._fishY;
    }

    public void setFishY(int i) {
        this._fishY = i;
    }

    public void checkChatInterval()
    {
        long nowChatTimeInMillis = System.currentTimeMillis();
        if (this._chatCount == 0) {
            this._chatCount = ((byte)(this._chatCount + 1));
            this._oldChatTimeInMillis = nowChatTimeInMillis;
            return;
        }

        long chatInterval = nowChatTimeInMillis - this._oldChatTimeInMillis;
        if (chatInterval > 2000L) {
            this._chatCount = 0;
            this._oldChatTimeInMillis = 0L;
        }
        else {
            if (this._chatCount >= 3) {
                setSkillEffect(1005, 120000);
                sendPackets(new S_SkillIconGFX(36, 120));
                sendPackets(new S_ServerMessage(153));
                this._chatCount = 0;
                this._oldChatTimeInMillis = 0L;
            }
            this._chatCount = ((byte)(this._chatCount + 1));
        }
    }

    public int getCallClanId()
    {
        return this._callClanId;
    }

    public void setCallClanId(int i) {
        this._callClanId = i;
    }

    public int getCallClanHeading()
    {
        return this._callClanHeading;
    }

    public void setCallClanHeading(int i) {
        this._callClanHeading = i;
    }

    public boolean isInCharReset()
    {
        return this._isInCharReset;
    }

    public void setInCharReset(boolean flag) {
        this._isInCharReset = flag;
    }

    public int getTempLevel()
    {
        return this._tempLevel;
    }

    public void setTempLevel(int i) {
        this._tempLevel = i;
    }

    public int getTempMaxLevel()
    {
        return this._tempMaxLevel;
    }

    public void setTempMaxLevel(int i) {
        this._tempMaxLevel = i;
    }

    public int getAwakeSkillId()
    {
        return this._awakeSkillId;
    }

    public void setAwakeSkillId(int i) {
        this._awakeSkillId = i;
    }

    public void setSummonMonster(boolean SummonMonster)
    {
        this._isSummonMonster = SummonMonster;
    }

    public boolean isSummonMonster() {
        return this._isSummonMonster;
    }

    public void setShapeChange(boolean isShapeChange)
    {
        this._isShapeChange = isShapeChange;
    }

    public boolean isShapeChange() {
        return this._isShapeChange;
    }

    public void setPartyType(int type) {
        this._partyType = type;
    }

    public int getPartyType() {
        return this._partyType;
    }

    public void setDropMessages(boolean dropMessages)
    {
        this._dropMessages = dropMessages;
    }
    public boolean getDropMessages() { return this._dropMessages; }

    public void setPartyDropMessages(boolean partyDropMessages)
    {
        this._partyDropMessages = partyDropMessages;
    }
    public boolean getPartyDropMessages() { return this._partyDropMessages; }


    public boolean getDmgMessages() {
        return this._dmgMessages;
    }
    public void setDmgMessages(boolean dmgMessages) { this._dmgMessages = dmgMessages; }


    public boolean getDmgRMessages() {
        return this._dmgRMessages;
    }
    public void setDmgRMessages(boolean dmgRMessages) { this._dmgRMessages = dmgRMessages; }

    public boolean getPotionMessages()
    {
        return this._potionMessages;
    }
    public void setPotionMessages(boolean potionMessages) { this._potionMessages = potionMessages; }


    public int getCryTime()
    {
        return this._CryTime;
    }

    public void setCryTime(int time) {
        this._CryTime = time;
    }

    public void startCryOfSurvival()
    {
        int INTERVAL = 60000;
        if (!this._CryOfSurvivalActive) {
            this._CryOfSurvival = new TheCryOfSurvival(this);
            _regenTimer.scheduleAtFixedRate(this._CryOfSurvival, 60000L,
                    60000L);
            this._CryOfSurvivalActive = true;
        }
    }

    public void stopCryOfSurvival()
    {
        if (this._CryOfSurvivalActive) {
            this._CryOfSurvival.cancel();
            this._CryOfSurvival = null;
            this._CryOfSurvivalActive = false;
        }
    }

    public void addEinPoint(int i)
    {
        this._einPoint += i;
        if (this._einPoint < 1) {
            this._einPoint = 0;
        }
        else if (this._einPoint > einMaxPercent) {
            this._einPoint = einMaxPercent;
        }

        sendPackets(new S_SkillIconEinhasad(this._einPoint));
    }

    public void checkEinhasad()
    {
        if (Config_Einhasad.EINHASAD_IS_ACTIVE) {
            setEinLevel();
            if (isMatchEinResult())
                addEinPoint(0);
        }
    }

    public void setEinLevel()
    {
        this._isEinLevel = (getLevel() >= 45);
    }

    public boolean isMatchEinResult()
    {
        if (this._isEinLevel) {
            return isEinZone();
        }
        return false;
    }

    public boolean isEinZone()
    {
        return getMap().isSafetyZone(getLocation());
    }

    public void CalcExpCostEin(int i)
    {
        if (this._einPoint < 1) {
            this._ein_getExp = 0;
        } else {
            this._ein_getExp += i;
            if (this._ein_getExp > 5000) {
                addEinPoint(-this._ein_getExp / 5000);
                this._ein_getExp %= 5000;
            }
        }
    }


    public Timestamp getLastActive() {
        return this._lastActive;
    }

    public void setLastActive() {
        this._lastActive = new Timestamp(System.currentTimeMillis());
    }

    public void setLastActive(Timestamp time) {
        this._lastActive = time;
    }

    public int getEinPoint() {
        return this._einPoint;
    }

    public boolean isEinLevel() {
        return this._isEinLevel;
    }

    public void setKill(int kill)
    {
        this._kill = kill;
    }

    public int getKill() {
        return this._kill;
    }

    public void setDeath(int death) {
        this._death = death;
    }

    public int getDeath() {
        return this._death;
    }

    public void changeFightType(int oldType, int newType)
    {
        switch (oldType) {
            case 1:
                addAc(2);
                addMr(-3);
                sendPackets(new S_Fight(0, 0));
                break;
            case 2:
                addAc(4);
                addMr(-6);
                sendPackets(new S_Fight(1, 0));
                break;
            case 3:
                addAc(6);
                addMr(-9);
                sendPackets(new S_Fight(2, 0));
                break;
            case -1:
                addDmgup(-1);
                addBowDmgup(-1);
                addSp(-1);
                sendPackets(new S_Fight(3, 0));
                break;
            case -2:
                addDmgup(-3);
                addBowDmgup(-3);
                addSp(-2);
                sendPackets(new S_Fight(4, 0));
                break;
            case -3:
                addDmgup(-5);
                addBowDmgup(-5);
                addSp(-3);
                sendPackets(new S_Fight(5, 0));
            case 0:
        }

        switch (newType) {
            case 1:
                addAc(-2);
                addMr(3);
                sendPackets(new S_Fight(0, 1));
                break;
            case 2:
                addAc(-4);
                addMr(6);
                sendPackets(new S_Fight(1, 1));
                break;
            case 3:
                addAc(-6);
                addMr(9);
                sendPackets(new S_Fight(2, 1));
                break;
            case -1:
                addDmgup(1);
                addBowDmgup(1);
                addSp(1);
                sendPackets(new S_Fight(3, 1));
                break;
            case -2:
                addDmgup(3);
                addBowDmgup(3);
                addSp(2);
                sendPackets(new S_Fight(4, 1));
                break;
            case -3:
                addDmgup(5);
                addBowDmgup(5);
                addSp(3);
                sendPackets(new S_Fight(5, 1));
            case 0:
        }
    }

    public void checkNoviceType()
    {
        if (!Config.NOVICE_PROTECTION_IS_ACTIVE) {
            return;
        }

        if (getLevel() > Config.NOVICE_MAX_LEVEL)
        {
            if (hasSkillEffect(8000))
            {
                removeSkillEffect(8000);

                sendPackets(new S_Fight(6, 0));
            }

        }
        else if (!hasSkillEffect(8000))
        {
            setSkillEffect(8000, 0);

            sendPackets(new S_Fight(6, 1));
        }
    }

    private class Death
            implements Runnable
    {
        L1Character _lastAttacker;

        Death(L1Character cha)
        {
            this._lastAttacker = cha;
        }

        public void run()
        {
            L1Character lastAttacker = this._lastAttacker;
            this._lastAttacker = null;
            L1PcInstance.this.setCurrentHp(0);
            L1PcInstance.this.setGresValid(false);

            while (L1PcInstance.this.isTeleport())
                try {
                    Thread.sleep(300L);
                }
                catch (Exception localException)
                {
                }
            L1PcInstance.this.stopHpRegeneration();
            L1PcInstance.this.stopMpRegeneration();
            L1PcInstance.this.stopCryOfSurvival();
            L1PcInstance.this.setCryTime(0);

            int targetobjid = L1PcInstance.this.getId();
            L1PcInstance.this.getMap().setPassable(L1PcInstance.this.getLocation(), true);

            int tempchargfx = 0;
            if (L1PcInstance.this.hasSkillEffect(67)) {
                tempchargfx = L1PcInstance.this.getTempCharGfx();
                L1PcInstance.this.setTempCharGfxAtDead(tempchargfx);
            }
            else {
                L1PcInstance.this.setTempCharGfxAtDead(L1PcInstance.this.getClassId());
            }

            L1SkillUse l1skilluse = new L1SkillUse();
            l1skilluse.handleCommands(L1PcInstance.this, 44, L1PcInstance.this.getId(), L1PcInstance.this.getX(), L1PcInstance.this.getY(), null, 0, 1);

            if (L1PcInstance.this.hasSkillEffect(4007)) {
                L1PcInstance.this.removeSkillEffect(4007);
            }

            if (L1PcInstance.this.hasSkillEffect(3048)) {
                L1PcInstance.this.removeSkillEffect(3048);
            }

            L1PcInstance.this.sendPackets(new S_DoActionGFX(targetobjid, 8));
            L1PcInstance.this.broadcastPacket(new S_DoActionGFX(targetobjid, 8));

            if (lastAttacker != L1PcInstance.this)
            {
                if (L1PcInstance.this.getZoneType() != 0) {
                    L1PcInstance player = null;
                    if ((lastAttacker instanceof L1PcInstance)) {
                        player = (L1PcInstance)lastAttacker;
                    }
                    else if ((lastAttacker instanceof L1PetInstance)) {
                        player = (L1PcInstance)((L1PetInstance)lastAttacker).getMaster();
                    }
                    else if ((lastAttacker instanceof L1SummonInstance)) {
                        player = (L1PcInstance)((L1SummonInstance)lastAttacker).getMaster();
                    }
                    if (player != null)
                    {
                        if (!L1PcInstance.this.isInWarAreaAndWarTime(L1PcInstance.this, player)) {
                            return;
                        }
                    }
                }

                boolean sim_ret = L1PcInstance.this.simWarResult(lastAttacker);
                if (sim_ret) {
                    return;
                }
            }

            if (!L1PcInstance.this.getMap().isEnabledDeathPenalty()) {
                return;
            }

            L1PcInstance fightPc = null;
            if ((lastAttacker instanceof L1PcInstance)) {
                fightPc = (L1PcInstance)lastAttacker;
            }

            boolean isNovice = false;
            if ((L1PcInstance.this.hasSkillEffect(8000)) && (fightPc != null))
            {
                if (fightPc.getLevel() > L1PcInstance.this.getLevel() + Config.NOVICE_PROTECTION_LEVEL_RANGE) {
                    isNovice = true;
                }
            }

            if ((fightPc != null) &&
                    (L1PcInstance.this.getFightId() == fightPc.getId()) && (fightPc.getFightId() == L1PcInstance.this.getId())) {
                L1PcInstance.this.setFightId(0);
                L1PcInstance.this.sendPackets(new S_PacketBox(5, 0, 0));
                fightPc.setFightId(0);
                fightPc.sendPackets(new S_PacketBox(5, 0, 0));
                return;
            }

            if ((lastAttacker instanceof L1GuardInstance)) {
                if (L1PcInstance.this.get_PKcount() > 0) {
                    L1PcInstance.this.set_PKcount(L1PcInstance.this.get_PKcount() - 1);
                }
                L1PcInstance.this.setLastPk(null);
            }
            if ((lastAttacker instanceof L1GuardianInstance)) {
                if (L1PcInstance.this.getPkCountForElf() > 0) {
                    L1PcInstance.this.setPkCountForElf(L1PcInstance.this.getPkCountForElf() - 1);
                }
                L1PcInstance.this.setLastPkForElf(null);
            }

            if (!isNovice)
            {
                int lostRate = (int)(((L1PcInstance.this.getLawful() + 32768.0D) / 1000.0D - 65.0D) * 4.0D);
                if (lostRate < 0) {
                    lostRate *= -1;
                    if (L1PcInstance.this.getLawful() < 0) {
                        lostRate *= 2;
                    }
                    int rnd = Random.nextInt(1000) + 1;
                    if (rnd <= lostRate) {
                        int count = 1;
                        if (L1PcInstance.this.getLawful() <= -30000) {
                            count = Random.nextInt(4) + 1;
                        }
                        else if (L1PcInstance.this.getLawful() <= -20000) {
                            count = Random.nextInt(3) + 1;
                        }
                        else if (L1PcInstance.this.getLawful() <= -10000) {
                            count = Random.nextInt(2) + 1;
                        }
                        else if (L1PcInstance.this.getLawful() < 0) {
                            count = Random.nextInt(1) + 1;
                        }
                        L1PcInstance.this.caoPenaltyResult(count);
                    }
                }
            }

            boolean castle_ret = L1PcInstance.this.castleWarResult();
            if (castle_ret) {
                return;
            }

            if (!L1PcInstance.this.getMap().isEnabledDeathPenalty()) {
                return;
            }

            if (!isNovice) {
                L1PcInstance.this.deathPenalty();
                L1PcInstance.this.setGresValid(true);
            }

            if (L1PcInstance.this.get_PKcount() > 0) {
                L1PcInstance.this.set_PKcount(L1PcInstance.this.get_PKcount() - 1);
            }
            L1PcInstance.this.setLastPk(null);

            L1PcInstance player = null;
            if ((lastAttacker instanceof L1PcInstance)) {
                player = (L1PcInstance)lastAttacker;
                if(player.getLevel() > 49 && L1PcInstance.this.getLevel() <= 49)
                {
                    L1World.getInstance().broadcastServerMessage(player.getName() + " is killing noobs now! Poor " + L1PcInstance.this.getName() + " didn't have a chance.");
                }
                else if(player.getLevel() > 49 && L1PcInstance.this.getLevel() > 49 && player.getLevel() > L1PcInstance.this.getLevel() + 19)
                {
                    L1World.getInstance().broadcastServerMessage(player.getName() + " just owned " + L1PcInstance.this.getName() + " in battle!");
                }
                else
                {
                    L1World.getInstance().broadcastServerMessage(player.getName() + " just owned " + L1PcInstance.this.getName() + " in battle!");
                    player.setKill(player.getKill() + 1);
                    L1PcInstance.this.setDeath(L1PcInstance.this.getDeath() + 1);
                }
            }
            if (player != null) {
                if ((L1PcInstance.this.getLawful() >= 0) && (!L1PcInstance.this.isPinkName())) {
                    boolean isChangePkCount = false;

                    if (player.getLawful() < 30000) {
                        player.set_PKcount(player.get_PKcount() + 1);
                        L1World.getInstance().broadcastServerMessage(player.getName() + " is dominating with pk count now at " +
                                player.get_PKcount() + "!");
                        isChangePkCount = true;
                        if ((player.isElf()) && (L1PcInstance.this.isElf())) {
                            player.setPkCountForElf(player.getPkCountForElf() + 1);
                        }
                    }
                    player.setLastPk();

                    if (player.getLawful() == 32767) {
                        player.setLastPk(null);
                    }
                    if ((player.isElf()) && (L1PcInstance.this.isElf()))
                        player.setLastPkForElf();
                    int lawful;

                    if (player.getLevel() < 50) {
                        lawful = -1 * (int)(Math.pow(player.getLevel(), 2.0D) * 4.0D);
                    }
                    else {
                        lawful = -1 * (int)(Math.pow(player.getLevel(), 3.0D) * 0.08D);
                    }

                    if (player.getLawful() - 1000 < lawful) {
                        lawful = player.getLawful() - 1000;
                    }

                    if (lawful <= -32768) {
                        lawful = -32768;
                    }
                    player.setLawful(lawful);

                    S_Lawful s_lawful = new S_Lawful(player.getId(), player.getLawful());
                    player.sendPackets(s_lawful);
                    player.broadcastPacket(s_lawful);

                    if ((isChangePkCount) && (player.get_PKcount() >= 5) && (player.get_PKcount() < 10))
                    {
                        player.sendPackets(new S_BlueMessage(551, String.valueOf(player.get_PKcount()), "10"));
                    }
                    else if ((isChangePkCount) && (player.get_PKcount() >= 10))
                        player.beginHell(true);
                }
                else
                {
                    L1PcInstance.this.setPinkName(false);
                }
            }
            L1PcInstance.this._pcDeleteTimer = new L1PcDeleteTimer(L1PcInstance.this);
            L1PcInstance.this._pcDeleteTimer.begin();
        }
    }
}