package oracle.iam.samples;


import java.util.List;

//import oim.client.Client;

import oracle.iam.catalog.api.CatalogService;
import oracle.iam.catalog.vo.Catalog;
import oracle.iam.catalog.vo.CatalogSearchCriteria;
import oracle.iam.catalog.vo.CatalogSearchResult;
import oracle.iam.request.api.RequestService;
import oracle.iam.request.vo.RequestBeneficiaryEntityAttribute;


//===================================================================
public abstract class ClientRequest extends Client
//===================================================================
{

   protected RequestService _service = null;

   //----------------------------------------------------------------
   public ClientRequest() throws Exception
   //----------------------------------------------------------------
   {
      super();
      _service = _oimClient.getService(RequestService.class);
      return;
   }

   //----------------------------------------------------------------
   protected RequestBeneficiaryEntityAttribute getAttr(String name, String value)
   //----------------------------------------------------------------
   {
      RequestBeneficiaryEntityAttribute attr = null;

      attr = new RequestBeneficiaryEntityAttribute(name, value,
         RequestBeneficiaryEntityAttribute.TYPE.String);

      return attr;
   }

   //----------------------------------------------------------------
   protected RequestBeneficiaryEntityAttribute getAttr(String name, Long value)
   //----------------------------------------------------------------
   {
      RequestBeneficiaryEntityAttribute attr = null;

      attr = new RequestBeneficiaryEntityAttribute(name, value,
         RequestBeneficiaryEntityAttribute.TYPE.Long);

      return attr;
   }

   //----------------------------------------------------------------
   public Catalog getCatalog(String searchTag, String category) throws Exception
   //----------------------------------------------------------------
   {
      Catalog catalog = null;
      CatalogService catalogService = null;
      CatalogSearchCriteria csc1 = null;
      CatalogSearchCriteria csc2 = null;
      CatalogSearchCriteria csc = null;
      CatalogSearchResult searchResults = null;
      List<Catalog> catalogs = null;

      csc1 = new CatalogSearchCriteria(CatalogSearchCriteria.Argument.TAG, searchTag, CatalogSearchCriteria.Operator.EQUAL);
      csc2 = new CatalogSearchCriteria(CatalogSearchCriteria.Argument.CATEGORY, category, CatalogSearchCriteria.Operator.EQUAL);
      csc = new CatalogSearchCriteria(csc1, csc2, CatalogSearchCriteria.Operator.AND);

      catalogService = _oimClient.getService(CatalogService.class);

      searchResults = catalogService.search(csc, 1, 10, "CATALOG_ID", CatalogSearchCriteria.SortCriteria.ASCENDING);

      catalogs = searchResults.getCatalogs();

      /*
       * The catalogs List should only contain one item (based on the search criteria)
       */

      if (catalogs != null && catalogs.size() == 1)
      {
         catalog = catalogs.get(0);
         if (catalog == null)
         {
            throw new Exception("Catalog Item is null, for searchTag '" + searchTag + "'");
         }
      }
      else
      {
         throw new Exception("Catalog List is null or has more than one item");
      }

      return catalog;
   }
}
