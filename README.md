# pkg-helper
Pokemon GO Helper bot

### Implemented features
- [x] Logging In and parsing longitude/latitude from properties
- [x] Finding nearby pokemons
- [ ] Finding nearby pokestops
- [ ] Walking randomly around the map
- [ ] Looting nearby pokestops
- [ ] Catching nearby pokemons

### Installation instructions
Just now the only way to install this is compiling it yourself, to do so clone the repo doing:
```shell
git clone <repo url>
```
And then run
```shell
gradle clean build shadowJar & cd build/libs/ & java -jar <jarfile>
```
