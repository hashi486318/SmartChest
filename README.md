# SmartChest

A storage-focused Minecraft mod that makes organizing and accessing large amounts of items easier through a page-based interface.

## Overview

SmartChest is designed around the idea of accessing multiple storage pages from a single chest, rather than simply adding a larger chest.

It provides a compact and organized way to manage a large number of items while keeping the experience close to vanilla Minecraft.

## Requirements

- Minecraft 1.20.1
- Forge
- Java 17

## Features

### 540 Slots

SmartChest provides a total of 540 storage slots.

- 10 pages
- 54 slots per page
- 540 slots in total

Each page can be accessed from the same interface, allowing large amounts of items to be organized without placing many separate chests.

### Page-Based Interface

SmartChest uses a multi-page storage system.

Pages can be switched using tabs, and each page can have its own icon.

Page icons can be used to visually categorize storage, such as:

- Ores
- Building blocks
- Food
- Tools
- Miscellaneous items

### External Item Logistics

SmartChest is not designed as an automated logistics system.

External item transportation, such as hopper-based automation, is not part of its core functionality.

The intended use is:

> Player → SmartChest

rather than:

> Machine → SmartChest

### Durability

SmartChest is designed to be sturdy while still behaving like a wooden storage block.

- Axe is the preferred tool
- Can still be broken without the preferred tool
- High resistance to explosions

SmartChest is intended to survive common accidental explosions while remaining destructible by powerful explosions.

### Portability

SmartChest is intended to behave as a chest, allowing it to work with mods that provide chest-carrying functionality.

This makes it possible to move the storage itself without having to empty it first.

## Design Concept

SmartChest is not intended to be simply a high-capacity chest.

Its core concept is:

> **A chest that lets you access large amounts of storage as if you were turning pages.**

The crafting recipe reflects this concept by combining familiar Minecraft components:

- Lectern — page-based interaction
- Books — pages and information
- Trapped Chest — storage with a mechanism
- Redstone Torch — control circuitry
- Redstone — circuitry
- Repeater — signal processing

The result is intended to feel like a small mechanical contraption that can be built using familiar Redstone components.

## Recipe

```text
Redstone Torch | Redstone      | Redstone Torch
Book           | Trapped Chest | Book
Repeater       | Lectern       | Repeater
```

## License

SmartChest is licensed under the [MIT License](LICENSE).
