# Declaration of Generative AI use — IMTPS

This declaration covers the ways Claude (Anthropic) was used while working on
this codebase.

## 1. Understanding assignment requirements
Used Claude to talk through the assignment's requirements and expectations
while working out how to approach the IMTPS project.

## 2. UI design
Used Claude as a sounding board for UI design decisions, layout and
structure of the task list, task form, and comparison views.

## 3. Reviewing class-level communication
Used Claude to trace how the project's classes communicate
(e.g. the controller-navigation flow and the strategy-comparison data flow),
as a way of checking that the actual class interactions matched what was
intended.

## 4. Checking architectural style
Asked Claude to assess whether the codebase's structure aligns with the
architectural style and design patterns intended for the project (e.g.
layering, the Strategy pattern for scheduling, and how the controllers,
services, and repositories relate).

## 5. Debugging
While reviewing and documenting the codebase, Claude was asked to flag
anything that looked like unintended behaviour. This surfaced several
concrete bugs, which were fixed independently afterward.

Claude did not write the fixes for these, it was used to identify and
explain the issues; the corrections themselves were implemented separately.

## 6. Documentation
Used Claude to generate Javadoc-style class and method-level documentation,
for the project's Java classes (controllers, models, services, repositories,
strategies, and utility classes). Claude was given the existing source files
and asked to document them without changing any logic. All generated documentation
was reviewed before being added to the codebase.

## What was not used
Claude was not used to design the scheduling algorithms, the scoring
formulas in `WeightedFocusStrategy`, or the overall class/package
architecture; those were already in place in the codebase Claude was
given to work with.