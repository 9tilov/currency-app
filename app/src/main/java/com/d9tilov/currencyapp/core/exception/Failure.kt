package com.d9tilov.currencyapp.core.exception

sealed class Failure {
    object NetworkConnection : Failure()
    object ServerError : Failure()
    object WrongEmail : Failure()
    object WrongPassword : Failure()

    /** * Extend this class for feature specific failures.*/
    abstract class FeatureFailure : Failure()
}
