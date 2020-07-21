# PKWrapper

PKWrapper is a kotlin CLI wrapper for [@Dabomstew's](https://github.com/Dabomstew) [Universal Pokemon Randomizer](https://github.com/Dabomstew/universal-pokemon-randomizer).
The purpose of this tool is to provide a single CLI for generating a randomized Pokemon ROM using an input path, an output path, and a settings file path.
The settings file can either be a .rnqs quick settings file from the randomizer itself, or a .json representation.

## Setup

Download or compile a binary of the Universal Pokemon Randomizer, and place the .jar file at `libs/randomizer.jar`.

After that, build the project with gradle:

```shell script
./gradlew build
```

The application will be built and saved as `build/libs/pkwrapper-$version.jar`.

## Usage

Make sure you have Java installed, and run the following command:

```shell script
java -jar pkwrapper <input> <output> <settings> [--seed seed] [--log logPath]
```

Randomize a ROM:

```shell script
java -jar pkwrapper emerald.gba emerald-random.gba race.rnqs
```

With a predefined seed (long):

```shell script
java -jar pkwrapper emerald.gba emerald-random.gba race.rnqs --seed 3439099000742416222
```

With settings in a JSON file:

```shell script
java -jar pkwrapper emerald.gba emerald-random.gba race.json
```

With a log file:

```shell script
java -jar pkwrapper emerald.gba emerald-random.gba race.rnqs --log race.log
```
