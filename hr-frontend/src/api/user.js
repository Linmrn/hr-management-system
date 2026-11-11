import { getRequest, postRequest, putRequest, deleteRequest } from '@/utils/api'

export const login = (data) => postRequest('/doLogin.do', data)
export const getCurrentUser = () => getRequest('/hr/info.do')
export const updatePassword = (data) => putRequest('/hr/pass.do', data)
export const updateHrInfo = (data) => putRequest('/hr/info/update.do', data)
export const updateUserface = (formData) => postRequest('/hr/userface.do', formData)
export const getHrList = (params) => getRequest('/system/hr/list.do', params)
export const addHr = (data) => postRequest('/system/hr/add.do', data)
export const updateHr = (data) => putRequest('/system/hr/update.do', data)
export const deleteHr = (id) => deleteRequest('/system/hr/delete.do', { id })
export const getAllRoles = () => getRequest('/system/hr/roles.do')
export const updateHrRole = (hrid, rids) => putRequest('/system/hr/role.do', { hrid, rids })