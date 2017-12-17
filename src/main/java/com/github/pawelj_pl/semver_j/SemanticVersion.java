package com.github.pawelj_pl.semver_j;

import com.github.pawelj_pl.semver_j.exceptions.VersionError;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class SemanticVersion implements Comparable<SemanticVersion> {
    private final Integer major;
    
    private final Integer minor;
    
    private final Integer patch;
    
    private final String preRelease;
    
    private final String buildMetadata;
    
    private final static String WITHOUT_LEADING_ZEROS_PATTERN =
            "[A-Za-z1-9-][A-Za-z0-9-]*(?:\\.[A-Za-z1-9-][A-Za-z0-9-]*)*";
    
    
    private final static String WITH_LEADING_ZEROS_PATTERN = "[A-Za-z0-9-]+(?:\\.[A-Za-z0-9-]+)*";
    
    private final static String COMPONENTS_PATTERN =
            String.format("^(?<major>\\d+)\\.(?<minor>\\d+)\\.(?<patch>\\d+)(?:-(?<preRelease>%s))?(?:\\+(?<buildMetadata>%s))?$", WITHOUT_LEADING_ZEROS_PATTERN, WITH_LEADING_ZEROS_PATTERN);
    
    public SemanticVersion(Integer major, Integer minor, Integer patch, String preRelease, String buildMetadata) {
        Stream.of(major, minor, patch).forEach(this::validateNumericParameter);
        validateParameterRegex(preRelease, WITHOUT_LEADING_ZEROS_PATTERN);
        validateParameterRegex(buildMetadata, WITH_LEADING_ZEROS_PATTERN);
        this.major = major;
        this.minor = minor;
        this.patch = patch;
        this.preRelease = preRelease;
        this.buildMetadata = buildMetadata;
    }
    
    public SemanticVersion(Integer major, Integer minor, Integer patch) {
        this(major, minor, patch, null, null);
    }
    
    public Integer getMajor() {
        return major;
    }
    
    public Integer getMinor() {
        return minor;
    }
    
    public Integer getPatch() {
        return patch;
    }
    
    public Optional<String> getPreRelease() {
        return Optional.ofNullable(preRelease);
    }
    
    public Optional<String> getBuildMetadata() {
        return Optional.ofNullable(buildMetadata);
    }
    
    public SemanticVersion newMajor(Integer major) {
        return new SemanticVersion(major, minor, patch, preRelease, buildMetadata);
    }
    
    public SemanticVersion newMinor(Integer minor) {
        return new SemanticVersion(major, minor, patch, preRelease, buildMetadata);
    }
    
    public SemanticVersion newPatch(Integer patch) {
        return new SemanticVersion(major, minor, patch, preRelease, buildMetadata);
    }
    
    public SemanticVersion newPreRelease(String preRelease) {
        return new SemanticVersion(major, minor, patch, preRelease, buildMetadata);
    }
    
    public SemanticVersion newBuildMetadata(String buildMetadata) {
        return new SemanticVersion(major, minor, patch, preRelease, buildMetadata);
    }
    
    public SemanticVersion incrementMajor() {
        return new SemanticVersion(major + 1, 0, 0);
    }
    
    public SemanticVersion incrementMinor() {
        return new SemanticVersion(major, minor + 1, 0);
    }
    
    public SemanticVersion incrementPatch() {
        return new SemanticVersion(major, minor, patch + 1);
    }
    
    @Override
    public int compareTo(SemanticVersion o) {
        if (major - o.major != 0) {
            return major - o.major;
        }
        
        if (minor - o.minor != 0) {
            return minor - o.minor;
        }
        
        if (patch - o.patch != 0) {
            return patch - o.patch;
        }
        
        return comparePreRelease(o.preRelease);
    }
    
    private int comparePreRelease(String otherPreRelease) {
        if (preRelease != null && otherPreRelease == null) {
            return -1;
        }
        
        if (preRelease == null && otherPreRelease != null) {
            return 1;
        }
        
        if (preRelease == null || preRelease.equals(otherPreRelease)) {
            return 0;
        }
    
        List<String> thisIds = Arrays.asList(preRelease.split("\\."));
        List<String> otherIds = Arrays.asList(otherPreRelease.split("\\."));
    
        Iterator<String> thisIterator = thisIds.iterator();
        Iterator<String> otherIterator = otherIds.iterator();
        
        while (thisIterator.hasNext() && otherIterator.hasNext()) {
            int result = (comparePreReleasePart(thisIterator.next(), otherIterator.next()));
            if (result != 0) {
                return result;
            }
        }
        
        if (thisIds.size() == otherIds.size()) {
            return 0;
        }
        
        return thisIds.size() > otherIds.size() ? 1 : -1;
    }
    
    private int comparePreReleasePart(String thisPart, String otherPart) {
        if (thisPart.equals(otherPart)) {
            return 0;
        }
        if (isNumeric(thisPart) && !isNumeric(otherPart)) {
            return -1;
        }
        if (!isNumeric(thisPart) && isNumeric(otherPart)) {
            return 1;
        }
        if (isNumeric(thisPart) && isNumeric(otherPart)) {
            return Integer.parseInt(thisPart) - Integer.parseInt(otherPart);
        }
        return thisPart.compareTo(otherPart);
    }
    
    private boolean isNumeric(String element) {
        try {
            //noinspection ResultOfMethodCallIgnored
            Integer.parseInt(element);
        } catch (NumberFormatException err) {
            return false;
        }
        return true;
    }
    
    private void validateParameterRegex(String parameter, String regex) {
        if (parameter == null) {
            return;
        }
        Pattern compiledPattern = Pattern.compile(regex);
        Matcher matcher = compiledPattern.matcher(parameter);
        if (!matcher.matches()) {
            throw new VersionError(String.format("Provided version element %s doesn't match to pattern: %s",
                                                 parameter, regex));
        }
    }
    
    private void validateNumericParameter(Integer parameter) {
        Objects.requireNonNull(parameter, "Numeric element can't be null");
        if (parameter < 0) {
            throw new IllegalArgumentException("Numeric element can't be negative");
        }
    }
    
    public static SemanticVersion fromString(String version) {
        Pattern compiledPattern = Pattern.compile(COMPONENTS_PATTERN);
        Matcher matcher = compiledPattern.matcher(version);
        if (!matcher.find()) {
            throw new VersionError(String.format("%s is not valid semver", version));
        }
        Integer major = Integer.parseInt(matcher.group("major"));
        Integer minor = Integer.parseInt(matcher.group("minor"));
        Integer patch = Integer.parseInt(matcher.group("patch"));
        return new SemanticVersion(major, minor, patch, matcher.group("preRelease"),
                                   matcher.group("buildMetadata"));
    }
    
    public SemanticVersion copy() {
        return new SemanticVersion(major, minor, patch, preRelease, buildMetadata);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SemanticVersion)) return false;
        SemanticVersion that = (SemanticVersion) o;
        return Objects.equals(major, that.major) &&
                Objects.equals(minor, that.minor) &&
                Objects.equals(patch, that.patch) &&
                Objects.equals(preRelease, that.preRelease) &&
                Objects.equals(buildMetadata, that.buildMetadata);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(major, minor, patch, preRelease, buildMetadata);
    }
    
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(major);
        stringBuilder.append(".");
        stringBuilder.append(minor);
        stringBuilder.append(".");
        stringBuilder.append(patch);
        if (preRelease != null) {
            stringBuilder.append("-");
            stringBuilder.append(preRelease);
        }
        if (buildMetadata != null) {
            stringBuilder.append("+");
            stringBuilder.append(buildMetadata);
        }
        return stringBuilder.toString();
    }
}
