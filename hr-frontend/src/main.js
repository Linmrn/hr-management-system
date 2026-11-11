import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'

import zhCn from 'element-plus/es/locale/lang/zh-cn'
import 'element-plus/dist/index.css'
import 'font-awesome/css/font-awesome.min.css'
import './assets/main.scss'
import { postRequest, putRequest, deleteRequest, getRequest, postKeyValueRequest } from './utils/api'
import { useUserStore } from './store/userInfo'

const app = createApp(App)

const pinia = createPinia()
app.use(pinia)

// 刷新页面后恢复登录用户信息
useUserStore(pinia).restoreSession()
app.use(router)
app.use(ElementPlus, {
    locale: zhCn,
    size: 'small' })

app.config.globalProperties.$postRequest = postRequest
app.config.globalProperties.$putRequest = putRequest
app.config.globalProperties.$deleteRequest = deleteRequest
app.config.globalProperties.$getRequest = getRequest
app.config.globalProperties.$postKeyValueRequest = postKeyValueRequest

app.mount('#app')