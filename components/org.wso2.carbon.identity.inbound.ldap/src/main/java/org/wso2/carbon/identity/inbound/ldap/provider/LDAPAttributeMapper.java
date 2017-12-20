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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class LDAPAttributeMapper {

    private static final Log log = LogFactory.getLog(LDAPAttributeMapper.class);

    public Map<String, String> getAddUserClaimsMap(HashMap<String, String> ldapAttrSet) {
        Map<String, String> claimsMap = new HashMap<>();

        HashMap userAttributes = (HashMap) removeObjectClassAttribute(ldapAttrSet);
        Iterator ldapAttribute = userAttributes.entrySet().iterator();

        while (ldapAttribute.hasNext()) {
            Map.Entry attribute = (Map.Entry) ldapAttribute.next();
            String attributkey = attribute.getKey().toString();
            String attributeValue = attribute.getValue().toString();
            StringBuilder ldapClaimUri = new StringBuilder(LDAPConstants.LDAP_CLAIM_URI);
            String attributeClaimUri = ldapClaimUri.append(attributkey).toString();
            claimsMap.put(attributeClaimUri, attributeValue);
        }

        return claimsMap;
    }

    private Map<String, String> removeObjectClassAttribute(HashMap<String, String> attributesMap) {
        HashMap userAttributes = new HashMap();
        Iterator itr = attributesMap.entrySet().iterator();
        while (itr.hasNext()) {
            Map.Entry attribute = (Map.Entry) itr.next();
            String attrId = attribute.getKey().toString();
            if (!attrId.equals("objectclass")) {
                userAttributes.put(attrId, attribute.getValue().toString());
            }
        }

        return userAttributes;

    }
}
