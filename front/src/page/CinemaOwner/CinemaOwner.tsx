import React, { useEffect, useState } from 'react';
import { useUser } from '../../context/UserContext';
import { useNavigate } from 'react-router-dom';
import { searchCinemas, Cinema, CinemaPayload, add } from '../../api/cinemas';
import '../CinemaDetails/Cinema.css';

const CinemaOwner: React.FC = () => {
  const { user, logout } = useUser();
  const [cinemas, setCinemas] = useState<Cinema[]>([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [newCinema, setNewCinema] = useState<{ name: string; address: string; city: string }>({
    name: '',
    address: '',
    city: ''
  });
  const navigate = useNavigate();

  useEffect(() => {
    if (!user) {
      navigate('/login');
      return;
    }
    if (!user.isCinema) {
      navigate('/');
      return;
    }
    fetchCinemas();
  }, [user]);

  const fetchCinemas = async () => {
    if (!user) return;
    try {
      const payload: CinemaPayload = {
        id: undefined,
        name: undefined,
        address: undefined,
        city: undefined,
        owner_id: user.id
      };
      const response = await searchCinemas(payload);
      setCinemas(response.results);
    } catch (error) {
      console.error('Erreur chargement cinémas:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleCinemaClick = (cinemaId: string) => {
    navigate(`/cinemas/${cinemaId}`);
  };

  const handleAddCinema = async () => {
    if (!user) return;
    try {
      const payload: CinemaPayload = {
        id: undefined,
        name: newCinema.name,
        address: newCinema.address,
        city: newCinema.city,
        owner_id: user.id
      };
      await add(payload, user.token);
      setShowModal(false);
      setNewCinema({ name: '', address: '', city: '' });
      fetchCinemas();
    } catch (err) {
      console.error('Erreur ajout cinéma :', err);
      alert('Erreur lors de l’ajout.');
    }
  };

  if (loading) {
    return <p>Chargement...</p>;
  }

  return (
    <div className="owner-cinemas-page">
      <h1>Mes cinémas</h1>
      <button className="add-cinema-btn" onClick={() => setShowModal(true)}>
        ➕ Ajouter un cinéma
      </button>

      {cinemas.length === 0 ? (
        <p>Aucun cinéma trouvé.</p>
      ) : (
        <ul className="cinema-list">
          {cinemas.map((cinema) => (
            <li
              key={cinema.id}
              onClick={() => handleCinemaClick(cinema.id)}
              className="cinema-item"
              style={{ cursor: 'pointer', padding: '10px', borderBottom: '1px solid #ccc' }}
            >
              <strong>{cinema.name}</strong> - {cinema.city}
            </li>
          ))}
        </ul>
      )}

      <button className="logout-btn" onClick={logout}>
        Se déconnecter
      </button>

      {/* MODAL */}
      {showModal && (
        <div className="modal">
          <div className="modal-content">
            <h3>Ajouter un cinéma</h3>
            <label>Nom</label>
            <input
              type="text"
              value={newCinema.name}
              onChange={(e) => setNewCinema({ ...newCinema, name: e.target.value })}
            />
            <label>Adresse</label>
            <input
              type="text"
              value={newCinema.address}
              onChange={(e) => setNewCinema({ ...newCinema, address: e.target.value })}
            />
            <label>Ville</label>
            <input
              type="text"
              value={newCinema.city}
              onChange={(e) => setNewCinema({ ...newCinema, city: e.target.value })}
            />
            <div className="modal-actions">
              <button onClick={handleAddCinema}>✅ Ajouter</button>
              <button onClick={() => setShowModal(false)}>Annuler</button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default CinemaOwner;
