L200: CLS                     ; 00E0
      LD   I,  #22A           ; A22A
      LD   V0, #0C            ; 600C
      LD   V1, #08            ; 6108
      DRW  V0, V1, #F         ; D01F
      ADD  V0, #09            ; 7009
      LD   I,  #239           ; A239
      DRW  V0, V1, #F         ; D01F
      LD   I,  #248           ; A248
      ADD  V0, #08            ; 7008
      DRW  V0, V1, #F         ; D01F
      ADD  V0, #04            ; 7004
      LD   I,  #257           ; A257
      DRW  V0, V1, #F         ; D01F
      ADD  V0, #08            ; 7008
      LD   I,  #266           ; A266
      DRW  V0, V1, #F         ; D01F
      ADD  V0, #08            ; 7008
      LD   I,  #275           ; A275
      DRW  V0, V1, #F         ; D01F
L228: JP   L228               ; 1228
L22A: db #FF, #00, #FF, #00
L22E: db #3C, #00, #3C, #00
L232: db #3C, #00, #3C, #00
L236: db #FF, #00, #FF, #FF
L23A: db #00, #FF, #00, #38
L23E: db #00, #3F, #00, #3F
L242: db #00, #38, #00, #FF
L246: db #00, #FF, #80, #00
L24A: db #E0, #00, #E0, #00
L24E: db #80, #00, #80, #00
L252: db #E0, #00, #E0, #00
L256: db #80, #F8, #00, #FC
L25A: db #00, #3E, #00, #3F
L25E: db #00, #3B, #00, #39
L262: db #00, #F8, #00, #F8
L266: db #03, #00, #07, #00
L26A: db #0F, #00, #BF, #00
L26E: db #FB, #00, #F3, #00
L272: db #E3, #00, #43, #E0
L276: db #00, #E0, #00, #80
L27A: db #00, #80, #00, #80
L27E: db #00, #80, #00, #E0
L282: db #00, #E0