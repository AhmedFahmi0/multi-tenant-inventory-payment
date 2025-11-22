-- Create payment_transactions table for Payment Gateway Microservice (Task 2)
CREATE TABLE IF NOT EXISTS payment_transactions (
    id UUID PRIMARY KEY,
    tenant_id VARCHAR(64) NOT NULL,
    dealer_id UUID NOT NULL,
    amount DECIMAL(19, 2) NOT NULL,
    method VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    request_id VARCHAR(255) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_payment_transactions_request UNIQUE (tenant_id, request_id)
);

-- Indexes to support tenant scoping and lookups
CREATE INDEX IF NOT EXISTS idx_payment_tx_tenant_id ON payment_transactions(tenant_id);
CREATE INDEX IF NOT EXISTS idx_payment_tx_tenant_id_id ON payment_transactions(tenant_id, id);
