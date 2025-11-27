import React from 'react'
import { UseFormReturn } from 'react-hook-form'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { EgneOrganisasjoner } from '@/utils/EgneOrganisasjoner'
import { ArbeidsgiverTyper } from '@/components/fagsystem/aareg/AaregTypes'
import { OrganisasjonMedArbeidsforholdSelect } from '@/components/organisasjonSelect'
import { OrganisasjonForvalterSelect } from '@/components/organisasjonSelect/OrganisasjonForvalterSelect'
import { ArbeidsgiverIdent } from '@/components/fagsystem/aareg/form/partials/arbeidsgiverIdent'

type ArbeidsgiverInputFieldsProps = {
	formMethods: UseFormReturn
	currentValue: ArbeidsgiverTyper
	path: string
	organisasjonPath: string
	personPath: string
	watchedOrgnr: string
	organisasjoner: any[]
	validationError: any
	validationLoading: boolean
	afterChange?: (value: any) => any
	showMiljoeInfo: boolean
	useValidation: boolean
	useFormState: boolean
	isDisabled: boolean
	disablePrivat: boolean
	title: string
	onOrgChange: (event: any) => void
}

export const ArbeidsgiverInputFields = ({
	formMethods,
	currentValue,
	path,
	organisasjonPath,
	personPath,
	watchedOrgnr,
	organisasjoner,
	validationError,
	validationLoading,
	afterChange,
	showMiljoeInfo,
	useValidation,
	useFormState,
	isDisabled,
	disablePrivat,
	title,
	onOrgChange,
}: ArbeidsgiverInputFieldsProps) => {
	if (currentValue === ArbeidsgiverTyper.felles) {
		return (
			<div title={title}>
				<OrganisasjonMedArbeidsforholdSelect
					path={organisasjonPath}
					label={'Organisasjonsnummer'}
					isDisabled={isDisabled}
					placeholder={'Velg organisasjon ...'}
					afterChange={afterChange}
				/>
			</div>
		)
	}

	if (currentValue === ArbeidsgiverTyper.egen) {
		return (
			<div className="flex-box" title={title}>
				<EgneOrganisasjoner
					path={organisasjonPath}
					afterChange={afterChange}
					showMiljoeInfo={showMiljoeInfo}
					handleChange={onOrgChange}
					filterValidEnhetstyper={true}
					isDisabled={isDisabled}
				/>
			</div>
		)
	}

	if (currentValue === ArbeidsgiverTyper.fritekst) {
		if (useValidation) {
			return (
				<OrganisasjonForvalterSelect
					path={organisasjonPath}
					parentPath={path}
					value={watchedOrgnr}
					success={
						organisasjoner?.length > 0 &&
						!validationError &&
						!formMethods.getFieldState(`manual.${organisasjonPath}`)?.error
					}
					error={validationError}
					loading={validationLoading}
					onTextBlur={(event) => {
						formMethods.setValue(organisasjonPath, event.target.value || null)
					}}
				/>
			)
		}
		return (
			<FormTextInput
				name={organisasjonPath}
				label={'Organisasjonsnummer'}
				size="xlarge"
				onBlur={afterChange}
				isDisabled={isDisabled}
				title={title}
			/>
		)
	}

	if (currentValue === ArbeidsgiverTyper.privat) {
		return (
			<div className="flexbox--flex-wrap">
				{useFormState ? (
					<ArbeidsgiverIdent
						path={personPath}
						isDisabled={isDisabled || disablePrivat}
						title={title}
					/>
				) : (
					<FormTextInput name={personPath} label="Personidentifikator" size="xlarge" />
				)}
			</div>
		)
	}

	return null
}
