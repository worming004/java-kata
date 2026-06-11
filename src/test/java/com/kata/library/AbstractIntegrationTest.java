package com.kata.library;

import org.springframework.boot.test.context.SpringBootTest;

/**
 * Base class for integration tests.
 *
 * Tests use the SQL Server instance configured through the standard
 * Spring datasource properties and do not start containers automatically.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class AbstractIntegrationTest {
}
