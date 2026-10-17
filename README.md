# Pizzeria Order System — Facade Pattern Assignment

This project demonstrates the **Facade Design Pattern** applied to an online pizzeria order processing system, coupled with strict adherence to **Clean Code** principles and resilient error handling via **Compensating Transactions (Rollback)**.

---

## 🍕 Architectural Overview

The application simulates placing pizza orders through a coordinated set of internal subsystems:

```
                      +-------------------+
                      |     Main (Client) |
                      +---------+---------+
                                |
                   calls placeOrder(OrderRequest)
                                |
                                v
                   +---------------------------+
                   |     PizzaOrderFacade      |
                   +--+-----+-------------+----+
                      |     |             |    |
       +--------------+     |             |    +---------------+
       |                    |             |                    |
       v                    v             v                    v
+--------------+    +--------------+  +--------------+  +--------------+
|  Inventory   |    |   Payment    |  |   Kitchen    |  |   Delivery   |
|   Service    |    |   Gateway    |  |   Service    |  |   Service    |
+--------------+    +--------------+  +--------------+  +--------------+
(Raw ingredients     (Processing &    (Multi-step       (Courier pool,
 & recipes)          refunds)          cooking)          route/ETA)
```

### Subsystems and Responsibilities:
1. **`InventoryService`**: Manages real, granular raw ingredients (`Dough`, `Tomato Sauce`, `Mozzarella`, `Pepperoni`, `Basil`, `Parmesan`, `Gorgonzola`) and recipes. Checks ingredient availability, reserves them, and restores them on demand. Independent of other subsystems.
2. **`PaymentGateway`**: Handles payment verification with `BigDecimal`, masks sensitive card numbers, issues transaction IDs, and supports refund operations for compensating transactions.
3. **`KitchenService`**: Simulates the real cooking process (dough stretching, stone oven baking at 450°C, slicing/packaging), tracks active kitchen capacity, and supports order cancellation as well as completion.
4. **`DeliveryService`**: Dispatches orders to an active pool of couriers (`Courier`), validates delivery zones, calculates estimated arrival times, generates unique tracking numbers, and handles courier release upon delivery cancellation or completion.
5. **`PizzaOrderFacade`**: The central facade providing a unified, simple API (`placeOrder(OrderRequest)`) that coordinates all 4 subsystems in sequence and initiates multi-stage rollback if any step fails.

---

## 🛡️ Multi-Stage Compensating Transactions (Rollback)

In real distributed and enterprise systems, failures can happen after intermediate steps have succeeded. The facade executes reverse compensating actions depending on which step fails:

| Failure Stage | Compensating Actions Taken |
| :--- | :--- |
| **Inventory** (Out of stock) | None needed (no state changed). |
| **Payment** (Declined) | Releases reserved ingredients back to stock. |
| **Kitchen** (Oven breakdown / overload) | Issues refund for payment + releases ingredients. |
| **Delivery** (Out of zone / no couriers) | Cancels kitchen order + issues refund + releases ingredients. |

---

## 🧼 Clean Code Principles (Identification & Justification)

The codebase strictly adheres to standard software engineering and Clean Code principles:

### 1. Single Responsibility Principle (SRP)
- **Concept:** Each class must have one, and only one, reason to change.
- **Application:**
  - `PizzaOrderFacade` only coordinates and orchestrates subsystems; it contains zero business calculations or raw cooking logic.
  - `InventoryService` is solely responsible for stock levels and recipe checking.
  - `PaymentGateway` only handles charge and refund transactions.
  - `KitchenService` manages cooking stages and oven capacity.
  - `DeliveryService` manages logistics and courier assignment.

### 2. Meaningful and Intention-Revealing Names
- **Concept:** Names should clearly describe what a class, method, or variable does, eliminating the need for explanatory comments.
- **Application:**
  - Methods: `checkAndReserveIngredients()`, `releaseIngredients()`, `processPayment()`, `cancelCooking()`, `createDeliveryTask()`, `completeDelivery()`.
  - Constants: `DECLINED_CARD_PREFIX = "0000"`, `OUT_OF_ZONE_KEYWORD = "Out of City"`, `INGREDIENT_MOZZARELLA = "Mozzarella"`.
  - Domain types: `OrderRequest`, `OrderResult`, `Courier`, `OutOfStockException`.

### 3. Encapsulation & Information Hiding (Facade Pattern)
- **Concept:** Clients should not depend on low-level implementation details or direct interactions between subsystems.
- **Application:**
  - The client (`Main`) interacts solely with `PizzaOrderFacade` using high-level DTOs (`OrderRequest` and `OrderResult`).
  - `Main` has **zero imports** from `pizzeria.subsystem.*`.
  - Subsystems have **zero knowledge of the Facade** and **zero dependencies on each other** (loose coupling).

### 4. Robust Error Handling with Custom Exceptions (No Magic Booleans)
- **Concept:** Functions should not return boolean flags or error codes to represent varied subsystem failure states. Clean Code emphasizes using explicit, domain-specific exceptions.
- **Application:**
  - Instead of returning `false`, subsystems throw meaningful checked domain exceptions inheriting from `PizzeriaException`:
    - `OutOfStockException` (includes details of which specific ingredient is depleted).
    - `PaymentFailedException`.
    - `KitchenException`.
    - `DeliveryUnavailableException`.
  - The facade catches these specific exceptions, logs actionable messages, triggers the appropriate rollback, and returns a detailed `OrderResult`.

### 5. Small, Focused Methods ("Do One Thing") & Resource Lifecycle
- **Concept:** Functions should do one thing, do it well, and do it only. Resources must be properly managed throughout their lifecycle.
- **Application:**
  - `compensate(...)` is dedicated strictly to rolling back completed actions.
  - `maskCard(...)` in `PaymentGateway` performs only card masking.
  - Subsystems properly release resources: upon order fulfillment, `completeCooking` and `completeDelivery` prevent leaks of oven slots or courier availability.

### 6. Accurate Domain Representation (No Magic Numbers & Precise Types)
- **Concept:** Avoid floating point rounding issues with currency and avoid magic strings/numbers.
- **Application:**
  - Monetary values use `BigDecimal` instead of `double` to prevent precision loss.
  - Card test prefixes and delivery zone keywords are extracted into constants inside their respective domain classes.

---

## 🎓 Defense & Architecture Q&A (Подготовка к защите)

### 1. Почему подсистемы не знают о фасаде?
- **Слабая связанность (Loose Coupling):** Подсистемы являются независимыми модулями. `PaymentGateway` или `InventoryService` можно переиспользовать в других сервисах (например, в приложении самовывоза или веб-магазине) без привязки к `PizzaOrderFacade`. Связь является строго однонаправленной: *Клиент $\rightarrow$ Фасад $\rightarrow$ Подсистемы*.

### 2. Чем Facade отличается от Mediator и Adapter?
- **Facade vs Adapter:**
  - *Adapter* приводит существующий несовместимый интерфейс одного класса к ожидаемому клиентом интерфейсу (отношение 1 к 1).
  - *Facade* объединяет и упрощает работу с целым комплексом подсистемных классов (отношение 1 ко многим), создавая новую удобную точку входа.
- **Facade vs Mediator:**
  - *Mediator* организует двустороннее взаимодействие между равноправными компонентами-коллегами («коллеги» знают о медиаторе и общаются через него, устраняя прямые связи между собой).
  - *Facade* организует одностороннее управление: фасад вызывает подсистемы, но подсистемы не знают о фасаде и не зависят от него.

### 3. Зачем нужен `compensate` и почему порядок отката обратный?
- В распределённых и модульных системах транзакция охватывает несколько изолированных сервисов (паттерн Saga). Если на шаге $N$ (например, доставка) происходит сбой, все предыдущие шаги $1 \dots N-1$ (кухня, оплата, склад) уже зафиксировали изменения в своем состоянии.
- Откат обязан выполняться в **строго обратном порядке** (LIFO), чтобы вернуть систему в исходное согласованное состояние без коллизий.

---

## 🚀 How to Compile and Run

### Prerequisites:
- Java JDK 17+ (tested with Java 25)

### Compilation:
From the root project directory:
```bash
javac -d bin (Get-ChildItem -Path src -Recurse -Filter *.java | ForEach-Object { $_.FullName })
```
*(On Linux/macOS: `javac -d bin $(find src -name "*.java")`)*

### Execution:
```bash
java -cp bin pizzeria.Main
```

### Demonstration Scenarios Covered in `Main`:
1. **Happy Path:** Successful order for Pepperoni (ingredients reserved, paid, cooked, courier assigned, ETA calculated).
2. **Out of Stock:** 4 Cheese fails at inventory (Gorgonzola is out of stock); fails fast without touching payment.
3. **Payment Failure:** Card starting with `0000` is declined; triggers automatic rollback of reserved ingredients.
4. **Kitchen Failure:** 'Inferno' triggers oven breakdown; triggers refund and ingredient rollback.
5. **Delivery Failure:** Address outside coverage zone triggers 3-stage cascading rollback (cancels cooking, refunds payment, restores ingredients).
