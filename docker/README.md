# Docker Setup for Task App

This directory contains Docker configuration for running PostgreSQL and pgAdmin.

## Prerequisites

- Docker
- Docker Compose

## Services

### PostgreSQL
- Port: 5432
- Database: task_app
- Username: postgres
- Password: postgres

### pgAdmin
- Port: 5050
- Email: admin@admin.com
- Password: admin

## Usage

1. Start the services:
```bash
docker-compose up -d
```

2. Stop the services:
```bash
docker-compose down
```

3. View logs:
```bash
docker-compose logs -f
```

## Connecting to pgAdmin

1. Open http://localhost:5050 in your browser
2. Login with:
   - Email: admin@admin.com
   - Password: admin
3. Add a new server:
   - Name: Task App DB
   - Host: postgres
   - Port: 5432
   - Database: task_app
   - Username: postgres
   - Password: postgres

## Data Persistence

PostgreSQL data is persisted in a Docker volume named `postgres_data`. 