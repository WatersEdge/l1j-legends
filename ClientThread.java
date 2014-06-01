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
package l1j.server.server;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.math.BigInteger;

import l1j.server.Config;
import l1j.server.server.Opcodes;
import l1j.server.server.datatables.CharBuffTable;
import l1j.server.server.log.LogIP;
import l1j.server.server.model.L1DragonSlayer;
import l1j.server.server.model.Getback;
import l1j.server.server.model.L1Trade;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1DollInstance;
import l1j.server.server.model.Instance.L1FollowerInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.model.Instance.L1SummonInstance;
import l1j.server.server.serverpackets.S_Disconnect;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_SummonPack;
import l1j.server.server.serverpackets.ServerBasePacket;
import l1j.server.server.utils.StreamUtil;
import l1j.server.server.utils.SystemUtil;
import static l1j.server.server.model.skill.L1SkillId.*;

// Referenced classes of package l1j.server.server:
// PacketHandler, Logins, IpTable, LoginController,
// ClanTable, IdFactory
//
public class ClientThread implements Runnable, PacketOutput {

	private static Logger _log = Logger.getLogger(ClientThread.class.getName());

	private InputStream _in;

	private OutputStream _out;

	private PacketHandler _handler;

	private Account _account;

	private L1PcInstance _activeChar;

	private String _ip;

	private String _hostname;

	private Socket _csocket;
	//TODO ä¼ºæœ�å™¨ç¶‘ç¶�
	private int _xorByte=(byte) 0xF0;
	private long _authdata;
	//ä¼ºæœ�å™¨ç¶‘ç¶�
	
	//MP Bug fix - dont remove - tricid
	private boolean stop = false;

	private int _loginStatus = 0;

	private static final byte[] FIRST_PACKET = { // 3.5C Taiwan Server 
		    (byte) 0xf4, (byte) 0x0a, (byte) 0x8d, (byte) 0x23, (byte) 0x6f, 
		    (byte) 0x7f, (byte) 0x04, (byte) 0x00, (byte) 0x05, (byte) 0x08, 
		    (byte) 0x00 
    };

	/**
	 * for Test
	 */
	protected ClientThread() {
	}

	public ClientThread(Socket socket) throws IOException {
		_csocket = socket;
		_ip = socket.getInetAddress().getHostAddress();
		if (Config.HOSTNAME_LOOKUPS) {
			_hostname = socket.getInetAddress().getHostName();
		} else {
			_hostname = _ip;
		}
		_in = socket.getInputStream();
		_out = new BufferedOutputStream(socket.getOutputStream());
		//TODO ä¼ºæœ�å™¨ç¶‘ç¶� 
		if(Config.LOGINS_TO_AUTOENTICATION)
		{
 			int randomNumber = (int)(Math.random()*900000000)+255;
 			_xorByte = randomNumber%255+1;
			_authdata = new BigInteger(Integer.toString(randomNumber)).modPow(new BigInteger(Config.RSA_KEY_E), new BigInteger(Config.RSA_KEY_N)).longValue();
		}
		//ä¼ºæœ�å™¨ç¶‘ç¶�
		// PacketHandler åˆ�å§‹åŒ–
		_handler = new PacketHandler(this);
	}

	public String getIp() {
		return _ip;
	}

	public String getHostname() {
		return _hostname;
	}

	// TODO: ç¿»è­¯
	// ClientThreadã�«ã‚ˆã‚‹ä¸€å®šé–“éš”è‡ªå‹•ã‚»ãƒ¼ãƒ–ã‚’åˆ¶é™�ã�™ã‚‹ç‚ºã�®ãƒ•ãƒ©ã‚°ï¼ˆtrue:åˆ¶é™� false:åˆ¶é™�ç„¡ã�—ï¼‰
	// ç�¾åœ¨ã�¯C_LoginToServerã�Œå®Ÿè¡Œã�•ã‚Œã�Ÿéš›ã�«falseã�¨ã�ªã‚Šã€�
	// C_NewCharSelectã�Œå®Ÿè¡Œã�•ã‚Œã�Ÿéš›ã�«trueã�¨ã�ªã‚‹
	private boolean _charRestart = true;

	public void CharReStart(boolean flag) {
		_charRestart = flag;
	}

	
	private byte[] readPacket() throws Exception {
		try {
			int hiByte = _in.read();
			int loByte = _in.read();
			//TODO ä¼ºæœ�å™¨ç¶‘ç¶� 
			if(Config.LOGINS_TO_AUTOENTICATION)
			{
				hiByte ^= _xorByte;
				loByte ^= _xorByte;
			}
			//ä¼ºæœ�å™¨ç¶‘ç¶� 
			if ((loByte < 0) || (hiByte < 0)) { 
				throw new RuntimeException();
			}

			final int dataLength = ((loByte << 8) + hiByte) - 2;
			if ((dataLength <= 0) || (dataLength > 65533)) {
				throw new RuntimeException();
			}
			
			byte data[] = new byte[dataLength];

			int readSize = 0;

			for (int i = 0; i != -1 && readSize < dataLength; readSize += i) {
				i = _in.read(data, readSize, dataLength - readSize);
			}

			if (readSize != dataLength) {
				_log.warning("Incomplete Packet is sent to the server, closing connection.");
				throw new RuntimeException();
			}			//ä¼ºæœ�å™¨ç¶‘ç¶�
			if(Config.LOGINS_TO_AUTOENTICATION)
			{
				for(int i =0 ; i < dataLength ; i++) {
                    			data[i]=(byte)(data[i] ^ _xorByte);
				}
			}			//ä¼ºæœ�å™¨ç¶‘ç¶�
			return _cipher.decrypt(data);
		} catch (Exception e) {
			throw e;
		}
	}

	private long _lastSavedTime = System.currentTimeMillis();

	private long _lastSavedTime_inventory = System.currentTimeMillis();

	private Cipher _cipher;

	private void doAutoSave() throws Exception {
		if (_activeChar == null || _charRestart) {
			return;
		}
		try {
			// è‡ªå‹•å„²å­˜è§’è‰²è³‡æ–™
			if (Config.AUTOSAVE_INTERVAL * 1000 < System.currentTimeMillis()
					- _lastSavedTime) {
				_activeChar.save();
				_lastSavedTime = System.currentTimeMillis();
			}

			// è‡ªå‹•å„²å­˜èº«ä¸Šé�“å…·è³‡æ–™
			if (Config.AUTOSAVE_INTERVAL_INVENTORY * 1000 < System
					.currentTimeMillis() - _lastSavedTime_inventory) {
				_activeChar.saveInventory();
				_lastSavedTime_inventory = System.currentTimeMillis();
			}
		} catch (Exception e) {
			_log.warning("Client autosave failure.");
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
			throw e;
		}
	}

	@Override
	public void run() {
		

		/*
		 * TODO: ç¿»è­¯ ã‚¯ãƒ©ã‚¤ã‚¢ãƒ³ãƒˆã�‹ã‚‰ã�®ãƒ‘ã‚±ãƒƒãƒˆã‚’ã�‚ã‚‹ç¨‹åº¦åˆ¶é™�ã�™ã‚‹ã€‚ ç�†ç”±ï¼šä¸�æ­£ã�®èª¤æ¤œå‡ºã�Œå¤šç™ºã�™ã‚‹æ��ã‚Œã�Œã�‚ã‚‹ã�Ÿã‚�
		 * ex1.ã‚µãƒ¼ãƒ�ã�«é�Žè² è�·ã�ŒæŽ›ã�‹ã�£ã�¦ã�„ã‚‹å ´å�ˆã€�è² è�·ã�Œè�½ã�¡ã�Ÿã�¨ã��ã�«ã‚¯ãƒ©ã‚¤ã‚¢ãƒ³ãƒˆãƒ‘ã‚±ãƒƒãƒˆã‚’ä¸€æ°—ã�«å‡¦ç�†ã�—ã€�çµ�æžœçš„ã�«ä¸�æ­£æ‰±ã�„ã�¨ã�ªã‚‹ã€‚
		 * ex2.ã‚µãƒ¼ãƒ�å�´ã�®ãƒ�ãƒƒãƒˆãƒ¯ãƒ¼ã‚¯ï¼ˆä¸‹ã‚Šï¼‰ã�«ãƒ©ã‚°ã�Œã�‚ã‚‹å ´å�ˆã€�ã‚¯ãƒ©ã‚¤ã‚¢ãƒ³ãƒˆãƒ‘ã‚±ãƒƒãƒˆã�Œä¸€æ°—ã�«æµ�ã‚Œè¾¼ã�¿ã€�çµ�æžœçš„ã�«ä¸�æ­£æ‰±ã�„ã�¨ã�ªã‚‹ã€‚
		 * ex3.ã‚¯ãƒ©ã‚¤ã‚¢ãƒ³ãƒˆå�´ã�®ãƒ�ãƒƒãƒˆãƒ¯ãƒ¼ã‚¯ï¼ˆä¸Šã‚Šï¼‰ã�«ãƒ©ã‚°ã�Œã�‚ã‚‹å ´å�ˆã€�ä»¥ä¸‹å�Œæ§˜ã€‚
		 * 
		 * ç„¡åˆ¶é™�ã�«ã�™ã‚‹å‰�ã�«ä¸�æ­£æ¤œå‡ºæ–¹æ³•ã‚’è¦‹ç›´ã�™å¿…è¦�ã�Œã�‚ã‚‹ã€‚
		 */
		HcPacket movePacket = new HcPacket(M_CAPACITY);
		HcPacket hcPacket = new HcPacket(H_CAPACITY);
		GeneralThreadPool.getInstance().execute(movePacket);
		GeneralThreadPool.getInstance().execute(hcPacket);
		
		String keyHax ="";
		int key = 0;
		byte Bogus = 0;
		
		try {
			/** æŽ¡å�–äº‚æ•¸å�–seed */
			keyHax = Integer.toHexString((int) (Math.random() * 2147483647) + 1);
			key = Integer.parseInt(keyHax, 16);

			Bogus = (byte) (FIRST_PACKET.length + 7);
    		//TODO ä¼ºæœ�å™¨ç¶‘ç¶�
	if(Config.LOGINS_TO_AUTOENTICATION)
	{
		_out.write((int)(_authdata & 0xff));
		_out.write((int)(_authdata >> 8 & 0xff));
		_out.write((int)(_authdata >> 16 & 0xff));
		_out.write((int)(_authdata >> 24 & 0xff));
		_out.flush();
	}
   		//ä¼ºæœ�å™¨ç¶‘ç¶�
			_out.write(Bogus & 0xFF);
			_out.write(Bogus >> 8 & 0xFF);
			_out.write(Opcodes.S_OPCODE_INITPACKET);// 3.5C Taiwan Server
			_out.write((byte) (key & 0xFF));
			_out.write((byte) (key >> 8 & 0xFF));
			_out.write((byte) (key >> 16 & 0xFF));
			_out.write((byte) (key >> 24 & 0xFF));

			_out.write(FIRST_PACKET);
			_out.flush();

		}
		catch (Throwable e) {
			try {
				//_log.info("ç•°å¸¸ç”¨æˆ¶ç«¯(" + _hostname + ") é€£çµ�åˆ°ä¼ºæœ�å™¨, å·²ä¸­æ–·è©²é€£ç·šã€‚");
				StreamUtil.close(_out, _in);
				if (_csocket != null) {
					_csocket.close();
					_csocket = null;
				}
				return;
			} catch (Throwable ex) {
				return;
			}
		}
		finally {
		
		}



		try {

			ClientThreadObserver observer = new ClientThreadObserver(
					Config.AUTOMATIC_KICK * 60 * 1000); // è‡ªå‹•æ–·ç·šçš„æ™‚é–“ï¼ˆå–®ä½�:æ¯«ç§’ï¼‰

			// æ˜¯å�¦å•Ÿç”¨è‡ªå‹•è¸¢äºº
			if (Config.AUTOMATIC_KICK > 0) {
				observer.start();
			}
			
			_cipher = new Cipher(key);

			while (true) {
				doAutoSave();

				byte data[] = null;
				try {
					data = readPacket();
				} catch (Exception e) {
					break;
				}
				// _log.finest("[C]\n" + new
				// ByteArrayUtil(data).dumpToString());

				int opcode = data[0] & 0xFF;

				// è™•ç�†å¤šé‡�ç™»å…¥
				if (opcode == Opcodes.C_OPCODE_COMMONCLICK
						|| opcode == Opcodes.C_OPCODE_CHANGECHAR) {
					_loginStatus = 1;
				}
				if (opcode == Opcodes.C_OPCODE_LOGINTOSERVER) {
					if (_loginStatus != 1) {
						continue;
					}
				}
				if (opcode == Opcodes.C_OPCODE_LOGINTOSERVEROK
						|| opcode == Opcodes.C_OPCODE_RETURNTOLOGIN) {
					_loginStatus = 0;
				}

				if (opcode != Opcodes.C_OPCODE_KEEPALIVE) {
					// C_OPCODE_KEEPALIVEä»¥å¤–ã�®ä½•ã�‹ã�—ã‚‰ã�®ãƒ‘ã‚±ãƒƒãƒˆã‚’å�—ã�‘å�–ã�£ã�Ÿã‚‰Observerã�¸é€šçŸ¥
					observer.packetReceived();
				}
				// TODO: ç¿»è­¯
				// å¦‚æžœç›®å‰�è§’è‰²ç‚º null ã�¯ã‚­ãƒ£ãƒ©ã‚¯ã‚¿ãƒ¼é�¸æŠžå‰�ã�ªã�®ã�§Opcodeã�®å�–æ�¨é�¸æŠžã�¯ã�›ã�šå…¨ã�¦å®Ÿè¡Œ
				if (_activeChar == null) {
					_handler.handlePacket(data, _activeChar);
					continue;
				}

				// TODO: ç¿»è­¯
				// ä»¥é™�ã€�PacketHandlerã�®å‡¦ç�†çŠ¶æ³�ã�ŒClientThreadã�«å½±éŸ¿ã‚’ä¸Žã�ˆã�ªã�„ã‚ˆã�†ã�«ã�™ã‚‹ç‚ºã�®å‡¦ç�†
				// ç›®çš„ã�¯Opcodeã�®å�–æ�¨é�¸æŠžã�¨ClientThreadã�¨PacketHandlerã�®åˆ‡ã‚Šé›¢ã�—

				// è¦�è™•ç�†çš„ OPCODE
				// åˆ‡æ�›è§’è‰²ã€�ä¸Ÿé�“å…·åˆ°åœ°ä¸Šã€�åˆªé™¤èº«ä¸Šé�“å…·
				if (opcode == Opcodes.C_OPCODE_CHANGECHAR
						|| opcode == Opcodes.C_OPCODE_DROPITEM
						|| opcode == Opcodes.C_OPCODE_DELETEINVENTORYITEM) {
					_handler.handlePacket(data, _activeChar);
				} else if (opcode == Opcodes.C_OPCODE_MOVECHAR) {
					// ç‚ºäº†ç¢ºä¿�å�³æ™‚çš„ç§»å‹•ï¼Œå°‡ç§»å‹•çš„å°�åŒ…ç�¨ç«‹å‡ºä¾†è™•ç�†
					movePacket.requestWork(data);
				} else {
					// è™•ç�†å…¶ä»–æ•¸æ“šçš„å‚³é�ž
					hcPacket.requestWork(data);
				}
			}
		} catch (Throwable e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			try {
				if (_activeChar != null) {
					quitGame(_activeChar);

					synchronized (_activeChar) {
						// å¾žç·šä¸Šä¸­ç™»å‡ºè§’è‰²
						_activeChar.logout();
						setActiveChar(null);
					}
				}
				// çŽ©å®¶é›¢ç·šæ™‚, online=0
				if (getAccount() != null) {
					Account.online(getAccount(), false);
				}

				// é€�å‡ºæ–·ç·šçš„å°�åŒ…
				sendPacket(new S_Disconnect());

				StreamUtil.close(_out, _in);
				if (_csocket != null) {
					_csocket.close();
					_csocket = null;
				}
			} catch (Exception e) {
				_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
			} finally {
				LoginController.getInstance().logout(this);
			}
		}
		_csocket = null;
		_log.fine("Server thread[C] stopped");
		if (_kick < 1) {

			if (getAccount() != null) {
				Account.online(getAccount(), false);
			}
		}
		return;
	}

	private int _kick = 0;

	public void kick() {
		try {
			Account.online(getAccount(), false);
			sendPacket(new S_Disconnect());
			_kick = 1;
			StreamUtil.close(_out, _in);
			if (_csocket != null) {
				_csocket.close();
				_csocket = null;
			}
		}
		catch (Throwable ex) {
			
		}
	}

	private static final int M_CAPACITY = 3; // ä¸€é‚Šç§»å‹•çš„æœ€å¤§å°�åŒ…é‡�

	private static final int H_CAPACITY = 2;// ä¸€æ–¹æŽ¥å�—çš„æœ€é«˜é™�é¡�æ‰€éœ€çš„è¡Œå‹•

	// å¸³è™Ÿè™•ç�†çš„ç¨‹åº�
	class HcPacket implements Runnable {
		private final Queue<byte[]> _queue;

		private PacketHandler _handler;

		public HcPacket() {
			_queue = new ConcurrentLinkedQueue<byte[]>();
			_handler = new PacketHandler(ClientThread.this);
		}

		public HcPacket(int capacity) {
			_queue = new LinkedBlockingQueue<byte[]>(capacity);
			_handler = new PacketHandler(ClientThread.this);
		}

		public void requestWork(byte data[]) {
			_queue.offer(data);
		}

		@Override
		public void run() {
			byte[] data;
			while (_csocket != null) {
				data = _queue.poll();
				if (data != null) {
					try {
						_handler.handlePacket(data, _activeChar);
					} catch (Exception e) {
					}
				} else {
					try {
						Thread.sleep(10);
					} catch (Exception e) {
					}
				}
			}
			return;
		}
	}

	private static Timer _observerTimer = new Timer();

	// å®šæ™‚ç›£æŽ§å®¢æˆ¶ç«¯
	class ClientThreadObserver extends TimerTask {
		private int _checkct = 1;

		private final int _disconnectTimeMillis;

		public ClientThreadObserver(int disconnectTimeMillis) {
			_disconnectTimeMillis = disconnectTimeMillis;
		}

		public void start() {
			_observerTimer.scheduleAtFixedRate(ClientThreadObserver.this, 0,
					_disconnectTimeMillis);
		}

		@Override
		public void run() {
			try {
				if (_csocket == null) {
					cancel();
					return;
				}

				if (_checkct > 0) {
					_checkct = 0;
					return;
				}

				if (_activeChar == null // é�¸è§’è‰²ä¹‹å‰�
						|| _activeChar != null && !_activeChar.isPrivateShop()) { // æ­£åœ¨å€‹äººå•†åº—
					kick();
					_log.warning("ä¸€å®šæ™‚é–“æ²’æœ‰æ”¶åˆ°å°�åŒ…å›žæ‡‰ï¼Œæ‰€ä»¥å¼·åˆ¶åˆ‡æ–· (" + _hostname + ") çš„é€£ç·šã€‚");
					Account.online(getAccount(), false);
					cancel();
					return;
				}
			} catch (Exception e) {
				_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
				cancel();
			}
		}

		public void packetReceived() {
			_checkct++;
		}
	}

	@Override
	public void sendPacket(ServerBasePacket packet) {
		synchronized (this) {
			try {
				byte content[] = packet.getContent();
				byte data[] = Arrays.copyOf(content, content.length);
				_cipher.encrypt(data);
				int length = data.length + 2;

				_out.write(length & 0xff);
				_out.write(length >> 8 & 0xff);
				_out.write(data);
				_out.flush();
			} catch (Exception e) {
			}
		}
	}
	
	//mp bug fix - dont remove - tricid
	public void rescue() {
			try {		
				System.out.println("* * * Closing socket	* * * ");
				_csocket.close();
			} catch (Exception e) { 
				System.out.println("* * * Failed closing socket	* * *");
				System.out.println(e); 
			}
			try {
				System.out.println("* * * Closing streams	* * *");
				StreamUtil.close(_out, _in);
			} catch (Exception e) { 
				System.out.println("* * * Failed to close streams	* * *");		
				System.out.println(e); }
		
			try {
				System.out.println("* * * Stopping client thread	* * *");
				stop = true;
			} catch (Exception e) {
				System.out.println("* * * Failed stopping thread	* * *");
			}
	}

	public void close() throws IOException {
		if (_csocket != null) {
			_csocket.close();
			_csocket = null;
		}
	}

	public void setActiveChar(L1PcInstance pc) {
		_activeChar = pc;
	}

	public L1PcInstance getActiveChar() {
		return _activeChar;
	}

	public void setAccount(Account account) {
		_account = account;
	}

	public Account getAccount() {
		return _account;
	}

	public String getAccountName() {
		if (_account == null) {
			return null;
		}
		return _account.getName();
	}

	public static void quitGame(L1PcInstance pc) {
		
		//Fix for force quit, the char will remain online for extra 30s if the char has the following effect - [Hank]
		if( (pc.hasSkillEffect(EARTH_BIND)) || (pc.hasSkillEffect(SHOCK_STUN)) || (pc.hasSkillEffect(THUNDER_GRAB)) ||
			(pc.hasSkillEffect(FOG_OF_SLEEPING)) || (pc.hasSkillEffect(ICE_LANCE)) || (pc.hasSkillEffect(BONE_BREAK)) ||
			(pc.hasSkillEffect(CURSE_PARALYZE)) || pc.isSleeped() || pc.isParalyzed() || pc.isPinkName())
		{
			//Announcement for force qut - [Hank]
			L1World.getInstance().broadcastServerMessage("Force Quit Penalty " + pc.getName() + " will be remained logged in for 30 sec");
			try {
				pc.save();
				pc.saveInventory();
				// char remain in the game for extra 30s
				Thread.sleep(30000);
				pc.save();
				pc.saveInventory();
			} catch (Exception e) {
				System.out.println("force quit penalty failed.");
			}
			
			
		}
		// å¦‚æžœæ­»æŽ‰å›žåˆ°åŸŽä¸­ï¼Œè¨­å®šé£½é£Ÿåº¦
		if (pc.isDead()) {
			try {
				Thread.sleep(2000);// æš«å�œè©²åŸ·è¡ŒçºŒï¼Œå„ªå…ˆæ¬Šè®“çµ¦expmonitor
				int[] loc = Getback.GetBack_Location(pc, true);
				pc.setX(loc[0]);
				pc.setY(loc[1]);
				pc.setMap((short) loc[2]);
				pc.setCurrentHp(pc.getLevel());
				pc.set_food(40);
			} catch (InterruptedException ie) {
				ie.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// çµ‚æ­¢äº¤æ˜“
		if (pc.getTradeID() != 0) { // ãƒˆãƒ¬ãƒ¼ãƒ‰ä¸­
			L1Trade trade = new L1Trade();
			trade.TradeCancel(pc);
		}

		// çµ‚æ­¢æ±ºé¬¥
		if (pc.getFightId() != 0) {
			L1PcInstance fightPc = (L1PcInstance) L1World.getInstance()
					.findObject(pc.getFightId());
			pc.setFightId(0);
			if (fightPc != null) {
				fightPc.setFightId(0);
				fightPc.sendPackets(new S_PacketBox(S_PacketBox.MSG_DUEL, 0, 0));
			}
		}

		// é›¢é–‹çµ„éšŠ
		if (pc.isInParty()) { // å¦‚æžœæœ‰çµ„éšŠ
			pc.getParty().leaveMember(pc);
		}

		// TODO: é›¢é–‹è�Šå¤©çµ„éšŠ(?)
		if (pc.isInChatParty()) { // å¦‚æžœåœ¨è�Šå¤©çµ„éšŠä¸­(?)
			pc.getChatParty().leaveMember(pc);
		}
		// ç§»é™¤ä¸–ç•Œåœ°åœ–ä¸Šçš„å¯µç‰©
		// è®Šæ›´å�¬å–šæ€ªç‰©çš„å��ç¨±
		for (L1NpcInstance petNpc : pc.getPetList().values()) {
			if (petNpc instanceof L1PetInstance) {
				L1PetInstance pet = (L1PetInstance) petNpc;
				// å�œæ­¢é£½é£Ÿåº¦è¨ˆæ™‚
				pet.stopFoodTimer(pet);
//				pet.dropItem(); // edited out pet item to owner
				pet.collect(true);
				pc.getPetList().remove(pet.getId());
				pet.deleteMe();
			} else if (petNpc instanceof L1SummonInstance) {
				L1SummonInstance summon = (L1SummonInstance) petNpc;
				for (L1PcInstance visiblePc : L1World.getInstance()
						.getVisiblePlayer(summon)) {
					visiblePc.sendPackets(new S_SummonPack(summon, visiblePc,
							false));
				}
			}
		}

		// ç§»é™¤ä¸–ç•Œåœ°åœ–ä¸Šçš„é­”æ³•å¨ƒå¨ƒ
		for (L1DollInstance doll : pc.getDollList().values())
			doll.deleteDoll();

		// é‡�æ–°å»ºç«‹è·Ÿéš¨è€…
		for (L1FollowerInstance follower : pc.getFollowerList().values()) {
			follower.setParalyzed(true);
			follower.spawn(follower.getNpcTemplate().get_npcId(),
					follower.getX(), follower.getY(), follower.getHeading(),
					follower.getMapId());
			follower.deleteMe();
		}

		// åˆªé™¤å± é¾�å‰¯æœ¬æ­¤çŽ©å®¶ç´€éŒ„
		if (pc.getPortalNumber() != -1) {
			L1DragonSlayer.getInstance().removePlayer(pc, pc.getPortalNumber());
		}

		// å„²å­˜é­”æ³•ç‹€æ…‹
		CharBuffTable.DeleteBuff(pc);
		CharBuffTable.SaveBuff(pc);
		pc.clearSkillEffectTimer();
		l1j.server.server.model.game.L1PolyRace.getInstance()
				.checkLeaveGame(pc);

		// å�œæ­¢çŽ©å®¶çš„å�µæ¸¬
		pc.stopEtcMonitor();
		// è¨­å®šç·šä¸Šç‹€æ…‹ç‚ºä¸‹ç·š
		pc.setOnlineStatus(0);
		// è¨­å®šå¸³è™Ÿç‚ºä¸‹ç·š
		//Account account = Account.load(pc.getAccountName());
		//Account.online(account, false);
		// è¨­å®šå¸³è™Ÿçš„è§’è‰²ç‚ºä¸‹ç·š
		Account account = Account.load(pc.getAccountName());
		Account.OnlineStatus(account, false);
		
		LogIP li = new LogIP();
	  	li.storeLogout(pc); 

		try {
			pc.save();
			pc.saveInventory();
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		}
	}
}
