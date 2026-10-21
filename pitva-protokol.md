# Cvičení 4.1 – Dekompilační protokol

Soubor: `app/src/main/java/cz/utb/meteodenik/cv04/pitva/Pitva.kt`

Poznámka: tlačítko Decompile v Android Studiu (Rabbit 2026.2.1) padalo na chybě IDE,
údaje jsem proto ověřil v panelu Tools › Kotlin › Show Kotlin Bytecode.

## Tabulka

| Konstrukce | Co jsem napsal | Co vygeneroval kompilátor | Řádků v Javě ručně (odhad) |
|---|---|---|---|
| data class | `data class Zapis(mesto, teplota, poznamka = "")` | konstruktor + syntetický konstruktor s `int` a `DefaultConstructorMarker`, gettery `getMesto/getTeplota/getPoznamka`, `component1–3`, `copy`, `copy$default`, `toString`, `hashCode`, `equals` | ~70 |
| object | `object Prevody` | `public static final Prevody INSTANCE`, privátní konstruktor, `public static final double ABSOLUTNI_NULA` | ~10 |
| extension funkce | `fun Double.zaokrouhlenoNaDesetiny()` | `public static final double zaokrouhlenoNaDesetiny(double $this$zaokrouhlenoNaDesetiny)` ve třídě `PitvaKt` | ~5 |
| lambda jako parametr | `vyber: (Zapis) -> Double` | `public static final double prumer(List zapisy, Function1 vyber)` | ~10 |
| suspend funkce | `suspend fun nactiPoZdrzeni(...)` | `public static final Object nactiPoZdrzeni(List zapisy, Continuation $completion)` + stavový automat se `switch` a `label` | ~40 |

## Odpovědi

**1. Kolik metod má třída Zapis a které nemají ve zdrojáku ani náznak?**
Třída má 11 metod: 3 gettery, `component1`, `component2`, `component3`, `copy`, `copy$default`,
`toString`, `hashCode` a `equals`. Ve zdrojáku jsem napsal jen tři vlastnosti, takže všechny
metody kromě getterů (a i ty jen nepřímo přes `val`) dopsal kompilátor kvůli slovu `data`.

**2. Proč má Zapis konstruktor navíc s `int` a `DefaultConstructorMarker`?**
Protože `poznamka` má výchozí hodnotu `""`. JVM výchozí hodnoty parametrů nezná, a proto
kompilátor vyrobí pomocný konstruktor. Číslo `int` je bitová maska, která říká, které
argumenty volající vynechal, a ty pak konstruktor doplní výchozími hodnotami.

**3. Jaký je první parametr `zaokrouhlenoNaDesetiny` a co z toho plyne?**
Prvním parametrem je samotné číslo (`double $this$zaokrouhlenoNaDesetiny`) a funkce je
obyčejná statická metoda ve třídě `PitvaKt`. Do třídy `Double` se nic nepřidalo.
Statická metoda není polymorfní: která funkce se zavolá, rozhodne kompilátor podle
deklarovaného typu už při překladu, ne podle skutečného typu objektu za běhu.

**4. Jaký typ má druhý parametr funkce `prumer`?**
Typ `Function1` (rozhraní `kotlin.jvm.functions.Function1`). Lambda se v JVM stane
objektem, který implementuje rozhraní s jedinou metodou `invoke`, a funkce `prumer` ji
volá jako `vyber.invoke(zapis)`. JVM tedy funkci jako hodnotu reprezentuje objektem.

**5. Co se stalo s návratovým typem `nactiPoZdrzeni` a proč je tam `switch`?**
Návratový typ se změnil z `List` na `Object` a přibyl skrytý parametr `Continuation`.
Funkce totiž může vrátit buď výsledek, nebo značku `COROUTINE_SUSPENDED` („pozastavuji se“).
Kompilátor z ní udělal stavový automat: `label` si pamatuje, kde funkce skončila, a `switch`
podle něj po probuzení skočí na správné místo.