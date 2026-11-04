package cz.utb.meteodenik.model

data class Mesto(
    val nazev: String,
    val teplota: Double
)


val UKAZKOVA_MESTA = listOf(
    Mesto("Zlín", 21.4),
    Mesto("Brno", 23.1),
    Mesto("Ostrava", 19.8)
)