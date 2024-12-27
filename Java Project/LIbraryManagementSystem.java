import java.util.*;

// Abstract class to represent borrowable items
abstract class Borrowable {
    protected int id;
    protected String title;
    protected String author;
    protected boolean isBorrowed;
    protected String borrowedBy; // Student ID
    protected Date borrowedDate; // Borrowed date
    protected int fine; // Fine amount for overdue books

    public Borrowable(int id, String title, String author) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.isBorrowed = false;
        this.borrowedBy = null;
        this.borrowedDate = null;
        this.fine = 0;
    }

    public abstract void borrow(String studentId);
    public abstract void returnBook();

    public int calculateFine() {
        if (isBorrowed && borrowedDate != null) {
            long difference = (new Date().getTime() - borrowedDate.getTime()) / (1000 * 60 * 60 * 24);
            if (difference > 14) { // 14-day borrowing duration
                fine = (int) (difference - 14) * 10; // Fine: 10 units per day
            }
        }
        return fine;
    }

    public int getFine() {
        return fine;
    }

    @Override
    public String toString() {
        String status = isBorrowed ? "Borrowed by: " + borrowedBy : "Available";
        return "ID: " + id + ", Title: " + title + ", Author: " + author + " (" + status + ")";
    }
}

// Book class extending Borrowable
class Book extends Borrowable {

    public Book(int id, String title, String author) {
        super(id, title, author);
    }

    @Override
    public void borrow(String studentId) {
        this.isBorrowed = true;
        this.borrowedBy = studentId;
        this.borrowedDate = new Date(); // Set current date as borrowed date
    }

    @Override
    public void returnBook() {
        this.isBorrowed = false;
        this.borrowedBy = null;
        this.borrowedDate = null;
        this.fine = 0; // Reset fine on return
    }
}

// EBook class extending Borrowable
class EBook extends Borrowable {
    private String fileFormat;

    public EBook(int id, String title, String author, String fileFormat) {
        super(id, title, author);
        this.fileFormat = fileFormat;
    }

    @Override
    public void borrow(String studentId) {
        this.isBorrowed = true;
        this.borrowedBy = studentId;
        this.borrowedDate = new Date(); // Set current date as borrowed date
    }

    @Override
    public void returnBook() {
        this.isBorrowed = false;
        this.borrowedBy = null;
        this.borrowedDate = null;
        this.fine = 0; // Reset fine on return
    }

    @Override
    public String toString() {
        return super.toString() + ", File Format: " + fileFormat;
    }
}

// Library class
class Library {
    private Map<Integer, Borrowable> borrowables = new HashMap<>();
    private int nextBookId = 1;

    public void addBook(String title, String author) {
        Book newBook = new Book(nextBookId++, title, author);
        borrowables.put(newBook.id, newBook);
        System.out.println("Book added successfully: " + newBook);
    }

    public void addEBook(String title, String author, String fileFormat) {
        EBook newEBook = new EBook(nextBookId++, title, author, fileFormat);
        borrowables.put(newEBook.id, newEBook);
        System.out.println("EBook added successfully: " + newEBook);
    }

    public void removeBorrowable(int id) {
        if (borrowables.containsKey(id)) {
            Borrowable removed = borrowables.remove(id);
            System.out.println("Borrowable removed: " + removed);
        } else {
            System.out.println("Borrowable with ID " + id + " not found.");
        }
    }

    public void borrowBorrowable(int id, String studentId) {
        Borrowable borrowable = borrowables.get(id);
        if (borrowable != null && !borrowable.isBorrowed) {
            borrowable.borrow(studentId);
            System.out.println("Borrowed successfully: " + borrowable);
        } else if (borrowable != null) {
            System.out.println("Borrowable is already borrowed.");
        } else {
            System.out.println("Borrowable with ID " + id + " not found.");
        }
    }

    public void returnBorrowable(int id) {
        Borrowable borrowable = borrowables.get(id);
        if (borrowable != null && borrowable.isBorrowed) {
            int fine = borrowable.calculateFine();
            if (fine > 0) {
                System.out.println("This item is overdue! Fine: " + fine + " units.");
            }
            borrowable.returnBook();
            System.out.println("Returned successfully: " + borrowable);
        } else if (borrowable != null) {
            System.out.println("This item was not borrowed.");
        } else {
            System.out.println("Borrowable with ID " + id + " not found.");
        }
    }

    public void viewBorrowables() {
        if (borrowables.isEmpty()) {
            System.out.println("No borrowables available in the library.");
        } else {
            System.out.println("Available borrowables in the library:");
            for (Borrowable borrowable : borrowables.values()) {
                System.out.println(borrowable);
            }
        }
    }
}

// Main class
 class LibraryManagementSystem {
    private static Map<String, String> admins = new HashMap<>();
    private static Library library = new Library();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        admins.put("admin", "1234"); // Default admin account

        while (true) {
            System.out.println("\nWelcome to the Library Management System");
            System.out.println("1. Admin");
            System.out.println("2. Student");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    adminLogin();
                    break;
                case 2:
                    handleStudent();
                    break;
                case 3:
                    System.out.println("Exiting the system. Goodbye!");
                    return;
                default:
                    System.out.println("Invalid option. Returning to the main menu.");
            }
        }
    }

    private static void adminLogin() {
        System.out.println("1. Create Account");
        System.out.println("2. Login");
        System.out.print("Choose an option: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice == 1) {
            System.out.print("Enter new username: ");
            String newUsername = scanner.nextLine();
            System.out.print("Enter new password: ");
            String newPassword = scanner.nextLine();
            if (admins.containsKey(newUsername)) {
                System.out.println("Account already exists!");
            } else {
                admins.put(newUsername, newPassword);
                System.out.println("Account created successfully!");
            }
        } else if (choice == 2) {
            System.out.print("Enter admin username: ");
            String username = scanner.nextLine();
            System.out.print("Enter admin password: ");
            String password = scanner.nextLine();

            if (!admins.containsKey(username) || !admins.get(username).equals(password)) {
                System.out.println("Invalid credentials. Returning to main menu.");
                return;
            }
            handleAdmin();
        } else {
            System.out.println("Invalid choice. Returning to main menu.");
        }
    }

    private static void handleAdmin() {
        while (true) {
            System.out.println("\nAdmin Menu");
            System.out.println("1. Add Book");
            System.out.println("2. Add EBook");
            System.out.println("3. Remove Borrowable");
            System.out.println("4. Borrow Borrowable");
            System.out.println("5. Return Borrowable");
            System.out.println("6. View All Borrowables");
            System.out.println("7. Logout");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    System.out.print("Enter book title: ");
                    String title = scanner.nextLine();
                    System.out.print("Enter book author: ");
                    String author = scanner.nextLine();
                    library.addBook(title, author);
                    break;
                case 2:
                    System.out.print("Enter ebook title: ");
                    String eBookTitle = scanner.nextLine();
                    System.out.print("Enter ebook author: ");
                    String eBookAuthor = scanner.nextLine();
                    System.out.print("Enter file format: ");
                    String fileFormat = scanner.nextLine();
                    library.addEBook(eBookTitle, eBookAuthor, fileFormat);
                    break;
                case 3:
                    System.out.print("Enter borrowable ID to remove: ");
                    int removeId = scanner.nextInt();
                    library.removeBorrowable(removeId);
                    break;
                case 4:
                    System.out.print("Enter borrowable ID to borrow: ");
                    int borrowId = scanner.nextInt();
                    scanner.nextLine();
                    System.out.print("Enter student ID: ");
                    String studentId = scanner.nextLine();
                    library.borrowBorrowable(borrowId, studentId);
                    break;
                case 5:
                    System.out.print("Enter borrowable ID to return: ");
                    int returnId = scanner.nextInt();
                    library.returnBorrowable(returnId);
                    break;
                case 6:
                    library.viewBorrowables();
                    break;
                case 7:
                    System.out.println("Logging out.");
                    return;
                default:
                    System.out.println("Invalid option. Returning to admin menu.");
            }
        }
    }

    private static void handleStudent() {
        while (true) {
            System.out.println("\nStudent Menu");
            System.out.println("1. View All Borrowables");
            System.out.println("2. Logout");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    library.viewBorrowables();
                    break;
                case 2:
                    System.out.println("Logging out.");
                    return;
                default:
                    System.out.println("Invalid option. Returning to student menu.");
            }
        }
    }
}