import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'
import { useUserStore } from '@/store/userInfo'

axios.defaults.withCredentials = true
axios.defaults.headers.common['X-Requested-With'] = 'XMLHttpRequest'

const expireSession = () => {
    const redirect = router.currentRoute.value.fullPath
    useUserStore().logout()
    if (router.currentRoute.value.path !== '/login') {
        router.replace({ path: '/login', query: { redirect } })
    }
}

// 统一响应处理
axios.interceptors.response.use(success => {
    // 后端统一返回 { success, code, data, message }
    if (success.status && success.status === 200) {
        const resp = success.data

        if (resp?.success === false || (resp?.code && resp.code >= 400)) {
            if (Number(resp.code) === 401) expireSession()
            ElMessage.error(resp.message || '请求失败')
            return Promise.reject(resp)
        }
        return resp
    }
    return success.data
}, error => {
    if (error.response) {
        const status = error.response.status
        switch (status) {
            case 504:
            case 404:
                ElMessage.error('服务器被吃了')
                break
            case 403:
                ElMessage.error('权限不足，请联系管理员')
                break
            case 401:
                ElMessage.error('尚未登录，请登录')
                expireSession()
                break
            default:
                if (error.response.data && error.response.data.message) {
                    ElMessage.error(error.response.data.message)
                } else {
                    ElMessage.error('未知错误!')
                }
        }
    } else {
        ElMessage.error('网络异常，请稍后再试')
    }
    return Promise.reject(error)
})


// Use Vite's same-origin proxy so the browser never makes a cross-origin request.
let base = ''

export const postKeyValueRequest = (url, params) => {
    return axios({
        method: 'post',
        url: `${base}${url}`,
        data: params,
        transformRequest: [function (data) {
            let ret = ''
            for (let i in data) {
                ret += encodeURIComponent(i) + '=' + encodeURIComponent(data[i]) + '&'
            }
            return ret.substring(0, ret.length - 1)
        }],
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        }
    })
}

export const postRequest = (url, params) => {
    return axios({
        method: 'post',
        url: `${base}${url}`,
        data: params
    })
}

export const putRequest = (url, params) => {
    return axios({
        method: 'put',
        url: `${base}${url}`,
        data: params
    })
}

export const getRequest = (url, params) => {
    return axios({
        method: 'get',
        url: `${base}${url}`,
        params
    })
}

export const deleteRequest = (url, params) => {
    return axios({
        method: 'delete',
        url: `${base}${url}`,
        params
    })
}