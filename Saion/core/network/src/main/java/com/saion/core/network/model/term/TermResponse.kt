package com.saion.core.network.model.term

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 약관 응답입니다.
 *
 * 현재 발효 중인 약관 버전 한 건을 나타냅니다.
 *
 * @property id 약관 버전 ID입니다.
 * @property termCode 약관 유형 코드입니다.
 * @property title 약관 제목입니다.
 * @property contentUrl 약관 본문 URL입니다.
 * @property version 약관 버전입니다.
 * @property required 필수 동의 여부입니다.
 * @property effectiveAt 발효 시각입니다.
 */
@Serializable
data class TermResponse(
    @SerialName("id")
    val id: String,
    @SerialName("termCode")
    val termCode: String,
    @SerialName("title")
    val title: String,
    @SerialName("contentUrl")
    val contentUrl: String? = null,
    @SerialName("version")
    val version: Int,
    @SerialName("required")
    val required: Boolean,
    @SerialName("effectiveAt")
    val effectiveAt: String,
)
