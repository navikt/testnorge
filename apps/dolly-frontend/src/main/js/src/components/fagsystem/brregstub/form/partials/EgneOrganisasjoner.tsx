import React, { useEffect, useState } from 'react'
import _get from 'lodash/get'
import { Adresse, Organisasjon } from '~/service/services/organisasjonforvalter/types'
import { Alert } from '@navikt/ds-react'
import { useCurrentBruker } from '~/utils/hooks/useBruker'
import { EgneOrgSelect } from '~/components/ui/form/inputs/select/EgneOrgSelect'
import { useOrganisasjoner } from '~/utils/hooks/useOrganisasjoner'
import { useFormikContext } from 'formik'
import _has from 'lodash/has'
import { OrgforvalterApi } from '~/service/Api'
import { OrgMiljoeInfoVisning } from '~/components/fagsystem/brregstub/form/partials/OrgMiljoeInfoVisning'

interface OrgProps {
	path: string
	label?: string
	handleChange: (event: React.ChangeEvent<any>) => void
	warningMessage?: string
	filterValidEnhetstyper?: boolean
}

const getAdresseWithAdressetype = (adresser: Adresse[], adressetype: string) => {
	if (!adresser || adresser.length === 0) {
		return []
	}

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
	if (!enheter) return ''
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

const getOversteJuridiskEnhet = (orgnr: string, enheter: Organisasjon[]) => {
	if (!enheter) return ''
	let oversteJuridiskEnhet = ''
	enheter.forEach((enhet) => {
		const getUnderenheter = (underenheter: Organisasjon[]) => {
			underenheter.forEach((underenhet) => {
				if (underenhet.organisasjonsnummer === orgnr) {
					oversteJuridiskEnhet = enhet.organisasjonsnummer
				} else if (underenhet.underenheter) {
					getUnderenheter(underenhet.underenheter)
				}
			})
		}
		if (enhet.underenheter) {
			getUnderenheter(enhet.underenheter)
		}
	})
	return oversteJuridiskEnhet
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
			forretningsAdresse: fAdresser?.length > 0 ? fAdresser[0] : null,
			postAdresse: pAdresser?.length > 0 ? pAdresser[0] : null,
			juridiskEnhet: juridiskEnhet,
		}
	})
}

export const EgneOrganisasjoner = ({
	path,
	label,
	handleChange,
	warningMessage,
	filterValidEnhetstyper,
}: OrgProps) => {
	const formikBag = useFormikContext()
	const [orgnr, setOrgnr] = useState(null)
	const [miljoer, setMiljoer] = useState([])
	const [miljoeError, setMiljoeError] = useState(false)
	const [miljoeLoading, setMiljoeLoading] = useState(false)

	const {
		currentBruker: { brukerId },
	} = useCurrentBruker()

	useEffect(() => {
		if (orgnr) {
			setMiljoeLoading(true)
			setMiljoeError(false)
			setMiljoer([])
			OrgforvalterApi.getOrganisasjonerMiljoeInfo(orgnr)
				.then((response: any) => {
					const orgInfo = response?.data
					setMiljoer(orgInfo ? Object.keys(orgInfo) : [])
					setMiljoeLoading(false)
					setMiljoeError(false)
				})
				.catch(() => {
					setMiljoer([])
					setMiljoeLoading(false)
					setMiljoeError(true)
				})
		}
	}, [orgnr])

	const { organisasjoner, loading, error } = useOrganisasjoner(brukerId)
	const egneOrganisasjoner = getEgneOrganisasjoner(organisasjoner)
	const harEgneOrganisasjoner = egneOrganisasjoner && egneOrganisasjoner.length > 0
	const validEnhetstyper = ['BEDR', 'AAFY']

	const getFilteredOptions = (organisasjoner: any) => {
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

	const sjekkOrganisasjoner = () => {
		if (_get(formikBag.values, path) === '') {
			if (!_has(formikBag.errors, path)) {
				formikBag.setFieldError(path, 'Feltet er påkrevd')
			}
			return { feilmelding: 'Feltet er påkrevd' }
		} else if (path.includes('amelding')) {
			//@ts-ignore
			const valgtOrgnr = formikBag.values?.aareg?.[0]?.amelding?.flatMap((a) =>
				a.arbeidsforhold?.flatMap((f) => f.arbeidsgiver?.orgnummer)
			)
			const valgtJuridiskEnhet = valgtOrgnr?.map((org) =>
				getOversteJuridiskEnhet(org, organisasjoner)
			)
			const valgtJuridiskEnhetFiltrert = valgtJuridiskEnhet?.filter((org) => org !== '')
			const juridiskEnhetErLik = valgtJuridiskEnhetFiltrert?.every((org) => {
				if (org === valgtJuridiskEnhetFiltrert[0]) {
					return true
				}
			})
			if (!juridiskEnhetErLik && !_has(formikBag.errors, path)) {
				formikBag.setFieldError(path, 'Alle organisasjoner må tilhøre samme overordnet enhet')
			}
			return juridiskEnhetErLik
				? null
				: { feilmelding: 'Alle organisasjoner må tilhøre samme overordnet enhet' }
		}
		return null
	}

	return (
		<>
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
			{!error && (
				<EgneOrgSelect
					name={path}
					label={label ? label : 'Organisasjonsnummer'}
					options={
						filterValidEnhetstyper ? getFilteredOptions(egneOrganisasjoner) : egneOrganisasjoner
					}
					isLoading={loading}
					size="xlarge"
					onChange={(event) => {
						setOrgnr(event.value)
						handleChange(event)
					}}
					value={_get(formikBag.values, path)}
					feil={sjekkOrganisasjoner()}
					isClearable={false}
				/>
			)}
			{orgnr && (
				<OrgMiljoeInfoVisning miljoer={miljoer} loading={miljoeLoading} error={miljoeError} />
			)}
		</>
	)
}
