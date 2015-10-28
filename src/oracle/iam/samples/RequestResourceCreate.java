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

import Thor.API.tcResultSet;
import Thor.API.Operations.tcObjectOperationsIntf;

//===================================================================
public class RequestResourceCreate extends ClientRequest {
	// ===================================================================
	private static final String RESOURCE = "LDAP User"; // Resource Name "AD User"
	private static final String RESOURCE_ATTR_NAME = "Objects.Name"; // Attribute Name for Name
	private static final String RESOURCE_ATTR_KEY = "Objects.Key"; // Attribute Name for Key
	private static final String USER_KEY = "33"; // Key to AA10127 User record
	private static final String TEMPLATE = "Provision Resource"; // OIM Request
																	// Type
	private tcObjectOperationsIntf _resourceService = null; // Thor API

	public RequestResourceCreate() throws Exception {
		super();

		_resourceService = _oimClient.getService(tcObjectOperationsIntf.class);

		return;
	}

	public static void main(String[] args) throws Exception
	// ----------------------------------------------------------------
	{
		RequestResourceCreate test = new RequestResourceCreate();

		try

		{

			test.execute();

		}

		catch (Exception ex)

		{

			test.log("EXCEPTION: " + ex.getMessage());

		}

		return;
	}

	// ----------------------------------------------------------------
	@Override

	protected void execute() throws Exception

	// ----------------------------------------------------------------

	{

		long resKey = 0L;

		String str = null;

		String resourceKey = null;

		Map<String, String> searchMap = new HashMap<String, String>();

		tcResultSet resultSet = null; // Thor

		RequestData requestData = null; // OIMClient

		Beneficiary beneficiary = null; // OIMClient

		RequestBeneficiaryEntity entity = null; // OIMClient

		List<Beneficiary> beneficiaryList = null; // OIMClient

		List<RequestBeneficiaryEntity> entityList = null; // OIMClient

		List<RequestBeneficiaryEntityAttribute> entityAttrList = null; // OIMClient

		/*
		 *  Need to get the Resource Key using the Resource Name
		 */

		searchMap.put(RESOURCE_ATTR_NAME, RESOURCE);

		resultSet = _resourceService.findObjects(searchMap);

		resKey = resultSet.getLongValue(RESOURCE_ATTR_KEY);

		resourceKey = Long.toString(resKey);
		
		System.out.println("Resource Key -> " + resourceKey);

		entityAttrList = new ArrayList<RequestBeneficiaryEntityAttribute>();

		// entityAttrList.add(this.getAttr("AD Server", new Long(24)));

		// entityAttrList.add(this.getAttr("First Name", "John"));

		// entityAttrList.add(this.getAttr("Last Name", "Doe"));

		entityAttrList.add(this.getAttr("User ID", "AA10127"));

		entityAttrList.add(this.getAttr("Password", "Oracle123"));

		//
		 // 166. Create a List of BeneficiaryEntities 167. Add "the"
		 // BeneficiaryEntity to the List 168.
		 //

		entityList = new ArrayList<RequestBeneficiaryEntity>();
		entityList.add(entity);

		//
		 // Set the beneficiaries .. Users that will "get" the resource (if
		 // approved) 
		// Create a Beneficiary object for each "user" 176. Add
		 // the "List of Entity Attributes" to the Beneficiary 177.
		 //
		 // 178. Then, add the Beneficiary to the "List" of Beneficiaries 179.
		 //
		beneficiary = new Beneficiary();
		beneficiary.setBeneficiaryType(Beneficiary.USER_BENEFICIARY);
		beneficiary.setBeneficiaryKey(USER_KEY);
		
		//
		 // Create beneficiary entity.
		 // Set the Key (for the Resource),
		 // Type and SubType Add the "List" of attributes 157.
		 //

		entity = new RequestBeneficiaryEntity();
		entity.setRequestEntityType(OIMType.ApplicationInstance);
		entity.setEntityKey(resourceKey); // the Key to the "AD User" Resource
		entity.setEntityType(RequestConstants.RESOURCE);
		entity.setEntitySubType(RESOURCE); // the Name of the Resource
		entity.setEntityData(entityAttrList);
		entity.setOperation(RequestConstants.MODEL_PROVISION_APPLICATION_INSTANCE_OPERATION);

		
		
		beneficiary.setTargetEntities(entityList);
		beneficiaryList = new ArrayList<Beneficiary>();
		beneficiaryList.add(beneficiary);

		//
		 //  Set the template, in this case, it will be the same as the
		 // Request Type Add the beneficiaries to the request data 192.
		 //

		requestData = new RequestData();
		requestData.setRequestTemplateName(TEMPLATE);
		requestData.setJustification("I want this Resource");
		requestData.setBeneficiaries(beneficiaryList);

		//
		 // 200. Submit the Request 201.
		 //
		try {
		str = _service.submitRequest(requestData);
		} catch (Exception e) {
			e.printStackTrace();
		}

		this.log("Value from submitRequest : '" + (str != null ? str : NULL) + "'");

		return;

	}

}