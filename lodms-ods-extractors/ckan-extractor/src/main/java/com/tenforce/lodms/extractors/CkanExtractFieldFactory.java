package com.tenforce.lodms.extractors;

import com.vaadin.data.Item;
import com.vaadin.data.validator.AbstractStringValidator;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.TextField;
import com.vaadin.ui.TwinColSelect;
import com.vaadin.ui.VerticalLayout;
import org.openrdf.model.impl.URIImpl;

public class CkanExtractFieldFactory extends DefaultFieldFactory {
    @Override
    public Field createField(Item item, Object propertyId, Component uiContext) {
        if ("baseUri".equals(propertyId)) {
            TextField uriField = new TextField("CKAN Url");
            uriField.setRequired(true);
            uriField.setRequiredError("CKAN Url is required!");
            uriField.setWidth(350, VerticalLayout.UNITS_PIXELS);
            uriField.setDescription("Base url of the ckan portal.");
            uriField.setImmediate(true);
            uriField.addValidator(new AbstractStringValidator(null) {
                @Override
                protected boolean isValidString(String value) {
                    try {
                        new URIImpl(value);
                        return true;
                    } catch (Exception ex) {
                        setErrorMessage("Invalid CKAN Url: " + ex.getMessage());
                        return false;
                    }
                }
            });
            return uriField;

        } else if ("publisher".equals(propertyId)) {
            Field field = super.createField(item, propertyId, uiContext);
            field.setDescription("The foaf:agent responsible for this catalog.");
            field.setRequired(true);
            return field;
        } else if ("title".equals(propertyId)) {
            Field field = super.createField(item, propertyId, uiContext);
            field.setDescription("Title for this catalog.");
            field.setRequired(true);
            return field;

        } else if ("license".equals(propertyId)) {
            Field field = super.createField(item, propertyId, uiContext);
            field.setDescription("license for this catalog.");
            field.setRequired(true);
            return field;

        } else if ("description".equals(propertyId)) {
            Field field = super.createField(item, propertyId, uiContext);
            field.setDescription("Description for this catalog.");
            field.setWidth(350, VerticalLayout.UNITS_PIXELS);
            field.setRequired(true);
            return field;

        } else if ("ignoredKeys".equals(propertyId)) {
            Field field = super.createField(item, propertyId, uiContext);
            field.setDescription("A comma seperated list of attributes in the metadata that should be ignored by the extractor.");
            return field;

        } else if ("subjectPrefix".equals(propertyId)) {
            TextField subjectField = new TextField("Subject Prefix");
            subjectField.setRequired(true);
            subjectField.setDescription("This prefix will be used to generate the subject url.");
            subjectField.setRequiredError("Subject Prefix is required!");
            subjectField.setWidth(350, VerticalLayout.UNITS_PIXELS);
            return subjectField;
        } else if ("predicatePrefix".equals(propertyId)) {
            TextField predicateField = new TextField("Predicate Prefix");
            predicateField.setRequired(true);
            predicateField.setDescription("All json attributes will be prefixed with this string to generate a predicate.");
            predicateField.setRequiredError("Predicate Prefix is required!");
            predicateField.setWidth(350, VerticalLayout.UNITS_PIXELS);
            return predicateField;
        } else if ("packageIds".equals(propertyId)) {
            TwinColSelect select = new TwinColSelect("Select catalog records to harvest");
            select.setLeftColumnCaption("Available records");
            select.setRightColumnCaption("Selected records");
            select.setRows(20);
            select.setWidth(500, VerticalLayout.UNITS_PIXELS);
            return select;
        } else if ("allDatasets".equals(propertyId)) {
            CheckBox box = new CheckBox("harvest all datasets");
            box.setImmediate(true);
            return box;
        }

        return super.createField(item, propertyId, uiContext);
    }
}
