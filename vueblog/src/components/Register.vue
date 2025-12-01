<template>
  <div class="register-container">
    <el-form ref="registerForm" :model="registerForm" :rules="rules" class="register-form" label-position="left"
             label-width="0px" v-loading="loading">
      <h3 class="register_title">用户注册</h3>
      <el-form-item prop="username">
        <el-input type="text" v-model="registerForm.username" auto-complete="off" placeholder="用户名" clearable></el-input>
      </el-form-item>
      <el-form-item prop="password">
        <el-input type="password" v-model="registerForm.password" auto-complete="off" placeholder="密码" clearable show-password></el-input>
      </el-form-item>
      <el-form-item prop="confirmPassword">
        <el-input type="password" v-model="registerForm.confirmPassword" auto-complete="off" placeholder="确认密码" clearable show-password></el-input>
      </el-form-item>
      <el-form-item prop="nickname">
        <el-input type="text" v-model="registerForm.nickname" auto-complete="off" placeholder="昵称" clearable></el-input>
      </el-form-item>
      <el-form-item prop="email">
        <el-input type="email" v-model="registerForm.email" auto-complete="off" placeholder="邮箱" clearable></el-input>
      </el-form-item>
      <el-form-item style="width: 100%">
        <el-button type="primary" @click.native.prevent="submitClick" style="width: 100%">注册</el-button>
      </el-form-item>
      <div class="login_link">
        <span>已有账号？</span>
        <el-link type="primary" @click="goToLogin">立即登录</el-link>
      </div>
    </el-form>
  </div>
</template>

<script>
  import {postRequest} from '../utils/api'
  
  export default{
    data(){
      // 密码确认验证
      var validateConfirmPassword = (rule, value, callback) => {
        if (value === '') {
          callback(new Error('请再次输入密码'));
        } else if (value !== this.registerForm.password) {
          callback(new Error('两次输入密码不一致!'));
        } else {
          callback();
        }
      };
      
      return {
        rules: {
          username: [
            {required: true, message: '请输入用户名', trigger: 'blur'},
            {min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur'}
          ],
          password: [
            {required: true, message: '请输入密码', trigger: 'blur'},
            {min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur'}
          ],
          confirmPassword: [
            {required: true, message: '请确认密码', trigger: 'blur'},
            {validator: validateConfirmPassword, trigger: 'blur'}
          ],
          nickname: [
            {required: true, message: '请输入昵称', trigger: 'blur'},
            {min: 2, max: 20, message: '昵称长度在 2 到 20 个字符', trigger: 'blur'}
          ],
          email: [
            {required: true, message: '请输入邮箱地址', trigger: 'blur'},
            {type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur'}
          ]
        },
        registerForm: {
          username: '',
          password: '',
          confirmPassword: '',
          nickname: '',
          email: ''
        },
        loading: false
      }
    },
    methods: {
      submitClick: function () {
        var _this = this;
        
        // 表单验证
        this.$refs.registerForm.validate((valid) => {
          if (!valid) {
            this.$message.error('请填写完整的注册信息');
            return false;
          }
          
          this.loading = true;
          
          // 发送注册请求
          postRequest('/reg', {
            username: this.registerForm.username,
            password: this.registerForm.password,
            nickname: this.registerForm.nickname,
            email: this.registerForm.email
          }).then(resp => {
            _this.loading = false;
            if (resp.status == 200) {
              var json = resp.data;
              if (json.status == 'success') {
                _this.$message.success('注册成功！请登录');
                _this.$router.push('/login');
              } else if (json.status == 'error') {
                if (json.msg.includes('用户名重复')) {
                  _this.$message.error('用户名已存在，请更换用户名');
                } else {
                  _this.$message.error('注册失败：' + json.msg);
                }
              }
            } else {
              _this.$message.error('注册失败，服务器错误');
            }
          }, resp => {
            _this.loading = false;
            _this.$message.error('服务器连接失败，请稍后重试');
          });
        });
      },
      
      goToLogin: function () {
        this.$router.push('/login');
      }
    }
  }
</script>

<style scoped>
  .register-container {
    display: flex;
    justify-content: center;
    align-items: center;
    min-height: 100vh;
    background: #f5f5f5;
  }
  
  .register-form {
    border-radius: 15px;
    background-clip: padding-box;
    width: 400px;
    padding: 35px 35px 15px 35px;
    background: #fff;
    border: 1px solid #eaeaea;
    box-shadow: 0 0 25px #cac6c6;
  }
  
  .register_title {
    margin: 0px auto 40px auto;
    text-align: center;
    color: #505458;
    font-size: 24px;
  }
  
  .login_link {
    text-align: center;
    margin-top: 15px;
    font-size: 14px;
    color: #606266;
  }
  
  .login_link span {
    margin-right: 8px;
  }
</style>