<%@ page language="java" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://metawidget.org/faces" prefix="m" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>
	<body>

		<f:view>
		
			<h1>RichFaces Panel Quirks 2</h1>

			<h:form>
				<m:metawidget value="#{richQuirks}" config="metawidget-richfaces-panel.xml" rendererType="div"/>
			</h:form>

		</f:view>
		
	</body>	    
</html>
