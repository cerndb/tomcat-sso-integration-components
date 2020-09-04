//        © Copyright 2020 CERN
//        This software is distributed under the terms of the GNU General Public Licence
//        version 3 (GPL Version 3), copied verbatim in the file "LICENSE". In applying
//        this licence, CERN does not waive the privileges and immunities granted to it
//        by virtue of its status as an Intergovernmental Organization or submit itself
//        to any jurisdiction.
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
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 * This class is a workaround for problems with ORDS' REST basic authentication using Tomcat realms other that UserDatabaseRealm.
 * @see <a href="https://db-blog.web.cern.ch/blog/jakub-granieczny/2019-12-oracle-rest-data-services-running-tomcat-basic-authentication-using">CERN IT-DB Blog entry</a>
 * @author jgraniec
 *
 */
public class OrdsBasicAuthValve extends BasicAuthenticator {

    private final static Logger LOGGER = Logger.getLogger("ch.cern.sso.tomcat.valve.OrdsBasicAuthValve");
    private final ResourceBundle messages = ResourceBundle.getBundle("Messages");

    private String rolePrefixes[];

    @Override
    protected Principal doLogin(Request request, String username, String password) throws ServletException {
        Principal principal = super.doLogin(request, username, password);
        this.initValveParameters(request);
        LOGGER.finest( MessageFormat.format(MessagesKeys.PRINCIPAL_INFO, principal.toString()));
        if (principal instanceof GenericPrincipal) {
            GenericPrincipal gp = (GenericPrincipal) principal;
            LOGGER.finer( MessageFormat.format(MessagesKeys.PRINCIPAL_ROLES, Arrays.toString(gp.getRoles())));
            String[] roles = getFilteredRoles(gp.getRoles(),this.rolePrefixes);
            LOGGER.finer( MessageFormat.format(MessagesKeys.FILTERED_ROLES, Arrays.toString(roles)));
            if (!(gp.getUserPrincipal() instanceof User)) {
                LOGGER.finest( MessageFormat.format(MessagesKeys.PRINCIPAL_NOT_INSTANCE_OF_USER, gp.getUserPrincipal().toString(), gp.toString()));
                User userPrincipal = new DbUserPrincipal(gp.getName(), gp.getPassword(), roles);
                principal = new GenericPrincipal(gp.getName(), gp.getPassword(), Arrays.asList(roles), userPrincipal);
                LOGGER.finest( MessageFormat.format(MessagesKeys.NEW_PRINCIPAL_CREATED, principal.toString(), userPrincipal.toString()));

            }
        }
        LOGGER.finest( MessageFormat.format(MessagesKeys.RETURNED_PRINCIPAL, principal));

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
        LOGGER.fine( MessageFormat.format(MessagesKeys.ROLE_PREFIXES_FOUND, Arrays.toString(rolePrefixes)));
        this.rolePrefixes = rolePrefixes;
    }
}
