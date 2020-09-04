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

import ch.cern.sso.cross.context.test.suite.utils.InitTestEnvironment;
import ch.cern.sso.cross.context.test.suite.utils.Utils;
import org.apache.catalina.startup.Tomcat;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;

import java.util.HashMap;

/**
 *
 * @author lurodrig
 */
public class SsoAisFilterTest {

    private static Tomcat tomcat = null;
    private static WebDriver browser;
    static String[] cookies = {"AI_USERNAME", "AI_USER", "AI_IDENTITY_CLASS", "AI_LANG", "AI_SESSION", "AI_HRID"};
    private String username;
    private String password;
    private String url;

    @BeforeClass
    public static void initTomcat() throws Exception {
        HashMap<String, String> contextPathBaseDir = new HashMap<String, String>() {
            {
                put("/web-module-1", "web-module-1");
            }
        };
        tomcat = InitTestEnvironment.initTomcat("/keycloak-saml/testsaml-with-mappers.json", contextPathBaseDir);
    }

    @AfterClass
    public static void shutdownTomcat() throws Exception {
        tomcat.stop();
        tomcat.destroy();
    }

    @Before
    public void initBrowser() {
        browser = InitTestEnvironment.initBrowser();
    }

    public void initTestParameters(String url, String username, String password) {
        browser = InitTestEnvironment.initBrowser();
        this.url = url;
        this.username = username;
        this.password = password;
    }

    //@Test
//    public void testAisCookiesAreCreated() {
//        initTestParameters("http://localhost:8082/web-module-1/cookie-info", "lurodrig", "password");
//        browser.get(url);
//        Utils.assertAtLoginPagePostBinding(browser);
//        Utils.login(browser, username, password);
//        Utils.assertAtModuleContextStartsWith(browser, "/web-module-1");
//        Utils.assertCookiesAreCreated(browser, cookies);
//        browser.close();
//    }

    //@Test
    public void testAuthorizedUserCanAccess() {
        initTestParameters("http://localhost:8082/web-module-1/principal-info", "lurodrig", "password");
        String stringToDisplay = "Name: " + username;
        Utils.testStringIsDisplayed(browser, url, username, password, stringToDisplay);
    }

    //@Test
    public void testNonAuthorizedUserCanNotAccess() {
        initTestParameters("http://localhost:8082/web-module-1/principal-info", "bburke", "password");
        String stringToDisplay = "HTTP Status 403 - User " + username + " is NOT member of any of the ALLOWED groups";
        Utils.testStringIsDisplayed(browser, url, username, password, stringToDisplay);
    }

    //@Test
    public void testForbiddenUserCanNotAccess() {
        initTestParameters("http://localhost:8082/web-module-1/principal-info", "bgates", "password");
        String stringToDisplay = "HTTP Status 403 - User " + username + " is member of these FORBIDDEN groups:";
        Utils.testStringIsDisplayed(browser, url, username, password, stringToDisplay);
    }

    //@Test
    public void testLoginAsCookiesAreCreated() {
        initTestParameters("http://localhost:8082/web-module-1/cookie-info", "lurodrig", "password");
        // Set the domain and context before adding the cookie.
        browser.get("http://localhost:8082/web-module-1");
        Cookie cookie = new Cookie("AI_LOGIN_AS", "awiecek:493034:e");
        browser.manage().addCookie(cookie);
        // Now the cookie will be sent. See https://stackoverflow.com/questions/36305660/selenium-js-add-cookie-to-request/
        browser.get(url);
        Utils.assertAtLoginPagePostBinding(browser);
        Utils.login(browser, username, password);
        Utils.assertAtModuleContextStartsWith(browser, "/web-module-1");
        //String[] loginAsCookies = {Constants.ORIGINAL_AI_USERNAME_COOKIE, Constants.ORIGINAL_AI_USER_COOKIE, Constants.ORIGINAL_AI_HRID_COOKIE, Constants.ORIGINAL_AI_LANG_COOKIE};
        //Utils.assertCookiesAreCreated(browser, ArrayManipulator.concatenate(cookies, loginAsCookies));
        browser.close();
    }

    //@Test
    public void testuserNotMemberOfLoginAsGroupsIsNotAuthorized() {
        initTestParameters("http://localhost:8082/web-module-1/cookie-info", "evilloginas", "password");
        // Set the domain and context before adding the cookie.
        browser.get("http://localhost:8082/web-module-1");
        Cookie cookie = new Cookie("AI_LOGIN_AS", "awiecek:493034:e");
        browser.manage().addCookie(cookie);
        String stringToDisplay = "HTTP Status 403 - User " + username + " is trying to inject the LOGIN AS COOKIE. This incident will be reported";
        Utils.testStringIsDisplayed(browser, url, username, password, stringToDisplay);
    }
}
