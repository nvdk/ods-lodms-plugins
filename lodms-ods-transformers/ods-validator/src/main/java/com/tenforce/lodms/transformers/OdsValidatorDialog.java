package com.tenforce.lodms.transformers;

import at.punkt.lodms.integration.ConfigDialog;
import com.tenforce.lodms.transformers.validator.ValidationRule;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.validator.AbstractStringValidator;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Select;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import org.openrdf.query.parser.ParsedUpdate;
import org.openrdf.query.parser.sparql.SPARQLParser;

import java.util.ArrayList;
import java.util.List;

public class OdsValidatorDialog extends VerticalLayout implements ConfigDialog {
    private final Table rulesTable = new Table("Validation Rules");
    private BeanItemContainer<ValidationRule> rules = new BeanItemContainer(ValidationRule.class);
    private OdsValidatorConfig config;
    private FormLayout ruleEditor = new FormLayout();
    private Form ruleFields = new Form();
    private Button addButton = new Button("add rule");
    private Button removeButton = new Button("remove this rule");

    public OdsValidatorDialog(OdsValidatorConfig oldConfig) {
        config = oldConfig;
        rules.addAll(config.getValidationRules());
        initRulesTable();
        initEditor();
        initAddRemoveButtons();
        initLayout();
    }

    private void initEditor() {
        ruleEditor.addComponent(removeButton);
        ruleFields.setFormFieldFactory(new DefaultFieldFactory() {
            @Override
            public Field createField(Item item, Object propertyId, Component uiContext) {
                if ("sparqlQuery".equals(propertyId)) {
                    TextArea sparqlQuery = new TextArea();
                    sparqlQuery.setCaption(createCaptionByPropertyId(propertyId));
                    sparqlQuery.setImmediate(true);
                    sparqlQuery.setRows(10);
                    sparqlQuery.setColumns(30);
                    sparqlQuery.addValidator(new AbstractStringValidator("invalid sparql query") {
                        @Override
                        protected boolean isValidString(String value) {
                            SPARQLParser parser = new SPARQLParser();
                            try {
                                ParsedUpdate parsed = parser.parseUpdate(value, null);
                            } catch (Exception ex) {
                                return false;
                            }
                            return true;
                        }
                    });
                    return sparqlQuery;
                }
                if ("severity".equals(propertyId)) {
                    Select s = new Select();
                    s.setCaption(createCaptionByPropertyId(propertyId));
                    s.addItem(ValidationRule.SEVERITY_ERROR);
                    s.addItem(ValidationRule.SEVERITY_WARN);
                    return s;
                }

                TextField f = new TextField();
                f.setCaption(createCaptionByPropertyId(propertyId));
                f.setWidth("300px");
                return f;

            }
        });
        ruleFields.setVisibleItemProperties(new String[]{"description", "sparqlQuery"});
        ruleEditor.addComponent(ruleFields);
    }

    private void initAddRemoveButtons() {
        addButton.addListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {
                Object beanItem = rules.addBean(new ValidationRule());
                rulesTable.select(beanItem);
            }
        });

        removeButton.addListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {
                Object contactId = rulesTable.getValue();
                rules.removeItem(contactId);
            }
        });
    }

    private void initRulesTable() {
        rulesTable.setContainerDataSource(rules);
        rulesTable.setWidth(100, UNITS_PIXELS);
        rulesTable.setHeight("100%");
        rulesTable.setImmediate(true);
        rulesTable.setSelectable(true);
        rulesTable.setVisibleColumns(new String[]{"description","severity"});
        rulesTable.addListener(new Property.ValueChangeListener() {
            public void valueChange(Property.ValueChangeEvent event) {
                Object o = rulesTable.getValue();
                if (null != o) {
                    ruleFields.setItemDataSource(rulesTable.getItem(o));
                }
                ruleEditor.setVisible(o != null);

            }
        });
        addComponent(rulesTable);
    }

    private void initLayout() {
        HorizontalSplitPanel splitPanel = new HorizontalSplitPanel();
        VerticalLayout rulesOverview = new VerticalLayout();
        splitPanel.addComponent(rulesOverview);
        splitPanel.addComponent(ruleEditor);
        rulesOverview.addComponent(rulesTable);
        rulesOverview.addComponent(addButton);
        rulesOverview.setSizeFull();
        rulesOverview.setExpandRatio(rulesTable, 1);
        rulesTable.setSizeFull();
        addButton.setWidth("100%");
        ruleEditor.setMargin(true);
        ruleEditor.setVisible(false);
        splitPanel.setHeight("100%");
        splitPanel.setMargin(true);
        splitPanel.setWidth("100%");
        splitPanel.setSizeFull();
        addComponent(splitPanel);
    }

    @Override
    public Object getConfig() {
        List rules = new ArrayList<ValidationRule>();
        rules.addAll(this.rules.getItemIds());
        config.setValidationRules(rules);
        return config;


    }
}
