package com.cursoback.libraryapi.service;

import com.cursoback.libraryapi.exception.BusinessException;
import com.cursoback.libraryapi.model.entity.Book;
import com.cursoback.libraryapi.model.repository.BookRepository;
import com.cursoback.libraryapi.service.impl.BookServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class BookServiceTest {
    BookService service;
    @MockBean
    BookRepository repository;

    @BeforeEach
    public void setUp(){
        this.service = new BookServiceImpl(repository);
    }

    @Test
    @DisplayName("Deve salvar um livro")
    public void saveBookTest(){
        //cenario
        Book book = createValidBook();
        when(repository.existsByIsbn(Mockito.anyString())).thenReturn(false);
        when(repository.save(book))
                .thenReturn(Book.builder().id(1L)
                .isbn("123")
                .title("As aventuras")
                .author("Fulano").build());

        //execucao
        Book savedBook = service.save(book);

        //verificacao
        assertThat(savedBook.getId()).isNotNull();
        assertThat(savedBook.getIsbn()).isEqualTo("123");
        assertThat(savedBook.getTitle()).isEqualTo("As aventuras");
        assertThat(savedBook.getAuthor()).isEqualTo("Fulano");


    }

    private static Book createValidBook() {
        return Book.builder().isbn("123").author("Fulano").title("As aventuras")
                .build();
    }

    @Test
    @DisplayName("Deve lançar erro de negocio ao tentar salvar livro com isbn duplicado")
    public void shouldNotSaveABookWithDuplicatedISBN(){
        //cenario
        Book book = createValidBook();
        when(repository.existsByIsbn(Mockito.anyString())).thenReturn(true);

        //execucao
        Throwable exception = Assertions.catchThrowable(() -> service.save(book));

        //verificacao
        assertThat(exception).isInstanceOf(BusinessException.class)
        .hasMessage("Isbn já cadastrado");

        verify(repository, Mockito.never()).save(book);


    }

    @Test
    @DisplayName("Deve obter um livro por id")
    public void getByIdTest(){
        Long id = 1L;
        Book book = createValidBook();
        book.setId(id);
        when(repository.findById(id)).thenReturn(Optional.of(book));

        //execucao
        Optional<Book> foundBook = service.getById(id);

        //verificacao
        assertThat(foundBook.isPresent()).isTrue();
        assertThat(foundBook.get().getId()).isEqualTo(id);
        assertThat(foundBook.get().getAuthor()).isEqualTo(book.getAuthor());
        assertThat(foundBook.get().getTitle()).isEqualTo(book.getTitle());
        assertThat(foundBook.get().getIsbn()).isEqualTo(book.getIsbn());
    }

    @Test
    @DisplayName("Deve retornar vazio ao obter um livro por id quando ele não existe na base")
    public void bookNotFoundByIdTest(){
        Long id = 1L;

        when(repository.findById(id)).thenReturn(Optional.empty());

        //execucao
        Optional<Book> book = service.getById(id);

        //verificacao
        assertThat(book.isPresent()).isFalse();
    }

    @Test
    @DisplayName("Deve deletar um livro")
    public void deleteByIdTest(){
        Long id = 1L;
        Book book = Book.builder().id(id).build();

        //execucao
        org.junit.jupiter.api.Assertions.assertDoesNotThrow( () -> service.delete(book));

        //verificacao
        verify(repository, times(1)).delete(book);
    }

    @Test
    @DisplayName("Deve ocorrer erro ao tentar deletar um livro inexistente")
    public void deleteInvalidTest(){
        //cenario
        Book book = new Book();

        //execucao
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> service.delete(book));

        //verificacao
        verify(repository, Mockito.never()).delete(book);
    }

    @Test
    @DisplayName("Deve atualizar um livro")
    public void updateBookTest(){
        Long id = 1L;
        Book book = createValidBook();
        book.setId(id);

        Book updatedBook = createValidBook();
        updatedBook.setId(id);

        when(repository.save(book))
                .thenReturn(updatedBook);

        //execucao
        Book savedBook = service.update(book);

        //verificacao
        assertThat(savedBook.getId()).isEqualTo(updatedBook.getId());
        assertThat(savedBook.getIsbn()).isEqualTo(updatedBook.getIsbn());
        assertThat(savedBook.getTitle()).isEqualTo(updatedBook.getTitle());
        assertThat(savedBook.getAuthor()).isEqualTo(updatedBook.getAuthor());

    }

    @Test
    @DisplayName("Deve ocoorer erro ao tentar atualizar um livro inexistente")
    public void updateInvalidBookTest() {
        //cenario
        Book book = new Book();

        //execucao
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> service.update(book));

        //verificacao
        verify(repository, Mockito.never()).save(book);
    }

    @Test
    @DisplayName("Deve filtrar livros pelas propriedades")
    public void findBookTest(){
        //cenario
        Book book = createValidBook();

        PageRequest pageRequest = PageRequest.of(0, 10);

        List<Book> lista = Arrays.asList(book);

        Page<Book> page = new PageImpl<Book>(lista, pageRequest, 1);
        when(repository.findAll(Mockito.any(Example.class), Mockito.any(PageRequest.class)))
                .thenReturn(page);

        //execucao
        Page<Book> result = service.find(book, pageRequest);

        //verificacao
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).isEqualTo(lista);
        assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(result.getPageable().getPageSize()).isEqualTo(10);
    }

    @Test
    @DisplayName("deve obter um livro pelo isbn")
    public void getBookByIsbnTest(){
        String isbn = "123";
        when(repository.findByIsbn(isbn)).thenReturn(Optional.of(Book.builder().id(1L).isbn(isbn).build()));

        Optional<Book> book = service.getBookByIsbn(isbn);

        assertThat(book.isPresent()).isTrue();
        assertThat(book.get().getId()).isEqualTo(1L);
        assertThat(book.get().getIsbn()).isEqualTo(isbn);

        verify(repository, times(1)).findByIsbn(isbn);

    }

}
