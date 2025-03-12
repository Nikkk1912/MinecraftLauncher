import axios from 'axios';

// Create a new Axios instance
const axiosInstance = axios.create({
    baseURL: 'http://localhost:8000/api', // Set base URL for your API
    headers: {
        'Content-Type': 'application/json',
        // Add any default headers here
    },
});

// Optionally, you can set up interceptors (request/response) here as well
axiosInstance.interceptors.request.use(
    (config) => {
        // You can add a token or any custom logic before request is sent
        return config;
    },
    (error) => {
        // Handle errors globally if needed
        return Promise.reject(error);
    }
);

axiosInstance.interceptors.response.use(
    (response) => {
        // Handle responses globally if needed
        return response;
    },
    (error) => {
        // Handle response errors globally
        return Promise.reject(error);
    }
);

export default axiosInstance;
