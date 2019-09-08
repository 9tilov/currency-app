package com.d9tilov.currencyapp.view

import android.content.Context
import android.content.res.Resources
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.d9tilov.currencyapp.R
import com.d9tilov.currencyapp.utils.Utils
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

    private val innerPadding = res.getDimension(R.dimen.icon_inner_padding)
    private val betweenNamesPadding = res.getDimension(R.dimen.between_names_inner_padding)

    private lateinit var iconFlag: AppCompatTextView
    private lateinit var shortName: TextView
    private lateinit var longName: TextView
    private lateinit var value: TextView
    private lateinit var sign: TextView

    init {
        val typeArray = getContext().obtainStyledAttributes(
            attrs,
            R.styleable.CurrencyCardView
        )

        iconSize = typeArray.getDimension(
            R.styleable.CurrencyCardView_iconSize,
            res.getDimension(R.dimen.icon_size)
        )
        initIcon(iconSize)

        shortNameTextSize = typeArray.getDimension(
            R.styleable.CurrencyCardView_shortNameTextSize,
            res.getDimension(R.dimen.short_text_size)
        )
        shortNameTextColor = typeArray.getColor(
            R.styleable.CurrencyCardView_shortNameTextSize,
            ContextCompat.getColor(context, R.color.shortNameTextColor)
        )
        initShortName(shortNameTextSize, shortNameTextColor)

        longNameTextSize = typeArray.getDimension(
            R.styleable.CurrencyCardView_longNameTextSize,
            res.getDimension(R.dimen.long_text_size)
        )
        longNameTextColor = typeArray.getColor(
            R.styleable.CurrencyCardView_shortNameTextSize,
            ContextCompat.getColor(context, R.color.longNameTextColor)
        )
        initLongName(longNameTextSize, longNameTextColor)

        valueTextSize = typeArray.getDimension(
            R.styleable.CurrencyCardView_valueTextSize,
            res.getDimension(R.dimen.value_text_size)
        )
        valueTextColor = typeArray.getColor(
            R.styleable.CurrencyCardView_valueTextColor,
            ContextCompat.getColor(context, R.color.valueTextColor)
        )
        initValue(valueTextSize, valueTextColor)

        signTextSize = typeArray.getDimension(
            R.styleable.CurrencyCardView_signSizeTextSize,
            res.getDimension(R.dimen.sign_text_size)
        )
        signTextColor = typeArray.getColor(
            R.styleable.CurrencyCardView_signSizeTextColor,
            ContextCompat.getColor(context, R.color.signTextColor)
        )
        initSign(signTextSize, signTextColor)
        typeArray.recycle()
    }

    private fun initIcon(iconSize: Float) {
        iconFlag = AppCompatTextView(context)
        iconFlag.textSize = iconSize
        addView(iconFlag)
    }

    private fun initLongName(textSize: Float, textColor: Int) {
        longName = TextView(context)
        longName.textSize = textSize
        longName.setTextColor(textColor)
        longName.maxLines = 1
        addView(longName)
    }

    private fun initShortName(textSize: Float, textColor: Int) {
        shortName = TextView(context)
        shortName.textSize = textSize
        shortName.setTextColor(textColor)
        shortName.maxLines = 1
        addView(shortName)
    }

    private fun initValue(textSize: Float, textColor: Int) {
        value = TextView(context)
        value.textSize = textSize
        value.setTextColor(textColor)
        value.maxLines = 1
        addView(value)
    }

    private fun initSign(textSize: Float, textColor: Int) {
        sign = TextView(context)
        sign.textSize = textSize
        sign.setTextColor(textColor)
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
            nameHeightUsed.toInt() + betweenNamesPadding.toInt(),
            innerPadding.toInt() + iconSize + innerPadding.toInt() + longName.measuredWidth,
            nameHeightUsed.toInt() + longName.measuredHeight + betweenNamesPadding.toInt()
        )

        nameHeightUsed += longName.measuredHeight + 2 * betweenNamesPadding
        shortName.layout(
            innerPadding.toInt() + iconSize + innerPadding.toInt(),
            nameHeightUsed.toInt() + betweenNamesPadding.toInt(),
            innerPadding.toInt() + iconSize + innerPadding.toInt() + shortName.measuredWidth,
            nameHeightUsed.toInt() + shortName.measuredHeight + betweenNamesPadding.toInt()
        )

        val signSize = max(sign.measuredHeight, value.measuredHeight)
        val halfSignSize = signSize / 2
        sign.layout(
            right - innerPadding.toInt() - sign.measuredWidth,
            centerY - halfSignSize,
            right - innerPadding.toInt(),
            centerY + signSize - halfSignSize
        )

        value.layout(
            right - innerPadding.toInt() - sign.measuredWidth - betweenNamesPadding.toInt() - value.measuredWidth,
            centerY - halfSignSize,
            right - innerPadding.toInt() - sign.measuredWidth - betweenNamesPadding.toInt(),
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
        iconFlag.text = Utils.getCurrencyFlagEmojiBy(icon)
    }

    fun setValue(sum: Int) {
        val fractional: Float = sum / 100f
        value.text = fractional.toString()
    }

    fun setSign(signText: String) {
        sign.text = signText
    }
}
