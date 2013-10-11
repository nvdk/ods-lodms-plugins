package com.tenforce.lodms.transformers;

import at.punkt.lodms.integration.ConfigDialog;
import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.terminal.UserError;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;
import org.openrdf.query.parser.ParsedUpdate;
import org.openrdf.query.parser.sparql.SPARQLParser;

public class MultipleSparqlUpdateDialog extends VerticalLayout implements ConfigDialog {
    private final MultipleSparqlUpdateConfig config;
    private final Table resourcesTable = new Table("SPARQL Queries");
    private final TextArea sparqlQuery = new TextArea();
    private final Button addButton = new Button("Add");
    private String currentQueryId;

    private BeanItemContainer<String> queries;

    public MultipleSparqlUpdateDialog(MultipleSparqlUpdateConfig oldConfig) {
        config = oldConfig;
        queries = new BeanItemContainer<String>(String.class);
        queries.addAll(config.getQueries());
        setupResourceTable();
        addComponent(resourcesTable);

        Label label = new Label("SPARQL Update Query");
        addComponent(label);

        HorizontalLayout addLayout = new HorizontalLayout();
        addLayout.setWidth(100, UNITS_PERCENTAGE);
        addLayout.setHeight(100, UNITS_PERCENTAGE);
        sparqlQuery.setRows(15);
        sparqlQuery.setColumns(60);
        addLayout.addComponent(sparqlQuery);
        addButton.addListener( new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                String query = sparqlQuery.getValue().toString();
                if (isValidQuery(query)) {
                    queries.addBean(query);
                    if (currentQueryId != null) {
                        queries.removeItem(currentQueryId);
                        currentQueryId = null;
                    }
                    sparqlQuery.setValue("");
                } else {
                    sparqlQuery.setComponentError(new UserError("invalid SPARQL Query"));
                }
            }
        });
        addLayout.addComponent(addButton);
        addComponent(addLayout);
    }

    private void setupResourceTable() {
        resourcesTable.setWidth(100, UNITS_PERCENTAGE);
        resourcesTable.setHeight(300, UNITS_PIXELS);
        resourcesTable.setContainerDataSource(queries);
        resourcesTable.setVisibleColumns(new String[]{});
        resourcesTable.addGeneratedColumn("query", new Table.ColumnGenerator() {

            @Override
            public Component generateCell(Table source, Object itemId, Object columnId) {
                return new Label(itemId.toString());
            }
        });
        resourcesTable.addGeneratedColumn("delete", new Table.ColumnGenerator() {

            @Override
            public Component generateCell(Table source, final Object itemId, Object columnId) {
                Button delete = new Button("X");
                delete.addListener(new Button.ClickListener() {

                    @Override
                    public void buttonClick(Button.ClickEvent event) {
                        queries.removeItem(itemId);
                    }
                });
                return delete;
            }
        });
        resourcesTable.setColumnWidth("delete", 50);
        resourcesTable.setSelectable(true);
        resourcesTable.setImmediate(true);
        resourcesTable.addListener(new Property.ValueChangeListener() {
            public void valueChange(Property.ValueChangeEvent event) {
                Object o = resourcesTable.getValue();
                if (null == o) {
                    sparqlQuery.setValue("");
                    addButton.setCaption("Add");
                    currentQueryId = null;
                }
                else {
                    String query = queries.getItem(o.toString()).getBean();
                    sparqlQuery.setValue(query);
                    addButton.setCaption("Replace");
                    currentQueryId = o.toString();
                }
            }
        });
    }

    @Override
    public MultipleSparqlUpdateConfig getConfig() {
        config.getQueries().clear();
        config.getQueries().addAll(queries.getItemIds());
        return config;
    }

    private boolean isValidQuery(String value) {
        SPARQLParser parser = new SPARQLParser();
        try {
            ParsedUpdate parsed = parser.parseUpdate(value, null);
        } catch (Exception ex) {
            return false;
        }
        return true;
    }
}
