import React, { useEffect, useState } from 'react';
import { useUser } from '../../context/UserContext';
import { useNavigate } from 'react-router-dom';
import { searchUsers, deleteUser, UserPayload, User, updateUser } from '../../api/users';
import { searchFilms, deleteFilm, FilmPayload, Film, updateFilm } from '../../api/films';
import { FaFilm } from 'react-icons/fa';
import './AdminPanel.css';

const AdminPanel: React.FC = () => {
  const { user, logout } = useUser();
  const navigate = useNavigate();
  const [users, setUsers] = useState<any[]>([]);
  const [films, setFilms] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);

  const [showUserModal, setShowUserModal] = useState(false);
  const [editingUser, setEditingUser] = useState<User | null>(null);

  const [showFilmModal, setShowFilmModal] = useState(false);
  const [editingFilm, setEditingFilm] = useState<Film | null>(null);
  const [refresh, setRefresh] = useState(false);

  useEffect(() => {
    if (!user || !user.isAdmin) {
      navigate('/');
      return;
    }

    const fetchData = async () => {
      try {
         const userload: UserPayload = {
            id: undefined,
            username: undefined,
            email: undefined,
            isAdmin: undefined,
            isCinema: undefined,
            createdBefore: undefined,
            createdAfter: undefined,
        }
        const usersRes = (await searchUsers(userload)).results;

        const filmload: FilmPayload = {
            id: undefined,
            titre: undefined,
            lang: undefined,
            realisateur: undefined,
            ageMin: undefined,
            ageMax: undefined,
            soustitres: undefined,
            dureeMin: undefined,
            dureeMax: undefined,
        };
        const filmsRes = (await searchFilms(filmload)).results;

        setUsers(usersRes);
        setFilms(filmsRes);
      } catch (err) {
        console.error('Erreur chargement admin :', err);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [user, navigate, refresh]);

  const handleDeleteUser = async (id: string) => {
    if (!window.confirm('Supprimer cet utilisateur ?')) return;
    try {
      if (!user) throw new Error('User is undefined');
      await deleteUser(id, user.token);
      setUsers(users.filter((u) => u.id !== id));
    } catch (err) {
      console.error('Erreur suppression utilisateur :', err);
    }
  };

  const handleDeleteFilm = async (id: string) => {
    if (!window.confirm('Supprimer ce film ?')) return;
    try {
      if (!user) throw new Error('User is undefined');
      await deleteFilm(id, user.token);
      setFilms(films.filter((f) => f.id !== id));
    } catch (err) {
      console.error('Erreur suppression film :', err);
    }
  };

  const openUserModal = (user?: any) => {
    if (user) {
      setEditingUser({
        id: user.id,
        username: user.username,
        email: user.email,
        admin: user.admin,
        cinema: user.cinema,
        createdAt: user.createdAt,
      });
    } else {
      setEditingUser({ id: '', username: '', email: '', admin: false, cinema: false, createdAt: Date.now().toLocaleString() });
    }
    setShowUserModal(true);
  };

  const openFilmModal = (film?: any) => {
    if (film) {
      setEditingFilm({
        id: film.id,
        titre: film.titre,
        lang: film.lang,
        dureeEnMinute: film.dureeEnMinute,
        soustitres: film.soustitres,
        realisateur: film.realisateur,
        ageMin: film.ageMin,
        acteurs: film.acteurs,

      });
    } else {
      setEditingFilm({
        id: '',
        titre: '',
        lang: '',
        dureeEnMinute: 0,
        soustitres: '',
        realisateur: '',
        ageMin: 0,
        acteurs: [],
      });
    }
    setShowFilmModal(true);
  };

  const closeModals = () => {
    setShowUserModal(false);
    setShowFilmModal(false);
    setEditingUser(null);
    setEditingFilm(null);
  };

  const handleUserFormSubmit = async () => {
    if (!editingUser || !user) return;
    await updateUser(editingUser, user.token);
    setRefresh(!refresh);
    closeModals();
  };

  const handleFilmFormSubmit = async () => {
    if (!editingFilm || !user) return;
    await updateFilm(editingFilm, user.token);
    setRefresh(!refresh);
    closeModals();
  };

  if (loading) return <div className="admin-loading">Chargement...</div>;

  return (
    <div>
      <div className="main-banner">
        <div className="main-banner-content">
          <FaFilm size={32} style={{ marginRight: 10 }} />
          <span>CineProj</span>
        </div>
      </div>
      <div className="admin-panel">
        <h1>🛠️ Panneau d'administration</h1>

        <section>
          <div className="admin-header">
            <h2>Utilisateurs</h2>
          </div>
          <table className="admin-table">
            <thead>
              <tr>
                <th>Nom</th>
                <th>Email</th>
                <th>Admin</th>
                <th>Cinéma</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {users.map((u) => (
                <tr key={u.id}>
                  <td>{u.username}</td>
                  <td>{u.email}</td>
                  <td>{u.admin ? '✅' : '❌'}</td>
                  <td>{u.cinema ? '✅' : '❌'}</td>
                  <td>
                    <button onClick={() => openUserModal(u)}>✏️</button>
                    <button onClick={() => handleDeleteUser(u.id)}>🗑️</button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </section>

        <section>
          <div className="admin-header">
            <h2>Films</h2>
            <button className="admin-add-btn" onClick={() => openFilmModal()}>➕ Ajouter</button>
          </div>
          <ul className="film-list">
            {films.map((f) => (
              <li key={f.id}>
                🎬 <strong>{f.titre}</strong> ({f.lang}) – {f.dureeEnMinute} min
                <span className="film-actions">
                  <button onClick={() => openFilmModal(f)}>✏️</button>
                  <button onClick={() => handleDeleteFilm(f.id)}>🗑️</button>
                </span>
              </li>
            ))}
          </ul>
        </section>

        {/* USER MODAL */}
        {showUserModal && editingUser && (
          <div className="modal">
            <div className="modal-content">
              <h3>{editingUser.id ? 'Modifier un utilisateur' : 'Ajouter un utilisateur'}</h3>
              <label>Nom d'utilisateur</label>
              <input
                value={editingUser.username}
                onChange={(e) => setEditingUser({ ...editingUser, username: e.target.value })}
              />
              <label>Email</label>
              <input
                value={editingUser.email}
                onChange={(e) => setEditingUser({ ...editingUser, email: e.target.value })}
              />
              <label>
                <input
                  type="checkbox"
                  checked={editingUser.admin}
                  onChange={(e) => setEditingUser({ ...editingUser, admin: e.target.checked })}
                />
                Admin
              </label>
              <label>
                <input
                  type="checkbox"
                  checked={editingUser.cinema}
                  onChange={(e) => setEditingUser({ ...editingUser, cinema: e.target.checked })}
                />
                Gérant cinéma
              </label>
              <div className="modal-actions">
                <button onClick={handleUserFormSubmit}>✅ Enregistrer</button>
                <button onClick={closeModals}>Annuler</button>
              </div>
            </div>
          </div>
        )}

        {/* FILM MODAL */}
        {showFilmModal && editingFilm && (
          <div className="modal">
              <div className="modal-content">
              <h3>{editingFilm.id ? 'Modifier un film' : 'Ajouter un film'}</h3>

              <label>Titre</label>
              <input
                  value={editingFilm.titre}
                  onChange={(e) => setEditingFilm({ ...editingFilm, titre: e.target.value })}
              />

              <label>Langue</label>
              <input
                  value={editingFilm.lang}
                  onChange={(e) => setEditingFilm({ ...editingFilm, lang: e.target.value })}
              />

              <label>Sous-titres</label>
              <input
                  value={editingFilm.soustitres || ''}
                  onChange={(e) => setEditingFilm({ ...editingFilm, soustitres: e.target.value })}
              />

              <label>Réalisateur</label>
              <input
                  value={editingFilm.realisateur || ''}
                  onChange={(e) => setEditingFilm({ ...editingFilm, realisateur: e.target.value })}
              />

              <label>Acteurs (séparés par des virgules)</label>
              <input
                  value={editingFilm.acteurs?.join(', ') || ''}
                  onChange={(e) =>
                  setEditingFilm({
                      ...editingFilm,
                      acteurs: e.target.value.split(',').map((a) => a.trim()),
                  })
                  }
              />

              <label>Âge minimum</label>
              <input
                  type="number"
                  min="0"
                  value={editingFilm.ageMin ?? 0}
                  onChange={(e) =>
                  setEditingFilm({ ...editingFilm, ageMin: parseInt(e.target.value) || 0 })
                  }
              />

              <label>Durée (min)</label>
              <input
                  type="number"
                  min="1"
                  value={editingFilm.dureeEnMinute}
                  onChange={(e) =>
                  setEditingFilm({ ...editingFilm, dureeEnMinute: parseInt(e.target.value) || 0 })
                  }
              />

              <div className="modal-actions">
                  <button onClick={handleFilmFormSubmit}>✅ Enregistrer</button>
                  <button onClick={closeModals}>Annuler</button>
              </div>
              </div>
          </div>
        )}

        <button className="logout-btn" onClick={logout}>
          Se déconnecter
        </button>
      </div>
    </div>
  );
};

export default AdminPanel;

