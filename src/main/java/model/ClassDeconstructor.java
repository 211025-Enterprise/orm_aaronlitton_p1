package model;

import annotations.ClassMarker;
import annotations.FieldMarker;
import annotations.ValueTypes;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ClassDeconstructor {
    private Class<?> clazz;
    private String primarykey;
    private String tableName;
    private List<String> fieldList;
    private List<String> properFieldName;

    public List<String> getProperFieldName() {
        return properFieldName;
    }

    public ClassDeconstructor setProperFieldName(List<String> properFieldName) {
        this.properFieldName = properFieldName;
        return this;
    }


    public List<ValueTypes> getFieldValues() {
        return fieldValues;
    }

    public ClassDeconstructor setFieldValues(List<ValueTypes> fieldValues) {
        this.fieldValues = fieldValues;
        return this;
    }

    private List<ValueTypes> fieldValues;

    public Class<?> getClazz() {
        return clazz;
    }

    public ClassDeconstructor setClazz(Class<?> clazz) {
        this.clazz = clazz;
        return this;
    }

    public String getPrimarykey() {
        return primarykey;
    }

    public ClassDeconstructor setPrimarykey(String primarykey) {
        this.primarykey = primarykey;
        return this;
    }

    public List<String> getFieldList() {
        return fieldList;
    }

    public ClassDeconstructor setFieldList(List<String> fieldList) {
        this.fieldList = fieldList;
        return this;
    }

    public ClassDeconstructor addFieldList(String field) {
        fieldList.add(field);
        return this;
    }



    public ClassDeconstructor(Class<?> clazz){
        this.clazz = clazz;
        this.fieldList = new ArrayList<>();
        this.fieldValues = new ArrayList<>();
        this.properFieldName = new ArrayList<>();
        deconstructClass();
    }

    public void deconstructClass(){
        boolean isMarked = false;
        Field[] fields = this.clazz.getDeclaredFields();
        Annotation[] classAnnotations = clazz.getDeclaredAnnotations();
        for(Annotation annotation: classAnnotations){
            if(annotation instanceof ClassMarker){
                this.tableName = (((ClassMarker) annotation).className());
                isMarked = true;
            }
        }
        if(!isMarked){
            System.out.println("There is no class marked to be parsed.");
            return;
        }
        for(Field field: fields){
            Annotation[] annotations = field.getDeclaredAnnotations();
            for(Annotation annotation: annotations){
                if(annotation instanceof FieldMarker){
                    if(((FieldMarker) annotation).isKey()){
                        primarykey = ((FieldMarker) annotation).columnName();
                        properFieldName.add(field.getName());
                    }else {
                        properFieldName.add(field.getName());
                        this.addFieldList(((FieldMarker) annotation).columnName());
                        if(field.getGenericType() == Integer.TYPE) {
                            fieldValues.add(ValueTypes.integer);
                        } else if (field.getGenericType() == Boolean.TYPE){
                            fieldValues.add(ValueTypes.bool);
                        } else if (field.getGenericType() == Double.TYPE){
                            fieldValues.add(ValueTypes.decimal);
                        } else {
                            fieldValues.add(ValueTypes.varChar);
                        }
                    }
                }
            }
        }
    }

    public String getTableName() {
        return tableName;
    }

    public ClassDeconstructor setTableName(String tableName) {
        this.tableName = tableName;
        return this;
    }
}