package ru.shcherbakovdv.hahtask

abstract class Status {
    abstract val value: Int

    companion object {
        val SUCCESS = object : Status() {
            override val value = 1
        }
        val ERROR = object : Status() {
            override val value = -1
        }
        val LOADING = object : Status() {
            override val value = 0
        }
    }
}