# PKWrapper

PKWrapper is a kotlin CLI wrapper for [@Dabomstew's](https://github.com/Dabomstew) [Universal Pokemon Randomizer](https://github.com/Dabomstew/universal-pokemon-randomizer).
The purpose of this tool is to provide a single CLI for generating a randomized Pokemon ROM with a JSON or RNQS config file.

This exists because the original randomizer does not have a CLI, and I wanted an excuse to try out Kotlin.

## Setup

Download or compile a binary of the Universal Pokemon Randomizer, and place the .jar file at `libs/randomizer.jar`.

After that, build the project with gradle:

```shell script
./gradlew build
```

The application will be built and saved as `build/libs/pkwrapper-$version.jar`.

## Usage

### Randomize

Use the `randomize` command to randomize a ROM.

`<config>`, `<input>` and `<output>` must be either a path to a file or a stream.

```shell script
java -jar pkwrapper randomize <config> <input> <output> [--seed seed] [--log logPath]
```

Randomize a ROM:

```shell script
java -jar pkwrapper randomize race.rnqs emerald.gba emerald-random.gba
```

With a predefined seed (long):

```shell script
java -jar pkwrapper randomize race.rnqs emerald.gba emerald-random.gba --seed 3439099000742416222
```

With settings in a JSON file:

```shell script
java -jar pkwrapper randomize race.json emerald.gba emerald-random.gba
```

With a log file:

```shell script
java -jar pkwrapper randomize race.rnqs emerald.gba emerald-random.gba --log race.log
```

### JSON

Use the `json` command to convert a settings file into JSON.

```shell script
java -jar pkwrapper json race.rnqs race.json [--pretty]
```

Pretty print

```shell script
java -jar pkwrapper json race.rnqs race.json --pretty
```

### RNQS

Use the `rnqs` command to convert a settings file into a randomizer quick settings formatted file.

```shell script
java -jar pkwrapper rnqs race.json race.rnqs
```

## Software Used

- [Apache Commons IO](https://github.com/apache/commons-io) Apache 2.0
- [Clikt](https://github.com/ajalt/clikt) Apache 2.0
- [Gson](https://github.com/google/gson) Apache 2.0
- [Universal Pokemon Randomizer](https://github.com/Dabomstew/universal-pokemon-randomizer) GPL-3.0

## License

PKWrapper is licensed under the GNU General Public License v3.0.