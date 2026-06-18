package com.example.student;

import java.io.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class DataManager {
    private final File dataDir;
    private final File textFile;
    private final File binaryFile;
    private final File objectFile;
    private final File backupDir;

    public DataManager(String baseDir) {
        dataDir = new File(baseDir);
        textFile = new File(dataDir, "students.txt");
        binaryFile = new File(dataDir, "students.dat");
        objectFile = new File(dataDir, "students.ser");
        backupDir = new File(dataDir, "backup");
        createFilesAndDirs();
    }

    private void createFilesAndDirs() {
        try {
            if (!dataDir.exists()) dataDir.mkdirs();
            if (!backupDir.exists()) backupDir.mkdirs();
            if (!textFile.exists()) textFile.createNewFile();
            if (!binaryFile.exists()) binaryFile.createNewFile();
            if (!objectFile.exists()) objectFile.createNewFile();
        } catch (IOException e) {
            System.err.println("Error creating files/directories: " + e.getMessage());
        }
    }

    // ---------- TEXT storage (CSV) ----------
    public void appendToText(Student s) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(textFile, true))) {
            pw.println(s.toString());
        } catch (IOException e) {
            System.err.println("Error writing to text file: " + e.getMessage());
        }
    }

    public List<Student> loadFromText() {
        List<Student> list = new ArrayList<>();
        try (Scanner sc = new Scanner(textFile)) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if (line.isEmpty()) continue;
                Student s = Student.fromCSV(line);
                if (s != null) list.add(s);
            }
        } catch (FileNotFoundException e) {
            System.err.println("Text file not found: " + e.getMessage());
        }
        return list;
    }

    // ---------- BINARY storage (DataInput/Output) ----------
    public void writeAllToBinary(List<Student> students) {
        try (DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(binaryFile)))) {
            for (Student s : students) {
                dos.writeUTF(s.getId());
                dos.writeUTF(s.getName());
                dos.writeUTF(s.getDepartment());
                dos.writeDouble(s.getGpa());
            }
        } catch (IOException e) {
            System.err.println("Error writing binary file: " + e.getMessage());
        }
    }

    public List<Student> loadFromBinary() {
        List<Student> list = new ArrayList<>();
        try (DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(binaryFile)))) {
            while (true) {
                try {
                    String id = dis.readUTF();
                    String name = dis.readUTF();
                    String dept = dis.readUTF();
                    double gpa = dis.readDouble();
                    list.add(new Student(id, name, dept, gpa));
                } catch (EOFException eof) {
                    break;
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("Binary file not found: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error reading binary file: " + e.getMessage());
        }
        return list;
    }

    // ---------- OBJECT serialization ----------
    public List<Student> loadFromObjects() {
        if (objectFile.length() == 0) return new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(objectFile))) {
            Object obj = ois.readObject();
            if (obj instanceof List) {
                @SuppressWarnings("unchecked")
                List<Student> list = (List<Student>) obj;
                return list;
            }
        } catch (EOFException eof) {
            return new ArrayList<>();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error reading objects: " + e.getMessage());
        }
        return new ArrayList<>();
    }

    public void saveObjects(List<Student> students) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(objectFile))) {
            oos.writeObject(students);
        } catch (IOException e) {
            System.err.println("Error saving objects: " + e.getMessage());
        }
    }

    // ---------- CRUD operations (we use object list as primary source for updates) ----------
    public void addStudent(Student s) {
        List<Student> list = loadFromObjects();
        list.add(s);
        saveObjects(list);
        // keep other formats in sync
        rewriteTextAndBinary(list);
    }

    public Student searchById(String id) {
        List<Student> list = loadFromObjects();
        for (Student s : list) if (s.getId().equals(id)) return s;
        return null;
    }

    public boolean updateStudent(String id, String name, String dept, double gpa) {
        List<Student> list = loadFromObjects();
        boolean found = false;
        for (Student s : list) {
            if (s.getId().equals(id)) {
                s.setName(name);
                s.setDepartment(dept);
                s.setGpa(gpa);
                found = true;
                break;
            }
        }
        if (found) {
            saveObjects(list);
            rewriteTextAndBinary(list);
        }
        return found;
    }

    public boolean deleteStudent(String id) {
        List<Student> list = loadFromObjects();
        boolean removed = list.removeIf(s -> s.getId().equals(id));
        if (removed) {
            saveObjects(list);
            rewriteTextAndBinary(list);
        }
        return removed;
    }

    public List<Student> getAllStudents() {
        // prefer object store; fallback to text
        List<Student> list = loadFromObjects();
        if (list.isEmpty()) {
            list = loadFromText();
        }
        return list;
    }

    private void rewriteTextAndBinary(List<Student> list) {
        // rewrite text
        try (PrintWriter pw = new PrintWriter(new FileWriter(textFile, false))) {
            for (Student s : list) pw.println(s.toString());
        } catch (IOException e) {
            System.err.println("Error rewriting text file: " + e.getMessage());
        }
        // rewrite binary
        writeAllToBinary(list);
    }

    // ---------- Report generation ----------
    public void generateReport() {
        List<Student> list = getAllStudents();
        if (list.isEmpty()) {
            System.out.println("No students available for report.");
            return;
        }
        int total = list.size();
        double sum = 0;
        Student highest = list.get(0);
        Student lowest = list.get(0);
        for (Student s : list) {
            sum += s.getGpa();
            if (s.getGpa() > highest.getGpa()) highest = s;
            if (s.getGpa() < lowest.getGpa()) lowest = s;
        }
        double avg = sum / total;
        System.out.println("---- Student Report ----");
        System.out.println("Total Students: " + total);
        System.out.println("Highest GPA: " + highest.getGpa() + " (" + highest.getId() + " - " + highest.getName() + ")");
        System.out.println("Lowest GPA: " + lowest.getGpa() + " (" + lowest.getId() + " - " + lowest.getName() + ")");
        System.out.printf("Average GPA: %.2f%n", avg);
    }

    // ---------- File properties ----------
    public void showFileProperties() {
        showPropsFor(textFile);
        showPropsFor(binaryFile);
        showPropsFor(objectFile);
    }

    private void showPropsFor(File f) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println("File: " + f.getName());
        System.out.println(" Path: " + f.getAbsolutePath());
        System.out.println(" Size: " + f.length() + " bytes");
        System.out.println(" Last Modified: " + sdf.format(new Date(f.lastModified())));
        System.out.println(" Readable: " + f.canRead() + " Writable: " + f.canWrite());
        System.out.println("---------------------------");
    }

    // ---------- Backup using buffered streams ----------
    public void backupSerializedFile() {
        if (!objectFile.exists() || objectFile.length() == 0) {
            System.out.println("No serialized file to backup.");
            return;
        }
        File dest = new File(backupDir, "students_backup_" + System.currentTimeMillis() + ".ser");
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(objectFile));
             BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(dest))) {
            byte[] buffer = new byte[8192];
            int read;
            while ((read = bis.read(buffer)) != -1) {
                bos.write(buffer, 0, read);
            }
            System.out.println("Backup created at: " + dest.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Backup failed: " + e.getMessage());
        }
    }
}
