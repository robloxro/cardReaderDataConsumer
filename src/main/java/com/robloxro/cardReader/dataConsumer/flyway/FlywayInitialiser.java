package com.robloxro.cardReader.dataConsumer.flyway;

import org.flywaydb.core.Flyway;
public class FlywayInitialiser {

    public static void runMigrations() {
    Flyway flyway =Flyway.configure().dataSource("jdbc:h2:mem:testdb", "sa", "password").load();
     flyway.migrate();
    }
}
