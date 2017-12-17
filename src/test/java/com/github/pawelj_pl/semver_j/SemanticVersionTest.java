package com.github.pawelj_pl.semver_j;

import com.github.pawelj_pl.semver_j.exceptions.VersionError;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.assertj.core.api.Assertions.assertThat;

public class SemanticVersionTest {
    
    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    
    @Test
    public void shouldCreateVersionFromComplexString() {
        //given
        String inputData = "3.17.4-beta12-3.4+1build17";
        
        //when
        SemanticVersion result = SemanticVersion.fromString(inputData);
        
        //then
        assertThat(result.toString()).isEqualTo(inputData);
        assertThat(result.getMajor()).isEqualTo(3);
        assertThat(result.getMinor()).isEqualTo(17);
        assertThat(result.getPatch()).isEqualTo(4);
        assertThat(result.getPreRelease().get()).isEqualTo("beta12-3.4");
        assertThat(result.getBuildMetadata().get()).isEqualTo("1build17");
    }
    
    @Test
    public void shouldCreateVersionByConstructor() {
        //given
        String expectedVersion = "3.17.4-beta12-3.4+1build17";
        
        //when
        SemanticVersion result = new SemanticVersion(3, 17, 4, "beta12-3.4", "1build17");
        
        //then
        assertThat(result.toString()).isEqualTo(expectedVersion);
        assertThat(result.getMajor()).isEqualTo(3);
        assertThat(result.getMinor()).isEqualTo(17);
        assertThat(result.getPatch()).isEqualTo(4);
        assertThat(result.getPreRelease().get()).isEqualTo("beta12-3.4");
        assertThat(result.getBuildMetadata().get()).isEqualTo("1build17");
    }
    
    @Test
    public void shouldCreateVersionFromRequiredParameters() {
        //given
        String inputData = "0.1.0";
        
        //when
        SemanticVersion result = SemanticVersion.fromString(inputData);
        
        //then
        assertThat(result.toString()).isEqualTo(inputData);
        assertThat(result.getMajor()).isEqualTo(0);
        assertThat(result.getMinor()).isEqualTo(1);
        assertThat(result.getPatch()).isEqualTo(0);
        assertThat(result.getPreRelease().isPresent()).isFalse();
        assertThat(result.getBuildMetadata().isPresent()).isFalse();
    }
    
    @Test
    public void shouldCreateVersionFromRequiredParametersAndPreRelease() {
        //given
        String inputData = "1.1.0-12-8.5.f";
        
        //when
        SemanticVersion result = SemanticVersion.fromString(inputData);
        
        //then
        assertThat(result.toString()).isEqualTo(inputData);
        assertThat(result.getMajor()).isEqualTo(1);
        assertThat(result.getMinor()).isEqualTo(1);
        assertThat(result.getPatch()).isEqualTo(0);
        assertThat(result.getPreRelease().get()).isEqualTo("12-8.5.f");
        assertThat(result.getBuildMetadata().isPresent()).isFalse();
    }
    
    @Test
    public void shouldCreateVersionFromRequiredParametersAndBuildMetadata() {
        //given
        String inputData = "0.1.0+04-1.abc-1";
        
        //when
        SemanticVersion result = SemanticVersion.fromString(inputData);
        
        //then
        assertThat(result.toString()).isEqualTo(inputData);
        assertThat(result.getMajor()).isEqualTo(0);
        assertThat(result.getMinor()).isEqualTo(1);
        assertThat(result.getPatch()).isEqualTo(0);
        assertThat(result.getPreRelease().isPresent()).isFalse();
        assertThat(result.getBuildMetadata().get()).isEqualTo("04-1.abc-1");
    }
    
    @Test
    public void shouldUpdateVersion() {
        //given
        String inputData = "3.17.4-beta12-3.4+1build17";
        SemanticVersion semanticVersion = SemanticVersion.fromString(inputData);
        
        //when
        SemanticVersion result = semanticVersion.newMajor(4);
        result = result.newMinor(19);
        result = result.newPatch(7);
        result = result.newPreRelease("Updated.1-a");
        result = result.newBuildMetadata("new.Build-1");
        
        //then
        assertThat(result.toString()).isEqualTo("4.19.7-Updated.1-a+new.Build-1");
        assertThat(semanticVersion.toString()).isEqualTo("3.17.4-beta12-3.4+1build17");
    }
    
    @Test
    public void shouldCopyVersion() {
        //given
        String inputData = "3.17.4-beta12-3.4+1build17";
        SemanticVersion semanticVersion = SemanticVersion.fromString(inputData);
        
        //when
        SemanticVersion newVersion = semanticVersion.copy();
        
        //then
        assertThat(newVersion.toString()).isEqualTo("3.17.4-beta12-3.4+1build17");
    }
    
    @Test
    public void shouldThrowExceptionWhenMajorNotNumber() {
        //given
        String inputData = "a3.17.4-beta12-3.4+1build17";
        
        //when
        expectedException.expect(VersionError.class);
        expectedException.expectMessage("a3.17.4-beta12-3.4+1build17 is not valid semver");
        SemanticVersion.fromString(inputData);
    }
    
    @Test
    public void shouldThrowExceptionWhenMinorNotNumber() {
        //given
        String inputData = "3.a17.4-beta12-3.4+1build17";
        
        //when
        expectedException.expect(VersionError.class);
        expectedException.expectMessage("3.a17.4-beta12-3.4+1build17 is not valid semver");
        SemanticVersion.fromString(inputData);
    }
    
    @Test
    public void shouldThrowExceptionWhenPatchNotNumber() {
        //given
        String inputData = "3.17.a4-beta12-3.4+1build17";
        
        //when
        expectedException.expect(VersionError.class);
        expectedException.expectMessage("3.17.a4-beta12-3.4+1build17 is not valid semver");
        SemanticVersion.fromString(inputData);
    }
    
    @Test
    public void shouldThrowExceptionWhenMajorNegative() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Numeric element can't be negative");
        new SemanticVersion(-1, 3, 10);
    }
    
    @Test
    public void shouldThrowExceptionWhenMinorNegative() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Numeric element can't be negative");
        new SemanticVersion(1, -3, 10);
    }
    
    @Test
    public void shouldThrowExceptionWhenPatchNegative() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Numeric element can't be negative");
        new SemanticVersion(1, 3, -10);
    }
    
    @Test
    public void shouldThrowExceptionWhenMajorNull() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("Numeric element can't be null");
        new SemanticVersion(null, 3, 10);
    }
    
    @Test
    public void shouldThrowExceptionWhenMinorNull() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("Numeric element can't be null");
        new SemanticVersion(1, null, 10);
    }
    
    @Test
    public void shouldThrowExceptionWhenPatchNull() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("Numeric element can't be null");
        new SemanticVersion(1, 3, null);
    }
    
    @Test
    public void shouldThrowExceptionWhenLeadingZeroInPreReleaseId() {
        //given
        String inputData = "0.2.4-12a.opa.01a";
        
        //when
        expectedException.expect(VersionError.class);
        expectedException.expectMessage("0.2.4-12a.opa.01a is not valid semver");
        SemanticVersion.fromString(inputData);
    }
    
    @Test
    public void shouldIncrementMajor() {
        //given
        String inputData = "3.17.4-beta12-3.4+1build17";
        SemanticVersion version = SemanticVersion.fromString(inputData);
        
        //when
        SemanticVersion result = version.incrementMajor();
        
        //then
        assertThat(version.toString()).isEqualTo(inputData);
        assertThat(result.toString()).isEqualTo("4.0.0");
    }
    
    @Test
    public void shouldIncrementMinor() {
        //given
        String inputData = "3.17.4-beta12-3.4+1build17";
        SemanticVersion version = SemanticVersion.fromString(inputData);
        
        //when
        SemanticVersion result = version.incrementMinor();
        
        //then
        assertThat(version.toString()).isEqualTo(inputData);
        assertThat(result.toString()).isEqualTo("3.18.0");
    }
    
    @Test
    public void shouldIncrementPatch() {
        //given
        String inputData = "3.17.4-beta12-3.4+1build17";
        SemanticVersion version = SemanticVersion.fromString(inputData);
        
        //when
        SemanticVersion result = version.incrementPatch();
        
        //then
        assertThat(version.toString()).isEqualTo(inputData);
        assertThat(result.toString()).isEqualTo("3.17.5");
    }
    
    @Test
    public void shouldCompareWhenVersionsEqual() {
        //given
        String inputData = "3.17.4-beta12-3.4+1build17";
        SemanticVersion version1 = SemanticVersion.fromString(inputData);
        SemanticVersion version2 = SemanticVersion.fromString(inputData);
        
        //when
        Integer result = version1.compareTo(version2);
        
        //then
        assertThat(result).isZero();
    }
    
    @Test
    public void shouldCompareWhenOnlyBuildMetadataDifferent() {
        //given
        String inputData1 = "3.17.4-beta12-3.4+1build17";
        String inputData2 = "3.17.4-beta12-3.4+3build18";
        SemanticVersion version1 = SemanticVersion.fromString(inputData1);
        SemanticVersion version2 = SemanticVersion.fromString(inputData2);
        
        //when
        Integer result = version1.compareTo(version2);
        
        //then
        assertThat(result).isZero();
    }
    
    @Test
    public void shouldCompareWhenOnlyBuildMetadataDifferentInOneVersion() {
        //given
        String inputData1 = "3.17.4-beta12-3.4+1build17";
        String inputData2 = "3.17.4-beta12-3.4";
        SemanticVersion version1 = SemanticVersion.fromString(inputData1);
        SemanticVersion version2 = SemanticVersion.fromString(inputData2);
        
        //when
        Integer result = version1.compareTo(version2);
        
        //then
        assertThat(result).isZero();
    }
    
    @Test
    public void shouldCompareWhenThisMajorGreater() {
        //given
        String inputData1 = "3.17.4-beta12-3.4+1build17";
        String inputData2 = "2.44.78-xyz.df.seded.ddd+zzzz";
        SemanticVersion version1 = SemanticVersion.fromString(inputData1);
        SemanticVersion version2 = SemanticVersion.fromString(inputData2);
        
        //when
        Integer result = version1.compareTo(version2);
        
        //then
        assertThat(result).isGreaterThan(0);
    }
    
    @Test
    public void shouldCompareWhenThisMajorLess() {
        //given
        String inputData1 = "2.17.4-beta12-3.4+1build17";
        String inputData2 = "3.44.78-xyz.df.seded.ddd+zzzz";
        SemanticVersion version1 = SemanticVersion.fromString(inputData1);
        SemanticVersion version2 = SemanticVersion.fromString(inputData2);
        
        //when
        Integer result = version1.compareTo(version2);
        
        //then
        assertThat(result).isLessThan(0);
    }
    
    @Test
    public void shouldCompareWhenThisMinorGreater() {
        //given
        String inputData1 = "3.40.4-beta12-3.4+1build17";
        String inputData2 = "3.6.78-xyz.df.seded.ddd+zzzz";
        SemanticVersion version1 = SemanticVersion.fromString(inputData1);
        SemanticVersion version2 = SemanticVersion.fromString(inputData2);
        
        //when
        Integer result = version1.compareTo(version2);
        
        //then
        assertThat(result).isGreaterThan(0);
    }
    
    @Test
    public void shouldCompareWhenThisMinorLess() {
        //given
        String inputData1 = "3.9.22-beta12-3.4+1build17";
        String inputData2 = "3.44.7-xyz.df.seded.ddd+zzzz";
        SemanticVersion version1 = SemanticVersion.fromString(inputData1);
        SemanticVersion version2 = SemanticVersion.fromString(inputData2);
        
        //when
        Integer result = version1.compareTo(version2);
        
        //then
        assertThat(result).isLessThan(0);
    }
    
    @Test
    public void shouldCompareWhenThisPatchGreater() {
        //given
        String inputData1 = "3.17.22-beta12-3.4+1build17";
        String inputData2 = "3.17.7-xyz.df.seded.ddd+zzzz";
        SemanticVersion version1 = SemanticVersion.fromString(inputData1);
        SemanticVersion version2 = SemanticVersion.fromString(inputData2);
        
        //when
        Integer result = version1.compareTo(version2);
        
        //then
        assertThat(result).isGreaterThan(0);
    }
    
    @Test
    public void shouldCompareWhenThisPatchLess() {
        //given
        String inputData1 = "3.17.4-beta12-3.4+1build17";
        String inputData2 = "3.17.78-xyz.df.seded.ddd+zzzz";
        SemanticVersion version1 = SemanticVersion.fromString(inputData1);
        SemanticVersion version2 = SemanticVersion.fromString(inputData2);
        
        //when
        Integer result = version1.compareTo(version2);
        
        //then
        assertThat(result).isLessThan(0);
    }
    
    @Test
    public void shouldCompareWhenPreReleaseMissingInThis() {
        //given
        String inputData1 = "3.17.4+1build17";
        String inputData2 = "3.17.4-beta12-3.4+1build17";
        SemanticVersion version1 = SemanticVersion.fromString(inputData1);
        SemanticVersion version2 = SemanticVersion.fromString(inputData2);
        
        //when
        Integer result = version1.compareTo(version2);
        
        //then
        assertThat(result).isGreaterThan(0);
    }
    
    @Test
    public void shouldCompareWhenPreReleaseMissingInOther() {
        //given
        String inputData1 = "3.17.4-beta12-3.4+1build17";
        String inputData2 = "3.17.4+1build17";
        SemanticVersion version1 = SemanticVersion.fromString(inputData1);
        SemanticVersion version2 = SemanticVersion.fromString(inputData2);
        
        //when
        Integer result = version1.compareTo(version2);
        
        //then
        assertThat(result).isLessThan(0);
    }
    
    @Test
    public void shouldCompareWhenPreReleaseMissing() {
        //given
        String inputData1 = "3.17.4+1build17";
        String inputData2 = "3.17.4+2build20";
        SemanticVersion version1 = SemanticVersion.fromString(inputData1);
        SemanticVersion version2 = SemanticVersion.fromString(inputData2);
        
        //when
        Integer result = version1.compareTo(version2);
        
        //then
        assertThat(result).isZero();
    }
    
    @Test
    public void shouldCompareWhenPreReleaseEqual() {
        //given
        String inputData1 = "3.17.4-beta12-3.4+1build17";
        String inputData2 = "3.17.4-beta12-3.4+2build20";
        SemanticVersion version1 = SemanticVersion.fromString(inputData1);
        SemanticVersion version2 = SemanticVersion.fromString(inputData2);
        
        //when
        Integer result = version1.compareTo(version2);
        
        //then
        assertThat(result).isZero();
    }
    
    @Test
    public void shouldCompareWhenPreReleaseTheSameButThisLonger() {
        //given
        String inputData1 = "3.17.4-beta12-3.4.22.7";
        String inputData2 = "3.17.4-beta12-3.4.22";
        SemanticVersion version1 = SemanticVersion.fromString(inputData1);
        SemanticVersion version2 = SemanticVersion.fromString(inputData2);
        
        //when
        Integer result = version1.compareTo(version2);
        
        //then
        assertThat(result).isGreaterThan(0);
    }
    
    @Test
    public void shouldCompareWhenPreReleaseTheSameButOtherLonger() {
        //given
        String inputData1 = "3.17.4-beta12-3.4.22";
        String inputData2 = "3.17.4-beta12-3.4.22.7";
        SemanticVersion version1 = SemanticVersion.fromString(inputData1);
        SemanticVersion version2 = SemanticVersion.fromString(inputData2);
        
        //when
        Integer result = version1.compareTo(version2);
        
        //then
        assertThat(result).isLessThan(0);
    }
    
    @Test
    public void shouldCompareWhenPreReleaseThisContainsNumeric() {
        //given
        String inputData1 = "3.17.4-beta12-3.4.22";
        String inputData2 = "3.17.4-beta12-3.a.22";
        SemanticVersion version1 = SemanticVersion.fromString(inputData1);
        SemanticVersion version2 = SemanticVersion.fromString(inputData2);
        
        //when
        Integer result = version1.compareTo(version2);
        
        //then
        assertThat(result).isLessThan(0);
    }
    
    @Test
    public void shouldCompareWhenPreReleaseOtherContainsNumeric() {
        //given
        String inputData1 = "3.17.4-beta12-3.a.22";
        String inputData2 = "3.17.4-beta12-3.4.22";
        SemanticVersion version1 = SemanticVersion.fromString(inputData1);
        SemanticVersion version2 = SemanticVersion.fromString(inputData2);
        
        //when
        Integer result = version1.compareTo(version2);
        
        //then
        assertThat(result).isGreaterThan(0);
    }
    
    @Test
    public void shouldCompareWhenPreReleaseDifferByNumeric() {
        //given
        String inputData1 = "3.17.4-beta12-3.7.22";
        String inputData2 = "3.17.4-beta12-3.4.22.1";
        SemanticVersion version1 = SemanticVersion.fromString(inputData1);
        SemanticVersion version2 = SemanticVersion.fromString(inputData2);
        
        //when
        Integer result = version1.compareTo(version2);
        
        //then
        assertThat(result).isGreaterThan(0);
    }
    
    @Test
    public void shouldCompareWhenPreReleaseDifferByString() {
        //given
        String inputData1 = "3.17.4-beta12-3.a.22";
        String inputData2 = "3.17.4-beta12-3.j.22.1";
        SemanticVersion version1 = SemanticVersion.fromString(inputData1);
        SemanticVersion version2 = SemanticVersion.fromString(inputData2);
        
        //when
        Integer result = version1.compareTo(version2);
        
        //then
        assertThat(result).isLessThan(0);
    }
}
