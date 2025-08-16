package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto createUser(UserDto userDto) {
        if (userRepository.findByEmail(userDto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email уже используется");
        }
        User newUser = UserMapper.toEntity(userDto);
        userRepository.save(newUser);
        return UserMapper.toDto(newUser);
    }

    @Override
    public UserDto getUserById(Long id) {
        return UserMapper.toDto(userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id)));
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto updateUser(Long id, UserDto updatedUserDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        if (updatedUserDto.getName() != null) {
            user.setName(updatedUserDto.getName());
        }
        if (updatedUserDto.getEmail() != null) {
            // Проверка: нельзя обновить email, если он уже у кого-то есть
            userRepository.findByEmail(updatedUserDto.getEmail())
                    .filter(existingUser -> !existingUser.getId().equals(id))
                    .ifPresent(existing -> {
                        throw new IllegalArgumentException("Email already in use");
                    });
            user.setEmail(updatedUserDto.getEmail());
        }
        userRepository.save(user);
        return UserMapper.toDto(user);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        userRepository.delete(id);
    }
}