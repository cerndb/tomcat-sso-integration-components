/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.cern.sso.cross.context.test.suite;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.catalina.startup.Tomcat;

/**
 *
 * @author lurodrig
 */
public class InitTestEnvironment {

    public static String AUTH_SERVER_URL = "http://localhost:8081/auth";
    public static String APP_SERVER_BASE_URL = "http://localhost:8082";

    public static Tomcat initTomcat(String rootFile, HashMap<String, String> contextPathBaseDir) throws Exception {
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(8082);
        File base = new File(InitTestEnvironment.class.getResource(rootFile).getFile()).getParentFile();
        Iterator<Map.Entry<String, String>> iterator = contextPathBaseDir.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            tomcat.addWebapp(entry.getKey(), new File(base, entry.getValue()).toString());
        }
        tomcat.start();
        return tomcat;
    }

    public static HtmlUnitTestDriver initBrowser() {
        HtmlUnitTestDriver d = new HtmlUnitTestDriver();
        d.getWebClient().getOptions().setJavaScriptEnabled(true);
        d.getWebClient().getOptions().setCssEnabled(false);
        d.getWebClient().getOptions().setTimeout(1000000);
        return d;
    }
}
