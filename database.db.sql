BEGIN TRANSACTION;
CREATE TABLE IF NOT EXISTS "AppConfig" (
	"key"	TEXT,
	"value"	TEXT,
	PRIMARY KEY("key")
);
CREATE TABLE IF NOT EXISTS "AttendanceReportCache" (
	"id"	INTEGER,
	"agent_id"	TEXT,
	"period_start"	DATE,
	"period_end"	DATE,
	"report_text"	TEXT,
	"generated_at"	DATETIME DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY("id" AUTOINCREMENT)
);
CREATE TABLE IF NOT EXISTS "EmployeeAssignment" (
	"id"	INTEGER,
	"agent_matrim"	TEXT NOT NULL,
	"group_id"	INTEGER NOT NULL,
	"date_debut"	DATE NOT NULL,
	"date_fin"	DATE,
	"source"	TEXT DEFAULT 'IMPORT',
	"created_at"	DATETIME DEFAULT CURRENT_TIMESTAMP,
	"uuid"	TEXT,
	"version"	INTEGER DEFAULT 0,
	"last_modified"	DATETIME DEFAULT CURRENT_TIMESTAMP,
	"deleted"	INTEGER DEFAULT 0,
	"justification"	TEXT,
	"remarque"	TEXT,
	"batch_id"	INTEGER,
	"justification_id"	INTEGER,
	"validated"	INTEGER DEFAULT 1,
	"active"	INTEGER DEFAULT 1,
	PRIMARY KEY("id" AUTOINCREMENT)
);
CREATE TABLE IF NOT EXISTS "EventLog" (
	"id"	INTEGER,
	"agent_id"	TEXT NOT NULL,
	"event_type"	TEXT NOT NULL,
	"event_date"	DATE NOT NULL,
	"value"	TEXT,
	"justified"	INTEGER DEFAULT 0,
	"source"	TEXT DEFAULT 'LOCAL',
	"synced"	INTEGER DEFAULT 0,
	"created_at"	DATETIME DEFAULT CURRENT_TIMESTAMP,
	"uuid"	TEXT,
	"version"	INTEGER DEFAULT 0,
	"last_modified"	DATETIME DEFAULT CURRENT_TIMESTAMP,
	"deleted"	INTEGER DEFAULT 0,
	PRIMARY KEY("id" AUTOINCREMENT)
);
CREATE TABLE IF NOT EXISTS "SyncLog" (
	"id"	INTEGER,
	"file_name"	TEXT,
	"version"	INTEGER,
	"type"	TEXT,
	"status"	TEXT,
	"message"	TEXT,
	"created_at"	DATETIME DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY("id" AUTOINCREMENT)
);
CREATE TABLE IF NOT EXISTS "SyncVersion" (
	"id"	INTEGER,
	"last_version"	INTEGER DEFAULT 0,
	PRIMARY KEY("id")
);
CREATE TABLE IF NOT EXISTS "WorkGroup" (
	"id"	INTEGER,
	"nom"	TEXT NOT NULL,
	"unite_id"	INTEGER,
	"uuid"	TEXT,
	"version"	INTEGER DEFAULT 0,
	"last_modified"	DATETIME DEFAULT CURRENT_TIMESTAMP,
	"deleted"	INTEGER DEFAULT 0,
	PRIMARY KEY("id" AUTOINCREMENT)
);
CREATE TABLE IF NOT EXISTS "agent" (
	"MATRIM"	TEXT,
	"AFFECX"	TEXT,
	"NOMX"	TEXT,
	"PRENX"	TEXT,
	"SEXE"	TEXT,
	"DATNSX"	DATE,
	"AFFEC_LIB"	TEXT,
	"STATUS"	TEXT DEFAULT 'ACTIVE',
	"uuid"	TEXT,
	"version"	INTEGER DEFAULT 0,
	"last_modified"	DATETIME DEFAULT CURRENT_TIMESTAMP,
	"deleted"	INTEGER DEFAULT 0,
	"LIBELLE"	TEXT,
	"SITFAX"	TEXT,
	"NUMSSX"	TEXT,
	"ENGAGX"	TEXT,
	"POSTEX"	TEXT,
	PRIMARY KEY("MATRIM")
);
CREATE TABLE IF NOT EXISTS "attendance" (
	"id"	INTEGER,
	"agent_id"	TEXT,
	"date"	DATE,
	"segment"	TEXT,
	"status"	TEXT,
	"retard"	INTEGER DEFAULT 0,
	"early_exit"	INTEGER DEFAULT 0,
	"source"	TEXT,
	"synced"	INTEGER DEFAULT 0,
	"uuid"	TEXT,
	"version"	INTEGER DEFAULT 0,
	"last_modified"	DATETIME DEFAULT CURRENT_TIMESTAMP,
	"deleted"	INTEGER DEFAULT 0,
	PRIMARY KEY("id" AUTOINCREMENT)
);
CREATE TABLE IF NOT EXISTS "justification" (
	"id"	INTEGER,
	"agent_id"	TEXT,
	"date_debut"	DATE,
	"date_fin"	DATE,
	"type"	TEXT,
	"remarque"	TEXT,
	"uuid"	TEXT,
	"version"	INTEGER DEFAULT 0,
	"last_modified"	DATETIME,
	"deleted"	INTEGER DEFAULT 0,
	PRIMARY KEY("id" AUTOINCREMENT)
);
CREATE TABLE IF NOT EXISTS "unite" (
	"id"	INTEGER,
	"libelle"	TEXT NOT NULL UNIQUE,
	"struct_id"	INTEGER,
	"created_at"	DATETIME DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY("id" AUTOINCREMENT)
);
CREATE TABLE IF NOT EXISTS "users" (
	"id"	INTEGER,
	"uuid"	TEXT UNIQUE,
	"username"	TEXT NOT NULL UNIQUE,
	"password_hash"	TEXT NOT NULL,
	"full_name"	TEXT,
	"role"	TEXT NOT NULL,
	"actif"	INTEGER DEFAULT 1,
	"created_at"	DATETIME DEFAULT CURRENT_TIMESTAMP,
	"last_login"	DATETIME,
	"version"	INTEGER DEFAULT 0,
	"last_modified"	DATETIME DEFAULT CURRENT_TIMESTAMP,
	"deleted"	INTEGER DEFAULT 0,
	"matrim"	TEXT,
	PRIMARY KEY("id" AUTOINCREMENT)
);
CREATE VIEW v_agent_events AS
SELECT 
    a.MATRIM,
    a.NOMX,
    wg.nom AS group_name,
    e.event_type,
    e.event_date,
    e.value
FROM agent a
LEFT JOIN EventLog e ON a.MATRIM = e.agent_id
LEFT JOIN EmployeeAssignment ea 
    ON a.MATRIM = ea.agent_matrim AND ea.date_fin IS NULL
LEFT JOIN WorkGroup wg 
    ON ea.group_id = wg.id;
CREATE UNIQUE INDEX IF NOT EXISTS "idx_agent_uuid" ON "agent" (
	"uuid"
);
CREATE UNIQUE INDEX IF NOT EXISTS "idx_assignment_uuid" ON "EmployeeAssignment" (
	"uuid"
);
CREATE UNIQUE INDEX IF NOT EXISTS "idx_attendance_uuid" ON "attendance" (
	"uuid"
);
CREATE UNIQUE INDEX IF NOT EXISTS "idx_eventlog_uuid" ON "EventLog" (
	"uuid"
);
CREATE UNIQUE INDEX IF NOT EXISTS "idx_justification_uuid" ON "justification" (
	"uuid"
);
CREATE UNIQUE INDEX IF NOT EXISTS "idx_workgroup_uuid" ON "WorkGroup" (
	"uuid"
);
COMMIT;
