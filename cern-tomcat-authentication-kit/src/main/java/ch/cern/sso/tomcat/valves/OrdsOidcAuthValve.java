package ch.cern.sso.tomcat.valves;

import ch.cern.sso.tomcat.common.utils.Constants;
import ch.cern.sso.tomcat.common.utils.InitParamsUtils;
import ch.cern.sso.tomcat.common.utils.MessagesKeys;
import ch.cern.sso.tomcat.realms.JNDIUserRealm;
import org.apache.catalina.Realm;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.realm.GenericPrincipal;
import org.apache.catalina.realm.JNDIRealm;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.adapters.AdapterTokenStore;
import org.keycloak.adapters.KeycloakDeployment;
import org.keycloak.adapters.spi.AuthOutcome;
import org.keycloak.adapters.tomcat.CatalinaHttpFacade;
import org.keycloak.adapters.tomcat.CatalinaRequestAuthenticator;
import org.keycloak.adapters.tomcat.OIDCCatalinaHttpFacade;

import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author jgraniec
 */
public class OrdsOidcAuthValve extends org.keycloak.adapters.tomcat.KeycloakAuthenticatorValve{
    private final static Logger LOGGER = Logger.getLogger("ch.cern.sso.tomcat.valve.OrdsOidcAuthValve");
    private final ResourceBundle messages = ResourceBundle.getBundle("Messages");
    private boolean addJndiRoles = false;
    private InitParamsUtils initParamsUtils;
    private boolean isValveInitialized = false;

    @Override
    protected void initInternal() {
        super.initInternal();
        this.initParamsUtils = new InitParamsUtils();
    }

    @Override
    public void invoke(Request request, Response response) throws IOException, ServletException {
        if(!this.isValveInitialized)
            initValveParameters(request);
        if(request.getHeader("authorization") != null && request.getHeader("authorization").contains("Bearer")) {
            checkToken(request,response);
            if(addJndiRoles && request.getUserPrincipal() != null ){
                addJndiRoles(request,response);
            }
        }
        super.invoke(request,response);
    }

    private void addJndiRoles(Request request,Response response) {
        Realm realm = context.getRealm();
        Principal principal = request.getUserPrincipal();

        if(!(principal instanceof KeycloakPrincipal)|| !(realm instanceof JNDIRealm) )
            return;
        JNDIUserRealm jndiUserRealm = new JNDIUserRealm((JNDIRealm) realm);

        String principalName = request.getUserPrincipal().getName();
        KeycloakPrincipal keycloakPrincipal =(KeycloakPrincipal) request.getUserPrincipal();
        try {
            List<String> roles = jndiUserRealm.getUserRoles(principalName);
            GenericPrincipal genericPrincipal = new GenericPrincipal(principalName, "", roles, keycloakPrincipal);
            request.setUserPrincipal(genericPrincipal);
            register(request, response, genericPrincipal, request.getAuthType(), genericPrincipal.getName(), "");
        } catch (NamingException e) {
            LOGGER.severe(e.toString());
        }
    }

    private AuthOutcome checkToken(Request request, Response response){
        CatalinaHttpFacade facade = new OIDCCatalinaHttpFacade(request, response);
        KeycloakDeployment deployment = this.deploymentContext.resolveDeployment(facade);
        if (deployment != null && deployment.isConfigured()) {
            AdapterTokenStore tokenStore = this.getTokenStore(request, facade, deployment);
            this.nodesRegistrationManagement.tryRegister(deployment);
            CatalinaRequestAuthenticator authenticator = this.createRequestAuthenticator(request, facade, deployment, tokenStore);
            AuthOutcome outcome = authenticator.authenticate();
            return outcome;
        }
        return null;
    }
    private void initValveParameters(Request request) throws ServletException {
        this.isValveInitialized = true;
        ServletContext servletContext = request.getServletContext();
        this.addJndiRoles = Boolean.parseBoolean(this.initParamsUtils.getInitParameter(servletContext.getInitParameter(Constants.IS_ENABLED_OVERRIDE_JNDI_ROLES), Constants.IS_ENABLED_OVERRIDE_JNDI_ROLES, false, Level.FINEST, MessagesKeys.NO_IS_ENABLED_OVERRIDE_JNDI_ROLES));
    }


}
