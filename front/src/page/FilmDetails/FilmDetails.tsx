import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import './FilmDetails.css';
import { getFilmById } from '../../api/films';
import { searchProjections, Projection } from '../../api/projections';
import { searchCinemas, Cinema, CinemaPayload } from '../../api/cinemas';
import { addDays, startOfWeek, format } from 'date-fns';
import { fr } from 'date-fns/locale';


const FilmDetails: React.FC = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [film, setFilm] = useState<any | null>(null);
  const [cinemas, setCinemas] = useState<Cinema[]>([]);
  const [cinemaId, setCinemaId] = useState('');
  const [projections, setProjections] = useState<Projection[]>([]);
  const [loading, setLoading] = useState(true);
  const [currentWeekStart, setCurrentWeekStart] = useState<Date>(startOfWeek(new Date(), { weekStartsOn: 1 }));
  const [selectedCinema, setSelectedCinema] = useState<Cinema | null>(null);
  
  const dayMap = {
    "Monday": "Lundi",
    "Tuesday": "Mardi",
    "Wednesday": "Mercredi",
    "Thursday": "Jeudi",
    "Friday": "Vendredi",
    "Saturday": "Samedi",
    "Sunday": "Dimanche",
  };

  // Charger le film
  useEffect(() => {
    const fetchFilm = async () => {
      try {
        if (!id) throw new Error('ID de film manquant');
        const filmData = await getFilmById(id);
        setFilm(filmData);
      } catch (error) {
        console.error('Erreur lors de la récupération du film', error);
      } finally {
        setLoading(false);
      }
    };
    fetchFilm();
  }, [id]);

  // Charger les cinémas
  useEffect(() => {
    const fetchCinemas = async () => {
      try {
        const payload: CinemaPayload = {
          id: undefined,
          name: undefined,
          address: undefined,
          city: undefined,
          owner_id: undefined,
        };
        const response = await searchCinemas(payload);
        setCinemas(response.results);
      } catch (error) {
        console.error('Erreur lors du chargement des cinémas', error);
      }
    };
    fetchCinemas();
  }, []);

useEffect(() => {
  const fetchProjections = async () => {
    if (!cinemaId || !id) return;

    try {
      const response = await searchProjections({
        id: undefined,
        film: id,
        cinema: cinemaId,
        dateDebut: undefined,
        dateFin: undefined,
        daysOfWeek: [],
        hour: undefined,
      });
      setProjections(response.results);
    } catch (error: any) {
      if (error.message === 'No projections found') {
        setProjections([]); // c’est juste qu’il n’y en a pas
      } else {
        console.error('Erreur inattendue lors des projections', error);
      }
    }
  };

  fetchProjections();
}, [cinemaId, id]);

    const getWeekDays = (start: Date) =>
        Array.from({ length: 7 }).map((_, i) => addDays(start, i));

    const handleWeekChange = (offset: number) => {
        setCurrentWeekStart((prev) => addDays(prev, offset * 7));
    };

    const hasProjectionsThisWeek = () => {
        return getWeekDays(currentWeekStart).some((day) => {
            const formatDate = (d: Date) => d.toISOString().split('T')[0];
            const dayFormatted = formatDate(day);

            return projections.some((p) => {
            const dateDebut = formatDate(new Date(p.dateDebut));
            const dateFin = formatDate(new Date(p.dateFin));
            return dayFormatted >= dateDebut && dayFormatted <= dateFin;
            });
        });
    };


  if (loading) return <div className="film-details-loading">Chargement...</div>;
  if (!film) return <div className="film-details-error">Film introuvable.</div>;

  return (
    <div className="film-details-wrapper">
      <button className="film-back-button" onClick={() => navigate(-1)}>← Retour</button>

      <div className="film-card">
        <h1 className="film-title">{film.titre}</h1>
        
        <ul className="film-info-list">
          <li><strong>Durée :</strong> {film.dureeEnMinute} min</li>
          <li><strong>Langue :</strong> {film.lang}</li>
          <li><strong>Sous-titres :</strong> {film.soustitres}</li>
          <li><strong>Réalisateur :</strong> {film.realisateur}</li>
          <li><strong>Acteurs :</strong> {film.acteurs.join(', ')}</li>
          <li><strong>Âge minimum :</strong> {film.ageMin} ans</li>
        </ul>
      </div>
    <br />

      <div className="projection-select">
        <label>Choisir un cinéma :</label>
        <select
            value={cinemaId}
            onChange={(e) => {
                const selectedId = e.target.value;
                setCinemaId(selectedId);
                const found = cinemas.find(c => c.id === selectedId);
                setSelectedCinema(found || null);
            }}
        >
          <option value="">-- Sélectionner --</option>
          {cinemas.map((c) => (
            <option key={c.id} value={c.id}>{c.name}</option>
          ))}
        </select>
      </div>

        {selectedCinema && (
            <div className="cinema-details">
                <h3>📍 {selectedCinema.name}</h3>
                <p><strong>Adresse :</strong> {selectedCinema.address}</p>
                <p><strong>Ville :</strong> {selectedCinema.city}</p>
            </div>
        )}

      {cinemaId && projections.length === 0 && (
        <p className="no-projection-msg">
            🎬 Aucune séance n’est disponible pour ce film dans ce cinéma.
        </p>
        )}

      {projections.length > 0 && (
        <div className="calendar-container">
            <div className="calendar-header">
            <button onClick={() => handleWeekChange(-1)}>← Semaine précédente</button>
            <span>
                Semaine du {format(currentWeekStart, "dd MMMM yyyy", { locale: fr })} au {format(addDays(currentWeekStart, 6), 'dd MMMM yyyy', { locale: fr })}
            </span>
            <button onClick={() => handleWeekChange(1)}>Semaine suivante →</button>
            </div>

            {!hasProjectionsThisWeek() ? (
                <p className="no-week-projection-msg">
                    📆 Aucune séance prévue pour cette semaine.
                </p>
            ) : (

            <div className="calendar-grid">
            {getWeekDays(currentWeekStart).map((day) => {
                const dayName = format(day, 'EEEE');
                const jourNom = dayMap[dayName as keyof typeof dayMap] || dayName;
                return (
                    <div key={day.toISOString()} className="calendar-day">
                        <h4>{jourNom}</h4>
                    {projections
                        .filter((p) => {
                            const formatDate = (d: Date) => d.toISOString().split('T')[0];

                            const dayFormatted = formatDate(day);
                            const dateDebutFormatted = formatDate(new Date(p.dateDebut));
                            const dateFinFormatted = formatDate(new Date(p.dateFin));

                            return dayFormatted >= dateDebutFormatted && dayFormatted <= dateFinFormatted;
                        })
                        .map((p) => {
                            const times = p.calendrier[dayName];
                            return times ? (
                            <div key={p.id + dayName} className="calendar-slot">
                                <div>{times.join(', ')}</div>
                            </div>
                            ) : null;
                        })}
                </div>
                );
            })}
            </div> )}
        </div>
        )}
    </div>
  );
};

export default FilmDetails;
