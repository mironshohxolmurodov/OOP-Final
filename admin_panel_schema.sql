CREATE TABLE admins (
    admin_id VARCHAR(40) PRIMARY KEY,
    password_hash VARCHAR(255) NOT NULL,
    name VARCHAR(120) NOT NULL,
    email VARCHAR(160) NOT NULL,
    phone VARCHAR(40),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'
);

CREATE TABLE books (
    book_id VARCHAR(40) PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    author VARCHAR(160) NOT NULL,
    category VARCHAR(120) NOT NULL,
    isbn VARCHAR(40) NOT NULL UNIQUE,
    publisher VARCHAR(160) NOT NULL,
    publication_year INT NOT NULL,
    total_quantity INT NOT NULL CHECK (total_quantity >= 0),
    available_copies INT NOT NULL CHECK (available_copies >= 0),
    CHECK (available_copies <= total_quantity)
);

CREATE TABLE students (
    user_id VARCHAR(40) PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    email VARCHAR(160) NOT NULL UNIQUE,
    department VARCHAR(120) NOT NULL,
    phone VARCHAR(40) NOT NULL
);

CREATE TABLE book_loans (
    loan_id VARCHAR(40) PRIMARY KEY,
    book_id VARCHAR(40) NOT NULL,
    user_id VARCHAR(40) NOT NULL,
    issue_date DATE NOT NULL,
    due_date DATE NOT NULL,
    return_date DATE,
    fine_amount DECIMAL(10, 2) NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL,
    FOREIGN KEY (book_id) REFERENCES books(book_id),
    FOREIGN KEY (user_id) REFERENCES students(user_id),
    CHECK (status IN ('ISSUED', 'RETURNED'))
);

CREATE TABLE activities (
    activity_id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    description VARCHAR(255) NOT NULL
);

CREATE INDEX idx_book_loans_status ON book_loans(status);
CREATE INDEX idx_book_loans_due_date ON book_loans(due_date);
CREATE INDEX idx_book_loans_user_id ON book_loans(user_id);
