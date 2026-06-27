package com.saion.core.model.member

data class ProfileImageUpload(
    val bytes: ByteArray,
    val fileName: String,
    val mimeType: String,
)
