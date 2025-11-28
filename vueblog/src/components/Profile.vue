<template>
  <div class="profile-container">
    <el-card class="profile-card">
      <div slot="header" class="clearfix">
        <span>个人主页</span>
      </div>
      <div class="profile-content">
        <div class="profile-header">
          <el-avatar :size="100" :src="userInfo.avatar" class="profile-avatar">
            <i class="el-icon-user-solid"></i>
          </el-avatar>
          <h2 class="profile-username">{{ userInfo.username }}</h2>
          <p class="profile-bio">{{ userInfo.bio || '这个人很懒，什么都没写~' }}</p>
        </div>
        
        <el-divider></el-divider>
        
        <div class="profile-stats">
          <el-row :gutter="20">
            <el-col :span="8">
              <div class="stat-item">
                <div class="stat-number">{{ userInfo.articleCount || 0 }}</div>
                <div class="stat-label">文章数</div>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="stat-item">
                <div class="stat-number">{{ userInfo.viewCount || 0 }}</div>
                <div class="stat-label">总浏览量</div>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="stat-item">
                <div class="stat-number">{{ userInfo.likeCount || 0 }}</div>
                <div class="stat-label">获赞数</div>
              </div>
            </el-col>
          </el-row>
        </div>
        
        <el-divider></el-divider>
        
        <div class="profile-info">
          <el-descriptions title="基本信息" :column="2" border>
            <el-descriptions-item label="用户名">{{ userInfo.username }}</el-descriptions-item>
            <el-descriptions-item label="昵称">{{ userInfo.nickname || userInfo.username }}</el-descriptions-item>
            <el-descriptions-item label="邮箱">{{ userInfo.email || '未设置' }}</el-descriptions-item>
            <el-descriptions-item label="手机号">{{ userInfo.phone || '未设置' }}</el-descriptions-item>
            <el-descriptions-item label="注册时间">{{ userInfo.registerTime || '未知' }}</el-descriptions-item>
            <el-descriptions-item label="最后登录">{{ userInfo.lastLoginTime || '未知' }}</el-descriptions-item>
          </el-descriptions>
        </div>
        
        <div class="profile-actions">
          <el-button type="primary" icon="el-icon-edit" @click="editProfile">编辑资料</el-button>
          <el-button type="success" icon="el-icon-document" @click="viewMyArticles">我的文章</el-button>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'Profile',
  data() {
    return {
      userInfo: {
        username: '',
        nickname: '',
        email: '',
        phone: '',
        bio: '',
        avatar: '',
        articleCount: 0,
        viewCount: 0,
        likeCount: 0,
        registerTime: '',
        lastLoginTime: ''
      }
    }
  },
  methods: {
    getUserProfile() {
      // 这里应该调用后端API获取用户信息
      // 暂时使用模拟数据
      this.userInfo = {
        username: 'admin',
        nickname: '管理员',
        email: 'admin@example.com',
        phone: '13800138000',
        bio: '这是我的个人简介，热爱技术，喜欢分享。',
        avatar: '',
        articleCount: 25,
        viewCount: 1580,
        likeCount: 89,
        registerTime: '2023-01-01',
        lastLoginTime: new Date().toLocaleString()
      }
    },
    editProfile() {
      this.$message.info('编辑资料功能待开发')
    },
    viewMyArticles() {
      this.$router.push('/articleList')
    }
  },
  mounted() {
    this.getUserProfile()
  }
}
</script>

<style scoped>
.profile-container {
  padding: 20px;
}

.profile-card {
  max-width: 800px;
  margin: 0 auto;
}

.profile-header {
  text-align: center;
  margin-bottom: 30px;
}

.profile-avatar {
  margin-bottom: 20px;
}

.profile-username {
  margin: 10px 0;
  color: #303133;
}

.profile-bio {
  color: #606266;
  font-size: 14px;
  line-height: 1.5;
}

.profile-stats {
  margin: 30px 0;
}

.stat-item {
  text-align: center;
}

.stat-number {
  font-size: 24px;
  font-weight: bold;
  color: #409EFF;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

.profile-info {
  margin: 30px 0;
}

.profile-actions {
  text-align: center;
  margin-top: 30px;
}

.profile-actions .el-button {
  margin: 0 10px;
}
</style>