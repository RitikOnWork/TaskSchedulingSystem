-- ProManage Solutions Pvt. Ltd.
-- Database Schema for Project Scheduling System

DROP TABLE IF EXISTS scheduled_projects;
DROP TABLE IF EXISTS projects;
DROP SEQUENCE IF EXISTS project_seq;

CREATE SEQUENCE project_seq START 1 INCREMENT 1;

CREATE TABLE projects (
    project_id      VARCHAR(10) PRIMARY KEY,
    title           VARCHAR(100) NOT NULL,
    deadline        INT NOT NULL CHECK (deadline >= 1 AND deadline <= 5),
    revenue         DECIMAL(12,2) NOT NULL CHECK (revenue > 0),
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE scheduled_projects (
    schedule_id     SERIAL PRIMARY KEY,
    project_id      VARCHAR(10) NOT NULL REFERENCES projects(project_id),
    day_number      INT NOT NULL CHECK (day_number >= 1 AND day_number <= 5),
    day_name        VARCHAR(10) NOT NULL,
    week_start_date DATE,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO projects (project_id, title, deadline, revenue) VALUES
    ('PROJ-001', 'UI Design for FinTech App',         2, 80000.00),
    ('PROJ-002', 'Backend Development Portal',        1, 50000.00),
    ('PROJ-003', 'QA Testing E-Commerce Site',        3, 40000.00),
    ('PROJ-004', 'Deployment Healthcare System',      2, 90000.00),
    ('PROJ-005', 'Mobile App UI Redesign',            3, 60000.00),
    ('PROJ-006', 'API Integration Module',            1, 70000.00),
    ('PROJ-007', 'Database Migration Project',        4, 55000.00),
    ('PROJ-008', 'Security Audit Web Platform',       5, 45000.00);