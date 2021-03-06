<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui"%>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<portlet:defineObjects />

<h3>Inställningar f&ouml;r User Wizard</h3>

<portlet:actionURL name="savePref" var="savePrefURL">
	<portlet:param name="action" value="savePref" />
</portlet:actionURL>

<portlet:renderURL portletMode="view" var="backURL" />

<aui:form action="${savePrefURL}" name="prefsForm" method="post" cssClass="">

  <aui:input name="wizardArticleId" type="text" label="ArtikelID f&ouml;r wizard" value="${wizardArticleId}" />
  <c:if test="${isEditResponse and not postedWizardArticleExists}">
  	<div class="portlet-msg-error">
  		Det finns ingen artikel med det artikel-id (${postedWizardArticleId}) du f&ouml;rs&ouml;kte spara. Kolla s&aring; att det verkligen finns en s&aring;dan artikel och f&ouml;rs&ouml;k igen.
  	</div>
  </c:if>

  <aui:button-row>
    <aui:button type="submit" name="save" />
    <aui:button onClick="window.location='${backURL}'" type="cancel" name="back" />
  </aui:button-row>

</aui:form>