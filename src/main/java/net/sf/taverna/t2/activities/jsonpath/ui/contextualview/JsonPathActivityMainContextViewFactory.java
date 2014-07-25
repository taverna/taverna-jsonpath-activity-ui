package net.sf.taverna.t2.activities.jsonpath.ui.contextualview;

import java.util.Arrays;
import java.util.List;

import net.sf.taverna.t2.activities.jsonpath.JsonPathActivity;
import net.sf.taverna.t2.workbench.ui.views.contextualviews.ContextualView;
import net.sf.taverna.t2.workbench.ui.views.contextualviews.activity.ContextualViewFactory;

/**
 * 
 * @author Sergejs Aleksejevs
 */
public class JsonPathActivityMainContextViewFactory implements
		ContextualViewFactory<JsonPathActivity>
{

	public boolean canHandle(Object selection) {
		return selection instanceof JsonPathActivity;
	}

	public List<ContextualView> getViews(JsonPathActivity selection) {
		return Arrays.<ContextualView>asList(new JsonPathActivityMainContextualView(selection));
	}
	
}
