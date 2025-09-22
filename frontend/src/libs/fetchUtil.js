const API_BASE = import.meta.env.VITE_API_URL

export default async function fetchUtil(path) {
  const url = `${API_BASE}${path}`

  try {
    const res = await fetch(url)

    if (!res.ok) {
      throw new Error(`Request failed with status ${res.status}`)
    }

    return await res.json()
  } catch (err) {
    console.error(`[fetchUtil] GET ${url} →`, err.message)
    throw err
  }
}
