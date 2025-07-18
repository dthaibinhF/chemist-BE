# Guide for Updating the Bank Memory

This document provides guidelines for updating the bank memory as you make changes to the Chemist-BE project.

## Purpose

The bank memory serves as a living documentation of the project, tracking changes, architectural decisions, and key knowledge over time. Keeping it up to date is essential for maintaining a clear understanding of the project's evolution.

## When to Update the Bank Memory

Update the bank memory in the following situations:

1. **Adding a new entity**: Document the entity's structure, relationships, and purpose
2. **Modifying an existing entity**: Update the entity's documentation to reflect the changes
3. **Adding a new API endpoint**: Document the endpoint's purpose, request/response structure, and any special considerations
4. **Modifying an existing API endpoint**: Update the endpoint's documentation to reflect the changes
5. **Making architectural decisions**: Document the decision, its rationale, and any alternatives considered
6. **Implementing a new feature**: Document the feature's purpose, implementation details, and any special considerations
7. **Fixing a significant bug**: Document the bug, its root cause, and the solution implemented

## How to Update the Bank Memory

### 1. Update the README.md

The README.md file in the bank_memory directory serves as the main entry point for the bank memory. Update it with a brief summary of the changes you've made.

### 2. Update Specific Documentation Files

Depending on the nature of your changes, update the relevant documentation files:

- **entities.md**: For changes to entities
- **apis.md**: For changes to API endpoints
- Create new files for major features or components

### 3. Add to the Change History

Add an entry to the Change History section in README.md with the following format:

```markdown
### [Date: YYYY-MM-DD] Brief Description of Changes

- Detailed point about the change
- Another detailed point about the change
```

### 4. Cross-Reference Related Changes

If your changes span multiple areas (e.g., adding a new entity and corresponding API endpoints), make sure to cross-reference these changes in the relevant documentation files.

## Example Update

Here's an example of how to update the bank memory when adding a new entity:

1. Update entities.md with the new entity's details
2. Update apis.md with the new API endpoints for the entity
3. Add an entry to the Change History section in README.md

```markdown
### [Date: 2025-07-15] Added Course Entity

- Added Course entity to represent courses offered by the school
- Added API endpoints for managing courses
- Established relationships between Course and Teacher entities
```

## Best Practices

1. **Be concise**: Keep documentation clear and to the point
2. **Be consistent**: Follow the established format and style
3. **Include rationale**: Explain why changes were made, not just what was changed
4. **Update promptly**: Update the bank memory as soon as you make changes, while the details are fresh in your mind
5. **Review regularly**: Periodically review the bank memory to ensure it remains accurate and up to date

## Conclusion

By following these guidelines, you'll help maintain a comprehensive and up-to-date bank memory that serves as a valuable resource for all developers working on the Chemist-BE project.