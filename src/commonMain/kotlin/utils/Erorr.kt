package utils


/**
 * Applies the given [transform] function to the value inside a successful [Result],
 * and returns the new [Result] returned by that function.
 *
 * If the original [Result] is a failure, the failure is returned unchanged.
 *
 * It's similar to `map`, but used when [transform] itself returns a `Result<R>`,
 * to avoid nesting like `Result<Result<R>>`.
 *
 * Imagine this flow:
 *
 *     Result<T>
 *        |
 *   onSuccess --> transform: (T) -> Result<R>
 *        |
 *    return Result<R>
 *
 * Example:
 *     fun parseInt(str: String): Result<Int> = ...
 *
 *     val result = Result.success("123")
 *         .flatMap(::parseInt)
 *
 *     // result: Result<Int>
 */
inline fun <T, R> Result<T>.flatMap(transform: (T) -> Result<R>): Result<R> {
    return fold(
        onSuccess = { transform(it) },                  // If success, apply transform
        onFailure = { Result.failure(it) }   // If failure, return as-is
    )
}
