# Resonance Apparatus Testing Guide

This checklist covers the spatial Resonance Apparatus system.

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

- Resonance Staff
- Resonant Copper
- Stabilized Iron Plate
- Charged Magitek Core
- Focusing Lens

Also confirm the existing material tabs still show their normal items:

- Magitek tab: Magitek ingots, nuggets, raw Magitek, ores, block
- Voxite tab: Voxite ingots, nuggets, raw Voxite, ores, block
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

## 4. Apparatus Pattern Checks

The Resonance Workbench now validates an exact top-down 9x9 apparatus circle on the same Y level as the bench.

Use this pattern:

```text
XXXFCFXXX
XXSXXXSXX
XSXXXXXSX
FXXXXXXXF
CXXXBXXXC
FXXXXXXXF
XSXXXXXSX
XXSXXXSXX
XXXFCFXXX
```

Legend:

- `B` = Resonance Workbench
- `C` = Magitek Core
- `S` = Voxite Stabilizer
- `F` = Amethyst Focus
- `X` = air, floor, or anything not part of the apparatus

Right-click the workbench with an empty hand.

Expected result for a complete pattern:

- Status says `Pattern complete: true`.
- Missing sockets count is `0`.

Expected result for an incomplete pattern:

- Status says `Pattern complete: false`.
- Missing socket count is greater than `0`.
- Activating with the staff reports the first missing socket offset from the bench.

## 5. Socket Item Checks

Magitek Cores, Voxite Stabilizers, and Amethyst Foci are now item sockets.

Test placing an item:

1. Hold an ingredient item.
2. Right-click a core, stabilizer, or focus.

Expected result:

- The block stores one item.
- The held stack shrinks by 1, unless in Creative.
- The stored item appears as a floating item model above the socket.
- The floating item slowly bobs and rotates.
- Colored particles appear above the socket.
- Occasional item particles appear above the socket.

Test removing an item:

1. Empty your hand.
2. Right-click the socket.

Expected result:

- The stored item returns to inventory, or drops if inventory is full.
- The socket becomes empty.
- The floating item model and socket particles stop rendering shortly after removal.

Test occupied socket behavior:

1. Place one item on a socket.
2. Try placing a second item on the same socket.

Expected result:

- The second item is not consumed.
- The socket reports that it is already occupied.

Test save/load behavior:

1. Place an item on any socket.
2. Exit the world.
3. Reopen the world.

Expected result:

- The socket still contains the item.
- The same floating item model renders above the socket after the world reloads.

## 6. Staff Activation Recipe Checks

These are not normal crafting-table recipes. Place items into sockets, then right-click the Resonance Workbench with the Resonance Staff.

### Resonant Copper

Required pattern:

- Complete 9x9 apparatus circle

Socketed ingredient:

- Copper Ingot on any Amethyst Focus

Activation:

1. Place Copper Ingot on an Amethyst Focus.
2. Right-click the Resonance Workbench with the Resonance Staff.

Expected result:

- Consumes the socketed Copper Ingot.
- Gives 1 Resonant Copper.
- Damages the staff by 1, unless in Creative.
- Plays resonance sounds.

### Stabilized Iron Plate

Required pattern:

- Complete 9x9 apparatus circle

Socketed ingredient:

- Iron Ingot on any Voxite Stabilizer

Activation:

1. Place Iron Ingot on a Voxite Stabilizer.
2. Right-click the Resonance Workbench with the Resonance Staff.

Expected result:

- Consumes the socketed Iron Ingot.
- Gives 1 Stabilized Iron Plate.
- Damages the staff by 1, unless in Creative.

### Charged Magitek Core

Required pattern:

- Complete 9x9 apparatus circle

Socketed ingredients:

- Magitek Ingot on any Magitek Core
- Amethyst Shard on any Amethyst Focus

Activation:

1. Place Magitek Ingot on a Magitek Core.
2. Place Amethyst Shard on an Amethyst Focus.
3. Right-click the Resonance Workbench with the Resonance Staff.

Expected result:

- Consumes the socketed Magitek Ingot.
- Consumes the socketed Amethyst Shard.
- Gives 1 Charged Magitek Core.
- Damages the staff by 1, unless in Creative.

## 7. Mining And Drops

Test each apparatus block in Survival mode with a valid pickaxe:

- Resonance Workbench
- Magitek Core
- Voxite Stabilizer
- Amethyst Focus

Expected result:

- Blocks mine normally with a pickaxe.
- Blocks drop themselves.
- If a socket is holding an item, the held item drops when the block breaks.

Also test incorrect tools:

- Breaking Magitek Core or Voxite Stabilizer without a suitable pickaxe should not behave like normal hand-mined blocks.

## 8. Visual Checks

Place the full apparatus circle.

Expected visual feel:

- Resonance Workbench has an animated copper/stone glyph top.
- Magitek Core has volatile pink-magitek pulsing frames.
- Voxite Stabilizer has a cooler blue stabilizing pulse.
- Amethyst Focus has a violet lens glint.
- Resonance Staff looks like a staff, not an ore test rod.
- Socketed ingredients float above the socket with a subtle bob and spin.
- Socketed ingredients create colored dust and item particles that match the ingredient family.

Check item icons:

- Resonant Copper should retain copper identity with resonance highlights.
- Stabilized Iron Plate should read as iron plus voxite stabilization.
- Charged Magitek Core should read as a compact magitek charge.
- Focusing Lens should read as amethyst/copper optical hardware.

## 9. Regression Checks

Confirm existing systems still work:

- Voxite and Magitek ores and storage blocks are still mineable with iron-tier or better tools.
- Crystal buds and clusters still mine like amethyst.
- Shard item textures still have transparent backgrounds.
- Existing ingots, nuggets, raw materials, and crystal shards still render in inventory.

## 10. Known First-Pass Limits

These are expected for this version:

- No GUI yet.
- No custom resonance recipe JSON yet.
- No advanced instability effects yet.
- Floating socket items are visual only; the socket still stores a single item stack in its block entity.
- The legacy `dowsing_rod` item id is kept, but its display name and behavior are now Resonance Staff.
