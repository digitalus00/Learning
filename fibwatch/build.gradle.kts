// use an integer for version numbers
version = 1

cloudstream {
    // All of these properties are optional, you can safely remove any of them.

    language = "hi"
    authors = listOf("digitalus")

    /**
     * Status int as one of the following:
     * 0: Down
     * 1: Ok
     * 2: Slow
     * 3: Beta-only
     **/
    status = 1 // Will be 3 if unspecified

    tvTypes = listOf(
        "Movie",
        "TvSeries"

    )
    iconUrl = "https://fibwatch.art/themes/default/img/logo-light.png?cache=123"

    isCrossPlatform = true
}
