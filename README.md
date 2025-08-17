# 📚 Library Management System (Java Swing)

A modern **Library Management System** built using **Java Swing** with file storage support.  
This project provides a simple yet stylish GUI to manage books in a library, allowing users to **add, search, borrow, return, edit, delete, export, and view history** of books.

---

## 🚀 Features

- ➕ **Add Book** – Add books with title and author.
- 🔍 **Search Book** – Search books by title.
- 📖 **Borrow Book** – Borrow a book (updates status and logs history).
- ↩ **Return Book** – Return borrowed books.
- ✏ **Edit Book** – Modify book title/author details.
- 🗑 **Delete Book** – Remove books from the library.
- 📜 **View History** – View borrow/return history (saved in `history.txt`).
- 💾 **Export CSV** – Export library records into a `library_export.csv` file.
- 🔽 **Sort by Title** – Alphabetically sort books.
- 📂 **Persistent Storage** – All data is stored in `books.txt` and loaded automatically on startup.
- 🎨 **Modern UI** – Styled buttons, alternate row colors, and clean table design.

---

## 🛠️ Tech Stack

- **Language:** Java (JDK 8+)
- **GUI Framework:** Java Swing (`JFrame`, `JTable`, `JPanel`, etc.)
- **Storage:** Local file system (`books.txt`, `history.txt`, CSV export)


## ⚡ How to Run

1. Clone the repository:
   ```bash
   git clone https://github.com/Soumita072/LibraryManagementSystem.git
   
javac StylishLibraryWithFile.java
java StylishLibraryWithFile


