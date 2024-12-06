import { ArbeidsgiverTyper } from '@/components/fagsystem/aareg/AaregTypes'
import { OrgserviceApi } from '@/service/Api'

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

export const getOrgType = (orgnr: string, fasteOrganisasjoner: any, egneOrganisasjoner: any) => {
	if (
		!orgnr ||
		orgnr === '' ||
		fasteOrganisasjoner
			?.map((organisasjon: any) => organisasjon?.orgnummer)
			?.some((org: string) => org === orgnr)
	) {
		return ArbeidsgiverTyper.felles
	} else if (
		egneOrganisasjoner
			?.map((organisasjon: any) => organisasjon?.orgnr)
			?.some((org: string) => org === orgnr)
	) {
		return ArbeidsgiverTyper.egen
	} else {
		return ArbeidsgiverTyper.fritekst
	}
}

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
