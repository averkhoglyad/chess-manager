package net.averkhoglyad.chess.manager.core.sdk.lichess.data

data class User(var username: String?,
                var title: String? = null,
                var url: String? = null,
                var online: Boolean = false,
                var playing: String? = null,
                var engine: Boolean = false,
                var language: String? = null,
                var profile: Profile = Profile(),
                var performances: Map<String, Stats> = mapOf())

data class Profile(var bio: String? = null,
                   var country: String? = null,
                   var firstName: String? = null,
                   var lastName: String? = null,
                   var location: String? = null)

data class Stats(var games: Int = 0,
                 var rating: Int = 0,
                 var rd: Int = 0,
                 var prog: Int = 0)
