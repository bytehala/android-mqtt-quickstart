package io.bytehala.eclipsemqtt.sample


import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.*
import org.hamcrest.TypeSafeMatcher
import org.hamcrest.core.IsInstanceOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

@LargeTest
@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun mainActivityTest() {
        val floatingActionButton = onView(
            allOf(
                withId(R.id.addConnectionFab),
                childAtPosition(
                    childAtPosition(
                        withId(android.R.id.content),
                        0
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        floatingActionButton.perform(click())

        val actionMenuItemView = onView(
            allOf(
                withId(R.id.advanced), withText("Advanced"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.action_bar),
                        1
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        actionMenuItemView.perform(click())

        val actionMenuItemView2 = onView(
            allOf(
                withId(R.id.setLastWill), withText("Last Will"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.action_bar),
                        1
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        actionMenuItemView2.perform(click())

        pressBack()

        pressBack()

        val appCompatEditText = onView(
            allOf(
                withId(R.id.clientId),
                childAtPosition(
                    allOf(
                        withId(R.id.clientIdGroup),
                        childAtPosition(
                            withClassName(`is`("android.widget.RelativeLayout")),
                            0
                        )
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        appCompatEditText.perform(replaceText(generateString()), closeSoftKeyboard())

        val appCompatAutoCompleteTextView = onView(
            allOf(
                withId(R.id.serverURI),
                childAtPosition(
                    allOf(
                        withId(R.id.serverGroup),
                        childAtPosition(
                            withClassName(`is`("android.widget.RelativeLayout")),
                            1
                        )
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        appCompatAutoCompleteTextView.perform(replaceText("broker.hivemq.com"), closeSoftKeyboard())

        val appCompatEditText2 = onView(
            allOf(
                withId(R.id.port),
                childAtPosition(
                    allOf(
                        withId(R.id.portGroup),
                        childAtPosition(
                            withClassName(`is`("android.widget.RelativeLayout")),
                            2
                        )
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        appCompatEditText2.perform(replaceText("1883"), closeSoftKeyboard())

        val appCompatButton = onView(
            allOf(
                withId(R.id.connectButton), withText("Connect"),
                childAtPosition(
                    childAtPosition(
                        withId(android.R.id.content),
                        0
                    ),
                    4
                ),
                isDisplayed()
            )
        )
        appCompatButton.perform(click())

        val textView = onView(
            allOf(
                withText("random\n Connected to broker.hivemq.com"),
                childAtPosition(
                    allOf(
                        withId(R.id.list),
                        childAtPosition(
                            IsInstanceOf.instanceOf(android.widget.FrameLayout::class.java),
                            0
                        )
                    ),
                    0
                ),
                isDisplayed()
            )
        )
//        textView.check(matches(withText("random  Connected to broker.hivemq.com")))

        val appCompatTextView = onData(anything())
            .inAdapterView(
                allOf(
                    withId(R.id.list),
                    childAtPosition(
                        withClassName(`is`("android.widget.FrameLayout")),
                        0
                    )
                )
            )
            .atPosition(0)
        appCompatTextView.perform(click())

        val tabView = onView(
            allOf(
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("androidx.appcompat.widget.ScrollingTabContainerView")),
                        0
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        tabView.perform(click())

        val appCompatEditText3 = onView(
            allOf(
                withId(R.id.topic),
                childAtPosition(
                    allOf(
                        withId(R.id.topicSubViewGroup),
                        childAtPosition(
                            withClassName(`is`("android.widget.RelativeLayout")),
                            0
                        )
                    ),
                    1
                ),
                isDisplayed()
            )
        )
//        appCompatEditText3.perform(replaceText("totopic"), closeSoftKeyboard())

        val appCompatButton2 = onView(
            allOf(
                withId(R.id.subscribeButton), withText("Subscribe"),
                childAtPosition(
                    withParent(withId(R.id.pager)),
                    2
                ),
                isDisplayed()
            )
        )
//        appCompatButton2.perform(click())

        val tabView2 = onView(
            allOf(
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("androidx.appcompat.widget.ScrollingTabContainerView")),
                        0
                    ),
                    2
                ),
                isDisplayed()
            )
        )
        tabView2.perform(click())

        val appCompatEditText4 = onView(
            allOf(
                withId(R.id.lastWillTopic),
                childAtPosition(
                    allOf(
                        withId(R.id.topicGroup),
                        childAtPosition(
                            withClassName(`is`("android.widget.RelativeLayout")),
                            0
                        )
                    ),
                    1
                ),
                isDisplayed()
            )
        )
//        appCompatEditText4.perform(replaceText("totopic"), closeSoftKeyboard())

        val appCompatEditText5 = onView(
            allOf(
                withId(R.id.lastWill),
                childAtPosition(
                    allOf(
                        withId(R.id.messageGroup),
                        childAtPosition(
                            withClassName(`is`("android.widget.RelativeLayout")),
                            1
                        )
                    ),
                    1
                ),
                isDisplayed()
            )
        )
//        appCompatEditText5.perform(replaceText("hello"), closeSoftKeyboard())

        val appCompatButton3 = onView(
            allOf(
                withId(R.id.publishButton), withText("Publish"),
                childAtPosition(
                    withParent(withId(R.id.pager)),
                    3
                ),
                isDisplayed()
            )
        )
//        appCompatButton3.perform(click())

        val tabView3 = onView(
            allOf(
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("androidx.appcompat.widget.ScrollingTabContainerView")),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        tabView3.perform(click())

        pressBack()
    }

    private fun childAtPosition(
        parentMatcher: Matcher<View>, position: Int
    ): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position)
            }
        }
    }

    private fun generateString(): String {
        val charPool : List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')

        val randomString = (1..5)
            .map { i -> kotlin.random.Random.nextInt(0, charPool.size) }
            .map(charPool::get)
            .joinToString("");

        return randomString;
    }
}
