package org.apache.tuscany.core.injection;

import commonj.sdo.DataObject;
import commonj.sdo.helper.CopyHelper;
import org.apache.tuscany.core.builder.ObjectFactory;

/**
 * Creates new instances of an SDO
 *
 * @version $Rev$ $Date$
 */
public class SDOObjectFactory implements ObjectFactory<DataObject> {

    private DataObject dataObject;

    //----------------------------------
    // Constructors
    //----------------------------------

    public SDOObjectFactory(DataObject dataObject) {
        this.dataObject = dataObject;
    }

    //----------------------------------
    // Methods
    //----------------------------------

    public DataObject getInstance() throws ObjectCreationException {
        return CopyHelper.INSTANCE.copy(dataObject);
    }

    public void releaseInstance(DataObject instance) {
    }

}

