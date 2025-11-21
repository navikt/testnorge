import React, { useContext, useEffect, useRef, useState } from 'react'
import { ToggleGroup } from '@navikt/ds-react'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { Kategori } from '@/components/ui/form/kategori/Kategori'
import { useFormContext, UseFormReturn } from 'react-hook-form'
import { useCurrentBruker } from '@/utils/hooks/useBruker'
import {
	useDollyFasteDataOrganisasjoner,
	useDollyOrganisasjoner,
	useOrganisasjonForvalter,
} from '@/utils/hooks/useDollyOrganisasjoner'
import { EgneOrganisasjoner, getEgneOrganisasjoner } from '@/utils/EgneOrganisasjoner'
import { ArbeidsgiverTyper } from '@/components/fagsystem/aareg/AaregTypes'
import Loading from '@/components/ui/loading/Loading'
import { OrganisasjonMedArbeidsforholdSelect } from '@/components/organisasjonSelect'
import { OrganisasjonForvalterSelect } from '@/components/organisasjonSelect/OrganisasjonForvalterSelect'
import styled from 'styled-components'
import { arbeidsgiverToggleValues, handleManualOrgChange } from '@/utils/OrgUtils'
import { ArbeidsgiverIdent } from '@/components/fagsystem/aareg/form/partials/arbeidsgiverIdent'
import {
	BestillingsveilederContext,
	BestillingsveilederContextType,
} from '@/components/bestillingsveileder/BestillingsveilederContext'
import { hentAaregEksisterendeData } from '@/components/fagsystem/aareg/form/utils'

const ToggleArbeidsgiver = styled(ToggleGroup)`
	display: grid;
	grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
	background-color: #ffffff;
`

const DisabledToggleArbeidsgiver = styled(ToggleGroup)`
	display: grid;
	grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));

	:hover {
		background-color: white;
		cursor: default;
	}

	&&& {
		button {
			color: #aab0ba;
		}

		.navds-toggle-group__button[aria-checked='true'] {
			background-color: #aab0ba;
			color: white;

			:hover {
				background-color: #aab0ba;
				cursor: default;
			}
		}
	}
`

type ArbeidsforholdToggleProps = {
	formMethods?: UseFormReturn
	path: string
	organisasjonPath?: string
	personPath?: string
	idx?: number
	afterChange?: (value: any) => any
	showMiljoeInfo?: boolean
	onToggle?: (value: any) => any
	disablePrivat?: boolean
	useKategori?: boolean
	useValidation?: boolean
	useFormState?: boolean
	initialArbeidsgiverOrg?: any
	initialArbeidsgiverPers?: any
}

export const ArbeidsforholdToggle = ({
	formMethods: externalFormMethods,
	path,
	organisasjonPath: customOrganisasjonPath,
	personPath: customPersonPath,
	idx,
	onToggle,
	afterChange,
	showMiljoeInfo = true,
	disablePrivat = false,
	useKategori = false,
	useValidation = false,
	useFormState = false,
	initialArbeidsgiverOrg,
	initialArbeidsgiverPers,
}: ArbeidsforholdToggleProps) => {
	const contextFormMethods = useFormContext()
	const formMethods = externalFormMethods || contextFormMethods
	const { currentBruker } = useCurrentBruker()

	const { organisasjoner: fasteOrganisasjoner, loading: fasteOrganisasjonerLoading } =
		useDollyFasteDataOrganisasjoner(true)

	const { organisasjoner: brukerOrganisasjoner, loading: brukerOrganisasjonerLoading } =
		useDollyOrganisasjoner(currentBruker?.brukerId)
	const egneOrganisasjoner = getEgneOrganisasjoner(brukerOrganisasjoner)

	const { personFoerLeggTil } = useContext(
		BestillingsveilederContext,
	) as BestillingsveilederContextType
	const tidligereAaregdata = hentAaregEksisterendeData(personFoerLeggTil)

	const erLaastArbeidsforhold = typeof idx === 'number' && idx < (tidligereAaregdata?.length ?? 0)

	const organisasjonPath = customOrganisasjonPath || `${path}.arbeidsgiver.orgnummer`
	const personPath = customPersonPath || `${path}.arbeidsgiver.ident`

	const watchedOrgnr = formMethods.watch(organisasjonPath)
	const watchedPers = formMethods.watch(personPath)
	const arbeidsgiverTypeFromForm = formMethods.watch(`${path}.arbeidsgiverType`) as
		| ArbeidsgiverTyper
		| undefined

	const [localArbeidsgiverType, setLocalArbeidsgiverType] = useState<ArbeidsgiverTyper>(
		ArbeidsgiverTyper.felles,
	)
	const [initialized, setInitialized] = useState(false)

	const typeArbeidsgiver = useFormState ? arbeidsgiverTypeFromForm : localArbeidsgiverType

	useEffect(() => {
		if (!fasteOrganisasjoner || !brukerOrganisasjoner) return
		if (initialized) return

		const getInitialType = () => {
			if (watchedPers) {
				return ArbeidsgiverTyper.privat
			} else if (
				!watchedOrgnr ||
				fasteOrganisasjoner
					?.map((organisasjon: any) => organisasjon?.orgnummer)
					?.some((org: string) => org === watchedOrgnr)
			) {
				return ArbeidsgiverTyper.felles
			} else if (
				egneOrganisasjoner
					?.map((organisasjon: any) => organisasjon?.orgnr)
					?.some((org: string) => org === watchedOrgnr)
			) {
				return ArbeidsgiverTyper.egen
			} else {
				return ArbeidsgiverTyper.fritekst
			}
		}

		if (useFormState && !arbeidsgiverTypeFromForm) {
			const newType = getInitialType()
			formMethods.setValue(`${path}.arbeidsgiverType`, newType, {
				shouldDirty: false,
			})
			setInitialized(true)
		} else if (!useFormState) {
			const newType = getInitialType()
			setLocalArbeidsgiverType(newType)
			setInitialized(true)
		}
	}, [
		fasteOrganisasjoner,
		brukerOrganisasjoner,
		arbeidsgiverTypeFromForm,
		useFormState,
		initialized,
		watchedOrgnr,
		watchedPers,
		egneOrganisasjoner,
		path,
	])

	const { organisasjoner, loading, error, hasBeenCalled } = useOrganisasjonForvalter(
		useValidation ? [watchedOrgnr] : [],
	)
	const organisasjon = organisasjoner?.[0]?.q1 || organisasjoner?.[0]?.q2

	const previousStateRef = useRef<{
		orgnr?: string
		hasError: boolean
		hasOrganisation: boolean
	}>({
		hasError: false,
		hasOrganisation: false,
	})

	useEffect(() => {
		if (!useValidation) return
		if (loading) return

		const isValidLength = watchedOrgnr?.length === 9
		const hasInvalidLength = watchedOrgnr && watchedOrgnr.length > 0 && !isValidLength
		const currentHasError = !organisasjon && watchedOrgnr && isValidLength && hasBeenCalled
		const currentHasOrganisation = !!organisasjon && isValidLength
		const prevState = previousStateRef.current

		queueMicrotask(() => {
			if (
				currentHasOrganisation &&
				(!prevState.hasOrganisation || prevState.orgnr !== watchedOrgnr)
			) {
				formMethods.clearErrors(`manual.${organisasjonPath}`)
				handleManualOrgChange(watchedOrgnr, formMethods, organisasjonPath, null, organisasjon)
				previousStateRef.current = {
					orgnr: watchedOrgnr,
					hasError: false,
					hasOrganisation: true,
				}
			} else if (currentHasError && (!prevState.hasError || prevState.orgnr !== watchedOrgnr)) {
				formMethods.setError(`manual.${organisasjonPath}`, {
					message: 'Fant ikke organisasjonen',
				})
				previousStateRef.current = {
					orgnr: watchedOrgnr,
					hasError: true,
					hasOrganisation: false,
				}
			} else if (hasInvalidLength && (!prevState.hasError || prevState.orgnr !== watchedOrgnr)) {
				formMethods.setError(`manual.${organisasjonPath}`, {
					message: 'Organisasjonsnummer må være 9 siffer',
				})
				previousStateRef.current = {
					orgnr: watchedOrgnr,
					hasError: true,
					hasOrganisation: false,
				}
			} else if (isValidLength && prevState.hasError && prevState.orgnr !== watchedOrgnr) {
				formMethods.clearErrors(`manual.${organisasjonPath}`)
				previousStateRef.current = {
					orgnr: watchedOrgnr,
					hasError: false,
					hasOrganisation: false,
				}
			} else if (!watchedOrgnr && prevState.hasError) {
				formMethods.clearErrors(`manual.${organisasjonPath}`)
				previousStateRef.current = {
					orgnr: watchedOrgnr,
					hasError: false,
					hasOrganisation: false,
				}
			}
		})
	}, [organisasjon, loading, useValidation, watchedOrgnr, organisasjonPath, hasBeenCalled])

	const handleToggleChange = (value: ArbeidsgiverTyper) => {
		onToggle && onToggle(value)

		if (useFormState) {
			formMethods.setValue(`${path}.arbeidsgiverType`, value, {
				shouldDirty: true,
				shouldTouch: true,
			})
			if (value === ArbeidsgiverTyper.privat && initialArbeidsgiverPers) {
				formMethods.resetField(`${path}.arbeidsgiver`, {
					defaultValue: initialArbeidsgiverPers,
				})
			} else if (initialArbeidsgiverOrg) {
				formMethods.resetField(`${path}.arbeidsgiver`, {
					defaultValue: initialArbeidsgiverOrg,
				})
			}
		} else {
			setLocalArbeidsgiverType(value)
			if (value === ArbeidsgiverTyper.privat) {
				formMethods.setValue(personPath, '')
				formMethods.setValue(organisasjonPath, undefined)
			} else {
				formMethods.setValue(organisasjonPath, '')
				formMethods.setValue(personPath, undefined)
			}
		}

		formMethods.clearErrors(path)
		formMethods.clearErrors(`manual.${path}`)
		formMethods.clearErrors(`${path}.arbeidsgiver`)
		formMethods.clearErrors(`manual.${path}.arbeidsgiver`)
	}

	const handleOrgChange = (event: any) => {
		const value = event?.value || event?.orgnr
		formMethods.setValue(organisasjonPath, value, {
			shouldDirty: true,
			shouldTouch: true,
		})
		formMethods.clearErrors(path)
		formMethods.clearErrors(`manual.${path}`)
		formMethods.clearErrors(`${path}.arbeidsgiver`)
		formMethods.clearErrors(`manual.${path}.arbeidsgiver`)
		formMethods.trigger(organisasjonPath)
	}

	if (fasteOrganisasjonerLoading || brukerOrganisasjonerLoading) {
		return <Loading label="Laster organisasjoner ..." />
	}

	const title = erLaastArbeidsforhold
		? 'Kan ikke endre arbeidsgiver på eksisterende arbeidsforhold'
		: ''

	const renderArbeidsgiverToggleItems = (disablePrivat: boolean) =>
		arbeidsgiverToggleValues
			.filter((t) => !(disablePrivat && t.value === ArbeidsgiverTyper.privat))
			.map((t) => (
				<ToggleGroup.Item key={t.value} value={t.value}>
					{t.label}
				</ToggleGroup.Item>
			))

	const currentValue = typeArbeidsgiver ?? ArbeidsgiverTyper.felles

	const toggleContent = (
		<div className="toggle--wrapper" key={idx ?? `${path}-toggle`}>
			{erLaastArbeidsforhold ? (
				<DisabledToggleArbeidsgiver
					onChange={() => null}
					value={currentValue}
					size={'small'}
					fill
					key={idx ?? `${path}-disabled`}
					title={'Kan ikke endre arbeidsgivertype på eksisterende arbeidsforhold'}
				>
					{renderArbeidsgiverToggleItems(disablePrivat)}
				</DisabledToggleArbeidsgiver>
			) : (
				<ToggleArbeidsgiver
					onChange={(value: string) => handleToggleChange(value as ArbeidsgiverTyper)}
					value={currentValue}
					size={'small'}
					fill
					key={idx ?? `${path}-enabled`}
				>
					{renderArbeidsgiverToggleItems(disablePrivat)}
				</ToggleArbeidsgiver>
			)}
			<div style={{ marginTop: '10px' }} className="flexbox--full-width">
				{currentValue === ArbeidsgiverTyper.felles && (
					<div title={title}>
						<OrganisasjonMedArbeidsforholdSelect
							path={organisasjonPath}
							label={'Organisasjonsnummer'}
							isDisabled={erLaastArbeidsforhold}
							placeholder={'Velg organisasjon ...'}
							afterChange={afterChange}
						/>
					</div>
				)}
				{currentValue === ArbeidsgiverTyper.egen && (
					<div className="flex-box" title={title}>
						<EgneOrganisasjoner
							path={organisasjonPath}
							afterChange={afterChange}
							showMiljoeInfo={showMiljoeInfo}
							handleChange={handleOrgChange}
							filterValidEnhetstyper={true}
							isDisabled={erLaastArbeidsforhold}
						/>
					</div>
				)}
				{currentValue === ArbeidsgiverTyper.fritekst && (
					<>
						{useValidation ? (
							<OrganisasjonForvalterSelect
								path={organisasjonPath}
								parentPath={path}
								value={watchedOrgnr}
								success={
									organisasjoner?.length > 0 &&
									!error &&
									!formMethods.getFieldState(`manual.${organisasjonPath}`)?.error
								}
								error={error}
								loading={loading}
								onTextBlur={(event) => {
									formMethods.setValue(organisasjonPath, event.target.value || null)
								}}
							/>
						) : (
							<FormTextInput
								name={organisasjonPath}
								label={'Organisasjonsnummer'}
								size="xlarge"
								onBlur={afterChange}
								isDisabled={erLaastArbeidsforhold}
								title={title}
							/>
						)}
					</>
				)}
				{currentValue === ArbeidsgiverTyper.privat && (
					<div className="flexbox--flex-wrap">
						{useFormState ? (
							<ArbeidsgiverIdent
								path={personPath}
								isDisabled={erLaastArbeidsforhold || disablePrivat}
								title={title}
							/>
						) : (
							<FormTextInput name={personPath} label="Personidentifikator" size="xlarge" />
						)}
					</div>
				)}
			</div>
		</div>
	)

	if (useKategori) {
		return <Kategori title="Arbeidsgiver">{toggleContent}</Kategori>
	}

	return toggleContent
}
