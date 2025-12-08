# Rule Engine Internals

This document dives into the internals of the classification rule
engine and discusses how to extend it with new matcher types or
machine learning models.

## Matcher Types

The SDK supports multiple matcher types.  Each matcher defines how
input data is evaluated against a pattern.  Matchers are defined in
the policy JSON under the `matchers` array.

### regex

The default matcher uses Java's `Pattern` class to compile and
evaluate regular expressions.  A rule is considered a match if any
matcher in its list of `matchers` produces a regex hit.  Patterns
should be designed carefully to avoid catastrophic backtracking.

### fuzzy

The fuzzy matcher performs approximate string matching using
Levenshtein distance.  It is useful when you expect typos or
variations in the input.  For example, the pattern `"passport"`
will match `"passwort"` or `"pasport"` when the edit distance
between the strings is less than or equal to 2.  The threshold is
currently fixed but can be parameterized in the future.

### semantic_model (future)

This matcher type is reserved for integrating machine learning
models.  A semantic model could classify text based on context
rather than exact patterns.  For example, you might use a BERT
model fine‑tuned for PII detection.  Integration considerations:

1. **Performance**: Models are computationally expensive.  Cache
   results or run them asynchronously where possible.
2. **Resource Management**: Large models require GPU/CPU
   resources.  Consider deploying the model as a separate service
   and call it from the SDK.
3. **Confidence Thresholds**: Models output probabilities.  Define
   thresholds to decide whether the text should be considered a
   match.

### Adding a New Matcher

To implement a new matcher:

1. Create a utility class (e.g. `DictionaryMatcher`) with a
   static `matches(String pattern, String data)` method.
2. Update the `matches` method in `DetectionEngine` to handle the
   new `type` by invoking your matcher.
3. Document the matcher type in this file so that policy authors
   know how to use it.

## Classification Modes

The SDK exposes two modes via the `ClassificationMode` enum:

- **TOP_MATCH_ONLY**: Return only the highest priority match for
  each input.  This is the default and aligns with many data
  protection policies where only the most sensitive classification
  matters.
- **MULTI_MATCH_ALL**: Return all rules that match the input.  Use
  this mode for auditing or debugging purposes when you want to
  understand the overlap between rules.

The mode can be specified on each call to `classify(...)`.  See
`integration_guide.md` for examples.