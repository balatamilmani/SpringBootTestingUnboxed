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

package com.balatamilmani.testingunboxed.catalog.controller;

/**
 * @author Balamurugan Tamilmani
 */

import java.util.List;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.balatamilmani.testingunboxed.catalog.exception.BookNotFoundException;
import com.balatamilmani.testingunboxed.catalog.model.Book;
import com.balatamilmani.testingunboxed.catalog.service.BookCatalogService;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookCatalogController.class)
class BookCatalogControllerTest {

	@Autowired
	private MockMvc mvc;

	//automatically swaps the bean into the Spring container, making it available for @Autowired injection
	//BookCatalogController is injected with this Mock BookCatalogService
	@MockitoBean
	private BookCatalogService service;

	@Test
	void getBookReturnsJsonFromTheControllerLayer() throws Exception {
		Long bookId = 2L;
		given(this.service.getBook(bookId))
				.willReturn(new Book(bookId, "Mockito Recipes", "Leo Grant", "testing", 3, true));

		this.mvc.perform(get("/api/books/"+bookId))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.title").value("Mockito Recipes"))
				.andExpect(jsonPath("$.topic").value("testing"));
	}

	@Test
	void getBookReturnsNotFoundWhenTheServiceThrows() throws Exception {
		given(this.service.getBook(99L)).willThrow(new BookNotFoundException(99L));

		this.mvc.perform(get("/api/books/99"))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.title").value("Book not found"))
				.andExpect(jsonPath("$.code").value("BOOK_NOT_FOUND"))
				.andExpect(jsonPath("$.detail").value("Book 99 was not found"))
				.andExpect(jsonPath("$.status").value(404));
	}

	@Test
	void getRecommendedBooksPassesQueryParametersToTheService() throws Exception {
		given(this.service.findRecommendedBooks("testing", true))
				.willReturn(List.of(new Book(2L, "Mockito Recipes", "Leo Grant", "testing", 3, true)));

		this.mvc.perform(get("/api/books")
				.queryParam("topic", "testing")
				.queryParam("practicalOnly", "true"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].author").value("Leo Grant"))
				.andExpect(jsonPath("$[0].practical").value(true));
	}
}
