package com.tenforce.lodms.transformers;

import com.tenforce.lodms.ODSVoc;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import virtuoso.sesame2.driver.VirtuosoRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ValueLoader {
    private Repository repository;

    public ValueLoader(String host, String port, String user, String pwd) {
        String connectionString = "jdbc:virtuoso://" + host + ':' + port;
        repository = new VirtuosoRepository(connectionString, user, pwd, true);
        try {
            RepositoryConnection con = repository.getConnection();
            con.close();
        } catch (RepositoryException e) {
            throw new IllegalArgumentException(e.getMessage(),e);
        }
    }

    public ValueLoader(Repository repository) {
        this.repository = repository;
    }
    public List<String> getValuesFor(URI context, URI predicate) throws RepositoryException {
        RepositoryConnection con = repository.getConnection();
        List<String> values = new ArrayList<String>();
        try {
            TupleQuery query = con.prepareTupleQuery(QueryLanguage.SPARQL, "SELECT distinct ?v WHERE {GRAPH <" + context + "> {?d <" + predicate + "> ?v}}");
            TupleQueryResult statements = query.evaluate();
            while(statements.hasNext())
                values.add(statements.next().getBinding("v").getValue().stringValue());
            statements.close();
        }
        finally {
            con.close();
            return values;
        }
    }
    public List<Resource> getAvailableGraph() throws RepositoryException {
        List<Resource> graphList = Collections.emptyList();
        RepositoryConnection con = repository.getConnection();
        try {
            String queryString = "SELECT distinct ?g WHERE {GRAPH ?g {?g a ?catalog}}";
            TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
            tupleQuery.setBinding("catalog", ODSVoc.DCAT_CATALOG);
            TupleQueryResult graphs = tupleQuery.evaluate();
            try {
                graphList = new ArrayList<Resource>();
                while (graphs.hasNext()) {
                    URI graph = new URIImpl(graphs.next().getBinding("g").getValue().stringValue());
                    graphList.add(graph);
                }
            }
            finally {
                graphs.close();
            }
        } catch (QueryEvaluationException e) {
            e.printStackTrace();
        } catch (MalformedQueryException e) {
            e.printStackTrace();
        } finally {
            con.close();
        }
       return graphList;
    }
}
