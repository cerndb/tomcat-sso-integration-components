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
package ch.cern.sso.tomcat.common.utils;

/**
 *
 * @author lurodrig
 */
public final class Constants {

    public static final String AICOOKIES = "aicookies";
    public static final String AI_USERNAME = "AI_USERNAME";
    public static final String AI_USER = "AI_USER";
    public static final String AI_SESSION = "AI_SESSION";
    public static final String AI_IDENTITY_CLASS = "AI_IDENTITY_CLASS";
    public static final String AI_LANG = "AI_LANG";
    public static final String AI_HRID = "AI_HRID";
    public static final String AI_LOGIN_AS = "AI_LOGIN_AS";
    public static final String DEFAULT_LANGUAGE = "E";
    public static final int CERN_ID = 00000;
    public static final short DEFAULT_X_RES = 800;
    public static final short DEFAULT_Y_RES = 600;
    public static final long AI_SESSION_VALIDITY = 3600000;
    public static final String PRINCIPAL_ROLES = "Roles";
    public static final String COOKIES_UPPER_CASE = "cookies.uppercase";
    public static final String STATUS_LOGIN_AS = "status.loginas";
    public static final String GROUPS_LOGIN_AS = "groups.loginas";
    public static final String COOKIE_LOGIN_AS = "cookie.loginas";
    public static final String GROUPS_FORBIDDEN = "groups.forbidden";
    public static final String GROUPS_ALLOWED = "groups.allowed";
    public static final String ORIGINAL_AI_USERNAME_COOKIE = "ORIGINAL_AI_USERNAME_COOKIE";
    public static final String ORIGINAL_AI_USER_COOKIE = "ORIGINAL_AI_USER_COOKIE";
    public static final String ORIGINAL_AI_HRID_COOKIE = "ORIGINAL_AI_HRID_COOKIE";
    public static final String ORIGINAL_AI_LANG_COOKIE = "ORIGINAL_AI_LANG_COOKIE";
    public static final String SECRET_COOKIE = "SECRET_COOKIE";
    public static final String[] GROUPS_FORBIDDEN_NONE = {"NONE"};
    public static final String[] GROUPS_ALLOWED_ALL = {"ALL"};
    public static final String MESSAGE_MISSED_IN_BUNDLE = "Message missed in bundle";
    public static final String URI_ENCODE = "uri.encode";
    public static final String GROUPS_EQUALS_STRICT = "groups.equals.strict";
    public static final String ROLES = "Roles";
    public static final String SSO_REMOTE_HEADERS = "sso.remote.headers";
    public static final String SSO_REMOTE_USER = "SSO_REMOTE_USER";
    public static final String SSO_REMOTE_HOST = "SSO_REMOTE_HOST";
    public static final String REMOTE_USER = "REMOTE_USER";
    public static final String MOCK_USERNAME = "mock.username";
    public static final String MOCK_ROLES = "mock.roles";
    public static final String GUARDED_HEADERS = "guarded.headers";
    public static final String ORDS_ROLE_PREFIXES = "ords.role.prefixes";
    public static final String IS_ENABLED_OVERRIDE_JNDI_ROLES = "is.enabled.override.jndi.roles";
}
