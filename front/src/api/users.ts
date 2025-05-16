const BASE_URL = import.meta.env.VITE_API_URL;


export interface UserPayload {
    id: string | undefined;
    username: string | undefined;
    isCinema: boolean | undefined;
    isAdmin: boolean | undefined;
    email: string | undefined;
    createdBefore: string | undefined;
    createdAfter: string | undefined;
}

export interface User {
    id: string;
    username: string;
    email: string;
    createdAt: string;
    cinema: boolean;
    admin: boolean;
}


export interface searchResponse {
    count: number;
    results: User[];
}

export const getUserById = async (id: string): Promise<User> => {
    const res = await fetch(`${BASE_URL}/users/${id}`, {
        method: 'GET',
        headers: { 'Content-Type' : 'application/json' },
    });

    if (!res.ok) {
        throw new Error('Erreur de connexion')
    }

    return await res.json();
}

export const searchUsers = async (credentials: UserPayload): Promise<searchResponse> => {
    const res = await fetch(`${BASE_URL}/users`, {
        method: 'POST',
        headers: { 'Content-Type' : 'application/json' },
        body: JSON.stringify(credentials),
    });

    if (!res.ok) {
        throw new Error('Erreur de connexion')
    }

    return await res.json();
}

export const me = async (token: string): Promise<User> => {
    const res = await fetch(`${BASE_URL}/users/me`, {
        method: 'GET',
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

export const updateUser = async (credential: User, token: string): Promise<User> => {
    const res = await fetch(`${BASE_URL}/users/${credential.id}`, {
        method: 'PUT',
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

export const deleteUser = async (id: string, token: string): Promise<User> => {
    const res = await fetch(`${BASE_URL}/users/${id}`, {
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