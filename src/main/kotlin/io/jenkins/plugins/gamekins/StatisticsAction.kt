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

package io.jenkins.plugins.gamekins

import hudson.model.AbstractItem
import hudson.model.AbstractProject
import hudson.model.ProminentProjectAction
import io.jenkins.plugins.gamekins.property.GameJobProperty
import io.jenkins.plugins.gamekins.property.GameMultiBranchProperty
import io.jenkins.plugins.gamekins.property.GameProperty
import io.jenkins.plugins.gamekins.statistics.Statistics
import org.jenkinsci.plugins.workflow.job.WorkflowJob
import org.jenkinsci.plugins.workflow.multibranch.WorkflowMultiBranchProject

/**
 * Action to display the [Statistics] XML representation on the left side panel of a job for evaluation purposes.
 *
 * @author Philipp Straubinger
 * @since 1.0
 */
class StatisticsAction(val job: AbstractItem) : ProminentProjectAction {

    override fun getDisplayName(): String {
        return "Statistics"
    }

    override fun getIconFileName(): String {
        return "document.png"
    }

    /**
     * Returns the XML representation of the project.
     */
    fun getStatistics(): String {
        val property: GameProperty = when (job) {
            is WorkflowMultiBranchProject -> {
                job.properties.get(GameMultiBranchProperty::class.java)
            }
            is WorkflowJob -> {
                job.getProperty(GameJobProperty::class.java)
            }
            else -> {
                (job as AbstractProject<*, *>).getProperty(GameJobProperty::class.java)
            }
        }
        return property.getStatistics().printToXML()
    }

    override fun getUrlName(): String {
        return "statistics"
    }
}
