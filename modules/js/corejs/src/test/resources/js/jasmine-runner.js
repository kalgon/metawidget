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

'use strict';

/**
 * Simple script to run Jasmine.
 */

//
// Default Jasmine initialization
//
var reporter = new jasmine.JsApiReporter();
reporter.log = print;

reporter.reportSuiteStarting = function( suite ) {

	reporter.log( suite.getFullName() );
}

reporter.reportSpecResults = function( spec ) {

	var results = spec.results();

	reporter.log( '\t' + spec.description + ' (' + results.passedCount + '/' + results.totalCount + ' passed)' );

	for ( var loop = 0, length = results.getItems().length; loop < length; loop++ ) {

		var item = results.getItems()[loop];
		if ( !item.passed() ) {
			var toLog = item.matcherName + ': ' + item.message;
			reporter.log( '\t\t' + toLog );
			throw new Error( spec.description + ': ' + toLog );
		}
	}
}

var jasmineEnv = jasmine.getEnv();
jasmineEnv.updateInterval = 0;
jasmineEnv.addReporter( reporter );

function runJasmine() {

	jasmineEnv.execute();
}

//
// Simple document implementation (can be replaced by EnvJS)
//

this.document = {
	"createElement": function( elementName ) {

		var attributes = {};

		return {
			"tagName": elementName.toUpperCase(),
			"childNodes": [],
			"children": function() {

				throw new Error( "children is not ECMAScript" );
			},
			"setAttribute": function( name, value ) {

				attributes[name] = value;
			},
			"hasAttribute": function( name ) {

				return ( attributes[name] != undefined );
			},
			"getAttribute": function( name ) {

				return attributes[name];
			},
			"appendChild": function( childNode ) {

				this.childNodes.push( childNode );
			},
			"cloneNode": function( deepClone ) {

				var clone = document.createElement( elementName );

				for ( var property in attributes ) {
					clone.setAttribute( property, attributes[property] );
				}
				return clone;
			},
			"removeChild": function( childNode ) {

				for ( var loop = 0, length = this.childNodes.length; loop < length; loop++ ) {
					if ( this.childNodes[loop] == childNode ) {
						this.childNodes.splice( loop, 1 );
						return childNode;
					}
				}

				throw new Error( "childNode not found: " + childNode );
			},
			"toString": function() {

				var toString = elementName;

				if ( attributes ) {
					for ( var name in attributes ) {
						toString += ' ' + name + '="' + attributes[name] + '"';
					}
				}

				return toString;
			}
		};
	}
};