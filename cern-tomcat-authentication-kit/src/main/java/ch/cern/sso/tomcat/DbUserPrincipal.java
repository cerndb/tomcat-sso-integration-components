package ch.cern.sso.tomcat;

import org.apache.catalina.Group;
import org.apache.catalina.Role;
import org.apache.catalina.UserDatabase;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * Implementing the org.apache.catalina.User as a workaround for
 * ORDS oracle.dbtools.auth.container.catalina.CatalinaAuthenticator issues
 * @author jgraniec
 */
public class DbUserPrincipal implements org.apache.catalina.User{

    private final List<Role> rolesList;
    private List<Group> groupsList;
    private String username, password;


    public DbUserPrincipal(String username, String password, String[] roles) {
        this(username,password,roles,null);
    }

    public DbUserPrincipal(String username, String password, String[] roles, List<Group> groups) {
        //Java 7 compatible (TM)
        rolesList = new ArrayList<>(roles.length);
        for(String roleName:roles)
            rolesList.add(new DbRolePrincipal(roleName + " role", roleName));
        if(groups == null)
            groups = new ArrayList<>();
        this.groupsList = groups;
        this.username = username;
        this.password = password;
    }


    @Override
    public String getFullName() {
        return username;
    }

    @Override
    public void setFullName(String username){
        this.username = username;
    }

    @Override
    public Iterator<Group> getGroups() {
        return groupsList.iterator();
    }

    @Override
    public String getPassword() {
        return password;
    }


    @Override
    public void setPassword(String password) {
        this.password = password;

    }
    @Override
    public Iterator<Role> getRoles() {
        return rolesList.iterator();
    }

    @Override
    public UserDatabase getUserDatabase() {
        throw new UnsupportedOperationException("This is User implementation for JNDI realm purposes. " +
                "                                                   It does not support this operation");
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public void addGroup(Group group) {
        groupsList.add(group);
    }

    @Override
    public void addRole(Role role) {
        rolesList.add(role);
    }

    @Override
    public boolean isInGroup(Group group) {
        return groupsList.contains(group);
    }

    @Override
    public boolean isInRole(Role role) {
        synchronized (rolesList) {
            return rolesList.contains(role);
        }
    }

    @Override
    public void removeGroup(Group group) {
        groupsList.remove(group);
    }
    @Override
    public void removeGroups() {
        groupsList.clear();
    }
    @Override
    public void removeRole(Role role) {
        rolesList.remove(role);
    }
    @Override
    public void removeRoles() {
        rolesList.clear();
    }

    @Override
    public String getName() {
        return username;
    }
}