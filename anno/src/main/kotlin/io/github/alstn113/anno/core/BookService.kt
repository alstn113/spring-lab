package io.github.alstn113.anno.core

import org.springframework.cache.annotation.Cacheable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BookService(
    private val bookRepository: BookRepository,
) {

    @Cacheable("books", key = "#id")
    @Transactional(readOnly = true)
    fun getBook(id: Long): BookDto {
        val book = bookRepository.findByIdOrNull(id)
            ?: throw IllegalArgumentException()

        return BookDto(id = book.id, title = book.title)
    }

    @Transactional
    fun createBook(title: String): Long {
        val book = Book(title = title)
        val savedBook = bookRepository.save(book)

        return savedBook.id
    }
}