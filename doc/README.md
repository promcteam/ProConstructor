[![Build](https://github.com/promcteam/ProSchematicBuilder/actions/workflows/maven.yml/badge.svg?branch=main)](https://s01.oss.sonatype.org/content/repositories/snapshots/com/promcteam/proschematicbuilder/)
[![Build](https://github.com/promcteam/ProSchematicBuilder/actions/workflows/release.yml/badge.svg?branch=main)](https://s01.oss.sonatype.org/content/repositories/snapshots/com/promcteam/proschematicbuilder/)
[![Build](https://github.com/promcteam/ProSchematicBuilder/actions/workflows/devbuild.yml/badge.svg?branch=dev)](https://s01.oss.sonatype.org/content/repositories/snapshots/com/promcteam/proschematicbuilder/${project.version}/)

# ${project.name}
Let [Citizens](https://www.spigotmc.org/resources/citizens.13811/) NPCs build your schematics and structures block by 
block.

## Dependencies

- [Citizens](https://www.spigotmc.org/resources/citizens.13811/)
- [ProMCCore](https://www.spigotmc.org/resources/promccore.93608/)

## Usage
1) Create a NPC with Citizens and give it the 'builder' trait.

Example: `/npc create Bob --trait builder`

2) Select the NPC, either by right clicking them or through `/npc select <npc>`
3) Load a schematic with `/schematicbuilder load <file name>`.

Example: `/schematicbuilder load house.schem`

This will load the included example file `house.schem`.

4) Position the NPC in the center of the area in which it should build.
5) Start the NPC building with: `/schematicbuilder build`
6) Watch the magic happen!
