package oracle.iam.samples;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import oracle.iam.api.OIMService;
import oracle.iam.platform.utils.vo.OIMType;
import oracle.iam.request.vo.Beneficiary;
import oracle.iam.request.vo.RequestBeneficiaryEntity;
import oracle.iam.request.vo.RequestBeneficiaryEntityAttribute;
import oracle.iam.request.vo.RequestConstants;
import oracle.iam.request.vo.RequestData;
import oracle.iam.request.vo.RequestEntity;
import oracle.iam.request.vo.RequestEntityAttribute;
import oracle.iam.vo.OperationResult;
import Thor.API.tcResultSet;
import Thor.API.Operations.tcITResourceInstanceOperationsIntf;
import Thor.API.Operations.tcObjectOperationsIntf;

public class AmolTest extends ClientRequest {
	private static final String RESOURCE_NAME = "LDAP User"; // Resource Name
	private static final String IT_RESOURCE_NAME = "LDAP User"; // Resource Name
	private static final String RESOURCE_ATTR_NAME = "Objects.Name"; // Attribute Name for Name
	private static final String RESOURCE_ATTR_KEY = "Objects.Key"; // Attribute Name for Key
	private static final String USER_KEY = "33"; // Key to AA10127 User record
	private static final String TEMPLATE = "Provision Resource"; // OIM Request
																	// Type
	private tcObjectOperationsIntf _resourceService = null; // Thor API
	private static tcITResourceInstanceOperationsIntf tcITResourceIntf = null;
	private static tcObjectOperationsIntf resourceService = null;
	private static OIMService unifiedService = null;

	public AmolTest() throws Exception {
		super();
		_resourceService = _oimClient.getService(tcObjectOperationsIntf.class);
		tcITResourceIntf = _oimClient.getService(tcITResourceInstanceOperationsIntf.class);
		unifiedService = _oimClient.getService(OIMService.class);
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
		 * Need to get the Resource Key using the Resource Name
		 */
		searchMap.put(RESOURCE_ATTR_NAME, RESOURCE_NAME);
		resultSet = _resourceService.findObjects(searchMap);
		resKey = resultSet.getLongValue(RESOURCE_ATTR_KEY);
		resourceKey = Long.toString(resKey);
		System.out.println("Resource Key -> " + resourceKey);

		RequestData requestData = new RequestData();

		RequestBeneficiaryEntity requestEntity = new RequestBeneficiaryEntity();
		requestEntity.setRequestEntityType(OIMType.ApplicationInstance);
		requestEntity.setEntitySubType("Cellphone");
		requestEntity.setEntityKey("213");
		requestEntity.setOperation(RequestConstants.MODEL_PROVISION_APPLICATION_INSTANCE_OPERATION);

		List<RequestBeneficiaryEntityAttribute> attrs = new ArrayList<RequestBeneficiaryEntityAttribute>();
		RequestBeneficiaryEntityAttribute attr = new RequestBeneficiaryEntityAttribute("User ID", "AA10127",
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

		OperationResult result = unifiedService.doOperation(requestData, OIMService.Intent.REQUEST);

		System.out.println("result = " + result.toString());
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

// RequestEntity entity2 = new RequestEntity();
// entity2.
// RequestEntityAttribute entityAttribute= new RequestEntityAttribute();
// entityAttribute.setEntity(entity2);
// List<RequestEntityAttribute> entityAttributes = new
// ArrayList<RequestEntityAttribute>();

// RequestEntity requestEntity = new RequestEntity();
// requestEntity.setEntityData(entityAttributes);
// List<RequestEntity> targetEntities = new ArrayList<RequestEntity>();
// requestData.setTargetEntities(targetEntities);
