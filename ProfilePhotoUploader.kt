package com.example.e_comm.util

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata

/**
 * Uploads a profile photo to Firebase Storage (blob storage) and saves the
 * resulting download URL on the user's Firebase Auth profile.
 *
 * Storage path: profile_photos/{uid}/avatar.jpg  (one photo per user, overwritten on change)
 */
object ProfilePhotoUploader {

    private const val TAG = "ProfilePhotoUploader"

    fun upload(
        imageUri: Uri,
        onSuccess: (Uri) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            onError(IllegalStateException("No signed-in user"))
            return
        }

        val ref = FirebaseStorage.getInstance().reference
            .child("profile_photos/${user.uid}/avatar.jpg")

        val metadata = StorageMetadata.Builder()
            .setContentType("image/jpeg")
            .build()

        Log.d(TAG, "Uploading photo for user ${user.uid}")

        ref.putFile(imageUri, metadata)
            .continueWithTask { task ->
                if (!task.isSuccessful) {
                    throw task.exception ?: Exception("Upload failed")
                }
                ref.downloadUrl
            }
            .addOnSuccessListener { downloadUrl ->
                Log.d(TAG, "Upload complete: $downloadUrl")
                val update = UserProfileChangeRequest.Builder()
                    .setPhotoUri(downloadUrl)
                    .build()
                user.updateProfile(update)
                    .addOnSuccessListener { onSuccess(downloadUrl) }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "Saving photo URL to profile failed", e)
                        onError(e)
                    }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Photo upload failed", e)
                onError(e)
            }
    }
}