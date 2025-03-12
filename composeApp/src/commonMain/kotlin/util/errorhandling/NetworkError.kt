package util.errorhandling

enum class NetworkError : Error {
    UNAUTHORIZED,
    TOO_MANY_REQUESTS,
    SERIALIZATION,
    NOT_FOUND,
    UNKNOWN,
    BAD_REQUEST,
    FORBIDDEN,
}
