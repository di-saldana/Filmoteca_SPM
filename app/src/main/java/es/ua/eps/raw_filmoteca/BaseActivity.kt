package es.ua.eps.raw_filmoteca

import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity


//-------------------------------------
open class BaseActivity : AppCompatActivity() {
    //---------------------------------
    companion object {
        private const val TAG: String = "BaseActivity"
        private const val REQUEST_PERMISSION: Int = 1234 // mastermoviles
        //-----------------------------
        @SuppressLint("StaticFieldLeak")
        private var mCurrentActivity : Activity? = null
        val currentActivity: Activity?
            get() = mCurrentActivity
        //-----------------------------
        private val mCallbacks: HashMap<String, IPermissionCallback> = HashMap()

        // Activity counter
        //-----------------------------
        private var mActivityCounter = 0
    }

    //---------------------------------
    private var mActivityNumber = 0

    //---------------------------------

    //---------------------------------
    // Activity Life Cycle
    //---------------------------------

    //---------------------------------
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ++mActivityCounter
        mActivityNumber = mActivityCounter
        mCurrentActivity = this
        Log.d(TAG, String.format("onCreate %s (%d/%d)", javaClass.simpleName, mActivityNumber, mActivityCounter))
    }

    //---------------------------------
    override fun onStart() {
        super.onStart()
        mCurrentActivity = this
        Log.d(TAG, String.format("onStart %s (%d)", javaClass.simpleName, mActivityNumber))
    }

    //---------------------------------
    override fun onResume() {
        super.onResume()
        mCurrentActivity = this
        Log.d(TAG, String.format("onResume %s (%d)", javaClass.simpleName, mActivityNumber))
    }

    //---------------------------------
    override fun onPause() {
        if(mCurrentActivity == this)
            mCurrentActivity = null
        Log.d(TAG, String.format("onPause %s (%d)", javaClass.simpleName, mActivityNumber))
        super.onPause()
    }

    //---------------------------------
    override fun onStop() {
        if(mCurrentActivity == this)
            mCurrentActivity = null
        Log.d(TAG, String.format("onStop %s (%d)", javaClass.simpleName, mActivityNumber))
        super.onStop()
    }

    //---------------------------------
    override fun onRestart() {
        super.onRestart()
        Log.d(TAG, String.format("onRestart %s (%d)", javaClass.simpleName, mActivityNumber))
    }

    //---------------------------------
    override fun onDestroy() {
        if(mCurrentActivity == this)
            mCurrentActivity = null
        Log.d(TAG, String.format("onDestroy %s (%d/%d)", javaClass.simpleName, mActivityNumber, mActivityCounter))
        --mActivityCounter
        super.onDestroy()
    }

    //---------------------------------
    // Permissions
    //---------------------------------

    //---------------------------------
    interface IPermissionCallback {
        fun onPermissionsResult(permission: String?)
    }

    //---------------------------------
    fun hasPermission(permission: String): Boolean {
        if (Build.VERSION.SDK_INT >= 23) { // M
            return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED)
        }

        return  true
    }

    //---------------------------------
    fun checkPermission(permission: String, callback: IPermissionCallback? = null, callOnGranted: Boolean = true): Boolean {
        if (Build.VERSION.SDK_INT >= 23) { // M
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                if(callback != null) {
                    mCallbacks[permission] = callback
                }
                requestPermissions(arrayOf(permission), REQUEST_PERMISSION)
                return false
            }
        }

        if (callOnGranted) {
            callback?.onPermissionsResult(permission)
        }

        return true
    }

    //---------------------------------
    fun checkPermission(permission: String, callback: (permission: String?) -> Unit, callOnGranted: Boolean = true): Boolean {
        return checkPermission(permission,
            object : IPermissionCallback {
                override fun onPermissionsResult(permission: String?) {
                    callback(permission)
                }
            },
            callOnGranted
        )
    }

    //---------------------------------
    fun checkPermissions(permissions: Array<String>, callback: IPermissionCallback? = null, callOnGranted: Boolean = true): Boolean {
        val toCheck = ArrayList<String>()
        if (Build.VERSION.SDK_INT >= 23) { // M
            for (permission in permissions) {
                if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                    if(callback != null) {
                        mCallbacks[permission] = callback
                    }
                    toCheck.add(permission)
                }
                else if (callOnGranted) {
                    callback?.onPermissionsResult(permission)
                }
            }

            if (toCheck.isNotEmpty()) {
                requestPermissions(toCheck.toArray(arrayOfNulls(toCheck.size)), REQUEST_PERMISSION)
                return false
            }
        }
        return true
    }

    //---------------------------------
    // Request Permissions
    //---------------------------------

    //---------------------------------
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        for (index in permissions.indices) {
            if (index < grantResults.size  && grantResults[index] == PackageManager.PERMISSION_GRANTED) {
                val perm = permissions[index]
                if(mCallbacks.containsKey(perm)) {
                    val callback = mCallbacks[perm]
                    callback?.onPermissionsResult(perm)
                }
            }
        }
    }

}

/*

boolean checkGooglePlayServices(Context context) {
    GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
    int result = googleAPI.isGooglePlayServicesAvailable(context);
    if(result != ConnectionResult.SUCCESS) {
        Log.d("GPS", "Google Play Services not available");
        if(googleAPI.isUserResolvableError(result)) {
            googleAPI.getErrorDialog(this, result, PLAY_SERVICES_RESOLUTION_REQUEST).show();
        }
        else {
            Log.d("GPS", "This device is not supported");
        }
        return false;
    }
    return true;
}

*/


/*

FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);
// Get the last known location client.getLastLocation()
                                     .addOnCompleteListener(this, new OnCompleteListener<Location>() {
                                         @Override
                                         public void onComplete(@NonNull Task<Location> task) {
                                            // ...
                                         }
                                     });

*/


/*

// This account must have the necessary scopes to make the API call
// See https://developers.google.com/identity/sign-in/android/
GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

// Get the app's Drive folder
DriveResourceClient client = Drive.getDriveResourceClient(this, account);
client.getAppFolder().
      .addOnCompleteListener(this, new OnCompleteListener<DriveFolder>() {
        @Override
        public void onComplete(@NonNull Task<DriveFolder>() {
            // ...
        }
      });

*/
