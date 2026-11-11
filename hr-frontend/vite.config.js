import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

export default defineConfig({
    plugins: [vue()],
    resolve: {
        alias: {
            '@': path.resolve(__dirname, 'src')
        }
    },
    server: {
        port: 5173,
        proxy: {
            // 同时代理带查询参数的 .do 接口，例如图片读取接口。
            '^/.*\\.do(?:\\?.*)?$': {
                target: 'http://localhost:8080',
                changeOrigin: true,
                cookieDomainRewrite: 'localhost'
            },
            '/system': {
                target: 'http://localhost:8080',
                changeOrigin: true,
                cookieDomainRewrite: 'localhost'
            },
            '/hr': {
                target: 'http://localhost:8080',
                changeOrigin: true,
                cookieDomainRewrite: 'localhost'
            },
            '/img': {
                target: 'http://localhost:8080',
                changeOrigin: true
            },
            '/file': {
                target: 'http://localhost:8080',
                changeOrigin: true,
                rewrite: (path) => path.replace(/^\/file/, '')
            }
        }
    }
})