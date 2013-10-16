package com.tenforce.lodms.transformers;

import com.tenforce.lodms.ODSVoc;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.openrdf.model.Resource;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;

import java.util.List;

@RunWith(JUnit4.class)
public class ValueLoaderTest {
    @Test
    public void getAvailableGraphsShouldReturnCatalogs() {
        Repository repository = new SailRepository(new MemoryStore());
        try {
            repository.initialize();
            repository.getConnection().add(new URIImpl("http://test"), ODSVoc.RDFTYPE,ODSVoc.DCAT_CATALOG,new URIImpl("http://test"));
            ValueLoader valueLoader = new ValueLoader(repository);
            List<Resource> bleh = valueLoader.getAvailableGraph();
            Assert.assertEquals(bleh.get(0),new URIImpl("http://test"));
        } catch (RepositoryException e) {
            e.printStackTrace();

        }
    }
    @Test
    public void getAvailableGraphsShouldNotReturnOtherGraphs() {
        Repository repository = new SailRepository(new MemoryStore());
        try {
            repository.initialize();
            repository.getConnection().add(new URIImpl("http://not-a-catalog"), ODSVoc.RDFTYPE,ODSVoc.DCAT_DATASET,new URIImpl("http://not-a-catalog"));
            ValueLoader valueLoader = new ValueLoader(repository);
            List<Resource> bleh = valueLoader.getAvailableGraph();
            Assert.assertTrue(bleh.size() == 0);
        } catch (RepositoryException e) {
            e.printStackTrace();
        }
    }
}
