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
