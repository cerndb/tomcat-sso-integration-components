/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.cern.sso.tomcat.valves;

import ch.cern.sso.tomcat.realms.JNDIUserRealm;
import ch.cern.sso.tomcat.wrappers.SamlPrincipalWrapper;
import org.apache.catalina.Realm;
import org.apache.catalina.connector.Request;
import org.apache.catalina.realm.GenericPrincipal;
import org.apache.catalina.realm.JNDIRealm;
import org.keycloak.adapters.saml.SamlPrincipal;

import javax.naming.NamingException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jgraniec
 */
public class KeycloakAuthenticatorJNDIGroupsValve extends KeycloakAuthenticatorValve {

    @Override
    protected boolean doAuthenticate(Request request, HttpServletResponse response) throws IOException {
        boolean authenticated = super.doAuthenticate(request,response);
        Realm realm = context.getRealm();

        if(!authenticated || request.getUserPrincipal() == null || !(request.getUserPrincipal() instanceof SamlPrincipal) || !(realm instanceof JNDIRealm))
            return authenticated;

        JNDIUserRealm jndiUserRealm = new JNDIUserRealm((JNDIRealm) realm);
        SamlPrincipal samlPrincipal = (SamlPrincipal) request.getUserPrincipal();
        List<String> roles;
        try {
            roles = jndiUserRealm.getUserRoles(samlPrincipal.getName());
        } catch (NamingException e) {
            Logger.getLogger(KeycloakAuthenticatorJNDIGroupsValve.class.getName()).log(Level.SEVERE, e.toString());
            return authenticated;
        }

        samlPrincipal = new SamlPrincipalWrapper(samlPrincipal, roles);
        GenericPrincipal genericPrincipal = new GenericPrincipal(samlPrincipal.getName(), "", roles, samlPrincipal);
        register(request, response, genericPrincipal, request.getAuthType(), genericPrincipal.getName(), "");
        Logger.getLogger(KeycloakAuthenticatorJNDIGroupsValve.class.getName()).log(Level.FINE, "User " + samlPrincipal.getName() + "registered in " + request.getNote(org.apache.catalina.authenticator.Constants.REQ_SSOID_NOTE));
        return authenticated;
    }

}