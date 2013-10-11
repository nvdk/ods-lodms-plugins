ods-lodms-plugins
=================

This repository contains the plugins that were created in the course of the Open Data Support Project to extend the the Linked Open Data Manager Suite into the Open Data Interopability platform. 

Plugins are available as maven projects for inclusion in your project. They are split up into three packages according to the pipeline stage. 

lodms-ods-extractors 
-------------------

* ckan extractor 

will try to retrieve metadata from a version 3 CKAN API and convert that data into RDF.

* CSV Extractor

A ODS specific version of the CSV extractor that support imports of DCAT CSV's.See sample included in the test

* RDF Extractor

A Basic RDF extractor that can import any rdf+xml into the pipeline

* Virtuoso extractor

Loads RDF from a specified graph in a virtuoso store into the pipeline

lodms-ods-transformers
-----------------------
* Multiple SPARQL updater

This plugins allows you to specify multiple SPARQL update queries easily.

* ODS Modification detector

A ODS specific plugin that tries to determine if metadata was updated since the last harvest (here be dragons)

* ODS DCAT Harmonization Plugin

A ODS specific plugin that creates a harmonized URL and basic catalog record for each dataset found in the raw data

* ODS Cleaner

A ODS specific plugin that deletes any non harmonized data from the pipeline

* ODS Value Mapper

A ODS specific plugin that can be used to create a value mapping to a controller vocabulary as specified in section 8.2 of the DCAT Application Profile for European portals

lodms-ods-loaders
------------------
* Virtuoso Loader

This plugin loads rdf in the pipeline into a specified graph in the virtuoso store.
