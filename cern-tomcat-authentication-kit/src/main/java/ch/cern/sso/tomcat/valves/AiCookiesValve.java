package ch.cern.sso.tomcat.valves;

import ch.cern.sso.tomcat.aicookies.CookiesInspector;
import ch.cern.sso.tomcat.common.cookies.AisCookieFactory;
import ch.cern.sso.tomcat.common.utils.Constants;
import ch.cern.sso.tomcat.common.exceptions.HeaderInjectionException;
import ch.cern.sso.tomcat.common.utils.InitParamsUtils;
import ch.cern.sso.tomcat.common.utils.MessagesKeys;
import ch.cern.sso.tomcat.common.utils.PrincipalWrapper;
import ch.cern.sso.tomcat.common.utils.SessionUtils;
import ch.cern.sso.tomcat.exceptions.WrongAiLoginAsCookieFormatException;
import ch.cern.sso.tomcat.loginas.Authorizer;
import ch.cern.sso.tomcat.loginas.LoginAsCookieFactory;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ValveBase;

/**
 *
 * @author lurodrig
 */
public class AiCookiesValve extends ValveBase {

    private CookiesInspector cookiesInspector;
    private SessionUtils sessionUtils;
    private String[] aiCookieNames;
    private InitParamsUtils initParamsUtils;
    private AisCookieFactory aisCookieFactory;
    private LoginAsCookieFactory loginAsCookieFactory;
    private boolean isCookiesUpperCase = false;
    private boolean isLoginAsEnabled = false;
    private Authorizer authorizer;
    private String[] groupsAllowed;

    @Override
    protected void initInternal() throws LifecycleException {
        this.sessionUtils = new SessionUtils();
        this.cookiesInspector = new CookiesInspector(sessionUtils);
        this.initParamsUtils = new InitParamsUtils();
        this.aisCookieFactory = new AisCookieFactory();
        this.loginAsCookieFactory = new LoginAsCookieFactory(sessionUtils);
        this.authorizer = new Authorizer();
    }

    @Override
    public void invoke(Request request, Response response) throws IOException, ServletException {
        try {
            initValveParameters(request);
            if (request.getUserPrincipal() != null) {
                PrincipalWrapper principalWrapper = new PrincipalWrapper(request.getUserPrincipal());
                // Be sure these cookies are not injected from the client
                for (String cookieName : this.aiCookieNames) {
                    Cookie cookieToDrop = new Cookie(cookieName, "");
                    this.cookiesInspector.dropCookie(request, cookieToDrop);
                }
                // Get the loginAsCookie and check if the user has rights to inject it in the request
                Cookie loginAsCookie = this.loginAsCookieFactory.getLoginAsCookie(request.getCookies(), Constants.AI_LOGIN_AS, isLoginAsEnabled);
                this.authorizer.autorizeLoginAs(this.groupsAllowed, loginAsCookie, principalWrapper, response, this.isLoginAsEnabled);
                Cookie[] aicookies = aisCookieFactory.getCookies(this.aiCookieNames, principalWrapper, this.isCookiesUpperCase, this.isLoginAsEnabled, loginAsCookie);
                addCookiesToRequest(aicookies, request);
            }
            getNext().invoke(request, response);
        } catch (RemoteException
                | WrongAiLoginAsCookieFormatException ex) {
            Logger.getLogger(AiCookiesValve.class.getName()).log(Level.SEVERE, null, ex);
            response.sendError(HttpServletResponse.SC_FORBIDDEN, ex.getMessage());
        }
    }

    private void addCookiesToRequest(Cookie[] aicookies, Request request) {
        for (Cookie cookie : aicookies) {
            request.addCookie(cookie);
        }
    }

    private void initValveParameters(Request request) throws ServletException {
        ServletContext servletContext = request.getServletContext();
        this.aiCookieNames = this.initParamsUtils.getInitParameter(servletContext.getInitParameter(Constants.AICOOKIES), Constants.AICOOKIES, ",", false, Level.FINEST, MessagesKeys.NO_AI_COOKIES_CONFIGURED);
        this.isLoginAsEnabled = Boolean.parseBoolean(initParamsUtils.getInitParameter(servletContext.getInitParameter(Constants.STATUS_LOGIN_AS), Constants.STATUS_LOGIN_AS, false, Level.FINEST, MessagesKeys.NO_AI_LOGIN_AS_CONFIGURED));
        this.isCookiesUpperCase = Boolean.parseBoolean(initParamsUtils.getInitParameter(servletContext.getInitParameter(Constants.COOKIES_UPPER_CASE), Constants.COOKIES_UPPER_CASE, false, Level.FINEST, MessagesKeys.NO_COOKIES_UPPER_CASE_CONFIGURED));
        this.groupsAllowed = initParamsUtils.getInitParameter(servletContext.getInitParameter(Constants.GROUPS_LOGIN_AS), Constants.GROUPS_LOGIN_AS, ",", false, Level.FINEST, MessagesKeys.NO_GROUPS_LOGIN_AS_CONFIGURED);
    }
}
