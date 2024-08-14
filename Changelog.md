# Changelog

### Version 1.18.2 - 2.2.1

- fix mobs not dropping any loot when `scale_loot` is turned off
- change how/when default values are used
  - not supported entities (e.g. players) were affected by loot and xp scaling
- fix config option `scale_xp` not affecting the dropped xp

### Version 1.18.2 - 2.1

- fix incorrect piglin eye height
- fix incorrect creeper model
- fix cave spiders not having scaling
- fix child mob scaling

### Version 1.18.2 - 2.0

- random scaling is now tied to the world random
- allow scaling of mob attributes
    - health, damage and speed can now be scaled along with the mob size
    - five options to scale those attributes
        - `none` => no scaling
        - `normal` => same scaling as the mob size
        - `square` => scaling is squared $(scaling*scaling)$
        - `inverse` => scaling is inverted $(1 / scaling)$
        - `inverse_square` => scaling is squared and inverted $(1 / (scaling * scaling))$
- new config format
    - allows to set a default scaling for all mobs
        - uses include/excludelist to include/exclude mobs from the default scaling
        - accepts wildcards like `minecraft:*`
    - all special scalings for mobs are now under `scaling_overrides`
        - all scalings defined here will bypass the default scaling
- better overall config handling
    - does not always overwrite the config file anymore, minor errors will just be ignored and loading continues
    - on major errors the config file will be overwritten with the default config, and a backup of the old config will
      be created
    - entries in `scaling_overrides` are now sorted alphabetically, to stop the shuffling of values that sometimes
      occurs on loading
- add new option to `gaussian` scaling
    - `close_to_original = true` => the mean of the gaussian distribution is dynamically calculated so that most mobs
      are sized close to their original size (which is 1.0)
        - `min_scaling` needs to be below 1.0 for this to work
        - `max_scaling` needs to be above 1.0 for this to work
    - `close_to_original = false` => the mean of the gaussian distribution is at 0.5
- add new scaling type `difficulty`
    - scaling is based on the world difficulty
    - has the options `easy`, `normal`, `hard` and `hardcore`
    - each option is parsed like all other scalings, so you can use `gaussian` or `static` for example
- allow scaling of loot and experience when the mob dies
    - option `scale_loot` to adjust whether or not the amount of dropped loot should be adjusted according to the
      scaling
    - option `scale_xp` to adjust whether or not the amount of dropped xp points should be adjusted according to the
      scaling
- rename command `/mobScalings` to `/mobscalings`

### Version 1.18.2 - 1.3

- fix incorrect suggestions for the `/mobScalings` command
- add support for geckolib models

### Version 1.18.2 - 1.2

- backport to 1.18.2

### Version 1.19.4 - 1.2

- mobs that can convert to other mobs (e.g. zombie to drowned) now keep their scaling when converting
    - can be disabled in the config

### Version 1.19.4 - 1.1

- port to 1.19.4

### Version 1.19.3 - 1.1

- reduce file size of the banner
- add new command `/mobScalings` to set/remove/show the configured mob scalings
    - `/mobScalings set <entity_type> <scaling>` => set static scaling for the entity type
    - `/mobScalings set <entity_type> <scaling_type> <min_scaling> <max_scaling>` => set a specific scaling for the
      entity type
    - `/mobScalings remove <entity_type>` => remove the scaling for the entity type
    - `/mobScalings show` => show all configured mob scalings
    - `/mobScalings show <entity_type>` => show the scaling for the entity type

### Version 1.19.3 - 1.0

- initial release
 