# Changelog

### Version 1.20.1 - 2.0

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
        - uses whitelist/blacklist to include/exclude mobs from the default scaling
        - accepts wildcards like `minecraft:*`
    - all special scalings for mobs are now under `scaling_overrides`
        - all scalings defined here will bypass the default scaling
- better overall config handling
    - does not always overwrite the config file anymore, minor errors will just be ignored and loading continues
    - on major errors the config file will be overwritten with the default config, and a backup of the old config will
      be created

### Version 1.20.1 - 1.3

- fix incorrect suggestions for the `/mobScalings` command
- add support for geckolib models

### Version 1.20.1 - 1.2

- port to 1.20.1

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
 