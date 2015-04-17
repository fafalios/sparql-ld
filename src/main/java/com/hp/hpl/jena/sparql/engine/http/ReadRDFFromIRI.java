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

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.semarglproject.jena.rdf.rdfa.JenaRdfaReader;

/**
 * Reads and queries the RDF data that may exist in the IRI given to the SERVICE
 * operator.
 *
 * @author Pavlos Fafalios (fafalios@ics.forth.gr, fafalios@csd.uoc.gr)
 */
public class ReadRDFFromIRI {

    private static final String askQuery = "ASK { ?x ?y ?z }"; // A SPARQL ASK query for checking if the IRI corresponds to a SPARQL endpoint
    private String iri; // The IRI of the SERVICE operator
    private Query query; // The query to run at the RDF data that may exist in the IRI.
    private ResultSet resultSet; // A ResultSet object containing the results of running the query to the IRI.
    private QueryExecution qe; // A QueryExecution object for running the query to the model that corresponds to the IRI.
    private String contentType; // The IRI content type

    /**
     * Create a new object for reading and querying the RDF data that may exist
     * in the given IRI.
     *
     * @param iri The IRI of the SERVICE operator.
     * @param query The query to run at the RDF data that may exist in the IRI.
     */
    public ReadRDFFromIRI(String iri, Query query) {
        this.iri = iri;
        this.query = query;

        read();
    }

    /**
     * Read the RDF data that may exist in the IRI by checking both the IRI file
     * extension and the IRI content type.
     *
     */
    private void read() {
        Model model = ModelFactory.createDefaultModel();

        /* First check the IRI file extension */
        if (iri.toLowerCase().endsWith(".ntriples") || iri.toLowerCase().endsWith(".nt")) {
            System.out.println("# Reading a '.nt' file...");
            model.read(iri, "N-TRIPLE");
            qe = QueryExecutionFactory.create(query, model);
            resultSet = qe.execSelect();
        } else if (iri.toLowerCase().endsWith(".json") || iri.toLowerCase().endsWith(".jsod") || iri.toLowerCase().endsWith(".jsonld")) {
            System.out.println("# Trying to read a 'json-ld' file...");
            model.read(iri, "JSON-LD");
            qe = QueryExecutionFactory.create(query, model);
            resultSet = qe.execSelect();
        } else {
            setContentType(); // get the IRI content type
            System.out.println("# IRI Content Type: " + contentType);
            if (contentType.contains("text/html") || contentType.contains("application/xhtml+xml")) {
                readRDFa();
            } else if (contentType.contains("application/ld+json") || contentType.contains("application/json")) {
                System.out.println("# Trying to read a 'json' file...");
                model.read(iri, "JSON-LD");
                qe = QueryExecutionFactory.create(query, model);
                resultSet = qe.execSelect();
            } else {
                model.read(iri);
                qe = QueryExecutionFactory.create(query, model);
                resultSet = qe.execSelect();
            }
        }
    }

    /**
     * Read RDF data embedded in an HTML Web page as RDFa using the Semargl
     * framework (https://github.com/levkhomich/semargl).
     *
     */
    private void readRDFa() {

        System.out.println("# Checking if the URI contains 'RDFa' data...");

        JenaRdfaReader.inject();
        Model model = ModelFactory.createDefaultModel();
        model.read(iri, "RDFA");

        qe = QueryExecutionFactory.create(query, model);
        resultSet = qe.execSelect();
    }

    /**
     * Read the IRI content type by opening an HTTP connection.
     *
     */
    public void setContentType() {
        contentType = "";
        try {
            URL url = new URL(iri);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD");
            connection.connect();
            contentType = connection.getContentType();
        } catch (MalformedURLException ex) {
            Logger.getLogger(ReadRDFFromIRI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ReadRDFFromIRI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Close the query execution and stop query evaluation.
     *
     */
    public void close() {
        qe.close();
    }

    /**
     * Return the IRI.
     *
     * @return The IRI associated to this object.
     */
    public String getUri() {
        return iri;
    }

    /**
     * Set the IRI.
     *
     * @param iri The IRI to be associated to this object
     */
    public void setUri(String iri) {
        this.iri = iri;
    }

    /**
     * Return the ResultSet object containing the results of running the query
     * to the IRI.
     *
     * @return A ResultSet object
     */
    public ResultSet getResultSet() {
        return resultSet;
    }

    /**
     * Set the result set.
     *
     * @param resultSet The new result set
     */
    public void setResultSet(ResultSet resultSet) {
        this.resultSet = resultSet;
    }

    /**
     * Run an ASK query at the IRI for checking if it corresponds to a SPARQL
     * endpoint.
     *
     */
    public static boolean isEndpoint(String uri) {
        try {
            QueryExecution qexecTest = QueryExecutionFactory.sparqlService(uri, QueryFactory.create(askQuery));
            boolean resultsTest = qexecTest.execAsk();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}