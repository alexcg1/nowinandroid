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

package com.google.samples.apps.nowinandroid.core.data.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.net.URI
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Service for fetching webpage titles from URLs
 */
@Singleton
class UrlTitleFetcher @Inject constructor() {
    /**
     * Fetches the title of a webpage from a URL
     * @param url The URL to fetch the title from
     * @return The title of the webpage, or the domain name if title cannot be fetched
     */
    suspend fun fetchTitle(url: String): String = withContext(Dispatchers.IO) {
        return@withContext try {
            // Get webpage title
            val doc = Jsoup.connect(url)
                .timeout(5000)
                .get()
            
            // Return the title or domain name if empty
            doc.title().ifEmpty {
                extractDomainFromUrl(url)
            }
        } catch (e: Exception) {
            // If any error occurs, return the domain name
            extractDomainFromUrl(url)
        }
    }
    
    /**
     * Extracts a readable domain name from a URL
     */
    private fun extractDomainFromUrl(url: String): String {
        return try {
            val uri = URI(url)
            val domain = uri.host ?: return url
            if (domain.startsWith("www.")) domain.substring(4) else domain
        } catch (e: Exception) {
            url
        }
    }
}