package com.saion.feature.schedule.impl.viewmodel

import androidx.compose.runtime.Immutable
import com.saion.core.model.schedule.ConfirmationType
import com.saion.core.model.schedule.ScheduleDetail
import com.saion.core.ui.viewmodel.UIState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
internal data class ScheduleDetailState(
    val isLoading: Boolean = true,
    val isSubmitting: Boolean = false,
    val detail: ScheduleDetail? = null,
    val confirmationOptions: ImmutableList<ScheduleConfirmationUiModel> = persistentListOf(),
    val canDelete: Boolean = false,
    val isDeleteDialogVisible: Boolean = false,
) : UIState

@Immutable
internal data class ScheduleConfirmationUiModel(
    val type: ConfirmationType,
    val label: String,
    val count: Int,
    val isSelected: Boolean,
)
