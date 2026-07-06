export const instdataKdiAttributt = 'instdataKdi'

export const initialInnsettelse = {
	meldingId: null,
	hendelseId: null,
	publiseringstidspunkt: null,
	kategori: '',
	organisasjonsnummer: '874718602',
	tidspunkt: null,
}

export const initialLoeslatelse = {
	meldingId: null,
	hendelseId: null,
	publiseringstidspunkt: null,
	kategori: '',
	organisasjonsnummer: '874718602',
	tidspunkt: null,
	erOverfoertTilUtlandskfengsel: false,
	erOverfoertTilVaretektMedElektroniskKontroll: false,
}

export const initialAvbruddStart = {
	meldingId: null,
	hendelseId: null,
	publiseringstidspunkt: null,
	kategori: '',
	organisasjonsnummer: '874718602',
	tidspunkt: null,
	forventetAvbruddSluttTidspunkt: null,
}

export const initialAvbruddSlutt = {
	meldingId: null,
	hendelseId: null,
	publiseringstidspunkt: null,
	kategori: '',
	organisasjonsnummer: '874718602',
	tidspunkt: null,
}

export const initialForventetLoeslatelse = {
	meldingId: null,
	hendelseId: null,
	publiseringstidspunkt: null,
	innmeldingHendelseId: '',
	tidspunkt: null,
}

export const initialAnnullering = {
	meldingId: null,
	hendelseId: null,
	publiseringstidspunkt: null,
	annullertMeldingId: null,
}

export const initialKdi = { innsettelse: [initialInnsettelse], annullering: [] }

export const initialKdiTesting = {
	innsettelse: [
		{
			meldingId: null,
			hendelseId: null,
			publiseringstidspunkt: '2026-06-16T17:11:30',
			kategori: 'Boetesoning',
			organisasjonsnummer: '874718602',
			tidspunkt: '2026-06-01T06:00:00',
		},
	],
}
