package com.project.dugoga.domain.payment.infrastructure.repository;

import com.project.dugoga.domain.payment.domain.model.entity.Payment;
import com.project.dugoga.domain.payment.domain.model.entity.QPayment;
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

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PaymentCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public Page<Payment> searchPayment(Long userId, String keyword, Pageable pageable) {

        QPayment qPayment = QPayment.payment;

        List<Payment> content = jpaQueryFactory
                .select(qPayment)
                .from(qPayment)
                .where(
                        qPayment.user.id.eq(userId),
                        qPayment.deletedAt.isNull(),
                        keywordContains(qPayment, keyword)
                )
                .orderBy(toOrderSpecifier(pageable.getSort(), qPayment))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> count = jpaQueryFactory
                .select(qPayment.count())
                .from(qPayment)
                .where(
                        qPayment.user.id.eq(userId),
                        qPayment.deletedAt.isNull(),
                        keywordContains(qPayment, keyword)
                );


        return PageableExecutionUtils.getPage(content, pageable, count::fetchOne);
    }

    private OrderSpecifier<?>[] toOrderSpecifier(Sort sort, QPayment payment) {
        if (sort == null || sort.isUnsorted()) {
            return new OrderSpecifier[]{payment.createdAt.desc()};
        }

        PathBuilder<Payment> entityPath = new PathBuilder<>(Payment.class, payment.getMetadata());

        return sort.stream()
                .map(order -> new OrderSpecifier(
                        order.isAscending() ? Order.ASC : Order.DESC,
                        entityPath.get(order.getProperty())
                ))
                .toArray(OrderSpecifier[]::new);
    }

    private BooleanExpression keywordContains(QPayment payment, String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return null;
        }

        return payment.order.store.name.containsIgnoreCase(keyword);
    }

}
