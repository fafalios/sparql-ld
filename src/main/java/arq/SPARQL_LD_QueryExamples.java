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
package arq;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

/**
 * Examples that exploit the functionality offered by SPARQL-LD.
 *
 * @author Pavlos Fafalios (fafalios@ics.forth.gr, fafalios.pavlos@gmail.com)
 */
public class SPARQL_LD_QueryExamples {

    private static String QUERY_TO_RUN; // The query to run

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {


        // Create an empty in-memory model and populate it with the DBpedia triples describing Yellowfin tuna
        Model model = ModelFactory.createDefaultModel();
        model.read("http://dbpedia.org/data/Yellowfin_tuna.n3");

        StringBuilder QUERY_getLocalTriples = new StringBuilder();
        QUERY_getLocalTriples.append("SELECT * WHERE { ?x ?y ?z }");

        StringBuilder QUERY_getRDFa = new StringBuilder();
        QUERY_getRDFa.append("SELECT DISTINCT ?y WHERE { ");
        QUERY_getRDFa.append(" ?x1 ?y ?z1 . ");
        QUERY_getRDFa.append(" SERVICE <http://users.ics.forth.gr/~fafalios> { ");
        QUERY_getRDFa.append("  ?x2 ?y ?z2 } ");
        QUERY_getRDFa.append("}");


        StringBuilder QUERY_getServiceTriples = new StringBuilder();
        QUERY_getServiceTriples.append("PREFIX oa: <http://www.w3.org/ns/oa#> ");
        QUERY_getServiceTriples.append("PREFIX oae: <http://www.ics.forth.gr/isl/oae/core#> ");
        QUERY_getServiceTriples.append("PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> ");
        QUERY_getServiceTriples.append("PREFIX foaf: <http://xmlns.com/foaf/0.1/> ");
        QUERY_getServiceTriples.append("PREFIX dbpedia: <http://dbpedia.org/resource/> ");
        QUERY_getServiceTriples.append("PREFIX dbpedia-owl: <http://dbpedia.org/ontology/> ");
        QUERY_getServiceTriples.append("SELECT DISTINCT ?detectedEntity ?categoryName (count(?position) as ?NumOfOccurrences) ");
        QUERY_getServiceTriples.append("WHERE { ");
        QUERY_getServiceTriples.append("  SERVICE <http://dbpedia.org/resource/Thunnus> { ");
        QUERY_getServiceTriples.append("    dbpedia:Thunnus dbpedia-owl:wikiPageExternalLink ?page } ");
        QUERY_getServiceTriples.append("  VALUES ?templ { <http://139.91.183.72/x-link-marine/api?categories=fish;country&url=PAGE> } BIND(REPLACE(str(?templ), 'PAGE', str(?page), 'i') as ?x) BIND(URI(?x) as ?serv) ");
        QUERY_getServiceTriples.append("  SERVICE ?serv { ");
        QUERY_getServiceTriples.append("    ?annot oa:hasBody ?ent . ");
        QUERY_getServiceTriples.append("    ?ent oae:regardsEntityName ?detectedEntity ; ");
        QUERY_getServiceTriples.append("         oae:position ?position ; ");
        QUERY_getServiceTriples.append("         oae:belongsTo ?category . ?category rdfs:label ?categoryName } ");
        QUERY_getServiceTriples.append("} GROUP BY ?detectedEntity ?categoryName ORDER BY DESC(?NumOfOccurrences)");

        StringBuilder QUERY_rdfa_derefUri = new StringBuilder();
        QUERY_rdfa_derefUri.append("PREFIX foaf: <http://xmlns.com/foaf/0.1/> ");
        QUERY_rdfa_derefUri.append("SELECT DISTINCT ?authorName (count(DISTINCT ?paper2) as ?numOfPapers) ");
        QUERY_rdfa_derefUri.append("WHERE { ");
        QUERY_rdfa_derefUri.append("  SERVICE <http://users.ics.forth.gr/~fafalios/> { ");
        QUERY_rdfa_derefUri.append("    ?paper1 <http://purl.org/dc/terms/creator> ?author ");
        QUERY_rdfa_derefUri.append("    FILTER(?author != <http://dblp.l3s.de/d2r/resource/authors/Pavlos_Fafalios>) } ");
        QUERY_rdfa_derefUri.append("  SERVICE ?author { ");
        QUERY_rdfa_derefUri.append("    ?author foaf:name ?authorName . ");
        QUERY_rdfa_derefUri.append("    ?paper2 <http://purl.org/dc/elements/1.1/creator> ?author } ");
        QUERY_rdfa_derefUri.append("} GROUP BY ?authorName ORDER BY DESC(?numOfPapers)");

        StringBuilder QUERY_rdfa_derefUri_endpoint = new StringBuilder();
        QUERY_rdfa_derefUri_endpoint.append("PREFIX foaf: <http://xmlns.com/foaf/0.1/> ");
        QUERY_rdfa_derefUri_endpoint.append("SELECT DISTINCT ?authorName (count(DISTINCT ?paper) AS ?numOfPapers) (count(DISTINCT ?series) AS ?numOfDiffConfs) WHERE { ");
        QUERY_rdfa_derefUri_endpoint.append("  SERVICE <http://users.ics.forth.gr/~fafalios> { ");
        QUERY_rdfa_derefUri_endpoint.append("    ?p <http://purl.org/dc/terms/creator> ?authorURI } ");
        QUERY_rdfa_derefUri_endpoint.append("  SERVICE ?authorURI { ");
        QUERY_rdfa_derefUri_endpoint.append("    ?author foaf:name ?authorName . ");
        QUERY_rdfa_derefUri_endpoint.append("    ?paper <http://purl.org/dc/elements/1.1/creator> ?authorURI } ");
        QUERY_rdfa_derefUri_endpoint.append("  SERVICE <http://dblp.l3s.de/d2r/sparql> { ");
        QUERY_rdfa_derefUri_endpoint.append("    ?p2 <http://purl.org/dc/elements/1.1/creator> ?authorURI . ");
        QUERY_rdfa_derefUri_endpoint.append("    ?p2 <http://swrc.ontoware.org/ontology#series> ?series } ");
        QUERY_rdfa_derefUri_endpoint.append("} GROUP BY ?authorName ORDER BY DESC(?numOfPapers)");


        // SET HERE THE QUERY TO RUN //
        QUERY_TO_RUN = QUERY_getRDFa.toString();

        // Create a Query object
        Query query = QueryFactory.create(QUERY_TO_RUN);

        // Execute the query and obtain results
        QueryExecution qe = QueryExecutionFactory.create(query, model);
        ResultSet results = qe.execSelect();

        // Output query results	
        ResultSetFormatter.out(System.out, results, query);

        // Free up resources used running the query
        qe.close();
    }
}
