package net.averkhoglyad.chess.manager.core.service

import net.averkhoglyad.chess.manager.core.data.Profile

interface ProfileService {

    fun load(): List<Profile>
    fun save(profiles: List<Profile>)

}
