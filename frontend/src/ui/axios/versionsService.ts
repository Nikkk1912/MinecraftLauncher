import axiosInstance from './axiosInstance';

export const getAllVersions = () => {
    return axiosInstance.get('/version/all');
};

export const getInstalledVersions = () => {
    return axiosInstance.get('/version/installed');
};