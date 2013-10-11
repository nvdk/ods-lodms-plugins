package com.tenforce.lodmds.extractors;

import at.punkt.lodms.integration.ConfigDialog;
import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Form;
import com.vaadin.ui.TextField;
import com.vaadin.ui.TwinColSelect;
import com.vaadin.ui.VerticalLayout;

import java.util.Collection;

public class CkanExtractorConfigDialog extends VerticalLayout implements ConfigDialog {
    private final CkanExtractorConfig config;
    private final Form form = new Form();
    private BeanItemContainer<String> availablePackages;

    public CkanExtractorConfigDialog(CkanExtractorConfig config) {
        this.config = config;
        availablePackages = new BeanItemContainer(String.class);
        form.setFormFieldFactory(new CkanExtractFieldFactory());
        form.setItemDataSource(new BeanItem<CkanExtractorConfig>(this.config));
        form.setVisibleItemProperties(new String[]{"baseUri", "publisher", "title", "description", "license", "predicatePrefix", "subjectPrefix", "ignoredKeys", "allDatasets", "packageIds"});

        final TextField uriField = (TextField) form.getField("baseUri");
        final TextField predicateField = (TextField) form.getField("predicatePrefix");
        final TextField subjectField = (TextField) form.getField("subjectPrefix");
        final TwinColSelect datasetSelector = (TwinColSelect) form.getField("packageIds");

        datasetSelector.setVisible(!config.getAllDatasets());
        datasetSelector.setContainerDataSource(availablePackages);

        uriField.addListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                String uri = (String) uriField.getValue();
                if (!uri.endsWith("/")) {
                    uriField.setValue(uri + '/');
                    return;
                }
                predicateField.setValue(uri + "predicate/");
                subjectField.setValue(uri + "dataset/");
            }
        });
        form.getField("allDatasets").addListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                if (Boolean.FALSE.equals(event.getProperty().getValue())) {
                    String uri = (String) uriField.getValue();
                    datasetSelector.setValue(null);
                    addPackageIdsToSelect(CkanDataSetList.getPackageIds(uri + "api/3/"));
                    datasetSelector.setVisible(true);
                } else {
                    datasetSelector.setVisible(false);
                }
            }


        });
        addComponent(form);
    }

    private void addPackageIdsToSelect(Collection<String> packageIds) {
        availablePackages.removeAllItems();
        availablePackages.addAll(packageIds);
    }

    @Override
    public CkanExtractorConfig getConfig() {
        form.commit();
        return config;
    }
}
