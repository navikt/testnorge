import React, { useEffect, useState } from 'react'
import { Adresse, Organisasjon } from '@/service/services/organisasjonforvalter/types'
import { useCurrentBruker } from '@/utils/hooks/useBruker'
import { EgneOrgSelect } from '@/components/ui/form/inputs/select/EgneOrgSelect'
import { useDollyOrganisasjoner } from '@/utils/hooks/useDollyOrganisasjoner'
import { OrgforvalterApi } from '@/service/Api'
import { useFormContext } from 'react-hook-form'
import StyledAlert from '@/components/ui/alert/StyledAlert'
import Loading from '@/components/ui/loading/Loading'
import Icon from '@/components/ui/icon/Icon'

interface OrgProps {
	path: string
	label?: string
	handleChange: (event: React.ChangeEvent<any>) => any
	afterChange?: (event: React.ChangeEvent<any>) => any
	showMiljoeInfo?: boolean
	warningMessage?: React.ReactElement
	filterValidEnhetstyper?: boolean
	isDisabled?: boolean
}

type Props = {
	miljoer: string[]
	loading?: boolean
	error?: boolean
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

const OrgMiljoeInfoVisning = ({ miljoer, loading = false, error = false }: Props) => {
	const harMiljoe = miljoer.length > 0
	return (
		<div style={{ padding: '0 0 10px 5px' }}>
			{loading && <Loading label="Sjekker organisasjonsnummer..." />}
			{!loading && error && (
				<div className="flexbox">
					<Icon size={20} kind="report-problem-circle" />
					Feil oppsto i henting av organisasjon-info
				</div>
			)}
			{!loading && !error && (
				<div className="flexbox">
					<Icon
						size={20}
						kind={harMiljoe ? 'feedback-check-circle' : 'report-problem-circle'}
						style={{ marginRight: '5px' }}
					/>
					{harMiljoe
						? 'Organisasjon funnet i miljø: ' + miljoer
						: 'Fant ikke organisasjon i noen miljø'}
				</div>
			)}
		</div>
	)
}

export const getEgneOrganisasjoner = (organisasjoner: Organisasjon[] | undefined) => {
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
	afterChange,
	showMiljoeInfo = true,
	warningMessage,
	filterValidEnhetstyper,
	isDisabled = false,
}: OrgProps) => {
	const [orgnr, setOrgnr] = useState(null)
	const [miljoer, setMiljoer] = useState([])
	const [miljoeError, setMiljoeError] = useState(false)
	const [miljoeLoading, setMiljoeLoading] = useState(false)

	const { currentBruker } = useCurrentBruker()
	const formMethods = useFormContext()

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

	const { organisasjoner, loading, error } = useDollyOrganisasjoner(currentBruker?.brukerId)
	const egneOrganisasjoner = getEgneOrganisasjoner(organisasjoner)
	const harEgneOrganisasjoner = egneOrganisasjoner && egneOrganisasjoner.length > 0
	const validEnhetstyper = ['BEDR', 'AAFY']

	const getFilteredOptions = (organisasjoner: any) => {
		return organisasjoner
			.filter(
				(virksomhet) =>
					validEnhetstyper.includes(virksomhet.enhetstype) || !virksomhet.juridiskEnhet,
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
			{error && (
				<StyledAlert variant={'warning'} size={'small'} style={{ margin: '10px 0' }}>
					Noe gikk galt med henting av egne organisasjoner! Prøv på nytt, velg et annet alternativ
					eller kontakt Team Dolly ved vedvarende feil.
				</StyledAlert>
			)}
			{!harEgneOrganisasjoner &&
				!loading &&
				!error &&
				(warningMessage ? (
					warningMessage
				) : (
					<StyledAlert variant={'warning'} size={'small'} style={{ margin: '10px 0' }}>
						Du har ingen egne organisasjoner. For å lage dine egne organisasjoner trykk{' '}
						<a href="/organisasjoner">her</a>.
					</StyledAlert>
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
						afterChange && afterChange(event)
						formMethods.trigger(path)
					}}
					value={formMethods.watch(path)}
					isClearable={false}
					isDisabled={isDisabled}
				/>
			)}
			{showMiljoeInfo && orgnr && (
				<OrgMiljoeInfoVisning miljoer={miljoer} loading={miljoeLoading} error={miljoeError} />
			)}
		</>
	)
}
