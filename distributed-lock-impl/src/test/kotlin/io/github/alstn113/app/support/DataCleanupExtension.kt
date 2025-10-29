package io.github.alstn113.app.support

import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.springframework.test.context.junit.jupiter.SpringExtension

class DataCleanupExtension : AfterEachCallback {

    override fun afterEach(context: ExtensionContext) {
        val dataCleaner = getDataCleaner(context)
        dataCleaner.clear()
    }

    private fun getDataCleaner(context: ExtensionContext): RedisDataCleaner {
        return SpringExtension.getApplicationContext(context)
            .getBean(RedisDataCleaner::class.java)
    }
}