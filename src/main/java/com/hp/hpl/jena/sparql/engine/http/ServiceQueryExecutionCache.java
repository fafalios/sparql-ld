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

import java.util.HashSet;

/**
 * Request-scope cache that stores pairs of (IRI, query) in order to avoid
 * unnecessary SERVICE invocations.
 *
 * @author Pavlos Fafalios (fafalios@ics.forth.gr, fafalios@csd.uoc.gr)
 */
public class ServiceQueryExecutionCache {

    private HashSet<String> cache; // A set for storing strings that represent pairs of (IRI, query)

    /**
     * Initialize a new cache.
     *
     */
    public ServiceQueryExecutionCache() {
        this.cache = new HashSet<>();
    }

    /**
     * Add to the cache a pair (IRI, query).
     *
     * @param iri The IRI of the pair (IRI, query)
     * @param query The query of the pair (IRI, query)
     */
    public void add(String iri, String query) {
        cache.add(iri + query);
    }

    /**
     * Check if a pair (IRI, query) exists in the cache.
     *
     * @param iri The IRI of the pair (IRI, query)
     * @param query The query of the pair (IRI, query)
     * @return True if the given pair exists in the cache
     */
    public boolean inCache(String iri, String query) {
        String toCheck = iri + query;
        if (cache.contains(toCheck)) {
            return true;
        } else {
            return false;
        }
    }
}
