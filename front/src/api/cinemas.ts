const BASE_URL = import.meta.env.VITE_API_URL;

export interface searchResponse {
    count: number;
    results: Cinema[];
}

export interface Cinema {
    id: string;
    name: string;
    address: string;
    city: string;
    owner_id: string;
}

export interface CinemaPayload {
    id: string | undefined;
    name: string | undefined;
    address: string | undefined;
    city: string | undefined;
    owner_id: string | undefined;
}

export const searchCinemas = async (credentials: CinemaPayload): Promise<searchResponse> => {
    const res = await fetch(`${BASE_URL}/cinemas`, {
        method: 'POST',
        headers: { 'Content-Type' : 'application/json' },
        body: JSON.stringify(credentials),
    });

    if (!res.ok) {
        throw new Error('Erreur de connexion')
    }

    return await res.json();
}

export const add = async(credential: CinemaPayload, token: string): Promise<Cinema> => {
    const res = await fetch(`${BASE_URL}/cinemas/add`, {
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

export const getCinemaById = async (id: string): Promise<Cinema> => {
    const res = await fetch(`${BASE_URL}/cinemas/${id}`, {
        method: 'GET',
        headers: { 'Content-Type' : 'application/json' },
    });

    if (!res.ok) {
        throw new Error('Erreur de connexion')
    }

    return await res.json();
}

export const updateCinema = async(credential: CinemaPayload, token: string): Promise<Cinema> => {
    const res = await fetch(`${BASE_URL}/cinemas/update`, {
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

