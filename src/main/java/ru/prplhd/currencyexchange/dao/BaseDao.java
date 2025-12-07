package ru.prplhd.currencyexchange.dao;

import java.util.List;
import java.util.Optional;

public interface BaseDao<T> {
    Optional<T> findById(Long id);

    List<T> findAll();

    T save(T entity);

 //   Optional<T> update(T entity);
}