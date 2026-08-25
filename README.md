# Data Structures and Time Complexity Analysis (AED - Trabalho 3)

A Java implementation of custom non-linear and hashing data structures developed for the Algorithms and Data Structures (*Algoritmos e Estruturas de Dados* - AED) course. This project focuses on custom tree structures, hash table bucket strategies, and empirical temporal analysis benchmarking.

---

## 📌 Features

- **Custom Tree Implementations (`aed.trees`):**
  - Node contract interface (`IUAlgTreeNode`) defining structural tree node behaviors.
  - Concrete tree data structure (`UAlgTree`) supporting fundamental tree operations and traversals.

- **Hash Table Structures (`aed.tables`):**
  - Bucket abstraction interface (`IUAlshBucket`) for collision handling.
  - Hash table implementation (`UAlshTable`) with customized hashing and bucket management.

- **Benchmarking & Time Complexity Analysis (`aed.utils`):**
  - Performance measurement tools (`TemporalAnalysisUtils`) for empirical runtime evaluations.
  - Automated execution harness via `Main.java` to test structure operations across varying dataset sizes.

---

## 🏗 Project Structure


```

Trabalho3AED/
├── src/
│   └── aed/
│       ├── Main.java                      # Main execution & benchmarking entry point
│       ├── tables/
│       │   ├── IUAlshBucket.java          # Bucket interface for hash collision handling
│       │   └── UAlshTable.java            # Hash table data structure implementation
│       ├── trees/
│       │   ├── IUAlgTreeNode.java         # Tree node interface
│       │   └── UAlgTree.java              # Custom tree structure implementation
│       └── utils/
│           └── TemporalAnalysisUtils.java # Utility methods for execution time benchmarking
└── Trabalho 3.iml                         # IntelliJ IDEA project module configuration

---
```
## 🚀 Getting Started

### Prerequisites

- **Java Development Kit (JDK):** Version 8 or higher (JDK 17+ recommended).

### Compilation & Execution

#### 1. Compile the Project
From the root directory, compile all source files into a target `bin/` directory:

```bash
mkdir -p bin
javac -d bin -sourcepath src src/aed/Main.java src/aed/tables/*.java src/aed/trees/*.java src/aed/utils/*.java

```

#### 2. Run the Benchmark

Execute the `Main` class to run data structure operations and print temporal analysis metrics:

```bash
java -cp bin aed.Main

```

---

## 📊 Benchmarking & Analysis

The utility class `TemporalAnalysisUtils` measures operation runtimes (e.g., search, insertion, and deletion) to validate theoretical asymptotic bounds ($O(1)$, $O(\log n)$, $O(n)$) against experimental results.

```
