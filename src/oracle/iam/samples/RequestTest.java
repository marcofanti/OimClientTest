package oracle.iam.samples;

//Role related API's
import oracle.iam.identity.rolemgmt.api.RoleManager;
import oracle.iam.identity.rolemgmt.vo.Role;
import oracle.iam.identity.exception.RoleSearchException;
import oracle.iam.identity.rolemgmt.api.RoleManagerConstants.RoleAttributeName;
import oracle.iam.identity.rolemgmt.api.RoleManagerConstants.RoleCategoryAttributeName;

//User related API's
import oracle.iam.identity.usermgmt.api.UserManager;
import oracle.iam.identity.usermgmt.vo.User;
import oracle.iam.identity.exception.UserSearchException;
import oracle.iam.identity.usermgmt.api.UserManagerConstants.AttributeName;

//Organization Legacy API's
import Thor.API.Operations.tcOrganizationOperationsIntf;
import Thor.API.tcResultSet;
import Thor.API.Exceptions.tcAPIException;
import Thor.API.Exceptions.tcColumnNotFoundException;
import Thor.API.Exceptions.tcOrganizationNotFoundException;

import oracle.iam.platform.OIMClient;
import oracle.iam.platform.authz.exception.AccessDeniedException;
import oracle.iam.platform.entitymgr.vo.SearchCriteria;

import java.util.*;

import javax.naming.NamingException;
import javax.security.auth.login.LoginException;
public class RequestTest {

	private static OIMClient oimClient;

	/*
	 * Initialize the context and login with client supplied environment
	 */
	public void init() throws LoginException {
		System.out.println("Creating client....");
		Properties props = System.getProperties();
	    Enumeration e = props.propertyNames();
	    /*
	    while (e.hasMoreElements()) {
	      String key = (String) e.nextElement();
	      System.out.println(key + " -- " + props.getProperty(key));
	    }
	    */
	    System.setProperty("java.security.auth.login.config", "/temp/oimclient/conf/authwl.conf");
	    
		String ctxFactory = "weblogic.jndi.WLInitialContextFactory";
		String serverURL = "t3://identity:14000";
		String username = "admin";
		char[] password = "Oracle123".toCharArray();
		Hashtable env = new Hashtable();
		env.put(OIMClient.JAVA_NAMING_FACTORY_INITIAL, ctxFactory);
		env.put(OIMClient.JAVA_NAMING_PROVIDER_URL, serverURL);

		oimClient = new OIMClient(env);
		System.out.println("Logging in");
		oimClient.login(username, password);
		System.out.println("Log in successful");
	}

	/**
	 * Retrieves User login based on the first name using OIM 11g UserManager
	 * service API.
	 */
	public List getUserLogin(String psFirstName) {
		Vector mvUsers = new Vector();
		UserManager userService = oimClient.getService(UserManager.class);
		Set<String> retAttrs = new HashSet<String>();

		// Attributes that should be returned as part of the search.
		// Retrieve "User Login" attribute of the User.
		// Note: Additional attributes can be specified in a
		// similar fashion.
		retAttrs.add(AttributeName.USER_LOGIN.getId());

		// Construct a search criteria. This search criteria states
		// "Find User(s) whose 'First Name' equals 'psFirstName'".
		SearchCriteria criteria;
		criteria = new SearchCriteria(AttributeName.FIRSTNAME.getId(), psFirstName, SearchCriteria.Operator.EQUAL);
		try {
			// Use 'search' method of UserManager API to retrieve
			// records that match the search criteria. The return
			// object is of type User.
			List<User> users = userService.search(criteria, retAttrs, null);

			for (int i = 0; i < users.size(); i++) {
				// Print User First Name and Login ID
				System.out.println("First Name : " + psFirstName + "  --  Login ID : " + users.get(i).getLogin());
				mvUsers.add(users.get(i).getLogin());
			}
		} catch (AccessDeniedException ade) {
			// handle exception
		} catch (UserSearchException use) {
			// handle exception
		}
		return mvUsers;
	}

	/**
	 * Retrieves the administrators of an Organization based on the Organization
	 * name. This is Legacy service API usage.
	 */
	public List getAdministratorsOfOrganization(String psOrganizationName) {
		Vector mvOrganizations = new Vector();
		tcOrganizationOperationsIntf moOrganizationUtility = oimClient.getService(tcOrganizationOperationsIntf.class);
		Hashtable mhSearchCriteria = new Hashtable();
		mhSearchCriteria.put("Organizations.Organization Name", psOrganizationName);
		try {
			tcResultSet moResultSet = moOrganizationUtility.findOrganizations(mhSearchCriteria);
			tcResultSet moAdmins;
			for (int i = 0; i < moResultSet.getRowCount(); i++) {
				moResultSet.goToRow(i);
				moAdmins = moOrganizationUtility.getAdministrators(moResultSet.getLongValue("Organizations.Key"));
				mvOrganizations.add(moAdmins.getStringValue("Groups.Group Name"));
				System.out.println("Organization Admin Name : " + moAdmins.getStringValue("Groups.Group Name"));
			}
		} catch (tcAPIException tce) {
			// handle exception
		} catch (tcColumnNotFoundException cnfe) {
			// handle exception
		} catch (tcOrganizationNotFoundException onfe) {
			// handle exception
		}
		return mvOrganizations;
	}

	/**
	 * Retrieves Role Display Name based on Role name and Role Category using
	 * OIM 11g RoleManager service API. This example shows how to construct
	 * compound search criteria.
	 */
	public List getRoleDisplayName(String roleName, String roleCategory) {
		Vector mvRoles = new Vector();
		RoleManager roleService = oimClient.getService(RoleManager.class);
		Set<String> retAttrs = new HashSet<String>();

		// Attributes that should be returned as part of the search.
		// Retrieve the "Role Display Name" attribute of a Role.
		// Note: Additional attributes can be specified in a
		// similar fashion.
		retAttrs.add(RoleAttributeName.DISPLAY_NAME.getId());

		// Construct the first search criteria. This search criteria
		// states "Find Role(s) whose 'Name' equals 'roleName'".
		SearchCriteria criteria1;
		criteria1 = new SearchCriteria(RoleAttributeName.NAME.getId(), roleName, SearchCriteria.Operator.EQUAL);

		// Construct the second search criteria. This search criteria
		// states "Find Role(s) whose 'category' equals 'roleCategory'".
		SearchCriteria criteria2;
		criteria2 = new SearchCriteria(RoleCategoryAttributeName.NAME.getId(), roleCategory,
				SearchCriteria.Operator.EQUAL);

		// Construct the compound search criteria using 'criteria1' and
		// 'criteria2' as arguments. This showcases how to construct
		// compound search criterias.
		SearchCriteria criteria = new SearchCriteria(criteria1, criteria2, SearchCriteria.Operator.AND);
		try {
			// Use 'search' method of RoleManager API to retrieve
			// records that match the search criteria. The return
			// object is of type Role.
			List<Role> roles = roleService.search(criteria, retAttrs, null);

			for (int i = 0; i < roles.size(); i++) {
				// Print Role Display Name
				System.out.println("Role Display Name : " + roles.get(i).getDisplayName());
				mvRoles.add(roles.get(i).getDisplayName());
			}
		} catch (AccessDeniedException ade) {
			// handle exception
		} catch (RoleSearchException use) {
			// handle exception
		}
		return mvRoles;
	}

	// Main method invocation
	// Following assumptions are made
	// 1. A User "Joe Doe" already exists in OIM
	// 2. An Organization "Example Organization" already exists in OIM
	// 3. A Role "Foobar" already exists in OIM
	public static void main(String args[]) {
		List moList = null;

		try {
			RequestTest oimSample = new RequestTest();

			// initialize resources
			oimSample.init();
			// retrieve User logins with first name 'Joe'
			moList = oimSample.getUserLogin("Joe");
			// retrieve User logins with first names starting with 'J'
			moList = oimSample.getUserLogin("J*");
			// retrieve the administrators of an Organization with name
			// 'Example Organization'
			moList = oimSample.getAdministratorsOfOrganization("Example Organization");
			// retrieve Role display name with role name 'FooBar'
			// and role category as 'Defaut'
			moList = oimSample.getRoleDisplayName("foobar", "Default");
			// release resources
			oimClient.logout();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

