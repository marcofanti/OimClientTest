package iamsamples.provisioning;

import iamsamples.init.Init;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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
import oracle.iam.provisioning.vo.ApplicationInstance;
import oracle.iam.request.vo.Beneficiary;
import oracle.iam.request.vo.RequestBeneficiaryEntity;
import oracle.iam.request.vo.RequestBeneficiaryEntityAttribute;
import oracle.iam.request.vo.RequestConstants;
import oracle.iam.request.vo.RequestData;
import oracle.iam.vo.OperationResult;


public class ProvisionAccount3 {
	private static final String USER_LOGIN = "AA10127"; // User Login
	private static final String RESOURCE_ATTR_NAME = "Objects.Name"; // Attribute Name for Name
	private static final String RESOURCE_ATTR_KEY = "Objects.Key"; // Attribute Name for Key
	private static final String APPLICATION_INSTANCE_NAME = "SecondOUDInstance"; // Attribute Name for Name

	
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
		HashMap <String, String> searchcriteria = new HashMap<String, String>();
		searchcriteria.put("IT Resources.Name", APPLICATION_INSTANCE_NAME);

		tcResultSet resultSet = tcITResourceIntf.findITResourceInstances(searchcriteria);
		long itResourceKey = resultSet.getLongValue("IT Resources.Key");
		System.out.println("IT Resource Key -> " + itResourceKey);

		SearchCriteria criteriaAi = new SearchCriteria(ApplicationInstance.APPINST_NAME,
	            "*", SearchCriteria.Operator.BEGINS_WITH);
		
		List<ApplicationInstance> aiLst = aiSvc.findApplicationInstance(criteriaAi, new HashMap<String, Object>());
		
		Long applicationInstanceKey = 0L;
		for(ApplicationInstance ai : aiLst) {
			if (ai.getApplicationInstanceName().equalsIgnoreCase(APPLICATION_INSTANCE_NAME)) {
				applicationInstanceKey = ai.getApplicationInstanceKey();
				System.out.println("ai name = " + ai.getApplicationInstanceName() + " instance Key " + ai.getApplicationInstanceKey());
				break;
			}
        	
        }

        // Find the user
        SearchCriteria criteria = new SearchCriteria("User Login",
        		USER_LOGIN, SearchCriteria.Operator.EQUAL);
        Set retSet = new HashSet();
        retSet.add("usr_key");
        retSet.add("User Login");
        retSet.add("First Name");
        retSet.add("Last Name");

        List<User> users = usrMgr.search(criteria, retSet, null); 
        User u = users.get(0);
        Long usrKey = (Long)u.getAttribute("usr_key");
        
        System.out.println("usrKey = " + usrKey);

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
		searchMap.put(RESOURCE_ATTR_NAME, "SecondOUDInstance");
		resultSet2 = resourceService.findObjects(searchMap);
		resKey = resultSet2.getLongValue(RESOURCE_ATTR_KEY);
		resourceKey = Long.toString(resKey);
		System.out.println("Resource Key -> " + resourceKey);

		RequestData requestData = new RequestData();

		RequestBeneficiaryEntity requestEntity = new RequestBeneficiaryEntity();
		requestEntity.setRequestEntityType(OIMType.ApplicationInstance);
		requestEntity.setEntitySubType("SecondOUDInstance");
		requestEntity.setEntityKey("" + applicationInstanceKey);
		requestEntity.setOperation(RequestConstants.MODEL_PROVISION_APPLICATION_INSTANCE_OPERATION);

		List<RequestBeneficiaryEntityAttribute> attrs = new ArrayList<RequestBeneficiaryEntityAttribute>();
		RequestBeneficiaryEntityAttribute attr = new RequestBeneficiaryEntityAttribute("User ID", USER_LOGIN + "X",
				RequestBeneficiaryEntityAttribute.TYPE.String);
		attrs.add(attr);
		//attr = new RequestBeneficiaryEntityAttribute("ITResource", 290,
		//		RequestBeneficiaryEntityAttribute.TYPE.Integer);
		attrs.add(attr);
		attr = new RequestBeneficiaryEntityAttribute("Email", USER_LOGIN + "@oracleads.com",
				RequestBeneficiaryEntityAttribute.TYPE.String);
		attrs.add(attr);
		attr = new RequestBeneficiaryEntityAttribute("Password", "Oracle123",
				RequestBeneficiaryEntityAttribute.TYPE.String);
		attrs.add(attr);

		requestEntity.setEntityData(attrs);

		List<RequestBeneficiaryEntity> entities = new ArrayList<RequestBeneficiaryEntity>();
		entities.add(requestEntity);


		beneficiary.setBeneficiaryKey("" + usrKey);
		beneficiary.setBeneficiaryType(Beneficiary.USER_BENEFICIARY);
		beneficiary.setTargetEntities(entities);

		List<Beneficiary> beneficiaries = new ArrayList<Beneficiary>();
		beneficiaries.add(beneficiary);
		requestData.setBeneficiaries(beneficiaries);

		OperationResult result = null;
		result = unifiedService.doOperation(requestData, OIMService.Intent.REQUEST);

		System.out.println("result getOperationStatus= " + result.getOperationStatus());
		System.out.println("result getRequestID= " + result.getRequestID());
		System.out.println("result getOrchestrationResult= " + result.getOrchestrationResult());

        oimClient.logout();
        System.exit(0);
    		
	}
}