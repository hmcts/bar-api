package uk.gov.hmcts.bar.api.converters;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractGenericHttpMessageConverter;
import uk.gov.hmcts.bar.api.data.model.PaymentInstruction;
import uk.gov.hmcts.bar.api.data.model.PaymentInstructionReportLine;
import uk.gov.hmcts.bar.api.data.utils.Util;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class PaymentInstructionsCsvConverter extends AbstractGenericHttpMessageConverter<List<PaymentInstruction>> {

    public static final String SEPARATOR = ",";
    public static final String EOL = "\n";
    public static final MediaType CSV_MEDIA_TYPE = new MediaType("text", "csv");
    public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat( "0.00" );

    public PaymentInstructionsCsvConverter(){
        super(CSV_MEDIA_TYPE);
    }

    @Override
    public List<PaymentInstruction> read(Type type, Class<?> contextClass, HttpInputMessage inputMessage)  {
        return Collections.emptyList();
    }

    @Override
    protected List<PaymentInstruction> readInternal(Class<? extends List<PaymentInstruction>> clazz, HttpInputMessage inputMessage)  {
        return Collections.emptyList();
    }

    @Override
    public boolean canWrite(Type type, Class<?> clazz, MediaType mediaType) {
        return Collection.class.isAssignableFrom(clazz) &&
            ((ParameterizedType) type).getActualTypeArguments()[0] == PaymentInstruction.class;
    }

    @Override
    protected void writeInternal(List<PaymentInstruction> paymentInstructions, Type type, HttpOutputMessage outputMessage) throws IOException {
        OutputStream outputStream = outputMessage.getBody();
        outputStream.write(convertToCsv(flattenEntity(paymentInstructions)).getBytes());
        outputStream.close();
    }

    private String convertToCsv(List<String[]> data){
        StringBuilder sb = new StringBuilder();
        data.forEach(line -> sb.append(convertLine(line)).append(EOL));
        return sb.toString();
    }

    private List<String[]> flattenEntity(List<PaymentInstruction> paymentInstructions) {
        List<String[]> paymentLines = new ArrayList<>();
        paymentLines.add(PaymentInstruction.CSV_TABLE_HEADER);
        for (PaymentInstruction paymentInstruction : paymentInstructions){
            List<PaymentInstructionReportLine> flattened = paymentInstruction.flattenPaymentInstruction();
            flattened.forEach(paymentInstructionReportLine -> paymentLines.add(convertReportCellToString(paymentInstructionReportLine)));
        }
        return paymentLines;
    }

    private String[] convertReportCellToString(PaymentInstructionReportLine line){
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm:ss");
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
        String[] csvRow = new String[28];
        csvRow[0] = line.getDailyId() == null ? null : line.getDailyId().toString();
        csvRow[1] = Util.getFormattedDateTime(line.getDate(),dateFormatter);
        csvRow[2] = line.getName();
        csvRow[3] = formatNumber(line.getCheckAmount());
        csvRow[4] = formatNumber(line.getPostalOrderAmount());
        csvRow[5] = formatNumber(line.getCashAmount());
        csvRow[6] = formatNumber(line.getCardAmount());
        csvRow[7] = formatNumber(line.getAllPayAmount());
        csvRow[8] = line.getAction();
        csvRow[9] = line.getCaseRef();
        csvRow[10] = line.getBgcNumber();
        csvRow[11] = (formatNumber(line.getFeeAmount()));
        csvRow[12] = line.getFeeCode();
        csvRow[13] = line.getFeeDescription();
        csvRow[14] = formatNumber(line.getRemissionAmount());
        csvRow[15] = line.getRemissionReference();
        csvRow[16] = line.getRecordedUser();
        csvRow[17] = Util.getFormattedDateTime(line.getRecordedTime(),dateTimeFormatter);
        csvRow[18] = line.getValidatedUser();
        csvRow[19] = Util.getFormattedDateTime(line.getValidatedTime(),dateTimeFormatter);
        csvRow[20] = line.getApprovedUser();
        csvRow[21] = Util.getFormattedDateTime(line.getApprovedTime(),dateTimeFormatter);
        csvRow[22] = line.getTransferredToBarUser();
        csvRow[23] = Util.getFormattedDateTime(line.getTransferredToBarTime(),dateTimeFormatter);
        csvRow[24] = (line.getSentToPayhub());
        csvRow[25] = line.getDmUser();
        csvRow[26] = Util.getFormattedDateTime(line.getDtSentToPayhub(),dateTimeFormatter);
       csvRow[27] = Util.getFormattedDateTime(line.getDtTrxReportReporting(),dateTimeFormatter);
        return csvRow;
    }

    private String formatNumber(Integer amount){
        return amount == null ? null : DECIMAL_FORMAT.format(amount / 100d);
    }

    private String convertLine(String[] line){
        return Arrays.stream(line).reduce("", (s, s2) -> s + SEPARATOR + (s2 == null ? "\"\"" : replaceSeparator(s2))).substring(1);
    }

    /**
     * We need to use comma as separator to be able to open correctly in Excel, So we have to double quote the content
     * @param source
     * @return
     */
    private String replaceSeparator(String source){
        return "\"" + source.replaceAll("\"", "\"\"") + "\"";
    }

}
