/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tenforce.lodms.extractors;

import org.deri.any23.extractor.ExtractionContext;
import org.deri.any23.writer.TripleHandler;
import org.deri.any23.writer.TripleHandlerException;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.ContextStatementImpl;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.rio.RDFHandler;
import org.openrdf.rio.RDFHandlerException;

import java.lang.Override;
import java.lang.String;

/**
 * @author Alex Kreiser
 */
public class TripleHandlerBridge implements TripleHandler {

    private final RDFHandler handler;

    public TripleHandlerBridge(RDFHandler handler) {
        this.handler = handler;
    }

    @Override
    public void startDocument(URI documentURI) throws TripleHandlerException {
    }

    @Override
    public void openContext(ExtractionContext context) throws TripleHandlerException {
        try {
            handler.startRDF();
        } catch (RDFHandlerException ex) {
            throw new TripleHandlerException(ex.getMessage(), ex);
        }
    }

    @Override
    public void receiveTriple(Resource s, URI p, Value o, URI g, ExtractionContext context) throws TripleHandlerException {
        try {
            if (s.toString().endsWith("row/0"))
                return;
            if (g != null) {
                handler.handleStatement(new ContextStatementImpl(s, p, o, g));
            } else {
                handler.handleStatement(new StatementImpl(s, p, o));
            }
        } catch (RDFHandlerException ex) {
            throw new TripleHandlerException(ex.getMessage(), ex);
        }
    }

    @Override
    public void receiveNamespace(String prefix, String uri, ExtractionContext context) throws TripleHandlerException {
        try {
            handler.handleNamespace(prefix, uri);
        } catch (RDFHandlerException ex) {
            throw new TripleHandlerException(ex.getMessage(), ex);
        }
    }

    @Override
    public void closeContext(ExtractionContext context) throws TripleHandlerException {
        try {
            handler.endRDF();
        } catch (RDFHandlerException ex) {
            throw new TripleHandlerException(ex.getMessage(), ex);
        }
    }

    @Override
    public void endDocument(URI documentURI) throws TripleHandlerException {
    }

    @Override
    public void setContentLength(long contentLength) {
    }

    @Override
    public void close() throws TripleHandlerException {
    }
}