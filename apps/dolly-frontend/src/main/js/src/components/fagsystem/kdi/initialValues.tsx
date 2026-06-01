export const instdataKdiAttributt = 'instdataKdi'

export const initialInnsettelse = {
	meldingId: 111,
	hendelseId: null,
	publiseringstidspunkt: new Date(),
	kategori: '',
	organisasjonsnummer: '874718602',
	tidspunkt: '',
}

export const initialLoeslatelse = {
	meldingId: 222,
	hendelseId: null,
	publiseringstidspunkt: new Date(),
	kategori: '',
	organisasjonsnummer: '874718602',
	tidspunkt: '',
	erOverfoertTilUtlandskfengsel: false,
	erOverfoertTilVaretektMedElektroniskKontroll: false,
}

export const initialAvbruddStart = {
	meldingId: 333,
	hendelseId: null,
	publiseringstidspunkt: new Date(),
	kategori: '',
	organisasjonsnummer: '874718602',
	tidspunkt: '',
	forventetAvbruddSluttTidspunkt: '',
}

export const initialAvbruddSlutt = {
	meldingId: 444,
	hendelseId: null,
	publiseringstidspunkt: new Date(),
	kategori: '',
	organisasjonsnummer: '874718602',
	tidspunkt: '',
}

export const initialForventetLoeslatelse = {
	meldingId: 555,
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
