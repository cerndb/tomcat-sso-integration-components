/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.cern.sso.cross.context.test.suite;

import ch.cern.sso.cross.context.test.suite.utils.HtmlUnitTestDriver;
import ch.cern.sso.cross.context.test.suite.utils.Utils;
import ch.cern.sso.tomcat.valves.mocks.MockConstants;
import org.apache.catalina.startup.Tomcat;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.WebDriver;

import java.io.File;

/**
 *
 * @author lurodrig
 */
public class AuthenticatorMockValveTest {

    protected String APP_SERVER_BASE_URL = "http://localhost:8082";

    static Tomcat tomcat = null;
    static WebDriver browser;
    static final String username = "lurodrig";
    static final String password = "password";
    
    @BeforeClass
    public static void initTomcat() throws Exception {
        tomcat = new Tomcat();
        tomcat.setPort(8082);
        File base = new File(AuthenticatorMockValveTest.class.getResource("/keycloak-saml/testsaml-with-mappers.json")
                .getFile()).getParentFile();
        tomcat.addWebapp("/web-module-2", new File(base, "web-module-2").toString());
        tomcat.start();
    }
   
    @AfterClass
    public static void shutdownTomcat() throws Exception {
        tomcat.stop();
        tomcat.destroy();
    }

    @Before
    public void initBrowser() {
        HtmlUnitTestDriver d = new HtmlUnitTestDriver();
        d.getWebClient().getOptions().setJavaScriptEnabled(true);
        d.getWebClient().getOptions().setCssEnabled(false);
        d.getWebClient().getOptions().setTimeout(1000000);
        browser = d;
    }

    @Test
    public void testUserIsAuthenticated() {
        browser.get("http://localhost:8082/web-module-2/principal-info");
        Utils.assertStringIsDisplayed(browser, MockConstants.PRINCIPAL_NAME);
        browser.close();
    }

}
