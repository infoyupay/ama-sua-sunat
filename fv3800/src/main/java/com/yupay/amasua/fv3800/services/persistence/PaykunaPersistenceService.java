package com.yupay.amasua.fv3800.services.persistence;

import com.yupay.amasua.fv3800.infra.AppContext;
import com.yupay.amasua.fv3800.model.jooq.tables.pojos.Paykuna;
import com.yupay.amasua.fv3800.model.jooq.tables.records.PaykunaRecord;
import com.yupay.amasua.fv3800.services.persistence.validation.PaykunaValidator;
import org.jetbrains.annotations.NotNull;
import org.jooq.Table;

import java.util.concurrent.CompletableFuture;

import static com.yupay.amasua.fv3800.model.jooq.Tables.PAYKUNA;

/**
 * Persistence service for {@link Paykuna} entities.
 * <br/>
 * This service coordinates persistence operations related to Paykuna,
 * composing asynchronous validation and database interaction while
 * preserving a clear and explicit execution flow.
 * <br/>
 * Validation is performed asynchronously prior to persistence. If validation
 * fails, the operation is short-circuited and no database interaction is
 * attempted. If validation succeeds, the entity is persisted using jOOQ
 * within an asynchronous transactional context.
 * <br/>
 * This service does not block the calling thread and relies on the
 * executors configured in the underlying {@link AppContext} and
 * {@link JooqPersistenceContext}.
 *
 * @param context the application context providing executors and
 *                persistence configuration.
 * @author David Vidal - InfoYupay SACS
 * @version 1.0
 */
public record PaykunaPersistenceService(@NotNull AppContext context)
        implements
        JooqInsertOneBase<PaykunaRecord, Paykuna>,
        JooqPersistenceContext {

    /**
     * Validates and persists a single {@link Paykuna} instance.
     * <br/>
     * The operation follows this execution flow:
     * <br/>
     * 1. Asynchronous validation of the Paykuna instance.
     * <br/>
     * 2. If validation fails, a {@link QueryResult#failure(Throwable)} is
     * returned immediately and no persistence operation is executed.
     * <br/>
     * 3. If validation succeeds, the Paykuna instance is persisted using
     * jOOQ within an asynchronous transactional context.
     * <br/>
     * 4. The returned Paykuna reflects the state of the database after
     * persistence, including generated identifiers and default values.
     * <br/>
     * This method is fully non-blocking and returns immediately with a
     * {@link CompletableFuture}.
     *
     * @param paykuna the Paykuna instance to validate and persist.
     * @return a {@link CompletableFuture} containing a {@link QueryResult}
     * representing either the persisted Paykuna or a failure state.
     */
    public @NotNull CompletableFuture<QueryResult<Paykuna>> insertOnePaykuna(Paykuna paykuna) {
        return PaykunaValidator
                .validatePaykunaAsync(paykuna, context.generalExecutor())
                .thenCompose(qr -> qr.isFailure()
                        ? CompletableFuture.completedFuture(qr)
                        : insertOne(qr.value()));
    }

    @Override
    public Table<PaykunaRecord> updatableTable() {
        return PAYKUNA;
    }

    @Override
    public Class<Paykuna> pojoClass() {
        return Paykuna.class;
    }
}
