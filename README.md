# Jena ARQ SERVICE Extension for Querying the Web of Data

This Jena ARQ SERVICE extension allows to query and integrate in the same SPARQL query:
- *data stored in the (local) endpoint*
- *data coming from online RDF files (in any standard format)*
- *data embedded in Web pages as RDFa*
- *data coming from JSON-LD files*
- *data coming from dereferenceable URIs*
- *data coming by querying other SPARQL endpoints*

by simply using the SERVICE operator of [SPARQL 1.1 Federated Query](http://www.w3.org/TR/sparql11-federated-query/).

A distinctive characteristic of this extension is that it enables to
query and integrate even data in datasets returned by a portion of the query,
i.e. discovered at query-execution time. 

This extension has been tested with Jena ARQ 2.13.0 (nevertheless, it **may** also work with other Jena ARQ releases). 
 
## New and updated files 

This repository contains only the *new* and the *updated* classes of Jena 2.13.0 ARQ.
Specifically, the following new classes have been created:

- main/com.hp.hpl.jena.sparql.engine.http.**ReadRDFFromIRI**
- main/com.hp.hpl.jena.sparql.engine.http.**ServiceQueryExecutionCache**
- main/arq.**service_extension_query_examples**

and the following classes have been updated:

- main/com.hp.hpl.jena.sparql.engine.http.**Service**
- main/com.hp.hpl.jena.sparql.engine.main.iterator.**QueryIterService**
- main/com.hp.hpl.jena.sparql.engine.ref.**EvaluatorDispatch**
- test/com.hp.hpl.jena.sparql.engine.http.**TestService**

We also provide a zip containing the *original* Jena 2.13.0 ARQ sources
(as downloaded from [https://jena.apache.org/download](https://jena.apache.org/download) in April 17, 2015). 

## Installation

- Download the original Jena 2.13.0 ARQ sources 
- Add the 3 new classes
- Replace the 4 updated classes
- Add the following dependency to pom.xml (which allows to load and query RDFa data):
```
 <dependency>
   <groupId>org.semarglproject</groupId>
   <artifactId>semargl-jena</artifactId>
   <version>0.6.1</version>
   <exclusions>
     <exclusion>
       <groupId>org.apache.jena</groupId>
       <artifactId>jena-core</artifactId>
     </exclusion>
   </exclusions>
 </dependency>
```	
- Build the sources
- Try to run the main class "arq.service_extension_query_examples"

## Example Query

The following query 
can be answered by an implementation of the proposed extension.
The query returns all co-authors of Pavlos Fafalios (main contributor of this repository)
together with their number of publications and the number of different conferences
in which they have a publication.
Notice that this query combines and integrates:
i) data embedded in the HTML Web page http://users.ics.forth.gr/~fafalios as RDFa (lines 3-4),
ii) data coming from dereferenceable URIs derived at *query-execution* time (line 5), and
iii) data coming by querying another endpoint (lines 6-8).
Note also that this query can be answered by any endpoint that implements
the proposed extension (independently of its "local" contents).

```
1. SELECT DISTINCT ?authorURI (count(?paper) AS ?numOfPapers)
2.                            (count(distinct ?series) AS ?numOfDiffConfs) WHERE {
3.   SERVICE <http://users.ics.forth.gr/~fafalios/> {
4.     ?p <http://purl.org/dc/terms/creator> ?authorURI }
5.   SERVICE ?authorURI { ?paper <http://purl.org/dc/elements/1.1/creator> ?authorURI }
6.   SERVICE <http://dblp.l3s.de/d2r/sparql> {
7.     ?p2 <http://purl.org/dc/elements/1.1/creator> ?authorURI .
8.     ?p2 <http://swrc.ontoware.org/ontology#series> ?series  }
9. } GROUP BY ?authorURI ORDER BY ?numOfPapers
```


