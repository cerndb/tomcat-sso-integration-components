/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.cern.sso.cross.context.test.suite;

import static ch.cern.sso.cross.context.test.suite.SsoAisFilterTest.browser;
import org.junit.Assert;
import org.keycloak.testsuite.pages.LoginPage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

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

    public static void assertCookiesAreCreated(WebDriver browser, String[] cookies) {
        String pageSource = browser.getPageSource();
        String[] cookieValuePair = pageSource.split("\n");
        for (String cookie : cookies) {
            // Check that the name of the cookie is display
            // Optional<String> cookieNameAndValue = Arrays.stream(cookieValuePair).filter(cvp -> cvp.contains(cookie)).findFirst();
            // TODO: avoid java 8 or upper features, sigh...
            String cookieNameAndValue = new String();
            Assert.assertTrue("Cookie " + cookie + " has not been created.", cookieNameAndValue.isEmpty());
            // Check the value is not empty
            String value = cookieNameAndValue.split("-->")[1];
            Assert.assertTrue("Cookie " + cookie + " value is empty", !value.isEmpty());
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
        Assert.assertTrue(expectedTitle + " not equals to " + title, title.equals(expectedTitle));
    }

    public static void testStringIsDisplayed(String url, String username, String password, String stringToDisplay) {
        browser.get(url);
        Utils.assertAtLoginPagePostBinding(browser);
        Utils.login(browser, username, password);
        Utils.assertStringIsDisplayed(browser, stringToDisplay);
        browser.close();
    }
}
