package oracle.iam.samples;

import java.util.List;

import oracle.iam.identity.usermgmt.api.UserManager;
import oracle.iam.identity.usermgmt.vo.User;
import oracle.iam.platform.entitymgr.vo.SearchCriteria;
import oracle.iam.selfservice.uself.uselfmgmt.api.UnauthenticatedSelfService;

//===================================================================
public abstract class ClientUser extends Client
//===================================================================
{
   //----------------------------------------------------------------
   public ClientUser() throws Exception
   //----------------------------------------------------------------
   {
      super();
      return;
   }

   //----------------------------------------------------------------
   protected UserManager getUserManager() throws Exception
   //----------------------------------------------------------------
   {
      UserManager umgr = null;

      umgr = _oimClient.getService(UserManager.class);

      this.log("UserManager ready");

      return umgr;
   }

   //----------------------------------------------------------------
   protected UnauthenticatedSelfService getUnauthSelfService()
   //----------------------------------------------------------------
   {
      UnauthenticatedSelfService unauthn = null;

      unauthn = _oimClient.getService(UnauthenticatedSelfService.class);
      
      this.log("UnauthenticatedSelfService ready");

      return unauthn;
   }
   
   //----------------------------------------------------------------
   public String getUserKey(String userName) throws Exception
   //----------------------------------------------------------------
   {
      String userKey = null;
      UserManager umgr = null;
      User user = null;
      List<User> users = null;
      SearchCriteria criteria = null;
      
      if ( userName == null || userName.length() < 1)
      {
         throw new Exception("userName is null or empty");
      }

      /*
       * Use the "userName" to obtain the internal "userKey"
       */

      umgr = _oimClient.getService(UserManager.class);

      criteria = new SearchCriteria("User Login", userName, SearchCriteria.Operator.EQUAL);

      users = umgr.search(criteria, null, null);

      if ( users == null || users.size() != 1)
      {
         throw new Exception("User Search results list is either null or NOT EQUAL to 1");
      }
      
      user = users.get(0);
      if ( user == null)
      {
         throw new Exception("Returned User object is null");
      }
      
      userKey = user.getId();

      return userKey;
   }

}
