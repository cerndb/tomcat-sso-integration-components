/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.cern.sso.tomcat.wrappers;

import java.util.*;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 *
 * @author jgraniec
 */
public class GuardedHeadersWrapper extends HttpServletRequestWrapper {

    private final ResourceBundle messages = ResourceBundle.getBundle("Messages");
    private final static Logger LOGGER = Logger.getLogger("ch.cern.sso");

    private Set<String> headersToDrop;

    public GuardedHeadersWrapper(HttpServletRequest httpServletRequest, String[] headers) {
        super(httpServletRequest);
        this.headersToDrop = new TreeSet<>(String.CASE_INSENSITIVE_ORDER); //TreeSet let's us compare string ignoring case
        this.headersToDrop.addAll(Arrays.asList(headers));
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        Set<String> originalHeadersNames = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        originalHeadersNames.addAll(Collections.list(super.getHeaderNames()));
        originalHeadersNames.removeAll(headersToDrop);
        System.out.println(originalHeadersNames);
        return Collections.enumeration(originalHeadersNames);
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        if (headersToDrop.contains(name))
            return null;
        return super.getHeaders(name);
    }

    @Override
    public String getHeader(String name) {
        if (headersToDrop.contains(name))
            return null;
        return super.getHeader(name);
    }

}
