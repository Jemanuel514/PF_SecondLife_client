package com.example.pf_secondlife_client.ui.payment

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class ExpirationVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val digits = text.text.take(4)
        val formatted = buildString {
            digits.forEachIndexed { index, char ->
                append(char)
                if (index == 1) append('/')
            }
        }

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int =
                if (offset <= 2) offset else offset + 1

            override fun transformedToOriginal(offset: Int): Int =
                if (offset <= 2) offset else offset - 1
        }

        return TransformedText(AnnotatedString(formatted), offsetMapping)
    }
}