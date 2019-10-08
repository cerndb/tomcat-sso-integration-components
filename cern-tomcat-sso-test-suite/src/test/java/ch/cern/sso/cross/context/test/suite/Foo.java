/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.cern.sso.cross.context.test.suite;

/**
 *
 * @author lurodrig
 */
public class Foo {
    
    
    public static void main(String[] args) {
        String pageSource ="AI_USERNAME LURODRIG\n"
                + "AI_USER LURODRIG\n"
                + "AI_IDENTITY_CLASS CERN REGISTERED\n"
                + "AI_LANG F\n"
                + "AI_SESSION 457500EEA84E38A17B0F6D8B8A8D203EC68DCCC4E92DE585060B09368EA4A8C5C3220AD2A9062FD29945D0AE40681895E7A7AD716DB17EB2D69990F0175168682E9D63ECD6094A9247D66D77230AFA9A330BA1A583D394D62D9A4A6CB18F29EA32A3DB76\n"
                + "AI_HRID 720335\n"
                + "JSESSIONID 235E3673D35FC8C402A6FB1EBEB1B4B6GANDALF 91664e4e-96e2-4a80-a4fd-251610b211b1\n";
        pageSource.lastIndexOf("AI_USER");
    }
}
