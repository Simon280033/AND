# AND
<b>Applikationsnavn:</b> <br/>
FellowShippers

<b>VIGTIGT:</b><br/>
-Kode er under master branch<br/>
-For at kunne bruge applikationen, SKAL du give den tilladelse til lokation samt storage (Den prompter dig i starten)

<b>Motivation:</b> <br/>
Onlinehandel oplever på nuværende tidspunkt enorm vækst, grundet COVID-19 epidemien og dens medfølgende problematikker angående traditionelle former for handel.
For mange shoppere er forsendelsesafgiften en kilde til frustration, da det er en afgift man som fysisk shopper ikke er vant til at betale.
Mange onlinebutikker giver dog mulighed for gratis forsendelse, ved køb over et større beløb.
"FellowShippers" gør det muligt for brugeren, at finde andre personer i nærheden, der har tænkt sig at handle i samme webshop som dem selv.
En bruger kan for eksempel slå en annonce op for Zalando.dk, hvor tingene kan hentes på dennes adresse (kan vælges i google maps). Der kan derefter stå hvor mange penge der yderligere
skal handles for, for at få gratis forsendelse. Brugere kan koble sig på med links til de ting de vil have med, og deres pris. Hvis ejeren accepterer, tilføjes de til shippingen, og kan betale deres andel.
De vil derefter blive notificeret når pakken er ankommet til personen der startede annoncen, så de kan komme og hente den. Brugere kan naturligvis anmeldes, så man kan vælge om man vil handle sammen med en,
baseret på deres pålidelighed. Ting så som track and trace links og kvitteringer, kan lægges op på annoncen, så de andre brugere kan se hvor langt pakken er etc. 

<b>MoSCoW requirements:</b>

<b>Must have:</b><br/>
-Mulighed for at oprette/se/tilkoble sig annoncer med filtre så som butik/lokation/dato/pengemængde. 
<br/>-Mulighed for at se basic information så som ejeren af annoncen, deres ry/profil etc
<br/>-Mulighed for at oprette en bruger

<b>Should have:</b><br/>
-Mulighed for at se afhentningslokationer på et kort med Google maps
<br/>-Mulighed for at se hvor mange handler en bruger har været med i, samt at kunne anmelde en bruger hvis de har været gode/dårlige
<br/>-Mulighed for at rapportere en bruger hvis de har snydt etc.
<br/>-Customizable profiler med info/avatar etc

<b>Could have:</b><br/>
-Integreret chat system på annoncen
<br/>-Embedded mobilepay system som kan håndtere om betalinger er foretaget

<b>Wont have:</b><br/>
-Reklamer baseret på hvilke webshops brugeren har vist interesse for
<br/>-In-app penge
<br/>-Premium features for betalende brugere

<b>Konklusion:</b><br/>
Efter at have udviklet applikationen, kan det konkluderes, at de requirements der var mulige at inkludere, blev inkluderet med success.
<br/>-Man kan oprette en bruger og customize den.
<br/>-Man kan oprette FellowShips samt joine eksisterende
<br/>-Man kan filtrere FellowShips, og finde dem der passer én, inklusiv med afstand til afhentning og dage indtil deadline
<br/>-Man kan vælge hvem man vil acceptere til sit FellowShip
<br/>-Man kan få en oversigt over ens FellowShips
<br/>-Man kan chatte med folk hvis FellowShips man deltager i
<br/>-Man kan rapportere brugere der evt snyder
<br/>-Man kan kommentere på brugeres profiler efter at have afsluttet et FellowShip med dem

<b>Requirements der ikke var mulige at implementere:</b><br/>
-Mobilepay understøttelse. Egnet til virksomheder, større process med at få adgang til deres API. Blev vurderet for langt uden for scope til bruge tid på, da det tilsyneladende ikke var lige til.
<br/>-Google Maps understøttelse. Jeg havde regnet med at jeg bare kunne bruge Google maps frit ligesom alle de andre Google tjenester jeg brugte, såsom Authenticator, Realtime database samt storage. Men det er tilsyneladende kommet bag en PayWall, og ville ikke til at fedte rundt med trial periods etc.

<b>Issues:</b><br/>
-Når man har oprettet sig, fungerer det meste som det skal. Jeg oplevede nogle uventede problemer med oprettelse af nye brugere, hvor der ikke er tilknyttet et billede til en google konto/email. Jeg opdagede det desværre alt for sent, så jeg kunne kun nå at lave en hurtigt hacky løsning, der er lidt dodgy. Det skulle dog ikke være et problem hvis den bruger du signer up med har en avatar tilknyttet.
<br/>-NavigationDrawer NÆGTER simpelthen at virke. Jeg kan få det til at virke på andre apps hvis jeg lavet et nyt Android Studio projekt, men der er et eller andet der gør, at det ikke vil virke på denne her. Det er en skam, for det ville se fedt ud, men jeg valgte at fokusere på vigtigere ting.

<b>YouTube demonstrations video:</b><br/>
https://youtu.be/EHzyaAj0B9k

<b>Ting jeg ville have gjort hvis jeg havde mere tid:</b><br/>
-Brugt recyclerviews i stedet for listviews. Jeg kom i tanke om det på deadline dagen, og med det oventstående problem (under Issues linje 54), blev jeg enig med mig selv om ikke at pille ved noget der virkede på deadline dagen. Gør ingen forskel alligevel, så længe der ikke er mange FellowShips (hvilket der ikke er her under demonstrationen, men det ville være smart at bruge recyclerviews "i virkeligheden")
<br/>-Gemt filter præferencer på lokal storage.

Overordnet set, er jeg glad for at være kommet i mål med noget der virker nogenlunde. Fandt hurtigt ud af at jeg havde sat ambitionerne højt, og der har været arbejdet på højtryk. Jeg glæder mig til en pause fra Firebase!!

-Simon

