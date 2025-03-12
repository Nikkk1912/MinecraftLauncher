import axiosInstance from './axiosInstance';

export const installVersion = (version: string) => {
    return axiosInstance.post('/install', null, {
        params: { version }
    });
};