/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.cern.sso.cross.context.test.suite;

import com.gargoylesoftware.htmlunit.WebClient;

/**
 *
 * @author lurodrig
 */
public class HtmlUnitTestDriver extends org.openqa.selenium.htmlunit.HtmlUnitDriver {
        
        @Override
        public WebClient getWebClient() {
            return super.getWebClient();
        }
}
