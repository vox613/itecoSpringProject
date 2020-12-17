package ru.iteco.project.service.specifications;

import ru.iteco.project.domain.Identified;
import ru.iteco.project.service.mappers.DateTimeMapper;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.math.BigDecimal;

/**
 * Класс содержит обобщенные методы для формирования объектов Predicate для Specification
 */
public class SpecificationSupport<T extends Identified> {

    /**
     * Метод предоставляет путь к атрибуту
     *
     * @param root - корневой тип
     * @param key  - наименование атрибута
     * @return - путь к атрибуту
     */
    public Path<T> getPath(Root<T> root, String key) {
        return root.get(key);
    }

    /**
     * Метод получения предиката с equals от вычисляемого выражения
     *
     * @param criteriaBuilder - объект для построения критериев и сложных выборок
     * @param path            - путь к атрибуту
     * @param searchObj       - искомое значение атрибута
     * @return - объект предиката
     */
    public Predicate getEqualsPredicate(CriteriaBuilder criteriaBuilder, Path<T> path, Object searchObj) {
        return criteriaBuilder.equal(path, searchObj);
    }

    /**
     * Метод получения предиката с Like от вычисляемого выражения
     *
     * @param criteriaBuilder - объект для построения критериев и сложных выборок
     * @param path            - путь к атрибуту
     * @param searchPart      - искомое значение атрибута
     * @return - объект предиката
     */
    public Predicate getLikePredicate(CriteriaBuilder criteriaBuilder, Path path, String searchPart) {
        return criteriaBuilder.like(path, "%" + searchPart + "%");
    }


    /**
     * Метод получения предиката с GreaterThanOrEqualTo от вычисляемого выражения
     *
     * @param criteriaBuilder - объект для построения критериев и сложных выборок
     * @param path            - путь к атрибуту
     * @param searchObj       - искомое значение атрибута
     * @searchObj - объект предиката
     */
    public Predicate getGreaterThanOrEqualToPredicate(CriteriaBuilder criteriaBuilder, Path path, String searchObj) {
        return criteriaBuilder.greaterThanOrEqualTo(path, DateTimeMapper.stringToObject(searchObj));
    }


    /**
     * Метод получения предиката с between от вычисляемого выражения
     *
     * @param criteriaBuilder - объект для построения критериев и сложных выборок
     * @param path            - путь к атрибуту
     * @param minPrice        - минимальная величина стоимости
     * @param maxPrice        - максимальная величина стоимости
     */
    public Predicate getBetweenPredicate(CriteriaBuilder criteriaBuilder, Path path, String minPrice, String maxPrice) {
        return criteriaBuilder.between(path, new BigDecimal(minPrice), new BigDecimal(maxPrice));
    }

}
