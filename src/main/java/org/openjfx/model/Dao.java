package org.openjfx.model;

import javafx.beans.property.IntegerProperty;
import org.openjfx.model.Identified;
import org.openjfx.model.PersistException;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;


// Унифицированный интерфейс управления персистентным состоянием объектов
//@param <T> тип объекта персистенции

public interface Dao<T extends Identified> {

    /** Создает новую запись и соответствующий ей объект */
    public void create(T object) throws PersistException;

    /** Создает новую запись, соответствующую объекту object */
    //public T persist(T object)  throws PersistException;

    /** Возвращает объект соответствующий записи с первичным ключом key или null */
    public T getByPK(int key) throws PersistException;

    /** Сохраняет состояние объекта group в базе данных */
    public void update(T object) throws PersistException;

    /** Удаляет запись об объекте из базы данных */
    public void delete(T object) throws PersistException;

    /** Возвращает список объектов соответствующих всем записям в базе данных */
    public List<T> getAll() throws PersistException;

    public List<T> search(T object) throws PersistException;
}