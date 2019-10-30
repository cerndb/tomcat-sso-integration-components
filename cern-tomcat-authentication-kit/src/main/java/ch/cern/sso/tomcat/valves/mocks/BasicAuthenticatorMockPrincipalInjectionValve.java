package ch.cern.sso.tomcat.valves.mocks;

import java.io.IOException;
import java.util.Arrays;
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
    
    private String username = MockConstants.PRINCIPAL_NAME;
    private String password = MockConstants.PRINCIPAL_PASSWORD;
    private String[] roleArray = MockConstants.ROLES;
    private String roles;

    @Override
    protected void initInternal() throws LifecycleException {
        if(this.roles!=null){
            this.roleArray = roles.split(",");
        }
        super.initInternal();
    }
    
    @Override
    protected boolean doAuthenticate(Request request, HttpServletResponse response) throws IOException {
        // Just check the authorization header is in the request
        MessageBytes authorization = request.getCoyoteRequest().getMimeHeaders().getValue("authorization");
        if (authorization != null) {
            GenericPrincipal genericPrincipal = new GenericPrincipal(this.username, this.password, Arrays.asList(this.roleArray), null);
            request.setUserPrincipal(genericPrincipal);
            register(request, response, genericPrincipal, super.getAuthMethod(), this.username, this.password);
            return true;
        } else {
            // If not leave the BasicAuthenticator shows the basic auth pop-up window
            super.doAuthenticate(request, response);
        }
        return false;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }
}
