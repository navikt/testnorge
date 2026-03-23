import React, { useEffect, useState } from 'react'
import styled from 'styled-components'
import { ToggleGroup } from '@navikt/ds-react'
import { ArbeidsgiverTyper } from '@/components/fagsystem/aareg/AaregTypes'
import { useFormContext } from 'react-hook-form'
import { EgneOrganisasjoner, getEgneOrganisasjoner } from '@/utils/EgneOrganisasjoner'
import Loading from '@/components/ui/loading/Loading'
import { OrganisasjonMedArbeidsforholdSelect } from '@/components/organisasjonSelect'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { useCurrentBruker } from '@/utils/hooks/useBruker'
import {
	useDollyFasteDataOrganisasjoner,
	useDollyOrganisasjoner,
} from '@/utils/hooks/useDollyOrganisasjoner'
import { arbeidsgiverToggleValues, getOrgMiljoer, getOrgType } from '@/utils/OrgUtils'
import { OrganisasjonForvalterSelect } from '@/components/organisasjonSelect/OrganisasjonForvalterSelect'
import { useOrganisasjonValidation } from '@/components/shared/ArbeidsforholdToggle/useOrganisasjonValidation'

const ToggleArbeidsgiver = styled(ToggleGroup)`
	display: grid;
	grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
	margin-bottom: 10px;
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
		useDollyOrganisasjoner(currentBruker?.brukerId)
	const egneOrganisasjoner = getEgneOrganisasjoner(brukerOrganisasjoner)

	const virksomhetPath = `${path}.virksomhet`
	const opplysningspliktigPath = `${path}.opplysningspliktig`

	const getArbeidsgiverType = () => {
		const orgnummerLength = 9
		const orgnr = formMethods.watch(virksomhetPath)
		if (!orgnr || orgnr === '' || orgnr?.length === orgnummerLength) {
			return getOrgType(orgnr, fasteOrganisasjoner, egneOrganisasjoner)
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

	const watchedOrgnr = formMethods.watch(virksomhetPath)
	const isFritekst = typeArbeidsgiver === ArbeidsgiverTyper.fritekst

	const { organisasjoner, loading, error } = useOrganisasjonValidation({
		formMethods,
		organisasjonPath: virksomhetPath,
		watchedOrgnr,
		useValidation: isFritekst,
		parentPath: opplysningspliktigPath,
	})

	const orgMiljoer = getOrgMiljoer(organisasjoner?.[0])

	const handleToggleChange = (value: ArbeidsgiverTyper) => {
		setTypeArbeidsgiver(value)
		formMethods.setValue(virksomhetPath, null)
		formMethods.setValue(opplysningspliktigPath, null)
		formMethods.clearErrors(`manual.${path}`)
		formMethods.clearErrors(`manual.${virksomhetPath}`)
		formMethods.clearErrors(path)
	}

	const handleOrgChange = (value: { juridiskEnhet: string; orgnr: string }) => {
		opplysningspliktigPath && formMethods.setValue(`${opplysningspliktigPath}`, value.juridiskEnhet)
		formMethods.clearErrors(`manual.${path}`)
		formMethods.trigger(opplysningspliktigPath)
		formMethods.setValue(virksomhetPath, value.orgnr)
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
						path={virksomhetPath}
						label={'Organisasjonsnummer'}
						placeholder={'Velg organisasjon ...'}
					/>
				)}
				{typeArbeidsgiver === ArbeidsgiverTyper.egen && (
					<EgneOrganisasjoner
						path={virksomhetPath}
						handleChange={handleOrgChange}
						filterValidEnhetstyper={true}
					/>
				)}
				{isFritekst && (
					<OrganisasjonForvalterSelect
						value={watchedOrgnr}
						path={virksomhetPath}
						parentPath={opplysningspliktigPath}
						success={
							organisasjoner?.length > 0 &&
							!error &&
							!formMethods.getFieldState(`manual.${virksomhetPath}`)?.error
						}
						miljoer={orgMiljoer}
						loading={loading}
						onTextBlur={(event) => {
							formMethods.setValue(virksomhetPath, event.target.value || null)
							formMethods.setValue(opplysningspliktigPath, null)
						}}
					/>
				)}
				{typeArbeidsgiver === ArbeidsgiverTyper.privat && (
					<div className="flexbox--flex-wrap">
						<FormTextInput
							name={virksomhetPath}
							label="Virksomhet (fnr/dnr/npid)"
							size="medium"
							autoFocus
						/>
						<FormTextInput name={opplysningspliktigPath} label="Opplysningspliktig" size="medium" />
					</div>
				)}
			</div>
		</div>
	)
}
