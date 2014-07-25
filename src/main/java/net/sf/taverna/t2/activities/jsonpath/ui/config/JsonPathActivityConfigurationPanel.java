package net.sf.taverna.t2.activities.jsonpath.ui.config;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.TreeSelectionModel;

import net.minidev.json.JSONValue;
import net.minidev.json.parser.ParseException;
import net.sf.taverna.t2.activities.jsonpath.ui.servicedescription.JsonPathActivityIcon;
import net.sf.taverna.t2.activities.jsonpath.utils.JSONPathUtils;
import net.sf.taverna.t2.activities.jsonpath.JsonPathActivityConfigurationBean;
import net.sf.taverna.t2.workbench.icons.WorkbenchIcons;

import org.apache.log4j.Logger;

import uk.ac.soton.mib104.t2.activities.json.path.ui.tree.JSONTree;
import uk.ac.soton.mib104.t2.activities.json.path.ui.tree.JSONTreeCellRenderer;
import uk.ac.soton.mib104.t2.activities.json.path.ui.tree.JSONTreeNode;
import uk.ac.soton.mib104.t2.activities.json.path.ui.tree.TreeUtils;

import com.jayway.jsonpath.InvalidPathException;
import com.jayway.jsonpath.JsonPath;

/**
 * @author Sergejs Aleksejevs
 */
@SuppressWarnings("serial")
public class JsonPathActivityConfigurationPanel extends JPanel {

	private static final String NULL_VALUE_FOR_STRING = "";

	private Logger logger = Logger.getLogger(JsonPathActivityConfigurationPanel.class);

	// --- CONSTANTS ---
	public static final int MAX_NUMBER_OF_MATCHING_NODES_TO_HIGHLIGHT_IN_THE_TREE = 100;

	private static final Color INACTIVE_PANEL_BACKGROUND_COLOR = new Color(215,
			215, 215);

	private static final String EXAMPLE_JSON_PROMPT = "Paste example JSON here...";

	private static final String JSONPATH_JSON_DOCUMENT_DIR_PROPERTY="JsonPathJSONDocumentDir";

	private JsonPathActivityConfigurationPanel thisPanel;

	// --- COMPONENTS FOR ACTIVITY CONFIGURATION PANEL ---
	private JPanel jpActivityConfiguration;

	private JPanel jpLeft;
	private JPanel jpRight;

	private JButton bGenerateJsonPathExpression;

	private JTextArea taSourceJSON;
	private JButton bLoadJSONDocument;
	private JButton bParseJSON;
	private JSONTree jsonTree;
	private JScrollPane spJSONTreePlaceholder;

	// --- COMPONENTS FOR JSONPATH EDITING PANEL ---
	private JLabel jlJsonPathExpressionStatus;
	private JLabel jlJsonPathExpression;
	private JTextField tfJsonPathExpression;
	private JButton bRunJsonPath;

	// --- COMPONENTS FOR JSONPATH TESTING PANEL ---
	private JPanel jpJsonPathTesting;

	private JTextField tfExecutedJsonPathExpression;
	private JTextField tfMatchingElementCount;

	private JTabbedPane tpExecutedJsonPathExpressionResults;
	private JTextArea taExecutedJsonPathExpressionResultsAsText;
	private JScrollPane spExecutedJsonPathExpressionResultsAsText;
	private JTextArea taExecutedJsonPathExpressionResultsAsJSON;
	private JScrollPane spExecutedJsonPathExpressionResultsAsJSON;

	public JsonPathActivityConfigurationPanel() {
		this.thisPanel = this;

		this.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.gridx = 0;
		c.gridy = 0;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0;
		c.weighty = 0.50;
		c.insets = new Insets(0, 10, 10, 10);
		this.jpActivityConfiguration = createActivityConfigurationPanel();
		this.add(this.jpActivityConfiguration, c);

		c.gridy++;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weighty = 0;
		c.insets = new Insets(0, 10, 0, 10);
		this.add(new JSeparator(), c);

		// JsonPath expression editing panel
		c.gridy++;
		c.fill = GridBagConstraints.BOTH;
		c.weighty = 0.05;
		c.insets = new Insets(5, 10, 5, 10);
		this.add(createJsonPathExpressionEditingPanel(), c);

		c.gridy++;
		;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weighty = 0;
		c.insets = new Insets(0, 10, 0, 10);
		this.add(new JSeparator(), c);

		// JsonPath expression testing panel
		c.gridy++;
		c.fill = GridBagConstraints.BOTH;
		c.weighty = 0.35;
		c.insets = new Insets(5, 10, 0, 10);
		this.jpJsonPathTesting = createJsonPathExpressionTestingPanel();
		this.add(this.jpJsonPathTesting, c);
	}

	private JPanel createActivityConfigurationPanel() {
		JPanel jpConfig = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		// text area for example JSON document
		c.gridx = 0;
		c.gridy = 0;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 0.5;
		c.weighty = 1.0;
		c.insets = new Insets(5, 0, 0, 5);
		taSourceJSON = new JTextArea(10, 30);
		taSourceJSON
				.setToolTipText("<html>Use this text area to paste or load an example JSON document.<br>"
						+ "This document can then be parsed by clicking the button<br>"
						+ "with a green arrow in order to see its tree structure.</html>");
		taSourceJSON.setText(EXAMPLE_JSON_PROMPT);
		taSourceJSON.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {
				taSourceJSON.selectAll();
			}

			public void focusLost(FocusEvent e) { /* do nothing */
			}
		});
		taSourceJSON.addCaretListener(new CaretListener() {
			public void caretUpdate(CaretEvent e) {
				// make sure that it is only allowed to "parse example JSON"
				// when something is actually present in the text area
				bParseJSON.setEnabled(taSourceJSON.getText().trim().length() > 0
						&& !taSourceJSON.getText().trim().equals(
								EXAMPLE_JSON_PROMPT));
			}
		});
		jpLeft = new JPanel(new GridLayout(1, 1));
		jpLeft.add(new JScrollPane(taSourceJSON));
		jpConfig.add(jpLeft, c);

		// button to parse example JSON document

		c.gridx++;
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0;
		c.weighty = 0;
		c.insets = new Insets(0, 0, 0, 0);
		bParseJSON = new JButton(
				JsonPathActivityIcon
						.getIconById(JsonPathActivityIcon.JSONPATH_ACTIVITY_CONFIGURATION_PARSE_JSON_ICON));
		bParseJSON
				.setToolTipText("Parse example JSON document and generate its tree structure");
		bParseJSON.setEnabled(false);
		bParseJSON.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				parseJSON();
			}
		});
		jpConfig.add(bParseJSON, c);

		// placeholder for JSON tree (will be replaced by a real tree when the
		// parsing is done)

		c.gridx++;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 0.5;
		c.weighty = 1.0;
		c.insets = new Insets(5, 5, 0, 0);
		JTextArea taJSONTreePlaceholder = new JTextArea(10, 30);
		taJSONTreePlaceholder
				.setToolTipText("<html>This area will show tree structure of the example JSON after you<br>"
						+ "paste it into the space on the left-hand side and press 'Parse'<br>"
						+ "button with the green arrow.</html>");
		taJSONTreePlaceholder.setEditable(false);
		taJSONTreePlaceholder.setBackground(INACTIVE_PANEL_BACKGROUND_COLOR);
		spJSONTreePlaceholder = new JScrollPane(taJSONTreePlaceholder);
		jpRight = new JPanel(new GridLayout(1, 1));
		jpRight.add(spJSONTreePlaceholder);
		jpConfig.add(jpRight, c);

		// Button to load JSON document from a file
		
		bLoadJSONDocument = new JButton("Load JSON from file", WorkbenchIcons.openIcon);	
		bLoadJSONDocument.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				Preferences prefs = Preferences.userNodeForPackage(getClass());
				String curDir = prefs.get(JSONPATH_JSON_DOCUMENT_DIR_PROPERTY, System.getProperty("user.home"));
				fileChooser.setDialogTitle("Select file to load JSON from");
				fileChooser.setFileFilter(new FileFilter() {  
					public boolean accept(File f) {
				        return f.isDirectory() || f.getName().toLowerCase().endsWith(".json");
				    }
				    
				    public String getDescription() {
				        return ".json files";
				    }
				});
				fileChooser.setCurrentDirectory(new File(curDir));		
				int returnVal = fileChooser.showOpenDialog(((JButton) e
						.getSource()).getParent());
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					prefs.put(JSONPATH_JSON_DOCUMENT_DIR_PROPERTY, fileChooser
							.getCurrentDirectory().toString());
					File file = fileChooser.getSelectedFile();
					// Read the contents of a file into a string
					// and set the value of the JSON document text area to it
					FileInputStream fis = null;
					try{
						byte[] fileBytes = new byte[(int)file.length()];
						fis = new FileInputStream(file);
						fis.read(fileBytes);
						String xmlDocument = new String(fileBytes, "UTF-8");
						setSourceJSON(xmlDocument);
					}
					catch(Exception ex){
						logger.error("An error occured while trying to read the JSON document from file " + file.getAbsolutePath(), ex);
						JOptionPane.showMessageDialog(
								((JButton) e.getSource()).getParent(), 
								"There was an error while trying to read the file", 
								"JsonPath Activity", 
								JOptionPane.ERROR_MESSAGE);
					}
					finally{
						try {
							fis.close();
						} catch (IOException e1) {
							// Ignore
						}
					}

				}
			}
		});
		c.gridx = 0;
		c.gridy++;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0;
		c.weighty = 0;
		c.insets = new Insets(5, 0, 0, 5);
		c.anchor = GridBagConstraints.EAST;
		jpConfig.add(bLoadJSONDocument, c);
		
		
		bGenerateJsonPathExpression = new JButton("Generate JsonPath expression",
				JsonPathActivityIcon
						.getIconById(JsonPathActivityIcon.JSON_TREE_NODE_ICON));
		bGenerateJsonPathExpression.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
			     updateJsonPathEditingPanelValues();	
			}	
		});
		
		JPanel generateExpressionButtonPanel = new JPanel();
		generateExpressionButtonPanel.add(bGenerateJsonPathExpression);
		
		c.gridx = 2;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0;
		c.weighty = 0;
		c.insets = new Insets(5, 0, 0, 0);
		c.anchor = GridBagConstraints.EAST;
		jpConfig.add(generateExpressionButtonPanel, c);

		return (jpConfig);
	}

	private JPanel createJsonPathExpressionEditingPanel() {
		this.jlJsonPathExpressionStatus = new JLabel();

		this.jlJsonPathExpression = new JLabel("JsonPath expression");

		this.bRunJsonPath = new JButton("Run JsonPath");
		this.bRunJsonPath.setEnabled(false);
		this.bRunJsonPath.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				runJsonPath(true);
			}
		});

		this.tfJsonPathExpression = new JTextField(30);
		this.tfJsonPathExpression.setPreferredSize(new Dimension(0, this.bRunJsonPath
				.getPreferredSize().height));
		this.tfJsonPathExpression.setMinimumSize(new Dimension(0, this.bRunJsonPath
				.getPreferredSize().height));
		this.tfJsonPathExpression.addCaretListener(new CaretListener() {
			public void caretUpdate(CaretEvent e) {
				validateJsonPathAndUpdateUI();
			}
		});
		this.tfJsonPathExpression.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					if (bRunJsonPath.isEnabled()) {
						// it is safe to check that ENTER key may execute the
						// JsonPath expression if the
						// "Run JsonPath" button is enabled, as expression
						// validation is responsible for
						// enabling / disabling the button as the expression
						// changes
						runJsonPath(true);
					}
				}
			}

			public void keyReleased(KeyEvent e) { /* not in use */
			}

			public void keyTyped(KeyEvent e) { /* not in use */
			}
		});

		JPanel jpJsonPath = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weighty = 0;

		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0;
		jpJsonPath.add(jlJsonPathExpressionStatus);

		c.gridx++;
		c.weightx = 0.0;
		c.insets = new Insets(0, 10, 0, 0);
		jpJsonPath.add(jlJsonPathExpression, c);
		
		c.gridx++;
		c.weightx = 1.0;
		c.insets = new Insets(0, 10, 0, 10);
		jpJsonPath.add(tfJsonPathExpression, c);

		c.gridx++;
		c.weightx = 0;
		c.insets = new Insets(0, 0, 0, 0);
		jpJsonPath.add(bRunJsonPath, c);

		// initialise some values / tooltips
		resetJsonPathEditingPanel();

		return (jpJsonPath);
	}

	private JPanel createJsonPathExpressionTestingPanel() {
		JPanel jpTesting = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0;
		c.weighty = 0;
		c.insets = new Insets(0, 0, 10, 10);
		jpTesting.add(new JLabel("Executed JsonPath expression:"), c);

		c.gridx++;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1.0;
		c.weighty = 0;
		c.insets = new Insets(0, 0, 10, 10);
		tfExecutedJsonPathExpression = new JTextField();
		tfExecutedJsonPathExpression.setEditable(false);
		tfExecutedJsonPathExpression.setBorder(null);
		jpTesting.add(tfExecutedJsonPathExpression, c);

		c.gridx = 0;
		c.gridy++;
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0;
		c.weighty = 0;
		c.insets = new Insets(0, 0, 5, 10);
		jpTesting.add(new JLabel("Number of matching nodes:"), c);

		c.gridx++;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1.0;
		c.weighty = 0;
		c.insets = new Insets(0, 0, 5, 10);
		tfMatchingElementCount = new JTextField();
		tfMatchingElementCount.setEditable(false);
		tfMatchingElementCount.setBorder(null);
		jpTesting.add(tfMatchingElementCount, c);

		c.gridx = 0;
		c.gridy++;
		c.gridwidth = 2;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0;
		c.weighty = 1.0;
		tpExecutedJsonPathExpressionResults = new JTabbedPane();
		jpTesting.add(tpExecutedJsonPathExpressionResults, c);

		taExecutedJsonPathExpressionResultsAsText = new JTextArea();
		taExecutedJsonPathExpressionResultsAsText.setEditable(false);
		spExecutedJsonPathExpressionResultsAsText = new JScrollPane(
				taExecutedJsonPathExpressionResultsAsText);
		spExecutedJsonPathExpressionResultsAsText.setPreferredSize(new Dimension(
				200, 60));
		spExecutedJsonPathExpressionResultsAsText.setBorder(BorderFactory
				.createLineBorder(INACTIVE_PANEL_BACKGROUND_COLOR, 3));
		tpExecutedJsonPathExpressionResults.add("Results as text",
				spExecutedJsonPathExpressionResultsAsText);

		taExecutedJsonPathExpressionResultsAsJSON = new JTextArea();
		taExecutedJsonPathExpressionResultsAsJSON.setEditable(false);
		spExecutedJsonPathExpressionResultsAsJSON = new JScrollPane(
				taExecutedJsonPathExpressionResultsAsJSON);
		spExecutedJsonPathExpressionResultsAsJSON.setPreferredSize(new Dimension(
				200, 60));
		spExecutedJsonPathExpressionResultsAsJSON.setBorder(BorderFactory
				.createLineBorder(INACTIVE_PANEL_BACKGROUND_COLOR, 3));
		tpExecutedJsonPathExpressionResults.add("Results as JSON",
				spExecutedJsonPathExpressionResultsAsJSON);

		// initialise some values / tooltips
		resetJsonPathTestingPanel();

		return (jpTesting);
	}

	protected void parseJSON() {
		String xmlData = taSourceJSON.getText();
		try {
			jsonTree = new JSONTree();
			jsonTree.setCellRenderer(new JSONTreeCellRenderer());

				jsonTree.setJSONValue(JSONValue.parseWithException(xmlData));

			jsonTree
					.setToolTipText("<html>This is a tree structure of the JSON document that you have pasted.<br><br>"
							+ "Clicking on the nodes in this tree will let you generate the<br>"
							+ "corresponding JsonPath expression. Multiple <b>identical</b> nodes can<br>"
							+ "be selected at once, however JSONPath generation for multiple nodes<br>"
							+ "does not work.<br>"
							+ "Contextual menu provides convenience methods for expanding or<br>"
							+ "collapsing the tree." + "</html>");
			jsonTree.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			JScrollPane spJSONTree = new JScrollPane(jsonTree);
			spJSONTree.setPreferredSize(spJSONTreePlaceholder.getPreferredSize());
			jpRight.removeAll();
			jpRight.add(spJSONTree);

			// data structures inside the JSON tree were reset (as the tree was
			// re-created) -
			// now reset the UI to the initial state as well
			resetJsonPathEditingPanel();
			resetJsonPathTestingPanel();

			// JSON tree has pre-populated the namespace map with the namespaces
			// specified in the
			// root element of the tree - load these values
			updateJsonPathEditingPanelValues();

			this.validate();
			this.repaint();
		} catch (ParseException e) {
			// TODO
		}
	}

	/**
	 * Makes the {@link JsonPathActivityJSONTree} to refresh its UI from the
	 * original JSON document that was used to create it in first place.
	 * 
	 * The reason for using this method is to apply new options to the way the
	 * tree is rendered - e.g. attributes shown/hidden in the tree, values and
	 * namespaces shown/hidden, etc.
	 */
//	protected void refreshJSONTreeUI() {
//		this.jsonTree.refreshFromExistingDocument(this.miIncludeAttributes
//				.isSelected(), this.miIncludeValues.isSelected(),
//				this.miIncludeNamespaces.isSelected());
//	}

	/**
	 * Initialises JsonPath Editing panel: -- resets JsonPath expression that is being
	 * shown; -- resets local copy of namespace map; -- resets UI of namespace
	 * mapping table;
	 */
	private void resetJsonPathEditingPanel() {
		tfJsonPathExpression.setText(NULL_VALUE_FOR_STRING);
		validateJsonPathAndUpdateUI();
	}

	/**
	 * Initialises JsonPath testing panel which shows results of executing current
	 * JsonPath expression against the example JSON - the panel is returned to the
	 * way it looks when it is first loaded.
	 */
	private void resetJsonPathTestingPanel() {
		this.tfExecutedJsonPathExpression.setText("--");
		this.tfMatchingElementCount.setText("--");

		this.taExecutedJsonPathExpressionResultsAsText.setText(NULL_VALUE_FOR_STRING);
		this.taExecutedJsonPathExpressionResultsAsText
				.setBackground(INACTIVE_PANEL_BACKGROUND_COLOR);

		this.taExecutedJsonPathExpressionResultsAsJSON.setText(NULL_VALUE_FOR_STRING);
		this.taExecutedJsonPathExpressionResultsAsJSON
				.setBackground(INACTIVE_PANEL_BACKGROUND_COLOR);
	}

	public void updateJsonPathEditingPanelValues() {
		final JsonPath jsonPath = JSONTreeNode.compile(jsonTree.getLeadSelectionPath());
		
		if (jsonPath != null) {
			tfJsonPathExpression.setText(jsonPath.getPath());
		}

		repaint();
	}

	/**
	 * Validates the current JsonPath expression and updates UI accordingly: --
	 * JsonPath status icon is updated; -- tooltip for the icon explains the
	 * status; -- 'Run JsonPath' button is enabled/disabled depending on validity
	 * of JsonPath expression and existence of example data in the JSON tree
	 */
	protected void validateJsonPathAndUpdateUI() {
		String candidatePath = tfJsonPathExpression.getText();
		int xpathStatus = JsonPathActivityConfigurationBean
				.validateJsonPath(candidatePath);

		switch (xpathStatus) {
		case JsonPathActivityConfigurationBean.JSONPATH_VALID:
			// success: expression is correct
			jlJsonPathExpressionStatus.setIcon(JsonPathActivityIcon
					.getIconById(JsonPathActivityIcon.JSONPATH_STATUS_OK_ICON));
			jlJsonPathExpressionStatus
					.setToolTipText("Current JsonPath expression is well-formed and valid");

			// could allow to execute against example JSON, with only condition:
			// JSON tree must be populated
			// (that is, there should be something to run the expression
			// against)
			if (jsonTree != null) {
				this.bRunJsonPath.setEnabled(true);
				this.bRunJsonPath
						.setToolTipText("<html>Evaluate current JsonPath expression against the JSON document<br>"
								+ "whose structure is shown in the tree view above.</html>");
			} else {
				this.bRunJsonPath.setEnabled(false);
				this.bRunJsonPath
						.setToolTipText("<html>No JSON document to evaluate the current JsonPath expression against.<br><br>"
								+ "Paste some example JSON into the area in the top-left section of the<br>"
								+ "window, then parse it by clicking on the button with the green arrow<br>"
								+ "in order to test your JsonPath expression.</html>");
			}
			break;

		case JsonPathActivityConfigurationBean.JSONPATH_EMPTY:
			// no JsonPath expression - can't tell if it is correct + nothing to
			// execute
			jlJsonPathExpressionStatus.setIcon(JsonPathActivityIcon
					.getIconById(JsonPathActivityIcon.JSONPATH_STATUS_UNKNOWN_ICON));
			jlJsonPathExpressionStatus
					.setToolTipText("<html>There is no JsonPath expression to validate.<br><br>"
							+ "<b>Hint:</b> select something in the tree view showing the structure<br>"
							+ "of the JSON document that you have pasted (or type the JsonPath<br>"
							+ "expression manually).</html>");
			this.bRunJsonPath.setEnabled(false);
			this.bRunJsonPath.setToolTipText("No JsonPath expression to execute");
			break;

		case JsonPathActivityConfigurationBean.JSONPATH_INVALID:
			// failed to parse the JsonPath expression: notify of the error
			jlJsonPathExpressionStatus.setIcon(JsonPathActivityIcon
					.getIconById(JsonPathActivityIcon.JSONPATH_STATUS_ERROR_ICON));
//			jlJsonPathExpressionStatus
//					.setToolTipText(getJsonPathValidationErrorMessage());

			this.bRunJsonPath.setEnabled(false);
			this.bRunJsonPath
					.setToolTipText("Cannot execute invalid JsonPath expression");
			break;
		}

	}

	/**
	 * Executes the current JsonPath expression against the current JSON tree.
	 * 
	 * @param displayResults
	 *            <code>true</code> to execute and display results in the JsonPath
	 *            activity configuration panel (this happens when the 'Run
	 *            JsonPath' button is clicked);<br/>
	 *            <false> to run the expression quietly and simply return the
	 *            number of matching nodes.
	 * @return Number of nodes in the JSON tree that match the current JsonPath
	 *         expression. (Or <code>-1</code> if an error has occurred during
	 *         the execution -- error messages will only be shown if
	 *         <code>displayResults == true</code>).
	 */
	public int runJsonPath(boolean displayResults) {
		// ----- RUNNING THE JsonPath EXPRESSION -----
		JsonPath jsonPath = null;
		String expressionText = this.tfJsonPathExpression.getText();
		try {

			jsonPath = JsonPath.compile(expressionText);
		} catch (InvalidPathException e) {
			if (displayResults) {
				JOptionPane
						.showMessageDialog(
								thisPanel,
								"Incorrect JsonPath Expression\n\n"
										+ "Please check the expression if you have manually modified it;\n"
										+ "Alternatively, try to select another node from the JSON tree.\n\n"
										+ "------------------------------------------------------------------------------------\n\n"
										+ "JsonPath processing library reported the following error:\n"
										+ e.getMessage(), "JsonPath Activity",
								JOptionPane.ERROR_MESSAGE);
			}
			return (-1);
		}
			
			int matchingNodeCount = 0;
			
			if (expressionText.trim().isEmpty()) {
				jsonTree.clearSelection();
			} else {
				try {
					
					TreeUtils.expandOrCollapseAllRows(jsonTree, true);
					
					final int[] rows = jsonTree.getRows(jsonPath);
					

					if ((rows == null) || (rows.length == 0)) {
						jsonTree.clearSelection();
					} else {
						matchingNodeCount = rows.length;
						jsonTree.setSelectionRows(rows);
					}
				} catch (final Throwable ex) {
					jsonTree.clearSelection();
					return -1;
				}
			}


		// ----- DISPLAYING THE RESULTS -----
		if (displayResults) {
			tfExecutedJsonPathExpression.setText(expressionText);
			tfMatchingElementCount.setText(NULL_VALUE_FOR_STRING + matchingNodeCount);
			
			try {
				Object jsonValue = JSONValue.parseWithException(getSourceJSON());
				Object resultValue = JSONPathUtils.read(jsonPath, jsonValue);
				List<?> resultValues = null;
				if (!(resultValue instanceof List)) {
					resultValues = Collections.singletonList(resultValue);
				} else {
					resultValues = (List) resultValue;
				}

			StringBuffer outNodesText = new StringBuffer();
			StringBuffer outNodesJSON = new StringBuffer();
			for (Object o : resultValues) {
				String oAsJsonString = JSONValue.toJSONString(o);
				String oAsString = (o == null ? NULL_VALUE_FOR_STRING : o.toString());
				outNodesText.append(oAsString);
				outNodesText.append("\n");
				outNodesJSON.append(oAsJsonString);
				outNodesJSON.append("\n");
			}

			taExecutedJsonPathExpressionResultsAsText.setText(outNodesText
					.toString());
			taExecutedJsonPathExpressionResultsAsText.setBackground(Color.WHITE);
			taExecutedJsonPathExpressionResultsAsText.setCaretPosition(0);
			spExecutedJsonPathExpressionResultsAsText.setBorder(BorderFactory
					.createLineBorder(Color.WHITE, 3));

			taExecutedJsonPathExpressionResultsAsJSON.setText(outNodesJSON
					.toString());
			taExecutedJsonPathExpressionResultsAsJSON.setBackground(Color.WHITE);
			taExecutedJsonPathExpressionResultsAsJSON.setCaretPosition(0);
			spExecutedJsonPathExpressionResultsAsJSON.setBorder(BorderFactory
					.createLineBorder(Color.WHITE, 3));
		}
		catch (ParseException e) {
			return -1;
		}
		}
		return (matchingNodeCount);
	}
	
	public String getSourceJSON() {
		return this.taSourceJSON.getText();
	}

	protected void setSourceJSON(String xmlData) {
		this.taSourceJSON.setText(xmlData);
		this.taSourceJSON.setCaretPosition(0);
	}

	protected String getCurrentJsonPathExpression() {
		return (this.tfJsonPathExpression.getText().trim());
	}

	protected void setCurrentJsonPathExpression(String xpathExpression) {
		this.tfJsonPathExpression.setText(xpathExpression);
	}

	protected JSONTree getCurrentJSONTree() {
		return (this.jsonTree);
	}

	/**
	 * For testing
	 */
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.getContentPane().add(new JsonPathActivityConfigurationPanel());
		frame.pack();
		frame.setSize(new Dimension(900, 600));
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

}
