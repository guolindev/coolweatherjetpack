# 酷欧天气Jetpack版
酷欧天气的Jetpack版本实现，采用了MVVM架构。

这里我先给出一张酷欧天气Jetpack版的架构设计图，这张图是模仿Google Codelabs的Sunshine项目画出来的。拥有良好架构设计的项目都是可以用简洁清晰的架构图表示出来的，而一个杂乱无章没有架构设计的项目则很难用架构图表示出来。

<img src="https://raw.githubusercontent.com/guolindev/coolweatherjetpack/master/images/architecture.jpg" width="750" />

上述架构图可能一开始看你会找不着重点，其实这张架构图非常清晰，我来带大家解读一下。

首先我们通过这张架构图成功将程序分为了若干层。

绿色部分表示的是UI控制层，这部分就是我们平时写的Activity和Fragment。

蓝色部分表示的是ViewModel层，ViewModel用于持有和UI元素相关的数据，以保证这些数据在屏幕旋转时不会丢失，以及负责和仓库之间进行通讯。

黄色部分表示的是仓库层，仓库层要做的工作是自主判断接口请求的数据应该是从数据库中读取还是从网络中获取，并将数据返回给调用方。如果是从网络中获取的话还要将这些数据存入到数据库当中，以避免下次重复从网络中获取。简而言之，仓库的工作就是在本地和网络数据之间做一个分配和调度的工作，调用方不管你的数据是从何而来的，我只是要从你仓库这里获取数据而已，而仓库则要自主分配如何更好更快地将数据提供给调用方。

接下来灰色部分表示是的本地数据层，实现方式并不固定，我使用了<a href="https://github.com/LitePalFramework/LitePal" target="_blank">LitePal</a>来进行数据持久化处理，你也可以使用别的框架。

最后红色部分表示的是网络数据层，这里使用了Retrofit从web服务接口获取数据。

借助这张架构图，我想会在很大程度上便于大家理解酷欧天气Jetpack版这个开源项目，而如果你自己编写的项目也能尝试画出这样一张架构图，那么你的代码结构一定是非常不错的。

另外对于这张架构图我还有必要再解释一下，图中所有的箭头都是单向的，比方说WeatherActivity指向了WeatherViewModel，表示WeatherActivity持有WeatherViewModel的引用，但是反过来WeatherViewModel不能持有WeatherActivity的引用。其他的几层也是一样的道理，一个箭头就表示持有一个引用。

还有，引用不能跨层持有，就比方说UI控制层不能持有仓库层的引用，每一层的组件都只能和它的相邻层交互。

大概就介绍这么多吧，剩下的就靠大家自己去阅读源码进行学习了。

项目运行截图如下：

<img src="https://raw.githubusercontent.com/guolindev/coolweatherjetpack/master/images/Screenshot_1.png" width="250" />&nbsp;&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;<img src="https://raw.githubusercontent.com/guolindev/coolweatherjetpack/master/images/Screenshot_2.png" width="250" />

最后，希望这个项目能够帮助大家更好地学习Jetpack，更好地学习MVVM架构。

学习更多的Android技术知识，请关注我的微信公众号：

<img src="https://raw.githubusercontent.com/guolindev/booksource/master/qrcode.jpg" width="250" />
