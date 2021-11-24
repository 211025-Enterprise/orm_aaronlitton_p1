package persistance;

import annotations.ValueTypes;
import connection.ConnectionSingleton;
import logger.LoggerConfig;
import model.ClassDeconstructor;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class TableDao extends Dao{
    private final Logger logger = Logger.getRootLogger();
    ClassDeconstructor classDeconstructor;



    public TableDao(Class<?> clazz){
        LoggerConfig.configure();
        classDeconstructor = new ClassDeconstructor(clazz);
    }

    public boolean createTable(){
        StringBuilder stmt = new StringBuilder();
        List<String> fields = classDeconstructor.getFieldList();
        stmt.append("create table if not exists \"")
                .append(classDeconstructor.getTableName())
                .append("\" (");
        if(classDeconstructor.getPrimarykey() != null){
            stmt.append("\"").append(classDeconstructor.getPrimarykey()).append("\"int primary key");
        } else {
            logger.error("Warning, There is no way to prove uniqueness");
            stmt.append("id serial primary key");
        }
        for (int i = 0; i < fields.size(); i++) {
            String field = fields.get(i);
            stmt.append(",\n\"").append(field).append("\" ");
            if(classDeconstructor.getFieldValues().get(i)== ValueTypes.decimal){
                stmt.append("decimal");
            }else if(classDeconstructor.getFieldValues().get(i)== ValueTypes.bool){
                stmt.append("boolean");
            }else if(classDeconstructor.getFieldValues().get(i)== ValueTypes.integer){
                stmt.append("int");
            }else{
                stmt.append(" varchar");
            }
        }
        stmt.append(")");
        try(Connection dbConnection = ConnectionSingleton.getInstance()) {
            PreparedStatement preparedStatement = dbConnection.prepareStatement(stmt.toString());
            preparedStatement.execute();
            preparedStatement.close();
            return true;
        } catch (SQLException throwable) {
            logger.error("SQL Error in executing", throwable);
            //to be deleted after testing
            throwable.printStackTrace();
        }
        return false;
    }

    //Tables are only stored once so no update method
    //There isn't any reading of tables

    public boolean deleteTable(){
        StringBuilder stmt = new StringBuilder();
        stmt.append("Drop Table if exists\"").append(classDeconstructor.getTableName()).append("\"");
        try(Connection dbConnection = ConnectionSingleton.getInstance()) {
            PreparedStatement preparedStatement = dbConnection.prepareStatement(stmt.toString());
            preparedStatement.execute();
            preparedStatement.close();
            return true;
        } catch (SQLException throwable) {
            logger.error("SQL Error in executing", throwable);
            //to be deleted after testing
            throwable.printStackTrace();
        }
        return false;
    }

    public boolean tableExists() {
        boolean result = false;
        StringBuilder stmt = new StringBuilder();
        stmt.append("SELECT EXISTS (\n" +
                "   SELECT FROM pg_catalog.pg_class c\n" +
                "   JOIN   pg_catalog.pg_namespace n ON n.oid = c.relnamespace\n" +
                "   WHERE  n.nspname = 'schema_name'\n" +
                "   AND    c.relname = ?\n" +
                "   AND    c.relkind = 'r'\n" +
                "   )");
        try(Connection dbConnection = ConnectionSingleton.getInstance()) {
            PreparedStatement preparedStatement = dbConnection.prepareStatement(stmt.toString());
            preparedStatement.setObject(1, classDeconstructor.getTableName());
            ResultSet rs = preparedStatement.executeQuery();
            if(rs.next()){
                //There was a result, therefore rs.object holds a boolean
                if((boolean) rs.getObject("EXISTS")){
                    result = true;
                }
            }
            preparedStatement.close();
            return result;
        } catch (SQLException throwable) {
            logger.error("SQL Error in executing", throwable);
            throwable.printStackTrace();
        }
        return result;
    }
}