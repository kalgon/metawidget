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

package org.metawidget.faces.component.html.layout.richfaces;

import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;

import org.metawidget.faces.component.UIMetawidget;
import org.metawidget.layout.impl.BaseLayout;

/**
 * RichFaces layout.
 *
 * @author Richard Kennard
 */

public class RichFacesLayout
	extends BaseLayout<UIComponent, UIMetawidget>
{
	//
	// Public methods
	//

	@Override
	public void layoutChild( UIComponent widget, Map<String, String> attributes, UIMetawidget metawidget )
	{
		List<UIComponent> children = metawidget.getChildren();

		// If this component already exists in the list, remove it and re-add it. This
		// enables us to sort existing, manually created components in the correct order

		children.remove( widget );

		// Note: delegating to the Renderer to do the adding, such that it can decorate
		// the UIComponent if necessary (eg. adding a UIMessage) doesn't work out too well.
		// Specifically, the Renderer should not care whether a UIComponent is manually created
		// or overridden, but if it wraps a UIComponent with a UIStub then it needs to specify
		// whether the UIStub is for a manually created component or an overridden one, so that
		// UIMetawidget will clean it up again during startBuild. This just smells wrong,
		// because
		// Renderers should render, not manipulate the UIComponent tree.

		children.add( widget );
	}
}
