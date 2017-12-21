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

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.directory.shared.ldap.schema.ldif.extractor.SchemaLdifExtractor;
import org.wso2.carbon.identity.inbound.ldap.utils.IdentityIOStreamUtils;
import org.wso2.carbon.identity.inbound.ldap.utils.IdentityLdapException;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * This is a schema ldif file extractor. Given a zip file of schemas, following class will unzip
 * schemas to a folder called "schema" under working directory.
 */

class CarbonSchemaLdifExtractor implements SchemaLdifExtractor {

    private static final String SCHEMA_SUB_DIR = "schema";

    private static final Log log = LogFactory.getLog(CarbonSchemaLdifExtractor.class);

    private boolean extracted;

    private File schemaDirectory;

    private File zipSchemaStore;

    /**
     * This will instantiate a Schema extractor.
     *
     * @param outputDirectory The directory which LDIF files will be extracted to.
     * @param zipSchemaStore  A zip file containing all default LDIF schema files.
     */
    public CarbonSchemaLdifExtractor(File outputDirectory, File zipSchemaStore) {

        this.zipSchemaStore = zipSchemaStore;
        this.schemaDirectory = new File(outputDirectory, SCHEMA_SUB_DIR);

        if (!outputDirectory.exists()) {
            log.debug(String.format("Creating output directory: %s", outputDirectory));
            if (!outputDirectory.mkdir()) {
                log.error(String.format("Failed to create outputDirectory: %s",
                        outputDirectory));
            }
        } else {
            log.debug("Output directory exists: no need to create.");
        }

        if (!schemaDirectory.exists()) {
            if (log.isDebugEnabled()) {
                log.debug(String.format("Schema directory '%s' does NOT exist: extracted state " +
                        "set to false.", schemaDirectory));
            }

            extracted = false;
        } else {
            if (log.isDebugEnabled()) {
                log.debug(String.format("Schema directory '%s' does exist: extracted state set to " +
                        "true.", schemaDirectory));
            }

            extracted = true;
        }

    }

    @Override
    public boolean isExtracted() {
        return extracted;
    }

    @Override
    public void extractOrCopy(boolean overwrite)
            throws IOException {

        if (schemaDirectory.exists() && overwrite) {
            // remove the existing schema directory
            FileUtils.deleteDirectory(schemaDirectory);
        }

        if (!schemaDirectory.exists() && !schemaDirectory.mkdir()) {
            throw new IOException("Unable to create schema directory " +
                    schemaDirectory.getAbsolutePath());
        }

        if (!this.zipSchemaStore.exists()) {
            String msg = "Did not find LDAP schema files in " +
                    this.zipSchemaStore.getAbsolutePath();
            log.error(msg);
            throw new IOException(msg);
        }

        try {
            unzipSchemaFile();
        } catch (Exception e) {
            e.printStackTrace();
        }

        extracted = true;
    }

    protected void unzipSchemaFile()
            throws IOException, IdentityLdapException {
        ZipInputStream zipFileStream = null;

        try {
            FileInputStream schemaFileStream = new FileInputStream(this.zipSchemaStore);
            zipFileStream = new ZipInputStream(new BufferedInputStream(schemaFileStream));
            ZipEntry entry;

            String basePath = this.schemaDirectory.getAbsolutePath();

            while ((entry = zipFileStream.getNextEntry()) != null) {

                if (entry.isDirectory()) {
                    File newDirectory = new File(basePath, entry.getName());
                    if (!newDirectory.mkdir()) {
                        throw new IOException("Unable to create directory - " +
                                newDirectory.getAbsolutePath());
                    }
                    continue;
                }

                int size;
                byte[] buffer = new byte[2048];

                FileOutputStream extractedSchemaFile = new FileOutputStream(
                        new File(basePath, entry.getName()));
                BufferedOutputStream extractingBufferedStream =
                        new BufferedOutputStream(extractedSchemaFile, buffer.length);
                try {
                    while ((size = zipFileStream.read(buffer, 0, buffer.length)) != -1) {
                        extractingBufferedStream.write(buffer, 0, size);
                    }
                } finally {
                    IdentityIOStreamUtils.flushOutputStream(extractingBufferedStream);
                    IdentityIOStreamUtils.closeOutputStream(extractingBufferedStream);
                }
            }
        } catch (IOException e) {
            String msg = "Unable to extract schema directory to location " +
                    this.schemaDirectory.getAbsolutePath() + " from " +
                    this.zipSchemaStore.getAbsolutePath();
            log.error(msg, e);
            throw new IOException(msg, e);
        } finally {
            IdentityIOStreamUtils.closeInputStream(zipFileStream);
        }

        if (log.isDebugEnabled()) {
            log.debug("Successfully extracted schema files to path " +
                    this.schemaDirectory.getAbsolutePath() + " using schema zip file " +
                    this.zipSchemaStore.getAbsolutePath());
        }

    }

    @Override
    public void extractOrCopy()
            throws IOException {
        extractOrCopy(false);
    }
}
