package ru.practicum.shareit.user.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserDto;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceImplTest {
    private final UserService service;
    private final EntityManager em;

    @Test
    void createUserTest() {
        UserDto userCreateDto = new UserDto(null, "name", "test@ya.ru");

        service.createUser(userCreateDto);

        TypedQuery<User> query = em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class);
        User user = query.setParameter("email", userCreateDto.getEmail()).getSingleResult();

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(userCreateDto.getName()));
        assertThat(user.getEmail(), equalTo(userCreateDto.getEmail()));
    }

    @Test
    void createUserWithRepeatedEmailMustThrownExceptionTest() {
        UserDto userCreateDto = new UserDto(null, "name", "test@ya.ru");
        service.createUser(userCreateDto);
        UserDto userCreateDtoRepeat = new UserDto(null, "name 1", "test@ya.ru");

        Assertions.assertThrows(ConflictException.class, () -> service.createUser(userCreateDtoRepeat));
    }

    @Test
    void updateUserNameTest() {
        UserDto userCreateDto = new UserDto(null, "name", "test@ya.ru");
        UserDto userDto = service.createUser(userCreateDto);
        UserDto userUpdateDto = new UserDto(userDto.getId(), "new name", null);

        service.updateUser(userDto.getId(), userUpdateDto);

        TypedQuery<User> query = em.createQuery("SELECT u FROM User u WHERE u.id = :id", User.class);
        User user = query.setParameter("id", userDto.getId()).getSingleResult();

        assertThat(user.getId(), equalTo(userDto.getId()));
        assertThat(user.getName(), equalTo(userUpdateDto.getName()));
        assertThat(user.getEmail(), equalTo(userCreateDto.getEmail()));
    }

    @Test
    void updateUserEmailTest() {
        UserDto userCreateDto = new UserDto(null, "name", "test@ya.ru");
        UserDto userDto = service.createUser(userCreateDto);
        UserDto userUpdateDto = new UserDto(userDto.getId(), null, "test-new@ya.ru");

        service.updateUser(userDto.getId(), userUpdateDto);

        TypedQuery<User> query = em.createQuery("SELECT u FROM User u WHERE u.id = :id", User.class);
        User user = query.setParameter("id", userDto.getId()).getSingleResult();

        assertThat(user.getId(), equalTo(userDto.getId()));
        assertThat(user.getName(), equalTo(userCreateDto.getName()));
        assertThat(user.getEmail(), equalTo(userUpdateDto.getEmail()));
    }

    @Test
    void updateUserUnknownUserMustThrownExceptionTest() {
        UserDto userUpdateDto = new UserDto(1L, "name", "test@ya.ru");

        Assertions.assertThrows(UserNotFoundException.class, () -> service.updateUser(userUpdateDto.getId(), userUpdateDto));
    }

    @Test
    void getUnknownUserMustThrownExceptionTest() {
        Assertions.assertThrows(UserNotFoundException.class, () -> service.getUserById(1L));
    }

    @Test
    void getUserByIdTest() {
        UserDto userCreateDto = new UserDto(null, "name", "test@ya.ru");
        UserDto userDto = service.createUser(userCreateDto);

        UserDto user = service.getUserById(userDto.getId());

        assertThat(user.getId(), equalTo(userDto.getId()));
        assertThat(user.getName(), equalTo(userDto.getName()));
        assertThat(user.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    void deleteUserTest() {
        UserDto userCreateDto = new UserDto(null, "name", "test@ya.ru");
        UserDto userDto = service.createUser(userCreateDto);

        service.deleteUser(userDto.getId());

        Assertions.assertThrows(
            NoResultException.class, () -> {
                TypedQuery<User> query = em.createQuery("SELECT u FROM User u WHERE u.id = :id", User.class);
                query.setParameter("id", userDto.getId()).getSingleResult();
            }
        );
    }
}