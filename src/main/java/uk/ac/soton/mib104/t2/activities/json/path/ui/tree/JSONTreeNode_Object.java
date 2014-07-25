/**
 * Copyright (C) 2013, University of Manchester and University of Southampton
 *
 * Licensed under the GNU Lesser General Public License v2.1
 * See the "LICENSE" file that is distributed with the source code for license terms. 
 */
package uk.ac.soton.mib104.t2.activities.json.path.ui.tree;

import java.util.Vector;

import javax.swing.tree.TreeNode;

/**
 * An object that can be used as a node in a {@link JSONTree}, where said node corresponds to any JSON value (or object.) 
 * 
 * @author Mark Borkum
 * @version 0.0.1-SNAPSHOT
 *
 * @param <K>  the type of JSON keys
 * @param <V>  the type of JSON values
 */
public final class JSONTreeNode_Object<K, V> extends JSONTreeNode<K, V> {
	
	/**
	 * Default constructor.
	 * 
	 * @param parent  the parent tree node
	 * @param key  the JSON key
	 * @param value  the JSON value
	 */
	public JSONTreeNode_Object(final TreeNode parent, final K key, final V value) {
		super(parent, key, value);
	}

	@Override
	protected Vector<TreeNode> createChildren() {
		return null;
	}
	
}
