# openvelov

an open source material 3 expressive android app for velov bike sharing system in lyon, france.
\
\
/!\ using a third party client to access velov data is against velov terms of service. use this app at your own risk.

## features

this is still under active development, but the following features are already implemented:

- [x] login using your velov account
- [x] view the different stations on the map
- [x] view the number of available bikes and docks at each station
- [ ] view your ride history
- [ ] view your profile information
- [ ] view your subscription information
- [ ] buy & activate subscriptions in app
- [ ] unlock bikes & see trip info!

more to come very soon! open an issue if anything mentioned above is not working.

## screenshots

<img src="/assets/details.png" height="400"> <img src="/assets/map.png" height="400"> <img src="/assets/login.png" height="400">

## installation

since this is still a wip, there isn't any stable release right now. grab the latest apk from the action that runs on every push or build it yourself

## building

run `./gradlew assembleDebug` to build the debug apk. \ you can also run `./gradlew assembleRelease` to build a release apk, but you'll need to provide your own signing config. \
the app also needs some credentials to access the velov api, which you can find either by decompiling the official app or by asking me! (i don't think i can give them out publicly here :sob:) \
\
local.properties example:

```
velovClientKey=
velovRefreshToken=
// only required for release builds
releaseStoreFile=
releaseStorePassword=
releaseKeyAlias=
releaseKeyPassword=
```
