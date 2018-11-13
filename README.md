###MCLGradlePlugin


MCLGraldePlugin is part of ModularClassLoader, helping to simplify
how it is used.  
Simply add 
```groovy
buildscript {
    repositories {
        mavenLocal()
        jcenter()
        maven { url "https://maven.covers1624.net/" }
        maven { url "http://chickenbones.net/maven" }
    }
    
    dependencies {
        classpath "net.covers1624:MCLGradlePlugin:1.0-SNAPSHOT"
    }
}
apply plugin: 'net.covers1624.mcl'

mcl {
    makePack false
    resolverDirectory "libs"
}

```
####Why those repositories?
- `https://maven.covers1624.net/` Is where this plugin is hosted.
- `http://chickenbones.net/maven` A dependency is required from here.

####How use? 
`makePack` Controls if a your libraries and main jar will be placed
inside a tar.xz file or not, `resolverDirectory` is the relative path
to your main jar where you want libraries to live.

If you have any questions or actually want to use this train wreck
feel free to poke me / create an issue.
