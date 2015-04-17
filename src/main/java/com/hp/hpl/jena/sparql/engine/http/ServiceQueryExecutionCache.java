/*
 * 
 * Copyright 2015 FORTH-ICS-ISL (http://www.ics.forth.gr/isl/) 
 * Foundation for Research and Technology - Hellas (FORTH)
 * Institute of Computer Science (ICS) 
 * Information Systems Laboratory (ISL)
 * 
 * Licensed under the EUPL, Version 1.1 or – as soon they
 * will be approved by the European Commission - subsequent 
 * versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the
 * Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://joinup.ec.europa.eu/software/page/eupl/licence-eupl
 * 
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 * 
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
