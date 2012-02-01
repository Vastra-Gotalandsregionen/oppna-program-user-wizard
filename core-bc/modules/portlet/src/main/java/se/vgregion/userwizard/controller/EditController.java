/**
 * 
 */
package se.vgregion.userwizard.controller;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.portlet.ReadOnlyException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ValidatorException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import com.liferay.portal.kernel.util.ParamUtil;

/**
 * @author Erik Andersson
 * 
 */

@Controller
@RequestMapping(value = "EDIT")
public class EditController {
    private static final Log LOG = LogFactory.getLog(EditController.class);

    @RenderMapping
    public String doEdit(RenderRequest renderRequest, RenderResponse renderResponse) throws IOException,
            PortletException {

        PortletPreferences prefs = renderRequest.getPreferences();

        renderRequest.setAttribute("wizardArticleId", prefs.getValue("wizardArticleId", ""));

        return "edit";
    }

    @ActionMapping(params = "action=savePref")
    public void savePref(ActionRequest request, ActionResponse response) {

        String wizardArticleId = ParamUtil.getString(request, "wizardArticleId");

        try {
            PortletPreferences prefs = request.getPreferences();
            prefs.setValue("wizardArticleId", wizardArticleId);
            prefs.store();
        } catch (ReadOnlyException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ValidatorException e) {
            e.printStackTrace();
        }
    }

}
