package com.labplatform.lab_platform_backend.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseConstraintFixer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseConstraintFixer.class);
    private final JdbcTemplate jdbcTemplate;

    public DatabaseConstraintFixer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) {
        try {
            logger.info("Updating database check constraint for equipment status...");
            jdbcTemplate.execute("ALTER TABLE equipment DROP CONSTRAINT IF EXISTS equipment_status_check");
            jdbcTemplate.execute("ALTER TABLE equipment ADD CONSTRAINT equipment_status_check CHECK (status IN ('AVAILABLE', 'BOOKED', 'UNDER_MAINTENANCE', 'OUT_OF_SERVICE', 'RETIRED', 'SHARED'))");
            logger.info("Successfully updated equipment_status_check constraint to include SHARED status!");
        } catch (Exception e) {
            logger.warn("Could not alter equipment_status_check constraint: {}", e.getMessage());
        }
    }
}
