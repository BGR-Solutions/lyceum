import { test, expect } from '@playwright/test'

const students = [{ id: 'stu-1', name: 'Alice' }]

// Two classrooms sharing the same discipline → should show counter labels
const classrooms = [
  { id: 'cls-1', discipline: { id: 'disc-1', name: 'Algoritmos' }, status: 'OPEN' },
  { id: 'cls-2', discipline: { id: 'disc-1', name: 'Algoritmos' }, status: 'OPEN' },
]

const enrollments = [
  {
    id: 'enr-1',
    status: 'CONFIRMED',
    student: { id: 'stu-1', name: 'Alice' },
    classroom: { id: 'cls-1', discipline: { id: 'disc-1', name: 'Algoritmos' } },
  },
]

test.beforeEach(async ({ page }) => {
  await page.route('**/students', (r) => r.fulfill({ json: students }))
  await page.route('**/classrooms', (r) => r.fulfill({ json: classrooms }))
  await page.route('**/enrollments/by-classroom/cls-1', (r) => r.fulfill({ json: enrollments }))
  await page.route('**/enrollments/by-classroom/cls-2', (r) => r.fulfill({ json: [] }))
  await page.route('**/enrollments/by-student/stu-1', (r) => r.fulfill({ json: enrollments }))
})

test('shows filter dropdowns', async ({ page }) => {
  await page.goto('/#/matriculas')
  await expect(page.locator('#filterStudent')).toBeVisible()
  await expect(page.locator('#filterClassroom')).toBeVisible()
  await expect(page.locator('#searchBtn')).toBeVisible()
})

test('populates student filter with loaded students', async ({ page }) => {
  await page.goto('/#/matriculas')
  await expect(page.locator('#filterStudent option[value="stu-1"]')).toBeAttached()
})

test('labels classrooms with counters when multiple share a discipline', async ({ page }) => {
  await page.goto('/#/matriculas')
  const options = page.locator('#filterClassroom option')
  await expect(options.filter({ hasText: 'Algoritmos (1)' })).toBeAttached()
  await expect(options.filter({ hasText: 'Algoritmos (2)' })).toBeAttached()
})

test('filters by student and shows their enrollments', async ({ page }) => {
  await page.goto('/#/matriculas')
  await page.selectOption('#filterStudent', 'stu-1')
  // scope to table cells to avoid matching the <option> text in the student dropdown
  await expect(page.locator('#matriculasTable td', { hasText: 'Alice' })).toBeVisible()
  await expect(page.locator('.status-badge.status-confirmed')).toBeVisible()
})

test('filters by classroom and shows only enrollments for that classroom', async ({ page }) => {
  await page.goto('/#/matriculas')
  await page.selectOption('#filterClassroom', 'cls-1')
  await expect(page.locator('#matriculasTable td', { hasText: 'Alice' })).toBeVisible()
})

test('shows empty message when no enrollments match the filter', async ({ page }) => {
  await page.goto('/#/matriculas')
  await page.selectOption('#filterClassroom', 'cls-2')
  await expect(page.getByText('Nenhuma matrícula encontrada.')).toBeVisible()
})
