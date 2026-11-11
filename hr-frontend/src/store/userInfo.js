import { defineStore } from 'pinia'

const SYSTEM_MANAGEMENT_ROUTE_PREFIXES = ['/home/sys', '/sys/']

export const resolveUserface = (userface, version = Date.now()) => {
    if (!userface) return ''

    const source = String(userface).trim().replace(/\\/g, '/')
    if (!source || source.startsWith('data:') || source.startsWith('blob:')) return source

    if (/^(https?:)?\/\//.test(source)) {
        const separator = source.includes('?') ? '&' : '?'
        return `${source}${separator}avatarVersion=${version}`
    }

    const normalizedPath = source.startsWith('/') ? source : `/${source}`
    const avatarPath = normalizedPath.startsWith('/file/') || normalizedPath.startsWith('/img/')
        ? normalizedPath
        : `/file${normalizedPath}`
    const separator = avatarPath.includes('?') ? '&' : '?'
    return `${avatarPath}${separator}avatarVersion=${version}`
}

export const isAdminUser = (user) =>
    Array.isArray(user?.roles) && user.roles.some(role => role.name === 'ROLE_admin')

export const canAccessSystemManagement = (user) => isAdminUser(user)

export const isSystemManagementPath = (path = '') =>
    SYSTEM_MANAGEMENT_ROUTE_PREFIXES.some(prefix => path === prefix.replace(/\/$/, '') || path.startsWith(prefix))

export const useUserStore = defineStore('user', {
    state: () => ({
        user: null,
        menus: [],
        token: ''
    }),

    actions: {
        setUser(user) {
            this.user = user
            sessionStorage.setItem('user', JSON.stringify(user))
        },

        setMenus(menus) {
            this.menus = menus
        },

        logout() {
            this.user = null
            this.menus = []
            this.token = ''
            sessionStorage.clear()
        },

        restoreSession() {
            const savedUser = sessionStorage.getItem('user')
            if (savedUser) {
                try {
                    this.user = JSON.parse(savedUser)
                } catch (e) {
                    this.user = null
                }
            }
        }
    }
})