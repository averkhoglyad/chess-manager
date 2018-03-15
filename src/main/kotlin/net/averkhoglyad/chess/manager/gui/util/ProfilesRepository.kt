package net.averkhoglyad.chess.manager.gui.util

import net.averkhoglyad.chess.manager.core.data.Profile
import net.averkhoglyad.chess.manager.core.util.toSet
import java.nio.file.Files
import java.nio.file.Path

interface ProfilesRepository {

    fun list(): List<Profile>
    fun put(profile: Profile): List<Profile>
    fun drop(profile: Profile): List<Profile>

}

class ProfilesRepositoryImpl(private val profilesFile: Path) : ProfilesRepository {

    private var profiles: Set<Profile> = setOf()

    init {
        if (Files.exists(profilesFile)) {
            Files.lines(profilesFile).use { lines ->
                profiles = lines
                        .filter { it.isNotEmpty() }
                        .map { Profile(it) }
                        .toSet()
            }
        }
    }

    override fun list(): List<Profile> = profiles.toList()

    override fun put(profile: Profile): List<Profile> {
        profiles += profile
        save()
        return profiles.toList()
    }

    override fun drop(profile: Profile): List<Profile> {
        profiles -= profile
        save()
        return profiles.toList()
    }

    private fun save() {
        val lichessIds = profiles
                .filter { it.lichessId.isNotEmpty() }
                .map { it.lichessId }
        Files.write(profilesFile, lichessIds)
    }

}