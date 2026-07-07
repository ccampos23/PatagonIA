package com.patagonia.app.domain.repository

/**
 * Thrown when a Supabase auth operation fails (invalid credentials, duplicate email,
 * network error during sign-in, etc.).
 *
 * The presentation layer maps this into inline error messages on the auth screens (D-24).
 */
class AuthException(message: String, cause: Throwable? = null) : Exception(message, cause)
