# AI Usage Notes

This project was built with Claude Code (Anthropic) as an active pair-programmer,
not a one-shot generator. This doc records how it was used, so the "how you use
AI" part of the assessment is visible, not just the output.

## Workflow

1. **Plan before code.** Before any implementation, I had Claude draft a concrete
   phase-by-phase plan (data model, API shape, commit structure) and reviewed/
   adjusted it before generating anything — the equivalent of a design review
   with a very fast junior engineer. Scope decisions (what's in/out, why) were
   made by me and then written up; Claude was asked to make the reasoning
   explicit in `docs/requirements.md` rather than inventing scope on its own.
2. **Incremental, reviewable commits.** Each phase (schema, API, seeding, tests,
   UI, UI tests, polish) was generated and committed separately rather than as
   one large drop, so the git history reflects the actual build order and each
   commit is independently reviewable.
3. **Human-set constraints, AI-filled implementation.** Decisions with real
   trade-offs — effective-dated salary history vs overwriting a salary column,
   per-currency analytics vs fabricated FX conversion, no auth for a
   single-persona take-home — were made deliberately and given to the AI as
   constraints, not left for it to decide.
4. **Verification, not blind trust.** Backend tests were run (`mvnw test`),
   the seeder's output was checked directly against Postgres (row counts,
   distribution sanity checks), and the frontend was exercised in a real
   browser against the live backend — AI-generated code was validated the same
   way hand-written code would be, not assumed correct because it compiled.

## Real problems it hit and how they were resolved

Bleeding-edge tooling on this machine surfaced several issues that a
one-shot "generate the app" prompt would have papered over or silently
gotten wrong. Each was root-caused and fixed, not worked around blindly:

- **JDK 23's embedded Tomcat wouldn't start** (`Unable to establish
  loopback connection`, deep in `sun.nio.ch`). Isolated with a five-line
  standalone Java reproduction to confirm it was `Selector.open()`
  itself failing on this machine, independent of Spring Boot — then
  switched the connector to Tomcat's NIO2 (IOCP-backed) protocol, which
  doesn't use a `Selector` at all. See `docs/architecture.md`.
- **A null search parameter crashed employee search** with a Postgres
  `function lower(bytea) does not exist` error — Hibernate couldn't
  infer the parameter type for `LOWER(:search)` when `:search` was
  `null`. Fixed with an explicit JPQL `CAST(:search AS string)`.
- **Lombok silently stopped generating methods** under Maven (but not
  under plain `javac`) — traced to JDK 23 requiring an explicit
  `-processorpath` for annotation processor discovery; Maven's
  `annotationProcessorPaths` config fixes it.
- **`ng add @angular/material` crashed** applying its theme schematic
  (`Cannot read properties of undefined ('primary')`) after installing
  the packages. Rather than deleting node_modules and hoping a retry
  works, finished the setup by hand: prebuilt theme in `angular.json`,
  Roboto/Material Icons in `index.html`, `provideAnimationsAsync()` +
  the missing `@angular/animations` dependency in `app.config.ts`.
- **The whole app rendered as if under a dark overlay** after that
  manual Material setup — caught during a mobile-viewport screenshot
  check, not by trusting the desktop pass. Root cause: the crashed
  schematic never added Material's `mat-typography` body class or a
  background color, so the page had no defined background at all.
- **Angular CLI's `new` command defaulted to v22**, which requires a
  newer Node.js than is installed here (`npm warn EBADENGINE`). Rather
  than force-installing Node, checked `npm view` for the newest CLI
  major whose engine range the installed Node satisfies (v21) and used
  that instead.
- **Spring Initializr defaulted to Spring Boot 4.1.1** — a major version
  released after this model's training cutoff, meaning far less
  confidence in its exact API surface. Pinned to Spring Boot 3.3.4
  (fixing up the handful of dependency artifact IDs the newer default
  had changed) instead of guessing at unfamiliar 4.x APIs mid-build.

The common thread: every one of these was caught by actually running
the code (compiling, booting the server, hitting the API, loading the
UI in a real browser at multiple viewport sizes) rather than trusting
that generated code was correct because it looked plausible.

## Tools used

- Claude Code (Sonnet) — architecture discussion, code generation across
  backend/frontend/tests/docs, and interactive debugging against real
  Postgres/browser sessions.

## Representative prompts (paraphrased)

- "Design the data model so salary changes are auditable — a HR manager needs
  to know what someone earned on a given past date, not just today."
- "Generate a Faker-based seeder for 10,000 employees with realistic
  country/department/seniority-correlated salary distributions, batch-inserted
  for performance."
- "Write JUnit tests for the salary-record service, specifically the case
  where adding a new salary record must close the previously open one."
- "Build the Angular employee list with server-side pagination against the
  real API, not a client-side filter of an in-memory array."
