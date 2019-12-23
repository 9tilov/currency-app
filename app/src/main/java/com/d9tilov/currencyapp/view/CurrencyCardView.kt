package com.d9tilov.currencyapp.view

import android.content.Context
import android.content.res.Resources
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.d9tilov.currencyapp.R
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*
import kotlin.math.max


class CurrencyCardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {

    private val res: Resources = resources
    private val iconSize: Float
    private val shortNameTextSize: Float
    private val shortNameTextColor: Int
    private val longNameTextSize: Float
    private val longNameTextColor: Int
    private val valueTextSize: Float
    private val valueTextColor: Int
    private val signTextSize: Float
    private val signTextColor: Int
    private val enableInput: Boolean

    private val innerPadding = res.getDimension(R.dimen.icon_inner_padding)
    private val betweenNamesPadding = res.getDimension(R.dimen.between_names_inner_padding)

    private lateinit var iconFlag: AppCompatTextView
    private lateinit var shortName: TextView
    private lateinit var longName: TextView
    lateinit var value: EditText
    private lateinit var sign: TextView

    private var textWatcher: TextWatcher? = null

    init {
        val typeArray = getContext().obtainStyledAttributes(
            attrs,
            R.styleable.CurrencyCardView
        )

        iconSize = typeArray.getDimension(
            R.styleable.CurrencyCardView_iconSize,
            res.getDimension(R.dimen.icon_size)
        ) / resources.displayMetrics.scaledDensity
        initIcon()

        shortNameTextSize = typeArray.getDimension(
            R.styleable.CurrencyCardView_shortNameTextSize,
            res.getDimension(R.dimen.short_text_size)
        ) / resources.displayMetrics.scaledDensity
        shortNameTextColor = typeArray.getColor(
            R.styleable.CurrencyCardView_shortNameTextSize,
            ContextCompat.getColor(context, R.color.shortNameTextColor)
        )
        initShortName()

        longNameTextSize = typeArray.getDimension(
            R.styleable.CurrencyCardView_longNameTextSize,
            res.getDimension(R.dimen.long_text_size)
        ) / resources.displayMetrics.scaledDensity

        longNameTextColor = typeArray.getColor(
            R.styleable.CurrencyCardView_shortNameTextSize,
            ContextCompat.getColor(context, R.color.longNameTextColor)
        )
        initLongName()

        valueTextSize = typeArray.getDimension(
            R.styleable.CurrencyCardView_valueTextSize,
            res.getDimension(R.dimen.value_text_size)
        ) / resources.displayMetrics.scaledDensity
        valueTextColor = typeArray.getColor(
            R.styleable.CurrencyCardView_valueTextColor,
            ContextCompat.getColor(context, R.color.valueTextColor)
        )
        enableInput = typeArray.getBoolean(R.styleable.CurrencyCardView_enableInput, false)
        initValue()

        signTextSize = typeArray.getDimension(
            R.styleable.CurrencyCardView_signSizeTextSize,
            res.getDimension(R.dimen.sign_text_size)
        ) / resources.displayMetrics.scaledDensity
        signTextColor = typeArray.getColor(
            R.styleable.CurrencyCardView_signSizeTextColor,
            ContextCompat.getColor(context, R.color.signTextColor)
        )
        initSign()

        typeArray.recycle()
    }

    private fun initIcon() {
        iconFlag = AppCompatTextView(context)
        iconFlag.textSize = iconSize
        iconFlag.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
        addView(iconFlag)
    }

    private fun initLongName() {
        longName = TextView(context)
        longName.textSize = longNameTextSize
        longName.setTextColor(longNameTextColor)
        longName.maxLines = 1
        addView(longName)
    }

    private fun initShortName() {
        shortName = TextView(context)
        shortName.textSize = shortNameTextSize
        shortName.setTextColor(shortNameTextColor)
        shortName.maxLines = 1
        addView(shortName)
    }

    private fun initValue() {
        value = EditText(context)
        value.textSize = valueTextSize
        value.setTextColor(valueTextColor)
        value.maxLines = 1
        value.isEnabled = enableInput
        if (!enableInput) {
            value.background = null
        }
        value.inputType =
            InputType.TYPE_CLASS_NUMBER or
                    InputType.TYPE_NUMBER_FLAG_DECIMAL or
                    InputType.TYPE_NUMBER_FLAG_SIGNED
        addView(value)
    }

    private fun initSign() {
        sign = TextView(context)
        sign.textSize = signTextSize
        sign.setTextColor(signTextColor)
        sign.maxLines = 1
        addView(sign)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        measureChild(shortName, widthMeasureSpec, heightMeasureSpec)
        measureChild(longName, widthMeasureSpec, heightMeasureSpec)
        measureChild(iconFlag, widthMeasureSpec, heightMeasureSpec)
        measureChild(sign, widthMeasureSpec, heightMeasureSpec)
        measureChild(value, widthMeasureSpec, heightMeasureSpec)

        val iconHeight = iconFlag.measuredHeight.toFloat()
        val nameTextBlockHeight =
            longName.measuredHeight + 4 * betweenNamesPadding + shortName.measuredHeight
        val height = paddingTop + max(iconHeight, nameTextBlockHeight) + paddingBottom
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), height.toInt())
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val centerY = (bottom - top) / 2
        val iconSize = iconFlag.measuredHeight
        val halfIconSize = iconSize / 2
        iconFlag.layout(
            innerPadding.toInt(),
            centerY - halfIconSize,
            innerPadding.toInt() + iconSize,
            centerY + iconSize - halfIconSize
        )

        val nameBlockHeight =
            shortName.measuredHeight + longName.measuredHeight + 4 * betweenNamesPadding
        val halfNameBlockHeight = nameBlockHeight / 2
        var nameHeightUsed = centerY - halfNameBlockHeight
        longName.layout(
            innerPadding.toInt() + iconSize + innerPadding.toInt(),
            nameHeightUsed.toInt() + 2 * betweenNamesPadding.toInt(),
            innerPadding.toInt() + iconSize + innerPadding.toInt() + longName.measuredWidth,
            nameHeightUsed.toInt() + longName.measuredHeight + 2 * betweenNamesPadding.toInt()
        )

        nameHeightUsed += longName.measuredHeight + 2 * betweenNamesPadding
        shortName.layout(
            innerPadding.toInt() + iconSize + innerPadding.toInt(),
            nameHeightUsed.toInt() + betweenNamesPadding.toInt() / 2,
            innerPadding.toInt() + iconSize + innerPadding.toInt() + shortName.measuredWidth,
            nameHeightUsed.toInt() + shortName.measuredHeight + betweenNamesPadding.toInt() / 2
        )

        sign.layout(
            right - innerPadding.toInt() - sign.measuredWidth,
            centerY - sign.measuredHeight / 2,
            right - innerPadding.toInt(),
            centerY + sign.measuredHeight / 2
        )

        val signSize = max(sign.measuredHeight, value.measuredHeight)
        val halfSignSize = signSize / 2
        value.layout(
            right - innerPadding.toInt() - sign.measuredWidth - value.measuredWidth,
            centerY - halfSignSize,
            right - innerPadding.toInt() - sign.measuredWidth,
            centerY + signSize - halfSignSize
        )
    }

    fun setLongName(name: String) {
        longName.text = name
    }

    fun setShortName(name: String) {
        shortName.text = name
    }

    fun setIcon(icon: String) {
        iconFlag.text = icon
    }

    fun setValue(sum: BigDecimal) {
        val fractional: BigDecimal =
            sum.setScale(ROUND_VALUE_AFTER_DOT, RoundingMode.HALF_EVEN)
        value.setText(String.format(Locale.getDefault(), fractional.toString()))
    }

    fun setSign(signText: String) {
        sign.text = signText
    }

    fun addTextWatcher(newTextWatcher: TextWatcher) {
        if (textWatcher == null) {
            this.textWatcher = newTextWatcher
            this.value.addTextChangedListener(newTextWatcher)
        }
    }

    companion object {
        private const val ROUND_VALUE_AFTER_DOT = 2
    }
}
