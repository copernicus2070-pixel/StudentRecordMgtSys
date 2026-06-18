package com.example.student;

import java.io.Serializable;
 

public class Student implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String name;
    private String department;
    private double gpa;



    public Student() {}

    public Student(String id, String name, String department, double gpa) {
        this.id = id;
        this.name = name;
        this.department = department;
        this.gpa = gpa;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getDepartment() { return department; }
    public double getGpa() { return gpa; }

    public void setName(String name) { this.name = name; }
    public void setDepartment(String department) { this.department = department; }
    public void setGpa(double gpa) { this.gpa = gpa; }

    @Override
    public String toString() {
        return id + "," + name + "," + department + "," + gpa;
    }

    public static Student fromCSV(String csv) {
        String[] parts = csv.split(",", -1);
        if (parts.length < 4) return null;
        try {
            return new Student(parts[0], parts[1], parts[2], Double.parseDouble(parts[3]));
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
