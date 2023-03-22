import * as _ from 'lodash-es'

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
	'bankkonto',
	'pdldata.person',
	'pdldata.opprettNyPerson.alder',
	'pdldata.opprettNyPerson.foedtEtter',
	'pdldata.opprettNyPerson.foedtFoer',
	'pdlforvalter',
	'aareg',
	'arbeidsplassenCV',
	'skjerming',
	'sigrunstub',
	'pensjonforvalter',
	'inntektstub',
	'instdata',
	'krrstub',
	'arenaforvalter',
	'udistub',
	'inntektsmelding',
	'brregstub',
	'dokarkiv',
	'sykemelding',
	'organisasjon',
]

export const harAvhukedeAttributter = (values) => {
	return rootPaths.some((path) => {
		return _.has(values, path)
	})
}

export const getLeggTilIdent = (personFoerLeggTil, identMaster) => {
	if (identMaster === 'PDL') return personFoerLeggTil.pdl.ident
	if (identMaster === 'PDLF') return personFoerLeggTil.pdlforvalter?.person?.ident
	return undefined
}

export const getSisteDato = (dates) => {
	if (dates?.length > 0) {
		return new Date(Math.max.apply(null, dates))
	}
	return null
}
