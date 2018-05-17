package uk.gov.hmcts.bar.api.componenttests.utils;

import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import uk.gov.hmcts.reform.auth.checker.spring.useronly.UserDetails;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.stream.Collectors;

public final class DbTestUtil {

    private static final String PROPERTY_KEY_RESET_SQL_TEMPLATE = "test.reset.sql.template";
    private static final String PROPERTY_KEY_INSERT_USER_SQL_TAMPLATE = "test.user.sql.template";
    private static final String INSERT_PI_QUERY =
        "INSERT INTO payment_instruction (id,payer_name,cheque_number,payment_type_id,payment_date,amount,currency,all_pay_transaction_id,status,postal_order_number,site_id,daily_sequence_id,action,user_id) VALUES (1,'John Doe',null,'cards',{ts '2018-03-25 23:32:23.871'},50000,'GBP',null,'TTB',null,'BR01',1,'P','1234');\n" +
        "INSERT INTO payment_instruction (id,payer_name,cheque_number,payment_type_id,payment_date,amount,currency,all_pay_transaction_id,status,postal_order_number,site_id,daily_sequence_id,action,user_id) VALUES (2,'John Doe',null,'cards',{ts '2018-03-25 23:36:11.207'},50000,'GBP','123','P',null,'BR01',2,null,'4321');\n" +
        "INSERT INTO case_fee_detail (case_fee_id,payment_instruction_id,fee_code,amount,fee_description,fee_version,case_reference,remission_amount,remission_benefiter,remission_authorisation,refund_amount) VALUES (1,1,'X0165',55000,'Filing an application for a divorce, nullity or civil partnership dissolution – fees order 1.2.','1','12345',5000,'sdfsf','dsfsf',null);";
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
        String resetSqlTemplate = getSqlTemplate(applicationContext, PROPERTY_KEY_RESET_SQL_TEMPLATE);
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

    private static String getSqlTemplate(ApplicationContext applicationContext, String key) {
        //Read the SQL template from the properties file
        Environment environment = applicationContext.getBean(Environment.class);
        return environment.getRequiredProperty(key);
    }

    public static void setTestUser(ApplicationContext applicationContext, UserDetails userDteails) throws SQLException {
        DataSource dataSource = applicationContext.getBean(DataSource.class);
        String insertUserSqlTemplate = getSqlTemplate(applicationContext, PROPERTY_KEY_INSERT_USER_SQL_TAMPLATE);
        try (Connection dbConnection = dataSource.getConnection();Statement stmt = dbConnection.createStatement() ) {

            String[] args1 = new String[]{"'John'", "'Doe'",
                "'" + userDteails.getUsername() + "'", "'" + userDteails.getAuthorities().stream().map(Object::toString).collect(Collectors.joining(", "))  + "'"};
            String[] args2 = Arrays.copyOf(args1, args1.length);
            args2[0] = "'Jane'";
            args2[2] = "'4321'";
            String query = "truncate table bar_user;";
            query += String.format(insertUserSqlTemplate, args1);
            query += String.format(insertUserSqlTemplate, args2);
            stmt.executeQuery(query);
        }
    }

    public static void insertPaymentInstructions(ApplicationContext applicationContext) throws SQLException {
        DataSource dataSource = applicationContext.getBean(DataSource.class);
        try (Connection dbConnection = dataSource.getConnection(); Statement stmt = dbConnection.createStatement()) {

            emptyTable(applicationContext, "case_fee_detail");
            emptyTable(applicationContext, "payment_instruction");
            stmt.executeQuery(INSERT_PI_QUERY);
        }
    }

    public static void emptyTable(ApplicationContext applicationContext, String tableName) throws SQLException {
        DataSource dataSource = applicationContext.getBean(DataSource.class);
        try (Connection dbConnection = dataSource.getConnection();Statement stmt = dbConnection.createStatement()) {

            stmt.executeQuery("truncate table " + tableName);
        }
    }
}

