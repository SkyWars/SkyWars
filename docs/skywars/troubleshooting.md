Troubleshooting
===============

### Players can't break blocks in the arena!
Try setting 'spawn-protection' in server.properties to 0.
If this solves your problem, then great! If it doesn't continue on to 1).


1. Try starting the server with no plugins besides SkyWars.  This is easily done by moving all of the plugin jar files
   outside of the plugins/ folder. If removing all of the plugins works, continue on to step 2.

- If that doesn't work, then [submit a ticket](https://github.com/skywars/submitting-a-ticket).

2. If removing all plugins works, you can use a [binary search](https://en.wikipedia.org/wiki/Binary_search_algorithm)
   method to find which plugin is causing the problem. This basically means putting back half the plugins, start the
   server and see if it works. If it does work than put in half of the remaining plugins, if it doesn't work remove half
   of the plugins you put in. Repeat this process until you narrow it down to one plugin that removing will stop the
   issue, and adding will reproduce the issue.

   Once you find which plugin is causing the issue, go ask the plugin author if the plugin could be stopping people from
   breaking blocks - and how to keep it from doing that. Perhaps there is a permission that you forgot to give them?
   Anyways, the issue is now on that plugin, not part of SkyWars. While we can try to help you, it is ultimately that
   plugin's author's job.

### UnsupportedClassVersionError unsupported major.minor version ...

If you have such an error when starting up the server, it likely means you need java 7, and do not have it.

If you are hosting on a host that gives you no control over your server besides Multicraft, there is not much you can do
except for bug them to update.

If you are home hosting / hosting on a vps/dedi you should update your java version to java 7.

If you already think you have java 7, try looking through installed programs (control panel on windows or aptitude on
debian-based systems) and see if java 6 is installed. Java 6 sometimes overrides java 7 if it is installed.

### The plugin doesn't work / other errors

You might have found an error in SkyWars!

[Submit a ticket](https://github.com/skywars/submitting-a-ticket) with your issue.
Explain what is happening, and include any errors that show in console.
