package service;

import org.junit.jupiter.api.BeforeEach;
import service.file.FileBackedTaskManager;
import java.io.File;
import java.io.IOException;

public class FileBackedTaskManagerTest {

    private File tempFile;
    private FileBackedTaskManager manager;

    @BeforeEach
    void setUp() throws IOException {
        tempFile = File.createTempFile("tasks", ".csv");
        manager = new FileBackedTaskManager(tempFile);
    }

}

