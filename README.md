# Personal Lightweight Orm

## Project Description

A lightweight orm with simple and minimal annotations 

## Technologies used

* Java 8
* Junit
* Mockito
* Apache maven
* Jackson library(for JSON marshalling/unmarshalling)
* Java EE servlet API
* PostGreSQL deployed on AWS RDS
 
# Features
* CRUD functionality
* Annotation only setup for repository and service layer setup
* Persistance to entities if hooked up to a functional RDS
* Optional Key if one is selected (Otherwise one is generated)
 
To-do list:

* Nested Objects
* Implementation of custom object mapping dynamically from the controller

#Getting Started

Clone the ORM and package as a maven dependency (Intelij + Maven build was used for this)
`git clone https://github.com/211025-Enterprise/orm_aaronlitton_p1.git`

Clone the if necessary, or hook up to your own program
`git clone https://github.com/211025-Enterprise/webapp_aaronlitton_p1.git`

#How to use

> You must annotate all classes with the ClassMarker annotation

> You must annotate all the fields that you want persisted with the @FieldMarker annotation

> Optionally one field that WILL be unique can be marked with the isKey = true variable, if that is marked the database will support removal and updating of singular objects

> Your constructor that has all the fields that you persisted must be marked with the @InitConstructor, and the parameters must be in the same order in which the fields are initialized

> Add a new Service and replace instances of the class name with your class to persist
