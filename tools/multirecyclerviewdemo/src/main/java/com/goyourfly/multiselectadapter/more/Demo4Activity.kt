package com.goyourfly.multiselectadapter.more

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.TextView
import com.goyourfly.multiple.adapter.MultipleSelect
import com.goyourfly.multiple.adapter.viewholder.view.RadioBtnFactory
import com.goyourfly.multiple.adapter.menu.MenuBar
import com.goyourfly.multiselectadapter.DemoAdapter
import com.goyourfly.multiselectadapter.R
import com.goyourfly.multiselectadapter.RecyclerActivityMulti
import java.util.*

class Demo4Activity : RecyclerActivityMulti() {
    var adapter = DemoAdapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        recycler.adapter = MultipleSelect.with(this)
                .adapter(adapter)
                .decorateFactory(RadioBtnFactory())
                .customMenu(CustomMenuBar(this, adapter))
                .build()

    }


    class CustomMenuBar(activity: Activity, val adapter: DemoAdapter) : MenuBar(activity, Gravity.TOP) {
        override fun onUpdateTitle(selectCount: Int, total: Int) {
            title?.setText("当前选中：$selectCount 条数据")
        }

        var title: TextView? = null
        var confirm: View? = null
        var cancel: View? = null
        var delete: View? = null
        override fun getContentView(): View {
            val view = View.inflate(activity, R.layout.custom_control, null)
            title = view.findViewById(R.id.title) as TextView
            confirm = view.findViewById(R.id.confirm)
            cancel = view.findViewById(R.id.cancel)
            delete = view.findViewById(R.id.delete)

            confirm?.setOnClickListener { controler?.done() }
            cancel?.setOnClickListener { controler?.cancel() }
            delete?.setOnClickListener {
                val select = controler?.getSelect()
                Collections.reverse(select)
                "select:$select".log()
                for (index in select!!) {
                    "removeItem:$index".log()
                    adapter.removeItem(index)
                }
                controler?.done()
            }
            return view;
        }

        fun String.log() {
            Log.d("Demo3Activity", this)
        }
    }

    fun String.log() {
        Log.d("Demo3Activity", this)
    }
}
