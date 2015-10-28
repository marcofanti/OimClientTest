package iamsamples.provisioning;

import iamsamples.init.Init;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import oracle.iam.platform.OIMClient;
import oracle.iam.platform.entitymgr.vo.SearchCriteria;
import oracle.iam.provisioning.api.ApplicationInstanceService;
import oracle.iam.provisioning.vo.ApplicationInstance;

public class GetAllAppInstances {


    public static void main(String[] args) throws Exception {
        OIMClient oimClient = Init.init();

        ApplicationInstanceService aiSvc = oimClient.getService(ApplicationInstanceService.class);

        SearchCriteria criteria = new SearchCriteria(ApplicationInstance.APPINST_NAME,
            "*", SearchCriteria.Operator.BEGINS_WITH);

        List<ApplicationInstance> aiLst = aiSvc.findApplicationInstance(criteria, new HashMap<String, Object>());

        for(ApplicationInstance ai : aiLst) {
        	System.out.println("ai name = " + ai.getApplicationInstanceName());
        }

        oimClient.logout();
        System.exit(0);
    }
}