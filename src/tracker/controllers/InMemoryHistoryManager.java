package tracker.controllers;

import tracker.interfaces.HistoryManager;
import tracker.model.Epic;
import tracker.model.SubTask;
import tracker.model.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryHistoryManager implements HistoryManager {

    private final CustomLinkedList<Task> customList = new CustomLinkedList<>();

    @Override
    public ArrayList<Task> getHistory() {
        return customList.getTasks();
    }

    @Override
    public void remove(int id) {
        if (customList.getNodesMap().containsKey(id)) {
            customList.removeNode(customList.getNodesMap().get(id));
        }
    }

    @Override
    public void add(Task task) {
        customList.linkLast(task);
    }

    public class CustomLinkedList<T extends Task> {

        private Node<T> head;
        private Node<T> tail;

        private final HashMap<Integer, Node<T>> nodesMap = new HashMap<>();

        public HashMap<Integer, Node<T>> getNodesMap() {
            return nodesMap;
        }

        public void linkLast(T task) {

            if (nodesMap.containsKey(task.getId())) {
                removeNode(nodesMap.get(task.getId()));
                nodesMap.remove(task.getId());
            }

            Node<T> newNode = new Node<>(task);

            if (head == null) {
                head = tail = newNode;
                head.prev = null;
                tail.next = null;
            } else {
                tail.next = newNode;
                newNode.prev = tail;
                tail = newNode;
                tail.next = null;
            }
            nodesMap.put(task.getId(), tail);
        }

        public ArrayList<T> getTasks() {
            ArrayList<T> history = new ArrayList<>();
            Node<T> currentTask = head;

            while (currentTask != null) {
                history.add(currentTask.data);
                currentTask = currentTask.next;

            }
            return history;
        }

        public void removeNode(Node<T> node) {
            nodesMap.remove(node.data.getId());

            if (node.data.getClass() == Epic.class) {
                Epic epic = (Epic) node.data;
                for (SubTask subTask : epic.getTaskList()) {
                    if (nodesMap.get(subTask.getId()) != null) {
                        removeNode(nodesMap.get(subTask.getId()));
                    }
                }
            }

            if (node.prev == null) {
                head = node.next;
            } else {
                node.prev.next = node.next;
            }

            if (node.next == null) {
                tail = node.prev;
            } else {
                node.next.prev = node.prev;
            }
        }
    }
}





