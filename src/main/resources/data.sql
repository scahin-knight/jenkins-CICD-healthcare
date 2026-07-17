-- Passwords are encrypted with BCrypt ('password')
INSERT INTO users (username, password, full_name, role, enabled, created_at, updated_at) VALUES 
('admin', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HCGzZ.y2Hw.b0nI4FzQYy', 'System Admin', 'ROLE_ADMIN', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
