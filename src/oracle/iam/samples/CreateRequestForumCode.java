package oracle.iam.samples;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import oracle.iam.platform.utils.vo.OIMType;
import oracle.iam.request.vo.Beneficiary;
import oracle.iam.request.vo.RequestBeneficiaryEntity;
import oracle.iam.request.vo.RequestBeneficiaryEntityAttribute;
import oracle.iam.request.vo.RequestConstants;
import oracle.iam.request.vo.RequestData;
import oracle.iam.request.vo.RequestEntity;
import oracle.iam.request.vo.RequestEntityAttribute;
import Thor.API.tcResultSet;
import Thor.API.Operations.tcITResourceInstanceOperationsIntf;
import Thor.API.Operations.tcObjectOperationsIntf;

public class CreateRequestForumCode extends ClientRequest {
	private static final String RESOURCE_NAME = "LDAP User"; // Resource Name
	private static final String IT_RESOURCE_NAME = "LDAP User"; // Resource Name
	private static final String RESOURCE_ATTR_NAME = "Objects.Name"; // Attribute Name for Name
	private static final String RESOURCE_ATTR_KEY = "Objects.Key"; // Attribute Name for Key
	private static final String USER_KEY = "33"; // Key to AA10127 User record
	private static final String TEMPLATE = "Provision Resource"; // OIM Request Type
	private tcObjectOperationsIntf _resourceService = null; // Thor API
	private static tcITResourceInstanceOperationsIntf tcITResourceIntf = null;
	private static tcObjectOperationsIntf resourceService = null;

	public CreateRequestForumCode() throws Exception {
		super();
		_resourceService = _oimClient.getService(tcObjectOperationsIntf.class);
		tcITResourceIntf = _oimClient.getService(tcITResourceInstanceOperationsIntf.class);
		return;
	}

	protected void execute() throws Exception {
		
		long resKey = 0L;

		String str = null;
		String resourceKey = null;
		Map<String, String> searchMap = new HashMap<String, String>();
		tcResultSet resultSet = null; 

		Beneficiary beneficiary = new Beneficiary();
		List<RequestBeneficiaryEntityAttribute> entityAttrList = new ArrayList<RequestBeneficiaryEntityAttribute>();
		RequestBeneficiaryEntity entity = new RequestBeneficiaryEntity(); 
		List<Beneficiary> beneficiaryList = null; // OIMClient
		List<RequestBeneficiaryEntity> entityList = null; // OIMClient
		
		/*
		 * Need to get IT Resource Key
		 */
		HashMap searchcriteria = new HashMap<String, String>();
		searchcriteria.put("IT Resources.Name", "Enterprise Dir OUD");

		resultSet = tcITResourceIntf.findITResourceInstances(searchcriteria);
		long itResourceKey = resultSet.getLongValue("IT Resources.Key");
		System.out.println("IT Resource Key -> " + itResourceKey);

		
		/*
		 *  Need to get the Resource Key using the Resource Name
		 */
		searchMap.put(RESOURCE_ATTR_NAME, RESOURCE_NAME);
		resultSet = _resourceService.findObjects(searchMap);
		resKey = resultSet.getLongValue(RESOURCE_ATTR_KEY);
		resourceKey = Long.toString(resKey);
		System.out.println("Resource Key -> " + resourceKey);

		
		entityAttrList.add(this.getAttr(IT_RESOURCE_NAME, itResourceKey));
		entityAttrList.add(this.getAttr("User ID", "AA10127"));
		entityAttrList.add(this.getAttr("Password", "Oracle123"));
		entity.setEntityKey(resourceKey);
		entity.setEntityType(RequestConstants.APPLICATION_INSTANCE);
		entity.setEntitySubType(RESOURCE_NAME);
		entity.setEntityData(entityAttrList);
		
		entityList = new ArrayList<RequestBeneficiaryEntity>();
		entityList.add(entity);
		
		beneficiary.setBeneficiaryType(Beneficiary.USER_BENEFICIARY);
		beneficiary.setBeneficiaryKey(USER_KEY);
		
		beneficiary.setTargetEntities(entityList);
		beneficiaryList = new ArrayList<Beneficiary>();
		beneficiaryList.add(beneficiary);
		
		
		RequestData requestData = new RequestData();
		requestData.setRequestTemplateName("Provision Resource");
		requestData.setJustification("testing");
		requestData.setBeneficiaries(beneficiaryList); 
		
		_service.validateRequestData(requestData);

		String result = _service.submitRequest(requestData);
		System.out.println("Value from submitRequest : " + (result != null ? result : "EMPTY"));
	}
	
	public static void main(String[] args) throws Exception
	// ----------------------------------------------------------------
	{
		CreateRequestForumCode test = new CreateRequestForumCode();

		try {
			test.execute();
		} catch (Exception ex) {
			ex.printStackTrace();
			test.log("EXCEPTION: " + ex.getMessage());
		}
		return;
	}
}


//RequestEntity entity2 = new RequestEntity();
//entity2.
//RequestEntityAttribute entityAttribute= new RequestEntityAttribute();
//entityAttribute.setEntity(entity2);
//List<RequestEntityAttribute> entityAttributes = new ArrayList<RequestEntityAttribute>();

//RequestEntity requestEntity = new RequestEntity();
//requestEntity.setEntityData(entityAttributes);
//List<RequestEntity> targetEntities = new ArrayList<RequestEntity>();
//requestData.setTargetEntities(targetEntities);

