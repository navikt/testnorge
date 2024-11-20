import _ from 'lodash'
import { isAfter, isBefore, isEqual } from 'date-fns'
import { Relasjon } from '@/components/fagsystem/pdlf/PdlTypes'
import { ArbeidsgiverTyper } from '@/components/fagsystem/aareg/AaregTypes'
import { OrgserviceApi } from '@/service/Api'

export const testDatoFom = (val, tomPath, feilmelding = 'Dato må være før til-dato') => {
	return val.test('is-before-tom', feilmelding, (value, testContext) => {
		const datoTom = _.get(testContext.parent, tomPath)
		if (!value || !datoTom) return true
		if (isEqual(value, datoTom)) return true
		return isBefore(value, datoTom)
	})
}

export const testDatoTom = (val, fomPath, feilmelding = 'Dato må være etter fra-dato') => {
	return val.test('is-after-fom', feilmelding, (value, testContext) => {
		const datoFom = _.get(testContext.parent, fomPath)
		if (!value || !datoFom) return true
		if (isEqual(value, datoFom)) return true
		return isAfter(value, datoFom)
	})
}

export const getEksisterendeNyPerson = (
	relasjoner: Array<Relasjon>,
	ident: String,
	relasjonTyper: Array<String>,
) => {
	const relasjon = relasjoner?.find(
		(relasjon) =>
			relasjon?.relatertPerson?.ident === ident && relasjonTyper.includes(relasjon.relasjonType),
	)

	if (!relasjon) {
		return null
	}

	return {
		value: relasjon?.relatertPerson?.ident,
		label: `${relasjon?.relatertPerson?.ident} - ${relasjon?.relatertPerson?.navn?.[0]?.fornavn} ${relasjon?.relatertPerson?.navn?.[0]?.etternavn}`,
	}
}

export const getRandomValue = (liste: Array<any>) => {
	if (!liste || liste?.length < 1) {
		return null
	}
	const random = Math.floor(Math.random() * liste.length) //NOSONAR not used in secure contexts
	return liste[random]
}

export const arbeidsgiverToggleValues = [
	{
		value: ArbeidsgiverTyper.felles,
		label: 'Felles organisasjoner',
	},
	{
		value: ArbeidsgiverTyper.egen,
		label: 'Egen organisasjon',
	},
	{
		value: ArbeidsgiverTyper.fritekst,
		label: 'Skriv inn org.nr.',
	},
	{
		value: ArbeidsgiverTyper.privat,
		label: 'Privat arbeidsgiver',
	},
]

export const handleManualOrgChange = (
	org: string,
	miljo: string,
	formMethods,
	path,
	setLoading,
	setSuccess,
	organisasjon,
	opplysningspliktigPath,
) => {
	const validEnhetstyper = ['BEDR', 'AAFY']
	if (!org || !miljo) {
		return
	}
	formMethods.clearErrors(path)
	setLoading(true)
	setSuccess(false)
	OrgserviceApi.getOrganisasjonInfo(org, miljo)
		.then((response: { data: { enhetType: string; juridiskEnhet: any; orgnummer: any } }) => {
			setLoading(false)
			if (!validEnhetstyper.includes(response.data.enhetType)) {
				formMethods.setError(path, { message: 'Organisasjonen må være av type BEDR eller AAFY' })
				return
			}
			if (!response.data.juridiskEnhet) {
				if (organisasjon?.overenhet) {
					opplysningspliktigPath &&
						formMethods.setValue(`${opplysningspliktigPath}`, organisasjon.overenhet)
				} else {
					formMethods.setError(path, { message: 'Organisasjonen mangler juridisk enhet' })
					return
				}
			}
			setSuccess(true)
			opplysningspliktigPath &&
				response.data.juridiskEnhet &&
				formMethods.setValue(`${opplysningspliktigPath}`, response.data.juridiskEnhet)
			formMethods.setValue(`${path}`, response.data.orgnummer)
		})
		.catch(() => {
			setLoading(false)
			formMethods.setError(path, { message: 'Fant ikke organisasjonen i ' + miljo })
		})
}

//TODO: Se paa stedene den er brukt, de boer sikkert skrives om
export const ORGANISASJONSTYPE_TOGGLE = 'ORGANISASJONSTYPE_TOGGLE'
