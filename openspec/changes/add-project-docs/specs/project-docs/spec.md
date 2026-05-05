## ADDED Requirements

### Requirement: Project overview document exists under docs

The repository SHALL provide at least one Markdown document under the `docs/` directory that describes the project's purpose, high-level architecture, and how to run the application.

#### Scenario: Reader finds project overview in docs

- **WHEN** a reader opens the `docs/` directory
- **THEN** they find a document that includes a project overview (name and purpose of the project)

#### Scenario: Document describes architecture and modules

- **WHEN** a reader reads the project documentation in `docs/`
- **THEN** the document SHALL describe the main modules (e.g. dzt-web, dzt-service, dzt-dao, dzt-common, dzt-bean, dzt-message) and their roles

### Requirement: Core features are documented

The docs SHALL describe the core features: WeChat official account integration, ChatGPT-based Q&A flow, rate limiting and trial usage policy, context (association) support, and optional user-provided OpenAI API key.

#### Scenario: WeChat and ChatGPT behavior is documented

- **WHEN** a reader looks for how the app interacts with WeChat and ChatGPT
- **THEN** the documentation SHALL explain the WeChat endpoint usage and the ChatGPT question/answer flow (including confirmation when applicable)

#### Scenario: Limits and configuration are documented

- **WHEN** a reader looks for usage limits and configuration
- **THEN** the documentation SHALL mention rate limiting (e.g. per OpenID), trial limits when no API key is set, and the option to set a custom API key (e.g. via a command like `/setApiKey`)

### Requirement: Configuration and run instructions are in docs

The docs SHALL include how to configure the application (e.g. Redis, WeChat app credentials, OpenAI-related settings) and how to build and run the application.

#### Scenario: Config and run steps are available

- **WHEN** a reader wants to run the project locally or deploy it
- **THEN** the documentation SHALL describe the required configuration (e.g. profiles/application-dev.properties or equivalent) and build/run commands (e.g. Maven build and `java -jar` with profile)
