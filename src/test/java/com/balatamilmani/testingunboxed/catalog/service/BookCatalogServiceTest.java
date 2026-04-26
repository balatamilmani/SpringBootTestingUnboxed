/*
 * Copyright (c) 2026 Balamurugan Tamilmani
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.balatamilmani.testingunboxed.catalog.service;

/**
 * @author Balamurugan Tamilmani
 */

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.balatamilmani.testingunboxed.catalog.exception.BookNotFoundException;
import com.balatamilmani.testingunboxed.catalog.model.Book;
import com.balatamilmani.testingunboxed.catalog.repository.BookCatalogRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class BookCatalogServiceTest {

	@Mock
	private BookCatalogRepository repository;

	@InjectMocks
	private BookCatalogService service;

	@Test
	void getBookReturnsTheRepositoryResult() {
		Book book = new Book(7L, "Testing Spring Boot", "Ivy Chen", "spring", 2, true);
		given(this.repository.findById(7L)).willReturn(Optional.of(book));

		Book result = this.service.getBook(7L);

		assertThat(result).isEqualTo(book);
	}

	@Test
	void getBookThrowsWhenTheBookIsMissing() {
		Long bookId = 99L;
		given(this.repository.findById(bookId)).willReturn(Optional.empty());

		assertThatThrownBy(() -> this.service.getBook(99L))
				.isInstanceOf(BookNotFoundException.class)
				.hasMessage(String.format("Book %d was not found", bookId));
	}

	@Test
	void findRecommendedBooksFiltersAndSortsResults() {
		given(this.repository.findAll()).willReturn(List.of(
				new Book(1L, "REST APIs with Spring", "Nina Cole", "spring", 4, true),
				new Book(2L, "Spring Boot Test", "Asha Rao", "spring", 2, true),
				new Book(3L, "Spring Internals", "Jules Park", "spring", 2, false),
				new Book(4L, "JUnit in Action", "Mina Hart", "testing", 1, true)));

		List<Book> result = this.service.findRecommendedBooks("spring", true);

		assertThat(result)
				.extracting(Book::title)
				.containsExactly("Spring Boot Test", "REST APIs with Spring");
	}
}
