package com.ishwor.bookmarks;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
  List<BookmarkInfo> findAllByOrderByCreatedAtDesc();

  Optional<BookmarkInfo> findBookmarkById(Long id);
  }