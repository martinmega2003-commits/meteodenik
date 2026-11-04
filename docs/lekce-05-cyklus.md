# Lekce 05 – tabulka životního cyklu

| Scénář | Můj předpoklad | Skutečnost z Logcatu | Proč se to liší |
|---|---|---|---|
| 1. Start aplikace z ikony | onCreate -> onStart -> onResume | onCreate (savedInstanceState = null) -> onStart -> onResume | Shoduje se. Aktivita vzniká poprvé, proto nemá uložený stav (null). |
| 2. Rotace displeje | onPause -> onStop -> onStart -> onResume | onPause -> onStop -> onDestroy (isFinishing = false) -> onCreate (savedInstanceState = Bundle) -> onStart -> onResume | Otočení je změna konfigurace, systém starou instanci zruší a postaví novou s novým rozvržením. isFinishing = false, protože aktivita nekončí, jen se přestavuje; stav předá v Bundle. |
| 3. Stisk Home | onPause -> onStop | onPause -> onStop | Shoduje se. Aktivita není vidět, ale zůstává v paměti na pozadí. |
| 4. Návrat do aplikace | onRestart -> onStart -> onResume | onRestart -> onStart -> onResume | Shoduje se. Vrací se stejná instance, proto onRestart a žádné onCreate. |
| 5. Stisk Zpět na MainActivity | onPause -> onStop -> onDestroy | onPause -> onStop | Od Androidu 12 Zpět na kořenové spouštěcí aktivitě aktivitu neukončí, jen přesune úlohu na pozadí (jako Home). |
| 6. adb shell am kill na pozadí + návrat | onDestroy, pak onCreate -> onStart -> onResume | PROCESS ENDED, žádné onDestroy; po návratu nový proces: onCreate (savedInstanceState = Bundle) -> onStart -> onResume | Proces byl ukončen natvrdo, systém nezavolá žádný callback (onDestroy ani isFinishing nepřišly). Stav uložil už při odchodu na pozadí a vrátí ho v Bundle nové instanci. |