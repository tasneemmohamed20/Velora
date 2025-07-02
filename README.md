# Velora - Modern E-commerce Android App

<p align="center">
  <img src="app/src/main/res/drawable/logo_name.png" alt="Velora Logo" width="120" height="120"/>
</p>

<p align="center">
  <strong>Your trusted e-commerce companion for seamless shopping experiences</strong>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android-green.svg" alt="Platform">
  <img src="https://img.shields.io/badge/Language-Kotlin-blue.svg" alt="Language">
  <img src="https://img.shields.io/badge/Architecture-Clean%20Architecture-orange.svg" alt="Architecture">
  <img src="https://img.shields.io/badge/UI-Jetpack%20Compose-purple.svg" alt="UI">
  <img src="https://img.shields.io/badge/Version-1.0-red.svg" alt="Version">
</p>

## 📱 About Velora

Velora is a modern, feature-rich e-commerce Android application built with cutting-edge technologies. Designed to provide users with a seamless shopping experience, Velora offers everything from product discovery to secure payment processing, all wrapped in a beautiful and intuitive user interface.

## ✨ Features & Highlights

### 🛍️ Core Shopping Features
- **Product Catalog**: Browse through a comprehensive collection of products with detailed information
- **Advanced Search**: Find products quickly with intelligent search and filtering capabilities
- **Shopping Cart**: Add, remove, and manage items in your cart with real-time updates
- **Wishlist/Favorites**: Save products for later purchase and easy access
- **Order Management**: Track your orders from placement to delivery

### 🔐 User Authentication
- **Multiple Login Options**: Email/password, Google Sign-In, and guest mode
- **Secure Registration**: Create accounts with email verification
- **Profile Management**: Update personal information and preferences

### 💳 Payment & Checkout
- **Secure Payments**: Integrated with Paymob payment gateway
- **Multiple Payment Methods**: Online card payments and cash on delivery
- **Order Confirmation**: Real-time order processing and confirmation

### 📍 Location Services
- **Address Management**: Add, edit, and manage multiple delivery addresses
- **Google Maps Integration**: Interactive maps for precise location selection
- **Location Search**: Find and select addresses with autocomplete

### 🎨 User Experience
- **Modern UI/UX**: Built with Jetpack Compose for smooth, native performance
- **Dark/Light Theme**: Adaptive theming for comfortable viewing
- **Onboarding**: Intuitive first-time user experience
- **Offline Support**: Core features available without internet connection

## 🛠️ Technologies Used

### Core Technologies
- **[Kotlin](https://kotlinlang.org/)** - Primary programming language, providing modern syntax and null safety
- **[Android SDK](https://developer.android.com/)** - Target SDK 35, Minimum SDK 24 (Android 7.0+)
- **[Jetpack Compose](https://developer.android.com/jetpack/compose)** - Modern Android UI toolkit for native interfaces

### Architecture & Design Patterns
- **[Clean Architecture](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)** - Separation of concerns with clear boundaries
- **MVVM Pattern** - Model-View-ViewModel architecture for reactive UI
- **Repository Pattern** - Abstraction layer for data access
- **Dependency Injection** - Hilt for managing dependencies

### Networking & Data
- **[Apollo GraphQL](https://www.apollographql.com/docs/android/)** - Type-safe GraphQL client for API communication
- **[Retrofit](https://square.github.io/retrofit/)** - REST API client for additional services
- **[Kotlin Serialization](https://kotlinlang.org/docs/serialization.html)** - JSON serialization and deserialization
- **[OkHttp](https://square.github.io/okhttp/)** - HTTP client for networking

### UI & Navigation
- **[Navigation Compose](https://developer.android.com/jetpack/compose/navigation)** - Type-safe navigation between screens
- **[Material Design 3](https://m3.material.io/)** - Modern design system implementation
- **[Coil](https://coil-kt.github.io/coil/compose/)** - Image loading library for Compose
- **[Lottie](https://airbnb.design/lottie/)** - Animations and micro-interactions

### Dependency Injection & Background Tasks
- **[Hilt](https://dagger.dev/hilt/)** - Dependency injection framework
- **[WorkManager](https://developer.android.com/topic/libraries/architecture/workmanager)** - Background task management
- **[Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)** - Asynchronous programming

### External Integrations
- **[Firebase](https://firebase.google.com/)** - Analytics, authentication, and backend services
- **[Google Maps](https://developers.google.com/maps/documentation/android-sdk)** - Location services and mapping
- **[Google Places API](https://developers.google.com/maps/documentation/places/android-sdk)** - Location search and autocomplete
- **[Paymob SDK](https://docs.paymob.com/)** - Payment processing integration
- **[Google Sign-In](https://developers.google.com/identity/sign-in/android)** - OAuth authentication

### Testing
- **[JUnit](https://junit.org/junit4/)** - Unit testing framework
- **[MockK](https://mockk.io/)** - Mocking library for Kotlin
- **[Turbine](https://github.com/cashapp/turbine)** - Testing utilities for Flow
- **[Espresso](https://developer.android.com/training/testing/espresso)** - UI testing framework

## 🚀 Getting Started

### Prerequisites

Before running the project, ensure you have the following installed:

- **Android Studio** (Arctic Fox or newer)
- **JDK 11** or higher
- **Android SDK** with API level 35
- **Git** for version control

### Installation Steps

1. **Clone the repository**
   ```bash
   git clone https://github.com/tasneemmohamed20/Velora.git
   cd Velora
   ```

2. **Set up API keys**
   Create an `api_key.properties` file in the root directory with the following keys:
   ```properties
   # GraphQL Endpoints
   ADMIN_URL=your_admin_graphql_url
   STOREFRONT_URL=your_storefront_graphql_url
   ADMIN_ACCESS_TOKEN=your_admin_access_token
   STOREFRONT_ACCESS_TOKEN=your_storefront_access_token
   
   # Payment Integration
   PUBLIC_KEY=your_paymob_public_key
   SECRET_KEY=your_paymob_secret_key
   API_KEY=your_paymob_api_key
   BASE_URL=your_paymob_base_url
   ONLINE_CARD_PAYMENT_METHOD_ID=your_payment_method_id
   
   # Google Services
   WEB_CLIENT_ID=your_google_web_client_id
   MAPS_API_KEY=your_google_maps_api_key
   
   # Currency Exchange
   CURRENCY_KEY=your_currency_exchange_api_key
   ```

3. **Configure Firebase**
   - Create a new Firebase project
   - Add your Android app to the project
   - Download `google-services.json` and place it in the `app/` directory

4. **Open in Android Studio**
   - Launch Android Studio
   - Select "Open an existing Android Studio project"
   - Navigate to the cloned directory and select it

5. **Sync and Build**
   ```bash
   ./gradlew clean
   ./gradlew build
   ```

6. **Run the app**
   - Connect an Android device or start an emulator
   - Click the "Run" button in Android Studio or use:
   ```bash
   ./gradlew installDebug
   ```

## 📖 Usage Instructions

### For Users

1. **First Launch**
   - Complete the onboarding process
   - Choose between creating an account, signing in with Google, or continuing as a guest

2. **Shopping**
   - Browse products by category or use the search function
   - Tap on products to view detailed information
   - Add items to your cart or favorites
   - Use filters to narrow down product searches

3. **Checkout Process**
   - Review items in your cart
   - Add or select a delivery address
   - Choose your preferred payment method
   - Confirm your order

4. **Account Management**
   - Access your profile from the account section
   - View order history and track current orders
   - Manage saved addresses
   - Update personal information

### For Developers

1. **Project Structure**
   ```
   app/src/main/java/com/example/m_commerce/
   ├── data/           # Data layer (repositories, data sources)
   ├── domain/         # Domain layer (entities, use cases)
   ├── presentation/   # Presentation layer (UI, ViewModels)
   └── di/            # Dependency injection modules
   ```

2. **Adding New Features**
   - Follow the existing Clean Architecture pattern
   - Create data sources in the `data` package
   - Define entities and repositories in the `domain` package
   - Implement UI and ViewModels in the `presentation` package

3. **Testing**
   ```bash
   # Run unit tests
   ./gradlew test
   
   # Run instrumented tests
   ./gradlew connectedAndroidTest
   ```

## 🤝 Contributing Guidelines

We welcome contributions to make Velora even better! Please follow these guidelines:

### Getting Started
1. Fork the repository
2. Create a feature branch: `git checkout -b feature/amazing-feature`
3. Make your changes following our coding standards

### Coding Standards
- **Kotlin Style**: Follow [Kotlin coding conventions](https://kotlinlang.org/docs/coding-conventions.html)
- **Architecture**: Maintain Clean Architecture principles
- **Testing**: Write unit tests for new features
- **Documentation**: Document public APIs and complex logic

### Commit Guidelines
- Use conventional commit messages: `feat:`, `fix:`, `docs:`, `test:`, etc.
- Keep commits atomic and focused
- Write clear, descriptive commit messages

### Pull Request Process
1. Update documentation if needed
2. Add tests for new functionality
3. Ensure all tests pass
4. Update the README if you've added features
5. Submit a pull request with a clear description

### Code Review
- All submissions require review before merging
- Address feedback promptly and professionally
- Maintain backward compatibility when possible

### Reporting Issues
- Use GitHub Issues to report bugs or request features
- Provide detailed reproduction steps for bugs
- Include device information and Android version when relevant

## 📄 License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

```
MIT License

Copyright (c) 2025 Velora Team

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

---

<p align="center">
  Made with ❤️ by the Velora Team<br>
  <strong>© 2025 Velora. All rights reserved.</strong>
</p>