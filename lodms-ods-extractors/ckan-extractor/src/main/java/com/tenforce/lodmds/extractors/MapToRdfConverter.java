package com.tenforce.lodmds.extractors;

import org.apache.log4j.Logger;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;

import java.util.*;
import java.util.concurrent.Callable;

public class MapToRdfConverter implements Callable<List<Statement>> {
    private Map<String, Object> map;
    private String predicatePrefix = "http://odp.tenforce.com/statement";
    private final List<Statement> statements = new ArrayList<Statement>();
    private final ValueFactory valueFactory = ValueFactoryImpl.getInstance();
    private List<String> ignoredKeys = new ArrayList<String>();
    private String subjectPrefix;
    protected Logger logger = org.apache.log4j.Logger.getLogger(MapToRdfConverter.class);

    public MapToRdfConverter(String subjectPrefix, String predicatePrefix, List<String> ignoredKeys) {
        this.subjectPrefix = subjectPrefix;
        this.predicatePrefix = predicatePrefix;
        this.ignoredKeys = ignoredKeys;
    }

    public void setSubjectPrefix(String subjectPrefix) {
        this.subjectPrefix = subjectPrefix;
    }

    public void setMap(Map<String, Object> map) {
        this.map = map;
    }

    public List<Statement> convert() {
        String provenancePrefix = "http://data.opendatasupport.eu/ontology/harmonisation.owl#";
        URI contentHash = valueFactory.createURI(provenancePrefix + "content_hash");
        Statement s = valueFactory.createStatement(valueFactory.createURI(subjectPrefix), contentHash, valueFactory.createLiteral(map.hashCode()));
        statements.add(s);
        convertHashmapToStatements(subjectPrefix, map);
        return Collections.unmodifiableList(statements);
    }

    /*
    * Convert a list to a set of statements
    * @param String subject
    * @param String key
    * @param List  list
    */
    private void convertListToStatements(String subjectStr, String key, List list) {
        // NOTE: currently doesn't support nested list
        // NOTE: this will not store empty arrays
        URI subject = valueFactory.createURI(subjectStr);
        URI predicate = valueFactory.createURI(generateKey(predicatePrefix, key));

        Integer i = 0;
        for (Object o : list) {
            String newSubj = generateKey(generateKey(subjectStr, key), i.toString());
            if (o instanceof String) {
                storeValue(subject, predicate, (String) o);
            } else if (o instanceof Integer) {
                storeValue(subject, predicate, (Integer) o);
            } else if (o instanceof Boolean) {
                storeValue(subject, predicate, (Boolean) o);
            } else if (o instanceof HashMap) {
                statements.add(valueFactory.createStatement(subject, predicate, valueFactory.createURI(newSubj)));
                convertHashmapToStatements(newSubj, (Map<String, Object>) o);
            }
            i++;
        }
    }

    private void convertHashmapToStatements(String subject, Map<String, Object> map) {
        for (Map.Entry<String, Object> pairs : map.entrySet()) {
            convertPairToStatements(subject, pairs.getKey(), pairs.getValue());
        }
    }

    /*
    * Convert a key value pair to set of statements
    * @params String subject
    * @param String key
    * @param Object value
    */
    private void convertPairToStatements(String subjectStr, String key, Object value) {
        URI subject = valueFactory.createURI(subjectStr);
        URI predicate = valueFactory.createURI(predicatePrefix, key);

        // don't do anything for ignored keys
        if (ignoredKeys.contains(key))
            return;

        // NOTE: not storing null values
        if (value == null)
            return;


        if (value instanceof String) {
            String v = (String) value;
            if (!v.isEmpty())
                storeValue(subject, predicate, v);
        } else if (value instanceof Boolean) {
            storeValue(subject, predicate, (Boolean) value);
        } else if (value instanceof Integer) {
            storeValue(subject, predicate, (Integer) value);
        } else if (value instanceof Map.Entry) {
            Map.Entry<String, Object> keyValuePair = (Map.Entry<String, Object>) value;
            String newSubj = generateKey(subjectStr, key);
            statements.add(valueFactory.createStatement(subject, predicate, valueFactory.createURI(newSubj)));
            convertPairToStatements(newSubj, keyValuePair.getKey(), keyValuePair.getValue());
        } else if (value instanceof List) {
            convertListToStatements(subjectStr, key, (List) value);
        } else if (value instanceof HashMap) {
            convertHashmapToStatements(generateKey(subjectStr, key), (Map) value);
        }
        // oops, encountered a value format we do not support yet
        else {
            logger.warn("unsupported class: " + value.getClass() + " for value " + value.toString());
        }
    }

    private void storeValue(URI subject, URI predicate, Integer value) {
        statements.add(valueFactory.createStatement(subject, predicate, valueFactory.createLiteral(value)));
    }

    private void storeValue(URI subject, URI predicate, String value) {
        statements.add(valueFactory.createStatement(subject, predicate, valueFactory.createLiteral(value)));

    }

    private void storeValue(URI subject, URI predicate, Boolean value) {
        statements.add(valueFactory.createStatement(subject, predicate, valueFactory.createLiteral(value)));
    }

    /*
    * Generate a unique identifier for this attribute in rdf
    * @param String parentKey
    * @param String subKey
    *
    * @return String attributeURI
    */
    private static String generateKey(String parentKey, String subKey) {
        if (parentKey.isEmpty())
            return subKey;
        return parentKey.endsWith("/") ? parentKey + subKey : parentKey + '/' + subKey;
    }

    @Override
    public List<Statement> call() throws Exception {
        return convert();
    }
}
