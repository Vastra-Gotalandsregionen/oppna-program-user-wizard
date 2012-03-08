package se.vgregion.userwizard.controller;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletMode;
import javax.portlet.PortletModeException;
import javax.portlet.PortletPreferences;
import javax.portlet.ReadOnlyException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ValidatorException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portlet.journal.service.JournalArticleLocalService;

/**
 * The edit controller.
 * 
 * @author Erik Andersson
 * 
 */

@Controller
@RequestMapping(value = "EDIT")
public class EditController {

    @Autowired
    private JournalArticleLocalService journalArticleLocalService;

    /**
     * Do edit.
     * 
     * @param request
     *            the request
     * @param response
     *            the response
     * @return the string
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * @throws PortletException
     *             the portlet exception
     */
    @RenderMapping
    public String doEdit(RenderRequest request, RenderResponse response) throws IOException, PortletException {

        boolean postedWizardArticleExists =
                Boolean.parseBoolean(ParamUtil.getString(request, "postedWizardArticleExists", "true"));
        boolean isEditResponse =
                Boolean.parseBoolean(ParamUtil.getString(request, "isEditResponseStr", "false"));
        String postedWizardArticleId = ParamUtil.getString(request, "postedWizardArticleId", "");

        PortletPreferences prefs = request.getPreferences();

        request.setAttribute("isEditResponse", isEditResponse);
        request.setAttribute("postedWizardArticleExists", postedWizardArticleExists);
        request.setAttribute("postedWizardArticleId", postedWizardArticleId);
        request.setAttribute("wizardArticleId", prefs.getValue("wizardArticleId", ""));

        return "edit";
    }

    /**
     * Sets article value for wizard article in portlet preferences.
     * 
     * @comment If portlet needs to be used on multiple pages with the same setting, the implementation should be
     *          changed from portlet preferences to setting the articleId value as an expandoValue for the company.
     *          To to this, use the CommunityExpandoHelper or CompanyExpandoHelper class After such a change,
     *          update doEdit above, and UserWizardController viewWizard
     * 
     * @param request
     *            the request
     * @param response
     *            the response
     */
    @ActionMapping(params = "action=savePref")
    public void savePref(ActionRequest request, ActionResponse response) {

        ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
        long scopeGroupId = themeDisplay.getScopeGroupId();

        String wizardArticleIdStr = ParamUtil.getString(request, "wizardArticleId");

        boolean hasWizardArticle = false;

        try {
            hasWizardArticle = journalArticleLocalService.hasArticle(scopeGroupId, wizardArticleIdStr);

        } catch (SystemException e) {
            _log.error(e, e);
        }

        _log.info("EditController - savePref - hasWizardArticle:" + hasWizardArticle);

        if (hasWizardArticle) {
            try {
                PortletPreferences prefs = request.getPreferences();
                prefs.setValue("wizardArticleId", wizardArticleIdStr);
                prefs.store();

                response.setPortletMode(PortletMode.VIEW);

            } catch (ReadOnlyException e) {
                _log.error(e, e);
            } catch (IOException e) {
                _log.error(e, e);
            } catch (ValidatorException e) {
                _log.error(e, e);
            } catch (PortletModeException e) {
                _log.error(e, e);
            }
        } else {
            response.setRenderParameter("isEditResponseStr", "true");
            response.setRenderParameter("postedWizardArticleExists", "false");
            response.setRenderParameter("postedWizardArticleId", wizardArticleIdStr);
        }
    }

    private static Log _log = LogFactoryUtil.getLog(EditController.class);
}
