package dev.tools.semantic.release

class VersionException : Exception {

    constructor(message: String) : super(message)

    constructor(throwable: Throwable) : super(throwable)

    constructor(message: String, throwable: Throwable) : super(message, throwable)

}