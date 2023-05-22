package org.openjfx;

import org.openjfx.model.PersistException;

public interface DataChangeListener {

        void onDataChanged() throws PersistException;
}