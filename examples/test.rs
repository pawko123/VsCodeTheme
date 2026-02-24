// Rust Test File
use std::collections::HashMap;
use std::error::Error;
use std::fmt;
use std::sync::{Arc, Mutex};

// Constants
const MAX_USERS: usize = 100;
const API_VERSION: &str = "1.0.0";

// Struct definitions
#[derive(Debug, Clone, PartialEq, Eq)]
struct User {
    id: u64,
    name: String,
    email: String,
    active: bool,
    roles: Vec<Role>,
}

#[derive(Debug, Clone, PartialEq, Eq)]
enum Role {
    Admin,
    User,
    Guest,
}

// Custom error type
#[derive(Debug)]
enum UserError {
    NotFound(u64),
    AlreadyExists(u64),
    InvalidEmail(String),
}

impl fmt::Display for UserError {
    fn fmt(&self, f: &mut fmt::Formatter) -> fmt::Result {
        match self {
            UserError::NotFound(id) => write!(f, "User {} not found", id),
            UserError::AlreadyExists(id) => write!(f, "User {} already exists", id),
            UserError::InvalidEmail(email) => write!(f, "Invalid email: {}", email),
        }
    }
}

impl Error for UserError {}

// Trait definition
trait UserRepository {
    fn find(&self, id: u64) -> Result<User, UserError>;
    fn save(&mut self, user: User) -> Result<(), UserError>;
    fn delete(&mut self, id: u64) -> Result<(), UserError>;
}

// Implementation
struct InMemoryUserRepository {
    users: HashMap<u64, User>,
}

impl InMemoryUserRepository {
    fn new() -> Self {
        Self {
            users: HashMap::new(),
        }
    }
    
    fn with_capacity(capacity: usize) -> Self {
        Self {
            users: HashMap::with_capacity(capacity),
        }
    }
}

impl UserRepository for InMemoryUserRepository {
    fn find(&self, id: u64) -> Result<User, UserError> {
        self.users
            .get(&id)
            .cloned()
            .ok_or(UserError::NotFound(id))
    }
    
    fn save(&mut self, user: User) -> Result<(), UserError> {
        if self.users.contains_key(&user.id) {
            return Err(UserError::AlreadyExists(user.id));
        }
        
        self.users.insert(user.id, user);
        Ok(())
    }
    
    fn delete(&mut self, id: u64) -> Result<(), UserError> {
        self.users
            .remove(&id)
            .map(|_| ())
            .ok_or(UserError::NotFound(id))
    }
}

// Generic struct
struct Container<T> {
    value: T,
}

impl<T> Container<T> {
    fn new(value: T) -> Self {
        Self { value }
    }
    
    fn get(&self) -> &T {
        &self.value
    }
    
    fn set(&mut self, value: T) {
        self.value = value;
    }
}

// Lifetime annotations
struct UserView<'a> {
    name: &'a str,
    email: &'a str,
}

impl<'a> UserView<'a> {
    fn new(name: &'a str, email: &'a str) -> Self {
        Self { name, email }
    }
}

// Functions
fn greet(name: &str) -> String {
    format!("Hello, {}!", name)
}

fn greet_with_age(name: &str, age: Option<u32>) -> String {
    match age {
        Some(a) => format!("Hello, {}! You are {} years old.", name, a),
        None => format!("Hello, {}!", name),
    }
}

// Pattern matching
fn process_result(result: Result<User, UserError>) {
    match result {
        Ok(user) => println!("Found user: {:?}", user),
        Err(UserError::NotFound(id)) => println!("User {} not found", id),
        Err(e) => println!("Error: {}", e),
    }
}

// If let
fn get_first_element(vec: Vec<i32>) -> Option<i32> {
    if let Some(first) = vec.first() {
        Some(*first)
    } else {
        None
    }
}

// Closures
fn apply_operation<F>(x: i32, y: i32, op: F) -> i32
where
    F: Fn(i32, i32) -> i32,
{
    op(x, y)
}

fn main() {
    // Variables
    let immutable = 42;
    let mut mutable = "Hello";
    mutable = "World";
    
    // Shadowing
    let x = 5;
    let x = x + 1;
    let x = x * 2;
    
    // References and borrowing
    let s = String::from("hello");
    let len = calculate_length(&s);
    println!("Length of '{}' is {}", s, len);
    
    // Mutable reference
    let mut s = String::from("hello");
    change(&mut s);
    
    // Ownership and moves
    let s1 = String::from("hello");
    let s2 = s1; // s1 is moved, no longer valid
    // println!("{}", s1); // This would cause an error
    
    // Clone
    let s1 = String::from("hello");
    let s2 = s1.clone();
    
    // Collections
    let mut numbers = vec![1, 2, 3, 4, 5];
    numbers.push(6);
    
    let mut map = HashMap::new();
    map.insert("key", "value");
    
    // Iterators
    let doubled: Vec<i32> = numbers.iter().map(|x| x * 2).collect();
    let evens: Vec<&i32> = numbers.iter().filter(|x| *x % 2 == 0).collect();
    let sum: i32 = numbers.iter().sum();
    
    // Range
    for i in 0..10 {
        println!("{}", i);
    }
    
    for i in 0..=10 {
        println!("{}", i);
    }
    
    // Match
    let number = 42;
    match number {
        0 => println!("Zero"),
        1..=10 => println!("Between 1 and 10"),
        42 => println!("The answer"),
        _ => println!("Something else"),
    }
    
    // Closures
    let add = |a, b| a + b;
    let multiply = |a: i32, b: i32| -> i32 { a * b };
    
    // Higher-order functions
    let result = apply_operation(5, 3, |x, y| x + y);
    
    // Option
    let some_value = Some(5);
    let no_value: Option<i32> = None;
    
    if let Some(x) = some_value {
        println!("Value: {}", x);
    }
    
    let value = some_value.unwrap_or(0);
    let value = some_value.unwrap_or_else(|| 0);
    
    // Result
    let result: Result<i32, &str> = Ok(42);
    let result: Result<i32, &str> = Err("error");
    
    match result {
        Ok(value) => println!("Success: {}", value),
        Err(e) => println!("Error: {}", e),
    }
    
    // Question mark operator
    fn read_file() -> Result<String, std::io::Error> {
        let content = std::fs::read_to_string("file.txt")?;
        Ok(content)
    }
    
    // Macros
    println!("Hello, world!");
    vec![1, 2, 3];
    format!("Value: {}", 42);
    
    // String manipulation
    let s = String::from("hello");
    let s = s + " world";
    let s = format!("{} {}", "hello", "world");
    
    // Threading
    use std::thread;
    use std::time::Duration;
    
    let handle = thread::spawn(|| {
        for i in 1..10 {
            println!("Thread: {}", i);
            thread::sleep(Duration::from_millis(1));
        }
    });
    
    handle.join().unwrap();
    
    // Shared state with Arc and Mutex
    let counter = Arc::new(Mutex::new(0));
    let mut handles = vec![];
    
    for _ in 0..10 {
        let counter = Arc::clone(&counter);
        let handle = thread::spawn(move || {
            let mut num = counter.lock().unwrap();
            *num += 1;
        });
        handles.push(handle);
    }
    
    for handle in handles {
        handle.join().unwrap();
    }
    
    // Smart pointers
    let b = Box::new(5);
    let r = std::rc::Rc::new(5);
    let rf = std::rc::Rc::clone(&r);
    
    // Attributes
    #[derive(Debug)]
    #[allow(dead_code)]
    struct Point {
        x: i32,
        y: i32,
    }
    
    // Tests
    #[cfg(test)]
    mod tests {
        use super::*;
        
        #[test]
        fn test_greet() {
            assert_eq!(greet("World"), "Hello, World!");
        }
        
        #[test]
        fn test_user_repository() {
            let mut repo = InMemoryUserRepository::new();
            let user = User {
                id: 1,
                name: String::from("John"),
                email: String::from("john@example.com"),
                active: true,
                roles: vec![Role::User],
            };
            
            assert!(repo.save(user.clone()).is_ok());
            assert_eq!(repo.find(1).unwrap(), user);
        }
    }
}

fn calculate_length(s: &String) -> usize {
    s.len()
}

fn change(s: &mut String) {
    s.push_str(", world");
}
