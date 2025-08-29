# Card BFF Pipeline - OOP (Kotlin + Spring MVC)

Design orientado a **interfaces**, cada passo em sua própria classe `Step`, composição por `Orchestrator` e `ParallelStep`.
Sem WebFlux; clientes via **RestClient** com timeouts; **Resilience4j** (Retry/CircuitBreaker) aplicado **nos métodos dos serviços**.
