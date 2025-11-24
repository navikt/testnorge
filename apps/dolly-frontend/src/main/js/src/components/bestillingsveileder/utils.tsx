import * as _ from 'lodash-es'
import { sigrunstubSummertSkattegrunnlagAttributt } from '@/components/fagsystem/sigrunstubSummertSkattegrunnlag/form/Form'
import { sigrunstubPensjonsgivendeAttributt } from '@/components/fagsystem/sigrunstubPensjonsgivende/form/Form'

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
	'fullmakt',
	'arbeidssoekerregisteret',
	'arbeidsplassenCV',
	'nomdata',
	'skjerming',
	sigrunstubPensjonsgivendeAttributt,
	sigrunstubSummertSkattegrunnlagAttributt,
	'pensjonforvalter',
	'inntektstub',
	'instdata',
	'krrstub',
	'arenaforvalter',
	'udistub',
	'inntektsmelding',
	'brregstub',
	'histark',
	'dokarkiv',
	'medl',
	'sykemelding',
	'yrkesskader',
	'organisasjon',
	'skattekort',
]

export const harAvhukedeAttributter = (values) => {
	return rootPaths.some((path) => {
		return _.has(values, path)
	})
}

export const getLeggTilIdent = (personFoerLeggTil, identMaster) => {
	if (identMaster === 'PDL') return personFoerLeggTil?.pdl?.ident
	if (identMaster === 'PDLF') return personFoerLeggTil.pdlforvalter?.person?.ident
	return undefined
}

export const getSisteDato = (dates) => {
	if (dates?.length > 0) {
		return new Date(Math.max.apply(null, dates))
	}
	return null
}
