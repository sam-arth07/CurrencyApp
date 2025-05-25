package org.example.currencyapp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform