# Adding Braid Support to Your 1.20.1 Fabric Mod

## What is Braid?

Braid is a tool from the [Ornithe Project](https://ornithemc.net/) that enables multi-version support for Fabric mods. It allows you to compile your mod against one Minecraft version and automatically remap it to work on multiple versions, reducing the maintenance burden of supporting different Minecraft versions.

## Why Use Braid?

- **Multi-version support**: Write code once, support multiple Minecraft versions
- **Reduced maintenance**: No need to maintain separate branches for each version
- **Automatic remapping**: Braid handles the bytecode transformation between versions
- **Version-specific code**: Still allows version-specific implementations when needed

## Prerequisites

- A working 1.20.1 Fabric mod project
- Gradle 8.0 or higher
- Basic understanding of Fabric mod development

## Step-by-Step Setup

### 1. Add Braid to Your Build Script

First, add the Braid plugin to your `build.gradle`:

```groovy
plugins {
    id 'fabric-loom' version '1.10-SNAPSHOT'
    id 'maven-publish'
    // Add the Braid plugin
    id 'net.ornithemc.braid' version '1.0.0'
}
```

**Note**: Check the [Braid releases](https://github.com/OrnitheMC/braid/releases) for the latest version.

### 2. Configure Braid in settings.gradle

Add the Braid plugin repository to your `settings.gradle`:

```groovy
pluginManagement {
    repositories {
        maven {
            name = 'Fabric'
            url = 'https://maven.fabricmc.net/'
        }
        // Add Ornithe Maven repository for Braid
        maven {
            name = 'Ornithe'
            url = 'https://maven.ornithemc.net/releases'
        }
        gradlePluginPortal()
    }
}
```

### 3. Configure Target Versions

In your `build.gradle`, configure which Minecraft versions you want to support:

```groovy
braid {
    // Your primary development version
    primaryVersion = '1.20.1'
    
    // Additional versions to support
    targetVersions = ['1.20', '1.20.2', '1.20.4']
    
    // Optional: Configure remapping settings
    remapSettings {
        // Skip certain packages from remapping if needed
        skipPackages = []
        
        // Enable/disable certain features
        includeNested = true
    }
}
```

### 4. Update Dependencies (If Using owo-lib)

If your mod depends on owo-lib, ensure you're using a version that supports your target versions:

```groovy
dependencies {
    // Use the appropriate owo-lib version for 1.20.1
    modImplementation "io.wispforest:owo-lib:0.11.2+1.20"
    annotationProcessor "io.wispforest:owo-lib:0.11.2+1.20"
    
    // Include owo-sentinel for automatic dependency management
    include "io.wispforest:owo-sentinel:0.11.2+1.20"
}
```

**Important**: Check the [owo-lib releases](https://github.com/wisp-forest/owo-lib/releases) for versions compatible with your target Minecraft versions.

### 5. Handle Version-Specific Code

When you need version-specific implementations, Braid provides utilities to detect the runtime version:

```java
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.Version;

public class VersionSpecificCode {
    private static final String MC_VERSION = FabricLoader.getInstance()
        .getModContainer("minecraft")
        .orElseThrow()
        .getMetadata()
        .getVersion()
        .getFriendlyString();
    
    public static void doSomethingVersionSpecific() {
        if (MC_VERSION.startsWith("1.20.1")) {
            // 1.20.1 specific code
        } else if (MC_VERSION.startsWith("1.20.2")) {
            // 1.20.2 specific code
        } else {
            // Default implementation
        }
    }
}
```

### 6. Update fabric.mod.json

Ensure your `fabric.mod.json` specifies the correct version range:

```json
{
  "schemaVersion": 1,
  "id": "your_mod_id",
  "version": "${version}",
  "depends": {
    "fabricloader": ">=0.14.0",
    "fabric-api": "*",
    "minecraft": "~1.20",
    "java": ">=17"
  }
}
```

The `"minecraft": "~1.20"` notation means "1.20.x", allowing compatibility with all 1.20 minor versions.

### 7. Build Your Mod

Build your mod as usual:

```bash
./gradlew build
```

Braid will automatically generate remapped versions of your mod for each target version specified in your configuration.

### 8. Locate Output Files

After building, you'll find version-specific JARs in your `build/libs` directory:

```
build/libs/
├── yourmod-1.0.0-1.20.jar
├── yourmod-1.0.0-1.20.1.jar
├── yourmod-1.0.0-1.20.2.jar
└── yourmod-1.0.0-1.20.4.jar
```

## Advanced Configuration

### Custom Remapping Rules

For more control over the remapping process:

```groovy
braid {
    primaryVersion = '1.20.1'
    targetVersions = ['1.20', '1.20.2', '1.20.4']
    
    // Custom mappings for specific classes or methods
    customMappings {
        // Map old names to new names for specific versions
        version('1.20') {
            map('com.example.OldClass', 'com.example.NewClass')
        }
    }
    
    // Exclude certain files from remapping
    excludeFiles = [
        '**/mixin/**',  // Be careful excluding mixins
        '**/resources/**'
    ]
}
```

### Conditional Compilation

For more complex version-specific code, consider using a separate source set:

```groovy
sourceSets {
    main {
        java {
            srcDirs = ['src/main/java', 'src/main/java-common']
        }
    }
    
    // Version-specific source sets
    v1_20_1 {
        java {
            srcDirs = ['src/main/java-1.20.1']
        }
    }
}
```

## Troubleshooting

### Common Issues

#### 1. Build Fails with "Cannot find mappings for version X"

**Solution**: Ensure the target Minecraft version has available mappings. Check [FabricMC Yarn mappings](https://fabricmc.net/develop) for supported versions.

#### 2. ClassNotFoundException at Runtime

**Solution**: This often happens when version-specific classes are referenced. Use reflection or version checks to handle these cases:

```java
try {
    Class<?> versionSpecificClass = Class.forName("net.minecraft.something.VersionSpecific");
    // Use the class
} catch (ClassNotFoundException e) {
    // Fallback implementation
}
```

#### 3. Mixins Not Working Across Versions

**Solution**: Mixins can be tricky with multi-version support. Consider:
- Using version-specific mixin configurations
- Testing each target version thoroughly
- Keeping mixins minimal and version-agnostic when possible

#### 4. Dependency Conflicts

**Solution**: Ensure all your dependencies (including owo-lib) support your target versions. Use version catalogs or properties to manage versions:

```groovy
// gradle.properties
owo_version_1_20=0.11.2+1.20
owo_version_1_20_1=0.11.2+1.20

// build.gradle
dependencies {
    def owoVersion = project.hasProperty('targetMcVersion') 
        ? project["owo_version_${project.targetMcVersion}"]
        : project.owo_version_1_20_1
    
    modImplementation "io.wispforest:owo-lib:${owoVersion}"
}
```

## Testing

Always test your mod on each target version:

1. Build your mod with Braid
2. Install each version-specific JAR in its corresponding Minecraft version
3. Test all features thoroughly
4. Pay special attention to:
   - Version-specific code paths
   - Mixin applications
   - Resource loading
   - Network packet handling

## Best Practices

1. **Start with a narrow version range**: Begin with 2-3 closely related versions (e.g., 1.20, 1.20.1, 1.20.2) before expanding.

2. **Keep version-specific code minimal**: The less version-specific code you have, the easier maintenance becomes.

3. **Use abstraction layers**: Create interfaces for version-specific implementations:

```java
public interface VersionAdapter {
    void doVersionSpecificThing();
}

// Load the appropriate implementation based on version
public class VersionAdapterFactory {
    public static VersionAdapter create() {
        String version = getMinecraftVersion();
        if (version.startsWith("1.20.1")) {
            return new VersionAdapter_1_20_1();
        }
        return new VersionAdapter_1_20();
    }
}
```

4. **Document version differences**: Keep notes about what changes between versions, especially breaking changes in Minecraft's API.

5. **Automated testing**: Set up CI/CD to test against all target versions automatically.

## Alternative Approaches

If Braid doesn't fit your needs, consider these alternatives:

- **Multi-version branches**: Maintain separate Git branches for each version
- **Architectury**: A multi-loader (Fabric/Forge/Quilt) platform that also handles version differences
- **Stone Cutter**: Another multi-version build tool for Minecraft mods
- **Preprocessor comments**: Use a preprocessor to handle version-specific code at build time

## Resources

- [Braid GitHub Repository](https://github.com/OrnitheMC/braid)
- [Ornithe Project](https://ornithemc.net/)
- [Fabric Documentation](https://fabricmc.net/wiki/)
- [owo-lib Documentation](https://docs.wispforest.io/owo/)
- [FabricMC Discord](https://discord.gg/v6v4pMv) - Get help from the community

## Contributing

If you find issues with this guide or have improvements, please:
1. Open an issue on the [owo-lib GitHub repository](https://github.com/wisp-forest/owo-lib/issues)
2. Submit a pull request with corrections or additions
3. Join the [Wisp Forest Discord](https://discord.gg/xrwHKktV2d) for discussion

---

**Note**: Braid is an evolving tool. Always check the [official Braid documentation](https://github.com/OrnitheMC/braid) for the most up-to-date information and features.
