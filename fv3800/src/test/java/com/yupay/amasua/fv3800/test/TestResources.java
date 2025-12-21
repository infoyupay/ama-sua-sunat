package com.yupay.amasua.fv3800.test;

import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

/**
 * Centralized catalog of test resources used by the FV-3800 test suite.<br/>
 * <br/>
 * This enum provides a type-safe, discoverable way to reference resources
 * available on the test classpath (e.g. sample databases, fixtures, or
 * preconfigured artifacts) without relying on hard-coded string paths
 * scattered across test code.<br/>
 * <br/>
 * Each enum constant represents a single, named test resource and exposes
 * multiple access strategies ({@link URL}, {@link InputStream}, {@link Path})
 * depending on the needs of the test scenario.<br/>
 * <br/>
 * This class is intended for <b>test infrastructure only</b> and must not be
 * used in production code.
 *
 * @author David Vidal
 * @version 1.0
 */
public enum TestResources {

    /**
     * Sample SQLite database used as a golden master for persistence and
     * workflow tests.
     *
     * <p>
     * This database contains the full schema and seeded catalog data required
     * to execute read-only and read-write test scenarios against a realistic
     * environment. Tests are expected to copy this resource to a temporary
     * location before use, never modifying it directly.
     * </p>
     */
    SAMPLE_DATABASE("amasua-3800.db");

    /**
     * Classpath-relative resource name.
     *
     * <p>
     * This value represents the exact path used to locate the resource via
     * {@link Class#getResource(String)} and related mechanisms.
     * </p>
     */
    private final @NotNull String resource;

    /**
     * Creates a new {@code TestResources} enum constant bound to the given
     * classpath resource name.
     *
     * @param resource the classpath-relative name of the test resource
     */
    TestResources(@NotNull String resource) {
        this.resource = resource;
    }

    /**
     * Returns the {@link URL} pointing to this test resource on the classpath.
     *
     * <p>
     * This method fails fast if the resource cannot be found, producing a
     * clear and actionable error message indicating a misconfigured test
     * environment.
     * </p>
     *
     * @return the {@link URL} of the test resource
     * @throws NullPointerException if the resource is not found on the classpath
     */
    @NotNull
    public URL getResource() {
        var r = getClass().getResource(resource);
        return Objects.requireNonNull(r, "Resource " + resource + " not found.");
    }

    /**
     * Returns an {@link InputStream} for reading the contents of this test
     * resource.
     *
     * <p>
     * The lifecycle of the returned stream is the responsibility of the
     * caller. This method does not attempt to close or manage the stream.
     * </p>
     *
     * @return an {@link InputStream} for the test resource
     * @throws NullPointerException if the resource is not found on the classpath
     */
    @NotNull
    @SuppressWarnings("resource")
    public InputStream getResourceAsStream() {
        var r = getClass().getResourceAsStream(resource);
        return Objects.requireNonNull(r, "Resource " + resource + " not found.");
    }

    /**
     * Returns a {@link Path} representing the location of this test resource
     * on the filesystem.
     *
     * <p>
     * This method assumes that the resource is accessible as a regular file,
     * which is valid for test execution environments where resources are
     * loaded directly from the filesystem (e.g. Gradle or Maven test runs).
     * </p>
     *
     * @return a {@link Path} pointing to the test resource
     * @throws RuntimeException if the resource URI cannot be converted to a {@link Path}
     */
    public @NonNull Path getResourceAsPath() {
        try {
            return Path.of(getResource().toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException("Invalid resource URI: " + resource, e);
        }
    }
}

/**
 * Self-test for {@link TestResources}.
 * <br/>
 * <br/>
 * This test class validates the internal consistency and correctness of all
 * test resources declared in the {@link TestResources} enum. Its purpose is to
 * fail fast if the test classpath is misconfigured or if any declared resource
 * becomes unavailable.
 * <br/>
 * <br/>
 * The tests verify that each resource:
 * <ul>
 *   <li>Can be resolved as a {@link java.net.URL}.</li>
 *   <li>Can be resolved as a {@link java.nio.file.Path}.</li>
 *   <li>Can be opened and closed safely as an {@link java.io.InputStream}.</li>
 * </ul>
 * <br/>
 * This class is intentionally colocated with {@code TestResources} and kept
 * package-private, as it validates infrastructure concerns local to that enum
 * and is not part of the public test API.
 *  <div data-infoyupay="manual-test-verification"
 *    style="border:1px solid #c00;
 *    border-radius:2px;
 *    padding:4px;
 *    color:#c00;
 *    margin-top:6px;
 *    margin-bottom:6px;">
 *       <strong>Tested-by:</strong>
 *       dvidal@infoyupay.com - passed 1 tests in 0.124s at 2025-12-21T16:10:25 (UTC-5).
 * </div>
 *
 * @author InfoYupay SACS
 * @version 1.0
 */
class TestResourcesSelfTest {
    /**
     * Verifies that all {@link TestResources} entries are resolvable and usable.
     *
     * <p>
     * For each enum constant, this test asserts that:
     * </p>
     * <ul>
     *   <li>The resource can be resolved without throwing exceptions.</li>
     *   <li>The returned {@link java.net.URL} and {@link java.nio.file.Path}
     *       are non-null.</li>
     *   <li>An {@link java.io.InputStream} can be opened and closed successfully,
     *       validating correct lifecycle behavior.</li>
     * </ul>
     *
     * <p>
     * This test acts as a defensive check for the test environment itself and is
     * expected to be extremely stable. Any failure indicates a broken or
     * misconfigured test setup rather than a functional defect in application
     * logic.
     * </p>
     *
     * @param resource the test resource being validated
     */
    @ParameterizedTest
    @EnumSource(TestResources.class)
    void testURLStreamAndPath_areNeverNull(TestResources resource) {

        System.out.println("Testing resource: " + resource);
        assertThatNoException().isThrownBy(resource::getResource);
        assertThatNoException().isThrownBy(resource::getResourceAsPath);

        assertThat(resource.getResource()).isNotNull();
        assertThat(resource.getResourceAsPath()).isNotNull();

        assertThatNoException().isThrownBy(() -> {
            try (var s = resource.getResourceAsStream()) {
                assertThat(s).isNotNull();
            }
        });
    }
}