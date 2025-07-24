# Git Commit Guidelines

This document outlines the process for creating Git commits that align with community best practices, ensuring clear, consistent, and maintainable version control history.

## Steps to Create a Commit

1. **Prepare Your Changes**
    - Stage files you want to commit:
      ```bash
      git add <file1> <file2> ...
      ```
      or stage all modified files:
      ```bash
      git add .
      ```
    - Verify staged changes:
      ```bash
      git status
      ```

2. **Write a Commit Message**
    - Follow the [Conventional Commits](https://www.conventionalcommits.org/) format:
      ```
      <type>(<scope>): <short description>
      <BLANK LINE>
      <optional detailed description>
      <BLANK LINE>
      <optional footer with references>
      ```
    - **Type**: Choose one based on the change:
        - `feat`: New feature
        - `fix`: Bug fix
        - `docs`: Documentation changes
        - `style`: Code style changes (e.g., formatting)
        - `refactor`: Code refactoring
        - `test`: Adding or updating tests
        - `chore`: Maintenance tasks (e.g., dependency updates)
        - `perf`: Performance improvements
        - `ci`: CI/CD configuration changes
        - `build`: Build system changes
    - **Scope**: (Optional) Specify the affected module or area (e.g., `api`, `ui`, `database`).
    - **Short Description**: Summarize the change in 50 characters or less, using imperative mood (e.g., "Add user login endpoint").
    - **Detailed Description**: (Optional) Explain the change’s context or motivation. Keep lines under 72 characters.
    - **Footer**: (Optional) Reference issues or pull requests (e.g., `Closes #123`).

   Example:
   ```bash
   git commit -m "feat(auth): add user login endpoint

   Implement JWT-based authentication for user login.
   Includes input validation and error handling.
   Closes #123"
   ```

3. **Follow Best Practices**
    - **Atomic Commits**: Each commit should represent one logical change.
    - **Clear Messages**: Avoid vague terms like "update" or "fix stuff."
    - **Imperative Mood**: Write as if giving a command (e.g., "Add feature" not "Added feature").
    - **Reference Issues**: Include issue numbers if applicable (e.g., `Closes #456`).
    - **Test Changes**: Ensure your changes pass tests and don’t break the build.
    - **Sign Commits**: If required, use:
      ```bash
      git commit -S -m "your commit message"
      ```

4. **Verify and Push**
    - Review your commit:
      ```bash
      git log --oneline
      ```
    - Amend if needed (e.g., to fix a message or add files):
      ```bash
      git add <forgotten-file>
      git commit --amend
      ```
    - Push to the remote repository:
      ```bash
      git push origin <branch-name>
      ```

## Example Workflow
For a new user registration endpoint:
```bash
git add src/api/register.js
git commit -m "feat(api): add user registration endpoint

Add POST /register endpoint with email and password validation.
Include unit tests for validation logic.
Closes #456"
git push origin main
```

## Additional Notes
- **Project-Specific Rules**: Check the project’s `CONTRIBUTING.md` for additional requirements, such as specific scopes or sign-off lines (e.g., `Signed-off-by: Your Name <email>`).
- **Tools**: Use tools like [Commitizen](https://commitizen-tools.github.io/commitizen/) for easier Conventional Commits.
- **Squashing Commits**: For pull requests, you may need to squash commits for a cleaner history.