package com.example.dangerousmapv10

import androidx.lifecycle.ViewModel

class MainViewModel:ViewModel() {
    val visiblePermissionDialogQueue= mutableListOf<String>()
    fun dismissDialog(){
        visiblePermissionDialogQueue.removeFirst()

    }
    fun onPermissionResult(
        permission: String,
        isGranted: Boolean
    )
    {
        if (!isGranted && !visiblePermissionDialogQueue.contains(permission)){
            visiblePermissionDialogQueue.add(permission)
        }
    }
}