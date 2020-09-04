//        © Copyright 2020 CERN
//        This software is distributed under the terms of the GNU General Public Licence
//        version 3 (GPL Version 3), copied verbatim in the file "LICENSE". In applying
//        this licence, CERN does not waive the privileges and immunities granted to it
//        by virtue of its status as an Intergovernmental Organization or submit itself
//        to any jurisdiction.
package ch.cern.sso.tomcat;


import org.apache.catalina.Role;
import org.apache.catalina.UserDatabase;

/**
 *
 * @author jgraniec
 */
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
