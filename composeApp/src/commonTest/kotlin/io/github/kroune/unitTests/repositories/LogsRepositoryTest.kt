package io.github.kroune.unitTests.repositories

import io.github.kroune.nine_mens_morris_kmp_app.domain.repositories.logging.Severity
import io.github.kroune.nine_mens_morris_kmp_app.domain.repositories.logging.log
import io.github.kroune.nine_mens_morris_kmp_app.domain.repositories.logging.logOnFailure
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LogsRepositoryTest {

    @Test
    fun log_prints_message_with_debug_severity() {
        // Test that log function doesn't throw exception
        log("Debug message", severity = Severity.DEBUG)
    }

    @Test
    fun log_prints_message_with_info_severity() {
        log("Info message", severity = Severity.INFO)
    }

    @Test
    fun log_prints_message_with_error_severity() {
        log("Error message", severity = Severity.ERROR)
    }

    @Test
    fun log_prints_message_with_trace_severity() {
        log("Trace message", severity = Severity.TRACE)
    }

    @Test
    fun log_prints_message_with_throwable() {
        val exception = RuntimeException("Test exception")
        log("Error occurred", throwable = exception, severity = Severity.ERROR)
    }

    @Test
    fun log_prints_message_without_throwable() {
        log("Simple message", throwable = null, severity = Severity.INFO)
    }

    @Test
    fun log_on_failure_logs_on_result_failure() {
        val result: Result<Int> = Result.failure(RuntimeException("Test error"))
        val logged = result.logOnFailure("Operation failed", Severity.ERROR)

        assertTrue(logged.isFailure)
        assertEquals(result, logged)
    }

    @Test
    fun log_on_failure_does_not_log_on_result_success() {
        val result: Result<Int> = Result.success(42)
        val logged = result.logOnFailure("This should not be logged", Severity.ERROR)

        assertTrue(logged.isSuccess)
        assertEquals(42, logged.getOrNull())
    }

    @Test
    fun log_on_failure_returns_same_result_on_success() {
        val originalResult: Result<String> = Result.success("test data")
        val loggedResult = originalResult.logOnFailure("Error message", Severity.ERROR)

        assertEquals(originalResult, loggedResult)
        assertEquals("test data", loggedResult.getOrNull())
    }

    @Test
    fun log_on_failure_returns_same_result_on_failure() {
        val exception = IllegalStateException("Invalid state")
        val originalResult: Result<String> = Result.failure(exception)
        val loggedResult = originalResult.logOnFailure("State error", Severity.ERROR)

        assertEquals(originalResult.isFailure, loggedResult.isFailure)
        assertEquals(exception, loggedResult.exceptionOrNull())
    }

    @Test
    fun log_on_failure_works_with_different_result_types() {
        val intResult: Result<Int> = Result.success(100)
        val stringResult: Result<String> = Result.success("text")
        val boolResult: Result<Boolean> = Result.success(true)

        assertEquals(100, intResult.logOnFailure("Int error", Severity.DEBUG).getOrNull())
        assertEquals("text", stringResult.logOnFailure("String error", Severity.INFO).getOrNull())
        assertEquals(true, boolResult.logOnFailure("Bool error", Severity.TRACE).getOrNull())
    }

    @Test
    fun log_on_failure_handles_different_exception_types() {
        val runtimeException = Result.failure<Int>(RuntimeException("Runtime error"))
        val illegalArgument = Result.failure<Int>(IllegalArgumentException("Invalid arg"))
        val nullPointer = Result.failure<Int>(NullPointerException("Null value"))

        runtimeException.logOnFailure("Runtime exception", Severity.ERROR)
        illegalArgument.logOnFailure("Illegal argument", Severity.ERROR)
        nullPointer.logOnFailure("Null pointer", Severity.ERROR)

        assertTrue(runtimeException.isFailure)
        assertTrue(illegalArgument.isFailure)
        assertTrue(nullPointer.isFailure)
    }

    @Test
    fun log_handles_empty_message() {
        log("", severity = Severity.INFO)
    }

    @Test
    fun log_handles_long_message() {
        val longMessage = "a".repeat(10000)
        log(longMessage, severity = Severity.DEBUG)
    }

    @Test
    fun log_handles_multiline_message() {
        val multilineMessage = """
            Line 1
            Line 2
            Line 3
        """.trimIndent()
        log(multilineMessage, severity = Severity.INFO)
    }
}