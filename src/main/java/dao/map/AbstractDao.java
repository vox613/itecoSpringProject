package dao.map;

import dao.GenericDAO;
import model.Identified;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

public abstract class AbstractDao<T extends Identified<PK>, PK extends Serializable> implements GenericDAO<T, PK> {

    protected Map<PK, T> elements;
    private Class<T> clazz;

    public AbstractDao(Class<T> elementClass, Map<PK, T> map) {
        this.clazz = elementClass;
        this.elements = map;
    }

    @Override
    public T getByPK(PK key) {
        return elements.get(key);
    }

    @Override
    public Collection<T> getAll() {
        return elements.values();
    }

    @Override
    public T save(T ob) {
        elements.put(ob.getId(), ob);
        return ob;
    }

    @Override
    public T update(T ob) {
        elements.put(ob.getId(), ob);
        return ob;
    }

    @Override
    public T delete(T ob) {
        elements.remove(ob);
        return ob;
    }

    @Override
    public T deleteByPK(PK key) {
        return elements.remove(key);
    }

    @Override
    public Collection<T> addAll(Collection<T> obs) {
        for (T ob : obs) {
            elements.put(ob.getId(), ob);
        }
        return obs;
    }

}