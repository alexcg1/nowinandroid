/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.samples.apps.nowinandroid.feature.foryou

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.samples.apps.nowinandroid.core.data.model.UserUrl
import com.google.samples.apps.nowinandroid.core.data.repository.UserUrlRepository
import com.google.samples.apps.nowinandroid.core.data.util.UrlTitleFetcher
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel for managing user-added URLs
 */
@HiltViewModel
class UserUrlViewModel @Inject constructor(
    private val userUrlRepository: UserUrlRepository,
    private val urlTitleFetcher: UrlTitleFetcher
) : ViewModel() {

    /**
     * Stream of user-added URLs
     */
    val userUrls: StateFlow<List<UserUrl>> = userUrlRepository.getUserUrls()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )
    
    /**
     * Adds a new URL to the repository and fetches its title
     */
    fun addUrl(url: String) {
        if (url.isBlank()) return
        
        viewModelScope.launch {
            val formattedUrl = if (!url.startsWith("http")) "https://$url" else url
            val title = urlTitleFetcher.fetchTitle(formattedUrl)
            userUrlRepository.addUserUrl(formattedUrl, title)
        }
    }
}