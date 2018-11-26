/* The following sql statements when executed clears all payment_instruction related data except reference data
 *
 *  1. TRUNCATE TABLE payment_instruction RESTART IDENTITY CASCADE; (clears data in payment_instruction,case_fee_detail,
 *      payment_instruction_status and resets sequences if any in all the tables)
 *
 *  2. TRUNCATE TABLE bank_giro_credit CASCADE ; (clears data in bank_giro_credit)
 *
 *  3. TRUNCATE TABLE payment_reference; (clears data in payment_reference)
 */
TRUNCATE TABLE payment_instruction RESTART IDENTITY CASCADE;
TRUNCATE TABLE bank_giro_credit CASCADE ;
TRUNCATE TABLE payment_reference;
