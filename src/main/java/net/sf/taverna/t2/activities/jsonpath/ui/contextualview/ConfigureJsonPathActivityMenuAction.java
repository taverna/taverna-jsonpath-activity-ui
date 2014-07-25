package net.sf.taverna.t2.activities.jsonpath.ui.contextualview;

import javax.swing.Action;

import net.sf.taverna.t2.activities.jsonpath.JsonPathActivity;
import net.sf.taverna.t2.activities.jsonpath.ui.config.JsonPathActivityConfigureAction;
import net.sf.taverna.t2.workbench.activitytools.AbstractConfigureActivityMenuAction;

/**
 * This action is responsible for enabling the contextual menu entry
 * on processors that perform JsonPathActivity'ies.
 * 
 * NB! As a side-effect this also enables the pop-up with for configuration
 * of the processor when it is added to the workflow from the Service Panel. 
 * 
 * @author Sergejs Aleksejevs
 */
public class ConfigureJsonPathActivityMenuAction extends
    AbstractConfigureActivityMenuAction<JsonPathActivity>
{

  public ConfigureJsonPathActivityMenuAction() {
    super(JsonPathActivity.class);
  }
  
  @Override
  protected Action createAction()
  {
    JsonPathActivityConfigureAction configAction = new JsonPathActivityConfigureAction(
        findActivity(), getParentFrame());
    configAction.putValue(Action.NAME, "Configure JsonPath service");
    addMenuDots(configAction);
    return configAction;
  }

}
