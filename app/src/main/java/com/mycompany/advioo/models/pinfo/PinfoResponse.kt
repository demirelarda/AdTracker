package com.mycompany.advioo.models.pinfo

data class PinfoResponse(
    val nredrate: List<Nredrate>,
    val phours: List<Phour>,
    val pinfos: List<Pinfo>
)