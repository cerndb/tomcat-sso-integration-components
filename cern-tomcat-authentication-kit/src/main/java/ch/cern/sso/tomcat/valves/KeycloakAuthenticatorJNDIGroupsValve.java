//        © Copyright 2020 CERN
//        This software is distributed under the terms of the GNU General Public Licence
//        version 3 (GPL Version 3), copied verbatim in the file "LICENSE". In applying
//        this licence, CERN does not waive the privileges and immunities granted to it
//        by virtue of its status as an Intergovernmental Organization or submit itself
//        to any jurisdiction.
package ch.cern.sso.tomcat.valves;

import ch.cern.sso.tomcat.common.utils.MessagesKeys;
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
import java.text.MessageFormat;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;

/**
 * Allows for getting the groups from JNDI Realm, while using Keycloak.
 * Creates a new Principal appending JNDI groups to the principal received from Keycloak.
 * @author jgraniec
 */
public class KeycloakAuthenticatorJNDIGroupsValve extends KeycloakAuthenticatorValve {

    private final static Logger LOGGER = Logger.getLogger("ch.cern.sso.tomcat.valve.KeycloakAuthenticatorJNDIGroupsValve");
    private final ResourceBundle messages = ResourceBundle.getBundle("Messages");
    @Override
    protected boolean doAuthenticate(Request request, HttpServletResponse response) throws IOException {
        boolean authenticated = super.doAuthenticate(request,response);
        Realm realm = context.getRealm();
        LOGGER.finer(MessageFormat.format(MessagesKeys.CURRENT_REALM, realm.getClass().toString()));

        if(!authenticated || request.getUserPrincipal() == null || !(request.getUserPrincipal() instanceof SamlPrincipal) || !(realm instanceof JNDIRealm) )
            return authenticated;

        JNDIUserRealm jndiUserRealm = new JNDIUserRealm((JNDIRealm) realm);
        SamlPrincipal samlPrincipal = (SamlPrincipal) request.getUserPrincipal();
        List<String> roles;
        try {
            LOGGER.fine(MessagesKeys.JNDI_REALM_ROLES_RETRIEVAL_ATTEMPT);
            roles = jndiUserRealm.getUserRoles(samlPrincipal.getName());
            LOGGER.fine(MessageFormat.format(MessagesKeys.JNDI_REALM_ROLES_RETRIEVAL_SUCCESS,roles));
        } catch (NamingException e) {
            LOGGER.severe(e.toString());
            return authenticated;
        }

        samlPrincipal = new SamlPrincipalWrapper(samlPrincipal, roles);
        GenericPrincipal genericPrincipal = new GenericPrincipal(samlPrincipal.getName(), "", roles, samlPrincipal);
        register(request, response, genericPrincipal, request.getAuthType(), genericPrincipal.getName(), "");
        LOGGER.fine("User " + samlPrincipal.getName() + "registered in " + request.getNote(org.apache.catalina.authenticator.Constants.REQ_SSOID_NOTE));
        return authenticated;
    }

}