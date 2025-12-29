var status = 0;
var regcost = 499;
var vipcost = 999;
var tempvar;
var townmaps = Array(
    Array(105000011, "一般桑拿室", 499),
    Array(105000012, "高級桑拿室", 999),
);

let selection = 0
var selects = 0;

npc.sendNext("歡迎來到奇幻村旅店，我們致力為您提供最好的服務，如果您累了，來這裏休息一下如何？");
var text = "我們提供兩種房間，請選擇你想要的：\r\n#d"

for (var i = 0; i < townmaps.length; i++) {
    text += "#L" + i + "##k" + townmaps[i][1] + "價格：" + townmaps[i][2] + '#l\r\n';
}
selection = npc.askMenu(text);

selects = selection;
selection = npc.me().next().askYesNoX("在這裡的事情辦完了嗎？確定要去你想要去的地方了嗎？");

if (selection) {
    npc.sendNext("你選擇了：" + townmaps[selects][1]);
    player.changeMap(townmaps[selects][0]);
    player.gainMeso(townmaps[selects][2]*-1);

}



// function action(mode, type, selection) {
//     // npc.next().sayX("How easy is it to consume the item? Simple, right? You can set a #bhotkey#k on the right bottom slot. Haha you didn't know that! right? Oh, and if you are a beginner, HP will automatically recover itself as time goes by. Well it takes time but this is one of the strategies for the beginners.");
//
//     if (mode == 1)
//         status++;
//     if (mode == 0 && status == 1) {
//         // cm.dispose();
//         return;
//     } if (mode == 0 && status == 2) {
//         npc.sendNext("我们也提供其他服务，决定好之前请仔细想想。");
//         cm.dispose();
//         return;
//     }
//     if (status == 0) {
//         npc.sendNext("歡迎來到奇幻村旅店，我們致力為您提供最好的服務，如果您累了，來這裏休息一下如何？");
//     }
//     if (status == 1) {
//         npc.sendSimple("我们提供两种房间，请选择你想要的\r\n#b#L0#一般桑拿室 (每次 " + regcost + " 枫币)#l\r\n#L1#高级桑拿室 (每次" + vipcost + " 枫币)#l");
//     }
//     if (status == 2) {
//         tempvar = selection;
//         if (tempvar == 0) {
//             npc.sendYesNo("你选择了一般桑拿室，你的HP和MP会回复得很快，你也可以在里面购买商品，你确定要进入吗？");
//         }
//         if (tempvar == 1) {
//             npc.sendYesNo("你选择了高级桑拿室，你的HP和MP会比一般桑拿室回复得更快，也可以在里面找到特殊的物品，你确定要进入吗？");
//         }
//     }
//     if (status == 3) {
//         if (tempvar == 0) {
//             if (npc.getMeso() >= regcost) {
//                 npc.warp(105040401);
//                 npc.gainMeso(-regcost);
//             } else {
//                 npc.sendNext("很抱歉，看起来您似乎没有足够的枫币。你至少要有 " + regcost + " 枫币才能待在我们的一般桑拿室。");
//             }
//         } if (tempvar == 1) {
//             if (npc.getMeso() >= vipcost) {
//                 npc.warp(105040402);
//                 npc.gainMeso(-vipcost);
//             } else {
//                 npc.sendNext("很抱歉，看起来您似乎没有足够的枫币。你至少要有 " + vipcost + " 枫币才能待在我们的高级桑拿室。");
//             }
//         }
//         npc.dispose();
//     }
// }