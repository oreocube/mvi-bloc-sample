package com.sangrok.bloc_mvi_sample

import com.sangrok.bloc_mvi_sample.repository.MockRepository
import com.sangrok.bloc_mvi_sample.ui.main.MainAction
import com.sangrok.bloc_mvi_sample.ui.main.MainActionTransformer
import com.sangrok.bloc_mvi_sample.ui.main.Member
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever

class MainActionTransformerTest {

    @Mock
    lateinit var mockRepository: MockRepository
    lateinit var mainActionTransformer: MainActionTransformer

    @Before
    fun before() {
        MockitoAnnotations.openMocks(this)
        mainActionTransformer = MainActionTransformer(mockRepository)
    }

    @Test
    fun `GIVEN 멤버 WHEN 토글 클릭 시 THEN 멤버의 좋아요 상태를 변경한다`() = runTest {
        //GIVEN
        val member = Member(name = "하하", liked = false)
        whenever(mockRepository.like(member)).thenReturn(member.copy(liked = member.liked.not()))

        //WHEN
        val action = MainAction.ClickToggle(member)
        val actual = mainActionTransformer.transformActions(action).last()

        //THEN
        val expect = MainAction.SetMemberState(member.copy(liked = member.liked.not()))
        Assert.assertEquals(expect, actual)
    }


    @Test
    fun `GIVEN 멤버 WHEN 토글 예외 발생 시 THEN 롤백한다`() = runTest {
        //GIVEN
        val member = Member(name = "하하", liked = false)
        whenever(mockRepository.like(member)).thenThrow(IllegalStateException())

        //WHEN
        val action = MainAction.ClickToggle(member)
        val actual = mainActionTransformer.transformActions(action).last()

        //THEN
        val expect = MainAction.SetMemberState(member)
        Assert.assertEquals(expect, actual)
    }
}