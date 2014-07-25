/**
 * Copyright (C) 2013, University of Manchester and University of Southampton
 *
 * Licensed under the GNU Lesser General Public License v2.1
 * See the "LICENSE" file that is distributed with the source code for license terms. 
 */
package uk.ac.soton.mib104.t2.activities.json.path.ui.tree;

import java.io.InputStream;
import java.io.Reader;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.swing.tree.TreeNode;

import com.jayway.jsonpath.InvalidJsonException;
import com.jayway.jsonpath.spi.JsonProvider;
import com.jayway.jsonpath.spi.Mode;

/**
 * Implementation of {@link JsonProvider} interface, which operates on instances of the {@link JSONTreeNode} class. 
 * 
 * @author Mark Borkum
 * @version 0.0.1-SNAPSHOT
 * 
 * @see JsonProvider
 */
public final class JSONTreeNodeJsonProvider implements JsonProvider {

	/**
	 * Reference to the default {@link JsonProvider} instance. 
	 */
	private final JsonProvider provider;
	
	/**
	 * Default constructor.
	 * 
	 * @param provider  the JSON provider
	 * @throws IllegalArgumentException  If <code>provider == null</code>.
	 */
	public JSONTreeNodeJsonProvider(final JsonProvider provider) throws IllegalArgumentException {
		super();
		
		if (provider == null) {
			throw new IllegalArgumentException(new NullPointerException("provider"));
		}
		
		this.provider = provider;
	}

	@Override
	public Object clone(final Object model) {
		return provider.clone(model);
	}

	@Override
	public List<Object> createList() {
		return provider.createList();
	}

	@Override
	public Map<String, Object> createMap() {
		return provider.createMap();
	}

	@Override
	public Object getMapValue(final Object map, final String key) {
		if (map instanceof JSONTreeNode) {
			final Object value = ((JSONTreeNode<?, ?>) map).getValue();
			
			if ((value instanceof Map) && ((Map<?, ?>) value).containsKey(key)) {
				final Enumeration<TreeNode> e = ((JSONTreeNode<?, ?>) map).children();
				
				if (e != null) {
					while (e.hasMoreElements()) {
						final TreeNode nextNode = e.nextElement();
						
						if (nextNode instanceof JSONTreeNode) {
							final Object nextKey = ((JSONTreeNode<?, ?>) nextNode).getKey();
							
							if ((nextKey != null) && nextKey.equals(key)) {
								return nextNode;
							}
						}
					}
				}
			}
			
			return null;
		}
		
		return provider.getMapValue(map, key);
	}

	@Override
	public Mode getMode() {
		return provider.getMode();
	}

	@Override
	public boolean isContainer(final Object obj) {
		return this.isList(obj) || this.isMap(obj);
	}

	@Override
	public boolean isList(final Object obj) {
		if (obj instanceof JSONTreeNode) {
			return (((JSONTreeNode<?, ?>) obj).getValue() instanceof List);
		}
		
		return provider.isList(obj);
	}

	@Override
	public boolean isMap(final Object obj) {
		if (obj instanceof JSONTreeNode) {
			return (((JSONTreeNode<?, ?>) obj).getValue() instanceof Map);
		}

		return provider.isMap(obj);
	}

	@Override
	public Object parse(final InputStream jsonStream) throws InvalidJsonException {
		return JSONTreeNode.createTreeNode(provider.parse(jsonStream));
	}

	@Override
	public Object parse(final Reader jsonReader) throws InvalidJsonException {
		return JSONTreeNode.createTreeNode(provider.parse(jsonReader));
	}

	@Override
	public Object parse(final String jsonString) throws InvalidJsonException {
		return JSONTreeNode.createTreeNode(provider.parse(jsonString));
	}

	@Override
	public String toJson(final Object obj) {
		if (obj instanceof JSONTreeNode) {
			return provider.toJson(((JSONTreeNode<?, ?>) obj).getValue());
		} 

		return provider.toJson(obj);
	}

	@Override
	public List<Object> toList(final Object list) {
		if (list instanceof JSONTreeNode) {
			final Object value = ((JSONTreeNode<?, ?>) list).getValue();
			
			if (value instanceof List) {
				final List<Object> result = this.createList();
				
				final Enumeration<TreeNode> e = ((JSONTreeNode<?, ?>) list).children();
				
				while (e.hasMoreElements()) {
					final TreeNode nextNode = e.nextElement();
					
					result.add(nextNode);
				}
				
				return provider.toList(Collections.unmodifiableList(result));
			}
		}
		
		return provider.toList(list);
	}

	@Override
	public Map<String, Object> toMap(final Object map) {
		if (map instanceof JSONTreeNode) {
			final Object value = ((JSONTreeNode<?, ?>) map).getValue();
			
			if (value instanceof Map) {
				final Map<String, Object> result = this.createMap();
				
				final Enumeration<TreeNode> e = ((JSONTreeNode<?, ?>) map).children();
				
				if (e != null) {
					while (e.hasMoreElements()) {
						final TreeNode nextNode = e.nextElement();
						
						if (nextNode instanceof JSONTreeNode) {
							final Object nextKey = ((JSONTreeNode<?, ?>) nextNode).getKey();
							
							result.put((nextKey == null) ? null : nextKey.toString(), nextNode);
						}
					}
				}
				
				return provider.toMap(Collections.unmodifiableMap(result));
			}
		}

		return provider.toMap(map);
	}
	
}
