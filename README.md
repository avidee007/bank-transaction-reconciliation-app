# 🏦 Bank Transaction Reconciliation using Spring Batch

## 📌 Overview

This project demonstrates a **real-world bank transaction reconciliation system** implemented using **Spring Batch** and
**Spring Boot**.

Transaction reconciliation is a **classic enterprise batch-processing problem**, commonly found in:

* Banking & financial services
* Payment gateways
* Clearing & settlement systems
* Audit and compliance platforms

The goal of this project is to showcase **why Spring Batch is the right tool** for such use cases and how to design a *
*production-grade batch job** with:

* Chunk processing
* Fault tolerance
* Retry & skip logic
* Idempotency (duplicate handling)
* Failure-aware job flows
* Operational visibility

---

## 🧠 Business Problem

Banks receive **daily transaction files** from external payment systems (UPI, cards, wallets, gateways).

For each transaction, the bank must:

1. Validate incoming records
2. Match them against internal ledger records
3. Identify discrepancies
4. Persist reconciliation results
5. Generate a summary for operations and audit teams

### Typical Challenges

* Very large files (100K → millions of records)
* Partial data corruption
* Duplicate files or transactions
* Restartability after failures
* Operational transparency

---

## ❓ Why Spring Batch?

Spring Batch is designed specifically for **large-scale, reliable, offline processing**.

### This use case is a perfect fit because:

| Requirement             | Why Spring Batch                          |
|-------------------------|-------------------------------------------|
| Large data volume       | Chunk-oriented processing                 |
| Fault tolerance         | Retry & skip mechanisms                   |
| Restartability          | JobRepository & checkpoints               |
| Idempotency             | Controlled processing with DB constraints |
| Observability           | Listeners & execution metadata            |
| Deterministic execution | Explicit job lifecycle                    |

> ❌ A scheduler or async executor would be the **wrong tool** here.

---

## 🏗️ High-Level Architecture

```
CSV File (Gateway Transactions)
        |
        v
+------------------------------+
| Spring Batch Job             |
|                              |
|  Step 1: Reconciliation      |
|   - Read CSV                 |
|   - Validate records         |
|   - Detect duplicates        |
|   - Match with ledger        |
|   - Persist results          |
|                              |
|  Step 2: Summary Tasklet     |
|   - Aggregate results        |
|   - Log / report counts      |
+------------------------------+
        |
        v
Database (Reconciliation Results)
```

---

## 🧩 Job Design

### Job Name

```
bankTransactionReconciliationJob
```

### Step 1: Reconciliation Step (Chunk-Oriented)

**Responsibilities**

* Read [gateway transactions from CSV](src/main/resources/transactions.csv)
* Validate mandatory fields
* Detect duplicate transactions
* Match with internal ledger data
* Persist reconciliation results

**Key Spring Batch Features Used**

* `FlatFileItemReader`
* `ItemProcessor`
* `RepositoryItemWriter`
* Chunk processing
* Retry & skip
* StepExecutionListener

---

### Step 2: Summary Step (Tasklet)

**Responsibilities**

* Aggregate reconciliation results by status
* Provide operational visibility
* Run **even if the reconciliation step fails**

**Why Tasklet?**

* Single, deterministic operation
* No item-by-item processing
* Ideal for reporting and aggregation

---

## 🔁 Reconciliation Logic

| Scenario              | Condition                   | Result Status       |
|-----------------------|-----------------------------|---------------------|
| Perfect match         | Txn exists & amount matches | `MATCHED`           |
| Amount mismatch       | Txn exists & amount differs | `AMOUNT_MISMATCH`   |
| Missing in ledger     | Txn not found               | `MISSING_IN_LEDGER` |
| Invalid input         | Missing mandatory fields    | Skipped             |
| Duplicate transaction | Already processed           | Skipped             |

---

## 🛡️ Fault Tolerance Strategy

### Retry

Used for **transient system failures** (e.g. DB/network hiccups).

```java
.retry(TransientDataAccessException .class)
.retryLimit(3)
```

### Skip

Used for **business-invalid records**.

```java
.skip(InvalidTransactionException .class)
.skip(DuplicateTransactionException .class)
.skipLimit(100)
```

> ✔ Bad data does NOT stop the entire job
> ✔ All skips are counted and visible

---

## ♻️ Idempotency & Duplicate Detection

### Why This Matters

* Files may be re-sent
* Jobs may be re-run
* Data integrity must be preserved

### Design Approach

1. **Database unique constraint** on `gateway_txn_id`
2. Application-level duplicate check in `ItemProcessor`
3. Duplicate records are **skipped**, not failed

This ensures:

* Safe re-runs
* No double reconciliation
* Predictable behavior

---

## 🚨 Failure-Aware Job Flow

By default, Spring Batch stops execution when a step fails.

This project **intentionally overrides that behavior**.

### Design Choice

> **Summary step must run regardless of reconciliation outcome**

```java
.start(reconciliationStep)
.on("*").to(reconciliationSummaryStep)
.end()
.build();
```

### Benefits

* Partial results are still visible
* Ops teams get immediate insight
* Failures are explicit, not silent

---

## 📊 Operational Visibility

### StepExecutionListener Captures:

* Read count
* Write count
* Skip count
* Commit & rollback count
* Failure exceptions

### Example Log Output

```
Step completed: reconciliationStep
Read Count   : 10
Write Count  : 9
Skip Count   : 1
Rollbacks    : 2
```

---

## 🗂️ Project Structure

```
src/main/java
 ├── config
 │   ├── BatchJobConfig
 │   ├── StepsConfig
 │
 ├── reader
 │   └── CsvFileReaderConfig
 │
 ├── processor
 │   └── ReconciliationItemProcessor
 │
 ├── writer
 │   └── JpaItemWriter
 │
 ├── tasklet
 │   └── ReconciliationSummaryTasklet
 │
 ├── listener
 │   └── ReconciliationStepListener
 │
 ├── domain
 │   ├── GatewayTransaction record
 │   └── enums
 │
 └── repository
     ├── entities
     └── jpa repositories
 
```

---

## ⚙️ Technology Stack

* Java 21
* Spring Boot
* Spring Batch
* Spring Data JPA
* PostgreSQL
* Gradle

---

## ▶️ Running the Job

```bash
gradlew bootRun
```

---

## 🎯 What This Project Demonstrates

* Correct usage of Spring Batch (not scheduler abuse)
* Chunk vs tasklet decision-making
* Retry vs skip semantics
* Idempotent batch design
* Failure-aware job flows
* Production-grade observability
* Clean, maintainable architecture

---

## 🚀 Further Enhancements

* Partitioning & parallel processing
* File checksum–based idempotency
* Email / Slack alerts on failures
* Metrics export (Micrometer)

---
