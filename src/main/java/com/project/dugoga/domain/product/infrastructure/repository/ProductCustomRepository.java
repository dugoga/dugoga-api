package com.project.dugoga.domain.product.infrastructure.repository;

import com.project.dugoga.domain.product.domain.model.entity.Product;
import com.project.dugoga.domain.product.domain.model.entity.QProduct;
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
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ProductCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public Page<Product> searchStoreProduct(UUID storeId, String keyword, boolean isAuthorized, Pageable pageable) {

        QProduct qProduct = QProduct.product;

        BooleanExpression[] conditions = {
                qProduct.store.id.eq(storeId),
                qProduct.deletedAt.isNull(),
                keywordCondition(qProduct, keyword),
                authorizedCondition(qProduct, isAuthorized)
        };

        List<Product> content = jpaQueryFactory
                .selectFrom(qProduct)
                .where(conditions)
                .orderBy(toOrderSpecifier(pageable.getSort(), qProduct))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> count = jpaQueryFactory
                .select(qProduct.count())
                .from(qProduct)
                .where(conditions);

        return PageableExecutionUtils.getPage(content, pageable, count::fetchOne);
    }

    public Page<Product> searchProduct(String keyword, boolean isAuthorized, Pageable pageable) {
        QProduct qProduct = QProduct.product;

        BooleanExpression[] conditions = {
                qProduct.deletedAt.isNull(),
                keywordCondition(qProduct, keyword),
                authorizedCondition(qProduct, isAuthorized)
        };

        List<Product> content = jpaQueryFactory
                .selectFrom(qProduct)
                .where(conditions)
                .orderBy(toOrderSpecifier(pageable.getSort(), qProduct))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> count = jpaQueryFactory
                .select(qProduct.count())
                .from(qProduct)
                .where(conditions);

        return PageableExecutionUtils.getPage(content, pageable, count::fetchOne);
    }

    private OrderSpecifier<?>[] toOrderSpecifier(Sort sort, QProduct product) {
        if (sort == null || sort.isUnsorted()) {
            return new OrderSpecifier[]{product.createdAt.desc()};
        }

        PathBuilder<Product> entityPath = new PathBuilder<>(Product.class, product.getMetadata());

        return sort.stream()
                .map(order -> new OrderSpecifier(
                        order.isAscending() ? Order.ASC : Order.DESC,
                        entityPath.get(order.getProperty())
                ))
                .toArray(OrderSpecifier[]::new);
    }

    private BooleanExpression keywordCondition(QProduct product, String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return null;
        }
        return product.name.containsIgnoreCase(keyword);
    }

    private BooleanExpression authorizedCondition(QProduct product, boolean isAuthorized) {
        if (isAuthorized) {
            return null;
        }
        return product.isHidden.isFalse();
    }
}
