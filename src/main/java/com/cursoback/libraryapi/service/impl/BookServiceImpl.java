package com.cursoback.libraryapi.service.impl;

import com.cursoback.libraryapi.exception.BusinessException;
import com.cursoback.libraryapi.model.entity.Book;
import com.cursoback.libraryapi.model.repository.BookRepository;
import com.cursoback.libraryapi.service.BookService;
import org.springframework.stereotype.Service;

@Service
public class BookServiceImpl implements BookService {
    private BookRepository repository;

    public BookServiceImpl(BookRepository repository){
        this.repository = repository;
    }

    @Override
    public Book save(Book book) {
        if(repository.existsByIsbn(book.getIsbn())){
            throw new BusinessException("Isbn já cadastrado");
        }
        return repository.save(book);
    }
}
