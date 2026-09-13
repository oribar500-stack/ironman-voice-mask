UPLOAD ONLY build.gradle
========================

Replace the build.gradle at the ROOT of the GitHub repository with this file.

Do NOT upload .github.
Do NOT edit gradle.properties.
Do NOT press Re-run on an old build.

After Commit changes, GitHub will create a NEW build automatically.

This build.gradle:
1. downloads the exact GeckoLib 5.4.5 Fabric 1.21.11 JAR from Modrinth,
2. checks the JAR really contains com/geckolib/renderer/GeoArmorRenderer.class,
3. passes that concrete JAR directly to Fabric Loom.

This removes the Maven dependency-resolution ambiguity that caused builds #5-#8.
