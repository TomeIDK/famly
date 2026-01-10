package com.tome.famly.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FieldValue
import com.tome.famly.MainActivity
import com.tome.famly.R
import com.tome.famly.data.CurrentUser
import com.tome.famly.data.FirestoreDB
import com.tome.famly.ui.navigation.Routes

@Composable
fun LoginScreen(navController: NavController) {
    CurrentUser.uid = null
    CurrentUser.currentFamily = null
    val context = LocalContext.current as MainActivity
    val auth = FirebaseAuth.getInstance()
    var user by remember { mutableStateOf(auth.currentUser) }

    // listen for auth changes
    DisposableEffect(auth) {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            user = firebaseAuth.currentUser
        }
        auth.addAuthStateListener(listener)
        onDispose { auth.removeAuthStateListener(listener) }
    }

    // launcher for Google sign-in
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data!!)
        try {
            val account = task.getResult(ApiException::class.java)
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            auth.signInWithCredential(credential)
        } catch (e: ApiException) {
            // handle error
        }
    }

    if (user == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.famly_logo_img),
                    contentDescription = "App Logo",
                    modifier = Modifier.size(128.dp)
                )

                Text(
                    text = "Famly",
                    style = MaterialTheme.typography.headlineMedium
                )
                Button(onClick = {
                    val intent = context.googleSignInClient.signInIntent
                    launcher.launch(intent)
                },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(text = "Sign in with Google")
                }
            }
        }

    } else {
        // signed in, update Firestore user
        val uid = user!!.uid
        CurrentUser.uid = uid
        val userDoc = FirestoreDB.db.collection("users").document(uid)
        LaunchedEffect(uid) {
            userDoc.get().addOnSuccessListener { doc ->
                if (!doc.exists()) {
                    userDoc.set(
                        mapOf(
                            "displayName" to user!!.displayName,
                            "email" to user!!.email,
                            "createdAt" to FieldValue.serverTimestamp()
                        )
                    )
                }
            }
        }

        // navigate to family entry screen
        navController.navigate(Routes.FamilyEntryScreen.name)
    }
}


