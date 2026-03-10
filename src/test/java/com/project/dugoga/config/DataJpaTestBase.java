package com.project.dugoga.config;

import com.project.dugoga.global.config.TestJpaAuditingConfig;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@Import(TestJpaAuditingConfig.class)
public class DataJpaTestBase {
}
