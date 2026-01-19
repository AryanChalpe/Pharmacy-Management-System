import React, { createContext, useState, useEffect } from 'react';

export const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(null);
    const [token, setToken] = useState(sessionStorage.getItem('token'));
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const checkAuth = async () => {
            const authToken = sessionStorage.getItem('token');
            const storedUser = sessionStorage.getItem('user');
            if (authToken) {
                setToken(authToken);
                if (storedUser) setUser(JSON.parse(storedUser));
                // Optionally verify token validity with backend here
            }
            setLoading(false);
        };
        checkAuth();
    }, []);

    const login = (userData, authToken) => {
        setToken(authToken);
        setUser(userData);
        sessionStorage.setItem('token', authToken);
        sessionStorage.setItem('user', JSON.stringify(userData));
    };

    const logout = () => {
        setToken(null);
        setUser(null);
        sessionStorage.removeItem('token');
        sessionStorage.removeItem('user');
    };

    return (
        <AuthContext.Provider value={{ user, token, login, logout }}>
            {children}
        </AuthContext.Provider>
    );
};
