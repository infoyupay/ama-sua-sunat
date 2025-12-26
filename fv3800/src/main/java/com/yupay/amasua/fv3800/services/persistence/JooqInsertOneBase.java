package com.yupay.amasua.fv3800.services.persistence;

import org.jooq.Table;
import org.jooq.UpdatableRecord;

import java.util.concurrent.CompletableFuture;

/**
 * Base helper interface for simple jOOQ insert operations.
 * <br/>
 * This interface provides a reusable default implementation for inserting
 * a single POJO into the database using jOOQ, delegating transaction handling,
 * error conversion, and asynchronous execution to the underlying
 * {@link JooqPersistenceContext}.
 * <br/>
 * The abstraction is intentionally minimal and mechanical: it encapsulates
 * the repetitive jOOQ pattern of creating a record from a POJO, storing it,
 * and converting the persisted record back into a POJO instance.
 * <br/>
 * It is designed to reduce boilerplate in persistence services without
 * introducing domain logic or constraining more complex insert scenarios.
 *
 * @param <R> the jOOQ {@link UpdatableRecord} type associated with the table.
 * @param <P> the POJO type used as input and output of the insert operation.
 * @author David Vidal - InfoYupay SACS
 * @version 1.0
 */
public interface JooqInsertOneBase<R extends UpdatableRecord<R>, P>
        extends JooqPersistenceContext {

    /**
     * Inserts a single POJO into the given table and returns the persisted
     * representation.
     * <br/>
     * The operation is executed asynchronously within a transactional context.
     * Any runtime exception thrown during record creation or persistence is
     * captured and translated into a {@link QueryResult} failure, preserving
     * the service-level contract.
     * <br/>
     * The returned POJO instance reflects the state of the database after
     * persistence, including generated identifiers, default values, and
     * database-side modifications.
     *
     * @param table    the jOOQ table into which the record will be inserted.
     * @param pojoType the concrete POJO class used for record-to-POJO conversion.
     * @param pojo     the POJO instance to be inserted.
     * @return a {@link CompletableFuture} containing a {@link QueryResult}
     * that represents either the successfully persisted POJO or a
     * failure state.
     */
    default CompletableFuture<QueryResult<P>> insertOne(
            Table<R> table,
            Class<P> pojoType,
            P pojo
    ) {
        return inTransactionAsync(dsl -> {
            var r = dsl.newRecord(table, pojo);
            r.store();
            return r.into(pojoType);
        });
    }
}

