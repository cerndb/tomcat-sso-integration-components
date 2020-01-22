package ch.cern.sso.tomcat;


import org.apache.catalina.Role;
import org.apache.catalina.UserDatabase;

public class DbRolePrincipal implements Role {
    String desc, name;
    public DbRolePrincipal(String desc, String name){
        this.desc=desc;
        this.name=name;
    }
    @Override
    public String getDescription() {
        return desc;
    }

    @Override
    public void setDescription(String s) {
        this.desc=s;
    }

    @Override
    public String getRolename() {
        return name;
    }

    @Override
    public void setRolename(String s) {
        this.name=s;
    }

    @Override
    public UserDatabase getUserDatabase() {
        return null;
    }

    @Override
    public String getName() {
        return this.name;
    }
}
