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

package org.wso2.carbon.identity.inbound.ldap.interceptor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.directory.server.core.DirectoryService;
import org.apache.directory.server.core.filtering.EntryFilteringCursor;
import org.apache.directory.server.core.interceptor.BaseInterceptor;
import org.apache.directory.server.core.interceptor.NextInterceptor;
import org.apache.directory.server.core.interceptor.context.AddOperationContext;
import org.apache.directory.server.core.interceptor.context.DeleteOperationContext;
import org.apache.directory.server.core.interceptor.context.ModifyOperationContext;
import org.apache.directory.server.core.interceptor.context.SearchOperationContext;
import org.wso2.carbon.identity.inbound.ldap.impl.WSO2UserManager;

public class WSO2Interceptor extends BaseInterceptor {

    private static final Log log = LogFactory.getLog(WSO2Interceptor.class);

    public void init(DirectoryService directoryService) throws Exception {
        log.info("WSO2Interceptor initialize sucessfully");
    }

    public void add(NextInterceptor next, AddOperationContext opContext) throws Exception {
        try {
            WSO2UserManager wso2UserManager = new WSO2UserManager();
            wso2UserManager.addUser(opContext);
        } catch (Exception e) {
            String msg = "Error occur in AddOperationContext";
            log.error(msg, e);
        }
    }

    public EntryFilteringCursor search(NextInterceptor next, SearchOperationContext opContext) throws Exception {
        return null;
    }

    public void modify(NextInterceptor next, ModifyOperationContext opContext) throws Exception {

    }

    public void delete(NextInterceptor next, DeleteOperationContext opContext) throws Exception {
        try {
            WSO2UserManager wso2UserManager = new WSO2UserManager();
            wso2UserManager.deleteUser(opContext);
        } catch (Exception e) {
            String msg = "Error occur in DeleteOperationContext";
            log.error(msg, e);
        }
    }

}
