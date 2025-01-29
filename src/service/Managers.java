package service;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import service.adapters.DurationAdapter;
import service.adapters.LocalDateTimeAdapter;
import service.file.FileBackedTaskManager;
import service.managers.HistoryManager;
import service.managers.InMemoryHistoryManager;
import service.managers.InMemoryTaskManager;
import service.managers.TaskManager;
import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;

public class Managers {

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static TaskManager getFileBackedTaskManager(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            throw new IllegalArgumentException("Имя файла не должно быть пустым или null");
        }
        return new FileBackedTaskManager(new File(fileName));
    }

    public static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.serializeNulls();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
        gsonBuilder.registerTypeAdapter(Duration.class, new DurationAdapter());
        return gsonBuilder.create();
    }
}
