package com.yupay.amasua.fv3800.services.persistence;

import org.jooq.Table;
import org.jooq.UpdatableRecord;

/**
 * Descriptor that defines how a domain concept is persisted using jOOQ
 * through an updatable table and its corresponding POJO representation.<br/>
 * <br/>
 * This interface does not perform persistence operations by itself.<br/>
 * It acts as a declarative contract used by C/U/D services to locate:
 * <ul>
 *   <li>the jOOQ table whose records support mutation operations
 *       ({@link org.jooq.UpdatableRecord})</li>
 *   <li>the POJO class used for data transfer and mapping</li>
 * </ul>
 * <br/>
 * The presence or absence of concrete services (Insert, Update, Delete)
 * determines which operations are allowed for a given descriptor.<br/>
 * No flags or runtime checks are involved; capability is defined by exposure.
 *
 * @param <R> the type of jOOQ record backing the table, which must be updatable
 * @param <P> the type of the POJO associated with the table
 * @author David Vidal - InfoYupay SACS
 * @version 1.0
 */
public interface JooqUpdatableDescriptor<R extends UpdatableRecord<R>, P> {

    /**
     * Returns the jOOQ table associated with this descriptor.<br/>
     * <br/>
     * The returned table must produce records of type {@code R}, ensuring
     * support for store, update, and delete operations within jOOQ-based
     * persistence services.
     *
     * @return the updatable jOOQ table
     */
    Table<R> updatableTable();

    /**
     * Returns the POJO class associated with this descriptor.<br/>
     * <br/>
     * This class is typically used for mapping data in and out of the
     * persistence layer and for transporting state across application layers.
     *
     * @return the POJO class bound to this descriptor
     */
    Class<P> pojoClass();
}
