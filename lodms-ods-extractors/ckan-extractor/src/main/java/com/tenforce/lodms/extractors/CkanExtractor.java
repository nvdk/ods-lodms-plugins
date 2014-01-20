package com.tenforce.lodms.extractors;

import at.punkt.lodms.integration.ConfigDialog;
import at.punkt.lodms.integration.ConfigDialogProvider;
import at.punkt.lodms.integration.ConfigurableBase;
import at.punkt.lodms.integration.ConfigurationException;
import at.punkt.lodms.integration.UIComponent;
import at.punkt.lodms.spi.extract.ExtractContext;
import at.punkt.lodms.spi.extract.ExtractException;
import at.punkt.lodms.spi.extract.Extractor;
import com.tenforce.lodms.extractors.views.CkanExtractorConfigDialog;
import com.vaadin.Application;
import com.vaadin.terminal.ClassResource;
import com.vaadin.terminal.Resource;
import org.openrdf.rio.RDFHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class CkanExtractor extends ConfigurableBase<CkanExtractorConfig> implements Extractor, UIComponent, ConfigDialogProvider<CkanExtractorConfig> {
  private String subjectPrefix;
  private String predicatePrefix;
  private String baseUri;
  private List<String> ignoredKeys;
  private boolean harvestAll;
  private List<String> packageIds = new ArrayList<String>(1000);

  @Override
  public CkanExtractorConfig newDefaultConfig() {
    return new CkanExtractorConfig();
  }

  @Override
  protected void configureInternal(CkanExtractorConfig config) throws ConfigurationException {
    subjectPrefix = config.getSubjectPrefix();
    predicatePrefix = config.getPredicatePrefix();
    baseUri = config.getBaseUri();
    packageIds.clear();
    packageIds.addAll(config.getPackageIds());
    ignoredKeys = Arrays.asList(config.getIgnoredKeys().split("\\s*,\\s*"));
    harvestAll = config.getAllDatasets();
  }

  @Override
  public void extract(RDFHandler handler, ExtractContext context) throws ExtractException {
    Map map = context.getCustomData();
    map.put("ckanExtractBaseUri", baseUri);
    CkanHarvester harvester = new CkanHarvester(baseUri, subjectPrefix, predicatePrefix, handler);

    harvester.setIgnoredKeys(ignoredKeys);
    harvester.setCatalogOwner(config.getPublisher());
    harvester.setCatalogDescription(config.getDescription());
    harvester.setCatalogTitle(config.getTitle());
    harvester.setLicense(config.getLicense());
    harvester.setEnableProvenance(true);
    harvester.setWarnings(context.getWarnings());

    try {
      if (harvestAll)
        harvester.harvest();
      else
        harvester.harvest(packageIds);
    } catch (Exception e) {
      throw new ExtractException(e.getMessage(), e);
    }
  }

  @Override
  public String getName() {
    return "CKAN Extractor";
  }

  @Override
  public String getDescription() {
    return "Extracts metadata from a CKAN api.";
  }

  @Override
  public Resource getIcon(Application application) {
    return new ClassResource("/ckan.png", application);
  }

  @Override
  public String asString() {
    return getName();
  }

  @Override
  public ConfigDialog getConfigDialog(CkanExtractorConfig config) {
    return new CkanExtractorConfigDialog(config);
  }
}
