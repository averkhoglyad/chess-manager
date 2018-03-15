package net.averkhoglyad.chess.manager.gui.util

import org.picocontainer.PicoContainer
import tornadofx.DIContainer
import kotlin.reflect.KClass

class PicoDIContainer(private val pico: PicoContainer) : DIContainer {

    override fun <T : Any> getInstance(type: KClass<T>) = pico.getComponent(type.java)!!

}
