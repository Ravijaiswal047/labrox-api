-- Flyway Migration V7: Clustered Quartz Cronjobs and Background Automation
CREATE SCHEMA IF NOT EXISTS cron_schema;

CREATE TABLE IF NOT EXISTS cron_schema.cron_job_definition (
    id SERIAL PRIMARY KEY,
    job_name VARCHAR(100) NOT NULL UNIQUE,
    cron_expression VARCHAR(60) NOT NULL,
    description VARCHAR(255) NOT NULL,
    is_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    last_run_time TIMESTAMP WITH TIME ZONE,
    next_run_time TIMESTAMP WITH TIME ZONE,
    last_status VARCHAR(30),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS cron_schema.cron_job_execution (
    id BIGSERIAL PRIMARY KEY,
    job_name VARCHAR(100) NOT NULL,
    status VARCHAR(30) NOT NULL, -- RUNNING, SUCCESS, FAILED
    start_time TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    end_time TIMESTAMP WITH TIME ZONE,
    duration_ms BIGINT,
    records_processed INT DEFAULT 0,
    error_message TEXT
);

CREATE TABLE IF NOT EXISTS cron_schema.tat_breach_log (
    id BIGSERIAL PRIMARY KEY,
    test_id BIGINT NOT NULL,
    target_tat_minutes INT NOT NULL,
    actual_elapsed_minutes INT NOT NULL,
    breach_time TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    escalation_status VARCHAR(30) NOT NULL DEFAULT 'FLAGGED'
);

CREATE TABLE IF NOT EXISTS cron_schema.notification_queue (
    id BIGSERIAL PRIMARY KEY,
    channel VARCHAR(20) NOT NULL, -- SMS, EMAIL, WHATSAPP
    recipient VARCHAR(100) NOT NULL,
    template_code VARCHAR(50) NOT NULL,
    payload_json TEXT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING', -- PENDING, SENT, FAILED
    retry_count INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    sent_at TIMESTAMP WITH TIME ZONE
);

CREATE INDEX IF NOT EXISTS idx_cron_exec_name ON cron_schema.cron_job_execution(job_name, start_time DESC);
CREATE INDEX IF NOT EXISTS idx_notif_status ON cron_schema.notification_queue(status, channel);

-- Pre-seed core background automation jobs
INSERT INTO cron_schema.cron_job_definition (job_name, cron_expression, description, is_enabled) VALUES
('BulkReportGenerationJob', '0 0/15 * * * ?', 'Batch renders approved diagnostic test reports into compressed PDFs', TRUE),
('MachineDataSyncJob', '0 0/5 * * * ?', 'Polls analyzer incoming telemetry queues (HL7/ASTM)', TRUE),
('NotificationDispatchJob', '0 0/2 * * * ?', 'Dispatches SMS, Email, and WhatsApp patient report notifications', TRUE),
('TatBreachMonitorJob', '0 0/10 * * * ?', 'Monitors laboratory Turnaround Time (TAT) and flags breaches', TRUE),
('TrioTreeHospitalSyncJob', '0 0/30 * * * ?', 'Syncs specimen status with external TrioTree HIS endpoints', TRUE),
('AuditHashCheckpointJob', '0 0 0 * * ?', 'Daily cryptographic hash chain integrity checkpoint', TRUE)
ON CONFLICT (job_name) DO NOTHING;
