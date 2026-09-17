# e-Comm

An Android e-commerce application built with Kotlin, developed as a school assignment. The app combines product browsing, a simulated checkout flow, and a social feed, backed by a REST API and Firebase.

## Features

- **Authentication** — sign up and log in via Firebase Authentication (email/password)
- **Product browsing** — live product data pulled from the [Fake Store API](https://fakestoreapi.com/), with search/filter
- **Product detail** — full description, quantity selector, add to cart, and buy now
- **Checkout** — simulated payment flow with order summary and shipping details (no real payment processing)
- **Community feed** — posts, likes, and comments backed by Cloud Firestore, updating in real time
- **Settings** — update display name, email, and password; toggle notification preference; log out

## Tech Stack

- **Language:** Kotlin
- **Min SDK / Target SDK:** *(fill in your project's values)*
- **Architecture:** Activity-based, ViewBinding
- **Networking:** Retrofit + Gson (Fake Store API)
- **Auth & Database:** Firebase Authentication, Cloud Firestore
- **Image loading:** Coil
- **UI:** Material Components (Material3 theme, custom dark/gold design system)

## Project Structure

```
app/src/main/java/com/example/e_comm/
├── model/       # Data classes (Product, Post, Comment, Rating)
├── network/     # Retrofit API service and client
├── adapter/     # RecyclerView adapters (products, feed posts)
└── ui/          # Activities (SignUp, Login, ProductList, ProductDetail, Payment, Socials, Settings)
```

## Setup

1. Clone the repository
2. Open in Android Studio
3. Add your own `google-services.json` file (from the Firebase console) to the `app/` directory to enable Authentication and Firestore
4. Sync Gradle and run on an emulator or device

## Notes

- The payment flow is simulated for demonstration purposes only — no real card details are transmitted or stored.
- Product data is sourced from the Fake Store API and is for demo purposes; it does not reflect real inventory.
