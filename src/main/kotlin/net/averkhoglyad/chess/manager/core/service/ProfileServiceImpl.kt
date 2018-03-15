//package net.averkhoglyad.chess.manager.core.service
//
//import net.averkhoglyad.chess.manager.core.sdk.data.User
//
//import java.nio.file.Files
//import java.nio.file.Path
//import java.util.ArrayList
//import java.util.stream.Collectors
//import java.util.stream.Stream
//
//import net.averkhoglyad.chess.manager.core.helper.ExceptionHelper.doStrict
//
//class ProfileServiceImpl(private val profilesFile: Path) : ProfileService {
//
//    override fun load(): List<User> {
//        val users = ArrayList<E>()
//        if (Files.exists(profilesFile)) {
//            doStrict { Files.lines(profilesFile) }.use({ lines -> lines.map(Function<String, R> { User() }).forEachOrdered(Consumer<R> { users.add() }) })
//        }
//        return users
//    }
//
//    override fun save(profiles: List<User>) {
//        val usernames = profiles.stream()
//                .map(Function<User, R> { User.getUsername() })
//                .collect(Collectors.toList<Any>())
//        doStrict { Files.write(profilesFile, usernames) }
//    }
//
//}
