# Changelog

### Version 1.20.1 - 1.2.1

- fix incorrect suggestions for the `/mobScalings` command

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
 