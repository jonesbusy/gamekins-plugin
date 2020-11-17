package io.jenkins.plugins.gamekins.challenge

import hudson.FilePath
import hudson.model.Run
import hudson.model.TaskListener
import hudson.model.User
import io.jenkins.plugins.gamekins.util.GitUtil.getLastChangedTestFilesOfUser
import io.jenkins.plugins.gamekins.util.JacocoUtil.getTestCount
import org.jenkinsci.plugins.workflow.multibranch.WorkflowMultiBranchProject
import kotlin.collections.HashMap

/**
 * Specific [Challenge] to motivate the user to write a new test.
 *
 * @author Philipp Straubinger
 * @since 1.0
 */
class TestChallenge(private val currentCommit: String, private val testCount: Int, private val user: User,
                    private val branch: String, private var constants: HashMap<String, String>) : Challenge {

    private val created = System.currentTimeMillis()
    private var solved: Long = 0
    private var testCountSolved = 0

    override fun getCreated(): Long {
        return created
    }

    override fun getScore(): Int {
        return 1
    }

    override fun getSolved(): Long {
        return solved
    }

    override fun getConstants(): HashMap<String, String> {
        return constants
    }

    /**
     * A [TestChallenge] is always solvable if the branch (taken form the [constants]), where it has been generated,
     * still exists in the project.
     */
    override fun isSolvable(constants: HashMap<String, String>, run: Run<*, *>, listener: TaskListener,
                            workspace: FilePath): Boolean {
        if (run.parent.parent is WorkflowMultiBranchProject) {
            for (workflowJob in (run.parent.parent as WorkflowMultiBranchProject).items) {
                if (workflowJob.name == branch) return true
            }
        } else {
            return true
        }
        return false
    }

    /**
     * A [TestChallenge] can only be solved in the branch (taken form the [constants]) where it has been generated,
     * because there can be different amounts of tests in different branches. The [TestChallenge] is solved if the
     * [testCount] during generation was less than the current amount of tests and the [user] has written a test since
     * the last commit ([currentCommit]).
     */
    override fun isSolved(constants: HashMap<String, String>, run: Run<*, *>, listener: TaskListener,
                          workspace: FilePath): Boolean {
        if (branch != constants["branch"]) return false
        try {
            val testCountSolved = getTestCount(workspace, run)
            if (testCountSolved <= testCount) {
                return false
            }
            val lastChangedFilesOfUser = getLastChangedTestFilesOfUser(
                    workspace, user, 0, currentCommit, User.getAll())
            if (lastChangedFilesOfUser.isNotEmpty()) {
                solved = System.currentTimeMillis()
                this.testCountSolved = testCountSolved
                return true
            }
        } catch (e: Exception) {
            e.printStackTrace(listener.logger)
        }
        return false
    }

    override fun printToXML(reason: String, indentation: String): String {
        var print = (indentation + "<TestChallenge created=\"" + created + "\" solved=\"" + solved
                + "\" tests=\"" + testCount + "\" testsAtSolved=\"" + testCountSolved)
        if (reason.isNotEmpty()) {
            print += "\" reason=\"$reason"
        }
        print += "\"/>"
        return print
    }

    /**
     * Called by Jenkins after the object has been created from his XML representation. Used for data migration.
     */
    private fun readResolve(): Any {
        if (constants == null) constants = hashMapOf()
        return this
    }

    override fun toString(): String {
        return "Write a new test in branch $branch"
    }
}
