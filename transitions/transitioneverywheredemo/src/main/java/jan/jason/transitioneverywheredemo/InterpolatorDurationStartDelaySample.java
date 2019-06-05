/*
 * Copyright (C) 2016 Andrey Kulikov (andkulikov@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jan.jason.transitioneverywheredemo;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.transitionseverywhere.ChangeBounds;
import com.transitionseverywhere.Transition;
import com.transitionseverywhere.TransitionManager;


/**
 * 平移动画
 * 边界变化
 * Created by Andrey Kulikov on 17/04/16.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class InterpolatorDurationStartDelaySample extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_interpolator, container, false);

        final ViewGroup transitionsContainer = view.findViewById(R.id.transitions_container);
        final View button = transitionsContainer.findViewById(R.id.button);
        final View button2=transitionsContainer.findViewById(R.id.interpolator_button2);

        button.setOnClickListener(new View.OnClickListener() {

            boolean mToRightAnimation;

            @Override
            public void onClick(View v) {
                mToRightAnimation = !mToRightAnimation;

                Transition transition = new ChangeBounds();
                transition.setDuration(mToRightAnimation ? 700 : 300);
                transition.setInterpolator(mToRightAnimation ? new FastOutSlowInInterpolator() : new AccelerateInterpolator());
                transition.setStartDelay(mToRightAnimation ? 0 : 500);
                TransitionManager.beginDelayedTransition(transitionsContainer, transition);

                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) button.getLayoutParams();
                params.gravity = mToRightAnimation ? (Gravity.RIGHT | Gravity.TOP) : (Gravity.LEFT | Gravity.TOP);
                button.setLayoutParams(params);
            }

        });

        button2.setOnClickListener(new View.OnClickListener() {

            boolean mToRightAnimation;

            @Override
            public void onClick(View v) {
                mToRightAnimation = !mToRightAnimation;

                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) button2.getLayoutParams();
                params.gravity = mToRightAnimation ? (Gravity.RIGHT ) : (Gravity.LEFT );
                button2.setLayoutParams(params);
            }

        });


        return view;
    }

}
