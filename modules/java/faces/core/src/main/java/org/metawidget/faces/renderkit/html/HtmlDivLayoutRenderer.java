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

package org.metawidget.faces.renderkit.html;

import static org.metawidget.inspector.InspectionResultConstants.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.component.UIParameter;
import javax.faces.component.html.HtmlInputHidden;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.metawidget.faces.FacesUtils;
import org.metawidget.faces.component.UIMetawidget;
import org.metawidget.faces.component.UIStub;
import org.metawidget.util.WidgetBuilderUtils;
import org.metawidget.util.simple.StringUtils;

/**
 * Layout to arrange widgets in HTML <code>DIV</code> tags, with one <code>DIV</code> per label and
 * per widget, and a wrapper <code>DIV</code> around both.
 * <p>
 * This implementation recognizes the following <code>&lt;f:param&gt;</code> parameters:
 * <p>
 * <ul>
 * <li><code>divStyleClasses</code> - comma separated list of style classes to apply to the DIVs, in
 * order of outer, label, required, component, errors
 * <li><code>outerStyle</code> - CSS styles to apply to the outer DIV
 * <li><code>labelStyle</code> - CSS styles to apply to the label DIV
 * <li><code>componentStyle</code> - this is the style applied to the DIV <em>around</em> the
 * component, not to the component itself. The component itself can be styled using the
 * <code>style</code> attribute on the Metawidget tag
 * <li><code>requiredStyle</code> - CSS styles to apply to the required DIV
 * </ul>
 * <p>
 *
 * @author <a href="http://kennardconsulting.com">Richard Kennard</a>
 */

public class HtmlDivLayoutRenderer
	extends HtmlLayoutRenderer {

	//
	// Public methods
	//

	@Override
	public void encodeBegin( FacesContext context, UIComponent metawidgetComponent )
		throws IOException {

		UIMetawidget metawidget = (UIMetawidget) metawidgetComponent;
		metawidget.putClientProperty( HtmlDivLayoutRenderer.class, null );
		super.encodeBegin( context, metawidget );

		// Determine outer styles

		State state = getState( metawidget );
		state.outerStyle = metawidget.getParameter( "outerStyle" );

		// Determine label, component, required styles

		state.labelStyle = metawidget.getParameter( "labelStyle" );
		state.componentStyle = metawidget.getParameter( "componentStyle" );
		state.requiredStyle = metawidget.getParameter( "requiredStyle" );

		// Determine style classes

		String styleClassesParameter = metawidget.getParameter( "divStyleClasses" );

		if ( styleClassesParameter != null ) {
			state.divStyleClasses = styleClassesParameter.split( StringUtils.SEPARATOR_COMMA );
		}

		// Start component

		ResponseWriter writer = context.getResponseWriter();
		writer.startElement( "div", metawidget );
		writer.writeAttribute( "id", metawidget.getClientId( context ), "id" );
	}

	@Override
	public void encodeChildren( FacesContext context, UIComponent component )
		throws IOException {

		List<UIComponent> children = component.getChildren();

		// For each child component...

		for ( UIComponent childComponent : children ) {
			// ...that is visible...

			if ( childComponent instanceof UIStub && childComponent.getChildCount() == 0 ) {
				continue;
			}

			if ( childComponent instanceof UIParameter ) {
				continue;
			}

			if ( !childComponent.isRendered() ) {
				continue;
			}

			// ...(and is not a hidden field)...

			if ( childComponent instanceof HtmlInputHidden ) {
				FacesUtils.render( context, childComponent );
				continue;
			}

			// ...render the label...

			layoutBeforeChild( context, component, childComponent );

			// ...and render the component

			layoutChild( context, component, childComponent );
			layoutAfterChild( context, component, childComponent );
		}
	}

	@Override
	public void encodeEnd( FacesContext context, UIComponent metawidget )
		throws IOException {

		super.encodeEnd( context, metawidget );

		ResponseWriter writer = context.getResponseWriter();

		// Footer facet

		UIComponent componentFooter = metawidget.getFacet( "footer" );

		if ( componentFooter != null ) {
			writer.startElement( "div", metawidget );
			writeStyleAndClass( (UIMetawidget) metawidget, writer, "footer" );

			// Render facet

			FacesUtils.render( context, componentFooter );

			writer.endElement( "div" );
		}

		// End component

		writer.endElement( "div" );
	}

	@Override
	protected UIComponent createInlineMessage( FacesContext context, UIComponent metawidget, String messageFor ) {

		UIComponent message = super.createInlineMessage( context, metawidget, messageFor );

		// Apply alternate style class (if any)

		State state = getState( metawidget );

		if ( state.divStyleClasses != null && state.divStyleClasses.length > 4 ) {
			FacesUtils.setStyleAndStyleClass( message, null, state.divStyleClasses[4] );
		}

		return message;
	}

	//
	// Protected methods
	//

	protected void layoutBeforeChild( FacesContext context, UIComponent metawidget, UIComponent componentChild )
		throws IOException {

		ResponseWriter writer = context.getResponseWriter();

		State state = getState( metawidget );

		// Outer

		writer.startElement( "div", metawidget );

		if ( state.outerStyle != null ) {
			writer.writeAttribute( "style", state.outerStyle, null );
		}

		writeStyleClass( metawidget, writer, 0 );

		// Label

		layoutLabel( context, metawidget, componentChild );

		// Component
		//
		// Note: it is debatable whether we should use DIVs inside DIVs or SPANs inside DIVs here.
		// We choose the former, and the JBoss Seam demos do it both ways (Hotel Booking the latter,
		// Groovy Hotel Booking the former)

		writer.startElement( "div", metawidget );

		if ( state.componentStyle != null ) {
			writer.writeAttribute( "style", state.componentStyle, null );
		}

		writeStyleClass( metawidget, writer, 3 );
	}

	/**
	 * @return whether a label was written
	 */

	@Override
	protected boolean layoutLabel( FacesContext context, UIComponent metawidget, UIComponent componentNeedingLabel )
		throws IOException {

		if ( getLabelText( componentNeedingLabel ) == null ) {
			return false;
		}

		ResponseWriter writer = context.getResponseWriter();

		writer.startElement( "div", metawidget );

		State state = getState( metawidget );

		if ( state.labelStyle != null ) {
			writer.writeAttribute( "style", state.labelStyle, null );
		}

		writeStyleClass( metawidget, writer, 1 );

		super.layoutLabel( context, metawidget, componentNeedingLabel );

		layoutRequired( context, metawidget, componentNeedingLabel );

		writer.endElement( "div" );

		return true;
	}

	protected void layoutRequired( FacesContext context, UIComponent metawidget, UIComponent child )
		throws IOException {

		@SuppressWarnings( "unchecked" )
		Map<String, String> metadataAttributes = (Map<String, String>) child.getAttributes().get( UIMetawidget.COMPONENT_ATTRIBUTE_METADATA );

		if ( metadataAttributes == null ) {
			return;
		}

		ResponseWriter writer = context.getResponseWriter();

		if ( TRUE.equals( metadataAttributes.get( REQUIRED ) ) && !WidgetBuilderUtils.isReadOnly( metadataAttributes ) && !( (UIMetawidget) metawidget ).isReadOnly() ) {
			writer.startElement( "span", metawidget );

			State state = getState( metawidget );
			String requiredStyle = metadataAttributes.get( state.requiredStyle );

			if ( requiredStyle != null ) {
				writer.writeAttribute( "style", requiredStyle, null );
			}

			writeStyleClass( metawidget, writer, 2 );
			writer.write( "*" );
			writer.endElement( "span" );
		}
	}

	/**
	 * @param metawidget the Metawidget doing the layout
	 * @param child	the component being laid out
	 */

	protected void layoutAfterChild( FacesContext context, UIComponent metawidget, UIComponent child )
		throws IOException {

		ResponseWriter writer = context.getResponseWriter();

		writer.endElement( "div" );
		writer.endElement( "div" );
	}

	protected void writeStyleClass( UIComponent metawidget, ResponseWriter writer, int styleClass )
		throws IOException {

		State state = getState( metawidget );

		if ( state.divStyleClasses == null || state.divStyleClasses.length <= styleClass ) {
			return;
		}

		String columnClass = state.divStyleClasses[styleClass];

		if ( columnClass.length() == 0 ) {
			return;
		}

		writer.writeAttribute( "class", columnClass.trim(), null );
	}

	//
	// Private methods
	//

	private State getState( UIComponent metawidget ) {

		State state = (State) ( (UIMetawidget) metawidget ).getClientProperty( HtmlDivLayoutRenderer.class );

		if ( state == null ) {
			state = new State();
			( (UIMetawidget) metawidget ).putClientProperty( HtmlDivLayoutRenderer.class, state );
		}

		return state;
	}

	//
	// Inner class
	//

	/**
	 * Simple, lightweight structure for saving state.
	 */

	/* package private */static class State {

		/* package private */String		outerStyle;

		/* package private */String		labelStyle;

		/* package private */String		requiredStyle;

		/* package private */String		componentStyle;

		/* package private */String[]	divStyleClasses;
	}
}
