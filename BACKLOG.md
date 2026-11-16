# openflexo-kafka — Backlog

Features and evolutions to develop. Each entry says what problem it solves and when it is done. Bugs that are known and not fixed go to `KNOWN_DEFECTS.md`, not here.

Identifiers are stable and never reused: `KAFKA-F-<n>`. Reference them from commits and from other entries.

Status legend: `TODO` · `IN PROGRESS` · `BLOCKED` · `DONE` · `DEFERRED`.

---

## FML runtime errors

### KAFKA-F-1 — The kafka adapter raises typed FML errors instead of returning null after a warning or a printed stack  ·  `DONE`

**Problem.** Part of `CORE-F-11` phase 6 (`openflexo-core/BACKLOG.md`), done with the other remaining adapters in one batch (asked by the user, 2026-11-16), with the rules settled on the first adapters (xlsx, xml, jdbc, rest, formod, docx): the edition actions caught the failures of their evaluations (four catches printing the stack, or a log), and went on with null after a warning.

**Rules applied (decided with the user on the first adapters).** Evaluations through `FMLEvaluation`; every `execute` / `performExecute` declares `throws FMLExecutionException`; a null receiver, model, document, name or required input is an `FMLNullReferenceError`; a select from a null source is an `FMLNullReferenceError` naming the select; a foreseen failure of the technology an `FMLTechnologyError` with a message of its own; a `ModelDefinitionException` an `FMLInternalError`; a cancelled loading an `FMLCancellation`; a typed failure of `AbstractCreateResource` rethrown unchanged; a log just before a throw removed (the failure is reported once, by the editor); the `System.out` traces of the actions removed. Scope: the paths that FML execution goes through; loading and conversion code left.

**Done (2026-11-16).**
- `CreateConsumerAction`, `StartConsumerAction`, `StopConsumerAction`: a null server or consumer is an `FMLNullReferenceError`, the failure of an evaluation propagates (they returned null or false after a warning).
- `SendRecordAction`: a null server is an `FMLNullReferenceError`, a record refused by the producer an `FMLTechnologyError` (null after a warning).
- `CreateKafkaResource`: typed failures, rethrown unchanged when already typed (it wrapped them again into an untyped error).

**Known limits.**
- No test: kafka has no test module (a broker is needed).

**Chosen without an explicit decision (batch review).** `StartConsumerAction` / `StopConsumerAction` raise an error where they gave
`false`.

**Verified (2026-11-16).** ECJ compiles kafka-ta (no test module).
