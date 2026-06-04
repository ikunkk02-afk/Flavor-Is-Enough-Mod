# Flavor Is Enough Mod · 味真足模组

*A Minecraft Fabric mod where eating changes your body — inspired by the mukbang streamer 良子大胃袋 (Liangzi Big Stomach).*

---

## Overview

Flavor Is Enough adds a dynamic **Flavor → Obesity → Health** system to Minecraft. Every food you eat — whether from the mod or vanilla — affects your body shape, movement speed, and visual appearance. Eat too much junk and you'll grow wider, slower, and visibly heavier. Exercise to slim back down.

New in this update: **趣味玩法 (Fun Mode)** — unlock the ultimate mukbang fantasy! Eat blocks, items, even mobs to grow infinitely stronger. Rare materials give more power. Also meet **良子 (Liangzi)** — a wandering NPC you can feed for mod rewards, and the gatekeeper who unlocks Fun Mode.

### Core Loop

```
Eat food → Flavor/Obesity/Health/Stomach change → Body scales in real-time → Exercise to recover
```

---

## Features

### 🍖 Liangzi NPC (良子)

A wandering NPC themed after the mukbang streamer 良子大胃袋, found naturally in 13 biome types.

| Feature | Detail |
|---------|--------|
| **Appearance** | Billboard texture that always faces the player |
| **Spawn** | Plains, Forests, Savanna, Taiga, Meadows, Cherry Grove + more (weight 20, groups 1–3) |
| **Spawn Egg** | Red with gold spots — found in the mod's Creative tab |
| **HP** | 50 (25 hearts), 50% knockback resistance |
| **Behavior** | Wanders, looks at players, picks up and eats nearby food drops |
| **Trade** | Right-click with meat (nutrition ≥3) → consumes meat, drops random mod food (1–2x) |
| **Voice** | Custom sound effects play randomly during trades and ambient idle (5s cooldown on trade spam) |

### 🎮 Fun Mode (趣味玩法) — Eat Everything!

Once enabled in config and unlocked via Liangzi, Fun Mode transforms the game into a mukbang power fantasy.

**Unlocking:**
1. Enable `Fun Mode` in the mod config (ModMenu → Flavor Is Enough Mod → 趣味玩法开关)
2. Find 良子, **sneak + right-click** him
3. Click `[确认上交两组肉类]` in the confirmation dialog — consumes 128 meat items
4. You'll be notified: Fun Mode is active!

**What you can do:**
| Action | Effect |
|--------|--------|
| Right-click air with any item | Eat it with eating animation (not instant!) |
| Right-click a mob | Bite it for damage (+ eat to grow stronger) |
| Place blocks normally | Aiming at a block still places it — no accidental eating |

**Rarity Power System (5 tiers):**

| Tier | Score | Examples | Eat Time |
|------|-------|----------|----------|
| Common (普通) | +1 | Dirt, stone, wood, sand | 0.8s |
| Useful (实用) | +3 | Iron, copper, coal, redstone, quartz | 1.0s |
| Rare (稀有) | +8 | Diamond, emerald, gold, obsidian | 1.4s |
| Special (特殊) | +12 | Ancient debris, crying obsidian, end stone | 1.8s |
| Godlike (神级) | +25 | Dragon egg, netherite block, beacon | 2.4s |

**Scaling (every 50 power = 1 tier):**
| Buff | Formula | Cap |
|------|---------|-----|
| Max Health | +4 per tier | ♾️ UNLIMITED — hearts go off-screen! |
| Speed | +1% per tier | 20 tiers |
| Attack Damage | +1 per tier | 20 tiers |
| Armor | +1 per tier | 20 tiers |
| Effects | Resistance, Regen, Fire Res, Night Vision, Strength, Haste, Absorption | Unlocks at tier thresholds |

**Quality of life in Fun Mode:**
- The mod HUD panel auto-hides (no clutter)
- Exercise prompts stop (you're too powerful to need workouts)
- Obesity/debuff system is disabled (no drawbacks — just pure power)

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

### Custom Items (7 total, including spawn egg)

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
| Liangzi Spawn Egg (良子刷怪蛋) | Summons the Liangzi NPC |

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

At obesity 60+, players periodically receive the **Obesity Burden** effect:
- **Resistance** — you're harder to knock around
- **Max Health Boost** — +4 HP per level (+2 more hearts to tank hits)
- **Hunger** — your massive body demands more food
- **Slowness** — moving that weight isn't easy

The idea: being fat makes you a tank, but it comes at a cost.

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

新版加入：**趣味玩法** — 解锁终极吃播幻想！吃方块、物品甚至生物，越吃越强，稀有材料给更多力量，血量无上限堆到出屏！还有 **良子 NPC** — 野外遇到的吃播 NPC，喂肉食换模组食物，也是解锁趣味玩法的守门人。

核心循环：**吃东西 → 数值变化 → Pehkui 体型缩放 → 运动恢复**

---

## 功能详情

### 🍖 良子 NPC

以大胃袋吃播良子为主题的 NPC，在 13 种群系自然生成。

| 特性 | 说明 |
|------|------|
| **外观** | Billboard 贴图，始终面朝玩家 |
| **生成** | 平原、森林、热带草原、针叶林、草甸、樱花树林等（权重 20，1–3 只/组） |
| **刷怪蛋** | 红底金斑，在模组创造模式物品栏中 |
| **血量** | 50（25心），50% 击退抗性 |
| **行为** | 闲逛、注视玩家、捡地上食物吃 |
| **交易** | 右键持肉食（营养值 ≥3）→ 消耗肉食，掉落随机模组食物（1–2个） |
| **语音** | 喂食和空闲时随机播放良子语音（喂食5秒冷却防刷屏） |

### 🎮 趣味玩法 — 吃万物变强！

在配置中开启并通过良子解锁后，趣味玩法将 Minecraft 变成吃播力量幻想。

**解锁流程：**
1. 模组设置中开启「趣味玩法开关」（ModMenu → Flavor Is Enough Mod）
2. 找到良子，**蹲下 + 右键** 点击他
3. 在弹出的确认框中点击 `[确认上交两组肉类]` — 消耗 128 个肉类
4. 系统提示：趣味模式已开启！

**你可以做的事：**
| 操作 | 效果 |
|------|------|
| 对着空气右键任意物品 | 进入吃东西动画，吃完后获得力量分 |
| 右键点击生物 | 咬一口造成伤害，吃完获得力量分 |
| 正常放置方块 | 准心指着方块时优先放置，不会误吃 |

**稀有度力量分（5档）：**

| 档位 | 分数 | 例子 | 进食时间 |
|------|------|------|----------|
| 普通 | +1 | 泥土、石头、木头、沙子 | 0.8秒 |
| 实用 | +3 | 铁、铜、煤、红石、石英 | 1.0秒 |
| 稀有 | +8 | 钻石、绿宝石、金、黑曜石 | 1.4秒 |
| 特殊 | +12 | 远古残骸、哭泣黑曜石、末地石 | 1.8秒 |
| 神级 | +25 | 龙蛋、下界合金块、信标 | 2.4秒 |

**成长体系（每 50 分 = 1 级）：**
| 增益 | 公式 | 上限 |
|------|------|------|
| 最大生命值 | 每级 +4 | ♾️ 无上限 — 心直接堆出屏幕！ |
| 移动速度 | 每级 +1% | 20 级封顶 |
| 攻击力 | 每级 +1 | 20 级封顶 |
| 护甲 | 每级 +1 | 20 级封顶 |
| 药水效果 | 抗性、再生、防火、夜视、力量、急迫、伤害吸收 | 随级解锁 |

**趣味模式下的便捷体验：**
- 模组 HUD 面板自动隐藏（不遮挡视线）
- 运动提示停止（你已经强到不需要锻炼了）
- 肥胖/负面效果系统禁用（只有纯粹的爽）

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

### 自定义物品（共7件，含刷怪蛋）

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
| 良子刷怪蛋 | 召唤良子 NPC |

### 原版食物整合

通过 Mixin 注入所有原版食物吃下后影响模组数值：苹果、胡萝卜等健康类 → 减胖加健康；熟肉 → 中等增胖；曲奇、蛋糕等高热量 → 大幅增胖扣健康。

### 视觉效果

- **肥胖体型渲染层**：肥胖 ≥40 显示肚子层（四档），≥60 四肢加厚。支持 Default/Slim 双模型。
- **HUD 面板**：半透明深色面板，四项数值 + 进度条 + 当前体态。按 **H** 开关，按 **J** 编辑（拖拽位置 + 滚轮调大小）。

### 肥胖负担效果

肥胖 ≥60 时周期性施加：

- **抗性提升** — 打不动的肉盾
- **最大生命值加成** — 每级 +4 最大生命值（+2心），越胖越能扛
- **饥饿** — 庞大身躯消耗更多能量
- **缓慢** — 拖着这身肉走不快

### 跳跃砸坑

肥胖 ≥40 时从高处落地产生冲击效果。

---

## 技术栈

- Minecraft 1.21.1 | Fabric Loader | Java 21
- Gradle + Fabric Loom（Mojang 映射）
- 依赖：Fabric API、Pehkui、Cardinal Components API

---

## 开源协议

本项目采用 **MIT 协议** 开源，详见 [LICENSE](LICENSE) 文件。
