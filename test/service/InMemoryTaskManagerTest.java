package service;

import org.junit.jupiter.api.BeforeEach;
import service.managers.InMemoryTaskManager;

import java.io.IOException;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    @BeforeEach
    void setUp() throws IOException {
        taskManager = new InMemoryTaskManager();
        super.setUp();
    }

}



