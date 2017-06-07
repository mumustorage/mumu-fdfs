1 FastDFS简介

FastDFS服务端有两个角色：跟踪器（tracker）和存储节点（storage）。跟踪器主要做调度工作，在访问上起负载均衡的作用。

存储节点存储文件，完成文件管理的所有功能：就是这样的存储、同步和提供存取接口，FastDFS同时对文件的metadata进行管理。所谓文件的meta data就是文件的相关属性，以键值对（key valuepair）方式表示，如：width=1024，其中的key为width，value为1024。文件metadata是文件属性列表，可以包含多个键值对。

跟踪器和存储节点都可以由一台或多台服务器构成。跟踪器和存储节点中的服务器均可以随时增加或下线而不会影响线上服务。其中跟踪器中的所有服务器都是对等的，可以根据服务器的压力情况随时增加或减少。

为了支持大容量，存储节点（服务器）采用了分卷（或分组）的组织方式。存储系统由一个或多个卷组成，卷与卷之间的文件是相互独立的，所有卷的文件容量累加就是整个存储系统中的文件容量。一个卷可以由一台或多台存储服务器组成，一个卷下的存储服务器中的文件都是相同的，卷中的多台存储服务器起到了冗余备份和负载均衡的作用。

在卷中增加服务器时，同步已有的文件由系统自动完成，同步完成后，系统自动将新增服务器切换到线上提供服务。

当存储空间不足或即将耗尽时，可以动态添加卷。只需要增加一台或多台服务器，并将它们配置为一个新的卷，这样就扩大了存储系统的容量。

FastDFS中的文件标识分为两个部分：卷名和文件名，二者缺一不可。

FastDFS集群模式：



FastDFS文件上传操作：



FastDFS文件下载操作：



FastDFS文件索引解析



2 FastDFS 安装（centos）

libevent 安装：

下载  http://libevent.org/

解压 tar -xzvf libevent-2.0.22-stable.tar.gz 

编辑  ./configure --prefix=/usr/local/libevent-2.0.22-stable

安装  make && make install

FastDFS 安装：

下载 https://sourceforge.net/projects/fastdfs/

解压 tar -xzvf fdfs.tar.gz

进入到  cd FastDFS/   查看INSTALL浏览安装步骤

./make.sh

./make.sh install

3 FastDFS配置

安装完成后 配置文件保存在 /etc/fdfs 目录下

                  执行文件保存在  /usr/local/bin/fdfs_* 

修改tracker.conf配置文件

修改 tracker

注意绑定的端口号和base_path



修改storage.conf





4 测试FastDFS

启动tracker server

首先先启动tracker然后在启动storage，要不然storage会等待tracker启动

/usr/local/bin/fdfs_trackerd /etc/fdfs/tracker.conf start

启动完成之后 查看启动日志

vim /opt/fdfs/storage/logs/storaged.log 


因为我们之启动了一台tracker server所以，当前的tracker就是leader

如果没有报错 说明启动成功

如果报错，请仔细查日志信息，并查看tracker.conf配置文件来找到问题所在。



启动storage server

启动完成tracker server之后就可以启动storage server了

/usr/local/bin/fdfs_storaged /etc/fdfs/storage.conf start

正常启动storage服务的时候，会生成一些目录文件，来保存上传的文件。

FastDFS 文件存储目录结构图：





启动完成之后，查看日志文件：





测试文件上传

当tracker服务和storage服务都已经启动成功之后，我们使用FastDFS自带的测试程序来测试文件的上传

测试文件上传之前，我们首先要修改配置文件:/etc/fdfs/client.conf 

修改base_path、tracker_server



配置完成之后，我们就可以测试文件上传

/usr/local/bin/fdfs_test /etc/fdfs/client.conf upload /etc/my.cnf





5 nginx安装配置

通过FastDFS我们可以上传文件，但是怎么下载文件呢？这是通过fastdfs-nginx-module 和nginx反向代理来实现的。

fastdfs-nginx-module

下载路径 

https://sourceforge.net/projects/fastdfs/files/FastDFS%20Nginx%20Module%20Source%20Code/

解压 

tar -xzvf fastdfs-nginx-module_v1.16.tar.gz

进入到解压目录 查看INSTALL文件 查看安装流程

根据自己操作系统的配置 修改 fastdfs-nginx-module/src/config配置文件





nginx

下载

http://nginx.org/

解压

tar -xzvf nginx-1.10.3.tar.gz 

编译

./configure   --prefix=/usr/local/nginx-1.10.3 

                     --add-module=/root/nginx_ajp_module-master/  

                     --add-module=/root/fastdfs-nginx-module/src/ 

                    --with-http_gzip_static_module

                    --with-http_stub_status_module 

                    --with-http_gzip_static_module 

                    --with-http_gunzip_module 

                    --with-poll_module 

                    --with-threads 

                    --with-http_ssl_module



其中 nginx_ajp_module-master 是项目反向代理的时候是使用ajp模式

--with-http_ssl_module  nginx配置https服务

--with-http_stub_status_module  展示nginx统计功能

安装 

make && make install

安装完成之后 配置nginx.conf



mod_fastdfs

nginx安装完成之后，会再/etc/fdfs目录下自动生成mod_fastdfs.conf配置文件（该文件目录可以通过在fastdfs-nginx-module中进行配置）





配置完成之后，启动nginx程序。

/usr/local/nginx-1.10.3/sbin/nginx -s reload
然后通过网页打开测试上传的文件（如果是图片会直接展示，如果是mp4会直接播放，如果是文本文件会直接下载）。



6 java客户端调用

下载 

https://sourceforge.net/projects/fastdfs/files/latest/download?source=files

下载完成之后更具api就可以进行文件的上传和下载



本人在使用fastdfs的时候经常碰到上传文件的时候报错（偶尔发生文件无法上传）

经过百度、谷歌最终发现问题：tracker 服务存在一个timeout，如果这段时间没有文件上传下载就自动断开tracker连接，使得当我们调用文件上传下载的时候报错。

废话不多说了，解决方法：当在文件上传或者下载失败的时候，重新连接tracker服务，再次进行文件的上传下载。



本人对fastdfs做了一个简单的封装，源码地址为：

https://git.oschina.net/babymumu/mumu-storage-fdfs.git

文章推荐

FastDFS安装、配置、部署（一）－安装和部署 http://blog.csdn.net/xifeijian/article/details/38567839



