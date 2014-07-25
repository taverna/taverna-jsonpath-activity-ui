/**
 * Copyright (C) 2013, University of Manchester and University of Southampton
 *
 * Licensed under the GNU Lesser General Public License v2.1
 * See the "LICENSE" file that is distributed with the source code for license terms. 
 */
package uk.ac.soton.mib104.t2.activities.json.path.ui.tree;

import java.awt.event.MouseEvent;

import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import com.jayway.jsonpath.JsonPath;

/**
 * A control that displays the contents of a JSON document as a hierarchical tree. 
 * 
 * @author Mark Borkum
 * @version 0.0.1-SNAPSHOT
 */
public final class JSONTree extends JTree {
	
	private static final long serialVersionUID = -4949900350534121992L;
	
	/**
	 * Empty constructor.
	 * <p>
	 * Equivalent to: <code>JSONTree(null);</code>
	 */
	public JSONTree() {
		this(null);
	}
	
	/**
	 * Default constructor.
	 * 
	 * @param jsonValue  the JSON value
	 */
	public JSONTree(final Object jsonValue) {
		super();
		
		// Register this control with the ToolTipManager (so that tree node tooltips will be displayed.)
		ToolTipManager.sharedInstance().registerComponent(this);
		
		// Disable editing capabilities.
		this.setDragEnabled(false);
		this.setEditable(false);
		this.setShowsRootHandles(true);
		
		// Disable icons.
		final DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer) this.getCellRenderer();
		renderer.setClosedIcon(null);
		renderer.setLeafIcon(null);
		renderer.setOpenIcon(null);
		
		this.setJSONValue(jsonValue);
	}
	
	/**
	 * Returns the current JSON value for this control. 
	 * 
	 * @return  the current JSON value for this control
	 */
	public final Object getJSONValue() {
		final TreeModel model = this.getModel();
		
		final Object root = model.getRoot();
		
		if (root == null) {
			return null;
		} else if (root instanceof JSONTreeNode) {
			return ((JSONTreeNode<?, ?>) root).getValue();
		} else {
			return null;
		}
	}
	
	/**
	 * Evaluates the given <code>jsonPath</code> and returns an array of matching row indices, or <code>null</code> if no matches are found. 
	 * 
	 * @param jsonPath  the JSON path
	 * @return  an array of matching row indices, or <code>null</code> if no matches are found
	 */
	public final int[] getRows(final JsonPath jsonPath) {
		final TreeModel model = this.getModel();
		
		final Object root = model.getRoot();
		
		if (root != null) {
			return TreeUtils.getRows(model, ((JSONTreeNode<?, ?>) root).read(jsonPath));
		}
		
		return null;
	}
	
	@Override
	public final String getToolTipText(final MouseEvent e) {
		if (this.getRowForLocation(e.getX(), e.getY()) != -1) {
			final TreePath path = this.getPathForLocation(e.getX(), e.getY());
			
			if (path != null) {
				final JsonPath jsonPath = JSONTreeNode.compile(path);
				
				if (jsonPath != null) {
					return jsonPath.getPath();
				}
			}
		}
		
		return super.getToolTipText();
	}
	
	/**
	 * Sets the current JSON value for this control.
	 * 
	 * @param jsonValue  the new JSON value
	 */
	public final void setJSONValue(final Object jsonValue) {
		// Construct a new root node.
		final TreeNode root = JSONTreeNode.createTreeNode(jsonValue);
		
		((DefaultTreeModel) this.getModel()).setRoot(root);
	}

}
