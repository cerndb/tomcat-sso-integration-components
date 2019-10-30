package ch.cern.sso.tomcat.valves.mocks;

import ch.cern.sso.tomcat.common.utils.Constants;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.authenticator.BasicAuthenticator;
import org.apache.catalina.connector.Request;
import org.apache.catalina.realm.GenericPrincipal;
import org.apache.tomcat.util.buf.MessageBytes;

/**
 *
 * @author lurodrig
 */
public class BasicAuthenticatorMockPrincipalInjectionValve extends BasicAuthenticator {

    private String username;
    private String password;
    private String[] roles;

    @Override
    protected void initInternal() throws LifecycleException {
        this.username = MockConstants.PRINCIPAL_NAME;
        this.password = MockConstants.PRINCIPAL_PASSWORD;
        this.roles = MockConstants.ROLES;
    }

    @Override
    protected boolean doAuthenticate(Request request, HttpServletResponse response) throws IOException {
        try {
           this.initValveParameters(request);
            // Just check the authorization header is in the request
            MessageBytes authorization = request.getCoyoteRequest().getMimeHeaders().getValue("authorization");
            if (authorization != null) {
                GenericPrincipal genericPrincipal = new GenericPrincipal(this.username, this.password, Arrays.asList(this.roles), null);
                request.setUserPrincipal(genericPrincipal);
                register(request, response, genericPrincipal, super.getAuthMethod(), this.username, this.password);
                return true;
            } else {
                // If not leave the BasicAuthenticator shows the basic auth pop-up window
                super.doAuthenticate(request, response);
            }
        } catch (ServletException ex) {
            Logger.getLogger(BasicAuthenticatorMockPrincipalInjectionValve.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    private void initValveParameters(Request request) throws ServletException {
        ServletContext servletContext = request.getServletContext();
        this.username = servletContext.getInitParameter(Constants.MOCK_USERNAME);
        this.roles = servletContext.getInitParameter(Constants.MOCK_ROLES).split(",");
    }
}