## Notes

1. Grateful for the code for TMS267-268 shared online. The database appears to be data from Hertz, thank you for sharing!

2. This is for single-player deployment only, for personal use and learning code development, reverse engineering, etc. There are still many bugs, and each bug is a learning opportunity.

3. Usage: Modify the TMS268 launcher (which requires local time modification) to use the local source code for login. The mainClass is in Net.NetRun.

4. For startup methods, see TMS-245. TMS245 is also relatively stable.

5. Researching single-player mode is a long process. I gave up many times, each time encountering obstacles. It's all out of passion, so thank you to all the friends who shared!

6. **I feel the MoSen client is more complete. I might upgrade TMS245 later.** First, let's study TMS245**

## Code Explanation

1. Most of the functionality is implemented using TMS268; I simply updated from 267 to 268. Thanks to the author.

2. There are significant differences between the TMS245 and TMS268 versions, and the code fixes are not as complete. It can be used to experience the coolness of sixth-tier skills.

3. Completely updated to version 268 on 2025-12-23. If there are bugs, they were already there. This code was cleaned up from version 268, and many bugs were obtained through decompilation, resulting in very slow startup. Cleaning it up was also very troublesome; I even considered deleting it, but a lot of effort was spent optimizing and integrating the code.

Please cherish this code; I hope it will be helpful to you. Please raise an issue if you have any questions.

## Debugging

1. Log configuration is in the logback.xml file. However, most debugging is not done via console.

2. System.setProperty("debug", "dev"); will enable logging. You can turn it off if not needed.

3. MySQL 5.6 / JDK 21

4. Command view: scripts/commands/entry.js !+corresponding command

For example, to farm items: !item 2435719 999
