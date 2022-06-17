package edu.aku.hassannaqvi.tpvicsshrucround2listing.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationRequest
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Looper
import android.provider.Settings
import android.telephony.TelephonyManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.common.ConnectionResult

import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.OnTokenCanceledListener
import com.google.android.gms.tasks.Task


fun isNetworkConnected(context: Context): Boolean {
    var result = false
    val connMgr =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val networkCapabilities = connMgr.activeNetwork ?: return false
        val networkInfo = connMgr.getNetworkCapabilities(networkCapabilities) ?: return false
        result = when {
            networkInfo.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            networkInfo.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            networkInfo.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    } else {
        connMgr.run {
            connMgr.activeNetworkInfo?.run {
                result = when (type) {
                    ConnectivityManager.TYPE_WIFI -> true
                    ConnectivityManager.TYPE_MOBILE -> true
                    ConnectivityManager.TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
    }

    return result
}

@SuppressLint("HardwareIds")
fun getIMEIInfo(context: Context): String {

    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        )
    } else {
        val mTelephony = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        mTelephony.imei
    }
}


fun getCurrentDeviceLocation(context: Context) : Task<Location> {

     var fusedLocationClient: FusedLocationProviderClient =
         LocationServices.getFusedLocationProviderClient(context)

    val cancellationToken : CancellationToken = object : CancellationToken(){
        override fun onCanceledRequested(onTokenCanceledListener: OnTokenCanceledListener): CancellationToken {
            return this
        }
        override fun isCancellationRequested(): Boolean {
            return false
        }
    }
    return fusedLocationClient.getCurrentLocation(LocationRequest.QUALITY_BALANCED_POWER_ACCURACY, cancellationToken)
}

fun getLocationUpdates(context: Context, locationRequest:com.google.android.gms.location.LocationRequest, locationCallback: LocationCallback){

    var fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)


    fusedLocationClient.requestLocationUpdates(locationRequest,
        locationCallback,
        Looper.getMainLooper())

}



fun checkPlayServices(context: Context): Boolean {
    val googleAPI = GoogleApiAvailability.getInstance()
    val result = googleAPI.isGooglePlayServicesAvailable(context)
    if (result != ConnectionResult.SUCCESS) {
        return false
    }
    return true
}



















