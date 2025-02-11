package com.ishwor.bookmarks;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class BookmarkControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateBookmark() throws Exception {
        var payload = new BookmarkController.CreateBookmarkPayload("My Bookmark", "https://example.com");

        mockMvc.perform(post("/api/bookmarks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("My Bookmark"))
                .andExpect(jsonPath("$.url").value("https://example.com"));
    }

    @Test
    void testGetBookmarkById_NotFound() throws Exception {
        mockMvc.perform(get("/api/bookmarks/9999")) // ID does not exist
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Bookmark not found"));
    }

    @Test
    void testUpdateBookmark() throws Exception {
        var createPayload = new BookmarkController.CreateBookmarkPayload("Initial Title", "https://example.com");

        var result = mockMvc.perform(post("/api/bookmarks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createPayload)))
                .andExpect(status().isCreated())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        Bookmark createdBookmark = objectMapper.readValue(responseBody, Bookmark.class);
        Long id = createdBookmark.getId();

        var updatePayload = new BookmarkController.UpdateBookmarkPayload("Updated Title", "https://updated-url.com");

        mockMvc.perform(put("/api/bookmarks/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatePayload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"))
                .andExpect(jsonPath("$.url").value("https://updated-url.com"));
    }


    @Test
    void testDeleteBookmark() throws Exception {
        var payload = new BookmarkController.CreateBookmarkPayload("Test Bookmark", "https://example.com");

        var result = mockMvc.perform(post("/api/bookmarks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isCreated())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        Bookmark createdBookmark = objectMapper.readValue(responseBody, Bookmark.class);
        Long id = createdBookmark.getId();

        mockMvc.perform(delete("/api/bookmarks/" + id))
                .andExpect(status().isNoContent()); // ✅ Expect 204 No Content
    }

}
