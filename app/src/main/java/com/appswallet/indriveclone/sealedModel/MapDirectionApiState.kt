package com.appswallet.indriveclone.sealedModel



sealed class MapDirectionApiState {

    class Success(val points: String): MapDirectionApiState()
    class Failed(val msg: String): MapDirectionApiState()
}