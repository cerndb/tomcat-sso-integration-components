/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.cern.sso.tomcat.valves;

import ch.cern.sso.tomcat.common.utils.Constants;
import ch.cern.sso.tomcat.common.utils.SessionUtils;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.realm.GenericPrincipal;
import org.keycloak.adapters.saml.SamlPrincipal;
import org.keycloak.adapters.saml.tomcat.SamlAuthenticatorValve;

/**
 *
 * @author lurodrig
 */
public class KeycloakAuthenticatorValve extends SamlAuthenticatorValve {

    private SessionUtils sessionUtils;
    private final static String KEYCLOAK_SAML_AUTH_TYPE = "KEYCLOAK-SAML";

    @Override
    protected void initInternal() {
        this.sessionUtils = new SessionUtils();
        super.initInternal();
        Logger.getLogger(KeycloakAuthenticatorValve.class.getName()).log(Level.FINE, "Initialized");
    }

    @Override
    protected boolean doAuthenticate(Request request, HttpServletResponse response) throws IOException {
        boolean authenticated = false;
        try {
            Logger.getLogger(KeycloakAuthenticatorValve.class.getName()).log(Level.FINE, "Checking SSO cookie");
            Cookie jsessionidsso = this.sessionUtils.searchCookie(request, org.apache.catalina.authenticator.Constants.SINGLE_SIGN_ON_COOKIE);
            if (jsessionidsso != null && isValid(jsessionidsso) && isAuthTypeKeycloak(request)) {
                Logger.getLogger(KeycloakAuthenticatorValve.class.getName()).log(Level.FINE, "SSO cookie found: " + jsessionidsso.getValue());
                authenticated = validateUserSession(request, jsessionidsso);
            } else {
                Logger.getLogger(KeycloakAuthenticatorValve.class.getName()).log(Level.FINE, "NO SSO cookie");
                authenticated = super.authenticate(request, response);
                if (authenticated) {
                    SamlPrincipal samlPrincipal = (SamlPrincipal) request.getUserPrincipal();
                    Logger.getLogger(KeycloakAuthenticatorValve.class.getName()).log(Level.FINE, "User " + samlPrincipal.getName() + "authenticated");
                    // This creates the tomcat SSO session that can be used by another context
                    GenericPrincipal genericPrincipal = new GenericPrincipal(samlPrincipal.getName(), "", samlPrincipal.getAttributes(Constants.ROLES), samlPrincipal);
                    register(request, response, genericPrincipal, request.getAuthType(), samlPrincipal.getName(), "");
                    Logger.getLogger(KeycloakAuthenticatorValve.class.getName()).log(Level.FINE, "User " + samlPrincipal.getName() + "registered in " + request.getNote(org.apache.catalina.authenticator.Constants.REQ_SSOID_NOTE));
                }
            }
        } catch (Exception ex) {
            authenticated = false;
            // For anything wrong that happen in this class we will respond with a 401
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, ex.getMessage());
            Logger.getLogger(KeycloakAuthenticatorValve.class.getName()).log(Level.SEVERE, null, ex);
        }
        return authenticated;
    }

    @Override
    public void invoke(Request request, Response response) throws IOException, ServletException {
        if (isLogout(request)) {
            logout(request);
        }
        super.invoke(request, response);
    }

    private boolean validateUserSession(Request request, Cookie jsessionidsso) {
        // There must be an authenticated user first
        if (request.getUserPrincipal() != null) {
            // ssoId represents the tomcat SSO session
            String ssoId = (String) request.getNote(org.apache.catalina.authenticator.Constants.REQ_SSOID_NOTE);
            if (ssoId != null && (ssoId.equals(jsessionidsso.getValue()))) {
                // The session exists and is valid
                return true;
            }
        }
        return false;
    }

    private boolean isLogout(Request request) {
        return request.getRequestURI().equals("/saml2slo/saml");
    }

    private boolean isValid(Cookie jsessionidsso) {
        return jsessionidsso.getMaxAge() != 0;
    }
    
    private boolean isAuthTypeKeycloak(Request request) {
        if(request.getAuthType()!=null && request.getAuthType().equals(KEYCLOAK_SAML_AUTH_TYPE)){
            return true;
        }
        return false;
    }
}
