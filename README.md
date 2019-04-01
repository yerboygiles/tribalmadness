Tribal Trouble
==============
(this section preserved from the [upstream repo](https://github.com/sunenielsen/tribaltrouble))

Tribal Trouble is a realtime strategy game released by Oddlabs in 2004. In 2014 the source was released under GPL2 license, and can be found in this repository.

The source is released "as is", and Oddlabs will not be available for help building it, modifying it or any other kind of support. Due to the age of the game, it is reasonable to expect there to be some problems on some systems. Oddlabs has not released updates to the game for years, and do not intend to start updating it now that it is open sourced.

**If** you know how to code Java, configure ant, use MySQL, and have a **genuine intention** of actually working on the game, you can create an issue for detailed questions about the source.

About the fork yerboygiles stole
---------------

I have been working to restore this enjoyable game to working order with modern Java. This has included updating to more recent LWJGL, JInput and OpenAL, removing the need for registration, removing demo mode, removing updater as well as many Java modernizations and cleanups. The ultimate goal is to produce a jar that can be run simply with `java -jar tt.jar` on MacOS X, Linux and Windows.

About the new repo yerboygiles has made
---------------

The game is able to be run by just command-lining "ant run" in the tt directory. I plan on making textural and unit changes, possibly even trying to create another race throughout my time in college. 

Building
--------
Clone the repository:
```
git clone https://github.com/bondolo/tribaltrouble.git
```
Make sure you have Java SDK at least version 8, and Apache Ant.


To build the game client, do this:
```
cd tt
ant run
```

Setting up a server is a lot more complex, and not something we have done in many years. It will take some work to get it working, but try looking at the server folder and see if you can figure it out. At the very least, you should know a bit about setting up a MySQL server.
