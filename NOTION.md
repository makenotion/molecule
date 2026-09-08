# Notion's Molecule fork

This fork is based on upstream 2.2.0 and backports the runtime fix from
[cashapp/molecule#761](https://github.com/cashapp/molecule/pull/761), reviewed at
commit `e9e70b952f9f1ef5b6c1ef2bcb74aa80b5280719`.

The patch allows initial composition to finish before cancellation disposes it.
Return to the upstream dependency once a release includes this fix.

## Validate and stage

Use JDK 17 or newer and Android SDK 36. Set `ANDROID_HOME` to your SDK directory.

```sh
./gradlew :molecule-runtime:jvmTest :molecule-runtime:assembleRelease
./gradlew :molecule-runtime:publishAndroidReleasePublicationToInstallLocallyRepository :molecule-runtime:publishJvmPublicationToInstallLocallyRepository :molecule-runtime:publishKotlinMultiplatformPublicationToInstallLocallyRepository
```

Artifacts are staged in `build/localMaven/so/notion`. Only the Android, JVM, and
root multiplatform publications are intended for this Android fork; other
platforms are not published.

## Publish

Use the standard Notion AWS login with write access to
`s3://notion-android-maven-artifacts/releases`. Publishing uses Gradle's default
AWS credential chain. Do not put credentials in this repository.

Set a new immutable `VERSION_NAME` in `gradle.properties` for each release. Never
overwrite an existing version. After validating and recording the source in Git:

```sh
./gradlew :molecule-runtime:publishAndroidReleasePublicationToNotionRepository :molecule-runtime:publishJvmPublicationToNotionRepository :molecule-runtime:publishKotlinMultiplatformPublicationToNotionRepository
```

The Android app consumes `so.notion:molecule-runtime:2.2.0-notion.1`. Redirect
transitive `app.cash.molecule` dependencies to the matching `so.notion` artifacts
at the same version so both runtimes do not appear on the classpath.

The inherited upstream GitHub workflows are not configured to release this fork.
Use the explicit publication tasks above.
