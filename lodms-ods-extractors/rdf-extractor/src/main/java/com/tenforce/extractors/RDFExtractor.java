package com.tenforce.extractors;

import at.punkt.lodms.integration.ConfigBeanProvider;
import at.punkt.lodms.integration.ConfigurableBase;
import at.punkt.lodms.integration.ConfigurationException;
import at.punkt.lodms.integration.UIComponent;
import at.punkt.lodms.spi.extract.ExtractContext;
import at.punkt.lodms.spi.extract.ExtractException;
import at.punkt.lodms.spi.extract.Extractor;
import com.vaadin.Application;
import com.vaadin.terminal.ClassResource;
import com.vaadin.terminal.Resource;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandler;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.Rio;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class RDFExtractor extends ConfigurableBase<RDFExtractorConfig> implements Extractor, UIComponent, ConfigBeanProvider<RDFExtractorConfig> {
    String rdfLocation;

    public RDFExtractorConfig newDefaultConfig() {
        return new RDFExtractorConfig();
    }

    @Override
    public void extract(RDFHandler handler, ExtractContext context) throws ExtractException {
        try {
            URL documentUrl = new URL(rdfLocation);
            InputStream inputStream = documentUrl.openStream();
            RDFParser rdfParser = Rio.createParser(RDFFormat.RDFXML);
            rdfParser.setRDFHandler(handler);
            rdfParser.parse(inputStream, documentUrl.toString());
        } catch (MalformedURLException e) {
            throw new ExtractException(e.getMessage(), e);
        } catch (IOException e) {
            throw new ExtractException(e.getMessage(), e);
        } catch (RDFHandlerException e) {
            throw new ExtractException(e.getMessage(), e);
        } catch (RDFParseException e) {
            throw new ExtractException(e.getMessage(), e);
        }

    }

    @Override
    public String getName() {
        return "RDF extractor";
    }

    @Override
    public String getDescription() {
        return "Extracts RDF statements from a RDF file.";
    }

    @Override
    public Resource getIcon(Application application) {
        return new ClassResource("/at/punkt/lodms/impl/load/rdffile.gif", application);
    }

    @Override
    public String asString() {
        return getName();
    }

    @Override
    protected void configureInternal(RDFExtractorConfig config) throws ConfigurationException {
        rdfLocation = config.getRdfLocation();
    }
}
