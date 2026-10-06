# java-upgrade-8-to-21

`Legacy8.java` and `Modern21.java` implement the same order-report behaviour in Java 8 and Java 21 style, with a parity test and a migration checklist below.

## Goal
Give an 8-to-21 migration checklist backed by code, and show that old and new idioms stay behaviourally equivalent.

## Run it
```
mvn -q test
```
Expected: `UpgradeParityTest` (2 tests) passes; the quiet build prints nothing. Needs Java 21 and Maven.

## What it proves
Checklist, in the order that usually works:
1. Build on JDK 21 first with `--release 8`, fix breakage, then raise `release` to 11, 17, 21 one step at a time.
2. Strong encapsulation (16/17): `--illegal-access` is gone; replace reflection into `sun.*`/`java.*` internals or add targeted `--add-opens`.
3. Removed APIs: Java EE modules (JAXB, JAX-WS, `javax.annotation`) left the JDK in 11; add them as dependencies (Jakarta).
4. Upgrade build plugins and libraries (Maven compiler/surefire, Mockito, Lombok, ASM-based tools) to versions that read class file 65.
5. GC and flags: CMS is removed and G1 is the default; drop obsolete `-XX` flags, since the JVM errors on removed ones.
6. Adopt language features after it is green: `var`, `List.of`, text blocks, records, sealed types, switch patterns.
7. Behaviour traps: a pattern `switch` throws NPE on `null` unless you add `case null` (covered by the test); the default charset is UTF-8 since 18; `SecurityManager` is deprecated for removal.

The test `sameBehaviourOldAndNew` asserts parity for labels (including `null` and a non-string), immutable lists, text-block output and `getFirst`; `bothListsAreImmutable` checks both lists reject `add`. Only the code, not the checklist items about flags and libraries, is exercised by the test.

## Trade-offs
- The checklist is generic; real projects hit framework-specific issues (Spring, Hibernate, Gradle versions) not shown here.
- Parity tests catch behaviour drift only for the paths they exercise.

## When not to use it
- Skipping straight to rewrites: migrate first, modernise later.
- Projects pinned to a vendor runtime that is not available above Java 8.
