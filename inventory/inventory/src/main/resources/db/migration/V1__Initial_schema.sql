-- Create tenant table
CREATE TABLE IF NOT EXISTS tenant (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    schema_name VARCHAR(63) NOT NULL UNIQUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Create dealers table
CREATE TABLE IF NOT EXISTS dealers (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    contact_person VARCHAR(255),
    email VARCHAR(255),
    phone VARCHAR(50),
    address TEXT,
    tenant_id VARCHAR(36) NOT NULL,
    subscription_type VARCHAR(50) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Create users table
CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Create user_roles join table
CREATE TABLE IF NOT EXISTS user_roles (
    user_id UUID NOT NULL,
    role VARCHAR(50) NOT NULL,
    PRIMARY KEY (user_id, role),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Create vehicles table (matches the Vehicle entity)
CREATE TABLE IF NOT EXISTS vehicles (
    id UUID PRIMARY KEY,
    make VARCHAR(100) NOT NULL,
    model VARCHAR(100) NOT NULL,
    year INTEGER NOT NULL,
    vin VARCHAR(50) UNIQUE,
    price DECIMAL(19, 2) NOT NULL,
    status VARCHAR(50) NOT NULL,
    dealer_id UUID NOT NULL,
    tenant_id VARCHAR(36) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_vehicle_dealer FOREIGN KEY (dealer_id) REFERENCES dealers(id) ON DELETE CASCADE,
    CONSTRAINT fk_vehicle_tenant FOREIGN KEY (tenant_id) REFERENCES tenant(id) ON DELETE CASCADE
);

-- Create indexes
CREATE INDEX idx_vehicle_dealer_id ON vehicles(dealer_id);
CREATE INDEX idx_vehicle_tenant_id ON vehicles(tenant_id);
CREATE INDEX idx_vehicle_status ON vehicles(status);
CREATE INDEX idx_dealer_tenant_id ON dealers(tenant_id);

-- Add foreign key constraint to dealers table after all tables are created
ALTER TABLE dealers 
    ADD CONSTRAINT fk_dealer_tenant 
    FOREIGN KEY (tenant_id) 
    REFERENCES tenant(id) 
    ON DELETE CASCADE;

-- Create function to update updated_at timestamp
CREATE OR REPLACE FUNCTION update_modified_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Create triggers for updated_at
CREATE TRIGGER update_tenant_modtime
BEFORE UPDATE ON tenant
FOR EACH ROW EXECUTE FUNCTION update_modified_column();

CREATE TRIGGER update_user_modtime
BEFORE UPDATE ON users
FOR EACH ROW EXECUTE FUNCTION update_modified_column();

CREATE TRIGGER update_dealer_modtime
BEFORE UPDATE ON dealers
FOR EACH ROW EXECUTE FUNCTION update_modified_column();

CREATE TRIGGER update_vehicle_modtime
BEFORE UPDATE ON vehicles
FOR EACH ROW EXECUTE FUNCTION update_modified_column();
