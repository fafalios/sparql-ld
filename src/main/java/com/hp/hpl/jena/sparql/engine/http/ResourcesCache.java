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

import com.hp.hpl.jena.rdf.model.Model;
import java.util.HashMap;

/**
 * Request-scope cache (in the context of a submitted SPARQL query) of the
 * already-retrieved resources/datasets. The cache stores pairs of [IRI, Model]
 * in order to avoid re-fetching the same resource triples.
 *
 * @author Pavlos Fafalios (fafalios@ics.forth.gr, fafalios.pavlos@gmail.com)
 */
public class ResourcesCache {

    private HashMap<String, Model> iri2model;

    /**
     * Initialize a new cache.
     *
     */
    public ResourcesCache() {
        this.iri2model = new HashMap<>();
    }

    /**
     * Add to the cache a pair (IRI, Model).
     *
     * @param iri The IRI of the Web resource.
     * @param model The RDF model of the Web resource
     */
    public void add(String iri, Model model) {
        iri2model.put(iri, model);
    }

    /**
     * Check if a Web resource exists in the cache.
     *
     * @param iri The IRI of the Web resource.
     * @return True if the given Web resource exists in the cache
     */
    public boolean inCache(String iri) {
        if (iri2model.containsKey(iri)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Get the RDF model of a Web resource.
     *
     * @param iri The IRI of the Web resource.
     * @return The RDF model of the given Web resource
     */
    public Model getModel(String iri) {
        return iri2model.get(iri);
    }

    /**
     * Get the IRI-Model map.
     *
     * @return The IRI-Model map.
     */
    public HashMap<String, Model> getIri2model() {
        return iri2model;
    }
}
