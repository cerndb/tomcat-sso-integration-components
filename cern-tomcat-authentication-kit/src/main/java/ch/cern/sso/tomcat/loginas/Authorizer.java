/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.cern.sso.tomcat.loginas;

import ch.cern.sso.tomcat.exceptions.UserIsNotInRoleException;
import ch.cern.sso.tomcat.exceptions.UserIsInRoleException;
import ch.cern.sso.tomcat.common.utils.ArrayManipulator;
import ch.cern.sso.tomcat.common.utils.PrincipalWrapper;
import ch.cern.sso.tomcat.valves.KeycloakAuthenticatorValve;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author lurodrig
 */
public class Authorizer {
    
    private final static Logger LOGGER = Logger.getLogger(Authorizer.class.getName());
    
     public void autorizeLoginAs(String[] groupsAllowed, Cookie loginAsCookie, PrincipalWrapper principalWrapper, HttpServletResponse response, boolean isLoginAsEnabled) throws IOException {
        if (loginAsCookie != null) {
            if (isLoginAsEnabled) {
                try {
                    // Check if the user has rights to add this cookie
                    allow(groupsAllowed, principalWrapper.getRoles(), principalWrapper.getName());
                } catch (UserIsNotInRoleException ex) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, ex.getMessage());
                    Logger.getLogger(KeycloakAuthenticatorValve.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public void forbid(String[] groupsForbidden, List<String> roles, String username) throws UserIsInRoleException {
        // groups.forbidden = NONE means that we can skip the check
        if (groupsForbidden != null) {
            String[] intersection = ArrayManipulator.intersection(groupsForbidden, ArrayManipulator.toArray(roles));
            if (intersection != null && intersection.length > 0) {
                throw new UserIsInRoleException("User " + username + " is member of these FORBIDDEN groups: " + ArrayManipulator.join(",", intersection));
            }
        }
    }

    public void allow(String[] groupsAllowed, List<String> roles, String username) throws UserIsNotInRoleException {
        // groups.allowed = ALL means that we can skip this check
        if (groupsAllowed != null) {
            String[] intersection = ArrayManipulator.intersection(groupsAllowed, ArrayManipulator.toArray(roles));
            if (intersection.length == 0) {
                throw new UserIsNotInRoleException("User " + username + " is NOT member of any of the ALLOWED groups " + Arrays.toString(groupsAllowed));
            }
        }
    }
}
