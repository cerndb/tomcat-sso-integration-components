//        © Copyright 2020 CERN
//        This software is distributed under the terms of the GNU General Public Licence
//        version 3 (GPL Version 3), copied verbatim in the file "LICENSE". In applying
//        this licence, CERN does not waive the privileges and immunities granted to it
//        by virtue of its status as an Intergovernmental Organization or submit itself
//        to any jurisdiction.
package ch.cern.sso.tomcat.valves;

import ch.cern.sso.tomcat.aicookies.CookiesInspector;
import ch.cern.sso.tomcat.common.aisession.AiSessionFactory;
import ch.cern.sso.tomcat.common.cookies.AisCookieFactory;
import ch.cern.sso.tomcat.common.utils.*;
import ch.cern.sso.tomcat.exceptions.WrongAiLoginAsCookieFormatException;
import ch.cern.sso.tomcat.loginas.Authorizer;
import ch.cern.sso.tomcat.loginas.LoginAsCookieFactory;
import ch.cern.sso.tomcat.wrappers.CookieRequestWrapper;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ValveBase;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author lurodrig
 */
public class AiSessionValve extends ValveBase {

    private InitParamsUtils initParamsUtils;
    private SessionUtils sessionUtils;
    private AisCookieFactory aisCookieFactory;
    private AiSessionFactory aiSessionFactory;
    private LoginAsCookieFactory loginAsCookieFactory;
    private boolean isCookiesUpperCase = false;
    private boolean isLoginAsEnabled = false;
    private String[] groupsAllowed;
    private Authorizer authorizer;
    private CookiesInspector cookiesInspector;
    private Cookie cookieToDrop;

    @Override
    protected void initInternal() throws LifecycleException {
        this.initParamsUtils = new InitParamsUtils();
        this.sessionUtils = new SessionUtils();
        this.aisCookieFactory = new AisCookieFactory();
        this.aiSessionFactory = new AiSessionFactory(this.aisCookieFactory);
        this.loginAsCookieFactory = new LoginAsCookieFactory(this.sessionUtils);
        this.authorizer = new Authorizer();
        this.cookiesInspector = new CookiesInspector(sessionUtils);
        this.cookieToDrop = new Cookie(Constants.AI_SESSION, "");
    }

    @Override
    public void invoke(Request request, Response response) throws IOException, ServletException {
        try {
            initValveParameters(request);
            if (request.getUserPrincipal() != null) {
                PrincipalWrapper principalWrapper = new PrincipalWrapper(request.getUserPrincipal());
                // Be sure AI_SESSION cookie is not injected from the client
                this.cookiesInspector.dropCookie(request, this.cookieToDrop);
                // Get the loginAsCookie and check if the user has rights to inject it in the request
                Cookie loginAsCookie = this.loginAsCookieFactory.getLoginAsCookie(request.getCookies(), Constants.AI_LOGIN_AS, isLoginAsEnabled);
                authorizer.autorizeLoginAs(this.groupsAllowed, loginAsCookie, principalWrapper, response, this.isLoginAsEnabled);
                Cookie ai_session = createAiSessionCookie(request, principalWrapper, loginAsCookie);

                CookieRequestWrapper cookieRequestWrapper = new CookieRequestWrapper(request.getRequest(), Collections.singleton(ai_session));
                request.setRequest(cookieRequestWrapper);
            }
            this.getNext().invoke(request, response);
        } catch (WrongAiLoginAsCookieFormatException
                | UnknownHostException
                | InstantiationException
                | NumberFormatException
                | RemoteException ex) {
            Logger.getLogger(AiSessionValve.class.getName()).log(Level.SEVERE, null, ex);
            response.sendError(HttpServletResponse.SC_FORBIDDEN, ex.getMessage());
        }
    }

    private void initValveParameters(Request request) throws ServletException {
        ServletContext servletContext = request.getServletContext();
        this.isLoginAsEnabled = Boolean.parseBoolean(initParamsUtils.getInitParameter(servletContext.getInitParameter(Constants.STATUS_LOGIN_AS), Constants.STATUS_LOGIN_AS, false, Level.FINEST, MessagesKeys.NO_AI_LOGIN_AS_CONFIGURED));
        this.groupsAllowed = initParamsUtils.getInitParameter(servletContext.getInitParameter(Constants.GROUPS_LOGIN_AS), Constants.GROUPS_LOGIN_AS, ",", false, Level.FINEST, MessagesKeys.NO_GROUPS_LOGIN_AS_CONFIGURED);
        this.isCookiesUpperCase = Boolean.parseBoolean(initParamsUtils.getInitParameter(servletContext.getInitParameter(Constants.COOKIES_UPPER_CASE), Constants.COOKIES_UPPER_CASE, false, Level.FINEST, MessagesKeys.NO_COOKIES_UPPER_CASE_CONFIGURED));
    }

    private Cookie createAiSessionCookie(Request request, PrincipalWrapper principalWrapper, Cookie loginAsCookie) throws UnknownHostException, InstantiationException, NumberFormatException {
        Cookie ai_session;
        byte[] clientIP = InetAddress.getByName(request.getRemoteAddr()).getAddress();
        ai_session = new Cookie(Constants.AI_SESSION, this.aiSessionFactory.buildAiSession(clientIP, isCookiesUpperCase, principalWrapper, isLoginAsEnabled, loginAsCookie));
        ai_session.setPath("/");
        return ai_session;
    }
}
