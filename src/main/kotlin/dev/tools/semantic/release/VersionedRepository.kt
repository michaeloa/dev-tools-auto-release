package dev.tools.semantic.release

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.ObjectId
import org.eclipse.jgit.lib.Ref
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import java.io.File
import java.io.IOException
import java.util.*

class VersionedRepository(workingDirectory: File) {
    private val builder: FileRepositoryBuilder
    private val repository: Repository
    private val versionedTags: List<VersionedTag>
    val currentVersion: Version
        get() = versionedTags.last().version
    val untaggedMessages: List<String>

    init {
        try {
            builder = FileRepositoryBuilder().setWorkTree(workingDirectory).findGitDir(workingDirectory)
            if (!builder.gitDir.exists()) {
                throw VersionException("Unable to find Git repository")
            }
            if (builder.gitDir.parentFile.absolutePath != workingDirectory.absolutePath) {
                builder.workTree = builder.gitDir.parentFile
            }
            repository = builder.build()
        } catch (e: IOException) {
            throw VersionException("Unable to find Git repository: ${e.message}", e)
        }
        // Get all the tags
        versionedTags = filterTags(getAllTags()).sortedBy { it.version }
        // Get messages since last version tag
        untaggedMessages = if (versionedTags.isNotEmpty())
            processCommits(versionedTags.last().commitId)
        else
            ArrayList()
    }

    private fun getActualRefObjectId(ref: Ref): ObjectId {
        val repoPeeled: Ref = repository.refDatabase.peel(ref)
        return if (repoPeeled.peeledObjectId != null) {
            repoPeeled.peeledObjectId
        } else {
            ref.objectId
        }
    }

    private fun getAllTags(): List<VersionedTag> {
        Git(repository).use { git ->
            return git.tagList().call().map { VersionedTag(it.name, getActualRefObjectId(it)) }
        }
    }

    private fun filterTags(refs: List<VersionedTag>): List<VersionedTag> {
        return refs.filter { it.tag.matches(Configuration.tagPattern) }
    }

    private fun processCommits(start: ObjectId): List<String> {
        val messages = ArrayList<String>()
        val head = repository.resolve("HEAD")
        Git(repository).use { git ->
            val logs = git.log().addRange(start, head).call()
            logs.forEach {
                messages.add(it.fullMessage)
            }
        }
        return messages
    }

}