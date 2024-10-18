export type YrkesskadePeriodeTypes = {
	fra: string
	til: string
}

export type YrkesskadeTypes = {
	rolletype: string
	innmelderrolle: string
	innmelderIdentifikator: string
	paaVegneAv: string
	klassifisering?: string
	tidstype?: string
	skadetidspunkt?: string
	perioder: Array<YrkesskadePeriodeTypes>
	referanse?: string
	ferdigstillSak?: string
}

export type YrkesskaderTypes = {
	yrkesskader: Array<YrkesskadeTypes>
}
