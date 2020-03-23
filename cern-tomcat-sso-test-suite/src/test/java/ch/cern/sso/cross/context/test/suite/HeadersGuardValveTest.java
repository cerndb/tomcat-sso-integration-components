/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.cern.sso.cross.context.test.suite;

import ch.cern.sso.cross.context.test.suite.utils.HtmlUnitTestDriver;
import ch.cern.sso.cross.context.test.suite.utils.Utils;
import org.apache.catalina.startup.Tomcat;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author jgraniec
 */
public class HeadersGuardValveTest {

    protected String APP_SERVER_BASE_URL = "http://localhost:8082";

    private static Tomcat tomcat = null;
    private static final String SPOOFED_HEADER_VALUE = "spoof";

    @BeforeClass
    public static void initTomcat() throws Exception {
        tomcat = new Tomcat();
        tomcat.setPort(8082);
        File base = new File(HeadersGuardValveTest.class.getResource("/header-guard/no-guarded-headers-module")
                .getFile()).getParentFile();
        tomcat.addWebapp("/no-guarded-headers-module",
                new File(base, "no-guarded-headers-module")
                        .toString());//    <Parameter name="guarded.headers" value=""/>
        tomcat.addWebapp("/single-guarder-header-module",
                new File(base, "single-guarder-header-module")
                        .toString());//    <Parameter name="guarded.headers" value="SSO_REMOTE_USER"/>
        tomcat.addWebapp("/multiple-guarded-headers-module",
                new File(base, "/multiple-guarded-headers-module")
                        .toString());//    <Parameter name="guarded.headers" value="SSO_REMOTE_USER,REMOTE_USER,REMOTE_HOST"/>

        tomcat.start();
    }

    @AfterClass
    public static void shutdownTomcat() throws Exception {
        tomcat.stop();
        tomcat.destroy();
    }

    private HtmlUnitTestDriver getBrowser() {
        HtmlUnitTestDriver d = new HtmlUnitTestDriver();
        d.getWebClient().getOptions().setJavaScriptEnabled(true);
        d.getWebClient().getOptions().setCssEnabled(false);
        d.getWebClient().getOptions().setTimeout(1000000);
        return d;
    }
    private void addHeaders(HtmlUnitTestDriver d, Map<String,String> headers){
        for(Map.Entry<String,String> header : headers.entrySet())
            d.getWebClient().addRequestHeader(header.getKey(), header.getValue());
    }
    private Map<String,String> getHeadersToSpoof(String[] headerNames, String[] headerValues){
        Map<String,String> headersToSpoof = new HashMap<>();
        if(headerNames == null)
            return headersToSpoof;
        if(headerValues == null)
            headerValues = new String[]{};
        for(int i=0; i<headerNames.length; i++)
            headersToSpoof.put(headerNames[i], i < headerValues.length ? headerValues[i] : SPOOFED_HEADER_VALUE);

        return headersToSpoof;
    }
    private Map<String,String> getHeadersToSpoof(String[] headerNames){
        return getHeadersToSpoof(headerNames,new String[]{});
    }

    @Test
    public void testGuardSingleHeaderMultipleRequested() {
        String[] spoofHeaderNames = {"SSO_REMOTE_USER", "sso_remote_user", "SSO_remote_User"};
        Map<String,String> headersToSpoof = getHeadersToSpoof(spoofHeaderNames);
        HtmlUnitTestDriver browser = getBrowser();
        addHeaders(browser, headersToSpoof);
        browser.get("http://localhost:8082/single-guarder-header-module/request-info");

        Utils.assertStringIsNOTdisplayed(browser, SPOOFED_HEADER_VALUE);

        spoofHeaderNames = new String[]{"remote_user", "SSO_remote_User",
                "REMOTE_USER", "REMOTE_HOST", "REMOTE_USeR"};
        headersToSpoof = getHeadersToSpoof(spoofHeaderNames);
        addHeaders(browser, headersToSpoof);
        browser.get("http://localhost:8082/single-guarder-header-module/request-info");

        Utils.assertStringIsDisplayed(browser, SPOOFED_HEADER_VALUE);

        Utils.assertStringIsNOTdisplayed(browser, "sso_remote_user:"+SPOOFED_HEADER_VALUE);

        browser.close();

    }
    @Test
    public void testGuardSingleHeaderSingleRequested() {
        String[] spoofHeaderNames = {"SSO_remote_User"};
        Map<String,String> headersToSpoof = getHeadersToSpoof(spoofHeaderNames);
        HtmlUnitTestDriver browser = getBrowser();
        addHeaders(browser, headersToSpoof);
        browser.get("http://localhost:8082/single-guarder-header-module/request-info");

        Utils.assertStringIsNOTdisplayed(browser, SPOOFED_HEADER_VALUE);
        browser.close();
    }
    @Test
    public void testGuardSingleHeaderNoRequested() {
        String[] spoofHeaderNames = {};
        Map<String,String> headersToSpoof = getHeadersToSpoof(spoofHeaderNames);
        HtmlUnitTestDriver browser = getBrowser();
        addHeaders(browser, headersToSpoof);
        browser.get("http://localhost:8082/single-guarder-header-module/request-info");

        Utils.assertStringIsNOTdisplayed(browser, SPOOFED_HEADER_VALUE);
        browser.close();
    }
    @Test
    public void testGuardMultipleHeaderMultipleRequested() {
        String[] spoofHeaderNames = {"SSO_REMOTE_USER", "sso_remote_user", "SSO_remote_User",
                "REMOTE_USER","REMOTE_HOST","REMOTE_USeR"};
        Map<String,String> headersToSpoof = getHeadersToSpoof(spoofHeaderNames);
        HtmlUnitTestDriver browser = getBrowser();
        addHeaders(browser, headersToSpoof);
        browser.get("http://localhost:8082/multiple-guarder-headers-module/request-info");

        Utils.assertStringIsNOTdisplayed(browser, SPOOFED_HEADER_VALUE);
        browser.close();
    }
    @Test
    public void testGuardMultipleHeaderSingleRequested() {
        String[] spoofHeaderNames = {"REMOTE_USeR"};
        Map<String,String> headersToSpoof = getHeadersToSpoof(spoofHeaderNames);
        HtmlUnitTestDriver browser = getBrowser();
        addHeaders(browser, headersToSpoof);
        browser.get("http://localhost:8082/multiple-guarder-headers-module/request-info");

        Utils.assertStringIsNOTdisplayed(browser, SPOOFED_HEADER_VALUE);
        browser.close();
    }
    @Test
    public void testGuardMultipleHeaderNoRequested() {
        String[] spoofHeaderNames = {};
        Map<String,String> headersToSpoof = getHeadersToSpoof(spoofHeaderNames);
        HtmlUnitTestDriver browser = getBrowser();
        addHeaders(browser, headersToSpoof);
        browser.get("http://localhost:8082/multiple-guarder-headers-module/request-info");

        Utils.assertStringIsNOTdisplayed(browser, SPOOFED_HEADER_VALUE);
        browser.close();
    }
    @Test
    public void testGuardNoHeaderMultipleRequested() {
        String[] spoofHeaderNames = {"SSO_REMOTE_USER", "sso_remote_user", "SSO_remote_User",
            "REMOTE_USER","REMOTE_HOST","REMOTE_USeR"};
        Map<String,String> headersToSpoof = getHeadersToSpoof(spoofHeaderNames);
        HtmlUnitTestDriver browser = getBrowser();
        addHeaders(browser, headersToSpoof);
        browser.get("http://localhost:8082/no-guarded-headers-module/request-info");
        Utils.assertStringIsDisplayed(browser, SPOOFED_HEADER_VALUE);
        browser.close();
    }
    @Test
    public void testGuardNoHeaderSingleRequested() {
        String[] spoofHeaderNames = {"REMOTE_USeR"};
        Map<String,String> headersToSpoof = getHeadersToSpoof(spoofHeaderNames);
        HtmlUnitTestDriver browser = getBrowser();
        addHeaders(browser, headersToSpoof);
        browser.get("http://localhost:8082/no-guarded-headers-module/request-info");

        Utils.assertStringIsDisplayed(browser, SPOOFED_HEADER_VALUE);
        browser.close();
    }
    @Test
    public void testGuardNoHeaderNoRequested() {
        String[] spoofHeaderNames = {};
        Map<String,String> headersToSpoof = getHeadersToSpoof(spoofHeaderNames);
        HtmlUnitTestDriver browser = getBrowser();
        addHeaders(browser, headersToSpoof);
        browser.get("http://localhost:8082/no-guarded-headers-module/request-info");

        Utils.assertStringIsNOTdisplayed(browser, SPOOFED_HEADER_VALUE);
        browser.close();
    }
}
