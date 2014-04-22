package com.tenforce.lodms.transformers.translators;

import junit.framework.Assert;
import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.repository.Repository;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.Rio;
import org.openrdf.sail.memory.MemoryStore;
import org.testng.annotations.Test;

public class TranslationCacheTest {
  //  @Test
  public void testHasTranslation() throws Exception {
    Repository repository = new SailRepository(new MemoryStore());
    repository.initialize();
    Model statements = Rio.parse(this.getClass().getResourceAsStream("bing/statements.ttl"), "", RDFFormat.TURTLE);
    TranslationCache cache = new TranslationCache(repository, null);
    for (Statement s : statements) {
      Assert.assertFalse("it should not find any translation", cache.hasTranslation(s));
    }
    Model translations = Rio.parse(this.getClass().getResourceAsStream("bing/translations.ttl"), "", RDFFormat.TURTLE);
    repository.getConnection().add(translations);
    for (Statement s : statements) {
      Assert.assertTrue("it should find a translation", cache.hasTranslation(s));
    }
  }

  @Test
  public void testTranslate() throws Exception {
  }

  @Test
  public void testAddTranslations() throws Exception {

  }
}
