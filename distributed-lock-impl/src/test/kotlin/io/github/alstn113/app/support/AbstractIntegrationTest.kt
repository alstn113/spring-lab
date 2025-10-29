package io.github.alstn113.app.support

import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestConstructor

@SpringBootTest
@ContextConfiguration(
    initializers = [
        RedisContainerInitializer::class,
    ]
)
@ExtendWith(DataCleanupExtension::class)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
abstract class AbstractIntegrationTest



