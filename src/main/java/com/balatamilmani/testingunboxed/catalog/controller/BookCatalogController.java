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

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.balatamilmani.testingunboxed.catalog.model.Book;
import com.balatamilmani.testingunboxed.catalog.service.BookCatalogService;

@RestController
@RequestMapping("/api/books")
public class BookCatalogController {

	private final BookCatalogService service;

	public BookCatalogController(BookCatalogService service) {
		this.service = service;
	}

	@GetMapping("/{id}")
	public Book getBook(@PathVariable Long id) {
		return this.service.getBook(id);
	}

	@GetMapping
	public List<Book> getRecommendedBooks(
			@RequestParam(required = false) String topic,
			@RequestParam(defaultValue = "false") boolean practicalOnly) {
		return this.service.findRecommendedBooks(topic, practicalOnly);
	}
}
