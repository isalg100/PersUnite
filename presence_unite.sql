BEGIN TRANSACTION;

-- =================================================
-- 1. REFERENCE DATA (FROM ADMIN - READ ONLY LOGIC)
-- =================================================

CREATE TABLE IF NOT EXISTS agent (
    MATRIM TEXT PRIMARY KEY,
    AFFECX TEXT,
    NOMX TEXT,
    PRENX TEXT,
    SEXE TEXT,
    DATNSX DATE,
    AFFEC_LIB TEXT,
    STATUS TEXT DEFAULT 'ACTIVE'
);

CREATE TABLE IF NOT EXISTS WorkGroup (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nom TEXT NOT NULL,
    unite_id INTEGER
);

CREATE TABLE IF NOT EXISTS unite (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    libelle TEXT NOT NULL UNIQUE
);

-- =================================================
-- 2. ASSIGNMENTS
-- =================================================

CREATE TABLE IF NOT EXISTS EmployeeAssignment (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    agent_matrim TEXT NOT NULL,
    group_id INTEGER NOT NULL,
    date_debut DATE NOT NULL,
    date_fin DATE,
    source TEXT DEFAULT 'IMPORT',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- =================================================
-- 3. EVENT SYSTEM (CORE)
-- =================================================

CREATE TABLE IF NOT EXISTS EventLog (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    agent_id TEXT NOT NULL,
    event_type TEXT NOT NULL, -- ABSENCE / LATE / EARLY_EXIT
    event_date DATE NOT NULL,
    value TEXT,
    justified INTEGER DEFAULT 0,
    source TEXT DEFAULT 'LOCAL',
    synced INTEGER DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- =================================================
-- 4. ATTENDANCE (OPTIONAL DERIVED)
-- =================================================

CREATE TABLE IF NOT EXISTS attendance (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    agent_id TEXT,
    date DATE,
    segment TEXT,
    status TEXT,
    retard INTEGER DEFAULT 0,
    early_exit INTEGER DEFAULT 0,
    source TEXT,
    synced INTEGER DEFAULT 0
);

-- =================================================
-- 5. SYNC SYSTEM
-- =================================================

CREATE TABLE IF NOT EXISTS SyncLog (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    file_name TEXT,
    version INTEGER,
    type TEXT, -- IMPORT / EXPORT
    status TEXT,
    message TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS SyncVersion (
    id INTEGER PRIMARY KEY,
    last_version INTEGER DEFAULT 0
);

-- =================================================
-- 6. CONFIG
-- =================================================

CREATE TABLE IF NOT EXISTS AppConfig (
    key TEXT PRIMARY KEY,
    value TEXT
);

-- =================================================
-- 7. REPORT CACHE (OPTIONAL OPTIMIZATION)
-- =================================================

CREATE TABLE IF NOT EXISTS AttendanceReportCache (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    agent_id TEXT,
    period_start DATE,
    period_end DATE,
    report_text TEXT,
    generated_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

COMMIT;





SELECT 
    a.MATRIM,
    a.NOMX,
    wg.nom,
    e.event_type,
    e.event_date,
    e.value
FROM agent a
LEFT JOIN EventLog e ON a.MATRIM = e.agent_id
LEFT JOIN EmployeeAssignment ea ON a.MATRIM = ea.agent_matrim AND ea.date_fin IS NULL
LEFT JOIN WorkGroup wg ON ea.group_id = wg.id
WHERE a.MATRIM = ?
ORDER BY e.event_date;