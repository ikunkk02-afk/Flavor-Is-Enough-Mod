# Flavor Is Enough Mod · 味真足模组

*A Minecraft Fabric mod where eating changes your body — inspired by the mukbang streamer 良子大胃袋 (Liangzi Big Stomach).*

---

## Overview

Flavor Is Enough adds a dynamic **Flavor → Obesity → Health** system to Minecraft. Every food you eat — whether from the mod or vanilla — affects your body shape, movement speed, and visual appearance. Eat too much junk and you'll grow wider, slower, and visibly heavier. Exercise to slim back down.

### Core Loop

```
Eat food → Flavor/Obesity/Health/Stomach change → Body scales in real-time → Exercise to recover
```

---

## Features

### Body Scale System (Continuous)

Obesity value (0–100) linearly drives Pehkui body scaling:

| Obesity | Width | Hitbox Height | Speed Penalty |
|---------|-------|---------------|---------------|
| 0 | 1.00× | 1.00× | 0% |
| 20 | 1.10× | 0.98× | −3.6% |
| 50 | 1.25× | 0.96× | −9% |
| 100 | 1.50× | 0.92× | −18% |

All values interpolate smoothly — no abrupt stage jumps. Pehkui provides built-in transition animations.

### Exercise & Recovery System

Real movement burns calories:

| Activity | Exercise | Stomach Drain | Obesity Drain | Detection |
|----------|----------|---------------|---------------|-----------|
| Sprinting | +1/sec | −1/sec | — | On-ground sprint + real horizontal movement |
| Swimming | +2/sec | −1/sec | −1/5s | Full 3D movement (horizontal + vertical) |
| Jumping | +1/jump | — | — | True jump (deltaY > 0.05), 1s cooldown |

**Anti-AFK**: requires actual position change, blocks mount/flight/spectator abuse.

**Recovery**: at 20 exercise points, obesity drops by 1–3 (scaled by severity), plus stomach drain and health gain. Body scale refreshes instantly.

### Custom Items (6 total)

**Foods** — eat to change your stats:

| Item | 味 | 胖 | 健 | 胃 | Description |
|------|----|----|----|----|-------------|
| Flavor Snack (味真足小零食) | +5 | +1 | 0 | +3 | Light snack |
| Big Stomach Bun (大胃袋包子) | +15 | +8 | 0 | +12 | High satiety |
| Oily Meat (油腻肉块) | +10 | +15 | −5 | +18 | Obesity bomb |
| Health Leaf (健康叶) | 0 | −1 | +6 | −3 | Slim-down aid |

**Tools**:

| Item | Use |
|------|-----|
| Measuring Tape (体态测量尺) | Right-click to check your current obesity value |
| Warning Card (健康提示卡) | Right-click to view health tips |

### Vanilla Food Integration

All vanilla foods affect mod stats via Mixin injection on `LivingEntity.eat()`:

- **Healthy** (apple, carrot, etc.) → +2 flavor / −1 obesity / +4 health
- **Staple** (bread, potato, etc.) → +4 / +1 / +1
- **Cooked meat** (steak, porkchop, etc.) → +6 / +3 / 0
- **Raw meat** → +6 / +3 / −2 health
- **Junk food** (cookie, cake, pie) → +8 / +6 / −2 health
- **Special** (golden apple, rotten flesh, etc.) → unique values

### Visual Effects

- **Fat Body Layer**: renders a belly overlay at obesity 40+, with small/medium/large/huge tiers. Limb thickening at obesity 60+. Supports Default and Slim player models.
- **HUD Panel**: semi-transparent overlay showing all 4 stats with progress bars and current body stage. Press **H** to toggle, **J** to enter drag-and-resize editor.

### Obesity Burden Status Effect

At obesity 60+, players periodically receive the **Obesity Burden** effect — brief Resistance buff offset by increased Hunger, simulating the strain of excess weight.

### Heavy Jump Impact

At obesity 40+, landing from a high fall creates a ground-slam effect (configurable).

---

## Technical

- **Minecraft**: 1.21.1
- **Loader**: Fabric
- **Java**: 21
- **Build**: Gradle + Fabric Loom (Mojang mappings)
- **Dependencies**: Fabric API, Pehkui, Cardinal Components API
- **Testing**: JUnit 5

### Build & Run

```bash
./gradlew build        # Compile + test + JAR
./gradlew runClient    # Launch game with mod
```

JAR output: `build/libs/flavor-is-enough-mod-<version>.jar`

### Controls

| Key | Action |
|-----|--------|
| H | Toggle HUD panel |
| J | Open HUD editor (drag to move, scroll to resize, ESC to save) |

---

## License

This project is licensed under the **MIT License**. See the [LICENSE](LICENSE) file for details.

---

# 中文说明

## 概述

味真足模组为 Minecraft 引入了一套动态的**「味真足 → 肥胖 → 健康」**数值系统。你吃的每一口食物——无论是模组自定义还是原版——都会实时影响你的体型、移动速度和外观。吃太多垃圾食品会让你变宽变慢；运动则能帮你瘦回来。

核心循环：**吃东西 → 数值变化 → Pehkui 体型缩放 → 运动恢复**

---

## 功能详情

### 体型缩放（连续线性）

肥胖值 0–100 驱动 Pehkui 缩放，线性插值无跳变：

| 肥胖 | 宽度 | 碰撞箱高度 | 移速惩罚 |
|------|------|-----------|---------|
| 0 | 1.00× | 1.00× | 0% |
| 20 | 1.10× | 0.98× | −3.6% |
| 50 | 1.25× | 0.96× | −9% |
| 100 | 1.50× | 0.92× | −18% |

### 运动恢复系统

真实移动消耗热量，满 20 运动值触发恢复，肥胖值下降 1–3 点（按档位递增）。

| 活动 | 运动值 | 胃袋消耗 | 肥胖消耗 | 检测方式 |
|------|--------|---------|---------|---------|
| 奔跑 | +1/秒 | −1/秒 | — | 着地疾跑 + 真实水平位移 |
| 游泳 | +2/秒 | −1/秒 | −1/5秒 | 完整 3D 位移（含垂直） |
| 跳跃 | +1/次 | — | — | 真跳跃检测，1秒冷却 |

**防挂机**: 检测实际位置变化，屏蔽骑乘/飞行/旁观模式。

### 自定义物品

**食物类**：

| 物品 | 味 | 胖 | 健 | 胃 | 说明 |
|------|----|----|----|----|------|
| 味真足小零食 | +5 | +1 | 0 | +3 | 轻量零食 |
| 大胃袋包子 | +15 | +8 | 0 | +12 | 高饱腹 |
| 油腻肉块 | +10 | +15 | −5 | +18 | 肥胖炸弹 |
| 健康叶 | 0 | −1 | +6 | −3 | 减脂回健康 |

**工具类**：

| 物品 | 用途 |
|------|------|
| 体态测量尺 | 右键查看当前肥胖值 |
| 健康提示卡 | 右键查看健康提示 |

### 原版食物整合

通过 Mixin 注入 `LivingEntity.eat()`，所有原版食物吃下后影响模组数值：苹果、胡萝卜等健康类 → 减胖加健康；熟肉 → 中等增胖；曲奇、蛋糕等高热量 → 大幅增胖扣健康。

### 视觉效果

- **肥胖体型渲染层**：肥胖 ≥40 显示肚子层（四档），≥60 四肢加厚。支持 Default/Slim 双模型。
- **HUD 面板**：半透明深色面板，四项数值 + 进度条 + 当前体态。按 **H** 开关，按 **J** 编辑（拖拽位置 + 滚轮调大小）。

### 其他系统

- **肥胖负担效果**：肥胖 ≥60 时周期性施加，提供短暂抗性但增加饥饿，模拟身体重负。
- **跳跃砸坑**：肥胖 ≥40 时从高处落地产生冲击效果。

---

## 技术栈

- Minecraft 1.21.1 | Fabric Loader | Java 21
- Gradle + Fabric Loom（Mojang 映射）
- 依赖：Fabric API、Pehkui、Cardinal Components API

---

## 开源协议

本项目采用 **MIT 协议** 开源，详见 [LICENSE](LICENSE) 文件。
