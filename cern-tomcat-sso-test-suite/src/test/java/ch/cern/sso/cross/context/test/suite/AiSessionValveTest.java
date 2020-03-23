/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.cern.sso.cross.context.test.suite;

import ch.cern.sso.cross.context.test.suite.utils.Cookies.*;
import ch.cern.sso.cross.context.test.suite.utils.HtmlUnitTestDriver;
import ch.cern.sso.cross.context.test.suite.utils.Utils;
import ch.cern.sso.tomcat.common.aisession.Credentials;
import ch.cern.sso.tomcat.common.utils.Constants;
import ch.cern.sso.tomcat.common.utils.SsoClaims;
import ch.cern.sso.tomcat.valves.mocks.MockConstants;
import org.apache.catalina.startup.Tomcat;
import org.junit.*;
import org.keycloak.adapters.saml.SamlPrincipal;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;

import javax.xml.datatype.DatatypeConfigurationException;
import java.io.File;
import java.rmi.RemoteException;

import static junit.framework.TestCase.fail;

/**
 *
 * @author lurodrig
 */
public class AiSessionValveTest {

    private static final String APP_SERVER_BASE_URL = "http://localhost:8082";
    private static final String CONTEXT_PATH = "/aisession-nologinas";
    private static final String CHECK_COOKIES_SERVLET_PATH = "/cookie-info";
    private static final String CHECK_REQUEST_SERVLET_PATH = "/request-info";

    private static final String AI_SESSION_COOKIE_NAME = Constants.AI_SESSION;
    private static final String BASE_CONF_RESOURCE_PATH = "/ai-session/aisession-nologinas";
    private static final String CONTEXT_CONF_FOLDER = "aisession-nologinas";
    private static final String NOT_FOUND_RESOURCE_FOR_COOKIE_INJECTION_TRICK = "/404";


    private static Tomcat tomcat = null;
    private static WebDriver browser;


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
        browser.get(APP_SERVER_BASE_URL + CONTEXT_PATH + NOT_FOUND_RESOURCE_FOR_COOKIE_INJECTION_TRICK);
    }
    @After
    public void closeBrowser(){
        browser.close();
    }

    @Test
    public void testAiSessionIsAdded() {
        browser.get(APP_SERVER_BASE_URL + CONTEXT_PATH + CHECK_COOKIES_SERVLET_PATH);
        Utils.assertStringIsDisplayed(browser, AI_SESSION_COOKIE_NAME);
        browser.get(APP_SERVER_BASE_URL + CONTEXT_PATH + CHECK_REQUEST_SERVLET_PATH);
        Utils.assertStringIsDisplayed(browser, AI_SESSION_COOKIE_NAME);
    }

    @Test
    public void testAiSessionContent(){
        browser.get(APP_SERVER_BASE_URL + CONTEXT_PATH + CHECK_COOKIES_SERVLET_PATH);
        try {
            String aiSessionCookie = new CookieServletCookieRetriever(browser.getPageSource()).getCookies().get(AI_SESSION_COOKIE_NAME);
            Credentials credentials = new Credentials(aiSessionCookie);
            SamlPrincipal principal = MockConstants.createSamlPrincipal();
            String language = principal.getAttribute(SsoClaims.SSO_CLAIM_PREFERRED_LANGUAGE).substring(0,1);
            String hrId = principal.getAttribute(SsoClaims.SSO_CLAIM_PERSON_ID);
            Assert.assertEquals("PreferredLanguage ", credentials.getPreferedLanguage(), language);
            Assert.assertEquals("HrId ", Integer.toString(credentials.getHrId()), hrId);
            Assert.assertEquals("LoginName ", credentials.getLoginName(), MockConstants.PRINCIPAL_NAME);

        } catch (InstantiationException | CookieParsingException | RemoteException | DatatypeConfigurationException e) {
            fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void testInjectedAiSessionIsDropped() {
        //try spoofing ai_session cookie
        browser.manage().addCookie(CookieFactory.createCookie(AI_SESSION_COOKIE_NAME));
        // Add other random cookies
        for (int i = 0; i < 10; i++)
            browser.manage().addCookie(CookieFactory.createCookie());

        Cookie aiSpoofedCookies = browser.manage().getCookieNamed(AI_SESSION_COOKIE_NAME);
        browser.get(APP_SERVER_BASE_URL + CONTEXT_PATH + CHECK_COOKIES_SERVLET_PATH);
        CookieRetriever cookieRetriever = new CookieServletCookieRetriever(browser.getPageSource());
        Utils.assertCookiesNotContain(new Cookie[]{aiSpoofedCookies},cookieRetriever);

        browser.get(APP_SERVER_BASE_URL + CONTEXT_PATH + CHECK_REQUEST_SERVLET_PATH);
        cookieRetriever = new RequestServletCookieRetriever(browser.getPageSource());
        Utils.assertCookiesNotContain(new Cookie[]{aiSpoofedCookies},cookieRetriever);

    }

    @Test
    public void testOtherCookiesAreNotDropped(){
        //add random cookies
        for (int i = 0; i < 10; i++)
            browser.manage().addCookie(CookieFactory.createCookie());
        Cookie[] randomCookies=browser.manage().getCookies().toArray(new Cookie[0]);
        browser.manage().addCookie(CookieFactory.createCookie(AI_SESSION_COOKIE_NAME));

        browser.get(APP_SERVER_BASE_URL + CONTEXT_PATH + CHECK_COOKIES_SERVLET_PATH);
        Utils.assertCookiesContain(randomCookies, new CookieServletCookieRetriever(browser.getPageSource()));

        browser.get(APP_SERVER_BASE_URL + CONTEXT_PATH + CHECK_REQUEST_SERVLET_PATH);
        Utils.assertCookiesContain(randomCookies, new RequestServletCookieRetriever(browser.getPageSource()));
    }

}
