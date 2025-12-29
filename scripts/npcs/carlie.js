

if (npc.askYesNo("你需要我的力量嗎")) {
    /* Response is Yes */
    player.addEdraCount(20999);
    player.gainItem(4009547, 1000)

} else {
    /* Response is No */
    npc.say("想好後在與我說話.");
}