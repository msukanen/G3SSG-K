package net.linkpc.scifi.g3ssg.core

open class Star(
    val type:Type,
    val klass:Class,
    val mass:Double,
    b:AU,
    val innerLimit:AU,
    val radius:AU,
    val planetsOn:Int,
    n:Int,
    val lifeRollMod:Int
    ) {
    enum class Type { D, M, K, G, F, A, B, O }
    enum class Class { D, VI, V, IV, III, II, Ib, Ia }

    class WhiteDwarf(t:Type, c:Class, p:Int, n:Int) :
          Star(t, c, 0.8, 0.025.AU, AU.Zero, AU.Zero, p, n, -10)

    val biozone = Pair(b, AU(b.toDouble() * 1.5))
    val numPotentialOrbits = n

    val orbits = mapOf<Int, Orbit>()
    private val baseD = 0.1 * 1.d6
    private val bodeC = if(klass == Class.VI && type == Type.M) 0.2 else when (1.d6) {
        in 1..2 -> 0.3
        in 3..4 -> 0.35
        else -> 0.4
    }

    fun distanceOf(i:Int): AU  = AU(when (i) {
        1 -> baseD
        2 -> baseD + bodeC
        else -> baseD + (2 shl (i - 3)) * bodeC
    })

    companion object Factory {
        fun create(): Star {
            val c = when (3.d6) {
                in 3..5 -> Class.D
                6 -> Class.VI
                in 7..17 -> Class.V
                else -> when (3.d6) {
                    3 -> if (1.d6 < 3) Class.Ia else Class.Ib
                    4 -> Class.II
                    in 5..12 -> Class.III
                    else -> Class.IV
                }
            }
            fun gst(c: Class): Type {
                var t: Type
                while(true) {
                    t = when(2.d6) {
                        2 -> Type.O
                        3 -> Type.M
                        in 4..5 -> Type.B
                        in 6..9 -> Type.K
                        else -> Type.A
                    }
                    if (t != Type.O && t != Type.M)
                        break
                    if (t == Type.M && c != Class.IV)
                        break
                    if (t == Type.O && (c != Class.II && c != Class.III && c != Class.IV))
                        break
                }
                return t
            }
            val t = when (c) {
                Class.V -> when(3.d6) {
                    3 -> Type.O
                    4 -> Type.B
                    5 -> Type.A
                    6 -> Type.F
                    7 -> Type.G
                    8 -> Type.K
                    else -> Type.M
                }
                Class.D -> Type.D
                Class.VI -> when(1.d6) {
                    1 -> Type.G
                    2 -> Type.K
                    else -> Type.M
                }
                else -> gst(c)
            }
            return when(t) {
                Type.O -> when(c) {
                    Class.Ia -> Star(t, c, 70.0, 790.AU, 16.AU, 0.2.AU, 0, 0, -12)
                    Class.Ib -> Star(t, c, 60.0, 630.AU, 13.AU, 0.1.AU, 0, 0, -12)
                    else -> Star(t, c, 50.0, 500.AU, 10.AU, AU.Zero, 0, 0, -9)
                }
                Type.B -> when(c) {
                    Class.Ia -> Star(t, c, 50.0, 500.AU, 10.AU, 0.2.AU, 0, 0, -10)
                    Class.Ib -> Star(t, c, 40.0, 320.AU, 6.3.AU, 0.1.AU, 0, 0, -10)
                    Class.II -> Star(t, c, 35.0, 250.AU, 5.AU, 0.1.AU, 3, 3.d6+1, -10)
                    Class.III -> Star(t, c, 30.0, 200.AU, 4.AU, AU.Zero, 3, 3.d6+1, -10)
                    Class.IV -> Star(t, c, 20.0, 180.AU, 3.8.AU, AU.Zero, 3, 3.d6+1, -10)
                    else -> Star(t, c, 10.0, 30.AU, 0.6.AU, AU.Zero, 4, 3.d6, -9)
                }
                Type.A -> when(c) {
                    Class.Ia -> Star(t, c, 30.0, 200.AU, 4.AU, 0.6.AU, 3, 3.d6+3, -10)
                    Class.Ib -> Star(t, c, 16.0, 50.AU, 1.AU, 0.2.AU, 3, 3.d6+2, -10)
                    Class.II -> Star(t, c, 10.0, 20.AU, 0.4.AU, AU.Zero, 3, 3.d6+2, -10)
                    Class.III -> Star(t, c, 6.0, 5.AU, AU.Zero, AU.Zero, 3, 3.d6+1, -10)
                    Class.IV -> Star(t, c, 4.0, 4.AU, AU.Zero, AU.Zero, 4, 3.d6, -10)
                    else -> Star(t, c, 3.0, 3.1.AU, AU.Zero, AU.Zero, 5, 3.d6-1, -9)
                }
                Type.F -> when(c) {
                    Class.Ia -> Star(t, c, 15.0, 200.AU, 4.AU, 0.8.AU, 4, 3.d6+3, -10)
                    Class.Ib -> Star(t, c, 13.0, 50.AU, 1.AU, 0.2.AU, 4, 3.d6+2, -10)
                    Class.II -> Star(t, c, 8.0, 13.AU, 0.3.AU, AU.Zero, 4, 3.d6+1, -9)
                    Class.III -> Star(t, c, 2.5, 2.5.AU, 0.1.AU, AU.Zero, 4, 3.d6, -9)
                    Class.IV -> Star(t, c, 2.2, 2.AU, AU.Zero, AU.Zero, 6, 3.d6, -9)
                    else -> Star(t, c, 1.9, 1.6.AU, AU.Zero, AU.Zero, 13, 3.d6-1, -8)
                }
                Type.G -> when(c) {
                    Class.Ia -> Star(t, c, 12.0, 160.AU, 3.1.AU, 1.4.AU, 6, 3.d6+3, -10)
                    Class.Ib -> Star(t, c, 10.0, 50.AU, 1.AU, 0.4.AU, 6, 3.d6+2, -10)
                    Class.II -> Star(t, c, 6.0, 13.AU, 0.3.AU, 0.1.AU, 6, 3.d6+1, -9)
                    Class.III -> Star(t, c, 2.7, 3.1.AU, 0.1.AU, AU.Zero, 6, 3.d6, -8)
                    Class.IV -> Star(t, c, 1.8, 1.AU, AU.Zero, AU.Zero, 7, 3.d6-1, -6)
                    Class.V -> Star(t, c, 1.1, 0.8.AU, AU.Zero, AU.Zero, 16, 3.d6-2, 0)
                    else -> Star(t, c, 0.8, 0.5.AU, AU.Zero, AU.Zero, 16, 2.d6+1, 1)
                }
                Type.K -> when(c) {
                    Class.Ia -> Star(t, c, 15.0, 125.AU, 2.5.AU, 3.AU, 10, 3.d6+2, -10)
                    Class.Ib -> Star(t, c, 12.0, 50.AU, 1.AU, 1.AU, 16, 3.d6+2, -10)
                    Class.II -> Star(t, c, 6.0, 13.AU, 0.3.AU, 0.2.AU,16, 3.d6+1, -9 )
                    Class.III -> Star(t, c, 3.0, 4.AU, 0.1.AU, AU.Zero, 16, 3.d6, -7)
                    Class.IV -> Star(t, c, 2.3, 1.AU, AU.Zero, AU.Zero, 16, 3.d6-1, -5)
                    Class.V -> Star(t, c, 0.9, 0.4.AU, AU.Zero, AU.Zero, 16, 3.d6-2, 0)
                    else -> Star(t, c, 0.5, 0.2.AU, AU.Zero, AU.Zero, 16, 2.d6+1, 1)
                }
                Type.M -> when(c) {
                    Class.Ia -> Star(t, c, 20.0, 100.AU, 2.AU, 7.AU, 16, 3.d6, -10)
                    Class.Ib -> Star(t, c, 16.0, 50.AU, 1.AU, 4.2.AU, 16, 3.d6, -10)
                    Class.II -> Star(t, c, 8.0, 16.AU, 0.3.AU, 1.1.AU, 16, 3.d6, -9)
                    Class.III -> Star(t, c, 4.0, 5.AU, 0.1.AU, 0.3.AU, 16, 3.d6, -6)
                    Class.V -> Star(t, c, 0.3, 0.1.AU, AU.Zero, AU.Zero, 16, 3.d6-2, 1)
                    else -> Star(t, c, 0.2, 0.095.AU, AU.Zero, AU.Zero, 16, 2.d6+2, 2)
                }
                else -> {
                    var original: Star
                    do {
                        original = create()
                    } while (original is WhiteDwarf)
                    WhiteDwarf(original.type, original.klass, original.planetsOn, original.numPotentialOrbits)
                }
            }
        }
    }
}
