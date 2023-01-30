package tracker.controllers;

import tracker.interfaces.HistoryManager;
import tracker.model.Epic;
import tracker.model.SubTask;
import tracker.model.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryHistoryManager implements HistoryManager {

    private ArrayList<Task> history = new ArrayList<>();
    private CustomLinkedList<Task> customList = new CustomLinkedList<>();

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

    public class CustomLinkedList<T> {

        private Node head;
        private Node tail;
        private int size = 0;

        private HashMap<Integer, Node<T>> nodesMap = new HashMap<>();

        public HashMap<Integer, Node<T>> getNodesMap() {
            return nodesMap;
        }

        public void linkLast(Task task) {

            if (nodesMap.containsKey(task.getId())) {
                removeNode(nodesMap.get(task.getId()));
                nodesMap.remove(task.getId());
            }

            Node<Task> newNode = new Node<>(task);

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
            size++;
            nodesMap.put(task.getId(), tail);
        }

        public ArrayList<Task> getTasks() {
            ArrayList<Task> history = new ArrayList<>();
            Node currentTask = head;

            while (currentTask != null) {
                history.add((Task) currentTask.data);
                currentTask = currentTask.next;

            }
            return history;
        }

        public void removeNode(Node<T> node) {
            nodesMap.remove(node);

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





