package com.example.student;

import java.util.List;
import java.util.Scanner;
 

public class MainApp {
    public static void main(String[] args) {
        DataManager dm = new DataManager("data");
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("\n--- Student Record Management ---");
            System.out.println("1. Add Student");
            System.out.println("2. Search Student by ID");
            System.out.println("3. Update Student");
            System.out.println("4. Delete Student");
            System.out.println("5. Display All Students");
            System.out.println("6. Generate Report");
            System.out.println("7. Show File Properties");
            System.out.println("8. Backup Serialized File");
            System.out.println("0. Exit");
            System.out.print("Choose: ");
            String choice = sc.nextLine().trim();
            try {
                switch (choice) {
                    case "1":
                        System.out.print("ID: "); String id = sc.nextLine().trim();
                        System.out.print("Name: "); String name = sc.nextLine().trim();
                        System.out.print("Department: "); String dept = sc.nextLine().trim();
                        System.out.print("GPA: "); double gpa = Double.parseDouble(sc.nextLine().trim());
                        Student s = new Student(id, name, dept, gpa);
                        dm.addStudent(s);
                        System.out.println("Added.");
                        break;
                    case "2":
                        System.out.print("Enter ID: "); String sid = sc.nextLine().trim();
                        Student found = dm.searchById(sid);
                        if (found != null) System.out.println("Found: " + found);
                        else System.out.println("Not found.");
                        break;
                    case "3":
                        System.out.print("Enter ID to update: "); String uid = sc.nextLine().trim();
                        Student u = dm.searchById(uid);
                        if (u == null) { System.out.println("Not found."); break; }
                        System.out.print("New Name (" + u.getName() + "): "); String nn = sc.nextLine().trim();
                        System.out.print("New Dept (" + u.getDepartment() + "): "); String nd = sc.nextLine().trim();
                        System.out.print("New GPA (" + u.getGpa() + "): "); String ng = sc.nextLine().trim();
                        String newName = nn.isEmpty() ? u.getName() : nn;
                        String newDept = nd.isEmpty() ? u.getDepartment() : nd;
                        double newGpa = ng.isEmpty() ? u.getGpa() : Double.parseDouble(ng);
                        boolean updated = dm.updateStudent(uid, newName, newDept, newGpa);
                        System.out.println(updated ? "Updated." : "Update failed.");
                        break;
                    case "4":
                        System.out.print("Enter ID to delete: "); String did = sc.nextLine().trim();
                        boolean del = dm.deleteStudent(did);
                        System.out.println(del ? "Deleted." : "Not found.");
                        break;
                    case "5":
                        List<Student> all = dm.getAllStudents();
                        if (all.isEmpty()) System.out.println("No students.");
                        else all.forEach(st -> System.out.println(st));
                        break;
                    case "6":
                        dm.generateReport();
                        break;
                    case "7":
                        dm.showFileProperties();
                        break;
                    case "8":
                        dm.backupSerializedFile();
                        break;
                    case "0":
                        System.out.println("Bye.");
                        sc.close();
                        return;
                    default:
                        System.out.println("Invalid choice.");
                }
            } catch (NumberFormatException nfe) {
                System.err.println("Invalid number input: " + nfe.getMessage());
            } catch (Exception ex) {
                System.err.println("Unexpected error: " + ex.getMessage());
            }
        }
    }
}
