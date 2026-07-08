import { describe, it, expect } from 'vitest'
import { buildClassroomLabels } from '../../src/pages/cadastros'

describe('buildClassroomLabels', () => {
  it('returns the discipline name when there is only one classroom for that discipline', () => {
    const classrooms = [
      { id: 'c1', discipline: { id: 'd1', name: 'Algoritmos' } },
    ]
    const labels = buildClassroomLabels(classrooms)
    expect(labels.get('c1')).toBe('Algoritmos')
  })

  it('appends (1) and (2) when two classrooms share the same discipline', () => {
    const classrooms = [
      { id: 'c1', discipline: { id: 'd1', name: 'Algoritmos' } },
      { id: 'c2', discipline: { id: 'd1', name: 'Algoritmos' } },
    ]
    const labels = buildClassroomLabels(classrooms)
    const both = new Set([labels.get('c1')!, labels.get('c2')!])
    expect(both).toContain('Algoritmos (1)')
    expect(both).toContain('Algoritmos (2)')
  })

  it('does not add a counter to other disciplines when only one shares a discipline', () => {
    const classrooms = [
      { id: 'c1', discipline: { id: 'd1', name: 'Algoritmos' } },
      { id: 'c2', discipline: { id: 'd1', name: 'Algoritmos' } },
      { id: 'c3', discipline: { id: 'd2', name: 'Estruturas' } },
    ]
    const labels = buildClassroomLabels(classrooms)
    expect(labels.get('c3')).toBe('Estruturas')
    expect(labels.get('c1')).toContain('Algoritmos (')
    expect(labels.get('c2')).toContain('Algoritmos (')
  })

  it('assigns counters stably: same input always yields same labels', () => {
    const classrooms = [
      { id: 'c1', discipline: { id: 'd1', name: 'Algoritmos' } },
      { id: 'c2', discipline: { id: 'd1', name: 'Algoritmos' } },
    ]
    const first = buildClassroomLabels(classrooms)
    const second = buildClassroomLabels(classrooms)
    expect(first.get('c1')).toBe(second.get('c1'))
    expect(first.get('c2')).toBe(second.get('c2'))
  })

  it('uses the classroom id as label when discipline is absent', () => {
    const classrooms = [{ id: 'c1' }] as Parameters<typeof buildClassroomLabels>[0]
    const labels = buildClassroomLabels(classrooms)
    expect(labels.get('c1')).toBe('c1')
  })

  it('returns an empty map for empty input', () => {
    expect(buildClassroomLabels([]).size).toBe(0)
  })
})
