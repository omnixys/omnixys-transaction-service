# omnixys-transaction-service

**omnixys-transaction-service** ist ein Microservice innerhalb der [OmnixysSphere](https://github.com/omnixys) Plattform. Er verwaltet und verarbeitet finanzielle Transaktionen zuverlässig und nachvollziehbar. Dabei werden Sicherheit, Integrität und Tracing durch moderne Technologien wie Keycloak, Kafka und OpenTelemetry gewährleistet.

---

## 🔍 Überblick

* 📈 Transaktionsverarbeitung mit Historien-Tracking
* 🔐 Integrierte Authentifizierung & Autorisierung via Keycloak
* 🧩 Teil des modularen Omnixys-Ökosystems
* 🌐 GraphQL-API für flexible Abfragen und Mutationen
* 🔄 Eventbasierte Kommunikation via Kafka
* 🧭 Vollständig observierbar (Tracing, Metriken, Logs)

---

## ⚙️ Tech Stack

| Technologie           | Zweck                  |
| --------------------- | ---------------------- |
| Java (Spring Boot)    | Haupt-Framework        |
| PostgreSQL            | Datenpersistenz        |
| GraphQL               | Schnittstelle/API      |
| Kafka                 | Event-Streaming        |
| OpenTelemetry + Tempo | Distributed Tracing    |
| Prometheus + Grafana  | Monitoring             |
| Keycloak              | AuthN / AuthZ          |
| LoggerPlus            | JSON Logging mit Kafka |

---

## 🚀 Schnellstart

### 1. Klonen & Setup

```bash
git clone https://github.com/omnixys/omnixys-transaction-service.git
cd omnixys-transaction-service
./gradlew build
```

### 2. Starten

```bash
docker-compose up
```

---

## 🔐 Sicherheit

Sicherheitslücken? Bitte keine öffentlichen Issues – kontaktiere stattdessen:

📧 **[security@omnixys.com](mailto:security@omnixys.com)**

Siehe [SECURITY.md](./SECURITY.md)

---

## 🔧 Mitwirken

Du möchtest helfen? Super! Lies bitte unsere [CONTRIBUTING.md](./CONTRIBUTING.md), bevor du einen Pull Request eröffnest.

---

## 🧪 Tests

```bash
./gradlew test
```

Code Coverage wird via JaCoCo + SonarQube erfasst.

---

## 📦 Portkonvention

Dieser Service läuft standardmäßig auf **Port `7203`** (vgl. [port-konvention.md](../port-konvention.md)).

---

## 📝 Lizenz

Veröffentlicht unter der [GPLv3](./LICENSE) – © 2025 Omnixys

> **Omnixys – The Fabric of Modular Innovation**
