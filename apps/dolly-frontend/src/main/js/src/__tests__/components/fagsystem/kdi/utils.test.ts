import { getMeldingTittel, grupperKdiHendelser } from '@/components/fagsystem/kdi/utils'
import type { KdiProps } from '@/components/fagsystem/kdi/form/partials/types'

const melding = (
	meldingId: string | null,
	hendelseId: string | null,
	publiseringstidspunkt?: string,
) => ({
	meldingId,
	hendelseId,
	publiseringstidspunkt,
})

const kdi = (data: object) => data as unknown as KdiProps

const meldingIder = (hendelse: { meldinger: Array<{ meldingId?: string | null }> }) =>
	hendelse.meldinger.map((m) => m.meldingId)

describe('grupperKdiHendelser', () => {
	it('should return empty list when kdi is undefined', () => {
		expect(grupperKdiHendelser(undefined)).toEqual([])
	})

	it('should group meldinger by type and hendelseId and skip annulleringer', () => {
		const hendelser = grupperKdiHendelser(
			kdi({
				innsettelse: [
					melding('m1', 'h1', '2026-06-01T10:00:00'),
					melding('m2', 'h1', '2026-06-02T10:00:00'),
				],
				loeslatelse: [melding('l1', 'h1', '2026-06-03T10:00:00')],
				annullering: [{ ...melding('a1', 'h1', '2026-06-04T10:00:00'), annullertMeldingId: 'm1' }],
			}),
		)

		expect(hendelser.map((hendelse) => hendelse.hendelseKey)).toEqual([
			'innsettelse-h1',
			'loeslatelse-h1',
		])
		expect(meldingIder(hendelser[0])).toEqual(['m1', 'm2'])
		expect(hendelser[1].meldinger[0].type).toBe('loeslatelse')
	})

	it('should sort meldinger within hendelse and hendelser by latest melding', () => {
		const hendelser = grupperKdiHendelser(
			kdi({
				innsettelse: [
					melding('m2', 'h1', '2026-06-20T10:00:00'),
					melding('m1', 'h1', '2026-06-01T10:00:00'),
					melding('m3', 'h2', '2026-06-10T10:00:00'),
				],
			}),
		)

		expect(hendelser.map((hendelse) => hendelse.hendelseKey)).toEqual([
			'innsettelse-h2',
			'innsettelse-h1',
		])
		expect(meldingIder(hendelser[1])).toEqual(['m1', 'm2'])
	})

	it('should place meldinger without publiseringstidspunkt last', () => {
		const hendelser = grupperKdiHendelser(
			kdi({
				innsettelse: [
					melding(null, 'h1'),
					melding('m1', 'h1', '2026-06-01T10:00:00'),
					melding('m2', 'h2', '2026-06-05T10:00:00'),
				],
			}),
		)

		expect(hendelser.map((hendelse) => hendelse.hendelseKey)).toEqual([
			'innsettelse-h2',
			'innsettelse-h1',
		])
		expect(meldingIder(hendelser[1])).toEqual(['m1', null])
	})

	it('should keep meldinger without hendelseId as separate hendelser', () => {
		const hendelser = grupperKdiHendelser(
			kdi({
				innsettelse: [
					melding(null, null, '2026-06-01T10:00:00'),
					melding(null, null, '2026-06-02T10:00:00'),
				],
			}),
		)

		expect(hendelser).toHaveLength(2)
	})
})

describe('getMeldingTittel', () => {
	it.each([
		['Eksisterende', melding('m1', 'h1')],
		['Redigering', melding(null, 'h1')],
		[undefined, melding(null, null)],
	])('should return %s', (tittel, kdiMelding) => {
		expect(getMeldingTittel(kdiMelding as never)).toBe(tittel)
	})
})
