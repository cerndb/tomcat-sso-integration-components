/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//        © Copyright 2020 CERN
//        This software is distributed under the terms of the GNU General Public Licence
//        version 3 (GPL Version 3), copied verbatim in the file "LICENSE". In applying
//        this licence, CERN does not waive the privileges and immunities granted to it
//        by virtue of its status as an Intergovernmental Organization or submit itself
//        to any jurisdiction.
package ch.cern.sso.cross.context.test.suite.utils;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import ch.cern.sso.cross.context.test.suite.KeycloakAuthenticatorValveTest;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;

/**
 *
 * @author lurodrig
 */
public class LaunchTomcat {

    public static void main(String[] args) {
       
        try {
            Tomcat tomcat = new Tomcat();
            tomcat.setPort(8082);
            File base = new File(KeycloakAuthenticatorValveTest.class.getResource("/keycloak-saml/testsaml-with-mappers.json")
                    .getFile()).getParentFile();
            //tomcat.addWebapp("/", new File(base, "root-module").toString());
            tomcat.addWebapp("/web-module-1", new File(base, "web-module-1").toString());
            //tomcat.addWebapp("/web-module-3", new File(base, "web-module-3").toString());
            //tomcat.addWebapp("/web-module-4", new File(base, "web-module-4").toString());
            //tomcat.addWebapp("/web-module-5", new File(base, "web-module-5").toString());
            //tomcat.addWebapp("/web-module-6", new File(base, "web-module-6").toString());
            //tomcat.getHost().getPipeline().addValve(null);
            tomcat.start();
            tomcat.getServer().await();
        } catch (LifecycleException ex) {
            Logger.getLogger(LaunchTomcat.class.getName()).log(Level.SEVERE, null, ex);
        }
        

    }
}
