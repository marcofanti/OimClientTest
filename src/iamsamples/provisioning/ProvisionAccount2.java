package iamsamples.provisioning;

import iamsamples.init.Init;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import Thor.API.tcResultSet;
import Thor.API.Operations.tcITResourceInstanceOperationsIntf;
import Thor.API.Operations.tcObjectOperationsIntf;
import oracle.iam.api.OIMService;
import oracle.iam.identity.usermgmt.api.UserManager;
import oracle.iam.identity.usermgmt.vo.User;
import oracle.iam.platform.OIMClient;
import oracle.iam.platform.entitymgr.vo.SearchCriteria;
import oracle.iam.platform.utils.vo.OIMType;
import oracle.iam.provisioning.api.ApplicationInstanceService;
import oracle.iam.provisioning.api.ProvisioningService;
import oracle.iam.provisioning.vo.Account;
import oracle.iam.provisioning.vo.AccountData;
import oracle.iam.provisioning.vo.ApplicationInstance;
import oracle.iam.provisioning.vo.FormField;
import oracle.iam.request.vo.Beneficiary;
import oracle.iam.request.vo.RequestBeneficiaryEntity;
import oracle.iam.request.vo.RequestBeneficiaryEntityAttribute;
import oracle.iam.request.vo.RequestConstants;
import oracle.iam.request.vo.RequestData;
import oracle.iam.vo.OperationResult;


public class ProvisionAccount2 {
	private static final String RESOURCE_ATTR_NAME = "Objects.Name"; // Attribute Name for Name
	private static final String RESOURCE_ATTR_KEY = "Objects.Key"; // Attribute Name for Key

	
	public static void main(String[] args) throws Exception {

        OIMClient oimClient = Init.init();

        ApplicationInstanceService aiSvc = oimClient.getService(ApplicationInstanceService.class);
        ProvisioningService provSvc = oimClient.getService(ProvisioningService.class);
        UserManager usrMgr = oimClient.getService(UserManager.class);
        tcITResourceInstanceOperationsIntf tcITResourceIntf = oimClient.getService(tcITResourceInstanceOperationsIntf.class);
        tcObjectOperationsIntf resourceService = oimClient.getService(tcObjectOperationsIntf.class);
        OIMService unifiedService = oimClient.getService(OIMService.class);
        
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

        long resKey = 0L;

		String str = null;
		String resourceKey = null;
		Map<String, String> searchMap = new HashMap<String, String>();
		tcResultSet resultSet2 = null;

		Beneficiary beneficiary = new Beneficiary();
		List<RequestBeneficiaryEntityAttribute> entityAttrList = new ArrayList<RequestBeneficiaryEntityAttribute>();
		RequestBeneficiaryEntity entity = new RequestBeneficiaryEntity();
		List<Beneficiary> beneficiaryList = null; // OIMClient
		List<RequestBeneficiaryEntity> entityList = null; // OIMClient

		/*
		 * Need to get the Resource Key using the Resource Name
		 */
		searchMap.put(RESOURCE_ATTR_NAME, "Cellphone");
		resultSet2 = resourceService.findObjects(searchMap);
		resKey = resultSet2.getLongValue(RESOURCE_ATTR_KEY);
		resourceKey = Long.toString(resKey);
		System.out.println("Resource Key -> " + resourceKey);

		tryLoop:
		for (int i = 125; i < 1000; i++) {
		RequestData requestData = new RequestData();

		RequestBeneficiaryEntity requestEntity = new RequestBeneficiaryEntity();
		requestEntity.setRequestEntityType(OIMType.ApplicationInstance);
		requestEntity.setEntitySubType("Cellphone");
		requestEntity.setEntityKey("" + i);
		requestEntity.setOperation(RequestConstants.MODEL_PROVISION_APPLICATION_INSTANCE_OPERATION);

		List<RequestBeneficiaryEntityAttribute> attrs = new ArrayList<RequestBeneficiaryEntityAttribute>();
		RequestBeneficiaryEntityAttribute attr = new RequestBeneficiaryEntityAttribute("Account Login", "AA10127",
				RequestBeneficiaryEntityAttribute.TYPE.String);
		attrs.add(attr);
		attr = new RequestBeneficiaryEntityAttribute("ITResource", 290,
				RequestBeneficiaryEntityAttribute.TYPE.Integer);
		attrs.add(attr);
		attr = new RequestBeneficiaryEntityAttribute("Account ID", "AA10127",
				RequestBeneficiaryEntityAttribute.TYPE.String);
		attrs.add(attr);
		attr = new RequestBeneficiaryEntityAttribute("Password", "Oracle123",
				RequestBeneficiaryEntityAttribute.TYPE.String);
		attrs.add(attr);

		requestEntity.setEntityData(attrs);

		List<RequestBeneficiaryEntity> entities = new ArrayList<RequestBeneficiaryEntity>();
		entities.add(requestEntity);

		String userKey = "33";
		beneficiary.setBeneficiaryKey(userKey);
		beneficiary.setBeneficiaryType(Beneficiary.USER_BENEFICIARY);
		beneficiary.setTargetEntities(entities);

		List<Beneficiary> beneficiaries = new ArrayList<Beneficiary>();
		beneficiaries.add(beneficiary);
		requestData.setBeneficiaries(beneficiaries);

		OperationResult result = null;
		try {
		result = unifiedService.doOperation(requestData, OIMService.Intent.REQUEST);
		} catch (Exception e) {
			continue tryLoop;
		}

		System.out.println("result getOperationStatus= " + result.getOperationStatus());
		System.out.println("result getRequestID= " + result.getRequestID());
		System.out.println("result getOrchestrationResult= " + result.getOrchestrationResult());
		System.out.println("result = " + result.toString() + "---->" + i);
		break tryLoop;
		}
        oimClient.logout();
        System.exit(0);
    		
	}
}