package com.tenforce.lodms.transformers;

import at.punkt.lodms.spi.transform.TransformContext;
import info.aduna.iteration.Iterations;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;
import org.openrdf.sail.memory.MemoryStore;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RunWith(JUnit4.class)
public class ODSValueMapperTest {
    static final URI DEFAULT_GRAPH = ValueFactoryImpl.getInstance().createURI("http://ods.test/");
    static final HashMap<String, URI> MAPPINGS = new HashMap<String, URI>() {
        {
            put("society", new URIImpl("http://example/society"));
            put("economy", new URIImpl("http://example/economy"));
            put("infrastruktur_bauen_wohnen", new URIImpl("http://example/living"));
            put("bevoelkerung", new URIImpl("http://example/population"));
            put("wirtschaft_arbeit", new URIImpl("http://example/work"));
        }

        ;
    };

    @Test
    public void transformShouldDeleteAllOldStatements() throws RepositoryException {
        Repository repository = new SailRepository(new MemoryStore());
        repository.initialize();
        RepositoryConnection con = repository.getConnection();
        try {
            loadRDFInStore("example_themes.n3", con);
            List<Statement> statements = Iterations.asList(con.getStatements(null, null, null, false, DEFAULT_GRAPH));
            ODSValueMapper mapper = new ODSValueMapper();
            ODSValueMapperConfig config = new ODSValueMapperConfig();
            config.setMappings(getMappingList());
            mapper.configure(config);
            mapper.transform(repository, DEFAULT_GRAPH, new TransformContext("myid", new HashMap<String, Object>()));
            for (Statement s : statements) {
                Assert.assertFalse(con.hasStatement(s, false, DEFAULT_GRAPH));
            }
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
            Assert.fail(e.getMessage());
        } finally {
            con.close();
        }
        repository.shutDown();
    }

    @Test
    public void transformShouldInsertNewStatements() throws RepositoryException {
        Repository repository = new SailRepository(new MemoryStore());
        repository.initialize();
        RepositoryConnection con = repository.getConnection();
        try {
            loadRDFInStore("example_themes.n3", con);
            List<Statement> statements = Iterations.asList(con.getStatements(null, null, null, false, DEFAULT_GRAPH));
            ODSValueMapper mapper = new ODSValueMapper();
            ODSValueMapperConfig config = new ODSValueMapperConfig();
            config.setMappedPredicate(new MappedPredicate(MappedPredicate.DATASET,"http://www.w3.org/ns/dcat#theme","bleh"));
            config.setMappings(getMappingList());
            mapper.configure(config);
            mapper.transform(repository, DEFAULT_GRAPH, new TransformContext("myid", new HashMap<String, Object>()));
            for (Statement s : statements) {
                Statement newStatement = new StatementImpl(s.getSubject(),s.getPredicate(),MAPPINGS.get(s.getObject().stringValue()));
                Assert.assertTrue(con.hasStatement(newStatement, false, DEFAULT_GRAPH));
            }
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        } finally {
            con.close();
        }
        repository.shutDown();

    }

    private void loadRDFInStore(String fileName, RepositoryConnection con, URI graph) throws RepositoryException, IOException, RDFParseException {
        InputStream stream = ODSValueMapperTest.class.getClassLoader().getResourceAsStream(fileName);
        if (null == stream) {
            throw new IllegalArgumentException("File not found: " + fileName);
        }
        con.add(stream, "http://testdata.com/", RDFFormat.N3, graph);
    }

    private void loadRDFInStore(String fileName, RepositoryConnection con) throws RepositoryException, IOException, RDFParseException {
        loadRDFInStore(fileName, con, DEFAULT_GRAPH);
    }

    private List<Mapping> getMappingList() {
        List<Mapping> mappings = new ArrayList<Mapping>();
        for (Map.Entry<String,URI> entry : MAPPINGS.entrySet()) {
            mappings.add(new Mapping(entry.getKey(),entry.getValue().stringValue()));
        }
        return mappings;
    }

}
