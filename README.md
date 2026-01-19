# Pharmacy Management System

A comprehensive Pharmacy Management System built with a modern tech stack. This application facilitates inventory management, sales tracking, and administrative tasks for a pharmacy.

## 🚀 Features

- **Inventory Management**: Add, update, view, and delete medicines.
- **Sales Tracking**: Record sales and monitor stock levels in real-time.
- **PDF Generation**: Generate invoices or reports for sales and inventory.
- **Email Notifications**: Integrated email system for alerts and communication.
- **Secure Authentication**: JWT-based authentication with role-based access control (Admin/User).
- **Responsive UI**: A modern, interactive dashboard built with React and Vite.

## 🛠️ Technology Stack

### Backend
- **Java 21**
- **Spring Boot 3.2.1**
- **Spring Security** (JWT Authentication)
- **Spring Data MongoDB**
- **MongoDB** (NoSQL Database)
- **Gradle** (Build Tool)

### Frontend
- **React 19**
- **Vite**
- **TypeScript**
- **Axios** (API Communication)
- **React Router**

### Infrastructure
- **Docker & Docker Compose**

## 🏃 Running the Application

### Prerequisites
- **Java 21 or higher**
- **Node.js 20 or higher**
- **MongoDB** (running locally or via Docker)
- **Docker** (optional, for containerized execution)

### 1. Local Development Setup

#### Backend
1. Navigate to the root directory.
2. Ensure your `.env` file is configured (refer to `.env.example`).
3. Run the application using the Gradle wrapper:
   ```powershell
   .\gradlew.bat bootRun
   ```
   The backend will start on `http://localhost:8080`.

#### Frontend
1. Navigate to the `frontend` directory:
   ```powershell
   cd frontend
   ```
2. Install dependencies:
   ```powershell
   npm install
   ```
3. Start the development server:
   ```powershell
   npm run dev
   ```
   The frontend will start on `http://localhost:5173`.

### 2. Docker Setup (Recommended)

To run the entire stack (Database, Backend, and Frontend) using Docker:

1. Ensure Docker Desktop is running.
2. From the root directory, run:
   ```powershell
   docker-compose up --build
   ```
3. Access the application:
   - **Frontend**: `http://localhost:3000`
   - **Backend API**: `http://localhost:8080`

## 📁 Project Structure

- `src/main/java/com/pharmacy`: Backend Java source files (Controllers, Services, Models, Repositories).
- `frontend/`: React frontend source files and configuration.
- `docker-compose.yml`: Infrastructure orchestration.
- `.env`: Environment variables configuration.
- `INSTRUCTIONS.md`: Detailed step-by-step setup guide.
