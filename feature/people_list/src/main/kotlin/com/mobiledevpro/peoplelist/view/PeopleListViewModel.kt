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
package com.mobiledevpro.peoplelist.view

import androidx.lifecycle.viewModelScope
import com.mobiledevpro.peoplelist.domain.usecase.GetPeopleListUseCase
import com.mobiledevpro.ui.vm.BaseViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class PeopleListViewModel(
    private val getPeopleListUseCase: GetPeopleListUseCase
) : BaseViewModel<PeopleProfileUIState>() {

    override fun initUIState(): PeopleProfileUIState = PeopleProfileUIState.Loading


    init {
        observePeopleList()
    }

    private fun observePeopleList() {
        viewModelScope.launch {
            getPeopleListUseCase.execute()
                .collectLatest { result ->
                    result.onSuccess { list ->
                        _uiState.update {
                            if (it is PeopleProfileUIState.Success)
                                it.copy(list)
                            else
                                PeopleProfileUIState.Success(list)
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