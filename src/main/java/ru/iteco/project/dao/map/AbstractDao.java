package ru.iteco.project.dao.map;

import ru.iteco.project.dao.GenericDAO;
import ru.iteco.project.model.Identified;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

/**
 * Абстрактный класс DAO слоя, содержит в себе наиболее общие методы взаимодействия с данными
 *
 * @param <T>  тип используемой модели данных
 * @param <PK> идентификатор объекта
 */
public abstract class AbstractDao<T extends Identified<PK>, PK extends Serializable> implements GenericDAO<T, PK> {

    /*** Коллекция сущностей определенного типа*/
    protected Map<PK, T> elements;

    /*** Класс определенной сущности*/
    private Class<T> clazz;

    public AbstractDao(Class<T> elementClass, Map<PK, T> map) {
        this.clazz = elementClass;
        this.elements = map;
    }

    /**
     * Метод получения объекта из коллекции по уникальному идентификатору
     *
     * @param key - ключ, уникальный идентификатор
     * @return - объект соответствующий данному ключу или null, если по данному ключу в коллекции ничего нет
     */
    @Override
    public T getByPK(PK key) {
        return elements.get(key);
    }

    /**
     * Метод получения всех записей в коллекции
     *
     * @return - все пары коллекции
     */
    @Override
    public Collection<T> getAll() {
        return elements.values();
    }

    /**
     * Сохраняет в коллекции переданный в аргументах объект
     *
     * @param ob - сохраняемый объект
     * @return - сохраненный объект
     */
    @Override
    public T save(T ob) {
        elements.put(ob.getId(), ob);
        return ob;
    }

    /**
     * Обновляет содержимое записи в коллекции
     *
     * @param ob - объект для обновления в коллекции
     * @return - обновленный объект
     */
    @Override
    public T update(T ob) {
        elements.put(ob.getId(), ob);
        return ob;
    }

    /**
     * Метод удаляет объект из коллекции
     *
     * @param ob - удаляемый объект
     * @return - удаленный объект
     */
    @Override
    public T delete(T ob) {
        elements.remove(ob);
        return ob;
    }

    /**
     * Метод удаляет объект из коллекции по уникальному ключу
     *
     * @param key - уникальный ключ
     * @return - удаленный объект или null, если объект по данному ключу отсутствовал в коллекции
     */
    @Override
    public T deleteByPK(PK key) {
        return elements.remove(key);
    }

    /**
     * Метод добавляет в коллекцию все переданные в аргументах объекты,
     * если объект присутствует в коллекции, то данные о нем перезаписываются
     *
     * @param obs - список объектов которые нужно добавить в коллекцию
     * @return
     */
    @Override
    public Collection<T> addAll(Collection<T> obs) {
        for (T ob : obs) {
            elements.put(ob.getId(), ob);
        }
        return obs;
    }

}