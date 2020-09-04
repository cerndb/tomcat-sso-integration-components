//        © Copyright 2020 CERN
//        This software is distributed under the terms of the GNU General Public Licence
//        version 3 (GPL Version 3), copied verbatim in the file "LICENSE". In applying
//        this licence, CERN does not waive the privileges and immunities granted to it
//        by virtue of its status as an Intergovernmental Organization or submit itself
//        to any jurisdiction.
package ch.cern.sso.tomcat.valves;

import ch.cern.sso.tomcat.aicookies.CookiesInspector;
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
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    private CookieRequestWrapper cookieRequestWrapper;
    private boolean isCookiesUpperCase = false;
    private boolean isLoginAsEnabled = false;
    private Authorizer authorizer;
    private String[] groupsAllowed;
    private Set<Cookie> cookiesToInject;

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
            Set<String> cookiesToDrop =  new HashSet<>(Arrays.asList(this.aiCookieNames));
            if(request.getUserPrincipal() != null){
                PrincipalWrapper principalWrapper = new PrincipalWrapper(request.getUserPrincipal());
                Cookie loginAsCookie = this.loginAsCookieFactory.getLoginAsCookie(request.getCookies(), Constants.AI_LOGIN_AS, isLoginAsEnabled);
                this.authorizer.autorizeLoginAs(this.groupsAllowed, loginAsCookie, principalWrapper, response, this.isLoginAsEnabled);
                Cookie[] aiCookies = aisCookieFactory.getCookies(this.aiCookieNames, principalWrapper, this.isCookiesUpperCase, this.isLoginAsEnabled, loginAsCookie);
                this.cookiesToInject = new HashSet<>(Arrays.asList(aiCookies));
            }
            cookieRequestWrapper = new CookieRequestWrapper(request.getRequest(),this.cookiesToInject,cookiesToDrop);
            request.setRequest(cookieRequestWrapper);
            getNext().invoke(request, response);
        } catch (RemoteException
                | WrongAiLoginAsCookieFormatException ex) {
            Logger.getLogger(AiCookiesValve.class.getName()).log(Level.SEVERE, null, ex);
            response.sendError(HttpServletResponse.SC_FORBIDDEN, ex.getMessage());
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
