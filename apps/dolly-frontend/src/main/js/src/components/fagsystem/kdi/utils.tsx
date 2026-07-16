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
