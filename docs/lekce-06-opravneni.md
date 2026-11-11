@'
# Lekce 06 – Žádost o polohu (cvičení 6.2)

Oprávnění: `ACCESS_COARSE_LOCATION`, kód v `PolohaKarta.kt`, karta je na obrazovce seznamu měst.

## Čtyři stavy

| # | Stav | Jak se do něj dostanu | Co uživatel vidí | Screenshot |
|---|---|---|---|---|
| 1 | `NEZNAMY` | první spuštění, ještě jsme se neptali | tlačítko „Zjistit moji polohu“ → hned systémový dialog | ![](lekce-06-stav1-neznamy.png) |
| 2 | `ODMITNUTO` | jednou „Don't allow“ | text „Polohu jste zatím nepovolili“ + tlačítko; po klepnutí nejdřív naše vysvětlení a tlačítko Pokračovat, teprve pak dialog | ![](lekce-06-stav2-odmitnuto.png) |
| 3 | `ODMITNUTO_NATRVALO` | podruhé „Don't allow“ | text, že vše ostatní funguje i bez polohy, + tlačítko „Otevřít nastavení“; systémový dialog už se neukáže | ![](lekce-06-stav3-natrvalo.png) |
| 4 | `UDELENO` | povolení v dialogu, nebo v Nastavení a návrat | „Poloha je povolena“ + souřadnice, nebo text, že poloha zatím není k dispozici (null) | ![](lekce-06-stav4-udeleno.png) |

## Proč se trvalé zamítnutí pozná až v callbacku

`shouldShowRequestPermissionRationale()` vrací `false` ve dvou situacích: před první žádostí i po trvalém zamítnutí. Při startu je proto nerozlišíme. Až v callbacku spouštěče víme, že žádost právě proběhla – když pak přijde `false` a rationale je taky `false`, je to trvalé zamítnutí. Když je rationale `true`, uživatel odmítl jen jednou a příště dostane vysvětlení.

## Proč COARSE a ne FINE

- Přibližná poloha (`ACCESS_COARSE_LOCATION`) má přesnost zhruba 3 km² – na určení města pro počasí to stačí.
- Přesná poloha (`ACCESS_FINE_LOCATION`) bývá přesná zhruba na 50 m – to potřebuje navigace, ne počasí.
- Princip nejmenších oprávnění: nežádáme víc, než funkce potřebuje, a uživatel spíš souhlasí.
- Od Androidu 12 si uživatel stejně může zvolit jen přibližnou polohu; FINE se musí žádat vždy spolu s COARSE.

Zdroj: https://developer.android.com/develop/sensors-and-location/location/permissions

## Ošetření

- Oprávnění se kontroluje (`checkSelfPermission`) před každým čtením polohy.
- `getLastKnownLocation(NETWORK_PROVIDER)` je v `try/catch (SecurityException)`.
- Když je poloha `null`, zobrazí se text ze zdrojů a tlačítko „Načíst znovu“.

## Reset pro testování

    adb shell pm revoke cz.utb.meteodenik android.permission.ACCESS_COARSE_LOCATION
    adb shell pm clear-permission-flags cz.utb.meteodenik android.permission.ACCESS_COARSE_LOCATION user-set user-fixed
'@ | Set-Content -Encoding UTF8 docs\lekce-06-opravneni.md