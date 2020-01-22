package ch.cern.sso.tomcat.valves;

import ch.cern.sso.tomcat.DbUserPrincipal;
import org.apache.catalina.User;
import org.apache.catalina.authenticator.BasicAuthenticator;
import org.apache.catalina.connector.Request;
import org.apache.catalina.realm.GenericPrincipal;

import javax.servlet.ServletException;
import java.security.Principal;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class OrdsBasicAuthValve extends BasicAuthenticator {
    final private String ROLE_PREFIX = "ords-rest-access-";

    @Override
    protected Principal doLogin(Request request, String username, String password) throws ServletException {
        Principal principal = super.doLogin(request, username, password);
        if (principal instanceof GenericPrincipal) {
            GenericPrincipal gp = (GenericPrincipal) principal;
            String[] roles = getFilteredRoles(gp.getRoles());
            if (!(gp.getUserPrincipal() instanceof User)) {
                User userPrincipal = new DbUserPrincipal(gp.getName(), gp.getPassword(), roles);
                principal = new GenericPrincipal(gp.getName(), gp.getPassword(), Arrays.asList(roles), userPrincipal);
            }
        }
        return principal;
    }
    private String[] getFilteredRoles(String[] roles){
        List<String> filteredRoles = new LinkedList<>();
        for(String role : roles)
            if(hasRolePrefix(role, ROLE_PREFIX))
                filteredRoles.add(role);
        return filteredRoles.toArray(new String[filteredRoles.size()]);
    }
    private boolean hasRolePrefix(String role, String prefix){
        return role.indexOf(prefix) == 0;

    }
}
