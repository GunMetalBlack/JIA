package org.gunmetalblack.rkis;

/** Original RKIS boss portraits, adapted from MIT-licensed commit 993972a. */
public final class BossArt {
    private BossArt() { }

    public static String[] forEncounter(Encounter encounter) {
        return switch (encounter) {
            case LEN -> LEN;
            case PAYWALL -> PAYWALL;
            case ARMSTRONG -> ARMSTRONG;
            default -> new String[0];
        };
    }

    private static final String[] LEN = {
        "xkkkkkkOOOOOOkxoc:;;,;:coxO00OOOOOOOOOOO",
        "kkkkkOOOOOOOl'..        ..;lx0000OOOOOOO",
        "kkkOOOOOOOkc.       ...     .lO00000OOOO",
        "kkkOOOOOOx;......''.......    ;k000000OO",
        "kkOOOOOO0l..:llodxxxxxxdolc:;..l0000000O",
        "kOOOOO000o.'lodkOO000000Okkxd;.o00000000",
        "OOOOO0000x,'ldxkO00000000OOxd;'dK0000000",
        "OOOOO0000d'':ldxkO0000000Okdo;,d0000000O",
        "OOOOO0000d,'...',:okOkl:;,',:cok000OOOOO",
        "OOOOOOOO0k::c;,;::lx0kl:::codoxOOOOOOOOO",
        "OOOOOOOOOkc;oxxxxookOkxkkOOOdldOOOOOOOkk",
        "OOOOOOOOOOo,:dxkdcokOxdxO0OxllkOOOOkkkkk",
        "OOkOOkkkkOkc':oxd:;clloxkkxocokOOkkkkkkk",
        "kkkkkkkxoc:'.':lccllloooooo:..,;cldxxkkk",
        "kkxol:,..    ..:llloooodo:,.      ...',:",
        ":,..         ...'codddoc,','            ",
        "             .''....;;;;;;               ",
        "             .,l;'...'',:c;.             ",
        "              'cc;;,',;cl:.              ",
        "              .;:::,',ldxo.              "
    };
    private static final String[] PAYWALL = {
        "XXXXXXXXXXOl;'.'',,;;;;;;:cxk0NNXXXXXXXX",
        "XXXXXXXX0o'..........'',,,;::cdKNXXXXXXX",
        "XXXXXXXXx;'.............',;;;;,oKXXXXXXX",
        "XXXXXXX0l'.,:cclc:cccc::cll:,,,l0XXXXXXX",
        "XXXXXXX0c.;odxxxxxxxxdddddo:,',oKXXXXXXX",
        "XXXXXXXKo,lddodddddddoooddoc,.,dXXXXXXXX",
        "XXXXXXXXxlddoolclolcccclolool;;dXXXXXXXX",
        "XXXXXXNXOdoooc::coo::::clloool:xXXXXXXXX",
        "XXXXXXNX0xddoooodkxolloodddoodoOXXXXXXXX",
        "XXXXXXNXKkooooloxkkdolcooollodkKXXXXXXXX",
        "XXXXXXNXKOocc:clccc:cc:::::cld0XXXXXXXXX",
        "XXXXXXNXKKdc:::::;;;;;::::::cx0KXXXXXXXX",
        "XXXXXXNXKK0xc:cc:;;;::c:;;;:oKk:oOXXXXXX",
        "XXXXXXNNKKKKOocccccccc::;;:dKXo..,lxO0KX",
        "XXXXXXNXKKKK0xoc::::::;:ldOX0c......';co",
        "XXXXXXNXKOxo;,dOo::clodk0XXO;...........",
        "XXXXXX0xl;'..,kXx:;lx0XXXNO;............",
        "XXXKkl;......c0kc;,,,l0NN0:.............",
        "Kkl:'........lKx:;;oxOKX0:..............",
        "k,...........oOo::l0NNN0:...............",
        "o...........,kxcccl0NNKc................",
        "c...........:dlllco0WKl................."
    };
    private static final String[] ARMSTRONG = {
        "MMMMMMMMMMMMXOdolcccccclldOXWMMMMMMMMMMM",
        "MMMMMMMMMMMMXOdolc::::clldOXWMMMMMMMMMMM",
        "MMMMMMMMMMNd,.            .'dXMMMMMMMMMM",
        "MMMMMMMMMXl.  ............  .cXMMMMMMMMM",
        "MMMMMMMMMk.  ..............  .xMMMMMMMMM",
        "MMMMMMMMWd....................oWMMMMMMMM",
        "MMMMMMMMWo....................lNMMMMMMMM",
        "MMMMMMMMWo....................lNMMMMMMMM",
        "MMMMMMMWKc................ ...:0WMMMMMMM",
        "MMMMMMMK:......................;0MMMMMMM",
        "MMMMMMMK:......................;0MMMMMMM",
        "MMMMMMMWd......................oNMMMMMMM",
        "MMMMMMMMKc....................:0MMMMMMMM",
        "MMMMMMMMXo...'................lXMMMMMMMM",
        "MMMMMMWOc......................cONMMMMMM",
        "MMMNKkl'.. .................. ..'ckKNMMM",
        "0koc,.....  ................  .....,:ok0",
        ",'......... ..............    ........',",
        ".......      .............       ......'",
        ".......      ............       .......'",
        "........     ........... ..     .....''.",
    };
}
