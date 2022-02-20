package org.scotthamilton.trollslate.data

open class FontLine(val pc: Int)
class HLine(pc: Int) : FontLine(pc)
class VLine(pc: Int) : FontLine(pc)


class FontData  {
    companion object {
        val linesMap = mapOf(
            'R' to VLine(100),
            'N' to VLine(100-22),
            'F' to VLine(66),
            'Q' to VLine(100-2*22),
            'A' to VLine(50),
            'E' to VLine(33),
            'L' to VLine(0),

            'H' to HLine(100),
            'C' to HLine(93),
            'P' to HLine(100 - 11),
            'B' to HLine(80),
            'M' to HLine(62),
            'J' to HLine(59),
            'I' to HLine(50),
            'S' to HLine(41),
            'O' to HLine(11),
            'D' to HLine(0),
            'G' to HLine(20)
        )
        val lettersCodonTable = mapOf(
            'A' to "RMLDHRM",
            'B' to "LRMLDHNM",
            'C' to "RLHR",
            'D' to "LH(PR)(OR)(DL)",
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
            'S' to "LRILHR",
            'T' to "AHLR",
            'U' to "LHDRH",
            'V' to "(HL)SEDFSRH",
            'W' to "(HL)DAHDRH",
            'X' to "LSAJRHJLHJASRD",
            'Y' to "RHJLH",
            'Z' to "RAHL"
        )
    }
}
