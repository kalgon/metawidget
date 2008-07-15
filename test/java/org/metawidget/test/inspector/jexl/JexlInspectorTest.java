// Metawidget
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

package org.metawidget.test.inspector.jexl;

import static org.metawidget.inspector.InspectionResultConstants.*;

import java.io.ByteArrayInputStream;

import junit.framework.TestCase;

import org.metawidget.inspector.iface.InspectorException;
import org.metawidget.inspector.jexl.JexlInspector;
import org.metawidget.inspector.jexl.JexlUtils;
import org.metawidget.inspector.jexl.JexlXmlInspector;
import org.metawidget.inspector.jexl.JexlXmlInspectorConfig;
import org.metawidget.inspector.jexl.UiJexlAttribute;
import org.metawidget.inspector.jexl.UiJexlAttributes;
import org.metawidget.util.XmlUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Richard Kennard
 */

public class JexlInspectorTest
	extends TestCase
{
	//
	//
	// Public methods
	//
	//

	public void testAnnotations()
	{
		JexlInspector inspector = new JexlInspector();
		assertTrue( null == inspector.inspect( (Object) null, Foo.class.getName() ));

		Document document = XmlUtils.documentFromString( inspector.inspect( new Foo(), Foo.class.getName() ));

		assertTrue( "inspection-result".equals( document.getFirstChild().getNodeName() ) );

		// Entity

		Element entity = (Element) document.getFirstChild().getFirstChild();
		assertTrue( ENTITY.equals( entity.getNodeName() ) );
		assertTrue( Foo.class.getName().equals( entity.getAttribute( TYPE ) ) );
		assertTrue( !entity.hasAttribute( NAME ) );

		// Properties

		Element property = XmlUtils.getChildWithAttributeValue( entity, NAME, "bar1" );
		assertTrue( PROPERTY.equals( property.getNodeName() ) );
		assertTrue( "from-baz".equals( property.getAttribute( "value-is-el" ) ) );
		assertTrue( "text".equals( property.getAttribute( "value-is-text" ) ) );
		assertTrue( !property.hasAttribute( "condition-is-false" ) );
		assertTrue( 3 == property.getAttributes().getLength() );

		property = XmlUtils.getChildWithAttributeValue( entity, NAME, "bar2" );
		assertTrue( PROPERTY.equals( property.getNodeName() ) );
		assertTrue( "was set".equals( property.getAttribute( "condition-is-true" ) ) );
		assertTrue( 2 == property.getAttributes().getLength() );

		assertTrue( entity.getChildNodes().getLength() == 2 );

		try
		{
			inspector.inspect( new BadFoo(), BadFoo.class.getName() );
		}
		catch( InspectorException e )
		{
			assertTrue( "Condition 'bad-condition' is not of the form ${...}".equals( e.getMessage() ));
		}
	}

	public void testXml()
	{
		String xml = "<?xml version=\"1.0\"?>";
		xml += "<inspection-result xmlns=\"http://www.metawidget.org/inspection-result\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.metawidget.org/inspection-result ../../inspector/inspection-result-1.0.xsd\">";
		xml += "<entity type=\"org.metawidget.test.inspector.jexl.JexlInspectorTest$Foo\">";
		xml += "<property name=\"bar1\" value-is-el=\"${foo.baz}\" value-is-text=\"text\"/>";
		xml += "</entity></inspection-result>";

		JexlXmlInspectorConfig config = new JexlXmlInspectorConfig();
		config.setInputStream( new ByteArrayInputStream( xml.getBytes() ) );
		config.setSchema( "" );
		JexlXmlInspector inspector = new JexlXmlInspector( config );

		Document document = XmlUtils.documentFromString( inspector.inspect( new Foo(), Foo.class.getName() ));

		assertTrue( "inspection-result".equals( document.getFirstChild().getNodeName() ) );

		// Entity

		Element entity = (Element) document.getFirstChild().getFirstChild();
		assertTrue( ENTITY.equals( entity.getNodeName() ) );
		assertTrue( Foo.class.getName().equals( entity.getAttribute( TYPE ) ) );
		assertTrue( !entity.hasAttribute( NAME ) );

		// Properties

		Element property = XmlUtils.getChildWithAttributeValue( entity, NAME, "bar1" );
		assertTrue( PROPERTY.equals( property.getNodeName() ) );
		assertTrue( "from-baz".equals( property.getAttribute( "value-is-el" ) ) );
		assertTrue( "text".equals( property.getAttribute( "value-is-text" ) ) );
		assertTrue( 3 == property.getAttributes().getLength() );

		assertTrue( entity.getChildNodes().getLength() == 1 );

		// Test null

		document = XmlUtils.documentFromString( inspector.inspect( null, Foo.class.getName() ));
		entity = (Element) document.getFirstChild().getFirstChild();
		property = XmlUtils.getChildWithAttributeValue( entity, NAME, "bar1" );
		assertTrue( "".equals( property.getAttribute( "value-is-el" ) ) );
		assertTrue( 3 == property.getAttributes().getLength() );

		assertTrue( entity.getChildNodes().getLength() == 1 );
	}

	public void testUtils()
	{
		assertTrue( JexlUtils.isValueReference( "${foo.bar}" ));
		assertTrue( !JexlUtils.isValueReference( "foo.bar" ));
		assertTrue( !JexlUtils.isValueReference( "${foo.bar" ));
		assertTrue( !JexlUtils.isValueReference( "foo.bar}" ));

		assertTrue( "foo.bar".equals( JexlUtils.unwrapValueReference( "foo.bar" )));
		assertTrue( "${foo.bar".equals( JexlUtils.unwrapValueReference( "${foo.bar" )));
		assertTrue( "foo.bar".equals( JexlUtils.unwrapValueReference( "${foo.bar}" )));
		assertTrue( "foo.bar".equals( JexlUtils.unwrapValueReference( "foo.bar" )));
		assertTrue( "${foo.bar".equals( JexlUtils.unwrapValueReference( "${foo.bar" )));

		assertTrue( "${foo.bar}".equals( JexlUtils.wrapValueReference( "foo.bar" )));
		assertTrue( "${foo.bar}".equals( JexlUtils.wrapValueReference( "${foo.bar}" )));
		assertTrue( "${${foo.bar}".equals( JexlUtils.wrapValueReference( "${foo.bar" )));
	}

	//
	//
	// Constructor
	//
	//

	/**
	 * JUnit 3.7 constructor.
	 */

	public JexlInspectorTest( String name )
	{
		super( name );
	}

	//
	//
	// Inner class
	//
	//

	public static class Foo
	{
		@UiJexlAttributes( {
			@UiJexlAttribute( name = "value-is-el", value="${foo.baz}" ),
			@UiJexlAttribute( name = "value-is-text", value="text" ),
			@UiJexlAttribute( name = "condition-is-false", value="was set", condition="${!foo.conditionResult}" )
		} )
		public String bar1;

		@UiJexlAttribute( name = "condition-is-true", value="was set", condition="${foo.conditionResult}" )
		public String bar2;

		public String getBaz()
		{
			return "from-baz";
		}

		public boolean isConditionResult()
		{
			return true;
		}
	}

	public static class BadFoo
	{
		@UiJexlAttribute( name = "foo", value = "bar", condition = "bad-condition" )
		public String bad;
	}
}
