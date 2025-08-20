package ru.practicum.shareit.item.service;

import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.mapper.CommentMapper;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;

    @Override
    public ItemDto addItem(Long ownerId, ItemDto itemDto) {
        User user = userRepository.findById(ownerId)
                .orElseThrow(() -> new UserNotFoundException(ownerId));

        Item item = ItemMapper.toEntity(itemDto);
        item.setOwner(user);
        Item savedItem = itemRepository.save(item);
        return ItemMapper.toDto(savedItem);
    }

    @Override
    public ItemDto updateItem(Long ownerId, Long itemId, ItemDto itemDto) {
        userRepository.findById(ownerId)
                .orElseThrow(() -> new UserNotFoundException(ownerId));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(itemId));

        if (!item.getOwner().getId().equals(ownerId)) {
            throw new RuntimeException("Только владелец может обновить предмет");
        }
        if (itemDto.getName() != null) item.setName(itemDto.getName());
        if (itemDto.getDescription() != null) item.setDescription(itemDto.getDescription());
        if (itemDto.getAvailable() != null) item.setAvailable(itemDto.getAvailable());
        Item updated = itemRepository.save(item);
        return ItemMapper.toDto(updated);
    }

    @Override
    public ItemDto getItemById(Long userId, Long itemId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(itemId));

        ItemDto itemDto = ItemMapper.toDto(item);

        List<Comment> comments = commentRepository.findByItemId(item.getId());
        itemDto.setComments(
                comments.stream().map(CommentMapper::toDto).collect(Collectors.toList())
        );

        return itemDto;
    }

    @Override
    public List<ItemDto> getAllItemsByUser(Long ownerId) {
        // проверить что пользователь существует
        userRepository.findById(ownerId)
        .orElseThrow(() -> new UserNotFoundException(ownerId));

        return itemRepository.findAll().stream()
                .filter(item -> item.getOwner() != null && ownerId.equals(item.getOwner().getId()))
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }
        String query = text.toLowerCase(Locale.ROOT);
        return itemRepository.findAll().stream()
                .filter(item -> Boolean.TRUE.equals(item.getAvailable()))
                .filter(item -> item.getName().toLowerCase().contains(query)
                        || item.getDescription().toLowerCase().contains(query))
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDto addComment(Long userId, Long itemId, String text) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(itemId));

        // Проверка: брал ли пользователь вещь как "booker" и аренда уже завершена
        boolean hasBooking = bookingRepository.existsByItem_IdAndBooker_IdAndEndBefore(
                itemId, userId, LocalDateTime.now()
        );

        if (!hasBooking) {
            throw new ValidationException("Пользователь не может комментировать вещь, которую не арендовал");
        }

        Comment comment = new Comment();
        comment.setAuthor(user);
        comment.setItem(item);
        comment.setText(text);
        comment.setCreated(LocalDateTime.now());

        return CommentMapper.toDto(commentRepository.save(comment));
    }
}
