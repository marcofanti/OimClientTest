package oracle.iam.samples;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import oracle.iam.identity.usermgmt.api.UserManager;
import oracle.iam.identity.usermgmt.vo.User;
import oracle.iam.platform.entitymgr.vo.SearchCriteria;


//===================================================================
public class UserSearch extends ClientUser
//===================================================================
{
   //----------------------------------------------------------------
   public UserSearch() throws Exception
   //----------------------------------------------------------------
   {
      super();
      return;
   }

   //----------------------------------------------------------------
   public static void main(String[] args) throws Exception
   //----------------------------------------------------------------
   {

      UserSearch test = new UserSearch();

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

   //----------------------------------------------------------------
   protected void execute() throws Exception
   //----------------------------------------------------------------
   {
      Object val = null;
      StringBuilder buf = null;
      List<User> users = null;
      HashMap<String, Object> attributes = null;
      HashMap<String, Object> parameters = null;
      Set<String> keys = null;
      Set<String> attrNames = null;

      UserManager umgr = null;        // OIMClient API
      SearchCriteria criteria = null; // OIMClient API

      this.log("__BEGIN__");

      umgr = this.getUserManager();

      /*
       * Create a SearchCriteria
       */

//      criteria = new SearchCriteria("First Name", "John", SearchCriteria.Operator.EQUAL);
//      criteria = new SearchCriteria("Email", "John.Wayne@openptk.org", SearchCriteria.Operator.EQUAL);
//      criteria = new SearchCriteria("First Name", "scott", SearchCriteria.Operator.EQUAL);
//      criteria = new SearchCriteria("usr_key", "*", SearchCriteria.Operator.EQUAL);
      criteria = new SearchCriteria("User Login", "AA10127", SearchCriteria.Operator.EQUAL);

      /*
       * What attributes to return
       * if null / empty, all attributes are returned
       */

      attrNames = new HashSet<String>();
      attrNames.add("User Login");
//      attrNames.add("First Name");
//      attrNames.add("Last Name");
//      attrNames.add("Email");
//      attrNames.add("Xellerate Type");
//      attrNames.add("Role");

      /*
       * Parameter map: determine how many rows to return, how to sort results
       * can be empty / null ... all data, default internal sorting
       */

//      mapParams = new HashMap<String, Object>();
//      mapParams.put("STARTROW", 0);
//      mapParams.put("ENDROW", -1);
//      mapParams.put("SORTEDBY", "Last Name");
//      mapParams.put("SORTORDER", "SortOrder.ASCENDING");

      /*
       * run the search
       */

      users = umgr.search(criteria, attrNames, parameters);

      /*
       * Display the results
       */

      if (users != null && !users.isEmpty())
      {
         this.log("search results, quantity=" + users.size());
         for (User user : users)
         {
            attributes = user.getAttributes();
            buf = new StringBuilder();

            keys = attributes.keySet();
            for (String key : keys)
            {
               val = attributes.get(key);
               buf.append(key).append("='").append(val).append("', ");

            }
            this.log("EntityId: " + user.getEntityId()
               + ", Id: " + user.getId()
               + ", Attributes: " + buf.toString());
         }
      }
      else
      {
         this.log("search result is empty");
      }

      this.log("__END__");

      return;
   }
}
