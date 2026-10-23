export type SamtalereferatTypes = {
	temagruppe?: string
	tema?: string
	enhet?: string
	fritekst?: string
	kjedeId?: string
	meldingsId?: string
}

export type MeldingTypes = SamtalereferatTypes & {
	tildelMeg?: boolean
}

export type HenvendelseTypes = {
	meldinger?: MeldingTypes[]
	samtalereferater?: SamtalereferatTypes[]
}

export type HenvendelseMeldingAvsender = {
	identType?: string
	ident?: string
	navEnhet?: string
}

export type HenvendelseMeldingInfo = {
	fritekst?: string
	sendtDato?: string
	lestDato?: string
	kanal?: string
	meldingsId?: string
	sladding?: boolean
	fra?: HenvendelseMeldingAvsender
}

export type HenvendelseJournalpost = {
	journalforerNavIdent?: string
	journalforendeEnhet?: string
	journalfortDato?: string
	journalfortTema?: string
	fagsakId?: string
	fagsaksystem?: string
	journalpostId?: string
}

export type HenvendelseMarkering = {
	markeringstype?: string
	markertDato?: string
	markertAv?: string
}

export type HenvendelseInfo = {
	henvendelseType?: string
	fnr?: string
	aktorId?: string
	opprettetDato?: string
	avsluttetDato?: string
	kasseringsDato?: string
	avsluttetAv?: string
	sattTilSladdingAv?: string
	sladding?: boolean
	feilsendt?: boolean
	kjedeId?: string
	gjeldendeTemagruppe?: string
	gjeldendeTema?: string
	journalposter?: HenvendelseJournalpost[]
	meldinger?: HenvendelseMeldingInfo[]
	markeringer?: HenvendelseMarkering[]
}

export type HenvendelseListeResponse = {
	data?: HenvendelseInfo[]
	currentPage?: number
	pageSize?: number
	totalPages?: number
	hasNextPage?: boolean
}
