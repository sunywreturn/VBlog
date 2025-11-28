windows系统中
后端运行命令：cd blogserver; .\mvnw.cmd spring-boot:run
前端npm依赖安装：npm install --legacy-peer-deps
前端运行命令：npm run dev
解决乱码问题：
docker exec -it mysql57 bash
mysql -uroot -p -e "DROP DATABASE IF EXISTS vueblog2;"
mysql -uroot -p --default-character-set=utf8 < /tmp/vueblog.sql