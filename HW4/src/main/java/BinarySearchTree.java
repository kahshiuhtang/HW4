import com.sun.source.tree.Tree;

import java.util.*;
public class BinarySearchTree<T extends Comparable<T>> implements Iterable<T>{
    final Lock lock = new Lock();
    class Lock{
        T value1;
        T value2;
        boolean noMoreFromFirst = false;
        boolean noMoreFromSecond = false;
        public Lock(){
            value1 = null;
            value2 = null;
        }

    }
    class Node<T>{
        T value;
        Node<T> left;
        Node<T> right;
        public String toString(){
            return value.toString();
        }
    }

    private class TreeIterator<T> implements Iterator<T>{
        Stack<Node<T>> order;

        TreeIterator( Node<T> r){
            order = new Stack<Node<T>>();
            move(r);
        }
        private void move(Node curr){
            Node<T> ptr = curr;
            while(ptr != null){
                order.push(ptr);
                ptr = ptr.left;
            }
        }
        @Override
        public boolean hasNext() {
            return !order.isEmpty();
        }

        @Override
        public T next() {
            if(!hasNext()){
                return null;
            }
            Node<T> temp = order.pop();
            if(temp.right != null)
                move(temp.right);
            return temp.value;
        }
        public String toString(){
            String ans = "["+name+"] ";

            return ans;
        }
    }
    Node<T> root;
    String name;
    public BinarySearchTree(String name){
        this.name = name;
    }
    @Override
    public Iterator<T> iterator() {
        return new TreeIterator<T>(root);
    }
    public void addAll(List<T> values){
        values.forEach(x ->{
            Node<T> ptr = root;
            Node<T> newNode = new Node<T>();
            newNode.value = x;
            if(root == null) {
                root = newNode;
            }else{
                while(true){
                    if(ptr.value.compareTo(x) > 0){
                        if(ptr.left == null){
                            ptr.left = newNode;
                            break;
                        }
                        ptr = ptr.left;
                    }else if(ptr.value.compareTo(x) <  0){
                        if(ptr.right == null){
                            ptr.right = newNode;
                            break;
                        }
                        ptr = ptr.right;
                    }else{
                        break;
                    }
                }
            }

        });
    }
    @Override
    public String toString(){
        String ans = "[" + name + "] ";
        String order = helper(root);
        return ans + order;
    }
    private String helper(Node<T> node){
        if(node.left == null && node.right == null){
           return node.value.toString();
        }else if(node.left == null){
            return node.value + " R:("+ helper(node.right)+ ")";
        }else if(node.right == null){
            return node.value + " L:("+helper(node.left) + ")";
        }
        return node.value + " L:("+ helper(node.left) + ") R:("+ helper(node.right)+ ")";
    }

    public static <T extends Comparable<T>> List<T> merge(BinarySearchTree<T> t1, BinarySearchTree<T> t2){
        List<T> ans = new ArrayList<>();
        BinarySearchTree<T> temp = new BinarySearchTree<>("temp");
        Thread one = new Thread(new Runnable() {
            @Override
            public void run() {
                for(T a : t1) {
                    synchronized (temp.lock) {
                        temp.lock.value1 = a;
                        if(temp.lock.noMoreFromSecond){
                            ans.add(temp.lock.value1);
                        }else{
                            try {
                                if(temp.lock.value2 == null){
                                    temp.lock.notify();
                                    temp.lock.wait();
                                }else if(temp.lock.value2.compareTo(temp.lock.value1) > 0){
                                    ans.add(temp.lock.value1);
                                }else{
                                    ans.add(temp.lock.value2);
                                    temp.lock.notify();
                                    temp.lock.wait();
                                }
                            } catch (InterruptedException tie) {
                                System.out.println("Error1");
                            }

                        }

                    }
                }
                synchronized (temp.lock){
                    temp.lock.notify();
                }
                temp.lock.noMoreFromFirst = true;
            }

        });
        Thread two = new Thread(new Runnable() {
            @Override
            public void run() {
                for(T b : t2){
                    synchronized (temp.lock){
                        temp.lock.value2 = b;
                        if(temp.lock.noMoreFromFirst){
                            ans.add(temp.lock.value2);
                        }else{
                            try {
                                if(temp.lock.value2.compareTo(temp.lock.value1) > 0){
                                    ans.add(temp.lock.value1);
                                    temp.lock.notify();
                                    temp.lock.wait();
                                }else{
                                    ans.add(temp.lock.value2);
                                }
                            } catch (InterruptedException tie) {
                                System.out.println("Error2");
                            }

                        }
                    }
                }
                synchronized (temp.lock){
                    temp.lock.notify();
                }
                temp.lock.noMoreFromSecond = true;
            }
        });
        one.start();
        two.start();
        try{
            one.join();
            two.join();
        }catch(InterruptedException tie){
            System.out.println("Joining Error");
        }
        System.out.println("END");
        return ans;
    }

    public static void main(String... args) {
        // each tree has a name, provided to its constructor
        BinarySearchTree<Integer> t1 = new BinarySearchTree<>("Oak");
        // adds the elements to t1 in the order 5, 3, 0, and then 9
        t1.addAll(Arrays.asList(6, 2, 0, 9,15,34,35,19));
        BinarySearchTree<Integer> t2 = new BinarySearchTree<>("Maple");
        // adds the elements to t2 in the order 9, 5, and then 10
        t2.addAll(Arrays.asList(8, 5, 10,9,19,21,100,1230,1238,12,12321));
        System.out.println(t1); // see the expected output for exact format
        t1.forEach(System.out::println); // iteration in increasing order
        System.out.println(t2); // see the expected output for exact format
        t2.forEach(System.out::println); // iteration in increasing order
        BinarySearchTree<String> t3 = new BinarySearchTree<>("Cornucopia");
        t3.addAll(Arrays.asList("coconut", "apple", "banana", "plum", "durian",
                "no durians on this tree!", "tamarind"));
        System.out.println(t3); // see the expected output for exact format
        t3.forEach(System.out::println); // iteration in increasing order
        List<Integer> answer = merge(t1,t2);
        answer.forEach(System.out::println);
    }
}
