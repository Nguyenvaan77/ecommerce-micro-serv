package com.ecommerce.userservice.service;

import com.ecommerce.common.core.api.PageResponse;
import com.ecommerce.common.core.exception.BusinessException;
import com.ecommerce.common.core.exception.NotFoundException;
import com.ecommerce.userservice.dto.CreateUserRequest;
import com.ecommerce.userservice.dto.UpdateUserRequest;
import com.ecommerce.userservice.dto.UpdateUserRolesRequest;
import com.ecommerce.userservice.dto.UserResponse;
import com.ecommerce.userservice.entity.Role;
import com.ecommerce.userservice.entity.RoleName;
import com.ecommerce.userservice.entity.User;
import com.ecommerce.userservice.error.UserErrorCode;
import com.ecommerce.userservice.mapper.UserMapper;
import com.ecommerce.userservice.repository.RoleRepository;
import com.ecommerce.userservice.repository.UserRepository;
import com.ecommerce.userservice.validation.UserInputNormalizer;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.hibernate.exception.ConstraintViolationException;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class UserService {

    private static final String USERNAME_CONSTRAINT = "uq_users_username";
    private static final String EMAIL_CONSTRAINT = "uq_users_email";
    private static final String PHONE_CONSTRAINT = "uq_users_phone";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final UserInputNormalizer normalizer;

    public UserService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            UserMapper userMapper,
            UserInputNormalizer normalizer
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userMapper = userMapper;
        this.normalizer = normalizer;
    }

    @Transactional
    public UserResponse create(CreateUserRequest request) {
        User user = userMapper.toEntity(request);
        ensureUsernameAvailable(user.getUsername());
        ensureEmailAvailable(user.getEmail());
        ensurePhoneAvailable(user.getPhone());

        Role customerRole = findRole(RoleName.CUSTOMER);
        user.setRoles(new HashSet<>(Set.of(customerRole)));

        return userMapper.toResponse(saveUser(user));
    }

    public UserResponse getById(Long id) {
        return userMapper.toResponse(findUser(id));
    }

    public PageResponse<UserResponse> getUsers(int page, int size, String keyword) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.ASC, "id")
        );
        String normalizedKeyword = normalizer.keyword(keyword);
        Page<User> users = normalizedKeyword.isEmpty()
                ? userRepository.findAll(pageable)
                : userRepository.searchByKeyword(normalizedKeyword, pageable);

        ensurePageExists(page, users.getTotalPages());
        return PageResponse.of(
                users.getContent().stream().map(userMapper::toResponse).toList(),
                users.getNumber(),
                users.getSize(),
                users.getTotalElements()
        );
    }

    @Transactional
    public UserResponse update(Long id, UpdateUserRequest request) {
        User user = findUser(id);
        String normalizedPhone = normalizer.phone(request.phone());
        if (!sameValue(user.getPhone(), normalizedPhone)) {
            ensurePhoneAvailable(normalizedPhone);
        }

        userMapper.updateEntity(user, request);
        return userMapper.toResponse(saveUser(user));
    }

    @Transactional
    public UserResponse updateRoles(Long id, UpdateUserRolesRequest request) {
        User user = findUser(id);
        Set<Role> roles = request.roles()
                .stream()
                .map(this::findRole)
                .collect(Collectors.toCollection(HashSet::new));

        user.getRoles().clear();
        user.getRoles().addAll(roles);
        return userMapper.toResponse(userRepository.saveAndFlush(user));
    }

    private User findUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(UserErrorCode.USER_NOT_FOUND, id));
    }

    private Role findRole(RoleName roleName) {
        return roleRepository.findByName(roleName)
                .orElseThrow(() -> new NotFoundException(UserErrorCode.ROLE_NOT_FOUND, roleName));
    }

    private void ensureUsernameAvailable(String username) {
        if (userRepository.existsByUsernameIgnoreCase(username)) {
            throw new BusinessException(UserErrorCode.USERNAME_ALREADY_EXISTS);
        }
    }

    private void ensureEmailAvailable(String email) {
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new BusinessException(UserErrorCode.EMAIL_ALREADY_EXISTS);
        }
    }

    private void ensurePhoneAvailable(String phone) {
        if (phone != null && userRepository.existsByPhone(phone)) {
            throw new BusinessException(UserErrorCode.PHONE_ALREADY_EXISTS);
        }
    }

    private void ensurePageExists(int requestedPage, int totalPages) {
        boolean outOfRange = (totalPages == 0 && requestedPage > 0)
                || (totalPages > 0 && requestedPage >= totalPages);
        if (outOfRange) {
            throw new BusinessException(
                    UserErrorCode.USER_PAGE_OUT_OF_RANGE,
                    requestedPage
            );
        }
    }

    private User saveUser(User user) {
        try {
            return userRepository.saveAndFlush(user);
        } catch (DataIntegrityViolationException exception) {
            throw mapConflict(exception);
        }
    }

    private RuntimeException mapConflict(DataIntegrityViolationException exception) {
        String sqlState = sqlState(exception);
        if (!"23505".equals(sqlState)) {
            return exception;
        }

        String details = constraintName(exception);
        if (details == null) {
            details = exceptionDetails(exception);
        }
        details = details.toLowerCase(Locale.ROOT);
        if (details.contains(USERNAME_CONSTRAINT) || details.contains("user_name")) {
            return new BusinessException(UserErrorCode.USERNAME_ALREADY_EXISTS);
        }
        if (details.contains(EMAIL_CONSTRAINT) || details.contains("email")) {
            return new BusinessException(UserErrorCode.EMAIL_ALREADY_EXISTS);
        }
        if (details.contains(PHONE_CONSTRAINT) || details.contains("phone_number")) {
            return new BusinessException(UserErrorCode.PHONE_ALREADY_EXISTS);
        }
        return new BusinessException(UserErrorCode.USER_UNIQUE_CONFLICT);
    }

    private String constraintName(Throwable exception) {
        Throwable current = exception;
        while (current != null) {
            if (current instanceof ConstraintViolationException constraintViolation) {
                return constraintViolation.getConstraintName();
            }
            current = current.getCause();
        }
        return null;
    }

    private String sqlState(Throwable exception) {
        Throwable current = exception;
        while (current != null) {
            if (current instanceof SQLException sqlException
                    && sqlException.getSQLState() != null) {
                return sqlException.getSQLState();
            }
            current = current.getCause();
        }
        return null;
    }

    private String exceptionDetails(Throwable exception) {
        StringBuilder details = new StringBuilder();
        Throwable current = exception;
        while (current != null) {
            if (current.getMessage() != null) {
                details.append(' ')
                        .append(current.getMessage().toLowerCase(Locale.ROOT));
            }
            current = current.getCause();
        }
        return details.toString();
    }

    private boolean sameValue(String first, String second) {
        return first == null ? second == null : first.equals(second);
    }
}
