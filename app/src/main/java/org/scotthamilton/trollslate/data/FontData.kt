package org.scotthamilton.trollslate.data

open class FontLine(val pc: Int)

class HLine(pc: Int) : FontLine(pc)

class VLine(pc: Int) : FontLine(pc)

class FontData {
    companion object {
        val linesMap =
            mapOf(
                'R' to VLine(100),
                'N' to VLine(100 - 22),
                'X' to VLine(75),
                'F' to VLine(66),
                'Q' to VLine(100 - 2 * 22),
                'V' to VLine(57),
                'A' to VLine(50),
                'W' to VLine(43),
                'E' to VLine(33),
                'K' to VLine(25),
                'L' to VLine(0),
                'H' to HLine(100),
                'C' to HLine(93),
                'P' to HLine(100 - 11),
                'B' to HLine(80),
                'U' to HLine(66),
                'M' to HLine(62),
                'J' to HLine(59),
                'I' to HLine(50),
                'S' to HLine(41),
                'T' to HLine(33),
                'G' to HLine(20),
                'O' to HLine(11),
                'D' to HLine(0),
            )
        val lettersCodonTable =
            mapOf(
                ' ' to "",
                'A' to "RMLDHRM",
                'B' to "LRMLDHNM",
                'C' to "(OA)DLHAB",
                'D' to "LHAPROADL",
                'E' to "RLMRLHR",
                'F' to "LMRLHR",
                'G' to "LNSQRNDLHNM",
                'H' to "RHSLHD",
                'I' to "AB(_C)H",
                'J' to "LSDNHQR",
                'K' to "RSLDHMRH",
                'L' to "RLH",
                'M' to "LHEMFHRD",
                'N' to "LHADRH",
                'O' to "LHRDL",
                'P' to "LHRML",
                'Q' to "NLHNDGQR",
                'R' to "LHRMLSRD",
                'S' to "(LO)DRILHRB",
                'T' to "AHLR",
                'U' to "LHDRH",
                'V' to "(HL)SEDFSRH",
                'W' to "(HL)DAHDRH",
                'X' to "LSAJRHJLHJASRD",
                'Y' to "RHJLH",
                'Z' to "RAHL",

                '1' to "AHKBL",
                '2' to "RLIRHL",
                '3' to "LRILRHL",
                '4' to "RHILH",
                '5' to "LRILHR",
                '6' to "(LH)DRML",
                '7' to "RIARHL",
                '8' to "LRHLIRLD",
                '9' to "RHLTR",
                '0' to "LRHLD(_TA)U",

                '?' to "WV(_TA)JRHLB",
                '!' to "WV(_TA)H",
                '-' to "(IL)R",
                '.' to "WV",
                '\'' to "(UK)AH",
                ',' to "KAT",
                '(' to "XAHX",
                '[' to "XAHX",
                ')' to "KAHK",
                ']' to "KAHK",
            )
    }
}
