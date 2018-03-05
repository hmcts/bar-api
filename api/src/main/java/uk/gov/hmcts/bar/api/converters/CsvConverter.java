package uk.gov.hmcts.bar.api.converters;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

public abstract class CsvConverter<T> extends AbstractHttpMessageConverter<T> {

    public static final String SEPARATOR = ";";
    public static final String EOL = "\n";

    public CsvConverter(){
        super(new MediaType("text", "csv"));
    }

    @Override
    protected T readInternal(Class<? extends T> clazz, HttpInputMessage inputMessage) throws HttpMessageNotReadableException {
        return null;
    }

    @Override
    protected void writeInternal(T entity, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        OutputStream outputStream = outputMessage.getBody();
        outputStream.write(convertToCsv(flattenEntity(entity)).getBytes());
        outputStream.close();
    }

    abstract List<String[]> flattenEntity(T entity);

    private String convertToCsv(List<String[]> data){
        StringBuilder sb = new StringBuilder();
        data.forEach(line -> sb.append(convertLine(line)).append(EOL));
        return sb.toString();
    }

    private String convertLine(String[] line){
        return Arrays.stream(line).reduce("", (s, s2) -> s + SEPARATOR + (s2 == null ? "" : s2)).substring(1);
    }
}
