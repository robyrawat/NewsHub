# Contributing to NewsHub 🤝

First off, thank you for considering contributing to NewsHub! It's people like you that make NewsHub such a great tool for the Android community.

## 🌟 Ways to Contribute

### 🐛 Reporting Bugs
- Use the GitHub Issues tab to report bugs
- Check if the bug hasn't already been reported
- Include detailed reproduction steps
- Provide device information and Android version

### ✨ Suggesting Features
- Open a feature request in GitHub Issues
- Explain the feature and its benefits
- Consider the impact on existing users

### 💻 Code Contributions
- Fork the repository
- Create a feature branch
- Make your changes
- Submit a pull request

## 🛠️ Development Setup

### Prerequisites
- Android Studio Hedgehog or newer
- JDK 17+
- Git knowledge
- Kotlin experience

### Setting Up
1. Fork and clone the repository
2. Open in Android Studio
3. Get NewsData.io API key
4. Add API key to Constants.kt
5. Build and test

## 📝 Code Style Guidelines

### Kotlin Code
- Follow official Kotlin coding conventions
- Use meaningful variable and function names
- Add KDoc comments for public APIs
- Keep functions small and focused

### Compose UI
- Use descriptive composable names
- Prefer stateless composables
- Handle state properly with remember and State
- Use Material Design 3 components

### Architecture
- Follow MVVM pattern
- Use Repository pattern for data
- Implement proper error handling
- Write unit tests for business logic

## 🧪 Testing

### Before Submitting
- Test on multiple device sizes
- Verify pull-to-refresh works
- Check offline functionality
- Test error scenarios
- Ensure no crashes

### Test Categories
- Unit tests for ViewModels
- Integration tests for Repository
- UI tests for key flows
- Performance tests

## 📋 Pull Request Process

1. **Create Feature Branch**
   ```bash
   git checkout -b feature/awesome-feature
   ```

2. **Make Changes**
   - Follow code style guidelines
   - Add tests if applicable
   - Update documentation

3. **Commit Changes**
   ```bash
   git commit -m "feat: add awesome feature"
   ```
   Use conventional commit format:
   - `feat:` for new features
   - `fix:` for bug fixes
   - `docs:` for documentation
   - `refactor:` for code refactoring

4. **Push and Create PR**
   ```bash
   git push origin feature/awesome-feature
   ```

5. **PR Requirements**
   - Clear title and description
   - Link related issues
   - Include screenshots for UI changes
   - Ensure CI passes

## 🔍 Code Review Process

- Maintainers will review within 48 hours
- Address feedback promptly
- Be open to suggestions
- Squash commits before merging

## 🌍 Translation Contributions

Help make NewsHub accessible globally:
- Add new language support
- Improve existing translations
- Test RTL languages
- Consider cultural contexts

## 📚 Documentation

- Update README for new features
- Add code comments
- Create wiki pages for complex features
- Include examples

## 🆘 Getting Help

- Ask questions in Discussions
- Join our community chat
- Check existing documentation
- Look at similar implementations

## 🏆 Recognition

Contributors will be:
- Listed in README
- Mentioned in release notes
- Invited to maintainer team (for regular contributors)
- Given special contributor badge

## 📞 Contact

- GitHub Issues for bugs/features
- Discussions for questions
- Email for sensitive matters

Thank you for contributing to NewsHub! 🎉
