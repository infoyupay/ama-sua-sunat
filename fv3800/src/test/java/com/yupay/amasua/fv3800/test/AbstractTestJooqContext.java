package com.yupay.amasua.fv3800.test;

import com.yupay.amasua.fv3800.infra.AppContext;
import com.yupay.amasua.fv3800.infra.AppMode;
import com.yupay.amasua.fv3800.services.persistence.JooqPersistenceContext;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Base test infrastructure for persistence-related tests using jOOQ.
 * <br/>
 * This abstract class provides a consistent {@link AppContext} configured
 * in {@link AppMode#TEST} and a temporary database location supplied by JUnit.
 * It is intended to be extended by tests that need direct access to the
 * {@link JooqPersistenceContext} facilities.
 * <br/>
 * <strong>Expected usage:</strong>
 * <br/>
 * {@snippet java:
 *     @SuppressWarnings("MissingJavadoc")
 *     class MyPersistenceTest extends AbstractTestJooqContext {
 *
 *    @Test
 *    void whenInsert_thenSuccessIsReturned(){
 *         var result = inTransactionSync(dsl-> dsl.insertInto());
 *         assertThat(result).isNotNull();
 *    }
 *
 * }
 *}
 * The application context and database are created once per test class,
 * ensuring fast and deterministic execution while still exercising real
 * persistence infrastructure.
 *
 * @author David Vidal - InfoYupay SACS
 * @version 1.0
 */
public abstract class AbstractTestJooqContext implements JooqPersistenceContext {

    /**
     * Shared application context for persistence tests.
     */
    private static AppContext appContext;

    /**
     * Temporary directory supplied by JUnit for this test class.
     * <br/>
     * It can be used by concrete tests if filesystem interaction is required.
     */
    private static Path tempDir;

    /**
     * Builds the test {@link AppContext} and copies the sample database
     * into a temporary directory.
     *
     * @param tempDir the temporary directory supplied by JUnit.
     * @throws IOException if the sample database cannot be copied.
     */
    @BeforeAll
    static void buildContext(@TempDir Path tempDir) throws IOException {
        var testDB = TestDbFileProvider.provideTestDb(tempDir);
        AbstractTestJooqContext.tempDir = tempDir;

        appContext = new AppContext(AppMode.TEST);
        appContext.setDbPath(testDB);
    }

    /**
     * Returns the {@link AppContext} used by this test class.
     *
     * @return the shared test application context.
     */
    protected static AppContext getAppContext() {
        return appContext;
    }

    /**
     * Provides the {@link AppContext} required by {@link JooqPersistenceContext}.
     *
     * @return the active test application context.
     */
    @Override
    public AppContext context() {
        return appContext;
    }
}
