import { describe, expect, it } from 'vitest'
import { render, screen } from '@testing-library/react'
import { NyIdent } from '@/components/fagsystem/pdlf/PdlTypes'
import { NyIdentitet } from '@/components/fagsystem/pdlf/bestilling/NyIdentitet'

describe('NyIdentitet', () => {
	it('should show new identity and current identity type for an empty entry', () => {
		const nyIdentitet: NyIdent = { kilde: 'Dolly', master: 'PDL' }

		render(<NyIdentitet nyIdentitetListe={[nyIdentitet]} />)

		expect(screen.getByText('Ny ident')).toBeInTheDocument()
		expect(screen.getByText('Samme som gjeldende')).toBeInTheDocument()
	})

	it('should show existing identity choice and identity', () => {
		const nyIdentitet: NyIdent = {
			eksisterendeIdent: '12345678901',
			master: 'PDL',
		}

		render(<NyIdentitet nyIdentitetListe={[nyIdentitet]} />)

		expect(screen.getByText('Ident fra')).toBeInTheDocument()
		expect(screen.getByText('Eksisterende person')).toBeInTheDocument()
		expect(screen.getByText('Eksisterende ident')).toBeInTheDocument()
		expect(screen.getByText('12345678901')).toBeInTheDocument()
	})

	it('should show existing identity choice before an identity is selected', () => {
		const nyIdentitet: NyIdent = { eksisterendePerson: true, master: 'FREG' }

		render(<NyIdentitet nyIdentitetListe={[nyIdentitet]} />)

		expect(screen.getByText('Eksisterende person')).toBeInTheDocument()
		expect(screen.queryByText('Samme som gjeldende')).not.toBeInTheDocument()
	})

	it('should show yes for synthetic and ID2032 identities', () => {
		const nyIdentitet: NyIdent = {
			syntetisk: true,
			id2032: true,
			master: 'PDL',
		}

		render(<NyIdentitet nyIdentitetListe={[nyIdentitet]} />)

		expect(screen.getAllByText('Ja')).toHaveLength(2)
	})

	it('should not show fields removed from identity orders', () => {
		const nyIdentitet: NyIdent = {
			kjoenn: 'MANN',
			alder: '30',
			master: 'PDL',
		}

		render(<NyIdentitet nyIdentitetListe={[nyIdentitet]} />)

		expect(screen.queryByText('Kjønn')).not.toBeInTheDocument()
		expect(screen.queryByText('Alder')).not.toBeInTheDocument()
		expect(screen.queryByText('MANN')).not.toBeInTheDocument()
		expect(screen.queryByText('30')).not.toBeInTheDocument()
	})
})
