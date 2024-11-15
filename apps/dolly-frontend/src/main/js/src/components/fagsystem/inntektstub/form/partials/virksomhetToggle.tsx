import React, { useEffect, useState } from 'react'
import styled from 'styled-components'
import { Alert, ToggleGroup } from '@navikt/ds-react'
import { ArbeidsgiverTyper } from '@/components/fagsystem/aareg/AaregTypes'
import { useFormContext } from 'react-hook-form'
import {
	EgneOrganisasjoner,
	getEgneOrganisasjoner,
} from '@/components/fagsystem/brregstub/form/partials/EgneOrganisasjoner'
import Loading from '@/components/ui/loading/Loading'
import { OrganisasjonMedArbeidsforholdSelect } from '@/components/organisasjonSelect'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { ArbeidsgiverIdent } from '@/components/fagsystem/aareg/form/partials/arbeidsgiverIdent'
import { useCurrentBruker } from '@/utils/hooks/useBruker'
import {
	useDollyFasteDataOrganisasjoner,
	useFasteDataOrganisasjon,
	useOrganisasjoner,
} from '@/utils/hooks/useOrganisasjoner'
import { OrganisasjonMedMiljoeSelect } from '@/components/organisasjonSelect/OrganisasjonMedMiljoeSelect'
import { useBoolean } from 'react-use'
import { OrgserviceApi } from '@/service/Api'
import { useDollyEnvironments } from '@/utils/hooks/useEnvironments'

const ToggleArbeidsgiver = styled(ToggleGroup)`
	display: grid;
	grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
`

const StyledAlert = styled(Alert)`
	margin-top: 10px;
`

type ArbeidsforholdToggleProps = {
	path: string
	idx: number
	fasteOrganisasjoner: any
	brukerOrganisasjoner: any
	egneOrganisasjoner: any
	loadingOrganisasjoner: boolean
}

// TODO: Ingen toggles funker som de skal når man sletter inntektsinformasjon.
// TODO: Men alle under-arrayer ser ut til å fungere som de skal.

export const VirksomhetToggle = ({
	path,
	// orgnummer,
	// aktoertype,
	// fasteOrganisasjoner,
	// brukerOrganisasjoner,
	// egneOrganisasjoner,
	// loadingOrganisasjoner,
	// kanHaPrivatArbeidsgiver,
}: ArbeidsforholdToggleProps) => {
	const formMethods = useFormContext()
	const { currentBruker } = useCurrentBruker()

	const { organisasjoner: fasteOrganisasjoner, loading: fasteOrganisasjonerLoading } =
		useDollyFasteDataOrganisasjoner(true)

	const { organisasjoner: brukerOrganisasjoner, loading: brukerOrganisasjonerLoading } =
		useOrganisasjoner(currentBruker?.brukerId)
	const egneOrganisasjoner = getEgneOrganisasjoner(brukerOrganisasjoner)

	const virksomhetPath = `${path}.virksomhet`
	const opplysningspliktigPath = `${path}.opplysningspliktig`
	console.log('fasteOrganisasjonerLoading: ', fasteOrganisasjonerLoading) //TODO - SLETT MEG
	console.log('brukerOrganisasjonerLoading: ', brukerOrganisasjonerLoading) //TODO - SLETT MEG
	console.log('egneOrganisasjoner: ', egneOrganisasjoner) //TODO - SLETT MEG
	// console.log('orgnummer: ', orgnummer) //TODO - SLETT MEG
	const getArbeidsgiverType = () => {
		const orgnummerLength = 9
		const orgnr = formMethods.watch(virksomhetPath)
		if (!orgnr || orgnr === '' || orgnr?.length === orgnummerLength) {
			if (
				!orgnr ||
				orgnr === '' ||
				fasteOrganisasjoner
					?.map((organisasjon: any) => organisasjon?.orgnr)
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
		} else {
			return ArbeidsgiverTyper.privat
		}
	}

	const [typeArbeidsgiver, setTypeArbeidsgiver] = useState(getArbeidsgiverType())

	// TODO: test fjerning av objekt i array
	useEffect(() => {
		setTypeArbeidsgiver(getArbeidsgiverType())
	}, [fasteOrganisasjoner, brukerOrganisasjoner, formMethods.watch('aareg')?.length])

	const toggleValues = [
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

	const { dollyEnvironments: aktiveMiljoer } = useDollyEnvironments()
	const [success, setSuccess] = useBoolean(false)
	const [loading, setLoading] = useBoolean(false)
	const [environment, setEnvironment] = useState(null)
	const [orgnummer, setOrgnummer] = useState(formMethods.watch(virksomhetPath) || null)
	const { organisasjon } = useFasteDataOrganisasjon(orgnummer)
	// console.log('orgnummer: ', orgnummer) //TODO - SLETT MEG
	// console.log('organisasjon: ', organisasjon) //TODO - SLETT MEG

	const handleToggleChange = (value: ArbeidsgiverTyper) => {
		setTypeArbeidsgiver(value)
		formMethods.setValue(virksomhetPath, '')
		formMethods.setValue(opplysningspliktigPath, '')
		formMethods.clearErrors(path)
		// if (value === ArbeidsgiverTyper.privat) {
		// 	formMethods.resetField(`${path}.arbeidsgiver`, { defaultValue: initialArbeidsgiverPers })
		// } else {
		// 	formMethods.resetField(`${path}.arbeidsgiver`, { defaultValue: initialArbeidsgiverOrg })
		// }
	}

	const handleOrgChange = (value: { juridiskEnhet: string; orgnr: string }) => {
		// console.log('value: ', value) //TODO - SLETT MEG
		opplysningspliktigPath && formMethods.setValue(`${opplysningspliktigPath}`, value.juridiskEnhet)
		formMethods.trigger(opplysningspliktigPath)
		formMethods.setValue(virksomhetPath, value.orgnr)
	}

	const handleManualOrgChange = (org: string, miljo: string) => {
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

	const warningMessage = (
		<StyledAlert variant={'warning'}>
			Du har ingen egne organisasjoner, og kan derfor ikke sende inn A-meldinger for person. For å
			lage dine egne organisasjoner trykk {<a href="/organisasjoner">her</a>}. For å opprette person
			med arbeidsforhold i felles organisasjoner eller andre arbeidsgivere, velg en annen kategori
			ovenfor.
		</StyledAlert>
	)

	if (
		fasteOrganisasjonerLoading ||
		brukerOrganisasjonerLoading
		// ||
		// egneOrganisasjoner.length === 0
	) {
		return <Loading label="Laster organisasjoner ..." />
	}
	// console.log('typeArbeidsgiver: ', typeArbeidsgiver) //TODO - SLETT MEG
	return (
		<div className="toggle--wrapper">
			<ToggleArbeidsgiver
				// @ts-ignore
				onChange={(value: ArbeidsgiverTyper) => handleToggleChange(value)}
				value={typeArbeidsgiver}
				size={'small'}
				fill
			>
				{toggleValues.map((type) => (
					<ToggleGroup.Item key={type.value} value={type.value}>
						{type.label}
					</ToggleGroup.Item>
				))}
			</ToggleArbeidsgiver>
			<div className="flexbox--full-width">
				{typeArbeidsgiver === ArbeidsgiverTyper.felles && (
					<OrganisasjonMedArbeidsforholdSelect
						afterChange={handleOrgChange}
						path={`${path}.virksomhet`}
						label={'Organisasjonsnummer'}
					/>
				)}
				{typeArbeidsgiver === ArbeidsgiverTyper.egen && (
					<EgneOrganisasjoner
						path={`${path}.virksomhet`}
						handleChange={handleOrgChange}
						warningMessage={warningMessage}
						filterValidEnhetstyper={true}
						// isDisabled={erLaastArbeidsforhold}
					/>
				)}
				{typeArbeidsgiver === ArbeidsgiverTyper.fritekst && (
					// <FormTextInput
					// 	name={`${path}.virksomhet`}
					// 	label={'Organisasjonsnummer'}
					// 	size="xlarge"
					// 	onBlur={() => checkAktiveArbeidsforhold()}
					// 	defaultValue={formMethods.watch(`${path}.virksomhet`)}
					// 	// isDisabled={erLaastArbeidsforhold}
					// 	// title={title}
					// />
					<OrganisasjonMedMiljoeSelect
						path={`${path}.virksomhet`}
						environment={environment}
						miljoeOptions={aktiveMiljoer}
						loading={loading}
						success={success}
						onTextBlur={(event) => {
							const org = event.target.value
							setOrgnummer(org)
							handleManualOrgChange(org, environment)
						}}
						onMiljoeChange={(event) => {
							setEnvironment(event.value)
							handleManualOrgChange(orgnummer, event.value)
						}}
					/>
				)}
				{typeArbeidsgiver === ArbeidsgiverTyper.privat && (
					<div className="flexbox--flex-wrap">
						<FormTextInput name={virksomhetPath} label="Virksomhet (fnr/dnr/npid)" size="medium" />
						<FormTextInput name={opplysningspliktigPath} label="Opplysningspliktig" size="medium" />
					</div>
				)}
			</div>
		</div>
	)
}
