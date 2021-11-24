package persistance;

import connection.ConnectionSingleton;
import logger.LoggerConfig;
import model.ClassDeconstructor;
import model.ClassReconstructor;
import org.apache.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.lang.reflect.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class FieldDao extends Dao{
    private final ClassDeconstructor classDeconstructor;
    private final ClassReconstructor classReconstructor;
    Class<?> clazz;
    private final Logger logger = Logger.getRootLogger();
    //Create, Read, Update, Delete

    //Wont be used, possible implementation later
    //public void createColumn(Class<?> clazz, String field){}

    public FieldDao(Class<?> clazz){
        LoggerConfig.configure();
        this.clazz = clazz;
        classDeconstructor = new ClassDeconstructor(clazz);
        classReconstructor = new ClassReconstructor(clazz);
    }


    public List<Object> readAll(){
        ResultSet rs;
        List<String> columnList = classDeconstructor.getProperFieldName();
        List<Object> results = new ArrayList<>();
        List<Object> initializers = new ArrayList<>();
        StringBuilder stmt = new StringBuilder();
        stmt.append("select * from \"")
                .append(classDeconstructor.getTableName())
                .append("\"");
        try (Connection dbConnections = ConnectionSingleton.getInstance()){
            PreparedStatement preparedStatement = dbConnections.prepareStatement(stmt.toString());
            rs = preparedStatement.executeQuery();
            while (rs.next()) {
                for (String s : columnList) {
                    initializers.add(rs.getObject(s));
                }
                results.add(classReconstructor.getConstructor().newInstance(initializers.toArray()));
                initializers.clear();
            }
        } catch (SQLException e) {
            logger.error("SQL Error", e);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            logger.error("Error initializing class", e);
        }
        return results;
    }

    public Object readSingle(int instanceId){
        List<Object> initializers = new ArrayList<>();
        List<String> columnList = classDeconstructor.getProperFieldName();
        Object result;
        if(classDeconstructor.getPrimarykey()==null){
            return null;
        }
        StringBuilder stmt = new StringBuilder();
        stmt.append("select * from \"")
                .append(classDeconstructor.getTableName())
                .append("\" where \"")
                .append(classDeconstructor.getPrimarykey())
                .append("\" = ?");
        PreparedStatement preparedStatement;
        try(Connection dbConnection = ConnectionSingleton.getInstance()){
            preparedStatement = dbConnection.prepareStatement(stmt.toString());
            preparedStatement.setObject(1, instanceId);
            ResultSet rs = preparedStatement.executeQuery();
            preparedStatement.close();
            rs.next();
            for (String s : columnList) {
                initializers.add(rs.getObject(s));
            }
            result = classReconstructor.getConstructor().newInstance(initializers.toArray());
            return result;
        } catch (SQLException e) {
            logger.error("Unable to complete SQL request", e);
            //to be deleted after testing
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Object readSingle(Object instance){
        ResultSet rs;
        Object result;
        List<Object> initializers = new ArrayList<>();
        ClassDeconstructor instanceDeconstructor = new ClassDeconstructor(instance.getClass());
        StringBuilder stmt = new StringBuilder();
        stmt.append("select * from \"")
                .append(instanceDeconstructor.getTableName())
                .append("\" (");
        List<String> columnList = instanceDeconstructor.getProperFieldName();
        if(classDeconstructor.getPrimarykey()==null){
            return null;
        }
        if(instanceDeconstructor.getPrimarykey() == null){
            logger.warn("Data was not given a primary key, so instance cannot be identified");
            return null;
        }
        stmt.append(columnList.get(0))
                .append("\" where \"")
                .append(instanceDeconstructor.getPrimarykey())
                .append("\" = ?");
        PreparedStatement preparedStatement;
        Field field;
        try(Connection dbConnection = ConnectionSingleton.getInstance()) {
            preparedStatement = dbConnection.prepareStatement(stmt.toString());
            field = instance.getClass().getDeclaredField(classDeconstructor.getPrimarykey());
            field.setAccessible(true);
            preparedStatement.setObject(1,field.get(instance));
            rs = preparedStatement.executeQuery();
            preparedStatement.close();
            rs.next();
            for (String s : columnList) {
                initializers.add(rs.getObject(s));
            }
            result = classReconstructor.getConstructor().newInstance(initializers.toArray());
            preparedStatement.close();
            return result;
        } catch (SQLException e) {
            logger.error("Unable to complete SQL request", e);
            //to be deleted after testing
        } catch (NoSuchFieldException e) {
            logger.error("No field of that name exists", e);
        } catch (IllegalAccessException e) {
            logger.error("Unable to access the variable", e);
        } catch (InvocationTargetException e) {
            logger.error("Unable to invoke the target", e);
            e.printStackTrace();
        } catch (InstantiationException e) {
            logger.error("Unable to instantiate", e);
            e.printStackTrace();
        }
        return null;
    }

    public boolean create(Object instance){
        ClassDeconstructor instanceDeconstructor = new ClassDeconstructor(instance.getClass());
        List<String> columnList = instanceDeconstructor.getFieldList();
        List<String> constructorFieldName = classDeconstructor.getProperFieldName();
        StringBuilder stmt = new StringBuilder();
        int arguments = constructorFieldName.size();
        stmt.append("insert into \"")
                .append(instanceDeconstructor.getTableName())
                .append("\" (\"");
        if(classDeconstructor.getPrimarykey() != null){
            stmt.append(classDeconstructor.getPrimarykey())
                    .append("\"");
            arguments --;
            IntStream.range(0, columnList.size())
                    .mapToObj(i -> ", \"" + columnList.get(i) + "\"")
                    .forEach(stmt::append);
        } else {
            stmt.append(columnList.get(0))
                    .append("\"");
            IntStream.range(1, columnList.size())
                    .mapToObj(i -> ", \"" + columnList.get(i) + "\"")
                    .forEach(stmt::append);
        }
        stmt.append(") values (?");
        IntStream.range(0, arguments - 1)
                .mapToObj(i -> ", ?")
                .forEach(stmt::append);
        stmt.append(")");
        PreparedStatement preparedStatement;
        Field field;
        try(Connection dbConnection = ConnectionSingleton.getInstance()){
            preparedStatement = dbConnection.prepareStatement(stmt.toString());
            for(int i = 0; i < constructorFieldName.size(); i++){
                field = instance.getClass().getDeclaredField(constructorFieldName.get(i));
                field.setAccessible(true);
                preparedStatement.setObject(i+1,field.get(instance));
            }
            preparedStatement.execute();
            preparedStatement.close();
            return true;
        } catch (SQLException e) {
            logger.error("Unable to complete SQL request", e);
            //to be deleted after testing
        } catch (NoSuchFieldException e) {
            logger.error("No field of that name exists", e);
        } catch (IllegalAccessException e) {
            logger.error("Unable to access the variable", e);
        }
        return false;
    }
    public boolean delete(Object instance){
        boolean result;
        ClassDeconstructor instanceDeconstructor = new ClassDeconstructor(instance.getClass());
        StringBuilder stmt = new StringBuilder();
        stmt.append("delete from \"")
                .append(instanceDeconstructor.getTableName())
                .append("\" (");
        List<String> columnList = instanceDeconstructor.getFieldList();
        if(instanceDeconstructor.getPrimarykey() == null){
            logger.warn("Data was not given a primary key, so instance cannot be identified");
            return false;
        }
        stmt.append(columnList.get(0))
                .append("\" where \"")
                .append(instanceDeconstructor.getPrimarykey())
                .append("\" = ?");
        PreparedStatement preparedStatement;
        Field field;
        try(Connection dbConnection = ConnectionSingleton.getInstance()) {
            preparedStatement = dbConnection.prepareStatement(stmt.toString());
            field = instance.getClass().getDeclaredField(classDeconstructor.getPrimarykey());
            field.setAccessible(true);
            preparedStatement.setObject(1,field.get(instance));
            result = preparedStatement.execute();
            preparedStatement.close();
            return result;
        } catch (SQLException e) {
            logger.error("Unable to complete SQL request", e);
            //to be deleted after testing
        } catch (NoSuchFieldException e) {
            logger.error("No field of that name exists", e);
        } catch (IllegalAccessException e) {
            logger.error("Unable to access the variable", e);
        }
        return false;
    }

    public boolean update(Object instance){
        ClassDeconstructor instanceDeconstructor = new ClassDeconstructor(instance.getClass());
        StringBuilder stmt = new StringBuilder();
        stmt.append("update \"")
                .append(instanceDeconstructor.getTableName())
                .append("\" set \"");
        List<String> columnList = instanceDeconstructor.getFieldList();
        stmt.append(columnList.get(0))
                .append("= ?");
        for(int i = 1; i < columnList.size(); i++){
            stmt.append(", ")
                    .append(columnList.get(i))
                    .append("= ?");
        }
        stmt.append(instanceDeconstructor.getTableName())
                .append("\" where \"")
                .append(instanceDeconstructor.getPrimarykey())
                .append("\" = ?");

        PreparedStatement preparedStatement;
        Field field;
        try(Connection dbConnection = ConnectionSingleton.getInstance()){
            preparedStatement = dbConnection.prepareStatement(stmt.toString());
            for(int i = 0; i < columnList.size(); i++){
                field = instance.getClass().getDeclaredField(columnList.get(i));
                field.setAccessible(true);
                preparedStatement.setObject(i+1,field.get(instance));
            }
            instance.getClass().getDeclaredField(instanceDeconstructor.getPrimarykey()).setAccessible(true);
            preparedStatement.setObject(columnList.size()+1,instance.getClass().getDeclaredField(instanceDeconstructor.getPrimarykey()).get(instance));//instanceid);
            preparedStatement.execute();
            preparedStatement.close();
            return true;
        } catch (SQLException e) {
            logger.error("Unable to complete SQL request", e);
            //to be deleted after testing
        } catch (NoSuchFieldException e) {
            logger.error("No field of that name exists", e);
        } catch (IllegalAccessException e) {
            logger.error("Unable to access the variable", e);
        }
        return false;
    }

    public boolean deleteSingle(int instanceId){
        List<Object> initializers = new ArrayList<>();
        List<String> columnList = classDeconstructor.getProperFieldName();
        if(classDeconstructor.getPrimarykey()==null){
            return false;
        }
        StringBuilder stmt = new StringBuilder();
        stmt.append("delete from \"")
                .append(classDeconstructor.getTableName())
                .append("\" where \"")
                .append(classDeconstructor.getPrimarykey())
                .append("\" = ?");
        PreparedStatement preparedStatement;
        try(Connection dbConnection = ConnectionSingleton.getInstance()){
            preparedStatement = dbConnection.prepareStatement(stmt.toString());
            preparedStatement.setObject(1, instanceId);
            preparedStatement.execute();
            preparedStatement.close();
            return true;
        } catch (SQLException e) {
            logger.error("Unable to complete SQL request", e);
            //to be deleted after testing
        }
        return false;
    }
}
