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

package com.balatamilmani.testingunboxed.catalog.repository;

/**
 * @author Balamurugan Tamilmani
 */

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.balatamilmani.testingunboxed.catalog.model.Book;

@Repository
class InMemoryBookCatalogRepository implements BookCatalogRepository {

	private static final List<Book> BOOKS = List.of(
			new Book(1L, "Spring Boot Test", "Asha Rao", "spring", 2, true),
			new Book(2L, "Mockito Recipes", "Leo Grant", "testing", 3, true),
			new Book(3L, "JUnit in Action", "Mina Hart", "testing", 2, false),
			new Book(4L, "REST APIs with Spring", "Nina Cole", "spring", 4, true));

	@Override
	public Optional<Book> findById(Long id) {
		return BOOKS.stream()
				.filter(book -> book.id().equals(id))
				.findFirst();
	}

	@Override
	public List<Book> findAll() {
		return BOOKS;
	}
}
