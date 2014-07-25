/**
 * Copyright (C) 2013, University of Manchester and University of Southampton
 *
 * Licensed under the GNU Lesser General Public License v2.1
 * See the "LICENSE" file that is distributed with the source code for license terms. 
 */
package uk.ac.soton.mib104.t2.activities.json.path.ui.tree;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.tree.TreeNode;

/**
 * An object that can be used as a node in a {@link JSONTree}, where said node corresponds to a JSON array (or list.)
 * 
 * @author Mark Borkum
 * @version 0.0.1-SNAPSHOT
 *
 * @param <K>  the type of JSON keys
 */
public final class JSONTreeNode_List<K> extends JSONTreeNode<K, List<?>> {

	/**
	 * Default constructor.
	 * 
	 * @param parent  the parent tree node
	 * @param key  the JSON key
	 * @param value  the list of JSON values
	 */
	public JSONTreeNode_List(final TreeNode parent, final K key, final List<?> value) {
		super(parent, key, value);
	}

	@Override
	protected Vector<TreeNode> createChildren() {
		final List<?> value = this.getValue();
		
		if ((value == null) || value.isEmpty()) {
			return null;
		} else {
			final List<TreeNode> result = new ArrayList<TreeNode>(value.size());
			
			for (int index = 0, length = value.size(); index < length; index++) {
				Object childValue;
				
				try {
					childValue = value.get(index);
				} catch (final ArrayIndexOutOfBoundsException ex) {
					childValue = null;
				}
				
				result.add(createTreeNode(this, index, childValue));
			}

			return new Vector<TreeNode>(result);
		}
	}
	
}
