package com.example.update

/**
 * Handles Semantic Versioning comparison (major.minor.patch).
 * Handles formats like "v2.3.1", "2.3.1", "2.4.0-beta", etc.
 */
data class SemVer(
    val major: Int,
    val minor: Int,
    val patch: Int
) : Comparable<SemVer> {

    override fun compareTo(other: SemVer): Int {
        if (this.major != other.major) return this.major.compareTo(other.major)
        if (this.minor != other.minor) return this.minor.compareTo(other.minor)
        return this.patch.compareTo(other.patch)
    }

    companion object {
        fun parse(versionStr: String?): SemVer? {
            if (versionStr.isNullOrEmpty()) return null
            val cleanStr = versionStr.trim()
                .removePrefix("v")
                .removePrefix("V")

            val baseVersion = cleanStr.split("-", "+")[0]
            val parts = baseVersion.split(".")

            val major = parts.getOrNull(0)?.toIntOrNull() ?: return null
            val minor = parts.getOrNull(1)?.toIntOrNull() ?: 0
            val patch = parts.getOrNull(2)?.toIntOrNull() ?: 0

            return SemVer(major, minor, patch)
        }
    }
}
