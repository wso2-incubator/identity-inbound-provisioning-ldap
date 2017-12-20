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

import org.junit.Test;

import java.util.HashMap;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;

public class LDAPAttributeMapperTest {

    @Test
    public void getClaimsMap() throws Exception {
        LDAPAttributeMapper ldapAttributeMapper = new LDAPAttributeMapper();
        HashMap ldapAttrSet = new HashMap();
        ldapAttrSet.put("cn", "testcn");
        ldapAttrSet.put("sn", "testsn");
        ldapAttrSet.put("mail", "test@gmail.com");
        ldapAttrSet.put("country", "SriLanka");
        ldapAttrSet.put("organizationName", "WSO2");
        ldapAttrSet.put("givenName", "abc");
        ldapAttrSet.put("createdDate", "2017-10-02");
        ldapAttrSet.put("lastModifiedDate", "2017-10-02T17:16:04");
        ldapAttrSet.put("objectclass", "top");
        ldapAttrSet.put("mobile", "0717463047");
        ldapAttrSet.put("scimId", "e8c029b1-e397-412e-87db-2125bb3ada56");
        HashMap claimMapExpected = (HashMap) ldapAttributeMapper.getAddUserClaimsMap(ldapAttrSet);

        HashMap claimMap = new HashMap();
        claimMap.put(LDAPConstants.LDAPSchemaConstants.CN_URI, "testcn");
        claimMap.put(LDAPConstants.LDAPSchemaConstants.LAST_NAME_URI, "testsn");
        claimMap.put(LDAPConstants.LDAPSchemaConstants.EMAIL_ADDRESS_URI, "test@gmail.com");
        claimMap.put(LDAPConstants.LDAPSchemaConstants.COUNTRY_URI, "SriLanka");
        claimMap.put(LDAPConstants.LDAPSchemaConstants.ORGANIZATION_URI, "WSO2");
        claimMap.put(LDAPConstants.LDAPSchemaConstants.GIVEN_NAME_URI, "abc");
        claimMap.put(LDAPConstants.LDAPSchemaConstants.CREATED_TIME_URI, "2017-10-02");
        claimMap.put(LDAPConstants.LDAPSchemaConstants.MODIFIED_TIME_URI, "2017-10-02T17:16:04");
        claimMap.put(LDAPConstants.LDAPSchemaConstants.MOBILE_URI, "0717463047");
        claimMap.put(LDAPConstants.LDAPSchemaConstants.SCIM_ID_URI, "e8c029b1-e397-412e-87db-2125bb3ada56");

        assertNotNull(claimMapExpected);
        assertFalse(claimMapExpected.containsValue("top"));
        assertEquals(claimMap, claimMapExpected);

    }

}
