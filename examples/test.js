// JavaScript Test File
// Testing syntax highlighting for JavaScript

'use strict';

// Imports
import { User, Role } from './models';
import * as utils from './utils';
const express = require('express');

/**
 * User Service class
 * Manages user operations
 */
class UserService {
  constructor(config) {
    this.config = config;
    this.users = [];
    this._initialized = false;
  }
  
  // Getters and setters
  get userCount() {
    return this.users.length;
  }
  
  set maxUsers(value) {
    this._maxUsers = value;
  }
  
  // Methods
  async findUser(id) {
    const user = this.users.find(u => u.id === id);
    return user ?? null;
  }
  
  createUser(name, email) {
    const newUser = {
      id: Math.random(),
      name,
      email,
      active: true,
      roles: ['user'],
      createdAt: new Date()
    };
    
    this.users.push(newUser);
    return newUser;
  }
  
  static validateEmail(email) {
    const pattern = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
    return pattern.test(email);
  }
}

// Arrow functions
const greet = (name, age = null) => {
  if (age !== null) {
    return `Hello, ${name}! You are ${age} years old.`;
  }
  return `Hello, ${name}!`;
};

// Regular function
function calculate(a, b, operation = 'add') {
  switch(operation) {
    case 'add':
      return a + b;
    case 'subtract':
      return a - b;
    case 'multiply':
      return a * b;
    case 'divide':
      return a / b;
    default:
      throw new Error('Invalid operation');
  }
}

// Variables
const constant = 42;
let variable = 'Hello';
var oldStyle = true;

// Destructuring
const { id, name, ...rest } = someUser;
const [first, second, ...others] = numbers;

// Object literals
const user = {
  id: 1,
  name: 'John Doe',
  email: 'john@example.com',
  // Method shorthand
  greet() {
    return `Hello, ${this.name}!`;
  },
  // Computed property
  ['role_' + 'admin']: true
};

// Array methods
const numbers = [1, 2, 3, 4, 5];
const doubled = numbers.map(n => n * 2);
const evens = numbers.filter(n => n % 2 === 0);
const sum = numbers.reduce((acc, n) => acc + n, 0);
const hasEven = numbers.some(n => n % 2 === 0);
const allPositive = numbers.every(n => n > 0);

// Template literals
const name = 'World';
const message = `Hello, ${name}!`;
const multiline = `
  This is a
  multi-line
  string
`;

// Tagged template
function highlight(strings, ...values) {
  return strings.reduce((result, str, i) => {
    return result + str + (values[i] || '');
  }, '');
}

const highlighted = highlight`Value: ${value}`;

// Promises
async function fetchData(url) {
  try {
    const response = await fetch(url);
    const data = await response.json();
    return data;
  } catch (error) {
    console.error('Error fetching data:', error);
    throw error;
  }
}

// Promise chains
fetchData('/api/users')
  .then(users => users.filter(u => u.active))
  .then(activeUsers => console.log(activeUsers))
  .catch(error => console.error(error))
  .finally(() => console.log('Done'));

// Promise utilities
Promise.all([promise1, promise2, promise3])
  .then(results => console.log(results));

Promise.race([promise1, promise2])
  .then(winner => console.log(winner));

// Regular expressions
const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
const phoneRegex = /^\d{3}-\d{3}-\d{4}$/;
const urlRegex = /https?:\/\/(www\.)?[-a-zA-Z0-9@:%._\+~#=]{1,256}\.[a-zA-Z0-9()]{1,6}\b/;

// Operators
const addition = a + b;
const subtraction = a - b;
const multiplication = a * b;
const division = a / b;
const modulo = a % b;
const exponentiation = a ** b;

const equal = a === b;
const notEqual = a !== b;
const greater = a > b;
const less = a < b;
const greaterOrEqual = a >= b;
const lessOrEqual = a <= b;

const and = a && b;
const or = a || b;
const not = !a;
const nullish = a ?? b;

const ternary = condition ? 'yes' : 'no';

// Numbers
const decimal = 42;
const hex = 0xFF;
const binary = 0b1010;
const octal = 0o755;
const float = 3.14159;
const exponential = 1.5e-10;
const infinity = Infinity;
const nan = NaN;

// Symbols
const sym1 = Symbol('description');
const sym2 = Symbol.for('global');

// WeakMap and WeakSet
const weakMap = new WeakMap();
const weakSet = new WeakSet();

// Generators
function* generateNumbers() {
  yield 1;
  yield 2;
  yield 3;
}

const gen = generateNumbers();
console.log(gen.next().value); // 1

// Async generators
async function* asyncGenerator() {
  yield await Promise.resolve(1);
  yield await Promise.resolve(2);
}

// Proxy
const handler = {
  get(target, prop) {
    return prop in target ? target[prop] : 'default';
  }
};
const proxy = new Proxy({}, handler);

// Reflect
Reflect.set(obj, 'key', 'value');
Reflect.get(obj, 'key');

// Error handling
try {
  riskyOperation();
} catch (error) {
  console.error(error);
  throw error;
} finally {
  cleanup();
}

// Classes with private fields
class BankAccount {
  #balance = 0;
  
  constructor(initialBalance) {
    this.#balance = initialBalance;
  }
  
  deposit(amount) {
    this.#balance += amount;
  }
  
  getBalance() {
    return this.#balance;
  }
}

// Export
export { UserService, greet, calculate };
export default UserService;
