# Feature Landscape: Offline ML & Maps

## 1. Offline Region Manager
- **Description:** A dedicated UI for users to select, download, and manage map regions.
- **Why:** Offline map tiles and styles consume massive storage. Users must be able to visually see what is downloaded, pause/resume downloads, and clear old data to free up space.

## 2. Pre-Trip Asset Checker
- **Description:** A utility that verifies all necessary ML models, species wikis, and maps are fully downloaded and validated before the user goes offline.
- **Why:** Prevents the "I thought I downloaded it" failure when users reach the trailhead without signal.

## 3. Local-First Capture Journal
- **Description:** Captures (photos + metadata) are instantly saved to the local database and marked as "Pending Sync".
- **Why:** Immediate feedback. The user should never see a loading spinner waiting for a network request when logging a species.

## 4. Adaptive Camera Frame Analysis
- **Description:** The ML recognition view intelligently drops frames if the processor cannot keep up, rather than queuing them.
- **Why:** Prevents OutOfMemory errors and thermal throttling when the device is under heavy load.
