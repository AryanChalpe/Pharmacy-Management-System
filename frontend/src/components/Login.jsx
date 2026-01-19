import React, { useState, useContext } from 'react';
import axios from 'axios';
import { AuthContext } from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';

const Login = () => {
    const [isLogin, setIsLogin] = useState(true);
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const [successMsg, setSuccessMsg] = useState('');
    const { login } = useContext(AuthContext);
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setSuccessMsg('');

        try {
            if (isLogin) {
                // LOGIN
                const response = await axios.post(`${import.meta.env.VITE_API_BASE_URL}/api/auth/login`, {
                    username,
                    password
                });
                const { token, role } = response.data;
                login({ username, role }, token);
                navigate('/');
            } else {
                // REGISTER ADMIN
                await axios.post(`${import.meta.env.VITE_API_BASE_URL}/api/auth/register?role=ADMIN`, {
                    username,
                    password
                });
                setSuccessMsg('Admin Registered Successfully! Please Login.');
                setIsLogin(true);
                setUsername('');
                setPassword('');
            }
        } catch (err) {
            console.error(err);
            if (err.response && err.response.data) {
                setError(typeof err.response.data === 'string' ? err.response.data : 'Operation failed');
            } else {
                setError('Network Error or Server Down');
            }
        }
    };

    return (
        <div style={{
            display: 'flex',
            justifyContent: 'center',
            alignItems: 'center',
            height: '100vh',
            background: '#F5E7C6', // Skin Tone Background
            fontFamily: "'Outfit', 'Inter', system-ui, sans-serif",
            position: 'relative',
            overflow: 'hidden'
        }}>
            {/* Decorative background shapes for "warm" feel */}
            <div style={{
                position: 'absolute',
                top: '-10%',
                left: '-10%',
                width: '500px',
                height: '500px',
                borderRadius: '50%',
                background: 'radial-gradient(circle, rgba(251, 146, 60, 0.1) 0%, rgba(251, 146, 60, 0) 70%)',
                zIndex: 0
            }}></div>

            <div style={{
                zIndex: 1,
                display: 'flex',
                flexDirection: 'column',
                alignItems: 'center'
            }}>
                {/* Welcome Text Above Box */}
                <h1 style={{
                    color: '#9a3412', // Deep Rust
                    marginBottom: '1.5rem',
                    fontSize: '2.5rem',
                    fontWeight: '900', // Extra bold
                    letterSpacing: '-0.025em',
                    textShadow: '0 4px 10px rgba(154, 52, 18, 0.2)' // Smooth shadow
                }}>
                    Welcome To Pharmacy
                </h1>

                <div style={{
                    padding: '3rem',
                    backgroundColor: '#fff7ed', // Warm Cream
                    borderRadius: '24px',
                    boxShadow: '0 20px 50px rgba(154, 52, 18, 0.15)',
                    width: '400px',
                    transition: 'transform 0.3s ease',
                    border: '1px solid rgba(251, 146, 60, 0.2)'
                }}
                    onMouseOver={(e) => {
                        e.currentTarget.style.transform = 'translateY(-5px)';
                    }}
                    onMouseOut={(e) => {
                        e.currentTarget.style.transform = 'translateY(0)';
                    }}
                >
                    <h2 style={{
                        textAlign: 'center',
                        marginBottom: '0.5rem',
                        color: '#9a3412', // Deep Rust
                        fontWeight: '800',
                        fontSize: '1.8rem'
                    }}>
                        {isLogin ? 'Login' : 'Registration'}
                    </h2>
                    <p style={{ textAlign: 'center', marginBottom: '2rem', color: '#9a3412', opacity: 0.8, fontSize: '1rem', fontWeight: '500' }}>
                        {isLogin ? 'Access your dashboard securely' : 'Join the management team'}
                    </p>

                    {error && <div style={{
                        backgroundColor: '#fee2e2',
                        color: '#991b1b',
                        padding: '1rem',
                        borderRadius: '12px',
                        marginBottom: '1.5rem',
                        fontSize: '0.9rem',
                        textAlign: 'center',
                        border: '1px solid #fecaca',
                        fontWeight: '600'
                    }}>{error}</div>}

                    <form onSubmit={handleSubmit}>
                        <div style={{ marginBottom: '1.25rem' }}>
                            <label style={{ display: 'block', margin: '0 0 0.5rem 0.5rem', fontSize: '0.9rem', color: '#9a3412', fontWeight: '700' }}>Username</label>
                            <input
                                type="text"
                                value={username}
                                onChange={(e) => setUsername(e.target.value)}
                                style={{
                                    width: '100%',
                                    padding: '0.9rem 1.2rem',
                                    border: '1px solid #e2e8f0',
                                    borderRadius: '12px',
                                    outline: 'none',
                                    fontSize: '1rem',
                                    transition: 'all 0.3s ease',
                                    backgroundColor: '#fff',
                                    boxSizing: 'border-box'
                                }}
                                onFocus={(e) => {
                                    e.target.style.borderColor = '#fb923c';
                                    e.target.style.boxShadow = '0 0 0 4px rgba(251, 146, 60, 0.1)';
                                }}
                                onBlur={(e) => {
                                    e.target.style.borderColor = '#e2e8f0';
                                    e.target.style.boxShadow = 'none';
                                }}
                                placeholder="Enter your username"
                                required
                            />
                        </div>
                        <div style={{ marginBottom: '2rem' }}>
                            <label style={{ display: 'block', margin: '0 0 0.5rem 0.5rem', fontSize: '0.9rem', color: '#9a3412', fontWeight: '700' }}>Password</label>
                            <input
                                type="password"
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                                style={{
                                    width: '100%',
                                    padding: '0.9rem 1.2rem',
                                    border: '1px solid #e2e8f0',
                                    borderRadius: '12px',
                                    outline: 'none',
                                    fontSize: '1rem',
                                    transition: 'all 0.3s ease',
                                    backgroundColor: '#fff',
                                    boxSizing: 'border-box'
                                }}
                                onFocus={(e) => {
                                    e.target.style.borderColor = '#fb923c';
                                    e.target.style.boxShadow = '0 0 0 4px rgba(251, 146, 60, 0.1)';
                                }}
                                onBlur={(e) => {
                                    e.target.style.borderColor = '#e2e8f0';
                                    e.target.style.boxShadow = 'none';
                                }}
                                placeholder="Enter your password"
                                required
                            />
                        </div>
                        <button
                            type="submit"
                            style={{
                                width: '100%',
                                padding: '1rem',
                                background: 'linear-gradient(135deg, #fb923c, #ea580c)',
                                color: 'white',
                                border: 'none',
                                borderRadius: '12px',
                                cursor: 'pointer',
                                fontWeight: 'bold',
                                fontSize: '1.1rem',
                                transition: 'all 0.3s ease',
                                boxShadow: '0 4px 15px rgba(234, 88, 12, 0.2)',
                                letterSpacing: '0.5px'
                            }}
                            onMouseOver={(e) => {
                                e.target.style.transform = 'translateY(-2px)';
                                e.target.style.boxShadow = '0 6px 20px rgba(234, 88, 12, 0.3)';
                            }}
                            onMouseOut={(e) => {
                                e.target.style.transform = 'translateY(0)';
                                e.target.style.boxShadow = '0 4px 15px rgba(234, 88, 12, 0.2)';
                            }}
                        >
                            {isLogin ? 'Sign In' : 'Register Admin'}
                        </button>
                    </form>

                    <div style={{ marginTop: '2rem', textAlign: 'center', borderTop: '1px solid #e2e8f0', paddingTop: '1.5rem' }}>
                        <button
                            onClick={() => {
                                setIsLogin(!isLogin);
                                setError('');
                                setSuccessMsg('');
                            }}
                            style={{
                                background: 'none',
                                border: 'none',
                                color: '#9a3412',
                                cursor: 'pointer',
                                fontWeight: '600',
                                fontSize: '0.95rem',
                                transition: 'color 0.2s',
                                display: 'flex',
                                alignItems: 'center',
                                justifyContent: 'center',
                                gap: '0.5rem',
                                margin: '0 auto',
                                opacity: 0.8
                            }}
                            onMouseOver={(e) => e.target.style.opacity = '1'}
                            onMouseOut={(e) => e.target.style.opacity = '0.8'}
                        >
                            {isLogin ? (
                                <>
                                    <span>Need an Admin account?</span>
                                    <span style={{ textDecoration: 'underline' }}>Register</span>
                                </>
                            ) : (
                                <>
                                    <span>← Back to Login</span>
                                </>
                            )}
                        </button>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default Login;
