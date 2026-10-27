import React, { useContext } from 'react'
import { useFormContext } from 'react-hook-form'
import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import Panel from '@/components/ui/panel/Panel'
import { erForsteEllerTest, usePanelError } from '@/components/ui/form/formUtils'
import { FormDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { FormCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'
import { HenvendelseKodeverk } from '@/config/kodeverk'
import { useAlleNavEnheter } from '@/utils/hooks/useNorg2'
import { useHenvendelseTemagrupper } from '@/utils/hooks/useHenvendelse'
import { formatDateTime } from '@/utils/DataFormatter'
import { HenvendelseInfo } from '@/components/fagsystem/henvendelse/HenvendelseTypes'
import {
	BestillingsveilederContext,
	BestillingsveilederContextType,
} from '@/components/bestillingsveileder/BestillingsveilederContext'
import {
	henvendelseAttributt,
	henvendelseMeldingerPath,
	henvendelseSamtalereferaterPath,
	initialMelding,
	initialSamtalereferat,
} from '@/components/fagsystem/henvendelse/initialValues'
import { validation } from '@/components/fagsystem/henvendelse/form/validation'

interface KjedeIdOption {
	value: string
	label: string
}

interface HenvendelseFelterProps {
	path: string
	kjedeIdOptions: KjedeIdOption[]
}

const getKjedeIdOptions = (
	henvendelser: HenvendelseInfo[] | undefined,
	henvendelseType: string,
): KjedeIdOption[] =>
	henvendelser
		?.filter(
			(henvendelse) => henvendelse.henvendelseType === henvendelseType && henvendelse.kjedeId,
		)
		.map((henvendelse) => ({
			value: henvendelse.kjedeId as string,
			label: henvendelse.opprettetDato
				? `${henvendelse.kjedeId} (${formatDateTime(henvendelse.opprettetDato)})`
				: (henvendelse.kjedeId as string),
		})) ?? []

const HenvendelseFelter = ({ path, kjedeIdOptions }: HenvendelseFelterProps) => {
	const { alleNavEnheter, loading: loadingEnheter } = useAlleNavEnheter()
	const { temagrupper, loading: loadingTemagrupper } = useHenvendelseTemagrupper()

	return (
		<>
			<FormSelect
				name={`${path}.temagruppe`}
				label="Temagruppe"
				options={temagrupper}
				isLoading={loadingTemagrupper}
				size="large"
			/>
			<FormSelect
				name={`${path}.tema`}
				label="Tema"
				kodeverk={HenvendelseKodeverk.Tema}
				size="xlarge"
			/>
			<FormSelect
				name={`${path}.enhet`}
				label="Enhet"
				options={alleNavEnheter}
				isLoading={loadingEnheter}
				size="xlarge"
				info="Hvis ikke valgt, vil enhet automatisk settes basert på personens adresse."
			/>
			<FormSelect
				name={`${path}.kjedeId`}
				label="Kjede-ID"
				options={kjedeIdOptions}
				isDisabled={kjedeIdOptions.length === 0}
				size="xlarge"
				info="Velg kjede-ID for å legge meldingen til en eksisterende henvendelse på person. Hvis ingen kjede-ID velges, vil en ny henvendelse opprettes. Se evt. eksisterende henvendelser øverst på siden."
			/>
			<FormTextInput name={`${path}.fritekst`} label="Fritekst" size="xxlarge" />
		</>
	)
}

export const HenvendelseForm = () => {
	const formMethods = useFormContext()
	const opts = useContext(BestillingsveilederContext) as BestillingsveilederContextType
	const harMeldinger = formMethods.watch(henvendelseMeldingerPath)
	const harSamtalereferater = formMethods.watch(henvendelseSamtalereferaterPath)

	const henvendelser: HenvendelseInfo[] | undefined = opts?.personFoerLeggTil?.henvendelser
	const meldingskjedeOptions = getKjedeIdOptions(henvendelser, 'MELDINGSKJEDE')
	const samtalereferatOptions = getKjedeIdOptions(henvendelser, 'SAMTALEREFERAT')

	return (
		<Vis attributt={henvendelseAttributt}>
			<Panel
				heading="Henvendelse"
				hasErrors={usePanelError(henvendelseAttributt)}
				// @ts-ignore
				iconType="salesforce"
				startOpen={erForsteEllerTest(formMethods.getValues(), [henvendelseAttributt])}
			>
				<>
					{harMeldinger && (
						<FormDollyFieldArray
							name={henvendelseMeldingerPath}
							header="Melding"
							newEntry={initialMelding}
							canBeEmpty={false}
						>
							{(path: string, idx: number) => (
								<React.Fragment key={idx}>
									<HenvendelseFelter path={path} kjedeIdOptions={meldingskjedeOptions} />
									<FormCheckbox name={`${path}.tildelMeg`} label="Tildel meg" checkboxMargin />
								</React.Fragment>
							)}
						</FormDollyFieldArray>
					)}
					{harSamtalereferater && (
						<FormDollyFieldArray
							name={henvendelseSamtalereferaterPath}
							header="Samtalereferat"
							newEntry={initialSamtalereferat}
							canBeEmpty={false}
						>
							{(path: string, idx: number) => (
								<React.Fragment key={idx}>
									<HenvendelseFelter path={path} kjedeIdOptions={samtalereferatOptions} />
								</React.Fragment>
							)}
						</FormDollyFieldArray>
					)}
				</>
			</Panel>
		</Vis>
	)
}

HenvendelseForm.validation = validation
