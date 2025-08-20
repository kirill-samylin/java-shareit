package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    // Найти все предметы по владельцу
    List<Item> findByOwner(User owner);

    // Поиск по названию, независимо от регистра
    List<Item> findByNameContainingIgnoreCase(String name);
}
