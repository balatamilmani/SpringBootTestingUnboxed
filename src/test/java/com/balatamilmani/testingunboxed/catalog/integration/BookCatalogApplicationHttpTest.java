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
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
class BookCatalogApplicationHttpTest {



    @Test
    void applicationServesTheRealInMemoryCatalogOverHttp(@Autowired RestTestClient restClient) {
        restClient.get().uri(
                "/api/books?topic=spring&practicalOnly=true").accept(MediaType.APPLICATION_JSON).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(2)
                .jsonPath("$[0].title").isEqualTo("Spring Boot Test")
                .jsonPath("$[0].author").isEqualTo("Asha Rao")
                .jsonPath("$[0].topic").isEqualTo("spring")
                .jsonPath("$[0].practical").isEqualTo(true)
                .jsonPath("$[1].title").isEqualTo("REST APIs with Spring")
                .jsonPath("$[1].author").isEqualTo("Nina Cole")
                .jsonPath("$[1].topic").isEqualTo("spring")
                .jsonPath("$[1].practical").isEqualTo(true);
    }
}
