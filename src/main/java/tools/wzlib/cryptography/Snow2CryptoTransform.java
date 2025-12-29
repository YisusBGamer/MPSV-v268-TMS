package tools.wzlib.cryptography;


import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class Snow2CryptoTransform {
    public final int InputBlockSize = 4;
    public final int OutputBlockSize = 4;
    public final boolean CanTransformMultipleBlocks = true;
    public final boolean CanReuseTransform = false;
    private boolean encrypting;
    private int s15;
    private int s14;
    private int s13;
    private int s12;
    private int s11;
    private int s10;
    private int s9;
    private int s8;
    private int s7;
    private int s6;
    private int s5;
    private int s4;
    private int s3;
    private int s2;
    private int s1;
    private int s0;
    private int r1;
    private int r2;
    private int[] keyStream;
    private int curIndex;
    private static final int[] snow_alpha_mul = new int[]{0, -509620461, 1805072166, -1979123659, -695767476, 924361055, -1123002006, 1552914041, 94887064, -466087029, 1848699838, -1884347219, -752831788, 851410375, -1195932174, 1495812833, 182915481, -344400246, 1634739903, -2131760724, -597655595, 1040167110, -1208530701, 1449691104, 255905025, -287374830, 1691875879, -2058865356, -641227955, 945319006, -1303341973, 1406098296, 342303387, -168235640, 2146440637, -1636836690, -1025487657, 595559364, -1451787279, 1223210210, 297860611, -262196976, 2052573477, -1681390026, -951611313, 651714396, -1395611799, 1297049722, 511718146, -14681071, 1964442660, -1802974409, -939041458, 697864797, -1550816664, 1108321659, 455602074, -88596343, 1890638012, -1859184721, -845119018, 742346437, -1506298128, 1202223587, 684606623, -917402740, 1129935801, -1564033878, -28759341, 534177216, -1780605451, 1950470886, 761894919, -856271084, 1191096097, -1486790606, -68225461, 443627864, -1871068819, 1910902398, 573134086, -1011443179, 1237213728, -1474187981, -189908150, 355595353, -1623651220, 2124858239, 663652766, -971946355, 1276755640, -1383698005, -251009070, 278276289, -1700867852, 2063671271, 1017734660, -583620329, 1463701794, -1230922191, -366081976, 196200283, -2118566034, 1613164669, 957266588, -661555825, 1385795002, -1291435351, -276179760, 236329923, -2078350346, 1702964453, 911112093, -674121586, 1574518971, -1136226392, -523691567, 22468290, -1956761865, 1791091172, 870951685, -763993066, 1484692515, -1176415440, -445725367, 82905690, -1896222097, 1868971388, 1345685655, -1314240636, 1000521649, -633507678, -2035095845, 1731013064, -316288515, 213524206, 1435653135, -1274176740, 1040540457, -543511494, -2095760829, 1653273936, -394130075, 152945270, 1523751182, -1152559587, 826650152, -790994629, -1940523198, 1841969233, -406667164, 106762103, 1601521046, -1091925371, 887255728, -713179741, -1980617766, 1752032457, -496689924, 66769903, 1146268172, -1513265889, 801479978, -832941511, -1831484352, 1934232403, -113052826, 417152117, 1106605716, -1603618425, 711082418, -872575327, -1754130216, 1995298763, -52088834, 494592237, 1320532885, -1356172154, 623021235, -994229344, -1741498919, 2041387722, -207232257, 305802732, 1259497229, -1433556962, 545607723, -1055219912, -1651177151, 2081080914, -167625113, 396226932, 2029767688, -1721498853, 325909294, -218942403, -1367563708, 1340304727, -974416542, 611605105, 2103185552, -1664884861, 382412726, -145430363, -1411678500, 1246015951, -1068742150, 567510761, 1914494353, -1820126590, 428599991, -132897372, -1533299747, 1157921998, -821263109, 781405160, 2008744201, -1775972838, 472659503, -38536900, -1589874875, 1084465238, -894740381, 724866928, 1822224019, -1929174656, 118217141, -426502490, -1172602657, 1535397836, -779307015, 806582506, 1765487115, -2002453224, 44827949, -483145154, -1078174649, 1579389780, -735351967, 901031026, 1719402250, -2015088615, 233621548, -328005825, -1325625018, 1365466709, -613702048, 989096307, 1675371410, -2109477759, 139138228, -371926105, -1252307490, 1422164685, -557024520, 1062450667};
    private static final int[] snow_alphainv_mul = new int[]{0, 403652813, 807305267, 672252158, 1614588262, 2016668075, 1344416085, 1210935704, -1065813044, -663241983, -261719041, -395756750, -1606135126, -1203039641, -1873096039, -2007657900, 703618865, 838803452, 435085058, 31563727, 1238169175, 1371780762, 2043836004, 1641887401, -376920835, -242752464, -644471602, -1046911997, -1984688741, -1849995946, -1180004952, -1582969499, 1380564578, 1246166703, 1649884753, 2052619932, 846534404, 712660937, 40605495, 442816506, -1842264658, -1975646877, -1573927523, -1172273840, -233968440, -368923643, -1038914309, -635687882, 2075779411, 1672913310, 1269260640, 1403527597, 461719605, 59377912, 731629574, 865372363, -1144972641, -1546757550, -1948280148, -1815029151, -604130311, -1007488204, -337431606, -202607865, -1533880892, -1130523383, -1802742281, -1937566406, -995197790, -593413009, -189727599, -322978724, 1693068808, 2095410885, 1425321531, 1291579126, 81168238, 484034467, 885524317, 751257488, -1922921739, -1787966920, -1115944250, -1519171061, -312597613, -179215522, -582966368, -984620179, 1293578553, 1427452404, 2097475850, 1695265223, 757520479, 891918482, 490231916, 87496865, -153392218, -288085141, -960107627, -557143208, -1763196224, -1897364979, -1493613837, -1091173826, 916689002, 783077543, 113053785, 515002516, 1453275404, 1318091201, 1719777599, 2123299314, -550816617, -953912230, -281693020, -147131287, -1088971279, -1491542724, -1895227966, -1761190641, 525586267, 123506582, 793595752, 927076261, 2138007101, 1734354672, 1332864526, 1467918019, -510852319, -108903444, -778861806, -912473121, -2119062969, -1715541366, -1313920396, -1449104711, 569768173, 972732448, 300644574, 165951507, 1103712651, 1506152774, 1909969336, 1775800693, -931308528, -797827875, -127673309, -529752850, -1472072330, -1337018949, -1738574523, -2142227064, 134588380, 269149969, 941303791, 538208034, 1748569786, 1882607223, 1478987401, 1076416068, -1278952125, -1412694642, -2082849424, -1680507459, -738716635, -872983320, -471428074, -68561701, 1941718671, 1806894658, 1134741180, 1538098801, 327217129, 193965860, 597585882, 999370519, -1707810190, -2110020929, -1440062911, -1306189172, -100119788, -502854695, -904475865, -770077718, 1514936766, 1111710067, 1783798157, 1918753088, 980463832, 578809877, 174993643, 308375590, 1159714533, 1561368104, 1963022038, 1829639707, 623083395, 1026309966, 356384688, 221429629, -2056835799, -1654100508, -1250317030, -1384714793, -446987185, -44776318, -716897156, -850770767, 1827637716, 1960888601, 1559300583, 1157515562, 215163058, 349986943, 1020108929, 616751180, -1399361000, -1265093931, -1668681173, -2071547162, -861152386, -727409741, -55223475, -457565312, 391538823, 257501258, 659089588, 1061660793, 2003485153, 1868923180, 1198801362, 1601896735, -684813493, -819866746, -416279688, -12627019, -1223542227, -1357022496, -2029209058, -1627129133, 1051080630, 648640379, 246986629, 381155144, 1587191504, 1184226845, 1854152419, 1988845102, -18953094, -422474569, -826258359, -691073916, -1629330148, -2031278639, -1359157969, -1225546270};
    private static final int[] snow_T0 = new int[]{-1520213050, -2072216328, -1720223762, -1921287178, 0xDF2F2FF, -1117033514, -1318096930, 1422247313, 1345335392, 50397442, -1452841010, 2099981142, 436141799, 1658312629, -424957107, -1703512340, 1170918031, -1652391393, 1086966153, -2021818886, 368769775, -346465870, -918075506, 0xBF0F0FB, -324162239, 1742001331, -39673249, -357585083, -1080255453, -140204973, -1770884380, 1539358875, -1028147339, 486407649, -1366060227, 1780885068, 1513502316, 1094664062, 49805301, 1338821763, 1546925160, -190470831, 887481809, 150073849, -1821281822, 1943591083, 1395732834, 1058346282, 201589768, 1388824469, 1696801606, 1589887901, 672667696, -1583966665, 251987210, -1248159185, 151455502, 907153956, -1686077413, 1038279391, 652995533, 1764173646, -843926913, -1619692054, 453576978, -1635548387, 1949051992, 773462580, 756751158, -1301385508, -296068428, -73359269, -162377052, 1295727478, 1641469623, -827083907, 2066295122, 0x3EE3E3DD, 1898917726, -1752923117, -179088474, 1758581177, 0, 753790401, 1612718144, 536673507, -927878791, -312779850, -1100322092, 1187761037, -641810841, 1262041458, -565556588, -733197160, -396863312, 1255133061, 1808847035, 720367557, -441800113, 385612781, -985447546, -682799718, 0x55333366, -1803188975, -817543798, 284817897, 100794884, -2122350594, -263171936, 1144798328, -1163944155, -475486133, -212774494, -22830243, -1069531008, -1970303227, -1382903233, -1130521311, 1211644016, 83228145, -541279133, -1044990345, 1977277103, 1663115586, 806359072, 452984805, 250868733, 1842533055, 1288555905, 336333848, 890442534, 804056259, -513843266, -1567123659, -867941240, 957814574, 1472513171, -223893675, -2105639172, 1195195770, -1402706744, -413311558, 723065138, -1787595802, -1604296512, -1736343271, -783331426, 2145180835, 0x66222244, 2116692564, -1416589253, -2088204277, -901364084, 703524551, -742868885, 1007948840, 2044649127, -497131844, 487262998, 1994120109, 1004593371, 1446130276, 1312438900, 503974420, -615954030, 168166924, 1814307912, -463709000, 1573044895, 1859376061, -273896381, -1503501628, -1466855111, -1533700815, 937747667, -1954973198, 854058965, 1137232011, 1496790894, -1217565222, -1936880383, 1691735473, -766620004, -525751991, -1267962664, -95005012, 133494003, 636152527, -1352309302, -1904575756, -374428089, 0x18080810, -709182865, -2005370640, 1864705354, 1915629148, 605822008, -240736681, -944458637, 1371981463, 602466507, 2094914977, -1670089496, 555687742, -582268010, -591544991, -2037675251, -2054518257, -1871679264, 1111375484, -994724495, -1436129588, -666351472, 84083462, 32962295, 302911004, -1553899070, 1597322602, -111716434, -793134743, -1853454825, 1489093017, 656219450, -1180787161, 954327513, 335083755, -1281845205, 0x33111122, -1150719534, 1893325225, -1987146233, -1483434957, -1231316179, 572399164, -1836611819, 552200649, 1238290055, -11184726, 2015897680, 2061492133, -1886614525, -123625127, -2138470135, 386731290, -624967835, 837215959, -968736124, -1201116976, -1019133566, -1332111063, 1999449434, 286199582, -877612933, -61582168, -692339859, 974525996};
    private static final int[] snow_T1 = new int[]{1667483301, 2088564868, 0x7777EE99, 2071721613, -218956019, 0x6B6BD6BD, 1869602481, -976907948, 808476752, 16843267, 1734856361, 724260477, -16849127, -673729182, -1414836762, 1987505306, -892694715, -2105401443, -909539008, 2105408135, -84218091, 1499050731, 1195871945, -252642549, -1381154324, -724257945, -1566416899, -1347467798, -1667488833, -1532734473, 1920132246, -1061119141, -1212713534, -33693412, -1819066962, 640044138, 909536346, 1061125697, -134744830, -859012273, 875849820, -1515892236, -437923532, -235800312, 1903288979, -656888973, 825320019, 353708607, 67373068, -943221422, 589514341, -1010590370, 404238376, -1768540255, 84216335, -1701171275, 117902857, 303178806, -2139087973, -488448195, -336868058, 656887401, -1296924723, 1970662047, 151589403, -2088559202, 741103732, 437924910, 454768173, 1852759218, 1515893998, -1600103429, 1381147894, 993752653, -690571423, -1280082482, 690573947, -471605954, 791633521, -2071719017, 1397991157, -774784664, 0, -303185620, 538984544, -50535649, -1313769016, 1532737261, 1785386174, -875852474, -1094817831, 960066123, 1246401758, 1280088276, 1482207464, -808483510, -791626901, -269499094, -1431679003, -67375850, 1128498885, 1296931543, 0x33336655, -2054876780, 1162185423, -101062384, 33686534, 2139094657, 1347461360, 1010595908, -1616960070, -1465365533, 1364304627, -1549574658, 1077969088, -1886452342, -1835909203, -1650646596, 943222856, -168431356, -1128504353, -1229555775, -623202443, 555827811, 269492272, -6886, -202113778, -757940371, -842170036, 202119188, 320022069, -320027857, 1600110305, -1751698014, 0x444488CC, 387395129, -993750185, -1482205710, 2122251394, 1027439175, 1684326572, 1566423783, 421081643, 1936975509, 1616953504, -2122245736, 1330618065, -589520001, 0x22224466, 707417214, -1869595733, -2004350077, 1179028682, -286341335, -1195873325, 336865340, -555833479, 1583267042, 185275933, -606360202, -522134725, 842163286, 976909390, 168432670, 1229558491, 101059594, 606357612, 1549580516, -1027432611, -741098130, -1397996561, 1650640038, -1852753496, -1785384540, -454765769, 2038035083, -404237006, -926381245, 926379609, 1835915959, -1920138868, -707415708, 1313774802, -1448523296, 1819072692, 1448520954, -185273593, -353710299, 1701169839, 2054878350, -1364310039, 0x8081018, -1162186795, 2021191816, 623200879, 774790258, 471611428, -1499047951, -1263242297, -960063663, -387396829, -572677764, 1953818780, 522141217, 1263245021, -1111662116, -1953821306, -1970663547, 1886445712, 1044282434, -1246400060, 0x6666CCAA, 1212715224, 50529797, -151587071, 235805714, 1633796771, 892693087, 1465364217, -1179031088, -2038032495, -1044276904, 488454695, -1633802311, -505292488, -117904621, -1734857805, 0x11112233, 1768542907, -640046736, -1903294583, -1802226777, -1684329034, 505297954, -2021190254, -370554592, -825325751, 0x5555AAFF, 673730680, -538991238, -1936981105, -1583261192, -1987507840, 218962455, -1077975590, -421079247, 1111655622, 1751699640, 1094812355, -1718015568, 757946999, 252648977, -1330611253, 1414834428, -1145344554, 370551866};
    private static final int[] snow_T2 = new int[]{1673962851, 2096661628, 0x77EE9977, 2079755643, -218165774, 0x6BD6BD6B, 1876865391, -980331323, 811618352, 16909057, 1741597031, 727088427, -18408962, -675978537, -1420958037, 1995217526, -896580150, -2111857278, -913751863, 2113570685, -84994566, 1504897881, 1200539975, -251982864, -1388188499, -726439980, -1570767454, -1354372433, -1675378788, -1538000988, 1927583346, -1063560256, -1217019209, -35578627, -1824674157, 642542118, 913070646, 1065238847, -134937865, -863809588, 879254580, -1521355611, -439274267, -235337487, 1910674289, -659852328, 828527409, 355090197, 67636228, -946515257, 591815971, -1013096765, 405809176, -1774739050, 84545285, -1708149350, 118360327, 304363026, -2145674368, -488686110, -338876693, 659450151, -1300247118, 1978310517, 152181513, -2095210877, 743994412, 439627290, 456535323, 1859957358, 1521806938, -1604584544, 1386542674, 997608763, -692624938, -1283600717, 693271337, -472039709, 794718511, -2079090812, 1403450707, -776378159, 0, -306107155, 541089824, -52224004, -1317418831, 1538714971, 1792327274, -879933749, -1100490306, 963791673, 1251270218, 1285084236, 1487988824, -813348145, -793023536, -272291089, -1437604438, -68348165, 1132905795, 1301993293, 0x33665533, -2062445435, 1166724933, -102166279, 33818114, 2147385727, 1352724560, 1014514748, -1624917345, -1471421528, 1369633617, -1554121053, 1082179648, -1895462257, -1841320558, -1658733411, 946882616, -168753931, -1134305348, -1233665610, -626035238, 557998881, 270544912, -1762561, -201519373, -759206446, -847164211, 202904588, 321271059, -322752532, 1606345055, -1758092649, 0x4488CC44, 388905239, -996976700, -1487539545, 2130477694, 1031423805, 1690872932, 1572530013, 422718233, 1944491379, 1623236704, -2129028991, 1335808335, -593264676, 0x22446622, 710180394, -1875137648, -2012511352, 1183631942, -288937490, -1200893000, 338181140, -559449634, 1589437022, 185998603, -609388837, -522503200, 845436466, 980700730, 169090570, 1234361161, 101452294, 608726052, 1555620956, -1029743166, -742560045, -1404833876, 1657054818, -1858492271, -1791908715, -455919644, 2045938553, -405458201, -930397240, 929978679, 1843050349, -1929278323, -709794603, 1318900302, -1454776151, 1826141292, 1454176854, -185399308, -355523094, 1707781989, 2062847610, -1371018834, 0x8101808, -1167075910, 2029029496, 625635109, 777810478, 473441308, -1504185946, -1267480652, -963161658, -389340184, -576619299, 1961401460, 524165407, 1268178251, -1117659971, -1962047861, -1978694262, 1893765232, 1048330814, -1250835275, 0x66CCAA66, 1217452104, 50726147, -151584266, 236720654, 1640145761, 896163637, 1471084887, -1184247623, -2045275770, -1046914879, 490350365, -1641563746, -505857823, -118811656, -1741966440, 0x11223311, 1775418217, -643206951, -1912108658, -1808554092, -1691502949, 507257374, -2028629369, -372694807, -829994546, 0x55AAFF55, 676362280, -542803233, -1945923700, -1587939167, -1995865975, 219813645, -1083843905, -422104602, 1115997762, 1758509160, 1099088705, -1725321063, 760903469, 253628687, -1334064208, 1420360788, -1150429509, 371997206};
    private static final int[] snow_T3 = new int[]{-962239645, -125535108, -291932297, -158499973, -15863054, -692229269, -558796945, -1856715323, 1615867952, 33751297, -827758745, 1451043627, -417726722, -1251813417, 1306962859, -325421450, -1891251510, 530416258, -1992242743, -91783811, -283772166, -1293199015, -1899411641, -83103504, 1106029997, -1285040940, 1610457762, 1173008303, 599760028, 1408738468, -459902350, -1688485696, 1975695287, -518193667, 1034851219, 1282024998, 1817851446, 2118205247, -184354825, -2091922228, 1750873140, 1374987685, -785062427, -116854287, -493653647, -1418471208, 1649619249, 708777237, 135005188, -1789737017, 1181033251, -1654733885, 807933976, 933336726, 168756485, 800430746, 235472647, 607523346, 463175808, -549592350, -853087253, 1315514151, 2144187058, -358648459, 303761673, 496927619, 1484008492, 875436570, 908925723, -592286098, -1259447718, 1543217312, -1527360942, 1984772923, -1218324778, 2110698419, 1383803177, -583080989, 1584475951, 328696964, -1493871789, -1184312879, 0, -1054020115, 1080041504, -484442884, 2043195825, -1225958565, -725718422, -1924740149, 1742323390, 1917532473, -1797371318, -1730917300, -1326950312, -2058694705, -1150562096, -987041809, 1340451498, -317260805, -2033892541, -1697166003, 0x66553333, 294946181, -1966127803, -384763399, 67502594, -25067649, -1594863536, 2017737788, 632987551, 1273211048, -1561112239, 1576969123, -2134884288, 92966799, 1068339858, 566009245, 1883781176, -251333131, 1675607228, 2009183926, -1351230758, 1113792801, 540020752, -451215361, -49351693, -1083321646, -2125673011, 403966988, 641012499, -1020269332, -1092526241, 899848087, -1999879100, 775493399, -1822964540, 1441965991, -58556802, 2051489085, -928226204, -1159242403, 841685273, -426413197, -1063231392, 0x19988181, -1630449841, -1551901476, 0x44662222, 1417554474, 1001099408, 0xB838888, -1932900794, -953553170, 1809037496, 675025940, -1485185314, -1126015394, 371002123, -1384719397, -616832800, 1683370546, 1951283770, 337512970, -1831122615, 201983494, 1215046692, -1192993700, -1621245246, -1116810285, 1139780780, -995728798, 967348625, 832869781, -751311644, -225740423, -718084121, -1958491960, 1851340599, -625513107, 25988493, -1318791723, -1663938994, 1239460265, -659264404, -1392880042, -217582348, -819598614, -894474907, -191989126, 1206496942, 0x10180808, 1876277946, -259491720, 1248797989, 1550986798, 941890588, 1475454630, 1942467764, -1756248378, -886839064, -1585652259, -392399756, 1042358047, -1763882165, 1641856445, 226921355, 260409994, -527404944, 2084716094, 1908716981, -861247898, -1864873912, 100991747, -150866186, 470945294, -1029480095, 1784624437, -1359390889, 1775286713, 395413126, -1722236479, 975641885, 666476190, -650583583, -351012616, 733190296, 0x22331111, -759469719, -1452221991, 126455438, 866620564, 766942107, 1008868894, 361924487, -920589847, -2025206066, -1426107051, 1350051880, -1518673953, 59739276, 1509466529, 0x9808989, 437718285, 1708834751, -684595482, -2067381694, -793221016, -2101132991, 699439513, 1517759789, 504434447, 2076946608, -1459858348, 0x6DD6BBBB, 742004246};

    public Snow2CryptoTransform(byte[] key, byte[] iv, boolean encrypting) {
        /* 11*/
        if (key.length != 16 && key.length != 32) {
            throw new IllegalArgumentException("Key size must be 16 or 32 bytes.");
        }
        /* 13*/
        if (iv != null && iv.length != 4) {
            throw new IllegalArgumentException("Iv size must be 4 bytes.");
        }
        /* 16*/
        this.encrypting = encrypting;
        /* 17*/
        this.keyStream = new int[16];
        /* 18*/
        this.LoadKey(key, iv);
        /* 19*/
        this.RefreshKeyStream();
        /* 20*/
        this.curIndex = 0;
    }

    public int TransformBlock(byte[] inputBuffer, int inputOffset, int inputCount, byte[] outputBuffer, int outputOffset) {
        /* 35*/
        this.ValidateTransformBlock(inputBuffer, inputOffset, inputCount);
        Objects.requireNonNull(this);
        int inputBlocks = inputCount / 4;
        Objects.requireNonNull(this);
        int rem = inputCount % 4;
        Objects.requireNonNull(this);
        int outputCount = inputBlocks * 4;
        /* 39*/
        if (inputBlocks == 0 || rem != 0) {
            throw new IllegalArgumentException("inputCount is out of range");
        }
        /* 42*/
        if (outputBuffer == null) {
            throw new NullPointerException("outputBuffer cannot be null");
        }
        /* 45*/
        if (outputCount > outputBuffer.length - outputOffset) {
            throw new IllegalArgumentException("outputBuffer is out of range");
        }
        AtomicInteger byteWritten = new AtomicInteger();
        this.TransformBlock(ByteBuffer.wrap(inputBuffer, inputOffset, inputCount - rem), ByteBuffer.wrap(outputBuffer, outputOffset, outputCount), byteWritten);
        /* 51*/
        return byteWritten.get();
    }

    public byte[] TransformFinalBlock(byte[] inputBuffer, int inputOffset, int inputCount) {
        /* 55*/
        this.ValidateTransformBlock(inputBuffer, inputOffset, inputCount);
        /* 56*/
        if (inputCount == 0) {
            /* 57*/
            return new byte[0];
        }
        Objects.requireNonNull(this);
        int inputBlocks = inputCount / 4;
        Objects.requireNonNull(this);
        int rem = inputCount % 4;
        Objects.requireNonNull(this);
        int outputCount = inputBlocks * 4;
        /* 63*/
        byte[] outputBuffer = new byte[outputCount];
        /* 64*/
        this.TransformBlock(ByteBuffer.wrap(inputBuffer, inputOffset, inputCount - rem), ByteBuffer.wrap(outputBuffer), new AtomicInteger());
        /* 65*/
        return outputBuffer;
    }

    private void ValidateTransformBlock(byte[] inputBuffer, int inputOffset, int inputCount) {
        /* 69*/
        if (inputBuffer == null) {
            throw new NullPointerException("Input buffer cannot be null.");
        }
        /* 72*/
        if (inputCount > inputBuffer.length) {
            throw new IllegalArgumentException("Invalid input count.");
        }
        /* 75*/
        if (inputOffset < 0) {
            throw new IllegalArgumentException("Invalid input offset.");
        }
        /* 78*/
        if (inputBuffer.length - inputCount < inputOffset) {
            throw new IllegalArgumentException("Offset and length out of bounds.");
        }
    }

    private void TransformBlock(ByteBuffer input, ByteBuffer output, AtomicInteger bytesWritten) {
        int i;
        /* 84*/
        IntBuffer inputBlocks = input.order(ByteOrder.LITTLE_ENDIAN).asReadOnlyBuffer().asIntBuffer();
        /* 85*/
        IntBuffer outputBlocks = output.order(ByteOrder.LITTLE_ENDIAN).asIntBuffer();
        /* 87*/
        for (i = 0; i < inputBlocks.limit(); ++i) {
            /* 88*/
            if (this.encrypting) {
                /* 89*/
                outputBlocks.put(i, inputBlocks.get(i) + this.keyStream[this.curIndex]);
            } else {
                /* 91*/
                outputBlocks.put(i, inputBlocks.get(i) - this.keyStream[this.curIndex]);
            }
            /* 94*/
            ++this.curIndex;
            /* 95*/
            if (this.curIndex < 16) continue;
            /* 96*/
            this.RefreshKeyStream();
            /* 97*/
            this.curIndex = 0;
        }
        /*100*/
        bytesWritten.set(i * 4);
    }

    private void LoadKey(byte[] key, byte[] iv) {
        /*104*/
        ByteBuffer keyBuffer = ByteBuffer.wrap(key).order(ByteOrder.LITTLE_ENDIAN);
        /*106*/
        if (key.length == 16) {
            /*107*/
            this.s15 = keyBuffer.getInt();
            /*108*/
            this.s14 = keyBuffer.getInt();
            /*109*/
            this.s13 = keyBuffer.getInt();
            /*110*/
            this.s12 = keyBuffer.getInt();
            /*111*/
            this.s11 = ~this.s15;
            /*112*/
            this.s10 = ~this.s14;
            /*113*/
            this.s9 = ~this.s13;
            /*114*/
            this.s8 = ~this.s12;
            /*115*/
            this.s7 = this.s15;
            /*116*/
            this.s6 = this.s14;
            /*117*/
            this.s5 = this.s13;
            /*118*/
            this.s4 = this.s12;
            /*119*/
            this.s3 = ~this.s15;
            /*120*/
            this.s2 = ~this.s14;
            /*121*/
            this.s1 = ~this.s13;
            /*122*/
            this.s0 = ~this.s12;
        } else {
            /*125*/
            this.s15 = keyBuffer.getInt();
            /*126*/
            this.s14 = keyBuffer.getInt();
            /*127*/
            this.s13 = keyBuffer.getInt();
            /*128*/
            this.s12 = keyBuffer.getInt();
            /*129*/
            this.s11 = keyBuffer.getInt();
            /*130*/
            this.s10 = keyBuffer.getInt();
            /*131*/
            this.s9 = keyBuffer.getInt();
            /*132*/
            this.s8 = keyBuffer.getInt();
            /*133*/
            this.s7 = ~this.s15;
            /*134*/
            this.s6 = ~this.s14;
            /*135*/
            this.s5 = ~this.s13;
            /*136*/
            this.s4 = ~this.s12;
            /*137*/
            this.s3 = ~this.s11;
            /*138*/
            this.s2 = ~this.s10;
            /*139*/
            this.s1 = ~this.s9;
            /*140*/
            this.s0 = ~this.s8;
        }
        /*144*/
        if (iv != null && iv.length > 0) {
            /*145*/
            this.s15 ^= iv[0] & 0xFF;
            /*146*/
            this.s12 ^= iv[1] & 0xFF;
            /*147*/
            this.s10 ^= iv[2] & 0xFF;
            /*148*/
            this.s9 ^= iv[3] & 0xFF;
        }
        /*151*/
        this.r1 = 0;
        /*152*/
        this.r2 = 0;
        /*154*/
        for (int i = 0; i < 2; ++i) {
            /*157*/
            int outfrom_fsm = this.r1 + this.s15 ^ this.r2;
            /*158*/
            this.s0 = Snow2CryptoTransform.a_mul(this.s0) ^ this.s2 ^ Snow2CryptoTransform.ainv_mul(this.s11) ^ outfrom_fsm;
            /*159*/
            int fsmtmp = this.r2 + this.s5;
            /*160*/
            this.r2 = snow_T0[Snow2CryptoTransform.$byte(0, this.r1)] ^ snow_T1[Snow2CryptoTransform.$byte(1, this.r1)] ^ snow_T2[Snow2CryptoTransform.$byte(2, this.r1)] ^ snow_T3[Snow2CryptoTransform.$byte(3, this.r1)];
            /*161*/
            this.r1 = fsmtmp;
            /*163*/
            outfrom_fsm = this.r1 + this.s0 ^ this.r2;
            /*164*/
            this.s1 = Snow2CryptoTransform.a_mul(this.s1) ^ this.s3 ^ Snow2CryptoTransform.ainv_mul(this.s12) ^ outfrom_fsm;
            /*165*/
            fsmtmp = this.r2 + this.s6;
            /*166*/
            this.r2 = snow_T0[Snow2CryptoTransform.$byte(0, this.r1)] ^ snow_T1[Snow2CryptoTransform.$byte(1, this.r1)] ^ snow_T2[Snow2CryptoTransform.$byte(2, this.r1)] ^ snow_T3[Snow2CryptoTransform.$byte(3, this.r1)];
            /*167*/
            this.r1 = fsmtmp;
            /*169*/
            outfrom_fsm = this.r1 + this.s1 ^ this.r2;
            /*170*/
            this.s2 = Snow2CryptoTransform.a_mul(this.s2) ^ this.s4 ^ Snow2CryptoTransform.ainv_mul(this.s13) ^ outfrom_fsm;
            /*171*/
            fsmtmp = this.r2 + this.s7;
            /*172*/
            this.r2 = snow_T0[Snow2CryptoTransform.$byte(0, this.r1)] ^ snow_T1[Snow2CryptoTransform.$byte(1, this.r1)] ^ snow_T2[Snow2CryptoTransform.$byte(2, this.r1)] ^ snow_T3[Snow2CryptoTransform.$byte(3, this.r1)];
            /*173*/
            this.r1 = fsmtmp;
            /*175*/
            outfrom_fsm = this.r1 + this.s2 ^ this.r2;
            /*176*/
            this.s3 = Snow2CryptoTransform.a_mul(this.s3) ^ this.s5 ^ Snow2CryptoTransform.ainv_mul(this.s14) ^ outfrom_fsm;
            /*177*/
            fsmtmp = this.r2 + this.s8;
            /*178*/
            this.r2 = snow_T0[Snow2CryptoTransform.$byte(0, this.r1)] ^ snow_T1[Snow2CryptoTransform.$byte(1, this.r1)] ^ snow_T2[Snow2CryptoTransform.$byte(2, this.r1)] ^ snow_T3[Snow2CryptoTransform.$byte(3, this.r1)];
            /*179*/
            this.r1 = fsmtmp;
            /*181*/
            outfrom_fsm = this.r1 + this.s3 ^ this.r2;
            /*182*/
            this.s4 = Snow2CryptoTransform.a_mul(this.s4) ^ this.s6 ^ Snow2CryptoTransform.ainv_mul(this.s15) ^ outfrom_fsm;
            /*183*/
            fsmtmp = this.r2 + this.s9;
            /*184*/
            this.r2 = snow_T0[Snow2CryptoTransform.$byte(0, this.r1)] ^ snow_T1[Snow2CryptoTransform.$byte(1, this.r1)] ^ snow_T2[Snow2CryptoTransform.$byte(2, this.r1)] ^ snow_T3[Snow2CryptoTransform.$byte(3, this.r1)];
            /*185*/
            this.r1 = fsmtmp;
            /*187*/
            outfrom_fsm = this.r1 + this.s4 ^ this.r2;
            /*188*/
            this.s5 = Snow2CryptoTransform.a_mul(this.s5) ^ this.s7 ^ Snow2CryptoTransform.ainv_mul(this.s0) ^ outfrom_fsm;
            /*189*/
            fsmtmp = this.r2 + this.s10;
            /*190*/
            this.r2 = snow_T0[Snow2CryptoTransform.$byte(0, this.r1)] ^ snow_T1[Snow2CryptoTransform.$byte(1, this.r1)] ^ snow_T2[Snow2CryptoTransform.$byte(2, this.r1)] ^ snow_T3[Snow2CryptoTransform.$byte(3, this.r1)];
            /*191*/
            this.r1 = fsmtmp;
            /*193*/
            outfrom_fsm = this.r1 + this.s5 ^ this.r2;
            /*194*/
            this.s6 = Snow2CryptoTransform.a_mul(this.s6) ^ this.s8 ^ Snow2CryptoTransform.ainv_mul(this.s1) ^ outfrom_fsm;
            /*195*/
            fsmtmp = this.r2 + this.s11;
            /*196*/
            this.r2 = snow_T0[Snow2CryptoTransform.$byte(0, this.r1)] ^ snow_T1[Snow2CryptoTransform.$byte(1, this.r1)] ^ snow_T2[Snow2CryptoTransform.$byte(2, this.r1)] ^ snow_T3[Snow2CryptoTransform.$byte(3, this.r1)];
            /*197*/
            this.r1 = fsmtmp;
            /*199*/
            outfrom_fsm = this.r1 + this.s6 ^ this.r2;
            /*200*/
            this.s7 = Snow2CryptoTransform.a_mul(this.s7) ^ this.s9 ^ Snow2CryptoTransform.ainv_mul(this.s2) ^ outfrom_fsm;
            /*201*/
            fsmtmp = this.r2 + this.s12;
            /*202*/
            this.r2 = snow_T0[Snow2CryptoTransform.$byte(0, this.r1)] ^ snow_T1[Snow2CryptoTransform.$byte(1, this.r1)] ^ snow_T2[Snow2CryptoTransform.$byte(2, this.r1)] ^ snow_T3[Snow2CryptoTransform.$byte(3, this.r1)];
            /*203*/
            this.r1 = fsmtmp;
            /*205*/
            outfrom_fsm = this.r1 + this.s7 ^ this.r2;
            /*206*/
            this.s8 = Snow2CryptoTransform.a_mul(this.s8) ^ this.s10 ^ Snow2CryptoTransform.ainv_mul(this.s3) ^ outfrom_fsm;
            /*207*/
            fsmtmp = this.r2 + this.s13;
            /*208*/
            this.r2 = snow_T0[Snow2CryptoTransform.$byte(0, this.r1)] ^ snow_T1[Snow2CryptoTransform.$byte(1, this.r1)] ^ snow_T2[Snow2CryptoTransform.$byte(2, this.r1)] ^ snow_T3[Snow2CryptoTransform.$byte(3, this.r1)];
            /*209*/
            this.r1 = fsmtmp;
            /*211*/
            outfrom_fsm = this.r1 + this.s8 ^ this.r2;
            /*212*/
            this.s9 = Snow2CryptoTransform.a_mul(this.s9) ^ this.s11 ^ Snow2CryptoTransform.ainv_mul(this.s4) ^ outfrom_fsm;
            /*213*/
            fsmtmp = this.r2 + this.s14;
            /*214*/
            this.r2 = snow_T0[Snow2CryptoTransform.$byte(0, this.r1)] ^ snow_T1[Snow2CryptoTransform.$byte(1, this.r1)] ^ snow_T2[Snow2CryptoTransform.$byte(2, this.r1)] ^ snow_T3[Snow2CryptoTransform.$byte(3, this.r1)];
            /*215*/
            this.r1 = fsmtmp;
            /*217*/
            outfrom_fsm = this.r1 + this.s9 ^ this.r2;
            /*218*/
            this.s10 = Snow2CryptoTransform.a_mul(this.s10) ^ this.s12 ^ Snow2CryptoTransform.ainv_mul(this.s5) ^ outfrom_fsm;
            /*219*/
            fsmtmp = this.r2 + this.s15;
            /*220*/
            this.r2 = snow_T0[Snow2CryptoTransform.$byte(0, this.r1)] ^ snow_T1[Snow2CryptoTransform.$byte(1, this.r1)] ^ snow_T2[Snow2CryptoTransform.$byte(2, this.r1)] ^ snow_T3[Snow2CryptoTransform.$byte(3, this.r1)];
            /*221*/
            this.r1 = fsmtmp;
            /*223*/
            outfrom_fsm = this.r1 + this.s10 ^ this.r2;
            /*224*/
            this.s11 = Snow2CryptoTransform.a_mul(this.s11) ^ this.s13 ^ Snow2CryptoTransform.ainv_mul(this.s6) ^ outfrom_fsm;
            /*225*/
            fsmtmp = this.r2 + this.s0;
            /*226*/
            this.r2 = snow_T0[Snow2CryptoTransform.$byte(0, this.r1)] ^ snow_T1[Snow2CryptoTransform.$byte(1, this.r1)] ^ snow_T2[Snow2CryptoTransform.$byte(2, this.r1)] ^ snow_T3[Snow2CryptoTransform.$byte(3, this.r1)];
            /*227*/
            this.r1 = fsmtmp;
            /*229*/
            outfrom_fsm = this.r1 + this.s11 ^ this.r2;
            /*230*/
            this.s12 = Snow2CryptoTransform.a_mul(this.s12) ^ this.s14 ^ Snow2CryptoTransform.ainv_mul(this.s7) ^ outfrom_fsm;
            /*231*/
            fsmtmp = this.r2 + this.s1;
            /*232*/
            this.r2 = snow_T0[Snow2CryptoTransform.$byte(0, this.r1)] ^ snow_T1[Snow2CryptoTransform.$byte(1, this.r1)] ^ snow_T2[Snow2CryptoTransform.$byte(2, this.r1)] ^ snow_T3[Snow2CryptoTransform.$byte(3, this.r1)];
            /*233*/
            this.r1 = fsmtmp;
            /*235*/
            outfrom_fsm = this.r1 + this.s12 ^ this.r2;
            /*236*/
            this.s13 = Snow2CryptoTransform.a_mul(this.s13) ^ this.s15 ^ Snow2CryptoTransform.ainv_mul(this.s8) ^ outfrom_fsm;
            /*237*/
            fsmtmp = this.r2 + this.s2;
            /*238*/
            this.r2 = snow_T0[Snow2CryptoTransform.$byte(0, this.r1)] ^ snow_T1[Snow2CryptoTransform.$byte(1, this.r1)] ^ snow_T2[Snow2CryptoTransform.$byte(2, this.r1)] ^ snow_T3[Snow2CryptoTransform.$byte(3, this.r1)];
            /*239*/
            this.r1 = fsmtmp;
            /*241*/
            outfrom_fsm = this.r1 + this.s13 ^ this.r2;
            /*242*/
            this.s14 = Snow2CryptoTransform.a_mul(this.s14) ^ this.s0 ^ Snow2CryptoTransform.ainv_mul(this.s9) ^ outfrom_fsm;
            /*243*/
            fsmtmp = this.r2 + this.s3;
            /*244*/
            this.r2 = snow_T0[Snow2CryptoTransform.$byte(0, this.r1)] ^ snow_T1[Snow2CryptoTransform.$byte(1, this.r1)] ^ snow_T2[Snow2CryptoTransform.$byte(2, this.r1)] ^ snow_T3[Snow2CryptoTransform.$byte(3, this.r1)];
            /*245*/
            this.r1 = fsmtmp;
            /*247*/
            outfrom_fsm = this.r1 + this.s14 ^ this.r2;
            /*248*/
            this.s15 = Snow2CryptoTransform.a_mul(this.s15) ^ this.s1 ^ Snow2CryptoTransform.ainv_mul(this.s10) ^ outfrom_fsm;
            /*249*/
            fsmtmp = this.r2 + this.s4;
            /*250*/
            this.r2 = snow_T0[Snow2CryptoTransform.$byte(0, this.r1)] ^ snow_T1[Snow2CryptoTransform.$byte(1, this.r1)] ^ snow_T2[Snow2CryptoTransform.$byte(2, this.r1)] ^ snow_T3[Snow2CryptoTransform.$byte(3, this.r1)];
            /*251*/
            this.r1 = fsmtmp;
        }
    }

    private void RefreshKeyStream() {
        /*258*/
        this.s0 = Snow2CryptoTransform.a_mul(this.s0) ^ this.s2 ^ Snow2CryptoTransform.ainv_mul(this.s11);
        /*259*/
        int fsmtmp = this.r2 + this.s5;
        /*260*/
        this.r2 = snow_T0[Snow2CryptoTransform.$byte(0, this.r1)] ^ snow_T1[Snow2CryptoTransform.$byte(1, this.r1)] ^ snow_T2[Snow2CryptoTransform.$byte(2, this.r1)] ^ snow_T3[Snow2CryptoTransform.$byte(3, this.r1)];
        /*261*/
        this.r1 = fsmtmp;
        /*262*/
        this.keyStream[0] = this.r1 + this.s0 ^ this.r2 ^ this.s1;
        /*264*/
        this.s1 = Snow2CryptoTransform.a_mul(this.s1) ^ this.s3 ^ Snow2CryptoTransform.ainv_mul(this.s12);
        /*265*/
        fsmtmp = this.r2 + this.s6;
        /*266*/
        this.r2 = snow_T0[Snow2CryptoTransform.$byte(0, this.r1)] ^ snow_T1[Snow2CryptoTransform.$byte(1, this.r1)] ^ snow_T2[Snow2CryptoTransform.$byte(2, this.r1)] ^ snow_T3[Snow2CryptoTransform.$byte(3, this.r1)];
        /*267*/
        this.r1 = fsmtmp;
        /*268*/
        this.keyStream[1] = this.r1 + this.s1 ^ this.r2 ^ this.s2;
        /*270*/
        this.s2 = Snow2CryptoTransform.a_mul(this.s2) ^ this.s4 ^ Snow2CryptoTransform.ainv_mul(this.s13);
        /*271*/
        fsmtmp = this.r2 + this.s7;
        /*272*/
        this.r2 = snow_T0[Snow2CryptoTransform.$byte(0, this.r1)] ^ snow_T1[Snow2CryptoTransform.$byte(1, this.r1)] ^ snow_T2[Snow2CryptoTransform.$byte(2, this.r1)] ^ snow_T3[Snow2CryptoTransform.$byte(3, this.r1)];
        /*273*/
        this.r1 = fsmtmp;
        /*274*/
        this.keyStream[2] = this.r1 + this.s2 ^ this.r2 ^ this.s3;
        /*276*/
        this.s3 = Snow2CryptoTransform.a_mul(this.s3) ^ this.s5 ^ Snow2CryptoTransform.ainv_mul(this.s14);
        /*277*/
        fsmtmp = this.r2 + this.s8;
        /*278*/
        this.r2 = snow_T0[Snow2CryptoTransform.$byte(0, this.r1)] ^ snow_T1[Snow2CryptoTransform.$byte(1, this.r1)] ^ snow_T2[Snow2CryptoTransform.$byte(2, this.r1)] ^ snow_T3[Snow2CryptoTransform.$byte(3, this.r1)];
        /*279*/
        this.r1 = fsmtmp;
        /*280*/
        this.keyStream[3] = this.r1 + this.s3 ^ this.r2 ^ this.s4;
        /*282*/
        this.s4 = Snow2CryptoTransform.a_mul(this.s4) ^ this.s6 ^ Snow2CryptoTransform.ainv_mul(this.s15);
        /*283*/
        fsmtmp = this.r2 + this.s9;
        /*284*/
        this.r2 = snow_T0[Snow2CryptoTransform.$byte(0, this.r1)] ^ snow_T1[Snow2CryptoTransform.$byte(1, this.r1)] ^ snow_T2[Snow2CryptoTransform.$byte(2, this.r1)] ^ snow_T3[Snow2CryptoTransform.$byte(3, this.r1)];
        /*285*/
        this.r1 = fsmtmp;
        /*286*/
        this.keyStream[4] = this.r1 + this.s4 ^ this.r2 ^ this.s5;
        /*288*/
        this.s5 = Snow2CryptoTransform.a_mul(this.s5) ^ this.s7 ^ Snow2CryptoTransform.ainv_mul(this.s0);
        /*289*/
        fsmtmp = this.r2 + this.s10;
        /*290*/
        this.r2 = snow_T0[Snow2CryptoTransform.$byte(0, this.r1)] ^ snow_T1[Snow2CryptoTransform.$byte(1, this.r1)] ^ snow_T2[Snow2CryptoTransform.$byte(2, this.r1)] ^ snow_T3[Snow2CryptoTransform.$byte(3, this.r1)];
        /*291*/
        this.r1 = fsmtmp;
        /*292*/
        this.keyStream[5] = this.r1 + this.s5 ^ this.r2 ^ this.s6;
        /*294*/
        this.s6 = Snow2CryptoTransform.a_mul(this.s6) ^ this.s8 ^ Snow2CryptoTransform.ainv_mul(this.s1);
        /*295*/
        fsmtmp = this.r2 + this.s11;
        /*296*/
        this.r2 = snow_T0[Snow2CryptoTransform.$byte(0, this.r1)] ^ snow_T1[Snow2CryptoTransform.$byte(1, this.r1)] ^ snow_T2[Snow2CryptoTransform.$byte(2, this.r1)] ^ snow_T3[Snow2CryptoTransform.$byte(3, this.r1)];
        /*297*/
        this.r1 = fsmtmp;
        /*298*/
        this.keyStream[6] = this.r1 + this.s6 ^ this.r2 ^ this.s7;
        /*300*/
        this.s7 = Snow2CryptoTransform.a_mul(this.s7) ^ this.s9 ^ Snow2CryptoTransform.ainv_mul(this.s2);
        /*301*/
        fsmtmp = this.r2 + this.s12;
        /*302*/
        this.r2 = snow_T0[Snow2CryptoTransform.$byte(0, this.r1)] ^ snow_T1[Snow2CryptoTransform.$byte(1, this.r1)] ^ snow_T2[Snow2CryptoTransform.$byte(2, this.r1)] ^ snow_T3[Snow2CryptoTransform.$byte(3, this.r1)];
        /*303*/
        this.r1 = fsmtmp;
        /*304*/
        this.keyStream[7] = this.r1 + this.s7 ^ this.r2 ^ this.s8;
        /*306*/
        this.s8 = Snow2CryptoTransform.a_mul(this.s8) ^ this.s10 ^ Snow2CryptoTransform.ainv_mul(this.s3);
        /*307*/
        fsmtmp = this.r2 + this.s13;
        /*308*/
        this.r2 = snow_T0[Snow2CryptoTransform.$byte(0, this.r1)] ^ snow_T1[Snow2CryptoTransform.$byte(1, this.r1)] ^ snow_T2[Snow2CryptoTransform.$byte(2, this.r1)] ^ snow_T3[Snow2CryptoTransform.$byte(3, this.r1)];
        /*309*/
        this.r1 = fsmtmp;
        /*310*/
        this.keyStream[8] = this.r1 + this.s8 ^ this.r2 ^ this.s9;
        /*312*/
        this.s9 = Snow2CryptoTransform.a_mul(this.s9) ^ this.s11 ^ Snow2CryptoTransform.ainv_mul(this.s4);
        /*313*/
        fsmtmp = this.r2 + this.s14;
        /*314*/
        this.r2 = snow_T0[Snow2CryptoTransform.$byte(0, this.r1)] ^ snow_T1[Snow2CryptoTransform.$byte(1, this.r1)] ^ snow_T2[Snow2CryptoTransform.$byte(2, this.r1)] ^ snow_T3[Snow2CryptoTransform.$byte(3, this.r1)];
        /*315*/
        this.r1 = fsmtmp;
        /*316*/
        this.keyStream[9] = this.r1 + this.s9 ^ this.r2 ^ this.s10;
        /*318*/
        this.s10 = Snow2CryptoTransform.a_mul(this.s10) ^ this.s12 ^ Snow2CryptoTransform.ainv_mul(this.s5);
        /*319*/
        fsmtmp = this.r2 + this.s15;
        /*320*/
        this.r2 = snow_T0[Snow2CryptoTransform.$byte(0, this.r1)] ^ snow_T1[Snow2CryptoTransform.$byte(1, this.r1)] ^ snow_T2[Snow2CryptoTransform.$byte(2, this.r1)] ^ snow_T3[Snow2CryptoTransform.$byte(3, this.r1)];
        /*321*/
        this.r1 = fsmtmp;
        /*322*/
        this.keyStream[10] = this.r1 + this.s10 ^ this.r2 ^ this.s11;
        /*324*/
        this.s11 = Snow2CryptoTransform.a_mul(this.s11) ^ this.s13 ^ Snow2CryptoTransform.ainv_mul(this.s6);
        /*325*/
        fsmtmp = this.r2 + this.s0;
        /*326*/
        this.r2 = snow_T0[Snow2CryptoTransform.$byte(0, this.r1)] ^ snow_T1[Snow2CryptoTransform.$byte(1, this.r1)] ^ snow_T2[Snow2CryptoTransform.$byte(2, this.r1)] ^ snow_T3[Snow2CryptoTransform.$byte(3, this.r1)];
        /*327*/
        this.r1 = fsmtmp;
        /*328*/
        this.keyStream[11] = this.r1 + this.s11 ^ this.r2 ^ this.s12;
        /*330*/
        this.s12 = Snow2CryptoTransform.a_mul(this.s12) ^ this.s14 ^ Snow2CryptoTransform.ainv_mul(this.s7);
        /*331*/
        fsmtmp = this.r2 + this.s1;
        /*332*/
        this.r2 = snow_T0[Snow2CryptoTransform.$byte(0, this.r1)] ^ snow_T1[Snow2CryptoTransform.$byte(1, this.r1)] ^ snow_T2[Snow2CryptoTransform.$byte(2, this.r1)] ^ snow_T3[Snow2CryptoTransform.$byte(3, this.r1)];
        /*333*/
        this.r1 = fsmtmp;
        /*334*/
        this.keyStream[12] = this.r1 + this.s12 ^ this.r2 ^ this.s13;
        /*336*/
        this.s13 = Snow2CryptoTransform.a_mul(this.s13) ^ this.s15 ^ Snow2CryptoTransform.ainv_mul(this.s8);
        /*337*/
        fsmtmp = this.r2 + this.s2;
        /*338*/
        this.r2 = snow_T0[Snow2CryptoTransform.$byte(0, this.r1)] ^ snow_T1[Snow2CryptoTransform.$byte(1, this.r1)] ^ snow_T2[Snow2CryptoTransform.$byte(2, this.r1)] ^ snow_T3[Snow2CryptoTransform.$byte(3, this.r1)];
        /*339*/
        this.r1 = fsmtmp;
        /*340*/
        this.keyStream[13] = this.r1 + this.s13 ^ this.r2 ^ this.s14;
        /*342*/
        this.s14 = Snow2CryptoTransform.a_mul(this.s14) ^ this.s0 ^ Snow2CryptoTransform.ainv_mul(this.s9);
        /*343*/
        fsmtmp = this.r2 + this.s3;
        /*344*/
        this.r2 = snow_T0[Snow2CryptoTransform.$byte(0, this.r1)] ^ snow_T1[Snow2CryptoTransform.$byte(1, this.r1)] ^ snow_T2[Snow2CryptoTransform.$byte(2, this.r1)] ^ snow_T3[Snow2CryptoTransform.$byte(3, this.r1)];
        /*345*/
        this.r1 = fsmtmp;
        /*346*/
        this.keyStream[14] = this.r1 + this.s14 ^ this.r2 ^ this.s15;
        /*348*/
        this.s15 = Snow2CryptoTransform.a_mul(this.s15) ^ this.s1 ^ Snow2CryptoTransform.ainv_mul(this.s10);
        /*349*/
        fsmtmp = this.r2 + this.s4;
        /*350*/
        this.r2 = snow_T0[Snow2CryptoTransform.$byte(0, this.r1)] ^ snow_T1[Snow2CryptoTransform.$byte(1, this.r1)] ^ snow_T2[Snow2CryptoTransform.$byte(2, this.r1)] ^ snow_T3[Snow2CryptoTransform.$byte(3, this.r1)];
        /*351*/
        this.r1 = fsmtmp;
        /*352*/
        this.keyStream[15] = this.r1 + this.s15 ^ this.r2 ^ this.s0;
    }

    private static int $byte(int n, int w) {
        /*356*/
        return w >>> n * 8 & 0xFF;
    }

    private static int ainv_mul(int w) {
        /*360*/
        return w >>> 8 ^ snow_alphainv_mul[w & 0xFF];
    }

    private static int a_mul(int w) {
        /*364*/
        return w << 8 ^ snow_alpha_mul[w >>> 24 & 0xFF];
    }
}

