package com.project.dugoga.domain.store.infrastructure.repository;

import com.project.dugoga.domain.category.domain.model.entity.QCategory;
import com.project.dugoga.domain.store.domain.model.entity.QStore;
import com.project.dugoga.domain.store.domain.model.entity.Store;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class StoreCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public Page<Store> searchStores(String keyword, String category, Long userId, boolean isAdmin, Pageable pageable) {

        QStore qStore = QStore.store;
        QCategory qCategory = QCategory.category;
        BooleanExpression storeDeletedCondition = qStore.deletedAt.isNull();
        BooleanExpression categoryCondition = categoryCondition(qCategory, category);
        BooleanExpression keywordCondition = keywordCondition(qStore, keyword);
        BooleanExpression authorizedCondition = authorizedCondition(qStore, isAdmin, userId);

        List<Store> content = jpaQueryFactory
                .selectFrom(qStore)
                .where(
                        storeDeletedCondition,
                        categoryCondition,
                        keywordCondition,
                        authorizedCondition
                )
                .innerJoin(qStore.category, qCategory)
                .fetchJoin()
                .orderBy(toOrderSpecifier(pageable.getSort(), qStore))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> count = jpaQueryFactory
                .select(qStore.count())
                .from(qStore)
                .where(
                        storeDeletedCondition,
                        categoryCondition,
                        keywordCondition,
                        authorizedCondition
                )
                .innerJoin(qStore.category, qCategory);

        // 매번 fetchOne 실행 x, 첫 페이지와 마지막 페이지일 경우 count 쿼리를 보내지않음
        return PageableExecutionUtils.getPage(content, pageable, count::fetchOne);
    }

    private OrderSpecifier<?>[] toOrderSpecifier(Sort sort, QStore store) {
        if (sort == null || sort.isUnsorted()) {
            return new OrderSpecifier[]{store.createdAt.desc()};
        }

        PathBuilder<Store> entityPath = new PathBuilder<>(Store.class, store.getMetadata());

        return sort.stream()
                .map(order -> new OrderSpecifier(
                        order.isAscending() ? Order.ASC : Order.DESC,
                        entityPath.get(order.getProperty())
                ))
                .toArray(OrderSpecifier[]::new);
    }

    private BooleanExpression keywordCondition(QStore store, String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return null;
        }
        return store.name.containsIgnoreCase(keyword);
    }

    private BooleanExpression categoryCondition(QCategory qCategory, String category) {
        if (!StringUtils.hasText(category)) {
            return null;
        }
        return qCategory.name.eq(category);
    }

    private BooleanExpression authorizedCondition(QStore store, boolean isAdmin, Long userId) {
        if (isAdmin) {
            return null;
        }
        // store.user.id 는 fk로 lazy 로딩 시에도 user_id 값을 가지고있기때문에 join x
        return store.isHidden.isFalse().or(store.user.id.eq(userId));
    }
}
