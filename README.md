# POS BIT G2

Point of Sale (POS) management system with:

- Spring Boot backend (JWT auth, MySQL)
- Vue 3 frontend (Vite + Pinia)

This guide helps your team set up and run the project locally.

## 1. Prerequisites

Install these first:

- Java 21+  
  Check: `java -version`
- Node.js 20+  
  Check: `node -v`
- MySQL 8+ (or MariaDB equivalent)  
  Check: `mysql --version`

## 2. Clone and Open Project

```bash
git clone <your-repo-url>
cd pos_bit_g2
```

## 3. Create Database

Run in MySQL:

```sql
CREATE DATABASE IF NOT EXISTS pos_bit;
```

## 4. Configure Backend Database

Edit `src/main/resources/application.yaml` if your DB credentials are different:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/pos_bit?useSSL=false
    username: root
    password: "root"
    driver-class-name: com.mysql.cj.jdbc.Driver

server:
  port: 8081
```

Default backend port is `8081`.

## 5. Run Backend

### Windows

```powershell
.\mvnw.cmd spring-boot:run
```

### macOS/Linux

```bash
./mvnw spring-boot:run
```

Backend URL:

- `http://localhost:8081`

If port `8081` is already used (Windows):

```powershell
netstat -ano | findstr :8081
taskkill /PID <PID> /F
```

## 6. Run Frontend

In another terminal:

```powershell
npm --prefix frontend install
npm --prefix frontend run dev
```

Frontend URL:

- `http://localhost:5173`

## 7. Default Accounts and Seed Data

App seeds demo data automatically on startup.

- Admin:
  - Username: `admin_pos`
  - Password: `admin_123`
- Demo users:
  - `cashier01` / `cashier123`
  - `cashier02` / `cashier123`

Seed includes categories, products, and sample orders.

## 8. Role Behavior

- Register = always `ROLE_USER`
- Only default admin account can keep `ROLE_ADMIN`
- Non-admin users see only their own sales/orders
- Admin can see all sales/orders and cashier username in sales history

## 9. Useful Commands

Run backend tests:

```powershell
.\mvnw.cmd test
```

Build frontend:

```powershell
npm --prefix frontend run build
```

## 10. Common Issues

DB connection error:

- Verify MySQL is running.
- Verify `application.yaml` username/password.
- Verify DB `pos_bit` exists.

Frontend shows stale user data:

- Logout and login again.
- Hard refresh browser (`Ctrl + Shift + R`).

Backend fails on port in use:

- Free `8081` or change `server.port` in `application.yaml`.

## 11. API Quick Start

Public:

- `POST /users/register`
- `POST /users/login`

Protected examples:

- `GET /users/me`
- `PUT /users/me`
- `PUT /users/me/password`
- `POST /sales-orders`
- `POST /sales-orders/all`
- `POST /sales-orders/summary`

