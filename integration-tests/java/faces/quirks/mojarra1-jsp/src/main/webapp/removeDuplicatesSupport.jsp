<%@ page language="java" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://metawidget.org/faces" prefix="m" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>

<html>
	<body>

		<f:view>
		
			<h1>Remove Duplicates Support</h1>

			<h:messages/>
			
			<h:form>
				<m:metawidget value="#{removeDuplicatesSupport.embedded}" config="metawidget-remove-duplicates-support.xml" rendered="#{removeDuplicatesSupport.embedded != null}">
					<h:inputHidden value="#{removeDuplicatesSupport.embedded.bar3}"/>
				</m:metawidget>
				<h:commandLink value="New Embedded" action="#{removeDuplicatesSupport.newEmbedded}"/>
				<br/>
				<h:commandLink value="Clear Embedded" action="#{removeDuplicatesSupport.clearEmbedded}"/>
			</h:form>

		</f:view>
		
	</body>	    
</html>
