-- Hotel Room Reservation System Database Schema

-- Users table
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(50) NOT NULL,
    role VARCHAR(20) NOT NULL
);

-- Rooms table
CREATE TABLE rooms (
    id INT AUTO_INCREMENT PRIMARY KEY,
    room_number VARCHAR(10) UNIQUE NOT NULL,
    type ENUM('Superior', 'Deluxe', 'Family', 'Suite') NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    status ENUM('Available', 'Booked', 'Maintenance', 'Occupied') DEFAULT 'Available',
    amenities TEXT
);

-- Guests table
CREATE TABLE guests (
    guest_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    contact VARCHAR(50),
    id_number VARCHAR(50),
    username VARCHAR(50) UNIQUE NOT NULL, -- Added for login
    password VARCHAR(50) NOT NULL         -- Added for login
);

-- Packages table
CREATE TABLE packages (
    package_id INT AUTO_INCREMENT PRIMARY KEY,
    package_name VARCHAR(50) NOT NULL,
    price DOUBLE NOT NULL,
    description VARCHAR(255)
);

INSERT INTO packages (package_name, price, description) VALUES
    ('Room Only', 0.0, 'Basic room stay without additional meals or services.'),
    ('Room + Breakfast', 500.0, 'Standard stay inclusive of daily breakfast service.'),
    ('Room + Amenities', 1000.0, 'Premium stay with full access to hotel leisure facilities.');

-- Reservations table
CREATE TABLE reservations (
    res_id INT AUTO_INCREMENT PRIMARY KEY,
    guest_id INT NOT NULL,
    room_id INT NOT NULL,
    check_in DATE NOT NULL,
    check_out DATE NOT NULL,

    package_id INT,
    status ENUM('Pending', 'Confirmed', 'Cancelled') DEFAULT 'Confirmed',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    -- Foreign key constraints
    FOREIGN KEY (guest_id) REFERENCES guests(guest_id),
    FOREIGN KEY (room_id) REFERENCES rooms(id),
    FOREIGN KEY (package_id) REFERENCES packages(package_id)
);

-- Payments table
CREATE TABLE payments (
    payment_id INT AUTO_INCREMENT PRIMARY KEY,
    res_id INT NOT NULL,
    amount_paid DECIMAL(10,2) NOT NULL,
    payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    payment_method ENUM('Cash', 'Card') NOT NULL,
    payment_type ENUM('Down Payment', 'Full Payment') NOT NULL,
    invoice_number VARCHAR(20) UNIQUE,
    FOREIGN KEY (res_id) REFERENCES reservations(res_id) ON DELETE CASCADE
);

added:
    ALTER TABLE guests ADD COLUMN user_id INT;
    ALTER TABLE guests ADD FOREIGN KEY (user_id) REFERENCES users(id);

changes:
    -- remove the user_id column from guests table
DROP TABLE IF EXISTS payments;

CREATE TABLE payments (
payment_id INT PRIMARY KEY AUTO_INCREMENT,
res_id INT, -- This links to the res_id in reservations table
amount_paid DOUBLE, -- The specific amount paid in this transaction
total_amount_due DOUBLE, -- The total price of the stay
discount_amount DOUBLE, -- The 15% from CTU2026
payment_method VARCHAR(50), -- 'Cash' or 'Card'
payment_type VARCHAR(50), -- 'Down Payment' or 'Full Payment'
invoice_number VARCHAR(100),
payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
FOREIGN KEY (res_id) REFERENCES reservations(res_id)
);