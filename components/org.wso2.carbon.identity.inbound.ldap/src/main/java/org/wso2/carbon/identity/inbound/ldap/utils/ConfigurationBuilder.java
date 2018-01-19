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
package org.wso2.carbon.identity.inbound.ldap.utils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.wso2.carbon.identity.inbound.ldap.impl.EndPointConfiguration;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * This class has been use for the read ldap endpoint configuration file.
 */
public class ConfigurationBuilder {

    private String connectionPassword;
    private EndPointConfiguration endPointConfiguration;
    private NodeList ldapEndpointConfiguration;

    public EndPointConfiguration getEndPointConfiguration() {
        return endPointConfiguration;
    }

    public void buildConfiguration(File inputFile) throws IdentityLdapException {
        //NodeList ldapEndpointConfiguration = null;
        try {
            // File inputFile = new File(file);
            System.setProperty("javax.xml.parsers.DocumentBuilderFactory",
                    "org.apache.xerces.jaxp.DocumentBuilderFactoryImpl");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = null;
            dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();

            ldapEndpointConfiguration = doc.getElementsByTagName("LDAPEndPointConfig");

        } catch (ParserConfigurationException e) {
            throw new IdentityLdapException("", e);
        } catch (SAXException e) {
            throw new IdentityLdapException("", e);
        } catch (IOException e) {
            throw new IdentityLdapException("", e);
        }
    }

    public void buildLDAPEndpointConfigurartion() {
        Element ele = null;
        ele = (Element) ldapEndpointConfiguration.item(0);
        endPointConfiguration = new EndPointConfiguration();

        endPointConfiguration.setAllowAnonymousAccess(getBooleanValue(getValue(ele, "allowAnonymousAccess")));
        endPointConfiguration.setAccessControlOn(getBooleanValue(getValue(ele, "accessControlEnabled")));
        endPointConfiguration.setDeNormalizedAttributesEnabled(getBooleanValue(getValue(ele,
                "denormalizeOpAttrsEnabled")));
        endPointConfiguration.setEnable(getBooleanValue(getValue(ele, "enable")));
        endPointConfiguration.setSaslHostName(getValue(ele, "saslHostName"));
        endPointConfiguration.setSaslPrincipalName(getValue(ele, "saslPrincipalName"));
        endPointConfiguration.setWorkingDirectory(getValue(ele, "workingDirectory"));
        endPointConfiguration.setInstanceId(getValue(ele, "instanceId"));
        endPointConfiguration.setEndpointPort(getIntegerValue(getValue(ele, "port")));
        endPointConfiguration.setMaxPDUSize(getIntegerValue(getValue(ele, "maxPDUSize")));
        connectionPassword = ele.getElementsByTagName("connectionPassword").item(0).getTextContent();
    }

    private int getIntegerValue(String value) {
        if (value != null) {
            return Integer.parseInt(value);
        }
        return -1;
    }

    private boolean getBooleanValue(String value) {
        if (value != null) {
            return Boolean.parseBoolean(value);
        }
        return false;
    }

    private String getValue(Element ele, String tagName) {
        return ele.getElementsByTagName(tagName).item(0).getTextContent();
    }

    public String getConnectionPassword() {
        return connectionPassword;
    }

    public boolean isLdapEndpointEnabled() {
        return endPointConfiguration.isEnable();
    }

}
