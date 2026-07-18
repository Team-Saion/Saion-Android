package com.saion.feature.profileedit.impl

import android.content.Context
import android.database.Cursor
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import androidx.core.net.toUri
import com.saion.core.model.member.ProfileImageUpload
import com.saion.core.model.result.AppError
import com.saion.core.model.result.AppResult
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal interface ProfileImageReader {
    suspend fun read(imageUri: String): AppResult<ProfileImageUpload>
}

internal const val PROFILE_IMAGE_READ_ERROR_MESSAGE: String = "PROFILE_IMAGE_READ_ERROR"

internal class DefaultProfileImageReader @Inject constructor(
    @param:ApplicationContext private val context: Context,
) : ProfileImageReader {
    override suspend fun read(imageUri: String): AppResult<ProfileImageUpload> = withContext(Dispatchers.IO) {
        runCatching {
            val uri = imageUri.toUri()
            val mimeType = context.contentResolver.getType(uri)
                ?: throw IllegalArgumentException("Profile image mime type is missing.")
            val fileName = context.contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)
                ?.useDisplayName()
                ?.ifBlank { null }
                ?: buildFallbackFileName(mimeType)
            val imageBytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                ?: throw IllegalArgumentException("Profile image stream is missing.")

            ProfileImageUpload(
                bytes = imageBytes,
                fileName = fileName,
                mimeType = mimeType,
            )
        }.fold(
            onSuccess = { AppResult.Success(it) },
            onFailure = { throwable ->
                AppResult.Failure(
                    AppError.Unknown(
                        message = PROFILE_IMAGE_READ_ERROR_MESSAGE,
                        cause = throwable,
                    ),
                )
            },
        )
    }

    private fun buildFallbackFileName(mimeType: String): String {
        val extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)?.ifBlank { null }
        return if (extension != null) {
            "profile_image.$extension"
        } else {
            "profile_image"
        }
    }
}

private fun Cursor.useDisplayName(): String? {
    val index = getColumnIndex(OpenableColumns.DISPLAY_NAME)
    return if (index >= 0 && moveToFirst()) getString(index) else null
}
