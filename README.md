# 🌐 Pricing Data Validation & Reporting Utility
### A Production-Ready Java + Spring Boot + Docker Project for Financial Pricing Pipelines

This project simulates **real-world pricing ingestion workflows** used in trading, clearing, market-risk, and middle-office applications.

It validates pricing files (CSV), separates good and bad data, allows API-based corrections, and generates summary reports.  
Designed to demonstrate **clean code, validation architecture, Spring Boot APIs, database usage, and containerization**.

---

## 📌 Table of Contents

- [Motivation](#motivation)
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Architecture Overview](#architecture-overview)
- [API Documentation](#api-documentation)
- [Running the Project (IntelliJ)](#running-the-project-intellij)
- [Running with Docker](#running-with-docker)
- [Project Structure](#project-structure)
- [Sample CSV](#sample-csv)
- [Final Notes](#final-notes)

---

## ⭐ Motivation

Financial institutions ingest **millions of pricing data points** across markets daily.  
Bad or inconsistent pricing data can lead to:

- Incorrect risk or P&L
- Failed clearing operations
- Bad data propagation
- Regulatory issues

This project demonstrates how real pricing-validation systems work:

✔ Parsing structured data  
✔ Multi-rule validation pipeline  
✔ Staging invalid rows  
✔ Correcting and revalidating  
✔ Promoting valid data to the main pricing store  
✔ Generating reports  
✔ Running fully inside Docker

---

## ✨ Features

### **1. CSV Upload & Parsing**
- Upload CSV via API or Swagger UI
- Header auto-detection
- Safe parsing with null-handling
- Date parsing with error tolerance

### **2. Validation Pipeline**
Validation rules include:

| Rule | Description |
|------|-------------|
| Required Fields | Checks mandatory fields like instrument ID, date, price |
| Price Format Rule | Detects invalid numeric formats (like "INVALID") |
| Allowed Exchange | Validates exchange based on config |
| Duplicate Rule | Checks duplicates within dataset |

All rules are configurable via `application.yml`.

---

### **3. Staging Table for Invalid Records**
Bad rows are saved in `staging_records` with error messages.

APIs allow:

- Viewing invalid rows
- Patching incorrect fields
- Revalidating and promoting them into the main pricing table

---

### **4. Reporting API**
- Count of valid/invalid records
- Summary of errors
- Exchange-level summary
- Product-wise segmentation

---

### **5. Complete REST API Suite**
Includes:

- Upload CSV
- List all valid pricing records
- List invalid staging records
- PATCH to correct errors
- Revalidate and promote

Swagger UI is auto-generated.

---

### **6. Full Docker Containerization**
This project includes:

✔ Multi-stage Dockerfile  
✔ Small & optimized JRE runtime image  
✔ Easy one-command deployment  
✔ Works on ANY system with Docker installed

---

## 🛠 Tech Stack

| Layer | Technology |
|-------|------------|
| Language | Java 21 |
| Framework | Spring Boot 4.x |
| Parsing | OpenCSV |
| Database | H2 (In-Memory), Docker-ready for Postgres |
| API Documentation | Swagger / Springdoc OpenAPI |
| Logs | SLF4J + Logback |
| Build | Maven |
| Runtime | Docker |

---

## 🏗 Architecture Overview

CSV File → Parser → Validator Pipeline →
| Valid → pricing_records
| Invalid → staging_records → (PATCH/REVALIDATE) → pricing_records


Encapsulated into:

- `CsvPricingParser`
- `ValidatorService`
- `ValidationRules`
- `PricingService`
- `StagingService`
- `ReportingService`
- `Swagger/OpenAPI`
- `Dockerfile`

---

## 📚 API Documentation
Once the application is running, open:
http://localhost:8080/swagger-ui/index.html


**Available endpoints:**

### **Pricing**
- `POST /api/pricing/ingest` → Upload CSV file
- `GET /api/pricing/all` → List valid pricing data

### **Staging (Invalid Records)**
- `GET /api/pricing/staging`
- `GET /api/pricing/staging/{id}`
- `PATCH /api/pricing/staging/{id}`
- `POST /api/pricing/staging/{id}/revalidate`
- `DELETE /api/pricing/staging/{id}`

### **Reporting**
- `GET /api/pricing/report/summary`

---

## ▶ Running the Project (IntelliJ)

1. Open IntelliJ → *Open Project*
2. Import as Maven project
3. Run the main class:
   PricingValidatorApplication


---

## 🐳 Running with Docker

### **1. Build Docker Image**
```bash
docker build -t pricing-validator:local .
```
### **2. Run Container**
```bash
docker run --rm -p 8080:8080 pricing-validator:local
```

### **3. Open Swagger**
```bash
http://localhost:8080/swagger-ui/index.html
```
### **4. Upload CSV**

Use the input box on
- `POST /api/pricing/ingest` → Upload CSV file

### **📁 Project Structure**
```css
pricing-validator
│── src/main/java/com/example/pricingvalidator/
│   ├── controller
│   ├── model
│   ├── parser
│   ├── repo
│   ├── service
│   ├── validation
│   └── logging
│── src/main/resources
│   ├── application.yml
│── Dockerfile
│── pom.xml
│── README.md
```
### **📄 Sample CSV**
```yaml
instrument_guid,trade_date,price,exchange,product_type
1001,2025-01-10,123.45,CME,FUT
1002,2025-01-10,222.10,NYMEX,OPT
1003,2025-01-10,,CME,FUT
1004,2025-01-10,350.00,CBOT,FUT
1004,2025-01-10,350.00,CBOT,FUT
1005,2025-01-10,INVALID,COMEX,OPT
```
### **✔ Final Notes**

**This project demonstrates:**

1. Clean Java + Spring design
2. Realistic validation architecture
3. Staging + correction workflow
4. Complete REST API suite
5. Dockerized microservice project
6. Strong logging & reporting