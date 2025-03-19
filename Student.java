import java.util.*;
public class Student {
    public String name;

    // Constructor
    Student(String name) {
        this.name = name;
    }

    // Override toString method to print the name of the student
    @Override
    public String toString() {
        return name;
    }
}

public class Main {
    public static void main(String[] args) {
        // Initialize the array of students
        Student[] mystudents = new Student[] {
            new Student("Dharma"), 
            new Student("Safvo"), 
            new Student("Shivam"), 
            new Student("Tanuu")
        };

        // Loop through each student and print their name
        for (Student m : mystudents) {
            System.out.println(m);
        }
    }
}
