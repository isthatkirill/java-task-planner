package tracker.controllers;

import tracker.interfaces.HistoryManager;
import tracker.model.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryHistoryManager implements HistoryManager {

    private ArrayList<Task> history = new ArrayList<>();
    private CustomLinkedList<Task> customList = new CustomLinkedList<>();
    private HashMap<Integer, Node> nodesMap = new HashMap<>();

    @Override
    public ArrayList<Task> getHistory() {
        return customList.getTasks();
    }

    @Override
    public void remove(int id) {
        customList.removeNode(nodesMap.get(id));
        nodesMap.remove(id);
    }

    @Override
    public void add(Task task) {
        if (nodesMap.containsKey(task.getId())) {
            customList.removeNode(nodesMap.get(task.getId()));
            nodesMap.remove(task.getId());
        }
        nodesMap.put(task.getId(), customList.linkLast(task));

    }

    public static class CustomLinkedList<T> {

        private Node head;
        private Node tail;
        private int size = 0;

        public Node linkLast(T element) {
            Node<T> newNode = new Node<T>(element);

            if(head == null) {
                head = tail = newNode;
                head.prev = null;
                tail.next = null;
            } else {
                tail.next = newNode;
                newNode.prev = tail;
                tail = newNode;
                tail.next = null;
            }
            return tail;
        }

        public ArrayList<Task> getTasks() {
            ArrayList<Task> history = new ArrayList<>();
            Node<T> ptr = head;

            while (ptr != null) {
                history.add((Task) ptr.data);
                ptr = ptr.next;

            }
            return history;
        }

        public void removeNode(Node<T> node)
        {
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





