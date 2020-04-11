## PNavigationView
简单使用
```
dependencies {
    ...
    implementation 'inkgirigiri:PNavigationView:1.0.0'
}
```

在布局文件
```
<ink.girigiri.navigation.PNavigatioinView
        android:id="@+id/navigation"
        android:layout_gravity="bottom"
        android:background="@color/colorPrimary"
        app:menu="@menu/navigation_menu"
        android:layout_width="match_parent"
        android:layout_height="60dp"/>
```
navigation_menu.xml
```
<?xml version="1.0" encoding="utf-8"?>
<menu xmlns:android="http://schemas.android.com/apk/res/android">
    <item android:id="@+id/item1" android:title="home" android:icon="@drawable/ic_home_white_24dp"/>
    <item android:id="@+id/item2" android:title="message" android:icon="@drawable/ic_message_white_24dp"/>
    <item android:id="@+id/item3" android:title="balance" android:icon="@drawable/ic_account_balance_wallet_white_24dp"/>
    <item android:id="@+id/item4" android:title="mine" android:icon="@drawable/ic_account_box_white_24dp"/>
</menu>
```
自定义属性
```
<declare-styleable name="PNavigatioinView">
        <attr name="iconSize" format="dimension" />
        <attr name="labelSize" format="dimension" />
        <attr name="labelColor" format="color" />
        <attr name="menu" format="reference" />
        <attr name="showLabel" format="boolean" />
        <attr name="showMiniBar" format="boolean" />
        <attr name="miniBarColor" format="color" />
        <attr name="miniBarHeight" format="dimension" />
    </declare-styleable>
```

run app
