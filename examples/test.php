<?php
/**
 * PHP Test File
 * Testing syntax highlighting for PHP
 */

namespace App\Controllers;

use App\Models\User;
use App\Services\UserService;
use Illuminate\Http\Request;
use Illuminate\Http\Response;
use Exception;

/**
 * User Controller
 * Handles user-related operations
 */
class UserController
{
    private UserService $userService;
    private array $config;
    protected string $tableName = 'users';
    public const MAX_USERS = 100;
    
    /**
     * Constructor
     */
    public function __construct(UserService $userService, array $config = [])
    {
        $this->userService = $userService;
        $this->config = $config;
    }
    
    /**
     * Get all users
     */
    public function index(Request $request): Response
    {
        $page = $request->get('page', 1);
        $limit = $request->get('limit', 10);
        
        $users = $this->userService->getUsers($page, $limit);
        
        return response()->json([
            'success' => true,
            'data' => $users,
            'total' => count($users)
        ]);
    }
    
    /**
     * Get single user
     */
    public function show(int $id): Response
    {
        try {
            $user = $this->userService->findUser($id);
            
            if (!$user) {
                throw new Exception("User not found");
            }
            
            return response()->json([
                'success' => true,
                'data' => $user
            ]);
        } catch (Exception $e) {
            return response()->json([
                'success' => false,
                'error' => $e->getMessage()
            ], 404);
        }
    }
    
    /**
     * Create new user
     */
    public function store(Request $request): Response
    {
        $data = $request->validate([
            'name' => 'required|string|max:255',
            'email' => 'required|email|unique:users',
            'password' => 'required|min:8'
        ]);
        
        $data['password'] = password_hash($data['password'], PASSWORD_DEFAULT);
        
        $user = $this->userService->createUser($data);
        
        return response()->json([
            'success' => true,
            'data' => $user
        ], 201);
    }
    
    /**
     * Update user
     */
    public function update(Request $request, int $id): Response
    {
        $user = $this->userService->findUser($id);
        
        if ($user === null) {
            return response()->json(['error' => 'Not found'], 404);
        }
        
        $data = $request->only(['name', 'email']);
        $updated = $this->userService->updateUser($id, $data);
        
        return response()->json([
            'success' => true,
            'data' => $updated
        ]);
    }
    
    /**
     * Delete user
     */
    public function destroy(int $id): Response
    {
        $deleted = $this->userService->deleteUser($id);
        
        return response()->json([
            'success' => $deleted,
            'message' => $deleted ? 'User deleted' : 'Failed to delete'
        ]);
    }
}

// Functions
function greet(string $name, ?int $age = null): string
{
    if ($age !== null) {
        return "Hello, {$name}! You are {$age} years old.";
    }
    return "Hello, {$name}!";
}

// Variables
$string = "Hello World";
$number = 42;
$float = 3.14159;
$boolean = true;
$null = null;
$array = [1, 2, 3, 4, 5];
$assoc = ['key' => 'value', 'name' => 'John'];

// String interpolation
$name = "John";
$message1 = "Hello, $name!";
$message2 = "Hello, {$name}!";
$message3 = 'Hello, $name!'; // Not interpolated

// Heredoc
$heredoc = <<<EOT
This is a heredoc string.
It can span multiple lines.
Variables like $name are interpolated.
EOT;

// Nowdoc
$nowdoc = <<<'EOT'
This is a nowdoc string.
Variables like $name are NOT interpolated.
EOT;

// Array operations
$numbers = [1, 2, 3, 4, 5];
$squared = array_map(fn($n) => $n ** 2, $numbers);
$evens = array_filter($numbers, fn($n) => $n % 2 === 0);
$sum = array_reduce($numbers, fn($carry, $n) => $carry + $n, 0);

// Null coalescing
$value = $data['key'] ?? 'default';
$value2 = $data['key'] ?: 'default';

// Spaceship operator
$compare = $a <=> $b;

// Control structures
if ($condition) {
    // do something
} elseif ($otherCondition) {
    // do something else
} else {
    // default
}

// Match expression (PHP 8)
$result = match($value) {
    1 => 'one',
    2 => 'two',
    3, 4, 5 => 'three to five',
    default => 'other'
};

// Loops
foreach ($users as $user) {
    echo $user->name;
}

for ($i = 0; $i < 10; $i++) {
    echo $i;
}

while ($condition) {
    // do something
}

do {
    // do something
} while ($condition);

// Exception handling
try {
    $result = riskyOperation();
} catch (Exception $e) {
    error_log($e->getMessage());
    throw $e;
} finally {
    cleanup();
}

// Anonymous functions
$greet = function(string $name) use ($prefix): string {
    return "{$prefix} {$name}";
};

// Arrow functions (PHP 7.4+)
$double = fn($n) => $n * 2;

// Traits
trait Loggable
{
    public function log(string $message): void
    {
        echo "[LOG] {$message}";
    }
}

// Interface
interface UserRepositoryInterface
{
    public function find(int $id): ?User;
    public function save(User $user): bool;
}

// Abstract class
abstract class BaseController
{
    abstract protected function getModel(): string;
    
    public function handleRequest(): void
    {
        // implementation
    }
}

?>
