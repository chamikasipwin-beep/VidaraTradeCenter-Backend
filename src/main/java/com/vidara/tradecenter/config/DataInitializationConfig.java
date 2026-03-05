package com.vidara.tradecenter.config;

import com.vidara.tradecenter.user.model.Role;
import com.vidara.tradecenter.user.model.enums.UserRole;
import com.vidara.tradecenter.user.repository.RoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializationConfig {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializationConfig.class);

    @Bean
    public CommandLineRunner initializeRoles(RoleRepository roleRepository) {
        return args -> {
            logger.info("Initializing default roles...");

            // Check if CUSTOMER role exists
            if (!roleRepository.existsByName(UserRole.CUSTOMER)) {
                Role customerRole = new Role(UserRole.CUSTOMER);
                roleRepository.save(customerRole);
                logger.info("✓ Created CUSTOMER role");
            }

            // Check if ADMIN role exists
            if (!roleRepository.existsByName(UserRole.ADMIN)) {
                Role adminRole = new Role(UserRole.ADMIN);
                roleRepository.save(adminRole);
                logger.info("✓ Created ADMIN role");
            }

            // Check if AGENT role exists
            if (!roleRepository.existsByName(UserRole.AGENT)) {
                Role agentRole = new Role(UserRole.AGENT);
                roleRepository.save(agentRole);
                logger.info("✓ Created AGENT role");
            }

            logger.info("Default roles initialization completed");
        };
    }
}
