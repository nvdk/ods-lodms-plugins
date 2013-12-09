package com.tenforce.lodms.transformers;

import at.punkt.lodms.base.TransformerBase;
import at.punkt.lodms.integration.ConfigDialog;
import at.punkt.lodms.integration.ConfigDialogProvider;
import at.punkt.lodms.integration.ConfigurationException;
import at.punkt.lodms.spi.transform.TransformContext;
import at.punkt.lodms.spi.transform.TransformException;
import com.tenforce.lodms.transformers.validator.ValidationExecutor;
import com.tenforce.lodms.transformers.validator.ValidationRule;
import com.vaadin.Application;
import com.vaadin.terminal.ClassResource;
import com.vaadin.terminal.Resource;
import org.apache.log4j.Logger;
import org.openrdf.model.URI;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;

public class OdsValidator extends TransformerBase<OdsValidatorConfig> implements ConfigDialogProvider<OdsValidatorConfig> {
    private Logger logger = Logger.getLogger(this.getClass());

    @Override
    public OdsValidatorConfig newDefaultConfig() {
        return new OdsValidatorConfig();
    }

    @Override
    protected void configureInternal(OdsValidatorConfig config) throws ConfigurationException {

    }

    @Override
    public void transform(Repository repository, URI graph, TransformContext context) throws TransformException {
        try {
            RepositoryConnection con = repository.getConnection();
            try {
                ValidationExecutor executor = new ValidationExecutor(con, graph, config.getLogFilePath());

                for (ValidationRule v : config.getValidationRules()) {
                    executor.validate(v);
                }
                context.getWarnings().addAll(executor.getWarnings());
            } catch (Exception e) {
                logger.error(e.getMessage());
                context.getWarnings().add(e.getMessage());
            } finally {
                con.close();
            }
        } catch (Exception e) {
            throw new TransformException(e.getMessage(), e);
        }
    }

    @Override
    public String getName() {
        return "ODS Validator";
    }

    @Override
    public String getDescription() {
        return "Verifies if triples in the pipeline follow the DCAT-AP.";
    }

    @Override
    public Resource getIcon(Application application) {
        return new ClassResource("/ods.png", application);
    }

    @Override
    public String asString() {
        return this.getName();
    }

    @Override
    public ConfigDialog getConfigDialog(OdsValidatorConfig config) {
        return new OdsValidatorDialog(config);
    }
}
