package com.tenforce.lodms.transformers;

import info.aduna.iteration.Iterations;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
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
        } catch (RepositoryException e) {
            throw new IllegalArgumentException(e.getMessage(),e);
        }
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
    public List<Resource> getAvailableGraph() {
        try {
            RepositoryConnection con = repository.getConnection();
            List<Resource> graphList = Collections.emptyList();
            try {
                RepositoryResult<Resource> graphs = con.getContextIDs();
                graphList = Iterations.asList(graphs);
                graphs.close();
            }
            finally {
                con.close();
                return graphList;
            }
        } catch (RepositoryException e) {
            return Collections.emptyList();
        }


    }
}
