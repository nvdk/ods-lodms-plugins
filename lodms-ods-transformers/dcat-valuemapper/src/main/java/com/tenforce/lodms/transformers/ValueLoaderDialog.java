package com.tenforce.lodms.transformers;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.terminal.UserError;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Select;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.RepositoryException;

public class ValueLoaderDialog extends VerticalLayout {
    private TextField host = new TextField("host");
    private TextField port = new TextField("port");
    private TextField username = new TextField("username");
    private TextField password = new TextField("password");
    private Select graph = new Select("graph");
    private Button loadGraphs = new Button("load graphs");
    private Button loadValues = new Button("load values");
    private URI predicate;
    private BeanItemContainer<Mapping> mappings;

    public ValueLoaderDialog(BeanItemContainer<Mapping> mappings) {
        configureLayout();
        this.mappings = mappings;
    }

    public String getHost() {
        return host.getValue().toString();
    }

    public String getPort() {
        return port.getValue().toString();
    }

    public String getUsername() {
        return username.getValue().toString();
    }

    public String getPassword() {
        return password.getValue().toString();
    }

    public URI getGraph() {
        return (URI) graph.getValue();
    }

    private void configureLayout() {
        setMargin(true);
        setSpacing(true);
        Label message = new Label("Load values from a virtuoso store");
        addComponent(message);
        host.setValue("localhost");
        port.setValue("1111");
        username.setValue("dba");
        password.setValue("dba");
        loadGraphs.addListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                try {
                    ValueLoader loader = new ValueLoader(getHost(), getPort(), getUsername(), getPassword());
                    graph.addItem(new URIImpl("http://wukido"));
                    for (Resource context : loader.getAvailableGraph()) {
                        graph.addItem(context);
                    }
                    loadGraphs.setComponentError(null);
                } catch (IllegalArgumentException e) {
                    loadGraphs.setComponentError(new UserError(e.getMessage()));
                }
            }
        }
        );
        loadValues.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                ValueLoader loader = new ValueLoader(getHost(), getPort(), getUsername(), getPassword());
                try {
                    for (String value : loader.getValuesFor(getGraph(), getPredicate())) {
                        Mapping mapping = new Mapping();
                        mapping.setOriginalValue(value);
                        mappings.addItem(mapping);
                    }
                } catch (RepositoryException e) {
                    System.out.println("wuk");
                }
            }
        });

        addComponent(host);
        addComponent(port);
        addComponent(username);
        addComponent(password);
        addComponent(loadGraphs);
        addComponent(graph);
        addComponent(loadValues);
    }

    public URI getPredicate() {
        return predicate;
    }

    public void setPredicate(URI predicate) {
        this.predicate = predicate;
    }
}
