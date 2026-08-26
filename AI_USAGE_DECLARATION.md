# Declaration of Generative AI use — IMTPS

This declaration covers the ways Claude (Anthropic) was used while working on
this codebase. Review it and edit anything that doesn't precisely match how
you used it before submitting — this needs to describe your actual usage,
not just be copied as-is.

## 1. Understanding assignment/architecture requirements

Used Claude to talk through how the existing IMTPS codebase was structured
and how its classes interact — e.g. asking it to trace which classes
communicate with which, and to diagram the controller-navigation flow and
the strategy-comparison data flow — as a way of building an accurate mental
model of the codebase before making changes to it.

## 2. Writing documentation

Used Claude to generate Javadoc-style class- and method-level documentation,
and occasional inline comments, for the project's Java classes (controllers,
models, services, repositories, strategies, and utility classes). Claude was
given the existing source files and asked to document them without changing
any logic. All generated documentation was reviewed before being added to
the codebase.

## 3. Debugging

While documenting the codebase, Claude was asked to note anything that
looked like unintended behaviour. This surfaced several concrete bugs,
which were fixed independently afterward:
- a missing null check in `TaskFormController.setTaskToEdit`, which would
  throw a `NullPointerException` when adding a new task
- `ComparisonController` computing the strategy comparison before the
  user's selected focus mode was applied to it
- `MainController.handleGenerateSchedule` reading the wrong spinner for the
  availability end time's minutes
- a dead `resetFields()` method that was never called after creating a task
- a duplicated cell-value-factory assignment in `ComparisonController`

Claude did not write the fixes for these — it was used to identify and
explain the issues; the corrections themselves were implemented separately.

## What was not used

Claude was not used to design the scheduling algorithms, the scoring
formulas in `WeightedFocusStrategy`, or the overall class/package
architecture — those were already in place in the codebase Claude was
given to document.
