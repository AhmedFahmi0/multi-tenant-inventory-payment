-- Insert default tenant
INSERT INTO tenant (id, name, schema_name) 
VALUES ('default-tenant', 'Default Tenant', 'public')
ON CONFLICT (id) DO NOTHING;

-- Insert global admin user with password: admin123 (bcrypt hashed)
INSERT INTO users (id, username, password, role)
VALUES (
    '00000000-0000-0000-0000-000000000000', 
    'globaladmin', 
    '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 
    'GLOBAL_ADMIN'
)
ON CONFLICT (username) DO NOTHING;

-- Assign global admin role
INSERT INTO user_roles (user_id, role)
VALUES ('00000000-0000-0000-0000-000000000000', 'ROLE_GLOBAL_ADMIN')
ON CONFLICT (user_id, role) DO NOTHING;

-- Insert regular admin user with password: admin123
INSERT INTO users (id, username, password, role)
VALUES (
    '11111111-1111-1111-1111-111111111111', 
    'admin', 
    '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 
    'ADMIN'
)
ON CONFLICT (username) DO NOTHING;

-- Assign admin role
INSERT INTO user_roles (user_id, role)
VALUES ('11111111-1111-1111-1111-111111111111', 'ROLE_ADMIN')
ON CONFLICT (user_id, role) DO NOTHING;

-- Insert sample dealer
INSERT INTO dealers (id, name, contact_person, email, phone, tenant_id, subscription_type)
VALUES (
    '22222222-2222-2222-2222-222222222222',
    'Premium Auto Dealers',
    'John Smith',
    'contact@premiumauto.com',
    '+1234567890',
    'default-tenant',
    'PREMIUM'
)
ON CONFLICT (id) DO NOTHING;

-- Insert sample vehicles
INSERT INTO vehicles (id, make, model, year, vin, price, status, dealer_id, tenant_id)
VALUES 
    ('33333333-3333-3333-3333-333333333333', 'Toyota', 'Camry', 2022, 'JT4RN01P1S7151515', 25000.00, 'AVAILABLE', '22222222-2222-2222-2222-222222222222', 'default-tenant'),
    ('44444444-4444-4444-4444-444444444444', 'Honda', 'Civic', 2023, '2HGFG4A58FH501234', 22500.00, 'AVAILABLE', '22222222-2222-2222-2222-222222222222', 'default-tenant'),
    ('55555555-5555-5555-5555-555555555555', 'Ford', 'F-150', 2022, '1FTFW1E53MFC12345', 45000.00, 'SOLD', '22222222-2222-2222-2222-222222222222', 'default-tenant')
ON CONFLICT (id) DO NOTHING;
