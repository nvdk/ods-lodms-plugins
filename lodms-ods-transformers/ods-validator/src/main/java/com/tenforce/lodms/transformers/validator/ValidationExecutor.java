package com.tenforce.lodms.transformers.validator;

import org.openrdf.model.URI;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryConnection;

import javax.xml.transform.TransformerException;
import java.util.Arrays;
import java.util.List;

public class ValidationExecutor {
    private RepositoryConnection connection;
    private URI context;
    private List<String> warnings;

    public ValidationExecutor(RepositoryConnection connection, URI context,List<String> warnings) {
        this.connection = connection;
        this.context = context;
        this.warnings = warnings;
    }

    public void validate(ValidationRule rule) throws TransformerException {
        try {
            String queryString = String.format("define input:default-graph-uri <%s> %s",context.stringValue(),rule.getSparqlQuery());
            TupleQuery query = connection.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
            TupleQueryResult result = query.evaluate();
            if (result.getBindingNames().containsAll(Arrays.asList("s","p","o"))) {
                while (result.hasNext()) {
                    createWarning(rule.getSeverity(),rule.getMessage(),result.next());
                }
            }
            else {
                warnings.add("rule " + rule.getDescription() + " does not bind all required names ?s, ?p,?o");
            }
        } catch (Exception e) {
            throw new TransformerException(e.getMessage(),e);
        }


    }

    private void createWarning(String severity, String message,BindingSet row) {
        String s = row.getValue("s").stringValue();
        String p = row.getValue("p").stringValue();
        String o = row.getValue("o").stringValue();
        String warning = "%s: %s {%s %s %s}";
        warnings.add(String.format(warning,severity,message,s,p,o));
    }
}
