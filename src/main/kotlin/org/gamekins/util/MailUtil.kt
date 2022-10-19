package org.gamekins.util

import com.cloudbees.hudson.plugins.folder.Folder
import hudson.model.AbstractItem
import hudson.model.User
import hudson.tasks.MailAddressResolver
import hudson.tasks.Mailer
import org.gamekins.property.GameFolderProperty
import org.gamekins.property.GameJobProperty
import org.gamekins.property.GameMultiBranchProperty
import org.jenkinsci.plugins.workflow.multibranch.WorkflowMultiBranchProject
import java.util.*
import javax.mail.Message
import javax.mail.MessagingException
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage


/**
 * Util object for sending mails
 *
 * @author Matthias Rainer
 */
object MailUtil {
    fun sendMail(user: User, subject: String, addressFrom: String, name: String, text: String) {
        val mailer = Mailer.descriptor()
        val mail = MailAddressResolver.resolve(user)
        val msg = MimeMessage(mailer.createSession())
        msg.subject = subject
        msg.setFrom(InternetAddress(addressFrom, name))
        msg.sentDate = Date()
        msg.addRecipient(Message.RecipientType.TO, Mailer.stringToAddress(mail, mailer.charset))
        msg.setText(text)
        try {
            Transport.send(msg)
        } catch (e: MessagingException) {
            e.printStackTrace()
        }
    }

    fun generateViewLeaderboardText(job: AbstractItem): String {
        var text = "View the leaderboard on ${job.absoluteUrl}leaderboard/\n"
        val property = PropertyUtil.retrieveGameProperty(job)
        if (property is GameJobProperty || property is GameMultiBranchProperty) {
            if (job.parent is Folder
                && (job.parent as Folder).properties.get(GameFolderProperty::class.java).leaderboard) {
                text += "View the comprehensive leaderboard on " +
                        "${(job.parent as Folder).absoluteUrl}leaderboard/\n"
            }
            if (job.parent is WorkflowMultiBranchProject
                && (job.parent as WorkflowMultiBranchProject).parent is Folder
                && ((job.parent as WorkflowMultiBranchProject).parent as Folder)
                    .properties.get(GameFolderProperty::class.java).leaderboard) {
                text += "View the comprehensive leaderboard on " +
                        "${((job.parent as WorkflowMultiBranchProject).parent as Folder)
                            .absoluteUrl}leaderboard/\n"
            }
        }
        return text
    }
}