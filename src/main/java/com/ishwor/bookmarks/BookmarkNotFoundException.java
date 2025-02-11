package com.ishwor.bookmarks;

public class BookmarkNotFoundException extends RuntimeException{
    public BookmarkNotFoundException(String message){
        super(message);
    }
}
