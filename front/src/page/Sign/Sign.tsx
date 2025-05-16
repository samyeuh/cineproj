import React, { useState } from 'react';
import { login, register } from '../../api/auth.ts';
import { me } from '../../api/users.ts';
import './Sign.css';
import { useNavigate } from 'react-router-dom';
import { useUser } from '../../context/UserContext.tsx';
import { FaFilm } from 'react-icons/fa';

const Sign: React.FC = () => {
  const [mode, setMode] = useState<'login' | 'register'>('login');
  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const navigate = useNavigate();
  const { login: loginContext } = useUser();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (mode == 'login') {
      const res = await login({ username, email, password });
      const user = await me(res.token);
      
      if (!user) {
        console.error('User not found');
        return;
      }

      loginContext({
        id: user.id,
        username: user.username,
        email: user.email,
        token: res.token,
        isCinema: user.cinema,
        isAdmin: user.admin,
      })

      if (user.admin) {
        navigate('/admin');
        return;
      }

      if (user.cinema) {
        navigate('/cinemas');
        return;
      } 
      
      navigate('/');
      
    } else if (mode == 'register') {
      await register({ username, email, password });
      setMode('login');
    } else {
      console.error('Unknown mode');
    }
  };

  return (
    <div>
      <div className="main-banner">
        <div className="main-banner-content">
          <FaFilm size={32} style={{ marginRight: 10 }} />
          <span>CineProj</span>
        </div>
      </div>
      <div className="sign-container">
        <h2 className="sign-title">
          {mode === 'login' ? 'Connectez-vous' : "S'inscire"}
        </h2>

        <form className="sign-form" onSubmit={handleSubmit}>
          <label>
            Nom d'utilisateur
            <input
              type="text"
              value={username}
              required
              onChange={(e) => setUsername(e.target.value)}
              placeholder="superadmin"
            />
          </label>

          {mode === 'register' && (
            <label>
              Adresse e-mail
              <input
                type="email"
                value={email}
                required
                onChange={(e) => setEmail(e.target.value)}
                placeholder="salim@efrei.fr"
              />
            </label>
          )}

          <label>
            Mot de passe
            <input
              type="password"
              value={password}
              required
              onChange={(e) => setPassword(e.target.value)}
              placeholder="••••••••"
            />
          </label>

          <button type="submit">
            {mode === 'login' ? 'Login' : 'Register'}
          </button>
        </form>

        <p className="sign-switch">
          {mode === 'login' ? "Vous n'avez pas de compte ?" : 'Vous avez déjà un compte ?'}{' '}
          <span onClick={() => setMode(mode === 'login' ? 'register' : 'login')}>
            {mode === 'login' ? "S'inscire" : 'Se connecter'}
          </span>
        </p>
        <p className="sign-switch">
          Voir uniquement les films et projections ? {' '}
          <span onClick={() => navigate('/')}>
               Cliquez ici
          </span>
        </p>
      </div>
    </div>
  );
};

export default Sign;
