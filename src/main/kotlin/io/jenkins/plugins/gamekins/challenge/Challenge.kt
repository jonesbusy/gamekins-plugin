/*
 * Copyright 2020 Gamekins contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at

 * http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.jenkins.plugins.gamekins.challenge

import hudson.FilePath
import hudson.model.Run
import hudson.model.TaskListener
import io.jenkins.plugins.gamekins.statistics.Statistics
import io.jenkins.plugins.gamekins.LeaderboardAction
import kotlin.collections.HashMap

/**
 * Interface for all Challenges of Gamekins.
 *
 * @author Philipp Straubinger
 * @since 1.0
 */
interface Challenge {

    override fun equals(other: Any?): Boolean

    /**
     * Returns the constants provided during creation. Must include entries for "projectName", "branch", "workspace",
     * "jacocoResultsPath" and "jacocoCSVPath".
     */
    fun getConstants(): HashMap<String, String>

    /**
     * Returns the creation time in milliseconds since 01.01.1970.
     */
    fun getCreated(): Long

    /**
     * Returns the score for the specific [Challenge].
     */
    fun getScore(): Int

    /**
     * Returns the time when the [Challenge] has been solved in milliseconds since 01.01.1970.
     */
    fun getSolved(): Long

    /**
     * Returns the tooltip of the Challenge. Currently only available for the [LineCoverageChallenge].
     */
    fun getToolTipText(): String {
        return ""
    }

    /**
     * Checks whether the current [Challenge] is still solvable or not.
     */
    fun isSolvable(constants: HashMap<String, String>, run: Run<*, *>, listener: TaskListener, workspace: FilePath)
            : Boolean

    /**
     * Checks whether the current [Challenge] is solved.
     */
    fun isSolved(constants: HashMap<String, String>, run: Run<*, *>, listener: TaskListener, workspace: FilePath)
            : Boolean

    /**
     * Returns whether a tooltip should be shown for the Challenge. Currently only available for the
     * [LineCoverageChallenge].
     */
    fun isToolTip(): Boolean {
        return false
    }

    /**
     * Returns the XML representation of the current [Challenge] with the [indentation] in front of the line
     * for the [Statistics]. Adds an additional [reason] in case that the [Challenge] has been rejected.
     */
    fun printToXML(reason: String, indentation: String): String?

    /**
     * Returns the String representation of the [Challenge] for the [LeaderboardAction].
     */
    override fun toString(): String
}
