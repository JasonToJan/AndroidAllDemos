package com.goyourfly.multiselectadapter.more

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem

import com.goyourfly.multiple.adapter.MultipleSelect
import com.goyourfly.multiple.adapter.menu.MenuController
import com.goyourfly.multiple.adapter.viewholder.view.CheckBoxFactory
import com.goyourfly.multiple.adapter.menu.CustomMenuBar
import com.goyourfly.multiselectadapter.DemoSectionAdapter
import com.goyourfly.multiselectadapter.R
import com.goyourfly.multiselectadapter.RecyclerActivityMulti

/**
 * Created by gaoyufei on 2017/6/12.
 */

class Demo6Activity : RecyclerActivityMulti() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val demoSectionAdapter = DemoSectionAdapter()
        val adapter = MultipleSelect
                .with(this)
                .adapter(demoSectionAdapter)
                .ignoreViewType(arrayOf(1))
                .linkList(demoSectionAdapter.list)
                .decorateFactory(CheckBoxFactory(Color.RED, 300, Gravity.RIGHT, 8))
                .customMenu(object : CustomMenuBar(this, R.menu.menu_select_multi, resources.getColor(R.color.colorPrimaryDark), Gravity.TOP) {
                    override fun onMenuItemClick(menuItem: MenuItem, controller: MenuController) {

                    }
                })
                .build()
        recycler.adapter = adapter
    }
}
