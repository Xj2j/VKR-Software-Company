package org.openjfx.model;

public interface DaoFactoryInterface<Context> {

    public interface DaoCreator<Context> {
        public Dao create(Context context);
    }

    public Context getContext() throws PersistException;

    public Dao getDao(Context context, Class dtoClass) throws PersistException;
}
