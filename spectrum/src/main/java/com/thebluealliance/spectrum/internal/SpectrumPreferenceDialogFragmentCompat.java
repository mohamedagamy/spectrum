package com.thebluealliance.spectrum.internal;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.PreferenceDialogFragmentCompat;

import com.thebluealliance.spectrum.R;
import com.thebluealliance.spectrum.SpectrumPalette;
import com.thebluealliance.spectrum.SpectrumPreferenceCompat;

public class SpectrumPreferenceDialogFragmentCompat extends PreferenceDialogFragmentCompat {

    private SpectrumPalette mColorPalette;
    private @ColorInt
    int mCurrentValue;

    public static SpectrumPreferenceDialogFragmentCompat newInstance(String key) {
        final SpectrumPreferenceDialogFragmentCompat fragment =
                new SpectrumPreferenceDialogFragmentCompat();
        final Bundle b = new Bundle(1);
        b.putString(ARG_KEY, key);
        fragment.setArguments(b);
        return fragment;
    }

    private SpectrumPreferenceCompat getSpectrumPreference() {
        return (SpectrumPreferenceCompat) getPreference();
    }

    @Override protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {
        super.onPrepareDialogBuilder(builder);

        // Don't show the positive button; clicking a color will be the "positive" action
        if (getSpectrumPreference().getCloseOnSelected()) {
            builder.setPositiveButton(null, null);
        }
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        final SpectrumPreferenceCompat preference = getSpectrumPreference();

        if (preference.getColors() == null) {
            throw new RuntimeException("SpectrumPreference requires a colors array");
        }

        mCurrentValue = preference.getColor();

        mColorPalette = (SpectrumPalette) view.findViewById(R.id.palette);
        mColorPalette.setColors(getSpectrumPreference().getColors());
        mColorPalette.setSelectedColor(mCurrentValue);
        mColorPalette.setOutlineWidth(getSpectrumPreference().getOutlineWidth());
        mColorPalette.setFixedColumnCount(getSpectrumPreference().getFixedColumnCount());
        mColorPalette.setOnColorSelectedListener(new SpectrumPalette.OnColorSelectedListener() {
            @Override
            public void onColorSelected(@ColorInt int color) {
                mCurrentValue = color;
                if (preference.getCloseOnSelected()) {
                    SpectrumPreferenceDialogFragmentCompat.this.onClick(null, DialogInterface.BUTTON_POSITIVE);
                    if (getDialog() != null) {
                        getDialog().dismiss();
                    }
                }
            }
        });
    }

    @Override
    public void onDialogClosed(boolean positiveResult) {
        final SpectrumPreferenceCompat preference = getSpectrumPreference();
        if (positiveResult) {
            if (preference.callChangeListener(mCurrentValue)) {
                preference.setColor(mCurrentValue);
            }
        }
    }
}
