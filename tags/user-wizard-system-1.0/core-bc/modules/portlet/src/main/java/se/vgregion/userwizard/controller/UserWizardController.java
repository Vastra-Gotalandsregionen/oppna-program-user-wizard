package se.vgregion.userwizard.controller;

import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

import se.vgregion.liferay.expando.UserExpandoHelper;

import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portlet.journal.service.JournalArticleLocalService;

/**
 * The controller for the user wizard portlet.
 * 
 * @author Erik Andersson
 * 
 */

@Controller
@RequestMapping(value = "VIEW")
public class UserWizardController {

    @Autowired
    private UserExpandoHelper userExpandoHelper;

    @Autowired
    private JournalArticleLocalService journalArticleLocalService;

    /**
     * Renders the User Wizard Default View.
     * 
     * @param model
     *            the model
     * @param request
     *            the request
     * @return the view to render
     */
    @RenderMapping
    public String viewDefault(Model model, RenderRequest request) {

        ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
        long companyId = themeDisplay.getCompanyId();
        long userId = themeDisplay.getUserId();

        Boolean hideWizardB = userExpandoHelper.get(EXPANDO_VALUE_HIDE_RP_WIZARD, companyId, userId);
        boolean hideWizard = (hideWizardB != null) ? hideWizardB.booleanValue() : false;

        Boolean hideWizardLoggedInSessionB =
                userExpandoHelper.get(EXPANDO_VALUE_HIDE_RP_WIZARD_LOGGED_IN_SESSION, companyId, userId);
        boolean hideWizardLoggedInSession =
                (hideWizardLoggedInSessionB != null) ? hideWizardLoggedInSessionB.booleanValue() : false;

        model.addAttribute("contextPath", request.getContextPath());
        model.addAttribute("hideWizard", hideWizard);
        model.addAttribute("hideWizardLoggedInSession", hideWizardLoggedInSession);

        return "view";
    }

    /**
     * Renders the User Wizard Wizard View.
     * 
     * @param model
     *            the model
     * @param request
     *            the request
     * @return the view to render
     */
    @RenderMapping(params = "view=viewWizard")
    public String viewWizard(Model model, RenderRequest request) {

        ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);

        long scopeGroupId = themeDisplay.getScopeGroupId();

        PortletPreferences prefs = request.getPreferences();
        String wizardArticleIdStr = prefs.getValue("wizardArticleId", "");

        boolean hasWizardArticle = false;

        try {
            hasWizardArticle = journalArticleLocalService.hasArticle(scopeGroupId, wizardArticleIdStr);
        } catch (SystemException e) {
            _log.error(e, e);
        }

        model.addAttribute("contextPath", request.getContextPath());
        model.addAttribute("scopeGroupId", themeDisplay.getScopeGroupId());
        model.addAttribute("hasWizardArticle", hasWizardArticle);
        model.addAttribute("wizardArticleId", wizardArticleIdStr);

        return "view_wizard";
    }

    /**
     * Renders the saveHideWizardConfirmation view as Json.
     * 
     * @param model
     *            the model
     * @param request
     *            the request
     * @param response
     *            the response
     * @return the view to render
     */
    @RenderMapping(params = "view=saveHideWizardConfirmation")
    public String saveHideWizardConfirmation(Model model, RenderRequest request, RenderResponse response) {

        return "view";
    }

    /**
     * Serve resource for saveHideWizard.
     * 
     * @param request
     *            the request
     * @param response
     *            the response
     * @throws PortletException
     *             the portlet exception
     */
    @ResourceMapping("saveHideWizard")
    public void saveHideWizard(ResourceRequest request, ResourceResponse response) throws PortletException {
        try {

            ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
            long companyId = themeDisplay.getCompanyId();
            long userId = themeDisplay.getUserId();

            boolean hideWizard = ParamUtil.getBoolean(request, "hideWizard", false);

            userExpandoHelper.set(EXPANDO_VALUE_HIDE_RP_WIZARD, hideWizard, companyId, userId);

            response.getWriter().append("{success: true, msg: 'success'}");

            response.setContentType("application/json");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Serve resource for saveHideWizard.
     * 
     * @param request
     *            the request
     * @param response
     *            the response
     * @throws PortletException
     *             the portlet exception
     */
    @ResourceMapping("saveHideWizardLoggedInSession")
    public void saveHideWizardLoggedInSession(ResourceRequest request, ResourceResponse response)
            throws PortletException {
        try {

            ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
            long companyId = themeDisplay.getCompanyId();
            long userId = themeDisplay.getUserId();

            boolean hideWizardLoggedInSession =
                    ParamUtil.getBoolean(request, "hideWizardLoggedInSession", false);

            userExpandoHelper.set(EXPANDO_VALUE_HIDE_RP_WIZARD_LOGGED_IN_SESSION, hideWizardLoggedInSession,
                    companyId, userId);

            response.getWriter().append("{success: true, msg: 'success'}");

            response.setContentType("application/json");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static final String EXPANDO_VALUE_HIDE_RP_WIZARD = "hide_rp_wizard";
    private static final String EXPANDO_VALUE_HIDE_RP_WIZARD_LOGGED_IN_SESSION =
            "hide_rp_wizard_logged_in_session";

    private static Log _log = LogFactoryUtil.getLog(UserWizardController.class);

}
