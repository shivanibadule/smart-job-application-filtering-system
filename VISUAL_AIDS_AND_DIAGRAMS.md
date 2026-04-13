# CHAIN OF RESPONSIBILITY - VISUAL AIDS & DIAGRAMS

## 1. DETAILED UML CLASS DIAGRAM

```
========================================================
           CHAIN OF RESPONSIBILITY PATTERN
========================================================

┌─────────────────────────────────────────────────────┐
│  <<interface>>                                      │
│  Request                                            │
├─────────────────────────────────────────────────────┤
│ # candidateName: String                            │
│ # yearsOfExperience: int                           │
│ # communicationScore: double                       │
│ # technicalScore: double                           │
│ # programmingLanguages: String                     │
│ # applicationStatus: String                        │
│ # rejectionReason: String                          │
│ # filterHistory: List<String>                      │
├─────────────────────────────────────────────────────┤
│ + getCandidateName(): String                       │
│ + getYearsOfExperience(): int                      │
│ + getCommunicationScore(): double                  │
│ + getTechnicalScore(): double                      │
│ + getProgrammingLanguages(): String                │
│ + getApplicationStatus(): String                   │
│ + getRejectionReason(): String                     │
│ + setApplicationStatus(status): void               │
│ + setRejectionReason(reason): void                 │
│ + addToFilterHistory(filter): void                 │
└─────────────────────────────────────────────────────┘
                      △ passed as
                      │
        ┌─────────────┴─────────────┐
        │                           │
        │                           │
┌───────────────────────────────────────────────────────────────────────┐
│  <<abstract>>                                                         │
│  Handler                                                              │
├───────────────────────────────────────────────────────────────────────┤
│ # nextHandler: Handler                                               │
├───────────────────────────────────────────────────────────────────────┤
│ + setNextHandler(handler: Handler): void                             │
│ + {final} handle(request: JobApplication): void                      │
│ # {abstract} canHandle(request: JobApplication): boolean             │
│ + getHandlerName(): String                                           │
└───────────────────────────────────────────────────────────────────────┘
           △              △              △
           │ extends      │ extends      │ extends
           │              │              │
    ┌──────────────┐  ┌──────────────────┐  ┌────────────────────┐
    │  HRFilter    │  │ TechnicalFilter  │  │ ManagerFilter      │
    ├──────────────┤  ├──────────────────┤  ├────────────────────┤
    │ - MIN_EXP: 2 │  │ - MIN_TECH: 7.0  │  │ - THRESHOLD: 7.5   │
    │ - MIN_COMM:  │  │ - LANGUAGES[]    │  │ - TECH_WEIGHT: 0.6 │
    │   6.0        │  │                  │  │ - COMM_WEIGHT: 0.4 │
    ├──────────────┤  ├──────────────────┤  ├────────────────────┤
    │ + canHandle()│  │ + canHandle()    │  │ + canHandle()      │
    └──────────────┘  └──────────────────┘  └────────────────────┘


┌───────────────────────────────┐
│ JobApplicationProcessor       │
├───────────────────────────────┤
│ - chain: Handler              │
├───────────────────────────────┤
│ + JobApplicationProcessor()   │
│ + processApplication(app)     │
│ - displayResult(app)          │
└───────────────────────────────┘
       uses
        │
        ├─→ creates HRFilter
        ├─→ creates TechnicalFilter
        ├─→ creates ManagerFilter
        └─→ chains: HR→Tech→Manager
```

---

## 2. SEQUENCE DIAGRAM - SUCCESSFUL FLOW

```
User    Processor    HR Filter    Tech Filter    Manager Filter
  │         │            │            │              │
  │         │            │            │              │
  │─────────→│ process()  │            │              │
  │         │            │            │              │
  │         │────────────→│ handle()   │              │
  │         │            │            │              │
  │         │         [CHECK 1: Experience]         │
  │         │            │            │              │
  │         │         [CHECK 2: Communication]      │
  │         │            │            │              │
  │         │         [PASSED: return true]         │
  │         │            │            │              │
  │         │            ├─────────────→│ handle()   │
  │         │            │            │              │
  │         │            │        [CHECK 1: Score]  │
  │         │            │        [CHECK 2: Lang]   │
  │         │            │        [PASSED: true]    │
  │         │            │            │              │
  │         │            │            ├──────────────→│ handle()
  │         │            │            │              │
  │         │            │            │         [Calculate Score]
  │         │            │            │         [CHECK: Score >= 7.5]
  │         │            │            │         [APPROVED: return true]
  │         │            │            │              │
  │         │←───────────────────────────────────────│
  │         │                                        │
  │←────────│ displayResult(APPROVED)               │
  │         │                                        │
```

---

## 3. SEQUENCE DIAGRAM - FAILURE FLOW (Early Rejection)

```
User    Processor    HR Filter    Tech Filter    Manager Filter
  │         │            │            │              │
  │─────────→│ process()  │            │              │
  │         │            │            │              │
  │         │────────────→│ handle()   │              │
  │         │            │            │              │
  │         │         [CHECK 1: Experience]         │
  │         │         [FAILED: < 2 years]           │
  │         │         [return false]                │
  │         │            │            │              │
  │         │            X(STOP)      X(NEVER)      X(NEVER)
  │         │            │            │              │
  │         │←────────────│            │              │
  │         │                                        │
  │←────────│ displayResult(REJECTED)               │
  │         │ Reason: Insufficient experience      │
  │         │                                        │
  │         │  filterHistory: []                    │
  │         │  (No filters processed beyond HR)     │
```

---

## 4. STATE MACHINE DIAGRAM

```
                    ┌─────────────────────────────┐
                    │   PENDING                   │
                    │  Application                │
                    │  Submitted                  │
                    └──────────────┬──────────────┘
                                   │
                    ┌──────────────►┤
                    │               │
           ┌────────┴────────┐      │
           │                 │      │
      HR Filter         HR Checks   │
      Responsibility    Applied     │
           │                 │      │
           │                 ▼      │
           │         ┌──────────────────┐
           │         │ PASSED HR TESTS  │
           │         │                  │
           │         │ Moves Forward    │
           │         └────────┬─────────┘
           │                  │
           │         ┌────────▼────────┐
           │         │ Tech Filter     │
           │         │ Checks Applied  │
           │         └────────┬────────┘
           │                  │
           │         ┌────────▼────────────┐
           │         │ PASSED TECH TESTS   │
           │         │                     │
           │         │ Moves Forward       │
           │         └────────┬────────────┘
           │                  │
           │         ┌────────▼──────────────┐
           │         │ Manager Filter       │
           │         │ Checks Applied       │
           │         └────────┬─────────────┘
           │                  │
           │         ┌────────▼──────────────┐
           │         │ PASSED MANAGER TESTS │
           │         │ Overall Score OK     │
           │         └────────┬─────────────┘
           │                  │
           │                  ▼
           │         ┌─────────────────────┐
           │         │     APPROVED        │
           │         │  Offer Letter Sent  │
           │         └─────────────────────┘
           │
           │
      FAILED AT    
      THIS STAGE
           │
           ▼
    ┌────────────────┐
    │   REJECTED     │
    │                │
    │ Notification   │
    │ Sent with      │
    │ Reason         │
    └────────────────┘
```

---

## 5. REQUEST PROCESSING FLOWCHART

```
                          START
                           │
                           ▼
                  ┌─────────────────┐
                  │ Create Job      │
                  │ Application     │
                  │ (Request)       │
                  └────────┬────────┘
                           │
                           ▼
                  ┌─────────────────────────┐
                  │ Pass to HR Filter       │
                  │ (First Handler)         │
                  └────────┬────────────────┘
                           │
                           ▼
              ┌────────────────────────────┐
              │   HR FILTER CHECKS         │
              │ 1. Experience >= 2?        │
              │ 2. Communication >= 6?     │
              └────┬──────────────┬────────┘
                   │              │
             FAILED│              │PASSED
                   ▼              ▼
        ┌──────────────────┐  ┌────────────────────┐
        │  REJECT          │  │ Set Status: PENDING│
        │  Stop Chain      │  │ Pass to Tech Filter│
        │                  │  │                    │
        └────────┬─────────┘  └────────┬───────────┘
                 │                     │
                 │                     ▼
                 │         ┌──────────────────────────┐
                 │         │   TECHNICAL FILTER CHECKS│
                 │         │ 1. Score >= 7?           │
                 │         │ 2. Has Required Langs?   │
                 │         └────┬──────────────┬──────┘
                 │              │              │
                 │        FAILED│              │PASSED
                 │              ▼              ▼
                 │  ┌──────────────────┐  ┌─────────────────┐
                 │  │  REJECT          │  │ Set Status:     │
                 │  │  Stop Chain      │  │ PENDING         │
                 │  │                  │  │ Pass to Manager │
                 │  └────────┬─────────┘  │ Filter          │
                 │           │            └────────┬────────┘
                 │           │                     │
                 │           │                     ▼
                 │           │         ┌──────────────────────────┐
                 │           │         │  MANAGER FILTER CHECKS   │
                 │           │         │ 1. Overall Score >= 7.5? │
                 │           │         │    (Tech 60% +           │
                 │           │         │     Comm 40%)            │
                 │           │         └────┬──────────────┬──────┘
                 │           │              │              │
                 │           │        FAILED│              │PASSED
                 │           │              ▼              ▼
                 │           │  ┌──────────────────┐  ┌──────────────┐
                 │           │  │  REJECT          │  │  APPROVED    │
                 │           │  │  Stop Chain      │  │  Send Offer  │
                 │           │  │                  │  │              │
                 │           │  └────────┬─────────┘  └────────┬─────┘
                 │           │           │                     │
                 └───────────┴───────────┴─────────────────────┘
                             │
                             ▼
                    ┌─────────────────────┐
                    │ Display Result      │
                    │ Status: APPROVED/   │
                    │ REJECTED            │
                    │ Show Reason         │
                    │ Show Filter History │
                    └────────┬────────────┘
                             │
                             ▼
                            END
```

---

## 6. HANDLER DECISION TREE

```
Application Received
        │
        ▼
    ┌─────────────────────────────┐
    │   HR FILTER DECISION        │
    │                             │
    │ Is Experience >= 2?         │
    │   ├─ NO  → REJECT           │
    │   └─ YES → Next Check       │
    │                             │
    │ Is Communication >= 6?      │
    │   ├─ NO  → REJECT           │
    │   └─ YES → PASSED (Next)    │
    └─────────────────────────────┘
                │
          PASSED
                │
                ▼
    ┌─────────────────────────────┐
    │ TECHNICAL FILTER DECISION   │
    │                             │
    │ Is Score >= 7?              │
    │   ├─ NO  → REJECT           │
    │   └─ YES → Next Check       │
    │                             │
    │ Has Required Languages?     │
    │   ├─ NO  → REJECT           │
    │   └─ YES → PASSED (Next)    │
    └─────────────────────────────┘
                │
          PASSED
                │
                ▼
    ┌─────────────────────────────┐
    │ MANAGER FILTER DECISION     │
    │                             │
    │ Score = (Tech × 0.6) +      │
    │         (Comm × 0.4)        │
    │                             │
    │ Is Score >= 7.5?            │
    │   ├─ NO  → REJECT           │
    │   └─ YES → APPROVED         │
    └─────────────────────────────┘
```

---

## 7. OBJECT INTERACTION DIAGRAM

```
Step 1: Create Chain
─────────────────────

    ┌──────────────┐
    │   HRFilter   │
    │  (Handler 1) │
    └──────┬───────┘
           │ setNextHandler()
           ▼
    ┌──────────────────────┐
    │  TechnicalFilter     │
    │  (Handler 2)         │
    └──────┬───────────────┘
           │ setNextHandler()
           ▼
    ┌──────────────────────┐
    │  ManagerFilter       │
    │  (Handler 3)         │
    └──────────────────────┘


Step 2: Pass Request
─────────────────────

    JobApplication
        │
        │ handle()
        ▼
    HRFilter
        │
        │ canHandle() returns true?
        │   ├─ YES: record in history
        │   │       │
        │   │       │ handle()
        │   │       ▼
        │   │   TechnicalFilter
        │   │       │
        │   │       │ canHandle() returns true?
        │   │       │   ├─ YES: record in history
        │   │       │   │       │
        │   │       │   │       │ handle()
        │   │       │   │       ▼
        │   │       │   │   ManagerFilter
        │   │       │   │       │
        │   │       │   │       │ canHandle() returns true?
        │   │       │   │       │   └─ YES: APPROVED
        │   │       │   │       │   └─ NO:  REJECTED (STOP)
        │   │       │   │       │
        │   │       │   │   return to TechnicalFilter
        │   │       │   │
        │   │       │   └─ NO: REJECTED (STOP CHAIN)
        │   │       │
        │   │   return to HRFilter
        │   │
        │   └─ NO: REJECTED (STOP CHAIN)
        │
    return to Processor
        │
        ▼
    Display Result
```

---

## 8. COMPARISON: WITH vs WITHOUT PATTERN

### WITHOUT Pattern (Tight Coupling)

```
public void processApplication(JobApplication app) {
    // HR Check 1
    if (app.getExperience() < 2) {
        app.reject("Low experience");
        return;
    }
    
    // HR Check 2
    if (app.getComm() < 6) {
        app.reject("Low communication");
        return;
    }
    
    // Technical Check 1
    if (app.getTechnical() < 7) {
        app.reject("Low technical");
        return;
    }
    
    // Technical Check 2
    if (!hasLanguages(app)) {
        app.reject("No languages");
        return;
    }
    
    // Manager Check
    double score = (app.getTechnical() * 0.6) + 
                   (app.getComm() * 0.4);
    if (score >= 7.5) {
        app.approve();
    } else {
        app.reject("Low overall score");
    }
}

PROBLEMS:
- All logic in one method (God Object)
- Hard to add new filter (modify this method)
- Can't test filters independently
- Hard to reorder checks
- Lots of if-else nesting
```

### WITH Pattern (Loose Coupling)

```
// Create handlers independently
HRFilter hr = new HRFilter();
TechFilter tech = new TechFilter();
ManagerFilter manager = new ManagerFilter();

// Chain them
hr.setNextHandler(tech);
tech.setNextHandler(manager);

// Process (automatic flow)
hr.handle(application);

BENEFITS:
- Each handler is independent
- Easy to add new filter (create new class)
- Test each filter in isolation
- Easy to reorder (change setNextHandler order)
- Clean, readable code
- Follows design principles
```

---

## 9. HANDLER RESPONSIBILITY BREAKDOWN

```
┌──────────────────────────────────────────────────────────────┐
│                     JOB APPLICATION                          │
├──────────────────────────────────────────────────────────────┤
│ Candidate Data:                                              │
│ - Name: Raj Kumar                                            │
│ - Experience: 5 years                                        │
│ - Communication: 8.5/10                                      │
│ - Technical: 8.8/10                                          │
│ - Languages: Java, Python, JavaScript                        │
└──────────────────────────────────────────────────────────────┘
                         │
                         │
        ┌────────────────┴────────────────┐
        │                                 │
        ▼                                 ▼
    ┌────────────────┐          ┌──────────────────┐
    │  HR FILTER     │          │  VERIFY NAME?    │
    │                │          │  - Check format  │
    │ RESPONSIBLE    │          └──────────────────┘
    │ FOR:           │
    │                │          ┌──────────────────┐
    │ 1. Experience  │          │ CHECK EXPERIENCE │
    │    >= 2 years  │          │ - Need 5, Have 5 │
    │                │          │ - PASS           │
    │ 2. Communication
    │    >= 6.0      │          └──────────────────┘
    │                │
    │ If ANY Check   │          ┌──────────────────┐
    │ FAILS:         │          │ COMMUNICATION    │
    │ - Set Status   │          │ - Need 6, Have   │
    │   to REJECTED  │          │   8.5 - PASS     │
    │ - Stop Chain   │          └──────────────────┘
    │                │
    │ If ALL Pass:   │
    │ - Record in    │
    │   history      │
    │ - Continue to  │
    │   next filter  │
    └────────────────┘
        │
        │ (if passed)
        ▼
    ┌────────────────┐
    │ TECH FILTER    │
    │                │
    │ RESPONSIBLE    │
    │ FOR:           │
    │                │
    │ 1. Technical   │
    │    Score >= 7  │
    │    - Need 7    │
    │    - Have 8.8  │
    │    - PASS      │
    │                │
    │ 2. Languages   │
    │    - Need Java/│
    │      Python/JS │
    │    - Have all  │
    │    - PASS      │
    │                │
    │ If ANY Check   │
    │ FAILS:         │
    │ - REJECT       │
    │ - STOP CHAIN   │
    │                │
    │ If ALL Pass:   │
    │ - Record       │
    │ - Continue     │
    └────────────────┘
        │
        │ (if passed)
        ▼
    ┌────────────────┐
    │ MANAGER FILTER │
    │                │
    │ RESPONSIBLE    │
    │ FOR:           │
    │                │
    │ Calculate:     │
    │ Overall Score  │
    │ = (Tech × 0.6) │
    │ + (Comm × 0.4) │
    │ = (8.8 × 0.6) +
    │   (8.5 × 0.4)  │
    │ = 5.28 + 3.4   │
    │ = 8.68         │
    │                │
    │ Check:         │
    │ 8.68 >= 7.5?   │
    │ YES - APPROVED │
    │                │
    │ Final Decision:│
    │ - APPROVED     │
    │ - STOP CHAIN   │
    └────────────────┘
        │
        ▼
    ┌─────────────────┐
    │ FINAL STATUS    │
    │                 │
    │ Status: APPROVED│
    │ Filters: [HR,   │
    │          TECH,  │
    │          MANAGER]
    │                 │
    │ Send offer to   │
    │ candidate       │
    └─────────────────┘
```

---

## 10. TIME COMPLEXITY & PERFORMANCE

```
Best Case (APPROVED):
├─ HR Filter:      2 checks  = O(1)
├─ Tech Filter:    2 checks  = O(1)
├─ Manager Filter: 1 check   = O(1)
└─ Total: O(1) - constant time

Worst Case (REJECTED at last stage):
├─ HR Filter:      2 checks  = O(1)
├─ Tech Filter:    2 checks  = O(1)
├─ Manager Filter: 1 check   = O(1)
└─ Total: O(1) - constant time

Rejection at early stage (Most Efficient):
├─ HR Filter:      1 check   = O(1)
├─ Stops here
└─ Total: O(1) - fastest!

Space Complexity:
└─ O(n) where n = number of handlers in chain
   (each handler stores reference to next)
```

---

## 11. TESTING STRATEGY

```
Unit Tests (for each handler):
┌───────────────────┐
│ HRFilterTest      │
├───────────────────┤
│ testPassGood()    │
│ testFailLowExp()  │
│ testFailLowComm() │
└───────────────────┘

┌───────────────────┐
│ TechFilterTest    │
├───────────────────┤
│ testPassGood()    │
│ testFailLowScore()│
│ testFailNoLang()  │
└───────────────────┘

┌───────────────────┐
│ ManagerFilter Test│
├───────────────────┤
│ testApprove()     │
│ testReject()      │
└───────────────────┘

Integration Tests (for chain):
┌───────────────────────────┐
│ ChainIntegrationTest      │
├───────────────────────────┤
│ testFullChainApproved()   │
│ testFailAtHR()            │
│ testFailAtTech()          │
│ testFailAtManager()       │
│ testChainOrder()          │
└───────────────────────────┘
```


