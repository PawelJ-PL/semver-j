# SemVer-J

## Description

Library for representation of [semantic version](https://semver.org/)

## Usage

### Examples:

```java
import com.github.pawelj_pl.semver_j.SemanticVersion;

SemanticVersion version1 = SemanticVersion.fromString("0.1.16-BETA+1000a");
SemanticVersion version2 = new SemanticVersion(0, 2, 20, "BETA", "2000a");

Integer major = version1.getMajor(); // value 0
Integer minor = version1.getMinor(); // value 1
Integer patch = version1.getPatch(); // value 16
Optional<String> preRelease = version1.getPreRelease(); // value Optional of BETA
Optional<String> buildMetadata = version1.getBuildMetadata(); // value Optional of 1000a

SemanticVersion v3 = version1.newMajor(3); // value SemanticVersion(3, 1, 16, "BETA", "1000a")
SemanticVersion v4 = version1.newMinor(3); // value SemanticVersion(0, 3, 16, "BETA", "1000a")
SemanticVersion v5 = version1.newPatch(3); // value SemanticVersion(0, 1, 3, "BETA", "1000a")
SemanticVersion v6 = version1.newPreRelease("ALFA"); // value SemanticVersion(0, 1, 16, "ALFA", "1000a")
SemanticVersion v7 = version1.newBuildMetadata("1000b"); // value SemanticVersion(0, 1, 16, "BETA", "1000b")

SemanticVersion v8 = version1.incrementMajor(); // value SemanticVersion(1, 1, 16, "BETA", "1000a")
SemanticVersion v9 = version1.incrementMinor(); // value SemanticVersion(0, 2, 16, "BETA", "1000a")
SemanticVersion v10 = version1.incrementPatch(); // value SemanticVersion(0, 1, 17, "BETA", "1000a")

SemanticVersion v11 = version1.copy(); //value SemanticVersion(0, 1, 16, "BETA", "1000a")
```
