import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import { request } from '../../src/api'

// Vitest runs in Node; stub globalThis.fetch before each test
describe('request', () => {
  beforeEach(() => {
    vi.stubGlobal('fetch', vi.fn())
  })

  afterEach(() => {
    vi.unstubAllGlobals()
  })

  const mockOk = (body: unknown, status = 200) =>
    (globalThis.fetch as ReturnType<typeof vi.fn>).mockResolvedValueOnce({
      ok: true,
      status,
      json: async () => body,
      text: async () => '',
    })

  const mockError = (text: string, status = 409) =>
    (globalThis.fetch as ReturnType<typeof vi.fn>).mockResolvedValueOnce({
      ok: false,
      status,
      text: async () => text,
    })

  it('calls the correct URL and returns parsed JSON', async () => {
    const payload = [{ id: '1', name: 'Alice' }]
    mockOk(payload)

    const result = await request('/students')

    expect(fetch).toHaveBeenCalledWith(
      'http://localhost:8080/students',
      expect.any(Object),
    )
    expect(result).toEqual(payload)
  })

  it('includes Content-Type: application/json for POST requests', async () => {
    mockOk({ id: 'new' })

    await request('/students', { method: 'POST', body: JSON.stringify({ name: 'Bob' }) })

    const [, options] = (fetch as ReturnType<typeof vi.fn>).mock.calls[0] as [string, RequestInit]
    expect((options.headers as Record<string, string>)['Content-Type']).toBe('application/json')
  })

  it('does not add Content-Type for GET requests', async () => {
    mockOk([])

    await request('/students')

    const [, options] = (fetch as ReturnType<typeof vi.fn>).mock.calls[0] as [string, RequestInit]
    expect((options.headers as Record<string, string>)['Content-Type']).toBeUndefined()
  })

  it('throws an Error with the server message when the response is not ok', async () => {
    mockError('Student already enrolled in classroom')

    await expect(
      request('/enrollments', { method: 'POST', body: '{}' }),
    ).rejects.toThrow('Student already enrolled in classroom')
  })

  it('returns null for 204 No Content', async () => {
    ;(globalThis.fetch as ReturnType<typeof vi.fn>).mockResolvedValueOnce({
      ok: true,
      status: 204,
    })

    const result = await request('/students/123', { method: 'DELETE' })
    expect(result).toBeNull()
  })
})
