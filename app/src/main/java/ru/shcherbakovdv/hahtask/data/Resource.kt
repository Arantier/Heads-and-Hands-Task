package ru.shcherbakovdv.hahtask.data

import ru.shcherbakovdv.hahtask.data.Status.Companion.ERROR
import ru.shcherbakovdv.hahtask.data.Status.Companion.LOADING
import ru.shcherbakovdv.hahtask.data.Status.Companion.SUCCESS

data class Resource<out T>(val status: Status, val data: T?, val message: String?) {
    companion object {
        fun <T> success(data: T?) =
            Resource(SUCCESS, data, null)

        fun <T> error(msg: String, data: T?) =
            Resource(ERROR, data, msg)

        fun <T> loading(data: T?) =
            Resource(LOADING, data, null)
    }
}