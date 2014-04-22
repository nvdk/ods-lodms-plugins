package com.tenforce.lodms.transformers.translators;

import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.QueryResult;
import org.openrdf.query.TupleQuery;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class TranslationCache {
  /* custom RDF class for translations */
  private static final URI KLASS_TRANS = new URIImpl("ods:Translation");
  /* predicate for original value */
  private static final URI PRED_ORIG = new URIImpl("ods:orig");
  /* predicate for translation   */
  private static final URI PRED_TRANS = new URIImpl("ods:translation");
  private Repository repository;
  private RepositoryConnection con;
  private URI translatorCache;
  private static final int LOOKUP_SIZE = 200;

  /**
   * @param translatorCache
   */
  public TranslationCache(Repository repository, URI translatorCache) {
    this.repository = repository;
    this.translatorCache = translatorCache;
    this.con = getConnection();
  }

  /**
   * checks whether the object of the provided statement has a translation in the translationCache
   *
   * @param statement to be translated
   */
  public boolean hasTranslation(Statement statement) throws RepositoryException {
    URI uri = buildResourceURI(statement.getObject().stringValue());
    return getConnection().hasStatement(uri, null, null, false, translatorCache);
  }

  /**
   * creates a translatedStatement using the translationCache
   *
   * @param originalStatements list of statements to be translated
   * @return list of TranslatedStatements
   */
  public List<TranslatedStatement> translate(Collection<Statement> originalStatements) {
    List<TranslatedStatement> translatedStatements = new ArrayList<TranslatedStatement>();
    HashMap<URI, Model> toBeTranslated = new HashMap<URI, Model>(LOOKUP_SIZE);
    int i = 1;
    for (Statement s : originalStatements) {
      smartPut(toBeTranslated, buildResourceURI(s.getObject().stringValue()), s);
      if (i == originalStatements.size() || toBeTranslated.keySet().size() % LOOKUP_SIZE == 0) {
        translatedStatements.addAll(loadTranslations(toBeTranslated));
        toBeTranslated = new HashMap<URI, Model>(LOOKUP_SIZE);
      }
      i++;
    }
    return translatedStatements;
  }

  /**
   * quick function to support multiple statements linked to one md5sum
   *
   * @param map
   * @param key
   * @param s
   */
  private void smartPut(HashMap<URI, Model> map, URI key, Statement s) {
    if (map.containsKey(key))
      map.get(key).add(s);
    else {
      Model m = new LinkedHashModel();
      m.add(s);
      map.put(key, m);
    }
  }

  private HashMap<URI, Statement> buildMd5StatementMap(Collection<Statement> originalStatements) {
    HashMap<URI, Statement> md5StatementMap = new HashMap<URI, Statement>(originalStatements.size());
    for (Statement s : originalStatements) {
      Value v = s.getObject();
      md5StatementMap.put(buildResourceURI(v.stringValue()), s);
    }
    return md5StatementMap;
  }


  /**
   * @param md5StatementMap
   * @return
   */
  private List<TranslatedStatement> loadTranslations(HashMap<URI, Model> md5StatementMap) {

    List<TranslatedStatement> translatedStatements = new ArrayList<TranslatedStatement>();
    String queryString = "SELECT ?uri ?translation \n" +
            "FROM <" + translatorCache + "> \n" +
            " WHERE \n" +
            "{ \n" +
            "?uri ?p ?translation. \n" +
            "VALUES ?uri { " + combineURIs(md5StatementMap.keySet(), " ") + "  } \n " +
            "}";
    try {
      TupleQuery query = con.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
      QueryResult<BindingSet> result = query.evaluate();
      while (result.hasNext()) {
        BindingSet set = result.next();
        URI md5Uri = (URI) set.getValue("uri");
        Value translation = set.getValue("translation");
        Model origStatements = md5StatementMap.get(md5Uri);
        for (Statement origStatement : origStatements) {
          Statement translatedStatement = new StatementImpl(origStatement.getSubject(), origStatement.getPredicate(), translation);
          translatedStatements.add(new TranslatedStatement(origStatement, translatedStatement));
        }
      }
      result.close();
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
    return translatedStatements;
  }

  /**
   * Add translations to the translation cache
   * Only adds new translations
   *
   * @param statementList
   * @throws RepositoryException
   */
  public void addTranslations(Collection<TranslatedStatement> statementList) throws RepositoryException {
    Model translations = new LinkedHashModel();
    for (TranslatedStatement s : statementList) {
      String orig = s.getOriginalStatement().getObject().stringValue();
      String trans = s.getTranslatedStatement().getObject().stringValue();
      translations.addAll(modelTranslation(orig, trans));
    }
    con.add(translations, translatorCache);
  }

  /**
   * prepare statement to store translation in virtuoso
   *
   * @param orig
   * @param trans
   * @return
   */
  private Model modelTranslation(String orig, String trans) {
    Model translation = new LinkedHashModel();
    translation.add(buildResourceURI(orig), PRED_TRANS, new LiteralImpl(trans, "en"));
    return translation;
  }

  /**
   * creates a unique uri for a string,
   * uses the md5hash of the string
   *
   * @param orig
   * @return
   */
  private URI buildResourceURI(String orig) {
    return new URIImpl("ods:translation#" + md5(orig));
  }

  // clean up connection
  protected void finalize() throws Throwable {
    super.finalize();
    con.close();
    repository.shutDown();
  }

  // helpers

  /**
   * convenience function to work around sesame exceptions
   *
   * @return
   */
  private RepositoryConnection getConnection() {
    try {
      return repository.getConnection();
    } catch (RepositoryException e) {
      throw new IllegalStateException(e);
    }
  }

  /**
   * create a md5hash from a string
   *
   * @param message
   * @return
   */
  private String md5(String message) {
    try {
      MessageDigest md5 = MessageDigest.getInstance("md5");
      return new HexBinaryAdapter().marshal(md5.digest(message.getBytes("UTF-8"))).toLowerCase();
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }

  /**
   * join a collection of uris,
   * will use sparql notation for the URIs
   *
   * @param uris
   * @param glue
   * @return
   */
  String combineURIs(Collection<URI> uris, String glue) {
    if (uris.size() == 0) {
      return "";
    }
    StringBuilder out = new StringBuilder();
    int i = 0;
    for (URI u : uris) {
      out.append("<" + u + ">");
      if (++i < uris.size())
        out.append(glue);
    }
    return out.toString();
  }
}
