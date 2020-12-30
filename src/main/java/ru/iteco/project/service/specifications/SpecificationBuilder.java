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
import java.util.HashMap;

import static ru.iteco.project.service.specifications.SearchOperations.*;

/**
 * Сервис предоставляет функционал универсального формировавния спецификаций для поиска данных
 */
@Service
public class SpecificationBuilder<T> {

    /*** Справочник со держащий наименование операции поиска против метода формирования предиката для этой операции**/
    private final HashMap<SearchOperations, PredicateProducer<CriteriaBuilder, Path, CriteriaObject.RestrictionValues, Predicate>>
            predicatesForSearchOperations = new HashMap<SearchOperations, PredicateProducer<CriteriaBuilder, Path, CriteriaObject.RestrictionValues, Predicate>>() {{

        put(EQUAL, (cb, path, restriction) -> new ComparisonPredicate((CriteriaBuilderImpl) cb, ComparisonPredicate.ComparisonOperator.EQUAL, path, restriction.getTypedValue()));
        put(NOT_EQUAL, (cb, path, restriction) -> new ComparisonPredicate((CriteriaBuilderImpl) cb, ComparisonPredicate.ComparisonOperator.NOT_EQUAL, path, restriction.getTypedValue()));


        put(BETWEEN, (cb, path, restriction) -> {
                    if (LocalDateTime.class.equals(path.getJavaType())) {
                        return cb.between(path, DateTimeMapper.stringToObject(restriction.getMinValue()), DateTimeMapper.stringToObject(restriction.getMaxValue()));
                    }
                    if (BigDecimal.class.equals(path.getJavaType())) {
                        return cb.between(path, new BigDecimal(restriction.getMinValue()), new BigDecimal(restriction.getMaxValue()));
                    }
                    return cb.isNull(path);
                }
        );

        put(NOT_BETWEEN, (cb, path, restriction) -> {
            if (LocalDateTime.class.equals(path.getJavaType())) {
                Predicate greaterThan = predicatesForSearchOperations.get(GREATER_THAN_OR_EQUAL)
                        .produce(cb, path, new CriteriaObject.RestrictionValues(
                                        restriction.getKey(), restriction.getMaxValue(), GREATER_THAN_OR_EQUAL
                                )
                        );
                Predicate lessThan = predicatesForSearchOperations.get(LESS_THAN_OR_EQUAL)
                        .produce(cb, path, new CriteriaObject.RestrictionValues(
                                        restriction.getKey(), restriction.getMinValue(), LESS_THAN_OR_EQUAL
                                )
                        );
                return cb.or(greaterThan, lessThan);
            }

            if (BigDecimal.class.equals(path.getJavaType())) {
                Predicate greaterThan = predicatesForSearchOperations.get(GREATER_THAN_OR_EQUAL)
                        .produce(cb, path, new CriteriaObject.RestrictionValues(
                                        restriction.getKey(), restriction.getMaxValue(), GREATER_THAN_OR_EQUAL
                                )
                        );
                Predicate lessThan = predicatesForSearchOperations.get(LESS_THAN_OR_EQUAL)
                        .produce(cb, path, new CriteriaObject.RestrictionValues(
                                        restriction.getKey(), restriction.getMinValue(), LESS_THAN_OR_EQUAL
                                )
                        );
                return cb.or(greaterThan, lessThan);
            }
            return cb.isNull(path);
        });


        put(LIKE, (cb, path, restriction) -> cb.like(path, "%" + restriction.getValue() + "%"));
        put(NOT_LIKE, (cb, path, restriction) -> cb.notLike(path, "%" + restriction.getValue() + "%"));


        put(LESS_THAN, (cb, path, restriction) -> {
            if (LocalDateTime.class.equals(path.getJavaType())) {
                return cb.lessThan(path, DateTimeMapper.stringToObject(restriction.getValue()));
            }
            if (BigDecimal.class.equals(path.getJavaType())) {
                return cb.lessThan(path, new BigDecimal(restriction.getValue()));
            }
            return cb.isNull(path);
        });
        put(GREATER_THAN, (cb, path, restriction) -> {
            if (LocalDateTime.class.equals(path.getJavaType())) {
                return cb.greaterThan(path, DateTimeMapper.stringToObject(restriction.getValue()));
            }
            if (BigDecimal.class.equals(path.getJavaType())) {
                return cb.greaterThan(path, new BigDecimal(restriction.getValue()));
            }
            return cb.isNull(path);
        });


        put(LESS_THAN_OR_EQUAL, (cb, path, restriction) -> {
            if (LocalDateTime.class.equals(path.getJavaType())) {
                return cb.lessThanOrEqualTo(path, DateTimeMapper.stringToObject(restriction.getValue()));
            }
            if (BigDecimal.class.equals(path.getJavaType())) {
                return cb.lessThanOrEqualTo(path, new BigDecimal(restriction.getValue()));
            }
            return cb.isNull(path);
        });

        put(GREATER_THAN_OR_EQUAL, (cb, path, restriction) -> {
            if (LocalDateTime.class.equals(path.getJavaType())) {
                return cb.greaterThanOrEqualTo(path, DateTimeMapper.stringToObject(restriction.getValue()));
            }
            if (BigDecimal.class.equals(path.getJavaType())) {
                return cb.greaterThanOrEqualTo(path, new BigDecimal(restriction.getValue()));
            }
            return cb.isNull(path);
        });

    }};


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
                .map(restriction -> predicatesForSearchOperations.get(restriction.getSearchOperation())
                        .produce(builder, root.get(restriction.getKey()), restriction))

                .toArray(Predicate[]::new);

        if (criteriaObject.getJoinOperation() == JoinOperations.AND) {
            return builder.and(predicates);
        }
        return builder.or(predicates);
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
