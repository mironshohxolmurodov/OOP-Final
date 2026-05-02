CREATE DATABASE IF NOT EXISTS books;
USE books;

DROP TABLE IF EXISTS AuthorISBN;
DROP TABLE IF EXISTS Titles;
DROP TABLE IF EXISTS Authors;

CREATE TABLE Authors (
    AuthorID INT AUTO_INCREMENT PRIMARY KEY,
    FirstName VARCHAR(50) NOT NULL,
    LastName VARCHAR(50) NOT NULL
);

CREATE TABLE Titles (
    ISBN CHAR(13) PRIMARY KEY,
    Title VARCHAR(100) NOT NULL,
    EditionNumber INT NOT NULL,
    Copyright YEAR NOT NULL
);

CREATE TABLE AuthorISBN (
    AuthorID INT,
    ISBN CHAR(13),
    FOREIGN KEY (AuthorID) REFERENCES Authors(AuthorID) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (ISBN) REFERENCES Titles(ISBN) ON DELETE CASCADE ON UPDATE CASCADE,
    PRIMARY KEY (AuthorID, ISBN)
);

INSERT INTO Authors (FirstName, LastName) VALUES
('Paul', 'Deitel'),
('Harvey', 'Deitel'),
('Abbey', 'Deitel'),
('Dan', 'Quirk'),
('Michael', 'Morgano'),
('Abraham', 'Silberschatz'),
('Henry', 'Korth'),
('S.', 'Sudarshan'),
('Thomas', 'Cormen'),
('Robert', 'Martin');

INSERT INTO Titles VALUES
('0133807800', 'Java How to Program', 10, 2015),
('013299044X00', 'C How to Program', 7, 2013),
('0133406954000', 'Visual Basic 2012', 6, 2014),
('0073523321000', 'Database System Concepts', 6, 2011),
('0078022150000', 'Database Management Systems', 3, 2010),
('0262033844000', 'Introduction to Algorithms', 3, 2009),
('0132350882000', 'Clean Code', 1, 2008);

INSERT INTO AuthorISBN VALUES
(1, '0133807800'),
(2, '0133807800'),
(1, '013299044X00'),
(2, '0133406954000'),
(3, '0133406954000'),
(6, '0073523321000'),
(7, '0078022150000'),
(8, '0078022150000'),
(9, '0262033844000'),
(10, '0132350882000');
