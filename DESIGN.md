---
name: PataGOnIA
description: Adult field-tool UI for offline biodiversity sightings in Chilean Patagonia.
colors:
  forest-night: "#07140E"
  forest-surface: "#0F211A"
  forest-surface-variant: "#163024"
  forest-container: "#1B4332"
  forest-primary: "#2E6A4F"
  forest-bright: "#5FBF8A"
  forest-mint: "#C9EBD4"
  mint-text: "#E6F3EA"
  mint-variant: "#A8C7B6"
  mint-chip-text: "#0C3320"
  ink: "#14241B"
  ink-variant: "#3E5A4A"
  outline-light: "#5C7565"
  outline-variant-light: "#B4C4B8"
  outline-dark: "#4A8268"
  background-light: "#F4F8F4"
  surface-variant-light: "#E3EAE3"
  white: "#FFFFFF"
  earth: "#5D4037"
  earth-container: "#EADBD0"
  earth-dark-text: "#2E1B12"
  earth-light: "#B89A8E"
  earth-light-text: "#1A0E08"
  teal-info: "#2D5F66"
  teal-bright: "#7FD3DE"
  teal-dark-text: "#06262B"
  error: "#B3261E"
  error-container: "#F9D9D6"
  error-container-text: "#410005"
  error-dark: "#FFB4AB"
  error-dark-container: "#93000A"
  success: "#217A43"
  success-dark: "#7BD99A"
  success-dark-text: "#00390E"
  warning: "#8A5D00"
  warning-dark: "#FFC069"
  warning-dark-text: "#3A2400"
typography:
  display:
    fontFamily: "Fira Sans"
    fontSize: "45sp"
    fontWeight: 700
    lineHeight: "52sp"
    letterSpacing: "-0.5sp"
  headline:
    fontFamily: "Fira Sans"
    fontSize: "30sp"
    fontWeight: 700
    lineHeight: "38sp"
    letterSpacing: "0sp"
  title:
    fontFamily: "Fira Sans"
    fontSize: "20sp"
    fontWeight: 600
    lineHeight: "26sp"
    letterSpacing: "0.1sp"
  body:
    fontFamily: "Fira Sans"
    fontSize: "16sp"
    fontWeight: 400
    lineHeight: "24sp"
    letterSpacing: "0.25sp"
  label:
    fontFamily: "Fira Sans"
    fontSize: "14sp"
    fontWeight: 500
    lineHeight: "20sp"
    letterSpacing: "0.1sp"
rounded:
  xs: "4dp"
  sm: "8dp"
  md: "12dp"
  lg: "16dp"
  xl: "28dp"
spacing:
  xs: "4dp"
  sm: "8dp"
  md: "12dp"
  lg: "16dp"
  xl: "24dp"
  screen: "32dp"
components:
  button-primary:
    backgroundColor: "{colors.forest-primary}"
    textColor: "{colors.white}"
    typography: "{typography.label}"
    rounded: "{rounded.md}"
    height: "48dp"
    padding: "0 16dp"
  button-danger:
    backgroundColor: "{colors.error}"
    textColor: "{colors.white}"
    typography: "{typography.label}"
    rounded: "{rounded.md}"
    height: "48dp"
    padding: "0 16dp"
  button-secondary:
    backgroundColor: "{colors.surface-variant-light}"
    textColor: "{colors.ink-variant}"
    typography: "{typography.label}"
    rounded: "{rounded.md}"
    height: "48dp"
    padding: "0 16dp"
  card-surface:
    backgroundColor: "{colors.white}"
    textColor: "{colors.ink}"
    rounded: "{rounded.lg}"
    padding: "16dp"
  input-outlined:
    backgroundColor: "transparent"
    textColor: "{colors.ink}"
    typography: "{typography.body}"
    rounded: "{rounded.md}"
    height: "56dp"
  chip-filter:
    backgroundColor: "{colors.forest-mint}"
    textColor: "{colors.mint-chip-text}"
    typography: "{typography.label}"
    rounded: "{rounded.xl}"
    height: "32dp"
---

# Design System: PataGOnIA

## 1. Overview

**Creative North Star: "Bitacora Viva de Campo"**

PataGOnIA is a field notebook that happens to be a mobile app: serious enough to be trusted for species identification and citizen-science records, alive enough to make learning biodiversity feel inviting. The interface must feel adult, reliable, and educational without becoming dry. It uses a restrained forest system, crisp contrast, real typography, and Material-native controls so the user can keep moving on trail.

The dominant physical scene is a hiker in Chilean Patagonia, outdoors in harsh light, with one hand available and unreliable connectivity. That scene forces the design toward high contrast, large-enough type, unmistakable states, dark camera chrome, and sparse use of accent color. Warmth comes from language, species imagery, and the field-guide mood, not from beige surfaces or playful decoration.

This system explicitly rejects the PRODUCT.md anti-references: **App de caricatura/juguete**, **Densidad de panel admin**, and **AI beige/cream**. It should never look like a toy monster-catcher, a dense admin console, or a generic cream AI concept.

**Key Characteristics:**
- Restrained forest-green palette with one primary accent used for action, selection, and status.
- High-contrast ink on green-tinted neutrals for outdoor readability.
- Forced dark capture flow for camera, review, and first-run loading.
- Fira Sans throughout: humanist, readable, offline bundled, never decorative.
- Tactile but sober controls: clear touch targets, modest radii, no cartoon surfaces.

## 2. Colors

The palette is a restrained Patagonia field palette: forest carries trust and action; green-tinted neutrals carry daylight readability; earth and teal support trail/context information without competing with primary actions.

### Primary
- **Forest Primary** (`forest-primary`): Primary action and selected-state color on light surfaces. Use for save, download, focus, map controls, and core progress indicators.
- **Bright Forest** (`forest-bright`): Primary action on forced-dark capture surfaces. It is bright enough to read against near-black forest chrome.
- **Forest Mint** (`forest-mint`): Primary container for selected chips, thumbnails, and non-danger emphasis.

### Secondary
- **Trail Earth** (`earth`): Secondary role for trail, ground, and offline-map context. Use sparingly; it should feel like terrain, not decoration.
- **Earth Container** (`earth-container`): Light secondary container for low-emphasis map-management cards and contextual blocks.

### Tertiary
- **Glacial Teal Info** (`teal-info`): Informational role, aliased to extended info in the theme. Use for factual secondary information, not primary CTAs.

### Neutral
- **Field Ink** (`ink`): Primary text on light browse/map surfaces.
- **Field Ink Variant** (`ink-variant`): Secondary text on light surfaces; dark enough to pass outdoor readability checks.
- **Daylight Background** (`background-light`): Main light surface for maps, management, and browse contexts. It is a faint green-tinted neutral, not cream.
- **Light Surface Variant** (`surface-variant-light`): Secondary panels, placeholder map backgrounds, progress tracks, and disabled containers.
- **Forest Night** (`forest-night`): Forced-dark camera background and dark-mode app background.
- **Forest Surface** (`forest-surface`): Dark surface layer for capture UI.
- **Mint Text** (`mint-text`): Primary text on dark forest surfaces.
- **Outline Light / Outline Dark** (`outline-light`, `outline-dark`): Borders and component outlines that must remain visible, not ornamental.

### Named Rules

**The One Forest Accent Rule.** Primary green is for action, selected state, focus, and progress. Do not use it as decoration across every card.

**The No Beige Field Rule.** Warmth is carried by copy, species imagery, and earth secondary roles. Do not use cream, sand, parchment, or paper neutrals as the body background.

**The Outdoor Contrast Rule.** If secondary text starts to feel elegant but pale, it is wrong. Push it toward `ink-variant` or `mint-variant` until it reads at a glance outdoors.

## 3. Typography

**Display Font:** Fira Sans, bundled in `app/src/main/res/font`.
**Body Font:** Fira Sans, bundled in `app/src/main/res/font`.
**Label/Mono Font:** Fira Sans; no separate mono or decorative family exists.

**Character:** Fira Sans is humanist and practical: open counters, high readability, and a field-guide tone that is warm without becoming cute. The system uses one family with weight, color, and spacing for hierarchy.

### Hierarchy
- **Display** (700, 45sp, 52sp): Rare large moments only. Use for brand-scale screens, not routine labels.
- **Headline** (700/600, 30sp to 23sp, 38sp to 30sp): Screen titles and major states such as review or permission screens.
- **Title** (600, 20sp to 15sp, 26sp to 22sp): Card titles, species names, active downloads, section labels.
- **Body** (400, 16sp to 12.5sp, 24sp to 18sp): Educational copy, metadata, field descriptions, validation text.
- **Label** (500, 14sp to 11sp, 20sp to 16sp): Buttons, compact coordinates, filters, and small state labels.

### Named Rules

**The One-Family Rule.** Do not introduce a second typeface for flavor. Product trust comes from consistent Fira Sans hierarchy.

**The Fixed Scale Rule.** Use the pinned `sp` scale from `PatagoniaTypography`; do not add fluid or oversized display typography to app UI.

**The Trail-Light Floor.** Body copy that matters in the field starts at `bodyLarge` when space allows. Do not make primary instructions smaller than they need to be for outdoor reading.

## 4. Elevation

PataGOnIA is tonal by default. Depth comes from surface color, borders, spacing, and state. Shadows are reserved for true overlays, especially map capture tooltips that float above the map and need separation from variable terrain content.

### Shadow Vocabulary
- **Tooltip Lift** (`CardDefaults.cardElevation(defaultElevation = 8.dp)`): Use only for transient overlays such as selected capture details on the map.

### Named Rules

**The Tonal-First Rule.** Cards, forms, and download panels are separated by surface tokens and spacing before shadows. If a static card needs a shadow to be understood, the surface hierarchy is wrong.

**The Overlay Earns Lift Rule.** Elevation belongs to something that truly floats above context: tooltip, popover, modal-equivalent. Lists and ordinary cards stay grounded.

## 5. Components

Components should feel **tactile and sober**: clear enough for one-handed field use, restrained enough to remain credible as an educational and scientific record tool.

### Buttons
- **Shape:** Modest rounded rectangle (`12dp` medium). Avoid oversized pills except where Material components require chip behavior.
- **Primary:** Forest primary container with white text (`forest-primary` / `white`) and label typography. Standard app actions use `48dp` height when explicitly sized.
- **Secondary:** Surface-variant background with ink-variant text and a primary or outline border when needed. Use for "retry", "cancel", and offline fallback actions.
- **Danger:** Error red background for destructive or failed download actions. Do not use red for emphasis.
- **Focus / Disabled:** Focus moves to primary border or label. Disabled containers use `surfaceVariant` with reduced emphasis, never pale text on low-contrast backgrounds.

### Chips
- **Style:** Filter chips use Material 3 chip behavior. Selected states should resolve to primary-container language (`forest-mint` with `mint-chip-text`).
- **State:** Use chips for real mode changes such as `My Journal` versus `Global`, not as decorative tags.

### Cards / Containers
- **Corner Style:** Outer image/card surfaces use `16dp`; inner detail panels use `12dp`; circular media/action affordances use `CircleShape`.
- **Background:** Regular cards use `surface`; secondary panels use `surfaceVariant` or secondary container when the content represents map/download context.
- **Shadow Strategy:** Flat/tonal at rest. Only map tooltips use `8dp` elevation.
- **Border:** Borders are full outlines (`1dp`) with `outline` or `outlineVariant`. Never use side stripes.
- **Internal Padding:** `16dp` is the standard card padding; dense overlays can use `12dp`; screen padding is `24dp` to `32dp` depending on context.

### Inputs / Fields
- **Style:** Outlined text fields with `12dp` radius. Background remains transparent or tonal surface-backed.
- **Focus:** Focus border and label use `primary`; cursor uses `primary`.
- **Error / Warning:** Error uses `error`; warning uses `extendedColors.warning`. Icons are Material Icons, never emoji/text glyphs.

### Navigation
- **Style:** Top app bars sit on `surface` with simple back navigation. The app should feel like a native Android product, not a custom web shell.
- **Map Controls:** Overlays sit at the edge with compact Material controls; do not obscure map content.
- **Camera Controls:** Dark camera chrome is forced. The shutter stays high contrast and physical; gallery uses a circular outlined affordance.

### Capture Review
- **Image Preview:** Large rounded image surface (`16dp`, `280dp` height in current implementation) anchors the review screen.
- **AI Details Toggle:** Circular 54dp affordance, selected state filled with primary, unselected state tonal.
- **Recognition Panel:** Tonal container with full border, not a nested decorative card.

### Offline Map Manager
- **Active Download:** Secondary container card with clear progress, percentage, and state-specific action buttons.
- **Preset Parks:** Simple list cards with title, region, estimated size, and one primary action.
- **Empty State:** Teaches the next action: download a region to use maps offline.

## 6. Do's and Don'ts

### Do:
- **Do** keep the primary context sentence in mind: hikers use this outdoors in bright Patagonia light, often one-handed and offline.
- **Do** use `forest-primary` only for action, selected state, focus, and progress.
- **Do** use `CaptureTheme` for camera, review, and first-run loading when dark camera chrome is the correct UX.
- **Do** use Material Icons for actions and states; give interactive icons clear content descriptions.
- **Do** keep cards flat and tonal unless they are true overlays.
- **Do** write empty and error states that teach the next action, especially offline readiness.

### Don't:
- **Don't** create an **App de caricatura/juguete**: no toy saturation, cartoon wildlife, emoji iconography, or Pokemon-like capture language.
- **Don't** create **Densidad de panel admin**: no dense data tables, dashboard walls, or office-console layouts for field workflows.
- **Don't** use **AI beige/cream**: no cream, sand, parchment, paper, ivory, or warm-neutral body backgrounds as a shortcut to warmth.
- **Don't** use side-stripe borders, gradient text, glassmorphism, hero-metric templates, or identical decorative card grids.
- **Don't** add new hard-coded colors in screens. Route color through `Color.kt`, `Theme.kt`, and `MaterialTheme`.
- **Don't** use text glyphs or emoji as icons. The regression test blocks camera, map, leaf, image, and info text glyphs in presentation code.
