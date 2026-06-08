# Resonance Apparatus Testing Guide

This checklist covers the first gameplay pass for the spatial Resonance Apparatus system.

## 1. Preflight

Run these before testing in-game:

```powershell
$env:JAVA_HOME = "C:\Users\Darkw\.jdks\openjdk-26.0.1"
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
.\gradlew.bat build
```

Expected result:

- Build finishes with `BUILD SUCCESSFUL`.
- No JSON, model, registry, or resource errors appear.

Then launch the client:

```powershell
$env:JAVA_HOME = "C:\Users\Darkw\.jdks\openjdk-26.0.1"
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
.\gradlew.bat runClient
```

Expected result:

- Minecraft opens.
- FearsMod loads without crashing.
- Dev auth or Realms warnings are acceptable.

## 2. Creative Inventory Checks

Open the creative inventory and find the `Resonance Apparatus` tab.

Confirm these blocks exist:

- Resonance Workbench
- Magitek Core
- Voxite Stabilizer
- Amethyst Focus

Confirm these items exist:

- Resonant Copper
- Stabilized Iron Plate
- Charged Magitek Core
- Focusing Lens

Also confirm the existing material tabs still show their normal items:

- Magitek tab: Magitek ingots, nuggets, raw Magitek, ores, block
- Voxite tab: Voxite ingots, nuggets, raw Voxite, ores, block, Dowsing Rod
- Crystals tab: crystal shards, crystal blocks, buds, clusters

## 3. Crafting Recipe Checks

Use a crafting table to verify each apparatus recipe appears and crafts correctly.

### Focusing Lens

Recipe:

```text
 G
CAC
 G
```

Ingredients:

- `G` = Glass
- `C` = Copper Ingot
- `A` = Amethyst Shard

Expected result:

- Produces Focusing Lens.

### Resonance Workbench

Recipe:

```text
VAV
CWC
CCC
```

Ingredients:

- `V` = Voxite Ingot
- `A` = Amethyst Shard
- `C` = Copper Ingot
- `W` = Crafting Table

Expected result:

- Produces Resonance Workbench.

### Magitek Core

Recipe:

```text
CAC
RMR
CAC
```

Ingredients:

- `C` = Copper Ingot
- `A` = Amethyst Shard
- `R` = Redstone
- `M` = Magitek Ingot

Expected result:

- Produces Magitek Core.

### Voxite Stabilizer

Recipe:

```text
 V
VAV
 V
```

Ingredients:

- `V` = Voxite Ingot
- `A` = Amethyst Shard or Agate Shard

Expected result:

- Produces Voxite Stabilizer from either recipe variant.

### Amethyst Focus

Recipe:

```text
 A
CLC
 V
```

Ingredients:

- `A` = Amethyst Shard
- `C` = Copper Ingot
- `L` = Focusing Lens
- `V` = Voxite Ingot

Expected result:

- Produces Amethyst Focus.

## 4. Apparatus Layout Checks

Place a Resonance Workbench in the world.

The workbench scans a 3-block radius around itself. Nearby apparatus blocks may be above, below, beside, or diagonal as long as they are within that radius.

### Invalid Setup

Place only:

- Resonance Workbench

Right-click the workbench with a Copper Ingot.

Expected result:

- No item is crafted.
- The workbench reports that it needs a Magitek Core and an Amethyst Focus.

### Basic Valid Setup

Place within 3 blocks of the Resonance Workbench:

- Magitek Core
- Amethyst Focus

Do not place a Voxite Stabilizer yet.

Right-click the workbench with an empty hand.

Expected result:

- Status message shows Magitek Core = true.
- Status message shows Amethyst Focus = true.
- Status message shows Stable = false.

### Stable Setup

Add within 3 blocks of the Resonance Workbench:

- Voxite Stabilizer

Right-click the workbench with an empty hand.

Expected result:

- Status message shows Stable = true.

## 5. Resonance Recipe Checks

These are not normal crafting-table recipes. They are right-click interactions on the Resonance Workbench.

### Resonant Copper

Required setup:

- Resonance Workbench
- Nearby Magitek Core
- Nearby Amethyst Focus

Test:

1. Hold a Copper Ingot.
2. Right-click the Resonance Workbench.

Expected result:

- Consumes 1 Copper Ingot, unless in Creative.
- Gives 1 Resonant Copper.
- Plays an amethyst resonance sound.

### Stabilized Iron Plate

Required setup:

- Resonance Workbench
- Nearby Magitek Core
- Nearby Amethyst Focus
- Nearby Voxite Stabilizer

Test without stabilizer:

1. Remove or move the Voxite Stabilizer farther than 3 blocks away.
2. Hold an Iron Ingot.
3. Right-click the Resonance Workbench.

Expected result:

- No item is crafted.
- The workbench reports that the iron pattern requires a Voxite Stabilizer.

Test with stabilizer:

1. Place the Voxite Stabilizer back within 3 blocks.
2. Hold an Iron Ingot.
3. Right-click the Resonance Workbench.

Expected result:

- Consumes 1 Iron Ingot, unless in Creative.
- Gives 1 Stabilized Iron Plate.

### Charged Magitek Core

Required setup:

- Resonance Workbench
- Nearby Magitek Core
- Nearby Amethyst Focus

Required inventory:

- Hold 1 Magitek Ingot.
- Have at least 1 Amethyst Shard anywhere in player inventory.

Test without Amethyst Shard:

1. Remove Amethyst Shards from inventory.
2. Hold a Magitek Ingot.
3. Right-click the Resonance Workbench.

Expected result:

- No item is crafted.
- The workbench reports that a Magitek charge needs an Amethyst Shard.

Test with Amethyst Shard:

1. Add an Amethyst Shard to inventory.
2. Hold a Magitek Ingot.
3. Right-click the Resonance Workbench.

Expected result:

- Consumes 1 Magitek Ingot, unless in Creative.
- Consumes 1 Amethyst Shard, unless in Creative.
- Gives 1 Charged Magitek Core.

## 6. Mining And Drops

Test each apparatus block in Survival mode with a valid pickaxe:

- Resonance Workbench
- Magitek Core
- Voxite Stabilizer
- Amethyst Focus

Expected result:

- Blocks mine normally with a pickaxe.
- Blocks drop themselves.

Also test incorrect tools:

- Breaking Magitek Core or Voxite Stabilizer without a suitable pickaxe should not behave like normal hand-mined blocks.

## 7. Visual Checks

Place all four apparatus blocks together.

Expected visual feel:

- Resonance Workbench reads as a copper-and-stone physical table with a tuned center.
- Magitek Core reads as volatile pink resonance/power.
- Voxite Stabilizer reads as cool blue stabilization geometry.
- Amethyst Focus reads as a copper-framed violet focusing component.

Check item icons:

- Resonant Copper should retain copper identity with resonance highlights.
- Stabilized Iron Plate should read as iron plus voxite stabilization.
- Charged Magitek Core should read as a compact magitek charge.
- Focusing Lens should read as amethyst/copper optical hardware.

## 8. Regression Checks

Confirm existing systems still work:

- Voxite and Magitek ores are still mineable with diamond-tier or better tools.
- Crystal buds and clusters still mine like amethyst.
- Shard item textures still have transparent backgrounds.
- Existing ingots, nuggets, raw materials, and crystal shards still render in inventory.

## 9. Known First-Pass Limits

These are expected for this version:

- No GUI yet.
- No block entity inventory yet.
- No instability effects yet.
- Resonance transformations are hardcoded in `ResonanceWorkbenchBlock`.
- Custom resonance recipe JSON is planned for a later pass.
- Dowsing Rod is not part of the apparatus system.
