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
import { arbeidsgiverToggleValues } from '@/components/fagsystem/utils'

const ToggleArbeidsgiver = styled(ToggleGroup)`
	display: grid;
	grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
	background-color: #ffffff;
`

type ArbeidsforholdToggleProps = {
	path: string
}

export const VirksomhetToggle = ({ path }: ArbeidsforholdToggleProps) => {
	const formMethods = useFormContext()
	const { currentBruker } = useCurrentBruker()

	const { organisasjoner: fasteOrganisasjoner, loading: fasteOrganisasjonerLoading } =
		useDollyFasteDataOrganisasjoner(true)

	const { organisasjoner: brukerOrganisasjoner, loading: brukerOrganisasjonerLoading } =
		useOrganisasjoner(currentBruker?.brukerId)
	const egneOrganisasjoner = getEgneOrganisasjoner(brukerOrganisasjoner)

	const virksomhetPath = `${path}.virksomhet`
	const opplysningspliktigPath = `${path}.opplysningspliktig`

	const getArbeidsgiverType = () => {
		const orgnummerLength = 9
		const orgnr = formMethods.watch(virksomhetPath)
		if (!orgnr || orgnr === '' || orgnr?.length === orgnummerLength) {
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
		} else {
			return ArbeidsgiverTyper.privat
		}
	}

	const [typeArbeidsgiver, setTypeArbeidsgiver] = useState(getArbeidsgiverType())

	useEffect(() => {
		setTypeArbeidsgiver(getArbeidsgiverType())
	}, [
		fasteOrganisasjoner,
		brukerOrganisasjoner,
		formMethods.watch('inntektstub.inntektsinformasjon')?.length,
	])

	const { dollyEnvironments: aktiveMiljoer } = useDollyEnvironments()
	const [success, setSuccess] = useBoolean(false)
	const [loading, setLoading] = useBoolean(false)
	const [environment, setEnvironment] = useState(null)
	const [orgnummer, setOrgnummer] = useState(formMethods.watch(virksomhetPath) || null)
	const { organisasjon } = useFasteDataOrganisasjon(orgnummer)

	const handleToggleChange = (value: ArbeidsgiverTyper) => {
		setTypeArbeidsgiver(value)
		formMethods.setValue(virksomhetPath, '')
		formMethods.setValue(opplysningspliktigPath, '')
		formMethods.clearErrors(path)
	}

	const handleOrgChange = (value: { juridiskEnhet: string; orgnr: string }) => {
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
			.catch((error) => {
				console.log('error: ', error) //TODO - SLETT MEG
				setLoading(false)
				formMethods.setError(path, { message: 'Fant ikke organisasjonen i ' + miljo })
			})
	}

	if (fasteOrganisasjonerLoading || brukerOrganisasjonerLoading) {
		return <Loading label="Laster organisasjoner ..." />
	}

	return (
		<div className="toggle--wrapper">
			<ToggleArbeidsgiver
				// @ts-ignore
				onChange={(value: ArbeidsgiverTyper) => handleToggleChange(value)}
				value={typeArbeidsgiver}
				size={'small'}
				fill
			>
				{arbeidsgiverToggleValues.map((type) => (
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
						placeholder={'Velg organisasjon ...'}
					/>
				)}
				{typeArbeidsgiver === ArbeidsgiverTyper.egen && (
					<EgneOrganisasjoner
						path={`${path}.virksomhet`}
						handleChange={handleOrgChange}
						filterValidEnhetstyper={true}
					/>
				)}
				{typeArbeidsgiver === ArbeidsgiverTyper.fritekst && (
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
