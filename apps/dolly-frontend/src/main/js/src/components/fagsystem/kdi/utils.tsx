import { publiseringstidspunktTid } from '@/components/fagsystem/kdi/form/Form'
import { KdiMeldingProps, KdiProps } from '@/components/fagsystem/kdi/form/partials/types'

export type KdiHendelse = {
	hendelseKey: string
	type: string
	meldinger: Array<KdiMeldingProps>
}

const sorterEtterPubliseringstidspunkt = (a: KdiMeldingProps, b: KdiMeldingProps) =>
	publiseringstidspunktTid(a.publiseringstidspunkt) -
	publiseringstidspunktTid(b.publiseringstidspunkt)

export const grupperKdiHendelser = (kdi?: KdiProps): Array<KdiHendelse> => {
	const hendelser = new Map<string, KdiHendelse>()

	Object.entries(kdi ?? {})
		.filter(([type]) => type !== 'annullering')
		.forEach(([type, meldinger]) =>
			meldinger?.forEach((melding: KdiMeldingProps, idx: number) => {
				const hendelseKey = `${type}-${melding.hendelseId ?? melding.meldingId ?? idx}`
				const hendelse = hendelser.get(hendelseKey) ?? { hendelseKey, type, meldinger: [] }
				hendelse.meldinger.push({ ...melding, type })
				hendelser.set(hendelseKey, hendelse)
			}),
		)

	const sisteMelding = (hendelse: KdiHendelse) => hendelse.meldinger[hendelse.meldinger.length - 1]

	return Array.from(hendelser.values())
		.map((hendelse) => ({
			...hendelse,
			meldinger: hendelse.meldinger.sort(sorterEtterPubliseringstidspunkt),
		}))
		.sort((a, b) => sorterEtterPubliseringstidspunkt(sisteMelding(a), sisteMelding(b)))
}

export const getMeldingTittel = (melding: KdiMeldingProps) => {
	if (melding.meldingId) {
		return 'Eksisterende'
	}
	return melding.hendelseId ? 'Redigering' : undefined
}

export const getTidspunktLabel = (meldingstype?: string) => {
	switch (meldingstype) {
		case 'innsettelse':
			return 'Innsettelsestidspunkt'
		case 'loeslatelse':
			return 'Løslatelsestidspunkt'
		case 'avbruddStart':
			return 'Tidspunkt for start på straffeavbrudd'
		case 'avbruddSlutt':
			return 'Tidspunkt for slutt på straffeavbrudd'
		case 'forventetLoeslatelse':
			return 'Forventet løslatt tidspunkt'
		default:
			return 'Tidspunkt'
	}
}
