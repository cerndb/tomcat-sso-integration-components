package ch.cern.sso.tomcat.common.aisession;

import ch.cern.sso.tomcat.common.utils.Constants;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.Locale;
import java.math.BigInteger;

/**
 * Session class used to encrypt/decrypt the session cookie.
 * 
 */
public class Credentials {
	/*
	 * Cookie format: 00 0 01 1 02-09 ff 10 1 11-14 checksum 15-18 timestamp
	 * 19-22 IP address 23-26 person id 27-30 CERN id 31-57 user name 58
	 * language 59 version 60-61 x resolution 62-63 y resolution 64 user has
	 * cernid flag 65 internal site access flag 66 user logged-in as someone
	 * else flag 67-70 original user's person id
	 */
	private static final int BITS = 800;
	private static final int BYTES = (BITS + 7) / 8;
	private static final int FREE_BYTES = 11;
	private static final int BYTES_USED = 60;

	private static final BigInteger modulus = new BigInteger(
			Utils.getValue("modulus"), 16);
	private static final BigInteger prime0 = new BigInteger(
			Utils.getValue("prime0"), 16);
	private static final BigInteger prime1 = new BigInteger(
			Utils.getValue("prime1"), 16);
	private static final BigInteger primeExponent0 = new BigInteger(
			Utils.getValue("primeExponent0"), 16);
	private static final BigInteger primeExponent1 = new BigInteger(
			Utils.getValue("primeExponent1"), 16);
	private static final BigInteger coefficient = new BigInteger(
			Utils.getValue("coefficient"), 16);
	private static final BigInteger public_key = new BigInteger(
			Utils.getValue("public_key"), 16);

	// 12 hours of expiration time in miliseconds
	private static final long EXPIRATION_TIME = (long) 12 * 60 * 60 * 1000;

	// Current version of the cookie
	private static final byte CURRENT_VERSION = (char) 1;

	private String m_cookie;
	private transient int m_chkSum;
	private transient Date m_dateOfIssue;
	private transient byte[] m_abyteIP;
	private transient int m_personId;
	private transient int m_originalPersonId;
	private transient int m_cernId;
	private transient String m_userName;
	private transient String m_language;
	private transient byte m_CookieVer;
	private transient short m_XResolution;
	private transient short m_YResolution;
	private transient boolean m_internalSiteAccess;
	private transient boolean m_userLoggedAsSomeoneElse;

	private short _cvt2Short(byte[] abyte, int i) {
		return (short) (((((short) abyte[i + 0]) & 0xff) << 8) + ((((short) abyte[i + 1]) & 0xff) << 0));
	}

	private boolean _cvt2Boolean(byte[] abyte, int i) {
		return (abyte[i] == (byte) 1);
	}

	private byte _cvt2Byte(byte[] abyte, int i) {
		return abyte[i];
	}

	private int _cvt2Int(byte[] abyte, int i) {
		return ((((int) abyte[i + 0]) & 0xff) << 24)
				+ ((((int) abyte[i + 1]) & 0xff) << 16)
				+ ((((int) abyte[i + 2]) & 0xff) << 8)
				+ ((((int) abyte[i + 3]) & 0xff));
	}

	private String _cvt2String(byte[] abyte, int iStart, int cbMax) {
		StringBuffer str = new StringBuffer();

		for (int i = 0; i < cbMax; i++) {
			if (abyte[iStart + i] == 0)
				return str.toString();
			else
				str.append((char) abyte[iStart + i]);
		}
		return str.toString();
	}

	private void _assignByte(byte[] abyte, int iStart, byte bArg) {
		abyte[iStart] = bArg;
	}

	private void _assignBoolean(byte[] abyte, int iStart, boolean bArg) {
		abyte[iStart] = (bArg ? (byte) 1 : (byte) 0);
	}

	private void _assignShort(byte[] abyte, int iStart, short shortArg) {
		if (iStart + 1 >= abyte.length)
			return;
		abyte[iStart + 0] = (byte) ((shortArg >> 8) & 0xff);
		abyte[iStart + 1] = (byte) ((shortArg >> 0) & 0xff);
	}

	private void _assignInt(byte[] abyte, int iStart, int intArg) {
		if (iStart + 3 >= abyte.length)
			return;
		abyte[iStart + 0] = (byte) ((intArg >> 24) & 0xff);
		abyte[iStart + 1] = (byte) ((intArg >> 16) & 0xff);
		abyte[iStart + 2] = (byte) ((intArg >> 8) & 0xff);
		abyte[iStart + 3] = (byte) ((intArg >> 0) & 0xff);
	}

	private void _assignString(byte[] abyte, int iStart, String str) {
		for (int i = 0; i < str.length(); i++) {
			if ((iStart + i) >= abyte.length || (i) >= str.length())
				return;
			abyte[iStart + i] = (byte) str.charAt(i);
		}
	}

	private void _assignBytes(byte[] abyte, int iStart, byte[] bbyte) {
		for (int i = 0; i < bbyte.length; i++) {
			if ((iStart + i) >= abyte.length || (i) >= bbyte.length)
				return;
			abyte[iStart + i] = bbyte[i];
		}
	}

	public Credentials(byte[] p_cookie) throws InstantiationException {
		m_cookie = new String(p_cookie);
		if (!isValidDecrypt()) {
			throw new InstantiationException(
					"Illegal Instantiation of Session()");
		}
	}

	public Credentials() throws InstantiationException {
		if (m_cookie == null) {
			throw new InstantiationException(
					"Illegal Instantiation of Session()");
		}
	}

	public Credentials(String p_cookie) throws InstantiationException {
		if (p_cookie == null) {
			throw new InstantiationException(
					"Illegal Instantiation of Session()");
		}
		m_cookie = new String(p_cookie);
		if (!isValidDecrypt()) {
			throw new IllegalArgumentException("Invalid Data");
		}
	}

	public Credentials(byte[] p_ip, int p_personId, int p_cernId,
			String p_username, String p_language, short p_xres, short p_yres,
			boolean p_internal, int p_originalPersonId, long validity)
			throws InstantiationException {
		// 
		long tmp_m_dateOfIssue = new Date().getTime();
		m_dateOfIssue = new Date( tmp_m_dateOfIssue -
				(tmp_m_dateOfIssue % (validity)));
		m_abyteIP = new byte[4];
		m_abyteIP[0] = p_ip[0];
		m_abyteIP[1] = p_ip[1];
		m_abyteIP[2] = p_ip[2];
		m_abyteIP[3] = p_ip[3];
		m_personId = p_personId;
		m_originalPersonId = p_originalPersonId;
		m_cernId = p_cernId;
		try {
			m_userName = new String(p_username);
		} catch (NullPointerException e) {
			m_userName = new String(Constants.DEFAULT_LANGUAGE);
		}
		m_language = new String(p_language);
		m_CookieVer = CURRENT_VERSION;
		m_XResolution = p_xres;
		m_YResolution = p_yres;
		m_internalSiteAccess = p_internal;

		if (!isValidEncrypt()) {
			throw new IllegalArgumentException("Invalid Data");
		}
	}
        
        public Credentials(byte[] p_ip, int p_personId, int p_cernId,
			String p_username, String p_language, short p_xres, short p_yres,
			boolean p_internal, int p_originalPersonId, long validity, boolean isAllowedLoginAs, String ssoIdentityClass)
			throws InstantiationException {
		// 
		long tmp_m_dateOfIssue = new Date().getTime();
		m_dateOfIssue = new Date( tmp_m_dateOfIssue -
				(tmp_m_dateOfIssue % (validity)));
		m_abyteIP = new byte[4];
		m_abyteIP[0] = p_ip[0];
		m_abyteIP[1] = p_ip[1];
		m_abyteIP[2] = p_ip[2];
		m_abyteIP[3] = p_ip[3];
		m_personId = p_personId;
		m_originalPersonId = p_originalPersonId;
		m_cernId = p_cernId;
		try {
			m_userName = new String(p_username);
		} catch (NullPointerException e) {
			m_userName = new String(Constants.DEFAULT_LANGUAGE);
		}
		m_language = new String(p_language);
		m_CookieVer = CURRENT_VERSION;
		m_XResolution = p_xres;
		m_YResolution = p_yres;
		m_internalSiteAccess = p_internal;

		if (!isValidEncrypt()) {
			throw new IllegalArgumentException("Invalid Data");
		}
	}

	private byte[] encrypt(byte[] de) {
		BigInteger p = prime0;
		BigInteger q = prime1;
		BigInteger dP = primeExponent0;
		BigInteger dQ = primeExponent1;
		BigInteger qInv = coefficient;
		BigInteger c, cP, cQ, mP, mQ;
		BigInteger intm = new BigInteger(1, de);
		byte[] result, ret;

		cP = intm.mod(p);
		cQ = intm.mod(q);
		mP = cP.modPow(dP, p);
		mQ = cQ.modPow(dQ, q);
		c = ((((((mP.subtract(mQ)).mod(p)).multiply(qInv))).mod(p)).multiply(q))
				.add(mQ);
		ret = c.toByteArray();
		if (de.length > ret.length) {
			result = new byte[de.length];
			System.arraycopy(ret, 0, result, de.length - ret.length, ret.length);
		} else if (de.length < ret.length) {
			result = new byte[de.length];
			System.arraycopy(ret, ret.length - de.length, result, 0, de.length);
		} else {
			result = ret;
		}
		return result;
	}

	private static byte[] decrypt(byte[] en) {
		BigInteger e = new BigInteger("65537");
		BigInteger n = public_key;
		BigInteger c = new BigInteger(1, en);
		BigInteger intm;
		byte[] result = null;
		byte[] ret;

		if (c.compareTo(n) <= 0) {
			intm = c.modPow(e, n);
			ret = intm.toByteArray();

			if (ret.length < BYTES) {
				result = new byte[BYTES];
				System.arraycopy(ret, 0, result, BYTES - ret.length, ret.length);
			} else {
				result = ret;
			}
		}
		return result;
	}

	public boolean isValidDecrypt() {
		byte[] data = null;
		int iStart = 0;
		boolean valid;

		if (m_cookie == null) {
			valid = false;
		} else {
			// Decryption of the session string
			if ((data = decrypt(fromHexCookie())) == null) {
				valid = false;
			} else {
				if (data[0] != 0 && data[1] != 1) {
					valid = false;
				} else {
					for (iStart = 2; iStart < BYTES - 1; iStart++) {
						if (data[iStart] != (byte) 0xff)
							break;
					}
					if (data[iStart++] != 0) {
						valid = false;
					} else {
						valid = (iStart + BYTES_USED <= BYTES);
					}
				}
			}
		}

		if (valid) {
			// Start pulling the fields out of the resulting byte array
			m_chkSum = _cvt2Int(data, iStart);
			m_dateOfIssue = new Date(
					((long) (_cvt2Int(data, iStart + 4)) * 1000));
			m_abyteIP = new byte[4];
			m_abyteIP[0] = data[iStart + 8];
			m_abyteIP[1] = data[iStart + 9];
			m_abyteIP[2] = data[iStart + 10];
			m_abyteIP[3] = data[iStart + 11];

			m_personId = _cvt2Int(data, iStart + 12);
			m_cernId = _cvt2Int(data, iStart + 16);
			m_userName = _cvt2String(data, iStart + 20, 27);
			m_language = _cvt2String(data, iStart + 47, 1);
			m_CookieVer = _cvt2Byte(data, iStart + 48);
			m_XResolution = _cvt2Short(data, iStart + 49);
			m_YResolution = _cvt2Short(data, iStart + 51);
			m_internalSiteAccess = _cvt2Boolean(data, iStart + 54);
			m_userLoggedAsSomeoneElse = _cvt2Boolean(data, iStart + 55);
			m_originalPersonId = _cvt2Int(data, iStart + 56);

			if (calculateCheckSum(data, iStart + 4, data.length) != m_chkSum) {
				valid = false;
			}
		}
		return valid;
	}

	private boolean isValidEncrypt() {
		int iDate;
		byte[] data;

		data = new byte[BYTES];
		data[0] = (byte) 0;
		data[1] = (byte) 1;
		for (int i = 2; i < FREE_BYTES - 1; i++)
			data[i] = (byte) 0xff;
		data[FREE_BYTES - 1] = (byte) 0;

		iDate = (int) ((m_dateOfIssue.getTime() / 1000));
		_assignInt(data, 4 + FREE_BYTES, iDate);
		m_dateOfIssue = new Date(((long) (iDate) * 1000));

		_assignBytes(data, 8 + FREE_BYTES, m_abyteIP);
		_assignInt(data, 12 + FREE_BYTES, m_personId);
		_assignInt(data, 16 + FREE_BYTES, m_cernId);
		_assignString(data, 20 + FREE_BYTES, m_userName);
		_assignString(data, 47 + FREE_BYTES, m_language);
		_assignByte(data, 48 + FREE_BYTES, m_CookieVer);
		_assignShort(data, 49 + FREE_BYTES, m_XResolution);
		_assignShort(data, 51 + FREE_BYTES, m_YResolution);
		_assignBoolean(data, 53 + FREE_BYTES, (m_cernId > 0));
		_assignBoolean(data, 54 + FREE_BYTES, m_internalSiteAccess);
		_assignBoolean(data, 55 + FREE_BYTES,
				(m_originalPersonId != m_personId));
		_assignInt(data, 56 + FREE_BYTES, m_originalPersonId);

		m_chkSum = calculateCheckSum(data, 4 + FREE_BYTES, data.length);
		_assignInt(data, 0 + FREE_BYTES, m_chkSum);

		m_cookie = toHex(encrypt(data));
		return (m_cookie != null);
	}

	private static final char[] CHAR_LOOKUP = { '0', '1', '2', '3', '4', '5',
			'6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	private String toHex(byte[] p_array) {
		char[] c = null;

		if (p_array != null) {
			c = new char[p_array.length * 2];
			for (int i = 0; i < p_array.length; i++) {
				c[i * 2] = CHAR_LOOKUP[(p_array[i] >> 4) & 0x0f];
				c[i * 2 + 1] = CHAR_LOOKUP[(p_array[i] >> 0) & 0x0f];
			}
		}
		return (c == null ? null : new String(c));
	}

	private byte[] fromHexCookie() {
		byte[] bytes = new byte[m_cookie.length() / 2];

		for (int i = 0; i < m_cookie.length() - 1; i += 2) {
			bytes[i / 2] = (byte) Integer.parseInt(
					m_cookie.substring(i, i + 2), 16);
		}
		return bytes;
	}

	/* Calculates the checkSum of a byte array from startInd up to endInd. */
	private int calculateCheckSum(byte[] aByte, int startInd, int endInd) {
		int i, sum = 0;

		if (aByte.length <= endInd) {
			for (i = startInd; i < endInd; i++) {
				sum += ((int) aByte[i]) & 0xff;
			}
		}
		return sum;
	}

	/**
	 * Tests if a session has expired. If the session has been obtained by a
	 * serialization mechanism, that is it has been constructed using the
	 * argumentless constructor, it must be preceeded by a call to isValid().
	 */
	public boolean isExpired() {
		Date current = new Date();

		return (m_dateOfIssue == null || m_dateOfIssue.after(current) || m_dateOfIssue
				.before(new Date(current.getTime() - EXPIRATION_TIME)));
	}

	/**
	 * Return the session string. The session string is an encrypted data block
	 * and should not be used by an application other than passing it to API's
	 * or objects requiring the session string.
	 * 
	 * @return The session string
	 */
	public String getSessionString() {
		return m_cookie;
	}

	/**
	 * Get the CheckSum id of the user of the session
	 */
	public int getCheckSum() {
		return m_chkSum;
	}

	/** Return the date of issue of the session. */
	public Date getSessionDate() {
		return m_dateOfIssue;
	}

	/** Return the IP address of the client. */
	public InetAddress getIPAddress() throws java.net.UnknownHostException {
		return InetAddress.getByName(String
				.valueOf(((int) m_abyteIP[0]) & 0xff)
				+ "."
				+ String.valueOf(((int) m_abyteIP[1]) & 0xff)
				+ "."
				+ String.valueOf(((int) m_abyteIP[2]) & 0xff)
				+ "."
				+ String.valueOf(((int) m_abyteIP[3]) & 0xff));
	}

	/** Return the HR id of the user of the session. */
	public int getHrId() {
		return getPersonId();
	}

	/** Return the person id of the user of the session. */
	public int getPersonId() {
		return m_personId;
	}

	/** Return the person id of the original user of the session. */
	public int getOriginalPersonId() {
		return m_originalPersonId;
	}

	/** Return the CERN id of user of the session. */
	public int getCernId() {
		return m_cernId;
	}

	/** Return the login name. */
	public String getLoginName() throws RemoteException {
		return m_userName;
	}

	/**
	 * Get the preferred language of the session. NOTE: The country of the
	 * locale returned will always be Switzerland but the language of the locale
	 * will reflect the users prefered language.
	 * 
	 * @return The prefered language as a Locale.
	 */
	public String getPreferedLanguage() {
		return m_language;
	}

	public Locale getPreferedLocale() throws RemoteException {
		return new Locale("CH", (m_language.equals("F")) ? "fr" : "en");
	}

	/** Return the Cookie Version. */
	public byte getCookieVer() {
		return m_CookieVer;
	}

	/** Return the X Resolution. */
	public int getXResolution() {
		return (int) m_XResolution;
	}

	/** Return the Y Resolution. */
	public int getYResolution() {
		return (int) m_YResolution;
	}

	public boolean internalSiteAccess() {
		return m_internalSiteAccess;
	}

	public boolean userLoggedAsSomeoneElse() {
		return m_userLoggedAsSomeoneElse;
	}

	static public void main(String argv[]) throws UnknownHostException {
		Credentials test1, test2, test3;
		byte[] IP = { (byte) 137, (byte) 138, (byte) 58, (byte) 52 }; //
		// Josef
		try {
			long ai_session_validity = 12 * 60 * 60 * 1000;
			
			 test1 = new Credentials(IP, 438064, 40526, "WVANLEER", "EN",
			 (short) 800, (short) 600, false, 438064, ai_session_validity);
			 System.out.println(test1.getCheckSum());
			 System.out.println((test1.getSessionDate()).toString());
			 System.out.println(test1.getIPAddress());
			 System.out.println(test1.getHrId());
			 System.out.println(test1.getCernId());
			 System.out.println(test1.getLoginName());
			 System.out.println(test1.getPreferedLanguage());
			 System.out.println(test1.getXResolution());
			 System.out.println(test1.getYResolution());
			 System.out.println(test1.getCookieVer());
			 System.out.println(test1.getSessionString());

			// test2 = new Credentials(test1.getSessionString());

//			test2 = new Credentials(
//					"14F70F544BFE0044270BCAFDC04514F12219959444455EB37B5D95BD42B7BDFFE65DEFFA3D394E2B2CB6F4FB05FC073E5D7637535D715C256BBCCF6377438A9062D0AA3406343D7149131F2209051642B8F8E25E8CEE5367788477D97C12F1EAA4CBCC57");
//			System.out.println(test2.getCheckSum());
//			System.out.println((test2.getSessionDate()).toString());
//			System.out.println(test2.getIPAddress());
//			System.out.println(test2.getHrId());
//			System.out.println(test2.getCernId());
//			System.out.println(test2.getLoginName());
//			System.out.println(test2.getPreferedLanguage());
//			System.out.println(test2.getXResolution());
//			System.out.println(test2.getYResolution());
//			System.out.println(test2.getCookieVer());
//			System.out.println(test2.getSessionString());

			// if (argv.length != 0) {
			// test3 = new Credentials(argv[0]);
			// System.out.println(test3.getSessionString());
			// System.out.println(test3.getCheckSum());
			// System.out.println((test3.getSessionDate()).toString());
			// System.out.println(test3.getIPAddress());
			// System.out.println(test3.getHrId());
			// System.out.println(test3.getCernId());
			// System.out.println(test3.getLoginName());
			// System.out.println(test3.getPreferedLanguage());
			// System.out.println(test3.getXResolution());
			// System.out.println(test3.getYResolution());
			// System.out.println(test3.getCookieVer());
			// System.out.println(test3.getSessionString());
			// }
		} catch (Exception e) {
			System.out.println("Exception!!!");
			e.printStackTrace(System.out);
		}

		byte[] ip = getIp("137.138.29.208");
	}

	private static byte[] getIp(String remoteAddr) throws UnknownHostException {

		InetAddress ip = InetAddress.getByName(remoteAddr);

		return ip.getAddress();
	}

}