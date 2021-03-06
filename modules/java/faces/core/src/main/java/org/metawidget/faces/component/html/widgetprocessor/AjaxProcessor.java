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

package org.metawidget.faces.component.html.widgetprocessor;

import static org.metawidget.inspector.faces.FacesInspectionResultConstants.*;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

import javax.el.MethodExpression;
import javax.faces.application.Application;
import javax.faces.component.UIComponent;
import javax.faces.component.behavior.AjaxBehavior;
import javax.faces.component.behavior.ClientBehaviorHolder;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.AjaxBehaviorListener;

import org.metawidget.faces.component.UIMetawidget;
import org.metawidget.faces.component.UIStub;
import org.metawidget.util.CollectionUtils;
import org.metawidget.widgetprocessor.iface.WidgetProcessor;
import org.metawidget.widgetprocessor.iface.WidgetProcessorException;

/**
 * WidgetProcessor for JSF2 environments.
 * <p>
 * Adds <code>AjaxBehavior</code> (equivalent to <code>f:ajax</code>) to suit the inspected fields.
 * Note that <code>f:ajax</code> is only supported under Facelets, as per section 10.4.1 of the JSF
 * 2 specification: "The following additional tags [f:ajax] apply to the Facelet Core Tag Library
 * <em>only</em>".
 * 
 * @author <a href="http://kennardconsulting.com">Richard Kennard</a>
 */

public class AjaxProcessor
	implements WidgetProcessor<UIComponent, UIMetawidget> {

	//
	// Public methods
	//

	public UIComponent processWidget( UIComponent component, String elementName, Map<String, String> attributes, UIMetawidget metawidget ) {

		// Ignore empty stubs

		if ( component instanceof UIStub && component.getChildCount() == 0 ) {
			return component;
		}

		// Ignore non-AJAX components

		if ( !( component instanceof ClientBehaviorHolder ) ) {
			return component;
		}

		// Ajax

		String ajaxEvent = attributes.get( FACES_AJAX_EVENT );

		if ( ajaxEvent != null ) {

			ClientBehaviorHolder clientBehaviorHolder = (ClientBehaviorHolder) component;

			// Sanity check

			if ( "".equals( ajaxEvent ) ) {
				ajaxEvent = clientBehaviorHolder.getDefaultEventName();
			} else {
				Collection<String> eventNames = clientBehaviorHolder.getEventNames();

				if ( eventNames == null || !eventNames.contains( ajaxEvent ) ) {
					throw WidgetProcessorException.newException( "'" + ajaxEvent + "' not a valid event for " + component.getClass() + ". Must be one of " + CollectionUtils.toString( eventNames ) );
				}
			}

			// Add behaviour

			FacesContext context = FacesContext.getCurrentInstance();
			AjaxBehavior ajaxBehaviour = (AjaxBehavior) context.getApplication().createBehavior( AjaxBehavior.BEHAVIOR_ID );
			clientBehaviorHolder.addClientBehavior( ajaxEvent, ajaxBehaviour );

			// Set render to the parent Metawidget level. This is not perfect, as there may be cases
			// where we want the AJAX event to, say, update a different Metawidget - but it should
			// work in the majority of cases. It is very problematic to ask the developer to specify
			// the 'render id' in the annotation, because in most cases that id will be dynamically
			// generated (may even be randomly generated). They can always use a custom
			// WidgetProcessor in that case
			//
			// If using a persistent scope (such as conversation scope or view scope) it may be more
			// optimal to use setExecute( "@this" ) instead

			ajaxBehaviour.setExecute( CollectionUtils.newArrayList( metawidget.getClientId() ) );
			ajaxBehaviour.setRender( ajaxBehaviour.getExecute() );

			// Listener

			String ajaxListener = attributes.get( FACES_AJAX_ACTION );

			if ( ajaxListener != null ) {
				ajaxBehaviour.addAjaxBehaviorListener( new AjaxBehaviorListenerImpl( ajaxListener ) );
			}
		}

		return component;
	}

	//
	// Inner class
	//

	/**
	 * As per section 3.7.10.2 of the JSF 2 specification : "The method signature defined by the
	 * listener interface must take a single parameter, an instance of the event class for which
	 * this listener is being created"
	 * <p>
	 * Must be marked <code>Serializable</code> for StateSaving.
	 */

	static class AjaxBehaviorListenerImpl
		implements AjaxBehaviorListener, Serializable {

		//
		// Private members
		//

		MethodExpression	mListenerMethod;

		//
		// Constructor
		//

		public AjaxBehaviorListenerImpl() {

			// Needed for StateSaving
		}

		public AjaxBehaviorListenerImpl( String listenerExpression ) {

			FacesContext context = FacesContext.getCurrentInstance();
			Application application = context.getApplication();

			mListenerMethod = application.getExpressionFactory().createMethodExpression( context.getELContext(), listenerExpression, Object.class, new Class[] { AjaxBehaviorEvent.class } );
		}

		//
		// Public methods
		//

		public void processAjaxBehavior( AjaxBehaviorEvent event ) {

			mListenerMethod.invoke( FacesContext.getCurrentInstance().getELContext(), new Object[] { event } );
		}
	}
}
