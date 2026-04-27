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

package com.balatamilmani.testingunboxed.catalog.integration;

/**
 * @author Balamurugan Tamilmani
 */

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class BookCatalogApplicationTest {

	@Autowired
	private MockMvc mvc;

	@Test
	void applicationServesTheRealInMemoryCatalog() throws Exception {
		this.mvc.perform(get("/api/books")
				.queryParam("topic", "spring")
				.queryParam("practicalOnly", "true"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].title").value("Spring Boot Test"))
				.andExpect(jsonPath("$[1].title").value("REST APIs with Spring"));
	}

	@Test
	void applicationServesBookById() throws Exception {
		this.mvc.perform(get("/api/books/{id}", 1L))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.title").value("Spring Boot Test"))
				.andExpect(jsonPath("$.author").value("Asha Rao"));
	}
}
