package com.neofoc.app.drivers.yumizenP8000;

import com.neofoc.app.drivers.astm.AstmFrame;

public interface YumizenP8000Const {
//	public static final char VT = (char) 0x0B;
//	public static final char FS = (char) 0x1C;

    public static final String Types[] = {
            //"VT",
            "MSH",
            "MSA",
            "PID",
            "PV1",
            "ORC",
            "TQ1",
            "OBR",
            "SPM",
            "OBX",
            "NTE",
            String.valueOf(AstmFrame.FS)};

    //	public static final int TYPE_VT  = 0;
    public static final int TYPE_MSH = 0;
    public static final int TYPE_MSA = 1;
    public static final int TYPE_PID = 2;
    public static final int TYPE_PV1 = 3;
    public static final int TYPE_ORC = 4;
    public static final int TYPE_TQ1 = 5;
    public static final int TYPE_OBR = 6;
    public static final int TYPE_SPM = 7;
    public static final int TYPE_OBX = 8;
    public static final int TYPE_NTE = 9;
    public static final int TYPE_FS = 10;
}
