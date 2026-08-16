# 📄 Veritastock: Verified Inventory Ledger & Trade Finance Engine

> **Financial Infrastructure for Retailers** — Turning physical retail stock into verified, bankable financial assets using double-blind barcode verification, immutable SHA-256 cryptographic audit chaining, and event-driven trade finance architecture.

---

## 1. Executive Summary & Problem Statement

### The Problem
* **Retailers are Cash-Trapped:** Traders in high-volume markets (e.g., Alaba, Computer Village) frequently hold ₦30M+ in fast-moving inventory (electronics, phones, spare parts) on shelves but have ₦0 in liquid working capital.
* **Banks Cannot Verify Inventory:** Financial institutions reject loan applications due to a lack of land titles (C of O) or audited statements. Banks have no way to verify whether stock is authentic, borrowed from neighboring vendors, or already pledged elsewhere.
* **Online Credit Default:** Granting goods on credit to unvetted buyers without 100% upfront payment leads to immediate fraud and severe default rates.

### The Solution
Veritastock bridges the gap between physical retail operations and institutional lenders:
1. **Double-Blind Supplier Verification:** Stock is flagged as `VERIFIED_SUPPLIER_STOCK` only when incoming physical barcode scans match the supplier's digital invoice manifest 100%.
2. **Immutable Cryptographic Ledger:** Every inventory movement (restock, POS checkout, layaway hold) generates a sequential SHA-256 cryptographic block linked to the previous block signature. Any manual database tampering breaks the hash chain instantly and triggers credit freeze alerts.
3. **Safe Retail Operations (Phase 1 MVP):** In-store POS transactions and zero-risk online **Layaway** (goods released strictly upon 100% payment completion).
4. **Embedded Trade Financing (Phase 2):** Institutional lenders connect via secure APIs to underwrite dynamic working capital loans backed by verified stock, with automated 80/20 sales split repayment.

---

## 2. System Architecture: Event-Driven Modular Monolith

Veritastock is designed as an **Event-Driven Modular Monolith** built on **Java 21**, **Spring Boot 3.3+**, and **PostgreSQL**. Modules do not perform direct cross-database queries or synchronous cross-boundary calls; they communicate asynchronously through Spring Domain Events.

```
┌──────────────────────────────────────────────────────────────────────────────────┐
│                            SPRING BOOT APPLICATION                               │
│                                                                                  │
│   ┌─────────────────────┐   ManifestVerifiedEvent    ┌───────────────────────┐   │
│   │  procurement-module │ ─────────────────────────► │   inventory-module    │   │
│   └─────────────────────┘                            └───────────────────────┘   │
│                                                                  ▲               │
│                                                                  │               │
│   ┌─────────────────────┐       OrderPaidEvent                   │ StockMutation │
│   │    sales-module     │ ───────────────────────────────────────┤     Event     │
│   └─────────────────────┘                                        │               │
│                                                                  ▼               │
│   ┌─────────────────────┐                            ┌───────────────────────┐   │
│   │   payment-module    │ ─────────────────────────► │     ledger-module     │   │
│   └─────────────────────┘     PaymentSuccessEvent    │ (Cryptographic Hash)  │   │
│                                                      └───────────────────────┘   │
└──────────────────────────────────────────────────────────────────────────────────┘
```

---

## 3. Module Boundaries & Responsibilities

| Module | Core Responsibility |
| :--- | :--- |
| `procurement` | Supplier registration, electronic invoice manifests, double-blind physical barcode scanning & reconciliation. |
| `inventory` | Product catalog, stock allocation, atomic 15-minute cart holds, and stock status gating (`UNVERIFIED_MANUAL` vs `VERIFIED_SUPPLIER_STOCK`). |
| `sales` | In-store counter POS checkouts, online store orders, and zero-risk Layaway installment plans. |
| `payment` | Payment gateway integration (Paystack/Flutterwave), webhook verification, receipt generation, and payment ledger events. |
| `ledger` | Listens to stock events (`@TransactionalEventListener`) and appends immutable, sequential SHA-256 signed blocks per merchant. |

---

## 4. Deep-Dive: Core Technical Mechanics

### A. Cryptographic Audit Chain (Tamper Defense)
Every inventory mutation creates an append-only block in `inventory_cryptographic_ledger`:
$$\text{recordPayloadHash} = \text{SHA-256}(\text{sequence} + \text{productId} + \text{eventType} + \text{qty} + \text{resultingBalance} + \text{timestamp})$$
$$\text{currentSignatureHash} = \text{SHA-256}(\text{previousHash} + \text{recordPayloadHash})$$

* **Tamper Verification:** If a malicious user modifies a past row in PostgreSQL, recalculating the hash chain from sequence `0` will result in a mismatch, throwing a `TAMPER_DETECTED` alarm.

### B. Double-Blind Inward Barcode Scanning
1. A vetted supplier uploads a digital manifest containing item barcodes and quantities.
2. Warehouse staff scans physical barcodes upon delivery without viewing expected totals.
3. When `scannedQuantity == expectedQuantity`, the manifest state transitions to `RECONCILED`, stock is marked `VERIFIED_SUPPLIER_STOCK`, and a `SUPPLIER_CHECKIN` block is signed on the ledger.

### C. POS & Zero-Risk Layaway Sales Model
* **In-Store POS:** Immediate payment & immediate stock deduction.
* **Online Layaway:** Customer pays initial deposit. Stock is reserved with a hold, but items **remain in merchant possession** until final balance reaches ₦0.

---

## 5. Technology Stack

* **Language:** Java 21 (LTS)
* **Framework:** Spring Boot 3.3+
* **Database:** PostgreSQL
* **Database Migrations:** Flyway
* **Data Access:** Spring Data JPA / Hibernate
* **Event Handling:** Spring Application Events (`@TransactionalEventListener`)
* **Build Tool:** Gradle

---

## 6. Product Roadmap

```
┌────────────────────────────────────────────────────────┐
│                   PHASE 1: THE MVP                     │
│  • Supplier Inward Barcode Scanning & Reconciliation   │
│  • Immutable SHA-256 Audit Ledger Chain                │
│  • Multi-Channel Retail: In-Store POS + Online Layaway │
│  • Automated Payment Webhooks & Digital Receipts       │
└────────────────────────────────────────────────────────┘
                           │
                           ▼
┌────────────────────────────────────────────────────────┐
│              PHASE 2: EMBEDDED FINANCING               │
│  • Real-Time Dynamic Borrowing Base Underwriting Score │
│  • Bank / Lender Open APIs (OAuth2 / mTLS)             │
│  • Automated 80/20 Payment Gateway Split-Settlement    │
└────────────────────────────────────────────────────────┘
```
