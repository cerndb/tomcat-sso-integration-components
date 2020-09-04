//        © Copyright 2020 CERN
//        This software is distributed under the terms of the GNU General Public Licence
//        version 3 (GPL Version 3), copied verbatim in the file "LICENSE". In applying
//        this licence, CERN does not waive the privileges and immunities granted to it
//        by virtue of its status as an Intergovernmental Organization or submit itself
//        to any jurisdiction.
package ch.cern.sso.tomcat.realms;

import org.apache.catalina.realm.JNDIRealm;
import org.apache.catalina.realm.RealmBase;
import org.apache.juli.logging.LogFactory;

import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import java.util.ArrayList;
import java.util.List;

/**
 * @author jgraniec
 */
public class JNDIUserRealm extends JNDIRealm {

    private DirContext context;
    public JNDIUserRealm(JNDIRealm jNDIRealm) {
        this.setAuthentication(jNDIRealm.getAuthentication());
        this.setConnectionName(jNDIRealm.getConnectionName());
        this.setConnectionPassword(jNDIRealm.getConnectionPassword());
        this.setConnectionURL(jNDIRealm.getConnectionURL());
        this.setDerefAliases(jNDIRealm.getDerefAliases());
        this.setProtocol(jNDIRealm.getProtocol());
        this.setAdCompat(jNDIRealm.getAdCompat());
        this.setReferrals(jNDIRealm.getReferrals());
        this.setUserBase(jNDIRealm.getUserBase());
        this.setUserSearch(jNDIRealm.getUserSearch());
        this.setUserSubtree(jNDIRealm.getUserSubtree());
        this.setUserPassword(jNDIRealm.getUserPassword());
        this.setUserRoleAttribute(jNDIRealm.getUserRoleAttribute());
        this.setUserPattern(jNDIRealm.getUserPattern());
        this.setRoleBase(jNDIRealm.getRoleBase());
        this.setUserRoleName(jNDIRealm.getUserRoleName());
        this.setRoleName(jNDIRealm.getRoleName());
        this.setRoleSearch(jNDIRealm.getRoleSearch());
        this.setRoleSubtree(jNDIRealm.getRoleSubtree());
        this.setRoleNested(jNDIRealm.getRoleNested());
        this.setAlternateURL(jNDIRealm.getAlternateURL());
        this.setCommonRole(jNDIRealm.getCommonRole());
        this.setConnectionTimeout(jNDIRealm.getConnectionTimeout());
        this.setReadTimeout(jNDIRealm.getReadTimeout());
        this.setSizeLimit(jNDIRealm.getSizeLimit());
        this.setSpnegoDelegationQop(jNDIRealm.getSpnegoDelegationQop());
        this.setContainer(jNDIRealm.getContainer());
        this.setUserRoleName(jNDIRealm.getRoleName());
        try {
            // Ensure that we have a directory context available
            this.containerLog = LogFactory.getLog(RealmBase.class);
            context = open();
        } catch (NamingException e) {
            // Log the problem for posterity
            containerLog.error(sm.getString("jndiRealm.exception"), e);
            if (context != null)
                close(context);
        }
    }

    public User getUserPublic(String username) throws NamingException {
        return this.getUser(context, username, null, 0);
    }
    public List<String> getUserRoles(String username) throws NamingException {
        return getUserRoles(getUserPublic(username));
    }
    private List<String> getUserRoles(User user) throws NamingException{
        if(user == null)
            return new ArrayList<>();
        return getRoles(context,user);
    }
}
