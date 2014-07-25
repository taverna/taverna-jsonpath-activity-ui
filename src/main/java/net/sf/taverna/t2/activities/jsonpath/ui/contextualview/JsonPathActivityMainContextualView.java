package net.sf.taverna.t2.activities.jsonpath.ui.contextualview;

import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.sf.taverna.t2.activities.jsonpath.JsonPathActivity;
import net.sf.taverna.t2.activities.jsonpath.JsonPathActivityConfigurationBean;
import net.sf.taverna.t2.activities.jsonpath.ui.config.JsonPathActivityConfigureAction;
import net.sf.taverna.t2.workbench.ui.views.contextualviews.ContextualView;

@SuppressWarnings("serial")
/**
 * 
 * @author Sergejs Aleksejevs
 */
public class JsonPathActivityMainContextualView extends ContextualView
{
  private final JsonPathActivity activity;
	
	private JPanel jpMainPanel;
	private JTextField tfJsonPathExpression;
	
	public JsonPathActivityMainContextualView(JsonPathActivity activity) {
	  this.activity = activity;
		initView();
	}
	
	
	@Override
	public JComponent getMainFrame()
	{
		jpMainPanel = new JPanel(new GridBagLayout());
		jpMainPanel.setBorder(
		    BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(4, 2, 4, 2),
            BorderFactory.createEmptyBorder()
//            BorderFactory.createLineBorder(ColourManager.getInstance().getPreferredColour(JsonPathActivity.class.getCanonicalName()), 2)  // makes a thin border with the colour of the processor
        ));
		
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		c.weighty = 0;
		
		
		// --- JsonPath Expression ---
		
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(5, 5, 5, 5);
		JLabel jlJsonPathExpression = new JLabel("JsonPath Expression:");
		jlJsonPathExpression.setFont(jlJsonPathExpression.getFont().deriveFont(Font.BOLD));
		jpMainPanel.add(jlJsonPathExpression, c);
		
		c.gridx++;
		c.weightx = 1.0;
		tfJsonPathExpression = new JTextField();
		tfJsonPathExpression.setEditable(false);
		jpMainPanel.add(tfJsonPathExpression, c);
		
		// populate the view with values
		refreshView();
		
		return jpMainPanel;
	}

	@Override
	/**
	 * This is the title of the contextual view - shown in the list of other available
	 * views (even when this contextual view is collapsed).
	 */
	public String getViewTitle() {
		JsonPathActivityConfigurationBean configuration = activity.getConfiguration();
		return "JsonPath Service Details";
	}

	/**
	 * Typically called when the activity configuration has changed.
	 */
	@Override
	public void refreshView()
	{
		JsonPathActivityConfigurationBean configBean = activity.getConfiguration();
		
		// Set JsonPath Expression
		tfJsonPathExpression.setText("" + configBean.getJsonPathAsString());
	}

	/**
	 * View position hint
	 */
	@Override
	public int getPreferredPosition() {
		// want to be on top, as it's the main contextual view for this activity
		return 100;
	}
	
	@Override
	public Action getConfigureAction(final Frame owner) {
	  // "Configure" button appears because of this action being returned
		return new JsonPathActivityConfigureAction(activity, owner);
	}

}
