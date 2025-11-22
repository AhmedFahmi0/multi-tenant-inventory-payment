-- Insert admin user with password: admin123 (bcrypt hashed)
INSERT INTO users (id, email, password, first_name, last_name, is_active)
VALUES (
    '11111111-1111-1111-1111-111111111111', 
    'admin@example.com', 
    '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 
    'Admin', 
    'User', 
    true
)
ON CONFLICT (email) DO NOTHING;

-- Assign admin role
INSERT INTO user_roles (user_id, role)
VALUES ('11111111-1111-1111-1111-111111111111', 'ROLE_ADMIN')
ON CONFLICT (user_id, role) DO NOTHING;

-- Insert sample payments
INSERT INTO payments (id, tenant_id, order_id, amount, currency, status, payment_method, transaction_id, customer_email, description)
VALUES 
    ('22222222-2222-2222-2222-222222222222', 'default-tenant', 'ORDER-001', 250.00, 'USD', 'COMPLETED', 'CREDIT_CARD', 'TXN_1234567890', 'customer1@example.com', 'Premium subscription'),
    ('33333333-3333-3333-3333-333333333333', 'default-tenant', 'ORDER-002', 99.99, 'USD', 'PENDING', 'PAYPAL', NULL, 'customer2@example.com', 'One-time purchase'),
    ('44444444-4444-4444-4444-444444444444', 'default-tenant', 'ORDER-003', 1500.00, 'USD', 'REFUNDED', 'BANK_TRANSFER', 'TXN_9876543210', 'customer3@example.com', 'Refund for order #123')
ON CONFLICT (id) DO NOTHING;
