import tracker.controllers.FileBackedTasksManager;

import java.io.File;

public class Main {

    public static void main(String[] args) {
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager("resources/data.csv");
        FileBackedTasksManager fromFileManager = FileBackedTasksManager.loadFromFile(new File("resources/data.csv"));
        System.out.println(fromFileManager);
        System.out.println(fileBackedTasksManager);
    }
}
