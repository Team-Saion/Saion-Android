package com.saion.core.domain.repository

import com.saion.core.model.member.MemberInfo
import com.saion.core.model.member.ProfileImageUpload
import com.saion.core.model.result.AppResult

/**
 * 멤버 도메인 경계를 정의합니다.
 */
interface MemberRepository {
    /**
     * 현재 로그인한 멤버의 프로필을 조회합니다.
     */
    suspend fun getMyInfo(): AppResult<MemberInfo>

    /**
     * 현재 로그인한 멤버의 닉네임을 수정합니다.
     */
    suspend fun updateProfile(nickname: String): AppResult<Unit>

    /**
     * 현재 로그인한 멤버의 프로필 이미지를 업로드합니다.
     */
    suspend fun uploadProfileImage(image: ProfileImageUpload): AppResult<Unit>

    /**
     * 현재 로그인한 멤버를 로그아웃합니다.
     */
    suspend fun logout(): AppResult<Unit>

    /**
     * 현재 로그인한 멤버를 탈퇴 처리합니다.
     */
    suspend fun withdraw(): AppResult<Unit>
}
