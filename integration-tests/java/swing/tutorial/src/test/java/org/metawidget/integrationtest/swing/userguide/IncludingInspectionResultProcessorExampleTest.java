// Metawidget
//
// This file is dual licensed under both the LGPL
// (http://www.gnu.org/licenses/lgpl-2.1.html) and the EPL
// (http://www.eclipse.org/org/documents/epl-v10.php). As a
// recipient of Metawidget, you may choose to receive it under either
// the LGPL or the EPL.
//
// Commercial licenses are also available. See http://metawidget.org
// for details.

package org.metawidget.integrationtest.swing.userguide;

import static org.metawidget.inspector.InspectionResultConstants.*;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;

import junit.framework.TestCase;

import org.metawidget.inspectionresultprocessor.iface.InspectionResultProcessor;
import org.metawidget.integrationtest.swing.tutorial.Person;
import org.metawidget.swing.SwingMetawidget;
import org.metawidget.util.XmlUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author <a href="http://kennardconsulting.com">Richard Kennard</a>
 */

public class IncludingInspectionResultProcessorExampleTest
	extends TestCase {

	//
	// Public methods
	//

	public void testIncludingInspectionResultProcessorExample()
		throws Exception {

		Person person = new Person();

		SwingMetawidget metawidget = new SwingMetawidget();
		metawidget.addInspectionResultProcessor( new IncludingInspectionResultProcessor() );
		metawidget.putClientProperty( "include", new String[] { "retired", "age" } );
		metawidget.setToInspect( person );

		assertTrue( metawidget.getComponent( 0 ) instanceof JLabel );
		assertTrue( metawidget.getComponent( 1 ) instanceof JCheckBox );
		assertEquals( "retired", metawidget.getComponent( 1 ).getName() );
		assertTrue( metawidget.getComponent( 2 ) instanceof JLabel );
		assertTrue( metawidget.getComponent( 3 ) instanceof JSpinner );
		assertEquals( "age", metawidget.getComponent( 3 ).getName() );
		assertTrue( metawidget.getComponent( 4 ) instanceof JPanel );
		assertEquals( 5, metawidget.getComponentCount() );
	}

	//
	// Inner class
	//

	static class IncludingInspectionResultProcessor
		implements InspectionResultProcessor<SwingMetawidget> {

		public String processInspectionResult( String inspectionResult, SwingMetawidget metawidget, Object toInspect, String type, String... names ) {

			String[] includes = (String[]) metawidget.getClientProperty( "include" );
			Document document = XmlUtils.documentFromString( inspectionResult );
			Element entity = (Element) document.getDocumentElement().getFirstChild();
			int propertiesToCleanup = entity.getChildNodes().getLength();

			// Pull out the names in order

			for ( String include : includes ) {
				Element property = XmlUtils.getChildWithAttributeValue( entity, NAME, include );

				if ( property == null ) {
					continue;
				}

				entity.appendChild( property );
				propertiesToCleanup--;
			}

			// Cleanup the rest

			for ( int loop = 0; loop < propertiesToCleanup; loop++ ) {
				entity.removeChild( entity.getFirstChild() );
			}

			return XmlUtils.documentToString( document, false );
		}
	}
}
