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

import org.apache.directory.shared.ldap.entry.Entry;
import org.apache.directory.shared.ldap.entry.EntryAttribute;

import java.util.HashMap;
import java.util.Iterator;

/**
 * This class has been use for the set ldapUser attributes.
 */
public class LDAPUser {

    private String username;
    private String password;
    private HashMap userAttributes;

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserName() {
        return this.username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String passowrd) {
        this.password = passowrd;
    }

    public HashMap getUserAttributes() {
        return userAttributes;
    }

    public void setUserAttributes(Entry entry) {
        userAttributes = new HashMap();
        Iterator itr = entry.iterator();
        while (itr.hasNext()) {
            EntryAttribute entryAttribute = (EntryAttribute) itr.next();
            String attrId = entryAttribute.getId().toString();
            String attrValue = entry.get(attrId).get().getString();
            this.userAttributes.put(attrId, attrValue);
        }
    }
}
