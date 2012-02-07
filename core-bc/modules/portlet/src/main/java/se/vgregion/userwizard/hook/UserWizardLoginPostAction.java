package se.vgregion.userwizard.hook;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import se.vgregion.liferay.expando.UserExpandoHelper;

import com.liferay.portal.kernel.events.Action;
import com.liferay.portal.kernel.events.ActionException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.service.UserLocalService;
import com.liferay.portlet.expando.model.ExpandoTableConstants;
import com.liferay.portlet.expando.service.ExpandoValueLocalService;

public class UserWizardLoginPostAction  extends Action {
	
	@Autowired
    private UserExpandoHelper userExpandoHelper;
	
	@Autowired
	private ExpandoValueLocalService expandoValueLocalService;

	private static Log _log = LogFactoryUtil.getLog(UserWizardLoginPostAction.class);
	
    private ApplicationContext applicationContext;
    private UserLocalService userLocalService;
    
    private static final String EXPANDO_VALUE_HIDE_RP_WIZARD_LOGGED_IN_SESSION = "hide_rp_wizard_logged_in_session";
		
    @Override
    public void run(HttpServletRequest request, HttpServletResponse response) throws ActionException {
        init();
        
        _log.info("UserWizardLoginPostAction...");

        User user = lookupUser(request);
        if (user == null) {
        	_log.info("UserWizardLoginPostAction user is null.");
            return;
        }

        try {
        	resetUserWizardLoggedInSession(user);
        } catch (Exception e) {
            log(e.getMessage(), e);
        } finally {
        }
    }
    
    private User lookupUser(HttpServletRequest request) {
        try {
            Long userId = Long.parseLong(request.getRemoteUser());

            User user = userLocalService.getUser(userId);
            if (user == null) {
                throw new Exception("User was not found");
            }
            return user;
        } catch (Exception ex) {
            String msg = String.format("There is no user with id [%s] in Liferay's database.",
                    request.getRemoteUser());
            log(msg, ex);
        }
        return null;
    }
    
    /**
     * Sets the expando value "hide_rp_wizard_logged_in_session" to false for a Liferay user.
     * 
     * @param user
     *            the Liferay user to update
     * 
     * @comment Should be separated into a service            
     */
    private void resetUserWizardLoggedInSession(User user) {
    	_log.info("UserWizardLoginPostAction - resetUserWizardLoggedInSession.");
    	
    	long companyId = user.getCompanyId();
    	long userId = user.getUserId();
    	
    	try {
    		
    		boolean valBefore = expandoValueLocalService.getData(companyId, User.class.getName(), ExpandoTableConstants.DEFAULT_TABLE_NAME, 
    			EXPANDO_VALUE_HIDE_RP_WIZARD_LOGGED_IN_SESSION, userId, false);
    		
    		_log.info("UserWizardLoginPostAction - resetUserWizardLoggedInSession - valBefore is: " + valBefore);
    		
			expandoValueLocalService.addValue(companyId, User.class.getName(), ExpandoTableConstants.DEFAULT_TABLE_NAME,
						EXPANDO_VALUE_HIDE_RP_WIZARD_LOGGED_IN_SESSION, userId, false);
			
    		boolean valAfter = expandoValueLocalService.getData(companyId, User.class.getName(), ExpandoTableConstants.DEFAULT_TABLE_NAME, 
        			EXPANDO_VALUE_HIDE_RP_WIZARD_LOGGED_IN_SESSION, userId, false);
        		
        		_log.info("UserWizardLoginPostAction - resetUserWizardLoggedInSession - valAfter is: " + valAfter);
			
			
		} catch (PortalException e) {
			_log.error(e, e);
		} catch (SystemException e) {
			_log.error(e, e);
		}
    	
    	/*
    	_log.info("UserWizardLoginPostAction resetUserWizardLoggedInSession. companyId is: " + companyId + " and userId is: " + userId);
    	
    	Boolean hideWizardLoggedInSessionB = userExpandoHelper.get(EXPANDO_VALUE_HIDE_RP_WIZARD_LOGGED_IN_SESSION, companyId, userId);
    	boolean hideWizardLoggedInSession =  (hideWizardLoggedInSessionB != null) ? hideWizardLoggedInSessionB.booleanValue() : false;
    	
    	_log.info("resetUserWizardLoggedInSession - hideWizardLoggedInSession before set is: " + hideWizardLoggedInSession);
    	
    	userExpandoHelper.set(EXPANDO_VALUE_HIDE_RP_WIZARD_LOGGED_IN_SESSION, false, companyId, userId);
    	
    	Boolean hideWizardLoggedInSessionB2 = userExpandoHelper.get(EXPANDO_VALUE_HIDE_RP_WIZARD_LOGGED_IN_SESSION, companyId, userId);
    	boolean hideWizardLoggedInSession2 =  (hideWizardLoggedInSessionB2 != null) ? hideWizardLoggedInSessionB2.booleanValue() : false;
    	
    	
    	_log.info("resetUserWizardLoggedInSession - hideWizardLoggedInSession after set is: " + hideWizardLoggedInSession2);
    	*/
    }

    private void init() {
        if (userLocalService == null) {
            userLocalService = (UserLocalService) getApplicationContext().getBean("userLocalService");
        }
    }

    private ApplicationContext getApplicationContext() {
        if (applicationContext == null) {
            applicationContext = new ClassPathXmlApplicationContext("userwizard-hook-application-context.xml");
        }
        return applicationContext;
    }

    private void log(String msg, Throwable ex) {
        if (_log.isDebugEnabled()) {
        	_log.warn(msg, ex);
        } else {
        	_log.warn(msg);
        }
    }
	
}
