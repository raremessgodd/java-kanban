package http;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import exceptions.EqualTimeException;
import managers.Managers;
import managers.TaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;

public class HttpTaskServer {
    public static final int PORT = 8090;
    private final HttpServer server;
    private final TaskManager manager;
    private static final Gson gson = new Gson();
    public static final Charset UTF_8 = StandardCharsets.UTF_8;
    public enum Endpoint {
        DELETE_EPIC, DELETE_EPIC_ID, DELETE_SUBTASK,
        DELETE_SUBTASK_ID, DELETE_TASK, DELETE_TASK_ID, GET_EPIC, GET_EPIC_ID,
        GET_EPIC_SUBTASKS, GET_HISTORY, GET_PRIORITIZED_TASKS, GET_SUBTASK, GET_SUBTASK_ID,
        GET_TASK, GET_TASK_ID, POST_EPIC, POST_SUBTASK, POST_TASK, UNKNOWN
    }

    public HttpTaskServer() throws IOException {
        server = HttpServer.create();
        server.bind(new InetSocketAddress("localhost", PORT), 0);
        server.createContext("/tasks", new TaskHandler());
        manager = Managers.getDefault();
    }

    public void start() {
        server.start();
    }

    public void stop() {
        server.stop(0);
    }

    class TaskHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().toString();
            String method = exchange.getRequestMethod();
            Endpoint endpoint = getEndpoint(path, method);
            switch (endpoint) {
                case GET_HISTORY:
                    handleGetHistory(exchange);
                    return;
                case GET_PRIORITIZED_TASKS:
                    handleGetPrioritizedTasks(exchange);
                    return;
                case GET_TASK:
                    handleGetAllTasks(exchange);
                    return;
                case POST_TASK:
                    handlePostTask(exchange);
                    return;
                case DELETE_TASK:
                    handleDeleteAllTasks(exchange);
                    return;
                case GET_TASK_ID:
                    handleGetTaskById(exchange);
                    return;
                case DELETE_TASK_ID:
                    handleDeleteTaskById(exchange);
                    return;
                case GET_EPIC:
                    handleGetAllEpics(exchange);
                    return;
                case POST_EPIC:
                    handlePostEpic(exchange);
                    return;
                case DELETE_EPIC:
                    handleDeleteAllEpics(exchange);
                    return;
                case GET_EPIC_ID:
                    handleGetEpicById(exchange);
                    return;
                case DELETE_EPIC_ID:
                    handleDeleteEpicById(exchange);
                    return;
                case GET_EPIC_SUBTASKS:
                    handleGetEpicSubtasks(exchange);
                    return;
                case GET_SUBTASK:
                    handleGetAllSubtasks(exchange);
                    return;
                case POST_SUBTASK:
                    handlePostSubtask(exchange);
                    return;
                case DELETE_SUBTASK:
                    handleDeleteAllSubtasks(exchange);
                    return;
                case GET_SUBTASK_ID:
                    handleGetSubtaskById(exchange);
                    return;
                case DELETE_SUBTASK_ID:
                    handleDeleteSubtaskById(exchange);
                    return;
                default:
                    sendResponse(exchange, 400, "Такого эндпоинта пока не существует.");
            }
        }

        private Endpoint getEndpoint(String requestPath, String requestMethod) {
            String[] pathParts = requestPath.split("/");
            if (pathParts.length == 2 && pathParts[1].equals("tasks") && requestMethod.equals("GET")) {
                return Endpoint.GET_PRIORITIZED_TASKS;
            }
            if (pathParts.length == 3 && pathParts[1].equals("tasks")) {
                if (pathParts[2].equals("task")) {
                    switch (requestMethod) {
                        case "GET":
                            return Endpoint.GET_TASK;
                        case "POST":
                            return Endpoint.POST_TASK;
                        case "DELETE":
                            return Endpoint.DELETE_TASK;
                    }
                }
                if (pathParts[2].equals("epic")) {
                    switch (requestMethod) {
                        case "GET":
                            return Endpoint.GET_EPIC;
                        case "POST":
                            return Endpoint.POST_EPIC;
                        case "DELETE":
                            return Endpoint.DELETE_EPIC;
                    }
                }
                if (pathParts[2].equals("subtask")) {
                    switch (requestMethod) {
                        case "GET":
                            return Endpoint.GET_SUBTASK;
                        case "POST":
                            return Endpoint.POST_SUBTASK;
                        case "DELETE":
                            return Endpoint.DELETE_SUBTASK;
                    }
                }
                if (pathParts[2].equals("history")) {
                    return Endpoint.GET_HISTORY;
                }
            }
            if (pathParts.length == 3 && pathParts[1].equals("tasks") && pathParts[2].contains("?")) {
                String[] parts = pathParts[2].split("\\?");
                if (parts[0].equals("task")) {
                    switch (requestMethod) {
                        case "GET":
                            return Endpoint.GET_TASK_ID;
                        case "DELETE":
                            return Endpoint.DELETE_TASK_ID;
                    }
                }
                if (parts[0].equals("epic")) {
                    switch (requestMethod) {
                        case "GET":
                            return Endpoint.GET_EPIC_ID;
                        case "DELETE":
                            return Endpoint.DELETE_EPIC_ID;
                    }
                }
                if (parts[0].equals("subtask")) {
                    switch (requestMethod) {
                        case "GET":
                            return Endpoint.GET_SUBTASK_ID;
                        case "DELETE":
                            return Endpoint.DELETE_SUBTASK_ID;
                    }
                }
            }
            if (pathParts.length == 4 && pathParts[1].equals("tasks")
                    && pathParts[2].equals("epic") && pathParts[3].contains("?")) {
                String[] parts = pathParts[2].split("\\?");
                if (parts[0].equals("epic")) {
                    return Endpoint.GET_EPIC_SUBTASKS;
                }
            }
            return Endpoint.UNKNOWN;
        }

        private Map<String, String> readQuery(HttpExchange exchange) {
            Map<String, String> queryMap = new HashMap<>();
            String[] query = exchange.getRequestURI().getRawQuery().split("&");
            for (String s: query) {
                String key = s.substring(0, s.indexOf("="));
                String value = s.substring(s.indexOf("=") + 1);
                queryMap.put(key, value);
            }
            return queryMap;
        }

        private void sendResponse(HttpExchange exchange, int rCode, String text) throws IOException {
            try (OutputStream stream = exchange.getResponseBody()){
                byte[] resp = text.getBytes(UTF_8);
                exchange.getResponseHeaders().add("Content-Type", "application/json");
                exchange.sendResponseHeaders(rCode, resp.length);
                stream.write(resp);
            } finally {
                exchange.close();
            }
        }

        private void handleGetHistory(HttpExchange exchange) throws IOException {
            ArrayList<Task> history = manager.getHistory();
            if (!history.isEmpty()) {
                sendResponse(exchange, 200, gson.toJson(history));
            } else {
                sendResponse(exchange, 200, "История пуста.");
            }
        }

        private void handleGetPrioritizedTasks(HttpExchange exchange) throws IOException {
            Set<Task> prioritizedTasks = manager.getPrioritizedTasks();
            if (!prioritizedTasks.isEmpty()) {
                sendResponse(exchange, 200, gson.toJson(prioritizedTasks));
            } else {
                sendResponse(exchange, 200, "Список задач пуст.");
            }
        }

        private void handleDeleteAllTasks(HttpExchange exchange) throws IOException {
            manager.deleteAllTasks();
            sendResponse(exchange, 200, "Все задачи удалены.");
        }

        private void handleGetAllTasks(HttpExchange exchange) throws IOException {
            ArrayList<Task> allTasks = manager.getAllTasks();
            if (!allTasks.isEmpty()) {
                sendResponse(exchange, 200, gson.toJson(allTasks));
            } else {
                sendResponse(exchange, 200, "Список задач пуст.");
            }
        }

        private void handlePostTask(HttpExchange exchange) throws IOException {
            try (InputStream stream = exchange.getRequestBody()) {
                String body = new String(stream.readAllBytes(), UTF_8);

                Task task = gson.fromJson(body, Task.class);
                if (checkTaskFromJson(task)) {
                    sendResponse(exchange, 400, "Не заполнены обязательные поля для задачи.");
                    return;
                }

                boolean flag = false;
                for (Task other: manager.getAllTasks()) {
                    if (task.getTaskId() == other.getTaskId()) {
                        flag = true;
                        break;
                    }
                }

                if (flag) {
                    try {
                        manager.updateTask(task, task.getTaskId());
                        sendResponse(exchange, 200, "Задача под id-" + task.getTaskId() + " обновлена.");
                    } catch (EqualTimeException | NullPointerException e) {
                        sendResponse(exchange, 400, e.getMessage());
                    }
                } else if (task.getTaskId() == 0) {
                    try {
                        manager.createTask(task);
                        sendResponse(exchange, 200, "Задача добавлена.");
                    } catch (EqualTimeException e) {
                        sendResponse(exchange, 400, e.getMessage());
                    }
                } else {
                    sendResponse(exchange, 400, "В теле запроса передан несуществующий id. Для создания новой задачи введите id=0.");
                }
            } catch (JsonSyntaxException e) {
                sendResponse(exchange, 400, "Получен некорректный JSON");
            }
        }

        private void handleDeleteTaskById(HttpExchange exchange) throws IOException {
            Map<String, String> queryMap = readQuery(exchange);
            if (queryMap.containsKey("id")) {
                try {
                    int id = Integer.parseInt(queryMap.get("id"));
                    manager.deleteTaskById(id);
                    sendResponse(exchange, 200, "Задача удалена.");
                } catch (NumberFormatException e) {
                    sendResponse(exchange, 400, "Некорректный идентификатор задачи.");
                }
            } else {
                sendResponse(exchange, 404, "Неккоректный запрос.");
            }
        }

        private void handleGetTaskById(HttpExchange exchange) throws IOException {
            Map<String, String> queryMap = readQuery(exchange);
            if (queryMap.containsKey("id")) {
                try {
                    int id = Integer.parseInt(queryMap.get("id"));
                    Task task = null;

                    for (Task other: manager.getAllTasks()) {
                        if (other.getTaskId() == id) {
                            task = manager.getTaskById(id);
                        }
                    }

                    if (task != null) {
                        String jsonTask = gson.toJson(task);
                        sendResponse(exchange, 200, jsonTask);
                    } else {
                        sendResponse(exchange, 404, "Задача не найдена.");
                    }
                } catch (NumberFormatException e) {
                    sendResponse(exchange, 400, "Некорректный идентификатор задачи.");
                }
            } else {
                sendResponse(exchange, 404, "Неккоректный запрос.");
            }
        }

        private void handleGetEpicSubtasks(HttpExchange exchange) throws IOException {
            Map<String, String> queryMap = readQuery(exchange);
            if (queryMap.containsKey("id")) {
                try {
                    int id = Integer.parseInt(queryMap.get("id"));
                    if (manager.getEpicById(id) != null) {
                        Epic epic = manager.getEpicById(id);
                        ArrayList<Subtask> subtasks = manager.getEpicSubtasks(epic.getTaskId());
                        if (!subtasks.isEmpty()) {
                            sendResponse(exchange, 200, gson.toJson(subtasks));
                        } else {
                            sendResponse(exchange, 200, "У этого эпика пока нет подзадач.");
                        }
                    } else {
                        sendResponse(exchange, 404, "Эпик не найден.");
                    }
                } catch (NumberFormatException e) {
                    sendResponse(exchange, 400, "Некорректный идентификатор задачи.");
                }
            } else {
                sendResponse(exchange, 404, "Неккоректный запрос.");
            }
        }

        private void handleDeleteAllEpics(HttpExchange exchange) throws IOException {
            manager.deleteAllEpics();
            sendResponse(exchange, 200, "Все эпики удалены.");
        }

        private void handleGetAllEpics(HttpExchange exchange) throws IOException {
            ArrayList<Epic> allEpics = manager.getAllEpics();
            if (!allEpics.isEmpty()) {
                sendResponse(exchange, 200, gson.toJson(allEpics));
            } else {
                sendResponse(exchange, 200, "Список эпиков пуст.");
            }
        }

        private void handlePostEpic(HttpExchange exchange) throws IOException {
            try (InputStream stream = exchange.getRequestBody()) {
                String body = new String(stream.readAllBytes(), UTF_8);

                Epic epic = gson.fromJson(body, Epic.class);
                if (checkEpicFromJson(epic)) {
                    sendResponse(exchange, 400, "Не заполнены обязательные или " +
                            "заполнены недопустимые поля для эпика.");
                    return;
                }

                boolean flag = false;
                for (Epic other: manager.getAllEpics()) {
                    if (epic.getTaskId() == other.getTaskId()) {
                        flag = true;
                        break;
                    }
                }

                if (flag) {
                    manager.updateEpic(epic, epic.getTaskId());
                    sendResponse(exchange, 200, "Эпик под id-" + epic.getTaskId() + " обновлен.");
                } else if (epic.getTaskId() == 0) {
                    manager.createEpic(epic);
                    sendResponse(exchange, 200, "Эпик добавлен.");
                } else {
                    sendResponse(exchange, 400, "В теле запроса передан несуществующий id." +
                            " Для создания нового эпика введите id=0.");
                }
            } catch (JsonSyntaxException e) {
                sendResponse(exchange, 400, "Получен некорректный JSON");
            }
        }

        private void handleDeleteEpicById(HttpExchange exchange) throws IOException {
            Map<String, String> queryMap = readQuery(exchange);
            if (queryMap.containsKey("id")) {
                try {
                    int id = Integer.parseInt(queryMap.get("id"));
                    manager.deleteEpicById(id);
                    sendResponse(exchange, 200, "Эпик удален.");
                } catch (NumberFormatException e) {
                    sendResponse(exchange, 400, "Некорректный идентификатор эпика.");
                }
            } else {
                sendResponse(exchange, 404, "Неккоректный запрос.");
            }
        }

        private void handleGetEpicById(HttpExchange exchange) throws IOException {
            Map<String, String> queryMap = readQuery(exchange);
            if (queryMap.containsKey("id")) {
                try {
                    int id = Integer.parseInt(queryMap.get("id"));
                    Epic epic = null;

                    for (Epic other: manager.getAllEpics()) {
                        if (other.getTaskId() == id) {
                            epic = manager.getEpicById(id);
                        }
                    }

                    if (epic != null) {
                        String jsonSubtask = gson.toJson(epic);
                        sendResponse(exchange, 200, jsonSubtask);
                    } else {
                        sendResponse(exchange, 404, "Эпик не найден.");
                    }
                } catch (NumberFormatException e) {
                    sendResponse(exchange, 400, "Некорректный идентификатор эпика.");
                }
            } else {
                sendResponse(exchange, 404, "Неккоректный запрос.");
            }
        }

        private void handleDeleteAllSubtasks(HttpExchange exchange) throws IOException {
            manager.deleteAllSubtasks();
            sendResponse(exchange, 200, "Все подзадачи удалены.");
        }

        private void handleGetAllSubtasks(HttpExchange exchange) throws IOException {
            ArrayList<Subtask> allSubtasks = manager.getAllSubtasks();
            if (!allSubtasks.isEmpty()) {
                sendResponse(exchange, 200, gson.toJson(allSubtasks));
            } else {
                sendResponse(exchange, 200, "Список подзадач пуст.");
            }
        }

        private void handlePostSubtask(HttpExchange exchange) throws IOException {
            try (InputStream stream = exchange.getRequestBody()) {
                String body = new String(stream.readAllBytes(), UTF_8);

                Subtask subtask = gson.fromJson(body, Subtask.class);
                if (checkSubtaskFromJson(subtask)) {
                    sendResponse(exchange, 400, "Не заполнены обязательные поля для подзадачи.");
                    return;
                }

                if (checkSubtaskEpicFromJson(subtask)) {
                    sendResponse(exchange, 400, "Задача привязана к несуществующему эпику.");
                    return;
                }

                boolean flag = false;
                for (Subtask other: manager.getAllSubtasks()) {
                    if (subtask.getTaskId() == other.getTaskId()) {
                        flag = true;
                        break;
                    }
                }

                if (flag) {
                    try {
                        manager.updateSubtask(subtask, subtask.getTaskId());
                        sendResponse(exchange, 200, "Подзадача под id-" + subtask.getTaskId() + " обновлена.");
                    } catch (EqualTimeException e) {
                        sendResponse(exchange, 400, e.getMessage());
                    }
                } else if (subtask.getTaskId() == 0) {
                    try {
                        manager.createSubtask(subtask);
                        sendResponse(exchange, 200, "Подзадача добавлена.");
                    } catch (EqualTimeException e) {
                        sendResponse(exchange, 400, e.getMessage());
                    }
                } else {
                    sendResponse(exchange, 400, "В теле запроса передан несуществующий id." +
                            " Для создания новой подзадачи введите id=0.");
                }
            } catch (JsonSyntaxException e) {
                sendResponse(exchange, 400, "Получен некорректный JSON");
            }
        }

        private void handleDeleteSubtaskById(HttpExchange exchange) throws IOException {
            Map<String, String> queryMap = readQuery(exchange);
            if (queryMap.containsKey("id")) {
                try {
                    int id = Integer.parseInt(queryMap.get("id"));
                    manager.deleteSubtaskById(id);
                    sendResponse(exchange, 200, "Подзадача удалена.");
                } catch (NumberFormatException e) {
                    sendResponse(exchange, 400, "Некорректный идентификатор подзадачи.");
                }
            } else {
                sendResponse(exchange, 404, "Неккоректный запрос.");
            }
        }

        private void handleGetSubtaskById(HttpExchange exchange) throws IOException {
            Map<String, String> queryMap = readQuery(exchange);
            if (queryMap.containsKey("id")) {
                try {
                    int id = Integer.parseInt(queryMap.get("id"));
                    Subtask subtask = null;

                    for (Subtask other: manager.getAllSubtasks()) {
                        if (other.getTaskId() == id) {
                            subtask = manager.getSubtaskById(id);
                        }
                    }

                    if (subtask != null) {
                        String jsonSubtask = gson.toJson(subtask);
                        sendResponse(exchange, 200, jsonSubtask);
                    } else {
                        sendResponse(exchange, 404, "Подзадача не найдена.");
                    }
                } catch (NumberFormatException e) {
                    sendResponse(exchange, 400, "Некорректный идентификатор подзадачи.");
                }
            } else {
                sendResponse(exchange, 404, "Неккоректный запрос.");
            }
        }

        private boolean checkTaskFromJson(Task task) {
            return task.getName() == null || task.getStartTime() == null ||
                    task.getDuration() == null || task.getStatus() == null;
        }

        private boolean checkSubtaskFromJson(Subtask subtask) {
            return checkTaskFromJson(subtask) || subtask.getEpicId() == 0;
        }

        private boolean checkSubtaskEpicFromJson(Subtask subtask) {
            ArrayList<Epic> allEpics = manager.getAllEpics();
            if (!allEpics.isEmpty()) {
                for (Epic epic: allEpics) {
                    if (epic.getTaskId() == subtask.getEpicId()) {
                        return false;
                    }
                }
            }
            return true;
        }

        private boolean checkEpicFromJson(Epic epic) {
            return epic.getName() == null || epic.getStatus() != null ||
                    epic.getStartTime() != null || !epic.getDuration().equals(Duration.ZERO) ||
                    !epic.getSubtasks().isEmpty();
        }
    }
}
