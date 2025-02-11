package com.ishwor.bookmarks;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.Instant;
import java.util.HashMap;

import java.util.Map;

@RestController
@RequestMapping("/api/bookmarks")
public class BookmarkController {
    private final BookmarkRepository bookmarkRepository;
    BookmarkController(BookmarkRepository bookmarkRepository){
        this.bookmarkRepository = bookmarkRepository;
    }

    @GetMapping
    Page<Bookmark> getBookmarks(Pageable pageable){
        return bookmarkRepository.findAll(pageable);

    }

    @GetMapping("/{id}")
    ResponseEntity<BookmarkInfo> getBookmarksById(@PathVariable Long id){
        var bookmark = bookmarkRepository.findBookmarkById(id)
                .orElseThrow(() -> new BookmarkNotFoundException("Bookmark not found"));
        return ResponseEntity.ok(bookmark);
    }

    record CreateBookmarkPayload(
            @NotEmpty(message="Title is required")
            String title,
            @NotEmpty(message = "URL is required")
            String url
    ){}

    @PostMapping
    ResponseEntity<Bookmark> createBookmark(
            @Valid @RequestBody CreateBookmarkPayload payload
    ){
        var bookmark = new Bookmark();
        bookmark.setTitle(payload.title);
        bookmark.setUrl(payload.url);
        bookmark.setCreatedAt(Instant.now());

        var savedBookmark = bookmarkRepository.save(bookmark);
        var uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedBookmark.getId())
                .toUri();
        return ResponseEntity.created(uri).body(savedBookmark);
    }

    //update

    record UpdateBookmarkPayload(
            @NotEmpty(message="Title is required")
            String title,
            @NotEmpty(message = "URL is required")
            String url
    ){}

    @PutMapping("/{id}")
    ResponseEntity<Bookmark> updateBookmark(
            @PathVariable Long id,
            @Valid @RequestBody UpdateBookmarkPayload payload
    ){
        var bookmark = bookmarkRepository.findById(id)
                        .orElseThrow(() -> new BookmarkNotFoundException("Bookmark not found"));
        bookmark.setTitle(payload.title);
        bookmark.setUrl(payload.url);
        bookmark.setUpdatedAt(Instant.now());

        var savedBookmark = bookmarkRepository.save(bookmark);
        return ResponseEntity.ok(savedBookmark);
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteBookmark(@PathVariable Long id){
        var bookmark = bookmarkRepository.findById(id)
                .orElseThrow(() -> new BookmarkNotFoundException("Bookmark not found"));
        bookmarkRepository.delete(bookmark);
        return ResponseEntity.noContent().build();

    }


    @ExceptionHandler(BookmarkNotFoundException.class)
    ResponseEntity<Map<String,String>> handle(BookmarkNotFoundException e){
        Map<String,String> errorResponse = new HashMap<>();
        errorResponse.put(
                "error" ,e.getMessage()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
}
