var amount = 10;


if (player.getFreeSlots(1)) {
    npc.sendOk("#e<獲得道具>#b\r\n#i" + 2435719 + ":# #t" + 2435719 + "# " + amount + "個");
    player.gainItem(2436078, amount);
    player.loseItem(npc.getItemId(), 1);
} else {
    npc.sendNext("請確認裝備欄有足夠欄位.");
}
