import { test, expect } from '@playwright/test'

const students = [
  { id: 'stu-1', name: 'Alice' },
  { id: 'stu-2', name: 'Bob' },
]

const disciplines = [
  { id: 'disc-1', name: 'Algoritmos', course: { id: 'c1', name: 'CS' } },
]

const classrooms = [
  {
    id: 'cls-1',
    discipline: { id: 'disc-1', name: 'Algoritmos' },
    status: 'OPEN',
    seatLimit: { maxSeats: 30, occupiedSeats: 5 },
    enrollmentPeriod: { startDate: '2025-01-01', endDate: '2030-12-31' },
  },
]

test.beforeEach(async ({ page }) => {
  await page.route('**/students', (r) => r.fulfill({ json: students }))
  await page.route('**/disciplines', (r) => r.fulfill({ json: disciplines }))
  await page.route('**/classrooms', (r) => r.fulfill({ json: classrooms }))
  await page.route('**/enrollments/by-student/**', (r) => r.fulfill({ json: [] }))
})

test('shows student selector with loaded options', async ({ page }) => {
  await page.goto('/#/aluno')
  const select = page.locator('#alunoSelect')
  await expect(select.locator('option[value="stu-1"]')).toBeAttached()
  await expect(select.locator('option[value="stu-2"]')).toBeAttached()
})

test('shows discipline list with enroll button after selecting a student', async ({ page }) => {
  await page.goto('/#/aluno')
  await page.selectOption('#alunoSelect', 'stu-1')
  await expect(page.getByText('Algoritmos')).toBeVisible()
  await expect(page.locator('.enroll-btn')).toBeVisible()
})

test('shows success message after enrolling', async ({ page }) => {
  await page.route('**/enrollments', async (r) => {
    if (r.request().method() === 'POST') {
      r.fulfill({
        json: {
          id: 'enr-1',
          status: 'CONFIRMED',
          student: { id: 'stu-1', name: 'Alice' },
          classroom: { id: 'cls-1', discipline: { id: 'disc-1' } },
        },
      })
    }
  })

  await page.goto('/#/aluno')
  await page.selectOption('#alunoSelect', 'stu-1')
  await page.locator('.enroll-btn').click()

  await expect(page.locator('#studentStatus')).toContainText('sucesso')
})

test('shows cancel button for enrolled discipline', async ({ page }) => {
  // Student is already enrolled in disc-1 via cls-1
  await page.route('**/enrollments/by-student/stu-1', (r) =>
    r.fulfill({
      json: [
        {
          id: 'enr-1',
          status: 'CONFIRMED',
          classroom: { id: 'cls-1', discipline: { id: 'disc-1' } },
        },
      ],
    }),
  )

  await page.goto('/#/aluno')
  await page.selectOption('#alunoSelect', 'stu-1')

  await expect(page.locator('.cancel-btn')).toBeVisible()
  await expect(page.locator('.badge.enrolled')).toBeVisible()
})

test('search filter hides non-matching disciplines', async ({ page }) => {
  await page.goto('/#/aluno')
  await page.selectOption('#alunoSelect', 'stu-1')
  await page.fill('#disciplineSearch', 'xyz')
  await expect(page.getByText('Nenhuma disciplina encontrada.')).toBeVisible()
})
