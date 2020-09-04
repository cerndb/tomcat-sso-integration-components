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
package ch.cern.sso.tomcat.wrappers;


import ch.cern.sso.tomcat.common.utils.ArrayManipulator;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.*;
import java.util.logging.Logger;

/**
 *
 * @author jgraniec
 */
public class CookieRequestWrapper extends HttpServletRequestWrapper {

    private final ResourceBundle messages = ResourceBundle.getBundle("Messages");
    private final static Logger LOGGER = Logger.getLogger("ch.cern.sso");
    private final String COOKIE_SEPARATING_OPERATOR = "; ";
    private final String COOKIE_ASSIGNMENT_OPERATOR = "=";
    private final String COOKIE_HEADER_NAME = "cookie";

    private Set<Cookie> cookiesToInject;
    private Map<String,String> cookiesToInjectMap;
    private Set<String> cookieNamesToDrop;

    public CookieRequestWrapper(HttpServletRequest httpServletRequest, Set<Cookie> cookiesToInject, Set<String> cookiesToDrop) {
        super(httpServletRequest);
        if(cookiesToDrop == null)
            cookiesToDrop = new HashSet<>();
        if(cookiesToInject == null)
            cookiesToInject = new HashSet<>();

        this.cookiesToInject = cookiesToInject;
        this.cookieNamesToDrop = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        this.cookieNamesToDrop.addAll(cookiesToDrop);
        this.cookiesToInjectMap = getCookiesMap(cookiesToInject);

    }

    public CookieRequestWrapper(HttpServletRequest httpServletRequest, Set<Cookie> cookiesToInject) {
        this(httpServletRequest, cookiesToInject,null);
    }

    @Override
    public Cookie[] getCookies() {
        Cookie[] originalCookies = super.getCookies();
        if(originalCookies == null)
            return cookiesToInject.toArray(new Cookie[0]);

        List<Cookie> processedCookies = new ArrayList<>();
        for(Cookie cookie : originalCookies)
            if(isCookieToStay(cookie.getName()))
                processedCookies.add(cookie);
        processedCookies.addAll(cookiesToInject);
        return processedCookies.toArray(new Cookie[0]);
    }


    @Override
    public Enumeration<String> getHeaderNames() {
        Enumeration<String> originalHeaderNames = super.getHeaderNames();
        Set<String> headerNames = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        headerNames.addAll(Collections.list(originalHeaderNames));
        headerNames.add(COOKIE_HEADER_NAME);
        return Collections.enumeration(headerNames);
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        Enumeration<String> originalHeaders = super.getHeaders(name);
        if(!isCookieHeader(name))
            return originalHeaders;
        //In case of Cookie header there's always only 1 header - RFC6265
        return Collections.enumeration(Collections.singletonList(this.getHeader(name)));
    }


    @Override
    public String getHeader(String name) {
        String originalHeader = super.getHeader(name);
        if(!isCookieHeader(name))
            return originalHeader;
        if(originalHeader == null)
            originalHeader = "";

        String[] cookieHeaders = originalHeader.split(COOKIE_SEPARATING_OPERATOR);
        List<String> cookies = getTrimmedCookiesHeader(cookieHeaders);
        addInjectedCookies(cookies);
        return ArrayManipulator.join(COOKIE_SEPARATING_OPERATOR, cookies.toArray(new String[0]));
    }

    private List<String> getTrimmedCookiesHeader(Enumeration<String> originalCookiesHeader){
        List<String> cookies = new ArrayList<>();
        while(originalCookiesHeader.hasMoreElements()){
            String cookie = originalCookiesHeader.nextElement();
            String cookieName = cookie.split(COOKIE_ASSIGNMENT_OPERATOR)[0];
            if(isCookieToStay(cookieName))
                cookies.add(cookie);
        }
        return cookies;
    }


    private void addInjectedCookies(List<String> cookies) {
        for(Map.Entry<String,String> cookieToInject : this.cookiesToInjectMap.entrySet())
            cookies.add(cookieToInject.getKey()+COOKIE_ASSIGNMENT_OPERATOR+cookieToInject.getValue());
    }
    private boolean isCookieToStay(String cookie) {
        return !isCookieToBeDropped(cookie) && !isCookieToBeReplaced(cookie);
    }

    private boolean isCookieToBeReplaced(String cookie) {
        return cookiesToInjectMap.containsKey(cookie.trim());
    }

    private boolean isCookieToBeDropped(String cookie) {
        return cookieNamesToDrop.contains(cookie.trim());
    }
    private boolean isCookieHeader(String headerName){
       return COOKIE_HEADER_NAME.equalsIgnoreCase(headerName.trim());
    }

    private List<String> getTrimmedCookiesHeader(String[] originalCookiesHeader){
        List<String> originalCookiesHeaderList = Arrays.asList(originalCookiesHeader);
        return getTrimmedCookiesHeader(Collections.enumeration(originalCookiesHeaderList));
    }
    private Map<String,String> getCookiesMap(Set<Cookie> cookiesToInject) {
        Map<String,String> cookieNamesToInject = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        for(Cookie c : cookiesToInject)
            cookieNamesToInject.put(c.getName(),c.getValue());
        return cookieNamesToInject;
    }
}
