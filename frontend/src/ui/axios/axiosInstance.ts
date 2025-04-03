import axios from 'axios';

// Java spring port
// const port = 8000;

//C sharp ASP
const port = 5123;

const axiosInstance = axios.create({
    baseURL: `http://localhost:${port}/api`,
    headers: {
        'Content-Type': 'application/json',
    },
});

axiosInstance.interceptors.request.use(
    (config) => {
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

axiosInstance.interceptors.response.use(
    (response) => {
        return response;
    },
    (error) => {
        return Promise.reject(error);
    }
);

export default axiosInstance;
