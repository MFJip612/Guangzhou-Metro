# 修复 ModCreativeTab.java 并挂载创造模式物品栏 — Implementation Plan

> Status: APPROVED
> Source: user request
> Mode: --quick
> Iterations: 1 / 3
> Author: trae-remote-official:dev-skills:dev-plan
> Last updated: 2026-07-20

## Requirements summary

当前 `ModCreativeTab.java` 在游戏启动时崩溃,抛出 `IllegalArgumentException: Builtin tab ... is not registered!`。需要依据 Architectury v9.x 的官方文档,把创造模式物品栏正确注册到 `Registries.CREATIVE_MODE_TAB` 注册表中,使 `ModItems.COMPANY_LOGO` 与 `ModBlocks.STATION_BLOCK` 的 BlockItem 能够成功挂载到自定义创造物品栏 `gzmtr:gzmtr_tab`。

## Acceptance criteria

- AC-1: `./gradlew :fabric:build` 编译通过,无 Java 编译错误。
- AC-2: `./gradlew :fabric:runClient`(或等效任务)能成功进入游戏主菜单,不再抛出 `Builtin tab ... is not registered!` 崩溃。
- AC-3: 在创造模式物品栏列表中可以看到名为 `gzmtr:gzmtr_tab` 的标签页(显示文本来自现有语言文件 `itemGroup.gzmtr.gzmtr_tab` = "Guangzhou Metro" / "广州地铁")。
- AC-4: 该标签页的图标为 `gzmtr:company_logo` 物品。
- AC-5: 该标签页中包含 `gzmtr:company_logo` 物品与 `gzmtr:station_block` 方块物品(二者均通过 `arch$tab(GZMTR_TAB)` 挂载)。
- AC-6: `ModItems.java` 与 `ModBlocks.java` 不需要任何改动即可正常工作(因为 `RegistrySupplier<CreativeModeTab>` 实现了 `DeferredSupplier<CreativeModeTab>`,与现有 `arch$tab(GZMTR_TAB)` 调用兼容)。

## Quick mode rationale

本任务为单文件修复,改动面 < 50 行,且根因已在崩溃报告中明确(`Builtin tab ... is not registered!`)。Architectury 官方文档与 v9.2.14 源码(`1.20` 分支)给出了标准模式。无架构决策需多方权衡,采用 `--quick` 模式即可。

## Current State Analysis

### 项目栈
- Minecraft 1.20.1,Architectury API `9.2.14`,Fabric Loader `0.19.3`,Forge `1.20.1-47.4.10`
- 多平台架构:`common`(共享逻辑) + `fabric` + `forge`
- 入口:`ExampleModFabric.onInitialize()` → `GzMtr.init()`

### 根因分析(来自 `fabric/run/crash-reports/crash-2026-07-20_00.18.20-client.txt`)

```
Caused by: java.lang.IllegalArgumentException: Builtin tab net.minecraft.world.item.CreativeModeTab@4defb598 is not registered!
    at knot//dev.architectury.registry.fabric.CreativeTabRegistryImpl.ofBuiltin(CreativeTabRegistryImpl.java:55)
    at knot//dev.architectury.registry.CreativeTabRegistry.ofBuiltin(CreativeTabRegistry.java)
    at knot//dev.architectury.registry.CreativeTabRegistry.appendBuiltin(CreativeTabRegistry.java:124)
    at knot//net.minecraft.world.item.Item.handler$zmc000$architectury$init(Item.java:1551)
    at knot//net.minecraft.world.item.Item.<init>(Item.java:113)
    at knot//top.waterspo.gzmtraddons.item.CompanyLogo.<init>(CompanyLogo.java:8)
    at knot//top.waterspo.gzmtraddons.item.ModItems.lambda$static$0(ModItems.java:14)
```

崩溃发生在 `ModItems.register()` 创建 `CompanyLogo` 物品时,Architectury 注入到 `Item.<init>` 的 `handler$zmc000$architectury$init` 钩子调用了 `appendBuiltin(tab)` → `ofBuiltin(tab)` → `BuiltInRegistries.CREATIVE_MODE_TAB.getKey(tab)` 返回 `null`(因为该 tab 从未被注册)→ 抛出异常。

### 当前 `ModCreativeTab.java` 的问题

```java
public static final CreativeTabRegistry.TabSupplier GZMTR_TAB = CreativeTabRegistry.create(
        new ResourceLocation(GzMtr.MOD_ID, "gzmtr_tab"),
        () -> new ItemStack(ModItems.COMPANY_LOGO.get())
);
public static void register() {
    // No additional actions needed for now.   <-- 空 register()!
}
```

1. `register()` 是空方法,**从未把 tab 注册到 `Registries.CREATIVE_MODE_TAB` 注册表**。
2. 当前 API 用法不符合 Architectury v9.x 官方模式。根据 v9.x 源码(`1.20` 分支 `CreativeTabRegistry.java`)与官方文档(docs.architectury.dev/api/registries/common-registries/creative-tabs):
   - `CreativeTabRegistry.create(Component title, Supplier<ItemStack> icon)` 返回 `CreativeModeTab` 实例
   - 源码注释明确写道:**"This has to be registered manually."**
   - 标准模式:用 `DeferredRegister<CreativeModeTab>` + `Registries.CREATIVE_MODE_TAB` 来注册,然后调用 `TABS.register()`。
3. `arch$tab(GZMTR_TAB)` 走到了 `arch$tab(CreativeModeTab)` 重载 → `appendBuiltin(tab)`,而 builtin 重载要求 tab 已注册。应改用 `arch$tab(DeferredSupplier<CreativeModeTab>)` 重载 → `append(supplier)`(惰性调度),由 `RegistrySupplier<CreativeModeTab>` 提供。

### 关联文件(已读完)
- [common/src/main/java/top/waterspo/gzmtraddons/GzMtr.java](file:///d:/Documents/source/IdeaProjects/guangzhou_metro_add-ons-1.20.1-fabric-forge-template/common/src/main/java/top/waterspo/gzmtraddons/GzMtr.java) — 调用 `ModCreativeTab.register()`、`ModBlocks.register()`、`ModItems.register()`,顺序正确无需改动。
- [common/src/main/java/top/waterspo/gzmtraddons/item/ModItems.java](file:///d:/Documents/source/IdeaProjects/guangzhou_metro_add-ons-1.20.1-fabric-forge-template/common/src/main/java/top/waterspo/gzmtraddons/item/ModItems.java) — 使用 `arch$tab(GZMTR_TAB)`,新方案类型兼容,无需改动。
- [common/src/main/java/top/waterspo/gzmtraddons/block/ModBlocks.java](file:///d:/Documents/source/IdeaProjects/guangzhou_metro_add-ons-1.20.1-fabric-forge-template/common/src/main/java/top/waterspo/gzmtraddons/block/ModBlocks.java) — BlockItem 同样使用 `arch$tab(GZMTR_TAB)`,无需改动。
- [common/src/main/resources/assets/gzmtr/lang/en_us.json](file:///d:/Documents/source/IdeaProjects/guangzhou_metro_add-ons-1.20.1-fabric-forge-template/common/src/main/resources/assets/gzmtr/lang/en_us.json) 与 `zh_cn.json` — 已包含 `itemGroup.gzmtr.gzmtr_tab` 翻译键,无需改动。

## Proposed Changes

### 唯一改动文件:`common/src/main/java/top/waterspo/gzmtraddons/item/ModCreativeTab.java`

**改动思路**:重写为 Architectury v9.x 官方推荐的 `DeferredRegister<CreativeModeTab>` 模式,把 tab 真正注册到 `Registries.CREATIVE_MODE_TAB`,并通过 `RegistrySupplier<CreativeModeTab>` 暴露给 `ModItems` / `ModBlocks`(类型兼容现有 `arch$tab(GZMTR_TAB)` 调用,无需改动)。

**改动后的文件内容**:

```java
package top.waterspo.gzmtraddons.item;

import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import top.waterspo.gzmtraddons.GzMtr;

public class ModCreativeTab {
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(GzMtr.MOD_ID, Registries.CREATIVE_MODE_TAB);

    public static final RegistrySupplier<CreativeModeTab> GZMTR_TAB = TABS.register("gzmtr_tab", () ->
            CreativeTabRegistry.create(
                    Component.translatable("itemGroup.gzmtr.gzmtr_tab"),
                    () -> new ItemStack(ModItems.COMPANY_LOGO.get())
            )
    );

    public static void register() {
        TABS.register();
    }
}
```

**关键差异点**:
1. 引入 `DeferredRegister<CreativeModeTab>` 与 `Registries.CREATIVE_MODE_TAB`。
2. `GZMTR_TAB` 类型从 `CreativeTabRegistry.TabSupplier` 改为 `RegistrySupplier<CreativeModeTab>`。
   - `RegistrySupplier<T>` 实现 `DeferredSupplier<T>`,因此 `arch$tab(GZMTR_TAB)` 会走 `arch$tab(DeferredSupplier<CreativeModeTab>)` 重载(惰性调度,不要求 tab 已注册),而非 `arch$tab(CreativeModeTab)` 重载(`appendBuiltin`,要求 tab 已注册)。
3. `CreativeTabRegistry.create(Component, Supplier<ItemStack>)` 重载返回 `CreativeModeTab` 实例,作为 `TABS.register(name, supplier)` 的工厂回调。
   - `Component.translatable("itemGroup.gzmtr.gzmtr_tab")` 与现有语言文件键一致。
   - 图标 `() -> new ItemStack(ModItems.COMPANY_LOGO.get())` 保持不变。
4. `register()` 改为 `TABS.register()`,真正把 `DeferredRegister` 挂载到 `RegisterEvent`。

**不需要改动的文件**:
- `GzMtr.java` — 已正确调用 `ModCreativeTab.register()` 在最前。
- `ModItems.java` — `arch$tab(GZMTR_TAB)` 调用保持不变,新类型 `RegistrySupplier<CreativeModeTab>` 自动匹配 `DeferredSupplier<CreativeModeTab>` 重载。
- `ModBlocks.java` — 同上。
- 语言文件 — 键 `itemGroup.gzmtr.gzmtr_tab` 已存在,且与注册名 `gzmtr:gzmtr_tab` 一致。

## Implementation steps

1. **重写 `common/src/main/java/top/waterspo/gzmtraddons/item/ModCreativeTab.java`** — 替换整个文件为上文 "改动后的文件内容",16 行,涵盖:
   - `DeferredRegister<CreativeModeTab> TABS` 静态字段
   - `RegistrySupplier<CreativeModeTab> GZMTR_TAB` 静态字段(由 `TABS.register` 创建)
   - `register()` 调用 `TABS.register()`
2. **编译验证** — 运行 `./gradlew :fabric:build`(或 `gradlew.bat :fabric:build` on Windows)确认无编译错误。
3. **运行时验证** — 运行 `./gradlew :fabric:runClient` 进入游戏,确认无崩溃且创造物品栏出现 `gzmtr:gzmtr_tab` 标签页。
4. **删除旧崩溃日志(可选)** — 清理 `fabric/run/crash-reports/crash-2026-07-20_00.18.20-client.txt`,避免后续混淆。

## Workspace setup

- 实施前运行 `git status --short` 与 `git branch --show-current`。
- 当前 working tree 是否干净需由用户确认。如果脏,本 plan 仅修改 `ModCreativeTab.java` 一个文件,容易隔离。
- 本任务为单文件修复,不强制要求 worktree;若当前在 `main` / `master` / `release/*` 分支且 working tree 干净,可考虑 `git worktree add -b fix/creativetab-mount ../guangzhou_metro_add-ons-fix-creativetab`,否则直接在当前分支修改即可。

## Risks & mitigations

| Risk | Mitigation |
|---|---|
| `RegistrySupplier<CreativeModeTab>` 类型不被 `arch$tab` 接受 | 已验证 v9.x `InjectedItemPropertiesExtension` 源码包含 `arch$tab(DeferredSupplier<CreativeModeTab>)` 重载;`RegistrySupplier<T>` 实现 `DeferredSupplier<T>`,类型兼容。 |
| Forge 平台行为不同 | Architectury 的 `DeferredRegister` / `CreativeTabRegistry` 是跨平台 API,Fabric 与 Forge 行为一致;`@ExpectPlatform` 由各平台实现保证。 |
| `Component.translatable("itemGroup.gzmtr.gzmtr_tab")` 翻译键不生效 | 已验证 `en_us.json` 与 `zh_cn.json` 均包含该键,值为 "Guangzhou Metro" / "广州地铁"。 |
| 注册顺序问题(tab 在 item 之后才注册) | `arch$tab(DeferredSupplier)` 走 `append(supplier)` 路径,调度到 `ItemGroupEvents.MODIFY_ENTRIES_ALL` 事件,在 tab 注册完成后再执行 append,顺序无影响。 |
| Forge 端未做对应验证 | 本 plan 仅修复 common 模块;Forge 端无需特殊改动,build 时会自动包含 common 改动。建议在 Fabric 验证通过后,补一次 `./gradlew :forge:build` 确认编译。 |

## Verification steps

- **AC-1 编译通过**:运行 `./gradlew :fabric:build` → 期望 `BUILD SUCCESSFUL`,无 Java 编译错误。
- **AC-2 不再崩溃**:运行 `./gradlew :fabric:runClient` → 期望游戏成功启动到主菜单,`fabric/run/crash-reports/` 下不再有新生成的崩溃报告。
- **AC-3 物品栏出现**:在游戏中按 `E` 打开创造物品栏 → 期望看到标签页 "Guangzhou Metro"(英文)或 "广州地铁"(中文)。
- **AC-4 图标正确**:该标签页图标为 `gzmtr:company_logo` 物品(其纹理为 `assets/gzmtr/textures/item/logo.png`)。
- **AC-5 物品挂载**:点击该标签页 → 期望看到 `Company Logo` 物品 与 `Station Block` 方块物品。
- **AC-6 兼容性验证**:`ModItems.java` 与 `ModBlocks.java` 的 git diff 应为空(未改动),证明新 `GZMTR_TAB` 类型与现有 `arch$tab(GZMTR_TAB)` 调用兼容。
- **额外 Forge 编译验证**:`./gradlew :forge:build` → 期望 `BUILD SUCCESSFUL`。

## ADR

- **Decision**: 重写 `ModCreativeTab.java` 为 Architectury v9.x 官方推荐的 `DeferredRegister<CreativeModeTab>` + `RegistrySupplier<CreativeModeTab>` 模式,通过 `TABS.register()` 真正把 tab 注册到 `Registries.CREATIVE_MODE_TAB`。
- **Drivers**: 1) 修复运行时崩溃(`Builtin tab ... is not registered!`)。2) 与 Architectury v9.x 官方文档与源码保持一致。3) 保持 `ModItems` / `ModBlocks` 不被波及(最小代码原则)。
- **Alternatives considered**:
  - **Option A(已选): `DeferredRegister<CreativeModeTab>` + `RegistrySupplier<CreativeModeTab>` 模式**。chosen — 与官方文档示例完全对齐,跨平台行为一致,`RegistrySupplier` 实现 `DeferredSupplier` 自动兼容现有 `arch$tab` 调用。
  - **Option B(rejected): 直接调用 `BuiltInRegistries.CREATIVE_MODE_TAB.register(...)` 在 `register()` 中手工注册**。rejected — 绕过 Architectury 的 `DeferredRegister` 机制,失去跨平台保证,且 Fabric 上 `RegisterEvent` 时机与直接 register 不一致,可能引入新的时序问题。
  - **Option C(rejected): 保留 `CreativeTabRegistry.create(ResourceLocation, ...)` 旧 API,在 `register()` 中补一次 `CreativeTabRegistry` 注册调用**。rejected — v9.x 源码中无 `CreativeTabRegistry.register(modId)` 之类的批量注册方法,且旧 API 已被官方文档明确替换为新模式;继续使用旧 API 不可持续。
- **Why chosen**: 与官方文档 100% 对齐,行为可预期,改动面最小(单文件),不需要触碰 `ModItems` / `ModBlocks` / `GzMtr`。
- **Consequences**:
  - 正面: 修复崩溃,创造物品栏可用;后续维护时遵循标准模式,易于升级到更高版本 Architectury。
  - 负面: 无。新 API 略多两行(引入 `TABS`),但换来正确性与可维护性。
- **Follow-ups**:
  - 可考虑为 `ModItems.COMPANY_LOGO` 显式调用 `setId(ResourceKey)`,以符合 v26.x 文档要求(v9.x 暂不强制)。本次不做,属于 Out of scope。

## Review trail

- Planner draft v1: 单 option 方案(`DeferredRegister<CreativeModeTab>` 模式),依据为 v9.x 源码与官方文档。
- (quick mode, 跳过 Architect / Critic 完整 pass)
- Final iterations: 1 / 3
