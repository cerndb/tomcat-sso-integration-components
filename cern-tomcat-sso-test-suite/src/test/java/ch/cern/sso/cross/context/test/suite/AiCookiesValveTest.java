/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.cern.sso.cross.context.test.suite;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.apache.catalina.startup.Tomcat;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.WebDriver;

/**
 *
 * @author lurodrig
 */
public class AiCookiesValveTest {

    static final String APP_SERVER_BASE_URL = "http://localhost:8082";
    static final String CONTEXT_PATH = "/web-module-4";
    static final String CHECK_COOKIES_SERVLET_PATH = "/cookie-info";
    static final String AI_SESSION_COOKIE_NAME = "AI_SESSION";
    static final String BASE_CONF_RESOURCE_PATH = "/keycloak-saml/testsaml-with-mappers.json";
    static final String CONTEXT_CONF_FOLDER = "web-module-4";
    static final String NOT_FOUND_RESOURCE_FOR_COOKIE_INJECTION_TRICK = "/404";
    static final String ROOT_PATH = "/";
    static final String[] aiCookieNames = {"AI_USERNAME", "AI_USER", "AI_IDENTITY_CLASS", "AI_LANG", "AI_HRID"};

    static Tomcat tomcat = null;
    static WebDriver browser;

    @BeforeClass
    public static void initTomcat() throws Exception {
        tomcat = new Tomcat();
        tomcat.setPort(8082);
        File base = new File(AiCookiesValveTest.class.getResource(BASE_CONF_RESOURCE_PATH)
                .getFile()).getParentFile();
        tomcat.addWebapp(CONTEXT_PATH, new File(base, CONTEXT_CONF_FOLDER).toString());
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
    public void testAiCookiesAreAdded() {
        browser.get(APP_SERVER_BASE_URL + CONTEXT_PATH + CHECK_COOKIES_SERVLET_PATH);
        for (String cookieName : aiCookieNames) {
            Utils.assertStringIsDisplayed(browser, cookieName);
        }
        browser.close();
    }

    @Test
    public void testInjectedAiCookiesAreDropped() {
        browser.get(APP_SERVER_BASE_URL + NOT_FOUND_RESOURCE_FOR_COOKIE_INJECTION_TRICK);
        // Inject more than one
        Random r = new Random();
        List<String> values = new ArrayList<String>();
        for (String cookieName : aiCookieNames) {
            String value = String.valueOf(r.nextInt());
            org.openqa.selenium.Cookie injected = new org.openqa.selenium.Cookie(cookieName, value);
            values.add(value);
            browser.manage().addCookie(injected);
        }
        browser.get(APP_SERVER_BASE_URL + CONTEXT_PATH + CHECK_COOKIES_SERVLET_PATH);
         for (String value : values) {
            Utils.assertStringIsNOTdisplayed(browser, value);
        }
        browser.close();
    }

}
