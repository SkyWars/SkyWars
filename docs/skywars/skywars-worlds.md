SkyWars Worlds
==============

### `SkyWarsArenaWorld`
SkyWarsArenaWorld is where all arenas are kept. It holds no data, and can be deleted at any time the server is not
running without losing anything.

### `SkyWarsBaseWorld`
SkyWarsBaseWorld is the default world that the arena is copied from. If there are no enabled arenas who's origin
boundaries are in this world, it will not be loaded.

If any configured arenas have other worlds listed in the `boundaries.origin.world`, they will be automatically loaded by
SkyWars if they are not already loaded by another plugin (like Multiverse).

The only reason `SkyWarsBaseWorld` is copied & loaded by default is that the default arena, `skyblock-warriors`, lists
`world: SkyWarsBaseWorld` in the origin boundaries.
