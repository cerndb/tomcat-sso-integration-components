package ch.cern.sso.tomcat.valves;

import ch.cern.sso.tomcat.common.utils.MessagesKeys;
import ch.cern.sso.tomcat.realms.JNDIUserRealm;
import org.apache.catalina.Realm;
import org.apache.catalina.connector.Request;
import org.apache.catalina.realm.GenericPrincipal;
import org.apache.catalina.realm.JNDIRealm;

import javax.naming.NamingException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;

/**
 * @author jgraniec
 */
public class OidcJNDIGroupsValve extends org.keycloak.adapters.tomcat.KeycloakAuthenticatorValve {
    private final static Logger LOGGER = Logger.getLogger("ch.cern.sso.tomcat.valve.OidcJNDIGroupsValve");
    private final ResourceBundle messages = ResourceBundle.getBundle("Messages");
    @Override
    protected boolean doAuthenticate(Request request, HttpServletResponse response) throws IOException {
        boolean authenticated = super.doAuthenticate(request,response);
        Realm realm = context.getRealm();
        LOGGER.finer(MessageFormat.format(MessagesKeys.CURRENT_REALM, realm.getClass().toString()));

        if(!authenticated || request.getUserPrincipal() == null || !(request.getUserPrincipal() instanceof GenericPrincipal) || !(realm instanceof JNDIRealm) )
            return authenticated;

        JNDIUserRealm jndiUserRealm = new JNDIUserRealm((JNDIRealm) realm);
        GenericPrincipal userPrincipal = (GenericPrincipal) request.getUserPrincipal();
        List<String> roles;
        try {
            LOGGER.fine(MessagesKeys.JNDI_REALM_ROLES_RETRIEVAL_ATTEMPT);
            roles = jndiUserRealm.getUserRoles(userPrincipal.getName());
            LOGGER.fine(MessageFormat.format(MessagesKeys.JNDI_REALM_ROLES_RETRIEVAL_SUCCESS,roles));
        } catch (NamingException e) {
            LOGGER.severe(e.toString());
            return authenticated;
        }
        GenericPrincipal genericPrincipal = new GenericPrincipal(userPrincipal.getName(), "", roles, null);
        request.setUserPrincipal(genericPrincipal);
        register(request, response, genericPrincipal, request.getAuthType(), genericPrincipal.getName(), "");
        LOGGER.fine("User " + userPrincipal.getName() + "registered in " + request.getNote(org.apache.catalina.authenticator.Constants.REQ_SSOID_NOTE));
        return authenticated;
    }
}
