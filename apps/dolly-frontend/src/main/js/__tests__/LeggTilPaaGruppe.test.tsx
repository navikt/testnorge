import { describe, expect, it } from 'vitest'
import { render, screen } from '@testing-library/react'
import { MemoryRouter } from 'react-router'
import { LeggTilPaaGruppe } from '@/pages/gruppe/LeggTilPaaGruppe/LeggTilPaaGruppe'

describe('LeggTilPaaGruppe', () => {
	it('should enable adding to all people when the group has 51 people', () => {
		render(
			<MemoryRouter>
				<LeggTilPaaGruppe antallPersoner={51} gruppeId="gruppe-id" />
			</MemoryRouter>,
		)

		expect(screen.getByRole('button', { name: 'Legg til på alle' })).toBeEnabled()
	})

	it('should disable adding to all people when the group is empty', () => {
		render(
			<MemoryRouter>
				<LeggTilPaaGruppe antallPersoner={0} gruppeId="gruppe-id" />
			</MemoryRouter>,
		)

		expect(screen.getByRole('button', { name: 'Legg til på alle' })).toBeDisabled()
	})
})
