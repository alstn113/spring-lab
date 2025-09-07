package io.github.alstn113.anno

import io.github.alstn113.anno.core.BookService
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestConstructor

@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class AnnotationTests(
    private val bookService: BookService
) {

    val log: Logger = LoggerFactory.getLogger(javaClass)

    @Test
    fun testAnnotation() {
        val bookId = bookService.createBook("Spring")

        log.info("⭐--- no caching start")
        bookService.getBook(bookId)
        log.info("⭐--- no caching end")

        log.info("⛅--- caching start")
        bookService.getBook(bookId)
        log.info("⛅--- caching end")
    }
}