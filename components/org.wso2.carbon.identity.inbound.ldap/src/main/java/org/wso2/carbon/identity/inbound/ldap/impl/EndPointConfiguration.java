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

public class EndPointConfiguration {

    private final static String DEFAULT_INSTANCE_ID = "default";

    /**
     * LDAP server specific configurations
     */
    private boolean enable;

    private int ldapPort;

    private String workingDirectory;

    private boolean allowAnonymousAccess;

    private boolean accessControlOn;

    private boolean deNormalizedAttributesEnabled;

    private int maxPDUSize;

    /**
     * Instance specific configurations
     */

    private String instanceId = DEFAULT_INSTANCE_ID;

    private String saslHostName;

    private String saslPrincipalName;

    public EndPointConfiguration() {
    }

    public boolean isAllowAnonymousAccess() {
        return allowAnonymousAccess;
    }

    public void setAllowAnonymousAccess(boolean allowAnonymousAccess) {
        this.allowAnonymousAccess = allowAnonymousAccess;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public boolean isAccessControlOn() {
        return accessControlOn;
    }

    public void setAccessControlOn(boolean accessControlOn) {
        this.accessControlOn = accessControlOn;
    }

    public boolean isDeNormalizedAttributesEnabled() {
        return deNormalizedAttributesEnabled;
    }

    public void setDeNormalizedAttributesEnabled(boolean deNormalizedAttributesEnabled) {
        this.deNormalizedAttributesEnabled = deNormalizedAttributesEnabled;
    }

    public int getMaxPDUSize() {
        return maxPDUSize;
    }

    public void setMaxPDUSize(int maxPDUSize) {
        if (maxPDUSize == -1) {
            return;
        }
        this.maxPDUSize = maxPDUSize;
    }

    public String getSaslHostName() {
        return saslHostName;
    }

    public void setSaslHostName(String saslHostName) {
        if (saslHostName == null) {
            return;
        }

        this.saslHostName = saslHostName;
    }

    public String getSaslPrincipalName() {
        return saslPrincipalName;
    }

    public void setSaslPrincipalName(String saslPrincipalName) {
        if (saslPrincipalName == null) {
            return;
        }

        this.saslPrincipalName = saslPrincipalName;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        if (instanceId == null) {
            return;
        }

        this.instanceId = instanceId;
    }

    public int getEndpointPort() {
        return ldapPort;
    }

    public void setEndpointPort(int ldapPort) {
        if (ldapPort == -1) {
            return;
        }

        this.ldapPort = ldapPort;
    }

    public String getWorkingDirectory() {
        return workingDirectory;
    }

    public void setWorkingDirectory(String workingDirectory) {
        if (workingDirectory == null) {
            return;
        }

        this.workingDirectory = workingDirectory;
    }
}
