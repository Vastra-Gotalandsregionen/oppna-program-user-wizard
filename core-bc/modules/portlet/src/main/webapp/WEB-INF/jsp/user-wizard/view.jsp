<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0"%>
<%@ taglib uri="http://liferay.com/tld/util" prefix="liferay-util" %>
<%@ taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %>

<portlet:defineObjects />

<c:if test="${not hideWizard and not hideWizardLoggedInSession}">
	<portlet:renderURL var="showWizardURL">
	  <portlet:param name="view" value="viewWizard" />
	</portlet:renderURL>
	
	<portlet:resourceURL var="saveHideWizardLoggedInSessionURL" id="saveHideWizardLoggedInSession" />
	
	<liferay-util:html-bottom>
		<script type="text/javascript" src="${contextPath}/js/vgr-user-wizard.js"></script>
		<script type="text/javascript">
		
		AUI().ready('aui-base', 'vgr-user-wizard', function(A) {
		
			var vgrUserWizard = new A.VgrUserWizard({
				saveHideWizardLoggedInSession: '${saveHideWizardLoggedInSessionURL}',
				wizardContentURL: '${showWizardURL}'
			
			}).render();
		});
		</script>
	</liferay-util:html-bottom>
</c:if>