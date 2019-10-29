/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.cern.sso.cross.context.test.suite;

import ch.cern.sso.tomcat.valves.mocks.MockConstants;
import java.io.File;
import org.apache.catalina.startup.Tomcat;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.WebDriver;

/**
 *
 * @author lurodrig
 */
public class BasicAuthenticatorMockPrincipalInjectionValveTest {

    protected String APP_SERVER_BASE_URL = "http://localhost:8082";

    static Tomcat tomcat = null;
    //static WebDriver browser;
    static final String username = "whatever";
    static final String password = "whatever";

    @BeforeClass
    public static void initTomcat() throws Exception {
        tomcat = new Tomcat();
        tomcat.setPort(8082);
        File base = new File(BasicAuthenticatorMockPrincipalInjectionValveTest.class.getResource("/keycloak-saml/testsaml-with-mappers.json")
                .getFile()).getParentFile();
        tomcat.addWebapp("/web-module-1", new File(base, "web-module-1").toString());
        tomcat.start();
    }

    @AfterClass
    public static void shutdownTomcat() throws Exception {
        tomcat.stop();
        tomcat.destroy();
    }

    public WebDriver getBrowser(String headerName, String headerValue) {
        HtmlUnitTestDriver d = new HtmlUnitTestDriver();
        d.getWebClient().getOptions().setJavaScriptEnabled(true);
        d.getWebClient().getOptions().setCssEnabled(false);
        d.getWebClient().getOptions().setTimeout(1000000);
        if (headerName != null) {
            d.getWebClient().addRequestHeader(headerName, headerValue);
        }
        return d;
    }

    @Test
    public void testUserIsAuthenticated() {
        WebDriver browser = getBrowser(null, null);
        browser.get("http://localhost:8082/web-module-1/principal-info");
        Utils.assertTitleEquals(browser, "HTTP Status 401 – Unauthorized");
        browser = getBrowser("Authorization", "Basic YWxhZGRpbjpvcGVuc2VzYW1l");
        browser.get("http://localhost:8082/web-module-1/principal-info");
        Utils.assertStringIsDisplayed(browser, MockConstants.PRINCIPAL_NAME);
        browser.close();
    }

}
