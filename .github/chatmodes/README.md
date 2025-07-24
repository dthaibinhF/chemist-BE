# Cursor AI Modes & General Workflow

## General Working Flow

```
                     ┌─────────────────┐
                     │  New Task/      │
                     │   Request       │
                     └─────────┬───────┘
                               │
                      ┌────────▼────────┐
                      │   Task Scope?   │
                      └─────────────────┘
                               │
                ┌──────────────┴──────────────┐
                │                             │
                ▼                             ▼
      ┌─────────────────┐            ┌──────────────────┐
      │ 🚀 Small Changes│            │ 🏗️ Large Features│
      │  (Clear Scope)  │            │  (Complex Mods)  │
      └─────────┬───────┘            └─────────┬────────┘
                │                              │
                ▼                              ▼
      ┌─────────────────┐            ┌─────────────────┐
      │  Choose Mode:   │            │ 1️⃣ Exploration  │
      │ • Vibe Coding   │            │ (Teach Mode)    │
      │ • Refactor      │            │   [optional]    │
      │ • Bug Assassin  │            └─────────┬───────┘
      └─────────┬───────┘                      │
                │                              ▼
                ▼                    ┌─────────────────┐
      ┌─────────────────┐            │ 2️⃣ Solution     │
      │ ✅ Implement &  │            │ (Architect Mode)│
      │   Complete      │            │ Design & Discuss│
      └─────────────────┘            └─────────┬───────┘
                                               │
                                               ▼
                                     ┌─────────────────┐
                                     │ 3️⃣ Planning     │
                                     │ (Task List)     │
                                     │ Implementation  │
                                     │     Plan        │
                                     └─────────┬───────┘
                                               │
                                               ▼
                                     ┌─────────────────┐
                                     │ 4️⃣ Implementation│
                                     │ (Code/Refactor/ │
                                     │  Bug Modes)     │
                                     └─────────┬───────┘
                                               │
                                               ▼
                                     ┌─────────────────┐
                                     │ ✅ Complete     │
                                     │   Feature       │
                                     └─────────────────┘
```

### 🚀 Small Changes Examples:
- Bug fixes, validation errors
- Component styling updates  
- Adding props to existing components
- Minor refactors with clear scope

### 🏗️ Large Features Examples:
- New feature development
- System architecture changes
- Complex integrations
- Multi-component workflows

---

## Cursor AI Mode Quick Guide:

1.  **Architect Mode:**
    *   **When to use:** Before writing any code for a new feature or system. Use it to thoroughly analyze requirements, explore different design options, define components, and create a technical plan.
    *   **Goal:** Design a robust, scalable solution *conceptually* to prevent costly mistakes later.

2.  **Teach Mode:**
    *   **When to use:** When you want to understand a coding concept, a piece of code, a new language feature, a design pattern, or the "why" behind a bug (separate from fixing it).
    *   **Goal:** Deepen your understanding and learn underlying principles.

3.  **Vibe Coding Assistant Mode:**
    *   **When to use:** For building new applications or features iteratively, focusing on the desired look, feel, and user experience ("the vibe"). You guide with high-level ideas and visual references.
    *   **Goal:** Collaboratively create functional and aesthetically pleasing applications, with the AI handling most code generation while matching existing codebase styles.

4.  **Expert Refactoring Assistant Mode:**
    *   **When to use:** When you have existing code that works but needs improvement in structure, readability, or maintainability without changing its functionality.
    *   **Goal:** Make existing code cleaner, more efficient, and easier to work with.

5.  **Bug Assassin Mode:**
    *   **When to use:** When you've encountered a bug and need to find its root cause and get it fixed correctly.
    *   **Goal:** Systematically diagnose the underlying issue, implement a robust fix directly in the code, and potentially capture lessons learned.
