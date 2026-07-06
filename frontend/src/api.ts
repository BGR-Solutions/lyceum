export const apiBase = (import.meta.env.VITE_API_BASE as string) || 'http://localhost:8080'

export const request = async (path: string, options: RequestInit = {}): Promise<any> => {
  const response = await fetch(`${apiBase}${path}`, {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      ...(options.headers || {}),
    },
  })

  if (!response.ok) {
    const text = await response.text()
    throw new Error(text)
  }

  if (response.status === 204) return null
  return response.json()
}
