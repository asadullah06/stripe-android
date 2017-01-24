package com.stripe.android.view;

import android.graphics.Color;
import android.support.annotation.ColorInt;

import com.stripe.android.testharness.CardInputTestActivity;
import com.stripe.android.testharness.ViewTestUtils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.ActivityController;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

/**
 * Test class for {@link StripeEditText}.
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 22)
public class StripeEditTextTest {

    @Mock StripeEditText.DeleteEmptyListener mDeleteEmptyListener;
    private StripeEditText mEditText;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ActivityController activityController =
                Robolectric.buildActivity(CardInputTestActivity.class).create().start();

        // Note that the CVC EditText is a DeleteWatchEditText
        mEditText = ((CardInputTestActivity) activityController.get()).getCvcEditText();
        mEditText.setText("");
        mEditText.setDeleteEmptyListener(mDeleteEmptyListener);
    }

    @Test
    public void deleteText_whenZeroLength_callsListener() {
        ViewTestUtils.sendDeleteKeyEvent(mEditText);
        verify(mDeleteEmptyListener, times(1)).onDeleteEmpty();
    }

    @Test
    public void addText_doesNotCallListener() {
        mEditText.append("1");
        verifyZeroInteractions(mDeleteEmptyListener);
    }

    @Test
    public void deleteText_whenNonZeroLength_doesNotCallListener() {
        mEditText.append("1");
        ViewTestUtils.sendDeleteKeyEvent(mEditText);
        verifyZeroInteractions(mDeleteEmptyListener);
    }

    @Test
    public void deleteText_whenSelectionAtBeginningButLengthNonZero_doesNotCallListener() {
        mEditText.append("12");
        mEditText.setSelection(0);
        ViewTestUtils.sendDeleteKeyEvent(mEditText);
        verifyZeroInteractions(mDeleteEmptyListener);
    }

    @Test
    public void deleteText_whenDeletingMultipleItems_onlyCallsListenerOneTime() {
        mEditText.append("123");
        // Doing this four times because we need to delete all three items, then jump back.
        for (int i = 0; i < 4; i++) {
            ViewTestUtils.sendDeleteKeyEvent(mEditText);
        }

        verify(mDeleteEmptyListener, times(1)).onDeleteEmpty();
    }

    @Test
    public void isColorDark_forExampleLightColors_returnsFalse() {
        @ColorInt int middleGray = 0x888888;
        @ColorInt int offWhite = 0xfaebd7;
        @ColorInt int lightCyan = 0x8feffb;
        @ColorInt int lightYellow = 0xfcf4b2;
        @ColorInt int lightBlue = 0x9cdbff;

        assertFalse(StripeEditText.isColorDark(middleGray));
        assertFalse(StripeEditText.isColorDark(offWhite));
        assertFalse(StripeEditText.isColorDark(lightCyan));
        assertFalse(StripeEditText.isColorDark(lightYellow));
        assertFalse(StripeEditText.isColorDark(lightBlue));
        assertFalse(StripeEditText.isColorDark(Color.WHITE));
    }

    @Test
    public void isColorDark_forExampleDarkColors_returnsTrue() {
        @ColorInt int logoBlue = 0x6772e5;
        @ColorInt int slate = 0x525f7f;
        @ColorInt int darkPurple = 0x6b3791;
        @ColorInt int darkishRed = 0x9e2146;

        assertTrue(StripeEditText.isColorDark(logoBlue));
        assertTrue(StripeEditText.isColorDark(slate));
        assertTrue(StripeEditText.isColorDark(darkPurple));
        assertTrue(StripeEditText.isColorDark(darkishRed));
        assertTrue(StripeEditText.isColorDark(Color.BLACK));
    }
}
