import { getRequest } from "./api"
import { useUserStore, canAccessSystemManagement, isSystemManagementPath } from "@/store/userInfo"

const componentMap = {
    'Dashboard': () => import('@/views/Dashboard.vue'),
    'EmpBasic': () => import('@/views/emp/EmpBasic.vue'),
    'EmpAdv': () => import('@/views/emp/EmpAdv.vue'),
    'PerEmp': () => import('@/views/per/PerEmp.vue'),
    'PerEc': () => import('@/views/per/PerEc.vue'),
    'PerTrain': () => import('@/views/per/PerTrain.vue'),
    'PerAppraise': () => import('@/views/per/PerAppraise.vue'),
    'PerSalary': () => import('@/views/per/PerSalary.vue'),
    'PerMv': () => import('@/views/per/PerMv.vue'),
    'SalSob': () => import('@/views/sal/SalSob.vue'),
    'SalSobCfg': () => import('@/views/sal/SalSobCfg.vue'),
    'SalTable': () => import('@/views/sal/SalTable.vue'),
    'SalSearch': () => import('@/views/sal/SalSearch.vue'),
    'StaAll': () => import('@/views/sta/StaAll.vue'),
    'StaScore': () => import('@/views/sta/StaScore.vue'),
    'StaPers': () => import('@/views/sta/StaPers.vue'),
    'StaRecord': () => import('@/views/sta/StaRecord.vue'),
    'SysBasic': () => import('@/views/sys/SysBasic.vue'),
    'SysLog': () => import('@/views/sys/SysLog.vue'),
    'SysHr': () => import('@/views/sys/SysHr.vue'),
    'HrInfo': () => import('@/views/HrInfo.vue'),
}

const getStaticMenus = () => [
    { id: 1, name: '系统首页', iconCls: 'fa fa-home', path: '/home/dashboard', component: 'Dashboard', children: [] },
    { id: 2, name: '员工资料', iconCls: 'fa fa-user-circle-o', path: '/', component: null, children: [{ id: 7, name: '基本资料', path: '/home/emp/basic', component: 'EmpBasic', iconCls: null }] },
    { id: 3, name: '人事管理', iconCls: 'fa fa-address-card-o', path: '/', component: null, children: [{ id: 9, name: '员工资料', path: '/home/per/emp', component: 'PerEmp', iconCls: null }, { id: 10, name: '员工奖惩', path: '/home/per/ec', component: 'PerEc', iconCls: null }, { id: 11, name: '员工培训', path: '/home/per/train', component: 'PerTrain', iconCls: null }, { id: 12, name: '员工调薪', path: '/home/per/salary', component: 'PerSalary', iconCls: null }, { id: 13, name: '员工调动', path: '/home/per/mv', component: 'PerMv', iconCls: null }] },
    { id: 4, name: '薪资管理', iconCls: 'fa fa-money', path: '/', component: null, children: [{ id: 14, name: '工资账套管理', path: '/home/sal/sob', component: 'SalSob', iconCls: null }, { id: 15, name: '员工账套设置', path: '/home/sal/sobcfg', component: 'SalSobCfg', iconCls: null }, { id: 16, name: '工资表管理', path: '/home/sal/table', component: 'SalTable', iconCls: null }, { id: 18, name: '工资表查询', path: '/home/sal/search', component: 'SalSearch', iconCls: null }] },
    { id: 5, name: '统计管理', iconCls: 'fa fa-bar-chart', path: '/', component: null, children: [{ id: 19, name: '综合信息统计', path: '/home/sta/all', component: 'StaAll', iconCls: null }, { id: 20, name: '员工积分统计', path: '/home/sta/score', component: 'StaScore', iconCls: null }, { id: 21, name: '人事信息统计', path: '/home/sta/pers', component: 'StaPers', iconCls: null }, { id: 22, name: '人事记录统计', path: '/home/sta/record', component: 'StaRecord', iconCls: null }] },
    { id: 6, name: '系统管理', iconCls: 'fa fa-windows', path: '/', component: null, children: [{ id: 23, name: '基础信息设置', path: '/home/sys/basic', component: 'SysBasic', iconCls: null }, { id: 25, name: '操作日志管理', path: '/home/sys/log', component: 'SysLog', iconCls: null }, { id: 26, name: '操作员管理', path: '/home/sys/hr', component: 'SysHr', iconCls: null }] }
]

export const formatRoutes = (routes) => routes.flatMap(route => {
    const children = formatRoutes(route.children || [])
    const component = componentMap[route.component]
    if (!component && children.length === 0) return []
    const path = component && route.path && !route.path.startsWith('/home/')
        ? `/home/${route.path.replace(/^\//, '')}` : route.path
    return [{ path, name: route.name, iconCls: route.iconCls || '', component: component || null,
        meta: { ...route.meta, requireAuth: true }, children }]
})

const getHomeRouteName = (router) => router.getRoutes().find(r => r.name === 'Home')?.name || 'Home'

const filterMenus = (menus) => {
    const removedNames = new Set(['月末处理', '权限组'])
    return (menus || [])
        .filter(menu => !removedNames.has(menu.name))
        .map(menu => ({
            ...menu,
            children: menu.children ? filterMenus(menu.children) : []
        }))
}

const filterSystemManagementMenus = (menus) => {
    return (menus || [])
        .map(menu => {
            if (!menu.children || menu.children.length === 0) {
                return menu
            }
            const children = menu.children.filter(child => !isSystemManagementPath(child.path))
            return {
                ...menu,
                children
            }
        })
        .filter(menu => menu.name !== '系统管理' || menu.children.length > 0)
}

export const initMenu = async (router) => {
    const userStore = useUserStore()
    let menus = []
    try {
        const response = await getRequest("/system/config/menu.do")
        if (response && response.data && Array.isArray(response.data)) menus = response.data
        else throw new Error('菜单数据为空')
    } catch (e) {
        if (Number(e?.code || e?.response?.status) === 401) throw e
        console.warn('使用静态菜单数据')
        menus = getStaticMenus()
    }
    menus = filterMenus(menus)
    if (!canAccessSystemManagement(userStore.user)) {
        menus = filterSystemManagementMenus(menus)
    }
    const fmtRoutes = formatRoutes(menus)
    const parentName = getHomeRouteName(router)
    // Preserve declared routes and their authentication metadata.
    const existingPaths = new Set(router.getRoutes().map(route => route.path))
    fmtRoutes.forEach(firstLevel => {
        if (firstLevel.children && firstLevel.children.length > 0) {
            firstLevel.children.forEach(child => { if (child.path && child.path !== '/' && !existingPaths.has(child.path)) { const routeConfig = { ...child }; if (child.path.startsWith('/home/')) routeConfig.path = child.path.substring(6); else if (child.path.startsWith('/')) routeConfig.path = child.path.substring(1); router.addRoute(parentName, routeConfig) } })
        } else if (firstLevel.component && firstLevel.path && firstLevel.path !== '/' && !existingPaths.has(firstLevel.path)) {
            const routeConfig = { ...firstLevel }
            if (firstLevel.path.startsWith('/home/')) { routeConfig.path = firstLevel.path.substring(6); router.addRoute(parentName, routeConfig) } else if (firstLevel.path.startsWith('/')) { router.addRoute(routeConfig) } else { router.addRoute(parentName, routeConfig) }
        }
    })
    userStore.setMenus(fmtRoutes)
}
