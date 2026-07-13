# Hotel Room Reservation System

A Java Swing desktop application designed to streamline hotel reservation and room management. The system automates front-desk operations such as room reservations, guest management, billing, payment processing, and room availability tracking using Java, Swing, JDBC, and MySQL.

---

## Features

### Admin

- Manage user accounts (Admin & Clerk)
- Manage rooms, categories, pricing, and amenities
- Monitor room availability and occupancy
- View reservation records
- Generate reservation, occupancy, and revenue reports
- Process advanced billing and price overrides

### Clerk

- Register guests
- Create, update, and cancel reservations
- Manage room status
- Process guest billing and payments
- Monitor guest history
- Handle guest check-in and check-out
- View real-time room availability

### System

- Role-based authentication (Admin, Clerk, Guest)
- Automatic room availability updates
- Double-booking prevention
- Automatic billing calculations
- Receipt generation
- MySQL database integration

---

## Tech Stack

- Java 21
- Java Swing
- JDBC
- MySQL
- Maven

### Design Pattern

- DAO (Data Access Object)

---

## Project Structure

```text
HotelRoomReservationSystem/
│
├── src/
│   ├── dao/
│   ├── db/
│   │   ├── DBConnection.java
│   │   └── hotel_room_reservation_system.sql
│   ├── images/
│   ├── model/
│   └── ui/
│
├── pom.xml
├── README.md
└── .gitignore
```

---

## Installation

### Prerequisites

- Java JDK 21 or later
- Maven
- MySQL Server
- MySQL Workbench (recommended)
- IntelliJ IDEA (recommended)

### 1. Clone the repository

```bash
git clone https://github.com/celer-belleta/hotel-room-reservation-system.git
```

### 2. Open the project

Open the project in IntelliJ IDEA.

### 3. Configure the database

1. Open MySQL Workbench.
2. Create a database named:

```text
hotel_room_reservation_system
```

3. Import the SQL file located in:

```text
src/db/hotel_room_reservation_system.sql
```

### 4. Configure the database connection

Open:

```text
src/db/DBConnection.java
```

Update the following values to match your local MySQL installation:

```java
private static final String USER = "root";
private static final String PASSWORD = "your_mysql_password";
```

### 5. Install dependencies

```bash
mvn clean install
```

### 6. Run the application

Run:

```text
src/ui/Main.java
```

---

## Demo Accounts

| Role | Username | Password |
|------|----------|----------|
| Admin | `admin` | `admin123` |
| Clerk | `receptionist` | `receptionist123` |
| Guest | `guest` | `guest123` |


---

## License

This project is intended for educational and portfolio purposes.