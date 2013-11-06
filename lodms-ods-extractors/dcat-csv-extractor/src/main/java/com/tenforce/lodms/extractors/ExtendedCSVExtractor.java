/*
 * Copyright 2008-2010 Digital Enterprise Research Institute (DERI)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tenforce.lodms.extractors;

import com.tenforce.lodms.ODSVoc;
import org.apache.commons.csv.CSVParser;
import org.deri.any23.extractor.ExtractionContext;
import org.deri.any23.extractor.ExtractionException;
import org.deri.any23.extractor.ExtractionParameters;
import org.deri.any23.extractor.ExtractionResult;
import org.deri.any23.extractor.Extractor;
import org.deri.any23.extractor.ExtractorDescription;
import org.deri.any23.extractor.ExtractorFactory;
import org.deri.any23.extractor.SimpleExtractorFactory;
import org.deri.any23.extractor.csv.CSVReaderBuilder;
import org.deri.any23.rdf.RDFUtils;
import org.deri.any23.vocab.CSV;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.XMLSchema;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * This extractor produces <i>RDF</i> from a <i>CSV file</i> .
 * It automatically detects fields <i>delimiter</i>. If not able uses
 * the one provided in the <i>Any23</i> configuration.
 * <p/>
 * based on the deri CSV Extractor
 */
public class ExtendedCSVExtractor implements Extractor.ContentExtractor {

    private CSVParser csvParser;

    private CSVHeader[] headers;

    private CSV csv = CSV.getInstance();

    public final static ExtractorFactory<ExtendedCSVExtractor> factory =
            SimpleExtractorFactory.create(
                    "csv",
                    null,
                    Arrays.asList(
                            "text/csv;q=0.1"
                    ),
                    "example-csv.csv",
                    ExtendedCSVExtractor.class
            );

    /**
     * {@inheritDoc}
     */
    public void setStopAtFirstError(boolean f) {
    }

    /**
     * {@inheritDoc}
     */
    public void run(
            ExtractionParameters extractionParameters,
            ExtractionContext extractionContext,
            InputStream in
            , ExtractionResult out
    ) throws IOException, ExtractionException {
        final URI documentURI = extractionContext.getDocumentURI();

        // build the parser
        csvParser = CSVReaderBuilder.build(in);

        // get the header and generate the URIs for column names
        String[] header = csvParser.getLine();
        headers = processHeader(header, documentURI);

        String[] nextLine;

        while ((nextLine = csvParser.getLine()) != null) {
            // ignore rows not starting with a URI
            if (RDFUtils.isAbsoluteURI(nextLine[0])) {
                produceRowStatements(nextLine, out);
            }
        }
    }

    /**
     * @param number
     * @return
     */
    private boolean isNumber(String number) {
        try {
            Double.valueOf(number);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * It process the first row of the file, returning a list of {@link URI}s representing
     * the properties for each column. If a value of the header is an absolute <i>URI</i>
     * then it leave it as is. Otherwise the {@link CSV} vocabulary is used.
     *
     * @param header
     * @return an array of {@link URI}s identifying the column names.
     */
    private CSVHeader[] processHeader(String[] header, URI documentURI) {
        CSVHeader[] result = new CSVHeader[header.length];
        int index = 0;
        for (String h : header) {
            String candidate = h.trim();
            if (RDFUtils.isAbsoluteURI(candidate)) {
                result[index] = new CSVHeader(new URIImpl(candidate));
            } else if (CSVLabelMap.HEADERS.containsKey(candidate)) {
                result[index] = new CSVHeader(CSVLabelMap.HEADERS.get(candidate));
            } else if (candidate.matches("description_([a-z]{2})")) {
                result[index] = new CSVHeader(ODSVoc.DCT_DESCRIPTION, candidate.substring(candidate.length() - 2));
            } else if (candidate.matches("title_([a-z]{2})")) {
                result[index] = new CSVHeader(ODSVoc.DCT_TITLE, candidate.substring(candidate.length() - 2));
            } else if (candidate.matches("keyword/tag_([a-z]{2})")) {
                result[index] = new CSVHeader(ODSVoc.DCAT_KEYWORD, candidate.substring(candidate.length() - 2));
            } else {
                result[index] = new CSVHeader(normalize(candidate, documentURI));
            }
            index++;
        }
        return result;
    }

    private URI normalize(String toBeNormalized, URI documentURI) {
        String candidate = toBeNormalized;
        candidate = candidate.trim().toLowerCase().replace("?", "").replace("&", "");
        String[] tokens = candidate.split(" ");
        candidate = tokens[0];
        for (int i = 1; i < tokens.length; i++) {
            String firstChar = ("" + tokens[i].charAt(0)).toUpperCase();
            candidate += firstChar + tokens[i].substring(1);
        }
        return new URIImpl(documentURI.toString() + candidate);
    }

    /**
     * It writes on the provided {@link ExtractionResult}, the </>RDF statements</>
     * representing the row <i>cell</i>. If a  row <i>cell</i> is an absolute <i>URI</i>
     * then an object property is written, literal otherwise.
     *
     * @param values
     * @param out
     */
    private void produceRowStatements(
            String[] values,
            ExtractionResult out
    ) {
        URI rowSubject = new URIImpl(values[0]);
        out.writeTriple(rowSubject, RDF.TYPE, headers[0].getUri());
        int index = 0;
        for (String cell : values) {
            if (index >= headers.length) {
                // there are some row cells that don't have an associated column name
                break;
            }
            if (cell.equals("") || index == 0) {
                index++;
                continue;
            }
            Value object;
            if (headers[index].hasLanguage()) {
                object = ValueFactoryImpl.getInstance().createLiteral(cell, headers[index].getLanguage());
            } else {
                object = getObjectFromCell(cell);
            }
            URI predicate = headers[index].getUri();
            out.writeTriple(rowSubject, predicate, object);
            index++;
        }
    }

    private Value getObjectFromCell(String cell) {
        Value object;
        cell = cell.trim();
        if (RDFUtils.isAbsoluteURI(cell)) {
            object = new URIImpl(cell);
        } else {
            URI datatype = XMLSchema.STRING;
            if (isNumber(cell)) {
                datatype = XMLSchema.INTEGER;
            }
            object = new LiteralImpl(cell, datatype);
        }
        return object;
    }

    /**
     * {@inheritDoc}
     */
    public ExtractorDescription getDescription() {
        return factory;
    }
}
