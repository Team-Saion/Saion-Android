package com.saion.core.network.datasource

import com.saion.core.network.model.common.ApiResponse
import com.saion.core.network.model.auth.TokenResponse
import com.saion.core.network.model.member.MemberInfoResponse
import com.saion.core.network.model.member.OnboardingInfoResponse

/**
 * 멤버 관련 원격 데이터 소스 계약입니다.
 */
interface MemberRemoteDataSource {
    /**
     * 현재 인증된 멤버의 프로필을 조회합니다.
     */
    suspend fun getMyInfo(): ApiResponse<MemberInfoResponse>

    /**
     * 현재 인증된 멤버의 온보딩 사전정보를 조회합니다.
     */
    suspend fun getOnboardingInfo(): ApiResponse<OnboardingInfoResponse>

    /**
     * 현재 인증된 멤버의 온보딩을 완료합니다.
     */
    suspend fun completeOnboarding(nickname: String): ApiResponse<TokenResponse>

    /**
     * 현재 인증된 멤버의 닉네임을 수정합니다.
     */
    suspend fun updateProfile(nickname: String): ApiResponse<MemberInfoResponse>

    /**
     * 현재 인증된 멤버의 프로필 이미지를 업로드합니다.
     */
    suspend fun uploadProfileImage(
        imageBytes: ByteArray,
        fileName: String,
        mimeType: String,
    ): ApiResponse<MemberInfoResponse>

    /**
     * 현재 인증된 멤버의 refresh token을 무효화합니다.
     */
    suspend fun logout(): ApiResponse<Unit>

    /**
     * 현재 인증된 멤버를 소프트 삭제 처리합니다.
     */
    suspend fun withdraw(): ApiResponse<Unit>
}
