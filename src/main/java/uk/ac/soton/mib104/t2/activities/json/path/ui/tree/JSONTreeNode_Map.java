/**
 * Copyright (C) 2013, University of Manchester and University of Southampton
 *
 * Licensed under the GNU Lesser General Public License v2.1
 * See the "LICENSE" file that is distributed with the source code for license terms. 
 */
package uk.ac.soton.mib104.t2.activities.json.path.ui.tree;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import javax.swing.tree.TreeNode;

/**
 * An object that can be used as a node in a {@link JSONTree}, where said node corresponds to a JSON object (or map.)
 * 
 * @author Mark Borkum
 * @version 0.0.1-SNAPSHOT
 *
 * @param <K>  the type of JSON keys
 */
public final class JSONTreeNode_Map<K> extends JSONTreeNode<K, Map<?, ?>> {

	/**
	 * Default constructor.
	 * 
	 * @param parent  the parent tree node
	 * @param key  the JSON key
	 * @param value  the map of JSON values
	 */
	public JSONTreeNode_Map(final TreeNode parent, final K key, final Map<?, ?> value) {
		super(parent, key, value);
	}

	@Override
	protected Vector<TreeNode> createChildren() {
		final Map<?, ?> value = this.getValue();
		
		if ((value == null) || value.isEmpty()) {
			return null;
		} else {
			final Map<String, Object> map = new TreeMap<String, Object>();
			
			for (final Map.Entry<?, ?> entry : value.entrySet()) {
				map.put(entry.getKey().toString(), entry.getValue());
			}
			
			final List<TreeNode> result = new ArrayList<TreeNode>(map.size());
			
			for (final Map.Entry<String, Object> entry : map.entrySet()) {
				result.add(createTreeNode(this, entry.getKey(), entry.getValue()));
			}
			
			return new Vector<TreeNode>(result);
		}
	}
	
}
