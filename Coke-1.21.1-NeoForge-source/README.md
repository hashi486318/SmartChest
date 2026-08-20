# Coke for Minecraft 1.21.1 (NeoForge)

Open this folder as a Gradle project in IntelliJ IDEA and use JDK 21. The first Gradle sync downloads and prepares Minecraft, so it may take some time.

- `gradlew.bat build` builds the JAR.
- `gradlew.bat runClient` starts a development client.

The vanilla recipes and the `c:coal_coke` tag use the singular 1.21 data paths.
The Mekanism compatibility recipes are in `src/main/resources/data/mekanism/recipe/chemical_conversion` and use Mekanism 10.7's chemical-conversion recipe type.
