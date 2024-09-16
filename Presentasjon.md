---
marp: true
theme: uncover
paginate: true
_paginate: false
transition: fade
---
# Testcontainers

Presentasjon Javabin Bergen 19/9-2024
Sondre Eikanger Kvalø - Sonat Consulting Bergen
@zapodot https://github.com/zapodot

---
<!-- 
header: 'Testcontainers'
footer: 'Javabin Bergen 19/9-2024'
-->

# Plan for dagen
 1. Hva er testcontainers?
 1. Hvordan gjorde vi testing før?
 1. Fordeler og ulemper
 1. Testcontainers 101 med eksempler
 1. Konklusjon

---
### Hva er en container?
[![width:1000](assets/oci.png)](https://github.com/opencontainers/image-spec/releases/download/v1.1.0/oci-image-spec-v1.1.0.pdf)
Kilde: *Open Container Initiative Image spec v 1.1.0*
<!--
OCI - specen definerer et image som summen av lag, index og config. Et container image skal kunne kjøre isolert og skal kun ha tilgang til bibliotek og kommandoer som er lagt inn i imaget 
-->

---
![width:1000](assets/docker-architecture.png)
Kilde: *[Docker overview]
(https://docs.docker.com/get-started/docker-overview/)*

<!--
Docker er den mest kjente container runtime brukt lokalt på utviklermaskiner og servere. Støtter også OCI. Docker Desktop er propritært mens selve dockerd er opensource. Et OpenSource alternativ er Podman Desktop 
Kubernetes bygger også på bruk av container images definert i henhold til OCI-standarden 
 -->

---
![image](https://testcontainers.com/getting-started/images/test-workflow.png)


---

### Hva menes med test?
>Kode som kjører som en del av standard bygging av et kodeprosjekt som tester ulike utfall som kan gjøres med produksjonskoden

---

### Ulike former for tester
- _enhetstest_ - tester en enkelt funksjon eller klasse. Fokus på f.eks grenseverdier. Mock-er alle avhengigheter
- _komponenttest_ - blackbox testing av en enkelt komponent. Mocker typisk alle eksterne komponenter
- _integrasjonstest_ - tester som fokuserer på grensesnittet mellom egen kode og tredjepart

---


### Hvordan gjorde vi dette før?
- Testet mot faktisk test/produksjonsmiljø
- Kjørte mocks/stubs som lot deg dekke en del av behovet (f.eks H2Database i kompabilitetsmodus)

---

### Funksjonalitet
- Kan kjøre på 
    - Docker (Desktop)
    - Podman i med docker emulering
    - embedded runtime basert på Alpine Linux (eksperimentell)
    - _Testcontainers cloud_
<!-- Siden Docker inc har kjøpt opp rettighetene til testcontainers er det rimelig å anta at de kommer til å bruke Docker Build Cloud på sikt -->

---
### Kjøre containere i sky
- Trenger ikke ha en container runtime installert
- Pay-as-you-go modell
- Kanskje mest nyttig i CI-sammenheng?
- Etter at Docker Inc kjøpte opp Testcontainers kan vi anta at det vil henge sammen med deres cloud-løsning
---
### Fordeler ved å skrive tester som bruker testcontainers
- Får testet integrasjonskode mot noe som ligner veldig på det du bruker i produksjon
- Får testet kode som er strenger i kodebasen, f.eks SQL som kompilatoren ikke har et forhold til
---
### Ulemper med å ha tester som bruker testcontainers
- Tar lengre tid å kjøre testene
- Lett for at man ungår å refaktorere koden som integrerer mot tredjepart
---

### Eksempel: enkelt container Java/JUnit 5
```java

@Testcontainers(disabledWithoutDocker = true)
public class ContainerBaseRepositoryTests {

    @Container
    private PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:12.20-alpine");

    @Test
    void testnoemotdatabasen() {
        final var dataSourceConfig = new DataSourceConfig(
                postgreSQLContainer.getJdbcUrl(),
                postgreSQLContainer.getUsername(),
                postgreSQLContainer.getPassword()
        //...
    }

}
 ```
<!-- Viser i IDEA eksempel både for Java/JUnit 5 og Kotlin/kotest-->
---
### Eksempel: enkelt container Kotlin/kotest
```kotlin 

class UserReadRepositoryTest : StringSpec({
    val postgres = PostgreSQLContainer("postgres:12.20-alpine")

    listeners(postgres.perSpec())

    "Test databasekode" {
        val exposedConnection = Database.connect(
            url = postgres.jdbcUrl,
            driver = postgres.driverClassName,
            user = postgres.username,
            password = postgres.password
        )

        //...

     }
})


```

---

### Koble sammen containere (code smell?)
```java
try (
    Network network = Network.newNetwork();
    GenericContainer<?> foo = new GenericContainer<>(TestImages.TINY_IMAGE)
        .withNetwork(network)
        .withNetworkAliases("foo")
        .withCommand(
            "/bin/sh",
            "-c",
            "while true ; do printf 'HTTP/1.1 200 OK\\n\\nyay' | nc -l -p 8080; done"
        );
    GenericContainer<?> bar = new GenericContainer<>(TestImages.TINY_IMAGE)
        .withNetwork(network)
        .withCommand("top")
) {
    foo.start();
    bar.start();

    String response = bar.execInContainer("wget", "-O", "-", "http://foo:8080").getStdout();
    assertThat(response).as("received response").isEqualTo("yay");
}
```
---
### Konklusjon
- Tester som er avhengig av en tredjepart er ikke enhetstester men integrasjonstester
- Kan velge å skille ut testene som krever containere
- Enhetstester er fremdeles viktig for å sikre at man opprettholder så løs kobling som mulig
