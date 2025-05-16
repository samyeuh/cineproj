import React, { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { getCinemaById } from '../../api/cinemas';
import { searchProjections, addProjection, Projection } from '../../api/projections';
import { Film, getFilmById, searchFilms } from '../../api/films';
import { useUser } from '../../context/UserContext';
import { FaFilm } from 'react-icons/fa';
import './Cinema.css';

const CinemaDetails: React.FC = () => {
  const { id } = useParams();
  const { user } = useUser();
  const [cinema, setCinema] = useState<any | null>(null);
  const [films, setFilms] = useState<Film[]>([]);
  const [projections, setProjections] = useState<Projection[]>([]);
  const navigate = useNavigate();

  const [newProjection, setNewProjection] = useState<{
    id: string,
    film: Film | undefined,
    dateDebut: string,
    dateFin: string,
    daysOfWeek: string[],
    hour: string,
  }>({
    id: '',
    film: undefined,
    dateDebut: '',
    dateFin: '',
    daysOfWeek: [] as string[],
    hour: '',
    });

  const [loading, setLoading] = useState(true);
  const days = ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday'];

  useEffect(() => {
    const fetchData = async () => {
      if (!id) return;
      try {
        const cinemaData = await getCinemaById(id);
        setCinema(cinemaData);

        const filmRes = await searchFilms({ id: undefined, titre: undefined, lang: undefined, realisateur: undefined, ageMin: undefined, ageMax: undefined, soustitres: undefined, dureeMin: undefined, dureeMax: undefined });
        setFilms(filmRes.results);

        const projRes = await searchProjections({ cinema: id, id: undefined, film: undefined, dateDebut: undefined, dateFin: undefined, daysOfWeek: [], hour: undefined });
        setProjections(projRes.results);
      } catch (err) {
        console.error('Erreur chargement:', err);
      } finally {
        setLoading(false);
      }
    };
    fetchData();
  }, [id]);

  const handleCheckbox = (day: string) => {
    setNewProjection((prev) => ({
      ...prev,
      daysOfWeek: prev.daysOfWeek.includes(day)
        ? prev.daysOfWeek.filter((d) => d !== day)
        : [...prev.daysOfWeek, day],
    }));
  };

  function buildCalendrier(daysOfWeek: string[], hour: string): Record<string, string[]> {
    const calendrier: Record<string, string[]> = {};
    for (const day of daysOfWeek) {
        calendrier[day] = [hour]; // ou plusieurs si besoin
    }
    return calendrier;
  }

  const handleAddProjection = async () => {
    if (!id || !user) return;

    try {
      const selectedCinema = await getCinemaById(id);
      const calendrier = buildCalendrier(newProjection.daysOfWeek, newProjection.hour);
      const payload = {
        id: undefined,
        film: newProjection.film,
        cinema: selectedCinema,
        dateDebut: newProjection.dateDebut,
        dateFin: newProjection.dateFin,
        calendrier, // ✅ construit au lieu de daysOfWeek/hour
     };

      await addProjection(payload as any, user.token);
      alert('Projection ajoutée !');
      setNewProjection({ id: '', film: undefined, dateDebut: '', dateFin: '', daysOfWeek: [], hour: '' });
    } catch (err) {
      console.error('Erreur ajout projection :', err);
    }
  };

  if (loading) return <div>Chargement...</div>;
  if (!cinema) return <div>Cinéma introuvable</div>;

  return (
    <div>
      <div className="main-banner">
        <div className="main-banner-content">
          <FaFilm size={32} style={{ marginRight: 10 }} />
          <span>CineProj</span>
        </div>
      </div>
      <div className="cinema-details-container">
        <button className="film-back-button" onClick={() => navigate(-1)}>← Retour</button>
        <h1>Gestion des projections – {cinema.name}</h1>
        <p><strong>Adresse :</strong> {cinema.address}, {cinema.city}</p>

        <section className="projections-section">
          <h2>Projections existantes</h2>
          {projections.length === 0 ? (
            <p>Aucune projection disponible.</p>
          ) : (
            <ul>
              {projections.map((p) => (
                <li key={p.id}>
                  🎬 <strong>{p.film.titre}</strong> – du {p.dateDebut} au {p.dateFin}<br />
                  📅 {Object.entries(p.calendrier).map(([day, hours]) => (
                    <div key={day}>{day} : {hours.join(', ')}</div>
                  ))}
                </li>
              ))}
            </ul>
          )}
        </section>

        <section className="add-projection-section">
          <h2>Ajouter une projection</h2>
          <label>Film :</label>
          <select value={newProjection.film?.id} onChange={async (e) => {
            const selectedFilm = await getFilmById(e.target.value);
            setNewProjection({ ...newProjection, film: selectedFilm });
          }}>
            <option value="">-- Choisir un film --</option>
            {films.map((f) => (
              <option key={f.id} value={f.id}>{f.titre}</option>
            ))}
          </select>

          <label>Date de début :</label>
          <input type="date" value={newProjection.dateDebut} onChange={(e) => setNewProjection({ ...newProjection, dateDebut: e.target.value })} />

          <label>Date de fin :</label>
          <input type="date" value={newProjection.dateFin} onChange={(e) => setNewProjection({ ...newProjection, dateFin: e.target.value })} />

          <label>Jours de la semaine :</label>
          <div className="days-checkboxes">
            {days.map((day) => (
              <label key={day}>
                <input type="checkbox" checked={newProjection.daysOfWeek.includes(day)} onChange={() => handleCheckbox(day)} />
                {day}
              </label>
            ))}
          </div>

          <label>Heure :</label>
          <input type="time" value={newProjection.hour} onChange={(e) => setNewProjection({ ...newProjection, hour: e.target.value })} />

          <button onClick={handleAddProjection}>Ajouter la projection</button>
        </section>
      </div>
    </div>
  );
};

export default CinemaDetails;
