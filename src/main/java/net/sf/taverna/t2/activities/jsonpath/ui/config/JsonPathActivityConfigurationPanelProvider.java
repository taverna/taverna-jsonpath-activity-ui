package net.sf.taverna.t2.activities.jsonpath.ui.config;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JOptionPane;

import net.sf.taverna.t2.activities.jsonpath.JsonPathActivity;
import net.sf.taverna.t2.activities.jsonpath.JsonPathActivityConfigurationBean;
import net.sf.taverna.t2.workbench.ui.views.contextualviews.activity.ActivityConfigurationPanel;

@SuppressWarnings("serial")
/**
 * 
 * @author Sergejs Aleksejevs
 */
public class JsonPathActivityConfigurationPanelProvider
		extends
		ActivityConfigurationPanel<JsonPathActivity, JsonPathActivityConfigurationBean> {
	private JsonPathActivity activity;
	private JsonPathActivityConfigurationBean configBean;

	private JsonPathActivityConfigurationPanel configPanel;

	public JsonPathActivityConfigurationPanelProvider(JsonPathActivity activity) {
		this.activity = activity;
		initGui();
	}

	protected void initGui() {
		removeAll();
		setLayout(new BorderLayout());

		// create view title
		// ShadedLabel slConfigurationLabel = new
		// ShadedLabel("Configuration options for this JsonPath service",
		// ShadedLabel.ORANGE);
		// JPanel jpConfigurationLabel = new JPanel(new GridLayout(1,1));
		// jpConfigurationLabel.add(slConfigurationLabel);
		// jpConfigurationLabel.setBorder(BorderFactory.createEmptyBorder(10,
		// 10, 0, 10));
		// add(jpConfigurationLabel, BorderLayout.NORTH);

		// create actual contents of the config panel
		this.configPanel = new JsonPathActivityConfigurationPanel();
		add(configPanel, BorderLayout.CENTER);

		// place the whole configuration panel into a raised area, so that
		// automatically added 'Apply' / 'Close' buttons visually apply to
		// the whole of the panel, not just part of it
		this.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createEmptyBorder(12, 12, 2, 12), BorderFactory
				.createRaisedBevelBorder()));

		// set preferred size for the panel (otherwise it will be way too small)
		//this.setMinimumSize(new Dimension(800, 600));
		//this.setPreferredSize(new Dimension(950, 650));

		// Populate fields from activity configuration bean
		refreshConfiguration();
	}

	/**
	 * Check that user values in the UI are valid.
	 */
	@Override
	public boolean checkValues() {
		// the only validity condition is the correctness of the JsonPath
		// expression -- so checking that
		int xpathExpressionStatus = JsonPathActivityConfigurationBean
				.validateJsonPath(this.configPanel.getCurrentJsonPathExpression());

		// show an explicit warning message to explain the problem
		if (xpathExpressionStatus == JsonPathActivityConfigurationBean.JSONPATH_EMPTY) {
			JOptionPane.showMessageDialog(this,
					"JsonPath expression should not be empty", "JsonPath Activity",
					JOptionPane.WARNING_MESSAGE);
		} else if (xpathExpressionStatus == JsonPathActivityConfigurationBean.JSONPATH_INVALID) {
			JOptionPane
					.showMessageDialog(
							this,
							"<html><center>JsonPath expression is invalid - hover the mouse over the JsonPath status<br>"
									+ "icon to get more information</center></html>",
							"JsonPath Activity", JOptionPane.WARNING_MESSAGE);
		}

		return (xpathExpressionStatus == JsonPathActivityConfigurationBean.JSONPATH_VALID);
	}

	/**
	 * Return configuration bean generated from user interface last time
	 * noteConfiguration() was called.
	 */
	@Override
	public JsonPathActivityConfigurationBean getConfiguration() {
		// Should already have been made by noteConfiguration()
		return configBean;
	}

	/**
	 * Check if the user has changed the configuration from the original
	 */
	@Override
	public boolean isConfigurationChanged() {
		boolean xmlDocumentHasNotChanged = configPanel.getSourceJSON().equals(configBean.getJsonText());
		boolean xpathExpressionHasNotChanged = configPanel
				.getCurrentJsonPathExpression().equals(
						configBean.getJsonPathAsString());

		// true (changed) unless all fields match the originals
		return !(xmlDocumentHasNotChanged && xpathExpressionHasNotChanged);
	}

	/**
	 * Prepare a new configuration bean from the UI, to be returned with
	 * getConfiguration()
	 */
	@Override
	public void noteConfiguration() {
		configBean = new JsonPathActivityConfigurationBean();
		configBean.setJsonText(configPanel.getSourceJSON());

		configBean.setJsonPathAsString(configPanel.getCurrentJsonPathExpression());
	}

	/**
	 * Update GUI from a changed configuration bean (perhaps by undo / redo).
	 */
	@Override
	public void refreshConfiguration() {
		configBean = activity.getConfiguration();

		if (configBean.getJsonText() != null) {
			configPanel.setSourceJSON(configBean.getJsonText());
			configPanel.parseJSON();
		}

		configPanel.setCurrentJsonPathExpression(configBean.getJsonPathAsString());

		// if the JSON tree was populated, (re-)run the JsonPath expression
		// and restore selection of nodes in the tree, if possible
		if (configPanel.getCurrentJSONTree() != null) {
			configPanel.runJsonPath(true);

			// invoke selection handler of the XML tree to do the job
//			configPanel.getCurrentJSONTree().getXMLTreeSelectionHandler()
//					.selectAllNodesThatMatchTheCurrentJsonPath(xpathLegList, null);
		}

	}
}
