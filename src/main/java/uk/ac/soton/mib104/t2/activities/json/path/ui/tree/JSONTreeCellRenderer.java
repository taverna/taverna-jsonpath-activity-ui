/**
 * 
 */
package uk.ac.soton.mib104.t2.activities.json.path.ui.tree;

import java.awt.Component;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import net.minidev.json.JSONValue;

/**
 * @author alanrw
 *
 */
public class JSONTreeCellRenderer extends DefaultTreeCellRenderer {
	
	private static final String SELECTED_COLOUR = "white";
	
	private static final String NULL_COLOUR = "red";
	
	private static final String BOOLEAN_COLOUR = "blue";
	
	private static final String STRING_COLOUR = "green";
	
	private static final String NUMBER_COLOUR = "purple";
	
	private static final String VALUE_FORMAT = "<font face=\"Monospaced\" color=\"%s\"><b>%s</b></font>";
	
	private static final String COLLECTION_COLOUR = "gray";
	
	
	private static final String EMPTY = "empty";

	private static final String LIST_FORMAT = "[&nbsp;<font color=\"%s\">%s</font>&nbsp;]";
	
	private static final String MEMBERS_FORMAT = "%d item(s)";
	
	private static final String MAP_FORMAT = "{&nbsp;<font color=\"%s\">%s</font>&nbsp;}";
	
	private static final String ELLIPSIS = "&hellip;";
	
	private static final String DEFAULT_FORMAT = "<font face=\"Monospaced\">%s</font>";
	
	private static final String KEY_ARROW_VALUE_FORMAT = "%s&nbsp;<font color=\"black\">&rarr;</font>&nbsp;%s";
	
	private static final String WRAP_IN_HTML_FORMAT = "<html>%s</html>";

	/**
	 * 
	 */
	private static final long serialVersionUID = 4691121387932583209L;
	
	
	public Component getTreeCellRendererComponent (JTree tree,
                                     Object value,
                                     boolean selected,
                                     boolean expanded,
                                     boolean leaf,
                                     int row,
                                     boolean hasFocus) {
		
		JSONTreeNode node = (JSONTreeNode) value;
		// If the "key" is an array or object, then ensure that the textual
		// representation is *not* expanded (regardless of the value of the
		// "expanded" argument.) 
		String toGoInsideHtml = "";
		String valueString = convertToText(node.getValue(), selected, expanded, leaf);
		if (node.getParent() == null) {
			toGoInsideHtml = valueString;
		} else {
			String keyValueFormat = KEY_ARROW_VALUE_FORMAT;
			String keyString = convertToText(node.getKey(), selected, false, leaf);
			toGoInsideHtml = String.format(keyValueFormat, keyString, valueString);
		}
		String labelContent = String.format(WRAP_IN_HTML_FORMAT, toGoInsideHtml);
		
		Component result = super.getTreeCellRendererComponent(tree, value, selected,
				expanded, leaf, row, hasFocus);
		
		((JLabel) result).setText(labelContent);
		return result;
	}
	
	/**
	 * Called by the renderers to convert the given <code>jsonValue</code> to text. 
	 * 
	 * @param jsonValue  the JSON value to convert to text
	 * @param selected  <code>true</code> if the node is selected
	 * @param expanded  <code>true</code> if the node is expanded
	 * @param leaf  <code>true</code> if the node is a leaf
	 * @param row  an integer specifying the node's display row, where 0 is the first row in the display
	 * @param hasFocus  <code>true</code> if the node has the focus
	 * @return  the <code>String</code> representation of the node's value
	 */
	private static final String convertToText(final Object jsonValue, final boolean selected, final boolean expanded, final boolean leaf) {
		String jsonValueAsString = JSONValue.toJSONString(jsonValue);
		
		String colour = SELECTED_COLOUR;
		if ((jsonValue == null) || (jsonValue instanceof String) || (jsonValue instanceof Number) || (jsonValue instanceof Boolean)){
			if (!selected) {
				if (jsonValue == null) {
					colour = NULL_COLOUR;
				} else if (jsonValue instanceof String) {
					colour = STRING_COLOUR;
				} else if (jsonValue instanceof Number) {
					colour = NUMBER_COLOUR;
				} else if (jsonValue instanceof Boolean) {
					colour = BOOLEAN_COLOUR;
				}
			}
			return String.format(VALUE_FORMAT, colour, jsonValueAsString);
		} else if ((jsonValue instanceof List) || (jsonValue instanceof Map)){
			if (!selected) {
				colour = COLLECTION_COLOUR;
			}
			String collectionString;
			if (leaf) {
				collectionString = EMPTY;
			} else if (expanded) {
				collectionString = ELLIPSIS;
			} else {
				if (jsonValue instanceof List) {
					collectionString = String.format(MEMBERS_FORMAT, ((List) jsonValue).size());
				} else {
					collectionString = String.format(MEMBERS_FORMAT, ((Map) jsonValue).size());					
				}
			}
			if (jsonValue instanceof List) {
				return String.format(LIST_FORMAT, colour, collectionString);
			} else {
				return String.format(MAP_FORMAT, colour, collectionString);
			}
		} else {
			return String.format(DEFAULT_FORMAT, jsonValueAsString);
		}
	}
}
