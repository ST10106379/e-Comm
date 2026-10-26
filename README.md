# e-Comm

A Kotlin Android app that combines an online store with a community and loyalty layer: browse products, check out, post in a community feed, and earn royalty points by RSVPing to and attending events.

<!-- TODO: add the build badge once the workflow exists. Replace USER and REPO. -->
![Build](https://github.com/USER/REPO/actions/workflows/build.yml/badge.svg)

**Demo video:** TODO: add link

## Purpose

e-Comm was built for [TODO: module name] as a fully working Android prototype that was then extended for the final Portfolio of Evidence. It is aimed at [TODO: target users from your Part 1 design document]. The app gives shoppers a place to browse products, talk to other members, and be rewarded for taking part in events.

## Features

| Area | What it does |
|---|---|
| Authentication | Register and log in with email and password, or continue with Google (single sign-on). Passwords are handled and encrypted by Firebase Authentication. |
| Products | Live product list and detail pages from the Fake Store API, with search. |
| Checkout | Simulated payment flow with order summary and validation. No real payments are processed. |
| Community | Posts, likes and comments stored in Cloud Firestore, updating in real time. |
| Events and royalty points | Members RSVP to events and check in to earn royalty points. Points are saved to their account. |
| Offline mode | The product list is cached on the device with Room and shown when there is no internet. It re-syncs automatically when the connection returns. |
| Push notifications | Notifications delivered through Firebase Cloud Messaging. |
| Multi-language | English, isiZulu and Afrikaans, switchable inside the app from Settings. |
| Settings | Change display name, email and password, toggle notifications, choose a language, and log out. |

## Design considerations

- **Visual design:** a dark charcoal background with a brass-gold accent used only for prices and primary actions, serif headings and sans-serif body text. All colours, text styles and button styles are defined once in `colors.xml`, `themes.xml` and `styles.xml` so every screen stays consistent.
- **Components:** Material Components (text fields with floating labels, cards, buttons) rather than plain widgets, so the app behaves the way Android users expect.
- **Architecture:** Activity-based with ViewBinding. Code is organised by type into `model`, `network`, `adapter`, `local` and `util` packages.
- **Data choices:** products come from a REST API, community and event data from Firestore (NoSQL), and a Room database provides the offline cache.
- **Points integrity:** royalty points are awarded inside Firestore transactions, so tapping a button twice, or on two devices, cannot award the same points twice.
- **Internationalisation:** all interface text lives in string resources, and event dates follow the language the user has chosen.
- **Input handling:** TODO: describe the validation you added (email format, password length, card fields) and how invalid input is reported without crashing.
- **Security:** Firestore rules restrict each user to their own points and event status. The release keystore and `google-services.json` are not committed.

## Tech stack

Kotlin, Android Studio, ViewBinding, Material Components, Retrofit and Gson, Coil, Room, Firebase (Authentication, Cloud Firestore, Cloud Messaging), Google Sign-In.

## Project structure

```
app/src/main/java/com/example/e_comm/
├── model/      Data classes (Product, Post, Comment, Event, EventStatus)
├── network/    Retrofit API service and client
├── adapter/    RecyclerView adapters (products, posts, events)
├── local/      Room database, DAO and entity for offline caching
├── util/       NetworkMonitor and LanguageManager helpers
└── (root)      Activities (Login, SignUp, ProductList, ProductDetail,
                Payment, Socials, Events, Settings), ECommApp, messaging service
```

## Getting started

1. Clone the repository and open it in Android Studio.
2. Create a Firebase project, enable Email/Password and Google sign-in, and create a Cloud Firestore database.
3. Download your own `google-services.json` and place it in the `app/` folder.
4. Add your debug SHA-1 fingerprint to the Firebase project (needed for Google sign-in).
5. Add the Firestore rules for `posts`, `events` and `users` in the Firebase console.
6. Sync Gradle and run on a physical device.

## GitHub and GitHub Actions

- **Version control:** the project was built up through regular commits, with the final PoE version marked by the tag `TODO: tag name`. TODO: mention branches or pull requests if you used them.
- **Automated build and tests:** a GitHub Actions workflow in `.github/workflows/TODO.yml` runs on every push. It TODO: describe what yours does, for example sets up the JDK, builds the debug APK and runs the unit tests.
- **Why:** building on GitHub's servers proves the project compiles on a clean machine and not only on mine.

## Testing

TODO: list the unit tests you wrote and what each one checks, for example input validation and the Product to database mapping functions.

## Release notes

### v2.0: Final PoE

New since the prototype (innovative features marked with ★):

- ★ **Royalty points and event RSVPs:** members earn points for RSVPing and more for checking in.
- ★ **Offline mode with automatic sync:** products stay available without internet and refresh when the connection returns.
- **Google single sign-on** alongside email login.
- **Push notifications** with Firebase Cloud Messaging.
- **Multi-language support:** English, isiZulu and Afrikaans.
- **Redesigned interface:** a consistent dark and gold design system across every screen.
- TODO: keep this line only if the feature stays in the app: profile photo upload with Firebase Storage.
- TODO: list the bugs you fixed and any validation you added.

### v1.0: Prototype

Registration and login, product list and detail pages from a REST API, simulated checkout, community feed and settings.

## Known limitations

- Check-in is a button tap and does not verify that the member is actually at the event. A production version would use a QR code or a location check.
- Points are written from the app. A production version would award them from a server-side function so they cannot be edited by a user.
- Checkout is simulated and no real payments are taken.
- TODO: describe the cart behaviour honestly (for example, whether Add to Cart is stored).

## Screenshots

<!-- TODO: add your own screenshots to docs/screenshots/ and keep the paths below in step. -->
| Login | Products | Events | Settings |
|---|---|---|---|
| ![Login](docs/screenshots/login.png) | ![Products](docs/screenshots/products.png) | ![Events](docs/screenshots/events.png) | ![Settings](docs/screenshots/settings.png) |

## AI use

AI tools were used during development and are documented in the separate AI-use write-up submitted with this project. Files drafted with AI assistance carry a comment header.

## Credits

[Fake Store API](https://fakestoreapi.com/), Firebase, Retrofit, Coil, Room and Material Components.
