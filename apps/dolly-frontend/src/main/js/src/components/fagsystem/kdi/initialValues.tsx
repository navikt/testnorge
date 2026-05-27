export const instdataKdiAttributt = 'instdataKdi'

export const initialInnsettelse = {
	hendelseId: null,
	publiseringstidspunkt: new Date(),
	kategori: '',
	organisasjonsnummer: '874718602',
	tidspunkt: '',
}

export const initialLoeslatelse = {
	hendelseId: null,
	publiseringstidspunkt: new Date(),
	kategori: '',
	organisasjonsnummer: '874718602',
	tidspunkt: '',
	erOverfoertTilUtlandskfengsel: false,
	erOverfoertTilVaretektMedElektroniskKontroll: false,
}

export const initialAvbruddStart = {
	hendelseId: null,
	publiseringstidspunkt: new Date(),
	kategori: '',
	organisasjonsnummer: '874718602',
	tidspunkt: '',
	forventetAvbruddSluttTidspunkt: '',
}

export const initialAvbruddSlutt = {
	hendelseId: null,
	publiseringstidspunkt: new Date(),
	kategori: '',
	organisasjonsnummer: '874718602',
	tidspunkt: '',
}

export const initialForventetLoeslatelse = {
	hendelseId: null,
	publiseringstidspunkt: new Date(),
	innmeldingHendelseId: '',
	tidspunkt: '',
}

export const initialAnnullering = {
	hendelseId: null,
	publiseringstidspunkt: new Date(),
}

export const initialKdi = { innsettelse: [initialInnsettelse] }
