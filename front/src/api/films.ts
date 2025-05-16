const BASE_URL = import.meta.env.VITE_API_URL;

export interface searchResponse {
    count: number;
    results: Film[];
}

export interface Film {
    id: string;
    titre: string;
    dureeEnMinute: number;
    lang: string;
    soustitres: string;
    realisateur: string;
    acteurs: string[];
    ageMin: number;
}

export interface FilmPayload {
    id: string | undefined;
    titre: string | undefined;
    lang: string | undefined;
    realisateur: string | undefined;
    ageMin: number | undefined;
    ageMax: number | undefined;
    soustitres: string | undefined;
    dureeMin: number | undefined;
    dureeMax: number | undefined;
}

export const searchFilms = async (credentials: FilmPayload): Promise<searchResponse> => {
    const res = await fetch(`${BASE_URL}/films`, {
        method: 'POST',
        headers: { 'Content-Type' : 'application/json' },
        body: JSON.stringify(credentials),
    });

    if (!res.ok) {
        throw new Error('Erreur de connexion')
    }

    return await res.json();
}

export const addFilm = async(credential: FilmPayload, token: string): Promise<Film> => {
    const res = await fetch(`${BASE_URL}/films/add`, {
        method: 'POST',
        headers: { 
            'Content-Type' : 'application/json',
            'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(credential)
    });
    
    if (!res.ok) {
        throw new Error('Erreur de connexion')
    }

    return await res.json();
}

export const getFilmById = async (id: string): Promise<Film> => {
    const res = await fetch(`${BASE_URL}/films/${id}`, {
        method: 'GET',
        headers: { 'Content-Type' : 'application/json' },
    });

    if (!res.ok) {
        throw new Error('Erreur de connexion')
    }

    return await res.json();
}

export const deleteFilm = async (id: string, token: string): Promise<Film> => {
    const res = await fetch(`${BASE_URL}/films/${id}`, {
        method: 'DELETE',
        headers: { 
            'Content-Type' : 'application/json',
            'Authorization': `Bearer ${token}`
        },
    });
    if (!res.ok) {
        throw new Error('Erreur de connexion')
    }
    return await res.json();
}

export const updateFilm = async(credential: Film, token: string): Promise<Film> => {
    const res = await fetch(`${BASE_URL}/films/${credential.id}`, {
        method: 'PUT',
        headers: { 
            'Content-Type' : 'application/json',
            'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(credential)
    });

    if (!res.ok) {
        throw new Error('Erreur de connexion');
    }

    return await res.json();
}

