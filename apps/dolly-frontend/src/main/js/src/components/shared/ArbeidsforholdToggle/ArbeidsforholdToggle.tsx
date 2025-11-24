import React, { useContext } from 'react'
import { Kategori } from '@/components/ui/form/kategori/Kategori'
import { useFormContext, UseFormReturn } from 'react-hook-form'
import { useCurrentBruker } from '@/utils/hooks/useBruker'
import {
	useDollyFasteDataOrganisasjoner,
	useDollyOrganisasjoner,
} from '@/utils/hooks/useDollyOrganisasjoner'
import { getEgneOrganisasjoner } from '@/utils/EgneOrganisasjoner'
import { ArbeidsgiverTyper } from '@/components/fagsystem/aareg/AaregTypes'
import Loading from '@/components/ui/loading/Loading'
import {
	BestillingsveilederContext,
	BestillingsveilederContextType,
} from '@/components/bestillingsveileder/BestillingsveilederContext'
import { hentAaregEksisterendeData } from '@/components/fagsystem/aareg/form/utils'
import { useArbeidsgiverType } from './useArbeidsgiverType'
import { useOrganisasjonValidation } from './useOrganisasjonValidation'
import { ArbeidsgiverToggleButtons } from './ArbeidsgiverToggleButtons'
import { ArbeidsgiverInputFields } from './ArbeidsgiverInputFields'

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

	const { typeArbeidsgiver, setLocalArbeidsgiverType } = useArbeidsgiverType({
		formMethods,
		path,
		watchedOrgnr,
		watchedPers,
		fasteOrganisasjoner: fasteOrganisasjoner || [],
		egneOrganisasjoner: egneOrganisasjoner || [],
		useFormState,
		arbeidsgiverTypeFromForm,
	})

	const { organisasjoner, loading, error } = useOrganisasjonValidation({
		formMethods,
		organisasjonPath,
		watchedOrgnr,
		useValidation,
	})

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

	const currentValue = typeArbeidsgiver ?? ArbeidsgiverTyper.felles

	const toggleContent = (
		<div className="toggle--wrapper" key={idx ?? `${path}-toggle`}>
			<ArbeidsgiverToggleButtons
				value={currentValue}
				onChange={handleToggleChange}
				isDisabled={erLaastArbeidsforhold}
				disablePrivat={disablePrivat}
				idx={idx}
				path={path}
			/>
			<div style={{ marginTop: '10px' }} className="flexbox--full-width">
				<ArbeidsgiverInputFields
					formMethods={formMethods}
					currentValue={currentValue}
					path={path}
					organisasjonPath={organisasjonPath}
					personPath={personPath}
					watchedOrgnr={watchedOrgnr}
					organisasjoner={organisasjoner}
					validationError={error}
					validationLoading={loading}
					afterChange={afterChange}
					showMiljoeInfo={showMiljoeInfo}
					useValidation={useValidation}
					useFormState={useFormState}
					isDisabled={erLaastArbeidsforhold}
					disablePrivat={disablePrivat}
					title={title}
					onOrgChange={handleOrgChange}
				/>
			</div>
		</div>
	)

	if (useKategori) {
		return <Kategori title="Arbeidsgiver">{toggleContent}</Kategori>
	}

	return toggleContent
}
