package ru.prplhd.currencyexchange.dao;

import java.util.List;

public interface BaseDao<T> {
    List<T> findAll();

    T save(T entity);
}