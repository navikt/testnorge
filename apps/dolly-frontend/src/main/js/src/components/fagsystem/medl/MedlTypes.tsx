export type Medlemskapsperiode = {
	unntakId: number
	ident: string
	fraOgMed: Date
	tilOgMed: Date
	status: string
	statusaarsak: string
	dekning: string
	helsedel: boolean
	medlem: boolean
	lovvalgsland: string
	lovvalg: string
	grunnlag: string
	sporingsinformasjon?: Sporingsinformasjon
	studieinformasjon?: Studieinformasjon
	kilde?: string
	kildedokument?: string
}

export type Sporingsinformasjon = {
	versjon: number
	registrert: Date
	besluttet: Date
	kilde: string
	kildedokument: string
	opprettet: Date
	opprettetAv: string
	sistEndret: Date
	sistEndretAv: string
}

export type Studieinformasjon = {
	statsborgerland: string
	studieland: string
	delstudie: boolean
	soeknadInnvilget: boolean
}
