package net.sf.taverna.t2.activities.jsonpath.ui.config;

import java.awt.Frame;
import java.awt.event.ActionEvent;

import net.sf.taverna.t2.activities.jsonpath.JsonPathActivity;
import net.sf.taverna.t2.activities.jsonpath.JsonPathActivityConfigurationBean;
import net.sf.taverna.t2.workbench.ui.actions.activity.ActivityConfigurationAction;
import net.sf.taverna.t2.workbench.ui.views.contextualviews.activity.ActivityConfigurationDialog;

@SuppressWarnings("serial")
/**
 * @author Sergejs Aleksejevs
 */
public class JsonPathActivityConfigureAction extends ActivityConfigurationAction<JsonPathActivity, JsonPathActivityConfigurationBean>
{

	public JsonPathActivityConfigureAction(JsonPathActivity activity, Frame owner) {
		super(activity);
	}

	@SuppressWarnings("unchecked")
	public void actionPerformed(ActionEvent e)
	{
		ActivityConfigurationDialog<JsonPathActivity,JsonPathActivityConfigurationBean> currentDialog =
		  ActivityConfigurationAction.getDialog(getActivity());
		
		if (currentDialog != null) {
			currentDialog.toFront();
			return;
		}
		
		JsonPathActivityConfigurationPanelProvider panel = new JsonPathActivityConfigurationPanelProvider(getActivity());
		ActivityConfigurationDialog<JsonPathActivity, JsonPathActivityConfigurationBean> dialog =
		  new ActivityConfigurationDialog<JsonPathActivity, JsonPathActivityConfigurationBean>(getActivity(), panel);

		ActivityConfigurationAction.setDialog(getActivity(), dialog);
	}

}
