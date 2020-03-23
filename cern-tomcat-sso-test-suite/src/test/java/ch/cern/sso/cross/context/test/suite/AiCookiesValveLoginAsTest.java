/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.cern.sso.cross.context.test.suite;

import ch.cern.sso.cross.context.test.suite.utils.Cookies.CookieFactory;
import ch.cern.sso.cross.context.test.suite.utils.Cookies.CookieRetriever;
import ch.cern.sso.cross.context.test.suite.utils.Cookies.CookieServletCookieRetriever;
import ch.cern.sso.cross.context.test.suite.utils.Cookies.RequestServletCookieRetriever;
import ch.cern.sso.cross.context.test.suite.utils.HtmlUnitTestDriver;
import ch.cern.sso.cross.context.test.suite.utils.Utils;
import ch.cern.sso.tomcat.common.utils.Constants;
import ch.cern.sso.tomcat.valves.mocks.MockConstants;
import com.sun.istack.Nullable;
import org.apache.catalina.startup.Tomcat;
import org.junit.*;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;

import java.io.File;

/**
 *
 * @author jgraniec
 */
public class AiCookiesValveLoginAsTest {

    private static final String APP_SERVER_BASE_URL = "http://localhost:8082";
    private static final String CONTEXT_PATH_LOGINAS = "/aicookies-loginas";
    private static final String CONTEXT_PATH_NOLOGINAS = "/aicookies-nologinas";
    private static final String CONTEXT_PATH_LOGINAS_NOGROUPS = "/aicookies-loginas-nogroupsallowed";

    private static final String CHECK_COOKIES_SERVLET_PATH = "/cookie-info";
    private static final String CHECK_REQUEST_SERVLET_PATH = "/request-info";
    private static final String BASE_CONF_RESOURCE_PATH = "/ai-cookies/aicookies-loginas";
    private static final String CONTEXT_CONF_FOLDER_LOGINAS = "aicookies-loginas";
    private static final String CONTEXT_CONF_FOLDER_NOLOGINAS = "aicookies-nologinas";
    private static final String CONTEXT_CONF_FOLDER_LOGINAS_NOGROUPS = "aicookies-loginas-nogroupsallowed";

    private static final String NOT_FOUND_RESOURCE_FOR_COOKIE_INJECTION_TRICK = "/404";
    private static final String[] aiCookieNames = {Constants.AI_USERNAME, Constants.AI_USER,
            Constants.AI_IDENTITY_CLASS, Constants.AI_LANG, Constants.AI_HRID};
    private static final String AI_LOGIN = "jgraniec";
    private static final String AI_HRID = "820233";
    private static final String AI_LANG = "E";


    private static final String AI_LOGIN_AS_COOKIE_VALUE = AI_LOGIN+":"+AI_HRID+":"+AI_LANG;
    private static final Cookie[] expectedLoginasCookies = {
            new Cookie(Constants.AI_USERNAME, AI_LOGIN),
            new Cookie(Constants.AI_USER, AI_LOGIN),
            new Cookie(Constants.AI_IDENTITY_CLASS, MockConstants.SSO_CLAIM_IDENTITY_CLASS),
            new Cookie(Constants.AI_LANG, AI_LANG),
            new Cookie(Constants.AI_HRID, AI_HRID),
            new Cookie(Constants.AI_LOGIN_AS,AI_LOGIN_AS_COOKIE_VALUE),
            new Cookie(Constants.ORIGINAL_AI_USERNAME_COOKIE, MockConstants.PRINCIPAL_NAME),
            new Cookie(Constants.ORIGINAL_AI_USER_COOKIE, MockConstants.PRINCIPAL_NAME),
            new Cookie(Constants.ORIGINAL_AI_LANG_COOKIE,  MockConstants.SSO_CLAIM_PREFERRED_LANGUAGE.substring(0,1)),
            new Cookie(Constants.ORIGINAL_AI_HRID_COOKIE, MockConstants.SSO_CLAIM_PERSON_ID)

    };
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
        File base = new File(AiCookiesValveLoginAsTest.class.getResource(BASE_CONF_RESOURCE_PATH)
                .getFile()).getParentFile();
        tomcat.addWebapp(CONTEXT_PATH_LOGINAS, new File(base, CONTEXT_CONF_FOLDER_LOGINAS).toString());
        tomcat.addWebapp(CONTEXT_PATH_NOLOGINAS, new File(base, CONTEXT_CONF_FOLDER_NOLOGINAS).toString());
        tomcat.addWebapp(CONTEXT_PATH_LOGINAS_NOGROUPS, new File(base, CONTEXT_CONF_FOLDER_LOGINAS_NOGROUPS).toString());

        tomcat.start();
    }


    @AfterClass
    public static void shutdownTomcat() throws Exception {
        tomcat.stop();
        tomcat.destroy();
    }

    private void addLoginasCookie(WebDriver d, @Nullable String cookieValue){
        if(cookieValue == null || cookieValue.isEmpty()){
            cookieValue = AI_LOGIN_AS_COOKIE_VALUE;
        }
        d.manage().addCookie(new Cookie(Constants.AI_LOGIN_AS,cookieValue));
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
    public void testAiCookiesAreNotChangedWithoutLoginas(){
        addLoginasCookie(browser,null);

        browser.get(APP_SERVER_BASE_URL + CONTEXT_PATH_NOLOGINAS + CHECK_COOKIES_SERVLET_PATH);
        CookieRetriever cookieRetriever = new CookieServletCookieRetriever(browser.getPageSource());
        Utils.assertCookiesContain(expectedCookies,cookieRetriever);

        browser.get(APP_SERVER_BASE_URL + CONTEXT_PATH_NOLOGINAS + CHECK_REQUEST_SERVLET_PATH);
        cookieRetriever = new RequestServletCookieRetriever(browser.getPageSource());
        Utils.assertCookiesContain(expectedCookies,cookieRetriever);
    }

    @Test
    public void testAiCookiesAreChangedWithLoginas() {
        addLoginasCookie(browser,null);

        browser.get(APP_SERVER_BASE_URL + CONTEXT_PATH_LOGINAS + CHECK_COOKIES_SERVLET_PATH);
        CookieRetriever cookieRetriever = new CookieServletCookieRetriever(browser.getPageSource());
        Utils.assertCookiesContain(expectedLoginasCookies,cookieRetriever);

        browser.get(APP_SERVER_BASE_URL + CONTEXT_PATH_LOGINAS + CHECK_REQUEST_SERVLET_PATH);
        cookieRetriever = new RequestServletCookieRetriever(browser.getPageSource());
        Utils.assertCookiesContain(expectedLoginasCookies,cookieRetriever);
    }

    @Test
    public void testGroupNotAllowedForLoginAs(){
        addLoginasCookie(browser,null);
        browser.get(APP_SERVER_BASE_URL + CONTEXT_PATH_LOGINAS_NOGROUPS + CHECK_COOKIES_SERVLET_PATH);
        Utils.assertTitleEquals(browser, "HTTP Status 403 ? Forbidden");
    }

    @Test
    public void testInjectedAiCookiesAreDropped() {
        for (String cookieName : aiCookieNames)
            browser.manage().addCookie(CookieFactory.createCookie(cookieName));
        Cookie[] spoofedCookies = browser.manage().getCookies().toArray(new Cookie[0]);
        addLoginasCookie(browser,null);

        browser.get(APP_SERVER_BASE_URL + CONTEXT_PATH_LOGINAS + CHECK_COOKIES_SERVLET_PATH);
        Utils.assertCookiesNotContain(spoofedCookies,new CookieServletCookieRetriever(browser.getPageSource()));

        browser.get(APP_SERVER_BASE_URL + CONTEXT_PATH_LOGINAS + CHECK_REQUEST_SERVLET_PATH);
        Utils.assertCookiesNotContain(spoofedCookies,new RequestServletCookieRetriever(browser.getPageSource()));


    }

    @Test
    public void testOtherCookiesAreNotDropped() {
        for (int i=0;i<5;i++)
            browser.manage().addCookie(CookieFactory.createCookie());
        Cookie[] cookies = browser.manage().getCookies().toArray(new Cookie[0]);
        addLoginasCookie(browser,null);


        browser.get(APP_SERVER_BASE_URL + CONTEXT_PATH_LOGINAS + CHECK_COOKIES_SERVLET_PATH);
        Utils.assertCookiesContain(cookies, new CookieServletCookieRetriever(browser.getPageSource()));
        browser.get(APP_SERVER_BASE_URL + CONTEXT_PATH_LOGINAS + CHECK_REQUEST_SERVLET_PATH);
        Utils.assertCookiesContain(cookies, new RequestServletCookieRetriever(browser.getPageSource()));
    }



}
