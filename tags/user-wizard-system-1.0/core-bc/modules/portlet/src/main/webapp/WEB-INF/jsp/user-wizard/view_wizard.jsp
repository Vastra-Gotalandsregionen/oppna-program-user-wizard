<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0"%>
<%@ taglib uri="http://liferay.com/tld/util" prefix="liferay-util" %>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>
<%@ taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %>

<portlet:defineObjects />

<portlet:resourceURL var="saveHideWizardURL" id="saveHideWizard" />

<div class="user-wizard-content-wrap">
	<c:choose>
		<c:when test="${hasWizardArticle}">
			<liferay-ui:journal-article groupId="${scopeGroupId}" articleId="${wizardArticleId}" />
		</c:when>
		<c:otherwise>
			<p>Det finns ingen artikel konfigurerad f&ouml;r wizarden. V&auml;lj inst&auml;llningar och ange ett giltigt artikelId.</p>
		</c:otherwise>
	</c:choose>
	
	<div class="user-wizard-control-wrap">
		<a class="user-wizard-show-control checked" href="${saveHideWizardURL}">Visa denna information n&auml;sta g&aring;ng jag loggar in<span></span></a>
	</div>
</div>