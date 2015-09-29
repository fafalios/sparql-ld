/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hp.hpl.jena.sparql.engine.http;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Index of known SPARQL endpoints.
 *
 * @author Pavlos Fafalios (fafalios@ics.forth.gr, fafalios@csd.uoc.gr)
 */
public class EndpointsIndex {

    /* 
     * A set containing the IRIs of the known SPARQL endpoints.
     */
    private HashSet<String> endpoints;
    /* 
     * The file containing the list of known SPARQL endpoints.
     */
    private String filepath;

    /**
     * Initialize a new index by reading a list of endpoints from a given file.
     *
     * @param filepath The file path.
     */
    public EndpointsIndex(String file) {

        this.endpoints = new HashSet<>();
        this.filepath = file;

        try {

            File fileDir = new File(filepath);
            try (BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                    new FileInputStream(fileDir), "UTF8"))) {
                String str;
                while ((str = in.readLine()) != null) {
                    if (!str.trim().equals("")) {
                        if (!str.trim().startsWith("#")) {
                            endpoints.add(str.trim().toLowerCase());
                        }
                    }
                }
            }

        } catch (IOException ex) {
            Logger.getLogger(EndpointsIndex.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Initialize a new index by reading a list of endpoints from a file.
     *
     */
    public EndpointsIndex() {
        this("endpoints.lst");
    }

    /**
     * Add to the index the IRI of a SPARQL endpoint.
     *
     * @param endpointIRI The IRI of the SPARQL Endpoint.
     */
    public void add(String endpointIRI) {
        endpoints.add(endpointIRI.trim().toLowerCase());
        try {
            FileWriter fstream = new FileWriter(filepath, true);
            try (BufferedWriter out = new BufferedWriter(fstream)) {
                out.write(endpointIRI);
                out.newLine();
            }
        } catch (IOException ex) {
            Logger.getLogger(EndpointsIndex.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Check if a IRI exists in the index.
     *
     * @param iri The IRI of the Web resource.
     * @return True if the given Web resource exists in the cache
     */
    public boolean inIndex(String iri) {
        if (endpoints.contains(iri.trim().toLowerCase())) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Return the set of SPARQL endpoints in the Index.
     *
     * @return A set of SPARQL endpoints.
     */
    public HashSet<String> getEndpoints() {
        return endpoints;
    }

    /**
     * Return the file containing the list of known SPARQL endpoints..
     *
     * @return The file path.
     */
    public String getFilepath() {
        return filepath;
    }

    /**
     * Set the file containing the list of known SPARQL endpoints..
     *
     * @param filepath The file path.
     */
    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }
}
