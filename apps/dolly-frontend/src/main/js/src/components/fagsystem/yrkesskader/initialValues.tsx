export const initialYrkesskadePeriode = {
	fra: null,
	til: null,
}

export const initialYrkesskade = {
	// skadelidtIdentifikator: '',
	rolletype: 'arbeidstaker',
	innmelderIdentifikator: '',
	innmelderrolle: 'virksomhetsrepresentant',
	klassifisering: 'MANUELL',
	paaVegneAv: '',
	tidstype: '',
	skadetidspunkt: null,
	perioder: [initialYrkesskadePeriode],
	referanse: '',
	ferdigstillSak: '',
}
