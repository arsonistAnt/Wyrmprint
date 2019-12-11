package com.example.wyrmprint.injection

import android.app.Activity
import androidx.fragment.app.Fragment
import com.example.wyrmprint.injection.component.ActivityComponent


/**
 * An interface of that provides the implementation of [ActivityComponent]
 */
interface InjectionProvider {
    val component: ActivityComponent
}

//Provide an extension property to access the component from MainActivity, in Fragments
val Fragment.injector
    get() = (requireActivity() as InjectionProvider).component

val Activity.injector
    get() = (this as InjectionProvider).component