package com.kalikian.smartshoppinglist.items;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Full-stack integration tests for ItemController.
 * - Boots the Spring context
 * - Uses Testcontainers (real Postgres)
 * - Exercises all endpoints (create/list/get/patch/toggle/delete)
 * - Verifies error responses via the GlobalExceptionHandler contract
 */
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class ItemControllerIT {

    // Spin up a disposable Postgres for the whole test class
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    // Wire Spring's datasource to point at the container DB
    @DynamicPropertySource
    static void datasourceProps(DynamicPropertyRegistry r) {
        postgres.start();
        r.add("spring.datasource.url", postgres::getJdbcUrl);
        r.add("spring.datasource.username", postgres::getUsername);
        r.add("spring.datasource.password", postgres::getPassword);
        // Use your Flyway migrations to build schema; no ddl-auto create here
        r.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
    }

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper om;           // JSON <-> Java
    @Autowired JdbcTemplate jdbc;         // quick setup/teardown helpers

    @BeforeEach
    void ensureListExists() {
        // Guarantee parent list with id=1 exists for tests
        jdbc.execute("""
            DO $$
            BEGIN
                IF NOT EXISTS (SELECT 1 FROM lists WHERE name = 'Groceries') THEN
                    INSERT INTO lists(name) VALUES ('Groceries');
                END IF;
            END$$;
        """);
    }

    // -------- CREATE --------

    @Test
    void create_shouldReturn201_Location_andBody() throws Exception {
        var body = Map.of(
                "listId", 1,
                "name", "Milk",
                "category", "DAIRY",
                "quantity", 2.0,
                "unit", "PCS"
        );

        mvc.perform(post("/api/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/lists/1/items/")))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.listId").value(1))
                .andExpect(jsonPath("$.name").value("Milk"))
                .andExpect(jsonPath("$.category").value("DAIRY"))
                .andExpect(jsonPath("$.quantity").value(2.0))
                .andExpect(jsonPath("$.unit").value("PCS"))
                .andExpect(jsonPath("$.done").value(false));
    }

    @Test
    void create_withBlankName_shouldReturn400_validationError() throws Exception {
        var invalid = Map.of(
                "listId", 1,
                "name", "   ", // will be trimmed to null by service -> IllegalArgumentException mapped to 400
                "category", "DAIRY",
                "quantity", 1.0,
                "unit", "PCS"
        );

        mvc.perform(post("/api/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.error", anyOf(
                        equalTo("VALIDATION_ERROR"),
                        equalTo("BAD_REQUEST"),
                        equalTo("Bad Request"))))
                .andExpect(jsonPath("$.path").value("/api/items"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    // -------- LIST BY LIST --------

    @Test
    void listByList_shouldReturnArray_afterCreateTwo() throws Exception {
        // create two items
        createItem(1, "Apples", "FRUIT", 5.0, "PCS");
        createItem(1, "Bread", "BAKERY", null, null);

        mvc.perform(get("/api/lists/1/items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))))
                .andExpect(jsonPath("$[*].name", hasItems("Apples", "Bread")));
    }

    // -------- GET ONE --------

    @Test
    void getOne_shouldReturn200_forExisting() throws Exception {
        int itemId = createItem(1, "Cheese", "DAIRY", 1.0, "BLOCK");

        mvc.perform(get("/api/lists/1/items/{itemId}", itemId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemId))
                .andExpect(jsonPath("$.name").value("Cheese"));
    }

    @Test
    void getOne_withWrongListOrUnknownId_shouldReturn404() throws Exception {
        mvc.perform(get("/api/lists/1/items/{itemId}", 999999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error", anyOf(
                        equalTo("NOT_FOUND"),
                        equalTo("RESOURCE_NOT_FOUND"),
                        equalTo("Not Found"))))
                .andExpect(jsonPath("$.path").value("/api/lists/1/items/999999"));
    }

    // -------- PARTIAL UPDATE (PATCH) --------

    @Test
    void update_shouldModifyProvidedFields_only() throws Exception {
        int itemId = createItem(1, "Tomato", "VEGGIE", 3.0, "PCS");

        // Patch name + quantity (leave unit null → unchanged)
        var patch = Map.of(
                "name", "Tomatoes",
                "quantity", 4.0
        );

        mvc.perform(patch("/api/lists/1/items/{itemId}", itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(patch)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemId))
                .andExpect(jsonPath("$.name").value("Tomatoes"))
                .andExpect(jsonPath("$.quantity").value(4.0))
                .andExpect(jsonPath("$.unit").value("PCS")); // still original unit from create
    }

    @Test
    void update_withBlankName_shouldReturn400() throws Exception {
        int itemId = createItem(1, "Yogurt", "DAIRY", 2.0, "CUP");

        var invalidPatch = Map.of("name", "   ");

        mvc.perform(patch("/api/lists/1/items/{itemId}", itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(invalidPatch)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error", anyOf(
                        equalTo("VALIDATION_ERROR"),
                        equalTo("BAD_REQUEST"),
                        equalTo("Bad Request"))))
                .andExpect(jsonPath("$.path").value("/api/lists/1/items/" + itemId));
    }

    @Test
    void update_withEmptyStrings_shouldClearOptionalFields() throws Exception {
        int itemId = createItem(1, "Juice", "DRINKS", 1.0, "BOTTLE");

        // According to controller's Javadoc: empty "" clears category/unit
        var patch = Map.of(
                "category", "",
                "unit", ""
        );

        mvc.perform(patch("/api/lists/1/items/{itemId}", itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(patch)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.category", anyOf(
                        equalTo(""),
                        nullValue()
                )))
                .andExpect(jsonPath("$.unit", anyOf(
                        equalTo(""),      // akzeptiere leeren String
                        nullValue()       // oder fehlendes Feld
                )));
    }

    // -------- TOGGLE DONE --------

    @Test
    void toggleDone_shouldFlipFlag() throws Exception {
        int itemId = createItem(1, "Eggs", "DAIRY", 10.0, "PCS");

        mvc.perform(patch("/api/lists/1/items/{itemId}/toggle", itemId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.done").value(true));

        mvc.perform(patch("/api/lists/1/items/{itemId}/toggle", itemId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.done").value(false));
    }

    // -------- DELETE --------

    @Test
    void delete_shouldReturn204_andSubsequentGet404() throws Exception {
        int itemId = createItem(1, "ToDelete", "MISC", null, null);

        mvc.perform(delete("/api/lists/1/items/{itemId}", itemId))
                .andExpect(status().isNoContent());

        mvc.perform(get("/api/lists/1/items/{itemId}", itemId))
                .andExpect(status().isNotFound());
    }

    // ------- helper: create item via API and return its id -------
    private int createItem(int listId, String name, String category, Double quantity, String unit) throws Exception {
        var body = new java.util.HashMap<String, Object>();
        body.put("listId", listId);
        body.put("name", name);
        if (category != null) body.put("category", category);
        if (quantity != null) body.put("quantity", quantity);
        if (unit != null) body.put("unit", unit);

        var res = mvc.perform(post("/api/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andReturn();

        var json = res.getResponse().getContentAsString();
        // Quick inline extraction without binding to DTO:
        var node = om.readTree(json);
        return node.get("id").asInt();
    }
}

