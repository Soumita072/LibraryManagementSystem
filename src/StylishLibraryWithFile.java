import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;

// Book Class
class Book {
    String title;
    String author;
    boolean borrowed;

    Book(String title, String author, boolean borrowed) {
        this.title = title;
        this.author = author;
        this.borrowed = borrowed;
    }
}

public class StylishLibraryWithFile extends JFrame {
    private ArrayList<Book> books = new ArrayList<>();
    private DefaultTableModel tableModel;
    private JTable table;
    private JTextField titleField, authorField, searchField;

    private final String FILE_NAME = "books.txt";
    private final String HISTORY_FILE = "history.txt";

    public StylishLibraryWithFile() {
        setTitle("📚 Library Management System");
        setSize(750, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);
        setResizable(false);

        // 🎨 Colors
        Color primaryColor = new Color(52, 152, 219);
        Color secondaryColor = new Color(41, 128, 185);
        Color bgColor = new Color(236, 240, 241);
        Color buttonColor = new Color(46, 204, 113);
        Color dangerColor = new Color(231, 76, 60);

        // Top Panel
        JPanel topPanel = new JPanel(new GridLayout(2, 4, 5, 5));
        topPanel.setBackground(bgColor);

        titleField = new JTextField();
        authorField = new JTextField();
        JButton addButton = createButton("➕ Add Book", buttonColor, Color.WHITE);
        JButton searchButton = createButton("🔍 Search", primaryColor, Color.WHITE);
        searchField = new JTextField();

        topPanel.add(new JLabel("Book Title:"));
        topPanel.add(titleField);
        topPanel.add(new JLabel("Author:"));
        topPanel.add(authorField);
        topPanel.add(addButton);
        topPanel.add(new JLabel("Search Title:"));
        topPanel.add(searchField);
        topPanel.add(searchButton);

        add(topPanel, BorderLayout.NORTH);

        // Table
        tableModel = new DefaultTableModel(new String[]{"Title", "Author", "Status"}, 0);
        table = new JTable(tableModel);
        table.setRowHeight(25);
        table.setFont(new Font("SansSerif", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 15));
        table.getTableHeader().setBackground(primaryColor);
        table.getTableHeader().setForeground(Color.WHITE);

        // Alternate row colors
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 245, 245));
                }
                return c;
            }
        });

        add(new JScrollPane(table), BorderLayout.CENTER);

        // Bottom Panel
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(bgColor);

        JButton borrowButton = createButton("📖 Borrow", secondaryColor, Color.WHITE);
        JButton returnButton = createButton("↩ Return", dangerColor, Color.WHITE);
        JButton deleteButton = createButton("🗑 Delete Book", dangerColor, Color.WHITE);
        JButton editButton = createButton("✏ Edit Book", buttonColor, Color.WHITE);
        JButton historyButton = createButton("📜 View History", primaryColor, Color.WHITE);
        JButton exportButton = createButton("💾 Export CSV", secondaryColor, Color.WHITE);
        JButton sortButton = createButton("🔽 Sort by Title", primaryColor, Color.WHITE);

        bottomPanel.add(borrowButton);
        bottomPanel.add(returnButton);
        bottomPanel.add(editButton);
        bottomPanel.add(deleteButton);
        bottomPanel.add(historyButton);
        bottomPanel.add(exportButton);
        bottomPanel.add(sortButton);

        add(bottomPanel, BorderLayout.SOUTH);

        // Load data from file
        loadBooksFromFile();
        updateTable();

        // Add Book
        addButton.addActionListener(e -> {
            String title = titleField.getText().trim();
            String author = authorField.getText().trim();
            if (title.isEmpty() || author.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter both Title and Author!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            books.add(new Book(title, author, false));
            saveBooksToFile();
            updateTable();
            titleField.setText("");
            authorField.setText("");
        });

        // Search
        searchButton.addActionListener(e -> {
            String searchTitle = searchField.getText().trim().toLowerCase();
            tableModel.setRowCount(0);
            for (Book b : books) {
                if (b.title.toLowerCase().contains(searchTitle)) {
                    tableModel.addRow(new Object[]{b.title, b.author, b.borrowed ? "Borrowed" : "Available"});
                }
            }
        });

        // Borrow
        borrowButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                String bookTitle = tableModel.getValueAt(selectedRow, 0).toString();
                for (Book b : books) {
                    if (b.title.equals(bookTitle)) {
                        if (!b.borrowed) {
                            b.borrowed = true;
                            logHistory("Borrowed", b.title);
                            JOptionPane.showMessageDialog(this, "You borrowed: " + b.title);
                        } else {
                            JOptionPane.showMessageDialog(this, "Book is already borrowed!");
                        }
                        break;
                    }
                }
                saveBooksToFile();
                updateTable();
            } else {
                JOptionPane.showMessageDialog(this, "Please select a book from the table!");
            }
        });

        // Return
        returnButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                String bookTitle = tableModel.getValueAt(selectedRow, 0).toString();
                for (Book b : books) {
                    if (b.title.equals(bookTitle)) {
                        if (b.borrowed) {
                            b.borrowed = false;
                            logHistory("Returned", b.title);
                            JOptionPane.showMessageDialog(this, "Book returned: " + b.title);
                        } else {
                            JOptionPane.showMessageDialog(this, "This book was not borrowed!");
                        }
                        break;
                    }
                }
                saveBooksToFile();
                updateTable();
            } else {
                JOptionPane.showMessageDialog(this, "Please select a book from the table!");
            }
        });

        // Delete Book
        deleteButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                String bookTitle = tableModel.getValueAt(selectedRow, 0).toString();
                books.removeIf(b -> b.title.equals(bookTitle));
                saveBooksToFile();
                updateTable();
                JOptionPane.showMessageDialog(this, "Book deleted: " + bookTitle);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a book to delete!");
            }
        });

        // Edit Book
        editButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                String oldTitle = tableModel.getValueAt(selectedRow, 0).toString();
                String oldAuthor = tableModel.getValueAt(selectedRow, 1).toString();

                String newTitle = JOptionPane.showInputDialog(this, "Enter new title:", oldTitle);
                String newAuthor = JOptionPane.showInputDialog(this, "Enter new author:", oldAuthor);

                if (newTitle != null && !newTitle.trim().isEmpty() && newAuthor != null && !newAuthor.trim().isEmpty()) {
                    for (Book b : books) {
                        if (b.title.equals(oldTitle)) {
                            b.title = newTitle;
                            b.author = newAuthor;
                            break;
                        }
                    }
                    saveBooksToFile();
                    updateTable();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a book to edit!");
            }
        });

        // View History
        historyButton.addActionListener(e -> {
            StringBuilder history = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new FileReader(HISTORY_FILE))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    history.append(line).append("\n");
                }
            } catch (IOException ex) {
                history.append("No history found.");
            }
            JOptionPane.showMessageDialog(this, history.toString(), "Borrow/Return History", JOptionPane.INFORMATION_MESSAGE);
        });

        // Export CSV
        exportButton.addActionListener(e -> exportToCSV());

        // Sort by Title
        sortButton.addActionListener(e -> {
            books.sort((b1, b2) -> b1.title.compareToIgnoreCase(b2.title));
            updateTable();
        });

        setVisible(true);
    }

    // Create Styled Buttons
    private JButton createButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        return btn;
    }

    // Update Table
    private void updateTable() {
        tableModel.setRowCount(0);
        for (Book b : books) {
            tableModel.addRow(new Object[]{b.title, b.author, b.borrowed ? "Borrowed" : "Available"});
        }
    }

    // Save Books to File
    private void saveBooksToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Book b : books) {
                writer.write(b.title + "," + b.author + "," + b.borrowed);
                writer.newLine();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving data!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Load Books from File
    private void loadBooksFromFile() {
        books.clear();
        File file = new File(FILE_NAME);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 3) {
                    books.add(new Book(data[0], data[1], Boolean.parseBoolean(data[2])));
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading data!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Log History
    private void logHistory(String action, String title) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(HISTORY_FILE, true))) {
            writer.write(action + ": " + title + " (" + java.time.LocalDateTime.now() + ")");
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Export to CSV
    private void exportToCSV() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("library_export.csv"))) {
            writer.write("Title,Author,Status\n");
            for (Book b : books) {
                writer.write(b.title + "," + b.author + "," + (b.borrowed ? "Borrowed" : "Available") + "\n");
            }
            JOptionPane.showMessageDialog(this, "Exported to library_export.csv");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error exporting data!");
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StylishLibraryWithFile());
    }
}
