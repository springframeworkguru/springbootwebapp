package guru.springframework.birendra;

import java.util.ArrayList;
import java.util.List;

public class Birendra {
    public static void main(String[] args) {
        List<Integer> myList= new ArrayList<>();
        myList.add(10);
        myList.add(20);
        myList.add(5);
        myList.add(25);
        myList.add(11);
        myList.add(20);
        myList.stream().forEach(System.out::println);
    }
}
