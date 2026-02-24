// Java Test File
package com.example.app;

import java.util.*;
import java.util.stream.*;
import java.io.IOException;
import java.nio.file.*;

/**
 * User Service class
 * Manages user operations
 * 
 * @author John Doe
 * @version 1.0.0
 */
public class UserService {
    // Constants
    private static final int MAX_USERS = 100;
    private static final String API_VERSION = "1.0.0";
    
    // Fields
    private final Map<Long, User> users;
    private final UserRepository repository;
    private volatile boolean initialized;
    
    /**
     * Constructor for UserService
     * 
     * @param repository the user repository
     */
    public UserService(UserRepository repository) {
        this.repository = repository;
        this.users = new HashMap<>();
        this.initialized = false;
    }
    
    /**
     * Find user by ID
     * 
     * @param id the user ID
     * @return Optional containing user if found
     */
    public Optional<User> findUser(Long id) {
        return Optional.ofNullable(users.get(id));
    }
    
    /**
     * Create a new user
     * 
     * @param name user name
     * @param email user email
     * @return the created user
     * @throws IllegalArgumentException if validation fails
     */
    public User createUser(String name, String email) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        
        User user = new User.Builder()
            .id(generateId())
            .name(name)
            .email(email)
            .active(true)
            .createdAt(Instant.now())
            .build();
        
        users.put(user.getId(), user);
        return user;
    }
    
    /**
     * Get all active users
     * 
     * @return list of active users
     */
    public List<User> getActiveUsers() {
        return users.values().stream()
            .filter(User::isActive)
            .sorted(Comparator.comparing(User::getName))
            .collect(Collectors.toList());
    }
    
    /**
     * Update user information
     * 
     * @param id user ID
     * @param updater function to update user
     * @return updated user
     */
    public Optional<User> updateUser(Long id, Function<User, User> updater) {
        return findUser(id).map(user -> {
            User updated = updater.apply(user);
            users.put(id, updated);
            return updated;
        });
    }
    
    /**
     * Delete user by ID
     * 
     * @param id user ID
     * @return true if deleted, false otherwise
     */
    public boolean deleteUser(Long id) {
        return users.remove(id) != null;
    }
    
    private Long generateId() {
        return System.currentTimeMillis();
    }
}

// User class with Builder pattern
class User {
    private final Long id;
    private final String name;
    private final String email;
    private final boolean active;
    private final List<String> roles;
    private final Instant createdAt;
    
    private User(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.email = builder.email;
        this.active = builder.active;
        this.roles = Collections.unmodifiableList(builder.roles);
        this.createdAt = builder.createdAt;
    }
    
    // Getters
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public boolean isActive() { return active; }
    public List<String> getRoles() { return roles; }
    public Instant getCreatedAt() { return createdAt; }
    
    @Override
    public String toString() {
        return String.format("User{id=%d, name='%s', email='%s', active=%b}", 
            id, name, email, active);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    // Builder class
    static class Builder {
        private Long id;
        private String name;
        private String email;
        private boolean active = true;
        private List<String> roles = new ArrayList<>();
        private Instant createdAt;
        
        public Builder id(Long id) {
            this.id = id;
            return this;
        }
        
        public Builder name(String name) {
            this.name = name;
            return this;
        }
        
        public Builder email(String email) {
            this.email = email;
            return this;
        }
        
        public Builder active(boolean active) {
            this.active = active;
            return this;
        }
        
        public Builder role(String role) {
            this.roles.add(role);
            return this;
        }
        
        public Builder roles(List<String> roles) {
            this.roles = new ArrayList<>(roles);
            return this;
        }
        
        public Builder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }
        
        public User build() {
            if (id == null) throw new IllegalStateException("ID is required");
            if (name == null) throw new IllegalStateException("Name is required");
            return new User(this);
        }
    }
}

// Interface
interface UserRepository {
    Optional<User> findById(Long id);
    List<User> findAll();
    User save(User user);
    void delete(Long id);
}

// Enum
enum UserRole {
    ADMIN("Administrator", 100),
    USER("Regular User", 10),
    GUEST("Guest User", 1);
    
    private final String displayName;
    private final int priority;
    
    UserRole(String displayName, int priority) {
        this.displayName = displayName;
        this.priority = priority;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public int getPriority() {
        return priority;
    }
}

// Abstract class
abstract class BaseService<T, ID> {
    protected abstract Optional<T> findById(ID id);
    protected abstract List<T> findAll();
    protected abstract T save(T entity);
    protected abstract void delete(ID id);
    
    public long count() {
        return findAll().size();
    }
}

// Generic class
class Container<T> {
    private T value;
    
    public Container(T value) {
        this.value = value;
    }
    
    public T get() {
        return value;
    }
    
    public void set(T value) {
        this.value = value;
    }
    
    public <R> Container<R> map(Function<T, R> mapper) {
        return new Container<>(mapper.apply(value));
    }
}

// Exception handling
class UserNotFoundException extends RuntimeException {
    private final Long userId;
    
    public UserNotFoundException(Long userId) {
        super("User not found: " + userId);
        this.userId = userId;
    }
    
    public Long getUserId() {
        return userId;
    }
}

// Annotations
@FunctionalInterface
interface UserValidator {
    boolean validate(User user);
}

// Lambda expressions and streams
class UserUtils {
    public static List<String> getUserEmails(List<User> users) {
        return users.stream()
            .filter(User::isActive)
            .map(User::getEmail)
            .filter(email -> email != null && !email.isEmpty())
            .collect(Collectors.toList());
    }
    
    public static Map<String, List<User>> groupByRole(List<User> users) {
        return users.stream()
            .collect(Collectors.groupingBy(
                user -> user.getRoles().isEmpty() ? "none" : user.getRoles().get(0)
            ));
    }
    
    public static double getAverageRoleCount(List<User> users) {
        return users.stream()
            .mapToInt(user -> user.getRoles().size())
            .average()
            .orElse(0.0);
    }
}

// Main method
class Application {
    public static void main(String[] args) {
        UserRepository repository = new InMemoryUserRepository();
        UserService service = new UserService(repository);
        
        // Create users
        User user1 = service.createUser("John Doe", "john@example.com");
        User user2 = service.createUser("Jane Smith", "jane@example.com");
        
        // Find users
        service.findUser(user1.getId())
            .ifPresent(u -> System.out.println("Found: " + u));
        
        // Get active users
        List<User> activeUsers = service.getActiveUsers();
        activeUsers.forEach(System.out::println);
        
        // Try-with-resources
        try (BufferedReader reader = Files.newBufferedReader(Paths.get("users.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        // Switch expression (Java 14+)
        String roleDescription = switch (UserRole.ADMIN) {
            case ADMIN -> "Administrator with full access";
            case USER -> "Regular user with limited access";
            case GUEST -> "Guest with read-only access";
        };
        
        System.out.println(roleDescription);
    }
}

// Record (Java 14+)
record UserDTO(Long id, String name, String email, List<String> roles) {
    // Compact constructor
    public UserDTO {
        roles = List.copyOf(roles);
    }
    
    public static UserDTO from(User user) {
        return new UserDTO(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getRoles()
        );
    }
}
