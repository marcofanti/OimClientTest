package oracle.iam.samples;

import java.util.Hashtable;

import oracle.iam.platform.OIMClient;

//===================================================================
public abstract class Client
// ===================================================================
{

	private static final String OIM_URL = "t3://192.168.1.142:14000"; // OIM 11g deployment
//	private static final String OIM_URL = "t3://identity.oracleads.com:14000"; // OIM 11g deployment
	private static final String AUTH_CONF = "/Users/mfanti/Programming Development/eclipseworkspace/oimclient/conf/authwl.conf"; // "/app/oracle/product/ofm/Oracle_IAM1/server/config/authwl.conf"
	private static final String APPSERVER_TYPE = "wls";
	private static final String WEBLOGIC_NAME = "oim_server1";
	protected static final String OIM_USERNAME = "admin";
	protected static final String OIM_PASSWORD = "Oracle123"; // "Oracle123"
	protected static final String NULL = "(null)";
	protected static final String NL = "\n";
	protected static final String SP = "\t";
	protected OIMClient _oimClient = null;

	// ----------------------------------------------------------------
	public Client() throws Exception
	// ----------------------------------------------------------------
	{
		Hashtable<String, String> env = new Hashtable<String, String>();

		System.setProperty("java.security.auth.login.config", AUTH_CONF);
		System.setProperty("APPSERVER_TYPE", APPSERVER_TYPE);
		System.setProperty("weblogic.Name", WEBLOGIC_NAME);

		env.put(OIMClient.JAVA_NAMING_FACTORY_INITIAL, "weblogic.jndi.WLInitialContextFactory");
		env.put(OIMClient.JAVA_NAMING_PROVIDER_URL, OIM_URL);

		_oimClient = new OIMClient(env);

		_oimClient.login(OIM_USERNAME, OIM_PASSWORD.toCharArray());

		return;
	}

	// ----------------------------------------------------------------
	protected abstract void execute() throws Exception;
	// ----------------------------------------------------------------

	// ----------------------------------------------------------------
	protected void log(String str)
	// ----------------------------------------------------------------
	{
		if (str != null) {
			System.out.println("LOG: " + str);
		}
		return;
	}
}
