DROP TABLE IF EXISTS note;
DROP TABLE IF EXISTS customer;
DROP TABLE IF EXISTS employee;

-- Create Employee Table
CREATE TABLE employee (
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    email VARCHAR(100) UNIQUE,
    department VARCHAR(50)
);

-- Create Customer Table
CREATE TABLE customer (
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    email VARCHAR(100) UNIQUE,
    phone VARCHAR(20),
    address VARCHAR(255),
    last_interaction_date DATE,
    employee_id INTEGER,
    FOREIGN KEY (employee_id) REFERENCES employee(id)
);

-- Create Note Table
CREATE TABLE note (
    id SERIAL PRIMARY KEY,
    interaction_type VARCHAR(50),
    content TEXT,
    date DATE,
    customer_id INTEGER,
    FOREIGN KEY (customer_id) REFERENCES customer(id)
);
