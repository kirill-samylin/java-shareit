package ru.practicum.shareit.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Repository
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    // Получить все запросы, сделанные конкретным пользователем (requestor)
    List<ItemRequest> findByRequestorOrderByCreatedDesc(User requestor);

    // Получить все запросы, созданные другими пользователями (не текущим), отсортированные по дате
    List<ItemRequest> findByRequestor_IdNotOrderByCreatedDesc(Long userId);
}