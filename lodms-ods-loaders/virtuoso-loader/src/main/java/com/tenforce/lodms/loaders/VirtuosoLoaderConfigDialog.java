package com.tenforce.lodms.loaders;

import at.punkt.lodms.integration.ConfigDialog;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Component;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.Select;
import com.vaadin.ui.VerticalLayout;

public class VirtuosoLoaderConfigDialog extends VerticalLayout implements ConfigDialog {
    private final VirtuosoLoaderConfig config;
    Form form = new Form();

    public VirtuosoLoaderConfigDialog(VirtuosoLoaderConfig configuration) {
        config = configuration;
        form.setFormFieldFactory(new DefaultFieldFactory() {
            @Override
            public Field createField(Item item, Object propertyId, Component uiContext) {
                if ("graph".equals(propertyId)) {
                    Field f = super.createField(item, propertyId, uiContext);
                    f.setVisible(config.getGraphSource().equals(VirtuosoLoaderConfig.GRAPHSOURCE_CUSTOM));

                } else if ("graphSource".equals(propertyId)) {
                    Select selector = new Select("Graph");
                    selector.addItem(VirtuosoLoaderConfig.GRAPHSOURCE_CKANURI);
                    selector.addItem(VirtuosoLoaderConfig.GRAPHSOURCE_ODSURI);
                    selector.addItem(VirtuosoLoaderConfig.GRAPHSOURCE_CUSTOM);
                    selector.addListener(new Property.ValueChangeListener() {
                        @Override
                        public void valueChange(Property.ValueChangeEvent event) {
                            Field graph = form.getField("graph");
                            if (event.getProperty().getValue() == null || graph == null)
                                return;
                            else {
                                boolean isCustom = event.getProperty().getValue().equals(VirtuosoLoaderConfig.GRAPHSOURCE_CUSTOM);
                                graph.setVisible(isCustom);
                                graph.setRequired(isCustom);
                            }
                        }
                    });
                    selector.setImmediate(true);
                    return selector;
                }
                return super.createField(item, propertyId, uiContext);
            }
        });
        form.setItemDataSource(new BeanItem<VirtuosoLoaderConfig>(this.config));
        form.setVisibleItemProperties(new String[]{"host", "port", "userName", "password", "versioned", "graphSource", "graph"});
        addComponent(form);
    }

    @Override
    public VirtuosoLoaderConfig getConfig() {
        form.commit();
        return config;
    }
}
