package ch.cern.sso.tomcat.valves;

import ch.cern.sso.tomcat.DbUserPrincipal;
import ch.cern.sso.tomcat.common.utils.Constants;
import ch.cern.sso.tomcat.common.utils.InitParamsUtils;
import ch.cern.sso.tomcat.common.utils.MessagesKeys;
import org.apache.catalina.User;
import org.apache.catalina.authenticator.BasicAuthenticator;
import org.apache.catalina.connector.Request;
import org.apache.catalina.realm.GenericPrincipal;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.security.Principal;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

public class OrdsBasicAuthValve extends BasicAuthenticator {
    private String rolePrefixes[];

    @Override
    protected Principal doLogin(Request request, String username, String password) throws ServletException {
        Principal principal = super.doLogin(request, username, password);
        this.initValveParameters(request);
        if (principal instanceof GenericPrincipal) {
            GenericPrincipal gp = (GenericPrincipal) principal;
            String[] roles = getFilteredRoles(gp.getRoles(),this.rolePrefixes);
            if (!(gp.getUserPrincipal() instanceof User)) {
                User userPrincipal = new DbUserPrincipal(gp.getName(), gp.getPassword(), roles);
                principal = new GenericPrincipal(gp.getName(), gp.getPassword(), Arrays.asList(roles), userPrincipal);
            }
        }
        return principal;
    }
    private String[] getFilteredRoles(String[] roles, String[] rolePrefixes){
        if(rolePrefixes == null || rolePrefixes.length == 0)
            return roles;
        List<String> filteredRoles = new LinkedList<>();
        for(String role : roles)
            for(String rolePrefix : rolePrefixes)
                if(hasRolePrefix(role, rolePrefix)){
                    filteredRoles.add(role);
                    break;
                }
        return filteredRoles.toArray(new String[0]);
    }
    private boolean hasRolePrefix(String role, String prefix){
        return role.indexOf(prefix) == 0;
    }
    private void initValveParameters(Request request) throws ServletException {
        ServletContext servletContext = request.getServletContext();
        InitParamsUtils initParamsUtils = new InitParamsUtils();
        String[] rolePrefixes = initParamsUtils.getInitParameter(servletContext.getInitParameter(Constants.ORDS_ROLE_PREFIXES), Constants.ORDS_ROLE_PREFIXES, ",", false, Level.FINEST, MessagesKeys.NO_ORDS_ROLE_PREFIXES_CONFIGURED);
        this.rolePrefixes = rolePrefixes;
    }
}
