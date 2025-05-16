import React, { useEffect, useState } from 'react';
import './Dashboard.css';
import { searchFilms, FilmPayload, Film } from '../../api/films.ts';
import { useNavigate } from 'react-router-dom';
import { useUser } from '../../context/UserContext.tsx';
import { FaSearch, FaSignInAlt, FaSignOutAlt, FaUserCircle, FaFilm } from 'react-icons/fa';

const Dashboard: React.FC<{}> = ({}) => {
  const [query, setQuery] = useState('');
  const [userEmail, setUserEmail] = useState<string | null>(null);
  const [results, setResults] = useState<Film[]>([]);
  const navigate = useNavigate();
  const {user, logout} = useUser();

  useEffect(() => {
      setUserEmail(user?.username ?? null);
  }, [user]);

  const handleSearch = async (e: React.FormEvent) => {
    e.preventDefault();
  
    try {
      const payload: FilmPayload = {
        id: undefined,
        titre: query,
        lang: undefined,
        realisateur: undefined,
        ageMin: undefined,
        ageMax: undefined,
        soustitres: undefined,
        dureeMin: undefined,
        dureeMax: undefined,
      };
  
      const response = await searchFilms(payload);
      console.log(response)
      setResults(response.results);
    } catch (error) {
      console.error('Erreur lors de la recherche :', error);
    }
  };

  const handleClickFilm = (r: Film) => {
    navigate(`/films/${r.id}`);
  }

  return (
    <div className="dashboard-bg">
      <div className="main-banner">
        <div className="main-banner-content">
          <FaFilm size={32} style={{ marginRight: 10 }} />
          <span>CineProj</span>
        </div>
      </div>
      <header className="dashboard-header">
        <div />
        <div className="dashboard-user-actions">
          {userEmail ? (
            <>
              <span className="dashboard-user"><FaUserCircle /> {userEmail}</span>
              <button className="logout-btn" onClick={logout} title="Se déconnecter">
                <FaSignOutAlt />
              </button>
            </>
          ) : (
            <button className="login-btn" onClick={() => navigate('/login')} title="Se connecter">
              <FaSignInAlt />
            </button>
          )}
        </div>
      </header>
      <main className="dashboard-main">
        <div className="dashboard-card">
          <h1 className="dashboard-title">Bienvenue sur CineProj <span role="img" aria-label="cinema">🎬</span></h1>
          <p className="dashboard-subtitle">
            Découvrez les films à l'affiche et trouvez votre prochaine séance !
          </p>
          <form className="search-form" onSubmit={handleSearch}>
            <div className="search-input-group">
              <FaSearch className="search-icon" />
              <input
                type="text"
                placeholder="Rechercher un film, un réalisateur..."
                value={query}
                onChange={(e) => setQuery(e.target.value)}
              />
            </div>
            <button type="submit" className="search-btn">Rechercher</button>
          </form>
          {results.length === 0 && query && (
            <div className="dashboard-empty">Aucun film trouvé pour cette recherche.</div>
          )}
          <div className="dashboard-films-list">
            {results.map((r, i) => (
              <div key={i} className="film-card" onClick={() => handleClickFilm(r)}>
                <div className="film-card-img">
                  <FaFilm size={48} />
                </div>
                <div className="film-card-info">
                  <div className="film-card-title">{r.titre}</div>
                  <div className="film-card-meta">{r.dureeEnMinute} min • {r.realisateur}</div>
                </div>
              </div>
            ))}
          </div>
        </div>
      </main>
    </div>
  );
};

export default Dashboard;