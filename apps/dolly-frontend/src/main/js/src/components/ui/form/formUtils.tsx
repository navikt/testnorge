import * as _ from 'lodash-es'
import { runningE2ETest } from '@/service/services/Request'
import { isDate } from 'date-fns'
import { useFormContext } from 'react-hook-form'
import { sigrunstubPensjonsgivendeAttributt } from '@/components/fagsystem/sigrunstubPensjonsgivende/form/Form'
import { sigrunstubSummertSkattegrunnlagAttributt } from '@/components/fagsystem/sigrunstubSummertSkattegrunnlag/form/Form'
import {
	nySykemeldingAttributt,
	sykemeldingAttributt,
} from '@/components/fagsystem/sykdom/form/Form'

export const panelError = (attributtPath) => {
	const {
		formState: { errors },
	} = useFormContext()
	// Ignore if values ikke er satt
	if (_.isNil(attributtPath)) return false

	// Strings er akseptert, men konverter til Array
	if (!Array.isArray(attributtPath)) attributtPath = [attributtPath]

	return attributtPath.some((attr) => _.has(errors, attr) || _.has(errors, `manual.${attr}`))
}

export const SyntEvent = (name, value) => ({ target: { name, value } })

export const isObjectEmptyDeep = (obj: any): boolean =>
	_.every(obj, (val) => {
		if (typeof val === 'object') {
			return isObjectEmptyDeep(val)
		}
		return val === '' || _.isNil(val)
	})

export const fixTimezone = (date: Date) => {
	if (!isDate(date) || date.getUTCHours() === 0) {
		return date
	}

	const erSommertid = date?.toString().includes('sommertid')

	// Denne funker naar dagens dato er vintertid, mulig vi maa ha en ekstra sjekk naar vi er paa sommertid
	const tzoffset = erSommertid
		? new Date().getTimezoneOffset() * 60000 * 2
		: new Date().getTimezoneOffset() * 60000

	return new Date(date.getTime() - tzoffset)
}

const getValgteAttributter = (values) => {
	const rootPaths = [
		'pdldata.opprettNyPerson.alder',
		'pdldata.person.foedested',
		'pdldata.person.foedselsdato',
		'pdldata.person.doedsfall',
		'pdldata.person.deltBosted',
		'pdldata.person.statsborgerskap',
		'pdldata.person.innflytting',
		'pdldata.person.utflytting',
		'pdldata.person.kjoenn',
		'pdldata.person.navn',
		'pdldata.person.telefonnummer',
		'pdldata.person.vergemaal',
		'pdldata.person.fullmakt',
		'pdldata.person.sikkerhetstiltak',
		'pdldata.person.tilrettelagtKommunikasjon',
		'pdldata.person.bostedsadresse',
		'pdldata.person.oppholdsadresse',
		'pdldata.person.kontaktadresse',
		'pdldata.person.adressebeskyttelse',
		'pdldata.person.sivilstand',
		'pdldata.person.doedfoedtBarn',
		'pdldata.person.forelderBarnRelasjon',
		'pdldata.person.foreldreansvar',
		'pdldata.person.falskIdentitet',
		'pdldata.person.utenlandskIdentifikasjonsnummer',
		'pdldata.person.nyident',
		'pdldata.person.kontaktinformasjonForDoedsbo',
		'tpsMessaging.egenAnsattDatoFom',
		'tpsMessaging.egenAnsattDatoTom',
		'skjerming.egenAnsattDatoFom',
		'skjerming.egenAnsattDatoTom',
		'tpsMessaging.utenlandskBankkonto',
		'tpsMessaging.norskBankkonto',
		'bankkonto.utenlandskBankkonto',
		'bankkonto.norskBankkonto',
		'aareg',
		'fullmakt',
		sigrunstubPensjonsgivendeAttributt,
		sigrunstubSummertSkattegrunnlagAttributt,
		'inntektstub',
		'inntektsmelding',
		'skattekort',
		'arbeidssoekerregisteret',
		'arbeidsplassenCV',
		'pensjonforvalter.inntekt',
		'pensjonforvalter.generertInntekt',
		'pensjonforvalter.pensjonsavtale',
		'pensjonforvalter.tp',
		'pensjonforvalter.alderspensjon',
		'pensjonforvalter.uforetrygd',
		'pensjonforvalter.afpOffentlig',
		'arenaforvalter',
		sykemeldingAttributt,
		nySykemeldingAttributt,
		'yrkesskader',
		'brregstub',
		'instdata',
		'krrstub',
		'udistub',
		'dokarkiv',
		'histark',
		'medl',
		'organisasjon.enhetstype',
		'organisasjon.naeringskode',
		'organisasjon.sektorkode',
		'organisasjon.formaal',
		'organisasjon.stiftelsesdato',
		'organisasjon.maalform',
		'organisasjon.telefon',
		'organisasjon.epost',
		'organisasjon.nettside',
		'organisasjon.forretningsadresse',
		'organisasjon.postadresse',
	]

	const valgteAttributter = []

	rootPaths.forEach((path) => {
		if (_.has(values, path)) {
			valgteAttributter.push(path)
		}
	})
	return valgteAttributter
}

export const erForsteEllerTest = (values, attributter) => {
	const valgteAttributter = getValgteAttributter(values)
	return runningE2ETest() || attributter.includes(valgteAttributter[0])
}

export const harValgtAttributt = (values, attributter) => {
	const valgteAttributter = getValgteAttributter(values)
	return attributter.some((attr) => valgteAttributter.includes(attr))
}
