// Go Test File
package main

import (
	"context"
	"encoding/json"
	"errors"
	"fmt"
	"log"
	"net/http"
	"sync"
	"time"
)

// Constants
const (
	MaxUsers     = 100
	APIVersion   = "1.0.0"
	DefaultPort  = 8080
	Timeout      = 30 * time.Second
)

// Type definitions
type User struct {
	ID        int       `json:"id"`
	Name      string    `json:"name"`
	Email     string    `json:"email"`
	Active    bool      `json:"active"`
	Roles     []string  `json:"roles"`
	CreatedAt time.Time `json:"created_at"`
}

type Role string

const (
	RoleAdmin Role = "admin"
	RoleUser  Role = "user"
	RoleGuest Role = "guest"
)

type UserService interface {
	FindUser(ctx context.Context, id int) (*User, error)
	CreateUser(ctx context.Context, user *User) error
	UpdateUser(ctx context.Context, user *User) error
	DeleteUser(ctx context.Context, id int) error
}

// Struct with methods
type userService struct {
	users  map[int]*User
	mu     sync.RWMutex
	config Config
}

// Constructor function
func NewUserService(config Config) UserService {
	return &userService{
		users:  make(map[int]*User),
		config: config,
	}
}

// Methods
func (s *userService) FindUser(ctx context.Context, id int) (*User, error) {
	s.mu.RLock()
	defer s.mu.RUnlock()
	
	user, exists := s.users[id]
	if !exists {
		return nil, errors.New("user not found")
	}
	
	return user, nil
}

func (s *userService) CreateUser(ctx context.Context, user *User) error {
	s.mu.Lock()
	defer s.mu.Unlock()
	
	if user == nil {
		return errors.New("user cannot be nil")
	}
	
	if _, exists := s.users[user.ID]; exists {
		return errors.New("user already exists")
	}
	
	user.CreatedAt = time.Now()
	s.users[user.ID] = user
	
	return nil
}

func (s *userService) UpdateUser(ctx context.Context, user *User) error {
	s.mu.Lock()
	defer s.mu.Unlock()
	
	if _, exists := s.users[user.ID]; !exists {
		return errors.New("user not found")
	}
	
	s.users[user.ID] = user
	return nil
}

func (s *userService) DeleteUser(ctx context.Context, id int) error {
	s.mu.Lock()
	defer s.mu.Unlock()
	
	if _, exists := s.users[id]; !exists {
		return errors.New("user not found")
	}
	
	delete(s.users, id)
	return nil
}

// HTTP Handler
type UserHandler struct {
	service UserService
}

func NewUserHandler(service UserService) *UserHandler {
	return &UserHandler{service: service}
}

func (h *UserHandler) GetUser(w http.ResponseWriter, r *http.Request) {
	ctx := r.Context()
	id := getIDFromRequest(r)
	
	user, err := h.service.FindUser(ctx, id)
	if err != nil {
		http.Error(w, err.Error(), http.StatusNotFound)
		return
	}
	
	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(user)
}

// Goroutines and channels
func processUsers(users []User) <-chan *User {
	out := make(chan *User)
	
	go func() {
		defer close(out)
		
		for _, user := range users {
			u := user // Create copy for goroutine
			out <- &u
		}
	}()
	
	return out
}

// Worker pool pattern
func workerPool(jobs <-chan int, results chan<- int, workerCount int) {
	var wg sync.WaitGroup
	
	for i := 0; i < workerCount; i++ {
		wg.Add(1)
		go func(id int) {
			defer wg.Done()
			
			for job := range jobs {
				result := processJob(job)
				results <- result
			}
		}(i)
	}
	
	go func() {
		wg.Wait()
		close(results)
	}()
}

// Error handling patterns
func fetchData(url string) ([]byte, error) {
	resp, err := http.Get(url)
	if err != nil {
		return nil, fmt.Errorf("failed to fetch data: %w", err)
	}
	defer resp.Body.Close()
	
	if resp.StatusCode != http.StatusOK {
		return nil, fmt.Errorf("unexpected status code: %d", resp.StatusCode)
	}
	
	var data []byte
	// Read body...
	return data, nil
}

// Context usage
func doWorkWithTimeout(ctx context.Context) error {
	ctx, cancel := context.WithTimeout(ctx, 5*time.Second)
	defer cancel()
	
	select {
	case <-time.After(10 * time.Second):
		return errors.New("work took too long")
	case <-ctx.Done():
		return ctx.Err()
	}
}

// Interface and type assertion
func processValue(val interface{}) {
	switch v := val.(type) {
	case int:
		fmt.Printf("Integer: %d\n", v)
	case string:
		fmt.Printf("String: %s\n", v)
	case User:
		fmt.Printf("User: %+v\n", v)
	default:
		fmt.Printf("Unknown type: %T\n", v)
	}
}

// Variadic functions
func sum(numbers ...int) int {
	total := 0
	for _, n := range numbers {
	total += n
	}
	return total
}

// Closures
func counter() func() int {
	count := 0
	return func() int {
		count++
		return count
	}
}

// Defer, panic, recover
func safeDivide(a, b float64) (result float64, err error) {
	defer func() {
		if r := recover(); r != nil {
			err = fmt.Errorf("panic recovered: %v", r)
		}
	}()
	
	if b == 0 {
		panic("division by zero")
	}
	
	return a / b, nil
}

// Generics (Go 1.18+)
func Map[T, U any](slice []T, fn func(T) U) []U {
	result := make([]U, len(slice))
	for i, v := range slice {
		result[i] = fn(v)
	}
	return result
}

func Filter[T any](slice []T, fn func(T) bool) []T {
	result := make([]T, 0)
	for _, v := range slice {
		if fn(v) {
			result = append(result, v)
		}
	}
	return result
}

// Main function
func main() {
	config := Config{
		Port:    DefaultPort,
		Timeout: Timeout,
	}
	
	service := NewUserService(config)
	handler := NewUserHandler(service)
	
	http.HandleFunc("/users", handler.GetUser)
	
	log.Printf("Starting server on port %d", config.Port)
	if err := http.ListenAndServe(fmt.Sprintf(":%d", config.Port), nil); err != nil {
		log.Fatal(err)
	}
}
