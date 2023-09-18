package com.cursoback.libraryapi.api.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoanFilterDTO {

    private Long id;
    private String isbn;
    private String customer;
    private BookDTO book;
}
