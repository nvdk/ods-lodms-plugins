package com.tenforce.lodms.transformers.validator;

import org.openrdf.model.URI;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryConnection;

import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class ValidationExecutor {
    private RepositoryConnection connection;
    private URI context;
    private List<String> warnings = new ArrayList<String>();
    private Logger validationLogger = Logger.getLogger("ValidationExecutor");

    public ValidationExecutor(RepositoryConnection connection, URI context, String logFilePath) {
        this.connection = connection;
        this.context = context;
        FileHandler fh;

        try {
            fh = new FileHandler(logFilePath);
            validationLogger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
        } catch (SecurityException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        } catch (IOException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
        validationLogger.info("configured for context: " + context.stringValue());
    }

    public void validate(ValidationRule rule) throws TransformerException {
        try {
            String queryString = String.format("define input:default-graph-uri <%s> %s", context.stringValue(), rule.getSparqlQuery());
            TupleQuery query = connection.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
            TupleQueryResult result = query.evaluate();
            if (result.getBindingNames().containsAll(Arrays.asList("s", "p", "o"))) {
                int i = 0;
                while (result.hasNext()) {
                    createWarning(rule.getSeverity(), rule.getMessage(), result.next());
                    i++;
                }
                warnings.add("rule " + rule.getDescription() + ": " + i + " failures");
            } else {
                warnings.add("rule " + rule.getDescription() + " does not bind all required names ?s, ?p,?o");
            }
        } catch (Exception e) {
            throw new TransformerException(e.getMessage(), e);
        }


    }

    public List<String> getWarnings() {
        return warnings;
    }

    private void createWarning(String severity, String message, BindingSet row) {
        String s = row.getValue("s").stringValue();
        String p = row.getValue("p").stringValue();
        String o = row.getValue("o").stringValue();
        String warning = "%s: %s {%s %s %s}";
        validationLogger.warning(String.format(warning, severity, message, s, p, o));
    }
}
