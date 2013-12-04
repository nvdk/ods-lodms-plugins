package com.tenforce.lodms.transformers;

import at.punkt.lodms.integration.ConfigDialog;
import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import org.openrdf.model.URI;

import java.util.ArrayList;
import java.util.List;

public class ODSValueMapperDialog extends HorizontalLayout implements ConfigDialog {
    private  ODSValueMapperConfig config;
    private final Table mappingTable = new Table("Value Mapping");
    private final PredicateSelector predicateSelector = new PredicateSelector("DCAT Property");
    private final Button addMapping = new Button("Add row");
    private final Button clearMapping = new Button("Clear table");
    private final Button showLoader = new Button("Load values from store");
    private BeanItemContainer<Mapping> mappings = new BeanItemContainer(Mapping.class);
    private ValueLoaderDialog loaderDialog;



    public ODSValueMapperDialog(ODSValueMapperConfig oldConfig) {
        config = oldConfig;
        mappings.addAll(config.getMappings());
        loaderDialog = new ValueLoaderDialog(mappings);
        configureMappingTable();
        VerticalLayout leftSide = new VerticalLayout();
        configurePredicateSelector();
        leftSide.addComponent(predicateSelector);
        leftSide.addComponent(mappingTable);
        leftSide.addComponent(getBottomBar());
        leftSide.setWidth(90, UNITS_PERCENTAGE);
        addComponent(leftSide);
    }

    @Override
    public ODSValueMapperConfig getConfig() {
        config.setMappedPredicate(getMappedPredicate());
        List maps = new ArrayList<Mapping>();
        maps.addAll(mappings.getItemIds());
        config.setMappings(maps);
        return config;
    }

    private URI getPredicate() {
        return getMappedPredicate().getDcatProp();
    }

    private MappedPredicate getMappedPredicate() {
        return (MappedPredicate) predicateSelector.getValue();
    }
    private void configureMappingTable() {
        mappingTable.setWidth(100, UNITS_PERCENTAGE);
        mappingTable.setHeight(300, UNITS_PIXELS);
        mappingTable.setContainerDataSource(mappings);
        mappingTable.setVisibleColumns(new String[]{"originalValue", "harmonizedValue"});
        mappingTable.addGeneratedColumn("delete", new Table.ColumnGenerator() {

            @Override
            public Component generateCell(Table source, final Object itemId, Object columnId) {
                Button delete = new Button("X");
                delete.addListener(new Button.ClickListener() {

                    @Override
                    public void buttonClick(Button.ClickEvent event) {
                        mappings.removeItem(itemId);
                    }
                });
                return delete;
            }
        });
        mappingTable.setEditable(true);
        mappingTable.setColumnWidth("delete", 50);
        mappingTable.setSelectable(true);
        mappingTable.setImmediate(true);
    }


    private HorizontalLayout getBottomBar() {
        HorizontalLayout bottom = new HorizontalLayout();
        bottom.setWidth(100, UNITS_PERCENTAGE);
        bottom.setHeight(100, UNITS_PERCENTAGE);
        configureAddMappingButton();
        bottom.addComponent(addMapping);
        configureClearMappingButton();
        bottom.addComponent(clearMapping);
        configureShowLoaderButton();
        bottom.addComponent(showLoader);

        return bottom;
    }

    private void configureClearMappingButton() {
        clearMapping.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                mappings.removeAllItems();
            }
        });
    }

    private void configureShowLoaderButton() {
        showLoader.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                if (null == loaderDialog.getParent()) {
                    loaderDialog.setPredicate(getPredicate());
                    addComponent(loaderDialog);
                }
            }
        });
    }

    private void configureAddMappingButton() {

        addMapping.addListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                mappings.addItem(new Mapping());
            }
        });
    }

    private void configurePredicateSelector() {
      if (config.getMappedPredicate() != null)
        predicateSelector.selectPredicate(config.getMappedPredicate().getDcatProp());
        predicateSelector.addListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                loaderDialog.setPredicate(getPredicate());
            }
        });
    }
}