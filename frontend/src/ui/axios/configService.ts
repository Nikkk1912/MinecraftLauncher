import axiosInstance from './axiosInstance';

export const getLastLaunchedVersion = () => {
    return axiosInstance.get('/config/lastLaunchedVersion');
};

export const getPlayerName = () => {
    return axiosInstance.get('/config/playerName');
};


export const getMinRamValue = () => {
    return axiosInstance.get('/config/minRam');
}

export const getMaxRamValue = () => {
    return axiosInstance.get('/config/maxRam');
}

export const getLauncherFolderPath = () => {
    return axiosInstance.get('/config/minecraft_home');
}

export const saveSetting = (key: string, value: string) => {
    return axiosInstance.post('/config', null, {
        params: {
            key,
            value
        }
    });
}