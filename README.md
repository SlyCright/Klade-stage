# Klade Stage - libGDX HTML5 Simulation Client

**Version:** 2026.01.25_ver.03

## Description
The libGDX-based HTML5 rendering engine for Klade Evolutionary Simulation Game. This project contains the GWT-compiled client that renders 2D evolutionary arenas and specimen interactions, designed to be embedded within the Vaadin management interface via iframe integration.

## Live Site: [klade.site](https://klade.site/)

## Vision
A high-performance, visually rich simulation client that brings evolutionary competition to life through fluid animations, particle effects, and dynamic arena rendering. The stage client displays the results of evolutionary algorithms in an interactive, real-time environment where players can watch their species compete and adapt.

## Tech Stack
- **Core Framework**: libGDX 1.14.0 with Ashley ECS 1.7.4
- **Platform**: GWT 2.11.0 (HTML5 backend)
- **Language**: Java 11 (libGDX requirement)
- **Build System**: Gradle (Groovy DSL)

## Current State
The iframe integration proof-of-concept is complete. The client currently displays a basic libGDX application with the framework logo. HUD overlay integration from the Vaadin parent is functional, demonstrating that Vaadin components can be positioned over the libGDX canvas. The Entity Component System architecture is configured but not yet implemented in the simulation logic.

## Project Architecture
This repository contains the **stage/** component of the three-project Klade architecture:
- **main**: Spring Boot + Vaadin management UI (separate repository at https://github.com/SlyCright/Klade)
- **stage** (this repo): libGDX HTML5 simulation client
- **simulation**: Shared simulation logic (future separate repository)

Each project runs independently on different ports and can be opened as separate projects.

With this:
## Setup for Development
1. Open this folder as a standalone project
2. Configure the Gradle JVM to Java 11
3. Use the `copyStageToMain` task (under "Klade" group) to build and copy client to main project 
   (Check whether the path to the main project in stage/build.gradle is correct)

## Building for Production
Generate a production-ready build by executing the 'dist' task in the 'html' module. This produces optimized JavaScript and assets in the html/build/dist directory, ready for deployment as static resources.

## Development Mode
1. Run `copyStageToMain` task in the Gradle tool window (under "Klade" group)
2. This builds the HTML5 client and copies it to main project's static resources
3. Restart the main Spring Boot application to see changes

## Integration Notes
The Vaadin management UI in the main repository embeds this client using an iframe pointed to the static resources served from this module. Authentication context is passed via POST-based JWT token exchange.

## Community & Support
- **Main Repository**: https://github.com/SlyCright/Klade
- **Funding**: https://boosty.to/klade (RUS)
- **License**: Apache 2.0

## License
This project is licensed under the Apache License 2.0 - see the LICENSE file for details.