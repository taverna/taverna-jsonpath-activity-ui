/**
 * Copyright (C) 2013, University of Manchester and University of Southampton
 *
 * Licensed under the GNU Lesser General Public License v2.1
 * See the "LICENSE" file that is distributed with the source code for license terms. 
 */
package uk.ac.soton.mib104.t2.activities.json.path.ui.tree;

import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.swing.JTree;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

/**
 * Utility methods for trees.
 * 
 * @author Mark Borkum
 * @version 0.0.1-SNAPSHOT
 */
public final class TreeUtils {
	
	/**
	 * An objective wrapper for (primitive) Integer values.
	 * <p>
	 * Instances of this class are passed by reference (rather than by value), allowing
	 * the Integer value to be modified by any code in any thread. 
	 * <p>
	 * A common use case for this class is to maintain a counter during a recursive, depth-first search. 
	 * 
	 * @author Mark Borkum
	 * @version 0.0.1-SNAPSHOT
	 */
	private static final class WrappedInteger {
		
		private volatile int value;
		
		/**
		 * Sole constructor. 
		 * 
		 * @param value  The initial value of this wrapped Integer.
		 */
		public WrappedInteger(final int value) {
			super();
			
			this.setValue(value);
		}

//		/**
//		 * Decrement the value of this wrapped Integer by one unit, and return.
//		 * 
//		 * @return  <code>this</code>
//		 */
//		public synchronized WrappedInteger decrement() {
//			this.setValue(this.getValue() - 1);
//			
//			return this;
//		}
		
		/**
		 * Returns the value of this wrapped Integer.
		 * 
		 * @return  The value.
		 */
		public int getValue() {
			return value;
		}

		/**
		 * Increment the value of this wrapped Integer.
		 * 
		 * @return  <code>this</code>
		 */
		public synchronized WrappedInteger increment() {
			this.setValue(this.getValue() + 1);
			
			return this;
		}
		
		/**
		 * Sets the value of this wrapped Integer.
		 * 
		 * @param value  The new value. 
		 */
		public void setValue(final int value) {
			this.value = value;
		}
		
	}
	
	/**
	 * Expand or collapse every node in the given <code>tree</code>.
	 * 
	 * @param tree  The tree to expand or collapse.
	 * @param expand  If <code>true</code>, then the given <code>tree</code> will be expanded.
	 * @throws IllegalArgumentException  If <code>tree == null</code>.
	 */
	public static final void expandOrCollapseAllRows(final JTree tree, final boolean expand) throws IllegalArgumentException {
		if (tree == null) {
			throw new IllegalArgumentException(new NullPointerException("tree"));
		}
		
		expandOrCollapseAllRows(tree, new TreePath(tree.getModel().getRoot()), expand);
	}
	
	/**
	 * Expand or collapse nodes in the given <code>tree</code> that match the given <code>path</code>.
	 * 
	 * @param tree  The tree to expand or collapse.
	 * @param path  The tree path.
	 * @param expand  If <code>true</code>, then the given <code>tree</code> will be expanded. 
	 * @throws IllegalArgumentException  If <code>tree == null || path == null</code>.
	 */
	private static final void expandOrCollapseAllRows(final JTree tree, final TreePath path, final boolean expand) throws IllegalArgumentException {
		if (tree == null) {
			throw new IllegalArgumentException(new NullPointerException("tree"));
		} else if (path == null) {
			throw new IllegalArgumentException(new NullPointerException("path"));
		}
		
		// Get the last component of the given 'path' (should be a tree node.)
		final TreeNode node = (TreeNode) path.getLastPathComponent();
		
		// Call this method for each child node. 
		if (node.getChildCount() > 0) {
			@SuppressWarnings("unchecked")
			final Enumeration<TreeNode> e = node.children();
			
			while (e.hasMoreElements()) {
				expandOrCollapseAllRows(tree, path.pathByAddingChild(e.nextElement()), expand);
			}
		}
		
		// Expand or collapse the given 'tree'.
		if (expand) {
			tree.expandPath(path);
		} else {
			tree.collapsePath(path);
		}
	}
	
	/**
	 * Returns an array of row indices for the given <code>model</code>, whose data matches the given <code>obj</code>.
	 * 
	 * @param model  The tree model. 
	 * @param obj  The object to be matched.
	 * @return  The array or row indices. 
	 * @throws IllegalArgumentException  If <code>model == null</code>.
	 */
	public static final int[] getRows(final TreeModel model, final Object obj) throws IllegalArgumentException {
		if (model == null) {
			throw new IllegalArgumentException(new NullPointerException("model"));
		}
		
		if (obj == null) {
			// If the given 'obj' is null, then return null. 
			return null;
		}
		
		// Cast the given 'obj' as a set. 
		final Set<Object> nodes = (obj instanceof Collection) ? new HashSet<Object>((Collection<?>) obj) : Collections.<Object>singleton(obj);
		
		if (nodes.isEmpty()) {
			// If the set of 'nodes' is empty, then return null. 
			return null;
		}
		
		// Create a [variable length] linked list to temporarily persist the results.
		final List<Integer> rowsAsList = new LinkedList<Integer>();
		
		// Begin the depth-first search (the first row has index 0.) 
		getRows(model, nodes, model.getRoot(), new WrappedInteger(0), rowsAsList);

		final int length = rowsAsList.size();
		
		if (length == 0) {
			// If no results are found, then return null.
			return null;
		} else {
			final int[] rows = new int[rowsAsList.size()];
			
			int index = 0;
			
			for (final Integer row : rowsAsList) {
				rows[index++] = row.intValue();
			}
			
			return rows;	
		}
	}
	
	/**
	 * Traverses the given 'currentNode'; adds the 'currentRow' to 'accumulator'; and, performs a depth-first search. 
	 * 
	 * @param model  The tree model.
	 * @param nodes  The set of required tree nodes. 
	 * @param currentNode  The current tree node. 
	 * @param currentRow  The row index for the current tree node.
	 * @param accumulator  The list of matching row indices. 
	 * @throws IllegalArgumentException  If any of the arguments <code>== null</code>.
	 */
	private static final void getRows(final TreeModel model, final Set<Object> nodes, final Object currentNode, final WrappedInteger currentRow, final List<Integer> accumulator) throws IllegalArgumentException {
		if (model == null) {
			throw new IllegalArgumentException(new NullPointerException("model"));
		} else if (nodes == null) {
			throw new IllegalArgumentException(new NullPointerException("nodes"));
		} else if (currentNode == null) {
			throw new IllegalArgumentException(new NullPointerException("parentNode"));
		} else if (currentRow == null) {
			throw new IllegalArgumentException(new NullPointerException("currentRow"));
		} else if (accumulator == null) {
			throw new IllegalArgumentException(new NullPointerException("accumulator"));
		}
		
		if (nodes.contains(currentNode)) {
			// If the set of 'nodes' contains the 'currentNode', then add the value of the 
			// 'currentRow' to the 'accumulator'.
			accumulator.add(currentRow.getValue());
		}
		
		for (int index = 0, length = model.getChildCount(currentNode); index < length; index++) {
			// Continue the depth-first search. 
			getRows(model, nodes, model.getChild(currentNode, index), currentRow.increment(), accumulator);
		}
	}

	/**
	 * Sole constructor.
	 */
	private TreeUtils() {
		super();
	}

}
