/**
 * Copyright (C) 2013, University of Manchester and University of Southampton
 *
 * Licensed under the GNU Lesser General Public License v2.1
 * See the "LICENSE" file that is distributed with the source code for license terms. 
 */
package net.sf.taverna.t2.activities.jsonpath.ui;

import net.sf.taverna.t2.workbench.dev.DeveloperWorkbench;

/**
 * Run with parameters:
 * 
 * -Xmx300m -XX:MaxPermSize=140m 
 * -Dsun.swing.enableImprovedDragGesture
 * -Dtaverna.startup=.
 * 
 * NOTE: Do not save any workflows made using this test mode, as the plugin
 * information will be missing from the workflow file, and it will not open
 * in a Taverna run normally.
 * 
 */
public class TavernaWorkbenchWithExamplePlugin {
	public static void main(String[] args) throws Exception {
		DeveloperWorkbench.main(args);
	}
}
