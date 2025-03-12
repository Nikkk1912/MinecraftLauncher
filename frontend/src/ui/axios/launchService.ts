import axiosInstance from './axiosInstance';

export const launchVersion = (version: string, playerName: string, isOffline: boolean) => {
    return axiosInstance.post('/launch', null, {
        params: {
            version: version,
            playerName: playerName,
            offlineMode: isOffline
        }
    });
};