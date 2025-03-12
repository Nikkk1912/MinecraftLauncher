import axiosInstance from './axiosInstance';

export const getLastLaunchedVersion = () => {
    return axiosInstance.get('/config/lastLaunchedVersion');
};

export const getPlayerName = () => {
    return axiosInstance.get('/config/playerName');
};

