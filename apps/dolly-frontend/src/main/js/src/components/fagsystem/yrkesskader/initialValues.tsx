export const initialYrkesskadePeriode = {
	fra: null,
	til: null,
}

export const initialYrkesskade = {
	// skadelidtIdentifikator: '',
	rolletype: 'arbeidstaker',
	innmelderIdentifikator: null,
	innmelderrolle: 'virksomhetsrepresentant',
	klassifisering: 'MANUELL',
	paaVegneAv: null,
	tidstype: null,
	skadetidspunkt: null,
	perioder: [initialYrkesskadePeriode],
	referanse: null,
	ferdigstillSak: null,
}
