/*
 * Copyright 2022 | Dmitri Chernysh | https://mobile-dev.pro
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.mobiledevpro.people.profile.view.vm

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.mobiledevpro.people.profile.domain.usecase.GetPeopleProfileUseCase
import com.mobiledevpro.people.profile.view.args.PeopleProfileArgs
import com.mobiledevpro.people.profile.view.state.PeopleProfileUIState
import com.mobiledevpro.ui.vm.BaseViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Profile screen for selected person from People list
 *
 * Created on Feb 04, 2023.
 *
 */
class PeopleProfileViewModel(
    savedStateHandle: SavedStateHandle,
    private val getPeopleProfileUseCase: GetPeopleProfileUseCase
) : BaseViewModel<PeopleProfileUIState>() {

    override fun initUIState(): PeopleProfileUIState = PeopleProfileUIState.Empty

    private val profileId : Int =  PeopleProfileArgs(savedStateHandle).peopleProfileId


    init {
        Log.d("navigation", "PeopleProfileViewModel: args = $profileId")

        PeopleProfileArgs(savedStateHandle)
            .peopleProfileId
            .also(::observePeopleProfile)
    }

    private fun observePeopleProfile(profileId: Int) {
        viewModelScope.launch {
            getPeopleProfileUseCase.execute(profileId)
                .collectLatest { result ->
                    result.onSuccess { profile ->
                        Log.d("navigation", "observePeopleProfile: ${profile?.id ?: -1}")
                        profile?.let {
                            _uiState.update {
                                if (it is PeopleProfileUIState.Success)
                                    it.copy(profile = profile)
                                else
                                    PeopleProfileUIState.Success(profile)
                            }
                        }
                    }.onFailure { err ->
                        _uiState.update {
                            PeopleProfileUIState.Fail(err)
                        }
                    }

                }
        }
    }
}