package com.tenforce.lodms.extractors;

import com.tenforce.lodms.ODSVoc;
import org.apache.log4j.Logger;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.rio.RDFHandler;
import org.openrdf.rio.RDFHandlerException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapToRdfConverter {
    private String predicatePrefix;
    private final ValueFactory valueFactory = ValueFactoryImpl.getInstance();
    private List<String> ignoredKeys;
    private RDFHandler handler;
    protected Logger logger = org.apache.log4j.Logger.getLogger(MapToRdfConverter.class);

    public MapToRdfConverter(String predicatePrefix, List<String> ignoredKeys, RDFHandler handler) {
        this.predicatePrefix = predicatePrefix;
        this.ignoredKeys = ignoredKeys;
        this.handler = handler;
    }


    public void convert(Map<String, Object> map, String subject) throws RDFHandlerException {
        Statement s = valueFactory.createStatement(valueFactory.createURI(subject), ODSVoc.ODS_CONTENT_HASH, valueFactory.createLiteral(map.hashCode()));
        handler.handleStatement(s);
        convertHashmapToStatements(subject, map);
    }

    /*
    * Convert a list to a set of statements
    * @param String subject
    * @param String key
    * @param List  list
    */
    private void convertListToStatements(String subjectStr, String key, List list) throws RDFHandlerException {
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
                handler.handleStatement(valueFactory.createStatement(subject, predicate, valueFactory.createURI(newSubj)));
                convertHashmapToStatements(newSubj, (Map<String, Object>) o);
            }
            i++;
        }
    }

    private void convertHashmapToStatements(String subject, Map<String, Object> map) throws RDFHandlerException {
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
    private void convertPairToStatements(String subjectStr, String key, Object value) throws RDFHandlerException {
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
            handler.handleStatement(valueFactory.createStatement(subject, predicate, valueFactory.createURI(newSubj)));
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

    private void storeValue(URI subject, URI predicate, Integer value) throws RDFHandlerException {
        handler.handleStatement(valueFactory.createStatement(subject, predicate, valueFactory.createLiteral(value)));
    }

    private void storeValue(URI subject, URI predicate, String value) throws RDFHandlerException {
        handler.handleStatement(valueFactory.createStatement(subject, predicate, valueFactory.createLiteral(value)));

    }

    private void storeValue(URI subject, URI predicate, Boolean value) throws RDFHandlerException {
        handler.handleStatement(valueFactory.createStatement(subject, predicate, valueFactory.createLiteral(value)));
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
}
