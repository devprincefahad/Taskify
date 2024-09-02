package dev.prince.taskify.signin

data class SignInState(
    val isSignInSuccessful: Boolean = false,
    val signInError: String? = null
)