# Markdown Test File

This is a **test file** for *Markdown* syntax highlighting.

## Table of Contents

- [Headers](#headers)
- [Text Formatting](#text-formatting)
- [Lists](#lists)
- [Links and Images](#links-and-images)
- [Code](#code)
- [Tables](#tables)
- [Blockquotes](#blockquotes)

## Headers

# H1 Header
## H2 Header
### H3 Header
#### H4 Header
##### H5 Header
###### H6 Header

## Text Formatting

This is **bold text** and this is __also bold__.

This is *italic text* and this is _also italic_.

This is ***bold and italic*** text.

This is ~~strikethrough~~ text.

This is `inline code` text.

## Lists

### Unordered List

- Item 1
- Item 2
  - Nested item 2.1
  - Nested item 2.2
    - Deeply nested item
- Item 3

### Ordered List

1. First item
2. Second item
   1. Nested item 2.1
   2. Nested item 2.2
3. Third item

### Task List

- [x] Completed task
- [ ] Incomplete task
- [ ] Another task

## Links and Images

[Link to Google](https://google.com)

[Link with title](https://github.com "GitHub Homepage")

![Alt text for image](https://via.placeholder.com/150)

Reference-style link: [GitHub][1]

[1]: https://github.com

## Code

### Inline Code

Use `const variable = 'value';` for declaring variables.

### Code Blocks

```javascript
// JavaScript code
function greet(name) {
  console.log(`Hello, ${name}!`);
  return true;
}

const user = {
  id: 1,
  name: 'John Doe',
  active: true
};
```

```python
# Python code
def greet(name: str) -> str:
    return f"Hello, {name}!"

class User:
    def __init__(self, name):
        self.name = name
```

```typescript
// TypeScript code
interface User {
  id: number;
  name: string;
  email?: string;
}

const greet = (user: User): string => {
  return `Hello, ${user.name}!`;
};
```

```json
{
  "name": "Test",
  "version": "1.0.0",
  "active": true,
  "count": 42
}
```

## Tables

| Header 1 | Header 2 | Header 3 |
|----------|----------|----------|
| Cell 1   | Cell 2   | Cell 3   |
| Cell 4   | Cell 5   | Cell 6   |
| Cell 7   | Cell 8   | Cell 9   |

Aligned table:

| Left | Center | Right |
|:-----|:------:|------:|
| L1   | C1     | R1    |
| L2   | C2     | R2    |

## Blockquotes

> This is a blockquote.
> It can span multiple lines.

> Nested blockquote:
>> This is nested

> **Note:** You can use *formatting* inside blockquotes.

## Horizontal Rules

---

***

___

## Footnotes

Here's a sentence with a footnote.[^1]

[^1]: This is the footnote content.

## Definition Lists

Term 1
: Definition 1a
: Definition 1b

Term 2
: Definition 2

## Emoji

:smile: :heart: :thumbsup: :rocket:

## HTML in Markdown

You can also use <strong>HTML tags</strong> in Markdown.

<div style="color: #7aa2f7;">
  This is colored text using HTML.
</div>

## Math (if supported)

Inline math: $E = mc^2$

Block math:

$$
\int_{-\infty}^{\infty} e^{-x^2} dx = \sqrt{\pi}
$$

## Special Characters

Escaping special characters: \* \_ \[ \] \( \) \# \+ \- \. \!

---

**End of Markdown Test File**
