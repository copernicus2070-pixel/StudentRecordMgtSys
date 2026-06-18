                   Student Record Management System (Java)

This project is a simple Student Record Management System built in Java. It shows how to design a modular application using OOP and different File I/O techniques.

    System Design

- Student class: Data model with ID, name, department, GPA. Serializable for object storage.
- DataManager: Controller/service layer. Handles CRUD, synchronizes text/binary/serialized files, generates reports, manages backups, shows file properties.
- MainApp: Console interface. Provides a menu and delegates tasks to DataManager.

    Storage Design

- Text file (`students.txt`) → CSV format, human-readable.
- Binary file (`students.dat`) → Compact raw storage with streams.
- Serialized file (`students.ser`) → Master copy storing the full list.

> Design choice: Serialized file is the **primary source of truth**. Text and binary files are rewritten after updates.

    Safety & Reliability

- Exception handling ensures safe file operations.
- Backup system copies the serialized file into a backup folder.
- File properties displayed for transparency.

    Design 

- Modular classes
- Consistent storage
- Safe backups
- Educational showcase
