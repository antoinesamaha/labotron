package com.neofoc.app;

public interface Constants {

    char ENQ = 5;
    char ACK = 6;
    char NACK = 21; // 15 pour Pentra (project astm)
    char EOT = 4;
    char SINGLE_CHAR_NOT_FOUND = 0;

    char STX = 2;
    char ETB = 23;
    char ETX = 3;
    char CR = 13;
    char LF = 10;
//    char VT = ASCII.VT;
//    char FS = ASCII.FS;

    char FRAME_TYPE_HEADER = 'H';
    char FRAME_TYPE_PATIENT = 'P';
    char FRAME_TYPE_ORDER = 'O';
    char FRAME_TYPE_INFORMATION_INQUIRY = 'Q';
    char FRAME_TYPE_RESULT = 'R';
    char FRAME_TYPE_LAST = 'L';
    char FRAME_TYPE_COMMENT = 'C';
    char FRAME_TYPE_NONE = '-';
    // char FRAME_TYPE_M = 'M';

    char FRAME_TYPE_ENQ = 'e';
    char FRAME_TYPE_ACK = 'a';
    char FRAME_TYPE_NACK = 'n';
    char FRAME_TYPE_EOT = 't';
    char FRAME_TYPE_CONTINUITY = 'X';

    char FIELD_SEPERATOR = '|';
    char COMPONENT_DELIMITER = '^';
    char ESCAPE_DELIMITER = '&';
    char REPEAT_DELIMITER = '\\';// 92
    char REPEAT_SUB_FIELD_DELIMITER = '~';

    // ASTM test data array
    String[] ASTM_TEST_DATA = {
        "1H|\\^&|6744636705355103||PSM^Roche Diagnostics^PSM^2.03.01b|||||||P||20250829101145",
        "2P|1||701641|1|||G",
        //"P|1||701641",
        //"|1|||G",
        "3O|1|5565802|||||||||X||||SERUM||||||||F",
        "4R|1|^^^627|1.740|||||F||bmserv^~SYSValDaemon~||20250829101112|452.1",
        "5O|2|5565802|||||||||X||||SERUM||||||||F",
        "6R|2|^^^587|23.00|||||F||bmserv^~SYSValDaemon~||20250829101112|452.1",
        "7O|3|5565802|||||||||X||||SERUM||||||||F",
        "0R|3|^^^685|26.00|||||F||bmserv^~SYSValDaemon~||20250829101112|452.1",
//        "1O|4|5565802|||||||||X||||SERUM||||||||F",
//        "2R|4|^^^679|7.50|||||F||bmserv^~SYSValDaemon~||20250829101112|452.1",
//        "3O|5|5565802|||||||||X||||SERUM||||||||F",
//        "4R|5|^^^781|193.00|||||F||bmserv^~SYSValDaemon~||20250829101112|452.1",
//        "5O|6|5565802|||||||||X||||SERUM||||||||F",
//        "6R|6|^^^700|4.30|||||F||bmserv^~SYSValDaemon~||20250829101112|452.1",
//        "7O|7|5565802|||||||||X||||SERUM||||||||F",
//        "0R|7|^^^419|69.00|||||F||bmserv^~SYSValDaemon~||20250829101111|452.1",
//        "1O|8|5565802|||||||||X||||SERUM||||||||F",
//        "2R|8|^^^688|2.10|||||F||bmserv^~SYSValDaemon~||20250829101112|452.1",
//        "3O|9|5565802|||||||||X||||SERUM||||||||F",
//        "4R|9|^^^675|3.80|||||F||bmserv^~SYSValDaemon~||20250829101112|452.1",
//        "5O|10|5565802|||||||||X||||SERUM||||||||F",
//        "6R|10|^^^L|10.00|||||F||bmserv^~SYSValDaemon~||20250829101112|452.1",
//        "7O|11|5565802|||||||||X||||SERUM||||||||F",
//        "0R|11|^^^80|192.00|||||F||bmserv^~SYSValDaemon~||20250829101111|452.1",
//        "1O|12|5565802|||||||||X||||SERUM||||||||F",
//        "2R|12|^^^961|140.00|||||F||bmserv^~SYSValDaemon~||20250829101112|452.1",
//        "3O|13|5565802|||||||||X||||SERUM||||||||F",
//        "4R|13|^^^480|34.00|||||F||bmserv^~SYSValDaemon~||20250829101111|452.1",
//        "5O|14|5565802|||||||||X||||SERUM||||||||F",
//        "6R|14|^^^668|121.00|||||F||bmserv^~SYSValDaemon~||20250829101112|452.1",
//        "7O|15|5565802|||||||||X||||SERUM||||||||F",
//        "0R|15|^^^H|5.00|||||F||bmserv^~SYSValDaemon~||20250829101112|452.1",
//        "1O|16|5565802|||||||||X||||SERUM||||||||F",
//        "2R|16|^^^435|36.00|||||F||bmserv^~SYSValDaemon~||20250829101111|452.1",
//        "3O|17|5565802|||||||||X||||SERUM||||||||F",
//        "4R|17|^^^I|1.00|||||F||bmserv^~SYSValDaemon~||20250829101112|452.1",
//        "5O|18|5565802|||||||||X||||SERUM||||||||F",
//        "6R|18|^^^798|215.00|||||F||bmserv^~SYSValDaemon~||20250829101112|452.1",
//        "7O|19|5565802|||||||||X||||SERUM||||||||F",
//        "0R|19|^^^452|1.37|||||F||bmserv^~SYSValDaemon~||20250829101111|452.1",
//        "1O|20|5565802|||||||||X||||SERUM||||||||F",
//        "2R|20|^^^256|0.17|||||F||bmserv^~SYSValDaemon~||20250829101111|452.1",
//        "3O|21|5565802|||||||||X||||SERUM||||||||F",
//        "4R|21|^^^413|4.80|||||F||bmserv^~SYSValDaemon~||20250829101111|452.1",
//        "5O|22|5565802|||||||||X||||SERUM||||||||F",
//        "6R|22|^^^158|58.00|||||F||bmserv^~SYSValDaemon~||20250829101111|452.1",
//        "7O|23|5565802|||||||||X||||SERUM||||||||F",
//        "0R|23|^^^699|9.70|||||F||bmserv^~SYSValDaemon~||20250829101112|452.1",
//        "1O|24|5565802|||||||||X||||SERUM||||||||F",
//        "2R|24|^^^800|24.67|||||F||bmserv^~SYSValDaemon~||20250829101112|452.1",
//        "3O|25|5565802|||||||||X||||SERUM||||||||F",
//        "4R|25|^^^962|1.8|||||F||^~SYSValDaemon~||20250829101113|Manual",
//        "5O|26|5565802|||||||||X||||SERUM||||||||F",
//        "6R|26|^^^1001|61|||||F||^~SYSValDaemon~||20250829101119|Manual",
//        "7O|27|5565802|||||||||X||||SERUM||||||||F",
//        "0R|27|^^^499|4.060|||||F||bmserv^~SYSValDaemon~||20250829101112|452.1",
        "1L|1|N"
    };

}
