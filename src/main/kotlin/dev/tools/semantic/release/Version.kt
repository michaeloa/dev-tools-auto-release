package dev.tools.semantic.release

import java.util.regex.Pattern

val versionPattern: Pattern = Pattern.compile("([0-9]\\d*)\\.(\\d+)\\.(\\d+)(?:-([a-zA-Z]+).(\\d+))?")

class Version(versionString: String) : Comparable<Version> {
    val major: Int
    val minor: Int
    val patch: Int
    val prereleaseId: String?
    val prerelease: Int?

    init {
        val matcher = versionPattern.matcher(versionString)
        matcher.matches()
        major = matcher.group(1).toInt()
        minor = matcher.group(2).toInt()
        patch = matcher.group(3).toInt()
        prereleaseId = matcher.group(4)
        prerelease = matcher.group(5)?.toInt()
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Version) return false
        return major == other.major &&
                minor == other.minor &&
                patch == other.patch &&
                prereleaseId == other.prereleaseId &&
                prerelease == other.prerelease
    }

    // Always override hashcode when changing equals
    override fun hashCode(): Int {
        var result = major
        result = 31 * result + minor
        result = 31 * result + patch
        result = 31 * result + (prereleaseId?.hashCode() ?: 0)
        result = 31 * result + (prerelease ?: 0)
        return result
    }

    override fun compareTo(other: Version): Int {
        when {
            major != other.major -> return major - other.major
            minor != other.minor -> return minor - other.minor
            patch != other.patch -> return patch - other.patch
            prereleaseId != other.prereleaseId -> {
                if (prereleaseId == null) return 1
                if (other.prereleaseId == null) return -1
                return prereleaseId.compareTo(other.prereleaseId)
            }
            prerelease != other.prerelease -> {
                if (prerelease == null) return 1
                if (other.prerelease == null) return -1
                return prerelease - other.prerelease
            }
            else -> return 0
        }
    }

    override fun toString(): String {
        return if (prereleaseId == null)
            "$major.$minor.$patch"
        else
            "$major.$minor.$patch-$prereleaseId.$prerelease"
    }

}