import _ from 'lodash'
import { runningE2ETest } from '@/service/services/Request'
import { isDate } from 'date-fns'
import { useFormContext } from 'react-hook-form'
import dayjs from 'dayjs'
import { VALID_DATE_FORMATS } from '@/components/ui/form/inputs/datepicker/Datepicker'

export const panelError = (attributtPath) => {
	const {
		formState: { errors },
	} = useFormContext()
	// Ignore if values ikke er satt
	if (_.isNil(attributtPath)) return false

	// Strings er akseptert, men konverter til Array
	if (!Array.isArray(attributtPath)) attributtPath = [attributtPath]

	return attributtPath.some((attr) => _.has(errors, attr))
}

export const SyntEvent = (name, value) => ({ target: { name, value } })

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

export const convertInputToDate = (date: any, name?: string) => {
	const formMethods = useFormContext()
	const dateString = isDate(date) ? date.toLocaleDateString() : date
	console.log('dateString: ', dateString) //TODO - SLETT MEG
	const dateLocalTime = dayjs(dateString, VALID_DATE_FORMATS, true)
	console.log('date: ', date) //TODO - SLETT MEG
	console.log('dateLocalTime: ', dateLocalTime) //TODO - SLETT MEG
	const dateJS = dateLocalTime.add(dateLocalTime.utcOffset(), 'minute')
	if (name) {
		if (!date || dateJS.isValid()) {
			formMethods.clearErrors(name)
		} else {
			formMethods.setError(name, {
				type: 'invalid-date-format',
				message: 'Ugyldig dato-format',
			})
		}
	}
	return dateJS
}

const getValgteAttributter = (values) => {
	const rootPaths = [
		'pdldata.opprettNyPerson.alder',
		'pdldata.person.foedested',
		'pdldata.person.foedselsdato',
		'pdldata.person.doedsfall',
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
		'tpsMessaging.spraakKode',
		'tpsMessaging.egenAnsattDatoFom',
		'tpsMessaging.egenAnsattDatoTom',
		'skjerming.egenAnsattDatoFom',
		'skjerming.egenAnsattDatoTom',
		'tpsMessaging.utenlandskBankkonto',
		'tpsMessaging.norskBankkonto',
		'bankkonto.utenlandskBankkonto',
		'bankkonto.norskBankkonto',
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
		'aareg',
		'fullmakt',
		'sigrunstub',
		'sigrunstubPensjonsgivende',
		'inntektstub',
		'inntektsmelding',
		'skattekort',
		'arbeidsplassenCV',
		'pensjonforvalter.inntekt',
		'pensjonforvalter.pensjonsavtale',
		'pensjonforvalter.tp',
		'pensjonforvalter.alderspensjon',
		'pensjonforvalter.uforetrygd',
		'pensjonforvalter.afpOffentlig',
		'arenaforvalter',
		'sykemelding',
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
