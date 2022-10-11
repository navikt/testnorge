import React from 'react'
import Loading from '~/components/ui/loading/Loading'
import _get from 'lodash/get'
import { FormikProps } from 'formik'
import { Adresse, Organisasjon } from '~/service/services/organisasjonforvalter/types'
import { Alert } from '@navikt/ds-react'
import { useCurrentBruker } from '~/utils/hooks/useBruker'
import { EgneOrgSelect } from '~/components/ui/form/inputs/select/EgneOrgSelect'
import { useOrganisasjoner } from '~/utils/hooks/useOrganisasjoner'

interface OrgProps {
	path: string
	label?: string
	formikBag: FormikProps<{}>
	handleChange: (event: React.ChangeEvent<any>) => void
	warningMessage?: string
	filterValidEnhetstyper?: boolean
}

export const getAdresseWithAdressetype = (adresser: Adresse[], adressetype: string) => {
	return adresser
		.filter((adr) => adr.adressetype === adressetype)
		.map((adr) => ({
			adresselinje1: adr.adresselinjer.length > 0 ? adr.adresselinjer[0] : '',
			kommunenr: adr.kommunenr,
			landkode: adr.landkode,
			postnr: adr.postnr,
			poststed: adr.poststed,
		}))
}

const addAlleVirksomheter = (virksomheter: Organisasjon[], organisasjoner: Organisasjon[]) => {
	for (let org of organisasjoner) {
		virksomheter.push(org)
		if (org.underenheter && org.underenheter.length > 0) {
			addAlleVirksomheter(virksomheter, org.underenheter)
		}
	}
}

const getJuridiskEnhet = (orgnr: string, enheter: Organisasjon[]) => {
	for (const enhet of enheter) {
		if (enhet.underenheter && enhet.underenheter.length > 0) {
			for (const underenhet of enhet.underenheter) {
				if (underenhet.organisasjonsnummer === orgnr) {
					return enhet.organisasjonsnummer
				}
				const juridisk: string = getJuridiskEnhet(orgnr, enhet.underenheter)
				if (juridisk !== '') {
					return juridisk
				}
			}
		}
	}
	return ''
}

const getEgneOrganisasjoner = (organisasjoner: Organisasjon[]) => {
	if (!organisasjoner) {
		return []
	}
	const egneOrg: Organisasjon[] = []
	addAlleVirksomheter(egneOrg, organisasjoner)

	return egneOrg.map((org: Organisasjon) => {
		const fAdresser = getAdresseWithAdressetype(org.adresser, 'FADR')
		const pAdresser = getAdresseWithAdressetype(org.adresser, 'PADR')
		const juridiskEnhet = getJuridiskEnhet(org.organisasjonsnummer, organisasjoner)
		return {
			value: org.organisasjonsnummer,
			label: `${juridiskEnhet ? '   ' : ''}${org.organisasjonsnummer} (${org.enhetstype}) - ${
				org.organisasjonsnavn
			} ${juridiskEnhet ? '' : '   '}`,
			orgnr: org.organisasjonsnummer,
			navn: org.organisasjonsnavn,
			enhetstype: org.enhetstype,
			forretningsAdresse: fAdresser.length > 0 ? fAdresser[0] : null,
			postAdresse: pAdresser.length > 0 ? pAdresser[0] : null,
			juridiskEnhet: juridiskEnhet,
		}
	})
}

export const EgneOrganisasjoner = ({
	path,
	label,
	formikBag,
	handleChange,
	warningMessage,
	filterValidEnhetstyper,
}: OrgProps) => {
	const {
		currentBruker: { brukerId },
	} = useCurrentBruker()

	const { organisasjoner, loading, error } = useOrganisasjoner(brukerId)
	const egneOrganisasjoner = getEgneOrganisasjoner(organisasjoner)

	const harEgneOrganisasjoner = egneOrganisasjoner && egneOrganisasjoner.length > 0
	const validEnhetstyper = ['BEDR', 'AAFY']

	const getFilteredOptions = (organisasjoner: Organisasjon[]) => {
		return organisasjoner
			.filter(
				(virksomhet) =>
					validEnhetstyper.includes(virksomhet.enhetstype) || !virksomhet.juridiskEnhet
			)
			.map((virksomhet) => {
				return {
					...virksomhet,
					isDisabled: !virksomhet.juridiskEnhet,
				}
			})
	}

	return (
		<>
			{loading && <Loading label="Laster organisasjoner" />}
			{error && (
				<Alert variant={'warning'}>
					Noe gikk galt med henting av egne organisasjoner! Prøv på nytt, velg et annet alternativ
					eller kontakt Team Dolly ved vedvarende feil.
				</Alert>
			)}
			{!harEgneOrganisasjoner &&
				!loading &&
				!error &&
				(warningMessage ? (
					warningMessage
				) : (
					<Alert variant={'warning'}>
						Du har ingen egne organisasjoner. For å lage dine egne organisasjoner trykk{' '}
						<a href="/organisasjoner">her</a>.
					</Alert>
				))}
			{harEgneOrganisasjoner && !error && (
				<EgneOrgSelect
					name={path}
					label={label ? label : 'Organisasjonsnummer'}
					options={
						filterValidEnhetstyper ? getFilteredOptions(egneOrganisasjoner) : egneOrganisasjoner
					}
					size="xlarge"
					onChange={handleChange}
					value={_get(formikBag.values, path)}
					feil={
						_get(formikBag.values, path) === '' && {
							feilmelding: 'Feltet er påkrevd',
						}
					}
					isClearable={false}
				/>
			)}
		</>
	)
}
