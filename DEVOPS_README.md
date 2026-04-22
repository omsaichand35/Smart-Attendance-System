# Smart Attendance - DevOps & Build Architecture Update

This document outlines the recent architectural improvements to the Smart Attendance System, transforming it into a full DevOps-ready project. This includes containerization, CI/CD automation, and modernizing the Android build configuration.

## 🚀 1. DevOps Implementation

### Dockerization (`Dockerfile` & `docker-compose.yml`)
The backend is now fully containerized, ensuring consistent environments across development and production.
- **Backend Service:** Containerized the Spring Boot application using a multi-stage Docker build, exposing port `8082`.
- **Database Service:** Integrated the official `mssql/server:2022-latest` image.
- **Orchestration:** Implemented `docker-compose.yml` to spin up both the application API and the database on a shared internal bridge network (`attendance-network`).
- **Data Persistence:** Defined Docker volumes (`sqlserver_data`) to prevent data loss upon database container restarts.

**How to run locally:**
```bash
docker-compose up -d --build
```
*The backend API will be available at `http://localhost:8082`.*

### Continuous Integration & CI/CD (`Jenkinsfile`)
A complete Jenkins pipeline has been established to automate the software development lifecycle.
- **Build Stage:** Automates Maven builds without requiring local Java installations via the Docker agent.
- **Test Stage:** Automatically provisions an isolated SQL Server test container to execute Spring Boot integration tests securely.
- **Security Scanning:** Integrated Trivy to scan the generated Docker images for vulnerabilities before deployment.
- **Delivery Stage:** Readies the checked and verified Docker image for production server deployment.

## 📱 2. Android Configuration Fixes

The Android frontend experienced significant build blockers related to JVM toolchain resolution and outdated Gradle configurations. These have been resolved entirely:

- **Kotlin DSL & Compiler Options:** Migrated the project away from deprecated `kotlinOptions` and automated toolchain downloads. It now utilizes strict `compilerOptions` mapped explicitly to Java 11 bytecode (`JvmTarget.JVM_11`). This eliminates the "Missing Java Installation" errors.
- **Dependency & AGP Alignment:** Downgraded and locked the Android Gradle Plugin (AGP) to `8.13.0` and the Gradle Wrapper to `8.13` to match the exact environment capabilities without breaking existing Kotlin features.
- **Network Routing:** Updated the `RetrofitClient.kt` base URL to target `http://10.0.2.2:8082/`. This ensures the Android emulator correctly maps its internal loopback to the host machine's published Docker API port.

## 📂 3. Minor Backend Tweaks
- **`mvnw` Permissions:** Elevated script permissions (`chmod +x`) to assure the Jenkins runner and Docker build stages can execute wrapper commands smoothly.
- **Database Security:** Cleaned `application.properties` to ensure smooth JDBC connections from inside the Docker bridge network to the local SQL server utilizing standard SQL authentication (`SA` user) instead of strict Windows Integrated Security.

