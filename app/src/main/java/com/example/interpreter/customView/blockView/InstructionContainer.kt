package com.example.interpreter.customView.blockView

import java.util.concurrent.Executor

interface InstructionContainer {
    var executors: MutableList<Executor>
}