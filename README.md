# 🧪 Lab Resource Utilization Platform

A full-stack enterprise web application for efficient management, utilization, maintenance, booking, and sharing of laboratory resources across educational and research institutions.

---

## 📌 Project Overview

The **Lab Resource Utilization Platform** is designed to help educational institutions manage laboratory resources through a centralized web application.

The platform provides features for:
- Laboratory and institution management
- Equipment management
- Equipment booking & waiting-list queueing
- Maintenance management & preventive servicing
- Equipment certification and compliance
- Inter-institution resource sharing
- Billing and cost management
- Equipment utilization analytics
- Reports and data export (PDF & Excel)
- Notifications and user management
- 6-Tier Role-Based Access Control (RBAC)

The system uses a **Spring Boot REST API**, **React frontend**, and **PostgreSQL database**.

---

## 🚀 Features

### 🔐 Authentication & Security
- JWT-based stateless authentication
- Google OAuth2 login integration (`prompt=select_account`)
- Secure password hashing using BCrypt
- 6-Tier Role-Based Access Control (RBAC)
- Protected REST APIs & URL pattern authorization
- Role-based frontend access control
- User approval workflow for new registrations

### 👤 User & Access Management
- Multiple user roles (`SYSTEM_ADMIN`, `INSTITUTION_ADMIN`, `DEPARTMENT_HEAD`, `LAB_MANAGER`, `LAB_TECHNICIAN`, `RESEARCHER`)
- User registration and administrative approval queue
- Role-based permissions enforcement
- Administrator user management
- Laboratory-level data access control
- Institution-level data isolation

### 🧪 Equipment Management
- Equipment registration and specification cataloging
- Real-time availability tracking (`AVAILABLE`, `BOOKED`, `UNDER_MAINTENANCE`, `SHARED`)
- Equipment status state management
- External/shared equipment discovery across institutions

### 📅 Booking Management
- Conflict-free date-range equipment booking
- Role-based booking management
- Automatic double-booking collision prevention
- Sequential waiting-list queue placement for date conflicts
- Automated queue promotion engine upon booking cancellation/completion

### 🔧 Maintenance Management
- Work order creation and technician assignment
- Preventive maintenance auto-scheduling based on usage tiers (High 15d, Medium 20d, Low 30d)
- Servicing cost tracking
- Maintenance dashboard
- Equipment status locking during active servicing
- Maintenance alert notifications

### 📜 Equipment Certificates
- Calibration certificate logging and document URL attachment
- Certificate upload and compliance tracking
- Certificate expiry monitoring with expiry warning banners
- Compliance status tracking
- Certificate expiry alert notifications

### 🤝 Resource Sharing
- Inter-institution equipment sharing discovery
- Outgoing and incoming sharing request workflows
- Shared equipment cataloging (`shared=true`)
- 10% inter-institution fee calculation & cost estimation
- Owner-only authorization controls for approval/rejection

### 💰 Billing & Cost Management
- Equipment usage billing calculation
- Billing summaries and invoice generation (`INV-YYYY-XXXXX`)
- Inter-institution sharing cost estimation
- Laboratory-scoped billing visibility & IDOR protection
- Role-based billing access enforcement

### 📊 Analytics & Utilization
- Equipment utilization percentage calculations
- Equipment usage ranking and classification
- Interactive utilization dashboards (powered by Recharts)
- Idle days and usage hours tracking
- Equipment utilization tier classification (`HIGH`, `MEDIUM`, `LOW`)

### 📑 Reports
- Operational utilization reports
- Server-side PDF report generation (via OpenPDF)
- Excel spreadsheet report export (via Apache POI)

### 🔔 Notifications
- System-wide automated notifications
- Booking update and approval notifications
- Maintenance and work order alerts
- Calibration certificate expiry alerts
- Registration approval/rejection notifications

---

## 🛠 Tech Stack

| Layer | Technologies & Libraries |
| :--- | :--- |
| **Backend** | Java 17, Spring Boot 4.1, Spring Security 6, Spring Data JPA, Hibernate ORM, Maven |
| **Security** | JWT (HMAC-SHA512), Google OAuth2 Client, BCrypt Password Encoder |
| **Frontend** | React 19.2, Vite 8.1, Axios 1.18, React Router DOM 7.18, HTML5, Vanilla CSS3, JavaScript |
| **Database** | PostgreSQL Relational Database (12 Structured Entities) |
| **Reporting & Analytics** | Apache POI 5.2 (Excel), OpenPDF 2.0 (PDF), Recharts 3.9 (Charts) |

---

## 🏗️ System Architecture

```text
                    ┌─────────────────────────┐
                    │       React Frontend    │
                    │       React + Vite      │
                    └────────────┬────────────┘
                                 │
                                 │ REST API (JSON / HTTP)
                                 ▼
                    ┌─────────────────────────┐
                    │    Spring Boot Backend  │
                    │                         │
                    │  Controllers            │
                    │  Services               │
                    │  Security / JWT         │
                    │  OAuth2 Client          │
                    │  Business Logic         │
                    └────────────┬────────────┘
                                 │
                                 │ JPA / Hibernate ORM
                                 ▼
                    ┌─────────────────────────┐
                    │       PostgreSQL        │
                    │        Database         │
                    └────────────┬────────────┘
```

---

## 📂 Project Structure

```text
Lab-Resource-Utilization-Platform
│
├── lab-platform-backend
│   ├── src
│   │   ├── main
│   │   │   ├── java/com/labplatform/lab_platform_backend
│   │   │   │   ├── config
│   │   │   │   ├── controller
│   │   │   │   ├── dto
│   │   │   │   ├── entity
│   │   │   │   ├── repository
│   │   │   │   ├── service
│   │   │   │   └── util
│   │   │   └── resources
│   │   │       └── application.properties
│   │   └── test
│   ├── pom.xml
│   └── uploads
│
├── lab-platform-frontend
│   ├── src
│   │   ├── assets
│   │   ├── components
│   │   ├── context
│   │   ├── pages
│   │   ├── services
│   │   └── App.jsx
│   ├── public
│   ├── package.json
│   └── vite.config.js
│
├── Images
│   ├── billing.png
│   ├── booking.png
│   ├── certificates.png
│   ├── dashboard.png
│   ├── equipment.png
│   ├── institutions.png
│   ├── laboratories.png
│   ├── login.png
│   ├── maintenance.png
│   ├── notifications.png
│   ├── reports.png
│   ├── sharing.png
│   ├── users.png
│   └── utilization.png
│
├── equipment_certificates
└── README.md
```

---

## 🚀 Development Milestones

### 🟢 Milestone 1 — Project Foundation
- Set up the Lab Resource Utilization Platform project repository and multi-module layout.
- Initialized Spring Boot backend using Maven with Java 17.
- Initialized React frontend using Vite.
- Configured PostgreSQL as the relational database engine.
- Defined initial database entities and Spring Data JPA repositories.
- Implemented user registration, password hashing, and login authentication.
- Configured Spring Security and stateless JWT token issuance.
- Implemented core equipment management CRUD endpoints.
- Created responsive dashboard layout and frontend component structure.
- Integrated Axios REST API communication between React and Spring Boot.

### 🟡 Milestone 2 — Core Platform & RBAC
- Implemented 6-Tier Role-Based Access Control (RBAC):
  - `RESEARCHER`
  - `LAB_TECHNICIAN`
  - `LAB_MANAGER`
  - `DEPARTMENT_HEAD`
  - `INSTITUTION_ADMIN`
  - `SYSTEM_ADMIN`
- Implemented backend API authorization via `@PreAuthorize` annotations.
- Added protected React Router frontend navigation guards.
- Implemented equipment reservation scheduling and collision detection.
- Developed automated system notification triggers.
- Built maintenance work order tracking modules.
- Implemented laboratory and institution management modules.
- Created inter-institution equipment sharing request workflows.
- Implemented automated waiting-list queueing for equipment date collisions.
- Enhanced Google OAuth2 login integration with explicit account chooser (`prompt=select_account`).

### 🔵 Milestone 3 — Advanced Features & Analytics
- Advanced RBAC refinement: Enforced strict laboratory-level and institution-level data isolation (`SecurityUtil`).
- Developed Equipment Calibration Certificate compliance tracking with document attachment.
- Added calibration certificate expiry monitoring with warning banners.
- Implemented preventive maintenance auto-scheduling (High 15d, Medium 20d, Low 30d).
- Built simulated chargeback billing and 10% inter-institution fee calculation.
- Implemented equipment utilization percentage, usage hours, and idle days tracking.
- Developed Recharts-powered analytics dashboards and equipment usage rankings.
- Implemented server-side PDF (OpenPDF) and Excel (Apache POI) operational report exporters.
- Added System Admin user approval and rejection workflow queue.
- Created dedicated user interfaces for Billing, Certificate Management, Reports, Utilization Analytics, and User Approvals.

---

## 🧪 Testing & Pre-Deployment Validation

The application underwent complete automated end-to-end testing before deployment.

### Build Validation
- **Backend (`mvn compile`)**: `BUILD SUCCESS` — 103 Java source files compiled cleanly with 0 errors.
- **Frontend (`npm run build`)**: `SUCCESS` — 708 modules transformed cleanly via Vite with 0 build errors.

### Functional API & Workflow Validation

| Test Category | Scenario Tested | Status | Notes |
| :--- | :--- | :---: | :--- |
| **Authentication** | System Admin Login | **PASS** | Valid JWT token returned |
| **Authentication** | Invalid Password / Email | **PASS** | `401 Unauthorized` returned |
| **Authentication** | User Registration & Duplicate Email | **PASS** | Status `PENDING` set / Duplicate returns `400` |
| **Google OAuth2** | OAuth Authorization Redirect | **PASS** | `302 Found` with `prompt=select_account` |
| **RBAC** | Admin vs Researcher Endpoint Access | **PASS** | `SYSTEM_ADMIN` allowed (`200`), `RESEARCHER` blocked (`403`) |
| **Data Isolation** | Lab Manager Scoped Billing & IDOR | **PASS** | Scoped to own lab / Cross-lab IDOR blocked (`400`/`403`) |
| **Equipment** | Inventory & Shared Discovery | **PASS** | Internal catalog & `shared=true` query functional |
| **Maintenance** | Work Orders & Preventive Servicing | **PASS** | Work orders & tier calculations functional |
| **Certificates** | Compliance & Expiry Monitoring | **PASS** | Expiry alerts & document URLs functional |
| **Utilization** | Usage Statistics & Ranking | **PASS** | Usage hours, idle days & tier rankings computed |
| **Reports** | PDF & Excel Export Generation | **PASS** | Binary PDF (2.0 KB) & Excel (4.2 KB) streams generated |
| **Notifications** | Alert System Queries | **PASS** | User alerts fetched cleanly |

### Summary Test Result
```text
Total Tests Executed : 22
Passed               : 22
Failed               : 0
Blocked / Unverified : 0
Success Rate         : 100%
```

---

## 📸 Application Screenshots

### 🔐 Login & Authentication
![Login & Authentication](Images/login.png)

### 📊 Dashboard
![Dashboard](Images/dashboard.png)

### 🏛️ Institution Management
![Institution Management](Images/institutions.png)

### 🧪 Laboratory Management
![Laboratory Management](Images/laboratories.png)

### 🔬 Equipment Management
![Equipment Management](Images/equipment.png)

### 📅 Booking Management
![Booking Management](Images/booking.png)

### 📈 Equipment Utilization Analytics
![Equipment Utilization Analytics](Images/utilization.png)

### 📜 Equipment Certificates & Compliance
![Equipment Certificates & Compliance](Images/certificates.png)

### 🔄 Inter-Institution Resource Sharing
![Inter-Institution Resource Sharing](Images/sharing.png)

### 💰 Cost & Billing
![Cost & Billing](Images/billing.png)

### 📑 Reports & Export
![Reports & Export](Images/reports.png)

### 🔔 Notifications
![Notifications](Images/notifications.png)

### 👥 User Management
![User Management](Images/users.png)

---

## ⚙️ Installation & Local Setup

### Prerequisites
Make sure the following software tools are installed on your machine:
- **Java 17 LTS**
- **Apache Maven 3.8+**
- **Node.js v18+ & npm**
- **PostgreSQL 14+**

### 1. Database Setup
Create a PostgreSQL database named `lab_platform_db`:
```sql
CREATE DATABASE lab_platform_db;
```

### 2. Backend Setup
Navigate to the backend directory:
```bash
cd lab-platform-backend
```
Configure local database credentials in `src/main/resources/application.properties` or set environment variables:
```bash
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/lab_platform_db
export SPRING_DATASOURCE_USERNAME=postgres
export SPRING_DATASOURCE_PASSWORD=your_postgres_password
export JWT_SECRET=your_base64_jwt_secret_key
```
Run the Spring Boot application:
```bash
mvn spring-boot:run
```
The backend server will start on `http://localhost:8080`.

### 3. Frontend Setup
Open a new terminal and navigate to the frontend directory:
```bash
cd lab-platform-frontend
```
Install dependencies:
```bash
npm install
```
Start the Vite development server:
```bash
npm run dev
```
The React frontend application will open on `http://localhost:5173`.

---

## 🔐 Environment Configuration

To ensure production security, sensitive credentials must be injected via environment variables rather than hardcoded in source control:

| Environment Variable | Description | Example Value |
| :--- | :--- | :--- |
| `SPRING_DATASOURCE_URL` | PostgreSQL JDBC connection URL | `jdbc:postgresql://host:5432/db_name` |
| `SPRING_DATASOURCE_USERNAME` | Database username | `postgres` |
| `SPRING_DATASOURCE_PASSWORD` | Database password | `secret_password` |
| `JWT_SECRET` | Secret key for signing HMAC-SHA512 JWT tokens | `min_64_character_random_string` |
| `GOOGLE_CLIENT_ID` | Google OAuth2 Application Client ID | `xxxx.apps.googleusercontent.com` |
| `GOOGLE_CLIENT_SECRET` | Google OAuth2 Application Client Secret | `GOCSPX-xxxx` |

---

## ☁️ Cloud Deployment Architecture

The application is prepared for cloud deployment on **Render**:

```text
                    ┌─────────────────────────┐
                    │     React Frontend      │
                    │    Render Static Site   │
                    └────────────┬────────────┘
                                 │
                                 │ REST API (HTTPS)
                                 ▼
                    ┌─────────────────────────┐
                    │    Spring Boot Backend  │
                    │    Render Web Service   │
                    └────────────┬────────────┘
                                 │
                                 ▼
                    ┌─────────────────────────┐
                    │    Render PostgreSQL    │
                    │     Managed Database    │
                    └────────────┬────────────┘
```

### Deployment Checklist & Status
- [x] PostgreSQL database on Render — **Configured**
- [ ] Backend deployment — **In Progress**
- [ ] Frontend deployment — **Pending**
- [ ] Production environment variables configuration — **Pending**
- [ ] Google OAuth production domain callback registration — **Pending**
- [ ] End-to-end live HTTPS testing — **Pending**

---

## 🔮 Future Enhancements

- 📧 **Automated Email Notifications**: Integrate SendGrid/JavaMailSender for email alerts.
- 🏷️ **QR-Code Equipment Tracking**: QR-code generation & mobile scanning for fast equipment check-in/out.
- 📡 **IoT Hardware Integration**: Connect physical IoT power meters for real-time equipment runtime monitoring.
- 📱 **Mobile Application**: Cross-platform mobile app built using React Native.
- 📊 **Demand Forecasting Analytics**: Predictive AI models to forecast peak lab equipment usage seasons.

---

## 👨‍💻 Author

**Naresh Kumar V**  
- **GitHub**: [github.com/NARESH-KUMAR-V](https://github.com/NARESH-KUMAR-V)