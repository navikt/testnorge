import { describe, expect, it } from 'vitest'
import { DollyIdentValidation } from '@/components/bestillingsveileder/stegVelger/steg/steg2/DollyIdentValidation'

const validerAntall = (antall: number, is: Record<string, boolean>) =>
	DollyIdentValidation.validateAt('antall', { antall }, { context: { is } })

describe('antall validation', () => {
	it('should reject more than 50 identities for a new order', async () => {
		await expect(validerAntall(51, { nyBestilling: true })).rejects.toThrow(
			'Kan kun bestille max 50 identer om gangen.',
		)
	})

	it('should accept more than 50 people when adding to a group', async () => {
		await expect(validerAntall(51, { leggTilPaaGruppe: true })).resolves.toBe(51)
	})
})
