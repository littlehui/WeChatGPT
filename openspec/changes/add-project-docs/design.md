## Context

dzt（多知堂）为 Spring Boot 多模块项目，当前说明集中在根目录 README.MD，内容涵盖功能介绍与运行步骤，但缺少结构化文档目录。本次变更仅新增 `docs/` 下的项目功能说明文档，不改变任何代码或配置行为。

## Goals / Non-Goals

**Goals:**

- 在 `docs/` 下提供一份项目功能说明文档，便于理解项目定位、架构、模块与核心能力。
- 文档内容基于现有代码与 README 提炼，与实现一致。
- 文档以 Markdown 形式存放，便于版本管理与后续扩展。

**Non-Goals:**

- 不新增或修改 API、配置项或运行时行为。
- 不替代根目录 README.MD，可与之并存并在 README 中可选增加指向 docs 的链接。
- 不涉及自动化文档生成（如 JavaDoc 导出、OpenAPI 文档迁移）。

## Decisions

- **文档位置**：使用项目内已有 `docs/` 目录，新增主文档（如 `docs/README.md` 或 `docs/project-overview.md`）。选择 `docs/` 与常见“文档集中放 docs”的惯例一致。
- **文档格式与范围**：单文件或少量 Markdown，包含：项目概述、技术栈、模块说明、核心功能（公众号接入、ChatGPT 对话、限流/试用、上下文与自定义 API Key）、配置与运行说明。不引入额外文档框架（如 MkDocs、VitePress），以降低维护成本。
- **内容来源**：以现有 `README.MD`、`pom.xml`、主要 Controller/Service（如 `AppIndexController`、`ChatServiceImpl`、`CommandFactory`）及配置为事实依据，保证文档与实现一致。

## Risks / Trade-offs

- **文档与代码不同步**：文档为手写，后续功能变更可能忘记更新。Mitigation：在变更说明或 PR 检查中提醒“若涉及行为变更，需同步更新 docs”。
- **docs 目录已有文件**：当前 `docs/` 下存在类似笔记文件（如 `dzt`）。Mitigation：新增文档使用清晰命名（如 `project-overview.md` 或 `README.md`），与既有文件区分，不覆盖现有文件内容。
