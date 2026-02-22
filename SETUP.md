# POS BIT G2 Setup

Spring Boot backend + Vue frontend for a POS management system.

## 1) Prerequisites

- Java 21+ (`java -version`)
- Node 20+ (`node -v`)
- MySQL running on `localhost:3306`

## 2) Database

Create database:

```sql
CREATE DATABASE IF NOT EXISTS pos_bit;
```

Current backend config is in `src/main/resources/application.yaml`:

- DB: `jdbc:mysql://localhost:3306/pos_bit?useSSL=false`
- User: `root`
- Password: `root`
- Port: `8081`

Adjust if your local MySQL credentials differ.

## 3) Run Backend

```powershell
.\mvnw.cmd spring-boot:run
```

Backend base URL: `http://localhost:8081`

If port `8081` is already used:

```powershell
netstat -ano | findstr :8081
taskkill /PID <PID> /F
```

## 4) Run Frontend

```powershell
npm --prefix frontend install
npm --prefix frontend run dev
```

Frontend URL: `http://localhost:5173`

## 5) Default Accounts and Seed Data

The app seeds demo data at startup.

- Default admin:
  - Username: `admin_pos`
  - Password: `admin_123`
- Demo cashiers:
  - `cashier01` / `cashier123`
  - `cashier02` / `cashier123`

It also seeds categories, products, and sample orders for testing dashboard/history/POS flows.

## 6) Role Rules

- Registration always creates `ROLE_USER`.
- Only the default admin account can keep `ROLE_ADMIN`.
- Any other user attempting admin assignment is forced back to `ROLE_USER`.

## 7) Profile Features

Logged-in users can:

- View profile (`/profile`)
- Update `fullName`, `email`, `phone`, `address`
- Change password (`currentPassword`, `newPassword`, `confirmPassword`)

## 8) Useful API Endpoints

Public:

- `POST /users/register`
- `POST /users/login`

Protected:

- `GET /users/me`
- `PUT /users/me`
- `PUT /users/me/password`
- `GET /products`
- `GET /product-category`
- `POST /sales-orders`
- `POST /sales-orders/all`
- `POST /sales-orders/summary`

## 9) MySQL Workbench Quick Connect

Use these fields:

- Hostname: `127.0.0.1`
- Port: `3306`
- Username: `root`
- Password: `root` (or your local one)
- Default Schema: `pos_bit`
