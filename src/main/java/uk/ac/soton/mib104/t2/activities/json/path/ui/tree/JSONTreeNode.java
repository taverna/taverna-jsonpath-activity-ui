/**
 * Copyright (C) 2013, University of Manchester and University of Southampton
 *
 * Licensed under the GNU Lesser General Public License v2.1
 * See the "LICENSE" file that is distributed with the source code for license terms. 
 */
package uk.ac.soton.mib104.t2.activities.json.path.ui.tree;

import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import net.minidev.json.JSONValue;
import net.sf.taverna.t2.activities.jsonpath.utils.JSONPathUtils;

//import uk.ac.soton.mib104.t2.activities.json.JSONStringUtils;
//import uk.ac.soton.mib104.t2.activities.json.path.JSONPathUtils;
//import uk.ac.soton.mib104.t2.workbench.ui.StringUtils;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.spi.JsonProvider;

/**
 * An object that can be used as a node in a {@link JSONTree}.
 * 
 * @author Mark Borkum
 * @version 0.0.1-SNAPSHOT
 *
 * @param <K>  the type of JSON keys
 * @param <V>  the type of JSON values
 */
public abstract class JSONTreeNode<K, V> implements TreeNode {
	
	private static final String arrayIndexTokenFormat1 = "[%d]";

	private static final String bracketNotationFieldFormat1 = "['%s']";
	

	
	private static JsonProvider defaultProvider;
	
	private static final String dotNotationFieldFormat1 = ".%s";
	
	private static final Vector<TreeNode> EMPTY_VECTOR = new Vector<TreeNode>(0);
	

	
	private static final String rootPathToken = "$";
	
	private static final String shouldUseDotNotationRegex = "^.*?\\W+.*?$";

	/**
	 * Compiles a tree <code>path</code> into a JSONPath expression, or returns <code>null</code>. 
	 * 
	 * @param path  the tree path
	 * @return  the JSONPath expression
	 */
	public static final JsonPath compile(final TreePath path) {
		if (path == null) {
			return null;
		} else {
			// Determine if dot notation should be used (instead of square-bracket notation.)
			final boolean dotNotation = shouldUseDotNotation(path);
			
			final StringBuffer sb = new StringBuffer();
			
			for (int index = 0, length = path.getPathCount(); index < length; index++) {
				final Object obj = path.getPathComponent(index);
				
				if (obj == null) {
					return null;
				} else if (obj instanceof JSONTreeNode) {
					final JSONTreeNode<?, ?> node = (JSONTreeNode<?, ?>) obj;
					
					final Object key = node.getKey();
					
					if (index == 0) {
						if (key == null) {
							sb.append(rootPathToken);
						} else {
							// If the first tree node in the given 'path' has a non-null key, we have an error.  
							return null;
						}
					} else {
						final String compiledKey = compileKey(dotNotation, key);
						
						if (compiledKey == null) {
							// If the next tree node in the given 'path' has a null key, we have an error. 
							return null;
						} else {
							sb.append(compiledKey);
						}
					}
				} else {
					return null;
				}
			}
			
			// Compile the contents of the string buffer. 
			return JsonPath.compile(sb.toString());
		}
	}
	
	/**
	 * Compiles a single key to a component of a JSONPath expression. 
	 * 
	 * @param dotNotation  Specifies if the key should be prefixed with a dot, or wrapped in square brackets. 
	 * @param key  the JSON key
	 * @return  a component of a JSONPath expression
	 */
	private static final String compileKey(final boolean dotNotation, final Object key) {
		if (key == null) {
			return null;
		} else if (key instanceof Integer) {
			return String.format(arrayIndexTokenFormat1, key);
		} else if (key instanceof String) {
			if (dotNotation) {
				return String.format(dotNotationFieldFormat1, key);
			} else {
				return String.format(bracketNotationFieldFormat1, key.toString());
			}
		} else {
			return null;
		}
	}
	
	/**
	 * Returns the default JSON provider.
	 * 
	 * @return  the default JSON provider
	 */
	public static final JsonProvider createProvider() {
		if (defaultProvider == null) {
			defaultProvider = new JSONTreeNodeJsonProvider(JSONPathUtils.createProvider());
		}
		
		return defaultProvider;
	}
	
	/**
	 * Constructs a new tree node for the given <code>jsonValue</code>.
	 * 
	 * @param jsonValue  the JSON value
	 * @return  a new tree node
	 */
	public static final TreeNode createTreeNode(final Object jsonValue) {
		return createTreeNode(null, (Void) null, jsonValue);
	}
	
	/**
	 * Constructs a new tree node for the given <code>parent</code>, <code>key</code> and <code>value</code>.
	 * 
	 * @param <K>  the type of JSON keys
	 * @param <V>  the type of JSON values
	 * @param parent  the parent tree node
	 * @param key  the JSON key
	 * @param value  the JSON value
	 * @return  a new tree node
	 */
	protected static final <K, V> TreeNode createTreeNode(final TreeNode parent, final K key, final V value) {
		if (value == null) {
			return new JSONTreeNode_Object<K, V>(parent, key, value);
		} else if (value instanceof List) {
			return new JSONTreeNode_List<K>(parent, key, (List<?>) value);
		} else if (value instanceof Map) {
			return new JSONTreeNode_Map<K>(parent, key, (Map<?, ?>) value);
		} else {
			return new JSONTreeNode_Object<K, V>(parent, key, value);
		}
	}
	
	/**
	 * Returns <code>true</code> if dot notation should be used to compile the given tree <code>path</code>.
	 * 
	 * @param path  the tree path
	 * @return  <code>true</code> if dot notation should be used, otherwise <code>false</code>
	 */
	private static final boolean shouldUseDotNotation(final TreePath path) {
		if (path != null) {
			for (int index = 0, length = path.getPathCount(); index < length; index++) {
				final Object obj = path.getPathComponent(index);
				
				if (obj instanceof JSONTreeNode) {
					final Object key = ((JSONTreeNode<?, ?>) obj).getKey();
					
					if ((key != null) && (key instanceof String) && key.toString().matches(shouldUseDotNotationRegex)) {
						// If the given 'key' is a string, but does not match the specified regular expression, then return false. 
						return false;
					}
				}
			}
		}
		
		return true;
	}
	
	private final Vector<TreeNode> children;

	private final K key;

	private final TreeNode parent;
	
	private final V value;

	/**
	 * Default constructor.
	 * 
	 * @param parent  the parent tree node
	 * @param key  the JSON key
	 * @param value  the JSON value
	 */
	protected JSONTreeNode(final TreeNode parent, final K key, final V value) {
		super();
		
		this.parent = parent;
		this.key = key;
		this.value = value;
		
		// Create the list of children *after* other attributes have been set. 
		this.children = this.createChildren();
	}

	@Override
	public final Enumeration<TreeNode> children() {
		return this.getChildren().elements();
	}

	
	/**
	 * Called by constructor to create list of child nodes.
	 * <p>
	 * If this node does not allow child nodes, then this method should return <code>null</code>. 
	 * 
	 * @return  list of child nodes
	 */
	protected abstract Vector<TreeNode> createChildren();
	
	@Override
	public final boolean getAllowsChildren() {
		return (children != null);
	}
	
	@Override
	public final TreeNode getChildAt(final int childIndex) throws ArrayIndexOutOfBoundsException {
		return this.getChildren().elementAt(childIndex);
	}
	
	@Override
	public final int getChildCount() {
		return this.getChildren().size();
	}
	
	/**
	 * Returns the list of child nodes. 
	 * 
	 * @return  the list of child nodes
	 */
	private Vector<TreeNode> getChildren() {
		if (children == null) {
			// If the vector of "children" is null, then always return the same
			// empty vector. The main advantage of this approach is that it
			// reduces the memory overhead when loading large trees. 
			return EMPTY_VECTOR;
		} else {
			return children;
		}
	}
	
	@Override
	public final int getIndex(final TreeNode node) {
		return this.getChildren().indexOf(node);
	}
	
	/**
	 * Returns the JSON key for this node. 
	 * 
	 * @return  the JSON key
	 */
	public final K getKey() {
		return key;
	}
	
	@Override
	public final TreeNode getParent() {
		return parent;
	}
	
	/**
	 * Returns the JSON value for this node.
	 * 
	 * @return  the JSON value
	 */
	public final V getValue() {
		return value;
	}
	
	@Override
	public final boolean isLeaf() {
		return this.getChildren().isEmpty();
	}
	
	/**
	 * Applies the given <code>jsonPath</code> to this node. 
	 * 
	 * @param <T>  expected return type
	 * @param jsonPath  the JSONPath expression
	 * @return  the result of the application
	 */
	public final <T> T read(final JsonPath jsonPath) {
		return JSONPathUtils.read(createProvider(), jsonPath, this);
	}
	
}
