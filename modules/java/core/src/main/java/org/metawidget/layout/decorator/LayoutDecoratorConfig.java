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

package org.metawidget.layout.decorator;

import org.metawidget.layout.iface.Layout;
import org.metawidget.util.simple.ObjectUtils;

/**
 * Configures a LayoutDecorator prior to use. Once instantiated, Layouts are immutable.
 *
 * @author <a href="http://kennardconsulting.com">Richard Kennard</a>
 */

public class LayoutDecoratorConfig<W, C extends W, M extends C> {

	//
	// Private members
	//

	private Layout<W, C, M>	mLayout;

	//
	// Public methods
	//

	/**
	 * @return this, as part of a fluent interface
	 */

	public LayoutDecoratorConfig<W, C, M> setLayout( Layout<W, C, M> layout ) {

		mLayout = layout;

		return this;
	}

	@SuppressWarnings( "unchecked" )
	@Override
	public boolean equals( Object that ) {

		if ( this == that ) {
			return true;
		}

		if ( !ObjectUtils.nullSafeClassEquals( this, that )) {
			return false;
		}

		return ObjectUtils.nullSafeEquals( mLayout, ( (LayoutDecoratorConfig<W, C, M>) that ).mLayout );
	}

	@Override
	public int hashCode() {

		return ObjectUtils.nullSafeHashCode( mLayout );
	}

	//
	// Protected methods
	//

	protected Layout<W, C, M> getLayout() {

		return mLayout;
	}
}
