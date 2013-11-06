package com.tenforce.lodms.extractors;

import at.punkt.lodms.integration.ConfigDialog;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.validator.AbstractStringValidator;
import com.vaadin.ui.Component;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import java.net.URL;

public class ODSCSVExtractorDialog extends VerticalLayout implements ConfigDialog {
    private final ODSCSVExtractorConfig config;
    private final Form form = new Form();

    public ODSCSVExtractorDialog(ODSCSVExtractorConfig config) {
        this.config = config;
        form.setFormFieldFactory(new DefaultFieldFactory() {
            @Override
            public Field createField(Item item, Object propertyId, Component uiContext) {
                TextField f = new TextField();
                f.setCaption(createCaptionByPropertyId(propertyId));
                f.setImmediate(true);
                f.setWidth(350, VerticalLayout.UNITS_PIXELS);
                f.addValidator(new AbstractStringValidator(null) {
                    @Override
                    protected boolean isValidString(String value) {
                        if (value.isEmpty())
                            return true;
                        try {
                            URL u = new URL(value);
                            return true;
                        } catch (Exception ex) {
                            setErrorMessage("Invalid Url: " + ex.getMessage());
                            return false;
                        }
                    }
                });
                return f;
            }
        });
        form.setVisibleItemProperties(new String[]{"catalogCsv", "recordCsv", "datasetCsv", "distributionCsv", "agentCsv", "licenseCsv"});
        form.setItemDataSource(new BeanItem<ODSCSVExtractorConfig>(this.config));
        addComponent(form);
    }

    @Override
    public Object getConfig() {
        form.commit();
        return config;
    }
}
