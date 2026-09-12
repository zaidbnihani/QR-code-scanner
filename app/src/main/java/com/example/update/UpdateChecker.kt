package com.example.update

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

data class UpdateInfo(
    val latestVersion: String,
    val releaseNotes: String,
    val downloadUrl: String
)

class UpdateChecker(
    private val owner: String,
    private val repo: String
) {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.github.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService = retrofit.create(GitHubApiService::class.java)

    suspend fun checkForUpdate(currentVersionName: String): UpdateInfo? {
        return try {
            val release = apiService.getLatestRelease(owner, repo)
            val latestSemVer = SemVer.parse(release.tagName) ?: SemVer.parse(release.name)
            val currentSemVer = SemVer.parse(currentVersionName)

            if (latestSemVer != null && currentSemVer != null && latestSemVer > currentSemVer) {
                // Find direct APK download url if available, otherwise fallback to release page
                val apkAsset = release.assets?.firstOrNull {
                    it.name?.endsWith(".apk", ignoreCase = true) == true
                }
                val downloadUrl = apkAsset?.browserDownloadUrl ?: release.htmlUrl ?: ""

                UpdateInfo(
                    latestVersion = release.tagName ?: release.name ?: "Unknown",
                    releaseNotes = release.body ?: "لا توجد ملاحظات بهذا الإصدار.",
                    downloadUrl = downloadUrl
                )
            } else {
                null
            }
        } catch (e: Exception) {
            // Silently handle any network or API error
            null
        }
    }
}
