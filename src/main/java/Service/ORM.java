package Service;

import model.ClassDeconstructor;
import persistance.FieldDao;
import persistance.TableDao;

import java.lang.reflect.Field;
import java.util.List;

/**
 * This is kind of like the "Driver" of the ORM, making calls to all the
 * functions made throughout the program in one place.
 */
public class ORM {
    private Class<?> clazz;
    private FieldDao fieldDao;
    private TableDao tableDao;

    public ORM(Class<?> clazz){
        this.clazz = clazz;
        fieldDao = new FieldDao(clazz);
        tableDao = new TableDao(clazz);
        if(!tableExists()){
            initializeTable();
        }
    }

    public Class<?> getBaseClass(){
        return new ClassDeconstructor(clazz).getClazz();
    }

    public boolean addRecord(Object instance){
        return fieldDao.create(instance);
    }

    public List getRecords(){
        return fieldDao.readAll();
    }

    public boolean deleteRecord(int instanceId){
        return fieldDao.deleteSingle(instanceId);
    }

    public boolean initializeTable(){
        return tableDao.createTable();
    }

    public boolean tableExists(){ return tableDao.tableExists();}

    public boolean deleteTable(){
        return tableDao.deleteTable();
    }

    public boolean updateRecord(Object instance){
        return fieldDao.update(instance);
    }


}
