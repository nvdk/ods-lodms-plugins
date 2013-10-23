import at.punkt.lodms.integration.ConfigurationException;
import at.punkt.lodms.spi.extract.ExtractContext;
import at.punkt.lodms.spi.extract.ExtractException;
import com.tenforce.lodms.extractors.RDFExtractor;
import com.tenforce.lodms.extractors.RDFExtractorConfig;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.openrdf.query.BooleanQuery;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.repository.util.RDFInserter;
import org.openrdf.rio.RDFHandler;
import org.openrdf.sail.memory.MemoryStore;

import java.util.HashMap;

@RunWith(JUnit4.class)

public class RdfExtractorTest {

    @Test
    public void itShouldLoadRDFXML() {
        Repository repository = new SailRepository(new MemoryStore());
        loadFile(repository, "test1.rdf");
    }

    @Test
    public void itShouldLoadTurtle() {
        Repository repository = new SailRepository(new MemoryStore());
        loadFile(repository, "test1.ttl");
    }

    private void loadFile(Repository repository, String file) {
        try {
            repository.initialize();
            RDFExtractor rdfExtractor = new RDFExtractor();
            RDFExtractorConfig config = new RDFExtractorConfig();
            config.setRdfLocation(getClass().getResource(file).toString());
            rdfExtractor.configure(config);
            RepositoryConnection connection = repository.getConnection();
            RDFHandler handler = new RDFInserter(connection);
            rdfExtractor.extract(handler, new ExtractContext("my-id", new HashMap<String, Object>()));
            BooleanQuery query = connection.prepareBooleanQuery(QueryLanguage.SPARQL, "ASK {<http://www.w3.org/People/EM/contact#me> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/2000/10/swap/pim/contact#Person>}");
            Assert.assertTrue(query.evaluate());
            query = connection.prepareBooleanQuery(QueryLanguage.SPARQL, "ASK {<http://www.w3.org/People/EM/contact#me> <http://www.w3.org/2000/10/swap/pim/contact#mailbox> <mailto:em@w3.org>}");
            Assert.assertTrue(query.evaluate());
            query = connection.prepareBooleanQuery(QueryLanguage.SPARQL, "ASK {<http://www.w3.org/People/EM/contact#me> <http://www.w3.org/2000/10/swap/pim/contact#personalTitle> \"Dr.\"}");
            Assert.assertTrue(query.evaluate());
            query = connection.prepareBooleanQuery(QueryLanguage.SPARQL, "ASK {<http://www.w3.org/People/EM/contact#me> <http://www.w3.org/2000/10/swap/pim/contact#fullName> \"Eric Miller\"}");
            Assert.assertTrue(query.evaluate());
        } catch (RepositoryException e) {
            e.printStackTrace();
        } catch (ExtractException e) {
            Assert.fail(e.getMessage());
        } catch (ConfigurationException e) {
            Assert.fail(e.getMessage());
        } catch (MalformedQueryException e) {
            e.printStackTrace();
        } catch (QueryEvaluationException e) {
            Assert.fail(e.getMessage());
        }
    }
}
