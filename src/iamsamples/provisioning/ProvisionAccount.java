package iamsamples.provisioning;

import iamsamples.init.Init;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import Thor.API.tcResultSet;
import Thor.API.Operations.tcITResourceInstanceOperationsIntf;
import oracle.iam.identity.usermgmt.api.UserManager;
import oracle.iam.identity.usermgmt.vo.User;
import oracle.iam.platform.OIMClient;
import oracle.iam.platform.entitymgr.vo.SearchCriteria;
import oracle.iam.provisioning.api.ApplicationInstanceService;
import oracle.iam.provisioning.api.ProvisioningService;
import oracle.iam.provisioning.vo.Account;
import oracle.iam.provisioning.vo.AccountData;
import oracle.iam.provisioning.vo.ApplicationInstance;
import oracle.iam.provisioning.vo.FormField;

public class ProvisionAccount {
	public static void main(String[] args) throws Exception {

        OIMClient oimClient = Init.init();

        ApplicationInstanceService aiSvc = oimClient.getService(ApplicationInstanceService.class);
        ProvisioningService provSvc = oimClient.getService(ProvisioningService.class);
        UserManager usrMgr = oimClient.getService(UserManager.class);
        tcITResourceInstanceOperationsIntf tcITResourceIntf = oimClient.getService(tcITResourceInstanceOperationsIntf.class);
        
		/*
		 * Need to get IT Resource Key
		 */
		HashMap searchcriteria = new HashMap<String, String>();
		searchcriteria.put("IT Resources.Name", "Cellphone");

		tcResultSet resultSet = tcITResourceIntf.findITResourceInstances(searchcriteria);
		long itResourceKey = resultSet.getLongValue("IT Resources.Key");
		System.out.println("IT Resource Key -> " + itResourceKey);


        // Find the user
        SearchCriteria criteria = new SearchCriteria("User Login",
                "AA10127", SearchCriteria.Operator.EQUAL);
        Set retSet = new HashSet();
        retSet.add("usr_key");
        retSet.add("User Login");
        retSet.add("First Name");
        retSet.add("Last Name");

        List<User> users = usrMgr.search(criteria, retSet, null); 
        User u = users.get(0);

        ApplicationInstance ai = aiSvc.findApplicationInstanceByName("Cellphone");

        HashMap<String, Object> parentData = new HashMap<String, Object>();
        
        List<FormField> fields = ai.getAccountForm().getFormFields();
        for (FormField f : fields) {
        	System.out.println(f.getLabel() + ":" + f.getName() + " " + f.getType() + " " + f.getVariantType());
        	if (f.getLabel().equals("ITResource")) {
        		continue;
        		//parentData.put(f.getName(), "" + itResourceKey);
        	} else if(f.getType().equals("DateFieldDlg")) {
        		continue;
        	} else if (f.getLabel().equals("Account Login")) {
        		parentData.put(f.getName(), u.getLogin());
        	} else if (f.getLabel().equals("Password")) {
        		parentData.put(f.getName(), "Oracle123");
        	} else if (f.getLabel().equals("Account ID")) {
        		parentData.put(f.getName(), "X" + u.getLogin());
        	}
        }
        
        AccountData accountData = new AccountData(ai.getAccountForm().getFormKey() + "", "", parentData);
        Account account = new Account(ai, accountData);
        
        System.out.println("account " + account.getProcessInstanceKey());
        
        provSvc.provision(u.getEntityId(), account);

        oimClient.logout();
        System.exit(0);
    		
	}
}