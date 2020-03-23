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
import com.sun.istack.Nullable;
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
 * @author jgraniec
 */
public class AiSessionValveLoginAsTest {

    private static final String APP_SERVER_BASE_URL = "http://localhost:8082";
    private static final String CONTEXT_PATH_LOGINAS = "/aisession-loginas";
    private static final String CONTEXT_PATH_NOLOGINAS = "/aisession-nologinas";
    private static final String CONTEXT_PATH_LOGINAS_NOGROUPS = "/aisession-loginas-nogroups";

    private static final String CHECK_COOKIES_SERVLET_PATH = "/cookie-info";
    private static final String CHECK_REQUEST_SERVLET_PATH = "/request-info";

    private static final String AI_SESSION_COOKIE_NAME = Constants.AI_SESSION;
    private static final String BASE_CONF_RESOURCE_PATH = "/ai-session/aisession-loginas";
    private static final String CONTEXT_CONF_FOLDER_LOGINAS = "aisession-loginas";
    private static final String CONTEXT_CONF_FOLDER_NOLOGINAS = "aisession-nologinas";
    private static final String CONTEXT_CONF_FOLDER_LOGINAS_NOGROUPS = "aisession-loginas-nogroups";
    private static final String NOT_FOUND_RESOURCE_FOR_COOKIE_INJECTION_TRICK = "/404";

    private static Tomcat tomcat = null;
    private static WebDriver browser;

    private static final String AI_LOGIN = "jgraniec";
    private static final String AI_HRID = "820233";
    private static final String AI_LANG = "E";
    private static final String AI_LOGIN_AS_COOKIE_VALUE = AI_LOGIN + ":" + AI_HRID + ":" + AI_LANG;

    @BeforeClass
    public static void initTomcat() throws Exception {
        tomcat = new Tomcat();
        tomcat.setPort(8082);
        File base = new File(AiSessionValveTest.class.getResource(BASE_CONF_RESOURCE_PATH)
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

    private void addLoginasCookie(WebDriver d, @Nullable String cookieValue) {
        if (cookieValue == null || cookieValue.isEmpty()) {
            cookieValue = AI_LOGIN_AS_COOKIE_VALUE;
        }
        d.manage().addCookie(new Cookie(Constants.AI_LOGIN_AS, cookieValue));
    }

    @Before
    public void initBrowser() {
        HtmlUnitTestDriver d = new HtmlUnitTestDriver();
        d.getWebClient().getOptions().setJavaScriptEnabled(true);
        d.getWebClient().getOptions().setCssEnabled(false);
        d.getWebClient().getOptions().setTimeout(1000000);
        browser = d;
        browser.get(APP_SERVER_BASE_URL + "/" + NOT_FOUND_RESOURCE_FOR_COOKIE_INJECTION_TRICK);
    }

    @After
    public void closeBrowser() {
        browser.close();
    }

    @Test
    public void testAiCookiesAreNotChangedWithoutLoginas() {
        addLoginasCookie(browser,null);
        browser.get(APP_SERVER_BASE_URL + CONTEXT_PATH_NOLOGINAS + CHECK_COOKIES_SERVLET_PATH);
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
    public void testAiCookiesAreChangedWithLoginas() {
        addLoginasCookie(browser,null);
        browser.get(APP_SERVER_BASE_URL + CONTEXT_PATH_LOGINAS + CHECK_COOKIES_SERVLET_PATH);
        try {
            String aiSessionCookie = new CookieServletCookieRetriever(browser.getPageSource()).getCookies().get(AI_SESSION_COOKIE_NAME);
            Credentials credentials = new Credentials(aiSessionCookie);
            SamlPrincipal principal = MockConstants.createSamlPrincipal();
            String originalPersonId = principal.getAttribute(SsoClaims.SSO_CLAIM_PERSON_ID);
            Assert.assertEquals("PreferredLanguage ", credentials.getPreferedLanguage(), AI_LANG);
            Assert.assertEquals("HrId ", Integer.toString(credentials.getHrId()), AI_HRID);
            Assert.assertEquals("LoginName ", credentials.getLoginName(), AI_LOGIN);
            Assert.assertEquals("OriginalPersonId ", Integer.toString(credentials.getOriginalPersonId()), originalPersonId);


        } catch (InstantiationException | CookieParsingException | RemoteException | DatatypeConfigurationException e) {
            fail(e.getMessage());
            e.printStackTrace();
        }


    }
    @Test
    public void testGroupNotAllowedForLoginAs(){
        addLoginasCookie(browser,null);
        browser.get(APP_SERVER_BASE_URL + CONTEXT_PATH_LOGINAS_NOGROUPS + CHECK_COOKIES_SERVLET_PATH);
        Utils.assertTitleEquals(browser, "HTTP Status 403 ? Forbidden");
    }

    @Test
    public void testOtherCookiesAreNotDropped() {
        //add random cookies
        for (int i = 0; i < 10; i++)
            browser.manage().addCookie(CookieFactory.createCookie());
        addLoginasCookie(browser,null);
        Cookie[] cookies=browser.manage().getCookies().toArray(new Cookie[0]);
        browser.manage().addCookie(CookieFactory.createCookie(AI_SESSION_COOKIE_NAME));

        browser.get(APP_SERVER_BASE_URL + CONTEXT_PATH_LOGINAS + CHECK_COOKIES_SERVLET_PATH);
        Utils.assertCookiesContain(cookies, new CookieServletCookieRetriever(browser.getPageSource()));

        browser.get(APP_SERVER_BASE_URL + CONTEXT_PATH_LOGINAS + CHECK_REQUEST_SERVLET_PATH);
        Utils.assertCookiesContain(cookies, new RequestServletCookieRetriever(browser.getPageSource()));
    }

}

