package com.yupay.amasua.fv3800.test;

import org.jspecify.annotations.NonNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;

/**
 * Utility class responsible for provisioning isolated SQLite database files
 * for test execution.<br/>
 * <br/>
 * This class creates per-test or per-iteration working copies of a reference
 * SQLite database defined in {@link TestResources}. The original resource is
 * treated as a read-only golden master and is never modified directly.<br/>
 * <br/>
 * Each invocation produces a uniquely named database file under a caller-
 * supplied target directory, allowing tests to execute safely without
 * interfering with one another.
 *
 * <p>
 * This class is part of the test infrastructure and must not be used in
 * production code.
 * </p>
 *
 * @author David Vidal
 * @version 1.0
 */
public final class TestDbFileProvider {

    /**
     * Private constructor to prevent instantiation.
     */
    private TestDbFileProvider() {
    }

    /**
     * Creates a working copy of the sample SQLite database for test execution.
     *
     * <p>
     * The database file is copied from {@link TestResources#SAMPLE_DATABASE}
     * into the specified target directory using a timestamp-based file name
     * to avoid collisions between test runs.
     * </p>
     *
     * @param target the directory where the test database file should be created
     * @return the {@link Path} to the newly created test database file
     * @throws IOException if the database file cannot be copied
     */
    public static @NonNull Path provideTestDb(@NonNull Path target) throws IOException {
        var r = target.resolve(targetName());
        Files.copy(
                TestResources.SAMPLE_DATABASE.getResourceAsPath(),
                r,
                StandardCopyOption.REPLACE_EXISTING
        );
        return r;
    }

    /**
     * Generates a timestamp-based file name for a test database instance.
     *
     * <p>
     * The generated name encodes the current date and time to provide
     * traceability and reduce the likelihood of name collisions during
     * test execution.
     * </p>
     *
     * @return a unique file name for a test database file
     */
    private static @NonNull String targetName() {
        return "fv3800_%1$tY-%1$tm-%1$tdT%1$tH-%1$tM-%1$tS_%1$tN_test.db"
                .formatted(LocalDateTime.now());
    }
}

