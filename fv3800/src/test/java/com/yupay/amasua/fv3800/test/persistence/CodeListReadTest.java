package com.yupay.amasua.fv3800.test.persistence;

import com.yupay.amasua.fv3800.model.enums.ListIds;
import com.yupay.amasua.fv3800.model.jooq.tables.pojos.CodeList;
import com.yupay.amasua.fv3800.services.persistence.QueryResult;
import com.yupay.amasua.fv3800.test.AbstractTestJooqContext;
import org.junit.jupiter.api.Test;

import static com.yupay.amasua.fv3800.model.jooq.Tables.CODE_LIST;
import static org.assertj.core.api.Assertions.assertThat;
/**
 * Persistence read tests for the {@code CodeList} catalog.
 * <br/>
 * This test class verifies that catalog data seeded in the database
 * can be correctly read using jOOQ, exercising the real persistence
 * infrastructure configured for {@link com.yupay.amasua.fv3800.infra.AppMode#TEST}.
 * <br/>
 * The {@code CodeList} entity is treated as a read-only catalog,
 * where the source of truth for the catalog identifier is defined
 * by {@link ListIds}.
 * <div data-infoyupay="manual-test-verification"
 *    style="border:1px solid #c00;
 *    border-radius:2px;
 *    padding:4px;
 *    color:#c00;
 *    margin-top:6px;
 *    margin-bottom:6px;">
 *       <strong>Tested-by:</strong>
 *       dvidal@infoyupay.com - passed 1 tests in 1.478s at 2025-12-25T17:38:14 (UTC-5).
 * </div>
 *
 * @author David Vidal - InfoYupay SACS
 * @version 1.0
 */
class CodeListReadTest extends AbstractTestJooqContext {

    /**
     * Verifies that seeded {@code CodeList} entries for the {@code COUNTRY}
     * catalog can be successfully read from the database.
     * <br/>
     * The test asserts that:
     * <br/>
     * <ul>
     *   <li>The query execution succeeds.</li>
     *   <li>The result contains at least one entry.</li>
     *   <li>All returned entries belong to the {@code COUNTRY} catalog.</li>
     * </ul>
     */
    @Test
    void whenReadingCodeList_thenSeededDataIsReturned() {

        var result = inTransactionSync(dsl ->
                dsl.selectFrom(CODE_LIST)
                        .where(CODE_LIST.LIST_ID.eq(ListIds.COUNTRY))
                        .fetchInto(CodeList.class)
        );

        assertThat(result)
                .isNotNull()
                .doesNotMatch(QueryResult::isFailure)
                .matches(QueryResult::isSuccess);

        // sanity check: all values are countries
        assertThat(result.value())
                .isNotEmpty()
                .allMatch(code ->
                        ListIds.COUNTRY.equals(code.getListId())
                );
    }
}
