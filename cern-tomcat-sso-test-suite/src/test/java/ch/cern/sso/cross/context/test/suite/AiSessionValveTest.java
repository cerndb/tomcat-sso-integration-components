/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.cern.sso.cross.context.test.suite;

import java.io.File;
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
public class AiSessionValveTest {

    static final String APP_SERVER_BASE_URL = "http://localhost:8082";
    static final String CONTEXT_PATH = "/web-module-3";
    static final String CHECK_COOKIES_SERVLET_PATH = "/cookie-info";
    static final String AI_SESSION_COOKIE_NAME = "AI_SESSION";
    static final String BASE_CONF_RESOURCE_PATH = "/keycloak-saml/testsaml-with-mappers.json";
    static final String CONTEXT_CONF_FOLDER = "web-module-3";
    static final String INJECTED_AI_SESSION_VALUE = "14F70F544BFE0044270BCAFDC04514F12219959444455EB37B5D95BD42B7BDFFE65DEFFA3D394E2B2CB6F4FB05FC073E5D7637535D715C256BBCCF6377438A9062D0AA3406343D7149131F2209051642B8F8E25E8CEE5367788477D97C12F1EAA4CBCC57";
    static final String NOT_FOUND_RESOURCE_FOR_COOKIE_INJECTION_TRICK = "/404";
    static final String ROOT_PATH = "/";

    static Tomcat tomcat = null;
    static WebDriver browser;

    @BeforeClass
    public static void initTomcat() throws Exception {
        tomcat = new Tomcat();
        tomcat.setPort(8082);
        File base = new File(AiSessionValveTest.class.getResource(BASE_CONF_RESOURCE_PATH)
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
    public void testAiSessionIsAdded() {
        browser.get(APP_SERVER_BASE_URL + CONTEXT_PATH + CHECK_COOKIES_SERVLET_PATH);
        Utils.assertStringIsDisplayed(browser, AI_SESSION_COOKIE_NAME);
        browser.close();
    }

    @Test
    public void testInjectedAiSessionIsDropped() {
        browser.get(APP_SERVER_BASE_URL + NOT_FOUND_RESOURCE_FOR_COOKIE_INJECTION_TRICK);
        // Inject more than one
        Random r = new Random();
        String value_0 = INJECTED_AI_SESSION_VALUE + r.nextInt();
        org.openqa.selenium.Cookie injected_ai_session_0 = new org.openqa.selenium.Cookie(AI_SESSION_COOKIE_NAME, value_0, CONTEXT_PATH);
        browser.manage().addCookie(injected_ai_session_0);
        String value_1 = INJECTED_AI_SESSION_VALUE + r.nextInt();
        org.openqa.selenium.Cookie injected_ai_session_1 = new org.openqa.selenium.Cookie(AI_SESSION_COOKIE_NAME, value_1, ROOT_PATH);
        browser.manage().addCookie(injected_ai_session_1);
        // Add other random cookies
        for (int i = 0; i < 10; i++) {
            String name = String.valueOf((char) (r.nextInt('z' - 'a') + 'a'));
            String value = String.valueOf(r.nextInt());
            org.openqa.selenium.Cookie injected_ai_session = new org.openqa.selenium.Cookie(name, value);
            browser.manage().addCookie(injected_ai_session);
        }
        browser.get(APP_SERVER_BASE_URL + CONTEXT_PATH + CHECK_COOKIES_SERVLET_PATH);
        Utils.assertStringIsNOTdisplayed(browser, value_0);
        Utils.assertStringIsNOTdisplayed(browser, value_1);
        browser.close();
    }

}
