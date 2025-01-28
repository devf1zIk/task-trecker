package service.adapters;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @Override
    public void write(final JsonWriter jsonWriter, final LocalDateTime localDateTime) throws IOException {
        Optional<LocalDateTime> localDateTimeOptional = Optional.ofNullable(localDateTime);
        if (localDateTimeOptional.isPresent()) {
            jsonWriter.value(FORMATTER.format(localDateTimeOptional.get()));
        } else {
            jsonWriter.nullValue();
        }
    }

    @Override
    public LocalDateTime read(final JsonReader jsonReader) throws IOException {
        try {
            String value = jsonReader.nextString();
            if (value.equals("null")) {
                return null;
            }
            return LocalDateTime.parse(value, FORMATTER);
        } catch (IllegalArgumentException ex) {
            jsonReader.nextNull();
            return null;
        }
    }
}
