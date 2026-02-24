// C# Test File
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using System.Text.Json;
using System.Text.Json.Serialization;
using System.ComponentModel;
using System.ComponentModel.DataAnnotations;
using System.Runtime.InteropServices;
using System.Runtime.Serialization;
using System.IO;

namespace UserManagement
{
    /// <summary>
    /// User service class for managing user operations
    /// </summary>
    [Serializable]
    [Description("Service for managing user operations")]
    public class UserService
    {
        // Constants
        private const int MaxUsers = 100;
        private const string ApiVersion = "1.0.0";
        
        // Fields
        private readonly Dictionary<long, User> _users;
        private readonly IUserRepository _repository;
        private volatile bool _initialized;
        
        // Property
        public int UserCount => _users.Count;
        
        /// <summary>
        /// Constructor for UserService
        /// </summary>
        /// <param name="repository">The user repository</param>
        public UserService(IUserRepository repository)
        {
            _repository = repository ?? throw new ArgumentNullException(nameof(repository));
            _users = new Dictionary<long, User>();
            _initialized = false;
        }
        
        /// <summary>
        /// Find user by ID
        /// </summary>
        /// <param name="id">The user ID</param>
        /// <returns>User if found, null otherwise</returns>
        public User? FindUser(long id)
        {
            return _users.TryGetValue(id, out var user) ? user : null;
        }
        
        /// <summary>
        /// Create a new user asynchronously
        /// </summary>
        /// <param name="name">User name</param>
        /// <param name="email">User email</param>
        /// <returns>The created user</returns>
        /// <exception cref="ArgumentException">Thrown when validation fails</exception>
        public async Task<User> CreateUserAsync(string name, string email)
        {
            if (string.IsNullOrWhiteSpace(name))
            {
                throw new ArgumentException("Name cannot be empty", nameof(name));
            }
            
            var user = new User
            {
                Id = GenerateId(),
                Name = name,
                Email = email,
                Active = true,
                Roles = new List<string> { "user" },
                CreatedAt = DateTime.UtcNow
            };
            
            _users.Add(user.Id, user);
            await _repository.SaveAsync(user);
            
            return user;
        }
        
        /// <summary>
        /// Get all active users
        /// </summary>
        /// <returns>List of active users</returns>
        public IEnumerable<User> GetActiveUsers()
        {
            return _users.Values
                .Where(u => u.Active)
                .OrderBy(u => u.Name)
                .ToList();
        }
        
        /// <summary>
        /// Update user information
        /// </summary>
        /// <param name="id">User ID</param>
        /// <param name="updater">Function to update user</param>
        /// <returns>Updated user or null</returns>
        public User? UpdateUser(long id, Func<User, User> updater)
        {
            var user = FindUser(id);
            if (user == null) return null;
            
            var updated = updater(user);
            _users[id] = updated;
            return updated;
        }
        
        /// <summary>
        /// Delete user by ID
        /// </summary>
        /// <param name="id">User ID</param>
        /// <returns>True if deleted, false otherwise</returns>
        [Obsolete("Use DeleteUserAsync instead")]
        [EditorBrowsable(EditorBrowsableState.Never)]
        public bool DeleteUser(long id) => _users.Remove(id);
        
        private long GenerateId() => DateTimeOffset.UtcNow.ToUnixTimeMilliseconds();
    }
    
    // User class
    [Serializable]
    [DataContract]
    public class User
    {
        [Key]
        [JsonPropertyName("id")]
        public long Id { get; set; }
        
        [Required]
        [StringLength(100)]
        [JsonPropertyName("name")]
        public string Name { get; set; } = string.Empty;
        
        [Required]
        [EmailAddress]
        [JsonPropertyName("email")]
        public string Email { get; set; } = string.Empty;
        
        [JsonPropertyName("active")]
        public bool Active { get; set; }
        
        [JsonPropertyName("roles")]
        public List<string> Roles { get; set; } = new();
        
        [JsonPropertyName("created_at")]
        public DateTime CreatedAt { get; set; }
        
        // Computed property
        public string FullInfo => $"{Name} <{Email}>";
        
        // Method
        public bool HasRole(string role) => Roles.Contains(role);
        
        public override string ToString()
        {
            return $"User {{ Id: {Id}, Name: '{Name}', Email: '{Email}', Active: {Active} }}";
        }
        
        public override bool Equals(object? obj)
        {
            return obj is User user && Id == user.Id;
        }
        
        public override int GetHashCode() => Id.GetHashCode();
    }
    
    // Record (C# 9+)
    [Serializable]
    [JsonSerializable(typeof(UserDto))]
    public record UserDto(long Id, string Name, string Email, List<string> Roles)
    {
        public static UserDto FromUser(User user)
        {
            return new UserDto(user.Id, user.Name, user.Email, user.Roles);
        }
    }
    
    // Struct
    [Serializable]
    [StructLayout(LayoutKind.Sequential)]
    public struct Point
    {
        public int X { get; set; }
        public int Y { get; set; }
        
        public Point(int x, int y)
        {
            X = x;
            Y = y;
        }
        
        public double Distance(Point other)
        {
            int dx = X - other.X;
            int dy = Y - other.Y;
            return Math.Sqrt(dx * dx + dy * dy);
        }
    }
    
    // Interface
    public interface IUserRepository
    {
        Task<User?> FindByIdAsync(long id);
        Task<List<User>> FindAllAsync();
        Task SaveAsync(User user);
        Task DeleteAsync(long id);
    }
    
    // Enum
    public enum UserRole
    {
        Admin = 100,
        User = 10,
        Guest = 1
    }
    
    // Flags enum
    [Flags]
    public enum Permissions
    {
        None = 0,
        Read = 1,
        Write = 2,
        Delete = 4,
        Admin = Read | Write | Delete
    }
    
    // Abstract class
    [Serializable]
    [EditorBrowsable(EditorBrowsableState.Advanced)]
    public abstract class BaseService<T, TId>
    {
        protected abstract Task<T?> FindByIdAsync(TId id);
        protected abstract Task<List<T>> FindAllAsync();
        protected abstract Task SaveAsync(T entity);
        protected abstract Task DeleteAsync(TId id);
        
        public virtual async Task<int> CountAsync()
        {
            var all = await FindAllAsync();
            return all.Count;
        }
    }
    
    // Generic class
    [Serializable]
    [Description("Generic container class")]
    public class Container<T>
    {
        private T _value;
        
        public Container(T value)
        {
            _value = value;
        }
        
        public T Get() => _value;
        public void Set(T value) => _value = value;
        
        public Container<TResult> Map<TResult>(Func<T, TResult> mapper)
        {
            return new Container<TResult>(mapper(_value));
        }
    }
    
    // Custom exception
    [Serializable]
    public class UserNotFoundException : Exception
    {
        public long UserId { get; }
        
        public UserNotFoundException(long userId) 
            : base($"User not found: {userId}")
        {
            UserId = userId;
        }
    }
    
    // Extension methods
    public static class UserExtensions
    {
        public static bool IsAdmin(this User user)
        {
            return user.HasRole("admin");
        }
        
        public static IEnumerable<string> GetEmails(this IEnumerable<User> users)
        {
            return users
                .Where(u => u.Active)
                .Select(u => u.Email)
                .Where(email => !string.IsNullOrEmpty(email));
        }
    }
    
    // Delegate
    public delegate bool UserValidator(User user);
    
    // LINQ examples
    public class UserUtils
    {
        public static List<string> GetUserEmails(List<User> users)
        {
            return users
                .Where(u => u.Active)
                .Select(u => u.Email)
                .Where(email => !string.IsNullOrWhiteSpace(email))
                .ToList();
        }
        
        public static Dictionary<string, List<User>> GroupByRole(List<User> users)
        {
            return users
                .GroupBy(u => u.Roles.FirstOrDefault() ?? "none")
                .ToDictionary(g => g.Key, g => g.ToList());
        }
        
        public static double GetAverageRoleCount(List<User> users)
        {
            return users.Average(u => u.Roles.Count);
        }
    }
    
    // Async/Await patterns
    [Description("Service for fetching user data from API")]
    public class UserDataService
    {
        private readonly HttpClient _httpClient;
        
        public UserDataService(HttpClient httpClient)
        {
            _httpClient = httpClient;
        }
        
        [Obsolete("Use FetchUserByIdAsync instead", false)]
        public async Task<User?> FetchUserAsync(long id)
        {
            try
            {
                var response = await _httpClient.GetAsync($"/api/users/{id}");
                response.EnsureSuccessStatusCode();
                
                var content = await response.Content.ReadAsStringAsync();
                return JsonSerializer.Deserialize<User>(content);
            }
            catch (HttpRequestException ex)
            {
                Console.WriteLine($"Error fetching user: {ex.Message}");
                return null;
            }
        }
        
        public async Task<List<User>> FetchAllUsersAsync()
        {
            var response = await _httpClient.GetAsync("/api/users");
            var content = await response.Content.ReadAsStringAsync();
            return JsonSerializer.Deserialize<List<User>>(content) ?? new List<User>();
        }
    }
    
    // Pattern matching (C# 8+)
    public class PatternMatchingExamples
    {
        public static string GetUserInfo(object obj)
        {
            return obj switch
            {
                User { Active: true } user => $"Active user: {user.Name}",
                User { Active: false } user => $"Inactive user: {user.Name}",
                string name => $"Name: {name}",
                int id => $"ID: {id}",
                null => "Null value",
                _ => "Unknown type"
            };
        }
        
        public static bool IsValidUser(User? user)
        {
            return user is { Active: true, Email: not null };
        }
    }
    
    // Nullable reference types
    public class NullableExamples
    {
        public string? GetUserEmail(long? userId)
        {
            if (userId == null) return null;
            
            var user = FindUser(userId.Value);
            return user?.Email;
        }
        
        private User? FindUser(long id) => null;
    }
    
    // Properties and indexers
    public class UserCollection
    {
        private readonly List<User> _users = new();
        
        public User this[int index]
        {
            get => _users[index];
            set => _users[index] = value;
        }
        
        public User this[string name]
        {
            get => _users.First(u => u.Name == name);
        }
    }
    
    // Events
    public class UserEventArgs : EventArgs
    {
        public User User { get; set; }
        public UserEventArgs(User user) => User = user;
    }
    
    public class UserEventService
    {
        public event EventHandler<UserEventArgs>? UserCreated;
        public event EventHandler<UserEventArgs>? UserDeleted;
        
        protected virtual void OnUserCreated(User user)
        {
            UserCreated?.Invoke(this, new UserEventArgs(user));
        }
        
        protected virtual void OnUserDeleted(User user)
        {
            UserDeleted?.Invoke(this, new UserEventArgs(user));
        }
    }
    
    // Main program
    class Program
    {
        static async Task Main(string[] args)
        {
            var repository = new InMemoryUserRepository();
            var service = new UserService(repository);
            
            // Create users
            var user1 = await service.CreateUserAsync("John Doe", "john@example.com");
            var user2 = await service.CreateUserAsync("Jane Smith", "jane@example.com");
            
            // Find user
            var foundUser = service.FindUser(user1.Id);
            Console.WriteLine(foundUser?.ToString() ?? "User not found");
            
            // Get active users
            var activeUsers = service.GetActiveUsers();
            foreach (var user in activeUsers)
            {
                Console.WriteLine(user);
            }
            
            // Using statement
            using (var reader = new StreamReader("users.txt"))
            {
                string? line;
                while ((line = await reader.ReadLineAsync()) != null)
                {
                    Console.WriteLine(line);
                }
            }
            
            // Lambda expressions
            var names = activeUsers.Select(u => u.Name).ToList();
            var admins = activeUsers.Where(u => u.HasRole("admin")).ToList();
            
            // String interpolation
            var count = service.UserCount;
            Console.WriteLine($"Total users: {count}");
            
            // Tuple
            var (success, message) = ValidateUser(user1);
            Console.WriteLine($"Validation: {success}, {message}");
        }
        
        static (bool Success, string Message) ValidateUser(User user)
        {
            if (string.IsNullOrWhiteSpace(user.Name))
                return (false, "Name is required");
            
            if (string.IsNullOrWhiteSpace(user.Email))
                return (false, "Email is required");
            
            return (true, "Valid user");
        }
    }
    
    // In-memory repository implementation
    public class InMemoryUserRepository : IUserRepository
    {
        private readonly Dictionary<long, User> _storage = new();
        
        public Task<User?> FindByIdAsync(long id)
        {
            return Task.FromResult(_storage.TryGetValue(id, out var user) ? user : null);
        }
        
        public Task<List<User>> FindAllAsync()
        {
            return Task.FromResult(_storage.Values.ToList());
        }
        
        public Task SaveAsync(User user)
        {
            _storage[user.Id] = user;
            return Task.CompletedTask;
        }
        
        public Task DeleteAsync(long id)
        {
            _storage.Remove(id);
            return Task.CompletedTask;
        }
    }
}
