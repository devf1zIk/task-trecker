package service.adapters;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.time.Duration;

public class DurationAdapter extends TypeAdapter<Duration> {
    @Override
    public void write(final JsonWriter jsonWriter, final Duration duration) throws IOException {
        if (duration != null) {
            jsonWriter.value(duration.toString());
        } else {
            jsonWriter.nullValue();
        }
    }

    @Override
    public Duration read(final JsonReader jsonReader) throws IOException {
        String durationString = jsonReader.nextString();
        if (durationString == null || durationString.equals("null")) {
            return null;
        }
        try {
            return Duration.parse(durationString);
        } catch (Exception e) {
            throw new IOException("Invalid duration format: " + durationString, e);
        }
    }
}
