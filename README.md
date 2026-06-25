# Release Pilot

A release promotion management service built with Quarkus.
It tracks the lifecycle of application promotions across environments, coordinating commands such as approvals,
deployments, rollbacks, etc. via a mediator leveraging handlers backed by RabbitMQ and PostgreSQL.

## Tech Stack

- **Runtime**: Quarkus (Reactive, Hibernate Reactive + Panache)
- **Database**: PostgreSQL (reactive pg client)
- **Messaging**: RabbitMQ (SmallRye Reactive Messaging)
- **API**: Jakarta REST + Jackson
- **Validation**: Hibernate Validator

---

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```shell script
./gradlew quarkusDev
```

> **_NOTE:_** Quarkus now ships with a Dev UI, available in dev mode only at <http://localhost:8080/q/dev/>.

---

## Running with Docker Compose

### Prerequisites

- [Docker](https://docs.docker.com/get-docker/) and [Docker Compose](https://docs.docker.com/compose/) installed

### Build and start all services

From the project root:

```shell script
docker compose -f src/main/docker/docker-compose.yml up --build
```

This starts three containers:
| Container | Description | Port |  
|---|---|---|  
| `release-pilot-postgres` | PostgreSQL (18) database | `5432` |  
| `release-pilot-rabbitmq` | RabbitMQ (4) message broker | `5672` / `15672` |  
| `release-pilot-app` | Quarkus application | `8080` |

## Packaging and running the application locally

Build the application:

```shell script
./gradlew build
```

Run it:

```shell script
java -jar build/quarkus-app/quarkus-run.jar
```

To build an über-jar:

```shell script
./gradlew build -Dquarkus.package.jar.type=uber-jar
java -jar build/*-runner.jar
```

---

## API Reference

Base URL: `http://localhost:8080`

> Interactive API docs (Swagger UI) available at: <http://localhost:8080/q/swagger-ui/>

---

### Applications

#### `GET /applications/{id}/status`

Returns the current environment status of an application.

| Parameter | Type | Location | Required | Description    |
|-----------|------|----------|----------|----------------|
| `id`      | UUID | path     | ✅        | Application ID |

**Response:** `200 OK` — application environment status object.

---

#### `POST /applications/{id}/promotions`

Returns a paginated list of promotions for an application.

| Parameter | Type | Location | Required | Description    |
|-----------|------|----------|----------|----------------|
| `id`      | UUID | path     | ✅        | Application ID |

**Request body:**

```json
{
  "pageIndex": 1,
  "pageSize": 20
}
```

> Both `pageIndex` and `pageSize` must be greater than 0.

**Response:** `200 OK` — paginated promotions list.

---

### Promotions

#### `GET /promotions/{id}`

Returns the full history of a promotion.

| Parameter | Type | Location | Required | Description  |
|-----------|------|----------|----------|--------------|
| `id`      | UUID | path     | ✅        | Promotion ID |

**Response:** `201 Created` — promotion history object.

---

#### `POST /promotions`

Creates a new promotion request.

**Request body:**

```json
{
  "applicationId": "uuid",
  "applicationVersion": "1.2.3",
  "targetEnvironment": "STAGING",
  "requestedBy": "uuid"
}
```

**Response:** `201 Created`

```json
{
  "id": "uuid"
}
```

---

#### `POST /promotions/{id}/approve`

Approves a pending promotion.

| Parameter | Type | Location | Required |
|-----------|------|----------|----------|
| `id`      | UUID | path     | ✅        |

**Request body:**

```json
{
  "approverId": "uuid"
}
```

**Response:** `202 Accepted`

---

#### `POST /promotions/{id}/start-deployment`

Triggers deployment for an approved promotion.

| Parameter | Type | Location | Required |
|-----------|------|----------|----------|
| `id`      | UUID | path     | ✅        |

**Response:** `202 Accepted`

---

#### `POST /promotions/{id}/complete`

Marks a promotion as successfully completed.

| Parameter | Type | Location | Required |
|-----------|------|----------|----------|
| `id`      | UUID | path     | ✅        |

**Response:** `202 Accepted`

---

#### `POST /promotions/{id}/rollback`

Rolls back a promotion.

| Parameter | Type | Location | Required |
|-----------|------|----------|----------|
| `id`      | UUID | path     | ✅        |

**Request body:**

```json
{
  "reason": "Deployment caused latency spike"
}
```

**Response:** `202 Accepted`

---

#### `POST /promotions/{id}/cancel`

Cancels a promotion.

| Parameter | Type | Location | Required |
|-----------|------|----------|----------|
| `id`      | UUID | path     | ✅        |

**Request body:**

```json
{
  "cancelledBy": "uuid",
  "reason": "No longer needed"
}
```

**Response:** `202 Accepted`

---

## Creating a native executable

> **_NOTE:_** Not tested unfortunately, there are some issue with the reflection used heavily by hibernate and jackson
> So without a proper domain configuration, the native build will fail.

```shell script
./gradlew build -Dquarkus.native.enabled=true
```

Or using a container (no GraalVM required):

```shell script
./gradlew build -Dquarkus.native.enabled=true -Dquarkus.native.container-build=true
./build/release_pilot-1.0.0-SNAPSHOT-runner
```

See <https://quarkus.io/guides/gradle-tooling> for more details.