# MyApp
这是一个新闻app，tablayout+viewpager实现了主页的新闻，drawerlayout实现左滑菜单
## 截图
![](https://github.com/Wantrer/MyApp/raw/master/raw/1.png)
![](https://github.com/Wantrer/MyApp/raw/master/raw/2.png)
![](https://github.com/Wantrer/MyApp/raw/master/raw/3.png)
## commentpullrefresh框架来刷新
com.chanven.lib:cptr:1.1.0
## fresco加载图片
com.facebook.fresco:fresco:1.1.0
## DefaultLoadingLayout SmartLoadingLayout网络等待加载界面
me.rawnhwang.library:app:1.2.5
## 网络请求 易源API
com.squareup.retrofit2:retrofit:2.2.0<br>
com.squareup.retrofit2:converter-gson:2.2.0
## 头部轮播框架
com.youth.banner:banner:1.4.9
## 换肤框架========混淆时要这样子导入
```java
compile('com.solid.skin:skinlibrary:1.4.0') {
   exclude group: 'com.android.support', module: 'appcompat-v7
   exclude group: 'com.android.support', module: 'cardview-v7'
}
```
## 返回顶部的floatingactionbutton框架
com.melnykov:floatingactionbutton:1.3.0

# LICENSE
```
Copyright [2017] [Wantrer]

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
