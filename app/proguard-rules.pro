# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in the SDK tools proguard-defaults.txt

# Firebase Realtime Database
-keepattributes Signature
-keepclassmembers class com.example.livetasknotes.** {
  *;
}
