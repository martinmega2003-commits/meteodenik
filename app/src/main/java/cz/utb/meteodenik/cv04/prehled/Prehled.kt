package cz.utb.meteodenik.cv04.prehled

data class Mesto(val nazev: String, val teplota: Double) {
    val popis: String
        get() = "$nazev: ${teplota.jakoCelsia()}"
}

fun Double.jakoCelsia(): String = "$this C"

sealed interface StavPrehledu {
    data object Nacita : StavPrehledu
    data object Chyba : StavPrehledu
    data object Prazdno : StavPrehledu
    data class Hotovo(val mesta: List<Mesto>) : StavPrehledu
}

fun textStavu(stav: StavPrehledu): String = when (stav) {
    StavPrehledu.Nacita -> "Načítám…"
    StavPrehledu.Chyba -> "Chyba"
    StavPrehledu.Prazdno -> "Nic tu není"
    is StavPrehledu.Hotovo -> "Měst: ${stav.mesta.size}"
}

class Prehled(private val mesta: List<Mesto>) {
    val stav: StavPrehledu
        get() = if (mesta.isEmpty()) StavPrehledu.Prazdno else StavPrehledu.Hotovo(mesta)

    fun nejteplejsi(): Mesto? = mesta.maxByOrNull { it.teplota }

    fun vypis() {
        mesta.forEach { println(it.popis) }
        println(textStavu(stav))
        nejteplejsi()?.let { println("Nejtepleji: ${it.nazev} (${it.teplota.jakoCelsia()})") }
    }
}

fun main() {
    Prehled(
        listOf(
            Mesto("Zlín", 21.4),
            Mesto("Brno", 23.0),
            Mesto("Otrokovice", 20.9),
        )
    ).vypis()
}