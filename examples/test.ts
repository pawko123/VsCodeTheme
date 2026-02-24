// TypeScript Test File
import { Request, Response } from 'express';
import * as fs from 'fs';

/**
 * User interface with various properties
 */
interface User {
  id: number;
  name: string;
  email?: string;
  active: boolean;
  roles: Role[];
}

type Role = 'admin' | 'user' | 'guest';

enum Status {
  Active = 1,
  Inactive = 0,
  Pending = 2
}

// Decorator example
function Logger(target: any, propertyName: string, descriptor: PropertyDescriptor) {
  console.log(`Calling ${propertyName}`);
}

class UserService {
  private users: User[] = [];
  readonly maxUsers: number = 100;
  
  constructor(private config: Config) {
    this.initialize();
  }
  
  @Logger
  async findUser(id: number): Promise<User | null> {
    const user = this.users.find(u => u.id === id);
    return user ?? null;
  }
  
  createUser(name: string, email: string): User {
    const newUser: User = {
      id: Math.random(),
      name,
      email,
      active: true,
      roles: ['user']
    };
    
    this.users.push(newUser);
    return newUser;
  }
  
  private initialize(): void {
    console.log('Initializing UserService...');
  }
}

// Arrow functions and template literals
const greet = (name: string, age?: number): string => {
  return `Hello, ${name}! You are ${age ?? 'unknown'} years old.`;
};

// Destructuring
const { id, name, ...rest } = someUser;
const [first, second, ...others] = numbers;

// Regex
const emailPattern = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
const isValid = emailPattern.test('test@example.com');

// Conditional and operators
const result = condition ? 'yes' : 'no';
const combined = value1 && value2 || value3;
const nullish = value ?? 'default';

// Numbers
const decimal = 42;
const hex = 0xFF;
const binary = 0b1010;
const float = 3.14159;
const exponential = 1.5e-10;

// Export
export { UserService, User, Role, Status };
export default UserService;
