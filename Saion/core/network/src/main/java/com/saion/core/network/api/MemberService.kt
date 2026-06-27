package com.saion.core.network.api

import com.saion.core.network.model.common.ApiResponse
import com.saion.core.network.model.common.ApiResponse.Companion.toApiResponse
import com.saion.core.network.model.member.MemberInfoResponse
import com.saion.core.network.model.member.UpdateProfileRequest
import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders

class MemberService(private val client: HttpClient) {
    suspend fun getMyInfo(): ApiResponse<MemberInfoResponse> = client
        .get("/api/v1/members/me")
        .toApiResponse()

    suspend fun updateProfile(nickname: String): ApiResponse<MemberInfoResponse> = client
        .patch("/api/v1/members/me/profile") {
            setBody(UpdateProfileRequest(nickname = nickname))
        }.toApiResponse()

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

    suspend fun logout(): ApiResponse<Unit> = client
        .post("/api/v1/members/me/logout")
        .toApiResponse()

    suspend fun withdraw(): ApiResponse<Unit> = client
        .delete("/api/v1/members/me")
        .toApiResponse()
}
