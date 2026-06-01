export const instdataKdiAttributt = 'instdataKdi'

export const initialInnsettelse = {
	meldingId: null,
	hendelseId: null,
	publiseringstidspunkt: new Date(),
	kategori: '',
	organisasjonsnummer: '874718602',
	tidspunkt: '',
}

export const initialLoeslatelse = {
	meldingId: null,
	hendelseId: null,
	publiseringstidspunkt: new Date(),
	kategori: '',
	organisasjonsnummer: '874718602',
	tidspunkt: '',
	erOverfoertTilUtlandskfengsel: false,
	erOverfoertTilVaretektMedElektroniskKontroll: false,
}

export const initialAvbruddStart = {
	meldingId: null,
	hendelseId: null,
	publiseringstidspunkt: new Date(),
	kategori: '',
	organisasjonsnummer: '874718602',
	tidspunkt: '',
	forventetAvbruddSluttTidspunkt: '',
}

export const initialAvbruddSlutt = {
	meldingId: null,
	hendelseId: null,
	publiseringstidspunkt: new Date(),
	kategori: '',
	organisasjonsnummer: '874718602',
	tidspunkt: '',
}

export const initialForventetLoeslatelse = {
	meldingId: null,
	hendelseId: null,
	publiseringstidspunkt: new Date(),
	innmeldingHendelseId: '',
	tidspunkt: '',
}

export const initialAnnullering = {
	meldingId: null,
	hendelseId: null,
	publiseringstidspunkt: new Date(),
	annullertMeldingId: null,
}

export const initialKdi = { innsettelse: [initialInnsettelse], annullering: [] }
