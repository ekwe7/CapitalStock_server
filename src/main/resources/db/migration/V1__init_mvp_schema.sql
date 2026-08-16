-- Veritastock Initial Database Migration Schema
CREATE TABLE system_admins (
    id UUID PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL DEFAULT 'SYSTEM_ADMIN',
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE merchants (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    business_phone VARCHAR(50),
    registration_number_rc_number VARCHAR(100),
    paystack_subaccount_code VARCHAR(100),
    flutterwave_subaccount_code VARCHAR(100),
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE roles (
    id UUID PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE users (
    id UUID PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    merchant_id UUID REFERENCES merchants(id),
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE user_roles (
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role_id UUID NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

CREATE TABLE staff_invitations (
    id UUID PRIMARY KEY,
    merchant_id UUID NOT NULL REFERENCES merchants(id),
    staff_email VARCHAR(255) NOT NULL,
    staff_full_name VARCHAR(255) NOT NULL,
    assigned_role VARCHAR(50) NOT NULL,
    invitation_status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE suppliers (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    code VARCHAR(100) UNIQUE NOT NULL,
    contact_email VARCHAR(255),
    status VARCHAR(50) NOT NULL DEFAULT 'VETTED',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE categories (
    id UUID PRIMARY KEY,
    merchant_id UUID NOT NULL REFERENCES merchants(id),
    category_name VARCHAR(255) NOT NULL,
    category_description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_merchant_category_name UNIQUE (merchant_id, category_name)
);

CREATE TABLE products (
    id UUID PRIMARY KEY,
    merchant_id UUID NOT NULL REFERENCES merchants(id),
    category_id UUID REFERENCES categories(id),
    barcode VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    sku VARCHAR(100),
    cost_price NUMERIC(15, 2) NOT NULL DEFAULT 0.00,
    selling_price NUMERIC(15, 2) NOT NULL DEFAULT 0.00,
    price NUMERIC(15, 2) NOT NULL DEFAULT 0.00,
    available_quantity INT NOT NULL DEFAULT 0,
    reserved_quantity INT NOT NULL DEFAULT 0,
    advance_rate_percentage NUMERIC(5, 2) NOT NULL DEFAULT 0.00,
    verification_status VARCHAR(50) NOT NULL DEFAULT 'UNVERIFIED_MANUAL',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_merchant_barcode UNIQUE (merchant_id, barcode)
);

CREATE TABLE supplier_invoice_manifests (
    id UUID PRIMARY KEY,
    merchant_id UUID NOT NULL REFERENCES merchants(id),
    supplier_id UUID NOT NULL REFERENCES suppliers(id),
    manifest_number VARCHAR(100) UNIQUE NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING_CHECKIN',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    reconciled_at TIMESTAMP
);

CREATE TABLE manifest_items (
    id UUID PRIMARY KEY,
    manifest_id UUID NOT NULL REFERENCES supplier_invoice_manifests(id),
    barcode VARCHAR(255) NOT NULL,
    expected_quantity INT NOT NULL,
    scanned_quantity INT NOT NULL DEFAULT 0
);

CREATE TABLE inventory_cryptographic_ledger (
    id UUID PRIMARY KEY,
    merchant_id UUID NOT NULL REFERENCES merchants(id),
    sequence_number BIGINT NOT NULL,
    product_id UUID REFERENCES products(id),
    event_type VARCHAR(50) NOT NULL,
    quantity_change INT NOT NULL,
    resulting_balance INT NOT NULL,
    record_payload_hash VARCHAR(64) NOT NULL,
    previous_hash VARCHAR(64) NOT NULL,
    current_signature_hash VARCHAR(64) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_merchant_sequence UNIQUE (merchant_id, sequence_number)
);

CREATE TABLE audit_breach_alerts (
    id UUID PRIMARY KEY,
    merchant_id UUID NOT NULL REFERENCES merchants(id),
    product_id UUID REFERENCES products(id),
    sequence_number BIGINT,
    alert_type VARCHAR(50) NOT NULL,
    description TEXT NOT NULL,
    resolved BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE orders (
    id UUID PRIMARY KEY,
    merchant_id UUID NOT NULL REFERENCES merchants(id),
    order_number VARCHAR(100) UNIQUE NOT NULL,
    channel VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    total_amount NUMERIC(15, 2) NOT NULL,
    paid_amount NUMERIC(15, 2) NOT NULL DEFAULT 0.00,
    hold_expires_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE order_items (
    id UUID PRIMARY KEY,
    order_id UUID NOT NULL REFERENCES orders(id),
    product_id UUID NOT NULL REFERENCES products(id),
    quantity INT NOT NULL,
    unit_price NUMERIC(15, 2) NOT NULL
);

CREATE TABLE payments (
    id UUID PRIMARY KEY,
    order_id UUID NOT NULL REFERENCES orders(id),
    reference VARCHAR(255) UNIQUE NOT NULL,
    payment_method VARCHAR(50) NOT NULL,
    amount NUMERIC(15, 2) NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Seed initial roles
INSERT INTO roles (id, name) VALUES ('a0000000-0000-0000-0000-000000000001', 'ROLE_SYSTEM_ADMIN');
INSERT INTO roles (id, name) VALUES ('a0000000-0000-0000-0000-000000000002', 'ROLE_MERCHANT_ADMIN');
INSERT INTO roles (id, name) VALUES ('a0000000-0000-0000-0000-000000000003', 'ROLE_STORE_STAFF');
INSERT INTO roles (id, name) VALUES ('a0000000-0000-0000-0000-000000000004', 'ROLE_SUPPLIER');
