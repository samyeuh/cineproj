const BASE_URL = import.meta.env.VITE_API_URL;


export interface LoginPayload {
  username: string;
  email: string | undefined;
  password: string;
}

export interface AuthResponse {
  token: string;
}

export const login = async (credentials: LoginPayload): Promise<AuthResponse> => {
  const res = await fetch(`${BASE_URL}/auth/login`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(credentials),
  });

  if (!res.ok) {
    throw new Error('Erreur de connexion');
  }

  return await res.json();
}

export const register = async (credentials: LoginPayload): Promise<AuthResponse> => {
  const res = await fetch(`${BASE_URL}/auth/register`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(credentials),
  });

  if (!res.ok) {
    throw new Error('Erreur d\'inscription');
  }

  return await res.json();
}