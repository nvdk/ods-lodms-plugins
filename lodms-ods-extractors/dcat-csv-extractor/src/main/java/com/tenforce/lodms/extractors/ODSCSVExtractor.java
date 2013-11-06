package com.tenforce.lodms.extractors;

import at.punkt.lodms.integration.ConfigDialog;
import at.punkt.lodms.integration.ConfigDialogProvider;
import at.punkt.lodms.integration.ConfigurableBase;
import at.punkt.lodms.integration.ConfigurationException;
import at.punkt.lodms.integration.UIComponent;
import at.punkt.lodms.spi.extract.ExtractContext;
import at.punkt.lodms.spi.extract.ExtractException;
import at.punkt.lodms.spi.extract.Extractor;
import com.vaadin.Application;
import com.vaadin.terminal.ClassResource;
import com.vaadin.terminal.Resource;
import org.deri.any23.extractor.ExtractionContext;
import org.deri.any23.extractor.ExtractionParameters;
import org.deri.any23.extractor.ExtractionResult;
import org.deri.any23.extractor.ExtractionResultImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.rio.RDFHandler;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;

public class ODSCSVExtractor extends ConfigurableBase<ODSCSVExtractorConfig> implements Extractor, UIComponent, ConfigDialogProvider<ODSCSVExtractorConfig> {
    @Override
    public ODSCSVExtractorConfig newDefaultConfig() {
        return new ODSCSVExtractorConfig();
    }

    @Override
    protected void configureInternal(ODSCSVExtractorConfig odscsvExtractorConfig) throws ConfigurationException {

    }

    @Override
    public void extract(RDFHandler rdfHandler, ExtractContext extractContext) throws ExtractException {
        try {

            for (String csv : config.definedCsvs()) {
                extractCSVData(rdfHandler, new URL(csv));
            }
        } catch (Exception e) {
            throw new ExtractException(e.getMessage(), e);
        }
    }

    @Override
    public String getName() {
        return "DCAT CSV Extractor";
    }

    @Override
    public String getDescription() {
        return "Convert and import CSV's in DCAT format";
    }

    @Override
    public Resource getIcon(Application application) {
        return new ClassResource("/ods.png", application);
    }

    @Override
    public String asString() {
        return getName() + config.getCatalogCsv();
    }

    private void extractCSVData(RDFHandler handler, URL csvURL) throws ExtractException {
        try {
            ExtendedCSVExtractor csvExtractor = new ExtendedCSVExtractor();
            ExtractionParameters params = new ExtractionParameters(ExtractionParameters.ValidationMode.None);
            ExtractionContext ctx = new ExtractionContext("CSV", new URIImpl("http://data.opendatasupport.eu/csv/"));
            ExtractionResult out = new ExtractionResultImpl(ctx, csvExtractor, new TripleHandlerBridge(handler));
            InputStream in = new BufferedInputStream(csvURL.openStream());
            try {
                csvExtractor.run(params, ctx, in, out);
            } finally {
                in.close();
            }
        } catch (Exception ex1) {
            throw new ExtractException(ex1);
        }

    }

    @Override
    public ConfigDialog getConfigDialog(ODSCSVExtractorConfig odscsvExtractorConfig) {
        return new ODSCSVExtractorDialog(odscsvExtractorConfig);
    }
}
