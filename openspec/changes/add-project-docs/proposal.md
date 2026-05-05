## Why

项目当前仅有根目录 README.MD 介绍，缺少结构化、可维护的文档位置。将项目功能说明整理并放入 `docs/` 目录，便于新人上手、后续维护与对外说明，且与代码库一起版本管理。

## What Changes

- 在 `docs/` 下新增项目功能说明文档，系统描述多知堂（dzt）项目的定位、架构、模块与核心能力。
- 文档包含：项目概述、技术栈、模块说明、核心功能（微信公众号接入、ChatGPT 对话、限流与试用策略、上下文与自定义 API Key）、配置与运行说明。
- 不修改现有代码或配置行为，仅增加文档产出物。

## Capabilities

### New Capabilities

- `project-docs`: 在 docs 目录下提供项目功能说明文档，覆盖项目介绍、架构与模块、功能说明、配置与使用方式。

### Modified Capabilities

- 无（仅新增文档，不改变现有需求或规格）。

## Impact

- **新增**：`docs/` 目录下的 Markdown 文档（如 `docs/README.md` 或 `docs/project-overview.md`）。
- **可选**：在根目录 README.MD 中增加指向 `docs/` 的链接（不强制）。
- 不影响现有代码、API、依赖或运行时行为。
