#Tech Stack

 Java 8
 Junit
 Mockito
 Apache maven
 Jackson library(for JSON marshalling/unmarshalling)
 Java EE servlet API
 PostGreSQL deployed on AWS RDS
 
#Functional requirements

 CRUD operations are supported for one or more domain objects via the web application's exposed endpoints
 JDBC logic is abstracted away by the custom ORM
 Programmatic persistence of entities (basic CRUD support) using custom ORM
 File-based or programmatic configuration of entities
 
#Non-Functional requirements

 85% line test coverage of the service layer
 Usage of the java.util.Stream API
 Custom ORM source code should be included as a Maven dependency

#Getting Started
Clone the ORM
git clone https://github.com/211025-Enterprise/orm_aaronlitton_p1.git

Clone the webApp
git clone https://github.com/211025-Enterprise/webapp_aaronlitton_p1.git

#How to use

You must annotate all classes with the ClassMarker annotation

You must annotate all the fields that you want persisted with the @FieldMarker annotation

Optionally one field that WILL be unique can be marked with the isKey = true variable, if that is marked the database will support removal and updating of singular objects

Your constructor that has all the fields that you persisted must be marked with the @InitConstructor, and the parameters must be in the same order in which the fields are initialized

Add a new Service and replace instances of the class name with your class to persist
