package ch.cern.sso.tomcat.valves.mocks;

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import org.apache.catalina.authenticator.BasicAuthenticator;
import org.apache.catalina.connector.Request;
import org.apache.catalina.realm.GenericPrincipal;
import org.apache.tomcat.util.buf.MessageBytes;

/**
 *
 * @author lurodrig
 */
public class BasicAuthenticatorMockPrincipalInjectionValve extends BasicAuthenticator {

    @Override
    protected boolean doAuthenticate(Request request, HttpServletResponse response) throws IOException {
        // Just check the authorization header is in the request
        MessageBytes authorization = request.getCoyoteRequest().getMimeHeaders().getValue("authorization");
        if (authorization != null) {
            GenericPrincipal genericPrincipal = MockConstants.createGenericPrincipal(null);
            request.setUserPrincipal(genericPrincipal);
            register(request, response, genericPrincipal, super.getAuthMethod(), MockConstants.PRINCIPAL_NAME, MockConstants.PRINCIPAL_PASSWORD);
            return true;
        } else {
            // If not leave the BasicAuthenticator shows the basic auth pop-up window
            super.doAuthenticate(request, response);
        }
        return false;
    }
}
