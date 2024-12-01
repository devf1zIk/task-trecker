package service;

import exception.ManagerSaveException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.file.FileBackedTaskManager;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import static org.junit.jupiter.api.Assertions.*;


public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    private static final String Stroke = "id,type,name,description,status,startTime,duration,endTime,epicId\n";
    private File tempFile;
    private FileBackedTaskManager manager;

    @Override
    protected FileBackedTaskManager createTaskManager() {
        return new FileBackedTaskManager(tempFile);
    }

    @BeforeEach
    void setUp() {
        tempFile = new File("temporary.csv");
        manager = new FileBackedTaskManager(tempFile);
    }

    @Test
    void testLoadFromFileWithInvalidData() throws IOException {
        Files.writeString(tempFile.toPath(), "id,type,name,description,status,startTime,duration,endTime,epicId\nINVALID DATA");

        Exception exception = assertThrows(ManagerSaveException.class, () -> FileBackedTaskManager.loadFromFile(tempFile));
        assertTrue(exception.getMessage().contains("Ошибка с строкой"), "Сообщение об ошибке не соответствует ожиданиям");
    }

}


