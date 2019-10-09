package ch.cern.sso.tomcat.valves.mocks;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletResponse;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.apache.catalina.authenticator.AuthenticatorBase;
import org.apache.catalina.connector.Request;
import org.apache.catalina.realm.GenericPrincipal;
import org.keycloak.adapters.saml.SamlPrincipal;
import org.keycloak.common.util.MultivaluedHashMap;
import org.keycloak.dom.saml.v2.assertion.AssertionType;

/**
 *
 * @author lurodrig
 */
public class AuthenticatorMockValve extends AuthenticatorBase {
    
    private SamlPrincipal samlPrincipal;
    
    public AuthenticatorMockValve() {
        try {
            XMLGregorianCalendar issueInstant = DatatypeFactory.newInstance().newXMLGregorianCalendar();
            AssertionType assertion = new AssertionType(MockConstants.SAML_PRINCIPAL_ID, issueInstant);
            MultivaluedHashMap<String, String> attributes = new MultivaluedHashMap<>();
            MultivaluedHashMap<String, String> friendlyAttributes = new MultivaluedHashMap<>();
            this.samlPrincipal = new SamlPrincipal(assertion, MockConstants.PRINCIPAL_NAME, MockConstants.SAML_PRINCIPAL_SUBJECT, MockConstants.SAML_PRINCIPAL_NAME_ID_FORMAT, attributes, friendlyAttributes);
        } catch (DatatypeConfigurationException ex) {
            Logger.getLogger(AuthenticatorMockValve.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    protected boolean doAuthenticate(Request request, HttpServletResponse response) throws IOException {
        List<String> roles = Arrays.asList(MockConstants.ROLES);
        GenericPrincipal genericPrincipal = new GenericPrincipal(MockConstants.PRINCIPAL_NAME, MockConstants.PRINCIPAL_PASSWORD, roles, this.samlPrincipal);
        request.setUserPrincipal(genericPrincipal);
        register(request, response, genericPrincipal, this.getAuthMethod(), this.samlPrincipal.getName(), MockConstants.PRINCIPAL_PASSWORD);
        return true;
    }

    @Override
    protected String getAuthMethod() {
        return "I am mock!";
    }

}
