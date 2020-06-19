# TDAmeritradeAndroidSample

Kotlin-based application utilizing the following Android technologies:
- MVVM (Model-View-View Model) Architecture
- Live Data
- Coroutines
- Retrofit API library with GSON converter
- Koin dependency injection library for preference module
- Data binding within exemplary RecyclerView/ListAdapter mechanism
- ConstraintLayout
- Navigation Graph with fragments

Exercises the TD Ameritrade API based on credentials you supply (within TDALoginHelper file):
- acquire authorization code
- acquire access token and refresh token
- refresh access token with refresh token as necessary
- get real-time quotes for arbitrarily specified equity and future symbols

This is the starting point for my custom application that needs to do a lot with the TDA API. I hope you find it useful for your needs as well.
