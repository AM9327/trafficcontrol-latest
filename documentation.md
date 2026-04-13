# Traffic Control Mod — Development Documentation

## Project Overview
Traffic Control mod — railroad crossing signals, traffic lights, signs, street lights for Minecraft.
- **Fork:** AM9327/trafficcontrol-latest, branch: `neoforge-1.21.11`
- **Original:** CSX8600/KillerMapper (Forge 1.12.2)
- **Tech:** Java 21, NeoForge 1.21.11 (stable v21.11.42), Gradle 9.2.1
- **Models:** Blockbench for block/item models
- **Primary Renderer:** `RotatableBlockEntityRenderer.java` (~1200+ lines)

---

## Development Timeline

### 3/27/26 — Project Start
| Commit | Description |
| :--- | :--- |
| `5b6170f` | Started porting Traffic Control mod from Forge 1.12.2 to NeoForge 1.21.11 |
| `d6dfd11` | Remove trafficcontrol-0.4.1 and trafficcontrol_old from tracking |

### 3/28/26 — Traffic Light Placement (Beta 1)
| Commit | Description |
| :--- | :--- |
| `f026787` | Traffic Light Placement (WIP) — mounting on CG poles, HP, CG bases |
| `7aba70d` | Fix TL pole arm rendering: remove double bars, add CG pole arm, fix gap (Beta 1.1) |
| `a7d4384` | HP renders ext arm toward back-to-back TL pairs for proper pole connection |
| `bb634e5` | Update changelog |
| `8a24e6e` | Remove signal_arm item, fix TL hitbox bug when holding frame |
| `9952913` | Remove signal_arm block entirely |

### 3/29/26 — Restore Signal Arm, Bulbs, Blocks
| Commit | Description |
| :--- | :--- |
| `798a5f0` | Restore signal_arm block, fix TL hitbox, HP renders arms for back-to-back TLs |
| `010b9af` | Add back poles (Y 0-16) to ALL vertical TL models, add credits/authors |
| `122b827` | Update changelog date |
| `7689530` | Add all 18 traffic light bulb items (basic, arrows, pedestrian, no-turn, tunnel) |
| `398ecd2` | Add 19 placeholder blocks, BlockSign with cutout/facing/pole connections |
| `7872ae6` | Update changelog |

### 3/31/26 — Sign Fix
| Commit | Description |
| :--- | :--- |
| `3e7b08d` | Fix sign pole showing through cutout texture, use short arms for sign connections |

### 4/1/26 — Sign System
| Commit | Description |
| :--- | :--- |
| `bf9ab44` | Add 16-direction rotation to sign and crossing gate pole, fix sign rendering |
| `b4bf6f9` | Add sign GUI with search/scroll/selection, dynamic sign face renderer, pole connections |

### 4/5/26 — Major Feature Day (Signs, Screwdriver, Horizontal TLs, NeoForge Upgrade)
| Commit | Description |
| :--- | :--- |
| `816702d` | Fix sign pole connections, shift toward CG pole, hitbox, SIGN_REPO init |
| `d85ab28` | Add diagonal rotation hitbox for signs |
| `a785bb4` | **Add sign-to-sign chaining, back-to-back snapping for signs and TLs** |
| `1f5b0cc` | Add screwdriver item for rotating traffic control blocks |
| `b109b28` | Add 9 horizontal traffic light frame variants (3/4/5-bulb, black/yellow/orange) |
| `0457093` | Upgrade NeoForge from 21.11.38-beta to 21.11.42 stable |
| `f564e7a` | Add vertical pole extension for TLs on CG poles, disable config cache |
| `6fa9c6b` | Add orange and yellow traffic light horizontal frame models |
| `f20dc94` | Fix: adjust GUI scale for horizontal traffic light frames |

### 4/6/26 — 3D Icons, Street Signs
| Commit | Description |
| :--- | :--- |
| `ca9069e` | 3D inventory icons, gray bulb backgrounds, street sign rework, creative tab reorder |
| `fbc066a` | Add Street Sign (Illuminated) variant, remove glow from regular street sign |

### 4/7/26 — Hanging Signs, Pole Fixes
| Commit | Description |
| :--- | :--- |
| `2b8c5d7` | Add hanging street signs, HP pole connection through signs, fix pole rendering |
| `186a2d1` | Fix pole rendering for signs and TLs on CG poles |

### 4/9/26 — Street Sign System
| Commit | Description |
| :--- | :--- |
| `6806ac9` | Street sign GUI, 2-line text, retroreflective rendering, pole mounting system |
| `3091d56` | Fix TL back-to-back pole rendering on CG poles |
| `9f0f2a5` | Remove all arm rendering for street signs, fix CG pole shift overlap |
| `709de18` | Add multi-sign stacking (up to 4 plates per block, like 1.12.2) |

### 4/10/26 — Street Sign Polish + TL Pole Rendering Overhaul
| Commit | Description |
| :--- | :--- |
| `dbe79f8` | Street sign stacking, per-plate rotation, GUI overhaul, texture fixes |
| `9aa755b` | Fix street sign edge faces, use 1.12.2 texture, per-plate rotation |
| `82d39de` | Fix hanging signs, edge textures, plate rotation, pole connections |
| `d4215c2` | Fix hanging street sign bracket connection and plate positioning |
| `2ccfebe` | Restore pre-session TL pole rendering logic |
| `f6f01f7` | Fix back-to-back TL shift and CG pole connections |
| `be024c6` | Revert side-by-side TL detection (needs proper design) |
| `a7cb972` | Side-by-side TL detection, back-to-back TL shift, CG pole fixes |
| `2ba7495` | Side-by-side TL connect model, horizontal TL spacing fix |
| `edef3e6` | Remove horizontal TL shifting, keep vertical TL side-by-side connect |
| `3063f3a` | Fix horizontal TL frame pole shifting, update changelog with street sign system |
| `fbac869` | Fix horizontal TL frame back poles, hitbox, and pole connection |
| `247bfc4` | Fix changelog heading for horizontal TL frames section |
| `f04063a` | Restore back-to-back TL/sign detection to original logic |

---

## Pole Rendering System — Technical Guide

### Model Inventory
All arm/bar models use a **2x2 cross-section** at X 7-9, Y 7-9 (or Z 7-9).

| Model File | Model Key | Dimension | Purpose |
| :--- | :--- | :--- | :--- |
| `horizontal_pole.json` | `HORIZONTAL_POLE_MODEL_KEY` | Z 0-25 | Full block+ HP connecting bar |
| `signal_arm_bar.json` | `SIGNAL_ARM_BAR_MODEL_KEY` | Z 0-9 (9px) | Bridge bar for back-to-back pairs |
| `traffic_light_pole_arm.json` | `TRAFFIC_LIGHT_POLE_ARM_MODEL_KEY` | Z 0-7 (7px) | Short arm for HP/CG pole connections |
| `horizontal_bar_connect.json` | `HORIZONTAL_BAR_CONNECT_MODEL_KEY` | Z 0-5 (5px) | Stub for sign-to-sign, TL-to-CG, back-to-back TL |
| `traffic_light_back_pole.json` | `BACK_POLE_MODEL_KEY` | Y 0-16 | Vertical pole extension up/down |
| `traffic_light_connect.json` | `TRAFFIC_LIGHT_CONNECT_MODEL_KEY` | (varies) | Side-by-side TL connect piece |
| `traffic_light_paired.json` | `TRAFFIC_LIGHT_PAIRED_MODEL_KEY` | (varies) | Paired TL across pole |

### Who Renders What

#### Crossing Gate (CG) Poles
- **Renders:** `traffic_light_pole_arm` (7px) toward ALL connected neighbors
- **Source:** `cgPoleArmDirs` list in render state
- **Rule:** CG pole handles ALL arm rendering toward its neighbors. TLs/signs do NOT render arms back toward CG poles.

#### Horizontal Poles (HP)
- **Toward TLs:** `traffic_light_pole_arm` (7px) via `signalArmTrafficLightDirs`
- **Toward other HPs:** `horizontal_pole` (full length)
- **Toward street signs:** extends `horizontal_pole` into street sign block via `streetSignDirs`
- **Toward horizontal TL frames:** extends `horizontal_pole` into horiz TL block via `horizTLDirs`
- **Toward regular signs:** `traffic_light_pole_arm` via `signalArmTrafficLightDirs`

#### Traffic Lights (Vertical)
- **Renders via `horizontalPoleDirs`:** bars toward adjacent TLs and signs ONLY
- **NEVER renders toward CG poles/bases** — CG pole handles that
- **Bar model:** `HORIZONTAL_POLE_MODEL_KEY` when opposite connection exists, `SIGNAL_ARM_BAR_MODEL_KEY` otherwise
- **TL-to-CG pole arm:** `traffic_light_pole_arm_short` (separate model from HP arm, allows independent size tuning)
- **Shift:** 9/16 toward pole when conditions met

#### Traffic Lights (Horizontal Frames)
- **NO shift** — stays centered in block
- **NO bars** — no `horizontalPoleDirs` rendering
- **NO bar-through-frame** — skipped from bar rendering
- **HP extends bar TO them** — HP renders full-length bar into horiz TL block
- **Wider hitbox** — `SHAPE_HORIZ_NS` / `SHAPE_HORIZ_EW` matching model width
- **Detected by:** `state.getBlock().getDescriptionId().contains("horiz")`

#### Signs (BlockSign)
- **On CG pole:** Shifts 9/16, CG pole renders arm
- **On HP:** No shift, HP renders arm
- **Sign-to-sign:** 2px arms (`horizontal_bar_connect`) via `signToSignDirs`
- **Back-to-back:** Both shift 9/16 toward each other + bridge bar

#### Street Signs (BlockStreetSign)
- **On CG pole:** Shifts 7/16 (smaller, plate edge stops at pole face)
- **No arms rendered** from/toward street signs (arm models too tall for 4px plate)
- **HP:** Extends bar into street sign block from HP side
- **Hanging:** No shift, no pole connections
- **Excluded from ALL arm rendering** — `isCurrentStreetSign` check

#### Back-to-Back TLs/Signs
- **Detection:** Adjacent blocks with `ROTATION_16` diff of exactly 8 (180 degrees)
- **Condition:** Only detected when `poleDir == null` (no HP or CG pole found first)
- **Shift:** Both set `mountedOnPole = true`, shift 9/16 toward each other
- **Bridge:** `signal_arm_bar` (9px) rendered from each side, shifted with body
- **Skip:** Back-to-back direction skipped from `horizontalPoleDirs` (bridge replaces bar)

### shiftToPole Logic

```java
boolean shiftToPole = 
    // TLs (not horizontal): on pole, no adjacent TLs blocking, no horizontalPoleDirs
    (isTrafficLight && !isHorizTL && mountedOnPole
        && horizontalBarDirection != null
        && (!hasAdjacentTrafficLight || backToBackTLDir != null)
        && horizontalPoleDirs.isEmpty())
    // Signs: on CG pole only (not HP)
    || (BlockSign && mountedOnPole && !mountedOnHorizontalPole
        && horizontalBarDirection != null)
    // Street signs: on CG pole only, 7/16 amount, not hanging
    || (isStreetSign && !hanging && mountedOnPole && !mountedOnHorizontalPole
        && horizontalBarDirection != null);
```

| Block Type | Shift Amount | Conditions |
| :--- | :--- | :--- |
| Traffic Light (vertical) | 9/16 | On pole, no adjacent TLs (unless back-to-back), horizontalPoleDirs empty |
| Traffic Light (horizontal) | NEVER | Excluded from all shifting |
| Sign (BlockSign) | 9/16 | On CG pole only (not HP) |
| Street Sign | 7/16 | On CG pole only, not hanging |
| Back-to-back TL/Sign | 9/16 | Toward each other (both shift) |

### Detection Priority in extractRenderState()

**For Signs (isSignLike):**
1. HP detection → `mountedOnPole = true`, `mountedOnHorizontalPole = true`
2. CG pole / CG base → `mountedOnPole = true`, `mountedOnHorizontalPole = false`
3. Chained sign (CG pole 2 blocks away through adjacent sign)
4. Back-to-back sign (rotation diff 8, only when `poleDir == null`)

**For Traffic Lights:**
1. HP detection → `mountedOnPole = true`, `mountedOnHorizontalPole = true`
2. CG pole fallback → `mountedOnPole = true`, `mountedOnHorizontalPole = false`
3. Direct back-to-back (only when `poleDir == null`) → `mountedOnPole = true`, `backToBackTLDir` set
4. `pairedAcrossPole` → checks 2 blocks beyond `poleDir` for opposite-facing TL

**Key:** Once `poleDir` is set (HP or CG found), direct back-to-back detection is SKIPPED.

### Key Math
- TL shift (9/16 = 9px) + CG pole arm (7px) = 16px = 1 block (exact connection)
- Back-to-back: each shifts 9/16 = 18px total into 16px gap = 2px overlap (hidden by bridge bar)
- Street sign shift (7/16 = 7px) stops plate edge at CG pole column face
- HP model (Z 0-25) extends well into adjacent block for full connection

---

## Common Pitfalls (Developer Warnings)

### DO NOT:
1. **Add CG poles/bases to `horizontalPoleDirs`** — TLs will render full-length bars that stick out past the CG pole. CG pole renders its own arms.
2. **Render bridge bars from both sides with long models** — doubles visual length (2x 9px = 18px > 16px block)
3. **Shift body AND bridge bar without accounting for total reach** — bar overshoots target
4. **Change `signal_arm_bar.json` dimensions** — breaks ALL uses globally, not just back-to-back
5. **Add entries to `horizontalPoleDirs` carelessly** — `horizontalPoleDirs.isEmpty()` gates `shiftToPole`, so adding entries prevents shifting
6. **Render TL-side arm toward CG pole** — CG pole already renders its arm + TL shifts 9/16 = 16px exact. Adding TL arm causes 23px total (sticks out 7px)
7. **Modify horizontal TL frame shift/bar logic** — they must NEVER shift. HP handles the connection.
8. **Change `traffic_light_pole_arm.json` to resize the CG pole arm** — same model is shared with HP arms toward TLs/signs and CG pole arms. Use `HORIZONTAL_BAR_CONNECT_MODEL_KEY` for the TL-to-CG arm instead.
9. **Remove `shiftToPole` guards (`hasAdjacentTrafficLight`, `horizontalPoleDirs.isEmpty()`)** — these are load-bearing. Removing them causes HP-mounted TLs to overshoot. Need a per-mount-type shift system instead.
10. **Add `!mountedOnHorizontalPole` to `shiftToPole`** — breaks CG pole connections when a TL is adjacent to both HP and CG pole (HP gets mount priority, then `!mountedOnHorizontalPole` prevents CG shift).
11. **Change HP arm toward TLs to 2px (`HORIZONTAL_BAR_CONNECT_MODEL_KEY`)** — removes the visible arm connecting HP to TL frame (red arrow gap, image 41). HP arm must stay at 7px (`TRAFFIC_LIGHT_POLE_ARM_MODEL_KEY`) to bridge the gap since HP-mounted TLs don't shift.
12. **Add `mountedOnHorizontalPole` bypass to `shiftToPole` guards** — causes gaps between side-by-side TLs on HP. HP TLs must use original guards (no shift when adjacent TLs present).
8. **Horizontal TL frame back pole sticking out — UNSOLVED (4/11/26)**
   - **Problem:** Horizontal TL frames have a built-in back pole at Y 3-13, plus the renderer adds a `BACK_POLE_MODEL_KEY` (Y 0-16, X 7-9, Z 7-9) when blocks are above/below. The renderer's Y 0-3 extension sticks out visibly below the frame body. Additionally, horizontal TL models had their back poles at different X positions per frame size (2-bulb: X 8-10, 3-bulb: X 7-9, 4-bulb: X 14-16, 5-bulb: X 11-13), causing XZ mismatch with the renderer's always-centered pole.
   - **Failed approach 1 (4/11/26):** Add `!isHorizTL` to renderer's pole rendering condition at line ~839. Removed the renderer's duplicate pole, but created a 3px gap (Y 0-3) when stacking horizontal TLs with vertical TLs below. The vertical TL's pole stops at its block boundary, and the horizontal TL's built-in pole starts at Y 3. Reverted.
   - **Failed approach 2 (4/11/26):** Extend all 12 horizontal TL model back poles from Y 3-13 to Y 0-16 (full block height), then add `!isHorizTL` to skip renderer's pole. The model would handle its own full-height pole and stacking connections. User reported "that didn't work." Reverted.
   - **Failed approach 3 (4/11/26):** Only skip `extendPoleDown`/`extendPoleUp` for CG poles below/above horizontal TLs in `extractRenderState()` (keep extension for TLs/signs). Didn't fully solve the issue since the renderer's pole still sticks out when other TLs are below. Was combined with approach 2 but both reverted.
   - **Failed approach 4 (4/11/26):** Center all horizontal TL model back poles to X 7-9, Z 7-9 (matching renderer's position). Changed 9 models: 2-bulb from X 8-10, 4-bulb from X 14-16, 5-bulb from X 11-13, all to X 7-9. 3-bulb models were already centered. Idea was that renderer's pole would overlap cleanly with model's pole at same XZ. User reported this also didn't work. Models still have centered poles (not reverted yet).
   - **Root cause analysis:** Two compounding issues: (1) Y range mismatch — renderer extends Y 0-3 below and Y 13-16 above the model's built-in Y 3-13 pole, creating visible stubs. (2) XZ position mismatch — renderer pole always at center X 7-9 but model poles were at different X per frame size, causing renderer pole to appear as separate element. Body model rotates via PoseStack (lines 584-586) but renderer's back pole renders without rotation (lines 841-847).
   - **Failed approach 5 (4/11/26):** Restore all 12 horizontal TL model back poles to original `b109b28` design: X 7-9, Y 0-16 (centered, full height). This matches both the renderer's `BACK_POLE_MODEL_KEY` position and the original working state. User reported this also didn't work. Models still have Y 0-16 poles (not reverted yet).
   - **Git history:** Original commit `b109b28` (4/5/26) had all poles at X 7-9, Y 0-16. Commit `ca9069e` (4/6/26) shortened 3-bulb to Y 3-13. Commit `fbac869` (4/10/26) shortened 2/4/5-bulb to Y 3-13 AND moved them off-center. These changes were labeled "fixes" but introduced the sticking-out problem.
   - **Proven approach 6 (4/11/26) — custom geometry gap fill:** Model poles at Y 3-13 (within frame body), renderer skips `BACK_POLE_MODEL_KEY` for horizontal TLs, instead renders custom geometry boxes for just the gap regions: Y 0-3 (down) when `extendPoleDown`, Y 13/16-1 (up) when `extendPoleUp`. Uses `generic.png` texture via `submitCustomGeometry`. Fixes the back pole sticking through the frame. **REVERTED 4/12/26 — needs to be re-applied.**
   - **Failed approach 7 (4/11-12/26) — remove Back Pole Arm:** The "Back Pole Arm" cross piece element uses non-standard rotation format `{"x": 0, "y": 0, "z": 90}`. Removing it also removes the horizontal cross piece that connects the back pole to the frame body — the frame loses its mounting bar and looks incomplete. The Back Pole Arm is actually the horizontal bar visible through the frame (the "sandwich" element). It CANNOT be simply removed. Needs Blockbench redesign to use proper ±45° rotation format or be replaced with renderer custom geometry. **REVERTED — do NOT remove.**
   - **Proven approach 8 (4/11/26) — skip HP/TL arms toward horizontal TLs:** Three sources of bars poking through horizontal TL frames: (a) HP arm in `signalArmTrafficLightDirs` loop — fix: `if (isTowardHorizTL) continue;`. (b) HP bar extension into horizontal TL blocks — fix: disable the `horizTLDirs` rendering loop. (c) Vertical TL `horizontalPoleDirs` bar toward horizontal TL neighbor — fix: `!isNeighborHorizTL` check. **REVERTED 4/12/26 — needs to be re-applied.**
   - **Failed approach 9 (4/12/26 debate) — extend backing plate Z depth:** Extended Backing Plate Z from `[9, 10]` to `[9, 12]` (1px → 3px deep). The thicker plate swallowed/hid the blinder visor elements (Z 10-12), making frames look like flat rectangles without 3D visors. **Reverted.** The sandwich fix needs a different approach — possibly adding a separate housing element between Z 7-9 (behind plate, not overlapping blinders) or redesigning the model in Blockbench.
   - **All changes reverted to commit f04063a on 4/12/26.** Models and renderer are back to original state.
   - **Build testing (4/12/26) — checking older commits for working state:**
     - `010b9af` (3/29/26) — "Add back poles to TL models" — **MAYBE** (user noted as possible good state, before back-to-back/horiz TL existed)
     - `f564e7a` (4/5/26) — "Add vertical pole extension" — not the right build
     - `ca9069e` (4/6/26) — "3D inventory icons, street sign rework" — not the right build
     - `2b8c5d7` (4/7/26) — "Hanging street signs, pole fixes" — not the right build
     - `186a2d1` (4/7/26) — "Fix pole rendering for signs/TLs on CG poles" — not the right build
     - `b109b28` (4/5/26) — "Add 9 horizontal TL frame variants" — not the right build
   - **Key insight:** The issue may predate horizontal TL frames. Commit `010b9af` (before back-to-back snapping and horizontal TLs) was the closest to looking right.
   - **Working fix 10 (4/12/26) — back-to-back bridge bar removed:** The `SIGNAL_ARM_BAR_MODEL_KEY` (9px) bridge bar between back-to-back TLs was too long — both TLs shift 9/16 toward each other (overlap by 2px), so each rendering a 9px bar created a massive H-shape. Bridge bar rendering removed entirely since back poles already overlap. **APPLIED.**
   - **Working fix 11 (4/12/26) — back-to-back hitbox:** Back-to-back TLs now get full-size shifted hitboxes (`SHAPE_B2B_EAST/WEST/SOUTH/NORTH`) instead of the split `SHAPE_PAIRED_POLE_*` shapes. Hitbox shifts 9px toward the partner, matching the body shift. Detection uses rotation diff of 8 in `getShape()`. **APPLIED.**
   - **Failed approach 12 (4/12/26) — re-apply fixes 6+8 for sandwich:** Re-applied custom geometry gap fill (fix 6), HP arm skip (fix 8a), HP bar extension disable (fix 8b), and vertical TL bar skip toward horiz TL (fix 8c) together with fixes 10+11. User reported "didn't work" — the sandwich/bar-through-frame issue persists. These renderer fixes may need to be combined with model changes (Back Pole Arm fix or Blockbench redesign) to fully resolve. **REVERTED renderer fixes 6+8, kept fixes 10+11 only.**
   - **Working fix 13 (4/12/26) — back-to-back no shift + short bridge:** Back-to-back detection kept (`backToBackTLDir` and `horizontalBarDirection` set) but `mountedOnPole` NOT set, so frames stay centered with gap. Bridge bar changed from `SIGNAL_ARM_BAR_MODEL_KEY` (9px, shifted) to `HORIZONTAL_BAR_CONNECT_MODEL_KEY` (2px, no shift). Each side renders a 2px stub. **APPLIED.**
   - **Working fix 14 (4/12/26) — hasOppositeConnection uses mountedOnHorizontalPole:** Changed `hasOppositeConnection` check from `renderState.mountedOnPole` to `renderState.mountedOnHorizontalPole` (line 907). Prevents back-to-back TLs (which set mountedOnPole but NOT mountedOnHorizontalPole) from triggering the full 16px `HORIZONTAL_POLE_MODEL_KEY` bar through the frame. CG pole and HP connections still use the full bar. **APPLIED.**
   - **Failed fix: HORIZONTAL_BAR_CONNECT_MODEL_KEY as fallback for horizontalPoleDirs (line 909):** Changed 9px fallback to 2px. Fixed back-to-back but broke CG pole arm connections (holes in pole, image 53). The 9px bar is needed for CG pole connections. Reverted — fix must be targeted to back-to-back only, not all horizontalPoleDirs.
   - **Working fix 15 (4/12/26) — TL-to-CG pole arm uses HORIZONTAL_BAR_CONNECT_MODEL_KEY (2px):** Changed TL-to-CG pole arm rendering (line ~951, `mountedOnPole && !mountedOnHorizontalPole`) from `TRAFFIC_LIGHT_POLE_ARM_MODEL_KEY` (7px) to `HORIZONTAL_BAR_CONNECT_MODEL_KEY` (2px). Makes the arm smaller without needing a separate model. **APPLIED.**
     - **Failed attempt: separate model traffic_light_pole_arm_short.json** — created identical 7px model, was unnecessary. Deleted.
     - **Failed attempts: shorten to 4/5/6px** — all created gaps. TL shift (9/16) + short arm doesn't bridge.
     - **Failed attempt: thin to 1x1px at 7px** — no gap but visually mismatched with 2x2 poles.
   - **Working fix 16 (4/12/26) — disable back-to-back TL detection:** Commented out the back-to-back detection block (adjacent TL with rotation diff of 8). The back-to-back code set `mountedOnPole=true` which caused: (a) sandwich look from side, (b) double shift overshoot, (c) long bridge bars. Without it, opposite-facing TLs stay centered and render short stubs via `horizontalPoleDirs`. **APPLIED.**
   - **Working fix 17 (4/12/26) — back-to-back bridge bar uses HORIZONTAL_BAR_CONNECT_MODEL_KEY (2px):** Changed bridge bar model from `SIGNAL_ARM_BAR_MODEL_KEY` (9px) to `HORIZONTAL_BAR_CONNECT_MODEL_KEY` (2px), removed the body shift from the bridge bar rendering. Each TL renders a 2px stub toward its partner, no shift. **APPLIED.**
   - **Working fix 18 (4/12/26) — re-enable back-to-back detection WITHOUT mountedOnPole:** Back-to-back detection re-enabled but only sets `backToBackTLDir` and `horizontalBarDirection` — does NOT set `mountedOnPole`. TLs stay centered (no shift/sandwich), just detect each other for bridge bar rendering. **APPLIED.**
   - **Extensive failed approaches (4/12/26) — shiftToPole modifications:**
     - Removing `hasAdjacentTrafficLight` guard: CG pole connections improved but HP connections broke (TLs flew off HP)
     - Removing `horizontalPoleDirs.isEmpty()` guard: same issue
     - Adding `!mountedOnHorizontalPole`: broke CG pole connections when TLs are also adjacent to HP
     - Adding signal arm shift (`signalArmBarDirection`): broke HP connections
     - HP arms changed to 2px/full-length: didn't fix underlying shift issue
     - Adding HP to `horizontalPoleDirs`: caused bars to poke through
     - **Conclusion:** `shiftToPole` guards are load-bearing — they prevent HP-mounted TLs from overshooting. Do NOT remove them without a per-mount-type shift system.
   - **Working fix 19 (4/12/26) — back-to-back uses own back direction, not partner direction:** Back-to-back detection calculates the TL's own back direction from rotation (snapped to cardinal) and only checks that direction for a partner. `backToBackTLDir` = back direction, `horizontalBarDirection` = back direction. TLs shift toward their own backs so backing plates face each other. **APPLIED.**
   - **Working fix 20 (4/12/26) — separate back-to-back shift with tiebreaker:** Back-to-back shift is its own code block, separate from `shiftToPole`. Only the SECOND TL shifts (tiebreaker: `backDir.getStepX() + backDir.getStepZ() > 0`). First TL stays at block center. Second TL shifts `8.0f / 16.0f` toward its back (toward the first). **APPLIED.**
     - **Key insight:** Only one TL in the pair shifts. The first stays on the CG pole column, the second shifts toward it. Tiebreaker ensures consistent behavior.
   - **Working fix 21 (4/12/26) — stub model extended to 5px:** Changed `traffic_light_horizontal_bar_connect.json` from Z 0-2 (2px) to Z 0-5 (5px). Longer stub bridges the gap between back-to-back frames.
     - Failed: 2px too short, 3px too short, 4px too short. 5px connects. **APPLIED.**
   - **Working fix 22 (4/12/26) — stub offset with tiebreaker (5/16):** Each TL renders a stub shifted 5/16 toward the middle. First TL's stub shifts toward partner (`b2bDirBridge`), second TL's stub shifts toward own center (`b2bDirBridge.getOpposite()`). Both stubs meet in the middle. **APPLIED.**
   - **Working fix 23 (4/12/26) — CG base renders arms like CG pole:** Added `BlockCrossingGateBase` to CG pole arm rendering condition and neighbor detection. CG base now detects adjacent TLs/signs and renders `HORIZONTAL_POLE_MODEL_KEY` (full-length) arms through TL frames. Also added CG base to `isVerticalPoleBlock()`. **APPLIED.**
   - **Working fix 24 (4/12/26) — direct back-to-back paired detection in blockstate:** Added direct adjacent TL check (rotation diff 8) to `BlockTrafficLight.updateHorizontalBar()` so `paired: true` shows for direct back-to-back, not just across-pole. Also expanded across-pole check to scan ALL pole directions (not just first). **APPLIED.**
   - **Working fix 25 (4/12/26) — `shiftToPole` bypasses guards for back-to-back only:** Added `|| renderState.backToBackTLDir != null` to both `hasAdjacentTrafficLight` and `horizontalPoleDirs.isEmpty()` guards. HP bypass (`mountedOnHorizontalPole`) was tried but caused gaps between side-by-side TLs on HP — **REVERTED**. HP TLs use original guards (no shift when adjacent TLs present). **APPLIED.**
   - **Working fix 26 (4/12/26) — CG pole/base arms use full-length model:** Changed CG pole arm rendering from `TRAFFIC_LIGHT_POLE_ARM_MODEL_KEY` (7px) to `HORIZONTAL_POLE_MODEL_KEY` (full-length 25px). Arms pass through TL frames regardless of shift state. **APPLIED.**
   - **Recommended next steps:**
     - Re-apply approaches 6+7+8 for horizontal TL frame fixes

### When Something Sticks Out:
1. Check which block is rendering the arm (use F3 debug to identify the block)
2. Check if `horizontalPoleDirs` contains unexpected directions
3. Check if `shiftToPole` is true AND a bar is also rendering in the same direction
4. Check if both sides of a connection are each rendering their own arm
5. Math check: shift amount + arm length should equal exactly 16px (1 block) for clean connection

---

## Current State (4/10/26)

### Working:
- Street sign system — full 1.12.2 port (stacking, per-plate rotation/color/text, GUI, hanging, pole mounting)
- Horizontal TL frames — no shift on HP, wider hitbox, proper back pole models (centered per frame width)
- Side-by-side vertical TLs — connect model between flush neighbors
- Back-to-back TLs/signs — shift + bridge bar (a785bb4 logic)
- CG pole arms — short arms toward all connected neighbors
- HP connections — arms toward TLs, signs, street signs, horizontal TL frames
- Screwdriver rotation
- Sign GUI with search/scroll/selection
- 3D inventory icons for all TL frames
- 2-bulb horizontal TL frames (all 3 colors)

### Known Issues Being Worked On:
1. **TL-to-CG pole arm sticking out** — TL `horizontalPoleDirs` bars render full-length toward secondary CG poles. CG pole renders its own arm, but TL also renders, causing overshoot.
2. **Back-to-back bridge bar overshoot** — signal_arm_bar (9px) shifted by 9/16 from each side may overshoot in multi-pole configurations.
3. **Regular BlockSign face texture on CG poles** — pre-existing bug, sign face quad doesn't render when mounted on CG pole.

### TODO:
- Fix remaining arm sticking out issues
- Traffic light signal logic (relay box, sensor, sequencer)
- Crossing gate animation
- Street lights
- Railroad crossing signals
- Networking packets (PayloadRegistrar)
- Recipe registration

---

## Key Design Decisions

| Date | Decision | Why |
| :--- | :--- | :--- |
| 3/28 | CG pole renders its own arms toward neighbors | Prevents double-rendering from both sides |
| 3/29 | Back poles (Y 0-16) added to all TL models | Visual structure behind TL frame |
| 4/5 | Back-to-back snapping with 9/16 shift | Simulates shared mounting pole between opposite-facing TLs |
| 4/5 | NeoForge upgrade to stable | Long-term compatibility |
| 4/9 | Street signs: no arms, 7/16 shift | Arm models too tall for 4px plate, smaller shift stops at pole face |
| 4/9 | TLs don't render toward CG poles | CG pole arm + TL shift = exact 16px, TL arm would overshoot |
| 4/10 | Horizontal TL frames: no shift | Wide frames overlap when shifted, HP extends bar to them instead |
| 4/10 | Separate horizTLDirs tracking | HP needs to know which neighbors are horizontal TLs for full-length bar |
| 4/10 | horizontalPoleDirs skips CG poles | Prevents TLs rendering full-length bars past CG poles |
| 4/12 | TL-to-CG arm uses `HORIZONTAL_BAR_CONNECT_MODEL_KEY` (2px) | Shorter arm, no separate model needed. CG pole arm (7px) + TL shift (9px) = 16px exact connection |
| 4/12 | Back-to-back uses own back direction | Detection calculates TL's back from rotation, only checks that direction for partner. Shift goes toward own back, not partner position |
| 4/12 | Back-to-back separate shift block (0/16) | Independent from `shiftToPole`. No shift needed — back pole (Z 7-9) already aligns with CG pole column. Bridge bar renders from unshifted position |
| 4/12 | CG base renders arms like CG pole | `BlockCrossingGateBase` added to arm rendering + `isVerticalPoleBlock()`. Full-length arms through TL frames |
| 4/12 | `shiftToPole` guards bypass for back-to-back ONLY | `backToBackTLDir` bypasses guards. HP bypass tried but reverted — causes gaps between side-by-side TLs on HP |
| 4/12 | `shiftToPole` guards are load-bearing | `hasAdjacentTrafficLight` and `horizontalPoleDirs.isEmpty()` prevent HP-mounted TLs from overshooting. Must NOT be removed |
| 4/12 | Back-to-back tiebreaker: only second TL shifts | `backDir.getStepX() + backDir.getStepZ() > 0` determines which TL is "second". First stays at block center, second shifts 8/16. Stubs use matching tiebreaker for offset direction |
| 4/12 | Stub model 5px, offset 2/16 with tiebreaker | `horizontal_bar_connect.json` at Z 0-5. First TL stub toward partner, second toward own center. Meet in middle |
| 4/12 | CG pole/base arms full-length | `HORIZONTAL_POLE_MODEL_KEY` passes through TL frames. CG base also renders arms |

---

## Render State Fields Reference

```java
// Core
float rotationDegrees;                    // free rotation angle
Direction horizontalBarDirection;          // direction toward mounting pole/connection

// Mounting
boolean mountedOnPole;                    // any pole adjacent (CG, HP, or back-to-back partner)
boolean mountedOnHorizontalPole;          // HP adjacent (vs CG pole)
boolean pairedAcrossPole;                 // opposite-facing TL beyond pole
boolean onCrossingGateBase;              // mounted on CG base column

// Adjacent blocks
boolean hasAdjacentTrafficLight;          // any adjacent TL
boolean adjacentTrafficLightConnection;   // adjacent TL with different rotation
Direction signalArmBarDirection;          // direction to signal_arm block
boolean signalArmConnectsToLight;        // TL on other side of signal_arm

// Side-by-side
boolean hasSideBySideNeighbor;            // flush side-by-side TL connection
Direction sideBySidePoleDirection;        // pole between side-by-side lights

// Direction lists
List<Direction> horizontalPoleDirs;       // TL: dirs toward adjacent HPs/TLs for bar rendering
List<Direction> signalArmTrafficLightDirs; // HP/signal_arm: dirs toward TLs
List<Direction> nonCardinalTLDirs;        // HP: dirs toward non-cardinal TLs
List<Direction> cardinalTLDirs;           // HP: dirs toward cardinal TLs
List<Direction> signDirs;                 // HP: dirs toward signs
List<Direction> horizTLDirs;             // HP: dirs toward horizontal TL frames
List<Direction> signToSignDirs;          // Sign: dirs toward other signs
List<Direction> streetSignDirs;          // HP: dirs toward street signs
List<Direction> cgPoleArmDirs;           // CG pole: dirs with connection arms

// Back-to-back
Direction backToBackSignDir;             // Sign: dir toward back-to-back paired sign
Direction backToBackTLDir;               // TL: dir toward back-to-back paired TL

// Vertical pole extension
boolean extendPoleUp;                    // render pole extension upward
boolean extendPoleDown;                  // render pole extension downward

// Sign textures
Identifier signFrontTexture;             // sign face texture (front)
Identifier signBackTexture;              // sign face texture (back)

// Street sign plates
int streetSignCount;                     // number of stacked plates (0-4)
String[] streetSignTexts;                // line 1 text per plate
String[] streetSignTexts2;               // line 2 text per plate
int[] streetSignFillColors;              // fill color per plate
float[] streetSignRotations;             // rotation degrees per plate
int streetSignTextColor;                 // shared text color
boolean streetSignGlowing;              // glowing text flag
boolean hanging;                         // hanging from block above
```
