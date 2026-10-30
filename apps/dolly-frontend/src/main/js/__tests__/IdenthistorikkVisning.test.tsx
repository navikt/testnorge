import { render, screen } from '@testing-library/react'
import { describe, expect, it, vi } from 'vitest'
import { IdenthistorikkVisning } from '@/components/fagsystem/pdlf/visning/partials/Identhistorikk'

vi.mock('@/components/ui/icon/Icon', () => ({
	default: ({ kind }: { kind: string }) => <span data-testid={`icon-${kind}`} />,
}))

vi.mock('@/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataVisning', () => ({
	PdlDataVisning: ({ ident }: { ident: string }) => <span data-testid={`pdl-data-${ident}`} />,
}))

describe('IdenthistorikkVisning', () => {
	it('should show ident history fields without showing the related person name', () => {
		render(
			<IdenthistorikkVisning
				relasjoner={[
					{
						id: 42,
						relasjonType: 'GAMMEL_IDENTITET',
						sistOppdatert: '2023-07-14',
						relatertPerson: {
							ident: '01010112345',
							navn: [
								{
									fornavn: 'Testnavn',
									mellomnavn: '',
									etternavn: 'Eksempel',
								},
							],
						},
					},
				]}
			/>,
		)

		expect(screen.getByText('01010112345')).toBeInTheDocument()
		expect(screen.getByText('FNR')).toBeInTheDocument()
		expect(screen.getByText('14.07.2023')).toBeInTheDocument()
		expect(screen.getByText('42')).toBeInTheDocument()
		expect(screen.getByText('FREG')).toBeInTheDocument()
		expect(screen.queryByText('Testnavn')).not.toBeInTheDocument()
		expect(screen.getByTestId('pdl-data-01010112345')).toBeInTheDocument()
	})
})
