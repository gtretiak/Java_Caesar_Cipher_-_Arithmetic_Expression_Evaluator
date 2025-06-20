# Caesar Cipher & Arithmetic Expression Evaluator

A dual-purpose Java console application for text encryption/decryption and mathematical expression evaluation.

## Overview

This application provides two essential functionalities commonly used in programming assessments and practical scenarios. The Caesar Cipher component offers secure text transformation capabilities supporting multiple languages, while the Expression Evaluator provides robust mathematical computation with proper order of operations handling.

## Compilation and Execution

```bash
# Compile
javac MyProgram.java

# Run
java MyProgram
```

### Practical Use Cases

**Caesar Cipher Applications:**
- Educational cryptography demonstrations
- Simple text obfuscation for configuration files
- Basic data encoding for non-sensitive information
- Learning tool for understanding encryption fundamentals
- Puzzle and game development requiring text transformation

**Expression Evaluator Applications:**
- Calculator functionality in larger applications
- Configuration file processing with dynamic calculations
- Spreadsheet-like formula evaluation
- Educational tool for teaching mathematical precedence
- Embedded computation engine for business logic

## Features

### Part 1: Caesar Cipher Implementation

**Core Functionality:**
- Bidirectional encryption/decryption using Caesar cipher algorithm
- Multi-language support (Russian Cyrillic and English Latin alphabets)
- Multiple input methods (console input and file reading)
- Intelligent decryption without shift value (brute force approach)

**Technical Capabilities:**
- Case preservation during transformation
- Non-alphabetic character preservation (spaces, punctuation, numbers)
- Proper alphabet wrap-around handling
- Support for positive and negative shift values
- Robust character encoding handling for Cyrillic text

**Input/Output Examples:**
```
Encryption:
Input: "Hello World", shift: 3
Output: "Khoor Zruog"

Input: "Привет Мир", shift: 5  
Output: "Хумёзй Рну"

Decryption:
Input: "Khoor Zruog", shift: 3
Output: "Hello World"
```

### Part 2: Arithmetic Expression Evaluator

**Mathematical Operations:**
- Basic arithmetic: addition (+), subtraction (-), multiplication (*), division (/)
- Parentheses support for complex expressions
- Proper order of operations (PEMDAS/BODMAS)
- Nested parentheses handling
- Negative number support

**Advanced Features:**
- Decimal number processing
- Division by zero protection
- Expression parsing and validation
- Error handling for malformed expressions

**Input/Output Examples:**
```
Input: "2 + 3 * 4"
Output: 14

Input: "(10 + 5) / 3"
Output: 5

Input: "2 * (3 + 4) - 1"
Output: 13

Input: "-5 + 3"
Output: -2
```

## Technical Features

- **Application Type:** Console-based interactive application
- **Architecture:** Modular design with separate classes for each functionality
- **Error Handling:** Comprehensive exception handling for invalid inputs
- **User Interface:** Menu-driven console interface

## Application Flow

```
Please choose an option:
1. Caesar Cipher Encryption
2. Caesar Cipher Decryption  
3. Arithmetic Expression Evaluation
4. Exit

Enter your choice: 1
Enter text to encrypt: Hello World
Enter shift value: 3
Result: Khoor Zruog

Continue? (y/n):
```

## Implementation Guidelines

**Code Organization:**
- Separate classes for Caesar Cipher and Expression Evaluator
- Utility classes for common operations
- Main application class for user interface management
- Proper separation of concerns and single responsibility principle

**Quality Assurance:**
- Input validation and sanitization
- Comprehensive error messages
- Edge case handling
- Clean, readable, and maintainable code structure

## Assumptions

1. **Alphabet Definitions**:
   - Russian alphabet contains 33 characters (including Ёё), whereas English - 26
   - This affects shift calculations and brute-force decryption

2. **Numerical Handling**:
   - Decimal numbers use '.' as decimal separator
   - Numbers contain maximum one decimal point

3. **Input Processing**:
   - Mixed-language text will be processed with both alphabets recognized
   - Empty inputs are rejected with clear error messages

4. **Algorithmic Choices**:
   - Brute-force decryption shows all possible shifts (with omiting non-target language for clarity)
