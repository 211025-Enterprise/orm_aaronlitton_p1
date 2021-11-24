package model;

import annotations.InitConstructor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

public class ClassReconstructor {
    private Constructor<?> constructor;
    private Field[] fields;

    public ClassReconstructor(Class<?> clazz){
        this.setConstructor(clazz);
    }

    public Constructor getConstructor(){
        return constructor;
    }

    public void setConstructor(Class<?> clazz){
        Constructor[] constructors = clazz.getDeclaredConstructors();
        Annotation[] annotations;
        for(Constructor constructor: constructors){
            annotations = constructor.getDeclaredAnnotations();
            for(Annotation annotation: annotations){
                if(annotation instanceof InitConstructor){
                    this.constructor = constructor;
                    return;
                }
            }
        }
        return;
    }

    public static Class<?> forceCreate(Class<?> clazz, String json){
        Logger logger = Logger.getLogger(clazz);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(json,clazz.getClass());
        } catch (JsonProcessingException e) {
            logger.error("Error creating the Json.",e);
            e.printStackTrace();
        }
        return null;
        /*
        ClassDeconstructor classDeconstructor = new ClassDeconstructor(clazz);
        constructors = clazz.getDeclaredConstructors();
        Constructor selectedConstructor = null;
        fields = clazz.getDeclaredFields();
        Annotation[] annotations;
        List<Field> validFields = new ArrayList<>();
        for(Field field: fields) {
            annotations = field.getDeclaredAnnotations();
            for (Annotation annotation : annotations) {
                if (annotation instanceof FieldMarker) {
                    validFields.add(field);
                    break;
                }
            }
        }
        for(Constructor constructor: constructors){
            annotations = constructor.getDeclaredAnnotations();
            for(Annotation annotation: annotations){
                if(annotation instanceof annotations.Constructor){
                    selectedConstructor = constructor;
                    break;
                }
            }
        }
        if(selectedConstructor == null){
            return null;
        }
        if(validFields.size() == selectedConstructor.getParameterCount()){
            try {
                return (clazz.getClass()); selectedConstructor.newInstance(clazz.getClass(),
                        classDeconstructor
                                .getFieldList()
                                .toArray(
                                        new String[classDeconstructor
                                                .getFieldList()
                                                .size()]
                                )
                );
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        return null;
        */
    }
}
