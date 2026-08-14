# Cobas Infinity — Protocol Analysis
**Source log:** `2026-02-11-12-00-18-957_INFINITY.log` (full day, 2026-02-11)

---

## Scale

| Metric | Count |
|---|---|
| Complete sessions (ENQ → EOT) | 1,960 |
| NACKs | 0 |
| Checksum errors | 0 |
| Unique samples | 1,416 |
| Total O/R pairs | 18,864 |

---

## Frame Types Received

| Frame | Count | Notes |
|---|---|---|
| `H` | 1,960 | One per session |
| `P` | 2,118 | Patient frame |
| `\|` *(unknown type)* | 2,118 | Non-standard manufacturer frame, always right after `P` — see §1 |
| `O` | 18,864 | One per test |
| `R` | 18,864 | One per test |
| `C` | 93 | Comment — always after `R` |
| `L` | 1,960 | Terminator |

---

## 1. The `|` Mystery Frame (after every P)

The Cobas Infinity sends a **manufacturer-specific frame with type `|`** (field separator character — not a valid ASTM record type letter) immediately after every `P` frame. Content examples:

```
1|||G                             ← normal sample, no flag
1||Slightly Lipemic Sample|G
1||High Icteric|G
1||Slightly Hemolysed Sample|G
1||Negative|G
```

**Current driver behavior:** The frame type `'|'` does not match any handler in `treatResultFrame()`, so it falls through silently. Content is concatenated to the buffer but never parsed. **The pre-analytical sample quality text is currently lost.**

> **Action for new version:** Decide whether to add a handler for this frame type to capture sample quality notes (lipemia, icterus, hemolysis status) as a pre-analytical flag on the sample.

---

## 2. LHI Interference Index Codes (`^^^L` / `^^^H` / `^^^I`)

**3,918 R frames** carry interference index codes as test codes:

| Code | Meaning | Observed value range |
|---|---|---|
| `^^^L` | Lipemia index | 0 – 31+ |
| `^^^H` | Hemolysis index | 0 – 28+ |
| `^^^I` | Icterus index | 0 – 10+ |

These arrive as regular `R` frames alongside normal test results. Every sample gets all three. They route through `TestLabelMap` like any other test code — label mappings for `L`, `H`, and `I` must exist to receive them.

> **Action for new version:** Verify that `L`, `H`, `I` test codes are present in `TestLabelMap` for the Infinity instrument and are stored/forwarded to LIS.

---

## 3. C Frames — Clinical Alert Messages

**93 C frames**, always occurring **after an R frame** (91 directly after R, 2 after another C). They carry free-text clinical warnings attached to the preceding result:

```
Negative
Positive
Possible Interference By Hemolysis
Possible Interference By Icterus
Not Reported - Sample slightly hemolyzed. Unable to report due to interference.
Falsely elevated levels of CK-MB can be caused by macro-CKs; measured CK-MB can be higher than total CK in some cases
```

**Current driver behavior:** `CobasInfinity_CommentResultReader` handles these via `parseCommentAfterResultFrame()` and stores them on the test's `notificationMessage`. This path **is exercised** in production.

> **Action for new version:** Must verify that C frame content reaches `notificationMessage` on the test and is forwarded to LIS.

---

## 4. Manual Results

**190 R frames** (primarily test codes `1001` and `1010`) have `instr_id = Manual` instead of `800.1`, meaning they were hand-entered by a lab operator. The operator field identifies the person:

| Operator | Count |
|---|---|
| `^~SYSValDaemon~` (auto-validated manual entries) | 2,447 |
| `^NADAD` | 75 |
| `^MONAF` | 44 |
| `^NAYL` | 18 |
| `^MARIAH` | 17 |
| `^EDDYK` | 10 |
| `^TONINES` | 7 |
| others | ~30 |

For manually entered results, the last field of the R frame is `Manual` instead of the instrument ID `800.1`. The operator name appears in field 11.

> **Action for new version:** Verify that `CobasInfinity_ResultLineReader` correctly distinguishes `Manual` from instrument-originated results and preserves the operator name.

---

## 5. Abnormal Flags

**Zero** R frames use the standard ASTM field 6 (abnormal flag `H`/`L`/`LL`/`HH` etc.).

All abnormality signaling on this instrument goes through:
- **C frames** — free-text clinical comments (see §3)
- **`^^^L/H/I` R frames** — numeric interference indices (see §2)

> Do **not** rely on the ASTM abnormal flag field for this instrument.

---

## 6. Top Test Codes

| Code | Result count |
|---|---|
| `L` (Lipemia) | 1,306 |
| `H` (Hemolysis) | 1,306 |
| `I` (Icterus) | 1,306 |
| `1001` | 1,153 |
| `452` | 993 |
| `990` | 937 |
| `989` | 935 |
| `991` | 921 |
| `1010` | 921 |
| `763` | 918 |
| `419` | 770 |
| `256` | 757 |
| `699` | 400 |
| `688` | 387 |

---

## Testing Checklist for New Labotron Version

| Feature | Priority | Notes |
|---|---|---|
| C frame parsing → `notificationMessage` on test | **Critical** | 93 occurrences in one day |
| `^^^L`, `^^^H`, `^^^I` test codes mapped and stored | **Critical** | 3,918 results, every sample |
| Manual results (`instr_id = Manual`) parsed correctly | **Critical** | 190 results, operator name preserved |
| `SYSValDaemon` vs named operator in validator field | **High** | Two distinct formats in field 11 |
| Multi-session (1,960 ENQ→EOT cycles) without sequence errors | **High** | Each session resets sequence to 1 |
| `|`-type frame (pre-analytical sample quality text) | **Medium** | Currently ignored — decide if needed |
| ASTM abnormal flag field | **N/A** | Never used by this instrument |
