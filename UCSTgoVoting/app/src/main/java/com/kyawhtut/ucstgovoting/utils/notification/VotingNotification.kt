package com.kyawhtut.ucstgovoting.utils.notification

import androidx.lifecycle.MutableLiveData
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import timber.log.Timber

class VotingNotification : FirebaseMessagingService() {

    companion object {
        @JvmField
        val notificationResponse: MutableLiveData<String> = MutableLiveData()
    }

    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)
        Timber.e("onMessageReceived => ${p0.from}")
        if (p0.data.isNotEmpty()) {
            Timber.e("onMessageReceived => ${p0.data}")
            if ((p0.data["type"] ?: "") == "voting") {
                notificationResponse.postValue(p0.data["message"] ?: "")
            }
        }

        if (p0.notification != null) {
            Timber.e("onMessageReceived => ${p0.notification?.body} ${p0.notification?.title} ${p0.notification?.imageUrl}")
        }
    }
}