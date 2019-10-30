package ch.cern.sso.tomcat.valves.mocks;

import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletResponse;
import javax.xml.datatype.DatatypeConfigurationException;
import org.apache.catalina.authenticator.AuthenticatorBase;
import org.apache.catalina.connector.Request;
import org.apache.catalina.realm.GenericPrincipal;
import org.keycloak.adapters.saml.SamlPrincipal;

/**
 *
 * @author lurodrig
 */
public class AuthenticatorMockValve extends AuthenticatorBase {

    private SamlPrincipal samlPrincipal;
    private String username = MockConstants.PRINCIPAL_NAME;
    private String password = MockConstants.PRINCIPAL_PASSWORD;
    private String[] roles = MockConstants.ROLES;

    public AuthenticatorMockValve() {
        try {
            this.samlPrincipal = MockConstants.createSamlPrincipal();
        } catch (DatatypeConfigurationException ex) {
            Logger.getLogger(AuthenticatorMockValve.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    protected boolean doAuthenticate(Request request, HttpServletResponse response) throws IOException {
        GenericPrincipal genericPrincipal = new GenericPrincipal(username, password, Arrays.asList(roles), samlPrincipal);
        request.setUserPrincipal(genericPrincipal);
        register(request, response, genericPrincipal, this.getAuthMethod(), this.samlPrincipal.getName(), password);  
        return true;
    }

    @Override
    protected String getAuthMethod() {
        return "I am mock!";
    }

}
