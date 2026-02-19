-- ============================================
-- ProManage Solutions Pvt. Ltd.
-- Full Database Schema with 10 Weeks Data
-- ============================================

DROP TABLE IF EXISTS scheduled_projects;
DROP TABLE IF EXISTS projects;

-- ============================================
-- PROJECTS TABLE
-- ============================================

DROP SEQUENCE IF EXISTS project_seq;
CREATE SEQUENCE project_seq START 101;

CREATE TABLE projects (
    project_id      VARCHAR(10) PRIMARY KEY DEFAULT 'PROJ-' || LPAD(nextval('project_seq')::text, 3, '0'),
    title           VARCHAR(100) NOT NULL,
    deadline        INT NOT NULL CHECK (deadline >= 1),
    revenue         DECIMAL(12,2) NOT NULL CHECK (revenue > 0),
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completion_date DATE,
    status          VARCHAR(20) DEFAULT 'PENDING',
    project_type    VARCHAR(50) DEFAULT 'GENERAL'
);

-- ============================================
-- SCHEDULE TABLE
-- ============================================

CREATE TABLE scheduled_projects (
    schedule_id     SERIAL PRIMARY KEY,
    project_id      VARCHAR(10) NOT NULL REFERENCES projects(project_id),
    day_number      INT NOT NULL CHECK (day_number >= 1 AND day_number <= 14),
    day_name        VARCHAR(10) NOT NULL,
    week_start_date DATE,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================
-- INSERT 100 PROJECTS
-- ============================================

INSERT INTO projects (project_id, title, deadline, revenue, status) VALUES
-- WEEK 1
('PROJ-001','Week1 - Enterprise Project 1',1,51000, 'COMPLETED'),
('PROJ-002','Week1 - Enterprise Project 2',2,52000, 'COMPLETED'),
('PROJ-003','Week1 - Enterprise Project 3',3,53000, 'COMPLETED'),
('PROJ-004','Week1 - Enterprise Project 4',4,54000, 'COMPLETED'),
('PROJ-005','Week1 - Enterprise Project 5',5,55000, 'COMPLETED'),
('PROJ-006','Week1 - Enterprise Project 6',1,56000, 'COMPLETED'),
('PROJ-007','Week1 - Enterprise Project 7',2,57000, 'COMPLETED'),
('PROJ-008','Week1 - Enterprise Project 8',3,58000, 'COMPLETED'),
('PROJ-009','Week1 - Enterprise Project 9',4,59000, 'COMPLETED'),
('PROJ-010','Week1 - Enterprise Project 10',5,60000, 'COMPLETED'),

-- WEEK 2
('PROJ-011','Week2 - Enterprise Project 11',1,61000, 'COMPLETED'),
('PROJ-012','Week2 - Enterprise Project 12',2,62000, 'COMPLETED'),
('PROJ-013','Week2 - Enterprise Project 13',3,63000, 'COMPLETED'),
('PROJ-014','Week2 - Enterprise Project 14',4,64000, 'COMPLETED'),
('PROJ-015','Week2 - Enterprise Project 15',5,65000, 'COMPLETED'),
('PROJ-016','Week2 - Enterprise Project 16',1,66000, 'COMPLETED'),
('PROJ-017','Week2 - Enterprise Project 17',2,67000, 'COMPLETED'),
('PROJ-018','Week2 - Enterprise Project 18',3,68000, 'COMPLETED'),
('PROJ-019','Week2 - Enterprise Project 19',4,69000, 'COMPLETED'),
('PROJ-020','Week2 - Enterprise Project 20',5,70000, 'COMPLETED'),

-- WEEK 3
('PROJ-021','Week3 - Enterprise Project 21',1,71000, 'COMPLETED'),
('PROJ-022','Week3 - Enterprise Project 22',2,72000, 'COMPLETED'),
('PROJ-023','Week3 - Enterprise Project 23',3,73000, 'COMPLETED'),
('PROJ-024','Week3 - Enterprise Project 24',4,74000, 'COMPLETED'),
('PROJ-025','Week3 - Enterprise Project 25',5,75000, 'COMPLETED'),
('PROJ-026','Week3 - Enterprise Project 26',1,76000, 'COMPLETED'),
('PROJ-027','Week3 - Enterprise Project 27',2,77000, 'COMPLETED'),
('PROJ-028','Week3 - Enterprise Project 28',3,78000, 'COMPLETED'),
('PROJ-029','Week3 - Enterprise Project 29',4,79000, 'COMPLETED'),
('PROJ-030','Week3 - Enterprise Project 30',5,80000, 'COMPLETED'),

-- WEEK 4
('PROJ-031','Week4 - Enterprise Project 31',1,81000, 'COMPLETED'),
('PROJ-032','Week4 - Enterprise Project 32',2,82000, 'COMPLETED'),
('PROJ-033','Week4 - Enterprise Project 33',3,83000, 'COMPLETED'),
('PROJ-034','Week4 - Enterprise Project 34',4,84000, 'COMPLETED'),
('PROJ-035','Week4 - Enterprise Project 35',5,85000, 'COMPLETED'),
('PROJ-036','Week4 - Enterprise Project 36',1,86000, 'COMPLETED'),
('PROJ-037','Week4 - Enterprise Project 37',2,87000, 'COMPLETED'),
('PROJ-038','Week4 - Enterprise Project 38',3,88000, 'COMPLETED'),
('PROJ-039','Week4 - Enterprise Project 39',4,89000, 'COMPLETED'),
('PROJ-040','Week4 - Enterprise Project 40',5,90000, 'COMPLETED'),

-- WEEK 5
('PROJ-041','Week5 - Enterprise Project 41',1,91000, 'COMPLETED'),
('PROJ-042','Week5 - Enterprise Project 42',2,92000, 'COMPLETED'),
('PROJ-043','Week5 - Enterprise Project 43',3,93000, 'COMPLETED'),
('PROJ-044','Week5 - Enterprise Project 44',4,94000, 'COMPLETED'),
('PROJ-045','Week5 - Enterprise Project 45',5,95000, 'COMPLETED'),
('PROJ-046','Week5 - Enterprise Project 46',1,96000, 'COMPLETED'),
('PROJ-047','Week5 - Enterprise Project 47',2,97000, 'COMPLETED'),
('PROJ-048','Week5 - Enterprise Project 48',3,98000, 'COMPLETED'),
('PROJ-049','Week5 - Enterprise Project 49',4,99000, 'COMPLETED'),
('PROJ-050','Week5 - Enterprise Project 50',5,100000, 'COMPLETED'),

-- WEEK 6
('PROJ-051','Week6 - Enterprise Project 51',1,101000, 'COMPLETED'),
('PROJ-052','Week6 - Enterprise Project 52',2,102000, 'COMPLETED'),
('PROJ-053','Week6 - Enterprise Project 53',3,103000, 'COMPLETED'),
('PROJ-054','Week6 - Enterprise Project 54',4,104000, 'COMPLETED'),
('PROJ-055','Week6 - Enterprise Project 55',5,105000, 'COMPLETED'),
('PROJ-056','Week6 - Enterprise Project 56',1,106000, 'COMPLETED'),
('PROJ-057','Week6 - Enterprise Project 57',2,107000, 'COMPLETED'),
('PROJ-058','Week6 - Enterprise Project 58',3,108000, 'COMPLETED'),
('PROJ-059','Week6 - Enterprise Project 59',4,109000, 'COMPLETED'),
('PROJ-060','Week6 - Enterprise Project 60',5,110000, 'COMPLETED'),

-- WEEK 7
('PROJ-061','Week7 - Enterprise Project 61',1,111000, 'COMPLETED'),
('PROJ-062','Week7 - Enterprise Project 62',2,112000, 'COMPLETED'),
('PROJ-063','Week7 - Enterprise Project 63',3,113000, 'COMPLETED'),
('PROJ-064','Week7 - Enterprise Project 64',4,114000, 'COMPLETED'),
('PROJ-065','Week7 - Enterprise Project 65',5,115000, 'COMPLETED'),
('PROJ-066','Week7 - Enterprise Project 66',1,116000, 'COMPLETED'),
('PROJ-067','Week7 - Enterprise Project 67',2,117000, 'COMPLETED'),
('PROJ-068','Week7 - Enterprise Project 68',3,118000, 'COMPLETED'),
('PROJ-069','Week7 - Enterprise Project 69',4,119000, 'COMPLETED'),
('PROJ-070','Week7 - Enterprise Project 70',5,120000, 'COMPLETED'),

-- WEEK 8
('PROJ-071','Week8 - Enterprise Project 71',1,121000, 'COMPLETED'),
('PROJ-072','Week8 - Enterprise Project 72',2,122000, 'COMPLETED'),
('PROJ-073','Week8 - Enterprise Project 73',3,123000, 'COMPLETED'),
('PROJ-074','Week8 - Enterprise Project 74',4,124000, 'COMPLETED'),
('PROJ-075','Week8 - Enterprise Project 75',5,125000, 'COMPLETED'),
('PROJ-076','Week8 - Enterprise Project 76',1,126000, 'COMPLETED'),
('PROJ-077','Week8 - Enterprise Project 77',2,127000, 'COMPLETED'),
('PROJ-078','Week8 - Enterprise Project 78',3,128000, 'COMPLETED'),
('PROJ-079','Week8 - Enterprise Project 79',4,129000, 'COMPLETED'),
('PROJ-080','Week8 - Enterprise Project 80',5,130000, 'COMPLETED'),

-- WEEK 9
('PROJ-081','Week9 - Enterprise Project 81',1,131000, 'COMPLETED'),
('PROJ-082','Week9 - Enterprise Project 82',2,132000, 'COMPLETED'),
('PROJ-083','Week9 - Enterprise Project 83',3,133000, 'COMPLETED'),
('PROJ-084','Week9 - Enterprise Project 84',4,134000, 'COMPLETED'),
('PROJ-085','Week9 - Enterprise Project 85',5,135000, 'COMPLETED'),
('PROJ-086','Week9 - Enterprise Project 86',1,136000, 'COMPLETED'),
('PROJ-087','Week9 - Enterprise Project 87',2,137000, 'COMPLETED'),
('PROJ-088','Week9 - Enterprise Project 88',3,138000, 'COMPLETED'),
('PROJ-089','Week9 - Enterprise Project 89',4,139000, 'COMPLETED'),
('PROJ-090','Week9 - Enterprise Project 90',5,140000, 'COMPLETED'),

-- WEEK 10
('PROJ-091','Week10 - Enterprise Project 91',1,141000, 'COMPLETED'),
('PROJ-092','Week10 - Enterprise Project 92',2,142000, 'COMPLETED'),
('PROJ-093','Week10 - Enterprise Project 93',3,143000, 'COMPLETED'),
('PROJ-094','Week10 - Enterprise Project 94',4,144000, 'COMPLETED'),
('PROJ-095','Week10 - Enterprise Project 95',5,145000, 'COMPLETED'),
('PROJ-096','Week10 - Enterprise Project 96',1,146000, 'COMPLETED'),
('PROJ-097','Week10 - Enterprise Project 97',2,147000, 'COMPLETED'),
('PROJ-098','Week10 - Enterprise Project 98',3,148000, 'COMPLETED'),
('PROJ-099','Week10 - Enterprise Project 99',4,149000, 'COMPLETED'),
('PROJ-100','Week10 - Enterprise Project 100',5,150000, 'COMPLETED');

-- ============================================
-- INSERT SCHEDULE DATA (2 PROJECTS PER DAY)
-- ============================================

INSERT INTO scheduled_projects (project_id, day_number, day_name, week_start_date) VALUES
-- WEEK 1 (2026-01-05)
('PROJ-001',1,'Monday','2026-01-05'),('PROJ-002',1,'Monday','2026-01-05'),
('PROJ-003',2,'Tuesday','2026-01-05'),('PROJ-004',2,'Tuesday','2026-01-05'),
('PROJ-005',3,'Wednesday','2026-01-05'),('PROJ-006',3,'Wednesday','2026-01-05'),
('PROJ-007',4,'Thursday','2026-01-05'),('PROJ-008',4,'Thursday','2026-01-05'),
('PROJ-009',5,'Friday','2026-01-05'),('PROJ-010',5,'Friday','2026-01-05'),

-- WEEK 2 (2026-01-12)
('PROJ-011',1,'Monday','2026-01-12'),('PROJ-012',1,'Monday','2026-01-12'),
('PROJ-013',2,'Tuesday','2026-01-12'),('PROJ-014',2,'Tuesday','2026-01-12'),
('PROJ-015',3,'Wednesday','2026-01-12'),('PROJ-016',3,'Wednesday','2026-01-12'),
('PROJ-017',4,'Thursday','2026-01-12'),('PROJ-018',4,'Thursday','2026-01-12'),
('PROJ-019',5,'Friday','2026-01-12'),('PROJ-020',5,'Friday','2026-01-12');
