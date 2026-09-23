USE stock_management_system;

CREATE TABLE IF NOT EXISTS restock_requests (
    id INT AUTO_INCREMENT PRIMARY KEY,
    product_id INT NOT NULL,
    product_name VARCHAR(100) NOT NULL,
    requested_quantity INT NOT NULL,
    employee_username VARCHAR(50) NOT NULL,
    request_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) DEFAULT 'Pending',
    FOREIGN KEY (product_id) REFERENCES products(id)
);