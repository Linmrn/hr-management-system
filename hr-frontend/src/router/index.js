import { createRouter, createWebHashHistory } from 'vue-router'
import Login from '@/views/Login.vue'
import Home from '@/views/Home.vue'
import Dashboard from '@/views/Dashboard.vue'
import { useUserStore, isAdminUser, isSystemManagementPath } from '@/store/userInfo'

const routes = [
    {
        path: '/login',
        name: 'Login',
        component: Login,
        meta: { requireAuth: false }
    },
    {
        path: '/register',
        redirect: '/login?mode=register',
        meta: { requireAuth: false }
    },
    {
        path: '/home',
        component: Home,
        name: 'Home',
        meta: { requireAuth: true },
        redirect: '/home/dashboard',
        children: [
            {
                path: 'dashboard',
                name: '仪表盘',
                component: Dashboard,
                meta: { requireAuth: true }
            },
            {
                path: 'hrinfo',
                name: 'HrInfo',
                component: () => import('@/views/HrInfo.vue'),
                meta: { requireAuth: true, hidden: true }
            },
            {
                path: 'emp/basic',
                name: 'EmpBasic',
                component: () => import('@/views/emp/EmpBasic.vue'),
                meta: { requireAuth: true }
            },
            {
                path: 'emp/adv',
                name: 'EmpAdv',
                component: () => import('@/views/emp/EmpAdv.vue'),
                meta: { requireAuth: true }
            },
            {
                path: 'per/emp',
                name: 'PerEmp',
                component: () => import('@/views/per/PerEmp.vue'),
                meta: { requireAuth: true }
            },
            {
                path: 'per/ec',
                name: 'PerEc',
                component: () => import('@/views/per/PerEc.vue'),
                meta: { requireAuth: true }
            },
            {
                path: 'per/train',
                name: 'PerTrain',
                component: () => import('@/views/per/PerTrain.vue'),
                meta: { requireAuth: true }
            },
            {
                path: 'per/salary',
                name: 'PerSalary',
                component: () => import('@/views/per/PerSalary.vue'),
                meta: { requireAuth: true }
            },
            {
                path: 'per/mv',
                name: 'PerMv',
                component: () => import('@/views/per/PerMv.vue'),
                meta: { requireAuth: true }
            },
            {
                path: 'sal/sob',
                name: 'SalSob',
                component: () => import('@/views/sal/SalSob.vue'),
                meta: { requireAuth: true }
            },
            {
                path: 'sal/sobcfg',
                name: 'SalSobCfg',
                component: () => import('@/views/sal/SalSobCfg.vue'),
                meta: { requireAuth: true }
            },
            {
                path: 'sal/table',
                name: 'SalTable',
                component: () => import('@/views/sal/SalTable.vue'),
                meta: { requireAuth: true }
            },
            {
                path: 'sal/month',
                name: 'SalMonth',
                component: () => import('@/views/sal/SalMonth.vue'),
                meta: { requireAuth: true }
            },
            {
                path: 'sal/search',
                name: 'SalSearch',
                component: () => import('@/views/sal/SalSearch.vue'),
                meta: { requireAuth: true }
            },
            {
                path: 'sta/all',
                name: 'StaAll',
                component: () => import('@/views/sta/StaAll.vue'),
                meta: { requireAuth: true }
            },
            {
                path: 'sta/score',
                name: 'StaScore',
                component: () => import('@/views/sta/StaScore.vue'),
                meta: { requireAuth: true }
            },
            {
                path: 'sta/pers',
                name: 'StaPers',
                component: () => import('@/views/sta/StaPers.vue'),
                meta: { requireAuth: true }
            },
            {
                path: 'sta/record',
                name: 'StaRecord',
                component: () => import('@/views/sta/StaRecord.vue'),
                meta: { requireAuth: true }
            },
            {
                path: 'sys/basic',
                name: 'SysBasic',
                component: () => import('@/views/sys/SysBasic.vue'),
                meta: { requireAuth: true, adminOnly: true }
            },
            {
                path: 'sys/log',
                name: 'SysLog',
                component: () => import('@/views/sys/SysLog.vue'),
                meta: { requireAuth: true, adminOnly: true }
            },
            {
                path: 'sys/hr',
                name: 'SysHr',
                component: () => import('@/views/sys/SysHr.vue'),
                meta: { requireAuth: true, adminOnly: true }
            },

        ]
    },
    {
        path: '/per/emp',
        redirect: '/home/per/emp'
    },
    {
        path: '/hrinfo',
        redirect: '/home/hrinfo'
    },
    {
        path: '/',
        redirect: '/home'
    }
]

const router = createRouter({
    history: createWebHashHistory(),
    routes
})

router.beforeEach(async (to, from, next) => {
    if (to.meta?.requireAuth === false || to.name === 'Login') return next()
    const userStore = useUserStore()
    const user = userStore.user || (() => {
        const savedUser = sessionStorage.getItem('user')
        return savedUser ? JSON.parse(savedUser) : null
    })()
    if (!user) return next({ path: '/login', query: { redirect: to.fullPath } })
    if (to.meta?.adminOnly && !isAdminUser(user)) return next({ path: '/home/dashboard' })
    if (isSystemManagementPath(to.path) && !isAdminUser(user)) return next({ path: '/home/dashboard' })
    try { const { initMenu } = await import('@/utils/menus'); await initMenu(router) } catch (e) { console.error('菜单初始化失败', e) }
    if (!userStore.user) return next({ path: '/login', query: { redirect: to.fullPath } })
    next()
})

export default router
