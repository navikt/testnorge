import { describe, expect, it } from 'vitest'
import { render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { BestillingsveilederContextType } from '@/components/bestillingsveileder/BestillingsveilederContext'
import { NyIdentRegelInfo } from '@/components/fagsystem/pdlf/form/partials/nyIdent/NyIdentInfo'
import {
	getKonverterValg,
	getStandardIdenttype,
} from '@/components/fagsystem/pdlf/form/partials/nyIdent/nyIdentUtils'

const makeOpts = (
	opts: Partial<BestillingsveilederContextType> = {},
): BestillingsveilederContextType => ({
	initialValues: {},
	...opts,
})

describe('NyIdentRegelInfo', () => {
	it('should show the ident priority and history rules', () => {
		render(<NyIdentRegelInfo />)

		expect(screen.getByText(/Rekkefølgen er NPID → DNR → FNR/)).toBeInTheDocument()
		expect(screen.getByRole('button', { name: 'Les mer om rekkefølgen' })).toHaveAttribute(
			'aria-expanded',
			'false',
		)
		expect(screen.queryByText(/Uten identtype/)).not.toBeInTheDocument()
	})

	it('should explain empty ident type when adding to a group', async () => {
		render(<NyIdentRegelInfo flereIdenttyper />)
		await userEvent.click(screen.getByRole('button', { name: 'Les mer om rekkefølgen' }))

		expect(
			screen.getByText(/Uten identtype får personen en ny ident av samme type/),
		).toBeInTheDocument()
	})
})

describe('getStandardIdenttype', () => {
	it('should use the current ident type for a single person', () => {
		expect(getStandardIdenttype(makeOpts({ is: { leggTil: true }, identtype: 'DNR' }))).toBe('DNR')
	})

	it('should leave ident type empty when adding to a group', () => {
		expect(
			getStandardIdenttype(makeOpts({ is: { leggTilPaaGruppe: true }, identtype: 'FNR' })),
		).toBeNull()
	})

	it('should leave ident type empty when creating from existing idents', () => {
		expect(
			getStandardIdenttype(makeOpts({ is: { opprettFraIdenter: true }, identtype: 'FNR' })),
		).toBeNull()
	})
})

describe('getKonverterValg', () => {
	it('should hide conversion choices for an existing person', () => {
		expect(getKonverterValg(makeOpts({ is: { leggTilPaaGruppe: true } }), true)).toEqual({
			visSyntetisk: false,
			visId2032: false,
		})
	})

	it('should show both conversion choices when adding to a group', () => {
		expect(getKonverterValg(makeOpts({ is: { leggTilPaaGruppe: true } }), false)).toEqual({
			visSyntetisk: true,
			visId2032: true,
		})
	})

	it('should show syntetisk for a prodlike person and ID2032 when missing', () => {
		expect(
			getKonverterValg(
				makeOpts({
					is: { leggTil: true },
					personFoerLeggTil: { pdl: { ident: '01019012345' } },
				}),
				false,
			),
		).toEqual({ visSyntetisk: true, visId2032: true })
	})

	it('should hide syntetisk for a non-prodlike person and ID2032 when already present', () => {
		expect(
			getKonverterValg(
				makeOpts({
					is: { leggTil: true },
					personFoerLeggTil: {
						pdlforvalter: { person: { ident: '01419012345', id2032: true } },
					},
				}),
				false,
			),
		).toEqual({ visSyntetisk: false, visId2032: false })
	})

	it('should show only ID2032 for a new person when it is not already enabled', () => {
		expect(getKonverterValg(makeOpts({ is: { nyBestilling: true } }), false)).toEqual({
			visSyntetisk: false,
			visId2032: true,
		})
	})

	it('should hide ID2032 when it is already enabled for a new person', () => {
		expect(
			getKonverterValg(makeOpts({ is: { nyBestillingFraMal: true }, id2032: true }), false),
		).toEqual({ visSyntetisk: false, visId2032: false })
	})
})
