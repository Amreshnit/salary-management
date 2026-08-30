# Requirements — Employee Salary Management System

## Goal

ACME's HR team currently manages salary data for 10,000 employees across multiple
countries in spreadsheets. This is slow, error-prone, and makes it hard to answer
basic questions about pay ("what's our average salary in Engineering?", "how has
this person's pay changed over time?"). This project replaces the spreadsheet
workflow with a web application so an **HR Manager** can browse, search, update,
and reason about salary data directly.

## Primary user

**HR Manager** — needs to find an employee quickly, view/update their profile and
salary, record salary changes (raises, promotions, adjustments) without losing
history, and get aggregate answers about how the org pays people (by department,
by country, by pay band).

## In scope

1. **Employee records** — create, view, update, and deactivate employees
   (name, email, department, job title, seniority level, country, currency,
   hire date, status).
2. **Salary history, not overwrites** — salary changes are recorded as new,
   effective-dated records (reason: HIRE, RAISE, PROMOTION, ADJUSTMENT) rather
   than mutating a single "current salary" field, so history is always
   auditable and "what did they earn on date X" is answerable.
3. **Search & filter** — find employees by name/code/email, filter by
   department, country, status; server-side pagination (required at 10k+ rows).
4. **Basic analytics** — average salary by department, average salary by
   country, and salary-band distribution, to directly answer "how do we pay
   people" at a glance.
5. **Seeding** — a script that generates 10,000 realistic synthetic employees
   (varied countries, departments, seniority, and salary distributions) so the
   app can be evaluated at realistic scale immediately.

## Deliberately out of scope (and why)

- **Payroll processing / tax & statutory compliance** — calculating net pay,
  withholdings, or country-specific tax rules is a large, jurisdiction-specific
  problem on its own and orthogonal to the stated question ("how do we pay
  people"). Out of scope for this exercise.
- **Approval workflows for salary changes** — a real system would want
  maker-checker approval before a raise takes effect. Left out to keep the
  data model and UI focused; the effective-dated salary record model is
  designed so an approval step could be added later without a redesign.
- **Authentication / multi-role access** — the brief specifies a single HR
  Manager persona and this is a take-home exercise, not a multi-tenant product.
  Adding a login screen and role-based access would add real engineering
  surface area (session handling, authorization checks on every endpoint)
  without demonstrating additional judgment relevant to the brief. Noted as
  the natural next step for a production rollout.
- **Live currency conversion** — employees are paid in their local currency;
  cross-country analytics are reported **per currency** rather than converted
  to a single reporting currency via a live or static FX table. Faking an
  exchange rate would produce a number that looks precise but isn't
  trustworthy for an HR decision; being explicit about currency is more honest
  than silently guessing at a rate.
- **Bulk CSV upload UI / employee self-service** — only the HR Manager persona
  and the one-time seed path are supported; a general-purpose import UI is a
  reasonable v2 feature, not core to answering the stated problem.

## Non-functional expectations

- Usable at 10,000-employee scale: list/search/analytics must stay responsive
  (server-side pagination + DB indexes, not client-side filtering of the full
  table).
- Salary history is append-only from the API's perspective — no endpoint
  mutates a past salary record in place.
