import axios from 'axios';
import { toast } from '@/components/ui/use-toast';

const apiUrl = import.meta.env.VITE_API_URL 
// console.log('API URL:', apiUrl);
const api = axios.create({
    
    baseURL: apiUrl,
    headers: {
        'Content-Type': 'application/json',
    },
});

api.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('authToken');
        if (token && config.headers) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

api.interceptors.response.use(
    (response) => response,
    (error) => {
        console.error('API Error:', error.response?.status, error.message);
        if (error.response?.status === 401) {
            toast({ variant: "destructive", title: "Session Expired", description: "Please log in again." });
            localStorage.removeItem('authToken');
        } else if (error.response?.status === 403) {
            toast({ variant: "destructive", title: "Access Denied", description: "You don't have permission." });
        }
        return Promise.reject(error);
    }
);

export default api;