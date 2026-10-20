import React from 'react'
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
import {
	henvendelseAttributt,
	henvendelseMeldingerPath,
	henvendelseSamtalereferaterPath,
	initialMelding,
	initialSamtalereferat,
} from '@/components/fagsystem/henvendelse/initialValues'
import { validation } from '@/components/fagsystem/henvendelse/form/validation'

interface HenvendelseFelterProps {
	path: string
}

const HenvendelseFelter = ({ path }: HenvendelseFelterProps) => {
	const { alleNavEnheter, loading: loadingEnheter } = useAlleNavEnheter()

	return (
		<>
			{/*TODO: Fix options fra kodeverk temagrupper */}
			<FormTextInput name={`${path}.temagruppe`} label="Temagruppe" size="large" />
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
			<FormTextInput name={`${path}.kjedeId`} label="Kjede-ID" size="large" />
			<FormTextInput name={`${path}.meldingsId`} label="Meldings-ID" size="large" />
			<FormTextInput name={`${path}.fritekst`} label="Fritekst" size="xxlarge" />
		</>
	)
}

export const HenvendelseForm = () => {
	const formMethods = useFormContext()
	const harMeldinger = formMethods.watch(henvendelseMeldingerPath)
	const harSamtalereferater = formMethods.watch(henvendelseSamtalereferaterPath)

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
									<HenvendelseFelter path={path} />
									<FormCheckbox name={`${path}.tildelMeg`} label="Tildel meg" />
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
									<HenvendelseFelter path={path} />
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
