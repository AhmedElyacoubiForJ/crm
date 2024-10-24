-- Create Employees
INSERT INTO employee (first_name, last_name, email, department) VALUES
('John', 'Doe', 'john.doe@example.com', 'Sales'),
('Jane', 'Smith', 'jane.smith@example.com', 'Support'),
('Alice', 'Johnson', 'alice.johnson@example.com', 'Marketing');

-- Create Customers
INSERT INTO customer (first_name, last_name, email, phone, address, last_interaction_date, employee_id) VALUES
('Max', 'Mustermann', 'max.mustermann@example.com', '1234567890', '123 Main St', '2024-10-22', 1),
('Erika', 'Musterfrau', 'erika.musterfrau@example.com', '2345678901', '456 Oak St', '2024-10-22', 1),
('Hans', 'Hansen', 'hans.hansen@example.com', '3456789012', '789 Pine St', '2024-10-22', 1),
('Petra', 'Petersen', 'petra.petersen@example.com', '4567890123', '101 Maple St', '2024-10-22', 2),
('Klaus', 'Klaussen', 'klaus.klaussen@example.com', '5678901234', '202 Birch St', '2024-10-22', 2),
('Lisa', 'Larsen', 'lisa.larsen@example.com', '6789012345', '303 Cedar St', '2024-10-22', 2),
('Frank', 'Franken', 'frank.franken@example.com', '7890123456', '404 Spruce St', '2024-10-22', 3),
('Sandra', 'Sanders', 'sandra.sanders@example.com', '8901234567', '505 Elm St', '2024-10-22', 3),
('Gerd', 'Gerdsen', 'gerd.gerdsen@example.com', '9012345678', '606 Fir St', '2024-10-22', 3),
('Ute', 'Utens', 'ute.utens@example.com', '0123456789', '707 Walnut St', '2024-10-22', 3);

-- Create Notes
INSERT INTO note (interaction_type, content, date, customer_id) VALUES
('MEETING', 'Meeting with Max', '2024-10-22', 1),
('CALL', 'Call with Erika', '2024-10-22', 2),
('EMAIL', 'Email to Hans', '2024-10-22', 3),
('MEETING', 'Meeting with Petra', '2024-10-22', 4),
('CALL', 'Call with Klaus', '2024-10-22', 5),
('EMAIL', 'Email to Lisa', '2024-10-22', 6),
('MEETING', 'Meeting with Frank', '2024-10-22', 7),
('CALL', 'Call with Sandra', '2024-10-22', 8),
('EMAIL', 'Email to Gerd', '2024-10-22', 9),
('MEETING', 'Meeting with Ute', '2024-10-22', 10);
