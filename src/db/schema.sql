-- Hotel Reservation and Room Management System Database Schema

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
   status ENUM('Available', 'Booked', 'Maintenance') DEFAULT 'Available',
   amenities TEXT
);

-- Guests table
CREATE TABLE guests (
    guest_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    contact VARCHAR(50),
    id_number VARCHAR(50)
);

-- Rservations table
CREATE TABLE reservations (
    res_id INT AUTO_INCREMENT PRIMARY KEY,
    guest_id INT NOT NULL,
    room_id INT NOT NULL,
    check_in DATE NOT NULL,
    check_out DATE NOT NULL,
    status ENUM('Pending', 'Confirmed', 'Cancelled') DEFAULT 'Pending',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    -- Links to ensure you can't book for a ghost guest or non-existent room
    FOREIGN KEY (guest_id) REFERENCES guests(guest_id),
    FOREIGN KEY (room_id) REFERENCES rooms(id)
);

-- NOT YET (stil making this)
-- Packages table
-- Bills table
-- Payments table
-- Alerts table




















-- Packages table
CREATE TABLE packages (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL
);

-- Reservations table
CREATE TABLE reservations (
    id INT AUTO_INCREMENT PRIMARY KEY,
    guest_id INT NOT NULL,
    room_id INT NOT NULL,
    check_in DATE NOT NULL,
    check_out DATE NOT NULL,
    status VARCHAR(20) DEFAULT 'Pending', -- Pending, Confirmed, Cancelled
    package_id INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (guest_id) REFERENCES guests(id),
    FOREIGN KEY (room_id) REFERENCES rooms(id),
    FOREIGN KEY (package_id) REFERENCES packages(id)
);

-- Bills table
CREATE TABLE bills (
    id INT AUTO_INCREMENT PRIMARY KEY,
    reservation_id INT NOT NULL,
    total_cost DECIMAL(10,2) NOT NULL,
    payment_status VARCHAR(20) DEFAULT 'Unpaid', -- Paid, Partially Paid, Unpaid
    FOREIGN KEY (reservation_id) REFERENCES reservations(id)
);

-- Payments table
CREATE TABLE payments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    bill_id INT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    method VARCHAR(20) NOT NULL, -- Cash, Card
    payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (bill_id) REFERENCES bills(id)
);

-- Alerts table
CREATE TABLE alerts (
    id INT AUTO_INCREMENT PRIMARY KEY,
    message TEXT NOT NULL,
    type VARCHAR(50) NOT NULL, -- Booking Confirmation, Check-in, etc.
    user_id INT, -- NULL for general alerts
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Insert sample data
INSERT INTO packages (name, description, price) VALUES
('Room Only', 'Basic room accommodation', 0.00),
('Room + Breakfast', 'Room with breakfast included', 20.00),
('Room + Amenities', 'Room with all amenities', 50.00);

INSERT INTO rooms (room_number, type, price, amenities) VALUES
('101', 'Superior', 100.00, 'Wi-Fi, AC'),
('102', 'Deluxe', 150.00, 'Wi-Fi, AC, TV'),
('201', 'Family', 200.00, 'Wi-Fi, AC, TV, Kitchen'),
('301', 'Suite', 300.00, 'Wi-Fi, AC, TV, Kitchen, Jacuzzi');
