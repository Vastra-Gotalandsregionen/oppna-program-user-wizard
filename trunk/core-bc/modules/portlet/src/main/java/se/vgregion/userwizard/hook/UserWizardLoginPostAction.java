package se.vgregion.userwizard.hook;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.liferay.portal.kernel.events.Action;
import com.liferay.portal.kernel.events.ActionException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.service.UserLocalService;

public class UserWizardLoginPostAction  extends Action {

	private static Log _log = LogFactoryUtil.getLog(UserWizardLoginPostAction.class);
	
    private ApplicationContext applicationContext;
    private UserLocalService userLocalService;
		
    @Override
    public void run(HttpServletRequest request, HttpServletResponse response) throws ActionException {
        init();
        
        _log.info("UserWizardLoginPostAction");

        User user = lookupUser(request);
        if (user == null) {
            return;
        }

        try {
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
