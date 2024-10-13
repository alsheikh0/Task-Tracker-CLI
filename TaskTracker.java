import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.nio.file.*;
import java.nio.file.Files;
import java.io.IOException;
import java.io.FileWriter;
import java.io.File;
import java.util.HashSet;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;

public class TaskTracker {
    private static final String FILE_PATH = "tasks.json";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final Random random = new Random();
    private static final HashSet<Integer> usedIds = new HashSet<>();


    public static void main(String args[]) {
        System.out.println("Task Tracker Application: ");
        while (true) {
            Scanner scan = new Scanner(System.in);
            System.out.println("Enter any command to proceed: ");
            System.out.println("-> 'add' to Add tasks");
            System.out.println("-> 'update' to Update the any task");
            System.out.println("-> 'remove' to remove any task from the list");
            System.out.println("-> 'list' to list all tasks in");
            System.out.println("-> 'exit' to Exit");
            String choice = scan.nextLine();
            

            switch (choice) {
                case "add":
                    System.out.println("Enter the task description");
                    String description = scan.nextLine();
                    addTask(description);
                    break;
                case "update":
                    System.out.println("Enter task ID to update");
                    String taskId = scan.nextLine();
                    System.out.println("Enter new status (eg, Pending, In-Progress, Completed)");
                    String status = scan.nextLine();
                    updateTask(taskId, status);
                    break;
                case "remove":
                    System.out.println("Enter task Id to remove: ");
                    String removeTaskId = scan.nextLine();
                    removeTask(removeTaskId);
                    break;
                case "list":
                    System.out.println("All available Tasks");
                    listTasks();
                    break;
                case "exit":
                    return;
                default:
                    System.out.println("Invalid command, please use one from the given.");

            }
            

        }
        

    }

    private static void addTask(String description) {
        try {
            JSONArray tasks = loadTasks();
            int id = generateUniqueTaskId();
            String createdAt = LocalDateTime.now().format(FORMATTER);

            JSONObject newTask = new JSONObject();
            newTask.put("id", String.format("%04d", id));
            newTask.put("description", description);
            newTask.put("status", "Pending");
            newTask.put("createdAt", createdAt);
            newTask.put("updatedAt", createdAt);

            tasks.put(newTask);
            saveTasks(tasks);
            System.out.println("Task added successfully with ID " + id);
        } catch (IOException e) {
            System.out.println("Issue adding task" + e.getMessage());
        }

    }

    private static void listTasks() {
        try {
            JSONArray tasks = loadTasks();
            if (tasks.length() == 0) {
                System.out.println("There are no tasks avaiable");
            } else {
                for (int i = 0; i < tasks.length(); i++) {
                    JSONObject task = tasks.getJSONObject(i);
                    System.out.println("Task: " + task.getString("description"));
                    System.out.println("Id: " + task.getString("id"));
                    System.out.println("Status: " + task.getString("status"));
                    System.out.println("Created: " + task.getString("createdAt"));
                    System.out.println("Updated: " + task.getString("updatedAt"));
                    System.out.println();
                }
            }
        } catch (IOException e) {
            System.out.println("cannot load tasks " + e.getMessage());
        }
    }

    private static void updateTask(String id, String newStatus) {
        try {
            JSONArray tasks = loadTasks();
            boolean taskFound = false;

            for (int i = 0; i < tasks.length(); i++) {
                JSONObject task = tasks.getJSONObject(i);
                if (task.getString("id").equals(id)) {
                    task.put("status", newStatus);
                    task.put("updatedAt", LocalDateTime.now().format(FORMATTER));
                    taskFound = true;
                    break;
                }

                if (taskFound) {
                    saveTasks(tasks);
                    System.out.println("Task updated Successfully");
                } else
                    System.out.println("Task not found");
            }
        } catch (IOException e) {
            System.out.println("Task cannot be updated " + e.getMessage());
        }
    }

    private static void removeTask(String id) {
        try {
            JSONArray tasks = loadTasks();
            boolean taskFound = false;

            for (int i = 0; i < tasks.length(); i++) {
                JSONObject task = tasks.getJSONObject(i);
                if (task.getString("id").equals(id)) {
                    tasks.remove(i);
                    taskFound = true;
                    break;
                }

            }
            if (taskFound) {
                saveTasks(tasks);
                System.out.println("Task removed successfully");
            } else
                System.out.println("Task not found");
        } catch (IOException e) {
            System.out.println("Error removing task" + e.getMessage());
        }

    }
    private static int generateUniqueTaskId() {
        int id;
        do {
            id = random.nextInt(9000)+1000;
        } while (usedIds.contains(id));
        usedIds.add(id);
        return id;
    }

    private static JSONArray loadTasks() throws IOException {
        File file = new File(FILE_PATH);
        if(!file.exists()) {
            return new JSONArray();
        }
        String content = new String(Files.readAllBytes(Paths.get(FILE_PATH)));
        return new JSONArray(content);
    }

    private static void saveTasks(JSONArray tasks) throws IOException {
        try (FileWriter file = new FileWriter(FILE_PATH)){
            file.write(tasks.toString(4));
        }
    }

}