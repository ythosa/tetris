package com.ythosa.tetris.models

import java.lang.IllegalArgumentException

class InvalidFrameNumberException(frameNumber: Int) :
    IllegalArgumentException("$frameNumber is invalid frame number")
