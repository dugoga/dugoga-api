package com.project.dugoga.domain.bookmark.infrastructure.repository;

import com.project.dugoga.domain.bookmark.domain.model.entity.Bookmark;
import com.project.dugoga.domain.bookmark.domain.model.entity.QBookmark;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Repository
@RequiredArgsConstructor
public class BookmarkCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public Page<Bookmark> search(String keyword, Long userId, boolean isAdmin, Pageable pageable) {
        QBookmark bookmark = QBookmark.bookmark;

        BooleanExpression keywordCondition = keywordCondition(bookmark, keyword);
        BooleanExpression authorizedCondition = authorizedCondition(bookmark, isAdmin, userId);
        BooleanExpression deletedCondition = bookmark.deletedAt.isNull();

        List<Bookmark> content = jpaQueryFactory
                .selectFrom(bookmark)
                .where(keywordCondition, authorizedCondition, deletedCondition)
                .orderBy(toOrderSpecifiers(pageable.getSort(), bookmark))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> count = jpaQueryFactory
                .select(bookmark.count())
                .from(bookmark)
                .where(
                        keywordCondition,
                        authorizedCondition,
                        deletedCondition
                );
        return PageableExecutionUtils.getPage(content, pageable, count::fetchOne);

    }

    private BooleanExpression authorizedCondition(QBookmark bookmark, boolean isAdmin, Long userId) {
        if (isAdmin) {
            return null;
        }
        return bookmark.isHidden.isFalse().and(bookmark.user.id.eq(userId));
    }

    private OrderSpecifier<?>[] toOrderSpecifiers(Sort sort, QBookmark bookmark) {
        if (sort == null || sort.isUnsorted()) {
            return new OrderSpecifier[]{ bookmark.createdAt.desc() };
        }

        PathBuilder<Bookmark> entityPath =
                new PathBuilder<>(Bookmark.class, bookmark.getMetadata());

        return sort.stream()
                .map(order -> new OrderSpecifier(
                        order.isAscending() ? Order.ASC : Order.DESC,
                        entityPath.get(order.getProperty())
                ))
                .toArray(OrderSpecifier[]::new);
    }

    private BooleanExpression keywordCondition(QBookmark bookmark, String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return null;
        }
        return bookmark.store.name.containsIgnoreCase(keyword);
    }
}
