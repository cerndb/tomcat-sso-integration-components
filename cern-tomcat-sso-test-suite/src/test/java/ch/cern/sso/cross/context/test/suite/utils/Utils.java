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
package ch.cern.sso.cross.context.test.suite.utils;

import ch.cern.sso.cross.context.test.suite.utils.Cookies.CookieParsingException;
import ch.cern.sso.cross.context.test.suite.utils.Cookies.CookieRetriever;
import org.junit.Assert;
import org.keycloak.testsuite.pages.LoginPage;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

import java.util.Map;

/**
 *
 * @author lurodrig
 */
public class Utils {

    public static void assertAtLoginPagePostBinding(WebDriver browser) {
        Assert.assertTrue(browser.getCurrentUrl().startsWith(InitTestEnvironment.AUTH_SERVER_URL + "/realms/demo/login-actions/authenticate"));
    }

    public static void assertAtModuleContextStartsWith(WebDriver browser, String context) {
        Assert.assertTrue(browser.getCurrentUrl().startsWith(InitTestEnvironment.APP_SERVER_BASE_URL + context));
    }

    public static void login(WebDriver browser, String username, String password) {
        LoginPage loginPage = PageFactory.initElements(browser, LoginPage.class);
        loginPage.login(username, password);
    }

    public static void assertCookiesContain(Cookie[] expectedCookies, CookieRetriever cookieRetriever){
        assertCookiesValue(expectedCookies,cookieRetriever,true);
    }
    public static void assertCookiesNotContain(Cookie[] expectedCookies, CookieRetriever cookieRetriever){
        assertCookiesValue(expectedCookies,cookieRetriever,false);
    }
    private static void assertCookiesValue(Cookie[] expectedCookies, CookieRetriever cookieRetriever, boolean contain){
        Map<String,String> displayedCookies = null;
        try {
            displayedCookies = cookieRetriever.getCookies();
        } catch (CookieParsingException e) {
            e.printStackTrace();
        }

        for(Cookie expectedCookie : expectedCookies){
            String cookieName = expectedCookie.getName();
            String cookieValue = expectedCookie.getValue();
            Assert.assertEquals("cookie: " + cookieName + " expected value: " + cookieValue + " actual value: " + displayedCookies.get(cookieName), contain, cookieValue.equalsIgnoreCase(displayedCookies.get(cookieName)));
        }
    }



    public static void assertStringIsDisplayed(WebDriver browser, String stringToDisplay) {
        String pageSource = browser.getPageSource();
        Assert.assertTrue(stringToDisplay + " is not displayed", pageSource.contains(stringToDisplay));
    }

    public static void assertStringIsNOTdisplayed(WebDriver browser, String stringToDisplay) {
        String pageSource = browser.getPageSource();
        Assert.assertFalse(stringToDisplay + " is displayed", pageSource.contains(stringToDisplay));
    }

    public static void assertTitleEquals(WebDriver browser, String expectedTitle) {
        String title = browser.getTitle();
        Assert.assertEquals(expectedTitle + " not equals to " + title, title, expectedTitle);
    }

    public static void testStringIsDisplayed(WebDriver browser, String url, String username, String password, String stringToDisplay) {
        browser.get(url);
        Utils.assertAtLoginPagePostBinding(browser);
        Utils.login(browser, username, password);
        Utils.assertStringIsDisplayed(browser, stringToDisplay);
        browser.close();
    }
}
