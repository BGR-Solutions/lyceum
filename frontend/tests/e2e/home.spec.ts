import { test, expect } from '@playwright/test'

test('shows all three navigation cards', async ({ page }) => {
  await page.goto('/')
  await expect(page.getByRole('heading', { name: 'Perfil de Cadastros' })).toBeVisible()
  await expect(page.getByRole('heading', { name: 'Matrículas', exact: true })).toBeVisible()
  await expect(page.getByRole('heading', { name: 'Perfil de Aluno' })).toBeVisible()
})

test('navigates to cadastros page', async ({ page }) => {
  await page.route('**/students', (r) => r.fulfill({ json: [] }))
  await page.route('**/courses', (r) => r.fulfill({ json: [] }))
  await page.route('**/disciplines', (r) => r.fulfill({ json: [] }))
  await page.route('**/classrooms', (r) => r.fulfill({ json: [] }))

  await page.goto('/#/cadastros')
  await expect(page.getByRole('heading', { name: 'Lyceum - Cadastros' })).toBeVisible()
})

test('navigates to matriculas page', async ({ page }) => {
  await page.route('**/students', (r) => r.fulfill({ json: [] }))
  await page.route('**/classrooms', (r) => r.fulfill({ json: [] }))

  await page.goto('/#/matriculas')
  await expect(page.getByRole('heading', { name: 'Consulta de Matrículas' })).toBeVisible()
})

test('navigates to aluno page', async ({ page }) => {
  await page.route('**/students', (r) => r.fulfill({ json: [] }))
  await page.route('**/disciplines', (r) => r.fulfill({ json: [] }))
  await page.route('**/classrooms', (r) => r.fulfill({ json: [] }))

  await page.goto('/#/aluno')
  await expect(page.getByRole('heading', { name: 'Perfil de Aluno' })).toBeVisible()
})
