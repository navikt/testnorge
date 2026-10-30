import { describe, expect, it } from 'vitest'
import { nyident } from '@/components/fagsystem/pdlf/form/validation/partials/identifikasjon'

describe('nyident validation', () => {
	it('should accept an empty entry with source and master', async () => {
		await expect(nyident.isValid({ kilde: 'Dolly', master: 'FREG' })).resolves.toBe(true)
	})

	it('should accept a null ident type when using a new ident', async () => {
		await expect(nyident.isValid({ identtype: null, eksisterendePerson: false })).resolves.toBe(
			true,
		)
	})

	it('should require an existing ident when selecting an existing person', async () => {
		await expect(
			nyident.isValid({ eksisterendePerson: true, eksisterendeIdent: null }),
		).resolves.toBe(false)
	})

	it('should accept an existing ident when selecting an existing person', async () => {
		await expect(
			nyident.isValid({ eksisterendePerson: true, eksisterendeIdent: '12345678901' }),
		).resolves.toBe(true)
	})
})
