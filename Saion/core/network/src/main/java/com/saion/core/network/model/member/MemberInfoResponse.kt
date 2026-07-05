package com.saion.core.network.model.member

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 멤버 프로필 응답입니다.
 *
 * 백엔드가 내려주는 멤버 기본 프로필을 그대로 보존하는 전송 모델입니다.
 *
 * @property id 멤버 식별자입니다.
 * @property email 서비스 내 고유 이메일입니다.
 * @property name 소셜 플랫폼에서 제공한 이름입니다.
 * @property nickname 서비스 내 노출 닉네임입니다.
 * @property role 멤버 역할입니다.
 * @property avatarColor 멤버 아바타 기본 색상입니다.
 * @property profileImageKey 프로필 이미지 객체 키입니다. 설정하지 않았으면 `null`입니다.
 * @property status 멤버 상태입니다.
 * @property createdAt 멤버 생성 시각입니다.
 */
@Serializable
data class MemberInfoResponse(
    @SerialName("id")
    val id: String,
    @SerialName("email")
    val email: String? = null,
    @SerialName("name")
    val name: String? = null,
    @SerialName("nickname")
    val nickname: String,
    @SerialName("role")
    val role: String,
    @SerialName("avatarColor")
    val avatarColor: String? = null,
    @SerialName("profileImageKey")
    val profileImageKey: String? = null,
    @SerialName("status")
    val status: String,
    @SerialName("createdAt")
    val createdAt: String,
)
