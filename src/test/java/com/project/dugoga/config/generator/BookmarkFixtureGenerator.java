package com.project.dugoga.config.generator;

import com.project.dugoga.domain.bookmark.domain.model.entity.Bookmark;
import com.project.dugoga.domain.store.domain.model.entity.Store;
import com.project.dugoga.domain.user.domain.model.entity.User;

public class BookmarkFixtureGenerator {

    public static final boolean IS_HIDDEN = false;

    public static Bookmark generateBookmarkFixture(User user, Store store) {
        return Bookmark.of(user, store);
    }

}