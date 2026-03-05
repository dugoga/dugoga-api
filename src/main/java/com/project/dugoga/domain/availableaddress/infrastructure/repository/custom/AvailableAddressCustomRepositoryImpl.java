package com.project.dugoga.domain.availableaddress.infrastructure.repository.custom;

import com.project.dugoga.domain.availableaddress.domain.model.entity.AvailableAddress;
import com.project.dugoga.domain.availableaddress.domain.model.entity.QAvailableAddress;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;

@RequiredArgsConstructor
public class AvailableAddressCustomRepositoryImpl implements  AvailableAddressCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<AvailableAddress> search(String query, Pageable pageable, boolean isAdmin) {
        Pageable normalizePageable = normalizePageable(pageable);
        String keyword = (query == null || query.isBlank()) ? null : query.trim();

        QAvailableAddress address = QAvailableAddress.availableAddress; // Q타입 클래스 객체 생성

        BooleanExpression deletedCondition = deletedCondition(address, isAdmin);
        BooleanExpression keywordCondition = keywordCondition(address, keyword);

        List<AvailableAddress> content = jpaQueryFactory
                .selectFrom(address)
                .where(deletedCondition, keywordCondition)
                .orderBy(toOrderSpecifiers(pageable.getSort(), address))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = jpaQueryFactory
                .select(address.count())
                .from(address)
                .where(deletedCondition, keywordCondition)
                .fetchOne();

        long totalCount = (total == null) ? 0L : total;
        return new PageImpl<>(content, pageable, totalCount);

    }

    private Pageable normalizePageable(Pageable pageable) {
        int page = Math.max(pageable.getPageNumber(), 0);

        int requestedSize = pageable.getPageSize();
        int size = (requestedSize == 10 || requestedSize == 30 || requestedSize == 50)
                ? requestedSize
                : 10;

        Sort sort = pageable.getSort().isSorted()
                ? pageable.getSort()
                : Sort.by(Sort.Direction.DESC, "createdAt");

        return PageRequest.of(page, size, sort);
    }

    private OrderSpecifier<?>[] toOrderSpecifiers(Sort sort, QAvailableAddress address) {
        if (sort == null || sort.isUnsorted()) {
            return new OrderSpecifier[]{ address.createdAt.desc() };
        }

        PathBuilder<AvailableAddress> entityPath =
                new PathBuilder<>(AvailableAddress.class, address.getMetadata());

        return sort.stream()
                .map(order -> new OrderSpecifier(
                        order.isAscending() ? Order.ASC : Order.DESC,
                        entityPath.get(order.getProperty())
                ))
                .toArray(OrderSpecifier[]::new);
    }

    private BooleanExpression deletedCondition(QAvailableAddress address, boolean isAdmin) {
        // isAdmin == true → 삭제 포함 전체 조회
        // isAdmin == false → 일반 사용자 → deletedAt is null
        if (isAdmin) {
            return null;
        }
        return address.deletedAt.isNull();
    }

    private BooleanExpression keywordCondition(QAvailableAddress address, String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return null; // 키워드 없으면 조건 없음(전체)
        }
        return address.region1depthName.contains(keyword)
                .or(address.region2depthName.contains(keyword));
    }
}
