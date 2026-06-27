package com.saion.core.domain.usecase.member

import com.saion.core.domain.repository.MemberRepository
import com.saion.core.model.member.MemberInfo
import com.saion.core.model.member.ProfileImageUpload
import com.saion.core.model.member.MemberRole
import com.saion.core.model.result.AppResult
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlinx.coroutines.runBlocking

class UpdateProfileUseCaseTest {
    @Test
    fun `프로필 수정 유스케이스는 닉네임 변경을 저장소에 위임한다`() = runBlocking {
        val repository = FakeMemberRepository()
        val useCase = UpdateProfileUseCase(memberRepository = repository)

        val result = useCase("new-nickname")

        assertEquals("new-nickname" to AppResult.Success(Unit), repository.updatedNickname to result)
    }
}

private class FakeMemberRepository : MemberRepository {
    var updatedNickname: String? = null

    override suspend fun getMyInfo(): AppResult<MemberInfo> = AppResult.Success(MemberInfo(role = MemberRole.MEMBER))

    override suspend fun updateProfile(nickname: String): AppResult<Unit> {
        updatedNickname = nickname
        return AppResult.Success(Unit)
    }

    override suspend fun uploadProfileImage(image: ProfileImageUpload): AppResult<Unit> = AppResult.Success(Unit)

    override suspend fun logout(): AppResult<Unit> = AppResult.Success(Unit)

    override suspend fun withdraw(): AppResult<Unit> = AppResult.Success(Unit)
}
