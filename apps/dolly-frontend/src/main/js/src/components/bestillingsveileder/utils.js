import _has from 'lodash/has'

export const tpsfAttributter = [
	'harMellomnavn',
	'harNyttNavn',
	'adresseNrInfo',
	'boadresse',
	'harIngenAdresse',
	'postadresse',
	'midlertidigAdresse',
	'utenFastBopel',
	'spesreg',
	'spesregDato',
	'erForsvunnet',
	'forsvunnetDato',
	'egenAnsattDatoFom',
	'egenAnsattDatoTom',
	'statsborgerskap',
	'statsborgerskapRegdato',
	'statsborgerskapTildato',
	'sprakKode',
	'datoSprak',
	'innvandretFraLand',
	'innvandretFraLandFlyttedato',
	'utvandretTilLand',
	'utvandretTilLandFlyttedato',
	'doedsdato',
	'harBankkontonr',
	'bankkontonrRegdato',
	'telefonLandskode_1',
	'telefonnummer_1',
	'telefonLandskode_2',
	'telefonnummer_2',
	'typeSikkerhetTiltak',
	'beskrSikkerhetTiltak',
	'sikkerhetTiltakDatoFom',
	'sikkerhetTiltakDatoTom',
	'sivilstand',
	'sivilstandRegdato',
	'relasjoner',
	'alder',
	'foedtEtter',
	'foedtFoer',
	'kjonn',
	'identHistorikk',
	'vergemaal',
	'fullmakt',
]

export const rootPaths = [
	'tpsf',
	'tpsMessaging',
	'pdldata.person',
	'pdldata.opprettNyPerson.alder',
	'pdldata.opprettNyPerson.foedtEtter',
	'pdldata.opprettNyPerson.foedtFoer',
	'pdlforvalter',
	'aareg',
	'sigrunstub',
	'pensjonforvalter',
	'inntektstub',
	'instdata',
	'krrstub',
	'nomData',
	'arenaforvalter',
	'udistub',
	'inntektsmelding',
	'brregstub',
	'dokarkiv',
	'sykemelding',
	'organisasjon',
]

export const harAvhukedeAttributter = (values) => {
	return rootPaths.some((path) => _has(values, path))
}

export const getLeggTilIdent = (personFoerLeggTil, identMaster) => {
	if (identMaster === 'TPSF') return personFoerLeggTil.tpsf.ident
	if (identMaster === 'PDL') return personFoerLeggTil.pdl.ident
	if (identMaster === 'PDLF') return personFoerLeggTil.pdlforvalter?.person?.ident
	return undefined
}
