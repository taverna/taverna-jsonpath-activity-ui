package net.sf.taverna.t2.activities.jsonpath.ui.servicedescription;

import javax.swing.Icon;

import net.sf.taverna.t2.activities.jsonpath.JsonPathActivity;
import net.sf.taverna.t2.activities.jsonpath.JsonPathActivityConfigurationBean;
import net.sf.taverna.t2.servicedescriptions.AbstractTemplateService;
import net.sf.taverna.t2.servicedescriptions.ServiceDescription;

/**
 * 
 * @author Sergejs Aleksejevs
 */
public class JsonPathTemplateService extends AbstractTemplateService<JsonPathActivityConfigurationBean>
{
  public JsonPathTemplateService ()
  {
    super();
  }
  
  @Override
  public Class<JsonPathActivity> getActivityClass() {
    return JsonPathActivity.class;
  }
  
  @Override
  /**
   * Default values for this template service are provided in this method.
   */
  public JsonPathActivityConfigurationBean getActivityConfiguration()
  {
    return (JsonPathActivityConfigurationBean.getDefaultInstance());
  }
  
  @Override
  public Icon getIcon() {
    return JsonPathActivityIcon.getJsonPathActivityIcon();
  }
  
  public String getName() {
    return "JsonPath";
  }
  
  public String getDescription() {
    return "Service for point-and-click creation of JsonPath expressions for JSON data";
  }
  
	@SuppressWarnings("unchecked")
	public static ServiceDescription getServiceDescription() {
		JsonPathTemplateService gts = new JsonPathTemplateService();
		return gts.templateService;
	}


  public String getId() {
    return "http://www.taverna.org.uk/2010/services/xpath";
  }
  
}
