# SPARQL-LD: A SPARQL Extension for Fetching and Querying Linked Data

This Jena ARQ SERVICE extension allows to fetch, query and integrate in the same SPARQL query:
- *data stored in the (local) endpoint*
- *data coming from online RDF files (in any standard format)*
- *data embedded in Web pages as RDFa*
- *data coming from JSON-LD files*
- *data coming from dereferenceable URIs*
- *data coming by querying other SPARQL endpoints*

by simply using the SERVICE operator of [SPARQL 1.1 Federated Query](http://www.w3.org/TR/sparql11-federated-query/).

A distinctive characteristic of SPARQL-LD is that it enables to 
fetch and query even data in datasets returned by a portion of the query,
i.e. discovered at query-execution time. 

SPARQL-LD is actually a generalization of SPARQL
in the sense that every query that can be answered by the original SPARQL can
be also answered by SPARQL-LD. Specifically, if the IRI given to the service
operator corresponds to a SPARQL endpoint, then it works exactly as the original
SPARQL (the remote endpoint evaluates the query and returns the result).
Otherwise, instead of returning an error (and no bindings), it tries to fetch and
query the triples that may exist in the given resource.


SPARQL-LD has been tested with Jena 2.13.0 ARQ (nevertheless, it may also work with other Jena ARQ releases). 

## How to cite SPARQL-LD
Cite the ISWC'15 demo paper:
P. Fafalios and Y. Tzitzikas, "SPARQL-LD: A SPARQL Extension for Fetching and Querying Linked Data", 14th International Semantic Web Conference (Demo Paper), ISWC 2015, Bethlehem, Pennsylvania, USA, October 11-15, 2015 ([PDF](http://users.ics.forth.gr/~fafalios/files/pubs/fafalios_2015_sparql-ld.pdf)
 | [BIB](http://users.ics.forth.gr/~fafalios/files/bibs/fafalios2015sparql.bib))

## Demo

A SPARQL endpoint that realizes SPARQL-LD is available online at: http://users.ics.forth.gr/~fafalios/sparql-ld-endpoint.
 
## Example Query

The following query 
can be answered by an implementation of SPARQL-LD.
The query returns all co-authors of Pavlos Fafalios (main contributor of this repository)
together with the number of their publications and the number of different conferences
in which they have a publication.
Notice that this query combines and integrates:
i) data embedded in the HTML Web page http://users.ics.forth.gr/~fafalios as RDFa (lines 3-4),
ii) data coming from dereferenceable URIs derived at *query-execution* time (lines 5-6), and
iii) data coming by querying another endpoint (lines 7-9).
Note also that this query can be answered by any endpoint that implements
this extension (independently of its "local" contents).

```
1.  SELECT DISTINCT ?authorURI (count(distinct ?paper)  AS ?numOfPapers)
2.                             (count(distinct ?series) AS ?numOfDiffConfs) WHERE {
3.    SERVICE <http://users.ics.forth.gr/~fafalios/> {
4.      ?p <http://purl.org/dc/terms/creator> ?authorURI }
5.    SERVICE ?authorURI { 
6.      ?paper <http://purl.org/dc/elements/1.1/creator> ?authorURI }
7.    SERVICE <http://dblp.l3s.de/d2r/sparql> {
8.      ?p2 <http://purl.org/dc/elements/1.1/creator> ?authorURI .
9.      ?p2 <http://swrc.ontoware.org/ontology#series> ?series  }
10. } GROUP BY ?authorURI ORDER BY ?numOfPapers
```
 
## Source code

For implementing SPARQL-LD, we have created the following 4 classes:

- com.hp.hpl.jena.sparql.engine.http.**ReadRDFFromIRI**
- com.hp.hpl.jena.sparql.engine.http.**ResourcesCache**
- com.hp.hpl.jena.sparql.engine.http.**EndpointsIndex**
- arq.**SPARQL_LD_QueryExamples**

We have also updated the following 2 classes of Jena 2.13.0 ARQ:

- com.hp.hpl.jena.sparql.engine.**QueryExecutionBase**
- com.hp.hpl.jena.sparql.engine.http.**Service**


This repository contains only the above 6 classes. 
We also provide a zip containing the *original* Jena 2.13.0 ARQ source code
(as downloaded from [https://jena.apache.org/download](https://jena.apache.org/download) in April 17, 2015)
as well as the extended, already built, Jena ARQ JAR file (**jena-arq-2.13.0_SPARQL-LD-1.0.jar**). 

## Installation

- Download the original Jena 2.13.0 ARQ source code
- Add the 4 new classes
- Replace the 2 updated classes
- Add the *endpoints.lst* file to the project folder (same level as pom.xml)
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
- Try to run the main class "arq.SPARQL_LD_QueryExamples"

or:

- Directly use the provided (already built) extended Jena ARQ jar: 

**jena-arq-2.13.0_SPARQL-LD-1.0.jar**
