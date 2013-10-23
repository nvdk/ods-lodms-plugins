package com.tenforce.lodms.extractors;

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
import java.util.Arrays;

public class RDFExtractor extends ConfigurableBase<RDFExtractorConfig> implements Extractor, UIComponent, ConfigBeanProvider<RDFExtractorConfig> {
    String rdfLocation;
    public static String[] validExtensions = {"rdf", "xml", "owl", "rdfs", "ttl", "n3"};

    public RDFExtractorConfig newDefaultConfig() {
        return new RDFExtractorConfig();
    }

    @Override
    public void extract(RDFHandler handler, ExtractContext context) throws ExtractException {
        try {
            String extension = rdfLocation.substring(rdfLocation.lastIndexOf('.') + 1);
            if (!isValidExtension(extension)) {
                throw new ExtractException("invalid RDF format, currently only rdf and turtle are supported");
            }

            URL documentUrl = new URL(rdfLocation);
            RDFParser rdfParser = Rio.createParser(RDFFormat.forFileName(documentUrl.getFile()));
            rdfParser.setRDFHandler(handler);
            InputStream inputStream = documentUrl.openStream();
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

    private boolean isValidExtension(String extension) {
        return Arrays.asList(validExtensions).contains(extension);
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
        return new ClassResource("/rdffile.gif", application);
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
