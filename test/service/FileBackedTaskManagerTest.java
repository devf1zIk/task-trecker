package service;

import org.junit.jupiter.api.BeforeEach;
import service.file.FileBackedTaskManager;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;


public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    private static final String Filepath = "tasks.csv";


    @BeforeEach
    void setUp() throws IOException {
        Path filePath = Paths.get(Filepath);
        taskManager = new FileBackedTaskManager(filePath.toFile());
        super.setUp();
    }
}


