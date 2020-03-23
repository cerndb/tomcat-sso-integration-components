/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.cern.sso.cross.context.test.suite;

import ch.cern.sso.cross.context.test.suite.utils.HtmlUnitTestDriver;
import ch.cern.sso.cross.context.test.suite.utils.Utils;
import org.apache.catalina.startup.Tomcat;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.WebDriver;

import java.io.File;

/**
 *
 * @author lurodrig
 */
public class SsoHeadersValveTest {

    protected String APP_SERVER_BASE_URL = "http://localhost:8082";

    private static Tomcat tomcat = null;
    private static final String SPOOFED_SSO_REMOTE_USER_HEADER_VALUE = "spoof";

    @BeforeClass
    public static void initTomcat() throws Exception {
        tomcat = new Tomcat();
        tomcat.setPort(8082);
        File base = new File(SsoHeadersValveTest.class.getResource("/keycloak-saml/testsaml-with-mappers.json")
                .getFile()).getParentFile();
        tomcat.addWebapp("/web-module-2", new File(base, "web-module-2").toString());
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
    public void testSsoRemoteHeaderIsNotSpoofed() {
        String[] ssoRemoteUserHeaders = {"SSO_REMOTE_USER", "sso_remote_user", "SSO_remote_User"};
        for (String ssoRemoteHeader : ssoRemoteUserHeaders) {
            WebDriver browser = getBrowser(ssoRemoteHeader, SPOOFED_SSO_REMOTE_USER_HEADER_VALUE);
            browser.get("http://localhost:8082/web-module-2/request-info");
            Utils.assertStringIsNOTdisplayed(browser, SPOOFED_SSO_REMOTE_USER_HEADER_VALUE);
            browser.close();
        }
    }
}
