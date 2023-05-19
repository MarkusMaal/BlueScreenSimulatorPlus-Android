# Sinise ekraani simulaator pluss Androidile
Tere tulemast sinise ekraani simulaatori Android versiooni! See on lihtne utilliit, mis võimaldab simuleerida erinevaid veaekraane erinevatest Windowsi versioonidest.

## Eelseadistused
See äpp kasutab eelseadistusi, mis sisaldavad sätteid enda kohta (Kasutaja konfiguratsioonid) ning milliseid opsüsteeme nad peaksid kasutama ja seeläbi millised suvandid on kasutajale nähtavad (OS mall).
Neid eelseadistusi talletatakse loendis, mida saab salvestada konfiguratsioonifaili.

![image](file:///android_asset/drawable/structure)

Vaikeseadistused on järgmised:

* Windows 11 (Native, ClearType) [Windows 11]
* Windows 10 (Native, ClearType) [Windows 10]
* Windows 8/8.1 (Native, ClearType) [Windows 8/8.1]
* Windows 7 (640x480, ClearType) [Windows 7]
* Windows Vista (640x480, Standard) [Windows Vista]
* Windows XP (640x480, Standard) [Windows XP]
* Windows 2000 Professional/Server Family (640x480, Standard) [Windows 2000]
* Windows NT 4.0/3.x (Text mode, Standard) [Windows NT 3.x/4.0]
* Windows CE 3.0 and later (750x400, Standard) [Windows CE]
* Windows 9x/Millennium Edition (Text mode, Standard) [Windows 9x/Me]
* Windows 3.1 (Text mode, Standard) [Windows 3.1x]
* Windows 1.x/2.x (Text mode, Standard) [Windows 1.x/2.x]

Kõiki neid seadistusi saab valida rippmenüüst, mis asub peamenüüs.

Iga eelseadistuse valimisel kuvatakse erinev arv sinise ekraani valikuid.
Nagu varem mainitud, kõiki neid suvandeid talletatakse eelseadistuses, mis tähendab, et
muudatused, mis tehakse ühes eelseadistuses, EI liigu edasi teisele suvandile
(nt kui keelate vesimärgi mingis eelseadistuses, jääb see sisselülitatuks teistes eelseadistustes).

## Kasutajaliides
Pärast rakenduse avamist näete kasutajaliidest, mis võimaldab teil valida sinise ekraani, mis vastab teie vajadustele!

![Kasutajaliides](file:///android_asset/drawable/help_ui)

Esiteks saate valida operatsisoonisüsteemi esimesest rippmenüüst. Nagu varem mainitud,
on tegelikult tegemist konfigureeritavate mallidega, millega opsüsteemid on seotud.

Pärast eelseadistuse valimist, saate kohandata järgmisi sätteid, olenevalt sellest, millise
valite:

### Veakood
See valik teeb kindlaks, milline veakood ilmub sinisel ekraanil. Kui soovite kasutada kohandatud koodi, lülitage sisse kohandatud veakood, misjärel näete järgmist hüpikakent:

![Kohandatud veakoodi aken](file:///android_asset/drawable/help_custom_error)

Siit saate määrata kohandatud vea kirjelduse ja heksakoodi. Heksakoodis võivad olla ainult numbrid (0-9) ja tähed A kuni F. Toksake "OK", et kinnitada kohandatud veakood. Kui te seda teete, ei ole võimalik teil valida muud veakoodi enne, kui lülitate kohandatud veakoodi välja.

### Automaatne sulgemine
Sisselülitatult, sulgeb simulaatori pärast progressi lõppu. See valik pole saadaval iga opsüsteemi jaoks.

### Vesimärk
Vaikesättena, Sinise ekraani simulaator pluss kuvab vesimärgi veaekraani peal ja keskel, mis annab kasutajale märku, et tegemist on simulatsiooniga, mitte päristõrkega. Kui te ei soovi vesimärki näha, lülitage "Kuva vesimärk" valik välja.

### Windows 10/11 valikud
Windows 10-s ja 11-s näete järgmisi valikuid:

* Eelvaate järk - Kuvab rohelise ekraani sinise asemel ja asendab PC/device tekstiga "Windows Insider Build"
* Serveri sinine ekraan - Peidab emootikoni ":(" veateateekraanilt
* Must ekraan (ainult W11) - Kuvab sinise asemel musta ekraani (nagu mõnedes Windows 11 järkudes)
* Asenda "PC" tekstiga "device" (ainult W10) - Uuemates Windows 10 versioonides, kuvab veaekraani "PC" asemel teksti "device". See valik võimaldab kahe vahel lülituda.
* Kuva parameetrid - Kuvab ülemises vasakus nurgas veaparameetrid
* Vea põhjustanud fail - Kuvab probleemse faili veakoodi all (nt What failed: wdfilter.sys). Saate konkreetse faili nime määrata "MÄÄRA PROBLEEMNE FAIL" nupuga. See avab dialoogi, kus saate kas kirjutada või valida faili.
* Kuva vea üksikasjad - Kui sisselülitatud, kuvab veakirjelduse, väljalülitatult kuvab tegeliku koodi

Mõned neist valikutest eksisteerivad ka Windows 8/8.1 mallides.

### Windows XP/Vista/7 valikud
Konkreetsed valikud nende kolma opsüsteemi jaoks. Ainus erinevus Vista ja 7 vahel on vaikefont, mida kasutatakse.

* Kuva vea üksikasjad - Sisselülitatult kuvab vea kirjelduse esimese lõigu all. Väljalülitatult ei kuvata veakirjeldust.
* Kuva probleemne fail - Sisselülitatult kuvab faili, kuvab faili, mis võis vea põhjustada, veakirjelduse/esimese lõigu all
* Kuva lisainfo probleemse faili kohta - Sisselülitatult kuvab lisainfo faili kohta stop koodi all
* Kohandatud veakood

### Windows NT 4.0/3.x valikud
Windows NT-l on kompleksne veaekraan, mitme erineva valikuga, mida saab kohandada.

* Vilkuv kursor - Sisselülitatult vilgutab ekraani ülemises vasakus osas kriipsu
* AMD protsessor - Näitab "AuthenticAMD" teksti "GenuineIntel" asemel
* Kuva kuhila jälg - Lülitab sisse/välja listi failidest, millega on seotud veakoodid. Seda saab kohandada "NT KOODIREDAKTOR" menüüs.
* Vea põhjustanud fail
* Kohandatud veakood