/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.carbon.identity.inbound.ldap.provider;

/**
 * This class has been use for the introduce new ldap claim dialect for IS.
 */
public class LDAPConstants {


    public static final String LDAP_CLAIM_URI = "http://wso2.org/ldap/claim/";

    public static class LDAPSchemaConstants {

        public static final String LDAP_SCHEMA_URI = "http://wso2.org/ldap/claim";
        public static final String GIVEN_NAME_URI = "http://wso2.org/ldap/claim/givenName";
        public static final String GIVEN_NAME = "givenName";
        public static final String NICK_NAME_URI = "http://wso2.org/ldap/claim/nickName";
        public static final String NICK_NAME = "nickName";
        public static final String LAST_NAME_URI = "http://wso2.org/ldap/claim/sn";
        public static final String LAST_NAME = "sn";
        public static final String EMAIL_ADDRESS_URI = "http://wso2.org/ldap/claim/mail";
        public static final String EMAIL_ADDRESS = "mail";
        public static final String DATE_OF_BIRTH_URI = "http://wso2.org/ldap/claim/dateOfBirth";
        public static final String DATE_OF_BIRTH = "DOB";
        public static final String GENDER_URI = "http://wso2.org/ldap/claim/gender";
        public static final String GENDER = "gender";
        public static final String COUNTRY_URI = "http://wso2.org/ldap/claim/country";
        public static final String COUNTRY = "country";
        public static final String STREET_ADDRESS_URI = "http://wso2.org/ldap/claim/streetAddress";
        public static final String STREET_ADDRESS = "streetAddress";
        public static final String HOME_PHONE_URI = "http://wso2.org/ldap/claim/homePhone";
        public static final String HOME_PHONE = "homePhone";
        public static final String OTHER_PHONE_URI = "http://wso2.org/ldap/claim/otherPhone";
        public static final String OTHER_PHONE = "otherPhone";
        public static final String MOBILE_URI = "http://wso2.org/ldap/claim/mobile";
        public static final String MOBILE = "mobile";
        public static final String LOCALITY_NAME_URI = "http://wso2.org/ldap/claim/localityName";
        public static final String LOCALITY_NAME = "localityName";
        public static final String STATE_OR_PROVINCE_NAME_URI = "http://wso2.org/ldap/claim/stateOrProvinceName";
        public static final String STATE_OR_PROVINCE_NAME = "stateOrProvinceName";
        public static final String POSTAL_CODE_URI = "http://wso2.org/ldap/claim/postalCode";
        public static final String POSTAL_CODE = "postalCode";
        public static final String PPID_URI = "http://wso2.org/ldap/claim/PPID";
        public static final String PPID = "privatePersonalIdentifier";
        public static final String CN_URI = "http://wso2.org/ldap/claim/cn";
        public static final String CN = "cn";
        public static final String ORGANIZATION_URI = "http://wso2.org/ldap/claim/organizationName";
        public static final String ORGANIZATION = "organizationName";
        public static final String TELEPHONE_URI = "http://wso2.org/ldap/claim/telephoneNumber";
        public static final String TELEPHONE = "telephoneNumber";
        public static final String USERNAME_URI = "http://wso2.org/ldap/claim/uid";
        public static final String USERNAME = "uid";
        public static final String PASSWORD_URI = "http://wso2.org/ldap/claim/userpassword";
        public static final String PASSWORD = "userPassword";
        public static final String ROLE_URI = "http://wso2.org/ldap/claim/role";
        public static final String ROLE = "role";
        public static final String CREATED_TIME_URI = "http://wso2.org/ldap/claim/createdDate";
        public static final String CREATED_TIME = "createdDate";
        public static final String IM_URI = "http://wso2.org/ldap/claim/im";
        public static final String IM = "im";
        public static final String MODIFIED_TIME_URI = "http://wso2.org/ldap/claim/lastModifiedDate";
        public static final String MODIFIED_TIME = "lastModifiedDate";
        public static final String SCIM_ID_URI = "http://wso2.org/ldap/claim/scimId";
        public static final String SCIM_ID = "scimId";
    }
}
