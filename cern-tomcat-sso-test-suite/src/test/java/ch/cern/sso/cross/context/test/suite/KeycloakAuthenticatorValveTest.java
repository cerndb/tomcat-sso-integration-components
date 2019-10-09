/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.cern.sso.cross.context.test.suite;

import java.io.File;
import org.apache.catalina.startup.Tomcat;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

/**
 *
 * @author lurodrig
 */
public class KeycloakAuthenticatorValveTest {

    protected String AUTH_SERVER_URL = "http://localhost:8081/auth";
    protected String APP_SERVER_BASE_URL = "http://localhost:8082";

    static Tomcat tomcat = null;
    static WebDriver browser;
    static final String username = "lurodrig";
    static final String password = "password";
    
    @BeforeClass
    public static void initTomcat() throws Exception {
        tomcat = new Tomcat();
        tomcat.setPort(8082);
        File base = new File(KeycloakAuthenticatorValveTest.class.getResource("/keycloak-saml/testsaml-with-mappers.json")
                .getFile()).getParentFile();
        tomcat.addWebapp("/web-module-1", new File(base, "web-module-1").toString());
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

    protected void checkAtLoginPage(boolean postBinding) {
        if (postBinding) {
            assertAtLoginPagePostBinding();
        } else {
            assertAtLoginPageRedirectBinding();
        }
    }

    protected void checkLoggedOut(String mainUrl, boolean postBinding) {
        Assert.assertTrue(browser.getPageSource().contains("Bye!"));
        browser.get(mainUrl);
        checkAtLoginPage(postBinding);
    }

    protected void assertAtLoginPageRedirectBinding() {
        Assert.assertTrue(browser.getCurrentUrl().startsWith(AUTH_SERVER_URL + "/realms/demo/login-actions/authenticate"));
    }

    protected void assertAtLoginPagePostBinding() {
        Assert.assertTrue(browser.getCurrentUrl().startsWith(AUTH_SERVER_URL + "/realms/demo/login-actions/authenticate"));
    }

    protected void assertAtModuleContextStartsWith(String context) {
        Assert.assertTrue(browser.getCurrentUrl().startsWith(APP_SERVER_BASE_URL + context));
    }

    protected void assertAtModuleUrlEquals(String path) {
        Assert.assertTrue(browser.getCurrentUrl().equals(APP_SERVER_BASE_URL + path));
    }

    //@Test
    public void testRedirectToIdPLogin() {
        browser.get("http://localhost:8082/web-module-1/request-info");
        assertAtLoginPagePostBinding();
        browser.close();
    }

    //@Test
    public void testRedirectToIdPLoginAuthenticateAndServe() {
        browser.get("http://localhost:8082/web-module-3/");
        assertAtLoginPagePostBinding();
        Utils.login(browser, username, password);
        assertAtModuleContextStartsWith("/web-module-3");
        browser.close();
    }

    //@Test
    public void testRedirectToIdPLoginAuthenticateServeAndLogout() {
        browser.get("http://localhost:8082/web-module-3/");
        assertAtLoginPagePostBinding();
        Utils.login(browser, username, password);
        assertAtModuleContextStartsWith("/web-module-3");
        browser.get("http://localhost:8082/web-module-3/?GLO=true");
        checkLoggedOut(APP_SERVER_BASE_URL + "/web-module-3/cookie-info", false);
        browser.close();
    }

    //@Test
    public void testRedirectToIdPLoginAuthenticateServeAndRequestDifferentContext() throws InterruptedException {
        browser.get("http://localhost:8082/web-module-3/");
        assertAtLoginPagePostBinding();
        Utils.login(browser, username, password);
        assertAtModuleContextStartsWith("/web-module-3");
        // Make a request from the same browser another context
        // Invokes a servlet that displays user's principal name
        JavascriptExecutor je = (JavascriptExecutor) browser;
        Object response = ((JavascriptExecutor) browser).executeAsyncScript(
                "var callback = arguments[arguments.length - 1];"
                + "var xhr = new XMLHttpRequest();"
                + "xhr.open('GET', 'http://localhost:8082/web-module-4/session-info', true);"
                + "xhr.onreadystatechange = function() {"
                + "  if (xhr.readyState == 4) {"
                + "    callback(xhr.responseText);"
                + "  }"
                + "};"
                + "xhr.send();");
        Assert.assertTrue(((String) response).contains(username));
        browser.close();
    }
}
