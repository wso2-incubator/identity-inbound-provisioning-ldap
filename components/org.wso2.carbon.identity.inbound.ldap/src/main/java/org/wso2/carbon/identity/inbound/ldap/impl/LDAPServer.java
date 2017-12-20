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

package org.wso2.carbon.identity.inbound.ldap.impl;

/**
 * Represents a LDAP server.
 */
public interface LDAPServer {

    /**
     * Initializes directory service. Will create a LDAP server instance using apacheeds
     * APIs.
     */
    void init(EndPointConfiguration configurations) throws Exception;

    /**
     * Will start LDAP server. Must be initialized first.
     */
    void start() throws Exception;

    /**
     * Stopes the LDAP server.
     */
    void stop() throws Exception;

    /**
     * Changes the connection user password. As for now we don't allow users to change admin domain
     * name.
     * But users can change admin's password using this method.
     *
     * @param password New password.
     */
    void changeConnectionUserPassword(String password)
            throws Exception;

    /**
     * Gets system admins connection domain name.
     * Ex :- uid=admin,ou=system
     *
     * @return Admin's domain name as a domain name entry.
     */
    String getConnectionDomainName() throws Exception;

}
