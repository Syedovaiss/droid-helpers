package com.ovais.odroidhelper.extensions

import android.app.Activity
import android.content.*
import android.content.ClipboardManager
import android.graphics.Paint
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Patterns
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ovais.odroidhelper.R
import com.ovais.odroidhelper.helpers.ODroidHelper
import org.greenrobot.eventbus.EventBus
import timber.log.Timber
import java.lang.reflect.Type
import java.util.*
import kotlin.collections.ArrayList


fun ContentResolver.getFileName(uri: Uri): String {
    var name = ""
    val returnCursor = this.query(uri, null, null, null, null)
    if (returnCursor != null) {
        val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        returnCursor.moveToFirst()
        name = returnCursor.getString(nameIndex)
        returnCursor.close()
    }
    return name.lowercase().replace(" ", "_")
}

fun String?.addDotSeparator(): String {
    return "• $this"
}

fun Context.copyToClipBoard(text: String, message: String? = null) {
    val clipboard: ClipboardManager =
        getSystemService(AppCompatActivity.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText(getString(R.string.copied_text), text)
    clipboard.setPrimaryClip(clip)
    message?.let {
        Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
    }
}


fun Fragment.showToast(message: String) {
    Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()

}

fun Activity.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}


fun View.hide() {
    this.visibility = View.GONE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}

fun View.show() {
    this.visibility = View.VISIBLE
}

fun ViewGroup.show() {
    this.visibility = View.VISIBLE
}

fun ViewGroup.hide() {
    this.visibility = View.GONE
}

fun ViewGroup.invisible() {
    this.visibility = View.INVISIBLE
}

fun View.enable() {
    this.isEnabled = true
}

fun View.disable() {
    this.isEnabled = false
}

fun ProgressBar.showProgressLoader(progressValue: Int, maxValue: Int) {
    this.max = maxValue
    this.progress = progressValue
    if (this.progress == this.max) {
        this.hide()
    }
}


fun ImageView.makeClickable() {
    this.isClickable = true
}

fun View.makeClickable() {
    this.isClickable = true
}

fun View.makeNonClickable() {
    this.isClickable = false
}


@Suppress("DEPRECATION")
fun TextView.makeNonFocusableTextColor(isClickable: Boolean = false) {
    if (isClickable) {
        this.makeClickable()
    } else {
        this.makeNonClickable()
    }
    this.setTextColor(resources.getColor(R.color.colorBlueGrayTransparent))
}

@Suppress("DEPRECATION")
fun TextView.makeFocusableText(
    isClickable: Boolean = true,
    @ColorRes colorRes: Int = R.color.black
) {
    if (isClickable) {
        this.makeClickable()
    } else {
        this.makeNonClickable()
    }
    this.setTextColor(resources.getColor(colorRes))
}


fun Fragment.routeTo(actionId: Int) {
    findNavController().navigate(actionId)
}

fun Fragment.routeTo(actionId: Int, bundle: Bundle) {
    findNavController().navigate(actionId, bundle)
}


fun EditText.value(): String {
    return this.text.toString()
}

fun TextView.value(data: Any) {
    this.text = data.toString()
}

fun TextView.value(data: Any, strikeThrough: Boolean = false) {
    this.text = data.toString()
    if (strikeThrough) {
        this.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
    }
}

fun TextView.value(): String {
    return this.text.toString()
}

fun EditText.value(data: Any) {
    this.setText(data.toString())
}

fun EditText.clear() {
    this.setText("")
}

fun MaterialButton.value(text: Any) {
    this.text = text.toString()
}

fun MaterialButton.value(): String {
    return this.text.toString()
}

fun ImageView.applyImage(
    context: Context,
    url: String,
    placeHolder: Int = R.drawable.ic_placeholder,
    baseImageURL: String = ""
) {
    Glide.with(context)
        .load(baseImageURL + url)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .placeholder(placeHolder)
        .into(this)
}

fun ImageView.applyCircularImage(
    context: Context,
    url: String,
    placeHolder: Int = R.drawable.ic_placeholder,
    baseImageURL: String = ""
) {
    Glide.with(context)
        .load(baseImageURL + url)
        .circleCrop()
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .placeholder(placeHolder)
        .into(this)
}

fun ImageView.applyResource(@DrawableRes drawable: Int) {
    this.setImageResource(drawable)
}

fun Double.isNotZero(): Boolean {
    return this != 0.0
}

fun Double.isZero(): Boolean {
    return this == 0.0
}

fun RecyclerView.init(
    context: Context,
    recyclerAdapter: RecyclerView.Adapter<*>,
    itemDecorator: RecyclerView.ItemDecoration? = null,
    isHorizontal: Boolean = false,
    fixedSize: Boolean = false
) {
    this.apply {
        layoutManager = if (isHorizontal) {
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        } else {
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }
        itemDecorator?.let {
            addItemDecoration(it)
        }
        adapter = recyclerAdapter
        this.setHasFixedSize(fixedSize)

    }
}

fun RecyclerView.initGrids(
    context: Context,
    recyclerAdapter: RecyclerView.Adapter<*>,
    itemDecorator: RecyclerView.ItemDecoration? = null,
    spanCount: Int = 2,
    fixedSize: Boolean = false
) {
    this.apply {
        layoutManager = GridLayoutManager(context, spanCount)
        this.setHasFixedSize(fixedSize)
        itemDecorator?.let {
            addItemDecoration(it)
        }
        adapter = recyclerAdapter

    }
}


fun Fragment.finish() {
    requireActivity().finish()
}

fun Fragment.getPackageName(): String = requireActivity().packageName

fun <T> Fragment.routeToActivity(clazz: Class<T>, isFlags: Boolean) {
    Intent(requireContext(), clazz).apply {
        if (isFlags) flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(this)
        finish()
    }
}

fun <T> Fragment.routeToActivityDontFinish(clazz: Class<T>) {
    Intent(requireContext(), clazz).apply {
        startActivity(this)
    }
}

fun <T> Activity.routeTo(clazz: Class<T>, shouldFinish: Boolean = false) {
    Intent(this, clazz).apply {
        startActivity(this)
        if (shouldFinish) {
            finish()
        }
    }
}

fun Fragment.tryCatch(trying: () -> Unit, catching: () -> Unit, shouldFinish: Boolean = false) {
    try {
        trying()
    } catch (e: Exception) {
        Timber.e(e)
        catching()
    }
    if (shouldFinish) {
        finish()
    }
}

fun Activity.tryCatch(trying: () -> Unit, catching: () -> Unit, shouldFinish: Boolean = false) {
    try {
        trying()
    } catch (e: Exception) {
        Timber.e(e)
        catching()
    }
    if (shouldFinish) {
        finish()
    }
}

fun View.listen(run: () -> Unit) {
    this.setOnClickListener {
        run()
    }
}

fun Fragment.showSnackBar(
    msg: String,
    length: Int,
    actionMessage: CharSequence?,
    action: (View) -> Unit
) {
    val snackBar = Snackbar.make(requireView(), msg, length)
    if (actionMessage != null) {
        snackBar.setAction(actionMessage) {
            action(requireView())
        }.show()
    } else {
        snackBar.show()
    }
}

fun EditText.watch(onChange: () -> Unit?, afterChange: () -> Unit?) {
    this.addTextChangedListener(object : TextWatcher {

        override fun afterTextChanged(s: Editable?) {
            afterChange()
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            onChange()
        }
    })
}

fun TextInputLayout.clear() {
    this.error = ""
}

fun TextInputLayout.error(message: String) {
    this.error = message
}

fun TextInputLayout.setResError(resError: Int?) {
    resError?.let {
        error = context.resources.getString(it)
    } ?: setError(null)
}

fun EditText.error(message: String) {
    this.error = message
}

fun Fragment.hideKeyboard() {
    tryCatch({
        val inputManager =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val view = requireActivity().currentFocus
        if (view != null) {
            inputManager.hideSoftInputFromWindow(
                view.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }, {})
}

fun Activity.hideKeyboard() {
    tryCatch({
        val inputManager = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val view = currentFocus
        if (view != null) {
            inputManager.hideSoftInputFromWindow(
                view.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }, {})
}

fun String.compare(value: String): Boolean = this == value

fun String.isDifferent(value: String) = this != value

fun Double.isDifferent(value: Double) = this != value

fun Double.isLessThan(value: Double) = this < value

fun Int.isZero() = this == 0
fun Int.isNotZero(): Boolean = this > 0 || this < 0
fun Int.isLessThanOne() = this < 1
fun Int.isOne() = this == 1
fun Long.isZero() = this == 0L
fun Long.isNotZero() = this > 0L || this < 0L
fun Int?.isNotNull() = this != null
fun Int.isGreaterThan(value: Int): Boolean = this > value


fun locateView(v: View): Rect {
    val locateInt = IntArray(2)
    try {
        v.getLocationOnScreen(locateInt)
    } catch (npe: NullPointerException) {
        Timber.e(npe)
    }
    val location = Rect()
    location.left = locateInt[0]
    location.top = locateInt[1]
    location.right = location.left + v.width
    location.bottom = location.top + v.height
    return location
}

fun TextView.pressedWhenTrue(predicate: Boolean) {
    if (predicate) {
        this.isPressed = predicate
    }
}

fun Double.isMinusOne() = this == -1.0

fun Double.isOne() = this == 1.0

//Not work with coroutine flow because when loading state emits observer will be removed
fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
    observe(lifecycleOwner, object : Observer<T> {
        override fun onChanged(t: T?) {
            observer.onChanged(t)
            removeObserver(this)
        }
    })
}


fun TextView.setColor(context: Context, @ColorRes color: Int) {
    this.setTextColor(ContextCompat.getColor(context, color))
}

fun TabLayout.selectTab(index: Int) {
    this.getTabAt(index)?.select()
}

fun View.enableWhenTrue(predicate: Boolean) {
    this.isEnabled = predicate
}

inline fun <reified T> String.toObject(): T {
    val gson = Gson()
    return gson.fromJson(this, T::class.java)
}

fun Any.toJson(): String? {
    val type: Type = object : TypeToken<Any>() {}.type
    val gson = Gson()
    return gson.toJson(this, type)
}

fun <T> getList(jsonArray: String?, clazz: Class<T>?): List<T>? {
    val typeOfT = TypeToken.getParameterized(MutableList::class.java, clazz).type
    return Gson().fromJson(jsonArray, typeOfT)
}

fun <T> getArrayList(jsonArray: String?, clazz: Class<T>?): ArrayList<T>? {
    val typeOfT = TypeToken.getParameterized(ArrayList::class.java, clazz).type
    return Gson().fromJson(jsonArray, typeOfT)
}

fun ImageView.setColorFilter(context: Context, @ColorRes colorID: Int) {
    this.setColorFilter(ContextCompat.getColor(context, colorID))
}


fun applyIcon(context: Context, @DrawableRes icon: Int) =
    ODroidHelper().bitmapDescriptorFromVector(context, icon)

fun Fragment.registerSubscriber() {
    if (!EventBus.getDefault().isRegistered(this)) {
        EventBus.getDefault().register(this)
    }
}

fun Fragment.removeSubscriber() {
    EventBus.getDefault().unregister(this)
}

fun Activity.registerSubscriber() {
    if (!EventBus.getDefault().isRegistered(this)) {
        EventBus.getDefault().register(this)
    }
}

fun Activity.removeSubscriber() {
    EventBus.getDefault().unregister(this)
}

fun TextView.setCompoundIntrinsicBounds(
    top: Int = 0,
    bottom: Int = 0,
    left: Int = 0,
    right: Int = 0
) {
    this.setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom)
}


fun <T> ArrayList<T>.addDistinctItems(timeItem: T) {
    val array = arrayListOf<T>()
    if (this.isNotEmpty()) {
        this.forEach { list ->
            if (timeItem != list) {
                array.add(timeItem)
            }
        }
    } else {
        array.add(timeItem)
    }
    this.add(array.first())
}


fun TextView.makeLinks(vararg links: Pair<String, View.OnClickListener>) {
    val spannableString = SpannableString(this.text)
    var startIndexOfLink = -1
    for (link in links) {
        val clickableSpan = object : ClickableSpan() {
            override fun updateDrawState(textPaint: TextPaint) {
                // use this to change the link color
                textPaint.color = textPaint.linkColor
                // toggle below value to enable/disable
                // the underline shown below the clickable text
                textPaint.isUnderlineText = true
            }

            override fun onClick(view: View) {
                Selection.setSelection((view as TextView).text as Spannable, 0)
                view.invalidate()
                link.second.onClick(view)
            }
        }
        startIndexOfLink = this.text.toString().indexOf(link.first, startIndexOfLink + 1)
//      if(startIndexOfLink == -1) continue // todo if you want to verify your texts contains links text
        spannableString.setSpan(
            clickableSpan, startIndexOfLink, startIndexOfLink + link.first.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
    this.movementMethod =
        LinkMovementMethod.getInstance() // without LinkMovementMethod, link can not click
    this.setText(spannableString, TextView.BufferType.SPANNABLE)
}


fun String.isValidEmail(): Boolean {
    return this.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()
}


fun Activity.isValidStartDate(
    startDate: Date?,
    endDate: Date?,
    context: Context,
    @StringRes messageIfInvalid: Int = R.string.invalid_start_date
): Boolean {
    return if (endDate == null || startDate!!.before(endDate)) {
        true
    } else {
        this.showToast(context.getString(messageIfInvalid))
        false
    }
}

fun Activity.isValidEndDate(
    startDate: Date?, endDate: Date?, context: Context,
    @StringRes messageIfInvalid: Int = R.string.invalid_end_date
): Boolean {
    return if (startDate == null || endDate!!.after(startDate)) {
        true
    } else {
        this.showToast(context.getString(messageIfInvalid))
        false
    }
}

fun Fragment.isValidStartDate(
    startDate: Date?, endDate: Date?, context: Context,
    @StringRes messageIfInvalid: Int = R.string.invalid_start_date
): Boolean {
    return if (endDate == null || startDate!!.before(endDate)) {
        true
    } else {
        this.showToast(context.getString(messageIfInvalid))
        false
    }
}

fun Fragment.isValidEndDate(
    startDate: Date?, endDate: Date?, context: Context,
    @StringRes messageIfInvalid: Int = R.string.invalid_end_date
): Boolean {
    return if (startDate == null || endDate!!.after(startDate)) {
        true
    } else {
        this.showToast(context.getString(messageIfInvalid))
        false
    }
}