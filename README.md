# RelationalDbToDocumentalDbMigration

This program let to migrate a relational model data base into a documental db. In this approach, oriented to use MongoDB.

## 1. MongoDb Set Up:

Follow the next steps:
* Search "MongoDb Community Download" or go to: https://www.mongodb.com/try/download/community
* Clic on "On-Premises" and Clic on "MongoDb Community Server"
* Select your appropiate settings for your local OS System.
* In windows set-up go to C:\ and create a folder called data that contains other folder called db, like this: C:\data\db

## 2. Java Program:

The relational database to document export methods are found in the class
*ExportDBADocumental*. Their description is as follows:  
**exportTableConReference (tablename)**:  
This method is used to export the tables with the document reference pattern, the table name parameter is used to select the table to be exported. In the constructor the method is invoked to load the metadata of the database which is configured in the file:   
```src/Configuration/bases.xml.```  
  
  **baseExporter.exportOneToEmbed (baseTable, baseTable field, embeddedTable, embeddedTable field, embeddedDocumentName)**:  
This method is used to export tables with the embedded document pattern, the method parameters are:
* tablaBase = table from which the main document will be extracted.
* fieldTablaBase = field with which the embedded document will be associated.
* Embedded table = table from which the embedded document will be extracted.
* Embedded Table field = field to associate the embedded table with the base table.
* Embedded DocumentName = label that will receive the embedded document.  

**exportOneAMany Embed (baseTable, baseTable field, embeddedTable, embeddedTable field, embeddedDocumentName)**:  
This method is used to export one-to-many records with the embedded document pattern, the parameters of the method are:
* tablaBase = table from which the main document will be extracted.
* fieldTablaBase = field with which the embedded document will be associated.
* embeddedTable = table from which to extract the array of embedded documents.
* Embedded Table field = field to associate the records of the embedded table with the base table.
* Embedded DocumentName = label that will receive the embedded document array.

## **Enjoy!**
