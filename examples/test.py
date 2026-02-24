#!/usr/bin/env python3
"""
Python Test File
Testing syntax highlighting for Python
"""

import os
import sys
from typing import List, Dict, Optional, Any
from datetime import datetime
from dataclasses import dataclass

# Constants
MAX_USERS = 100
API_VERSION = "1.0.0"
DEBUG = True

@dataclass
class User:
    """User data class"""
    id: int
    name: str
    email: str
    active: bool = True
    roles: List[str] = None
    
    def __post_init__(self):
        if self.roles is None:
            self.roles = ['user']


class UserService:
    """Service for managing users"""
    
    def __init__(self, config: Dict[str, Any]):
        self.config = config
        self.users: List[User] = []
        self._initialized = False
    
    def __str__(self) -> str:
        return f"UserService with {len(self.users)} users"
    
    def __repr__(self) -> str:
        return f"UserService(users={len(self.users)})"
    
    async def find_user(self, user_id: int) -> Optional[User]:
        """Find user by ID"""
        for user in self.users:
            if user.id == user_id:
                return user
        return None
    
    @staticmethod
    def validate_email(email: str) -> bool:
        """Validate email format"""
        import re
        pattern = r'^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$'
        return bool(re.match(pattern, email))
    
    @classmethod
    def from_config(cls, config_path: str):
        """Create instance from config file"""
        with open(config_path, 'r') as f:
            config = json.load(f)
        return cls(config)


def greet(name: str, age: Optional[int] = None) -> str:
    """Greet a user"""
    if age is not None:
        return f"Hello, {name}! You are {age} years old."
    else:
        return f"Hello, {name}!"


# Lambda functions
square = lambda x: x ** 2
add = lambda a, b: a + b

# List comprehension
numbers = [1, 2, 3, 4, 5]
squares = [x ** 2 for x in numbers if x % 2 == 0]
evens = [n for n in range(10) if n % 2 == 0]

# Dictionary comprehension
user_dict = {user.id: user.name for user in users}

# String formatting
name = "John"
age = 30
message1 = f"Hello, {name}! You are {age} years old."
message2 = "Hello, {}! You are {} years old.".format(name, age)
message3 = "Hello, %s! You are %d years old." % (name, age)

# Multi-line strings
multiline = """
This is a
multi-line
string
"""

# Raw strings
raw_string = r"C:\Users\John\Documents\file.txt"
regex_pattern = r"\d{3}-\d{3}-\d{4}"

# Boolean operations
result = True and False or not True
is_valid = value is not None and len(value) > 0

# Numbers
decimal = 42
hex_num = 0xFF
binary = 0b1010
octal = 0o755
float_num = 3.14159
exponential = 1.5e-10

# Exception handling
try:
    result = risky_operation()
except ValueError as e:
    print(f"Error: {e}")
except Exception:
    raise
finally:
    cleanup()

# Context manager
with open('file.txt', 'r') as f:
    content = f.read()

# Decorators
@property
def full_name(self) -> str:
    return f"{self.first_name} {self.last_name}"

@full_name.setter
def full_name(self, value: str):
    self.first_name, self.last_name = value.split()

if __name__ == '__main__':
    print("Running tests...")
    service = UserService({})
    print(service)
