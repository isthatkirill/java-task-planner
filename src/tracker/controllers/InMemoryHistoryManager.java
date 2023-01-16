package tracker.controllers;

import tracker.interfaces.HistoryManager;
import tracker.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class InMemoryHistoryManager implements HistoryManager {

    private ArrayList<Task> viewedTasks = new ArrayList<>();
    private CustomLinkedList<Task> taskCustomLinkedList = new CustomLinkedList<>();
    private HashMap<Integer, Node> nodesMap = new HashMap<>();

    @Override
    public ArrayList<Task> getHistory() {
        return taskCustomLinkedList.getTasks();
    }

    @Override
    public void remove(int id) {
        for (Task task : viewedTasks) {
            if (task.getId() == id) {
                viewedTasks.remove(task);
            }
        }
    }

    @Override
    public void add(Task task) {
        taskCustomLinkedList.linkLast(task);

    }

    public static class CustomLinkedList<T> {

        private Node<T> head;
        private Node<T> tail;
        private int size = 0;

        public void linkLast(T element) {
            Node temp = new Node(element, null);
            if (tail == null) {
                head = tail = temp;
            } else {
                tail.next = temp;
                tail = temp;
            }
            size++;
        }

        public ArrayList<Task> getTasks() {
            ArrayList<Task> listToReturn = new ArrayList<>();
            CustomLinkedListIterator iterator = new CustomLinkedListIterator(head);

            while(iterator.hasNext()) {
                listToReturn.add((Task) iterator.next());
            }
            return listToReturn;

        }

        class CustomLinkedListIterator implements Iterator<T> {
            Node curr;

            public CustomLinkedListIterator(Node head) {
                curr = head;
            }

            @Override
            public boolean hasNext() {
                return curr != null;
            }

            @Override
            public T next() {
                T result = (T) curr.data;
                curr = curr.next;
                return result;
            }
        }

        public void removeNode(Node del)
        {
            if (head == null || del == null) {
                return;
            }
            if (head == del) {
                head = del.next;
            }
            if (del.next != null) {
                del.next.prev = del.prev;
            }
            if (del.prev != null) {
                del.prev.next = del.next;
            }
            return;
        }

    }

}





