package com.example.wyrmprint.data.model

/**
 * Wraps around the back-end request payload while keeping track of network state.
 */
class NetworkState<T>(
    val inProgress: Boolean,
    val success: Boolean,
    val err: Throwable?,
    val data: T?
) {

    companion object {

        /**
         * Produces a [NetworkState] object that has its current properties representing the in-progress state.
         * @return a [NetworkState] object
         */
        fun <T> inProgress(): NetworkState<T> {
            return NetworkState<T>(
                inProgress = true,
                success = false,
                err = null,
                data = null
            )
        }

        /**
         * Produces a [NetworkState] object that has its current properties representing the success state.
         *
         * @param data the payload to store into this network object.
         * @return a [NetworkState] object
         */
        fun <T> success(data: T?): NetworkState<T> {
            return NetworkState<T>(
                inProgress = false,
                success = true,
                err = null,
                data = data
            )
        }

        /**
         * Produces a [NetworkState] object that has its current properties representing the failed state.
         *
         * @param err a throwable to be passed.
         * @return a [NetworkState] object
         */
        fun <T> failure(err: Throwable): NetworkState<T> {
            return NetworkState<T>(false, success = false, err = err, data = null)
        }
    }

    /**
     * Returns true/false if the [NetworkState] represents an error.
     */
    fun hasError() = err != null
}

/**
 * Class that only holds the network status.
 *
 * See [NetworkState] for requests that have a data payload.
 */
class NetworkStatus(
    val inProgress: Boolean,
    val success: Boolean,
    val err: Throwable?
) {

    companion object {
        /**
         * Produces a [NetworkStatus] object that represents the in-progress state.
         *
         * @return a [NetworkStatus] object.
         */
        fun inProgress(): NetworkStatus {
            return NetworkStatus(
                inProgress = true,
                success = false,
                err = null
            )
        }

        /**
         * Produces a [NetworkStatus] object that represents the success state.
         *
         * @return a [NetworkStatus] object.
         */
        fun success(): NetworkStatus {
            return NetworkStatus(
                inProgress = false,
                success = true,
                err = null
            )
        }

        /**
         * Produces a [NetworkStatus] object that represents the failure state.
         *
         * @return a [NetworkStatus] object.
         */
        fun failure(err: Throwable): NetworkStatus {
            return NetworkStatus(false, success = false, err = err)
        }
    }

    /**
     * Returns true/false if the [NetworkState] represents an error.
     */
    fun hasError() = err != null
}