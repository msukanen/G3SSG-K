package net.linkpc.scifi.g3ssg.core

import net.linkpc.scifi.g3ssg.core.orbital.generateOrbits

class StarSystem {
    val primaryStar = Star.create()
    val primarySys = generateOrbits(primaryStar)
}
