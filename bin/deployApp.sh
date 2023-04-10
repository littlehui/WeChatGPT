#!/bin/bash
EXPECT_PATH="/bin:/sbin:/usr/bin:/usr/sbin:/usr/local/bin:/usr/local/sbin:"
LANG=zh_CN
export EXPECT_PATH LANG
mvn clean package -DskipTests -T 4C

echo '编译结束'
ips=(
47.75.0.185
)
for ip in ${ips[@]};
do
echo '开始'
echo '开始发布'${ip}
scp dzt-web/dzt-app-web/target/dzt-app-web.jar root@${ip}:/home/html/wxapp/dzt/tmp
ssh root@${ip} 'cp -r /home/html/wxapp/dzt/tmp/dzt-app-web.jar /home/html/wxapp/dzt'
ssh root@${ip} '~/bin/java/redeployDztAppWeb.sh'
echo ${ip}'发布完成'
done
echo '全部成功'
