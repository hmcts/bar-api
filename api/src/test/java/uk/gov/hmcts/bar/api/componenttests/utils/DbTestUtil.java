package uk.gov.hmcts.bar.api.componenttests.utils;

import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public final class DbTestUtil {

    private static final String PROPERTY_KEY_RESET_SQL_TEMPLATE = "test.reset.sql.template";

    /**
     * Prevents instantiation.
     */
    private DbTestUtil() {}

    /**
     * This method reads the invoked SQL statement template from a properties file, creates
     * the invoked SQL statements, and invokes them.
     *
     * @param applicationContext    The application context that is used by our tests.
     * @param tableNames            The names of the database tables which auto-increment column will be reseted.
     * @throws SQLException         If a SQL statement cannot be invoked for some reason.
     */
    public static void resetAutoIncrementColumns(ApplicationContext applicationContext,
                                                 String... tableNames) throws SQLException {
        DataSource dataSource = applicationContext.getBean(DataSource.class);
        String resetSqlTemplate = getResetSqlTemplate(applicationContext);
        try (Connection dbConnection = dataSource.getConnection()) {
            //Create SQL statements that reset the auto-increment columns and invoke
            //the created SQL statements.
            for (String resetSqlArgument: tableNames) {
                try (Statement statement = dbConnection.createStatement()) {
                    String resetSql = String.format(resetSqlTemplate, resetSqlArgument);
                    statement.execute(resetSql);
                }
            }
        }
    }

    private static String getResetSqlTemplate(ApplicationContext applicationContext) {
        //Read the SQL template from the properties file
        Environment environment = applicationContext.getBean(Environment.class);
        return environment.getRequiredProperty(PROPERTY_KEY_RESET_SQL_TEMPLATE);
    }
}

