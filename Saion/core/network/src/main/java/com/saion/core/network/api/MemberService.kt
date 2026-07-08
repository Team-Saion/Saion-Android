package com.saion.core.network.api

import com.saion.core.network.model.auth.TokenResponse
import com.saion.core.network.model.common.ApiResponse
import com.saion.core.network.model.common.ApiResponse.Companion.toApiResponse
import com.saion.core.network.model.member.CompleteOnboardingRequest
import com.saion.core.network.model.member.MemberInfoResponse
import com.saion.core.network.model.member.OnboardingInfoResponse
import com.saion.core.network.model.member.UpdateProfileRequest
import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders

/**
 * Member API 원격 호출을 구성합니다.
 */
class MemberService(private val client: HttpClient) {
    /**
     * 현재 인증된 멤버의 프로필 정보를 조회합니다.
     *
     * `MEMBER`, `ADMIN`만 접근할 수 있고 `PENDING` 역할은 403을 반환합니다.
     */
    suspend fun getMyInfo(): ApiResponse<MemberInfoResponse> = client
        .get("/api/v1/members/me")
        .toApiResponse()

    /**
     * 현재 인증된 멤버의 온보딩 사전정보를 조회합니다.
     */
    suspend fun getOnboardingInfo(): ApiResponse<OnboardingInfoResponse> = client
        .get("/api/v1/members/me/onboarding-info")
        .toApiResponse()

    /**
     * 현재 인증된 멤버의 온보딩을 완료합니다.
     *
     * 성공 시 멤버 역할이 `MEMBER`로 바뀌며 새 access token과 refresh token을 반환합니다.
     */
    suspend fun completeOnboarding(nickname: String): ApiResponse<TokenResponse> = client
        .patch("/api/v1/members/me/onboarding") {
            setBody(CompleteOnboardingRequest(nickname = nickname))
        }.toApiResponse()

    /**
     * 현재 인증된 멤버의 닉네임을 변경합니다.
     *
     * 닉네임은 2자 이상 10자 이하이며 한글, 영문, 숫자만 허용하고 앞뒤 공백은 허용하지 않습니다.
     */
    suspend fun updateProfile(nickname: String): ApiResponse<MemberInfoResponse> = client
        .patch("/api/v1/members/me/profile") {
            setBody(UpdateProfileRequest(nickname = nickname))
        }.toApiResponse()

    /**
     * 현재 인증된 멤버의 프로필 이미지를 업로드합니다.
     *
     * 파일은 `image` 파트로 전송하며 허용 포맷은 JPEG, PNG, WebP이고 최대 용량은 20MB입니다.
     */
    suspend fun uploadProfileImage(
        imageBytes: ByteArray,
        fileName: String,
        mimeType: String,
    ): ApiResponse<MemberInfoResponse> = client
        .post("/api/v1/members/me/profile-image") {
            setBody(
                MultiPartFormDataContent(
                    formData {
                        append(
                            key = "image",
                            value = imageBytes,
                            headers = Headers.build {
                                append(HttpHeaders.ContentDisposition, "filename=\"$fileName\"")
                                append(HttpHeaders.ContentType, mimeType)
                            },
                        )
                    },
                ),
            )
        }.toApiResponse()

    /**
     * 현재 인증된 멤버의 refresh token을 무효화합니다.
     *
     * access token 자체는 stateless JWT라 서버 저장소에서 삭제하지 않습니다.
     */
    suspend fun logout(): ApiResponse<Unit> = client
        .post("/api/v1/members/me/logout")
        .toApiResponse()

    /**
     * 현재 인증된 멤버를 소프트 삭제 처리합니다.
     *
     * 탈퇴 시 refresh token도 함께 무효화됩니다.
     */
    suspend fun withdraw(): ApiResponse<Unit> = client
        .delete("/api/v1/members/me")
        .toApiResponse()
}
