import request from '../utils/request.js';

export const listCrewRequest = (params) => {
    return request.get('/aircrew-server/crew/listCrew', { params });
}

export const addCrewRequest = (data) => {
    return request.post('/aircrew-server/crew/addCrew', data);
}

export const updateCrewRequest = (data) => {
    return request.put('/aircrew-server/crew/updateCrew', data);
}

export const deleteCrewRequest = (ids) => {
    return request.delete(`/aircrew-server/crew/deleteCrew/${ids.join(',')}`);
}

export const listCrewGroupRequest = (params) => {
    return request.get('/aircrew-server/crewGroup/list', { params });
}

export const addCrewGroupRequest = (data) => {
    return request.post('/aircrew-server/crewGroup/add', data);
}

export const updateCrewGroupRequest = (data) => {
    return request.put('/aircrew-server/crewGroup/update', data);
}

export const deleteCrewGroupRequest = (ids) => {
    return request.delete(`/aircrew-server/crewGroup/delete/${ids}`);
}

export const getCityListRequest = () => {
    return request.get('/aircrew-server/crewGroup/cityDetail');
}
/**
 * @description: 查询机组小队的人员组成信息
 * @param {*} id 小队ID
 * @return {*}
 */
export const getCrewGroupMapRequest = (id) => {
    return request.get('/aircrew-server/crewGroup/groupDetailById', { params: { id } });
}
/**
 * @description: 查询机组人员组成信息
 * @return {*}
 */
export const getCrewListForSelectRequest = () => {
    return request.get('/aircrew-server/crewGroup/crewList');
}

export const getGroupMemberDetailRequest = (id) => {
    return request.get('/aircrew-server/crewGroup/groupMemberDetail', { params: { id } });
}

export const listCrewScheduleRequest = (params) => {
    return request.get('/aircrew-server/crewSchedule/list', { params });
}

export const addCrewScheduleRequest = (data) => {
    return request.put('/aircrew-server/crewSchedule/add', data);
}

export const updateCrewScheduleRequest = (data) => {
    return request.post('/aircrew-server/crewSchedule/update', data);
}

export const deleteCrewScheduleRequest = (ids) => {
    return request.delete(`/aircrew-server/crewSchedule/delete/${ids.join(',')}`);
}

export const getCrewScheduleDetailRequest = (ids) => {
    return request.get('/aircrew-server/crewSchedule/getDetail', { params: { ids } });
}

export const getCrewByPositionRequest = (columnName) => {
    return request.get('/aircrew-server/crew/crewListByPosition', { params: { columnName } });
}