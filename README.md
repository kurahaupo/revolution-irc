Revolution IRC
==============
 
Revolution IRC Client is the next-generation IRC client for Android, made with design and functionality in mind. Let's start this revolution!

<a href="https://f-droid.org/packages/io.mrarm.irc/" target="_blank">
<img src="https://f-droid.org/badge/get-it-on.png" alt="Get it on F-Droid" height="80"/></a>
<a href="https://play.google.com/store/apps/details?id=io.mrarm.irc" target="_blank">
<img src="https://play.google.com/intl/en_us/badges/images/generic/en-play-badge.png" alt="Get it on Google Play" height="80"/></a>

Changes in debug build: versionName 0.5.4c versionCode 15 (plp 20210502 pl: v1a):

* The Ignore list entries are now full regexps vs. the previous rather undocumented
  glob match style accepting just `* ?` wildcards for each of `name user host` in
  the ignore pattern sets.
* The new `Message text` pattern is matched against the whole message text line.
* Known issues: none.  
   *Note that using many matching rules with a high traffic server increases battery consumption, as expected. The CPU works more.*

This client features a modern Material design as well as many other awesome features:

* Stays in background properly, even on more recent Android versions
* Store chat messages to be displayed after reconnecting to the server later
* Nick/channel/command autocomplete
* Ignore list
* mIRC color formatting support
* SSL certificate exception list
* Command list to run after connecting
* Customization: custom command aliases, notification rules, reconnection interval, chat font, message format, app colors

...and much more!
