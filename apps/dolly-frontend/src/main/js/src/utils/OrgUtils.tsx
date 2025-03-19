import { ArbeidsgiverTyper } from '@/components/fagsystem/aareg/AaregTypes'

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
	formMethods,
	path,
	opplysningspliktigPath,
	organisasjon,
) => {
	const validEnhetstyper = ['BEDR', 'AAFY']

	if (!org) {
		formMethods.setError(`manual.${path}`, { message: 'Skriv inn org' })
		return
	}
	if (!organisasjon) {
		formMethods.setError(`manual.${path}`, { message: 'Fant ikke organisasjonen' })
		return
	}

	if (!validEnhetstyper.includes(organisasjon?.enhetstype)) {
		formMethods.setError(`manual.${path}`, {
			message: 'Organisasjonen må være av type BEDR eller AAFY',
		})
		return
	}
	if (!organisasjon?.juridiskEnhet) {
		if (organisasjon?.overenhet) {
			opplysningspliktigPath &&
				formMethods.setValue(`${opplysningspliktigPath}`, organisasjon.overenhet)
		} else {
			formMethods.setError(`manual.${path}`, {
				message: 'Organisasjonen mangler juridisk enhet',
			})
			return
		}
	}
	opplysningspliktigPath &&
		organisasjon?.juridiskEnhet &&
		formMethods.setValue(opplysningspliktigPath, organisasjon?.juridiskEnhet)
	formMethods.setValue(path, organisasjon?.organisasjonsnummer)

	formMethods.clearErrors(`manual.${path}`)
	formMethods.clearErrors(path)
}
