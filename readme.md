# SystemInfo-Widget - App for Android

SystemInfo-Widget ist ein Widget, das dir Informationen über dein Smartphone/Tablet anzeigt. Dabei hast du die Möglichkeit, aus 7 Kategorien zu wählen.

Website: [SystemInfo-Widget - Produkt-Website](http://systeminfowidget.manuzid.de/)

## Development and Build informationen

Zum starten der App muss nichts besonderes beachtet werden.

* Java Version: 1.7
* CompileSdkVersion: 26
* BuildToolsVersion: '26.0.2'
* ApplicationId: "com.manuzid.systeminfowidget"
* MinSdkVersion: 17
* TargetSdkVersion: 26

**Dependencies**

* com.android.support:appcompat-v7:26.0.0
* org.jetbrains:annotations-java5:15.0
* io.github.kobakei:ratethisapp:1.2.0
    * Quelle: [Android-RateThisApp](https://github.com/kobakei/Android-RateThisApp)

## Release History

* v3.2.7/v3.2.7v2
    * Fehler behoben wobei es beim Zugriff auf den Externen-Speicher zu Problemen kam
    * Build Gradle Version angehoben
    * Reihenfolge der Farbeinstellungen korrigiert
* v3.2.6
    * Neuen AppRater Dialog implementiert
    * Kleinere Bugs behoben und Typos entfernt
    * Kamera Support für SDK größer gleich 21 implementiert
* v3.2.5
    * Einstellungs-Activity überarbeitet, die Verwaltungen der Eigenschaften wird nun vom System übernommen und geschieht nicht mehr manuell
* v3.2.4
    * Lauftext Bug kann nicht behoben werden
    * Gradle und Android für SDK 26 angehoben und angepasst
    * Deprecated Code behandelt
* v3.2.3
    * Kategorie Icons überarbeitet
* v3.2.2
    * Hinzufügen der neuen Netzwerk-Kategorie
    * Benutzer kann nun unter Einstellungen die Kategorien aussuchen
    * Einstellungs-Activity optimiert
    * Aktualisierung des Widgets weiter verfeinert, es reagiert nun auf weitere Events
* v3.0.1
    * Min SDK-Version von 13 auf 17 angehoben
    * Deprecated Code, in der "More" Kategroie behandelt 
    * Information ausgetauscht, CPU2 entfernt und dafür RAM-Speicher hinzugenommen
    * Deprecated Code, in der "Memory" Kategroie behandelt 
* v3.0.0
    * Überarbeiten der Software-Architektur, sauberes Design
    * Kategorien dahingehend erweitert das nun der App dynamisch weitere Kategorien hinzugefügt werden können
    * Nicht mehr verwendetes entfernt
* v2.1.0
    * Beheben des Problems das bei Aufruf des Reiters "Speicher" das Widget abstürzt
    * Beheben des Problems, dass das Widget im Menü nicht angezeigt wurde
    * Beheben des Problems das nicht der korrekte SD-Karten Wert bei den Samsung Galaxy S3 angezeigt wird
    * Verfügbar für Nexus 7
    * Hinzufügen einer neuen Farbe: Schwarz
    * Beheben des Update-Problems (aus den Einstellung kommend)
    * Beheben des Fehlerhaften Links unter "Mehr Apps"

## Best Practices

**Kategorie hinzufügen**

    * Kategorie-Klasse unter 'com.manuzid.systeminfowidget.category' anlegen, diese muss von der Klasse: 'com.manuzid.systeminfowidget.category.AbstractCategory' erben
    * Diese muss folgende Kriterien erfüllen
        * ein neues Icon in den entsprechenden Farben
        * ein IntentFilter der auch in der AndroidManifest bekannt gegeben werden muss (in der Klasse eine Konstante)
        * ein RequestCode
        * in der array.xml muss 'config_categories' und 'config_categories_keys' entsprechend erweitert werden
    * In der 'ConfigPreferencesActivity' muss das Set 'categorySelection' mit der Konstante aus der neuen Kategorie-Klasse erweitert werden