package ru.iteco.project.service.specifications;

import org.hibernate.query.criteria.internal.CriteriaBuilderImpl;
import org.hibernate.query.criteria.internal.predicate.ComparisonPredicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import ru.iteco.project.controller.searching.SearchUnit;
import ru.iteco.project.service.mappers.DateTimeMapper;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static ru.iteco.project.service.specifications.SearchOperations.*;

/**
 * Сервис предоставляет функционал универсального формировавния спецификаций для поиска данных
 */
@Service
public class SpecificationBuilder<T> {

    /**
     * Метод получения спецификации для поиска
     *
     * @param criteriaObject - объект со всей информаций о критериях и условиях поиска
     * @return - объект спецификации для поиска данных
     */
    public Specification<T> getSpec(final CriteriaObject criteriaObject) {
        return (root, query, builder) -> buildPredicates(root, builder, criteriaObject);
    }

    /**
     * Метод формирует итоговый предикат на основании списка предоставленных
     *
     * @param root           - корневой тип
     * @param builder        - объект для построения критериев и сложных выборок
     * @param criteriaObject - объект со всей информаций о критериях и условиях поиска
     * @return - итоговый предикат на основании списка предоставленных
     */
    private Predicate buildPredicates(Root root, CriteriaBuilder builder, final CriteriaObject criteriaObject) {
        Predicate[] predicates = criteriaObject.getRestrictions().stream()
                .map(restriction -> getPredicate(builder, root.get(restriction.getKey()), restriction))
                .toArray(Predicate[]::new);

        if (criteriaObject.getJoinOperation() == JoinOperations.AND) {
            return builder.and(predicates);
        }
        return builder.or(predicates);
    }

    /**
     * Метод получения предиката на основании переданных параметров
     *
     * @param criteriaBuilder   - объект для построения критериев и сложных выборок
     * @param path              - путь к атрибуту
     * @param restrictionValues - ограничения и критерии создания предиката
     * @return - предикат на основании предоставленных данных
     */
    public Predicate getPredicate(CriteriaBuilder criteriaBuilder, Path path, CriteriaObject.RestrictionValues restrictionValues) {
        switch (restrictionValues.getSearchOperation()) {


            case EQUAL: {
                return new ComparisonPredicate((CriteriaBuilderImpl) criteriaBuilder, ComparisonPredicate.ComparisonOperator.EQUAL, path, restrictionValues.getTypedValue());
            }
            case NOT_EQUAL: {
                return new ComparisonPredicate((CriteriaBuilderImpl) criteriaBuilder, ComparisonPredicate.ComparisonOperator.NOT_EQUAL, path, restrictionValues.getTypedValue());
            }


            case BETWEEN: {
                if (LocalDateTime.class.equals(path.getJavaType())) {
                    return criteriaBuilder.between(path, DateTimeMapper.stringToObject(restrictionValues.getMinValue()), DateTimeMapper.stringToObject(restrictionValues.getMaxValue()));
                }
                if (BigDecimal.class.equals(path.getJavaType())) {
                    return criteriaBuilder.between(path, new BigDecimal(restrictionValues.getMinValue()), new BigDecimal(restrictionValues.getMaxValue()));
                }
                return criteriaBuilder.isNull(path);
            }
            case NOT_BETWEEN: {
                if (LocalDateTime.class.equals(path.getJavaType())) {
                    Predicate greaterThan = getPredicate(
                            criteriaBuilder,
                            path,
                            new CriteriaObject.RestrictionValues(restrictionValues.getKey(), restrictionValues.getMaxValue(), GREATER_THAN_OR_EQUAL
                            )
                    );

                    Predicate lessThan = getPredicate(
                            criteriaBuilder,
                            path,
                            new CriteriaObject.RestrictionValues(
                                    restrictionValues.getKey(),
                                    restrictionValues.getMinValue(),
                                    LESS_THAN_OR_EQUAL
                            )
                    );
                    return criteriaBuilder.or(greaterThan, lessThan);
                }
                if (BigDecimal.class.equals(path.getJavaType())) {
                    Predicate greaterThan = getPredicate(
                            criteriaBuilder,
                            path,
                            new CriteriaObject.RestrictionValues(
                                    restrictionValues.getKey(),
                                    restrictionValues.getMaxValue(),
                                    GREATER_THAN_OR_EQUAL
                            )
                    );
                    Predicate lessThan = getPredicate(
                            criteriaBuilder,
                            path,
                            new CriteriaObject.RestrictionValues(
                                    restrictionValues.getKey(),
                                    restrictionValues.getMinValue(),
                                    LESS_THAN_OR_EQUAL
                            )
                    );
                    return criteriaBuilder.or(greaterThan, lessThan);
                }
                return criteriaBuilder.isNull(path);
            }


            case LIKE: {
                return criteriaBuilder.like(path, "%" + restrictionValues.getValue() + "%");
            }
            case NOT_LIKE: {
                return criteriaBuilder.notLike(path, "%" + restrictionValues.getValue() + "%");
            }


            case LESS_THAN: {
                if (LocalDateTime.class.equals(path.getJavaType())) {
                    return criteriaBuilder.lessThan(path, DateTimeMapper.stringToObject(restrictionValues.getValue()));
                }
                if (BigDecimal.class.equals(path.getJavaType())) {
                    return criteriaBuilder.lessThan(path, new BigDecimal(restrictionValues.getValue()));
                }
                return criteriaBuilder.isNull(path);
            }
            case GREATER_THAN: {
                if (LocalDateTime.class.equals(path.getJavaType())) {
                    return criteriaBuilder.greaterThan(path, DateTimeMapper.stringToObject(restrictionValues.getValue()));
                }
                if (BigDecimal.class.equals(path.getJavaType())) {
                    return criteriaBuilder.greaterThan(path, new BigDecimal(restrictionValues.getValue()));
                }
                return criteriaBuilder.isNull(path);
            }


            case LESS_THAN_OR_EQUAL: {
                if (LocalDateTime.class.equals(path.getJavaType())) {
                    return criteriaBuilder.lessThanOrEqualTo(path, DateTimeMapper.stringToObject(restrictionValues.getValue()));
                }
                if (BigDecimal.class.equals(path.getJavaType())) {
                    return criteriaBuilder.lessThanOrEqualTo(path, new BigDecimal(restrictionValues.getValue()));
                }
                return criteriaBuilder.isNull(path);
            }
            case GREATER_THAN_OR_EQUAL: {
                if (LocalDateTime.class.equals(path.getJavaType())) {
                    return criteriaBuilder.greaterThanOrEqualTo(path, DateTimeMapper.stringToObject(restrictionValues.getValue()));
                }
                if (BigDecimal.class.equals(path.getJavaType())) {
                    return criteriaBuilder.greaterThanOrEqualTo(path, new BigDecimal(restrictionValues.getValue()));
                }
                return criteriaBuilder.isNull(path);
            }


            default: {
                return criteriaBuilder.isNull(path);
            }
        }
    }

    /**
     * Метод определяет относится ли операция к диапазонной
     *
     * @param searchUnit - элемент поиска со всеми данными
     * @return - true - операция является диапазонной, false - операция не является диапазонной
     */
    public static boolean isBetweenOperation(SearchUnit searchUnit) {
        return (searchUnit.getSearchOperation() == BETWEEN) || (searchUnit.getSearchOperation() == NOT_BETWEEN);
    }

    /**
     * Метод проверяет наличие необходимых значений в searchUnit для конкретного типа операции
     *
     * @param searchUnit - элемент поиска со всеми данными
     * @return true - элемент поиска содержит необходимые данные, false - элемент поиска не содержит необходимые данные
     */
    public static boolean searchUnitIsValid(SearchUnit searchUnit) {
        if (!ObjectUtils.isEmpty(searchUnit) && !ObjectUtils.isEmpty(searchUnit.getSearchOperation())) {
            if (isBetweenOperation(searchUnit)) {
                return !ObjectUtils.isEmpty(searchUnit.getMinValue()) && !ObjectUtils.isEmpty(searchUnit.getMaxValue());
            } else {
                return !ObjectUtils.isEmpty(searchUnit.getValue());
            }
        }
        return false;
    }
}
