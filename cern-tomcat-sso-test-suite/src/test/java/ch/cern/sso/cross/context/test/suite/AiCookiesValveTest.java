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
package ch.cern.sso.cross.context.test.suite;

import ch.cern.sso.cross.context.test.suite.utils.Cookies.CookieFactory;
import ch.cern.sso.cross.context.test.suite.utils.Cookies.CookieRetriever;
import ch.cern.sso.cross.context.test.suite.utils.Cookies.CookieServletCookieRetriever;
import ch.cern.sso.cross.context.test.suite.utils.Cookies.RequestServletCookieRetriever;
import ch.cern.sso.cross.context.test.suite.utils.HtmlUnitTestDriver;
import ch.cern.sso.cross.context.test.suite.utils.Utils;
import ch.cern.sso.tomcat.common.utils.Constants;
import ch.cern.sso.tomcat.valves.mocks.MockConstants;
import org.apache.catalina.startup.Tomcat;
import org.junit.*;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;

import java.io.File;

/**
 *
 * @author lurodrig
 */
public class AiCookiesValveTest {

    private static final String APP_SERVER_BASE_URL = "http://localhost:8082";
    private static final String CONTEXT_PATH = "/aicookies-nologinas";
    private static final String CHECK_COOKIES_SERVLET_PATH = "/cookie-info";
    private static final String CHECK_REQUEST_SERVLET_PATH = "/request-info";
    private static final String BASE_CONF_RESOURCE_PATH = "/ai-cookies/aicookies-loginas";
    private static final String CONTEXT_CONF_FOLDER = "aicookies-nologinas";
    private static final String NOT_FOUND_RESOURCE_FOR_COOKIE_INJECTION_TRICK = "/404";
    private static final Cookie[] expectedCookies = {
            new Cookie(Constants.AI_USERNAME, MockConstants.PRINCIPAL_NAME),
            new Cookie(Constants.AI_USER, MockConstants.PRINCIPAL_NAME),
            new Cookie(Constants.AI_IDENTITY_CLASS, MockConstants.SSO_CLAIM_IDENTITY_CLASS),
            new Cookie(Constants.AI_LANG, MockConstants.SSO_CLAIM_PREFERRED_LANGUAGE.substring(0,1)),
            new Cookie(Constants.AI_HRID, MockConstants.SSO_CLAIM_PERSON_ID)
    };

    private static Tomcat tomcat = null;
    private static WebDriver browser;

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
        browser.get(APP_SERVER_BASE_URL + NOT_FOUND_RESOURCE_FOR_COOKIE_INJECTION_TRICK);// You first have to navigate to some page before setting cookies
    }

    @After
    public void closeBrowser(){
        browser.close();
    }


    @Test
    public void testAiCookiesAreAdded() {


        browser.get(APP_SERVER_BASE_URL + CONTEXT_PATH + CHECK_COOKIES_SERVLET_PATH);
        CookieRetriever cookieRetriever = new CookieServletCookieRetriever(browser.getPageSource());
        Utils.assertCookiesContain(expectedCookies, cookieRetriever);

        browser.get(APP_SERVER_BASE_URL + CONTEXT_PATH + CHECK_REQUEST_SERVLET_PATH);
        cookieRetriever = new RequestServletCookieRetriever(browser.getPageSource());
        Utils.assertCookiesContain(expectedCookies, cookieRetriever);
    }

    @Test
    public void testInjectedAiCookiesAreDropped() {

        for (Cookie cookie : expectedCookies)
            browser.manage().addCookie(CookieFactory.createCookie(cookie.getName()));
        Cookie[] spoofedCookies = browser.manage().getCookies().toArray(new Cookie[0]);

        browser.get(APP_SERVER_BASE_URL + CONTEXT_PATH + CHECK_COOKIES_SERVLET_PATH);
        Utils.assertCookiesNotContain(spoofedCookies,new CookieServletCookieRetriever(browser.getPageSource()));

        browser.get(APP_SERVER_BASE_URL + CONTEXT_PATH + CHECK_REQUEST_SERVLET_PATH);
        Utils.assertCookiesNotContain(spoofedCookies,new RequestServletCookieRetriever(browser.getPageSource()));
    }

    @Test
    public void testOtherCookiesAreNotDropped() {
        for (int i=0;i<5;i++)
            browser.manage().addCookie(CookieFactory.createCookie());
        Cookie[] cookies = browser.manage().getCookies().toArray(new Cookie[0]);

        browser.get(APP_SERVER_BASE_URL + CONTEXT_PATH + CHECK_COOKIES_SERVLET_PATH);
        Utils.assertCookiesContain(cookies, new CookieServletCookieRetriever(browser.getPageSource()));

        browser.get(APP_SERVER_BASE_URL + CONTEXT_PATH + CHECK_REQUEST_SERVLET_PATH);
        Utils.assertCookiesContain(cookies, new RequestServletCookieRetriever(browser.getPageSource()));
    }

}
