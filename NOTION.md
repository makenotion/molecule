# Notion's Molecule fork

This fork is based on upstream 2.2.0 and backports the runtime fix from
[cashapp/molecule#761](https://github.com/cashapp/molecule/pull/761), reviewed at
commit `e9e70b952f9f1ef5b6c1ef2bcb74aa80b5280719`.

The patch allows initial composition to finish before cancellation disposes it.
Return to the upstream dependency once a release includes this fix.

## Build and publish

Use JDK 17 or newer and Android SDK 36. Set `ANDROID_HOME` to your SDK directory.

```sh
./gradlew :molecule-runtime:jvmTest :molecule-runtime:publishAndroidReleasePublicationToMavenLocal :molecule-runtime:publishJvmPublicationToMavenLocal :molecule-runtime:publishKotlinMultiplatformPublicationToMavenLocal
```

JitPack runs the commands in `jitpack.yml` and publishes the Android, JVM, and
root multiplatform artifacts. Other platforms are not published.

Consume `com.github.makenotion.molecule:molecule-runtime:<commit>` from
`https://jitpack.io`, pinning a full commit SHA. Redirect transitive
`app.cash.molecule` dependencies to the matching JitPack artifacts at the same
commit so both runtimes do not appear on the classpath.
