# pkg-helper
An experimental smart Pokemon Go bot. Experimental because im most likely going to break everything eventually.

### Implemented features
- [x] Logging In and parsing longitude/latitude from properties
- [x] Finding nearby pokemons
- [x] Finding nearby pokestops
- [x] Walking randomly around the map
- [x] Looting nearby pokestops
- [x] Catching nearby pokemons
- [x] Automatically hatching eggs
- [x] Automatically transferring bad pokemons with CP lower than 200 if you have more than one of the type.

### Installation instructions
Just now the only way to install this is compiling it yourself, to do so clone the repo doing:
```shell
git clone git@github.com:erosemberg/pkg-helper.git
```
And then run
```shell
gradle clean build shadowJar & cd build/libs/ & java -jar pkgo-helper-ALPHA-1.0-all.jar -d
```

###Acknowledgements
Thanks to @jabbink for providing the magic values.
