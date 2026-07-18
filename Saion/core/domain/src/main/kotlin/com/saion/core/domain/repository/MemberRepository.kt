package com.saion.core.domain.repository

import com.saion.core.model.member.MemberInfo
import com.saion.core.model.member.MemberRole
import com.saion.core.model.member.MemberStatus
import com.saion.core.model.member.OnboardingInfo
import com.saion.core.model.member.ProfileImageUpload
import com.saion.core.model.result.AppResult
import kotlinx.coroutines.flow.Flow

/**
 * 멤버 도메인 경계를 정의합니다.
 */
interface MemberRepository {
    fun observeMyInfo(): Flow<MemberInfo?>

    /**
     * 현재 로그인한 멤버의 프로필을 조회합니다.
     */
    suspend fun getMyInfo(): AppResult<MemberInfo>

    suspend fun refreshMyInfo(): AppResult<MemberInfo>

    /**
     * 현재 로그인한 멤버의 온보딩 사전정보를 조회합니다.
     */
    suspend fun getOnboardingInfo(): AppResult<OnboardingInfo>

    /**
     * 현재 로그인한 멤버의 온보딩을 완료합니다.
     */
    suspend fun completeOnboarding(nickname: String): AppResult<Unit>

    /**
     * 현재 로그인한 멤버의 닉네임을 수정합니다.
     */
    suspend fun updateProfile(nickname: String): AppResult<Unit>

    /**
     * 현재 로그인한 멤버의 상태나 역할을 개발 편의용으로 변경합니다.
     */
    suspend fun changeState(
        status: MemberStatus? = null,
        role: MemberRole? = null,
    ): AppResult<MemberInfo>

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
    suspend fun withdraw(reason: String = ""): AppResult<Unit>
}
